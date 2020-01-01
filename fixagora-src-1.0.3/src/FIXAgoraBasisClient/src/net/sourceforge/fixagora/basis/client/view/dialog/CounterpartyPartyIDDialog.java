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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.dialog.fix.AbstractFIXPanel;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;

/**
 * The Class CounterpartyPartyIDDialog.
 */
public class CounterpartyPartyIDDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private CounterPartyPartyID counterPartyPartyID = null;
	private boolean dirty;
	private final ImageIcon warning = new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private JComboBox businessComponentComboBox;
	private JTextField partyIDTextField;
	private JComboBox partyIDSourceComboBox;
	private JLabel partyIDWarningLabel;
	private JComboBox partyRoleComboBox;
	private JLabel partyRoleWarningLabel;

	/**
	 * Instantiates a new counterparty party id dialog.
	 *
	 * @param counterPartyPartyID the counter party party id
	 * @param businessComponents the business components
	 */
	public CounterpartyPartyIDDialog(CounterPartyPartyID counterPartyPartyID, List<AbstractBusinessObject> businessComponents) {

		this.counterPartyPartyID = counterPartyPartyID;
		setBounds(100, 100, 517, 237);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(CounterpartyPartyIDDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
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
		businessComponents2.add(new ComboBoxEntry("Default"));
		for (AbstractBusinessObject abstractBusinessObject : businessComponents)
			businessComponents2.add(new ComboBoxEntry(abstractBusinessObject,abstractBusinessObject.getName()));

		businessComponentComboBox = new JComboBox(businessComponents2.toArray());
		businessComponentComboBox.setMinimumSize(new Dimension(250, 25));
		businessComponentComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		businessComponentComboBox.setPreferredSize(new Dimension(250, 25));
		businessComponentComboBox.setBackground(new Color(255, 243, 204));
		
		if(counterPartyPartyID!=null&&counterPartyPartyID.getAbstractBusinessComponent()!=null)
			businessComponentComboBox.setSelectedItem(new ComboBoxEntry(counterPartyPartyID.getAbstractBusinessComponent(), null));
		else
			businessComponentComboBox.setSelectedIndex(0);
		businessComponentComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_businessComponentComboBox = new GridBagConstraints();
		gbc_businessComponentComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_businessComponentComboBox.gridwidth = 2;
		gbc_businessComponentComboBox.anchor = GridBagConstraints.NORTH;
		gbc_businessComponentComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_businessComponentComboBox.gridx = 2;
		gbc_businessComponentComboBox.gridy = 0;
		contentPanel.add(businessComponentComboBox, gbc_businessComponentComboBox);
		
		JLabel partyIDLabel = new JLabel("Party ID");
		partyIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityLabel = new GridBagConstraints();
		gbc_securityLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityLabel.insets = new Insets(0, 25, 5, 5);
		gbc_securityLabel.gridx = 0;
		gbc_securityLabel.gridy = 1;
		contentPanel.add(partyIDLabel, gbc_securityLabel);
		
		partyIDWarningLabel = new JLabel("");
		partyIDWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_securityWarningLabel = new GridBagConstraints();
		gbc_securityWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_securityWarningLabel.gridx = 1;
		gbc_securityWarningLabel.gridy = 1;
		contentPanel.add(partyIDWarningLabel, gbc_securityWarningLabel);

		partyIDTextField = new JTextField();
		partyIDTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		partyIDTextField.setBackground(new Color(255, 243, 204));
		partyIDTextField.setPreferredSize(new Dimension(200, 25));


		GridBagConstraints gbc_partyIDTextField = new GridBagConstraints();
		gbc_partyIDTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDTextField.gridwidth = 2;
		gbc_partyIDTextField.anchor = GridBagConstraints.NORTH;
		gbc_partyIDTextField.insets = new Insets(0, 0, 5, 25);
		gbc_partyIDTextField.gridx = 2;
		gbc_partyIDTextField.gridy = 1;
		contentPanel.add(partyIDTextField, gbc_partyIDTextField);
		
		if(counterPartyPartyID!=null)
			partyIDTextField.setText(counterPartyPartyID.getPartyID());
		
		partyIDTextField.getDocument().addDocumentListener(new DocumentListener() {

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
		});
		
		JLabel partyIDSourceLabel = new JLabel("Party ID Source");
		partyIDSourceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partyIDSourceLabel = new GridBagConstraints();
		gbc_partyIDSourceLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDSourceLabel.insets = new Insets(0, 25, 5, 5);
		gbc_partyIDSourceLabel.gridx = 0;
		gbc_partyIDSourceLabel.gridy = 2;
		contentPanel.add(partyIDSourceLabel, gbc_partyIDSourceLabel);

		List<ComboBoxEntry> partyIDSourceComboBoxEntries = new ArrayList<ComboBoxEntry>();
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
		partyIDSourceComboBox.setMinimumSize(new Dimension(250, 25));
		partyIDSourceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		partyIDSourceComboBox.setPreferredSize(new Dimension(250, 25));
		partyIDSourceComboBox.setBackground(new Color(255, 243, 204));
		
		if(counterPartyPartyID!=null)
			partyIDSourceComboBox.setSelectedItem(new ComboBoxEntry(counterPartyPartyID.getPartyIDSource(), null));
		else
			partyIDSourceComboBox.setSelectedIndex(2);
		partyIDSourceComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_partyIDSourceComboBox = new GridBagConstraints();
		gbc_partyIDSourceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyIDSourceComboBox.gridwidth = 2;
		gbc_partyIDSourceComboBox.anchor = GridBagConstraints.NORTH;
		gbc_partyIDSourceComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_partyIDSourceComboBox.gridx = 2;
		gbc_partyIDSourceComboBox.gridy = 2;
		contentPanel.add(partyIDSourceComboBox, gbc_partyIDSourceComboBox);
		
		
		JLabel partyRoleLabel = new JLabel("Party Role");
		partyRoleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partyRoleLabel = new GridBagConstraints();
		gbc_partyRoleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyRoleLabel.insets = new Insets(0, 25, 5, 5);
		gbc_partyRoleLabel.gridx = 0;
		gbc_partyRoleLabel.gridy = 3;
		contentPanel.add(partyRoleLabel, gbc_partyRoleLabel);
		
		partyRoleWarningLabel = new JLabel("");
		partyRoleWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_partyRoleWarningLabel = new GridBagConstraints();
		gbc_partyRoleWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyRoleWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_partyRoleWarningLabel.gridx = 1;
		gbc_partyRoleWarningLabel.gridy = 3;
		contentPanel.add(partyRoleWarningLabel, gbc_partyRoleWarningLabel);

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
		partyRoleComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		partyRoleComboBox.setPreferredSize(new Dimension(32, 25));
		partyRoleComboBox.setBackground(new Color(255, 243, 204));
		
		if(counterPartyPartyID!=null)
			partyRoleComboBox.setSelectedItem(new ComboBoxEntry(counterPartyPartyID.getPartyRole(), null));
		else
			partyRoleComboBox.setSelectedIndex(0);
		
		partyRoleComboBox.addActionListener(actionListener);
		
		GridBagConstraints gbc_partyRoleComboBox = new GridBagConstraints();
		gbc_partyRoleComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_partyRoleComboBox.gridwidth = 2;
		gbc_partyRoleComboBox.anchor = GridBagConstraints.NORTH;
		gbc_partyRoleComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_partyRoleComboBox.gridx = 2;
		gbc_partyRoleComboBox.gridy = 3;
		contentPanel.add(partyRoleComboBox, gbc_partyRoleComboBox);

		okButton = new JButton();
		
		if (this.counterPartyPartyID == null) {
			this.counterPartyPartyID = new CounterPartyPartyID();
			setTitle("Add Party ID");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Party ID");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 4;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;				
				CounterpartyPartyIDDialog.this.counterPartyPartyID.setAbstractBusinessComponent((AbstractBusinessComponent) ((ComboBoxEntry) businessComponentComboBox.getSelectedItem()).getEntry());
				CounterpartyPartyIDDialog.this.counterPartyPartyID.setPartyID(partyIDTextField.getText());
				CounterpartyPartyIDDialog.this.counterPartyPartyID.setPartyIDSource((String) ((ComboBoxEntry) partyIDSourceComboBox.getSelectedItem()).getEntry());
				if (businessComponentComboBox.getSelectedIndex() == 0)
					CounterpartyPartyIDDialog.this.counterPartyPartyID.setPartyRole(null);
				else
					CounterpartyPartyIDDialog.this.counterPartyPartyID.setPartyRole((Integer) ((ComboBoxEntry) partyRoleComboBox.getSelectedItem()).getEntry());
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 4;
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
	
	
	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}
	
	private void checkConsistency() {

		dirty = false;
				
		if (counterPartyPartyID.getAbstractBusinessComponent() != null && !counterPartyPartyID.getAbstractBusinessComponent().equals(((ComboBoxEntry) businessComponentComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((counterPartyPartyID.getAbstractBusinessComponent() == null )
				&& businessComponentComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		
		if (counterPartyPartyID.getPartyIDSource() != null && !counterPartyPartyID.getPartyIDSource().equals(((ComboBoxEntry) partyIDSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((counterPartyPartyID.getPartyIDSource() == null )
				&& partyIDSourceComboBox.getSelectedIndex() > 0)
			dirty = true;
			
		
		if (counterPartyPartyID.getPartyRole() != null && !counterPartyPartyID.getPartyRole().equals(((ComboBoxEntry) partyRoleComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((counterPartyPartyID.getPartyRole() == null )
				&& partyRoleComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		if(dirtyFieldCheck(counterPartyPartyID.getPartyID(), partyIDTextField))
			dirty=true;
		
		if (partyIDTextField.getText().trim().length() == 0)
		{
			partyIDWarningLabel.setIcon(warning);
			partyIDWarningLabel.setToolTipText("Party ID is empty.");
			dirty = false;
		}
		else
		{
			partyIDWarningLabel.setIcon(null);
			partyIDWarningLabel.setToolTipText(null);			
		}
		
		if (businessComponentComboBox.getSelectedIndex() == 0)
		{
			partyRoleComboBox.setEnabled(false);
			partyRoleWarningLabel.setIcon(null);
			partyRoleWarningLabel.setToolTipText(null);			
		}
		else
		{
			partyRoleComboBox.setEnabled(true);
			
			if(partyRoleComboBox.getSelectedIndex()==0)
			{
				partyRoleWarningLabel.setIcon(warning);
				partyRoleWarningLabel.setToolTipText("Party role is empty.");
				dirty = false;
			}
			else
			{
				partyRoleWarningLabel.setIcon(null);
				partyRoleWarningLabel.setToolTipText(null);			
			}
		}
				
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
	 * Gets the counter party party id.
	 *
	 * @return the counter party party id
	 */
	public CounterPartyPartyID getCounterPartyPartyID() {
	
		return counterPartyPartyID;
	}



}
