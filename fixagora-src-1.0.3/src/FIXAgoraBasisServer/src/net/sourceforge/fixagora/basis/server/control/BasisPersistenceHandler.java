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
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LogEntryResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendarGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterpartyGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptorGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXInitiatorGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRoleGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.IDManager;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellContent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellContent.SpreadSheetCellType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetColumnFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetRowFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellReference;
import org.jboss.netty.channel.Channel;

/**
 * The Class BasisPersistenceHandler.
 */
public class BasisPersistenceHandler {

	private EntityManagerFactory entityManagerFactory = null;
	private FBasisClientAcceptor basisClientAcceptor = null;
	private Map<Channel, FUser> userMap = new HashMap<Channel, FUser>();

	private static Logger log = Logger.getLogger(BasisPersistenceHandler.class);

	private LinkedBlockingQueue<ResponseSet> abstractResponsesQueue = new LinkedBlockingQueue<ResponseSet>();

	private LinkedBlockingQueue<LogEntry> logEntryQueue = new LinkedBlockingQueue<LogEntry>();

	private boolean closed = false;

	private EntityManager entityManager = null;

	/**
	 * Instantiates a new basis persistence handler.
	 */
	public BasisPersistenceHandler() {

		super();

		entityManagerFactory = Persistence.createEntityManagerFactory("fbasis");

		entityManager = entityManagerFactory.createEntityManager();

		createNewSystem();
	}

	private void handleResponses() {

		while (!closed) {
			try {
				ResponseSet responseSet = abstractResponsesQueue.take();
				AbstractResponse abstractResponse = responseSet.getAbstractResponse();
				abstractResponse.setTimestamp(System.currentTimeMillis());
				Channel channel = responseSet.getChannel();

				if (channel == null) {
					if (log.isTraceEnabled())
						log.trace("send to all " + abstractResponse);
					basisClientAcceptor.send(abstractResponse);
				}
				else
					basisClientAcceptor.send(abstractResponse, channel);
			}
			catch (InterruptedException e) {
				log.error("Bug", e);
			}
		}
	}

