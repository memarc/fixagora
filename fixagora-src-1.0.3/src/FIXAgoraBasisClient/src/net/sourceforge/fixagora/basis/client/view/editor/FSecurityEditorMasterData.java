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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.model.editor.SecurityAltIDTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityAltIDDialog;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

/**
 * The Class FSecurityEditorMasterData.
 */
public class FSecurityEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private JLabel nameWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private FSecurityEditor securityEditor = null;
	
	private JTextField securityIdentifierTextField = null;
	
	private JTextField cfiTextField = null;
	
	private JTextField securitySubTypeTextField = null;

	private JComboBox additionalInformationComboBox;

	private JComboBox securityIdentifierSourceComboBox;

	private JComboBox productComboBox;

	private JComboBox securityTypeComboBox;

	private boolean update = false;

	private JTable table;

	private SecurityAltIDTableModel securityAltIDTableModel;

	private JScrollPane scrollPane;

	private JButton addButton;

	private JButton removeButton;

	private boolean consistent;

	private JLabel maturityDateWarningLabel;

	private JDateChooser maturityDateChooser;

	private JLabel maturityTimeWarningLabel;

	private JDateChooser maturityTimeDateChooser;

	private JLabel couponPaymentDateWarningLabel;

	private JDateChooser couponPaymentDateChooser;

	private JLabel issueDateWarningLabel;

	private JDateChooser issueDateChooser;

	private JLabel redemptionDateWarningLabel;

	private JDateChooser redemptionDateChooser;

	private JLabel interestAccrualDateWarningLabel;

	private JDateChooser interestAccrualDateChooser;

	private JLabel datedDateWarningLabel;

	private JDateChooser datedDateChooser;

	private JTextField currencyTextField;

	private JComboBox putOrCallComboBoxComboBox;

	private JTextField issuerTextField;

	private JComboBox countryComboBox;

	private JTextField stateOfIssueTextField;

	private JTextField localeOfIssueTextField;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

	private JLabel securityIdWarningLabel;


	/**
	 * Instantiates a new f security editor master data.
	 *
	 * @param securityEditor the security editor
	 */
	public FSecurityEditorMasterData(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;

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
		
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("date")) {

					checkConsistency();

				}

			}
		};

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,1.0};
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 370, 0, 0, 370, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 12;
		gbc_leftFillPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel userLabel = new JLabel("Symbol");
		userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_userLabel = new GridBagConstraints();
		gbc_userLabel.anchor = GridBagConstraints.WEST;
		gbc_userLabel.insets = new Insets(50, 50, 5, 10);
		gbc_userLabel.gridx = 1;
		gbc_userLabel.gridy = 0;
		panel.add(userLabel, gbc_userLabel);

		nameWarningLabel = securityEditor.getNameWarningLabel();
		nameWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_nameWarningLabel = new GridBagConstraints();
		gbc_nameWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_nameWarningLabel.gridx = 2;
		gbc_nameWarningLabel.gridy = 0;
		panel.add(nameWarningLabel, gbc_nameWarningLabel);

		nameTextField = new JTextField(securityEditor.getSecurity().getName());
		nameTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		nameTextField.getDocument().addDocumentListener(documentListener);
		nameTextField.setBackground(new Color(255, 243, 204));
		nameTextField.setPreferredSize(new Dimension(250, 25));
		nameTextField.setColumns(10);
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
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
		gbc_modifiedField.insets = new Insets(0, 0, 5, 50);
		gbc_modifiedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedField.gridx = 3;
		gbc_modifiedField.gridy = 1;
		panel.add(modifiedField, gbc_modifiedField);

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 12;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 7;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JLabel additionalInformationLabel = new JLabel("Additional Information");
		additionalInformationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_additionalInformationLabel = new GridBagConstraints();
		gbc_additionalInformationLabel.anchor = GridBagConstraints.WEST;
		gbc_additionalInformationLabel.insets = new Insets(0, 50, 5, 10);
		gbc_additionalInformationLabel.gridx = 1;
		gbc_additionalInformationLabel.gridy = 2;
		panel.add(additionalInformationLabel, gbc_additionalInformationLabel);

		List<ComboBoxEntry> additionalInformationComboBoxEntries = new ArrayList<ComboBoxEntry>();
		additionalInformationComboBoxEntries.add(new ComboBoxEntry());
		additionalInformationComboBoxEntries.add(new ComboBoxEntry("CD", "EUCP with lump-sum interest rather than discount price"));
		additionalInformationComboBoxEntries.add(new ComboBoxEntry("WI", "When Issued"));

		additionalInformationComboBox = new JComboBox(additionalInformationComboBoxEntries.toArray());
		additionalInformationComboBox.setMinimumSize(new Dimension(32, 25));
		additionalInformationComboBox.setPreferredSize(new Dimension(32, 25));
		additionalInformationComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_additionalInformationComboBox = new GridBagConstraints();
		gbc_additionalInformationComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_additionalInformationComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_additionalInformationComboBox.gridx = 3;
		gbc_additionalInformationComboBox.gridy = 2;
		panel.add(additionalInformationComboBox, gbc_additionalInformationComboBox);

		additionalInformationComboBox.addActionListener(actionListener);

		JLabel lblProduct = new JLabel("Product");
		lblProduct.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblProduct = new GridBagConstraints();
		gbc_lblProduct.anchor = GridBagConstraints.WEST;
		gbc_lblProduct.insets = new Insets(0, 50, 5, 5);
		gbc_lblProduct.gridx = 1;
		gbc_lblProduct.gridy = 3;
		panel.add(lblProduct, gbc_lblProduct);
		
		List<ComboBoxEntry> productComboBoxEntries = new ArrayList<ComboBoxEntry>();
		productComboBoxEntries.add(new ComboBoxEntry());
		productComboBoxEntries.add(new ComboBoxEntry(1, "Agency"));
		productComboBoxEntries.add(new ComboBoxEntry(2, "Commodity"));
		productComboBoxEntries.add(new ComboBoxEntry(3, "Corporate"));
		productComboBoxEntries.add(new ComboBoxEntry(4, "Currency"));
		productComboBoxEntries.add(new ComboBoxEntry(5, "Equity"));
		productComboBoxEntries.add(new ComboBoxEntry(6, "Government"));
		productComboBoxEntries.add(new ComboBoxEntry(7, "Index"));
		productComboBoxEntries.add(new ComboBoxEntry(8, "Loan"));
		productComboBoxEntries.add(new ComboBoxEntry(9, "Money Market"));
		productComboBoxEntries.add(new ComboBoxEntry(10, "Mortage"));
		productComboBoxEntries.add(new ComboBoxEntry(11, "Municipal"));
		productComboBoxEntries.add(new ComboBoxEntry(12, "Other"));
		productComboBoxEntries.add(new ComboBoxEntry(13, "Financing"));

		productComboBox = new JComboBox(productComboBoxEntries.toArray());
		productComboBox.setMinimumSize(new Dimension(32, 25));
		productComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		productComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_productComboBox = new GridBagConstraints();
		gbc_productComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_productComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_productComboBox.gridx = 3;
		gbc_productComboBox.gridy = 3;
		panel.add(productComboBox, gbc_productComboBox);

		productComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				List<ComboBoxEntry> securityTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
				securityTypeComboBoxEntries.add(new ComboBoxEntry());

				switch (productComboBox.getSelectedIndex()) {
					case 1:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUSUPRA", "Euro Supranational Coupons"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FAC", "Federal Agency Coupon"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FADN", "Federal Agency Discount Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PEF", "Private Export Funding"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SUPRA", "USD Supranational Coupons"));
						break;

					case 3:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CORP", "Corporate Bond"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CPP", "Corporate Private Placement"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CB", "Convertible Bond"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("DUAL", "Dual Currency"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUCORP", "Euro Corporate Bond"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUFRN", "Euro Corporate Floating Rate Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FRN", "US Corporate Floating Rate Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("XLINKD", "Indexed Linked"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("STRUCT", "Structured Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("YANK", "Yankee Corporate Bond"));
						break;

					case 4:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FXNDF", "Non-deliverable forward"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FXSPOT", "FX Spot"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FXFWD", "FX Forward"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FXSWAP", "FX Swap"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FOR", "Foreign Exchange Contract"));
						break;

					case 5:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CS", "Common Stock"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PS", "Preferred Stock"));
						break;

					case 13:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("REPO", "Repurchase"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FORWARD", "Forward"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BUYSELL", "Buy Sellback"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SECLOAN", "Securities Loan"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SECPLEDGE", "Securities Pledge"));
						break;

					case 6:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BRADY", "Brady Bond"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CAN", "Canadian Treasury Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CTB", "Canadian Treasury Bills"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUSOV", "Euro Sovereigns"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PROV", "Canadian Provincial Bonds"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TB", "Treasury Bill - non US"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TBOND", "US Treasury Bond"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TINT", "Interest Strip From Any Bond Or Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TBILL", "US Treasury Bill"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TIPS", "Treasury Inflation Protected Securities"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TCAL", "Principal Strip Of A Callable Bond Or Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TPRN", "Principal Strip From A Non-Callable Bond Or Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TNOTE", "US Treasury Note"));
						break;

					case 8:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TERM", "Term Loan"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("RVLV", "Revolver Loan"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("RVLVTRM", "Revolver/Term Loan"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BRIDGE", "Bridge Loan"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("LOFC", "Letter Of Credit"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SWING", "Swing Line Facility"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("DINP", "Debtor In Possession"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("DEFLTED", "Defaulted"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("WITHDRN", "Withdrawn"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("REPLACD", "Replaced"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MATURED", "Matured"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("AMENDED", "Amended and Restated"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("RETIRED", "Retired"));
						break;

					case 9:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BA", "Bankers Acceptance"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BDN", "Bank Depository Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BN", "Bank Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("BOX", "Bill Of Exchanges"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CAMM", "Canadian Money Markets"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CD", "Certificate Of Deposit"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CL", "Call Loans"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CP", "Commercial Paper"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("DN", "Deposit Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUCD", "Euro Certificate Of Deposit"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("EUCP", "Euro Commercial Paper"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("LQN", "Liquidity Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MTN", "Medium Term Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("ONITE", "Overnight"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PN", "Promissory Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PZFJ", "Plazos Fijos"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("STN", "Short Term Loan Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SLQN", "Secured Liquidity Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TD", "Time Deposit"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TLQN", "Term Liquidity Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("XCN", "Extended Comm Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("YCD", "Yankee Certificate Of Deposit"));
						break;

					case 10:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("ABS", "Asset-backed Securities"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CMB", "Canadian Mortgage Bonds"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CMBS", "Corp. Mortgage-backed Securities"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CMO", "Collateralized Mortgage Obligation"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("IET", "IOETTE Mortgage"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MBS", "Mortgage-backed Securities"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MIO", "Mortgage Interest Only"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MPO", "Mortgage Principal Only"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MPP", "Mortgage Private Placement"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MPT", "Miscellaneous Pass-through"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("PFAND", "Pfandbriefe"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TBA", "To Be Announced"));
						break;

					case 11:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("AN", "Other Anticipation Notes"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("COFO", "Certificate Of Obligation"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("COFP", "Certificate Of Participation"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("GO", "General Obligation Bonds"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MT", "Mandatory Tender"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("RAN", "Revenue Anticipation Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("REV", "Revenue Bonds"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SPCLA", "Special Assessment"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SPCLO", "Special Obligation"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("SPCLT", "Special Tax"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TAN", "Tax Anticipation Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TAXA", "Tax Allocation"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TECP", "Tax Exempt Commercial Paper"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TMCP", "Taxable Municipal CP"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("TRAN", "Tax Revenue Anticipation Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("VRDN", "Variable Rate Demand Note"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("WAR", "Warrant"));
						break;

					case 0:
					case 12:
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MF", "Mutual Fund"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("MLEG", "Multileg Instrument"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("NONE", "No Security Type"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CASH", "Cash"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("CDS", "Credit Default Swap"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("FUT", "Future"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("OPT", "Option"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("OOF", "Options on Futures"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("OOP", "Options on Physical"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("IRS", "Interest Rate Swap"));
						securityTypeComboBoxEntries.add(new ComboBoxEntry("OOC", "Options on Combo"));
						break;

					default:
						break;
				}
				securityTypeComboBox.setModel(new DefaultComboBoxModel(securityTypeComboBoxEntries.toArray()));
				if (securityTypeComboBoxEntries.size() == 1) {
					securityTypeComboBox.setSelectedIndex(0);
					securityTypeComboBox.setBackground(new Color(204, 216, 255));
					securityTypeComboBox.setEnabled(false);
				}
				else {
					securityTypeComboBox.setBackground(new Color(255, 243, 204));
					securityTypeComboBox.setEnabled(true);
					if (securityEditor.getSecurity().getSecurityDetails().getSecurityType() != null
							&& securityTypeComboBoxEntries.contains(securityEditor.getSecurity().getSecurityDetails().getSecurityType())) {
						securityTypeComboBox.setSelectedItem(securityEditor.getSecurity().getSecurityDetails().getSecurityType());
					}
					else {
						securityTypeComboBox.setSelectedIndex(0);
					}
				}
				checkConsistency();
			}
		});

		addButton = new JButton("Add");
		addButton.setHorizontalAlignment(SwingConstants.LEFT);
		addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addButton.setPreferredSize(new Dimension(100, 25));
		addButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.anchor = GridBagConstraints.EAST;
		gbc_addButton.insets = new Insets(0, 50, 5, 25);
		gbc_addButton.gridx = 4;
		gbc_addButton.gridy = 3;
		panel.add(addButton, gbc_addButton);

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SecurityAltIDDialog securityAltIDDialog = new SecurityAltIDDialog();
				securityAltIDDialog.setVisible(true);
				if (!securityAltIDDialog.isCancelled()) {
					SecurityAltIDGroup securityAltIDGroup = securityAltIDDialog.getSecurityAltIDGroup();
					securityAltIDGroup.setSecurity(securityEditor.getSecurity().getSecurityDetails());
					securityAltIDTableModel.addSecurityAltIDGroup(securityAltIDGroup);
					checkConsistency();
				}
			}
		});

		JLabel lblClassificationOfFinancial = new JLabel("Classification of Financial Instrument");
		lblClassificationOfFinancial.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblClassificationOfFinancial = new GridBagConstraints();
		gbc_lblClassificationOfFinancial.anchor = GridBagConstraints.WEST;
		gbc_lblClassificationOfFinancial.insets = new Insets(0, 50, 5, 25);
		gbc_lblClassificationOfFinancial.gridx = 1;
		gbc_lblClassificationOfFinancial.gridy = 4;
		panel.add(lblClassificationOfFinancial, gbc_lblClassificationOfFinancial);

		cfiTextField = new JTextField();
		cfiTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		cfiTextField.setPreferredSize(new Dimension(250, 25));
		cfiTextField.setColumns(10);
		cfiTextField.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_cfiTextField = new GridBagConstraints();
		gbc_cfiTextField.insets = new Insets(0, 0, 5, 50);
		gbc_cfiTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_cfiTextField.gridx = 3;
		gbc_cfiTextField.gridy = 4;
		panel.add(cfiTextField, gbc_cfiTextField);

		removeButton = new JButton("Remove");
		removeButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeButton.setPreferredSize(new Dimension(100, 25));
		removeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_removeButton.insets = new Insets(0, 50, 5, 25);
		gbc_removeButton.gridx = 4;
		gbc_removeButton.gridy = 4;
		panel.add(removeButton, gbc_removeButton);

		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					securityAltIDTableModel.remove(table.getSelectionModel().getMinSelectionIndex());
					checkConsistency();
				}
			}
		});

		List<ComboBoxEntry> securityTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
		securityTypeComboBoxEntries.add(new ComboBoxEntry());

		JLabel lblSecurityType = new JLabel("Security Type");
		lblSecurityType.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecurityType = new GridBagConstraints();
		gbc_lblSecurityType.anchor = GridBagConstraints.WEST;
		gbc_lblSecurityType.insets = new Insets(0, 50, 5, 5);
		gbc_lblSecurityType.gridx = 1;
		gbc_lblSecurityType.gridy = 5;
		panel.add(lblSecurityType, gbc_lblSecurityType);

		securityTypeComboBox = new JComboBox(securityTypeComboBoxEntries.toArray());
		securityTypeComboBox.setMinimumSize(new Dimension(32, 25));
		securityTypeComboBox.setPreferredSize(new Dimension(32, 25));
		securityTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_comboBox_3 = new GridBagConstraints();
		gbc_comboBox_3.insets = new Insets(0, 0, 5, 50);
		gbc_comboBox_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_3.gridx = 3;
		gbc_comboBox_3.gridy = 5;
		panel.add(securityTypeComboBox, gbc_comboBox_3);

		securityTypeComboBox.addActionListener(actionListener);

		JLabel lblSecuritySubtype = new JLabel("Security Sub-type");
		lblSecuritySubtype.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecuritySubtype = new GridBagConstraints();
		gbc_lblSecuritySubtype.anchor = GridBagConstraints.WEST;
		gbc_lblSecuritySubtype.insets = new Insets(3, 50, 5, 5);
		gbc_lblSecuritySubtype.gridx = 1;
		gbc_lblSecuritySubtype.gridy = 6;
		panel.add(lblSecuritySubtype, gbc_lblSecuritySubtype);
		
		securitySubTypeTextField = new JTextField();
		securitySubTypeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		securitySubTypeTextField.setPreferredSize(new Dimension(250, 25));
		securitySubTypeTextField.setColumns(10);
		securitySubTypeTextField.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_securitySubTypeTextField = new GridBagConstraints();
		gbc_securitySubTypeTextField.insets = new Insets(0, 0, 5, 50);
		gbc_securitySubTypeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securitySubTypeTextField.gridx = 3;
		gbc_securitySubTypeTextField.gridy = 6;
		panel.add(securitySubTypeTextField, gbc_securitySubTypeTextField);
		
		
		
		JLabel lblMaturityDate = new JLabel("Maturity Date");
		lblMaturityDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblMaturityDate = new GridBagConstraints();
		gbc_lblMaturityDate.anchor = GridBagConstraints.WEST;
		gbc_lblMaturityDate.insets = new Insets(0, 50, 5, 5);
		gbc_lblMaturityDate.gridx = 1;
		gbc_lblMaturityDate.gridy = 7;
		panel.add(lblMaturityDate, gbc_lblMaturityDate);
		
		maturityDateWarningLabel = new JLabel("");
		maturityDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_maturityDateWarningLabel = new GridBagConstraints();
		gbc_maturityDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maturityDateWarningLabel.gridx = 2;
		gbc_maturityDateWarningLabel.gridy = 7;
		panel.add(maturityDateWarningLabel, gbc_maturityDateWarningLabel);

		maturityDateChooser = new JDateChooser();
		maturityDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)maturityDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		maturityDateChooser.setOpaque(false);
		maturityDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) maturityDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5, new Color(204,
				216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 50);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 7;
		panel.add(maturityDateChooser, gbc_textField);

		maturityDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) maturityDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel maturityTimeLabel = new JLabel("Maturity Time");
		maturityTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_maturityTimeLabel = new GridBagConstraints();
		gbc_maturityTimeLabel.anchor = GridBagConstraints.WEST;
		gbc_maturityTimeLabel.insets = new Insets(0, 50, 5, 5);
		gbc_maturityTimeLabel.gridx = 1;
		gbc_maturityTimeLabel.gridy = 8;
		panel.add(maturityTimeLabel, gbc_maturityTimeLabel);
		
		maturityTimeWarningLabel = new JLabel("");
		maturityTimeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_maturityTimeWarningLabel = new GridBagConstraints();
		gbc_maturityTimeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maturityTimeWarningLabel.gridx = 2;
		gbc_maturityTimeWarningLabel.gridy = 8;
		panel.add(maturityTimeWarningLabel, gbc_maturityTimeWarningLabel);		

		maturityTimeDateChooser = new JDateChooser();
		maturityTimeDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)maturityTimeDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		maturityTimeDateChooser.setOpaque(false);
		maturityTimeDateChooser.getCalendarButton().setVisible(false);
		maturityTimeDateChooser.setDateFormatString("HH:mm:ss");
		((JTextFieldDateEditor) maturityTimeDateChooser.getDateEditor()).setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		maturityTimeDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_maturityTimeDateChooser = new GridBagConstraints();
		gbc_maturityTimeDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_maturityTimeDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_maturityTimeDateChooser.gridx = 3;
		gbc_maturityTimeDateChooser.gridy = 8;
		panel.add(maturityTimeDateChooser, gbc_maturityTimeDateChooser);

		maturityTimeDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) maturityTimeDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		
		JLabel lblNewLabel = new JLabel("Coupon Payment Date");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 50, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 9;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		couponPaymentDateWarningLabel = new JLabel();
		couponPaymentDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_couponPaymentDateWarningLabel = new GridBagConstraints();
		gbc_couponPaymentDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_couponPaymentDateWarningLabel.gridx = 2;
		gbc_couponPaymentDateWarningLabel.gridy = 9;
		panel.add(couponPaymentDateWarningLabel, gbc_couponPaymentDateWarningLabel);

		couponPaymentDateChooser = new JDateChooser();
		couponPaymentDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)couponPaymentDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		couponPaymentDateChooser.setOpaque(false);
		couponPaymentDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) couponPaymentDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5,
				new Color(204, 216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		couponPaymentDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_couponPaymentDateChooser = new GridBagConstraints();
		gbc_couponPaymentDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_couponPaymentDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_couponPaymentDateChooser.gridx = 3;
		gbc_couponPaymentDateChooser.gridy = 9;
		panel.add(couponPaymentDateChooser, gbc_couponPaymentDateChooser);

		couponPaymentDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) couponPaymentDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel lblIssueDate = new JLabel("Issue Date");
		lblIssueDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblIssueDate = new GridBagConstraints();
		gbc_lblIssueDate.anchor = GridBagConstraints.WEST;
		gbc_lblIssueDate.insets = new Insets(0, 50, 5, 5);
		gbc_lblIssueDate.gridx = 1;
		gbc_lblIssueDate.gridy = 10;
		panel.add(lblIssueDate, gbc_lblIssueDate);
		
		issueDateWarningLabel = new JLabel("");
		issueDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_issueDateWarningLabel = new GridBagConstraints();
		gbc_issueDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_issueDateWarningLabel.gridx = 2;
		gbc_issueDateWarningLabel.gridy = 10;
		panel.add(issueDateWarningLabel, gbc_issueDateWarningLabel);

		issueDateChooser = new JDateChooser();
		issueDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)issueDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		issueDateChooser.setOpaque(false);
		issueDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) issueDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5, new Color(204,
				216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		issueDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_issueDateChooser = new GridBagConstraints();
		gbc_issueDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_issueDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_issueDateChooser.gridx = 3;
		gbc_issueDateChooser.gridy = 10;
		panel.add(issueDateChooser, gbc_issueDateChooser);

		issueDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) issueDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel lblNewLabel_1 = new JLabel("Redemption Date");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 50, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 11;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		redemptionDateWarningLabel = new JLabel("");
		redemptionDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_redemptionDateWarningLabel = new GridBagConstraints();
		gbc_redemptionDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_redemptionDateWarningLabel.gridx = 2;
		gbc_redemptionDateWarningLabel.gridy = 11;
		panel.add(redemptionDateWarningLabel, gbc_redemptionDateWarningLabel);

		redemptionDateChooser = new JDateChooser();
		redemptionDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)redemptionDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		redemptionDateChooser.setOpaque(false);
		redemptionDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) redemptionDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5, new Color(
				204, 216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		redemptionDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_redemptionDateChooser = new GridBagConstraints();
		gbc_redemptionDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_redemptionDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_redemptionDateChooser.gridx = 3;
		gbc_redemptionDateChooser.gridy = 11;
		panel.add(redemptionDateChooser, gbc_redemptionDateChooser);

		redemptionDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) redemptionDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel lblInterestAccrualDate = new JLabel("Interest Accrual Date");
		lblInterestAccrualDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblInterestAccrualDate = new GridBagConstraints();
		gbc_lblInterestAccrualDate.anchor = GridBagConstraints.WEST;
		gbc_lblInterestAccrualDate.insets = new Insets(0, 50, 5, 5);
		gbc_lblInterestAccrualDate.gridx = 1;
		gbc_lblInterestAccrualDate.gridy = 12;
		panel.add(lblInterestAccrualDate, gbc_lblInterestAccrualDate);
		
		interestAccrualDateWarningLabel = new JLabel("");
		interestAccrualDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_interestAccrualDateWarningLabel = new GridBagConstraints();
		gbc_interestAccrualDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_interestAccrualDateWarningLabel.gridx = 2;
		gbc_interestAccrualDateWarningLabel.gridy = 12;
		panel.add(interestAccrualDateWarningLabel, gbc_interestAccrualDateWarningLabel);		

		interestAccrualDateChooser = new JDateChooser();
		interestAccrualDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)interestAccrualDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		interestAccrualDateChooser.setOpaque(false);
		interestAccrualDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) interestAccrualDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5,
				new Color(204, 216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		interestAccrualDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_interestAccrualDateChooser = new GridBagConstraints();
		gbc_interestAccrualDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_interestAccrualDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_interestAccrualDateChooser.gridx = 3;
		gbc_interestAccrualDateChooser.gridy = 12;
		panel.add(interestAccrualDateChooser, gbc_interestAccrualDateChooser);

		interestAccrualDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) interestAccrualDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel lblDatedDate = new JLabel("Dated Date");
		lblDatedDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblDatedDate = new GridBagConstraints();
		gbc_lblDatedDate.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDatedDate.insets = new Insets(3, 50, 5, 5);
		gbc_lblDatedDate.gridx = 1;
		gbc_lblDatedDate.gridy = 13;
		panel.add(lblDatedDate, gbc_lblDatedDate);
		
		datedDateWarningLabel = new JLabel("");
		datedDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_datedDateWarningLabel = new GridBagConstraints();
		gbc_datedDateWarningLabel.anchor = GridBagConstraints.NORTH;
		gbc_datedDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_datedDateWarningLabel.gridx = 2;
		gbc_datedDateWarningLabel.gridy = 13;
		panel.add(datedDateWarningLabel, gbc_datedDateWarningLabel);		

		datedDateChooser = new JDateChooser();
		datedDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor)datedDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		datedDateChooser.setOpaque(false);
		datedDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) datedDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5, new Color(204,
				216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		datedDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_datedDateChooser = new GridBagConstraints();
		gbc_datedDateChooser.anchor = GridBagConstraints.NORTH;
		gbc_datedDateChooser.insets = new Insets(0, 0, 5, 50);
		gbc_datedDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_datedDateChooser.gridx = 3;
		gbc_datedDateChooser.gridy = 13;
		panel.add(datedDateChooser, gbc_datedDateChooser);

		datedDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) datedDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		

		// col2

		JLabel securityIdentifierLabel = new JLabel("Security Identifier");
		securityIdentifierLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityIdentifierLabel = new GridBagConstraints();
		gbc_securityIdentifierLabel.anchor = GridBagConstraints.WEST;
		gbc_securityIdentifierLabel.insets = new Insets(50, 0, 5, 0);
		gbc_securityIdentifierLabel.gridx = 4;
		gbc_securityIdentifierLabel.gridy = 0;
		panel.add(securityIdentifierLabel, gbc_securityIdentifierLabel);

		securityIdWarningLabel = securityEditor.getSecurityIdWarningLabel();
		securityIdWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_securityIdWarningLabel = new GridBagConstraints();
		gbc_securityIdWarningLabel.insets = new Insets(50, 0, 5, 5);
		gbc_securityIdWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_securityIdWarningLabel.gridx = 5;
		gbc_securityIdWarningLabel.gridy = 0;
		panel.add(securityIdWarningLabel, gbc_securityIdWarningLabel);
		
		securityIdentifierTextField = new JTextField();
		securityIdentifierTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		securityIdentifierTextField.setPreferredSize(new Dimension(250, 25));
		securityIdentifierTextField.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_securityidentifierTextField = new GridBagConstraints();
		gbc_securityidentifierTextField.insets = new Insets(50, 0, 5, 50);
		gbc_securityidentifierTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityidentifierTextField.gridx = 6;
		gbc_securityidentifierTextField.gridy = 0;
		panel.add(securityIdentifierTextField, gbc_securityidentifierTextField);
		securityIdentifierTextField.setColumns(10);

		JLabel lblSecurityIdentifierSource = new JLabel("Security Identifier Source");
		lblSecurityIdentifierSource.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecurityIdentifierSource = new GridBagConstraints();
		gbc_lblSecurityIdentifierSource.anchor = GridBagConstraints.WEST;
		gbc_lblSecurityIdentifierSource.insets = new Insets(0, 0, 5, 0);
		gbc_lblSecurityIdentifierSource.gridx = 4;
		gbc_lblSecurityIdentifierSource.gridy = 1;
		panel.add(lblSecurityIdentifierSource, gbc_lblSecurityIdentifierSource);

		List<ComboBoxEntry> securityIdentifierSourceComboBoxEntries = new ArrayList<ComboBoxEntry>();
		securityIdentifierSourceComboBoxEntries.add(new ComboBoxEntry());
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
		gbc_securityIdentifierSourceComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_securityIdentifierSourceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityIdentifierSourceComboBox.gridx = 6;
		gbc_securityIdentifierSourceComboBox.gridy = 1;
		panel.add(securityIdentifierSourceComboBox, gbc_securityIdentifierSourceComboBox);

		securityIdentifierSourceComboBox.addActionListener(actionListener);

		JLabel lblSecurityAlternativeIdentifier = new JLabel("Security Alternative Identifier");
		lblSecurityAlternativeIdentifier.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSecurityAlternativeIdentifier = new GridBagConstraints();
		gbc_lblSecurityAlternativeIdentifier.anchor = GridBagConstraints.WEST;
		gbc_lblSecurityAlternativeIdentifier.insets = new Insets(5, 0, 5, 25);
		gbc_lblSecurityAlternativeIdentifier.gridx = 4;
		gbc_lblSecurityAlternativeIdentifier.gridy = 2;
		panel.add(lblSecurityAlternativeIdentifier, gbc_lblSecurityAlternativeIdentifier);

		scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, Color.GRAY, width / 2.F, 26, Color.BLACK);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		scrollPane.setPreferredSize(new Dimension(200, 100));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 6;
		gbc_scrollPane.gridwidth = 1;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 50);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 6;
		gbc_scrollPane.gridy = 2;
		panel.add(scrollPane, gbc_scrollPane);

		table = new JTable();
		table.setTableHeader(new JTableHeader(table.getColumnModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, Color.GRAY, width / 2.F, 25, Color.BLACK);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}
		});

		table.getTableHeader().setReorderingAllowed(false);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		securityAltIDTableModel = new SecurityAltIDTableModel(securityEditor);
		table.setModel(securityAltIDTableModel);

		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				securityEditor.checkDirty();
			}
		});

		table.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(securityEditor));
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(178);
		table.getColumnModel().getColumn(1).setPreferredWidth(177);
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

		securityAltIDTableModel.setTable(table);
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));
		securityAltIDTableModel.setTable(table);
		securityAltIDTableModel.setMinWidth(177);

		scrollPane.setViewportView(table);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject()))
					removeButton.setEnabled(true);

				else
					removeButton.setEnabled(false);
			}
		});
		
		JLabel currencyLabel = new JLabel("Currency");
		currencyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_currencyLabel = new GridBagConstraints();
		gbc_currencyLabel.anchor = GridBagConstraints.WEST;
		gbc_currencyLabel.insets = new Insets(0, 0, 5, 10);
		gbc_currencyLabel.gridx = 4;
		gbc_currencyLabel.gridy = 8;
		panel.add(currencyLabel, gbc_currencyLabel);

		currencyTextField = new JTextField(securityEditor.getSecurity().getName());
		currencyTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		currencyTextField.getDocument().addDocumentListener(documentListener);
		currencyTextField.setBackground(new Color(255, 243, 204));
		currencyTextField.setPreferredSize(new Dimension(250, 25));
		currencyTextField.setColumns(10);
		GridBagConstraints gbc_currencyTextField = new GridBagConstraints();
		gbc_currencyTextField.insets = new Insets(0, 0, 5, 50);
		gbc_currencyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_currencyTextField.gridx = 6;
		gbc_currencyTextField.gridy = 8;
		panel.add(currencyTextField, gbc_currencyTextField);

		JLabel putOrCallLabel = new JLabel("Put or Call");
		putOrCallLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_putOrCallLabel = new GridBagConstraints();
		gbc_putOrCallLabel.anchor = GridBagConstraints.WEST;
		gbc_putOrCallLabel.insets = new Insets(0, 0, 5, 5);
		gbc_putOrCallLabel.gridx = 4;
		gbc_putOrCallLabel.gridy = 9;
		panel.add(putOrCallLabel, gbc_putOrCallLabel);

		List<ComboBoxEntry> putOrCallComboBoxEntries = new ArrayList<ComboBoxEntry>();
		putOrCallComboBoxEntries.add(new ComboBoxEntry());
		putOrCallComboBoxEntries.add(new ComboBoxEntry(0, "Put"));
		putOrCallComboBoxEntries.add(new ComboBoxEntry(1, "Call"));

		putOrCallComboBoxComboBox = new JComboBox(putOrCallComboBoxEntries.toArray());
		putOrCallComboBoxComboBox.setMinimumSize(new Dimension(32, 25));
		putOrCallComboBoxComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		putOrCallComboBoxComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_putOrCallComboBoxComboBox = new GridBagConstraints();
		gbc_putOrCallComboBoxComboBox.anchor = GridBagConstraints.NORTH;
		gbc_putOrCallComboBoxComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_putOrCallComboBoxComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_putOrCallComboBoxComboBox.gridx = 6;
		gbc_putOrCallComboBoxComboBox.gridy = 9;
		panel.add(putOrCallComboBoxComboBox, gbc_putOrCallComboBoxComboBox);
		
		putOrCallComboBoxComboBox.addActionListener(actionListener);
		
		JLabel lblIssuer = new JLabel("Issuer");
		lblIssuer.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblIssuer = new GridBagConstraints();
		gbc_lblIssuer.anchor = GridBagConstraints.WEST;
		gbc_lblIssuer.insets = new Insets(0, 0, 5, 5);
		gbc_lblIssuer.gridx = 4;
		gbc_lblIssuer.gridy = 10;
		panel.add(lblIssuer, gbc_lblIssuer);

		issuerTextField = new JTextField();
		issuerTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		issuerTextField.getDocument().addDocumentListener(documentListener);
		issuerTextField.setBackground(new Color(255, 243, 204));
		issuerTextField.setPreferredSize(new Dimension(250, 25));
		issuerTextField.setColumns(10);
		GridBagConstraints gbc_issuerTextField = new GridBagConstraints();
		gbc_issuerTextField.insets = new Insets(0, 0, 5, 50);
		gbc_issuerTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_issuerTextField.gridx = 6;
		gbc_issuerTextField.gridy = 10;
		panel.add(issuerTextField, gbc_issuerTextField);
		issuerTextField.setColumns(10);

		JLabel lblCountryOfIssue = new JLabel("Country of Issue");
		lblCountryOfIssue.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblCountryOfIssue = new GridBagConstraints();
		gbc_lblCountryOfIssue.anchor = GridBagConstraints.WEST;
		gbc_lblCountryOfIssue.insets = new Insets(0, 0, 5, 5);
		gbc_lblCountryOfIssue.gridx = 4;
		gbc_lblCountryOfIssue.gridy = 11;
		panel.add(lblCountryOfIssue, gbc_lblCountryOfIssue);

		List<String> countries = new ArrayList<String>();
		List<String> countryCode = new ArrayList<String>();
		Locale[] localeList = NumberFormat.getAvailableLocales();
		for (Locale locale : localeList)
			if (!countries.contains(locale.getDisplayCountry()))
			{
				countries.add(locale.getDisplayCountry());
				countryCode.add(locale.getCountry());
			}

		List<ComboBoxEntry> countryEntries = new ArrayList<ComboBoxEntry>();
		
		for(int i = 0; i<countries.size();i++)
			countryEntries.add(new ComboBoxEntry(countryCode.get(i),countries.get(i)));
		
		Comparator<ComboBoxEntry> comparator = new Comparator<ComboBoxEntry>() {

			@Override
			public int compare(ComboBoxEntry o1, ComboBoxEntry o2) {

				return o1.toString().compareTo(o2.toString());
			}
		};
		
		Collections.sort(countryEntries, comparator);

		countryEntries.add(0, new ComboBoxEntry());
		
		countryComboBox = new JComboBox(countryEntries.toArray());
		countryComboBox.setPreferredSize(new Dimension(175, 25));
		countryComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 50);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 6;
		gbc_comboBox.gridy = 11;
		panel.add(countryComboBox, gbc_comboBox);
		
		countryComboBox.addActionListener(actionListener);

		JLabel lblStateOfIssue = new JLabel("State of Issue");
		lblStateOfIssue.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblStateOfIssue = new GridBagConstraints();
		gbc_lblStateOfIssue.anchor = GridBagConstraints.WEST;
		gbc_lblStateOfIssue.insets = new Insets(0, 0, 5, 5);
		gbc_lblStateOfIssue.gridx = 4;
		gbc_lblStateOfIssue.gridy = 12;
		panel.add(lblStateOfIssue, gbc_lblStateOfIssue);

		stateOfIssueTextField = new JTextField();
		stateOfIssueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		stateOfIssueTextField.getDocument().addDocumentListener(documentListener);
		stateOfIssueTextField.setBackground(new Color(255, 243, 204));
		stateOfIssueTextField.setPreferredSize(new Dimension(250, 25));
		stateOfIssueTextField.setColumns(10);
		GridBagConstraints gbc_stateOfIssueTextField = new GridBagConstraints();
		gbc_stateOfIssueTextField.insets = new Insets(0, 0, 5, 50);
		gbc_stateOfIssueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_stateOfIssueTextField.gridx = 6;
		gbc_stateOfIssueTextField.gridy = 12;
		panel.add(stateOfIssueTextField, gbc_stateOfIssueTextField);

		JLabel lblLocaleOfIssue = new JLabel("Locale of Issue");
		lblLocaleOfIssue.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblLocaleOfIssue = new GridBagConstraints();
		gbc_lblLocaleOfIssue.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblLocaleOfIssue.insets = new Insets(3, 0, 50, 5);
		gbc_lblLocaleOfIssue.gridx = 4;
		gbc_lblLocaleOfIssue.gridy = 13;
		panel.add(lblLocaleOfIssue, gbc_lblLocaleOfIssue);

		localeOfIssueTextField = new JTextField();
		localeOfIssueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		localeOfIssueTextField.getDocument().addDocumentListener(documentListener);
		localeOfIssueTextField.setBackground(new Color(255, 243, 204));
		localeOfIssueTextField.setPreferredSize(new Dimension(250, 25));
		localeOfIssueTextField.setColumns(10);
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(0, 0, 0, 50);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 6;
		gbc_localeOfissueTextField.gridy = 13;
		panel.add(localeOfIssueTextField, gbc_localeOfissueTextField);

		updateContent();
	}

	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}
	
	private boolean dirtyFieldCheck(Date value, JDateChooser jDateChooser, JLabel warningLabel) {
		JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) jDateChooser.getDateEditor();
		Date value2 = null;
		if (jTextFieldDateEditor.getText().length()>0) {
			try {
				value2 = simpleDateFormat.parse(jTextFieldDateEditor.getText());
			}
			catch (ParseException e) {
				
				warningLabel.setToolTipText("Date is invalid.");
				warningLabel.setIcon(securityEditor.getBugIcon());
				
				dirty = true;
				return false;
			}
		}

		warningLabel.setToolTipText(null);
		warningLabel.setIcon(null);
		
		if (value != null && value2 == null)
			dirty = true;

		else if (value == null && value2 != null)
			dirty = true;
			
		else if (value == null && value2 == null)
			return true;

		else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(value2);
			
			if (calendar.get(Calendar.DAY_OF_MONTH) != calendar2.get(Calendar.DAY_OF_MONTH) || calendar.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)
					|| calendar.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR))
				dirty = true;
		}
		return true;
	}
	
	private boolean dirtyTimeFieldCheck(Date value, JDateChooser jDateChooser, JLabel warningLabel) {
		JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) jDateChooser.getDateEditor();
		Date value2 = null;
		if (jTextFieldDateEditor.getText().length()>0) {
			try {
				value2 = simpleTimeFormat.parse(jTextFieldDateEditor.getText());
			}
			catch (ParseException e) {
				
				warningLabel.setToolTipText("Time is invalid.");
				warningLabel.setIcon(securityEditor.getBugIcon());
				
				dirty = true;
				return false;
			}
		}
		
		warningLabel.setToolTipText(null);
		warningLabel.setIcon(null);
		
		if (value != null && value2 == null)
			dirty = true;

		else if (value == null && value2 != null)
			dirty = true;
			
		else if (value == null && value2 == null)
			return true;

		else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(value2);
			
			if (calendar.get(Calendar.HOUR_OF_DAY) != calendar2.get(Calendar.HOUR_OF_DAY) || calendar.get(Calendar.MINUTE) != calendar2.get(Calendar.MINUTE)
					|| calendar.get(Calendar.SECOND) != calendar2.get(Calendar.SECOND))
				dirty = true;
		}
		return true;
	}

	private void checkConsistency() {

		if (update)
			return;

		consistent = true;
		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Symbol is empty");
			nameWarningLabel.setIcon(securityEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (securityEditor.getSecurity().getName() == null || !securityEditor.getSecurity().getName().equals(nameTextField.getText())) {

				securityEditor.checkName(nameTextField.getText());

				dirty = true;
			}
		}

		consistent = consistent && valid;

		if (securityEditor.getSecurity().getSecurityDetails().getSymbolSfx() != null && securityEditor.getSecurity().getSecurityDetails().getSymbolSfx().trim().length() > 0
				&& !securityEditor.getSecurity().getSecurityDetails().getSymbolSfx().equals(((ComboBoxEntry) additionalInformationComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if ((securityEditor.getSecurity().getSecurityDetails().getSymbolSfx() == null || securityEditor.getSecurity().getSecurityDetails().getSymbolSfx().trim().length() == 0)
				&& additionalInformationComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityIdentifierTextField.getText().trim().length() == 0) {

			securityIdWarningLabel.setToolTipText("Security identifier is empty");
			securityIdWarningLabel.setIcon(securityEditor.getBugIcon());

			valid = false;
		}
		else {

			securityIdWarningLabel.setToolTipText(null);
			securityIdWarningLabel.setIcon(null);

			if (securityEditor.getSecurity().getSecurityID() == null || !securityEditor.getSecurity().getSecurityID().equals(securityIdentifierTextField.getText())) {

				securityEditor.checkSecurityID(securityIdentifierTextField.getText());

				dirty = true;
			}
		}

		consistent = consistent && valid;

		
		
		if (securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource() != null && securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource().trim().length() > 0
				&& !securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource().equals(((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if ((securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource() == null || securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource().trim().length() == 0)
				&& securityIdentifierSourceComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getProduct() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getProduct().equals(((ComboBoxEntry) productComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getProduct() == null && productComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getcFICode(), cfiTextField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSecurityType() != null && securityEditor.getSecurity().getSecurityDetails().getSecurityType().trim().length() > 0
				&& !securityEditor.getSecurity().getSecurityDetails().getSecurityType().equals(((ComboBoxEntry) securityTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if ((securityEditor.getSecurity().getSecurityDetails().getSecurityType() == null || securityEditor.getSecurity().getSecurityDetails().getSecurityType().trim().length() == 0)
				&& securityTypeComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getSecuritySubType(), securitySubTypeTextField))
			dirty = true;

		if (securityAltIDTableModel.isDirty())
			dirty = true;

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getMaturity(), maturityDateChooser, maturityDateWarningLabel);
		
		consistent = consistent && dirtyTimeFieldCheck(securityEditor.getSecurity().getSecurityDetails().getMaturityTime(), maturityTimeDateChooser, maturityTimeWarningLabel);

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCouponPaymentDate(), couponPaymentDateChooser, couponPaymentDateWarningLabel);

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getIssueDate(), issueDateChooser, issueDateWarningLabel);

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getRedemptionDate(), redemptionDateChooser, redemptionDateWarningLabel);

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getInterestAccrualDate(), interestAccrualDateChooser, interestAccrualDateWarningLabel);

		consistent = consistent && dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getDatedDate(), datedDateChooser, datedDateWarningLabel);
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCurrency(), currencyTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getIssuer(), issuerTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStateOfIssue(), stateOfIssueTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getLocaleOfIssue(), localeOfIssueTextField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getCountryOfIssue() != null&& !securityEditor.getSecurity().getSecurityDetails().getCountryOfIssue().equals(((ComboBoxEntry) countryComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityEditor.getSecurity().getSecurityDetails().getCountryOfIssue() == null || securityEditor.getSecurity().getSecurityDetails().getCountryOfIssue().trim().length() == 0)
				&& countryComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getPutOrCall() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getPutOrCall().equals(((ComboBoxEntry) putOrCallComboBoxComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getPutOrCall() == null && putOrCallComboBoxComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		securityEditor.checkDirty();
	}

	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().setName(nameTextField.getText().trim());
		
		securityEditor.getSecurity().getSecurityDetails().setSymbolSfx((String) ((ComboBoxEntry) additionalInformationComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().setSecurityID(securityIdentifierTextField.getText().trim());
		
		securityEditor.getSecurity().getSecurityDetails().setSecurityIDSource((String) ((ComboBoxEntry) securityIdentifierSourceComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setProduct((Integer) ((ComboBoxEntry) productComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setcFICode(cfiTextField.getText().trim());
		
		securityEditor.getSecurity().getSecurityDetails().setSecurityType((String) ((ComboBoxEntry) securityTypeComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setSecuritySubType(securitySubTypeTextField.getText().trim());
		
		securityAltIDTableModel.save();
		
		securityEditor.getSecurity().getSecurityDetails().setCurrency(currencyTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setIssuer(issuerTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setStateOfIssue(stateOfIssueTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setLocaleOfIssue(localeOfIssueTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setCountryOfIssue((String) ((ComboBoxEntry) countryComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().setMaturity(maturityDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setCouponPaymentDate(couponPaymentDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setIssueDate(issueDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setRedemptionDate(redemptionDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setInterestAccrualDate(interestAccrualDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setDatedDate(datedDateChooser.getDate());
		
		securityEditor.getSecurity().getSecurityDetails().setPutOrCall((Integer) ((ComboBoxEntry) putOrCallComboBoxComboBox.getSelectedItem()).getEntry());
		
		try {
			Date date = simpleTimeFormat.parse(((JTextFieldDateEditor)maturityTimeDateChooser.getDateEditor()).getText());
			securityEditor.getSecurity().getSecurityDetails().setMaturityTime(date);
		}
		catch (ParseException e) {
		}
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
	 * Update content.
	 */
	public void updateContent() {

		update = true;

		nameTextField.setText(securityEditor.getSecurity().getName());

		if (securityEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(securityEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(securityEditor.getAbstractBusinessObject().getModificationDate()));

			if (securityEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(securityEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		additionalInformationComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSymbolSfx(), null));

		securityIdentifierTextField.setText(securityEditor.getSecurity().getSecurityID());

		securityIdentifierSourceComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSecurityIDSource(), null));

		productComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getProduct(), null));

		cfiTextField.setText(securityEditor.getSecurity().getSecurityDetails().getcFICode());

		securityTypeComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSecurityType(), null));

		securitySubTypeTextField.setText(securityEditor.getSecurity().getSecurityDetails().getSecuritySubType());

		securityAltIDTableModel.updateSecurityAltIDGroups(securityEditor.getSecurity().getSecurityDetails().getSecurityAltIDGroups());
		
		currencyTextField.setText(securityEditor.getSecurity().getSecurityDetails().getCurrency());

		issuerTextField.setText(securityEditor.getSecurity().getSecurityDetails().getIssuer());

		stateOfIssueTextField.setText(securityEditor.getSecurity().getSecurityDetails().getStateOfIssue());

		localeOfIssueTextField.setText(securityEditor.getSecurity().getSecurityDetails().getLocaleOfIssue());

		countryComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getCountryOfIssue(),null));

		maturityDateChooser.setDate(securityEditor.getSecurity().getMaturity());

		couponPaymentDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getCouponPaymentDate());

		issueDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getIssueDate());

		redemptionDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getRedemptionDate());

		interestAccrualDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getInterestAccrualDate());

		datedDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getDatedDate());
		
		maturityTimeDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getMaturityTime());
		
		putOrCallComboBoxComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getPutOrCall(), null));

		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);
			additionalInformationComboBox.setBackground(new Color(255, 243, 204));
			additionalInformationComboBox.setEnabled(true);
			securityIdentifierTextField.setBackground(new Color(255, 243, 204));
			securityIdentifierTextField.setEditable(true);
			securityIdentifierSourceComboBox.setBackground(new Color(255, 243, 204));
			securityIdentifierSourceComboBox.setEnabled(true);
			productComboBox.setBackground(new Color(255, 243, 204));
			productComboBox.setEnabled(true);
			cfiTextField.setBackground(new Color(255, 243, 204));
			cfiTextField.setEditable(true);
			securityTypeComboBox.setBackground(new Color(255, 243, 204));
			securityTypeComboBox.setEnabled(true);
			securitySubTypeTextField.setBackground(new Color(255, 243, 204));
			securitySubTypeTextField.setEditable(true);
			scrollPane.getViewport().setBackground(new Color(255, 243, 204));
			addButton.setEnabled(true);
			removeButton.setEnabled(false);
			
			currencyTextField.setBackground(new Color(255, 243, 204));
			currencyTextField.setEditable(true);

			issuerTextField.setBackground(new Color(255, 243, 204));
			issuerTextField.setEditable(true);

			stateOfIssueTextField.setBackground(new Color(255, 243, 204));
			stateOfIssueTextField.setEditable(true);

			localeOfIssueTextField.setBackground(new Color(255, 243, 204));
			localeOfIssueTextField.setEditable(true);

			countryComboBox.setBackground(new Color(255, 243, 204));
			countryComboBox.setEnabled(true);

			((JTextFieldDateEditor) maturityDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			maturityDateChooser.setEnabled(true);

			((JTextFieldDateEditor) couponPaymentDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			couponPaymentDateChooser.setEnabled(true);

			((JTextFieldDateEditor) issueDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			issueDateChooser.setEnabled(true);

			((JTextFieldDateEditor) redemptionDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			redemptionDateChooser.setEnabled(true);

			((JTextFieldDateEditor) interestAccrualDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			interestAccrualDateChooser.setEnabled(true);

			((JTextFieldDateEditor) datedDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			datedDateChooser.setEnabled(true);
			
			((JTextFieldDateEditor) maturityTimeDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			maturityTimeDateChooser.setEnabled(true);
			
			putOrCallComboBoxComboBox.setBackground(new Color(255, 243, 204));
			putOrCallComboBoxComboBox.setEnabled(true);
		}
		else {

			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);
			additionalInformationComboBox.setBackground(new Color(204, 216, 255));
			additionalInformationComboBox.setEnabled(false);
			securityIdentifierTextField.setBackground(new Color(204, 216, 255));
			securityIdentifierTextField.setEditable(false);
			securityIdentifierSourceComboBox.setBackground(new Color(204, 216, 255));
			securityIdentifierSourceComboBox.setEnabled(false);
			productComboBox.setBackground(new Color(204, 216, 255));
			productComboBox.setEnabled(false);
			cfiTextField.setBackground(new Color(204, 216, 255));
			cfiTextField.setEditable(false);
			securityTypeComboBox.setBackground(new Color(204, 216, 255));
			securityTypeComboBox.setEnabled(false);
			securitySubTypeTextField.setBackground(new Color(204, 216, 255));
			securitySubTypeTextField.setEditable(false);
			scrollPane.getViewport().setBackground(new Color(204, 216, 255));
			addButton.setEnabled(false);
			removeButton.setEnabled(false);
			
			currencyTextField.setBackground(new Color(204, 216, 255));
			currencyTextField.setEditable(false);

			issuerTextField.setBackground(new Color(204, 216, 255));
			issuerTextField.setEditable(false);

			stateOfIssueTextField.setBackground(new Color(204, 216, 255));
			stateOfIssueTextField.setEditable(false);

			localeOfIssueTextField.setBackground(new Color(204, 216, 255));
			localeOfIssueTextField.setEditable(false);

			countryComboBox.setBackground(new Color(204, 216, 255));
			countryComboBox.setEnabled(false);

			((JTextFieldDateEditor) maturityDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			maturityDateChooser.setEnabled(false);

			((JTextFieldDateEditor) couponPaymentDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			couponPaymentDateChooser.setEnabled(false);

			((JTextFieldDateEditor) issueDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			issueDateChooser.setEnabled(false);

			((JTextFieldDateEditor) redemptionDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			redemptionDateChooser.setEnabled(false);

			((JTextFieldDateEditor) interestAccrualDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			interestAccrualDateChooser.setEnabled(false);

			((JTextFieldDateEditor) datedDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			datedDateChooser.setEnabled(false);
			
			((JTextFieldDateEditor) maturityTimeDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			maturityTimeDateChooser.setEnabled(false);
			
			putOrCallComboBoxComboBox.setBackground(new Color(204, 216, 255));
			putOrCallComboBoxComboBox.setEnabled(false);
		}

		update = false;

		checkConsistency();
	}

}
