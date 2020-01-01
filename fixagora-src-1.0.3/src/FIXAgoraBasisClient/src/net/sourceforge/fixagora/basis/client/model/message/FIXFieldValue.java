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
package net.sourceforge.fixagora.basis.client.model.message;

/**
 * The Class FIXFieldValue.
 */
public class FIXFieldValue {

	private String description = null;

	private String value = null;

	/**
	 * Instantiates a new fIX field value.
	 *
	 * @param value the value
	 * @param description the description
	 */
	public FIXFieldValue(final String value, final String description) {

		super();
		
		this.value = value;
		this.description = description;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {

		return value;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(final String description) {

		this.description = description;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(final String value) {

		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(value);
		stringBuffer.append(" - ");
		stringBuffer.append(description);
		
		return stringBuffer.toString();
	}

}
