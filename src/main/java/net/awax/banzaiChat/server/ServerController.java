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
package net.awax.banzaiChat.server;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Calendar;

import net.awax.banzaiChat.gui.ChatPanel;
import net.awax.banzaiChat.net.ServerConnectionException;
import net.awax.banzaiChat.net.TcpServer;
import net.awax.banzaiChat.util.LogStatus;
import net.awax.banzaiChat.util.ResourceManager;

/**
 * Contrôleur général du serveur.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class ServerController {

	private final ServerModel model;
	private final ServerView view;
	private final ResourceManager props;

	/**
	 * Permet d'instancier le contrôleur du serveur.
	 * 
	 * @param model
	 *            Modèle de données du serveur.
	 */
	public ServerController (final ServerModel model) {
		this.model = model;
		this.view = new ServerView(model, this);
		this.props = ResourceManager.getInstance();
		appendConsole("Initialisation de la console");
	}

	/**
	 * Renvoie le timestamp actuel au format hh:mm:ss.SSS.
	 * 
	 * @return Timestamp actuel au format hh:mm:ss.SSS.
	 */
	public static String getTimestamp () {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		return String.format("%02d:%02d:%02d.%03d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}

	/**
	 * Permet d'afficher l'interface graphique de l'application.
	 */
	public void showGui () {
		if (this.view != null) {
			this.view.showGui();
		}
	}

	/**
	 * Permet d'afficher du texte brut dans la console.
	 * 
	 * @param text
	 *            Texte à afficher.
	 */
	public void appendConsole (String text) {
		ChatPanel console = this.view.getConsole();
		console.append(getTimestamp() + "  ", Font.BOLD, Color.blue);
		console.append(text + "\n");
	}

	/**
	 * Permet d'afficher du texte dans la console en précisant le type de
	 * message à afficher.
	 * 
	 * @param text
	 *            Texte à afficher.
	 * @param status
	 *            Type de message à afficher.
	 */
	public void appendConsole (String text, LogStatus status) {
		ChatPanel console = this.view.getConsole();
		console.append(getTimestamp() + "  ", Font.BOLD, Color.blue);
		Color color = Color.black;
		Font font = ChatPanel.DEFAULT_FONT;
		switch (status) {
			case SERVER_MESSAGE:
				color = Color.blue;
				break;
			case CLIENT_MESSAGE:
				color = Color.green.darker();
				break;
			case ERROR:
				color = Color.red;
				font.deriveFont(Font.BOLD, font.getSize());
				break;
			case WARNING:
				color = Color.orange;
				break;
			default:
				break;
		}
		console.append(text + "\n", font, color);
	}

	/**
	 * Permet de connecter le serveur.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de l'ouverture du serveur, une
	 *             exception est lancée.
	 * @throws ServerConnectionException
	 *             Si le serveur est déjà en cours d'exécution, une exception
	 *             est lancée.
	 */
	public void connectServer () throws IOException, ServerConnectionException {
		if (this.model.getServer() == null) {
			int port = this.props.getInt("server.port");
			int maxConnections = this.props.getInt("server.maxConnections");
			TcpServer server = new TcpServer(port, maxConnections);
			this.model.setServer(server);
			server.start();
			appendConsole("Server is now running on " + server.getInetAddress().getHostAddress() + "/" + server.getPort(), LogStatus.SERVER_MESSAGE);
		} else {
			throw new ServerConnectionException("Server is already running");
		}
	}

	/**
	 * Permet de déconnecter le server.
	 * 
	 * @throws IOException
	 *             Si une erreur survient lors de la déconnexion du serveur, une
	 *             exception est lancée.
	 */
	public void disconnectServer () throws IOException {
		if (this.model.getServer() != null) {
			TcpServer server = this.model.getServer();
			server.stop();
			this.model.setServer(null);
			appendConsole("Server disconnected", LogStatus.SERVER_MESSAGE);
		}
	}
}