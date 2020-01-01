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

import java.util.List;

import org.jboss.netty.channel.Channel;


/**
 * The Class SAPTradeCaptureEntryRequest.
 */
public class SAPTradeCaptureEntryRequest extends AbstractSAPRequest {

	private static final long serialVersionUID = 1L;

	private List<SAPTradeCaptureEntry> tradeCaptureEntries = null;
		
	/**
	 * Instantiates a new sAP trade capture entry request.
	 *
	 * @param tradeCaptureEntries the trade capture entries
	 * @param requestID the request id
	 */
	public SAPTradeCaptureEntryRequest(List<SAPTradeCaptureEntry> tradeCaptureEntries, long requestID) {

		super(requestID);
		this.tradeCaptureEntries = tradeCaptureEntries;
	}


	
	/**
	 * Gets the trade capture entries.
	 *
	 * @return the trade capture entries
	 */
	public List<SAPTradeCaptureEntry> getTradeCaptureEntries() {
	
		return tradeCaptureEntries;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.AbstractSAPRequest#handleAbstractSAPRequest(net.sourceforge.fixagora.sap.shared.communication.SAPRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractSAPRequest(SAPRequests excelRequests, Channel channel) {

		excelRequests.onSAPTradeCaptureEntryRequest(this, channel);
		
	}




}
