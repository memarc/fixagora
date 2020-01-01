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
package net.sourceforge.fixagora.sap.server.control.component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.MessageEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.sap.shared.communication.CloseSAPTradeCaptureRequest;
import net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureRequest;
import net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest.SAPTradeCaptureAction;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse.ActionResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.Side;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCapture;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCaptureReport;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCaptureReport.ExportStatus;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class SAPTradeCaptureComponentHandler.
 */
public class SAPTradeCaptureComponentHandler extends AbstractComponentHandler {

	private SAPTradeCapture sapTradeCapture = null;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0##############");

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private DecimalFormat decimalFormat3 = new DecimalFormat("####");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<Channel> tradeCaptureSessions = new HashSet<Channel>();

	private boolean actionInProgress = false;

	private SAPFIXAgoraConnector sapConnector = null;

	private static Logger log = Logger.getLogger(SAPTradeCaptureComponentHandler.class);

	/**
	 * Instantiates a new sAP trade capture component handler.
	 */
	public SAPTradeCaptureComponentHandler() {

		super();

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#startProcessor()
	 */
	@Override
	protected void startProcessor() {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {

			Message tradeCaptureReportRequest = new Message();
			tradeCaptureReportRequest.getHeader().setString(MsgType.FIELD, "AD");
			tradeCaptureReportRequest.setString(568, decimalFormat3.format(basisPersistenceHandler.getId("TRADEREQUESTID")));
			tradeCaptureReportRequest.setInt(569, 0);
			MessageEntry messageEntry = new MessageEntry(sapTradeCapture, sapTradeCapture, tradeCaptureReportRequest);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}

		try {
			Class.forName("com.sap.mw.jco.JCO", false, ClassLoader.getSystemClassLoader());
			sapConnector = new SAPFIXAgoraConnector(sapTradeCapture, basisPersistenceHandler, businessComponentHandler);
		}
		catch (Exception e) {
			log.error("SAP Java Connector is missing. Please contact your administrator. The export function is disabled.");
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sapTradeCapture.getName());
			logEntry.setMessageText("SAP\u00ae Java Connector is missing. Please contact your administrator.");
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

		super.startProcessor();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof SAPTradeCapture) {
			sapTradeCapture = (SAPTradeCapture) abstractBusinessComponent;
		}

	}

