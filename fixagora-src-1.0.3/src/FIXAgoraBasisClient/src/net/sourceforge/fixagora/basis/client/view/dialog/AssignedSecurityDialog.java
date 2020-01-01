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
package net.sourceforge.fixagora.basis.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.shared.model.persistence.AssignedInitiatorSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class AssignedSecurityDialog.
 */
public class AssignedSecurityDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
		
	private boolean cancelled = true;
	
	private JButton okButton = null;
	
	private AssignedInitiatorSecurity assignedInitiatorSecurity = null;
	
	private JTextField securityTextField = null;
		
	private boolean dirty = false;
		
	private JButton btnNewButton = null;
	
	private SecurityTreeDialog securityTreeDialog = null;
	
	private CounterpartyTreeDialog counterpartyTreeDialog = null;

	private JTextField counterpartyTextField;

	private JButton counterpartyNewButton;

	/**
	 * Instantiates a new assigned security dialog.
	 *
	 * @param assignedInitiatorSecurity the assigned initiator security
	 * @param securityTreeDialog the security tree dialog
	 * @param counterpartyTreeDialog the counterparty tree dialog
	 */
	public AssignedSecurityDialog(AssignedInitiatorSecurity assignedInitiatorSecurity, final SecurityTreeDialog securityTreeDialog, final CounterpartyTreeDialog counterpartyTreeDialog) {

		this.assignedInitiatorSecurity = assignedInitiatorSecurity;
		this.securityTreeDialog = securityTreeDialog;
		this.counterpartyTreeDialog = counterpartyTreeDialog;
		setBounds(100, 100, 487, 179);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(AssignedSecurityDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				checkConsistency();
			}
		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 1.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel legSecurityLabel = new JLabel("Security");
		legSecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 0;
		contentPanel.add(legSecurityLabel, gbc_legSecurityLabel);

		securityTextField = new JTextField();
		securityTextField.setPreferredSize(new Dimension(200, 25));
		securityTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		securityTextField.setBackground(new Color(255, 243, 204));
		securityTextField.setColumns(10);
		
		AutoCompleteDecorator.decorate(securityTextField,securityTreeDialog.getSecurityList(),true);
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.gridwidth = 2;
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(25, 0, 5, 5);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 0;
		contentPanel.add(securityTextField, gbc_localeOfissueTextField);
		
		btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(25, 0, 5, 25);
		gbc_btnNewButton.gridx = 4;
		gbc_btnNewButton.gridy = 0;
		contentPanel.add(btnNewButton, gbc_btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				securityTreeDialog.setVisible(true);
				if(securityTreeDialog.getSecurity()!=null&&!securityTreeDialog.isCancelled())
					securityTextField.setText(securityTreeDialog.getSecurity().getName());
			}
		});
		
		if(assignedInitiatorSecurity!=null&&assignedInitiatorSecurity.getSecurity()!=null)
			securityTextField.setText(assignedInitiatorSecurity.getSecurity().getName());
		
		securityTextField.getDocument().addDocumentListener(documentListener);
		

		
		
		JLabel counterpartySecurityLabel = new JLabel("Counterparty");
		counterpartySecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterpartySecurityLabel = new GridBagConstraints();
		gbc_counterpartySecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_counterpartySecurityLabel.insets = new Insets(0, 25, 5, 5);
		gbc_counterpartySecurityLabel.gridx = 0;
		gbc_counterpartySecurityLabel.gridy = 1;
		contentPanel.add(counterpartySecurityLabel, gbc_counterpartySecurityLabel);

		counterpartyTextField = new JTextField();
		counterpartyTextField.setPreferredSize(new Dimension(200, 25));
		counterpartyTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		counterpartyTextField.setBackground(new Color(255, 243, 204));
		counterpartyTextField.setColumns(10);
		
		AutoCompleteDecorator.decorate(counterpartyTextField,counterpartyTreeDialog.getCounterpartyList(),false);
		GridBagConstraints gbc_counterpartyTextField = new GridBagConstraints();
		gbc_counterpartyTextField.gridwidth = 2;
		gbc_counterpartyTextField.anchor = GridBagConstraints.NORTH;
		gbc_counterpartyTextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterpartyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterpartyTextField.gridx = 2;
		gbc_counterpartyTextField.gridy = 1;
		contentPanel.add(counterpartyTextField, gbc_counterpartyTextField);
		
		counterpartyNewButton = new JButton("...");
		counterpartyNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterpartyNewButton = new GridBagConstraints();
		gbc_counterpartyNewButton.insets = new Insets(0, 0, 5, 25);
		gbc_counterpartyNewButton.gridx = 4;
		gbc_counterpartyNewButton.gridy = 1;
		contentPanel.add(counterpartyNewButton, gbc_counterpartyNewButton);
		
		counterpartyNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				counterpartyTreeDialog.setVisible(true);
				if(counterpartyTreeDialog.getCounterparty()!=null&&!counterpartyTreeDialog.isCancelled())
					counterpartyTextField.setText(counterpartyTreeDialog.getCounterparty().getName());
			}
		});
		
		if(assignedInitiatorSecurity!=null&&assignedInitiatorSecurity.getCounterparty()!=null)
			counterpartyTextField.setText(assignedInitiatorSecurity.getCounterparty().getName());
		
		counterpartyTextField.getDocument().addDocumentListener(documentListener);		
		
		
		
		okButton = new JButton();

		if (this.assignedInitiatorSecurity == null) {
			this.assignedInitiatorSecurity = new AssignedInitiatorSecurity();
			setTitle("Subscribe Security");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Subscribed Security");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 2;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
								
				if (securityTextField.getText().trim().length() > 0)
					AssignedSecurityDialog.this.assignedInitiatorSecurity.setSecurity(securityTreeDialog.getSecurityForName(securityTextField.getText()));
				
				else
					AssignedSecurityDialog.this.assignedInitiatorSecurity.setSecurity(null);
				
				if (counterpartyTextField.getText().trim().length() > 0)
					AssignedSecurityDialog.this.assignedInitiatorSecurity.setCounterparty(counterpartyTreeDialog.getCounterpartyForName(counterpartyTextField.getText()));
				
				else
					AssignedSecurityDialog.this.assignedInitiatorSecurity.setCounterparty(null);
				
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridwidth = 2;
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 2;
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
		
		checkConsistency();
	}
	
	
	private void checkConsistency() {

		dirty = false;
		
		FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());
		
		if (assignedInitiatorSecurity.getSecurity() != null && !assignedInitiatorSecurity.getSecurity().equals(security))
			dirty = true;
		
		if ((assignedInitiatorSecurity.getSecurity() == null )
				&& security!=null)
			dirty = true;
		
		Counterparty counterparty = counterpartyTreeDialog.getCounterpartyForName(counterpartyTextField.getText());
		
		if (assignedInitiatorSecurity.getCounterparty() != null && !assignedInitiatorSecurity.getCounterparty().equals(counterparty))
			dirty = true;
		
		if ((assignedInitiatorSecurity.getCounterparty() == null )
				&& counterparty!=null)
			dirty = true;
		
		if(security==null)
			dirty=false;
		
		if(counterpartyTextField.getText().length()>0&&counterpartyTreeDialog.getCounterpartyForName(counterpartyTextField.getText())==null)
		{
			dirty = false;
			counterpartyTextField.setForeground(Color.RED.darker());
		}
		else
			counterpartyTextField.setForeground(Color.BLACK);
		
		okButton.setEnabled(dirty);

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
	 * Gets the assigned initiator security.
	 *
	 * @return the assigned initiator security
	 */
	public AssignedInitiatorSecurity getAssignedInitiatorSecurity() {
		
		return assignedInitiatorSecurity;
	}
}