	/**
	 * Persist.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param channel the channel
	 * @param transientValueSetter the transient value setter
	 * @throws Exception the exception
	 */
	public synchronized void persist(AbstractBusinessObject abstractBusinessObject, Channel channel, TransientValueSetter transientValueSetter)
			throws Exception {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			if (channel != null)
				abstractBusinessObject.setModificationUser(userMap.get(channel));
			else
				abstractBusinessObject.setModificationUser(null);
			abstractBusinessObject.setModificationDate(new Date());
			entityManager.persist(abstractBusinessObject);
			tx.commit();

			if (basisClientAcceptor != null)
				basisClientAcceptor.send(new PersistResponse(abstractBusinessObject));

			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
			if (channel != null)
				logEntry.setMessageText("User " + userMap.get(channel) + " created new " + abstractBusinessObject.getBusinessObjectName().toLowerCase() + " "
						+ abstractBusinessObject + ".");
			else
				logEntry.setMessageText("System created new " + abstractBusinessObject.getBusinessObjectName().toLowerCase() + " " + abstractBusinessObject
						+ ".");
			logEntry.setMessageComponent("Basis");
			writeLogEntry(logEntry);
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			throw (e);
		}

	}

	/**
	 * Persist.
	 *
	 * @param object the object
	 * @throws Exception the exception
	 */
	public synchronized void persist(Object object) throws Exception {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			entityManager.persist(object);
			tx.commit();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			throw (e);
		}

	}

	/**
	 * Update.
	 *
	 * @param object the object
	 * @throws Exception the exception
	 */
	public synchronized void update(Object object) throws Exception {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			entityManager.merge(object);
			tx.commit();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			throw (new Exception("Update failed"));
		}

	}

	/**
	 * Removes the object.
	 *
	 * @param object the object
	 * @throws Exception the exception
	 */
	public synchronized void removeObject(Object object) throws Exception {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			entityManager.remove(object);
			tx.commit();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			throw (e);
		}

	}

	/**
	 * Find by name.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param name the name
	 * @param transientValueSetter the transient value setter
	 * @return the t
	 */
	public synchronized <T> T findByName(Class<T> clazz, String name, TransientValueSetter transientValueSetter) {

		AbstractBusinessObject abstractBusinessObject = null;
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		Query query = entityManager.createQuery("select a from " + clazz.getSimpleName() + " a where a.name=?1");
		query.setParameter(1, name);
		@SuppressWarnings("unchecked")
		List<AbstractBusinessObject> abstractBusinessObjects = query.getResultList();
		if (abstractBusinessObjects.size() > 0) {
			abstractBusinessObject = abstractBusinessObjects.get(0);
			abstractBusinessObject.makeEager();
		}
		tx.commit();

		if (abstractBusinessObject == null)
			return null;
		abstractBusinessObject.setTransientValues(transientValueSetter);
		return clazz.cast(abstractBusinessObject);
	}

	/**
	 * Find by id.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param id the id
	 * @param transientValueSetter the transient value setter
	 * @return the t
	 */
	public synchronized <T> T findById(Class<T> clazz, long id, TransientValueSetter transientValueSetter) {

		AbstractBusinessObject abstractBusinessObject = null;
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		Query query = entityManager.createQuery("select a from " + clazz.getSimpleName() + " a where a.id=?1");
		query.setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<AbstractBusinessObject> abstractBusinessObjects = query.getResultList();
		if (abstractBusinessObjects.size() > 0)
			abstractBusinessObject = abstractBusinessObjects.get(0);
		if (abstractBusinessObject == null) {
			tx.commit();

			return null;
		}
		abstractBusinessObject.makeEager();
		tx.commit();
		abstractBusinessObject.setTransientValues(transientValueSetter);
		return clazz.cast(abstractBusinessObject);
	}

	/**
	 * Execute query.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param queryString the query string
	 * @param transientValueSetter the transient value setter
	 * @param makeEager the make eager
	 * @return the list
	 */
	public <T extends PersistenceInterface> List<T> executeQuery(Class<T> clazz, String queryString, TransientValueSetter transientValueSetter,
			boolean makeEager) {

		return executeQuery(clazz, queryString, new ArrayList<Object>(), transientValueSetter, makeEager);
	}

	/**
	 * Execute query.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param queryString the query string
	 * @param parameter the parameter
	 * @param transientValueSetter the transient value setter
	 * @param makeEager the make eager
	 * @return the list
	 */
	public <T extends PersistenceInterface> List<T> executeQuery(Class<T> clazz, String queryString, Object parameter,
			TransientValueSetter transientValueSetter, boolean makeEager) {

		List<Object> parameters = new ArrayList<Object>();
		parameters.add(parameter);
		return executeQuery(clazz, queryString, parameters, transientValueSetter, makeEager);
	}

	/**
	 * Execute query.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param queryString the query string
	 * @param parameterList the parameter list
	 * @param transientValueSetter the transient value setter
	 * @param makeEager the make eager
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends PersistenceInterface> List<T> executeQuery(Class<T> clazz, final String queryString, List<Object> parameterList,
			TransientValueSetter transientValueSetter, boolean makeEager) {

		List<T> abstractBusinessObjects = new ArrayList<T>();

		EntityTransaction tx = entityManager.getTransaction();

		try {

			tx.begin();

			Query query = entityManager.createQuery(queryString);
			for (int i = 1; i <= parameterList.size(); i++)
				query.setParameter(i, parameterList.get(i - 1));
			abstractBusinessObjects.addAll(query.getResultList());

			for (PersistenceInterface abstractBusinessObject : abstractBusinessObjects) {
				if (makeEager)
					abstractBusinessObject.makeEager();
				else {
					abstractBusinessObject.getReadRoles().size();
					abstractBusinessObject.getWriteRoles().size();
					abstractBusinessObject.getExecuteRoles().size();
				}
			}

			tx.commit();

		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		for (PersistenceInterface abstractBusinessObject : abstractBusinessObjects)
			abstractBusinessObject.setTransientValues(transientValueSetter);

		//Collections.sort(abstractBusinessObjects);
		
		return abstractBusinessObjects;
	}

	/**
	 * Close.
	 */
	public void close() {

		entityManager.close();
		entityManagerFactory.close();
	}

	private synchronized void createNewSystem() {

		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		Query query = entityManager.createQuery("select b from BusinessSection b where b.name='Master Data'");
		@SuppressWarnings("unchecked")
		List<BusinessSection> masterDataBusinessSections = query.getResultList();

		BusinessSection masterDataBusinessSection = null;

		if (masterDataBusinessSections.size() == 0) {
			masterDataBusinessSection = new BusinessSection();
			masterDataBusinessSection.setName("Master Data");
			masterDataBusinessSection.setPosition(10d);
			masterDataBusinessSection.setDescription("This group contains all master data.");
			masterDataBusinessSection.setModificationDate(new Date());

			entityManager.persist(masterDataBusinessSection);

		}
		else
			masterDataBusinessSection = masterDataBusinessSections.get(0);

		Query query2 = entityManager.createQuery("select b from BusinessSection b where b.name='Administration'");
		@SuppressWarnings("unchecked")
		List<BusinessSection> administrationBusinessSections = query2.getResultList();

		BusinessSection administrationBusinessSection = null;

		if (administrationBusinessSections.size() == 0) {
			administrationBusinessSection = new BusinessSection();
			administrationBusinessSection.setName("Administration");
			administrationBusinessSection.setPosition(30d);
			administrationBusinessSection.setDescription("This group contains all administration data.");
			administrationBusinessSection.setModificationDate(new Date());

			entityManager.persist(administrationBusinessSection);

		}
		else
			administrationBusinessSection = administrationBusinessSections.get(0);

		Query query3 = entityManager.createQuery("select b from BusinessSection b where b.name='User Administration'");
		@SuppressWarnings("unchecked")
		List<BusinessSection> userAdministrationBusinessSections = query3.getResultList();

		BusinessSection userAdministrationBusinessSection = null;

		if (userAdministrationBusinessSections.size() == 0) {
			userAdministrationBusinessSection = new BusinessSection();
			userAdministrationBusinessSection.setName("User Administration");
			userAdministrationBusinessSection.setPosition(3d);
			userAdministrationBusinessSection.setParent(administrationBusinessSection);
			userAdministrationBusinessSection.setDescription("This group contains all user data.");
			userAdministrationBusinessSection.setModificationDate(new Date());
			entityManager.persist(userAdministrationBusinessSection);

			administrationBusinessSection.getChildren().add(userAdministrationBusinessSection);
			entityManager.merge(administrationBusinessSection);
		}
		else
			userAdministrationBusinessSection = userAdministrationBusinessSections.get(0);

		Query query4 = entityManager.createQuery("select b from BusinessSection b where b.name='Market Interfaces'");
		@SuppressWarnings("unchecked")
		List<BusinessSection> marketInterfacesBusinessSections = query4.getResultList();

		BusinessSection marketInterfacesBusinessSection = null;

		if (marketInterfacesBusinessSections.size() == 0) {
			marketInterfacesBusinessSection = new BusinessSection();
			marketInterfacesBusinessSection.setName("Market Interfaces");
			marketInterfacesBusinessSection.setPosition(4d);
			marketInterfacesBusinessSection.setParent(administrationBusinessSection);
			marketInterfacesBusinessSection.setDescription("This group contains all market interfaces.");
			marketInterfacesBusinessSection.setModificationDate(new Date());

			entityManager.persist(marketInterfacesBusinessSection);

			administrationBusinessSection.getChildren().add(marketInterfacesBusinessSection);
			entityManager.merge(administrationBusinessSection);

		}
		else
			marketInterfacesBusinessSection = marketInterfacesBusinessSections.get(0);

		Query query5 = entityManager.createQuery("select f from FRoleGroup f");
		@SuppressWarnings("unchecked")
		List<FRoleGroup> fRoleGroups = query5.getResultList();

		FRoleGroup fRoleGroup = null;

		if (fRoleGroups.size() == 0) {
			fRoleGroup = new FRoleGroup();
			fRoleGroup.setName("Roles");
			fRoleGroup.setDescription("This group contains all roles used by the permission control.");
			fRoleGroup.setParent(userAdministrationBusinessSection);
			fRoleGroup.setModificationDate(new Date());
			entityManager.persist(fRoleGroup);

			userAdministrationBusinessSection.getChildren().add(fRoleGroup);

			entityManager.merge(userAdministrationBusinessSection);

		}
		else
			fRoleGroup = fRoleGroups.get(0);

		Query query6 = entityManager.createQuery("select f from FRole f");
		@SuppressWarnings("unchecked")
		List<FRole> fRoles = query6.getResultList();

		if (fRoles.size() == 0) {
			FRole fRole = new FRole();
			fRole.setName("Admin Role");
			fRole.setDescription("This role defines super users. A user assigned to this role can make everything!");
			fRole.setParent(fRoleGroup);
			fRole.setModificationDate(new Date());
			entityManager.persist(fRole);
		}

		Query query7 = entityManager.createQuery("select f from FUserGroup f");
		@SuppressWarnings("unchecked")
		List<FUserGroup> fUserGroups = query7.getResultList();

		FUserGroup fUserGroup = null;

		if (fUserGroups.size() == 0) {
			fUserGroup = new FUserGroup();
			fUserGroup.setName("Users");
			fUserGroup.setParent(userAdministrationBusinessSection);
			fUserGroup.setModificationDate(new Date());
			entityManager.persist(fUserGroup);

			userAdministrationBusinessSection.getChildren().add(fUserGroup);

			entityManager.merge(userAdministrationBusinessSection);
		}
		else
			fUserGroup = fUserGroups.get(0);

		Query query8 = entityManager.createQuery("select f from FUser f");
		@SuppressWarnings("unchecked")
		List<FUser> fUsers = query8.getResultList();

		if (fUsers.size() == 0) {
			Query query9 = entityManager.createQuery("select f from FRole f where f.name='Admin Role'");

			FUser fUser = new FUser();
			fUser.setName("Admin");
			fUser.setTreePanelWidth(300);
			fUser.setMainPanelPanelHeight(600);
			fUser.setParent(fUserGroup);
			try {

				fUser.setPlainPassword("admin");
				fUser.setModificationDate(new Date());
				fUser.getAssignedRoles().add((FRole) query9.getResultList().get(0));
				entityManager.persist(fUser);
			}
			catch (Exception e) {

				log.error("Bug", e);
			}

		}

		Query query10 = entityManager.createQuery("select c from CounterpartyGroup c");
		@SuppressWarnings("unchecked")
		List<Counterparty> counterparties = query10.getResultList();

		if (counterparties.size() == 0) {

			CounterpartyGroup counterpartyGroup = new CounterpartyGroup();
			counterpartyGroup.setName("Counterparties");
			counterpartyGroup.setParent(masterDataBusinessSection);
			counterpartyGroup.setDescription("This group contains all counterparties.");
			counterpartyGroup.setModificationDate(new Date());

			entityManager.persist(counterpartyGroup);

			masterDataBusinessSection.getChildren().add(counterpartyGroup);

			entityManager.merge(masterDataBusinessSection);
		}

		Query query11 = entityManager.createQuery("select f from FSecurityGroup f where f.name='Securities'");
		@SuppressWarnings("unchecked")
		List<FSecurityGroup> securityGroups = query11.getResultList();

		if (securityGroups.size() == 0) {

			FSecurityGroup securityGroup = new FSecurityGroup();
			securityGroup.setName("Securities");
			securityGroup.setParent(masterDataBusinessSection);
			securityGroup.setDescription("This group contains all securities.");
			securityGroup.setModificationDate(new Date());

			entityManager.persist(securityGroup);

			masterDataBusinessSection.getChildren().add(securityGroup);

			entityManager.merge(masterDataBusinessSection);

		}

		Query query12 = entityManager.createQuery("select b from BankCalendarGroup b");
		@SuppressWarnings("unchecked")
		List<BankCalendarGroup> bankCalendarGroups = query12.getResultList();

		BankCalendarGroup bankCalendarGroup = null;

		if (bankCalendarGroups.size() == 0) {
			bankCalendarGroup = new BankCalendarGroup();
			bankCalendarGroup.setName("Bank Calendars");
			bankCalendarGroup.setParent(administrationBusinessSection);
			bankCalendarGroup.setDescription("This group contains all bank calendars.");
			bankCalendarGroup.setModificationDate(new Date());

			entityManager.persist(bankCalendarGroup);

			administrationBusinessSection.getChildren().add(bankCalendarGroup);

			entityManager.merge(administrationBusinessSection);

		}
		else
			bankCalendarGroup = bankCalendarGroups.get(0);

		checkBankCalendar("Argentina (Buenos Aires Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Australia", bankCalendarGroup);
		checkBankCalendar("Brazil (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("Brazil (Bolsa de Valores de Sao Paulo)", bankCalendarGroup);
		checkBankCalendar("Canada (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("Canada (Toronto Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("China (Shanghai Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Czech Republic (Prague Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Denmark", bankCalendarGroup);
		checkBankCalendar("Finland", bankCalendarGroup);
		checkBankCalendar("Germany (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("Germany (Eurex)", bankCalendarGroup);
		checkBankCalendar("Germany (Frankfurt Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Germany (Xetra)", bankCalendarGroup);
		checkBankCalendar("Hong Kong (Hong Kong Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Hungary", bankCalendarGroup);
		checkBankCalendar("Iceland (Iceland Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("India (National Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Indonesia (Jakarta Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Italy (Milan Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("Italy (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("Japan", bankCalendarGroup);
		checkBankCalendar("Mexico", bankCalendarGroup);
		checkBankCalendar("New Zealand", bankCalendarGroup);
		checkBankCalendar("Norway", bankCalendarGroup);
		checkBankCalendar("Poland", bankCalendarGroup);
		checkBankCalendar("Saudi Arabia (Tadawul Financial Market)", bankCalendarGroup);
		checkBankCalendar("Singapore", bankCalendarGroup);
		checkBankCalendar("Slovakia (Bratislava Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("South Africa", bankCalendarGroup);
		checkBankCalendar("South Korea (Korea Exchange)", bankCalendarGroup);
		checkBankCalendar("South Korea (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("Sweden", bankCalendarGroup);
		checkBankCalendar("Switzerland", bankCalendarGroup);
		checkBankCalendar("Taiwan", bankCalendarGroup);
		checkBankCalendar("Target", bankCalendarGroup);
		checkBankCalendar("Turkey", bankCalendarGroup);
		checkBankCalendar("Ukraine", bankCalendarGroup);
		checkBankCalendar("United Kingdom (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("United Kingdom (London Stock Exchange)", bankCalendarGroup);
		checkBankCalendar("United Kingdom (London Metals Exchange)", bankCalendarGroup);
		checkBankCalendar("United States (Generic Settlement)", bankCalendarGroup);
		checkBankCalendar("United States (Government Bond Market)", bankCalendarGroup);
		checkBankCalendar("United States (North American Energy Reliability Council)", bankCalendarGroup);
		checkBankCalendar("United States (New York Stock Exchange)", bankCalendarGroup);

		Query query13 = entityManager.createQuery("select f from FIXAcceptorGroup f");
		@SuppressWarnings("unchecked")
		List<FIXAcceptorGroup> fixAcceptorGroups = query13.getResultList();

		if (fixAcceptorGroups.size() == 0) {

			FIXAcceptorGroup fixAcceptorGroup = new FIXAcceptorGroup();
			fixAcceptorGroup.setName("FIX Acceptors");
			fixAcceptorGroup.setParent(marketInterfacesBusinessSection);
			fixAcceptorGroup.setDescription("This group contains all fix acceptors.");
			fixAcceptorGroup.setModificationDate(new Date());

			entityManager.persist(fixAcceptorGroup);

			marketInterfacesBusinessSection.getChildren().add(fixAcceptorGroup);

			entityManager.merge(marketInterfacesBusinessSection);

		}

		Query query14 = entityManager.createQuery("select f from FIXInitiatorGroup f");
		@SuppressWarnings("unchecked")
		List<FIXInitiatorGroup> fixInitiatorGroups = query14.getResultList();

		if (fixInitiatorGroups.size() == 0) {

			FIXInitiatorGroup fixInitiatorGroup = new FIXInitiatorGroup();
			fixInitiatorGroup.setName("FIX Initiators");
			fixInitiatorGroup.setParent(marketInterfacesBusinessSection);
			fixInitiatorGroup.setDescription("This group contains all fix initiators.");
			fixInitiatorGroup.setModificationDate(new Date());

			entityManager.persist(fixInitiatorGroup);

			marketInterfacesBusinessSection.getChildren().add(fixInitiatorGroup);

			entityManager.merge(marketInterfacesBusinessSection);

		}

		Query query15 = entityManager.createQuery("select s from SpreadSheetGroup s");
		@SuppressWarnings("unchecked")
		List<SpreadSheetGroup> spreadSheetGroups = query15.getResultList();

		if (spreadSheetGroups.size() == 0) {
			SpreadSheetGroup spreadSheetGroup = new SpreadSheetGroup();
			spreadSheetGroup.setName("Spreadsheets");
			spreadSheetGroup.setDescription("This group contains all spreadsheets.");
			spreadSheetGroup.setModificationDate(new Date());
			entityManager.persist(spreadSheetGroup);
		}

		Query query16 = entityManager.createQuery("select f from FSecurityGroup f where f.name='Imported Securities'");
		@SuppressWarnings("unchecked")
		List<FSecurityGroup> importedSecurityGroups = query16.getResultList();

		if (importedSecurityGroups.size() == 0) {

			FSecurityGroup securityGroup = new FSecurityGroup();
			securityGroup.setName("Imported Securities");
			securityGroup.setParent(masterDataBusinessSection);
			securityGroup.setDescription("This group contains all imported securities.");
			securityGroup.setModificationDate(new Date());

			entityManager.persist(securityGroup);

			masterDataBusinessSection.getChildren().add(securityGroup);

			entityManager.merge(masterDataBusinessSection);

		}

		tx.commit();

	}

	private void checkBankCalendar(String country, BankCalendarGroup bankCalendarGroup) {

		Query query = entityManager.createQuery("select b from BankCalendar b where b.name='" + country + "'");

		if (query.getResultList().size() == 0) {

			BankCalendar bankCalendar = new BankCalendar();
			bankCalendar.setName(country);
			bankCalendar.setModificationDate(new Date());
			bankCalendar.setParent(bankCalendarGroup);
			entityManager.persist(bankCalendar);
		}

	}

	/**
	 * Sets the basis client acceptor.
	 *
	 * @param fBasisClientAcceptor the new basis client acceptor
	 */
	public void setBasisClientAcceptor(FBasisClientAcceptor fBasisClientAcceptor) {

		this.basisClientAcceptor = fBasisClientAcceptor;
		fBasisClientAcceptor.setBasisPersistenceHandler(this);
		Thread thread = new Thread() {

			public void run() {

				handleResponses();
			}
		};
		thread.start();

		Thread thread2 = new Thread() {

			public void run() {

				while (!closed) {
					try {
						LogEntry logEntry = logEntryQueue.take();
						List<LogEntry> logEntries = new ArrayList<LogEntry>();
						logEntries.add(logEntry);
						synchronized (logEntryQueue) {
							logEntryQueue.drainTo(logEntries);
							logEntryQueue.clear();
						}
						processLogEntries(logEntries);
					}
					catch (InterruptedException e) {
						log.error("Bug", e);
					}
				}
			}
		};
		thread2.start();
	}

	/**
	 * Process log entries.
	 *
	 * @param logEntries the log entries
	 */
	public synchronized void processLogEntries(List<LogEntry> logEntries) {

		EntityTransaction tx = entityManager.getTransaction();

		try {
			tx.begin();

			for (LogEntry logEntry : logEntries) {

				if (logEntry.getMessageText() == null)
					logEntry.setMessageText("No details available.");
				entityManager.persist(logEntry);
				send(new LogEntryResponse(logEntry));
			}

			tx.commit();

		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
	}

	/**
	 * Write log entry.
	 *
	 * @param logEntry the log entry
	 */
	public void writeLogEntry(LogEntry logEntry) {

		synchronized (logEntryQueue) {
			logEntryQueue.add(logEntry);
		}
	}

	/**
	 * Name check.
	 *
	 * @param uniqueNameRequest the unique name request
	 * @return true, if successful
	 */
	public synchronized boolean nameCheck(UniqueNameRequest uniqueNameRequest) {

		boolean unique = true;

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			Query query = entityManager.createNativeQuery("select * from abstractbusinessobject where name='" + uniqueNameRequest.getName().trim()
					+ "' and not id = " + uniqueNameRequest.getId());
			if (query.getResultList().size() > 0)
				unique = false;
			tx.commit();

		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}

		return unique;

	}

	/**
	 * Security id check.
	 *
	 * @param uniqueSecurityIDRequest the unique security id request
	 * @return true, if successful
	 */
	public synchronized boolean securityIDCheck(UniqueSecurityIDRequest uniqueSecurityIDRequest) {

		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			boolean unique = true;
			Query query = entityManager.createNativeQuery("select * from fsecurity where securityID='" + uniqueSecurityIDRequest.getName().trim()
					+ "' and not id = " + uniqueSecurityIDRequest.getId());
			if (query.getResultList().size() > 0)
				unique = false;
			tx.commit();

			return unique;
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			return false;
		}

	}

	/**
	 * Update.
	 *
	 * @param abstractBusinessObjects the abstract business objects
	 * @param channel the channel
	 * @param transientValueSetter the transient value setter
	 * @param writeLogEntry the write log entry
	 * @return the list
	 */
	public synchronized List<AbstractBusinessObject> update(List<AbstractBusinessObject> abstractBusinessObjects, Channel channel,
			TransientValueSetter transientValueSetter, boolean writeLogEntry) {

		List<AbstractBusinessObject> updatedAbstractBusinessObjects = new ArrayList<AbstractBusinessObject>();
		for (AbstractBusinessObject abstractBusinessObject : abstractBusinessObjects) {

			EntityTransaction tx = entityManager.getTransaction();

			try {
				tx.begin();
				if (channel != null)
					abstractBusinessObject.setModificationUser(userMap.get(channel));
				abstractBusinessObject.setModificationDate(new Date());

				abstractBusinessObject = entityManager.merge(abstractBusinessObject);

				abstractBusinessObject.makeEager();

				tx.commit();

				abstractBusinessObject.setTransientValues(transientValueSetter);
				updatedAbstractBusinessObjects.add(abstractBusinessObject);
				if (writeLogEntry) {
					LogEntry logEntry = new LogEntry();
					logEntry.setLogDate(new Date());
					logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
					if (channel != null)
						logEntry.setMessageText("User " + userMap.get(channel) + " modified " + abstractBusinessObject.getBusinessObjectName().toLowerCase()
								+ " " + abstractBusinessObject + ".");
					else
						logEntry.setMessageText("System modified " + abstractBusinessObject.getBusinessObjectName().toLowerCase() + " "
								+ abstractBusinessObject + ".");
					logEntry.setMessageComponent("Basis");
					writeLogEntry(logEntry);
				}

			}
			catch (Exception e) {

				log.error("Bug", e);

				if (tx.isActive())
					tx.rollback();

				// generated ids of security details are making trouble if i do
				// not close the entity manager
				entityManager.close();
				entityManager = entityManagerFactory.createEntityManager();
			}
		}
		if (basisClientAcceptor != null && updatedAbstractBusinessObjects.size() > 0)
			basisClientAcceptor.send(new UpdateResponse(updatedAbstractBusinessObjects));
		return updatedAbstractBusinessObjects;
	}

	/**
	 * Sets the user.
	 *
	 * @param fUser the f user
	 * @param session the session
	 * @param transientValueSetter the transient value setter
	 * @return true, if successful
	 */
	public synchronized boolean setUser(FUser fUser, Channel session, TransientValueSetter transientValueSetter) {

		if (userMap.values().contains(fUser))
			return false;
		userMap.put(session, fUser);
		fUser.setTransientValues(transientValueSetter);
		basisClientAcceptor.send(new UpdateResponse(fUser));
		return true;
	}

	/**
	 * Checks if is user online.
	 *
	 * @param fUser the f user
	 * @return true, if is user online
	 */
	public synchronized boolean isUserOnline(FUser fUser) {

		return userMap.values().contains(fUser);
	}

	/**
	 * Removes the session.
	 *
	 * @param channel the channel
	 * @param transientValueSetter the transient value setter
	 */
	public synchronized void removeSession(Channel channel, TransientValueSetter transientValueSetter) {

		FUser fUser = userMap.remove(channel);
		if (fUser != null) {
			fUser.setTransientValues(transientValueSetter);
			basisClientAcceptor.send(new UpdateResponse(fUser));
		}
	}

	/**
	 * Removes the.
	 *
	 * @param abstractBusinessObjects the abstract business objects
	 * @param channel the channel
	 * @return the sets the
	 */
	public synchronized Set<AbstractBusinessObject> remove(Set<AbstractBusinessObject> abstractBusinessObjects, Channel channel) {

		Set<AbstractBusinessObject> updatedAbstractBusinessObjects = new HashSet<AbstractBusinessObject>();

		Set<AbstractBusinessObject> removedAbstractBusinessObjects = new HashSet<AbstractBusinessObject>();

		for (AbstractBusinessObject abstractBusinessObject : abstractBusinessObjects) {
			Set<AbstractBusinessObject> updatedAbstractBusinessObjects2 = new HashSet<AbstractBusinessObject>();

			EntityTransaction tx = entityManager.getTransaction();
			tx.begin();

			try {
				Query query = entityManager.createQuery("select a from AbstractBusinessObject a where a.parent.id=?1");
				query.setParameter(1, abstractBusinessObject.getId());
				for (Object object : query.getResultList()) {
					AbstractBusinessObject abstractBusinessObject2 = (AbstractBusinessObject) object;

					if (abstractBusinessObject2.isMovable()) {
						abstractBusinessObject2.setParent(abstractBusinessObject.getParent());
						entityManager.merge(abstractBusinessObject2);
						updatedAbstractBusinessObjects2.add(abstractBusinessObject2);
					}
					else {
						entityManager.remove(abstractBusinessObject2);
						entityManager.flush();
						removedAbstractBusinessObjects.add(abstractBusinessObject2);
					}
				}
				abstractBusinessObject = entityManager.find(AbstractBusinessObject.class, abstractBusinessObject.getId());
				entityManager.remove(abstractBusinessObject);
				entityManager.flush();

				if (abstractBusinessObject instanceof SpreadSheet) {
					Query query2 = entityManager.createQuery("select a from SpreadSheetCellContent a where a.spreadSheet=?1");
					query2.setParameter(1, abstractBusinessObject.getId());
					for (Object object : query2.getResultList())
						entityManager.remove(object);

					query2 = entityManager.createQuery("select a from SpreadSheetCellFormat a where a.spreadSheet=?1");
					query2.setParameter(1, abstractBusinessObject.getId());
					for (Object object : query2.getResultList())
						entityManager.remove(object);

					query2 = entityManager.createQuery("select a from SpreadSheetColumnFormat a where a.spreadSheet=?1");
					query2.setParameter(1, abstractBusinessObject.getId());
					for (Object object : query2.getResultList())
						entityManager.remove(object);

					query2 = entityManager.createQuery("select a from SpreadSheetConditionalFormat a where a.spreadSheet=?1");
					query2.setParameter(1, abstractBusinessObject.getId());
					for (Object object : query2.getResultList())
						entityManager.remove(object);

					query2 = entityManager.createQuery("select a from SpreadSheetRowFormat a where a.spreadSheet=?1");
					query2.setParameter(1, abstractBusinessObject.getId());
					for (Object object : query2.getResultList())
						entityManager.remove(object);
				}
				entityManager.flush();
				tx.commit();

				updatedAbstractBusinessObjects.addAll(updatedAbstractBusinessObjects2);
				removedAbstractBusinessObjects.add(abstractBusinessObject);
			}
			catch (Exception e) {

				log.error("Bug", e);

				if (tx.isActive())
					tx.rollback();

				// generated ids of security details are making trouble if i do
				// not
				// close the entity manager
				entityManager.close();
				entityManager = entityManagerFactory.createEntityManager();
			}

		}

		updatedAbstractBusinessObjects.removeAll(removedAbstractBusinessObjects);
		for (AbstractBusinessObject abstractBusinessObject : updatedAbstractBusinessObjects) {
			basisClientAcceptor.send(new UpdateResponse(abstractBusinessObject));
			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
			logEntry.setMessageText("User " + userMap.get(channel) + " modified " + abstractBusinessObject.getBusinessObjectName().toLowerCase() + " "
					+ abstractBusinessObject + ".");
			logEntry.setMessageComponent("Basis");
			writeLogEntry(logEntry);
		}
		for (AbstractBusinessObject abstractBusinessObject : removedAbstractBusinessObjects) {
			basisClientAcceptor.send(new RemoveResponse(abstractBusinessObject));
			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
			logEntry.setMessageText("User " + userMap.get(channel) + " removed " + abstractBusinessObject.getBusinessObjectName().toLowerCase() + " "
					+ abstractBusinessObject + ".");
			logEntry.setMessageComponent("Basis");
			writeLogEntry(logEntry);
		}
		return removedAbstractBusinessObjects;
	}

	/**
	 * Gets the log entries.
	 *
	 * @return the log entries
	 */
	public synchronized List<LogEntry> getLogEntries() {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Query query = entityManager.createQuery("select l from LogEntry l where l.logDate>?1 order by l.logDate desc");
			query.setParameter(1, calendar.getTime());
			@SuppressWarnings("unchecked")
			List<LogEntry> logEntries = query.getResultList();
			tx.commit();

			return logEntries;
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			// entityManager = entityManagerFactory.createEntityManager();
			return new ArrayList<LogEntry>();
		}
	}

	/**
	 * Update spread sheet cell content.
	 *
	 * @param sheetCellContents the sheet cell contents
	 */
	public synchronized void updateSpreadSheetCellContent(List<SpreadSheetCellContent> sheetCellContents) {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			for (SpreadSheetCellContent spreadSheetCellContent : sheetCellContents) {

				if (spreadSheetCellContent.getId() > 0) {
					if (spreadSheetCellContent.getSpreadSheetCellType() != SpreadSheetCellType.CLEAR) {
						entityManager.merge(spreadSheetCellContent);
					}
					else {
						spreadSheetCellContent = entityManager.find(SpreadSheetCellContent.class, spreadSheetCellContent.getId());
						if (spreadSheetCellContent != null) {
							entityManager.remove(spreadSheetCellContent);
						}
					}
				}
				else if (spreadSheetCellContent.getSpreadSheetCellType() != SpreadSheetCellType.CLEAR)
					entityManager.persist(spreadSheetCellContent);

				// entityManager.detach(spreadSheetCellContent);
			}

			tx.commit();
			entityManager.clear();

		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			// entityManager = entityManagerFactory.createEntityManager();
		}
	}

	/**
	 * Gets the spread sheet cell contents.
	 *
	 * @param id the id
	 * @return the spread sheet cell contents
	 */
	public synchronized Set<SpreadSheetCellContent> getSpreadSheetCellContents(long id) {

		Set<SpreadSheetCellContent> spreadSheetCellContents = new HashSet<SpreadSheetCellContent>();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			Query query = entityManager.createQuery("select a from SpreadSheetCellContent a where a.spreadSheet=?1");
			query.setParameter(1, id);
			for (Object object : query.getResultList())
				if (object instanceof SpreadSheetCellContent) {
					SpreadSheetCellContent spreadSheetCellContent = (SpreadSheetCellContent) object;
					if (spreadSheetCellContents.contains(spreadSheetCellContent)) {
						entityManager.remove(spreadSheetCellContent);
						log.warn("Double entry for cell content of "+CellReference.convertNumToColString(spreadSheetCellContent.getCellColumn()+1)+(spreadSheetCellContent.getCellRow()+1)+" spreadsheet id=" + id + " found. Removed entry!!!");
					}
					else {
						spreadSheetCellContents.add(spreadSheetCellContent);
						entityManager.detach(spreadSheetCellContent);
					}
				}
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		return spreadSheetCellContents;
	}

	/**
	 * Update spread sheet cell format.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public synchronized void updateSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		EntityTransaction tx = entityManager.getTransaction();

		try {
			tx.begin();
			if (spreadSheetCellFormat.getId() > 0) {
				if (spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.CLEAR) {
					entityManager.merge(spreadSheetCellFormat);
				}
				else {
					spreadSheetCellFormat = entityManager.find(SpreadSheetCellFormat.class, spreadSheetCellFormat.getId());
					if (spreadSheetCellFormat != null) {
						entityManager.remove(spreadSheetCellFormat);
					}
				}
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.CLEAR) {
				entityManager.persist(spreadSheetCellFormat);
			}
			tx.commit();
			entityManager.clear();

		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
	}

	/**
	 * Gets the spread sheet cell formats.
	 *
	 * @param id the id
	 * @return the spread sheet cell formats
	 */
	public synchronized Set<SpreadSheetCellFormat> getSpreadSheetCellFormats(long id) {

		Set<SpreadSheetCellFormat> spreadSheetCellFormats = new HashSet<SpreadSheetCellFormat>();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			Query query = entityManager.createQuery("select a from SpreadSheetCellFormat a where a.spreadSheet=?1");
			query.setParameter(1, id);
			for (Object object : query.getResultList())
				if (object instanceof SpreadSheetCellFormat) {
					SpreadSheetCellFormat spreadSheetCellFormat = (SpreadSheetCellFormat) object;
					if (spreadSheetCellFormats.contains(spreadSheetCellFormat)) {
						entityManager.remove(spreadSheetCellFormat);
						log.warn("Double entry for cell format of spreadsheet id=" + id + " found. Removed entry!!!");

					}
					else {
						spreadSheetCellFormat.getPredefinedStrings().size();
						spreadSheetCellFormats.add(spreadSheetCellFormat);
						entityManager.detach(spreadSheetCellFormats);
					}
				}
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		return spreadSheetCellFormats;
	}

	/**
	 * Update spread sheet column format.
	 *
	 * @param spreadSheetColumnFormat the spread sheet column format
	 */
	public synchronized void updateSpreadSheetColumnFormat(SpreadSheetColumnFormat spreadSheetColumnFormat) {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			if (spreadSheetColumnFormat.getId() > 0) {
				if (spreadSheetColumnFormat.getColumnNumber() == -1) {
					spreadSheetColumnFormat = entityManager.find(SpreadSheetColumnFormat.class, spreadSheetColumnFormat.getId());
					if (spreadSheetColumnFormat != null)
						entityManager.remove(spreadSheetColumnFormat);
				}
				else
					entityManager.merge(spreadSheetColumnFormat);
			}
			else
				entityManager.persist(spreadSheetColumnFormat);
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
	}

	/**
	 * Gets the spread sheet column formats.
	 *
	 * @param id the id
	 * @return the spread sheet column formats
	 */
	public synchronized Set<SpreadSheetColumnFormat> getSpreadSheetColumnFormats(long id) {

		Set<SpreadSheetColumnFormat> spreadSheetColumnFormats = new HashSet<SpreadSheetColumnFormat>();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			Query query = entityManager.createQuery("select a from SpreadSheetColumnFormat a where a.spreadSheet=?1");
			query.setParameter(1, id);
			for (Object object : query.getResultList())
				if (object instanceof SpreadSheetColumnFormat) {
					SpreadSheetColumnFormat spreadSheetColumnFormat = (SpreadSheetColumnFormat) object;
					if (spreadSheetColumnFormats.contains(spreadSheetColumnFormat)) {
						entityManager.remove(spreadSheetColumnFormat);
						log.warn("Double entry for column format of spreadsheet id=" + id + " found. Removed entry!!!");
					}
					else {
						spreadSheetColumnFormats.add(spreadSheetColumnFormat);
						entityManager.detach(spreadSheetColumnFormat);
					}
				}
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		return spreadSheetColumnFormats;
	}

	/**
	 * Update spread sheet row format.
	 *
	 * @param spreadSheetRowFormat the spread sheet row format
	 */
	public synchronized void updateSpreadSheetRowFormat(SpreadSheetRowFormat spreadSheetRowFormat) {

		EntityTransaction tx = entityManager.getTransaction();

		try {
			tx.begin();
			if (spreadSheetRowFormat.getId() > 0) {
				if (spreadSheetRowFormat.getRowNumber() == -1) {
					spreadSheetRowFormat = entityManager.find(SpreadSheetRowFormat.class, spreadSheetRowFormat.getId());
					if (spreadSheetRowFormat != null)
						entityManager.remove(spreadSheetRowFormat);
				}
				else
					entityManager.merge(spreadSheetRowFormat);
			}
			else
				entityManager.persist(spreadSheetRowFormat);
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();
		}
	}

	/**
	 * Gets the spread sheet row formats.
	 *
	 * @param id the id
	 * @return the spread sheet row formats
	 */
	public synchronized Set<SpreadSheetRowFormat> getSpreadSheetRowFormats(long id) {

		Set<SpreadSheetRowFormat> spreadSheetRowFormats = new HashSet<SpreadSheetRowFormat>();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			Query query = entityManager.createQuery("select a from SpreadSheetRowFormat a where a.spreadSheet=?1");
			query.setParameter(1, id);
			for (Object object : query.getResultList())
				if (object instanceof SpreadSheetRowFormat) {
					SpreadSheetRowFormat spreadSheetRowFormat = (SpreadSheetRowFormat) object;
					if (spreadSheetRowFormats.contains(spreadSheetRowFormat)) {
						entityManager.remove(spreadSheetRowFormat);
						log.warn("Double entry for row format of spreadsheet id=" + id + " found. Removed entry!!!");
					}
					else {
						spreadSheetRowFormats.add(spreadSheetRowFormat);
					}
				}
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		return spreadSheetRowFormats;
	}

	/**
	 * Update spread sheet conditional format.
	 *
	 * @param spreadSheetConditionalFormat the spread sheet conditional format
	 */
	public synchronized void updateSpreadSheetConditionalFormat(SpreadSheetConditionalFormat spreadSheetConditionalFormat) {

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			if (spreadSheetConditionalFormat.getId() > 0) {
				if (spreadSheetConditionalFormat.getSpreadSheetCondition1() == null && spreadSheetConditionalFormat.getSpreadSheetCondition2() == null
						&& spreadSheetConditionalFormat.getSpreadSheetCondition3() == null) {
					spreadSheetConditionalFormat = entityManager.find(SpreadSheetConditionalFormat.class, spreadSheetConditionalFormat.getId());
					if (spreadSheetConditionalFormat != null)
						entityManager.remove(spreadSheetConditionalFormat);
				}
				else {
					entityManager.merge(spreadSheetConditionalFormat);
				}

			}
			else if (spreadSheetConditionalFormat.getSpreadSheetCondition1() != null || spreadSheetConditionalFormat.getSpreadSheetCondition2() != null
					|| spreadSheetConditionalFormat.getSpreadSheetCondition3() != null)
				entityManager.persist(spreadSheetConditionalFormat);
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
	}

	/**
	 * Gets the spread sheet conditional formats.
	 *
	 * @param id the id
	 * @return the spread sheet conditional formats
	 */
	public synchronized Set<SpreadSheetConditionalFormat> getSpreadSheetConditionalFormats(long id) {

		Set<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new HashSet<SpreadSheetConditionalFormat>();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		try {
			Query query = entityManager.createQuery("select a from SpreadSheetConditionalFormat a where a.spreadSheet=?1");
			query.setParameter(1, id);
			for (Object object : query.getResultList())
				if (object instanceof SpreadSheetConditionalFormat) {
					SpreadSheetConditionalFormat spreadSheetConditionalFormat = (SpreadSheetConditionalFormat) object;
					if (spreadSheetConditionalFormats.contains(spreadSheetConditionalFormat)) {
						entityManager.remove(spreadSheetConditionalFormat);
						log.warn("Double entry for conditional format of spreadsheet id=" + id + " found. Removed entry!!!");
					}
					else {
						spreadSheetConditionalFormat.getSpreadSheetCondition1();
						spreadSheetConditionalFormat.getSpreadSheetCondition2();
						spreadSheetConditionalFormat.getSpreadSheetCondition3();
						spreadSheetConditionalFormats.add(spreadSheetConditionalFormat);
						entityManager.detach(spreadSheetConditionalFormats);
					}
				}
			tx.commit();
			entityManager.clear();
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();
		}
		return spreadSheetConditionalFormats;
	}

	/**
	 * Gets the user.
	 *
	 * @param channel the channel
	 * @return the user
	 */
	public FUser getUser(Channel channel) {

		return userMap.get(channel);
	}

	/**
	 * Gets the id.
	 *
	 * @param name the name
	 * @return the id
	 */
	public synchronized long getId(final String name) {

		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		try {

			Query query = entityManager.createQuery("select i from IDManager i where i.name = ?1", IDManager.class);

			query.setParameter(1, name);

			IDManager idManager = null;

			@SuppressWarnings("unchecked")
			final List<IDManager> resultList = query.getResultList();

			if (resultList.size() > 0) {
				idManager = (IDManager) resultList.get(0);

			}

			if (idManager == null) {

				idManager = new IDManager();
				idManager.setName(name);
				idManager.setLastID(0L);
				entityManager.persist(idManager);
			}

			long id = idManager.getLastID() + 1;

			idManager.setLastID(id);

			idManager = entityManager.merge(idManager);

			tx.commit();

			return id;
		}
		catch (Exception e) {

			log.error("Bug", e);

			if (tx.isActive())
				tx.rollback();

			// generated ids of security details are making trouble if i do not
			// close the entity manager
			entityManager.close();
			entityManager = entityManagerFactory.createEntityManager();

			return 0;
		}
	}

	/**
	 * Send.
	 *
	 * @param abstractResponse the abstract response
	 * @param channel the channel
	 */
	public void send(AbstractResponse abstractResponse, Channel channel) {

		abstractResponsesQueue.add(new ResponseSet(abstractResponse, channel));
	}

	/**
	 * Send.
	 *
	 * @param abstractResponse the abstract response
	 */
	public void send(AbstractResponse abstractResponse) {

		abstractResponsesQueue.add(new ResponseSet(abstractResponse, null));

	}

}
