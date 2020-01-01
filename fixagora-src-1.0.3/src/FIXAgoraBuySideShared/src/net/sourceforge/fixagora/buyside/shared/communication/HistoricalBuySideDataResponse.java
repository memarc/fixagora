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


/**
 * The Class HistoricalBuySideDataResponse.
 */
public class HistoricalBuySideDataResponse extends AbstractBuySideResponse {

	private static final long serialVersionUID = 1L;
	
	private long buysideQuotePage = 0;
	
	private byte[] historicalData = null;
	
	

	/**
	 * Instantiates a new historical buy side data response.
	 *
	 * @param buysideQuotePage the buyside quote page
	 * @param historicalData the historical data
	 */
	public HistoricalBuySideDataResponse(long buysideQuotePage, byte[] historicalData) {

		super(null);
		this.buysideQuotePage = buysideQuotePage;
		this.historicalData = historicalData;
	}



	
	/**
	 * Gets the buyside quote page.
	 *
	 * @return the buyside quote page
	 */
	public long getBuysideQuotePage() {
	
		return buysideQuotePage;
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
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideResponse#handleBuySideResponse(net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses)
	 */
	@Override
	public void handleBuySideResponse(BuySideResponses buySideResponses) {

		buySideResponses.onHistoricalBuySideDataResponse(this);
		
	}

}
