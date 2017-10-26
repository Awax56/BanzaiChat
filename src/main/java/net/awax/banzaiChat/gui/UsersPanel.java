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

import java.awt.Component;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import net.awax.banzaiChat.util.ResourceManager;
import net.awax.banzaiChat.util.User;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau permettant d'afficher et d'interagir avec les utilisateurs connectés.
 * 
 * @author AwaX
 * @created 1 mai 2014
 * @version 1.0
 */
public class UsersPanel extends JPanel {

	private static final long serialVersionUID = -7649876102342886890L;

	private final ResourceManager props;
	private final ImageIcon iconConnected;
	private final ImageIcon iconBusy;
	private final ImageIcon iconAbsent;
	private final ImageIcon iconDefault;

	private JTree tree;
	private DefaultTreeModel treeModel;

	/**
	 * Permet d'instancier le panneau d'affichage des utilisateurs connectés.
	 */
	public UsersPanel () {
		super();
		this.props = ResourceManager.getInstance();
		this.iconConnected = this.props.getIcon("banzaichat.icon.status.connected");
		this.iconBusy = this.props.getIcon("banzaichat.icon.status.busy");
		this.iconAbsent = this.props.getIcon("banzaichat.icon.status.absent");
		this.iconDefault = this.props.getIcon("banzaichat.icon.status.unknown");
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet de mettre à jour la liste des utilisateurs connectés.
	 * 
	 * @param usersList
	 *            Liste des utilisateurs connectés indexés par pseudonyme.
	 */
	public void updateUsersList (final LinkedHashMap<?, User> usersList) {
		this.tree.removeAll();
		for (User user : usersList.values()) {
			addNode(user);
		}
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		createTree();
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		setLayout(new MigLayout("fill"));
		add(this.tree, "grow");
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		//
	}

	/**
	 * Permet de créer la liste d'affichage des utilisateurs connectés.
	 */
	private void createTree () {
		this.tree = new JTree();
		this.tree.setCellRenderer(new TreeNodeRenderer());
		this.tree.setRootVisible(false);
		this.tree.setShowsRootHandles(true);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		this.treeModel = new DefaultTreeModel(rootNode, true);
		this.tree.setModel(this.treeModel);
	}

	/**
	 * Permet d'ajouter un nouvel utilisateur dans la liste.
	 * 
	 * @param user
	 *            Nouvel utilisateur à ajouter dans l'arbre.
	 * @return La node qui a été ajoutée.
	 */
	private DefaultMutableTreeNode addNode (User user) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.treeModel.getRoot();
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(user, false);
		this.treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
		if (rootNode.equals(treeModel.getRoot())) {
			this.treeModel.nodeStructureChanged(rootNode);
		}
		return node;
	}

	/**
	 * Permet de modifier le renderer de l'arbre pour initialiser le logo des
	 * éléments affichés.
	 * 
	 * @author AwaX
	 * @created 1 mai 2014
	 * @version 1.0
	 */
	private class TreeNodeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 8461048841532611717L;

		@Override
		public Component getTreeCellRendererComponent (JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			// On récupère l'utilisateur
			if (node.getUserObject() instanceof User) {
				User user = (User) node.getUserObject();
				// Mise à jour de l'icône utilisateur
				switch (user.getStatus()) {
					case CONNECTED:
						setLeafIcon(iconConnected);
						break;
					case ABSENT:
						setLeafIcon(iconAbsent);
						break;
					case BUSY:
						setLeafIcon(iconBusy);
						break;
					default:
						setLeafIcon(iconDefault);
						break;
				}
			} else {
				setLeafIcon(iconDefault);
			}
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			return this;
		}
	}
}