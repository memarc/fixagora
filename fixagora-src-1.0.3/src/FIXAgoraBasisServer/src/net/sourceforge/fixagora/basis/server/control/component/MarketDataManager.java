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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;

import org.apache.log4j.Logger;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;

/**
 * The Class MarketDataManager.
 */
public class MarketDataManager {

	/**
	 * The Enum ManagerType.
	 */
	public enum ManagerType {
		
		/** The unsubscribed incremental refresh. */
		UNSUBSCRIBED_INCREMENTAL_REFRESH, 
 /** The unsubscribed full refresh. */
 UNSUBSCRIBED_FULL_REFRESH, 
 /** The market data request. */
 MARKET_DATA_REQUEST
	}

	private ManagerType managerType = ManagerType.MARKET_DATA_REQUEST;

	private Map<SessionID, SessionManager> sessionManagerMap = new HashMap<SessionID, SessionManager>();

	private Map<String, Map<Character, Group>> lastValueMap = new HashMap<String, Map<Character, Group>>();

	private Map<String, String> refIDMap = new HashMap<String, String>();

	private long id = 0;

	private String partyID = null;

	private String partyIDSource = null;

	private Integer partyRole = null;

	private DecimalFormat idFormat = new DecimalFormat("######");

	private SecurityDictionary securityDictionary = null;

	private String securityIDSource = null;

	private static Logger log = Logger.getLogger(MarketDataManager.class);
	
	private int major = 0;

