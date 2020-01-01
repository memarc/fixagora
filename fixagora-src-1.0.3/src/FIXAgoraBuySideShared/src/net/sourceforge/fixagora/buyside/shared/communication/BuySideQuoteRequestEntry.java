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


/**
 * The Class BuySideQuoteRequestEntry.
 */
public class BuySideQuoteRequestEntry extends AbstractBuySideEntry {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum OrderStatus.
	 */
	public enum OrderStatus {
		
		/** The new pending. */
		NEW_PENDING, 
 /** The new. */
 NEW, 
 /** The pass all pending. */
 PASS_ALL_PENDING, 
 /** The pass pending. */
 PASS_PENDING, 
 /** The pass. */
 PASS, 
 /** The rejected. */
 REJECTED, 
 /** The quoted. */
 QUOTED, 
 /** The counter pending. */
 COUNTER_PENDING, 
 /** The counter. */
 COUNTER, 
 /** The hit lift pending. */
 HIT_LIFT_PENDING, 
 /** The hit lift. */
 HIT_LIFT, 
 /** The filled. */
 FILLED, 
 /** The expired pending. */
 EXPIRED_PENDING, 
 /** The expired. */
 EXPIRED, 
 /** The cover pending. */
 COVER_PENDING, 
 /** The cover. */
 COVER, 
 /** The done away pending. */
 DONE_AWAY_PENDING,
/** The done away. */
DONE_AWAY
	};

	private String multiId = null;

	private String quoteReqId = null;
	
	private String quoteRespId = null;

	private String quoteId = null;

	private OrderStatus orderStatus = null;

	private long expireDate = 0;
	
	private long settlementDate = 0;
	
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
	 * Gets the multi id.
	 *
	 * @return the multi id
	 */
	public String getMultiId() {
	
		return multiId;
	}

	
	/**
	 * Sets the multi id.
	 *
	 * @param multiId the new multi id
	 */
	public void setMultiId(String multiId) {
	
		this.multiId = multiId;
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
	 * Gets the settlement date.
	 *
	 * @return the settlement date
	 */
	public long getSettlementDate() {
	
		return settlementDate;
	}

	
	/**
	 * Sets the settlement date.
	 *
	 * @param settlementDate the new settlement date
	 */
	public void setSettlementDate(long settlementDate) {
	
		this.settlementDate = settlementDate;
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
		
		if(orderStatus==null)
			return false;
		
		switch(orderStatus)
		{
			
			case COUNTER:
			case PASS_PENDING:
			case DONE_AWAY_PENDING:
			case COVER_PENDING:
			case COUNTER_PENDING:
			case HIT_LIFT_PENDING:
			case NEW:			
			case NEW_PENDING:
			case QUOTED:			
				return false;
				
			default:
				return true;
		}
	}
	
}
