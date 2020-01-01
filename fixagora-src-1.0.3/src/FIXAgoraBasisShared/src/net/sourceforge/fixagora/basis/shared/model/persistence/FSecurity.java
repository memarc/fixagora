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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup.SortBy;

/**
 * The Class FSecurity.
 */
@Entity
public class FSecurity extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;

	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
	private SecurityDetails securityDetails = new SecurityDetails();
	
	@Column(unique = true, nullable = false)
	private String securityID = null;
	
	@Column(unique = false, nullable = true)
	private Date maturity = null;
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	public String getAdditionalTreeText() {
		
		return securityID;
		
	}
	
	
	/**
	 * Gets the maturity.
	 *
	 * @return the maturity
	 */
	public Date getMaturity() {
		
		return maturity;
	}


	
	/**
	 * Sets the maturity.
	 *
	 * @param maturity the new maturity
	 */
	public void setMaturity(Date maturity) {
	
		this.maturity = maturity;
	}


	/**
	 * Gets the security details.
	 *
	 * @return the security details
	 */
	public SecurityDetails getSecurityDetails() {

		return securityDetails;
	}

	/**
	 * Sets the security details.
	 *
	 * @param securityDetails the new security details
	 */
	public void setSecurityDetails(SecurityDetails securityDetails) {

		this.securityDetails = securityDetails;
	}
	
	/**
	 * Gets the security id.
	 *
	 * @return the security id
	 */
	public String getSecurityID() {
		
		return securityID;
	}
	
	/**
	 * Sets the security id.
	 *
	 * @param securityID the new security id
	 */
	public void setSecurityID(String securityID) {
	
		this.securityID = securityID;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#isAffectedBy(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject)
	 */
	@Override
	public boolean isAffectedBy(AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject == null)
			return false;

		if (securityDetails != null) {
			for (SecurityLeg securityLeg : securityDetails.getSecurityLegs())
				if (securityLeg.getLegSecurity() != null && securityLeg.getLegSecurity().getId() == abstractBusinessObject.getId())
					return true;

			for (SecurityUnderlying securityUnderlying : securityDetails.getSecurityUnderlyings())
				if (securityUnderlying.getUnderlyingSecurity() != null && securityUnderlying.getUnderlyingSecurity().getId() == abstractBusinessObject.getId())
					return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Security";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/security.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/security.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {
		super.makeEager();
		getSecurityDetails().makeEager();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object object) {

		if(!(getParent() instanceof FSecurityGroup) || !(object instanceof FSecurity))
			return super.compareTo(object);
		FSecurity security = (FSecurity) object;
		FSecurityGroup securityGroup = (FSecurityGroup)getParent();
		if(securityGroup.getSortBy()==SortBy.SYMBOL||(getMaturity()==null&&security.getMaturity()==null))
			return super.compareTo(object);
		if(getMaturity()==null&&security.getMaturity()!=null)
			return -1;
		if(getMaturity()!=null&&security.getMaturity()==null)
			return 1;
		return getMaturity().compareTo(security.getMaturity());
	}

}
