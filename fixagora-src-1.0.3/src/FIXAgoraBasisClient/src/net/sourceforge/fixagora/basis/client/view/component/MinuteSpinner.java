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

import org.apache.poi.ss.usermodel.DateUtil;


/**
 * The Class MinuteSpinner.
 */
public class MinuteSpinner extends AbstractDateSpinner {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new minute spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public MinuteSpinner(final CellEditor cellEditor, Color backgroundColor) {

		super(cellEditor, new TimeTextField(backgroundColor));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#increment()
	 */
	@Override
	protected void increment() {

		Calendar calendar = getCalendarValue();
		calendar.add(Calendar.MINUTE, 1);
		setValue(DateUtil.getExcelDate(calendar.getTime()));
		((AbstractDateTextField)jTextField).postActionEvent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#decrement()
	 */
	@Override
	protected void decrement() {

		Calendar calendar = getCalendarValue();
		calendar.add(Calendar.MINUTE, -1);
		setValue(DateUtil.getExcelDate(calendar.getTime()));
		((AbstractDateTextField)jTextField).postActionEvent();
	}

}
