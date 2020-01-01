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
 * The Class TimeTextField.
 */
public class TimeTextField extends AbstractDateTextField {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");


	/**
	 * Instantiates a new time text field.
	 *
	 * @param backgroundColor the background color
	 */
	public TimeTextField(Color backgroundColor) {

		super(backgroundColor);
		
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.FULL, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(simpleDateFormat);
		dateFormats.add(simpleDateFormat2);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractDateTextField#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if (value instanceof Double)
		{
			Date date = DateUtil.getJavaDate((Double)value);
			if(date!=null)
			switch (timeFormat) {
				case 1:
					jTextField.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
					break;
				case 2:
					jTextField.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date));
					break;
				case 3:
					jTextField.setText(DateFormat.getTimeInstance(DateFormat.LONG).format(date));
					break;
				case 4:
					jTextField.setText(simpleDateFormat.format(date));
					break;
				default:
					jTextField.setText(simpleDateFormat2.format(date));
					break;
			}
			this.value  = (Double)value;
		}
		else
			this.value  = null;
	}

}
