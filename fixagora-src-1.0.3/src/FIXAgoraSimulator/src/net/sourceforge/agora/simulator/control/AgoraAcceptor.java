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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

/**
 * The Class AgoraAcceptor.
 */
public class AgoraAcceptor implements Application {

	private String conf = null;
	private List<String> traders = null;
	private SocketAcceptor acceptor = null;
	private Vector<BlotterListener> blotterListeners = new Vector<BlotterListener>();
	private SessionID sessionID = null;
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private Vector<AgoraInitiator> agoraInitiators = new Vector<AgoraInitiator>();
	private Map<String, Message> executionReports = new HashMap<String, Message>();
	private long execid = System.currentTimeMillis();
	private long id = System.currentTimeMillis();
	private long quoteid = System.currentTimeMillis();
	private DecimalFormat idFormat = new DecimalFormat("######");
	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private DecimalFormat quantityFormat = new DecimalFormat("#,##0.##############");
	private String name;
	private Color green = new Color(51, 153, 0);
	private Color red = new Color(204, 0, 0);
	private Map<String, Message> quoteRequests = new HashMap<String, Message>();
	private Map<String, Message> quoteResponses = new HashMap<String, Message>();

	/**
	 * Instantiates a new agora acceptor.
	 *
	 * @param conf the conf
	 * @param traders the traders
	 */
	public AgoraAcceptor(String conf, List<String> traders) {

		super();
		this.conf = "./conf/" + conf + "_acceptor.conf";
		this.traders = traders;

		try {
			final InputStream inputStream = new FileInputStream(this.conf);

			Properties properties = new Properties();

			BufferedInputStream stream = new BufferedInputStream(inputStream);
			properties.load(stream);
			stream.close();

			name = properties.getProperty("name");
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
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

	/**
	 * Connect.
	 */
	public void connect() {

		try {

			final InputStream inputStream = new FileInputStream(conf);
			final SessionSettings settings = new SessionSettings(inputStream);
			final MessageStoreFactory storeFactory = new FileStoreFactory(settings);
			final LogFactory logFactory = new EmptyLogFactory();
			final MessageFactory messageFactory = new DefaultMessageFactory();

			acceptor = new SocketAcceptor(new DefaultSessionFactory(this, storeFactory, logFactory, messageFactory), settings);

			acceptor.start();

			Thread thread = new Thread() {

				@Override
				public void run() {

					while (true) {

						try {

							sleep(1000);
							handleExecutionReports();
							handleQuoteRequests();
						}
						catch (InterruptedException e) {

						}
					}
				}

			};
			thread.start();
		}
		catch (final Exception e) {
		}

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

		List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

		BlotterText blotterText = new BlotterText(name + " sell side is connected to FIX Agora Server");
		blotterTexts.add(blotterText);

		BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.writeBlotterEntry(blotterEntry);

		executorService.execute(new Runnable() {

			@Override
			public void run() {

				for (AgoraInitiator agoraInitiator : agoraInitiators)
					agoraInitiator.onAcceptorLogon();

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

		BlotterText blotterText = new BlotterText(name + " sell side is disconnected from FIX Agora Server");
		blotterTexts.add(blotterText);

		BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.WARNING);

		for (BlotterListener blotterListener : blotterListeners)
			blotterListener.writeBlotterEntry(blotterEntry);

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(Message message, SessionID sessionId) {

		try {
			String messageType = message.getHeader().getString(MsgType.FIELD);

			List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

			BlotterText blotterText = new BlotterText(name + " sell side send " + DictionaryParser.getMessageName(messageType) + " :");
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

		// dirty
		try {
			String messageType = message.getHeader().getString(MsgType.FIELD);
			if (messageType.equals("4"))
				executorService.execute(new Runnable() {

					@Override
					public void run() {

						for (AgoraInitiator agoraInitiator : agoraInitiators)
							agoraInitiator.onAcceptorLogon();

					}
				});
		}
		catch (FieldNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

		String messageType = message.getHeader().getString(MsgType.FIELD);

		List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

		BlotterText blotterText = new BlotterText(name + " sell side received " + DictionaryParser.getMessageName(messageType) + " :");
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

			BlotterText blotterText = new BlotterText(name + " sell side send " + DictionaryParser.getMessageName(messageType) + " :");
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

	private synchronized void handleFromApp(final Message message) {

		try {
			String messageType = message.getHeader().getString(MsgType.FIELD);

			List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

			BlotterText blotterText = new BlotterText(name + " sell side received " + DictionaryParser.getMessageName(messageType) + " :");
			blotterTexts.add(blotterText);

			blotterTexts.add(new BlotterText(message.toString(), Color.GRAY));

			BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.MESSAGE);

			for (BlotterListener blotterListener : blotterListeners)
				blotterListener.writeBlotterEntry(blotterEntry);

			if (messageType.equals("D")) {

				List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();
				blotterTexts2.add(new BlotterText(name + " received new order Bank of Midas "));
				if (message.getChar(54) == '1')
					blotterTexts2.add(new BlotterText("asks ", red));
				else
					blotterTexts2.add(new BlotterText("bids ", green));
				blotterTexts2.add(new BlotterText(quantityFormat.format(message.getDouble(38))));
				Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));
				if (security != null) {
					blotterTexts2.add(new BlotterText(" " + security.getName()));
				}
				switch (message.getChar(59)) {
					case '0':
						blotterTexts2.add(new BlotterText(" good till day"));
						if (message.isSetField(64)) {
							blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(64)))
									+ ")"));
						}
						break;
					case '1':
						blotterTexts2.add(new BlotterText(" good till cancel"));
						break;
					case '4':
						blotterTexts2.add(new BlotterText(" fill or kill"));
						if (message.isSetField(64)) {
							blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(64)))
									+ ")"));
						}
						break;
					case '6':
						blotterTexts2.add(new BlotterText(" good till " + displayDateFormat.format(localMarketDateFormat.parse(message.getString(432)))));
						break;

				}
				BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

