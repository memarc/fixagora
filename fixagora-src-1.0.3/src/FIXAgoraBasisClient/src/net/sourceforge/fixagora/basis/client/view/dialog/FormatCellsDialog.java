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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;
import net.sourceforge.fixagora.basis.client.view.document.PositiveDoubleDocument;
import net.sourceforge.fixagora.basis.client.view.document.PositiveIntegerDocument;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditorMasterData;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.PresentationRoundingMode;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;

import org.apache.poi.hssf.util.CellReference;

/**
 * The Class FormatCellsDialog.
 */
public class FormatCellsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private final JPanel panel = new JPanel();
	private final JList categoryList = new JList();
	private final JPanel cardPanel = new JPanel();
	private final JLabel foregroundLabel = new JLabel("");
	private final JLabel backgroundLabel = new JLabel("");
	private final JButton backgroundButton = new JButton("Background");
	private final JButton foregroundButton = new JButton("Foreground");
	private final JCheckBox lockedCheckBox = new JCheckBox("Locked");
	private final JPanel numberPanel = new JPanel();
	private final JCheckBox gradientPaintCheckBox = new JCheckBox("Gradient Paint");
	private final JCheckBox boldCheckBox = new JCheckBox("Bold");
	private JComboBox decimalPlacesComboBox = null;
	private final JLabel leadingZeroeslabel = new JLabel("Leading Zeroes");
	private final JLabel tickSizeLabel = new JLabel("Tick Size");
	private final JTextField tickSizeTextField = new JTextField("0.001");
	private final JCheckBox negativeNumbersRedCheckBox = new JCheckBox("");
	private final JLabel negaiveNumbersRedLabel = new JLabel("Negative Numbers Red");
	private final JLabel thousandsSeparatorLabel = new JLabel("Thousands Separator");
	private final JCheckBox thousandsSeparatorCheckBox = new JCheckBox("");
	private final JLabel spinnerLabel = new JLabel("Spinner");
	private final JLabel solverLabel = new JLabel("Formula Solver");
	private final JLabel adjustCellLabel = new JLabel("Adjust Cell");
	private final JTextField adjustCellTextField = new JTextField("A1");
	private final JLabel toleranceCellLabel = new JLabel("Tolerance");
	private final JTextField toleranceCellTextField = new JTextField("0.001");
	private final JCheckBox spinnerCheckBox = new JCheckBox("(prevent formula)");
	private final JCheckBox solverCheckBox = new JCheckBox("(formula required)");
	private final JRadioButton spinner1RadioButton = new JRadioButton("");
	private final JButton spinner1MinusButton = new JButton("-");
	private final JTextField spinner1TextField = new JTextField();
	private final JButton spinner1PlusButton = new JButton("+");
	private final JLabel incrementLabel = new JLabel("Increment");
	private final JTextField incrementTextField = new JTextField("0.001");
	private final JRadioButton spinner2RadioButton = new JRadioButton("");
	private final JButton spinner2MinusButton = new JButton("");
	private final JTextField spinner2TextField = new JTextField();
	private final JButton spinner2PlusButton = new JButton("");
	private final JButton spinner3PlusButton = new JButton("");
	private final JButton spinner4PlusButton = new JButton("");
	private final JButton spinner3MinusButton = new JButton("");
	private final JButton spinner4MinusButton = new JButton("");
	private final JButton spinner5MinusButton = new JButton("");
	private final JButton spinner5PlusButton = new JButton("");
	private final JTextField spinner3TextField = new JTextField();
	private final JTextField spinner4TextField = new JTextField();
	private final JTextField spinner5TextField = new JTextField();
	private final JRadioButton spinner3RadioButton = new JRadioButton("");
	private final JRadioButton spinner4RadioButton = new JRadioButton("");
	private final JRadioButton spinner5RadioButton = new JRadioButton("");
	private final JButton decimalPlacesMinusButton = new JButton("-");
	private final JTextField decimalPlacesTextField = new JTextField();
	private final JButton decimalPlacesPlusButton = new JButton("+");
	private final JButton leadingZeroesMinusButton = new JButton("-");
	private final JTextField leadingZeroesTextField = new JTextField();
	private final JButton leadingZeroesPlusButton = new JButton("+");
	private final JPanel booleanPanel = new JPanel();
	private final JLabel trueLabel = new JLabel("TRUE");
	private final JTextField trueTextField = new JTextField();
	private final JLabel falseLabel = new JLabel("FALSE");
	private final JTextField falseTextField = new JTextField();
	private final JLabel plainBooleanLabel = new JLabel("Plain");
	private final JPanel datePanel = new JPanel();
	private final JLabel dateFormatLabel = new JLabel("Format");
	private final JList dateFormatList = new JList();
	private final JLabel daySpinnerLabel = new JLabel("Month Spinner");
	private final JPanel timePanel = new JPanel();
	private final JLabel timeFormatLabel = new JLabel("Format");
	private final JList timeFormatList = new JList();
	private final JPanel textPanel = new JPanel();
	private final JLabel predefinedLabel = new JLabel("Predefined");
	private final JCheckBox predefinedTextCheckBox = new JCheckBox("(prevent formula)");
	private final JList predefinedTextList = new JList();
	private final JButton predefinedAddButton = new JButton("Add");
	private final JButton predefinedRemoveButton = new JButton("Remove");
	private final JLabel minValueLabel = new JLabel("Minimum Value");
	private final JLabel maximumValueLabel = new JLabel("Maximum Value");
	private final JTextField minValueTextField = new JTextField();
	private final JTextField maximumValueTextField = new JTextField();
	private final JLabel tickSizeWarningLabel = new JLabel("");
	private final JLabel toleranceWarningLabel = new JLabel("");
	private final JLabel adjustCellWarningLabel = new JLabel("");
	private final JLabel incrementWarningLabel = new JLabel("");
	private final JLabel minimumValueWarningLabel = new JLabel("");
	private final JLabel maximumValueWarningLabel = new JLabel("");
	private final JLabel styleLabel = new JLabel("Style");
	private final ImageIcon bugIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private final ImageIcon blueLeftIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1leftarrow.png"));
	private final ImageIcon blueRightIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1rightarrow.png"));
	private final ImageIcon blueUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrow.png"));
	private final ImageIcon redDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowred.png"));
	private final ImageIcon redUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowred.png"));
	private final ImageIcon greenDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowgreen.png"));
	private final ImageIcon greenUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowgreen.png"));
	private final ImageIcon blueDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrow.png"));

	private final JLabel roundingModeLabel = new JLabel("Presentation Rounding Mode");
	private JComboBox roundingModeComboBox;
	private final JLabel dateChooserLabel = new JLabel("Date Chooser");
	private final JLabel plainDateLabel = new JLabel("Plain");
	private final JLabel monthSpinnerLabel = new JLabel("Day Spinner");
	private final JRadioButton plainDateRadioButton = new JRadioButton("");
	private final JRadioButton dateChooserRadioButton = new JRadioButton("(prevent formula)");
	private final JRadioButton daySpinnerRadioButton = new JRadioButton("(prevent formula)");
	private final JRadioButton monthSpinnerRadioButton = new JRadioButton("(prevent formula)");
	private final JLabel plainTimeLabel = new JLabel("Plain");
	private final JRadioButton plainTimeRadioButton = new JRadioButton("");
	private final JLabel hourSpinnerLabel = new JLabel("Hour Spinner");
	private final JRadioButton hourSpinnerRadioButton = new JRadioButton("(prevent formula)");
	private final JLabel minuteSpinnerLabel = new JLabel("Minute Spinner");
	private final JRadioButton minuteSpinnerRadioButton = new JRadioButton("(prevent formula)");
	private final JLabel trueWarningLabel = new JLabel("");
	private final JLabel falseWarningLabel = new JLabel("");
	private final JRadioButton plainBooleanRadioButton = new JRadioButton("");
	private final JLabel checkboxBooleanLabel = new JLabel("Check Box");
	private final JRadioButton checkboxBooleanRadioButton = new JRadioButton("(prevent formula)");
	private final JLabel buttonBooleanLabel = new JLabel("Button");
	private final JRadioButton buttonBooleanRadioButton = new JRadioButton("(prevent formula)");
	private final JButton predefinedEditButton = new JButton("Edit");
	private JLabel decimalPlacesWarningLabel = new JLabel();

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	private DecimalFormat decimalFormat = new DecimalFormat("########0.0####################################################",
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	/**
	 * Instantiates a new format cells dialog.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public FormatCellsDialog(SpreadSheetCellFormat spreadSheetCellFormat) {

		incrementTextField.setMinimumSize(new Dimension(4, 25));
		incrementTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		incrementTextField.setPreferredSize(new Dimension(4, 25));
		incrementTextField.setColumns(10);

		spinner1TextField.setMinimumSize(new Dimension(100, 25));
		spinner1TextField.setPreferredSize(new Dimension(100, 25));
		spinner1TextField.setColumns(10);

		tickSizeTextField.setMinimumSize(new Dimension(4, 25));
		tickSizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		tickSizeTextField.setPreferredSize(new Dimension(4, 25));
		tickSizeTextField.setColumns(10);

		adjustCellTextField.setMinimumSize(new Dimension(4, 25));
		adjustCellTextField.setPreferredSize(new Dimension(4, 25));
		adjustCellTextField.setColumns(10);

		toleranceCellTextField.setMinimumSize(new Dimension(4, 25));
		toleranceCellTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		toleranceCellTextField.setPreferredSize(new Dimension(4, 25));
		toleranceCellTextField.setColumns(10);

		negativeNumbersRedCheckBox.setMinimumSize(new Dimension(200, 25));
		negativeNumbersRedCheckBox.setPreferredSize(new Dimension(200, 25));

		thousandsSeparatorCheckBox.setMinimumSize(new Dimension(200, 25));
		thousandsSeparatorCheckBox.setPreferredSize(new Dimension(200, 25));

		spinnerCheckBox.setMinimumSize(new Dimension(200, 25));
		spinnerCheckBox.setPreferredSize(new Dimension(200, 25));

		solverCheckBox.setMinimumSize(new Dimension(200, 25));
		solverCheckBox.setPreferredSize(new Dimension(200, 25));

		setBounds(100, 100, 583, 656);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(FormatCellsDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		setTitle("Format Cells");

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 52 };
		contentPanel.setLayout(gbl_contentPanel);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 25, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panel.setOpaque(false);
		contentPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_categoryList = new GridBagConstraints();
		gbc_categoryList.gridwidth = 2;
		gbc_categoryList.insets = new Insets(0, 0, 5, 5);
		gbc_categoryList.fill = GridBagConstraints.BOTH;
		gbc_categoryList.gridx = 0;
		gbc_categoryList.gridy = 0;
		categoryList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(categoryList, gbc_categoryList);

		GridBagConstraints gbc_cardPanel = new GridBagConstraints();
		gbc_cardPanel.gridheight = 6;
		gbc_cardPanel.insets = new Insets(0, 0, 0, 5);
		gbc_cardPanel.fill = GridBagConstraints.BOTH;
		gbc_cardPanel.gridx = 2;
		gbc_cardPanel.gridy = 0;
		cardPanel.setOpaque(false);
		panel.add(cardPanel, gbc_cardPanel);
		final CardLayout cardLayout = new CardLayout(0, 0);
		cardPanel.setLayout(cardLayout);
		numberPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
		numberPanel.setOpaque(false);

		cardPanel.add(numberPanel, "number");
		GridBagLayout gbl_numberPanel = new GridBagLayout();
		gbl_numberPanel.columnWidths = new int[] { 129, 25, 0, 17, 0, 0 };
		gbl_numberPanel.rowHeights = new int[] { 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 };
		gbl_numberPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_numberPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		numberPanel.setLayout(gbl_numberPanel);

		List<ComboBoxEntry> displayPriceEntries = new ArrayList<ComboBoxEntry>();
		displayPriceEntries.add(new ComboBoxEntry(false, "Decimal Places"));
		displayPriceEntries.add(new ComboBoxEntry(true, "Dollar Fraction 0-00 \u00B9 \u2044"));

		decimalPlacesComboBox = new JComboBox(displayPriceEntries.toArray());
		decimalPlacesComboBox.setMinimumSize(new Dimension(32, 25));
		decimalPlacesComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		decimalPlacesComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_decimalPlacesLabel = new GridBagConstraints();
		gbc_decimalPlacesLabel.anchor = GridBagConstraints.WEST;
		gbc_decimalPlacesLabel.fill = GridBagConstraints.BOTH;
		gbc_decimalPlacesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_decimalPlacesLabel.gridx = 0;
		gbc_decimalPlacesLabel.gridy = 0;
		numberPanel.add(decimalPlacesComboBox, gbc_decimalPlacesLabel);

		decimalPlacesComboBox.setSelectedIndex(0);

		GridBagConstraints gbc_decimalPlacesWarningLabel = new GridBagConstraints();
		gbc_decimalPlacesWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_decimalPlacesWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_decimalPlacesWarningLabel.gridx = 1;
		gbc_decimalPlacesWarningLabel.gridy = 0;
		decimalPlacesWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(decimalPlacesWarningLabel, gbc_decimalPlacesWarningLabel);

		GridBagConstraints gbc_decimalPlacesMinusButton = new GridBagConstraints();
		gbc_decimalPlacesMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_decimalPlacesMinusButton.gridx = 2;
		gbc_decimalPlacesMinusButton.gridy = 0;
		decimalPlacesMinusButton.setPreferredSize(new Dimension(25, 25));
		decimalPlacesMinusButton.setMinimumSize(new Dimension(25, 25));
		decimalPlacesMinusButton.setMargin(new Insets(2, 2, 2, 2));
		decimalPlacesMinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(decimalPlacesMinusButton, gbc_decimalPlacesMinusButton);

		GridBagConstraints gbc_decimalPlacesTextField = new GridBagConstraints();
		gbc_decimalPlacesTextField.gridwidth = 2;
		gbc_decimalPlacesTextField.insets = new Insets(0, 0, 5, 0);
		gbc_decimalPlacesTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_decimalPlacesTextField.gridx = 3;
		gbc_decimalPlacesTextField.gridy = 0;

		decimalPlacesTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 0, 0, 0)));
		decimalPlacesTextField.setDocument(new PositiveIntegerDocument());
		decimalPlacesTextField.setText("3");
		decimalPlacesTextField.setHorizontalAlignment(JTextField.RIGHT);
		decimalPlacesTextField.setBackground(new Color(255, 243, 204));
		decimalPlacesTextField.setPreferredSize(new Dimension(100, 25));
		decimalPlacesTextField.setMinimumSize(new Dimension(100, 25));
		decimalPlacesTextField.setColumns(10);
		numberPanel.add(decimalPlacesTextField, gbc_decimalPlacesTextField);

		decimalPlacesTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(decimalPlacesTextField.getText());
					if (decimalPlacesComboBox.getSelectedIndex() == 1 && getFractionTickSize() == null)
						throw new NumberFormatException();
				}
				catch (Exception e1) {
					if (decimalPlacesComboBox.getSelectedIndex() == 1)
						value = 32;
				}
				if (decimalPlacesComboBox.getSelectedIndex() == 1 && value == 1) {
					decimalPlacesMinusButton.setEnabled(false);
				}
				else if (value == 0)
					decimalPlacesMinusButton.setEnabled(false);
				else
					decimalPlacesMinusButton.setEnabled(true);
				decimalPlacesTextField.setText(Integer.toString(value));

			}
		});

		decimalPlacesTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(decimalPlacesTextField.getText());
					if (decimalPlacesComboBox.getSelectedIndex() == 1 && getFractionTickSize() == null) {
						value = 32;
					}
				}
				catch (NumberFormatException e1) {
				}

				if (decimalPlacesComboBox.getSelectedIndex() == 1) {
					if (value < 1)
						value = 32;
				}
				else {
					if (value == 0)
						decimalPlacesMinusButton.setEnabled(false);
					else
						decimalPlacesMinusButton.setEnabled(true);
				}
				decimalPlacesTextField.setText(Integer.toString(value));

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		decimalPlacesTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				if (decimalPlacesComboBox.getSelectedIndex() != 1) {
					if (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()) {
						if (tickSizeTextField.getText().length() > 0) {
							BigDecimal tickValue = new BigDecimal("0.001");
							int value = 0;
							try {
								String text = tickSizeTextField.getText();
								while (text.length() > 0 && text.charAt(text.length() - 1) == '0')
									text = text.substring(0, text.length() - 1);
								tickValue = new BigDecimal(text);
								value = Integer.parseInt(decimalPlacesTextField.getText());
							}
							catch (Exception e1) {
							}
							if (tickValue.scale() > value) {
								decimalPlacesWarningLabel.setIcon(bugIcon);
								decimalPlacesWarningLabel.setToolTipText("Precision is less than tick size.");
							}
							else {
								decimalPlacesWarningLabel.setIcon(null);
								decimalPlacesWarningLabel.setToolTipText(null);
							}
						}
						else {
							decimalPlacesWarningLabel.setIcon(bugIcon);
							decimalPlacesWarningLabel.setToolTipText("Tick size is empty.");
						}

					}
					else {
						decimalPlacesWarningLabel.setIcon(null);
						decimalPlacesWarningLabel.setToolTipText(null);
					}
				}
				else {
					Double fraction = getFractionTickSize();
					if (fraction != null) {
						String fractionText = decimalFormat.format(fraction);

						if (!tickSizeTextField.getText().equals(fractionText))
							tickSizeTextField.setText(fractionText);
					}

				}
				checkOkButton();
			}
		});

		decimalPlacesComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (decimalPlacesComboBox.getSelectedIndex() == 0) {
					incrementTextField.setText("0.001");
					tickSizeTextField.setText("0.001");
					decimalPlacesTextField.setText("3");
				}
				else
					decimalPlacesTextField.setText("32");

				checkBoxCheck();

			}
		});

		decimalPlacesMinusButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(decimalPlacesTextField.getText());
				}
				catch (Exception e1) {
				}
				if (decimalPlacesComboBox.getSelectedIndex() == 1) {
					if (value > 1)
						value = value / 2;
					decimalPlacesTextField.setText(Integer.toString(value));
				}
				else {
					if (value > 0)
						decimalPlacesTextField.setText(Integer.toString(value - 1));
				}
				if (value <= 1)
					decimalPlacesMinusButton.setEnabled(false);

			}
		});

		GridBagConstraints gbc_decimalPlacesPlusButton = new GridBagConstraints();
		gbc_decimalPlacesPlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_decimalPlacesPlusButton.gridx = 5;
		gbc_decimalPlacesPlusButton.gridy = 0;
		decimalPlacesPlusButton.setPreferredSize(new Dimension(25, 25));
		decimalPlacesPlusButton.setMinimumSize(new Dimension(25, 25));
		decimalPlacesPlusButton.setMargin(new Insets(2, 2, 2, 2));
		numberPanel.add(decimalPlacesPlusButton, gbc_decimalPlacesPlusButton);

		decimalPlacesPlusButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(decimalPlacesTextField.getText());
				}
				catch (NumberFormatException e1) {
					decimalPlacesTextField.setText("0");
				}
				if (decimalPlacesComboBox.getSelectedIndex() == 1) {
					if (value >= 1)
						value = value * 2;
					decimalPlacesTextField.setText(Integer.toString(value));
				}
				else {
					decimalPlacesTextField.setText(Integer.toString(value + 1));
				}

				if (value >= 0) {
					decimalPlacesMinusButton.setEnabled(true);
				}
			}
		});

		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		leadingZeroeslabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(leadingZeroeslabel, gbc_lblNewLabel_2);

		GridBagConstraints gbc_leadingZeroesMinusButton = new GridBagConstraints();
		gbc_leadingZeroesMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_leadingZeroesMinusButton.gridx = 2;
		gbc_leadingZeroesMinusButton.gridy = 1;
		leadingZeroesMinusButton.setPreferredSize(new Dimension(25, 25));
		leadingZeroesMinusButton.setMinimumSize(new Dimension(25, 25));
		leadingZeroesMinusButton.setMargin(new Insets(2, 2, 2, 2));
		leadingZeroesMinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(leadingZeroesMinusButton, gbc_leadingZeroesMinusButton);

		GridBagConstraints gbc_leadingZeroesTextField = new GridBagConstraints();
		gbc_leadingZeroesTextField.gridwidth = 2;
		gbc_leadingZeroesTextField.insets = new Insets(0, 0, 5, 0);
		gbc_leadingZeroesTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_leadingZeroesTextField.gridx = 3;
		gbc_leadingZeroesTextField.gridy = 1;

		leadingZeroesTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 0, 0, 0)));
		leadingZeroesTextField.setBackground(new Color(255, 243, 204));
		leadingZeroesTextField.setDocument(new PositiveIntegerDocument());
		leadingZeroesTextField.setText("1");
		leadingZeroesTextField.setHorizontalAlignment(JTextField.RIGHT);
		leadingZeroesTextField.setPreferredSize(new Dimension(100, 25));
		leadingZeroesTextField.setMinimumSize(new Dimension(100, 25));
		leadingZeroesTextField.setColumns(10);
		numberPanel.add(leadingZeroesTextField, gbc_leadingZeroesTextField);

		leadingZeroesTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(leadingZeroesTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0)
					leadingZeroesMinusButton.setEnabled(false);
				else
					leadingZeroesMinusButton.setEnabled(true);
				leadingZeroesTextField.setText(Integer.toString(value));

			}
		});

		leadingZeroesTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(leadingZeroesTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0)
					leadingZeroesMinusButton.setEnabled(false);
				else
					leadingZeroesMinusButton.setEnabled(true);
				leadingZeroesTextField.setText(Integer.toString(value));

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		leadingZeroesMinusButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(leadingZeroesTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value > 0)
					leadingZeroesTextField.setText(Integer.toString(value - 1));
				if (value <= 1)
					leadingZeroesMinusButton.setEnabled(false);

			}
		});

		GridBagConstraints gbc_leadingZeroesPlusButton = new GridBagConstraints();
		gbc_leadingZeroesPlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_leadingZeroesPlusButton.gridx = 5;
		gbc_leadingZeroesPlusButton.gridy = 1;
		leadingZeroesPlusButton.setPreferredSize(new Dimension(25, 25));
		leadingZeroesPlusButton.setMinimumSize(new Dimension(25, 25));
		leadingZeroesPlusButton.setMargin(new Insets(2, 2, 2, 2));
		numberPanel.add(leadingZeroesPlusButton, gbc_leadingZeroesPlusButton);

		leadingZeroesPlusButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int value = 0;
				try {
					value = Integer.parseInt(leadingZeroesTextField.getText());
				}
				catch (NumberFormatException e1) {
					leadingZeroesTextField.setText("0");
				}
				leadingZeroesTextField.setText(Integer.toString(value + 1));
				if (value >= 0) {
					leadingZeroesMinusButton.setEnabled(true);
				}
			}
		});

		GridBagConstraints gbc_roundingModeLabel = new GridBagConstraints();
		gbc_roundingModeLabel.anchor = GridBagConstraints.WEST;
		gbc_roundingModeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_roundingModeLabel.gridx = 0;
		gbc_roundingModeLabel.gridy = 2;
		roundingModeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(roundingModeLabel, gbc_roundingModeLabel);

		roundingModeComboBox = new JComboBox(new String[] { "Up", "Down", "Ceiling", "Floor", "Half Up", "Half Down", "Half Even" });
		roundingModeComboBox.setSelectedIndex(6);
		roundingModeComboBox.setBackground(new Color(255, 243, 204));
		roundingModeComboBox.setMinimumSize(new Dimension(32, 25));
		roundingModeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		roundingModeComboBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_roundingModeComboBox = new GridBagConstraints();
		gbc_roundingModeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_roundingModeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_roundingModeComboBox.gridx = 2;
		gbc_roundingModeComboBox.gridwidth = 4;
		gbc_roundingModeComboBox.gridy = 2;
		roundingModeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(roundingModeComboBox, gbc_roundingModeComboBox);

		GridBagConstraints gbc_negaiveNumbersRedLabel = new GridBagConstraints();
		gbc_negaiveNumbersRedLabel.anchor = GridBagConstraints.WEST;
		gbc_negaiveNumbersRedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_negaiveNumbersRedLabel.gridx = 0;
		gbc_negaiveNumbersRedLabel.gridy = 3;
		negaiveNumbersRedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(negaiveNumbersRedLabel, gbc_negaiveNumbersRedLabel);

		GridBagConstraints gbc_negativeNumbersRedCheckBox = new GridBagConstraints();
		gbc_negativeNumbersRedCheckBox.gridwidth = 4;
		gbc_negativeNumbersRedCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_negativeNumbersRedCheckBox.anchor = GridBagConstraints.WEST;
		gbc_negativeNumbersRedCheckBox.gridx = 2;
		gbc_negativeNumbersRedCheckBox.gridy = 3;
		negativeNumbersRedCheckBox.setOpaque(false);
		numberPanel.add(negativeNumbersRedCheckBox, gbc_negativeNumbersRedCheckBox);

		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 4;
		thousandsSeparatorLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(thousandsSeparatorLabel, gbc_lblNewLabel_3);

		GridBagConstraints gbc_thousandsSeparatorCheckBox = new GridBagConstraints();
		gbc_thousandsSeparatorCheckBox.gridwidth = 4;
		gbc_thousandsSeparatorCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_thousandsSeparatorCheckBox.anchor = GridBagConstraints.WEST;
		gbc_thousandsSeparatorCheckBox.gridx = 2;
		gbc_thousandsSeparatorCheckBox.gridy = 4;
		thousandsSeparatorCheckBox.setOpaque(false);
		numberPanel.add(thousandsSeparatorCheckBox, gbc_thousandsSeparatorCheckBox);

		GridBagConstraints gbc_spinnerLabel = new GridBagConstraints();
		gbc_spinnerLabel.anchor = GridBagConstraints.WEST;
		gbc_spinnerLabel.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerLabel.gridx = 0;
		gbc_spinnerLabel.gridy = 5;
		spinnerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(spinnerLabel, gbc_spinnerLabel);

		GridBagConstraints gbc_spinnerCheckBox = new GridBagConstraints();
		gbc_spinnerCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerCheckBox.gridwidth = 4;
		gbc_spinnerCheckBox.anchor = GridBagConstraints.WEST;
		gbc_spinnerCheckBox.gridx = 2;
		gbc_spinnerCheckBox.gridy = 5;
		spinnerCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinnerCheckBox.setOpaque(false);
		numberPanel.add(spinnerCheckBox, gbc_spinnerCheckBox);

		spinnerCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (spinnerCheckBox.isSelected())
					solverCheckBox.setSelected(false);
				checkBoxCheck();

			}
		});

		GridBagConstraints gbc_solverLabel = new GridBagConstraints();
		gbc_solverLabel.anchor = GridBagConstraints.WEST;
		gbc_solverLabel.insets = new Insets(0, 0, 5, 5);
		gbc_solverLabel.gridx = 0;
		gbc_solverLabel.gridy = 6;
		solverLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberPanel.add(solverLabel, gbc_solverLabel);

		GridBagConstraints gbc_solverCheckBox = new GridBagConstraints();
		gbc_solverCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_solverCheckBox.gridwidth = 4;
		gbc_solverCheckBox.anchor = GridBagConstraints.WEST;
		gbc_solverCheckBox.gridx = 2;
		gbc_solverCheckBox.gridy = 6;
		solverCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		solverCheckBox.setOpaque(false);
		numberPanel.add(solverCheckBox, gbc_solverCheckBox);

		solverCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (solverCheckBox.isSelected())
					spinnerCheckBox.setSelected(false);
				checkBoxCheck();

			}
		});

		GridBagConstraints gbc_adjustCellLabel = new GridBagConstraints();
		gbc_adjustCellLabel.anchor = GridBagConstraints.WEST;
		gbc_adjustCellLabel.insets = new Insets(0, 0, 5, 5);
		gbc_adjustCellLabel.gridx = 0;
		gbc_adjustCellLabel.gridy = 7;
		adjustCellLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		adjustCellLabel.setEnabled(false);
		numberPanel.add(adjustCellLabel, gbc_adjustCellLabel);

		GridBagConstraints gbc_adjustCellWarningLabel = new GridBagConstraints();
		gbc_adjustCellWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_adjustCellWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_adjustCellWarningLabel.gridx = 1;
		gbc_adjustCellWarningLabel.gridy = 7;
		adjustCellWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(adjustCellWarningLabel, gbc_adjustCellWarningLabel);

		GridBagConstraints gbc_adjustCellTextField = new GridBagConstraints();
		gbc_adjustCellTextField.gridwidth = 4;
		gbc_adjustCellTextField.insets = new Insets(0, 0, 5, 0);
		gbc_adjustCellTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_adjustCellTextField.gridx = 2;
		gbc_adjustCellTextField.gridy = 7;

		adjustCellTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		adjustCellTextField.setEnabled(false);
		adjustCellTextField.setBackground(new Color(204, 216, 255));
		adjustCellTextField.setText("A1");
		numberPanel.add(adjustCellTextField, gbc_adjustCellTextField);

		adjustCellTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				String text = adjustCellTextField.getText();

				if (text.length() > 0) {

					StringBuffer columnName = new StringBuffer();

					String columnNumber = null;

					if (text.startsWith("$")) {
						text = text.substring(1);
					}

					try {
						while (text.length() > 0) {

							if (Character.isDigit(text.charAt(0))) {
								columnNumber = text;
								text = " ";
							}
							else if (text.charAt(0) != '$') {
								if ((text.charAt(0) >= 'a' && text.charAt(0) <= 'z') || (text.charAt(0) >= 'A' && text.charAt(0) <= 'Z'))
									columnName.append(text.charAt(0));
								else
									throw new Exception();
							}
							else {
								text = text.substring(1);
								columnNumber = text;
								text = " ";
							}

							text = text.substring(1);
						}

						Integer.parseInt(columnNumber);
						if (CellReference.convertColStringToIndex(columnName.toString()) < 0
								|| CellReference.convertColStringToIndex(columnName.toString()) >= 250)
							throw new Exception();

						adjustCellWarningLabel.setIcon(null);
						adjustCellWarningLabel.setToolTipText(null);
					}
					catch (Exception e1) {
						adjustCellWarningLabel.setIcon(bugIcon);
						adjustCellWarningLabel.setToolTipText("Invalid cell name.");
					}

				}
				else {

					adjustCellWarningLabel.setIcon(bugIcon);
					adjustCellWarningLabel.setToolTipText("No cell defined");
				}

				checkOkButton();
			}
		});

		GridBagConstraints gbc_toleranceCellLabel = new GridBagConstraints();
		gbc_toleranceCellLabel.anchor = GridBagConstraints.WEST;
		gbc_toleranceCellLabel.insets = new Insets(0, 0, 5, 5);
		gbc_toleranceCellLabel.gridx = 0;
		gbc_toleranceCellLabel.gridy = 8;
		toleranceCellLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		toleranceCellLabel.setEnabled(false);
		numberPanel.add(toleranceCellLabel, gbc_toleranceCellLabel);

		GridBagConstraints gbc_toleranceWarningLabel = new GridBagConstraints();
		gbc_toleranceWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_toleranceWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_toleranceWarningLabel.gridx = 1;
		gbc_toleranceWarningLabel.gridy = 8;
		toleranceWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(toleranceWarningLabel, gbc_toleranceWarningLabel);

		GridBagConstraints gbc_toleranceCellTextField = new GridBagConstraints();
		gbc_toleranceCellTextField.gridwidth = 4;
		gbc_toleranceCellTextField.insets = new Insets(0, 0, 5, 0);
		gbc_toleranceCellTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_toleranceCellTextField.gridx = 2;
		gbc_toleranceCellTextField.gridy = 8;

		toleranceCellTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		toleranceCellTextField.setEnabled(false);
		toleranceCellTextField.setBackground(new Color(204, 216, 255));
		toleranceCellTextField.setDocument(new PositiveDoubleDocument());
		toleranceCellTextField.setText("0.001");
		numberPanel.add(toleranceCellTextField, gbc_toleranceCellTextField);

		toleranceCellTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				String text = toleranceCellTextField.getText();
				if (text.length() > 0) {
					BigDecimal tickValue = new BigDecimal(text);
					if (tickValue.doubleValue() < 0) {
						toleranceWarningLabel.setIcon(bugIcon);
						toleranceWarningLabel.setToolTipText("Tolerance must be greater than or equal to zero.");
					}
					else {
						toleranceWarningLabel.setIcon(null);
						toleranceWarningLabel.setToolTipText(null);
					}

				}
				else {
					toleranceWarningLabel.setIcon(bugIcon);
					toleranceWarningLabel.setToolTipText("Tolerance is empty.");
				}

				checkOkButton();
			}
		});

		toleranceCellTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				Double value = null;
				try {
					value = Double.parseDouble(tickSizeTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == null) {
					tickSizeTextField.setText("0.001");
				}

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		tickSizeTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Double value = null;
				try {
					value = Double.parseDouble(tickSizeTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == null) {
					tickSizeTextField.setText("0.001");
				}
			}
		});

		//

		GridBagConstraints gbc_labelTickSize = new GridBagConstraints();
		gbc_labelTickSize.anchor = GridBagConstraints.WEST;
		gbc_labelTickSize.insets = new Insets(0, 0, 5, 5);
		gbc_labelTickSize.gridx = 0;
		gbc_labelTickSize.gridy = 9;
		tickSizeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		tickSizeLabel.setEnabled(false);
		numberPanel.add(tickSizeLabel, gbc_labelTickSize);

		GridBagConstraints gbc_tickSizeWarningLabel = new GridBagConstraints();
		gbc_tickSizeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_tickSizeWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_tickSizeWarningLabel.gridx = 1;
		gbc_tickSizeWarningLabel.gridy = 9;
		tickSizeWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(tickSizeWarningLabel, gbc_tickSizeWarningLabel);

		GridBagConstraints gbc_tickSizeTextField = new GridBagConstraints();
		gbc_tickSizeTextField.gridwidth = 4;
		gbc_tickSizeTextField.insets = new Insets(0, 0, 5, 0);
		gbc_tickSizeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_tickSizeTextField.gridx = 2;
		gbc_tickSizeTextField.gridy = 9;

		tickSizeTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		tickSizeTextField.setEnabled(false);
		tickSizeTextField.setBackground(new Color(204, 216, 255));
		tickSizeTextField.setDocument(new PositiveDoubleDocument());
		tickSizeTextField.setText("0.001");
		numberPanel.add(tickSizeTextField, gbc_tickSizeTextField);

		tickSizeTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				if (decimalPlacesComboBox.getSelectedIndex() != 1) {
					String text = tickSizeTextField.getText();
					while (text.length() > 0 && text.charAt(text.length() - 1) == '0')
						text = text.substring(0, text.length() - 1);
					if (text.length() > 0) {
						BigDecimal tickValue = null;
						try {
							tickValue = new BigDecimal(text);
						}
						catch (Exception exception) {
						}
						if (tickValue == null) {
							tickSizeWarningLabel.setIcon(bugIcon);
							tickSizeWarningLabel.setToolTipText("Invalid tick size.");
						}
						else if (tickValue.doubleValue() == 0) {
							tickSizeWarningLabel.setIcon(bugIcon);
							tickSizeWarningLabel.setToolTipText("Tick size must be greater than zero.");
						}
						else {
							decimalPlacesTextField.setText(Integer.toString(tickValue.scale()));
							tickSizeWarningLabel.setIcon(null);
							tickSizeWarningLabel.setToolTipText(null);
						}
						incrementTextField.setText(tickSizeTextField.getText());
					}
					else {
						tickSizeWarningLabel.setIcon(bugIcon);
						tickSizeWarningLabel.setToolTipText("Tick size is empty.");
					}
				}
				else {

					Double fraction = getFractionTickSize();

					if (fraction != null) {
						try {
//							BigDecimal increment = new BigDecimal(incrementTextField.getText());
//							increment = increment.divide(new BigDecimal(decimalFormat.format(fraction)), 32, RoundingMode.HALF_EVEN);
//							if (increment.doubleValue() != increment.intValue())
								incrementTextField.setText(decimalFormat.format(fraction));
						}
						catch (Exception e1) {
							incrementTextField.setText(decimalFormat.format(fraction));
						}
					}
				}

				checkOkButton();
			}
		});

		tickSizeTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				double value = 0;
				try {
					value = Double.parseDouble(tickSizeTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0) {
					tickSizeTextField.setText("0.001");
				}

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		tickSizeTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				double value = 0;
				try {
					value = Double.parseDouble(tickSizeTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0) {
					tickSizeTextField.setText("0.001");
				}
			}
		});

		GridBagConstraints gbc_incrementLabel = new GridBagConstraints();
		gbc_incrementLabel.anchor = GridBagConstraints.WEST;
		gbc_incrementLabel.insets = new Insets(0, 0, 5, 5);
		gbc_incrementLabel.gridx = 0;
		gbc_incrementLabel.gridy = 10;
		incrementLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		incrementLabel.setEnabled(false);
		numberPanel.add(incrementLabel, gbc_incrementLabel);

		GridBagConstraints gbc_incrementWarningLabel = new GridBagConstraints();
		gbc_incrementWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_incrementWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_incrementWarningLabel.gridx = 1;
		gbc_incrementWarningLabel.gridy = 10;
		incrementWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(incrementWarningLabel, gbc_incrementWarningLabel);

		GridBagConstraints gbc_incrementTextField = new GridBagConstraints();
		gbc_incrementTextField.gridwidth = 4;
		gbc_incrementTextField.insets = new Insets(0, 0, 5, 0);
		gbc_incrementTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_incrementTextField.gridx = 2;
		gbc_incrementTextField.gridy = 10;

		incrementTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		incrementTextField.setEnabled(false);
		incrementTextField.setDocument(new PositiveDoubleDocument());
		incrementTextField.setText("0.001");
		incrementTextField.setBackground(new Color(204, 216, 255));
		numberPanel.add(incrementTextField, gbc_incrementTextField);

		incrementTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				if (incrementTextField.getText().length() > 0) {
					BigDecimal incrementValue = new BigDecimal(incrementTextField.getText());
					if (incrementValue.doubleValue() == 0) {
						incrementWarningLabel.setIcon(bugIcon);
						incrementWarningLabel.setToolTipText("Increment must be greater than zero.");
					}
					else {
						try {
							BigDecimal tickSizeValue = new BigDecimal(tickSizeTextField.getText());
							if (tickSizeValue.doubleValue() != 0 && incrementValue.remainder(tickSizeValue).doubleValue() != 0) {
								incrementWarningLabel.setIcon(bugIcon);
								incrementWarningLabel.setToolTipText("Increment must be a multiple of tick size.");
							}
							else {
								incrementWarningLabel.setIcon(null);
								incrementWarningLabel.setToolTipText(null);
							}
						}
						catch (Exception e1) {
						}
					}
				}
				else {
					incrementWarningLabel.setIcon(bugIcon);
					incrementWarningLabel.setToolTipText("Increment value is empty.");
				}

				checkOkButton();
			}
		});

		incrementTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				double value = 0;
				try {
					value = Double.parseDouble(incrementTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0) {
					incrementTextField.setText("0.001");
				}

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		incrementTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				double value = 0;
				try {
					value = Double.parseDouble(incrementTextField.getText());
				}
				catch (NumberFormatException e1) {
				}
				if (value == 0) {
					incrementTextField.setText("0.001");
				}
			}
		});

		GridBagConstraints gbc_minValueLabel = new GridBagConstraints();
		gbc_minValueLabel.anchor = GridBagConstraints.WEST;
		gbc_minValueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_minValueLabel.gridx = 0;
		gbc_minValueLabel.gridy = 11;
		minValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		minValueLabel.setEnabled(false);
		numberPanel.add(minValueLabel, gbc_minValueLabel);

		GridBagConstraints gbc_minimumValueWarningLabel = new GridBagConstraints();
		gbc_minimumValueWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_minimumValueWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_minimumValueWarningLabel.gridx = 1;
		gbc_minimumValueWarningLabel.gridy = 11;
		minimumValueWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(minimumValueWarningLabel, gbc_minimumValueWarningLabel);

		GridBagConstraints gbc_minValueTextField = new GridBagConstraints();
		gbc_minValueTextField.gridwidth = 4;
		gbc_minValueTextField.insets = new Insets(0, 0, 5, 0);
		gbc_minValueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_minValueTextField.gridx = 2;
		gbc_minValueTextField.gridy = 11;
		minValueTextField.setMinimumSize(new Dimension(4, 25));
		minValueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		minValueTextField.setPreferredSize(new Dimension(4, 25));
		minValueTextField.setColumns(10);
		minValueTextField.setDocument(new DoubleDocument());
		minValueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		minValueTextField.setEnabled(false);
		minValueTextField.setBackground(new Color(204, 216, 255));
		numberPanel.add(minValueTextField, gbc_minValueTextField);

		minValueTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				if (maximumValueTextField.getText().length() > 0 && minValueTextField.getText().length() > 0) {
					Double minValue = Double.parseDouble(minValueTextField.getText());
					Double maxValue = Double.parseDouble(maximumValueTextField.getText());
					if (minValue >= maxValue) {
						maximumValueWarningLabel.setIcon(bugIcon);
						maximumValueWarningLabel.setToolTipText("Maximum value must be greater than minimum value.");
					}
				}
				else {
					maximumValueTextField.setText(maximumValueTextField.getText());
				}

				minimumValueWarningLabel.setIcon(null);
				minimumValueWarningLabel.setToolTipText(null);

				if (minValueTextField.getText().length() > 0) {
					BigDecimal maximumValue = new BigDecimal(minValueTextField.getText());
					if (maximumValue.doubleValue() != 0) {
						BigDecimal tickSizeValue = new BigDecimal(tickSizeTextField.getText());
						if (tickSizeValue.doubleValue() != 0 && maximumValue.remainder(tickSizeValue).doubleValue() != 0) {
							minimumValueWarningLabel.setIcon(bugIcon);
							minimumValueWarningLabel.setToolTipText("Minimum value be a multiple of tick size.");
						}
					}
				}

				checkOkButton();
			}
		});

		GridBagConstraints gbc_maximumValueLabel = new GridBagConstraints();
		gbc_maximumValueLabel.anchor = GridBagConstraints.WEST;
		gbc_maximumValueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maximumValueLabel.gridx = 0;
		gbc_maximumValueLabel.gridy = 12;
		maximumValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		maximumValueLabel.setEnabled(false);
		numberPanel.add(maximumValueLabel, gbc_maximumValueLabel);

		GridBagConstraints gbc_maximumValueWarningLabel = new GridBagConstraints();
		gbc_maximumValueWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maximumValueWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_maximumValueWarningLabel.gridx = 1;
		gbc_maximumValueWarningLabel.gridy = 12;
		maximumValueWarningLabel.setPreferredSize(new Dimension(20, 20));
		numberPanel.add(maximumValueWarningLabel, gbc_maximumValueWarningLabel);

		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 4;
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 12;
		maximumValueTextField.setMinimumSize(new Dimension(4, 25));
		maximumValueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		maximumValueTextField.setPreferredSize(new Dimension(4, 25));
		maximumValueTextField.setColumns(10);
		maximumValueTextField.setDocument(new DoubleDocument());
		maximumValueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 0, 0, 5)));
		maximumValueTextField.setBackground(new Color(204, 216, 255));
		maximumValueTextField.setEnabled(false);
		numberPanel.add(maximumValueTextField, gbc_textField_1);

		maximumValueTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				maximumValueWarningLabel.setIcon(null);
				maximumValueWarningLabel.setToolTipText(null);

				if (maximumValueTextField.getText().length() > 0 && minValueTextField.getText().length() > 0) {
					Double minValue = Double.parseDouble(minValueTextField.getText());
					Double maxValue = Double.parseDouble(maximumValueTextField.getText());
					if (minValue >= maxValue) {
						maximumValueWarningLabel.setIcon(bugIcon);
						maximumValueWarningLabel.setToolTipText("Maximum value must be greater than minimum value.");
					}
				}

				if (maximumValueTextField.getText().length() > 0) {
					BigDecimal maximumValue = new BigDecimal(maximumValueTextField.getText());
					if (maximumValue.doubleValue() != 0) {
						BigDecimal tickSizeValue = new BigDecimal(tickSizeTextField.getText());
						if (tickSizeValue.doubleValue() != 0 && maximumValue.remainder(tickSizeValue).doubleValue() != 0) {
							maximumValueWarningLabel.setIcon(bugIcon);
							maximumValueWarningLabel.setToolTipText("Maximum value be a multiple of tick size.");
						}
					}
				}

				checkOkButton();

			}
		});

		GridBagConstraints gbc_styleLabel = new GridBagConstraints();
		gbc_styleLabel.anchor = GridBagConstraints.WEST;
		gbc_styleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_styleLabel.gridx = 0;
		gbc_styleLabel.gridy = 13;
		styleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		styleLabel.setEnabled(false);
		numberPanel.add(styleLabel, gbc_styleLabel);

		GridBagConstraints gbc_spinner1RadioButton = new GridBagConstraints();
		gbc_spinner1RadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner1RadioButton.gridx = 2;
		gbc_spinner1RadioButton.gridy = 13;
		spinner1RadioButton.setOpaque(false);
		spinner1RadioButton.setEnabled(false);
		numberPanel.add(spinner1RadioButton, gbc_spinner1RadioButton);

		GridBagConstraints gbc_spinner1MinusButton = new GridBagConstraints();
		gbc_spinner1MinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner1MinusButton.gridx = 3;
		gbc_spinner1MinusButton.gridy = 13;
		spinner1MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner1MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner1MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner1MinusButton.setMinimumSize(new Dimension(25, 25));
		spinner1MinusButton.setEnabled(false);
		numberPanel.add(spinner1MinusButton, gbc_spinner1MinusButton);

		GridBagConstraints gbc_spinner1TextField = new GridBagConstraints();
		gbc_spinner1TextField.insets = new Insets(0, 0, 5, 0);
		gbc_spinner1TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner1TextField.gridx = 4;
		gbc_spinner1TextField.gridy = 13;

		spinner1TextField.setBorder(new CompoundBorder(new MatteBorder(new Insets(1, 0, 1, 0), (new Color(204, 216, 255).darker())),
				new EmptyBorder(5, 0, 0, 0)));
		spinner1TextField.setBackground(new Color(204, 216, 255));
		spinner1TextField.setEnabled(false);
		numberPanel.add(spinner1TextField, gbc_spinner1TextField);

		GridBagConstraints gbc_spinner1PlusButton = new GridBagConstraints();
		gbc_spinner1PlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner1PlusButton.gridx = 5;
		gbc_spinner1PlusButton.gridy = 13;
		spinner1PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner1PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner1PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner1PlusButton.setEnabled(false);
		numberPanel.add(spinner1PlusButton, gbc_spinner1PlusButton);

		GridBagConstraints gbc_spinner2RadioButton = new GridBagConstraints();
		gbc_spinner2RadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner2RadioButton.gridx = 2;
		gbc_spinner2RadioButton.gridy = 14;
		spinner2RadioButton.setOpaque(false);
		spinner2RadioButton.setEnabled(false);
		numberPanel.add(spinner2RadioButton, gbc_spinner2RadioButton);

		GridBagConstraints gbc_spinner2MinusButton = new GridBagConstraints();
		gbc_spinner2MinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner2MinusButton.gridx = 3;
		gbc_spinner2MinusButton.gridy = 14;
		spinner2MinusButton.setIcon(blueLeftIcon);
		spinner2MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner2MinusButton.setMinimumSize(new Dimension(25, 25));
		spinner2MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner2MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner2MinusButton.setEnabled(false);
		numberPanel.add(spinner2MinusButton, gbc_spinner2MinusButton);

		GridBagConstraints gbc_spinner2TextField = new GridBagConstraints();
		gbc_spinner2TextField.insets = new Insets(0, 0, 5, 0);
		gbc_spinner2TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner2TextField.gridx = 4;
		gbc_spinner2TextField.gridy = 14;

		spinner2TextField.setBorder(new CompoundBorder(new MatteBorder(new Insets(1, 0, 1, 0), (new Color(204, 216, 255).darker())),
				new EmptyBorder(5, 0, 0, 0)));
		spinner2TextField.setBackground(new Color(204, 216, 255));
		spinner2TextField.setEnabled(false);
		spinner2TextField.setPreferredSize(new Dimension(100, 25));
		spinner2TextField.setMinimumSize(new Dimension(100, 25));
		spinner2TextField.setColumns(10);
		numberPanel.add(spinner2TextField, gbc_spinner2TextField);

		GridBagConstraints gbc_spinner2PlusButton = new GridBagConstraints();
		gbc_spinner2PlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner2PlusButton.gridx = 5;
		gbc_spinner2PlusButton.gridy = 14;
		spinner2PlusButton.setIcon(blueRightIcon);
		spinner2PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner2PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner2PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner2PlusButton.setEnabled(false);
		numberPanel.add(spinner2PlusButton, gbc_spinner2PlusButton);

		GridBagConstraints gbc_spinner3RadioButton = new GridBagConstraints();
		gbc_spinner3RadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner3RadioButton.gridx = 2;
		gbc_spinner3RadioButton.gridy = 15;
		spinner3RadioButton.setOpaque(false);
		spinner3RadioButton.setEnabled(false);
		numberPanel.add(spinner3RadioButton, gbc_spinner3RadioButton);

		GridBagConstraints gbc_spinner3MinusButton = new GridBagConstraints();
		gbc_spinner3MinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner3MinusButton.gridx = 3;
		gbc_spinner3MinusButton.gridy = 15;
		spinner3MinusButton.setIcon(blueDownIcon);
		spinner3MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner3MinusButton.setMinimumSize(new Dimension(25, 25));
		spinner3MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner3MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner3MinusButton.setEnabled(false);
		numberPanel.add(spinner3MinusButton, gbc_spinner3MinusButton);

		GridBagConstraints gbc_spinner3TextField = new GridBagConstraints();
		gbc_spinner3TextField.insets = new Insets(0, 0, 5, 0);
		gbc_spinner3TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner3TextField.gridx = 4;
		gbc_spinner3TextField.gridy = 15;

		spinner3TextField.setBorder(new CompoundBorder(new MatteBorder(new Insets(1, 0, 1, 0), (new Color(204, 216, 255).darker())),
				new EmptyBorder(5, 0, 0, 0)));
		spinner3TextField.setBackground(new Color(204, 216, 255));
		spinner3TextField.setEnabled(false);
		spinner3TextField.setPreferredSize(new Dimension(100, 25));
		spinner3TextField.setMinimumSize(new Dimension(100, 25));
		spinner3TextField.setColumns(10);
		numberPanel.add(spinner3TextField, gbc_spinner3TextField);

		GridBagConstraints gbc_spinner3PlusButton = new GridBagConstraints();
		gbc_spinner3PlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner3PlusButton.gridx = 5;
		gbc_spinner3PlusButton.gridy = 15;
		spinner3PlusButton.setIcon(blueUpIcon);
		spinner3PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner3PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner3PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner3PlusButton.setEnabled(false);
		numberPanel.add(spinner3PlusButton, gbc_spinner3PlusButton);

		GridBagConstraints gbc_spinner4RadioButton = new GridBagConstraints();
		gbc_spinner4RadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner4RadioButton.gridx = 2;
		gbc_spinner4RadioButton.gridy = 16;
		spinner4RadioButton.setOpaque(false);
		spinner4RadioButton.setEnabled(false);
		numberPanel.add(spinner4RadioButton, gbc_spinner4RadioButton);

		GridBagConstraints gbc_spinner4MinusButton = new GridBagConstraints();
		gbc_spinner4MinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner4MinusButton.gridx = 3;
		gbc_spinner4MinusButton.gridy = 16;
		spinner4MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner4MinusButton.setIcon(redDownIcon);
		spinner4MinusButton.setMinimumSize(new Dimension(25, 25));
		spinner4MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner4MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner4MinusButton.setEnabled(false);
		numberPanel.add(spinner4MinusButton, gbc_spinner4MinusButton);

		GridBagConstraints gbc_spinner4TextField = new GridBagConstraints();
		gbc_spinner4TextField.insets = new Insets(0, 0, 5, 0);
		gbc_spinner4TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner4TextField.gridx = 4;
		gbc_spinner4TextField.gridy = 16;

		spinner4TextField.setBorder(new CompoundBorder(new MatteBorder(new Insets(1, 0, 1, 0), (new Color(204, 216, 255).darker())),
				new EmptyBorder(5, 0, 0, 0)));
		spinner4TextField.setBackground(new Color(204, 216, 255));
		spinner4TextField.setEnabled(false);
		spinner4TextField.setPreferredSize(new Dimension(100, 25));
		spinner4TextField.setMinimumSize(new Dimension(100, 25));
		spinner4TextField.setColumns(10);
		numberPanel.add(spinner4TextField, gbc_spinner4TextField);

		GridBagConstraints gbc_spinner4PlusButton = new GridBagConstraints();
		gbc_spinner4PlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_spinner4PlusButton.gridx = 5;
		gbc_spinner4PlusButton.gridy = 16;
		spinner4PlusButton.setIcon(greenUpIcon);
		spinner4PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner4PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner4PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner4PlusButton.setEnabled(false);
		numberPanel.add(spinner4PlusButton, gbc_spinner4PlusButton);

		GridBagConstraints gbc_spinner5RadioButton = new GridBagConstraints();
		gbc_spinner5RadioButton.gridx = 2;
		gbc_spinner5RadioButton.gridy = 17;
		spinner5RadioButton.setOpaque(false);
		spinner5RadioButton.setEnabled(false);
		numberPanel.add(spinner5RadioButton, gbc_spinner5RadioButton);

		GridBagConstraints gbc_spinner5MinusButton = new GridBagConstraints();
		gbc_spinner5MinusButton.gridx = 3;
		gbc_spinner5MinusButton.gridy = 17;
		spinner5MinusButton.setIcon(greenDownIcon);
		spinner5MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner5MinusButton.setMinimumSize(new Dimension(25, 25));
		spinner5MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner5MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner5MinusButton.setEnabled(false);
		numberPanel.add(spinner5MinusButton, gbc_spinner5MinusButton);

		GridBagConstraints gbc_spinner5TextField = new GridBagConstraints();
		gbc_spinner5TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner5TextField.gridx = 4;
		gbc_spinner5TextField.gridy = 17;

		spinner5TextField.setBorder(new CompoundBorder(new MatteBorder(new Insets(1, 0, 1, 0), (new Color(204, 216, 255).darker())),
				new EmptyBorder(5, 0, 0, 0)));
		spinner5TextField.setBackground(new Color(204, 216, 255));
		spinner5TextField.setEnabled(false);
		spinner5TextField.setPreferredSize(new Dimension(100, 25));
		spinner5TextField.setMinimumSize(new Dimension(100, 25));
		spinner5TextField.setColumns(10);
		numberPanel.add(spinner5TextField, gbc_spinner5TextField);

		GridBagConstraints gbc_spinner5PlusButton = new GridBagConstraints();
		gbc_spinner5PlusButton.gridx = 5;
		gbc_spinner5PlusButton.gridy = 17;
		spinner5PlusButton.setIcon(redUpIcon);
		spinner5PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner5PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner5PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner5PlusButton.setEnabled(false);
		numberPanel.add(spinner5PlusButton, gbc_spinner5PlusButton);
		booleanPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
		booleanPanel.setOpaque(false);

		ButtonGroup numberGroup = new ButtonGroup();
		numberGroup.add(spinner1RadioButton);
		numberGroup.add(spinner2RadioButton);
		numberGroup.add(spinner3RadioButton);
		numberGroup.add(spinner4RadioButton);
		numberGroup.add(spinner5RadioButton);

		spinner1RadioButton.setSelected(true);

		cardPanel.add(booleanPanel, "boolean");
		GridBagLayout gbl_booleanPanel = new GridBagLayout();
		gbl_booleanPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_booleanPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_booleanPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_booleanPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		booleanPanel.setLayout(gbl_booleanPanel);

		GridBagConstraints gbc_trueLabel = new GridBagConstraints();
		gbc_trueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_trueLabel.anchor = GridBagConstraints.WEST;
		gbc_trueLabel.gridx = 0;
		gbc_trueLabel.gridy = 0;
		trueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(trueLabel, gbc_trueLabel);

		GridBagConstraints gbc_trueWarningLabel = new GridBagConstraints();
		gbc_trueWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_trueWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_trueWarningLabel.gridx = 1;
		gbc_trueWarningLabel.gridy = 0;
		trueWarningLabel.setPreferredSize(new Dimension(20, 25));
		booleanPanel.add(trueWarningLabel, gbc_trueWarningLabel);

		GridBagConstraints gbc_trueTextField = new GridBagConstraints();
		gbc_trueTextField.insets = new Insets(0, 0, 5, 0);
		gbc_trueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_trueTextField.gridx = 2;
		gbc_trueTextField.gridy = 0;

		trueTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		trueTextField.setBackground(new Color(255, 243, 204));
		trueTextField.setPreferredSize(new Dimension(4, 25));
		trueTextField.setColumns(10);
		trueTextField.setText("True");

		trueTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				trueWarningLabel.setIcon(null);
				trueWarningLabel.setToolTipText(null);

				if (trueTextField.getText().trim().length() == 0) {
					trueWarningLabel.setIcon(bugIcon);
					trueWarningLabel.setToolTipText("True presentation value is empty.");
				}
				else if (trueTextField.getText().trim().equals(falseTextField.getText().trim())) {
					trueWarningLabel.setIcon(bugIcon);
					trueWarningLabel.setToolTipText("True presentation value equals False presentation value.");
				}

				checkOkButton();
			}
		});

		booleanPanel.add(trueTextField, gbc_trueTextField);

		GridBagConstraints gbc_falseLabel = new GridBagConstraints();
		gbc_falseLabel.anchor = GridBagConstraints.WEST;
		gbc_falseLabel.insets = new Insets(0, 0, 5, 5);
		gbc_falseLabel.gridx = 0;
		gbc_falseLabel.gridy = 1;
		falseLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(falseLabel, gbc_falseLabel);

		GridBagConstraints gbc_falseWarningLabel = new GridBagConstraints();
		gbc_falseWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_falseWarningLabel.anchor = GridBagConstraints.EAST;
		gbc_falseWarningLabel.gridx = 1;
		gbc_falseWarningLabel.gridy = 1;
		falseWarningLabel.setPreferredSize(new Dimension(20, 25));
		booleanPanel.add(falseWarningLabel, gbc_falseWarningLabel);

		GridBagConstraints gbc_falseTextField = new GridBagConstraints();
		gbc_falseTextField.insets = new Insets(0, 0, 5, 0);
		gbc_falseTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_falseTextField.gridx = 2;
		gbc_falseTextField.gridy = 1;

		falseTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		falseTextField.setBackground(new Color(255, 243, 204));
		falseTextField.setPreferredSize(new Dimension(4, 25));
		falseTextField.setColumns(10);
		falseTextField.setText("False");
		booleanPanel.add(falseTextField, gbc_falseTextField);

		falseTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				falseWarningLabel.setIcon(null);
				falseWarningLabel.setToolTipText(null);

				if (falseTextField.getText().trim().length() == 0) {
					falseWarningLabel.setIcon(bugIcon);
					falseWarningLabel.setToolTipText("False presentation value is empty.");
				}
				else if (falseTextField.getText().trim().equals(trueTextField.getText().trim())) {
					falseWarningLabel.setIcon(bugIcon);
					falseWarningLabel.setToolTipText("False presentation value equals True presentation value.");
				}

				checkOkButton();
			}
		});

		ActionListener booleanActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!plainBooleanRadioButton.isSelected()) {
					spinnerCheckBox.setSelected(false);
					solverCheckBox.setSelected(false);
					predefinedTextCheckBox.setSelected(false);
					plainDateRadioButton.setSelected(true);
					plainTimeRadioButton.setSelected(true);
					checkBoxCheck();
				}
			}

		};

		GridBagConstraints gbc_plainBooleanLabel = new GridBagConstraints();
		gbc_plainBooleanLabel.anchor = GridBagConstraints.WEST;
		gbc_plainBooleanLabel.insets = new Insets(0, 0, 5, 5);
		gbc_plainBooleanLabel.gridx = 0;
		gbc_plainBooleanLabel.gridy = 2;
		plainBooleanLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(plainBooleanLabel, gbc_plainBooleanLabel);

		GridBagConstraints gbc_plainBooleanRadioButton = new GridBagConstraints();
		gbc_plainBooleanRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_plainBooleanRadioButton.anchor = GridBagConstraints.WEST;
		gbc_plainBooleanRadioButton.gridx = 2;
		gbc_plainBooleanRadioButton.gridy = 2;

		plainBooleanRadioButton.setSelected(true);
		plainBooleanRadioButton.addActionListener(booleanActionListener);
		plainBooleanRadioButton.setOpaque(false);
		booleanPanel.add(plainBooleanRadioButton, gbc_plainBooleanRadioButton);

		GridBagConstraints gbc_checkboxBooleanLabel = new GridBagConstraints();
		gbc_checkboxBooleanLabel.anchor = GridBagConstraints.WEST;
		gbc_checkboxBooleanLabel.insets = new Insets(0, 0, 5, 5);
		gbc_checkboxBooleanLabel.gridx = 0;
		gbc_checkboxBooleanLabel.gridy = 3;
		checkboxBooleanLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(checkboxBooleanLabel, gbc_checkboxBooleanLabel);

		GridBagConstraints gbc_checkboxBooleanRadioButton = new GridBagConstraints();
		gbc_checkboxBooleanRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_checkboxBooleanRadioButton.anchor = GridBagConstraints.WEST;
		gbc_checkboxBooleanRadioButton.gridx = 2;
		gbc_checkboxBooleanRadioButton.gridy = 3;

		checkboxBooleanRadioButton.addActionListener(booleanActionListener);
		checkboxBooleanRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		checkboxBooleanRadioButton.setOpaque(false);
		booleanPanel.add(checkboxBooleanRadioButton, gbc_checkboxBooleanRadioButton);

		GridBagConstraints gbc_buttonBooleanLabel = new GridBagConstraints();
		gbc_buttonBooleanLabel.anchor = GridBagConstraints.WEST;
		gbc_buttonBooleanLabel.insets = new Insets(0, 0, 0, 5);
		gbc_buttonBooleanLabel.gridx = 0;
		gbc_buttonBooleanLabel.gridy = 4;
		buttonBooleanLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(buttonBooleanLabel, gbc_buttonBooleanLabel);

		GridBagConstraints gbc_buttonBooleanRadioButton = new GridBagConstraints();
		gbc_buttonBooleanRadioButton.anchor = GridBagConstraints.WEST;
		gbc_buttonBooleanRadioButton.gridx = 2;
		gbc_buttonBooleanRadioButton.gridy = 4;

		buttonBooleanRadioButton.setOpaque(false);
		buttonBooleanRadioButton.addActionListener(booleanActionListener);
		buttonBooleanRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		booleanPanel.add(buttonBooleanRadioButton, gbc_buttonBooleanRadioButton);

		ButtonGroup booleanGroup = new ButtonGroup();
		booleanGroup.add(plainBooleanRadioButton);
		booleanGroup.add(checkboxBooleanRadioButton);
		booleanGroup.add(buttonBooleanRadioButton);

		datePanel.setOpaque(false);
		datePanel.setBorder(new EmptyBorder(0, 25, 0, 25));

		cardPanel.add(datePanel, "date");
		GridBagLayout gbl_datePanel = new GridBagLayout();
		gbl_datePanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_datePanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_datePanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_datePanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		datePanel.setLayout(gbl_datePanel);

		GridBagConstraints gbc_dateFormatLabel = new GridBagConstraints();
		gbc_dateFormatLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_dateFormatLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dateFormatLabel.gridx = 0;
		gbc_dateFormatLabel.gridy = 0;
		dateFormatLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(dateFormatLabel, gbc_dateFormatLabel);

		GridBagConstraints gbc_dateFormatList = new GridBagConstraints();
		gbc_dateFormatList.insets = new Insets(0, 0, 5, 0);
		gbc_dateFormatList.fill = GridBagConstraints.BOTH;
		gbc_dateFormatList.gridx = 1;
		gbc_dateFormatList.gridy = 0;
		datePanel.add(dateFormatList, gbc_dateFormatList);

		DefaultListModel dateFormatListModel = new DefaultListModel();
		dateFormatListModel.addElement(simpleDateFormat.format(Calendar.getInstance().getTime()));
		dateFormatListModel.addElement(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()));
		dateFormatListModel.addElement(DateFormat.getDateInstance(DateFormat.LONG).format(Calendar.getInstance().getTime()));
		dateFormatListModel.addElement(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));

		dateFormatList.setModel(dateFormatListModel);
		dateFormatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dateFormatList.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		dateFormatList.setBackground(new Color(255, 243, 204));
		dateFormatList.setSelectedIndex(0);

		GridBagConstraints gbc_plainDateLabel = new GridBagConstraints();
		gbc_plainDateLabel.anchor = GridBagConstraints.WEST;
		gbc_plainDateLabel.insets = new Insets(0, 0, 5, 5);
		gbc_plainDateLabel.gridx = 0;
		gbc_plainDateLabel.gridy = 1;
		plainDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(plainDateLabel, gbc_plainDateLabel);

		ActionListener dateActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!plainDateRadioButton.isSelected()) {
					spinnerCheckBox.setSelected(false);
					solverCheckBox.setSelected(false);
					plainTimeRadioButton.setSelected(true);
					plainBooleanRadioButton.setSelected(true);
					predefinedTextCheckBox.setSelected(false);
					checkBoxCheck();
				}
			}
		};

		plainDateRadioButton.setSelected(true);
		plainDateRadioButton.addActionListener(dateActionListener);
		plainDateRadioButton.setOpaque(false);
		plainDateRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagConstraints gbc_plainDateRadioButton = new GridBagConstraints();
		gbc_plainDateRadioButton.anchor = GridBagConstraints.WEST;
		gbc_plainDateRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_plainDateRadioButton.gridx = 1;
		gbc_plainDateRadioButton.gridy = 1;
		datePanel.add(plainDateRadioButton, gbc_plainDateRadioButton);

		GridBagConstraints gbc_dateChooserLabel = new GridBagConstraints();
		gbc_dateChooserLabel.anchor = GridBagConstraints.WEST;
		gbc_dateChooserLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dateChooserLabel.gridx = 0;
		gbc_dateChooserLabel.gridy = 2;
		dateChooserLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(dateChooserLabel, gbc_dateChooserLabel);

		GridBagConstraints gbc_dateChooserNewRadioButton = new GridBagConstraints();
		gbc_dateChooserNewRadioButton.anchor = GridBagConstraints.WEST;
		gbc_dateChooserNewRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_dateChooserNewRadioButton.gridx = 1;
		gbc_dateChooserNewRadioButton.gridy = 2;

		dateChooserRadioButton.setOpaque(false);
		dateChooserRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		dateChooserRadioButton.addActionListener(dateActionListener);
		datePanel.add(dateChooserRadioButton, gbc_dateChooserNewRadioButton);

		GridBagConstraints gbc_monthSpinnerLabel = new GridBagConstraints();
		gbc_monthSpinnerLabel.anchor = GridBagConstraints.WEST;
		gbc_monthSpinnerLabel.insets = new Insets(0, 0, 5, 5);
		gbc_monthSpinnerLabel.gridx = 0;
		gbc_monthSpinnerLabel.gridy = 3;
		monthSpinnerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(monthSpinnerLabel, gbc_monthSpinnerLabel);

		GridBagConstraints gbc_daySpinnerRadioButton = new GridBagConstraints();
		gbc_daySpinnerRadioButton.anchor = GridBagConstraints.WEST;
		gbc_daySpinnerRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_daySpinnerRadioButton.gridx = 1;
		gbc_daySpinnerRadioButton.gridy = 3;

		daySpinnerRadioButton.addActionListener(dateActionListener);
		daySpinnerRadioButton.setOpaque(false);
		daySpinnerRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(daySpinnerRadioButton, gbc_daySpinnerRadioButton);

		GridBagConstraints gbc_daySpinnerLabel = new GridBagConstraints();
		gbc_daySpinnerLabel.anchor = GridBagConstraints.WEST;
		gbc_daySpinnerLabel.insets = new Insets(0, 0, 0, 5);
		gbc_daySpinnerLabel.gridx = 0;
		gbc_daySpinnerLabel.gridy = 4;
		daySpinnerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		datePanel.add(daySpinnerLabel, gbc_daySpinnerLabel);

		GridBagConstraints gbc_monthSpinnerRadioButton = new GridBagConstraints();
		gbc_monthSpinnerRadioButton.anchor = GridBagConstraints.WEST;
		gbc_monthSpinnerRadioButton.gridx = 1;
		gbc_monthSpinnerRadioButton.gridy = 4;

		monthSpinnerRadioButton.addActionListener(dateActionListener);
		monthSpinnerRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		monthSpinnerRadioButton.setOpaque(false);
		datePanel.add(monthSpinnerRadioButton, gbc_monthSpinnerRadioButton);

		ButtonGroup dateGroup = new ButtonGroup();
		dateGroup.add(plainDateRadioButton);
		dateGroup.add(daySpinnerRadioButton);
		dateGroup.add(monthSpinnerRadioButton);
		dateGroup.add(dateChooserRadioButton);

		timePanel.setOpaque(false);
		timePanel.setBorder(new EmptyBorder(0, 25, 0, 25));

		cardPanel.add(timePanel, "time");
		GridBagLayout gbl_timePanel = new GridBagLayout();
		gbl_timePanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_timePanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_timePanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_timePanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		timePanel.setLayout(gbl_timePanel);

		GridBagConstraints gbc_timeFormatLabel = new GridBagConstraints();
		gbc_timeFormatLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_timeFormatLabel.insets = new Insets(0, 0, 5, 5);
		gbc_timeFormatLabel.gridx = 0;
		gbc_timeFormatLabel.gridy = 0;
		timeFormatLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(timeFormatLabel, gbc_timeFormatLabel);

		DefaultListModel timeFormatListModel = new DefaultListModel();
		timeFormatListModel.addElement(simpleDateFormat2.format(Calendar.getInstance().getTime()));
		timeFormatListModel.addElement(DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime()));
		timeFormatListModel.addElement(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()));
		timeFormatListModel.addElement(DateFormat.getTimeInstance(DateFormat.LONG).format(Calendar.getInstance().getTime()));
		timeFormatListModel.addElement(simpleDateFormat3.format(Calendar.getInstance().getTime()));

		timeFormatList.setModel(timeFormatListModel);
		timeFormatList.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		timeFormatList.setBackground(new Color(255, 243, 204));

		GridBagConstraints gbc_timeFormatList = new GridBagConstraints();
		gbc_timeFormatList.insets = new Insets(0, 0, 5, 0);
		gbc_timeFormatList.fill = GridBagConstraints.BOTH;
		gbc_timeFormatList.gridx = 1;
		gbc_timeFormatList.gridy = 0;
		timePanel.add(timeFormatList, gbc_timeFormatList);

		GridBagConstraints gbc_plainTimeLabel = new GridBagConstraints();
		gbc_plainTimeLabel.anchor = GridBagConstraints.WEST;
		gbc_plainTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_plainTimeLabel.gridx = 0;
		gbc_plainTimeLabel.gridy = 1;
		plainTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(plainTimeLabel, gbc_plainTimeLabel);

		ActionListener timeActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!plainTimeRadioButton.isSelected()) {
					spinnerCheckBox.setSelected(false);
					solverCheckBox.setSelected(false);
					plainDateRadioButton.setSelected(true);
					plainBooleanRadioButton.setSelected(true);
					predefinedTextCheckBox.setSelected(false);
					checkBoxCheck();
				}
			}

		};

		GridBagConstraints gbc_plainTimeRadioButton = new GridBagConstraints();
		gbc_plainTimeRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_plainTimeRadioButton.anchor = GridBagConstraints.WEST;
		gbc_plainTimeRadioButton.gridx = 1;
		gbc_plainTimeRadioButton.gridy = 1;
		plainTimeRadioButton.setOpaque(false);
		plainTimeRadioButton.setSelected(true);
		plainTimeRadioButton.addActionListener(timeActionListener);
		plainTimeRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(plainTimeRadioButton, gbc_plainTimeRadioButton);

		GridBagConstraints gbc_hourSpinnerLabel = new GridBagConstraints();
		gbc_hourSpinnerLabel.anchor = GridBagConstraints.WEST;
		gbc_hourSpinnerLabel.insets = new Insets(0, 0, 5, 5);
		gbc_hourSpinnerLabel.gridx = 0;
		gbc_hourSpinnerLabel.gridy = 2;
		hourSpinnerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(hourSpinnerLabel, gbc_hourSpinnerLabel);

		GridBagConstraints gbc_hourSpinnerRadioButton = new GridBagConstraints();
		gbc_hourSpinnerRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_hourSpinnerRadioButton.anchor = GridBagConstraints.WEST;
		gbc_hourSpinnerRadioButton.gridx = 1;
		gbc_hourSpinnerRadioButton.gridy = 2;

		hourSpinnerRadioButton.addActionListener(timeActionListener);
		hourSpinnerRadioButton.setOpaque(false);
		hourSpinnerRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(hourSpinnerRadioButton, gbc_hourSpinnerRadioButton);

		GridBagConstraints gbc_minuteSpinnerLabel = new GridBagConstraints();
		gbc_minuteSpinnerLabel.anchor = GridBagConstraints.WEST;
		gbc_minuteSpinnerLabel.insets = new Insets(0, 0, 0, 5);
		gbc_minuteSpinnerLabel.gridx = 0;
		gbc_minuteSpinnerLabel.gridy = 3;
		minuteSpinnerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(minuteSpinnerLabel, gbc_minuteSpinnerLabel);

		GridBagConstraints gbc_minuteSpinnerRadioButton = new GridBagConstraints();
		gbc_minuteSpinnerRadioButton.anchor = GridBagConstraints.WEST;
		gbc_minuteSpinnerRadioButton.gridx = 1;
		gbc_minuteSpinnerRadioButton.gridy = 3;

		minuteSpinnerRadioButton.addActionListener(timeActionListener);
		minuteSpinnerRadioButton.setOpaque(false);
		minuteSpinnerRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		timePanel.add(minuteSpinnerRadioButton, gbc_minuteSpinnerRadioButton);

		ButtonGroup timeGroup = new ButtonGroup();
		timeGroup.add(plainTimeRadioButton);
		timeGroup.add(hourSpinnerRadioButton);
		timeGroup.add(minuteSpinnerRadioButton);

		textPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
		textPanel.setOpaque(false);

		cardPanel.add(textPanel, "text");
		GridBagLayout gbl_textPanel = new GridBagLayout();
		gbl_textPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_textPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_textPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_textPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		textPanel.setLayout(gbl_textPanel);

		GridBagConstraints gbc_predefinedLabel = new GridBagConstraints();
		gbc_predefinedLabel.anchor = GridBagConstraints.WEST;
		gbc_predefinedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_predefinedLabel.gridx = 0;
		gbc_predefinedLabel.gridy = 0;
		predefinedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		textPanel.add(predefinedLabel, gbc_predefinedLabel);

		GridBagConstraints gbc_predefinedTextCheckBox = new GridBagConstraints();
		gbc_predefinedTextCheckBox.anchor = GridBagConstraints.WEST;
		gbc_predefinedTextCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_predefinedTextCheckBox.gridx = 1;
		gbc_predefinedTextCheckBox.gridy = 0;

		ActionListener predefinedTextActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (predefinedTextCheckBox.isSelected()) {
					spinnerCheckBox.setSelected(false);
					solverCheckBox.setSelected(false);
				}
				checkBoxCheck();
			}

		};

		predefinedTextCheckBox.addActionListener(predefinedTextActionListener);
		predefinedTextCheckBox.setOpaque(false);
		predefinedTextCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		textPanel.add(predefinedTextCheckBox, gbc_predefinedTextCheckBox);

		GridBagConstraints gbc_predefinedAddButton = new GridBagConstraints();
		gbc_predefinedAddButton.insets = new Insets(0, 0, 5, 5);
		gbc_predefinedAddButton.gridx = 0;
		gbc_predefinedAddButton.gridy = 1;

		predefinedAddButton.setHorizontalAlignment(SwingConstants.LEFT);
		predefinedAddButton.setEnabled(false);
		predefinedAddButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		predefinedAddButton.setPreferredSize(new Dimension(100, 25));
		predefinedAddButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		textPanel.add(predefinedAddButton, gbc_predefinedAddButton);

		GridBagConstraints gbc_predefinedTextList = new GridBagConstraints();
		gbc_predefinedTextList.gridheight = 4;
		gbc_predefinedTextList.fill = GridBagConstraints.BOTH;
		gbc_predefinedTextList.gridx = 1;
		gbc_predefinedTextList.gridy = 1;

		final DefaultListModel predefinedTextListModel = new DefaultListModel();

		predefinedTextList.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		predefinedTextList.setBackground(new Color(204, 216, 255));
		predefinedTextList.setEnabled(false);
		predefinedTextList.setModel(predefinedTextListModel);
		textPanel.add(predefinedTextList, gbc_predefinedTextList);

		predefinedAddButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				PredefinedCellValueDialog predefinedCellValueDialog = new PredefinedCellValueDialog(null);
				predefinedCellValueDialog.setVisible(true);
				if (!predefinedCellValueDialog.isCancelled()) {
					predefinedTextListModel.addElement(predefinedCellValueDialog.getPredefinedText());
				}
				if (predefinedTextListModel.getSize() > 0) {
					predefinedTextList.setSelectedIndex(0);
					predefinedRemoveButton.setEnabled(true);
					predefinedEditButton.setEnabled(true);
				}
			}
		});

		GridBagConstraints gbc_predefinedEditButton = new GridBagConstraints();
		gbc_predefinedEditButton.insets = new Insets(0, 0, 5, 5);
		gbc_predefinedEditButton.gridx = 0;
		gbc_predefinedEditButton.gridy = 2;

		predefinedEditButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		predefinedEditButton.setPreferredSize(new Dimension(100, 25));
		predefinedEditButton.setHorizontalAlignment(SwingConstants.LEFT);
		predefinedEditButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		predefinedEditButton.setEnabled(false);
		textPanel.add(predefinedEditButton, gbc_predefinedEditButton);

		predefinedEditButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int i = predefinedTextList.getSelectedIndex();
				if (i > -1) {
					PredefinedCellValueDialog predefinedCellValueDialog = new PredefinedCellValueDialog((String) predefinedTextListModel.get(i));
					predefinedCellValueDialog.setVisible(true);
					if (!predefinedCellValueDialog.isCancelled()) {
						predefinedTextListModel.remove(i);
						predefinedTextListModel.add(i, predefinedCellValueDialog.getPredefinedText());
						predefinedTextList.setSelectedIndex(i);
					}
				}
			}
		});

		GridBagConstraints gbc_predefinedRemoveButton = new GridBagConstraints();
		gbc_predefinedRemoveButton.insets = new Insets(0, 0, 5, 5);
		gbc_predefinedRemoveButton.gridx = 0;
		gbc_predefinedRemoveButton.gridy = 3;
		predefinedRemoveButton.setFont(new Font("Dialog", Font.PLAIN, 12));

		predefinedRemoveButton.setHorizontalAlignment(SwingConstants.LEFT);
		predefinedRemoveButton.setEnabled(false);
		predefinedRemoveButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		predefinedRemoveButton.setPreferredSize(new Dimension(100, 25));
		predefinedRemoveButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		textPanel.add(predefinedRemoveButton, gbc_predefinedRemoveButton);

		predefinedRemoveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int i = predefinedTextList.getSelectedIndex();
				if (i > -1) {
					predefinedTextListModel.remove(i);
					if (predefinedTextListModel.getSize() > 0)
						predefinedTextList.setSelectedIndex(0);
					else {
						predefinedRemoveButton.setEnabled(false);
						predefinedEditButton.setEnabled(false);
					}
				}
			}
		});

		GridBagConstraints gbc_foregroundLabel = new GridBagConstraints();
		gbc_foregroundLabel.anchor = GridBagConstraints.WEST;
		gbc_foregroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_foregroundLabel.gridx = 0;
		gbc_foregroundLabel.gridy = 1;
		foregroundLabel.setMinimumSize(new Dimension(50, 25));
		foregroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		foregroundLabel.setBackground(Color.BLACK);
		foregroundLabel.setOpaque(true);
		foregroundLabel.setPreferredSize(new Dimension(50, 25));
		foregroundLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(foregroundLabel, gbc_foregroundLabel);

		GridBagConstraints gbc_foregroundButton = new GridBagConstraints();
		gbc_foregroundButton.fill = GridBagConstraints.BOTH;
		gbc_foregroundButton.insets = new Insets(0, 0, 5, 5);
		gbc_foregroundButton.gridx = 1;
		gbc_foregroundButton.gridy = 1;
		foregroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(foregroundButton, gbc_foregroundButton);

		foregroundButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Color color = JColorChooser.showDialog(FormatCellsDialog.this, "Choose Foreground", foregroundLabel.getBackground());
				if (color != null) {
					foregroundLabel.setBackground(color);
					if (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()) {
						spinner1TextField.setForeground(color);
						spinner2TextField.setForeground(color);
						spinner3TextField.setForeground(color);
						spinner4TextField.setForeground(color);
						spinner5TextField.setForeground(color);
					}
				}
			}
		});

		GridBagConstraints gbc_backgroundLabel = new GridBagConstraints();
		gbc_backgroundLabel.anchor = GridBagConstraints.WEST;
		gbc_backgroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_backgroundLabel.gridx = 0;
		gbc_backgroundLabel.gridy = 2;
		backgroundLabel.setMinimumSize(new Dimension(50, 25));
		backgroundLabel.setOpaque(true);
		backgroundLabel.setBackground(new Color(255, 243, 204));
		backgroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		backgroundLabel.setPreferredSize(new Dimension(50, 25));
		backgroundLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(backgroundLabel, gbc_backgroundLabel);

		GridBagConstraints gbc_backgroundButton = new GridBagConstraints();
		gbc_backgroundButton.fill = GridBagConstraints.BOTH;
		gbc_backgroundButton.insets = new Insets(0, 0, 5, 5);
		gbc_backgroundButton.gridx = 1;
		gbc_backgroundButton.gridy = 2;
		backgroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(backgroundButton, gbc_backgroundButton);

		backgroundButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Color color = JColorChooser.showDialog(FormatCellsDialog.this, "Choose Background", backgroundLabel.getBackground());
				if (color != null) {
					backgroundLabel.setBackground(color);
					if (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()) {
						spinner1TextField.setBackground(backgroundLabel.getBackground());
						spinner2TextField.setBackground(backgroundLabel.getBackground());
						spinner3TextField.setBackground(backgroundLabel.getBackground());
						spinner4TextField.setBackground(backgroundLabel.getBackground());
						spinner5TextField.setBackground(backgroundLabel.getBackground());
					}
				}
			}
		});

		GridBagConstraints gbc_boldCheckBox = new GridBagConstraints();
		gbc_boldCheckBox.anchor = GridBagConstraints.WEST;
		gbc_boldCheckBox.gridwidth = 2;
		gbc_boldCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_boldCheckBox.gridx = 0;
		gbc_boldCheckBox.gridy = 3;
		boldCheckBox.setOpaque(false);
		boldCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(boldCheckBox, gbc_boldCheckBox);

		GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_1.gridwidth = 2;
		gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_1.gridx = 0;
		gbc_chckbxNewCheckBox_1.gridy = 4;
		gradientPaintCheckBox.setOpaque(false);
		gradientPaintCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(gradientPaintCheckBox, gbc_chckbxNewCheckBox_1);

		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.gridwidth = 2;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 5;
		lockedCheckBox.setOpaque(false);
		lockedCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(lockedCheckBox, gbc_chckbxNewCheckBox);

		lockedCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (lockedCheckBox.isSelected()) {
					plainDateRadioButton.setSelected(true);
					daySpinnerLabel.setEnabled(false);
					daySpinnerRadioButton.setEnabled(false);
					monthSpinnerLabel.setEnabled(false);
					monthSpinnerRadioButton.setEnabled(false);
					dateChooserLabel.setEnabled(false);
					dateChooserRadioButton.setEnabled(false);

					spinnerCheckBox.setSelected(false);
					spinnerCheckBox.setEnabled(false);
					spinnerLabel.setEnabled(false);

					solverCheckBox.setSelected(false);
					solverCheckBox.setEnabled(false);
					solverLabel.setEnabled(false);

				}
				else {
					daySpinnerLabel.setEnabled(true);
					daySpinnerRadioButton.setEnabled(true);

					monthSpinnerLabel.setEnabled(true);
					monthSpinnerRadioButton.setEnabled(true);

					dateChooserLabel.setEnabled(true);
					dateChooserRadioButton.setEnabled(true);

					spinnerCheckBox.setEnabled(true);
					spinnerLabel.setEnabled(true);

					solverCheckBox.setEnabled(true);
					solverLabel.setEnabled(true);
				}
				checkBoxCheck();
			}
		});

		okButton = new JButton();
		okButton.setMinimumSize(new Dimension(100, 25));
		okButton.setText("Format");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png")));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 0;
		gbc_okButton.gridy = 1;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(100, 25));
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 1;
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

		DefaultListModel defaultListModel = new DefaultListModel();
		defaultListModel.addElement("Number");
		defaultListModel.addElement("Date");
		defaultListModel.addElement("Time");
		defaultListModel.addElement("Boolean Value");
		defaultListModel.addElement("Text");

		categoryList.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(5, 5, 5, 5)));
		categoryList.setBackground(new Color(255, 243, 204));
		categoryList.setModel(defaultListModel);
		categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		categoryList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				switch (categoryList.getSelectedIndex()) {
					case 0:
						cardLayout.show(cardPanel, "number");
						break;
					case 1:
						cardLayout.show(cardPanel, "date");
						break;
					case 2:
						cardLayout.show(cardPanel, "time");
						break;
					case 3:
						cardLayout.show(cardPanel, "boolean");
						break;
					case 4:
						cardLayout.show(cardPanel, "text");
						break;
				}

			}
		});

		categoryList.setSelectedIndex(0);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		DecimalFormat decimalFormat = new DecimalFormat("0.####################################");
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		backgroundLabel.setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(), spreadSheetCellFormat.getBackgroundGreen(), spreadSheetCellFormat
				.getBackgroundBlue()));
		foregroundLabel.setBackground(new Color(spreadSheetCellFormat.getForegroundRed(), spreadSheetCellFormat.getForegroundGreen(), spreadSheetCellFormat
				.getForegroundBlue()));
		gradientPaintCheckBox.setSelected(spreadSheetCellFormat.getGradientPaint());
		if (spreadSheetCellFormat.getBoldFont() != null)
			boldCheckBox.setSelected(spreadSheetCellFormat.getBoldFont());
		lockedCheckBox.setSelected(spreadSheetCellFormat.getLocked());
		trueTextField.setText(spreadSheetCellFormat.getTrueValue());
		falseTextField.setText(spreadSheetCellFormat.getFalseValue());
		dateFormatList.setSelectedIndex(spreadSheetCellFormat.getDateFormat());
		timeFormatList.setSelectedIndex(spreadSheetCellFormat.getTimeFormat());
		if(spreadSheetCellFormat.getFractionalDisplay()!=null&&spreadSheetCellFormat.getFractionalDisplay()==true)
			decimalPlacesComboBox.setSelectedIndex(1);
		if(spreadSheetCellFormat.getDecimalPlaces()!=null)
			decimalPlacesTextField.setText(Integer.toString(spreadSheetCellFormat.getDecimalPlaces()));
		
		if(spreadSheetCellFormat.getLeadingZeroes()!=null)
			leadingZeroesTextField.setText(Integer.toString(spreadSheetCellFormat.getLeadingZeroes()));
		
		if(spreadSheetCellFormat.getPresentationRoundingMode()!=null)
		{
			switch(spreadSheetCellFormat.getPresentationRoundingMode())
			{
				case CEILING:
					roundingModeComboBox.setSelectedIndex(2);
					break;
				case DOWN:
					roundingModeComboBox.setSelectedIndex(1);
					break;
				case FLOOR:
					roundingModeComboBox.setSelectedIndex(3);
					break;
				case HALF_DOWN:
					roundingModeComboBox.setSelectedIndex(5);
					break;
				case HALF_EVEN:
					roundingModeComboBox.setSelectedIndex(6);
					break;
				case HALF_UP:
					roundingModeComboBox.setSelectedIndex(4);
					break;
				case UP:
				default:
					roundingModeComboBox.setSelectedIndex(0);
					break;
				
			}
		}
		
		negativeNumbersRedCheckBox.setSelected(spreadSheetCellFormat.getNegativeNumbersRed());
		thousandsSeparatorCheckBox.setSelected(spreadSheetCellFormat.getThousandsSeparator());

		switch (spreadSheetCellFormat.getSpreadSheetFormatType()) {
			case BUTTON:
				categoryList.setSelectedIndex(3);
				checkBoxCheck();
				buttonBooleanRadioButton.doClick();
				break;
			case CHECKBOX:
				categoryList.setSelectedIndex(3);
				checkBoxCheck();
				checkboxBooleanRadioButton.doClick();
				break;
			case CLEAR:
				break;
			case DATE_CHOOSER:
				categoryList.setSelectedIndex(1);
				checkBoxCheck();
				dateChooserRadioButton.doClick();

				break;
			case DAY_SPINNER:
				categoryList.setSelectedIndex(1);
				checkBoxCheck();
				daySpinnerRadioButton.doClick();
				break;
			case HOUR_SPINNER:
				categoryList.setSelectedIndex(2);
				checkBoxCheck();
				hourSpinnerRadioButton.doClick();
				break;
			case MINUTE_SPINNER:
				categoryList.setSelectedIndex(2);
				checkBoxCheck();
				minuteSpinnerRadioButton.doClick();
				break;
			case MONTH_SPINNER:
				categoryList.setSelectedIndex(1);
				checkBoxCheck();
				monthSpinnerRadioButton.doClick();
				break;
			case NUMBER_SPINNER:
			case SOLVER:
				if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.SOLVER) {
					solverCheckBox.setSelected(true);
					adjustCellTextField.setText(spreadSheetCellFormat.getSolverCell());
					try {
						toleranceCellTextField.setText(decimalFormat.format(spreadSheetCellFormat.getSolverTolerance()));
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					spinnerCheckBox.setSelected(true);
				}
				checkBoxCheck();
				tickSizeTextField.setText(decimalFormat.format(spreadSheetCellFormat.getTickSize()));
				incrementTextField.setText(decimalFormat.format(spreadSheetCellFormat.getIncrement()));
				if (spreadSheetCellFormat.getMinimumValue() != null)
					minValueTextField.setText(decimalFormat.format(spreadSheetCellFormat.getMinimumValue()));
				if (spreadSheetCellFormat.getMaximumValue() != null)
					maximumValueTextField.setText(decimalFormat.format(spreadSheetCellFormat.getMaximumValue()));
				switch (spreadSheetCellFormat.getSpinnerStyle()) {
					case 0:
						spinner1RadioButton.doClick();
						break;
					case 1:
						spinner2RadioButton.doClick();
						break;
					case 2:
						spinner3RadioButton.doClick();
						break;
					case 3:
						spinner4RadioButton.doClick();
						break;
					case 4:
						spinner5RadioButton.doClick();
						break;
				}
				break;
			case PLAIN:
			case PLAIN_DATE:
			case PLAIN_TIME:
				break;
			case PREDEFINED:
				categoryList.setSelectedIndex(4);
				checkBoxCheck();
				predefinedTextCheckBox.doClick();
				for (String string : spreadSheetCellFormat.getPredefinedStrings())
					predefinedTextListModel.addElement(string);
				break;
			case TMP_DATE:
			case TMP_TIME:
				break;
		}
		checkBoxCheck();

	}

	/**
	 * Checks if is cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {

		return cancelled;
	}

	private void checkBoxCheck() {

		spinner1RadioButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner2RadioButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner3RadioButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner4RadioButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner5RadioButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner1MinusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner2MinusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner3MinusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner4MinusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner5MinusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner1PlusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner2PlusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner3PlusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner4PlusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner5PlusButton.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner1TextField.setEditable(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner2TextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner3TextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner4TextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		spinner5TextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		tickSizeTextField.setEnabled(decimalPlacesComboBox.getSelectedIndex() != 1 && (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()));
		tickSizeLabel.setEnabled(decimalPlacesComboBox.getSelectedIndex() != 1 && (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()));
		incrementTextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		incrementLabel.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		minValueLabel.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		maximumValueLabel.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		minValueTextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		maximumValueTextField.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());
		styleLabel.setEnabled(spinnerCheckBox.isSelected() || solverCheckBox.isSelected());

		adjustCellLabel.setEnabled(solverCheckBox.isSelected());
		adjustCellTextField.setEnabled(solverCheckBox.isSelected());

		toleranceCellTextField.setEnabled(solverCheckBox.isSelected());
		toleranceCellLabel.setEnabled(solverCheckBox.isSelected());

		tickSizeWarningLabel.setIcon(null);
		incrementWarningLabel.setIcon(null);
		maximumValueWarningLabel.setIcon(null);
		minimumValueWarningLabel.setIcon(null);
		decimalPlacesWarningLabel.setIcon(null);

		tickSizeWarningLabel.setToolTipText(null);
		incrementWarningLabel.setToolTipText(null);
		maximumValueWarningLabel.setToolTipText(null);
		minimumValueWarningLabel.setToolTipText(null);
		decimalPlacesWarningLabel.setToolTipText(null);

		spinner1TextField.setBackground(new Color(204, 216, 255));
		spinner2TextField.setBackground(new Color(204, 216, 255));
		spinner3TextField.setBackground(new Color(204, 216, 255));
		spinner4TextField.setBackground(new Color(204, 216, 255));
		spinner5TextField.setBackground(new Color(204, 216, 255));
		tickSizeTextField.setBackground(new Color(204, 216, 255));
		incrementTextField.setBackground(new Color(204, 216, 255));
		minValueTextField.setBackground(new Color(204, 216, 255));
		maximumValueTextField.setBackground(new Color(204, 216, 255));
		adjustCellTextField.setBackground(new Color(204, 216, 255));
		toleranceCellTextField.setBackground(new Color(204, 216, 255));

		if (spinnerCheckBox.isSelected() || solverCheckBox.isSelected()) {
			plainDateRadioButton.setSelected(true);
			plainTimeRadioButton.setSelected(true);
			plainBooleanRadioButton.setSelected(true);
			predefinedTextCheckBox.setSelected(false);
			spinner1TextField.setBackground(backgroundLabel.getBackground());
			spinner2TextField.setBackground(backgroundLabel.getBackground());
			spinner3TextField.setBackground(backgroundLabel.getBackground());
			spinner4TextField.setBackground(backgroundLabel.getBackground());
			spinner5TextField.setBackground(backgroundLabel.getBackground());

			if (decimalPlacesComboBox.getSelectedIndex() != 1)
				tickSizeTextField.setBackground(new Color(255, 243, 204));

			incrementTextField.setBackground(new Color(255, 243, 204));
			minValueTextField.setBackground(new Color(255, 243, 204));
			maximumValueTextField.setBackground(new Color(255, 243, 204));
			String increment = incrementTextField.getText();
			tickSizeTextField.setText(tickSizeTextField.getText());
			decimalPlacesTextField.setText(decimalPlacesTextField.getText());
			incrementTextField.setText(increment);
			maximumValueTextField.setText(maximumValueTextField.getText());
			minValueTextField.setText(minValueTextField.getText());
		}

		if (solverCheckBox.isSelected()) {
			adjustCellTextField.setBackground(new Color(255, 243, 204));
			toleranceCellTextField.setBackground(new Color(255, 243, 204));
		}

		if (predefinedTextCheckBox.isSelected()) {
			plainDateRadioButton.setSelected(true);
			plainTimeRadioButton.setSelected(true);
			plainBooleanRadioButton.setSelected(true);
			predefinedAddButton.setEnabled(true);
			predefinedTextList.setEnabled(true);
			predefinedTextList.setBackground(new Color(255, 243, 204));
			if (predefinedTextList.getModel().getSize() > 0) {
				if (predefinedTextList.getSelectedIndex() == -1)
					predefinedTextList.setSelectedIndex(0);
				predefinedRemoveButton.setEnabled(true);
				predefinedEditButton.setEnabled(true);
			}
			else {
				predefinedRemoveButton.setEnabled(false);
				predefinedEditButton.setEnabled(false);
			}
		}
		else {
			predefinedRemoveButton.setEnabled(false);
			predefinedEditButton.setEnabled(false);
			predefinedAddButton.setEnabled(false);
			predefinedTextList.setEnabled(false);
			predefinedTextList.setBackground(new Color(204, 216, 255));
		}

		checkOkButton();
	}

	private void checkOkButton() {

		boolean okButtonEnabled = true;
		if (tickSizeWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (decimalPlacesWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (incrementWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (minimumValueWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (maximumValueWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (trueWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (falseWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (toleranceWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		if (adjustCellWarningLabel.getIcon() != null)
			okButtonEnabled = false;
		okButton.setEnabled(okButtonEnabled);
	}

	/**
	 * Gets the spread sheet cell format.
	 *
	 * @return the spread sheet cell format
	 */
	public SpreadSheetCellFormat getSpreadSheetCellFormat() {

		SpreadSheetCellFormat spreadSheetCellFormat = new SpreadSheetCellFormat();

		spreadSheetCellFormat.setForegroundBlue(foregroundLabel.getBackground().getBlue());

		spreadSheetCellFormat.setForegroundGreen(foregroundLabel.getBackground().getGreen());

		spreadSheetCellFormat.setForegroundRed(foregroundLabel.getBackground().getRed());

		spreadSheetCellFormat.setBackgroundBlue(backgroundLabel.getBackground().getBlue());

		spreadSheetCellFormat.setBackgroundGreen(backgroundLabel.getBackground().getGreen());

		spreadSheetCellFormat.setBackgroundRed(backgroundLabel.getBackground().getRed());

		spreadSheetCellFormat.setGradientPaint(gradientPaintCheckBox.isSelected());

		spreadSheetCellFormat.setBoldFont(boldCheckBox.isSelected());

		spreadSheetCellFormat.setLocked(lockedCheckBox.isSelected());
		
		if(decimalPlacesComboBox.getSelectedIndex()==1)
			spreadSheetCellFormat.setFractionalDisplay(true);
		else
			spreadSheetCellFormat.setFractionalDisplay(false);

		switch (roundingModeComboBox.getSelectedIndex()) {
			case 0:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.UP);
				break;
			case 1:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.DOWN);
				break;
			case 2:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.CEILING);
				break;
			case 3:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.FLOOR);
				break;
			case 4:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.HALF_UP);
				break;
			case 5:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.HALF_DOWN);
				break;
			default:
				spreadSheetCellFormat.setPresentationRoundingMode(PresentationRoundingMode.HALF_EVEN);
				break;
		}

		try {
			spreadSheetCellFormat.setDecimalPlaces(Integer.parseInt(decimalPlacesTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		try {
			spreadSheetCellFormat.setLeadingZeroes(Integer.parseInt(leadingZeroesTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		spreadSheetCellFormat.setNegativeNumbersRed(negativeNumbersRedCheckBox.isSelected());

		spreadSheetCellFormat.setThousandsSeparator(thousandsSeparatorCheckBox.isSelected());

		spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN);

		if (spinnerCheckBox.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.NUMBER_SPINNER);
		else if (solverCheckBox.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.SOLVER);
		else if (dateChooserRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.DATE_CHOOSER);
		else if (daySpinnerRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.DAY_SPINNER);
		else if (monthSpinnerRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.MONTH_SPINNER);
		else if (hourSpinnerRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.HOUR_SPINNER);
		else if (minuteSpinnerRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.MINUTE_SPINNER);
		else if (checkboxBooleanRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CHECKBOX);
		else if (buttonBooleanRadioButton.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.BUTTON);
		else if (predefinedTextCheckBox.isSelected())
			spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PREDEFINED);

		try {
			spreadSheetCellFormat.setTickSize(Double.parseDouble(tickSizeTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		try {
			spreadSheetCellFormat.setSolverTolerance(Double.parseDouble(toleranceCellTextField.getText()));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}

		spreadSheetCellFormat.setSolverCell(adjustCellTextField.getText());

		try {
			spreadSheetCellFormat.setIncrement(Double.parseDouble(incrementTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		try {
			spreadSheetCellFormat.setMinimumValue(Double.parseDouble(minValueTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		try {
			spreadSheetCellFormat.setMaximumValue(Double.parseDouble(maximumValueTextField.getText()));
		}
		catch (NumberFormatException e) {
		}

		spreadSheetCellFormat.setSpinnerStyle(0);

		if (spinner2RadioButton.isSelected())
			spreadSheetCellFormat.setSpinnerStyle(1);
		else if (spinner3RadioButton.isSelected())
			spreadSheetCellFormat.setSpinnerStyle(2);
		else if (spinner4RadioButton.isSelected())
			spreadSheetCellFormat.setSpinnerStyle(3);
		else if (spinner5RadioButton.isSelected())
			spreadSheetCellFormat.setSpinnerStyle(4);

		spreadSheetCellFormat.setDateFormat(0);

		if (dateFormatList.getSelectedIndex() > -1)
			spreadSheetCellFormat.setDateFormat(dateFormatList.getSelectedIndex());

		spreadSheetCellFormat.setTimeFormat(0);

		if (timeFormatList.getSelectedIndex() > -1)
			spreadSheetCellFormat.setTimeFormat(timeFormatList.getSelectedIndex());

		spreadSheetCellFormat.setTrueValue(trueTextField.getText());

		spreadSheetCellFormat.setFalseValue(falseTextField.getText());

		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < predefinedTextList.getModel().getSize(); i++)
			strings.add(predefinedTextList.getModel().getElementAt(i).toString());

		spreadSheetCellFormat.getPredefinedStrings().addAll(strings);

		return spreadSheetCellFormat;

	}

	private Double getFractionTickSize() {

		try {
			BigDecimal one = new BigDecimal("1");
			BigDecimal thirtyTwo = new BigDecimal("32");
			BigDecimal bigDecimal = new BigDecimal(decimalPlacesTextField.getText());
			bigDecimal = one.divide(thirtyTwo, 32, RoundingMode.HALF_EVEN).divide(bigDecimal, 32, RoundingMode.HALF_EVEN);
			if (!DollarFraction.isValidTickSize(bigDecimal.doubleValue()))
				throw new NumberFormatException();
			return bigDecimal.doubleValue();
		}
		catch (Exception e) {
			return null;
		}
	}

}
