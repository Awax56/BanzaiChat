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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Permet de créer un serveur TCP multithreadé.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class TcpServer implements Runnable {

	private final Logger logger;
	private final HashMap<InetAddress, TcpServerClient> clients;
	private Thread currentThread;
	private ServerSocket server;
	private int port;
	private int maxConnections;

	/**
	 * Permet d'instancier le serveur TCP sur le port spécifié.
	 * 
	 * @param port
	 *            Port de connexion du serveur.
	 * @param maxConnections
	 *            Nombre de clients simultanés maximum.
	 */
	public TcpServer (int port, int maxConnections) {
		super();
		this.logger = Logger.getLogger(getClass());
		this.clients = new HashMap<>();
		this.currentThread = null;
		this.server = null;
		this.port = port;
		this.maxConnections = maxConnections;
	}

	@SuppressWarnings("resource")
	@Override
	public void run () {
		this.logger.info("Server thread is now running on " + this.server.getInetAddress() + "/"
				+ this.server.getLocalPort());
		while (!this.currentThread.isInterrupted()) {
			// Attente de connexion d'un client
			Socket client = null;
			try {
				client = this.server.accept();
				this.logger.info("Client connection from " + client.getInetAddress() + "/" + client.getPort());
				TcpServerClient clientThread = new TcpServerClient(client);
				this.clients.put(client.getInetAddress(), clientThread);
			} catch (IOException e) {
				this.logger.error("Client accept failed", e);
				if (client != null) {
					try {
						client.close();
					} catch (IOException e1) {
						this.logger.error("Client close failed", e1);
					}
				}
			}
		}
		this.logger.info("Server thread finished");
	}

	/**
	 * Permet de lancer le thread et de créer la socket serveur. Une fois la
	 * méthode exécutée le thread sera alors en attente de nouvelle connexions
	 * distantes.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de l'ouverture de la socket
	 *             serveur, une exception est lancée.
	 * @throws ServerConnectionException
	 *             Si le serveur est déjà en cours d'exécution, une exception
	 *             est lancée.
	 */
	public synchronized void start () throws IOException, ServerConnectionException {
		// Si le thread ne tourne pas
		if (this.currentThread == null) {
			this.currentThread = new Thread(this);
			connect();
			this.currentThread.start();
		} else {
			throw new IllegalStateException("Server thread is already running");
		}
	}

	/**
	 * Permet de stopper le thread et de libérer la socket serveur.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de la fermeture de la socket
	 *             serveur, une exception est lancée.
	 */
	public synchronized void stop () throws IOException {
		// Si le thread existe
		if (this.currentThread != null) {
			this.currentThread.interrupt();
			this.currentThread = null;
			this.logger.info("Server thread stopped");
		}
		// Si un serveur a été instancié
		if (this.server != null) {
			disconnect();
		}
	}

	/**
	 * Permet d'instancier le serveur TCP.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de l'ouverture de la socket, une
	 *             exception est lancée.
	 * @throws ServerConnectionException
	 *             Si le serveur est déjà en cours d'exécution, une exception
	 *             est lancée.
	 */
	private void connect () throws IOException, ServerConnectionException {
		if (this.server == null) {
			this.server = new ServerSocket(this.port, this.maxConnections);
		} else {
			throw new ServerConnectionException("Server is already running");
		}
	}

	/**
	 * Permet de déconnecter le serveur TCP.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de la fermeture de la socket, une
	 *             exception est lancée.
	 */
	private void disconnect () throws IOException {
		if (this.server != null) {
			this.server.close();
			this.server = null;
			this.logger.info("Server disconnected");
		}
	}

	/**
	 * Permet de dire si le thread serveur est en cours de fonctionnement ou
	 * non.
	 * 
	 * @return <code>true</code> si le thread serveur est en cours de
	 *         fonctionnement, <code>false</code> sinon.
	 */
	public boolean isRunning () {
		if (this.currentThread != null && !this.currentThread.isInterrupted()) {
			return true;
		}
		return false;
	}

	public InetAddress getInetAddress () {
		return this.server != null ? this.server.getInetAddress() : null;
	}

	public int getPort () {
		return this.server != null ? this.server.getLocalPort() : this.port;
	}
}