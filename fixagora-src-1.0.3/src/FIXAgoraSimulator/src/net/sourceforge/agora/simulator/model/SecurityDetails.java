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
import java.util.Date;
import java.util.List;

/**
 * The Class SecurityDetails.
 */
public class SecurityDetails {

	private String symbolSfx = null;
		
	private String securityIDSource = null;
	
	private List<SecurityAltIDGroup> securityAltIDGroups;
	
	private List<SecurityEvent> securityEvents;
	
	private List<SecurityUnderlying> securityUnderlyings;
	
	private List<SecurityLeg> securityLegs;
	
	private List<SecurityComplexEvent> securityComplexEvents;
	
	private List<SecurityAttribute> securityAttribute;
	
	private Integer product = null;
	
	private String cFICode = null;
	
	private String securityType = null;
	
	private String securitySubType = null;
	
	private String currency = null;

	private Date couponPaymentDate = null;
	
	private Date issueDate = null;
	
	private Date redemptionDate = null;
		
	private Date interestAccrualDate = null;
	
	private Date datedDate = null;
	
	private String countryOfIssue = null;
	
	private String stateOfIssue = null;
	
	private String localeOfIssue = null;
	
	private String issuer = null;
	
	private Double couponRate = null;
	
	private Double contractMultiplier = null;
	
	private Double minPriceIncrement = null;
		
	private Double minPriceIncrementAmount = null;
	
	private Double factor = null;
	
	private String unitOfMeasure = null;
	
	private Double unitOfMeasureQty = null;
	
	private String timeUnit = null;
	
	private Date maturityTime = null;
	
	private Integer contractMultiplierUnit = null;

	private String creditRating = null;
	
	private String instrumentRegistry = null;

	private Double strikePrice = null;	

	private String strikeCurrency = null;
	
	private Double strikeMultiplier = null;

	private Double strikeValue = null;
	
	private Integer strikePriceDeterminationMethod = null;
	
	private Integer strikePriceBoundaryMethod = null;
	
	private Double strikePriceBoundaryPrecision = null;

	private Boolean flexibleIndicator= null;
	
	private Boolean flexProductEligibilityIndicator= null;
	
	private String valuationMethod = null;
	
	private Integer flowScheduleTyped = null;
	
	private String restructuringType = null;
	
	private String seniority = null;
	
	private String description = null;
	
	private String securityExchange = null;
	
	private String securityGroup = null;
	
	private String fpool = null;	
	
	private Date contractSettlMonth = null;

	private Integer cpProgramm = null;
	
	private String cpRegType = null;

	private String securityStatus = null;
	
	private String settlOnOpen = null;
	
	private String instrmtAssignmentMethod = null;

	private Integer positionLimit = null;
	
	private Integer ntPositionLimit = null;	
	
	private String productComplex = null;
	
	private String priceUnitOfMeasure = null;

	private Double priceUnitOfMeasureQty = null;
	
	private String settleMethod = null;
	
	private Integer exerciseStyle = null;
	
	private Double optionPayoutAmount = null;

	private String priceQuoteMethod = null;
	
	private Integer listMethod = null;

	private Double capPrice = null;

	private Double floorPrice = null;
	
	private Integer putOrCall = null;
	
	private Integer optionPayoutType = null;
	
	private Integer underlyingDeterminationMethod = null;
	
	private Integer deliveryForm = null;
	
	private Double percentAtRisk = null;	
	
	


	/**
	 * Gets the security legs.
	 *
	 * @return the security legs
	 */
	public List<SecurityLeg> getSecurityLegs() {
	
		if(securityLegs==null)
			securityLegs = new ArrayList<SecurityLeg>();
		return securityLegs;
	}




	
	/**
	 * Sets the security legs.
	 *
	 * @param securityLegs the new security legs
	 */
	public void setSecurityLegs(List<SecurityLeg> securityLegs) {
	
		this.securityLegs = securityLegs;
	}




