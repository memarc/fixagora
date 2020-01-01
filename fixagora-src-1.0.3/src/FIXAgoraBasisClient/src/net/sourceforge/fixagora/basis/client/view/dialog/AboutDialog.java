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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;

import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;

import java.awt.Dimension;

/**
 * The Class AboutDialog.
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final ImageIcon backImage = new ImageIcon(AboutDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/about.png"));

	private final JPanel contentPanel = new JPanel();

	private final Image image = backImage.getImage();
	
	private String contact = null;

	/**
	 * Instantiates a new about dialog.
	 *
	 * @param fixAgoraPlugins the fix agora plugins
	 */
	public AboutDialog(List<FIXAgoraPlugin> fixAgoraPlugins) {

		setTitle("About FIX Agora");
		setIconImage(new ImageIcon(AboutDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setBounds(100, 100, 632, 480);
		setResizable(false);

		final JPanel contentPane = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				super.paintComponent(g);

				for (int i = 0; i < g.getClipBounds().getWidth(); i = i + backImage.getIconWidth())
					g.drawImage(image, i, 0, this);
			}

		};

		setContentPane(contentPane);
		getContentPane().setLayout(new BorderLayout());

		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		final GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		comboBox.setPreferredSize(new Dimension(200, 25));
		comboBox.setMinimumSize(new Dimension(200, 25));
		comboBox.setFocusable(false);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(90, 0, 5, 0);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 0;
		contentPanel.add(comboBox, gbc_comboBox);
		
		comboBox.addItem(new PluginInfo("FIX Agora Basis", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com"));
		
		for(FIXAgoraPlugin fixAgoraPlugin: fixAgoraPlugins)
			comboBox.addItem(fixAgoraPlugin.getPluginInfo());

		final JLabel copyrightLabel = new JLabel();
		copyrightLabel.setForeground(Color.WHITE);
		copyrightLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		final GridBagConstraints gbc_copyrightLabel = new GridBagConstraints();
		gbc_copyrightLabel.insets = new Insets(5, 0, 5, 0);
		gbc_copyrightLabel.gridx = 0;
		gbc_copyrightLabel.gridy = 1;
		contentPanel.add(copyrightLabel, gbc_copyrightLabel);

		final JLabel contactLabel = new JLabel();
		contactLabel.setIcon(new ImageIcon(AboutDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/mail_new3.png")));
		contactLabel.setForeground(Color.WHITE);
		contactLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		final GridBagConstraints gbc_contactLabel = new GridBagConstraints();
		gbc_contactLabel.insets = new Insets(5, 0, 5, 0);
		gbc_contactLabel.gridx = 0;
		gbc_contactLabel.gridy = 2;
		contentPanel.add(contactLabel, gbc_contactLabel);

		contactLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {

			}

			@Override
			public void mouseEntered(final MouseEvent e) {

				contactLabel.setForeground(new Color(204, 216, 255));

			}

			@Override
			public void mouseExited(final MouseEvent e) {

				contactLabel.setForeground(Color.WHITE);

			}

			@Override
			public void mousePressed(final MouseEvent e) {

				try {
					Desktop.getDesktop().mail(new URI("mailto:"+contact));
				}
				catch (final Exception e1) {
					
					ExceptionDialog.showException(e1);
				}

			}

			@Override
			public void mouseReleased(final MouseEvent e) {

			}
		});
		
		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(15, 25, 20, 25);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPanel.add(scrollPane, gbc_scrollPane);

		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer
				.append("FIX Pusher is free software; you can redistribute it and/or modify it under the terms of the GNU Library General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.");
		stringBuffer.append("\n\n");
		stringBuffer
				.append("FIX Pusher is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more details.");
		stringBuffer.append("\n\n");
		stringBuffer
				.append("You should have received a copy of the GNU Library General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.");

		final JTextPane licensePane = new JTextPane();
		licensePane.setEditable(false);
		licensePane.setBackground(new Color(255, 243, 204));
		
		scrollPane.setViewportView(licensePane);
		
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				PluginInfo pluginInfo = (PluginInfo)comboBox.getSelectedItem();
				if(pluginInfo!=null)
				{
					copyrightLabel.setText(pluginInfo.getCopyRight());
					contactLabel.setText(pluginInfo.getContact());
					licensePane.setText(pluginInfo.getLicenseText());
					contact = pluginInfo.getContact();
					scrollPane.getViewport().setViewPosition(new Point(0,0));
				}
				
			}
		});
		
		comboBox.setSelectedIndex(0);

		final JPanel buttonPane = new JPanel();
		buttonPane.setBorder(new EmptyBorder(0, 0, 25, 25));
		buttonPane.setOpaque(false);
		final FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
		buttonPane.setLayout(fl_buttonPane);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		final JButton closeButton = new JButton("Close");
		closeButton.setMinimumSize(new Dimension(100, 25));
		closeButton.setMaximumSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setIcon(new ImageIcon(AboutDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		closeButton.setActionCommand("Cancel");
		buttonPane.add(closeButton);

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				AboutDialog.this.setVisible(false);

			}
		});

	}

}
