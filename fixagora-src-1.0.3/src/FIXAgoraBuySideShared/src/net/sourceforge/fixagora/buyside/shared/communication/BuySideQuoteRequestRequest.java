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
package net.sourceforge.fixagora.buyside.shared.communication;

import java.util.List;

import org.jboss.netty.channel.Channel;


/**
 * The Class BuySideQuoteRequestRequest.
 */
public class BuySideQuoteRequestRequest extends AbstractBuySideRequest {

	private static final long serialVersionUID = 1L;

	private List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = null;
		
	/**
	 * Instantiates a new buy side quote request request.
	 *
	 * @param buySideQuoteRequestEntries the buy side quote request entries
	 * @param requestID the request id
	 */
	public BuySideQuoteRequestRequest(List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries, long requestID) {

		super(requestID);
		this.buySideQuoteRequestEntries = buySideQuoteRequestEntries;
	}

	



	
	/**
	 * Gets the buy side quote request entries.
	 *
	 * @return the buy side quote request entries
	 */
	public List<BuySideQuoteRequestEntry> getBuySideQuoteRequestEntries() {
	
		return buySideQuoteRequestEntries;
	}





	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideRequest#handleBuySideRequest(net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleBuySideRequest(BuySideRequests buySideRequests, Channel channel) {

		buySideRequests.onBuySideQuoteRequestRequest(this, channel);
		
	}




}
