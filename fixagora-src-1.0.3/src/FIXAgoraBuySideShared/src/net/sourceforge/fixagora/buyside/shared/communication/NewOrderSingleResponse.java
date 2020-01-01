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
 * The Class NewOrderSingleResponse.
 */
public class NewOrderSingleResponse extends AbstractBuySideResponse {

	private static final long serialVersionUID = 1L;
	
	private BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = null;
		
	/**
	 * Instantiates a new new order single response.
	 *
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 */
	public NewOrderSingleResponse(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		super(null);
		this.buySideNewOrderSingleEntry = buySideNewOrderSingleEntry;
	}
	

	/**
	 * Instantiates a new new order single response.
	 *
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 * @param newOrderSingleRequest the new order single request
	 */
	public NewOrderSingleResponse(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry, NewOrderSingleRequest newOrderSingleRequest) {

		super(newOrderSingleRequest);
		this.buySideNewOrderSingleEntry = buySideNewOrderSingleEntry;
	}
	
	
	/**
	 * Gets the buy side new order single entry.
	 *
	 * @return the buy side new order single entry
	 */
	public BuySideNewOrderSingleEntry getBuySideNewOrderSingleEntry() {
	
		return buySideNewOrderSingleEntry;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideResponse#handleBuySideResponse(net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses)
	 */
	@Override
	public void handleBuySideResponse(BuySideResponses buySideResponses) {

		buySideResponses.onNewOrderSingleResponse(this);
		
	}

}
