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
import java.text.DateFormat;

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

import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;

import org.apache.log4j.Logger;

/**
 * The Class FUserEditorMasterData.
 */
public class FUserEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private JPasswordField passwordField = null;

	private JPasswordField oldPasswordField = null;

	private JPasswordField rePasswordField = null;

	private FUserEditor fUserEditor = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private JLabel newPasswordWarningLabel = null;

	private JLabel rePasswordWarningLabel = null;

	private JLabel oldPasswordWarningLabel = null;

	private boolean consistent = false;
	
	private static Logger log = Logger.getLogger(FUserEditorMasterData.class);

	/**
	 * Instantiates a new f user editor master data.
	 *
	 * @param fUserEditor the f user editor
	 */
	public FUserEditorMasterData(FUserEditor fUserEditor) {

		super();

		this.fUserEditor = fUserEditor;

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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 5;
		gbc_leftFillPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel userLabel = new JLabel("User");
		userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_userLabel = new GridBagConstraints();
		gbc_userLabel.anchor = GridBagConstraints.WEST;
		gbc_userLabel.insets = new Insets(50, 50, 5, 10);
		gbc_userLabel.gridx = 1;
		gbc_userLabel.gridy = 0;
		panel.add(userLabel, gbc_userLabel);

		nameWarningLabel = fUserEditor.getNameWarningLabel();
		nameWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_nameWarningLabel = new GridBagConstraints();
		gbc_nameWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_nameWarningLabel.gridx = 2;
		gbc_nameWarningLabel.gridy = 0;
		panel.add(nameWarningLabel, gbc_nameWarningLabel);

		nameTextField = new JTextField(fUserEditor.getUser().getName());
		nameTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		nameTextField.getDocument().addDocumentListener(documentListener);
		nameTextField.setBackground(new Color(255, 243, 204));
		nameTextField.setPreferredSize(new Dimension(250, 25));
		nameTextField.setColumns(10);
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.gridwidth = 3;
		gbc_nameTextField.insets = new Insets(50, 0, 5, 50);
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 0;
		panel.add(nameTextField, gbc_nameTextField);

		JLabel modifiedLabel = new JLabel("Modified");
		modifiedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_modifiedLabel = new GridBagConstraints();
		gbc_modifiedLabel.anchor = GridBagConstraints.WEST;
		gbc_modifiedLabel.insets = new Insets(0, 50, 5, 10);
		gbc_modifiedLabel.gridx = 1;
		gbc_modifiedLabel.gridy = 1;
		panel.add(modifiedLabel, gbc_modifiedLabel);

		modifiedField = new JTextField();
		modifiedField.setFont(new Font("Dialog", Font.PLAIN, 12));
		modifiedField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		modifiedField.setPreferredSize(new Dimension(250, 25));
		modifiedField.setBackground(new Color(204, 216, 255));
		modifiedField.setEditable(false);
		modifiedField.setColumns(10);
		GridBagConstraints gbc_modifiedField = new GridBagConstraints();
		gbc_modifiedField.gridwidth = 3;
		gbc_modifiedField.insets = new Insets(0, 0, 5, 50);
		gbc_modifiedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedField.gridx = 3;
		gbc_modifiedField.gridy = 1;
		panel.add(modifiedField, gbc_modifiedField);

		JLabel oldPasswordLabel = new JLabel("Old Password");
		oldPasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_oldPasswordLabel = new GridBagConstraints();
		gbc_oldPasswordLabel.insets = new Insets(0, 50, 5, 10);
		gbc_oldPasswordLabel.anchor = GridBagConstraints.WEST;
		gbc_oldPasswordLabel.gridx = 1;
		gbc_oldPasswordLabel.gridy = 2;
		panel.add(oldPasswordLabel, gbc_oldPasswordLabel);

		oldPasswordWarningLabel = new JLabel("");
		oldPasswordWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_oldPasswordWarningLabel = new GridBagConstraints();
		gbc_oldPasswordWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_oldPasswordWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_oldPasswordWarningLabel.gridx = 2;
		gbc_oldPasswordWarningLabel.gridy = 2;
		panel.add(oldPasswordWarningLabel, gbc_oldPasswordWarningLabel);

		oldPasswordField = new JPasswordField();
		oldPasswordField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		oldPasswordField.getDocument().addDocumentListener(documentListener);
		oldPasswordField.setBackground(new Color(255, 243, 204));
		oldPasswordField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_oldPasswordField = new GridBagConstraints();
		gbc_oldPasswordField.gridwidth = 3;
		gbc_oldPasswordField.insets = new Insets(0, 0, 5, 50);
		gbc_oldPasswordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_oldPasswordField.gridx = 3;
		gbc_oldPasswordField.gridy = 2;
		panel.add(oldPasswordField, gbc_oldPasswordField);

		JLabel passwordLabel = new JLabel("New Password");
		passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
		gbc_passwordLabel.insets = new Insets(0, 50, 5, 10);
		gbc_passwordLabel.anchor = GridBagConstraints.WEST;
		gbc_passwordLabel.gridx = 1;
		gbc_passwordLabel.gridy = 3;
		panel.add(passwordLabel, gbc_passwordLabel);

		newPasswordWarningLabel = new JLabel("");
		newPasswordWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_newPasswordWarningLabel = new GridBagConstraints();
		gbc_newPasswordWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_newPasswordWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_newPasswordWarningLabel.gridx = 2;
		gbc_newPasswordWarningLabel.gridy = 3;
		panel.add(newPasswordWarningLabel, gbc_newPasswordWarningLabel);

		passwordField = new JPasswordField();
		passwordField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		passwordField.getDocument().addDocumentListener(documentListener);
		passwordField.setBackground(new Color(255, 243, 204));
		passwordField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.gridwidth = 3;
		gbc_passwordField.insets = new Insets(0, 0, 5, 50);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 3;
		gbc_passwordField.gridy = 3;
		panel.add(passwordField, gbc_passwordField);

		JLabel rePasswordLabel = new JLabel("Re Password");
		rePasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_rePasswordLabel = new GridBagConstraints();
		gbc_rePasswordLabel.anchor = GridBagConstraints.WEST;
		gbc_rePasswordLabel.insets = new Insets(0, 50, 50, 10);
		gbc_rePasswordLabel.gridx = 1;
		gbc_rePasswordLabel.gridy = 4;
		panel.add(rePasswordLabel, gbc_rePasswordLabel);

		rePasswordWarningLabel = new JLabel("");
		rePasswordWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_rePasswordWarningLabel = new GridBagConstraints();
		gbc_rePasswordWarningLabel.insets = new Insets(0, 0, 50, 5);
		gbc_rePasswordWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_rePasswordWarningLabel.gridx = 2;
		gbc_rePasswordWarningLabel.gridy = 4;
		panel.add(rePasswordWarningLabel, gbc_rePasswordWarningLabel);

		rePasswordField = new JPasswordField();
		rePasswordField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		rePasswordField.getDocument().addDocumentListener(documentListener);
		rePasswordField.setBackground(new Color(255, 243, 204));
		rePasswordField.setPreferredSize(new Dimension(250, 25));
		GridBagConstraints gbc_rePasswordField = new GridBagConstraints();
		gbc_rePasswordField.gridwidth = 3;
		gbc_rePasswordField.insets = new Insets(0, 0, 50, 50);
		gbc_rePasswordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_rePasswordField.gridx = 3;
		gbc_rePasswordField.gridy = 4;
		panel.add(rePasswordField, gbc_rePasswordField);

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 5;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 6;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		updateContent();
	}

	private void checkConsistency() {

		consistent = true;
		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("User name is empty");
			nameWarningLabel.setIcon(fUserEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (fUserEditor.getUser().getName() == null || !fUserEditor.getUser().getName().equals(nameTextField.getText())) {

				fUserEditor.checkName(nameTextField.getText());

				dirty = true;
			}
		}

		if (passwordField.getPassword().length > 0 || rePasswordField.getPassword().length > 0 || oldPasswordField.getPassword().length > 0) {
			
			dirty = true;

			if (passwordField.getPassword().length == 0) {
				
				newPasswordWarningLabel.setToolTipText("Password is empty");
				newPasswordWarningLabel.setIcon(fUserEditor.getBugIcon());

				valid = false;
			}
			else if (passwordField.getPassword().length < 5) {
				
				newPasswordWarningLabel.setToolTipText("Password is to short");
				newPasswordWarningLabel.setIcon(fUserEditor.getBugIcon());

				valid = false;
			}
			else {
				
				newPasswordWarningLabel.setToolTipText(null);
				newPasswordWarningLabel.setIcon(null);
			}

			String password = new String(passwordField.getPassword());
			String password2 = new String(rePasswordField.getPassword());

			if (!password.equals(password2)) {
				
				rePasswordWarningLabel.setToolTipText("Password is incorrect");
				rePasswordWarningLabel.setIcon(fUserEditor.getBugIcon());

				valid = false;
			}
			else {
				
				rePasswordWarningLabel.setToolTipText(null);
				rePasswordWarningLabel.setIcon(null);
			}

			boolean checkOld = true;

			for (FRole role : fUserEditor.getBasisClientConnector().getFUser().getAssignedRoles())
				if (role.getName().equals("Admin Role"))
					checkOld = false;

			if (checkOld && fUserEditor.getUser().getId() > 0) {
				
				if (oldPasswordField.getPassword().length == 0) {
					
					oldPasswordWarningLabel.setToolTipText("Old password is empty");
					oldPasswordWarningLabel.setIcon(fUserEditor.getBugIcon());

					valid = false;
				}
				else {
					
					oldPasswordWarningLabel.setToolTipText(null);
					oldPasswordWarningLabel.setIcon(null);
				}
			}
		}
		else {
			
			newPasswordWarningLabel.setToolTipText(null);
			newPasswordWarningLabel.setIcon(null);
			
			oldPasswordWarningLabel.setToolTipText(null);
			oldPasswordWarningLabel.setIcon(null);
			
			rePasswordWarningLabel.setToolTipText(null);
			rePasswordWarningLabel.setIcon(null);
		}
		
		if(fUserEditor.getUser().getId()==0&&passwordField.getPassword().length==0)
		{
			newPasswordWarningLabel.setToolTipText("Password is empty");
			newPasswordWarningLabel.setIcon(fUserEditor.getBugIcon());

			valid = false;
		}

		consistent = consistent && valid;

		fUserEditor.checkDirty();
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
	 * Save.
	 */
	public void save() {

		try {
			
			fUserEditor.getUser().setName(nameTextField.getText().trim());
			
			if (passwordField.getPassword().length > 0)
				fUserEditor.getUser().setPlainPassword(new String(passwordField.getPassword()));
		}
		catch (Exception e) {
			
			log.error("Bug", e);
		}
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
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(fUserEditor.getUser().getName());
		passwordField.setText("");
		oldPasswordField.setText("");
		rePasswordField.setText("");
		
		if (fUserEditor.getAbstractBusinessObject().getId() != 0) {
			
			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(fUserEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(fUserEditor.getAbstractBusinessObject().getModificationDate()));
			
			if (fUserEditor.getAbstractBusinessObject().getModificationUser() != null) {
				textBuffer.append(" by ");
				textBuffer.append(fUserEditor.getAbstractBusinessObject().getModificationUser());
			}
			
			modifiedField.setText(textBuffer.toString());
		}
		else {
			
			modifiedField.setText("New Business Object");
		}
		
		if (fUserEditor.getBasisClientConnector().getFUser().canWrite(fUserEditor.getAbstractBusinessObject())) {
			
			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			
			passwordField.setBackground(new Color(255, 243, 204));
			passwordField.setEditable(true);
			
			rePasswordField.setBackground(new Color(255, 243, 204));
			rePasswordField.setEditable(true);
			
			oldPasswordField.setBackground(new Color(255, 243, 204));
			oldPasswordField.setEditable(true);
		}
		else {
			
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			
			passwordField.setBackground(new Color(204, 216, 255));
			passwordField.setEditable(false);
			
			rePasswordField.setBackground(new Color(204, 216, 255));
			rePasswordField.setEditable(false);
			
			oldPasswordField.setBackground(new Color(204, 216, 255));
			oldPasswordField.setEditable(false);
		}
		
		if (fUserEditor.getUser().getId() == fUserEditor.getBasisClientConnector().getFUser().getId()) {
			
			passwordField.setBackground(new Color(255, 243, 204));
			passwordField.setEditable(true);
			
			rePasswordField.setBackground(new Color(255, 243, 204));
			rePasswordField.setEditable(true);
			
			oldPasswordField.setBackground(new Color(255, 243, 204));
			oldPasswordField.setEditable(true);
		}
		
		for (FRole role : fUserEditor.getBasisClientConnector().getFUser().getAssignedRoles())
			if (role.getName().equals("Admin Role")) {
				
				oldPasswordField.setBackground(new Color(204, 216, 255));
				oldPasswordField.setEditable(false);
			}
		
		checkConsistency();
	}

	/**
	 * Check password.
	 *
	 * @return true, if successful
	 */
	public boolean checkPassword() {

		if (passwordField.getPassword().length == 0 && rePasswordField.getPassword().length == 0 && oldPasswordField.getPassword().length == 0)
			return true;
		
		for (FRole role : fUserEditor.getBasisClientConnector().getFUser().getAssignedRoles())
			if (role.getName().equals("Admin Role")) {
				
				String password = new String(passwordField.getPassword());
				String password2 = new String(passwordField.getPassword());
				
				if (password.length() > 3 && password.equals(password2))
					return true;
			}
		
		if (fUserEditor.getBasisClientConnector().getFUser().canWrite(fUserEditor.getAbstractBusinessObject())
				|| fUserEditor.getUser().getId() == fUserEditor.getBasisClientConnector().getFUser().getId()) {
			
			String oldPassword = new String(oldPasswordField.getPassword());
			String password = new String(passwordField.getPassword());
			String password2 = new String(rePasswordField.getPassword());

			if (fUserEditor.getUser().getId() > 0) {
				
				try {
					
					if (password.length() > 3 && password.equals(password2)
							&& fUserEditor.getUser().getHash(oldPassword).equals(fUserEditor.getUser().getFPassword()))
						return true;
				}
				catch (Exception e) {
					
					log.error("Bug", e);
				}
			}
			else if (password.length() > 3 && password.equals(password2))
				return true;
		}
		
		return false;
	}
}
