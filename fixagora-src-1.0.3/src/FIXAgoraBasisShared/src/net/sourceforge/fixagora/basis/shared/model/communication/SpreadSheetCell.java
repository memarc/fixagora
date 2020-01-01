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
package net.sourceforge.fixagora.basis.shared.model.communication;

import java.io.Serializable;


/**
 * The Class SpreadSheetCell.
 */
public class SpreadSheetCell implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private long sheet = 0;
		
	private Object value = null;
	
	private String formula = null;
	
	private int row = 0;
	
	private int column = 0;
	
	private int condition = 0;
	
	
	/**
	 * Instantiates a new spread sheet cell.
	 *
	 * @param sheet the sheet
	 * @param value the value
	 * @param formula the formula
	 * @param row the row
	 * @param column the column
	 */
	public SpreadSheetCell(long sheet, Object value, String formula, int row, int column) {

		super();
		this.sheet = sheet;
		this.value = value;
		this.formula = formula;
		this.row = row;
		this.column = column;
	}
	
	/**
	 * Instantiates a new spread sheet cell.
	 *
	 * @param sheet the sheet
	 * @param value the value
	 * @param formula the formula
	 * @param row the row
	 * @param column the column
	 * @param condition the condition
	 */
	public SpreadSheetCell(long sheet, Object value, String formula, int row, int column, int condition) {

		super();
		this.sheet = sheet;
		this.value = value;
		this.formula = formula;
		this.row = row;
		this.column = column;
		this.condition = condition;
	}

	
	
	/**
	 * Gets the condition.
	 *
	 * @return the condition
	 */
	public int getCondition() {
	
		return condition;
	}




	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value) {
	
		this.value = value;
	}



	
	/**
	 * Sets the formula.
	 *
	 * @param formula the new formula
	 */
	public void setFormula(String formula) {
	
		this.formula = formula;
	}



	
	/**
	 * Sets the row.
	 *
	 * @param row the new row
	 */
	public void setRow(int row) {
	
		this.row = row;
	}



	
	/**
	 * Sets the column.
	 *
	 * @param column the new column
	 */
	public void setColumn(int column) {
	
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


	/**
	 * Gets the sheet.
	 *
	 * @return the sheet
	 */
	public long getSheet() {
	
		return sheet;
	}

	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
	
		return value;
	}

	
	/**
	 * Gets the formula.
	 *
	 * @return the formula
	 */
	public String getFormula() {
	
		return formula;
	}

}
