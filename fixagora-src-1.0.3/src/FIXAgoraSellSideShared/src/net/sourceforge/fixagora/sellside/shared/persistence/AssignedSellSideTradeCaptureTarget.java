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
package net.sourceforge.fixagora.sellside.shared.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;

/**
 * The Class AssignedSellSideTradeCaptureTarget.
 */
@Entity
@Table(name="ASSTCT")
public class AssignedSellSideTradeCaptureTarget implements Serializable, Comparable<AssignedSellSideTradeCaptureTarget> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "sellsidebook_id", insertable = false, updatable = false, nullable = false)
	private SellSideBook sellSideBook = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private AbstractBusinessComponent abstractBusinessComponent = null;
	
	@Column(nullable=true, unique=false)
	private long lastTradeId = 0;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}
	
	
	/**
	 * Gets the sell side book.
	 *
	 * @return the sell side book
	 */
	public SellSideBook getSellSideBook() {
	
		return sellSideBook;
	}

	
	/**
	 * Sets the sell side book.
	 *
	 * @param sellSideBook the new sell side book
	 */
	public void setSellSideBook(SellSideBook sellSideBook) {
	
		this.sellSideBook = sellSideBook;
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
	 * Gets the last trade id.
	 *
	 * @return the last trade id
	 */
	public long getLastTradeId() {
	
		return lastTradeId;
	}


	
	/**
	 * Sets the last trade id.
	 *
	 * @param lastTradeId the new last trade id
	 */
	public void setLastTradeId(long lastTradeId) {
	
		this.lastTradeId = lastTradeId;
	}


	/**
	 * Gets the abstract business component.
	 *
	 * @return the abstract business component
	 */
	public AbstractBusinessComponent getAbstractBusinessComponent() {
	
		return abstractBusinessComponent;
	}


	
	/**
	 * Sets the abstract business component.
	 *
	 * @param abstractBusinessComponent the new abstract business component
	 */
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {
	
		this.abstractBusinessComponent = abstractBusinessComponent;
	}


	/**
	 * Make eager.
	 */
	public void makeEager() {

		abstractBusinessComponent.getName();
	}




	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AssignedSellSideTradeCaptureTarget assignedAcceptorSecurity) {

		return(getAbstractBusinessComponent().getName().compareTo(assignedAcceptorSecurity.getAbstractBusinessComponent().getName()));

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
		
		if(obj instanceof AssignedSellSideTradeCaptureTarget)
		{
			AssignedSellSideTradeCaptureTarget abstractBusinessObject = (AssignedSellSideTradeCaptureTarget)obj;
			return abstractBusinessObject.getId()==id;
			
		}
		return false;
	}	
	

}
