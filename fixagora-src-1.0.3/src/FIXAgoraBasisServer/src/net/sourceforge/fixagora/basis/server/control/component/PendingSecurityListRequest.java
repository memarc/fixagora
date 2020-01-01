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
package net.sourceforge.fixagora.basis.server.control.component;

import quickfix.Message;
import quickfix.SessionID;


/**
 * The Class PendingSecurityListRequest.
 */
public class PendingSecurityListRequest {

	private Message message = null;
	private SessionID sessionID = null;
	
	/**
	 * Instantiates a new pending security list request.
	 *
	 * @param message the message
	 * @param sessionID the session id
	 */
	public PendingSecurityListRequest(Message message, SessionID sessionID) {

		super();
		this.message = message;
		this.sessionID = sessionID;
	}

	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public Message getMessage() {
	
		return message;
	}

	
	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public SessionID getSessionID() {
	
		return sessionID;
	}
	
	
	
}
