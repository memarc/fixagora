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

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;


/**
 * The Class AbstractBuySideRequest.
 */
public abstract class AbstractBuySideRequest extends AbstractRequest{

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new abstract buy side request.
	 *
	 * @param requestID the request id
	 */
	public AbstractBuySideRequest(long requestID) {

		super(requestID);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest#handleAbstractRequest(net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractRequest(AbstractRequests abstractRequests, Channel channel) {

		if(abstractRequests instanceof BuySideRequests)
		{
			BuySideRequests buySideRequests = (BuySideRequests)abstractRequests;
			handleBuySideRequest(buySideRequests, channel);
		}
		
	}
	
	/**
	 * Handle buy side request.
	 *
	 * @param buySideRequests the buy side requests
	 * @param channel the channel
	 */
	public abstract void handleBuySideRequest(BuySideRequests buySideRequests, Channel channel);	

}
