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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;

/**
 * The Class SellSideBookMDInput.
 */
@Entity
public class SellSideBookMDInput implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "sellsidebook_id", insertable = false, updatable = false, nullable = false)
	private SellSideBook sellSideBook = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private AbstractBusinessComponent businessComponent = null;


	
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
	 * Gets the business component.
	 *
	 * @return the business component
	 */
	public AbstractBusinessComponent getBusinessComponent() {
	
		return businessComponent;
	}




	
	/**
	 * Sets the business component.
	 *
	 * @param businessComponent the new business component
	 */
	public void setBusinessComponent(AbstractBusinessComponent businessComponent) {
	
		this.businessComponent = businessComponent;
	}




	/**
	 * Make eager.
	 */
	public void makeEager() {
		
		if(businessComponent!=null)
			businessComponent.getName();
		
	}

	
}
