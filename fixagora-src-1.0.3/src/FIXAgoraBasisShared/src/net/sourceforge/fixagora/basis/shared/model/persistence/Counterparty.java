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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class Counterparty.
 */
@Entity
public class Counterparty extends AbstractBusinessObject implements BusinessObjectGroup{

	private static final long serialVersionUID = 1L;

	@Column(unique = false, nullable = true)
	private String street = null;

	@Column(unique = false, nullable = true)
	private String house = null;

	@Column(unique = false, nullable = true)
	private String postalCode = null;

	@Column(unique = false, nullable = true)
	private String city = null;

	@Column(unique = false, nullable = true)
	private String country = null;
	
	@Column(unique = false, nullable = true)
	private String ftimezone = null;		
	
	@Column(unique = false, nullable = true)
	private String poBox = null;
	
	@Column(unique = false, nullable = true)
	private String poPostalCode = null;
	
	@Column(unique = false, nullable = true)
	private String companyPostalCode = null;	
	
	@Column(unique = false, nullable = true)
	private String fLanguage = null;

	@Column(unique = false, nullable = true)
	private String telephone = null;

	@Column(unique = false, nullable = true)
	private String mobilePhone = null;

	@Column(unique = false, nullable = true)
	private String fax = null;

	@Column(unique = false, nullable = true)
	private String email = null;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "counterparty_id", nullable = false)
	private List<CounterPartyPartyID> counterPartyPartyIDs;	
	
	
	/**
	 * Gets the counter party party i ds.
	 *
	 * @return the counter party party i ds
	 */
	public List<CounterPartyPartyID> getCounterPartyPartyIDs() {
	
		if(counterPartyPartyIDs==null)
			counterPartyPartyIDs = new ArrayList<CounterPartyPartyID>();
		return counterPartyPartyIDs;
	}


	
	/**
	 * Sets the counter party party i ds.
	 *
	 * @param counterPartyPartyIDs the new counter party party i ds
	 */
	public void setCounterPartyPartyIDs(List<CounterPartyPartyID> counterPartyPartyIDs) {
	
		this.counterPartyPartyIDs = counterPartyPartyIDs;
	}


	/**
	 * Gets the street.
	 *
	 * @return the street
	 */
	public String getStreet() {
	
		return street;
	}

	
	/**
	 * Sets the street.
	 *
	 * @param street the new street
	 */
	public void setStreet(String street) {
	
		this.street = street;
	}

	
	
	/**
	 * Gets the house.
	 *
	 * @return the house
	 */
	public String getHouse() {
	
		return house;
	}


	
	/**
	 * Sets the house.
	 *
	 * @param house the new house
	 */
	public void setHouse(String house) {
	
		this.house = house;
	}


	/**
	 * Gets the postal code.
	 *
	 * @return the postal code
	 */
	public String getPostalCode() {
	
		return postalCode;
	}

	
	/**
	 * Sets the postal code.
	 *
	 * @param postalCode the new postal code
	 */
	public void setPostalCode(String postalCode) {
	
		this.postalCode = postalCode;
	}

	
	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
	
		return city;
	}

	
	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
	
		this.city = city;
	}

	
	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
	
		return country;
	}

	
	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
	
		this.country = country;
	}

	
	/**
	 * Gets the ftimezone.
	 *
	 * @return the ftimezone
	 */
	public String getFtimezone() {
	
		return ftimezone;
	}

	
	/**
	 * Sets the ftimezone.
	 *
	 * @param ftimezone the new ftimezone
	 */
	public void setFtimezone(String ftimezone) {
	
		this.ftimezone = ftimezone;
	}

	
	/**
	 * Gets the po box.
	 *
	 * @return the po box
	 */
	public String getPoBox() {
	
		return poBox;
	}

	
	/**
	 * Sets the po box.
	 *
	 * @param poBox the new po box
	 */
	public void setPoBox(String poBox) {
	
		this.poBox = poBox;
	}

	
	/**
	 * Gets the po postal code.
	 *
	 * @return the po postal code
	 */
	public String getPoPostalCode() {
	
		return poPostalCode;
	}

	
	/**
	 * Sets the po postal code.
	 *
	 * @param poPostalCode the new po postal code
	 */
	public void setPoPostalCode(String poPostalCode) {
	
		this.poPostalCode = poPostalCode;
	}

	
	/**
	 * Gets the company postal code.
	 *
	 * @return the company postal code
	 */
	public String getCompanyPostalCode() {
	
		return companyPostalCode;
	}

	
	/**
	 * Sets the company postal code.
	 *
	 * @param companyPostalCode the new company postal code
	 */
	public void setCompanyPostalCode(String companyPostalCode) {
	
		this.companyPostalCode = companyPostalCode;
	}

	
	/**
	 * Gets the f language.
	 *
	 * @return the f language
	 */
	public String getfLanguage() {
	
		return fLanguage;
	}

	
	/**
	 * Sets the f language.
	 *
	 * @param fLanguage the new f language
	 */
	public void setfLanguage(String fLanguage) {
	
		this.fLanguage = fLanguage;
	}

	
	/**
	 * Gets the telephone.
	 *
	 * @return the telephone
	 */
	public String getTelephone() {
	
		return telephone;
	}

	
	/**
	 * Sets the telephone.
	 *
	 * @param telephone the new telephone
	 */
	public void setTelephone(String telephone) {
	
		this.telephone = telephone;
	}

	
	/**
	 * Gets the mobile phone.
	 *
	 * @return the mobile phone
	 */
	public String getMobilePhone() {
	
		return mobilePhone;
	}

	
	/**
	 * Sets the mobile phone.
	 *
	 * @param mobilePhone the new mobile phone
	 */
	public void setMobilePhone(String mobilePhone) {
	
		this.mobilePhone = mobilePhone;
	}

	
	/**
	 * Gets the fax.
	 *
	 * @return the fax
	 */
	public String getFax() {
	
		return fax;
	}

	
	/**
	 * Sets the fax.
	 *
	 * @param fax the new fax
	 */
	public void setFax(String fax) {
	
		this.fax = fax;
	}

	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
	
		return email;
	}

	
	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
	
		this.email = email;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Counterparty";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon()
	{
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/home.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/home.png";
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getChildClass()
	 */
	@Override
	public Class<? extends AbstractBusinessObject> getChildClass() {

		return Trader.class;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#isMovable()
	 */
	@Override
	public boolean isMovable() {

		return false;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#isSubfolderAllowed()
	 */
	@Override
	public boolean isSubfolderAllowed() {

		return false;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getDescription()
	 */
	@Override
	public String getDescription() {

		return null;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String text) {

	}




	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		for(CounterPartyPartyID counterPartyPartyID: getCounterPartyPartyIDs())
			if(counterPartyPartyID.getAbstractBusinessComponent()!=null)
				counterPartyPartyID.getAbstractBusinessComponent().getName();
		super.makeEager();
	}
	

	
}
