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
package net.sourceforge.fixagora.basis.client.view.document;

import javax.swing.text.PlainDocument;


/**
 * The Class AbstractNumberDocument.
 */
public abstract class AbstractNumberDocument extends PlainDocument{

	private static final long serialVersionUID = 1L;

	/**
	 * Sets the decimal places.
	 *
	 * @param decimalPlaces the new decimal places
	 */
	public abstract void setDecimalPlaces(int decimalPlaces);

	/**
	 * Sets the min value.
	 *
	 * @param minValue the new min value
	 */
	public abstract void setMinValue(Double minValue);

	/**
	 * Sets the thousands separator.
	 *
	 * @param thousandsSeparator the new thousands separator
	 */
	public abstract void setThousandsSeparator(boolean thousandsSeparator);

	/**
	 * Sets the tick size.
	 *
	 * @param tickSize the new tick size
	 */
	public abstract void setTickSize(double tickSize);
	
	/**
	 * Sets the leading zeros.
	 *
	 * @param leadingZeros the new leading zeros
	 */
	public abstract void setLeadingZeros(int leadingZeros);

	/**
	 * Removes the listener.
	 */
	public abstract void removeListener();

}
