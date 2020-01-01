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
 * The Class OpenTradeCaptureRequest.
 */
public class OpenTradeCaptureRequest extends AbstractTradeCaptureRequest {

	private static final long serialVersionUID = 1L;

	private long tradeCapture = 0;
	
	private long timestamp = 0;
		
	/**
	 * Instantiates a new open trade capture request.
	 *
	 * @param tradeCapture the trade capture
	 * @param timestamp the timestamp
	 * @param requestID the request id
	 */
	public OpenTradeCaptureRequest(long tradeCapture,long timestamp, long requestID) {

		super(requestID);
		this.tradeCapture = tradeCapture;
		this.timestamp = timestamp;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest#getTimestamp()
	 */
	public long getTimestamp() {
	
		return timestamp;
	}



	/**
	 * Gets the trade capture.
	 *
	 * @return the trade capture
	 */
	public long getTradeCapture() {
	
		return tradeCapture;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.AbstractTradeCaptureRequest#handleTradeCaptureRequest(net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleTradeCaptureRequest(TradeCaptureRequests tradeCaptureRequests, Channel channel) {

		tradeCaptureRequests.onOpenTradeCaptureRequest(this, channel);
		
	}




}
