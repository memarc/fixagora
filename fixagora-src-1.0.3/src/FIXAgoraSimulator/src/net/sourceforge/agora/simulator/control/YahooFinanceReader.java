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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;

/**
 * The Class YahooFinanceReader.
 */
public class YahooFinanceReader {

	private boolean stopped = false;

	private ArrayList<Character> entryTypes = null;

	private StringBuffer requestEntryTypes = new StringBuffer();

	private List<StringBuffer> requestInstrumentsList = new ArrayList<StringBuffer>();

	private SessionID sessionID = null;

	private DecimalFormat decimalFormat = new DecimalFormat("#,###.#");

	private DecimalFormat idFormat = new DecimalFormat("######");

	private boolean snapshotOnly = false;

	private int updateType = 0;

	private long id = System.currentTimeMillis();

	private Map<String, Map<Character, String>> refIdMap = new HashMap<String, Map<Character, String>>();

	private Map<String, Map<Character, String>> lastValueMap = new HashMap<String, Map<Character, String>>();

	/**
	 * Instantiates a new yahoo finance reader.
	 *
	 * @param message the message
	 * @param sessionID the session id
	 */
	public YahooFinanceReader(Message message, SessionID sessionID) {

		super();

		try {
			if (message.isSetField(263))
				if (message.getString(263).equals("0"))
					snapshotOnly = true;

			if (message.isSetField(265))
				if (message.getString(265).equals("1"))
					updateType = 1;
		}
		catch (FieldNotFound e1) {
			e1.printStackTrace();
		}

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		this.sessionID = sessionID;
		entryTypes = new ArrayList<Character>();
		requestEntryTypes.append("s");
		for (int k = 1; k <= message.getGroupCount(267); k++)
			try {
				final Group group = message.getGroup(k, 267);
				char entryType = group.getChar(269);
				if (entryType == '0') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sbsb6");
				}
				if (entryType == '1') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sbsa5");
				}
				if (entryType == '2') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sl1");
				}
				if (entryType == '3') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sl1");
				}
				if (entryType == '4') {
					entryTypes.add(entryType);
					requestEntryTypes.append("so");
				}
				if (entryType == '5' || entryType == 'W') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sp");
				}
				if (entryType == '7' || entryType == 'K') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sh");
				}
				if (entryType == '8' || entryType == 'L') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sg");
				}
				if (entryType == 'B') {
					entryTypes.add(entryType);
					requestEntryTypes.append("sk3");
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		int i = 0;
		StringBuffer requestInstruments = null;
		for (int k = 1; k <= message.getGroupCount(146); k++) {
			if (i == 0) {
				requestInstruments = new StringBuffer();
				requestInstrumentsList.add(requestInstruments);
			}
			i++;
			try {
				final Group group = message.getGroup(k, 146);
				String security = group.getString(48).trim();
				if (security.length() > 0) {
					requestInstruments.append("+");
					requestInstruments.append(security);
				}
			}
			catch (Exception e) {

			}
		}
		if (requestInstruments.length() > 1) {
			Reader reader = new Reader();
			reader.start();
		}

	}

	private class Reader extends Thread {

		@Override
		public void run() {

			while (!stopped) {

				if (snapshotOnly)
					stopped = true;
				long timestamp = System.currentTimeMillis();

				try {
					if (updateType < 2) {

						if (updateType == 1)
							updateType = 2;

						for (StringBuffer requestInstruments : requestInstrumentsList) {
							StringBuffer url = new StringBuffer("http://finance.yahoo.com/d/quotes.csv?s=");
							url.append(requestInstruments.toString().substring(1));
							url.append("&f=");
							url.append(requestEntryTypes.toString());
							URL yahoo = new URL(url.toString());
							BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));
							String inputLine;
							while ((inputLine = in.readLine()) != null) {

								Message message = new Message();
								message.getHeader().setString(MsgType.FIELD, "W");
								String[] parts = inputLine.split(",");

								String symbol = parts[0].substring(1, parts[0].length() - 1);
								if (symbol.startsWith("^"))
									parts = inputLine.substring(symbol.length() * 2 + 6).split(",\"\\^" + symbol.substring(1) + "\",");
								else
									parts = inputLine.substring(symbol.length() * 2 + 6).split(",\"" + symbol + "\",");
								message.setString(55, "N/A");
								message.setString(48, symbol);
								message.setString(22, "8");
								double size = 0;
								int shift = 0;
								for (int i = 0; i < entryTypes.size(); i++) {
									Character character = entryTypes.get(i);
									final Group group = new Group(268, 269, new int[] { 269, 270, 271 });
									group.setChar(269, character);
									String part = parts[i + shift];
									try {
										if (!character.equals('B'))
											group.setDouble(270, decimalFormat.parse(part).doubleValue());
										else
											size = decimalFormat.parse(part).doubleValue();
									}
									catch (Exception e) {
										group.setDouble(270, 0);
									}
									if (character.equals('0') || character.equals('1')) {
										shift++;
										part = parts[i + shift];
										try {
											size = decimalFormat.parse(part).doubleValue();
										}
										catch (Exception e) {
										}
										group.setUtcDateOnly(272, new Date());
										group.setUtcTimeOnly(273, new Date());

									}
									group.setDouble(271, size);
									message.addGroup(group);
								}

								Session.lookupSession(sessionID).send(message);
							}
							in.close();
						}
					}
					else {
						Message message = new Message();
						message.getHeader().setString(MsgType.FIELD, "X");

						for (StringBuffer requestInstruments : requestInstrumentsList) {
							StringBuffer url = new StringBuffer("http://finance.yahoo.com/d/quotes.csv?s=");
							url.append(requestInstruments.toString().substring(1));
							url.append("&f=");
							url.append(requestEntryTypes.toString());
							URL yahoo = new URL(url.toString());
							BufferedReader in = null;
							try {
								in = new BufferedReader(new InputStreamReader(yahoo.openStream()));
							}
							catch (Exception e1) {
								if(in!=null)
								{
									in.close();
									in=null;
								}
								e1.printStackTrace();
							}
							String inputLine;
							while (in!=null && (inputLine = in.readLine()) != null) {

								String[] parts = inputLine.split(",");

								String symbol = parts[0].substring(1, parts[0].length() - 1);

								if (symbol.startsWith("^"))
									parts = inputLine.substring(symbol.length() * 2 + 6).split(",\"\\^" + symbol.substring(1) + "\",");
								else
									parts = inputLine.substring(symbol.length() * 2 + 6).split(",\"" + symbol + "\",");

								double size = 0;
								int shift = 0;
								for (int i = 0; i < entryTypes.size(); i++) {
									Character character = entryTypes.get(i);
									final Group group = new Group(268, 279, new int[] { 279, 269, 278, 280, 55, 48, 22, 270, 271, 272, 273 });
									group.setChar(269, character);

									Map<Character, String> entryMap = refIdMap.get(symbol);
									if (entryMap == null) {
										entryMap = new HashMap<Character, String>();
										refIdMap.put(symbol, entryMap);
									}

									Map<Character, String> valueMap = lastValueMap.get(symbol);
									if (valueMap == null) {
										valueMap = new HashMap<Character, String>();
										lastValueMap.put(symbol, valueMap);
									}

									String oldValue = valueMap.get(character);

									String refId = entryMap.get(character);
									String entryId = idFormat.format(id++);
									if (refId == null) {

										group.setString(55, "N/A");
										group.setString(48, symbol);
										group.setString(22, "8");
										group.setString(279, "0");
										entryMap.put(character, entryId);

									}
									else {
										group.setString(279, "1");
										group.setString(280, refId);
									}
									group.setString(278, entryId);

									String part = parts[i + shift];

									try {
										if (!character.equals('B'))
										{
											//Test
//											double value = decimalFormat.parse(part).doubleValue();
//											value = value + ((int)(Math.random()*10))/1000d;
//											
//											group.setDouble(270, value);
											
											//original
											group.setDouble(270, decimalFormat.parse(part).doubleValue());
										}
										else
											size = decimalFormat.parse(part).doubleValue();
									}
									catch (Exception e) {
										group.setDouble(270, 0);
									}
									if (character.equals('0') || character.equals('1')) {
										shift++;
										part = parts[i + shift];
										try {
											size = decimalFormat.parse(part).doubleValue();
										}
										catch (Exception e) {
										}

									}

									if (character.equals('0') || character.equals('1') || character.equals('3')) {
										Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
										group.setUtcDateOnly(272, calendar.getTime());
										group.setUtcTimeOnly(273, calendar.getTime());
									}

									group.setDouble(271, size);

									StringBuffer newValue = new StringBuffer();
									if (group.isSetField(270))
										newValue.append(group.getString(270));
									newValue.append(";");
									if (group.isSetField(271))
										newValue.append(group.getString(271));
									if (oldValue == null || !newValue.toString().equals(oldValue)) {
										message.addGroup(group);
										valueMap.put(character, newValue.toString());
									}
								}

							}
							if(in!=null)
								in.close();
						}
						if (message.getGroupCount(268) > 0) {
							Session.lookupSession(sessionID).send(message);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				long delay = 2000 - (System.currentTimeMillis() - timestamp);
				try {
					if (delay > 0)
						sleep(delay);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * Stop.
	 */
	public void stop() {

		stopped = true;

	}

	/**
	 * Reset.
	 */
	public void reset() {

		refIdMap.clear();
		lastValueMap.clear();
		
	}

}