	/**
	 * Gets the security underlyings.
	 *
	 * @return the security underlyings
	 */
	public List<SecurityUnderlying> getSecurityUnderlyings() {
		
		if(securityUnderlyings==null)
			securityUnderlyings=new ArrayList<SecurityUnderlying>();
		return securityUnderlyings;
	}



	
	/**
	 * Sets the security underlyings.
	 *
	 * @param securityUnderlying the new security underlyings
	 */
	public void setSecurityUnderlyings(List<SecurityUnderlying> securityUnderlying) {
	
		this.securityUnderlyings = securityUnderlying;
	}



	/**
	 * Gets the security attribute.
	 *
	 * @return the security attribute
	 */
	public List<SecurityAttribute> getSecurityAttribute() {
	
		if(securityAttribute==null)
			securityAttribute = new ArrayList<SecurityAttribute>();
		return securityAttribute;
	}


	
	/**
	 * Sets the security attribute.
	 *
	 * @param securityAttribute the new security attribute
	 */
	public void setSecurityAttribute(List<SecurityAttribute> securityAttribute) {
	
		this.securityAttribute = securityAttribute;
	}


	/**
	 * Gets the security complex events.
	 *
	 * @return the security complex events
	 */
	public List<SecurityComplexEvent> getSecurityComplexEvents() {
	
		if(securityComplexEvents==null)
			securityComplexEvents = new ArrayList<SecurityComplexEvent>();
		return securityComplexEvents;
	}

	
	/**
	 * Sets the security complex events.
	 *
	 * @param securityComplexEvents the new security complex events
	 */
	public void setSecurityComplexEvents(List<SecurityComplexEvent> securityComplexEvents) {
	
		this.securityComplexEvents = securityComplexEvents;
	}

	/**
	 * Gets the security events.
	 *
	 * @return the security events
	 */
	public List<SecurityEvent> getSecurityEvents() {
	
		if(securityEvents==null)
			securityEvents = new ArrayList<SecurityEvent>();
		return securityEvents;
	}

	/**
	 * Sets the security events.
	 *
	 * @param securityEvents the new security events
	 */
	public void setSecurityEvents(List<SecurityEvent> securityEvents) {
	
		this.securityEvents = securityEvents;
	}
	
	/**
	 * Gets the delivery form.
	 *
	 * @return the delivery form
	 */
	public Integer getDeliveryForm() {
	
		return deliveryForm;
	}


	
	/**
	 * Sets the delivery form.
	 *
	 * @param deliveryForm the new delivery form
	 */
	public void setDeliveryForm(Integer deliveryForm) {
	
		this.deliveryForm = deliveryForm;
	}


	

	
	/**
	 * Gets the percent at risk.
	 *
	 * @return the percent at risk
	 */
	public Double getPercentAtRisk() {
	
		return percentAtRisk;
	}



	
	/**
	 * Sets the percent at risk.
	 *
	 * @param percentAtRisk the new percent at risk
	 */
	public void setPercentAtRisk(Double percentAtRisk) {
	
		this.percentAtRisk = percentAtRisk;
	}



	/**
	 * Gets the option payout type.
	 *
	 * @return the option payout type
	 */
	public Integer getOptionPayoutType() {
	
		return optionPayoutType;
	}

	
	/**
	 * Sets the option payout type.
	 *
	 * @param optionPayoutType the new option payout type
	 */
	public void setOptionPayoutType(Integer optionPayoutType) {
	
		this.optionPayoutType = optionPayoutType;
	}

	
	/**
	 * Gets the underlying determination method.
	 *
	 * @return the underlying determination method
	 */
	public Integer getUnderlyingDeterminationMethod() {
	
		return underlyingDeterminationMethod;
	}




	
	/**
	 * Sets the underlying determination method.
	 *
	 * @param underlyingDeterminationMethod the new underlying determination method
	 */
	public void setUnderlyingDeterminationMethod(Integer underlyingDeterminationMethod) {
	
		this.underlyingDeterminationMethod = underlyingDeterminationMethod;
	}




