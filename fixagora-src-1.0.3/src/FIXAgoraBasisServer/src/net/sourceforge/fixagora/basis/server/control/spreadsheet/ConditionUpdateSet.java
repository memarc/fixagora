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

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;


/**
 * The Class ConditionUpdateSet.
 */
public class ConditionUpdateSet {
	
	/** The original cell. */
	Cell originalCell = null;
	
	/** The condition cell. */
	HSSFCell conditionCell = null;
	
	/** The spread sheet condition. */
	SpreadSheetCondition spreadSheetCondition = null;
	
	/** The condition number. */
	int conditionNumber = 0;

	/**
	 * Instantiates a new condition update set.
	 *
	 * @param originalCell the original cell
	 * @param conditionCell the condition cell
	 * @param spreadSheetCondition the spread sheet condition
	 * @param conditionNumber the condition number
	 */
	public ConditionUpdateSet(Cell originalCell, HSSFCell conditionCell, SpreadSheetCondition spreadSheetCondition, int conditionNumber) {

		super();
		this.originalCell = originalCell;
		this.conditionCell = conditionCell;
		this.spreadSheetCondition = spreadSheetCondition;
		this.conditionNumber = conditionNumber;
	}

	
	/**
	 * Gets the original cell.
	 *
	 * @return the original cell
	 */
	public Cell getOriginalCell() {
	
		return originalCell;
	}

	
	/**
	 * Gets the condition cell.
	 *
	 * @return the condition cell
	 */
	public HSSFCell getConditionCell() {
	
		return conditionCell;
	}

	
	/**
	 * Gets the spread sheet condition.
	 *
	 * @return the spread sheet condition
	 */
	public SpreadSheetCondition getSpreadSheetCondition() {
	
		return spreadSheetCondition;
	}

	
	/**
	 * Gets the condition number.
	 *
	 * @return the condition number
	 */
	public int getConditionNumber() {
	
		return conditionNumber;
	}
	
	

}
