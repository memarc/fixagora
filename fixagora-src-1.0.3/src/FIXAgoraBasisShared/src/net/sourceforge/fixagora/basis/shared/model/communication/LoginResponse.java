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


/**
 * The Class LoginResponse.
 */
public class LoginResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum LoginStatus.
	 */
	public static enum LoginStatus{/** The success. */
SUCCESS,/** The failed. */
FAILED};
	
	private LoginStatus loginStatus = null;
	
	private FUser fUser = null;
	
	private byte[] logs = null;

	private String responseText = null;
	
	/**
	 * Instantiates a new login response.
	 *
	 * @param abstractRequest the abstract request
	 * @param fUser the f user
	 * @param loginStatus the login status
	 * @param responseText the response text
	 * @param logs the logs
	 */
	public LoginResponse(AbstractRequest abstractRequest, FUser fUser, LoginStatus loginStatus, String responseText, byte[] logs) {

		super(abstractRequest);
		this.loginStatus = loginStatus;
		this.fUser = fUser;
		this.logs = logs;
		this.responseText = responseText;
	}
		
	/**
	 * Gets the login status.
	 *
	 * @return the login status
	 */
	public LoginStatus getLoginStatus() {
	
		return loginStatus;
	}
	
	/**
	 * Gets the f user.
	 *
	 * @return the f user
	 */
	public FUser getFUser() {
	
		return fUser;
	}

	
	/**
	 * Gets the logs.
	 *
	 * @return the logs
	 */
	public byte[] getLogs() {
	
		return logs;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {

		fBasisResponses.onLoginResponse(this);
		
	}

	/**
	 * Gets the response text.
	 *
	 * @return the response text
	 */
	public String getResponseText() {

		return responseText ;
	}

}
