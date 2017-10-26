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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.awax.banzaiChat.util.ResourceManager;
import net.miginfocom.swing.MigLayout;

/**
 * Le panneau de connexion permet d'afficher tout ce qui concerne les
 * informations de connexion au serveur distant.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class ConnectionPanel extends JPanel implements ActionListener, KeyListener, ChangeListener {

	private static final long serialVersionUID = 4347529777357839590L;

	private final ResourceManager props;
	private final ArrayList<ActionListener> actionListeners;
	private final ArrayList<PropertyChangeListener> propertyListeners;

	private JLabel lblPseudo;
	private JLabel lblAddress;
	private JLabel lblPort;
	private JLabel lblState;
	private JLabel lblStateLed;
	private JTextField tfPseudo;
	private JTextField tfAddress;
	private JSpinner spPort;
	private JButton btnConnect;
	private JButton btnDisconnect;

	/**
	 * Permet d'instancier le panneau de connexion au serveur distant.
	 */
	public ConnectionPanel () {
		super();
		this.props = ResourceManager.getInstance();
		this.actionListeners = new ArrayList<>();
		this.propertyListeners = new ArrayList<>();
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet de mettre à jour une des données de l'interface graphique.
	 * 
	 * @param propertyName
	 *            Identifiant de la donnée à mettre à jour.
	 * @param newValue
	 *            Nouvelle valeur à appliquer.
	 */
	public void setProperty (String propertyName, Object newValue) {
		switch (propertyName) {
			case "Pseudo":
				this.tfPseudo.setText(newValue.toString());
				break;
			case "Address":
				this.tfAddress.setText(newValue.toString());
				break;
			case "Port":
				this.spPort.setValue(newValue);
				break;
			default:
				break;
		}
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		this.lblPseudo = new JLabel(this.props.getString("banzaichat.mainview.panel.connexion.label.pseudo"));
		this.lblAddress = new JLabel(this.props.getString("banzaichat.mainview.panel.connexion.label.address"));
		this.lblPort = new JLabel(this.props.getString("banzaichat.mainview.panel.connexion.label.port"));
		this.lblState = new JLabel(this.props.getString("banzaichat.mainview.panel.connexion.label.state"));
		this.lblStateLed = new JLabel(this.props.getIcon("banzaichat.icon.ledRed"));
		this.tfPseudo = new JTextField();
		this.tfAddress = new JTextField();
		this.spPort = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
		this.btnConnect = new JButton(this.props.getString("banzaichat.mainview.panel.connexion.button.label.connect"));
		this.btnDisconnect = new JButton(
				this.props.getString("banzaichat.mainview.panel.connexion.button.label.disconnect"));
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		setLayout(new MigLayout());
		add(this.lblPseudo, "");
		add(this.tfPseudo, "grow, wrap");
		add(this.lblAddress, "");
		add(this.tfAddress, "grow, wrap");
		add(this.lblPort, "");
		add(this.spPort, "grow, wrap");
		add(this.btnConnect, "gap top 15px, gap bottom 15px, split, span, center");
		add(this.btnDisconnect, "wrap");
		add(this.lblState, "split, span, center");
		add(this.lblStateLed, "");
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.btnConnect.addActionListener(this);
		this.btnDisconnect.addActionListener(this);
		this.tfPseudo.addKeyListener(this);
		this.tfAddress.addKeyListener(this);
		this.spPort.addChangeListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// Connect
			if (this.btnConnect.equals(btn)) {
				for (ActionListener l : this.actionListeners) {
					l.actionPerformed(new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, "Connect"));
				}
			}
			// Disconnect
			else if (this.btnDisconnect.equals(btn)) {
				for (ActionListener l : this.actionListeners) {
					l.actionPerformed(new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, "Disconnect"));
				}
			}
		}
	}

	@Override
	public void stateChanged (ChangeEvent e) {
		/*
		 * Spinner
		 */
		if (e.getSource() instanceof JSpinner) {
			JSpinner spinner = (JSpinner) e.getSource();

			// Port
			if (this.spPort.equals(spinner)) {
				Object newValue = spinner.getValue();
				for (PropertyChangeListener l : this.propertyListeners) {
					l.propertyChange(new PropertyChangeEvent(spinner, "Port", null, newValue));
				}
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
		 * JTextField
		 */
		if (e.getSource() instanceof JTextField) {
			JTextField tf = (JTextField) e.getSource();
			Object newValue = tf.getText();
			String name = "";

			// Pseudo
			if (this.tfPseudo.equals(tf)) {
				name = "Pseudo";
			}
			// Address
			else if (this.tfAddress.equals(tf)) {
				name = "Address";
			}

			// Notification des abonnés
			for (PropertyChangeListener l : this.propertyListeners) {
				l.propertyChange(new PropertyChangeEvent(tf, name, null, newValue));
			}
		}
	}

	/**
	 * Permet d'ajouter un écouteur sur les notifications des boutons de
	 * connexion/déconnexion.
	 * 
	 * @param listener
	 *            Ecouteur sur les boutons de connexion/déconnexion.
	 */
	public void addCommandListener (ActionListener listener) {
		if (!this.actionListeners.contains(listener)) {
			this.actionListeners.add(listener);
		}
	}

	/**
	 * Permet d'ajouter un écouteur sur les changements de données du panneau.
	 * 
	 * @param listener
	 *            Ecouteur sur les changements de données du panneau.
	 */
	public void addPropertyListener (PropertyChangeListener listener) {
		if (!this.propertyListeners.contains(listener)) {
			this.propertyListeners.add(listener);
		}
	}
}