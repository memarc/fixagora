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
package net.sourceforge.fixagora.tradecapture.server.control;

import java.util.Collection;
import java.util.Date;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.tradecapture.server.control.component.TradeCaptureComponentHandler;
import net.sourceforge.fixagora.tradecapture.shared.communication.CloseTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.CloseTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests;
import net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCaptureGroup;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * The Class TradeCaptureRequestHandler.
 */
public class TradeCaptureRequestHandler implements TradeCaptureRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(TradeCaptureRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		BusinessComponentHandler businessComponentHandler = (BusinessComponentHandler)object2;
		
		this.basisPersistenceHandler = basisPersistenceHandler;

		if (basisPersistenceHandler.findByName(TradeCaptureGroup.class, "Trade Captures", businessComponentHandler) == null) {
			TradeCaptureGroup tradeCaptureGroup = new TradeCaptureGroup();
			tradeCaptureGroup.setName("Trade Captures");
			tradeCaptureGroup.setDescription("This group contains all trade captures.");
			tradeCaptureGroup.setModificationDate(new Date());
			try {
				basisPersistenceHandler.persist(tradeCaptureGroup, null, businessComponentHandler);
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
		logEntry.setMessageComponent("Sell Side Book");
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
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests#onCloseTradeCaptureRequest(net.sourceforge.fixagora.tradecapture.shared.communication.CloseTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseTradeCaptureRequest(CloseTradeCaptureRequest closeTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(closeTradeCaptureRequest.getTradeCapture());
		if (abstractComponentHandler instanceof TradeCaptureComponentHandler) {
			((TradeCaptureComponentHandler) abstractComponentHandler).onCloseTradeCaptureRequest(closeTradeCaptureRequest, channel);
			basisPersistenceHandler.send(new CloseTradeCaptureResponse(closeTradeCaptureRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests#onOpenTradeCaptureRequest(net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenTradeCaptureRequest(OpenTradeCaptureRequest openTradeCaptureRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(openTradeCaptureRequest.getTradeCapture());
		if (abstractComponentHandler instanceof TradeCaptureComponentHandler) {
			Collection<TradeCaptureEntry> tradeCaptureEntries = ((TradeCaptureComponentHandler) abstractComponentHandler).onOpenTradeCaptureRequest(openTradeCaptureRequest, channel);
			basisPersistenceHandler.send(new OpenTradeCaptureResponse(openTradeCaptureRequest, tradeCaptureEntries),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests#onTradeCaptureEntryRequest(net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onTradeCaptureEntryRequest(TradeCaptureEntryRequest tradeCaptureEntryRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(tradeCaptureEntryRequest
				.getTradeCaptureEntry().getTradeCapture());
		if (abstractComponentHandler instanceof TradeCaptureComponentHandler) {
			TradeCaptureEntry tradeCaptureEntry = ((TradeCaptureComponentHandler) abstractComponentHandler).onTradeCaptureEntryRequest(
					tradeCaptureEntryRequest, channel);
			basisPersistenceHandler.send(new TradeCaptureEntryResponse(tradeCaptureEntry, tradeCaptureEntryRequest),channel);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora Trade Capture Server";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}


}
