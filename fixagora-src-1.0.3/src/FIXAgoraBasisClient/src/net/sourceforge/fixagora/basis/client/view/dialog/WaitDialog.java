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
package net.sourceforge.fixagora.basis.client.view.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import net.sourceforge.fixagora.basis.client.view.BasisClientFrame;
import net.sourceforge.fixagora.basis.client.view.ToolbarButton;

import org.apache.log4j.Logger;

/**
 * The Class WaitDialog.
 */
public class WaitDialog extends JDialog implements Runnable {

	private static final long serialVersionUID = 1L;

	private JPanel panel = null;

	private int offset = 0;

	private int shift = 1;

	private Color blue = new Color(204, 216, 255);

	private Color yellow = new Color(255, 243, 204);

	private ImageIcon backImageIcon = null;

	private Image backImage = null;

	private JLabel jLabel;
	
	private double progress = -1;
	
	private double increment = -1;
	
	private static Logger log = Logger.getLogger(WaitDialog.class);

	/**
	 * Instantiates a new wait dialog.
	 *
	 * @param parent the parent
	 * @param text the text
	 */
	public WaitDialog(Component parent, String text) {


		backImageIcon = new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/background.png"));
		backImage = backImageIcon.getImage();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Wait");
		setIconImage(new ImageIcon(BasisClientFrame.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setBounds(100, 100, 300, 128);
		setResizable(false);
		setModal(false);

		final JPanel contentPane = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				for (int i = 0; i < 128; i = i + 32)
					g.drawImage(backImage, 0, i, this);
			}
		};
		setContentPane(contentPane);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel(text);
		lblNewLabel.setForeground(yellow);
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setVerticalTextPosition(SwingConstants.TOP);
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.insets = new Insets(25, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		panel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(offset, 0, blue, offset + 100, height, blue.darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, height);

				getUI().paint(graphics2D, this);
			}
		};

		panel.setOpaque(false);
		panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setPreferredSize(new Dimension(10, 25));
		panel.setBackground(blue);
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 0, 0);
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		
		jLabel = new JLabel();
		jLabel.setMinimumSize(new Dimension(150, 16));
		jLabel.setPreferredSize(new Dimension(150, 16));
		jLabel.setForeground(yellow);
		jLabel.setForeground(Color.BLACK);
		jLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		jLabel.setHorizontalAlignment(SwingConstants.CENTER);		
		panel.add(jLabel,gbc_label);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 25, 25, 25);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);

		final Thread thread = new Thread(this);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

				thread.start();
			}

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});

		if (parent == null) {

			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final Rectangle r = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();

			setLocation(new Point((int) (r.getWidth() - 300) / 2, (int) (r.getHeight() - 128) / 2));
		}
		else {

			final Rectangle r = parent.getBounds();

			setLocation(new Point((int) (r.getWidth() - 300) / 2, (int) (r.getHeight() - 128) / 2));
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while (isVisible()) {

			offset = offset + shift;

			if (offset == 0)
				shift = 1;

			if (offset >= panel.getWidth() - 100)
				shift = -1;

			panel.repaint();
			jLabel.repaint();

			try {

				Thread.sleep(10);
			}
			catch (InterruptedException e) {

				log.error("Bug", e);

			}
		}
	}

	
	/**
	 * Gets the progress.
	 *
	 * @return the progress
	 */
	public double getProgress() {
	
		return progress;
	}

	
	/**
	 * Do progress.
	 */
	public void doProgress() {
		
		if(increment<=0)
			return;
	
		if(progress<0)
			progress=0;
		else
			progress = progress+increment;
				
		if(progress>100)
			progress = 100;
		jLabel.setText(Integer.toString((int)progress)+" %");
	}
	
	/**
	 * Sets the increment.
	 *
	 * @param increment the new increment
	 */
	public void setIncrement(double increment)
	{
		this.increment = increment;
	}

	
	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public double getIncrement() {
	
		return increment;
	}
		
}
