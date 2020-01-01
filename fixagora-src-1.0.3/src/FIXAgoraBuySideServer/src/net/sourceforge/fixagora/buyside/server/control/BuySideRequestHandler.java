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
package net.sourceforge.fixagora.buyside.server.control;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.buyside.server.control.component.BuySideBookComponentHandler;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests;
import net.sourceforge.fixagora.buyside.shared.communication.CloseBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.CloseBuySideBookResponse;
import net.sourceforge.fixagora.buyside.shared.communication.CloseQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.CloseQuotePageResponse;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuoteDepthRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuoteDepthResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuotePageResponse;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestRequest;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBookGroup;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * The Class BuySideRequestHandler.
 */
public class BuySideRequestHandler implements BuySideRequests {

	private BasisPersistenceHandler basisPersistenceHandler = null;

	private BusinessComponentHandler businessComponentHandler = null;
	
	private static Logger log = Logger.getLogger(BuySideRequestHandler.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#init(java.lang.Object, java.lang.Object)
	 */
	public void init(Object object, Object object2) throws Exception{
		
		BasisPersistenceHandler basisPersistenceHandler = (BasisPersistenceHandler)object;
		BusinessComponentHandler businessComponentHandler = (BusinessComponentHandler)object2;

		this.basisPersistenceHandler = basisPersistenceHandler;

		if (basisPersistenceHandler.findByName(BuySideBookGroup.class, "Buy Side", businessComponentHandler) == null) {
			BuySideBookGroup buySideBookGroup = new BuySideBookGroup();
			buySideBookGroup.setName("Buy Side");
			buySideBookGroup.setDescription("This group contains all buy side books.");
			buySideBookGroup.setModificationDate(new Date());
			try {
				basisPersistenceHandler.persist(buySideBookGroup, null, businessComponentHandler);
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
		logEntry.setMessageComponent("Buy Side");
		logEntry.setMessageText(exception.getMessage());
		basisPersistenceHandler.writeLogEntry(logEntry);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#handleSessionClosed(org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleSessionClosed(Channel channel) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onCloseQuotePageRequest(net.sourceforge.fixagora.buyside.shared.communication.CloseQuotePageRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseQuotePageRequest(CloseQuotePageRequest closeQuotePageRequest, Channel channel) {

		BuySideQuotePage buySideQuotePage = basisPersistenceHandler.findById(BuySideQuotePage.class, closeQuotePageRequest.getBuySideQuotePage(),
				businessComponentHandler);
		if (buySideQuotePage != null) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(buySideQuotePage.getParent().getId());
			if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
				((BuySideBookComponentHandler) abstractComponentHandler).onCloseQuoteMonitorRequest(closeQuotePageRequest, buySideQuotePage, channel);
				basisPersistenceHandler.send(new CloseQuotePageResponse(closeQuotePageRequest),channel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onOpenQuotePageRequest(net.sourceforge.fixagora.buyside.shared.communication.OpenQuotePageRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenQuotePageRequest(OpenQuotePageRequest openQuotePageRequest, Channel channel) {
		
		basisPersistenceHandler.send(new OpenQuotePageResponse(openQuotePageRequest),channel);

		BuySideQuotePage buySideQuotePage = basisPersistenceHandler.findById(BuySideQuotePage.class, openQuotePageRequest.getBuySideQuotePage(),
				businessComponentHandler);
		if (buySideQuotePage != null) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(buySideQuotePage.getParent().getId());
			if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
				((BuySideBookComponentHandler) abstractComponentHandler).onOpenQuoteMonitorRequest(openQuotePageRequest, buySideQuotePage, channel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onOpenQuoteDepthRequest(net.sourceforge.fixagora.buyside.shared.communication.OpenQuoteDepthRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenQuoteDepthRequest(OpenQuoteDepthRequest openQuoteDepthRequest, Channel channel) {

		BuySideQuotePage buySideQuotePage = basisPersistenceHandler.findById(BuySideQuotePage.class, openQuoteDepthRequest.getBuySideQuotePage(),
				businessComponentHandler);
		if (buySideQuotePage != null) {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(buySideQuotePage.getParent().getId());
			if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
				((BuySideBookComponentHandler) abstractComponentHandler).onOpenQuoteDepthRequest(openQuoteDepthRequest, buySideQuotePage, channel);
				basisPersistenceHandler.send(new OpenQuoteDepthResponse(openQuoteDepthRequest),channel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onNewOrderSingleRequest(net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel) {

		try {
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(newOrderSingleRequest
					.getBuySideNewOrderSingleEntry().getBuySideBook());
			if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
				BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = ((BuySideBookComponentHandler) abstractComponentHandler).onNewOrderSingleRequest(
						newOrderSingleRequest, channel);
				basisPersistenceHandler.send(new NewOrderSingleResponse(buySideNewOrderSingleEntry, newOrderSingleRequest),channel);
			}
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onBuySideQuoteRequestRequest(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onBuySideQuoteRequestRequest(BuySideQuoteRequestRequest quoteRequestRequest, Channel channel) {

		try {
			
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(quoteRequestRequest
					.getBuySideQuoteRequestEntries().get(0).getBuySideBook());
			if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
				List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = ((BuySideBookComponentHandler) abstractComponentHandler).onBuySideQuoteRequestRequest(
						quoteRequestRequest, channel);

				basisPersistenceHandler.send(new BuySideQuoteRequestResponse(buySideQuoteRequestEntries, quoteRequestRequest),channel);
			}
		}
		catch (Exception e) {
			
			log.error("Bug", e);
			
		}		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onOpenBuySideBookRequest(net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onOpenBuySideBookRequest(OpenBuySideBookRequest openBuySideBookRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(openBuySideBookRequest.getBuySideBook());
		if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
			Collection<AbstractBuySideEntry>  buySideNewOrderSingleEntries = ((BuySideBookComponentHandler) abstractComponentHandler).onOpenBuySideBookRequest(openBuySideBookRequest, channel);
			basisPersistenceHandler.send(new OpenBuySideBookResponse(openBuySideBookRequest, buySideNewOrderSingleEntries),channel);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests#onCloseBuySideBookRequest(net.sourceforge.fixagora.buyside.shared.communication.CloseBuySideBookRequest, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void onCloseBuySideBookRequest(CloseBuySideBookRequest closeBuySideBookRequest, Channel channel) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(closeBuySideBookRequest.getBuySideBook());
		if (abstractComponentHandler instanceof BuySideBookComponentHandler) {
			((BuySideBookComponentHandler) abstractComponentHandler).onCloseBuySideBookRequest(closeBuySideBookRequest, channel);
			basisPersistenceHandler.send(new CloseBuySideBookResponse(closeBuySideBookRequest),channel);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getName()
	 */
	@Override
	public String getName() {

		return "FIX Agora Buy Side Server";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests#getLicense()
	 */
	@Override
	public String getLicense() {

		return "LGPL Version 2.1 - Copyright(C) 2012-2013 - Alexander Pinnow";
	}
	
}
