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
package net.sourceforge.fixagora.basis.client.model.editor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventDate;

/**
 * The Class ComplexEventDateTableModel.
 */
public class ComplexEventDateTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private ComplexEventTableModel complexEventTableModel = null;

	private List<ComplexEventDate> complexEventDates = new ArrayList<ComplexEventDate>();
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	/**
	 * Instantiates a new complex event date table model.
	 *
	 * @param complexEventTableModel the complex event table model
	 */
	public ComplexEventDateTableModel(ComplexEventTableModel complexEventTableModel) {

		super();

		this.complexEventTableModel = complexEventTableModel;

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {
			
			case 0:
				return "Start Date";

			case 1:
				return "End Date";
				

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return complexEventDates.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		ComplexEventDate complexEventDate = complexEventDates.get(rowIndex);

		switch (columnIndex) {

			case 0:
				if(complexEventDate.getEventStartDate()!=null)
					return simpleDateFormat.format(complexEventDate.getEventStartDate());
				return "";

			case 1:
				if(complexEventDate.getEventEndDate()!=null)
					return simpleDateFormat.format(complexEventDate.getEventEndDate());
				return "";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}


	/**
	 * Update complex event dates.
	 *
	 * @param list the list
	 */
	public void updateComplexEventDates(List<ComplexEventDate> list) {

		this.complexEventDates = list;
				
		fireTableDataChanged();
	}

	/**
	 * Adds the security event.
	 *
	 * @param complexEventDate the complex event date
	 */
	public void addSecurityEvent(ComplexEventDate complexEventDate) {

		complexEventDates.add(complexEventDate);
		
		complexEventTableModel.modified();
		
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param complexEventDate the complex event date
	 */
	public void remove(ComplexEventDate complexEventDate) {

		complexEventDates.remove(complexEventDate);
		
		complexEventTableModel.modified();
		
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the complex event date
	 */
	public ComplexEventDate get(int index) {

		return complexEventDates.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		complexEventTableModel.modified();
	}

}
