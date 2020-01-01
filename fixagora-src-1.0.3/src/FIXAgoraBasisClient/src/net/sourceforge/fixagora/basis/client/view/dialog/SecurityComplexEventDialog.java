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
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityComplexEvent;

/**
 * The Class SecurityComplexEventDialog.
 */
public class SecurityComplexEventDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField optionPayoutAmountTextField;
	private boolean cancelled = true;
	private JButton okButton;
	private SecurityComplexEvent securityComplexEvent = null;
	private JComboBox typeComboBox;
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");
	private boolean dirty;
	private JTextField priceTextField;
	private JComboBox priceBoundaryMethodComboBox;
	private JComboBox priceTimeTypeComboBox;
	private JComboBox eventConditionComboBox;
	private JTextField priceBoundaryPrecisionTextField;	

	/**
	 * Instantiates a new security complex event dialog.
	 *
	 * @param securityEvent the security event
	 */
	public SecurityComplexEventDialog(SecurityComplexEvent securityEvent) {

		this.securityComplexEvent = securityEvent;
		setBounds(100, 100, 607, 366);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityComplexEventDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
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
		typeComboEntries.add(new ComboBoxEntry(1, "Capped"));
		typeComboEntries.add(new ComboBoxEntry(2, "Trigger"));
		typeComboEntries.add(new ComboBoxEntry(3, "Knock-in up"));
		typeComboEntries.add(new ComboBoxEntry(4, "Kock-in down"));
		typeComboEntries.add(new ComboBoxEntry(5, "Knock-out up"));
		typeComboEntries.add(new ComboBoxEntry(6, "Knock-out down"));
		typeComboEntries.add(new ComboBoxEntry(7, "Underlying"));
		typeComboEntries.add(new ComboBoxEntry(8, "Reset Barrier"));
		typeComboEntries.add(new ComboBoxEntry(9, "Rolling Barrier"));

		typeComboBox = new JComboBox(typeComboEntries.toArray());
		typeComboBox.setMinimumSize(new Dimension(32, 25));
		typeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		typeComboBox.setPreferredSize(new Dimension(32, 25));
		typeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_typeComboBox = new GridBagConstraints();
		gbc_typeComboBox.gridwidth = 3;
		gbc_typeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_typeComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_typeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeComboBox.gridx = 2;
		gbc_typeComboBox.gridy = 0;
		contentPanel.add(typeComboBox, gbc_typeComboBox);
		
		if(securityEvent!=null)
			typeComboBox.setSelectedItem(new ComboBoxEntry(securityEvent.getEventType(), null));
		else
			typeComboBox.setSelectedIndex(0);
		
		typeComboBox.addActionListener(actionListener);


		JLabel optionpayoutAmountLabel = new JLabel("Option Payout Amount");
		optionpayoutAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_optionpayoutAmountLabel = new GridBagConstraints();
		gbc_optionpayoutAmountLabel.anchor = GridBagConstraints.WEST;
		gbc_optionpayoutAmountLabel.insets = new Insets(0, 25, 5, 15);
		gbc_optionpayoutAmountLabel.gridx = 0;
		gbc_optionpayoutAmountLabel.gridy = 1;
		contentPanel.add(optionpayoutAmountLabel, gbc_optionpayoutAmountLabel);

		optionPayoutAmountTextField = new JTextField();
		optionPayoutAmountTextField.setPreferredSize(new Dimension(200, 25));
		optionPayoutAmountTextField.setDocument(new DoubleDocument());
		optionPayoutAmountTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		optionPayoutAmountTextField.setBackground(new Color(255, 243, 204));
		optionPayoutAmountTextField.setColumns(10);
		GridBagConstraints gbc_optionpayoutAmountTextField = new GridBagConstraints();
		gbc_optionpayoutAmountTextField.gridwidth = 2;
		gbc_optionpayoutAmountTextField.insets = new Insets(0, 0, 5, 25);
		gbc_optionpayoutAmountTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_optionpayoutAmountTextField.gridx = 2;
		gbc_optionpayoutAmountTextField.gridy = 1;
		contentPanel.add(optionPayoutAmountTextField, gbc_optionpayoutAmountTextField);
		
		if(securityEvent!=null&&securityEvent.getEventPrice()!=null)
			optionPayoutAmountTextField.setText(doubleFormat.format(securityEvent.getOptionPayoutAmount()));
		
		optionPayoutAmountTextField.getDocument().addDocumentListener(documentListener);	
		
		JLabel priceLabel = new JLabel("Price");
		priceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceLabel = new GridBagConstraints();
		gbc_priceLabel.anchor = GridBagConstraints.WEST;
		gbc_priceLabel.insets = new Insets(0, 25, 5, 15);
		gbc_priceLabel.gridx = 0;
		gbc_priceLabel.gridy = 2;
		contentPanel.add(priceLabel, gbc_priceLabel);

		priceTextField = new JTextField();
		priceTextField.setPreferredSize(new Dimension(200, 25));
		priceTextField.setDocument(new DoubleDocument());
		priceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		priceTextField.setBackground(new Color(255, 243, 204));
		priceTextField.setColumns(10);
		GridBagConstraints gbc_priceTextField = new GridBagConstraints();
		gbc_priceTextField.gridwidth = 2;
		gbc_priceTextField.insets = new Insets(0, 0, 5, 25);
		gbc_priceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceTextField.gridx = 2;
		gbc_priceTextField.gridy = 2;
		contentPanel.add(priceTextField, gbc_priceTextField);
		
		if(securityEvent!=null&&securityEvent.getEventPrice()!=null)
			priceTextField.setText(doubleFormat.format(securityEvent.getEventPrice()));
		
		priceTextField.getDocument().addDocumentListener(documentListener);

		JLabel priceBoundaryMethodLabel = new JLabel("Price Boundary Method");
		priceBoundaryMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceBoundaryMethodLabel = new GridBagConstraints();
		gbc_priceBoundaryMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_priceBoundaryMethodLabel.insets = new Insets(0, 25, 5, 5);
		gbc_priceBoundaryMethodLabel.gridx = 0;
		gbc_priceBoundaryMethodLabel.gridy = 3;
		contentPanel.add(priceBoundaryMethodLabel, gbc_priceBoundaryMethodLabel);

		List<ComboBoxEntry> priceBoundaryMethodEntries = new ArrayList<ComboBoxEntry>();
		priceBoundaryMethodEntries.add(new ComboBoxEntry());
		priceBoundaryMethodEntries.add(new ComboBoxEntry(1, "Less than complex event price"));
		priceBoundaryMethodEntries.add(new ComboBoxEntry(2, "Less than or equal to complex event price"));
		priceBoundaryMethodEntries.add(new ComboBoxEntry(3, "Equal to complex event price"));
		priceBoundaryMethodEntries.add(new ComboBoxEntry(4, "Greater than or equal to complex event price"));
		priceBoundaryMethodEntries.add(new ComboBoxEntry(5, "Greater than complex event price"));

		priceBoundaryMethodComboBox = new JComboBox(priceBoundaryMethodEntries.toArray());
		priceBoundaryMethodComboBox.setMinimumSize(new Dimension(32, 25));
		priceBoundaryMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		priceBoundaryMethodComboBox.setPreferredSize(new Dimension(32, 25));
		priceBoundaryMethodComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_priceBoundaryMethodComboBox = new GridBagConstraints();
		gbc_priceBoundaryMethodComboBox.gridwidth = 3;
		gbc_priceBoundaryMethodComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_priceBoundaryMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceBoundaryMethodComboBox.gridx = 2;
		gbc_priceBoundaryMethodComboBox.gridy = 3;
		contentPanel.add(priceBoundaryMethodComboBox, gbc_priceBoundaryMethodComboBox);
		
		if(securityEvent!=null)
			priceBoundaryMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEvent.getEventPriceBoundaryMethod(), null));
		
		priceBoundaryMethodComboBox.addActionListener(actionListener);

		
		JLabel priceBoundaryPrecisionLabel = new JLabel("Price Boundary Precision");
		priceBoundaryPrecisionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceBoundaryPrecisionLabel = new GridBagConstraints();
		gbc_priceBoundaryPrecisionLabel.anchor = GridBagConstraints.WEST;
		gbc_priceBoundaryPrecisionLabel.insets = new Insets(0, 25, 5, 15);
		gbc_priceBoundaryPrecisionLabel.gridx = 0;
		gbc_priceBoundaryPrecisionLabel.gridy = 4;
		contentPanel.add(priceBoundaryPrecisionLabel, gbc_priceBoundaryPrecisionLabel);

		priceBoundaryPrecisionTextField = new JTextField();
		priceBoundaryPrecisionTextField.setPreferredSize(new Dimension(200, 25));
		priceBoundaryPrecisionTextField.setDocument(new DoubleDocument());
		priceBoundaryPrecisionTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		priceBoundaryPrecisionTextField.setBackground(new Color(255, 243, 204));
		priceBoundaryPrecisionTextField.setColumns(10);
		GridBagConstraints gbc_priceBoundaryPrecisionTextField = new GridBagConstraints();
		gbc_priceBoundaryPrecisionTextField.gridwidth = 2;
		gbc_priceBoundaryPrecisionTextField.insets = new Insets(0, 0, 5, 25);
		gbc_priceBoundaryPrecisionTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceBoundaryPrecisionTextField.gridx = 2;
		gbc_priceBoundaryPrecisionTextField.gridy = 4;
		contentPanel.add(priceBoundaryPrecisionTextField, gbc_priceBoundaryPrecisionTextField);
		
		if(securityEvent!=null&&securityEvent.getEventPrice()!=null)
			priceBoundaryPrecisionTextField.setText(doubleFormat.format(securityEvent.getEventPrice()));
		
		priceBoundaryPrecisionTextField.getDocument().addDocumentListener(documentListener);
		
		JLabel priceTimeTypeLabel = new JLabel("Price Time Type");
		priceTimeTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceTimeTypeLabel = new GridBagConstraints();
		gbc_priceTimeTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_priceTimeTypeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_priceTimeTypeLabel.gridx = 0;
		gbc_priceTimeTypeLabel.gridy = 5;
		contentPanel.add(priceTimeTypeLabel, gbc_priceTimeTypeLabel);

		List<ComboBoxEntry> priceTimeTypeEntries = new ArrayList<ComboBoxEntry>();
		priceTimeTypeEntries.add(new ComboBoxEntry());
		priceTimeTypeEntries.add(new ComboBoxEntry(1, "Expiration"));
		priceTimeTypeEntries.add(new ComboBoxEntry(2, "Immediate"));
		priceTimeTypeEntries.add(new ComboBoxEntry(3, "Specified Date"));

		priceTimeTypeComboBox = new JComboBox(priceTimeTypeEntries.toArray());
		priceTimeTypeComboBox.setMinimumSize(new Dimension(32, 25));
		priceTimeTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		priceTimeTypeComboBox.setPreferredSize(new Dimension(32, 25));
		priceTimeTypeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_priceTimeTypeComboBox = new GridBagConstraints();
		gbc_priceTimeTypeComboBox.gridwidth = 3;
		gbc_priceTimeTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_priceTimeTypeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_priceTimeTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceTimeTypeComboBox.gridx = 2;
		gbc_priceTimeTypeComboBox.gridy = 5;
		contentPanel.add(priceTimeTypeComboBox, gbc_priceTimeTypeComboBox);
		
		if(securityEvent!=null)
			priceTimeTypeComboBox.setSelectedItem(new ComboBoxEntry(securityEvent.getEventPriceTimeType(), null));
		
		priceTimeTypeComboBox.addActionListener(actionListener);
		
		JLabel eventConditionLabel = new JLabel("Event Condition");
		eventConditionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventConditionLabel = new GridBagConstraints();
		gbc_eventConditionLabel.anchor = GridBagConstraints.WEST;
		gbc_eventConditionLabel.insets = new Insets(0, 25, 5, 5);
		gbc_eventConditionLabel.gridx = 0;
		gbc_eventConditionLabel.gridy = 6;
		contentPanel.add(eventConditionLabel, gbc_eventConditionLabel);

		List<ComboBoxEntry> eventConditionEntries = new ArrayList<ComboBoxEntry>();
		eventConditionEntries.add(new ComboBoxEntry());
		eventConditionEntries.add(new ComboBoxEntry(1, "And"));
		eventConditionEntries.add(new ComboBoxEntry(2, "Or"));
		
		eventConditionComboBox = new JComboBox(eventConditionEntries.toArray());
		eventConditionComboBox.setMinimumSize(new Dimension(32, 25));
		eventConditionComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		eventConditionComboBox.setPreferredSize(new Dimension(32, 25));
		eventConditionComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_eventConditionComboBox = new GridBagConstraints();
		gbc_eventConditionComboBox.gridwidth = 3;
		gbc_eventConditionComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_eventConditionComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventConditionComboBox.gridx = 2;
		gbc_eventConditionComboBox.gridy = 6;
		contentPanel.add(eventConditionComboBox, gbc_eventConditionComboBox);
		
		if(securityEvent!=null)
			eventConditionComboBox.setSelectedItem(new ComboBoxEntry(securityEvent.getEventCondition(), null));
		
		eventConditionComboBox.addActionListener(actionListener);
		
		okButton = new JButton();

		if (this.securityComplexEvent == null) {
			this.securityComplexEvent = new SecurityComplexEvent();
			setTitle("Add Security Complex Event");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Complex Event");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 7;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
				
				SecurityComplexEventDialog.this.securityComplexEvent.setEventType((Integer) ((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry());
				
				if (optionPayoutAmountTextField.getText().trim().length() > 0)
					SecurityComplexEventDialog.this.securityComplexEvent.setOptionPayoutAmount(Double.parseDouble(optionPayoutAmountTextField.getText()));
				
				else
					SecurityComplexEventDialog.this.securityComplexEvent.setOptionPayoutAmount(null);
				
				if (priceTextField.getText().trim().length() > 0)
					SecurityComplexEventDialog.this.securityComplexEvent.setEventPrice(Double.parseDouble(priceTextField.getText()));
				
				else
					SecurityComplexEventDialog.this.securityComplexEvent.setEventPrice(null);
				
				SecurityComplexEventDialog.this.securityComplexEvent.setEventPriceBoundaryMethod((Integer) ((ComboBoxEntry) priceBoundaryMethodComboBox.getSelectedItem()).getEntry());
				
				if (priceBoundaryPrecisionTextField.getText().trim().length() > 0)
					SecurityComplexEventDialog.this.securityComplexEvent.setEventPriceBoundaryPrecision(Double.parseDouble(priceBoundaryPrecisionTextField.getText()));
				
				else
					SecurityComplexEventDialog.this.securityComplexEvent.setEventPriceBoundaryPrecision(null);

				SecurityComplexEventDialog.this.securityComplexEvent.setEventPriceTimeType((Integer) ((ComboBoxEntry) priceTimeTypeComboBox.getSelectedItem()).getEntry());

				SecurityComplexEventDialog.this.securityComplexEvent.setEventCondition((Integer) ((ComboBoxEntry) eventConditionComboBox.getSelectedItem()).getEntry());
				
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 7;
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
		
		boolean consistent = true;
		
		if (securityComplexEvent.getEventType() != null && !securityComplexEvent.getEventType().equals(((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (securityComplexEvent.getEventType() == null)
			dirty = true;
		
		if(dirtyFieldCheck(securityComplexEvent.getOptionPayoutAmount(), optionPayoutAmountTextField))
			dirty = true;
		
		if(dirtyFieldCheck(securityComplexEvent.getEventPrice(), priceTextField))
			dirty = true;		

		
		if (securityComplexEvent.getEventPriceBoundaryMethod() != null && !securityComplexEvent.getEventPriceBoundaryMethod().equals(((ComboBoxEntry) priceBoundaryMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityComplexEvent.getEventPriceBoundaryMethod() == null )
				&& priceBoundaryMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if(dirtyFieldCheck(securityComplexEvent.getEventPriceBoundaryPrecision(), priceBoundaryPrecisionTextField))
			dirty = true;			
		
		if (securityComplexEvent.getEventPriceTimeType() != null && !securityComplexEvent.getEventPriceTimeType().equals(((ComboBoxEntry) priceTimeTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityComplexEvent.getEventPriceTimeType() == null )
				&& priceTimeTypeComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityComplexEvent.getEventCondition() != null && !securityComplexEvent.getEventCondition().equals(((ComboBoxEntry) eventConditionComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityComplexEvent.getEventCondition() == null )
				&& eventConditionComboBox.getSelectedIndex() > 0)
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
	 * Gets the security complex event.
	 *
	 * @return the security complex event
	 */
	public SecurityComplexEvent getSecurityComplexEvent() {
		return securityComplexEvent;
	}

}
