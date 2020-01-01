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
 * The Class ComplexEventTime.
 */
@Entity
public class ComplexEventTime implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="complexeventdate_id", insertable=false, updatable=false, nullable=false)
    private ComplexEventDate complexEventDate;
	
	@Column(unique = false, nullable = false)
	private Date eventStartTime = null;
	
	@Column(unique = false, nullable = true)
	private Date eventEndTime = null;

	
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
	 * Gets the event start time.
	 *
	 * @return the event start time
	 */
	public Date getEventStartTime() {
	
		return eventStartTime;
	}


	
	/**
	 * Sets the event start time.
	 *
	 * @param eventStartTime the new event start time
	 */
	public void setEventStartTime(Date eventStartTime) {
	
		this.eventStartTime = eventStartTime;
	}


	
	/**
	 * Gets the event end time.
	 *
	 * @return the event end time
	 */
	public Date getEventEndTime() {
	
		return eventEndTime;
	}


	
	/**
	 * Sets the event end time.
	 *
	 * @param eventEndTime the new event end time
	 */
	public void setEventEndTime(Date eventEndTime) {
	
		this.eventEndTime = eventEndTime;
	}


	
	/**
	 * Gets the complex event date.
	 *
	 * @return the complex event date
	 */
	public ComplexEventDate getComplexEventDate() {
	
		return complexEventDate;
	}


	
	/**
	 * Sets the complex event date.
	 *
	 * @param complexEventDate the new complex event date
	 */
	public void setComplexEventDate(ComplexEventDate complexEventDate) {
	
		this.complexEventDate = complexEventDate;
	}


	
	
}
