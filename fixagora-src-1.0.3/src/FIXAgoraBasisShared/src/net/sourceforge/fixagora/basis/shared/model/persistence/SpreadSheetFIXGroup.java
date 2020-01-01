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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * The Class SpreadSheetFIXGroup.
 */
@Entity
public class SpreadSheetFIXGroup extends SpreadSheetFIXFieldMap implements Serializable {

	private static final long serialVersionUID = 1L;
	

	@Column(unique = false, nullable = false)
	private Integer fixNumber = null;
	
	@Column(unique = false, nullable = false)
	private String fixName = null;
	
	@Column(unique = false, nullable = false)
	private Boolean groupIterator = false;
	
	@ManyToOne
    @JoinColumn(name="spreadsheetfixgrouplist_id", insertable=false, updatable=false, nullable=false)
    private SpreadSheetFIXGroupList spreadSheetFIXGroupList;	
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
	@OrderBy(value="id asc")
    @JoinColumn(name="fixfieldorderentry_id", nullable=false)
	private List<FIXFieldOrderEntry> fixFieldOrderEntries;	
	
	/**
	 * Gets the fix name.
	 *
	 * @return the fix name
	 */
	public String getFixName() {
	
		return fixName;
	}

	
	/**
	 * Gets the fix field order entries.
	 *
	 * @return the fix field order entries
	 */
	public List<FIXFieldOrderEntry> getFixFieldOrderEntries() {
	
		if(fixFieldOrderEntries==null)
			fixFieldOrderEntries = new ArrayList<FIXFieldOrderEntry>();
		return fixFieldOrderEntries;
	}


	
	/**
	 * Sets the fix field order entries.
	 *
	 * @param fixFieldOrderEntries the new fix field order entries
	 */
	public void setFixFieldOrderEntries(List<FIXFieldOrderEntry> fixFieldOrderEntries) {
	
		this.fixFieldOrderEntries = fixFieldOrderEntries;
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
	 * Gets the spread sheet fix group list.
	 *
	 * @return the spread sheet fix group list
	 */
	public SpreadSheetFIXGroupList getSpreadSheetFIXGroupList() {
	
		return spreadSheetFIXGroupList;
	}


	
	/**
	 * Sets the spread sheet fix group list.
	 *
	 * @param spreadSheetFIXGroupList the new spread sheet fix group list
	 */
	public void setSpreadSheetFIXGroupList(SpreadSheetFIXGroupList spreadSheetFIXGroupList) {
	
		this.spreadSheetFIXGroupList = spreadSheetFIXGroupList;
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
	 * Gets the group iterator.
	 *
	 * @return the group iterator
	 */
	public Boolean getGroupIterator() {
	
		return groupIterator;
	}



	
	/**
	 * Sets the group iterator.
	 *
	 * @param groupIterator the new group iterator
	 */
	public void setGroupIterator(Boolean groupIterator) {
	
		this.groupIterator = groupIterator;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap#makeEager()
	 */
	@Override
	public void makeEager() {
		
		super.makeEager();
		getSpreadsheetFIXFields().size();
	}
	
	
}
