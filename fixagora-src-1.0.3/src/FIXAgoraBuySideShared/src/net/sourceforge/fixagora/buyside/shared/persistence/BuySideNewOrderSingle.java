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
package net.sourceforge.fixagora.buyside.shared.persistence;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter;

/**
 * The Class BuySideNewOrderSingle.
 */
@Entity
public class BuySideNewOrderSingle  implements PersistenceInterface {
	
	/**
	 * The Enum OrderStatus.
	 */
	public enum OrderStatus{/** The new pending. */
NEW_PENDING,/** The new. */
NEW,/** The cancel pending. */
CANCEL_PENDING, /** The cancel. */
 CANCEL,/** The rejected. */
REJECTED, /** The replace pending. */
 REPLACE_PENDING, /** The filled. */
 FILLED, /** The partially filled. */
 PARTIALLY_FILLED, /** The done for day. */
 DONE_FOR_DAY};
	
	/**
	 * The Enum TimeInForce.
	 */
	public enum TimeInForce{/** The fill or kill. */
FILL_OR_KILL,/** The good till day. */
GOOD_TILL_DAY,/** The good till cancel. */
GOOD_TILL_CANCEL,/** The good till date. */
GOOD_TILL_DATE};
	
	/**
	 * The Enum Side.
	 */
	public enum Side{/** The bid. */
BID,/** The ask. */
ASK};	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;	
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private BuySideBook buySideBook = null;

	@Column(unique=true,nullable=false)
	private String orderId = null;
	
	@Column(unique=true,nullable=true)
	private String newOrderId = null;
	
	@Column(unique=false,nullable=true)
	private String counterpartyOrderId = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private FSecurity security = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private Counterparty counterparty = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Trader trader = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private AbstractInitiator marketInterface = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	private FUser fUser = null;
	
	@Column(unique=false,nullable=false)
	private OrderStatus orderStatus = OrderStatus.NEW;
	
	@Column(unique=false,nullable=true)
	private Double limit = null;

	@Column(unique=false,nullable=false)
	private double orderQuantity = 0;
	
	@Column(unique=false,nullable=true)
	private Double newLimit = null;

	@Column(unique=false,nullable=true)
	private Double newQuantity = null;
	
	@Column(unique=false,nullable=false)
	private long tifTimestamp = 0;
	
	@Column(unique=false,nullable=false)
	private TimeInForce timeInForce = TimeInForce.FILL_OR_KILL;	
		
	@Column(unique=false,nullable=false)
	private double cumulativeQuantity = 0;
	
	@Column(unique=false,nullable=false)
	private double leaveQuantity = 0;
	
	@Column(unique=false,nullable=false)
	private double minQuantity = 0;
	
	@Column(unique=false,nullable=true)
	private Double lastQuantity = null;
	
	@Column(unique=false,nullable=true)
	private Double lastPrice = null;
	
	@Column(unique=false,nullable=true)
	private Double lastYield = null;
	
	@Column(unique=false,nullable=false)
	private Side side = Side.BID;
	
	@Column(unique=false,nullable=false)
	private long createdTimestamp = 0;
	
	@Column(unique=false,nullable=false)
	private long updatedTimestamp = 0;
	
	
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
	 * Gets the leave quantity.
	 *
	 * @return the leave quantity
	 */
	public double getLeaveQuantity() {
	
		return leaveQuantity;
	}
	
	
	/**
	 * Gets the new limit.
	 *
	 * @return the new limit
	 */
	public Double getNewLimit() {
	
		return newLimit;
	}
	
	/**
	 * Sets the new limit.
	 *
	 * @param newLimit the new new limit
	 */
	public void setNewLimit(Double newLimit) {
	
		this.newLimit = newLimit;
	}



	
	/**
	 * Gets the new quantity.
	 *
	 * @return the new quantity
	 */
	public Double getNewQuantity() {
	
		return newQuantity;
	}



	
	/**
	 * Sets the new quantity.
	 *
	 * @param newQuantity the new new quantity
	 */
	public void setNewQuantity(Double newQuantity) {
	
		this.newQuantity = newQuantity;
	}



