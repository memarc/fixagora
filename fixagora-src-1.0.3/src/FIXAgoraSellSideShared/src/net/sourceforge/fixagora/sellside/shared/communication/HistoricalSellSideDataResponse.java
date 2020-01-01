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


/**
 * The Class HistoricalSellSideDataResponse.
 */
public class HistoricalSellSideDataResponse extends AbstractSellSideResponse {

	private static final long serialVersionUID = 1L;
	
	private long sellsideQuotePage = 0;
	
	private byte[] historicalData = null;
	
	

	/**
	 * Instantiates a new historical sell side data response.
	 *
	 * @param sellsideQuotePage the sellside quote page
	 * @param historicalData the historical data
	 */
	public HistoricalSellSideDataResponse(long sellsideQuotePage, byte[] historicalData) {

		super(null);
		this.sellsideQuotePage = sellsideQuotePage;
		this.historicalData = historicalData;
	}



	
	/**
	 * Gets the sellside quote page.
	 *
	 * @return the sellside quote page
	 */
	public long getSellsideQuotePage() {
	
		return sellsideQuotePage;
	}



	
	/**
	 * Gets the historical data.
	 *
	 * @return the historical data
	 */
	public byte[] getHistoricalData() {
	
		return historicalData;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideResponse#handleSellSideResponse(net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses)
	 */
	@Override
	public void handleSellSideResponse(SellSideResponses sellSideResponses) {

		sellSideResponses.onHistoricalSellSideDataResponse(this);
		
	}

}
