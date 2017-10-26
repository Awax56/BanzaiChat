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
package net.awax.banzaiChat.util;

/**
 * Permet de représenter un utilisateur du chat.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class User {

	private final String pseudo;

	private UserStatus status;

	/**
	 * Permet d'instancier un utilisateur à partir de son pseudonyme.
	 * 
	 * @param pseudo
	 *            Pseudonyme de l'utilisateur.
	 */
	public User (String pseudo) {
		this(pseudo, UserStatus.UNKNOWN);
	}

	/**
	 * Permet d'instancier un utilisateur à partir de son pseudonyme et de son
	 * statut de connexion.
	 * 
	 * @param pseudo
	 *            Pseudonyme de l'utilisateur.
	 * @param userStatus
	 *            Statut de connexion.
	 */
	public User (String pseudo, UserStatus userStatus) {
		this.pseudo = pseudo;
		this.status = userStatus;
	}

	public String getPseudo () {
		return this.pseudo;
	}

	public UserStatus getStatus () {
		return this.status;
	}

	public void setStatus (UserStatus status) {
		this.status = status;
	}

	@Override
	public String toString () {
		return this.pseudo;
	}
}