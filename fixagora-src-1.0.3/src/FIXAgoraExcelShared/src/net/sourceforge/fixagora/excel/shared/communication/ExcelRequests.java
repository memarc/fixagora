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

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;


/**
 * The Interface ExcelRequests.
 */
public interface ExcelRequests extends AbstractRequests {

	/**
	 * On security list request.
	 *
	 * @param securityListRequest the security list request
	 * @param channel the channel
	 */
	public void onSecurityListRequest(SecurityListRequest securityListRequest, Channel channel);

	/**
	 * On close excel trade capture request.
	 *
	 * @param closeExcelTradeCaptureRequest the close excel trade capture request
	 * @param channel the channel
	 */
	public void onCloseExcelTradeCaptureRequest(CloseExcelTradeCaptureRequest closeExcelTradeCaptureRequest, Channel channel);

	/**
	 * On open excel trade capture request.
	 *
	 * @param openExcelTradeCaptureRequest the open excel trade capture request
	 * @param channel the channel
	 */
	public void onOpenExcelTradeCaptureRequest(OpenExcelTradeCaptureRequest openExcelTradeCaptureRequest, Channel channel);

	/**
	 * On excel trade capture entry request.
	 *
	 * @param excelTradeCaptureEntryRequest the excel trade capture entry request
	 * @param channel the channel
	 */
	public void onExcelTradeCaptureEntryRequest(ExcelTradeCaptureEntryRequest excelTradeCaptureEntryRequest, Channel channel);

	
}
