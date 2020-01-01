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

import java.io.Serializable;

/**
 * The Class SellSideMDInputEntry.
 */
public class SellSideMDInputEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long securityValue = 0;			
		
	private Double mdEntryBidPxValue = null;
		
	private Double mdEntryBidSizeValue =  null;
	
	private Double mdEntryBidYieldValue =  null;
		
	private Double mdBidPriceDeltaValue = null;
		
	private Double highBidPxValue =  null;
	
	private Double lowBidPxValue =  null;
	
	private Double mdEntryAskPxValue = null;
	
	private Double mdEntryAskSizeValue =  null;
	
	private Double mdEntryAskYieldValue =  null;
		
	private Double mdAskPriceDeltaValue = null;
		
	private Double highAskPxValue =  null;
	
	private Double lowAskPxValue =  null;
		
	private long time = 0;
	
	/**
	 * Gets the security value.
	 *
	 * @return the security value
	 */
	public long getSecurityValue() {
	
		return securityValue;
	}

	
	/**
	 * Sets the security value.
	 *
	 * @param securityValue the new security value
	 */
	public void setSecurityValue(long securityValue) {
	
		this.securityValue = securityValue;
	}


	
	/**
	 * Gets the md entry bid px value.
	 *
	 * @return the md entry bid px value
	 */
	public Double getMdEntryBidPxValue() {
	
		return mdEntryBidPxValue;
	}


	
	/**
	 * Sets the md entry bid px value.
	 *
	 * @param mdEntryBidPxValue the new md entry bid px value
	 */
	public void setMdEntryBidPxValue(Double mdEntryBidPxValue) {
	
		this.mdEntryBidPxValue = mdEntryBidPxValue;
	}


	
	/**
	 * Gets the md entry bid size value.
	 *
	 * @return the md entry bid size value
	 */
	public Double getMdEntryBidSizeValue() {
	
		return mdEntryBidSizeValue;
	}


	
	/**
	 * Sets the md entry bid size value.
	 *
	 * @param mdEntryBidSizeValue the new md entry bid size value
	 */
	public void setMdEntryBidSizeValue(Double mdEntryBidSizeValue) {
	
		this.mdEntryBidSizeValue = mdEntryBidSizeValue;
	}


	
	
	/**
	 * Gets the md entry bid yield value.
	 *
	 * @return the md entry bid yield value
	 */
	public Double getMdEntryBidYieldValue() {
	
		return mdEntryBidYieldValue;
	}


	
	/**
	 * Sets the md entry bid yield value.
	 *
	 * @param mdEntryBidYieldValue the new md entry bid yield value
	 */
	public void setMdEntryBidYieldValue(Double mdEntryBidYieldValue) {
	
		this.mdEntryBidYieldValue = mdEntryBidYieldValue;
	}


	
	/**
	 * Gets the md entry ask yield value.
	 *
	 * @return the md entry ask yield value
	 */
	public Double getMdEntryAskYieldValue() {
	
		return mdEntryAskYieldValue;
	}


	
	/**
	 * Sets the md entry ask yield value.
	 *
	 * @param mdEntryAskYieldValue the new md entry ask yield value
	 */
	public void setMdEntryAskYieldValue(Double mdEntryAskYieldValue) {
	
		this.mdEntryAskYieldValue = mdEntryAskYieldValue;
	}


	/**
	 * Gets the md bid price delta value.
	 *
	 * @return the md bid price delta value
	 */
	public Double getMdBidPriceDeltaValue() {
	
		return mdBidPriceDeltaValue;
	}


	
	/**
	 * Sets the md bid price delta value.
	 *
	 * @param mdBidPriceDeltaValue the new md bid price delta value
	 */
	public void setMdBidPriceDeltaValue(Double mdBidPriceDeltaValue) {
	
		this.mdBidPriceDeltaValue = mdBidPriceDeltaValue;
	}


	
	/**
	 * Gets the high bid px value.
	 *
	 * @return the high bid px value
	 */
	public Double getHighBidPxValue() {
	
		return highBidPxValue;
	}


	
	/**
	 * Sets the high bid px value.
	 *
	 * @param highBidPxValue the new high bid px value
	 */
	public void setHighBidPxValue(Double highBidPxValue) {
	
		this.highBidPxValue = highBidPxValue;
	}


	
	/**
	 * Gets the low bid px value.
	 *
	 * @return the low bid px value
	 */
	public Double getLowBidPxValue() {
	
		return lowBidPxValue;
	}


	
	/**
	 * Sets the low bid px value.
	 *
	 * @param lowBidPxValue the new low bid px value
	 */
	public void setLowBidPxValue(Double lowBidPxValue) {
	
		this.lowBidPxValue = lowBidPxValue;
	}


	
	/**
	 * Gets the md entry ask px value.
	 *
	 * @return the md entry ask px value
	 */
	public Double getMdEntryAskPxValue() {
	
		return mdEntryAskPxValue;
	}


	
	/**
	 * Sets the md entry ask px value.
	 *
	 * @param mdEntryAskPxValue the new md entry ask px value
	 */
	public void setMdEntryAskPxValue(Double mdEntryAskPxValue) {
	
		this.mdEntryAskPxValue = mdEntryAskPxValue;
	}


	
	/**
	 * Gets the md entry ask size value.
	 *
	 * @return the md entry ask size value
	 */
	public Double getMdEntryAskSizeValue() {
	
		return mdEntryAskSizeValue;
	}


	
	/**
	 * Sets the md entry ask size value.
	 *
	 * @param mdEntryAskSizeValue the new md entry ask size value
	 */
	public void setMdEntryAskSizeValue(Double mdEntryAskSizeValue) {
	
		this.mdEntryAskSizeValue = mdEntryAskSizeValue;
	}


	
	/**
	 * Gets the md ask price delta value.
	 *
	 * @return the md ask price delta value
	 */
	public Double getMdAskPriceDeltaValue() {
	
		return mdAskPriceDeltaValue;
	}


	
	/**
	 * Sets the md ask price delta value.
	 *
	 * @param mdAskPriceDeltaValue the new md ask price delta value
	 */
	public void setMdAskPriceDeltaValue(Double mdAskPriceDeltaValue) {
	
		this.mdAskPriceDeltaValue = mdAskPriceDeltaValue;
	}


	
	/**
	 * Gets the high ask px value.
	 *
	 * @return the high ask px value
	 */
	public Double getHighAskPxValue() {
	
		return highAskPxValue;
	}


	
	/**
	 * Sets the high ask px value.
	 *
	 * @param highAskPxValue the new high ask px value
	 */
	public void setHighAskPxValue(Double highAskPxValue) {
	
		this.highAskPxValue = highAskPxValue;
	}


	
	/**
	 * Gets the low ask px value.
	 *
	 * @return the low ask px value
	 */
	public Double getLowAskPxValue() {
	
		return lowAskPxValue;
	}


	
	/**
	 * Sets the low ask px value.
	 *
	 * @param lowAskPxValue the new low ask px value
	 */
	public void setLowAskPxValue(Double lowAskPxValue) {
	
		this.lowAskPxValue = lowAskPxValue;
	}


	
	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public long getTime() {
	
		return time;
	}


	
	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void setTime(long time) {
	
		this.time = time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SellSideMDInputEntry clone()
	{
		SellSideMDInputEntry sellSideMDInputEntry = new SellSideMDInputEntry();
		sellSideMDInputEntry.setHighAskPxValue(getHighAskPxValue());
		sellSideMDInputEntry.setHighBidPxValue(getHighBidPxValue());
		sellSideMDInputEntry.setLowAskPxValue(getLowAskPxValue());
		sellSideMDInputEntry.setLowBidPxValue(getLowBidPxValue());
		sellSideMDInputEntry.setMdAskPriceDeltaValue(getMdAskPriceDeltaValue());
		sellSideMDInputEntry.setMdBidPriceDeltaValue(getMdBidPriceDeltaValue());
		sellSideMDInputEntry.setMdEntryAskPxValue(getMdEntryAskPxValue());
		sellSideMDInputEntry.setMdEntryAskSizeValue(getMdEntryAskSizeValue());
		sellSideMDInputEntry.setMdEntryAskYieldValue(getMdEntryAskYieldValue());
		sellSideMDInputEntry.setMdEntryBidPxValue(getMdEntryBidPxValue());
		sellSideMDInputEntry.setMdEntryBidSizeValue(getMdEntryBidSizeValue());
		sellSideMDInputEntry.setMdEntryBidYieldValue(getMdEntryBidYieldValue());
		sellSideMDInputEntry.setSecurityValue(getSecurityValue());
		sellSideMDInputEntry.setTime(getTime());
		
		return sellSideMDInputEntry;
	}

	
}
