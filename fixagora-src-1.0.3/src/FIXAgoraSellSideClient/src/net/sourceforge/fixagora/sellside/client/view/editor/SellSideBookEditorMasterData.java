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
package net.sourceforge.fixagora.sellside.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
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
import net.sourceforge.fixagora.basis.client.view.document.PositiveIntegerDocument;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;

/**
 * The Class SellSideBookEditorMasterData.
 */
public class SellSideBookEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private SellSideBookEditor sellSideBookEditor = null;

	private JLabel nameWarningLabel = null;

	private JTextArea textArea = null;

	private JLabel descriptionWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private boolean consistent;

	private JComboBox daycountComboBox;

	private JTextField valutaTextField;

	private JComboBox bankCalendarComboBox;

	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	private JComboBox styleComboBox;

	private JComboBox calculationComboBox;

	private JComboBox displayChangeComboBox;

	private JComboBox displayPriceComboBox;

	/**
	 * Instantiates a new sell side book editor master data.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public SellSideBookEditorMasterData(SellSideBookEditor sellSideBookEditor) {

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

		this.sellSideBookEditor = sellSideBookEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
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

		JLabel nameLabel = new JLabel("Book Name");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.WEST;
		gbc_nameLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = sellSideBookEditor.getNameWarningLabel();
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
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		panel.add(modifiedField, gbc_textField);

		JLabel valutaLabel = new JLabel("Valuta T+");
		valutaLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_positionLimitLabel = new GridBagConstraints();
		gbc_positionLimitLabel.anchor = GridBagConstraints.WEST;
		gbc_positionLimitLabel.insets = new Insets(0, 0, 5, 5);
		gbc_positionLimitLabel.gridx = 1;
		gbc_positionLimitLabel.gridy = 2;
		panel.add(valutaLabel, gbc_positionLimitLabel);

		valutaTextField = new JTextField();
		valutaTextField.setDocument(new PositiveIntegerDocument());
		valutaTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		valutaTextField.getDocument().addDocumentListener(documentListener);
		valutaTextField.setBackground(new Color(255, 243, 204));
		valutaTextField.setPreferredSize(new Dimension(250, 25));
		valutaTextField.setColumns(10);
		GridBagConstraints gbc_positionLimitTextField = new GridBagConstraints();
		gbc_positionLimitTextField.insets = new Insets(0, 0, 5, 5);
		gbc_positionLimitTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_positionLimitTextField.gridx = 3;
		gbc_positionLimitTextField.gridy = 2;
		panel.add(valutaTextField, gbc_positionLimitTextField);

		JLabel bankCalendarLabel = new JLabel("Bank Calendar");
		bankCalendarLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_bankCalendarLabel = new GridBagConstraints();
		gbc_bankCalendarLabel.anchor = GridBagConstraints.WEST;
		gbc_bankCalendarLabel.insets = new Insets(0, 0, 5, 5);
		gbc_bankCalendarLabel.gridx = 1;
		gbc_bankCalendarLabel.gridy = 3;
		panel.add(bankCalendarLabel, gbc_bankCalendarLabel);

		bankCalendarComboBox = new JComboBox(sellSideBookEditor.getMainPanel().getBankCalendars().toArray());
		bankCalendarComboBox.setMinimumSize(new Dimension(32, 25));
		bankCalendarComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		bankCalendarComboBox.setPreferredSize(new Dimension(32, 25));
		bankCalendarComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_bankCalendarComboBox = new GridBagConstraints();
		gbc_bankCalendarComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_bankCalendarComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_bankCalendarComboBox.gridx = 3;
		gbc_bankCalendarComboBox.gridy = 3;
		panel.add(bankCalendarComboBox, gbc_bankCalendarComboBox);

		bankCalendarComboBox.setSelectedIndex(0);
		bankCalendarComboBox.addActionListener(actionListener);

		JLabel daycountLabel = new JLabel("Day Count Basis");
		daycountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_daycountLabel = new GridBagConstraints();
		gbc_daycountLabel.anchor = GridBagConstraints.WEST;
		gbc_daycountLabel.insets = new Insets(0, 0, 5, 5);
		gbc_daycountLabel.gridx = 1;
		gbc_daycountLabel.gridy = 4;
		panel.add(daycountLabel, gbc_daycountLabel);

		List<ComboBoxEntry> daycountEntries = new ArrayList<ComboBoxEntry>();
		daycountEntries.add(new ComboBoxEntry(0, "US (NASD) 30/360"));
		daycountEntries.add(new ComboBoxEntry(1, "Actual/actual"));
		daycountEntries.add(new ComboBoxEntry(2, "Actual/360"));
		daycountEntries.add(new ComboBoxEntry(3, "Actual/365"));
		daycountEntries.add(new ComboBoxEntry(4, "European 30/360"));

		daycountComboBox = new JComboBox(daycountEntries.toArray());
		daycountComboBox.setMinimumSize(new Dimension(32, 25));
		daycountComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		daycountComboBox.setPreferredSize(new Dimension(32, 25));
		daycountComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_priceBoundaryMethodComboBox = new GridBagConstraints();
		gbc_priceBoundaryMethodComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_priceBoundaryMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceBoundaryMethodComboBox.gridx = 3;
		gbc_priceBoundaryMethodComboBox.gridy = 4;
		panel.add(daycountComboBox, gbc_priceBoundaryMethodComboBox);

		daycountComboBox.setSelectedIndex(0);
		daycountComboBox.addActionListener(actionListener);	
		
		JLabel calculationLabel = new JLabel("Price/Yield Calculation");
		calculationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_calculationLabel = new GridBagConstraints();
		gbc_calculationLabel.anchor = GridBagConstraints.WEST;
		gbc_calculationLabel.insets = new Insets(0, 0, 5, 5);
		gbc_calculationLabel.gridx = 1;
		gbc_calculationLabel.gridy = 5;
		panel.add(calculationLabel, gbc_calculationLabel);

		List<ComboBoxEntry> calculationEntries = new ArrayList<ComboBoxEntry>();
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.NONE.ordinal(), "None"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.DEFAULT.ordinal(), "PRICE / YIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.DISC.ordinal(), "PRICEDISC / YIELDDISC"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.MAT.ordinal(), "PRICEMAT / YIELDMAT"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.ODDF.ordinal(), "ODDFPRICE / ODDFYIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.ODDL.ordinal(), "ODDLPRICE / ODDLYIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.TBILL.ordinal(), "TBILLPRICE / TBILLYIELD"));

		calculationComboBox = new JComboBox(calculationEntries.toArray());
		calculationComboBox.setMinimumSize(new Dimension(32, 25));
		calculationComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		calculationComboBox.setPreferredSize(new Dimension(32, 25));
		calculationComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_calculationComboBox = new GridBagConstraints();
		gbc_calculationComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_calculationComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_calculationComboBox.gridx = 3;
		gbc_calculationComboBox.gridy = 5;
		panel.add(calculationComboBox, gbc_calculationComboBox);

		calculationComboBox.setSelectedIndex(1);
		calculationComboBox.addActionListener(actionListener);
		
		JLabel displayChangeLabel = new JLabel("Display Change");
		displayChangeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_displayChangeLabel = new GridBagConstraints();
		gbc_displayChangeLabel.anchor = GridBagConstraints.WEST;
		gbc_displayChangeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_displayChangeLabel.gridx = 1;
		gbc_displayChangeLabel.gridy = 6;
		panel.add(displayChangeLabel, gbc_displayChangeLabel);

		List<ComboBoxEntry> displayChangeEntries = new ArrayList<ComboBoxEntry>();
		displayChangeEntries.add(new ComboBoxEntry(true, "Absolute"));
		displayChangeEntries.add(new ComboBoxEntry(false, "Percentage"));

		displayChangeComboBox = new JComboBox(displayChangeEntries.toArray());
		displayChangeComboBox.setMinimumSize(new Dimension(32, 25));
		displayChangeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		displayChangeComboBox.setPreferredSize(new Dimension(32, 25));
		displayChangeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_displayChangeComboBox = new GridBagConstraints();
		gbc_displayChangeComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_displayChangeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_displayChangeComboBox.gridx = 3;
		gbc_displayChangeComboBox.gridy = 6;
		panel.add(displayChangeComboBox, gbc_displayChangeComboBox);

		displayChangeComboBox.setSelectedIndex(0);
		displayChangeComboBox.addActionListener(actionListener);
		
		JLabel displayPriceLabel = new JLabel("Display Price");
		displayPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_displayPriceLabel = new GridBagConstraints();
		gbc_displayPriceLabel.anchor = GridBagConstraints.WEST;
		gbc_displayPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_displayPriceLabel.gridx = 1;
		gbc_displayPriceLabel.gridy = 7;
		panel.add(displayPriceLabel, gbc_displayPriceLabel);

		List<ComboBoxEntry> displayPriceEntries = new ArrayList<ComboBoxEntry>();
		displayPriceEntries.add(new ComboBoxEntry(false, "Decimal"));
		displayPriceEntries.add(new ComboBoxEntry(true, "Fraction"));

		displayPriceComboBox = new JComboBox(displayPriceEntries.toArray());
		displayPriceComboBox.setMinimumSize(new Dimension(32, 25));
		displayPriceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		displayPriceComboBox.setPreferredSize(new Dimension(32, 25));
		displayPriceComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_displayPriceComboBox = new GridBagConstraints();
		gbc_displayPriceComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_displayPriceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_displayPriceComboBox.gridx = 3;
		gbc_displayPriceComboBox.gridy = 7;
		panel.add(displayPriceComboBox, gbc_displayPriceComboBox);

		displayPriceComboBox.setSelectedIndex(0);
		displayPriceComboBox.addActionListener(actionListener);
		

		JLabel styleLabel = new JLabel("Style");
		styleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_styleLabel = new GridBagConstraints();
		gbc_styleLabel.anchor = GridBagConstraints.WEST;
		gbc_styleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_styleLabel.gridx = 1;
		gbc_styleLabel.gridy = 8;
		panel.add(styleLabel, gbc_styleLabel);

		styleComboBox = new JComboBox(new String[] { "Black", "Blue", "Agora" });
		styleComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		styleComboBox.setMinimumSize(new Dimension(100, 25));
		styleComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		styleComboBox.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_styleComboBox = new GridBagConstraints();
		gbc_styleComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_styleComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_styleComboBox.gridx = 3;
		gbc_styleComboBox.gridy = 8;
		panel.add(styleComboBox, gbc_styleComboBox);

		styleComboBox.addActionListener(new ActionListener() {

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
		gbc_descriptionLabel.gridy = 9;
		panel.add(descriptionLabel, gbc_descriptionLabel);

		descriptionWarningLabel = new JLabel("");
		descriptionWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_descriptionWarningLabel = new GridBagConstraints();
		gbc_descriptionWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_descriptionWarningLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_descriptionWarningLabel.gridx = 2;
		gbc_descriptionWarningLabel.gridy = 9;
		panel.add(descriptionWarningLabel, gbc_descriptionWarningLabel);

		textArea = new JTextArea();
		textArea.setBorder( new EmptyBorder(5, 5, 5, 5));
		textArea.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 50, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 3;
		gbc_textArea.gridy = 9;
		JScrollPane jScrollPane = new JScrollPane(textArea);
		jScrollPane.setPreferredSize(new Dimension(350, 200));
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(jScrollPane, gbc_textArea);

		updateContent();
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

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Book name is empty");
			nameWarningLabel.setIcon(sellSideBookEditor.getBugIcon());

			valid = false;
		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (sellSideBookEditor.getAbstractBusinessObject().getName() == null
					|| !sellSideBookEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				sellSideBookEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}

		if (dirtyFieldCheck(sellSideBookEditor.getSellSideBook().getValuta(), valutaTextField))
			dirty = true;

		if (sellSideBookEditor.getSellSideBook().getBankCalendar() != null
				&& !sellSideBookEditor.getSellSideBook().getBankCalendar().equals(bankCalendarComboBox.getSelectedItem()))
			dirty = true;

		if (sellSideBookEditor.getSellSideBook().getBankCalendar() == null)
			dirty = true;

		if (sellSideBookEditor.getSellSideBook().getDaycountConvention() != null
				&& !sellSideBookEditor.getSellSideBook().getDaycountConvention().equals(((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (sellSideBookEditor.getSellSideBook().getDaycountConvention() == null)
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getCalcMethod()!=null&&sellSideBookEditor.getSellSideBook().getCalcMethod().ordinal()!=((Integer)((ComboBoxEntry) calculationComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getCalcMethod() == null)
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getAbsolutChange()!=null&&!sellSideBookEditor.getSellSideBook().getAbsolutChange().equals(((ComboBoxEntry) displayChangeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getAbsolutChange() == null)
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getFractionalDisplay()!=null&&!sellSideBookEditor.getSellSideBook().getFractionalDisplay().equals(((ComboBoxEntry) displayPriceComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (sellSideBookEditor.getSellSideBook().getFractionalDisplay() == null)
			dirty = true;

		if (sellSideBookEditor.getSellSideBook().getDisplayStyle() != styleComboBox.getSelectedIndex())
			dirty = true;

		if (textArea.getText().trim().length() == 0) {

			descriptionWarningLabel.setToolTipText("Book description is empty");
			descriptionWarningLabel.setIcon(sellSideBookEditor.getWarningIcon());
		}
		else {

			descriptionWarningLabel.setToolTipText(null);
			descriptionWarningLabel.setIcon(null);
		}

		if (sellSideBookEditor.getSellSideBook().getDescription() != null && !sellSideBookEditor.getSellSideBook().getDescription().equals(textArea.getText())) {

			dirty = true;
		}

		if (sellSideBookEditor.getSellSideBook().getDescription() == null && textArea.getText().trim().length() > 0) {

			dirty = true;
		}

		consistent = consistent && valid;

		sellSideBookEditor.checkDirty();
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

		sellSideBookEditor.getSellSideBook().setName(nameTextField.getText().trim());
		sellSideBookEditor.getSellSideBook().setDaycountConvention((Integer) ((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry());
		sellSideBookEditor.getSellSideBook().setBankCalendar((BankCalendar) bankCalendarComboBox.getSelectedItem());
		sellSideBookEditor.getSellSideBook().setDisplayStyle(styleComboBox.getSelectedIndex());
		
		if(displayPriceComboBox.getSelectedIndex()==0)
			sellSideBookEditor.getSellSideBook().setFractionalDisplay(false);
		else
			sellSideBookEditor.getSellSideBook().setFractionalDisplay(true);
		
		switch(calculationComboBox.getSelectedIndex())
		{
			case 1:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.DEFAULT);
				break;
			case 2:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.DISC);
				break;
			case 3:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.MAT);
				break;
			case 4:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.ODDF);
				break;
			case 5:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.ODDL);
				break;
			case 6:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.TBILL);
				break;
			default:
				sellSideBookEditor.getSellSideBook().setCalcMethod(CalcMethod.NONE);
				break;
		}
		
		if(displayChangeComboBox.getSelectedIndex()==0)
			sellSideBookEditor.getSellSideBook().setAbsolutChange(true);
		else
			sellSideBookEditor.getSellSideBook().setAbsolutChange(false);

		if (valutaTextField.getText().trim().length() > 0)
			sellSideBookEditor.getSellSideBook().setValuta(Integer.parseInt(valutaTextField.getText()));
		else
			sellSideBookEditor.getSellSideBook().setValuta(0);
		sellSideBookEditor.getSellSideBook().setDescription(textArea.getText());
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(sellSideBookEditor.getSellSideBook().getName());

		textArea.setText(sellSideBookEditor.getSellSideBook().getDescription());

		styleComboBox.setSelectedIndex(sellSideBookEditor.getSellSideBook().getDisplayStyle());

		daycountComboBox.setSelectedItem(new ComboBoxEntry(sellSideBookEditor.getSellSideBook().getDaycountConvention(), null));
		
		if(sellSideBookEditor.getSellSideBook().getCalcMethod()!=null)
			switch(sellSideBookEditor.getSellSideBook().getCalcMethod())
			{
				case DEFAULT:
					calculationComboBox.setSelectedIndex(1);
					break;
				case DISC:
					calculationComboBox.setSelectedIndex(2);
					break;
				case MAT:
					calculationComboBox.setSelectedIndex(3);
					break;
				case NONE:
					calculationComboBox.setSelectedIndex(0);
					break;
				case ODDF:
					calculationComboBox.setSelectedIndex(4);
					break;
				case ODDL:
					calculationComboBox.setSelectedIndex(5);
					break;
				case TBILL:
					calculationComboBox.setSelectedIndex(6);
					break;
				default:
					calculationComboBox.setSelectedIndex(1);
					break;
				
			}
		
		if(sellSideBookEditor.getSellSideBook().getAbsolutChange()!=null)
		{
			if(sellSideBookEditor.getSellSideBook().getAbsolutChange()==true)
				displayChangeComboBox.setSelectedIndex(0);
			else
				displayChangeComboBox.setSelectedIndex(1);
		}

		if(sellSideBookEditor.getSellSideBook().getFractionalDisplay()!=null)
		{
			if(sellSideBookEditor.getSellSideBook().getFractionalDisplay()==true)
				displayPriceComboBox.setSelectedIndex(1);
			else
				displayPriceComboBox.setSelectedIndex(0);
		}
		
		if (sellSideBookEditor.getSellSideBook().getBankCalendar() != null)
			bankCalendarComboBox.setSelectedItem(sellSideBookEditor.getSellSideBook().getBankCalendar());
		else
			bankCalendarComboBox.setSelectedIndex(0);

		if (sellSideBookEditor.getSellSideBook().getValuta() != null)
			valutaTextField.setText(integerFormat.format(sellSideBookEditor.getSellSideBook().getValuta()));
		else
			valutaTextField.setText("0");

		if (sellSideBookEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(sellSideBookEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer.append(DateFormat.getDateInstance(DateFormat.SHORT).format(sellSideBookEditor.getAbstractBusinessObject().getModificationDate()));

			if (sellSideBookEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(sellSideBookEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		if (sellSideBookEditor.getBasisClientConnector().getFUser().canWrite(sellSideBookEditor.getAbstractBusinessObject())
				&& !(sellSideBookEditor.getAbstractBusinessObject().getName() != null && sellSideBookEditor.getAbstractBusinessObject().getName()
						.equals("Admin Role"))) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);

			valutaTextField.setBackground(new Color(255, 243, 204));
			valutaTextField.setEditable(true);

			daycountComboBox.setBackground(new Color(255, 243, 204));
			daycountComboBox.setEnabled(true);

			calculationComboBox.setBackground(new Color(255, 243, 204));
			calculationComboBox.setEnabled(true);
			
			displayChangeComboBox.setBackground(new Color(255, 243, 204));
			displayChangeComboBox.setEnabled(true);
			
			displayPriceComboBox.setBackground(new Color(255, 243, 204));
			displayPriceComboBox.setEnabled(true);
			
			bankCalendarComboBox.setBackground(new Color(255, 243, 204));
			bankCalendarComboBox.setEnabled(true);

			textArea.setBackground(new Color(255, 243, 204));
			textArea.setEditable(true);

			styleComboBox.setBackground(new Color(255, 243, 204));
			styleComboBox.setEnabled(true);
		}
		else {
			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);

			valutaTextField.setBackground(new Color(204, 216, 255));
			valutaTextField.setEditable(false);

			daycountComboBox.setBackground(new Color(204, 216, 255));
			daycountComboBox.setEnabled(false);
			
			calculationComboBox.setBackground(new Color(204, 216, 255));
			calculationComboBox.setEnabled(false);
			
			displayChangeComboBox.setBackground(new Color(204, 216, 255));
			displayChangeComboBox.setEnabled(false);
			
			displayPriceComboBox.setBackground(new Color(204, 216, 255));
			displayPriceComboBox.setEnabled(false);

			bankCalendarComboBox.setBackground(new Color(204, 216, 255));
			bankCalendarComboBox.setEnabled(false);

			textArea.setBackground(new Color(204, 216, 255));
			textArea.setEditable(false);

			styleComboBox.setBackground(new Color(204, 216, 255));
			styleComboBox.setEnabled(false);
		}

		checkConsistency();
	}
}
