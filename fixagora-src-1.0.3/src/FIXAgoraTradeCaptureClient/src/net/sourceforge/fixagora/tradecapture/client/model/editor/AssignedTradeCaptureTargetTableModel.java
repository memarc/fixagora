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
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureTarget;

/**
 * The Class AssignedTradeCaptureTargetTableModel.
 */
public class AssignedTradeCaptureTargetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private TradeCaptureEditor sellSideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets = null;

	private boolean modified = false;

	/**
	 * Instantiates a new assigned trade capture target table model.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public AssignedTradeCaptureTargetTableModel(final TradeCaptureEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		updateAssignedTradeCaptureTargets(this.sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureTargets());

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
				return "Trade Capture Target";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return assignedTradeCaptureTargets.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedTradeCaptureTarget assignedTradeCaptureTarget = assignedTradeCaptureTargets.get(rowIndex);


		return assignedTradeCaptureTarget.getAbstractBusinessComponent().getName();


	
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

		boolean clean = !modified && assignedTradeCaptureTargets.containsAll(sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureTargets())
				&& sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureTargets().containsAll(assignedTradeCaptureTargets);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		sellSideBookEditor.getTradeCapture().setAssignedTradeCaptureTargets(assignedTradeCaptureTargets);
	}

	/**
	 * Update assigned trade capture targets.
	 *
	 * @param list the list
	 */
	public void updateAssignedTradeCaptureTargets(List<AssignedTradeCaptureTarget> list) {

		this.assignedTradeCaptureTargets = new ArrayList<AssignedTradeCaptureTarget>();

		if (list != null)
			this.assignedTradeCaptureTargets.addAll(list);

		modified = false;

		checkDoubleEntries();
		
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned trade capture target.
	 *
	 * @param assignedTradeCaptureTarget the assigned trade capture target
	 */
	public void addAssignedTradeCaptureTarget(AssignedTradeCaptureTarget assignedTradeCaptureTarget) {

		assignedTradeCaptureTargets.add(assignedTradeCaptureTarget);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param assignedTradeCaptureTarget the assigned trade capture target
	 */
	public void remove(AssignedTradeCaptureTarget assignedTradeCaptureTarget) {

		assignedTradeCaptureTargets.remove(assignedTradeCaptureTarget);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned trade capture target
	 */
	public AssignedTradeCaptureTarget get(int index) {

		return assignedTradeCaptureTargets.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}

	private void checkDoubleEntries() {

		List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets2 = new ArrayList<AssignedTradeCaptureTarget>();
		assignedTradeCaptureTargets2.addAll(this.assignedTradeCaptureTargets);
		for (AssignedTradeCaptureTarget assignedTradeCaptureTarget : assignedTradeCaptureTargets2)
		{
			List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets3 = new ArrayList<AssignedTradeCaptureTarget>();
			assignedTradeCaptureTargets3.addAll(this.assignedTradeCaptureTargets);
			for (AssignedTradeCaptureTarget assignedTradeCaptureTarget2 : assignedTradeCaptureTargets3)
				if (assignedTradeCaptureTarget != assignedTradeCaptureTarget2
						&& assignedTradeCaptureTarget.getAbstractBusinessComponent().getId() == assignedTradeCaptureTarget2.getAbstractBusinessComponent().getId())
					assignedTradeCaptureTargets.remove(assignedTradeCaptureTarget);
		}
		
		Collections.sort(assignedTradeCaptureTargets);

	}

}
