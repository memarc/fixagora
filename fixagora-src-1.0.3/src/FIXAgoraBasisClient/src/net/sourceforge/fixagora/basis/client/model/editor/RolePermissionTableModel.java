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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;

/**
 * The Class RolePermissionTableModel.
 */
public class RolePermissionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private AbstractBusinessObjectEditor abstractBusinessObjectEditor = null;

	private List<FRole> roles = null;

	private int minWidth = 0;

	private JTable table = null;

	private Set<FRole> readRoles = new HashSet<FRole>();

	private Set<FRole> writeRoles = new HashSet<FRole>();

	private Set<FRole> executeRoles = new HashSet<FRole>();

	/**
	 * Instantiates a new role permission table model.
	 *
	 * @param abstractBusinessObjectEditor the abstract business object editor
	 * @param roles the roles
	 */
	public RolePermissionTableModel(final AbstractBusinessObjectEditor abstractBusinessObjectEditor, List<FRole> roles) {

		super();

		this.abstractBusinessObjectEditor = abstractBusinessObjectEditor;

		updateRoles(roles);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Role";

			case 1:
				return "Read";

			case 2:
				return "Write";

			default:
				return "Execute";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return roles.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		FRole role = roles.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return role.getName();

			case 1:
				return readRoles.contains(role);

			case 2:
				return writeRoles.contains(role);

			case 3:
				return executeRoles.contains(role);

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

		this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		table.getColumnModel().getColumn(3).setPreferredWidth(minWidth);
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

		if (abstractBusinessObjectEditor != null && !abstractBusinessObjectEditor.isEditable())
			return false;

		if (columnIndex > 0)
			return true;

		return false;

	}

	/**
	 * Gets the abstract business object editor.
	 *
	 * @return the abstract business object editor
	 */
	public AbstractBusinessObjectEditor getAbstractBusinessObjectEditor() {

		return abstractBusinessObjectEditor;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<FRole> getRoles() {

		return roles;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = readRoles.containsAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getReadRoles())
				&& abstractBusinessObjectEditor.getAbstractBusinessObject().getReadRoles().containsAll(readRoles)
				&& writeRoles.containsAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getWriteRoles())
				&& abstractBusinessObjectEditor.getAbstractBusinessObject().getWriteRoles().containsAll(writeRoles)
				&& executeRoles.containsAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getExecuteRoles())
				&& abstractBusinessObjectEditor.getAbstractBusinessObject().getExecuteRoles().containsAll(executeRoles);

		return !clean;
	}

	/**
	 * Gets the read roles.
	 *
	 * @return the read roles
	 */
	public Set<FRole> getReadRoles() {

		return readRoles;
	}

	/**
	 * Gets the write roles.
	 *
	 * @return the write roles
	 */
	public Set<FRole> getWriteRoles() {

		return writeRoles;
	}

	/**
	 * Gets the execute roles.
	 *
	 * @return the execute roles
	 */
	public Set<FRole> getExecuteRoles() {

		return executeRoles;
	}

	/**
	 * Save.
	 */
	public void save() {

		abstractBusinessObjectEditor.getAbstractBusinessObject().setReadRoles(readRoles);
		abstractBusinessObjectEditor.getAbstractBusinessObject().setWriteRoles(writeRoles);
		abstractBusinessObjectEditor.getAbstractBusinessObject().setExecuteRoles(executeRoles);

	}

	/**
	 * Checks if is all read set.
	 *
	 * @return true, if is all read set
	 */
	public boolean isAllReadSet() {

		return readRoles.containsAll(roles);
	}

	/**
	 * Checks if is all write set.
	 *
	 * @return true, if is all write set
	 */
	public boolean isAllWriteSet() {

		return writeRoles.containsAll(roles);
	}

	/**
	 * Checks if is all execute set.
	 *
	 * @return true, if is all execute set
	 */
	public boolean isAllExecuteSet() {

		return executeRoles.containsAll(roles);
	}

	/**
	 * Adds the all read.
	 */
	public void addAllRead() {

		readRoles.addAll(roles);

		fireTableCellUpdated(0, 1);
	}

	/**
	 * Adds the all write.
	 */
	public void addAllWrite() {

		writeRoles.addAll(roles);

		fireTableCellUpdated(0, 2);
	}

	/**
	 * Adds the all execute.
	 */
	public void addAllExecute() {

		executeRoles.addAll(roles);

		fireTableCellUpdated(0, 3);
	}

	/**
	 * Removes the all read.
	 */
	public void removeAllRead() {

		readRoles.clear();

		fireTableCellUpdated(0, 1);

	}

	/**
	 * Removes the all write.
	 */
	public void removeAllWrite() {

		writeRoles.clear();

		fireTableCellUpdated(0, 2);

	}

	/**
	 * Removes the all execute.
	 */
	public void removeAllExecute() {

		executeRoles.clear();

		fireTableCellUpdated(0, 3);

	}

	/**
	 * Update roles.
	 *
	 * @param fRoles the f roles
	 */
	public void updateRoles(List<FRole> fRoles) {

		if (fRoles != null) {

			List<FRole> fRoles2 = new ArrayList<FRole>();
			fRoles2.addAll(fRoles);

			FRole admin = null;

			for (FRole role : fRoles2)
				if (role.getName().equals("Admin Role"))
					admin = role;

			if (admin != null)
				fRoles2.remove(admin);

			this.roles = fRoles2;

		}

		readRoles.clear();
		writeRoles.clear();
		executeRoles.clear();

		if (abstractBusinessObjectEditor != null) {

			readRoles.addAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getReadRoles());
			writeRoles.addAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getWriteRoles());
			executeRoles.addAll(abstractBusinessObjectEditor.getAbstractBusinessObject().getExecuteRoles());
		}

		fireTableDataChanged();

	}

}
