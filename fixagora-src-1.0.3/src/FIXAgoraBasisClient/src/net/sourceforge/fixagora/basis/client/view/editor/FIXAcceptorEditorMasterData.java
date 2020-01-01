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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.document.IntegerDocument;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor.MarketDataType;

/**
 * The Class FIXAcceptorEditorMasterData.
 */
public class FIXAcceptorEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private FIXAcceptorEditor fixAcceptorEditor = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private JTextField socketAdressTextField = null;

	private JTextField socketPortTextField = null;

	private JComboBox dataDictionaryComboBox = null;

	private boolean consistent = false;

	private JTextField senderCompIDTextField = null;

	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	private JLabel socketAdressWarningLabel = null;

	private JLabel socketPortWarningLabel = null;

	private JLabel senderCompIDWarningLabel = null;

	private JCheckBox connectAtStartUpCheckBox = null;

	private JLabel marketWarningLabel = null;

	private JTextField marketTextField = null;

	private JLabel partyIDWarningLabel = null;

	private JTextField partyIDTextField = null;

	private JComboBox partyRoleComboBox = null;

	private JComboBox partyIDSourceComboBox = null;

	private JLabel partyIDSourceWarningLabel;

	private JLabel partyRoleWarningLabel;

	private JComboBox sendMarketDataComboBox;

	private JComboBox securityIDSourceComboBox;

	private JCheckBox persistMessagesCheckBox;

	/**
	 * Instantiates a new fIX acceptor editor master data.
	 *
	 * @param fixAcceptorEditor the fix acceptor editor
	 */
	public FIXAcceptorEditorMasterData(FIXAcceptorEditor fixAcceptorEditor) {

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

		this.fixAcceptorEditor = fixAcceptorEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 300, 0, 0, 300,0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0,0, 0  };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.weightx = 1.0;
		gbc_leftFillPanel.gridheight = 7;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameLabel.insets = new Insets(50, 50, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = fixAcceptorEditor.getNameWarningLabel();
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
		nameTextField.setPreferredSize(new Dimension(300, 25));
		nameTextField.setColumns(10);
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.insets = new Insets(50, 0, 5, 0);
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 0;
		panel.add(nameTextField, gbc_nameTextField);

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 7;
		gbc_rightFillPanel.weightx = 1.0;
		gbc_rightFillPanel.insets = new Insets(0, 0, 5, 0);
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 7;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JLabel modifiedLabel = new JLabel("Modified");
		modifiedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_modifiedLabel = new GridBagConstraints();
		gbc_modifiedLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedLabel.insets = new Insets(0, 50, 5, 5);
		gbc_modifiedLabel.gridx = 1;
		gbc_modifiedLabel.gridy = 1;
		panel.add(modifiedLabel, gbc_modifiedLabel);

		modifiedField = new JTextField();
		modifiedField.setEditable(false);
		modifiedField.setBackground(new Color(204, 216, 255));
		modifiedField.setPreferredSize(new Dimension(300, 25));
		modifiedField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		modifiedField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		panel.add(modifiedField, gbc_textField);


		JLabel dataDictionaryLabel = new JLabel("Data Dictionary");
		dataDictionaryLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_dataDictionaryLabel = new GridBagConstraints();
		gbc_dataDictionaryLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataDictionaryLabel.insets = new Insets(0, 50, 5, 5);
		gbc_dataDictionaryLabel.gridx = 1;
		gbc_dataDictionaryLabel.gridy = 2;
		panel.add(dataDictionaryLabel, gbc_dataDictionaryLabel);

		List<String> dictionaries = new ArrayList<String>();
		for (DataDictionary dataDictionary : fixAcceptorEditor.getMainPanel().getDataDictionaries())
			dictionaries.add(dataDictionary.getName());

		dataDictionaryComboBox = new JComboBox(dictionaries.toArray());
		dataDictionaryComboBox.setPreferredSize(new Dimension(175, 25));
		dataDictionaryComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		dataDictionaryComboBox.setSelectedIndex(0);
		GridBagConstraints gbc_dataDictionaryComboBox = new GridBagConstraints();
		gbc_dataDictionaryComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataDictionaryComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_dataDictionaryComboBox.gridx = 3;
		gbc_dataDictionaryComboBox.gridy = 2;
		panel.add(dataDictionaryComboBox, gbc_dataDictionaryComboBox);

		dataDictionaryComboBox.addActionListener(actionListener);

		JLabel socketAdressLabel = new JLabel("Socket Adress");
		socketAdressLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_socketAdressLabel = new GridBagConstraints();
		gbc_socketAdressLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_socketAdressLabel.insets = new Insets(0, 50, 5, 5);
		gbc_socketAdressLabel.gridx = 1;
		gbc_socketAdressLabel.gridy = 3;
		panel.add(socketAdressLabel, gbc_socketAdressLabel);

		socketAdressWarningLabel = new JLabel();
		socketAdressWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_socketAdressWarningLabel = new GridBagConstraints();
		gbc_socketAdressWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_socketAdressWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_socketAdressWarningLabel.gridx = 2;
		gbc_socketAdressWarningLabel.gridy = 3;
		panel.add(socketAdressWarningLabel, gbc_socketAdressWarningLabel);

		socketAdressTextField = new JTextField();
		socketAdressTextField.setMaximumSize(new Dimension(225, 25));
		socketAdressTextField.setMinimumSize(new Dimension(225, 25));
		socketAdressTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		socketAdressTextField.setPreferredSize(new Dimension(225, 25));
		socketAdressTextField.getDocument().addDocumentListener(documentListener);
		socketAdressTextField.setColumns(10);
		GridBagConstraints gbc_socketAdressTextField = new GridBagConstraints();
		gbc_socketAdressTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_socketAdressTextField.insets = new Insets(0, 0, 5, 0);
		gbc_socketAdressTextField.gridx = 3;
		gbc_socketAdressTextField.gridy = 3;
		panel.add(socketAdressTextField, gbc_socketAdressTextField);

		JLabel socketPortLabel = new JLabel("Socket Port");
		socketPortLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_socketPortLabel = new GridBagConstraints();
		gbc_socketPortLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_socketPortLabel.insets = new Insets(0, 50, 5, 5);
		gbc_socketPortLabel.gridx = 1;
		gbc_socketPortLabel.gridy = 4;
		panel.add(socketPortLabel, gbc_socketPortLabel);

		socketPortWarningLabel = new JLabel();
		socketPortWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_socketPortWarningLabel = new GridBagConstraints();
		gbc_socketPortWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_socketPortWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_socketPortWarningLabel.gridx = 2;
		gbc_socketPortWarningLabel.gridy = 4;
		panel.add(socketPortWarningLabel, gbc_socketPortWarningLabel);

		socketPortTextField = new JTextField();
		socketPortTextField.setMaximumSize(new Dimension(75, 25));
		socketPortTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		socketPortTextField.setPreferredSize(new Dimension(75, 25));
		socketPortTextField.setDocument(new IntegerDocument());
		socketPortTextField.getDocument().addDocumentListener(documentListener);
		socketPortTextField.setColumns(4);
		GridBagConstraints gbc_socketPortTextField = new GridBagConstraints();
		gbc_socketPortTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_socketPortTextField.insets = new Insets(0, 0, 5, 0);
		gbc_socketPortTextField.gridx = 3;
		gbc_socketPortTextField.gridy = 4;
		panel.add(socketPortTextField, gbc_socketPortTextField);

		JLabel senderCompIDLabel = new JLabel("Sender Comp ID");
		senderCompIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_senderCompIDLabel = new GridBagConstraints();
		gbc_senderCompIDLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_senderCompIDLabel.insets = new Insets(0, 50, 5, 5);
		gbc_senderCompIDLabel.gridx = 1;
		gbc_senderCompIDLabel.gridy = 5;
		panel.add(senderCompIDLabel, gbc_senderCompIDLabel);

		senderCompIDWarningLabel = new JLabel();
		senderCompIDWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_senderCompIDWarningLabel = new GridBagConstraints();
		gbc_senderCompIDWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_senderCompIDWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_senderCompIDWarningLabel.gridx = 2;
		gbc_senderCompIDWarningLabel.gridy = 5;
		panel.add(senderCompIDWarningLabel, gbc_senderCompIDWarningLabel);

		senderCompIDTextField = new JTextField();
		senderCompIDTextField.setMaximumSize(new Dimension(225, 25));
		senderCompIDTextField.setMinimumSize(new Dimension(225, 25));
		senderCompIDTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		senderCompIDTextField.setPreferredSize(new Dimension(225, 25));
		senderCompIDTextField.getDocument().addDocumentListener(documentListener);
		senderCompIDTextField.setColumns(10);
		GridBagConstraints gbc_senderCompIDTextField = new GridBagConstraints();
		gbc_senderCompIDTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_senderCompIDTextField.insets = new Insets(0, 0, 5, 0);
		gbc_senderCompIDTextField.gridx = 3;
		gbc_senderCompIDTextField.gridy = 5;
		panel.add(senderCompIDTextField, gbc_senderCompIDTextField);

		JLabel connectAtStartUp = new JLabel("Open Session at Startup");
		connectAtStartUp.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_connectAtStartUp = new GridBagConstraints();
		gbc_connectAtStartUp.anchor = GridBagConstraints.NORTH;
		gbc_connectAtStartUp.fill = GridBagConstraints.HORIZONTAL;
		gbc_connectAtStartUp.insets = new Insets(5, 50, 5, 5);
		gbc_connectAtStartUp.gridx = 1;
		gbc_connectAtStartUp.gridy = 6;
		panel.add(connectAtStartUp, gbc_connectAtStartUp);

		connectAtStartUpCheckBox = new JCheckBox();
		connectAtStartUpCheckBox.setBackground(new Color(204, 216, 255));
		connectAtStartUpCheckBox.addActionListener(actionListener);
		GridBagConstraints gbc_connectAtStartUpCheckBox = new GridBagConstraints();
		gbc_connectAtStartUpCheckBox.anchor = GridBagConstraints.NORTH;
		gbc_connectAtStartUpCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_connectAtStartUpCheckBox.insets = new Insets(3, 0, 5, 0);
		gbc_connectAtStartUpCheckBox.gridx = 3;
		gbc_connectAtStartUpCheckBox.gridy = 6;
		panel.add(connectAtStartUpCheckBox, gbc_connectAtStartUpCheckBox);
		
		JLabel marketLabel = new JLabel("Market Name");
		marketLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_marketLabel = new GridBagConstraints();
		gbc_marketLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_marketLabel.insets = new Insets(50, 50, 5, 5);
		gbc_marketLabel.gridx = 4;
		gbc_marketLabel.gridy = 0;
		panel.add(marketLabel, gbc_marketLabel);

		marketWarningLabel = new JLabel();
		marketWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_marketWarningLabel = new GridBagConstraints();
		gbc_marketWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_marketWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_marketWarningLabel.gridx = 5;
		gbc_marketWarningLabel.gridy = 0;
		panel.add(marketWarningLabel, gbc_marketWarningLabel);

		marketTextField = new JTextField();
		marketTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		marketTextField.getDocument().addDocumentListener(documentListener);
		marketTextField.setPreferredSize(new Dimension(300, 25));
		marketTextField.setColumns(10);
		GridBagConstraints gbc_marketTextField = new GridBagConstraints();
		gbc_marketTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_marketTextField.insets = new Insets(50, 0, 5, 0);
		gbc_marketTextField.gridx = 6;
		gbc_marketTextField.gridy = 0;
		panel.add(marketTextField, gbc_marketTextField);
		
		JLabel partyIDLabel = new JLabel("Party ID");
		partyIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partyIDLabel = new GridBagConstraints();
		gbc_partyIDLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDLabel.insets = new Insets(0, 50, 5, 5);
		gbc_partyIDLabel.gridx = 4;
		gbc_partyIDLabel.gridy = 1;
		panel.add(partyIDLabel, gbc_partyIDLabel);

		partyIDWarningLabel = new JLabel();
		partyIDWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_partyIDWarningLabel = new GridBagConstraints();
		gbc_partyIDWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_partyIDWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_partyIDWarningLabel.gridx = 5;
		gbc_partyIDWarningLabel.gridy = 1;
		panel.add(partyIDWarningLabel, gbc_partyIDWarningLabel);

		partyIDTextField = new JTextField();
		partyIDTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		partyIDTextField.getDocument().addDocumentListener(documentListener);
		partyIDTextField.setPreferredSize(new Dimension(300, 25));
		partyIDTextField.setColumns(10);
		GridBagConstraints gbc_partyIDTextField = new GridBagConstraints();
		gbc_partyIDTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDTextField.insets = new Insets(0, 0, 5, 0);
		gbc_partyIDTextField.gridx = 6;
		gbc_partyIDTextField.gridy = 1;
		panel.add(partyIDTextField, gbc_partyIDTextField);
		
		JLabel partyIDSourceLabel = new JLabel("Party ID Source");
		partyIDSourceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partyIDSourceLabel = new GridBagConstraints();
		gbc_partyIDSourceLabel.anchor = GridBagConstraints.WEST;
		gbc_partyIDSourceLabel.insets = new Insets(0, 50, 5, 50);
		gbc_partyIDSourceLabel.gridx = 4;
		gbc_partyIDSourceLabel.gridy = 2;
		panel.add(partyIDSourceLabel, gbc_partyIDSourceLabel);
		
		partyIDSourceWarningLabel = new JLabel();
		partyIDSourceWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_partyIDSourceWarningLabel = new GridBagConstraints();
		gbc_partyIDSourceWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_partyIDSourceWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_partyIDSourceWarningLabel.gridx = 5;
		gbc_partyIDSourceWarningLabel.gridy = 2;
		panel.add(partyIDSourceWarningLabel, gbc_partyIDSourceWarningLabel);

		List<ComboBoxEntry> partyIDSourceComboBoxEntries = new ArrayList<ComboBoxEntry>();
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry());
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("B", "BIC (Bank Identification Code)"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("C", "Generally accepted market participant identifier"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("D", "Proprietary / Custom code"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("E", "ISO Country Code"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("F", "Settlement Entity Location"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("G", "MIC (ISO 10383 - Market Identificer Code)"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("H", "CSD participant/member code"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("6", "UK National Insurance or Pension Number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("7", "US Social Security Number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("8", "US Employer or Tax ID Number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("9", "Australian Business Number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("A", "Australian Tax File Number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("I", "Directed broker three character acronym as defined in ISITC"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("1", "Korean Investor ID"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("2", "Taiwanese Qualified Foreign Investor ID QFII/FID"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("3", "Taiwanese Trading Acct"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("4", "Malaysian Central Depository (MCD) number"));
		partyIDSourceComboBoxEntries.add(new ComboBoxEntry("5", "Chinese Investor ID"));

		partyIDSourceComboBox = new JComboBox(partyIDSourceComboBoxEntries.toArray());
		partyIDSourceComboBox.setMinimumSize(new Dimension(32, 25));
		partyIDSourceComboBox.setPreferredSize(new Dimension(32, 25));
		partyIDSourceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_partyIDSourceComboBox = new GridBagConstraints();
		gbc_partyIDSourceComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_partyIDSourceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDSourceComboBox.gridx = 6;
		gbc_partyIDSourceComboBox.gridy = 2;
		panel.add(partyIDSourceComboBox, gbc_partyIDSourceComboBox);

		partyIDSourceComboBox.addActionListener(actionListener);
		
		JLabel securityIDSourceLabel = new JLabel("Security ID Source");
		securityIDSourceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityIDSourceLabel = new GridBagConstraints();
		gbc_securityIDSourceLabel.anchor = GridBagConstraints.WEST;
		gbc_securityIDSourceLabel.insets = new Insets(0, 50, 5, 10);
		gbc_securityIDSourceLabel.gridx = 4;
		gbc_securityIDSourceLabel.gridy = 3;
		panel.add(securityIDSourceLabel, gbc_securityIDSourceLabel);

		List<ComboBoxEntry> securityIdentifierSourceComboBoxEntries = new ArrayList<ComboBoxEntry>();
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry("Default"));
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

		securityIDSourceComboBox = new JComboBox(securityIdentifierSourceComboBoxEntries.toArray());
		securityIDSourceComboBox.setMinimumSize(new Dimension(32, 25));
		securityIDSourceComboBox.setPreferredSize(new Dimension(32, 25));
		securityIDSourceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_securityIDSourceComboBoxEntries = new GridBagConstraints();
		gbc_securityIDSourceComboBoxEntries.insets = new Insets(0, 0, 5, 0);
		gbc_securityIDSourceComboBoxEntries.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityIDSourceComboBoxEntries.gridx = 6;
		gbc_securityIDSourceComboBoxEntries.gridy = 3;
		panel.add(securityIDSourceComboBox, gbc_securityIDSourceComboBoxEntries);

		securityIDSourceComboBox.addActionListener(actionListener);
		
		JLabel sendMarketDataLabel = new JLabel("Send Market Data");
		sendMarketDataLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sendMarketDataLabel = new GridBagConstraints();
		gbc_sendMarketDataLabel.anchor = GridBagConstraints.WEST;
		gbc_sendMarketDataLabel.insets = new Insets(0, 50, 5, 50);
		gbc_sendMarketDataLabel.gridx = 4;
		gbc_sendMarketDataLabel.gridy = 4;
		panel.add(sendMarketDataLabel, gbc_sendMarketDataLabel);

		List<ComboBoxEntry>  sendMarketDataComboBoxEntries = new ArrayList<ComboBoxEntry>();
		sendMarketDataComboBoxEntries.add(new ComboBoxEntry("MAKET_DATA_REQUEST", "on MarketDataRequest"));
		sendMarketDataComboBoxEntries.add(new ComboBoxEntry("UNSUBSCRIBED_INCREMENTAL_REFRESH", "always as MarketDataIncrementalRefresh"));
		sendMarketDataComboBoxEntries.add(new ComboBoxEntry("UNSUBSCRIBED_FULL_REFRESH", "always as MarketDataSnapshotFullRefresh"));

		sendMarketDataComboBox = new JComboBox(sendMarketDataComboBoxEntries.toArray());
		sendMarketDataComboBox.setMinimumSize(new Dimension(32, 25));
		sendMarketDataComboBox.setPreferredSize(new Dimension(32, 25));
		sendMarketDataComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_sendMarketDataComboBox = new GridBagConstraints();
		gbc_sendMarketDataComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_sendMarketDataComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendMarketDataComboBox.gridx = 6;
		gbc_sendMarketDataComboBox.gridy = 4;
		panel.add(sendMarketDataComboBox, gbc_sendMarketDataComboBox);

		sendMarketDataComboBox.addActionListener(actionListener);

		JLabel partyRoleLabel = new JLabel("Market Data Party Role");
		partyRoleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_additionalInformationLabel = new GridBagConstraints();
		gbc_additionalInformationLabel.anchor = GridBagConstraints.WEST;
		gbc_additionalInformationLabel.insets = new Insets(0, 50, 5, 5);
		gbc_additionalInformationLabel.gridx = 4;
		gbc_additionalInformationLabel.gridy = 5;
		panel.add(partyRoleLabel, gbc_additionalInformationLabel);
		
		partyRoleWarningLabel = new JLabel();
		partyRoleWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_partyRoleWarningLabelWarningLabel = new GridBagConstraints();
		gbc_partyRoleWarningLabelWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_partyRoleWarningLabelWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_partyRoleWarningLabelWarningLabel.gridx = 5;
		gbc_partyRoleWarningLabelWarningLabel.gridy = 5;
		panel.add(partyRoleWarningLabel, gbc_partyRoleWarningLabelWarningLabel);

		List<ComboBoxEntry> partyRoleComboBoxEntries = new ArrayList<ComboBoxEntry>();
		partyRoleComboBoxEntries.add(new ComboBoxEntry());
		partyRoleComboBoxEntries.add(new ComboBoxEntry(1, "Executing Firm "));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(2, "Broker of Credit"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(3, "Client ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(4, "Clearing Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(5, "Investor ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(6, "Introducing Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(7, "Entering Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(8, "Locate / Lending Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(9, "Fund Manager Client ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(10, "Settlement Location"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(11, "Order Origination Trader"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(12, "Executing Trader"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(13, "Order Origination Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(14, "Giveup Clearing Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(15, "Correspondant Clearing Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(16, "Executing System"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(17, "Contra Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(18, "Contra Clearing Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(19, "Sponsoring Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(20, "Underlying Contra Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(21, "Clearing Organization"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(22, "Exchange"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(24, "Customer Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(25, "Correspondent Clearing Organization"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(26, "Correspondent Broker"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(27, "Buyer/Seller (Receiver/Deliverer)"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(28, "Custodian"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(29, "Intermediary"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(30, "Agent"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(31, "Sub-custodian"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(32, "Beneficiary"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(33, "Interested party"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(34, "Regulatory body"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(35, "Liquidity provider"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(36, "Entering trader"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(37, "Contra trader"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(38, "Position account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(39, "Contra Investor ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(40, "Transfer to Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(41, "Contra Position Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(42, "Contra Exchange"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(43, "Internal Carry Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(44, "Order Entry Operator ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(45, "Secondary Account Number"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(46, "Foreign Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(47, "Third Party Allocation Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(48, "Claiming Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(49, "Asset Manager"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(50, "Pledgor Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(51, "Pledgee Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(52, "Large Trader Reportable Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(53, "Trader mnemonic"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(54, "Sender Location"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(55, "Session ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(56, "Acceptable Counterparty"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(57, "Unacceptable Counterparty"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(58, "Entering Unit"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(59, "Executing Unit"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(60, "Introducing Broker"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(61, "Quote originator"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(62, "Report originator"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(63, "Systematic internaliser"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(64, "Multilateral Trading Facility"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(65, "Regulated Market"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(66, "Market Maker"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(67, "Investment Firm"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(68, "Host Competent Authority"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(69, "Home Competent Authority"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(70, "Competent Authority of the most relevant market in terms of liquidity"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(71, "Competent Authority of the Transaction (Execution) Venue"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(72, "Reporting intermediary"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(73, "Execution Venue"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(74, "Market data entry originator"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(75, "Location ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(76, "Desk ID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(77, "Market data market"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(78, "Allocation Entity"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(79, "Prime Broker providing General Trade Services"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(80, "Step-Out Firm (Prime Broker)"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(81, "BrokerClearingID"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(82, "Central Registration Depository"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(83, "Clearing Account"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(84, "Acceptable Settling Counterparty"));
		partyRoleComboBoxEntries.add(new ComboBoxEntry(85, "Unacceptable Settling Counterparty"));

		partyRoleComboBox = new JComboBox(partyRoleComboBoxEntries.toArray());
		partyRoleComboBox.setMinimumSize(new Dimension(32, 25));
		partyRoleComboBox.setPreferredSize(new Dimension(32, 25));
		partyRoleComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_additionalInformationComboBox = new GridBagConstraints();
		gbc_additionalInformationComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_additionalInformationComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_additionalInformationComboBox.gridx = 6;
		gbc_additionalInformationComboBox.gridy = 5;
		panel.add(partyRoleComboBox, gbc_additionalInformationComboBox);

		partyRoleComboBox.addActionListener(actionListener);

		JLabel persistMessagesLabel = new JLabel("Persist Messages");
		persistMessagesLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_persistMessagesLabel = new GridBagConstraints();
		gbc_persistMessagesLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_persistMessagesLabel.insets = new Insets(0, 50, 5, 5);
		gbc_persistMessagesLabel.gridx = 4;
		gbc_persistMessagesLabel.gridy = 6;
		panel.add(persistMessagesLabel, gbc_persistMessagesLabel);

		persistMessagesCheckBox = new JCheckBox();
		persistMessagesCheckBox.setPreferredSize(new Dimension(25, 25));
		persistMessagesCheckBox.setBackground(new Color(204, 216, 255));
		persistMessagesCheckBox.addActionListener(actionListener);
		GridBagConstraints gbc_persistMessagesCheckBox = new GridBagConstraints();
		gbc_persistMessagesCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_persistMessagesCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_persistMessagesCheckBox.gridx = 6;
		gbc_persistMessagesCheckBox.gridy = 6;
		panel.add(persistMessagesCheckBox, gbc_persistMessagesCheckBox);

		updateContent();
	}

	private boolean dirtyFieldCheck(String value, JTextField jTextField, JLabel label) {

		if (jTextField.getText().trim().length() == 0) {
			label.setToolTipText("Required field is empty");
			label.setIcon(fixAcceptorEditor.getBugIcon());
			consistent = false;
		}
		else {
			label.setToolTipText(null);
			label.setIcon(null);
		}

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}
	
	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}

	private boolean dirtyFieldCheck(Integer value, JTextField jTextField, JLabel label) {

		if (jTextField.getText().trim().length() == 0) {
			label.setToolTipText("Required field is empty");
			label.setIcon(fixAcceptorEditor.getBugIcon());
		}
		else {
			label.setToolTipText(null);
			label.setIcon(null);
		}

		Integer value2 = null;
		try {
			value2 = integerFormat.parse(jTextField.getText()).intValue();
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

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("FIX Initiator name is empty");
			nameWarningLabel.setIcon(fixAcceptorEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (fixAcceptorEditor.getAbstractBusinessObject().getName() == null
					|| !fixAcceptorEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				fixAcceptorEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}

		consistent = consistent && valid;

		if ((fixAcceptorEditor.getFixAcceptor().getDataDictionary() == null || fixAcceptorEditor.getFixAcceptor().getDataDictionary().trim().length() == 0)
				&& !(dataDictionaryComboBox.getSelectedIndex() == -1))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getDataDictionary() != null && fixAcceptorEditor.getFixAcceptor().getDataDictionary().trim().length() > 0
				&& !fixAcceptorEditor.getFixAcceptor().getDataDictionary().equals(((String) dataDictionaryComboBox.getSelectedItem())))
			dirty = true;

		if (dirtyFieldCheck(fixAcceptorEditor.getFixAcceptor().getSocketAdress(), socketAdressTextField, socketAdressWarningLabel))
			dirty = true;

		if (dirtyFieldCheck(fixAcceptorEditor.getFixAcceptor().getSocketPort(), socketPortTextField, socketPortWarningLabel))
			dirty = true;

		if (dirtyFieldCheck(fixAcceptorEditor.getFixAcceptor().getSenderCompID(), senderCompIDTextField, senderCompIDWarningLabel))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getConnectAtStartup() != connectAtStartUpCheckBox.isSelected())
			dirty = true;
		
		if (fixAcceptorEditor.getFixAcceptor().getPersistMessage() != persistMessagesCheckBox.isSelected())
			dirty = true;
		
		if (dirtyFieldCheck(fixAcceptorEditor.getFixAcceptor().getMarketName(), marketTextField, marketWarningLabel))
			dirty = true;
		
		if (dirtyFieldCheck(fixAcceptorEditor.getFixAcceptor().getPartyID(), partyIDTextField))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getPartyIDSource() != null
				&& !fixAcceptorEditor.getFixAcceptor().getPartyIDSource().equals(((ComboBoxEntry) partyIDSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getPartyIDSource() == null && partyIDSourceComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (fixAcceptorEditor.getFixAcceptor().getSecurityIDSource() != null
				&& !fixAcceptorEditor.getFixAcceptor().getSecurityIDSource().equals(((ComboBoxEntry) securityIDSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getSecurityIDSource() == null && securityIDSourceComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getPartyRole() != null
				&& !fixAcceptorEditor.getFixAcceptor().getPartyRole().equals(((ComboBoxEntry) partyRoleComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (fixAcceptorEditor.getFixAcceptor().getPartyRole() == null && partyRoleComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if(fixAcceptorEditor.getFixAcceptor().getMarketDataType()!=MarketDataType.valueOf((String)((ComboBoxEntry) sendMarketDataComboBox.getSelectedItem()).getEntry()))
			dirty=true;

		if (partyIDTextField.getText() == null || partyIDTextField.getText().trim().length() == 0) {
			
			if (partyRoleComboBox.getSelectedIndex() > 0) {
				
				partyIDWarningLabel.setToolTipText("Party ID must be set, if a role is selected.");
				partyIDWarningLabel.setIcon(fixAcceptorEditor.getBugIcon());
				consistent = false;
			}
			else if (partyIDSourceComboBox.getSelectedIndex() > 0) {
				
				partyIDWarningLabel.setToolTipText("Party ID must be set, if a source is selected.");
				partyIDWarningLabel.setIcon(fixAcceptorEditor.getBugIcon());
				consistent = false;
			}
			else {				
				partyIDWarningLabel.setToolTipText(null);
				partyIDWarningLabel.setIcon(null);
			}
			partyRoleWarningLabel.setToolTipText(null);
			partyRoleWarningLabel.setIcon(null);
			partyIDSourceWarningLabel.setToolTipText(null);
			partyIDSourceWarningLabel.setIcon(null);

		}
		else {
			partyIDWarningLabel.setToolTipText(null);
			partyIDWarningLabel.setIcon(null);

			if (partyRoleComboBox.getSelectedIndex() == 0) {
				
				partyRoleWarningLabel.setToolTipText("Party Role is not set.");
				partyRoleWarningLabel.setIcon(fixAcceptorEditor.getWarningIcon());
			}
			else {
				
				partyRoleWarningLabel.setToolTipText(null);
				partyRoleWarningLabel.setIcon(null);
			}

			if (partyIDSourceComboBox.getSelectedIndex() == 0) {
				
				partyIDSourceWarningLabel.setToolTipText("Party ID Source is not set.");
				partyIDSourceWarningLabel.setIcon(fixAcceptorEditor.getWarningIcon());
			}
			else {
				
				partyIDSourceWarningLabel.setToolTipText(null);
				partyIDSourceWarningLabel.setIcon(null);
			}
		}

		fixAcceptorEditor.checkDirty();
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

		fixAcceptorEditor.getFixAcceptor().setName(nameTextField.getText().trim());

		fixAcceptorEditor.getFixAcceptor().setDataDictionary(((String) dataDictionaryComboBox.getSelectedItem()));

		fixAcceptorEditor.getFixAcceptor().setSocketAdress(socketAdressTextField.getText().trim());

		fixAcceptorEditor.getFixAcceptor().setSocketPort(Integer.parseInt(socketPortTextField.getText().trim()));

		fixAcceptorEditor.getFixAcceptor().setSenderCompID(senderCompIDTextField.getText().trim());

		fixAcceptorEditor.getFixAcceptor().setConnectAtStartup(connectAtStartUpCheckBox.isSelected());
		
		fixAcceptorEditor.getFixAcceptor().setPersistMessage(persistMessagesCheckBox.isSelected());

		fixAcceptorEditor.getFixAcceptor().setMarketDataRequest(true);
		
		if(marketTextField.getText()==null||marketTextField.getText().trim().length()==0)
			fixAcceptorEditor.getFixAcceptor().setMarketName(null);
		else
			fixAcceptorEditor.getFixAcceptor().setMarketName(marketTextField.getText().trim());
		
		if(partyIDTextField.getText()==null||partyIDTextField.getText().trim().length()==0)
			fixAcceptorEditor.getFixAcceptor().setPartyID(null);
		else
			fixAcceptorEditor.getFixAcceptor().setPartyID(partyIDTextField.getText().trim());
		
		fixAcceptorEditor.getFixAcceptor().setPartyIDSource((String)((ComboBoxEntry)partyIDSourceComboBox.getSelectedItem()).getEntry());
		
		fixAcceptorEditor.getFixAcceptor().setSecurityIDSource((String) ((ComboBoxEntry) securityIDSourceComboBox.getSelectedItem()).getEntry());
		
		fixAcceptorEditor.getFixAcceptor().setPartyRole((Integer)((ComboBoxEntry)partyRoleComboBox.getSelectedItem()).getEntry());
		
		fixAcceptorEditor.getFixAcceptor().setMarketDataType(MarketDataType.valueOf((String)((ComboBoxEntry) sendMarketDataComboBox.getSelectedItem()).getEntry()));

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(fixAcceptorEditor.getFixAcceptor().getName());
		
		marketTextField.setText(fixAcceptorEditor.getFixAcceptor().getMarketName());
		
		partyIDTextField.setText(fixAcceptorEditor.getFixAcceptor().getPartyID());
		
		partyIDSourceComboBox.setSelectedItem(new ComboBoxEntry(fixAcceptorEditor.getFixAcceptor().getPartyIDSource(), null));
		
		securityIDSourceComboBox.setSelectedItem(new ComboBoxEntry(fixAcceptorEditor.getFixAcceptor().getSecurityIDSource(), null));
		
		if(fixAcceptorEditor.getFixAcceptor().getMarketDataType()==MarketDataType.UNSUBSCRIBED_INCREMENTAL_REFRESH)
			sendMarketDataComboBox.setSelectedIndex(1);
		else if(fixAcceptorEditor.getFixAcceptor().getMarketDataType()==MarketDataType.UNSUBSCRIBED_FULL_REFRESH)
			sendMarketDataComboBox.setSelectedIndex(2);
		else
			sendMarketDataComboBox.setSelectedIndex(0);
		
		partyRoleComboBox.setSelectedItem(new ComboBoxEntry(fixAcceptorEditor.getFixAcceptor().getPartyRole(), null));

		if (fixAcceptorEditor.getFixAcceptor().getDataDictionary() != null)
			dataDictionaryComboBox.setSelectedItem(fixAcceptorEditor.getFixAcceptor().getDataDictionary());

		socketAdressTextField.setText(fixAcceptorEditor.getFixAcceptor().getSocketAdress());

		if (fixAcceptorEditor.getFixAcceptor().getSocketPort() != null)
			socketPortTextField.setText(integerFormat.format(fixAcceptorEditor.getFixAcceptor().getSocketPort()));
		else
			socketPortTextField.setText("");

		senderCompIDTextField.setText(fixAcceptorEditor.getFixAcceptor().getSenderCompID());

		if (fixAcceptorEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(fixAcceptorEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(fixAcceptorEditor.getAbstractBusinessObject().getModificationDate()));

			if (fixAcceptorEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(fixAcceptorEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		connectAtStartUpCheckBox.setSelected(fixAcceptorEditor.getFixAcceptor().getConnectAtStartup());
		
		persistMessagesCheckBox.setSelected(fixAcceptorEditor.getFixAcceptor().getPersistMessage());

		if (fixAcceptorEditor.getBasisClientConnector().getFUser().canWrite(fixAcceptorEditor.getAbstractBusinessObject())
				&& fixAcceptorEditor.getFixAcceptor().getStartLevel() == 0) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			
			marketTextField.setBackground(new Color(255, 243, 204));
			marketTextField.setEditable(true);
			
			partyIDTextField.setBackground(new Color(255, 243, 204));
			partyIDTextField.setEditable(true);
			
			partyRoleComboBox.setBackground(new Color(255, 243, 204));
			partyRoleComboBox.setEnabled(true);
			
			partyIDSourceComboBox.setBackground(new Color(255, 243, 204));
			partyIDSourceComboBox.setEnabled(true);
			
			securityIDSourceComboBox.setBackground(new Color(255, 243, 204));
			securityIDSourceComboBox.setEnabled(true);

			dataDictionaryComboBox.setBackground(new Color(255, 243, 204));
			dataDictionaryComboBox.setEnabled(true);
			
			sendMarketDataComboBox.setBackground(new Color(255, 243, 204));
			sendMarketDataComboBox.setEnabled(true);

			socketAdressTextField.setBackground(new Color(255, 243, 204));
			socketAdressTextField.setEditable(true);

			socketPortTextField.setBackground(new Color(255, 243, 204));
			socketPortTextField.setEditable(true);

			senderCompIDTextField.setBackground(new Color(255, 243, 204));
			senderCompIDTextField.setEditable(true);

			connectAtStartUpCheckBox.setEnabled(true);
			
			persistMessagesCheckBox.setEnabled(true);

		}
		else {

			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			
			marketTextField.setBackground(new Color(204, 216, 255));
			marketTextField.setEditable(false);
			
			partyIDTextField.setBackground(new Color(204, 216, 255));
			partyIDTextField.setEditable(false);
			
			partyRoleComboBox.setBackground(new Color(204, 216, 255));
			partyRoleComboBox.setEnabled(false);
			
			partyIDSourceComboBox.setBackground(new Color(204, 216, 255));
			partyIDSourceComboBox.setEnabled(false);
			
			securityIDSourceComboBox.setBackground(new Color(204, 216, 255));
			securityIDSourceComboBox.setEnabled(false);

			dataDictionaryComboBox.setBackground(new Color(204, 216, 255));
			dataDictionaryComboBox.setEnabled(false);
			
			sendMarketDataComboBox.setBackground(new Color(204, 216, 255));
			sendMarketDataComboBox.setEnabled(false);

			socketAdressTextField.setBackground(new Color(204, 216, 255));
			socketAdressTextField.setEditable(false);

			socketPortTextField.setBackground(new Color(204, 216, 255));
			socketPortTextField.setEditable(false);

			senderCompIDTextField.setBackground(new Color(204, 216, 255));
			senderCompIDTextField.setEditable(false);

			connectAtStartUpCheckBox.setEnabled(false);
			
			persistMessagesCheckBox.setEnabled(false);
		}

		checkConsistency();
	}
}
