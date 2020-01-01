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
package net.sourceforge.fixagora.buyside.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;

import javax.swing.JCheckBox;
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

import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage.SortBy;

/**
 * The Class BuySideQuotePageEditorMasterData.
 */
public class BuySideQuotePageEditorMasterData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField = null;

	private BuySideQuotePageEditor buySideQuotePageEditor = null;

	private JLabel nameWarningLabel = null;

	private JTextArea descriptionArea = null;

	private JLabel descriptionWarningLabel = null;

	private boolean dirty = false;

	private JTextField modifiedField = null;

	private boolean consistent = false;

	private JComboBox sortSecuritiesComboBox= null;

	private JComboBox styleComboBox= null;
	
	private JCheckBox showMarketDepthCheckBox= null;

	/**
	 * Instantiates a new buy side quote page editor master data.
	 *
	 * @param buySideQuotePageEditor the buy side quote page editor
	 */
	public BuySideQuotePageEditorMasterData(BuySideQuotePageEditor buySideQuotePageEditor) {

		super();

		this.buySideQuotePageEditor = buySideQuotePageEditor;

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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0,0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		setViewportView(panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.gridheight = 8;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		JLabel nameLabel = new JLabel("Page Name");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.WEST;
		gbc_nameLabel.insets = new Insets(50, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);

		nameWarningLabel = buySideQuotePageEditor.getNameWarningLabel();
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
		GridBagConstraints gbc_modifiedField = new GridBagConstraints();
		gbc_modifiedField.insets = new Insets(0, 0, 5, 5);
		gbc_modifiedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiedField.gridx = 3;
		gbc_modifiedField.gridy = 1;
		panel.add(modifiedField, gbc_modifiedField);
		
		JLabel styleLabel = new JLabel("Style");
		styleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_styleLabel = new GridBagConstraints();
		gbc_styleLabel.anchor = GridBagConstraints.WEST;
		gbc_styleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_styleLabel.gridx = 1;
		gbc_styleLabel.gridy = 2;
		panel.add(styleLabel, gbc_styleLabel);

		styleComboBox = new JComboBox(new String[]{"Black","Blue","Agora"});
		styleComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		styleComboBox.setMinimumSize(new Dimension(100, 25));
		styleComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		styleComboBox.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_styleComboBox = new GridBagConstraints();
		gbc_styleComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_styleComboBox.insets = new Insets(0, 0, 5, 50);
		gbc_styleComboBox.gridx = 3;
		gbc_styleComboBox.gridy = 2;
		panel.add(styleComboBox, gbc_styleComboBox);
		
		styleComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				checkConsistency();
				
			}
		});
		
		JLabel showMarketDepthLabel = new JLabel("Show Market Depth");
		showMarketDepthLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_showMarketDepthLabel = new GridBagConstraints();
		gbc_showMarketDepthLabel.anchor = GridBagConstraints.WEST;
		gbc_showMarketDepthLabel.insets = new Insets(0, 0, 5, 5);
		gbc_showMarketDepthLabel.gridx = 1;
		gbc_showMarketDepthLabel.gridy = 3;
		panel.add(showMarketDepthLabel, gbc_showMarketDepthLabel);

		showMarketDepthCheckBox = new JCheckBox();
		showMarketDepthCheckBox.setSelected(true);
		showMarketDepthCheckBox.setPreferredSize(new Dimension(25, 25));
		showMarketDepthCheckBox.setBackground(new Color(204, 216, 255));
		showMarketDepthCheckBox.addActionListener(actionListener);
		GridBagConstraints gbc_showMarketDepthCheckBox = new GridBagConstraints();
		gbc_showMarketDepthCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_showMarketDepthCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_showMarketDepthCheckBox.gridx = 3;
		gbc_showMarketDepthCheckBox.gridy = 3;
		panel.add(showMarketDepthCheckBox, gbc_showMarketDepthCheckBox);
		
		JLabel sortSecuritiesLabel = new JLabel("Sort Securities");
		sortSecuritiesLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sortSecuritiesLabel = new GridBagConstraints();
		gbc_sortSecuritiesLabel.anchor = GridBagConstraints.WEST;
		gbc_sortSecuritiesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sortSecuritiesLabel.gridx = 1;
		gbc_sortSecuritiesLabel.gridy = 4;
		panel.add(sortSecuritiesLabel, gbc_sortSecuritiesLabel);

		sortSecuritiesComboBox = new JComboBox(new String[]{"by symbol","by maturity"});
		sortSecuritiesComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		sortSecuritiesComboBox.setMinimumSize(new Dimension(100, 25));
		sortSecuritiesComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sortSecuritiesComboBox.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTHWEST;
		gbc_localeOfissueTextField.insets = new Insets(0, 0, 5, 50);
		gbc_localeOfissueTextField.gridx = 3;
		gbc_localeOfissueTextField.gridy = 4;
		panel.add(sortSecuritiesComboBox, gbc_localeOfissueTextField);
		
		sortSecuritiesComboBox.addActionListener(new ActionListener() {
			
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
		gbc_descriptionLabel.gridy = 5;
		panel.add(descriptionLabel, gbc_descriptionLabel);

		descriptionWarningLabel = new JLabel("");
		descriptionWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_descriptionWarningLabel = new GridBagConstraints();
		gbc_descriptionWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_descriptionWarningLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_descriptionWarningLabel.gridx = 2;
		gbc_descriptionWarningLabel.gridy = 5;
		panel.add(descriptionWarningLabel, gbc_descriptionWarningLabel);

		descriptionArea = new JTextArea();
		descriptionArea.setBorder( new EmptyBorder(5, 5, 5, 5));
		descriptionArea.getDocument().addDocumentListener(documentListener);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 3;
		gbc_textArea.gridy = 5;
		JScrollPane jScrollPane = new JScrollPane(descriptionArea);
		jScrollPane.setPreferredSize(new Dimension(400, 300));
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(jScrollPane, gbc_textArea);

		updateContent();
	}

	private void checkConsistency() {

		consistent = true;

		dirty = false;

		boolean valid = true;

		if (nameTextField.getText().trim().length() == 0) {

			nameWarningLabel.setToolTipText("Role name is empty");
			nameWarningLabel.setIcon(buySideQuotePageEditor.getBugIcon());

			valid = false;

		}
		else {

			nameWarningLabel.setToolTipText(null);
			nameWarningLabel.setIcon(null);

			if (buySideQuotePageEditor.getAbstractBusinessObject().getName() == null
					|| !buySideQuotePageEditor.getAbstractBusinessObject().getName().equals(nameTextField.getText())) {

				buySideQuotePageEditor.checkName(nameTextField.getText());
				dirty = true;
			}
		}

		consistent = consistent && valid;

		if (descriptionArea.getText().trim().length() == 0) {

			descriptionWarningLabel.setToolTipText("Role description is empty");
			descriptionWarningLabel.setIcon(buySideQuotePageEditor.getWarningIcon());
		}
		else {

			descriptionWarningLabel.setToolTipText(null);
			descriptionWarningLabel.setIcon(null);
		}

		if (buySideQuotePageEditor.getBuySideQuotePage().getDescription() != null
				&& !buySideQuotePageEditor.getBuySideQuotePage().getDescription().equals(descriptionArea.getText())) {

			dirty = true;
		}
		
		if (buySideQuotePageEditor.getBuySideQuotePage().getDescription() == null && descriptionArea.getText().trim().length()>0) {
			
			dirty = true;
		}
		
		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() != null && buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() != showMarketDepthCheckBox.isSelected())
			dirty = true;
		
		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() == null)
			dirty = true;
		
		if(buySideQuotePageEditor.getBuySideQuotePage().getSortBy()==SortBy.MATURITY&&sortSecuritiesComboBox.getSelectedIndex()!=1)
			dirty=true;
		
		if(buySideQuotePageEditor.getBuySideQuotePage().getSortBy()==SortBy.SYMBOL&&sortSecuritiesComboBox.getSelectedIndex()!=0)
			dirty=true;
		
		if(buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle()!=styleComboBox.getSelectedIndex())
			dirty=true;

		consistent = consistent && valid;

		buySideQuotePageEditor.checkDirty();
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

		buySideQuotePageEditor.getAbstractBusinessObject().setName(nameTextField.getText().trim());
		buySideQuotePageEditor.getBuySideQuotePage().setDescription(descriptionArea.getText());
		buySideQuotePageEditor.getBuySideQuotePage().setDisplayStyle(styleComboBox.getSelectedIndex());		
		buySideQuotePageEditor.getBuySideQuotePage().setShowMarketDepth(showMarketDepthCheckBox.isSelected());
		
		if(sortSecuritiesComboBox.getSelectedIndex()==1)
			buySideQuotePageEditor.getBuySideQuotePage().setSortBy(SortBy.MATURITY);
		else
			buySideQuotePageEditor.getBuySideQuotePage().setSortBy(SortBy.SYMBOL);
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		nameTextField.setText(buySideQuotePageEditor.getAbstractBusinessObject().getName());
		descriptionArea.setText(buySideQuotePageEditor.getBuySideQuotePage().getDescription());
		
		if(buySideQuotePageEditor.getBuySideQuotePage().getSortBy()==SortBy.MATURITY)
			sortSecuritiesComboBox.setSelectedIndex(1);
		else
			sortSecuritiesComboBox.setSelectedIndex(0);
		
		if(buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth()!=null)
		{
			showMarketDepthCheckBox.setSelected(buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth());
		}
			
		styleComboBox.setSelectedIndex(buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle());
		
		if (buySideQuotePageEditor.getAbstractBusinessObject().getId() != 0) {

			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(DateFormat.getTimeInstance().format(buySideQuotePageEditor.getAbstractBusinessObject().getModificationDate()));
			textBuffer.append(" ");
			textBuffer
					.append(DateFormat.getDateInstance(DateFormat.SHORT).format(buySideQuotePageEditor.getAbstractBusinessObject().getModificationDate()));

			if (buySideQuotePageEditor.getAbstractBusinessObject().getModificationUser() != null) {

				textBuffer.append(" by ");
				textBuffer.append(buySideQuotePageEditor.getAbstractBusinessObject().getModificationUser());
			}

			modifiedField.setText(textBuffer.toString());
		}
		else {

			modifiedField.setText("New Business Object");
		}

		if (buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject())) {

			nameTextField.setBackground(new Color(255, 243, 204));
			nameTextField.setEditable(true);

			descriptionArea.setBackground(new Color(255, 243, 204));
			descriptionArea.setEditable(true);
			
			sortSecuritiesComboBox.setBackground(new Color(255, 243, 204));
			sortSecuritiesComboBox.setEnabled(true);
			
			showMarketDepthCheckBox.setEnabled(true);
			
			styleComboBox.setBackground(new Color(255, 243, 204));
			styleComboBox.setEnabled(true);
			
		}
		else {

			nameTextField.setBackground(new Color(204, 216, 255));
			nameTextField.setEditable(false);

			descriptionArea.setBackground(new Color(204, 216, 255));
			descriptionArea.setEditable(false);
			
			sortSecuritiesComboBox.setBackground(new Color(204, 216, 255));
			sortSecuritiesComboBox.setEnabled(false);
			
			showMarketDepthCheckBox.setEnabled(false);
			
			styleComboBox.setBackground(new Color(204, 216, 255));
			styleComboBox.setEnabled(false);


		}

		checkConsistency();
	}
}
