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

import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventTime;

/**
 * The Class ComplexEventTimeTableModel.
 */
public class ComplexEventTimeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private ComplexEventTableModel complexEventTableModel = null;

	private List<ComplexEventTime> complexEventTimes = new ArrayList<ComplexEventTime>();
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Instantiates a new complex event time table model.
	 *
	 * @param complexEventTableModel the complex event table model
	 */
	public ComplexEventTimeTableModel(ComplexEventTableModel complexEventTableModel) {

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
				return "Start Time";

			case 1:
				return "End Time";
				

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return complexEventTimes.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		ComplexEventTime complexEventTime = complexEventTimes.get(rowIndex);

		switch (columnIndex) {

			case 0:
				if(complexEventTime.getEventStartTime()!=null)
					return simpleDateFormat.format(complexEventTime.getEventStartTime());
				return "";

			case 1:
				if(complexEventTime.getEventEndTime()!=null)
					return simpleDateFormat.format(complexEventTime.getEventEndTime());
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
	 * Update complex event times.
	 *
	 * @param list the list
	 */
	public void updateComplexEventTimes(List<ComplexEventTime> list) {

		this.complexEventTimes = list;
				
		fireTableDataChanged();
	}

	/**
	 * Adds the security event.
	 *
	 * @param complexEventTime the complex event time
	 */
	public void addSecurityEvent(ComplexEventTime complexEventTime) {

		complexEventTimes.add(complexEventTime);
		
		complexEventTableModel.modified();
		
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param complexEventTime the complex event time
	 */
	public void remove(ComplexEventTime complexEventTime) {

		complexEventTimes.remove(complexEventTime);
		
		complexEventTableModel.modified();
		
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the complex event time
	 */
	public ComplexEventTime get(int index) {

		return complexEventTimes.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		complexEventTableModel.modified();
	}

}
