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
package net.sourceforge.fixagora.basis.client.view.component;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DateUtil;

/**
 * The Class DateTextField.
 */
public class DateTextField extends AbstractDateTextField {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Instantiates a new date text field.
	 *
	 * @param backgroundColor the background color
	 */
	public DateTextField(Color backgroundColor) {

		super(backgroundColor);
		
		dateFormats.add(DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(simpleDateFormat);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractDateTextField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		if (value instanceof Double)
		{
			value = DateUtil.getJavaDate((Double)value);
		}
		if (value instanceof Date)
		{
			Date date = (Date) value;
			switch (dateFormat) {
				case 1:
					jTextField.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
					break;
				case 2:
					jTextField.setText(DateFormat.getDateInstance(DateFormat.LONG).format(date));
					break;
				case 3:
					jTextField.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
					break;
				default:
					jTextField.setText(simpleDateFormat.format(date));
					break;
			}
			this.value  = DateUtil.getExcelDate(date);
		}
		else
			this.value  = null;
		
	}


}
