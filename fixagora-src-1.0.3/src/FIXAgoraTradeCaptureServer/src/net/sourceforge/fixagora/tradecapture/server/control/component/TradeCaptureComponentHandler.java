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
package net.sourceforge.fixagora.tradecapture.server.control.component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.MessageEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.tradecapture.shared.communication.CloseTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry.Side;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureSource;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureTarget;
import net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCapture;
import net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCaptureReport;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class TradeCaptureComponentHandler.
 */
public class TradeCaptureComponentHandler extends AbstractComponentHandler {

	private TradeCapture tradeCapture = null;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0##############");

	private DecimalFormat decimalFormat3 = new DecimalFormat("####");

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Map<Channel, Long> tradeCaptureSessions = new HashMap<Channel, Long>();

	private Set<AbstractComponentHandler> onlineTradeCaptureTargets = new HashSet<AbstractComponentHandler>();

	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");

	private static Logger log = Logger.getLogger(TradeCaptureComponentHandler.class);

	/**
	 * Instantiates a new trade capture component handler.
	 */
	public TradeCaptureComponentHandler() {

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

		for (AssignedTradeCaptureSource assignedTradeCaptureSource : tradeCapture.getAssignedTradeCaptureSources()) {
			AbstractBusinessComponent abstractBusinessComponent = assignedTradeCaptureSource.getBusinessComponent();
			Message tradeCaptureReportRequest = new Message();
			tradeCaptureReportRequest.getHeader().setString(MsgType.FIELD, "AD");
			tradeCaptureReportRequest.setString(568, decimalFormat3.format(basisPersistenceHandler.getId("TRADEREQUESTID")));
			tradeCaptureReportRequest.setInt(569, 0);

			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractBusinessComponent.getId());
			if (abstractComponentHandler != null) {
				MessageEntry messageEntry = new MessageEntry(tradeCapture, tradeCapture, tradeCaptureReportRequest);
				abstractComponentHandler.addFIXMessage(messageEntry);
			}
		}

