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
import javax.persistence.ManyToOne;

/**
 * The Class SpreadSheetFIXField.
 */
@Entity
public class SpreadSheetFIXField implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum ValueType.
	 */
	public enum ValueType{/** The integer. */
INTEGER,/** The double. */
DOUBLE,/** The boolean. */
BOOLEAN,/** The text. */
TEXT,/** The date. */
DATE,/** The time. */
TIME,/** The datetime. */
DATETIME}
	
	@Id
	@GeneratedValue
	private long id;	

	@Column(unique = false, nullable = false)
	private Integer fixNumber = null;
	
	@Column(unique = false, nullable = false)
	private String fixName = null;
	
	@Column(unique = false, nullable = false)
	private Boolean keyValue = false;

	@Column(unique = false, nullable = false)
	private Integer spreadColumn = null;
	
	@Column(unique = false, nullable = false)
	private ValueType valueType = null;

	@ManyToOne
	private SpreadSheetFIXFieldMap fixFieldMap = null;	

	
	/**
	 * Gets the fix name.
	 *
	 * @return the fix name
	 */
	public String getFixName() {
	
		return fixName;
	}

	
	/**
	 * Sets the fix name.
	 *
	 * @param fixName the new fix name
	 */
	public void setFixName(String fixName) {
	
		this.fixName = fixName;
	}


	/**
	 * Gets the fix field map.
	 *
	 * @return the fix field map
	 */
	public SpreadSheetFIXFieldMap getFixFieldMap() {
	
		return fixFieldMap;
	}


	
	/**
	 * Sets the fix field map.
	 *
	 * @param fixFieldMap the new fix field map
	 */
	public void setFixFieldMap(SpreadSheetFIXFieldMap fixFieldMap) {
	
		this.fixFieldMap = fixFieldMap;
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

	
	/**
	 * Gets the fix number.
	 *
	 * @return the fix number
	 */
	public Integer getFixNumber() {
	
		return fixNumber;
	}

	
	/**
	 * Sets the fix number.
	 *
	 * @param fixNumber the new fix number
	 */
	public void setFixNumber(Integer fixNumber) {
	
		this.fixNumber = fixNumber;
	}


	
	/**
	 * Gets the key value.
	 *
	 * @return the key value
	 */
	public Boolean getKeyValue() {
	
		return keyValue;
	}


	
	/**
	 * Sets the key value.
	 *
	 * @param keyValue the new key value
	 */
	public void setKeyValue(Boolean keyValue) {
	
		this.keyValue = keyValue;
	}


	
	/**
	 * Gets the spread column.
	 *
	 * @return the spread column
	 */
	public Integer getSpreadColumn() {
	
		return spreadColumn;
	}


	
	/**
	 * Sets the spread column.
	 *
	 * @param spreadColumn the new spread column
	 */
	public void setSpreadColumn(Integer spreadColumn) {
	
		this.spreadColumn = spreadColumn;
	}




	/**
	 * Gets the value type.
	 *
	 * @return the value type
	 */
	public ValueType getValueType() {

		return valueType;
	}




	/**
	 * Sets the value type.
	 *
	 * @param valueType the new value type
	 */
	public void setValueType(ValueType valueType) {

		this.valueType = valueType;
	}

	
	
	
}
