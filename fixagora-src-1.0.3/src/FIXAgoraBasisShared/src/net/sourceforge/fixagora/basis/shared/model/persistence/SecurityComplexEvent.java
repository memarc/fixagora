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
package net.sourceforge.fixagora.basis.shared.model.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class SecurityComplexEvent.
 */
@Entity
public class SecurityComplexEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="securitydetails_id", insertable=false, updatable=false, nullable=false)
    private SecurityDetails security;
	
	@Column(unique = false, nullable = true)
	private Integer eventType = null;
	
	@Column(unique = false, nullable = true)
	private Double optionPayoutAmount = null;
	
	@Column(unique = false, nullable = true)
	private Double eventPrice = null;
	
	@Column(unique = false, nullable = true)
	private Integer eventPriceBoundaryMethod = null;
	
	@Column(unique = false, nullable = true)
	private Double eventPriceBoundaryPrecision = null;
	
	@Column(unique = false, nullable = true)
	private Integer eventPriceTimeType = null;
	
	@Column(unique = false, nullable = true)
	private Integer eventCondition = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
	@OrderBy(value="eventStartDate asc")
    @JoinColumn(name="securitycomplexevent_id", nullable=false)
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}

	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
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


	/**
	 * Make eager.
	 */
	public void makeEager() {

		if(complexEventDates!=null)
		{
			complexEventDates.size();
			for(ComplexEventDate complexEventDate: complexEventDates)
				complexEventDate.makeEager();
		}
		
	}

	
	
}
