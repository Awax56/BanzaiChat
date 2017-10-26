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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import net.awax.banzaiChat.gui.ChatPanel;
import net.awax.banzaiChat.gui.ConnectionPanel;
import net.awax.banzaiChat.gui.HeaderPanel;
import net.awax.banzaiChat.gui.MessagePanel;
import net.awax.banzaiChat.gui.UsersPanel;
import net.miginfocom.swing.MigLayout;

/**
 * Interface graphique générale de l'application.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class ApplicationView extends JFrame implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 6188190248398326488L;

	private final ApplicationModel model;
	private final ApplicationController controller;
	private final Logger logger;

	private HeaderPanel headerPanel;
	private ConnectionPanel connectionPanel;
	private MessagePanel messagePanel;
	private UsersPanel usersPanel;
	private JTabbedPane chatTabs;
	private JSplitPane splitNS;
	private JSplitPane splitWE;

	/**
	 * Permet d'instancier la vue principale de l'application.
	 * 
	 * @param model
	 *            Modèle de données de l'application.
	 * @param controller
	 *            Contrôleur principal de l'application.
	 */
	public ApplicationView (final ApplicationModel model, final ApplicationController controller) {
		super(model.getAppName() + "  -  Version " + model.getAppVersion());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.model = model;
		this.controller = controller;
		this.logger = Logger.getLogger(getClass());
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet d'afficher l'application.
	 */
	public void showGui () {
		setSize(1000, 800);
		setState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);
		adjustSplitPanes();
	}

	/**
	 * Permet d'afficher une pop-up à l'utilisateur.
	 * 
	 * @param title
	 *            Titre de la pop-up.
	 * @param msg
	 *            Message à afficher.
	 * @param msgType
	 *            Type de message, voir {@link JOptionPane}.
	 */
	public void pop (String title, String msg, int msgType) {
		showMessageDialog(title, msg, msgType);
	}

	/**
	 * Permet d'ajouter une nouvelle fenêtre de chat dans le panneau principal.
	 * 
	 * @param panel
	 *            Fenêtre de chat à ajouter.
	 */
	public void addChatPanel (ChatPanel panel) {
		this.chatTabs.add(panel.getChatId(), panel);
	}

	/**
	 * Permet d'ajuster automatiquement les proportions des zones
	 * redimensionnables.
	 */
	public void adjustSplitPanes () {
		if (this.splitNS != null && this.splitWE != null) {
			this.splitNS.setDividerLocation(0.7);
			this.splitWE.setDividerLocation(0.8);
		}
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		this.headerPanel = new HeaderPanel();
		this.connectionPanel = new ConnectionPanel();
		this.messagePanel = new MessagePanel();
		this.usersPanel = new UsersPanel();
		this.usersPanel.updateUsersList(this.model.getConnectedUsers());
		this.chatTabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
		this.splitNS = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.chatTabs, this.messagePanel);
		this.splitNS.setResizeWeight(1.0);
		this.splitWE = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.splitNS, this.usersPanel);
		this.splitWE.setResizeWeight(1.0);

		// Création des chats déjà instanciés
		for (ChatPanel chat : this.model.getChatPanels().values()) {
			addChatPanel(chat);
		}

		// Initialisation des interfaces
		this.connectionPanel.setProperty("Pseudo", this.model.getPseudo());
		this.connectionPanel.setProperty("Address", this.model.getAddress());
		this.connectionPanel.setProperty("Port", this.model.getPort());
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		setLayout(new MigLayout("fill"));
		add(this.headerPanel, "top, pushx, growx");
		add(this.connectionPanel, "top, wrap");
		add(this.splitWE, "pushy, span, grow");
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.connectionPanel.addCommandListener(this);
		this.connectionPanel.addPropertyListener(this);
		this.messagePanel.addSendingListener(this);
	}

	/**
	 * Permet d'afficher une pop-up à l'utilisateur.
	 * 
	 * @param title
	 *            Titre de la pop-up.
	 * @param msg
	 *            Message à afficher.
	 * @param msgType
	 *            Type de message, voir {@link JOptionPane}.
	 */
	private void showMessageDialog (final String title, final String msg, final int msgType) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				JOptionPane.showMessageDialog(ApplicationView.this, msg, title, msgType);
			}
		});
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			// Connect
			if ("Connect".equals(e.getActionCommand())) {
				this.controller.connectClient();
			}
			//Disconnect
			else if ("Disconnect".equals(e.getActionCommand())) {
				this.controller.disconnectClient();
			}
			// Send Message
			else if ("Send".equals(e.getActionCommand())) {
				this.logger.debug("Sending message");
			}
		}
	}

	@Override
	public void propertyChange (PropertyChangeEvent e) {
		Object newValue = e.getNewValue();
		String name = e.getPropertyName();
		/*
		 * String
		 */
		if (newValue instanceof String) {
			String s = (String) newValue;
			// Pseudo
			if (name.equals("Pseudo")) {
				this.model.setPseudo(s);
			}
			// Address
			else if (name.equals("Address")) {
				this.model.setAddress(s);
			}
		}
		/*
		 * Integer
		 */
		else if (newValue instanceof Integer) {
			Integer i = (Integer) newValue;

			// Port
			if (name.equals("Port")) {
				this.model.setPort(i.intValue());
			}
		}
	}
}