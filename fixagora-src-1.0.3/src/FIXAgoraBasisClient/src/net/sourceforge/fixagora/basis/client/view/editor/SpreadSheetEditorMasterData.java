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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.poi.ss.util.WorkbookUtil;

/**
 * The Class SpreadSheetEditorMasterData.
 */
public class SpreadSheetEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private JTextField modifiedField = null;
	
	private SpreadSheetEditor spreadSheetEditor = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private boolean consistent;

	private JTextField workbookTextField;

	/**
	 * Instantiates a new spread sheet editor master data.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetEditorMasterData(SpreadSheetEditor spreadSheetEditor) {

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

		this.spreadSheetEditor = spreadSheetEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 370, 0, 0};
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

		nameWarningLabel = spreadSheetEditor.getNameWarningLabel();
		nameWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_nameWarningLabel = new GridBagConstraints();
		gbc_nameWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_nameWarningLabel.gridx = 2;
		gbc_nameWarningLabel.gridy = 0;
		panel.add(nameWarningLabel, gbc_nameWarningLabel);

		nameTextField = new JTextField();
		nameTextField.addActionListener(actionListener);
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
		
		JLabel workbookLabel = new JLabel("Workbook");
		workbookLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_workbookLabel = new GridBagConstraints();
		gbc_workbookLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_workbookLabel.insets = new Insets(0, 25, 5, 5);
		gbc_workbookLabel.gridx = 1;
		gbc_workbookLabel.gridy = 2;
		panel.add(workbookLabel, gbc_workbookLabel);

		JLabel workbookWarningLabel = new JLabel();
		workbookWarningLabel.setPreferredSize(new Dimension(21, 25));
		workbookWarningLabel.setToolTipText("<html>If you want to refer to cells in another sheet, both sheets have to be in the same workbook.<br>Put as few sheets as possible in the same workbook to improve performance<br>or leave this field blank if you don't want to refer to another sheet.</html>");
		workbookWarningLabel.setIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/sc_helperdialog.png")));
		GridBagConstraints gbc_workbookWarningLabel = new GridBagConstraints();
		gbc_workbookWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_workbookWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_workbookWarningLabel.gridx = 2;
		gbc_workbookWarningLabel.gridy = 2;
		panel.add(workbookWarningLabel, gbc_workbookWarningLabel);

		workbookTextField = new JTextField();
		workbookTextField.addActionListener(actionListener);
		workbookTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		workbookTextField.getDocument().addDocumentListener(documentListener);
		workbookTextField.setPreferredSize(new Dimension(300, 25));
		workbookTextField.setColumns(10);
		GridBagConstraints gbc_workbookTextField = new GridBagConstraints();
		gbc_workbookTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_workbookTextField.gridwidth = 3;
		gbc_workbookTextField.insets = new Insets(0, 0, 5, 25);
		gbc_workbookTextField.gridx = 3;
		gbc_workbookTextField.gridy = 2;
		panel.add(workbookTextField, gbc_workbookTextField);

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

			nameWarningLabel.setToolTipText("Sheet name is empty");
			nameWarningLabel.setIcon(spreadSheetEditor.getBugIcon());

			valid = false;
		}
		else if (!nameTextField.getText().trim().equals(WorkbookUtil.createSafeSheetName(nameTextField.getText().trim()))) {

			nameWarningLabel.setToolTipText(nameTextField.getText().trim()+" is an invalid sheet name");
			nameWarningLabel.setIcon(spreadSheetEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (spreadSheetEditor.getAbstractBusinessObject().getName() == null || !spreadSheetEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				spreadSheetEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}
		
		if (dirtyFieldCheck(spreadSheetEditor.getSpreadSheet().getWorkbook(),workbookTextField))
			dirty = true;
		
		consistent = consistent && valid;

		spreadSheetEditor.checkDirty();
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

		spreadSheetEditor.getSpreadSheet().setName(nameTextField.getText().trim());
		if(workbookTextField.getText().trim().length()>0)
			spreadSheetEditor.getSpreadSheet().setWorkbook(workbookTextField.getText().trim());
		else
			spreadSheetEditor.getSpreadSheet().setWorkbook(null);
				
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(spreadSheetEditor.getSpreadSheet().getName());
		workbookTextField.setText(spreadSheetEditor.getSpreadSheet().getWorkbook());
		
		if (spreadSheetEditor.getAbstractBusinessObject().getId() != 0) {
			
			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(spreadSheetEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(spreadSheetEditor.getAbstractBusinessObject().getModificationDate()));
			
			if (spreadSheetEditor.getAbstractBusinessObject().getModificationUser() != null) {
				
				textBuffer.append(" by ");
				textBuffer.append(spreadSheetEditor.getAbstractBusinessObject().getModificationUser());
			}
			
			modifiedField.setText(textBuffer.toString());
		}
		else {
			
			modifiedField.setText("New Business Object");
		}
		
		if (spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {
			
			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			workbookTextField.setBackground(new Color(255, 243, 204));
			workbookTextField.setEditable(true);			
		}
		else {
			
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			workbookTextField.setBackground(new Color(204, 216, 255));
			workbookTextField.setEditable(false);			
		}
		
		checkConsistency();
	}
}