	private SAPTradeCaptureEntry getSAPTradeCaptureEntry(SAPTradeCaptureReport sapTradeCaptureReport) {

		SAPTradeCaptureEntry sapTradeCaptureEntry = new SAPTradeCaptureEntry();
		sapTradeCaptureEntry.setTradeCapture(sapTradeCaptureReport.getSAPTradeCapture().getId());
		sapTradeCaptureEntry.setCounterparty(sapTradeCaptureReport.getCounterparty().getId());
		sapTradeCaptureEntry.setCounterpartyOrderId(sapTradeCaptureReport.getCounterpartyOrderId());
		sapTradeCaptureEntry.setCreated(sapTradeCaptureReport.getCreatedTimestamp());
		sapTradeCaptureEntry.setCumulativeQuantity(sapTradeCaptureReport.getCumulativeQuantity());
		sapTradeCaptureEntry.setLastPrice(sapTradeCaptureReport.getLastPrice());
		sapTradeCaptureEntry.setLastQuantity(sapTradeCaptureReport.getLastQuantity());
		sapTradeCaptureEntry.setLeaveQuantity(sapTradeCaptureReport.getLeaveQuantity());
		sapTradeCaptureEntry.setSourceComponent(sapTradeCaptureReport.getSourceComponent().getId());
		sapTradeCaptureEntry.setId(sapTradeCaptureReport.getId());
		sapTradeCaptureEntry.setOrderId(sapTradeCaptureReport.getOrderId());
		sapTradeCaptureEntry.setOrderQuantity(sapTradeCaptureReport.getOrderQuantity());
		sapTradeCaptureEntry.setSettlementDate(sapTradeCaptureReport.getSettlementDate());
		sapTradeCaptureEntry.setMarket(sapTradeCaptureReport.getMarket());
		sapTradeCaptureEntry.setExecId(sapTradeCaptureReport.getExecId());
		sapTradeCaptureEntry.setTradeId(sapTradeCaptureReport.getTradeId());

		sapTradeCaptureEntry.setSecurity(sapTradeCaptureReport.getSecurity().getId());
		sapTradeCaptureEntry.setSide(Side.BUY);
		if (sapTradeCaptureReport.getSide() == SAPTradeCaptureReport.Side.SELL)
			sapTradeCaptureEntry.setSide(Side.SELL);

		sapTradeCaptureEntry.setUpdated(sapTradeCaptureReport.getUpdatedTimestamp());

		if (sapTradeCaptureReport.getTrader() != null)
			sapTradeCaptureEntry.setTrader(sapTradeCaptureReport.getTrader().getId());

		if (sapTradeCaptureReport.getExecutingTrader() != null)
			sapTradeCaptureEntry.setUserID(sapTradeCaptureReport.getExecutingTrader());

		if (sapTradeCaptureReport.getUser() != null)
			sapTradeCaptureEntry.setUser(sapTradeCaptureReport.getUser().getId());

		sapTradeCaptureEntry.setSapFinancialTransaction(sapTradeCaptureReport.getSapFinancialTransaction());

		switch (sapTradeCaptureReport.getExportStatus()) {
			case DONE:
				sapTradeCaptureEntry.setExportStatus(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.DONE);
				break;
			case FAILED:
				sapTradeCaptureEntry.setExportStatus(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.FAILED);
				break;
			case NEW:
				sapTradeCaptureEntry.setExportStatus(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.NEW);
				break;
			case TEST_DONE:
				sapTradeCaptureEntry.setExportStatus(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.TEST_DONE);
				break;
			case TEST_FAILED:
				sapTradeCaptureEntry.setExportStatus(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.TEST_FAILED);
				break;
		}

		return sapTradeCaptureEntry;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getStartLevel()
	 */
	@Override
	public int getStartLevel() {

		return 2;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#start(org.jboss.netty.channel.Channel)
	 */
	@Override
	public void start(Channel channel) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#stop(org.jboss.netty.channel.Channel)
	 */
	@Override
	public synchronized void stop(Channel channel) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getAbstractBusinessComponent()
	 */
	@Override
	public AbstractBusinessComponent getAbstractBusinessComponent() {

		return sapTradeCapture;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	protected synchronized void processFIXMessage(List<MessageEntry> messages) {

		for (MessageEntry messageEntry : messages) {
			try {
				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);
				if (messageType.equals("AE")) {
					handleTradeCaptureReport(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("x")) {
					handleSecurityListRequest(message, messageEntry.getInitialComponent());
				}
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}
	}

	private void handleTradeCaptureReport(Message message, AbstractBusinessComponent businessComponent) {

		SAPTradeCaptureEntry sapTradeCaptureEntry = new SAPTradeCaptureEntry();

		sapTradeCaptureEntry.setSourceComponent(businessComponent.getId());

		String partyId = null;
		String partyIdSource = null;

		String traderId = null;
		String traderIdSource = null;

		String userId = null;
		String userIdSource = null;

		try {

			for (int i = 1; i <= message.getGroupCount(453); i++) {

				Group parties = message.getGroup(i, 453);
				if (parties.isSetField(452) && parties.getInt(452) == 17) {

					if (parties.isSetField(447))
						partyIdSource = parties.getString(447);

					if (parties.isSetField(448))
						partyId = parties.getString(448);
				}
				if (parties.isSetField(452) && parties.getInt(452) == 37) {

					if (parties.isSetField(447))
						traderIdSource = parties.getString(447);

					if (parties.isSetField(448))
						traderId = parties.getString(448);
				}
				if (parties.isSetField(452) && parties.getInt(452) == 12) {

					if (parties.isSetField(447))
						userIdSource = parties.getString(447);

					if (parties.isSetField(448))
						userId = parties.getString(448);
				}
				if (parties.isSetField(452) && parties.getInt(452) == 16) {

					sapTradeCaptureEntry.setMarket(parties.getString(448));
				}
			}

			Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 17);

			if (counterparty != null) {

				sapTradeCaptureEntry.setCounterparty(counterparty.getId());

				if (traderId != null) {

					List<Trader> traders = basisPersistenceHandler.executeQuery(Trader.class, "select t from Trader t where t.parent=?1", counterparty,
							businessComponentHandler, true);
					Trader trader = null;
					for (Trader trader2 : traders) {
						for (TraderPartyID traderPartyID : trader2.getTraderPartyIDs()) {
							if (traderPartyID.getAbstractBusinessComponent() == null) {
								if (trader == null && traderPartyID.getPartyIDSource() != null && traderPartyID.getPartyIDSource().equals(traderIdSource)
										&& traderId.equals(traderPartyID.getPartyID()))
									trader = trader2;
							}
							else if (traderPartyID.getAbstractBusinessComponent().getId() == sapTradeCapture.getId() && traderPartyID.getPartyRole() != null
									&& traderPartyID.getPartyRole().intValue() == 37 && traderPartyID.getPartyIDSource() != null
									&& traderPartyID.getPartyIDSource().equals(traderIdSource) && traderId.equals(traderPartyID.getPartyID()))
								trader = trader2;
						}
					}
					if (trader == null) {
						String traderName = traderId;

						StringBuffer stringBuffer = new StringBuffer("Unknown trader ");
						stringBuffer.append(traderName);
						stringBuffer.append(" for counterparty ");
						stringBuffer.append(counterparty.getName());
						stringBuffer.append(".");

						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(sapTradeCapture.getName());
						logEntry.setMessageText(stringBuffer.toString());
						basisPersistenceHandler.writeLogEntry(logEntry);

						int i = 1;
						String newName = null;
						do {
							if (i == 1)
								newName = traderName;
							else
								newName = traderName + " (" + i + ")";

							i++;

							List<Trader> traders2 = basisPersistenceHandler.executeQuery(Trader.class, "select t from Trader t where t.name=?1", newName,
									businessComponentHandler, true);
							if (traders2.size() > 0)
								newName = null;
						}
						while (newName == null);

						trader = new Trader();
						trader.setName(newName);
						trader.setParent(counterparty);

						List<TraderPartyID> traderPartyIDs = new ArrayList<TraderPartyID>();
						TraderPartyID traderPartyID = new TraderPartyID();
						traderPartyID.setPartyID(traderId);
						traderPartyID.setPartyIDSource(traderIdSource);
						traderPartyID.setPartyRole(37);
						traderPartyID.setTrader(trader);
						traderPartyID.setAbstractBusinessComponent(businessComponent);
						traderPartyIDs.add(traderPartyID);
						trader.setTraderPartyIDs(traderPartyIDs);

						basisPersistenceHandler.persist(trader, null, businessComponentHandler);
					}

					sapTradeCaptureEntry.setTrader(trader.getId());

				}

				FUser user = null;

				if (userId != null) {

					List<FUser> users = basisPersistenceHandler.executeQuery(FUser.class, "select t from FUser t", businessComponentHandler, true);

					for (FUser user2 : users) {
						for (FUserPartyID userPartyID : user2.getfUserPartyIDs()) {
							
							if (userPartyID.getAbstractBusinessComponent() == null) {
								
								if (user == null && userPartyID.getPartyIDSource().equals(userIdSource) && userId.equals(userPartyID.getPartyID()))
									user = user2;
							}
							else if (userPartyID.getAbstractBusinessComponent().getId() == sapTradeCapture.getId() && userPartyID.getPartyRole() != null
									&& userPartyID.getPartyRole().intValue() == 12 && userPartyID.getPartyIDSource() != null
									&& userPartyID.getPartyIDSource().equals(userIdSource) && userId.equals(userPartyID.getPartyID()))
								user = user2;
						}
					}
				}

				if (user != null)
					sapTradeCaptureEntry.setUser(user.getId());
				else
					sapTradeCaptureEntry.setUserID(userId);

				if (message.isSetField(11))
					sapTradeCaptureEntry.setCounterpartyOrderId(message.getString(11));
				if (message.isSetField(38))
					sapTradeCaptureEntry.setOrderQuantity(message.getDouble(38));
				if (message.isSetField(151))
					sapTradeCaptureEntry.setLeaveQuantity(message.getDouble(151));

				sapTradeCaptureEntry.setCreated(System.currentTimeMillis());

				if (message.isSetField(14))
					sapTradeCaptureEntry.setCumulativeQuantity(message.getDouble(14));

				if (message.isSetField(64))
					sapTradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

				if (message.isSetField(32))
					sapTradeCaptureEntry.setLastQuantity(message.getDouble(32));

				if (message.isSetField(37))
					sapTradeCaptureEntry.setOrderId(message.getString(37));

				if (message.isSetField(1003))
					sapTradeCaptureEntry.setTradeId(message.getString(1003));

				if (message.isSetField(17))
					sapTradeCaptureEntry.setExecId(message.getString(17));

				String securityId = null;
				String securityIdSource = null;
				if (message.isSetField(48))
					securityId = message.getString(48);
				if (message.isSetField(22))
					securityIdSource = message.getString(22);

				FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);
				if (security != null) {

					if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

						if (message.isSetField(669))
							sapTradeCaptureEntry.setLastPrice(message.getDouble(669));
						if (message.isSetField(31))
							sapTradeCaptureEntry.setLastYield(message.getDouble(31));
					}
					else if (message.isSetField(31))
						sapTradeCaptureEntry.setLastPrice(message.getDouble(31));

					sapTradeCaptureEntry.setSecurity(security.getId());
					sapTradeCaptureEntry.setTradeCapture(sapTradeCapture.getId());
					if (message.isSetField(54)) {

						if (message.getString(54).equals("1"))
							sapTradeCaptureEntry.setSide(Side.BUY);
						else if (message.getString(54).equals("2"))
							sapTradeCaptureEntry.setSide(Side.SELL);
						else {

						}
						if (message.isSetField(64))
							sapTradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

						sapTradeCaptureEntry.setUpdated(System.currentTimeMillis());

						persistTradeCaptureEntry(sapTradeCaptureEntry, true);

					}
				}
			}

		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	private void persistTradeCaptureEntry(SAPTradeCaptureEntry sapTradeCaptureEntry, boolean logTradeCapture) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("System processed trade capture ");

		SAPTradeCaptureReport tradeCaptureReport = new SAPTradeCaptureReport();

		AbstractBusinessComponent sourceComponent = basisPersistenceHandler.findById(AbstractBusinessComponent.class,
				sapTradeCaptureEntry.getSourceComponent(), businessComponentHandler);

		List<SAPTradeCaptureReport> tradeCaptureReports = new ArrayList<SAPTradeCaptureReport>();

		if (sapTradeCaptureEntry.getTradeId() != null) {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(sapTradeCaptureEntry.getTradeId());
			parameters.add(sourceComponent);

			tradeCaptureReports = basisPersistenceHandler.executeQuery(SAPTradeCaptureReport.class,
					"select a from SAPTradeCaptureReport a where a.tradeId=?1 and a.sourceComponent=?2", parameters, businessComponentHandler, true);
		}

		if (tradeCaptureReports.size() > 0) {
			newEntry = false;
			tradeCaptureReport = tradeCaptureReports.get(0);
		}

		SAPTradeCapture sapTradeCapture = basisPersistenceHandler.findById(SAPTradeCapture.class, sapTradeCaptureEntry.getSAPTradeCapture(),
				businessComponentHandler);
		tradeCaptureReport.setSAPTradeCapture(sapTradeCapture);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sapTradeCaptureEntry.getCounterparty(), businessComponentHandler);
		tradeCaptureReport.setCounterparty(counterparty);

		Trader trader = null;

		if (sapTradeCaptureEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, sapTradeCaptureEntry.getTrader(), businessComponentHandler);
			tradeCaptureReport.setTrader(trader);
		}

		tradeCaptureReport.setExecutingTrader(sapTradeCaptureEntry.getUserID());

		if (sapTradeCaptureEntry.getUser() != null) {
			FUser user = basisPersistenceHandler.findById(FUser.class, sapTradeCaptureEntry.getUser(), businessComponentHandler);
			tradeCaptureReport.setUser(user);
		}

		tradeCaptureReport.setCounterpartyOrderId(sapTradeCaptureEntry.getCounterpartyOrderId());
		tradeCaptureReport.setCreatedTimestamp(sapTradeCaptureEntry.getCreated());
		tradeCaptureReport.setCumulativeQuantity(sapTradeCaptureEntry.getCumulativeQuantity());
		tradeCaptureReport.setLastPrice(sapTradeCaptureEntry.getLastPrice());
		tradeCaptureReport.setLastQuantity(sapTradeCaptureEntry.getLastQuantity());
		tradeCaptureReport.setLeaveQuantity(sapTradeCaptureEntry.getLeaveQuantity());
		tradeCaptureReport.setSettlementDate(sapTradeCaptureEntry.getSettlementDate());
		tradeCaptureReport.setMarket(sapTradeCaptureEntry.getMarket());

		tradeCaptureReport.setSourceComponent(sourceComponent);

		tradeCaptureReport.setOrderId(sapTradeCaptureEntry.getOrderId());
		tradeCaptureReport.setTradeId(sapTradeCaptureEntry.getTradeId());
		tradeCaptureReport.setExecId(sapTradeCaptureEntry.getExecId());
		tradeCaptureReport.setOrderQuantity(sapTradeCaptureEntry.getOrderQuantity());
		tradeCaptureReport.setExecutingTrader(sapTradeCaptureEntry.getUserID());

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sapTradeCaptureEntry.getSecurity(), businessComponentHandler);
		tradeCaptureReport.setSecurity(security);

