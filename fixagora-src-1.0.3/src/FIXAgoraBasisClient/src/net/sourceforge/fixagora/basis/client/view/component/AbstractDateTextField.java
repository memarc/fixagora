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
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;

import org.apache.poi.ss.usermodel.DateUtil;


/**
 * The Class AbstractDateTextField.
 */
public abstract class AbstractDateTextField extends AbstractTextField {
	
	private static final long serialVersionUID = 1L;

	/** The date format. */
	protected int dateFormat = 0;

	/** The value. */
	protected Double value = null;

	/** The time format. */
	protected int timeFormat = 0;

	private BankCalendar bankCalendar = null;

	private Calendar minValue;
		
	/**
	 * Instantiates a new abstract date text field.
	 *
	 * @param backgroundColor the background color
	 */
	public AbstractDateTextField(Color backgroundColor) {

		super(backgroundColor);
		
		jTextField.setHorizontalAlignment(JTextField.RIGHT);
		
		jTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!isValidDate())
					setBorder(new MatteBorder(new Insets(1, 1, 1, 1), Color.RED));
				else
					setBorder(new MatteBorder(new Insets(1, 1, 1, 1), AbstractDateTextField.this.highlightColor));
			}
		});
	}
	
	/**
	 * Checks if is valid date.
	 *
	 * @return true, if is valid date
	 */
	public boolean isValidDate() {

		return parseDate()!=null;
	}
	
	private Double parseDate()
	{
		for (DateFormat dateFormat: dateFormats) {
			try {
				Date date =  dateFormat.parse(jTextField.getText().trim());
				if(bankCalendar!=null&&!bankCalendar.getCalendar().isBusinessDay(new org.jquantlib.time.Date(date)))
					return null;
				if(minValue!=null)
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					if(calendar.get(Calendar.YEAR)<minValue.get(Calendar.YEAR)||(calendar.get(Calendar.YEAR)==minValue.get(Calendar.YEAR)&&calendar.get(Calendar.DAY_OF_YEAR)<minValue.get(Calendar.DAY_OF_YEAR)))
						return null;
				}
				Double numeric = DateUtil.getExcelDate(date);
				return numeric;
			}
			catch (Exception e) {
			}
		}
		return null;
	}
	
	/**
	 * Sets the date format.
	 *
	 * @param dateFormat the new date format
	 */
	public void setDateFormat(int dateFormat) {

		this.dateFormat = dateFormat;
		if(value!=null)
			setValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#getValue()
	 */
	public Double getValue() {

		if(!isValidDate())
			return value;
		return parseDate();
	}
	
	/**
	 * Gets the calendar value.
	 *
	 * @return the calendar value
	 */
	public Calendar getCalendarValue() {

		Calendar calendar = Calendar.getInstance();
		Double value = getValue();
		if(value!=null)
		{
			calendar.setTime(DateUtil.getJavaDate(value));
		}
		else
		{
			calendar=null;
		}
		return calendar;
	}
	
	/**
	 * Sets the time format.
	 *
	 * @param timeFormat the new time format
	 */
	public void setTimeFormat(int timeFormat) {

		this.timeFormat  = timeFormat;
		if(value!=null)
			setValue(value);		
	}
	
	/**
	 * Sets the bank calendar.
	 *
	 * @param bankCalendar the new bank calendar
	 */
	public void setBankCalendar(BankCalendar bankCalendar) {

		this.bankCalendar  = bankCalendar;
		
	}
	
	/**
	 * Sets the min value.
	 *
	 * @param instance the new min value
	 */
	public void setMinValue(Calendar instance) {

		this.minValue = instance;
		
	}
	
	/**
	 * Gets the min value.
	 *
	 * @return the min value
	 */
	public Calendar getMinValue() {
	
		return minValue;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		// TODO Auto-generated method stub
		
	}

}
