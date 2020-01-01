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
package net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation;


/**
 * The Class SpreadSheetLocation.
 */
public class SpreadSheetLocation {

	private int row = 0;
	
	private int column = 0;

	/**
	 * Instantiates a new spread sheet location.
	 *
	 * @param row the row
	 * @param column the column
	 */
	public SpreadSheetLocation(int row, int column) {

		super();
		this.row = row;
		this.column = column;
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
	 * Gets the column.
	 *
	 * @return the column
	 */
	public int getColumn() {
	
		return column;
	}
	
	
	
}
