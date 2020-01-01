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

import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

import org.jboss.netty.channel.Channel;


/**
 * The Class LoginRequest.
 */
public class LoginRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private FUser fUser = null;
		
	/**
	 * Instantiates a new login request.
	 *
	 * @param fUser the f user
	 * @param requestID the request id
	 */
	public LoginRequest(FUser fUser, long requestID) {

		super(requestID);
		this.fUser = fUser;
	}
	
	/**
	 * Gets the f user.
	 *
	 * @return the f user
	 */
	public FUser getFUser() {
	
		return fUser;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onLoginRequest(this, channel);
		
	}




}
