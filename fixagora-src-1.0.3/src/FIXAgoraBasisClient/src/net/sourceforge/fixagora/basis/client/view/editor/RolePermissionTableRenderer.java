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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.model.editor.RolePermissionTableModel;

/**
 * The Class RolePermissionTableRenderer.
 */
public class RolePermissionTableRenderer implements TableCellRenderer {

	private RolePermissionTableModel rolePermissionTableModel = null;

	/**
	 * Instantiates a new role permission table renderer.
	 *
	 * @param rolePermissionTableModel the role permission table model
	 */
	public RolePermissionTableRenderer(final RolePermissionTableModel rolePermissionTableModel) {

		super();

		this.rolePermissionTableModel = rolePermissionTableModel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		if (value instanceof Boolean) {

			JCheckBox jCheckBox = new JCheckBox();
			jCheckBox.setSelected((Boolean) value);
			jCheckBox.setBorder(new EmptyBorder(5, 5, 5, 5));

			if (rolePermissionTableModel.getAbstractBusinessObjectEditor() != null && !rolePermissionTableModel.getAbstractBusinessObjectEditor().isEditable()) {
				jCheckBox.setEnabled(false);

				if (row % 2 == 0)
					jCheckBox.setBackground(new Color(204, 216, 255));

				else
					jCheckBox.setBackground(new Color(179, 196, 255));
			}
			else {
				if (row % 2 == 0)
					jCheckBox.setBackground(new Color(255, 243, 204));

				else
					jCheckBox.setBackground(new Color(255, 236, 179));
			}

			return jCheckBox;
		}
		else {

			JComponent component = new JLabel(value.toString());
			component.setFont(new Font("Dialog", Font.PLAIN, 12));
			component.setBorder(new EmptyBorder(5, 25, 5, 5));

			if (rolePermissionTableModel.getAbstractBusinessObjectEditor() != null && !rolePermissionTableModel.getAbstractBusinessObjectEditor().isEditable()) {

				
				if (row % 2 == 0)
					component.setBackground(new Color(204, 216, 255));

				else
					component.setBackground(new Color(179, 196, 255));
			}
			else {
				if (row % 2 == 0)
					component.setBackground(new Color(255, 243, 204));

				else
					component.setBackground(new Color(255, 236, 179));
			}

			component.setOpaque(true);

			return component;
		}
	}

}
