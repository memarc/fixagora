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
package net.sourceforge.fixagora.excel.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.excel.client.model.ExcelImportCell;
import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel.EntryStatus;

/**
 * The Class ExcelImportTableRenderer.
 */
public class ExcelImportTableRenderer implements TableCellRenderer {

	private Color blue = new Color(204, 216, 255);

	private Color red = new Color(255, 198, 179);
	
	private Color green = new Color(216, 255, 204);

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		if (column == 0) {
			
			GradientPanel gradientPanel = new GradientPanel(Color.LIGHT_GRAY.darker(), Color.LIGHT_GRAY);
			
			if (value instanceof ExcelImportCell) {
				
				ExcelImportCell excelImportCell = (ExcelImportCell) value;
				
				JLabel statusLabel = new JLabel();
				statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
				statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
				statusLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
				
				GridBagConstraints gbc_statusLabel = new GridBagConstraints();
				gbc_statusLabel.gridx=0;
				gbc_statusLabel.gridy=0;
				gbc_statusLabel.fill=GridBagConstraints.BOTH;
				
				JLabel numberLabel = new JLabel();
				numberLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
				numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				numberLabel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, blue.darker()), new EmptyBorder(0, 5, 0, 5)));
				
				GridBagConstraints gbc_numberLabel = new GridBagConstraints();
				gbc_numberLabel.gridx=1;
				gbc_numberLabel.gridy=0;
				gbc_numberLabel.fill=GridBagConstraints.BOTH;
				
				if (excelImportCell.getEntryStatus() == EntryStatus.DISABLED) {
					
					statusLabel.setText("Disabled");
					statusLabel.setForeground(Color.DARK_GRAY);
					numberLabel.setForeground(Color.DARK_GRAY);
				}
				else if (excelImportCell.getEntryStatus() == EntryStatus.INVALID) {
					
					gradientPanel = new GradientPanel(red.darker(), red);
					statusLabel.setText(" Invalid");
					statusLabel.setForeground(Color.BLACK);
					numberLabel.setForeground(Color.BLACK);
				}
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOAD_FAILED) {
					
					gradientPanel = new GradientPanel(red.darker(), red);
					statusLabel.setText(" Failed");
					statusLabel.setForeground(Color.BLACK);
					numberLabel.setForeground(Color.BLACK);
				}
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOADED) {
					
					gradientPanel = new GradientPanel(green.darker(), green);
					statusLabel.setText(" Uploaded");
					statusLabel.setForeground(Color.BLACK);
					numberLabel.setForeground(Color.BLACK);
				}
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOADING) {
					
					gradientPanel = new GradientPanel(blue.darker(), blue);
					statusLabel.setText(" Uploading");
					statusLabel.setForeground(Color.BLACK);
					numberLabel.setForeground(Color.BLACK);
				}
				else {
					
					gradientPanel = new GradientPanel(blue.darker(), blue);
					statusLabel.setText(" Ok");
					statusLabel.setForeground(Color.BLACK);
					numberLabel.setForeground(Color.BLACK);
				}
				
				if (row > 0)
					numberLabel.setText(Integer.toString(row));
				
				GridBagLayout gbl_contentPanel = new GridBagLayout();
				gbl_contentPanel.rowWeights = new double[] { 1.0 };
				gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0};
				
				gradientPanel.setLayout(gbl_contentPanel);
				gradientPanel.add(numberLabel, gbc_numberLabel);
				gradientPanel.add(statusLabel, gbc_statusLabel);
			}

			return gradientPanel;
		}
		else {

			JLabel label = new JLabel();
			label.setFont(new Font("Dialog", Font.PLAIN, 12));
			label.setOpaque(true);
			label.setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 0, 1, 1), blue.darker()),new EmptyBorder(0, 5, 0, 0)));
			
			if (value instanceof ExcelImportCell) {
				
				ExcelImportCell excelImportCell = (ExcelImportCell) value;
				
				if (row == 0) {
					String comboEntry = "Not used";
					
					if (value instanceof ExcelImportCell)
						comboEntry = ((ExcelImportCell) value).getCellValue();
					
					JComboBox jComboBox = new JComboBox(new String[] { comboEntry });
					jComboBox.setFont(new Font("Dialog", Font.BOLD, 12));
					
					if (excelImportCell.getEntryStatus() == EntryStatus.DISABLED)
						jComboBox.setBackground(Color.LIGHT_GRAY);
					
					else if (excelImportCell.getEntryStatus() == EntryStatus.INVALID)
						jComboBox.setBackground(red);
					
					else
						jComboBox.setBackground(new Color(255, 243, 204));
					
					return jComboBox;
				}
				
				if (excelImportCell.getEntryStatus() == EntryStatus.DISABLED)
					label.setBackground(Color.LIGHT_GRAY);
				
				else if (excelImportCell.getEntryStatus() == EntryStatus.INVALID)
					label.setBackground(red);
				
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOAD_FAILED)
					label.setBackground(red);
				
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOADING)
					label.setBackground((new Color(255, 243, 204)).darker());
				else if (excelImportCell.getEntryStatus() == EntryStatus.UPLOADED)
					label.setBackground(green);
				
				else
					label.setBackground(new Color(255, 243, 204));
				
				label.setText(excelImportCell.getCellValue());
			}
			return label;
		}
	}

}
