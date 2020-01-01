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
package net.sourceforge.fixagora.basis.server.control.spreadsheet;


/**
 * The Class FIXCheckEntry.
 */
public class FIXCheckEntry {

	/** The column. */
	int column = -1;
	
	/** The value. */
	Object value = null;
	
	/** The key. */
	boolean key = false;

	
	/**
	 * Instantiates a new fIX check entry.
	 *
	 * @param column the column
	 * @param value the value
	 * @param key the key
	 */
	public FIXCheckEntry(int column, Object value, boolean key) {

		super();
		this.column = column;
		this.value = value;
		this.key = key;
	}

	
	/**
	 * Checks if is key.
	 *
	 * @return true, if is key
	 */
	public boolean isKey() {
	
		return key;
	}
	
	
	/**
	 * Gets the column.
	 *
	 * @return the column
	 */
	public int getColumn() {
	
		return column;
	}


	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
	
		return value;
	}
	
}