	/**
	 * Gets the flexible indicator.
	 *
	 * @return the flexible indicator
	 */
	public Boolean getFlexibleIndicator() {
	
		return flexibleIndicator;
	}



	
	/**
	 * Sets the flexible indicator.
	 *
	 * @param flexibleIndicator the new flexible indicator
	 */
	public void setFlexibleIndicator(Boolean flexibleIndicator) {
	
		this.flexibleIndicator = flexibleIndicator;
	}



	
	/**
	 * Gets the flex product eligibility indicator.
	 *
	 * @return the flex product eligibility indicator
	 */
	public Boolean getFlexProductEligibilityIndicator() {
	
		return flexProductEligibilityIndicator;
	}



	
	/**
	 * Sets the flex product eligibility indicator.
	 *
	 * @param flexProductEligibilityIndicator the new flex product eligibility indicator
	 */
	public void setFlexProductEligibilityIndicator(Boolean flexProductEligibilityIndicator) {
	
		this.flexProductEligibilityIndicator = flexProductEligibilityIndicator;
	}



	
	/**
	 * Gets the valuation method.
	 *
	 * @return the valuation method
	 */
	public String getValuationMethod() {
	
		return valuationMethod;
	}



	
	/**
	 * Sets the valuation method.
	 *
	 * @param valuationMethod the new valuation method
	 */
	public void setValuationMethod(String valuationMethod) {
	
		this.valuationMethod = valuationMethod;
	}



	
	/**
	 * Gets the flow schedule typed.
	 *
	 * @return the flow schedule typed
	 */
	public Integer getFlowScheduleTyped() {
	
		return flowScheduleTyped;
	}



	
	/**
	 * Sets the flow schedule typed.
	 *
	 * @param flowScheduleTyped the new flow schedule typed
	 */
	public void setFlowScheduleTyped(Integer flowScheduleTyped) {
	
		this.flowScheduleTyped = flowScheduleTyped;
	}



	
	/**
	 * Gets the restructuring type.
	 *
	 * @return the restructuring type
	 */
	public String getRestructuringType() {
	
		return restructuringType;
	}



	
	/**
	 * Sets the restructuring type.
	 *
	 * @param restructuringType the new restructuring type
	 */
	public void setRestructuringType(String restructuringType) {
	
		this.restructuringType = restructuringType;
	}



	
	/**
	 * Gets the seniority.
	 *
	 * @return the seniority
	 */
	public String getSeniority() {
	
		return seniority;
	}



	
	/**
	 * Sets the seniority.
	 *
	 * @param seniority the new seniority
	 */
	public void setSeniority(String seniority) {
	
		this.seniority = seniority;
	}



	/**
	 * Gets the strike price determination method.
	 *
	 * @return the strike price determination method
	 */
	public Integer getStrikePriceDeterminationMethod() {
	
		return strikePriceDeterminationMethod;
	}


	
	/**
	 * Sets the strike price determination method.
	 *
	 * @param strikePriceDeterminationMethod the new strike price determination method
	 */
	public void setStrikePriceDeterminationMethod(Integer strikePriceDeterminationMethod) {
	
		this.strikePriceDeterminationMethod = strikePriceDeterminationMethod;
	}


	
	/**
	 * Gets the strike price boundary method.
	 *
	 * @return the strike price boundary method
	 */
	public Integer getStrikePriceBoundaryMethod() {
	
		return strikePriceBoundaryMethod;
	}


	
	/**
	 * Sets the strike price boundary method.
	 *
	 * @param strikePriceBoundaryMethod the new strike price boundary method
	 */
	public void setStrikePriceBoundaryMethod(Integer strikePriceBoundaryMethod) {
	
		this.strikePriceBoundaryMethod = strikePriceBoundaryMethod;
	}


	
	/**
	 * Gets the strike price boundary precision.
	 *
	 * @return the strike price boundary precision
	 */
	public Double getStrikePriceBoundaryPrecision() {
	
		return strikePriceBoundaryPrecision;
	}


	
	/**
	 * Sets the strike price boundary precision.
	 *
	 * @param strikePriceBoundaryPrecision the new strike price boundary precision
	 */
	public void setStrikePriceBoundaryPrecision(Double strikePriceBoundaryPrecision) {
	
		this.strikePriceBoundaryPrecision = strikePriceBoundaryPrecision;
	}


