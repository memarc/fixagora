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
package net.sourceforge.fixagora.sellside.server.control.component;

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
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry.Side;
import net.sourceforge.fixagora.sellside.shared.communication.CloseQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.CloseSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.HistoricalSellSideDataResponse;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.TimeInForce;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestRequest;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideTradeCaptureTarget;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBookTradeCaptureReport;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest;

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
 * The Class SellSideBookComponentHandler.
 */
public class SellSideBookComponentHandler extends AbstractComponentHandler {

	private SellSideBook sellSideBook = null;

	private Map<Long, Map<String, Group>> refIDMap = new HashMap<Long, Map<String, Group>>();

	private Map<Long, SellSideMDInputEntry> securityEntries = Collections.synchronizedMap(new HashMap<Long, SellSideMDInputEntry>());

	private Map<Long, List<SellSideMDInputEntry>> historicalData = new HashMap<Long, List<SellSideMDInputEntry>>();

	private Map<Long, AssignedSellSideBookSecurity> assignedSellSideBookSecurities = new HashMap<Long, AssignedSellSideBookSecurity>();

	private Map<Channel, Set<SellSideQuotePage>> channels = new HashMap<Channel, Set<SellSideQuotePage>>();

	private boolean closed = false;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0###################");

	private DecimalFormat decimalFormat3 = new DecimalFormat("####");

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<Channel> bookSessions = new HashSet<Channel>();

	private Set<AbstractComponentHandler> onlineTradeCaptureComponentHandlers = new HashSet<AbstractComponentHandler>();

	private Map<String, SellSideNewOrderSingleEntry> sellSideNewOrderSingleEntryMap = new HashMap<String, SellSideNewOrderSingleEntry>();

	private Map<String, SellSideQuoteRequestEntry> sellSideQuoteRequestEntryMap = new HashMap<String, SellSideQuoteRequestEntry>();

	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");

	private static Logger log = Logger.getLogger(SellSideBookComponentHandler.class);

	private LinkedBlockingQueue<MessageEntry> messageQueue = new LinkedBlockingQueue<MessageEntry>();

	private Timer timer;
	private Set<SellSideMDInputEntry> periodicUpdateSet = new HashSet<SellSideMDInputEntry>();

	/**
	 * Instantiates a new sell side book component handler.
	 */
	public SellSideBookComponentHandler() {

		super();

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {

				writeMDInputEntries();
				checkQuoteRequests();

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

		if (abstractBusinessComponent instanceof SellSideBook) {
			sellSideBook = (SellSideBook) abstractBusinessComponent;
		}

		assignedSellSideBookSecurities.clear();

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBook.getAssignedSellSideBookSecurities())
			assignedSellSideBookSecurities.put(assignedSellSideBookSecurity.getSecurity().getId(), assignedSellSideBookSecurity);

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		calendar.set(java.util.Calendar.MINUTE, 0);
		calendar.set(java.util.Calendar.SECOND, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);

		List<Object> parameters = new ArrayList<Object>();
		parameters.add(sellSideBook.getId());
		parameters.add(calendar.getTimeInMillis());

		List<SellSideNewOrderSingle> newOrderSingleEntries = basisPersistenceHandler.executeQuery(SellSideNewOrderSingle.class,
				"select a from SellSideNewOrderSingle a where a.sellSideBook.id=?1 and (a.createdTimestamp>=?2 or a.leaveQuantity>0)", parameters,
				businessComponentHandler, true);
		for (SellSideNewOrderSingle newOrderSingleEntry : newOrderSingleEntries) {

			SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = getSellSideNewOrderSingleEntry(newOrderSingleEntry);
			sellSideNewOrderSingleEntryMap.put(sellSideNewOrderSingleEntry.getOrderId(), sellSideNewOrderSingleEntry);
		}

		List<SellSideQuoteRequest> sellSideQuoteRequests = basisPersistenceHandler.executeQuery(SellSideQuoteRequest.class,
				"select a from SellSideQuoteRequest a where a.sellSideBook.id=?1 and (a.createdTimestamp>=?2 or a.leaveQuantity>0)", parameters,
				businessComponentHandler, true);

		for (SellSideQuoteRequest sellSideQuoteRequest : sellSideQuoteRequests) {

			SellSideQuoteRequestEntry sellSideNewOrderSingleEntry = getSellSideQuoteRequestEntry(sellSideQuoteRequest);
			sellSideQuoteRequestEntryMap.put(
					sellSideNewOrderSingleEntry.getQuoteReqId() + decimalFormat3.format(sellSideNewOrderSingleEntry.getCounterparty()),
					sellSideNewOrderSingleEntry);
		}

		onChangeOfDay();

	}

