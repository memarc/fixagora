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
package net.sourceforge.fixagora.excel.client.model;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;


/**
 * The Class UploadEntry.
 */
public class UploadEntry {

	private int row = -1;
	
	private FSecurity security = null;
	
	
	/**
	 * Instantiates a new upload entry.
	 *
	 * @param row the row
	 * @param security the security
	 */
	public UploadEntry(int row, FSecurity security) {

		super();
		this.row = row;
		this.security = security;
	}


	/**
	 * Gets the row.
	 *
	 * @return the row
	 */
	public int getRow() {
	
		return row;
	}

	
	/**
	 * Gets the security.
	 *
	 * @return the security
	 */
	public FSecurity getSecurity() {
	
		return security;
	}
	
	
	
}
