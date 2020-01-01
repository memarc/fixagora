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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;

/**
 * The Class CounterpartyEditorMasterData.
 */
public class CounterpartyEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private CounterpartyEditor counterpartyEditor = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;
	private JTextField streetTextField;
	private JTextField houseNumberTextField;
	private JTextField postalCodeTextField;
	private JTextField cityTextField;

	private JComboBox countryComboBox;

	private JComboBox timeZoneComboBox;

	private boolean consistent;

	/**
	 * Instantiates a new counterparty editor master data.
	 *
	 * @param counterpartyEditor the counterparty editor
	 */
	public CounterpartyEditorMasterData(CounterpartyEditor counterpartyEditor) {

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

		this.counterpartyEditor = counterpartyEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 223, 0, 0};
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		gbc_nameLabel.insets = new Insets(50, 25, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = counterpartyEditor.getNameWarningLabel();
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
		gbc_nameTextField.gridwidth = 3;
		gbc_nameTextField.insets = new Insets(50, 0, 5, 25);
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 0;
		panel.add(nameTextField, gbc_nameTextField);
		
		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 7;
		gbc_rightFillPanel.weightx = 1.0;
		gbc_rightFillPanel.insets = new Insets(0, 0, 5, 0);
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 6;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JLabel modifiedLabel = new JLabel("Modified");
		modifiedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_modifiedLabel = new GridBagConstraints();
		gbc_modifiedLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedLabel.insets = new Insets(0, 25, 5, 5);
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
		gbc_textField.gridwidth = 3;
		gbc_textField.insets = new Insets(0, 0, 5, 25);
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		panel.add(modifiedField, gbc_textField);		
		
		JLabel streetAdressLabel = new GradientLabel();
		streetAdressLabel.setText("Communication");
		streetAdressLabel.setText("Street Address");
		streetAdressLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		streetAdressLabel.setHorizontalAlignment(SwingConstants.CENTER);
		streetAdressLabel.setPreferredSize(new Dimension(99, 25));
		streetAdressLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_streetAdressLabel = new GridBagConstraints();
		gbc_streetAdressLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_streetAdressLabel.gridwidth = 5;
		gbc_streetAdressLabel.insets = new Insets(25, 0, 15, 5);
		gbc_streetAdressLabel.gridx = 1;
		gbc_streetAdressLabel.gridy = 2;
		panel.add(streetAdressLabel, gbc_streetAdressLabel);
		
		JLabel streetLabel = new JLabel("Street / House number");
		streetLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_streetLabel = new GridBagConstraints();
		gbc_streetLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_streetLabel.insets = new Insets(0, 25, 5, 5);
		gbc_streetLabel.gridx = 1;
		gbc_streetLabel.gridy = 3;
		panel.add(streetLabel, gbc_streetLabel);
		
		streetTextField = new JTextField();
		streetTextField.setMaximumSize(new Dimension(225, 25));
		streetTextField.setMinimumSize(new Dimension(225, 25));
		streetTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		streetTextField.setPreferredSize(new Dimension(225, 25));
		streetTextField.getDocument().addDocumentListener(documentListener);
		streetTextField.setColumns(10);
		GridBagConstraints gbc_streetTextField = new GridBagConstraints();
		gbc_streetTextField.gridwidth = 2;
		gbc_streetTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_streetTextField.insets = new Insets(0, 0, 5, 5);
		gbc_streetTextField.gridx = 3;
		gbc_streetTextField.gridy = 3;
		panel.add(streetTextField, gbc_streetTextField);
			
		houseNumberTextField = new JTextField();
		houseNumberTextField.setMaximumSize(new Dimension(50, 25));
		houseNumberTextField.setPreferredSize(new Dimension(50, 25));
		houseNumberTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		houseNumberTextField.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_houseNumberTextField = new GridBagConstraints();
		gbc_houseNumberTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_houseNumberTextField.insets = new Insets(0, 0, 5, 25);
		gbc_houseNumberTextField.gridx = 5;
		gbc_houseNumberTextField.gridy = 3;
		panel.add(houseNumberTextField, gbc_houseNumberTextField);
		houseNumberTextField.setColumns(3);
		
		JLabel lblPostalCode = new JLabel("Postal Code / City");
		lblPostalCode.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblPostalCode = new GridBagConstraints();
		gbc_lblPostalCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPostalCode.insets = new Insets(0, 25, 5, 5);
		gbc_lblPostalCode.gridx = 1;
		gbc_lblPostalCode.gridy = 4;
		panel.add(lblPostalCode, gbc_lblPostalCode);
		
		postalCodeTextField = new JTextField();
		postalCodeTextField.setMaximumSize(new Dimension(75, 25));
		postalCodeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		postalCodeTextField.setPreferredSize(new Dimension(75, 25));
		postalCodeTextField.getDocument().addDocumentListener(documentListener);
		postalCodeTextField.setColumns(4);
		GridBagConstraints gbc_postalCodeTextField = new GridBagConstraints();
		gbc_postalCodeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_postalCodeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_postalCodeTextField.gridx = 3;
		gbc_postalCodeTextField.gridy = 4;
		panel.add(postalCodeTextField, gbc_postalCodeTextField);
		
		cityTextField = new JTextField();
		cityTextField.setMaximumSize(new Dimension(175, 25));
		cityTextField.setMinimumSize(new Dimension(175, 25));
		cityTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		cityTextField.setPreferredSize(new Dimension(175, 25));
		cityTextField.getDocument().addDocumentListener(documentListener);
		cityTextField.setColumns(10);
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.gridwidth = 2;
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.insets = new Insets(0, 0, 5, 25);
		gbc_textField_3.gridx = 4;
		gbc_textField_3.gridy = 4;
		panel.add(cityTextField, gbc_textField_3);
		
		
		JLabel lblCountry = new JLabel("Country");
		lblCountry.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblCountry = new GridBagConstraints();
		gbc_lblCountry.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCountry.insets = new Insets(0, 25, 5, 5);
		gbc_lblCountry.gridx = 1;
		gbc_lblCountry.gridy = 5;
		panel.add(lblCountry, gbc_lblCountry);
		
		List<String> countries = new ArrayList<String>();
		Locale[] localeList = NumberFormat.getAvailableLocales();
		for (Locale locale : localeList)
			if (!countries.contains(locale.getDisplayCountry()))
				countries.add(locale.getDisplayCountry());

		Collections.sort(countries);
		
		countryComboBox = new JComboBox(countries.toArray());
		countryComboBox.setPreferredSize(new Dimension(175, 25));
		countryComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.gridx = 3;
		gbc_comboBox.gridy = 5;
		panel.add(countryComboBox, gbc_comboBox);
		
		countryComboBox.addActionListener(actionListener);

		
		JLabel lblTimeZone = new JLabel("Time zone");
		lblTimeZone.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblTimeZone = new GridBagConstraints();
		gbc_lblTimeZone.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTimeZone.insets = new Insets(0, 25, 50, 5);
		gbc_lblTimeZone.gridx = 1;
		gbc_lblTimeZone.gridy = 6;
		panel.add(lblTimeZone, gbc_lblTimeZone);
		
		List<String> timeZones = new ArrayList<String>();
		timeZones.addAll(Arrays.asList(TimeZone.getAvailableIDs()));

		Collections.sort(timeZones);
		
		timeZoneComboBox = new JComboBox(timeZones.toArray());
		timeZoneComboBox.setPreferredSize(new Dimension(32, 25));
		timeZoneComboBox.setMinimumSize(new Dimension(32, 25));
		timeZoneComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridwidth = 2;
		gbc_comboBox_1.insets = new Insets(0, 0, 50, 5);
		gbc_comboBox_1.gridx = 3;
		gbc_comboBox_1.gridy = 6;
		panel.add(timeZoneComboBox, gbc_comboBox_1);
		
		timeZoneComboBox.addActionListener(actionListener);

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

			nameWarningLabel.setToolTipText("Role name is empty");
			nameWarningLabel.setIcon(counterpartyEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (counterpartyEditor.getAbstractBusinessObject().getName() == null || !counterpartyEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				counterpartyEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}
		
		consistent = consistent && valid;
		
		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getStreet(), streetTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getHouse(), houseNumberTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getPostalCode(), postalCodeTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getCity(), cityTextField))
			dirty = true;

		if ((counterpartyEditor.getCounterparty().getCountry() == null || counterpartyEditor.getCounterparty().getCountry().trim().length() == 0)
				&& !(countryComboBox.getSelectedIndex() == -1))
			dirty = true;
		

		if (counterpartyEditor.getCounterparty().getCountry() != null && counterpartyEditor.getCounterparty().getCountry().trim().length() > 0
				&& !counterpartyEditor.getCounterparty().getCountry().equals(countryComboBox.getSelectedItem()))
			dirty = true;
		
		if ((counterpartyEditor.getCounterparty().getFtimezone() == null || counterpartyEditor.getCounterparty().getFtimezone().trim().length() == 0)
				&& !(timeZoneComboBox.getSelectedIndex() == -1))
			dirty = true;		

		if (counterpartyEditor.getCounterparty().getFtimezone() != null && counterpartyEditor.getCounterparty().getFtimezone().trim().length() > 0
				&& !counterpartyEditor.getCounterparty().getFtimezone().equals(timeZoneComboBox.getSelectedItem()))
			dirty = true;

		counterpartyEditor.checkDirty();
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

		counterpartyEditor.getCounterparty().setName(nameTextField.getText().trim());
		
		counterpartyEditor.getCounterparty().setStreet(streetTextField.getText().trim());
		
		counterpartyEditor.getCounterparty().setHouse(houseNumberTextField.getText().trim());

		counterpartyEditor.getCounterparty().setPostalCode(postalCodeTextField.getText().trim());

		counterpartyEditor.getCounterparty().setCity(cityTextField.getText().trim());

		counterpartyEditor.getCounterparty().setFtimezone((String) timeZoneComboBox.getSelectedItem());
		
		counterpartyEditor.getCounterparty().setCountry((String) countryComboBox.getSelectedItem());
		
	}

	/**
	 * Update content.
	 */
	public void updateContent() {
		
		nameTextField.setText(counterpartyEditor.getCounterparty().getName());
		
		streetTextField.setText(counterpartyEditor.getCounterparty().getStreet());
		
		houseNumberTextField.setText(counterpartyEditor.getCounterparty().getHouse());
		
		postalCodeTextField.setText(counterpartyEditor.getCounterparty().getPostalCode());
		
		cityTextField.setText(counterpartyEditor.getCounterparty().getCity());
		
		timeZoneComboBox.setSelectedItem(counterpartyEditor.getCounterparty().getFtimezone());
		
		countryComboBox.setSelectedItem(counterpartyEditor.getCounterparty().getCountry());
		
		if (counterpartyEditor.getAbstractBusinessObject().getId() != 0) {
			
			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(counterpartyEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(counterpartyEditor.getAbstractBusinessObject().getModificationDate()));
			
			if (counterpartyEditor.getAbstractBusinessObject().getModificationUser() != null) {
				
				textBuffer.append(" by ");
				textBuffer.append(counterpartyEditor.getAbstractBusinessObject().getModificationUser());
			}
			
			modifiedField.setText(textBuffer.toString());
		}
		else {
			
			modifiedField.setText("New Business Object");
		}
		
		if (counterpartyEditor.getBasisClientConnector().getFUser().canWrite(counterpartyEditor.getAbstractBusinessObject())) {
			
			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			
			streetTextField.setBackground(new Color(255, 243, 204));
			streetTextField.setEditable(true);
			
			houseNumberTextField.setBackground(new Color(255, 243, 204));
			houseNumberTextField.setEditable(true);
			
			postalCodeTextField.setBackground(new Color(255, 243, 204));
			postalCodeTextField.setEditable(true);
			
			cityTextField.setBackground(new Color(255, 243, 204));
			cityTextField.setEditable(true);
			
			countryComboBox.setBackground(new Color(255, 243, 204));
			countryComboBox.setEnabled(true);
			
			timeZoneComboBox.setBackground(new Color(255, 243, 204));
			timeZoneComboBox.setEnabled(true);			
		}
		else {
			
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			
			streetTextField.setBackground(new Color(204, 216, 255));
			streetTextField.setEditable(false);
			
			houseNumberTextField.setBackground(new Color(204, 216, 255));
			houseNumberTextField.setEditable(false);
			
			postalCodeTextField.setBackground(new Color(204, 216, 255));
			postalCodeTextField.setEditable(false);
			
			cityTextField.setBackground(new Color(204, 216, 255));
			cityTextField.setEditable(false);
			
			countryComboBox.setBackground(new Color(204, 216, 255));
			countryComboBox.setEnabled(false);
			
			timeZoneComboBox.setBackground(new Color(204, 216, 255));
			timeZoneComboBox.setEnabled(false);						
		}
		
		checkConsistency();
	}
}
