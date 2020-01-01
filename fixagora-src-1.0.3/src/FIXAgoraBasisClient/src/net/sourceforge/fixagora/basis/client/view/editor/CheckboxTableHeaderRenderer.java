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
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.sourceforge.fixagora.basis.client.model.editor.RolePermissionTableModel;
import net.sourceforge.fixagora.basis.client.view.ColoredCheckBoxIcon;
import net.sourceforge.fixagora.basis.client.view.GradientLabel;

/**
 * The Class CheckboxTableHeaderRenderer.
 */
public class CheckboxTableHeaderRenderer extends GradientLabel implements TableCellRenderer, MouseListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	/** The mouse pressed. */
	protected boolean mousePressed = false;

	private JCheckBox checkBox = null;

	private int column;

	private boolean selected = false;

	private RolePermissionTableModel rolePermissionTableModel = null;

	private JTableHeader tableHeader = null;

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable jTable, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		if (checkBox == null) {

			this.column = column;
			this.rolePermissionTableModel = (RolePermissionTableModel) jTable.getModel();
			this.tableHeader = jTable.getTableHeader();

			checkBox = new JCheckBox(value.toString());
			checkBox.setIcon(new ColoredCheckBoxIcon(Color.WHITE));
			checkBox.setSelected(selected);
			checkBox.setFont(new Font("Dialog", Font.PLAIN, 12));
			checkBox.setOpaque(false);
			checkBox.setForeground(Color.WHITE);

			if (column < jTable.getColumnCount() - 1)
				checkBox.setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 0, 0, 1), Color.BLACK), new EmptyBorder(5, 5, 5, 0)));

			else
				checkBox.setBorder(new EmptyBorder(5, 5, 5, 0));

			tableChanged(null);

			jTable.getModel().addTableModelListener(this);
			jTable.getTableHeader().addMouseListener(this);

			if (rolePermissionTableModel.getAbstractBusinessObjectEditor() != null && !rolePermissionTableModel.getAbstractBusinessObjectEditor().isEditable())
				checkBox.setEnabled(false);
		}

		return checkBox;
	}

	/**
	 * Handle click event.
	 *
	 * @param e the e
	 */
	protected void handleClickEvent(MouseEvent e) {

		if (rolePermissionTableModel.getAbstractBusinessObjectEditor()!=null&&!rolePermissionTableModel.getAbstractBusinessObjectEditor().isEditable())
			return;

		if (mousePressed) {

			mousePressed = false;

			JTableHeader header = (JTableHeader) (e.getSource());
			JTable tableView = header.getTable();
			TableColumnModel columnModel = tableView.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = tableView.convertColumnIndexToModel(viewColumn);

			if (viewColumn == this.column && e.getClickCount() == 1 && column > 0) {

				checkBox.setSelected(!checkBox.isSelected());

				if (checkBox.isSelected()) {

					switch (column) {
						case 1:
							rolePermissionTableModel.addAllRead();
							break;

						case 2:
							rolePermissionTableModel.addAllWrite();
							break;

						case 3:
							rolePermissionTableModel.addAllExecute();
							break;
					}
				}
				else {

					switch (column) {

						case 1:
							rolePermissionTableModel.removeAllRead();
							break;

						case 2:
							rolePermissionTableModel.removeAllWrite();
							break;

						case 3:

							rolePermissionTableModel.removeAllExecute();
							break;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {

		handleClickEvent(e);
		((JTableHeader) e.getSource()).repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {

		mousePressed = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {

		switch (column) {
			
			case 1:
				checkBox.setSelected(rolePermissionTableModel.isAllReadSet());
				break;
				
			case 2:
				checkBox.setSelected(rolePermissionTableModel.isAllWriteSet());
				break;
				
			case 3:
				checkBox.setSelected(rolePermissionTableModel.isAllExecuteSet());
				break;
		}
		
		if (rolePermissionTableModel.getAbstractBusinessObjectEditor() != null && !rolePermissionTableModel.getAbstractBusinessObjectEditor().isEditable())
			checkBox.setEnabled(false);
		
		else
			checkBox.setEnabled(true);
		
		tableHeader.repaint();
	}

}