				for (BlotterListener blotterListener : blotterListeners)
					blotterListener.writeBlotterEntry(blotterEntry2);

				Message executionReport = new Message();
				executionReport.getHeader().setString(MsgType.FIELD, "8");
				executionReport.setString(37, idFormat.format(id++));
				executionReport.setString(17, idFormat.format(execid++));
				executionReport.setString(11, message.getString(11));
				executionReport.setChar(150, '0');
				executionReport.setChar(39, '0');
				if (message.isSetField(59))
					executionReport.setString(59, message.getString(59));
				if (message.isSetField(432))
					executionReport.setString(432, message.getString(432));
				if (message.isSetField(64))
					executionReport.setString(64, message.getString(64));
				executionReport.setChar(54, message.getChar(54));
				executionReport.setDouble(14, 0);
				executionReport.setDouble(151, message.getDouble(38));
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
						e.printStackTrace();
					}
				}
				final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
				group.setString(448, traders.get((int) (Math.random() * (double) traders.size())));
				group.setChar(447, 'D');
				group.setInt(452, 12);
				executionReport.addGroup(group);
				executionReport.setString(55, "N/A");
				executionReport.setString(48, message.getString(48));
				executionReport.setString(22, message.getString(22));
				if (message.isSetField(64)) {
					executionReport.setString(64, message.getString(64));
				}
				else {
					Calendar calendar = Calendar.getInstance();
					for (int j = 0; j < 3; j++) {
						calendar.add(Calendar.DAY_OF_YEAR, 1);
						while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
							calendar.add(Calendar.DAY_OF_YEAR, 1);
					}
					executionReport.setString(64, localMarketDateFormat.format(calendar.getTime()));
				}
				send(executionReport);
				executionReports.put(message.getString(11), executionReport);
			}
			if (messageType.equals("R")) {

				for (int i = 1; i <= message.getGroupCount(146); i++) {

					Group noRelatedSym = message.getGroup(i, 146);

					List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();
					blotterTexts2.add(new BlotterText(name + " received quote requests Bank of Midas "));
					if (noRelatedSym.getChar(54) == '1')
						blotterTexts2.add(new BlotterText("asks ", red));
					else
						blotterTexts2.add(new BlotterText("bids ", green));
					blotterTexts2.add(new BlotterText(quantityFormat.format(noRelatedSym.getDouble(38))));
					Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(noRelatedSym.getString(48), noRelatedSym.getString(22));
					if (security != null) {
						blotterTexts2.add(new BlotterText(" " + security.getName()));
					}

					blotterTexts2.add(new BlotterText(" (settlement " + displayDateFormat.format(localMarketDateFormat.parse(noRelatedSym.getString(64))) + ")"));

					BlotterEntry blotterEntry2 = new BlotterEntry(name, blotterTexts2, LevelIcon.INFO);

					for (BlotterListener blotterListener : blotterListeners)
						blotterListener.writeBlotterEntry(blotterEntry2);

				}

				quoteRequests.put(message.getString(131), message);
			}
			if (messageType.equals("AJ")) {
				List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();

				Message message2 = quoteResponses.remove(message.getString(117));

				switch (message.getInt(694)) {
					case 5:
						blotterTexts2.add(new BlotterText(name + " received done away for inquiry Bank of Midas "));
						break;
					case 4:
						blotterTexts2.add(new BlotterText(name + " received cover for inquiry  Bank of Midas "));
						break;
					case 3:
						blotterTexts2.add(new BlotterText(name + " received expiration for inquiry  Bank of Midas "));
						break;
					case 2:
					case 1:
						if (message2 != null) {
							long validUntil = message2.getUtcTimeStamp(62).getTime();
							if ((message.getInt(694) == 1 && validUntil >= System.currentTimeMillis()) || Math.random() > 0.6) {

								Message executionReport = new Message();
								executionReport.getHeader().setString(MsgType.FIELD, "8");
								executionReport.setString(37, idFormat.format(id++));
								executionReport.setString(17, idFormat.format(execid++));
								executionReport.setString(11, message.getString(11));
								executionReport.setString(693, message.getString(693));
								executionReport.setChar(150, 'F');
								executionReport.setChar(39, '2');
								if (message.isSetField(432))
									executionReport.setString(432, message.getString(432));
								if (message.isSetField(64))
									executionReport.setString(64, message.getString(64));
								executionReport.setChar(54, message.getChar(54));
								executionReport.setDouble(151, 0);

								if (message.getChar(54) == '1') {
									executionReport.setDouble(44, message.getDouble(133));
									executionReport.setDouble(14, message.getDouble(135));
									executionReport.setDouble(38, message.getDouble(135));
									executionReport.setDouble(32, message.getDouble(135));
									if (message.getInt(694) == 1)
										blotterTexts2.add(new BlotterText(name + " received lift for inquiry  Bank of Midas "));
									else
										blotterTexts2.add(new BlotterText(name + " received counter for inquiry  Bank of Midas "));
								}
								else {
									executionReport.setDouble(44, message.getDouble(132));
									executionReport.setDouble(14, message.getDouble(134));
									executionReport.setDouble(38, message.getDouble(134));
									executionReport.setDouble(32, message.getDouble(134));
									if (message.getInt(694) == 1)
										blotterTexts2.add(new BlotterText(name + " received hit for inquiry  Bank of Midas "));
									else
										blotterTexts2.add(new BlotterText(name + " received counter for inquiry  Bank of Midas "));
								}

								if (message.isSetField(44))

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
										e.printStackTrace();
									}
								}
								final Group group = new Group(453, 448, new int[] { 448, 447, 452 });
								group.setString(448, traders.get((int) (Math.random() * (double) traders.size())));
								group.setChar(447, 'D');
								group.setInt(452, 12);
								executionReport.addGroup(group);
								executionReport.setString(55, "N/A");
								executionReport.setString(48, message.getString(48));
								executionReport.setString(22, message.getString(22));
								if (message.isSetField(64)) {
									executionReport.setString(64, message.getString(64));
								}
								else {
									Calendar calendar = Calendar.getInstance();
									for (int j = 0; j < 3; j++) {
										calendar.add(Calendar.DAY_OF_YEAR, 1);
										while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
											calendar.add(Calendar.DAY_OF_YEAR, 1);
									}
									executionReport.setString(64, localMarketDateFormat.format(calendar.getTime()));
								}
								Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48),
										message.getString(22));
								if (security != null && message.isSetField(64) && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

									try {
										java.util.Date settlementDate = localMarketDateFormat.parse(message.getString(64));
										Double yield = executionReport.getDouble(44);
										Double price = YieldCalculator.getInstance().getPrice(security, yield, settlementDate, 1);
										if (price != null)
											executionReport.setDouble(669, price);
									}
									catch (ParseException e) {
										e.printStackTrace();
									}

								}

								executionReport.setDouble(31, executionReport.getDouble(44));

								send(executionReport);

							}
							else {

								Security security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message2.getString(48),
										message2.getString(22));

								Double bidPx = null;
								Double askPx = null;

								for (AgoraInitiator agoraInitiator : agoraInitiators) {
									bidPx = agoraInitiator.getBidPx(message.getString(48));
									askPx = agoraInitiator.getAskPx(message.getString(48));
								}

								long validUntilTime = System.currentTimeMillis();
								validUntilTime = validUntilTime + 30000;

								if (Math.random() > 0.5 && ((message.getChar(54) == '1' && askPx != null) || (message.getChar(54) == '2' && bidPx != null))) {
									Message quote = (Message) message2.clone();

									quote.getHeader().setString(MsgType.FIELD, "S");
									quote.setString(117, idFormat.format(quoteid++));
									quote.setInt(537, 3);
									quote.setInt(301, 0);
									quote.setUtcTimeStamp(62, new Date(validUntilTime));

									if (sessionID != null) {

										if (message.getChar(54) == '1') {
											quote.setDouble(133, askPx);
											if (message.getInt(694) == 1)
												blotterTexts2.add(new BlotterText(name + " countered off wire lift for inquiry Bank of Midas "));
											else
												blotterTexts2.add(new BlotterText(name + " countered counter for inquiry Bank of Midas "));
										}
										else {
											quote.setDouble(132, bidPx);
											if (message.getInt(694) == 1)
												blotterTexts2.add(new BlotterText(name + " countered off wire hit for inquiry Bank of Midas "));
											else
												blotterTexts2.add(new BlotterText(name + " countered counter for inquiry Bank of Midas "));

										}
										if (security != null) {
											blotterTexts.add(new BlotterText(security.getName()));
										}

										blotterTexts.add(new BlotterText(" (settlement "
												+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));

										quoteResponses.put(quote.getString(117), quote);

										send((Message) quote.clone());

									}

								}
								else {
									Message quoteStatusReport = (Message) message2.clone();
									quoteStatusReport.getHeader().setString(MsgType.FIELD, "AI");
									quoteStatusReport.removeField(301);
									quoteStatusReport.setInt(297, 11);
									if (message.getChar(54) == '1') {
										if (message.getInt(694) == 1)
											blotterTexts2.add(new BlotterText(name + " passed off wire lift for inquiry Bank of Midas "));
										else
											blotterTexts2.add(new BlotterText(name + " passed counter for inquiry Bank of Midas "));
									}
									else {
										if (message.getInt(694) == 1)
											blotterTexts2.add(new BlotterText(name + " passed off wire hit for inquiry Bank of Midas "));
										else
											blotterTexts2.add(new BlotterText(name + " passed counter for inquiry Bank of Midas "));
									}
									send(quoteStatusReport);
								}

							}
						}
						break;
					case 6:
						blotterTexts2.add(new BlotterText(name + " received pass for inquiry  Bank of Midas "));

						break;
					default:
						return;
				}
				if (message.getChar(54) == '1')
					blotterTexts2.add(new BlotterText("asks ", red));
				else
					blotterTexts2.add(new BlotterText("bids ", green));

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
			if (messageType.equals("G")) {
				Message lastExecutionReport = executionReports.remove(message.getString(41));
				if (lastExecutionReport != null) {
					Message executionReport = new Message();
					executionReport.getHeader().setString(MsgType.FIELD, "8");
					executionReport.setString(37, idFormat.format(id++));
					executionReport.setString(17, idFormat.format(execid++));
					executionReport.setString(11, message.getString(11));
					executionReport.setString(41, message.getString(41));
					executionReports.put(message.getString(11), executionReport);
					executionReport.setChar(150, '5');
					executionReport.setChar(39, lastExecutionReport.getChar(39));
					executionReport.setChar(54, message.getChar(54));
					executionReport.setDouble(14, lastExecutionReport.getDouble(14));
					executionReport.setDouble(38, message.getDouble(38));
					executionReport.setDouble(151, message.getDouble(38) - lastExecutionReport.getDouble(14));
					executionReport.setString(37, idFormat.format(id++));
					executionReport.setString(17, idFormat.format(execid++));
					if (message.isSetField(44))
						executionReport.setDouble(44, message.getDouble(44));
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
							e.printStackTrace();
						}
					}
					executionReport.setString(55, "N/A");
					executionReport.setString(48, message.getString(48));
					executionReport.setString(22, message.getString(22));
					send(executionReport);
				}
			}
			if (messageType.equals("F")) {
				Message executionReport = new Message();
				executionReport.getHeader().setString(MsgType.FIELD, "8");
				Message lastExecutionReport = executionReports.remove(message.getString(11));
				if (lastExecutionReport != null)
					executionReport.setString(37, lastExecutionReport.getString(37));
				else
					executionReport.setString(37, idFormat.format(id++));
				executionReport.setString(17, idFormat.format(execid++));
				executionReport.setString(11, message.getString(11));
				executionReport.setChar(150, '4');
				executionReport.setChar(39, '4');
				executionReport.setChar(54, message.getChar(54));
				executionReport.setDouble(14, 0);
				executionReport.setDouble(151, 0);
				executionReport.setString(37, idFormat.format(id++));
				executionReport.setString(17, idFormat.format(execid++));
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
						e.printStackTrace();
					}
				}
				executionReport.setString(55, "N/A");
				executionReport.setString(48, message.getString(48));
				executionReport.setString(22, message.getString(22));
				send(executionReport);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private synchronized void handleQuoteRequests() {

		Set<Message> messages = new HashSet<Message>();
		messages.addAll(quoteRequests.values());
		for (Message message : messages) {
			try {
				if (message.getHeader().isSetField(52) && System.currentTimeMillis() - message.getHeader().getUtcTimeStamp(52).getTime() > 10000) {
					if (message.getHeader().getString(35).equals("R")) {

						for (int i = 1; i <= message.getGroupCount(146); i++) {

							Group noRelatedSym = message.getGroup(i, 146);

							Security security = null;

							List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

							blotterTexts.add(new BlotterText(name));

							if (noRelatedSym.isSetField(48) && noRelatedSym.isSetField(22)) {

								security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(noRelatedSym.getString(48),
										noRelatedSym.getString(22));

								Double bidPx = null;
								Double askPx = null;

								for (AgoraInitiator agoraInitiator : agoraInitiators) {
									bidPx = agoraInitiator.getBidPx(noRelatedSym.getString(48));
									askPx = agoraInitiator.getAskPx(noRelatedSym.getString(48));
								}

								Thread.sleep((long) (5000 * Math.random()));

								long validUntilTime = System.currentTimeMillis();
								validUntilTime = validUntilTime + 30000;

								long expiretime = noRelatedSym.getUtcTimeStamp(126).getTime();

								quoteRequests.remove(message.getString(131));

								if (expiretime > validUntilTime && Math.random() > 0.15
										&& ((noRelatedSym.getChar(54) == '1' && askPx != null) || (noRelatedSym.getChar(54) == '2' && bidPx != null))) {
									Message quote = new Message();

									quote.getHeader().setString(MsgType.FIELD, "S");
									quote.setString(131, message.getString(131));
									quote.setString(55, noRelatedSym.getString(55));
									quote.setString(48, noRelatedSym.getString(48));
									quote.setString(22, noRelatedSym.getString(22));
									quote.setString(54, noRelatedSym.getString(54));
									quote.setDouble(38, noRelatedSym.getDouble(38));
									quote.setString(64, noRelatedSym.getString(64));
									quote.setString(60, noRelatedSym.getString(60));
									if (noRelatedSym.isSetField(423)) {
										quote.setString(423, noRelatedSym.getString(423));
									}
									for (int j = 1; j <= message.getGroupCount(453); j++) {
										quote.addGroup(message.getGroup(j, 453));
									}
									quote.setString(117, idFormat.format(quoteid++));
									quote.setInt(537, 1);
									quote.setInt(301, 0);
									quote.setUtcTimeStamp(62, new Date(validUntilTime));

									int quantity = (int) noRelatedSym.getDouble(38);
									if (Math.random() > 0.75) {
										int zeroCount = 1;
										int checkQuantity = quantity;
										while (checkQuantity > 0 && checkQuantity % 10 == 0) {
											zeroCount = zeroCount * 10;
											checkQuantity = checkQuantity / 10;
										}
										quantity = ((int) (quantity / (2 * (zeroCount)))) * zeroCount;
									}

									if (quantity != 0 && quantity < noRelatedSym.getDouble(38)) {
										quantity = quantity * -1 + (int) noRelatedSym.getDouble(38);
									}
									else {
										quantity = (int) noRelatedSym.getDouble(38);
									}

									if (sessionID != null) {
										
										blotterTexts.add(new BlotterText(" filled inquiry"));

										if (noRelatedSym.getChar(54) == '1') {
											blotterTexts.add(new BlotterText(" ask ", red));
											if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
													&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
												quote.setDouble(634, askPx);
											else
												quote.setDouble(133, askPx);
											quote.setDouble(135, quantity);
										}
										else {
											blotterTexts.add(new BlotterText(" bid ", green));
											
											if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
													&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
												quote.setDouble(632, bidPx);
											else
												quote.setDouble(132, bidPx);
											quote.setDouble(134, quantity);
										}
										if (security != null) {
											blotterTexts.add(new BlotterText(security.getName()));
										}

										blotterTexts.add(new BlotterText(" (settlement "
												+ displayDateFormat.format(localMarketDateFormat.parse(noRelatedSym.getString(64))) + ")"));

										quoteResponses.put(quote.getString(117), quote);

										send((Message) quote.clone());

										BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

										for (BlotterListener blotterListener : blotterListeners)
											blotterListener.writeBlotterEntry(blotterEntry);

									}

								}
								else {

									if (sessionID != null) {

										blotterTexts.add(new BlotterText(" rejected inquiry"));

										if (noRelatedSym.getChar(54) == '1') {
											blotterTexts.add(new BlotterText(" ask ", red));
										}
										else {
											blotterTexts.add(new BlotterText(" bid ", green));
										}
										if (security != null) {
											blotterTexts.add(new BlotterText(security.getName()));
										}

										blotterTexts.add(new BlotterText(" (settlement "
												+ displayDateFormat.format(localMarketDateFormat.parse(noRelatedSym.getString(64))) + ")"));

										Message quoteRequestReject = new Message();
										quoteRequestReject.getHeader().setString(MsgType.FIELD, "AG");
										quoteRequestReject.setString(131, message.getString(131));
										quoteRequestReject.setInt(658, 10);
										quoteRequestReject.addGroup(message.getGroup(i, 146));


										send(quoteRequestReject);

										BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

										for (BlotterListener blotterListener : blotterListeners)
											blotterListener.writeBlotterEntry(blotterEntry);

									}

								}
							}
						}

					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private synchronized void handleExecutionReports() {

		Set<Message> messages = new HashSet<Message>();
		messages.addAll(executionReports.values());
		for (Message message : messages) {
			try {
				if (message.getHeader().isSetField(52) && System.currentTimeMillis() - message.getHeader().getUtcTimeStamp(52).getTime() > 10000) {
					if (message.getString(150).equals("0") || message.getString(150).equals("5")) {
						executionReports.remove(message.getString(11));
						Message message2 = (Message) message.clone();

						Security security = null;

						List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

						blotterTexts.add(new BlotterText(name));

						if (message.isSetField(48) && message.isSetField(22)) {
							security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));
							if (Math.random() > 0.25) {
								if (!message.isSetField(110) || message.getDouble(110) < message.getDouble(38)) {
									int quantity = (int) message.getDouble(38);
									int zeroCount = 1;
									int checkQuantity = quantity;
									while (checkQuantity > 0 && checkQuantity % 10 == 0) {
										zeroCount = zeroCount * 10;
										checkQuantity = checkQuantity / 10;
									}
									quantity = ((int) (quantity / (2 * (zeroCount)))) * zeroCount;

									if (quantity == 0 || quantity > message.getDouble(38) || message.isSetField(110) && quantity > message.getDouble(110)) {

										blotterTexts.add(new BlotterText(" filled order Bank of Midas "));

										message2.setChar(150, 'F');
										message2.setChar(39, '2');
										message2.setDouble(14, message.getDouble(38));
										message2.setDouble(151, 0);
										message2.setDouble(32, message.getDouble(38));

										if (security != null && message.isSetField(64) && security.getSecurityDetails().getPriceQuoteMethod() != null
												&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

											try {
												java.util.Date settlementDate = localMarketDateFormat.parse(message.getString(64));
												Double yield = message.getDouble(44);
												Double price = YieldCalculator.getInstance().getPrice(security, yield, settlementDate, 1);
												if (price != null)
													message2.setDouble(669, price);
											}
											catch (ParseException e) {
												e.printStackTrace();
											}

										}

										message2.setDouble(31, message.getDouble(44));

									}
									else {

										blotterTexts.add(new BlotterText(" partially filled order Bank of Midas "));

										message2.setChar(150, 'F');
										message2.setChar(39, '1');
										message2.setDouble(14, quantity);
										message2.setDouble(151, message.getDouble(38) - quantity);
										message2.setDouble(32, quantity);

										if (security != null && message.isSetField(64) && security.getSecurityDetails().getPriceQuoteMethod() != null
												&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

											try {
												java.util.Date settlementDate = localMarketDateFormat.parse(message.getString(64));
												Double yield = message.getDouble(44);
												Double price = YieldCalculator.getInstance().getPrice(security, yield, settlementDate, 1);
												if (price != null)
													message2.setDouble(669, price);
											}
											catch (ParseException e) {
												// TODO Auto-generated
												// catch
												// block
												e.printStackTrace();
											}
										}
										executionReports.put(message2.getString(11), message2);
									}

									message2.setDouble(31, message.getDouble(44));
								}

							}
							else {

								blotterTexts.add(new BlotterText(" filled order Bank of Midas "));

								message2.setChar(150, 'F');
								message2.setChar(39, '2');
								message2.setDouble(14, message.getDouble(38));
								message2.setDouble(151, 0);
								message2.setDouble(32, message.getDouble(38));
								message2.setDouble(31, message.getDouble(44));

								if (security != null && message.isSetField(64) && security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

									try {
										java.util.Date settlementDate = localMarketDateFormat.parse(message.getString(64));
										Double yield = message.getDouble(44);
										Double price = YieldCalculator.getInstance().getPrice(security, yield, settlementDate, 1);
										if (price != null)
											message2.setDouble(669, price);
									}
									catch (ParseException e) {
										e.printStackTrace();
									}

								}
							}

						}
						else {

							blotterTexts.add(new BlotterText(" rejects order Bank of Midas "));

							message2.setChar(150, '8');
							message2.setChar(39, '8');
							message2.setDouble(14, 0);
							message2.setDouble(151, 0);
							message2.setDouble(32, 0);
						}
						if (sessionID != null) {
							if (message2.getChar(54) == '1') {
								blotterTexts.add(new BlotterText(" asks ", red));
							}
							else {
								blotterTexts.add(new BlotterText(" bids ", green));
							}
							if (security != null) {
								blotterTexts.add(new BlotterText(security.getName()));
							}

							try {
								switch (message.getChar(59)) {
									case '0':
										blotterTexts.add(new BlotterText(" good till day"));
										if (message.isSetField(64)) {
											blotterTexts.add(new BlotterText(" (settlement "
													+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));
										}
										break;
									case '1':
										blotterTexts.add(new BlotterText(" good till cancel"));
										break;
									case '4':
										blotterTexts.add(new BlotterText(" fill or kill"));
										if (message.isSetField(64)) {
											blotterTexts.add(new BlotterText(" (settlement "
													+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));
										}
										break;
									case '6':
										blotterTexts.add(new BlotterText(" good till "
												+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(432)))));
										break;

								}
							}
							catch (ParseException e) {
								e.printStackTrace();
							}

							send((Message) message2.clone());

							BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

							for (BlotterListener blotterListener : blotterListeners)
								blotterListener.writeBlotterEntry(blotterEntry);
						}
					}
					if (message.getString(150).equals("F")) {
						executionReports.remove(message.getString(11));

						Security security = null;

						List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

						blotterTexts.add(new BlotterText(name));

						if (message.isSetField(48) && message.isSetField(22))
							security = SecurityDictionary.getInstance().getSecurityForDefaultSecurityID(message.getString(48), message.getString(22));

						Message message2 = (Message) message.clone();
						if (Math.random() > 0.5) {

							blotterTexts.add(new BlotterText(" has done for day order Bank of Midas "));

							message2.setChar(150, '3');
							message2.setChar(39, '3');
							message2.setDouble(31, 0);
							message2.setDouble(151, 0);
							message2.setDouble(32, 0);
						}
						else {

							blotterTexts.add(new BlotterText(" filled  order Bank of Midas "));

							message2.setChar(150, 'F');
							message2.setChar(39, '2');
							message2.setDouble(32, message.getDouble(38) - message.getDouble(14));
							message2.setDouble(14, message.getDouble(38));
							message2.setDouble(151, 0);
							message2.setDouble(31, message.getDouble(44));

							if (security != null && message.isSetField(64) && security.getSecurityDetails().getPriceQuoteMethod() != null
									&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

								try {
									java.util.Date settlementDate = localMarketDateFormat.parse(message.getString(64));
									Double yield = message.getDouble(44);
									Double price = YieldCalculator.getInstance().getPrice(security, yield, settlementDate, 1);
									if (price != null)
										message2.setDouble(669, price);
								}
								catch (ParseException e) {
									e.printStackTrace();
								}

							}
						}
						if (sessionID != null) {
							send(message2);

							if (message2.getChar(54) == '1') {
								blotterTexts.add(new BlotterText(" asks ", red));
							}
							else {
								blotterTexts.add(new BlotterText(" bids ", green));
							}
							if (security != null) {
								blotterTexts.add(new BlotterText(security.getName()));
							}

							try {
								switch (message.getChar(59)) {
									case '0':
										blotterTexts.add(new BlotterText(" good till day"));
										if (message.isSetField(64)) {
											blotterTexts.add(new BlotterText(" (settlement "
													+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));
										}
										break;
									case '1':
										blotterTexts.add(new BlotterText(" good till cancel"));
										break;
									case '4':
										blotterTexts.add(new BlotterText(" fill or kill"));
										if (message.isSetField(64)) {
											blotterTexts.add(new BlotterText(" (settlement "
													+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(64))) + ")"));
										}
										break;
									case '6':
										blotterTexts.add(new BlotterText(" good till "
												+ displayDateFormat.format(localMarketDateFormat.parse(message.getString(432)))));
										break;

								}
							}
							catch (ParseException e) {
								e.printStackTrace();
							}

							BlotterEntry blotterEntry = new BlotterEntry(name, blotterTexts, LevelIcon.INFO);

							for (BlotterListener blotterListener : blotterListeners)
								blotterListener.writeBlotterEntry(blotterEntry);
						}
					}
				}
			}
			catch (FieldNotFound e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send.
	 *
	 * @param message2 the message2
	 */
	public void send(final Message message2) {

		executorService.execute(new Runnable() {

			@Override
			public void run() {

				if (sessionID != null) {
					Session session = Session.lookupSession(sessionID);
					if (session != null) {
						message2.getHeader().setString(1128, "9");
						session.send(message2);
					}
				}

			}
		});

	}

	/**
	 * Adds the agora initiator.
	 *
	 * @param agoraInitiator the agora initiator
	 */
	public synchronized void addAgoraInitiator(AgoraInitiator agoraInitiator) {

		agoraInitiators.add(agoraInitiator);

	}

	/**
	 * Removes the agora initiator.
	 *
	 * @param agoraInitiator the agora initiator
	 */
	public synchronized void removeAgoraInitiator(AgoraInitiator agoraInitiator) {

		agoraInitiators.remove(agoraInitiator);

	}

}
