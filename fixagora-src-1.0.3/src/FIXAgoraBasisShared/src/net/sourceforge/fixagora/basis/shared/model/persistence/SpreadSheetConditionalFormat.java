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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * The Class SpreadSheetConditionalFormat.
 */
@Entity
public class SpreadSheetConditionalFormat implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;	

	@Column(unique = false, nullable = false)
	private Long spreadSheet = 0l;
	
	@Column(unique = false, nullable = false)
	private Integer cellRow = null;

	@Column(unique = false, nullable = false)
	private Integer cellColumn = null;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL, orphanRemoval=true)
	private SpreadSheetCondition spreadSheetCondition1 = null;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL, orphanRemoval=true)
	private SpreadSheetCondition spreadSheetCondition2 = null;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL, orphanRemoval=true)
	private SpreadSheetCondition spreadSheetCondition3 = null;
	
	@Transient
	private int condition = 0;
	
	/**
	 * Gets the condition.
	 *
	 * @return the condition
	 */
	@Transient
	public int getCondition() {
	
		return condition;
	}

	
	/**
	 * Sets the condition.
	 *
	 * @param condition the new condition
	 */
	@Transient
	public void setCondition(int condition) {
	
		this.condition = condition;
	}



	/**
	 * Gets the spread sheet condition1.
	 *
	 * @return the spread sheet condition1
	 */
	public SpreadSheetCondition getSpreadSheetCondition1() {
	
		return spreadSheetCondition1;
	}


	
	/**
	 * Sets the spread sheet condition1.
	 *
	 * @param spreadSheetCondition1 the new spread sheet condition1
	 */
	public void setSpreadSheetCondition1(SpreadSheetCondition spreadSheetCondition1) {
	
		this.spreadSheetCondition1 = spreadSheetCondition1;
	}


	
	/**
	 * Gets the spread sheet condition2.
	 *
	 * @return the spread sheet condition2
	 */
	public SpreadSheetCondition getSpreadSheetCondition2() {
	
		return spreadSheetCondition2;
	}


	
	/**
	 * Sets the spread sheet condition2.
	 *
	 * @param spreadSheetCondition2 the new spread sheet condition2
	 */
	public void setSpreadSheetCondition2(SpreadSheetCondition spreadSheetCondition2) {
	
		this.spreadSheetCondition2 = spreadSheetCondition2;
	}


	
	/**
	 * Gets the spread sheet condition3.
	 *
	 * @return the spread sheet condition3
	 */
	public SpreadSheetCondition getSpreadSheetCondition3() {
	
		return spreadSheetCondition3;
	}


	
	/**
	 * Sets the spread sheet condition3.
	 *
	 * @param spreadSheetCondition3 the new spread sheet condition3
	 */
	public void setSpreadSheetCondition3(SpreadSheetCondition spreadSheetCondition3) {
	
		this.spreadSheetCondition3 = spreadSheetCondition3;
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
		
		if(obj instanceof SpreadSheetConditionalFormat)
		{
			SpreadSheetConditionalFormat spreadSheetConditionalFormat = (SpreadSheetConditionalFormat)obj;
			return spreadSheet==spreadSheetConditionalFormat.getSpreadSheet()&&spreadSheetConditionalFormat.getCellRow()==getCellRow()&&spreadSheetConditionalFormat.getCellColumn()==getCellColumn();
			
		}
		return false;
	}
	
	/**
	 * Copy.
	 *
	 * @return the spread sheet conditional format
	 */
	public SpreadSheetConditionalFormat copy()
	{
		SpreadSheetConditionalFormat spreadSheetConditionalFormat = new SpreadSheetConditionalFormat();
		if(spreadSheetCondition1!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition1(spreadSheetCondition1.clone());
		if(spreadSheetCondition2!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition2(spreadSheetCondition2.clone());
		if(spreadSheetCondition3!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition3(spreadSheetCondition3.clone());
		
		return spreadSheetConditionalFormat;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SpreadSheetConditionalFormat clone()
	{
		SpreadSheetConditionalFormat spreadSheetConditionalFormat = new SpreadSheetConditionalFormat();
		if(spreadSheetCondition1!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition1(spreadSheetCondition1.clone());
		if(spreadSheetCondition2!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition2(spreadSheetCondition2.clone());
		if(spreadSheetCondition3!=null)
			spreadSheetConditionalFormat.setSpreadSheetCondition3(spreadSheetCondition3.clone());
		
		spreadSheetConditionalFormat.setCellColumn(getCellColumn());
		spreadSheetConditionalFormat.setCellRow(getCellRow());
		spreadSheetConditionalFormat.setCondition(getCondition());
		spreadSheetConditionalFormat.setId(getId());
		spreadSheetConditionalFormat.setSpreadSheet(getSpreadSheet());
		
		return spreadSheetConditionalFormat;
	}
	

	/**
	 * Update.
	 *
	 * @param spreadSheetConditionalFormat the spread sheet conditional format
	 */
	public void update(SpreadSheetConditionalFormat spreadSheetConditionalFormat) {

		if(spreadSheetConditionalFormat.getSpreadSheetCondition1()!=null)
			spreadSheetCondition1 = spreadSheetConditionalFormat.getSpreadSheetCondition1().clone();
		else
			spreadSheetCondition1 = null;
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition2()!=null)
			spreadSheetCondition2 = spreadSheetConditionalFormat.getSpreadSheetCondition2().clone();
		else
			spreadSheetCondition2 = null;
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition3()!=null)
			spreadSheetCondition3 = spreadSheetConditionalFormat.getSpreadSheetCondition3().clone();
		else
			spreadSheetCondition3 = null;
		
	}
	
	/**
	 * Checks if is same format.
	 *
	 * @param spreadSheetConditionalFormat the spread sheet conditional format
	 * @return true, if is same format
	 */
	public boolean isSameFormat(SpreadSheetConditionalFormat spreadSheetConditionalFormat) {
		
		if(spreadSheetCondition1==null&&spreadSheetConditionalFormat.getSpreadSheetCondition1()!=null)
			return false;
		
		if(spreadSheetCondition1!=null&&!spreadSheetCondition1.isSameFormat(spreadSheetConditionalFormat.getSpreadSheetCondition1()))
			return false;

		
		if(spreadSheetCondition2==null&&spreadSheetConditionalFormat.getSpreadSheetCondition2()!=null)
			return false;
		
		if(spreadSheetCondition2!=null&&!spreadSheetCondition2.isSameFormat(spreadSheetConditionalFormat.getSpreadSheetCondition2()))
			return false;
		
		
		if(spreadSheetCondition3==null&&spreadSheetConditionalFormat.getSpreadSheetCondition3()!=null)
			return false;
		
		if(spreadSheetCondition3!=null&&!spreadSheetCondition3.isSameFormat(spreadSheetConditionalFormat.getSpreadSheetCondition3()))
			return false;
		
		return true;
		
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
	
}
