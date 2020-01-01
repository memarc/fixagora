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
 * The Class DataDictionary.
 */
public class DataDictionary implements Serializable, Comparable<DataDictionary> {

	private static final long serialVersionUID = 1L;

	/** The name. */
	String name = null;
	
	/** The dictionary. */
	StringBuffer dictionary = null;

	private byte[] compressed = null;	
	
	
	/**
	 * Instantiates a new data dictionary.
	 *
	 * @param name the name
	 * @param dictionary the dictionary
	 * @param compressed the compressed
	 */
	public DataDictionary(String name, StringBuffer dictionary, byte[] compressed) {

		super();
		this.name = name;
		this.dictionary = dictionary;
		this.compressed  = compressed;
	}


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
	
		return name;
	}

	
	/**
	 * Gets the dictionary.
	 *
	 * @return the dictionary
	 */
	public StringBuffer getDictionary() {
	
		return dictionary;
	}	

	
	/**
	 * Gets the compressed.
	 *
	 * @return the compressed
	 */
	public byte[] getCompressed() {
	
		return compressed;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return name;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataDictionary o) {

		return name.compareTo(o.getName());
		
	}
	

	
}
