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
 * The Class BuySideCounterpartyMDInputEntry.
 */
public class BuySideCounterpartyMDInputEntry extends AbstractBuySideMDInputEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long counterpartyValue = 0;
	
	private long interfaceValue = 0;
	
	
	
	/**
	 * Gets the interface value.
	 *
	 * @return the interface value
	 */
	public long getInterfaceValue() {
	
		return interfaceValue;
	}

	
	/**
	 * Sets the interface value.
	 *
	 * @param interfaceValue the new interface value
	 */
	public void setInterfaceValue(long interfaceValue) {
	
		this.interfaceValue = interfaceValue;
	}



	/**
	 * Gets the counterparty value.
	 *
	 * @return the counterparty value
	 */
	public long getCounterpartyValue() {
	
		return counterpartyValue;
	}


	
	/**
	 * Sets the counterparty value.
	 *
	 * @param counterpartyValue the new counterparty value
	 */
	public void setCounterpartyValue(long counterpartyValue) {
	
		this.counterpartyValue = counterpartyValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public BuySideCounterpartyMDInputEntry clone()
	{
		BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry = new BuySideCounterpartyMDInputEntry();
		buySideCounterpartyMDInputEntry.setHighAskPxValue(getHighAskPxValue());
		buySideCounterpartyMDInputEntry.setHighBidPxValue(getHighBidPxValue());
		buySideCounterpartyMDInputEntry.setLowAskPxValue(getLowAskPxValue());
		buySideCounterpartyMDInputEntry.setLowBidPxValue(getLowBidPxValue());
		buySideCounterpartyMDInputEntry.setMdAskPriceDeltaValue(getMdAskPriceDeltaValue());
		buySideCounterpartyMDInputEntry.setMdBidPriceDeltaValue(getMdBidPriceDeltaValue());
		buySideCounterpartyMDInputEntry.setMdEntryAskPxValue(getMdEntryAskPxValue());
		buySideCounterpartyMDInputEntry.setMdEntryAskSizeValue(getMdEntryAskSizeValue());
		buySideCounterpartyMDInputEntry.setMdEntryAskYieldValue(getMdEntryAskYieldValue());
		buySideCounterpartyMDInputEntry.setMdEntryBidPxValue(getMdEntryBidPxValue());
		buySideCounterpartyMDInputEntry.setMdEntryBidSizeValue(getMdEntryBidSizeValue());
		buySideCounterpartyMDInputEntry.setMdEntryBidYieldValue(getMdEntryBidYieldValue());
		buySideCounterpartyMDInputEntry.setSecurityValue(getSecurityValue());
		buySideCounterpartyMDInputEntry.setTime(getTime());
		
		buySideCounterpartyMDInputEntry.setCounterpartyValue(getCounterpartyValue());
		buySideCounterpartyMDInputEntry.setInterfaceValue(getInterfaceValue());
		
		return buySideCounterpartyMDInputEntry;
	}
	
}
