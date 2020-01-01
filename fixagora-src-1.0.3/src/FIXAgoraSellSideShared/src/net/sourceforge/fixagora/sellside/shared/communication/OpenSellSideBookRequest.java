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
 * The Class OpenSellSideBookRequest.
 */
public class OpenSellSideBookRequest extends AbstractSellSideRequest {

	private static final long serialVersionUID = 1L;

	private long sellSideBook = 0;
		
	/**
	 * Instantiates a new open sell side book request.
	 *
	 * @param sellSideBook the sell side book
	 * @param requestID the request id
	 */
	public OpenSellSideBookRequest(long sellSideBook, long requestID) {

		super(requestID);
		this.sellSideBook = sellSideBook;
	}
	
	
	/**
	 * Gets the sell side book.
	 *
	 * @return the sell side book
	 */
	public long getSellSideBook() {
	
		return sellSideBook;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideRequest#handleSellSideRequest(net.sourceforge.fixagora.sellside.shared.communication.SellSideRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleSellSideRequest(SellSideRequests sellSideRequests, Channel channel) {

		sellSideRequests.onOpenSellSideBookRequest(this, channel);
		
	}




}