	/**
	 * Gets the product complex.
	 *
	 * @return the product complex
	 */
	public String getProductComplex() {
	
		return productComplex;
	}

	
	/**
	 * Sets the product complex.
	 *
	 * @param productComplex the new product complex
	 */
	public void setProductComplex(String productComplex) {
	
		this.productComplex = productComplex;
	}



	
	/**
	 * Gets the price unit of measure.
	 *
	 * @return the price unit of measure
	 */
	public String getPriceUnitOfMeasure() {
	
		return priceUnitOfMeasure;
	}



	
	/**
	 * Sets the price unit of measure.
	 *
	 * @param priceUnitOfMeasure the new price unit of measure
	 */
	public void setPriceUnitOfMeasure(String priceUnitOfMeasure) {
	
		this.priceUnitOfMeasure = priceUnitOfMeasure;
	}



	
	/**
	 * Gets the price unit of measure qty.
	 *
	 * @return the price unit of measure qty
	 */
	public Double getPriceUnitOfMeasureQty() {
	
		return priceUnitOfMeasureQty;
	}



	
	/**
	 * Sets the price unit of measure qty.
	 *
	 * @param priceUnitOfMeasureQty the new price unit of measure qty
	 */
	public void setPriceUnitOfMeasureQty(Double priceUnitOfMeasureQty) {
	
		this.priceUnitOfMeasureQty = priceUnitOfMeasureQty;
	}



	
	/**
	 * Gets the settle method.
	 *
	 * @return the settle method
	 */
	public String getSettleMethod() {
	
		return settleMethod;
	}



	
	/**
	 * Sets the settle method.
	 *
	 * @param settleMethod the new settle method
	 */
	public void setSettleMethod(String settleMethod) {
	
		this.settleMethod = settleMethod;
	}



	
	/**
	 * Gets the exercise style.
	 *
	 * @return the exercise style
	 */
	public Integer getExerciseStyle() {
	
		return exerciseStyle;
	}



	
	/**
	 * Sets the exercise style.
	 *
	 * @param exerciseStyle the new exercise style
	 */
	public void setExerciseStyle(Integer exerciseStyle) {
	
		this.exerciseStyle = exerciseStyle;
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
	 * Gets the price quote method.
	 *
	 * @return the price quote method
	 */
	public String getPriceQuoteMethod() {
	
		return priceQuoteMethod;
	}



	
	/**
	 * Sets the price quote method.
	 *
	 * @param priceQuoteMethod the new price quote method
	 */
	public void setPriceQuoteMethod(String priceQuoteMethod) {
	
		this.priceQuoteMethod = priceQuoteMethod;
	}



	
	/**
	 * Gets the list method.
	 *
	 * @return the list method
	 */
	public Integer getListMethod() {
	
		return listMethod;
	}



	
	/**
	 * Sets the list method.
	 *
	 * @param listMethod the new list method
	 */
	public void setListMethod(Integer listMethod) {
	
		this.listMethod = listMethod;
	}



	
	/**
	 * Gets the cap price.
	 *
	 * @return the cap price
	 */
	public Double getCapPrice() {
	
		return capPrice;
	}



	
	/**
	 * Sets the cap price.
	 *
	 * @param capPrice the new cap price
	 */
	public void setCapPrice(Double capPrice) {
	
		this.capPrice = capPrice;
	}



	
	/**
	 * Gets the floor price.
	 *
	 * @return the floor price
	 */
	public Double getFloorPrice() {
	
		return floorPrice;
	}



	
	/**
	 * Sets the floor price.
	 *
	 * @param floorPrice the new floor price
	 */
	public void setFloorPrice(Double floorPrice) {
	
		this.floorPrice = floorPrice;
	}



	
	/**
	 * Gets the put or call.
	 *
	 * @return the put or call
	 */
	public Integer getPutOrCall() {
	
		return putOrCall;
	}



	
	/**
	 * Sets the put or call.
	 *
	 * @param putOrCall the new put or call
	 */
	public void setPutOrCall(Integer putOrCall) {
	
		this.putOrCall = putOrCall;
	}



	/**
	 * Gets the security group.
	 *
	 * @return the security group
	 */
	public String getSecurityGroup() {
	
		return securityGroup;
	}


	
	/**
	 * Sets the security group.
	 *
	 * @param securityGroup the new security group
	 */
	public void setSecurityGroup(String securityGroup) {
	
		this.securityGroup = securityGroup;
	}




	/**
	 * Gets the unit of measure qty.
	 *
	 * @return the unit of measure qty
	 */
	public Double getUnitOfMeasureQty() {
	
		return unitOfMeasureQty;
	}



	
	/**
	 * Sets the unit of measure qty.
	 *
	 * @param unitOfMeasureQty the new unit of measure qty
	 */
	public void setUnitOfMeasureQty(Double unitOfMeasureQty) {
	
		this.unitOfMeasureQty = unitOfMeasureQty;
	}

	
	/**
	 * Gets the coupon payment date.
	 *
	 * @return the coupon payment date
	 */
	public Date getCouponPaymentDate() {
	
		return couponPaymentDate;
	}


	
	/**
	 * Sets the coupon payment date.
	 *
	 * @param couponPaymentDate the new coupon payment date
	 */
	public void setCouponPaymentDate(Date couponPaymentDate) {
	
		this.couponPaymentDate = couponPaymentDate;
	}


	
	/**
	 * Gets the issue date.
	 *
	 * @return the issue date
	 */
	public Date getIssueDate() {
	
		return issueDate;
	}


	
	/**
	 * Sets the issue date.
	 *
	 * @param issueDate the new issue date
	 */
	public void setIssueDate(Date issueDate) {
	
		this.issueDate = issueDate;
	}


	
	/**
	 * Gets the dated date.
	 *
	 * @return the dated date
	 */
	public Date getDatedDate() {
	
		return datedDate;
	}


	
	/**
	 * Sets the dated date.
	 *
	 * @param datedDate the new dated date
	 */
	public void setDatedDate(Date datedDate) {
	
		this.datedDate = datedDate;
	}


	
	/**
	 * Gets the interest accrual date.
	 *
	 * @return the interest accrual date
	 */
	public Date getInterestAccrualDate() {
	
		return interestAccrualDate;
	}


	
	/**
	 * Sets the interest accrual date.
	 *
	 * @param interestAccrualDate the new interest accrual date
	 */
	public void setInterestAccrualDate(Date interestAccrualDate) {
	
		this.interestAccrualDate = interestAccrualDate;
	}


	
	/**
	 * Gets the country of issue.
	 *
	 * @return the country of issue
	 */
	public String getCountryOfIssue() {
	
		return countryOfIssue;
	}


	
	/**
	 * Sets the country of issue.
	 *
	 * @param countryOfIssue the new country of issue
	 */
	public void setCountryOfIssue(String countryOfIssue) {
	
		this.countryOfIssue = countryOfIssue;
	}


	
	/**
	 * Gets the state of issue.
	 *
	 * @return the state of issue
	 */
	public String getStateOfIssue() {
	
		return stateOfIssue;
	}


	
	/**
	 * Sets the state of issue.
	 *
	 * @param stateOfIssue the new state of issue
	 */
	public void setStateOfIssue(String stateOfIssue) {
	
		this.stateOfIssue = stateOfIssue;
	}


	
	/**
	 * Gets the locale of issue.
	 *
	 * @return the locale of issue
	 */
	public String getLocaleOfIssue() {
	
		return localeOfIssue;
	}


	
	/**
	 * Sets the locale of issue.
	 *
	 * @param localeOfIssue the new locale of issue
	 */
	public void setLocaleOfIssue(String localeOfIssue) {
	
		this.localeOfIssue = localeOfIssue;
	}


	
	/**
	 * Gets the issuer.
	 *
	 * @return the issuer
	 */
	public String getIssuer() {
	
		return issuer;
	}


	
	/**
	 * Sets the issuer.
	 *
	 * @param issuer the new issuer
	 */
	public void setIssuer(String issuer) {
	
		this.issuer = issuer;
	}


	
	/**
	 * Gets the redemption date.
	 *
	 * @return the redemption date
	 */
	public Date getRedemptionDate() {
	
		return redemptionDate;
	}


	
	/**
	 * Sets the redemption date.
	 *
	 * @param redemptionDate the new redemption date
	 */
	public void setRedemptionDate(Date redemptionDate) {
	
		this.redemptionDate = redemptionDate;
	}


	
	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
	
		return currency;
	}


	
	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
	
