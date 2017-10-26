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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Observable;

import net.awax.banzaiChat.gui.ChatPanel;
import net.awax.banzaiChat.net.TcpClient;
import net.awax.banzaiChat.util.ResourceManager;
import net.awax.banzaiChat.util.User;
import net.awax.banzaiChat.util.UserStatus;

/**
 * Modèle de données général de l'application.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class ApplicationModel extends Observable {

	private final ResourceManager props;
	private final String appName;
	private final String appVersion;
	private final HashMap<String, ChatPanel> chatPanels;
	private final LinkedHashMap<String, User> connectedUsers;
	private TcpClient tcpClient;

	private String pseudo;
	private String address;
	private int port;

	/**
	 * Permet d'instancier le modèle de données général par défaut de
	 * l'application.
	 */
	public ApplicationModel () {
		this.props = ResourceManager.getInstance();
		this.appName = this.props.getString("banzaichat.client.application.name");
		this.appVersion = this.props.getString("banzaichat.client.application.version");
		this.chatPanels = new HashMap<>();
		this.connectedUsers = new LinkedHashMap<>();
		this.tcpClient = null;
		this.pseudo = "User";
		this.address = "localhost";
		this.port = 50000;

		User user1 = new User("User1", UserStatus.CONNECTED);
		User user2 = new User("User2", UserStatus.BUSY);
		User user3 = new User("User3", UserStatus.ABSENT);
		User user4 = new User("User4", UserStatus.UNKNOWN);
		this.connectedUsers.put(user1.getPseudo(), user1);
		this.connectedUsers.put(user2.getPseudo(), user2);
		this.connectedUsers.put(user3.getPseudo(), user3);
		this.connectedUsers.put(user4.getPseudo(), user4);
	}

	public String getAppName () {
		return appName;
	}

	public String getAppVersion () {
		return appVersion;
	}

	public HashMap<String, ChatPanel> getChatPanels () {
		return this.chatPanels;
	}

	/**
	 * Renvoie le chat associé à l'identifiant spécifié s'il existe.
	 * 
	 * @param chatId
	 *            Identifiant du chat souhaité.
	 * @return Instance du chat souhaité, ou <code>null</code> si l'identifiant
	 *         spécifié n'existe pas.
	 */
	public ChatPanel getChatPanel (String chatId) {
		if (this.chatPanels.containsKey(chatId)) {
			return this.chatPanels.get(chatId);
		}
		return null;
	}

	public LinkedHashMap<String, User> getConnectedUsers () {
		return this.connectedUsers;
	}

	public TcpClient getTcpClient () {
		return this.tcpClient;
	}

	public void setTcpClient (TcpClient tcpClient) {
		this.tcpClient = tcpClient;
	}

	public String getPseudo () {
		return this.pseudo;
	}

	public void setPseudo (String pseudo) {
		this.pseudo = pseudo;
	}

	public String getAddress () {
		return this.address;
	}

	public void setAddress (String address) {
		this.address = address;
	}

	public int getPort () {
		return this.port;
	}

	public void setPort (int port) {
		this.port = port;
	}
}