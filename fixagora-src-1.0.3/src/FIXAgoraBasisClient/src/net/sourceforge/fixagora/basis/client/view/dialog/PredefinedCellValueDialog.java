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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The Class PredefinedCellValueDialog.
 */
public class PredefinedCellValueDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private String originalText = null;
	private boolean dirty;
	private JTextField optionRatioTextField;

	/**
	 * Instantiates a new predefined cell value dialog.
	 *
	 * @param originalText the original text
	 */
	public PredefinedCellValueDialog(final String originalText) {
		
		this.originalText = originalText;
		setBounds(100, 100, 487, 147);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(PredefinedCellValueDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				checkConsistency();
			}
		};

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
		
		JLabel predefinedTextLabel = new JLabel("Predefined Text");
		predefinedTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_predefinedTextLabel = new GridBagConstraints();
		gbc_predefinedTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_predefinedTextLabel.insets = new Insets(25, 25, 5, 15);
		gbc_predefinedTextLabel.gridx = 0;
		gbc_predefinedTextLabel.gridy = 0;
		contentPanel.add(predefinedTextLabel, gbc_predefinedTextLabel);

		optionRatioTextField = new JTextField();
		optionRatioTextField.setPreferredSize(new Dimension(200, 25));
		optionRatioTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		optionRatioTextField.setBackground(new Color(255, 243, 204));
		optionRatioTextField.setColumns(10);
		GridBagConstraints gbc_dirtyPriceTextField = new GridBagConstraints();
		gbc_dirtyPriceTextField.gridwidth = 3;
		gbc_dirtyPriceTextField.insets = new Insets(25, 0, 5, 25);
		gbc_dirtyPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_dirtyPriceTextField.gridx = 2;
		gbc_dirtyPriceTextField.gridy = 0;
		contentPanel.add(optionRatioTextField, gbc_dirtyPriceTextField);
		
		if(originalText!=null)
			optionRatioTextField.setText(originalText);
		
		optionRatioTextField.getDocument().addDocumentListener(documentListener);
		
		okButton = new JButton();

		if (this.originalText == null) {
			this.originalText = new String();
			setTitle("Add Predefined Value");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Predefined Value");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 1;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
								
				if (optionRatioTextField.getText().trim().length() > 0)
					PredefinedCellValueDialog.this.originalText=optionRatioTextField.getText().trim();
				
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
		
		checkConsistency();
	}
	
	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}

	
	private void checkConsistency() {

		dirty = false;
		
		if(dirtyFieldCheck(originalText, optionRatioTextField))
			dirty = true;
		
		okButton.setEnabled(dirty);

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
	 * Gets the predefined text.
	 *
	 * @return the predefined text
	 */
	public String getPredefinedText() {
		
		return originalText;
	}
}
