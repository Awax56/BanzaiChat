/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Julien Le Sauce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.awax.banzaiChat.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Permet de réaliser un client TCP monothreadé entre l'application et un
 * serveur distant implémentant une architecture du type provider-subscriber. Ce
 * type d'architecture permet de notifier les abonnés (qui doivent implémenter
 * l'interface {@link EthernetEvent}) de l'arrivée de nouveaux messages.
 * 
 * @author LE SAUCE Julien
 * @version 1.0
 */
public class TcpClient implements Runnable {

	private static final int MAX_ERRORS = 10;
	private static final int ERROR_BROKEN_LINK = 1;

	private final Logger logger = Logger.getLogger(getClass());

	private final InetAddress address;
	private final int portNumber;
	private final long timeout;
	private final ArrayList<EthernetEvent> listeners;

	private InputStream sockIn;
	private DataOutputStream sockOut;
	private Socket socket;
	private Thread ownThread;
	private String errorDesc;
	private int errorCode;
	private int watchdog;

	/**
	 * Permet d'instancier un client TCP, il est ensuite nécessaire de lancer le
	 * thread d'écoute via la méthode <code>start()</code> et de s'abonner aux
	 * notifications de la liaison via la méthode <code>addListener()</code>.
	 * 
	 * @param address
	 *            Adresse de connexion au serveur distant.
	 * @param port
	 *            Port de connexion au serveur distant.
	 * @param timeout
	 *            Temps de timeout sur la liaison en millisecondes.
	 * @throws IOException
	 *             Si une erreur survient durant l'acquisition de l'adresse IP
	 *             ou la création de la socket, une exception est lancée.
	 */
	public TcpClient (String address, int port, long timeout) throws IOException {
		this.address = InetAddress.getByName(address);
		this.portNumber = port;
		this.timeout = timeout;
		this.listeners = new ArrayList<>();
		this.ownThread = null;
		this.errorDesc = "";
		this.errorCode = 0;
		this.watchdog = 0;
	}

	@Override
	public void run () {
		this.logger.info(this.address.getHostAddress() + " en écoute" + " sur le port " + this.portNumber);
		/*
		 * Boucle de réception
		 */
		while (!Thread.currentThread().isInterrupted() && !this.socket.isClosed()) {
			try {
				byte[] msg = receive();
				if (msg != null) {
					// Notifications des abonnés
					for (EthernetEvent listener : this.listeners) {
						listener.onReceive(msg);
					}
				} else {
					this.logger.warn("Erreur de réception");
					this.watchdog++;
				}
				// S'il y a trop d'erreurs on coupe la liaison
				if (this.watchdog > TcpClient.MAX_ERRORS) {
					this.errorCode = TcpClient.ERROR_BROKEN_LINK;
					this.errorDesc = "Broken Link (Max error limit reached)";
					break;
				}
			} catch (IOException e) {
				if (!(e instanceof SocketTimeoutException)) {
					this.logger.error("Une erreur est survenue lors de la réception d'un message depuis "
							+ this.address.getHostAddress(), e);
				}
			}
		}
		if (this.errorCode > 0) {
			for (EthernetEvent listener : this.listeners) {
				listener.onError(this.errorCode, this.errorDesc);
			}
		}
		this.logger.debug(this.address.getHostAddress() + " n'est plus en écoute");
		// Fermeture de la socket
		try {
			closeSocket();
			this.logger.info("Déconnexion réussie de " + this.address.getHostAddress());
		} catch (IOException e) {
			this.logger.error("Une erreur s'est produite durant la déconnexion de " + this.address.getHostAddress(), e);
		}
		this.ownThread = null;
	}

