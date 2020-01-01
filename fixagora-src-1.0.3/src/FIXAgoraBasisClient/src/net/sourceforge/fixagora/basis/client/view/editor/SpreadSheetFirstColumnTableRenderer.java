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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;

/**
 * The Class SpreadSheetFirstColumnTableRenderer.
 */
public class SpreadSheetFirstColumnTableRenderer implements TableCellRenderer {

	private Color blue = new Color(204, 216, 255);

	private Color darkBlue = new Color(143, 169, 255);

	private JTable conentTable;

	/**
	 * Instantiates a new spread sheet first column table renderer.
	 *
	 * @param conentTable the conent table
	 */
	public SpreadSheetFirstColumnTableRenderer(JTable conentTable) {

		super();
		this.conentTable = conentTable;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

			JLabel label = null;
			if (conentTable.isRowSelected(row)) {
				label = new GradientLabel(darkBlue.darker(), darkBlue);
				label.setForeground(Color.WHITE);
			}
			else
				label = new GradientLabel(blue.darker(), blue);
			label.setText(Integer.toString(table.convertRowIndexToModel(row) + 1));
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setBorder(new CompoundBorder(new MatteBorder(0,0,0,1, blue.darker()), new EmptyBorder(0, 0, 0, 5)));
			return label;

	}

}
