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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;

import net.miginfocom.swing.MigLayout;
import net.awax.banzaiChat.util.ResourceManager;

/**
 * Panneau permettant à l'utilisateur d'envoyer des messages.
 * 
 * @author AwaX
 * @created 29 avr. 2014
 * @version 1.0
 */
public class MessagePanel extends JPanel implements ActionListener, KeyListener, FocusListener {

	private static final long serialVersionUID = -1604499527367179544L;

	private final Logger logger;
	private final ResourceManager props;
	private final ArrayList<ActionListener> sendingListeners;
	private boolean isWriting;
	private String tooltip;

	private JTextPane console;
	private JButton btnChooseColor;
	private JButton btnSend;

	/**
	 * Permet d'instancier le panneau d'envoi des messages.
	 */
	public MessagePanel () {
		super();
		this.logger = Logger.getLogger(getClass());
		this.props = ResourceManager.getInstance();
		this.sendingListeners = new ArrayList<>();
		this.isWriting = false;
		this.tooltip = this.props.getString("banzaichat.mainview.panel.message.textpane.console.tooltip");
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		this.btnChooseColor =
				new JButton(this.props.getString("banzaichat.mainview.panel.message.button.label.chooseColor"));
		this.btnSend = new JButton(this.props.getString("banzaichat.mainview.panel.message.button.label.send"));
		this.console = new JTextPane();
		this.console.setText(this.tooltip);
		this.console.setToolTipText(this.tooltip);
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		setLayout(new MigLayout("", "[][grow][]", "grow"));
		setBorder(BorderFactory.createTitledBorder(this.props
				.getString("banzaichat.mainview.panel.message.border.label")));
		add(this.btnChooseColor);
		add(new JScrollPane(this.console), "grow");
		add(this.btnSend);
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.btnSend.addActionListener(this);
		this.btnChooseColor.addActionListener(this);
		this.console.addKeyListener(this);
		this.console.addFocusListener(this);
	}

	/**
	 * Cette méthode est appelée lorsque l'utilisateur souhaite envoyer le
	 * message tapé.
	 */
	private void sendMessage () {
		this.logger.debug("Sending message");
		if (this.console.hasFocus()) {
			this.console.setText("");
		} else {
			this.console.setText(this.tooltip);
		}
		this.isWriting = false;
		// Notification des écouteurs
		for (ActionListener l : this.sendingListeners) {
			l.actionPerformed(new ActionEvent(this.btnSend, ActionEvent.ACTION_PERFORMED, "Send"));
		}
	}

	/**
	 * Permet d'ajouter un écouteur sur le bouton d'envoi de la fenêtre.
	 * 
	 * @param listener
	 *            Ecouteur à ajouter.
	 */
	public void addSendingListener (final ActionListener listener) {
		if (!this.sendingListeners.contains(listener)) {
			this.sendingListeners.add(listener);
		}
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// Send
			if (this.btnSend.equals(btn)) {
				sendMessage();
			}
			// Choose Color
			else if (this.btnChooseColor.equals(btn)) {
				this.logger.debug("Showing color chooser");
			}
		}
	}

	@Override
	public void keyPressed (KeyEvent e) {
		//
	}

	@Override
	public void keyReleased (KeyEvent e) {
		//
	}

	@Override
	public void keyTyped (KeyEvent e) {
		/*
		 * JTextPane
		 */
		if (e.getSource() instanceof JTextPane) {
			int key = e.getKeyChar();

			// Appuie sur "ENTREE"
			if (key == KeyEvent.VK_ENTER && !e.isShiftDown()) {
				// Alors on envoie le message
				sendMessage();
			}
			// Appuie sur "ENTREE" + "SHIFT"
			else if (key == KeyEvent.VK_ENTER && e.isShiftDown()) {
				// Alors on passe une ligne dans le message
				this.logger.debug("Line break");
				console.setText(console.getText() + "\n");
			}
		}
	}

	@Override
	public void focusGained (FocusEvent e) {
		/*
		 * JTextPane
		 */
		if (e.getSource() instanceof JTextPane) {
			JTextPane textPane = (JTextPane) e.getSource();

			if (this.isWriting == false) {
				textPane.setText("");
			}
		}
	}

	@Override
	public void focusLost (FocusEvent e) {
		/*
		 * JTextPane
		 */
		if (e.getSource() instanceof JTextPane) {
			JTextPane textPane = (JTextPane) e.getSource();

			// Si la zone d'écriture est vide
			if (textPane.getText().equals("")) {
				this.isWriting = false;
				textPane.setText(this.tooltip);
			} else {
				this.isWriting = true;
			}
		}
	}
}