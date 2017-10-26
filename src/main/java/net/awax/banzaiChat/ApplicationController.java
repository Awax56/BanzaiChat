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
package net.awax.banzaiChat;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JOptionPane;

import net.awax.banzaiChat.gui.ChatPanel;

import org.apache.log4j.Logger;

import net.awax.banzaiChat.net.ServerConnectionException;
import net.awax.banzaiChat.net.TcpClient;
import net.awax.banzaiChat.util.LogStatus;

/**
 * Contrôleur principal de l'application.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class ApplicationController {

	private final ApplicationModel model;
	private final ApplicationView view;
	private final Logger logger;

	/**
	 * Permet d'instancier le contrôleur principal de l'application.
	 * 
	 * @param model
	 *            Modèle de données de l'application.
	 */
	public ApplicationController (final ApplicationModel appModel) {
		this.model = appModel;
		this.view = new ApplicationView(appModel, this);
		this.logger = Logger.getLogger(getClass());
		createChat("General");
		appendChat("General", "Initialisation de la console générale");
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
		} else {
			throw new NullPointerException("ApplicationView is null");
		}
	}

	/**
	 * Permet de connecter l'utilisateur au serveur distant.
	 */
	public void connectClient () {
		TcpClient client = this.model.getTcpClient();
		if (client == null) {
			this.logger.info("Connecting to server");
			appendChat("General", "Connecting to server...", LogStatus.SERVER_MESSAGE);
			try {
				client = new TcpClient(this.model.getAddress(), this.model.getPort(), 2000);
				client.start();
				this.model.setTcpClient(client);
				appendChat("General", "Client connection succeeded", LogStatus.SERVER_MESSAGE);
			} catch (IOException e) {
				this.logger.error("Cannot create TCP client", e);
				appendChat("General", "Cannot create chat client : " + e.getMessage(), LogStatus.ERROR);
				this.view.pop("Client Creation Failed", "Cannot create chat client.\n\n" + e.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			} catch (ServerConnectionException e) {
				this.logger.error("Cannot start client thread", e);
				appendChat("General", "Cannot start chat client : " + e.getMessage(), LogStatus.ERROR);
				this.view.pop("Client Connection Failed", "Cannot start chat client.\n\n" + e.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Permet de déconnecter l'utilisateur du serveur distant.
	 */
	public void disconnectClient () {
		if (this.model.getTcpClient() != null) {
			this.logger.info("Disconnecting from server");
			TcpClient client = this.model.getTcpClient();
			client.stop();
			this.model.setTcpClient(null);
			appendChat("General", "Client disconnected successfully", LogStatus.SERVER_MESSAGE);
		}
	}

	/**
	 * Permet de créer un nouveau chat dans l'interface graphique.
	 * 
	 * @param id
	 *            Identifiant du nouveau chat.
	 * @return Instance du chat.
	 */
	public ChatPanel createChat (String id) {
		if (id != null && !id.isEmpty()) {
			if (!this.model.getChatPanels().containsKey(id)) {
				ChatPanel chat = new ChatPanel(id);
				this.model.getChatPanels().put(id, chat);
				if (this.view != null) {
					this.view.addChatPanel(chat);
				}
				return chat;
			}
			throw new IllegalArgumentException("Chat id already exists : " + id);
		}
		throw new NullPointerException("Id cannot be null or empty");
	}

	/**
	 * Permet d'afficher du texte brut dans le chat spécifié.
	 * 
	 * @param chatId
	 *            Identifiant du chat.
	 * @param text
	 *            Texte à afficher dans le chat.
	 */
	public void appendChat (String chatId, String text) {
		ChatPanel chat = this.model.getChatPanel(chatId);
		chat.append(getTimestamp() + "  ", Font.BOLD, Color.blue);
		chat.append(text + "\n");
	}

	/**
	 * Permet d'afficher du texte dans le chat spécifié en précisant le type de
	 * message à afficher.
	 * 
	 * @param chatId
	 *            Identifiant du chat.
	 * @param text
	 *            Texte à afficher dans le chat.
	 * @param status
	 *            Type de message à afficher.
	 */
	public void appendChat (String chatId, String text, LogStatus status) {
		ChatPanel chat = this.model.getChatPanel(chatId);
		chat.append(getTimestamp() + "  ", Font.BOLD, Color.blue);
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
		chat.append(text + "\n", font, color);
	}
}