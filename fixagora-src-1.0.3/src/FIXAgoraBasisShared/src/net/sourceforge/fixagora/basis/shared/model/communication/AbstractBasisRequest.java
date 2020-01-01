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
package net.sourceforge.fixagora.basis.shared.model.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Class AbstractBasisRequest.
 */
public abstract class AbstractBasisRequest extends AbstractRequest{

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new abstract basis request.
	 *
	 * @param requestID the request id
	 */
	public AbstractBasisRequest(long requestID) {

		super(requestID);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest#handleAbstractRequest(net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractRequest(AbstractRequests abstractRequests, Channel channel) {

		if(abstractRequests instanceof BasisRequests)
		{
			BasisRequests fBasisRequests = (BasisRequests)abstractRequests;
			handleAbstractFBasisRequest(fBasisRequests, channel);
		}
		
	}
	
	/**
	 * Handle abstract f basis request.
	 *
	 * @param fBasisRequests the f basis requests
	 * @param channel the channel
	 */
	public abstract void handleAbstractFBasisRequest(BasisRequests fBasisRequests, Channel channel);	

}
