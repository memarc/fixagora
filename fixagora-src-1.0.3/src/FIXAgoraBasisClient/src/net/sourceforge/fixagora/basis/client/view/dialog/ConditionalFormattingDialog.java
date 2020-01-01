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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition.ConditionType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition.IsType;

/**
 * The Class ConditionalFormattingDialog.
 */
public class ConditionalFormattingDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private String[] comboValues = new String[]{"equal to","greater than","less than","less than or equal to","greater than or equal to","not equal to","between","not between"};
	private JTextField condition1TextField;
	private JTextField condition1betweenTextField1;
	private JTextField condition1betweenTextField2;
	private JCheckBox condition1CheckBox;
	private JComboBox condition1ComboBox;
	private JPanel condition1Panel;
	private JTextField condition1FormulaTextField;
	private CardLayout condition1Cardlayout;
	private JPanel condition1CellValuePanel;
	private JComboBox condition1CellValueComboBox;
	private JPanel condition1ValuePanel;
	private CardLayout condition1CardLayout2;
	private JCheckBox condition1FadeoutCheckBox;
	private JButton condition1BackgroundButton;
	private JButton condition1ForegroundButton;
	private Color condition1Foreground = Color.BLACK;
	private Color condition1Background = new Color(255, 243, 204);
	private Color condition2Foreground = Color.BLACK;
	private Color condition2Background = new Color(255, 243, 204);
	private Color condition3Foreground = Color.BLACK;
	private Color condition3Background = new Color(255, 243, 204);
	private JLabel condition1ForegroundLabel;
	private JLabel condition1BackgroundLabel;
	private JCheckBox condition2CheckBox;
	private JComboBox condition2ComboBox;
	private JPanel condition2Panel;
	private CardLayout condition2Cardlayout;
	private JTextField condition2FormulaTextField;
	private JPanel condition2CellValuePanel;
	private JComboBox condition2CellValueComboBox;
	private JPanel condition2ValuePanel;
	private CardLayout condition2CardLayout2;
	private JTextField condition2TextField;
	private JTextField condition2betweenTextField1;
	private JTextField condition2betweenTextField2;
	private JLabel condition2ForegroundLabel;
	private JButton condition2ForegroundButton;
	private JLabel condition2BackgroundLabel;
	private JButton condition2BackgroundButton;
	private JCheckBox condition2FadeoutCheckBox;
	private JCheckBox condition3CheckBox;
	private JComboBox condition3ComboBox;
	private JPanel condition3Panel;
	private CardLayout condition3Cardlayout;
	private JTextField condition3FormulaTextField;
	private JPanel condition3CellValuePanel;
	private JComboBox condition3CellValueComboBox;
	private JPanel condition3ValuePanel;
	private CardLayout condition3CardLayout2;
	private JTextField condition3TextField;
	private JTextField condition3betweenTextField1;
	private JTextField condition3betweenTextField2;
	private JLabel condition3ForegroundLabel;
	private JButton condition3ForegroundButton;
	private JLabel condition3BackgroundLabel;
	private JButton condition3BackgroundButton;
	private JCheckBox condition3FadeoutCheckBox;

	/**
	 * Instantiates a new conditional formatting dialog.
	 *
	 * @param spreadSheetConditionalFormat the spread sheet conditional format
	 */
	public ConditionalFormattingDialog(SpreadSheetConditionalFormat spreadSheetConditionalFormat) {

		setBounds(100, 100, 669, 440);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(ConditionalFormattingDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		
		DocumentListener documentListener = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
			
				checkOkButton();
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
			
				checkOkButton();
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
			
				checkOkButton();
				
			}
		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 52, 0, 0, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		condition1CheckBox = new JCheckBox("Condition 1");
		condition1CheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition1CheckBox.setOpaque(false);
		GridBagConstraints gbc_condition1CheckBox = new GridBagConstraints();
		gbc_condition1CheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition1CheckBox.insets = new Insets(25, 25, 5, 15);
		gbc_condition1CheckBox.gridx = 0;
		gbc_condition1CheckBox.gridy = 0;
		contentPanel.add(condition1CheckBox, gbc_condition1CheckBox);

		condition1ComboBox= new JComboBox(new String[]{"Formula is","Cell value is"});
		condition1ComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));		
		condition1ComboBox.setBackground(new Color(204, 216, 255));
		condition1ComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition1ComboBox.setSelectedIndex(0);
		condition1ComboBox.setEnabled(false);
		
		GridBagConstraints gbc_condition1ComboBox = new GridBagConstraints();
		gbc_condition1ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition1ComboBox.insets = new Insets(0, 25, 5, 5);
		gbc_condition1ComboBox.gridx = 0;
		gbc_condition1ComboBox.gridy = 1;
		contentPanel.add(condition1ComboBox, gbc_condition1ComboBox);

		condition1Panel = new JPanel();
		condition1Panel.setEnabled(false);
		GridBagConstraints gbc_condition1Panel = new GridBagConstraints();
		gbc_condition1Panel.gridwidth = 5;
		gbc_condition1Panel.insets = new Insets(0, 0, 5, 25);
		gbc_condition1Panel.fill = GridBagConstraints.BOTH;
		gbc_condition1Panel.gridx = 1;
		gbc_condition1Panel.gridy = 1;
		contentPanel.add(condition1Panel, gbc_condition1Panel);
		condition1Cardlayout = new CardLayout(0, 0);
		condition1Panel.setLayout(condition1Cardlayout);
		
		condition1FormulaTextField = new JTextField();
		condition1Panel.add(condition1FormulaTextField, "condition1FormulaTextField");
		condition1FormulaTextField.setHorizontalAlignment(SwingConstants.LEFT);
		condition1FormulaTextField.setPreferredSize(new Dimension(200, 25));
		condition1FormulaTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition1FormulaTextField.setBackground(new Color(204, 216, 255));
		condition1FormulaTextField.setEnabled(false);
		condition1FormulaTextField.setColumns(4);
		condition1FormulaTextField.getDocument().addDocumentListener(documentListener);
		
		condition1CellValuePanel = new JPanel();
		condition1CellValuePanel.setBackground(new Color(204, 216, 255));
		condition1Panel.add(condition1CellValuePanel, "condition1CellValuePanel");
		GridBagLayout gbl_condition1CellValuePanel = new GridBagLayout();
		gbl_condition1CellValuePanel.rowWeights = new double[]{0.0};
		gbl_condition1CellValuePanel.columnWeights = new double[]{0.0, 1.0};
		condition1CellValuePanel.setLayout(gbl_condition1CellValuePanel);
		
		condition1CellValueComboBox = new JComboBox(comboValues);
		condition1CellValueComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition1CellValueComboBox.setBackground(new Color(204, 216, 255));
		condition1CellValueComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition1CellValueComboBox.setSelectedIndex(0);
		condition1CellValueComboBox.setEnabled(false);

		GridBagConstraints gbc_condition1CellValueComboBox = new GridBagConstraints();
		gbc_condition1CellValueComboBox.insets = new Insets(0, 0, 0, 10);
		gbc_condition1CellValueComboBox.fill = GridBagConstraints.BOTH;
		gbc_condition1CellValueComboBox.gridx = 0;
		gbc_condition1CellValueComboBox.gridy = 0;
		condition1CellValuePanel.add(condition1CellValueComboBox, gbc_condition1CellValueComboBox);
		
		condition1ValuePanel = new JPanel();
		condition1ValuePanel.setBackground(new Color(204, 216, 255));
		GridBagConstraints gbc_condition1ValuePanel = new GridBagConstraints();
		gbc_condition1ValuePanel.fill = GridBagConstraints.BOTH;
		gbc_condition1ValuePanel.gridx = 1;
		gbc_condition1ValuePanel.gridy = 0;
		condition1CellValuePanel.add(condition1ValuePanel, gbc_condition1ValuePanel);
		condition1CardLayout2 = new CardLayout(0, 0);
		condition1ValuePanel.setLayout(condition1CardLayout2);
		
		condition1TextField = new JTextField();
		condition1TextField.setPreferredSize(new Dimension(200, 25));
		condition1TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		condition1TextField.setColumns(4);
		condition1TextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition1TextField.setBackground(new Color(204, 216, 255));
		condition1TextField.setEnabled(false);
		condition1TextField.setDocument(new DoubleDocument());
		condition1TextField.getDocument().addDocumentListener(documentListener);
		condition1ValuePanel.add(condition1TextField, "condition1TextField");
		
		JPanel condition1betweenValuePanel = new JPanel();
		condition1betweenValuePanel.setBackground(new Color(204, 216, 255));
		condition1ValuePanel.add(condition1betweenValuePanel, "condition1betweenValuePanel");
		GridBagLayout gbl_condition1betweenValuePanel = new GridBagLayout();
		gbl_condition1betweenValuePanel.columnWeights = new double[]{1.0, 0.0, 1.0};
		condition1betweenValuePanel.setLayout(gbl_condition1betweenValuePanel);
		
		condition1betweenTextField1 = new JTextField();
		condition1betweenTextField1.setText("0");
		condition1betweenTextField1.setPreferredSize(new Dimension(200, 25));
		condition1betweenTextField1.setHorizontalAlignment(SwingConstants.RIGHT);
		condition1betweenTextField1.setColumns(4);
		condition1betweenTextField1.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition1betweenTextField1.setBackground(new Color(204, 216, 255));
		condition1betweenTextField1.setDocument(new DoubleDocument());
		condition1betweenTextField1.getDocument().addDocumentListener(documentListener);
		condition1betweenTextField1.setEnabled(false);
		GridBagConstraints gbc_condition1betweenTextField1 = new GridBagConstraints();
		gbc_condition1betweenTextField1.insets = new Insets(0, 0, 0, 5);
		gbc_condition1betweenTextField1.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition1betweenTextField1.gridx = 0;
		gbc_condition1betweenTextField1.gridy = 0;
		condition1betweenValuePanel.add(condition1betweenTextField1, gbc_condition1betweenTextField1);
		
		JLabel lblNewcondition1AndLabelLabel = new JLabel("and");
		lblNewcondition1AndLabelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewcondition1AndLabelLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewcondition1AndLabelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewcondition1AndLabelLabel = new GridBagConstraints();
		gbc_lblNewcondition1AndLabelLabel.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewcondition1AndLabelLabel.anchor = GridBagConstraints.CENTER;
		gbc_lblNewcondition1AndLabelLabel.gridx = 1;
		gbc_lblNewcondition1AndLabelLabel.gridy = 0;
		condition1betweenValuePanel.add(lblNewcondition1AndLabelLabel, gbc_lblNewcondition1AndLabelLabel);
		
		condition1betweenTextField2 = new JTextField();
		condition1betweenTextField2.setPreferredSize(new Dimension(200, 25));
		condition1betweenTextField2.setHorizontalAlignment(SwingConstants.RIGHT);
		condition1betweenTextField2.setColumns(4);
		condition1betweenTextField2.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition1betweenTextField2.setBackground(new Color(204, 216, 255));
		condition1betweenTextField2.setDocument(new DoubleDocument());
		condition1betweenTextField2.getDocument().addDocumentListener(documentListener);
		condition1betweenTextField2.setEnabled(false);
		GridBagConstraints gbc_condition1betweenTextField2 = new GridBagConstraints();
		gbc_condition1betweenTextField2.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition1betweenTextField2.gridx = 2;
		gbc_condition1betweenTextField2.gridy = 0;
		condition1betweenValuePanel.add(condition1betweenTextField2, gbc_condition1betweenTextField2);
		
		JLabel condition1CellStyleLabel1 = new JLabel("Cell Style");
		condition1CellStyleLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_cellStyleLabel1 = new GridBagConstraints();
		gbc_cellStyleLabel1.anchor = GridBagConstraints.WEST;
		gbc_cellStyleLabel1.insets = new Insets(0, 25, 5, 5);
		gbc_cellStyleLabel1.gridx = 0;
		gbc_cellStyleLabel1.gridy = 2;
		contentPanel.add(condition1CellStyleLabel1, gbc_cellStyleLabel1);

		condition1ForegroundLabel = new JLabel("");
		condition1ForegroundLabel.setOpaque(true);
		condition1ForegroundLabel.setBackground(condition1Foreground);
		condition1ForegroundLabel.setPreferredSize(new Dimension(50, 25));
		condition1ForegroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_condition1ForegroundLabel = new GridBagConstraints();
		gbc_condition1ForegroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition1ForegroundLabel.gridx = 1;
		gbc_condition1ForegroundLabel.gridy = 2;
		contentPanel.add(condition1ForegroundLabel, gbc_condition1ForegroundLabel);

		condition1ForegroundButton = new JButton("Foreground");
		condition1ForegroundButton.setPreferredSize(new Dimension(110, 25));
		condition1ForegroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition1ForegroundButton.setEnabled(false);
		GridBagConstraints gbc_condition1ForegroundButton = new GridBagConstraints();
		gbc_condition1ForegroundButton.insets = new Insets(0, 0, 5, 25);
		gbc_condition1ForegroundButton.gridx = 2;
		gbc_condition1ForegroundButton.gridy = 2;
		contentPanel.add(condition1ForegroundButton, gbc_condition1ForegroundButton);
		
		condition1ForegroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Foreground", condition1ForegroundLabel.getBackground());
				if (color != null) {
					condition1Foreground = color;
					condition1ForegroundLabel.setBackground(color);
				}
				
			}
		});

		condition1BackgroundLabel = new JLabel("");
		condition1BackgroundLabel.setBackground(condition1Background);
		condition1BackgroundLabel.setOpaque(true);
		condition1BackgroundLabel.setMinimumSize(new Dimension(50, 25));
		condition1BackgroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		condition1BackgroundLabel.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_condition1BackgroundLabel = new GridBagConstraints();
		gbc_condition1BackgroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition1BackgroundLabel.gridx = 3;
		gbc_condition1BackgroundLabel.gridy = 2;
		contentPanel.add(condition1BackgroundLabel, gbc_condition1BackgroundLabel);

		condition1BackgroundButton = new JButton("Background");
		condition1BackgroundButton.setPreferredSize(new Dimension(110, 25));
		condition1BackgroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition1BackgroundButton.setEnabled(false);
		GridBagConstraints gbc_condition1BackgroundButton = new GridBagConstraints();
		gbc_condition1BackgroundButton.insets = new Insets(0, 0, 5, 5);
		gbc_condition1BackgroundButton.gridx = 4;
		gbc_condition1BackgroundButton.gridy = 2;
		contentPanel.add(condition1BackgroundButton, gbc_condition1BackgroundButton);
		
		condition1BackgroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Background", condition1BackgroundLabel.getBackground());
				if (color != null) {
					condition1Background = color;
					condition1BackgroundLabel.setBackground(color);
				}
				
			}
		});

		condition1FadeoutCheckBox = new JCheckBox("Fade out");
		condition1FadeoutCheckBox.setOpaque(false);
		condition1FadeoutCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition1FadeoutCheckBox.setEnabled(false);
		GridBagConstraints gbc_condition1FadeoutCheckBox = new GridBagConstraints();
		gbc_condition1FadeoutCheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition1FadeoutCheckBox.insets = new Insets(0, 25, 5, 25);
		gbc_condition1FadeoutCheckBox.gridx = 5;
		gbc_condition1FadeoutCheckBox.gridy = 2;
		contentPanel.add(condition1FadeoutCheckBox, gbc_condition1FadeoutCheckBox);
		
		condition1CheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition1CheckBox.isSelected())
				{
					condition1ComboBox.setBackground(new Color(255, 243, 204));
					condition1ComboBox.setEnabled(true);
					condition1FormulaTextField.setBackground(new Color(255, 243, 204));
					condition1FormulaTextField.setEnabled(true);
					condition1CellValueComboBox.setBackground(new Color(255, 243, 204));
					condition1CellValueComboBox.setEnabled(true);
					condition1TextField.setBackground(new Color(255, 243, 204));
					condition1TextField.setEnabled(true);
					condition1betweenTextField1.setBackground(new Color(255, 243, 204));
					condition1betweenTextField1.setEnabled(true);
					condition1betweenTextField2.setBackground(new Color(255, 243, 204));
					condition1betweenTextField2.setEnabled(true);
					condition1ForegroundButton.setEnabled(true);
					condition1BackgroundButton.setEnabled(true);
					condition1FadeoutCheckBox.setEnabled(true);
				}
				else
				{
					condition1ComboBox.setBackground(new Color(204, 216, 255));
					condition1ComboBox.setEnabled(false);
					condition1FormulaTextField.setBackground(new Color(204, 216, 255));
					condition1FormulaTextField.setEnabled(false);
					condition1CellValueComboBox.setBackground(new Color(204, 216, 255));
					condition1CellValueComboBox.setEnabled(false);
					condition1TextField.setBackground(new Color(204, 216, 255));
					condition1TextField.setEnabled(false);
					condition1betweenTextField1.setBackground(new Color(204, 216, 255));
					condition1betweenTextField1.setEnabled(false);
					condition1betweenTextField2.setBackground(new Color(204, 216, 255));
					condition1betweenTextField2.setEnabled(false);
					condition1ForegroundButton.setEnabled(false);
					condition1BackgroundButton.setEnabled(false);
					condition1FadeoutCheckBox.setEnabled(false);

				}
				checkOkButton();
			}
			
		});
		
		condition1ComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition1ComboBox.getSelectedIndex()==1)
				{
					condition1Cardlayout.show(condition1Panel,"condition1CellValuePanel");
				}
				else
				{
					condition1Cardlayout.show(condition1Panel,"condition1FormulaTextField");
				}
				checkOkButton();
			}
		});
		
		condition1CellValueComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition1CellValueComboBox.getSelectedIndex()>5)
				{
					condition1CardLayout2.show(condition1ValuePanel,"condition1betweenValuePanel");
				}
				else
				{
					condition1CardLayout2.show(condition1ValuePanel,"condition1TextField");
				}
				checkOkButton();
			}
		});
		

		//
		
		condition2CheckBox = new JCheckBox("Condition 2");
		condition2CheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition2CheckBox.setOpaque(false);
		GridBagConstraints gbc_condition2CheckBox = new GridBagConstraints();
		gbc_condition2CheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition2CheckBox.insets = new Insets(25, 25, 5, 15);
		gbc_condition2CheckBox.gridx = 0;
		gbc_condition2CheckBox.gridy = 3;
		contentPanel.add(condition2CheckBox, gbc_condition2CheckBox);

		condition2ComboBox= new JComboBox(new String[]{"Formula is","Cell value is"});
		condition2ComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));		
		condition2ComboBox.setBackground(new Color(204, 216, 255));
		condition2ComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition2ComboBox.setSelectedIndex(0);
		condition2ComboBox.setEnabled(false);
		
		GridBagConstraints gbc_condition2ComboBox = new GridBagConstraints();
		gbc_condition2ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition2ComboBox.insets = new Insets(0, 25, 5, 5);
		gbc_condition2ComboBox.gridx = 0;
		gbc_condition2ComboBox.gridy = 4;
		contentPanel.add(condition2ComboBox, gbc_condition2ComboBox);

		condition2Panel = new JPanel();
		condition2Panel.setEnabled(false);
		GridBagConstraints gbc_condition2Panel = new GridBagConstraints();
		gbc_condition2Panel.gridwidth = 5;
		gbc_condition2Panel.insets = new Insets(0, 0, 5, 25);
		gbc_condition2Panel.fill = GridBagConstraints.BOTH;
		gbc_condition2Panel.gridx = 1;
		gbc_condition2Panel.gridy = 4;
		contentPanel.add(condition2Panel, gbc_condition2Panel);
		condition2Cardlayout = new CardLayout(0, 0);
		condition2Panel.setLayout(condition2Cardlayout);
		
		condition2FormulaTextField = new JTextField();
		condition2Panel.add(condition2FormulaTextField, "condition2FormulaTextField");
		condition2FormulaTextField.setHorizontalAlignment(SwingConstants.LEFT);
		condition2FormulaTextField.setPreferredSize(new Dimension(200, 25));
		condition2FormulaTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition2FormulaTextField.setBackground(new Color(204, 216, 255));
		condition2FormulaTextField.setEnabled(false);
		condition2FormulaTextField.setColumns(4);
		condition2FormulaTextField.getDocument().addDocumentListener(documentListener);
		
		condition2CellValuePanel = new JPanel();
		condition2CellValuePanel.setBackground(new Color(204, 216, 255));
		condition2Panel.add(condition2CellValuePanel, "condition2CellValuePanel");
		GridBagLayout gbl_condition2CellValuePanel = new GridBagLayout();
		gbl_condition2CellValuePanel.rowWeights = new double[]{0.0};
		gbl_condition2CellValuePanel.columnWeights = new double[]{0.0, 1.0};
		condition2CellValuePanel.setLayout(gbl_condition2CellValuePanel);
		
		condition2CellValueComboBox = new JComboBox(comboValues);
		condition2CellValueComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition2CellValueComboBox.setBackground(new Color(204, 216, 255));
		condition2CellValueComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition2CellValueComboBox.setSelectedIndex(0);
		condition2CellValueComboBox.setEnabled(false);

		GridBagConstraints gbc_condition2CellValueComboBox = new GridBagConstraints();
		gbc_condition2CellValueComboBox.insets = new Insets(0, 0, 0, 10);
		gbc_condition2CellValueComboBox.fill = GridBagConstraints.BOTH;
		gbc_condition2CellValueComboBox.gridx = 0;
		gbc_condition2CellValueComboBox.gridy = 0;
		condition2CellValuePanel.add(condition2CellValueComboBox, gbc_condition2CellValueComboBox);
		
		condition2ValuePanel = new JPanel();
		condition2ValuePanel.setBackground(new Color(204, 216, 255));
		GridBagConstraints gbc_condition2ValuePanel = new GridBagConstraints();
		gbc_condition2ValuePanel.fill = GridBagConstraints.BOTH;
		gbc_condition2ValuePanel.gridx = 1;
		gbc_condition2ValuePanel.gridy = 0;
		condition2CellValuePanel.add(condition2ValuePanel, gbc_condition2ValuePanel);
		condition2CardLayout2 = new CardLayout(0, 0);
		condition2ValuePanel.setLayout(condition2CardLayout2);
		
		condition2TextField = new JTextField();
		condition2TextField.setPreferredSize(new Dimension(200, 25));
		condition2TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		condition2TextField.setColumns(4);
		condition2TextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition2TextField.setBackground(new Color(204, 216, 255));
		condition2TextField.setEnabled(false);
		condition2TextField.setDocument(new DoubleDocument());
		condition2TextField.getDocument().addDocumentListener(documentListener);
		condition2ValuePanel.add(condition2TextField, "condition2TextField");
		
		JPanel condition2betweenValuePanel = new JPanel();
		condition2betweenValuePanel.setBackground(new Color(204, 216, 255));
		condition2ValuePanel.add(condition2betweenValuePanel, "condition2betweenValuePanel");
		GridBagLayout gbl_condition2betweenValuePanel = new GridBagLayout();
		gbl_condition2betweenValuePanel.columnWeights = new double[]{1.0, 0.0, 1.0};
		condition2betweenValuePanel.setLayout(gbl_condition2betweenValuePanel);
		
		condition2betweenTextField1 = new JTextField();
		condition2betweenTextField1.setText("0");
		condition2betweenTextField1.setPreferredSize(new Dimension(200, 25));
		condition2betweenTextField1.setHorizontalAlignment(SwingConstants.RIGHT);
		condition2betweenTextField1.setColumns(4);
		condition2betweenTextField1.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition2betweenTextField1.setBackground(new Color(204, 216, 255));
		condition2betweenTextField1.setDocument(new DoubleDocument());
		condition2betweenTextField1.getDocument().addDocumentListener(documentListener);
		condition2betweenTextField1.setEnabled(false);
		GridBagConstraints gbc_condition2betweenTextField1 = new GridBagConstraints();
		gbc_condition2betweenTextField1.insets = new Insets(0, 0, 0, 5);
		gbc_condition2betweenTextField1.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition2betweenTextField1.gridx = 0;
		gbc_condition2betweenTextField1.gridy = 0;
		condition2betweenValuePanel.add(condition2betweenTextField1, gbc_condition2betweenTextField1);
		
		JLabel lblNewcondition2AndLabelLabel = new JLabel("and");
		lblNewcondition2AndLabelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewcondition2AndLabelLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewcondition2AndLabelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewcondition2AndLabelLabel = new GridBagConstraints();
		gbc_lblNewcondition2AndLabelLabel.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewcondition2AndLabelLabel.anchor = GridBagConstraints.CENTER;
		gbc_lblNewcondition2AndLabelLabel.gridx = 1;
		gbc_lblNewcondition2AndLabelLabel.gridy = 0;
		condition2betweenValuePanel.add(lblNewcondition2AndLabelLabel, gbc_lblNewcondition2AndLabelLabel);
		
		condition2betweenTextField2 = new JTextField();
		condition2betweenTextField2.setPreferredSize(new Dimension(200, 25));
		condition2betweenTextField2.setHorizontalAlignment(SwingConstants.RIGHT);
		condition2betweenTextField2.setColumns(4);
		condition2betweenTextField2.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition2betweenTextField2.setBackground(new Color(204, 216, 255));
		condition2betweenTextField2.setDocument(new DoubleDocument());
		condition2betweenTextField2.getDocument().addDocumentListener(documentListener);
		condition2betweenTextField2.setEnabled(false);
		GridBagConstraints gbc_condition2betweenTextField2 = new GridBagConstraints();
		gbc_condition2betweenTextField2.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition2betweenTextField2.gridx = 2;
		gbc_condition2betweenTextField2.gridy = 0;
		condition2betweenValuePanel.add(condition2betweenTextField2, gbc_condition2betweenTextField2);
		
		JLabel condition2CellStyleLabel1 = new JLabel("Cell Style");
		condition2CellStyleLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_condition2CellStyleLabel1 = new GridBagConstraints();
		gbc_condition2CellStyleLabel1.anchor = GridBagConstraints.WEST;
		gbc_condition2CellStyleLabel1.insets = new Insets(0, 25, 5, 5);
		gbc_condition2CellStyleLabel1.gridx = 0;
		gbc_condition2CellStyleLabel1.gridy = 5;
		contentPanel.add(condition2CellStyleLabel1, gbc_condition2CellStyleLabel1);

		condition2ForegroundLabel = new JLabel("");
		condition2ForegroundLabel.setOpaque(true);
		condition2ForegroundLabel.setBackground(condition2Foreground);
		condition2ForegroundLabel.setPreferredSize(new Dimension(50, 25));
		condition2ForegroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_condition2ForegroundLabel = new GridBagConstraints();
		gbc_condition2ForegroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition2ForegroundLabel.gridx = 1;
		gbc_condition2ForegroundLabel.gridy = 5;
		contentPanel.add(condition2ForegroundLabel, gbc_condition2ForegroundLabel);

		condition2ForegroundButton = new JButton("Foreground");
		condition2ForegroundButton.setPreferredSize(new Dimension(110, 25));
		condition2ForegroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition2ForegroundButton.setEnabled(false);
		GridBagConstraints gbc_condition2ForegroundButton = new GridBagConstraints();
		gbc_condition2ForegroundButton.insets = new Insets(0, 0, 5, 25);
		gbc_condition2ForegroundButton.gridx = 2;
		gbc_condition2ForegroundButton.gridy = 5;
		contentPanel.add(condition2ForegroundButton, gbc_condition2ForegroundButton);
		
		condition2ForegroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Foreground", condition2ForegroundLabel.getBackground());
				if (color != null) {
					condition2Foreground = color;
					condition2ForegroundLabel.setBackground(color);
				}
				
			}
		});

		condition2BackgroundLabel = new JLabel("");
		condition2BackgroundLabel.setBackground(condition2Background);
		condition2BackgroundLabel.setOpaque(true);
		condition2BackgroundLabel.setMinimumSize(new Dimension(50, 25));
		condition2BackgroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		condition2BackgroundLabel.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_condition2BackgroundLabel = new GridBagConstraints();
		gbc_condition2BackgroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition2BackgroundLabel.gridx = 3;
		gbc_condition2BackgroundLabel.gridy = 5;
		contentPanel.add(condition2BackgroundLabel, gbc_condition2BackgroundLabel);

		condition2BackgroundButton = new JButton("Background");
		condition2BackgroundButton.setPreferredSize(new Dimension(110, 25));
		condition2BackgroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition2BackgroundButton.setEnabled(false);
		GridBagConstraints gbc_condition2BackgroundButton = new GridBagConstraints();
		gbc_condition2BackgroundButton.insets = new Insets(0, 0, 5, 5);
		gbc_condition2BackgroundButton.gridx = 4;
		gbc_condition2BackgroundButton.gridy = 5;
		contentPanel.add(condition2BackgroundButton, gbc_condition2BackgroundButton);
		
		condition2BackgroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Background", condition2BackgroundLabel.getBackground());
				if (color != null) {
					condition2Background = color;
					condition2BackgroundLabel.setBackground(color);
				}
				
			}
		});

		condition2FadeoutCheckBox = new JCheckBox("Fade out");
		condition2FadeoutCheckBox.setOpaque(false);
		condition2FadeoutCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition2FadeoutCheckBox.setEnabled(false);
		GridBagConstraints gbc_condition2FadeoutCheckBox = new GridBagConstraints();
		gbc_condition2FadeoutCheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition2FadeoutCheckBox.insets = new Insets(0, 25, 5, 25);
		gbc_condition2FadeoutCheckBox.gridx = 5;
		gbc_condition2FadeoutCheckBox.gridy = 5;
		contentPanel.add(condition2FadeoutCheckBox, gbc_condition2FadeoutCheckBox);
		
		condition2CheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition2CheckBox.isSelected())
				{
					condition2ComboBox.setBackground(new Color(255, 243, 204));
					condition2ComboBox.setEnabled(true);
					condition2FormulaTextField.setBackground(new Color(255, 243, 204));
					condition2FormulaTextField.setEnabled(true);
					condition2CellValueComboBox.setBackground(new Color(255, 243, 204));
					condition2CellValueComboBox.setEnabled(true);
					condition2TextField.setBackground(new Color(255, 243, 204));
					condition2TextField.setEnabled(true);
					condition2betweenTextField1.setBackground(new Color(255, 243, 204));
					condition2betweenTextField1.setEnabled(true);
					condition2betweenTextField2.setBackground(new Color(255, 243, 204));
					condition2betweenTextField2.setEnabled(true);
					condition2ForegroundButton.setEnabled(true);
					condition2BackgroundButton.setEnabled(true);
					condition2FadeoutCheckBox.setEnabled(true);
				}
				else
				{
					condition2ComboBox.setBackground(new Color(204, 216, 255));
					condition2ComboBox.setEnabled(false);
					condition2FormulaTextField.setBackground(new Color(204, 216, 255));
					condition2FormulaTextField.setEnabled(false);
					condition2CellValueComboBox.setBackground(new Color(204, 216, 255));
					condition2CellValueComboBox.setEnabled(false);
					condition2TextField.setBackground(new Color(204, 216, 255));
					condition2TextField.setEnabled(false);
					condition2betweenTextField1.setBackground(new Color(204, 216, 255));
					condition2betweenTextField1.setEnabled(false);
					condition2betweenTextField2.setBackground(new Color(204, 216, 255));
					condition2betweenTextField2.setEnabled(false);
					condition2ForegroundButton.setEnabled(false);
					condition2BackgroundButton.setEnabled(false);
					condition2FadeoutCheckBox.setEnabled(false);

				}
				checkOkButton();
			}
			
		});
		
		condition2ComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition2ComboBox.getSelectedIndex()==1)
				{
					condition2Cardlayout.show(condition2Panel,"condition2CellValuePanel");
				}
				else
				{
					condition2Cardlayout.show(condition2Panel,"condition2FormulaTextField");
				}
				checkOkButton();
			}
		});
		
		condition2CellValueComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition2CellValueComboBox.getSelectedIndex()>5)
				{
					condition2CardLayout2.show(condition2ValuePanel,"condition2betweenValuePanel");
				}
				else
				{
					condition2CardLayout2.show(condition2ValuePanel,"condition2TextField");
				}
				checkOkButton();
			}
		});
		
		//
		
		condition3CheckBox = new JCheckBox("Condition 3");
		condition3CheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition3CheckBox.setOpaque(false);
		GridBagConstraints gbc_condition3CheckBox = new GridBagConstraints();
		gbc_condition3CheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition3CheckBox.insets = new Insets(25, 25, 5, 15);
		gbc_condition3CheckBox.gridx = 0;
		gbc_condition3CheckBox.gridy = 6;
		contentPanel.add(condition3CheckBox, gbc_condition3CheckBox);

		condition3ComboBox= new JComboBox(new String[]{"Formula is","Cell value is"});
		condition3ComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));		
		condition3ComboBox.setBackground(new Color(204, 216, 255));
		condition3ComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition3ComboBox.setSelectedIndex(0);
		condition3ComboBox.setEnabled(false);
		
		GridBagConstraints gbc_condition3ComboBox = new GridBagConstraints();
		gbc_condition3ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition3ComboBox.insets = new Insets(0, 25, 5, 5);
		gbc_condition3ComboBox.gridx = 0;
		gbc_condition3ComboBox.gridy = 7;
		contentPanel.add(condition3ComboBox, gbc_condition3ComboBox);

		condition3Panel = new JPanel();
		condition3Panel.setEnabled(false);
		GridBagConstraints gbc_condition3Panel = new GridBagConstraints();
		gbc_condition3Panel.gridwidth = 5;
		gbc_condition3Panel.insets = new Insets(0, 0, 5, 25);
		gbc_condition3Panel.fill = GridBagConstraints.BOTH;
		gbc_condition3Panel.gridx = 1;
		gbc_condition3Panel.gridy = 7;
		contentPanel.add(condition3Panel, gbc_condition3Panel);
		condition3Cardlayout = new CardLayout(0, 0);
		condition3Panel.setLayout(condition3Cardlayout);
		
		condition3FormulaTextField = new JTextField();
		condition3Panel.add(condition3FormulaTextField, "condition3FormulaTextField");
		condition3FormulaTextField.setHorizontalAlignment(SwingConstants.LEFT);
		condition3FormulaTextField.setPreferredSize(new Dimension(200, 25));
		condition3FormulaTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition3FormulaTextField.setBackground(new Color(204, 216, 255));
		condition3FormulaTextField.setEnabled(false);
		condition3FormulaTextField.setColumns(4);
		condition3FormulaTextField.getDocument().addDocumentListener(documentListener);
		
		condition3CellValuePanel = new JPanel();
		condition3CellValuePanel.setBackground(new Color(204, 216, 255));
		condition3Panel.add(condition3CellValuePanel, "condition3CellValuePanel");
		GridBagLayout gbl_condition3CellValuePanel = new GridBagLayout();
		gbl_condition3CellValuePanel.rowWeights = new double[]{0.0};
		gbl_condition3CellValuePanel.columnWeights = new double[]{0.0, 1.0};
		condition3CellValuePanel.setLayout(gbl_condition3CellValuePanel);
		
		condition3CellValueComboBox = new JComboBox(comboValues);
		condition3CellValueComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition3CellValueComboBox.setBackground(new Color(204, 216, 255));
		condition3CellValueComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		condition3CellValueComboBox.setSelectedIndex(0);
		condition3CellValueComboBox.setEnabled(false);

		GridBagConstraints gbc_condition3CellValueComboBox = new GridBagConstraints();
		gbc_condition3CellValueComboBox.insets = new Insets(0, 0, 0, 10);
		gbc_condition3CellValueComboBox.fill = GridBagConstraints.BOTH;
		gbc_condition3CellValueComboBox.gridx = 0;
		gbc_condition3CellValueComboBox.gridy = 0;
		condition3CellValuePanel.add(condition3CellValueComboBox, gbc_condition3CellValueComboBox);
		
		condition3ValuePanel = new JPanel();
		condition3ValuePanel.setBackground(new Color(204, 216, 255));
		GridBagConstraints gbc_condition3ValuePanel = new GridBagConstraints();
		gbc_condition3ValuePanel.fill = GridBagConstraints.BOTH;
		gbc_condition3ValuePanel.gridx = 1;
		gbc_condition3ValuePanel.gridy = 0;
		condition3CellValuePanel.add(condition3ValuePanel, gbc_condition3ValuePanel);
		condition3CardLayout2 = new CardLayout(0, 0);
		condition3ValuePanel.setLayout(condition3CardLayout2);
		
		condition3TextField = new JTextField();
		condition3TextField.setPreferredSize(new Dimension(200, 25));
		condition3TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		condition3TextField.setColumns(4);
		condition3TextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition3TextField.setBackground(new Color(204, 216, 255));
		condition3TextField.setEnabled(false);
		condition3TextField.setDocument(new DoubleDocument());
		condition3TextField.getDocument().addDocumentListener(documentListener);
		condition3ValuePanel.add(condition3TextField, "condition3TextField");
		
		JPanel condition3betweenValuePanel = new JPanel();
		condition3betweenValuePanel.setBackground(new Color(204, 216, 255));
		condition3ValuePanel.add(condition3betweenValuePanel, "condition3betweenValuePanel");
		GridBagLayout gbl_condition3betweenValuePanel = new GridBagLayout();
		gbl_condition3betweenValuePanel.columnWeights = new double[]{1.0, 0.0, 1.0};
		condition3betweenValuePanel.setLayout(gbl_condition3betweenValuePanel);
		
		condition3betweenTextField1 = new JTextField();
		condition3betweenTextField1.setText("0");
		condition3betweenTextField1.setPreferredSize(new Dimension(200, 25));
		condition3betweenTextField1.setHorizontalAlignment(SwingConstants.RIGHT);
		condition3betweenTextField1.setColumns(4);
		condition3betweenTextField1.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition3betweenTextField1.setBackground(new Color(204, 216, 255));
		condition3betweenTextField1.setDocument(new DoubleDocument());
		condition3betweenTextField1.getDocument().addDocumentListener(documentListener);
		condition3betweenTextField1.setEnabled(false);
		GridBagConstraints gbc_condition3betweenTextField1 = new GridBagConstraints();
		gbc_condition3betweenTextField1.insets = new Insets(0, 0, 0, 5);
		gbc_condition3betweenTextField1.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition3betweenTextField1.gridx = 0;
		gbc_condition3betweenTextField1.gridy = 0;
		condition3betweenValuePanel.add(condition3betweenTextField1, gbc_condition3betweenTextField1);
		
		JLabel lblNewcondition3AndLabelLabel = new JLabel("and");
		lblNewcondition3AndLabelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewcondition3AndLabelLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewcondition3AndLabelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewcondition3AndLabelLabel = new GridBagConstraints();
		gbc_lblNewcondition3AndLabelLabel.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewcondition3AndLabelLabel.anchor = GridBagConstraints.CENTER;
		gbc_lblNewcondition3AndLabelLabel.gridx = 1;
		gbc_lblNewcondition3AndLabelLabel.gridy = 0;
		condition3betweenValuePanel.add(lblNewcondition3AndLabelLabel, gbc_lblNewcondition3AndLabelLabel);
		
		condition3betweenTextField2 = new JTextField();
		condition3betweenTextField2.setPreferredSize(new Dimension(200, 25));
		condition3betweenTextField2.setHorizontalAlignment(SwingConstants.RIGHT);
		condition3betweenTextField2.setColumns(4);
		condition3betweenTextField2.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 5)));
		condition3betweenTextField2.setBackground(new Color(204, 216, 255));
		condition3betweenTextField2.setDocument(new DoubleDocument());
		condition3betweenTextField2.getDocument().addDocumentListener(documentListener);
		condition3betweenTextField2.setEnabled(false);
		GridBagConstraints gbc_condition3betweenTextField2 = new GridBagConstraints();
		gbc_condition3betweenTextField2.fill = GridBagConstraints.HORIZONTAL;
		gbc_condition3betweenTextField2.gridx = 2;
		gbc_condition3betweenTextField2.gridy = 0;
		condition3betweenValuePanel.add(condition3betweenTextField2, gbc_condition3betweenTextField2);
		
		JLabel condition3CellStyleLabel1 = new JLabel("Cell Style");
		condition3CellStyleLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_condition3CellStyleLabel1 = new GridBagConstraints();
		gbc_condition3CellStyleLabel1.anchor = GridBagConstraints.WEST;
		gbc_condition3CellStyleLabel1.insets = new Insets(0, 25, 5, 5);
		gbc_condition3CellStyleLabel1.gridx = 0;
		gbc_condition3CellStyleLabel1.gridy = 2;
		contentPanel.add(condition3CellStyleLabel1, gbc_condition3CellStyleLabel1);

		condition3ForegroundLabel = new JLabel("");
		condition3ForegroundLabel.setOpaque(true);
		condition3ForegroundLabel.setBackground(condition3Foreground);
		condition3ForegroundLabel.setPreferredSize(new Dimension(50, 25));
		condition3ForegroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_condition3ForegroundLabel = new GridBagConstraints();
		gbc_condition3ForegroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition3ForegroundLabel.gridx = 1;
		gbc_condition3ForegroundLabel.gridy = 8;
		contentPanel.add(condition3ForegroundLabel, gbc_condition3ForegroundLabel);

		condition3ForegroundButton = new JButton("Foreground");
		condition3ForegroundButton.setPreferredSize(new Dimension(110, 25));
		condition3ForegroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition3ForegroundButton.setEnabled(false);
		GridBagConstraints gbc_condition3ForegroundButton = new GridBagConstraints();
		gbc_condition3ForegroundButton.insets = new Insets(0, 0, 5, 25);
		gbc_condition3ForegroundButton.gridx = 2;
		gbc_condition3ForegroundButton.gridy = 8;
		contentPanel.add(condition3ForegroundButton, gbc_condition3ForegroundButton);
		
		condition3ForegroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Foreground", condition3ForegroundLabel.getBackground());
				if (color != null) {
					condition3Foreground = color;
					condition3ForegroundLabel.setBackground(color);
				}
				
			}
		});

		condition3BackgroundLabel = new JLabel("");
		condition3BackgroundLabel.setBackground(condition3Background);
		condition3BackgroundLabel.setOpaque(true);
		condition3BackgroundLabel.setMinimumSize(new Dimension(50, 25));
		condition3BackgroundLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		condition3BackgroundLabel.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_condition3BackgroundLabel = new GridBagConstraints();
		gbc_condition3BackgroundLabel.insets = new Insets(0, 0, 5, 5);
		gbc_condition3BackgroundLabel.gridx = 3;
		gbc_condition3BackgroundLabel.gridy = 8;
		contentPanel.add(condition3BackgroundLabel, gbc_condition3BackgroundLabel);

		condition3BackgroundButton = new JButton("Background");
		condition3BackgroundButton.setPreferredSize(new Dimension(110, 25));
		condition3BackgroundButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition3BackgroundButton.setEnabled(false);
		GridBagConstraints gbc_condition3BackgroundButton = new GridBagConstraints();
		gbc_condition3BackgroundButton.insets = new Insets(0, 0, 5, 5);
		gbc_condition3BackgroundButton.gridx = 4;
		gbc_condition3BackgroundButton.gridy = 8;
		contentPanel.add(condition3BackgroundButton, gbc_condition3BackgroundButton);
		
		condition3BackgroundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Color color = JColorChooser.showDialog(ConditionalFormattingDialog.this, "Choose Background", condition3BackgroundLabel.getBackground());
				if (color != null) {
					condition3Background = color;
					condition3BackgroundLabel.setBackground(color);
				}
				
			}
		});

		condition3FadeoutCheckBox = new JCheckBox("Fade out");
		condition3FadeoutCheckBox.setOpaque(false);
		condition3FadeoutCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		condition3FadeoutCheckBox.setEnabled(false);
		GridBagConstraints gbc_condition3FadeoutCheckBox = new GridBagConstraints();
		gbc_condition3FadeoutCheckBox.anchor = GridBagConstraints.WEST;
		gbc_condition3FadeoutCheckBox.insets = new Insets(0, 25, 5, 25);
		gbc_condition3FadeoutCheckBox.gridx = 5;
		gbc_condition3FadeoutCheckBox.gridy = 8;
		contentPanel.add(condition3FadeoutCheckBox, gbc_condition3FadeoutCheckBox);
		
		condition3CheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition3CheckBox.isSelected())
				{
					condition3ComboBox.setBackground(new Color(255, 243, 204));
					condition3ComboBox.setEnabled(true);
					condition3FormulaTextField.setBackground(new Color(255, 243, 204));
					condition3FormulaTextField.setEnabled(true);
					condition3CellValueComboBox.setBackground(new Color(255, 243, 204));
					condition3CellValueComboBox.setEnabled(true);
					condition3TextField.setBackground(new Color(255, 243, 204));
					condition3TextField.setEnabled(true);
					condition3betweenTextField1.setBackground(new Color(255, 243, 204));
					condition3betweenTextField1.setEnabled(true);
					condition3betweenTextField2.setBackground(new Color(255, 243, 204));
					condition3betweenTextField2.setEnabled(true);
					condition3ForegroundButton.setEnabled(true);
					condition3BackgroundButton.setEnabled(true);
					condition3FadeoutCheckBox.setEnabled(true);
				}
				else
				{
					condition3ComboBox.setBackground(new Color(204, 216, 255));
					condition3ComboBox.setEnabled(false);
					condition3FormulaTextField.setBackground(new Color(204, 216, 255));
					condition3FormulaTextField.setEnabled(false);
					condition3CellValueComboBox.setBackground(new Color(204, 216, 255));
					condition3CellValueComboBox.setEnabled(false);
					condition3TextField.setBackground(new Color(204, 216, 255));
					condition3TextField.setEnabled(false);
					condition3betweenTextField1.setBackground(new Color(204, 216, 255));
					condition3betweenTextField1.setEnabled(false);
					condition3betweenTextField2.setBackground(new Color(204, 216, 255));
					condition3betweenTextField2.setEnabled(false);
					condition3ForegroundButton.setEnabled(false);
					condition3BackgroundButton.setEnabled(false);
					condition3FadeoutCheckBox.setEnabled(false);

				}
				checkOkButton();
			}
			
		});
		
		condition3ComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition3ComboBox.getSelectedIndex()==1)
				{
					condition3Cardlayout.show(condition3Panel,"condition3CellValuePanel");
				}
				else
				{
					condition3Cardlayout.show(condition3Panel,"condition3FormulaTextField");
				}
				checkOkButton();
			}
		});
		
		condition3CellValueComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(condition3CellValueComboBox.getSelectedIndex()>5)
				{
					condition3CardLayout2.show(condition3ValuePanel,"condition3betweenValuePanel");
				}
				else
				{
					condition3CardLayout2.show(condition3ValuePanel,"condition3TextField");
				}
				checkOkButton();
			}
		});
		
		
		//
		
		setTitle("Conditional Formatting");

		okButton = new JButton();
		okButton.setText("Ok");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png")));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(30, 0, 15, 5);
		gbc_okButton.gridx = 4;
		gbc_okButton.gridy = 9;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(110, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;


				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_cancelButton.insets = new Insets(30, 0, 15, 25);
		gbc_cancelButton.gridx = 5;
		gbc_cancelButton.gridy = 9;
		contentPanel.add(cancelButton, gbc_cancelButton);
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(110, 25));
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
			}
		});
		
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition1()!=null)
			condition1CheckBox.doClick();

		if(spreadSheetConditionalFormat.getSpreadSheetCondition1()!=null)
		{
			if(spreadSheetConditionalFormat.getSpreadSheetCondition1().getConditionType()==ConditionType.CELL_VALUE_IS)
			{
				condition1ComboBox.setSelectedIndex(1);
				
				switch(spreadSheetConditionalFormat.getSpreadSheetCondition1().getIsType())
				{
					case GREATER_THAN:
						condition1CellValueComboBox.setSelectedIndex(1);
						break;
					case LESS_THAN:
						condition1CellValueComboBox.setSelectedIndex(2);
						break;
					case LESS_THAN_OR_EQUAL_TO:
						condition1CellValueComboBox.setSelectedIndex(3);
						break;
					case GREATER_THAN_OR_EQUAL_TO:
						condition1CellValueComboBox.setSelectedIndex(4);
						break;
					case NOT_EQUAL_TO:
						condition1CellValueComboBox.setSelectedIndex(5);
						break;
					case BETWEEN:
						condition1CellValueComboBox.setSelectedIndex(6);
						break;
					case NOT_BETWEEN:
						condition1CellValueComboBox.setSelectedIndex(7);
						break;
					default:
						condition1CellValueComboBox.setSelectedIndex(0);
						break;
				}
				
				if(spreadSheetConditionalFormat.getSpreadSheetCondition1().getValue1()!=null)
				{
					condition1betweenTextField1.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition1().getValue1()));
					condition1TextField.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition1().getValue1()));
				}
				if(spreadSheetConditionalFormat.getSpreadSheetCondition1().getValue2()!=null)
				{
					condition1betweenTextField2.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition1().getValue2()));
				}
			}
			else
			{
				condition1FormulaTextField.setText(spreadSheetConditionalFormat.getSpreadSheetCondition1().getFormula());
			}
			condition1BackgroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition1().getBackgroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition1().getBackgroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition1().getBackgroundBlue()));
			condition1ForegroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition1().getForegroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition1().getForegroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition1().getForegroundBlue()));
			condition1FadeoutCheckBox.setSelected(spreadSheetConditionalFormat.getSpreadSheetCondition1().getFadeOut());
		}
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition2()!=null)
			condition2CheckBox.doClick();
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition2()!=null)
		{
			if(spreadSheetConditionalFormat.getSpreadSheetCondition2().getConditionType()==ConditionType.CELL_VALUE_IS)
			{
				condition2ComboBox.setSelectedIndex(1);
				
				switch(spreadSheetConditionalFormat.getSpreadSheetCondition2().getIsType())
				{
					case GREATER_THAN:
						condition2CellValueComboBox.setSelectedIndex(1);
						break;
					case LESS_THAN:
						condition2CellValueComboBox.setSelectedIndex(2);
						break;
					case LESS_THAN_OR_EQUAL_TO:
						condition2CellValueComboBox.setSelectedIndex(3);
						break;
					case GREATER_THAN_OR_EQUAL_TO:
						condition2CellValueComboBox.setSelectedIndex(4);
						break;
					case NOT_EQUAL_TO:
						condition2CellValueComboBox.setSelectedIndex(5);
						break;
					case BETWEEN:
						condition2CellValueComboBox.setSelectedIndex(6);
						break;
					case NOT_BETWEEN:
						condition2CellValueComboBox.setSelectedIndex(7);
						break;
					default:
						condition2CellValueComboBox.setSelectedIndex(0);
						break;
				}
				
				if(spreadSheetConditionalFormat.getSpreadSheetCondition2().getValue1()!=null)
				{
					condition2betweenTextField1.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition2().getValue1()));
					condition2TextField.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition2().getValue1()));
				}
				if(spreadSheetConditionalFormat.getSpreadSheetCondition2().getValue2()!=null)
				{
					condition2betweenTextField2.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition2().getValue2()));
				}
			}
			else
			{
				condition2FormulaTextField.setText(spreadSheetConditionalFormat.getSpreadSheetCondition2().getFormula());
			}
			condition2BackgroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition2().getBackgroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition2().getBackgroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition2().getBackgroundBlue()));
			condition2ForegroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition2().getForegroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition2().getForegroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition2().getForegroundBlue()));
			condition2FadeoutCheckBox.setSelected(spreadSheetConditionalFormat.getSpreadSheetCondition2().getFadeOut());
		}
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition3()!=null)
			condition3CheckBox.doClick();
		
		if(spreadSheetConditionalFormat.getSpreadSheetCondition3()!=null)
		{
			if(spreadSheetConditionalFormat.getSpreadSheetCondition3().getConditionType()==ConditionType.CELL_VALUE_IS)
			{
				condition3ComboBox.setSelectedIndex(1);
				
				switch(spreadSheetConditionalFormat.getSpreadSheetCondition3().getIsType())
				{
					case GREATER_THAN:
						condition3CellValueComboBox.setSelectedIndex(1);
						break;
					case LESS_THAN:
						condition3CellValueComboBox.setSelectedIndex(2);
						break;
					case LESS_THAN_OR_EQUAL_TO:
						condition3CellValueComboBox.setSelectedIndex(3);
						break;
					case GREATER_THAN_OR_EQUAL_TO:
						condition3CellValueComboBox.setSelectedIndex(4);
						break;
					case NOT_EQUAL_TO:
						condition3CellValueComboBox.setSelectedIndex(5);
						break;
					case BETWEEN:
						condition3CellValueComboBox.setSelectedIndex(6);
						break;
					case NOT_BETWEEN:
						condition3CellValueComboBox.setSelectedIndex(7);
						break;
					default:
						condition3CellValueComboBox.setSelectedIndex(0);
						break;
				}
				
				if(spreadSheetConditionalFormat.getSpreadSheetCondition3().getValue1()!=null)
				{
					condition3betweenTextField1.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition3().getValue1()));
					condition3TextField.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition3().getValue1()));
				}
				if(spreadSheetConditionalFormat.getSpreadSheetCondition3().getValue2()!=null)
				{
					condition3betweenTextField2.setText(decimalFormat.format(spreadSheetConditionalFormat.getSpreadSheetCondition3().getValue2()));
				}
			}
			else
			{
				condition3FormulaTextField.setText(spreadSheetConditionalFormat.getSpreadSheetCondition3().getFormula());
			}
			condition3BackgroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition3().getBackgroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition3().getBackgroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition3().getBackgroundBlue()));
			condition3ForegroundLabel.setBackground(new Color(spreadSheetConditionalFormat.getSpreadSheetCondition3().getForegroundRed(),spreadSheetConditionalFormat.getSpreadSheetCondition3().getForegroundGreen(),spreadSheetConditionalFormat.getSpreadSheetCondition3().getForegroundBlue()));
			condition3FadeoutCheckBox.setSelected(spreadSheetConditionalFormat.getSpreadSheetCondition3().getFadeOut());
		}
		
	}
	
	private void checkOkButton()
	{
		boolean okEnabled = true;
		if(condition1CheckBox.isSelected())
		{
			if(condition1ComboBox.getSelectedIndex()==1)
			{
				if(condition1CellValueComboBox.getSelectedIndex()>5)
				{
					if(condition1betweenTextField1.getText().trim().length()==0)
						okEnabled = false;
					if(condition1betweenTextField2.getText().trim().length()==0)
						okEnabled = false;
				}
				else if (condition1TextField.getText().trim().length()==0)
					okEnabled = false;
			}
			else
			{
				String text = condition1FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				if(text.trim().length()==0)
					okEnabled = false;
			}
		}
		
		if(condition2CheckBox.isSelected())
		{
			if(condition2ComboBox.getSelectedIndex()==1)
			{
				if(condition2CellValueComboBox.getSelectedIndex()>5)
				{
					if(condition2betweenTextField1.getText().trim().length()==0)
						okEnabled = false;
					if(condition2betweenTextField2.getText().trim().length()==0)
						okEnabled = false;
				}
				else if (condition2TextField.getText().trim().length()==0)
					okEnabled = false;
			}
			else
			{
				String text = condition2FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				if(text.trim().length()==0)
					okEnabled = false;
			}
		}
		
		if(condition3CheckBox.isSelected())
		{
			if(condition3ComboBox.getSelectedIndex()==1)
			{
				if(condition3CellValueComboBox.getSelectedIndex()>5)
				{
					if(condition3betweenTextField1.getText().trim().length()==0)
						okEnabled = false;
					if(condition3betweenTextField2.getText().trim().length()==0)
						okEnabled = false;
				}
				else if (condition3TextField.getText().trim().length()==0)
					okEnabled = false;
			}
			else
			{
				String text = condition3FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				if(text.trim().length()==0)
					okEnabled = false;
			}
		}
		
		if(!(condition1CheckBox.isSelected()||condition2CheckBox.isSelected()||condition3CheckBox.isSelected()))
			okEnabled = false;
			
	
		okButton.setEnabled(okEnabled);
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
	 * Gets the spread sheet conditional format.
	 *
	 * @return the spread sheet conditional format
	 */
	public SpreadSheetConditionalFormat getSpreadSheetConditionalFormat() {

		SpreadSheetConditionalFormat spreadSheetConditionalFormat = new SpreadSheetConditionalFormat();
		if(condition1CheckBox.isSelected())
		{
			SpreadSheetCondition spreadSheetCondition = new SpreadSheetCondition();
			if(condition1ComboBox.getSelectedIndex()!=1)
			{
				spreadSheetCondition.setConditionType(ConditionType.FORMULA_IS);
				spreadSheetCondition.setIsType(IsType.EQUAL_TO);
				String text = condition1FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				spreadSheetCondition.setFormula(text.trim());
			}
			else
			{
				spreadSheetCondition.setConditionType(ConditionType.CELL_VALUE_IS);
				switch(condition1CellValueComboBox.getSelectedIndex())
				{
					case 1:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN);
						break;
					case 2:
						spreadSheetCondition.setIsType(IsType.LESS_THAN);
						break;
					case 3:
						spreadSheetCondition.setIsType(IsType.LESS_THAN_OR_EQUAL_TO);
						break;
					case 4:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN_OR_EQUAL_TO);
						break;
					case 5:
						spreadSheetCondition.setIsType(IsType.NOT_EQUAL_TO);
						break;
					case 6:
						spreadSheetCondition.setIsType(IsType.BETWEEN);
						break;
					case 7:
						spreadSheetCondition.setIsType(IsType.NOT_BETWEEN);
						break;
					default:
						spreadSheetCondition.setIsType(IsType.EQUAL_TO);
						break;
				}
				
				if(condition1CellValueComboBox.getSelectedIndex()>5)
				{
					spreadSheetCondition.setValue1(Double.parseDouble(condition1betweenTextField1.getText()));
					spreadSheetCondition.setValue2(Double.parseDouble(condition1betweenTextField2.getText()));
				}
				else
					spreadSheetCondition.setValue1(Double.parseDouble(condition1TextField.getText()));
				
			}
			spreadSheetCondition.setBackgroundRed(condition1BackgroundLabel.getBackground().getRed());
			spreadSheetCondition.setBackgroundGreen(condition1BackgroundLabel.getBackground().getGreen());
			spreadSheetCondition.setBackgroundBlue(condition1BackgroundLabel.getBackground().getBlue());
			spreadSheetCondition.setForegroundRed(condition1ForegroundLabel.getBackground().getRed());
			spreadSheetCondition.setForegroundGreen(condition1ForegroundLabel.getBackground().getGreen());
			spreadSheetCondition.setForegroundBlue(condition1ForegroundLabel.getBackground().getBlue());
			spreadSheetCondition.setFadeOut(condition1FadeoutCheckBox.isSelected());
			spreadSheetConditionalFormat.setSpreadSheetCondition1(spreadSheetCondition);
		}
		
		if(condition2CheckBox.isSelected())
		{
			SpreadSheetCondition spreadSheetCondition = new SpreadSheetCondition();
			if(condition2ComboBox.getSelectedIndex()!=1)
			{
				spreadSheetCondition.setConditionType(ConditionType.FORMULA_IS);
				spreadSheetCondition.setIsType(IsType.EQUAL_TO);
				String text = condition2FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				spreadSheetCondition.setFormula(text.trim());
			}
			else
			{
				spreadSheetCondition.setConditionType(ConditionType.CELL_VALUE_IS);
				switch(condition2CellValueComboBox.getSelectedIndex())
				{
					case 1:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN);
						break;
					case 2:
						spreadSheetCondition.setIsType(IsType.LESS_THAN);
						break;
					case 3:
						spreadSheetCondition.setIsType(IsType.LESS_THAN_OR_EQUAL_TO);
						break;
					case 4:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN_OR_EQUAL_TO);
						break;
					case 5:
						spreadSheetCondition.setIsType(IsType.NOT_EQUAL_TO);
						break;
					case 6:
						spreadSheetCondition.setIsType(IsType.BETWEEN);
						break;
					case 7:
						spreadSheetCondition.setIsType(IsType.NOT_BETWEEN);
						break;
					default:
						spreadSheetCondition.setIsType(IsType.EQUAL_TO);
						break;
				}
				
				if(condition2CellValueComboBox.getSelectedIndex()>5)
				{
					spreadSheetCondition.setValue1(Double.parseDouble(condition2betweenTextField1.getText()));
					spreadSheetCondition.setValue2(Double.parseDouble(condition2betweenTextField2.getText()));
				}
				else
					spreadSheetCondition.setValue1(Double.parseDouble(condition2TextField.getText()));
				
			}
			spreadSheetCondition.setBackgroundRed(condition2BackgroundLabel.getBackground().getRed());
			spreadSheetCondition.setBackgroundGreen(condition2BackgroundLabel.getBackground().getGreen());
			spreadSheetCondition.setBackgroundBlue(condition2BackgroundLabel.getBackground().getBlue());
			spreadSheetCondition.setForegroundRed(condition2ForegroundLabel.getBackground().getRed());
			spreadSheetCondition.setForegroundGreen(condition2ForegroundLabel.getBackground().getGreen());
			spreadSheetCondition.setForegroundBlue(condition2ForegroundLabel.getBackground().getBlue());
			spreadSheetCondition.setFadeOut(condition2FadeoutCheckBox.isSelected());
			spreadSheetConditionalFormat.setSpreadSheetCondition2(spreadSheetCondition);
		}
		
		if(condition3CheckBox.isSelected())
		{
			SpreadSheetCondition spreadSheetCondition = new SpreadSheetCondition();
			if(condition3ComboBox.getSelectedIndex()!=1)
			{
				spreadSheetCondition.setConditionType(ConditionType.FORMULA_IS);
				spreadSheetCondition.setIsType(IsType.EQUAL_TO);
				String text = condition3FormulaTextField.getText().trim();
				if(text.startsWith("="))
					text = text.substring(1);
				spreadSheetCondition.setFormula(text.trim());
			}
			else
			{
				spreadSheetCondition.setConditionType(ConditionType.CELL_VALUE_IS);
				switch(condition3CellValueComboBox.getSelectedIndex())
				{
					case 1:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN);
						break;
					case 2:
						spreadSheetCondition.setIsType(IsType.LESS_THAN);
						break;
					case 3:
						spreadSheetCondition.setIsType(IsType.LESS_THAN_OR_EQUAL_TO);
						break;
					case 4:
						spreadSheetCondition.setIsType(IsType.GREATER_THAN_OR_EQUAL_TO);
						break;
					case 5:
						spreadSheetCondition.setIsType(IsType.NOT_EQUAL_TO);
						break;
					case 6:
						spreadSheetCondition.setIsType(IsType.BETWEEN);
						break;
					case 7:
						spreadSheetCondition.setIsType(IsType.NOT_BETWEEN);
						break;
					default:
						spreadSheetCondition.setIsType(IsType.EQUAL_TO);
						break;
				}
				
				if(condition3CellValueComboBox.getSelectedIndex()>5)
				{
					spreadSheetCondition.setValue1(Double.parseDouble(condition3betweenTextField1.getText()));
					spreadSheetCondition.setValue2(Double.parseDouble(condition3betweenTextField2.getText()));
				}
				else
					spreadSheetCondition.setValue1(Double.parseDouble(condition3TextField.getText()));
				
			}
			spreadSheetCondition.setBackgroundRed(condition3BackgroundLabel.getBackground().getRed());
			spreadSheetCondition.setBackgroundGreen(condition3BackgroundLabel.getBackground().getGreen());
			spreadSheetCondition.setBackgroundBlue(condition3BackgroundLabel.getBackground().getBlue());
			spreadSheetCondition.setForegroundRed(condition3ForegroundLabel.getBackground().getRed());
			spreadSheetCondition.setForegroundGreen(condition3ForegroundLabel.getBackground().getGreen());
			spreadSheetCondition.setForegroundBlue(condition3ForegroundLabel.getBackground().getBlue());
			spreadSheetCondition.setFadeOut(condition3FadeoutCheckBox.isSelected());
			spreadSheetConditionalFormat.setSpreadSheetCondition3(spreadSheetCondition);
		}
		
		return spreadSheetConditionalFormat;
	}
}
