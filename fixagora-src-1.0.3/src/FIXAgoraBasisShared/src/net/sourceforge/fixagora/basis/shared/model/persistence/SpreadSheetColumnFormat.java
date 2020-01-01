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

/**
 * The Class SpreadSheetColumnFormat.
 */
@Entity
public class SpreadSheetColumnFormat implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private long id;	

	@Column(unique = false, nullable = false)
	private Long spreadSheet = 0l;
	
	@Column(unique = false, nullable = false)
	private Integer columnWidth = null;
	
	@Column(unique = false, nullable = false)
	private Integer columnNumber = null;

	@Column(unique = false, nullable = false)
	private Boolean hidden = false;

	
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
	 * Gets the column width.
	 *
	 * @return the column width
	 */
	public Integer getColumnWidth() {
	
		return columnWidth;
	}

	
	/**
	 * Sets the column width.
	 *
	 * @param columnWidth the new column width
	 */
	public void setColumnWidth(Integer columnWidth) {
	
		this.columnWidth = columnWidth;
	}

	
	/**
	 * Gets the column number.
	 *
	 * @return the column number
	 */
	public Integer getColumnNumber() {
	
		return columnNumber;
	}

	
	/**
	 * Sets the column number.
	 *
	 * @param column the new column number
	 */
	public void setColumnNumber(Integer column) {
	
		this.columnNumber = column;
	}

	
	/**
	 * Gets the hidden.
	 *
	 * @return the hidden
	 */
	public Boolean getHidden() {
	
		return hidden;
	}

	
	/**
	 * Sets the hidden.
	 *
	 * @param hidden the new hidden
	 */
	public void setHidden(Boolean hidden) {
	
		this.hidden = hidden;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof SpreadSheetColumnFormat)
		{
			SpreadSheetColumnFormat spreadSheetColumnFormat = (SpreadSheetColumnFormat)obj;
			return spreadSheet==spreadSheetColumnFormat.getSpreadSheet()&&spreadSheetColumnFormat.getColumnNumber()==getColumnNumber();
			
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
	    buffer.append(getColumnNumber());	    
	    return buffer.toString().hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SpreadSheetColumnFormat clone()
	{
		SpreadSheetColumnFormat spreadSheetColumnFormat = new SpreadSheetColumnFormat();
		spreadSheetColumnFormat.setColumnNumber(getColumnNumber());
		spreadSheetColumnFormat.setColumnWidth(getColumnWidth());
		spreadSheetColumnFormat.setHidden(getHidden());
		spreadSheetColumnFormat.setId(getId());
		spreadSheetColumnFormat.setSpreadSheet(getSpreadSheet());
		return spreadSheetColumnFormat;
	}
	
}
