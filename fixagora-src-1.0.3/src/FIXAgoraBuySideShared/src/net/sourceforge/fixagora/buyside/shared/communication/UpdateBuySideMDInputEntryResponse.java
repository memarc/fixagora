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

import java.util.List;

import net.sourceforge.fixagora.buyside.shared.control.MDInputEntryConverter;

/**
 * The Class UpdateBuySideMDInputEntryResponse.
 */
public class UpdateBuySideMDInputEntryResponse extends AbstractBuySideResponse {

	private static final long serialVersionUID = 1L;
	
	private byte[] buySideMDInputEntries=null;

	/**
	 * Instantiates a new update buy side md input entry response.
	 *
	 * @param buySideMDInputEntries the buy side md input entries
	 */
	public UpdateBuySideMDInputEntryResponse(List<AbstractBuySideMDInputEntry> buySideMDInputEntries) {

		super(null);
		this.buySideMDInputEntries = MDInputEntryConverter.getBytes(buySideMDInputEntries);
	}	

	
	/**
	 * Gets the buy side md input entries.
	 *
	 * @return the buy side md input entries
	 */
	public List<AbstractBuySideMDInputEntry> getBuySideMDInputEntries() {
	
		return MDInputEntryConverter.getBuySideMDInputEntries(buySideMDInputEntries);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideResponse#handleBuySideResponse(net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses)
	 */
	@Override
	public void handleBuySideResponse(BuySideResponses buySideResponses) {

		buySideResponses.onUpdateSellSideMDInputEntryResponse(this);
		
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "UpdateBuySideMDInputEntryResponse length="+buySideMDInputEntries.length;
	}
	
	

}
