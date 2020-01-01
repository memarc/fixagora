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
import java.text.ParseException;
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
import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityLeg;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class SecurityLegDialog.
 */
public class SecurityLegDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField ratioQuantityTextField;
	private boolean cancelled = true;
	private JButton okButton;
	private SecurityLeg securityLeg = null;
	private JTextField legTextField;
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");
	private boolean dirty;
	private JComboBox sideComboBox;
	private JTextField optionRatioTextField;
	private JButton btnNewButton;
	private SecurityTreeDialog securityTreeDialog;

	/**
	 * Instantiates a new security leg dialog.
	 *
	 * @param securityLeg the security leg
	 * @param securityEditor the security editor
	 */
	public SecurityLegDialog(SecurityLeg securityLeg, final FSecurityEditor securityEditor) {

		this.securityLeg = securityLeg;
		this.securityTreeDialog = securityEditor.getMainPanel().getSecurityTreeDialog();
		setBounds(100, 100, 487, 240);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityLegDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel legSecurityLabel = new JLabel("Leg Security");
		legSecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 0;
		contentPanel.add(legSecurityLabel, gbc_legSecurityLabel);

		legTextField = new JTextField();
		legTextField.setPreferredSize(new Dimension(200, 25));
		legTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		legTextField.setBackground(new Color(255, 243, 204));
		legTextField.setColumns(10);

		AutoCompleteDecorator.decorate(legTextField, securityTreeDialog.getSecurityList(), true);
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.gridwidth = 2;
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(25, 0, 5, 5);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 0;
		contentPanel.add(legTextField, gbc_localeOfissueTextField);

		btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(25, 0, 5, 25);
		gbc_btnNewButton.gridx = 4;
		gbc_btnNewButton.gridy = 0;
		contentPanel.add(btnNewButton, gbc_btnNewButton);

		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				securityTreeDialog.setVisible(true);
				if (securityTreeDialog.getSecurity() != null && !securityTreeDialog.isCancelled())
					legTextField.setText(securityTreeDialog.getSecurity().getName());
			}
		});

		if (securityLeg != null && securityLeg.getLegSecurity() != null)
		{
			FSecurity security = (FSecurity)securityEditor.getMainPanel().getAbstractBusinessObjectForId(securityLeg.getLegSecurity().getId());
			legTextField.setText(security.getName());
		}
		
		legTextField.getDocument().addDocumentListener(documentListener);

		JLabel ratioQuantityLabel = new JLabel("Ratio Quantity");
		ratioQuantityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_ratioQuantityLabel = new GridBagConstraints();
		gbc_ratioQuantityLabel.anchor = GridBagConstraints.WEST;
		gbc_ratioQuantityLabel.insets = new Insets(0, 25, 5, 15);
		gbc_ratioQuantityLabel.gridx = 0;
		gbc_ratioQuantityLabel.gridy = 1;
		contentPanel.add(ratioQuantityLabel, gbc_ratioQuantityLabel);

		ratioQuantityTextField = new JTextField();
		ratioQuantityTextField.setPreferredSize(new Dimension(200, 25));
		ratioQuantityTextField.setDocument(new DoubleDocument());
		ratioQuantityTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		ratioQuantityTextField.setBackground(new Color(255, 243, 204));
		ratioQuantityTextField.setColumns(10);
		GridBagConstraints gbc_ratioQuantityTextField = new GridBagConstraints();
		gbc_ratioQuantityTextField.gridwidth = 3;
		gbc_ratioQuantityTextField.insets = new Insets(0, 0, 5, 25);
		gbc_ratioQuantityTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ratioQuantityTextField.gridx = 2;
		gbc_ratioQuantityTextField.gridy = 1;
		contentPanel.add(ratioQuantityTextField, gbc_ratioQuantityTextField);

		if (securityLeg != null && securityLeg.getRatioQuantity() != null)
			ratioQuantityTextField.setText(doubleFormat.format(securityLeg.getRatioQuantity()));

		ratioQuantityTextField.getDocument().addDocumentListener(documentListener);

		JLabel optionRatioLabel = new JLabel("Option Ratio");
		optionRatioLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_optionRatioLabel = new GridBagConstraints();
		gbc_optionRatioLabel.anchor = GridBagConstraints.WEST;
		gbc_optionRatioLabel.insets = new Insets(0, 25, 5, 15);
		gbc_optionRatioLabel.gridx = 0;
		gbc_optionRatioLabel.gridy = 2;
		contentPanel.add(optionRatioLabel, gbc_optionRatioLabel);

		optionRatioTextField = new JTextField();
		optionRatioTextField.setPreferredSize(new Dimension(200, 25));
		optionRatioTextField.setDocument(new DoubleDocument());
		optionRatioTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		optionRatioTextField.setBackground(new Color(255, 243, 204));
		optionRatioTextField.setColumns(10);
		GridBagConstraints gbc_dirtyPriceTextField = new GridBagConstraints();
		gbc_dirtyPriceTextField.gridwidth = 3;
		gbc_dirtyPriceTextField.insets = new Insets(0, 0, 5, 25);
		gbc_dirtyPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_dirtyPriceTextField.gridx = 2;
		gbc_dirtyPriceTextField.gridy = 2;
		contentPanel.add(optionRatioTextField, gbc_dirtyPriceTextField);

		if (securityLeg != null && securityLeg.getOptionRatio() != null)
			optionRatioTextField.setText(doubleFormat.format(securityLeg.getOptionRatio()));

		optionRatioTextField.getDocument().addDocumentListener(documentListener);

		JLabel sideLabel = new JLabel("Side");
		sideLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sideLabel = new GridBagConstraints();
		gbc_sideLabel.anchor = GridBagConstraints.WEST;
		gbc_sideLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sideLabel.gridx = 0;
		gbc_sideLabel.gridy = 3;
		contentPanel.add(sideLabel, gbc_sideLabel);

		List<ComboBoxEntry> sideComboEntries = new ArrayList<ComboBoxEntry>();
		sideComboEntries.add(new ComboBoxEntry());
		sideComboEntries.add(new ComboBoxEntry("1", "Buy"));
		sideComboEntries.add(new ComboBoxEntry("2", "Sell"));
		sideComboEntries.add(new ComboBoxEntry("3", "Buy minus"));
		sideComboEntries.add(new ComboBoxEntry("4", "Sell plus"));
		sideComboEntries.add(new ComboBoxEntry("5", "Sell short"));
		sideComboEntries.add(new ComboBoxEntry("6", "Sell short exempt"));
		sideComboEntries.add(new ComboBoxEntry("7", "Undisclosed"));
		sideComboEntries.add(new ComboBoxEntry("8", "Cross"));
		sideComboEntries.add(new ComboBoxEntry("9", "Cross short"));
		sideComboEntries.add(new ComboBoxEntry("A", "Cross short exempt"));
		sideComboEntries.add(new ComboBoxEntry("B", "As Defined"));
		sideComboEntries.add(new ComboBoxEntry("C", "Opposite"));
		sideComboEntries.add(new ComboBoxEntry("D", "Subscribe"));
		sideComboEntries.add(new ComboBoxEntry("E", "Redeem"));
		sideComboEntries.add(new ComboBoxEntry("F", "Lend"));
		sideComboEntries.add(new ComboBoxEntry("G", "Borrow"));

		sideComboBox = new JComboBox(sideComboEntries.toArray());
		sideComboBox.setMinimumSize(new Dimension(32, 25));
		sideComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sideComboBox.setPreferredSize(new Dimension(32, 25));
		sideComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_settlementTypeComboBox = new GridBagConstraints();
		gbc_settlementTypeComboBox.gridwidth = 3;
		gbc_settlementTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_settlementTypeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_settlementTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settlementTypeComboBox.gridx = 2;
		gbc_settlementTypeComboBox.gridy = 3;
		contentPanel.add(sideComboBox, gbc_settlementTypeComboBox);

		if (securityLeg != null)
			sideComboBox.setSelectedItem(new ComboBoxEntry(securityLeg.getSide(), null));
		else
			sideComboBox.setSelectedIndex(0);

		sideComboBox.addActionListener(actionListener);

		okButton = new JButton();

		if (this.securityLeg == null) {
			this.securityLeg = new SecurityLeg();
			setTitle("Add Security Leg");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Leg");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 4;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;

				if (legTextField.getText().trim().length() > 0)
					SecurityLegDialog.this.securityLeg.setLegSecurity(securityTreeDialog.getSecurityForName(legTextField.getText()));

				else
					SecurityLegDialog.this.securityLeg.setLegSecurity(null);

				if (ratioQuantityTextField.getText().trim().length() > 0)
					SecurityLegDialog.this.securityLeg.setRatioQuantity(Double.parseDouble(ratioQuantityTextField.getText()));

				else
					SecurityLegDialog.this.securityLeg.setRatioQuantity(null);

				if (optionRatioTextField.getText().trim().length() > 0)
					SecurityLegDialog.this.securityLeg.setOptionRatio(Double.parseDouble(optionRatioTextField.getText()));

				else
					SecurityLegDialog.this.securityLeg.setOptionRatio(null);

				SecurityLegDialog.this.securityLeg.setSide((String) ((ComboBoxEntry) sideComboBox.getSelectedItem()).getEntry());

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
		gbc_cancelButton.gridy = 4;
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

	private boolean dirtyFieldCheck(Double value, JTextField jTextField) {

		Number value2 = null;

		try {

			value2 = doubleFormat.parse(jTextField.getText()).doubleValue();
		}
		catch (ParseException e) {
		}

		if (value != null && value2 == null)
			return true;

		else if (value == null && value2 != null)
			return true;

		else if (value == null && value2 == null)
			return false;

		return (!value.equals(value2));
	}

	private void checkConsistency() {

		dirty = false;

		FSecurity security = securityTreeDialog.getSecurityForName(legTextField.getText());

		if (security != null) {

			if (securityLeg.getLegSecurity() != null && !securityLeg.getLegSecurity().equals(security))
				dirty = true;

			if ((securityLeg.getLegSecurity() == null) && security != null)
				dirty = true;

			if (dirtyFieldCheck(securityLeg.getRatioQuantity(), ratioQuantityTextField))
				dirty = true;

			if (dirtyFieldCheck(securityLeg.getOptionRatio(), optionRatioTextField))
				dirty = true;

			if (securityLeg.getSide() != null && !securityLeg.getSide().equals(((ComboBoxEntry) sideComboBox.getSelectedItem()).getEntry()))
				dirty = true;

			if ((securityLeg.getSide() == null) && sideComboBox.getSelectedIndex() > 0)
				dirty = true;
		}

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
	 * Gets the security leg.
	 *
	 * @return the security leg
	 */
	public SecurityLeg getSecurityLeg() {

		return securityLeg;
	}
}
