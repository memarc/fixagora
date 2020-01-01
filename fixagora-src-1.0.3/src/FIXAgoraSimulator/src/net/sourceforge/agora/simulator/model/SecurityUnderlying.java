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
package net.sourceforge.agora.simulator.model;


/**
 * The Class SecurityUnderlying.
 */
public class SecurityUnderlying {
	
   private SecurityDetails security;
	
	private Security underlyingSecurity = null;
	
	private Double endPrice = null;
	
	private Double startValue = null;
	
	private Double endValue = null;
	
	private Double allocationPercent = null;
	
	private Integer settlementType = null;
	
	private Double cashAmount = null;
	
	private String cashType = null;
		


	
	
	/**
	 * Gets the security.
	 *
	 * @return the security
	 */
	public SecurityDetails getSecurity() {
	
		return security;
	}




	
	/**
	 * Sets the security.
	 *
	 * @param security the new security
	 */
	public void setSecurity(SecurityDetails security) {
	
		this.security = security;
	}




	/**
	 * Gets the underlying security.
	 *
	 * @return the underlying security
	 */
	public Security getUnderlyingSecurity() {
	
		return underlyingSecurity;
	}



	
	/**
	 * Sets the underlying security.
	 *
	 * @param underlyingSecurity the new underlying security
	 */
	public void setUnderlyingSecurity(Security underlyingSecurity) {
	
		this.underlyingSecurity = underlyingSecurity;
	}



	
	/**
	 * Gets the end price.
	 *
	 * @return the end price
	 */
	public Double getEndPrice() {
	
		return endPrice;
	}



	
	/**
	 * Sets the end price.
	 *
	 * @param endPrice the new end price
	 */
	public void setEndPrice(Double endPrice) {
	
		this.endPrice = endPrice;
	}



	
	/**
	 * Gets the start value.
	 *
	 * @return the start value
	 */
	public Double getStartValue() {
	
		return startValue;
	}



	
	/**
	 * Sets the start value.
	 *
	 * @param startValue the new start value
	 */
	public void setStartValue(Double startValue) {
	
		this.startValue = startValue;
	}



	
	/**
	 * Gets the end value.
	 *
	 * @return the end value
	 */
	public Double getEndValue() {
	
		return endValue;
	}



	
	/**
	 * Sets the end value.
	 *
	 * @param endValue the new end value
	 */
	public void setEndValue(Double endValue) {
	
		this.endValue = endValue;
	}



	
	/**
	 * Gets the allocation percent.
	 *
	 * @return the allocation percent
	 */
	public Double getAllocationPercent() {
	
		return allocationPercent;
	}



	
	/**
	 * Sets the allocation percent.
	 *
	 * @param allocationPercent the new allocation percent
	 */
	public void setAllocationPercent(Double allocationPercent) {
	
		this.allocationPercent = allocationPercent;
	}



	
	/**
	 * Gets the settlement type.
	 *
	 * @return the settlement type
	 */
	public Integer getSettlementType() {
	
		return settlementType;
	}



	
	/**
	 * Sets the settlement type.
	 *
	 * @param settlementType the new settlement type
	 */
	public void setSettlementType(Integer settlementType) {
	
		this.settlementType = settlementType;
	}



	
	/**
	 * Gets the cash amount.
	 *
	 * @return the cash amount
	 */
	public Double getCashAmount() {
	
		return cashAmount;
	}



	
	/**
	 * Sets the cash amount.
	 *
	 * @param cashAmount the new cash amount
	 */
	public void setCashAmount(Double cashAmount) {
	
		this.cashAmount = cashAmount;
	}



	
	/**
	 * Gets the cash type.
	 *
	 * @return the cash type
	 */
	public String getCashType() {
	
		return cashType;
	}



	
	/**
	 * Sets the cash type.
	 *
	 * @param cashType the new cash type
	 */
	public void setCashType(String cashType) {
	
		this.cashType = cashType;
	}



	
}
