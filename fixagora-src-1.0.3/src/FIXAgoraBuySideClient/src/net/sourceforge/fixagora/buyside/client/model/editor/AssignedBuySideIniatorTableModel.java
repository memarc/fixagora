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
package net.sourceforge.fixagora.buyside.client.model.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideInitiator;

/**
 * The Class AssignedBuySideIniatorTableModel.
 */
public class AssignedBuySideIniatorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private BuySideBookEditor buySideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedBuySideInitiator> assignedBuySideFIXInitiators = null;

	private boolean modified = false;

	/**
	 * Instantiates a new assigned buy side iniator table model.
	 *
	 * @param buySideBookEditor the buy side book editor
	 */
	public AssignedBuySideIniatorTableModel(final BuySideBookEditor buySideBookEditor) {

		super();

		this.buySideBookEditor = buySideBookEditor;

		updateAssignedFIXInitiators(this.buySideBookEditor.getBuySideBook().getAssignedBuySideFIXInitiators());

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
				return "Initiator";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return assignedBuySideFIXInitiators.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedBuySideInitiator assignedBuySideFIXInitiator = assignedBuySideFIXInitiators.get(rowIndex);


		return assignedBuySideFIXInitiator.getInitiator().getName();


	
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

		return buySideBookEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && assignedBuySideFIXInitiators.containsAll(buySideBookEditor.getBuySideBook().getAssignedBuySideFIXInitiators())
				&& buySideBookEditor.getBuySideBook().getAssignedBuySideFIXInitiators().containsAll(assignedBuySideFIXInitiators);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		buySideBookEditor.getBuySideBook().setAssignedBuySideFIXInitiators(assignedBuySideFIXInitiators);
	}

	/**
	 * Update assigned fix initiators.
	 *
	 * @param list the list
	 */
	public void updateAssignedFIXInitiators(List<AssignedBuySideInitiator> list) {

		this.assignedBuySideFIXInitiators = new ArrayList<AssignedBuySideInitiator>();

		if (list != null)
			this.assignedBuySideFIXInitiators.addAll(list);

		modified = false;

		checkDoubleEntries();
		
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned buy side book security.
	 *
	 * @param assignedBuySideFIXInitiator the assigned buy side fix initiator
	 */
	public void addAssignedBuySideBookSecurity(AssignedBuySideInitiator assignedBuySideFIXInitiator) {

		assignedBuySideFIXInitiators.add(assignedBuySideFIXInitiator);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param assignedBuySideFIXInitiator the assigned buy side fix initiator
	 */
	public void remove(AssignedBuySideInitiator assignedBuySideFIXInitiator) {

		assignedBuySideFIXInitiators.remove(assignedBuySideFIXInitiator);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned buy side initiator
	 */
	public AssignedBuySideInitiator get(int index) {

		return assignedBuySideFIXInitiators.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}

	private void checkDoubleEntries() {

		List<AssignedBuySideInitiator> assignedBuySideFIXInitiators2 = new ArrayList<AssignedBuySideInitiator>();
		assignedBuySideFIXInitiators2.addAll(this.assignedBuySideFIXInitiators);
		for (AssignedBuySideInitiator assignedBuySideBookSecurity : assignedBuySideFIXInitiators2)
		{
			List<AssignedBuySideInitiator> assignedBuySideFIXInitiators3 = new ArrayList<AssignedBuySideInitiator>();
			assignedBuySideFIXInitiators3.addAll(this.assignedBuySideFIXInitiators);
			for (AssignedBuySideInitiator assignedBuySideBookSecurity2 : assignedBuySideFIXInitiators3)
				if (assignedBuySideBookSecurity != assignedBuySideBookSecurity2
						&& assignedBuySideBookSecurity.getInitiator().getId() == assignedBuySideBookSecurity2.getInitiator().getId())
					assignedBuySideFIXInitiators.remove(assignedBuySideBookSecurity);
		}
		
		Collections.sort(assignedBuySideFIXInitiators);

	}

}
