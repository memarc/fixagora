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
package net.sourceforge.fixagora.sap.server.control;

import java.util.Date;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.sap.server.control.component.SAPTradeCaptureComponentHandler;
import net.sourceforge.fixagora.sap.shared.communication.CloseSAPTradeCaptureRequest;
import net.sourceforge.fixagora.sap.shared.communication.CloseSAPTradeCaptureResponse;
import net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureRequest;
import net.sourceforge.fixagora.sap.shared.communication.SAPRequests;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryRequest;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCaptureGroup;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * The Class SAPRequestHandler.
 */
public class SAPRequestHandler implements SAPRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(SAPRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		BusinessComponentHandler businessComponentHandler = (BusinessComponentHandler)object2;
		
		this.basisPersistenceHandler = basisPersistenceHandler;

		BusinessSection businessSection = basisPersistenceHandler.findByName(BusinessSection.class, "Administration", businessComponentHandler);

		if (businessSection != null
				&& basisPersistenceHandler.findByName(SAPTradeCaptureGroup.class, "SAP Trade Capture Exports", businessComponentHandler) == null) {
			SAPTradeCaptureGroup sapTradeCaptureGroup = new SAPTradeCaptureGroup();
			sapTradeCaptureGroup.setName("SAP Trade Capture Exports");
			sapTradeCaptureGroup.setDescription("This group contains all SAP trade capture exports.");
			sapTradeCaptureGroup.setParent(businessSection);
			sapTradeCaptureGroup.setModificationDate(new Date());
			try {
				basisPersistenceHandler.persist(sapTradeCaptureGroup, null, businessComponentHandler);

				businessSection.getChildren().add(sapTradeCaptureGroup);
				basisPersistenceHandler.update(businessSection);
			}
			catch (Exception e) {
				log.error("Bug", e);
				writeLogEntry(e);
			}
		}

		this.businessComponentHandler = businessComponentHandler;

	}


	private void writeLogEntry(Exception exception) {

		LogEntry logEntry = new LogEntry();
		logEntry.setLogDate(new Date());
		logEntry.setLogLevel(Level.INFO);
		logEntry.setMessageComponent("SAP Integration");
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
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPRequests#onCloseSAPTradeCaptureRequest(net.sourceforge.fixagora.sap.shared.communication.CloseSAPTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseSAPTradeCaptureRequest(CloseSAPTradeCaptureRequest closeSAPTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(closeSAPTradeCaptureRequest
				.getSAPTradeCapture());
		if (abstractComponentHandler instanceof SAPTradeCaptureComponentHandler) {
			((SAPTradeCaptureComponentHandler) abstractComponentHandler).onCloseSAPTradeCaptureRequest(closeSAPTradeCaptureRequest, channel);
			basisPersistenceHandler.send(new CloseSAPTradeCaptureResponse(closeSAPTradeCaptureRequest),channel);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPRequests#onOpenSAPTradeCaptureRequest(net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenSAPTradeCaptureRequest(OpenSAPTradeCaptureRequest openSAPTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(openSAPTradeCaptureRequest
				.getSAPTradeCapture());
		if (abstractComponentHandler instanceof SAPTradeCaptureComponentHandler) {
			basisPersistenceHandler.send(((SAPTradeCaptureComponentHandler) abstractComponentHandler)
					.onOpenSAPTradeCaptureRequest(openSAPTradeCaptureRequest, channel),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPRequests#onSAPTradeCaptureEntryRequest(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onSAPTradeCaptureEntryRequest(SAPTradeCaptureEntryRequest sapTradeCaptureEntryRequest, Channel channel) {

		for (SAPTradeCaptureEntry sapTradeCaptureEntry : sapTradeCaptureEntryRequest.getTradeCaptureEntries()) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(sapTradeCaptureEntry
					.getSAPTradeCapture());
			if (abstractComponentHandler instanceof SAPTradeCaptureComponentHandler) {
				((SAPTradeCaptureComponentHandler) abstractComponentHandler).removeSAPTradeCaptureEntry(sapTradeCaptureEntry);
			}
		}
		basisPersistenceHandler.send(new SAPTradeCaptureEntryResponse(null, sapTradeCaptureEntryRequest),channel);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPRequests#onSAPTradeCaptureActionRequest(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onSAPTradeCaptureActionRequest(SAPTradeCaptureActionRequest sapTradeCaptureActionRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(sapTradeCaptureActionRequest
				.getSAPTradeCapture());
		
		if (abstractComponentHandler instanceof SAPTradeCaptureComponentHandler) {
			((SAPTradeCaptureComponentHandler) abstractComponentHandler).onSAPTradeCaptureActionRequest(sapTradeCaptureActionRequest, channel);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora SAP Server";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}


}
