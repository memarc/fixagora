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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * The Class FSecurityGroup.
 */
@Entity
public class FSecurityGroup extends AbstractBusinessObject implements BusinessObjectGroup {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum SortBy.
	 */
	public enum SortBy{/** The symbol. */
SYMBOL,/** The maturity. */
MATURITY}
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
	
	@Column(nullable = false, unique = false)
	private SortBy sortBy = SortBy.SYMBOL;

	@Transient
	private int childCount = 0;
		
	
	/**
	 * Gets the sort by.
	 *
	 * @return the sort by
	 */
	public SortBy getSortBy() {
	
		return sortBy;
	}

	
	/**
	 * Sets the sort by.
	 *
	 * @param sortBy the new sort by
	 */
	public void setSortBy(SortBy sortBy) {
	
		this.sortBy = sortBy;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getDescription()
	 */
	public String getDescription() {
	
		return description;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
	
		this.description = description;
	}

	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getPosition()
	 */
	public double getPosition()
	{
		if(getName()!=null&&getName().equals("Imported Securities"))
			return 11d;
		return 10d;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getChildClass()
	 */
	public Class<? extends AbstractBusinessObject> getChildClass()
	{
		return FSecurity.class;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#isSubfolderAllowed()
	 */
	@Override
	public boolean isSubfolderAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Security Group";
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeTextColor()
	 */
	@Override
	public Color getAdditionalTreeTextColor() {

		if(childCount>0)
			return new Color(204,0,0);
		return super.getAdditionalTreeTextColor();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	@Override
	public String getAdditionalTreeText() {

		if(childCount>0)
			return childCount + " New";
		return super.getAdditionalTreeText();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	@Override
	public void setTransientValues(TransientValueSetter transientValueSetter) {

		if(getName()!=null&&getName().equals("Imported Securities"))
			childCount = transientValueSetter.getChildCount(this);

		super.setTransientValues(transientValueSetter);
	}
	
	

}
