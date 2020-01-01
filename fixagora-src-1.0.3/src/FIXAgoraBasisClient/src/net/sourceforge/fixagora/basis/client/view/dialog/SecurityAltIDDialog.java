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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;

/**
 * The Class SecurityAltIDDialog.
 */
public class SecurityAltIDDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JComboBox textField_1;
	private boolean cancelled = true;
	private JButton okButton;

	/**
	 * Instantiates a new security alt id dialog.
	 */
	public SecurityAltIDDialog() {
		
		setTitle("Add Security Alternative Identifier");
		setBounds(100, 100, 379, 180);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityAltIDDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		
		DocumentListener documentListener = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
			
				checkConsistency();				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
			
				checkConsistency();			}
			
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
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		JLabel lblNewLabel = new JLabel("Identifier");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(25, 25, 5, 15);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 25));
		textField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		textField.setBackground(new Color(255, 243, 204));
		textField.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(25, 0, 5, 25);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		contentPanel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Source");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 25, 5, 15);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		List<ComboBoxEntry> securityIdentifierSourceComboBoxEntries = new ArrayList<ComboBoxEntry>();
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("1", "CUSIP"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("2", "SEDOL"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("3", "QUIK"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("4", "ISIN number"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("5", "RIC code"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("6", "ISO Currency Code"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("7", "ISO Country Code"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("8", "Exchange Symbol"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("9", "Consolidated Tape Association"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("A", "Bloomberg Symbol"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("B", "Wertpapier"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("C", "Dutch"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("D", "Valoren"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("E", "Sicovam"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("F", "Belgian"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("G", "Common"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("H", "Clearing House"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("I", "ISDA/FpML Product Specification"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("J", "Option Price Reporting Authority"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("K", "ISDA/FpML Product URL"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("L", "Letter of Credit"));
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("M", "Marketplace-assigned Identifier"));	
		
		textField_1 = new JComboBox(securityIdentifierSourceComboBoxEntries.toArray());
		textField_1.setMinimumSize(new Dimension(200, 25));
		textField_1.setRenderer(new DefaultEditorComboBoxRenderer());
		textField_1.setPreferredSize(new Dimension(250, 25));
		textField_1.setBackground(new Color(255, 243, 204));
		textField_1.setPreferredSize(new Dimension(200, 25));
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 25);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		contentPanel.add(textField_1, gbc_textField_1);
		okButton = new JButton("Add");
		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.anchor = GridBagConstraints.EAST;
		gbc_okButton.gridx = 1;
		gbc_okButton.gridy = 2;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 2;
		gbc_cancelButton.gridy = 2;
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
	 * Gets the security alt id group.
	 *
	 * @return the security alt id group
	 */
	public SecurityAltIDGroup getSecurityAltIDGroup()
	{
		SecurityAltIDGroup securityAltIDGroup = new SecurityAltIDGroup();
		securityAltIDGroup.setSecurityAltID(textField.getText().trim());
		securityAltIDGroup.setSecurityAltIDSource(((ComboBoxEntry)textField_1.getSelectedItem()).getEntry().toString());
		return securityAltIDGroup;
	}
	
	/**
	 * Checks if is cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {
	
		return cancelled;
	}
	
	private void checkConsistency() {

		if(textField.getText().trim().length()>0)
			okButton.setEnabled(true);
		else
			okButton.setEnabled(false);
	
	}


}
