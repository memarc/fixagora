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
 * The Class UniqueNameRequest.
 */
public class UniqueNameRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;
	private String name = null;
	private long id = 0;

		
	/**
	 * Instantiates a new unique name request.
	 *
	 * @param name the name
	 * @param id the id
	 * @param requestID the request id
	 */
	public UniqueNameRequest(String name, long id, long requestID) {

		super(requestID);
		this.name = name;
		this.id = id;
	}
		
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
	
		return name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onUniqueNameRequest(this, channel);
		
	}

}
