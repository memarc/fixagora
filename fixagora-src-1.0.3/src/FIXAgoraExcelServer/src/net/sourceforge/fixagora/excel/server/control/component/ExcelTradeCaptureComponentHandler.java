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
package net.sourceforge.fixagora.excel.server.control.component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import net.sourceforge.fixagora.excel.shared.communication.CloseExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry.Side;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCapture;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCaptureReport;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class ExcelTradeCaptureComponentHandler.
 */
public class ExcelTradeCaptureComponentHandler extends AbstractComponentHandler {

	private ExcelTradeCapture excelTradeCapture = null;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0##############");

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private DecimalFormat decimalFormat3 = new DecimalFormat("####");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<Channel> tradeCaptureSessions = new HashSet<Channel>();

	private static Logger log = Logger.getLogger(ExcelTradeCaptureComponentHandler.class);

	/**
	 * Instantiates a new excel trade capture component handler.
	 */
	public ExcelTradeCaptureComponentHandler() {

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
			MessageEntry messageEntry = new MessageEntry(excelTradeCapture, excelTradeCapture, tradeCaptureReportRequest);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}

		super.startProcessor();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof ExcelTradeCapture) {
			excelTradeCapture = (ExcelTradeCapture) abstractBusinessComponent;
		}

	}

	private ExcelTradeCaptureEntry getExcelTradeCaptureEntry(ExcelTradeCaptureReport excelTradeCaptureReport) {

		ExcelTradeCaptureEntry excelTradeCaptureEntry = new ExcelTradeCaptureEntry();
		excelTradeCaptureEntry.setTradeCapture(excelTradeCaptureReport.getExcelTradeCapture().getId());
		excelTradeCaptureEntry.setCounterparty(excelTradeCaptureReport.getCounterparty().getId());
		excelTradeCaptureEntry.setCounterpartyOrderId(excelTradeCaptureReport.getCounterpartyOrderId());
		excelTradeCaptureEntry.setCreated(excelTradeCaptureReport.getCreatedTimestamp());
		excelTradeCaptureEntry.setCumulativeQuantity(excelTradeCaptureReport.getCumulativeQuantity());
		excelTradeCaptureEntry.setLastPrice(excelTradeCaptureReport.getLastPrice());
		excelTradeCaptureEntry.setLastYield(excelTradeCaptureReport.getLastYield());
		excelTradeCaptureEntry.setLastQuantity(excelTradeCaptureReport.getLastQuantity());
		excelTradeCaptureEntry.setLeaveQuantity(excelTradeCaptureReport.getLeaveQuantity());
		excelTradeCaptureEntry.setSourceComponent(excelTradeCaptureReport.getSourceComponent().getId());
		excelTradeCaptureEntry.setId(excelTradeCaptureReport.getId());
		excelTradeCaptureEntry.setOrderId(excelTradeCaptureReport.getOrderId());
		excelTradeCaptureEntry.setOrderQuantity(excelTradeCaptureReport.getOrderQuantity());
		excelTradeCaptureEntry.setSettlementDate(excelTradeCaptureReport.getSettlementDate());
		excelTradeCaptureEntry.setMarket(excelTradeCaptureReport.getMarket());
		excelTradeCaptureEntry.setExecId(excelTradeCaptureReport.getExecId());
		excelTradeCaptureEntry.setTradeId(excelTradeCaptureReport.getTradeId());

		excelTradeCaptureEntry.setSecurity(excelTradeCaptureReport.getSecurity().getId());
		excelTradeCaptureEntry.setSide(Side.BUY);
		if (excelTradeCaptureReport.getSide() == ExcelTradeCaptureReport.Side.SELL)
			excelTradeCaptureEntry.setSide(Side.SELL);

		excelTradeCaptureEntry.setUpdated(excelTradeCaptureReport.getUpdatedTimestamp());

		if (excelTradeCaptureReport.getTrader() != null)
			excelTradeCaptureEntry.setTrader(excelTradeCaptureReport.getTrader().getId());

		if (excelTradeCaptureReport.getExecutingTrader() != null)
			excelTradeCaptureEntry.setUserID(excelTradeCaptureReport.getExecutingTrader());

		if (excelTradeCaptureReport.getUser() != null)
			excelTradeCaptureEntry.setUser(excelTradeCaptureReport.getUser().getId());

		return excelTradeCaptureEntry;
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

		return excelTradeCapture;
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
			}
		}
	}

	private void handleTradeCaptureReport(Message message, AbstractBusinessComponent businessComponent) {

		ExcelTradeCaptureEntry excelTradeCaptureEntry = new ExcelTradeCaptureEntry();

		excelTradeCaptureEntry.setSourceComponent(businessComponent.getId());

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

					excelTradeCaptureEntry.setMarket(parties.getString(448));
				}
			}

			Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 17);

			if (counterparty != null) {

				excelTradeCaptureEntry.setCounterparty(counterparty.getId());

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
							else if (traderPartyID.getAbstractBusinessComponent().getId() == businessComponent.getId() && traderPartyID.getPartyRole() != null
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
						logEntry.setMessageComponent(excelTradeCapture.getName());
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

					excelTradeCaptureEntry.setTrader(trader.getId());

				}

				FUser user = null;

				if (userId != null) {

					List<FUser> users = basisPersistenceHandler.executeQuery(FUser.class, "select t from FUser t", businessComponentHandler, true);

					for (FUser user2 : users) {
						for (FUserPartyID userPartyID : user2.getfUserPartyIDs()) {

							if (userPartyID.getAbstractBusinessComponent() == null) {
								if (user == null && userPartyID.getPartyIDSource() != null && userPartyID.getPartyIDSource().equals(userIdSource)
										&& userId.equals(userPartyID.getPartyID()))
									user = user2;
							}
							else if (userPartyID.getAbstractBusinessComponent().getId() == businessComponent.getId() && userPartyID.getPartyRole() != null
									&& userPartyID.getPartyRole().intValue() == 12 && userPartyID.getPartyIDSource() != null
									&& userPartyID.getPartyIDSource().equals(userIdSource) && userId.equals(userPartyID.getPartyID()))
								user = user2;
						}
					}
				}

				if (user != null)
					excelTradeCaptureEntry.setUser(user.getId());
				else
					excelTradeCaptureEntry.setUserID(userId);

				if (message.isSetField(11))
					excelTradeCaptureEntry.setCounterpartyOrderId(message.getString(11));
				if (message.isSetField(38))
					excelTradeCaptureEntry.setOrderQuantity(message.getDouble(38));
				if (message.isSetField(151))
					excelTradeCaptureEntry.setLeaveQuantity(message.getDouble(151));

				excelTradeCaptureEntry.setCreated(System.currentTimeMillis());

				if (message.isSetField(14))
					excelTradeCaptureEntry.setCumulativeQuantity(message.getDouble(14));

				if (message.isSetField(64))
					excelTradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

				if (message.isSetField(32))
					excelTradeCaptureEntry.setLastQuantity(message.getDouble(32));

				if (message.isSetField(37))
					excelTradeCaptureEntry.setOrderId(message.getString(37));

				if (message.isSetField(1003))
					excelTradeCaptureEntry.setTradeId(message.getString(1003));

				if (message.isSetField(17))
					excelTradeCaptureEntry.setExecId(message.getString(17));

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
							excelTradeCaptureEntry.setLastPrice(message.getDouble(669));
						if (message.isSetField(31))
							excelTradeCaptureEntry.setLastYield(message.getDouble(31));
					}
					else if (message.isSetField(31))
						excelTradeCaptureEntry.setLastPrice(message.getDouble(31));

					excelTradeCaptureEntry.setSecurity(security.getId());
					excelTradeCaptureEntry.setTradeCapture(excelTradeCapture.getId());
					if (message.isSetField(54)) {

						if (message.getString(54).equals("1"))
							excelTradeCaptureEntry.setSide(Side.BUY);
						else if (message.getString(54).equals("2"))
							excelTradeCaptureEntry.setSide(Side.SELL);
						else {

						}
						if (message.isSetField(64))
							excelTradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

						excelTradeCaptureEntry.setUpdated(System.currentTimeMillis());

						persistTradeCaptureEntry(excelTradeCaptureEntry, true);

					}
				}
			}

		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	private void persistTradeCaptureEntry(ExcelTradeCaptureEntry excelTradeCaptureEntry, boolean logTradeCapture) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("System processed trade capture ");

		ExcelTradeCaptureReport tradeCaptureReport = new ExcelTradeCaptureReport();

		AbstractBusinessComponent sourceComponent = basisPersistenceHandler.findById(AbstractBusinessComponent.class,
				excelTradeCaptureEntry.getSourceComponent(), businessComponentHandler);

		List<ExcelTradeCaptureReport> tradeCaptureReports = new ArrayList<ExcelTradeCaptureReport>();

		if (excelTradeCaptureEntry.getTradeId() != null) {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(excelTradeCaptureEntry.getTradeId());
			parameters.add(sourceComponent);

			tradeCaptureReports = basisPersistenceHandler.executeQuery(ExcelTradeCaptureReport.class,
					"select a from ExcelTradeCaptureReport a where a.tradeId=?1 and a.sourceComponent=?2", parameters, businessComponentHandler, true);
		}

		if (tradeCaptureReports.size() > 0) {
			newEntry = false;
			tradeCaptureReport = tradeCaptureReports.get(0);
		}

		ExcelTradeCapture excelTradeCapture = basisPersistenceHandler.findById(ExcelTradeCapture.class, excelTradeCaptureEntry.getExcelTradeCapture(),
				businessComponentHandler);
		tradeCaptureReport.setExcelTradeCapture(excelTradeCapture);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, excelTradeCaptureEntry.getCounterparty(), businessComponentHandler);
		tradeCaptureReport.setCounterparty(counterparty);

		Trader trader = null;

		if (excelTradeCaptureEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, excelTradeCaptureEntry.getTrader(), businessComponentHandler);
			tradeCaptureReport.setTrader(trader);
		}

		tradeCaptureReport.setExecutingTrader(excelTradeCaptureEntry.getUserID());

		if (excelTradeCaptureEntry.getUser() != null) {
			FUser user = basisPersistenceHandler.findById(FUser.class, excelTradeCaptureEntry.getUser(), businessComponentHandler);
			tradeCaptureReport.setUser(user);
		}

		tradeCaptureReport.setCounterpartyOrderId(excelTradeCaptureEntry.getCounterpartyOrderId());
		tradeCaptureReport.setCreatedTimestamp(excelTradeCaptureEntry.getCreated());
		tradeCaptureReport.setCumulativeQuantity(excelTradeCaptureEntry.getCumulativeQuantity());
		tradeCaptureReport.setLastPrice(excelTradeCaptureEntry.getLastPrice());
		tradeCaptureReport.setLastYield(excelTradeCaptureEntry.getLastYield());
		tradeCaptureReport.setLastQuantity(excelTradeCaptureEntry.getLastQuantity());
		tradeCaptureReport.setLeaveQuantity(excelTradeCaptureEntry.getLeaveQuantity());
		tradeCaptureReport.setSettlementDate(excelTradeCaptureEntry.getSettlementDate());
		tradeCaptureReport.setMarket(excelTradeCaptureEntry.getMarket());

		tradeCaptureReport.setSourceComponent(sourceComponent);

		tradeCaptureReport.setOrderId(excelTradeCaptureEntry.getOrderId());
		tradeCaptureReport.setTradeId(excelTradeCaptureEntry.getTradeId());
		tradeCaptureReport.setExecId(excelTradeCaptureEntry.getExecId());
		tradeCaptureReport.setOrderQuantity(excelTradeCaptureEntry.getOrderQuantity());
		tradeCaptureReport.setExecutingTrader(excelTradeCaptureEntry.getUserID());

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, excelTradeCaptureEntry.getSecurity(), businessComponentHandler);
		tradeCaptureReport.setSecurity(security);

		tradeCaptureReport.setSide(ExcelTradeCaptureReport.Side.BUY);
		String side = " from ";
		if (excelTradeCaptureEntry.getSide() == Side.SELL) {
			stringBuffer.append("sell ");
			side = " to ";
			tradeCaptureReport.setSide(ExcelTradeCaptureReport.Side.SELL);
		}
		else {
			stringBuffer.append("buy ");
		}

		stringBuffer.append(decimalFormat2.format(excelTradeCaptureEntry.getLastQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		if (excelTradeCaptureEntry.getSettlementDate() != null) {
			stringBuffer.append(" settlement ");
			stringBuffer.append(simpleDateFormat.format(new Date(excelTradeCaptureEntry.getSettlementDate())));
		}

		tradeCaptureReport.setUpdatedTimestamp(excelTradeCaptureEntry.getUpdated());
		try {
			if (newEntry) {
				basisPersistenceHandler.persist(tradeCaptureReport);
				excelTradeCaptureEntry.setId(tradeCaptureReport.getId());
			}
			else
				basisPersistenceHandler.update(tradeCaptureReport);

			for (Channel channel : tradeCaptureSessions) {

				basisPersistenceHandler.send(new ExcelTradeCaptureEntryResponse(excelTradeCaptureEntry), channel);
			}

			if (logTradeCapture) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(excelTradeCapture.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(excelTradeCaptureEntry.getOrderId() + ";;;" + excelTradeCaptureEntry.getTradeId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(excelTradeCapture.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(excelTradeCaptureEntry.getOrderId() + ";;;" + excelTradeCaptureEntry.getTradeId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

	}

	/**
	 * On open excel trade capture request.
	 *
	 * @param openExcelTradeCaptureRequest the open excel trade capture request
	 * @param channel the channel
	 * @return the collection
	 */
	public Collection<ExcelTradeCaptureEntry> onOpenExcelTradeCaptureRequest(OpenExcelTradeCaptureRequest openExcelTradeCaptureRequest, Channel channel) {

		Set<ExcelTradeCaptureEntry> tradeCaptureEntries = new HashSet<ExcelTradeCaptureEntry>();

		if (openExcelTradeCaptureRequest.getExcelTradeCapture() == excelTradeCapture.getId()) {

			List<ExcelTradeCaptureReport> excelTradeCaptureReports = basisPersistenceHandler.executeQuery(ExcelTradeCaptureReport.class,
					"select a from ExcelTradeCaptureReport a where a.excelTradeCapture.id=?1", excelTradeCapture.getId(), businessComponentHandler, true);
			for (ExcelTradeCaptureReport excelTradeCaptureReport : excelTradeCaptureReports) {
				ExcelTradeCaptureEntry excelTradeCaptureEntry = getExcelTradeCaptureEntry(excelTradeCaptureReport);
				tradeCaptureEntries.add(excelTradeCaptureEntry);
			}
		}

		tradeCaptureSessions.add(channel);

		return tradeCaptureEntries;
	}

	/**
	 * On close excel trade capture request.
	 *
	 * @param closeExcelTradeCaptureRequest the close excel trade capture request
	 * @param channel the channel
	 */
	public void onCloseExcelTradeCaptureRequest(CloseExcelTradeCaptureRequest closeExcelTradeCaptureRequest, Channel channel) {

		tradeCaptureSessions.remove(channel);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

	}

	/**
	 * Removes the excel trade capture entry.
	 *
	 * @param excelTradeCaptureEntry the excel trade capture entry
	 */
	public void removeExcelTradeCaptureEntry(ExcelTradeCaptureEntry excelTradeCaptureEntry) {

		try {
			List<ExcelTradeCaptureReport> excelTradeCaptureReports = basisPersistenceHandler.executeQuery(ExcelTradeCaptureReport.class,
					"select a from ExcelTradeCaptureReport a where a.id=?1", excelTradeCaptureEntry.getId(), businessComponentHandler, false);
			for (ExcelTradeCaptureReport excelTradeCaptureReport : excelTradeCaptureReports) {
				{
					basisPersistenceHandler.removeObject(excelTradeCaptureReport);
					for (Channel channel : tradeCaptureSessions)
						basisPersistenceHandler.send(new ExcelTradeCaptureEntryResponse(excelTradeCaptureEntry), channel);
				}
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
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
