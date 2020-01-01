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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class FIXFieldOrderEntry.
 */
@Entity
public class FIXFieldOrderEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="fixfieldorderentry_id", insertable=false, updatable=false, nullable=false)
    private SpreadSheetFIXGroup spreadSheetFIXGroup;
	
	@Column(unique = false, nullable = false)
	private Integer fixField = null;

	
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
	 * Gets the spread sheet fix group.
	 *
	 * @return the spread sheet fix group
	 */
	public SpreadSheetFIXGroup getSpreadSheetFIXGroup() {
	
		return spreadSheetFIXGroup;
	}


	
	/**
	 * Sets the spread sheet fix group.
	 *
	 * @param spreadSheetFIXGroup the new spread sheet fix group
	 */
	public void setSpreadSheetFIXGroup(SpreadSheetFIXGroup spreadSheetFIXGroup) {
	
		this.spreadSheetFIXGroup = spreadSheetFIXGroup;
	}


	
	/**
	 * Gets the fix field.
	 *
	 * @return the fix field
	 */
	public Integer getFixField() {
	
		return fixField;
	}


	
	/**
	 * Sets the fix field.
	 *
	 * @param fixField the new fix field
	 */
	public void setFixField(Integer fixField) {
	
		this.fixField = fixField;
	}	

	
}
