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

import net.awax.banzaiChat.net.TcpServer;
import net.awax.banzaiChat.util.ResourceManager;

/**
 * Modèle de données du serveur.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class ServerModel {

	private final ResourceManager props;
	private final String appName;
	private final String appVersion;

	private TcpServer server;

	/**
	 * Permet d'instancier un modèle de données par défaut.
	 */
	public ServerModel () {
		this.props = ResourceManager.getInstance();
		this.appName = this.props.getString("banzaichat.server.application.name");
		this.appVersion = this.props.getString("banzaichat.server.application.version");
		this.server = null;
	}

	public String getAppName () {
		return this.appName;
	}

	public String getAppVersion () {
		return this.appVersion;
	}

	public TcpServer getServer () {
		return this.server;
	}

	public void setServer (TcpServer server) {
		this.server = server;
	}
}