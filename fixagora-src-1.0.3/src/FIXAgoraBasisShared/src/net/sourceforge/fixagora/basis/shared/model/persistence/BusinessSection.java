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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * The Class BusinessSection.
 */
@Entity
public class BusinessSection extends AbstractBusinessObject implements BusinessObjectGroup {

	private static final long serialVersionUID = 1L;	
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
	
	@Column(nullable = false, unique = false)
	private Double position = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=false)
	@JoinTable(name="BSCHILDREN")
	private List<AbstractBusinessObject> children= new ArrayList<AbstractBusinessObject>();
		
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
		if(position==null)
			position = 0d;
		return position;
	}
		
	
	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(Double position) {
	
		this.position = position;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<AbstractBusinessObject> getChildren() {
	
		return children;
	}

	
	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(List<AbstractBusinessObject> children) {
	
		this.children = children;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {
		
		return "Business Section";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		for(AbstractBusinessObject abstractBusinessObject: getChildren())
			abstractBusinessObject.makeEager();
		super.makeEager();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getChildClass()
	 */
	@Override
	public Class<? extends AbstractBusinessObject> getChildClass() {

		return AbstractBusinessObject.class;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#isSubfolderAllowed()
	 */
	@Override
	public boolean isSubfolderAllowed() {

		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	@Override
	public void setTransientValues(TransientValueSetter transientValueSetter) {

		for(AbstractBusinessObject abstractBusinessObject: getChildren())
			abstractBusinessObject.setTransientValues(transientValueSetter);
		super.setTransientValues(transientValueSetter);
	}
	


}
