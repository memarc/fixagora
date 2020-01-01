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
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * The Class AbstractBusinessObject.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractBusinessObject implements Serializable, PersistenceInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;

	@Column(unique = true, nullable = false)
	private String name = null;
	
	

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private AbstractBusinessObject parent = null;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "read_roles")
	private Set<FRole> readRoles = new HashSet<FRole>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "write_roles")
	private Set<FRole> writeRoles = new HashSet<FRole>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "execute_roles")
	private Set<FRole> executeRoles = new HashSet<FRole>();

	@Column(nullable = false, unique = false)
	private Date modificationDate = null;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private FUser modificationUser = null;

	/**
	 * Gets the additional tree text color.
	 *
	 * @return the additional tree text color
	 */
	public Color getAdditionalTreeTextColor() {

		return new Color(178, 170, 142);
	}

	/**
	 * Gets the additional tree text.
	 *
	 * @return the additional tree text
	 */
	public String getAdditionalTreeText() {

		return null;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public AbstractBusinessObject getParent() {

		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(AbstractBusinessObject parent) {

		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getReadRoles()
	 */
	public Set<FRole> getReadRoles() {

		return readRoles;
	}

	/**
	 * Sets the read roles.
	 *
	 * @param readRoles the new read roles
	 */
	public void setReadRoles(Set<FRole> readRoles) {

		this.readRoles = readRoles;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getWriteRoles()
	 */
	public Set<FRole> getWriteRoles() {

		return writeRoles;
	}

	/**
	 * Sets the write roles.
	 *
	 * @param writeRoles the new write roles
	 */
	public void setWriteRoles(Set<FRole> writeRoles) {

		this.writeRoles = writeRoles;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#getExecuteRoles()
	 */
	public Set<FRole> getExecuteRoles() {

		return executeRoles;
	}

	/**
	 * Sets the execute roles.
	 *
	 * @param executeRoles the new execute roles
	 */
	public void setExecuteRoles(Set<FRole> executeRoles) {

		this.executeRoles = executeRoles;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public double getPosition() {

		return Double.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return name;
	}

	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	public String getIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/folder.png";
	}

	/**
	 * Gets the large icon.
	 *
	 * @return the large icon
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/folder.png";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object object) {

		if (object instanceof AbstractBusinessObject) {
			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) object;
			if (getPosition() == abstractBusinessObject.getPosition())
				return getName().compareToIgnoreCase(abstractBusinessObject.getName());
			if (getPosition() < abstractBusinessObject.getPosition())
				return -1;
		}
		
		return 1;
		
	}
	
	

	/**
	 * Gets the modification date.
	 *
	 * @return the modification date
	 */
	public Date getModificationDate() {

		return modificationDate;
	}

	/**
	 * Sets the modification date.
	 *
	 * @param modificationDate the new modification date
	 */
	public void setModificationDate(Date modificationDate) {

		this.modificationDate = modificationDate;
	}

	/**
	 * Gets the modification user.
	 *
	 * @return the modification user
	 */
	public FUser getModificationUser() {

		return modificationUser;
	}

	/**
	 * Sets the modification user.
	 *
	 * @param modificationUser the new modification user
	 */
	public void setModificationUser(FUser modificationUser) {

		this.modificationUser = modificationUser;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#makeEager()
	 */
	public void makeEager() {

		if (modificationUser != null)
			modificationUser.getName();
		if (parent != null)
			parent.getId();
		readRoles.size();
		writeRoles.size();
		executeRoles.size();

	}

	/**
	 * Gets the business object name.
	 *
	 * @return the business object name
	 */
	public abstract String getBusinessObjectName();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(this.id);
		return buffer.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof AbstractBusinessObject) {
			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) obj;
			return abstractBusinessObject.getId() == id;

		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.PersistenceInterface#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	public void setTransientValues(TransientValueSetter transientValueSetter) {

	}

	/**
	 * Checks if is affected by.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @return true, if is affected by
	 */
	public boolean isAffectedBy(AbstractBusinessObject abstractBusinessObject) {

		return false;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	public boolean isEditable() {

		return true;
	}

	/**
	 * Checks if is movable.
	 *
	 * @return true, if is movable
	 */
	public boolean isMovable() {

		return true;
	}

}
