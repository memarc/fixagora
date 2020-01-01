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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class FIXInitiator.
 */
@Entity
public class FIXInitiator extends AbstractInitiator {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum MarketDataType.
	 */
	public enum MarketDataType {
		
		/** The unsubscribed incremental refresh. */
		UNSUBSCRIBED_INCREMENTAL_REFRESH, 
 /** The unsubscribed full refresh. */
 UNSUBSCRIBED_FULL_REFRESH, 
 /** The maket data request. */
 MAKET_DATA_REQUEST
	}

	@Column(unique = false, nullable = false)
	private String socketAdress = null;

	@Column(unique = false, nullable = false)
	private Integer socketPort = null;

	@Column(unique = false, nullable = false)
	private String senderCompID = null;

	@Column(unique = false, nullable = false)
	private String targetCompID = null;

	@Column(unique = false, nullable = false)
	private Integer heartbeatInterval = null;

	@Column(unique = false, nullable = false)
	private Integer reconnectInterval = null;

	@Column(unique = false, nullable = false)
	private String dataDictionary = null;
	
	@Column(unique = false, nullable = false)
	private Boolean shutdownAfterSnapshot=false;
	
	@Column(unique = false, nullable = false)
	private Integer requestType=0;

	@Column(unique = false, nullable = false)
	private Boolean bid=false;

	@Column(unique = false, nullable = false)
	private Boolean offer=false;

	@Column(unique = false, nullable = false)
	private Boolean recoveryRateForShort=false;

	@Column(unique = false, nullable = false)
	private Boolean recoveryRateForLong=false;

	@Column(unique = false, nullable = false)
	private Boolean recoveryRate=false;

	@Column(unique = false, nullable = false)
	private Boolean cashRate=false;

	@Column(unique = false, nullable = false)
	private Boolean fixingPrice=false;

	@Column(unique = false, nullable = false, name="CVAFS")
	private Boolean cumulativeValueAdjustmentForShort=false;

	@Column(unique = false, nullable = false, name="DVAFS")
	private Boolean dailyValueAdjustmentForShort=false;

	@Column(unique = false, nullable = false, name="CVAFL")
	private Boolean cumulativeValueAdjustmentForLong=false;

	@Column(unique = false, nullable = false, name="DVAFL")
	private Boolean dailyValueAdjustmentForLong=false;

	@Column(unique = false, nullable = false)
	private Boolean swapValueFactor=false;

	@Column(unique = false, nullable = false)
	private Boolean auctionClearingPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean earlyPrices=false;

	@Column(unique = false, nullable = false)
	private Boolean sessionLowOffer=false;

	@Column(unique = false, nullable = false)
	private Boolean sessionHighBid=false;

	@Column(unique = false, nullable = false)
	private Boolean priorSettlePrice=false;

	@Column(unique = false, nullable = false)
	private Boolean settleLowPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean settleHighPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean midPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean marginRate=false;

	@Column(unique = false, nullable = false)
	private Boolean simulatedBuyPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean simulatedSellPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean compositeUnderlying=false;

	@Column(unique = false, nullable = false)
	private Boolean openInterest=false;

	@Column(unique = false, nullable = false)
	private Boolean tradeVolume=false;

	@Column(unique = false, nullable = false)
	private Boolean tradingSessionVWAPPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean tradingSessionLowPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean tradingSessionHighPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean settlementPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean closingPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean openingPrice=false;

	@Column(unique = false, nullable = false)
	private Boolean indexValue=false;

	@Column(unique = false, nullable = false)
	private Boolean trade=false;
	
	@Column(unique = false, nullable = true)
	private Boolean securityListRequest=false;

	@Column(unique = false, nullable = true)
	private String partyID = null;
	
	@Column(unique = false, nullable = true)
	private Integer partyRole = null;
	
