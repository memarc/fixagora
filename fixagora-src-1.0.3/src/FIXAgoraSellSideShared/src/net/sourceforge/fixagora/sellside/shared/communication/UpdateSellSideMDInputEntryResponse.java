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

import java.util.List;

import net.sourceforge.fixagora.sellside.shared.control.MDInputEntryConverter;

/**
 * The Class UpdateSellSideMDInputEntryResponse.
 */
public class UpdateSellSideMDInputEntryResponse extends AbstractSellSideResponse {

	private static final long serialVersionUID = 1L;
	
	private byte[] sellSideMDInputEntries=null;

	/**
	 * Instantiates a new update sell side md input entry response.
	 *
	 * @param sellSideMDInputEntries the sell side md input entries
	 */
	public UpdateSellSideMDInputEntryResponse(List<SellSideMDInputEntry> sellSideMDInputEntries) {

		super(null);
		this.sellSideMDInputEntries = MDInputEntryConverter.getBytes(sellSideMDInputEntries);
	}	

	
	/**
	 * Gets the sell side md input entries.
	 *
	 * @return the sell side md input entries
	 */
	public List<SellSideMDInputEntry> getSellSideMDInputEntries() {
	
		return MDInputEntryConverter.getSellSideMDInputEntries(sellSideMDInputEntries);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideResponse#handleSellSideResponse(net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses)
	 */
	@Override
	public void handleSellSideResponse(SellSideResponses sellSideResponses) {

		sellSideResponses.onUpdateSellSideMDInputEntryResponse(this);
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "UpdateSellSideMDInputEntryResponse length="+sellSideMDInputEntries.length;
	}

}