		tradeCaptureReport.setSide(SAPTradeCaptureReport.Side.BUY);
		String side = " from ";
		if (sapTradeCaptureEntry.getSide() == Side.SELL) {
			stringBuffer.append("sell ");
			side = " to ";
			tradeCaptureReport.setSide(SAPTradeCaptureReport.Side.SELL);
		}
		else {
			stringBuffer.append("buy ");
		}

		stringBuffer.append(decimalFormat2.format(sapTradeCaptureEntry.getLastQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		if (sapTradeCaptureEntry.getSettlementDate() != null) {
			stringBuffer.append(" settlement ");
			stringBuffer.append(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())));
		}

		tradeCaptureReport.setUpdatedTimestamp(sapTradeCaptureEntry.getUpdated());

		tradeCaptureReport.setSapFinancialTransaction(sapTradeCaptureEntry.getSapFinancialTransaction());

		switch (sapTradeCaptureEntry.getExportStatus()) {
			case DONE:
				tradeCaptureReport.setExportStatus(ExportStatus.DONE);
				break;
			case FAILED:
				tradeCaptureReport.setExportStatus(ExportStatus.FAILED);
				break;
			case NEW:
				tradeCaptureReport.setExportStatus(ExportStatus.NEW);
				break;
			case TEST_DONE:
				tradeCaptureReport.setExportStatus(ExportStatus.TEST_DONE);
				break;
			case TEST_FAILED:
				tradeCaptureReport.setExportStatus(ExportStatus.TEST_FAILED);
				break;
		}

