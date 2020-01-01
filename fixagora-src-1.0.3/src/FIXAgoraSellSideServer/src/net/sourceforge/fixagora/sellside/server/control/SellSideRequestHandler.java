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
package net.sourceforge.fixagora.sellside.server.control;

import java.util.Collection;
import java.util.Date;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.sellside.server.control.component.SellSideBookComponentHandler;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry;
import net.sourceforge.fixagora.sellside.shared.communication.CloseQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.CloseQuoteMonitorResponse;
import net.sourceforge.fixagora.sellside.shared.communication.CloseSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.CloseSellSideBookResponse;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.OpenQuoteMonitorResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestRequest;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBookGroup;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * The Class SellSideRequestHandler.
 */
public class SellSideRequestHandler implements SellSideRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(SellSideRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		BusinessComponentHandler businessComponentHandler = (BusinessComponentHandler)object2;
		
		this.basisPersistenceHandler = basisPersistenceHandler;

		if (basisPersistenceHandler.findByName(SellSideBookGroup.class, "Sell Side", businessComponentHandler) == null) {
			SellSideBookGroup sellSideBookGroup = new SellSideBookGroup();
			sellSideBookGroup.setName("Sell Side");
			sellSideBookGroup.setDescription("This group contains all sell side books.");
			sellSideBookGroup.setModificationDate(new Date());
			try {
				basisPersistenceHandler.persist(sellSideBookGroup, null, businessComponentHandler);
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
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onCloseQuoteMonitorRequest(net.sourceforge.fixagora.sellside.shared.communication.CloseQuoteMonitorRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseQuoteMonitorRequest(CloseQuoteMonitorRequest closeQuoteMonitorRequest, Channel channel) {

		SellSideQuotePage sellSideQuotePage = basisPersistenceHandler.findById(SellSideQuotePage.class, closeQuoteMonitorRequest.getSellSideQuotePage(),
				businessComponentHandler);
		if (sellSideQuotePage != null) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(sellSideQuotePage.getParent().getId());
			if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
				((SellSideBookComponentHandler) abstractComponentHandler).onCloseQuoteMonitorRequest(closeQuoteMonitorRequest, sellSideQuotePage, channel);
				basisPersistenceHandler.send(new CloseQuoteMonitorResponse(closeQuoteMonitorRequest),channel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onOpenQuoteMonitorRequest(net.sourceforge.fixagora.sellside.shared.communication.OpenQuoteMonitorRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenQuoteMonitorRequest(OpenQuoteMonitorRequest openQuoteMonitorRequest, Channel channel) {
		
		basisPersistenceHandler.send(new OpenQuoteMonitorResponse(openQuoteMonitorRequest),channel);

		SellSideQuotePage sellSideQuotePage = basisPersistenceHandler.findById(SellSideQuotePage.class, openQuoteMonitorRequest.getSellSideQuotePage(),
				businessComponentHandler);
		if (sellSideQuotePage != null) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(sellSideQuotePage.getParent().getId());
			if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
				((SellSideBookComponentHandler) abstractComponentHandler).onOpenQuoteMonitorRequest(openQuoteMonitorRequest, sellSideQuotePage, channel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onNewOrderSingleRequest(net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(newOrderSingleRequest
				.getSellSideNewOrderSingleEntry().getSellSideBook());
		if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
			SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = ((SellSideBookComponentHandler) abstractComponentHandler).onNewOrderSingleRequest(
					newOrderSingleRequest, channel);
			basisPersistenceHandler.send(new NewOrderSingleResponse(sellSideNewOrderSingleEntry, newOrderSingleRequest),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onOpenSellSideBookRequest(net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenSellSideBookRequest(OpenSellSideBookRequest openSellSideBookRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(openSellSideBookRequest.getSellSideBook());
		if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
			Collection<AbstractSellSideEntry> abstractSellSideEntries = ((SellSideBookComponentHandler) abstractComponentHandler)
					.onOpenSellSideBookRequest(openSellSideBookRequest, channel);
			basisPersistenceHandler.send(new OpenSellSideBookResponse(openSellSideBookRequest, abstractSellSideEntries),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onCloseSellSideBookRequest(net.sourceforge.fixagora.sellside.shared.communication.CloseSellSideBookRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseSellSideBookRequest(CloseSellSideBookRequest closeSellSideBookRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(closeSellSideBookRequest.getSellSideBook());
		if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
			((SellSideBookComponentHandler) abstractComponentHandler).onCloseSellSideBookRequest(closeSellSideBookRequest, channel);
			basisPersistenceHandler.send(new CloseSellSideBookResponse(closeSellSideBookRequest),channel);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora Sell Side Server";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests#onSellSideQuoteRequestRequest(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onSellSideQuoteRequestRequest(SellSideQuoteRequestRequest sellSideQuoteRequestRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(sellSideQuoteRequestRequest.getSellSideQuoteRequestEntry().getSellSideBook());
		if (abstractComponentHandler instanceof SellSideBookComponentHandler) {
			SellSideQuoteRequestEntry sellSideQuoteRequestEntry = ((SellSideBookComponentHandler) abstractComponentHandler).onSellSideQuoteRequestRequest(
					sellSideQuoteRequestRequest, channel);
			basisPersistenceHandler.send(new SellSideQuoteRequestResponse(sellSideQuoteRequestEntry, sellSideQuoteRequestRequest),channel);
		}
		
	}


}
