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
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityUnderlying;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class SecurityUnderlyingDialog.
 */
public class SecurityUnderlyingDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private SecurityUnderlying securityUnderlying = null;
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");
	private boolean dirty;
	private JComboBox cashTypeComboBox;
	private JTextField cashAmountLabelTextField;
	private JComboBox settlementTypeComboBox;
	private JTextField allocationPercentTextField;
	private JTextField endValueTextField;
	private JTextField startValueTextField;
	private JTextField endPriceTextField;
	private JTextField legTextField;
	private JButton btnNewButton;
	private SecurityTreeDialog securityTreeDialog;

	/**
	 * Instantiates a new security underlying dialog.
	 *
	 * @param securityUnderlying the security underlying
	 * @param securityEditor the security editor
	 */
	public SecurityUnderlyingDialog(SecurityUnderlying securityUnderlying, final FSecurityEditor securityEditor) {

		this.securityUnderlying = securityUnderlying;
		this.securityTreeDialog = securityEditor.getMainPanel().getSecurityTreeDialog();
		setBounds(100, 100, 487, 383);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityUnderlyingDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png"))
				.getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel underlyingLabel = new JLabel("Underlying");
		underlyingLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_typeLabel = new GridBagConstraints();
		gbc_typeLabel.anchor = GridBagConstraints.WEST;
		gbc_typeLabel.insets = new Insets(25, 25, 5, 5);
		gbc_typeLabel.gridx = 0;
		gbc_typeLabel.gridy = 0;
		contentPanel.add(underlyingLabel, gbc_typeLabel);

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

		if (securityUnderlying != null && securityUnderlying.getUnderlyingSecurity() != null)
		{
			FSecurity security = (FSecurity)securityEditor.getMainPanel().getAbstractBusinessObjectForId(securityUnderlying.getUnderlyingSecurity().getId());
			legTextField.setText(security.getName());
		}

		legTextField.getDocument().addDocumentListener(documentListener);

		JLabel endPriceLabel = new JLabel("End Price");
		endPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_endPriceLabel = new GridBagConstraints();
		gbc_endPriceLabel.anchor = GridBagConstraints.WEST;
		gbc_endPriceLabel.insets = new Insets(0, 25, 5, 15);
		gbc_endPriceLabel.gridx = 0;
		gbc_endPriceLabel.gridy = 1;
		contentPanel.add(endPriceLabel, gbc_endPriceLabel);

		endPriceTextField = new JTextField();
		endPriceTextField.setPreferredSize(new Dimension(200, 25));
		endPriceTextField.setDocument(new DoubleDocument());
		endPriceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		endPriceTextField.setBackground(new Color(255, 243, 204));
		endPriceTextField.setColumns(10);
		GridBagConstraints gbc_endPriceTextField = new GridBagConstraints();
		gbc_endPriceTextField.gridwidth = 3;
		gbc_endPriceTextField.insets = new Insets(0, 0, 5, 25);
		gbc_endPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_endPriceTextField.gridx = 2;
		gbc_endPriceTextField.gridy = 1;
		contentPanel.add(endPriceTextField, gbc_endPriceTextField);

		if (securityUnderlying != null && securityUnderlying.getEndPrice() != null)
			endPriceTextField.setText(doubleFormat.format(securityUnderlying.getEndPrice()));

		endPriceTextField.getDocument().addDocumentListener(documentListener);

		JLabel startValueLabel = new JLabel("Start Value");
		startValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_startValueLabel = new GridBagConstraints();
		gbc_startValueLabel.anchor = GridBagConstraints.WEST;
		gbc_startValueLabel.insets = new Insets(0, 25, 5, 15);
		gbc_startValueLabel.gridx = 0;
		gbc_startValueLabel.gridy = 2;
		contentPanel.add(startValueLabel, gbc_startValueLabel);

		startValueTextField = new JTextField();
		startValueTextField.setPreferredSize(new Dimension(200, 25));
		startValueTextField.setDocument(new DoubleDocument());
		startValueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		startValueTextField.setBackground(new Color(255, 243, 204));
		startValueTextField.setColumns(10);
		GridBagConstraints gbc_startValueTextField = new GridBagConstraints();
		gbc_startValueTextField.gridwidth = 3;
		gbc_startValueTextField.insets = new Insets(0, 0, 5, 25);
		gbc_startValueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_startValueTextField.gridx = 2;
		gbc_startValueTextField.gridy = 2;
		contentPanel.add(startValueTextField, gbc_startValueTextField);

		if (securityUnderlying != null && securityUnderlying.getStartValue() != null)
			startValueTextField.setText(doubleFormat.format(securityUnderlying.getStartValue()));

		startValueTextField.getDocument().addDocumentListener(documentListener);

		JLabel endValueLabel = new JLabel("End Value");
		endValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_endValueLabel = new GridBagConstraints();
		gbc_endValueLabel.anchor = GridBagConstraints.WEST;
		gbc_endValueLabel.insets = new Insets(0, 25, 5, 15);
		gbc_endValueLabel.gridx = 0;
		gbc_endValueLabel.gridy = 3;
		contentPanel.add(endValueLabel, gbc_endValueLabel);

		endValueTextField = new JTextField();
		endValueTextField.setPreferredSize(new Dimension(200, 25));
		endValueTextField.setDocument(new DoubleDocument());
		endValueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		endValueTextField.setBackground(new Color(255, 243, 204));
		endValueTextField.setColumns(10);
		GridBagConstraints gbc_endValueTextField = new GridBagConstraints();
		gbc_endValueTextField.gridwidth = 3;
		gbc_endValueTextField.insets = new Insets(0, 0, 5, 25);
		gbc_endValueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_endValueTextField.gridx = 2;
		gbc_endValueTextField.gridy = 3;
		contentPanel.add(endValueTextField, gbc_endValueTextField);

		if (securityUnderlying != null && securityUnderlying.getEndValue() != null)
			endValueTextField.setText(doubleFormat.format(securityUnderlying.getEndValue()));

		endValueTextField.getDocument().addDocumentListener(documentListener);

		JLabel allocationPercentLabel = new JLabel("Allocation Percent");
		allocationPercentLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_allocationPercentLabel = new GridBagConstraints();
		gbc_allocationPercentLabel.anchor = GridBagConstraints.WEST;
		gbc_allocationPercentLabel.insets = new Insets(0, 25, 5, 15);
		gbc_allocationPercentLabel.gridx = 0;
		gbc_allocationPercentLabel.gridy = 4;
		contentPanel.add(allocationPercentLabel, gbc_allocationPercentLabel);

		allocationPercentTextField = new JTextField();
		allocationPercentTextField.setPreferredSize(new Dimension(200, 25));
		allocationPercentTextField.setDocument(new DoubleDocument());
		allocationPercentTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		allocationPercentTextField.setBackground(new Color(255, 243, 204));
		allocationPercentTextField.setColumns(10);
		GridBagConstraints gbc_allocationPercentTextField = new GridBagConstraints();
		gbc_allocationPercentTextField.gridwidth = 3;
		gbc_allocationPercentTextField.insets = new Insets(0, 0, 5, 25);
		gbc_allocationPercentTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_allocationPercentTextField.gridx = 2;
		gbc_allocationPercentTextField.gridy = 4;
		contentPanel.add(allocationPercentTextField, gbc_allocationPercentTextField);

		if (securityUnderlying != null && securityUnderlying.getAllocationPercent() != null)
			allocationPercentTextField.setText(doubleFormat.format(securityUnderlying.getAllocationPercent()));

		allocationPercentTextField.getDocument().addDocumentListener(documentListener);

		JLabel settlementTypeLabel = new JLabel("Settlement Type");
		settlementTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_settlementTypeLabel = new GridBagConstraints();
		gbc_settlementTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_settlementTypeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_settlementTypeLabel.gridx = 0;
		gbc_settlementTypeLabel.gridy = 5;
		contentPanel.add(settlementTypeLabel, gbc_settlementTypeLabel);

		List<ComboBoxEntry> settlementTypeComboEntries = new ArrayList<ComboBoxEntry>();
		settlementTypeComboEntries.add(new ComboBoxEntry());
		settlementTypeComboEntries.add(new ComboBoxEntry(2, "T+1"));
		settlementTypeComboEntries.add(new ComboBoxEntry(4, "T+3"));
		settlementTypeComboEntries.add(new ComboBoxEntry(5, "T+4"));

		settlementTypeComboBox = new JComboBox(settlementTypeComboEntries.toArray());
		settlementTypeComboBox.setMinimumSize(new Dimension(32, 25));
		settlementTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		settlementTypeComboBox.setPreferredSize(new Dimension(32, 25));
		settlementTypeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_settlementTypeComboBox = new GridBagConstraints();
		gbc_settlementTypeComboBox.gridwidth = 3;
		gbc_settlementTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_settlementTypeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_settlementTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settlementTypeComboBox.gridx = 2;
		gbc_settlementTypeComboBox.gridy = 5;
		contentPanel.add(settlementTypeComboBox, gbc_settlementTypeComboBox);

		if (securityUnderlying != null)
			settlementTypeComboBox.setSelectedItem(new ComboBoxEntry(securityUnderlying.getUnderlyingSecurity(), null));
		else
			settlementTypeComboBox.setSelectedIndex(0);

		settlementTypeComboBox.addActionListener(actionListener);

		JLabel cashAmountLabel = new JLabel("Cash Amount");
		cashAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_cashAmountLabel = new GridBagConstraints();
		gbc_cashAmountLabel.anchor = GridBagConstraints.WEST;
		gbc_cashAmountLabel.insets = new Insets(0, 25, 5, 15);
		gbc_cashAmountLabel.gridx = 0;
		gbc_cashAmountLabel.gridy = 6;
		contentPanel.add(cashAmountLabel, gbc_cashAmountLabel);

		cashAmountLabelTextField = new JTextField();
		cashAmountLabelTextField.setPreferredSize(new Dimension(200, 25));
		cashAmountLabelTextField.setDocument(new DoubleDocument());
		cashAmountLabelTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		cashAmountLabelTextField.setBackground(new Color(255, 243, 204));
		cashAmountLabelTextField.setColumns(10);
		GridBagConstraints gbc_cashAmountLabelTextField = new GridBagConstraints();
		gbc_cashAmountLabelTextField.gridwidth = 3;
		gbc_cashAmountLabelTextField.insets = new Insets(0, 0, 5, 25);
		gbc_cashAmountLabelTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_cashAmountLabelTextField.gridx = 2;
		gbc_cashAmountLabelTextField.gridy = 6;
		contentPanel.add(cashAmountLabelTextField, gbc_cashAmountLabelTextField);

		if (securityUnderlying != null && securityUnderlying.getCashAmount() != null)
			cashAmountLabelTextField.setText(doubleFormat.format(securityUnderlying.getCashAmount()));

		cashAmountLabelTextField.getDocument().addDocumentListener(documentListener);

		JLabel cashTypeLabel = new JLabel("Cash Type");
		cashTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_cashTypeLabel = new GridBagConstraints();
		gbc_cashTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_cashTypeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_cashTypeLabel.gridx = 0;
		gbc_cashTypeLabel.gridy = 7;
		contentPanel.add(cashTypeLabel, gbc_cashTypeLabel);

		List<ComboBoxEntry> cashTypeComboEntries = new ArrayList<ComboBoxEntry>();
		cashTypeComboEntries.add(new ComboBoxEntry());
		cashTypeComboEntries.add(new ComboBoxEntry("FIXED", "FIXED"));
		cashTypeComboEntries.add(new ComboBoxEntry("DIFF", "DIFF"));

		cashTypeComboBox = new JComboBox(cashTypeComboEntries.toArray());
		cashTypeComboBox.setMinimumSize(new Dimension(32, 25));
		cashTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		cashTypeComboBox.setPreferredSize(new Dimension(32, 25));
		cashTypeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_cashTypeComboBox = new GridBagConstraints();
		gbc_cashTypeComboBox.gridwidth = 3;
		gbc_cashTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_cashTypeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_cashTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_cashTypeComboBox.gridx = 2;
		gbc_cashTypeComboBox.gridy = 7;
		contentPanel.add(cashTypeComboBox, gbc_cashTypeComboBox);

		if (securityUnderlying != null)
			cashTypeComboBox.setSelectedItem(new ComboBoxEntry(securityUnderlying.getCashType(), null));
		else
			cashTypeComboBox.setSelectedIndex(0);

		cashTypeComboBox.addActionListener(actionListener);

		okButton = new JButton();

		if (this.securityUnderlying == null) {
			this.securityUnderlying = new SecurityUnderlying();
			setTitle("Add Security Event");
			cashTypeComboBox.setSelectedIndex(0);
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Event");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 8;
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
					SecurityUnderlyingDialog.this.securityUnderlying.setUnderlyingSecurity(securityTreeDialog.getSecurityForName(legTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setUnderlyingSecurity(null);

				if (endPriceTextField.getText().trim().length() > 0)
					SecurityUnderlyingDialog.this.securityUnderlying.setEndPrice(Double.parseDouble(endPriceTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setEndPrice(null);

				if (startValueTextField.getText().trim().length() > 0)
					SecurityUnderlyingDialog.this.securityUnderlying.setStartValue(Double.parseDouble(startValueTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setStartValue(null);

				if (endValueTextField.getText().trim().length() > 0)
					SecurityUnderlyingDialog.this.securityUnderlying.setEndValue(Double.parseDouble(endValueTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setEndValue(null);

				if (allocationPercentTextField.getText().trim().length() > 0)
					SecurityUnderlyingDialog.this.securityUnderlying.setAllocationPercent(Double.parseDouble(allocationPercentTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setAllocationPercent(null);

				SecurityUnderlyingDialog.this.securityUnderlying.setSettlementType((Integer) ((ComboBoxEntry) settlementTypeComboBox.getSelectedItem())
						.getEntry());

				if (cashAmountLabelTextField.getText().trim().length() > 0)
					SecurityUnderlyingDialog.this.securityUnderlying.setCashAmount(Double.parseDouble(cashAmountLabelTextField.getText()));

				else
					SecurityUnderlyingDialog.this.securityUnderlying.setCashAmount(null);

				SecurityUnderlyingDialog.this.securityUnderlying.setCashType((String) ((ComboBoxEntry) cashTypeComboBox.getSelectedItem()).getEntry());

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridwidth = 2;
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 8;
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

			if (securityUnderlying.getUnderlyingSecurity() != null && !securityUnderlying.getUnderlyingSecurity().equals(security))
				dirty = true;

			if ((securityUnderlying.getUnderlyingSecurity() == null) && security != null)
				dirty = true;

			if (dirtyFieldCheck(securityUnderlying.getEndPrice(), endPriceTextField))
				dirty = true;

			if (dirtyFieldCheck(securityUnderlying.getStartValue(), startValueTextField))
				dirty = true;

			if (dirtyFieldCheck(securityUnderlying.getEndValue(), endValueTextField))
				dirty = true;

			if (dirtyFieldCheck(securityUnderlying.getAllocationPercent(), allocationPercentTextField))
				dirty = true;

			if (securityUnderlying.getSettlementType() != null
					&& !securityUnderlying.getSettlementType().equals(((ComboBoxEntry) settlementTypeComboBox.getSelectedItem()).getEntry()))
				dirty = true;

			if ((securityUnderlying.getSettlementType() == null) && settlementTypeComboBox.getSelectedIndex() >= 0)
				dirty = true;

			if (dirtyFieldCheck(securityUnderlying.getCashAmount(), cashAmountLabelTextField))
				dirty = true;

			if (securityUnderlying.getCashType() != null
					&& !securityUnderlying.getCashType().equals(((ComboBoxEntry) cashTypeComboBox.getSelectedItem()).getEntry()))
				dirty = true;

			if ((securityUnderlying.getCashType() == null) && cashTypeComboBox.getSelectedIndex() >= 0)
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
	 * Gets the security underlying.
	 *
	 * @return the security underlying
	 */
	public SecurityUnderlying getSecurityUnderlying() {

		return securityUnderlying;
	}

}
