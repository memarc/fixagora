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
package net.sourceforge.fixagora.basis.client.model;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class BasisProperties.
 */
public class BasisProperties {

	private Document document = null;
	private Element lastNameElement;
	private Element lastHostElement;
	private Element lastPortElement;
	
	private static Logger log = Logger.getLogger(BasisProperties.class);

	/**
	 * Instantiates a new basis properties.
	 */
	public BasisProperties() {

		super();

		load();
	}

	/**
	 * Load.
	 */
	public void load() {

		try {

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File("./conf/basis.xml"));

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

					if (propertyNode.getAttribute("name").equals("lastname"))
						lastNameElement = propertyNode;

					else if (propertyNode.getAttribute("name").equals("lasthost"))
						lastHostElement = propertyNode;

					else if (propertyNode.getAttribute("name").equals("lastname"))
						lastNameElement = propertyNode;

					else if (propertyNode.getAttribute("name").equals("lastport"))
						lastPortElement = propertyNode;
				}

			if (lastNameElement == null) {
				
				lastNameElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lastname");
				lastNameElement.setAttribute("value", "Admin");
				
				basisElement.appendChild(lastNameElement);
			}
			if (lastHostElement == null) {

				lastHostElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lasthost");
				lastHostElement.setAttribute("value", "localhost");
				
				basisElement.appendChild(lastHostElement);
			}
			if (lastPortElement == null) {
				
				lastPortElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lastport");
				lastPortElement.setAttribute("value", "4711");
				
				basisElement.appendChild(lastPortElement);
			}
		}
		catch (final Exception e) {

			try {

				final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.newDocument();

				final Element basisElement = document.createElement("basis");
				document.appendChild(basisElement);

				lastNameElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lastname");
				lastNameElement.setAttribute("value", "admin");
				basisElement.appendChild(lastNameElement);

				lastHostElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lasthost");
				lastHostElement.setAttribute("value", "localhost");
				basisElement.appendChild(lastHostElement);

				lastPortElement = document.createElement("property");
				lastNameElement.setAttribute("name", "lastport");
				lastPortElement.setAttribute("value", "4711");
				basisElement.appendChild(lastPortElement);

				final TransformerFactory tFactory = TransformerFactory.newInstance();
				final Transformer transformer = tFactory.newTransformer();
				final DOMSource source = new DOMSource(document);
				final StreamResult result = new StreamResult(new File("./conf/basis.xml"));
				
				transformer.transform(source, result);

			}
			catch (final Exception e1) {

				log.error("Bug", e);

			}
		}

	}

	/**
	 * Gets the last name element.
	 *
	 * @return the last name element
	 */
	public String getLastNameElement() {

		return lastNameElement.getAttribute("value");
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {

		this.lastNameElement.setAttribute("value", lastName);
	}

	/**
	 * Gets the last host.
	 *
	 * @return the last host
	 */
	public String getLastHost() {

		return lastHostElement.getAttribute("value");
	}

	/**
	 * Sets the last host element.
	 *
	 * @param lastHost the new last host element
	 */
	public void setLastHostElement(String lastHost) {

		this.lastHostElement.setAttribute("value", lastHost);
	}

	/**
	 * Gets the last port element.
	 *
	 * @return the last port element
	 */
	public String getLastPortElement() {

		return lastPortElement.getAttribute("value");
	}

	/**
	 * Sets the last port element.
	 *
	 * @param lastPort the new last port element
	 */
	public void setLastPortElement(String lastPort) {

		this.lastPortElement.setAttribute("value", lastPort);
	}

	/**
	 * Store.
	 */
	public void store() {

		try {

			final TransformerFactory tFactory = TransformerFactory.newInstance();
			final Transformer transformer = tFactory.newTransformer();
			final DOMSource source = new DOMSource(document);
			final StreamResult result = new StreamResult(new File("./conf/basis.xml"));
			transformer.transform(source, result);
		}
		catch (final Exception e1) {

			log.error("Bug", e1);
		}

	}

}
