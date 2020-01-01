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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FIXField.
 */
public class FIXField extends AbstractFIXElement {

	/**
	 * The Enum Type.
	 */
	public enum Type {

		/** The amt. */
		AMT, 
 /** The boolean. */
		BOOLEAN, 
 /** The char. */
		CHAR, 
 /** The country. */
		COUNTRY, 
 /** The currency. */
		CURRENCY, 
 /** The data. */
		DATA, 
 /** The dayofmonth. */
		DAYOFMONTH, 
 /** The exchange. */
		EXCHANGE, 
 /** The float. */
		FLOAT, 
 /** The int. */
		INT, 
 /** The language. */
		LANGUAGE, 
 /** The length. */
		LENGTH, 
 /** The localmktdate. */
		LOCALMKTDATE, 
 /** The month year. */
		MONTH_YEAR, 
 /** The monthyear. */
		MONTHYEAR, 
 /** The multiplecharvalue. */
		MULTIPLECHARVALUE, 
 /** The multiplestringvalue. */
		MULTIPLESTRINGVALUE, 
 /** The multiplevaluestring. */
		MULTIPLEVALUESTRING, 
 /** The numingroup. */
		NUMINGROUP, 
 /** The percentage. */
		PERCENTAGE, 
 /** The price. */
		PRICE, 
 /** The priceoffset. */
		PRICEOFFSET, 
 /** The qty. */
		QTY, 
 /** The seqnum. */
		SEQNUM, 
 /** The string. */
		STRING, 
 /** The time. */
		TIME, 
 /** The tztimeonly. */
		TZTIMEONLY, 
 /** The tztimestamp. */
		TZTIMESTAMP, 
 /** The unknown. */
		UNKNOWN, 
 /** The utcdate. */
		UTCDATE, 
 /** The utcdateonly. */
		UTCDATEONLY, 
 /** The utctimeonly. */
		UTCTIMEONLY, 
 /** The utctimestamp. */
		UTCTIMESTAMP, 
 /** The xmldata. */
		XMLDATA, 
 /** The date. */
 DATE
	};

	private Type type = null;

	private List<FIXFieldValue> valueEnum = new ArrayList<FIXFieldValue>();

	/**
	 * Instantiates a new fIX field.
	 *
	 * @param name the name
	 * @param number the number
	 * @param fieldType the field type
	 * @param type the type
	 */
	public FIXField(final String name, final int number, final FieldType fieldType, final Type type) {

		super(name, number, fieldType, new ArrayList<AbstractFIXElement>());

		this.type = type;
	}

	/**
	 * Instantiates a new fIX field.
	 *
	 * @param name the name
	 * @param number the number
	 * @param fieldType the field type
	 * @param type the type
	 * @param valueEnum the value enum
	 */
	public FIXField(final String name, final int number, final FieldType fieldType, final Type type, final List<FIXFieldValue> valueEnum) {

		super(name, number, fieldType, new ArrayList<AbstractFIXElement>());

		this.type = type;
		this.valueEnum = valueEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fixpusher.model.message.AbstractFIXElement#getDepth()
	 */
	@Override
	public int getDepth() {

		return 0;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Type getType() {

		return type;
	}

	/**
	 * Gets the value enum.
	 *
	 * @return the value enum
	 */
	public List<FIXFieldValue> getValueEnum() {

		return valueEnum;
	}

}
