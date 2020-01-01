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

import net.sourceforge.fixagora.basis.server.control.component.MarketDataManager.ManagerType;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptorTarget;
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
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

/**
 * The Class FIXAcceptorComponentHandler.
 */
public class FIXAcceptorComponentHandler extends AbstractComponentHandler implements Application {

	private FIXAcceptor fixAcceptor = null;

	private quickfix.SocketAcceptor acceptor = null;

	private DataDictionary dataDictionary = null;

	private MarketDataManager marketDataManager = null;

	private Set<SessionID> sessionIDs = new HashSet<SessionID>();

	private int startLevel = 0;

	private String appVerID = null;

	private DecimalFormat decimalFormat = new DecimalFormat("####");

	private Map<FSecurity, AbstractComponentHandler> tradeableSecurities = new HashMap<FSecurity, AbstractComponentHandler>();

	private Set<String> openSecurityListRequests = new HashSet<String>();

	private Set<PendingSecurityListRequest> pendingExternalSecurityListRequests = new HashSet<PendingSecurityListRequest>();

	private int major = 0;

	private int minor = 0;

	private int servicepack = 0;

	private static Logger log = Logger.getLogger(FIXAcceptorComponentHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof FIXAcceptor) {
			fixAcceptor = (FIXAcceptor) abstractBusinessComponent;
			if (marketDataManager == null)
				marketDataManager = new MarketDataManager();
			switch (fixAcceptor.getMarketDataType()) {
				case UNSUBSCRIBED_FULL_REFRESH:
					marketDataManager.setManagerType(ManagerType.UNSUBSCRIBED_FULL_REFRESH);
					break;
				case UNSUBSCRIBED_INCREMENTAL_REFRESH:
					marketDataManager.setManagerType(ManagerType.UNSUBSCRIBED_INCREMENTAL_REFRESH);
					break;
				default:
					marketDataManager.setManagerType(ManagerType.MARKET_DATA_REQUEST);
					break;
			}

			marketDataManager.setSecurityDictionary(securityDictionary);
			marketDataManager.setSecurityIDSource(fixAcceptor.getSecurityIDSource());
			marketDataManager.setPartyID(fixAcceptor.getPartyID());
			marketDataManager.setPartyIDSource(fixAcceptor.getPartyIDSource());
			marketDataManager.setPartyRole(fixAcceptor.getPartyRole());
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#start(org.jboss.netty.channel.Channel)
	 */
	@Override
	public synchronized void start(Channel channel) {

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
		logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is starting FIX acceptor.");
		logEntry.setMessageComponent(fixAcceptor.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);

		Thread thread = new Thread() {

			public void run() {

				FIXAcceptorComponentHandler.this.start();
			}
		};
		thread.start();

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
		logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is stopping FIX acceptor.");
		logEntry.setMessageComponent(fixAcceptor.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);

		startLevel = 3;
		fixAcceptor.setTransientValues(businessComponentHandler);

		basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

		Thread thread = new Thread() {

			public void run() {

				stopAcceptor(channel);

			}
		};
		thread.start();

	}

	private synchronized void stopAcceptor(Channel channel) {

		if (acceptor != null) {
			acceptor.stop();
			acceptor = null;
		}

		startLevel = 0;
		major = 0;
		marketDataManager.setMajor(major);
		minor = 0;
		servicepack = 0;
		fixAcceptor.setTransientValues(businessComponentHandler);

		basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));
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

