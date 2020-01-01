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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;

/**
 * The Class AssignedBuySideInitiator.
 */
@Entity
public class AssignedBuySideInitiator implements Serializable, Comparable<AssignedBuySideInitiator> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "buysidebook_id", insertable = false, updatable = false, nullable = false)
	private BuySideBook buySideBook = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private AbstractInitiator initiator = null;
		
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
	}

	


	
	/**
	 * Gets the initiator.
	 *
	 * @return the initiator
	 */
	public AbstractInitiator getInitiator() {
	
		return initiator;
	}


	
	/**
	 * Sets the initiator.
	 *
	 * @param initiator the new initiator
	 */
	public void setInitiator(AbstractInitiator initiator) {
	
		this.initiator = initiator;
	}


	/**
	 * Make eager.
	 */
	public void makeEager() {

		initiator.getName();
	}




	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AssignedBuySideInitiator assignedInitiatorSecurity) {

		return(getInitiator().getName().compareTo(assignedInitiatorSecurity.getInitiator().getName()));

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(this.id);
	    return buffer.toString().hashCode();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof AssignedBuySideInitiator)
		{
			AssignedBuySideInitiator abstractBusinessObject = (AssignedBuySideInitiator)obj;
			return abstractBusinessObject.getId()==id;
			
		}
		return false;
	}	
	

}
