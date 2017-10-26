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

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

/**
 * Thread client instancié par le serveur pour pouvoir interagir avec le client
 * distant.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class TcpServerClient implements Runnable {

	private static final int MAX_ERRORS = 10;

	private final Logger logger;
	private final Thread currentThread;
	private final Socket socket;
	private InputStream socketIn;
	private int watchdog;

	/**
	 * Permet de créer un thread client.
	 * 
	 * @param clientSocket
	 *            Socket client.
	 */
	public TcpServerClient (final Socket clientSocket) {
		this.logger = Logger.getLogger(getClass());
		this.socket = clientSocket;
		try {
			this.socketIn = clientSocket.getInputStream();
		} catch (IOException e) {
			this.logger.error("An error occurred while getting the socket input stream", e);
		}
		this.currentThread = new Thread(this);
		this.currentThread.start();
		this.watchdog = 0;
	}

	@Override
	public void run () {
		this.logger.info("Client thread from " + this.socket.getInetAddress() + "/" + this.socket.getLocalPort()
				+ "is now running");
		while (!this.currentThread.isInterrupted()) {
			try {
				byte[] msg = receive();
				if (msg != null) {
					// Notifications des abonnés
					// ...
					this.watchdog = 0;
				} else {
					this.logger.warn("Decoding error : message null");
					this.watchdog++;
				}
			} catch (SocketTimeoutException e) {
				this.logger.error("Timeout occurred on " + this.socket.getInetAddress() + "/" + this.socket.getPort(),
						e);
				this.watchdog++;
			} catch (IOException e) {
				this.logger.error("An error occurred on " + this.socket.getInetAddress() + "/" + this.socket.getPort(),
						e);
				this.watchdog++;
			} catch (Exception e) {
				this.logger.error("An error occurred on " + this.socket.getInetAddress() + "/" + this.socket.getPort(),
						e);
				this.watchdog++;
			}
			// S'il y a trop d'erreurs on coupe la liaison
			if (this.watchdog > MAX_ERRORS) {
				this.logger.error("Broken Link (Max error limit reached)");
				try {
					stop();
				} catch (IOException e) {
					this.logger.error("Cannot stop server client", e);
				}
			}
		}
		this.logger.info("Client thread finished");
	}

	/**
	 * Permet de stopper le thread et de libérer la socket client.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de la fermeture de la socket
	 *             client, une exception est lancée.
	 */
	public synchronized void stop () throws IOException {
		// Si le thread existe
		if (this.currentThread != null) {
			this.currentThread.interrupt();
			this.logger.info("Client thread stopped");
		}
		// Si un serveur a été instancié
		if (this.socket != null) {
			this.socket.close();
			this.logger.info("Client socket closed");
		}
	}

	/**
	 * Permet de recevoir les messages TCP depuis le client distant. On s'attend
	 * à recevoir en premier lieu la taille du message sur 4 octets (formattée
	 * en BigEndian), puis on attend de lire la taille spécifiée avant de
	 * remonter le message complet (utile lorsque le message est découpé en
	 * plusieurs segments).
	 * 
	 * @return Tableau de bytes lu depuis la socket.
	 * @throws IOException
	 *             Si une erreur survient lors de la lecture de la socket, une
	 *             exception est lancée.
	 * @throws SocketTimeoutException
	 *             Si le temps de timeout est atteint sur la socket avant de
	 *             lire de nouveau des données, une exception est levée.
	 */
	public synchronized byte[] receive () throws IOException, SocketTimeoutException {
		byte[] byteSize = new byte[4];

		// Acquisition de la taille du message (4 octets obligatoires en début de message)
		int cpt = 0;
		cpt = this.socketIn.read(byteSize);
		int size = ByteBuffer.wrap(byteSize).order(ByteOrder.BIG_ENDIAN).getInt();
		if (cpt <= 0) {
			this.logger.error("Invalid size");
			return null;
		}
		// Création du buffer de message à partir de la taille lue
		byte[] recvData = new byte[size];
		// Tant que l'on n'a pas reçu toutes les données on boucle (cas où le message est segmenté)
		cpt = 0;
		while (cpt < size) {
			int recvSize = this.socketIn.read(recvData, cpt, size - cpt);
			if (recvSize <= 0) {
				return null;
			}
			cpt += recvSize;
		}
		// On renvoie un seul buffer représentant le message complet
		return recvData;
	}
}