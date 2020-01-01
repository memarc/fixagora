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
package net.sourceforge.fixagora.basis.server.control.component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AssignedInitiatorSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DefaultSessionFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

/**
 * The Class FIXInitiatorComponentHandler.
 */
public class FIXInitiatorComponentHandler extends AbstractComponentHandler implements Application {

	private FIXInitiator fixInitiator = null;

	private SocketInitiator socketInitiator = null;

	private DataDictionary dataDictionary;

	private int major = 0;

	private int minor = 0;

	private int servicepack = 0;

	private SessionID channelID = null;

	private int startLevel = 0;

	private String appVerID = null;

	private static Logger log = Logger.getLogger(FIXInitiatorComponentHandler.class);

	private DecimalFormat decimalFormat = new DecimalFormat("####");

	private String partyID = null;

	private String partyIDSource = null;

	private Integer partyRole = null;

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof FIXInitiator) {
			fixInitiator = (FIXInitiator) abstractBusinessComponent;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#startProcessor()
	 */
	@Override
	protected void startProcessor() {

		if (fixInitiator.getConnectAtStartup())
			start();
		super.startProcessor();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getStartLevel()
	 */
	@Override
	public int getStartLevel() {

		return startLevel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#start(org.jboss.netty.channel.Channel)
	 */
	@Override
	public synchronized void start(Channel channel) {

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
		logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is starting FIX initiator.");
		logEntry.setMessageComponent(fixInitiator.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);

		Thread thread = new Thread() {

			public void run() {

				FIXInitiatorComponentHandler.this.start();
			}
		};
		thread.start();
	}

	private synchronized void start() {

		if (startLevel != 0)
			return;

		final StringBuffer stringBuffer = new StringBuffer("[default]\n");
		stringBuffer.append("FileStorePath=");
		stringBuffer.append("./log/fix/" + fixInitiator.getName());
		stringBuffer.append("\nFileLogPath=");
		stringBuffer.append("./log/fix/" + fixInitiator.getName());

		for (DataDictionary dataDictionary : dataDictionaries)
			if (dataDictionary.getName().trim().equals(fixInitiator.getDataDictionary().trim()))
				this.dataDictionary = dataDictionary;

		if (dataDictionary == null) {
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText("Data dictionary " + fixInitiator.getDataDictionary() + " not found on server.");
			logEntry2.setMessageComponent(fixInitiator.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			return;
		}

		String beginString = "?";

		StringBuffer defaultApplVerID = new StringBuffer();

		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			Document dataDictionaryDocument = builder.parse(new InputSource(new StringReader(dataDictionary.getDictionary().toString())));

			final NodeList xmlFieldValues = dataDictionaryDocument.getElementsByTagName("fix");

			for (int j = 0; j < xmlFieldValues.getLength(); j++)
				if (xmlFieldValues.item(j).getNodeType() == Node.ELEMENT_NODE) {

					final Element xmlFieldValue = (Element) xmlFieldValues.item(j);

					defaultApplVerID.append("FIX.");

					defaultApplVerID.append(xmlFieldValue.getAttribute("major"));

					defaultApplVerID.append(".");

					defaultApplVerID.append(xmlFieldValue.getAttribute("minor"));

					if (xmlFieldValue.getAttribute("servicepack").length() > 0) {

						defaultApplVerID.append("SP");
						defaultApplVerID.append(xmlFieldValue.getAttribute("servicepack"));
						final String sp = xmlFieldValue.getAttribute("minor");
						servicepack = Integer.parseInt(sp);
					}

					if (defaultApplVerID.toString().equals("FIX.5.0"))
						appVerID = "7";

					if (defaultApplVerID.toString().equals("FIX.5.0SP1"))
						appVerID = "8";

					if (defaultApplVerID.toString().equals("FIX.5.0SP2"))
						appVerID = "9";

					final String enumString = xmlFieldValue.getAttribute("major");
					major = Integer.parseInt(enumString);

					final String enumString2 = xmlFieldValue.getAttribute("minor");
					minor = Integer.parseInt(enumString2);

					if (major > 4)
						beginString = "FIXT.1.1";
					else {
						beginString = "FIX." + xmlFieldValue.getAttribute("major").trim() + "." + xmlFieldValue.getAttribute("minor").trim();

						partyID = null;

						partyIDSource = null;

						partyRole = null;

						if (fixInitiator.getCounterparty() != null) {
							Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, fixInitiator.getCounterparty().getId(),
									businessComponentHandler);
							if (counterparty != null) {

								CounterPartyPartyID correspondentBroker = null;

								CounterPartyPartyID defaultForInitiator = null;

								CounterPartyPartyID defaultId = null;

								for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
									if (counterPartyPartyID.getAbstractBusinessComponent() == null) {

										defaultId = counterPartyPartyID;

									}
									else if (counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole() == 26) {
										correspondentBroker = counterPartyPartyID;
									}
									else {
										defaultForInitiator = counterPartyPartyID;
									}
								}

								if (correspondentBroker != null) {
									partyID = correspondentBroker.getPartyID();
									partyIDSource = correspondentBroker.getPartyIDSource();
									partyRole = 26;
								}
								else if (defaultForInitiator != null) {
									partyID = defaultForInitiator.getPartyID();
									partyIDSource = defaultForInitiator.getPartyIDSource();
									partyRole = defaultForInitiator.getPartyRole();
								}
								else if (defaultId != null) {
									partyID = defaultId.getPartyID();
									partyIDSource = defaultId.getPartyIDSource();
									partyRole = 26;
								}

							}
						}

					}
				}
		}
		catch (Exception e1) {
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText("Data dictionary " + fixInitiator.getDataDictionary() + " is not valid.");
			logEntry2.setMessageComponent(fixInitiator.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			return;
		}

		if (beginString.startsWith("FIXT")) {

			stringBuffer.append("\nAppDataDictionary=./conf/");
			stringBuffer.append(fixInitiator.getDataDictionary());
			stringBuffer.append("\nTransportDataDictionary=./conf/FIXT11.xml");
		}
		else {
			stringBuffer.append("\nDataDictionary=./conf/");
			stringBuffer.append(fixInitiator.getDataDictionary());
		}

		stringBuffer.append("\nBeginString=");
		stringBuffer.append(beginString);
		stringBuffer.append("\nConnectionType=initiator");
		stringBuffer.append("\nStartTime=00:00:00\n");
		stringBuffer.append("EndTime=00:00:00\n");
		stringBuffer.append("HeartBtInt=");
		stringBuffer.append(fixInitiator.getHeartbeatInterval());

		stringBuffer.append("\nSocketConnectHost=");
		stringBuffer.append(fixInitiator.getSocketAdress());
		stringBuffer.append("");
		stringBuffer.append("\nSocketConnectPort=");
		stringBuffer.append(fixInitiator.getSocketPort());

		stringBuffer.append("\nCheckLatency=Y");
		stringBuffer.append("\nPersistMessages=");
		if (fixInitiator.getPersistMessage())
			stringBuffer.append("Y");
		else
			stringBuffer.append("N");
		stringBuffer.append("\nValidateFieldsOutOfOrder=Y");
		stringBuffer.append("\nValidateFieldsHaveValues=Y");
		stringBuffer.append("\nValidateUserDefinedFields=Y");

		stringBuffer.append("\n[session]\n");

		stringBuffer.append("SenderCompID=");
		stringBuffer.append(fixInitiator.getSenderCompID());
		stringBuffer.append("\nTargetCompID=");
		stringBuffer.append(fixInitiator.getTargetCompID());
		stringBuffer.append("\nReconnectInterval=");
		stringBuffer.append(fixInitiator.getReconnectInterval());
		stringBuffer.append("\nValidateIncomingMessage=Y");
		stringBuffer.append("\n");
		if (beginString.startsWith("FIXT")) {
			stringBuffer.append("\nDefaultApplVerID=");
			stringBuffer.append(defaultApplVerID.toString());
		}

		try {

			startLevel = 1;
			fixInitiator.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixInitiator));

			final InputStream inputStream = new ByteArrayInputStream(stringBuffer.toString().getBytes("UTF-8"));
			final SessionSettings settings = new SessionSettings(inputStream);
			final MessageStoreFactory storeFactory = new FileStoreFactory(settings);
			quickfix.LogFactory logFactory = new EmptyLogFactory();
			if (fixInitiator.getPersistMessage())
				logFactory = new FileLogFactory(settings);
			final quickfix.MessageFactory messageFactory = new DefaultMessageFactory();
			socketInitiator = new SocketInitiator(new DefaultSessionFactory(this, storeFactory, logFactory, messageFactory), settings);

			socketInitiator.start();
			

		}
		catch (final Exception e) {

			startLevel = 0;
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText(e.getMessage());
			logEntry2.setMessageComponent(fixInitiator.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			socketInitiator = null;
			fixInitiator.setTransientValues(businessComponentHandler);
			basisPersistenceHandler.send(new UpdateResponse(fixInitiator));
			return;
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#stop(org.jboss.netty.channel.Channel)
	 */
	@Override
	public synchronized void stop(final Channel channel) {

		if (startLevel == 3)
			return;

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
		logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is stopping FIX initiator.");
		logEntry.setMessageComponent(fixInitiator.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);

		startLevel = 3;
		fixInitiator.setTransientValues(businessComponentHandler);
		basisPersistenceHandler.send(new UpdateResponse(fixInitiator));

		Thread thread = new Thread() {

			public void run() {

				stopInitiator(channel);

			}
		};
		thread.start();
	}

	private synchronized void stopInitiator(final Channel channel) {

		if (socketInitiator != null) {

			socketInitiator.stop();
			socketInitiator = null;

		}

		startLevel = 0;
		fixInitiator.setTransientValues(businessComponentHandler);
		basisPersistenceHandler.send(new UpdateResponse(fixInitiator));

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

		String messageType = arg0.getHeader().getString(MsgType.FIELD);
		if (messageType.equals("3")) {
			logRejectMessage(arg0, false);
		}
		else if (messageType.equals("5")) {
			try {
				if (arg0.isSetField(58)) {
					String info = arg0.getString(58);
					LogEntry logEntry2 = new LogEntry();
					logEntry2.setLogDate(new Date());
					logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
					logEntry2.setMessageText("Received FIX Logout (5): " + info);
					logEntry2.setMessageComponent(fixInitiator.getName());
					basisPersistenceHandler.writeLogEntry(logEntry2);
				}
			}
			catch (Exception exception) {
				log.error("Bug", exception);
			}

		}

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public synchronized void fromApp(Message message, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

		Message newMessage = (Message) message.clone();
		String messageType = newMessage.getHeader().getString(MsgType.FIELD);

		if (messageType.equals("j")) {
			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			StringBuffer stringBuffer = new StringBuffer("Received FIX business message reject (j). Please contact your administrator. ");
			if (message.isSetField(373)) {
				stringBuffer.append("Business reject reason: ");

				switch (message.getInt(380)) {
					case 0:
						stringBuffer.append("Other.");
						break;
					case 1:
						stringBuffer.append("Unknown ID.");
						break;
					case 2:
						stringBuffer.append("Unknown Security.");
						break;
					case 3:
						stringBuffer.append("Unsupported Message Type.");
						break;
					case 4:
						stringBuffer.append("Application not available.");
						break;
					case 5:
						stringBuffer.append("Conditionally required field missing.");
						break;
					case 6:
						stringBuffer.append("Not Authorized.");
						break;
					case 7:
						stringBuffer.append("DeliverTo firm not available at this time.");
						break;
					case 18:
						stringBuffer.append("Invalid price increment.");
						break;
				}

				stringBuffer.append(" ");
			}
			if (message.isSetField(45)) {
				stringBuffer.append("Reference sequence number: ");
				stringBuffer.append(message.getString(45));
				stringBuffer.append(" ");
			}
			if (message.isSetField(372)) {
				stringBuffer.append("Reference message type: ");
				stringBuffer.append(message.getString(372));
				stringBuffer.append(" ");
			}
			if (message.isSetField(58)) {
				stringBuffer.append("Text: ");
				stringBuffer.append(message.getString(58));
				stringBuffer.append(" ");
			}
			logEntry.setMessageText(stringBuffer.toString());
			logEntry.setMessageComponent(fixInitiator.getName());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}
		else if (messageType.equals("y")) {
			businessComponentHandler.handleSecurityList(newMessage);
		}
		else {
			if (messageType.equals("X")) {

				for (int i = 1; i <= newMessage.getGroupCount(268); i++) {
					try {
						Group mdEntry = newMessage.getGroup(i, 268);
						if (mdEntry.isSetField(22)) {
							String securityIDSource = mdEntry.getString(22);
							FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(mdEntry.getString(48), securityIDSource);
							if (security != null) {
								mdEntry.setString(48, security.getSecurityID());
								if (security.getSecurityDetails().getSecurityIDSource() != null) {
									mdEntry.setString(22, security.getSecurityDetails().getSecurityIDSource());
								}
								else
									mdEntry.removeField(22);
							}
						}
						if (major == 4 && partyID != null && partyIDSource != null && partyRole != null) {
							Group instrumentParty = new Group(453, 448, new int[] { 448, 447, 452 });
							instrumentParty.setString(448, partyID);
							instrumentParty.setChar(447, partyIDSource.charAt(0));
							instrumentParty.setInt(452, partyRole);
							mdEntry.addGroup(instrumentParty);
						}
					}
					catch (Exception exception) {
						log.error("Bug", exception);
					}
				}
			}
			else if (messageType.equals("W")) {
				try {
					if (newMessage.isSetField(22)) {
						String securityIDSource = newMessage.getString(22);
						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(newMessage.getString(48), securityIDSource);
						if (security != null) {
							newMessage.setString(48, security.getSecurityID());
							if (security.getSecurityDetails().getSecurityIDSource() != null) {
								newMessage.setString(22, security.getSecurityDetails().getSecurityIDSource());
							}
							else
								newMessage.removeField(22);
						}
						
					}
				}
				catch (Exception exception) {
					log.error("Bug", exception);
				}
				
				for (int i = 1; i <= newMessage.getGroupCount(268); i++) {
					try {
						Group mdEntry = newMessage.getGroup(i, 268);
						if (major == 4 && partyID != null && partyIDSource != null && partyRole != null) {
							Group instrumentParty = new Group(453, 448, new int[] { 448, 447, 452 });
							instrumentParty.setString(448, partyID);
							instrumentParty.setChar(447, partyIDSource.charAt(0));
							instrumentParty.setInt(452, partyRole);
							mdEntry.addGroup(instrumentParty);
						}
					}
					catch (Exception exception) {
						log.error("Bug", exception);
					}
				}

			}

			for (AbstractComponentHandler abstractComponentHandler : outputComponents) {
				abstractComponentHandler.addFIXMessage(new MessageEntry(fixInitiator, fixInitiator, newMessage));
			}
		}

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onCreate(quickfix.SessionID)
	 */
	@Override
	public void onCreate(SessionID arg0) {

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onLogon(quickfix.SessionID)
	 */
	@Override
	public synchronized void onLogon(SessionID channelID) {

		try {

			this.channelID = channelID;

			startLevel = 2;

			fixInitiator.setTransientValues(businessComponentHandler);
			basisPersistenceHandler.send(new UpdateResponse(fixInitiator));
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
			logEntry2.setMessageText("Connection established.");
			logEntry2.setMessageComponent(fixInitiator.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);

			for (AbstractComponentHandler abstractComponentHandler : outputComponents) {
				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "A");
				MessageEntry messageEntry = new MessageEntry(fixInitiator, fixInitiator, message);
				abstractComponentHandler.addFIXMessage(messageEntry);
			}

			if (fixInitiator.getSecurityListRequest() != null && fixInitiator.getSecurityListRequest() == true) {

				Message securityListRequest = new Message();
				securityListRequest.getHeader().setString(MsgType.FIELD, "x");
				String securityReqID = decimalFormat.format(basisPersistenceHandler.getId("SECURITYREQID"));
				securityListRequest.setString(320, securityReqID);
				securityListRequest.setInt(559, 4);

				Session.lookupSession(channelID).send(securityListRequest);
			}

			if (fixInitiator.getRequestType() == 0)
				return;

			List<Character> mdEntryTypes = new ArrayList<Character>();

			if (major == 4) {
				if (minor == 2) {

					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

				}
				if (minor == 3) {
					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

				}
				if (minor == 4) {
					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

					if (fixInitiator.getTradeVolume())
						mdEntryTypes.add('B');

					if (fixInitiator.getOpenInterest())
						mdEntryTypes.add('C');

				}
			}
			if (major == 5) {
				if (servicepack == 0) {
					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

					if (fixInitiator.getTradeVolume())
						mdEntryTypes.add('B');

					if (fixInitiator.getOpenInterest())
						mdEntryTypes.add('C');

					if (fixInitiator.getCompositeUnderlying())
						mdEntryTypes.add('D');

					if (fixInitiator.getSimulatedSellPrice())
						mdEntryTypes.add('E');

					if (fixInitiator.getSimulatedBuyPrice())
						mdEntryTypes.add('F');

					if (fixInitiator.getMarginRate())
						mdEntryTypes.add('G');

					if (fixInitiator.getMidPrice())
						mdEntryTypes.add('H');

					if (fixInitiator.getSettleHighPrice())
						mdEntryTypes.add('K');

					if (fixInitiator.getSettleLowPrice())
						mdEntryTypes.add('L');

					if (fixInitiator.getPriorSettlePrice())
						mdEntryTypes.add('M');

					if (fixInitiator.getSessionHighBid())
						mdEntryTypes.add('N');

					if (fixInitiator.getSessionLowOffer())
						mdEntryTypes.add('O');

					if (fixInitiator.getEarlyPrices())
						mdEntryTypes.add('P');

					if (fixInitiator.getAuctionClearingPrice())
						mdEntryTypes.add('Q');
				}
				if (servicepack == 1) {
					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

					if (fixInitiator.getTradeVolume())
						mdEntryTypes.add('B');

					if (fixInitiator.getOpenInterest())
						mdEntryTypes.add('C');

					if (fixInitiator.getCompositeUnderlying())
						mdEntryTypes.add('D');

					if (fixInitiator.getSimulatedSellPrice())
						mdEntryTypes.add('E');

					if (fixInitiator.getSimulatedBuyPrice())
						mdEntryTypes.add('F');

					if (fixInitiator.getMarginRate())
						mdEntryTypes.add('G');

					if (fixInitiator.getMidPrice())
						mdEntryTypes.add('H');

					if (fixInitiator.getSettleHighPrice())
						mdEntryTypes.add('K');

					if (fixInitiator.getSettleLowPrice())
						mdEntryTypes.add('L');

					if (fixInitiator.getPriorSettlePrice())
						mdEntryTypes.add('M');

					if (fixInitiator.getSessionHighBid())
						mdEntryTypes.add('N');

					if (fixInitiator.getSessionLowOffer())
						mdEntryTypes.add('O');

					if (fixInitiator.getEarlyPrices())
						mdEntryTypes.add('P');

					if (fixInitiator.getAuctionClearingPrice())
						mdEntryTypes.add('Q');

					if (fixInitiator.getSwapValueFactor())
						mdEntryTypes.add('R');

					if (fixInitiator.getDailyValueAdjustmentForLong())
						mdEntryTypes.add('S');

					if (fixInitiator.getCumulativeValueAdjustmentForLong())
						mdEntryTypes.add('T');

					if (fixInitiator.getDailyValueAdjustmentForShort())
						mdEntryTypes.add('U');

					if (fixInitiator.getCumulativeValueAdjustmentForShort())
						mdEntryTypes.add('V');
				}
				if (servicepack == 2) {
					if (fixInitiator.getBid())
						mdEntryTypes.add('0');

					if (fixInitiator.getOffer())
						mdEntryTypes.add('1');

					if (fixInitiator.getTrade())
						mdEntryTypes.add('2');

					if (fixInitiator.getIndexValue())
						mdEntryTypes.add('3');

					if (fixInitiator.getOpeningPrice())
						mdEntryTypes.add('4');

					if (fixInitiator.getClosingPrice())
						mdEntryTypes.add('5');

					if (fixInitiator.getSettlementPrice())
						mdEntryTypes.add('6');

					if (fixInitiator.getTradingSessionHighPrice())
						mdEntryTypes.add('7');

					if (fixInitiator.getTradingSessionLowPrice())
						mdEntryTypes.add('8');

					if (fixInitiator.getTradingSessionVWAPPrice())
						mdEntryTypes.add('9');

					if (fixInitiator.getTradeVolume())
						mdEntryTypes.add('B');

					if (fixInitiator.getOpenInterest())
						mdEntryTypes.add('C');

					if (fixInitiator.getCompositeUnderlying())
						mdEntryTypes.add('D');

					if (fixInitiator.getSimulatedSellPrice())
						mdEntryTypes.add('E');

					if (fixInitiator.getSimulatedBuyPrice())
						mdEntryTypes.add('F');

					if (fixInitiator.getMarginRate())
						mdEntryTypes.add('G');

					if (fixInitiator.getMidPrice())
						mdEntryTypes.add('H');

					if (fixInitiator.getSettleHighPrice())
						mdEntryTypes.add('K');

					if (fixInitiator.getSettleLowPrice())
						mdEntryTypes.add('L');

					if (fixInitiator.getPriorSettlePrice())
						mdEntryTypes.add('M');

					if (fixInitiator.getSessionHighBid())
						mdEntryTypes.add('N');

					if (fixInitiator.getSessionLowOffer())
						mdEntryTypes.add('O');

					if (fixInitiator.getEarlyPrices())
						mdEntryTypes.add('P');

					if (fixInitiator.getAuctionClearingPrice())
						mdEntryTypes.add('Q');

					if (fixInitiator.getSwapValueFactor())
						mdEntryTypes.add('R');

					if (fixInitiator.getDailyValueAdjustmentForLong())
						mdEntryTypes.add('S');

					if (fixInitiator.getCumulativeValueAdjustmentForLong())
						mdEntryTypes.add('T');

					if (fixInitiator.getDailyValueAdjustmentForShort())
						mdEntryTypes.add('U');

					if (fixInitiator.getCumulativeValueAdjustmentForShort())
						mdEntryTypes.add('V');

					if (fixInitiator.getFixingPrice())
						mdEntryTypes.add('W');

					if (fixInitiator.getCashRate())
						mdEntryTypes.add('X');

					if (fixInitiator.getRecoveryRate())
						mdEntryTypes.add('Y');

					if (fixInitiator.getRecoveryRateForLong())
						mdEntryTypes.add('Z');

					if (fixInitiator.getRecoveryRateForShort())
						mdEntryTypes.add('a');
				}

			}

			if (((major == 4 && minor > 1) || major == 5) && mdEntryTypes.size() > 0) {
				try {
					Message message = new Message();
					message.getHeader().setString(MsgType.FIELD, "V");
					message.setInt(262, (int) basisPersistenceHandler.getId("MDREQID"));
					if (fixInitiator.getRequestType() == 1)
						message.setChar(263, '0');
					else
						message.setChar(263, '1');
					if (fixInitiator.getRequestType() == 2)
						message.setInt(265, 0);
					if (fixInitiator.getRequestType() == 3)
						message.setInt(265, 1);
					message.setInt(264, 0);
					for (Character mdEntryType : mdEntryTypes) {
						final Group group = new Group(267, 269, new int[] { 269 });
						group.setChar(269, mdEntryType);
						message.addGroup(group);
					}

					Map<FSecurity, Set<String>> instruments = new HashMap<FSecurity, Set<String>>();
					for (AssignedInitiatorSecurity assignedInitiatorSecurity : fixInitiator.getAssignedInitiatorSecurities()) {
						Set<String> counterparties = instruments.get(assignedInitiatorSecurity.getSecurity());
						if (counterparties == null) {
							counterparties = new HashSet<String>();
							instruments.put(assignedInitiatorSecurity.getSecurity(), counterparties);
						}
						if (assignedInitiatorSecurity.getCounterparty() != null) {
							counterparties.add(assignedInitiatorSecurity.getCounterparty().getName());
						}
					}
					for (FSecurity instrument : instruments.keySet()) {
						final Group group = new Group(146, 55, new int[] { 55, 48, 22, 1018 });
						group.setString(55, "N/A");
						String securityID = instrument.getSecurityID();
						if (fixInitiator.getSecurityIDSource() != null)
							securityID = securityDictionary.getAlternativeSecurityID(securityID, fixInitiator.getSecurityIDSource());
						if (securityID != null) {
							group.setString(48, securityID);
							if (instrument.getSecurityDetails().getSecurityIDSource() != null
									&& instrument.getSecurityDetails().getSecurityIDSource().trim().length() > 0) {
								group.setString(22, instrument.getSecurityDetails().getSecurityIDSource());
							}
							if (major > 4) {
								for (String counterparty : instruments.get(instrument)) {
									final Group group2 = new Group(1018, 1019, new int[] { 1019 });
									group2.setString(1019, counterparty);
									group.addGroup(group2);
								}
							}
							message.addGroup(group);
						}
					}

					Session.lookupSession(channelID).send(message);

				}
				catch (Exception e) {
					log.error("Bug", e);

					LogEntry logEntry = new LogEntry();
					logEntry.setLogDate(new Date());
					logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
					logEntry.setMessageText(e.getMessage());
					logEntry.setMessageComponent(fixInitiator.getName());
					basisPersistenceHandler.writeLogEntry(logEntry);
				}
			}

		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onLogout(quickfix.SessionID)
	 */
	@Override
	public synchronized void onLogout(SessionID channelID) {

		this.channelID = null;

		if (startLevel == 3 || startLevel == 0)
			startLevel = 0;
		else
			startLevel = 1;

		fixInitiator.setTransientValues(businessComponentHandler);
		basisPersistenceHandler.send(new UpdateResponse(fixInitiator));
		LogEntry logEntry2 = new LogEntry();
		logEntry2.setLogDate(new Date());
		logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
		logEntry2.setMessageText("Interface disconnected.");
		logEntry2.setMessageComponent(fixInitiator.getName());
		basisPersistenceHandler.writeLogEntry(logEntry2);

		for (AbstractComponentHandler abstractComponentHandler : outputComponents) {
			Message message = new Message();
			message.getHeader().setString(MsgType.FIELD, "5");
			MessageEntry messageEntry = new MessageEntry(fixInitiator, fixInitiator, message);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}
	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(Message arg0, SessionID arg1) {

		if (major > 4 && appVerID != null)
			arg0.getHeader().setString(1128, appVerID);

		try {
			String messageType = arg0.getHeader().getString(MsgType.FIELD);
			if (messageType.equals("4"))
				onLogon(arg1);
			else if (messageType.equals("3")) 
				logRejectMessage(arg0, false);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}
	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toApp(Message arg0, SessionID arg1) throws DoNotSend {

		if (major > 4 && appVerID != null)
			arg0.getHeader().setString(1128, appVerID);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getAbstractBusinessComponent()
	 */
	@Override
	public AbstractBusinessComponent getAbstractBusinessComponent() {

		return fixInitiator;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	public synchronized void processFIXMessage(List<MessageEntry> messages) {

		if (channelID != null)
			for (MessageEntry message : messages)
				try {
					Session.lookupSession(channelID).send(message.getMessage());
				}
				catch (Exception e) {
					log.error("Bug", e);
				}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getTargetCompIDIfOnline(net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty)
	 */
	@Override
	public String getTargetCompIDIfOnline(Counterparty counterparty) {

		if (channelID != null)
			return channelID.getTargetCompID();
		return null;
	}
	
	private void logRejectMessage(Message message, boolean sent) throws FieldNotFound
	{
		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
		StringBuffer stringBuffer = new StringBuffer("System ");
		if(sent)
			stringBuffer.append("sent");
		else
			stringBuffer.append("received");
		stringBuffer.append(" FIX reject (3) message. Please contact your administrator. ");
		if (message.isSetField(373)) {
			stringBuffer.append("Session reject reason: ");

			switch (message.getInt(373)) {
				case 0:
					stringBuffer.append("Invalid Tag Number.");
					break;
				case 1:
					stringBuffer.append("Required Tag Missing.");
					break;
				case 2:
					stringBuffer.append("Tag not defined for this message type.");
					break;
				case 3:
					stringBuffer.append("Undefined tag.");
					break;
				case 4:
					stringBuffer.append("Tag specified without a value.");
					break;
				case 5:
					stringBuffer.append("Value is incorrect (out of range) for this tag.");
					break;
				case 6:
					stringBuffer.append("Incorrect data format for value.");
					break;
				case 7:
					stringBuffer.append("Decryption problem.");
					break;
				case 8:
					stringBuffer.append("Signature problem.");
					break;
				case 9:
					stringBuffer.append("CompID problem.");
					break;
				case 10:
					stringBuffer.append("SendingTime Accuracy Problem.");
					break;
				case 11:
					stringBuffer.append("Invalid MsgType.");
					break;
				case 12:
					stringBuffer.append("XML Validation Error.");
					break;
				case 13:
					stringBuffer.append("Tag appears more than once.");
					break;
				case 14:
					stringBuffer.append("Tag specified out of required order.");
					break;
				case 15:
					stringBuffer.append("Repeating group fields out of order.");
					break;
				case 16:
					stringBuffer.append("Incorrect NumInGroup count for repeating group.");
					break;
				case 17:
					stringBuffer.append("Non Data value includes field delimiter (<SOH> character).");
					break;
				case 18:
					stringBuffer.append("Invalid/Unsupported Application Version.");
					break;
				case 99:
					stringBuffer.append("Other.");
					break;
			}

			stringBuffer.append(" ");
		}
		if (message.isSetField(371)) {
			stringBuffer.append("Reference tag ID: ");
			stringBuffer.append(message.getString(371));
			stringBuffer.append(" ");
		}
		if (message.isSetField(372)) {
			stringBuffer.append("Reference message type: ");
			stringBuffer.append(message.getString(372));
			stringBuffer.append(" ");
		}
		if (message.isSetField(58)) {
			stringBuffer.append("Text: ");
			stringBuffer.append(message.getString(58));
			stringBuffer.append(" ");
		}
		logEntry.setMessageText(stringBuffer.toString());
		logEntry.setMessageComponent(fixInitiator.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);
	}

}
