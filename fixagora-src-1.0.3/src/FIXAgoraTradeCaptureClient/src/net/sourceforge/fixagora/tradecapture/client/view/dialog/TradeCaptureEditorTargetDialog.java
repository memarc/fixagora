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
package net.sourceforge.fixagora.tradecapture.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureTarget;

/**
 * The Class TradeCaptureEditorTargetDialog.
 */
public class TradeCaptureEditorTargetDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private JComboBox businessComponentComboBox = null;
	
	/** The assigned trade capture target. */
	protected AssignedTradeCaptureTarget assignedTradeCaptureTarget;

	/**
	 * Instantiates a new trade capture editor target dialog.
	 *
	 * @param businessComponents the business components
	 */
	public TradeCaptureEditorTargetDialog(List<AbstractBusinessObject> businessComponents) {

		setBounds(100, 100, 517, 157);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(TradeCaptureEditorTargetDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[]{0.0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		
		
		JLabel businessComponentLabel = new JLabel("Business Component");
		businessComponentLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_businessComponentLabel = new GridBagConstraints();
		gbc_businessComponentLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_businessComponentLabel.insets = new Insets(25, 25, 5, 5);
		gbc_businessComponentLabel.gridx = 0;
		gbc_businessComponentLabel.gridy = 0;
		contentPanel.add(businessComponentLabel, gbc_businessComponentLabel);

		ArrayList<ComboBoxEntry> businessComponents2 = new ArrayList<ComboBoxEntry>();
		for (AbstractBusinessObject abstractBusinessObject : businessComponents)
			businessComponents2.add(new ComboBoxEntry(abstractBusinessObject,abstractBusinessObject.getName()));

		businessComponentComboBox = new JComboBox(businessComponents2.toArray());
		businessComponentComboBox.setMinimumSize(new Dimension(250, 25));
		businessComponentComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		businessComponentComboBox.setPreferredSize(new Dimension(250, 25));
		businessComponentComboBox.setBackground(new Color(255, 243, 204));
		

		businessComponentComboBox.setSelectedIndex(0);

		GridBagConstraints gbc_businessComponentComboBox = new GridBagConstraints();
		gbc_businessComponentComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_businessComponentComboBox.gridwidth = 2;
		gbc_businessComponentComboBox.anchor = GridBagConstraints.NORTH;
		gbc_businessComponentComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_businessComponentComboBox.gridx = 2;
		gbc_businessComponentComboBox.gridy = 0;
		contentPanel.add(businessComponentComboBox, gbc_businessComponentComboBox);
		
		setTitle("Add Trade Capture Target");
		
		okButton = new JButton();
		okButton.setText("Add");
		
		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 1;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
				assignedTradeCaptureTarget = new AssignedTradeCaptureTarget();
				assignedTradeCaptureTarget.setAbstractBusinessComponent((AbstractBusinessComponent) ((ComboBoxEntry) businessComponentComboBox.getSelectedItem()).getEntry());
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 1;
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
	 * Gets the assigned trade capture target.
	 *
	 * @return the assigned trade capture target
	 */
	public AssignedTradeCaptureTarget getAssignedTradeCaptureTarget() {
	
		return assignedTradeCaptureTarget;
	}


}
