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
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 * The Class FUser.
 */
@Entity
public class FUser extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;

	@Column(unique = false, nullable = false)
	private String fpassword = null;

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

	@Column(unique = false, nullable = false)
	private int treePanelWidth = 0;

	@Column(unique = false, nullable = false)
	private int mainPanelPanelHeight = 0;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "assigned_roles")
	private Set<FRole> assignedRoles = new HashSet<FRole>();

	@ElementCollection
	@CollectionTable(name = "expandSet", joinColumns = @JoinColumn(name = "id"))
	@Column(nullable = false)
	private Set<Long> expandSet = new HashSet<Long>();

	@ElementCollection
	@CollectionTable(name = "openBusinessObjects", joinColumns = @JoinColumn(name = "id"))
	@Column(nullable = false)
	private Set<Long> openBusinessObjects = new HashSet<Long>();

	@ElementCollection
	@CollectionTable(name = "dockSettings", joinColumns = @JoinColumn(name = "id"))
	@Column(nullable = false)
	private List<String> dockSettings = new ArrayList<String>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "fuser_id", nullable = false)
	private List<FUserPartyID> fUserPartyIDs;

	@Transient
	private boolean loggedIn = false;

	/**
	 * Gets the fpassword.
	 *
	 * @return the fpassword
	 */
	public String getFpassword() {

		return fpassword;
	}

	/**
	 * Gets the f user party i ds.
	 *
	 * @return the f user party i ds
	 */
	public List<FUserPartyID> getfUserPartyIDs() {

		if (fUserPartyIDs == null)
			fUserPartyIDs = new ArrayList<FUserPartyID>();
		return fUserPartyIDs;
	}

	/**
	 * Sets the f user party i ds.
	 *
	 * @param fUserPartyIDs the new f user party i ds
	 */
	public void setfUserPartyIDs(List<FUserPartyID> fUserPartyIDs) {

		this.fUserPartyIDs = fUserPartyIDs;
	}

	/**
	 * Sets the fpassword.
	 *
	 * @param fpassword the new fpassword
	 */
	public void setFpassword(String fpassword) {

		this.fpassword = fpassword;
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
	 * Gets the dock settings.
	 *
	 * @return the dock settings
	 */
	public List<String> getDockSettings() {

		return dockSettings;
	}

	/**
	 * Sets the dock settings.
	 *
	 * @param dockSettings the new dock settings
	 */
	public void setDockSettings(List<String> dockSettings) {

		this.dockSettings = dockSettings;
	}

	/**
	 * Gets the xML dock settings.
	 *
	 * @return the xML dock settings
	 */
	public String getXMLDockSettings() {

		StringBuffer stringBuffer = new StringBuffer();
		for (String string : dockSettings)
			stringBuffer.append(string);
		return stringBuffer.toString();
	}

	/**
	 * Sets the xML dock settings.
	 *
	 * @param dockSettings the new xML dock settings
	 */
	public void setXMLDockSettings(String dockSettings) {

		this.dockSettings = new ArrayList<String>();
		int i = 0;
		while (dockSettings.length() - i > 100) {
			this.dockSettings.add(dockSettings.substring(i, i + 100));
			i = i + 100;
		}
		this.dockSettings.add(dockSettings.substring(i));

	}

	/**
	 * Gets the open business objects.
	 *
	 * @return the open business objects
	 */
	public Set<Long> getOpenBusinessObjects() {

		return openBusinessObjects;
	}

	/**
	 * Sets the open business objects.
	 *
	 * @param openBusinessObjects the new open business objects
	 */
	public void setOpenBusinessObjects(Set<Long> openBusinessObjects) {

		this.openBusinessObjects = openBusinessObjects;
	}

	/**
	 * Gets the expand set.
	 *
	 * @return the expand set
	 */
	public Set<Long> getExpandSet() {

		return expandSet;
	}

	/**
	 * Sets the expand set.
	 *
	 * @param expandSet the new expand set
	 */
	public void setExpandSet(Set<Long> expandSet) {

		this.expandSet = expandSet;
	}

	/**
	 * Gets the tree panel width.
	 *
	 * @return the tree panel width
	 */
	public int getTreePanelWidth() {

		return treePanelWidth;
	}

	/**
	 * Sets the tree panel width.
	 *
	 * @param treePanelWidth the new tree panel width
	 */
	public void setTreePanelWidth(int treePanelWidth) {

		this.treePanelWidth = treePanelWidth;
	}

	/**
	 * Gets the main panel panel height.
	 *
	 * @return the main panel panel height
	 */
	public int getMainPanelPanelHeight() {

		return mainPanelPanelHeight;
	}

	/**
	 * Sets the main panel panel height.
	 *
	 * @param mainPanelPanelHeight the new main panel panel height
	 */
	public void setMainPanelPanelHeight(int mainPanelPanelHeight) {

		this.mainPanelPanelHeight = mainPanelPanelHeight;
	}

	/**
	 * Gets the f password.
	 *
	 * @return the f password
	 */
	public String getFPassword() {

		return fpassword;
	}

	/**
	 * Sets the f password.
	 *
	 * @param password the new f password
	 */
	public void setFPassword(String password) {

		this.fpassword = password;
	}

	/**
	 * Sets the plain password.
	 *
	 * @param password the new plain password
	 * @throws Exception the exception
	 */
	public void setPlainPassword(String password) throws Exception {

		this.fpassword = getHash(password);
	}

	/**
	 * Gets the hash.
	 *
	 * @param password the password
	 * @return the hash
	 * @throws Exception the exception
	 */
	public String getHash(String password) throws Exception {

		Cipher c = Cipher.getInstance("DES");
		Key k = new SecretKeySpec("01234567".getBytes(), "DES");
		c.init(Cipher.ENCRYPT_MODE, k);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CipherOutputStream cos = new CipherOutputStream(out, c);
		cos.write(password.getBytes());
		cos.close();
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b : out.toByteArray()) {
			stringBuffer.append(Byte.toString(b));
		}
		out.close();
		return stringBuffer.toString();

	}

	/**
	 * Gets the assigned roles.
	 *
	 * @return the assigned roles
	 */
	public Set<FRole> getAssignedRoles() {

		return assignedRoles;
	}

	/**
	 * Sets the assigned roles.
	 *
	 * @param assignedRoles the new assigned roles
	 */
	public void setAssignedRoles(Set<FRole> assignedRoles) {

		this.assignedRoles = assignedRoles;
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

		if (loggedIn)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/edit_user.png";
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/grey_user.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeTextColor()
	 */
	public Color getAdditionalTreeTextColor() {

		if (loggedIn)
			return new Color(51, 153, 0);
		return new Color(204, 0, 0);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	public String getAdditionalTreeText() {

		if (loggedIn)
			return "Online";
		return "Offline";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/personal.png";
	}

	/**
	 * Checks if is logged in.
	 *
	 * @return true, if is logged in
	 */
	public boolean isLoggedIn() {

		return loggedIn;
	}

	/**
	 * Sets the logged in.
	 *
	 * @param loggedIn the new logged in
	 */
	public void setLoggedIn(boolean loggedIn) {

		this.loggedIn = loggedIn;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		assignedRoles.size();
		expandSet.size();
		openBusinessObjects.size();
		dockSettings.size();
		for (FUserPartyID fUserPartyID : getfUserPartyIDs())
			if (fUserPartyID.getAbstractBusinessComponent() != null)
				fUserPartyID.getAbstractBusinessComponent().getName();
		super.makeEager();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "User";
	}

	/**
	 * Can read.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @return true, if successful
	 */
	public boolean canRead(AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject == null)
			return false;

		for (FRole fRole : assignedRoles) {
			if (fRole.getName().equals("Admin Role"))
				return true;
			if (abstractBusinessObject.getReadRoles().contains(fRole))
				return true;
		}
		return false;
	}

	/**
	 * Can write.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @return true, if successful
	 */
	public boolean canWrite(AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject == null)
			return false;

		if (abstractBusinessObject.getName() != null && abstractBusinessObject.getName().equals("Admin Role"))
			return false;
		for (FRole fRole : assignedRoles) {
			if (fRole.getName().equals("Admin Role"))
				return true;
			if (abstractBusinessObject.getWriteRoles().contains(fRole))
				return true;
		}
		return false;
	}

	/**
	 * Can execute.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @return true, if successful
	 */
	public boolean canExecute(AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject == null)
			return false;

		for (FRole fRole : assignedRoles) {
			if (fRole.getName().equals("Admin Role"))
				return true;
			if (abstractBusinessObject.getExecuteRoles().contains(fRole))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	public void setTransientValues(TransientValueSetter transientValueSetter) {

		transientValueSetter.isLoggedIn(this);
	}

}
