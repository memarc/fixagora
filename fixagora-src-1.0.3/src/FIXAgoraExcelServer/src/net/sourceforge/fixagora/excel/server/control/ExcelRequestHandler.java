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
package net.sourceforge.fixagora.excel.server.control;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.excel.server.control.component.ExcelTradeCaptureComponentHandler;
import net.sourceforge.fixagora.excel.shared.communication.CloseExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.communication.CloseExcelTradeCaptureResponse;
import net.sourceforge.fixagora.excel.shared.communication.ExcelRequests;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryRequest;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureResponse;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListRequest;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListResponse;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCaptureGroup;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * The Class ExcelRequestHandler.
 */
public class ExcelRequestHandler implements ExcelRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(ExcelRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		BusinessComponentHandler businessComponentHandler = (BusinessComponentHandler)object2;
		
		this.basisPersistenceHandler = basisPersistenceHandler;

		BusinessSection businessSection = basisPersistenceHandler.findByName(BusinessSection.class, "Administration", businessComponentHandler);

		if (businessSection != null
				&& basisPersistenceHandler.findByName(ExcelTradeCaptureGroup.class, "Excel Trade Capture Exports", businessComponentHandler) == null) {
			ExcelTradeCaptureGroup excelTradeCaptureGroup = new ExcelTradeCaptureGroup();
			excelTradeCaptureGroup.setName("Excel Trade Capture Exports");
			excelTradeCaptureGroup.setDescription("This group contains all trade capture excel exports.");
			excelTradeCaptureGroup.setParent(businessSection);
			excelTradeCaptureGroup.setModificationDate(new Date());
			try {
				basisPersistenceHandler.persist(excelTradeCaptureGroup, null, businessComponentHandler);

				businessSection.getChildren().add(excelTradeCaptureGroup);
				basisPersistenceHandler.update(businessSection);
			}
			catch (Exception e) {
				log.error("Bug", e);
				writeLogEntry(e);
			}
		}

		this.businessComponentHandler = businessComponentHandler;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.ExcelRequests#onSecurityListRequest(net.sourceforge.fixagora.excel.shared.communication.SecurityListRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onSecurityListRequest(SecurityListRequest securityListRequest, Channel channel) {

		try {

			List<FSecurity> securities = basisPersistenceHandler.executeQuery(FSecurity.class, "select f from FSecurity f", businessComponentHandler, false);

			List<FSecurityGroup> groups = basisPersistenceHandler.executeQuery(FSecurityGroup.class,
					"select f from FSecurityGroup f where f.name='Imported Securities'", businessComponentHandler, false);

			FSecurityGroup securityGroup = null;

			if (groups.size() > 0)
				securityGroup = groups.get(0);

			SecurityListResponse securityListResponse = new SecurityListResponse(securityListRequest, securities, securityGroup);

			basisPersistenceHandler.send(securityListResponse,channel);
		}
		catch (Exception e) {
			log.error("Bug", e);
			writeLogEntry(e);
			basisPersistenceHandler.send(new ErrorResponse(securityListRequest),channel);
		}
	}

	private void writeLogEntry(Exception exception) {

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(Level.INFO);
		logEntry.setMessageComponent("Excel Integration");
		logEntry.setMessageText(exception.getMessage());
		basisPersistenceHandler.writeLogEntry(logEntry);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#handleSessionClosed(org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleSessionClosed(Channel channel) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.ExcelRequests#onCloseExcelTradeCaptureRequest(net.sourceforge.fixagora.excel.shared.communication.CloseExcelTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseExcelTradeCaptureRequest(CloseExcelTradeCaptureRequest closeExcelTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(closeExcelTradeCaptureRequest
				.getExcelTradeCapture());
		if (abstractComponentHandler instanceof ExcelTradeCaptureComponentHandler) {
			((ExcelTradeCaptureComponentHandler) abstractComponentHandler).onCloseExcelTradeCaptureRequest(closeExcelTradeCaptureRequest, channel);
			basisPersistenceHandler.send(new CloseExcelTradeCaptureResponse(closeExcelTradeCaptureRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.ExcelRequests#onOpenExcelTradeCaptureRequest(net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenExcelTradeCaptureRequest(OpenExcelTradeCaptureRequest openExcelTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(openExcelTradeCaptureRequest
				.getExcelTradeCapture());
		if (abstractComponentHandler instanceof ExcelTradeCaptureComponentHandler) {
			Collection<ExcelTradeCaptureEntry> excelTradeCaptureEntries = ((ExcelTradeCaptureComponentHandler) abstractComponentHandler)
					.onOpenExcelTradeCaptureRequest(openExcelTradeCaptureRequest, channel);
			basisPersistenceHandler.send(new OpenExcelTradeCaptureResponse(openExcelTradeCaptureRequest, excelTradeCaptureEntries),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.ExcelRequests#onExcelTradeCaptureEntryRequest(net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onExcelTradeCaptureEntryRequest(ExcelTradeCaptureEntryRequest excelTradeCaptureEntryRequest, Channel channel) {

		for (ExcelTradeCaptureEntry excelTradeCaptureEntry : excelTradeCaptureEntryRequest.getTradeCaptureEntries()) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(excelTradeCaptureEntry
					.getExcelTradeCapture());
			if (abstractComponentHandler instanceof ExcelTradeCaptureComponentHandler) {
				((ExcelTradeCaptureComponentHandler) abstractComponentHandler).removeExcelTradeCaptureEntry(excelTradeCaptureEntry);
			}
		}
		basisPersistenceHandler.send(new ExcelTradeCaptureEntryResponse(null, excelTradeCaptureEntryRequest),channel);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora Excel Server";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}


}
