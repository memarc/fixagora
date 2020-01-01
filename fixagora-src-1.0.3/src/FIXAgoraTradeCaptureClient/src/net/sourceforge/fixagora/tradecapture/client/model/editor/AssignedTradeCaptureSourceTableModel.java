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
package net.sourceforge.fixagora.tradecapture.client.model.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.tradecapture.client.view.editor.TradeCaptureEditor;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureSource;

/**
 * The Class AssignedTradeCaptureSourceTableModel.
 */
public class AssignedTradeCaptureSourceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private TradeCaptureEditor sellSideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedTradeCaptureSource> assignedTradeCaptureSources = null;

	private boolean modified = false;

	/**
	 * Instantiates a new assigned trade capture source table model.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public AssignedTradeCaptureSourceTableModel(final TradeCaptureEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		updateAssignedTradeCaptureSources(this.sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureSources());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Trade Capture Source";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return assignedTradeCaptureSources.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedTradeCaptureSource assignedTradeCaptureSource = assignedTradeCaptureSources.get(rowIndex);


		return assignedTradeCaptureSource.getBusinessComponent().getName();


	
	}

	/**
	 * Sets the min width.
	 *
	 * @param minWidth the new min width
	 */
	public void setMinWidth(final int minWidth) {

		if(minWidth>0)
			this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		table.getColumnModel().getColumn(0).setPreferredWidth(minWidth);
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final JTable table) {

		this.table = table;
		setTableWidth();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	/**
	 * Gets the abstract business object editor.
	 *
	 * @return the abstract business object editor
	 */
	public AbstractBusinessObjectEditor getAbstractBusinessObjectEditor() {

		return sellSideBookEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && assignedTradeCaptureSources.containsAll(sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureSources())
				&& sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureSources().containsAll(assignedTradeCaptureSources);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		sellSideBookEditor.getTradeCapture().setAssignedTradeCaptureSources(assignedTradeCaptureSources);
	}

	/**
	 * Update assigned trade capture sources.
	 *
	 * @param list the list
	 */
	public void updateAssignedTradeCaptureSources(List<AssignedTradeCaptureSource> list) {

		this.assignedTradeCaptureSources = new ArrayList<AssignedTradeCaptureSource>();

		if (list != null)
			this.assignedTradeCaptureSources.addAll(list);

		modified = false;

		checkDoubleEntries();
		
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned trade capture source.
	 *
	 * @param assignedTradeCaptureSource the assigned trade capture source
	 */
	public void addAssignedTradeCaptureSource(AssignedTradeCaptureSource assignedTradeCaptureSource) {

		assignedTradeCaptureSources.add(assignedTradeCaptureSource);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param assignedTradeCaptureSource the assigned trade capture source
	 */
	public void remove(AssignedTradeCaptureSource assignedTradeCaptureSource) {

		assignedTradeCaptureSources.remove(assignedTradeCaptureSource);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned trade capture source
	 */
	public AssignedTradeCaptureSource get(int index) {

		return assignedTradeCaptureSources.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}

	private void checkDoubleEntries() {

		List<AssignedTradeCaptureSource> assignedTradeCaptureSources2 = new ArrayList<AssignedTradeCaptureSource>();
		assignedTradeCaptureSources2.addAll(this.assignedTradeCaptureSources);
		for (AssignedTradeCaptureSource assignedSellSideBookSecurity : assignedTradeCaptureSources2)
		{
			List<AssignedTradeCaptureSource> assignedTradeCaptureSources3 = new ArrayList<AssignedTradeCaptureSource>();
			assignedTradeCaptureSources3.addAll(this.assignedTradeCaptureSources);
			for (AssignedTradeCaptureSource assignedSellSideBookSecurity2 : assignedTradeCaptureSources3)
				if (assignedSellSideBookSecurity != assignedSellSideBookSecurity2
						&& assignedSellSideBookSecurity.getBusinessComponent().getId() == assignedSellSideBookSecurity2.getBusinessComponent().getId())
					assignedTradeCaptureSources.remove(assignedSellSideBookSecurity);
		}
		
		Collections.sort(assignedTradeCaptureSources);

	}

}
