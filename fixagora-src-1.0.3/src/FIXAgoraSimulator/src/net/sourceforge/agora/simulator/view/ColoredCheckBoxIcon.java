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
package net.sourceforge.agora.simulator.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * The Class ColoredCheckBoxIcon.
 */
public class ColoredCheckBoxIcon implements Icon {

	private Color color = null;

	private int width = 0;
	private int height = 0;

	private Icon icon;

	/**
	 * Instantiates a new colored check box icon.
	 *
	 * @param iconColor the icon color
	 */
	public ColoredCheckBoxIcon(Color iconColor) {

		super();

		color = iconColor;

		icon = UIManager.getIcon("CheckBox.icon");

		width = icon.getIconWidth();

		height = icon.getIconHeight();

	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		g.setColor(color);

		g.fillRect(x, y, width, height);
		
		icon.paintIcon(c, g, x, y);
		
		

	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	@Override
	public int getIconWidth() {

		return width;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	@Override
	public int getIconHeight() {

		return height;
	}

}
