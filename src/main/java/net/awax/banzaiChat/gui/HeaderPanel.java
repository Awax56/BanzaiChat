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
package net.awax.banzaiChat.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.awax.banzaiChat.util.ResourceManager;

/**
 * Panneau permettant d'afficher le header du panneau principal de
 * l'application.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class HeaderPanel extends JPanel {

	private static final long serialVersionUID = 6558193024776622815L;

	private final ResourceManager props;

	/**
	 * Permet d'instancier le header de l'application.
	 */
	public HeaderPanel () {
		super();
		this.props = ResourceManager.getInstance();
		createGui();
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		JLabel lblLogo = new JLabel(this.props.getIcon("banzaichat.icon.header"));
		JLabel lblTitle = new JLabel(this.props.getString("banzaichat.mainview.panel.header.label"));
		lblTitle.setForeground(Color.blue);
		lblTitle.setHorizontalTextPosition(JLabel.CENTER);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
		setBackground(Color.CYAN);
		// Ajout des éléments
		setLayout(new MigLayout());
		add(lblLogo, "");
		add(lblTitle, "pushx, span, center");
	}
}