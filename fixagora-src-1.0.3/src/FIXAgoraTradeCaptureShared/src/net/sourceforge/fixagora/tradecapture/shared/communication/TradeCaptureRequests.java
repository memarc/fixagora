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

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;

/**
 * The Interface TradeCaptureRequests.
 */
public interface TradeCaptureRequests extends AbstractRequests {

	/**
	 * On close trade capture request.
	 *
	 * @param closeTradeCaptureRequest the close trade capture request
	 * @param channel the channel
	 */
	void onCloseTradeCaptureRequest(CloseTradeCaptureRequest closeTradeCaptureRequest, Channel channel);

	/**
	 * On open trade capture request.
	 *
	 * @param openTradeCaptureRequest the open trade capture request
	 * @param channel the channel
	 */
	void onOpenTradeCaptureRequest(OpenTradeCaptureRequest openTradeCaptureRequest, Channel channel);

	/**
	 * On trade capture entry request.
	 *
	 * @param tradeCaptureEntryRequest the trade capture entry request
	 * @param channel the channel
	 */
	void onTradeCaptureEntryRequest(TradeCaptureEntryRequest tradeCaptureEntryRequest, Channel channel);
	
}