	/**
	 * Instantiates a new market data manager.
	 */
	public MarketDataManager() {

		super();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		id = System.currentTimeMillis() - calendar.getTimeInMillis();
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Adds the incremental refresh.
	 *
	 * @param marketDataIncrementalRefresh the market data incremental refresh
	 */
	public void addIncrementalRefresh(List<Message> marketDataIncrementalRefresh) {

		for (Message message : marketDataIncrementalRefresh) {
			message = (Message) message.clone();
			for (int i = 1; i <= message.getGroupCount(268); i++) {
				try {
					Group mdEntry = message.getGroup(i, 268);

					String securityID = null;
					if (mdEntry.isSetField(280)) {
						securityID = refIDMap.get(mdEntry.getString(280));
					}
					if (securityID == null && mdEntry.isSetField(48)) {
						securityID = mdEntry.getString(48);
						if (mdEntry.isSetField(278)) {
							refIDMap.put(mdEntry.getString(278), securityID);
						}
					}
					if (securityID != null) {
						Map<Character, Group> lastValues = lastValueMap.get(securityID);

						if (lastValues == null) {
							lastValues = new HashMap<Character, Group>();
							lastValueMap.put(securityID, lastValues);
						}

						if (mdEntry.isSetField(269)) {
							lastValues.put(mdEntry.getChar(269), mdEntry);
						}
					}
				}
				catch (FieldNotFound e) {
					log.error("Bug", e);
				}
			}
		}
	}

	/**
	 * Sets the manager type.
	 *
	 * @param managerType the new manager type
	 */
	public void setManagerType(ManagerType managerType) {

		this.managerType = managerType;
	}

	/**
	 * Sets the security dictionary.
	 *
	 * @param securityDictionary the new security dictionary
	 */
	public void setSecurityDictionary(SecurityDictionary securityDictionary) {

		this.securityDictionary = securityDictionary;
	}

	/**
	 * Sets the security id source.
	 *
	 * @param securityIDSource the new security id source
	 */
	public void setSecurityIDSource(String securityIDSource) {

		this.securityIDSource = securityIDSource;
	}

	/**
	 * Gets the messages.
	 *
	 * @param marketDataIncrementalRefresh the market data incremental refresh
	 * @param sessionID the session id
	 * @return the messages
	 */
	public List<Message> getMessages(List<Message> marketDataIncrementalRefresh, SessionID sessionID) {

		List<Message> messages = new ArrayList<Message>();

		Map<String, Set<Character>> securities = new HashMap<String, Set<Character>>();

		SessionManager sessionManager = sessionManagerMap.get(sessionID);

		if (sessionManager != null) {

			for (Message message : marketDataIncrementalRefresh) {

				for (int i = 1; i <= message.getGroupCount(268); i++) {
					try {
						Group mdEntry = message.getGroup(i, 268);
						String securityID = null;
						if (mdEntry.isSetField(280)) {
							securityID = refIDMap.get(mdEntry.getString(280));
						}
						if (securityID == null && mdEntry.isSetField(48)) {
							securityID = mdEntry.getString(48);
							if (mdEntry.isSetField(278))
								refIDMap.put(mdEntry.getString(278), securityID);
						}

						if (managerType != ManagerType.MARKET_DATA_REQUEST || sessionManager.getSecurities().contains(securityID)) {

							Set<Character> entries = securities.get(securityID);
							if (entries == null) {
								entries = new HashSet<Character>();
								securities.put(securityID, entries);
							}
							if (mdEntry.isSetField(269)) {
								if (!sessionManager.isIncrementalRefresh() || sessionManager.getEntryTypes().contains(mdEntry.getChar(269))
										|| managerType == ManagerType.UNSUBSCRIBED_INCREMENTAL_REFRESH)
									entries.add(mdEntry.getChar(269));
							}
						}
					}
					catch (Exception exception) {
						log.error("Bug", exception);
					}
				}
			}

			if (sessionManager.isIncrementalRefresh())
				messages.addAll(getIncrementalRefreshs(securities, sessionID));
			else
				messages.addAll(getMarketDataSnapShotFullRefreshs(securities));

		}

		return messages;
	}

	/**
	 * Adds the session.
	 *
	 * @param sessionID the session id
	 * @return the list
	 */
	public List<Message> addSession(SessionID sessionID) {

		List<Message> messages = new ArrayList<Message>();
		if (managerType != ManagerType.MARKET_DATA_REQUEST) {
			Map<String, Set<Character>> securities = new HashMap<String, Set<Character>>();
			for (Entry<String, Map<Character, Group>> entry : lastValueMap.entrySet()) {
				Set<Character> entryTypes = new HashSet<Character>();
				securities.put(entry.getKey(), entryTypes);
				entryTypes.addAll(entry.getValue().keySet());
			}
			if (managerType == ManagerType.UNSUBSCRIBED_INCREMENTAL_REFRESH) {
				SessionManager sessionManager = new SessionManager(true);
				sessionManagerMap.put(sessionID, sessionManager);
				messages.addAll(getIncrementalRefreshs(securities, sessionID));
			}
			else {
				SessionManager sessionManager = new SessionManager(false);
				sessionManagerMap.put(sessionID, sessionManager);
				messages.addAll(getMarketDataSnapShotFullRefreshs(securities));
			}
		}
		return messages;
	}

	/**
	 * Process market data request.
	 *
	 * @param message the message
	 * @param sessionID the session id
	 * @return the list
	 */
	public List<Message> processMarketDataRequest(Message message, SessionID sessionID) {

		boolean snapshotOnly = false;

		boolean incrementalRefresh = false;

		Set<String> securitySet = new HashSet<String>();

		Set<Character> entryTypes = new HashSet<Character>();

		try {
			if (message.isSetField(263))
				if (message.getString(263).equals("0"))
					snapshotOnly = true;

			if (message.isSetField(265))
				if (message.getString(265).equals("1"))
					incrementalRefresh = true;

			SessionManager sessionManager = new SessionManager(incrementalRefresh);

			for (int i = 1; i <= message.getGroupCount(267); i++) {
				final Group group = message.getGroup(i, 267);
				char entryType = group.getChar(269);
				entryTypes.add(entryType);
				sessionManager.addEntryType(entryType);

			}

			for (int i = 1; i <= message.getGroupCount(146); i++) {

				final Group group = message.getGroup(i, 146);
				if (group.isSetField(48)) {
					String securityID = group.getString(48).trim();
					if (group.isSetField(22)) {
						String securityIDSource = group.getString(22);
						FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityID, securityIDSource);
						if (security != null) {
							group.setString(48, security.getSecurityID());
							if (security.getSecurityDetails().getSecurityIDSource() != null) {
								group.setString(22, security.getSecurityDetails().getSecurityIDSource());
							}
							else
								group.removeField(22);
							securitySet.add(security.getSecurityID());
						}
					}
					else
						securitySet.add(securityID);
				}
			}

			if (!snapshotOnly)
				sessionManagerMap.put(sessionID, sessionManager);

			Map<String, Set<Character>> securities = new HashMap<String, Set<Character>>();
			for (String security : securitySet) {
				Set<Character> entries = new HashSet<Character>();
				securities.put(security, entries);
				sessionManager.addSecurity(security);
				entries.addAll(entryTypes);
			}

			return getMarketDataSnapShotFullRefreshs(securities);

		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
			return new ArrayList<Message>();
		}

	}

