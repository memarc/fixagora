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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The Class TraderPartyID.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"abstractbusinesscomponent_id", "partyid","partyidSource","partyrole"})})
public class TraderPartyID implements Serializable, Comparable<TraderPartyID> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "trader_id", insertable = false, updatable = false, nullable = false)
	private Trader trader = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private AbstractBusinessComponent abstractBusinessComponent = null;
	
	@Column(unique=false,nullable=false)
	private String partyID = null;
	
	@Column(unique=false,nullable=false)
	private String partyIDSource = null;
	
	@Column(unique=false,nullable=true)
	private Integer partyRole = null;		
	
	/**
	 * Gets the party id.
	 *
	 * @return the party id
	 */
	public String getPartyID() {
	
		return partyID;
	}

	
	/**
	 * Sets the party id.
	 *
	 * @param partyID the new party id
	 */
	public void setPartyID(String partyID) {
	
		this.partyID = partyID;
	}



	
	/**
	 * Gets the party id source.
	 *
	 * @return the party id source
	 */
	public String getPartyIDSource() {
	
		return partyIDSource;
	}



	
	/**
	 * Sets the party id source.
	 *
	 * @param partyIDSource the new party id source
	 */
	public void setPartyIDSource(String partyIDSource) {
	
		this.partyIDSource = partyIDSource;
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
	 * Gets the trader.
	 *
	 * @return the trader
	 */
	public Trader getTrader() {
	
		return trader;
	}


	
	/**
	 * Sets the trader.
	 *
	 * @param trader the new trader
	 */
	public void setTrader(Trader trader) {
	
		this.trader = trader;
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
	 * Gets the party role.
	 *
	 * @return the party role
	 */
	public Integer getPartyRole() {
	
		return partyRole;
	}


	
	/**
	 * Sets the party role.
	 *
	 * @param partyRole the new party role
	 */
	public void setPartyRole(Integer partyRole) {
	
		this.partyRole = partyRole;
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
	public int compareTo(TraderPartyID assignedInitiatorSecurity) {

		if(getAbstractBusinessComponent()==null)
			return -1;
		
		if(assignedInitiatorSecurity.getAbstractBusinessComponent()==null)
			return 1;
		
		return(getAbstractBusinessComponent().getName().compareTo(assignedInitiatorSecurity.getAbstractBusinessComponent().getName()));

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
		
		if(obj instanceof TraderPartyID)
		{
			TraderPartyID abstractBusinessObject = (TraderPartyID)obj;
			return abstractBusinessObject.getId()==id;
			
		}
		return false;
	}	
	

}
