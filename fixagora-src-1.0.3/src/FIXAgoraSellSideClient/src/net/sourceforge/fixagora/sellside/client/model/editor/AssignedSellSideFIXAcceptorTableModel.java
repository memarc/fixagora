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
package net.sourceforge.fixagora.sellside.client.model.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditor;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideAcceptor;

/**
 * The Class AssignedSellSideFIXAcceptorTableModel.
 */
public class AssignedSellSideFIXAcceptorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private SellSideBookEditor sellSideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedSellSideAcceptor> assignedSellSideFIXAcceptors = null;

	private boolean modified = false;

	/**
	 * Instantiates a new assigned sell side fix acceptor table model.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public AssignedSellSideFIXAcceptorTableModel(final SellSideBookEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		updateAssignedFIXAcceptors(this.sellSideBookEditor.getSellSideBook().getAssignedSellSideFIXAcceptors());

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
				return "FIXAcceptor";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return assignedSellSideFIXAcceptors.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedSellSideAcceptor assignedSellSideFIXAcceptor = assignedSellSideFIXAcceptors.get(rowIndex);


		return assignedSellSideFIXAcceptor.getAcceptor().getName();


	
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

		boolean clean = !modified && assignedSellSideFIXAcceptors.containsAll(sellSideBookEditor.getSellSideBook().getAssignedSellSideFIXAcceptors())
				&& sellSideBookEditor.getSellSideBook().getAssignedSellSideFIXAcceptors().containsAll(assignedSellSideFIXAcceptors);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		sellSideBookEditor.getSellSideBook().setAssignedSellSideFIXAcceptors(assignedSellSideFIXAcceptors);
	}

	/**
	 * Update assigned fix acceptors.
	 *
	 * @param list the list
	 */
	public void updateAssignedFIXAcceptors(List<AssignedSellSideAcceptor> list) {

		this.assignedSellSideFIXAcceptors = new ArrayList<AssignedSellSideAcceptor>();

		if (list != null)
			this.assignedSellSideFIXAcceptors.addAll(list);

		modified = false;

		checkDoubleEntries();
		
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned sell side book security.
	 *
	 * @param assignedSellSideFIXAcceptor the assigned sell side fix acceptor
	 */
	public void addAssignedSellSideBookSecurity(AssignedSellSideAcceptor assignedSellSideFIXAcceptor) {

		assignedSellSideFIXAcceptors.add(assignedSellSideFIXAcceptor);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param assignedSellSideFIXAcceptor the assigned sell side fix acceptor
	 */
	public void remove(AssignedSellSideAcceptor assignedSellSideFIXAcceptor) {

		assignedSellSideFIXAcceptors.remove(assignedSellSideFIXAcceptor);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned sell side acceptor
	 */
	public AssignedSellSideAcceptor get(int index) {

		return assignedSellSideFIXAcceptors.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}

	private void checkDoubleEntries() {

		List<AssignedSellSideAcceptor> assignedSellSideFIXAcceptors2 = new ArrayList<AssignedSellSideAcceptor>();
		assignedSellSideFIXAcceptors2.addAll(this.assignedSellSideFIXAcceptors);
		for (AssignedSellSideAcceptor assignedSellSideBookSecurity : assignedSellSideFIXAcceptors2)
		{
			List<AssignedSellSideAcceptor> assignedSellSideFIXAcceptors3 = new ArrayList<AssignedSellSideAcceptor>();
			assignedSellSideFIXAcceptors3.addAll(this.assignedSellSideFIXAcceptors);
			for (AssignedSellSideAcceptor assignedSellSideBookSecurity2 : assignedSellSideFIXAcceptors3)
				if (assignedSellSideBookSecurity != assignedSellSideBookSecurity2
						&& assignedSellSideBookSecurity.getAcceptor().getId() == assignedSellSideBookSecurity2.getAcceptor().getId())
					assignedSellSideFIXAcceptors.remove(assignedSellSideBookSecurity);
		}
		
		Collections.sort(assignedSellSideFIXAcceptors);

	}

}
