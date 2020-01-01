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

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponses;



/**
 * The Interface BuySideResponses.
 */
public interface BuySideResponses extends AbstractResponses {

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void onUpdateSellSideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateSellSideMDInputEntryResponse);

	/**
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse);

	/**
	 * On historical buy side data response.
	 *
	 * @param historicalDataResponse the historical data response
	 */
	public void onHistoricalBuySideDataResponse(HistoricalBuySideDataResponse historicalDataResponse);

	/**
	 * On buy side quote request response.
	 *
	 * @param quoteRequestResponse the quote request response
	 */
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse);
	
}