		try {
			if (newEntry) {
				basisPersistenceHandler.persist(tradeCaptureReport);
				sapTradeCaptureEntry.setId(tradeCaptureReport.getId());
			}
			else
				basisPersistenceHandler.update(tradeCaptureReport);

			for (Channel channel : tradeCaptureSessions) {

				basisPersistenceHandler.send(new SAPTradeCaptureEntryResponse(sapTradeCaptureEntry), channel);
			}

			if (logTradeCapture) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(sapTradeCapture.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(sapTradeCaptureEntry.getOrderId() + ";;;" + sapTradeCaptureEntry.getTradeId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sapTradeCapture.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(sapTradeCaptureEntry.getOrderId() + ";;;" + sapTradeCaptureEntry.getTradeId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

	}

	/**
	 * On open sap trade capture request.
	 *
	 * @param openSAPTradeCaptureRequest the open sap trade capture request
	 * @param channel the channel
	 * @return the open sap trade capture response
	 */
	public OpenSAPTradeCaptureResponse onOpenSAPTradeCaptureRequest(OpenSAPTradeCaptureRequest openSAPTradeCaptureRequest, Channel channel) {

		Set<SAPTradeCaptureEntry> tradeCaptureEntries = new HashSet<SAPTradeCaptureEntry>();

		if (openSAPTradeCaptureRequest.getSAPTradeCapture() == sapTradeCapture.getId()) {

			List<SAPTradeCaptureReport> sapTradeCaptureReports = basisPersistenceHandler.executeQuery(SAPTradeCaptureReport.class,
					"select a from SAPTradeCaptureReport a where a.sapTradeCapture.id=?1", sapTradeCapture.getId(), businessComponentHandler, true);
			for (SAPTradeCaptureReport sapTradeCaptureReport : sapTradeCaptureReports) {
				SAPTradeCaptureEntry sapTradeCaptureEntry = getSAPTradeCaptureEntry(sapTradeCaptureReport);
				tradeCaptureEntries.add(sapTradeCaptureEntry);
			}
		}

		tradeCaptureSessions.add(channel);

		return new OpenSAPTradeCaptureResponse(openSAPTradeCaptureRequest, tradeCaptureEntries, actionInProgress, sapConnector != null);

	}

	/**
	 * On close sap trade capture request.
	 *
	 * @param closeSAPTradeCaptureRequest the close sap trade capture request
	 * @param channel the channel
	 */
	public void onCloseSAPTradeCaptureRequest(CloseSAPTradeCaptureRequest closeSAPTradeCaptureRequest, Channel channel) {

		tradeCaptureSessions.remove(channel);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

	}

	/**
	 * Removes the sap trade capture entry.
	 *
	 * @param sapTradeCaptureEntry the sap trade capture entry
	 */
	public void removeSAPTradeCaptureEntry(SAPTradeCaptureEntry sapTradeCaptureEntry) {

		try {
			List<SAPTradeCaptureReport> sapTradeCaptureReports = basisPersistenceHandler.executeQuery(SAPTradeCaptureReport.class,
					"select a from SAPTradeCaptureReport a where a.id=?1", sapTradeCaptureEntry.getId(), businessComponentHandler, false);
			for (SAPTradeCaptureReport sapTradeCaptureReport : sapTradeCaptureReports) {
				{
					basisPersistenceHandler.removeObject(sapTradeCaptureReport);
					for (Channel channel : tradeCaptureSessions)
						basisPersistenceHandler.send(new SAPTradeCaptureEntryResponse(sapTradeCaptureEntry), channel);
				}
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	/**
	 * On sap trade capture action request.
	 *
	 * @param sapTradeCaptureActionRequest the sap trade capture action request
	 * @param channel the channel
	 */
	public void onSAPTradeCaptureActionRequest(SAPTradeCaptureActionRequest sapTradeCaptureActionRequest, Channel channel) {

		if (actionInProgress)
			basisPersistenceHandler.send(new SAPTradeCaptureActionResponse(sapTradeCaptureActionRequest, sapTradeCaptureActionRequest.getSAPTradeCapture(),
					sapTradeCaptureActionRequest.getTradeCaptureAction(), ActionResponse.FAILED), channel);
		else {
			actionInProgress = true;
			basisPersistenceHandler.send(new SAPTradeCaptureActionResponse(sapTradeCaptureActionRequest, sapTradeCaptureActionRequest.getSAPTradeCapture(),
					sapTradeCaptureActionRequest.getTradeCaptureAction(), ActionResponse.STARTED), channel);

			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.INFO);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sapTradeCapture.getName());
			if (sapTradeCaptureActionRequest.getTradeCaptureAction() == SAPTradeCaptureAction.REMOVE)
				logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is removing all sap trade exports from cache.");

			if (sapTradeCaptureActionRequest.getTradeCaptureAction() == SAPTradeCaptureAction.TEST)
				logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is testing sap export.");

			if (sapTradeCaptureActionRequest.getTradeCaptureAction() == SAPTradeCaptureAction.EXPORT)
				logEntry.setMessageText("User " + basisPersistenceHandler.getUser(channel) + " is starting sap export.");

			basisPersistenceHandler.writeLogEntry(logEntry);

			for (Channel channel2 : tradeCaptureSessions) {
				basisPersistenceHandler.send(new SAPTradeCaptureActionResponse(null, sapTradeCaptureActionRequest.getSAPTradeCapture(),
						sapTradeCaptureActionRequest.getTradeCaptureAction(), ActionResponse.STARTED), channel2);

			}

			List<SAPTradeCaptureReport> sapTradeCaptureReports = basisPersistenceHandler.executeQuery(SAPTradeCaptureReport.class,
					"select a from SAPTradeCaptureReport a where a.sapTradeCapture.id=?1", sapTradeCaptureActionRequest.getSAPTradeCapture(),
					businessComponentHandler, true);

			if (sapTradeCaptureActionRequest.getTradeCaptureAction() == SAPTradeCaptureAction.REMOVE) {
				for (SAPTradeCaptureReport sapTradeCaptureReport : sapTradeCaptureReports) {
					{
						try {

							SAPTradeCaptureEntry sapTradeCaptureEntry = getSAPTradeCaptureEntry(sapTradeCaptureReport);
							sapTradeCaptureEntry.setRemoved(true);
							basisPersistenceHandler.removeObject(sapTradeCaptureReport);
							for (Channel channel2 : tradeCaptureSessions)
								basisPersistenceHandler.send(new SAPTradeCaptureEntryResponse(sapTradeCaptureEntry), channel2);
						}
						catch (Exception e) {
							log.error("Bug", e);
						}
					}
				}

			}
			else {
				if (sapConnector != null) {
					try {
						sapConnector.connect();
						for (SAPTradeCaptureReport sapTradeCaptureReport : sapTradeCaptureReports) {
							try {
								SAPTradeCaptureEntry sapTradeCaptureEntry = getSAPTradeCaptureEntry(sapTradeCaptureReport);
								if (sapTradeCaptureEntry.getExportStatus() != net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus.DONE) {
									sapConnector.exportSAPTradeCaptureEntry(sapTradeCaptureEntry, channel,
											sapTradeCaptureActionRequest.getTradeCaptureAction() == SAPTradeCaptureAction.TEST);
									persistTradeCaptureEntry(sapTradeCaptureEntry, false);
									for (Channel channel2 : tradeCaptureSessions)
										basisPersistenceHandler.send(new SAPTradeCaptureEntryResponse(sapTradeCaptureEntry), channel2);
								}
							}
							catch (Exception e) {
								log.error("Bug", e);
							}
						}
						sapConnector.disconnect();
					}
					catch (Exception e) {

						log.error("Bug", e);

						LogEntry logEntry2 = new LogEntry();
						logEntry2.setLogLevel(Level.FATAL);
						logEntry2.setLogDate(new Date());
						logEntry2.setMessageComponent(sapTradeCapture.getName());
						logEntry2.setMessageText(e.getMessage());
						basisPersistenceHandler.writeLogEntry(logEntry2);
					}
				}

			}

			for (Channel channel2 : tradeCaptureSessions) {

				basisPersistenceHandler.send(new SAPTradeCaptureActionResponse(null, sapTradeCaptureActionRequest.getSAPTradeCapture(),
						sapTradeCaptureActionRequest.getTradeCaptureAction(), ActionResponse.STOPPED), channel2);

			}

			if (sapConnector != null)
				actionInProgress = false;

		}
	}

	private void handleSecurityListRequest(Message message, AbstractBusinessComponent initialComponent) {

		try {
			Message securityList = new Message();
			securityList.getHeader().setString(MsgType.FIELD, "y");
			securityList.setString(320, message.getString(320));
			securityList.setString(322, message.getString(320));
			securityList.setInt(560, 0);
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(initialComponent.getId());
			MessageEntry messageEntry = new MessageEntry(getAbstractBusinessComponent(), getAbstractBusinessComponent(), securityList);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

}
