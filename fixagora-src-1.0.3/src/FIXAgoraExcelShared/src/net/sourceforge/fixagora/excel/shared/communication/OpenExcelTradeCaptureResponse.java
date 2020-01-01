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
package net.sourceforge.fixagora.excel.shared.communication;

import java.util.Collection;

/**
 * The Class OpenExcelTradeCaptureResponse.
 */
public class OpenExcelTradeCaptureResponse extends AbstractExcelResponse {

	private static final long serialVersionUID = 1L;
	
	private Collection<ExcelTradeCaptureEntry> captureEntries = null;
		
	/**
	 * Instantiates a new open excel trade capture response.
	 *
	 * @param openTradeCaptureRequest the open trade capture request
	 * @param captureEntries the capture entries
	 */
	public OpenExcelTradeCaptureResponse(OpenExcelTradeCaptureRequest openTradeCaptureRequest, Collection<ExcelTradeCaptureEntry> captureEntries) {

		super(openTradeCaptureRequest);
		this.captureEntries = captureEntries;
	}
	
	
	
	
	/**
	 * Gets the capture entries.
	 *
	 * @return the capture entries
	 */
	public Collection<ExcelTradeCaptureEntry> getCaptureEntries() {
	
		return captureEntries;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.AbstractExcelResponse#handleAbstractExcelResponse(net.sourceforge.fixagora.excel.shared.communication.ExcelResponses)
	 */
	@Override
	public void handleAbstractExcelResponse(ExcelResponses excelResponses) {

	}

}