		super.startProcessor();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof TradeCapture) {
			tradeCapture = (TradeCapture) abstractBusinessComponent;
		}

	}

	private TradeCaptureEntry getTradeCaptureEntry(TradeCaptureReport tradeCaptureReport) {

		TradeCaptureEntry tradeCaptureEntry = new TradeCaptureEntry();
		tradeCaptureEntry.setTradeCapture(tradeCaptureReport.getTradeCapture().getId());
		tradeCaptureEntry.setCounterparty(tradeCaptureReport.getCounterparty().getId());
		tradeCaptureEntry.setCounterpartyOrderId(tradeCaptureReport.getCounterpartyOrderId());
		tradeCaptureEntry.setCreated(tradeCaptureReport.getCreatedTimestamp());
		tradeCaptureEntry.setCumulativeQuantity(tradeCaptureReport.getCumulativeQuantity());
		tradeCaptureEntry.setLastPrice(tradeCaptureReport.getLastPrice());
		tradeCaptureEntry.setLastYield(tradeCaptureReport.getLastYield());
		tradeCaptureEntry.setLastQuantity(tradeCaptureReport.getLastQuantity());
		tradeCaptureEntry.setLeaveQuantity(tradeCaptureReport.getLeaveQuantity());
		tradeCaptureEntry.setSourceComponent(tradeCaptureReport.getSourceComponent().getId());
		tradeCaptureEntry.setId(tradeCaptureReport.getId());
		tradeCaptureEntry.setOrderId(tradeCaptureReport.getOrderId());
		tradeCaptureEntry.setOrderQuantity(tradeCaptureReport.getOrderQuantity());
		tradeCaptureEntry.setSettlementDate(tradeCaptureReport.getSettlementDate());
		tradeCaptureEntry.setMarket(tradeCaptureReport.getMarket());
		tradeCaptureEntry.setExecId(tradeCaptureReport.getExecId());
		tradeCaptureEntry.setTradeId(tradeCaptureReport.getTradeId());

		tradeCaptureEntry.setSecurity(tradeCaptureReport.getSecurity().getId());
		tradeCaptureEntry.setSide(Side.BUY);
		if (tradeCaptureReport.getSide() == net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCaptureReport.Side.SELL)
			tradeCaptureEntry.setSide(Side.SELL);

		tradeCaptureEntry.setUpdated(tradeCaptureReport.getUpdatedTimestamp());

		if (tradeCaptureReport.getTrader() != null)
			tradeCaptureEntry.setTrader(tradeCaptureReport.getTrader().getId());

		tradeCaptureEntry.setExecutingTrader(tradeCaptureReport.getExecutingTrader());
		tradeCaptureEntry.setExecutingTraderSource(tradeCaptureReport.getExecutingTraderSource());

		if (tradeCaptureReport.getfUser() != null)
			tradeCaptureEntry.setFUser(tradeCaptureReport.getfUser().getId());

		return tradeCaptureEntry;
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

		return tradeCapture;
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

				if ((!messageType.equals("X")) && (!messageType.equals("W"))) {
					if (messageType.equals("AE")) {
						handleTradeCaptureReport(message, messageEntry.getInitialComponent());
					}
					else if (messageType.equals("x")) {
						handleSecurityListRequest(message, messageEntry.getInitialComponent());
					}
					else if (messageType.equals("AD")) {
						Message tradeCaptureReportRequestAck = new Message();
						tradeCaptureReportRequestAck.getHeader().setString(MsgType.FIELD, "AQ");
						tradeCaptureReportRequestAck.setString(568, message.getString(568));
						tradeCaptureReportRequestAck.setInt(569, message.getInt(569));

						if (message.getHeader().isSetField(49))
							tradeCaptureReportRequestAck.getHeader().setString(56, message.getHeader().getString(49));

						AssignedTradeCaptureTarget assignedTradeCaptureTarget = null;

						for (AssignedTradeCaptureTarget assignedTradeCaptureTarget2 : tradeCapture.getAssignedTradeCaptureTargets())
							if (assignedTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent().getId())
								assignedTradeCaptureTarget = assignedTradeCaptureTarget2;

						tradeCaptureReportRequestAck.setInt(749, 0);
						tradeCaptureReportRequestAck.setInt(750, 0);

						if (assignedTradeCaptureTarget != null) {

							MessageEntry messageEntry2 = new MessageEntry(tradeCapture, tradeCapture, tradeCaptureReportRequestAck);

							AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(messageEntry
									.getInitialComponent().getId());
							if (abstractComponentHandler != null) {

								onlineTradeCaptureTargets.add(abstractComponentHandler);
								abstractComponentHandler.addFIXMessage(messageEntry2);
								List<Object> parameters = new ArrayList<Object>();
								parameters.add(tradeCapture);
								parameters.add(assignedTradeCaptureTarget.getLastTradeId());
								List<TradeCaptureReport> tradeCaptureReports = basisPersistenceHandler.executeQuery(TradeCaptureReport.class,
										"select s from TradeCaptureReport s where s.tradeCapture=?1 and s.createdTimestamp>?2", parameters,
										businessComponentHandler, true);
								for (TradeCaptureReport tradeCaptureReport : tradeCaptureReports) {
									if (sendTradeCaptureReport(tradeCaptureReport, messageEntry.getInitialComponent()))
										for (AssignedTradeCaptureTarget assignedTradeCaptureTarget2 : tradeCapture.getAssignedTradeCaptureTargets()) {
											if (assignedTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent()
													.getId()) {
												assignedTradeCaptureTarget2.setLastTradeId(tradeCaptureReport.getCreatedTimestamp());
												try {
													basisPersistenceHandler.update(assignedTradeCaptureTarget2);
												}
												catch (Exception e) {
													log.error("Bug", e);
												}
											}
										}
								}
							}
						}
					}

				}
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}
	}

	private boolean sendTradeCaptureReport(TradeCaptureReport tradeCaptureReport, AbstractBusinessComponent targetComponent) {

		Message tradeCaptureReportMessage = new Message();
		tradeCaptureReportMessage.getHeader().setString(MsgType.FIELD, "AE");
		tradeCaptureReportMessage.setString(37, tradeCaptureReport.getOrderId());
		tradeCaptureReportMessage.setString(17, tradeCaptureReport.getExecId());
		tradeCaptureReportMessage.setString(11, tradeCaptureReport.getCounterpartyOrderId());
		if (tradeCaptureReport.getSide() == TradeCaptureReport.Side.BUY)
			tradeCaptureReportMessage.setChar(54, '1');
		else
			tradeCaptureReportMessage.setChar(54, '2');
		tradeCaptureReportMessage.setDouble(14, tradeCaptureReport.getCumulativeQuantity());
		tradeCaptureReportMessage.setDouble(151, tradeCaptureReport.getLeaveQuantity());
		tradeCaptureReportMessage.setDouble(38, tradeCaptureReport.getOrderQuantity());
		tradeCaptureReportMessage.setString(37, tradeCaptureReport.getOrderId());
		tradeCaptureReportMessage.setString(1003, tradeCaptureReport.getTradeId());

		if (tradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod() != null
				&& tradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
			tradeCaptureReportMessage.setDouble(669, tradeCaptureReport.getLastPrice());
			tradeCaptureReportMessage.setDouble(31, tradeCaptureReport.getLastYield());
		}
		else
			tradeCaptureReportMessage.setDouble(31, tradeCaptureReport.getLastPrice());

		tradeCaptureReportMessage.setDouble(32, tradeCaptureReport.getLastQuantity());
		if (tradeCaptureReport.getSettlementDate() != null)
			tradeCaptureReportMessage.setString(64, localMarketDateFormat.format(new Date(tradeCaptureReport.getSettlementDate())));

		tradeCaptureReportMessage.setString(48, tradeCaptureReport.getSecurity().getSecurityID());
		tradeCaptureReportMessage.setString(22, tradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());

		AbstractBusinessComponent sourceComponent = tradeCaptureReport.getSourceComponent();

		Counterparty counterparty = tradeCaptureReport.getCounterparty();

		if (sourceComponent != null) {

			final Group group = new Group(453, 448, new int[] { 448, 447, 452 });

			if (tradeCaptureReport.getMarket() != null) {
				group.setString(448, tradeCaptureReport.getMarket());
				group.setChar(447, 'D');
				group.setInt(452, 16);
				tradeCaptureReportMessage.addGroup(group);
			}

			Group executingTrader = null;

			if (tradeCaptureReport.getfUser() != null) {

				FUserPartyID userPartyID = null;
				FUserPartyID originalUserPartyID = null;

				for (FUserPartyID fUserPartyID : tradeCaptureReport.getfUser().getfUserPartyIDs()) {
					if (fUserPartyID.getAbstractBusinessComponent() == null) {
						
						if(userPartyID==null)
							userPartyID = fUserPartyID;
						if(originalUserPartyID==null)
							originalUserPartyID = fUserPartyID;
					}
					else {
						if (fUserPartyID.getAbstractBusinessComponent().getId() == targetComponent.getId() && fUserPartyID.getPartyRole() != null
								&& fUserPartyID.getPartyRole().intValue() == 12) {
							userPartyID = fUserPartyID;
						}
						if (fUserPartyID.getAbstractBusinessComponent().getId() == tradeCaptureReport.getSourceComponent().getId()
								&& fUserPartyID.getPartyRole() != null && fUserPartyID.getPartyRole().intValue() == 12) {
							originalUserPartyID = fUserPartyID;
						}
					}
				}

				if (userPartyID == null && originalUserPartyID != null) {

					FUser user = originalUserPartyID.getFuser();

					userPartyID = new FUserPartyID();
					userPartyID.setPartyID(originalUserPartyID.getPartyID());
					userPartyID.setPartyIDSource(originalUserPartyID.getPartyIDSource());
					userPartyID.setAbstractBusinessComponent(targetComponent);
					userPartyID.setPartyRole(12);
					userPartyID.setFuser(user);
					user.getfUserPartyIDs().add(userPartyID);
					List<AbstractBusinessObject> users = new ArrayList<AbstractBusinessObject>();
					users.add(user);

					StringBuffer stringBuffer = new StringBuffer("No party role \"Executing Trader\" defined for user ");
					stringBuffer.append(user.getName());
					stringBuffer.append(" in target component ");
					stringBuffer.append(targetComponent.getName());
					stringBuffer
							.append(". System creates a new party role taking the values from \"Executing Trader\". This role is needed to process the trade capture.");

					LogEntry logEntry = new LogEntry();
					logEntry.setLogLevel(Level.WARNING);
					logEntry.setLogDate(new Date());
					logEntry.setMessageComponent(tradeCapture.getName());
					logEntry.setMessageText(stringBuffer.toString());
					logEntry.setHighlightKey(tradeCaptureReport.getOrderId());
					basisPersistenceHandler.writeLogEntry(logEntry);

					basisPersistenceHandler.update(users, null, businessComponentHandler, true);

				}

				if (userPartyID != null) {
					executingTrader = new Group(453, 448, new int[] { 448, 447, 452 });
					executingTrader.setString(448, userPartyID.getPartyID());
					if (userPartyID.getPartyIDSource() != null)
						executingTrader.setChar(447, userPartyID.getPartyIDSource().charAt(0));
					executingTrader.setInt(452, 12);
					tradeCaptureReportMessage.addGroup(executingTrader);
				}

			}

			if (executingTrader == null && tradeCaptureReport.getExecutingTrader() != null) {
				executingTrader = new Group(453, 448, new int[] { 448, 447, 452 });
				executingTrader.setString(448, tradeCaptureReport.getExecutingTrader());
				if (tradeCaptureReport.getExecutingTraderSource() != null)
					executingTrader.setChar(447, tradeCaptureReport.getExecutingTraderSource().charAt(0));
				executingTrader.setInt(452, 12);
				tradeCaptureReportMessage.addGroup(executingTrader);
			}

			if (counterparty != null) {

				CounterPartyPartyID contraFirm = null;
				CounterPartyPartyID originalContraFirm = null;

				for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
					if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
						if (contraFirm == null) {
							contraFirm = counterPartyPartyID;
						}
						if (originalContraFirm == null) {
							originalContraFirm = counterPartyPartyID;
						}
					}
					else {
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == targetComponent.getId() && counterPartyPartyID.getPartyRole() != null
								&& counterPartyPartyID.getPartyRole().intValue() == 17) {
							contraFirm = counterPartyPartyID;
						}
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == tradeCaptureReport.getSourceComponent().getId()
								&& counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole().intValue() == 17) {
							originalContraFirm = counterPartyPartyID;
						}
					}
				}

				if (contraFirm == null && originalContraFirm != null) {
					contraFirm = new CounterPartyPartyID();
					contraFirm.setPartyID(originalContraFirm.getPartyID());
					contraFirm.setPartyIDSource(originalContraFirm.getPartyIDSource());
					contraFirm.setAbstractBusinessComponent(targetComponent);
					contraFirm.setPartyRole(17);
					contraFirm.setCounterparty(counterparty);
					counterparty.getCounterPartyPartyIDs().add(contraFirm);
					List<AbstractBusinessObject> counterparties = new ArrayList<AbstractBusinessObject>();
					counterparties.add(counterparty);

					StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Firm\" defined for counterparty ");
					stringBuffer.append(counterparty.getName());
					stringBuffer.append(" in target component ");
					stringBuffer.append(targetComponent.getName());
					stringBuffer
							.append(". System creates a new party role taking the values from \"Order Origination Firm\". This role is needed to process the trade capture.");

					LogEntry logEntry = new LogEntry();
					logEntry.setLogLevel(Level.WARNING);
					logEntry.setLogDate(new Date());
					logEntry.setMessageComponent(tradeCapture.getName());
					logEntry.setMessageText(stringBuffer.toString());
					logEntry.setHighlightKey(tradeCaptureReport.getOrderId());
					basisPersistenceHandler.writeLogEntry(logEntry);

					List<AbstractBusinessObject> counterparties2 = basisPersistenceHandler.update(counterparties, null, businessComponentHandler, true);

					for (AbstractBusinessObject abstractBusinessObject : counterparties2) {

						if (abstractBusinessObject instanceof Counterparty) {
							businessComponentHandler.updateCounterparty((Counterparty) abstractBusinessObject);
						}
					}

				}
				if (contraFirm != null) {
					final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
					group2.setString(448, contraFirm.getPartyID());
					if (contraFirm.getPartyIDSource() != null)
						group2.setChar(447, contraFirm.getPartyIDSource().charAt(0));
					group2.setInt(452, 17);
					tradeCaptureReportMessage.addGroup(group2);
				}

				if (tradeCaptureReport.getTrader() != null) {

					Trader trader = tradeCaptureReport.getTrader();

					TraderPartyID contraTraderPartyID = null;
					TraderPartyID originalContraTraderPartyID = null;

					for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
						if (traderPartyID.getAbstractBusinessComponent() == null) {
							if (contraTraderPartyID == null)
								contraTraderPartyID = traderPartyID;
							if (originalContraTraderPartyID == null)
								originalContraTraderPartyID = traderPartyID;
						}
						else {
							if (traderPartyID.getAbstractBusinessComponent().getId() == targetComponent.getId() && traderPartyID.getPartyRole() != null
									&& traderPartyID.getPartyRole().intValue() == 37) {
								contraTraderPartyID = traderPartyID;

							}
							if (traderPartyID.getAbstractBusinessComponent().getId() == tradeCaptureReport.getSourceComponent().getId()
									&& traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole().intValue() == 37) {
								originalContraTraderPartyID = traderPartyID;
							}
						}
					}

					if (contraTraderPartyID == null && originalContraTraderPartyID != null) {
						contraTraderPartyID = new TraderPartyID();
						contraTraderPartyID.setPartyID(originalContraTraderPartyID.getPartyID());
						contraTraderPartyID.setPartyIDSource(originalContraTraderPartyID.getPartyIDSource());
						contraTraderPartyID.setAbstractBusinessComponent(targetComponent);
						contraTraderPartyID.setPartyRole(37);
						contraTraderPartyID.setTrader(trader);
						trader.getTraderPartyIDs().add(contraTraderPartyID);
						List<AbstractBusinessObject> traders = new ArrayList<AbstractBusinessObject>();
						traders.add(trader);

						StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Trader\" defined for trader ");
						stringBuffer.append(tradeCaptureReport.getTrader().getName());
						stringBuffer.append(" in target component ");
						stringBuffer.append(targetComponent.getName());
						stringBuffer
								.append(". System creates a new party role taking the values from \"Order Origination Trader\". This role is needed to process the trade capture.");

						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(tradeCapture.getName());
						logEntry.setMessageText(stringBuffer.toString());
						logEntry.setHighlightKey(tradeCaptureReport.getOrderId());
						basisPersistenceHandler.writeLogEntry(logEntry);

						basisPersistenceHandler.update(traders, null, businessComponentHandler, true);

					}

					if (contraTraderPartyID != null) {
						final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
						group2.setString(448, contraTraderPartyID.getPartyID());
						if (contraTraderPartyID.getPartyIDSource() != null)
							group2.setChar(447, contraTraderPartyID.getPartyIDSource().charAt(0));
						group2.setInt(452, 37);
						tradeCaptureReportMessage.addGroup(group2);
					}

				}

			}

			tradeCaptureReportMessage.setString(48, tradeCaptureReport.getSecurity().getSecurityID());
			tradeCaptureReportMessage.setString(22, tradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());
			tradeCaptureReportMessage.setString(55, "N/A");
			MessageEntry messageEntry = new MessageEntry(tradeCapture, sourceComponent, tradeCaptureReportMessage);
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(targetComponent.getId());
			if (abstractComponentHandler != null)
				abstractComponentHandler.addFIXMessage(messageEntry);
		}

		return true;

	}

	private void handleTradeCaptureReport(Message message, AbstractBusinessComponent businessComponent) {

		TradeCaptureEntry tradeCaptureEntry = new TradeCaptureEntry();

		tradeCaptureEntry.setSourceComponent(businessComponent.getId());

		String partyId = null;
		String partyIdSource = null;

		String traderId = null;
		String traderIdSource = null;

		String executingTraderId = null;
		String executingTraderIdSource = null;

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
						executingTraderIdSource = parties.getString(447);

					if (parties.isSetField(448))
						executingTraderId = parties.getString(448);
				}
				if (parties.isSetField(452) && parties.getInt(452) == 16) {

					tradeCaptureEntry.setMarket(parties.getString(448));
				}
			}

			Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 17);

			if (counterparty != null) {

				tradeCaptureEntry.setCounterparty(counterparty.getId());

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
						logEntry.setMessageComponent(tradeCapture.getName());
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

					tradeCaptureEntry.setTrader(trader.getId());

				}

				FUser user = null;

				if (executingTraderId != null) {

					List<FUser> users = basisPersistenceHandler.executeQuery(FUser.class, "select t from FUser t", businessComponentHandler, true);

					for (FUser user2 : users) {
						for (FUserPartyID userPartyID : user2.getfUserPartyIDs()) {
							if (userPartyID.getAbstractBusinessComponent() == null) {
								if (user == null && userPartyID.getPartyIDSource() != null && userPartyID.getPartyIDSource().equals(executingTraderIdSource)
										&& executingTraderId.equals(userPartyID.getPartyID()))
									user = user2;
							}
							else if (userPartyID.getAbstractBusinessComponent().getId() == businessComponent.getId() && userPartyID.getPartyRole() != null
									&& userPartyID.getPartyRole().intValue() == 12 && userPartyID.getPartyIDSource() != null
									&& userPartyID.getPartyIDSource().equals(executingTraderIdSource) && executingTraderId.equals(userPartyID.getPartyID()))
								user = user2;
						}
					}
				}

				if (user != null) {
					tradeCaptureEntry.setFUser(user.getId());
				}

				tradeCaptureEntry.setExecutingTrader(executingTraderId);
				tradeCaptureEntry.setExecutingTraderSource(executingTraderIdSource);

				if (message.isSetField(11))
					tradeCaptureEntry.setCounterpartyOrderId(message.getString(11));
				if (message.isSetField(38))
					tradeCaptureEntry.setOrderQuantity(message.getDouble(38));
				if (message.isSetField(151))
					tradeCaptureEntry.setLeaveQuantity(message.getDouble(151));

				tradeCaptureEntry.setCreated(System.currentTimeMillis());

				if (message.isSetField(14))
					tradeCaptureEntry.setCumulativeQuantity(message.getDouble(14));

				if (message.isSetField(64))
					tradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

				if (message.isSetField(32))
					tradeCaptureEntry.setLastQuantity(message.getDouble(32));

				if (message.isSetField(37))
					tradeCaptureEntry.setOrderId(message.getString(37));

				if (message.isSetField(1003))
					tradeCaptureEntry.setTradeId(message.getString(1003));

				if (message.isSetField(17))
					tradeCaptureEntry.setExecId(message.getString(17));

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
							tradeCaptureEntry.setLastPrice(message.getDouble(669));
						if (message.isSetField(31))
							tradeCaptureEntry.setLastYield(message.getDouble(31));
					}
					else if (message.isSetField(31))
						tradeCaptureEntry.setLastPrice(message.getDouble(31));

					tradeCaptureEntry.setSecurity(security.getId());
					tradeCaptureEntry.setTradeCapture(tradeCapture.getId());
					if (message.isSetField(54)) {

						if (message.getString(54).equals("1"))
							tradeCaptureEntry.setSide(Side.BUY);
						else if (message.getString(54).equals("2"))
							tradeCaptureEntry.setSide(Side.SELL);
						else {

						}
						if (message.isSetField(64))
							tradeCaptureEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());

						tradeCaptureEntry.setUpdated(System.currentTimeMillis());

						TradeCaptureReport tradeCaptureReport = persistTradeCaptureEntry(tradeCaptureEntry, true);

						if (tradeCaptureReport != null)
							for (AbstractComponentHandler abstractComponentHandler : onlineTradeCaptureTargets) {
								AbstractBusinessComponent abstractBusinessComponent = abstractComponentHandler.getAbstractBusinessComponent();
								sendTradeCaptureReport(tradeCaptureReport, abstractBusinessComponent);
								for (AssignedTradeCaptureTarget assignedTradeCaptureTarget : tradeCapture.getAssignedTradeCaptureTargets()) {
									if (assignedTradeCaptureTarget.getAbstractBusinessComponent().getId() == abstractBusinessComponent.getId()) {
										assignedTradeCaptureTarget.setLastTradeId(tradeCaptureReport.getCreatedTimestamp());
										basisPersistenceHandler.update(assignedTradeCaptureTarget);
									}
								}
							}
					}
				}
			}

		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	private TradeCaptureReport persistTradeCaptureEntry(TradeCaptureEntry tradeCaptureEntry, boolean logTradeCapture) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("System processed trade capture ");

		TradeCaptureReport tradeCaptureReport = new TradeCaptureReport();

		AbstractBusinessComponent sourceComponent = basisPersistenceHandler.findById(AbstractBusinessComponent.class, tradeCaptureEntry.getSourceComponent(),
				businessComponentHandler);

		List<TradeCaptureReport> tradeCaptureReports = new ArrayList<TradeCaptureReport>();

		if (tradeCaptureEntry.getTradeId() != null) {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(tradeCaptureEntry.getTradeId());
			parameters.add(sourceComponent);

			tradeCaptureReports = basisPersistenceHandler.executeQuery(TradeCaptureReport.class,
					"select a from TradeCaptureReport a where a.tradeId=?1 and a.sourceComponent=?2", parameters, businessComponentHandler, true);
		}

		if (tradeCaptureReports.size() > 0) {
			newEntry = false;
			tradeCaptureReport = tradeCaptureReports.get(0);
		}

		TradeCapture tradeCapture = basisPersistenceHandler.findById(TradeCapture.class, tradeCaptureEntry.getTradeCapture(), businessComponentHandler);
		tradeCaptureReport.setTradeCapture(tradeCapture);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, tradeCaptureEntry.getCounterparty(), businessComponentHandler);
		tradeCaptureReport.setCounterparty(counterparty);

		Trader trader = null;

		if (tradeCaptureEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, tradeCaptureEntry.getTrader(), businessComponentHandler);
			tradeCaptureReport.setTrader(trader);
		}

		tradeCaptureReport.setExecutingTrader(tradeCaptureEntry.getExecutingTrader());
		if (tradeCaptureEntry.getUser() != null) {
			FUser fUser = basisPersistenceHandler.findById(FUser.class, tradeCaptureEntry.getUser(), businessComponentHandler);
			tradeCaptureReport.setfUser(fUser);
		}

		tradeCaptureReport.setCounterpartyOrderId(tradeCaptureEntry.getCounterpartyOrderId());
		tradeCaptureReport.setCreatedTimestamp(tradeCaptureEntry.getCreated());
		tradeCaptureReport.setCumulativeQuantity(tradeCaptureEntry.getCumulativeQuantity());
		tradeCaptureReport.setLastPrice(tradeCaptureEntry.getLastPrice());
		tradeCaptureReport.setLastYield(tradeCaptureEntry.getLastYield());
		tradeCaptureReport.setLastQuantity(tradeCaptureEntry.getLastQuantity());
		tradeCaptureReport.setLeaveQuantity(tradeCaptureEntry.getLeaveQuantity());
		tradeCaptureReport.setSettlementDate(tradeCaptureEntry.getSettlementDate());
		tradeCaptureReport.setMarket(tradeCaptureEntry.getMarket());

		tradeCaptureReport.setSourceComponent(sourceComponent);

		tradeCaptureReport.setOrderId(tradeCaptureEntry.getOrderId());
		tradeCaptureReport.setTradeId(tradeCaptureEntry.getTradeId());
		tradeCaptureReport.setExecId(tradeCaptureEntry.getExecId());
		tradeCaptureReport.setOrderQuantity(tradeCaptureEntry.getOrderQuantity());
		tradeCaptureReport.setExecutingTrader(tradeCaptureEntry.getExecutingTrader());

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, tradeCaptureEntry.getSecurity(), businessComponentHandler);
		tradeCaptureReport.setSecurity(security);

		tradeCaptureReport.setSide(net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCaptureReport.Side.BUY);
		String side = " from ";
		if (tradeCaptureEntry.getSide() == Side.SELL) {
			stringBuffer.append("sell ");
			side = " to ";
			tradeCaptureReport.setSide(net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCaptureReport.Side.SELL);
		}
		else {
			stringBuffer.append("buy ");
		}

		stringBuffer.append(decimalFormat2.format(tradeCaptureEntry.getLastQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		if (tradeCaptureEntry.getSettlementDate() != null) {
			stringBuffer.append(" settlement ");
			stringBuffer.append(simpleDateFormat.format(new Date(tradeCaptureEntry.getSettlementDate())));
		}

		tradeCaptureReport.setUpdatedTimestamp(tradeCaptureEntry.getUpdated());
		try {
			if (newEntry) {
				basisPersistenceHandler.persist(tradeCaptureReport);
				tradeCaptureEntry.setId(tradeCaptureReport.getId());
			}
			else
				basisPersistenceHandler.update(tradeCaptureReport);

			for (Entry<Channel, Long> entry : tradeCaptureSessions.entrySet()) {

				if (tradeCaptureEntry.getCreated() >= entry.getValue() && tradeCaptureEntry.getCreated() < entry.getValue() + 86400000) {

					basisPersistenceHandler.send(new TradeCaptureEntryResponse(tradeCaptureEntry), entry.getKey());
				}
			}

			if (logTradeCapture) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(tradeCapture.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(tradeCaptureEntry.getOrderId() + ";;;" + tradeCaptureEntry.getTradeId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}

			return tradeCaptureReport;
		}
		catch (Exception e) {
			log.error("Bug", e);
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(tradeCapture.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(tradeCaptureEntry.getOrderId() + ";;;" + tradeCaptureEntry.getTradeId());
			basisPersistenceHandler.writeLogEntry(logEntry);
			return null;
		}

	}

	/**
	 * On open trade capture request.
	 *
	 * @param openTradeCaptureRequest the open trade capture request
	 * @param channel the channel
	 * @return the collection
	 */
	public Collection<TradeCaptureEntry> onOpenTradeCaptureRequest(OpenTradeCaptureRequest openTradeCaptureRequest, Channel channel) {

		Set<TradeCaptureEntry> tradeCaptureEntries = new HashSet<TradeCaptureEntry>();

		if (openTradeCaptureRequest.getTradeCapture() == tradeCapture.getId()) {
			tradeCaptureSessions.put(channel, openTradeCaptureRequest.getTimestamp());

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(tradeCapture.getId());
			parameters.add(openTradeCaptureRequest.getTimestamp());
			parameters.add(openTradeCaptureRequest.getTimestamp() + 86400000);

			List<TradeCaptureReport> tradeCaptureReports = basisPersistenceHandler.executeQuery(TradeCaptureReport.class,
					"select a from TradeCaptureReport a where a.tradeCapture.id=?1 and a.createdTimestamp>=?2 and a.createdTimestamp<?3", parameters,
					businessComponentHandler, true);
			for (TradeCaptureReport tradeCaptureReport : tradeCaptureReports) {
				TradeCaptureEntry tradeCaptureEntry = getTradeCaptureEntry(tradeCaptureReport);
				tradeCaptureEntries.add(tradeCaptureEntry);
			}
		}

		return tradeCaptureEntries;
	}

	/**
	 * On close trade capture request.
	 *
	 * @param closeTradeCaptureRequest the close trade capture request
	 * @param channel the channel
	 */
	public void onCloseTradeCaptureRequest(CloseTradeCaptureRequest closeTradeCaptureRequest, Channel channel) {

		tradeCaptureSessions.remove(channel);

	}

	/**
	 * On trade capture entry request.
	 *
	 * @param tradeCaptureEntryRequest the trade capture entry request
	 * @param channel the channel
	 * @return the trade capture entry
	 */
	public TradeCaptureEntry onTradeCaptureEntryRequest(TradeCaptureEntryRequest tradeCaptureEntryRequest, Channel channel) {

		return tradeCaptureEntryRequest.getTradeCaptureEntry();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

		// TODO Auto-generated method stub

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
