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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


/**
 * The Class GradientPanel.
 */
public class GradientPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Color dark=Color.BLACK;
	private Color bright=Color.GRAY;
	

	/**
	 * Instantiates a new gradient panel.
	 */
	public GradientPanel() {

		super();
	}

	/**
	 * Instantiates a new gradient panel.
	 *
	 * @param dark the dark
	 * @param bright the bright
	 */
	public GradientPanel(Color dark, Color bright) {

		super();
		this.dark=dark;
		this.bright=bright;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(final Graphics graphics) {

		final Graphics2D graphics2D = (Graphics2D) graphics;

		super.paintComponent(graphics2D);

		final int width = this.getWidth();
		final int height = this.getHeight();
		final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

		graphics2D.setPaint(gradientPaint);
		graphics2D.fillRect(0, 0, width, height);

		getUI().paint(graphics2D, this);
	}

}
