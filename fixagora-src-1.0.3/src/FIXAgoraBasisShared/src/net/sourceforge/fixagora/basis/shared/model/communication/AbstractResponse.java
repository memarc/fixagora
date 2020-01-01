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

import java.io.Serializable;


/**
 * The Class AbstractResponse.
 */
public abstract class AbstractResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private long requestID = 0;
	
	private long timestamp = 0;
	
	/**
	 * Instantiates a new abstract response.
	 *
	 * @param abstractRequest the abstract request
	 */
	public AbstractResponse(AbstractRequest abstractRequest) {

		super();
		if(abstractRequest!=null)
			requestID = abstractRequest.getRequestID();		
	}

	
	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public long getRequestID() {
	
		return requestID;
	}

	/**
	 * Handle abstract response.
	 *
	 * @param abstractResponses the abstract responses
	 */
	public abstract void handleAbstractResponse(AbstractResponses abstractResponses);


	
	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public long getTimestamp() {
	
		return timestamp;
	}


	
	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(long timestamp) {
	
		this.timestamp = timestamp;
	}
	
	
	
}
