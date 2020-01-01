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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FIXInitiatorEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AssignedInitiatorSecurity;

/**
 * The Class AssignedSecurityTableModel.
 */
public class AssignedSecurityTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FIXInitiatorEditor fixInitiatorEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedInitiatorSecurity> assignedInitiatorSecurities = null;

	private boolean modified = false;

	/**
	 * Instantiates a new assigned security table model.
	 *
	 * @param fixInitiatorEditor the fix initiator editor
	 */
	public AssignedSecurityTableModel(final FIXInitiatorEditor fixInitiatorEditor) {

		super();

		this.fixInitiatorEditor = fixInitiatorEditor;

		updateAssignedInitiatorSecurities(this.fixInitiatorEditor.getFixInitiator().getAssignedInitiatorSecurities());

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
				return "Security";

			case 1:
				return "Counterparty";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return assignedInitiatorSecurities.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedInitiatorSecurity assignedInitiatorSecurity = assignedInitiatorSecurities.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return assignedInitiatorSecurity.getSecurity().getName();

			case 1:
				if (assignedInitiatorSecurity.getCounterparty() != null)
					return assignedInitiatorSecurity.getCounterparty().getName();
				return "";

			default:
				return "";
		}
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

		table.getColumnModel().getColumn(1).setPreferredWidth(minWidth);
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

		return fixInitiatorEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && assignedInitiatorSecurities.containsAll(fixInitiatorEditor.getFixInitiator().getAssignedInitiatorSecurities())
				&& fixInitiatorEditor.getFixInitiator().getAssignedInitiatorSecurities().containsAll(assignedInitiatorSecurities);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		fixInitiatorEditor.getFixInitiator().setAssignedInitiatorSecurities(assignedInitiatorSecurities);
	}

	/**
	 * Update assigned initiator securities.
	 *
	 * @param list the list
	 */
	public void updateAssignedInitiatorSecurities(List<AssignedInitiatorSecurity> list) {

		this.assignedInitiatorSecurities = new ArrayList<AssignedInitiatorSecurity>();

		if (list != null)
			this.assignedInitiatorSecurities.addAll(list);

		modified = false;

		checkDoubleEntries();
		
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned initiator security.
	 *
	 * @param assignedInitiatorSecurity the assigned initiator security
	 */
	public void addAssignedInitiatorSecurity(AssignedInitiatorSecurity assignedInitiatorSecurity) {

		assignedInitiatorSecurities.add(assignedInitiatorSecurity);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param assignedInitiatorSecurity the assigned initiator security
	 */
	public void remove(AssignedInitiatorSecurity assignedInitiatorSecurity) {

		assignedInitiatorSecurities.remove(assignedInitiatorSecurity);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned initiator security
	 */
	public AssignedInitiatorSecurity get(int index) {

		return assignedInitiatorSecurities.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}

	private void checkDoubleEntries() {

		List<AssignedInitiatorSecurity> assignedInitiatorSecurities2 = new ArrayList<AssignedInitiatorSecurity>();
		assignedInitiatorSecurities2.addAll(this.assignedInitiatorSecurities);
		for (AssignedInitiatorSecurity assignedInitiatorSecurity : assignedInitiatorSecurities2)
		{
			List<AssignedInitiatorSecurity> assignedInitiatorSecurities3 = new ArrayList<AssignedInitiatorSecurity>();
			assignedInitiatorSecurities3.addAll(this.assignedInitiatorSecurities);
			for (AssignedInitiatorSecurity assignedInitiatorSecurity2 : assignedInitiatorSecurities3)
				if (assignedInitiatorSecurity != assignedInitiatorSecurity2
						&& assignedInitiatorSecurity.getSecurity().getId() == assignedInitiatorSecurity2.getSecurity().getId()
						&& ((assignedInitiatorSecurity.getCounterparty() == null && assignedInitiatorSecurity2.getCounterparty() == null) || (assignedInitiatorSecurity
								.getCounterparty() != null && assignedInitiatorSecurity2.getCounterparty() != null && assignedInitiatorSecurity
								.getCounterparty().getId() == assignedInitiatorSecurity2.getCounterparty().getId())))
					assignedInitiatorSecurities.remove(assignedInitiatorSecurity);
		}
		
		Collections.sort(assignedInitiatorSecurities);

	}

}
