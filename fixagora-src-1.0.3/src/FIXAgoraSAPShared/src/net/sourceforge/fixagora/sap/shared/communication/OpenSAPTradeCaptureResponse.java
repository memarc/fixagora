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

import java.util.Collection;

/**
 * The Class OpenSAPTradeCaptureResponse.
 */
public class OpenSAPTradeCaptureResponse extends AbstractSAPResponse {

	private static final long serialVersionUID = 1L;
	
	private Collection<SAPTradeCaptureEntry> captureEntries = null;
	
	private boolean exportInProgress = false;
	
	/** The sap connector exist. */
	boolean sapConnectorExist = false;
		
	/**
	 * Instantiates a new open sap trade capture response.
	 *
	 * @param openTradeCaptureRequest the open trade capture request
	 * @param captureEntries the capture entries
	 * @param exportInProgress the export in progress
	 * @param sapConnectorExist the sap connector exist
	 */
	public OpenSAPTradeCaptureResponse(OpenSAPTradeCaptureRequest openTradeCaptureRequest, Collection<SAPTradeCaptureEntry> captureEntries, boolean exportInProgress, boolean sapConnectorExist) {

		super(openTradeCaptureRequest);
		this.captureEntries = captureEntries;
		this.exportInProgress = exportInProgress;
		this.sapConnectorExist = sapConnectorExist;
	}
	
	
	/**
	 * Checks if is export in progress.
	 *
	 * @return true, if is export in progress
	 */
	public boolean isExportInProgress() {
	
		return exportInProgress;
	}


	/**
	 * Gets the capture entries.
	 *
	 * @return the capture entries
	 */
	public Collection<SAPTradeCaptureEntry> getCaptureEntries() {
	
		return captureEntries;
	}


	
	/**
	 * Checks if is sap connector exist.
	 *
	 * @return true, if is sap connector exist
	 */
	public boolean isSapConnectorExist() {
	
		return sapConnectorExist;
	}


	
	/**
	 * Sets the sap connector exist.
	 *
	 * @param sapConnectorExist the new sap connector exist
	 */
	public void setSapConnectorExist(boolean sapConnectorExist) {
	
		this.sapConnectorExist = sapConnectorExist;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.AbstractSAPResponse#handleAbstractSAPResponse(net.sourceforge.fixagora.sap.shared.communication.SAPResponses)
	 */
	@Override
	public void handleAbstractSAPResponse(SAPResponses excelResponses) {

	}

}
