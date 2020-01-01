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

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The Class FRole.
 */
@Entity
public class FRole extends AbstractBusinessObject {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
		
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
	
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
	
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon()
	{
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/kuser.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/kuser.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Role";
	}
	
}
