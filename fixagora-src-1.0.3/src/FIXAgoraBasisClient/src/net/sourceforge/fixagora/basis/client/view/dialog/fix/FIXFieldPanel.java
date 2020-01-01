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
package net.sourceforge.fixagora.basis.client.view.dialog.fix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement;
import net.sourceforge.fixagora.basis.client.model.message.FIXField;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessageFilter;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXField.ValueType;

import org.apache.poi.hssf.util.CellReference;

/**
 * The Class FIXFieldPanel.
 */
public class FIXFieldPanel extends AbstractFIXPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox columnComboBox;
	private JCheckBox jCheckBox;

	/**
	 * Instantiates a new fIX field panel.
	 *
	 * @param parent the parent
	 * @param abstractFIXElement the abstract fix element
	 * @param fieldMap the field map
	 * @param enabled the enabled
	 * @param depth the depth
	 * @param fixMessageFilter the fix message filter
	 * @param input the input
	 */
	public FIXFieldPanel(final AbstractFIXPanel parent, final FIXField abstractFIXElement, final SpreadSheetFIXFieldMap fieldMap, final boolean enabled, final int depth,
			final FIXMessageFilter fixMessageFilter, boolean input) {

		super(parent, abstractFIXElement, fieldMap, enabled, depth, fixMessageFilter, input);

		dataTypeLabel.setText(abstractFIXElement.getType().toString());

		final GridBagConstraints gbc_jDateChooser = new GridBagConstraints();
		gbc_jDateChooser.anchor = GridBagConstraints.WEST;
		gbc_jDateChooser.insets = new Insets(0, 0, 0, 5);
		gbc_jDateChooser.gridx = 0;
		gbc_jDateChooser.gridy = 0;
		
		boolean key = false;
		int index = -1;
		if(fieldMap.isSetField(abstractFIXElement.getNumber()))
		{
			key = fieldMap.getKeyValue(abstractFIXElement.getNumber());
			index = fieldMap.getSpreadColumn(abstractFIXElement.getNumber());
		}
		
		JPanel jPanel = new JPanel();
		jPanel.setOpaque(false);
		jPanel.setLayout(new GridBagLayout());
		jCheckBox = new JCheckBox("Trigger");
		if (input)
			jCheckBox.setText("Key");
		jCheckBox.setOpaque(false);
		jCheckBox.setEnabled(index>=0);
		jCheckBox.setSelected(key);

		final GridBagConstraints gbc_jCheckBox = new GridBagConstraints();
		gbc_jCheckBox.anchor = GridBagConstraints.WEST;
		gbc_jCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_jCheckBox.gridx = 1;
		gbc_jCheckBox.gridy = 0;

		jPanel.add(jCheckBox, gbc_jCheckBox);

		List<String> column = new ArrayList<String>();
		column.add("Not used");

		for (int i = 0; i < 255; i++)
			column.add(CellReference.convertNumToColString(i));

		final GridBagConstraints gbc_columnComboBox = new GridBagConstraints();
		gbc_columnComboBox.anchor = GridBagConstraints.WEST;
		gbc_columnComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_columnComboBox.gridx = 0;
		gbc_columnComboBox.gridy = 0;

		if (enabled) {

			columnComboBox = new JComboBox(column.toArray());
			columnComboBox.setMinimumSize(new Dimension(150, 25));
			columnComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
			columnComboBox.setPreferredSize(new Dimension(100, 25));
			columnComboBox.setBackground(new Color(255, 243, 204));
			columnComboBox.setEnabled(enabled);
			columnComboBox.setSelectedIndex(index+1);
			columnComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (columnComboBox.getSelectedIndex() > 0) {
						
						jCheckBox.setEnabled(true);
						fieldMap.setSpreadColumn(abstractFIXElement.getNumber(), getValueType(abstractFIXElement),abstractFIXElement.getName(),columnComboBox.getSelectedIndex()-1);	
						fieldValueChanged();
					}
					else
					{
						jCheckBox.setEnabled(false);
						fieldMap.removeField(abstractFIXElement.getNumber());
						fieldValueChanged();
					}

				}
			});
			jPanel.add(columnComboBox, gbc_columnComboBox);
		}
		else {
			JTextField jTextField = new JTextField(column.get(index+1));
			jTextField.setMinimumSize(new Dimension(150, 25));
			jTextField.setPreferredSize(new Dimension(100, 25));
			jTextField.setBackground(Color.LIGHT_GRAY);
			jTextField.setForeground(Color.GRAY);
			jTextField.setBorder(new EmptyBorder(0,5,0,0));
			jPanel.add(jTextField, gbc_columnComboBox);
			
		}

		jCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
			
				fieldMap.setKeyValue(abstractFIXElement.getNumber(),getValueType(abstractFIXElement), abstractFIXElement.getName(),jCheckBox.isSelected());
				fieldValueChanged();
			}
		});
		
		lastPanel.add(jPanel, gbc_columnComboBox); 
		
		if(input)
			inputCheck();
		else
			fieldValueCheck();
		
	}
	
	private ValueType getValueType(AbstractFIXElement abstractFIXElement)
	{
		ValueType valueType = ValueType.TEXT;
		if(abstractFIXElement instanceof FIXField)
		{
			switch (((FIXField)abstractFIXElement).getType()) {
				case BOOLEAN:
					return ValueType.BOOLEAN;
					
				case AMT:
				case FLOAT:
				case PERCENTAGE:
				case PRICE:
				case PRICEOFFSET:
				case QTY:
					return ValueType.DOUBLE;
					
				case LENGTH:
				case INT:
				case NUMINGROUP:
				case SEQNUM:
					return ValueType.INTEGER;
				
				case CHAR:
				case COUNTRY:
				case DATA:
				case DAYOFMONTH:
				case EXCHANGE:
				case XMLDATA:
				case LANGUAGE:
				case STRING:
				case CURRENCY:
				case UNKNOWN:
				case MONTH_YEAR:
				case MONTHYEAR:
				case MULTIPLECHARVALUE:
				case MULTIPLESTRINGVALUE:
				case MULTIPLEVALUESTRING:
					return ValueType.TEXT;
					
				case TIME:
				case TZTIMEONLY:
				case UTCTIMEONLY:
					return ValueType.TIME;
					
				case TZTIMESTAMP:				
				case UTCDATE:
				case UTCTIMESTAMP:
					return ValueType.DATETIME;
					
				case UTCDATEONLY:
				case DATE:
				case LOCALMKTDATE:
					return ValueType.DATE;

			}
		}
		return valueType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fixpusher.view.message.AbstractFIXPanel#getPanelBackground
	 * ()
	 */
	@Override
	protected Color getPanelBackground() {

		return new Color(255, 243, 204);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#toString()
	 */
	@Override
	public String toString() {

		return "Field";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fixpusher.view.message.AbstractFIXPanel#dispose()
	 */
	public void dispose() {

		valueComponent = null;

		super.dispose();

	}

}
