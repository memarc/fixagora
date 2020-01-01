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
package net.sourceforge.fixagora.sellside.shared.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Class SellSideQuoteRequestRequest.
 */
public class SellSideQuoteRequestRequest extends AbstractSellSideRequest {

	private static final long serialVersionUID = 1L;

	private SellSideQuoteRequestEntry sellSideQuoteRequestEntry = null;
		
	/**
	 * Instantiates a new sell side quote request request.
	 *
	 * @param sellSideQuoteRequestEntry the sell side quote request entry
	 * @param requestID the request id
	 */
	public SellSideQuoteRequestRequest(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, long requestID) {

		super(requestID);
		this.sellSideQuoteRequestEntry = sellSideQuoteRequestEntry;
	}

	
	/**
	 * Gets the sell side quote request entry.
	 *
	 * @return the sell side quote request entry
	 */
	public SellSideQuoteRequestEntry getSellSideQuoteRequestEntry() {
	
		return sellSideQuoteRequestEntry;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideRequest#handleSellSideRequest(net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleSellSideRequest(SellSideRequests sellSideRequests, Channel channel) {

		sellSideRequests.onSellSideQuoteRequestRequest(this, channel);
		
	}




}
