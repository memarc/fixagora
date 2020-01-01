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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;

/**
 * The Class AssignedBuySideBookSecurity.
 */
@Entity
@Table(name="ABSBS")
public class AssignedBuySideBookSecurity implements Serializable, Comparable<AssignedBuySideBookSecurity> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="absbs_id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "buysidebook_id", insertable = false, updatable = false, nullable = false)
	private BuySideBook buySideBook = null;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="assignedBuySideBookSecurities")
	private Set<BuySideQuotePage> buySideQuotePages = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private FSecurity security = null;	

	@Column(unique = false, nullable = true)
	private Integer daycountConvention=null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private BankCalendar bankCalendar = null;
	
	@Column(nullable = true, unique = false)
	private Integer valuta = null;	
	
	@Column(nullable = true, unique = false)
	private CalcMethod calcMethod = null;
	
	@Column(nullable = true, unique = false)
	private Boolean fractionalDisplay = null;
	
	/**
	 * Gets the fractional display.
	 *
	 * @return the fractional display
	 */
	public Boolean getFractionalDisplay() {
	
		return fractionalDisplay;
	}

	
	/**
	 * Sets the fractional display.
	 *
	 * @param fractionalDisplay the new fractional display
	 */
	public void setFractionalDisplay(Boolean fractionalDisplay) {
	
		this.fractionalDisplay = fractionalDisplay;
	}

	/**
	 * Gets the calc method.
	 *
	 * @return the calc method
	 */
	public CalcMethod getCalcMethod() {

		return calcMethod;
	}

	/**
	 * Sets the calc method.
	 *
	 * @param calcMethod the new calc method
	 */
	public void setCalcMethod(CalcMethod calcMethod) {

		this.calcMethod = calcMethod;
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
	 * Gets the daycount convention.
	 *
	 * @return the daycount convention
	 */
	public Integer getDaycountConvention() {
	
		return daycountConvention;
	}




	
	/**
	 * Sets the daycount convention.
	 *
	 * @param daycountConvention the new daycount convention
	 */
	public void setDaycountConvention(Integer daycountConvention) {
	
		this.daycountConvention = daycountConvention;
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
	 * Gets the buy side quote pages.
	 *
	 * @return the buy side quote pages
	 */
	public Set<BuySideQuotePage> getBuySideQuotePages() {
	
		if(buySideQuotePages == null)
			buySideQuotePages=new HashSet<BuySideQuotePage>();
		return buySideQuotePages;
	}


	
	/**
	 * Sets the buy side quote pages.
	 *
	 * @param buySideQuotePages the new buy side quote pages
	 */
	public void setBuySideQuotePages(Set<BuySideQuotePage> buySideQuotePages) {
	
		this.buySideQuotePages = buySideQuotePages;
	}



	
	/**
	 * Gets the bank calendar.
	 *
	 * @return the bank calendar
	 */
	public BankCalendar getBankCalendar() {
	
		return bankCalendar;
	}


	
	/**
	 * Sets the bank calendar.
	 *
	 * @param bankCalendar the new bank calendar
	 */
	public void setBankCalendar(BankCalendar bankCalendar) {
	
		this.bankCalendar = bankCalendar;
	}


	
	/**
	 * Gets the valuta.
	 *
	 * @return the valuta
	 */
	public Integer getValuta() {
	
		return valuta;
	}


	
	/**
	 * Sets the valuta.
	 *
	 * @param valuta the new valuta
	 */
	public void setValuta(Integer valuta) {
	
		this.valuta = valuta;
	}


	/**
	 * Make eager.
	 */
	public void makeEager() {

		security.getName();
		
		getBuySideQuotePages().size();
		
		if(bankCalendar!=null)
			bankCalendar.getName();
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AssignedBuySideBookSecurity assignedInitiatorSecurity) {

		return(getSecurity().getName().compareTo(assignedInitiatorSecurity.getSecurity().getName()));

	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return security.getName();
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
		
		if(obj instanceof AssignedBuySideBookSecurity)
		{
			AssignedBuySideBookSecurity abstractBusinessObject = (AssignedBuySideBookSecurity)obj;
			return abstractBusinessObject.getId()==id;
			
		}
		return false;
	}	
	

}
