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

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

/**
 * Permet de créer une console de chat permettant d'afficher à l'utilisateur
 * tous les messages entrant et sortant.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class ChatPanel extends JTextPane {
	
	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
	public static final Color DEFAULT_TEXT_COLOR = Color.black;
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.white;

	private static final long serialVersionUID = -5756191661208385436L;

	private final Logger logger;

	private String id;
	private Font font;

	/**
	 * Permet d'instancier un panneau de chat.
	 * 
	 * @param chatId
	 *            Identifiant de la fenêtre de chat.
	 */
	public ChatPanel (String chatId) {
		super();
		this.logger = Logger.getLogger(ChatPanel.class);
		this.id = chatId;
		this.font = new Font("Arial", Font.PLAIN, 14);
		setFont(this.font);
		setBackground(Color.BLUE);
		setForeground(Color.BLACK);
		setEditable(false);
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher dans la console.
	 */
	public void append (String text) {
		append(text, DEFAULT_FONT, DEFAULT_TEXT_COLOR, DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher dans la console.
	 * @param color
	 *            Couleur du texte à afficher.
	 */
	public void append (String text, Color color) {
		append(text, DEFAULT_FONT, color, DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher.
	 * @param font
	 *            Style du texte à afficher.
	 * @param color
	 *            Couleur du texte à afficher.
	 */
	public void append (String text, Font font, Color color) {
		append(text, font, color, DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher.
	 * @param fontStyle
	 *            Style du texte à afficher, voir {@link Font}.
	 * @param color
	 *            Couleur du texte à afficher.
	 */
	public void append (String text, int fontStyle, Color color) {
		Font font = new Font(DEFAULT_FONT.getName(), fontStyle, DEFAULT_FONT.getSize());
		append(text, font, color, DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher.
	 * @param font
	 *            Style du texte à afficher.
	 * @param textColor
	 *            Couleur du texte à afficher.
	 * @param foreground
	 *            Couleur de fond du texte à afficher.
	 */
	public void append (String text, Font font, Color textColor, Color foreground) {
		StyledDocument doc = getStyledDocument();
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, font.getFamily());
		StyleConstants.setFontSize(style, font.getSize());
		StyleConstants.setForeground(style, textColor);
		StyleConstants.setBackground(style, foreground);
		if (font.isBold()) {
			StyleConstants.setBold(style, true);
		}
		if (font.isItalic()) {
			StyleConstants.setItalic(style, true);
		}
		try {
			doc.insertString(doc.getLength(), text, style);
		} catch (Exception e) {
			this.logger.error("Cannot append text into " + this.id, e);
		}
	}

	/**
	 * Renvoie l'identifiant de la fenêtre de chat.
	 * 
	 * @return Identifiant de la fenêtre de chat.
	 */
	public String getChatId () {
		return this.id;
	}
}