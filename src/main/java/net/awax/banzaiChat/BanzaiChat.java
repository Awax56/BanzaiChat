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

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import net.awax.banzaiChat.util.ResourceManager;

/**
 * Permet de lancer le chat dans sa version client.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class BanzaiChat {

	private static final Logger logger = Logger.getLogger(BanzaiChat.class);

	/**
	 * Permet de lancer l'application.
	 * 
	 * @param args
	 *            Pas d'arguments.
	 */
	public static void main (String[] args) {
		/*
		 * Initialisation du logger
		 */
		File log4jConfigFile = ResourceManager.LOG4J_FILE;
		if (log4jConfigFile.exists()) {
			DOMConfigurator.configure(log4jConfigFile.getPath());
		} else {
			logger.fatal("Log4j configuration file not found (" + ResourceManager.LOG4J_FILE.getAbsolutePath() + ")");
			Runtime.getRuntime().exit(-1);
		}
		logger.info("Launching Banzaï Chat Client");

		/*
		 * Look & Feel Nimbus
		 */
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					logger.info("Setting Nimbus look & feel");
					break;
				}
			}
		} catch (Exception e) {
			logger.warn("Cannot set java look and feel");
		}

		/*
		 * Lancement de l'application
		 */
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				try {
					final ApplicationModel model = new ApplicationModel();
					final ApplicationController controller = new ApplicationController(model);
					controller.showGui();
				} catch (Exception e) {
					logger.fatal("An error occurred while launching application", e);
					System.exit(-1);
				}
			}
		});
	}
}