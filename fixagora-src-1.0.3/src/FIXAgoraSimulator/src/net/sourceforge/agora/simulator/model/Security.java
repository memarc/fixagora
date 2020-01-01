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
package net.sourceforge.agora.simulator.model;

import java.util.Date;

/**
 * The Class Security.
 */
public class Security  {

	private SecurityDetails securityDetails = new SecurityDetails();
	
	private String securityID = null;
	
	private Date maturity = null;
	
	private String name = null;
	
	
	
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
	 * Gets the additional tree text.
	 *
	 * @return the additional tree text
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


}
