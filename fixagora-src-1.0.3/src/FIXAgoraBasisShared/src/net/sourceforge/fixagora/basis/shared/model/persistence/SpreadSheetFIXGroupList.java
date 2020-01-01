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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The Class SpreadSheetFIXGroupList.
 */
@Entity
public class SpreadSheetFIXGroupList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private long id;	
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name="spreadsheetfixgrouplist_id", nullable=false)
	private List<SpreadSheetFIXGroup> spreadSheetFixGroups = new ArrayList<SpreadSheetFIXGroup>();
	
	@Column(unique = false, nullable = false)
	private Integer fixNumber = null;
	
	@ManyToOne
	private SpreadSheetFIXFieldMap fixFieldMap = null;
		
	
	
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
	 * Gets the spread sheet fix groups.
	 *
	 * @return the spread sheet fix groups
	 */
	public List<SpreadSheetFIXGroup> getSpreadSheetFixGroups() {
	
		return spreadSheetFixGroups;
	}



	
	/**
	 * Sets the spread sheet fix groups.
	 *
	 * @param spreadSheetFixGroups the new spread sheet fix groups
	 */
	public void setSpreadSheetFixGroups(List<SpreadSheetFIXGroup> spreadSheetFixGroups) {
	
		this.spreadSheetFixGroups = spreadSheetFixGroups;
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
	 * Make eager.
	 */
	public void makeEager() {

		for(SpreadSheetFIXGroup fixInputGroup: spreadSheetFixGroups)
			fixInputGroup.makeEager();
		
	}


	/**
	 * Gets the group count.
	 *
	 * @return the group count
	 */
	public int getGroupCount() {
		
		return spreadSheetFixGroups.size();
	}


	/**
	 * Removes the.
	 *
	 * @param fixInputGroup the fix input group
	 */
	public void remove(SpreadSheetFIXGroup fixInputGroup) {

		spreadSheetFixGroups.remove(fixInputGroup);
		
	}


	/**
	 * Adds the.
	 *
	 * @param fixInputGroup the fix input group
	 */
	public void add(SpreadSheetFIXGroup fixInputGroup) {

		fixInputGroup.setSpreadSheetFIXGroupList(this);
		spreadSheetFixGroups.add(fixInputGroup);
		
	}
	

	
}
