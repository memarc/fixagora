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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class AssignedInitiatorSecurity.
 */
@Entity
public class AssignedInitiatorSecurity implements Serializable, Comparable<AssignedInitiatorSecurity> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "fixinitiator_id", insertable = false, updatable = false, nullable = false)
	private AbstractInitiator fixInitiator = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private FSecurity security = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private Counterparty counterparty = null;
	
	
	
	
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
	 * Gets the fix initiator.
	 *
	 * @return the fix initiator
	 */
	public AbstractInitiator getFixInitiator() {
	
		return fixInitiator;
	}



	
	/**
	 * Sets the fix initiator.
	 *
	 * @param fixInitiator the new fix initiator
	 */
	public void setFixInitiator(AbstractInitiator fixInitiator) {
	
		this.fixInitiator = fixInitiator;
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
	 * Make eager.
	 */
	public void makeEager() {
		if(counterparty!=null)
			counterparty.getName();
		security.getName();
	}




	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AssignedInitiatorSecurity assignedInitiatorSecurity) {

		int security = getSecurity().getName().compareTo(assignedInitiatorSecurity.getSecurity().getName());
		if(security!=0)
			return security;
		if(assignedInitiatorSecurity.getCounterparty()!=null&&getCounterparty()==null)
			return -1;
		if(assignedInitiatorSecurity.getCounterparty()==null&&getCounterparty()==null)
			return 0;
		if(assignedInitiatorSecurity.getCounterparty()==null&&getCounterparty()!=null)
			return 1;
		return getCounterparty().getName().compareTo(assignedInitiatorSecurity.getCounterparty().getName());
	}

}
