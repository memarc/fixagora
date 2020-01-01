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
package net.sourceforge.fixagora.basis.client.model.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditor;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;

import org.apache.poi.ss.util.CellReference;

/**
 * The Class SpreadSheetTableModel.
 */
public class SpreadSheetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private Map<String, SpreadSheetCell> spreadSheetCells = new HashMap<String, SpreadSheetCell>();

	private SpreadSheetEditor spreadSheetEditor = null;

	private Map<String, SpreadSheetCellFormat> spreadSheetCellFormatMap = new HashMap<String, SpreadSheetCellFormat>();

	private Map<String, SpreadSheetConditionalFormat> spreadSheetConditionalFormatMap = new HashMap<String, SpreadSheetConditionalFormat>();

	private int columnCount = 50;

	private int rowCount = 50;

	private boolean showingFormula = false;

	private Set<String> dropShadow = new HashSet<String>();

	private Map<Integer, Integer> fractionalOffSetMap = new HashMap<Integer, Integer>();

	private FontMetrics fontMetrics = null;

	private FontMetrics boldFontMetrics = null;

	/**
	 * Instantiates a new spread sheet table model.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetTableModel(SpreadSheetEditor spreadSheetEditor) {

		super();
		this.spreadSheetEditor = spreadSheetEditor;

		Font font = new Font("Dialog", Font.PLAIN, 12);
		fontMetrics = spreadSheetEditor.getComponent().getFontMetrics(font);

		font = new Font("Dialog", Font.BOLD, 12);
		boldFontMetrics = spreadSheetEditor.getComponent().getFontMetrics(font);

	}

	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public SpreadSheet getSpreadSheet() {

		return spreadSheetEditor.getSpreadSheet();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return columnCount;
	}

	/**
	 * Increase column count.
	 */
	public void increaseColumnCount() {

		if (columnCount < 255) {
			columnCount = columnCount + 1;
			fireTableStructureChanged();
		}
	}

	/**
	 * Increase row count.
	 */
	public void increaseRowCount() {

		rowCount = rowCount + 1;
		fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		return CellReference.convertNumToColString(column);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return rowCount;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		CellReference cellReference = new CellReference(rowIndex, columnIndex);
		return spreadSheetCells.get(cellReference.formatAsString());
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		SpreadSheetCellFormat spreadSheetCellFormat = getSpreadSheetCellFormatAt(rowIndex, columnIndex);

		if (spreadSheetCellFormat != null && spreadSheetCellFormat.getLocked())
			return false;

		if (spreadSheetEditor.getBasisClientConnector().getFUser().canExecute(spreadSheetEditor.getSpreadSheet())
				|| spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet()))
			return true;

		return false;

	}

	/**
	 * On update sheet cell response.
	 *
	 * @param updateSheetCellResponse the update sheet cell response
	 */
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {

		for (SpreadSheetCell spreadSheetCell : updateSheetCellResponse.getSpreadSheetCells()) {
			if (spreadSheetCell.getSheet() == spreadSheetEditor.getSpreadSheet().getId()) {
				CellReference cellReference = new CellReference(spreadSheetCell.getRow(), spreadSheetCell.getColumn());

				spreadSheetCells.put(cellReference.formatAsString(), spreadSheetCell);
				if (rowCount < spreadSheetCell.getRow())
					rowCount = spreadSheetCell.getRow();
			}
		}

	}

	/**
	 * On update sheet cell format response.
	 *
	 * @param updateSheetCellFormatResponse the update sheet cell format response
	 */
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {

		for (SpreadSheetCellFormat spreadSheetCellFormat : updateSheetCellFormatResponse.getSpreadSheetCellFormats()) {
			if (spreadSheetCellFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId()) {
				CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
				if (spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.CLEAR)
					spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
				else {
					spreadSheetCellFormatMap.remove(cellReference.formatAsString());
					fractionalOffSetMap.remove(spreadSheetCellFormat.getCellColumn());
				}
			}
		}

		for (SpreadSheetCellFormat spreadSheetCellFormat : spreadSheetCellFormatMap.values())
			if (spreadSheetCellFormat.getFractionalDisplay() != null && spreadSheetCellFormat.getFractionalDisplay() == true) {

				if (spreadSheetCellFormat.getDecimalPlaces() != null) {
					String decimalPlaces = Integer.toString(spreadSheetCellFormat.getDecimalPlaces());

					StringBuffer text = new StringBuffer("00 ");
					for (int i = 0; i < decimalPlaces.length(); i++)
						text.append(DollarFraction.getNumeratorDigit('0'));

					text.append(" / ");

					for (int i = 0; i < decimalPlaces.length(); i++)
						text.append(DollarFraction.getDenomiatorDigit('0'));

					Integer colOffset = fractionalOffSetMap.get(spreadSheetCellFormat.getCellColumn());

					if (colOffset == null)
						colOffset = 0;

					Integer offset = null;

					if (spreadSheetCellFormat.getBoldFont() != null && spreadSheetCellFormat.getBoldFont() == true) {
						offset = boldFontMetrics.stringWidth(text.toString());
					}
					else {
						offset = fontMetrics.stringWidth(text.toString());
					}

					if (offset > colOffset) {
						fractionalOffSetMap.put(spreadSheetCellFormat.getCellColumn(), offset);
					}

				}

			}

	}

	/**
	 * Gets the fractional offset.
	 *
	 * @param column the column
	 * @return the fractional offset
	 */
	public Integer getFractionalOffset(int column) {

		return fractionalOffSetMap.get(column);
	}

	/**
	 * Gets the spread sheet cell format at.
	 *
	 * @param row the row
	 * @param column the column
	 * @return the spread sheet cell format at
	 */
	public SpreadSheetCellFormat getSpreadSheetCellFormatAt(int row, int column) {

		CellReference cellReference = new CellReference(row, column);
		return spreadSheetCellFormatMap.get(cellReference.formatAsString());

	}

	/**
	 * Gets the spread sheet conditional format at.
	 *
	 * @param row the row
	 * @param column the column
	 * @return the spread sheet conditional format at
	 */
	public SpreadSheetConditionalFormat getSpreadSheetConditionalFormatAt(int row, int column) {

		CellReference cellReference = new CellReference(row, column);
		return spreadSheetConditionalFormatMap.get(cellReference.formatAsString());

	}

	/**
	 * On update sheet conditional format response.
	 *
	 * @param updateSheetConditionalFormatResponse the update sheet conditional format response
	 */
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : updateSheetConditionalFormatResponse.getSpreadSheetConditionalFormats()) {
			if (spreadSheetConditionalFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId()) {
				CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
				if (spreadSheetConditionalFormat.getSpreadSheetCondition1() != null || spreadSheetConditionalFormat.getSpreadSheetCondition2() != null
						|| spreadSheetConditionalFormat.getSpreadSheetCondition3() != null)
					spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
				else
					spreadSheetConditionalFormatMap.remove(cellReference.formatAsString());
			}
		}
	}

	/**
	 * Checks if is showing formula.
	 *
	 * @return true, if is showing formula
	 */
	public boolean isShowingFormula() {

		return showingFormula;
	}

	/**
	 * Switch showing formula.
	 *
	 * @param showingFormula the showing formula
	 */
	public void switchShowingFormula(boolean showingFormula) {

		this.showingFormula = showingFormula;
	}

	/**
	 * Adds the drop shadow.
	 *
	 * @param cellRefs the cell refs
	 */
	public synchronized void addDropShadow(Set<String> cellRefs) {

		dropShadow = cellRefs;

	}

	/**
	 * Checks if is drop shadow.
	 *
	 * @param row the row
	 * @param column the column
	 * @return true, if is drop shadow
	 */
	public synchronized boolean isDropShadow(int row, int column) {

		return dropShadow.contains(CellReference.convertNumToColString(column) + row);
	}

}
