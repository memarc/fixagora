/**
 * Copyright (C) 2012-2013 Alexander Pinnow
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 **/
package net.sourceforge.fixagora.basis.client.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.sourceforge.fixagora.basis.client.model.BasisProperties;
import net.sourceforge.fixagora.basis.client.view.BasisClientFrame;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse.LoginStatus;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * The Class BasisClient.
 */
public class BasisClient implements BasisClientConnectorListener {

	private String user = null;

	private String password = null;

	private BasisClientConnector basisClientConnector = null;

	private BasisProperties basisProperties = null;

	private BasisClientFrame basisClientFrame = null;

	private String host;

	private int port;

	private int failedCounter;

	private static String directHost = null;

	private static int directPort = 0;

	private static String directUser = null;

	private static String directPassword = null;
	
	private static Logger log = Logger.getLogger(BasisClient.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {

		if (args.length == 4) {
			directHost = args[0];
			directPort = Integer.parseInt(args[1]);
			directUser = args[2];
			directPassword = args[3];
		}
		
		String logName = "fix-agora-client";
		
		if(directUser!=null)
			logName = logName+"-"+directUser;
		
		System.setProperty("agoralogfilename", logName);
		
		DOMConfigurator.configure("./conf/log4j.xml");
		log.info("Start FIX Agora Client");
		log.info("LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow");

		for(Entry<Object, Object> entry: System.getProperties().entrySet())
		{
			log.info("System: "+entry.getKey()+"="+entry.getValue());
		}
		
		@SuppressWarnings("rawtypes")
		Class[] parameters = new Class[] { URL.class };

		try {
			File dir = new File("extlib");
			File files[] = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith(".jar")) {
					URL url = files[i].toURI().toURL();

					URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					@SuppressWarnings("rawtypes")
					Class sysclass = URLClassLoader.class;

					try {
						@SuppressWarnings("unchecked")
						Method method = sysclass.getDeclaredMethod("addURL", parameters);
						method.setAccessible(true);
						method.invoke(sysloader, new Object[] { url });
					}
					catch (Throwable t) {
						log.fatal("External Library "+url.toString()+" not loaded.", t);
					}
					
					log.info("External Library "+url.toString()+" loaded.");

				}
			}
		}
		catch (Exception e) {
			log.fatal("External Libraries  not loaded.", e);
		}

		new BasisClient();
	}

	/**
	 * Instantiates a new basis client.
	 */
	public BasisClient() {

		super();

		try {

			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());

		}
		catch (Exception e) {

			log.fatal("Bug", e);
		}

		UIManager.put("Panel.background", new Color(204, 216, 255));
		UIManager.put("OptionPane.background", new Color(204, 216, 255));
		UIManager.put("ComboBox.disabledBackground", new Color(204, 216, 255));
		UIManager.put("ComboBox.disabledForeground", Color.BLACK);
		UIManager.put("ComboBox.background", new Color(255, 243, 204));
		UIManager.put("TextField.background", new Color(255, 243, 204));
		UIManager.put("TextField.foreground", Color.BLACK);
		UIManager.put("Button.opaque", false);
		UIManager.put("TextField.inactiveBackground", new Color(204, 216, 255));
		UIManager.put("ScrollPane.background", new Color(255, 243, 204));
		UIManager.put("List.background", new Color(255, 243, 204));
		UIManager.put("List.foreground", Color.BLACK);
		Locale.setDefault(Locale.ENGLISH);
		JComponent.setDefaultLocale(Locale.ENGLISH);

		final Enumeration<Object> keys = UIManager.getDefaults().keys();

		while (keys.hasMoreElements()) {

			final Object key = keys.nextElement();
			final Object value = UIManager.get(key);

			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, new Font("Dialog", Font.PLAIN, 12));
		}

		login(null);

	}

	private void login(String comment) {

		basisProperties = new BasisProperties();

		if (directHost == null || failedCounter > 10) {

			String lastHost = basisProperties.getLastHost();

			String lastPort = basisProperties.getLastPortElement();

			String lastUser = basisProperties.getLastNameElement();

			LoginDialog loginDialog = new LoginDialog(lastHost, lastPort, lastUser, comment);

			loginDialog.setAlwaysOnTop(true);
			
			loginDialog.setVisible(true);

			if (loginDialog.isCancelled())
				System.exit(0);
			else if (basisClientFrame != null) {
				basisClientFrame.setVisible(false);
			}

			host = loginDialog.getHost();

			port = Integer.parseInt(loginDialog.getPort());

			user = loginDialog.getUser();

			password = loginDialog.getPassword();
		}
		else {
			host = directHost;
			port = directPort;
			user = directUser;
			password = directPassword;
		}

		basisClientConnector = new BasisClientConnector(host, port);

		basisClientConnector.addBasisClientConnectorListener(this);

		basisClientConnector.connect();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

		FUser fUser = new FUser();
		fUser.setName(user);

		try {

			fUser.setPlainPassword(password);
		}
		catch (Exception e) {

			log.fatal("Bug", e);
		}

		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new LoginRequest(fUser, RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof LoginResponse) {
			final LoginResponse loginResponse = (LoginResponse) abstractResponse;

			if (loginResponse.getLoginStatus() == LoginStatus.SUCCESS) {
				
				directHost = null;

				basisProperties.setLastPortElement(Integer.toString(port));
				basisProperties.setLastHostElement(host);
				basisProperties.setLastName(user);
				basisProperties.store();

				final WaitDialog waitDialog = new WaitDialog(null, "Loading application ...");

				final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						try {
							basisClientConnector.setFUser(loginResponse.getFUser());
							if (basisClientFrame != null)
								basisClientFrame.setVisible(false);
							basisClientFrame = new BasisClientFrame(waitDialog, basisProperties, basisClientConnector, loginResponse);
							basisClientFrame.setVisible(true);
							basisClientFrame.setExtendedState(basisClientFrame.getExtendedState() | Frame.MAXIMIZED_BOTH);
						}
						catch (Exception e) {
							
							log.fatal("Bug", e);
						}
						return null;
					}

					@Override
					protected void done() {

						super.done();
						waitDialog.setVisible(false);
					}

				};

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					
						waitDialog.setVisible(true);
						swingWorker.execute();
						
						
					}
				});
				
			}
			else {

				failedCounter++;

				if (basisClientFrame != null)
					basisClientFrame.setVisible(false);

				login(loginResponse.getResponseText());
			}
		}
		else if (abstractResponse instanceof ErrorResponse) {
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

		failedCounter++;
		if (basisClientFrame != null)
			basisClientFrame.onDisconnected();
		login("Server unreachable");

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

		// TODO Auto-generated method stub

	}

}