	private SellSideQuoteRequestEntry getSellSideQuoteRequestEntry(SellSideQuoteRequest buySideQuoteRequest) {

		SellSideQuoteRequestEntry buySideQuoteRequestEntry = new SellSideQuoteRequestEntry();
		buySideQuoteRequestEntry.setSellSideBook(buySideQuoteRequest.getSellSideBook().getId());
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
		buySideQuoteRequestEntry.setQuoteReqId(buySideQuoteRequest.getQuoteReqId());
		buySideQuoteRequestEntry.setQuoteRespId(buySideQuoteRequest.getQuoteRespId());
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
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COUNTER);
				break;
			case COVER:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COVER);
				break;
			case DONE_AWAY:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.DONE_AWAY);
				break;
			case EXPIRED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.EXPIRED);
				break;
			case FILLED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED);
				break;
			case FILLED_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED_PENDING);
				break;
			case HIT_LIFT:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.HIT_LIFT);
				break;
			case NEW:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.NEW);
				break;
			case PASS:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.PASS);
				break;
			case QUOTED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED);
				break;
			case QUOTED_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED_PENDING);
				break;
			case REJECTED:
				buySideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED);
				break;
			case REJECTED_PENDING:
				buySideQuoteRequestEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING);
				break;

		}

		buySideQuoteRequestEntry.setSecurity(buySideQuoteRequest.getSecurity().getId());

		buySideQuoteRequestEntry.setSide(Side.BID);

		if (buySideQuoteRequest.getSide() == net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.Side.ASK)
			buySideQuoteRequestEntry.setSide(Side.ASK);

		buySideQuoteRequestEntry.setUpdated(buySideQuoteRequest.getUpdatedTimestamp());
		if (buySideQuoteRequest.getfUser() != null)
			buySideQuoteRequestEntry.setUser(buySideQuoteRequest.getfUser().getName());

		return buySideQuoteRequestEntry;
	}

	private synchronized void checkQuoteRequests() {

		long timestamp = System.currentTimeMillis();

		Set<SellSideQuoteRequestEntry> sellSideQuoteRequestEntries = new HashSet<SellSideQuoteRequestEntry>(sellSideQuoteRequestEntryMap.values());

		for (SellSideQuoteRequestEntry sellSideQuoteRequestEntry : sellSideQuoteRequestEntries) {

			try {
				if (sellSideQuoteRequestEntry.getExpireDate() <= timestamp && !sellSideQuoteRequestEntry.isDone()) {

					if (!sendQuoteRequestReject(sellSideQuoteRequestEntry, null)) {
						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);
					}

				}
				else if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING) {

					if (sellSideQuoteRequestEntry.getQuoteRespId() == null)
						sendQuoteRequestReject(sellSideQuoteRequestEntry, null);
					else
						sendQuoteStatusReport(sellSideQuoteRequestEntry, null);

				}
				else if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED_PENDING) {

					if (sendExecutionReport(sellSideQuoteRequestEntry, false)) {
						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);
					}

				}
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}

	}

	private SellSideNewOrderSingleEntry getSellSideNewOrderSingleEntry(SellSideNewOrderSingle newOrderSingleEntry) {

		SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = new SellSideNewOrderSingleEntry();
		sellSideNewOrderSingleEntry.setSellSideBook(newOrderSingleEntry.getSellSideBook().getId());
		sellSideNewOrderSingleEntry.setCounterparty(newOrderSingleEntry.getCounterparty().getId());
		sellSideNewOrderSingleEntry.setCounterpartyOrderId(newOrderSingleEntry.getCounterpartyOrderId());
		sellSideNewOrderSingleEntry.setCreated(newOrderSingleEntry.getCreatedTimestamp());
		sellSideNewOrderSingleEntry.setCumulativeQuantity(newOrderSingleEntry.getCumulativeQuantity());
		sellSideNewOrderSingleEntry.setLastPrice(newOrderSingleEntry.getLastPrice());
		sellSideNewOrderSingleEntry.setLastYield(newOrderSingleEntry.getLastYield());
		sellSideNewOrderSingleEntry.setLastQuantity(newOrderSingleEntry.getLastQuantity());
		sellSideNewOrderSingleEntry.setLimit(newOrderSingleEntry.getLimit());
		sellSideNewOrderSingleEntry.setNewOrderId(newOrderSingleEntry.getNewOrderId());
		sellSideNewOrderSingleEntry.setNewLimit(newOrderSingleEntry.getNewLimit());
		sellSideNewOrderSingleEntry.setNewOrderQuantity(newOrderSingleEntry.getNewQuantity());
		sellSideNewOrderSingleEntry.setLeaveQuantity(newOrderSingleEntry.getLeaveQuantity());
		sellSideNewOrderSingleEntry.setMinQuantity(newOrderSingleEntry.getMinQuantity());
		sellSideNewOrderSingleEntry.setMarketInterface(newOrderSingleEntry.getMarketInterface().getId());
		sellSideNewOrderSingleEntry.setOrderId(newOrderSingleEntry.getOrderId());
		sellSideNewOrderSingleEntry.setOrderQuantity(newOrderSingleEntry.getOrderQuantity());
		sellSideNewOrderSingleEntry.setTifDate(newOrderSingleEntry.getTifTimestamp());
		sellSideNewOrderSingleEntry.setSettlementDate(newOrderSingleEntry.getSettlementDate());

		switch (newOrderSingleEntry.getOrderStatus()) {
			case DONE_FOR_DAY_PENDING:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.DONE_FOR_DAY_PENDING);
				break;
			case CANCEL:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.CANCEL);
				break;
			case FILLED_PENDING:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.FILLED_PENDING);
				break;
			case DONE_FOR_DAY:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.DONE_FOR_DAY);
				break;
			case NEW:
				sellSideNewOrderSingleEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.NEW);
				break;
			case PARTIALLY_FILLED_PENDING:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.PARTIALLY_FILLED_PENDING);
				break;
			case REJECTED:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.REJECTED);
				break;
			case FILLED:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.FILLED);
				break;
			case PARTIALLY_FILLED:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.PARTIALLY_FILLED);
				break;
			case REJECTED_PENDING:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.REJECTED_PENDING);
				break;

			case MODIFIED:
				sellSideNewOrderSingleEntry
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus.MODIFIED);
				break;
		}
		sellSideNewOrderSingleEntry.setSecurity(newOrderSingleEntry.getSecurity().getId());
		sellSideNewOrderSingleEntry.setSide(Side.BID);
		if (newOrderSingleEntry.getSide() == net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.Side.ASK)
			sellSideNewOrderSingleEntry.setSide(Side.ASK);
		switch (newOrderSingleEntry.getTimeInForce()) {
			case FILL_OR_KILL:
				sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);
				break;
			case GOOD_TILL_CANCEL:
				sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_CANCEL);
				break;
			case GOOD_TILL_DATE:
				sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
				break;
			case GOOD_TILL_DAY:
				sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DAY);
				break;
		}

		sellSideNewOrderSingleEntry.setUpdated(newOrderSingleEntry.getUpdatedTimestamp());

		if (newOrderSingleEntry.getTrader() != null)
			sellSideNewOrderSingleEntry.setTrader(newOrderSingleEntry.getTrader().getId());

		if (newOrderSingleEntry.getfUser() != null)
			sellSideNewOrderSingleEntry.setUser(newOrderSingleEntry.getfUser().getName());

		return sellSideNewOrderSingleEntry;
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

		return sellSideBook;
	}

	private void writeMDInputEntries() {

		if (closed)
			timer.cancel();

		List<SellSideMDInputEntry> sellSideMDInputEntries = new ArrayList<SellSideMDInputEntry>();
		synchronized (periodicUpdateSet) {

			for (SellSideMDInputEntry sellSideCounterpartyMDInputEntry : periodicUpdateSet) {
				sellSideMDInputEntries.add(sellSideCounterpartyMDInputEntry);
			}
			sellSideMDInputEntries.addAll(periodicUpdateSet);
			periodicUpdateSet.clear();
		}

		List<SellSideMDInputEntry> sellSideMDInputEntries2 = new ArrayList<SellSideMDInputEntry>();

		for (SellSideMDInputEntry sellSideMDInputEntry : sellSideMDInputEntries) {

			synchronized (sellSideMDInputEntry) {

				FSecurity security = securityDictionary.getSecurityForBusinessObjectID(sellSideMDInputEntry.getSecurityValue());

				if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

					Double bidPrice = getPrice(security, sellSideMDInputEntry.getMdEntryBidYieldValue());

					if (sellSideMDInputEntry.getMdEntryBidPxValue() == null)
						sellSideMDInputEntry.setMdBidPriceDeltaValue(0d);
					else if (sellSideMDInputEntry.getMdEntryBidPxValue() != bidPrice) {
						if (sellSideBook.getAbsolutChange())
							sellSideMDInputEntry.setMdBidPriceDeltaValue(bidPrice - sellSideMDInputEntry.getMdEntryBidPxValue());
						else
							sellSideMDInputEntry.setMdBidPriceDeltaValue(100d * (bidPrice - sellSideMDInputEntry.getMdEntryBidPxValue())
									/ sellSideMDInputEntry.getMdEntryBidPxValue());
					}

					sellSideMDInputEntry.setMdEntryBidPxValue(bidPrice);

					Double askPrice = getPrice(security, sellSideMDInputEntry.getMdEntryAskYieldValue());

					if (sellSideMDInputEntry.getMdEntryAskPxValue() == null)
						sellSideMDInputEntry.setMdAskPriceDeltaValue(0d);
					else if (sellSideMDInputEntry.getMdEntryAskPxValue() != askPrice) {
						if (sellSideBook.getAbsolutChange())
							sellSideMDInputEntry.setMdAskPriceDeltaValue(askPrice - sellSideMDInputEntry.getMdEntryAskPxValue());
						else
							sellSideMDInputEntry.setMdAskPriceDeltaValue(100d * (askPrice - sellSideMDInputEntry.getMdEntryAskPxValue())
									/ sellSideMDInputEntry.getMdEntryAskPxValue());
					}

					sellSideMDInputEntry.setMdEntryAskPxValue(askPrice);
				}
				else {
					sellSideMDInputEntry.setMdEntryBidYieldValue(getYield(security, sellSideMDInputEntry.getMdEntryBidPxValue()));
					sellSideMDInputEntry.setMdEntryAskYieldValue(getYield(security, sellSideMDInputEntry.getMdEntryAskPxValue()));
				}

				if (sellSideMDInputEntry.getMdEntryBidPxValue() != null) {
					if (sellSideMDInputEntry.getHighBidPxValue() == null
							|| sellSideMDInputEntry.getHighBidPxValue() < sellSideMDInputEntry.getMdEntryBidPxValue())
						sellSideMDInputEntry.setHighBidPxValue(sellSideMDInputEntry.getMdEntryBidPxValue());

					if (sellSideMDInputEntry.getLowBidPxValue() == null
							|| sellSideMDInputEntry.getLowBidPxValue() > sellSideMDInputEntry.getMdEntryBidPxValue())
						sellSideMDInputEntry.setLowBidPxValue(sellSideMDInputEntry.getMdEntryBidPxValue());
				}

				if (sellSideMDInputEntry.getMdEntryAskPxValue() != null) {
					if (sellSideMDInputEntry.getHighAskPxValue() == null
							|| sellSideMDInputEntry.getHighAskPxValue() < sellSideMDInputEntry.getMdEntryAskPxValue())
						sellSideMDInputEntry.setHighAskPxValue(sellSideMDInputEntry.getMdEntryAskPxValue());

					if (sellSideMDInputEntry.getLowAskPxValue() == null
							|| sellSideMDInputEntry.getLowAskPxValue() > sellSideMDInputEntry.getMdEntryAskPxValue())
						sellSideMDInputEntry.setLowAskPxValue(sellSideMDInputEntry.getMdEntryAskPxValue());
				}

				sellSideMDInputEntry.setTime(System.currentTimeMillis());

				SellSideMDInputEntry historicalBuySideCompositeMDInputEntry = sellSideMDInputEntry.clone();
				List<SellSideMDInputEntry> historicalEntries = historicalData.get(historicalBuySideCompositeMDInputEntry.getSecurityValue());
				if (historicalEntries == null) {
					historicalEntries = new ArrayList<SellSideMDInputEntry>();
					historicalData.put(historicalBuySideCompositeMDInputEntry.getSecurityValue(), historicalEntries);
				}
				historicalEntries.add(historicalBuySideCompositeMDInputEntry);

				sellSideMDInputEntries2.add(sellSideMDInputEntry.clone());
			}

		}

		for (Channel channel : channels.keySet()) {
			Set<SellSideMDInputEntry> sellSideMDInputEntries3 = new HashSet<SellSideMDInputEntry>();
			for (SellSideQuotePage sellSideQuotePage : channels.get(channel)) {
				for (SellSideMDInputEntry sellSideMDInputEntry : sellSideMDInputEntries2) {
					AssignedSellSideBookSecurity assignedSellSideBookSecurity = assignedSellSideBookSecurities.get(sellSideMDInputEntry.getSecurityValue());
					if (assignedSellSideBookSecurity != null && sellSideQuotePage.getAssignedSellSideBookSecurities().contains(assignedSellSideBookSecurity))
						sellSideMDInputEntries3.add(sellSideMDInputEntry);
				}
			}
			if (sellSideMDInputEntries3.size() > 0)
				basisPersistenceHandler.send(new UpdateSellSideMDInputEntryResponse(new ArrayList<SellSideMDInputEntry>(sellSideMDInputEntries3)), channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	protected synchronized void processFIXMessage(List<MessageEntry> messages) {

		Set<SellSideMDInputEntry> mdInputEntries = new HashSet<SellSideMDInputEntry>();
		for (MessageEntry messageEntry : messages) {

			try {
				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);
				if (messageType.equals("W")) {
					if (message.isSetField(48)) {
						String securityID = message.getString(48);
						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID);
						if (security != null) {
							SellSideMDInputEntry mdInputEntry = securityEntries.get(security.getId());
							if (mdInputEntry == null) {
								mdInputEntry = new SellSideMDInputEntry();
								mdInputEntry.setSecurityValue(security.getId());
								securityEntries.put(security.getId(), mdInputEntry);
							}

							synchronized (mdInputEntry) {

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

														value = value.divide(tickSize, RoundingMode.UP).setScale(0, RoundingMode.UP).multiply(tickSize);
														mdEntries.setDouble(270, value.doubleValue());
														mdInputEntry.setMdEntryBidYieldValue(value.doubleValue());
													}
													else {

														value = value.divide(tickSize, RoundingMode.DOWN).setScale(0, RoundingMode.DOWN).multiply(tickSize);
														mdEntries.setDouble(270, value.doubleValue());
														mdInputEntry.setMdEntryAskYieldValue(value.doubleValue());
													}
												}
												else {

													BigDecimal tickSize = new BigDecimal("0.001");
													if (security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {
														tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
													}

													if (bid) {

														value = value.divide(tickSize, RoundingMode.DOWN).setScale(0, RoundingMode.DOWN).multiply(tickSize);
														mdEntries.setDouble(270, value.doubleValue());

														if (mdInputEntry.getMdEntryBidPxValue() == null)
															mdInputEntry.setMdBidPriceDeltaValue(0d);
														else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue()) {
															if (sellSideBook.getAbsolutChange())
																mdInputEntry.setMdBidPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryBidPxValue());
															else
																mdInputEntry.setMdBidPriceDeltaValue(100d
																		* (value.doubleValue() - mdInputEntry.getMdEntryBidPxValue())
																		/ mdInputEntry.getMdEntryBidPxValue());
														}
														mdInputEntry.setMdEntryBidPxValue(value.doubleValue());
													}
													else {

														value = value.divide(tickSize, RoundingMode.UP).setScale(0, RoundingMode.UP).multiply(tickSize);
														mdEntries.setDouble(270, value.doubleValue());

														if (mdInputEntry.getMdEntryAskPxValue() == null)
															mdInputEntry.setMdAskPriceDeltaValue(0d);
														else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue()) {
															if (sellSideBook.getAbsolutChange())
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
								if (security != null) {
									SellSideMDInputEntry mdInputEntry = securityEntries.get(security.getId());
									if (mdInputEntry == null) {
										mdInputEntry = new SellSideMDInputEntry();
										mdInputEntry.setSecurityValue(security.getId());
										securityEntries.put(security.getId(), mdInputEntry);
									}

									synchronized (mdInputEntry) {

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

																value = value.divide(tickSize, RoundingMode.UP).setScale(0, RoundingMode.UP).multiply(tickSize);
																mdEntries.setDouble(270, value.doubleValue());
																mdInputEntry.setMdEntryBidYieldValue(value.doubleValue());
															}
															else {

																value = value.divide(tickSize, RoundingMode.DOWN).setScale(0, RoundingMode.DOWN)
																		.multiply(tickSize);
																mdEntries.setDouble(270, value.doubleValue());
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

																value = value.divide(tickSize, RoundingMode.DOWN).setScale(0, RoundingMode.DOWN)
																		.multiply(tickSize);
																mdEntries.setDouble(270, value.doubleValue());

																if (mdInputEntry.getMdEntryBidPxValue() == null)
																	mdInputEntry.setMdBidPriceDeltaValue(0d);
																else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue()) {
																	if (sellSideBook.getAbsolutChange())
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

																value = value.divide(tickSize, RoundingMode.UP).setScale(0, RoundingMode.UP).multiply(tickSize);
																mdEntries.setDouble(270, value.doubleValue());

																if (mdInputEntry.getMdEntryAskPxValue() == null)
																	mdInputEntry.setMdAskPriceDeltaValue(0d);
																else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue()) {
																	if (sellSideBook.getAbsolutChange())
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
				else
					messageQueue.add(messageEntry);

			}
			catch (FieldNotFound e) {
				log.error("Bug", e);
			}

			for (AbstractComponentHandler abstractComponentHandler : outputComponents) {

				MessageEntry messageEntry2 = new MessageEntry(messageEntry.getInitialComponent(), sellSideBook, messageEntry.getMessage());
				abstractComponentHandler.addFIXMessage(messageEntry2);
			}
		}

		synchronized (periodicUpdateSet) {

			periodicUpdateSet.addAll(mdInputEntries);
		}
	}

	private void handleSecurityListRequest(Message message, AbstractBusinessComponent initialComponent) {

		try {
			Message securityList = new Message();
			securityList.getHeader().setString(MsgType.FIELD, "y");
			securityList.setString(320, message.getString(320));
			securityList.setString(322, message.getString(320));
			securityList.setInt(560, 0);

			for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBook.getAssignedSellSideBookSecurities()) {
				FSecurity instrument = assignedSellSideBookSecurity.getSecurity();
				final Group group = new Group(146, 55, new int[] { 55, 48, 22 });
				group.setString(55, "N/A");
				String securityID = instrument.getSecurityID();
				if (initialComponent instanceof AbstractAcceptor) {
					AbstractAcceptor abstractAcceptor = (AbstractAcceptor) initialComponent;
					if (abstractAcceptor.getSecurityIDSource() != null)
						securityID = securityDictionary.getAlternativeSecurityID(securityID, abstractAcceptor.getSecurityIDSource());
				}
				if (securityID != null) {
					group.setString(48, securityID);
					if (instrument.getSecurityDetails().getSecurityIDSource() != null
							&& instrument.getSecurityDetails().getSecurityIDSource().trim().length() > 0) {
						group.setString(22, instrument.getSecurityDetails().getSecurityIDSource());
					}
					securityList.addGroup(group);
				}
			}

			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(initialComponent.getId());
			MessageEntry messageEntry = new MessageEntry(getAbstractBusinessComponent(), getAbstractBusinessComponent(), securityList);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	private void handleOrderMassStatusRequest(Message message, AbstractBusinessComponent initialComponent) {

		try {

			String partyIdSource = null;
			String partyId = null;

			for (int i = 1; i <= message.getGroupCount(453); i++) {

				Group parties = message.getGroup(i, 453);
				if (parties.isSetField(452) && parties.getInt(452) == 13) {

					if (parties.isSetField(447))
						partyIdSource = parties.getString(447);

					if (parties.isSetField(448))
						partyId = parties.getString(448);
				}
			}

			Counterparty counterparty = businessComponentHandler.getCounterparty(initialComponent.getId(), partyId, partyIdSource, 13);

			if (counterparty != null) {
				for (SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry : sellSideNewOrderSingleEntryMap.values()) {

					if (sellSideNewOrderSingleEntry.getCounterparty() == counterparty.getId()) {

						sendExecutionReport(sellSideNewOrderSingleEntry, true);
					}
				}
			}
		}
		catch (Exception exception) {
			log.error("Bug", exception);
		}

	}

	private boolean sendTradeCaptureReport(SellSideBookTradeCaptureReport sellSideBookTradeCaptureReport, AbstractBusinessComponent targetComponent) {

		Message tradeCaptureReport = new Message();
		tradeCaptureReport.getHeader().setString(MsgType.FIELD, "AE");
		tradeCaptureReport.setString(37, sellSideBookTradeCaptureReport.getOrderId());
		tradeCaptureReport.setString(17, sellSideBookTradeCaptureReport.getExecId());
		tradeCaptureReport.setString(11, sellSideBookTradeCaptureReport.getCounterpartyOrderId());
		if (sellSideBookTradeCaptureReport.getSide() == net.sourceforge.fixagora.sellside.shared.persistence.SellSideBookTradeCaptureReport.Side.BUY)
			tradeCaptureReport.setChar(54, '1');
		else
			tradeCaptureReport.setChar(54, '2');
		tradeCaptureReport.setDouble(14, sellSideBookTradeCaptureReport.getCumulativeQuantity());
		tradeCaptureReport.setDouble(151, sellSideBookTradeCaptureReport.getLeaveQuantity());
		tradeCaptureReport.setDouble(38, sellSideBookTradeCaptureReport.getOrderQuantity());
		tradeCaptureReport.setString(37, sellSideBookTradeCaptureReport.getOrderId());
		tradeCaptureReport.setString(1003, sellSideBookTradeCaptureReport.getTradeId());

		if (sellSideBookTradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod() != null
				&& sellSideBookTradeCaptureReport.getSecurity().getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
			tradeCaptureReport.setDouble(669, sellSideBookTradeCaptureReport.getLastPrice());
			tradeCaptureReport.setDouble(31, sellSideBookTradeCaptureReport.getLastYield());
		}
		else
			tradeCaptureReport.setDouble(31, sellSideBookTradeCaptureReport.getLastPrice());
		tradeCaptureReport.setDouble(32, sellSideBookTradeCaptureReport.getLastQuantity());
		if (sellSideBookTradeCaptureReport.getSettlementDate() != null)
			tradeCaptureReport.setString(64, localMarketDateFormat.format(new Date(sellSideBookTradeCaptureReport.getSettlementDate())));

		tradeCaptureReport.setString(48, sellSideBookTradeCaptureReport.getSecurity().getSecurityID());
		tradeCaptureReport.setString(22, sellSideBookTradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());

		AbstractAcceptor abstractAcceptor = sellSideBookTradeCaptureReport.getSourceComponent();

		Counterparty counterparty = sellSideBookTradeCaptureReport.getCounterparty();

		if (abstractAcceptor != null) {

			final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
			group.setString(448, abstractAcceptor.getMarketName());
			group.setChar(447, 'D');
			group.setInt(452, 16);
			tradeCaptureReport.addGroup(group);

			if (sellSideBookTradeCaptureReport.getfUser() != null)
				addUserGroup(abstractAcceptor, sellSideBookTradeCaptureReport.getfUser(), tradeCaptureReport);

			if (counterparty != null) {

				CounterPartyPartyID contraFirm = null;
				CounterPartyPartyID orderOriginationFirm = null;

				for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
					if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
						if (contraFirm == null) {
							contraFirm = counterPartyPartyID;
						}
						if (orderOriginationFirm == null) {
							orderOriginationFirm = counterPartyPartyID;
						}
					}
					else {
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == sellSideBook.getId() && counterPartyPartyID.getPartyRole() != null
								&& counterPartyPartyID.getPartyRole().intValue() == 17) {
							contraFirm = counterPartyPartyID;
						}
						if (counterPartyPartyID.getAbstractBusinessComponent().getId() == sellSideBookTradeCaptureReport.getSourceComponent().getId()
								&& counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole().intValue() == 13) {
							orderOriginationFirm = counterPartyPartyID;
						}
					}
				}

				if (contraFirm == null && orderOriginationFirm != null) {
					contraFirm = new CounterPartyPartyID();
					contraFirm.setPartyID(orderOriginationFirm.getPartyID());
					contraFirm.setPartyIDSource(orderOriginationFirm.getPartyIDSource());
					contraFirm.setAbstractBusinessComponent(sellSideBook);
					contraFirm.setPartyRole(17);
					contraFirm.setCounterparty(counterparty);
					counterparty.getCounterPartyPartyIDs().add(contraFirm);
					List<AbstractBusinessObject> counterparties = new ArrayList<AbstractBusinessObject>();
					counterparties.add(counterparty);

					StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Firm\" defined for counterparty ");
					stringBuffer.append(counterparty.getName());
					stringBuffer
							.append(". System creates a new party role taking the values from \"Order Origination Firm\". This role is needed to process the trade capture.");

					LogEntry logEntry = new LogEntry();
					logEntry.setLogLevel(Level.WARNING);
					logEntry.setLogDate(new Date());
					logEntry.setMessageComponent(sellSideBook.getName());
					logEntry.setMessageText(stringBuffer.toString());
					logEntry.setHighlightKey(sellSideBookTradeCaptureReport.getOrderId());
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

				if (sellSideBookTradeCaptureReport.getTrader() != null) {

					Trader trader = sellSideBookTradeCaptureReport.getTrader();

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
							if (traderPartyID.getAbstractBusinessComponent().getId() == sellSideBook.getId() && traderPartyID.getPartyRole() != null
									&& traderPartyID.getPartyRole().intValue() == 37) {
								contraTraderPartyID = traderPartyID;

							}
							if (traderPartyID.getAbstractBusinessComponent().getId() == sellSideBookTradeCaptureReport.getSourceComponent().getId()
									&& traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole().intValue() == 11) {
								orderOriginatingTraderPartyID = traderPartyID;
							}
						}
					}

					if (contraTraderPartyID == null && orderOriginatingTraderPartyID != null) {
						contraTraderPartyID = new TraderPartyID();
						contraTraderPartyID.setPartyID(orderOriginatingTraderPartyID.getPartyID());
						contraTraderPartyID.setPartyIDSource(orderOriginatingTraderPartyID.getPartyIDSource());
						contraTraderPartyID.setAbstractBusinessComponent(sellSideBook);
						contraTraderPartyID.setPartyRole(37);
						contraTraderPartyID.setTrader(trader);
						trader.getTraderPartyIDs().add(contraTraderPartyID);
						List<AbstractBusinessObject> traders = new ArrayList<AbstractBusinessObject>();
						traders.add(trader);

						StringBuffer stringBuffer = new StringBuffer("No party role \"Contra Trader\" defined for trader ");
						stringBuffer.append(sellSideBookTradeCaptureReport.getTrader().getName());
						stringBuffer
								.append(". System creates a new party role taking the values from \"Order Origination Trader\". This role is needed to process the trade capture.");

						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(sellSideBook.getName());
						logEntry.setMessageText(stringBuffer.toString());
						logEntry.setHighlightKey(sellSideBookTradeCaptureReport.getOrderId());
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

			tradeCaptureReport.setString(48, sellSideBookTradeCaptureReport.getSecurity().getSecurityID());
			tradeCaptureReport.setString(22, sellSideBookTradeCaptureReport.getSecurity().getSecurityDetails().getSecurityIDSource());
			tradeCaptureReport.setString(55, "N/A");
			MessageEntry messageEntry = new MessageEntry(sellSideBook, sellSideBook, tradeCaptureReport);
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(targetComponent.getId());
			if (abstractComponentHandler != null)
				abstractComponentHandler.addFIXMessage(messageEntry);
		}

		return true;

	}

	private synchronized void handleOrderReplace(Message message, AbstractBusinessComponent initialComponent) {

		try {

			if (message.isSetField(41) && message.isSetField(38)) {

				for (SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry : sellSideNewOrderSingleEntryMap.values()) {

					if (message.getString(41).equals(sellSideNewOrderSingleEntry.getCounterpartyOrderId())) {

						if (sellSideNewOrderSingleEntry.getLeaveQuantity() > 0 && sellSideNewOrderSingleEntry.getCumulativeQuantity() < message.getDouble(38)) {

							String partyId = null;
							String partyIdSource = null;

							for (int i = 1; i <= message.getGroupCount(453); i++) {

								Group parties = message.getGroup(i, 453);
								if (parties.isSetField(452) && parties.getInt(452) == 13) {

									if (parties.isSetField(447))
										partyIdSource = parties.getString(447);

									if (parties.isSetField(448))
										partyId = parties.getString(448);
								}
							}

							Counterparty counterparty = businessComponentHandler.getCounterparty(initialComponent.getId(), partyId, partyIdSource, 13);
							if (counterparty != null) {

								sellSideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (message.isSetField(11)) {

									sellSideNewOrderSingleEntry.setOriginalOrderId(sellSideNewOrderSingleEntry.getCounterpartyOrderId());
									sellSideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(11));
									sellSideNewOrderSingleEntry.setOrderQuantity(message.getDouble(38));
									sellSideNewOrderSingleEntry.setLeaveQuantity(message.getDouble(38) - sellSideNewOrderSingleEntry.getCumulativeQuantity());
									sellSideNewOrderSingleEntry.setCreated(System.currentTimeMillis());

									if (message.isSetField(44))
										sellSideNewOrderSingleEntry.setLimit(message.getDouble(44));
									else
										sellSideNewOrderSingleEntry.setLimit(null);
									sellSideNewOrderSingleEntry.setMarketInterface(initialComponent.getId());
									if (message.isSetField(110))
										sellSideNewOrderSingleEntry.setMinQuantity(message.getDouble(110));

									String securityId = null;
									String securityIdSource = null;
									if (message.isSetField(48))
										securityId = message.getString(48);
									if (message.isSetField(22))
										securityIdSource = message.getString(22);

									FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);
									if (security != null) {

										sellSideNewOrderSingleEntry.setSecurity(security.getId());
										sellSideNewOrderSingleEntry.setSellSideBook(sellSideBook.getId());
										if (message.isSetField(54)) {

											if (message.getString(54).equals("1"))
												sellSideNewOrderSingleEntry.setSide(Side.ASK);
											else if (message.getString(54).equals("2"))
												sellSideNewOrderSingleEntry.setSide(Side.BID);
											else {

											}
											sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);
											if (message.isSetField(64))
												sellSideNewOrderSingleEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());
											java.util.Calendar calendar = java.util.Calendar.getInstance();
											calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
											calendar.set(java.util.Calendar.MINUTE, 0);
											calendar.set(java.util.Calendar.SECOND, 0);
											calendar.set(java.util.Calendar.MILLISECOND, 0);
											sellSideNewOrderSingleEntry.setTifDate(calendar.getTimeInMillis());
											if (message.isSetField(59)) {
												if (message.getString(59).equals("4")) {
													sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);

												}
												if (message.getString(59).equals("1")) {
													sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_CANCEL);
												}
												if (message.getString(59).equals("6")) {
													sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
													if (message.isSetField(432))
														sellSideNewOrderSingleEntry.setTifDate(message.getUtcDateOnly(432).getTime());
												}
												if (message.getString(59).equals("0")) {
													sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DAY);
												}

											}
											if (isValidPrice(security, sellSideNewOrderSingleEntry.getLimit())
													&& isValidQuantity(security, sellSideNewOrderSingleEntry.getOrderQuantity())) {

												sellSideNewOrderSingleEntry.setUpdated(System.currentTimeMillis());
												sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.MODIFIED);
												persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, true);
												sellSideNewOrderSingleEntryMap.put(sellSideNewOrderSingleEntry.getOrderId(), sellSideNewOrderSingleEntry);

												sendExecutionReport(sellSideNewOrderSingleEntry, false);

											}
											else {

												sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
												sendExecutionReport(sellSideNewOrderSingleEntry, false);

												StringBuffer stringBuffer = new StringBuffer("System automatically rejected order for security ");
												stringBuffer.append(security.getName());
												stringBuffer.append(" from counterparty ");
												stringBuffer.append(counterparty.getName());
												stringBuffer.append(". Reject reason was an invalid ");
												if (!isValidPrice(security, sellSideNewOrderSingleEntry.getLimit()))
													stringBuffer.append("limit for an order replacement.");
												if (!isValidQuantity(security, sellSideNewOrderSingleEntry.getOrderQuantity()))
													stringBuffer.append("quantity for an order replacement.");
												stringBuffer.append(" Please contact your counterparty.");
												LogEntry logEntry = new LogEntry();
												logEntry.setLogLevel(Level.WARNING);
												logEntry.setLogDate(new Date());
												logEntry.setMessageComponent(sellSideBook.getName());
												logEntry.setMessageText(stringBuffer.toString());
												logEntry.setHighlightKey(sellSideNewOrderSingleEntry.getOrderId());
												basisPersistenceHandler.writeLogEntry(logEntry);
											}

										}
									}
								}
							}
							return;
						}
						else {
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
							sellSideNewOrderSingleEntry.setLeaveQuantity(0);
							if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
								persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
							else
								sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY_PENDING);
							return;
						}
					}
				}
				sendOrderCancelReject(message, "NONE", initialComponent);
			}
			else
				sendOrderCancelReject(message, "NONE", initialComponent);
		}
		catch (FieldNotFound e) {
			sendOrderCancelReject(message, "NONE", initialComponent);
		}
	}

	private synchronized void handleOrderCancel(Message message, AbstractBusinessComponent initialComponent) {

		try {

			if (message.isSetField(41)) {
				for (SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry : sellSideNewOrderSingleEntryMap.values()) {

					if (message.getString(41).equals(sellSideNewOrderSingleEntry.getCounterpartyOrderId())) {
						if (sellSideNewOrderSingleEntry.getLeaveQuantity() > 0) {
							sellSideNewOrderSingleEntry.setLeaveQuantity(0);
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL);
							persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, true);
							sendExecutionReport(sellSideNewOrderSingleEntry, false);
							return;
						}
						else
							sendOrderCancelReject(message, sellSideNewOrderSingleEntry.getOrderId(), initialComponent);
					}
				}
				sendOrderCancelReject(message, "NONE", initialComponent);
			}
			else
				sendOrderCancelReject(message, "NONE", initialComponent);
		}
		catch (FieldNotFound e) {
			sendOrderCancelReject(message, "NONE", initialComponent);
		}

	}

	private void sendOrderCancelReject(Message message, String orderId, AbstractBusinessComponent initialComponent) {

		try {
			Message orderCancelReject = new Message();
			orderCancelReject.getHeader().setString(MsgType.FIELD, "9");
			orderCancelReject.setString(37, orderId);
			orderCancelReject.setString(11, message.getString(11));

			if (message.isSetField(41))
				orderCancelReject.setString(41, message.getString(41));

			if (message.getHeader().getString(MsgType.FIELD).equals("F"))
				orderCancelReject.setChar(434, '1');
			else
				orderCancelReject.setChar(434, '2');

			SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = sellSideNewOrderSingleEntryMap.get(orderId);

			if (sellSideNewOrderSingleEntry != null) {
				switch (sellSideNewOrderSingleEntry.getOrderStatus()) {
					case CANCEL:
						orderCancelReject.setChar(39, '4');
						break;

					case DONE_FOR_DAY_PENDING:
					case DONE_FOR_DAY:
						orderCancelReject.setChar(39, '3');
						break;

					case FILLED_PENDING:
					case FILLED:
						orderCancelReject.setChar(39, '2');
						break;

					case PARTIALLY_FILLED_PENDING:
					case PARTIALLY_FILLED:
						orderCancelReject.setChar(39, '1');
						break;

					case NEW:
						orderCancelReject.setChar(39, '0');
						break;

					case MODIFIED:
						if (sellSideNewOrderSingleEntry.getCumulativeQuantity() == 0)
							orderCancelReject.setChar(39, '0');
						else
							orderCancelReject.setChar(39, '1');
						break;

					case REJECTED_PENDING:
					case REJECTED:
						orderCancelReject.setChar(39, '8');
						break;

				}
			}
			else
				orderCancelReject.setChar(39, '3');

			orderCancelReject.getHeader().setString(56, message.getHeader().getString(49));
			MessageEntry messageEntry = new MessageEntry(sellSideBook, sellSideBook, orderCancelReject);
			businessComponentHandler.getBusinessComponentHandler(initialComponent.getId()).addFIXMessage(messageEntry);
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	private Trader getTrader(Counterparty counterparty, String traderId, String traderIdSource, AbstractBusinessComponent businessComponent) throws Exception {

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
						&& traderPartyID.getPartyRole().intValue() == 11 && traderPartyID.getPartyIDSource() != null
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
			logEntry.setMessageComponent(sellSideBook.getName());
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
			traderPartyID.setPartyRole(11);
			traderPartyID.setTrader(trader);
			traderPartyID.setAbstractBusinessComponent(businessComponent);
			traderPartyIDs.add(traderPartyID);
			TraderPartyID traderPartyID2 = new TraderPartyID();
			traderPartyID2.setPartyID(traderId);
			traderPartyID2.setPartyIDSource(traderIdSource);
			traderPartyID2.setPartyRole(37);
			traderPartyID2.setTrader(trader);
			traderPartyID2.setAbstractBusinessComponent(businessComponent);
			traderPartyIDs.add(traderPartyID2);
			trader.setTraderPartyIDs(traderPartyIDs);

			basisPersistenceHandler.persist(trader, null, businessComponentHandler);
		}

		return trader;

	}

	private synchronized void handleNewOrderSingle(Message message, AbstractBusinessComponent businessComponent) {

		SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = new SellSideNewOrderSingleEntry();
		sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.NEW);

		String partyId = null;
		String partyIdSource = null;

		String traderId = null;
		String traderIdSource = null;

		try {

			for (int i = 1; i <= message.getGroupCount(453); i++) {

				Group parties = message.getGroup(i, 453);
				if (parties.isSetField(452) && parties.getInt(452) == 13) {

					if (parties.isSetField(447))
						partyIdSource = parties.getString(447);

					if (parties.isSetField(448))
						partyId = parties.getString(448);
				}
				if (parties.isSetField(452) && parties.getInt(452) == 11) {

					if (parties.isSetField(447))
						traderIdSource = parties.getString(447);

					if (parties.isSetField(448))
						traderId = parties.getString(448);
				}
			}

			Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 13);
			if (counterparty != null) {

				sellSideNewOrderSingleEntry.setCounterparty(counterparty.getId());

				if (traderId != null) {

					Trader trader = getTrader(counterparty, traderId, traderIdSource, businessComponent);
					sellSideNewOrderSingleEntry.setTrader(trader.getId());

				}

				if (message.isSetField(11) && message.isSetField(38)) {

					sellSideNewOrderSingleEntry.setCounterpartyOrderId(message.getString(11));
					sellSideNewOrderSingleEntry.setOrderQuantity(message.getDouble(38));
					sellSideNewOrderSingleEntry.setLeaveQuantity(message.getDouble(38));
					sellSideNewOrderSingleEntry.setCreated(System.currentTimeMillis());
					sellSideNewOrderSingleEntry.setCumulativeQuantity(0);
					sellSideNewOrderSingleEntry.setLastPrice(null);
					sellSideNewOrderSingleEntry.setLastQuantity(null);
					if (message.isSetField(44))
						sellSideNewOrderSingleEntry.setLimit(message.getDouble(44));
					else
						sellSideNewOrderSingleEntry.setLimit(null);

					sellSideNewOrderSingleEntry.setMarketInterface(businessComponent.getId());
					if (message.isSetField(110))
						sellSideNewOrderSingleEntry.setMinQuantity(110);

					sellSideNewOrderSingleEntry.setOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));

					String securityId = null;
					String securityIdSource = null;
					if (message.isSetField(48))
						securityId = message.getString(48);
					if (message.isSetField(22))
						securityIdSource = message.getString(22);

					FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);
					if (security != null) {

						boolean assigned = false;

						for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBook.getAssignedSellSideBookSecurities())
							if (assignedSellSideBookSecurity.getSecurity().getId() == security.getId())
								assigned = true;

						if (assigned) {

							sellSideNewOrderSingleEntry.setSecurity(security.getId());
							sellSideNewOrderSingleEntry.setSellSideBook(sellSideBook.getId());
							if (message.isSetField(54)) {

								if (message.getString(54).equals("1"))
									sellSideNewOrderSingleEntry.setSide(Side.ASK);
								else if (message.getString(54).equals("2"))
									sellSideNewOrderSingleEntry.setSide(Side.BID);
								else {

								}
								sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);
								if (message.isSetField(64))
									sellSideNewOrderSingleEntry.setSettlementDate(message.getUtcDateOnly(64).getTime());
								java.util.Calendar calendar = java.util.Calendar.getInstance();
								calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
								calendar.set(java.util.Calendar.MINUTE, 0);
								calendar.set(java.util.Calendar.SECOND, 0);
								calendar.set(java.util.Calendar.MILLISECOND, 0);
								sellSideNewOrderSingleEntry.setTifDate(calendar.getTimeInMillis());
								if (message.isSetField(59)) {
									if (message.getString(59).equals("4")) {
										sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);

									}
									if (message.getString(59).equals("1")) {
										sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_CANCEL);
									}
									if (message.getString(59).equals("6")) {
										sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
										if (message.isSetField(432))
											sellSideNewOrderSingleEntry.setTifDate(message.getUtcDateOnly(432).getTime());
									}
									if (message.getString(59).equals("0")) {
										sellSideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DAY);
									}

								}

								sellSideNewOrderSingleEntry.setUpdated(System.currentTimeMillis());
								sellSideNewOrderSingleEntry.setUser(null);

								if (isValidPrice(security, sellSideNewOrderSingleEntry.getLimit())
										&& isValidQuantity(security, sellSideNewOrderSingleEntry.getOrderQuantity())) {

									persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, true);
									sellSideNewOrderSingleEntryMap.put(sellSideNewOrderSingleEntry.getOrderId(), sellSideNewOrderSingleEntry);

									sendExecutionReport(sellSideNewOrderSingleEntry, false);

								}
								else {

									sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
									sendExecutionReport(sellSideNewOrderSingleEntry, false);

									StringBuffer stringBuffer = new StringBuffer("System automatically rejected new order for security ");
									stringBuffer.append(security.getName());
									stringBuffer.append(" from counterparty ");
									stringBuffer.append(counterparty.getName());
									stringBuffer.append(". Reject reason was an invalid ");
									if (!isValidPrice(security, sellSideNewOrderSingleEntry.getLimit()))
										stringBuffer.append("limit.");
									if (!isValidQuantity(security, sellSideNewOrderSingleEntry.getOrderQuantity()))
										stringBuffer.append("quantity.");
									stringBuffer.append(" Please contact your counterparty.");
									LogEntry logEntry = new LogEntry();
									logEntry.setLogLevel(Level.WARNING);
									logEntry.setLogDate(new Date());
									logEntry.setMessageComponent(sellSideBook.getName());
									logEntry.setMessageText(stringBuffer.toString());
									logEntry.setHighlightKey(sellSideNewOrderSingleEntry.getOrderId());
									basisPersistenceHandler.writeLogEntry(logEntry);
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

	/**
	 * On open quote monitor request.
	 *
	 * @param openQuoteMonitorRequest the open quote monitor request
	 * @param sellSideQuotePage the sell side quote page
	 * @param channel the channel
	 */
	public void onOpenQuoteMonitorRequest(OpenQuoteMonitorRequest openQuoteMonitorRequest, SellSideQuotePage sellSideQuotePage, Channel channel) {

		Set<SellSideQuotePage> pages = channels.get(channel);

		if (pages == null) {
			pages = new HashSet<SellSideQuotePage>();
			channels.put(channel, pages);
		}
		pages.add(sellSideQuotePage);

		Set<SellSideMDInputEntry> mdInputEntries = new HashSet<SellSideMDInputEntry>();

		List<SellSideMDInputEntry> historicalInpuEntries = new ArrayList<SellSideMDInputEntry>();

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideQuotePage.getAssignedSellSideBookSecurities()) {
			SellSideMDInputEntry sellSideMDInputEntry = securityEntries.get(assignedSellSideBookSecurity.getSecurity().getId());
			if (sellSideMDInputEntry != null)
				mdInputEntries.add(sellSideMDInputEntry);

			List<SellSideMDInputEntry> historicalEntries = historicalData.get(assignedSellSideBookSecurity.getSecurity().getId());
			if (historicalEntries != null)
				historicalInpuEntries.addAll(historicalEntries);
		}
		basisPersistenceHandler.send(new UpdateSellSideMDInputEntryResponse(new ArrayList<SellSideMDInputEntry>(mdInputEntries)), channel);

		if (historicalInpuEntries.size() > 0) {

			try {

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(historicalInpuEntries);
				objectOutputStream.close();
				byteArrayOutputStream.close();

				ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream2);
				gzip.write(byteArrayOutputStream.toByteArray());
				gzip.finish();
				gzip.close();

				basisPersistenceHandler.send(new HistoricalSellSideDataResponse(sellSideQuotePage.getId(), byteArrayOutputStream2.toByteArray()), channel);
			}
			catch (Exception exception) {
				log.error("Bug", exception);
			}
		}

	}

	/**
	 * On close quote monitor request.
	 *
	 * @param closeQuoteMonitorRequest the close quote monitor request
	 * @param sellSideQuotePage the sell side quote page
	 * @param channel the channel
	 */
	public void onCloseQuoteMonitorRequest(CloseQuoteMonitorRequest closeQuoteMonitorRequest, SellSideQuotePage sellSideQuotePage, Channel channel) {

		Set<SellSideQuotePage> pages = channels.get(channel);

		if (pages != null) {
			pages.remove(sellSideQuotePage);
		}
	}

	private Double getYield(FSecurity fSecurity, Double price) {

		if (price == null)
			return null;

		AssignedSellSideBookSecurity assignedSellSideBookSecurity = assignedSellSideBookSecurities.get(fSecurity.getId());
		if (assignedSellSideBookSecurity == null)
			return null;

		if (fSecurity.getMaturity() == null || fSecurity.getSecurityDetails().getCouponRate() == 0)
			return null;

		BankCalendar bankCalendar = assignedSellSideBookSecurity.getBankCalendar();
		Integer settlementDays = assignedSellSideBookSecurity.getValuta();
		if (bankCalendar == null)
			bankCalendar = sellSideBook.getBankCalendar();
		if (settlementDays == null)
			settlementDays = sellSideBook.getValuta();

		Calendar calendar = bankCalendar.getCalendar();
		org.jquantlib.time.Date settlementDate = calendar.advance(new org.jquantlib.time.Date(new Date()), settlementDays, TimeUnit.Days);

		Integer basis = null;

		basis = assignedSellSideBookSecurity.getDaycountConvention();

		if (basis == null)
			basis = sellSideBook.getDaycountConvention();

		net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod calcMethod = sellSideBook.getCalcMethod();

		if (assignedSellSideBookSecurity.getCalcMethod() != null)
			calcMethod = assignedSellSideBookSecurity.getCalcMethod();

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

		AssignedSellSideBookSecurity assignedSellSideBookSecurity = assignedSellSideBookSecurities.get(fSecurity.getId());
		if (assignedSellSideBookSecurity == null)
			return null;

		if (fSecurity.getMaturity() == null || fSecurity.getSecurityDetails().getCouponRate() == 0)
			return null;

		BankCalendar bankCalendar = assignedSellSideBookSecurity.getBankCalendar();
		Integer settlementDays = assignedSellSideBookSecurity.getValuta();
		if (bankCalendar == null)
			bankCalendar = sellSideBook.getBankCalendar();
		if (settlementDays == null)
			settlementDays = sellSideBook.getValuta();

		Calendar calendar = bankCalendar.getCalendar();
		org.jquantlib.time.Date settlementDate = calendar.advance(new org.jquantlib.time.Date(new Date()), settlementDays, TimeUnit.Days);

		Integer basis = null;

		basis = assignedSellSideBookSecurity.getDaycountConvention();

		if (basis == null)
			basis = sellSideBook.getDaycountConvention();

		net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod calcMethod = sellSideBook.getCalcMethod();

		if (assignedSellSideBookSecurity.getCalcMethod() != null)
			calcMethod = assignedSellSideBookSecurity.getCalcMethod();

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

	private void persistSellSideQuoteRequestEntry(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, FUser fUser, boolean logNewOrderSingle) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		if (fUser != null)
			stringBuffer.append(fUser.getName());
		else
			stringBuffer.append("System");

		SellSideQuoteRequest sellSideQuoteRequest = new SellSideQuoteRequest();

		List<SellSideQuoteRequest> sellSideQuoteRequests = null;

		sellSideQuoteRequests = basisPersistenceHandler.executeQuery(SellSideQuoteRequest.class, "select a from SellSideQuoteRequest a where a.quoteReqId=?1",
				sellSideQuoteRequestEntry.getQuoteReqId(), businessComponentHandler, true);

		if (sellSideQuoteRequests.size() > 0) {
			newEntry = false;
			sellSideQuoteRequest = sellSideQuoteRequests.get(0);
		}

		SellSideBook sellSideBook = basisPersistenceHandler.findById(SellSideBook.class, sellSideQuoteRequestEntry.getSellSideBook(), businessComponentHandler);
		sellSideQuoteRequest.setSellSideBook(sellSideBook);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideQuoteRequestEntry.getCounterparty(), businessComponentHandler);
		sellSideQuoteRequest.setCounterparty(counterparty);

		Trader trader = null;

		if (sellSideQuoteRequestEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, sellSideQuoteRequestEntry.getTrader(), businessComponentHandler);
			sellSideQuoteRequest.setTrader(trader);
		}

		sellSideQuoteRequest.setCounterpartyOrderId(sellSideQuoteRequestEntry.getCounterpartyOrderId());
		sellSideQuoteRequest.setCreatedTimestamp(sellSideQuoteRequestEntry.getCreated());
		sellSideQuoteRequest.setCumulativeQuantity(sellSideQuoteRequestEntry.getCumulativeQuantity());
		sellSideQuoteRequest.setLastPrice(sellSideQuoteRequestEntry.getLastPrice());
		sellSideQuoteRequest.setLastYield(sellSideQuoteRequestEntry.getLastYield());
		sellSideQuoteRequest.setLastQuantity(sellSideQuoteRequestEntry.getLastQuantity());
		sellSideQuoteRequest.setLimit(sellSideQuoteRequestEntry.getLimit());
		sellSideQuoteRequest.setMinQuantity(sellSideQuoteRequestEntry.getMinQuantity());
		sellSideQuoteRequest.setLeaveQuantity(sellSideQuoteRequestEntry.getLeaveQuantity());
		sellSideQuoteRequest.setQuoteId(sellSideQuoteRequestEntry.getQuoteId());
		sellSideQuoteRequest.setQuoteReqId(sellSideQuoteRequestEntry.getQuoteReqId());
		sellSideQuoteRequest.setQuoteRespId(sellSideQuoteRequestEntry.getQuoteRespId());
		sellSideQuoteRequest.setSettlementTimestamp(sellSideQuoteRequestEntry.getSettlementDate());

		AbstractAcceptor fixAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, sellSideQuoteRequestEntry.getMarketInterface(),
				businessComponentHandler);
		sellSideQuoteRequest.setMarketInterface(fixAcceptor);

		sellSideQuoteRequest.setOrderId(sellSideQuoteRequestEntry.getOrderId());
		sellSideQuoteRequest.setOrderQuantity(sellSideQuoteRequestEntry.getOrderQuantity());

		switch (sellSideQuoteRequestEntry.getOrderStatus()) {
			case REJECTED_PENDING:
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.REJECTED_PENDING);
				stringBuffer.append(" rejected inquiry ");
				break;
			case PASS:
				stringBuffer.append(" received pass for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.PASS);
				break;
			case FILLED_PENDING:
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.FILLED_PENDING);
				stringBuffer.append(" completely filled inquiry ");
				break;
			case FILLED:
				stringBuffer.append(" sent fill for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.FILLED);
				break;
			case NEW:
				stringBuffer.append(" received new inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.NEW);
				break;
			case REJECTED:
				stringBuffer.append(" sent reject for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.REJECTED);
				break;
			case COUNTER:
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.COUNTER);
				stringBuffer.append(" received counter for inquiry ");
				break;
			case COVER:
				stringBuffer.append(" received cover for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.COVER);
				break;
			case DONE_AWAY:
				stringBuffer.append(" received done away for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.DONE_AWAY);
				break;
			case EXPIRED:
				stringBuffer.append(" received expiration for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.EXPIRED);
				break;
			case HIT_LIFT:
				if (sellSideQuoteRequestEntry.getSide() == Side.BID)
					stringBuffer.append(" received hit for inquiry ");
				else
					stringBuffer.append(" received lift for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.PASS);
				break;
			case QUOTED:
				stringBuffer.append(" sent quote for inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.QUOTED);
				break;
			case QUOTED_PENDING:
				stringBuffer.append(" quoted inquiry ");
				sellSideQuoteRequest.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.OrderStatus.QUOTED_PENDING);
				break;
			default:
				break;

		}

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sellSideQuoteRequestEntry.getSecurity(), businessComponentHandler);
		sellSideQuoteRequest.setSecurity(security);

		sellSideQuoteRequest.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.Side.BID);
		String side = " from ";
		if (sellSideQuoteRequestEntry.getSide() == Side.ASK) {
			stringBuffer.append("sell ");
			side = " to ";
			sellSideQuoteRequest.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuoteRequest.Side.ASK);
		}
		else {
			stringBuffer.append("buy ");
		}

		stringBuffer.append(decimalFormat2.format(sellSideQuoteRequestEntry.getOrderQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		if (sellSideQuoteRequestEntry.getLimit() != null) {
			stringBuffer.append(" limit ");
			stringBuffer.append(decimalFormat.format(sellSideQuoteRequestEntry.getLimit()));
		}

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		sellSideQuoteRequest.setUpdatedTimestamp(sellSideQuoteRequestEntry.getUpdated());

		if (fUser != null)
			sellSideQuoteRequest.setfUser(fUser);

		try {
			if (newEntry)
				basisPersistenceHandler.persist(sellSideQuoteRequest);
			else
				basisPersistenceHandler.update(sellSideQuoteRequest);

			for (Channel channel : bookSessions)
				basisPersistenceHandler.send(new SellSideQuoteRequestResponse(sellSideQuoteRequestEntry), channel);

			if (logNewOrderSingle) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(sellSideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(sellSideQuoteRequestEntry.getQuoteReqId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sellSideBook.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(sellSideQuoteRequestEntry.getQuoteReqId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

	}

	private void persistNewOrderSingleEntry(SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry, FUser fUser, boolean logNewOrderSingle) {

		boolean newEntry = true;

		StringBuffer stringBuffer = new StringBuffer();

		if (fUser != null)
			stringBuffer.append(fUser.getName());
		else
			stringBuffer.append("System");

		SellSideNewOrderSingle sellSideNewOrderSingle = new SellSideNewOrderSingle();

		List<SellSideNewOrderSingle> newOrderSingleEntries = basisPersistenceHandler.executeQuery(SellSideNewOrderSingle.class,
				"select a from SellSideNewOrderSingle a where a.orderId=?1", sellSideNewOrderSingleEntry.getOrderId(), businessComponentHandler, true);

		if (newOrderSingleEntries.size() > 0) {
			newEntry = false;
			sellSideNewOrderSingle = newOrderSingleEntries.get(0);
		}

		SellSideBook sellSideBook = basisPersistenceHandler.findById(SellSideBook.class, sellSideNewOrderSingleEntry.getSellSideBook(),
				businessComponentHandler);
		sellSideNewOrderSingle.setSellSideBook(sellSideBook);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideNewOrderSingleEntry.getCounterparty(),
				businessComponentHandler);
		sellSideNewOrderSingle.setCounterparty(counterparty);

		Trader trader = null;

		if (sellSideNewOrderSingleEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, sellSideNewOrderSingleEntry.getTrader(), businessComponentHandler);
			sellSideNewOrderSingle.setTrader(trader);
		}

		sellSideNewOrderSingle.setCounterpartyOrderId(sellSideNewOrderSingleEntry.getCounterpartyOrderId());
		sellSideNewOrderSingle.setCreatedTimestamp(sellSideNewOrderSingleEntry.getCreated());
		sellSideNewOrderSingle.setCumulativeQuantity(sellSideNewOrderSingleEntry.getCumulativeQuantity());
		sellSideNewOrderSingle.setLastPrice(sellSideNewOrderSingleEntry.getLastPrice());
		sellSideNewOrderSingle.setLastYield(sellSideNewOrderSingleEntry.getLastYield());
		sellSideNewOrderSingle.setLastQuantity(sellSideNewOrderSingleEntry.getLastQuantity());
		sellSideNewOrderSingle.setLimit(sellSideNewOrderSingleEntry.getLimit());
		sellSideNewOrderSingle.setMinQuantity(sellSideNewOrderSingleEntry.getMinQuantity());
		sellSideNewOrderSingle.setLeaveQuantity(sellSideNewOrderSingleEntry.getLeaveQuantity());
		sellSideNewOrderSingle.setNewOrderId(sellSideNewOrderSingleEntry.getNewOrderId());
		sellSideNewOrderSingle.setNewLimit(sellSideNewOrderSingleEntry.getNewLimit());
		sellSideNewOrderSingle.setNewQuantity(sellSideNewOrderSingleEntry.getNewOrderQuantity());
		sellSideNewOrderSingle.setTifTimestamp(sellSideNewOrderSingleEntry.getTifDate());
		sellSideNewOrderSingle.setSettlementDate(sellSideNewOrderSingleEntry.getSettlementDate());

		AbstractAcceptor fixAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, sellSideNewOrderSingleEntry.getMarketInterface(),
				businessComponentHandler);
		sellSideNewOrderSingle.setMarketInterface(fixAcceptor);

		sellSideNewOrderSingle.setOrderId(sellSideNewOrderSingleEntry.getOrderId());
		sellSideNewOrderSingle.setOrderQuantity(sellSideNewOrderSingleEntry.getOrderQuantity());

		switch (sellSideNewOrderSingleEntry.getOrderStatus()) {
			case REJECTED_PENDING:
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.REJECTED_PENDING);
				stringBuffer.append(" rejected order ");
				break;
			case CANCEL:
				stringBuffer.append(" received cancel request for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.CANCEL);
				break;
			case DONE_FOR_DAY_PENDING:
				sellSideNewOrderSingle
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.DONE_FOR_DAY_PENDING);
				stringBuffer.append(" killed order ");
				break;
			case FILLED_PENDING:
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.FILLED_PENDING);
				stringBuffer.append(" completely filled order ");
				break;
			case DONE_FOR_DAY:
				stringBuffer.append(" received done for day for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.DONE_FOR_DAY);
				break;
			case FILLED:
				stringBuffer.append(" sent fill for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.FILLED);
				break;
			case NEW:
				stringBuffer.append(" received new order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.NEW);
				break;
			case PARTIALLY_FILLED:
				stringBuffer.append(" received partial fill for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.PARTIALLY_FILLED);
				break;
			case REJECTED:
				stringBuffer.append(" sent reject for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.REJECTED);
				break;
			case PARTIALLY_FILLED_PENDING:
				stringBuffer.append(" partially filled order ");
				sellSideNewOrderSingle
						.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.PARTIALLY_FILLED_PENDING);
				break;
			case MODIFIED:
				stringBuffer.append(" received modification for order ");
				sellSideNewOrderSingle.setOrderStatus(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.OrderStatus.MODIFIED);
				break;

		}

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sellSideNewOrderSingleEntry.getSecurity(), businessComponentHandler);
		sellSideNewOrderSingle.setSecurity(security);

		sellSideNewOrderSingle.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.Side.BID);
		String side = " from ";
		if (sellSideNewOrderSingleEntry.getSide() == Side.ASK) {
			stringBuffer.append("sell ");
			side = " to ";
			sellSideNewOrderSingle.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.Side.ASK);
		}
		else {
			stringBuffer.append("buy ");
		}

		stringBuffer.append(decimalFormat2.format(sellSideNewOrderSingleEntry.getOrderQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		if (sellSideNewOrderSingleEntry.getLimit() != null) {
			stringBuffer.append(" limit ");
			stringBuffer.append(decimalFormat.format(sellSideNewOrderSingleEntry.getLimit()));
		}

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		switch (sellSideNewOrderSingleEntry.getTimeInForce()) {
			case FILL_OR_KILL:
				stringBuffer.append(" fill or kill (settlement ");
				stringBuffer.append(simpleDateFormat.format(new Date(sellSideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(").");
				sellSideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.TimeInForce.FILL_OR_KILL);
				break;
			case GOOD_TILL_CANCEL:
				stringBuffer.append(" good till cancel.");
				sellSideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.TimeInForce.GOOD_TILL_CANCEL);
				break;
			case GOOD_TILL_DATE:
				stringBuffer.append(" good till ");
				stringBuffer.append(simpleDateFormat.format(new Date(sellSideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(".");
				sellSideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.TimeInForce.GOOD_TILL_DATE);
				break;
			case GOOD_TILL_DAY:
				stringBuffer.append(" good till day (settlement ");
				stringBuffer.append(simpleDateFormat.format(new Date(sellSideNewOrderSingleEntry.getTifDate())));
				stringBuffer.append(").");
				sellSideNewOrderSingle.setTimeInForce(net.sourceforge.fixagora.sellside.shared.persistence.SellSideNewOrderSingle.TimeInForce.GOOD_TILL_DAY);
				break;
		}

		sellSideNewOrderSingle.setUpdatedTimestamp(sellSideNewOrderSingleEntry.getUpdated());

		if (fUser != null)
			sellSideNewOrderSingle.setfUser(fUser);

		try {
			if (newEntry)
				basisPersistenceHandler.persist(sellSideNewOrderSingle);
			else
				basisPersistenceHandler.update(sellSideNewOrderSingle);

			for (Channel channel : bookSessions)
				basisPersistenceHandler.send(new NewOrderSingleResponse(sellSideNewOrderSingleEntry), channel);

			if (logNewOrderSingle) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.INFO);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(sellSideBook.getName());
				logEntry.setMessageText(stringBuffer.toString());
				logEntry.setHighlightKey(sellSideNewOrderSingleEntry.getOrderId());
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
			LogEntry logEntry = new LogEntry();
			logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sellSideBook.getName());
			logEntry.setMessageText(e.getMessage());
			logEntry.setHighlightKey(sellSideNewOrderSingleEntry.getOrderId());
			basisPersistenceHandler.writeLogEntry(logEntry);
		}

	}

	/**
	 * On new order single request.
	 *
	 * @param newOrderSingleRequest the new order single request
	 * @param channel the channel
	 * @return the sell side new order single entry
	 */
	public synchronized SellSideNewOrderSingleEntry onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel) {

		SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = newOrderSingleRequest.getSellSideNewOrderSingleEntry();
		sellSideNewOrderSingleEntry.setOriginalOrderId(null);
		long timestamp = System.currentTimeMillis();
		sellSideNewOrderSingleEntry.setUpdated(timestamp);

		FUser user = null;

		if (channel != null) {
			user = basisPersistenceHandler.getUser(channel);
			sellSideNewOrderSingleEntry.setUser(user.getName());
		}

		SellSideNewOrderSingleEntry lastEntry = sellSideNewOrderSingleEntryMap.get(sellSideNewOrderSingleEntry.getOrderId());
		if (lastEntry != null) {

			BigDecimal lastCumulativeQuantity = new BigDecimal(decimalFormat.format(lastEntry.getCumulativeQuantity()));
			BigDecimal newCumulativeQuantity = new BigDecimal(decimalFormat.format(sellSideNewOrderSingleEntry.getCumulativeQuantity()));
			BigDecimal newLastQuantity = new BigDecimal(0);
			if (sellSideNewOrderSingleEntry.getLastQuantity() != null && sellSideNewOrderSingleEntry.getOrderStatus() != OrderStatus.DONE_FOR_DAY_PENDING
					&& sellSideNewOrderSingleEntry.getOrderStatus() != OrderStatus.REJECTED_PENDING)
				newLastQuantity = new BigDecimal(decimalFormat.format(sellSideNewOrderSingleEntry.getLastQuantity()));

			if (newCumulativeQuantity.subtract(newLastQuantity).compareTo(lastCumulativeQuantity) != 0) {
				LogEntry logEntry = new LogEntry();
				logEntry.setLogLevel(Level.FATAL);
				logEntry.setLogDate(new Date());
				logEntry.setMessageComponent(sellSideBook.getName());
				logEntry.setMessageText("Execution canceled because of concurrent modification.");
				logEntry.setHighlightKey(sellSideNewOrderSingleEntry.getOrderId());
				basisPersistenceHandler.writeLogEntry(logEntry);
				return null;
			}

		}

		sellSideNewOrderSingleEntryMap.put(sellSideNewOrderSingleEntry.getOrderId(), sellSideNewOrderSingleEntry);
		persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, user, true);

		if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.DONE_FOR_DAY_PENDING) {
			sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
			if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
				persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
			else
				sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY_PENDING);
		}
		else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.FILLED_PENDING) {
			sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED);
			if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
				persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
			else
				sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED_PENDING);
		}
		else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED_PENDING) {
			sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
			if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
				persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
			else
				sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED_PENDING);
		}
		else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED_PENDING) {
			sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
			if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
				persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
			else
				sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED_PENDING);
		}

		return sellSideNewOrderSingleEntry;
	}

	/**
	 * On open sell side book request.
	 *
	 * @param openSellSideBookRequest the open sell side book request
	 * @param channel the channel
	 * @return the collection
	 */
	public synchronized Collection<AbstractSellSideEntry> onOpenSellSideBookRequest(OpenSellSideBookRequest openSellSideBookRequest, Channel channel) {

		bookSessions.add(channel);
		Collection<AbstractSellSideEntry> abstractSellSideEntries = new HashSet<AbstractSellSideEntry>(sellSideNewOrderSingleEntryMap.values());
		abstractSellSideEntries.addAll(sellSideQuoteRequestEntryMap.values());
		return abstractSellSideEntries;

	}

	/**
	 * On close sell side book request.
	 *
	 * @param closeSellSideBookRequest the close sell side book request
	 * @param channel the channel
	 */
	public void onCloseSellSideBookRequest(CloseSellSideBookRequest closeSellSideBookRequest, Channel channel) {

		bookSessions.remove(channel);

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

	private void addUserGroup(AbstractBusinessComponent abstractBusinessComponent, FUser user, FieldMap fieldMap) {

		Group group = null;

		for (FUserPartyID fUserPartyID : user.getfUserPartyIDs()) {
			if (fUserPartyID.getAbstractBusinessComponent() == null) {
				if (group == null) {
					group = new Group(453, 448, new int[] { 448, 447, 452 });
					group.setString(448, fUserPartyID.getPartyID());
					if (fUserPartyID.getPartyIDSource() != null)
						group.setChar(447, fUserPartyID.getPartyIDSource().charAt(0));
					group.setInt(452, 12);
				}
			}
			else if (fUserPartyID.getAbstractBusinessComponent().getId() == abstractBusinessComponent.getId() && fUserPartyID.getPartyRole() != null
					&& fUserPartyID.getPartyRole().intValue() == 12) {
				group = new Group(453, 448, new int[] { 448, 447, 452 });
				group.setString(448, fUserPartyID.getPartyID());
				if (fUserPartyID.getPartyIDSource() != null)
					group.setChar(447, fUserPartyID.getPartyIDSource().charAt(0));
				group.setInt(452, 12);

			}
		}

		if (group != null)
			fieldMap.addGroup(group);
	}

	private Trader addTraderGroup(AbstractBusinessComponent abstractAcceptor, Counterparty counterparty, long traderId, FieldMap fieldMap) {

		Group group = null;

		List<Object> key = new ArrayList<Object>();
		key.add(counterparty);
		key.add(traderId);

		Trader orderOriginatingTrader = null;

		List<Trader> traders = basisPersistenceHandler.executeQuery(Trader.class, "select t from Trader t where t.parent=?1 and t.id=?2", key,
				businessComponentHandler, true);
		for (Trader trader : traders) {

			for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
				if (traderPartyID.getAbstractBusinessComponent() == null) {
					if (group == null) {
						group = new Group(453, 448, new int[] { 448, 447, 452 });
						group.setString(448, traderPartyID.getPartyID());
						if (traderPartyID.getPartyIDSource() != null)
							group.setChar(447, traderPartyID.getPartyIDSource().charAt(0));
						group.setInt(452, 11);
					}
				}
				else if (traderPartyID.getAbstractBusinessComponent().getId() == abstractAcceptor.getId() && traderPartyID.getPartyRole() != null
						&& traderPartyID.getPartyRole().intValue() == 11) {
					group = new Group(453, 448, new int[] { 448, 447, 452 });
					group.setString(448, traderPartyID.getPartyID());
					if (traderPartyID.getPartyIDSource() != null)
						group.setChar(447, traderPartyID.getPartyIDSource().charAt(0));
					group.setInt(452, 11);
				}
			}

			if (group != null) {
				fieldMap.addGroup(group);
				orderOriginatingTrader = trader;
			}

		}

		return orderOriginatingTrader;

	}

	private boolean sendQuoteRequestReject(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, FUser fUser) {

		if (businessComponentHandler.getStartLevel(sellSideQuoteRequestEntry.getMarketInterface()) != 2)
			return false;

		Message quoteRequestReject = new Message();
		quoteRequestReject.getHeader().setString(MsgType.FIELD, "AG");
		quoteRequestReject.setString(131, sellSideQuoteRequestEntry.getQuoteReqId());
		quoteRequestReject.setInt(658, 10);

		final Group noRelatedSym = new Group(146, 55, new int[] { 55, 48, 22, 54, 38, 64, 126, 60, 423, 453 });

		if (sellSideQuoteRequestEntry.getSide() == Side.BID)
			noRelatedSym.setChar(54, '2');
		else
			noRelatedSym.setChar(54, '1');

		noRelatedSym.setDouble(38, sellSideQuoteRequestEntry.getOrderQuantity());

		noRelatedSym.setString(64, localMarketDateFormat.format(new Date(sellSideQuoteRequestEntry.getSettlementDate())));

		noRelatedSym.setUtcTimeStamp(126, new Date(sellSideQuoteRequestEntry.getExpireDate()));
		noRelatedSym.setUtcTimeStamp(60, new Date(sellSideQuoteRequestEntry.getCreated()));

		FSecurity security = securityDictionary.getSecurityForBusinessObjectID(sellSideQuoteRequestEntry.getSecurity());

		if (security != null) {

			noRelatedSym.setString(48, security.getSecurityID());
			noRelatedSym.setString(22, security.getSecurityDetails().getSecurityIDSource());
			noRelatedSym.setString(55, "N/A");

			AbstractAcceptor abstractAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, sellSideQuoteRequestEntry.getMarketInterface(),
					businessComponentHandler);

			Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideQuoteRequestEntry.getCounterparty(),
					businessComponentHandler);

			if (abstractAcceptor != null) {

				if (abstractAcceptor instanceof FIXAcceptor) {
					FIXAcceptor fixAcceptor = (FIXAcceptor) abstractAcceptor;
					if (fixAcceptor.getPartyID() != null) {
						final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
						group.setString(448, fixAcceptor.getPartyID());
						if (fixAcceptor.getPartyIDSource() != null)
							group.setChar(447, fixAcceptor.getPartyIDSource().charAt(0));
						group.setInt(452, 1);
						noRelatedSym.addGroup(group);
					}
				}

				if (fUser != null) {

					sellSideQuoteRequestEntry.setUser(fUser.getName());
					addUserGroup(abstractAcceptor, fUser, noRelatedSym);

				}

				if (counterparty != null) {

					addCounterpartyGroup(abstractAcceptor, counterparty, 13, noRelatedSym);

					if (sellSideQuoteRequestEntry.getTrader() != null) {

						addTraderGroup(abstractAcceptor, counterparty, sellSideQuoteRequestEntry.getTrader(), noRelatedSym);

					}

				}

				if (abstractAcceptor.getSecurityIDSource() != null) {
					String securityId = securityDictionary.getAlternativeSecurityID(security.getSecurityID(), abstractAcceptor.getSecurityIDSource());
					if (securityId != null) {
						noRelatedSym.setString(48, securityId);
						noRelatedSym.setString(22, abstractAcceptor.getSecurityIDSource());
					}
				}

			}

			noRelatedSym.setString(55, "N/A");

			quoteRequestReject.addGroup(noRelatedSym);

			if (counterparty != null && abstractAcceptor != null) {

				AbstractComponentHandler targetComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractAcceptor.getId());

				String targetCompID = targetComponentHandler.getTargetCompIDIfOnline(counterparty);

				if (targetCompID != null) {
					quoteRequestReject.getHeader().setString(56, targetCompID);
					targetComponentHandler.addFIXMessage(new MessageEntry(sellSideBook, sellSideBook, quoteRequestReject));
					sellSideQuoteRequestEntry
							.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED);
					sellSideQuoteRequestEntry.setLeaveQuantity(0);
					persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, fUser, true);
					return true;
				}
			}

		}

		return false;

	}

	private boolean sendExecutionReport(AbstractSellSideEntry abstractSellSideEntry, boolean orderStatus) {

		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, abstractSellSideEntry.getSecurity(), businessComponentHandler);
		if (security == null)
			return false;

		Message executionReport = new Message();
		executionReport.getHeader().setString(MsgType.FIELD, "8");

		if (abstractSellSideEntry.getOrderId() != null)
			executionReport.setString(37, abstractSellSideEntry.getOrderId());
		if (abstractSellSideEntry.getCounterpartyOrderId() != null)
			executionReport.setString(11, abstractSellSideEntry.getCounterpartyOrderId());

		if (security.getSecurityDetails().getPriceQuoteMethod() != null) {
			if (security.getSecurityDetails().getPriceQuoteMethod().equals("PCTPAR"))
				executionReport.setInt(423, 1);
			else if (security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
				executionReport.setInt(423, 9);
		}

		executionReport.setDouble(6, 0);

		if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {
			switch (((SellSideNewOrderSingleEntry) abstractSellSideEntry).getOrderStatus()) {
				case CANCEL:
					executionReport.setChar(150, '4');
					executionReport.setChar(39, '4');
					break;

				case DONE_FOR_DAY:
					executionReport.setChar(150, '3');
					executionReport.setChar(39, '3');
					break;
				case DONE_FOR_DAY_PENDING:
					return false;

				case FILLED:
					executionReport.setChar(150, 'F');
					executionReport.setChar(39, '2');

					if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
						executionReport.setDouble(669, abstractSellSideEntry.getLastPrice());
						executionReport.setDouble(31, abstractSellSideEntry.getLastYield());
					}
					else
						executionReport.setDouble(31, abstractSellSideEntry.getLastPrice());
					executionReport.setDouble(32, abstractSellSideEntry.getLastQuantity());
					if (abstractSellSideEntry.getSettlementDate() != null)
						executionReport.setString(64, localMarketDateFormat.format(new Date(abstractSellSideEntry.getSettlementDate())));
					break;
				case FILLED_PENDING:
					return false;

				case PARTIALLY_FILLED:
					executionReport.setChar(150, 'F');
					executionReport.setChar(39, '1');
					if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
						executionReport.setDouble(669, abstractSellSideEntry.getLastPrice());
						executionReport.setDouble(31, abstractSellSideEntry.getLastYield());
					}
					else
						executionReport.setDouble(31, abstractSellSideEntry.getLastPrice());

					executionReport.setDouble(32, abstractSellSideEntry.getLastQuantity());
					if (abstractSellSideEntry.getSettlementDate() != null)
						executionReport.setString(64, localMarketDateFormat.format(new Date(abstractSellSideEntry.getSettlementDate())));
					break;
				case PARTIALLY_FILLED_PENDING:
					return false;

				case NEW:
					executionReport.setChar(150, '0');
					executionReport.setChar(39, '0');
					break;

				case MODIFIED:
					executionReport.setChar(150, '5');
					if (abstractSellSideEntry.getCumulativeQuantity() == 0)
						executionReport.setChar(39, '0');
					else
						executionReport.setChar(39, '1');

					if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry
							&& ((SellSideNewOrderSingleEntry) abstractSellSideEntry).getOriginalOrderId() != null)
						executionReport.setString(41, ((SellSideNewOrderSingleEntry) abstractSellSideEntry).getOriginalOrderId());
					executionReport.setDouble(38, abstractSellSideEntry.getOrderQuantity());
					executionReport.setDouble(151, abstractSellSideEntry.getLeaveQuantity());
					executionReport.setDouble(44, abstractSellSideEntry.getLimit());

					break;

				case REJECTED:
					executionReport.setChar(150, '8');
					executionReport.setChar(39, '8');
					break;
				case REJECTED_PENDING:
					return false;

				default:
					return false;
			}
		}
		else {
			executionReport.setChar(150, 'F');
			executionReport.setChar(39, '2');

			if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
				executionReport.setDouble(669, abstractSellSideEntry.getLastPrice());
				executionReport.setDouble(31, abstractSellSideEntry.getLastYield());
			}
			else
				executionReport.setDouble(31, abstractSellSideEntry.getLastPrice());
			executionReport.setDouble(32, abstractSellSideEntry.getLastQuantity());
			if (abstractSellSideEntry.getSettlementDate() != null)
				executionReport.setString(64, localMarketDateFormat.format(new Date(abstractSellSideEntry.getSettlementDate())));
		}

		if (abstractSellSideEntry.getSide() == Side.ASK)
			executionReport.setChar(54, '1');
		else
			executionReport.setChar(54, '2');
		executionReport.setDouble(14, abstractSellSideEntry.getCumulativeQuantity());
		executionReport.setDouble(151, abstractSellSideEntry.getLeaveQuantity());
		executionReport.setDouble(38, abstractSellSideEntry.getOrderQuantity());
		executionReport.setString(37, abstractSellSideEntry.getOrderId());
		executionReport.setString(17, decimalFormat3.format(basisPersistenceHandler.getId("EXECID")));
		if (abstractSellSideEntry.getLimit() != null)
			executionReport.setDouble(44, abstractSellSideEntry.getLimit());
		if (abstractSellSideEntry.getMinQuantity() > 0)
			executionReport.setDouble(110, abstractSellSideEntry.getMinQuantity());

		executionReport.setString(48, security.getSecurityID());
		executionReport.setString(22, security.getSecurityDetails().getSecurityIDSource());

		AbstractAcceptor abstractAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, abstractSellSideEntry.getMarketInterface(),
				businessComponentHandler);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, abstractSellSideEntry.getCounterparty(), businessComponentHandler);

		FUser user = null;
		Trader orderInitiatingTrader = null;

		if (abstractAcceptor != null) {

			if (abstractAcceptor instanceof FIXAcceptor) {
				FIXAcceptor fixAcceptor = (FIXAcceptor) abstractAcceptor;
				if (fixAcceptor.getPartyID() != null) {
					final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
					group.setString(448, fixAcceptor.getPartyID());
					if (fixAcceptor.getPartyIDSource() != null)
						group.setChar(447, fixAcceptor.getPartyIDSource().charAt(0));
					group.setInt(452, 1);
					executionReport.addGroup(group);
				}
			}

			if (abstractSellSideEntry.getUser() != null) {
				List<FUser> users = basisPersistenceHandler.executeQuery(FUser.class, "select f from FUser f where f.name=?1", abstractSellSideEntry.getUser(),
						businessComponentHandler, true);
				if (users.size() > 0) {

					user = users.get(0);
					addUserGroup(abstractAcceptor, user, executionReport);
				}
			}

			if (counterparty != null) {

				addCounterpartyGroup(abstractAcceptor, counterparty, 13, executionReport);

				if (abstractSellSideEntry.getTrader() != null) {

					orderInitiatingTrader = addTraderGroup(abstractAcceptor, counterparty, abstractSellSideEntry.getTrader(), executionReport);

				}

			}

			if (abstractAcceptor.getSecurityIDSource() != null) {
				String securityId = securityDictionary.getAlternativeSecurityID(security.getSecurityID(), abstractAcceptor.getSecurityIDSource());
				if (securityId != null) {
					executionReport.setString(48, securityId);
					executionReport.setString(22, abstractAcceptor.getSecurityIDSource());
				}
			}
		}

		executionReport.setString(55, "N/A");

		if (counterparty != null && abstractAcceptor != null) {

			AbstractComponentHandler targetComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractAcceptor.getId());

			String targetCompID = targetComponentHandler.getTargetCompIDIfOnline(counterparty);

			if (targetCompID != null) {
				executionReport.getHeader().setString(56, targetCompID);

				if (orderStatus)
					executionReport.setChar(150, 'I');

				targetComponentHandler.addFIXMessage(new MessageEntry(sellSideBook, sellSideBook, executionReport));

				try {
					if (executionReport.getString(150).equals("F") && !orderStatus) {
						SellSideBookTradeCaptureReport sellSideBookTradeCaptureReport = new SellSideBookTradeCaptureReport();
						sellSideBookTradeCaptureReport.setCounterparty(counterparty);
						sellSideBookTradeCaptureReport.setCounterpartyOrderId(abstractSellSideEntry.getCounterpartyOrderId());
						sellSideBookTradeCaptureReport.setCreatedTimestamp(System.currentTimeMillis());
						sellSideBookTradeCaptureReport.setCumulativeQuantity(abstractSellSideEntry.getCumulativeQuantity());
						sellSideBookTradeCaptureReport.setfUser(user);
						sellSideBookTradeCaptureReport.setExecId(executionReport.getString(17));
						sellSideBookTradeCaptureReport.setLastPrice(abstractSellSideEntry.getLastPrice());
						sellSideBookTradeCaptureReport.setLastYield(abstractSellSideEntry.getLastYield());
						sellSideBookTradeCaptureReport.setLastQuantity(abstractSellSideEntry.getLastQuantity());
						sellSideBookTradeCaptureReport.setLeaveQuantity(abstractSellSideEntry.getLeaveQuantity());
						sellSideBookTradeCaptureReport.setOrderId(abstractSellSideEntry.getOrderId());
						sellSideBookTradeCaptureReport.setOrderQuantity(abstractSellSideEntry.getOrderQuantity());
						sellSideBookTradeCaptureReport.setSecurity(security);
						sellSideBookTradeCaptureReport.setSellSideBook(sellSideBook);
						sellSideBookTradeCaptureReport.setSettlementDate(abstractSellSideEntry.getSettlementDate());
						if (abstractSellSideEntry.getSide() == Side.ASK)
							sellSideBookTradeCaptureReport
									.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideBookTradeCaptureReport.Side.SELL);
						else
							sellSideBookTradeCaptureReport
									.setSide(net.sourceforge.fixagora.sellside.shared.persistence.SellSideBookTradeCaptureReport.Side.BUY);
						sellSideBookTradeCaptureReport.setSourceComponent(abstractAcceptor);
						sellSideBookTradeCaptureReport.setTradeId(decimalFormat3.format(basisPersistenceHandler.getId("TRADEID")));
						sellSideBookTradeCaptureReport.setTrader(orderInitiatingTrader);
						sellSideBookTradeCaptureReport.setUpdatedTimestamp(System.currentTimeMillis());
						handleSellSideBookTradeCaptureReport(sellSideBookTradeCaptureReport);
					}
				}
				catch (FieldNotFound e) {
					log.error("Bug", e);
				}
				return true;
			}
			else
				return false;

		}

		return false;
	}

	private void handleSellSideBookTradeCaptureReport(SellSideBookTradeCaptureReport sellSideBookTradeCaptureReport) {

		try {
			basisPersistenceHandler.persist(sellSideBookTradeCaptureReport);
			for (AbstractComponentHandler abstractComponentHandler : onlineTradeCaptureComponentHandlers) {
				AbstractBusinessComponent abstractBusinessComponent = abstractComponentHandler.getAbstractBusinessComponent();
				sendTradeCaptureReport(sellSideBookTradeCaptureReport, abstractBusinessComponent);
				for (AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget : sellSideBook.getAssignedSellSideTradeCaptureTargets()) {
					if (assignedSellSideTradeCaptureTarget.getAbstractBusinessComponent().getId() == abstractBusinessComponent.getId()) {
						assignedSellSideTradeCaptureTarget.setLastTradeId(sellSideBookTradeCaptureReport.getCreatedTimestamp());
						basisPersistenceHandler.update(assignedSellSideTradeCaptureTarget);
					}
				}
			}
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

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		calendar.set(java.util.Calendar.MINUTE, 0);
		calendar.set(java.util.Calendar.SECOND, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);
		for (SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry : sellSideNewOrderSingleEntryMap.values()) {
			if (sellSideNewOrderSingleEntry.getTimeInForce() == TimeInForce.FILL_OR_KILL
					|| sellSideNewOrderSingleEntry.getTimeInForce() == TimeInForce.GOOD_TILL_DAY
					|| (sellSideNewOrderSingleEntry.getTimeInForce() == TimeInForce.GOOD_TILL_DATE && sellSideNewOrderSingleEntry.getTifDate() < calendar
							.getTimeInMillis())) {
				if (sellSideNewOrderSingleEntry.getLeaveQuantity() > 0) {
					sellSideNewOrderSingleEntry.setLeaveQuantity(0);
					sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED_PENDING);
					NewOrderSingleRequest newOrderSingleRequest = new NewOrderSingleRequest(sellSideNewOrderSingleEntry, 0);
					onNewOrderSingleRequest(newOrderSingleRequest, null);
				}
			}
		}

	}

	private void handleNonQuoteMessage() {

		while (!closed) {
			try {
				MessageEntry messageEntry = messageQueue.take();

				Message message = messageEntry.getMessage();
				String messageType = message.getHeader().getString(MsgType.FIELD);

				if (messageType.equals("D")) {
					handleNewOrderSingle(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("x")) {
					handleSecurityListRequest(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("F")) {
					handleOrderCancel(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("AF")) {
					handleOrderMassStatusRequest(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("G")) {
					handleOrderReplace(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("R")) {
					handleQuoteRequest(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("AJ")) {
					handleQuoteResponse(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("AI")) {
					handleQuoteStatusReport(message, messageEntry.getInitialComponent());
				}
				else if (messageType.equals("A")) {

					for (SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry : sellSideNewOrderSingleEntryMap.values()) {
						if (sellSideNewOrderSingleEntry.getMarketInterface() == messageEntry.getInitialComponent().getId()) {
							if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.DONE_FOR_DAY_PENDING) {
								sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY);
								if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
									persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
								else
									sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY_PENDING);
							}
							else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.FILLED_PENDING) {
								sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED);
								if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
									persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
								else
									sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED_PENDING);
							}
							else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED_PENDING) {
								sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
								if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
									persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
								else
									sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED_PENDING);
							}
							else if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED_PENDING) {
								sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED);
								if (sendExecutionReport(sellSideNewOrderSingleEntry, false))
									persistNewOrderSingleEntry(sellSideNewOrderSingleEntry, null, false);
								else
									sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED_PENDING);
							}
						}
					}
				}
				else if (messageType.equals("AD")) {
					Message tradeCaptureReportRequestAck = new Message();
					tradeCaptureReportRequestAck.getHeader().setString(MsgType.FIELD, "AQ");
					tradeCaptureReportRequestAck.setString(568, message.getString(568));
					tradeCaptureReportRequestAck.setInt(569, message.getInt(569));

					if (message.getHeader().isSetField(49))
						tradeCaptureReportRequestAck.getHeader().setString(56, message.getHeader().getString(49));

					AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget = null;

					for (AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget2 : sellSideBook.getAssignedSellSideTradeCaptureTargets())
						if (assignedSellSideTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent().getId())
							assignedSellSideTradeCaptureTarget = assignedSellSideTradeCaptureTarget2;

					tradeCaptureReportRequestAck.setInt(749, 0);
					tradeCaptureReportRequestAck.setInt(750, 0);

					if (assignedSellSideTradeCaptureTarget == null) {

						assignedSellSideTradeCaptureTarget = new AssignedSellSideTradeCaptureTarget();
						assignedSellSideTradeCaptureTarget.setAbstractBusinessComponent(messageEntry.getInitialComponent());
						assignedSellSideTradeCaptureTarget.setSellSideBook(sellSideBook);
						sellSideBook.getAssignedSellSideTradeCaptureTargets().add(assignedSellSideTradeCaptureTarget);

						try {

							basisPersistenceHandler.update(sellSideBook);
						}
						catch (Exception e) {
							log.error("Bug", e);
							tradeCaptureReportRequestAck.setInt(749, 99);
							tradeCaptureReportRequestAck.setInt(750, 2);

						}
					}

					MessageEntry messageEntry2 = new MessageEntry(sellSideBook, sellSideBook, tradeCaptureReportRequestAck);

					AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(messageEntry.getInitialComponent()
							.getId());
					if (abstractComponentHandler != null) {

						onlineTradeCaptureComponentHandlers.add(abstractComponentHandler);
						abstractComponentHandler.addFIXMessage(messageEntry2);
						List<Object> parameters = new ArrayList<Object>();
						parameters.add(sellSideBook.getId());
						parameters.add(assignedSellSideTradeCaptureTarget.getLastTradeId());
						List<SellSideBookTradeCaptureReport> sellSideBookTradeCaptureReports = basisPersistenceHandler.executeQuery(
								SellSideBookTradeCaptureReport.class,
								"select s from SellSideBookTradeCaptureReport s where s.sellSideBook.id=?1 and s.createdTimestamp>?2", parameters,
								businessComponentHandler, true);
						for (SellSideBookTradeCaptureReport sellSideBookTradeCaptureReport : sellSideBookTradeCaptureReports) {
							if (sendTradeCaptureReport(sellSideBookTradeCaptureReport, messageEntry.getInitialComponent()))
								for (AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget2 : sellSideBook
										.getAssignedSellSideTradeCaptureTargets()) {
									if (assignedSellSideTradeCaptureTarget2.getAbstractBusinessComponent().getId() == messageEntry.getInitialComponent()
											.getId()) {
										assignedSellSideTradeCaptureTarget2.setLastTradeId(sellSideBookTradeCaptureReport.getCreatedTimestamp());
										try {
											basisPersistenceHandler.update(assignedSellSideTradeCaptureTarget2);
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

	private void handleQuoteResponse(Message message, AbstractBusinessComponent businessComponent) {

		for (SellSideQuoteRequestEntry sellSideQuoteRequestEntry : sellSideQuoteRequestEntryMap.values()) {
			try {
				if (sellSideQuoteRequestEntry.getQuoteId() != null && message.isSetField(117)
						&& sellSideQuoteRequestEntry.getQuoteId().equals(message.getString(117))) {

					String traderId = null;
					String traderIdSource = null;

					for (int i = 1; i <= message.getGroupCount(453); i++) {

						Group parties = message.getGroup(i, 453);
						if (parties.isSetField(452) && parties.getInt(452) == 11) {

							if (parties.isSetField(447))
								traderIdSource = parties.getString(447);

							if (parties.isSetField(448))
								traderId = parties.getString(448);
						}
					}

					Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideQuoteRequestEntry.getCounterparty(),
							businessComponentHandler);

					if (counterparty != null && traderId != null) {

						Trader trader = getTrader(counterparty, traderId, traderIdSource, businessComponent);

						sellSideQuoteRequestEntry.setTrader(trader.getId());

					}

					FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sellSideQuoteRequestEntry.getSecurity(), businessComponentHandler);

					switch (message.getInt(694)) {

						case 1:

							if (message.isSetField(11))
								sellSideQuoteRequestEntry.setCounterpartyOrderId(message.getString(11));

							if (sellSideQuoteRequestEntry.getSubjectDate() > System.currentTimeMillis()) {
								sellSideQuoteRequestEntry.setLeaveQuantity(0d);
								sellSideQuoteRequestEntry.setLastQuantity(sellSideQuoteRequestEntry.getOrderQuantity());
								sellSideQuoteRequestEntry.setCumulativeQuantity(sellSideQuoteRequestEntry.getOrderQuantity());
								sellSideQuoteRequestEntry.setOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));
								sellSideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED_PENDING);

								if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									sellSideQuoteRequestEntry.setLastYield(sellSideQuoteRequestEntry.getLimit());
									sellSideQuoteRequestEntry.setLastPrice(getPrice(security, sellSideQuoteRequestEntry.getLimit()));
								}
								else
									sellSideQuoteRequestEntry.setLastPrice(sellSideQuoteRequestEntry.getLimit());
							}
							else
								sellSideQuoteRequestEntry
										.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.HIT_LIFT);
							break;

						case 2:

							if (message.isSetField(11))
								sellSideQuoteRequestEntry.setCounterpartyOrderId(message.getString(11));

							if (message.getChar(54) == '1') {

								if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
									sellSideQuoteRequestEntry.setLimit(message.getDouble(634));
								else
									sellSideQuoteRequestEntry.setLimit(message.getDouble(133));
								sellSideQuoteRequestEntry.setOrderQuantity(message.getDouble(135));
								sellSideQuoteRequestEntry.setLeaveQuantity(message.getDouble(135));
							}
							else {
								if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
									sellSideQuoteRequestEntry.setLimit(message.getDouble(632));
								else
									sellSideQuoteRequestEntry.setLimit(message.getDouble(132));

								sellSideQuoteRequestEntry.setOrderQuantity(message.getDouble(134));
								sellSideQuoteRequestEntry.setLeaveQuantity(message.getDouble(134));
							}

							sellSideQuoteRequestEntry
									.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COUNTER);
							break;

						case 3:

							sellSideQuoteRequestEntry
									.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.EXPIRED);
							sellSideQuoteRequestEntry.setLeaveQuantity(0);
							break;

						case 4:

							sellSideQuoteRequestEntry
									.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COVER);
							sellSideQuoteRequestEntry.setLeaveQuantity(0);
							break;

						case 5:

							sellSideQuoteRequestEntry
									.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.DONE_AWAY);
							sellSideQuoteRequestEntry.setLeaveQuantity(0);
							break;

						case 6:

							sellSideQuoteRequestEntry
									.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.PASS);
							sellSideQuoteRequestEntry.setLeaveQuantity(0);
							break;
					}

					sellSideQuoteRequestEntry.setQuoteRespId(message.getString(693));

					sellSideQuoteRequestEntry.setUpdated(System.currentTimeMillis());

					if (sellSideQuoteRequestEntry.getOrderStatus() != net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COUNTER
							|| (isValidPrice(security, sellSideQuoteRequestEntry.getLimit()) && isValidQuantity(security,
									sellSideQuoteRequestEntry.getOrderQuantity()))) {

						sellSideQuoteRequestEntryMap.put(
								sellSideQuoteRequestEntry.getQuoteReqId() + decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()),
								sellSideQuoteRequestEntry);

						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);
					}
					else {

						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);

						StringBuffer stringBuffer = new StringBuffer("System automatically canceled inquiry for security ");
						stringBuffer.append(security.getName());
						stringBuffer.append(" from counterparty ");
						stringBuffer.append(counterparty.getName());
						stringBuffer.append(". Reject reason was an invalid counter");
						if (!isValidPrice(security, sellSideQuoteRequestEntry.getLimit()))
							stringBuffer.append(" limit.");
						if (!isValidQuantity(security, sellSideQuoteRequestEntry.getOrderQuantity()))
							stringBuffer.append(" quantity.");
						stringBuffer.append(" Please contact your counterparty.");
						LogEntry logEntry = new LogEntry();
						logEntry.setLogLevel(Level.WARNING);
						logEntry.setLogDate(new Date());
						logEntry.setMessageComponent(sellSideBook.getName());
						logEntry.setMessageText(stringBuffer.toString());
						logEntry.setHighlightKey(sellSideQuoteRequestEntry.getQuoteReqId());
						basisPersistenceHandler.writeLogEntry(logEntry);
					}
				}
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}

	}

	private synchronized void handleQuoteStatusReport(Message message, AbstractBusinessComponent businessComponent) {

		try {

			if (message.getInt(297) == 11) {

				String partyId = null;
				String partyIdSource = null;

				for (int i = 1; i <= message.getGroupCount(453); i++) {

					Group parties = message.getGroup(i, 453);
					if (parties.isSetField(452) && parties.getInt(452) == 13) {

						if (parties.isSetField(447))
							partyIdSource = parties.getString(447);

						if (parties.isSetField(448))
							partyId = parties.getString(448);
					}

				}

				Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 13);

				if (counterparty != null) {

					SellSideQuoteRequestEntry sellSideQuoteRequestEntry = sellSideQuoteRequestEntryMap.get(message.getString(131)
							+ decimalFormat3.format(counterparty.getId()));

					if (sellSideQuoteRequestEntry != null) {

						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.PASS);

						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);
					}

				}
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}

	}

	private void handleQuoteRequest(Message message, AbstractBusinessComponent businessComponent) {

		for (int j = 1; j <= Math.min(message.getGroupCount(146), 1); j++) {

			try {

				Group noRelatedSym = message.getGroup(j, 146);

				SellSideQuoteRequestEntry sellSideQuoteRequestEntry = new SellSideQuoteRequestEntry();
				sellSideQuoteRequestEntry.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.NEW);

				String partyId = null;
				String partyIdSource = null;

				String traderId = null;
				String traderIdSource = null;

				for (int i = 1; i <= noRelatedSym.getGroupCount(453); i++) {

					Group parties = noRelatedSym.getGroup(i, 453);
					if (parties.isSetField(452) && parties.getInt(452) == 13) {

						if (parties.isSetField(447))
							partyIdSource = parties.getString(447);

						if (parties.isSetField(448))
							partyId = parties.getString(448);
					}
					if (parties.isSetField(452) && parties.getInt(452) == 11) {

						if (parties.isSetField(447))
							traderIdSource = parties.getString(447);

						if (parties.isSetField(448))
							traderId = parties.getString(448);
					}
				}

				Counterparty counterparty = businessComponentHandler.getCounterparty(businessComponent.getId(), partyId, partyIdSource, 13);
				if (counterparty != null) {

					sellSideQuoteRequestEntry.setCounterparty(counterparty.getId());

					if (traderId != null) {

						Trader trader = getTrader(counterparty, traderId, traderIdSource, businessComponent);

						sellSideQuoteRequestEntry.setTrader(trader.getId());

					}

					if (message.isSetField(131) && noRelatedSym.isSetField(38)) {

						sellSideQuoteRequestEntry.setQuoteReqId(message.getString(131));
						sellSideQuoteRequestEntry.setOrderQuantity(noRelatedSym.getDouble(38));
						sellSideQuoteRequestEntry.setLeaveQuantity(noRelatedSym.getDouble(38));
						sellSideQuoteRequestEntry.setCreated(System.currentTimeMillis());
						sellSideQuoteRequestEntry.setCumulativeQuantity(0);
						sellSideQuoteRequestEntry.setLastPrice(null);
						sellSideQuoteRequestEntry.setLastQuantity(null);
						sellSideQuoteRequestEntry.setMarketInterface(businessComponent.getId());
						if (noRelatedSym.isSetField(110))
							sellSideQuoteRequestEntry.setMinQuantity(110);

						if (noRelatedSym.isSetField(126))
							sellSideQuoteRequestEntry.setExpireDate(noRelatedSym.getUtcTimeStamp(126).getTime());

						String securityId = null;
						String securityIdSource = null;
						if (noRelatedSym.isSetField(48))
							securityId = noRelatedSym.getString(48);
						if (noRelatedSym.isSetField(22))
							securityIdSource = noRelatedSym.getString(22);

						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);

						if (security != null) {

							SellSideMDInputEntry mdInputEntry = securityEntries.get(security.getId());

							boolean assigned = false;

							for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBook.getAssignedSellSideBookSecurities())
								if (assignedSellSideBookSecurity.getSecurity().getId() == security.getId())
									assigned = true;

							if (assigned) {

								sellSideQuoteRequestEntry.setSecurity(security.getId());
								sellSideQuoteRequestEntry.setSellSideBook(sellSideBook.getId());
								if (noRelatedSym.isSetField(54)) {

									if (noRelatedSym.getString(54).equals("1")) {
										sellSideQuoteRequestEntry.setSide(Side.ASK);
										if (mdInputEntry != null) {
											if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
													&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
												sellSideQuoteRequestEntry.setLimit(mdInputEntry.getMdEntryAskYieldValue());
											else
												sellSideQuoteRequestEntry.setLimit(mdInputEntry.getMdEntryAskPxValue());

										}
									}
									else if (noRelatedSym.getString(54).equals("2")) {
										sellSideQuoteRequestEntry.setSide(Side.BID);
										if (mdInputEntry != null) {
											if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
													&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
												sellSideQuoteRequestEntry.setLimit(mdInputEntry.getMdEntryBidYieldValue());
											else
												sellSideQuoteRequestEntry.setLimit(mdInputEntry.getMdEntryBidPxValue());
										}
									}
									else {

									}
									if (noRelatedSym.isSetField(64))
										sellSideQuoteRequestEntry.setSettlementDate(noRelatedSym.getUtcDateOnly(64).getTime());

									sellSideQuoteRequestEntry.setUpdated(System.currentTimeMillis());

									if (isValidQuantity(security, sellSideQuoteRequestEntry.getOrderQuantity())) {
										sellSideQuoteRequestEntryMap.put(
												sellSideQuoteRequestEntry.getQuoteReqId() + decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()),
												sellSideQuoteRequestEntry);
										persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, null, true);
									}
									else {

										sellSideQuoteRequestEntry.setLimit(null);
										sellSideQuoteRequestEntry
												.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING);
										sendQuoteRequestReject(sellSideQuoteRequestEntry, null);
										
										StringBuffer stringBuffer = new StringBuffer("System automatically canceled inquiry for security ");
										stringBuffer.append(security.getName());
										stringBuffer.append(" from counterparty ");
										stringBuffer.append(counterparty.getName());
										stringBuffer.append(". Reject reason was an invalid quantity.");
										stringBuffer.append(" Please contact your counterparty.");
										LogEntry logEntry = new LogEntry();
										logEntry.setLogLevel(Level.WARNING);
										logEntry.setLogDate(new Date());
										logEntry.setMessageComponent(sellSideBook.getName());
										logEntry.setMessageText(stringBuffer.toString());
										logEntry.setHighlightKey(sellSideQuoteRequestEntry.getQuoteReqId());
										basisPersistenceHandler.writeLogEntry(logEntry);
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

	/**
	 * On sell side quote request request.
	 *
	 * @param sellSideQuoteRequestRequest the sell side quote request request
	 * @param channel the channel
	 * @return the sell side quote request entry
	 */
	public synchronized SellSideQuoteRequestEntry onSellSideQuoteRequestRequest(SellSideQuoteRequestRequest sellSideQuoteRequestRequest, Channel channel) {

		SellSideQuoteRequestEntry sellSideQuoteRequestEntry = sellSideQuoteRequestRequest.getSellSideQuoteRequestEntry();

		long timestamp = System.currentTimeMillis();
		sellSideQuoteRequestEntry.setUpdated(timestamp);

		FUser user = null;

		if (channel != null) {
			user = basisPersistenceHandler.getUser(channel);
			sellSideQuoteRequestEntry.setUser(user.getName());
		}

		SellSideQuoteRequestEntry lastEntry = sellSideQuoteRequestEntryMap.get(sellSideQuoteRequestEntry.getQuoteReqId()
				+ decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()));

		if (lastEntry != null) {

			if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING) {
				if (!lastEntry.isDone()) {
					if (!(lastEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED && lastEntry
							.getSubjectDate() > System.currentTimeMillis())) {
						if (lastEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.NEW)
							sellSideQuoteRequestEntry.setLimit(null);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, user, true);
						sellSideQuoteRequestEntryMap.put(
								sellSideQuoteRequestEntry.getQuoteReqId() + decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()),
								sellSideQuoteRequestEntry);
					}
				}

			}

			else if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED_PENDING) {
				if (!lastEntry.isDone()) {
					if (!(lastEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED && lastEntry
							.getSubjectDate() > System.currentTimeMillis())) {

						sellSideQuoteRequestEntry.setQuoteId(decimalFormat3.format(basisPersistenceHandler.getId("QUOTEID")));
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, user, true);
						sellSideQuoteRequestEntryMap.put(
								sellSideQuoteRequestEntry.getQuoteReqId() + decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()),
								sellSideQuoteRequestEntry);
						sendQuote(sellSideQuoteRequestEntry, user);
					}
				}
			}

			else if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED_PENDING) {
				if (!lastEntry.isDone()) {
					if (!(lastEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED && lastEntry
							.getSubjectDate() > System.currentTimeMillis())) {
						sellSideQuoteRequestEntry.setLeaveQuantity(0d);
						sellSideQuoteRequestEntry.setLastQuantity(sellSideQuoteRequestEntry.getOrderQuantity());
						sellSideQuoteRequestEntry.setCumulativeQuantity(sellSideQuoteRequestEntry.getOrderQuantity());
						sellSideQuoteRequestEntry.setOrderId(decimalFormat3.format(basisPersistenceHandler.getId("ORDERID")));
						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.FILLED_PENDING);

						FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sellSideQuoteRequestEntry.getSecurity(),
								businessComponentHandler);

						if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
								&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
							sellSideQuoteRequestEntry.setLastYield(sellSideQuoteRequestEntry.getLimit());
							sellSideQuoteRequestEntry.setLastPrice(getPrice(security, sellSideQuoteRequestEntry.getLimit()));
						}
						else
							sellSideQuoteRequestEntry.setLastPrice(sellSideQuoteRequestEntry.getLimit());

						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, user, true);
						sellSideQuoteRequestEntryMap.put(
								sellSideQuoteRequestEntry.getQuoteReqId() + decimalFormat3.format(sellSideQuoteRequestEntry.getCounterparty()),
								sellSideQuoteRequestEntry);
					}
				}
			}

		}

		return sellSideQuoteRequestRequest.getSellSideQuoteRequestEntry();
	}

	private void sendQuoteStatusReport(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, FUser fUser) {

		Message quoteStatusReport = new Message();

		quoteStatusReport.getHeader().setString(MsgType.FIELD, "AI");

		quoteStatusReport.setString(131, sellSideQuoteRequestEntry.getQuoteReqId());
		quoteStatusReport.setString(117, sellSideQuoteRequestEntry.getQuoteId());

		if (sellSideQuoteRequestEntry.getQuoteRespId() != null)
			quoteStatusReport.setString(693, sellSideQuoteRequestEntry.getQuoteRespId());

		quoteStatusReport.setInt(537, 1);

		if (sellSideQuoteRequestEntry.getSubjectDate() != null)
			quoteStatusReport.setUtcTimeStamp(62, new Date(sellSideQuoteRequestEntry.getSubjectDate()));

		quoteStatusReport.setDouble(38, sellSideQuoteRequestEntry.getOrderQuantity());

		if (sellSideQuoteRequestEntry.getMinQuantity() > 0)
			quoteStatusReport.setDouble(110, sellSideQuoteRequestEntry.getMinQuantity());
		quoteStatusReport.setString(64, localMarketDateFormat.format(new Date(sellSideQuoteRequestEntry.getSettlementDate())));

		quoteStatusReport.setUtcTimeStamp(60, new Date(sellSideQuoteRequestEntry.getCreated()));

		if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING) {
			if (sellSideQuoteRequestEntry.getExpireDate() < System.currentTimeMillis())
				quoteStatusReport.setInt(297, 7);
			else
				quoteStatusReport.setInt(297, 11);
		}

		FSecurity security = securityDictionary.getSecurityForBusinessObjectID(sellSideQuoteRequestEntry.getSecurity());

		if (security != null) {

			if (sellSideQuoteRequestEntry.getSide() == Side.BID) {
				quoteStatusReport.setChar(54, '2');
				if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
					quoteStatusReport.setDouble(632, sellSideQuoteRequestEntry.getLimit());
				else
					quoteStatusReport.setDouble(132, sellSideQuoteRequestEntry.getLimit());
				quoteStatusReport.setDouble(134, sellSideQuoteRequestEntry.getOrderQuantity());

			}
			else {
				quoteStatusReport.setChar(54, '1');
				if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
					quoteStatusReport.setDouble(634, sellSideQuoteRequestEntry.getLimit());
				else
					quoteStatusReport.setDouble(133, sellSideQuoteRequestEntry.getLimit());
				quoteStatusReport.setDouble(135, sellSideQuoteRequestEntry.getOrderQuantity());
			}

			quoteStatusReport.setString(48, security.getSecurityID());
			quoteStatusReport.setString(22, security.getSecurityDetails().getSecurityIDSource());
			quoteStatusReport.setString(55, "N/A");

			AbstractAcceptor abstractAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, sellSideQuoteRequestEntry.getMarketInterface(),
					businessComponentHandler);

			Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideQuoteRequestEntry.getCounterparty(),
					businessComponentHandler);

			if (abstractAcceptor != null) {

				if (abstractAcceptor instanceof FIXAcceptor) {
					FIXAcceptor fixAcceptor = (FIXAcceptor) abstractAcceptor;
					if (fixAcceptor.getPartyID() != null) {
						final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
						group.setString(448, fixAcceptor.getPartyID());
						if (fixAcceptor.getPartyIDSource() != null)
							group.setChar(447, fixAcceptor.getPartyIDSource().charAt(0));
						group.setInt(452, 1);
						quoteStatusReport.addGroup(group);
					}
				}

				if (fUser != null) {

					sellSideQuoteRequestEntry.setUser(fUser.getName());
					addUserGroup(abstractAcceptor, fUser, quoteStatusReport);

				}

				if (counterparty != null) {

					addCounterpartyGroup(abstractAcceptor, counterparty, 13, quoteStatusReport);

					if (sellSideQuoteRequestEntry.getTrader() != null) {

						addTraderGroup(abstractAcceptor, counterparty, sellSideQuoteRequestEntry.getTrader(), quoteStatusReport);

					}

				}

				if (abstractAcceptor.getSecurityIDSource() != null) {
					String securityId = securityDictionary.getAlternativeSecurityID(security.getSecurityID(), abstractAcceptor.getSecurityIDSource());
					if (securityId != null) {
						quoteStatusReport.setString(48, securityId);
						quoteStatusReport.setString(22, abstractAcceptor.getSecurityIDSource());
					}
				}

				if (counterparty != null && abstractAcceptor != null) {

					AbstractComponentHandler targetComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractAcceptor.getId());

					String targetCompID = targetComponentHandler.getTargetCompIDIfOnline(counterparty);

					if (targetCompID != null) {
						quoteStatusReport.getHeader().setString(56, targetCompID);
						targetComponentHandler.addFIXMessage(new MessageEntry(sellSideBook, sellSideBook, quoteStatusReport));
						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, fUser, true);
					}
				}

			}
		}

	}

	private void sendQuote(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, FUser fUser) {

		Message quote = new Message();

		quote.getHeader().setString(MsgType.FIELD, "S");

		quote.setString(131, sellSideQuoteRequestEntry.getQuoteReqId());
		quote.setString(117, sellSideQuoteRequestEntry.getQuoteId());

		if (sellSideQuoteRequestEntry.getQuoteRespId() != null)
			quote.setString(693, sellSideQuoteRequestEntry.getQuoteRespId());

		quote.setInt(537, 1);
		quote.setInt(301, 0);
		quote.setUtcTimeStamp(62, new Date(sellSideQuoteRequestEntry.getSubjectDate()));

		quote.setDouble(38, sellSideQuoteRequestEntry.getOrderQuantity());

		if (sellSideQuoteRequestEntry.getMinQuantity() > 0)
			quote.setDouble(110, sellSideQuoteRequestEntry.getMinQuantity());
		quote.setString(64, localMarketDateFormat.format(new Date(sellSideQuoteRequestEntry.getSettlementDate())));

		quote.setUtcTimeStamp(60, new Date(sellSideQuoteRequestEntry.getCreated()));

		FSecurity security = securityDictionary.getSecurityForBusinessObjectID(sellSideQuoteRequestEntry.getSecurity());

		if (security != null) {

			if (sellSideQuoteRequestEntry.getSide() == Side.BID) {
				quote.setChar(54, '2');
				if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
					quote.setDouble(632, sellSideQuoteRequestEntry.getLimit());
				else
					quote.setDouble(132, sellSideQuoteRequestEntry.getLimit());
				quote.setDouble(134, sellSideQuoteRequestEntry.getOrderQuantity());

			}
			else {
				quote.setChar(54, '1');
				if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
						&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
					quote.setDouble(634, sellSideQuoteRequestEntry.getLimit());
				else
					quote.setDouble(133, sellSideQuoteRequestEntry.getLimit());
				quote.setDouble(135, sellSideQuoteRequestEntry.getOrderQuantity());
			}

			quote.setString(48, security.getSecurityID());
			quote.setString(22, security.getSecurityDetails().getSecurityIDSource());
			quote.setString(55, "N/A");

			AbstractAcceptor abstractAcceptor = basisPersistenceHandler.findById(AbstractAcceptor.class, sellSideQuoteRequestEntry.getMarketInterface(),
					businessComponentHandler);

			Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sellSideQuoteRequestEntry.getCounterparty(),
					businessComponentHandler);

			if (abstractAcceptor != null) {

				if (abstractAcceptor instanceof FIXAcceptor) {
					FIXAcceptor fixAcceptor = (FIXAcceptor) abstractAcceptor;
					if (fixAcceptor.getPartyID() != null) {
						final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
						group.setString(448, fixAcceptor.getPartyID());
						if (fixAcceptor.getPartyIDSource() != null)
							group.setChar(447, fixAcceptor.getPartyIDSource().charAt(0));
						group.setInt(452, 1);
						quote.addGroup(group);
					}
				}

				if (fUser != null) {

					sellSideQuoteRequestEntry.setUser(fUser.getName());
					addUserGroup(abstractAcceptor, fUser, quote);

				}

				if (counterparty != null) {

					addCounterpartyGroup(abstractAcceptor, counterparty, 13, quote);

					if (sellSideQuoteRequestEntry.getTrader() != null) {

						addTraderGroup(abstractAcceptor, counterparty, sellSideQuoteRequestEntry.getTrader(), quote);

					}

				}

				if (abstractAcceptor.getSecurityIDSource() != null) {
					String securityId = securityDictionary.getAlternativeSecurityID(security.getSecurityID(), abstractAcceptor.getSecurityIDSource());
					if (securityId != null) {
						quote.setString(48, securityId);
						quote.setString(22, abstractAcceptor.getSecurityIDSource());
					}
				}

				if (counterparty != null && abstractAcceptor != null) {

					AbstractComponentHandler targetComponentHandler = businessComponentHandler.getBusinessComponentHandler(abstractAcceptor.getId());

					String targetCompID = targetComponentHandler.getTargetCompIDIfOnline(counterparty);

					if (targetCompID != null) {
						quote.getHeader().setString(56, targetCompID);
						targetComponentHandler.addFIXMessage(new MessageEntry(sellSideBook, sellSideBook, quote));
						sellSideQuoteRequestEntry
								.setOrderStatus(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.QUOTED);
						persistSellSideQuoteRequestEntry(sellSideQuoteRequestEntry, fUser, true);
					}
				}

			}
		}

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
		
		if(price==null)
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
