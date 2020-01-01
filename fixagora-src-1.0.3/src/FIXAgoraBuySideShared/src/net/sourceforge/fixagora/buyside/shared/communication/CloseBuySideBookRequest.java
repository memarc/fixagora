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

import org.jboss.netty.channel.Channel;


/**
 * The Class CloseBuySideBookRequest.
 */
public class CloseBuySideBookRequest extends AbstractBuySideRequest {

	private static final long serialVersionUID = 1L;

	private long buySideBook = 0;
		
	/**
	 * Instantiates a new close buy side book request.
	 *
	 * @param buySideBook the buy side book
	 * @param requestID the request id
	 */
	public CloseBuySideBookRequest(long buySideBook, long requestID) {

		super(requestID);
		this.buySideBook = buySideBook;
	}

	
	/**
	 * Gets the buy side book.
	 *
	 * @return the buy side book
	 */
	public long getBuySideBook() {
	
		return buySideBook;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideRequest#handleBuySideRequest(net.sourceforge.fixagora.buyside.shared.communication.BuySideRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleBuySideRequest(BuySideRequests buySideRequests, Channel channel) {

		buySideRequests.onCloseBuySideBookRequest(this, channel);
		
	}




}
