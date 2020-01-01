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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import net.sourceforge.fixagora.basis.client.model.editor.RolePermissionTableModel;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;

/**
 * The Class RolePermissionCelleditor.
 */
public class RolePermissionCelleditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private int row = 0;

	private int column = 0;

	private RolePermissionTableModel rolePermissionTableModel;

	/**
	 * Instantiates a new role permission celleditor.
	 *
	 * @param rolePermissionTableModel the role permission table model
	 */
	public RolePermissionCelleditor(RolePermissionTableModel rolePermissionTableModel) {

		super(new JCheckBox());

		this.rolePermissionTableModel = rolePermissionTableModel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		this.row = row;
		this.column = column;

		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {

		AbstractBusinessObjectEditor abstractBusinessObjectEditor = rolePermissionTableModel.getAbstractBusinessObjectEditor();

		FRole fRole = rolePermissionTableModel.getRoles().get(row);

		if (((Boolean) getCellEditorValue()) == true) {
			
			switch (column) {
				
				case 1:
					rolePermissionTableModel.getReadRoles().add(fRole);
					break;
					
				case 2:
					rolePermissionTableModel.getWriteRoles().add(fRole);
					break;
					
				case 3:
					rolePermissionTableModel.getExecuteRoles().add(fRole);
					break;

				default:
					break;
			}
		}
		else {
			
			switch (column) {
				
				case 1:
					rolePermissionTableModel.getReadRoles().remove(fRole);
					break;
					
				case 2:
					rolePermissionTableModel.getWriteRoles().remove(fRole);
					break;
					
				case 3:
					rolePermissionTableModel.getExecuteRoles().remove(fRole);
					break;

				default:
					break;
			}
		}
		
		rolePermissionTableModel.fireTableCellUpdated(row, column);
		
		if (abstractBusinessObjectEditor != null)
			abstractBusinessObjectEditor.checkDirty();
		
		return super.stopCellEditing();
	}

}
