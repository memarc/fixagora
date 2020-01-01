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
package net.sourceforge.fixagora.excel.shared.communication;

import java.util.List;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;


/**
 * The Class SecurityListResponse.
 */
public class SecurityListResponse extends AbstractExcelResponse {

	private static final long serialVersionUID = 1L;
	
	private List<FSecurity> securities = null;
	
	private FSecurityGroup securityGroup = null;
	
	/**
	 * Instantiates a new security list response.
	 *
	 * @param securityListRequest the security list request
	 * @param securities the securities
	 * @param securityGroup the security group
	 */
	public SecurityListResponse(SecurityListRequest securityListRequest, List<FSecurity> securities, FSecurityGroup securityGroup) {

		super(securityListRequest);
		this.securities = securities;
		this.securityGroup = securityGroup;
	}	


	
	/**
	 * Gets the securities.
	 *
	 * @return the securities
	 */
	public List<FSecurity> getSecurities() {
	
		return securities;
	}

	
	
	/**
	 * Gets the security group.
	 *
	 * @return the security group
	 */
	public FSecurityGroup getSecurityGroup() {
	
		return securityGroup;
	}



	/**
	 * Sets the securities.
	 *
	 * @param securities the new securities
	 */
	public void setSecurities(List<FSecurity> securities) {
	
		this.securities = securities;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.AbstractExcelResponse#handleAbstractExcelResponse(net.sourceforge.fixagora.excel.shared.communication.ExcelResponses)
	 */
	@Override
	public void handleAbstractExcelResponse(ExcelResponses excelResponses) {

		// TODO Auto-generated method stub
		
	}


}
