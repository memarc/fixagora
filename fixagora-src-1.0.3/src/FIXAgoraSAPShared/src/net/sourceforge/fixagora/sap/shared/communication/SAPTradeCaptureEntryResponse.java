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

/**
 * The Class SAPTradeCaptureEntryResponse.
 */
public class SAPTradeCaptureEntryResponse extends AbstractSAPResponse {

	private static final long serialVersionUID = 1L;
	
	private SAPTradeCaptureEntry tradeCaptureEntry = null;
		
	/**
	 * Instantiates a new sAP trade capture entry response.
	 *
	 * @param tradeCaptureEntry the trade capture entry
	 */
	public SAPTradeCaptureEntryResponse(SAPTradeCaptureEntry tradeCaptureEntry) {

		super(null);
		this.tradeCaptureEntry = tradeCaptureEntry;
	}
	

	/**
	 * Instantiates a new sAP trade capture entry response.
	 *
	 * @param tradeCaptureEntry the trade capture entry
	 * @param tradeCaptureEntryRequest the trade capture entry request
	 */
	public SAPTradeCaptureEntryResponse(SAPTradeCaptureEntry tradeCaptureEntry, SAPTradeCaptureEntryRequest tradeCaptureEntryRequest) {

		super(tradeCaptureEntryRequest);
		this.tradeCaptureEntry = tradeCaptureEntry;
	}


	
	/**
	 * Gets the sAP trade capture entry.
	 *
	 * @return the sAP trade capture entry
	 */
	public SAPTradeCaptureEntry getSAPTradeCaptureEntry() {
	
		return tradeCaptureEntry;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.AbstractSAPResponse#handleAbstractSAPResponse(net.sourceforge.fixagora.sap.shared.communication.SAPResponses)
	 */
	@Override
	public void handleAbstractSAPResponse(SAPResponses sapResponses) {

		sapResponses.onSAPTradeCaptureEntryResponse(this);
		
	}

}