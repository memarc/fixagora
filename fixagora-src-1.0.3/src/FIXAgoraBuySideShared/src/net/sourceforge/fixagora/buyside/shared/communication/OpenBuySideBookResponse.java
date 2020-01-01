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

import java.util.Collection;

/**
 * The Class OpenBuySideBookResponse.
 */
public class OpenBuySideBookResponse extends AbstractBuySideResponse {

	private static final long serialVersionUID = 1L;
	
	private Collection<AbstractBuySideEntry> abstractBuySideEntries = null;
		
	/**
	 * Instantiates a new open buy side book response.
	 *
	 * @param buySideBookRequest the buy side book request
	 * @param abstractBuySideEntries the abstract buy side entries
	 */
	public OpenBuySideBookResponse(OpenBuySideBookRequest buySideBookRequest, Collection<AbstractBuySideEntry> abstractBuySideEntries) {

		super(buySideBookRequest);
		this.abstractBuySideEntries = abstractBuySideEntries;
	}
	
	

	
	/**
	 * Gets the abstract buy side entries.
	 *
	 * @return the abstract buy side entries
	 */
	public Collection<AbstractBuySideEntry> getAbstractBuySideEntries() {
	
		return abstractBuySideEntries;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideResponse#handleBuySideResponse(net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses)
	 */
	@Override
	public void handleBuySideResponse(BuySideResponses buySideResponses) {

	}

}
