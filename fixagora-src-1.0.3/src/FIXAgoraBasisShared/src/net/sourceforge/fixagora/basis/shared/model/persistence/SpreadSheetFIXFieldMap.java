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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXField.ValueType;

/**
 * The Class SpreadSheetFIXFieldMap.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class SpreadSheetFIXFieldMap implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@javax.persistence.OneToMany(cascade = CascadeType.ALL)
	@javax.persistence.MapKey(name = "fixNumber")
	@JoinTable(name="SSFF")
	private Map<Integer, SpreadSheetFIXField> spreadsheetFIXFields = new HashMap<Integer, SpreadSheetFIXField>();

	@javax.persistence.OneToMany(cascade = CascadeType.ALL)
	@javax.persistence.MapKey(name = "fixNumber")
	@JoinTable(name="SSFGL")
	private Map<Integer, SpreadSheetFIXGroupList> spreadSheetFIXGroupLists = new HashMap<Integer, SpreadSheetFIXGroupList>();

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
	 * Gets the spreadsheet fix fields.
	 *
	 * @return the spreadsheet fix fields
	 */
	public Map<Integer, SpreadSheetFIXField> getSpreadsheetFIXFields() {
	
		return spreadsheetFIXFields;
	}

	
	/**
	 * Sets the spreadsheet fix fields.
	 *
	 * @param spreadsheetFIXFields the spreadsheet fix fields
	 */
	public void setSpreadsheetFIXFields(Map<Integer, SpreadSheetFIXField> spreadsheetFIXFields) {
	
		this.spreadsheetFIXFields = spreadsheetFIXFields;
	}

	
	/**
	 * Gets the spread sheet fix group lists.
	 *
	 * @return the spread sheet fix group lists
	 */
	public Map<Integer, SpreadSheetFIXGroupList> getSpreadSheetFIXGroupLists() {
	
		return spreadSheetFIXGroupLists;
	}

	
	/**
	 * Sets the spread sheet fix group lists.
	 *
	 * @param spreadSheetFIXGroupLists the spread sheet fix group lists
	 */
	public void setSpreadSheetFIXGroupLists(Map<Integer, SpreadSheetFIXGroupList> spreadSheetFIXGroupLists) {
	
		this.spreadSheetFIXGroupLists = spreadSheetFIXGroupLists;
	}

	/**
	 * Checks if is sets the field.
	 *
	 * @param field the field
	 * @return true, if is sets the field
	 */
	public boolean isSetField(int field) {

		return spreadsheetFIXFields.get(field) != null;
	}

	/**
	 * Gets the group count.
	 *
	 * @param field the field
	 * @return the group count
	 */
	public int getGroupCount(int field) {

		SpreadSheetFIXGroupList fixInputGroup = spreadSheetFIXGroupLists.get(field);
		if (fixInputGroup == null)
			return 0;
		return fixInputGroup.getGroupCount();
	}

	/**
	 * Removes the group.
	 *
	 * @param fixInputGroup the fix input group
	 */
	public void removeGroup(SpreadSheetFIXGroup fixInputGroup) {

		SpreadSheetFIXGroupList fixInputGroupList = spreadSheetFIXGroupLists.get(fixInputGroup.getFixNumber());
		if (fixInputGroupList != null)
			fixInputGroupList.remove(fixInputGroup);

	}

	/**
	 * Adds the group.
	 *
	 * @param fixInputGroup the fix input group
	 */
	public void addGroup(SpreadSheetFIXGroup fixInputGroup) {

		SpreadSheetFIXGroupList fixInputGroupList = spreadSheetFIXGroupLists.get(fixInputGroup.getFixNumber());
		if (fixInputGroupList == null) {
			fixInputGroupList = new SpreadSheetFIXGroupList();
			fixInputGroupList.setFixNumber(fixInputGroup.getFixNumber());
			spreadSheetFIXGroupLists.put(fixInputGroup.getFixNumber(), fixInputGroupList);
		}
		fixInputGroupList.add(fixInputGroup);
	}

	/**
	 * Gets the groups.
	 *
	 * @param number the number
	 * @return the groups
	 */
	public List<SpreadSheetFIXGroup> getGroups(int number) {

		SpreadSheetFIXGroupList fixInputGroupList = spreadSheetFIXGroupLists.get(number);
		if (fixInputGroupList != null)
			return fixInputGroupList.getSpreadSheetFixGroups();
		return new ArrayList<SpreadSheetFIXGroup>();
	}

	/**
	 * Make eager.
	 */
	public void makeEager() {

		for (SpreadSheetFIXGroupList fixInputGroupList : spreadSheetFIXGroupLists.values())
			fixInputGroupList.makeEager();
		spreadsheetFIXFields.size();
	}

	/**
	 * Checks for group.
	 *
	 * @param number the number
	 * @return true, if successful
	 */
	public boolean hasGroup(int number) {

		return spreadSheetFIXGroupLists.get(number) != null;
	}

	/**
	 * Removes the field.
	 *
	 * @param number the number
	 */
	public void removeField(int number) {

		spreadsheetFIXFields.remove(number);

	}

	/**
	 * Removes the group.
	 *
	 * @param number the number
	 */
	public void removeGroup(int number) {

		spreadSheetFIXGroupLists.remove(number);

	}

	/**
	 * Gets the group.
	 *
	 * @param position the position
	 * @param number the number
	 * @return the group
	 */
	public SpreadSheetFIXGroup getGroup(int position, int number) {

		SpreadSheetFIXGroupList fixInputGroupList = spreadSheetFIXGroupLists.get(number);
		if (fixInputGroupList == null)
			return null;
		if (fixInputGroupList.getSpreadSheetFixGroups().size() < position)
			return null;
		return fixInputGroupList.getSpreadSheetFixGroups().get(position - 1);
	}

	/**
	 * Sets the spread column.
	 *
	 * @param number the number
	 * @param valueType the value type
	 * @param name the name
	 * @param index the index
	 */
	public void setSpreadColumn(int number,ValueType valueType, String name, int index) {

		SpreadSheetFIXField spreadsheetFIXField = spreadsheetFIXFields.get(number);
		if(spreadsheetFIXField==null)
		{
			spreadsheetFIXField = new SpreadSheetFIXField();
			spreadsheetFIXField.setFixNumber(number);
			spreadsheetFIXField.setFixName(name);
			spreadsheetFIXField.setFixFieldMap(this);
			spreadsheetFIXField.setValueType(valueType);
			spreadsheetFIXFields.put(number, spreadsheetFIXField);
		}
		spreadsheetFIXField.setSpreadColumn(index);
		
	}
	
	/**
	 * Sets the key value.
	 *
	 * @param number the number
	 * @param valueType the value type
	 * @param name the name
	 * @param key the key
	 */
	public void setKeyValue(int number, ValueType valueType,String name, boolean key) {
		
		SpreadSheetFIXField spreadsheetFIXField = spreadsheetFIXFields.get(number);
		if(spreadsheetFIXField==null)
		{
			spreadsheetFIXField = new SpreadSheetFIXField();
			spreadsheetFIXField.setFixNumber(number);
			spreadsheetFIXField.setFixName(name);
			spreadsheetFIXField.setFixFieldMap(this);
			spreadsheetFIXField.setValueType(valueType);
			spreadsheetFIXFields.put(number, spreadsheetFIXField);
		}
		spreadsheetFIXField.setKeyValue(key);
		
	}
	
	/**
	 * Gets the spread column.
	 *
	 * @param number the number
	 * @return the spread column
	 */
	public int getSpreadColumn(int number) {

		SpreadSheetFIXField spreadsheetFIXField = spreadsheetFIXFields.get(number);
		
		if(spreadsheetFIXField==null)
			return -1;
		
		return spreadsheetFIXField.getSpreadColumn();
		
	}
	
	/**
	 * Gets the key value.
	 *
	 * @param number the number
	 * @return the key value
	 */
	public boolean getKeyValue(int number) {

		SpreadSheetFIXField spreadsheetFIXField = spreadsheetFIXFields.get(number);
		if(spreadsheetFIXField==null)
			return false;
		return spreadsheetFIXField.getKeyValue();
		
	}

	/**
	 * Checks if is header or trailer.
	 *
	 * @return true, if is header or trailer
	 */
	public boolean isHeaderOrTrailer() {

		if(this instanceof SpreadSheetFIXInputMessage || this instanceof SpreadSheetFIXOutputMessage || this instanceof SpreadSheetFIXGroup)
			return false;
		return true;
	}


}
