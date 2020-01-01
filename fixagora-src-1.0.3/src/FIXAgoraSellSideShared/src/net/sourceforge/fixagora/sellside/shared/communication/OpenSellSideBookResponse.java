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

import java.util.Collection;

/**
 * The Class OpenSellSideBookResponse.
 */
public class OpenSellSideBookResponse extends AbstractSellSideResponse {

	private static final long serialVersionUID = 1L;
	
	private Collection<AbstractSellSideEntry> abstractSellSideEntries = null;
		
	/**
	 * Instantiates a new open sell side book response.
	 *
	 * @param sellSideBookRequest the sell side book request
	 * @param abstractSellSideEntries the abstract sell side entries
	 */
	public OpenSellSideBookResponse(OpenSellSideBookRequest sellSideBookRequest, Collection<AbstractSellSideEntry> abstractSellSideEntries) {

		super(sellSideBookRequest);
		this.abstractSellSideEntries  = abstractSellSideEntries;
	}
	
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideResponse#handleSellSideResponse(net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses)
	 */
	@Override
	public void handleSellSideResponse(SellSideResponses sellSideResponses) {

	}



	
	/**
	 * Gets the abstract sell side entries.
	 *
	 * @return the abstract sell side entries
	 */
	public Collection<AbstractSellSideEntry> getAbstractSellSideEntries() {
	
		return abstractSellSideEntries;
	}



}
