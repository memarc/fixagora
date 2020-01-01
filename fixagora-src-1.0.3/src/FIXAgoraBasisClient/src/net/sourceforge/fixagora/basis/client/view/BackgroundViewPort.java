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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JViewport;



/**
 * The Class BackgroundViewPort.
 */
public class BackgroundViewPort extends JViewport {
	
	private static final long serialVersionUID = 1L;
	
	private Image backImage = null;

	private ImageIcon backImageIcon = null;


	/**
	 * Instantiates a new background view port.
	 */
	public BackgroundViewPort() {

		super();
		
		setOpaque(true);
		setBackground(new Color(204, 216, 255));
		
		backImageIcon = new ImageIcon(BackgroundViewPort.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/logo.png"));
		backImage = backImageIcon.getImage();


	}



	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {

		super.paintComponent(g);

		final Graphics2D graphics2d = (Graphics2D) g;
		final int width = getBounds().width - 100;
		final int height = width / 12;
		final int y = (int) getBounds().getHeight() - height - 50;
		graphics2d.drawImage(backImage, 50, y, width, height, null);

	}

}
