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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;

/**
 * The Class StatusPanel.
 */
public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final ImageIcon greenLED = new ImageIcon(StatusPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/greenled.png"));
	
	private final ImageIcon redLED = new ImageIcon(StatusPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/redled.png"));
	
	private final JLabel infoIconLabel;
	
	private final JLabel infoTextLabel;
	
	private final JLabel connectionLabel;
	
	private final JLabel connectionIconLabel;
	
	private Color blue = new Color(204, 216, 255);

	/**
	 * Instantiates a new status panel.
	 *
	 * @param basisClientConnector the basis client connector
	 */
	public StatusPanel(BasisClientConnector basisClientConnector) {

		super();
		
		setPreferredSize(new Dimension(10, 24));
		setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0 };
		setLayout(gridBagLayout);

		infoIconLabel = new JLabel();
		final GridBagConstraints gbc_infoIconLabel = new GridBagConstraints();
		gbc_infoIconLabel.insets = new Insets(2, 2, 2, 5);
		gbc_infoIconLabel.gridx = 0;
		gbc_infoIconLabel.gridy = 0;
		add(infoIconLabel, gbc_infoIconLabel);

		infoTextLabel = new JLabel();
		infoTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_infoTextLabel = new GridBagConstraints();
		gbc_infoTextLabel.anchor = GridBagConstraints.WEST;
		gbc_infoTextLabel.insets = new Insets(2, 2, 2, 5);
		gbc_infoTextLabel.gridx = 1;
		gbc_infoTextLabel.gridy = 0;
		add(infoTextLabel, gbc_infoTextLabel);
		
		connectionLabel = new JLabel(basisClientConnector.getFUser().getName()+" connected at " + DateFormat.getDateTimeInstance().format(new Date()));
		connectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		connectionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_connectionLabel = new GridBagConstraints();
		gbc_connectionLabel.anchor = GridBagConstraints.EAST;
		gbc_connectionLabel.insets = new Insets(2, 2, 5, 5);
		gbc_connectionLabel.gridx = 2;
		gbc_connectionLabel.gridy = 0;
		add(connectionLabel, gbc_connectionLabel);

		connectionIconLabel = new JLabel();
		connectionIconLabel.setIcon(greenLED);
		final GridBagConstraints gbc_connectionIconLabel = new GridBagConstraints();
		gbc_connectionIconLabel.anchor = GridBagConstraints.WEST;
		gbc_connectionIconLabel.fill = GridBagConstraints.VERTICAL;
		gbc_connectionIconLabel.insets = new Insets(2, 2, 5, 15);
		gbc_connectionIconLabel.gridx = 3;
		gbc_connectionIconLabel.gridy = 0;
		add(connectionIconLabel, gbc_connectionIconLabel);
		
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
		
		final GradientPaint gradientPaint2 = new GradientPaint(width / 2.F, height-27, blue, width / 2.F, height, blue.darker());

		graphics2D.setPaint(gradientPaint2);
		graphics2D.fillRoundRect(3,  height-34, width-7, 30, 14,14);
		
		graphics2D.setColor(Color.GRAY);
		graphics2D.drawRoundRect(3,  height-34, width-7, 30, 14,14);
		
		getUI().paint(graphics2D, this);
	}

	/**
	 * On disconnected.
	 */
	public void onDisconnected() {

		connectionLabel.setText("Disconnected at " + DateFormat.getDateTimeInstance().format(new Date()));
		connectionIconLabel.setIcon(redLED);
		
	}	
	
}
