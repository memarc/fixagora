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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * The Class ToolbarButton.
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;

	private Image backImage = null;

	private ImageIcon backImageIcon = null;

	/**
	 * Instantiates a new toolbar button.
	 *
	 * @param imageIcon the image icon
	 * @param tooltipText the tooltip text
	 */
	public ToolbarButton(final ImageIcon imageIcon, final String tooltipText) {

		super();
		
		setToolTipText(tooltipText);
		setFocusable(false);
		setOpaque(true);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setIcon(imageIcon);
		setEnabled(false);
		
		backImageIcon = new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/background.png"));
		backImage = backImageIcon.getImage();

		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {

				getParent().repaint();
				repaint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {

				getParent().repaint();
				repaint();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				getParent().repaint();
				repaint();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {

				getParent().repaint();
				repaint();
			}
		});
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {

		for (int i = 0; i < g.getClipBounds().getWidth(); i = i + backImageIcon.getIconWidth())
			g.drawImage(backImage, i, 0, this);

		super.paintComponent(g);
	}

}