	/**
	 * Permet de lancer le thread client.
	 * 
	 * @throws ServerConnectionException
	 *             Si une erreur se produit durant l'ouverture du socket, une
	 *             exception est lancée.
	 */
	public void start () throws ServerConnectionException {
		// Si le socket n'a pas encore été créé, on l'ouvre
		if (this.socket == null) {
			try {
				this.socket = new Socket(this.address, this.portNumber);
				this.socket.setSoTimeout((int) this.timeout);
				this.sockIn = this.socket.getInputStream();
				this.sockOut = new DataOutputStream(this.socket.getOutputStream());
				this.logger.debug("Ouverture d'une socket sur " + this.socket.getInetAddress() + "/"
						+ this.socket.getPort());
				// Si le thread n'existe pas on le lance
				if (this.ownThread == null) {
					this.ownThread = new Thread(this);
					this.ownThread.start();
				}
			} catch (IOException e) {
				try {
					closeSocket();
				} catch (IOException e1) {
					this.logger.error("Cannot close socket", e1);
				}
				throw new ServerConnectionException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Permet de stopper le thread client.
	 */
	public void stop () {
		// Si le thread existe
		if (this.ownThread != null) {
			this.ownThread.interrupt();
			this.ownThread = null;
			this.logger.debug("Interruption du thread client " + this.address.getHostAddress());
		}
	}

	/**
	 * Permet d'envoyer une chaîne de texte vers le serveur TCP. On insère
	 * également en début de message la taille effective en octets du message
	 * (format BigEndian) sur 4 octets qui permettra au receveur de connaître la
	 * taille des données à recevoir.
	 * 
	 * @param str
	 *            Chaîne de texte à envoyer.
	 */
	public void send (String str) {
		byte[] msg = str.getBytes();
		int sizeInt = str.length();
		byte[] size = ByteBuffer.allocate(4).putInt(sizeInt).order(ByteOrder.BIG_ENDIAN).array();
		// Envoi du message
		if (this.isRunning() && !this.socket.isClosed()) {
			try {
				this.logger.debug("Envoi d'un message vers " + this.address.toString() + " (Length=" + str.length()
						+ ")");
				// On ajoute la taille en début de message
				this.sockOut.write(size);
				this.sockOut.write(msg);
				this.sockOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Permet de recevoir les messages TCP depuis le serveur distant. On
	 * s'attend à recevoir en premier lieu la taille du message sur 4 octets
	 * (formattée en BigEndian), puis on attend de lire la taille spécifiée
	 * avant de remonter le message complet (utile lorsque le message est
	 * découpé en plusieurs segments).
	 * 
	 * @return Tableau de bytes lu depuis la socket.
	 * @throws IOException
	 *             Si une erreur survient lors de la lecture de la socket, une
	 *             exception est levée.
	 * @throws SocketTimeoutException
	 *             Si le temps de timeout est atteint sur la socket avant de
	 *             lire de nouveau des données, une exception est levée.
	 */
	public byte[] receive () throws IOException, SocketTimeoutException {
		byte[] byteSize = new byte[4];

		// Acquisition de la taille du message (4 octets obligatoires en début
		// de message)
		int cpt = 0;
		cpt = this.sockIn.read(byteSize);
		int size = ByteBuffer.wrap(byteSize).order(ByteOrder.BIG_ENDIAN).getInt();
		if (cpt <= 0) {
			this.logger.warn("Invalid size");
			return null;
		}
		// Création du buffer de message à partir de la taille lue
		byte[] recvData = new byte[size];
		// Tant que l'on n'a pas reçu toutes les données on boucle (cas où le
		// message est segmenté)
		cpt = 0;
		while (cpt < size) {
			int recvSize = this.sockIn.read(recvData, cpt, size - cpt);
			if (recvSize <= 0) {
				return null;
			}
			cpt += recvSize;
		}
		// On renvoie un seul buffer représentant la chaîne utile du message
		return recvData;
	}

	/**
	 * Permet d'ajouter un abonné aux notifications du client TCP.
	 * 
	 * @param listener
	 *            Ecouteur sur les notifications du client TCP.
	 */
	public void addListener (EthernetEvent listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Permet de retirer un abonné du client TCP.
	 * 
	 * @param listener
	 *            Ecouteur sur les notifications du client TCP contenu dans la
	 *            liste.
	 * @return <code>true</code> si l'abonné a bien été retiré,
	 *         <code>false</code> s'il n'a pas été trouvé.
	 */
	public boolean removeListener (EthernetEvent listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
			return true;
		}
		return false;
	}

	/**
	 * Permet de fermer la socket de connexion TCP.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de la fermeture de la socket, une
	 *             exception est lancée.
	 */
	private void closeSocket () throws IOException {
		if (this.socket != null) {
			if (this.socket.isConnected() && !this.socket.isClosed()) {
				this.socket.close();
			}
			this.socket = null;
		}
	}

	/*
	 * Accesseurs
	 */

	/**
	 * Permet de dire si le client est en cours de fonctionnement ou non.
	 * 
	 * @return <code>true</code> si le thread client fonctionne,
	 *         <code>false</code> sinon.
	 */
	public boolean isRunning () {
		if (this.ownThread != null && !this.ownThread.isInterrupted()) {
			return true;
		}
		return false;
	}
}