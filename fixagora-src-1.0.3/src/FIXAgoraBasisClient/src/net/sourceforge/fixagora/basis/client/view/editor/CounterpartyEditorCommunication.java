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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
 * The Class CounterpartyEditorCommunication.
 */
public class CounterpartyEditorCommunication extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField telephoneTextField = null;

	private CounterpartyEditor counterpartyEditor = null;

	private boolean dirty = false;

	private JTextField poBoxTextField;

	private JTextField postalCodeTextField;

	private JTextField companyPostalCodeTextField;

	private JTextField mobilePhoneTextField = null;

	private JTextField faxTextField = null;

	private JTextField emailTextField = null;

	private JComboBox languageComboBox = null;

	/**
	 * Instantiates a new counterparty editor communication.
	 *
	 * @param counterpartyEditor the counterparty editor
	 */
	public CounterpartyEditorCommunication(CounterpartyEditor counterpartyEditor) {

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
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 95, 192, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel academicTitleLabel = new JLabel("PO Box");
		academicTitleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.insets = new Insets(50, 50, 5, 25);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 0;
		panel.add(academicTitleLabel, gbc_lblNewLabel_5);

		poBoxTextField = new JTextField();
		poBoxTextField.getDocument().addDocumentListener(documentListener);
		poBoxTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		poBoxTextField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_poBoxTextField = new GridBagConstraints();
		gbc_poBoxTextField.gridwidth = 2;
		gbc_poBoxTextField.insets = new Insets(50, 0, 5, 50);
		gbc_poBoxTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_poBoxTextField.gridx = 3;
		gbc_poBoxTextField.gridy = 0;
		panel.add(poBoxTextField, gbc_poBoxTextField);
		poBoxTextField.setColumns(10);

		JLabel functionLabel = new JLabel("Postal Code");
		functionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_6.gridx = 1;
		gbc_lblNewLabel_6.gridy = 1;
		panel.add(functionLabel, gbc_lblNewLabel_6);

		postalCodeTextField = new JTextField();
		postalCodeTextField.getDocument().addDocumentListener(documentListener);
		postalCodeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		postalCodeTextField.setPreferredSize(new Dimension(250, 25));
		postalCodeTextField.setColumns(10);
		GridBagConstraints gbc_postalCodeTextField = new GridBagConstraints();
		gbc_postalCodeTextField.gridwidth = 2;
		gbc_postalCodeTextField.insets = new Insets(0, 0, 5, 50);
		gbc_postalCodeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_postalCodeTextField.gridx = 3;
		gbc_postalCodeTextField.gridy = 1;
		panel.add(postalCodeTextField, gbc_postalCodeTextField);

		JLabel departmentLabel = new JLabel("Company Postal Code");
		departmentLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_7.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_7.gridx = 1;
		gbc_lblNewLabel_7.gridy = 2;
		panel.add(departmentLabel, gbc_lblNewLabel_7);

		companyPostalCodeTextField = new JTextField();
		companyPostalCodeTextField.getDocument().addDocumentListener(documentListener);
		companyPostalCodeTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
		companyPostalCodeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		companyPostalCodeTextField.setPreferredSize(new Dimension(250, 25));
		companyPostalCodeTextField.setMinimumSize(new Dimension(250, 25));
		companyPostalCodeTextField.setColumns(10);
		GridBagConstraints gbc_companyPostalCodeTextField = new GridBagConstraints();
		gbc_companyPostalCodeTextField.gridwidth = 2;
		gbc_companyPostalCodeTextField.insets = new Insets(0, 0, 5, 50);
		gbc_companyPostalCodeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_companyPostalCodeTextField.gridx = 3;
		gbc_companyPostalCodeTextField.gridy = 2;
		panel.add(companyPostalCodeTextField, gbc_companyPostalCodeTextField);

		JLabel communicationLabel = new GradientLabel();
		communicationLabel.setText("Communication");
		communicationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		communicationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		communicationLabel.setPreferredSize(new Dimension(99, 25));
		communicationLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_communicationLabel = new GridBagConstraints();
		gbc_communicationLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_communicationLabel.gridwidth = 4;
		gbc_communicationLabel.insets = new Insets(25, 0, 15, 5);
		gbc_communicationLabel.gridx = 1;
		gbc_communicationLabel.gridy = 3;
		panel.add(communicationLabel, gbc_communicationLabel);

		JLabel languageLabel = new JLabel("Language");
		languageLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_12.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_12.gridx = 1;
		gbc_lblNewLabel_12.gridy = 4;
		panel.add(languageLabel, gbc_lblNewLabel_12);

		List<String> languages = new ArrayList<String>();
		Locale[] localeList = NumberFormat.getAvailableLocales();
		for (Locale locale : localeList)
			if (!languages.contains(locale.getDisplayLanguage()))
				languages.add(locale.getDisplayLanguage());

		Collections.sort(languages);

		languageComboBox = new JComboBox(languages.toArray());
		languageComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_languageComboBox = new GridBagConstraints();
		gbc_languageComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_languageComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_languageComboBox.gridx = 3;
		gbc_languageComboBox.gridy = 4;
		panel.add(languageComboBox, gbc_languageComboBox);

		languageComboBox.addActionListener(actionListener);

		JLabel phoneLabel = new JLabel("Telephone");
		phoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_phoneLabel = new GridBagConstraints();
		gbc_phoneLabel.insets = new Insets(0, 50, 5, 25);
		gbc_phoneLabel.anchor = GridBagConstraints.WEST;
		gbc_phoneLabel.gridx = 1;
		gbc_phoneLabel.gridy = 5;
		panel.add(phoneLabel, gbc_phoneLabel);

		telephoneTextField = new JTextField();
		telephoneTextField.getDocument().addDocumentListener(documentListener);
		telephoneTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		telephoneTextField.getDocument().addDocumentListener(documentListener);
		telephoneTextField.setBackground(new Color(255, 243, 204));
		telephoneTextField.setPreferredSize(new Dimension(250, 25));
		telephoneTextField.setColumns(10);
		GridBagConstraints gbc_phoneTextField = new GridBagConstraints();
		gbc_phoneTextField.gridwidth = 2;
		gbc_phoneTextField.insets = new Insets(0, 0, 5, 50);
		gbc_phoneTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_phoneTextField.gridx = 3;
		gbc_phoneTextField.gridy = 5;
		panel.add(telephoneTextField, gbc_phoneTextField);

		JLabel mobilePhoneLabel = new JLabel("Mobile Phone");
		mobilePhoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_mobilePhoneLabel = new GridBagConstraints();
		gbc_mobilePhoneLabel.anchor = GridBagConstraints.WEST;
		gbc_mobilePhoneLabel.insets = new Insets(0, 50, 5, 25);
		gbc_mobilePhoneLabel.gridx = 1;
		gbc_mobilePhoneLabel.gridy = 6;
		panel.add(mobilePhoneLabel, gbc_mobilePhoneLabel);

		mobilePhoneTextField = new JTextField();
		mobilePhoneTextField.getDocument().addDocumentListener(documentListener);
		mobilePhoneTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		mobilePhoneTextField.setPreferredSize(new Dimension(250, 25));
		mobilePhoneTextField.setColumns(10);
		GridBagConstraints gbc_mobilePhoneTextField = new GridBagConstraints();
		gbc_mobilePhoneTextField.gridwidth = 2;
		gbc_mobilePhoneTextField.insets = new Insets(0, 0, 5, 50);
		gbc_mobilePhoneTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_mobilePhoneTextField.gridx = 3;
		gbc_mobilePhoneTextField.gridy = 6;
		panel.add(mobilePhoneTextField, gbc_mobilePhoneTextField);

		JLabel faxLabel = new JLabel("Fax");
		faxLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_faxLabel = new GridBagConstraints();
		gbc_faxLabel.anchor = GridBagConstraints.WEST;
		gbc_faxLabel.insets = new Insets(0, 50, 5, 25);
		gbc_faxLabel.gridx = 1;
		gbc_faxLabel.gridy = 7;
		panel.add(faxLabel, gbc_faxLabel);

		faxTextField = new JTextField();
		faxTextField.getDocument().addDocumentListener(documentListener);
		faxTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		faxTextField.setPreferredSize(new Dimension(250, 25));
		faxTextField.setColumns(10);
		GridBagConstraints gbc_faxTextField = new GridBagConstraints();
		gbc_faxTextField.gridwidth = 2;
		gbc_faxTextField.insets = new Insets(0, 0, 5, 50);
		gbc_faxTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_faxTextField.gridx = 3;
		gbc_faxTextField.gridy = 7;
		panel.add(faxTextField, gbc_faxTextField);

		JLabel emailLabel = new JLabel("E-Mail");
		emailLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_emailLabel = new GridBagConstraints();
		gbc_emailLabel.anchor = GridBagConstraints.WEST;
		gbc_emailLabel.insets = new Insets(0, 50, 50, 25);
		gbc_emailLabel.gridx = 1;
		gbc_emailLabel.gridy = 8;
		panel.add(emailLabel, gbc_emailLabel);

		emailTextField = new JTextField();
		emailTextField.getDocument().addDocumentListener(documentListener);
		emailTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		emailTextField.setPreferredSize(new Dimension(250, 25));
		emailTextField.setColumns(10);
		GridBagConstraints gbc_emailTextField = new GridBagConstraints();
		gbc_emailTextField.gridwidth = 2;
		gbc_emailTextField.insets = new Insets(0, 0, 50, 50);
		gbc_emailTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_emailTextField.gridx = 3;
		gbc_emailTextField.gridy = 8;
		panel.add(emailTextField, gbc_emailTextField);

		updateContent();
	}

	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}

	/**
	 * Save.
	 */
	public void save() {

		counterpartyEditor.getCounterparty().setPoBox(poBoxTextField.getText().trim());

		counterpartyEditor.getCounterparty().setPoPostalCode(postalCodeTextField.getText().trim());

		counterpartyEditor.getCounterparty().setCompanyPostalCode(companyPostalCodeTextField.getText().trim());

		counterpartyEditor.getCounterparty().setfLanguage((String) languageComboBox.getSelectedItem());

		counterpartyEditor.getCounterparty().setTelephone(telephoneTextField.getText().trim());

		counterpartyEditor.getCounterparty().setMobilePhone(mobilePhoneTextField.getText().trim());

		counterpartyEditor.getCounterparty().setFax(faxTextField.getText().trim());

		counterpartyEditor.getCounterparty().setEmail(emailTextField.getText().trim());
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return dirty;
	}

	private void checkConsistency() {

		dirty = false;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getPoBox(), poBoxTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getPoPostalCode(), postalCodeTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getCompanyPostalCode(), companyPostalCodeTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getTelephone(), telephoneTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getMobilePhone(), mobilePhoneTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getFax(), faxTextField))
			dirty = true;

		if (dirtyFieldCheck(counterpartyEditor.getCounterparty().getEmail(), emailTextField))
			dirty = true;
		
		if ((counterpartyEditor.getCounterparty().getfLanguage() == null || counterpartyEditor.getCounterparty().getfLanguage().trim().length() == 0)
				&& !(languageComboBox.getSelectedIndex() == -1))
			dirty = true;		

		if (counterpartyEditor.getCounterparty().getfLanguage() != null && counterpartyEditor.getCounterparty().getfLanguage().trim().length() > 0
				&& !counterpartyEditor.getCounterparty().getfLanguage().equals(languageComboBox.getSelectedItem()))
			dirty = true;

		counterpartyEditor.checkDirty();
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		poBoxTextField.setText(counterpartyEditor.getCounterparty().getPoBox());

		postalCodeTextField.setText(counterpartyEditor.getCounterparty().getPoPostalCode());

		companyPostalCodeTextField.setText(counterpartyEditor.getCounterparty().getCompanyPostalCode());

		languageComboBox.setSelectedItem(counterpartyEditor.getCounterparty().getfLanguage());

		telephoneTextField.setText(counterpartyEditor.getCounterparty().getTelephone());

		mobilePhoneTextField.setText(counterpartyEditor.getCounterparty().getMobilePhone());

		faxTextField.setText(counterpartyEditor.getCounterparty().getFax());

		emailTextField.setText(counterpartyEditor.getCounterparty().getEmail());

		if (counterpartyEditor.getBasisClientConnector().getFUser().canWrite(counterpartyEditor.getAbstractBusinessObject())) {

			poBoxTextField.setBackground(new Color(255, 243, 204));
			poBoxTextField.setEnabled(true);

			postalCodeTextField.setBackground(new Color(255, 243, 204));
			postalCodeTextField.setEditable(true);

			companyPostalCodeTextField.setBackground(new Color(255, 243, 204));
			companyPostalCodeTextField.setEditable(true);

			languageComboBox.setBackground(new Color(255, 243, 204));
			languageComboBox.setEnabled(true);

			telephoneTextField.setBackground(new Color(255, 243, 204));
			telephoneTextField.setEditable(true);

			mobilePhoneTextField.setBackground(new Color(255, 243, 204));
			mobilePhoneTextField.setEditable(true);

			faxTextField.setBackground(new Color(255, 243, 204));
			faxTextField.setEditable(true);

			emailTextField.setBackground(new Color(255, 243, 204));
			emailTextField.setEditable(true);
		}
		else {

			poBoxTextField.setBackground(new Color(204, 216, 255));
			poBoxTextField.setEnabled(false);

			postalCodeTextField.setBackground(new Color(204, 216, 255));
			postalCodeTextField.setEditable(false);

			companyPostalCodeTextField.setBackground(new Color(204, 216, 255));
			companyPostalCodeTextField.setEditable(false);

			languageComboBox.setBackground(new Color(204, 216, 255));
			languageComboBox.setEnabled(false);

			telephoneTextField.setBackground(new Color(204, 216, 255));
			telephoneTextField.setEditable(false);

			mobilePhoneTextField.setBackground(new Color(204, 216, 255));
			mobilePhoneTextField.setEditable(false);

			faxTextField.setBackground(new Color(204, 216, 255));
			faxTextField.setEditable(false);

			emailTextField.setBackground(new Color(204, 216, 255));
			emailTextField.setEditable(false);
		}

		checkConsistency();
	}
}
