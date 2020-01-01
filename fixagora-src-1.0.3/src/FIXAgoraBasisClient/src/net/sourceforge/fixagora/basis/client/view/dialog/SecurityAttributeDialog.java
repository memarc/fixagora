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
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAttribute;

/**
 * The Class SecurityAttributeDialog.
 */
public class SecurityAttributeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField valueTextField;
	private boolean cancelled = true;
	private JButton okButton;
	private SecurityAttribute securityAttribute = null;
	private JComboBox typeComboBox;
	private boolean dirty;

	/**
	 * Instantiates a new security attribute dialog.
	 *
	 * @param securityAttribute the security attribute
	 */
	public SecurityAttributeDialog(SecurityAttribute securityAttribute) {

		this.securityAttribute = securityAttribute;
		setBounds(100, 100, 533, 218);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityAttributeDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 146 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel typeLabel = new JLabel("Type");
		typeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_typeLabel = new GridBagConstraints();
		gbc_typeLabel.anchor = GridBagConstraints.WEST;
		gbc_typeLabel.insets = new Insets(25, 25, 5, 5);
		gbc_typeLabel.gridx = 0;
		gbc_typeLabel.gridy = 0;
		contentPanel.add(typeLabel, gbc_typeLabel);

		List<ComboBoxEntry> typeComboEntries = new ArrayList<ComboBoxEntry>();
		typeComboEntries.add(new ComboBoxEntry(1, "Flat"));
		typeComboEntries.add(new ComboBoxEntry(2, "Zero coupon"));
		typeComboEntries.add(new ComboBoxEntry(3, "Interest bearing"));
		typeComboEntries.add(new ComboBoxEntry(4, "No periodic payments"));
		typeComboEntries.add(new ComboBoxEntry(5, "Variable rate"));
		typeComboEntries.add(new ComboBoxEntry(6, "Less fee for put"));
		typeComboEntries.add(new ComboBoxEntry(7, "Stepped coupon"));
		typeComboEntries.add(new ComboBoxEntry(8, "Coupon period"));
		typeComboEntries.add(new ComboBoxEntry(9, "When (and if) issued"));
		typeComboEntries.add(new ComboBoxEntry(10, "Original issue discount"));
		typeComboEntries.add(new ComboBoxEntry(11, "Callable, puttable"));
		typeComboEntries.add(new ComboBoxEntry(12, "Escrowed to Maturity"));
		typeComboEntries.add(new ComboBoxEntry(13, "Escrowed to redemption date"));
		typeComboEntries.add(new ComboBoxEntry(14, "Pre-refunded"));
		typeComboEntries.add(new ComboBoxEntry(15, "In default"));
		typeComboEntries.add(new ComboBoxEntry(16, "Unrated"));
		typeComboEntries.add(new ComboBoxEntry(17, "Taxable"));
		typeComboEntries.add(new ComboBoxEntry(18, "Indexed"));
		typeComboEntries.add(new ComboBoxEntry(19, "Subject To Alternative Minimum Tax"));
		typeComboEntries.add(new ComboBoxEntry(20, "Original issue discount price"));
		typeComboEntries.add(new ComboBoxEntry(21, "Callable below maturity value"));
		typeComboEntries.add(new ComboBoxEntry(22, "Callable without notice"));
		typeComboEntries.add(new ComboBoxEntry(23, "Price tick rules for security"));
		typeComboEntries.add(new ComboBoxEntry(24, "Trade type eligibility details for security"));
		typeComboEntries.add(new ComboBoxEntry(25, "Instrument Denominator"));
		typeComboEntries.add(new ComboBoxEntry(26, "Instrument Numerator"));
		typeComboEntries.add(new ComboBoxEntry(27, "Instrument Price Precision"));
		typeComboEntries.add(new ComboBoxEntry(28, "Instrument Strike Price"));
		typeComboEntries.add(new ComboBoxEntry(29, "Tradeable Indicator"));
		typeComboEntries.add(new ComboBoxEntry(99, "Text"));

		typeComboBox = new JComboBox(typeComboEntries.toArray());
		typeComboBox.setMinimumSize(new Dimension(32, 25));
		typeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		typeComboBox.setPreferredSize(new Dimension(32, 25));
		typeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.gridwidth = 3;
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(25, 0, 5, 25);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 0;
		contentPanel.add(typeComboBox, gbc_localeOfissueTextField);
		
		if(securityAttribute!=null)
			typeComboBox.setSelectedItem(new ComboBoxEntry(securityAttribute.getAttributeType(), null));
		else
			typeComboBox.setSelectedIndex(0);
		
		typeComboBox.addActionListener(actionListener);

		JLabel eventTextLabel = new JLabel("Value");
		eventTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventTextLabel = new GridBagConstraints();
		gbc_eventTextLabel.anchor = GridBagConstraints.WEST;
		gbc_eventTextLabel.insets = new Insets(0, 25, 5, 15);
		gbc_eventTextLabel.gridx = 0;
		gbc_eventTextLabel.gridy = 1;
		contentPanel.add(eventTextLabel, gbc_eventTextLabel);

		valueTextField = new JTextField();
		valueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		valueTextField.setBackground(new Color(255, 243, 204));
		valueTextField.setPreferredSize(new Dimension(200, 25));
		GridBagConstraints gbc_valueTextField = new GridBagConstraints();
		gbc_valueTextField.gridwidth = 2;
		gbc_valueTextField.insets = new Insets(0, 0, 5, 25);
		gbc_valueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_valueTextField.gridx = 2;
		gbc_valueTextField.gridy = 1;
		contentPanel.add(valueTextField, gbc_valueTextField);
		valueTextField.setColumns(10);
		
		if(securityAttribute!=null)
			valueTextField.setText(securityAttribute.getAttributeValue());
		
		valueTextField.getDocument().addDocumentListener(documentListener);
		
		okButton = new JButton();

		if (this.securityAttribute == null) {
			this.securityAttribute = new SecurityAttribute();
			setTitle("Add Security Attribute");
			typeComboBox.setSelectedIndex(19);
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Attribute");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 2;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
				
				SecurityAttributeDialog.this.securityAttribute.setAttributeType((Integer) ((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry());
				
				SecurityAttributeDialog.this.securityAttribute.setAttributeValue(valueTextField.getText());
				
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
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
		
		boolean consistent = true;
		
		if (securityAttribute.getAttributeType() != null && !securityAttribute.getAttributeType().equals(((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityAttribute.getAttributeType() == null )
				&& typeComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		if(dirtyFieldCheck(securityAttribute.getAttributeValue(), valueTextField))
			dirty = true;
		
		
		okButton.setEnabled(consistent&&dirty);

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
	 * Gets the security attribute.
	 *
	 * @return the security attribute
	 */
	public SecurityAttribute getSecurityAttribute() {
		return securityAttribute;
	}

}
