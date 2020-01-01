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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.view.BasisClientFrame;
import net.sourceforge.fixagora.basis.client.view.document.IntegerDocument;
import javax.swing.SwingConstants;

/**
 * The Class LoginDialog.
 */
public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField userField;
	private JPasswordField passwordField;
	private JTextField hostField;
	private JTextField portField;

	private final ImageIcon backImageIcon = new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/login.png"));

	private final Image backImage = backImageIcon.getImage();

	private boolean cancelled = false;

	/**
	 * Instantiates a new login dialog.
	 *
	 * @param lastHost the last host
	 * @param lastPort the last port
	 * @param lastUser the last user
	 * @param comment the comment
	 */
	public LoginDialog(String lastHost, String lastPort, String lastUser, String comment) {

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(BasisClientFrame.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());

		setModal(true);

		setBounds(100, 100, 500, 350);
		setResizable(false);

		setTitle("Login");

		final JPanel contentPane = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				super.paintComponent(g);

				g.drawImage(backImage, 0, 0, this);

			}

		};

		setContentPane(contentPane);
		getContentPane().setLayout(new BorderLayout());

		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(50, 50, 25, 50));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		
		JLabel versionLabel = new JLabel("Version " + "1.0.3" + "  -  Copyright (C) 2012-2015  Alexander Pinnow");
		versionLabel.setForeground(Color.WHITE);
		versionLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_versionLabel = new GridBagConstraints();
		gbc_versionLabel.anchor = GridBagConstraints.SOUTH;
		gbc_versionLabel.gridwidth = 3;
		gbc_versionLabel.gridx = 0;
		gbc_versionLabel.gridy = 0;
		contentPanel.add(versionLabel, gbc_versionLabel);

		JLabel commentLabel = new JLabel(comment);
		commentLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		commentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		commentLabel.setPreferredSize(new Dimension(250, 15));
		commentLabel.setMinimumSize(new Dimension(250, 15));
		commentLabel.setMaximumSize(new Dimension(250, 15));
		commentLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		commentLabel.setForeground(new Color(255, 198, 179));
		GridBagConstraints gbc_commentLabel = new GridBagConstraints();
		gbc_commentLabel.fill = GridBagConstraints.BOTH;
		gbc_commentLabel.gridwidth = 3;
		gbc_commentLabel.insets = new Insets(5, 0, 5, 0);
		gbc_commentLabel.gridx = 0;
		gbc_commentLabel.gridy = 1;
		contentPanel.add(commentLabel, gbc_commentLabel);
		
		JLabel userLabel = new JLabel("User");
		userLabel.setForeground(Color.WHITE);
		userLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_userLabel = new GridBagConstraints();
		gbc_userLabel.insets = new Insets(5, 0, 5, 25);
		gbc_userLabel.anchor = GridBagConstraints.WEST;
		gbc_userLabel.gridx = 0;
		gbc_userLabel.gridy = 2;
		contentPanel.add(userLabel, gbc_userLabel);
		
		userField = new JTextField(lastUser);
		userField.setBackground(new Color(255, 243, 204));
		userField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		userField.setPreferredSize(new Dimension(72, 25));
		userField.setColumns(10);
		GridBagConstraints gbc_userField = new GridBagConstraints();
		gbc_userField.insets = new Insets(5, 0, 5, 0);
		gbc_userField.fill = GridBagConstraints.HORIZONTAL;
		gbc_userField.gridx = 1;
		gbc_userField.gridy = 2;
		contentPanel.add(userField, gbc_userField);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setForeground(Color.WHITE);
		passwordLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 25);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 3;
		contentPanel.add(passwordLabel, gbc_lblNewLabel_1);
		
		passwordField = new JPasswordField();
		passwordField.setBackground(new Color(255, 243, 204));
		passwordField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		passwordField.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 3;
		contentPanel.add(passwordField, gbc_passwordField);
		
		JLabel hostLabel = new JLabel("Host");
		hostLabel.setForeground(Color.WHITE);
		hostLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_hostLabel = new GridBagConstraints();
		gbc_hostLabel.anchor = GridBagConstraints.WEST;
		gbc_hostLabel.insets = new Insets(0, 0, 5, 25);
		gbc_hostLabel.gridx = 0;
		gbc_hostLabel.gridy = 4;
		contentPanel.add(hostLabel, gbc_hostLabel);
		
		hostField = new JTextField(lastHost);
		hostField.setBackground(new Color(255, 243, 204));
		hostField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		hostField.setPreferredSize(new Dimension(4, 25));
		hostField.setColumns(10);
		GridBagConstraints gbc_hostField = new GridBagConstraints();
		gbc_hostField.insets = new Insets(0, 0, 5, 0);
		gbc_hostField.fill = GridBagConstraints.HORIZONTAL;
		gbc_hostField.gridx = 1;
		gbc_hostField.gridy = 4;
		contentPanel.add(hostField, gbc_hostField);
	
		JLabel portLabel = new JLabel("Port");
		portLabel.setForeground(Color.WHITE);
		portLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 0, 25);
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 5;
		contentPanel.add(portLabel, gbc_lblNewLabel_3);
		
		portField = new JTextField();
		portField.setBackground(new Color(255, 243, 204));
		portField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		portField.setPreferredSize(new Dimension(4, 25));
		portField.setDocument(new IntegerDocument());
		portField.setText(lastPort);
		portField.setColumns(10);
		GridBagConstraints gbc_portField = new GridBagConstraints();
		gbc_portField.fill = GridBagConstraints.HORIZONTAL;
		gbc_portField.gridx = 1;
		gbc_portField.gridy = 5;
		contentPanel.add(portField, gbc_portField);
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setOpaque(false);
		buttonPane.setBorder(new EmptyBorder(0, 0, 25, 45));
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(100, 25));
		//loginButton.setOpaque(false);
		loginButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit_user.png")));
		loginButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		loginButton.setActionCommand("OK");
		buttonPane.add(loginButton);
		
		loginButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(loginButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setActionCommand("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 25));
		//cancelButton.setOpaque(false);
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		buttonPane.add(cancelButton);
		
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
			}
		});
		
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

				passwordField.requestFocus();

			}

			@Override
			public void componentResized(ComponentEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {

				// TODO Auto-generated method stub

			}
		});

		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final Rectangle r = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();

		setLocation(new Point((int) (r.getWidth() - 500) / 2, (int) (r.getHeight() - 350) / 2));

	}

	/**
	 * Checks if is cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {

		return cancelled;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public String getPort() {

		return portField.getText();
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {

		return hostField.getText();
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {

		return userField.getText();
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {

		return new String(passwordField.getPassword());
	}

}
