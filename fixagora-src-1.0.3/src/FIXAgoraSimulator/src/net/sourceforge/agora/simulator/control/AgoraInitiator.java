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

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.agora.simulator.model.BlotterEntry;
import net.sourceforge.agora.simulator.model.BlotterEntry.LevelIcon;
import net.sourceforge.agora.simulator.model.BlotterText;
import net.sourceforge.agora.simulator.model.Security;
import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DefaultSessionFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

/**
 * The Class AgoraInitiator.
 */
public class AgoraInitiator implements Application {

	private List<String> traders = null;
	private DecimalFormat decimalFormat = new DecimalFormat("0.0#####################");
	private String conf = null;
	private int spread = 0;
	private BigDecimal sizeFactor = null;
	private Vector<BlotterListener> blotterListeners = new Vector<BlotterListener>();
	private SocketInitiator initiator = null;
	private SessionID sessionID = null;
	private Vector<AgoraAcceptor> agoraAcceptors = new Vector<AgoraAcceptor>();
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private Map<String, Group> refMap = new HashMap<String, Group>();
	private long id = System.currentTimeMillis();
	private DecimalFormat idFormat = new DecimalFormat("######");
	private Map<String, Map<Character, String>> refIdMap = new HashMap<String, Map<Character, String>>();
	private Map<String, Map<Character, String>> lastValueMap = new HashMap<String, Map<Character, String>>();
	private Map<String, Message> openOrders = new HashMap<String, Message>();
	private Map<String, Message> openQuoteRequests = new HashMap<String, Message>();
	private Map<String, Message> openQuotes = new HashMap<String, Message>();
	private Map<String, InitiatorMDInputEntry> securityEntries = new HashMap<String, InitiatorMDInputEntry>();
	private long orderId = System.currentTimeMillis();
	private long quoteReqId = System.currentTimeMillis();
	private long quoteRespId = System.currentTimeMillis();
	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private DecimalFormat quantityFormat = new DecimalFormat("#,##0.##############");
	private Color green = new Color(51, 153, 0);
	private Color red = new Color(204, 0, 0);

	private String name;

