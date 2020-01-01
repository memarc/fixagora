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

import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup.SortBy;

/**
 * The Class FSecurityGroupMasterData.
 */
public class FSecurityGroupMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private FSecurityGroupEditor securityGroupEditor = null;

	private JLabel nameWarningLabel = null;

	private JTextArea descriptionArea = null;

	private JLabel descriptionWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private boolean consistent;

	private JComboBox sortSecuritiesComboBox;

	/**
	 * Instantiates a new f security group master data.
	 *
	 * @param securityGroupEditor the security group editor
	 */
	public FSecurityGroupMasterData(FSecurityGroupEditor securityGroupEditor) {

		super();

		this.securityGroupEditor = securityGroupEditor;

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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		setViewportView(panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 8;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel nameLabel = new JLabel("Group Name");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.WEST;
		gbc_nameLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = securityGroupEditor.getNameWarningLabel();
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
		GridBagConstraints gbc_modifiedField = new GridBagConstraints();
		gbc_modifiedField.insets = new Insets(0, 0, 5, 5);
		gbc_modifiedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedField.gridx = 3;
		gbc_modifiedField.gridy = 1;
		panel.add(modifiedField, gbc_modifiedField);
		
		JLabel sortSecuritiesLabel = new JLabel("Sort Securities");
		sortSecuritiesLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sortSecuritiesLabel = new GridBagConstraints();
		gbc_sortSecuritiesLabel.anchor = GridBagConstraints.WEST;
		gbc_sortSecuritiesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sortSecuritiesLabel.gridx = 1;
		gbc_sortSecuritiesLabel.gridy = 2;
		panel.add(sortSecuritiesLabel, gbc_sortSecuritiesLabel);

		sortSecuritiesComboBox = new JComboBox(new String[]{"by symbol","by maturity"});
		sortSecuritiesComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		sortSecuritiesComboBox.setMinimumSize(new Dimension(100, 25));
		sortSecuritiesComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sortSecuritiesComboBox.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTHWEST;
		gbc_localeOfissueTextField.insets = new Insets(0, 0, 5, 50);
		gbc_localeOfissueTextField.gridx = 3;
		gbc_localeOfissueTextField.gridy = 2;
		panel.add(sortSecuritiesComboBox, gbc_localeOfissueTextField);
		
		sortSecuritiesComboBox.addActionListener(new ActionListener() {
			
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

		descriptionArea = new JTextArea();
		descriptionArea.setBorder( new EmptyBorder(5, 5, 5, 5));
		descriptionArea.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 3;
		gbc_textArea.gridy = 3;
		JScrollPane jScrollPane = new JScrollPane(descriptionArea);
		jScrollPane.setPreferredSize(new Dimension(400, 300));
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(jScrollPane, gbc_textArea);

		updateContent();
	}

	private void checkConsistency() {

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Role name is empty");
			nameWarningLabel.setIcon(securityGroupEditor.getBugIcon());

			valid = false;

		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (securityGroupEditor.getAbstractBusinessObject().getName() == null
					|| !securityGroupEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				securityGroupEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}

		consistent = consistent && valid;

		if (descriptionArea.getText().trim().length() == 0) {

			descriptionWarningLabel.setToolTipText("Role description is empty");
			descriptionWarningLabel.setIcon(securityGroupEditor.getWarningIcon());
		}
		else {

			descriptionWarningLabel.setToolTipText(null);
			descriptionWarningLabel.setIcon(null);
		}

		if (securityGroupEditor.getSecurityGroup().getDescription() != null
				&& !securityGroupEditor.getSecurityGroup().getDescription().equals(descriptionArea.getText())) {

			dirty = true;
		}
		
		if (securityGroupEditor.getSecurityGroup().getDescription() == null && descriptionArea.getText().trim().length()>0) {
			
			dirty = true;
		}
		
		if(securityGroupEditor.getSecurityGroup().getSortBy()==SortBy.MATURITY&&sortSecuritiesComboBox.getSelectedIndex()!=1)
			dirty=true;
		
		if(securityGroupEditor.getSecurityGroup().getSortBy()==SortBy.SYMBOL&&sortSecuritiesComboBox.getSelectedIndex()!=0)
			dirty=true;

		consistent = consistent && valid;

		securityGroupEditor.checkDirty();
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

		securityGroupEditor.getAbstractBusinessObject().setName(nameTextField.getText().trim());
		securityGroupEditor.getSecurityGroup().setDescription(descriptionArea.getText());
		if(sortSecuritiesComboBox.getSelectedIndex()==1)
			securityGroupEditor.getSecurityGroup().setSortBy(SortBy.MATURITY);
		else
			securityGroupEditor.getSecurityGroup().setSortBy(SortBy.SYMBOL);
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(securityGroupEditor.getAbstractBusinessObject().getName());
		descriptionArea.setText(securityGroupEditor.getSecurityGroup().getDescription());
		if(securityGroupEditor.getSecurityGroup().getSortBy()==SortBy.MATURITY)
			sortSecuritiesComboBox.setSelectedIndex(1);
		else
			sortSecuritiesComboBox.setSelectedIndex(0);
			

		if (securityGroupEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(securityGroupEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer
					.append(DateFormat.getDateInstance(DateFormat.SHORT).format(securityGroupEditor.getAbstractBusinessObject().getModificationDate()));

			if (securityGroupEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(securityGroupEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		if (securityGroupEditor.getBasisClientConnector().getFUser().canWrite(securityGroupEditor.getAbstractBusinessObject())
				&& securityGroupEditor.getAbstractBusinessObject().getParent() != null && !(securityGroupEditor.getAbstractBusinessObject().getParent() instanceof BusinessSection)) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);

			descriptionArea.setBackground(new Color(255, 243, 204));
			descriptionArea.setEditable(true);
			
			sortSecuritiesComboBox.setBackground(new Color(255, 243, 204));
			sortSecuritiesComboBox.setEnabled(true);
			
		}
		else {

			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);

			descriptionArea.setBackground(new Color(204, 216, 255));
			descriptionArea.setEditable(false);
			
			sortSecuritiesComboBox.setBackground(new Color(204, 216, 255));
			sortSecuritiesComboBox.setEnabled(false);

		}

		checkConsistency();
	}
}
