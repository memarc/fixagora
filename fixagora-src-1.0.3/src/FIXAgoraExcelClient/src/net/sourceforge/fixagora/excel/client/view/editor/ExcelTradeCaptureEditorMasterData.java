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
package net.sourceforge.fixagora.excel.client.view.editor;

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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;

/**
 * The Class ExcelTradeCaptureEditorMasterData.
 */
public class ExcelTradeCaptureEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private ExcelTradeCaptureEditor excelTradeCaptureEditor = null;

	private JLabel nameWarningLabel = null;

	private JTextArea textArea = null;

	private JLabel descriptionWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private boolean consistent;

	private JComboBox securityIdentifierSourceComboBox;

	/**
	 * Instantiates a new excel trade capture editor master data.
	 *
	 * @param excelTradeCaptureEditor the excel trade capture editor
	 */
	public ExcelTradeCaptureEditorMasterData(ExcelTradeCaptureEditor excelTradeCaptureEditor) {

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

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);

		this.excelTradeCaptureEditor = excelTradeCaptureEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0};
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0};
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 4;
		gbc_leftFillPanel.insets = new Insets(0, 0, 0, 5);
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

		nameWarningLabel = excelTradeCaptureEditor.getNameWarningLabel();
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

		JLabel modifiedLabel = new JLabel("Modified");
		modifiedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_modifiedLabel = new GridBagConstraints();
		gbc_modifiedLabel.anchor = GridBagConstraints.WEST;
		gbc_modifiedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_modifiedLabel.gridx = 1;
		gbc_modifiedLabel.gridy = 1;
		panel.add(modifiedLabel, gbc_modifiedLabel);

		modifiedField = new JTextField();
		modifiedField.setEditable(false);
		modifiedField.setBackground(new Color(204, 216, 255));
		modifiedField.setPreferredSize(new Dimension(4, 25));
		modifiedField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		modifiedField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		panel.add(modifiedField, gbc_textField);
		
		JLabel lblSecurityIdentifierSource = new JLabel("Security Identifier Source");
		lblSecurityIdentifierSource.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecurityIdentifierSource = new GridBagConstraints();
		gbc_lblSecurityIdentifierSource.anchor = GridBagConstraints.WEST;
		gbc_lblSecurityIdentifierSource.insets = new Insets(0, 0, 5, 0);
		gbc_lblSecurityIdentifierSource.gridx = 1;
		gbc_lblSecurityIdentifierSource.gridy = 2;
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
		gbc_securityIdentifierSourceComboBox.gridy = 2;
		panel.add(securityIdentifierSourceComboBox, gbc_securityIdentifierSourceComboBox);
		securityIdentifierSourceComboBox.setSelectedIndex(0);

		securityIdentifierSourceComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		});


		JLabel descriptionLabel = new JLabel("Description");
		descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
		gbc_descriptionLabel.insets = new Insets(5, 0, 5, 5);
		gbc_descriptionLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_descriptionLabel.gridx = 1;
		gbc_descriptionLabel.gridy = 3;
		panel.add(descriptionLabel, gbc_descriptionLabel);

		descriptionWarningLabel = new JLabel("");
		descriptionWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_descriptionWarningLabel = new GridBagConstraints();
		gbc_descriptionWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_descriptionWarningLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_descriptionWarningLabel.gridx = 2;
		gbc_descriptionWarningLabel.gridy = 3;
		panel.add(descriptionWarningLabel, gbc_descriptionWarningLabel);

		textArea = new JTextArea();
		textArea.setBorder( new EmptyBorder(5, 5, 5, 5));
		textArea.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 50, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 3;
		gbc_textArea.gridy = 3;
		JScrollPane jScrollPane = new JScrollPane(textArea);
		jScrollPane.setPreferredSize(new Dimension(350, 300));
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(jScrollPane, gbc_textArea);
		
		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 4;
		gbc_rightFillPanel.weightx = 1.0;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 4;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		updateContent();
	}

	private void checkConsistency() {

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Book name is empty");
			nameWarningLabel.setIcon(excelTradeCaptureEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (excelTradeCaptureEditor.getAbstractBusinessObject().getName() == null
					|| !excelTradeCaptureEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				excelTradeCaptureEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}


		if (textArea.getText().trim().length() == 0) {

			descriptionWarningLabel.setToolTipText("Description is empty");
			descriptionWarningLabel.setIcon(excelTradeCaptureEditor.getWarningIcon());
		}
		else {

			descriptionWarningLabel.setToolTipText(null);
			descriptionWarningLabel.setIcon(null);
		}
		
		if (excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource() != null && excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource().trim().length() > 0
				&& !excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource().equals(((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource() == null || excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource().trim().length() == 0)
				&& securityIdentifierSourceComboBox.getSelectedIndex() > 0)
			dirty = true;


		if (excelTradeCaptureEditor.getExcelTradeCapture().getDescription() != null && !excelTradeCaptureEditor.getExcelTradeCapture().getDescription().equals(textArea.getText())) {

			dirty = true;
		}
		
		if (excelTradeCaptureEditor.getExcelTradeCapture().getDescription() == null && textArea.getText().trim().length()>0) {
			
			dirty = true;
		}

		consistent = consistent && valid;

		excelTradeCaptureEditor.checkDirty();
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

		excelTradeCaptureEditor.getExcelTradeCapture().setName(nameTextField.getText().trim());
		excelTradeCaptureEditor.getExcelTradeCapture().setDescription(textArea.getText());
		excelTradeCaptureEditor.getExcelTradeCapture().setSecurityIDSource((String) ((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry());

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(excelTradeCaptureEditor.getExcelTradeCapture().getName());
		
		textArea.setText(excelTradeCaptureEditor.getExcelTradeCapture().getDescription());
		
		if(excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource()!=null)
			securityIdentifierSourceComboBox.setSelectedItem(new ComboBoxEntry(excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource(), null));
		else
			securityIdentifierSourceComboBox.setSelectedIndex(0);

		if (excelTradeCaptureEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(excelTradeCaptureEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(excelTradeCaptureEditor.getAbstractBusinessObject().getModificationDate()));

			if (excelTradeCaptureEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(excelTradeCaptureEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		if (excelTradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(excelTradeCaptureEditor.getAbstractBusinessObject())
				&& !(excelTradeCaptureEditor.getAbstractBusinessObject().getName() != null && excelTradeCaptureEditor.getAbstractBusinessObject().getName()
						.equals("Admin Role"))) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			
			securityIdentifierSourceComboBox.setBackground(new Color(255, 243, 204));
			securityIdentifierSourceComboBox.setEnabled(true);

			textArea.setBackground(new Color(255, 243, 204));
			textArea.setEditable(true);
			
		}
		else {
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			
			securityIdentifierSourceComboBox.setBackground(new Color(204, 216, 255));
			securityIdentifierSourceComboBox.setEnabled(false);

			textArea.setBackground(new Color(204, 216, 255));
			textArea.setEditable(false);
			
		}

		checkConsistency();
	}
}
