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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * The Class SpreadSheetCellContent.
 */
@Entity
public class SpreadSheetCellContent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum SpreadSheetCellType.
	 */
	public enum SpreadSheetCellType {
		
		/** The type string. */
		TYPE_STRING, 
 /** The type formula. */
 TYPE_FORMULA, 
 /** The type boolean. */
 TYPE_BOOLEAN, 
 /** The type numeric. */
 TYPE_NUMERIC, 
 /** The clear. */
 CLEAR
	};
	
	@Id
	@GeneratedValue
	private long id;	

	@Column(unique = false, nullable = false)
	private Long spreadSheet = 0l;

	@Column(unique = false, nullable = false)
	private Integer cellRow = null;

	@Column(unique = false, nullable = false)
	private Integer cellColumn = null;

	@Column(unique = false, nullable = false)
	private SpreadSheetCellType spreadSheetCellType = null;

	@Column(unique = false, nullable = true)
	private String formulaValue = null;

	@Column(unique = false, nullable = true)
	private String stringValue = null;

	@Column(unique = false, nullable = true)
	private Boolean booleanValue = null;

	@Column(unique = false, nullable = true)
	private Double numericValue = null;
	

	@Transient
	private Object calculatedValue = null; 




	/**
	 * Gets the calculated value.
	 *
	 * @return the calculated value
	 */
	public Object getCalculatedValue() {
	
		return calculatedValue;
	}


	
	/**
	 * Sets the calculated value.
	 *
	 * @param calculatedValue the new calculated value
	 */
	public void setCalculatedValue(Object calculatedValue) {
	
		this.calculatedValue = calculatedValue;
	}


	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public long getSpreadSheet() {
	
		return spreadSheet;
	}

	
	/**
	 * Sets the spread sheet.
	 *
	 * @param spreadSheet the new spread sheet
	 */
	public void setSpreadSheet(long spreadSheet) {
	
		this.spreadSheet = spreadSheet;
	}

	
	
	/**
	 * Gets the cell row.
	 *
	 * @return the cell row
	 */
	public Integer getCellRow() {
	
		return cellRow;
	}



	
	/**
	 * Sets the cell row.
	 *
	 * @param cellRow the new cell row
	 */
	public void setCellRow(Integer cellRow) {
	
		this.cellRow = cellRow;
	}



	
	/**
	 * Gets the cell column.
	 *
	 * @return the cell column
	 */
	public Integer getCellColumn() {
	
		return cellColumn;
	}



	
	/**
	 * Sets the cell column.
	 *
	 * @param cellColumn the new cell column
	 */
	public void setCellColumn(Integer cellColumn) {
	
		this.cellColumn = cellColumn;
	}



	/**
	 * Gets the spread sheet cell type.
	 *
	 * @return the spread sheet cell type
	 */
	public SpreadSheetCellType getSpreadSheetCellType() {
	
		return spreadSheetCellType;
	}

	
	/**
	 * Sets the spread sheet cell type.
	 *
	 * @param spreadSheetCellType the new spread sheet cell type
	 */
	public void setSpreadSheetCellType(SpreadSheetCellType spreadSheetCellType) {
	
		this.spreadSheetCellType = spreadSheetCellType;
	}

	
	/**
	 * Gets the formula value.
	 *
	 * @return the formula value
	 */
	public String getFormulaValue() {
	
		return formulaValue;
	}

	
	/**
	 * Sets the formula value.
	 *
	 * @param formulaValue the new formula value
	 */
	public void setFormulaValue(String formulaValue) {
	
		this.formulaValue = formulaValue;
	}

	
	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public String getStringValue() {
	
		return stringValue;
	}

	
	/**
	 * Sets the string value.
	 *
	 * @param stringValue the new string value
	 */
	public void setStringValue(String stringValue) {
	
		this.stringValue = stringValue;
	}

	
	/**
	 * Gets the boolean value.
	 *
	 * @return the boolean value
	 */
	public Boolean getBooleanValue() {
	
		return booleanValue;
	}

	
	/**
	 * Sets the boolean value.
	 *
	 * @param booleanValue the new boolean value
	 */
	public void setBooleanValue(Boolean booleanValue) {
	
		this.booleanValue = booleanValue;
	}

	
	/**
	 * Gets the numeric value.
	 *
	 * @return the numeric value
	 */
	public Double getNumericValue() {
	
		return numericValue;
	}

	
	/**
	 * Sets the numeric value.
	 *
	 * @param numericValue the new numeric value
	 */
	public void setNumericValue(Double numericValue) {
	
		this.numericValue = numericValue;
	}

	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}

	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof SpreadSheetCellContent)
		{
			SpreadSheetCellContent spreadSheetCellContent = (SpreadSheetCellContent)obj;
			return spreadSheet == spreadSheetCellContent.getSpreadSheet() && spreadSheetCellContent.getCellRow()==getCellRow()&&spreadSheetCellContent.getCellColumn()==getCellColumn();
			
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(spreadSheet);
	    buffer.append("+");
	    buffer.append(getCellRow());
	    buffer.append("+");
	    buffer.append(getCellColumn());	    
	    return buffer.toString().hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SpreadSheetCellContent clone()
	{
		SpreadSheetCellContent spreadSheetCellContent = new SpreadSheetCellContent();
		spreadSheetCellContent.setBooleanValue(getBooleanValue());
		spreadSheetCellContent.setCalculatedValue(getCalculatedValue());
		spreadSheetCellContent.setCellColumn(getCellColumn());
		spreadSheetCellContent.setCellRow(getCellRow());
		spreadSheetCellContent.setFormulaValue(getFormulaValue());
		spreadSheetCellContent.setId(getId());
		spreadSheetCellContent.setNumericValue(getNumericValue());
		spreadSheetCellContent.setSpreadSheet(getSpreadSheet());
		spreadSheetCellContent.setSpreadSheetCellType(getSpreadSheetCellType());
		spreadSheetCellContent.setStringValue(getStringValue());
		return spreadSheetCellContent;
	}
	
}
