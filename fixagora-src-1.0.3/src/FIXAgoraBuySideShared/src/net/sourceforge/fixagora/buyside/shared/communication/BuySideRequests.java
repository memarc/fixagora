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
package net.sourceforge.fixagora.buyside.shared.communication;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;

import org.jboss.netty.channel.Channel;

/**
 * The Interface BuySideRequests.
 */
public interface BuySideRequests extends AbstractRequests {

	/**
	 * On close quote page request.
	 *
	 * @param closeQuotePageRequest the close quote page request
	 * @param channel the channel
	 */
	public void onCloseQuotePageRequest(CloseQuotePageRequest closeQuotePageRequest, Channel channel);

	/**
	 * On open quote page request.
	 *
	 * @param openQuotePageRequest the open quote page request
	 * @param channel the channel
	 */
	public void onOpenQuotePageRequest(OpenQuotePageRequest openQuotePageRequest, Channel channel);

	/**
	 * On open quote depth request.
	 *
	 * @param openQuoteDepthRequest the open quote depth request
	 * @param channel the channel
	 */
	public void onOpenQuoteDepthRequest(OpenQuoteDepthRequest openQuoteDepthRequest, Channel channel);

	/**
	 * On new order single request.
	 *
	 * @param newOrderSingleRequest the new order single request
	 * @param channel the channel
	 */
	public void onNewOrderSingleRequest(NewOrderSingleRequest newOrderSingleRequest, Channel channel);

	/**
	 * On open buy side book request.
	 *
	 * @param openBuySideBookRequest the open buy side book request
	 * @param channel the channel
	 */
	public void onOpenBuySideBookRequest(OpenBuySideBookRequest openBuySideBookRequest, Channel channel);

	/**
	 * On close buy side book request.
	 *
	 * @param closeBuySideBookRequest the close buy side book request
	 * @param channel the channel
	 */
	public void onCloseBuySideBookRequest(CloseBuySideBookRequest closeBuySideBookRequest, Channel channel);

	/**
	 * On buy side quote request request.
	 *
	 * @param quoteRequestRequest the quote request request
	 * @param channel the channel
	 */
	public void onBuySideQuoteRequestRequest(BuySideQuoteRequestRequest quoteRequestRequest, Channel channel);

	
}