	/**
	 * Instantiates a new agora initiator.
	 *
	 * @param conf the conf
	 * @param spread the spread
	 * @param sizeFactor the size factor
	 * @param traders the traders
	 */
	public AgoraInitiator(String conf, int spread, double sizeFactor, List<String> traders) {

		super();

		this.traders = traders;

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		quantityFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		this.conf = conf;
		this.spread = spread;
		this.sizeFactor = new BigDecimal(decimalFormat.format(sizeFactor));

		Thread thread = new Thread() {

			@Override
			public void run() {

				while (true) {

					try {

						Thread.sleep(10000);

						handleExecutionReports();
						handleQuotes();

					}
					catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}

		};
		thread.start();
	}

	/**
	 * Connect.
	 */
	public void connect() {

		try {
			final InputStream inputStream = new FileInputStream("./conf/" + conf + "_initiator.conf");

			Properties properties = new Properties();

			BufferedInputStream stream = new BufferedInputStream(inputStream);
			properties.load(stream);
			stream.close();

			name = properties.getProperty("name");

			final InputStream inputStream2 = new FileInputStream("./conf/" + conf + "_initiator.conf");

			final SessionSettings settings = new SessionSettings(inputStream2);
			final MessageStoreFactory storeFactory = new FileStoreFactory(settings);
			final LogFactory logFactory = new EmptyLogFactory();
			final MessageFactory messageFactory = new DefaultMessageFactory();

			initiator = new SocketInitiator(new DefaultSessionFactory(this, storeFactory, logFactory, messageFactory), settings);
			initiator.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds the blotter listener.
	 *
	 * @param blotterListener the blotter listener
	 */
	public void addBlotterListener(BlotterListener blotterListener) {

		blotterListeners.add(blotterListener);
	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onCreate(quickfix.SessionID)
	 */
	@Override
	public void onCreate(SessionID sessionId) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onLogon(quickfix.SessionID)
	 */
	@Override
	public void onLogon(SessionID sessionId) {

		this.sessionID = sessionId;

		refMap.clear();

		List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

		BlotterText blotterText = new BlotterText(name + " buy side is connected to FIX Agora Server");
		blotterTexts.add(blotterText);

		BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.writeBlotterEntry(blotterEntry);

		executorService.execute(new Runnable() {

			@Override
			public void run() {

				handleLogon();

			}
		});

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#onLogout(quickfix.SessionID)
	 */
	@Override
	public void onLogout(SessionID sessionId) {

		this.sessionID = null;

		List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

		BlotterText blotterText = new BlotterText(name + " buy side is disconnected from FIX Agora Server");
		blotterTexts.add(blotterText);

		BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.WARNING);

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.writeBlotterEntry(blotterEntry);

		executorService.execute(new Runnable() {

			@Override
			public void run() {

				handleLogout();

			}
		});

	}

	private synchronized void handleQuotes() {

		if (sessionID == null)
			return;

		for (Message message : openQuotes.values()) {

			try {

				if ((!message.isSetField(694) || message.getInt(694) != 2) && Math.random() <= 0.33) {

					double random = Math.random();

					openQuotes.remove(message.getString(131));

					Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));

					Message quoteResponse = (Message) message.clone();
					quoteResponse.getHeader().setString(MsgType.FIELD, "AJ");
					quoteResponse.removeField(131);
					quoteResponse.removeField(301);
					quoteResponse.setString(693, idFormat.format(quoteRespId++));

					List<BlotterText> blotterTexts3 = new ArrayList<BlotterText>();

					if (random < 0.33) {
						if (message.getChar(54) == '1') {
							blotterTexts3.add(new BlotterText(name + " lifted quote from  Bank of Midas for inquiry "));
						}
						else {
							blotterTexts3.add(new BlotterText(name + " hit quote from  Bank of Midas for inquiry "));
						}
						quoteResponse.setInt(694, 1);
						quoteResponse.setString(11, idFormat.format(orderId++));
						openOrders.put(quoteResponse.getString(11), quoteResponse);

					}
					else if (random < 0.66) {
						blotterTexts3.add(new BlotterText(name + " countered quote from  Bank of Midas for inquiry "));
						quoteResponse.setInt(694, 2);
						quoteResponse.setString(11, idFormat.format(orderId++));
						openOrders.put(quoteResponse.getString(11), quoteResponse);
					}
					else if (random < 0.77) {
						blotterTexts3.add(new BlotterText(name + " covered quote from  Bank of Midas for inquiry "));
						quoteResponse.setInt(694, 4);
					}
					else if (random < 0.88) {
						blotterTexts3.add(new BlotterText(name + " done away quote from  Bank of Midas for inquiry "));
						quoteResponse.setInt(694, 5);
					}
					else {
						blotterTexts3.add(new BlotterText(name + " passed quote from  Bank of Midas for inquiry "));
						quoteResponse.setInt(694, 6);
					}

					if (message.getChar(54) == '1')
						blotterTexts3.add(new BlotterText("ask ", red));
					else
						blotterTexts3.add(new BlotterText("bid ", green));
					blotterTexts3.add(new BlotterText(quantityFormat.format(message.getDouble(38))));

					if (security != null) {
						blotterTexts3.add(new BlotterText(" " + security.getName()));
					}

					blotterTexts3.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));

					BlotterEntry blotterEntry3 = new BlotterEntry(name, blotterTexts3, LevelIcon.INFO);

					for (BlotterListener blotterListener : blotterListeners)
						blotterListener.writeBlotterEntry(blotterEntry3);

					if (sessionID != null) {
						Session session = Session.lookupSession(sessionID);
						if (session != null)
							session.send(quoteResponse);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void handleExecutionReports() {

		try {

			if (sessionID == null)
				return;

			if (openOrders.size() < 1 && openQuoteRequests.size() < 1 && openQuotes.size() < 1) {
				int value = (int) (securityEntries.size() * Math.random());
				int i = 0;
				for (InitiatorMDInputEntry initiatorMDInputEntry : securityEntries.values()) {
					i++;
					if (i == value) {
						try {

							if (Math.random() >= 0.5) {

								List<BlotterText> blotterTexts = new ArrayList<BlotterText>();
								blotterTexts.add(new BlotterText(name + " is initiating new inquiry "));

								Message message = new Message();
								message.getHeader().setString(MsgType.FIELD, "R");
								message.setString(131, idFormat.format(quoteReqId++));

								Group noRelatedSym = new Group(146, 55, new int[] { 55, 48, 22, 54, 38, 64, 126, 60, 423, 453, 110 });

								int quantity = 0;

								if (value % 2 == 0 && initiatorMDInputEntry.getMdEntryBidPxValue() != null) {
									blotterTexts.add(new BlotterText("buy ", green));
									noRelatedSym.setChar(54, '2');
									if (initiatorMDInputEntry.getMdEntryBidSizeValue() != null)
										quantity = (int) initiatorMDInputEntry.getMdEntryBidSizeValue().intValue();
								}
								else if (initiatorMDInputEntry.getMdEntryAskPxValue() != null) {
									blotterTexts.add(new BlotterText("sell ", red));
									noRelatedSym.setChar(54, '1');
									if (initiatorMDInputEntry.getMdEntryAskSizeValue() != null)
										quantity = (int) initiatorMDInputEntry.getMdEntryAskSizeValue().intValue();
								}

								long timeStamp = System.currentTimeMillis();

								noRelatedSym.setUtcTimeStamp(60, new Date(timeStamp));
								noRelatedSym.setUtcTimeStamp(126, new Date(timeStamp + 180000));

								int zeroCount = 1;
								int checkQuantity = quantity;
								while (checkQuantity > 0 && checkQuantity % 10 == 0) {
									zeroCount = zeroCount * 10;
									checkQuantity = checkQuantity / 10;
								}
								quantity = ((int) (quantity / (5 * (zeroCount)))) * zeroCount;

								if (quantity > 0) {

									noRelatedSym.setDouble(38, quantity);

									blotterTexts.add(new BlotterText(quantityFormat.format(quantity)));

									Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(
											initiatorMDInputEntry.getSecurityValue(), initiatorMDInputEntry.getSecurityIDSource());

									if (security != null) {
										blotterTexts.add(new BlotterText(" " + security.getName()));
									}

									if (Math.random() > 0.5)
										noRelatedSym.setDouble(110, quantity);

									Calendar calendar = Calendar.getInstance();
									for (int j = 0; j < 3; j++) {
										calendar.add(Calendar.DAY_OF_YEAR, 1);
										while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
											calendar.add(Calendar.DAY_OF_YEAR, 1);
									}

									noRelatedSym.setString(64, localMarketDateFormat.format(calendar.getTime()));

									noRelatedSym.setString(55, "N/A");
									noRelatedSym.setString(48, initiatorMDInputEntry.getSecurityValue());
									noRelatedSym.setString(22, initiatorMDInputEntry.getSecurityIDSource());

									final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
									group.setString(448, conf.toUpperCase());
									group.setChar(447, 'B');
									group.setInt(452, 13);
									noRelatedSym.addGroup(group);

									final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
									group2.setString(448, sessionID.getTargetCompID().substring(0, sessionID.getTargetCompID().length() - 1));
									group2.setChar(447, 'B');
									group2.setInt(452, 1);
									noRelatedSym.addGroup(group2);

									final Group group3 = new Group(453, 448, new int[] { 448, 447, 452 });
									group3.setString(448, traders.get((int) (Math.random() * (double) traders.size())));

									group3.setChar(447, 'D');
									group3.setInt(452, 11);
									noRelatedSym.addGroup(group3);

									message.addGroup(noRelatedSym);

									openQuoteRequests.put(message.getString(131), message);

									if (sessionID != null) {
										Session session = Session.lookupSession(sessionID);
										if (session != null) {
											session.send((Message) message.clone());

											BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

											for (BlotterListener blotterListener : blotterListeners)
												blotterListener.writeBlotterEntry(blotterEntry);

										}
									}
								}

							}
							else {

								List<BlotterText> blotterTexts = new ArrayList<BlotterText>();
								blotterTexts.add(new BlotterText(name + " is initiating new order "));

								Message message = new Message();
								message.getHeader().setString(MsgType.FIELD, "D");
								message.setString(11, idFormat.format(orderId++));

								int quantity = 0;

								if (value % 2 == 0 && initiatorMDInputEntry.getMdEntryBidPxValue() != null) {
									blotterTexts.add(new BlotterText("buy ", green));
									message.setChar(54, '2');
									message.setDouble(44, initiatorMDInputEntry.getMdEntryBidPxValue());
									if (initiatorMDInputEntry.getMdEntryBidSizeValue() != null)
										quantity = (int) initiatorMDInputEntry.getMdEntryBidSizeValue().intValue();
								}
								else if (initiatorMDInputEntry.getMdEntryAskPxValue() != null) {
									blotterTexts.add(new BlotterText("sell ", red));
									message.setChar(54, '1');
									message.setDouble(44, initiatorMDInputEntry.getMdEntryAskPxValue());
									if (initiatorMDInputEntry.getMdEntryAskSizeValue() != null)
										quantity = (int) initiatorMDInputEntry.getMdEntryAskSizeValue().intValue();
								}

								message.setUtcTimeStamp(60, new Date());
								message.setChar(40, '2');
								int zeroCount = 1;
								int checkQuantity = quantity;
								while (checkQuantity > 0 && checkQuantity % 10 == 0) {
									zeroCount = zeroCount * 10;
									checkQuantity = checkQuantity / 10;
								}
								quantity = ((int) (quantity / (5 * (zeroCount)))) * zeroCount;

								if (quantity > 0 && message.isSetField(44)) {

									message.setDouble(38, quantity);

									blotterTexts.add(new BlotterText(quantityFormat.format(quantity)));

									Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(
											initiatorMDInputEntry.getSecurityValue(), initiatorMDInputEntry.getSecurityIDSource());

									if (security != null) {
										blotterTexts.add(new BlotterText(" " + security.getName()));
									}

									if (Math.random() > 0.5)
										message.setDouble(110, quantity);

									Calendar calendar = Calendar.getInstance();
									for (int j = 0; j < 3; j++) {
										calendar.add(Calendar.DAY_OF_YEAR, 1);
										while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
											calendar.add(Calendar.DAY_OF_YEAR, 1);
									}

									switch ((int) (Math.random() * 4)) {
										case 0:
											blotterTexts.add(new BlotterText(" good till cancel"));
											message.setChar(59, '1');
											break;
										case 1:
											message.setChar(59, '6');
											blotterTexts.add(new BlotterText(" good till " + displayDateFormat.format(calendar.getTime())));
											message.setString(432, localMarketDateFormat.format(calendar.getTime()));
											break;
										case 2:
											message.setChar(59, '0');
											blotterTexts
													.add(new BlotterText(" good till day (settlement " + displayDateFormat.format(calendar.getTime()) + ")"));
											message.setString(64, localMarketDateFormat.format(calendar.getTime()));
											break;
										default:
											message.setChar(59, '4');
											blotterTexts
													.add(new BlotterText(" fill or kill (settlement " + displayDateFormat.format(calendar.getTime()) + ")"));
											message.setString(64, localMarketDateFormat.format(calendar.getTime()));
											break;

									}

									message.setString(55, "N/A");
									message.setString(48, initiatorMDInputEntry.getSecurityValue());
									message.setString(22, initiatorMDInputEntry.getSecurityIDSource());

									final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
									group.setString(448, conf.toUpperCase());
									group.setChar(447, 'B');
									group.setInt(452, 13);
									message.addGroup(group);

									final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
									group2.setString(448, sessionID.getTargetCompID().substring(0, sessionID.getTargetCompID().length() - 1));
									group2.setChar(447, 'B');
									group2.setInt(452, 1);
									message.addGroup(group2);

									final Group group3 = new Group(453, 448, new int[] { 448, 447, 452 });
									group3.setString(448, traders.get((int) (Math.random() * (double) traders.size())));

									group3.setChar(447, 'D');
									group3.setInt(452, 11);
									message.addGroup(group3);
									openOrders.put(message.getString(11), message);

									if (sessionID != null) {
										Session session = Session.lookupSession(sessionID);
										if (session != null) {
											session.send((Message) message.clone());

											BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

											for (BlotterListener blotterListener : blotterListeners)
												blotterListener.writeBlotterEntry(blotterEntry);

										}
									}
								}
							}
						}
						catch (FieldNotFound e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized void handleLogout() {

	}

	private synchronized void handleLogon() {

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.onLogon(true);

		Message message = new Message();
		message.getHeader().setString(MsgType.FIELD, "AF");
		message.setString(584, idFormat.format(id++));
		message.setInt(585, 8);

		final Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
		group2.setString(448, sessionID.getSenderCompID().substring(0, sessionID.getSenderCompID().length() - 1));
		group2.setChar(447, 'B');
		group2.setInt(452, 13);
		message.addGroup(group2);

		if (sessionID != null) {
			Session session = Session.lookupSession(sessionID);
			if (session != null)
				session.send((Message) message.clone());
		}

		Message securityListRequest = new Message();
		securityListRequest.getHeader().setString(MsgType.FIELD, "x");
		String securityReqID = idFormat.format(id++);
		securityListRequest.setString(320, securityReqID);
		securityListRequest.setInt(559, 4);

		if (sessionID != null) {
			Session session = Session.lookupSession(sessionID);
			if (session != null)
				session.send(securityListRequest);
		}

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(Message message, SessionID sessionId) {

		try {
			String messageType = message.getHeader().getString(MsgType.FIELD);

			List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

			BlotterText blotterText = new BlotterText(name + " buy side send " + DictionaryParser.getMessageName(messageType) + " :");
			blotterTexts.add(blotterText);

			BlotterText blotterText2 = new BlotterText(message.toString(), Color.GRAY);
			blotterTexts.add(blotterText2);

			BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.MESSAGE);

			for (BlotterListener blotterListener : blotterListeners)
				blotterListener.writeBlotterEntry(blotterEntry);
		}
		catch (FieldNotFound e) {
			e.printStackTrace();
		}

		message.getHeader().setString(1128, "9");

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

		String messageType = message.getHeader().getString(MsgType.FIELD);

		List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

		BlotterText blotterText = new BlotterText(name + " buy side received " + DictionaryParser.getMessageName(messageType) + " :");
		blotterTexts.add(blotterText);

		BlotterText blotterText2 = new BlotterText(message.toString(), Color.GRAY);
		blotterTexts.add(blotterText2);

		BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.MESSAGE);

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.writeBlotterEntry(blotterEntry);

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		
		try {
			String messageType = message.getHeader().getString(MsgType.FIELD);

			List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

			BlotterText blotterText = new BlotterText(name + " buy side send " + DictionaryParser.getMessageName(messageType) + " :");
			blotterTexts.add(blotterText);

			BlotterText blotterText2 = new BlotterText(message.toString(), Color.GRAY);
			blotterTexts.add(blotterText2);

			BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.MESSAGE);

			for (BlotterListener blotterListener : blotterListeners)
				blotterListener.writeBlotterEntry(blotterEntry);
		}
		catch (FieldNotFound e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromApp(final Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
			
				handleFromApp((Message) message.clone());
				
			}
		});

	}

	private synchronized void handleFromApp(Message message) {

		try {
			Set<InitiatorMDInputEntry> mdInputEntries = new HashSet<InitiatorMDInputEntry>();

			String messageType = message.getHeader().getString(MsgType.FIELD);

			List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

			BlotterText blotterText = new BlotterText(name + " buy side received " + DictionaryParser.getMessageName(messageType) + " :");
			blotterTexts.add(blotterText);

			BlotterText blotterText2 = new BlotterText(message.toString(), Color.GRAY);
			blotterTexts.add(blotterText2);

			BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.MESSAGE);

			for (BlotterListener blotterListener : blotterListeners)
				blotterListener.writeBlotterEntry(blotterEntry);

			if (messageType.equals("W")) {
				if (message.isSetField(48)) {
					String securityID = message.getString(48);
					String securityIDSource = message.getString(22);
					InitiatorMDInputEntry mdInputEntry = securityEntries.get(securityID);
					if (mdInputEntry == null) {
						mdInputEntry = new InitiatorMDInputEntry();
						mdInputEntry.setSecurityValue(securityID);
						mdInputEntry.setSecurityIDSource(securityIDSource);
						securityEntries.put(securityID, mdInputEntry);
					}
					for (int i = 1; i <= message.getGroupCount(268); i++) {
						Group mdEntries = message.getGroup(i, 268);
						if (mdEntries.isSetField(269)) {
							String mdEntryType = mdEntries.getString(269);
							if (mdEntryType.equals("0") || mdEntryType.equals("1")) {
								boolean bid = false;
								if (mdEntryType.equals("0"))
									bid = true;
								if (mdEntries.isSetField(270)) {
									BigDecimal value = new BigDecimal(decimalFormat.format(mdEntries.getDouble(270)));
									BigDecimal tickSize = new BigDecimal("0.001");
									Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(securityID, securityIDSource);
									if (security != null && security.getSecurityDetails() != null
											&& security.getSecurityDetails().getMinPriceIncrement() != null) {
										tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
									}

									if (security != null)
										value = value.add(tickSize.multiply(new BigDecimal(spread)));

									if (bid) {
										
										if (security != null)
											value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);

										if (mdInputEntry.getHighBidPxValue() == null || mdInputEntry.getHighBidPxValue() < value.doubleValue())
											mdInputEntry.setHighBidPxValue(value.doubleValue());

										if (mdInputEntry.getLowBidPxValue() == null || mdInputEntry.getLowBidPxValue() > value.doubleValue())
											mdInputEntry.setLowBidPxValue(value.doubleValue());

										if (mdInputEntry.getMdEntryBidPxValue() == null)
											mdInputEntry.setMdBidPriceDeltaValue(0d);
										else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue())
											mdInputEntry.setMdBidPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryBidPxValue());

										mdInputEntry.setMdEntryBidPxValue(value.doubleValue());
									}
									else {

										if (security != null)
											value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);


										if (mdInputEntry.getHighAskPxValue() == null || mdInputEntry.getHighAskPxValue() < value.doubleValue())
											mdInputEntry.setHighAskPxValue(value.doubleValue());

										if (mdInputEntry.getLowAskPxValue() == null || mdInputEntry.getLowAskPxValue() > value.doubleValue())
											mdInputEntry.setLowAskPxValue(value.doubleValue());

										if (mdInputEntry.getMdEntryAskPxValue() == null)
											mdInputEntry.setMdAskPriceDeltaValue(0d);
										else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue())
											mdInputEntry.setMdAskPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryAskPxValue());

										mdInputEntry.setMdEntryAskPxValue(value.doubleValue());
									}
									mdInputEntries.add(mdInputEntry);
								}
								if (mdEntries.isSetField(271)) {
									BigDecimal size = new BigDecimal(decimalFormat.format(mdEntries.getDouble(271)));
									if (bid) {
										mdInputEntry.setMdEntryBidSizeValue((double) ((int) (size.multiply(sizeFactor).doubleValue())));
									}
									else
										mdInputEntry.setMdEntryAskSizeValue((double) ((int) (size.multiply(sizeFactor).doubleValue())));
									mdInputEntries.add(mdInputEntry);
								}

							}
						}
					}
				}
			}
			else if (messageType.equals("X")) {

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

						if (refMdEntry.isSetField(48)&&refMdEntry.isSetField(22)) {
							String securityID = refMdEntry.getString(48);
							String securityIDSource = refMdEntry.getString(22);
							InitiatorMDInputEntry mdInputEntry = securityEntries.get(securityID);
							if (mdInputEntry == null) {
								mdInputEntry = new InitiatorMDInputEntry();
								mdInputEntry.setSecurityValue(securityID);
								mdInputEntry.setSecurityIDSource(securityIDSource);
								securityEntries.put(securityID, mdInputEntry);
							}
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
										}
										else {
											mdInputEntry.setMdAskPriceDeltaValue(null);
											mdInputEntry.setMdEntryAskPxValue(null);
											mdInputEntry.setMdEntryAskSizeValue(null);

										}
										mdInputEntries.add(mdInputEntry);
									}
									else {
										if (mdEntries.isSetField(270)) {
											BigDecimal value = new BigDecimal(decimalFormat.format(mdEntries.getDouble(270)));
											
											BigDecimal tickSize = new BigDecimal("0.001");
											Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(securityID, securityIDSource);
											if (security != null && security.getSecurityDetails() != null
													&& security.getSecurityDetails().getMinPriceIncrement() != null) {
												tickSize = new BigDecimal(decimalFormat.format(security.getSecurityDetails().getMinPriceIncrement()));
											}

											if (security != null)
												value = value.add(tickSize.multiply(new BigDecimal(spread)));

											if (bid) {

												if (security != null)
												{
													
													value = value.divide(tickSize).setScale(0, RoundingMode.DOWN).multiply(tickSize);
												}
												
												if (mdInputEntry.getHighBidPxValue() == null || mdInputEntry.getHighBidPxValue() < value.doubleValue())
													mdInputEntry.setHighBidPxValue(value.doubleValue());

												if (mdInputEntry.getLowBidPxValue() == null || mdInputEntry.getLowBidPxValue() > value.doubleValue())
													mdInputEntry.setLowBidPxValue(value.doubleValue());

												if (mdInputEntry.getMdEntryBidPxValue() == null)
													mdInputEntry.setMdBidPriceDeltaValue(0d);
												else if (mdInputEntry.getMdEntryBidPxValue() != value.doubleValue())
													mdInputEntry.setMdBidPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryBidPxValue());

												mdInputEntry.setMdEntryBidPxValue(value.doubleValue());
											}
											else {

												if (security != null)
													value = value.divide(tickSize).setScale(0, RoundingMode.UP).multiply(tickSize);

												if (mdInputEntry.getHighAskPxValue() == null || mdInputEntry.getHighAskPxValue() < value.doubleValue())
													mdInputEntry.setHighAskPxValue(value.doubleValue());

												if (mdInputEntry.getLowAskPxValue() == null || mdInputEntry.getLowAskPxValue() > value.doubleValue())
													mdInputEntry.setLowAskPxValue(value.doubleValue());

												if (mdInputEntry.getMdEntryAskPxValue() == null)
													mdInputEntry.setMdAskPriceDeltaValue(0d);
												else if (mdInputEntry.getMdEntryAskPxValue() != value.doubleValue())
													mdInputEntry.setMdAskPriceDeltaValue(value.doubleValue() - mdInputEntry.getMdEntryAskPxValue());

												mdInputEntry.setMdEntryAskPxValue(value.doubleValue());
											}
											mdInputEntries.add(mdInputEntry);
										}
										if (mdEntries.isSetField(271)) {
											BigDecimal size = new BigDecimal(decimalFormat.format(mdEntries.getDouble(271)));
											if (bid) {
												mdInputEntry.setMdEntryBidSizeValue((double) ((int) (size.multiply(sizeFactor).doubleValue())));
											}
											else
												mdInputEntry.setMdEntryAskSizeValue((double) ((int) (size.multiply(sizeFactor).doubleValue())));
											mdInputEntries.add(mdInputEntry);
										}
									}
								}
							}
						}
					}
				}
			}
			else if (messageType.equals("S")) {

				if (message.isSetField(131)) {

					boolean counter = false;

					if (openQuoteRequests.remove(message.getString(131)) == null) {
						for (Entry<String, Message> entry : openOrders.entrySet()) {
							Message message2 = entry.getValue();

							if (message2.isSetField(693) && message2.getString(693).equals(message.getString(693))) {
								openOrders.remove(entry.getKey());
								counter = true;
							}
						}
					}

					List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();

					if (counter)
						blotterTexts2.add(new BlotterText(name + " received counter quote from  Bank of Midas for inquiry "));
					else
						blotterTexts2.add(new BlotterText(name + " received quote from  Bank of Midas for inquiry "));

					if (message.getChar(54) == '1')
						blotterTexts2.add(new BlotterText("ask ", red));
					else
						blotterTexts2.add(new BlotterText("bid ", green));
					blotterTexts2.add(new BlotterText(quantityFormat.format(message.getDouble(38))));
					Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));
					if (security != null) {
						blotterTexts2.add(new BlotterText(" " + security.getName()));
					}

					blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));

					BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

					for (BlotterListener blotterListener : blotterListeners)
						blotterListener.writeBlotterEntry(blotterEntry2);

					openQuotes.put(message.getString(131), message);

				}
			}
			else if (messageType.equals("AI")) {

				if (message.isSetField(297) && (message.getInt(297) == 11 || message.getInt(297) == 7)) {

					if (openQuoteRequests.remove(message.getString(131)) == null && openQuotes.remove(message.getString(131)) == null) {
						for (Entry<String, Message> entry : openOrders.entrySet()) {
							Message message2 = entry.getValue();

							if (message2.isSetField(693) && message2.getString(693).equals(message.getString(693))) {
								openOrders.remove(entry.getKey());
							}
						}
					}

					List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();

					if (message.getInt(297) == 11)
						blotterTexts2.add(new BlotterText(name + " received pass from  Bank of Midas for inquiry "));
					if (message.getInt(297) == 7)
						blotterTexts2.add(new BlotterText(name + " received expiration from  Bank of Midas for inquiry "));

					if (message.getChar(54) == '1')
						blotterTexts2.add(new BlotterText("ask ", red));
					else
						blotterTexts2.add(new BlotterText("bid ", green));
					blotterTexts2.add(new BlotterText(quantityFormat.format(message.getDouble(38))));
					Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));
					if (security != null) {
						blotterTexts2.add(new BlotterText(" " + security.getName()));
					}

					blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));

					BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

					for (BlotterListener blotterListener : blotterListeners)
						blotterListener.writeBlotterEntry(blotterEntry2);

				}
			}
			else if (messageType.equals("AG")) {

				if (message.isSetField(131)) {
					
					openQuoteRequests.remove(message.getString(131));
					openQuotes.remove(message.getString(131));

					for (int i = 1; i <= message.getGroupCount(146); i++) {

						Group noRelatedSym = message.getGroup(i, 146);

						List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();
						blotterTexts2.add(new BlotterText(name + " received quote request reject from  Bank of Midas for inquiry "));
						if (noRelatedSym.getChar(54) == '1')
							blotterTexts2.add(new BlotterText("ask ", red));
						else
							blotterTexts2.add(new BlotterText("bid ", green));
						blotterTexts2.add(new BlotterText(quantityFormat.format(noRelatedSym.getDouble(38))));
						Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(noRelatedSym.getString(48),
								noRelatedSym.getString(22));
						if (security != null) {
							blotterTexts2.add(new BlotterText(" " + security.getName()));
						}

						blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(noRelatedSym.getString(64)))
								+ ")"));

						BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

						for (BlotterListener blotterListener : blotterListeners)
							blotterListener.writeBlotterEntry(blotterEntry2);

					}
				}
			}
			else if (messageType.equals("8")) {

				boolean inquiry = false;

				if (message.isSetField(11)) {

					if (message.isSetField(151) && message.getDouble(151) == 0) {

						if (openOrders.remove(message.getString(11)) == null) {
							for (Entry<String, Message> entry : openQuotes.entrySet()) {
								Message message2 = entry.getValue();
								if (message2.isSetField(693) && message2.getString(693).equals(message.getString(693))) {
									openQuotes.remove(entry.getKey());
									inquiry = true;
								}
							}

							for (Entry<String, Message> entry : openOrders.entrySet()) {
								Message message2 = entry.getValue();

								if (message2.isSetField(693) && message2.getString(693).equals(message.getString(693))) {
									openOrders.remove(entry.getKey());
									inquiry = true;
								}
							}
						}
					}
					else {

						openOrders.put(message.getString(11), message);
					}

					List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();
					blotterTexts2.add(new BlotterText(name + " received "));
					if (message.isSetField(39) && message.isSetField(150)) {
						if (!message.getString(150).equals("I")) {

							Security security = null;

							Double lastPrice = null;

							Double lastYield = null;

							Double lastQuantity = null;

							Double cumulativeQuantity = null;

							Double leavesQuantity = null;

							if (message.isSetField(48) && message.isSetField(22))
								security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));

							if (message.getString(39).equals("1")) {
								blotterTexts2.add(new BlotterText("partial fill "));

								if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

									lastPrice = message.getDouble(669);
									lastYield = message.getDouble(31);
								}
								else
									lastPrice = message.getDouble(31);

								lastQuantity = message.getDouble(32);
								cumulativeQuantity = message.getDouble(14);
								leavesQuantity = message.getDouble(151);
							}
							else if (message.getString(39).equals("2")) {
								blotterTexts2.add(new BlotterText("fill "));

								if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

									lastPrice = message.getDouble(669);
									lastYield = message.getDouble(31);
								}
								else
									lastPrice = message.getDouble(31);

								lastQuantity = message.getDouble(32);
								cumulativeQuantity = message.getDouble(14);
								leavesQuantity = message.getDouble(151);

							}
							else if (message.getString(39).equals("3"))
								blotterTexts2.add(new BlotterText("done for day "));
							else if (message.getString(39).equals("4"))
								blotterTexts2.add(new BlotterText("cancel "));
							else if (message.getString(39).equals("8"))
								blotterTexts2.add(new BlotterText("reject "));
							else
								return;

							if (inquiry)
								blotterTexts2.add(new BlotterText("for inquiry "));
							else
								blotterTexts2.add(new BlotterText("for order "));
							if (message.isSetField(54)) {
								if (message.getString(54).equals("1")) {
									blotterTexts2.add(new BlotterText("buy ", green));
								}
								else {
									blotterTexts2.add(new BlotterText("sell ", red));
								}
							}
							if (message.isSetField(38)) {
								blotterTexts2.add(new BlotterText(quantityFormat.format(message.getDouble(38))));
							}
							if (security != null)
								blotterTexts2.add(new BlotterText(" " + security.getName()));
							if (message.isSetField(64)) {

								Date settlementDate = localMarketDateFormat.parse(message.getString(64));
								blotterTexts2.add(new BlotterText(" settlement date " + displayDateFormat.format(settlementDate)));
							}
							if (lastPrice != null) {
								blotterTexts2.add(new BlotterText(" last price " + decimalFormat.format(lastPrice)));
							}
							if (lastYield != null) {
								blotterTexts2.add(new BlotterText(" last yield " + decimalFormat.format(lastYield)));
							}
							if (lastQuantity != null) {
								blotterTexts2.add(new BlotterText(" last quantity " + decimalFormat.format(lastQuantity)));
							}

							if (cumulativeQuantity != null) {
								blotterTexts2.add(new BlotterText(" cumulative quantity " + decimalFormat.format(cumulativeQuantity)));
							}

							if (leavesQuantity != null) {
								blotterTexts2.add(new BlotterText(" leaves quantity " + decimalFormat.format(leavesQuantity)));
							}

							BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

							for (BlotterListener blotterListener : blotterListeners)
								blotterListener.writeBlotterEntry(blotterEntry2);
						}
					}
				}
			}
			else if (messageType.equals("y")) {

				List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();

				if (message.getGroupCount(146) == 1) {
					BlotterText blotterText3 = new BlotterText(name + " buy side added tradeable security ");
					blotterTexts2.add(blotterText3);
				}
				else {
					BlotterText blotterText3 = new BlotterText(name + " buy side added tradeable securities ");
					blotterTexts2.add(blotterText3);
				}

				for (int i = 1; i <= message.getGroupCount(146); i++) {

					final Group relatedSym = message.getGroup(i, 146);
					Security security = SecurityDictionary.getInstance().getRelatedSym2(relatedSym);

					if (security != null) {

						SecurityDictionary.getInstance().addSecurity(security);

						if (i == 1) {
							BlotterText blotterText3 = new BlotterText(security.getName());
							blotterTexts2.add(blotterText3);
						}
						else {
							BlotterText blotterText3 = new BlotterText(" ," + security.getName());
							blotterTexts2.add(blotterText3);
						}

						BlotterText blotterText4 = new BlotterText(" " + security.getSecurityID(), Color.GRAY);
						blotterTexts2.add(blotterText4);

					}

				}

				if (blotterTexts2.size() > 1) {
					BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

					for (BlotterListener blotterListener : blotterListeners)
						blotterListener.writeBlotterEntry(blotterEntry2);
				}
			}

			if (mdInputEntries.size() > 0) {

				send(mdInputEntries);

			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void send(Collection<InitiatorMDInputEntry> mdInputEntries) throws Exception {

		for (AgoraAcceptor agoraAcceptor : agoraAcceptors) {
			Message message2 = new Message();
			message2.getHeader().setString(MsgType.FIELD, "X");

			for (InitiatorMDInputEntry initiatorMDInputEntry : mdInputEntries) {
				{

					final Group group = new Group(268, 279, new int[] { 279, 269, 278, 280, 55, 48, 22, 270, 271, 272, 273 });
					group.setChar(269, '0');

					Map<Character, String> entryMap = refIdMap.get(initiatorMDInputEntry.getSecurityValue());
					if (entryMap == null) {
						entryMap = new HashMap<Character, String>();
						refIdMap.put(initiatorMDInputEntry.getSecurityValue(), entryMap);
					}

					Map<Character, String> valueMap = lastValueMap.get(initiatorMDInputEntry.getSecurityValue());
					if (valueMap == null) {
						valueMap = new HashMap<Character, String>();
						lastValueMap.put(initiatorMDInputEntry.getSecurityValue(), valueMap);
					}

					String oldValue = valueMap.get('0');

					String refId = entryMap.get('0');
					String entryId = idFormat.format(id++);

					Double size = initiatorMDInputEntry.getMdEntryBidSizeValue();
					Double price = initiatorMDInputEntry.getMdEntryBidPxValue();
					group.setString(278, entryId);

					if (price == null || size == null) {
						if (refId != null) {
							group.setString(279, "2");
							group.setString(280, refId);
							message2.addGroup(group);
							valueMap.remove('0');
							entryMap.remove('0');
						}

					}
					else {
						if (refId == null) {

							group.setString(55, "N/A");
							group.setString(48, initiatorMDInputEntry.getSecurityValue());
							group.setString(22, "8");
							group.setString(279, "0");

							Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
							group2.setString(448, conf.toUpperCase());
							group2.setChar(447, 'B');
							group2.setInt(452, 26);

							group.addGroup(group2);

							entryMap.put('0', entryId);

						}
						else {
							group.setString(279, "1");
							group.setString(280, refId);
						}

						group.setDouble(270, price);

						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
						group.setUtcDateOnly(272, calendar.getTime());
						group.setUtcTimeOnly(273, calendar.getTime());

						group.setDouble(271, size);
						StringBuffer newValue = new StringBuffer();
						if (group.isSetField(270))
							newValue.append(group.getString(270));
						newValue.append(";");
						if (group.isSetField(271))
							newValue.append(group.getString(271));

						if (oldValue == null || !newValue.toString().equals(oldValue)) {
							message2.addGroup(group);
							valueMap.put('0', newValue.toString());
						}
					}
				}
				{
					final Group group = new Group(268, 279, new int[] { 279, 269, 278, 280, 55, 48, 22, 270, 271, 272, 273 });
					group.setChar(269, '1');

					Map<Character, String> entryMap = refIdMap.get(initiatorMDInputEntry.getSecurityValue());
					if (entryMap == null) {
						entryMap = new HashMap<Character, String>();
						refIdMap.put(initiatorMDInputEntry.getSecurityValue(), entryMap);
					}

					Map<Character, String> valueMap = lastValueMap.get(initiatorMDInputEntry.getSecurityValue());
					if (valueMap == null) {
						valueMap = new HashMap<Character, String>();
						lastValueMap.put(initiatorMDInputEntry.getSecurityValue(), valueMap);
					}

					String oldValue = valueMap.get('1');

					String refId = entryMap.get('1');
					String entryId = idFormat.format(id++);

					Double size = initiatorMDInputEntry.getMdEntryAskSizeValue();
					Double price = initiatorMDInputEntry.getMdEntryAskPxValue();
					group.setString(278, entryId);

					if (price == null || size == null) {
						if (refId != null) {
							group.setString(279, "2");
							group.setString(280, refId);
							message2.addGroup(group);
							valueMap.remove('1');
							entryMap.remove('1');
						}

					}
					else {

						if (refId == null) {

							group.setString(55, "N/A");
							group.setString(48, initiatorMDInputEntry.getSecurityValue());
							group.setString(22, "8");
							group.setString(279, "0");

							Group group2 = new Group(453, 448, new int[] { 448, 447, 452 });
							group2.setString(448, conf.toUpperCase());
							group2.setChar(447, 'B');
							group2.setInt(452, 26);

							group.addGroup(group2);

							entryMap.put('1', entryId);

						}
						else {
							group.setString(279, "1");
							group.setString(280, refId);
						}

						group.setDouble(270, price);

						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
						group.setUtcDateOnly(272, calendar.getTime());
						group.setUtcTimeOnly(273, calendar.getTime());

						group.setDouble(271, size);
						StringBuffer newValue = new StringBuffer();
						if (group.isSetField(270))
							newValue.append(group.getString(270));
						newValue.append(";");
						if (group.isSetField(271))
							newValue.append(group.getString(271));
						if (oldValue == null || !newValue.toString().equals(oldValue)) {
							message2.addGroup(group);
							valueMap.put('1', newValue.toString());
						}
					}
				}
			}

			if (message2.getGroupCount(268) > 0) {
				agoraAcceptor.send(message2);
			}
		}

	}

	/**
	 * Adds the agora acceptor.
	 *
	 * @param agoraAcceptor the agora acceptor
	 */
	public synchronized void addAgoraAcceptor(AgoraAcceptor agoraAcceptor) {

		agoraAcceptors.add(agoraAcceptor);

	}

	/**
	 * Removes the agora acceptor.
	 *
	 * @param agoraAcceptor the agora acceptor
	 */
	public synchronized void removeAgoraAcceptor(AgoraAcceptor agoraAcceptor) {

		agoraAcceptors.remove(agoraAcceptor);

	}

	/**
	 * On acceptor logon.
	 */
	public synchronized void onAcceptorLogon() {

		refIdMap.clear();
		lastValueMap.clear();

		try {

			Collection<InitiatorMDInputEntry> mdInputEntries = new HashSet<InitiatorMDInputEntry>();
			mdInputEntries.addAll(securityEntries.values());
			send(mdInputEntries);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the bid px.
	 *
	 * @param string the string
	 * @return the bid px
	 */
	public Double getBidPx(String string) {

		InitiatorMDInputEntry initiatorMDInputEntry = securityEntries.get(string);
		if (initiatorMDInputEntry != null)
			return initiatorMDInputEntry.getMdEntryBidPxValue();
		return null;
	}

	/**
	 * Gets the ask px.
	 *
	 * @param string the string
	 * @return the ask px
	 */
	public Double getAskPx(String string) {

		InitiatorMDInputEntry initiatorMDInputEntry = securityEntries.get(string);
		if (initiatorMDInputEntry != null)
			return initiatorMDInputEntry.getMdEntryBidPxValue();
		return null;
	}

}
