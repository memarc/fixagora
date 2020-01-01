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
import java.awt.Component;
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
 * The Class FUserEditorAddress.
 */
public class FUserEditorAddress extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField firstNameTextField = null;

	private JTextField lastNameTextField = null;

	private JTextField telephoneTextField = null;

	private FUserEditor userEditor = null;

	private boolean dirty = false;

	private JTextField academicTitleTextField = null;

	private JTextField functionTextField = null;

	private JTextField departmentTextField = null;

	private JTextField roomNumberTextField = null;

	private JTextField floorTextField = null;

	private JTextField buildingTextField = null;

	private JTextField mobilePhoneTextField = null;

	private JTextField faxTextField = null;

	private JTextField emailTextField = null;

	private JComboBox titleComboBox = null;

	private JComboBox languageComboBox = null;

	/**
	 * Instantiates a new f user editor address.
	 *
	 * @param userEditor the user editor
	 */
	public FUserEditorAddress(FUserEditor userEditor) {

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

		this.userEditor = userEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel titleLabel = new JLabel("Titel");
		titleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		titleLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_4.insets = new Insets(50, 50, 10, 25);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 0;
		panel.add(titleLabel, gbc_lblNewLabel_4);

		titleComboBox = new JComboBox();
		titleComboBox.setPreferredSize(new Dimension(32, 25));
		titleComboBox.addItem("");
		titleComboBox.addItem("Mrs.");
		titleComboBox.addItem("Mr.");
		titleComboBox.addItem("Dr.");
		GridBagConstraints gbc_titleComboBox = new GridBagConstraints();
		gbc_titleComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_titleComboBox.insets = new Insets(50, 0, 5, 50);
		gbc_titleComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_titleComboBox.gridx = 3;
		gbc_titleComboBox.gridy = 0;
		panel.add(titleComboBox, gbc_titleComboBox);

		titleComboBox.addActionListener(actionListener);

		JLabel firstNameLabel = new JLabel("First Name");
		firstNameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_firstNameLabel = new GridBagConstraints();
		gbc_firstNameLabel.insets = new Insets(0, 50, 5, 25);
		gbc_firstNameLabel.anchor = GridBagConstraints.WEST;
		gbc_firstNameLabel.gridx = 1;
		gbc_firstNameLabel.gridy = 1;
		panel.add(firstNameLabel, gbc_firstNameLabel);

		firstNameTextField = new JTextField();
		firstNameTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		firstNameTextField.getDocument().addDocumentListener(documentListener);
		firstNameTextField.setBackground(new Color(255, 243, 204));
		firstNameTextField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_firstNameTextField = new GridBagConstraints();
		gbc_firstNameTextField.gridwidth = 3;
		gbc_firstNameTextField.insets = new Insets(0, 0, 5, 50);
		gbc_firstNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstNameTextField.gridx = 3;
		gbc_firstNameTextField.gridy = 1;
		panel.add(firstNameTextField, gbc_firstNameTextField);
		firstNameTextField.setColumns(10);

		JLabel lastNameLabel = new JLabel("Last Name");
		lastNameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lastNameLabel = new GridBagConstraints();
		gbc_lastNameLabel.anchor = GridBagConstraints.WEST;
		gbc_lastNameLabel.insets = new Insets(0, 50, 5, 25);
		gbc_lastNameLabel.gridx = 1;
		gbc_lastNameLabel.gridy = 2;
		panel.add(lastNameLabel, gbc_lastNameLabel);

		lastNameTextField = new JTextField();
		lastNameTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		lastNameTextField.getDocument().addDocumentListener(documentListener);
		lastNameTextField.setBackground(new Color(255, 243, 204));
		lastNameTextField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_lastNameTextField = new GridBagConstraints();
		gbc_lastNameTextField.gridwidth = 3;
		gbc_lastNameTextField.insets = new Insets(0, 0, 5, 50);
		gbc_lastNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_lastNameTextField.gridx = 3;
		gbc_lastNameTextField.gridy = 2;
		panel.add(lastNameTextField, gbc_lastNameTextField);
		lastNameTextField.setColumns(10);

		JLabel academicTitleLabel = new JLabel("Academic Title");
		academicTitleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 3;
		panel.add(academicTitleLabel, gbc_lblNewLabel_5);

		academicTitleTextField = new JTextField();
		academicTitleTextField.getDocument().addDocumentListener(documentListener);
		academicTitleTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		academicTitleTextField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_academicTitleTextField = new GridBagConstraints();
		gbc_academicTitleTextField.gridwidth = 3;
		gbc_academicTitleTextField.insets = new Insets(0, 0, 5, 50);
		gbc_academicTitleTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_academicTitleTextField.gridx = 3;
		gbc_academicTitleTextField.gridy = 3;
		panel.add(academicTitleTextField, gbc_academicTitleTextField);
		academicTitleTextField.setColumns(10);

		JLabel functionLabel = new JLabel("Function");
		functionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_6.gridx = 1;
		gbc_lblNewLabel_6.gridy = 4;
		panel.add(functionLabel, gbc_lblNewLabel_6);

		functionTextField = new JTextField();
		functionTextField.getDocument().addDocumentListener(documentListener);
		functionTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		functionTextField.setPreferredSize(new Dimension(250, 25));
		functionTextField.setColumns(10);
		GridBagConstraints gbc_functionTextField = new GridBagConstraints();
		gbc_functionTextField.gridwidth = 3;
		gbc_functionTextField.insets = new Insets(0, 0, 5, 50);
		gbc_functionTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_functionTextField.gridx = 3;
		gbc_functionTextField.gridy = 4;
		panel.add(functionTextField, gbc_functionTextField);

		JLabel departmentLabel = new JLabel("Department");
		departmentLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_7.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_7.gridx = 1;
		gbc_lblNewLabel_7.gridy = 5;
		panel.add(departmentLabel, gbc_lblNewLabel_7);

		departmentTextField = new JTextField();
		departmentTextField.getDocument().addDocumentListener(documentListener);
		departmentTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
		departmentTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		departmentTextField.setPreferredSize(new Dimension(250, 25));
		departmentTextField.setMinimumSize(new Dimension(250, 25));
		departmentTextField.setColumns(10);
		GridBagConstraints gbc_departmentTextField = new GridBagConstraints();
		gbc_departmentTextField.gridwidth = 3;
		gbc_departmentTextField.insets = new Insets(0, 0, 5, 50);
		gbc_departmentTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_departmentTextField.gridx = 3;
		gbc_departmentTextField.gridy = 5;
		panel.add(departmentTextField, gbc_departmentTextField);

		JLabel roomNumberLabel = new JLabel("Room Number");
		roomNumberLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_roomNumberLabel = new GridBagConstraints();
		gbc_roomNumberLabel.anchor = GridBagConstraints.WEST;
		gbc_roomNumberLabel.insets = new Insets(0, 50, 5, 25);
		gbc_roomNumberLabel.gridx = 1;
		gbc_roomNumberLabel.gridy = 6;
		panel.add(roomNumberLabel, gbc_roomNumberLabel);

		roomNumberTextField = new JTextField();
		roomNumberTextField.getDocument().addDocumentListener(documentListener);
		roomNumberTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		roomNumberTextField.setPreferredSize(new Dimension(50, 25));
		roomNumberTextField.setColumns(10);
		GridBagConstraints gbc_roomNumberTextField = new GridBagConstraints();
		gbc_roomNumberTextField.insets = new Insets(0, 0, 5, 5);
		gbc_roomNumberTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_roomNumberTextField.gridx = 3;
		gbc_roomNumberTextField.gridy = 6;
		panel.add(roomNumberTextField, gbc_roomNumberTextField);

		JLabel floorLabel = new JLabel("Floor");
		floorLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_floorLabel = new GridBagConstraints();
		gbc_floorLabel.anchor = GridBagConstraints.EAST;
		gbc_floorLabel.insets = new Insets(0, 25, 5, 25);
		gbc_floorLabel.gridx = 4;
		gbc_floorLabel.gridy = 6;
		panel.add(floorLabel, gbc_floorLabel);

		floorTextField = new JTextField();
		floorTextField.getDocument().addDocumentListener(documentListener);
		floorTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		floorTextField.setPreferredSize(new Dimension(50, 25));
		floorTextField.setColumns(10);
		GridBagConstraints gbc_floorTextField = new GridBagConstraints();
		gbc_floorTextField.insets = new Insets(0, 0, 5, 50);
		gbc_floorTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_floorTextField.gridx = 5;
		gbc_floorTextField.gridy = 6;
		panel.add(floorTextField, gbc_floorTextField);

		JLabel buildingLabel = new JLabel("Building");
		buildingLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_buildingLabel = new GridBagConstraints();
		gbc_buildingLabel.anchor = GridBagConstraints.WEST;
		gbc_buildingLabel.insets = new Insets(0, 50, 5, 25);
		gbc_buildingLabel.gridx = 1;
		gbc_buildingLabel.gridy = 7;
		panel.add(buildingLabel, gbc_buildingLabel);

		buildingTextField = new JTextField();
		buildingTextField.getDocument().addDocumentListener(documentListener);
		buildingTextField.setPreferredSize(new Dimension(250, 25));
		buildingTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		buildingTextField.setColumns(10);
		GridBagConstraints gbc_buildingTextField = new GridBagConstraints();
		gbc_buildingTextField.gridwidth = 3;
		gbc_buildingTextField.insets = new Insets(0, 0, 5, 50);
		gbc_buildingTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildingTextField.gridx = 3;
		gbc_buildingTextField.gridy = 7;
		panel.add(buildingTextField, gbc_buildingTextField);

		JLabel communicationLabel = new GradientLabel();
		communicationLabel.setText("Communication");
		communicationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		communicationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		communicationLabel.setPreferredSize(new Dimension(99, 25));
		communicationLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_communicationLabel = new GridBagConstraints();
		gbc_communicationLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_communicationLabel.gridwidth = 5;
		gbc_communicationLabel.insets = new Insets(25, 0, 15, 5);
		gbc_communicationLabel.gridx = 1;
		gbc_communicationLabel.gridy = 8;
		panel.add(communicationLabel, gbc_communicationLabel);

		JLabel languageLabel = new JLabel("Language");
		languageLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_12.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_12.gridx = 1;
		gbc_lblNewLabel_12.gridy = 9;
		panel.add(languageLabel, gbc_lblNewLabel_12);

		List<String> languages = new ArrayList<String>();
		Locale[] localeList = NumberFormat.getAvailableLocales();
		for (Locale locale : localeList)
			if (!languages.contains(locale.getDisplayLanguage()))
				languages.add(locale.getDisplayLanguage());

		Collections.sort(languages);

		languageComboBox = new JComboBox(languages.toArray());
		languageComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_languageComboBox = new GridBagConstraints();
		gbc_languageComboBox.gridwidth = 2;
		gbc_languageComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_languageComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_languageComboBox.gridx = 3;
		gbc_languageComboBox.gridy = 9;
		panel.add(languageComboBox, gbc_languageComboBox);

		languageComboBox.addActionListener(actionListener);

		JLabel phoneLabel = new JLabel("Telephone");
		phoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_phoneLabel = new GridBagConstraints();
		gbc_phoneLabel.insets = new Insets(0, 50, 5, 25);
		gbc_phoneLabel.anchor = GridBagConstraints.WEST;
		gbc_phoneLabel.gridx = 1;
		gbc_phoneLabel.gridy = 10;
		panel.add(phoneLabel, gbc_phoneLabel);

		telephoneTextField = new JTextField();
		telephoneTextField.getDocument().addDocumentListener(documentListener);
		telephoneTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		telephoneTextField.getDocument().addDocumentListener(documentListener);
		telephoneTextField.setBackground(new Color(255, 243, 204));
		telephoneTextField.setPreferredSize(new Dimension(250, 25));
		telephoneTextField.setColumns(10);
		GridBagConstraints gbc_phoneTextField = new GridBagConstraints();
		gbc_phoneTextField.gridwidth = 3;
		gbc_phoneTextField.insets = new Insets(0, 0, 5, 50);
		gbc_phoneTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_phoneTextField.gridx = 3;
		gbc_phoneTextField.gridy = 10;
		panel.add(telephoneTextField, gbc_phoneTextField);

		JLabel mobilePhoneLabel = new JLabel("Mobile Phone");
		mobilePhoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_mobilePhoneLabel = new GridBagConstraints();
		gbc_mobilePhoneLabel.anchor = GridBagConstraints.WEST;
		gbc_mobilePhoneLabel.insets = new Insets(0, 50, 5, 25);
		gbc_mobilePhoneLabel.gridx = 1;
		gbc_mobilePhoneLabel.gridy = 11;
		panel.add(mobilePhoneLabel, gbc_mobilePhoneLabel);

		mobilePhoneTextField = new JTextField();
		mobilePhoneTextField.getDocument().addDocumentListener(documentListener);
		mobilePhoneTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		mobilePhoneTextField.setPreferredSize(new Dimension(250, 25));
		mobilePhoneTextField.setColumns(10);
		GridBagConstraints gbc_mobilePhoneTextField = new GridBagConstraints();
		gbc_mobilePhoneTextField.gridwidth = 3;
		gbc_mobilePhoneTextField.insets = new Insets(0, 0, 5, 50);
		gbc_mobilePhoneTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_mobilePhoneTextField.gridx = 3;
		gbc_mobilePhoneTextField.gridy = 11;
		panel.add(mobilePhoneTextField, gbc_mobilePhoneTextField);

		JLabel faxLabel = new JLabel("Fax");
		faxLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_faxLabel = new GridBagConstraints();
		gbc_faxLabel.anchor = GridBagConstraints.WEST;
		gbc_faxLabel.insets = new Insets(0, 50, 5, 25);
		gbc_faxLabel.gridx = 1;
		gbc_faxLabel.gridy = 12;
		panel.add(faxLabel, gbc_faxLabel);

		faxTextField = new JTextField();
		faxTextField.getDocument().addDocumentListener(documentListener);
		faxTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		faxTextField.setPreferredSize(new Dimension(250, 25));
		faxTextField.setColumns(10);
		GridBagConstraints gbc_faxTextField = new GridBagConstraints();
		gbc_faxTextField.gridwidth = 3;
		gbc_faxTextField.insets = new Insets(0, 0, 5, 50);
		gbc_faxTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_faxTextField.gridx = 3;
		gbc_faxTextField.gridy = 12;
		panel.add(faxTextField, gbc_faxTextField);

		JLabel emailLabel = new JLabel("E-Mail");
		emailLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_emailLabel = new GridBagConstraints();
		gbc_emailLabel.anchor = GridBagConstraints.WEST;
		gbc_emailLabel.insets = new Insets(0, 50, 50, 25);
		gbc_emailLabel.gridx = 1;
		gbc_emailLabel.gridy = 13;
		panel.add(emailLabel, gbc_emailLabel);

		emailTextField = new JTextField();
		emailTextField.getDocument().addDocumentListener(documentListener);
		emailTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		emailTextField.setPreferredSize(new Dimension(250, 25));
		emailTextField.setColumns(10);
		GridBagConstraints gbc_emailTextField = new GridBagConstraints();
		gbc_emailTextField.gridwidth = 3;
		gbc_emailTextField.insets = new Insets(0, 0, 50, 50);
		gbc_emailTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_emailTextField.gridx = 3;
		gbc_emailTextField.gridy = 13;
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

		userEditor.getUser().setFtitle((String) titleComboBox.getSelectedItem());

		userEditor.getUser().setFirstName(firstNameTextField.getText().trim());

		userEditor.getUser().setLastName(lastNameTextField.getText().trim());

		userEditor.getUser().setAcademicTitle(academicTitleTextField.getText().trim());

		userEditor.getUser().setFfunction(functionTextField.getText().trim());

		userEditor.getUser().setDepartment(departmentTextField.getText().trim());

		userEditor.getUser().setRoomNumber(roomNumberTextField.getText().trim());

		userEditor.getUser().setFloor(floorTextField.getText().trim());

		userEditor.getUser().setBuilding(buildingTextField.getText().trim());

		userEditor.getUser().setfLanguage((String) languageComboBox.getSelectedItem());

		userEditor.getUser().setTelephone(telephoneTextField.getText().trim());

		userEditor.getUser().setMobilePhone(mobilePhoneTextField.getText().trim());

		userEditor.getUser().setFax(faxTextField.getText().trim());

		userEditor.getUser().setEmail(emailTextField.getText().trim());
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

		if (dirtyFieldCheck(userEditor.getUser().getFirstName(), firstNameTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getLastName(), lastNameTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getAcademicTitle(), academicTitleTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getFfunction(), functionTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getDepartment(), departmentTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getRoomNumber(), roomNumberTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getFloor(), floorTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getBuilding(), buildingTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getTelephone(), telephoneTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getMobilePhone(), mobilePhoneTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getFax(), faxTextField))
			dirty = true;

		if (dirtyFieldCheck(userEditor.getUser().getEmail(), emailTextField))
			dirty = true;

		if ((userEditor.getUser().getFtitle() == null || userEditor.getUser().getFtitle().trim().length() == 0)
				&& !(titleComboBox.getSelectedIndex() == 0 || titleComboBox.getSelectedIndex() == -1))
			dirty = true;

		if (userEditor.getUser().getFtitle() != null && userEditor.getUser().getFtitle().trim().length() > 0
				&& !userEditor.getUser().getFtitle().equals(titleComboBox.getSelectedItem()))
			dirty = true;

		if ((userEditor.getUser().getfLanguage() == null || userEditor.getUser().getfLanguage().trim().length() == 0)
				&& !(languageComboBox.getSelectedIndex() == -1))
			dirty = true;

		if (userEditor.getUser().getfLanguage() != null && userEditor.getUser().getfLanguage().trim().length() > 0
				&& !userEditor.getUser().getfLanguage().equals(languageComboBox.getSelectedItem()))
			dirty = true;

		userEditor.checkDirty();
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		titleComboBox.setSelectedItem(userEditor.getUser().getFtitle());

		firstNameTextField.setText(userEditor.getUser().getFirstName());

		lastNameTextField.setText(userEditor.getUser().getLastName());

		academicTitleTextField.setText(userEditor.getUser().getAcademicTitle());

		functionTextField.setText(userEditor.getUser().getFfunction());

		departmentTextField.setText(userEditor.getUser().getDepartment());

		roomNumberTextField.setText(userEditor.getUser().getRoomNumber());

		floorTextField.setText(userEditor.getUser().getFloor());

		buildingTextField.setText(userEditor.getUser().getBuilding());

		languageComboBox.setSelectedItem(userEditor.getUser().getfLanguage());

		telephoneTextField.setText(userEditor.getUser().getTelephone());

		mobilePhoneTextField.setText(userEditor.getUser().getMobilePhone());

		faxTextField.setText(userEditor.getUser().getFax());

		emailTextField.setText(userEditor.getUser().getEmail());

		if (userEditor.getBasisClientConnector().getFUser().canWrite(userEditor.getAbstractBusinessObject())) {

			titleComboBox.setBackground(new Color(255, 243, 204));
			titleComboBox.setEnabled(true);

			firstNameTextField.setBackground(new Color(255, 243, 204));
			firstNameTextField.setEditable(true);

			lastNameTextField.setBackground(new Color(255, 243, 204));
			lastNameTextField.setEditable(true);

			academicTitleTextField.setBackground(new Color(255, 243, 204));
			academicTitleTextField.setEditable(true);

			functionTextField.setBackground(new Color(255, 243, 204));
			functionTextField.setEditable(true);

			departmentTextField.setBackground(new Color(255, 243, 204));
			departmentTextField.setEditable(true);

			roomNumberTextField.setBackground(new Color(255, 243, 204));
			roomNumberTextField.setEditable(true);

			floorTextField.setBackground(new Color(255, 243, 204));
			floorTextField.setEditable(true);

			buildingTextField.setBackground(new Color(255, 243, 204));
			buildingTextField.setEditable(true);

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

			titleComboBox.setBackground(new Color(204, 216, 255));
			titleComboBox.setEnabled(false);

			firstNameTextField.setBackground(new Color(204, 216, 255));
			firstNameTextField.setEditable(false);

			lastNameTextField.setBackground(new Color(204, 216, 255));
			lastNameTextField.setEditable(false);

			academicTitleTextField.setBackground(new Color(204, 216, 255));
			academicTitleTextField.setEditable(false);

			functionTextField.setBackground(new Color(204, 216, 255));
			functionTextField.setEditable(false);

			departmentTextField.setBackground(new Color(204, 216, 255));
			departmentTextField.setEditable(false);

			roomNumberTextField.setBackground(new Color(204, 216, 255));
			roomNumberTextField.setEditable(false);

			floorTextField.setBackground(new Color(204, 216, 255));
			floorTextField.setEditable(false);

			buildingTextField.setBackground(new Color(204, 216, 255));
			buildingTextField.setEditable(false);

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
