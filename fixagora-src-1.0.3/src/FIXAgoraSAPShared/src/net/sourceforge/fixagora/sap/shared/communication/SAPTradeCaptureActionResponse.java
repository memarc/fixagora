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

import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest.SAPTradeCaptureAction;


/**
 * The Class SAPTradeCaptureActionResponse.
 */
public class SAPTradeCaptureActionResponse extends AbstractSAPResponse {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum ActionResponse.
	 */
	public enum ActionResponse{/** The started. */
STARTED,/** The stopped. */
STOPPED,/** The failed. */
FAILED}

	private long tradeCapture = 0;
	private SAPTradeCaptureAction tradeCaptureAction = null;
	private ActionResponse actionResponse = null;
		
	/**
	 * Instantiates a new sAP trade capture action response.
	 *
	 * @param sapTradeCaptureActionRequest the sap trade capture action request
	 * @param tradeCapture the trade capture
	 * @param tradeCaptureAction the trade capture action
	 * @param actionResponse the action response
	 */
	public SAPTradeCaptureActionResponse(SAPTradeCaptureActionRequest sapTradeCaptureActionRequest, long tradeCapture, SAPTradeCaptureAction tradeCaptureAction, ActionResponse actionResponse) {

		super(sapTradeCaptureActionRequest);
		this.tradeCapture = tradeCapture;
		this.tradeCaptureAction = tradeCaptureAction;
		this.actionResponse=actionResponse;
	}

	

	
	/**
	 * Gets the trade capture.
	 *
	 * @return the trade capture
	 */
	public long getTradeCapture() {
	
		return tradeCapture;
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
	 * Gets the action response.
	 *
	 * @return the action response
	 */
	public ActionResponse getActionResponse() {
	
		return actionResponse;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.AbstractSAPResponse#handleAbstractSAPResponse(net.sourceforge.fixagora.sap.shared.communication.SAPResponses)
	 */
	@Override
	public void handleAbstractSAPResponse(SAPResponses sapResponses) {

		sapResponses.onSAPTradeCaptureActionResponse(this);
		
	}

}
