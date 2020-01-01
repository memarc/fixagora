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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class ComplexEventDate.
 */
@Entity
public class ComplexEventDate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="securitycomplexevent_id", insertable=false, updatable=false, nullable=false)
    private SecurityComplexEvent securityComplexEvent;
	
	@Column(unique = false, nullable = false)
	private Date eventStartDate = null;
	
	@Column(unique = false, nullable = true)
	private Date eventEndDate = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
	@OrderBy(value="eventStartTime asc")
    @JoinColumn(name="complexeventdate_id", nullable=false)
	private List<ComplexEventTime> complexEventTimes;	
		
	
	
	
	
	/**
	 * Gets the security complex event.
	 *
	 * @return the security complex event
	 */
	public SecurityComplexEvent getSecurityComplexEvent() {
	
		return securityComplexEvent;
	}

	
	/**
	 * Sets the security complex event.
	 *
	 * @param securityComplexEvent the new security complex event
	 */
	public void setSecurityComplexEvent(SecurityComplexEvent securityComplexEvent) {
	
		this.securityComplexEvent = securityComplexEvent;
	}

	/**
	 * Gets the complex event times.
	 *
	 * @return the complex event times
	 */
	public List<ComplexEventTime> getComplexEventTimes() {
	
		if(complexEventTimes == null)
			complexEventTimes = new ArrayList<ComplexEventTime>();
		return complexEventTimes;
	}
	
	/**
	 * Sets the complex event times.
	 *
	 * @param complexEventTimes the new complex event times
	 */
	public void setComplexEventTimes(List<ComplexEventTime> complexEventTimes) {
	
		this.complexEventTimes = complexEventTimes;
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
	 * Gets the event start date.
	 *
	 * @return the event start date
	 */
	public Date getEventStartDate() {
	
		return eventStartDate;
	}

	
	/**
	 * Sets the event start date.
	 *
	 * @param eventStartDate the new event start date
	 */
	public void setEventStartDate(Date eventStartDate) {
	
		this.eventStartDate = eventStartDate;
	}

	
	/**
	 * Gets the event end date.
	 *
	 * @return the event end date
	 */
	public Date getEventEndDate() {
	
		return eventEndDate;
	}

	
	/**
	 * Sets the event end date.
	 *
	 * @param eventEndDate the new event end date
	 */
	public void setEventEndDate(Date eventEndDate) {
	
		this.eventEndDate = eventEndDate;
	}

	/**
	 * Make eager.
	 */
	public void makeEager() {

		if(complexEventTimes!=null)
			complexEventTimes.size();
		
	}
	
	
	
}
