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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SecurityComplexEvent.
 */
public class SecurityComplexEvent {


    private SecurityDetails security;
	
	private Integer eventType = null;
	
	private Double optionPayoutAmount = null;
	
	private Double eventPrice = null;
	
	private Integer eventPriceBoundaryMethod = null;
	
	private Double eventPriceBoundaryPrecision = null;
	
	private Integer eventPriceTimeType = null;
	
	private Integer eventCondition = null;
	
	private List<ComplexEventDate> complexEventDates;	
		
	
	/**
	 * Gets the complex event dates.
	 *
	 * @return the complex event dates
	 */
	public List<ComplexEventDate> getComplexEventDates() {
		
		if(complexEventDates==null)
			complexEventDates = new ArrayList<ComplexEventDate>();
	
		return complexEventDates;
	}

	
	/**
	 * Sets the complex event dates.
	 *
	 * @param complexEventDates the new complex event dates
	 */
	public void setComplexEventDates(List<ComplexEventDate> complexEventDates) {
	
		this.complexEventDates = complexEventDates;
	}



	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public Integer getEventType() {
	
		return eventType;
	}


	
	/**
	 * Sets the event type.
	 *
	 * @param eventType the new event type
	 */
	public void setEventType(Integer eventType) {
	
		this.eventType = eventType;
	}


	
	/**
	 * Gets the option payout amount.
	 *
	 * @return the option payout amount
	 */
	public Double getOptionPayoutAmount() {
	
		return optionPayoutAmount;
	}


	
	/**
	 * Sets the option payout amount.
	 *
	 * @param optionPayoutAmount the new option payout amount
	 */
	public void setOptionPayoutAmount(Double optionPayoutAmount) {
	
		this.optionPayoutAmount = optionPayoutAmount;
	}


	
	/**
	 * Gets the event price.
	 *
	 * @return the event price
	 */
	public Double getEventPrice() {
	
		return eventPrice;
	}


	
	/**
	 * Sets the event price.
	 *
	 * @param eventPrice the new event price
	 */
	public void setEventPrice(Double eventPrice) {
	
		this.eventPrice = eventPrice;
	}


	
	/**
	 * Gets the event price boundary method.
	 *
	 * @return the event price boundary method
	 */
	public Integer getEventPriceBoundaryMethod() {
	
		return eventPriceBoundaryMethod;
	}


	
	/**
	 * Sets the event price boundary method.
	 *
	 * @param eventPriceBoundaryMethod the new event price boundary method
	 */
	public void setEventPriceBoundaryMethod(Integer eventPriceBoundaryMethod) {
	
		this.eventPriceBoundaryMethod = eventPriceBoundaryMethod;
	}


	
	/**
	 * Gets the event price boundary precision.
	 *
	 * @return the event price boundary precision
	 */
	public Double getEventPriceBoundaryPrecision() {
	
		return eventPriceBoundaryPrecision;
	}


	
	/**
	 * Sets the event price boundary precision.
	 *
	 * @param eventPriceBoundaryPrecision the new event price boundary precision
	 */
	public void setEventPriceBoundaryPrecision(Double eventPriceBoundaryPrecision) {
	
		this.eventPriceBoundaryPrecision = eventPriceBoundaryPrecision;
	}


	
	/**
	 * Gets the event price time type.
	 *
	 * @return the event price time type
	 */
	public Integer getEventPriceTimeType() {
	
		return eventPriceTimeType;
	}


	
	/**
	 * Sets the event price time type.
	 *
	 * @param eventPriceTimeType the new event price time type
	 */
	public void setEventPriceTimeType(Integer eventPriceTimeType) {
	
		this.eventPriceTimeType = eventPriceTimeType;
	}


	
	/**
	 * Gets the event condition.
	 *
	 * @return the event condition
	 */
	public Integer getEventCondition() {
	
		return eventCondition;
	}


	
	/**
	 * Sets the event condition.
	 *
	 * @param eventCondition the new event condition
	 */
	public void setEventCondition(Integer eventCondition) {
	
		this.eventCondition = eventCondition;
	}



	
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



}
