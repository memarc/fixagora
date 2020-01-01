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
package net.sourceforge.fixagora.basis.client.model.editor;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;


/**
 * The Class ComboBoxEntry.
 */
public class ComboBoxEntry implements Comparable<ComboBoxEntry>{
	
	private Object entry = null;
	
	private String displayName = null;

	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param entry the entry
	 * @param displayName the display name
	 */
	public ComboBoxEntry(Integer entry, String displayName) {

		super();
		this.entry = entry;
		this.displayName = displayName;
	}
	
	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param entry the entry
	 * @param displayName the display name
	 */
	public ComboBoxEntry(Long entry, String displayName) {

		super();
		this.entry = entry;
		this.displayName = displayName;
	}
	

	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param entry the entry
	 * @param displayName the display name
	 */
	public ComboBoxEntry(String entry, String displayName) {

		super();
		this.entry = entry;
		this.displayName = displayName;
	}
	
	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param entry the entry
	 * @param displayName the display name
	 */
	public ComboBoxEntry(Boolean entry, String displayName) {

		super();
		this.entry = entry;
		this.displayName = displayName;
	}

	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param entry the entry
	 * @param displayName the display name
	 */
	public ComboBoxEntry(AbstractBusinessObject entry, String displayName) {

		super();
		this.entry = entry;
		this.displayName = displayName;
	}
	
	/**
	 * Instantiates a new combo box entry.
	 *
	 * @param displayName the display name
	 */
	public ComboBoxEntry(String displayName) {

		super();
		this.displayName = displayName;
	}


	/**
	 * Instantiates a new combo box entry.
	 */
	public ComboBoxEntry() {

		super();
		this.displayName = " ";
	}
	
	/**
	 * Gets the entry.
	 *
	 * @return the entry
	 */
	public Object getEntry() {
	
		return entry;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return displayName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof ComboBoxEntry)
			object = ((ComboBoxEntry)object).getEntry();

		if(object==null&&entry!=null)
			return false;
		
		if(object!=null&&entry==null)
			return false;
		
		if(object==null&&entry==null)
			return true;
		
		return entry.equals(object);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ComboBoxEntry o) {
		return displayName.compareTo(o.toString());
	}

}
