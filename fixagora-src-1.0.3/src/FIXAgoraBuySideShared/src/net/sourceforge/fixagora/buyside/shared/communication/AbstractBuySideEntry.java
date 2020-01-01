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
package net.sourceforge.fixagora.buyside.shared.communication;

import java.io.Serializable;

/**
 * The Class AbstractBuySideEntry.
 */
public abstract class AbstractBuySideEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum Side.
	 */
	public enum Side {
		
		/** The bid. */
		BID, 
 /** The ask. */
 ASK
	};

	/** The side. */
	protected Side side = Side.BID;

	/** The security. */
	protected long security = 0;

	/** The counterparty. */
	protected long counterparty = 0;

	/** The market interface. */
	protected long marketInterface = 0;

	/** The order id. */
	protected String orderId = null;

	/** The buy side book. */
	protected long buySideBook = 0;

	/** The counterparty order id. */
	protected String counterpartyOrderId = null;

	/** The order quantity. */
	protected double orderQuantity = 0;

	/** The cumulative quantity. */
	protected double cumulativeQuantity = 0;

	/** The last quantity. */
	protected Double lastQuantity = null;

	/** The last price. */
	protected Double lastPrice = null;

	/** The last yield. */
	protected Double lastYield = null;

	/** The user. */
	protected String user = null;

	/** The updated. */
	protected long updated = 0;

	/** The created. */
	protected long created = 0;

	/** The leave quantity. */
	protected double leaveQuantity = 0;

	/** The min quantity. */
	protected double minQuantity = 0;

	/** The limit. */
	protected Double limit = null;

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
	 * Gets the min quantity.
	 *
	 * @return the min quantity
	 */
	public double getMinQuantity() {

		return minQuantity;
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
	public String getUser() {

		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {

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
	 * Gets the buy side book.
	 *
	 * @return the buy side book
	 */
	public long getBuySideBook() {

		return buySideBook;
	}

	/**
	 * Sets the buy side book.
	 *
	 * @param buySideBook the new buy side book
	 */
	public void setBuySideBook(long buySideBook) {

		this.buySideBook = buySideBook;
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
	 * Gets the market interface.
	 *
	 * @return the market interface
	 */
	public long getMarketInterface() {

		return marketInterface;
	}

	/**
	 * Sets the market interface.
	 *
	 * @param marketInterface the new market interface
	 */
	public void setMarketInterface(long marketInterface) {

		this.marketInterface = marketInterface;
	}

}
