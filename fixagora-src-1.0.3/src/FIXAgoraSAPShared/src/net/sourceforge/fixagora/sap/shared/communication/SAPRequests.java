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

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;


/**
 * The Interface SAPRequests.
 */
public interface SAPRequests extends AbstractRequests {

	/**
	 * On close sap trade capture request.
	 *
	 * @param closeSAPTradeCaptureRequest the close sap trade capture request
	 * @param channel the channel
	 */
	public void onCloseSAPTradeCaptureRequest(CloseSAPTradeCaptureRequest closeSAPTradeCaptureRequest, Channel channel);

	/**
	 * On open sap trade capture request.
	 *
	 * @param openSAPTradeCaptureRequest the open sap trade capture request
	 * @param channel the channel
	 */
	public void onOpenSAPTradeCaptureRequest(OpenSAPTradeCaptureRequest openSAPTradeCaptureRequest, Channel channel);

	/**
	 * On sap trade capture entry request.
	 *
	 * @param sapTradeCaptureEntryRequest the sap trade capture entry request
	 * @param channel the channel
	 */
	public void onSAPTradeCaptureEntryRequest(SAPTradeCaptureEntryRequest sapTradeCaptureEntryRequest, Channel channel);

	/**
	 * On sap trade capture action request.
	 *
	 * @param sapTradeCaptureActionRequest the sap trade capture action request
	 * @param channel the channel
	 */
	public void onSAPTradeCaptureActionRequest(SAPTradeCaptureActionRequest sapTradeCaptureActionRequest, Channel channel);

	
}
