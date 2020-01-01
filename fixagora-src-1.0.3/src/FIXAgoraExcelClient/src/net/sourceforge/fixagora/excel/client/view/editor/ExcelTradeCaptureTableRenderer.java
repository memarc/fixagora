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
package net.sourceforge.fixagora.excel.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;

/**
 * The Class ExcelTradeCaptureTableRenderer.
 */
public class ExcelTradeCaptureTableRenderer implements TableCellRenderer {

	private Color backGround1 = new Color(255, 243, 204);

	// private Color backGround2 = new Color(255, 236, 179);

	private Color foreGroundDefault = Color.BLACK;

	private Color blue = new Color(204, 216, 255);

	private Color green = new Color(51, 153, 0);

	private Color red = new Color(204, 0, 0);

	/**
	 * Instantiates a new excel trade capture table renderer.
	 */
	public ExcelTradeCaptureTableRenderer() {

		super();

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		if (column == 0) {
			GradientLabel numberLabel = new GradientLabel(blue.darker(), blue);
			numberLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			numberLabel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, blue.darker()), new EmptyBorder(0, 5, 0, 5)));
			numberLabel.setText(value.toString());
			return numberLabel;
		}
		else {

			GradientLabel component = new GradientLabel();
			if (value != null)
				component.setText(value.toString());

			if(row==0)
				component.setFont(new Font("Dialog", Font.BOLD, 12));
			else
				component.setFont(new Font("Dialog", Font.PLAIN, 12));

			component.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 1, blue.darker()), new EmptyBorder(4, 5, 4, 5)));

			if ((column > 9 && column < 12) || column ==19)
				component.setHorizontalAlignment(SwingConstants.RIGHT);

			component.setBright(backGround1);
			component.setDark(backGround1);

			component.setForeground(foreGroundDefault);

			if (column == 3 && row>0 && value instanceof String) {
				if (((String) value).startsWith("Sell"))
					component.setForeground(red);
				else
					component.setForeground(green);

			}

			component.setOpaque(true);
			return component;
		}

	}

}
