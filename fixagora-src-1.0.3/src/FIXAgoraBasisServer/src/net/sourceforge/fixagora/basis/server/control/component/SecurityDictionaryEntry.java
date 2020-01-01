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
package net.sourceforge.fixagora.basis.server.control.component;


/**
 * The Class SecurityDictionaryEntry.
 */
public class SecurityDictionaryEntry {

	private String securityID = null;
	
	private String securityIDSource = null;

	/**
	 * Instantiates a new security dictionary entry.
	 *
	 * @param securityID the security id
	 * @param securityIDSource the security id source
	 */
	public SecurityDictionaryEntry(String securityID, String securityIDSource) {

		super();
		this.securityID = securityID;
		this.securityIDSource = securityIDSource;
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
	 * Gets the security id source.
	 *
	 * @return the security id source
	 */
	public String getSecurityIDSource() {
	
		return securityIDSource;
	}
	

	
}
