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
package net.sourceforge.fixagora.sellside.shared.communication;

/**
 * The Class SellSideNewOrderSingleEntry.
 */
public class SellSideNewOrderSingleEntry extends AbstractSellSideEntry {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum OrderStatus.
	 */
	public enum OrderStatus {
		
		/** The new. */
		NEW, 
 /** The modified. */
 MODIFIED, 
 /** The cancel. */
 CANCEL, 
 /** The rejected. */
 REJECTED, 
 /** The rejected pending. */
 REJECTED_PENDING, 
 /** The filled. */
 FILLED, 
 /** The filled pending. */
 FILLED_PENDING, 
 /** The partially filled. */
 PARTIALLY_FILLED, 
 /** The partially filled pending. */
 PARTIALLY_FILLED_PENDING, 
 /** The done for day. */
 DONE_FOR_DAY, 
 /** The done for day pending. */
 DONE_FOR_DAY_PENDING
	};

	/**
	 * The Enum TimeInForce.
	 */
	public enum TimeInForce {
		
		/** The fill or kill. */
		FILL_OR_KILL, 
 /** The good till day. */
 GOOD_TILL_DAY, 
 /** The good till cancel. */
 GOOD_TILL_CANCEL, 
 /** The good till date. */
 GOOD_TILL_DATE
	};

	private String newOrderId = null;

	private String originalOrderId = null;

	private OrderStatus orderStatus = null;

	private Double newLimit = null;

	private TimeInForce timeInForce = TimeInForce.FILL_OR_KILL;

	private Double newOrderQuantity = null;

	private long tifDate = 0;

	/**
	 * Gets the new limit.
	 *
	 * @return the new limit
	 */
	public Double getNewLimit() {

		return newLimit;
	}

	/**
	 * Gets the original order id.
	 *
	 * @return the original order id
	 */
	public String getOriginalOrderId() {

		return originalOrderId;
	}

	/**
	 * Sets the original order id.
	 *
	 * @param originalOrderId the new original order id
	 */
	public void setOriginalOrderId(String originalOrderId) {

		this.originalOrderId = originalOrderId;
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
	 * Gets the new order quantity.
	 *
	 * @return the new order quantity
	 */
	public Double getNewOrderQuantity() {

		return newOrderQuantity;
	}

	/**
	 * Sets the new order quantity.
	 *
	 * @param newOrderQuantity the new new order quantity
	 */
	public void setNewOrderQuantity(Double newOrderQuantity) {

		this.newOrderQuantity = newOrderQuantity;
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
	 * Gets the tif date.
	 *
	 * @return the tif date
	 */
	public long getTifDate() {

		return tifDate;
	}

	/**
	 * Sets the tif date.
	 *
	 * @param tifDate the new tif date
	 */
	public void setTifDate(long tifDate) {

		this.tifDate = tifDate;
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


}
