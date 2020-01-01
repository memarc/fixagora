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
package net.sourceforge.fixagora.sap.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.document.IntegerDocument;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;

/**
 * The Class SAPTradeCaptureEditorMasterData.
 */
public class SAPTradeCaptureEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private SAPTradeCaptureEditor sapTradeCaptureEditor = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private boolean consistent;

	private JComboBox securityIdentifierSourceComboBox;

	private JLabel sapClientLabel;

	private JLabel sapClientWarningLabel;

	private JTextField sapClientTextField;

	private JLabel sapUserLabel;

	private JLabel sapUserWarningLabel;

	private JTextField sapUserTextField;

	private JLabel sapPasswordLabel;

	private JLabel sapPasswordWarningLabel;

	private JPasswordField sapPasswordTextField;

	private JLabel sapServerLabel;

	private JLabel sapServerWarningLabel;

	private JTextField sapServerTextField;

	private JLabel sapSystemNumber;

	private JLabel sapSystemNumberWarningLabel;

	private JTextField sapSystemNumberTextField;

	private JLabel sapCompanyCodeLabel;

	private JLabel sapCompanyCodeWarningLabel;

	private JTextField sapCompanyCodeTextField;

	private JLabel sapSecuritiesAccountWarningLabel;

	private JTextField sapSecuritiesAccountTextField;

	private JLabel sapGeneralValuationClassWarningLabel;

	private JTextField sapGeneralValuationClassTextField;

	private JLabel sapTransactionTypeBuyWarningLabel;

	private JTextField sapTransactionTypeBuyTextField;

	private JLabel sapTransactionTypeSellWarningLabel;

	private JTextField sapTransactionTypeSellTextField;

	private JCheckBox commitExchangeCheckBox;

	private JCheckBox commitTraderCheckBox;

	/**
	 * Instantiates a new sAP trade capture editor master data.
	 *
	 * @param sapTradeCaptureEditor the sap trade capture editor
	 */
	public SAPTradeCaptureEditorMasterData(SAPTradeCaptureEditor sapTradeCaptureEditor) {

		super();

		setBorder(new EmptyBorder(0, 0, 0, 0));
		getViewport().setBackground(new Color(204, 216, 255));

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {

				checkConsistency();

			}

			@Override
			public void insertUpdate(final DocumentEvent e) {

				checkConsistency();

			}

			@Override
			public void removeUpdate(final DocumentEvent e) {

				checkConsistency();

			}

		};
		
		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};


		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);

		this.sapTradeCaptureEditor = sapTradeCaptureEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 250, 0 , 0, 250, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0,0, 0, 0, 0, 0, 0,0};
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 4;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel nameLabel = new JLabel("Trade Capture Name");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.WEST;
		gbc_nameLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = sapTradeCaptureEditor.getNameWarningLabel();
		nameWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_nameWarningLabel = new GridBagConstraints();
		gbc_nameWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_nameWarningLabel.gridx = 2;
		gbc_nameWarningLabel.gridy = 0;
		panel.add(nameWarningLabel, gbc_nameWarningLabel);

		nameTextField = new JTextField();
		nameTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		nameTextField.getDocument().addDocumentListener(documentListener);
		nameTextField.setPreferredSize(new Dimension(250, 25));
		nameTextField.setColumns(10);
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.insets = new Insets(50, 0, 5, 5);
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 0;
		panel.add(nameTextField, gbc_nameTextField);


		sapClientLabel = new JLabel("SAP Client");
		sapClientLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapClientLabel = new GridBagConstraints();
		gbc_sapClientLabel.anchor = GridBagConstraints.WEST;
		gbc_sapClientLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapClientLabel.gridx = 1;
		gbc_sapClientLabel.gridy = 1;
		panel.add(sapClientLabel, gbc_sapClientLabel);

		sapClientWarningLabel = new JLabel();
		sapClientWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapClientWarningLabel = new GridBagConstraints();
		gbc_sapClientWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapClientWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapClientWarningLabel.gridx = 2;
		gbc_sapClientWarningLabel.gridy = 1;
		panel.add(sapClientWarningLabel, gbc_sapClientWarningLabel);

		sapClientTextField = new JTextField();
		sapClientTextField.setDocument(new IntegerDocument());
		sapClientTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapClientTextField.getDocument().addDocumentListener(documentListener);
		sapClientTextField.setPreferredSize(new Dimension(250, 25));
		sapClientTextField.setColumns(10);
		GridBagConstraints gbc_sapClientTextField = new GridBagConstraints();
		gbc_sapClientTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapClientTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapClientTextField.gridx = 3;
		gbc_sapClientTextField.gridy = 1;
		panel.add(sapClientTextField, gbc_sapClientTextField);

		sapUserLabel = new JLabel("SAP User");
		sapUserLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapUserLabel = new GridBagConstraints();
		gbc_sapUserLabel.anchor = GridBagConstraints.WEST;
		gbc_sapUserLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapUserLabel.gridx = 1;
		gbc_sapUserLabel.gridy = 2;
		panel.add(sapUserLabel, gbc_sapUserLabel);

		sapUserWarningLabel = new JLabel();
		sapUserWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapUserWarningLabel = new GridBagConstraints();
		gbc_sapUserWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapUserWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapUserWarningLabel.gridx = 2;
		gbc_sapUserWarningLabel.gridy = 2;
		panel.add(sapUserWarningLabel, gbc_sapUserWarningLabel);

		sapUserTextField = new JTextField();
		sapUserTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapUserTextField.getDocument().addDocumentListener(documentListener);
		sapUserTextField.setPreferredSize(new Dimension(250, 25));
		sapUserTextField.setColumns(10);
		GridBagConstraints gbc_sapUserTextField = new GridBagConstraints();
		gbc_sapUserTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapUserTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapUserTextField.gridx = 3;
		gbc_sapUserTextField.gridy = 2;
		panel.add(sapUserTextField, gbc_sapUserTextField);

		sapSystemNumber = new JLabel("SAP System Number");
		sapSystemNumber.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapSystemNumber = new GridBagConstraints();
		gbc_sapSystemNumber.anchor = GridBagConstraints.WEST;
		gbc_sapSystemNumber.insets = new Insets(0, 0, 5, 5);
		gbc_sapSystemNumber.gridx = 1;
		gbc_sapSystemNumber.gridy = 3;
		panel.add(sapSystemNumber, gbc_sapSystemNumber);

		sapSystemNumberWarningLabel = new JLabel();
		sapSystemNumberWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapSystemNumberWarningLabel = new GridBagConstraints();
		gbc_sapSystemNumberWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapSystemNumberWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapSystemNumberWarningLabel.gridx = 2;
		gbc_sapSystemNumberWarningLabel.gridy = 3;
		panel.add(sapSystemNumberWarningLabel, gbc_sapSystemNumberWarningLabel);

		sapSystemNumberTextField = new JTextField();
		sapSystemNumberTextField.setDocument(new IntegerDocument());
		sapSystemNumberTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapSystemNumberTextField.getDocument().addDocumentListener(documentListener);
		sapSystemNumberTextField.setPreferredSize(new Dimension(250, 25));
		sapSystemNumberTextField.setColumns(10);
		GridBagConstraints gbc_sapSystemNumberTextField = new GridBagConstraints();
		gbc_sapSystemNumberTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapSystemNumberTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapSystemNumberTextField.gridx = 3;
		gbc_sapSystemNumberTextField.gridy = 3;
		panel.add(sapSystemNumberTextField, gbc_sapSystemNumberTextField);


		
		JLabel sapSecuritiesAccountLabel = new JLabel("SAP Securities Account");
		sapSecuritiesAccountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapSecuritiesAccountLabel = new GridBagConstraints();
		gbc_sapSecuritiesAccountLabel.anchor = GridBagConstraints.WEST;
		gbc_sapSecuritiesAccountLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapSecuritiesAccountLabel.gridx = 1;
		gbc_sapSecuritiesAccountLabel.gridy = 4;
		panel.add(sapSecuritiesAccountLabel, gbc_sapSecuritiesAccountLabel);

		sapSecuritiesAccountWarningLabel = new JLabel();
		sapSecuritiesAccountWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapSecuritiesAccountWarningLabel = new GridBagConstraints();
		gbc_sapSecuritiesAccountWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapSecuritiesAccountWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapSecuritiesAccountWarningLabel.gridx = 2;
		gbc_sapSecuritiesAccountWarningLabel.gridy = 4;
		panel.add(sapSecuritiesAccountWarningLabel, gbc_sapSecuritiesAccountWarningLabel);

		sapSecuritiesAccountTextField = new JTextField();
		sapSecuritiesAccountTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapSecuritiesAccountTextField.getDocument().addDocumentListener(documentListener);
		sapSecuritiesAccountTextField.setPreferredSize(new Dimension(250, 25));
		sapSecuritiesAccountTextField.setColumns(10);
		GridBagConstraints gbc_sapSecuritiesAccountTextField = new GridBagConstraints();
		gbc_sapSecuritiesAccountTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapSecuritiesAccountTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapSecuritiesAccountTextField.gridx = 3;
		gbc_sapSecuritiesAccountTextField.gridy = 4;
		panel.add(sapSecuritiesAccountTextField, gbc_sapSecuritiesAccountTextField);
		

		
		JLabel sapTransactionTypeBuyLabel = new JLabel("SAP Transaction Type Buy");
		sapTransactionTypeBuyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapTransactionTypeBuyLabel = new GridBagConstraints();
		gbc_sapTransactionTypeBuyLabel.anchor = GridBagConstraints.WEST;
		gbc_sapTransactionTypeBuyLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapTransactionTypeBuyLabel.gridx = 1;
		gbc_sapTransactionTypeBuyLabel.gridy = 5;
		panel.add(sapTransactionTypeBuyLabel, gbc_sapTransactionTypeBuyLabel);

		sapTransactionTypeBuyWarningLabel = new JLabel();
		sapTransactionTypeBuyWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapTransactionTypeBuyWarningLabel = new GridBagConstraints();
		gbc_sapTransactionTypeBuyWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapTransactionTypeBuyWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapTransactionTypeBuyWarningLabel.gridx = 2;
		gbc_sapTransactionTypeBuyWarningLabel.gridy = 5;
		panel.add(sapTransactionTypeBuyWarningLabel, gbc_sapTransactionTypeBuyWarningLabel);

		sapTransactionTypeBuyTextField = new JTextField();
		sapTransactionTypeBuyTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapTransactionTypeBuyTextField.getDocument().addDocumentListener(documentListener);
		sapTransactionTypeBuyTextField.setPreferredSize(new Dimension(250, 25));
		sapTransactionTypeBuyTextField.setColumns(10);
		GridBagConstraints gbc_sapTransactionTypeBuyTextField = new GridBagConstraints();
		gbc_sapTransactionTypeBuyTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapTransactionTypeBuyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapTransactionTypeBuyTextField.gridx = 3;
		gbc_sapTransactionTypeBuyTextField.gridy = 5;
		panel.add(sapTransactionTypeBuyTextField, gbc_sapTransactionTypeBuyTextField);

		JLabel lblSecurityIdentifierSource = new JLabel("Security Identifier Source");
		lblSecurityIdentifierSource.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecurityIdentifierSource = new GridBagConstraints();
		gbc_lblSecurityIdentifierSource.anchor = GridBagConstraints.WEST;
		gbc_lblSecurityIdentifierSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblSecurityIdentifierSource.gridx = 1;
		gbc_lblSecurityIdentifierSource.gridy = 6;
		panel.add(lblSecurityIdentifierSource, gbc_lblSecurityIdentifierSource);

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

		securityIdentifierSourceComboBox = new JComboBox(securityIdentifierSourceComboBoxEntries.toArray());
		securityIdentifierSourceComboBox.setMinimumSize(new Dimension(32, 25));
		securityIdentifierSourceComboBox.setPreferredSize(new Dimension(32, 25));
		securityIdentifierSourceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_securityIdentifierSourceComboBox = new GridBagConstraints();
		gbc_securityIdentifierSourceComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_securityIdentifierSourceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityIdentifierSourceComboBox.gridx = 3;
		gbc_securityIdentifierSourceComboBox.gridy = 6;
		panel.add(securityIdentifierSourceComboBox, gbc_securityIdentifierSourceComboBox);
		securityIdentifierSourceComboBox.setSelectedIndex(0);
		
		
		
		JLabel modifiedLabel = new JLabel("Modified");
		modifiedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_modifiedLabel = new GridBagConstraints();
		gbc_modifiedLabel.anchor = GridBagConstraints.WEST;
		gbc_modifiedLabel.insets = new Insets(50, 50, 5, 5);
		gbc_modifiedLabel.gridx = 4;
		gbc_modifiedLabel.gridy = 0;
		panel.add(modifiedLabel, gbc_modifiedLabel);

		modifiedField = new JTextField();
		modifiedField.setEditable(false);
		modifiedField.setBackground(new Color(204, 216, 255));
		modifiedField.setPreferredSize(new Dimension(4, 25));
		modifiedField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		modifiedField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(50, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 6;
		gbc_textField.gridy = 0;
		panel.add(modifiedField, gbc_textField);
		
		sapServerLabel = new JLabel("SAP Server");
		sapServerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapServerLabel = new GridBagConstraints();
		gbc_sapServerLabel.anchor = GridBagConstraints.WEST;
		gbc_sapServerLabel.insets = new Insets(0, 50, 5, 5);
		gbc_sapServerLabel.gridx = 4;
		gbc_sapServerLabel.gridy = 1;
		panel.add(sapServerLabel, gbc_sapServerLabel);

		sapServerWarningLabel = new JLabel();
		sapServerWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapServerWarningLabel = new GridBagConstraints();
		gbc_sapServerWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapServerWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapServerWarningLabel.gridx = 5;
		gbc_sapServerWarningLabel.gridy = 1;
		panel.add(sapServerWarningLabel, gbc_sapServerWarningLabel);

		sapServerTextField = new JTextField();
		sapServerTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapServerTextField.getDocument().addDocumentListener(documentListener);
		sapServerTextField.setPreferredSize(new Dimension(250, 25));
		sapServerTextField.setColumns(10);
		GridBagConstraints gbc_sapServerTextField = new GridBagConstraints();
		gbc_sapServerTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapServerTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapServerTextField.gridx = 6;
		gbc_sapServerTextField.gridy = 1;
		panel.add(sapServerTextField, gbc_sapServerTextField);
		
		sapPasswordLabel = new JLabel("SAP Password");
		sapPasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapPasswordLabel = new GridBagConstraints();
		gbc_sapPasswordLabel.anchor = GridBagConstraints.WEST;
		gbc_sapPasswordLabel.insets = new Insets(0, 50, 5, 5);
		gbc_sapPasswordLabel.gridx = 4;
		gbc_sapPasswordLabel.gridy = 2;
		panel.add(sapPasswordLabel, gbc_sapPasswordLabel);

		sapPasswordWarningLabel = new JLabel();
		sapPasswordWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapPasswordWarningLabel = new GridBagConstraints();
		gbc_sapPasswordWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapPasswordWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapPasswordWarningLabel.gridx = 5;
		gbc_sapPasswordWarningLabel.gridy = 2;
		panel.add(sapPasswordWarningLabel, gbc_sapPasswordWarningLabel);

		sapPasswordTextField = new JPasswordField();
		sapPasswordTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapPasswordTextField.getDocument().addDocumentListener(documentListener);
		sapPasswordTextField.setPreferredSize(new Dimension(250, 25));
		sapPasswordTextField.setColumns(10);
		GridBagConstraints gbc_sapPasswordTextField = new GridBagConstraints();
		gbc_sapPasswordTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapPasswordTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapPasswordTextField.gridx = 6;
		gbc_sapPasswordTextField.gridy = 2;
		panel.add(sapPasswordTextField, gbc_sapPasswordTextField);
		
		sapCompanyCodeLabel = new JLabel("SAP Company Code");
		sapCompanyCodeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapCompanyCodeLabel = new GridBagConstraints();
		gbc_sapCompanyCodeLabel.anchor = GridBagConstraints.WEST;
		gbc_sapCompanyCodeLabel.insets = new Insets(0, 50, 5, 5);
		gbc_sapCompanyCodeLabel.gridx = 4;
		gbc_sapCompanyCodeLabel.gridy = 3;
		panel.add(sapCompanyCodeLabel, gbc_sapCompanyCodeLabel);

		sapCompanyCodeWarningLabel = new JLabel();
		sapCompanyCodeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapCompanyCodeWarningLabel = new GridBagConstraints();
		gbc_sapCompanyCodeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapCompanyCodeWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapCompanyCodeWarningLabel.gridx = 5;
		gbc_sapCompanyCodeWarningLabel.gridy = 3;
		panel.add(sapCompanyCodeWarningLabel, gbc_sapCompanyCodeWarningLabel);

		sapCompanyCodeTextField = new JTextField();
		sapCompanyCodeTextField.setDocument(new IntegerDocument());
		sapCompanyCodeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapCompanyCodeTextField.getDocument().addDocumentListener(documentListener);
		sapCompanyCodeTextField.setPreferredSize(new Dimension(250, 25));
		sapCompanyCodeTextField.setColumns(10);
		GridBagConstraints gbc_sapCompanyCodeTextField = new GridBagConstraints();
		gbc_sapCompanyCodeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapCompanyCodeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapCompanyCodeTextField.gridx = 6;
		gbc_sapCompanyCodeTextField.gridy = 3;
		panel.add(sapCompanyCodeTextField, gbc_sapCompanyCodeTextField);

		JLabel sapGeneralValuationClassLabel = new JLabel("SAP General Valuation Class");
		sapGeneralValuationClassLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapGeneralValuationClassLabel = new GridBagConstraints();
		gbc_sapGeneralValuationClassLabel.anchor = GridBagConstraints.WEST;
		gbc_sapGeneralValuationClassLabel.insets = new Insets(0, 50, 5, 5);
		gbc_sapGeneralValuationClassLabel.gridx = 4;
		gbc_sapGeneralValuationClassLabel.gridy = 4;
		panel.add(sapGeneralValuationClassLabel, gbc_sapGeneralValuationClassLabel);

		sapGeneralValuationClassWarningLabel = new JLabel();
		sapGeneralValuationClassWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapGeneralValuationClassWarningLabel = new GridBagConstraints();
		gbc_sapGeneralValuationClassWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapGeneralValuationClassWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapGeneralValuationClassWarningLabel.gridx = 5;
		gbc_sapGeneralValuationClassWarningLabel.gridy = 4;
		panel.add(sapGeneralValuationClassWarningLabel, gbc_sapGeneralValuationClassWarningLabel);

		sapGeneralValuationClassTextField = new JTextField();
		sapGeneralValuationClassTextField.setDocument(new IntegerDocument());
		sapGeneralValuationClassTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapGeneralValuationClassTextField.getDocument().addDocumentListener(documentListener);
		sapGeneralValuationClassTextField.setPreferredSize(new Dimension(250, 25));
		sapGeneralValuationClassTextField.setColumns(10);
		GridBagConstraints gbc_sapGeneralValuationClassTextField = new GridBagConstraints();
		gbc_sapGeneralValuationClassTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapGeneralValuationClassTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapGeneralValuationClassTextField.gridx = 6;
		gbc_sapGeneralValuationClassTextField.gridy = 4;
		panel.add(sapGeneralValuationClassTextField, gbc_sapGeneralValuationClassTextField);
		
		JLabel sapTransactionTypeSellLabel = new JLabel("SAP Transaction Type Sell");
		sapTransactionTypeSellLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sapTransactionTypeSellLabel = new GridBagConstraints();
		gbc_sapTransactionTypeSellLabel.anchor = GridBagConstraints.WEST;
		gbc_sapTransactionTypeSellLabel.insets = new Insets(0, 50, 5, 5);
		gbc_sapTransactionTypeSellLabel.gridx = 4;
		gbc_sapTransactionTypeSellLabel.gridy = 5;
		panel.add(sapTransactionTypeSellLabel, gbc_sapTransactionTypeSellLabel);

		sapTransactionTypeSellWarningLabel = new JLabel();
		sapTransactionTypeSellWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sapTransactionTypeSellWarningLabel = new GridBagConstraints();
		gbc_sapTransactionTypeSellWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sapTransactionTypeSellWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_sapTransactionTypeSellWarningLabel.gridx = 5;
		gbc_sapTransactionTypeSellWarningLabel.gridy = 5;
		panel.add(sapTransactionTypeSellWarningLabel, gbc_sapTransactionTypeSellWarningLabel);

		sapTransactionTypeSellTextField = new JTextField();
		sapTransactionTypeSellTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		sapTransactionTypeSellTextField.getDocument().addDocumentListener(documentListener);
		sapTransactionTypeSellTextField.setPreferredSize(new Dimension(250, 25));
		sapTransactionTypeSellTextField.setColumns(10);
		GridBagConstraints gbc_sapTransactionTypeSellTextField = new GridBagConstraints();
		gbc_sapTransactionTypeSellTextField.insets = new Insets(0, 0, 5, 5);
		gbc_sapTransactionTypeSellTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sapTransactionTypeSellTextField.gridx = 6;
		gbc_sapTransactionTypeSellTextField.gridy = 5;
		panel.add(sapTransactionTypeSellTextField, gbc_sapTransactionTypeSellTextField);
		
		JLabel commitExchangeLabel = new JLabel("Commit Exchange");
		commitExchangeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_commitExchangeLabel = new GridBagConstraints();
		gbc_commitExchangeLabel.anchor = GridBagConstraints.WEST;
		gbc_commitExchangeLabel.insets = new Insets(0, 50, 5, 5);
		gbc_commitExchangeLabel.gridx = 4;
		gbc_commitExchangeLabel.gridy = 6;
		panel.add(commitExchangeLabel, gbc_commitExchangeLabel);

		commitExchangeCheckBox = new JCheckBox();
		commitExchangeCheckBox.setPreferredSize(new Dimension(25, 25));
		commitExchangeCheckBox.setBackground(new Color(204, 216, 255));
		commitExchangeCheckBox.addActionListener(actionListener);
		GridBagConstraints gbc_ccommitExchangeCheckBox = new GridBagConstraints();
		gbc_ccommitExchangeCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_ccommitExchangeCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_ccommitExchangeCheckBox.gridx = 6;
		gbc_ccommitExchangeCheckBox.gridy = 6;
		panel.add(commitExchangeCheckBox, gbc_ccommitExchangeCheckBox);
		
		JLabel commitTraderLabel = new JLabel("Commit Trader");
		commitTraderLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_commitTraderLabel = new GridBagConstraints();
		gbc_commitTraderLabel.anchor = GridBagConstraints.WEST;
		gbc_commitTraderLabel.insets = new Insets(0, 50, 50, 5);
		gbc_commitTraderLabel.gridx = 4;
		gbc_commitTraderLabel.gridy = 7;
		panel.add(commitTraderLabel, gbc_commitTraderLabel);
		
		commitTraderCheckBox = new JCheckBox();
		commitTraderCheckBox.setPreferredSize(new Dimension(25, 25));
		commitTraderCheckBox.setBackground(new Color(204, 216, 255));
		commitTraderCheckBox.addActionListener(actionListener);
		GridBagConstraints gbc_commitTraderCheckBox = new GridBagConstraints();
		gbc_commitTraderCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_commitTraderCheckBox.insets = new Insets(0, 0, 50, 5);
		gbc_commitTraderCheckBox.gridx = 6;
		gbc_commitTraderCheckBox.gridy = 7;
		panel.add(commitTraderCheckBox, gbc_commitTraderCheckBox);

		securityIdentifierSourceComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		});


		updateContent();
	}

	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}

	private void checkConsistency() {

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Book name is empty");
			nameWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (sapTradeCaptureEditor.getAbstractBusinessObject().getName() == null
					|| !sapTradeCaptureEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				sapTradeCaptureEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}

		if (sapClientTextField.getText() == null || sapClientTextField.getText().trim().length() != 3) {

			sapClientWarningLabel.setToolTipText("SAP client is not specified.");
			sapClientWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapClientWarningLabel.setToolTipText(null);
			sapClientWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapClient(), sapClientTextField))
				dirty = true;
		}
		
		if (sapUserTextField.getText() == null || sapUserTextField.getText().trim().length() == 0) {

			sapUserWarningLabel.setToolTipText("SAP user is not specified.");
			sapUserWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapUserWarningLabel.setToolTipText(null);
			sapUserWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapUserName(), sapUserTextField))
				dirty = true;
		}

		if (sapPasswordTextField.getPassword() == null || sapPasswordTextField.getPassword().length == 0) {

			sapPasswordWarningLabel.setToolTipText("SAP password is not specified.");
			sapPasswordWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapPasswordWarningLabel.setToolTipText(null);
			sapPasswordWarningLabel.setIcon(null);

			if (sapTradeCaptureEditor.getSAPTradeCapture().getSapPassword() == null && sapPasswordTextField.getPassword().length > 0)
				dirty = true;

			else if (sapTradeCaptureEditor.getSAPTradeCapture().getSapPassword() == null && sapPasswordTextField.getPassword().length == 0)
				dirty = false;

			else if (!sapTradeCaptureEditor.getSAPTradeCapture().getSapPassword().equals(new String(sapPasswordTextField.getPassword())))
				dirty = true;
		}

		if (sapServerTextField.getText() == null || sapServerTextField.getText().trim().length() == 0) {

			sapServerWarningLabel.setToolTipText("SAP server is not specified.");
			sapServerWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapServerWarningLabel.setToolTipText(null);
			sapServerWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapServerName(), sapServerTextField))
				dirty = true;
		}
		
		if (sapSystemNumberTextField.getText() == null || sapSystemNumberTextField.getText().trim().length() != 2) {

			sapSystemNumberWarningLabel.setToolTipText("SAP system number is not specified.");
			sapSystemNumberWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapSystemNumberWarningLabel.setToolTipText(null);
			sapSystemNumberWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapSystemNumber(), sapSystemNumberTextField))
				dirty = true;
		}

		if (sapCompanyCodeTextField.getText() == null || sapCompanyCodeTextField.getText().trim().length() != 4) {

			sapCompanyCodeWarningLabel.setToolTipText("SAP company code is not specified.");
			sapCompanyCodeWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapCompanyCodeWarningLabel.setToolTipText(null);
			sapCompanyCodeWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapCompanyCode(), sapCompanyCodeTextField))
				dirty = true;
		}
		
		if (sapTransactionTypeBuyTextField.getText() == null || sapTransactionTypeBuyTextField.getText().trim().length() != 3) {

			sapTransactionTypeBuyWarningLabel.setToolTipText("SAP transaction type is not specified.");
			sapTransactionTypeBuyWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapTransactionTypeBuyWarningLabel.setToolTipText(null);
			sapTransactionTypeBuyWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeBuy(), sapTransactionTypeBuyTextField))
				dirty = true;
		}
		
		if (sapTransactionTypeSellTextField.getText() == null || sapTransactionTypeSellTextField.getText().trim().length() != 3) {

			sapTransactionTypeSellWarningLabel.setToolTipText("SAP transaction type is not specified.");
			sapTransactionTypeSellWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapTransactionTypeSellWarningLabel.setToolTipText(null);
			sapTransactionTypeSellWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeSell(), sapTransactionTypeSellTextField))
				dirty = true;
		}
		
		if (sapSecuritiesAccountTextField.getText() == null || sapSecuritiesAccountTextField.getText().trim().length() == 0) {

			sapSecuritiesAccountWarningLabel.setToolTipText("SAP securities account is not specified.");
			sapSecuritiesAccountWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapSecuritiesAccountWarningLabel.setToolTipText(null);
			sapSecuritiesAccountWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapSecuritiesAccount(), sapSecuritiesAccountTextField))
				dirty = true;
		}
		
		if (sapGeneralValuationClassTextField.getText() == null || sapGeneralValuationClassTextField.getText().trim().length() !=4) {

			sapGeneralValuationClassWarningLabel.setToolTipText("SAP general valuation class is not specified.");
			sapGeneralValuationClassWarningLabel.setIcon(sapTradeCaptureEditor.getBugIcon());
			consistent = false;
		}
		else {
			sapGeneralValuationClassWarningLabel.setToolTipText(null);
			sapGeneralValuationClassWarningLabel.setIcon(null);

			if (dirtyFieldCheck(sapTradeCaptureEditor.getSAPTradeCapture().getSapGeneralValuationClass(), sapGeneralValuationClassTextField))
				dirty = true;
		}
				
		if (sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange() != commitExchangeCheckBox.isSelected())
			dirty = true;
		
		if (sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader() != commitTraderCheckBox.isSelected())
			dirty = true;
		
		if (sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource() != null
				&& sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource().trim().length() > 0
				&& !sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource()
						.equals(((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if ((sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource() == null || sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource()
				.trim().length() == 0)
				&& securityIdentifierSourceComboBox.getSelectedIndex() > 0)
			dirty = true;


		consistent = consistent && valid;

		sapTradeCaptureEditor.checkDirty();
	}

	/**
	 * Checks if is consistent.
	 *
	 * @return true, if is consistent
	 */
	public boolean isConsistent() {

		return consistent;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return dirty;
	}

	/**
	 * Save.
	 */
	public void save() {

		sapTradeCaptureEditor.getSAPTradeCapture().setName(nameTextField.getText().trim());
		sapTradeCaptureEditor.getSAPTradeCapture()
				.setSecurityIDSource((String) ((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry());

		sapTradeCaptureEditor.getSAPTradeCapture().setSapClient(sapClientTextField.getText());

		sapTradeCaptureEditor.getSAPTradeCapture().setSapCompanyCode(sapCompanyCodeTextField.getText());

		sapTradeCaptureEditor.getSAPTradeCapture().setSapPassword(new String(sapPasswordTextField.getPassword()));
		sapTradeCaptureEditor.getSAPTradeCapture().setSapServerName(sapServerTextField.getText());

		sapTradeCaptureEditor.getSAPTradeCapture().setSapSystemNumber(sapSystemNumberTextField.getText());

		sapTradeCaptureEditor.getSAPTradeCapture().setSapUserName(sapUserTextField.getText());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSapTransactionTypeBuy(sapTransactionTypeBuyTextField.getText());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSapTransactionTypeSell(sapTransactionTypeSellTextField.getText());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSapSecuritiesAccount(sapSecuritiesAccountTextField.getText());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSapGeneralValuationClass(sapGeneralValuationClassTextField.getText());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSetExchange(commitExchangeCheckBox.isSelected());
		
		sapTradeCaptureEditor.getSAPTradeCapture().setSetTrader(commitTraderCheckBox.isSelected());
				
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getName());

		sapClientTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapClient());
				
		sapCompanyCodeTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapCompanyCode());
				
		sapPasswordTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapPassword());
				
		sapServerTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapServerName());
		
		sapSystemNumberTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapSystemNumber());
				
		sapUserTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapUserName());
		
		sapTransactionTypeBuyTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeBuy());
		
		sapTransactionTypeSellTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeSell());
		
		sapSecuritiesAccountTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapSecuritiesAccount());
		
		sapGeneralValuationClassTextField.setText(sapTradeCaptureEditor.getSAPTradeCapture().getSapGeneralValuationClass());
		
		commitExchangeCheckBox.setSelected(sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange());
		
		commitTraderCheckBox.setSelected(sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader());

		if (sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource() != null)
			securityIdentifierSourceComboBox.setSelectedItem(new ComboBoxEntry(sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource(), null));
		else
			securityIdentifierSourceComboBox.setSelectedIndex(0);

		if (sapTradeCaptureEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(sapTradeCaptureEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(sapTradeCaptureEditor.getAbstractBusinessObject().getModificationDate()));

			if (sapTradeCaptureEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(sapTradeCaptureEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		if (sapTradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(sapTradeCaptureEditor.getAbstractBusinessObject())
				&& !(sapTradeCaptureEditor.getAbstractBusinessObject().getName() != null && sapTradeCaptureEditor.getAbstractBusinessObject().getName()
						.equals("Admin Role"))) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);

			securityIdentifierSourceComboBox.setBackground(new Color(255, 243, 204));
			securityIdentifierSourceComboBox.setEnabled(true);
			
			sapClientTextField.setBackground(new Color(255, 243, 204));
			sapClientTextField.setEditable(true);

			sapCompanyCodeTextField.setBackground(new Color(255, 243, 204));
			sapCompanyCodeTextField.setEditable(true);

			sapPasswordTextField.setBackground(new Color(255, 243, 204));
			sapPasswordTextField.setEditable(true);

			sapServerTextField.setBackground(new Color(255, 243, 204));
			sapServerTextField.setEditable(true);

			sapSystemNumberTextField.setBackground(new Color(255, 243, 204));
			sapSystemNumberTextField.setEditable(true);

			sapUserTextField.setBackground(new Color(255, 243, 204));
			sapUserTextField.setEditable(true);
			
			sapTransactionTypeBuyTextField.setBackground(new Color(255, 243, 204));
			sapTransactionTypeBuyTextField.setEditable(true);
			
			sapTransactionTypeSellTextField.setBackground(new Color(255, 243, 204));
			sapTransactionTypeSellTextField.setEditable(true);
			
			sapSecuritiesAccountTextField.setBackground(new Color(255, 243, 204));
			sapSecuritiesAccountTextField.setEditable(true);
			
			sapGeneralValuationClassTextField.setBackground(new Color(255, 243, 204));
			sapGeneralValuationClassTextField.setEditable(true);

			commitExchangeCheckBox.setEnabled(true);
			commitTraderCheckBox.setEnabled(true);

		}
		else {
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);

			securityIdentifierSourceComboBox.setBackground(new Color(204, 216, 255));
			securityIdentifierSourceComboBox.setEnabled(false);

			sapClientTextField.setBackground(new Color(204, 216, 255));
			sapClientTextField.setEditable(false);
			
			sapCompanyCodeTextField.setBackground(new Color(204, 216, 255));
			sapCompanyCodeTextField.setEditable(false);
			
			sapPasswordTextField.setBackground(new Color(204, 216, 255));
			sapPasswordTextField.setEditable(false);
			
			sapServerTextField.setBackground(new Color(204, 216, 255));
			sapServerTextField.setEditable(false);
			
			sapSystemNumberTextField.setBackground(new Color(204, 216, 255));
			sapSystemNumberTextField.setEditable(false);
			
			sapUserTextField.setBackground(new Color(204, 216, 255));
			sapUserTextField.setEditable(false);
			
			sapTransactionTypeBuyTextField.setBackground(new Color(204, 216, 255));
			sapTransactionTypeBuyTextField.setEditable(false);
			
			sapTransactionTypeSellTextField.setBackground(new Color(204, 216, 255));
			sapTransactionTypeSellTextField.setEditable(false);
			
			sapSecuritiesAccountTextField.setBackground(new Color(204, 216, 255));
			sapSecuritiesAccountTextField.setEditable(false);
			
			sapGeneralValuationClassTextField.setBackground(new Color(204, 216, 255));
			sapGeneralValuationClassTextField.setEditable(false);

			commitExchangeCheckBox.setEnabled(false);
			commitTraderCheckBox.setEnabled(false);

		}

		checkConsistency();
	}
}
