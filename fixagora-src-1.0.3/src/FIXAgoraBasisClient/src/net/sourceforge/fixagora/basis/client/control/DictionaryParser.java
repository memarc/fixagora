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

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement;
import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement.FieldType;
import net.sourceforge.fixagora.basis.client.model.message.FIXComponent;
import net.sourceforge.fixagora.basis.client.model.message.FIXField;
import net.sourceforge.fixagora.basis.client.model.message.FIXFieldValue;
import net.sourceforge.fixagora.basis.client.model.message.FIXGroup;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessage;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The Class DictionaryParser.
 */
public class DictionaryParser {

	/**
	 * Check.
	 *
	 * @param file the file
	 * @return true, if successful
	 */
	public static boolean check(final File file) {

		try {

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(file);

			if (document.getElementsByTagName("message").getLength() == 0)
				return false;

		}
		catch (final Exception e) {

			return false;
		}

		return true;
	}

	private HashMap<String, Element> componentMap = null;

	private Document dataDictionaryDocument = null;

	private HashMap<String, Element> fieldNameMap = null;

	private HashMap<String, Element> fieldNumberMap = null;

	private final List<FIXMessage> messageList = new ArrayList<FIXMessage>();

	private final Map<String, FIXMessage> messageMap = new HashMap<String, FIXMessage>();

	private Document transportDataDictionaryDocument = null;
	
	private static Logger log = Logger.getLogger(DictionaryParser.class);

	/**
	 * Instantiates a new dictionary parser.
	 *
	 * @param stringBuffer the string buffer
	 */
	public DictionaryParser(StringBuffer stringBuffer) {

		super();

		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			dataDictionaryDocument = builder.parse(new InputSource(new StringReader(stringBuffer.toString())));
		}
		catch (final Exception e) {

			log.error("Bug", e);
		}

		parseFile(dataDictionaryDocument);

		transportDataDictionaryDocument = dataDictionaryDocument;

		final NodeList xmlFieldValues = dataDictionaryDocument.getElementsByTagName("fix");

