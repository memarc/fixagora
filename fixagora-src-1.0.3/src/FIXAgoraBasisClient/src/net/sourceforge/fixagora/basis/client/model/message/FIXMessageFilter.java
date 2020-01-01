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
 * The Class FIXMessageFilter.
 */
public class FIXMessageFilter {

	private boolean hideEmptyField = false;

	private boolean hideHeader = false;

	private boolean hideOptionalFields = false;

	/**
	 * Instantiates a new fIX message filter.
	 */
	public FIXMessageFilter() {

		super();
	}


	/**
	 * Checks if is hide empty fields.
	 *
	 * @return true, if is hide empty fields
	 */
	public boolean isHideEmptyFields() {

		return hideEmptyField;
	}

	/**
	 * Checks if is hide header.
	 *
	 * @return true, if is hide header
	 */
	public boolean isHideHeader() {

		return hideHeader;
	}


	/**
	 * Checks if is hide optional fields.
	 *
	 * @return true, if is hide optional fields
	 */
	public boolean isHideOptionalFields() {

		return hideOptionalFields;
	}


	/**
	 * Sets the hide empty fields.
	 *
	 * @param hideEmptyField the new hide empty fields
	 */
	public void setHideEmptyFields(final boolean hideEmptyField) {

		this.hideEmptyField = hideEmptyField;
	}

	/**
	 * Sets the hide header.
	 *
	 * @param hideHeader the new hide header
	 */
	public void setHideHeader(final boolean hideHeader) {

		this.hideHeader = hideHeader;
	}


	/**
	 * Sets the hide optional fields.
	 *
	 * @param hideOptionalFields the new hide optional fields
	 */
	public void setHideOptionalFields(final boolean hideOptionalFields) {

		this.hideOptionalFields = hideOptionalFields;
	}


}
