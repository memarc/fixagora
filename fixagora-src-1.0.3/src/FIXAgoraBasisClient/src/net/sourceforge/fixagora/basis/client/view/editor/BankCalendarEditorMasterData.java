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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jquantlib.time.Date;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.document.IntegerDocument;

/**
 * The Class BankCalendarEditorMasterData.
 */
public class BankCalendarEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private BankCalendarEditor bankCalendarEditor = null;

	private final ImageIcon prevImageIcon = new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1leftarrow.png"));

	private final ImageIcon nextImageIcon = new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1rightarrow.png"));

	private Calendar calendar = Calendar.getInstance();

	private JLabel[][] jLabel = new JLabel[6][8];

	private JComboBox monthComboBox;

	private JTextField yearTextField;

	/**
	 * Instantiates a new bank calendar editor master data.
	 *
	 * @param bankCalendarEditor the bank calendar editor
	 */
	public BankCalendarEditorMasterData(BankCalendarEditor bankCalendarEditor) {

		super();

		calendar.set(Calendar.DAY_OF_MONTH, 1);

		this.bankCalendarEditor = bankCalendarEditor;

		setBorder(new EmptyBorder(0, 0, 0, 0));
		getViewport().setBackground(new Color(204, 216, 255));

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);

		this.bankCalendarEditor = bankCalendarEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.weightx = 1.0;
		gbc_leftFillPanel.gridheight = 8;
		gbc_leftFillPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JButton prevMonthButton = new JButton("");
		prevMonthButton.setPreferredSize(new Dimension(25, 25));
		prevMonthButton.setIcon(prevImageIcon);
		GridBagConstraints gbc_prevMonthButton = new GridBagConstraints();
		gbc_prevMonthButton.insets = new Insets(50, 50, 50, 5);
		gbc_prevMonthButton.gridx = 1;
		gbc_prevMonthButton.gridy = 0;
		panel.add(prevMonthButton, gbc_prevMonthButton);

		List<ComboBoxEntry> monthComboBoxEntries = new ArrayList<ComboBoxEntry>();
		monthComboBoxEntries.add(new ComboBoxEntry());
		monthComboBoxEntries.add(new ComboBoxEntry(0, "January"));
		monthComboBoxEntries.add(new ComboBoxEntry(1, "February"));
		monthComboBoxEntries.add(new ComboBoxEntry(2, "March"));
		monthComboBoxEntries.add(new ComboBoxEntry(3, "April"));
		monthComboBoxEntries.add(new ComboBoxEntry(4, "May"));
		monthComboBoxEntries.add(new ComboBoxEntry(5, "June"));
		monthComboBoxEntries.add(new ComboBoxEntry(6, "July"));
		monthComboBoxEntries.add(new ComboBoxEntry(7, "August"));
		monthComboBoxEntries.add(new ComboBoxEntry(8, "September"));
		monthComboBoxEntries.add(new ComboBoxEntry(9, "October"));
		monthComboBoxEntries.add(new ComboBoxEntry(10, "November"));
		monthComboBoxEntries.add(new ComboBoxEntry(11, "December"));

		monthComboBox = new JComboBox(monthComboBoxEntries.toArray());
		monthComboBox.setBackground(new Color(255, 243, 204));
		monthComboBox.setMinimumSize(new Dimension(94, 25));
		monthComboBox.setPreferredSize(new Dimension(94, 25));
		monthComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		monthComboBox.setSelectedItem(new ComboBoxEntry(calendar.get(Calendar.MONTH), null));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(50, 0, 50, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		panel.add(monthComboBox, gbc_lblNewLabel);

		JButton nextMonthButton = new JButton("");
		nextMonthButton.setPreferredSize(new Dimension(25, 25));
		nextMonthButton.setIcon(nextImageIcon);
		GridBagConstraints gbc_nextMonthButton = new GridBagConstraints();
		gbc_nextMonthButton.insets = new Insets(50, 0, 50, 15);
		gbc_nextMonthButton.gridx = 3;
		gbc_nextMonthButton.gridy = 0;
		panel.add(nextMonthButton, gbc_nextMonthButton);

		JButton prevYearButton = new JButton("");
		prevYearButton.setPreferredSize(new Dimension(25, 25));
		prevYearButton.setIcon(prevImageIcon);
		GridBagConstraints gbc_prevYearButton = new GridBagConstraints();
		gbc_prevYearButton.insets = new Insets(50, 0, 50, 5);
		gbc_prevYearButton.gridx = 4;
		gbc_prevYearButton.gridy = 0;
		panel.add(prevYearButton, gbc_prevYearButton);

		yearTextField = new JTextField();
		yearTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		yearTextField.setDocument(new IntegerDocument());
		yearTextField.setText(Integer.toString(calendar.get(Calendar.YEAR)));
		yearTextField.setBackground(new Color(255, 243, 204));
		yearTextField.setPreferredSize(new Dimension(52, 25));
		GridBagConstraints gbc_yearTextField = new GridBagConstraints();
		gbc_yearTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_yearTextField.insets = new Insets(50, 0, 50, 5);
		gbc_yearTextField.gridx = 5;
		gbc_yearTextField.gridy = 0;
		panel.add(yearTextField, gbc_yearTextField);

		yearTextField.getDocument().addDocumentListener(new DocumentListener() {

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

				if (yearTextField.getText().length() > 0) {
					calendar.set(Calendar.YEAR, Integer.parseInt(yearTextField.getText()));
				}
				updateContent();

			}
		});

		JButton nextYearButton = new JButton("");
		nextYearButton.setPreferredSize(new Dimension(25, 25));
		nextYearButton.setIcon(nextImageIcon);
		GridBagConstraints gbc_nextYearButton = new GridBagConstraints();
		gbc_nextYearButton.insets = new Insets(50, 0, 50, 50);
		gbc_nextYearButton.gridx = 6;
		gbc_nextYearButton.gridy = 0;
		panel.add(nextYearButton, gbc_nextYearButton);

		monthComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calendar.set(Calendar.MONTH, (Integer) ((ComboBoxEntry) monthComboBox.getSelectedItem()).getEntry());
				updateContent();
			}
		});

		prevMonthButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calendar.add(Calendar.MONTH, -1);
				monthComboBox.setSelectedItem(new ComboBoxEntry(calendar.get(Calendar.MONTH), null));
				yearTextField.setText(Integer.toString(calendar.get(Calendar.YEAR)));
				updateContent();

			}
		});

		nextMonthButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calendar.add(Calendar.MONTH, 1);
				monthComboBox.setSelectedItem(new ComboBoxEntry(calendar.get(Calendar.MONTH), null));
				yearTextField.setText(Integer.toString(calendar.get(Calendar.YEAR)));
				updateContent();

			}
		});

		prevYearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calendar.add(Calendar.YEAR, -1);
				yearTextField.setText(Integer.toString(calendar.get(Calendar.YEAR)));
				updateContent();

			}
		});

		nextYearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calendar.add(Calendar.YEAR, 1);
				yearTextField.setText(Integer.toString(calendar.get(Calendar.YEAR)));
				updateContent();

			}
		});

		JPanel rightFillPanel = new JPanel();
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.gridheight = 8;
		gbc_rightFillPanel.weightx = 1.0;
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 7;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		JPanel monthPanel = new JPanel();
		GridBagConstraints gbc_monthPanel = new GridBagConstraints();
		gbc_monthPanel.anchor = GridBagConstraints.NORTH;
		gbc_monthPanel.gridwidth = 6;
		gbc_monthPanel.insets = new Insets(0, 0, 50, 5);
		gbc_monthPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_monthPanel.gridx = 1;
		gbc_monthPanel.gridy = 1;
		panel.add(monthPanel, gbc_monthPanel);
		GridBagLayout gbl_monthPanel = new GridBagLayout();
		gbl_monthPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_monthPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_monthPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_monthPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		monthPanel.setLayout(gbl_monthPanel);

		JLabel monLabel = new JLabel("Mon");
		monLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_monLabel = new GridBagConstraints();
		gbc_monLabel.insets = new Insets(1, 1, 1, 1);
		gbc_monLabel.gridx = 1;
		gbc_monLabel.gridy = 0;
		monthPanel.add(monLabel, gbc_monLabel);

		JLabel tueLabel = new JLabel("Tue");
		tueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_tueLabel = new GridBagConstraints();
		gbc_tueLabel.insets = new Insets(1, 1, 1, 1);
		gbc_tueLabel.gridx = 2;
		gbc_tueLabel.gridy = 0;
		monthPanel.add(tueLabel, gbc_tueLabel);

		JLabel wedLabel = new JLabel("Wed");
		wedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_wedLabel = new GridBagConstraints();
		gbc_wedLabel.insets = new Insets(1, 1, 1, 1);
		gbc_wedLabel.gridx = 3;
		gbc_wedLabel.gridy = 0;
		monthPanel.add(wedLabel, gbc_wedLabel);

		JLabel thuLabel = new JLabel("Thu");
		thuLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_thuLabel = new GridBagConstraints();
		gbc_thuLabel.insets = new Insets(1, 1, 1, 1);
		gbc_thuLabel.gridx = 4;
		gbc_thuLabel.gridy = 0;
		monthPanel.add(thuLabel, gbc_thuLabel);

		JLabel friLabel = new JLabel("Fri");
		friLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_friLabel = new GridBagConstraints();
		gbc_friLabel.insets = new Insets(1, 1, 1, 1);
		gbc_friLabel.gridx = 5;
		gbc_friLabel.gridy = 0;
		monthPanel.add(friLabel, gbc_friLabel);

		JLabel satLabel = new JLabel("Sat");
		satLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_satLabel = new GridBagConstraints();
		gbc_satLabel.insets = new Insets(1, 1, 1, 1);
		gbc_satLabel.gridx = 6;
		gbc_satLabel.gridy = 0;
		monthPanel.add(satLabel, gbc_satLabel);

		JLabel sunLabel = new JLabel("Sun");
		sunLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sunLabel = new GridBagConstraints();
		gbc_sunLabel.insets = new Insets(1, 1, 1, 1);
		gbc_sunLabel.gridx = 7;
		gbc_sunLabel.gridy = 0;
		monthPanel.add(sunLabel, gbc_sunLabel);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.WEST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		panel.add(panel_1, gbc_panel_1);

		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 8; j++) {
				jLabel[i][j] = new JLabel("");
				jLabel[i][j].setFont(new Font("Dialog", Font.PLAIN, 12));
				jLabel[i][j].setOpaque(true);
				jLabel[i][j].setBorder(new EmptyBorder(10, 10, 10, 10));
				jLabel[i][j].setHorizontalAlignment(SwingConstants.RIGHT);
				jLabel[i][j].setBackground(new Color(204, 216, 255));
				GridBagConstraints gbc_jLabel = new GridBagConstraints();
				gbc_jLabel.insets = new Insets(1, 1, 0, 0);
				gbc_jLabel.gridx = j;
				gbc_jLabel.gridy = i + 1;
				gbc_jLabel.fill = GridBagConstraints.BOTH;
				monthPanel.add(jLabel[i][j], gbc_jLabel);
			}

		updateContent();
	}

	private void updateContent() {

		if (calendar.get(Calendar.YEAR) < 1901 || calendar.get(Calendar.YEAR) > 2199) {
			yearTextField.setBackground(new Color(255, 198, 179));
		}
		else {
			yearTextField.setBackground(new Color(255, 243, 204));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.calendar.getTime());
			while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
				calendar.add(Calendar.DAY_OF_YEAR, -1);
			for (int i = 0; i < 6; i++) {
				jLabel[i][0].setText(Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR)));
				for (int j = 1; j < 8; j++) {
					jLabel[i][j].setForeground(Color.BLACK);
					jLabel[i][j].setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
					if (calendar.get(Calendar.MONTH) == this.calendar.get(Calendar.MONTH)) {
						if (bankCalendarEditor.getBankCalendar().getCalendar().isBusinessDay(new Date(calendar.getTime())))
							jLabel[i][j].setBackground(new Color(255, 243, 204));
						else
							jLabel[i][j].setBackground(new Color(255, 198, 179));
					}
					else {
						jLabel[i][j].setBackground(new Color(204, 216, 255));
						if (!bankCalendarEditor.getBankCalendar().getCalendar().isBusinessDay(new Date(calendar.getTime())))
							jLabel[i][j].setForeground(new Color(255, 152, 117));
					}
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

			}
		}
	}

}
