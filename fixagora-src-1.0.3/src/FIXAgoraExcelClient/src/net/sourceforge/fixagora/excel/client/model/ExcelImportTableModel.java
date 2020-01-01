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
package net.sourceforge.fixagora.excel.client.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

/**
 * The Class ExcelImportTableModel.
 */
public class ExcelImportTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private Sheet sheet = null;

	/**
	 * The Enum EntryStatus.
	 */
	public enum EntryStatus {
		
		/** The valid. */
		VALID, 
 /** The invalid. */
 INVALID, 
 /** The disabled. */
 DISABLED, 
 /** The uploading. */
 UPLOADING, 
 /** The uploaded. */
 UPLOADED, 
 /** The upload failed. */
 UPLOAD_FAILED
	}

	private Map<Integer, String> columnTypeMap = new HashMap<Integer, String>();

	private ExcelParser excelParser = null;

	private Set<Integer> disabledRows = new HashSet<Integer>();

	private JTable table = null;

	private boolean updateExisting = false;

	private Map<String, FSecurity> symbols = new HashMap<String, FSecurity>();

	private Map<String, FSecurity> securityIDs = new HashMap<String, FSecurity>();

	private Map<Integer, EntryStatus> uploadMap = new HashMap<Integer, ExcelImportTableModel.EntryStatus>();

	/**
	 * Instantiates a new excel import table model.
	 *
	 * @param sheet the sheet
	 * @param updateExisting the update existing
	 * @param table the table
	 * @param securities the securities
	 */
	public ExcelImportTableModel(Sheet sheet, boolean updateExisting, JTable table, List<FSecurity> securities) {

		this.sheet = sheet;
		this.excelParser = new ExcelParser(this);
		this.table = table;
		this.updateExisting = updateExisting;
		for (FSecurity security : securities) {
			symbols.put(security.getName(), security);
			securityIDs.put(security.getSecurityID(), security);
		}
		checkFistRow();

	}

	private void checkFistRow() {
		if(sheet==null)
			return;
		Set<String> comboBoxEntries = new HashSet<String>();
		comboBoxEntries.addAll(Arrays.asList(excelParser.getComboBoxEntries()));
		Row row = sheet.getRow(0);
		if (row!=null)
			for(int i=0;i<=row.getLastCellNum();i++)
			{
				Cell cell = row.getCell(i);
				if (cell != null&&cell.getCellType()==Cell.CELL_TYPE_STRING) {
					if(cell.getStringCellValue()!=null&&comboBoxEntries.contains(cell.getStringCellValue().trim()))
					{
						disabledRows.add(1);
						setColumnType(cell.getStringCellValue().trim(), i+1);
					}
				}
			}		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		if (sheet == null)
			return 255;
		int colCount = 0;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null && row.getLastCellNum() > colCount)
				colCount = row.getLastCellNum();
		}

		if (colCount + 1 > 255)
			return colCount + 1;
		return 255;

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		if (column == 0)
			return "";

		return CellReference.convertNumToColString(column - 1);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		if (sheet == null)
			return 255;
		if (sheet.getLastRowNum() > 255)
			return sheet.getLastRowNum();
		return 255;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		if (rowIndex > 0) {
			if (sheet == null)
				return new ExcelImportCell("", EntryStatus.DISABLED);
			if (sheet.getLastRowNum() >= rowIndex - 1) {

				Row row = sheet.getRow(rowIndex - 1);
				if (row != null && row.getLastCellNum() >= columnIndex - 1) {
					if (columnIndex > 0) {
						Cell cell = row.getCell(columnIndex - 1);
						if (cell != null) {
							switch (cell.getCellType()) {
								case Cell.CELL_TYPE_STRING:
									return excelParser.getExcelImportCell(cell.getStringCellValue(), rowIndex, columnIndex);
								case Cell.CELL_TYPE_NUMERIC:
									if (DateUtil.isCellDateFormatted(cell)) {
										return excelParser.getExcelImportCell(DateUtil.getJavaDate(cell.getNumericCellValue()), rowIndex, columnIndex);
									}
									return excelParser.getExcelImportCell(cell.getNumericCellValue(), rowIndex, columnIndex);
								case Cell.CELL_TYPE_BOOLEAN:
									return excelParser.getExcelImportCell(cell.getBooleanCellValue(), rowIndex, columnIndex);
							}
						}
					}
					else {
						if (disabledRows.contains(rowIndex))
							return new ExcelImportCell(Integer.toString(rowIndex), EntryStatus.DISABLED);
						EntryStatus entryStatus = uploadMap.get(rowIndex);
						if (entryStatus == null) {
							entryStatus = EntryStatus.VALID;
							for (int i = 1; i <= row.getLastCellNum(); i++) {
								Object cell = getValueAt(rowIndex, i);
								if (cell instanceof ExcelImportCell) {
									if (((ExcelImportCell) cell).getEntryStatus() == EntryStatus.INVALID)
										entryStatus = EntryStatus.INVALID;
								}
							}
						}
						return new ExcelImportCell(Integer.toString(rowIndex), entryStatus);
					}
				}
			}
			if (columnIndex == 0)
				return new ExcelImportCell(Integer.toString(rowIndex), EntryStatus.DISABLED);
			return new ExcelImportCell("", EntryStatus.DISABLED);
		}
		else {
			String cellValue = columnTypeMap.get(columnIndex);
			if (cellValue == null)
				cellValue = "Not used";
			EntryStatus entryStatus = EntryStatus.DISABLED;
			if (sheet != null) {
				int lastColNum = 0;
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					if (row != null && row.getLastCellNum() > lastColNum)
						lastColNum = row.getLastCellNum();
				}
				if (columnIndex <= lastColNum) {
					if (columnIndex > 0)
						entryStatus = excelParser.checkColumn(columnIndex, table);
					else {
						entryStatus = EntryStatus.VALID;
						for (int i = 1; i <= lastColNum; i++)
							if (excelParser.checkColumn(i, table) == EntryStatus.INVALID)
								entryStatus = EntryStatus.INVALID;
						if (!columnTypeMap.values().contains("Symbol"))
							entryStatus = EntryStatus.INVALID;
						if (!columnTypeMap.values().contains("Security Identifier"))
							entryStatus = EntryStatus.INVALID;
					}
				}
			}
			ExcelImportCell excelImportCell = new ExcelImportCell(cellValue, entryStatus);
			return excelImportCell;
		}

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		if (sheet == null)
			return false;

		if (uploadMap.size() > 0)
			return false;

		if (rowIndex == 0 && columnIndex > 0) {
			int lastColNum = 0;
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null && row.getLastCellNum() > lastColNum)
					lastColNum = row.getLastCellNum();
			}
			if (columnIndex <= lastColNum)
				return true;
			else
				return false;
		}

		if (rowIndex > 0 && columnIndex == 0) {
			if (sheet.getLastRowNum() >= rowIndex - 1) {
				Row row = sheet.getRow(rowIndex - 1);
				if (row != null && row.getLastCellNum() > columnIndex - 1) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * Sets the column type.
	 *
	 * @param selectedItem the selected item
	 * @param editingColumn the editing column
	 */
	public void setColumnType(String selectedItem, int editingColumn) {

		if (selectedItem == null)
			columnTypeMap.remove(editingColumn);
		else
			columnTypeMap.put(editingColumn, selectedItem);

	}

	/**
	 * Gets the column type map.
	 *
	 * @return the column type map
	 */
	public Map<Integer, String> getColumnTypeMap() {

		return columnTypeMap;
	}

	/**
	 * Gets the status text.
	 *
	 * @return the status text
	 */
	public String getStatusText() {

		if (sheet == null)
			return "No sheet selected.";
		return excelParser.getStatusText(table);
	}

	/**
	 * Toggle row.
	 *
	 * @param editingRow the editing row
	 */
	public void toggleRow(int editingRow) {

		if (!disabledRows.remove(editingRow))
			disabledRows.add(editingRow);
		fireTableDataChanged();

	}

	/**
	 * Checks if is row disabled.
	 *
	 * @param rowIndex the row index
	 * @return true, if is row disabled
	 */
	public boolean isRowDisabled(int rowIndex) {

		return disabledRows.contains(rowIndex);
	}

	/**
	 * Checks if is update existing.
	 *
	 * @return true, if is update existing
	 */
	public boolean isUpdateExisting() {

		return updateExisting;
	}

	/**
	 * Sets the update existing.
	 *
	 * @param updateExisting the new update existing
	 */
	public void setUpdateExisting(boolean updateExisting) {

		this.updateExisting = updateExisting;
	}

	/**
	 * Gets the symbol map.
	 *
	 * @return the symbol map
	 */
	public Map<String, FSecurity> getSymbolMap() {

		return symbols;
	}

	/**
	 * Gets the security id map.
	 *
	 * @return the security id map
	 */
	public Map<String, FSecurity> getSecurityIDMap() {

		return securityIDs;
	}

	/**
	 * Gets the securities.
	 *
	 * @return the securities
	 */
	public List<UploadEntry> getSecurities() {

		return excelParser.getSecurities(table, sheet);
	}

	/**
	 * Sets the upload status.
	 *
	 * @param row the row
	 * @param entryStatus the entry status
	 */
	public void setUploadStatus(int row, EntryStatus entryStatus) {

		uploadMap.put(row, entryStatus);
	}

	/**
	 * Gets the upload status.
	 *
	 * @param row the row
	 * @return the upload status
	 */
	public EntryStatus getUploadStatus(int row) {

		return uploadMap.get(row);
	}

	/**
	 * Checks if is uploading.
	 *
	 * @return true, if is uploading
	 */
	public boolean isUploading() {

		return uploadMap.size() > 0;
	}

}
