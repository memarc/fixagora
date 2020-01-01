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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;
import net.sourceforge.fixagora.basis.client.view.document.IntegerDocument;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

/**
 * The Class FSecurityEditorDetails2.
 */
public class FSecurityEditorDetails2 extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JComboBox securityExchangeComboBox = null;

	private boolean dirty = false;

	private FSecurityEditor securityEditor = null;

	private boolean update = false;
	private JTextField poolTextField;
	private JDateChooser contractSettlementDateChooser;
	private JTextField cpRegTypeTextField;
	private JComboBox cpProgramComboBox;
	private JComboBox securityStatusComboBox;
	private final DecimalFormat integerFormat = new DecimalFormat("##0");
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private JTextField ntPositionLimitTextField;

	private JTextField positionLimitTextField;

	private JComboBox instrumentAssignementMethodComboBox;

	private JComboBox settleOnOpenComboBox;

	private boolean consistent;

	private JLabel contractSettlementWarningLabel;

	private JTextField securityGroupTextField;

	private JComboBox underlyingPriceDeterminationMethodComboBox;

	private JComboBox deliveryFormComboBox;

	private JTextField percentAtRiskTextField;

	private JTextField productComplexTextField;

	private JComboBox settlementMethodComboBox;

	private JComboBox exerciseStyleComboBox;

	private JTextField optionPayoutAmountTextField;

	private JComboBox optionPayoutTypeComboBox;

	private JComboBox priceQuoteMethodComboBox;

	private JComboBox listMethodComboBox;

	private JComboBox flexibleIndicatorComboBox;

	private JComboBox flexProductEligibilityIndicatorComboBox;

	private JComboBox valuationMethodComboBox;

	private JComboBox flowScheduleTypeComboBox;

	private JComboBox restructuringTypeComboBox;

	private JComboBox seniorityComboBox;

	/**
	 * Instantiates a new f security editor details2.
	 *
	 * @param securityEditor the security editor
	 */
	public FSecurityEditorDetails2(final FSecurityEditor securityEditor) {

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

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 1.0 };
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 370, 0, 370, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0,0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 8;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel securityExchangeLabel = new JLabel("Security Exchange");
		securityExchangeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityExchangeLabel = new GridBagConstraints();
		gbc_securityExchangeLabel.anchor = GridBagConstraints.WEST;
		gbc_securityExchangeLabel.insets = new Insets(50, 50, 5, 10);
		gbc_securityExchangeLabel.gridx = 1;
		gbc_securityExchangeLabel.gridy = 0;
		panel.add(securityExchangeLabel, gbc_securityExchangeLabel);

		ExchangeComboBoxEntries exchangeComboBoxEntries = new ExchangeComboBoxEntries();

		securityExchangeComboBox = new JComboBox(exchangeComboBoxEntries.getComboBoxEntries());
		securityExchangeComboBox.setMinimumSize(new Dimension(32, 25));
		securityExchangeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		securityExchangeComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_creditRatingTextField = new GridBagConstraints();
		gbc_creditRatingTextField.insets = new Insets(50, 0, 5, 50);
		gbc_creditRatingTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_creditRatingTextField.gridx = 3;
		gbc_creditRatingTextField.gridy = 0;
		panel.add(securityExchangeComboBox, gbc_creditRatingTextField);
		
		securityExchangeComboBox.addActionListener(actionListener);

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.insets = new Insets(0, 0, 5, 0);
		gbc_rightFillPanel.gridheight = 8;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 6;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JLabel securityGroupLabel = new JLabel("Security Group");
		securityGroupLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityGroupLabel = new GridBagConstraints();
		gbc_securityGroupLabel.anchor = GridBagConstraints.WEST;
		gbc_securityGroupLabel.insets = new Insets(0, 50, 5, 25);
		gbc_securityGroupLabel.gridx = 1;
		gbc_securityGroupLabel.gridy = 1;
		panel.add(securityGroupLabel, gbc_securityGroupLabel);

		securityGroupTextField = new JTextField();
		securityGroupTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		securityGroupTextField.getDocument().addDocumentListener(documentListener);
		securityGroupTextField.setBackground(new Color(255, 243, 204));
		securityGroupTextField.setPreferredSize(new Dimension(250, 25));
		securityGroupTextField.setColumns(10);
		GridBagConstraints gbc_securityGroupTextField = new GridBagConstraints();
		gbc_securityGroupTextField.insets = new Insets(0, 0, 5, 50);
		gbc_securityGroupTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityGroupTextField.gridx = 3;
		gbc_securityGroupTextField.gridy = 1;
		panel.add(securityGroupTextField, gbc_securityGroupTextField);
		
		JLabel poolLabel = new JLabel("Pool");
		poolLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_poolLabel = new GridBagConstraints();
		gbc_poolLabel.anchor = GridBagConstraints.WEST;
		gbc_poolLabel.insets = new Insets(0, 50, 5, 25);
		gbc_poolLabel.gridx = 1;
		gbc_poolLabel.gridy = 2;
		panel.add(poolLabel, gbc_poolLabel);

		poolTextField = new JTextField();
		poolTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		poolTextField.getDocument().addDocumentListener(documentListener);
		poolTextField.setBackground(new Color(255, 243, 204));
		poolTextField.setPreferredSize(new Dimension(250, 25));
		poolTextField.setColumns(10);
		GridBagConstraints gbc_instrumentRegistryTextField = new GridBagConstraints();
		gbc_instrumentRegistryTextField.insets = new Insets(0, 0, 5, 50);
		gbc_instrumentRegistryTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_instrumentRegistryTextField.gridx = 3;
		gbc_instrumentRegistryTextField.gridy = 2;
		panel.add(poolTextField, gbc_instrumentRegistryTextField);

		JLabel contractSettlementLabel = new JLabel("Contract Settlement");
		contractSettlementLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_contractSettlementLabel = new GridBagConstraints();
		gbc_contractSettlementLabel.anchor = GridBagConstraints.WEST;
		gbc_contractSettlementLabel.insets = new Insets(0, 50, 5, 5);
		gbc_contractSettlementLabel.gridx = 1;
		gbc_contractSettlementLabel.gridy = 3;
		panel.add(contractSettlementLabel, gbc_contractSettlementLabel);

		contractSettlementWarningLabel = new JLabel("");
		contractSettlementWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_maturityTimeWarningLabel = new GridBagConstraints();
		gbc_maturityTimeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maturityTimeWarningLabel.gridx = 2;
		gbc_maturityTimeWarningLabel.gridy = 3;
		panel.add(contractSettlementWarningLabel, gbc_maturityTimeWarningLabel);

		contractSettlementDateChooser = new JDateChooser();
		contractSettlementDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor) contractSettlementDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		contractSettlementDateChooser.setOpaque(false);
		contractSettlementDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) contractSettlementDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5,
				new Color(204, 216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		GridBagConstraints gbc_contractSettlementTextField = new GridBagConstraints();
		gbc_contractSettlementTextField.insets = new Insets(0, 0, 5, 50);
		gbc_contractSettlementTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_contractSettlementTextField.gridx = 3;
		gbc_contractSettlementTextField.gridy = 3;
		panel.add(contractSettlementDateChooser, gbc_contractSettlementTextField);

		JLabel cpProgramLabel = new JLabel("Commercial Paper Registration Type");
		cpProgramLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikeCurrencyLabel = new GridBagConstraints();
		gbc_strikeCurrencyLabel.anchor = GridBagConstraints.WEST;
		gbc_strikeCurrencyLabel.insets = new Insets(0, 50, 5, 5);
		gbc_strikeCurrencyLabel.gridx = 1;
		gbc_strikeCurrencyLabel.gridy = 4;
		panel.add(cpProgramLabel, gbc_strikeCurrencyLabel);

		cpRegTypeTextField = new JTextField();
		cpRegTypeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		cpRegTypeTextField.getDocument().addDocumentListener(documentListener);
		cpRegTypeTextField.setBackground(new Color(255, 243, 204));
		cpRegTypeTextField.setPreferredSize(new Dimension(250, 25));
		cpRegTypeTextField.setColumns(10);
		GridBagConstraints gbc_strikeCurrencyTextField = new GridBagConstraints();
		gbc_strikeCurrencyTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikeCurrencyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikeCurrencyTextField.gridx = 3;
		gbc_strikeCurrencyTextField.gridy = 4;
		panel.add(cpRegTypeTextField, gbc_strikeCurrencyTextField);

		JLabel cpRegTypeLabel = new JLabel("Commercial Paper Programm");
		cpRegTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_cpRegTypeLabel = new GridBagConstraints();
		gbc_cpRegTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_cpRegTypeLabel.insets = new Insets(0, 50, 5, 5);
		gbc_cpRegTypeLabel.gridx = 1;
		gbc_cpRegTypeLabel.gridy = 5;
		panel.add(cpRegTypeLabel, gbc_cpRegTypeLabel);

		List<ComboBoxEntry> cpRegTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
		cpRegTypeComboBoxEntries.add(new ComboBoxEntry());
		cpRegTypeComboBoxEntries.add(new ComboBoxEntry(1, "3(a)(3)"));
		cpRegTypeComboBoxEntries.add(new ComboBoxEntry(2, "4(2)"));
		cpRegTypeComboBoxEntries.add(new ComboBoxEntry(99, "Other"));

		cpProgramComboBox = new JComboBox(cpRegTypeComboBoxEntries.toArray());
		cpProgramComboBox.setMinimumSize(new Dimension(32, 25));
		cpProgramComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		cpProgramComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_cpRegTypeComboBox = new GridBagConstraints();
		gbc_cpRegTypeComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_cpRegTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_cpRegTypeComboBox.gridx = 3;
		gbc_cpRegTypeComboBox.gridy = 5;
		panel.add(cpProgramComboBox, gbc_cpRegTypeComboBox);

		cpProgramComboBox.addActionListener(actionListener);
		
		JLabel securityStatusLabel = new JLabel("Security Status");
		securityStatusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityStatusLabel = new GridBagConstraints();
		gbc_securityStatusLabel.anchor = GridBagConstraints.WEST;
		gbc_securityStatusLabel.insets = new Insets(0, 50, 5, 5);
		gbc_securityStatusLabel.gridx = 1;
		gbc_securityStatusLabel.gridy = 6;
		panel.add(securityStatusLabel, gbc_securityStatusLabel);

		List<ComboBoxEntry> securityStatusComboBoxEntries = new ArrayList<ComboBoxEntry>();
		securityStatusComboBoxEntries.add(new ComboBoxEntry());
		securityStatusComboBoxEntries.add(new ComboBoxEntry("1", "Active"));
		securityStatusComboBoxEntries.add(new ComboBoxEntry("2", "Inactive"));

		securityStatusComboBox = new JComboBox(securityStatusComboBoxEntries.toArray());
		securityStatusComboBox.setMinimumSize(new Dimension(32, 25));
		securityStatusComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		securityStatusComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_securityStatusComboBox = new GridBagConstraints();
		gbc_securityStatusComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_securityStatusComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityStatusComboBox.gridx = 3;
		gbc_securityStatusComboBox.gridy = 6;
		panel.add(securityStatusComboBox, gbc_securityStatusComboBox);
		
		securityStatusComboBox.addActionListener(actionListener);

		JLabel settleOnOpenLabel = new JLabel("Settle on Open");
		settleOnOpenLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_settleOnOpenLabel = new GridBagConstraints();
		gbc_settleOnOpenLabel.anchor = GridBagConstraints.WEST;
		gbc_settleOnOpenLabel.insets = new Insets(0, 50, 5, 5);
		gbc_settleOnOpenLabel.gridx = 1;
		gbc_settleOnOpenLabel.gridy = 7;
		panel.add(settleOnOpenLabel, gbc_settleOnOpenLabel);

		List<ComboBoxEntry> settleOnOpenComboBoxEntries = new ArrayList<ComboBoxEntry>();
		settleOnOpenComboBoxEntries.add(new ComboBoxEntry());
		settleOnOpenComboBoxEntries.add(new ComboBoxEntry("Y", "Yes"));
		settleOnOpenComboBoxEntries.add(new ComboBoxEntry("N", "No"));

		settleOnOpenComboBox = new JComboBox(settleOnOpenComboBoxEntries.toArray());
		settleOnOpenComboBox.setMinimumSize(new Dimension(32, 25));
		settleOnOpenComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		settleOnOpenComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_settleOnOpenTextField = new GridBagConstraints();
		gbc_settleOnOpenTextField.insets = new Insets(0, 0, 5, 50);
		gbc_settleOnOpenTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_settleOnOpenTextField.gridx = 3;
		gbc_settleOnOpenTextField.gridy = 7;
		panel.add(settleOnOpenComboBox, gbc_settleOnOpenTextField);
		
		settleOnOpenComboBox.addActionListener(actionListener);

		JLabel instrumentAssignementMethodLabel = new JLabel("Instrument Assignment Method");
		instrumentAssignementMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_instrumentAssignementMethodLabel = new GridBagConstraints();
		gbc_instrumentAssignementMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_instrumentAssignementMethodLabel.insets = new Insets(0, 50, 5, 5);
		gbc_instrumentAssignementMethodLabel.gridx = 1;
		gbc_instrumentAssignementMethodLabel.gridy = 8;
		panel.add(instrumentAssignementMethodLabel, gbc_instrumentAssignementMethodLabel);

		List<ComboBoxEntry> instrumentAssignementMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		instrumentAssignementMethodComboBoxEntries.add(new ComboBoxEntry());
		instrumentAssignementMethodComboBoxEntries.add(new ComboBoxEntry("P", "Pro rata"));
		instrumentAssignementMethodComboBoxEntries.add(new ComboBoxEntry("R", "Random"));

		instrumentAssignementMethodComboBox = new JComboBox(instrumentAssignementMethodComboBoxEntries.toArray());
		instrumentAssignementMethodComboBox.setMinimumSize(new Dimension(32, 25));
		instrumentAssignementMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		instrumentAssignementMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_instrumentAssignementMethodTextField = new GridBagConstraints();
		gbc_instrumentAssignementMethodTextField.insets = new Insets(0, 0, 5, 50);
		gbc_instrumentAssignementMethodTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_instrumentAssignementMethodTextField.gridx = 3;
		gbc_instrumentAssignementMethodTextField.gridy = 8;
		panel.add(instrumentAssignementMethodComboBox, gbc_instrumentAssignementMethodTextField);
		
		instrumentAssignementMethodComboBox.addActionListener(actionListener);

		JLabel positionLimitLabel = new JLabel("Position Limit");
		positionLimitLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_positionLimitLabel = new GridBagConstraints();
		gbc_positionLimitLabel.anchor = GridBagConstraints.WEST;
		gbc_positionLimitLabel.insets = new Insets(0, 50, 5, 5);
		gbc_positionLimitLabel.gridx = 1;
		gbc_positionLimitLabel.gridy = 9;
		panel.add(positionLimitLabel, gbc_positionLimitLabel);

		positionLimitTextField = new JTextField();
		positionLimitTextField.setDocument(new IntegerDocument());
		positionLimitTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		positionLimitTextField.getDocument().addDocumentListener(documentListener);
		positionLimitTextField.setBackground(new Color(255, 243, 204));
		positionLimitTextField.setPreferredSize(new Dimension(250, 25));
		positionLimitTextField.setColumns(10);
		GridBagConstraints gbc_positionLimitTextField = new GridBagConstraints();
		gbc_positionLimitTextField.insets = new Insets(0, 0, 5, 50);
		gbc_positionLimitTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_positionLimitTextField.gridx = 3;
		gbc_positionLimitTextField.gridy = 9;
		panel.add(positionLimitTextField, gbc_positionLimitTextField);

		JLabel ntPositionLimitLabel = new JLabel("Near-Term Position Limit");
		ntPositionLimitLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_ntPositionLimitLabel = new GridBagConstraints();
		gbc_ntPositionLimitLabel.anchor = GridBagConstraints.WEST;
		gbc_ntPositionLimitLabel.insets = new Insets(0, 50, 5, 5);
		gbc_ntPositionLimitLabel.gridx = 1;
		gbc_ntPositionLimitLabel.gridy = 10;
		panel.add(ntPositionLimitLabel, gbc_ntPositionLimitLabel);

		ntPositionLimitTextField = new JTextField();
		ntPositionLimitTextField.setDocument(new IntegerDocument());
		ntPositionLimitTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		ntPositionLimitTextField.getDocument().addDocumentListener(documentListener);
		ntPositionLimitTextField.setBackground(new Color(255, 243, 204));
		ntPositionLimitTextField.setPreferredSize(new Dimension(250, 25));
		ntPositionLimitTextField.setColumns(10);
		GridBagConstraints gbc_ntPositionLimitTextField = new GridBagConstraints();
		gbc_ntPositionLimitTextField.anchor = GridBagConstraints.NORTH;
		gbc_ntPositionLimitTextField.insets = new Insets(0, 0, 5, 50);
		gbc_ntPositionLimitTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ntPositionLimitTextField.gridx = 3;
		gbc_ntPositionLimitTextField.gridy = 10;
		panel.add(ntPositionLimitTextField, gbc_ntPositionLimitTextField);
		
		JLabel underlyingPriceDeterminationMethodLabel = new JLabel("Underlying Price Determination Method");
		underlyingPriceDeterminationMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_underlyingPriceDeterminationMethodLabel = new GridBagConstraints();
		gbc_underlyingPriceDeterminationMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_underlyingPriceDeterminationMethodLabel.insets = new Insets(0, 50, 5, 5);
		gbc_underlyingPriceDeterminationMethodLabel.gridx = 1;
		gbc_underlyingPriceDeterminationMethodLabel.gridy = 11;
		panel.add(underlyingPriceDeterminationMethodLabel, gbc_underlyingPriceDeterminationMethodLabel);

		List<ComboBoxEntry> underlyingPriceDeterminationMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		underlyingPriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry());
		underlyingPriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(1, "Regular"));
		underlyingPriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(2, "Special reference"));
		underlyingPriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(3, "Optimal value"));
		underlyingPriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(4, "Average value"));

		underlyingPriceDeterminationMethodComboBox = new JComboBox(underlyingPriceDeterminationMethodComboBoxEntries.toArray());
		underlyingPriceDeterminationMethodComboBox.setMinimumSize(new Dimension(32, 25));
		underlyingPriceDeterminationMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		underlyingPriceDeterminationMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_underlyingPriceDeterminationMethodComboBox = new GridBagConstraints();
		gbc_underlyingPriceDeterminationMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_underlyingPriceDeterminationMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_underlyingPriceDeterminationMethodComboBox.gridx = 3;
		gbc_underlyingPriceDeterminationMethodComboBox.gridy = 11;
		panel.add(underlyingPriceDeterminationMethodComboBox, gbc_underlyingPriceDeterminationMethodComboBox);
		
		underlyingPriceDeterminationMethodComboBox.addActionListener(actionListener);

		JLabel deliveryFormLabel = new JLabel("Delivery Form");
		deliveryFormLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_deliveryFormLabel = new GridBagConstraints();
		gbc_deliveryFormLabel.anchor = GridBagConstraints.WEST;
		gbc_deliveryFormLabel.insets = new Insets(0, 50, 5, 5);
		gbc_deliveryFormLabel.gridx = 1;
		gbc_deliveryFormLabel.gridy = 12;
		panel.add(deliveryFormLabel, gbc_deliveryFormLabel);
		
		List<ComboBoxEntry> deliveryFormComboBoxEntries = new ArrayList<ComboBoxEntry>();
		deliveryFormComboBoxEntries.add(new ComboBoxEntry());
		deliveryFormComboBoxEntries.add(new ComboBoxEntry(1, "Book Entry"));
		deliveryFormComboBoxEntries.add(new ComboBoxEntry(2, "Bearer"));

		deliveryFormComboBox = new JComboBox(deliveryFormComboBoxEntries.toArray());
		deliveryFormComboBox.setMinimumSize(new Dimension(32, 25));
		deliveryFormComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		deliveryFormComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_deliveryFormComboBox = new GridBagConstraints();
		gbc_deliveryFormComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_deliveryFormComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_deliveryFormComboBox.gridx = 3;
		gbc_deliveryFormComboBox.gridy = 12;
		panel.add(deliveryFormComboBox, gbc_deliveryFormComboBox);
		
		deliveryFormComboBox.addActionListener(actionListener);
		
		JLabel percentAtRiskLabel = new JLabel("Percent at Risk");
		percentAtRiskLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_percentAtRisk = new GridBagConstraints();
		gbc_percentAtRisk.anchor = GridBagConstraints.NORTHWEST;
		gbc_percentAtRisk.insets = new Insets(3, 50, 50, 5);
		gbc_percentAtRisk.gridx = 1;
		gbc_percentAtRisk.gridy = 13;
		panel.add(percentAtRiskLabel, gbc_percentAtRisk);

		percentAtRiskTextField = new JTextField();
		percentAtRiskTextField.setDocument(new DoubleDocument());
		percentAtRiskTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		percentAtRiskTextField.getDocument().addDocumentListener(documentListener);
		percentAtRiskTextField.setBackground(new Color(255, 243, 204));
		percentAtRiskTextField.setPreferredSize(new Dimension(250, 25));
		percentAtRiskTextField.setColumns(10);
		GridBagConstraints gbc_percentAtRiskTextField = new GridBagConstraints();
		gbc_percentAtRiskTextField.anchor = GridBagConstraints.NORTH;
		gbc_percentAtRiskTextField.insets = new Insets(0, 0, 50, 50);
		gbc_percentAtRiskTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_percentAtRiskTextField.gridx = 3;
		gbc_percentAtRiskTextField.gridy = 13;
		panel.add(percentAtRiskTextField, gbc_percentAtRiskTextField);
		
		//col2
		
		JLabel productComplexLabel = new JLabel("Product Complex");
		productComplexLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_productComplexLabel = new GridBagConstraints();
		gbc_productComplexLabel.anchor = GridBagConstraints.WEST;
		gbc_productComplexLabel.insets = new Insets(50, 0, 5, 25);
		gbc_productComplexLabel.gridx = 4;
		gbc_productComplexLabel.gridy = 0;
		panel.add(productComplexLabel, gbc_productComplexLabel);

		productComplexTextField = new JTextField();
		productComplexTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		productComplexTextField.getDocument().addDocumentListener(documentListener);
		productComplexTextField.setBackground(new Color(255, 243, 204));
		productComplexTextField.setPreferredSize(new Dimension(250, 25));
		productComplexTextField.setColumns(10);
		GridBagConstraints gbc_productComplexTextField = new GridBagConstraints();
		gbc_productComplexTextField.insets = new Insets(50, 0, 5, 50);
		gbc_productComplexTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_productComplexTextField.gridx = 5;
		gbc_productComplexTextField.gridy = 0;
		panel.add(productComplexTextField, gbc_productComplexTextField);
		
		JLabel settlementMethodLabel = new JLabel("Settlement Method");
		settlementMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_settlementMethodLabel = new GridBagConstraints();
		gbc_settlementMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_settlementMethodLabel.insets = new Insets(0, 0, 5, 25);
		gbc_settlementMethodLabel.gridx = 4;
		gbc_settlementMethodLabel.gridy = 1;
		panel.add(settlementMethodLabel, gbc_settlementMethodLabel);

		List<ComboBoxEntry> settlementMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		settlementMethodComboBoxEntries.add(new ComboBoxEntry());
		settlementMethodComboBoxEntries.add(new ComboBoxEntry("C", "Cash settlement required"));
		settlementMethodComboBoxEntries.add(new ComboBoxEntry("P", "Physical settlement required"));

		settlementMethodComboBox = new JComboBox(settlementMethodComboBoxEntries.toArray());
		settlementMethodComboBox.setMinimumSize(new Dimension(32, 25));
		settlementMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		settlementMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_settlementMethodComboBox = new GridBagConstraints();
		gbc_settlementMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_settlementMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settlementMethodComboBox.gridx = 5;
		gbc_settlementMethodComboBox.gridy = 1;
		panel.add(settlementMethodComboBox, gbc_settlementMethodComboBox);
		
		settlementMethodComboBox.addActionListener(actionListener);
		
		JLabel exerciseStyleLabel = new JLabel("Exercise Style");
		exerciseStyleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_exerciseStyleLabel = new GridBagConstraints();
		gbc_exerciseStyleLabel.anchor = GridBagConstraints.WEST;
		gbc_exerciseStyleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_exerciseStyleLabel.gridx = 4;
		gbc_exerciseStyleLabel.gridy = 2;
		panel.add(exerciseStyleLabel, gbc_exerciseStyleLabel);

		List<ComboBoxEntry> exerciseStyleComboBoxEntries = new ArrayList<ComboBoxEntry>();
		exerciseStyleComboBoxEntries.add(new ComboBoxEntry());
		exerciseStyleComboBoxEntries.add(new ComboBoxEntry(0, "European"));
		exerciseStyleComboBoxEntries.add(new ComboBoxEntry(1, "American"));
		exerciseStyleComboBoxEntries.add(new ComboBoxEntry(2, "Bermuda"));

		exerciseStyleComboBox = new JComboBox(exerciseStyleComboBoxEntries.toArray());
		exerciseStyleComboBox.setMinimumSize(new Dimension(32, 25));
		exerciseStyleComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		exerciseStyleComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_exerciseStyleComboBox = new GridBagConstraints();
		gbc_exerciseStyleComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_exerciseStyleComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_exerciseStyleComboBox.gridx = 5;
		gbc_exerciseStyleComboBox.gridy = 2;
		panel.add(exerciseStyleComboBox, gbc_exerciseStyleComboBox);

		exerciseStyleComboBox.addActionListener(actionListener);
		
		JLabel optionPayoutAmountLabel = new JLabel("Option Payout Amount");
		optionPayoutAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_optionPayoutAmountLabel = new GridBagConstraints();
		gbc_optionPayoutAmountLabel.anchor = GridBagConstraints.WEST;
		gbc_optionPayoutAmountLabel.insets = new Insets(0, 0, 5, 5);
		gbc_optionPayoutAmountLabel.gridx = 4;
		gbc_optionPayoutAmountLabel.gridy = 3;
		panel.add(optionPayoutAmountLabel, gbc_optionPayoutAmountLabel);

		optionPayoutAmountTextField = new JTextField();
		optionPayoutAmountTextField.setDocument(new DoubleDocument());
		optionPayoutAmountTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		optionPayoutAmountTextField.getDocument().addDocumentListener(documentListener);
		optionPayoutAmountTextField.setBackground(new Color(255, 243, 204));
		optionPayoutAmountTextField.setPreferredSize(new Dimension(250, 25));
		optionPayoutAmountTextField.setColumns(10);
		GridBagConstraints gbc_optionPayoutAmountTextField = new GridBagConstraints();
		gbc_optionPayoutAmountTextField.insets = new Insets(0, 0, 5, 50);
		gbc_optionPayoutAmountTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_optionPayoutAmountTextField.gridx = 5;
		gbc_optionPayoutAmountTextField.gridy = 3;
		panel.add(optionPayoutAmountTextField, gbc_optionPayoutAmountTextField);
		
		JLabel optionPayoutTypeLabel = new JLabel("Option Payout Type");
		optionPayoutTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_optionPayoutTypeLabel = new GridBagConstraints();
		gbc_optionPayoutTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_optionPayoutTypeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_optionPayoutTypeLabel.gridx = 4;
		gbc_optionPayoutTypeLabel.gridy = 4;
		panel.add(optionPayoutTypeLabel, gbc_optionPayoutTypeLabel);

		List<ComboBoxEntry> optionPayoutTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
		optionPayoutTypeComboBoxEntries.add(new ComboBoxEntry());
		optionPayoutTypeComboBoxEntries.add(new ComboBoxEntry(1, "Vanilla"));
		optionPayoutTypeComboBoxEntries.add(new ComboBoxEntry(2, "Capped"));
		optionPayoutTypeComboBoxEntries.add(new ComboBoxEntry(3, "Binary"));

		optionPayoutTypeComboBox = new JComboBox(optionPayoutTypeComboBoxEntries.toArray());
		optionPayoutTypeComboBox.setMinimumSize(new Dimension(32, 25));
		optionPayoutTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		optionPayoutTypeComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_optionPayoutTypeComboBox = new GridBagConstraints();
		gbc_optionPayoutTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_optionPayoutTypeComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_optionPayoutTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_optionPayoutTypeComboBox.gridx = 5;
		gbc_optionPayoutTypeComboBox.gridy = 4;
		panel.add(optionPayoutTypeComboBox, gbc_optionPayoutTypeComboBox);
		
		optionPayoutTypeComboBox.addActionListener(actionListener);

		
		JLabel priceQuoteMethodLabel = new JLabel("Price Quote Method");
		priceQuoteMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceQuoteMethodLabel = new GridBagConstraints();
		gbc_priceQuoteMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_priceQuoteMethodLabel.insets = new Insets(0, 0, 5, 5);
		gbc_priceQuoteMethodLabel.gridx = 4;
		gbc_priceQuoteMethodLabel.gridy = 5;
		panel.add(priceQuoteMethodLabel, gbc_priceQuoteMethodLabel);

		List<ComboBoxEntry> priceQuoteMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		priceQuoteMethodComboBoxEntries.add(new ComboBoxEntry());
		priceQuoteMethodComboBoxEntries.add(new ComboBoxEntry("STD", "Standard, money per unit of a physical"));
		priceQuoteMethodComboBoxEntries.add(new ComboBoxEntry("INX", "Index"));
		priceQuoteMethodComboBoxEntries.add(new ComboBoxEntry("INT", "Interest rate Index"));
		priceQuoteMethodComboBoxEntries.add(new ComboBoxEntry("PCTPAR", "Percent of Par"));
		
		priceQuoteMethodComboBox = new JComboBox(priceQuoteMethodComboBoxEntries.toArray());
		priceQuoteMethodComboBox.setMinimumSize(new Dimension(32, 25));
		priceQuoteMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		priceQuoteMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_priceQuoteMethodComboBox = new GridBagConstraints();
		gbc_priceQuoteMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_priceQuoteMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceQuoteMethodComboBox.gridx = 5;
		gbc_priceQuoteMethodComboBox.gridy = 5;
		panel.add(priceQuoteMethodComboBox, gbc_priceQuoteMethodComboBox);
		
		priceQuoteMethodComboBox.addActionListener(actionListener);
		
		JLabel listMethodLabel = new JLabel("List Method");
		listMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_listMethodLabel = new GridBagConstraints();
		gbc_listMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_listMethodLabel.insets = new Insets(0, 0, 5, 5);
		gbc_listMethodLabel.gridx = 4;
		gbc_listMethodLabel.gridy = 6;
		panel.add(listMethodLabel, gbc_listMethodLabel);
		
		List<ComboBoxEntry> listMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		listMethodComboBoxEntries.add(new ComboBoxEntry());
		listMethodComboBoxEntries.add(new ComboBoxEntry(0, "pre-listed only"));
		listMethodComboBoxEntries.add(new ComboBoxEntry(1, "user requested"));

		listMethodComboBox = new JComboBox(listMethodComboBoxEntries.toArray());
		listMethodComboBox.setMinimumSize(new Dimension(32, 25));
		listMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		listMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_listMethodComboBox = new GridBagConstraints();
		gbc_listMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_listMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_listMethodComboBox.gridx = 5;
		gbc_listMethodComboBox.gridy = 6;
		panel.add(listMethodComboBox, gbc_listMethodComboBox);
		
		listMethodComboBox.addActionListener(actionListener);
		
		
		JLabel flexibleIndicator = new JLabel("Flexible Indicator");
		flexibleIndicator.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_flexibleIndicator = new GridBagConstraints();
		gbc_flexibleIndicator.anchor = GridBagConstraints.WEST;
		gbc_flexibleIndicator.insets = new Insets(0, 0, 5, 5);
		gbc_flexibleIndicator.gridx = 4;
		gbc_flexibleIndicator.gridy = 7;
		panel.add(flexibleIndicator, gbc_flexibleIndicator);
		
		List<ComboBoxEntry> flexibleIndicatorComboBoxEntries = new ArrayList<ComboBoxEntry>();
		flexibleIndicatorComboBoxEntries.add(new ComboBoxEntry());
		flexibleIndicatorComboBoxEntries.add(new ComboBoxEntry(true, "Flexible"));
		flexibleIndicatorComboBoxEntries.add(new ComboBoxEntry(false, "Not flexible"));

		flexibleIndicatorComboBox = new JComboBox(flexibleIndicatorComboBoxEntries.toArray());
		flexibleIndicatorComboBox.setMinimumSize(new Dimension(32, 25));
		flexibleIndicatorComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		flexibleIndicatorComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_flexibleIndicatorComboBox = new GridBagConstraints();
		gbc_flexibleIndicatorComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_flexibleIndicatorComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_flexibleIndicatorComboBox.gridx = 5;
		gbc_flexibleIndicatorComboBox.gridy = 7;
		panel.add(flexibleIndicatorComboBox, gbc_flexibleIndicatorComboBox);
		
		flexibleIndicatorComboBox.addActionListener(actionListener);
		
		JLabel flexProductEligibilityIndicator = new JLabel("Flexible Product Eligibility Indicator");
		flexProductEligibilityIndicator.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_flexProductEligibilityIndicator = new GridBagConstraints();
		gbc_flexProductEligibilityIndicator.anchor = GridBagConstraints.WEST;
		gbc_flexProductEligibilityIndicator.insets = new Insets(0, 0, 5, 25);
		gbc_flexProductEligibilityIndicator.gridx = 4;
		gbc_flexProductEligibilityIndicator.gridy = 8;
		panel.add(flexProductEligibilityIndicator, gbc_flexProductEligibilityIndicator);
		
		List<ComboBoxEntry> flexProductEligibilityIndicatorComboBoxEntries = new ArrayList<ComboBoxEntry>();
		flexProductEligibilityIndicatorComboBoxEntries.add(new ComboBoxEntry());
		flexProductEligibilityIndicatorComboBoxEntries.add(new ComboBoxEntry(true, "Flexible"));
		flexProductEligibilityIndicatorComboBoxEntries.add(new ComboBoxEntry(false, "Not flexible"));

		flexProductEligibilityIndicatorComboBox = new JComboBox(flexProductEligibilityIndicatorComboBoxEntries.toArray());
		flexProductEligibilityIndicatorComboBox.setMinimumSize(new Dimension(32, 25));
		flexProductEligibilityIndicatorComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		flexProductEligibilityIndicatorComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_flexProductEligibilityIndicatorComboBox = new GridBagConstraints();
		gbc_flexProductEligibilityIndicatorComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_flexProductEligibilityIndicatorComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_flexProductEligibilityIndicatorComboBox.gridx = 5;
		gbc_flexProductEligibilityIndicatorComboBox.gridy = 8;
		panel.add(flexProductEligibilityIndicatorComboBox, gbc_flexProductEligibilityIndicatorComboBox);
		
		flexProductEligibilityIndicatorComboBox.addActionListener(actionListener);
		
		JLabel valuationMethodLabel = new JLabel("Valuation Method");
		valuationMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_valuationMethodLabel = new GridBagConstraints();
		gbc_valuationMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_valuationMethodLabel.insets = new Insets(0, 0, 5, 5);
		gbc_valuationMethodLabel.gridx = 4;
		gbc_valuationMethodLabel.gridy = 9;
		panel.add(valuationMethodLabel, gbc_valuationMethodLabel);
		
		List<ComboBoxEntry> valuationMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		valuationMethodComboBoxEntries.add(new ComboBoxEntry());
		valuationMethodComboBoxEntries.add(new ComboBoxEntry("EQTY", "Premium style"));
		valuationMethodComboBoxEntries.add(new ComboBoxEntry("FUT", "Futures style mark-to-market"));
		valuationMethodComboBoxEntries.add(new ComboBoxEntry("FUTDA", "Futures style with an attached cash adjustment"));
		valuationMethodComboBoxEntries.add(new ComboBoxEntry("CDS", "CDS style collateralization"));
		valuationMethodComboBoxEntries.add(new ComboBoxEntry("CDSD", "CDS in delivery - use recovery rate to calculate obligation"));

		valuationMethodComboBox = new JComboBox(valuationMethodComboBoxEntries.toArray());
		valuationMethodComboBox.setMinimumSize(new Dimension(32, 25));
		valuationMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		valuationMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_valuationMethodComboBox = new GridBagConstraints();
		gbc_valuationMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_valuationMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_valuationMethodComboBox.gridx = 5;
		gbc_valuationMethodComboBox.gridy = 9;
		panel.add(valuationMethodComboBox, gbc_valuationMethodComboBox);
		
		valuationMethodComboBox.addActionListener(actionListener);
		
		JLabel flowScheduleTypeLabel = new JLabel("Flow Schedule Type");
		flowScheduleTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_flowScheduleTypeLabel = new GridBagConstraints();
		gbc_flowScheduleTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_flowScheduleTypeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_flowScheduleTypeLabel.gridx = 4;
		gbc_flowScheduleTypeLabel.gridy = 10;
		panel.add(flowScheduleTypeLabel, gbc_flowScheduleTypeLabel);
		
		List<ComboBoxEntry> flowScheduleTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry());
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry(0, "NERC Eastern Off-Peak"));
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry(1, "NERC Western Off-Peak"));
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry(2, "NERC Calendar-All Days in month"));
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry(3, "NERC Eastern Peak"));
		flowScheduleTypeComboBoxEntries.add(new ComboBoxEntry(4, "NERC Western Peak"));

		flowScheduleTypeComboBox = new JComboBox(flowScheduleTypeComboBoxEntries.toArray());
		flowScheduleTypeComboBox.setMinimumSize(new Dimension(32, 25));
		flowScheduleTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		flowScheduleTypeComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_flowScheduleTypeComboBox = new GridBagConstraints();
		gbc_flowScheduleTypeComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_flowScheduleTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_flowScheduleTypeComboBox.gridx = 5;
		gbc_flowScheduleTypeComboBox.gridy = 10;
		panel.add(flowScheduleTypeComboBox, gbc_flowScheduleTypeComboBox);
		
		flowScheduleTypeComboBox.addActionListener(actionListener);
		
		JLabel restructuringTypeLabel = new JLabel("Restructuring Type");
		restructuringTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_restructuringTypeLabel = new GridBagConstraints();
		gbc_restructuringTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_restructuringTypeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_restructuringTypeLabel.gridx = 4;
		gbc_restructuringTypeLabel.gridy = 11;
		panel.add(restructuringTypeLabel, gbc_restructuringTypeLabel);
		
		List<ComboBoxEntry> restructuringTypeComboBoxEntries = new ArrayList<ComboBoxEntry>();
		restructuringTypeComboBoxEntries.add(new ComboBoxEntry());
		restructuringTypeComboBoxEntries.add(new ComboBoxEntry("FR", "Full Restructuring"));
		restructuringTypeComboBoxEntries.add(new ComboBoxEntry("MR", "Modified Restructuring"));
		restructuringTypeComboBoxEntries.add(new ComboBoxEntry("MM", "Modified Mod Restructuring"));
		restructuringTypeComboBoxEntries.add(new ComboBoxEntry("XR", "No Restructuring specified"));
	
		restructuringTypeComboBox = new JComboBox(restructuringTypeComboBoxEntries.toArray());
		restructuringTypeComboBox.setMinimumSize(new Dimension(32, 25));
		restructuringTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		restructuringTypeComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_restructuringTypeComboBox = new GridBagConstraints();
		gbc_restructuringTypeComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_restructuringTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_restructuringTypeComboBox.gridx = 5;
		gbc_restructuringTypeComboBox.gridy = 11;
		panel.add(restructuringTypeComboBox, gbc_restructuringTypeComboBox);
		
		restructuringTypeComboBox.addActionListener(actionListener);
		
		JLabel seniorityLabel = new JLabel("Seniority");
		seniorityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_seniorityLabel = new GridBagConstraints();
		gbc_seniorityLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_seniorityLabel.insets = new Insets(3, 0, 5, 5);
		gbc_seniorityLabel.gridx = 4;
		gbc_seniorityLabel.gridy = 12;
		panel.add(seniorityLabel, gbc_seniorityLabel);
		
		List<ComboBoxEntry> seniorityComboBoxEntries = new ArrayList<ComboBoxEntry>();
		seniorityComboBoxEntries.add(new ComboBoxEntry());
		seniorityComboBoxEntries.add(new ComboBoxEntry("SD", "Senior Secured"));
		seniorityComboBoxEntries.add(new ComboBoxEntry("SR", "Senior"));
		seniorityComboBoxEntries.add(new ComboBoxEntry("SB", "Subordinated"));
	
		seniorityComboBox = new JComboBox(seniorityComboBoxEntries.toArray());
		seniorityComboBox.setMinimumSize(new Dimension(32, 25));
		seniorityComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		seniorityComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_seniorityComboBox = new GridBagConstraints();
		gbc_seniorityComboBox.anchor = GridBagConstraints.NORTH;
		gbc_seniorityComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_seniorityComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_seniorityComboBox.gridx = 5;
		gbc_seniorityComboBox.gridy = 12;
		panel.add(seniorityComboBox, gbc_seniorityComboBox);
		
		seniorityComboBox.addActionListener(actionListener);	
		
		updateContent();
	}
	
	private boolean dirtyFieldCheck(Double value, JTextField jTextField) {

		Number value2 = null;
		
		try {
		
				value2 = doubleFormat.parse(jTextField.getText()).doubleValue();
		}
		catch (ParseException e) {
		}
		
		if (value != null && value2 == null)
			return true;

		else if (value == null && value2 != null)
			return true;
			
		else if (value == null && value2 == null)
			return false;
		
		return (!value.equals(value2));
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
		if (jTextFieldDateEditor.getText().length() > 0) {
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

	private boolean dirtyFieldCheck(Integer value, JTextField jTextField) {

		Integer value2 = null;
		try {
			value2 = integerFormat.parse(jTextField.getText()).intValue();
		}
		catch (ParseException e) {
		}

		if (value != null && value2 == null)
			return true;

		else if (value == null && value2 != null)
			return true;

		else if (value == null && value2 == null)
			return false;

		return (!value.equals(value2));
	}

	private void checkConsistency() {

		if (update)
			return;

		dirty = false;

		consistent = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSecurityExchange() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getSecurityExchange().equals(((ComboBoxEntry) securityExchangeComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSecurityExchange() == null && securityExchangeComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getFpool(), poolTextField))
			dirty = true;
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getSecurityGroup(), securityGroupTextField))
			dirty = true;

		consistent = consistent
				&& dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getContractSettlMonth(), contractSettlementDateChooser, contractSettlementWarningLabel);

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCpRegType(), cpRegTypeTextField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getCpProgramm() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getCpProgramm().equals(((ComboBoxEntry) cpProgramComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getCpProgramm() == null && cpProgramComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSecurityStatus() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getSecurityStatus().equals(((ComboBoxEntry) securityStatusComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSecurityStatus() == null && securityStatusComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSettlOnOpen() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getSettlOnOpen().equals(((ComboBoxEntry) settleOnOpenComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSettlOnOpen() == null && settleOnOpenComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getInstrmtAssignmentMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getInstrmtAssignmentMethod().equals(((ComboBoxEntry) instrumentAssignementMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getInstrmtAssignmentMethod() == null && instrumentAssignementMethodComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getPositionLimit(), positionLimitTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getNtPositionLimit(), ntPositionLimitTextField))
			dirty = true;
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getPercentAtRisk(), percentAtRiskTextField))
			dirty = true;
				
		if (securityEditor.getSecurity().getSecurityDetails().getUnderlyingDeterminationMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getUnderlyingDeterminationMethod().equals(((ComboBoxEntry) underlyingPriceDeterminationMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getUnderlyingDeterminationMethod() == null && underlyingPriceDeterminationMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (securityEditor.getSecurity().getSecurityDetails().getDeliveryForm() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getDeliveryForm().equals(((ComboBoxEntry) deliveryFormComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getDeliveryForm() == null && deliveryFormComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		//col2
		
		if (securityEditor.getSecurity().getSecurityDetails().getSettleMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getSettleMethod().equals(((ComboBoxEntry) settlementMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSettleMethod() == null && settlementMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getProductComplex(), productComplexTextField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getExerciseStyle() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getExerciseStyle().equals(((ComboBoxEntry) exerciseStyleComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getExerciseStyle() == null && exerciseStyleComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getPriceQuoteMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getPriceQuoteMethod().equals(((ComboBoxEntry) priceQuoteMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getPriceQuoteMethod() == null && priceQuoteMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getListMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getListMethod().equals(((ComboBoxEntry) listMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getListMethod() == null && listMethodComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getOptionPayoutAmount(), optionPayoutAmountTextField))
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getFlexibleIndicator() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getFlexibleIndicator().equals(((ComboBoxEntry) flexibleIndicatorComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getFlexibleIndicator() == null && flexibleIndicatorComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getFlexProductEligibilityIndicator() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getFlexProductEligibilityIndicator().equals(((ComboBoxEntry) flexProductEligibilityIndicatorComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getFlexProductEligibilityIndicator() == null && flexProductEligibilityIndicatorComboBox.getSelectedIndex() > 0)
			dirty = true;
				
		if (securityEditor.getSecurity().getSecurityDetails().getValuationMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getValuationMethod().equals(((ComboBoxEntry) valuationMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getValuationMethod() == null && valuationMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getFlowScheduleTyped() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getFlowScheduleTyped().equals(((ComboBoxEntry) flowScheduleTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getFlowScheduleTyped() == null && flowScheduleTypeComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getRestructuringType() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getRestructuringType().equals(((ComboBoxEntry) restructuringTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getRestructuringType() == null && restructuringTypeComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getSeniority() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getSeniority().equals(((ComboBoxEntry) seniorityComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getSeniority() == null && seniorityComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getOptionPayoutType() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getOptionPayoutType().equals(((ComboBoxEntry) optionPayoutTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getOptionPayoutType() == null && optionPayoutTypeComboBox.getSelectedIndex() > 0)
			dirty = true;


		securityEditor.checkDirty();
	}

	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityExchange((String) ((ComboBoxEntry) securityExchangeComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setFpool(poolTextField.getText());
		
		securityEditor.getSecurity().getSecurityDetails().setSecurityGroup(securityGroupTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setContractSettlMonth(contractSettlementDateChooser.getDate());

		securityEditor.getSecurity().getSecurityDetails().setCpRegType(cpRegTypeTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setCpProgramm((Integer) ((ComboBoxEntry) cpProgramComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setSecurityStatus((String) ((ComboBoxEntry) securityStatusComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setSettlOnOpen((String) ((ComboBoxEntry) settleOnOpenComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setInstrmtAssignmentMethod((String) ((ComboBoxEntry) instrumentAssignementMethodComboBox.getSelectedItem()).getEntry());

		if (positionLimitTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setPositionLimit(Integer.parseInt(positionLimitTextField.getText()));
		
		else
			securityEditor.getSecurity().getSecurityDetails().setPositionLimit(null);

		if (ntPositionLimitTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setNtPositionLimit(Integer.parseInt(ntPositionLimitTextField.getText()));
		
		else
			securityEditor.getSecurity().getSecurityDetails().setNtPositionLimit(null);
		
		if (percentAtRiskTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setPercentAtRisk(Double.parseDouble(percentAtRiskTextField.getText()));
		
		else
			securityEditor.getSecurity().getSecurityDetails().setPercentAtRisk(null);
		
		securityEditor.getSecurity().getSecurityDetails().setUnderlyingDeterminationMethod((Integer) ((ComboBoxEntry) underlyingPriceDeterminationMethodComboBox.getSelectedItem()).getEntry());

		
		securityEditor.getSecurity().getSecurityDetails().setDeliveryForm((Integer) ((ComboBoxEntry) deliveryFormComboBox.getSelectedItem()).getEntry());
		
		//col2
		
		securityEditor.getSecurity().getSecurityDetails().setSettleMethod((String) ((ComboBoxEntry) settlementMethodComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setProductComplex(productComplexTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setExerciseStyle((Integer) ((ComboBoxEntry) exerciseStyleComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setPriceQuoteMethod((String) ((ComboBoxEntry) priceQuoteMethodComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setListMethod((Integer) ((ComboBoxEntry) listMethodComboBox.getSelectedItem()).getEntry());	
		
		securityEditor.getSecurity().getSecurityDetails().setFlexibleIndicator((Boolean) ((ComboBoxEntry) flexibleIndicatorComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setFlexProductEligibilityIndicator((Boolean) ((ComboBoxEntry) flexProductEligibilityIndicatorComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setValuationMethod((String) ((ComboBoxEntry) valuationMethodComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setFlowScheduleTyped((Integer) ((ComboBoxEntry) flowScheduleTypeComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setRestructuringType((String) ((ComboBoxEntry) restructuringTypeComboBox.getSelectedItem()).getEntry());
		
		securityEditor.getSecurity().getSecurityDetails().setSeniority((String) ((ComboBoxEntry) seniorityComboBox.getSelectedItem()).getEntry());

		if (optionPayoutAmountTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setOptionPayoutAmount(Double.parseDouble(optionPayoutAmountTextField.getText()));
		
		else
			securityEditor.getSecurity().getSecurityDetails().setOptionPayoutAmount(null);
		
		securityEditor.getSecurity().getSecurityDetails().setOptionPayoutType((Integer) ((ComboBoxEntry) optionPayoutTypeComboBox.getSelectedItem()).getEntry());
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

		securityExchangeComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSecurityExchange(), null));

		poolTextField.setText(securityEditor.getSecurity().getSecurityDetails().getFpool());
		
		securityGroupTextField.setText(securityEditor.getSecurity().getSecurityDetails().getSecurityGroup());

		contractSettlementDateChooser.setDate(securityEditor.getSecurity().getSecurityDetails().getContractSettlMonth());

		cpRegTypeTextField.setText(securityEditor.getSecurity().getSecurityDetails().getCpRegType());

		cpProgramComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getCpProgramm(), null));

		securityStatusComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSecurityStatus(), null));
		
		instrumentAssignementMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getInstrmtAssignmentMethod(), null));

		settleOnOpenComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSettlOnOpen(), null));

		if (securityEditor.getSecurity().getSecurityDetails().getPositionLimit() != null)
			positionLimitTextField.setText(integerFormat.format(securityEditor.getSecurity().getSecurityDetails().getPositionLimit()));
		else
			positionLimitTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getNtPositionLimit() != null)
			ntPositionLimitTextField.setText(integerFormat.format(securityEditor.getSecurity().getSecurityDetails().getNtPositionLimit()));
		else
			ntPositionLimitTextField.setText("");
		
		if (securityEditor.getSecurity().getSecurityDetails().getPercentAtRisk() != null)
			percentAtRiskTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getPercentAtRisk()));
		else
			percentAtRiskTextField.setText("");
		
		underlyingPriceDeterminationMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getUnderlyingDeterminationMethod(), null));

		deliveryFormComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getDeliveryForm(), null));
		
		settlementMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSettleMethod(), null));
		
		productComplexTextField.setText(securityEditor.getSecurity().getSecurityDetails().getProductComplex());

		exerciseStyleComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getExerciseStyle(), null));

		priceQuoteMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getPriceQuoteMethod(), null));
		
		listMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getListMethod(), null));
		
		flexibleIndicatorComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getFlexibleIndicator(), null));
		
		flexProductEligibilityIndicatorComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getFlexProductEligibilityIndicator(), null));
		
		valuationMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getValuationMethod(), null));
		
		flowScheduleTypeComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getFlowScheduleTyped(), null));
		
		restructuringTypeComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getRestructuringType(), null));
		
		seniorityComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getSeniority(), null));
		
		optionPayoutTypeComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getOptionPayoutType(), null));		

		if (securityEditor.getSecurity().getSecurityDetails().getOptionPayoutAmount() != null)
			optionPayoutAmountTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getOptionPayoutAmount()));
		else
			optionPayoutAmountTextField.setText("");
		
		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {

			securityExchangeComboBox.setBackground(new Color(255, 243, 204));
			securityExchangeComboBox.setEnabled(true);

			poolTextField.setBackground(new Color(255, 243, 204));
			poolTextField.setEditable(true);
			
			securityGroupTextField.setBackground(new Color(255, 243, 204));
			securityGroupTextField.setEditable(true);

			((JTextFieldDateEditor) contractSettlementDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
			contractSettlementDateChooser.setEnabled(true);

			cpRegTypeTextField.setBackground(new Color(255, 243, 204));
			cpRegTypeTextField.setEditable(true);

			cpProgramComboBox.setBackground(new Color(255, 243, 204));
			cpProgramComboBox.setEnabled(true);

			securityStatusComboBox.setBackground(new Color(255, 243, 204));
			securityStatusComboBox.setEnabled(true);

			settleOnOpenComboBox.setBackground(new Color(255, 243, 204));
			settleOnOpenComboBox.setEnabled(true);

			instrumentAssignementMethodComboBox.setBackground(new Color(255, 243, 204));
			instrumentAssignementMethodComboBox.setEnabled(true);

			positionLimitTextField.setBackground(new Color(255, 243, 204));
			positionLimitTextField.setEditable(true);

			ntPositionLimitTextField.setBackground(new Color(255, 243, 204));
			ntPositionLimitTextField.setEditable(true);
			
			percentAtRiskTextField.setBackground(new Color(255, 243, 204));
			percentAtRiskTextField.setEditable(true);
						
			underlyingPriceDeterminationMethodComboBox.setBackground(new Color(255, 243, 204));
			underlyingPriceDeterminationMethodComboBox.setEnabled(true);
			
			deliveryFormComboBox.setBackground(new Color(255, 243, 204));
			deliveryFormComboBox.setEnabled(true);
			
			
			settlementMethodComboBox.setBackground(new Color(255, 243, 204));
			settlementMethodComboBox.setEnabled(true);
			
			productComplexTextField.setBackground(new Color(255, 243, 204));
			productComplexTextField.setEditable(true);

			exerciseStyleComboBox.setBackground(new Color(255, 243, 204));
			exerciseStyleComboBox.setEnabled(true);

			priceQuoteMethodComboBox.setBackground(new Color(255, 243, 204));
			priceQuoteMethodComboBox.setEnabled(true);

			listMethodComboBox.setBackground(new Color(255, 243, 204));
			listMethodComboBox.setEnabled(true);

			optionPayoutAmountTextField.setBackground(new Color(255, 243, 204));
			optionPayoutAmountTextField.setEditable(true);
			
			flexibleIndicatorComboBox.setBackground(new Color(255, 243, 204));
			flexibleIndicatorComboBox.setEnabled(true);
			
			flexProductEligibilityIndicatorComboBox.setBackground(new Color(255, 243, 204));
			flexProductEligibilityIndicatorComboBox.setEnabled(true);
			
			valuationMethodComboBox.setBackground(new Color(255, 243, 204));
			valuationMethodComboBox.setEnabled(true);
			
			flowScheduleTypeComboBox.setBackground(new Color(255, 243, 204));
			flowScheduleTypeComboBox.setEnabled(true);
			
			restructuringTypeComboBox.setBackground(new Color(255, 243, 204));
			restructuringTypeComboBox.setEnabled(true);
			
			seniorityComboBox.setBackground(new Color(255, 243, 204));
			seniorityComboBox.setEnabled(true);
			
			optionPayoutTypeComboBox.setBackground(new Color(255, 243, 204));
			optionPayoutTypeComboBox.setEnabled(true);
		}
		else {

			securityExchangeComboBox.setBackground(new Color(204, 216, 255));
			securityExchangeComboBox.setEnabled(false);

			poolTextField.setBackground(new Color(204, 216, 255));
			poolTextField.setEditable(false);
			
			securityGroupTextField.setBackground(new Color(204, 216, 255));
			securityGroupTextField.setEditable(false);

			((JTextFieldDateEditor) contractSettlementDateChooser.getDateEditor()).setBackground(new Color(204, 216, 255));
			contractSettlementDateChooser.setEnabled(false);

			cpRegTypeTextField.setBackground(new Color(204, 216, 255));
			cpRegTypeTextField.setEditable(false);

			cpProgramComboBox.setBackground(new Color(204, 216, 255));
			cpProgramComboBox.setEnabled(false);

			securityStatusComboBox.setBackground(new Color(204, 216, 255));
			securityStatusComboBox.setEnabled(false);

			settleOnOpenComboBox.setBackground(new Color(204, 216, 255));
			settleOnOpenComboBox.setEnabled(false);

			instrumentAssignementMethodComboBox.setBackground(new Color(204, 216, 255));
			instrumentAssignementMethodComboBox.setEnabled(false);

			positionLimitTextField.setBackground(new Color(204, 216, 255));
			positionLimitTextField.setEditable(false);

			ntPositionLimitTextField.setBackground(new Color(204, 216, 255));
			ntPositionLimitTextField.setEditable(false);
			
			percentAtRiskTextField.setBackground(new Color(204, 216, 255));
			percentAtRiskTextField.setEditable(false);
						
			underlyingPriceDeterminationMethodComboBox.setBackground(new Color(204, 216, 255));
			underlyingPriceDeterminationMethodComboBox.setEnabled(false);
			
			deliveryFormComboBox.setBackground(new Color(204, 216, 255));
			deliveryFormComboBox.setEnabled(false);
			
			settlementMethodComboBox.setBackground(new Color(204, 216, 255));
			settlementMethodComboBox.setEnabled(false);
			
			productComplexTextField.setBackground(new Color(204, 216, 255));
			productComplexTextField.setEnabled(false);


			exerciseStyleComboBox.setBackground(new Color(204, 216, 255));
			exerciseStyleComboBox.setEnabled(false);

			priceQuoteMethodComboBox.setBackground(new Color(204, 216, 255));
			priceQuoteMethodComboBox.setEnabled(false);

			listMethodComboBox.setBackground(new Color(204, 216, 255));
			listMethodComboBox.setEnabled(false);

			optionPayoutAmountTextField.setBackground(new Color(204, 216, 255));
			optionPayoutAmountTextField.setEditable(false);
			
			flexibleIndicatorComboBox.setBackground(new Color(204, 216, 255));
			flexibleIndicatorComboBox.setEnabled(false);
			
			flexProductEligibilityIndicatorComboBox.setBackground(new Color(204, 216, 255));
			flexProductEligibilityIndicatorComboBox.setEnabled(false);
			
			valuationMethodComboBox.setBackground(new Color(204, 216, 255));
			valuationMethodComboBox.setEnabled(false);
			
			flowScheduleTypeComboBox.setBackground(new Color(204, 216, 255));
			flowScheduleTypeComboBox.setEnabled(false);
			
			restructuringTypeComboBox.setBackground(new Color(204, 216, 255));
			restructuringTypeComboBox.setEnabled(false);
			
			seniorityComboBox.setBackground(new Color(204, 216, 255));
			seniorityComboBox.setEnabled(false);
			
			optionPayoutTypeComboBox.setBackground(new Color(204, 216, 255));
			optionPayoutTypeComboBox.setEnabled(false);
		}

		update = false;

		checkConsistency();
	}

	/**
	 * Checks if is consistent.
	 *
	 * @return true, if is consistent
	 */
	public boolean isConsistent() {

		return consistent;
	}
}
