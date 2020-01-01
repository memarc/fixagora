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
 * The Class SellSideQuoteRequestEntry.
 */
public class SellSideQuoteRequestEntry extends AbstractSellSideEntry {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum OrderStatus.
	 */
	public enum OrderStatus {
		
		/** The pass. */
		PASS, /** The rejected. */
 REJECTED, /** The rejected pending. */
 REJECTED_PENDING, /** The quoted. */
 QUOTED, /** The quoted pending. */
 QUOTED_PENDING, /** The counter. */
 COUNTER, /** The hit lift. */
 HIT_LIFT, /** The filled. */
 FILLED, /** The filled pending. */
 FILLED_PENDING, /** The expired. */
 EXPIRED, /** The cover. */
 COVER, /** The done away. */
 DONE_AWAY, /** The new. */
 NEW
	};


	private String quoteReqId = null;
	
	private String quoteRespId = null;

	private String quoteId = null;

	private OrderStatus orderStatus = null;

	private long expireDate = 0;
		
	private Long subjectDate = null;

	/**
	 * Gets the quote req id.
	 *
	 * @return the quote req id
	 */
	public String getQuoteReqId() {

		return quoteReqId;
	}
	
	
	/**
	 * Gets the quote resp id.
	 *
	 * @return the quote resp id
	 */
	public String getQuoteRespId() {
	
		return quoteRespId;
	}

	
	/**
	 * Sets the quote resp id.
	 *
	 * @param quoteRespId the new quote resp id
	 */
	public void setQuoteRespId(String quoteRespId) {
	
		this.quoteRespId = quoteRespId;
	}



	/**
	 * Sets the quote req id.
	 *
	 * @param quoteReqId the new quote req id
	 */
	public void setQuoteReqId(String quoteReqId) {

		this.quoteReqId = quoteReqId;
	}

	/**
	 * Gets the quote id.
	 *
	 * @return the quote id
	 */
	public String getQuoteId() {

		return quoteId;
	}

	/**
	 * Sets the quote id.
	 *
	 * @param quoteId the new quote id
	 */
	public void setQuoteId(String quoteId) {

		this.quoteId = quoteId;
	}

	
	/**
	 * Gets the expire date.
	 *
	 * @return the expire date
	 */
	public long getExpireDate() {
	
		return expireDate;
	}
	
	/**
	 * Sets the expire date.
	 *
	 * @param expireDate the new expire date
	 */
	public void setExpireDate(long expireDate) {
	
		this.expireDate = expireDate;
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
	 * Gets the subject date.
	 *
	 * @return the subject date
	 */
	public Long getSubjectDate() {
	
		return subjectDate;
	}


	
	/**
	 * Sets the subject date.
	 *
	 * @param subjectDate the new subject date
	 */
	public void setSubjectDate(Long subjectDate) {
	
		this.subjectDate = subjectDate;
	}

	/**
	 * Checks if is done.
	 *
	 * @return true, if is done
	 */
	public boolean isDone()
	{
				
		switch(orderStatus)
		{
			
			case COUNTER:
			case HIT_LIFT:
			case NEW:
			case QUOTED:
			case QUOTED_PENDING:			
				return false;
				
			default:
				return true;
		}
	}
	
}
