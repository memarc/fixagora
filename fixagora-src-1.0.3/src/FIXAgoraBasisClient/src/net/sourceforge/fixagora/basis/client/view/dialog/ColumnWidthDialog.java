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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.view.document.PositiveIntegerDocument;

/**
 * The Class ColumnWidthDialog.
 */
public class ColumnWidthDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private int columnWidth = 0;
	private JTextField columnWidthTextField;
	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	/**
	 * Instantiates a new column width dialog.
	 *
	 * @param originalWidth the original width
	 */
	public ColumnWidthDialog(final int originalWidth) {

		this.columnWidth = originalWidth;
		setBounds(100, 100, 390, 147);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(ColumnWidthDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel predefinedTextLabel = new JLabel("Column Width");
		predefinedTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_predefinedTextLabel = new GridBagConstraints();
		gbc_predefinedTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_predefinedTextLabel.insets = new Insets(25, 25, 5, 15);
		gbc_predefinedTextLabel.gridx = 0;
		gbc_predefinedTextLabel.gridy = 0;
		contentPanel.add(predefinedTextLabel, gbc_predefinedTextLabel);

		columnWidthTextField = new JTextField();
		columnWidthTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		columnWidthTextField.setPreferredSize(new Dimension(200, 25));
		columnWidthTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		columnWidthTextField.setBackground(new Color(255, 243, 204));
		columnWidthTextField.setColumns(4);
		columnWidthTextField.setDocument(new PositiveIntegerDocument());
		GridBagConstraints gbc_dirtyPriceTextField = new GridBagConstraints();
		gbc_dirtyPriceTextField.gridwidth = 3;
		gbc_dirtyPriceTextField.insets = new Insets(25, 0, 5, 25);
		gbc_dirtyPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_dirtyPriceTextField.gridx = 2;
		gbc_dirtyPriceTextField.gridy = 0;
		contentPanel.add(columnWidthTextField, gbc_dirtyPriceTextField);

		columnWidthTextField.setText(integerFormat.format(originalWidth));

		okButton = new JButton();

		setTitle("Column Width");
		okButton.setText("Ok");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 1;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;

				ColumnWidthDialog.this.columnWidth = Integer.parseInt(columnWidthTextField.getText().trim());

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridwidth = 2;
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 1;
		contentPanel.add(cancelButton, gbc_cancelButton);
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
			}
		});
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
	 * Gets the column width.
	 *
	 * @return the column width
	 */
	public int getColumnWidth() {

		return columnWidth;
	}
}
