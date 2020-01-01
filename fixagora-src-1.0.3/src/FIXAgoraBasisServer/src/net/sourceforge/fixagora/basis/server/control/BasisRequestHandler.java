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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests;
import net.sourceforge.fixagora.basis.shared.model.communication.ChildrenBusinessObjectRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ChildrenBusinessObjectResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.CloseSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CloseSpreadSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.CopySpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CopySpreadSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionariesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionariesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertRowResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse.LoginStatus;
import net.sourceforge.fixagora.basis.shared.model.communication.LogoffRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.LogoffResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.OpenSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.OpenSpreadSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RootBusinessObjectRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RootBusinessObjectResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.SolverRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse.UpdateStatus;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class BasisRequestHandler.
 */
public class BasisRequestHandler implements BasisRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private List<DataDictionary> dataDictionaries = new ArrayList<DataDictionary>();

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(BasisRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		
		this.basisPersistenceHandler = basisPersistenceHandler;

		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();

			File folder = new File("conf");
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					try {
						Document dataDictionaryDocument = builder.parse(listOfFiles[i]);

						final NodeList xmlFixRoot = dataDictionaryDocument.getElementsByTagName("fix");

						for (int j = 0; j < xmlFixRoot.getLength(); j++) {

							final Element xmlFixElement = (Element) xmlFixRoot.item(j);
							if (xmlFixElement.getAttribute("type") == null || !xmlFixElement.getAttribute("type").equals("FIXT")) {
								FileReader inputFileReader = new FileReader(listOfFiles[i]);
								String inLine = null;
								BufferedReader inputStream = new BufferedReader(inputFileReader);
								StringBuffer stringBuffer = new StringBuffer();
								while ((inLine = inputStream.readLine()) != null)
									stringBuffer.append(inLine);

								ByteArrayOutputStream out = new ByteArrayOutputStream();
								GZIPOutputStream gzip = new GZIPOutputStream(out);
								gzip.write(stringBuffer.toString().getBytes());
								gzip.finish();
								gzip.close();

								dataDictionaries.add(new DataDictionary(listOfFiles[i].getName(), stringBuffer, out.toByteArray()));
								inputStream.close();
							}
						}
					}
					catch (SAXException e) {
					}
					catch (IOException e) {
					}
				}
			}
		}
		catch (ParserConfigurationException e) {
			log.error("Bug", e);
		}

		businessComponentHandler = new BusinessComponentHandler(basisPersistenceHandler, dataDictionaries);

		Collections.sort(dataDictionaries);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onLoginRequest(net.sourceforge.fixagora.basis.shared.model.communication.LoginRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onLoginRequest(LoginRequest loginRequest, Channel channel) {

		try {
						
			FUser fUser = loginRequest.getFUser();

			FUser fUser2 = basisPersistenceHandler.findByName(FUser.class, fUser.getName(), businessComponentHandler);

			if (fUser2 == null)
			{			
				basisPersistenceHandler.send(new LoginResponse(loginRequest, fUser, LoginStatus.FAILED, "Login failed: Unknown user", new byte[0]),channel);
			}
			else if (fUser.getFPassword().equals(fUser2.getFPassword()) && basisPersistenceHandler.setUser(fUser2, channel, businessComponentHandler)) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				List<LogEntry> logEntries = basisPersistenceHandler.getLogEntries();

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(logEntries);
				objectOutputStream.close();
				byteArrayOutputStream.close();

				ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream2);
				gzip.write(byteArrayOutputStream.toByteArray());
				gzip.finish();
				gzip.close();
				
				basisPersistenceHandler.send(new LoginResponse(loginRequest, fUser2, LoginStatus.SUCCESS, null, byteArrayOutputStream2.toByteArray()),channel);

				LogEntry logEntry = new LogEntry();
				logEntry.setLogDate(new Date());
				logEntry.setLogLevel(Level.INFO);
				logEntry.setMessageComponent("Basis");
				logEntry.setMessageText("User " + fUser2.getName() + " logged in.");
				basisPersistenceHandler.writeLogEntry(logEntry);
			}
			else {

				String text = "Login failed: Incorrect password";
				if (basisPersistenceHandler.isUserOnline(fUser2))
					text = "Login failed: " + fUser2.getName() + " already logged in";
				basisPersistenceHandler.send(new LoginResponse(loginRequest, fUser, LoginStatus.FAILED, text, new byte[0]),channel);
			}
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(loginRequest),channel);
		}
	}

	private void writeLogEntry(Exception exception) {

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(Level.INFO);
		logEntry.setMessageComponent("Basis");
		logEntry.setMessageText(exception.getMessage());
		basisPersistenceHandler.writeLogEntry(logEntry);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onRootBusinessObjectRequest(net.sourceforge.fixagora.basis.shared.model.communication.RootBusinessObjectRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onRootBusinessObjectRequest(RootBusinessObjectRequest rootBusinessObjectRequest, Channel channel) {
		
		try {
			List<AbstractBusinessObject> abstractBusinessObjects = basisPersistenceHandler.executeQuery(AbstractBusinessObject.class,
					"select a from AbstractBusinessObject a where a.parent=null", businessComponentHandler, true);
			Collections.sort(abstractBusinessObjects);
			basisPersistenceHandler.send(new RootBusinessObjectResponse(rootBusinessObjectRequest, abstractBusinessObjects),channel);

		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(rootBusinessObjectRequest),channel);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onChildrenBusinessObjectRequest(net.sourceforge.fixagora.basis.shared.model.communication.ChildrenBusinessObjectRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onChildrenBusinessObjectRequest(ChildrenBusinessObjectRequest childrenBusinessObjectRequest, Channel channel) {
		
		try {

			Class<?> classIdentifier = Class.forName(childrenBusinessObjectRequest.getClassName());
			
			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) basisPersistenceHandler.findById(classIdentifier, childrenBusinessObjectRequest.getObjectId(), businessComponentHandler);
			
			if (abstractBusinessObject instanceof HibernateProxy) {
								
				abstractBusinessObject = (AbstractBusinessObject)((HibernateProxy)abstractBusinessObject).getHibernateLazyInitializer().getImplementation();
			}
						
			List<AbstractBusinessObject> abstractBusinessObjects = basisPersistenceHandler.executeQuery(AbstractBusinessObject.class, "select a from "
					+ abstractBusinessObject.getClass().getSimpleName() + " a where a.parent.id=?1", abstractBusinessObject.getId(), businessComponentHandler,
					false);
			
			if (abstractBusinessObject instanceof BusinessObjectGroup) {

				BusinessObjectGroup businessObjectGroup = (BusinessObjectGroup) abstractBusinessObject;

				abstractBusinessObjects.addAll(basisPersistenceHandler.executeQuery(businessObjectGroup.getChildClass(), "select a from "
						+ businessObjectGroup.getChildClass().getSimpleName() + " a where a.parent.id=?1", abstractBusinessObject.getId(), businessComponentHandler, true));
				
			}
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(abstractBusinessObjects);
			objectOutputStream.close();
			byteArrayOutputStream.close();

			ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream2);
			gzip.write(byteArrayOutputStream.toByteArray());
			gzip.finish();
			gzip.close();

			ChildrenBusinessObjectResponse childrenBusinessObjectResponse = new ChildrenBusinessObjectResponse(childrenBusinessObjectRequest,
					byteArrayOutputStream2.toByteArray());

			basisPersistenceHandler.send(childrenBusinessObjectResponse,channel);
						

		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(childrenBusinessObjectRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onPersistRequest(net.sourceforge.fixagora.basis.shared.model.communication.PersistRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onPersistRequest(PersistRequest persistRequest, Channel channel) {

		try {
			AbstractBusinessObject abstractBusinessObject = persistRequest.getAbstractBusinessObject();

			basisPersistenceHandler.persist(abstractBusinessObject, channel, businessComponentHandler);
			
			basisPersistenceHandler.send(new PersistResponse(persistRequest, abstractBusinessObject, PersistResponse.PersistStatus.SUCCESS),channel);

			if (abstractBusinessObject instanceof AbstractBusinessComponent)
				businessComponentHandler.init((AbstractBusinessComponent) abstractBusinessObject);

			if (abstractBusinessObject instanceof SpreadSheet)
				businessComponentHandler.getSpreadSheetHandler().addSpreadSheet((SpreadSheet) abstractBusinessObject, true);

			if (abstractBusinessObject instanceof FSecurity)
				businessComponentHandler.addSecurity((FSecurity) abstractBusinessObject);

			if (abstractBusinessObject instanceof Counterparty)
				businessComponentHandler.addCounterparty((Counterparty) abstractBusinessObject);

		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(persistRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onUniqueNameRequest(net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onUniqueNameRequest(UniqueNameRequest uniqueNameRequest, Channel channel) {

		try {
			boolean unique = basisPersistenceHandler.nameCheck(uniqueNameRequest);

			basisPersistenceHandler.send(new UniqueNameResponse(uniqueNameRequest, unique),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(uniqueNameRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onRolesRequest(net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onRolesRequest(RolesRequest rolesRequest, Channel channel) {

		try {
						
			List<FRole> roles = basisPersistenceHandler.executeQuery(FRole.class, "select f from FRole f", businessComponentHandler, true);

			basisPersistenceHandler.send(new RolesResponse(rolesRequest, roles),channel);
			
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(rolesRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onUpdateRequest(net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onUpdateRequest(UpdateRequest updateRequest, Channel channel) {

		try {
			
			List<AbstractBusinessObject> abstractBusinessObjects = basisPersistenceHandler.update(updateRequest.getAbstractBusinessObjects(), channel,
					businessComponentHandler, true);
			
			if (abstractBusinessObjects.size() == 0)
				basisPersistenceHandler.send(new UpdateResponse(updateRequest, abstractBusinessObjects, UpdateStatus.FAILED),channel);
			else if (abstractBusinessObjects.size() != updateRequest.getAbstractBusinessObjects().size())
				basisPersistenceHandler.send(new UpdateResponse(updateRequest, abstractBusinessObjects, UpdateStatus.PARTIALLY_FAILED),channel);
			else
				basisPersistenceHandler.send(new UpdateResponse(updateRequest, abstractBusinessObjects, UpdateStatus.SUCCESS),channel);
			
			for (AbstractBusinessObject abstractBusinessObject : abstractBusinessObjects) {
				if (abstractBusinessObject instanceof SpreadSheet) {
					businessComponentHandler.getSpreadSheetHandler().updateSpreadSheet((SpreadSheet) abstractBusinessObject);
				}
				if (abstractBusinessObject instanceof AbstractBusinessComponent)
					businessComponentHandler.update((AbstractBusinessComponent) abstractBusinessObject);

				if (abstractBusinessObject instanceof FSecurity)
					businessComponentHandler.updateSecurity((FSecurity) abstractBusinessObject);

				if (abstractBusinessObject instanceof Counterparty)
					businessComponentHandler.updateCounterparty((Counterparty) abstractBusinessObject);

			}
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(updateRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onRemoveRequest(net.sourceforge.fixagora.basis.shared.model.communication.RemoveRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onRemoveRequest(RemoveRequest removeRequest, Channel channel) {

		try {
			Set<AbstractBusinessObject> abstractBusinessObjects = removeRequest.getAbstractBusinessObjects();

			for (AbstractBusinessObject abstractBusinessObject : abstractBusinessObjects) {
				businessComponentHandler.close(abstractBusinessObject);
				if (abstractBusinessObject instanceof SpreadSheet)
					businessComponentHandler.getSpreadSheetHandler().removeSpreadSheet((SpreadSheet) abstractBusinessObject);
			}

			Set<AbstractBusinessObject> removedBusinessObjects = basisPersistenceHandler.remove(abstractBusinessObjects, channel);

			for (AbstractBusinessObject abstractBusinessObject : removedBusinessObjects) {
				if (abstractBusinessObject instanceof FSecurity)
					businessComponentHandler.removeSecurity((FSecurity) abstractBusinessObject);

				if (abstractBusinessObject instanceof Counterparty)
					businessComponentHandler.removeCounterparty((Counterparty) abstractBusinessObject);

			}

			basisPersistenceHandler.send(new RemoveResponse(removeRequest, removedBusinessObjects),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(removeRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#handleSessionClosed(org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleSessionClosed(Channel channel) {

		basisPersistenceHandler.removeSession(channel, businessComponentHandler);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onLogoffRequest(net.sourceforge.fixagora.basis.shared.model.communication.LogoffRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onLogoffRequest(LogoffRequest logoffRequest, Channel channel) {

		try {
			basisPersistenceHandler.removeSession(channel, businessComponentHandler);

			FUser fUser = logoffRequest.getFUser();
			ArrayList<AbstractBusinessObject> users = new ArrayList<AbstractBusinessObject>();
			users.add(fUser);
			basisPersistenceHandler.update(users, channel, businessComponentHandler, false);
			basisPersistenceHandler.send(new LogoffResponse(logoffRequest),channel);

			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(Level.INFO);
			logEntry.setMessageComponent("Basis");
			logEntry.setMessageText("User " + fUser.getName() + " logged out.");
			basisPersistenceHandler.writeLogEntry(logEntry);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(logoffRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onDataDictionariesRequest(net.sourceforge.fixagora.basis.shared.model.communication.DataDictionariesRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onDataDictionariesRequest(DataDictionariesRequest securitiesRequest, Channel channel) {

		try {
			List<DataDictionary> dictionaries = new ArrayList<DataDictionary>();
			for (DataDictionary dataDictionary : dataDictionaries)
				if (securitiesRequest.isEmpty())
					dictionaries.add(new DataDictionary(dataDictionary.getName(), null, new byte[0]));
				else
					dictionaries.add(new DataDictionary(dataDictionary.getName(), null, dataDictionary.getCompressed()));
			basisPersistenceHandler.send(new DataDictionariesResponse(securitiesRequest, dictionaries),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(securitiesRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onRefreshRequest(net.sourceforge.fixagora.basis.shared.model.communication.RefreshRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onRefreshRequest(RefreshRequest refreshRequest, Channel channel) {

		try {
			
			Class<?> classIdentifier = Class.forName(refreshRequest.getClassName());

			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) basisPersistenceHandler.findById(classIdentifier, refreshRequest.getObjectId(), businessComponentHandler);

			basisPersistenceHandler.send(new RefreshResponse(refreshRequest, abstractBusinessObject),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(refreshRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onUniqueSecurityIDRequest(net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onUniqueSecurityIDRequest(UniqueSecurityIDRequest uniqueSecurityIDRequest, Channel channel) {

		try {
			boolean unique = basisPersistenceHandler.securityIDCheck(uniqueSecurityIDRequest);

			basisPersistenceHandler.send(new UniqueSecurityIDResponse(uniqueSecurityIDRequest, unique),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(uniqueSecurityIDRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onOpenSpreadSheetRequest(net.sourceforge.fixagora.basis.shared.model.communication.OpenSpreadSheetRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenSpreadSheetRequest(OpenSpreadSheetRequest openSpreadSheetRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onOpenSpreadSheetRequest(openSpreadSheetRequest, channel);
			basisPersistenceHandler.send(new OpenSpreadSheetResponse(openSpreadSheetRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(openSpreadSheetRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifySheetCellRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifySheetCellRequest(ModifySheetCellRequest modifySheetCellRequest, Channel channel) {

		try {

			basisPersistenceHandler.send(new ModifySheetCellResponse(modifySheetCellRequest),channel);
			
			businessComponentHandler.getSpreadSheetHandler().onModifySheetCellRequest(modifySheetCellRequest, channel);

		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifySheetCellRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onCloseSpreadSheetRequest(net.sourceforge.fixagora.basis.shared.model.communication.CloseSpreadSheetRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseSpreadSheetRequest(CloseSpreadSheetRequest closeSpreadSheetRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onCloseSpreadSheetRequest(closeSpreadSheetRequest, channel);
			basisPersistenceHandler.send(new CloseSpreadSheetResponse(closeSpreadSheetRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(closeSpreadSheetRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onCopySpreadSheetCellRequest(net.sourceforge.fixagora.basis.shared.model.communication.CopySpreadSheetCellRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCopySpreadSheetCellRequest(CopySpreadSheetCellRequest copySpreadSheetCellRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
			basisPersistenceHandler.send(new CopySpreadSheetCellResponse(copySpreadSheetCellRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(copySpreadSheetCellRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onCutSpreadSheetCellRequest(net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCutSpreadSheetCellRequest(CutSpreadSheetCellRequest cutSpreadSheetCellRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onCutSpreadSheetCellRequest(cutSpreadSheetCellRequest, channel);
			basisPersistenceHandler.send(new CutSpreadSheetCellResponse(cutSpreadSheetCellRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(cutSpreadSheetCellRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onPasteSpreadSheetCellRequest(net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onPasteSpreadSheetCellRequest(PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
			basisPersistenceHandler.send(new PasteSpreadSheetCellResponse(pasteSpreadSheetCellRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(pasteSpreadSheetCellRequest),channel);
		}

	}

	/**
	 * Close.
	 */
	public void close() {

		businessComponentHandler.getSpreadSheetHandler().close();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifySpreadSheetCellFormatRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifySpreadSheetCellFormatRequest(ModifySpreadSheetCellFormatRequest modifySpreadSheetCellFormatRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onModifySpreadSheetCellFormatRequest(modifySpreadSheetCellFormatRequest, channel);
			basisPersistenceHandler.send(new ModifySpreadSheetCellFormatResponse(modifySpreadSheetCellFormatRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifySpreadSheetCellFormatRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifyColumnWidthRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifyColumnWidthRequest(ModifyColumnWidthRequest modifyColumnWidthRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onModifyColumnWidthRequest(modifyColumnWidthRequest, channel);
			basisPersistenceHandler.send(new ModifyColumnWidthResponse(modifyColumnWidthRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifyColumnWidthRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifyColumnVisibleRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifyColumnVisibleRequest(ModifyColumnVisibleRequest modifyColumnVisibleRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onModifyColumnVisibleRequest(modifyColumnVisibleRequest, channel);
			basisPersistenceHandler.send(new ModifyColumnVisibleResponse(modifyColumnVisibleRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifyColumnVisibleRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifyRowVisibleRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifyRowVisibleRequest(ModifyRowVisibleRequest modifyRowVisibleRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onModifyRowVisibleResponse(modifyRowVisibleRequest, channel);
			basisPersistenceHandler.send(new ModifyRowVisibleResponse(modifyRowVisibleRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifyRowVisibleRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onModifySpreadSheetConditionalFormatRequest(net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onModifySpreadSheetConditionalFormatRequest(ModifySpreadSheetConditionalFormatRequest modifySpreadSheetConditionalFormatRequest,
			Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onModifySpreadSheetConditionalFormatResponse(modifySpreadSheetConditionalFormatRequest, channel);
			basisPersistenceHandler.send(new ModifySpreadSheetConditionalFormatResponse(modifySpreadSheetConditionalFormatRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(modifySpreadSheetConditionalFormatRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onInsertColumnRequest(net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onInsertColumnRequest(InsertColumnRequest insertColumnRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onInsertColumnRequest(insertColumnRequest, channel);
			basisPersistenceHandler.send(new InsertColumnResponse(insertColumnRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(insertColumnRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onInsertRowRequest(net.sourceforge.fixagora.basis.shared.model.communication.InsertRowRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onInsertRowRequest(InsertRowRequest insertRowRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onInsertRowRequest(insertRowRequest, channel);
			basisPersistenceHandler.send(new InsertRowResponse(insertRowRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(insertRowRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onDeleteColumnRequest(net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onDeleteColumnRequest(DeleteColumnRequest deleteColumnRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onDeleteColumnRequest(deleteColumnRequest, channel);
			basisPersistenceHandler.send(new DeleteColumnResponse(deleteColumnRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(deleteColumnRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onDeleteRowRequest(net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onDeleteRowRequest(DeleteRowRequest deleteRowRequest, Channel channel) {

		try {
			businessComponentHandler.getSpreadSheetHandler().onDeleteRowRequest(deleteRowRequest, channel);
			basisPersistenceHandler.send(new DeleteRowResponse(deleteRowRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(deleteRowRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onStartBusinessComponentRequest(net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onStartBusinessComponentRequest(StartBusinessComponentRequest startBusinessComponentRequest, Channel channel) {

		basisPersistenceHandler.send(new StartBusinessComponentResponse(startBusinessComponentRequest),channel);
		businessComponentHandler.onStartBusinessComponentRequest(startBusinessComponentRequest, channel);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onStopBusinessComponentRequest(net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onStopBusinessComponentRequest(StopBusinessComponentRequest stopBusinessComponentRequest, Channel channel) {

		basisPersistenceHandler.send(new StopBusinessComponentResponse(stopBusinessComponentRequest),channel);
		businessComponentHandler.onStopBusinessComponentRequest(stopBusinessComponentRequest, channel);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onFillSpreadSheetCellRequest(net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onFillSpreadSheetCellRequest(FillSpreadSheetCellRequest fillSpreadSheetCellRequest, Channel channel) {

		try {

			businessComponentHandler.getSpreadSheetHandler().onFillSpreadSheetCellRequest(fillSpreadSheetCellRequest, channel);
			basisPersistenceHandler.send(new FillSpreadSheetCellResponse(fillSpreadSheetCellRequest),channel);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(fillSpreadSheetCellRequest),channel);
		}

	}

	/**
	 * Gets the business component handler.
	 *
	 * @return the business component handler
	 */
	public BusinessComponentHandler getBusinessComponentHandler() {

		return businessComponentHandler;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora Basis Server";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests#onSolverRequest(net.sourceforge.fixagora.basis.shared.model.communication.SolverRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onSolverRequest(SolverRequest solverRequest, Channel channel) {

		try {
			
			businessComponentHandler.getSpreadSheetHandler().onSolverRequest(solverRequest, channel);

		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(solverRequest),channel);
		}
		
	}


}
