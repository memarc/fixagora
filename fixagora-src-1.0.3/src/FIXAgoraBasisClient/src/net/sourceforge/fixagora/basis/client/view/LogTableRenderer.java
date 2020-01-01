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
package net.sourceforge.fixagora.basis.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.model.log.LogTableModel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;

/**
 * The Class LogTableRenderer.
 */
public class LogTableRenderer implements TableCellRenderer {

	private final ImageIcon warningIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));

	private final ImageIcon bugIcon = new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	private final ImageIcon infoIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/info.png"));

	/**
	 * Instantiates a new log table renderer.
	 */
	public LogTableRenderer() {

		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		JLabel component = new JLabel(value.toString());

		component.setFont(new Font("Dialog", Font.PLAIN, 12));
		if (column == 0)
			component.setBorder(new EmptyBorder(5, 25, 5, 5));
		else
			component.setBorder(new EmptyBorder(5, 5, 5, 5));

		if (row % 2 == 0)
			component.setBackground(new Color(255, 243, 204));

		else
			component.setBackground(new Color(255, 236, 179));

		component.setOpaque(true);
		
		if(((LogTableModel)table.getModel()).isHighlighted(row))
			component.setForeground(new Color(56,106,255));

		if (column == 1) {
			
			component.setIcon(infoIcon);
			if (value.equals("Fatal"))
				component.setIcon(bugIcon);
			if (value.equals("Warning"))
				component.setIcon(warningIcon);
		}

		return component;
	}

}
