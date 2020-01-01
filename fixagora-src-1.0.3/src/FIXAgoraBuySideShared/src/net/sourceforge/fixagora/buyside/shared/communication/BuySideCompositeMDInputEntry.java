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

import java.io.Serializable;

/**
 * The Class BuySideCompositeMDInputEntry.
 */
public class BuySideCompositeMDInputEntry extends AbstractBuySideMDInputEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public BuySideCompositeMDInputEntry clone()
	{
		BuySideCompositeMDInputEntry buySideCompositeMDInputEntry = new BuySideCompositeMDInputEntry();
		buySideCompositeMDInputEntry.setHighAskPxValue(getHighAskPxValue());
		buySideCompositeMDInputEntry.setHighBidPxValue(getHighBidPxValue());
		buySideCompositeMDInputEntry.setLowAskPxValue(getLowAskPxValue());
		buySideCompositeMDInputEntry.setLowBidPxValue(getLowBidPxValue());
		buySideCompositeMDInputEntry.setMdAskPriceDeltaValue(getMdAskPriceDeltaValue());
		buySideCompositeMDInputEntry.setMdBidPriceDeltaValue(getMdBidPriceDeltaValue());
		buySideCompositeMDInputEntry.setMdEntryAskPxValue(getMdEntryAskPxValue());
		buySideCompositeMDInputEntry.setMdEntryAskSizeValue(getMdEntryAskSizeValue());
		buySideCompositeMDInputEntry.setMdEntryAskYieldValue(getMdEntryAskYieldValue());
		buySideCompositeMDInputEntry.setMdEntryBidPxValue(getMdEntryBidPxValue());
		buySideCompositeMDInputEntry.setMdEntryBidSizeValue(getMdEntryBidSizeValue());
		buySideCompositeMDInputEntry.setMdEntryBidYieldValue(getMdEntryBidYieldValue());
		buySideCompositeMDInputEntry.setSecurityValue(getSecurityValue());
		buySideCompositeMDInputEntry.setTime(getTime());
		
		return buySideCompositeMDInputEntry;
	}
		
}
