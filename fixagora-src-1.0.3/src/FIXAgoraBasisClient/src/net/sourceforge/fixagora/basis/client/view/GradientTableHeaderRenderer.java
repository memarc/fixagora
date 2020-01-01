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
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

/**
 * The Class GradientTableHeaderRenderer.
 */
public class GradientTableHeaderRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private int offset = 25;

	private Color dark=Color.BLACK;
	private Color bright=Color.GRAY;
	private Color foreground = Color.WHITE;
	
	
	/**
	 * Instantiates a new gradient table header renderer.
	 *
	 * @param offset the offset
	 */
	public GradientTableHeaderRenderer(int offset) {

		super();
		this.offset  = offset;
	}

	/**
	 * Instantiates a new gradient table header renderer.
	 *
	 * @param offset the offset
	 * @param dark the dark
	 * @param bright the bright
	 * @param foreground the foreground
	 */
	public GradientTableHeaderRenderer(int offset, Color dark, Color bright, Color foreground) {

		super();
		this.offset  = offset;
		this.dark=dark;
		this.bright=bright;
		this.foreground=foreground;
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

		final JLabel label = new GradientLabel(dark,bright);
		if(value!=null)
			label.setText(value.toString());
		label.setFont(new Font("Dialog", Font.PLAIN, 12));
		label.setHorizontalAlignment(getHorizontalAlignment());
		
		if (column < jTable.getColumnCount() - 1) {
			
			if (column == 0)
				label.setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 0, 0, 1),dark), new EmptyBorder(5, offset, 5, 5)));
			
			else
				label.setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 0, 0, 1), dark), new EmptyBorder(5, 5, 5, 5)));
		}
		else
			label.setBorder(new EmptyBorder(5, 5, 5, 0));
		
		label.setOpaque(true); 
		label.setForeground(foreground);
		label.setBackground(bright);

		return label;
	}

}
