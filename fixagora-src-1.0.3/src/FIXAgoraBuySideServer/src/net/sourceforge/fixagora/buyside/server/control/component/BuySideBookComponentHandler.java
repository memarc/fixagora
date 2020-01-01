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
package net.sourceforge.fixagora.buyside.server.control.component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPOutputStream;

import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.MessageEntry;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCompositeMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCounterpartyMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.TimeInForce;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestRequest;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.communication.CloseBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.CloseQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.HistoricalBuySideDataResponse;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuoteDepthRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideTradeCaptureTarget;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.TimeUnit;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class BuySideBookComponentHandler.
 */
public class BuySideBookComponentHandler extends AbstractComponentHandler {

	private BuySideBook buySideBook = null;

	private Map<Long, Map<String, Group>> refIDMap = new HashMap<Long, Map<String, Group>>();

	private Map<Long, BuySideCompositeMDInputEntry> securityCompositeEntries = Collections.synchronizedMap(new HashMap<Long, BuySideCompositeMDInputEntry>());

	private Map<Long, Map<String, BuySideCounterpartyMDInputEntry>> securityCounterpartyEntries = Collections
			.synchronizedMap(new HashMap<Long, Map<String, BuySideCounterpartyMDInputEntry>>());

	private Map<Long, AssignedBuySideBookSecurity> assignedBuySideBookSecurities = new HashMap<Long, AssignedBuySideBookSecurity>();

	private Map<Channel, Map<BuySideQuotePage, Long>> quotepageSessions = new HashMap<Channel, Map<BuySideQuotePage, Long>>();

	private Map<Long, List<BuySideCompositeMDInputEntry>> historicalData = new HashMap<Long, List<BuySideCompositeMDInputEntry>>();

	private Set<Channel> bookSessions = new HashSet<Channel>();

	private Map<String, BuySideNewOrderSingleEntry> buySideNewOrderSingleEntryMap = new HashMap<String, BuySideNewOrderSingleEntry>();

	private Map<String, BuySideQuoteRequestEntry> buySideQuoteRequestEntryMap = new HashMap<String, BuySideQuoteRequestEntry>();

	private DecimalFormat decimalFormat = new DecimalFormat("0.0##############");

	private DecimalFormat decimalFormat3 = new DecimalFormat("####");

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");

	private boolean closed = false;

	private Set<AbstractComponentHandler> onlineTradeCaptureComponentHandlers = new HashSet<AbstractComponentHandler>();

	private static Logger log = Logger.getLogger(BuySideBookComponentHandler.class);

	private Timer timer;

	private Set<BuySideCounterpartyMDInputEntry> periodicUpdateSet = new HashSet<BuySideCounterpartyMDInputEntry>();

	private LinkedBlockingQueue<MessageEntry> messageQueue = new LinkedBlockingQueue<MessageEntry>();

	/**
	 * Instantiates a new buy side book component handler.
	 */
	public BuySideBookComponentHandler() {

		super();

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {

				try {
					writeMDInputEntries();
					checkQuoteRequestExpiration();
				}
				catch (Exception e) {
					log.error("Bug", e);
				}

			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 100, 200);

		Thread thread = new Thread() {

			public void run() {

				handleNonQuoteMessage();
			}
		};

		thread.start();
	}

