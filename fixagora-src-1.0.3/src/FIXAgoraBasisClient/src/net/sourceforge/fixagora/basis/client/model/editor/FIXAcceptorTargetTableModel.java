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
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FIXAcceptorEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptorTarget;

/**
 * The Class FIXAcceptorTargetTableModel.
 */
public class FIXAcceptorTargetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FIXAcceptorEditor fixAcceptorEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<FIXAcceptorTarget> fixAcceptorTargets = null;

	private boolean modified = false;

	private FIXAcceptor fixAcceptor = null;

	/**
	 * Instantiates a new fIX acceptor target table model.
	 *
	 * @param fixAcceptorEditor the fix acceptor editor
	 */
	public FIXAcceptorTargetTableModel(final FIXAcceptorEditor fixAcceptorEditor) {

		super();

		this.fixAcceptorEditor = fixAcceptorEditor;
		
		this.fixAcceptor  = fixAcceptorEditor.getFixAcceptor();

		updateFIXAcceptorTargets(this.fixAcceptorEditor.getFixAcceptor());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 3;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Status";

			case 1:
				return "Target Comp ID";
				
			case 2:
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

		return fixAcceptorTargets.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		FIXAcceptorTarget fixAcceptorTarget = fixAcceptorTargets.get(rowIndex);

		switch (columnIndex) {

			case 0:
				
				if(fixAcceptor.getOpenSessions().contains(fixAcceptorTarget.getTargetCompID()))
					return "Online";
				return "Offline";	

			case 1:
				return fixAcceptorTarget.getTargetCompID();
				
			case 2:
				return fixAcceptorTarget.getCounterparty().getName();
				
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

		if(minWidth>0&&minWidth>this.minWidth)
			this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		table.getColumnModel().getColumn(2).setPreferredWidth(minWidth);
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

		return fixAcceptorEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && fixAcceptorTargets.containsAll(fixAcceptorEditor.getFixAcceptor().getAcceptorTargets())
				&& fixAcceptorEditor.getFixAcceptor().getAcceptorTargets().containsAll(fixAcceptorTargets);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		fixAcceptorEditor.getFixAcceptor().setAcceptorTargets(fixAcceptorTargets);
	}

	/**
	 * Update fix acceptor targets.
	 *
	 * @param fixAcceptor the fix acceptor
	 */
	public void updateFIXAcceptorTargets(FIXAcceptor fixAcceptor) {

		this.fixAcceptorTargets = new ArrayList<FIXAcceptorTarget>();

		this.fixAcceptor = fixAcceptor;
		
		if (fixAcceptor != null)
			this.fixAcceptorTargets.addAll(fixAcceptor.getAcceptorTargets());

		modified = false;

		fireTableDataChanged();
	}

	/**
	 * Adds the fix acceptor target.
	 *
	 * @param fixAcceptorTarget the fix acceptor target
	 */
	public void addFIXAcceptorTarget(FIXAcceptorTarget fixAcceptorTarget) {

		fixAcceptorTargets.add(fixAcceptorTarget);
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param fixAcceptorTarget the fix acceptor target
	 */
	public void remove(FIXAcceptorTarget fixAcceptorTarget) {

		fixAcceptorTargets.remove(fixAcceptorTarget);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the fIX acceptor target
	 */
	public FIXAcceptorTarget get(int index) {

		return fixAcceptorTargets.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}


}