	@Column(unique = false, nullable = true)
	private String partyIDSource = null;
		
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "fixinitiator_id", nullable = false)
	private List<AssignedInitiatorSecurity> assignedInitiatorSecurities;
	
	@Column(unique = false, nullable = false)
	private Boolean persistMessage=false;
		
	@Column(unique = false, nullable = false)
	private MarketDataType marketDataType = null;
		




	
	/**
	 * Gets the security list request.
	 *
	 * @return the security list request
	 */
	public Boolean getSecurityListRequest() {
	
		return securityListRequest;
	}




	
	/**
	 * Sets the security list request.
	 *
	 * @param securityListRequest the new security list request
	 */
	public void setSecurityListRequest(Boolean securityListRequest) {
	
		this.securityListRequest = securityListRequest;
	}




	/**
	 * Gets the market data type.
	 *
	 * @return the market data type
	 */
	public MarketDataType getMarketDataType() {
	
		return marketDataType;
	}



	
	/**
	 * Sets the market data type.
	 *
	 * @param marketDataType the new market data type
	 */
	public void setMarketDataType(MarketDataType marketDataType) {
	
		this.marketDataType = marketDataType;
	}



	/**
	 * Gets the persist message.
	 *
	 * @return the persist message
	 */
	public Boolean getPersistMessage() {
	
		return persistMessage;
	}


	
	/**
	 * Sets the persist message.
	 *
	 * @param persistMessage the new persist message
	 */
	public void setPersistMessage(Boolean persistMessage) {
	
		this.persistMessage = persistMessage;
	}


	




	/**
	 * Gets the party id.
	 *
	 * @return the party id
	 */
	public String getPartyID() {
	
		return partyID;
	}




	
	/**
	 * Sets the party id.
	 *
	 * @param partyID the new party id
	 */
	public void setPartyID(String partyID) {
	
		this.partyID = partyID;
	}



	
	/**
	 * Gets the party role.
	 *
	 * @return the party role
	 */
	public Integer getPartyRole() {
	
		return partyRole;
	}


	
	/**
	 * Sets the party role.
	 *
	 * @param partyRole the new party role
	 */
	public void setPartyRole(Integer partyRole) {
	
		this.partyRole = partyRole;
	}


	
	/**
	 * Gets the party id source.
	 *
	 * @return the party id source
	 */
	public String getPartyIDSource() {
	
		return partyIDSource;
	}


	
	/**
	 * Sets the party id source.
	 *
	 * @param partyIDSource the new party id source
	 */
	public void setPartyIDSource(String partyIDSource) {
	
		this.partyIDSource = partyIDSource;
	}


	/**
	 * Gets the request type.
	 *
	 * @return the request type
	 */
	public Integer getRequestType() {
	
		return requestType;
	}



	
	/**
	 * Sets the request type.
	 *
	 * @param requestType the new request type
	 */
	public void setRequestType(Integer requestType) {
	
		this.requestType = requestType;
	}



	/**
	 * Gets the shutdown after snapshot.
	 *
	 * @return the shutdown after snapshot
	 */
	public Boolean getShutdownAfterSnapshot() {
	
		return shutdownAfterSnapshot;
	}


	
	/**
	 * Sets the shutdown after snapshot.
	 *
	 * @param shutdownAfterSnapshot the new shutdown after snapshot
	 */
	public void setShutdownAfterSnapshot(Boolean shutdownAfterSnapshot) {
	
		this.shutdownAfterSnapshot = shutdownAfterSnapshot;
	}


	


	/**
	 * Gets the bid.
	 *
	 * @return the bid
	 */
	public Boolean getBid() {
	
		return bid;
	}

	
	/**
	 * Sets the bid.
	 *
	 * @param bid the new bid
	 */
	public void setBid(Boolean bid) {
	
		this.bid = bid;
	}

	
	/**
	 * Gets the offer.
	 *
	 * @return the offer
	 */
	public Boolean getOffer() {
	
		return offer;
	}

	
	/**
	 * Sets the offer.
	 *
	 * @param offer the new offer
	 */
	public void setOffer(Boolean offer) {
	
		this.offer = offer;
	}

	
	/**
	 * Gets the recovery rate for short.
	 *
	 * @return the recovery rate for short
	 */
	public Boolean getRecoveryRateForShort() {
	
		return recoveryRateForShort;
	}

	
	/**
	 * Sets the recovery rate for short.
	 *
	 * @param recoveryRateForShort the new recovery rate for short
	 */
	public void setRecoveryRateForShort(Boolean recoveryRateForShort) {
	
		this.recoveryRateForShort = recoveryRateForShort;
	}

	
	/**
	 * Gets the recovery rate for long.
	 *
	 * @return the recovery rate for long
	 */
	public Boolean getRecoveryRateForLong() {
	
		return recoveryRateForLong;
	}

	
	/**
	 * Sets the recovery rate for long.
	 *
	 * @param recoveryRateForLong the new recovery rate for long
	 */
	public void setRecoveryRateForLong(Boolean recoveryRateForLong) {
	
		this.recoveryRateForLong = recoveryRateForLong;
	}

	
	/**
	 * Gets the recovery rate.
	 *
	 * @return the recovery rate
	 */
	public Boolean getRecoveryRate() {
	
		return recoveryRate;
	}

	
	/**
	 * Sets the recovery rate.
	 *
	 * @param recoveryRate the new recovery rate
	 */
	public void setRecoveryRate(Boolean recoveryRate) {
	
		this.recoveryRate = recoveryRate;
	}

	
	/**
	 * Gets the cash rate.
	 *
	 * @return the cash rate
	 */
	public Boolean getCashRate() {
	
		return cashRate;
	}

	
	/**
	 * Sets the cash rate.
	 *
	 * @param cashRate the new cash rate
	 */
	public void setCashRate(Boolean cashRate) {
	
		this.cashRate = cashRate;
	}

	
	/**
	 * Gets the fixing price.
	 *
	 * @return the fixing price
	 */
	public Boolean getFixingPrice() {
	
		return fixingPrice;
	}

	
	/**
	 * Sets the fixing price.
	 *
	 * @param fixingPrice the new fixing price
	 */
	public void setFixingPrice(Boolean fixingPrice) {
	
		this.fixingPrice = fixingPrice;
	}

	
	/**
	 * Gets the cumulative value adjustment for short.
	 *
	 * @return the cumulative value adjustment for short
	 */
	public Boolean getCumulativeValueAdjustmentForShort() {
	
		return cumulativeValueAdjustmentForShort;
	}

	
	/**
	 * Sets the cumulative value adjustment for short.
	 *
	 * @param cumulativeValueAdjustmentForShort the new cumulative value adjustment for short
	 */
	public void setCumulativeValueAdjustmentForShort(Boolean cumulativeValueAdjustmentForShort) {
	
		this.cumulativeValueAdjustmentForShort = cumulativeValueAdjustmentForShort;
	}

	
	/**
	 * Gets the daily value adjustment for short.
	 *
	 * @return the daily value adjustment for short
	 */
	public Boolean getDailyValueAdjustmentForShort() {
	
		return dailyValueAdjustmentForShort;
	}

	
	/**
	 * Sets the daily value adjustment for short.
	 *
	 * @param dailyValueAdjustmentForShort the new daily value adjustment for short
	 */
	public void setDailyValueAdjustmentForShort(Boolean dailyValueAdjustmentForShort) {
	
		this.dailyValueAdjustmentForShort = dailyValueAdjustmentForShort;
	}

	
	/**
	 * Gets the cumulative value adjustment for long.
	 *
	 * @return the cumulative value adjustment for long
	 */
	public Boolean getCumulativeValueAdjustmentForLong() {
	
		return cumulativeValueAdjustmentForLong;
	}

	
	/**
	 * Sets the cumulative value adjustment for long.
	 *
	 * @param cumulativeValueAdjustmentForLong the new cumulative value adjustment for long
	 */
	public void setCumulativeValueAdjustmentForLong(Boolean cumulativeValueAdjustmentForLong) {
	
		this.cumulativeValueAdjustmentForLong = cumulativeValueAdjustmentForLong;
	}

	
	/**
	 * Gets the daily value adjustment for long.
	 *
	 * @return the daily value adjustment for long
	 */
	public Boolean getDailyValueAdjustmentForLong() {
	
		return dailyValueAdjustmentForLong;
	}

	
	/**
	 * Sets the daily value adjustment for long.
	 *
	 * @param dailyValueAdjustmentForLong the new daily value adjustment for long
	 */
	public void setDailyValueAdjustmentForLong(Boolean dailyValueAdjustmentForLong) {
	
		this.dailyValueAdjustmentForLong = dailyValueAdjustmentForLong;
	}

	
	/**
	 * Gets the swap value factor.
	 *
	 * @return the swap value factor
	 */
	public Boolean getSwapValueFactor() {
	
		return swapValueFactor;
	}

	
	/**
	 * Sets the swap value factor.
	 *
	 * @param swapValueFactor the new swap value factor
	 */
	public void setSwapValueFactor(Boolean swapValueFactor) {
	
		this.swapValueFactor = swapValueFactor;
	}

	
	/**
	 * Gets the auction clearing price.
	 *
	 * @return the auction clearing price
	 */
	public Boolean getAuctionClearingPrice() {
	
		return auctionClearingPrice;
	}

	
	/**
	 * Sets the auction clearing price.
	 *
	 * @param auctionClearingPrice the new auction clearing price
	 */
	public void setAuctionClearingPrice(Boolean auctionClearingPrice) {
	
		this.auctionClearingPrice = auctionClearingPrice;
	}

	
	/**
	 * Gets the early prices.
	 *
	 * @return the early prices
	 */
	public Boolean getEarlyPrices() {
	
		return earlyPrices;
	}

	
	/**
	 * Sets the early prices.
	 *
	 * @param earlyPrices the new early prices
	 */
	public void setEarlyPrices(Boolean earlyPrices) {
	
		this.earlyPrices = earlyPrices;
	}

	
	/**
	 * Gets the session low offer.
	 *
	 * @return the session low offer
	 */
	public Boolean getSessionLowOffer() {
	
		return sessionLowOffer;
	}

	
	/**
	 * Sets the session low offer.
	 *
	 * @param sessionLowOffer the new session low offer
	 */
	public void setSessionLowOffer(Boolean sessionLowOffer) {
	
		this.sessionLowOffer = sessionLowOffer;
	}

	
	/**
	 * Gets the session high bid.
	 *
	 * @return the session high bid
	 */
	public Boolean getSessionHighBid() {
	
		return sessionHighBid;
	}

	
	/**
	 * Sets the session high bid.
	 *
	 * @param sessionHighBid the new session high bid
	 */
	public void setSessionHighBid(Boolean sessionHighBid) {
	
		this.sessionHighBid = sessionHighBid;
	}

	
	/**
	 * Gets the prior settle price.
	 *
	 * @return the prior settle price
	 */
	public Boolean getPriorSettlePrice() {
	
		return priorSettlePrice;
	}

	
	/**
	 * Sets the prior settle price.
	 *
	 * @param priorSettlePrice the new prior settle price
	 */
	public void setPriorSettlePrice(Boolean priorSettlePrice) {
	
		this.priorSettlePrice = priorSettlePrice;
	}

	
	/**
	 * Gets the settle low price.
	 *
	 * @return the settle low price
	 */
	public Boolean getSettleLowPrice() {
	
		return settleLowPrice;
	}

	
	/**
	 * Sets the settle low price.
	 *
	 * @param settleLowPrice the new settle low price
	 */
	public void setSettleLowPrice(Boolean settleLowPrice) {
	
		this.settleLowPrice = settleLowPrice;
	}

	
	/**
	 * Gets the settle high price.
	 *
	 * @return the settle high price
	 */
	public Boolean getSettleHighPrice() {
	
		return settleHighPrice;
	}

	
	/**
	 * Sets the settle high price.
	 *
	 * @param settleHighPrice the new settle high price
	 */
	public void setSettleHighPrice(Boolean settleHighPrice) {
	
		this.settleHighPrice = settleHighPrice;
	}

	
	
	/**
	 * Gets the mid price.
	 *
	 * @return the mid price
	 */
	public Boolean getMidPrice() {
	
		return midPrice;
	}

	
	/**
	 * Sets the mid price.
	 *
	 * @param midPrice the new mid price
	 */
	public void setMidPrice(Boolean midPrice) {
	
		this.midPrice = midPrice;
	}

	
	/**
	 * Gets the margin rate.
	 *
	 * @return the margin rate
	 */
	public Boolean getMarginRate() {
	
		return marginRate;
	}

	
	/**
	 * Sets the margin rate.
	 *
	 * @param marginRate the new margin rate
	 */
	public void setMarginRate(Boolean marginRate) {
	
		this.marginRate = marginRate;
	}

	
	/**
	 * Gets the simulated buy price.
	 *
	 * @return the simulated buy price
	 */
	public Boolean getSimulatedBuyPrice() {
	
		return simulatedBuyPrice;
	}

	
	/**
	 * Sets the simulated buy price.
	 *
	 * @param simulatedBuyPrice the new simulated buy price
	 */
	public void setSimulatedBuyPrice(Boolean simulatedBuyPrice) {
	
		this.simulatedBuyPrice = simulatedBuyPrice;
	}

	
	/**
	 * Gets the simulated sell price.
	 *
	 * @return the simulated sell price
	 */
	public Boolean getSimulatedSellPrice() {
	
		return simulatedSellPrice;
	}

	
	/**
	 * Sets the simulated sell price.
	 *
	 * @param simulatedSellPrice the new simulated sell price
	 */
	public void setSimulatedSellPrice(Boolean simulatedSellPrice) {
	
		this.simulatedSellPrice = simulatedSellPrice;
	}

	
	/**
	 * Gets the composite underlying.
	 *
	 * @return the composite underlying
	 */
	public Boolean getCompositeUnderlying() {
	
		return compositeUnderlying;
	}

	
	/**
	 * Sets the composite underlying.
	 *
	 * @param compositeUnderlying the new composite underlying
	 */
	public void setCompositeUnderlying(Boolean compositeUnderlying) {
	
		this.compositeUnderlying = compositeUnderlying;
	}

	
	/**
	 * Gets the open interest.
	 *
	 * @return the open interest
	 */
	public Boolean getOpenInterest() {
	
		return openInterest;
	}

	
	/**
	 * Sets the open interest.
	 *
	 * @param openInterest the new open interest
	 */
	public void setOpenInterest(Boolean openInterest) {
	
		this.openInterest = openInterest;
	}

	
	/**
	 * Gets the trade volume.
	 *
	 * @return the trade volume
	 */
	public Boolean getTradeVolume() {
	
		return tradeVolume;
	}

	
	/**
	 * Sets the trade volume.
	 *
	 * @param tradeVolume the new trade volume
	 */
	public void setTradeVolume(Boolean tradeVolume) {
	
		this.tradeVolume = tradeVolume;
	}

	
	/**
	 * Gets the trading session vwap price.
	 *
	 * @return the trading session vwap price
	 */
	public Boolean getTradingSessionVWAPPrice() {
	
		return tradingSessionVWAPPrice;
	}

	
	/**
	 * Sets the trading session vwap price.
	 *
	 * @param tradingSessionVWAPPrice the new trading session vwap price
	 */
	public void setTradingSessionVWAPPrice(Boolean tradingSessionVWAPPrice) {
	
		this.tradingSessionVWAPPrice = tradingSessionVWAPPrice;
	}

	
	/**
	 * Gets the trading session low price.
	 *
	 * @return the trading session low price
	 */
	public Boolean getTradingSessionLowPrice() {
	
		return tradingSessionLowPrice;
	}

	
	/**
	 * Sets the trading session low price.
	 *
	 * @param tradingSessionLowPrice the new trading session low price
	 */
	public void setTradingSessionLowPrice(Boolean tradingSessionLowPrice) {
	
		this.tradingSessionLowPrice = tradingSessionLowPrice;
	}

	
	/**
	 * Gets the trading session high price.
	 *
	 * @return the trading session high price
	 */
	public Boolean getTradingSessionHighPrice() {
	
		return tradingSessionHighPrice;
	}

	
	/**
	 * Sets the trading session high price.
	 *
	 * @param tradingSessionHighPrice the new trading session high price
	 */
	public void setTradingSessionHighPrice(Boolean tradingSessionHighPrice) {
	
		this.tradingSessionHighPrice = tradingSessionHighPrice;
	}

	
	/**
	 * Gets the settlement price.
	 *
	 * @return the settlement price
	 */
	public Boolean getSettlementPrice() {
	
		return settlementPrice;
	}

	
	/**
	 * Sets the settlement price.
	 *
	 * @param settlementPrice the new settlement price
	 */
	public void setSettlementPrice(Boolean settlementPrice) {
	
		this.settlementPrice = settlementPrice;
	}

	
	/**
	 * Gets the closing price.
	 *
	 * @return the closing price
	 */
	public Boolean getClosingPrice() {
	
		return closingPrice;
	}

	
	/**
	 * Sets the closing price.
	 *
	 * @param closingPrice the new closing price
	 */
	public void setClosingPrice(Boolean closingPrice) {
	
		this.closingPrice = closingPrice;
	}

	
	/**
	 * Gets the opening price.
	 *
	 * @return the opening price
	 */
	public Boolean getOpeningPrice() {
	
		return openingPrice;
	}

	
	/**
	 * Sets the opening price.
	 *
	 * @param openingPrice the new opening price
	 */
	public void setOpeningPrice(Boolean openingPrice) {
	
		this.openingPrice = openingPrice;
	}

	
	/**
	 * Gets the index value.
	 *
	 * @return the index value
	 */
	public Boolean getIndexValue() {
	
		return indexValue;
	}

	
	/**
	 * Sets the index value.
	 *
	 * @param indexValue the new index value
	 */
	public void setIndexValue(Boolean indexValue) {
	
		this.indexValue = indexValue;
	}

	
	/**
	 * Gets the trade.
	 *
	 * @return the trade
	 */
	public Boolean getTrade() {
	
		return trade;
	}

	
	/**
	 * Sets the trade.
	 *
	 * @param trade the new trade
	 */
	public void setTrade(Boolean trade) {
	
		this.trade = trade;
	}

	/**
	 * Gets the assigned initiator securities.
	 *
	 * @return the assigned initiator securities
	 */
	public List<AssignedInitiatorSecurity> getAssignedInitiatorSecurities() {

		if (assignedInitiatorSecurities == null)
			assignedInitiatorSecurities = new ArrayList<AssignedInitiatorSecurity>();
		return assignedInitiatorSecurities;
	}

	/**
	 * Sets the assigned initiator securities.
	 *
	 * @param assignedInitiatorSecurities the new assigned initiator securities
	 */
	public void setAssignedInitiatorSecurities(List<AssignedInitiatorSecurity> assignedInitiatorSecurities) {

		this.assignedInitiatorSecurities = assignedInitiatorSecurities;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#getDataDictionary()
	 */
	public String getDataDictionary() {

		return dataDictionary;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#setDataDictionary(java.lang.String)
	 */
	public void setDataDictionary(String dataDictionary) {

		this.dataDictionary = dataDictionary;
	}

	/**
	 * Gets the socket adress.
	 *
	 * @return the socket adress
	 */
	public String getSocketAdress() {

		return socketAdress;
	}

	/**
	 * Sets the socket adress.
	 *
	 * @param socketAdress the new socket adress
	 */
	public void setSocketAdress(String socketAdress) {

		this.socketAdress = socketAdress;
	}

	/**
	 * Gets the socket port.
	 *
	 * @return the socket port
	 */
	public Integer getSocketPort() {

		return socketPort;
	}

	/**
	 * Sets the socket port.
	 *
	 * @param socketPort the new socket port
	 */
	public void setSocketPort(Integer socketPort) {

		this.socketPort = socketPort;
	}

	/**
	 * Gets the sender comp id.
	 *
	 * @return the sender comp id
	 */
	public String getSenderCompID() {

		return senderCompID;
	}

	/**
	 * Sets the sender comp id.
	 *
	 * @param senderCompID the new sender comp id
	 */
	public void setSenderCompID(String senderCompID) {

		this.senderCompID = senderCompID;
	}

	/**
	 * Gets the target comp id.
	 *
	 * @return the target comp id
	 */
	public String getTargetCompID() {

		return targetCompID;
	}

	/**
	 * Sets the target comp id.
	 *
	 * @param targetCompID the new target comp id
	 */
	public void setTargetCompID(String targetCompID) {

		this.targetCompID = targetCompID;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "FIX Initiator";
	}

	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#getIcon()
	 */
	public String getIcon() {

		if (started == 3)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_pending.png";
		if (started == 2)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_online.png";
		if (started == 1)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_pending.png";
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_offline.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/connect_no.png";
	}

	/**
	 * Gets the heartbeat interval.
	 *
	 * @return the heartbeat interval
	 */
	public Integer getHeartbeatInterval() {

		return heartbeatInterval;
	}

	/**
	 * Sets the heartbeat interval.
	 *
	 * @param heartbeatInterval the new heartbeat interval
	 */
	public void setHeartbeatInterval(Integer heartbeatInterval) {

		this.heartbeatInterval = heartbeatInterval;
	}

	/**
	 * Gets the reconnect interval.
	 *
	 * @return the reconnect interval
	 */
	public Integer getReconnectInterval() {

		return reconnectInterval;
	}

	/**
	 * Sets the reconnect interval.
	 *
	 * @param reconnectInterval the new reconnect interval
	 */
	public void setReconnectInterval(Integer reconnectInterval) {

		this.reconnectInterval = reconnectInterval;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();
		for (AssignedInitiatorSecurity assignedInitiatorSecurity : getAssignedInitiatorSecurities())
			assignedInitiatorSecurity.makeEager();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.basis.server.control.component.FIXInitiatorComponentHandler";
	}


}
