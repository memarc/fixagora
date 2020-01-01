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

import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel.EntryStatus;



/**
 * The Class ExcelImportCell.
 */
public class ExcelImportCell {
	
	private String cellValue = null;
	
	private EntryStatus entryStatus = null;	
	
	/**
	 * Instantiates a new excel import cell.
	 *
	 * @param cellValue the cell value
	 * @param entryStatus the entry status
	 */
	public ExcelImportCell(String cellValue, EntryStatus entryStatus) {

		super();
		this.cellValue = cellValue;
		this.entryStatus = entryStatus;
	}


	/**
	 * Gets the cell value.
	 *
	 * @return the cell value
	 */
	public String getCellValue() {
	
		return cellValue;
	}

	
	/**
	 * Gets the entry status.
	 *
	 * @return the entry status
	 */
	public EntryStatus getEntryStatus() {
	
		return entryStatus;
	}
	


}