		else if (messageType.equals("A")) {
			Message newMessage = (Message) arg0.clone();
			for (AbstractComponentHandler abstractComponentHandler : inputComponents)
				abstractComponentHandler.addFIXMessage(new MessageEntry(fixAcceptor, fixAcceptor, newMessage));
		}
	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public synchronized void fromApp(Message message, SessionID channelID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

		Message newMessage = (Message) message.clone();
		
		try {
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
				logEntry.setMessageComponent(fixAcceptor.getName());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
			else if (messageType.equals("V")) {
				List<Message> messages = marketDataManager.processMarketDataRequest(newMessage, channelID);
				for (Message message2 : messages) {
					if (fixAcceptor.getSecurityIDSource() != null) {
						if (message.isSetField(48)) {
							String securityID = securityDictionary.getAlternativeSecurityID(message.getString(48), fixAcceptor.getSecurityIDSource());
							if (securityID != null)
								message2.setString(48, securityID);
							message2.setString(22, fixAcceptor.getSecurityIDSource());
						}
					}

					Session.lookupSession(channelID).send(message2);
				}
			}
			else if (messageType.equals("D")) {
				String securityId = null;
				String securityIdSource = null;
				if (message.isSetField(48))
					securityId = message.getString(48);
				if (message.isSetField(22))
					securityIdSource = message.getString(22);

				FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);
				if (security != null) {
					AbstractComponentHandler abstractComponentHandler = tradeableSecurities.get(security);
					if (abstractComponentHandler != null)
						abstractComponentHandler.addFIXMessage(new MessageEntry(fixAcceptor, fixAcceptor, newMessage));
					else
						handleNewOrderSingle(message, channelID);
				}
				else
					handleNewOrderSingle(message, channelID);
			}
			else if (messageType.equals("x")) {
				if (openSecurityListRequests.size() > 0)
					pendingExternalSecurityListRequests.add(new PendingSecurityListRequest(message, channelID));
				else
					handleSecurityListRequest(message, channelID);
			}
			else
				for (AbstractComponentHandler abstractComponentHandler : inputComponents)
					abstractComponentHandler.addFIXMessage(new MessageEntry(fixAcceptor, fixAcceptor, newMessage));
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	private void handleNewOrderSingle(Message message, SessionID channelID) {

		try {
			Message executionReport = new Message();
			executionReport.getHeader().setString(MsgType.FIELD, "8");
			executionReport.setString(37, decimalFormat.format(basisPersistenceHandler.getId("ORDERID")));
			executionReport.setString(17, decimalFormat.format(basisPersistenceHandler.getId("EXECID")));
			executionReport.setString(11, message.getString(11));
			executionReport.setChar(150, '8');
			executionReport.setChar(39, '8');
			executionReport.setChar(54, message.getChar(54));
			executionReport.setDouble(14, 0);
			executionReport.setDouble(151, 0);
			executionReport.setDouble(38, message.getDouble(38));
			if (message.isSetField(44))
				executionReport.setDouble(44, message.getDouble(44));
			if (message.isSetField(110))
				executionReport.setDouble(110, message.getDouble(110));
			for (int i = 1; i <= message.getGroupCount(453); i++) {
				try {
					Group parties = message.getGroup(i, 453);
					final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
					group.setString(448, parties.getString(448));
					if (parties.isSetField(447))
						group.setChar(447, parties.getChar(447));
					if (parties.isSetField(452))
						group.setInt(452, parties.getInt(452));
					executionReport.addGroup(group);
				}
				catch (Exception e) {
					log.error("Bug", e);
				}
			}
			executionReport.setString(55, "N/A");
			executionReport.setString(48, message.getString(48));
			executionReport.setString(22, message.getString(22));
			if (message.isSetField(64)) {
				executionReport.setString(64, message.getString(64));
			}
			Session channel = Session.lookupSession(channelID);
			if (channel != null)
				channel.send(executionReport);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
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
	public synchronized void onLogon(SessionID sessionID) {

		try {
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
			logEntry2.setMessageText(sessionID.getTargetCompID() + " connected.");
			logEntry2.setMessageComponent(fixAcceptor.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			sessionIDs.add(sessionID);

			fixAcceptor.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

			List<Message> messages = marketDataManager.addSession(sessionID);

			for (Message message : messages) {
				Session.lookupSession(sessionID).send(message);

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
	public synchronized void onLogout(SessionID sessionID) {

		LogEntry logEntry2 = new LogEntry();
		logEntry2.setLogDate(new Date());
		logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
		logEntry2.setMessageText(sessionID.getTargetCompID() + " disconnected.");
		logEntry2.setMessageComponent(fixAcceptor.getName());
		basisPersistenceHandler.writeLogEntry(logEntry2);

		marketDataManager.removeSession(sessionID);

		sessionIDs.remove(sessionID);

		fixAcceptor.setTransientValues(businessComponentHandler);

		basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
		
		if (major>4&&appVerID != null)
			arg0.getHeader().setString(1128, appVerID);

		try {
			String messageType = arg0.getHeader().getString(MsgType.FIELD);
			if (messageType.equals("4")) {
				onLogout(arg1);
				onLogon(arg1);
			}
			else if (messageType.equals("3")) {
				logRejectMessage(arg0, true);
			}
			
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
		
		if (major>4&&appVerID != null)
			arg0.getHeader().setString(1128, appVerID);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getAbstractBusinessComponent()
	 */
	@Override
	public AbstractBusinessComponent getAbstractBusinessComponent() {

		return fixAcceptor;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	protected synchronized void processFIXMessage(List<MessageEntry> messages) {

		List<Message> marketDataIncrementalRefresh = new ArrayList<Message>();

		for (MessageEntry messageEntry : messages) {
			try {
				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);
				if (messageType.equals("X")) {
					marketDataIncrementalRefresh.add(message);
				}
				else if (messageType.equals("y")) {
					handleSecurityList(message, messageEntry.getInitialComponent());
				}
				else {
					if (message.getHeader().isSetField(56)) {
						for (SessionID channelID : sessionIDs) {
							if (channelID.getTargetCompID().equals(message.getHeader().getString(56))) {
								Session channel = Session.lookupSession(channelID);
								if (channel != null) {
									channel.send(message);

								}
							}

						}
					}
				}
			}
			catch (FieldNotFound e) {
				log.error("Bug", e);
			}
		}

		marketDataManager.addIncrementalRefresh(marketDataIncrementalRefresh);

		for (SessionID channelID : sessionIDs) {
			List<Message> messages2 = marketDataManager.getMessages(marketDataIncrementalRefresh, channelID);
			for (Message message : messages2) {
				Session channel = Session.lookupSession(channelID);
				if (channel != null) {
					channel.send(message);

				}
			}
		}

	}

	private void handleSecurityList(Message message, AbstractBusinessComponent initialComponent) {

		try {
			String securityReqID = message.getString(320);
			openSecurityListRequests.remove(securityReqID);

			for (int i = 1; i <= message.getGroupCount(146); i++) {

				final Group group = message.getGroup(i, 146);
				if (group.isSetField(48)) {
					String securityID = group.getString(48).trim();
					if (group.isSetField(22)) {
						String securityIDSource = group.getString(22);
						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID, securityIDSource);
						if (security != null) {
							AbstractComponentHandler abstractComponentHandler = tradeableSecurities.get(security);
							if (abstractComponentHandler != null && abstractComponentHandler.getAbstractBusinessComponent().getId() != initialComponent.getId()) {
								LogEntry logEntry2 = new LogEntry();
								logEntry2.setLogDate(new Date());
								logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
								logEntry2.setMessageText(security.getName() + " is assigned to multiple books that are connected to the same acceptor "
										+ getAbstractBusinessComponent().getName() + ".");
								logEntry2.setMessageComponent(fixAcceptor.getName());
								basisPersistenceHandler.writeLogEntry(logEntry2);
							}
							else {
								tradeableSecurities.put(security, businessComponentHandler.getBusinessComponentHandler(initialComponent.getId()));
							}
						}
					}
				}
			}

			if (openSecurityListRequests.size() == 0) {
				for (PendingSecurityListRequest pendingSecurityListRequest : pendingExternalSecurityListRequests)
					handleSecurityListRequest(pendingSecurityListRequest.getMessage(), pendingSecurityListRequest.getSessionID());

				pendingExternalSecurityListRequests.clear();
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	private void handleSecurityListRequest(Message message2, SessionID channelID) {

		try {
			Message securityList = new Message();
			securityList.getHeader().setString(MsgType.FIELD, "y");
			securityList.setString(320, message2.getString(320));
			securityList.setString(322, message2.getString(320));
			securityList.setInt(560, 0);

			for (FSecurity instrument : tradeableSecurities.keySet()) {
				Group relatedSym = securityDictionary.getRelatedSym(instrument, major, minor, servicepack);
				securityList.addGroup(relatedSym);
			}

			Session channel = Session.lookupSession(channelID);
			if (channel != null)
				channel.send(securityList);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getTargetCompIDIfOnline(net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty)
	 */
	public String getTargetCompIDIfOnline(Counterparty counterparty) {

		for (FIXAcceptorTarget fixAcceptorTarget : fixAcceptor.getAcceptorTargets())
			if (fixAcceptorTarget.getCounterparty().getId() == counterparty.getId())
				for (SessionID channelID : sessionIDs)
					if (channelID.getTargetCompID().equals(fixAcceptorTarget.getTargetCompID()))
						return channelID.getTargetCompID();
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#startProcessor()
	 */
	@Override
	protected void startProcessor() {

		if (fixAcceptor.getConnectAtStartup())
			start();
		super.startProcessor();
	}

	private synchronized void start() {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {
			Message securityListRequest = new Message();
			securityListRequest.getHeader().setString(MsgType.FIELD, "x");
			String securityReqID = decimalFormat.format(basisPersistenceHandler.getId("SECURITYREQID"));
			openSecurityListRequests.add(securityReqID);
			securityListRequest.setString(320, securityReqID);
			securityListRequest.setInt(559, 4);
			abstractComponentHandler.addFIXMessage(new MessageEntry(fixAcceptor, fixAcceptor, securityListRequest));
		}

		if (startLevel != 0)
			return;

		final StringBuffer stringBuffer = new StringBuffer("[DEFAULT]\n");
		stringBuffer.append("FileStorePath=");
		stringBuffer.append("./log/fix/" + fixAcceptor.getName());
		stringBuffer.append("\nFileLogPath=");
		stringBuffer.append("./log/fix/" + fixAcceptor.getName());

		for (DataDictionary dataDictionary : dataDictionaries)
			if (dataDictionary.getName().trim().equals(fixAcceptor.getDataDictionary().trim()))
				this.dataDictionary = dataDictionary;

		if (dataDictionary == null) {
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText("Data dictionary " + fixAcceptor.getDataDictionary() + " not found on server.");
			logEntry2.setMessageComponent(fixAcceptor.getName());
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

					final String majorString = xmlFieldValue.getAttribute("major");
					defaultApplVerID.append(majorString);

					major = Integer.parseInt(majorString);
					
					marketDataManager.setMajor(major);

					defaultApplVerID.append(".");

					final String minorString = xmlFieldValue.getAttribute("minor");
					defaultApplVerID.append(minorString);

					minor = Integer.parseInt(minorString);

					if (xmlFieldValue.getAttribute("servicepack").length() > 0) {

						final String servicepackString = xmlFieldValue.getAttribute("servicepack");
						servicepack = Integer.parseInt(servicepackString);
						if (servicepack > 0) {

							defaultApplVerID.append("SP");
							defaultApplVerID.append(servicepackString);

						}

					}

					if (major > 4)
						beginString = "FIXT.1.1";
					else
						beginString = "FIX." + xmlFieldValue.getAttribute("major").trim() + "." + xmlFieldValue.getAttribute("minor").trim();
				}
		}
		catch (Exception e1) {
			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText("Data dictionary " + fixAcceptor.getDataDictionary() + " is not valid.");
			logEntry2.setMessageComponent(fixAcceptor.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			return;
		}

		if (defaultApplVerID.toString().equals("FIX.5.0"))
			appVerID = "7";

		if (defaultApplVerID.toString().equals("FIX.5.0SP1"))
			appVerID = "8";

		if (defaultApplVerID.toString().equals("FIX.5.0SP2"))
			appVerID = "9";

		if (beginString.startsWith("FIXT")) {

			stringBuffer.append("\nAppDataDictionary=./conf/");
			stringBuffer.append(fixAcceptor.getDataDictionary());
			stringBuffer.append("\nTransportDataDictionary=./conf/FIXT11.xml");
		}
		else {
			stringBuffer.append("\nDataDictionary=./conf/");
			stringBuffer.append(fixAcceptor.getDataDictionary());
		}

		stringBuffer.append("\nBeginString=");
		stringBuffer.append(beginString);
		stringBuffer.append("\nConnectionType=acceptor");
		stringBuffer.append("\nStartTime=00:00:00\n");
		stringBuffer.append("EndTime=00:00:00\n");

		stringBuffer.append("\nSocketAcceptAddress=");
		stringBuffer.append(fixAcceptor.getSocketAdress());
		stringBuffer.append("\nSocketAcceptPort=");
		stringBuffer.append(fixAcceptor.getSocketPort());
		stringBuffer.append("\nResetOnLogon=");
		stringBuffer.append("N");
		stringBuffer.append("\nCheckLatency=Y");
		stringBuffer.append("\nPersistMessages=");
		if (fixAcceptor.getPersistMessage())
			stringBuffer.append("Y");
		else
			stringBuffer.append("N");
		stringBuffer.append("\nValidateFieldsOutOfOrder=Y");
		stringBuffer.append("\nValidateFieldsHaveValues=Y");
		stringBuffer.append("\nValidateUserDefinedFields=Y");

		for (FIXAcceptorTarget fixAcceptorTarget : fixAcceptor.getAcceptorTargets()) {
			stringBuffer.append("\n[session]\n");
			stringBuffer.append("\nRefreshOnLogon=Y\n");
			stringBuffer.append("SenderCompID=");
			stringBuffer.append(fixAcceptor.getSenderCompID());
			stringBuffer.append("\nTargetCompID=");
			stringBuffer.append(fixAcceptorTarget.getTargetCompID());
			stringBuffer.append("\nReconnectInterval=5");
			stringBuffer.append("\nValidateIncomingMessage=Y");
			stringBuffer.append("\n");
			if (beginString.startsWith("FIXT")) {
				stringBuffer.append("\nDefaultApplVerID=");
				stringBuffer.append(defaultApplVerID.toString());
			}
		}

		try {

			startLevel = 1;
			fixAcceptor.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

			final InputStream inputStream = new ByteArrayInputStream(stringBuffer.toString().getBytes("UTF-8"));
			final SessionSettings settings = new SessionSettings(inputStream);
			final MessageStoreFactory storeFactory = new FileStoreFactory(settings);

			quickfix.LogFactory logFactory = new EmptyLogFactory();
			if (fixAcceptor.getPersistMessage())
				logFactory = new FileLogFactory(settings);
			final quickfix.MessageFactory messageFactory = new DefaultMessageFactory();

			acceptor = new quickfix.SocketAcceptor(new DefaultSessionFactory(this, storeFactory, logFactory, messageFactory), settings);
			acceptor.start();
			
			fixAcceptor.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

			startLevel = 2;
			fixAcceptor.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

		}
		catch (final Exception e) {

			log.error("Bug", e);

			startLevel = 0;
			fixAcceptor.setTransientValues(businessComponentHandler);

			basisPersistenceHandler.send(new UpdateResponse(fixAcceptor));

			LogEntry logEntry2 = new LogEntry();
			logEntry2.setLogDate(new Date());
			logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
			logEntry2.setMessageText(e.getMessage());
			logEntry2.setMessageComponent(fixAcceptor.getName());
			basisPersistenceHandler.writeLogEntry(logEntry2);
			return;
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getStartLevel()
	 */
	@Override
	public int getStartLevel() {

		return startLevel;
	}

	/**
	 * Gets the open sessions.
	 *
	 * @return the open sessions
	 */
	public Set<String> getOpenSessions() {

		Set<String> openSessions = new HashSet<String>();
		for (SessionID channelID : sessionIDs)
			openSessions.add(channelID.getTargetCompID());
		return openSessions;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

		// TODO Auto-generated method stub

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
		logEntry.setMessageComponent(fixAcceptor.getName());
		basisPersistenceHandler.writeLogEntry(logEntry);
	}

}