		this.currency = currency;
	}


	
	/**
	 * Gets the coupon rate.
	 *
	 * @return the coupon rate
	 */
	public Double getCouponRate() {
	
		return couponRate;
	}


	
	/**
	 * Sets the coupon rate.
	 *
	 * @param couponRate the new coupon rate
	 */
	public void setCouponRate(Double couponRate) {
	
		this.couponRate = couponRate;
	}


	
	/**
	 * Gets the contract multiplier.
	 *
	 * @return the contract multiplier
	 */
	public Double getContractMultiplier() {
	
		return contractMultiplier;
	}


	
	/**
	 * Sets the contract multiplier.
	 *
	 * @param contractMultiplier the new contract multiplier
	 */
	public void setContractMultiplier(Double contractMultiplier) {
	
		this.contractMultiplier = contractMultiplier;
	}


	
	/**
	 * Gets the min price increment.
	 *
	 * @return the min price increment
	 */
	public Double getMinPriceIncrement() {
	
		return minPriceIncrement;
	}


	
	/**
	 * Sets the min price increment.
	 *
	 * @param minPriceIncrement the new min price increment
	 */
	public void setMinPriceIncrement(Double minPriceIncrement) {
	
		this.minPriceIncrement = minPriceIncrement;
	}
	
	
	/**
	 * Gets the min price increment amount.
	 *
	 * @return the min price increment amount
	 */
	public Double getMinPriceIncrementAmount() {
	
		return minPriceIncrementAmount;
	}
	
	/**
	 * Sets the min price increment amount.
	 *
	 * @param minPriceIncrementAmount the new min price increment amount
	 */
	public void setMinPriceIncrementAmount(Double minPriceIncrementAmount) {
	
		this.minPriceIncrementAmount = minPriceIncrementAmount;
	}
	
	
	/**
	 * Gets the factor.
	 *
	 * @return the factor
	 */
	public Double getFactor() {
	
		return factor;
	}


	
	/**
	 * Sets the factor.
	 *
	 * @param factor the new factor
	 */
	public void setFactor(Double factor) {
	
		this.factor = factor;
	}


	
	/**
	 * Gets the unit of measure.
	 *
	 * @return the unit of measure
	 */
	public String getUnitOfMeasure() {
	
		return unitOfMeasure;
	}


	
	/**
	 * Sets the unit of measure.
	 *
	 * @param unitOfMeasure the new unit of measure
	 */
	public void setUnitOfMeasure(String unitOfMeasure) {
	
		this.unitOfMeasure = unitOfMeasure;
	}


	
	/**
	 * Gets the time unit.
	 *
	 * @return the time unit
	 */
	public String getTimeUnit() {
	
		return timeUnit;
	}


	
	/**
	 * Sets the time unit.
	 *
	 * @param timeUnit the new time unit
	 */
	public void setTimeUnit(String timeUnit) {
	
		this.timeUnit = timeUnit;
	}


	
	/**
	 * Gets the maturity time.
	 *
	 * @return the maturity time
	 */
	public Date getMaturityTime() {
	
		return maturityTime;
	}


	
	/**
	 * Sets the maturity time.
	 *
	 * @param maturityTime the new maturity time
	 */
	public void setMaturityTime(Date maturityTime) {
	
		this.maturityTime = maturityTime;
	}


	/**
	 * Gets the credit rating.
	 *
	 * @return the credit rating
	 */
	public String getCreditRating() {
	
		return creditRating;
	}


	
	/**
	 * Sets the credit rating.
	 *
	 * @param creditRating the new credit rating
	 */
	public void setCreditRating(String creditRating) {
	
		this.creditRating = creditRating;
	}


	
	/**
	 * Gets the instrument registry.
	 *
	 * @return the instrument registry
	 */
	public String getInstrumentRegistry() {
	
		return instrumentRegistry;
	}


	
	/**
	 * Sets the instrument registry.
	 *
	 * @param instrumentRegistry the new instrument registry
	 */
	public void setInstrumentRegistry(String instrumentRegistry) {
	
		this.instrumentRegistry = instrumentRegistry;
	}


	
	/**
	 * Gets the strike price.
	 *
	 * @return the strike price
	 */
	public Double getStrikePrice() {
	
		return strikePrice;
	}


	
	/**
	 * Sets the strike price.
	 *
	 * @param strikePrice the new strike price
	 */
	public void setStrikePrice(Double strikePrice) {
	
		this.strikePrice = strikePrice;
	}


	
	/**
	 * Gets the strike currency.
	 *
	 * @return the strike currency
	 */
	public String getStrikeCurrency() {
	
		return strikeCurrency;
	}


	
	/**
	 * Sets the strike currency.
	 *
	 * @param strikeCurrency the new strike currency
	 */
	public void setStrikeCurrency(String strikeCurrency) {
	
		this.strikeCurrency = strikeCurrency;
	}


	
	/**
	 * Gets the strike multiplier.
	 *
	 * @return the strike multiplier
	 */
	public Double getStrikeMultiplier() {
	
		return strikeMultiplier;
	}


	
	/**
	 * Sets the strike multiplier.
	 *
	 * @param strikeMultiplier the new strike multiplier
	 */
	public void setStrikeMultiplier(Double strikeMultiplier) {
	
		this.strikeMultiplier = strikeMultiplier;
	}


	
	/**
	 * Gets the strike value.
	 *
	 * @return the strike value
	 */
	public Double getStrikeValue() {
	
		return strikeValue;
	}


	
	/**
	 * Sets the strike value.
	 *
	 * @param strikeValue the new strike value
	 */
	public void setStrikeValue(Double strikeValue) {
	
		this.strikeValue = strikeValue;
	}


	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
	
		return description;
	}


	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
	
		this.description = description;
	}


	
	/**
	 * Gets the security exchange.
	 *
	 * @return the security exchange
	 */
	public String getSecurityExchange() {
	
		return securityExchange;
	}


	
	/**
	 * Sets the security exchange.
	 *
	 * @param securityExchange the new security exchange
	 */
	public void setSecurityExchange(String securityExchange) {
	
		this.securityExchange = securityExchange;
	}


	
	/**
	 * Gets the fpool.
	 *
	 * @return the fpool
	 */
	public String getFpool() {
	
		return fpool;
	}


	
	/**
	 * Sets the fpool.
	 *
	 * @param fpool the new fpool
	 */
	public void setFpool(String fpool) {
	
		this.fpool = fpool;
	}


	
	/**
	 * Gets the contract settl month.
	 *
	 * @return the contract settl month
	 */
	public Date getContractSettlMonth() {
	
		return contractSettlMonth;
	}


	
	/**
	 * Sets the contract settl month.
	 *
	 * @param contractSettlMonth the new contract settl month
	 */
	public void setContractSettlMonth(Date contractSettlMonth) {
	
		this.contractSettlMonth = contractSettlMonth;
	}


	
	/**
	 * Gets the cp programm.
	 *
	 * @return the cp programm
	 */
	public Integer getCpProgramm() {
	
		return cpProgramm;
	}


	
	/**
	 * Sets the cp programm.
	 *
	 * @param cpProgramm the new cp programm
	 */
	public void setCpProgramm(Integer cpProgramm) {
	
		this.cpProgramm = cpProgramm;
	}


	
	/**
	 * Gets the cp reg type.
	 *
	 * @return the cp reg type
	 */
	public String getCpRegType() {
	
		return cpRegType;
	}


	
	/**
	 * Sets the cp reg type.
	 *
	 * @param cpRegType the new cp reg type
	 */
	public void setCpRegType(String cpRegType) {
	
		this.cpRegType = cpRegType;
	}


	
	/**
	 * Gets the security status.
	 *
	 * @return the security status
	 */
	public String getSecurityStatus() {
	
		return securityStatus;
	}


	
	/**
	 * Sets the security status.
	 *
	 * @param securityStatus the new security status
	 */
	public void setSecurityStatus(String securityStatus) {
	
		this.securityStatus = securityStatus;
	}


	
	/**
	 * Gets the settl on open.
	 *
	 * @return the settl on open
	 */
	public String getSettlOnOpen() {
	
		return settlOnOpen;
	}


	
	/**
	 * Sets the settl on open.
	 *
	 * @param settlOnOpen the new settl on open
	 */
	public void setSettlOnOpen(String settlOnOpen) {
	
		this.settlOnOpen = settlOnOpen;
	}


	
	/**
	 * Gets the instrmt assignment method.
	 *
	 * @return the instrmt assignment method
	 */
	public String getInstrmtAssignmentMethod() {
	
		return instrmtAssignmentMethod;
	}


	
	/**
	 * Sets the instrmt assignment method.
	 *
	 * @param instrmtAssignmentMethod the new instrmt assignment method
	 */
	public void setInstrmtAssignmentMethod(String instrmtAssignmentMethod) {
	
		this.instrmtAssignmentMethod = instrmtAssignmentMethod;
	}


	
	/**
	 * Gets the position limit.
	 *
	 * @return the position limit
	 */
	public Integer getPositionLimit() {
	
		return positionLimit;
	}


	
	/**
	 * Sets the position limit.
	 *
	 * @param positionLimit the new position limit
	 */
	public void setPositionLimit(Integer positionLimit) {
	
		this.positionLimit = positionLimit;
	}


	
	/**
	 * Gets the nt position limit.
	 *
	 * @return the nt position limit
	 */
	public Integer getNtPositionLimit() {
	
		return ntPositionLimit;
	}


	
	/**
	 * Sets the nt position limit.
	 *
	 * @param ntPositionLimit the new nt position limit
	 */
	public void setNtPositionLimit(Integer ntPositionLimit) {
	
		this.ntPositionLimit = ntPositionLimit;
	}


	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public Integer getProduct() {
	
		return product;
	}

	
	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(Integer product) {
	
		this.product = product;
	}

	
	/**
	 * Gets the c fi code.
	 *
	 * @return the c fi code
	 */
	public String getcFICode() {
	
		return cFICode;
	}

	
	/**
	 * Sets the c fi code.
	 *
	 * @param cFICode the new c fi code
	 */
	public void setcFICode(String cFICode) {
	
		this.cFICode = cFICode;
	}

	
	/**
	 * Gets the security type.
	 *
	 * @return the security type
	 */
	public String getSecurityType() {
	
		return securityType;
	}

	
	/**
	 * Sets the security type.
	 *
	 * @param securityType the new security type
	 */
	public void setSecurityType(String securityType) {
	
		this.securityType = securityType;
	}

	
	/**
	 * Gets the security sub type.
	 *
	 * @return the security sub type
	 */
	public String getSecuritySubType() {
	
		return securitySubType;
	}

	
	/**
	 * Sets the security sub type.
	 *
	 * @param securitySubType the new security sub type
	 */
	public void setSecuritySubType(String securitySubType) {
	
		this.securitySubType = securitySubType;
	}

	/**
	 * Gets the symbol sfx.
	 *
	 * @return the symbol sfx
	 */
	public String getSymbolSfx() {
	
		return symbolSfx;
	}
	
	/**
	 * Sets the symbol sfx.
	 *
	 * @param symbolSfx the new symbol sfx
	 */
	public void setSymbolSfx(String symbolSfx) {
	
		this.symbolSfx = symbolSfx;
	}
	
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
	 * Gets the security alt id groups.
	 *
	 * @return the security alt id groups
	 */
	public List<SecurityAltIDGroup> getSecurityAltIDGroups() {
		
		if(securityAltIDGroups==null)
			securityAltIDGroups = new ArrayList<SecurityAltIDGroup>();
	
		return securityAltIDGroups;
	}
	
	/**
	 * Sets the security alt id groups.
	 *
	 * @param securityAltIDGroups the new security alt id groups
	 */
	public void setSecurityAltIDGroups(List<SecurityAltIDGroup> securityAltIDGroups) {
	
		this.securityAltIDGroups = securityAltIDGroups;
	}
	
	
	/**
	 * Gets the contract multiplier unit.
	 *
	 * @return the contract multiplier unit
	 */
	public Integer getContractMultiplierUnit() {
	
		return contractMultiplierUnit;
	}

	
	/**
	 * Sets the contract multiplier unit.
	 *
	 * @param contractMultiplierUnit the new contract multiplier unit
	 */
	public void setContractMultiplierUnit(Integer contractMultiplierUnit) {
	
		this.contractMultiplierUnit = contractMultiplierUnit;
	}

		
}
