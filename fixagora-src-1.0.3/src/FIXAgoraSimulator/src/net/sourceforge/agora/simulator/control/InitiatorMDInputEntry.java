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
package net.sourceforge.agora.simulator.control;

import java.io.Serializable;

/**
 * The Class InitiatorMDInputEntry.
 */
public class InitiatorMDInputEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String securityValue = null;
	
	private String securityIDSource = null;			
		
	private Double mdEntryBidPxValue = null;
		
	private Double mdEntryBidSizeValue =  null;
			
	private Double mdBidPriceDeltaValue = null;
		
	private Double highBidPxValue =  null;
	
	private Double lowBidPxValue =  null;
	
	private Double mdEntryAskPxValue = null;
	
	private Double mdEntryAskSizeValue =  null;
			
	private Double mdAskPriceDeltaValue = null;
		
	private Double highAskPxValue =  null;
	
	private Double lowAskPxValue =  null;
		
	private long time = 0;
	

	
	
	
	/**
	 * Gets the security id source.
	 *
	 * @return the security id source
	 */
	public String getSecurityIDSource() {
	
		return securityIDSource;
	}




	
	/**
	 * Sets the security id source.
	 *
	 * @param securityIDSource the new security id source
	 */
	public void setSecurityIDSource(String securityIDSource) {
	
		this.securityIDSource = securityIDSource;
	}




	/**
	 * Gets the security value.
	 *
	 * @return the security value
	 */
	public String getSecurityValue() {
	
		return securityValue;
	}



	
	/**
	 * Sets the security value.
	 *
	 * @param securityValue the new security value
	 */
	public void setSecurityValue(String securityValue) {
	
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


	
}
