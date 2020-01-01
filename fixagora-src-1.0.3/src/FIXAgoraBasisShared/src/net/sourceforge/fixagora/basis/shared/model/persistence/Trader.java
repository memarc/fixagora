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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class Trader.
 */
@Entity
public class Trader extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;

	@Column(unique = false, nullable = true)
	private String ftitle = null;

	@Column(unique = false, nullable = true)
	private String academicTitle = null;

	@Column(unique = false, nullable = true)
	private String ffunction = null;

	@Column(unique = false, nullable = true)
	private String department = null;

	@Column(unique = false, nullable = true)
	private String roomNumber = null;

	@Column(unique = false, nullable = true)
	private String floor = null;

	@Column(unique = false, nullable = true)
	private String building = null;

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

	@Column(unique = false, nullable = true)
	private String firstName = null;

	@Column(unique = false, nullable = true)
	private String lastName = null;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "trader_id", nullable = false)
	private List<TraderPartyID> traderPartyIDs;
	
	

	
	/**
	 * Gets the trader party i ds.
	 *
	 * @return the trader party i ds
	 */
	public List<TraderPartyID> getTraderPartyIDs() {
	
		if(traderPartyIDs==null)
			traderPartyIDs = new ArrayList<TraderPartyID>();
		return traderPartyIDs;
	}

	
	/**
	 * Sets the trader party i ds.
	 *
	 * @param traderPartyIDs the new trader party i ds
	 */
	public void setTraderPartyIDs(List<TraderPartyID> traderPartyIDs) {
	
		this.traderPartyIDs = traderPartyIDs;
	}

	/**
	 * Gets the ftitle.
	 *
	 * @return the ftitle
	 */
	public String getFtitle() {

		return ftitle;
	}

	/**
	 * Sets the ftitle.
	 *
	 * @param ftitle the new ftitle
	 */
	public void setFtitle(String ftitle) {

		this.ftitle = ftitle;
	}

	/**
	 * Gets the academic title.
	 *
	 * @return the academic title
	 */
	public String getAcademicTitle() {

		return academicTitle;
	}

	/**
	 * Sets the academic title.
	 *
	 * @param academicTitle the new academic title
	 */
	public void setAcademicTitle(String academicTitle) {

		this.academicTitle = academicTitle;
	}
	
	/**
	 * Gets the ffunction.
	 *
	 * @return the ffunction
	 */
	public String getFfunction() {
	
		return ffunction;
	}

	
	/**
	 * Sets the ffunction.
	 *
	 * @param ffunction the new ffunction
	 */
	public void setFfunction(String ffunction) {
	
		this.ffunction = ffunction;
	}

	/**
	 * Gets the department.
	 *
	 * @return the department
	 */
	public String getDepartment() {

		return department;
	}

	/**
	 * Sets the department.
	 *
	 * @param department the new department
	 */
	public void setDepartment(String department) {

		this.department = department;
	}

	/**
	 * Gets the room number.
	 *
	 * @return the room number
	 */
	public String getRoomNumber() {

		return roomNumber;
	}

	/**
	 * Sets the room number.
	 *
	 * @param roomNumber the new room number
	 */
	public void setRoomNumber(String roomNumber) {

		this.roomNumber = roomNumber;
	}

	/**
	 * Gets the floor.
	 *
	 * @return the floor
	 */
	public String getFloor() {

		return floor;
	}

	/**
	 * Sets the floor.
	 *
	 * @param floor the new floor
	 */
	public void setFloor(String floor) {

		this.floor = floor;
	}

	/**
	 * Gets the building.
	 *
	 * @return the building
	 */
	public String getBuilding() {

		return building;
	}

	/**
	 * Sets the building.
	 *
	 * @param building the new building
	 */
	public void setBuilding(String building) {

		this.building = building;
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

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {

		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {

		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {

		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {

		this.lastName = lastName;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/edit_user.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/personal.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeTextColor()
	 */
	public Color getAdditionalTreeTextColor() {
		
		return new Color(204,0,0);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	public String getAdditionalTreeText() {
		
		if(getModificationUser()==null)
			return "New";

		return null;
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {
		for(TraderPartyID traderPartyID: getTraderPartyIDs())
			if(traderPartyID.getAbstractBusinessComponent()!=null)
				traderPartyID.getAbstractBusinessComponent().getName();
		super.makeEager();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Trader";
	}

}
