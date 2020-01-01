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
import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;

/**
 * The Class FSecurityEditorDetails1.
 */
public class FSecurityEditorDetails1 extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField couponRateField = null;

	private boolean dirty = false;

	private FSecurityEditor securityEditor = null;

	private boolean update = false;
	private JComboBox timeUnitComboBox;
	private JComboBox contractMultiplierUnitComboBox;
	private JTextField contractMultiplierTextField;
	private JTextField minimumPriceIncrementTextField;
	private JTextField minimumPriceIncrementAmountTextField;
	private JTextField factorTextField;
	private JComboBox unitOfMeasureComboBox;
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0##############################");
	private JComboBox priceUnitOfMeasureComboBox;
	private JTextField priceUnitOfMeasureQuantityTextField;
	private JTextField capPriceTextField;
	private JTextField floorPriceTextField;

	private boolean consistent;

	private JTextField unitOfMeasureQtyTextField;

	private JTextField creditRatingTextField;

	private JTextField instrumentRegistryTextField;

	private JTextField strikePriceTextField;

	private JTextField strikeCurrencyTextField;

	private JTextField strikeMultiplierTextField;

	private JTextField strikeValueTextField;

	private JComboBox strikePriceDeterminationMethodComboBox;

	private JComboBox strikePriceBoundaryMethodComboBox;

	private JTextField strikePriceBoundaryPrecisionTextField;

	private JTextArea descriptionTextArea;

	/**
	 * Instantiates a new f security editor details1.
	 *
	 * @param securityEditor the security editor
	 */
	public FSecurityEditorDetails1(final FSecurityEditor securityEditor) {

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
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.columnWidths = new int[] { 0, 0, 370, 0, 370, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0,0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 10;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel couponRateLabel = new JLabel("Coupon Rate");
		couponRateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_userLabel = new GridBagConstraints();
		gbc_userLabel.anchor = GridBagConstraints.WEST;
		gbc_userLabel.insets = new Insets(50, 50, 5, 25);
		gbc_userLabel.gridx = 1;
		gbc_userLabel.gridy = 0;
		panel.add(couponRateLabel, gbc_userLabel);

		couponRateField = new JTextField();
		couponRateField.setDocument(new DoubleDocument());
		couponRateField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		couponRateField.getDocument().addDocumentListener(documentListener);
		couponRateField.setBackground(new Color(255, 243, 204));
		couponRateField.setPreferredSize(new Dimension(250, 25));
		couponRateField.setColumns(10);
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.insets = new Insets(50, 0, 5, 50);
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.gridx = 2;
		gbc_nameTextField.gridy = 0;
		panel.add(couponRateField, gbc_nameTextField);

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 13;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 5;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JLabel capPrice = new JLabel("Cap Price");
		capPrice.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_capPrice = new GridBagConstraints();
		gbc_capPrice.anchor = GridBagConstraints.WEST;
		gbc_capPrice.insets = new Insets(0, 50, 5, 25);
		gbc_capPrice.gridx = 1;
		gbc_capPrice.gridy = 1;
		panel.add(capPrice, gbc_capPrice);
		
		capPriceTextField = new JTextField();
		capPriceTextField.setDocument(new DoubleDocument());
		capPriceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		capPriceTextField.getDocument().addDocumentListener(documentListener);
		capPriceTextField.setBackground(new Color(255, 243, 204));
		capPriceTextField.setPreferredSize(new Dimension(250, 25));
		capPriceTextField.setColumns(10);
		GridBagConstraints gbc_capPriceTextField = new GridBagConstraints();
		gbc_capPriceTextField.insets = new Insets(0, 0, 5, 50);
		gbc_capPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_capPriceTextField.gridx = 2;
		gbc_capPriceTextField.gridy = 1;
		panel.add(capPriceTextField, gbc_capPriceTextField);

		JLabel floorPriceLabel = new JLabel("Floor Price");
		floorPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_floorPriceLabel = new GridBagConstraints();
		gbc_floorPriceLabel.anchor = GridBagConstraints.WEST;
		gbc_floorPriceLabel.insets = new Insets(0, 50, 5, 25);
		gbc_floorPriceLabel.gridx = 1;
		gbc_floorPriceLabel.gridy = 2;
		panel.add(floorPriceLabel, gbc_floorPriceLabel);

		floorPriceTextField = new JTextField();
		floorPriceTextField.setDocument(new DoubleDocument());
		floorPriceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		floorPriceTextField.getDocument().addDocumentListener(documentListener);
		floorPriceTextField.setBackground(new Color(255, 243, 204));
		floorPriceTextField.setPreferredSize(new Dimension(250, 25));
		floorPriceTextField.setColumns(10);
		GridBagConstraints gbc_floorPriceTextField = new GridBagConstraints();
		gbc_floorPriceTextField.insets = new Insets(0, 0, 5, 50);
		gbc_floorPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_floorPriceTextField.gridx = 2;
		gbc_floorPriceTextField.gridy = 2;
		panel.add(floorPriceTextField, gbc_floorPriceTextField);

		JLabel lblMaturityDate = new JLabel("Contract Multiplier");
		lblMaturityDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblMaturityDate = new GridBagConstraints();
		gbc_lblMaturityDate.anchor = GridBagConstraints.WEST;
		gbc_lblMaturityDate.insets = new Insets(0, 50, 5, 25);
		gbc_lblMaturityDate.gridx = 1;
		gbc_lblMaturityDate.gridy = 3;
		panel.add(lblMaturityDate, gbc_lblMaturityDate);

		contractMultiplierTextField = new JTextField();
		contractMultiplierTextField.setDocument(new DoubleDocument());
		contractMultiplierTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		contractMultiplierTextField.getDocument().addDocumentListener(documentListener);
		contractMultiplierTextField.setBackground(new Color(255, 243, 204));
		contractMultiplierTextField.setPreferredSize(new Dimension(250, 25));
		contractMultiplierTextField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 50);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 3;
		panel.add(contractMultiplierTextField, gbc_textField);

		JLabel lblLocaleOfIssue = new JLabel("Contract Multiplier Unit");
		lblLocaleOfIssue.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblLocaleOfIssue = new GridBagConstraints();
		gbc_lblLocaleOfIssue.anchor = GridBagConstraints.WEST;
		gbc_lblLocaleOfIssue.insets = new Insets(0, 50, 5, 25);
		gbc_lblLocaleOfIssue.gridx = 1;
		gbc_lblLocaleOfIssue.gridy = 4;
		panel.add(lblLocaleOfIssue, gbc_lblLocaleOfIssue);

		List<ComboBoxEntry> contractMultiplierUnitComboEntries = new ArrayList<ComboBoxEntry>();
		contractMultiplierUnitComboEntries.add(new ComboBoxEntry());
		contractMultiplierUnitComboEntries.add(new ComboBoxEntry(0, "Shares"));
		contractMultiplierUnitComboEntries.add(new ComboBoxEntry(1, "Hours"));
		contractMultiplierUnitComboEntries.add(new ComboBoxEntry(2, "Days"));

		contractMultiplierUnitComboBox = new JComboBox(contractMultiplierUnitComboEntries.toArray());
		contractMultiplierUnitComboBox.setMinimumSize(new Dimension(32, 25));
		contractMultiplierUnitComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		contractMultiplierUnitComboBox.setPreferredSize(new Dimension(32, 25));
		contractMultiplierUnitComboBox.setOpaque(true);
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(0, 0, 5, 50);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 4;
		panel.add(contractMultiplierUnitComboBox, gbc_localeOfissueTextField);

		JLabel lblNewLabel = new JLabel("Minimum Price Increment");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 5;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		minimumPriceIncrementTextField = new JTextField();
		minimumPriceIncrementTextField.setDocument(new DoubleDocument());
		minimumPriceIncrementTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		minimumPriceIncrementTextField.getDocument().addDocumentListener(documentListener);
		minimumPriceIncrementTextField.setBackground(new Color(255, 243, 204));
		minimumPriceIncrementTextField.setPreferredSize(new Dimension(250, 25));
		minimumPriceIncrementTextField.setColumns(10);
		GridBagConstraints gbc_minimumPriceIncrementTextField = new GridBagConstraints();
		gbc_minimumPriceIncrementTextField.insets = new Insets(0, 0, 5, 50);
		gbc_minimumPriceIncrementTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_minimumPriceIncrementTextField.gridx = 2;
		gbc_minimumPriceIncrementTextField.gridy = 5;
		panel.add(minimumPriceIncrementTextField, gbc_minimumPriceIncrementTextField);

		JLabel minimumPriceIncrementAmountLabel = new JLabel("Minimum Price Increment Amount");
		minimumPriceIncrementAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_minimumPriceIncrementAmountLabel = new GridBagConstraints();
		gbc_minimumPriceIncrementAmountLabel.anchor = GridBagConstraints.WEST;
		gbc_minimumPriceIncrementAmountLabel.insets = new Insets(0, 50, 5, 25);
		gbc_minimumPriceIncrementAmountLabel.gridx = 1;
		gbc_minimumPriceIncrementAmountLabel.gridy = 6;
		panel.add(minimumPriceIncrementAmountLabel, gbc_minimumPriceIncrementAmountLabel);

		minimumPriceIncrementAmountTextField = new JTextField();
		minimumPriceIncrementAmountTextField.setDocument(new DoubleDocument());
		minimumPriceIncrementAmountTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5,
				0, 0)));
		minimumPriceIncrementAmountTextField.getDocument().addDocumentListener(documentListener);
		minimumPriceIncrementAmountTextField.setBackground(new Color(255, 243, 204));
		minimumPriceIncrementAmountTextField.setPreferredSize(new Dimension(250, 25));
		minimumPriceIncrementAmountTextField.setColumns(10);
		GridBagConstraints gbc_roundLotTextField = new GridBagConstraints();
		gbc_roundLotTextField.insets = new Insets(0, 0, 5, 50);
		gbc_roundLotTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_roundLotTextField.gridx = 2;
		gbc_roundLotTextField.gridy = 6;
		panel.add(minimumPriceIncrementAmountTextField, gbc_roundLotTextField);

		JLabel lblNewLabel_1 = new JLabel("Factor");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 50, 5, 25);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 7;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);

		factorTextField = new JTextField();
		factorTextField.setDocument(new DoubleDocument());
		factorTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		factorTextField.getDocument().addDocumentListener(documentListener);
		factorTextField.setBackground(new Color(255, 243, 204));
		factorTextField.setPreferredSize(new Dimension(250, 25));
		factorTextField.setColumns(10);
		GridBagConstraints gbc_factorTextField = new GridBagConstraints();
		gbc_factorTextField.insets = new Insets(0, 0, 5, 50);
		gbc_factorTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_factorTextField.gridx = 2;
		gbc_factorTextField.gridy = 7;
		panel.add(factorTextField, gbc_factorTextField);

		JLabel lblInterestAccrualDate = new JLabel("Unit of Measure");
		lblInterestAccrualDate.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblInterestAccrualDate = new GridBagConstraints();
		gbc_lblInterestAccrualDate.anchor = GridBagConstraints.WEST;
		gbc_lblInterestAccrualDate.insets = new Insets(0, 50, 5, 25);
		gbc_lblInterestAccrualDate.gridx = 1;
		gbc_lblInterestAccrualDate.gridy = 8;
		panel.add(lblInterestAccrualDate, gbc_lblInterestAccrualDate);

		List<ComboBoxEntry> unitOfMeasureComboBoxEntries = new ArrayList<ComboBoxEntry>();
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry());
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bcf", "Billion cubic feet"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MMbbl", "Million Barrels"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MMBtu", "One Million BTU"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MWh", "Megawatt hours"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bbl", "Barrels"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bu", "Bushels"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("lbs", "Pounds"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Gal", "Gallons"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("oz_tr", "Troy Ounces"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("t", "Metric Tons"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("tn", "Tons (US)"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("USD", "US Dollars"));
		unitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Alw", "Allowances"));

		unitOfMeasureComboBox = new JComboBox(unitOfMeasureComboBoxEntries.toArray());
		unitOfMeasureComboBox.setMinimumSize(new Dimension(32, 25));
		unitOfMeasureComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		unitOfMeasureComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_unitOfMeasureTextField = new GridBagConstraints();
		gbc_unitOfMeasureTextField.insets = new Insets(0, 0, 5, 50);
		gbc_unitOfMeasureTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_unitOfMeasureTextField.gridx = 2;
		gbc_unitOfMeasureTextField.gridy = 8;
		panel.add(unitOfMeasureComboBox, gbc_unitOfMeasureTextField);

		unitOfMeasureComboBox.addActionListener(actionListener);

		JLabel unitOfMeasureQtyLabel = new JLabel("Unit of Measure Quantity");
		unitOfMeasureQtyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_unitOfMeasureQtyLabel = new GridBagConstraints();
		gbc_unitOfMeasureQtyLabel.anchor = GridBagConstraints.WEST;
		gbc_unitOfMeasureQtyLabel.insets = new Insets(0, 50, 5, 25);
		gbc_unitOfMeasureQtyLabel.gridx = 1;
		gbc_unitOfMeasureQtyLabel.gridy = 9;
		panel.add(unitOfMeasureQtyLabel, gbc_unitOfMeasureQtyLabel);

		unitOfMeasureQtyTextField = new JTextField();
		unitOfMeasureQtyTextField.setDocument(new DoubleDocument());
		unitOfMeasureQtyTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		unitOfMeasureQtyTextField.getDocument().addDocumentListener(documentListener);
		unitOfMeasureQtyTextField.setBackground(new Color(255, 243, 204));
		unitOfMeasureQtyTextField.setPreferredSize(new Dimension(250, 25));
		unitOfMeasureQtyTextField.setColumns(10);
		GridBagConstraints gbc_unitOfMeasureQtyTextField = new GridBagConstraints();
		gbc_unitOfMeasureQtyTextField.insets = new Insets(0, 0, 5, 50);
		gbc_unitOfMeasureQtyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_unitOfMeasureQtyTextField.gridx = 2;
		gbc_unitOfMeasureQtyTextField.gridy = 9;
		panel.add(unitOfMeasureQtyTextField, gbc_unitOfMeasureQtyTextField);

		JLabel priceUnitOfMeasureLabel = new JLabel("Price Unit of Measure");
		priceUnitOfMeasureLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceUnitOfMeasureLabel = new GridBagConstraints();
		gbc_priceUnitOfMeasureLabel.anchor = GridBagConstraints.WEST;
		gbc_priceUnitOfMeasureLabel.insets = new Insets(0, 50, 5, 25);
		gbc_priceUnitOfMeasureLabel.gridx = 1;
		gbc_priceUnitOfMeasureLabel.gridy = 10;
		panel.add(priceUnitOfMeasureLabel, gbc_priceUnitOfMeasureLabel);

		List<ComboBoxEntry> priceUnitOfMeasureComboBoxEntries = new ArrayList<ComboBoxEntry>();
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry());
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bcf", "Billion cubic feet"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MMbbl", "Million Barrels"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MMBtu", "One Million BTU"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("MWh", "Megawatt hours"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bbl", "Barrels"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Bu", "Bushels"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("lbs", "pounds"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Gal", "Gallons"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("oz_tr", "Troy Ounces"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("t", "Metric Tons "));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("tn", "Tons (US)"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("USD", "US Dollars"));
		priceUnitOfMeasureComboBoxEntries.add(new ComboBoxEntry("Alw", "Allowances"));

		priceUnitOfMeasureComboBox = new JComboBox(priceUnitOfMeasureComboBoxEntries.toArray());
		priceUnitOfMeasureComboBox.setMinimumSize(new Dimension(32, 25));
		priceUnitOfMeasureComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		priceUnitOfMeasureComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_priceUnitOfMeasureComboBox = new GridBagConstraints();
		gbc_priceUnitOfMeasureComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_priceUnitOfMeasureComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceUnitOfMeasureComboBox.gridx = 2;
		gbc_priceUnitOfMeasureComboBox.gridy = 10;
		panel.add(priceUnitOfMeasureComboBox, gbc_priceUnitOfMeasureComboBox);

		JLabel priceUnitOfMeasureQuantityLabel = new JLabel("Price Unit of Measure Quantity");
		priceUnitOfMeasureQuantityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_priceUnitOfMeasureQuantityLabel = new GridBagConstraints();
		gbc_priceUnitOfMeasureQuantityLabel.anchor = GridBagConstraints.WEST;
		gbc_priceUnitOfMeasureQuantityLabel.insets = new Insets(0, 50, 5, 25);
		gbc_priceUnitOfMeasureQuantityLabel.gridx = 1;
		gbc_priceUnitOfMeasureQuantityLabel.gridy = 11;
		panel.add(priceUnitOfMeasureQuantityLabel, gbc_priceUnitOfMeasureQuantityLabel);

		priceUnitOfMeasureQuantityTextField = new JTextField();
		priceUnitOfMeasureQuantityTextField.setDocument(new DoubleDocument());
		priceUnitOfMeasureQuantityTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0,
				0)));
		priceUnitOfMeasureQuantityTextField.getDocument().addDocumentListener(documentListener);
		priceUnitOfMeasureQuantityTextField.setBackground(new Color(255, 243, 204));
		priceUnitOfMeasureQuantityTextField.setPreferredSize(new Dimension(250, 25));
		priceUnitOfMeasureQuantityTextField.setColumns(10);
		GridBagConstraints gbc_ntPositionLimitTextField = new GridBagConstraints();
		gbc_ntPositionLimitTextField.insets = new Insets(0, 0, 5, 50);
		gbc_ntPositionLimitTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ntPositionLimitTextField.gridx = 2;
		gbc_ntPositionLimitTextField.gridy = 11;
		panel.add(priceUnitOfMeasureQuantityTextField, gbc_ntPositionLimitTextField);

		JLabel lblIssuer = new JLabel("Time Unit");
		lblIssuer.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblIssuer = new GridBagConstraints();
		gbc_lblIssuer.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblIssuer.insets = new Insets(3, 50, 50, 25);
		gbc_lblIssuer.gridx = 1;
		gbc_lblIssuer.gridy = 12;
		panel.add(lblIssuer, gbc_lblIssuer);

		List<ComboBoxEntry> timeUnitComboBoxEntries = new ArrayList<ComboBoxEntry>();
		timeUnitComboBoxEntries.add(new ComboBoxEntry());
		timeUnitComboBoxEntries.add(new ComboBoxEntry("H", "Hour"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("Min", "Minute"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("S", "Second"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("D", "Day"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("Wk", "Week"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("Mo", "Month"));
		timeUnitComboBoxEntries.add(new ComboBoxEntry("Yr", "Year"));

		timeUnitComboBox = new JComboBox(timeUnitComboBoxEntries.toArray());
		timeUnitComboBox.setMinimumSize(new Dimension(32, 25));
		timeUnitComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		timeUnitComboBox.setPreferredSize(new Dimension(32, 25));

		GridBagConstraints gbc_issuerTextField = new GridBagConstraints();
		gbc_issuerTextField.anchor = GridBagConstraints.NORTH;
		gbc_issuerTextField.insets = new Insets(0, 0, 0, 50);
		gbc_issuerTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_issuerTextField.gridx = 2;
		gbc_issuerTextField.gridy = 12;
		panel.add(timeUnitComboBox, gbc_issuerTextField);

		timeUnitComboBox.addActionListener(actionListener);
		
		
		//col2 
		
		JLabel creditRatingLabel = new JLabel("Credit Rating");
		creditRatingLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_creditRatingLabel = new GridBagConstraints();
		gbc_creditRatingLabel.anchor = GridBagConstraints.WEST;
		gbc_creditRatingLabel.insets = new Insets(50, 0, 5, 25);
		gbc_creditRatingLabel.gridx = 3;
		gbc_creditRatingLabel.gridy = 0;
		panel.add(creditRatingLabel, gbc_creditRatingLabel);

		creditRatingTextField = new JTextField();
		creditRatingTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		creditRatingTextField.getDocument().addDocumentListener(documentListener);
		creditRatingTextField.setBackground(new Color(255, 243, 204));
		creditRatingTextField.setPreferredSize(new Dimension(250, 25));
		creditRatingTextField.setColumns(10);
		GridBagConstraints gbc_creditRatingTextField = new GridBagConstraints();
		gbc_creditRatingTextField.insets = new Insets(50, 0, 5, 50);
		gbc_creditRatingTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_creditRatingTextField.gridx = 4;
		gbc_creditRatingTextField.gridy = 0;
		panel.add(creditRatingTextField, gbc_creditRatingTextField);

		JLabel instrumentRegistryLabel = new JLabel("Instrument Registry");
		instrumentRegistryLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_instrumentRegistryLabel = new GridBagConstraints();
		gbc_instrumentRegistryLabel.anchor = GridBagConstraints.WEST;
		gbc_instrumentRegistryLabel.insets = new Insets(0, 0, 5, 25);
		gbc_instrumentRegistryLabel.gridx = 3;
		gbc_instrumentRegistryLabel.gridy = 1;
		panel.add(instrumentRegistryLabel, gbc_instrumentRegistryLabel);

		instrumentRegistryTextField = new JTextField();
		instrumentRegistryTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		instrumentRegistryTextField.getDocument().addDocumentListener(documentListener);
		instrumentRegistryTextField.setBackground(new Color(255, 243, 204));
		instrumentRegistryTextField.setPreferredSize(new Dimension(250, 25));
		instrumentRegistryTextField.setColumns(10);
		GridBagConstraints gbc_instrumentRegistryTextField = new GridBagConstraints();
		gbc_instrumentRegistryTextField.insets = new Insets(0, 0, 5, 50);
		gbc_instrumentRegistryTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_instrumentRegistryTextField.gridx = 4;
		gbc_instrumentRegistryTextField.gridy = 1;
		panel.add(instrumentRegistryTextField, gbc_instrumentRegistryTextField);

		JLabel strikePriceLabel = new JLabel("Strike Price");
		strikePriceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikePriceLabel = new GridBagConstraints();
		gbc_strikePriceLabel.anchor = GridBagConstraints.WEST;
		gbc_strikePriceLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikePriceLabel.gridx = 3;
		gbc_strikePriceLabel.gridy = 2;
		panel.add(strikePriceLabel, gbc_strikePriceLabel);

		strikePriceTextField = new JTextField();
		strikePriceTextField.setDocument(new DoubleDocument());
		strikePriceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		strikePriceTextField.getDocument().addDocumentListener(documentListener);
		strikePriceTextField.setBackground(new Color(255, 243, 204));
		strikePriceTextField.setPreferredSize(new Dimension(250, 25));
		strikePriceTextField.setColumns(10);
		GridBagConstraints gbc_strikePriceTextField = new GridBagConstraints();
		gbc_strikePriceTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikePriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikePriceTextField.gridx = 4;
		gbc_strikePriceTextField.gridy = 2;
		panel.add(strikePriceTextField, gbc_strikePriceTextField);

		JLabel strikeCurrencyLabel = new JLabel("Strike Currency");
		strikeCurrencyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikeCurrencyLabel = new GridBagConstraints();
		gbc_strikeCurrencyLabel.anchor = GridBagConstraints.WEST;
		gbc_strikeCurrencyLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikeCurrencyLabel.gridx = 3;
		gbc_strikeCurrencyLabel.gridy = 3;
		panel.add(strikeCurrencyLabel, gbc_strikeCurrencyLabel);

		strikeCurrencyTextField = new JTextField();
		strikeCurrencyTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		strikeCurrencyTextField.getDocument().addDocumentListener(documentListener);
		strikeCurrencyTextField.setBackground(new Color(255, 243, 204));
		strikeCurrencyTextField.setPreferredSize(new Dimension(250, 25));
		strikeCurrencyTextField.setColumns(10);
		GridBagConstraints gbc_strikeCurrencyTextField = new GridBagConstraints();
		gbc_strikeCurrencyTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikeCurrencyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikeCurrencyTextField.gridx = 4;
		gbc_strikeCurrencyTextField.gridy = 3;
		panel.add(strikeCurrencyTextField, gbc_strikeCurrencyTextField);

		JLabel strikeMultiplierLabel = new JLabel("Strike Multiplier");
		strikeMultiplierLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikeMultiplierLabel = new GridBagConstraints();
		gbc_strikeMultiplierLabel.anchor = GridBagConstraints.WEST;
		gbc_strikeMultiplierLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikeMultiplierLabel.gridx = 3;
		gbc_strikeMultiplierLabel.gridy = 4;
		panel.add(strikeMultiplierLabel, gbc_strikeMultiplierLabel);

		strikeMultiplierTextField = new JTextField();
		strikeMultiplierTextField.setDocument(new DoubleDocument());
		strikeMultiplierTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		strikeMultiplierTextField.getDocument().addDocumentListener(documentListener);
		strikeMultiplierTextField.setBackground(new Color(255, 243, 204));
		strikeMultiplierTextField.setPreferredSize(new Dimension(250, 25));
		strikeMultiplierTextField.setColumns(10);
		GridBagConstraints gbc_strikeMultiplierTextField = new GridBagConstraints();
		gbc_strikeMultiplierTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikeMultiplierTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikeMultiplierTextField.gridx = 4;
		gbc_strikeMultiplierTextField.gridy = 4;
		panel.add(strikeMultiplierTextField, gbc_strikeMultiplierTextField);

		JLabel strikeValueLabel = new JLabel("Strike Value");
		strikeValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikeValueLabel = new GridBagConstraints();
		gbc_strikeValueLabel.anchor = GridBagConstraints.WEST;
		gbc_strikeValueLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikeValueLabel.gridx = 3;
		gbc_strikeValueLabel.gridy = 5;
		panel.add(strikeValueLabel, gbc_strikeValueLabel);

		strikeValueTextField = new JTextField();
		strikeValueTextField.setDocument(new DoubleDocument());
		strikeValueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		strikeValueTextField.getDocument().addDocumentListener(documentListener);
		strikeValueTextField.setBackground(new Color(255, 243, 204));
		strikeValueTextField.setPreferredSize(new Dimension(250, 25));
		strikeValueTextField.setColumns(10);
		GridBagConstraints gbc_strikeValueTextField = new GridBagConstraints();
		gbc_strikeValueTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikeValueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikeValueTextField.gridx = 4;
		gbc_strikeValueTextField.gridy = 5;
		panel.add(strikeValueTextField, gbc_strikeValueTextField);

		JLabel strikePriceDeterminationMethodLabel = new JLabel("Strike Price Determination Method");
		strikePriceDeterminationMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikePriceDeterminationMethodLabel = new GridBagConstraints();
		gbc_strikePriceDeterminationMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_strikePriceDeterminationMethodLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikePriceDeterminationMethodLabel.gridx = 3;
		gbc_strikePriceDeterminationMethodLabel.gridy = 6;
		panel.add(strikePriceDeterminationMethodLabel, gbc_strikePriceDeterminationMethodLabel);

		List<ComboBoxEntry> strikePriceDeterminationMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		strikePriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry());
		strikePriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(1, "Fixed Strike"));
		strikePriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(2, "Strike set at expiration"));
		strikePriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(3, "Strike set to average across life"));
		strikePriceDeterminationMethodComboBoxEntries.add(new ComboBoxEntry(4, "Strike set to optimal value"));

		strikePriceDeterminationMethodComboBox = new JComboBox(strikePriceDeterminationMethodComboBoxEntries.toArray());
		strikePriceDeterminationMethodComboBox.setMinimumSize(new Dimension(32, 25));
		strikePriceDeterminationMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		strikePriceDeterminationMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_strikePriceDeterminationMethodComboBox = new GridBagConstraints();
		gbc_strikePriceDeterminationMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_strikePriceDeterminationMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikePriceDeterminationMethodComboBox.gridx = 4;
		gbc_strikePriceDeterminationMethodComboBox.gridy = 6;
		panel.add(strikePriceDeterminationMethodComboBox, gbc_strikePriceDeterminationMethodComboBox);

		strikePriceDeterminationMethodComboBox.addActionListener(actionListener);

		JLabel strikePriceBoundaryMethodLabel = new JLabel("Strike Price Boundary Method");
		strikePriceBoundaryMethodLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikePriceBoundaryMethodLabel = new GridBagConstraints();
		gbc_strikePriceBoundaryMethodLabel.anchor = GridBagConstraints.WEST;
		gbc_strikePriceBoundaryMethodLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikePriceBoundaryMethodLabel.gridx = 3;
		gbc_strikePriceBoundaryMethodLabel.gridy = 7;
		panel.add(strikePriceBoundaryMethodLabel, gbc_strikePriceBoundaryMethodLabel);

		List<ComboBoxEntry> strikePriceBoundaryMethodComboBoxEntries = new ArrayList<ComboBoxEntry>();
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry());
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry(1, "Less than"));
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry(2, "Less than or equal"));
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry(3, "Equal"));
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry(4, "Greater than or equal"));
		strikePriceBoundaryMethodComboBoxEntries.add(new ComboBoxEntry(5, "Greater than"));

		strikePriceBoundaryMethodComboBox = new JComboBox(strikePriceBoundaryMethodComboBoxEntries.toArray());
		strikePriceBoundaryMethodComboBox.setMinimumSize(new Dimension(32, 25));
		strikePriceBoundaryMethodComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		strikePriceBoundaryMethodComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_strikePriceBoundaryMethodComboBox = new GridBagConstraints();
		gbc_strikePriceBoundaryMethodComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_strikePriceBoundaryMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikePriceBoundaryMethodComboBox.gridx = 4;
		gbc_strikePriceBoundaryMethodComboBox.gridy = 7;
		panel.add(strikePriceBoundaryMethodComboBox, gbc_strikePriceBoundaryMethodComboBox);

		strikePriceBoundaryMethodComboBox.addActionListener(actionListener);

		JLabel strikePriceBoundaryPrecisionLabel = new JLabel("Strike Price Boundary Precision");
		strikePriceBoundaryPrecisionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_strikePriceBoundaryPrecisionLabel = new GridBagConstraints();
		gbc_strikePriceBoundaryPrecisionLabel.anchor = GridBagConstraints.WEST;
		gbc_strikePriceBoundaryPrecisionLabel.insets = new Insets(0, 0, 5, 25);
		gbc_strikePriceBoundaryPrecisionLabel.gridx = 3;
		gbc_strikePriceBoundaryPrecisionLabel.gridy = 8;
		panel.add(strikePriceBoundaryPrecisionLabel, gbc_strikePriceBoundaryPrecisionLabel);

		strikePriceBoundaryPrecisionTextField = new JTextField();
		strikePriceBoundaryPrecisionTextField.setDocument(new DoubleDocument());
		strikePriceBoundaryPrecisionTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5,
				0, 0)));
		strikePriceBoundaryPrecisionTextField.getDocument().addDocumentListener(documentListener);
		strikePriceBoundaryPrecisionTextField.setBackground(new Color(255, 243, 204));
		strikePriceBoundaryPrecisionTextField.setPreferredSize(new Dimension(250, 25));
		strikePriceBoundaryPrecisionTextField.setColumns(10);
		GridBagConstraints gbc_strikePriceBoundaryPrecisionTextField = new GridBagConstraints();
		gbc_strikePriceBoundaryPrecisionTextField.insets = new Insets(0, 0, 5, 50);
		gbc_strikePriceBoundaryPrecisionTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_strikePriceBoundaryPrecisionTextField.gridx = 4;
		gbc_strikePriceBoundaryPrecisionTextField.gridy = 8;
		panel.add(strikePriceBoundaryPrecisionTextField, gbc_strikePriceBoundaryPrecisionTextField);

		JLabel descriptionLabel = new JLabel("Description");
		descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
		gbc_descriptionLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_descriptionLabel.insets = new Insets(3, 0, 5, 25);
		gbc_descriptionLabel.gridx = 3;
		gbc_descriptionLabel.gridy = 9;
		panel.add(descriptionLabel, gbc_descriptionLabel);

		descriptionTextArea = new JTextArea();
		descriptionTextArea.setMinimumSize(new Dimension(200, 100));
		
		descriptionTextArea.getDocument().addDocumentListener(documentListener);
		descriptionTextArea.setBackground(new Color(255, 243, 204));
		descriptionTextArea.setBorder( new EmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints gbc_descriptionTextArea = new GridBagConstraints();
		gbc_descriptionTextArea.anchor = GridBagConstraints.NORTH;
		gbc_descriptionTextArea.gridheight = 4;
		gbc_descriptionTextArea.insets = new Insets(0, 0, 50, 50);
		gbc_descriptionTextArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_descriptionTextArea.gridx = 4;
		gbc_descriptionTextArea.gridy = 9;
		JScrollPane jScrollPane = new JScrollPane(descriptionTextArea);
		jScrollPane.setPreferredSize(new Dimension(250, 115));
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		panel.add(jScrollPane, gbc_descriptionTextArea);

		updateContent();
	}
	
	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
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
	
	private boolean dirtyFieldCheck(String value, JTextArea jTextArea) {

		if (value == null && jTextArea.getText().trim().length() > 0)
			return true;

		if (value == null && jTextArea.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextArea.getText().trim()));
	}

	private void checkConsistency() {

		if (update)
			return;

		consistent = true;

		dirty = false;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getContractMultiplier(), contractMultiplierTextField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getContractMultiplierUnit() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getContractMultiplierUnit()
						.equals(((ComboBoxEntry) contractMultiplierUnitComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getContractMultiplierUnit() == null && contractMultiplierUnitComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrement(), minimumPriceIncrementTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrementAmount(), minimumPriceIncrementAmountTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getFactor(), factorTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasureQty(), unitOfMeasureQtyTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCouponRate(), couponRateField))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getTimeUnit() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getTimeUnit().equals(((ComboBoxEntry) timeUnitComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getTimeUnit() == null && timeUnitComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasure() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasure().equals(((ComboBoxEntry) unitOfMeasureComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasure() == null && unitOfMeasureComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasure() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasure().equals(((ComboBoxEntry) priceUnitOfMeasureComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasure() == null && priceUnitOfMeasureComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasureQty(), priceUnitOfMeasureQuantityTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCapPrice(), capPriceTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getFloorPrice(), floorPriceTextField))
			dirty = true;
		
		//col2
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getCreditRating(), creditRatingTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getInstrumentRegistry(), instrumentRegistryTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStrikePrice(), strikePriceTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStrikeCurrency(), strikeCurrencyTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStrikeMultiplier(), strikeMultiplierTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStrikeValue(), strikeValueTextField))
			dirty = true;
		
		if (securityEditor.getSecurity().getSecurityDetails().getStrikePriceDeterminationMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getStrikePriceDeterminationMethod().equals(((ComboBoxEntry) strikePriceDeterminationMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getStrikePriceDeterminationMethod() == null && strikePriceDeterminationMethodComboBox.getSelectedIndex() > 0)
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryMethod() != null
				&& !securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryMethod().equals(((ComboBoxEntry) strikePriceBoundaryMethodComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryMethod() == null && strikePriceBoundaryMethodComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryPrecision(), strikePriceBoundaryPrecisionTextField))
			dirty = true;

		if (dirtyFieldCheck(securityEditor.getSecurity().getSecurityDetails().getDescription(), descriptionTextArea))
			dirty = true;

		securityEditor.checkDirty();
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

		if (couponRateField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setCouponRate(Double.parseDouble(couponRateField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setCouponRate(null);

		securityEditor.getSecurity().getSecurityDetails().setTimeUnit((String) ((ComboBoxEntry) timeUnitComboBox.getSelectedItem()).getEntry());

		if (contractMultiplierTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setContractMultiplier(Double.parseDouble(contractMultiplierTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setContractMultiplier(null);

		securityEditor.getSecurity().getSecurityDetails().setContractMultiplierUnit((Integer) ((ComboBoxEntry) contractMultiplierUnitComboBox.getSelectedItem()).getEntry());

		if (minimumPriceIncrementTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setMinPriceIncrement(Double.parseDouble(minimumPriceIncrementTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setMinPriceIncrement(null);

		if (minimumPriceIncrementAmountTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setMinPriceIncrementAmount(Double.parseDouble(minimumPriceIncrementAmountTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setMinPriceIncrementAmount(null);

		if (factorTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setFactor(Double.parseDouble(factorTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setFactor(null);

		if (unitOfMeasureQtyTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setUnitOfMeasureQty(Double.parseDouble(unitOfMeasureQtyTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setUnitOfMeasureQty(null);

		if (priceUnitOfMeasureQuantityTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setPriceUnitOfMeasureQty(Double.parseDouble(priceUnitOfMeasureQuantityTextField.getText()));

		else
			securityEditor.getSecurity().getSecurityDetails().setPriceUnitOfMeasureQty(null);

		securityEditor.getSecurity().getSecurityDetails().setUnitOfMeasure((String) ((ComboBoxEntry) unitOfMeasureComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setPriceUnitOfMeasure((String) ((ComboBoxEntry) priceUnitOfMeasureComboBox.getSelectedItem()).getEntry());

		if (capPriceTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setCapPrice(Double.parseDouble(capPriceTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setCapPrice(null);

		if (floorPriceTextField.getText().trim().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setFloorPrice(Double.parseDouble(floorPriceTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setFloorPrice(null);
		
		
		//col2
		
		securityEditor.getSecurity().getSecurityDetails().setCreditRating(creditRatingTextField.getText());

		securityEditor.getSecurity().getSecurityDetails().setInstrumentRegistry(instrumentRegistryTextField.getText());

		if (strikePriceTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setStrikePrice(Double.parseDouble(strikePriceTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setStrikePrice(null);

		securityEditor.getSecurity().getSecurityDetails().setStrikeCurrency(strikeCurrencyTextField.getText());

		if (strikeMultiplierTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setStrikeMultiplier(Double.parseDouble(strikeMultiplierTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setStrikeMultiplier(null);

		if (strikeValueTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setStrikeValue(Double.parseDouble(strikeValueTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setStrikeValue(null);
		
		securityEditor.getSecurity().getSecurityDetails().setStrikePriceDeterminationMethod((Integer) ((ComboBoxEntry) strikePriceDeterminationMethodComboBox.getSelectedItem()).getEntry());

		securityEditor.getSecurity().getSecurityDetails().setStrikePriceBoundaryMethod((Integer) ((ComboBoxEntry) strikePriceBoundaryMethodComboBox.getSelectedItem()).getEntry());
		
		if (strikePriceBoundaryPrecisionTextField.getText().length() > 0)
			securityEditor.getSecurity().getSecurityDetails().setStrikePriceBoundaryPrecision(Double.parseDouble(strikePriceBoundaryPrecisionTextField.getText()));
		else
			securityEditor.getSecurity().getSecurityDetails().setStrikePriceBoundaryPrecision(null);

		securityEditor.getSecurity().getSecurityDetails().setDescription(descriptionTextArea.getText());

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

		if (securityEditor.getSecurity().getSecurityDetails().getCouponRate() != null)
			couponRateField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getCouponRate()));
		else
			couponRateField.setText("");

		timeUnitComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getTimeUnit(), null));

		contractMultiplierUnitComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getContractMultiplierUnit(), null));

		if (securityEditor.getSecurity().getSecurityDetails().getContractMultiplier() != null)
			contractMultiplierTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getContractMultiplier()));
		else
			contractMultiplierTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrement() != null)
			minimumPriceIncrementTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrement()));
		else
			minimumPriceIncrementTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrementAmount() != null)
			minimumPriceIncrementAmountTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getMinPriceIncrementAmount()));
		else
			minimumPriceIncrementAmountTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getFactor() != null)
			factorTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getFactor()));
		else
			factorTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasureQty() != null)
			unitOfMeasureQtyTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasureQty()));
		else
			unitOfMeasureQtyTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasureQty() != null)
			priceUnitOfMeasureQuantityTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasureQty()));
		else
			priceUnitOfMeasureQuantityTextField.setText("");

		unitOfMeasureComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getUnitOfMeasure(), null));

		priceUnitOfMeasureComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getPriceUnitOfMeasure(), null));

		if (securityEditor.getSecurity().getSecurityDetails().getCapPrice() != null)
			capPriceTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getCapPrice()));
		else
			capPriceTextField.setText("");


		if (securityEditor.getSecurity().getSecurityDetails().getFloorPrice() != null)
			floorPriceTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getFloorPrice()));
		else
			floorPriceTextField.setText("");
		
		//col2
		
		creditRatingTextField.setText(securityEditor.getSecurity().getSecurityDetails().getCreditRating());

		instrumentRegistryTextField.setText(securityEditor.getSecurity().getSecurityDetails().getInstrumentRegistry());

		if (securityEditor.getSecurity().getSecurityDetails().getStrikePrice() != null)
			strikePriceTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getStrikePrice()));
		else
			strikePriceTextField.setText("");

		strikeCurrencyTextField.setText(securityEditor.getSecurity().getSecurityDetails().getStrikeCurrency());

		if (securityEditor.getSecurity().getSecurityDetails().getStrikeMultiplier() != null)
			strikeMultiplierTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getStrikeMultiplier()));
		else
			strikeMultiplierTextField.setText("");

		if (securityEditor.getSecurity().getSecurityDetails().getStrikeValue() != null)
			strikeValueTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getStrikeValue()));
		else
			strikeMultiplierTextField.setText("");
		
		strikePriceDeterminationMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getStrikePriceDeterminationMethod(), null));

		strikePriceBoundaryMethodComboBox.setSelectedItem(new ComboBoxEntry(securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryMethod(), null));
		
		if (securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryPrecision() != null)
			strikePriceBoundaryPrecisionTextField.setText(doubleFormat.format(securityEditor.getSecurity().getSecurityDetails().getStrikePriceBoundaryPrecision()));
		else
			strikePriceBoundaryPrecisionTextField.setText("");		

		descriptionTextArea.setText(securityEditor.getSecurity().getSecurityDetails().getDescription());


		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {

			couponRateField.setBackground(new Color(255, 243, 204));
			couponRateField.setEditable(true);

			timeUnitComboBox.setBackground(new Color(255, 243, 204));
			timeUnitComboBox.setEnabled(true);

			contractMultiplierUnitComboBox.setBackground(new Color(255, 243, 204));
			contractMultiplierUnitComboBox.setEnabled(true);

			contractMultiplierTextField.setBackground(new Color(255, 243, 204));
			contractMultiplierTextField.setEditable(true);

			minimumPriceIncrementTextField.setBackground(new Color(255, 243, 204));
			minimumPriceIncrementTextField.setEditable(true);

			minimumPriceIncrementAmountTextField.setBackground(new Color(255, 243, 204));
			minimumPriceIncrementAmountTextField.setEditable(true);

			factorTextField.setBackground(new Color(255, 243, 204));
			factorTextField.setEditable(true);

			unitOfMeasureQtyTextField.setBackground(new Color(255, 243, 204));
			unitOfMeasureQtyTextField.setEditable(true);

			unitOfMeasureComboBox.setBackground(new Color(255, 243, 204));
			unitOfMeasureComboBox.setEnabled(true);

			priceUnitOfMeasureQuantityTextField.setBackground(new Color(255, 243, 204));
			priceUnitOfMeasureQuantityTextField.setEditable(true);

			priceUnitOfMeasureComboBox.setBackground(new Color(255, 243, 204));
			priceUnitOfMeasureComboBox.setEnabled(true);

			capPriceTextField.setBackground(new Color(255, 243, 204));
			capPriceTextField.setEditable(true);

			floorPriceTextField.setBackground(new Color(255, 243, 204));
			floorPriceTextField.setEditable(true);
			
			creditRatingTextField.setBackground(new Color(255, 243, 204));
			creditRatingTextField.setEditable(true);

			instrumentRegistryTextField.setBackground(new Color(255, 243, 204));
			instrumentRegistryTextField.setEditable(true);

			strikePriceTextField.setBackground(new Color(255, 243, 204));
			strikePriceTextField.setEditable(true);

			strikeCurrencyTextField.setBackground(new Color(255, 243, 204));
			strikeCurrencyTextField.setEditable(true);

			strikeMultiplierTextField.setBackground(new Color(255, 243, 204));
			strikeMultiplierTextField.setEditable(true);

			strikeValueTextField.setBackground(new Color(255, 243, 204));
			strikeValueTextField.setEditable(true);
			
			strikePriceDeterminationMethodComboBox.setBackground(new Color(255, 243, 204));
			strikePriceDeterminationMethodComboBox.setEnabled(true);
			
			strikePriceBoundaryMethodComboBox.setBackground(new Color(255, 243, 204));
			strikePriceBoundaryMethodComboBox.setEnabled(true);
			
			strikePriceBoundaryPrecisionTextField.setBackground(new Color(255, 243, 204));
			strikePriceBoundaryPrecisionTextField.setEditable(true);

			descriptionTextArea.setBackground(new Color(255, 243, 204));
			descriptionTextArea.setEditable(true);

		}
		else {

			couponRateField.setBackground(new Color(204, 216, 255));
			couponRateField.setEditable(false);

			timeUnitComboBox.setBackground(new Color(204, 216, 255));
			timeUnitComboBox.setEnabled(false);

			contractMultiplierUnitComboBox.setBackground(new Color(204, 216, 255));
			contractMultiplierUnitComboBox.setEnabled(false);

			contractMultiplierTextField.setBackground(new Color(204, 216, 255));
			contractMultiplierTextField.setEditable(false);

			minimumPriceIncrementTextField.setBackground(new Color(204, 216, 255));
			minimumPriceIncrementTextField.setEditable(false);

			minimumPriceIncrementAmountTextField.setBackground(new Color(204, 216, 255));
			minimumPriceIncrementAmountTextField.setEditable(false);

			factorTextField.setBackground(new Color(204, 216, 255));
			factorTextField.setEditable(false);

			unitOfMeasureQtyTextField.setBackground(new Color(204, 216, 255));
			unitOfMeasureQtyTextField.setEditable(false);

			unitOfMeasureComboBox.setBackground(new Color(204, 216, 255));
			unitOfMeasureComboBox.setEnabled(false);

			priceUnitOfMeasureQuantityTextField.setBackground(new Color(204, 216, 255));
			priceUnitOfMeasureQuantityTextField.setEditable(false);

			priceUnitOfMeasureComboBox.setBackground(new Color(204, 216, 255));
			priceUnitOfMeasureComboBox.setEnabled(false);

			capPriceTextField.setBackground(new Color(204, 216, 255));
			capPriceTextField.setEnabled(false);

			floorPriceTextField.setBackground(new Color(204, 216, 255));
			floorPriceTextField.setEditable(false);
			
			creditRatingTextField.setBackground(new Color(204, 216, 255));
			creditRatingTextField.setEditable(false);

			instrumentRegistryTextField.setBackground(new Color(204, 216, 255));
			instrumentRegistryTextField.setEditable(false);

			strikePriceTextField.setBackground(new Color(204, 216, 255));
			strikePriceTextField.setEditable(false);

			strikeCurrencyTextField.setBackground(new Color(204, 216, 255));
			strikeCurrencyTextField.setEditable(false);

			strikeMultiplierTextField.setBackground(new Color(204, 216, 255));
			strikeMultiplierTextField.setEditable(false);

			strikeValueTextField.setBackground(new Color(204, 216, 255));
			strikeValueTextField.setEditable(false);
			
			strikePriceDeterminationMethodComboBox.setBackground(new Color(204, 216, 255));
			strikePriceDeterminationMethodComboBox.setEnabled(false);
			
			strikePriceBoundaryMethodComboBox.setBackground(new Color(204, 216, 255));
			strikePriceBoundaryMethodComboBox.setEnabled(false);
			
			strikePriceBoundaryPrecisionTextField.setBackground(new Color(204, 216, 255));
			strikePriceBoundaryPrecisionTextField.setEditable(false);

			descriptionTextArea.setBackground(new Color(204, 216, 255));
			descriptionTextArea.setEditable(false);

		}

		update = false;

		checkConsistency();
	}
}
