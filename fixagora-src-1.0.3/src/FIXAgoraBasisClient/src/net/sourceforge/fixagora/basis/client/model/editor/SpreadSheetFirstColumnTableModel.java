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

import javax.swing.table.AbstractTableModel;

/**
 * The Class SpreadSheetFirstColumnTableModel.
 */
public class SpreadSheetFirstColumnTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int rowCount = 50;

	/**
	 * Instantiates a new spread sheet first column table model.
	 */
	public SpreadSheetFirstColumnTableModel() {

		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 1 ;
	}
	
	
	/**
	 * Increase row count.
	 */
	public void increaseRowCount()
	{
		rowCount = rowCount +1;
		fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

			return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return rowCount;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

			return Integer.toString(rowIndex + 1);
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	
	/**
	 * Sets the row count.
	 *
	 * @param rowCount the new row count
	 */
	public void setRowCount(int rowCount) {
	
		this.rowCount = rowCount;
	}



	
}
