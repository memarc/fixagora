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

import org.jboss.netty.channel.Channel;


/**
 * The Class CloseExcelTradeCaptureRequest.
 */
public class CloseExcelTradeCaptureRequest extends AbstractExcelRequest {

	private static final long serialVersionUID = 1L;

	private long tradeCapture = 0;
		
	/**
	 * Instantiates a new close excel trade capture request.
	 *
	 * @param tradeCapture the trade capture
	 * @param requestID the request id
	 */
	public CloseExcelTradeCaptureRequest(long tradeCapture, long requestID) {

		super(requestID);
		this.tradeCapture = tradeCapture;
	}

	

	
	/**
	 * Gets the excel trade capture.
	 *
	 * @return the excel trade capture
	 */
	public long getExcelTradeCapture() {
	
		return tradeCapture;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.AbstractExcelRequest#handleAbstractExcelRequest(net.sourceforge.fixagora.excel.shared.communication.ExcelRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractExcelRequest(ExcelRequests excelRequests, Channel channel) {

		excelRequests.onCloseExcelTradeCaptureRequest(this, channel);
		
	}




}