	/**
	 * Gets the min quantity.
	 *
	 * @return the min quantity
	 */
	public double getMinQuantity() {
	
		return minQuantity;
	}
	
	
	/**
	 * Gets the new order id.
	 *
	 * @return the new order id
	 */
	public String getNewOrderId() {
	
		return newOrderId;
	}


	
	/**
	 * Sets the new order id.
	 *
	 * @param newOrderId the new new order id
	 */
	public void setNewOrderId(String newOrderId) {
	
		this.newOrderId = newOrderId;
	}


	/**
	 * Sets the min quantity.
	 *
	 * @param minQuantity the new min quantity
	 */
	public void setMinQuantity(double minQuantity) {
	
		this.minQuantity = minQuantity;
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
	 * Gets the tif timestamp.
	 *
	 * @return the tif timestamp
	 */
	public long getTifTimestamp() {
	
		return tifTimestamp;
	}




	
	/**
	 * Sets the tif timestamp.
	 *
	 * @param tifTimestamp the new tif timestamp
	 */
	public void setTifTimestamp(long tifTimestamp) {
	
		this.tifTimestamp = tifTimestamp;
	}




	/**
	 * Gets the f user.
	 *
	 * @return the f user
	 */
	public FUser getfUser() {
	
		return fUser;
	}



	
	/**
	 * Sets the f user.
	 *
	 * @param fUser the new f user
	 */
	public void setfUser(FUser fUser) {
	
		this.fUser = fUser;
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
	 * Gets the buy side book.
	 *
	 * @return the buy side book
	 */
	public BuySideBook getBuySideBook() {
	
		return buySideBook;
	}

	
	/**
	 * Sets the buy side book.
	 *
	 * @param buySideBook the new buy side book
	 */
	public void setBuySideBook(BuySideBook buySideBook) {
	
		this.buySideBook = buySideBook;
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
	 * Gets the market interface.
	 *
	 * @return the market interface
	 */
	public AbstractInitiator getMarketInterface() {
	
		return marketInterface;
	}

	
	/**
	 * Sets the market interface.
	 *
	 * @param marketInterface the new market interface
	 */
	public void setMarketInterface(AbstractInitiator marketInterface) {
	
		this.marketInterface = marketInterface;
	}

	
	/**
	 * Gets the order status.
	 *
	 * @return the order status
	 */
	public OrderStatus getOrderStatus() {
	
		return orderStatus;
	}

	
	/**
	 * Sets the order status.
	 *
	 * @param orderStatus the new order status
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
	
		this.orderStatus = orderStatus;
	}

	
	/**
	 * Gets the limit.
	 *
	 * @return the limit
	 */
	public Double getLimit() {
	
		return limit;
	}

	
	/**
	 * Sets the limit.
	 *
	 * @param limit the new limit
	 */
	public void setLimit(Double limit) {
	
		this.limit = limit;
	}

	
	/**
	 * Gets the time in force.
	 *
	 * @return the time in force
	 */
	public TimeInForce getTimeInForce() {
	
		return timeInForce;
	}

	
	/**
	 * Sets the time in force.
	 *
	 * @param timeInForce the new time in force
	 */
	public void setTimeInForce(TimeInForce timeInForce) {
	
		this.timeInForce = timeInForce;
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



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#makeEager()
	 */
	@Override
	public void makeEager() {

		if(buySideBook!=null)
			buySideBook.getName();
		
		if(trader!=null)
			trader.getName();
		
		if(security!=null)
			security.getName();
		
		if(marketInterface!=null)
			marketInterface.getName();
		
		if(fUser!=null)
			fUser.getName();
		
		if(counterparty!=null)
			counterparty.getName();
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
