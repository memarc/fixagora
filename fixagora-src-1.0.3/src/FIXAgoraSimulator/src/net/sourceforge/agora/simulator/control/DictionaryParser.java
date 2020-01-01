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
package net.sourceforge.agora.simulator.control;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class DictionaryParser.
 */
public class DictionaryParser {

	private static DictionaryParser dictionaryParser = null;

	private Document dataDictionaryDocument = null;

	private final Map<String, String> messageMap = new HashMap<String, String>();

	private Document transportDataDictionaryDocument = null;

	/**
	 * Instantiates a new dictionary parser.
	 *
	 * @param file the file
	 * @param file2 the file2
	 */
	private DictionaryParser() {

		super();
		
		String file = "./conf/FIX50SP2.xml";
		String file2 = "./conf/FIXT11.xml";


		try {

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();

			dataDictionaryDocument = builder.newDocument();
			transportDataDictionaryDocument = builder.newDocument();

		}
		catch (final Exception e) {

		}

		try {

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			dataDictionaryDocument = builder.parse(new File(file));
		}
		catch (final Exception e) {

		}

		parseFile(dataDictionaryDocument);

		transportDataDictionaryDocument = dataDictionaryDocument;

			DocumentBuilder builder = null;

			try {

				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				transportDataDictionaryDocument = builder.parse(new File(file2));
			}
			catch (final Exception e) {

				if (builder != null)
					dataDictionaryDocument = builder.newDocument();
			}

			parseFile(transportDataDictionaryDocument);

	}







	/**
	 * Gets the message name.
	 *
	 * @param messageType the message type
	 * @return the message name
	 */
	public static String getMessageName(final String messageType) {
		
		if(dictionaryParser==null)
			dictionaryParser = new DictionaryParser();

		return dictionaryParser.messageMap.get(messageType);

	}




	private void parseFile(final Document document) {


		final NodeList xmlMessages = document.getElementsByTagName("message");

		for (int i = 0; i < xmlMessages.getLength(); i++) {

			final Element xmlMessage = (Element) xmlMessages.item(i);
			final String name = xmlMessage.getAttribute("name");
			final String messageType = xmlMessage.getAttribute("msgtype");
	
			messageMap.put(messageType, name);

		}
	}

}