	private void handleNonQuoteMessage() {

		while (!closed) {
			try {
				MessageEntry messageEntry = messageQueue.take();

				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);
				if (messageType.equals("8")) {
					handleExecutionReport(message);
				}
				else if (messageType.equals("9")) {
					handleOrderCancelReject(message);
				}
				else if (messageType.equals("Q")) {
					handleDontKnowTrade(message);
				}
				else if (messageType.equals("S")) {
					handleQuote(message);
				}
				else if (messageType.equals("AI")) {
					handleQuoteStatusReport(message);
				}
				else if (messageType.equals("AG")) {
					handleQuoteRequestReject(message);
				}
				else if (messageType.equals("A")) {
					handleLogon(messageEntry);
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

					AssignedBuySideTradeCaptureTarget assignedBuySideTradeCaptureTarget = null;

					for (AssignedBuySideTradeCaptureTarget assignedBuySideTradeCaptureTarget2 : buySideBook.getAssignedTradeCaptureTargets())
						if (assignedBuySideTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent().getId())
							assignedBuySideTradeCaptureTarget = assignedBuySideTradeCaptureTarget2;

					tradeCaptureReportRequestAck.setInt(749, 0);
					tradeCaptureReportRequestAck.setInt(750, 0);

					if (assignedBuySideTradeCaptureTarget == null) {

						assignedBuySideTradeCaptureTarget = new AssignedBuySideTradeCaptureTarget();
						assignedBuySideTradeCaptureTarget.setAbstractBusinessComponent(messageEntry.getInitialComponent());
						assignedBuySideTradeCaptureTarget.setBuySideBook(buySideBook);
						buySideBook.getAssignedTradeCaptureTargets().add(assignedBuySideTradeCaptureTarget);

						try {

							basisPersistenceHandler.update(buySideBook);
						}
						catch (Exception e) {
							log.error("Bug", e);

							tradeCaptureReportRequestAck.setInt(749, 99);
							tradeCaptureReportRequestAck.setInt(750, 2);

						}
					}

					MessageEntry messageEntry2 = new MessageEntry(buySideBook, buySideBook, tradeCaptureReportRequestAck);

					AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(messageEntry.getInitialComponent()
							.getId());
					if (abstractComponentHandler != null) {

						onlineTradeCaptureComponentHandlers.add(abstractComponentHandler);
						abstractComponentHandler.addFIXMessage(messageEntry2);
						List<Object> parameters = new ArrayList<Object>();
						parameters.add(buySideBook);
						parameters.add(assignedBuySideTradeCaptureTarget.getLastTradeId());
						List<BuySideBookTradeCaptureReport> buySideBookTradeCaptureReports = basisPersistenceHandler.executeQuery(
								BuySideBookTradeCaptureReport.class,
								"select s from BuySideBookTradeCaptureReport s where s.buySideBook=?1 and s.createdTimestamp>?2", parameters,
								businessComponentHandler, true);
						for (BuySideBookTradeCaptureReport buySideBookTradeCaptureReport : buySideBookTradeCaptureReports) {
							if (sendTradeCaptureReport(buySideBookTradeCaptureReport, messageEntry.getInitialComponent()))
								for (AssignedBuySideTradeCaptureTarget assignedBuySideTradeCaptureTarget2 : buySideBook.getAssignedTradeCaptureTargets()) {
									if (assignedBuySideTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent().getId()) {
										assignedBuySideTradeCaptureTarget2.setLastTradeId(buySideBookTradeCaptureReport.getCreatedTimestamp());
										try {
											basisPersistenceHandler.update(buySideBook);
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
			catch (Exception e) {
				log.error("Bug", e);
			}
		}

	}

	private synchronized void handleQuoteStatusReport(Message message) {

		try {

			if (message.getInt(297) == 11 || message.getInt(297) == 7) {

				BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntryMap.get(message.getString(131));

				if (buySideQuoteRequestEntry == null && message.isSetField(693)) {
					for (BuySideQuoteRequestEntry buySideQuoteRequestEntry2 : buySideQuoteRequestEntryMap.values()) {
						if (buySideQuoteRequestEntry2.getQuoteRespId() != null && buySideQuoteRequestEntry2.getQuoteRespId().equals(message.getString(693)))
							buySideQuoteRequestEntry = buySideQuoteRequestEntry2;
					}
				}

				if (buySideQuoteRequestEntry != null) {

					buySideQuoteRequestEntry
							.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.REJECTED);

					persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
				}
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	private synchronized void handleQuoteRequestReject(Message message) {

		try {

			BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntryMap.get(message.getString(131));

			if (buySideQuoteRequestEntry != null) {

				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.REJECTED);

				persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}
	}

	private synchronized void handleQuote(Message message) {

		try {

			BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntryMap.get(message.getString(131));

			if (buySideQuoteRequestEntry != null) {

				FSecurity security = basisPersistenceHandler.findById(FSecurity.class, buySideQuoteRequestEntry.getSecurity(), businessComponentHandler);

				buySideQuoteRequestEntry.setQuoteId(message.getString(117));

				if (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT
						|| !buySideQuoteRequestEntry.isDone()) {

					buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.QUOTED);

					Double value = null;

					if (message.getChar(54) == '1') {

						if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
								&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							value = message.getDouble(634);
						else
							value = message.getDouble(133);

						buySideQuoteRequestEntry.setOrderQuantity(message.getDouble(135));
					}
					else {
						if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
								&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							value = message.getDouble(632);
						else
							value = message.getDouble(132);
						buySideQuoteRequestEntry.setOrderQuantity(message.getDouble(134));
					}

					buySideQuoteRequestEntry.setLimit(value);

					if (message.isSetField(62))
						buySideQuoteRequestEntry.setSubjectDate(message.getUtcTimeStamp(62).getTime());
					else
						buySideQuoteRequestEntry.setSubjectDate(System.currentTimeMillis());

					if (isValidPrice(security, value) && isValidQuantity(security, buySideQuoteRequestEntry.getOrderQuantity()))
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					else {
						StringBuffer stringBuffer = new StringBuffer("System automatically canceled inquiry for security ");
						stringBuffer.append(security.getName());

						Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideQuoteRequestEntry.getCounterparty(),
								businessComponentHandler);

						if (counterparty != null) {

							stringBuffer.append(" from counterparty ");
							stringBuffer.append(counterparty.getName());
						}
						else {
							stringBuffer.append(" from unknown counterparty");
						}

						stringBuffer.append(". Reject reason was an invalid ");
						if (!isValidPrice(security, value))
							stringBuffer.append(" limit.");
						if (!isValidQuantity(security, buySideQuoteRequestEntry.getOrderQuantity()))
							stringBuffer.append(" quantity.");
						stringBuffer.append(" Please contact your counterparty.");
						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(buySideBook.getName());
						logEntry.setMessageText(stringBuffer.toString());
						if (buySideQuoteRequestEntry.getOrderId() != null)
							logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId() + ";;;" + buySideQuoteRequestEntry.getOrderId());
						else
							logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId());
						basisPersistenceHandler.writeLogEntry(logEntry);
						buySideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
						sendQuoteResponse(buySideQuoteRequestEntry);
					}

				}
				else {
					buySideQuoteRequestEntry
							.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING);
					persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					sendQuoteResponse(buySideQuoteRequestEntry);
				}
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#close()
	 */
	@Override
	public void close() {

		closed = true;
		super.close();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof BuySideBook) {
			buySideBook = (BuySideBook) abstractBusinessComponent;
		}

		assignedBuySideBookSecurities.clear();

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBook.getAssignedBuySideBookSecurities())
			assignedBuySideBookSecurities.put(assignedBuySideBookSecurity.getSecurity().getId(), assignedBuySideBookSecurity);

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		calendar.set(java.util.Calendar.MINUTE, 0);
		calendar.set(java.util.Calendar.SECOND, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);

		List<Object> parameters = new ArrayList<Object>();
		parameters.add(buySideBook.getId());
		parameters.add(calendar.getTimeInMillis());

		List<BuySideNewOrderSingle> newOrderSingleEntries = basisPersistenceHandler.executeQuery(BuySideNewOrderSingle.class,
				"select a from BuySideNewOrderSingle a where a.buySideBook.id=?1 and (a.createdTimestamp>=?2 or a.leaveQuantity>0)", parameters,
				businessComponentHandler, true);
		for (BuySideNewOrderSingle newOrderSingleEntry : newOrderSingleEntries) {
			BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = getBuySideNewOrderSingleEntry(newOrderSingleEntry);
			buySideNewOrderSingleEntryMap.put(buySideNewOrderSingleEntry.getOrderId(), buySideNewOrderSingleEntry);
		}

		List<BuySideQuoteRequest> buySideQuoteRequests = basisPersistenceHandler.executeQuery(BuySideQuoteRequest.class,
				"select a from BuySideQuoteRequest a where a.buySideBook.id=?1 and (a.createdTimestamp>=?2 or a.leaveQuantity>0)", parameters,
				businessComponentHandler, true);
		for (BuySideQuoteRequest buySideQuoteRequest : buySideQuoteRequests) {
			BuySideQuoteRequestEntry buySideQuoteRequestEntry = getBuySideQuoteRequestEntry(buySideQuoteRequest);
			buySideQuoteRequestEntryMap.put(buySideQuoteRequestEntry.getQuoteReqId(), buySideQuoteRequestEntry);
		}

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

		return buySideBook;
	}

	private void writeMDInputEntries() {

		if (closed)
			timer.cancel();

		Set<BuySideCounterpartyMDInputEntry> buySideMDInputEntries = new HashSet<BuySideCounterpartyMDInputEntry>();

		synchronized (periodicUpdateSet) {

			buySideMDInputEntries.addAll(periodicUpdateSet);
			periodicUpdateSet.clear();
		}

		Set<BuySideCompositeMDInputEntry> buySideCompositeMDInputEntries = new HashSet<BuySideCompositeMDInputEntry>();

		Set<AbstractBuySideMDInputEntry> abstractBuySideMDInputEntries = new HashSet<AbstractBuySideMDInputEntry>();

		for (BuySideCounterpartyMDInputEntry buySideMDInputEntry : buySideMDInputEntries) {

			synchronized (buySideMDInputEntry) {

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideMDInputEntry.getSecurityValue());

				if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

					Double bidPrice = getPrice(security, buySideMDInputEntry.getMdEntryBidYieldValue());

					if (buySideMDInputEntry.getMdEntryBidPxValue() == null)
						buySideMDInputEntry.setMdBidPriceDeltaValue(0d);
					else if (buySideMDInputEntry.getMdEntryBidPxValue() != bidPrice) {
						if (buySideBook.getAbsolutChange())
							buySideMDInputEntry.setMdBidPriceDeltaValue(bidPrice - buySideMDInputEntry.getMdEntryBidPxValue());
						else
							buySideMDInputEntry.setMdBidPriceDeltaValue(100d * (bidPrice - buySideMDInputEntry.getMdEntryBidPxValue())
									/ buySideMDInputEntry.getMdEntryBidPxValue());
					}

					buySideMDInputEntry.setMdEntryBidPxValue(bidPrice);

					Double askPrice = getPrice(security, buySideMDInputEntry.getMdEntryAskYieldValue());

					if (buySideMDInputEntry.getMdEntryAskPxValue() == null)
						buySideMDInputEntry.setMdAskPriceDeltaValue(0d);
					else if (buySideMDInputEntry.getMdEntryAskPxValue() != askPrice) {
						if (buySideBook.getAbsolutChange())
							buySideMDInputEntry.setMdAskPriceDeltaValue(askPrice - buySideMDInputEntry.getMdEntryAskPxValue());
						else
							buySideMDInputEntry.setMdAskPriceDeltaValue(100d * (askPrice - buySideMDInputEntry.getMdEntryAskPxValue())
									/ buySideMDInputEntry.getMdEntryAskPxValue());
					}
					buySideMDInputEntry.setMdEntryAskPxValue(askPrice);
				}
				else {
					buySideMDInputEntry.setMdEntryBidYieldValue(getYield(security, buySideMDInputEntry.getMdEntryBidPxValue()));
					buySideMDInputEntry.setMdEntryAskYieldValue(getYield(security, buySideMDInputEntry.getMdEntryAskPxValue()));
				}

				if (buySideMDInputEntry.getMdEntryBidPxValue() != null) {
					if (buySideMDInputEntry.getHighBidPxValue() == null || buySideMDInputEntry.getHighBidPxValue() < buySideMDInputEntry.getMdEntryBidPxValue())
						buySideMDInputEntry.setHighBidPxValue(buySideMDInputEntry.getMdEntryBidPxValue());

					if (buySideMDInputEntry.getLowBidPxValue() == null || buySideMDInputEntry.getLowBidPxValue() > buySideMDInputEntry.getMdEntryBidPxValue())
						buySideMDInputEntry.setLowBidPxValue(buySideMDInputEntry.getMdEntryBidPxValue());
				}

				if (buySideMDInputEntry.getMdEntryAskPxValue() != null) {
					if (buySideMDInputEntry.getHighAskPxValue() == null || buySideMDInputEntry.getHighAskPxValue() < buySideMDInputEntry.getMdEntryAskPxValue())
						buySideMDInputEntry.setHighAskPxValue(buySideMDInputEntry.getMdEntryAskPxValue());

					if (buySideMDInputEntry.getLowAskPxValue() == null || buySideMDInputEntry.getLowAskPxValue() > buySideMDInputEntry.getMdEntryAskPxValue())
						buySideMDInputEntry.setLowAskPxValue(buySideMDInputEntry.getMdEntryAskPxValue());
				}

				abstractBuySideMDInputEntries.add(buySideMDInputEntry.clone());

				BuySideCompositeMDInputEntry buySideCompositeMDInputEntry = securityCompositeEntries.get(buySideMDInputEntry.getSecurityValue());
				if (buySideCompositeMDInputEntry == null) {
					buySideCompositeMDInputEntry = new BuySideCompositeMDInputEntry();
					buySideCompositeMDInputEntry.setSecurityValue(buySideMDInputEntry.getSecurityValue());
					securityCompositeEntries.put(buySideMDInputEntry.getSecurityValue(), buySideCompositeMDInputEntry);
				}
				else {
					BuySideCompositeMDInputEntry historicalBuySideCompositeMDInputEntry = buySideCompositeMDInputEntry.clone();
					List<BuySideCompositeMDInputEntry> historicalEntries = historicalData.get(historicalBuySideCompositeMDInputEntry.getSecurityValue());
					if (historicalEntries == null) {
						historicalEntries = new ArrayList<BuySideCompositeMDInputEntry>();
						historicalData.put(historicalBuySideCompositeMDInputEntry.getSecurityValue(), historicalEntries);
					}
					historicalEntries.add(historicalBuySideCompositeMDInputEntry);
				}
				buySideCompositeMDInputEntries.add(buySideCompositeMDInputEntry);
			}
		}

		for (BuySideCompositeMDInputEntry buySideCompositeMDInputEntry : buySideCompositeMDInputEntries) {

			FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideCompositeMDInputEntry.getSecurityValue());

			BigDecimal yieldTickSize = new BigDecimal("0.0001");

			BigDecimal priceTickSize = new BigDecimal("0.001");

			BuySideCompositeMDInputEntry historicalEntry = null;

			long timestamp = System.currentTimeMillis();

			if (security != null) {
				if (security.getSecurityDetails().getMinPriceIncrement() != null) {

					if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
						yieldTickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
					else
						priceTickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
				}
				List<BuySideCompositeMDInputEntry> historicalList = historicalData.get(security.getId());
				if (historicalList != null)
					for (int i = historicalList.size() - 1; i >= 0; i--) {
						BuySideCompositeMDInputEntry buySideCompositeMDInputEntry2 = historicalList.get(i);
						if (buySideCompositeMDInputEntry2.getTime() < timestamp - 1000) {

							historicalEntry = buySideCompositeMDInputEntry2;
							i = -1;
						}
					}
			}

			Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(buySideCompositeMDInputEntry.getSecurityValue());
			BigDecimal bidSizeCount = null;
			BigDecimal askSizeCount = null;
			BigDecimal bidPx = null;
			BigDecimal askPx = null;
			BigDecimal bidYield = null;
			BigDecimal askYield = null;

			List<BuySideCounterpartyMDInputEntry> list = new ArrayList<BuySideCounterpartyMDInputEntry>(counterpartyMap.values());
			for (BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry : list) {
				synchronized (buySideCounterpartyMDInputEntry) {

					if (buySideCounterpartyMDInputEntry.getMdEntryBidSizeValue() != null) {
						BigDecimal bidSize = new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryBidSizeValue()));
						if (bidSizeCount == null)
							bidSizeCount = bidSize;
						else
							bidSizeCount = bidSizeCount.add(bidSize);

						if (buySideCounterpartyMDInputEntry.getMdEntryBidPxValue() != null) {
							if (bidPx == null)
								bidPx = bidSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryBidPxValue())));
							else
								bidPx = bidPx
										.add(bidSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryBidPxValue()))));
						}

						if (buySideCounterpartyMDInputEntry.getMdEntryBidYieldValue() != null) {
							if (bidYield == null)
								bidYield = bidSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryBidYieldValue())));
							else
								bidYield = bidYield.add(bidSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry
										.getMdEntryBidYieldValue()))));
						}

					}

					if (buySideCounterpartyMDInputEntry.getMdEntryAskSizeValue() != null) {

						BigDecimal askSize = new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryAskSizeValue()));
						if (askSizeCount == null)
							askSizeCount = askSize;
						else
							askSizeCount = askSizeCount.add(askSize);

						if (buySideCounterpartyMDInputEntry.getMdEntryAskPxValue() != null) {
							if (askPx == null)
								askPx = askSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryAskPxValue())));
							else
								askPx = askPx
										.add(askSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryAskPxValue()))));
						}

						if (buySideCounterpartyMDInputEntry.getMdEntryAskYieldValue() != null) {
							if (askYield == null)
								askYield = askSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry.getMdEntryAskYieldValue())));
							else
								askYield = askYield.add(askSize.multiply(new BigDecimal(decimalFormat.format(buySideCounterpartyMDInputEntry
										.getMdEntryAskYieldValue()))));
						}
					}

				}
			}

			if (bidSizeCount != null)
				buySideCompositeMDInputEntry.setMdEntryBidSizeValue(bidSizeCount.doubleValue());
			else
				buySideCompositeMDInputEntry.setMdEntryBidSizeValue(null);

			if (askSizeCount != null)
				buySideCompositeMDInputEntry.setMdEntryAskSizeValue(askSizeCount.doubleValue());
			else
				buySideCompositeMDInputEntry.setMdEntryAskSizeValue(null);

			if (bidPx != null && bidSizeCount != null && bidSizeCount.doubleValue() != 0) {

				BigDecimal tickSize = new BigDecimal("0.001");
				if (security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {
					tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
				}

				bidPx = bidPx.divide(bidSizeCount, RoundingMode.HALF_EVEN).divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);
				;
				if (buySideCompositeMDInputEntry.getMdEntryBidPxValue() == null)
					buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(0d);

				else if (historicalEntry != null && historicalEntry.getMdEntryBidPxValue() != null
						&& bidPx.doubleValue() != historicalEntry.getMdEntryBidPxValue()) {
					if (buySideBook.getAbsolutChange())
						buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(bidPx.doubleValue() - historicalEntry.getMdEntryBidPxValue());
					else
						buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(100d * (bidPx.doubleValue() - historicalEntry.getMdEntryBidPxValue())
								/ historicalEntry.getMdEntryBidPxValue());
				}
				else
					buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(0d);

				buySideCompositeMDInputEntry.setMdEntryBidPxValue(bidPx.doubleValue());

				if (buySideCompositeMDInputEntry.getHighBidPxValue() == null || buySideCompositeMDInputEntry.getHighBidPxValue() < bidPx.doubleValue())
					buySideCompositeMDInputEntry.setHighBidPxValue(bidPx.doubleValue());

				if (buySideCompositeMDInputEntry.getLowBidPxValue() == null || buySideCompositeMDInputEntry.getLowBidPxValue() > bidPx.doubleValue())
					buySideCompositeMDInputEntry.setLowBidPxValue(bidPx.doubleValue());

			}
			else {
				buySideCompositeMDInputEntry.setMdEntryBidPxValue(null);
				buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(null);
			}

			if (bidYield != null && bidSizeCount != null && bidSizeCount.doubleValue() != 0) {
				bidYield = bidYield.divide(bidSizeCount, RoundingMode.HALF_EVEN).divide(yieldTickSize).setScale(0, RoundingMode.UP).multiply(yieldTickSize);
				buySideCompositeMDInputEntry.setMdEntryBidYieldValue(bidYield.doubleValue());
			}
			else {
				buySideCompositeMDInputEntry.setMdEntryBidYieldValue(null);
			}

			if (askPx != null && askSizeCount != null && askSizeCount.doubleValue() != 0) {
				askPx = askPx.divide(askSizeCount, RoundingMode.HALF_EVEN).divide(priceTickSize).setScale(0, RoundingMode.UP).multiply(priceTickSize);

				if (buySideCompositeMDInputEntry.getMdEntryAskPxValue() == null)
					buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(0d);

				else if (historicalEntry != null && historicalEntry.getMdEntryAskPxValue() != null
						&& askPx.doubleValue() != historicalEntry.getMdEntryAskPxValue()) {
					if (buySideBook.getAbsolutChange())
						buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(askPx.doubleValue() - historicalEntry.getMdEntryAskPxValue());
					else
						buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(100d * (askPx.doubleValue() - historicalEntry.getMdEntryAskPxValue())
								/ historicalEntry.getMdEntryAskPxValue());
				}
				else
					buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(0d);

				buySideCompositeMDInputEntry.setMdEntryAskPxValue(askPx.doubleValue());

				if (buySideCompositeMDInputEntry.getHighAskPxValue() == null || buySideCompositeMDInputEntry.getHighAskPxValue() < askPx.doubleValue())
					buySideCompositeMDInputEntry.setHighAskPxValue(askPx.doubleValue());

				if (buySideCompositeMDInputEntry.getLowAskPxValue() == null || buySideCompositeMDInputEntry.getLowAskPxValue() > askPx.doubleValue())
					buySideCompositeMDInputEntry.setLowAskPxValue(askPx.doubleValue());

			}
			else {
				buySideCompositeMDInputEntry.setMdEntryAskPxValue(null);
				buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(null);
			}

			if (askYield != null && askSizeCount != null && askSizeCount.doubleValue() != 0) {
				askYield = askYield.divide(askSizeCount, RoundingMode.HALF_EVEN).divide(yieldTickSize).setScale(0, RoundingMode.DOWN).multiply(yieldTickSize);
				buySideCompositeMDInputEntry.setMdEntryAskYieldValue(askYield.doubleValue());
			}
			else {
				buySideCompositeMDInputEntry.setMdEntryAskYieldValue(null);
			}

			buySideCompositeMDInputEntry.setTime(timestamp);

			abstractBuySideMDInputEntries.add(buySideCompositeMDInputEntry);

		}

		for (Channel channel : quotepageSessions.keySet()) {
			List<AbstractBuySideMDInputEntry> abstractBuySideMDInputEntries2 = new ArrayList<AbstractBuySideMDInputEntry>();
			Map<BuySideQuotePage, Long> map = quotepageSessions.get(channel);
			if (map != null)
				for (BuySideQuotePage buySideQuotePage : map.keySet()) {
					for (AbstractBuySideMDInputEntry abstractBuySideMDInputEntry : abstractBuySideMDInputEntries) {
						AssignedBuySideBookSecurity assignedBuySideBookSecurity = assignedBuySideBookSecurities.get(abstractBuySideMDInputEntry
								.getSecurityValue());
						if (assignedBuySideBookSecurity != null && buySideQuotePage.getAssignedBuySideBookSecurities().contains(assignedBuySideBookSecurity)) {
							abstractBuySideMDInputEntries2.add(abstractBuySideMDInputEntry);
						}
					}
				}
			if (abstractBuySideMDInputEntries2.size() > 0) {
				basisPersistenceHandler.send(new UpdateBuySideMDInputEntryResponse(abstractBuySideMDInputEntries2), channel);
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	protected void processFIXMessage(List<MessageEntry> messages) {

		Set<BuySideCounterpartyMDInputEntry> mdInputEntries = new HashSet<BuySideCounterpartyMDInputEntry>();
		for (MessageEntry messageEntry : messages) {
			try {
				AbstractBusinessComponent marketBusinessComponent = messageEntry.getInitialComponent();
				if (marketBusinessComponent instanceof FIXInitiator) {
					FIXInitiator fixInitiator = (FIXInitiator) marketBusinessComponent;
					if (fixInitiator.getRoute() != null)
						marketBusinessComponent = fixInitiator.getRoute();
				}

				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);

				if (messageType.equals("W")) {

					if (message.isSetField(48) && message.getGroupCount(453) > 0) {
						String securityID = message.getString(48);
						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID);
						Counterparty counterparty = null;
						for (int j = 1; j <= message.getGroupCount(453); j++) {
							Group partyIDs = message.getGroup(j, 453);
							if (partyIDs.isSetField(448)) {
								String partyId = partyIDs.getString(448);
								String partyIdSource = null;
								Integer partyRole = null;
								if (partyIDs.isSetField(447))
									partyIdSource = partyIDs.getString(447);
								if (partyIDs.isSetField(452))
									partyRole = partyIDs.getInt(452);
								Counterparty counterparty2 = businessComponentHandler.getCounterparty(messageEntry.getInitialComponent().getId(), partyId,
										partyIdSource, partyRole);
								if (counterparty2 != null) {
									counterparty = counterparty2;
									j = message.getGroupCount(453) + 1;
								}
							}
						}

						if (security != null && counterparty != null) {

							Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(security.getId());
							if (counterpartyMap == null) {
								counterpartyMap = Collections.synchronizedMap(new HashMap<String, BuySideCounterpartyMDInputEntry>());
								securityCounterpartyEntries.put(security.getId(), counterpartyMap);
							}

							BuySideCounterpartyMDInputEntry mdInputEntry = counterpartyMap.get(marketBusinessComponent.getId() + "-" + counterparty.getId());
							if (mdInputEntry == null) {
								mdInputEntry = new BuySideCounterpartyMDInputEntry();
								mdInputEntry.setSecurityValue(security.getId());
								mdInputEntry.setCounterpartyValue(counterparty.getId());
								mdInputEntry.setInterfaceValue(marketBusinessComponent.getId());
								counterpartyMap.put(marketBusinessComponent.getId() + "-" + counterparty.getId(), mdInputEntry);
							}

							synchronized (mdInputEntry) {
								mdInputEntry.setTime(System.currentTimeMillis());

								for (int i = 1; i <= message.getGroupCount(268); i++) {
									Group mdEntries = message.getGroup(i, 268);
									if (mdEntries.isSetField(269)) {
										String mdEntryType = mdEntries.getString(269);
										if (mdEntryType.equals("0") || mdEntryType.equals("1")) {
											boolean bid = false;
											if (mdEntryType.equals("0"))
												bid = true;
											if (mdEntries.isSetField(270)) {

												BigDecimal value = new BigDecimal(mdEntries.getString(270));

												if (security != null && security.getSecurityDetails() != null
														&& security.getSecurityDetails().getPriceQuoteMethod() != null
														&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

													BigDecimal tickSize = new BigDecimal("0.0001");
													if (security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {
														tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
													}

													if (bid) {

														value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);
														mdInputEntry.setMdEntryBidYieldValue(value.doubleValue());
													}
													else {

														value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);
														mdInputEntry.setMdEntryAskYieldValue(value.doubleValue());
													}
												}
												else {

													BigDecimal tickSize = new BigDecimal("0.001");
													if (security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {
														tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
													}

													if (bid) {

														value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);

														if (mdInputEntry.getMdEntryBidPxValue() == null)
															mdInputEntry.setMdBidPriceDeltaValue(0d);
														else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue()) {
															if (buySideBook.getAbsolutChange())
																mdInputEntry.setMdBidPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryBidPxValue());
															else
																mdInputEntry.setMdBidPriceDeltaValue(100d
																		* (value.doubleValue() - mdInputEntry.getMdEntryBidPxValue())
																		/ mdInputEntry.getMdEntryBidPxValue());
														}
														mdInputEntry.setMdEntryBidPxValue(value.doubleValue());
													}
													else {

														value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);

														if (mdInputEntry.getMdEntryAskPxValue() == null)
															mdInputEntry.setMdAskPriceDeltaValue(0d);
														else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue()) {
															if (buySideBook.getAbsolutChange())
																mdInputEntry.setMdAskPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryAskPxValue());
															else
																mdInputEntry.setMdAskPriceDeltaValue(100d
																		* (value.doubleValue() - mdInputEntry.getMdEntryAskPxValue())
																		/ mdInputEntry.getMdEntryAskPxValue());
														}
														mdInputEntry.setMdEntryAskPxValue(value.doubleValue());
													}
												}
												mdInputEntries.add(mdInputEntry);
											}
											if (mdEntries.isSetField(271)) {
												if (bid) {
													mdInputEntry.setMdEntryBidSizeValue(mdEntries.getDouble(271));
												}
												else
													mdInputEntry.setMdEntryAskSizeValue(mdEntries.getDouble(271));
												mdInputEntries.add(mdInputEntry);
											}

										}
									}
								}
							}
						}
					}
				}
				else if (messageType.equals("X")) {
					Map<String, Group> refMap = refIDMap.get(messageEntry.getInitialComponent().getId());
					if (refMap == null) {
						refMap = new HashMap<String, Group>();
						refIDMap.put(messageEntry.getInitialComponent().getId(), refMap);
					}
					for (int i = 1; i <= message.getGroupCount(268); i++) {
						Group mdEntries = message.getGroup(i, 268);
						Group refMdEntry = mdEntries;
						if (mdEntries.isSetField(279)) {
							String updateAction = mdEntries.getString(279);
							if (updateAction.equals("0")) {
								if (mdEntries.isSetField(278))
									refMap.put(mdEntries.getString(278), mdEntries);
							}
							else {
								Group refMdEntry2 = null;
								if (mdEntries.isSetField(278))
									refMdEntry2 = refMap.get(mdEntries.getString(278));
								if (refMdEntry2 != null) {
									refMdEntry = refMdEntry2;
									if (updateAction.equals("2") || updateAction.equals("3") || updateAction.equals("4")) {
										refMap.remove(mdEntries.getString(278));
									}
								}
								else if (mdEntries.isSetField(280)) {
									Group refMdEntry3 = refMap.get(mdEntries.getString(280));
									if (refMdEntry3 != null) {
										refMdEntry = refMdEntry3;
										if (updateAction.equals("2") || updateAction.equals("3") || updateAction.equals("4")) {
											refMap.remove(mdEntries.getString(280));
										}
									}
								}
							}

							if (refMdEntry.isSetField(48)) {

								String securityID = refMdEntry.getString(48);
								FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID);
								Counterparty counterparty = null;

								for (int j = 1; j <= refMdEntry.getGroupCount(453); j++) {
									Group partyIDs = refMdEntry.getGroup(j, 453);
									if (partyIDs.isSetField(448)) {
										String partyId = partyIDs.getString(448);

										String partyIdSource = null;
										Integer partyRole = null;
										if (partyIDs.isSetField(447))
											partyIdSource = partyIDs.getString(447);
										if (partyIDs.isSetField(452))
											partyRole = partyIDs.getInt(452);
										Counterparty counterparty2 = businessComponentHandler.getCounterparty(messageEntry.getInitialComponent().getId(),
												partyId, partyIdSource, partyRole);

										if (counterparty2 != null) {
											counterparty = counterparty2;
											j = message.getGroupCount(453) + 1;
										}
									}
								}

								if (security != null && counterparty != null) {

									Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(security.getId());
									if (counterpartyMap == null) {
										counterpartyMap = Collections.synchronizedMap(new HashMap<String, BuySideCounterpartyMDInputEntry>());
										securityCounterpartyEntries.put(security.getId(), counterpartyMap);
									}
									BuySideCounterpartyMDInputEntry mdInputEntry = counterpartyMap.get(marketBusinessComponent.getId() + "-"
											+ counterparty.getId());
									if (mdInputEntry == null) {
										mdInputEntry = new BuySideCounterpartyMDInputEntry();
										mdInputEntry.setSecurityValue(security.getId());
										mdInputEntry.setCounterpartyValue(counterparty.getId());
										mdInputEntry.setInterfaceValue(marketBusinessComponent.getId());
										counterpartyMap.put(marketBusinessComponent.getId() + "-" + counterparty.getId(), mdInputEntry);
									}

									synchronized (mdInputEntry) {

										mdInputEntry.setTime(System.currentTimeMillis());

										if (refMdEntry.isSetField(269)) {
											String mdEntryType = refMdEntry.getString(269);
											if (mdEntryType.equals("0") || mdEntryType.equals("1")) {
												boolean bid = false;
												if (mdEntryType.equals("0"))
													bid = true;
												if (updateAction.equals("2") || updateAction.equals("3") || updateAction.equals("4")) {

													if (bid) {
														mdInputEntry.setMdBidPriceDeltaValue(null);
														mdInputEntry.setMdEntryBidPxValue(null);
														mdInputEntry.setMdEntryBidSizeValue(null);
														mdInputEntry.setMdEntryBidYieldValue(null);
													}
													else {
														mdInputEntry.setMdAskPriceDeltaValue(null);
														mdInputEntry.setMdEntryAskPxValue(null);
														mdInputEntry.setMdEntryAskSizeValue(null);
														mdInputEntry.setMdEntryAskYieldValue(null);

													}
													mdInputEntries.add(mdInputEntry);
												}
												else {
													if (mdEntries.isSetField(270)) {

														BigDecimal value = new BigDecimal(mdEntries.getString(270));

														if (security != null && security.getSecurityDetails() != null
																&& security.getSecurityDetails().getPriceQuoteMethod() != null
																&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

															BigDecimal tickSize = new BigDecimal("0.0001");
															if (security.getSecurityDetails() != null
																	&& security.getSecurityDetails().getMinPriceIncrement() != null) {
																tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails()
																		.getMinPriceIncrement()));
															}

															if (bid) {

																value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);
																mdInputEntry.setMdEntryBidYieldValue(value.doubleValue());
															}
															else {

																value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);
																mdInputEntry.setMdEntryAskYieldValue(value.doubleValue());
															}
														}
														else {

															BigDecimal tickSize = new BigDecimal("0.001");
															if (security.getSecurityDetails() != null
																	&& security.getSecurityDetails().getMinPriceIncrement() != null) {
																tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails()
																		.getMinPriceIncrement()));
															}

															if (bid) {

																value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);

																if (mdInputEntry.getMdEntryBidPxValue() == null)
																	mdInputEntry.setMdBidPriceDeltaValue(0d);
																else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue()) {
																	if (buySideBook.getAbsolutChange())
																		mdInputEntry.setMdBidPriceDeltaValue(value.doubleValue()
																				- mdInputEntry.getMdEntryBidPxValue());
																	else
																		mdInputEntry.setMdBidPriceDeltaValue(100d
																				* (value.doubleValue() - mdInputEntry.getMdEntryBidPxValue())
																				/ mdInputEntry.getMdEntryBidPxValue());
																}
																mdInputEntry.setMdEntryBidPxValue(value.doubleValue());
															}
															else {

																value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);

																if (mdInputEntry.getMdEntryAskPxValue() == null)
																	mdInputEntry.setMdAskPriceDeltaValue(0d);
																else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue()) {
																	if (buySideBook.getAbsolutChange())
																		mdInputEntry.setMdAskPriceDeltaValue(value.doubleValue()
																				- mdInputEntry.getMdEntryAskPxValue());
																	else
																		mdInputEntry.setMdAskPriceDeltaValue(100d
																				* (value.doubleValue() - mdInputEntry.getMdEntryAskPxValue())
																				/ mdInputEntry.getMdEntryAskPxValue());
																}
																mdInputEntry.setMdEntryAskPxValue(value.doubleValue());
															}
														}
														mdInputEntries.add(mdInputEntry);
													}
													if (mdEntries.isSetField(271)) {
														if (bid) {
															mdInputEntry.setMdEntryBidSizeValue(mdEntries.getDouble(271));
														}
														else
															mdInputEntry.setMdEntryAskSizeValue(mdEntries.getDouble(271));
														mdInputEntries.add(mdInputEntry);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				else if (messageType.equals("5")) {
					Map<String, Group> refMap = refIDMap.remove(messageEntry.getInitialComponent().getId());
					if (refMap != null) {
						for (Group refMdEntry : refMap.values()) {
							if (refMdEntry.isSetField(48)) {
								String securityID = refMdEntry.getString(48);
								FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID);
								Counterparty counterparty = null;
								for (int j = 1; j <= refMdEntry.getGroupCount(453); j++) {
									Group partyIDs = refMdEntry.getGroup(j, 453);
									if (partyIDs.isSetField(448)) {
										String partyId = partyIDs.getString(448);
										String partyIdSource = null;
										Integer partyRole = null;
										if (partyIDs.isSetField(447))
											partyIdSource = partyIDs.getString(447);
										if (partyIDs.isSetField(452))
											partyRole = partyIDs.getInt(452);
										Counterparty counterparty2 = businessComponentHandler.getCounterparty(messageEntry.getInitialComponent().getId(),
												partyId, partyIdSource, partyRole);
										if (counterparty2 != null) {
											counterparty = counterparty2;
											j = message.getGroupCount(453) + 1;
										}
									}
								}
								if (security != null && counterparty != null) {
									Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(security.getId());
									if (counterpartyMap != null) {
										BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry = counterpartyMap.get(marketBusinessComponent.getId()
												+ "-" + counterparty.getId());
										if (buySideCounterpartyMDInputEntry != null) {
											synchronized (buySideCounterpartyMDInputEntry) {
												buySideCounterpartyMDInputEntry.setTime(System.currentTimeMillis());
												buySideCounterpartyMDInputEntry.setMdBidPriceDeltaValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryBidPxValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryBidSizeValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryBidYieldValue(null);
												buySideCounterpartyMDInputEntry.setMdAskPriceDeltaValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryAskPxValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryAskSizeValue(null);
												buySideCounterpartyMDInputEntry.setMdEntryAskYieldValue(null);
												mdInputEntries.add(buySideCounterpartyMDInputEntry);
											}
										}
									}
								}
							}
						}
					}
				}
				else {
					messageQueue.add(messageEntry);
				}

			}
			catch (FieldNotFound e) {
				log.error("Bug", e);

			}

			for (AbstractComponentHandler abstractComponentHandler : outputComponents) {

				MessageEntry messageEntry2 = new MessageEntry(messageEntry.getInitialComponent(), buySideBook, messageEntry.getMessage());
				abstractComponentHandler.addFIXMessage(messageEntry2);
			}
		}

		synchronized (periodicUpdateSet) {
			periodicUpdateSet.addAll(mdInputEntries);
		}
	}

	private synchronized void handleLogon(MessageEntry messageEntry) {

		for (BuySideNewOrderSingleEntry buySideNewOrderSingleEntry : buySideNewOrderSingleEntryMap.values()) {
			if (buySideNewOrderSingleEntry.getMarketInterface() == messageEntry.getInitialComponent().getId()) {
				if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW_PENDING)
					sendNewOrderSingle(buySideNewOrderSingleEntry);
				else if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL_PENDING)
					sendOrderCancel(buySideNewOrderSingleEntry);
				else if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REPLACE_PENDING)
					sendReplaceRequest(buySideNewOrderSingleEntry);
			}
		}

	}

	private synchronized void checkQuoteRequestExpiration() {

		long timestamp = System.currentTimeMillis();

		for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntryMap.values()) {

			if (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW_PENDING) {

				if (buySideQuoteRequestEntry.getExpireDate() <= timestamp) {
					buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED);

					persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, false);
				}
			}
			else if (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_ALL_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING
					|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.QUOTED) {

				if (buySideQuoteRequestEntry.getExpireDate() <= timestamp) {

					buySideQuoteRequestEntry
							.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED_PENDING);

					try {
						sendQuoteResponse(buySideQuoteRequestEntry);
					}
					catch (Exception e) {

						buySideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED);

						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, false);
					}
				}
			}
		}
	}

	private void addCounterpartyGroup(AbstractBusinessComponent abstractBusinessComponent, Counterparty counterparty, int partyRole, FieldMap fieldMap) {

		Group group = null;

		for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
			if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
				if (group == null) {
					group = new Group(453, 448, new int[] { 448, 447, 452 });
					group.setString(448, counterPartyPartyID.getPartyID());
					if (counterPartyPartyID.getPartyIDSource() != null)
						group.setChar(447, counterPartyPartyID.getPartyIDSource().charAt(0));
					group.setInt(452, partyRole);
				}
			}
			else if (counterPartyPartyID.getAbstractBusinessComponent().getId() == abstractBusinessComponent.getId()
					&& counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole().intValue() == partyRole) {
				group = new Group(453, 448, new int[] { 448, 447, 452 });
				group.setString(448, counterPartyPartyID.getPartyID());
				if (counterPartyPartyID.getPartyIDSource() != null)
					group.setChar(447, counterPartyPartyID.getPartyIDSource().charAt(0));
				group.setInt(452, partyRole);
			}
		}

		if (group != null)
			fieldMap.addGroup(group);
	}

	private void sendQuoteResponse(BuySideQuoteRequestEntry buySideQuoteRequestEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {

			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideQuoteRequestEntry.getMarketInterface()
					&& abstractComponentHandler.getStartLevel() == 2) {

				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "AJ");
				message.setString(117, buySideQuoteRequestEntry.getQuoteId());

				buySideQuoteRequestEntry.setQuoteRespId(decimalFormat3.format(basisPersistenceHandler.getId("QUOTERESPID")));
				message.setString(693, buySideQuoteRequestEntry.getQuoteRespId());

				if (buySideQuoteRequestEntry.getSide() == Side.BID) {
					message.setChar(54, '2');
					message.setDouble(134, buySideQuoteRequestEntry.getOrderQuantity());
					if (buySideQuoteRequestEntry.getLimit() != null)
						message.setDouble(132, buySideQuoteRequestEntry.getLimit());
				}
				else {
					message.setChar(54, '1');
					message.setDouble(135, buySideQuoteRequestEntry.getOrderQuantity());
					if (buySideQuoteRequestEntry.getLimit() != null)
						message.setDouble(133, buySideQuoteRequestEntry.getLimit());
				}

				message.setUtcTimeStamp(60, new Date());

				message.setDouble(38, buySideQuoteRequestEntry.getOrderQuantity());

				if (buySideQuoteRequestEntry.getMinQuantity() > 0)
					message.setDouble(110, buySideQuoteRequestEntry.getMinQuantity());

				message.setString(64, localMarketDateFormat.format(new Date(buySideQuoteRequestEntry.getSettlementDate())));

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideQuoteRequestEntry.getSecurity());

				if (security != null) {

					message.setString(55, "N/A");
					message.setString(48, security.getSecurityID());
					message.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								message.setString(48, alternativeSecurityID);
								message.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							message.addGroup(group);
						}
					}

					if (security.getSecurityDetails().getPriceQuoteMethod() != null) {
						if (security.getSecurityDetails().getPriceQuoteMethod().equals("PCTPAR"))
							message.setInt(423, 1);
						else if (security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							message.setInt(423, 9);
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideQuoteRequestEntry.getCounterparty(),
							businessComponentHandler);

					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, message);

						switch (buySideQuoteRequestEntry.getOrderStatus()) {

							case NEW_PENDING:
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW);
							case COUNTER_PENDING:
								message.setInt(694, 2);
								message.setString(11, buySideQuoteRequestEntry.getOrderId());
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER);
								break;
							case DONE_AWAY_PENDING:
								message.setInt(694, 5);
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY);
								break;

							case COVER_PENDING:
								message.setInt(694, 4);
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER);
								break;

							case EXPIRED_PENDING:
								message.setInt(694, 3);
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED);
								break;

							case HIT_LIFT_PENDING:
								message.setInt(694, 1);
								message.setString(11, buySideQuoteRequestEntry.getOrderId());
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT);
								break;

							case PASS_PENDING:
								message.setInt(694, 6);
								buySideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
								break;

							default:
								return;
						}

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, message);
						abstractComponentHandler.addFIXMessage(messageEntry);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					}
					else {
						buySideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					}
				}
				else {
					buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
					persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
				}
			}
		}

	}

	private void sendQuoteStatusReport(BuySideQuoteRequestEntry buySideQuoteRequestEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {

			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideQuoteRequestEntry.getMarketInterface()
					&& abstractComponentHandler.getStartLevel() == 2) {

				Message quoteStatusReport = new Message();

				quoteStatusReport.getHeader().setString(MsgType.FIELD, "AI");

				quoteStatusReport.setString(131, buySideQuoteRequestEntry.getQuoteReqId());
				if (buySideQuoteRequestEntry.getQuoteId() != null)
					quoteStatusReport.setString(117, buySideQuoteRequestEntry.getQuoteId());
				else
					quoteStatusReport.setString(117, "N/A");

				if (buySideQuoteRequestEntry.getQuoteRespId() != null)
					quoteStatusReport.setString(693, buySideQuoteRequestEntry.getQuoteRespId());

				quoteStatusReport.setInt(537, 1);

				if (buySideQuoteRequestEntry.getSubjectDate() != null)
					quoteStatusReport.setUtcTimeStamp(62, new Date(buySideQuoteRequestEntry.getSubjectDate()));

				quoteStatusReport.setDouble(38, buySideQuoteRequestEntry.getOrderQuantity());

				if (buySideQuoteRequestEntry.getMinQuantity() > 0)
					quoteStatusReport.setDouble(110, buySideQuoteRequestEntry.getMinQuantity());
				quoteStatusReport.setString(64, localMarketDateFormat.format(new Date(buySideQuoteRequestEntry.getSettlementDate())));

				quoteStatusReport.setUtcTimeStamp(60, new Date(buySideQuoteRequestEntry.getCreated()));

				if (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING) {
					quoteStatusReport.setInt(297, 11);
					buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
				}

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideQuoteRequestEntry.getSecurity());

				if (security != null) {
					quoteStatusReport.setString(55, "N/A");
					quoteStatusReport.setString(48, security.getSecurityID());
					quoteStatusReport.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								quoteStatusReport.setString(48, alternativeSecurityID);
								quoteStatusReport.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							quoteStatusReport.addGroup(group);
						}
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideQuoteRequestEntry.getCounterparty(),
							businessComponentHandler);

					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, quoteStatusReport);

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, quoteStatusReport);
						abstractComponentHandler.addFIXMessage(messageEntry);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);

					}

				}

			}
		}

	}

	private synchronized void handleDontKnowTrade(Message message) {

		try {
			BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = buySideNewOrderSingleEntryMap.get(message.getString(37));
			if (buySideNewOrderSingleEntry == null) {
				for (BuySideNewOrderSingleEntry buySideNewOrderSingleEntry2 : buySideNewOrderSingleEntryMap.values()) {
					if (message.getString(37).equals(buySideNewOrderSingleEntry2.getNewOrderId()))
						buySideNewOrderSingleEntry = buySideNewOrderSingleEntry2;
				}
			}
			if (buySideNewOrderSingleEntry != null) {

				StringBuffer stringBuffer = new StringBuffer();

				stringBuffer.append("System received reject for order ");

				FSecurity security = basisPersistenceHandler.findById(FSecurity.class, buySideNewOrderSingleEntry.getSecurity(), businessComponentHandler);
				String side = " from ";
				if (buySideNewOrderSingleEntry.getSide() == Side.ASK) {
					stringBuffer.append("buy ");
				}
				else {
					stringBuffer.append("sell ");
					side = " to ";
				}

				stringBuffer.append(decimalFormat2.format(buySideNewOrderSingleEntry.getOrderQuantity()));

				stringBuffer.append(" ");

				stringBuffer.append(security.getName());

				if (buySideNewOrderSingleEntry.getLimit() != null) {
					stringBuffer.append(" limit ");
					stringBuffer.append(decimalFormat.format(buySideNewOrderSingleEntry.getLimit()));
				}

				stringBuffer.append(side);

				Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(),
						businessComponentHandler);

				stringBuffer.append(counterparty.getName());

				switch (buySideNewOrderSingleEntry.getTimeInForce()) {
					case FILL_OR_KILL:
						stringBuffer.append(" fill or kill (settlement ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(").");
						break;
					case GOOD_TILL_CANCEL:
						stringBuffer.append(" good till cancel.");
						break;
					case GOOD_TILL_DATE:
						stringBuffer.append(" good till ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(".");
						break;
					case GOOD_TILL_DAY:
						stringBuffer.append(" good till day (settlement ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(").");
						break;
				}

				buySideNewOrderSingleEntry.setNewLimit(null);
				buySideNewOrderSingleEntry.setNewOrderId(null);
				buySideNewOrderSingleEntry.setNewOrderQuantity(null);
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
				buySideNewOrderSingleEntry.setLeaveQuantity(0);
				persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, false);

				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.FATAL);
				logEntry.setLogDate(new Date());
				logEntry.setHighlightKey(buySideNewOrderSingleEntry.getOrderId());
				logEntry.setMessageComponent(buySideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				basisPersistenceHandler.writeLogEntry(logEntry);

			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);

		}
	}

	private synchronized void handleOrderCancelReject(Message message) {

		try {
			BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = buySideNewOrderSingleEntryMap.get(message.getString(11));
			if (buySideNewOrderSingleEntry == null && message.isSetField(41))
				buySideNewOrderSingleEntry = buySideNewOrderSingleEntryMap.get(message.getString(41));
			if (buySideNewOrderSingleEntry != null) {

				StringBuffer stringBuffer = new StringBuffer();

				stringBuffer.append("System received reject for ");

				if (message.getString(434).equals("1"))
					stringBuffer.append(" cancellation of order ");
				else
					stringBuffer.append(" replacement of order ");

				FSecurity security = basisPersistenceHandler.findById(FSecurity.class, buySideNewOrderSingleEntry.getSecurity(), businessComponentHandler);
				String side = " from ";
				if (buySideNewOrderSingleEntry.getSide() == Side.ASK) {
					stringBuffer.append("buy ");
				}
				else {
					stringBuffer.append("sell ");
					side = " to ";
				}

				stringBuffer.append(decimalFormat2.format(buySideNewOrderSingleEntry.getOrderQuantity()));

				stringBuffer.append(" ");

				stringBuffer.append(security.getName());

				if (buySideNewOrderSingleEntry.getLimit() != null) {
					stringBuffer.append(" limit ");
					stringBuffer.append(decimalFormat.format(buySideNewOrderSingleEntry.getLimit()));
				}

				stringBuffer.append(side);

				Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(),
						businessComponentHandler);

				stringBuffer.append(counterparty.getName());

				switch (buySideNewOrderSingleEntry.getTimeInForce()) {
					case FILL_OR_KILL:
						stringBuffer.append(" fill or kill (settlement ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(").");
						break;
					case GOOD_TILL_CANCEL:
						stringBuffer.append(" good till cancel.");
						break;
					case GOOD_TILL_DATE:
						stringBuffer.append(" good till ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(".");
						break;
					case GOOD_TILL_DAY:
						stringBuffer.append(" good till day (settlement ");
						stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						stringBuffer.append(").");
						break;
				}

				if (message.getString(11).equals(buySideNewOrderSingleEntry.getNewOrderId())) {
					buySideNewOrderSingleEntry.setNewLimit(null);
					buySideNewOrderSingleEntry.setNewOrderId(null);
					buySideNewOrderSingleEntry.setNewOrderQuantity(null);
				}
				if (message.getString(39).equals("1"))
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
				else if (message.getString(39).equals("2"))
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED);
				else if (message.getString(39).equals("4"))
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
				else if (message.getString(39).equals("3"))
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
				else if (message.getString(39).equals("0"))
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW);
				else
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
				persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, false);

				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.WARNING);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(buySideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(buySideNewOrderSingleEntry.getOrderId());
				basisPersistenceHandler.writeLogEntry(logEntry);

			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);

		}
	}

	private void sendOrderCancel(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {
			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideNewOrderSingleEntry.getMarketInterface()) {
				if (buySideNewOrderSingleEntry.getCounterpartyOrderId() == null)
					return;
				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "F");
				message.setString(37, buySideNewOrderSingleEntry.getCounterpartyOrderId());
				message.setString(11, buySideNewOrderSingleEntry.getOrderId());
				if (buySideNewOrderSingleEntry.getOriginalOrderId() != null)
					message.setString(41, buySideNewOrderSingleEntry.getOriginalOrderId());
				else
					message.setString(41, buySideNewOrderSingleEntry.getOrderId());
				message.setUtcTimeStamp(60, new Date());
				if (buySideNewOrderSingleEntry.getSide() == Side.BID)
					message.setChar(54, '2');
				else
					message.setChar(54, '1');
				message.setUtcTimeStamp(60, new Date());
				message.setDouble(38, buySideNewOrderSingleEntry.getOrderQuantity());

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideNewOrderSingleEntry.getSecurity());

				if (security != null) {

					message.setString(55, "N/A");
					message.setString(48, security.getSecurityID());
					message.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								message.setString(48, alternativeSecurityID);
								message.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							message.addGroup(group);
						}
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(),
							businessComponentHandler);
					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, message);

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, message);
						abstractComponentHandler.addFIXMessage(messageEntry);
					}
					else {
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
						persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
					}
				}
				else {
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
			}
		}

	}

	private Trader getTrader(Counterparty counterparty, String traderId, String traderIdSource, AbstractBuySideEntry abstractBuySideEntry) {

		Trader trader = null;

		AbstractBusinessComponent abstractBusinessComponent = null;
		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractBuySideEntry.getMarketInterface());

		if (abstractComponentHandler != null)
			abstractBusinessComponent = abstractComponentHandler.getAbstractBusinessComponent();

		if (counterparty != null && traderId != null && traderIdSource != null) {

			List<Trader> traders = basisPersistenceHandler.executeQuery(Trader.class, "select t from Trader t where t.parent=?1", counterparty,
					businessComponentHandler, true);

			for (Trader trader2 : traders) {
				for (TraderPartyID traderPartyID : trader2.getTraderPartyIDs()) {
					if (traderPartyID.getAbstractBusinessComponent() == null) {
						if (trader == null && traderPartyID.getPartyIDSource() != null && traderPartyID.getPartyIDSource().equals(traderIdSource)
								&& traderId.equals(traderPartyID.getPartyID()))
							trader = trader2;
					}
					else if (traderPartyID.getAbstractBusinessComponent().getId() == abstractBuySideEntry.getMarketInterface()
							&& traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole().intValue() == 12
							&& traderPartyID.getPartyIDSource() != null && traderPartyID.getPartyIDSource().equals(traderIdSource)
							&& traderId.equals(traderPartyID.getPartyID()))
						trader = trader2;
				}
			}

			if (trader == null) {
				for (Trader trader2 : traders) {
					for (TraderPartyID traderPartyID : trader2.getTraderPartyIDs()) {
						if (traderPartyID.getPartyIDSource() != null && traderPartyID.getPartyIDSource().equals(traderIdSource)
								&& traderId.equals(traderPartyID.getPartyID()))
							trader = trader2;
					}
				}

				if (trader != null) {

					StringBuffer stringBuffer = new StringBuffer("Trader ");
					stringBuffer.append(trader.getName());
					stringBuffer.append(" for counterparty ");
					stringBuffer.append(counterparty.getName());
					stringBuffer
							.append(" has the same party id but no party role \"Executing Trader\" defined. System creates this role. Please double-check this entry.");
					LogEntry logEntry = new LogEntry();
					logEntry.setLogLevel(Level.WARNING);
					logEntry.setLogDate(new Date());
					logEntry.setMessageComponent(buySideBook.getName());
					logEntry.setMessageText(stringBuffer.toString());
					logEntry.setHighlightKey(abstractBuySideEntry.getOrderId());
					basisPersistenceHandler.writeLogEntry(logEntry);

					TraderPartyID executingTraderPartyID = new TraderPartyID();
					executingTraderPartyID.setPartyID(traderId);
					executingTraderPartyID.setPartyIDSource(traderIdSource);

					executingTraderPartyID.setAbstractBusinessComponent(abstractBusinessComponent);
					executingTraderPartyID.setPartyRole(12);
					executingTraderPartyID.setTrader(trader);
					trader.getTraderPartyIDs().add(executingTraderPartyID);
					List<AbstractBusinessObject> traders2 = new ArrayList<AbstractBusinessObject>();
					traders2.add(trader);

					basisPersistenceHandler.update(traders2, null, businessComponentHandler, true);

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
				logEntry.setMessageComponent(buySideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(abstractBuySideEntry.getOrderId());
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
				traderPartyID.setPartyRole(12);
				traderPartyID.setTrader(trader);
				traderPartyID.setAbstractBusinessComponent(abstractBusinessComponent);
				traderPartyIDs.add(traderPartyID);
				TraderPartyID traderPartyID2 = new TraderPartyID();
				traderPartyID2.setPartyID(traderId);
				traderPartyID2.setPartyIDSource(traderIdSource);
				traderPartyID2.setPartyRole(37);
				traderPartyID2.setTrader(trader);
				traderPartyID2.setAbstractBusinessComponent(abstractBusinessComponent);
				traderPartyIDs.add(traderPartyID2);
				trader.setTraderPartyIDs(traderPartyIDs);

				try {
					basisPersistenceHandler.persist(trader, null, businessComponentHandler);
				}
				catch (Exception e) {
					log.error("Bug", e);

				}
			}
		}

		return trader;
	}

	private synchronized void handleExecutionReport(Message message) {

		try {

			BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = buySideNewOrderSingleEntryMap.get(message.getString(11));
			if (buySideNewOrderSingleEntry == null && message.isSetField(41))
				buySideNewOrderSingleEntry = buySideNewOrderSingleEntryMap.get(message.getString(41));
			if (buySideNewOrderSingleEntry != null) {
				buySideNewOrderSingleEntry.setOriginalOrderId(null);
				if (message.getString(150).equals("0")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW);
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
				if (message.getString(150).equals("4")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
					buySideNewOrderSingleEntry.setLeaveQuantity(0);
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
				if (message.getString(150).equals("3")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
					buySideNewOrderSingleEntry.setLeaveQuantity(message.getDouble(151));
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
				if (message.getString(150).equals("5")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					buySideNewOrderSingleEntry.setOriginalOrderId(buySideNewOrderSingleEntry.getOrderId());
					buySideNewOrderSingleEntry.setOrderId(message.getString(11));
					buySideNewOrderSingleEntry.setOrderQuantity(message.getDouble(38));
					buySideNewOrderSingleEntry.setLeaveQuantity(message.getDouble(151));
					if (message.isSetField(44))
						buySideNewOrderSingleEntry.setLimit(message.getDouble(44));
					else
						buySideNewOrderSingleEntry.setLimit(null);

					if (buySideNewOrderSingleEntry.getLeaveQuantity() == 0)
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
					else if (message.getDouble(14) > 0)
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
					else
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW);

					buySideNewOrderSingleEntryMap.remove(buySideNewOrderSingleEntry.getOriginalOrderId());
					buySideNewOrderSingleEntryMap.put(buySideNewOrderSingleEntry.getOrderId(), buySideNewOrderSingleEntry);

					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
				if (message.getString(150).equals("8")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
					buySideNewOrderSingleEntry.setLeaveQuantity(0);
					buySideNewOrderSingleEntry.setLastPrice(null);
					buySideNewOrderSingleEntry.setLastQuantity(null);
					buySideNewOrderSingleEntry.setCumulativeQuantity(message.getDouble(14));
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
				if (message.getString(150).equals("F")) {
					buySideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(37));
					if (message.getString(39).equals("1"))
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
					else if (message.getString(39).equals("2"))
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED);

					FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideNewOrderSingleEntry.getSecurity());

					Double value = message.getDouble(31);

					if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

						if (message.isSetField(669))
							buySideNewOrderSingleEntry.setLastPrice(message.getDouble(669));

						buySideNewOrderSingleEntry.setLastYield(message.getDouble(31));

					}
					else
						buySideNewOrderSingleEntry.setLastPrice(message.getDouble(31));

					buySideNewOrderSingleEntry.setLastQuantity(message.getDouble(32));
					buySideNewOrderSingleEntry.setCumulativeQuantity(message.getDouble(14));
					buySideNewOrderSingleEntry.setLeaveQuantity(message.getDouble(151));
					BuySideNewOrderSingle buySideNewOrderSingle = persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);

					BuySideBookTradeCaptureReport buySideBookTradeCaptureReport = new BuySideBookTradeCaptureReport();
					buySideBookTradeCaptureReport.setCounterparty(buySideNewOrderSingle.getCounterparty());
					buySideBookTradeCaptureReport.setCounterpartyOrderId(buySideNewOrderSingleEntry.getCounterpartyOrderId());
					buySideBookTradeCaptureReport.setCreatedTimestamp(System.currentTimeMillis());
					buySideBookTradeCaptureReport.setCumulativeQuantity(buySideNewOrderSingleEntry.getCumulativeQuantity());
					buySideBookTradeCaptureReport.setfUser(buySideNewOrderSingle.getfUser());
					buySideBookTradeCaptureReport.setExecId(message.getString(17));
					buySideBookTradeCaptureReport.setLastPrice(buySideNewOrderSingleEntry.getLastPrice());
					buySideBookTradeCaptureReport.setLastYield(buySideNewOrderSingleEntry.getLastYield());
					buySideBookTradeCaptureReport.setLastQuantity(buySideNewOrderSingleEntry.getLastQuantity());
					buySideBookTradeCaptureReport.setLeaveQuantity(buySideNewOrderSingleEntry.getLeaveQuantity());
					buySideBookTradeCaptureReport.setOrderId(buySideNewOrderSingleEntry.getOrderId());
					buySideBookTradeCaptureReport.setOrderQuantity(buySideNewOrderSingleEntry.getOrderQuantity());
					buySideBookTradeCaptureReport.setSecurity(buySideNewOrderSingle.getSecurity());
					buySideBookTradeCaptureReport.setBuySideBook(buySideBook);
					if (message.isSetField(64))
						buySideBookTradeCaptureReport.setSettlementDate(message.getUtcDateOnly(64).getTime());
					if (buySideNewOrderSingleEntry.getSide() == Side.ASK)
						buySideBookTradeCaptureReport.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport.Side.BUY);
					else
						buySideBookTradeCaptureReport.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport.Side.SELL);
					buySideBookTradeCaptureReport.setSourceComponent(buySideNewOrderSingle.getMarketInterface());
					buySideBookTradeCaptureReport.setTradeId(decimalFormat3.format(basisPersistenceHandler.getId("TRADEID")));

					String traderId = null;
					String traderIdSource = null;

					for (int i = 1; i <= message.getGroupCount(453); i++) {

						Group parties = message.getGroup(i, 453);

						if (parties.isSetField(452) && parties.getInt(452) == 12) {

							if (parties.isSetField(447))
								traderIdSource = parties.getString(447);

							if (parties.isSetField(448))
								traderId = parties.getString(448);
						}
					}

					Counterparty counterparty = buySideNewOrderSingle.getCounterparty();

					Trader trader = getTrader(counterparty, traderId, traderIdSource, buySideNewOrderSingleEntry);

					buySideBookTradeCaptureReport.setTrader(trader);
					buySideBookTradeCaptureReport.setUpdatedTimestamp(System.currentTimeMillis());
					handleBuySideBookTradeCaptureReport(buySideBookTradeCaptureReport);

					StringBuffer stringBuffer = new StringBuffer();

					if (!isValidPrice(security, value)) {
						stringBuffer.append("System received execution report for security ");
						stringBuffer.append(security.getName());
						stringBuffer.append(" from counterparty ");
						stringBuffer.append(counterparty.getName());
						stringBuffer.append(" with an invalid price. The message was processed neverless, but you should contact your counterparty.");

					}
					else if (!isValidQuantity(security, buySideNewOrderSingle.getLastQuantity())) {
						stringBuffer.append("System received execution report for security ");
						stringBuffer.append(security.getName());
						stringBuffer.append(" from counterparty ");
						stringBuffer.append(counterparty.getName());
						stringBuffer.append(" with an invalid quantity. The message was processed neverless, but you should contact your counterparty.");
					}

					if (stringBuffer.length() > 0) {
						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(buySideBook.getName());
						logEntry.setMessageText(stringBuffer.toString());
						logEntry.setHighlightKey(buySideNewOrderSingle.getOrderId());
						basisPersistenceHandler.writeLogEntry(logEntry);
					}

				}
			}
			else {
				if (message.getString(150).equals("F") && message.getString(39).equals("2")) {

					BuySideQuoteRequestEntry buySideQuoteRequestEntry = null;

					for (BuySideQuoteRequestEntry buySideQuoteRequestEntry2 : buySideQuoteRequestEntryMap.values())
						if (buySideQuoteRequestEntry2.getOrderId() != null && buySideQuoteRequestEntry2.getOrderId().equals(message.getString(11)))
							buySideQuoteRequestEntry = buySideQuoteRequestEntry2;
					if (buySideQuoteRequestEntry != null) {

						buySideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.FILLED);

						FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideQuoteRequestEntry.getSecurity());

						Double value = null;

						if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

							if (message.isSetField(669))
								buySideQuoteRequestEntry.setLastPrice(message.getDouble(669));

							buySideQuoteRequestEntry.setLastYield(message.getDouble(31));
						}
						else
							buySideQuoteRequestEntry.setLastPrice(message.getDouble(31));

						value = message.getDouble(31);

						buySideQuoteRequestEntry.setLastQuantity(message.getDouble(32));
						buySideQuoteRequestEntry.setCumulativeQuantity(message.getDouble(14));
						buySideQuoteRequestEntry.setLeaveQuantity(message.getDouble(151));
						buySideQuoteRequestEntry.setCounterpartyOrderId(message.getString(37));

						BuySideQuoteRequest buySideNewOrderSingle = persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);

						BuySideBookTradeCaptureReport buySideBookTradeCaptureReport = new BuySideBookTradeCaptureReport();
						buySideBookTradeCaptureReport.setCounterparty(buySideNewOrderSingle.getCounterparty());
						buySideBookTradeCaptureReport.setCounterpartyOrderId(buySideQuoteRequestEntry.getCounterpartyOrderId());
						buySideBookTradeCaptureReport.setCreatedTimestamp(System.currentTimeMillis());
						buySideBookTradeCaptureReport.setCumulativeQuantity(buySideQuoteRequestEntry.getCumulativeQuantity());
						buySideBookTradeCaptureReport.setfUser(buySideNewOrderSingle.getfUser());
						buySideBookTradeCaptureReport.setExecId(message.getString(17));
						buySideBookTradeCaptureReport.setLastPrice(buySideQuoteRequestEntry.getLastPrice());
						buySideBookTradeCaptureReport.setLastYield(buySideQuoteRequestEntry.getLastYield());
						buySideBookTradeCaptureReport.setLastQuantity(buySideQuoteRequestEntry.getLastQuantity());
						buySideBookTradeCaptureReport.setLeaveQuantity(buySideQuoteRequestEntry.getLeaveQuantity());
						buySideBookTradeCaptureReport.setOrderId(buySideQuoteRequestEntry.getOrderId());
						buySideBookTradeCaptureReport.setOrderQuantity(buySideQuoteRequestEntry.getOrderQuantity());
						buySideBookTradeCaptureReport.setSecurity(buySideNewOrderSingle.getSecurity());
						buySideBookTradeCaptureReport.setBuySideBook(buySideBook);
						if (message.isSetField(64))
							buySideBookTradeCaptureReport.setSettlementDate(message.getUtcDateOnly(64).getTime());
						if (buySideQuoteRequestEntry.getSide() == Side.ASK)
							buySideBookTradeCaptureReport.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport.Side.BUY);
						else
							buySideBookTradeCaptureReport.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport.Side.SELL);
						buySideBookTradeCaptureReport.setSourceComponent(buySideNewOrderSingle.getMarketInterface());
						buySideBookTradeCaptureReport.setTradeId(decimalFormat3.format(basisPersistenceHandler.getId("TRADEID")));

						String traderId = null;
						String traderIdSource = null;

						for (int i = 1; i <= message.getGroupCount(453); i++) {

							Group parties = message.getGroup(i, 453);

							if (parties.isSetField(452) && parties.getInt(452) == 12) {

								if (parties.isSetField(447))
									traderIdSource = parties.getString(447);

								if (parties.isSetField(448))
									traderId = parties.getString(448);
							}
						}

						Counterparty counterparty = buySideNewOrderSingle.getCounterparty();

						Trader trader = getTrader(counterparty, traderId, traderIdSource, buySideQuoteRequestEntry);

						buySideBookTradeCaptureReport.setTrader(trader);
						buySideBookTradeCaptureReport.setUpdatedTimestamp(System.currentTimeMillis());
						handleBuySideBookTradeCaptureReport(buySideBookTradeCaptureReport);

						StringBuffer stringBuffer = new StringBuffer();

						if (!isValidPrice(security, value)) {
							stringBuffer.append("System received execution report for security ");
							stringBuffer.append(security.getName());
							stringBuffer.append(" from counterparty ");
							stringBuffer.append(counterparty.getName());
							stringBuffer.append(" with an invalid price. The message was processed neverless, but you should contact your counterparty.");

						}
						else if (!isValidQuantity(security, buySideQuoteRequestEntry.getLastQuantity())) {
							stringBuffer.append("System received execution report for security ");
							stringBuffer.append(security.getName());
							stringBuffer.append(" from counterparty ");
							stringBuffer.append(counterparty.getName());
							stringBuffer.append(" with an invalid quantity. The message was processed neverless, but you should contact your counterparty.");
						}

						if (stringBuffer.length() > 0) {
							LogEntry logEntry = new LogEntry();
							logEntry.setLogLevel(Level.WARNING);
							logEntry.setLogDate(new Date());
							logEntry.setMessageComponent(buySideBook.getName());
							logEntry.setMessageText(stringBuffer.toString());
							logEntry.setHighlightKey(buySideQuoteRequestEntry.getOrderId());
							basisPersistenceHandler.writeLogEntry(logEntry);
						}

					}
				}
			}
		}
		catch (FieldNotFound e) {
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(buySideBook.getName());
			logEntry.setMessageText(e.getMessage());
			basisPersistenceHandler.writeLogEntry(logEntry);
			log.error("Bug", e);

		}

	}

	private Double getYield(FSecurity fSecurity, Double price) {

		if (price == null)
			return null;

		AssignedBuySideBookSecurity assignedBuySideBookSecurity = assignedBuySideBookSecurities.get(fSecurity.getId());
		if (assignedBuySideBookSecurity == null)
			return null;

		if (fSecurity.getMaturity() == null || fSecurity.getSecurityDetails().getCouponRate() == 0)
			return null;

		BankCalendar bankCalendar = assignedBuySideBookSecurity.getBankCalendar();
		Integer settlementDays = assignedBuySideBookSecurity.getValuta();
		if (bankCalendar == null)
			bankCalendar = buySideBook.getBankCalendar();
		if (settlementDays == null)
			settlementDays = buySideBook.getValuta();

		Calendar calendar = bankCalendar.getCalendar();

		org.jquantlib.time.Date settlementDate = calendar.advance(new org.jquantlib.time.Date(new Date()), settlementDays, TimeUnit.Days);

		Integer basis = null;

		basis = assignedBuySideBookSecurity.getDaycountConvention();

		if (basis == null)
			basis = buySideBook.getDaycountConvention();

		net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod calcMethod = buySideBook.getCalcMethod();

		if (assignedBuySideBookSecurity.getCalcMethod() != null)
			calcMethod = assignedBuySideBookSecurity.getCalcMethod();

		switch (calcMethod) {
			case DEFAULT:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.DEFAULT);
			case DISC:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.DISC);
			case MAT:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.MAT);
			case NONE:
				return null;
			case ODDF:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.ODDF);
			case ODDL:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.ODDL);
			case TBILL:
				return YieldCalculator.getInstance().getYield(fSecurity, price, settlementDate.isoDate(), basis, CalcMethod.TBILL);
			default:
				return null;

		}

	}

	private Double getPrice(FSecurity fSecurity, Double yield) {

		if (yield == null)
			return null;

		AssignedBuySideBookSecurity assignedBuySideBookSecurity = assignedBuySideBookSecurities.get(fSecurity.getId());
		if (assignedBuySideBookSecurity == null)
			return null;

		if (fSecurity.getMaturity() == null || fSecurity.getSecurityDetails().getCouponRate() == 0)
			return null;

		BankCalendar bankCalendar = assignedBuySideBookSecurity.getBankCalendar();
		Integer settlementDays = assignedBuySideBookSecurity.getValuta();
		if (bankCalendar == null)
			bankCalendar = buySideBook.getBankCalendar();
		if (settlementDays == null)
			settlementDays = buySideBook.getValuta();

		Calendar calendar = bankCalendar.getCalendar();
		org.jquantlib.time.Date settlementDate = calendar.advance(new org.jquantlib.time.Date(new Date()), settlementDays, TimeUnit.Days);

		Integer basis = null;

		basis = assignedBuySideBookSecurity.getDaycountConvention();

		if (basis == null)
			basis = buySideBook.getDaycountConvention();

		net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod calcMethod = buySideBook.getCalcMethod();

		if (assignedBuySideBookSecurity.getCalcMethod() != null)
			calcMethod = assignedBuySideBookSecurity.getCalcMethod();

		switch (calcMethod) {
			case DEFAULT:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.DEFAULT);
			case DISC:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.DISC);
			case MAT:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.MAT);
			case NONE:
				return null;
			case ODDF:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.ODDF);
			case ODDL:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.ODDL);
			case TBILL:
				return YieldCalculator.getInstance().getPrice(fSecurity, yield, settlementDate.isoDate(), basis, CalcMethod.TBILL);
			default:
				return null;

		}

	}

	/**
	 * On close quote monitor request.
	 *
	 * @param closeQuotePageRequest the close quote page request
	 * @param buySideQuotePage the buy side quote page
	 * @param channel the channel
	 */
	public void onCloseQuoteMonitorRequest(CloseQuotePageRequest closeQuotePageRequest, BuySideQuotePage buySideQuotePage, Channel channel) {

		Map<BuySideQuotePage, Long> quotePageMap = quotepageSessions.get(channel);

		if (quotePageMap != null) {
			quotePageMap.remove(buySideQuotePage);
		}
	}

	/**
	 * On open quote monitor request.
	 *
	 * @param openQuotePageRequest the open quote page request
	 * @param buySideQuotePage the buy side quote page
	 * @param channel the channel
	 */
	public void onOpenQuoteMonitorRequest(OpenQuotePageRequest openQuotePageRequest, BuySideQuotePage buySideQuotePage, Channel channel) {

		Set<AbstractBuySideMDInputEntry> mdInputEntries = new HashSet<AbstractBuySideMDInputEntry>();

		Map<BuySideQuotePage, Long> quotePageMap = quotepageSessions.get(channel);

		List<AbstractBuySideMDInputEntry> abstractBuySideMDInpuEntries = new ArrayList<AbstractBuySideMDInputEntry>();

		if (quotePageMap == null) {
			quotePageMap = new HashMap<BuySideQuotePage, Long>();
			quotepageSessions.put(channel, quotePageMap);
		}

		if (buySideQuotePage.getAssignedBuySideBookSecurities().size() > 0) {
			quotePageMap.put(buySideQuotePage, buySideQuotePage.getAssignedBuySideBookSecurities().get(0).getSecurity().getId());

			synchronized (securityCompositeEntries) {

				for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideQuotePage.getAssignedBuySideBookSecurities()) {
					BuySideCompositeMDInputEntry buySideMDInputEntry = securityCompositeEntries.get(assignedBuySideBookSecurity.getSecurity().getId());
					if (buySideMDInputEntry != null)
						mdInputEntries.add(buySideMDInputEntry);

					List<BuySideCompositeMDInputEntry> historicalEntries = historicalData.get(assignedBuySideBookSecurity.getSecurity().getId());
					if (historicalEntries != null)
						abstractBuySideMDInpuEntries.addAll(historicalEntries);
				}

			}

			Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(buySideQuotePage.getAssignedBuySideBookSecurities()
					.get(0).getSecurity().getId());
			if (counterpartyMap != null) {
				List<BuySideCounterpartyMDInputEntry> list = new ArrayList<BuySideCounterpartyMDInputEntry>(counterpartyMap.values());
				for (BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry : list)
					mdInputEntries.add(buySideCounterpartyMDInputEntry);
			}
		}
		basisPersistenceHandler.send(new UpdateBuySideMDInputEntryResponse(new ArrayList<AbstractBuySideMDInputEntry>(mdInputEntries)), channel);

		if (abstractBuySideMDInpuEntries.size() > 0) {

			try {

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(abstractBuySideMDInpuEntries);
				objectOutputStream.close();
				byteArrayOutputStream.close();

				ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream2);
				gzip.write(byteArrayOutputStream.toByteArray());
				gzip.finish();
				gzip.close();

				basisPersistenceHandler.send(new HistoricalBuySideDataResponse(buySideQuotePage.getId(), byteArrayOutputStream2.toByteArray()), channel);
			}
			catch (Exception exception) {
				log.error("Bug", exception);

			}
		}
	}

	/**
	 * On open quote depth request.
	 *
	 * @param openQuoteDepthRequest the open quote depth request
	 * @param buySideQuotePage the buy side quote page
	 * @param channel the channel
	 */
	public void onOpenQuoteDepthRequest(OpenQuoteDepthRequest openQuoteDepthRequest, BuySideQuotePage buySideQuotePage, Channel channel) {

		Set<AbstractBuySideMDInputEntry> mdInputEntries = new HashSet<AbstractBuySideMDInputEntry>();

		Map<BuySideQuotePage, Long> quotePageMap = quotepageSessions.get(channel);

		if (quotePageMap == null) {
			quotePageMap = new HashMap<BuySideQuotePage, Long>();
			quotepageSessions.put(channel, quotePageMap);
		}

		if (buySideQuotePage.getAssignedBuySideBookSecurities().size() > 0) {

			quotePageMap.put(buySideQuotePage, openQuoteDepthRequest.getSecurity());

			Map<String, BuySideCounterpartyMDInputEntry> counterpartyMap = securityCounterpartyEntries.get(openQuoteDepthRequest.getSecurity());
			if (counterpartyMap != null) {

				for (BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry : counterpartyMap.values()) {

					mdInputEntries.add(buySideCounterpartyMDInputEntry);
				}
			}
		}
		basisPersistenceHandler.send(new UpdateBuySideMDInputEntryResponse(new ArrayList<AbstractBuySideMDInputEntry>(mdInputEntries)), channel);
	}

	/**
	 * On new order single request.
	 *
	 * @param newOrderSingleRequest the new order single request
	 * @param channel the channel
	 * @return the buy side new order single entry
	 */
	public synchronized BuySideNewOrderSingleEntry onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel) {

		BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = newOrderSingleRequest.getBuySideNewOrderSingleEntry();
		buySideNewOrderSingleEntry.setOriginalOrderId(null);
		long timestamp = System.currentTimeMillis();
		buySideNewOrderSingleEntry.setUpdated(timestamp);
		FUser user = basisPersistenceHandler.getUser(channel);
		buySideNewOrderSingleEntry.setUser(user.getName());

		if (buySideNewOrderSingleEntry.getOrderStatus() == null) {
			buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW_PENDING);
			buySideNewOrderSingleEntry.setOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));
			buySideNewOrderSingleEntry.setCreated(timestamp);
		}

		if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REPLACE_PENDING) {
			buySideNewOrderSingleEntry.setNewOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));
		}

		buySideNewOrderSingleEntryMap.put(buySideNewOrderSingleEntry.getOrderId(), buySideNewOrderSingleEntry);
		persistNewOrderSingleEntry(buySideNewOrderSingleEntry, user, true);

		switch (buySideNewOrderSingleEntry.getOrderStatus()) {
			case NEW_PENDING:
				sendNewOrderSingle(buySideNewOrderSingleEntry);
				break;
			case CANCEL_PENDING:
				sendOrderCancel(buySideNewOrderSingleEntry);
				break;
			case REPLACE_PENDING:
				sendReplaceRequest(buySideNewOrderSingleEntry);
				break;
			case CANCEL:
			case DONE_FOR_DAY:
			case FILLED:
			case NEW:
			case PARTIALLY_FILLED:
			case REJECTED:
				break;
		}

		return buySideNewOrderSingleEntry;
	}

	private void sendReplaceRequest(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {
			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideNewOrderSingleEntry.getMarketInterface()) {

				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "G");
				message.setString(41, buySideNewOrderSingleEntry.getOrderId());
				message.setString(11, buySideNewOrderSingleEntry.getNewOrderId());

				if (buySideNewOrderSingleEntry.getSide() == Side.BID)
					message.setChar(54, '2');
				else
					message.setChar(54, '1');
				message.setUtcTimeStamp(60, new Date());
				if (buySideNewOrderSingleEntry.getLimit() == null)
					message.setChar(40, '1');
				else {
					message.setChar(40, '2');
					message.setDouble(44, buySideNewOrderSingleEntry.getNewLimit());
				}

				message.setDouble(38, buySideNewOrderSingleEntry.getNewOrderQuantity());

				switch (buySideNewOrderSingleEntry.getTimeInForce()) {
					case FILL_OR_KILL:
						message.setChar(59, '4');
						message.setString(64, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
					case GOOD_TILL_CANCEL:
						message.setChar(59, '1');
						break;
					case GOOD_TILL_DATE:
						message.setChar(59, '6');
						message.setString(432, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
					case GOOD_TILL_DAY:
						message.setChar(59, '0');
						message.setString(64, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
				}

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideNewOrderSingleEntry.getSecurity());

				if (security != null) {

					message.setString(55, "N/A");
					message.setString(48, security.getSecurityID());
					message.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								message.setString(48, alternativeSecurityID);
								message.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							message.addGroup(group);
						}
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(),
							businessComponentHandler);
					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, message);

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, message);
						abstractComponentHandler.addFIXMessage(messageEntry);

					}
					else {
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
					}
				}
				else {
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
				}
				persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
			}
		}
	}

	/**
	 * On open buy side book request.
	 *
	 * @param openBuySideBookRequest the open buy side book request
	 * @param channel the channel
	 * @return the collection
	 */
	public synchronized Collection<AbstractBuySideEntry> onOpenBuySideBookRequest(OpenBuySideBookRequest openBuySideBookRequest, Channel channel) {

		bookSessions.add(channel);
		Collection<AbstractBuySideEntry> abstractBuySideEntries = new HashSet<AbstractBuySideEntry>(buySideNewOrderSingleEntryMap.values());
		abstractBuySideEntries.addAll(buySideQuoteRequestEntryMap.values());
		return abstractBuySideEntries;
	}

	/**
	 * On close buy side book request.
	 *
	 * @param closeBuySideBookRequest the close buy side book request
	 * @param channel the channel
	 */
	public void onCloseBuySideBookRequest(CloseBuySideBookRequest closeBuySideBookRequest, Channel channel) {

		bookSessions.remove(channel);

	}

	private BuySideNewOrderSingle persistNewOrderSingleEntry(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry, FUser fUser, boolean logTransaction) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		if (fUser != null)
			stringBuffer.append(fUser.getName());
		else
			stringBuffer.append("System");

		BuySideNewOrderSingle buySideNewOrderSingle = new BuySideNewOrderSingle();

		List<BuySideNewOrderSingle> newOrderSingleEntries = null;
		if (buySideNewOrderSingleEntry.getOriginalOrderId() == null)
			newOrderSingleEntries = basisPersistenceHandler.executeQuery(BuySideNewOrderSingle.class,
					"select a from BuySideNewOrderSingle a where a.orderId=?1", buySideNewOrderSingleEntry.getOrderId(), businessComponentHandler, true);
		else
			newOrderSingleEntries = basisPersistenceHandler
					.executeQuery(BuySideNewOrderSingle.class, "select a from BuySideNewOrderSingle a where a.orderId=?1",
							buySideNewOrderSingleEntry.getOriginalOrderId(), businessComponentHandler, true);

		if (newOrderSingleEntries.size() > 0) {
			newEntry = false;
			buySideNewOrderSingle = newOrderSingleEntries.get(0);
		}

		BuySideBook buySideBook = basisPersistenceHandler.findById(BuySideBook.class, buySideNewOrderSingleEntry.getBuySideBook(), businessComponentHandler);
		buySideNewOrderSingle.setBuySideBook(buySideBook);

		Counterparty counterparty = basisPersistenceHandler
				.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(), businessComponentHandler);
		buySideNewOrderSingle.setCounterparty(counterparty);

		buySideNewOrderSingle.setCounterpartyOrderId(buySideNewOrderSingleEntry.getCounterpartyOrderId());
		buySideNewOrderSingle.setCreatedTimestamp(buySideNewOrderSingleEntry.getCreated());
		buySideNewOrderSingle.setCumulativeQuantity(buySideNewOrderSingleEntry.getCumulativeQuantity());
		buySideNewOrderSingle.setLastPrice(buySideNewOrderSingleEntry.getLastPrice());
		buySideNewOrderSingle.setLastYield(buySideNewOrderSingleEntry.getLastYield());
		buySideNewOrderSingle.setLastQuantity(buySideNewOrderSingleEntry.getLastQuantity());
		buySideNewOrderSingle.setLimit(buySideNewOrderSingleEntry.getLimit());
		buySideNewOrderSingle.setMinQuantity(buySideNewOrderSingleEntry.getMinQuantity());
		buySideNewOrderSingle.setLeaveQuantity(buySideNewOrderSingleEntry.getLeaveQuantity());
		buySideNewOrderSingle.setNewOrderId(buySideNewOrderSingleEntry.getNewOrderId());
		buySideNewOrderSingle.setNewLimit(buySideNewOrderSingleEntry.getNewLimit());
		buySideNewOrderSingle.setNewQuantity(buySideNewOrderSingleEntry.getNewOrderQuantity());
		buySideNewOrderSingle.setTifTimestamp(buySideNewOrderSingleEntry.getTifDate());

		FIXInitiator fixInitiator = basisPersistenceHandler.findById(FIXInitiator.class, buySideNewOrderSingleEntry.getMarketInterface(),
				businessComponentHandler);
		buySideNewOrderSingle.setMarketInterface(fixInitiator);

		buySideNewOrderSingle.setOrderId(buySideNewOrderSingleEntry.getOrderId());
		buySideNewOrderSingle.setOrderQuantity(buySideNewOrderSingleEntry.getOrderQuantity());

		switch (buySideNewOrderSingleEntry.getOrderStatus()) {
			case CANCEL_PENDING:
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.CANCEL_PENDING);
				stringBuffer.append(" canceled order ");
				break;
			case CANCEL:
				stringBuffer.append(" received cancel confirmation for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.CANCEL);
				break;
			case REPLACE_PENDING:
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.REPLACE_PENDING);
				stringBuffer.append(" replace order ");
				break;
			case NEW_PENDING:
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.NEW_PENDING);
				stringBuffer.append(" created new order ");
				break;
			case DONE_FOR_DAY:
				stringBuffer.append(" received done for day for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.DONE_FOR_DAY);
				break;
			case FILLED:
				stringBuffer.append(" received fill for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.FILLED);
				break;
			case NEW:
				stringBuffer.append(" confirmation for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.NEW);
				break;
			case PARTIALLY_FILLED:
				stringBuffer.append(" received partial fill for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.PARTIALLY_FILLED);
				break;
			case REJECTED:
				stringBuffer.append(" received reject for order ");
				buySideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.OrderStatus.REJECTED);
				break;

		}

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, buySideNewOrderSingleEntry.getSecurity(), businessComponentHandler);
		buySideNewOrderSingle.setSecurity(security);

		buySideNewOrderSingle.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.Side.BID);
		String side = " to ";
		if (buySideNewOrderSingleEntry.getSide() == Side.ASK) {
			stringBuffer.append("buy ");
			side = " from ";
			buySideNewOrderSingle.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.Side.ASK);
		}
		else {
			stringBuffer.append("sell ");
		}

		stringBuffer.append(decimalFormat2.format(buySideNewOrderSingleEntry.getOrderQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		if (buySideNewOrderSingleEntry.getLimit() != null) {
			stringBuffer.append(" limit ");
			stringBuffer.append(decimalFormat.format(buySideNewOrderSingleEntry.getLimit()));
		}

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());

		switch (buySideNewOrderSingleEntry.getTimeInForce()) {
			case FILL_OR_KILL:
				stringBuffer.append(" fill or kill (settlement ");
				stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(").");
				buySideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.TimeInForce.FILL_OR_KILL);
				break;
			case GOOD_TILL_CANCEL:
				stringBuffer.append(" good till cancel.");
				buySideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.TimeInForce.GOOD_TILL_CANCEL);
				break;
			case GOOD_TILL_DATE:
				stringBuffer.append(" good till ");
				stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(".");
				buySideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.TimeInForce.GOOD_TILL_DATE);
				break;
			case GOOD_TILL_DAY:
				stringBuffer.append(" good till day (settlement ");
				stringBuffer.append(simpleDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(").");
				buySideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.TimeInForce.GOOD_TILL_DAY);
				break;
		}

		buySideNewOrderSingle.setUpdatedTimestamp(buySideNewOrderSingleEntry.getUpdated());

		if (fUser != null)
			buySideNewOrderSingle.setfUser(fUser);

		try {
			if (newEntry)
				basisPersistenceHandler.persist(buySideNewOrderSingle);
			else
				basisPersistenceHandler.update(buySideNewOrderSingle);

			for (Channel channel : bookSessions)
				basisPersistenceHandler.send(new NewOrderSingleResponse(buySideNewOrderSingleEntry), channel);

			if (logTransaction) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(buySideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				if (buySideNewOrderSingleEntry.getNewOrderId() != null)
					logEntry.setHighlightKey(buySideNewOrderSingleEntry.getNewOrderId() + ";;;" + buySideNewOrderSingleEntry.getOrderId());
				else
					logEntry.setHighlightKey(buySideNewOrderSingleEntry.getOrderId());

				if (!(buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REPLACE_PENDING && fUser == null))
					basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {

			log.error("Bug", e);

			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(buySideBook.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(buySideNewOrderSingleEntry.getOrderId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

		return buySideNewOrderSingle;

	}

	private void sendNewOrderSingle(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {

			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideNewOrderSingleEntry.getMarketInterface()) {

				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "D");
				message.setString(11, buySideNewOrderSingleEntry.getOrderId());

				if (buySideNewOrderSingleEntry.getSide() == Side.BID)
					message.setChar(54, '2');
				else
					message.setChar(54, '1');
				message.setUtcTimeStamp(60, new Date());
				if (buySideNewOrderSingleEntry.getLimit() == null)
					message.setChar(40, '1');
				else {
					message.setChar(40, '2');
					message.setDouble(44, buySideNewOrderSingleEntry.getLimit());
				}

				message.setDouble(38, buySideNewOrderSingleEntry.getOrderQuantity());

				if (buySideNewOrderSingleEntry.getMinQuantity() > 0)
					message.setDouble(110, buySideNewOrderSingleEntry.getMinQuantity());

				switch (buySideNewOrderSingleEntry.getTimeInForce()) {
					case FILL_OR_KILL:
						message.setChar(59, '4');
						message.setString(64, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
					case GOOD_TILL_CANCEL:
						message.setChar(59, '1');
						break;
					case GOOD_TILL_DATE:
						message.setChar(59, '6');
						message.setString(432, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
					case GOOD_TILL_DAY:
						message.setChar(59, '0');
						message.setString(64, localMarketDateFormat.format(new Date(buySideNewOrderSingleEntry.getTifDate())));
						break;
				}

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideNewOrderSingleEntry.getSecurity());

				if (security != null) {

					message.setString(55, "N/A");
					message.setString(48, security.getSecurityID());
					message.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								message.setString(48, alternativeSecurityID);
								message.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							message.addGroup(group);
						}
					}

					if (security.getSecurityDetails().getPriceQuoteMethod() != null) {
						if (security.getSecurityDetails().getPriceQuoteMethod().equals("PCTPAR"))
							message.setInt(423, 1);
						else if (security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							message.setInt(423, 9);
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideNewOrderSingleEntry.getCounterparty(),
							businessComponentHandler);
					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, message);

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, message);
						abstractComponentHandler.addFIXMessage(messageEntry);
					}
					else {
						buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
						persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
					}
				}
				else {
					buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
					persistNewOrderSingleEntry(buySideNewOrderSingleEntry, null, true);
				}
			}
		}

	}

	private BuySideQuoteRequestEntry getBuySideQuoteRequestEntry(BuySideQuoteRequest buySideQuoteRequest) {

		BuySideQuoteRequestEntry buySideQuoteRequestEntry = new BuySideQuoteRequestEntry();
		buySideQuoteRequestEntry.setBuySideBook(buySideQuoteRequest.getBuySideBook().getId());
		buySideQuoteRequestEntry.setCounterparty(buySideQuoteRequest.getCounterparty().getId());
		buySideQuoteRequestEntry.setCounterpartyOrderId(buySideQuoteRequest.getCounterpartyOrderId());
		buySideQuoteRequestEntry.setCreated(buySideQuoteRequest.getCreatedTimestamp());
		buySideQuoteRequestEntry.setCumulativeQuantity(buySideQuoteRequest.getCumulativeQuantity());
		buySideQuoteRequestEntry.setLastYield(buySideQuoteRequest.getLastYield());
		buySideQuoteRequestEntry.setLastPrice(buySideQuoteRequest.getLastPrice());
		buySideQuoteRequestEntry.setLastYield(buySideQuoteRequest.getLastYield());
		buySideQuoteRequestEntry.setLastQuantity(buySideQuoteRequest.getLastQuantity());
		buySideQuoteRequestEntry.setLimit(buySideQuoteRequest.getLimit());
		buySideQuoteRequestEntry.setQuoteId(buySideQuoteRequest.getQuoteId());
		buySideQuoteRequestEntry.setMultiId(buySideQuoteRequest.getMultiId());
		buySideQuoteRequestEntry.setQuoteReqId(buySideQuoteRequest.getQuoteReqId());
		buySideQuoteRequestEntry.setLeaveQuantity(buySideQuoteRequest.getLeaveQuantity());
		buySideQuoteRequestEntry.setSubjectDate(buySideQuoteRequest.getSubjectTimestamp());
		buySideQuoteRequestEntry.setMinQuantity(buySideQuoteRequest.getMinQuantity());
		buySideQuoteRequestEntry.setMarketInterface(buySideQuoteRequest.getMarketInterface().getId());
		buySideQuoteRequestEntry.setOrderId(buySideQuoteRequest.getOrderId());
		buySideQuoteRequestEntry.setOrderQuantity(buySideQuoteRequest.getOrderQuantity());
		buySideQuoteRequestEntry.setExpireDate(buySideQuoteRequest.getExpireTimestamp());
		buySideQuoteRequestEntry.setSettlementDate(buySideQuoteRequest.getSettlementTimestamp());

		switch (buySideQuoteRequest.getOrderStatus()) {
			case COUNTER:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER);
				break;
			case COUNTER_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING);
				break;
			case FILLED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.FILLED);
				break;
			case HIT_LIFT:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT);
				break;
			case HIT_LIFT_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT_PENDING);
				break;
			case NEW:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW);
				break;
			case NEW_PENDING:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW_PENDING);
				break;
			case PASS:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
				break;
			case PASS_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING);
				break;
			case QUOTED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.QUOTED);
				break;
			case REJECTED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.REJECTED);
				break;
			case COVER:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER);
				break;
			case COVER_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING);
				break;
			case DONE_AWAY:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY);
				break;
			case DONE_AWAY_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING);
				break;
			case EXPIRED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED);
				break;
			case EXPIRED_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED_PENDING);
				break;

		}

		buySideQuoteRequestEntry.setSecurity(buySideQuoteRequest.getSecurity().getId());

		buySideQuoteRequestEntry.setSide(Side.BID);

		if (buySideQuoteRequest.getSide() == net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.Side.ASK)
			buySideQuoteRequestEntry.setSide(Side.ASK);

		buySideQuoteRequestEntry.setUpdated(buySideQuoteRequest.getUpdatedTimestamp());
		buySideQuoteRequestEntry.setUser(buySideQuoteRequest.getfUser().getName());

		return buySideQuoteRequestEntry;
	}

	private BuySideNewOrderSingleEntry getBuySideNewOrderSingleEntry(BuySideNewOrderSingle newOrderSingleEntry) {

		BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
		buySideNewOrderSingleEntry.setBuySideBook(newOrderSingleEntry.getBuySideBook().getId());
		buySideNewOrderSingleEntry.setCounterparty(newOrderSingleEntry.getCounterparty().getId());
		buySideNewOrderSingleEntry.setCounterpartyOrderId(newOrderSingleEntry.getCounterpartyOrderId());
		buySideNewOrderSingleEntry.setCreated(newOrderSingleEntry.getCreatedTimestamp());
		buySideNewOrderSingleEntry.setCumulativeQuantity(newOrderSingleEntry.getCumulativeQuantity());
		buySideNewOrderSingleEntry.setLastYield(newOrderSingleEntry.getLastYield());
		buySideNewOrderSingleEntry.setLastPrice(newOrderSingleEntry.getLastPrice());
		buySideNewOrderSingleEntry.setLastYield(newOrderSingleEntry.getLastYield());
		buySideNewOrderSingleEntry.setLastQuantity(newOrderSingleEntry.getLastQuantity());
		buySideNewOrderSingleEntry.setLimit(newOrderSingleEntry.getLimit());
		buySideNewOrderSingleEntry.setNewOrderId(newOrderSingleEntry.getNewOrderId());
		buySideNewOrderSingleEntry.setNewLimit(newOrderSingleEntry.getNewLimit());
		buySideNewOrderSingleEntry.setNewOrderQuantity(newOrderSingleEntry.getNewQuantity());
		buySideNewOrderSingleEntry.setLeaveQuantity(newOrderSingleEntry.getLeaveQuantity());
		buySideNewOrderSingleEntry.setMinQuantity(newOrderSingleEntry.getMinQuantity());
		buySideNewOrderSingleEntry.setMarketInterface(newOrderSingleEntry.getMarketInterface().getId());
		buySideNewOrderSingleEntry.setOrderId(newOrderSingleEntry.getOrderId());
		buySideNewOrderSingleEntry.setOrderQuantity(newOrderSingleEntry.getOrderQuantity());
		buySideNewOrderSingleEntry.setTifDate(newOrderSingleEntry.getTifTimestamp());
		switch (newOrderSingleEntry.getOrderStatus()) {
			case CANCEL_PENDING:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL_PENDING);
				break;
			case CANCEL:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
				break;
			case REPLACE_PENDING:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REPLACE_PENDING);
				break;
			case DONE_FOR_DAY:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
				break;
			case NEW:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW);
				break;
			case NEW_PENDING:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW_PENDING);
				break;
			case REJECTED:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
				break;
			case FILLED:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED);
				break;
			case PARTIALLY_FILLED:
				buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
				break;
		}
		buySideNewOrderSingleEntry.setSecurity(newOrderSingleEntry.getSecurity().getId());
		buySideNewOrderSingleEntry.setSide(Side.BID);
		if (newOrderSingleEntry.getSide() == net.sourceforge.fixagora.buyside.shared.persistence.BuySideNewOrderSingle.Side.ASK)
			buySideNewOrderSingleEntry.setSide(Side.ASK);
		switch (newOrderSingleEntry.getTimeInForce()) {
			case FILL_OR_KILL:
				buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);
				break;
			case GOOD_TILL_CANCEL:
				buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_CANCEL);
				break;
			case GOOD_TILL_DATE:
				buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
				break;
			case GOOD_TILL_DAY:
				buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DAY);
				break;
		}
		buySideNewOrderSingleEntry.setUpdated(newOrderSingleEntry.getUpdatedTimestamp());
		buySideNewOrderSingleEntry.setUser(newOrderSingleEntry.getfUser().getName());

		return buySideNewOrderSingleEntry;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

		historicalData.clear();

	}

	private boolean sendTradeCaptureReport(BuySideBookTradeCaptureReport buySideBookTradeCaptureReport, AbstractBusinessComponent targetComponent) {

		Message tradeCaptureReport = new Message();
		tradeCaptureReport.getHeader().setString(MsgType.FIELD, "AE");
		tradeCaptureReport.setString(37, buySideBookTradeCaptureReport.getOrderId());
		tradeCaptureReport.setString(17, buySideBookTradeCaptureReport.getExecId());
		tradeCaptureReport.setString(11, buySideBookTradeCaptureReport.getCounterpartyOrderId());
		if (buySideBookTradeCaptureReport.getSide() == net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookTradeCaptureReport.Side.BUY)
			tradeCaptureReport.setChar(54, '1');
		else
			tradeCaptureReport.setChar(54, '2');
		tradeCaptureReport.setDouble(14, buySideBookTradeCaptureReport.getCumulativeQuantity());
		tradeCaptureReport.setDouble(151, buySideBookTradeCaptureReport.getLeaveQuantity());
		tradeCaptureReport.setDouble(38, buySideBookTradeCaptureReport.getOrderQuantity());
		tradeCaptureReport.setString(37, buySideBookTradeCaptureReport.getOrderId());
		tradeCaptureReport.setString(1003, buySideBookTradeCaptureReport.getTradeId());

		if (buySideBookTradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod() != null
				&& buySideBookTradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
			if (buySideBookTradeCaptureReport.getLastPrice() != null)
				tradeCaptureReport.setDouble(669, buySideBookTradeCaptureReport.getLastPrice());
			tradeCaptureReport.setDouble(31, buySideBookTradeCaptureReport.getLastYield());
		}
		else
			tradeCaptureReport.setDouble(31, buySideBookTradeCaptureReport.getLastPrice());

		tradeCaptureReport.setDouble(32, buySideBookTradeCaptureReport.getLastQuantity());
		if (buySideBookTradeCaptureReport.getSettlementDate() != null)
			tradeCaptureReport.setString(64, localMarketDateFormat.format(new Date(buySideBookTradeCaptureReport.getSettlementDate())));

		tradeCaptureReport.setString(48, buySideBookTradeCaptureReport.getSecurity().getSecurityID());
		tradeCaptureReport.setString(22, buySideBookTradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());

		AbstractInitiator abstractInitiator = buySideBookTradeCaptureReport.getSourceComponent();

		Counterparty counterparty = buySideBookTradeCaptureReport.getCounterparty();

		if (abstractInitiator != null) {

			final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
			group.setString(448, abstractInitiator.getMarketName());
			group.setChar(447, 'D');
			group.setInt(452, 16);
			tradeCaptureReport.addGroup(group);

			Group userPartyID = null;

			if (buySideBookTradeCaptureReport.getfUser() != null) {
				for (FUserPartyID fUserPartyID : buySideBookTradeCaptureReport.getfUser().getfUserPartyIDs()) {

					if (fUserPartyID.getAbstractBusinessComponent() == null) {

						if (userPartyID == null) {

							userPartyID = new Group(453, 448, new int[] { 448, 447, 452 });
							userPartyID.setString(448, fUserPartyID.getPartyID());

							if (fUserPartyID.getPartyIDSource() != null)
								userPartyID.setChar(447, fUserPartyID.getPartyIDSource().charAt(0));

							userPartyID.setInt(452, 12);
						}
					}
					else if (fUserPartyID.getAbstractBusinessComponent().getId() == buySideBook.getId() && fUserPartyID.getPartyRole() != null
							&& fUserPartyID.getPartyRole().intValue() == 12) {

						userPartyID = new Group(453, 448, new int[] { 448, 447, 452 });
						userPartyID.setString(448, fUserPartyID.getPartyID());

						if (fUserPartyID.getPartyIDSource() != null)
							userPartyID.setChar(447, fUserPartyID.getPartyIDSource().charAt(0));

						userPartyID.setInt(452, 12);
					}
				}

			}

			if (userPartyID != null)
				tradeCaptureReport.addGroup(userPartyID);

			if (counterparty != null) {

				CounterPartyPartyID contraFirm = null;
				CounterPartyPartyID executingFirm = null;

				for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {

					if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
						if (contraFirm == null)
							contraFirm = counterPartyPartyID;
						if (executingFirm == null)
							executingFirm = counterPartyPartyID;
					}
					else {
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == buySideBook.getId() && counterPartyPartyID.getPartyRole() != null
								&& counterPartyPartyID.getPartyRole().intValue() == 17) {
							contraFirm = counterPartyPartyID;
						}
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == buySideBookTradeCaptureReport.getSourceComponent().getId()
								&& counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole().intValue() == 1) {
							executingFirm = counterPartyPartyID;
						}
					}
				}

				if (contraFirm == null && executingFirm != null) {
					contraFirm = new CounterPartyPartyID();
					contraFirm.setPartyID(executingFirm.getPartyID());
					contraFirm.setPartyIDSource(executingFirm.getPartyIDSource());
					contraFirm.setAbstractBusinessComponent(buySideBook);
					contraFirm.setPartyRole(17);
					contraFirm.setCounterparty(counterparty);
					counterparty.getCounterPartyPartyIDs().add(contraFirm);
					List<AbstractBusinessObject> counterparties = new ArrayList<AbstractBusinessObject>();
					counterparties.add(counterparty);

					StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Firm\" defined for counterparty ");
					stringBuffer.append(counterparty.getName());
					stringBuffer
							.append(". System creates a new party role taking the values from \"Executing Firm\". This role is needed to process the trade capture.");

					LogEntry logEntry = new LogEntry();
					logEntry.setLogLevel(Level.WARNING);
					logEntry.setLogDate(new Date());
					logEntry.setMessageComponent(buySideBook.getName());
					logEntry.setMessageText(stringBuffer.toString());
					logEntry.setHighlightKey(buySideBookTradeCaptureReport.getOrderId());
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
					tradeCaptureReport.addGroup(group2);
				}

				if (buySideBookTradeCaptureReport.getTrader() != null) {

					Trader trader = buySideBookTradeCaptureReport.getTrader();

					TraderPartyID contraTraderPartyID = null;
					TraderPartyID orderOriginatingTraderPartyID = null;

					for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
						if (traderPartyID.getAbstractBusinessComponent() == null) {
							if (contraTraderPartyID == null)
								contraTraderPartyID = traderPartyID;
							if (orderOriginatingTraderPartyID == null)
								orderOriginatingTraderPartyID = traderPartyID;
						}
						else {
							if (traderPartyID.getAbstractBusinessComponent().getId() == buySideBook.getId() && traderPartyID.getPartyRole() != null
									&& traderPartyID.getPartyRole().intValue() == 37) {
								contraTraderPartyID = traderPartyID;

							}
							if (traderPartyID.getAbstractBusinessComponent().getId() == buySideBookTradeCaptureReport.getSourceComponent().getId()
									&& traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole().intValue() == 12) {
								orderOriginatingTraderPartyID = traderPartyID;
							}
						}
					}

					if (contraTraderPartyID == null && orderOriginatingTraderPartyID != null) {
						contraTraderPartyID = new TraderPartyID();
						contraTraderPartyID.setPartyID(orderOriginatingTraderPartyID.getPartyID());
						contraTraderPartyID.setPartyIDSource(orderOriginatingTraderPartyID.getPartyIDSource());
						contraTraderPartyID.setAbstractBusinessComponent(buySideBook);
						contraTraderPartyID.setPartyRole(37);
						contraTraderPartyID.setTrader(trader);
						trader.getTraderPartyIDs().add(contraTraderPartyID);
						List<AbstractBusinessObject> traders = new ArrayList<AbstractBusinessObject>();
						traders.add(trader);

						StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Trader\" defined for trader ");
						stringBuffer.append(buySideBookTradeCaptureReport.getTrader().getName());
						stringBuffer
								.append(". System creates a new party role taking the values from \"Executing Trader\". This role is needed to process the trade capture.");

						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(buySideBook.getName());
						logEntry.setMessageText(stringBuffer.toString());
						logEntry.setHighlightKey(buySideBookTradeCaptureReport.getOrderId());
						basisPersistenceHandler.writeLogEntry(logEntry);

						basisPersistenceHandler.update(traders, null, businessComponentHandler, true);

					}

					if (contraTraderPartyID != null) {
						final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
						group2.setString(448, contraTraderPartyID.getPartyID());
						if (contraTraderPartyID.getPartyIDSource() != null)
							group2.setChar(447, contraTraderPartyID.getPartyIDSource().charAt(0));
						group2.setInt(452, 37);
						tradeCaptureReport.addGroup(group2);
					}

				}

			}

			tradeCaptureReport.setString(48, buySideBookTradeCaptureReport.getSecurity().getSecurityID());
			tradeCaptureReport.setString(22, buySideBookTradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());
			tradeCaptureReport.setString(55, "N/A");
			MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, tradeCaptureReport);
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(targetComponent.getId());
			if (abstractComponentHandler != null)
				abstractComponentHandler.addFIXMessage(messageEntry);
		}

		return true;

	}

	private void handleBuySideBookTradeCaptureReport(BuySideBookTradeCaptureReport buySideBookTradeCaptureReport) {

		try {
			basisPersistenceHandler.persist(buySideBookTradeCaptureReport);
			for (AbstractComponentHandler abstractComponentHandler : onlineTradeCaptureComponentHandlers) {
				AbstractBusinessComponent abstractBusinessComponent = abstractComponentHandler.getAbstractBusinessComponent();
				sendTradeCaptureReport(buySideBookTradeCaptureReport, abstractBusinessComponent);
				for (AssignedBuySideTradeCaptureTarget assignedBuySideTradeCaptureTarget : buySideBook.getAssignedTradeCaptureTargets()) {
					if (assignedBuySideTradeCaptureTarget.getAbstractBusinessComponent().getId() == abstractBusinessComponent.getId()) {
						assignedBuySideTradeCaptureTarget.setLastTradeId(buySideBookTradeCaptureReport.getCreatedTimestamp());
						basisPersistenceHandler.update(assignedBuySideTradeCaptureTarget);
					}
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

	/**
	 * On buy side quote request request.
	 *
	 * @param quoteRequestRequest the quote request request
	 * @param channel the channel
	 * @return the list
	 */
	public synchronized List<BuySideQuoteRequestEntry> onBuySideQuoteRequestRequest(BuySideQuoteRequestRequest quoteRequestRequest, Channel channel) {

		List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = quoteRequestRequest.getBuySideQuoteRequestEntries();

		long timestamp = System.currentTimeMillis();

		String multiId = null;

		if (buySideQuoteRequestEntries.size() > 0) {
			if (buySideQuoteRequestEntries.get(0).getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_ALL_PENDING) {

				BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntries.get(0);
				buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();
				for (BuySideQuoteRequestEntry buySideQuoteRequestEntry2 : buySideQuoteRequestEntryMap.values())
					if (buySideQuoteRequestEntry2.getMultiId().equals(buySideQuoteRequestEntry.getMultiId()) && !buySideQuoteRequestEntry2.isDone()) {
						buySideQuoteRequestEntry2
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING);
						buySideQuoteRequestEntries.add(buySideQuoteRequestEntry2);
					}
			}

			if (buySideQuoteRequestEntries.get(0).getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.HIT_LIFT_PENDING
					|| buySideQuoteRequestEntries.get(0).getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING) {

				BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntries.get(0);

				buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();

				for (BuySideQuoteRequestEntry buySideQuoteRequestEntry2 : buySideQuoteRequestEntryMap.values())
					if (buySideQuoteRequestEntry2.getMultiId().equals(buySideQuoteRequestEntry.getMultiId()) && !buySideQuoteRequestEntry2.isDone()
							&& !buySideQuoteRequestEntry2.getQuoteReqId().equals(buySideQuoteRequestEntry.getQuoteReqId())) {

						if (buySideQuoteRequestEntry2.getLimit() != null
								&& buySideQuoteRequestEntry.getOrderStatus() != net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING) {

							FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideQuoteRequestEntry2.getSecurity());

							if (buySideQuoteRequestEntry2.getSide() == Side.ASK) {

								if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									if (buySideQuoteRequestEntry2.getLimit() > buySideQuoteRequestEntry.getLimit())
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING);
									else
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING);

								}
								else {
									if (buySideQuoteRequestEntry2.getLimit() < buySideQuoteRequestEntry.getLimit())
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING);
									else
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING);
								}
							}
							else {
								if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									if (buySideQuoteRequestEntry2.getLimit() < buySideQuoteRequestEntry.getLimit())
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING);
									else
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING);
								}
								else {
									if (buySideQuoteRequestEntry2.getLimit() > buySideQuoteRequestEntry.getLimit())
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING);
									else
										buySideQuoteRequestEntry2
												.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING);
								}
							}
						}
						else
							buySideQuoteRequestEntry2
									.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING);
						buySideQuoteRequestEntries.add(buySideQuoteRequestEntry2);

					}

				buySideQuoteRequestEntries.add(buySideQuoteRequestEntry);
			}

		}

		for (int i = 0; i < buySideQuoteRequestEntries.size(); i++) {

			BuySideQuoteRequestEntry buySideQuoteRequestEntry = buySideQuoteRequestEntries.get(i);

			if (!buySideQuoteRequestEntry.isDone()) {

				buySideQuoteRequestEntry.setUpdated(timestamp);
				FUser user = basisPersistenceHandler.getUser(channel);
				buySideQuoteRequestEntry.setUser(user.getName());

				if (buySideQuoteRequestEntry.getOrderStatus() == null) {

					buySideQuoteRequestEntry
							.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW_PENDING);
					buySideQuoteRequestEntry.setQuoteReqId(decimalFormat3.format(basisPersistenceHandler.getId("QUOTEREQID")));
					if (i == 0)
						multiId = buySideQuoteRequestEntry.getQuoteReqId();
					buySideQuoteRequestEntry.setMultiId(multiId);
					buySideQuoteRequestEntry.setCreated(timestamp);
				}

				buySideQuoteRequestEntryMap.put(buySideQuoteRequestEntry.getQuoteReqId(), buySideQuoteRequestEntry);

				persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, user, true);

				switch (buySideQuoteRequestEntry.getOrderStatus()) {
					case NEW_PENDING:
						sendQuoteRequest(buySideQuoteRequestEntry);
						break;
					case PASS_PENDING:
						if (buySideQuoteRequestEntry.getQuoteId() != null)
							sendQuoteResponse(buySideQuoteRequestEntry);
						else {
							sendQuoteStatusReport(buySideQuoteRequestEntry);
						}
						break;
					case COVER_PENDING:
					case DONE_AWAY_PENDING:
						sendQuoteResponse(buySideQuoteRequestEntry);
						break;
					case COUNTER_PENDING:
					case HIT_LIFT_PENDING:
						buySideQuoteRequestEntry.setOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));
						sendQuoteResponse(buySideQuoteRequestEntry);
						break;
					default:
						break;
				}
			}
		}

		return buySideQuoteRequestEntries;
	}

	private void sendQuoteRequest(BuySideQuoteRequestEntry buySideQuoteRequestEntry) {

		for (AbstractComponentHandler abstractComponentHandler : inputComponents) {

			if (abstractComponentHandler.getAbstractBusinessComponent().getId() == buySideQuoteRequestEntry.getMarketInterface()) {

				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "R");
				message.setString(131, buySideQuoteRequestEntry.getQuoteReqId());

				final Group noRelatedSym = new Group(146, 55, new int[] { 55, 48, 22, 54, 38, 64, 126, 60, 423, 453, 110 });

				if (buySideQuoteRequestEntry.getSide() == Side.BID)
					noRelatedSym.setChar(54, '2');
				else
					noRelatedSym.setChar(54, '1');

				noRelatedSym.setDouble(38, buySideQuoteRequestEntry.getOrderQuantity());

				if (buySideQuoteRequestEntry.getMinQuantity() > 0)
					noRelatedSym.setDouble(110, buySideQuoteRequestEntry.getMinQuantity());
				noRelatedSym.setString(64, localMarketDateFormat.format(new Date(buySideQuoteRequestEntry.getSettlementDate())));

				noRelatedSym.setUtcTimeStamp(126, new Date(buySideQuoteRequestEntry.getExpireDate()));
				noRelatedSym.setUtcTimeStamp(60, new Date(buySideQuoteRequestEntry.getCreated()));

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(buySideQuoteRequestEntry.getSecurity());

				if (security != null) {

					noRelatedSym.setString(55, "N/A");
					noRelatedSym.setString(48, security.getSecurityID());
					noRelatedSym.setString(22, security.getSecurityDetails().getSecurityIDSource());
					if (abstractComponentHandler.getAbstractBusinessComponent() instanceof FIXInitiator) {
						FIXInitiator fixInitiator = (FIXInitiator) abstractComponentHandler.getAbstractBusinessComponent();
						if (fixInitiator.getSecurityIDSource() != null) {
							String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getSecurityID(),
									fixInitiator.getSecurityIDSource());
							if (alternativeSecurityID != null) {
								noRelatedSym.setString(48, alternativeSecurityID);
								noRelatedSym.setString(22, fixInitiator.getSecurityIDSource());
							}
						}
						if (fixInitiator.getPartyID() != null) {
							final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
							group.setString(448, fixInitiator.getPartyID());
							if (fixInitiator.getPartyIDSource() != null)
								group.setChar(447, fixInitiator.getPartyIDSource().charAt(0));
							group.setInt(452, 13);
							noRelatedSym.addGroup(group);
						}
					}

					if (security.getSecurityDetails().getPriceQuoteMethod() != null) {
						if (security.getSecurityDetails().getPriceQuoteMethod().equals("PCTPAR"))
							noRelatedSym.setInt(423, 1);
						else if (security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							noRelatedSym.setInt(423, 9);
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideQuoteRequestEntry.getCounterparty(),
							businessComponentHandler);
					if (counterparty != null) {

						addCounterpartyGroup(abstractComponentHandler.getAbstractBusinessComponent(), counterparty, 1, noRelatedSym);

						message.addGroup(noRelatedSym);

						MessageEntry messageEntry = new MessageEntry(buySideBook, buySideBook, message);
						abstractComponentHandler.addFIXMessage(messageEntry);
						buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					}
					else {
						buySideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
						persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
					}
				}
				else {
					buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS);
					persistBuySideQuoteRequestEntry(buySideQuoteRequestEntry, null, true);
				}
			}
		}
	}

	private BuySideQuoteRequest persistBuySideQuoteRequestEntry(BuySideQuoteRequestEntry buySideQuoteRequestEntry, FUser fUser, boolean logTransaction) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		if (fUser != null)
			stringBuffer.append(fUser.getName());
		else
			stringBuffer.append("System");

		BuySideQuoteRequest buySideQuoteRequest = new BuySideQuoteRequest();

		List<BuySideQuoteRequest> buySideQuoteRequests = basisPersistenceHandler.executeQuery(BuySideQuoteRequest.class,
				"select a from BuySideQuoteRequest a where a.quoteReqId=?1", buySideQuoteRequestEntry.getQuoteReqId(), businessComponentHandler, true);

		if (buySideQuoteRequests.size() > 0) {
			newEntry = false;
			buySideQuoteRequest = buySideQuoteRequests.get(0);
		}

		BuySideBook buySideBook = basisPersistenceHandler.findById(BuySideBook.class, buySideQuoteRequestEntry.getBuySideBook(), businessComponentHandler);
		buySideQuoteRequest.setBuySideBook(buySideBook);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, buySideQuoteRequestEntry.getCounterparty(), businessComponentHandler);
		buySideQuoteRequest.setCounterparty(counterparty);

		buySideQuoteRequest.setCounterpartyOrderId(buySideQuoteRequestEntry.getCounterpartyOrderId());
		buySideQuoteRequest.setCreatedTimestamp(buySideQuoteRequestEntry.getCreated());
		buySideQuoteRequest.setCumulativeQuantity(buySideQuoteRequestEntry.getCumulativeQuantity());
		buySideQuoteRequest.setLastPrice(buySideQuoteRequestEntry.getLastPrice());
		buySideQuoteRequest.setLastYield(buySideQuoteRequestEntry.getLastYield());
		buySideQuoteRequest.setLastQuantity(buySideQuoteRequestEntry.getLastQuantity());
		buySideQuoteRequest.setSubjectTimestamp(buySideQuoteRequestEntry.getSubjectDate());
		buySideQuoteRequest.setLimit(buySideQuoteRequestEntry.getLimit());
		buySideQuoteRequest.setMinQuantity(buySideQuoteRequestEntry.getMinQuantity());
		buySideQuoteRequest.setLeaveQuantity(buySideQuoteRequestEntry.getLeaveQuantity());
		buySideQuoteRequest.setMultiId(buySideQuoteRequestEntry.getMultiId());
		buySideQuoteRequest.setQuoteReqId(buySideQuoteRequestEntry.getQuoteReqId());
		buySideQuoteRequest.setQuoteRespId(buySideQuoteRequestEntry.getQuoteRespId());
		buySideQuoteRequest.setQuoteId(buySideQuoteRequestEntry.getQuoteId());
		buySideQuoteRequest.setExpireTimestamp(buySideQuoteRequestEntry.getExpireDate());
		buySideQuoteRequest.setSettlementTimestamp(buySideQuoteRequestEntry.getSettlementDate());

		FIXInitiator fixInitiator = basisPersistenceHandler.findById(FIXInitiator.class, buySideQuoteRequestEntry.getMarketInterface(),
				businessComponentHandler);
		buySideQuoteRequest.setMarketInterface(fixInitiator);

		buySideQuoteRequest.setOrderId(buySideQuoteRequestEntry.getOrderId());
		buySideQuoteRequest.setOrderQuantity(buySideQuoteRequestEntry.getOrderQuantity());

		switch (buySideQuoteRequestEntry.getOrderStatus()) {
			case COUNTER:
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.COUNTER);
				stringBuffer.append(" sent counter for inquiry ");
				break;
			case COUNTER_PENDING:
				stringBuffer.append(" countered inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.COUNTER_PENDING);
				break;
			case HIT_LIFT_PENDING:
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.HIT_LIFT_PENDING);
				if (buySideQuoteRequestEntry.getSide() == Side.ASK) {
					stringBuffer.append(" lift inquiry ");
				}
				else {
					stringBuffer.append(" hit inquiry ");
				}
				break;
			case NEW_PENDING:
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.NEW_PENDING);
				stringBuffer.append(" created new inquiry ");
				break;
			case HIT_LIFT:
				if (buySideQuoteRequestEntry.getSide() == Side.ASK) {
					stringBuffer.append(" sent lift for inquiry ");
				}
				else {
					stringBuffer.append(" sent hit for inquiry ");
				}
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.HIT_LIFT);
				break;
			case FILLED:
				stringBuffer.append(" received fill for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.FILLED);
				break;
			case NEW:
				stringBuffer.append(" sent new inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.NEW);
				break;
			case PASS:
				stringBuffer.append(" sent pass for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.PASS);
				break;
			case PASS_ALL_PENDING:
			case PASS_PENDING:
				stringBuffer.append(" passes inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.PASS_PENDING);
				break;
			case REJECTED:
				stringBuffer.append(" received reject for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.REJECTED);
				break;
			case QUOTED:
				stringBuffer.append(" received quote for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.QUOTED);
				break;
			case COVER:
				stringBuffer.append(" sent cover for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.COVER);
				break;
			case COVER_PENDING:
				stringBuffer.append(" covered inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.COVER_PENDING);
				break;
			case DONE_AWAY:
				stringBuffer.append(" sent done away for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.DONE_AWAY);
				break;
			case DONE_AWAY_PENDING:
				stringBuffer.append(" done away inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.DONE_AWAY_PENDING);
				break;
			case EXPIRED:
				stringBuffer.append(" sent expiration for inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.EXPIRED);
				break;
			case EXPIRED_PENDING:
				stringBuffer = new StringBuffer();
				stringBuffer.append("Expiration of inquiry ");
				buySideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.OrderStatus.EXPIRED_PENDING);
				break;

		}

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, buySideQuoteRequestEntry.getSecurity(), businessComponentHandler);
		buySideQuoteRequest.setSecurity(security);

		buySideQuoteRequest.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.Side.BID);
		String side = " to ";
		if (buySideQuoteRequestEntry.getSide() == Side.ASK) {
			stringBuffer.append("buy ");
			side = " from ";
			buySideQuoteRequest.setSide(net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuoteRequest.Side.ASK);
		}
		else {
			stringBuffer.append("sell ");
		}

		stringBuffer.append(decimalFormat2.format(buySideQuoteRequestEntry.getOrderQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		if (buySideQuoteRequestEntry.getLimit() != null) {
			stringBuffer.append(" limit ");
			stringBuffer.append(decimalFormat.format(buySideQuoteRequestEntry.getLimit()));
		}

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());

		stringBuffer.append(" settlement ");
		stringBuffer.append(simpleDateFormat.format(new Date(buySideQuoteRequestEntry.getSettlementDate())));
		stringBuffer.append(").");

		buySideQuoteRequest.setUpdatedTimestamp(buySideQuoteRequestEntry.getUpdated());

		if (fUser != null)
			buySideQuoteRequest.setfUser(fUser);

		try {
			if (newEntry)
				basisPersistenceHandler.persist(buySideQuoteRequest);
			else
				basisPersistenceHandler.update(buySideQuoteRequest);

			List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();
			buySideQuoteRequestEntries.add(buySideQuoteRequestEntry);

			for (Channel channel : bookSessions)
				basisPersistenceHandler.send(new BuySideQuoteRequestResponse(buySideQuoteRequestEntries), channel);

			if (logTransaction) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(buySideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				if (buySideQuoteRequestEntry.getOrderId() != null)
					logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId() + ";;;" + buySideQuoteRequestEntry.getOrderId());
				else
					logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {

			log.error("Bug", e);

			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(buySideBook.getName());
			logEntry.setMessageText(e.getMessage());
			if (buySideQuoteRequestEntry.getOrderId() != null)
				logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId() + ";;;" + buySideQuoteRequestEntry.getOrderId());
			else
				logEntry.setHighlightKey(buySideQuoteRequestEntry.getQuoteReqId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

		return buySideQuoteRequest;
	}

	private boolean isValidQuantity(FSecurity security, double quantity) {

		if (security == null)
			return false;

		if (security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null) {

			BigDecimal quantityBigDecimal = new BigDecimal(decimalFormat.format(quantity));

			BigDecimal contractMultiplier = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getContractMultiplier()));

			quantityBigDecimal = quantityBigDecimal.divide(contractMultiplier, RoundingMode.HALF_EVEN).setScale(0, RoundingMode.HALF_EVEN)
					.multiply(contractMultiplier);

			if (quantityBigDecimal.doubleValue() != quantity)
				return false;
		}

		return true;
	}

	private boolean isValidPrice(FSecurity security, Double price) {

		if (security == null)
			return false;

		if (price == null)
			return true;

		if (security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {

			BigDecimal priceBigDecimal = new BigDecimal(decimalFormat.format(price));

			BigDecimal minPriceIncrement = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));

			priceBigDecimal = priceBigDecimal.divide(minPriceIncrement, RoundingMode.HALF_EVEN).setScale(0, RoundingMode.HALF_EVEN).multiply(minPriceIncrement);

			if (priceBigDecimal.doubleValue() != price)
				return false;
		}

		return true;
	}

}
