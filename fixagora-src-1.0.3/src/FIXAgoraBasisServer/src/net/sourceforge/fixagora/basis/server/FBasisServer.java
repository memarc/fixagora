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
package net.sourceforge.fixagora.basis.server;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BasisRequestHandler;
import net.sourceforge.fixagora.basis.server.control.FBasisClientAcceptor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.mina.common.IoHandlerAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class FBasisServer.
 */
public class FBasisServer extends IoHandlerAdapter {

	private static Logger log = Logger.getLogger(FBasisServer.class);

	//-verbose:gc -XX:+UseParallelGC -XX:NewRatio=3 -Xms1024m -XX:MaxPermSize=512m
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {

		DOMConfigurator.configure("./conf/log4j.xml");
		log.info("Start FIX Agora Server");
		log.info("LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow");

		for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
			log.info("System: " + entry.getKey() + "=" + entry.getValue());
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

		new FBasisServer();

	}

	/**
	 * Instantiates a new f basis server.
	 */
	public FBasisServer() {

		super();

		try {
			final BasisPersistenceHandler basisPersistenceHandler = new BasisPersistenceHandler();
			
			List<AbstractRequests> abstractRequests = new ArrayList<AbstractRequests>();
			final BasisRequestHandler fBasisRequestHandler = new BasisRequestHandler();
			fBasisRequestHandler.init(basisPersistenceHandler, null);

			ServiceLoader<AbstractRequests> serviceLoader = ServiceLoader.load(AbstractRequests.class);

			for (AbstractRequests abstractRequests2 : serviceLoader) {

				log.info("FIX Agora Plugin \"" + abstractRequests2.getName() + "\" loaded.");
				log.info(abstractRequests2.getName()+" - License: "+abstractRequests2.getLicense());
				
				try {
					abstractRequests2.init(basisPersistenceHandler, fBasisRequestHandler.getBusinessComponentHandler());
					abstractRequests.add(abstractRequests2);
				}
				catch (Exception e) {

					log.fatal("Initialization of "+abstractRequests2.getName()+" failed.", e);
				}

			}

			String host = "127.0.0.1";
			int port = 4711;
			
			try {

				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();

				File file = new File("./conf/basis.xml");
				
				log.info("Load configuration from "+file.getAbsolutePath());
				
				Document document = builder.parse(file);

				NodeList xmlElementsRoot = document.getElementsByTagName("basis");

				Element basisElement = null;

				if (xmlElementsRoot.getLength() > 0)
					basisElement = (Element) xmlElementsRoot.item(0);

				else {

					basisElement = document.createElement("basis");
					document.appendChild(basisElement);
				}

				final NodeList propertyNodes = basisElement.getChildNodes();

				for (int j = 0; j < propertyNodes.getLength(); j++)
					if (propertyNodes.item(j) instanceof Element) {

						final Element propertyNode = (Element) propertyNodes.item(j);

						if (propertyNode.getAttribute("name").equals("host"))
							host = propertyNode.getAttribute("value");

						else if (propertyNode.getAttribute("name").equals("port"))
							port = Integer.parseInt(propertyNode.getAttribute("value"));
					}

			}
			catch (final Exception e) {
				log.warn("No valid configuration found. Please check the file basis.xml in the conf directory.", e);
			}
			
			log.info("Server is accepting clients as host="+host+" on port="+port);

			FBasisClientAcceptor basisClientAcceptor = new FBasisClientAcceptor(abstractRequests, host, port);
			basisPersistenceHandler.setBasisClientAcceptor(basisClientAcceptor);

			abstractRequests.add(fBasisRequestHandler);

			Runtime.getRuntime().addShutdownHook(new Thread() {

				public void run() {

					basisPersistenceHandler.close();
					fBasisRequestHandler.close();
				}
			});

		}
		catch (Exception e) {
			
			log.error("Bug", e);
		}

	}

}
