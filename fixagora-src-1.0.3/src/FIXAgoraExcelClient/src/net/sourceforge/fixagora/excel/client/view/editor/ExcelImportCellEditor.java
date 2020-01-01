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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel;
import net.sourceforge.fixagora.excel.client.model.ExcelParser;

/**
 * The Class ExcelImportCellEditor.
 */
public class ExcelImportCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;

	private ExcelImportTableModel excelImportTableModel = null;

	private int editingColumn = -1;

	private JComboBox jComboBox = null;

	private int editingRow;

	private ExcelParser excelParser;

	/**
	 * Instantiates a new excel import cell editor.
	 *
	 * @param excelImportTableModel the excel import table model
	 */
	public ExcelImportCellEditor(ExcelImportTableModel excelImportTableModel) {

		super();
		this.excelImportTableModel = excelImportTableModel;
		this.excelParser = new ExcelParser(excelImportTableModel);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int rowIndex, final int vColIndex) {

		editingColumn = table.convertColumnIndexToModel(vColIndex);
		editingRow = rowIndex;

		if (rowIndex == 0) {
			
			jComboBox = new JComboBox(excelParser.getComboBoxEntries());
			if (value != null)
				jComboBox.setSelectedItem(value);
			jComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					stopCellEditing();
				}
			});
			return jComboBox;
		}

		GradientLabel gradientLabel = new GradientLabel(Color.LIGHT_GRAY.darker(), Color.LIGHT_GRAY);
		gradientLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				stopCellEditing();
			}

		});
		return gradientLabel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {

		if (editingRow == 0) {
			if (jComboBox.getSelectedIndex() == 0)
				excelImportTableModel.setColumnType(null, editingColumn);
			else
				excelImportTableModel.setColumnType((String) jComboBox.getSelectedItem(), editingColumn);

			excelImportTableModel.fireTableDataChanged();

			return jComboBox.getSelectedItem();
		}
		
		excelImportTableModel.toggleRow(editingRow);
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractCellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject evt) {

		if (evt instanceof MouseEvent) {
			return ((MouseEvent) evt).getClickCount() == 1;
		}
		return false;
	}

}