		for (int j = 0; j < xmlFieldValues.getLength(); j++)
			if (xmlFieldValues.item(j).getNodeType() == Node.ELEMENT_NODE) {

				final Element xmlFieldValue = (Element) xmlFieldValues.item(j);
				final String enumString = xmlFieldValue.getAttribute("major");
				if (enumString != null && Integer.parseInt(enumString) > 4) {
					DocumentBuilder builder = null;

					try {

						final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						builder = factory.newDocumentBuilder();
						transportDataDictionaryDocument = builder.parse(new File("./conf/FIXT11.xml"));
					}
					catch (final Exception e) {

						if (builder != null)
							dataDictionaryDocument = builder.newDocument();
					}

					parseFile(transportDataDictionaryDocument);
				}
			}

	}

	private AbstractFIXElement getAbstractFIXElement(final Element messageElementNode, final boolean conditionally) {

		if (messageElementNode.getNodeName().equals("field")) {

			final String name = messageElementNode.getAttribute("name");

			FieldType fieldType = FieldType.OPTIONAL;

			if (conditionally)
				fieldType = FieldType.CONDITIONALLY;

			if (messageElementNode.getAttribute("required").equals("Y"))
				fieldType = FieldType.REQUIRED;

			final Element fieldElement = fieldNameMap.get(name);
			final int number = Integer.parseInt(fieldElement.getAttribute("number"));
			final FIXField.Type type = FIXField.Type.valueOf(fieldElement.getAttribute("type").replace('-', '_'));
			final NodeList xmlFieldValues = fieldElement.getElementsByTagName("value");
			final List<FIXFieldValue> fieldValues = new ArrayList<FIXFieldValue>();

			for (int j = 0; j < xmlFieldValues.getLength(); j++)
				if (xmlFieldValues.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element xmlFieldValue = (Element) xmlFieldValues.item(j);
					final String enumString = xmlFieldValue.getAttribute("enum");
					final String description = xmlFieldValue.getAttribute("description");
					fieldValues.add(new FIXFieldValue(enumString, description));
				}

			return new FIXField(name, number, fieldType, type, fieldValues);
		}

		if (messageElementNode.getNodeName().equals("component")) {

			final String name = messageElementNode.getAttribute("name");
			final Element componentElement = componentMap.get(name);
			final List<AbstractFIXElement> abstractFIXElements = new ArrayList<AbstractFIXElement>();
			final NodeList componentElementNodes = componentElement.getChildNodes();

			FieldType fieldType = FieldType.OPTIONAL;

			if (conditionally)
				fieldType = FieldType.CONDITIONALLY;

			if (messageElementNode.getAttribute("required").equals("Y"))
				fieldType = FieldType.REQUIRED;

			boolean first = conditionally;

			for (int j = 0; j < componentElementNodes.getLength(); j++)
				if (componentElementNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element componentElementEntry = (Element) componentElementNodes.item(j);
					abstractFIXElements.add(getAbstractFIXElement(componentElementEntry, first));
					first = false;
				}

			return new FIXComponent(name, 0, fieldType, abstractFIXElements);
		}

		if (messageElementNode.getNodeName().equals("group")) {

			final String name = messageElementNode.getAttribute("name");
			final Element fieldElement = fieldNameMap.get(name);
			final int number = Integer.parseInt(fieldElement.getAttribute("number"));
			final List<AbstractFIXElement> abstractFIXElements = new ArrayList<AbstractFIXElement>();
			final NodeList componentElementNodes = messageElementNode.getChildNodes();

			FieldType fieldType = FieldType.OPTIONAL;

			if (conditionally)
				fieldType = FieldType.CONDITIONALLY;

			if (messageElementNode.getAttribute("required").equals("Y"))
				fieldType = FieldType.REQUIRED;

			boolean first = true;

			for (int j = 0; j < componentElementNodes.getLength(); j++)
				if (componentElementNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element componentElementEntry = (Element) componentElementNodes.item(j);
					abstractFIXElements.add(getAbstractFIXElement(componentElementEntry, first));
					first = false;
				}

			return new FIXGroup(name, number, fieldType, abstractFIXElements);
		}
		return null;
	}

	/**
	 * Gets the default appl ver id.
	 *
	 * @return the default appl ver id
	 */
	public String getDefaultApplVerID() {

		final StringBuffer defaultApplVerID = new StringBuffer();

		final NodeList xmlFIXRoot = dataDictionaryDocument.getElementsByTagName("fix");

		if (xmlFIXRoot.getLength() > 0) {

			final Element xmlFIXlement = (Element) xmlFIXRoot.item(0);

			defaultApplVerID.append("FIX.");

			defaultApplVerID.append(xmlFIXlement.getAttribute("major"));

			defaultApplVerID.append(".");

			defaultApplVerID.append(xmlFIXlement.getAttribute("minor"));

			if (xmlFIXlement.getAttribute("servicepack").length() > 0) {

				defaultApplVerID.append("SP");
				defaultApplVerID.append(xmlFIXlement.getAttribute("servicepack"));
			}

		}

		return defaultApplVerID.toString();
	}

	/**
	 * Gets the fIX message.
	 *
	 * @param messageType the message type
	 * @return the fIX message
	 */
	public FIXMessage getFIXMessage(final String messageType) {

		FIXMessage fixMessage = messageMap.get(messageType);

		return fixMessage;
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public FIXComponent getHeader() {

		FIXComponent header = null;
		final NodeList xmlHeaders = transportDataDictionaryDocument.getElementsByTagName("header");

		for (int i = 0; i < xmlHeaders.getLength(); i++) {

			final Element xmlHeader = (Element) xmlHeaders.item(i);
			final NodeList messageElementNodes = xmlHeader.getChildNodes();
			final List<AbstractFIXElement> abstractFIXElements = new ArrayList<AbstractFIXElement>();

			for (int j = 0; j < messageElementNodes.getLength(); j++)
				if (messageElementNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					final Element messageElementNode = (Element) messageElementNodes.item(j);
					abstractFIXElements.add(getAbstractFIXElement(messageElementNode, false));
				}

			header = new FIXComponent("Header", -1, FieldType.REQUIRED, abstractFIXElements);
		}

		return header;
	}

	/**
	 * Gets the message list.
	 *
	 * @return the message list
	 */
	public List<FIXMessage> getMessageList() {

		return messageList;
	}

	/**
	 * Gets the message name.
	 *
	 * @param messageType the message type
	 * @return the message name
	 */
	public String getMessageName(final String messageType) {

		FIXMessage fixMessage = messageMap.get(messageType);

		if (fixMessage != null)
			return fixMessage.getName();

		return "?";
	}

	/**
	 * Gets the trailer.
	 *
	 * @return the trailer
	 */
	public FIXComponent getTrailer() {

		FIXComponent trailer = null;
		final NodeList xmlTrailers = transportDataDictionaryDocument.getElementsByTagName("trailer");

		for (int i = 0; i < xmlTrailers.getLength(); i++) {

			final Element xmlTrailer = (Element) xmlTrailers.item(i);
			final NodeList messageElementNodes = xmlTrailer.getChildNodes();
			final List<AbstractFIXElement> abstractFIXElements = new ArrayList<AbstractFIXElement>();

			for (int j = 0; j < messageElementNodes.getLength(); j++)
				if (messageElementNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element messageElementNode = (Element) messageElementNodes.item(j);
					abstractFIXElements.add(getAbstractFIXElement(messageElementNode, false));
				}

			trailer = new FIXComponent("Trailer", -2, FieldType.REQUIRED, abstractFIXElements);
		}

		return trailer;
	}

	private void parseFile(final Document document) {

		fieldNameMap = new HashMap<String, Element>();
		fieldNumberMap = new HashMap<String, Element>();
		final NodeList xmlFieldsRoot = document.getElementsByTagName("fields");

		for (int i = 0; i < xmlFieldsRoot.getLength(); i++) {

			final Element xmlFieldsElement = (Element) xmlFieldsRoot.item(i);
			final NodeList xmlFieldRoot = xmlFieldsElement.getElementsByTagName("field");

			for (int j = 0; j < xmlFieldRoot.getLength(); j++) {

				final Element xmlFieldElement = (Element) xmlFieldRoot.item(j);

				fieldNameMap.put(xmlFieldElement.getAttribute("name"), xmlFieldElement);
				fieldNumberMap.put(xmlFieldElement.getAttribute("number"), xmlFieldElement);
			}
		}

		componentMap = new HashMap<String, Element>();

		final NodeList xmlComponentsRoot = document.getElementsByTagName("components");

		for (int i = 0; i < xmlComponentsRoot.getLength(); i++) {

			final Element xmlComponentsElement = (Element) xmlComponentsRoot.item(i);
			final NodeList xmlComponentNodeList = xmlComponentsElement.getChildNodes();

			for (int j = 0; j < xmlComponentNodeList.getLength(); j++)
				if (xmlComponentNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element xmlComponentElement = (Element) xmlComponentNodeList.item(j);
					componentMap.put(xmlComponentElement.getAttribute("name"), xmlComponentElement);
				}
		}

		final NodeList xmlMessages = document.getElementsByTagName("message");

		for (int i = 0; i < xmlMessages.getLength(); i++) {

			final Element xmlMessage = (Element) xmlMessages.item(i);
			final NodeList messageElementNodes = xmlMessage.getChildNodes();
			final List<AbstractFIXElement> abstractFIXElements = new ArrayList<AbstractFIXElement>();

			for (int j = 0; j < messageElementNodes.getLength(); j++)
				if (messageElementNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element messageElementNode = (Element) messageElementNodes.item(j);
					abstractFIXElements.add(getAbstractFIXElement(messageElementNode, false));
				}

			final String name = xmlMessage.getAttribute("name");
			final String messageType = xmlMessage.getAttribute("msgtype");
			final String messageCat = xmlMessage.getAttribute("msgcat");
			final FIXMessage fixMessage = new FIXMessage(name, messageType, messageCat, abstractFIXElements, this);

			messageMap.put(fixMessage.getMessageType(), fixMessage);
			messageList.add(fixMessage);

			Collections.sort(messageList);
		}
	}

}
