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

import java.util.Calendar;

import javax.swing.CellEditor;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

/**
 * The Class AbstractDateSpinner.
 */
public abstract class AbstractDateSpinner extends AbstractSpinner {

	/**
	 * Instantiates a new abstract date spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param abstractDateTextField the abstract date text field
	 */
	public AbstractDateSpinner(final CellEditor cellEditor, AbstractDateTextField abstractDateTextField) {

		super(cellEditor, blueRightIcon, blueLeftIcon, abstractDateTextField);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Sets the date format.
	 *
	 * @param dateFormat the new date format
	 */
	public void setDateFormat(int dateFormat) {

		((AbstractDateTextField) jTextField).setDateFormat(dateFormat);
	}

	/**
	 * Sets the time format.
	 *
	 * @param dateFormat the new time format
	 */
	public void setTimeFormat(int dateFormat) {

		((AbstractDateTextField) jTextField).setTimeFormat(dateFormat);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	public void setValue(final Object value) {

		jTextField.setValue(value);

	}

	/**
	 * Gets the calendar value.
	 *
	 * @return the calendar value
	 */
	public Calendar getCalendarValue() {

		return ((AbstractDateTextField) jTextField).getCalendarValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Double getValue() {

		return ((AbstractDateTextField) jTextField).getValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		setDateFormat(spreadSheetCellFormat.getDateFormat());
		setTimeFormat(spreadSheetCellFormat.getTimeFormat());
		jTextField.setSpreadSheetCellFormat(spreadSheetCellFormat);
	}

}
