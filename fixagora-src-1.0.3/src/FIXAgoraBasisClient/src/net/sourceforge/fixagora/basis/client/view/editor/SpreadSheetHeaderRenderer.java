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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

/**
 * The Class SpreadSheetHeaderRenderer.
 */
public class SpreadSheetHeaderRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Color blue = new Color(204, 216, 255);
	private Color darkBlue = new Color(143, 169, 255);

	/**
	 * Instantiates a new spread sheet header renderer.
	 */
	public SpreadSheetHeaderRenderer() {

		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable jTable, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		final JLabel label = new JLabel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				Color bright = blue;
				Color dark = blue.darker();
				setForeground(Color.BLACK);
				if (jTable.getColumnCount()>1&&jTable.isColumnSelected(column)) {
					bright = darkBlue;
					dark = darkBlue.darker();
					setForeground(Color.WHITE);
				}

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, height);

				getUI().paint(graphics2D, this);
			}
		};

		if (value != null)
			label.setText(value.toString());
		label.setFont(new Font("Dialog", Font.PLAIN, 12));

		label.setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 0, 0, 1), blue.darker()), new EmptyBorder(5, 5, 5, 0)));

		label.setOpaque(true);

		return label;
	}

}
