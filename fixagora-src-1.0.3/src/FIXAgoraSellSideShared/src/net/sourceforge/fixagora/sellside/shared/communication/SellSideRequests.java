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
package net.sourceforge.fixagora.sellside.shared.communication;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;

/**
 * The Interface SellSideRequests.
 */
public interface SellSideRequests extends AbstractRequests {

	/**
	 * On close quote monitor request.
	 *
	 * @param closeQuoteMonitorRequest the close quote monitor request
	 * @param channel the channel
	 */
	void onCloseQuoteMonitorRequest(CloseQuoteMonitorRequest closeQuoteMonitorRequest, Channel channel);

	/**
	 * On open quote monitor request.
	 *
	 * @param openQuoteMonitorRequest the open quote monitor request
	 * @param channel the channel
	 */
	void onOpenQuoteMonitorRequest(OpenQuoteMonitorRequest openQuoteMonitorRequest, Channel channel);

	/**
	 * On close sell side book request.
	 *
	 * @param closeSellSideBookRequest the close sell side book request
	 * @param channel the channel
	 */
	void onCloseSellSideBookRequest(CloseSellSideBookRequest closeSellSideBookRequest, Channel channel);

	/**
	 * On open sell side book request.
	 *
	 * @param openSellSideBookRequest the open sell side book request
	 * @param channel the channel
	 */
	void onOpenSellSideBookRequest(OpenSellSideBookRequest openSellSideBookRequest, Channel channel);

	/**
	 * On new order single request.
	 *
	 * @param newOrderSingleRequest the new order single request
	 * @param channel the channel
	 */
	void onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel);

	/**
	 * On sell side quote request request.
	 *
	 * @param sellSideQuoteRequestRequest the sell side quote request request
	 * @param channel the channel
	 */
	void onSellSideQuoteRequestRequest(SellSideQuoteRequestRequest sellSideQuoteRequestRequest, Channel channel);
	
}
