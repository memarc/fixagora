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
 * The Class FontProperties.
 */
public class FontProperties {

	private int fieldDataTypeWidth = 0;

	private int fieldNameWidth = 0;

	private int fieldNumberWidth = 0;

	private int fieldRequiredWidth = 0;

	private int fieldTypeWidth = 0;

	/**
	 * Instantiates a new font properties.
	 */
	public FontProperties() {

		super();
	}

	/**
	 * Gets the field data type width.
	 *
	 * @return the field data type width
	 */
	public int getFieldDataTypeWidth() {

		return fieldDataTypeWidth;
	}

	/**
	 * Gets the field name width.
	 *
	 * @return the field name width
	 */
	public int getFieldNameWidth() {

		return fieldNameWidth;
	}

	/**
	 * Gets the field number width.
	 *
	 * @return the field number width
	 */
	public int getFieldNumberWidth() {

		return fieldNumberWidth;
	}

	/**
	 * Gets the field required width.
	 *
	 * @return the field required width
	 */
	public int getFieldRequiredWidth() {

		return fieldRequiredWidth;
	}

	/**
	 * Gets the field type width.
	 *
	 * @return the field type width
	 */
	public int getFieldTypeWidth() {

		return fieldTypeWidth;
	}

	/**
	 * Sets the field data type width.
	 *
	 * @param fieldDataTypeWidth the new field data type width
	 */
	public void setFieldDataTypeWidth(final int fieldDataTypeWidth) {

		this.fieldDataTypeWidth = fieldDataTypeWidth;
	}

	/**
	 * Sets the field name width.
	 *
	 * @param fieldNameWidth the new field name width
	 */
	public void setFieldNameWidth(final int fieldNameWidth) {

		this.fieldNameWidth = fieldNameWidth;
	}

	/**
	 * Sets the field number width.
	 *
	 * @param fieldNumberWidth the new field number width
	 */
	public void setFieldNumberWidth(final int fieldNumberWidth) {

		this.fieldNumberWidth = fieldNumberWidth;
	}

	/**
	 * Sets the field required width.
	 *
	 * @param fieldRequiredWidth the new field required width
	 */
	public void setFieldRequiredWidth(final int fieldRequiredWidth) {

		this.fieldRequiredWidth = fieldRequiredWidth;
	}

	/**
	 * Sets the field type width.
	 *
	 * @param fieldTypeWidth the new field type width
	 */
	public void setFieldTypeWidth(final int fieldTypeWidth) {

		this.fieldTypeWidth = fieldTypeWidth;
	}
}
