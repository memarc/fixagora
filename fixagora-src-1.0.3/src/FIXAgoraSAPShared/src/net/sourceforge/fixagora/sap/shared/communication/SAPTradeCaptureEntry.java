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
package net.sourceforge.fixagora.sap.shared.communication;

import java.io.Serializable;


/**
 * The Class SAPTradeCaptureEntry.
 */
public class SAPTradeCaptureEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum Side.
	 */
	public enum Side{/** The buy. */
BUY,/** The sell. */
SELL};
	
	/**
	 * The Enum ExportStatus.
	 */
	public enum ExportStatus{/** The new. */
NEW,/** The done. */
DONE,/** The failed. */
FAILED,/** The test done. */
TEST_DONE,/** The test failed. */
TEST_FAILED};	
	
	private String orderId = null;
		
	private long tradeCapture = 0;
	
	private String counterpartyOrderId = null;
	
	private String tradeId = null;
	
	private String execId = null;
	
	private String market = null;
	
	private long security = 0;
	
	private long counterparty = 0;
	
	private Long trader = null;
	
	private long sourceComponent = 0;
				
	private double orderQuantity = 0;
		
	private double cumulativeQuantity = 0;
	
	private Double lastQuantity = null;
	
	private Double lastPrice = null;
	
	private Double lastYield = null;
	
	private Side side = Side.BUY;
	
	private Long user = null;
		
	private long updated = 0;
	
	private long created = 0;
		
	private Long settlementDate = null;
	
	private double leaveQuantity = 0;

	private long id = 0;
	
	private boolean removed = false;

	private String userID = null;
	
	private String sapFinancialTransaction = null;
	
	private ExportStatus exportStatus = ExportStatus.NEW;
		
	
	
	/**
	 * Gets the last yield.
	 *
	 * @return the last yield
	 */
	public Double getLastYield() {
	
		return lastYield;
	}



	
	/**
	 * Sets the last yield.
	 *
	 * @param lastYield the new last yield
	 */
	public void setLastYield(Double lastYield) {
	
		this.lastYield = lastYield;
	}



	/**
	 * Gets the export status.
	 *
	 * @return the export status
	 */
	public ExportStatus getExportStatus() {
	
		return exportStatus;
	}


	
	/**
	 * Sets the export status.
	 *
	 * @param exportStatus the new export status
	 */
	public void setExportStatus(ExportStatus exportStatus) {
	
		this.exportStatus = exportStatus;
	}



	
	/**
	 * Gets the trade capture.
	 *
	 * @return the trade capture
	 */
	public long getTradeCapture() {
	
		return tradeCapture;
	}






	/**
	 * Gets the sap financial transaction.
	 *
	 * @return the sap financial transaction
	 */
	public String getSapFinancialTransaction() {
	
		return sapFinancialTransaction;
	}





	
	/**
	 * Sets the sap financial transaction.
	 *
	 * @param sapFinancialTransaction the new sap financial transaction
	 */
	public void setSapFinancialTransaction(String sapFinancialTransaction) {
	
		this.sapFinancialTransaction = sapFinancialTransaction;
	}





	/**
	 * Checks if is removed.
	 *
	 * @return true, if is removed
	 */
	public boolean isRemoved() {
	
		return removed;
	}




	
	/**
	 * Sets the removed.
	 *
	 * @param removed the new removed
	 */
	public void setRemoved(boolean removed) {
	
		this.removed = removed;
	}




	/**
	 * Gets the trader.
	 *
	 * @return the trader
	 */
	public Long getTrader() {
	
		return trader;
	}


	
	
	/**
	 * Gets the trade id.
	 *
	 * @return the trade id
	 */
	public String getTradeId() {
	
		return tradeId;
	}


	
	/**
	 * Sets the trade id.
	 *
	 * @param tradeId the new trade id
	 */
	public void setTradeId(String tradeId) {
	
		this.tradeId = tradeId;
	}







	
	/**
	 * Gets the exec id.
	 *
	 * @return the exec id
	 */
	public String getExecId() {
	
		return execId;
	}







	
	/**
	 * Sets the exec id.
	 *
	 * @param execId the new exec id
	 */
	public void setExecId(String execId) {
	
		this.execId = execId;
	}







	/**
	 * Sets the trader.
	 *
	 * @param trader the new trader
	 */
	public void setTrader(Long trader) {
	
		this.trader = trader;
	}






	/**
	 * Gets the settlement date.
	 *
	 * @return the settlement date
	 */
	public Long getSettlementDate() {
	
		return settlementDate;
	}





	
	/**
	 * Sets the settlement date.
	 *
	 * @param settlementDate the new settlement date
	 */
	public void setSettlementDate(Long settlementDate) {
	
		this.settlementDate = settlementDate;
	}






	
	/**
	 * Gets the market.
	 *
	 * @return the market
	 */
	public String getMarket() {
	
		return market;
	}




	
	/**
	 * Sets the market.
	 *
	 * @param market the new market
	 */
	public void setMarket(String market) {
	
		this.market = market;
	}




	/**
	 * Gets the leave quantity.
	 *
	 * @return the leave quantity
	 */
	public double getLeaveQuantity() {
	
		return leaveQuantity;
	}

	
	/**
	 * Sets the leave quantity.
	 *
	 * @param leaveQuantity the new leave quantity
	 */
	public void setLeaveQuantity(double leaveQuantity) {
	
		this.leaveQuantity = leaveQuantity;
	}

	


	/**
	 * Gets the updated.
	 *
	 * @return the updated
	 */
	public long getUpdated() {
	
		return updated;
	}

	
	/**
	 * Sets the updated.
	 *
	 * @param updated the new updated
	 */
	public void setUpdated(long updated) {
	
		this.updated = updated;
	}

	
	/**
	 * Gets the created.
	 *
	 * @return the created
	 */
	public long getCreated() {
	
		return created;
	}

	
	/**
	 * Sets the created.
	 *
	 * @param created the new created
	 */
	public void setCreated(long created) {
	
		this.created = created;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public Long getUser() {
	
		return user;
	}
	
	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(Long user) {
	
		this.user = user;
	}

	/**
	 * Gets the last price.
	 *
	 * @return the last price
	 */
	public Double getLastPrice() {
	
		return lastPrice;
	}
	
	/**
	 * Sets the last price.
	 *
	 * @param lastPrice the new last price
	 */
	public void setLastPrice(Double lastPrice) {
	
		this.lastPrice = lastPrice;
	}

	
	
	/**
	 * Gets the sAP trade capture.
	 *
	 * @return the sAP trade capture
	 */
	public long getSAPTradeCapture() {
	
		return tradeCapture;
	}


	
	/**
	 * Sets the trade capture.
	 *
	 * @param tradeCapture the new trade capture
	 */
	public void setTradeCapture(long tradeCapture) {
	
		this.tradeCapture = tradeCapture;
	}



	/**
	 * Gets the order id.
	 *
	 * @return the order id
	 */
	public String getOrderId() {
	
		return orderId;
	}

	
	/**
	 * Sets the order id.
	 *
	 * @param orderId the new order id
	 */
	public void setOrderId(String orderId) {
	
		this.orderId = orderId;
	}

	
	/**
	 * Gets the counterparty order id.
	 *
	 * @return the counterparty order id
	 */
	public String getCounterpartyOrderId() {
	
		return counterpartyOrderId;
	}

	
	/**
	 * Sets the counterparty order id.
	 *
	 * @param counterpartyOrderId the new counterparty order id
	 */
	public void setCounterpartyOrderId(String counterpartyOrderId) {
	
		this.counterpartyOrderId = counterpartyOrderId;
	}

	/**
	 * Gets the side.
	 *
	 * @return the side
	 */
	public Side getSide() {
	
		return side;
	}


	
	/**
	 * Sets the side.
	 *
	 * @param side the new side
	 */
	public void setSide(Side side) {
	
		this.side = side;
	}


	/**
	 * Gets the security.
	 *
	 * @return the security
	 */
	public long getSecurity() {
	
		return security;
	}

	
	/**
	 * Sets the security.
	 *
	 * @param security the new security
	 */
	public void setSecurity(long security) {
	
		this.security = security;
	}

	
	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public long getCounterparty() {
	
		return counterparty;
	}

	
	/**
	 * Sets the counterparty.
	 *
	 * @param counterparty the new counterparty
	 */
	public void setCounterparty(long counterparty) {
	
		this.counterparty = counterparty;
	}

	
	
	/**
	 * Gets the order quantity.
	 *
	 * @return the order quantity
	 */
	public double getOrderQuantity() {
	
		return orderQuantity;
	}

	
	/**
	 * Sets the order quantity.
	 *
	 * @param orderQuantity the new order quantity
	 */
	public void setOrderQuantity(double orderQuantity) {
	
		this.orderQuantity = orderQuantity;
	}

	
	/**
	 * Gets the cumulative quantity.
	 *
	 * @return the cumulative quantity
	 */
	public double getCumulativeQuantity() {
	
		return cumulativeQuantity;
	}

	
	/**
	 * Sets the cumulative quantity.
	 *
	 * @param cumulativeQuantity the new cumulative quantity
	 */
	public void setCumulativeQuantity(double cumulativeQuantity) {
	
		this.cumulativeQuantity = cumulativeQuantity;
	}

	
	/**
	 * Gets the last quantity.
	 *
	 * @return the last quantity
	 */
	public Double getLastQuantity() {
	
		return lastQuantity;
	}

	
	/**
	 * Sets the last quantity.
	 *
	 * @param lastQuantity the new last quantity
	 */
	public void setLastQuantity(Double lastQuantity) {
	
		this.lastQuantity = lastQuantity;
	}







	/**
	 * Gets the source component.
	 *
	 * @return the source component
	 */
	public long getSourceComponent() {

		return sourceComponent;
	}







	/**
	 * Sets the source component.
	 *
	 * @param sourceComponent the new source component
	 */
	public void setSourceComponent(long sourceComponent) {

		this.sourceComponent = sourceComponent;
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
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserID() {

		
		return userID ;
	}





	
	/**
	 * Sets the user id.
	 *
	 * @param userID the new user id
	 */
	public void setUserID(String userID) {
	
		this.userID = userID;
	}







	
}
