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
package net.sourceforge.fixagora.sap.shared.persistence;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter;

/**
 * The Class SAPTradeCaptureReport.
 */
@Entity
public class SAPTradeCaptureReport  implements PersistenceInterface {
		
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
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;	
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private SAPTradeCapture sapTradeCapture = null;

	@Column(unique=false,nullable=true)
	private String orderId = null;
	
	@Column(unique=false,nullable=true)
	private String tradeId = null;
	
	@Column(unique=false,nullable=true)
	private String execId = null;
	
	@Column(unique=false,nullable=true)
	private String sapFinancialTransaction = null;
		
	@Column(unique=false,nullable=true)
	private String counterpartyOrderId = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private FSecurity security = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private Counterparty counterparty = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Trader trader = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private FUser user = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private AbstractBusinessComponent sourceComponent = null;
	
	@Column(unique=false,nullable=true)
	private String executingTrader = null;
		
	@Column(unique=false,nullable=false)
	private double orderQuantity = 0;
	
	@Column(unique=false,nullable=true)
	private Long settlementDate = null;	
	
	@Column(unique=false,nullable=true)
	private String market = null;	
		
	@Column(unique=false,nullable=false)
	private double cumulativeQuantity = 0;
	
	@Column(unique=false,nullable=false)
	private double leaveQuantity = 0;
		
	@Column(unique=false,nullable=true)
	private Double lastQuantity = null;
	
	@Column(unique=false,nullable=true)
	private Double lastPrice = null;
	
	@Column(unique=false,nullable=true)
	private Double lastYield = null;
	
	@Column(unique=false,nullable=false)
	private Side side = Side.BUY;
	
	@Column(unique=false,nullable=false)
	private long createdTimestamp = 0;
	
	@Column(unique=false,nullable=false)
	private long updatedTimestamp = 0;		
		
	@Column(unique=false,nullable=false)
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
	 * Gets the sap trade capture.
	 *
	 * @return the sap trade capture
	 */
	public SAPTradeCapture getSapTradeCapture() {
	
		return sapTradeCapture;
	}




	
	/**
	 * Sets the sap trade capture.
	 *
	 * @param sapTradeCapture the new sap trade capture
	 */
	public void setSapTradeCapture(SAPTradeCapture sapTradeCapture) {
	
		this.sapTradeCapture = sapTradeCapture;
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
	 * Gets the exec id.
	 *
	 * @return the exec id
	 */
	public String getExecId() {
	
		return execId;
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
	 * Gets the user.
	 *
	 * @return the user
	 */
	public FUser getUser() {
	
		return user;
	}



	
	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(FUser user) {
	
		this.user = user;
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
	 * Gets the executing trader.
	 *
	 * @return the executing trader
	 */
	public String getExecutingTrader() {
	
		return executingTrader;
	}



	
	/**
	 * Sets the executing trader.
	 *
	 * @param executingTrader the new executing trader
	 */
	public void setExecutingTrader(String executingTrader) {
	
		this.executingTrader = executingTrader;
	}



	/**
	 * Gets the created timestamp.
	 *
	 * @return the created timestamp
	 */
	public long getCreatedTimestamp() {
	
		return createdTimestamp;
	}


	
	/**
	 * Sets the created timestamp.
	 *
	 * @param createdTimestamp the new created timestamp
	 */
	public void setCreatedTimestamp(long createdTimestamp) {
	
		this.createdTimestamp = createdTimestamp;
	}


	
	/**
	 * Gets the updated timestamp.
	 *
	 * @return the updated timestamp
	 */
	public long getUpdatedTimestamp() {
	
		return updatedTimestamp;
	}


	
	/**
	 * Sets the updated timestamp.
	 *
	 * @param updatedTimestamp the new updated timestamp
	 */
	public void setUpdatedTimestamp(long updatedTimestamp) {
	
		this.updatedTimestamp = updatedTimestamp;
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
	 * Gets the security.
	 *
	 * @return the security
	 */
	public FSecurity getSecurity() {
	
		return security;
	}

	
	/**
	 * Sets the security.
	 *
	 * @param security the new security
	 */
	public void setSecurity(FSecurity security) {
	
		this.security = security;
	}

	
	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public Counterparty getCounterparty() {
	
		return counterparty;
	}

	
	/**
	 * Sets the counterparty.
	 *
	 * @param counterparty the new counterparty
	 */
	public void setCounterparty(Counterparty counterparty) {
	
		this.counterparty = counterparty;
	}

	
	/**
	 * Gets the trader.
	 *
	 * @return the trader
	 */
	public Trader getTrader() {
	
		return trader;
	}

	
	/**
	 * Sets the trader.
	 *
	 * @param trader the new trader
	 */
	public void setTrader(Trader trader) {
	
		this.trader = trader;
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



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {

		return 0;
	}



	
	
	/**
	 * Gets the sAP trade capture.
	 *
	 * @return the sAP trade capture
	 */
	public SAPTradeCapture getSAPTradeCapture() {
	
		return sapTradeCapture;
	}



	
	/**
	 * Sets the sAP trade capture.
	 *
	 * @param sapTradeCapture the new sAP trade capture
	 */
	public void setSAPTradeCapture(SAPTradeCapture sapTradeCapture) {
	
		this.sapTradeCapture = sapTradeCapture;
	}



	/**
	 * Gets the source component.
	 *
	 * @return the source component
	 */
	public AbstractBusinessComponent getSourceComponent() {
	
		return sourceComponent;
	}



	
	/**
	 * Sets the source component.
	 *
	 * @param sourceComponent the new source component
	 */
	public void setSourceComponent(AbstractBusinessComponent sourceComponent) {
	
		this.sourceComponent = sourceComponent;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#makeEager()
	 */
	@Override
	public void makeEager() {

		if(sapTradeCapture!=null)
			sapTradeCapture.getName();
		
		if(trader!=null)
			trader.getName();
		
		if(counterparty!=null)
			counterparty.getName();
		
		if(security!=null)
			security.getName();
		
		if(sourceComponent!=null)
			sourceComponent.getName();
		
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getWriteRoles()
	 */
	@Override
	public Set<FRole> getWriteRoles() {

		return new HashSet<FRole>();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getReadRoles()
	 */
	@Override
	public Set<FRole> getReadRoles() {

		return new HashSet<FRole>();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getExecuteRoles()
	 */
	@Override
	public Set<FRole> getExecuteRoles() {

		return new HashSet<FRole>();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	@Override
	public void setTransientValues(TransientValueSetter transientValueSetter) {
		
	}	
		
}
