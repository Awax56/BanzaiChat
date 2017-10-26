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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import net.awax.banzaiChat.gui.ChatPanel;
import net.miginfocom.swing.MigLayout;
import net.awax.banzaiChat.net.ServerConnectionException;
import net.awax.banzaiChat.util.ResourceManager;

/**
 * Fenêtre d'afficage du panneau de gestion du serveur.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class ServerView extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4310528149382140197L;

	private final ServerController controller;
	private final ResourceManager props;
	private final Logger logger;

	private ChatPanel chatPanel;
	private JMenuBar menuBar;
	private JMenu menuServer;
	private JMenuItem itemConnect;
	private JMenuItem itemDisconnect;

	/**
	 * Permet d'instancier la fenêtre principale du serveur.
	 * 
	 * @param model
	 *            Modèle de données du serveur.
	 * @param controller
	 *            Contrôleur du serveur.
	 */
	public ServerView (final ServerModel model, final ServerController controller) {
		super(model.getAppName() + " - Version " + model.getAppVersion());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.controller = controller;
		this.props = ResourceManager.getInstance();
		this.logger = Logger.getLogger(getClass());
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet d'afficher l'interface graphique de l'application.
	 */
	public void showGui () {
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
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
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		createMenuBar();
		this.chatPanel = new ChatPanel("Console");
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		setJMenuBar(this.menuBar);
		setLayout(new MigLayout("fill"));
		JScrollPane scroll = new JScrollPane(this.chatPanel);
		add(scroll, "grow");
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.itemConnect.addActionListener(this);
		this.itemDisconnect.addActionListener(this);
	}

	/**
	 * Permet de créer la barre de menu de la fenêtre.
	 */
	private void createMenuBar () {
		this.menuBar = new JMenuBar();
		this.menuServer = new JMenu(this.props.getString("serverView.menu.server"));
		this.itemConnect = new JMenuItem(this.props.getString("serverView.menu.item.connect"));
		this.itemDisconnect = new JMenuItem(this.props.getString("serverView.menu.item.disconnect"));
		// Ajout des menus
		this.menuServer.add(this.itemConnect);
		this.menuServer.add(this.itemDisconnect);
		this.menuBar.add(this.menuServer);
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
				JOptionPane.showMessageDialog(ServerView.this, msg, title, msgType);
			}
		});
	}

	/**
	 * Renvoie l'instance du panneau de console.
	 * 
	 * @return Panneau de la console.
	 */
	public ChatPanel getConsole () {
		return this.chatPanel;
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JMenuItem
		 */
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem item = (JMenuItem) e.getSource();

			// Connect
			if (this.itemConnect.equals(item)) {
				try {
					this.controller.connectServer();
				} catch (IOException e1) {
					this.logger.error("Cannot connect server", e1);
					pop("Connection Error", "Cannot connect the server :\n\n" + e1.getMessage(),
							JOptionPane.ERROR_MESSAGE);
				} catch (ServerConnectionException e1) {
					this.logger.error("Cannot connect server", e1);
					pop("Connection Error", "Cannot connect the server :\n\n" + e1.getMessage(),
							JOptionPane.ERROR_MESSAGE);
				}
			}
			// Disconnect
			else if (this.itemDisconnect.equals(item)) {
				try {
					this.controller.disconnectServer();
				} catch (IOException e1) {
					this.logger.error("Cannot disconnect server", e1);
					pop("Disconnection Error", "Cannot disconnect the server :\n\n" + e1.getMessage(),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}