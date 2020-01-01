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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class SecurityEvent.
 */
@Entity
public class SecurityEvent implements Serializable {

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
	private String eventText = null;
	
	@Column(unique = false, nullable = true)
	private Double eventPrice = null;
	
	@Column(unique = false, nullable = true)
	private Date eventDate = null;
	
	@Column(unique = false, nullable = true)
	private Date eventTime = null;


	
	
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
	 * Gets the event text.
	 *
	 * @return the event text
	 */
	public String getEventText() {
	
		return eventText;
	}


	
	/**
	 * Sets the event text.
	 *
	 * @param eventText the new event text
	 */
	public void setEventText(String eventText) {
	
		this.eventText = eventText;
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
	 * Gets the event date.
	 *
	 * @return the event date
	 */
	public Date getEventDate() {
	
		return eventDate;
	}


	
	/**
	 * Sets the event date.
	 *
	 * @param eventDate the new event date
	 */
	public void setEventDate(Date eventDate) {
	
		this.eventDate = eventDate;
	}


	
	/**
	 * Gets the event time.
	 *
	 * @return the event time
	 */
	public Date getEventTime() {
	
		return eventTime;
	}


	
	/**
	 * Sets the event time.
	 *
	 * @param eventTime the new event time
	 */
	public void setEventTime(Date eventTime) {
	
		this.eventTime = eventTime;
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

	
	
}
