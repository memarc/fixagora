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
package net.sourceforge.fixagora.basis.server.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.FIXAcceptorComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.SecurityDictionary;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.SpreadSheetHandler;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.netty.channel.Channel;

import quickfix.Group;
import quickfix.Message;

/**
 * The Class BusinessComponentHandler.
 */
public class BusinessComponentHandler implements TransientValueSetter {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private List<DataDictionary> dataDictionaries = new ArrayList<DataDictionary>();

	private SpreadSheetHandler spreadSheetHandler = null;

	private Map<Long, AbstractComponentHandler> businessComponents = new HashMap<Long, AbstractComponentHandler>();

	private Map<String, BankCalendar> bankCalendarMap = new HashMap<String, BankCalendar>();

	private SecurityDictionary securityDictionary = new SecurityDictionary();

	private Map<String, Counterparty> counterpartyMap = new HashMap<String, Counterparty>();

	private Map<String, Counterparty> defaultCounterpartyMap = new HashMap<String, Counterparty>();

	private long id = -1;

	private static Logger log = Logger.getLogger(BusinessComponentHandler.class);
	
	private FSecurityGroup importedSecurities = null;

	/**
	 * Instantiates a new business component handler.
	 *
	 * @param basisPersistenceHandler the basis persistence handler
	 * @param dataDictionaries the data dictionaries
	 */
	public BusinessComponentHandler(BasisPersistenceHandler basisPersistenceHandler, List<DataDictionary> dataDictionaries) {

		super();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		id = System.currentTimeMillis() - calendar.getTimeInMillis();
		calendar.set(Calendar.MILLISECOND, 0);
		this.basisPersistenceHandler = basisPersistenceHandler;
		this.dataDictionaries = dataDictionaries;

		try {
			
			List<FSecurityGroup> fSecurityGroups = basisPersistenceHandler.executeQuery(FSecurityGroup.class, "select f from FSecurityGroup f where f.name='Imported Securities'", this, true);

			if(fSecurityGroups.size()>0)
				importedSecurities = fSecurityGroups.get(0);
			
			List<FSecurity> securities = basisPersistenceHandler.executeQuery(FSecurity.class, "select f from FSecurity f", this, true);

			for (FSecurity security : securities)
				securityDictionary.addSecurity(security);

			List<Counterparty> counterparties = basisPersistenceHandler.executeQuery(Counterparty.class, "select c from Counterparty c", this, true);

			for (Counterparty counterparty : counterparties) {
				for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
					if (counterPartyPartyID.getAbstractBusinessComponent() != null) {
						
						StringBuffer stringBuffer = new StringBuffer(Long.toString(counterPartyPartyID.getAbstractBusinessComponent().getId()));
						stringBuffer.append("-");
						stringBuffer.append(counterPartyPartyID.getPartyID());
						stringBuffer.append("-");
						if (counterPartyPartyID.getPartyIDSource() != null)
							stringBuffer.append(counterPartyPartyID.getPartyIDSource());
						else
							stringBuffer.append("null");
						stringBuffer.append("-");
						if (counterPartyPartyID.getPartyRole() != null)
							stringBuffer.append(Integer.toString(counterPartyPartyID.getPartyRole()));
						else
							stringBuffer.append("null");
						counterpartyMap.put(stringBuffer.toString(), counterparty);
						
					}
					else {
						StringBuffer stringBuffer = new StringBuffer(counterPartyPartyID.getPartyID());
						stringBuffer.append("-");
						if (counterPartyPartyID.getPartyIDSource() != null)
							stringBuffer.append(counterPartyPartyID.getPartyIDSource());
						else
							stringBuffer.append("null");

						defaultCounterpartyMap.put(stringBuffer.toString(), counterparty);
					}
				}

			}

			List<BankCalendar> bankCalendars = basisPersistenceHandler.executeQuery(BankCalendar.class, "select b from BankCalendar b", this, true);

			for (BankCalendar bankCalendar : bankCalendars)
				bankCalendarMap.put(bankCalendar.getName(), bankCalendar);

			List<AbstractBusinessComponent> abstractBusinessComponents = basisPersistenceHandler.executeQuery(AbstractBusinessComponent.class,
					"select a from AbstractBusinessComponent a", this, true);

			for (AbstractBusinessComponent abstractBusinessComponent : abstractBusinessComponents) {

				if (abstractBusinessComponent instanceof HibernateProxy) {
					abstractBusinessComponent = (AbstractBusinessComponent) ((HibernateProxy) abstractBusinessComponent).getHibernateLazyInitializer()
							.getImplementation();
				}

				String className = abstractBusinessComponent.getComponentClass();

				if (className != null) {
					AbstractComponentHandler abstractComponentHandler = (AbstractComponentHandler) Class.forName(className).newInstance();
					businessComponents.put(abstractBusinessComponent.getId(), abstractComponentHandler);
					abstractComponentHandler.setBasisPersistenceHandler(basisPersistenceHandler);
					abstractComponentHandler.setDataDictionaries(dataDictionaries);
					abstractComponentHandler.setSecurityDictionary(securityDictionary);
					abstractComponentHandler.setBusinessComponentHandler(this);
					abstractComponentHandler.setAbstractBusinessComponent(abstractBusinessComponent);
				}
			}

			for (AbstractComponentHandler abstractComponentHandler : businessComponents.values()) {
				AbstractBusinessComponent abstractBusinessComponent = abstractComponentHandler.getAbstractBusinessComponent();

				for (AbstractBusinessComponent abstractBusinessComponent2 : abstractBusinessComponent.getInputComponents()) {
					businessComponents.get(abstractBusinessComponent2.getId()).addOutputComponent(abstractComponentHandler);
					abstractComponentHandler.addInputComponent(businessComponents.get(abstractBusinessComponent2.getId()));
				}
				for (AbstractBusinessComponent abstractBusinessComponent2 : abstractBusinessComponent.getOutputComponents()) {

					businessComponents.get(abstractBusinessComponent2.getId()).addInputComponent(abstractComponentHandler);
					abstractComponentHandler.addOutputComponent(businessComponents.get(abstractBusinessComponent2.getId()));
				}
			}

			this.spreadSheetHandler = new SpreadSheetHandler(basisPersistenceHandler, this);

			for (AbstractComponentHandler abstractComponentHandler : businessComponents.values())
				abstractComponentHandler.setSpreadSheetHandler(spreadSheetHandler);

			for (AbstractComponentHandler abstractComponentHandler : businessComponents.values()) {
				abstractComponentHandler.startHandler();
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/**
	 * On start business component request.
	 *
	 * @param startBusinessComponentRequest the start business component request
	 * @param channel the channel
	 */
	public void onStartBusinessComponentRequest(StartBusinessComponentRequest startBusinessComponentRequest, Channel channel) {

		for (Long abstractBusinessComponent : startBusinessComponentRequest.getBusinessComponents()) {
			AbstractComponentHandler abstractComponentHandler = businessComponents.get(abstractBusinessComponent);
			if (abstractComponentHandler != null)
				abstractComponentHandler.start(channel);
		}
	}

	/**
	 * On stop business component request.
	 *
	 * @param stopBusinessComponentRequest the stop business component request
	 * @param channel the channel
	 */
	public void onStopBusinessComponentRequest(StopBusinessComponentRequest stopBusinessComponentRequest, Channel channel) {

		for (Long abstractBusinessComponent : stopBusinessComponentRequest.getBusinessComponents()) {
			AbstractComponentHandler abstractComponentHandler = businessComponents.get(abstractBusinessComponent);
			if (abstractComponentHandler != null)
				abstractComponentHandler.stop(channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter#isLoggedIn(net.sourceforge.fixagora.basis.shared.model.persistence.FUser)
	 */
	@Override
	public void isLoggedIn(FUser fUser) {

		fUser.setLoggedIn(basisPersistenceHandler.isUserOnline(fUser));

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter#getStartLevel(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public int getStartLevel(AbstractBusinessComponent abstractBusinessComponent) {

		return getStartLevel(abstractBusinessComponent.getId());

	}

	/**
	 * Gets the start level.
	 *
	 * @param abstractBusinessComponent the abstract business component
	 * @return the start level
	 */
	public int getStartLevel(long abstractBusinessComponent) {

		AbstractComponentHandler abstractComponentHandler = businessComponents.get(abstractBusinessComponent);
		if (abstractComponentHandler != null)
			return abstractComponentHandler.getStartLevel();
		return 0;
	}

	/**
	 * Gets the spread sheet handler.
	 *
	 * @return the spread sheet handler
	 */
	public SpreadSheetHandler getSpreadSheetHandler() {

		return spreadSheetHandler;
	}

	/**
	 * Close.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void close(AbstractBusinessObject abstractBusinessObject) {

		AbstractComponentHandler abstractComponentHandler = businessComponents.get(abstractBusinessObject.getId());
		if (abstractComponentHandler != null)
			abstractComponentHandler.close();

	}

	/**
	 * Inits the.
	 *
	 * @param abstractBusinessComponent the abstract business component
	 */
	public void init(AbstractBusinessComponent abstractBusinessComponent) {

		String className = abstractBusinessComponent.getComponentClass();
		if (className != null) {
			try {
				AbstractComponentHandler abstractComponentHandler = (AbstractComponentHandler) Class.forName(className).newInstance();
				businessComponents.put(abstractBusinessComponent.getId(), abstractComponentHandler);
				abstractComponentHandler.setBasisPersistenceHandler(basisPersistenceHandler);
				abstractComponentHandler.setDataDictionaries(dataDictionaries);
				abstractComponentHandler.setSecurityDictionary(securityDictionary);
				abstractComponentHandler.setSpreadSheetHandler(spreadSheetHandler);
				abstractComponentHandler.setBusinessComponentHandler(this);
				abstractComponentHandler.setAbstractBusinessComponent(abstractBusinessComponent);

				for (AbstractComponentHandler abstractComponentHandler2 : businessComponents.values()) {
					AbstractBusinessComponent abstractBusinessComponent2 = abstractComponentHandler2.getAbstractBusinessComponent();

					for (AbstractBusinessComponent abstractBusinessComponent3 : abstractBusinessComponent2.getInputComponents()) {
						businessComponents.get(abstractBusinessComponent3.getId()).addOutputComponent(abstractComponentHandler2);
					}
					for (AbstractBusinessComponent abstractBusinessComponent3 : abstractBusinessComponent2.getOutputComponents()) {
						businessComponents.get(abstractBusinessComponent3.getId()).addInputComponent(abstractComponentHandler2);
					}
				}

				abstractComponentHandler.startHandler();

			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}
	}

	/**
	 * Update.
	 *
	 * @param abstractBusinessComponent the abstract business component
	 */
	public void update(AbstractBusinessComponent abstractBusinessComponent) {

		AbstractComponentHandler abstractComponentHandler = businessComponents.get(abstractBusinessComponent.getId());
		if (abstractComponentHandler != null)
			abstractComponentHandler.setAbstractBusinessComponent(abstractBusinessComponent);
		for (AbstractComponentHandler abstractComponentHandler2 : businessComponents.values()) {
			AbstractBusinessComponent abstractBusinessComponent2 = abstractComponentHandler2.getAbstractBusinessComponent();

			for (AbstractBusinessComponent abstractBusinessComponent3 : abstractBusinessComponent2.getInputComponents()) {
				businessComponents.get(abstractBusinessComponent3.getId()).addOutputComponent(abstractComponentHandler2);
				abstractComponentHandler2.addInputComponent(businessComponents.get(abstractBusinessComponent3.getId()));
			}
			for (AbstractBusinessComponent abstractBusinessComponent3 : abstractBusinessComponent2.getOutputComponents()) {
				businessComponents.get(abstractBusinessComponent3.getId()).addInputComponent(abstractComponentHandler2);
				abstractComponentHandler2.addOutputComponent(businessComponents.get(abstractBusinessComponent3.getId()));
			}
		}

	}

	/**
	 * Gets the business component handler.
	 *
	 * @param abstractBusinessComponent the abstract business component
	 * @return the business component handler
	 */
	public AbstractComponentHandler getBusinessComponentHandler(long abstractBusinessComponent) {

		return businessComponents.get(abstractBusinessComponent);
	}

	/**
	 * Removes the security.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void removeSecurity(FSecurity abstractBusinessObject) {

		securityDictionary.removeSecurity(abstractBusinessObject);

	}

	/**
	 * Adds the security.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void addSecurity(FSecurity abstractBusinessObject) {

		securityDictionary.addSecurity(abstractBusinessObject);

	}

	/**
	 * Update security.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void updateSecurity(FSecurity abstractBusinessObject) {

		securityDictionary.updateSecurity(abstractBusinessObject);

	}

	/**
	 * Gets the central id.
	 *
	 * @return the central id
	 */
	public synchronized long getCentralID() {

		return id++;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter#getChildCount(net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup)
	 */
	@Override
	public int getChildCount(FSecurityGroup securityGroup) {

		List<AbstractBusinessObject> abstractBusinessObjects = basisPersistenceHandler.executeQuery(AbstractBusinessObject.class, "select a from "
				+ securityGroup.getChildClass().getSimpleName() + " a where a.parent.id=?1", securityGroup.getId(), this, false);
		return abstractBusinessObjects.size();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter#getOpenSessions(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor)
	 */
	@Override
	public Set<String> getOpenSessions(AbstractAcceptor fixAcceptor) {

		AbstractComponentHandler abstractComponentHandler = businessComponents.get(fixAcceptor.getId());
		if (abstractComponentHandler instanceof FIXAcceptorComponentHandler)
			return ((FIXAcceptorComponentHandler) abstractComponentHandler).getOpenSessions();
		return null;
	}

	/**
	 * Gets the security dictionary.
	 *
	 * @return the security dictionary
	 */
	public SecurityDictionary getSecurityDictionary() {

		return securityDictionary;
	}

	/**
	 * Gets the bank calendar map.
	 *
	 * @return the bank calendar map
	 */
	public Map<String, BankCalendar> getBankCalendarMap() {

		return bankCalendarMap;
	}

	/**
	 * Adds the counterparty.
	 *
	 * @param counterparty the counterparty
	 */
	public synchronized void addCounterparty(Counterparty counterparty) {

		for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
			if (counterPartyPartyID.getAbstractBusinessComponent() != null) {
				StringBuffer stringBuffer = new StringBuffer(Long.toString(counterPartyPartyID.getAbstractBusinessComponent().getId()));
				stringBuffer.append("-");
				stringBuffer.append(counterPartyPartyID.getPartyID());
				stringBuffer.append("-");
				if (counterPartyPartyID.getPartyIDSource() != null)
					stringBuffer.append(counterPartyPartyID.getPartyIDSource());
				else
					stringBuffer.append("null");
				stringBuffer.append("-");
				if (counterPartyPartyID.getPartyRole() != null)
					stringBuffer.append(Integer.toString(counterPartyPartyID.getPartyRole()));
				else
					stringBuffer.append("null");

				counterpartyMap.put(stringBuffer.toString(), counterparty);
			}
			else {
				StringBuffer stringBuffer = new StringBuffer(counterPartyPartyID.getPartyID());
				stringBuffer.append("-");
				if (counterPartyPartyID.getPartyIDSource() != null)
					stringBuffer.append(counterPartyPartyID.getPartyIDSource());
				else
					stringBuffer.append("null");
				
				defaultCounterpartyMap.put(stringBuffer.toString(), counterparty);
			}
		}

	}

	/**
	 * Removes the counterparty.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public synchronized void removeCounterparty(Counterparty abstractBusinessObject) {

		Set<String> remove = new HashSet<String>();

		for (Entry<String, Counterparty> entry : counterpartyMap.entrySet())
			if (entry.getValue().getId() == abstractBusinessObject.getId())
				remove.add(entry.getKey());

		for (String key : remove)
			counterpartyMap.remove(key);

		remove = new HashSet<String>();

		for (Entry<String, Counterparty> entry : defaultCounterpartyMap.entrySet())
			if (entry.getValue().getId() == abstractBusinessObject.getId())
				remove.add(entry.getKey());

		for (String key : remove)
			defaultCounterpartyMap.remove(key);
	}

	/**
	 * Update counterparty.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public synchronized void updateCounterparty(Counterparty abstractBusinessObject) {

		removeCounterparty(abstractBusinessObject);
		addCounterparty(abstractBusinessObject);

	}

	/**
	 * Gets the counterparty.
	 *
	 * @param businessComponent the business component
	 * @param partyId the party id
	 * @param partyIdSource the party id source
	 * @param partyRole the party role
	 * @return the counterparty
	 */
	public Counterparty getCounterparty(long businessComponent, String partyId, String partyIdSource, Integer partyRole) {

		StringBuffer stringBuffer = new StringBuffer(Long.toString(businessComponent));
		stringBuffer.append("-");
		stringBuffer.append(partyId);
		stringBuffer.append("-");
		if (partyIdSource != null)
			stringBuffer.append(partyIdSource);
		else
			stringBuffer.append("null");
		stringBuffer.append("-");
		if (partyRole != null)
			stringBuffer.append(Integer.toString(partyRole));
		else
			stringBuffer.append("null");
		Counterparty counterparty = counterpartyMap.get(stringBuffer.toString());

		if (counterparty == null) {
			stringBuffer = new StringBuffer(partyId);
			stringBuffer.append("-");
			if (partyIdSource != null)
				stringBuffer.append(partyIdSource);
			else
				stringBuffer.append("null");
						
			counterparty = defaultCounterpartyMap.get(stringBuffer.toString());
		}
		
		return counterparty;
	}

	/**
	 * Handle security list.
	 *
	 * @param message the message
	 */
	public synchronized void handleSecurityList(Message message) {

		try {
			
			List<Group> relatedSyms = new ArrayList<Group>();
			
			List<Group> relatedSyms1 = new ArrayList<Group>();
			
			List<Group> relatedSyms2 = new ArrayList<Group>();
			
			List<Group> relatedSyms3 = new ArrayList<Group>();
			
			for (int i = 1; i <= message.getGroupCount(146); i++) {

				final Group relatedSym = message.getGroup(i, 146);
				if(relatedSym.getGroupCount(711)==0&&relatedSym.getGroupCount(555)==0)
					relatedSyms1.add(relatedSym);
				else if(relatedSym.getGroupCount(555)==0)
					relatedSyms2.add(relatedSym);
				else
					relatedSyms3.add(relatedSym);
			}
			
			relatedSyms.addAll(relatedSyms1);
			relatedSyms.addAll(relatedSyms2);
			relatedSyms.addAll(relatedSyms3);
			
			for (Group relatedSym: relatedSyms) {
				
				String securityId = null;
				
				String securityIdSource = null;
				
				if(relatedSym.isSetField(48))
				{
					securityId = relatedSym.getString(48);
				}
				
				if(relatedSym.isSetField(22))
				{
					securityIdSource = relatedSym.getString(22);
				}
				
				if(securityId!=null&&securityIdSource!=null)
				{
					FSecurity security = securityDictionary.getSecurityForDefaultSecurityID(securityId, securityIdSource);
					
					if(security==null)
					{
						security = securityDictionary.getSecurityForRelatedSym(relatedSym);
						
						if(security!=null)
						{
							LogEntry logEntry2 = new LogEntry();
							logEntry2.setLogDate(new Date());
							logEntry2.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
							logEntry2.setMessageText("Unknow security "+security.getName()+" is imported from counterparty.");
							logEntry2.setMessageComponent("Basis");
							basisPersistenceHandler.writeLogEntry(logEntry2);
							security.setParent(importedSecurities);
							try {
								basisPersistenceHandler.persist(security, null, this);
								addSecurity(security);
							}
							catch (Exception e) {
								LogEntry logEntry3 = new LogEntry();
								logEntry3.setLogDate(new Date());
								logEntry3.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.FATAL);
								logEntry3.setMessageText("Import of security "+security.getName()+" failed.");
								logEntry3.setMessageComponent("Basis");
								basisPersistenceHandler.writeLogEntry(logEntry3);
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
