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
package net.sourceforge.fixagora.sap.shared.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Class SAPTradeCaptureActionRequest.
 */
public class SAPTradeCaptureActionRequest extends AbstractSAPRequest {
	
	/**
	 * The Enum SAPTradeCaptureAction.
	 */
	public enum SAPTradeCaptureAction{/** The remove. */
REMOVE,/** The test. */
TEST,/** The export. */
EXPORT};

	private static final long serialVersionUID = 1L;

	private long tradeCapture = 0;
	
	private SAPTradeCaptureAction tradeCaptureAction = null;
			
	/**
	 * Instantiates a new sAP trade capture action request.
	 *
	 * @param tradeCapture the trade capture
	 * @param tradeCaptureAction the trade capture action
	 * @param requestID the request id
	 */
	public SAPTradeCaptureActionRequest(long tradeCapture,SAPTradeCaptureAction tradeCaptureAction,long requestID) {

		super(requestID);
		this.tradeCapture = tradeCapture;
		this.tradeCaptureAction = tradeCaptureAction;
	}
	
	

	
	/**
	 * Gets the trade capture action.
	 *
	 * @return the trade capture action
	 */
	public SAPTradeCaptureAction getTradeCaptureAction() {
	
		return tradeCaptureAction;
	}



	/**
	 * Gets the sAP trade capture.
	 *
	 * @return the sAP trade capture
	 */
	public long getSAPTradeCapture() {
	
		return tradeCapture;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.AbstractSAPRequest#handleAbstractSAPRequest(net.sourceforge.fixagora.sap.shared.communication.SAPRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractSAPRequest(SAPRequests sapRequests, Channel channel) {

		sapRequests.onSAPTradeCaptureActionRequest(this, channel);
		
	}




}
