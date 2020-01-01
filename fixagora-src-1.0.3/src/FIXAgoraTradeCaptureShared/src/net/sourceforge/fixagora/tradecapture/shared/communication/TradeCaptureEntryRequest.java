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
package net.sourceforge.fixagora.tradecapture.shared.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Class TradeCaptureEntryRequest.
 */
public class TradeCaptureEntryRequest extends AbstractTradeCaptureRequest {

	private static final long serialVersionUID = 1L;

	private TradeCaptureEntry tradeCaptureEntry = null;
		
	/**
	 * Instantiates a new trade capture entry request.
	 *
	 * @param tradeCaptureEntry the trade capture entry
	 * @param requestID the request id
	 */
	public TradeCaptureEntryRequest(TradeCaptureEntry tradeCaptureEntry, long requestID) {

		super(requestID);
		this.tradeCaptureEntry = tradeCaptureEntry;
	}


	
	/**
	 * Gets the trade capture entry.
	 *
	 * @return the trade capture entry
	 */
	public TradeCaptureEntry getTradeCaptureEntry() {
	
		return tradeCaptureEntry;
	}




	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.AbstractTradeCaptureRequest#handleTradeCaptureRequest(net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleTradeCaptureRequest(TradeCaptureRequests tradeCaptureRequests, Channel channel) {

		tradeCaptureRequests.onTradeCaptureEntryRequest(this, channel);
		
	}




}