	private List<Message> getIncrementalRefreshs(Map<String, Set<Character>> securities, SessionID sessionID) {

		List<Message> messages = new ArrayList<Message>();
		Message message = new Message();
		message.getHeader().setString(MsgType.FIELD, "X");

		SessionManager sessionManager = sessionManagerMap.get(sessionID);

		if (sessionManager != null) {

			try {
				for (Entry<String, Set<Character>> security : securities.entrySet()) {

					Map<Character, Group> lastValues = lastValueMap.get(security.getKey());
					if (lastValues != null)
						for (Character entryType : security.getValue()) {

							Group incrementalMdEntry = lastValues.get(entryType);
							if (incrementalMdEntry != null) {
								message.addGroup(incrementalMdEntry);
							}
						}
				}

				if (message.getGroupCount(268) > 0) {

					Message newMessage = (Message) message.clone();
					for (int i = 1; i <= newMessage.getGroupCount(268); i++) {

						Group group = newMessage.getGroup(i, 268);
						char entry = group.getChar(269);
						String entryID = idFormat.format(id++);
						group.setString(278, entryID);
						String securityID = null;
						if (group.isSetField(280)) {
							securityID = refIDMap.get(group.getString(280));
						}
						if (securityID == null && group.isSetField(48)) {
							securityID = group.getString(48);
						}
						if (securityID != null) {
							char updateAction = group.getChar(279);
							if (sessionManager.getRefId(securityID, entry) == null) {
								group.setString(55, "N/A");
								group.setString(48, securityID);
								if (updateAction != '2' && updateAction != '3' && updateAction != '4')
									group.setChar(279, '0');
								sessionManager.setRefId(securityID, entry, entryID);

								if (major>4&&partyID != null) {
									Group instrumentParty = new Group(453, 448, new int[] { 448, 447, 452 });
									instrumentParty.setString(448, partyID);
									if (partyIDSource != null)
										instrumentParty.setChar(447, partyIDSource.charAt(0));
									if (partyRole != null)
										instrumentParty.setInt(452, partyRole);
									group.addGroup(instrumentParty);
								}

							}
							else {
								group.removeField(269);
							}
							if (group.isSetField(48)) {
								if (securityIDSource != null) {
									String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(securityID, securityIDSource);
									if (alternativeSecurityID != null) {
										group.setString(48, alternativeSecurityID);
										group.setString(22, securityIDSource);
									}
								}
								else {
									FSecurity fSecurity = securityDictionary.getSecurityForDefaultSecurityID(securityID);
									if (fSecurity != null) {
										group.setString(22, fSecurity.getSecurityDetails().getSecurityIDSource());
									}
								}
							}
							group.setString(280, sessionManager.getRefId(securityID, entry));
							if (updateAction == '2' || updateAction == '3' || updateAction == '4') {
								sessionManager.removeRefId(securityID, entry);
							}
						}
					}
					messages.add(newMessage);
				}

			}
			catch (FieldNotFound e) {
				log.error("Bug", e);
			}
		}

		return messages;
	}

