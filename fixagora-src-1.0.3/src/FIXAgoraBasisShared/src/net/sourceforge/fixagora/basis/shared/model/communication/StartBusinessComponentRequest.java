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

import java.util.List;

import org.jboss.netty.channel.Channel;


/**
 * The Class StartBusinessComponentRequest.
 */
public class StartBusinessComponentRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private List<Long> businessComponents = null;
		

		
	/**
	 * Instantiates a new start business component request.
	 *
	 * @param requestID the request id
	 * @param businessComponents the business components
	 */
	public StartBusinessComponentRequest(long requestID, List<Long> businessComponents) {

		super(requestID);
		this.businessComponents = businessComponents;
	}



	/**
	 * Gets the business components.
	 *
	 * @return the business components
	 */
	public List<Long> getBusinessComponents() {
	
		return businessComponents;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onStartBusinessComponentRequest(this, channel);
		
	}




}
