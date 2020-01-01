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
 * The Class DataDictionariesRequest.
 */
public class DataDictionariesRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;
	
	private boolean empty = true;
		
	/**
	 * Instantiates a new data dictionaries request.
	 *
	 * @param requestID the request id
	 * @param empty the empty
	 */
	public DataDictionariesRequest(long requestID, boolean empty) {

		super(requestID);
		this.empty = empty;
	}	
	
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
	
		return empty;
	}



	
	/**
	 * Sets the empty.
	 *
	 * @param empty the new empty
	 */
	public void setEmpty(boolean empty) {
	
		this.empty = empty;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onDataDictionariesRequest(this, channel);
		
	}




}
