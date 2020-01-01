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
package net.sourceforge.fixagora.excel.shared.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;

/**
 * The Class ExcelTradeCapture.
 */
@Entity
public class ExcelTradeCapture extends AbstractBusinessComponent  {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name="exceltradecapture_id", nullable=false)
	private List<AssignedUserSettings> assignedUserSettings;
	
	@Column(unique = false, nullable = false)
	private String securityIDSource = null;

	
	/**
	 * Gets the security id source.
	 *
	 * @return the security id source
	 */
	public String getSecurityIDSource() {
	
		return securityIDSource;
	}

	
	/**
	 * Sets the security id source.
	 *
	 * @param securityIDSource the new security id source
	 */
	public void setSecurityIDSource(String securityIDSource) {
	
		this.securityIDSource = securityIDSource;
	}

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
		return "/net/sourceforge/fixagora/excel/client/view/images/16x16/fileimport.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		return "/net/sourceforge/fixagora/excel/client/view/images/24x24/fileimport.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Excel \u00a9 Trade Export";
	}
	


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.excel.server.control.component.ExcelTradeCaptureComponentHandler";
	}
	
	/**
	 * Sets the assigned user settings.
	 *
	 * @param assignedUserSettings the new assigned user settings
	 */
	public void setAssignedUserSettings(List<AssignedUserSettings> assignedUserSettings) {
		
		this.assignedUserSettings = assignedUserSettings;
	}

	/**
	 * Gets the assigned user settings.
	 *
	 * @return the assigned user settings
	 */
	public List<AssignedUserSettings> getAssignedUserSettings() {
	
		if(assignedUserSettings==null)
			assignedUserSettings = new ArrayList<AssignedUserSettings>();
		return assignedUserSettings;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();
						
		for (AssignedUserSettings assignedUserSetting : getAssignedUserSettings())
			assignedUserSetting.makeEager();
		
	}
	
}