	private List<Message> getMarketDataSnapShotFullRefreshs(Map<String, Set<Character>> securities) {

		List<Message> messages = new ArrayList<Message>();

		try {
			for (Entry<String, Set<Character>> security : securities.entrySet()) {

				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "W");

				Map<Character, Group> lastValues = lastValueMap.get(security.getKey());

				if (lastValues == null) {
					lastValues = new HashMap<Character, Group>();
					lastValueMap.put(security.getKey(), lastValues);
				}

				message.setString(55, "N/A");
				message.setString(48, security.getKey());
				if (securityIDSource != null) {
					String alternativeSecurityID = securityDictionary.getAlternativeSecurityID(security.getKey(), securityIDSource);
					if (alternativeSecurityID != null) {
						message.setString(48, alternativeSecurityID);
						message.setString(22, securityIDSource);
					}
				}

				for (Character entryType : lastValues.keySet()) {
					Group incrementalMdEntry = lastValues.get(entryType);
					if (incrementalMdEntry != null) {
						final Group group = new Group(268, 269, new int[] { 269, 270, 271, 272, 273, 811, 332, 333, 1020, 453 });
						if ((!incrementalMdEntry.isSetField(279) || (incrementalMdEntry.getChar(279) != '2' && incrementalMdEntry.getChar(279) != '3' && incrementalMdEntry
								.getChar(279) != '4'))) {
							if (incrementalMdEntry.isSetField(269))
								group.setString(269, incrementalMdEntry.getString(269));

							if (incrementalMdEntry.isSetField(270))
								group.setString(270, incrementalMdEntry.getString(270));

							if (incrementalMdEntry.isSetField(271))
								group.setString(271, incrementalMdEntry.getString(271));

							if (incrementalMdEntry.isSetField(272))
								group.setString(272, incrementalMdEntry.getString(272));

							if (incrementalMdEntry.isSetField(273))
								group.setString(273, incrementalMdEntry.getString(273));

							if (incrementalMdEntry.isSetField(811))
								group.setString(811, incrementalMdEntry.getString(811));

							if (incrementalMdEntry.isSetField(332))
								group.setString(332, incrementalMdEntry.getString(332));

							if (incrementalMdEntry.isSetField(333))
								group.setString(333, incrementalMdEntry.getString(333));

							if (incrementalMdEntry.isSetField(1020))
								group.setString(1020, incrementalMdEntry.getString(1020));

							message.addGroup(group);
							
							if (major>4&&partyID != null) {
								Group instrumentParty = new Group(453, 448, new int[] { 448, 447, 452 });
								instrumentParty.setString(448, partyID);
								if (partyIDSource != null)
									instrumentParty.setChar(447, partyIDSource.charAt(0));
								if (partyRole != null)
									instrumentParty.setInt(452, partyRole);
								group.addGroup(instrumentParty);
							}

						}
					}
				}

				if (message.getGroupCount(268) == 0) {
					final Group group = new Group(268, 269, new int[] { 269, 270, 271, 272, 273, 811, 332, 333, 1020, 453 });
					group.setString(269, "J");
					
					if (major>4&&partyID != null) {
						Group instrumentParty = new Group(453, 448, new int[] { 448, 447, 452 });
						instrumentParty.setString(448, partyID);
						if (partyIDSource != null)
							instrumentParty.setChar(447, partyIDSource.charAt(0));
						if (partyRole != null)
							instrumentParty.setInt(452, partyRole);
						group.addGroup(instrumentParty);
					}
					
					message.addGroup(group);
				}



				messages.add(message);
			}
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}
		return messages;
	}

	/**
	 * Sets the party id.
	 *
	 * @param partyID the new party id
	 */
	public void setPartyID(String partyID) {

		this.partyID = partyID;
	}

	/**
	 * Sets the party id source.
	 *
	 * @param partyIDSource the new party id source
	 */
	public void setPartyIDSource(String partyIDSource) {

		this.partyIDSource = partyIDSource;
	}

	/**
	 * Sets the party role.
	 *
	 * @param partyRole the new party role
	 */
	public void setPartyRole(Integer partyRole) {

		this.partyRole = partyRole;
	}

	/**
	 * Removes the session.
	 *
	 * @param sessionID the session id
	 */
	public void removeSession(SessionID sessionID) {

		sessionManagerMap.remove(sessionID);

	}

	
	/**
	 * Sets the major.
	 *
	 * @param major the new major
	 */
	public void setMajor(int major) {
	
		this.major = major;
	}
	
	

}
