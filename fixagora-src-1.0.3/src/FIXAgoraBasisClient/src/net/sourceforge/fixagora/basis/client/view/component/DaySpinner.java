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
import java.util.Calendar;

import javax.swing.CellEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;

import org.apache.poi.ss.usermodel.DateUtil;
import org.jquantlib.time.Date;


/**
 * The Class DaySpinner.
 */
public class DaySpinner extends AbstractDateSpinner {

	private static final long serialVersionUID = 1L;
	private BankCalendar bankCalendar = null;

	/**
	 * Instantiates a new day spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public DaySpinner(final CellEditor cellEditor, Color backgroundColor) {

		super(cellEditor, new DateTextField(backgroundColor));
		jTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
			
				buttonCheck();
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
			
				buttonCheck();
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
			
				buttonCheck();
				
			}
		});
	}
	

	private void buttonCheck() {
		Calendar calendar = ((AbstractDateTextField)jTextField).getMinValue();
		Calendar calendar2 = getCalendarValue();
		if(calendar2==null||!((AbstractDateTextField)jTextField).isValidDate())
		{
			spinner1MinusButton.setEnabled(false);
			spinner1PlusButton.setEnabled(false);
		}
		else if(calendar==null)
		{
			spinner1MinusButton.setEnabled(true);
			spinner1PlusButton.setEnabled(true);
		}
		else
		{
			spinner1PlusButton.setEnabled(true);
			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTime(calendar2.getTime());
			calendar3.add(Calendar.DAY_OF_YEAR, -1);
			if(calendar3.get(Calendar.YEAR)<calendar.get(Calendar.YEAR)||calendar3.get(Calendar.DAY_OF_YEAR)<calendar.get(Calendar.DAY_OF_YEAR))
				spinner1MinusButton.setEnabled(false);
			else
				spinner1MinusButton.setEnabled(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#increment()
	 */
	@Override
	protected void increment() {

		Calendar calendar = getCalendarValue();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		if(bankCalendar!=null)
			while(!bankCalendar.getCalendar().isBusinessDay(new Date(calendar.getTime())))
			  calendar.add(Calendar.DAY_OF_YEAR, 1);
		setValue(DateUtil.getExcelDate(calendar.getTime()));
		((AbstractDateTextField)jTextField).postActionEvent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#decrement()
	 */
	@Override
	protected void decrement() {

		Calendar calendar = getCalendarValue();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		if(bankCalendar!=null)
			while(!bankCalendar.getCalendar().isBusinessDay(new Date(calendar.getTime())))
			  calendar.add(Calendar.DAY_OF_YEAR, -1);
		setValue(DateUtil.getExcelDate(calendar.getTime()));
		((AbstractDateTextField)jTextField).postActionEvent();
	}

	/**
	 * Sets the bank calendar.
	 *
	 * @param bankCalendar the new bank calendar
	 */
	public void setBankCalendar(BankCalendar bankCalendar) {

		((AbstractDateTextField)jTextField).setBankCalendar(bankCalendar);
		this.bankCalendar  = bankCalendar;
		
	}

	/**
	 * Sets the min value.
	 *
	 * @param instance the new min value
	 */
	public void setMinValue(Calendar instance) {

		((AbstractDateTextField)jTextField).setMinValue(instance);
		
	}


	/**
	 * Checks if is valid date.
	 *
	 * @return true, if is valid date
	 */
	public boolean isValidDate() {

		return ((AbstractDateTextField)jTextField).isValidDate();
	}

}
