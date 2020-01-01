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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JTable;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.editor.ExchangeComboBoxEntries;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventDate;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventTime;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAttribute;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityComplexEvent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityDetails;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityEvent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityLeg;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityUnderlying;
import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel.EntryStatus;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

/**
 * The Class ExcelParser.
 */
public class ExcelParser {

	private Set<String> formattedEntries = new HashSet<String>();

	private Set<String> dateEntries = new HashSet<String>();

	private Set<String> timeEntries = new HashSet<String>();

	private Set<String> doubleEntries = new HashSet<String>();

	private Set<String> integerEntries = new HashSet<String>();

	private Set<String> stringEntries = new HashSet<String>();

	private Set<String> currencyEntries = new HashSet<String>();
	
	private String statusText = "";

	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.##########");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

	private ExcelImportTableModel excelImportTableModel = null;

	private Map<String, String> groupedColumns = new HashMap<String, String>();

	private Map<String, List<String>> columnGroups = new HashMap<String, List<String>>();
	
	/**
	 * Instantiates a new excel parser.
	 *
	 * @param excelImportTableModel the excel import table model
	 */
	public ExcelParser(ExcelImportTableModel excelImportTableModel) {

		super();

		this.excelImportTableModel = excelImportTableModel;

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		List<String> attributeList = new ArrayList<String>();
		attributeList.add("Attribute Type");
		attributeList.add("Attribute Value");
		columnGroups.put("Attribute Type", attributeList);
		groupedColumns.put("Attribute Type", "Attribute Type");
		groupedColumns.put("Attribute Value", "Attribute Type");

		List<String> eventList = new ArrayList<String>();
		eventList.add("Event Type");
		eventList.add("Event Date");
		eventList.add("Event Time");
		eventList.add("Event Price");
		eventList.add("Event Text");
		columnGroups.put("Event Type", eventList);
		groupedColumns.put("Event Type", "Event Type");
		groupedColumns.put("Event Date", "Event Type");
		groupedColumns.put("Event Time", "Event Type");
		groupedColumns.put("Event Price", "Event Type");
		groupedColumns.put("Event Text", "Event Type");

		List<String> complexEventList = new ArrayList<String>();
		complexEventList.add("Complex Event Type");
		complexEventList.add("Complex Event Option Payout Amount");
		complexEventList.add("Complex Event Price");
		complexEventList.add("Complex Event Price Boundary Method");
		complexEventList.add("Complex Event Price Boundary Precision");
		complexEventList.add("Complex Event Price Time Type");
		complexEventList.add("Complex Event Condition");
		columnGroups.put("Complex Event Type", complexEventList);
		groupedColumns.put("Complex Event Type", "Complex Event Type");
		groupedColumns.put("Complex Event Option Payout Amount", "Complex Event Type");
		groupedColumns.put("Complex Event Price", "Complex Event Type");
		groupedColumns.put("Complex Event Price Boundary Method", "Complex Event Type");
		groupedColumns.put("Complex Event Price Boundary Precision", "Complex Event Type");
		groupedColumns.put("Complex Event Price Time Type", "Complex Event Type");
		groupedColumns.put("Complex Event Condition", "Complex Event Type");

		List<String> complexEventDateList = new ArrayList<String>();
		complexEventDateList.add("Complex Event Start Date");
		complexEventDateList.add("Complex Event End Date");
		columnGroups.put("Complex Event Start Date", complexEventDateList);
		groupedColumns.put("Complex Event Start Date", "Complex Event Start Date");
		groupedColumns.put("Complex Event End Date", "Complex Event Start Date");

		List<String> complexEventTimeList = new ArrayList<String>();
		complexEventTimeList.add("Complex Event Start Time");
		complexEventTimeList.add("Complex Event End Time");
		columnGroups.put("Complex Event Start Time", complexEventTimeList);
		groupedColumns.put("Complex Event Start Time", "Complex Event Start Time");
		groupedColumns.put("Complex Event End Time", "Complex Event Start Time");

		List<String> underlyingList = new ArrayList<String>();
		underlyingList.add("Underlying Security");
		underlyingList.add("Underlying End Price");
		underlyingList.add("Underlying Start Value");
		underlyingList.add("Underlying End Value");
		underlyingList.add("Underlying Allocation Percent");
		underlyingList.add("Underlying Settlement Type");
		underlyingList.add("Underlying Cash Amount");
		underlyingList.add("Underlying Cash Type");
		columnGroups.put("Underlying Security", underlyingList);
		groupedColumns.put("Underlying Security", "Underlying Security");
		groupedColumns.put("Underlying End Price", "Underlying Security");
		groupedColumns.put("Underlying Start Value", "Underlying Security");
		groupedColumns.put("Underlying End Value", "Underlying Security");
		groupedColumns.put("Underlying Allocation Percent", "Underlying Security");
		groupedColumns.put("Underlying Settlement Type", "Underlying Security");
		groupedColumns.put("Underlying Cash Amount", "Underlying Security");
		groupedColumns.put("Underlying Cash Type", "Underlying Security");

		List<String> alternativeIdentifierList = new ArrayList<String>();
		alternativeIdentifierList.add("Security Alternative Identifier");
		alternativeIdentifierList.add("Security Alternative Identifier Source");
		columnGroups.put("Security Alternative Identifier", alternativeIdentifierList);
		groupedColumns.put("Security Alternative Identifier", "Security Alternative Identifier");
		groupedColumns.put("Security Alternative Identifier Source", "Security Alternative Identifier");

		List<String> legList = new ArrayList<String>();
		legList.add("Leg Security");
		legList.add("Leg Ratio Quantity");
		legList.add("Leg Option Ratio");
		legList.add("Leg Side");
		columnGroups.put("Leg Security", legList);
		groupedColumns.put("Leg Security", "Leg Security");
		groupedColumns.put("Leg Ratio Quantity", "Leg Security");
		groupedColumns.put("Leg Option Ratio", "Leg Security");
		groupedColumns.put("Leg Side", "Leg Security");

		dateEntries.add("Maturity Date");
		dateEntries.add("Coupon Payment Date");
		dateEntries.add("Issue Date");
		dateEntries.add("Redemption Date");
		dateEntries.add("Interest Accrual Date");
		dateEntries.add("Dated Date");
		dateEntries.add("Contract Settlement");
		dateEntries.add("Event Date");
		dateEntries.add("Complex Event Start Date");
		dateEntries.add("Complex Event End Date");

		timeEntries.add("Complex Event Start Time");
		timeEntries.add("Complex Event End Time");
		timeEntries.add("Event Time");
		timeEntries.add("Maturity Time");

		stringEntries.add("Symbol");
		stringEntries.add("Classification of Financial Instrument");
		stringEntries.add("Security Sub-type");
		stringEntries.add("Security Identifier");
		stringEntries.add("Security Alternative Identifier");
		stringEntries.add("Issuer");
		stringEntries.add("State of Issue");
		stringEntries.add("Locale of Issue");
		stringEntries.add("Credit Rating");
		stringEntries.add("Instrument Registry");
		stringEntries.add("Description");
		stringEntries.add("Security Group");
		stringEntries.add("Pool");
		stringEntries.add("Commercial Paper Registration Type");
		stringEntries.add("Product Complex");
		stringEntries.add("Attribute Value");
		stringEntries.add("Event Text");

		doubleEntries.add("Coupon Rate");
		doubleEntries.add("Cap Price");
		doubleEntries.add("Floor Price");
		doubleEntries.add("Contract Multiplier");
		doubleEntries.add("Minimum Price Increment");
		doubleEntries.add("Minimum Price Increment Amount");
		doubleEntries.add("Factor");
		doubleEntries.add("Strike Price");
		doubleEntries.add("Unit of Measure Quantity");
		doubleEntries.add("Price Unit of Measure Quantity");
		doubleEntries.add("Strike Value");
		doubleEntries.add("Strike Price Boundary Precision");
		doubleEntries.add("Strike Multiplier");
		doubleEntries.add("Percent at Risk");
		doubleEntries.add("Option Payout Amount");
		doubleEntries.add("Event Price");
		doubleEntries.add("Complex Event Option Payout Amount");
		doubleEntries.add("Complex Event Price");
		doubleEntries.add("Complex Event Price Boundary Precision");
		doubleEntries.add("Underlying End Price");
		doubleEntries.add("Underlying Start Value");
		doubleEntries.add("Underlying End Value");
		doubleEntries.add("Underlying Allocation Percent");
		doubleEntries.add("Underlying Cash Amount");
		doubleEntries.add("Leg Ratio Quantity");
		doubleEntries.add("Leg Option Ratio");

		integerEntries.add("Position Limit");
		integerEntries.add("Near-Term Position Limit");

		currencyEntries.add("Currency");
		currencyEntries.add("Strike Currency");

		formattedEntries.add("Additional Information");
		formattedEntries.add("Product");
		formattedEntries.add("Security Type");
		formattedEntries.add("Security Identifier Source");
		formattedEntries.add("Security Alternative Identifier Source");
		formattedEntries.add("Put or Call");
		formattedEntries.add("Country of Issue");
		formattedEntries.add("Contract Multiplier Unit");
		formattedEntries.add("Unit of Measure");
		formattedEntries.add("Price Unit of Measure");
		formattedEntries.add("Strike Price Determination Method");
		formattedEntries.add("Strike Price Boundary Method");
		formattedEntries.add("Security Exchange");
		formattedEntries.add("Commercial Paper Programm");
		formattedEntries.add("Security Status");
		formattedEntries.add("Settle on Open");
		formattedEntries.add("Instrument Assignment Method");
		formattedEntries.add("Underlying Price Determination Method");
		formattedEntries.add("Delivery Form");
		formattedEntries.add("Settlement Method");
		formattedEntries.add("Exercise Style");
		formattedEntries.add("Option Payout Type");
		formattedEntries.add("Price Quote Method");
		formattedEntries.add("List Method");
		formattedEntries.add("Flexible Indicator");
		formattedEntries.add("Flexible Product Eligibility Indicator");
		formattedEntries.add("Valuation Method");
		formattedEntries.add("Flow Schedule Type");
		formattedEntries.add("Restructuring Type");
		formattedEntries.add("Seniority");
		formattedEntries.add("Attribute Type");
		formattedEntries.add("Event Type");
		formattedEntries.add("Complex Event Type");
		formattedEntries.add("Complex Event Price Boundary Method");
		formattedEntries.add("Complex Event Price Time Type");
		formattedEntries.add("Complex Event Condition");
		formattedEntries.add("Underlying Security");
		formattedEntries.add("Underlying Settlement Type");
		formattedEntries.add("Underlying Cash Type");
		formattedEntries.add("Leg Security");
		formattedEntries.add("Leg Side");

	}

	/**
	 * Gets the combo box entries.
	 *
	 * @return the combo box entries
	 */
	public String[] getComboBoxEntries() {

		List<String> comboBoxEntries = new ArrayList<String>();

		comboBoxEntries.addAll(formattedEntries);

		comboBoxEntries.addAll(dateEntries);

		comboBoxEntries.addAll(timeEntries);

		comboBoxEntries.addAll(doubleEntries);

		comboBoxEntries.addAll(integerEntries);

		comboBoxEntries.addAll(stringEntries);

		comboBoxEntries.addAll(currencyEntries);

		Collections.sort(comboBoxEntries);

		comboBoxEntries.add(0, "Not used");
		return comboBoxEntries.toArray(new String[0]);
	}

	/**
	 * Gets the status text.
	 *
	 * @param table the table
	 * @return the status text
	 */
	public String getStatusText(JTable table) {

		statusText = null;
		Set<String> entrySet = new HashSet<String>();
		for (String comboBoxEntry : excelImportTableModel.getColumnTypeMap().values()) {
			if (entrySet.contains(comboBoxEntry) && groupedColumns.get(comboBoxEntry) == null) {
				statusText = "Invalid column configuration: Multiple selection for " + comboBoxEntry + ".";
			}
			else
				entrySet.add(comboBoxEntry);
		}
		if (!entrySet.contains("Symbol")) {
			statusText = "Invalid column configuration: No symbol specified.";
		}
		if (!entrySet.contains("Security Identifier")) {
			statusText = "Invalid column configuration: No security identifier specified.";
		}

		if (statusText == null) {
			for (int j = 1; j < table.getColumnCount(); j++) {
				String column = excelImportTableModel.getColumnTypeMap().get(table.convertColumnIndexToModel(j));
				int columnHits = 0;
				if (column != null) {
					String firstEntry = groupedColumns.get(column);
					if (firstEntry != null) {
						int viewColumn = table.convertColumnIndexToView(j);
						int firstEntryColumn = -1;
						for (int i = viewColumn; i >= 0; i--) {
							String assignedCol = excelImportTableModel.getColumnTypeMap().get(table.convertColumnIndexToModel(i));
							if (firstEntry.equals(assignedCol)) {
								firstEntryColumn = i;
								i = -1;
							}
						}
						if (firstEntryColumn == -1)
							return "Column " + CellReference.convertNumToColString(table.convertColumnIndexToModel(j) - 1) + " is defined as " + column
									+ " and there is no column defined as " + firstEntry + " on the left side. Rearrange the column order.";
						for (int i = firstEntryColumn; i < table.getColumnCount(); i++) {
							String assignedCol = excelImportTableModel.getColumnTypeMap().get(table.convertColumnIndexToModel(i));

							if (firstEntry.equals(assignedCol) && i != firstEntryColumn)
								i = table.getColumnCount();
							else if (column.equals(assignedCol))
								columnHits++;
						}
					}
					if (columnHits > 1)
						return "Column " + CellReference.convertNumToColString(table.convertColumnIndexToModel(j) - 1) + " is defined as " + column
								+ " and there are multiple entries for the group starting with " + firstEntry
								+ " on the left side. Rearrange the column order.";
				}
			}
		}

		if (statusText == null) {
			int invalidCount = 0;
			for (int i = 0; i < excelImportTableModel.getRowCount(); i++) {
				Object value = excelImportTableModel.getValueAt(i, 0);
				if (value instanceof ExcelImportCell) {
					if (((ExcelImportCell) value).getEntryStatus() == EntryStatus.INVALID)
						invalidCount++;
				}
			}
			if (invalidCount == 1)
				statusText = "Sheet contains an invalid row. Disable the row or reconfigure the columns.";
			if (invalidCount > 1)
				statusText = "Sheet contains " + invalidCount + " invalid rows. Disable the rows or reconfigure the columns.";
		}
		return statusText;
	}

	/**
	 * Gets the excel import cell.
	 *
	 * @param cellValue the cell value
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the excel import cell
	 */
	public ExcelImportCell getExcelImportCell(Object cellValue, int rowIndex, int columnIndex) {

		EntryStatus entryStatus = EntryStatus.DISABLED;

		String column = excelImportTableModel.getColumnTypeMap().get(columnIndex);

		if (column != null && !excelImportTableModel.isRowDisabled(rowIndex)) {
			entryStatus = EntryStatus.VALID;
			Object value = convertValue(cellValue, column);
			if (value == null && cellValue != null)
				entryStatus = EntryStatus.INVALID;

			if (column.equals("Symbol")) {
				if (cellValue == null || (excelImportTableModel.getSymbolMap().get(cellValue) != null && !excelImportTableModel.isUpdateExisting()))
					entryStatus = EntryStatus.INVALID;
			}
			if (column.equals("Security Identifier")) {
				if (cellValue == null || (excelImportTableModel.getSecurityIDMap().get(cellValue)!=null && !excelImportTableModel.isUpdateExisting()))
					entryStatus = EntryStatus.INVALID;
			}
			
			if (column.equals("Underlying Security")||column.equals("Leg Security")) {
				if (cellValue != null && (excelImportTableModel.getSecurityIDMap().get(cellValue)==null))
					entryStatus = EntryStatus.INVALID;
			}
			
		}
		
		EntryStatus uploadStatus = excelImportTableModel.getUploadStatus(rowIndex);

		if(entryStatus!=EntryStatus.DISABLED&&uploadStatus!=null)
			entryStatus = uploadStatus;
		
		return new ExcelImportCell(convertValueToString(cellValue, column), entryStatus);
	}

	/**
	 * Check column.
	 *
	 * @param columnIndex the column index
	 * @param table the table
	 * @return the entry status
	 */
	public EntryStatus checkColumn(int columnIndex, JTable table) {

		EntryStatus entryStatus = EntryStatus.VALID;
		Set<String> entrySet = new HashSet<String>();
		for (Entry<Integer, String> comboBoxEntry : excelImportTableModel.getColumnTypeMap().entrySet()) {
			if (entrySet.contains(comboBoxEntry.getValue()) && comboBoxEntry.getKey().intValue() == columnIndex
					&& groupedColumns.get(comboBoxEntry.getValue()) == null) {
				entryStatus = EntryStatus.INVALID;
			}
			else
				entrySet.add(comboBoxEntry.getValue());
		}
		String column = excelImportTableModel.getColumnTypeMap().get(columnIndex);
		int columnHits = 0;
		if (column != null) {
			String firstEntry = groupedColumns.get(column);
			if (firstEntry != null) {
				int viewColumn = table.convertColumnIndexToView(columnIndex);
				int firstEntryColumn = -1;
				for (int i = viewColumn; i >= 0; i--) {
					String assignedCol = excelImportTableModel.getColumnTypeMap().get(table.convertColumnIndexToModel(i));
					if (firstEntry.equals(assignedCol)) {
						firstEntryColumn = i;
						i = -1;
					}
				}
				if (firstEntryColumn == -1)
					return EntryStatus.INVALID;
				for (int i = firstEntryColumn; i < table.getColumnCount(); i++) {
					String assignedCol = excelImportTableModel.getColumnTypeMap().get(table.convertColumnIndexToModel(i));
					if (firstEntry.equals(assignedCol) && i != firstEntryColumn)
						i = table.getColumnCount();
					else if (column.equals(assignedCol))
						columnHits++;
				}
			}
		}
		if (columnHits > 1)
			return EntryStatus.INVALID;
		return entryStatus;
	}

	/**
	 * Convert value.
	 *
	 * @param value the value
	 * @param column the column
	 * @return the object
	 */
	public Object convertValue(Object value, String column) {

		if (column == null)
			return null;

		if (value == null)
			return null;

		if (stringEntries.contains(column)) {
			if (value instanceof String) {
				return ((String) value).trim();
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "TRUE";
				return "FALSE";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);

		}

		else if (currencyEntries.contains(column)) {
			if (value instanceof String && ((String) value).trim().length() == 3 && ((String) value).toUpperCase().equals(value)) {
				return value;
			}
			return null;
		}

		else if (doubleEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return decimalFormat.parse((String) value).doubleValue();
				}
				catch (Exception e) {
					return null;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return 1d;
				return 0d;
			}
			if (value instanceof Double)
				return value;
			if (value instanceof Date)
				return null;
		}

		else if (integerEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return decimalFormat.parse((String) value).intValue();
				}
				catch (Exception e) {
					return null;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return 1;
				return 0;
			}
			if (value instanceof Double)
				return ((Double) value).intValue();
			if (value instanceof Date)
				return null;

		}

		else if (dateEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return simpleDateFormat.parse((String) value);
				}
				catch (Exception e) {
					return null;
				}
			}
			if (value instanceof Boolean) {
				return null;
			}
			if (value instanceof Double)
				return null;
			if (value instanceof Date)
				return value;
		}

		else if (timeEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return simpleTimeFormat.parse((String) value);
				}
				catch (Exception e) {
					return null;
				}
			}
			if (value instanceof Boolean) {
				return null;
			}
			if (value instanceof Double)
				return null;
			if (value instanceof Date)
				return value;
		}

		else if (column.equals("Product")) {
			if (value instanceof String) {
				if (value.equals("Agency"))
					return new Integer(1);
				if (value.equals("Commodity"))
					return new Integer(2);
				if (value.equals("Corporate"))
					return new Integer(3);
				if (value.equals("Currency"))
					return new Integer(4);
				if (value.equals("Equity"))
					return new Integer(5);
				if (value.equals("Government"))
					return new Integer(6);
				if (value.equals("Index"))
					return new Integer(7);
				if (value.equals("Loan"))
					return new Integer(8);
				if (value.equals("Money Market"))
					return new Integer(9);
				if (value.equals("Mortage"))
					return new Integer(10);
				if (value.equals("Municipal"))
					return new Integer(11);
				if (value.equals("Other"))
					return new Integer(12);
				if (value.equals("Financing"))
					return new Integer(13);
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
				if (((Double) value).doubleValue() == 6)
					return new Integer(6);
				if (((Double) value).doubleValue() == 7)
					return new Integer(7);
				if (((Double) value).doubleValue() == 8)
					return new Integer(8);
				if (((Double) value).doubleValue() == 9)
					return new Integer(9);
				if (((Double) value).doubleValue() == 10)
					return new Integer(10);
				if (((Double) value).doubleValue() == 11)
					return new Integer(11);
				if (((Double) value).doubleValue() == 12)
					return new Integer(12);
				if (((Double) value).doubleValue() == 13)
					return new Integer(13);
			}
		}

		else if (column.equals("Additional Information")) {
			if (value instanceof String) {
				if (value.equals("CD"))
					return value;
				if (value.equals("EUCP with lump-sum interest rather than discount price"))
					return "CD";
				if (value.equals("WI"))
					return value;
				if (value.equals("When Issued"))
					return "WI";
			}
		}

		else if (column.equals("Security Type")) {
			if (value instanceof String) {
				if (value.equals("EUSUPRA"))
					return value;
				if (value.equals("Euro Supranational Coupons"))
					return "EUSUPRA";
				if (value.equals("FAC"))
					return value;
				if (value.equals("Federal Agency Coupon"))
					return "FAC";
				if (value.equals("FADN"))
					return value;
				if (value.equals("Federal Agency Discount Note"))
					return "FADN";
				if (value.equals("PEF"))
					return value;
				if (value.equals("Private Export Funding"))
					return "PEF";
				if (value.equals("SUPRA"))
					return value;
				if (value.equals("USD Supranational Coupons"))
					return "SUPRA";
				if (value.equals("CORP"))
					return value;
				if (value.equals("Corporate Bond"))
					return "CORP";
				if (value.equals("CPP"))
					return value;
				if (value.equals("Corporate Private Placement"))
					return "CPP";
				if (value.equals("CB"))
					return value;
				if (value.equals("Convertible Bond"))
					return "CB";
				if (value.equals("DUAL"))
					return value;
				if (value.equals("Dual Currency"))
					return "DUAL";
				if (value.equals("EUCORP"))
					return value;
				if (value.equals("Euro Corporate Bond"))
					return "EUCORP";
				if (value.equals("EUFRN"))
					return value;
				if (value.equals("Euro Corporate Floating Rate Notes"))
					return "EUFRN";
				if (value.equals("FRN"))
					return value;
				if (value.equals("US Corporate Floating Rate Notes"))
					return "FRN";
				if (value.equals("XLINKD"))
					return value;
				if (value.equals("Indexed Linked"))
					return "XLINKD";
				if (value.equals("STRUCT"))
					return value;
				if (value.equals("Structured Notes"))
					return "STRUCT";
				if (value.equals("YANK"))
					return value;
				if (value.equals("Yankee Corporate Bond"))
					return "YANK";
				if (value.equals("FXNDF"))
					return value;
				if (value.equals("Non-deliverable forward"))
					return "FXNDF";
				if (value.equals("FXSPOT"))
					return value;
				if (value.equals("FX Spot"))
					return "FXSPOT";
				if (value.equals("FXFWD"))
					return value;
				if (value.equals("FX Forward"))
					return "FXFWD";
				if (value.equals("FXSWAP"))
					return value;
				if (value.equals("FX Swap"))
					return "FXSWAP";
				if (value.equals("FOR"))
					return value;
				if (value.equals("Foreign Exchange Contract"))
					return "FOR";
				if (value.equals("CS"))
					return value;
				if (value.equals("Common Stock"))
					return "CS";
				if (value.equals("PS"))
					return value;
				if (value.equals("Preferred Stock"))
					return "PS";
				if (value.equals("REPO"))
					return value;
				if (value.equals("Repurchase"))
					return "REPO";
				if (value.equals("FORWARD"))
					return value;
				if (value.equals("Forward"))
					return "FORWARD";
				if (value.equals("BUYSELL"))
					return value;
				if (value.equals("Buy Sellback"))
					return "BUYSELL";
				if (value.equals("SECLOAN"))
					return value;
				if (value.equals("Securities Loan"))
					return "SECLOAN";
				if (value.equals("SECPLEDGE"))
					return value;
				if (value.equals("Securities Pledge"))
					return "SECPLEDGE";
				if (value.equals("BRADY"))
					return value;
				if (value.equals("Brady Bond"))
					return "BRADY";
				if (value.equals("CAN"))
					return value;
				if (value.equals("Canadian Treasury Notes"))
					return "CAN";
				if (value.equals("CTB"))
					return value;
				if (value.equals("Canadian Treasury Bills"))
					return "CTB";
				if (value.equals("EUSOV"))
					return value;
				if (value.equals("Euro Sovereigns"))
					return "EUSOV";
				if (value.equals("PROV"))
					return value;
				if (value.equals("Canadian Provincial Bonds"))
					return "PROV";
				if (value.equals("TB"))
					return value;
				if (value.equals("Treasury Bill - non US"))
					return "TB";
				if (value.equals("TBOND"))
					return value;
				if (value.equals("US Treasury Bond"))
					return "TBOND";
				if (value.equals("TINT"))
					return value;
				if (value.equals("Interest Strip From Any Bond Or Note"))
					return "TINT";
				if (value.equals("TBILL"))
					return value;
				if (value.equals("US Treasury Bill"))
					return "TBILL";
				if (value.equals("TIPS"))
					return value;
				if (value.equals("Treasury Inflation Protected Securities"))
					return "TIPS";
				if (value.equals("TCAL"))
					return value;
				if (value.equals("Principal Strip Of A Callable Bond Or Note"))
					return "TCAL";
				if (value.equals("TPRN"))
					return value;
				if (value.equals("Principal Strip From A Non-Callable Bond Or Note"))
					return "TPRN";
				if (value.equals("TNOTE"))
					return value;
				if (value.equals("US Treasury Note"))
					return "TNOTE";
				if (value.equals("TERM"))
					return value;
				if (value.equals("Term Loan"))
					return "TERM";
				if (value.equals("RVLV"))
					return value;
				if (value.equals("Revolver Loan"))
					return "RVLV";
				if (value.equals("RVLVTRM"))
					return value;
				if (value.equals("Revolver/Term Loan"))
					return "RVLVTRM";
				if (value.equals("BRIDGE"))
					return value;
				if (value.equals("Bridge Loan"))
					return "BRIDGE";
				if (value.equals("LOFC"))
					return value;
				if (value.equals("Letter Of Credit"))
					return "LOFC";
				if (value.equals("SWING"))
					return value;
				if (value.equals("Swing Line Facility"))
					return "SWING";
				if (value.equals("DINP"))
					return value;
				if (value.equals("Debtor In Possession"))
					return "DINP";
				if (value.equals("DEFLTED"))
					return value;
				if (value.equals("Defaulted"))
					return "DEFLTED";
				if (value.equals("WITHDRN"))
					return value;
				if (value.equals("Withdrawn"))
					return "WITHDRN";
				if (value.equals("REPLACD"))
					return value;
				if (value.equals("Replaced"))
					return "REPLACD";
				if (value.equals("MATURED"))
					return value;
				if (value.equals("Matured"))
					return "MATURED";
				if (value.equals("AMENDED"))
					return value;
				if (value.equals("Amended and Restated"))
					return "AMENDED";
				if (value.equals("RETIRED"))
					return value;
				if (value.equals("Retired"))
					return "RETIRED";
				if (value.equals("BA"))
					return value;
				if (value.equals("Bankers Acceptance"))
					return "BA";
				if (value.equals("BDN"))
					return value;
				if (value.equals("Bank Depository Note"))
					return "BDN";
				if (value.equals("BN"))
					return value;
				if (value.equals("Bank Notes"))
					return "BN";
				if (value.equals("BOX"))
					return value;
				if (value.equals("Bill Of Exchanges"))
					return "BOX";
				if (value.equals("CAMM"))
					return value;
				if (value.equals("Canadian Money Markets"))
					return "CAMM";
				if (value.equals("CD"))
					return value;
				if (value.equals("Certificate Of Deposit"))
					return "CD";
				if (value.equals("CL"))
					return value;
				if (value.equals("Call Loans"))
					return "CL";
				if (value.equals("CP"))
					return value;
				if (value.equals("Commercial Paper"))
					return "CP";
				if (value.equals("DN"))
					return value;
				if (value.equals("Deposit Notes"))
					return "DN";
				if (value.equals("EUCD"))
					return value;
				if (value.equals("Euro Certificate Of Deposit"))
					return "EUCD";
				if (value.equals("EUCP"))
					return value;
				if (value.equals("Euro Commercial Paper"))
					return "EUCP";
				if (value.equals("LQN"))
					return value;
				if (value.equals("Liquidity Note"))
					return "LQN";
				if (value.equals("MTN"))
					return value;
				if (value.equals("Medium Term Notes"))
					return "MTN";
				if (value.equals("ONITE"))
					return value;
				if (value.equals("Overnight"))
					return "ONITE";
				if (value.equals("PN"))
					return value;
				if (value.equals("Promissory Note"))
					return "PN";
				if (value.equals("PZFJ"))
					return value;
				if (value.equals("Plazos Fijos"))
					return "PZFJ";
				if (value.equals("STN"))
					return value;
				if (value.equals("Short Term Loan Note"))
					return "STN";
				if (value.equals("SLQN"))
					return value;
				if (value.equals("Secured Liquidity Note"))
					return "SLQN";
				if (value.equals("TD"))
					return value;
				if (value.equals("Time Deposit"))
					return "TD";
				if (value.equals("TLQN"))
					return value;
				if (value.equals("Term Liquidity Note"))
					return "TLQN";
				if (value.equals("XCN"))
					return value;
				if (value.equals("Extended Comm Note"))
					return "XCN";
				if (value.equals("YCD"))
					return value;
				if (value.equals("Yankee Certificate Of Deposit"))
					return "YCD";
				if (value.equals("ABS"))
					return value;
				if (value.equals("Asset-backed Securities"))
					return "ABS";
				if (value.equals("CMB"))
					return value;
				if (value.equals("Canadian Mortgage Bonds"))
					return "CMB";
				if (value.equals("CMBS"))
					return value;
				if (value.equals("Corp. Mortgage-backed Securities"))
					return "CMBS";
				if (value.equals("CMO"))
					return value;
				if (value.equals("Collateralized Mortgage Obligation"))
					return "CMO";
				if (value.equals("IET"))
					return value;
				if (value.equals("IOETTE Mortgage"))
					return "IET";
				if (value.equals("MBS"))
					return value;
				if (value.equals("Mortgage-backed Securities"))
					return "MBS";
				if (value.equals("MIO"))
					return value;
				if (value.equals("Mortgage Interest Only"))
					return "MIO";
				if (value.equals("MPO"))
					return value;
				if (value.equals("Mortgage Principal Only"))
					return "MPO";
				if (value.equals("MPP"))
					return value;
				if (value.equals("Mortgage Private Placement"))
					return "MPP";
				if (value.equals("MPT"))
					return value;
				if (value.equals("Miscellaneous Pass-through"))
					return "MPT";
				if (value.equals("PFAND"))
					return value;
				if (value.equals("Pfandbriefe"))
					return "PFAND";
				if (value.equals("TBA"))
					return value;
				if (value.equals("To Be Announced"))
					return "TBA";
				if (value.equals("AN"))
					return value;
				if (value.equals("Other Anticipation Notes"))
					return "AN";
				if (value.equals("COFO"))
					return value;
				if (value.equals("Certificate Of Obligation"))
					return "COFO";
				if (value.equals("COFP"))
					return value;
				if (value.equals("Certificate Of Participation"))
					return "COFP";
				if (value.equals("GO"))
					return value;
				if (value.equals("General Obligation Bonds"))
					return "GO";
				if (value.equals("MT"))
					return value;
				if (value.equals("Mandatory Tender"))
					return "MT";
				if (value.equals("RAN"))
					return value;
				if (value.equals("Revenue Anticipation Note"))
					return "RAN";
				if (value.equals("REV"))
					return value;
				if (value.equals("Revenue Bonds"))
					return "REV";
				if (value.equals("SPCLA"))
					return value;
				if (value.equals("Special Assessment"))
					return "SPCLA";
				if (value.equals("SPCLO"))
					return value;
				if (value.equals("Special Obligation"))
					return "SPCLO";
				if (value.equals("SPCLT"))
					return value;
				if (value.equals("Special Tax"))
					return "SPCLT";
				if (value.equals("TAN"))
					return value;
				if (value.equals("Tax Anticipation Note"))
					return "TAN";
				if (value.equals("TAXA"))
					return value;
				if (value.equals("Tax Allocation"))
					return "TAXA";
				if (value.equals("TECP"))
					return value;
				if (value.equals("Tax Exempt Commercial Paper"))
					return "TECP";
				if (value.equals("TMCP"))
					return value;
				if (value.equals("Taxable Municipal CP"))
					return "TMCP";
				if (value.equals("TRAN"))
					return value;
				if (value.equals("Tax Revenue Anticipation Note"))
					return "TRAN";
				if (value.equals("VRDN"))
					return value;
				if (value.equals("Variable Rate Demand Note"))
					return "VRDN";
				if (value.equals("WAR"))
					return value;
				if (value.equals("Warrant"))
					return "WAR";
				if (value.equals("MF"))
					return value;
				if (value.equals("Mutual Fund"))
					return "MF";
				if (value.equals("MLEG"))
					return value;
				if (value.equals("Multileg Instrument"))
					return "MLEG";
				if (value.equals("NONE"))
					return value;
				if (value.equals("No Security Type"))
					return "NONE";
				if (value.equals("CASH"))
					return value;
				if (value.equals("Cash"))
					return "CASH";
				if (value.equals("CDS"))
					return value;
				if (value.equals("Credit Default Swap"))
					return "CDS";
				if (value.equals("FUT"))
					return value;
				if (value.equals("Future"))
					return "FUT";
				if (value.equals("OPT"))
					return value;
				if (value.equals("Option"))
					return "OPT";
				if (value.equals("OOF"))
					return value;
				if (value.equals("Options on Futures"))
					return "OOF";
				if (value.equals("OOP"))
					return value;
				if (value.equals("Options on Physical"))
					return "OOP";
				if (value.equals("IRS"))
					return value;
				if (value.equals("Interest Rate Swap"))
					return "IRS";
				if (value.equals("OOC"))
					return value;
				if (value.equals("Options on Combo"))
					return "OOC";
			}
		}

		else if (column.equals("Security Identifier Source") || column.equals("Security Alternative Identifier Source")) {
			if (value instanceof String) {
				if (value.equals("1"))
					return (String) value;
				if (value.equals("CUSIP"))
					return "1";
				if (value.equals("2"))
					return (String) value;
				if (value.equals("SEDOL"))
					return "2";
				if (value.equals("3"))
					return (String) value;
				if (value.equals("QUIK"))
					return "3";
				if (value.equals("4"))
					return (String) value;
				if (value.equals("ISIN number"))
					return "4";
				if (value.equals("5"))
					return (String) value;
				if (value.equals("RIC code"))
					return "5";
				if (value.equals("6"))
					return (String) value;
				if (value.equals("ISO Currency Code"))
					return "6";
				if (value.equals("7"))
					return (String) value;
				if (value.equals("ISO Country Code"))
					return "7";
				if (value.equals("8"))
					return (String) value;
				if (value.equals("Exchange Symbol"))
					return "8";
				if (value.equals("9"))
					return (String) value;
				if (value.equals("Consolidated Tape Association"))
					return "9";

				if (value.equals("A"))
					return (String) value;
				if (value.equals("Bloomberg Symbol"))
					return "A";
				if (value.equals("B"))
					return (String) value;
				if (value.equals("Wertpapier"))
					return "B";
				if (value.equals("C"))
					return (String) value;
				if (value.equals("Dutch"))
					return "C";
				if (value.equals("D"))
					return (String) value;
				if (value.equals("Valoren"))
					return "D";
				if (value.equals("E"))
					return (String) value;
				if (value.equals("Sicovam"))
					return "E";
				if (value.equals("F"))
					return (String) value;
				if (value.equals("Belgian"))
					return "F";
				if (value.equals("G"))
					return (String) value;
				if (value.equals("Common"))
					return "G";
				if (value.equals("H"))
					return (String) value;
				if (value.equals("Clearing House"))
					return "H";
				if (value.equals("I"))
					return (String) value;
				if (value.equals("ISDA/FpML Product Specification"))
					return "I";
				if (value.equals("J"))
					return (String) value;
				if (value.equals("Option Price Reporting Authority"))
					return "J";
				if (value.equals("K"))
					return (String) value;
				if (value.equals("ISDA/FpML Product URL"))
					return "K";
				if (value.equals("L"))
					return (String) value;
				if (value.equals("Letter of Credit"))
					return "L";
				if (value.equals("M"))
					return (String) value;
				if (value.equals("Marketplace-assigned Identifier"))
					return "M";

			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "1";
				if (((Double) value).doubleValue() == 2)
					return "2";
				if (((Double) value).doubleValue() == 3)
					return "3";
				if (((Double) value).doubleValue() == 4)
					return "4";
				if (((Double) value).doubleValue() == 5)
					return "5";
				if (((Double) value).doubleValue() == 6)
					return "6";
				if (((Double) value).doubleValue() == 7)
					return "7";
				if (((Double) value).doubleValue() == 8)
					return "8";
				if (((Double) value).doubleValue() == 9)
					return "9";
			}
		}

		else if (column.equals("Put or Call")) {
			if (value instanceof String) {
				if (value.equals("Put"))
					return new Integer(0);
				if (value.equals("Call"))
					return new Integer(1);
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
			}
		}

		
		else if (column.equals("Country of Issue")) {
			Locale[] localeList = NumberFormat.getAvailableLocales();
			for (Locale locale : localeList) {
				if (value.equals(locale.getDisplayCountry()))
					return (locale.getCountry());
				if (value.equals(locale.getCountry()))
					return value;
			}
		}
		
		else if (column.equals("Contract Multiplier Unit")) {
			if (value instanceof String) {
				if (value.equals("Shares"))
					return new Integer(0);
				if (value.equals("Hours"))
					return new Integer(1);
				if (value.equals("Days"))
					return new Integer(2);
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
			}
		}
		
		else if (column.equals("Unit of Measure")||column.equals("Price Unit of Measure")) {
			if (value instanceof String) {
				if (value.equals("Billion cubic feet"))
					return "Bcf";
				if (value.equals("Million Barrels"))
					return "MMbbl";
				if (value.equals("One Million BTU"))
					return "MMBtu";
				if (value.equals("Megawatt hours"))
					return "MWh";
				if (value.equals("Barrels"))
					return "Bbl";
				if (value.equals("Bushels"))
					return "Bu";
				if (value.equals("Pounds"))
					return "lbs";
				if (value.equals("Gallons"))
					return "Gal";
				if (value.equals("Troy Ounces"))
					return "oz_tr";
				if (value.equals("Metric Tons"))
					return "t";
				if (value.equals("Tons (US)"))
					return "tn";
				if (value.equals("US Dollars"))
					return "USD";
				if (value.equals("Allowances"))
					return "Alw";

				if (value.equals("Bcf"))
					return (String) value;
				if (value.equals("MMbbl"))
					return (String) value;
				if (value.equals("MMBtu"))
					return (String) value;
				if (value.equals("MWh"))
					return (String) value;
				if (value.equals("Bbl"))
					return (String) value;
				if (value.equals("Bu"))
					return (String) value;
				if (value.equals("lbs"))
					return (String) value;
				if (value.equals("Gal"))
					return (String) value;
				if (value.equals("oz_tr"))
					return (String) value;
				if (value.equals("t"))
					return (String) value;
				if (value.equals("tn"))
					return (String) value;
				if (value.equals("USD"))
					return (String) value;
				if (value.equals("Alw"))
					return (String) value;
			}
		}

		else if (column.equals("Time Unit")) {
			if (value instanceof String) {
				
				if (value.equals("Hour"))
					return "H";
				if (value.equals("Minute"))
					return "Min";
				if (value.equals("Second"))
					return "S";
				if (value.equals("Day"))
					return "D";
				if (value.equals("Week"))
					return "Wk";
				if (value.equals("Month"))
					return "Mo";
				if (value.equals("Year"))
					return "Yr";
				
				if (value.equals("H"))
					return "H";
				if (value.equals("Min"))
					return "Min";
				if (value.equals("S"))
					return "S";
				if (value.equals("D"))
					return "D";
				if (value.equals("Wk"))
					return "Wk";
				if (value.equals("Mo"))
					return "Mo";
				if (value.equals("Yr"))
					return "Yr";

			}
		}

		else if (column.equals("Strike Price Determination Method")) {
			if (value instanceof String) {
				
				if (value.equals("Fixed Strike"))
					return new Integer(1);
				if (value.equals("Strike set at expiration"))
					return new Integer(2);
				if (value.equals("Strike set to average across life"))
					return new Integer(3);
				if (value.equals("Strike set to optimal value"))
					return new Integer(4);
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
			}
		}
		
		else if (column.equals("Security Exchange")) {
			if (value instanceof String) {
				ExchangeComboBoxEntries exchangeComboBoxEntries = new ExchangeComboBoxEntries();
				for(ComboBoxEntry comboBoxEntry: exchangeComboBoxEntries.getComboBoxEntries())
				{
					if(comboBoxEntry.toString().equals(value))
						return (String)comboBoxEntry.getEntry();
					if(comboBoxEntry.getEntry().equals(value))
						return (String)comboBoxEntry.getEntry();
				}
			}
		}
		
		else if (column.equals("Commercial Paper Programm")) {
			if (value instanceof String) {
				
				if (value.equals("3(a)(3)"))
					return new Integer(1);
				if (value.equals("4(2)"))
					return new Integer(2);
				if (value.equals("Other"))
					return new Integer(99);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 99)
					return new Integer(99);
			}
		}
		
		else if (column.equals("Security Status")) {
			if (value instanceof String) {
				
				if (value.equals("Active"))
					return "1";
				if (value.equals("Inactive"))
					return "2";
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "1";
				if (((Double) value).doubleValue() == 2)
					return "2";
			}
		}
		
		else if (column.equals("Settle on Open")) {
			if (value instanceof String) {
				
				if (value.equals("Y"))
					return (String)value;
				if (value.equals("N"))
					return (String)value;
				if (value.equals("Yes"))
					return "Y";
				if (value.equals("No"))
					return "N";
			}
			
		}
		
		else if (column.equals("Instrument Assignment Method")) {
			if (value instanceof String) {
				
				if (value.equals("P"))
					return (String)value;
				if (value.equals("R"))
					return (String)value;
				if (value.equals("Pro rata"))
					return "P";
				if (value.equals("Random"))
					return "R";
			}
			
		}
		
		else if (column.equals("Underlying Price Determination Method")) {
			if (value instanceof String) {
				
				if (value.equals("Regular"))
					return new Integer(1);
				if (value.equals("Special reference"))
					return new Integer(2);
				if (value.equals("Optimal value"))
					return new Integer(3);
				if (value.equals("Average value"))
					return new Integer(4);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
			}
		}
		
		else if (column.equals("Delivery Form")) {
			if (value instanceof String) {
				
				if (value.equals("Book Entry"))
					return new Integer(1);
				if (value.equals("Bearer"))
					return new Integer(2);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
			}
		}
		
		else if (column.equals("Settlement Method")) {
			if (value instanceof String) {
				
				if (value.equals("Cash settlement required"))
					return "C";
				if (value.equals("Physical settlement required"))
					return "P";
				if (value.equals("C"))
					return (String)value;
				if (value.equals("P"))
					return (String)value;
			}
		}
		
		else if (column.equals("Exercise Style")) {
			if (value instanceof String) {
				
				if (value.equals("European"))
					return new Integer(0);
				if (value.equals("American"))
					return new Integer(1);
				if (value.equals("Bermuda"))
					return new Integer(2);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
			}
		}
		
		else if (column.equals("Option Payout Type")) {
			if (value instanceof String) {
				
				if (value.equals("Vanilla"))
					return new Integer(1);
				if (value.equals("Capped"))
					return new Integer(2);
				if (value.equals("Binary"))
					return new Integer(3);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
			}
		}
		
		else if (column.equals("Price Quote Method")) {
			if (value instanceof String) {
				
				if (value.equals("Standard, money per unit of a physical"))
					return "STD";
				if (value.equals("Index"))
					return "INX";
				if (value.equals("Interest rate Index"))
					return "INT";
				if (value.equals("Percent of Par"))
					return "PCTPAR";
				if (value.equals("STD"))
					return (String)value;
				if (value.equals("INX"))
					return (String)value;
				if (value.equals("INT"))
					return (String)value;
				if (value.equals("PCTPAR"))
					return (String)value;
			}
		}
		
		else if (column.equals("List Method")) {
			if (value instanceof String) {
				
				if (value.equals("pre-listed only"))
					return new Integer(0);
				if (value.equals("user requested"))
					return new Integer(1);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
			}
		}
		
		else if (column.equals("List Method")) {
			if (value instanceof String) {
				
				if (value.equals("pre-listed only"))
					return new Integer(0);
				if (value.equals("user requested"))
					return new Integer(1);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
			}
		}
		
		else if (column.equals("Flexible Indicator")||column.equals("Flexible Product Eligibility Indicator")) {
			if (value instanceof String) {
				
				if (value.equals("Flexible"))
					return new Boolean(true);
				if (value.equals("Not flexible"))
					return new Boolean(false);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return  new Boolean(false);
				if (((Double) value).doubleValue() == 1)
					return new Boolean(true);
			}
			
			if (value instanceof Boolean) {
				return (Boolean)value;
			}
		}
		
		else if (column.equals("Valuation Method")) {
			if (value instanceof String) {
				
				if (value.equals( "Premium style"))
					return "EQTY";
				if (value.equals("Futures style mark-to-market"))
					return "FUT";
				if (value.equals("Futures style with an attached cash adjustment"))
					return "FUTDA";
				if (value.equals("CDS style collateralization"))
					return "CDS";
				if (value.equals("CDS in delivery - use recovery rate to calculate obligation"))
					return "CDSD";
				
				if (value.equals("EQTY"))
					return (String)value;
				if (value.equals("FUT"))
					return (String)value;
				if (value.equals("FUTDA"))
					return (String)value;
				if (value.equals("CDS"))
					return (String)value;
				if (value.equals("CDSD"))
					return (String)value;
			}
		}
		
		else if (column.equals("Flow Schedule Type")) {
			if (value instanceof String) {
				
				if (value.equals("NERC Eastern Off-Peak"))
					return new Integer(0);
				if (value.equals("NERC Western Off-Peak"))
					return new Integer(1);
				if (value.equals("NERC Calendar-All Days in month"))
					return new Integer(2);
				if (value.equals("NERC Eastern Peak"))
					return new Integer(3);
				if (value.equals("NERC Western Peak"))
					return new Integer(4);

			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return new Integer(0);
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);

			}
		}
		
		else if (column.equals("Restructuring Type")) {
			if (value instanceof String) {
				
				if (value.equals("Full Restructuring"))
					return "FR";
				if (value.equals("Modified Restructuring"))
					return "MR";
				if (value.equals("Modified Mod Restructuring"))
					return "MM";
				if (value.equals("No Restructuring specified"))
					return "XR";
				
				if (value.equals("FR"))
					return (String)value;
				if (value.equals("MR"))
					return (String)value;
				if (value.equals("MM"))
					return (String)value;
				if (value.equals("XR"))
					return (String)value;

			}
			
		}
		
		else if (column.equals("Seniority")) {
			if (value instanceof String) {
				
				if (value.equals("Senior Secured"))
					return "SD";
				if (value.equals("Senior"))
					return "SR";
				if (value.equals("Subordinated"))
					return "SB";
				
				if (value.equals("SD"))
					return (String)value;
				if (value.equals("SR"))
					return (String)value;
				if (value.equals("SB"))
					return (String)value;

			}
			
		}
		
		else if (column.equals("Event Type")) {
			if (value instanceof String) {
				
				if (value.equals("Put"))
					return new Integer(1);
				if (value.equals("Call"))
					return new Integer(2);
				if (value.equals("Tender"))
					return new Integer(3);
				if (value.equals("Sinking Fund Call"))
					return new Integer(4);
				if (value.equals("Activation"))
					return new Integer(5);
				if (value.equals("Inactiviation"))
					return new Integer(6);
				if (value.equals("Last Eligible Trade Date"))
					return new Integer(7);
				if (value.equals("Swap Start Date"))
					return new Integer(8);
				if (value.equals("Swap End Date"))
					return new Integer(9);
				if (value.equals("Swap Roll Date"))
					return new Integer(10);
				if (value.equals("Swap Next Start Date"))
					return new Integer(11);
				if (value.equals("Swap Next Roll Date"))
					return new Integer(12);
				if (value.equals("First Delivery Date"))
					return new Integer(13);
				if (value.equals("Last Delivery Date"))
					return new Integer(14);
				if (value.equals("Initial Inventory Due Date"))
					return new Integer(15);
				if (value.equals("Final Inventory Due Date"))
					return new Integer(16);
				if (value.equals("First Intent Date"))
					return new Integer(17);
				if (value.equals("Last Intent Date"))
					return new Integer(18);
				if (value.equals("Position Removal Date"))
					return new Integer(19);
				if (value.equals("Other"))
					return new Integer(99);

			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
				if (((Double) value).doubleValue() == 6)
					return new Integer(6);
				if (((Double) value).doubleValue() == 7)
					return new Integer(7);
				if (((Double) value).doubleValue() == 8)
					return new Integer(8);
				if (((Double) value).doubleValue() == 9)
					return new Integer(9);
				if (((Double) value).doubleValue() == 10)
					return new Integer(10);
				if (((Double) value).doubleValue() == 11)
					return new Integer(11);
				if (((Double) value).doubleValue() == 12)
					return new Integer(12);
				if (((Double) value).doubleValue() == 13)
					return new Integer(13);
				if (((Double) value).doubleValue() == 14)
					return new Integer(14);
				if (((Double) value).doubleValue() == 15)
					return new Integer(15);
				if (((Double) value).doubleValue() == 16)
					return new Integer(16);
				if (((Double) value).doubleValue() == 17)
					return new Integer(17);
				if (((Double) value).doubleValue() == 18)
					return new Integer(18);
				if (((Double) value).doubleValue() == 19)
					return new Integer(19);
				if (((Double) value).doubleValue() == 99)
					return new Integer(99);

			}
		}
		
		
		else if (column.equals("Complex Event Type")) {
			if (value instanceof String) {
				
				if (value.equals("Capped"))
					return new Integer(1);
				if (value.equals("Trigger"))
					return new Integer(2);
				if (value.equals("Knock-in up"))
					return new Integer(3);
				if (value.equals("Kock-in down"))
					return new Integer(4);
				if (value.equals("Knock-out up"))
					return new Integer(5);
				if (value.equals("Knock-out down"))
					return new Integer(6);
				if (value.equals("Underlying"))
					return new Integer(7);
				if (value.equals("Reset Barrier"))
					return new Integer(8);
				if (value.equals("Rolling Barrier"))
					return new Integer(9);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
				if (((Double) value).doubleValue() == 6)
					return new Integer(6);
				if (((Double) value).doubleValue() == 7)
					return new Integer(7);
				if (((Double) value).doubleValue() == 8)
					return new Integer(8);
				if (((Double) value).doubleValue() == 9)
					return new Integer(9);
			}
		}
		
		else if (column.equals("Complex Event Price Boundary Method")) {
			if (value instanceof String) {
				
				if (value.equals("Less than complex event price"))
					return new Integer(1);
				if (value.equals("Less than or equal to complex event price"))
					return new Integer(2);
				if (value.equals("Equal to complex event price"))
					return new Integer(3);
				if (value.equals("Greater than or equal to complex event price"))
					return new Integer(4);
				if (value.equals("Greater than complex event price"))
					return new Integer(5);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
			}
		}
				
		else if (column.equals("Complex Event Price Time Type")) {
			if (value instanceof String) {
				
				if (value.equals("Expiration"))
					return new Integer(1);
				if (value.equals("Immediate"))
					return new Integer(2);
				if (value.equals("Specified Date"))
					return new Integer(3);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
			}
		}
		
		else if (column.equals("Complex Event Condition")) {
			if (value instanceof String) {
				
				if (value.equals("And"))
					return new Integer(1);
				if (value.equals("Or"))
					return new Integer(2);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
			}
		}	
				
		else if (column.equals("Underlying Settlement Type")) {
			if (value instanceof String) {
				
				if (value.equals("T+1"))
					return new Integer(2);
				if (value.equals("T+3"))
					return new Integer(4);
				if (value.equals("T+4"))
					return new Integer(5);
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
			}
		}
		
		else if (column.equals("Underlying Cash Type")) {
			if (value instanceof String) {
				
				if (value.equals("FIXED"))
					return (String)value;
				if (value.equals("DIFF"))
					return (String)value;
			}
		}	
		
		else if (column.equals("Leg Side")) {
			if (value instanceof String) {
				
				if (value.equals("A"))
					return (String)value;
				if (value.equals("B"))
					return (String)value;
				if (value.equals("C"))
					return (String)value;
				if (value.equals("D"))
					return (String)value;
				if (value.equals("E"))
					return (String)value;
				if (value.equals("F"))
					return (String)value;
				if (value.equals("G"))
					return (String)value;
				
				if (value.equals("Buy"))
					return "1";
				if (value.equals("Sell"))
					return "2";
				if (value.equals("Buy minus"))
					return "3";
				if (value.equals("Sell plus"))
					return "4";
				if (value.equals("Sell short"))
					return "5";
				if (value.equals("Sell short exempt"))
					return "6";
				if (value.equals("Undisclosed"))
					return "7";
				if (value.equals("Cross"))
					return "8";
				if (value.equals("Cross short"))
					return "9";
				if (value.equals("Cross short exempt"))
					return "A";
				if (value.equals("As Defined"))
					return "B";
				if (value.equals("Opposite"))
					return "C";
				if (value.equals("Subscribe"))
					return "D";
				if (value.equals("Redeem"))
					return "E";
				if (value.equals("Lend"))
					return "F";
				if (value.equals("Borrow"))
					return "G";
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
				if (((Double) value).doubleValue() == 6)
					return new Integer(6);
				if (((Double) value).doubleValue() == 7)
					return new Integer(7);
				if (((Double) value).doubleValue() == 8)
					return new Integer(8);
				if (((Double) value).doubleValue() == 9)
					return new Integer(9);
			}
			
		}	
		
		else if (column.equals("Attribute Type")) {
			if (value instanceof String) {
				if (value.equals("Flat"))
					return new Integer(1);
				if (value.equals("Zero coupon"))
					return new Integer(2);
				if (value.equals("Interest bearing"))
					return new Integer(3);
				if (value.equals("No periodic payments"))
					return new Integer(4);
				if (value.equals("Variable rate"))
					return new Integer(5);
				if (value.equals("Less fee for put"))
					return new Integer(6);
				if (value.equals("Stepped coupon"))
					return new Integer(7);
				if (value.equals("Coupon period"))
					return new Integer(8);
				if (value.equals("When (and if) issued"))
					return new Integer(9);
				if (value.equals("Original issue discount"))
					return new Integer(10);
				if (value.equals("Callable, puttable"))
					return new Integer(11);
				if (value.equals("Escrowed to Maturity"))
					return new Integer(12);
				if (value.equals("Escrowed to redemption date"))
					return new Integer(13);
				if (value.equals("Pre-refunded"))
					return new Integer(14);
				if (value.equals("In default"))
					return new Integer(15);
				if (value.equals("Unrated"))
					return new Integer(16);
				if (value.equals("Taxable"))
					return new Integer(17);
				if (value.equals("Indexed"))
					return new Integer(18);
				if (value.equals("Subject To Alternative Minimum Tax"))
					return new Integer(19);
				if (value.equals("Original issue discount price"))
					return new Integer(20);
				if (value.equals("Callable below maturity value"))
					return new Integer(21);
				if (value.equals("Callable without notice"))
					return new Integer(22);
				if (value.equals("Price tick rules for security"))
					return new Integer(23);
				if (value.equals("Trade type eligibility details for security"))
					return new Integer(24);
				if (value.equals("Instrument Denominator"))
					return new Integer(25);
				if (value.equals("Instrument Numerator"))
					return new Integer(26);
				if (value.equals("Instrument Price Precision"))
					return new Integer(27);
				if (value.equals("Instrument Strike Price"))
					return new Integer(28);
				if (value.equals("Tradeable Indicator"))
					return new Integer(29);
				if (value.equals("Text"))
					return new Integer(99);
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return new Integer(1);
				if (((Double) value).doubleValue() == 2)
					return new Integer(2);
				if (((Double) value).doubleValue() == 3)
					return new Integer(3);
				if (((Double) value).doubleValue() == 4)
					return new Integer(4);
				if (((Double) value).doubleValue() == 5)
					return new Integer(5);
				if (((Double) value).doubleValue() == 6)
					return new Integer(6);
				if (((Double) value).doubleValue() == 7)
					return new Integer(7);
				if (((Double) value).doubleValue() == 8)
					return new Integer(8);
				if (((Double) value).doubleValue() == 9)
					return new Integer(9);
				if (((Double) value).doubleValue() == 10)
					return new Integer(10);
				if (((Double) value).doubleValue() == 11)
					return new Integer(11);
				if (((Double) value).doubleValue() == 12)
					return new Integer(12);
				if (((Double) value).doubleValue() == 13)
					return new Integer(13);
				if (((Double) value).doubleValue() == 14)
					return new Integer(14);
				if (((Double) value).doubleValue() == 15)
					return new Integer(15);
				if (((Double) value).doubleValue() == 16)
					return new Integer(16);
				if (((Double) value).doubleValue() == 17)
					return new Integer(17);
				if (((Double) value).doubleValue() == 18)
					return new Integer(18);
				if (((Double) value).doubleValue() == 19)
					return new Integer(19);
				if (((Double) value).doubleValue() == 20)
					return new Integer(20);
				if (((Double) value).doubleValue() == 21)
					return new Integer(21);
				if (((Double) value).doubleValue() == 22)
					return new Integer(22);
				if (((Double) value).doubleValue() == 23)
					return new Integer(23);
				if (((Double) value).doubleValue() == 24)
					return new Integer(24);
				if (((Double) value).doubleValue() == 25)
					return new Integer(25);
				if (((Double) value).doubleValue() == 26)
					return new Integer(26);
				if (((Double) value).doubleValue() == 27)
					return new Integer(27);
				if (((Double) value).doubleValue() == 28)
					return new Integer(28);
				if (((Double) value).doubleValue() == 29)
					return new Integer(29);
				if (((Double) value).doubleValue() == 99)
					return new Integer(99);
			}
		}

		return null;
	}

	/**
	 * Convert value to string.
	 *
	 * @param value the value
	 * @param column the column
	 * @return the string
	 */
	public String convertValueToString(Object value, String column) {

		if (value == null)
			return "";

		if (column == null) {

			if (value instanceof String) {
				return (String) value;
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "TRUE";
				return "FALSE";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);
		}

		if (stringEntries.contains(column) || currencyEntries.contains(column)) {
			if (value instanceof String) {
				return (String) value;
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "TRUE";
				return "FALSE";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);

		}

		else if (doubleEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return decimalFormat.format(decimalFormat.parse((String) value).doubleValue());
				}
				catch (Exception e) {
					return (String) value;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "1";
				return "0";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return (String) value;
		}

		else if (integerEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return decimalFormat.format(decimalFormat.parse((String) value).intValue());
				}
				catch (Exception e) {
					return (String) value;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "1";
				return "0";
			}
			if (value instanceof Double)
				return decimalFormat.format(((Double) value).intValue());
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);

		}

		else if (dateEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return simpleDateFormat.format(simpleDateFormat.parse((String) value));
				}
				catch (Exception e) {
					return (String) value;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "TRUE";
				return "FALSE";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);
		}

		else if (timeEntries.contains(column)) {
			if (value instanceof String) {
				try {
					return simpleTimeFormat.format(simpleTimeFormat.parse((String) value));
				}
				catch (Exception e) {
					return (String) value;
				}
			}
			if (value instanceof Boolean) {
				if ((Boolean) value)
					return "TRUE";
				return "FALSE";
			}
			if (value instanceof Double)
				return decimalFormat.format((Double) value);
			if (value instanceof Date)
				return simpleDateFormat.format((Date) value);
		}

		else if (column.equals("Product")) {
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Agency";
				if (((Double) value).doubleValue() == 2)
					return "Commodity";
				if (((Double) value).doubleValue() == 3)
					return "Corporate";
				if (((Double) value).doubleValue() == 4)
					return "Currency";
				if (((Double) value).doubleValue() == 5)
					return "Equity";
				if (((Double) value).doubleValue() == 6)
					return "Government";
				if (((Double) value).doubleValue() == 7)
					return "Index";
				if (((Double) value).doubleValue() == 8)
					return "Loan";
				if (((Double) value).doubleValue() == 9)
					return "Money Market";
				if (((Double) value).doubleValue() == 10)
					return "Mortage";
				if (((Double) value).doubleValue() == 11)
					return "Municipal";
				if (((Double) value).doubleValue() == 12)
					return "Other";
				if (((Double) value).doubleValue() == 13)
					return "Financing";
			}
		}

		else if (column.equals("Additional Information")) {
			if (value instanceof String) {
				if (value.equals("CD"))
					return "EUCP with lump-sum interest rather than discount price";
				if (value.equals("EUCP with lump-sum interest rather than discount price"))
					return (String) value;
				if (value.equals("WI"))
					return "When Issued";
				if (value.equals("When Issued"))
					return (String) value;
			}
		}

		else if (column.equals("Security Type")) {
			if (value instanceof String) {
				if (value.equals("EUSUPRA"))
					return "Euro Supranational Coupons";
				if (value.equals("Euro Supranational Coupons"))
					return (String) value;
				if (value.equals("FAC"))
					return "Federal Agency Coupon";
				if (value.equals("Federal Agency Coupon"))
					return (String) value;
				if (value.equals("FADN"))
					return "Federal Agency Discount Note";
				if (value.equals("Federal Agency Discount Note"))
					return (String) value;
				if (value.equals("PEF"))
					return "Private Export Funding";
				if (value.equals("Private Export Funding"))
					return (String) value;
				if (value.equals("SUPRA"))
					return "USD Supranational Coupons";
				if (value.equals("USD Supranational Coupons"))
					return (String) value;
				if (value.equals("CORP"))
					return "Corporate Bond";
				if (value.equals("Corporate Bond"))
					return (String) value;
				if (value.equals("CPP"))
					return "Corporate Private Placement";
				if (value.equals("Corporate Private Placement"))
					return (String) value;
				if (value.equals("CB"))
					return "Convertible Bond";
				if (value.equals("Convertible Bond"))
					return (String) value;
				if (value.equals("DUAL"))
					return "Dual Currency";
				if (value.equals("Dual Currency"))
					return (String) value;
				if (value.equals("EUCORP"))
					return "Euro Corporate Bond";
				if (value.equals("Euro Corporate Bond"))
					return (String) value;
				if (value.equals("EUFRN"))
					return "Euro Corporate Floating Rate Notes";
				if (value.equals("Euro Corporate Floating Rate Notes"))
					return (String) value;
				if (value.equals("FRN"))
					return "US Corporate Floating Rate Notes";
				if (value.equals("US Corporate Floating Rate Notes"))
					return (String) value;
				if (value.equals("XLINKD"))
					return "Indexed Linked";
				if (value.equals("Indexed Linked"))
					return (String) value;
				if (value.equals("STRUCT"))
					return "Structured Notes";
				if (value.equals("Structured Notes"))
					return (String) value;
				if (value.equals("YANK"))
					return "Yankee Corporate Bond";
				if (value.equals("Yankee Corporate Bond"))
					return (String) value;
				if (value.equals("FXNDF"))
					return "Non-deliverable forward";
				if (value.equals("Non-deliverable forward"))
					return (String) value;
				if (value.equals("FXSPOT"))
					return "FX Spot";
				if (value.equals("FX Spot"))
					return (String) value;
				if (value.equals("FXFWD"))
					return "FX Forward";
				if (value.equals("FX Forward"))
					return (String) value;
				if (value.equals("FXSWAP"))
					return "FX Swap";
				if (value.equals("FX Swap"))
					return (String) value;
				if (value.equals("FOR"))
					return "Foreign Exchange Contract";
				if (value.equals("Foreign Exchange Contract"))
					return (String) value;
				if (value.equals("CS"))
					return "Common Stock";
				if (value.equals("Common Stock"))
					return (String) value;
				if (value.equals("PS"))
					return "Preferred Stock";
				if (value.equals("Preferred Stock"))
					return (String) value;
				if (value.equals("REPO"))
					return "Repurchase";
				if (value.equals("Repurchase"))
					return (String) value;
				if (value.equals("FORWARD"))
					return "Forward";
				if (value.equals("Forward"))
					return (String) value;
				if (value.equals("BUYSELL"))
					return "Buy Sellback";
				if (value.equals("Buy Sellback"))
					return (String) value;
				if (value.equals("SECLOAN"))
					return "Securities Loan";
				if (value.equals("Securities Loan"))
					return (String) value;
				if (value.equals("SECPLEDGE"))
					return "Securities Pledge";
				if (value.equals("Securities Pledge"))
					return (String) value;
				if (value.equals("BRADY"))
					return "Brady Bond";
				if (value.equals("Brady Bond"))
					return (String) value;
				if (value.equals("CAN"))
					return "Canadian Treasury Notes";
				if (value.equals("Canadian Treasury Notes"))
					return (String) value;
				if (value.equals("CTB"))
					return "Canadian Treasury Bills";
				if (value.equals("Canadian Treasury Bills"))
					return (String) value;
				if (value.equals("EUSOV"))
					return "Euro Sovereigns";
				if (value.equals("Euro Sovereigns"))
					return (String) value;
				if (value.equals("PROV"))
					return "Canadian Provincial Bonds";
				if (value.equals("Canadian Provincial Bonds"))
					return (String) value;
				if (value.equals("TB"))
					return "Treasury Bill - non US";
				if (value.equals("Treasury Bill - non US"))
					return (String) value;
				if (value.equals("TBOND"))
					return "US Treasury Bond";
				if (value.equals("US Treasury Bond"))
					return (String) value;
				if (value.equals("TINT"))
					return "Interest Strip From Any Bond Or Note";
				if (value.equals("Interest Strip From Any Bond Or Note"))
					return (String) value;
				if (value.equals("TBILL"))
					return "US Treasury Bill";
				if (value.equals("US Treasury Bill"))
					return (String) value;
				if (value.equals("TIPS"))
					return "Treasury Inflation Protected Securities";
				if (value.equals("Treasury Inflation Protected Securities"))
					return (String) value;
				if (value.equals("TCAL"))
					return "Principal Strip Of A Callable Bond Or Note";
				if (value.equals("Principal Strip Of A Callable Bond Or Note"))
					return (String) value;
				if (value.equals("TPRN"))
					return "Principal Strip From A Non-Callable Bond Or Note";
				if (value.equals("Principal Strip From A Non-Callable Bond Or Note"))
					return (String) value;
				if (value.equals("TNOTE"))
					return "US Treasury Note";
				if (value.equals("US Treasury Note"))
					return (String) value;
				if (value.equals("TERM"))
					return "Term Loan";
				if (value.equals("Term Loan"))
					return (String) value;
				if (value.equals("RVLV"))
					return "Revolver Loan";
				if (value.equals("Revolver Loan"))
					return (String) value;
				if (value.equals("RVLVTRM"))
					return "Revolver/Term Loan";
				if (value.equals("Revolver/Term Loan"))
					return (String) value;
				if (value.equals("BRIDGE"))
					return "Bridge Loan";
				if (value.equals("Bridge Loan"))
					return (String) value;
				if (value.equals("LOFC"))
					return "Letter Of Credit";
				if (value.equals("Letter Of Credit"))
					return (String) value;
				if (value.equals("SWING"))
					return "Swing Line Facility";
				if (value.equals("Swing Line Facility"))
					return (String) value;
				if (value.equals("DINP"))
					return "Debtor In Possession";
				if (value.equals("Debtor In Possession"))
					return (String) value;
				if (value.equals("DEFLTED"))
					return "Defaulted";
				if (value.equals("Defaulted"))
					return (String) value;
				if (value.equals("WITHDRN"))
					return "Withdrawn";
				if (value.equals("Withdrawn"))
					return (String) value;
				if (value.equals("REPLACD"))
					return "Replaced";
				if (value.equals("Replaced"))
					return (String) value;
				if (value.equals("MATURED"))
					return "Matured";
				if (value.equals("Matured"))
					return (String) value;
				if (value.equals("AMENDED"))
					return "Amended and Restated";
				if (value.equals("Amended and Restated"))
					return (String) value;
				if (value.equals("RETIRED"))
					return "Retired";
				if (value.equals("Retired"))
					return (String) value;
				if (value.equals("BA"))
					return "Bankers Acceptance";
				if (value.equals("Bankers Acceptance"))
					return (String) value;
				if (value.equals("BDN"))
					return "Bank Depository Note";
				if (value.equals("Bank Depository Note"))
					return (String) value;
				if (value.equals("BN"))
					return "Bank Notes";
				if (value.equals("Bank Notes"))
					return (String) value;
				if (value.equals("BOX"))
					return "Bill Of Exchanges";
				if (value.equals("Bill Of Exchanges"))
					return (String) value;
				if (value.equals("CAMM"))
					return "Canadian Money Markets";
				if (value.equals("Canadian Money Markets"))
					return (String) value;
				if (value.equals("CD"))
					return "Certificate Of Deposit";
				if (value.equals("Certificate Of Deposit"))
					return (String) value;
				if (value.equals("CL"))
					return "Call Loans";
				if (value.equals("Call Loans"))
					return (String) value;
				if (value.equals("CP"))
					return "Commercial Paper";
				if (value.equals("Commercial Paper"))
					return (String) value;
				if (value.equals("DN"))
					return "Deposit Notes";
				if (value.equals("Deposit Notes"))
					return (String) value;
				if (value.equals("EUCD"))
					return "Euro Certificate Of Deposit";
				if (value.equals("Euro Certificate Of Deposit"))
					return (String) value;
				if (value.equals("EUCP"))
					return "Euro Commercial Paper";
				if (value.equals("Euro Commercial Paper"))
					return (String) value;
				if (value.equals("LQN"))
					return "Liquidity Note";
				if (value.equals("Liquidity Note"))
					return (String) value;
				if (value.equals("MTN"))
					return "Medium Term Notes";
				if (value.equals("Medium Term Notes"))
					return (String) value;
				if (value.equals("ONITE"))
					return "Overnight";
				if (value.equals("Overnight"))
					return (String) value;
				if (value.equals("PN"))
					return "Promissory Note";
				if (value.equals("Promissory Note"))
					return (String) value;
				if (value.equals("PZFJ"))
					return "Plazos Fijos";
				if (value.equals("Plazos Fijos"))
					return (String) value;
				if (value.equals("STN"))
					return "Short Term Loan Note";
				if (value.equals("Short Term Loan Note"))
					return (String) value;
				if (value.equals("SLQN"))
					return "Secured Liquidity Note";
				if (value.equals("Secured Liquidity Note"))
					return (String) value;
				if (value.equals("TD"))
					return "Time Deposit";
				if (value.equals("Time Deposit"))
					return (String) value;
				if (value.equals("TLQN"))
					return "Term Liquidity Note";
				if (value.equals("Term Liquidity Note"))
					return (String) value;
				if (value.equals("XCN"))
					return "Extended Comm Note";
				if (value.equals("Extended Comm Note"))
					return (String) value;
				if (value.equals("YCD"))
					return "Yankee Certificate Of Deposit";
				if (value.equals("Yankee Certificate Of Deposit"))
					return (String) value;
				if (value.equals("ABS"))
					return "Asset-backed Securities";
				if (value.equals("Asset-backed Securities"))
					return (String) value;
				if (value.equals("CMB"))
					return "Canadian Mortgage Bonds";
				if (value.equals("Canadian Mortgage Bonds"))
					return (String) value;
				if (value.equals("CMBS"))
					return "Corp. Mortgage-backed Securities";
				if (value.equals("Corp. Mortgage-backed Securities"))
					return (String) value;
				if (value.equals("CMO"))
					return "Collateralized Mortgage Obligation";
				if (value.equals("Collateralized Mortgage Obligation"))
					return (String) value;
				if (value.equals("IET"))
					return "IOETTE Mortgage";
				if (value.equals("IOETTE Mortgage"))
					return (String) value;
				if (value.equals("MBS"))
					return "Mortgage-backed Securities";
				if (value.equals("Mortgage-backed Securities"))
					return (String) value;
				if (value.equals("MIO"))
					return "Mortgage Interest Only";
				if (value.equals("Mortgage Interest Only"))
					return (String) value;
				if (value.equals("MPO"))
					return "Mortgage Principal Only";
				if (value.equals("Mortgage Principal Only"))
					return (String) value;
				if (value.equals("MPP"))
					return "Mortgage Private Placement";
				if (value.equals("Mortgage Private Placement"))
					return (String) value;
				if (value.equals("MPT"))
					return "Miscellaneous Pass-through";
				if (value.equals("Miscellaneous Pass-through"))
					return (String) value;
				if (value.equals("PFAND"))
					return "Pfandbriefe";
				if (value.equals("Pfandbriefe"))
					return (String) value;
				if (value.equals("TBA"))
					return "To Be Announced";
				if (value.equals("To Be Announced"))
					return (String) value;
				if (value.equals("AN"))
					return "Other Anticipation Notes";
				if (value.equals("Other Anticipation Notes"))
					return (String) value;
				if (value.equals("COFO"))
					return "Certificate Of Obligation";
				if (value.equals("Certificate Of Obligation"))
					return (String) value;
				if (value.equals("COFP"))
					return "Certificate Of Participation";
				if (value.equals("Certificate Of Participation"))
					return (String) value;
				if (value.equals("GO"))
					return "General Obligation Bonds";
				if (value.equals("General Obligation Bonds"))
					return (String) value;
				if (value.equals("MT"))
					return "Mandatory Tender";
				if (value.equals("Mandatory Tender"))
					return (String) value;
				if (value.equals("RAN"))
					return "Revenue Anticipation Note";
				if (value.equals("Revenue Anticipation Note"))
					return (String) value;
				if (value.equals("REV"))
					return "Revenue Bonds";
				if (value.equals("Revenue Bonds"))
					return (String) value;
				if (value.equals("SPCLA"))
					return "Special Assessment";
				if (value.equals("Special Assessment"))
					return (String) value;
				if (value.equals("SPCLO"))
					return "Special Obligation";
				if (value.equals("Special Obligation"))
					return (String) value;
				if (value.equals("SPCLT"))
					return "Special Tax";
				if (value.equals("Special Tax"))
					return (String) value;
				if (value.equals("TAN"))
					return "Tax Anticipation Note";
				if (value.equals("Tax Anticipation Note"))
					return (String) value;
				if (value.equals("TAXA"))
					return "Tax Allocation";
				if (value.equals("Tax Allocation"))
					return (String) value;
				if (value.equals("TECP"))
					return "Tax Exempt Commercial Paper";
				if (value.equals("Tax Exempt Commercial Paper"))
					return (String) value;
				if (value.equals("TMCP"))
					return "Taxable Municipal CP";
				if (value.equals("Taxable Municipal CP"))
					return (String) value;
				if (value.equals("TRAN"))
					return "Tax Revenue Anticipation Note";
				if (value.equals("Tax Revenue Anticipation Note"))
					return (String) value;
				if (value.equals("VRDN"))
					return "Variable Rate Demand Note";
				if (value.equals("Variable Rate Demand Note"))
					return (String) value;
				if (value.equals("WAR"))
					return "Warrant";
				if (value.equals("Warrant"))
					return (String) value;
				if (value.equals("MF"))
					return "Mutual Fund";
				if (value.equals("Mutual Fund"))
					return (String) value;
				if (value.equals("MLEG"))
					return "Multileg Instrument";
				if (value.equals("Multileg Instrument"))
					return (String) value;
				if (value.equals("NONE"))
					return "No Security Type";
				if (value.equals("No Security Type"))
					return (String) value;
				if (value.equals("CASH"))
					return "Cash";
				if (value.equals("Cash"))
					return (String) value;
				if (value.equals("CDS"))
					return "Credit Default Swap";
				if (value.equals("Credit Default Swap"))
					return (String) value;
				if (value.equals("FUT"))
					return "Future";
				if (value.equals("Future"))
					return (String) value;
				if (value.equals("OPT"))
					return "Option";
				if (value.equals("Option"))
					return (String) value;
				if (value.equals("OOF"))
					return "Options on Futures";
				if (value.equals("Options on Futures"))
					return (String) value;
				if (value.equals("OOP"))
					return "Options on Physical";
				if (value.equals("Options on Physical"))
					return (String) value;
				if (value.equals("IRS"))
					return "Interest Rate Swap";
				if (value.equals("Interest Rate Swap"))
					return (String) value;
				if (value.equals("OOC"))
					return "Options on Combo";
				if (value.equals("Options on Combo"))
					return (String) value;
			}
		}

		else if (column.equals("Security Identifier Source") || column.equals("Security Alternative Identifier Source")) {
			if (value instanceof String) {
				if (value.equals("1"))
					return "CUSIP";
				if (value.equals("CUSIP"))
					return (String) value;
				if (value.equals("2"))
					return "SEDOL";
				if (value.equals("SEDOL"))
					return (String) value;
				if (value.equals("3"))
					return "QUIK";
				if (value.equals("QUIK"))
					return (String) value;
				if (value.equals("4"))
					return "ISIN number";
				if (value.equals("ISIN number"))
					return (String) value;
				if (value.equals("5"))
					return "RIC code";
				if (value.equals("RIC code"))
					return (String) value;
				if (value.equals("6"))
					return "ISO Currency Code";
				if (value.equals("ISO Currency Code"))
					return (String) value;
				if (value.equals("7"))
					return "ISO Country Code";
				if (value.equals("ISO Country Code"))
					return (String) value;
				if (value.equals("8"))
					return "Exchange Symbol";
				if (value.equals("Exchange Symbol"))
					return (String) value;
				if (value.equals("9"))
					return "Consolidated Tape Association";
				if (value.equals("Consolidated Tape Association"))
					return (String) value;

				if (value.equals("A"))
					return "Bloomberg Symbol";
				if (value.equals("Bloomberg Symbol"))
					return (String) value;
				if (value.equals("B"))
					return "Wertpapier";
				if (value.equals("Wertpapier"))
					return (String) value;
				if (value.equals("C"))
					return "Dutch";
				if (value.equals("Dutch"))
					return (String) value;
				if (value.equals("D"))
					return "Valoren";
				if (value.equals("Valoren"))
					return (String) value;
				if (value.equals("E"))
					return "Sicovam";
				if (value.equals("Sicovam"))
					return (String) value;
				if (value.equals("F"))
					return "Belgian";
				if (value.equals("Belgian"))
					return (String) value;
				if (value.equals("G"))
					return "Common";
				if (value.equals("Common"))
					return (String) value;
				if (value.equals("H"))
					return "Clearing House";
				if (value.equals("Clearing House"))
					return (String) value;
				if (value.equals("I"))
					return "ISDA/FpML Product Specification";
				if (value.equals("ISDA/FpML Product Specification"))
					return (String) value;
				if (value.equals("J"))
					return "Option Price Reporting Authority";
				if (value.equals("Option Price Reporting Authority"))
					return (String) value;
				if (value.equals("K"))
					return "ISDA/FpML Product URL";
				if (value.equals("ISDA/FpML Product URL"))
					return (String) value;
				if (value.equals("L"))
					return "Letter of Credit";
				if (value.equals("Letter of Credit"))
					return (String) value;
				if (value.equals("M"))
					return "Marketplace-assigned Identifier";
				if (value.equals("Marketplace-assigned Identifier"))
					return (String) value;

			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "CUSIP";
				if (((Double) value).doubleValue() == 2)
					return "SEDOL";
				if (((Double) value).doubleValue() == 3)
					return "QUIK";
				if (((Double) value).doubleValue() == 4)
					return "ISIN number";
				if (((Double) value).doubleValue() == 5)
					return "RIC code";
				if (((Double) value).doubleValue() == 6)
					return "ISO Currency Code";
				if (((Double) value).doubleValue() == 7)
					return "ISO Country Code";
				if (((Double) value).doubleValue() == 8)
					return "Exchange Symbol";
				if (((Double) value).doubleValue() == 9)
					return "Consolidated Tape Association";
			}
		}

		else if (column.equals("Put or Call")) {
			if (value instanceof String) {
				if (value.equals("Put"))
					return (String) value;
				if (value.equals("Call"))
					return (String) value;
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return "Put";
				if (((Double) value).doubleValue() == 1)
					return "Call";
			}
		}

		else if (column.equals("Country of Issue")) {
			Locale[] localeList = NumberFormat.getAvailableLocales();
			for (Locale locale : localeList) {
				if (value.equals(locale.getCountry()))
					return (locale.getDisplayCountry());
				if (value.equals(locale.getDisplayCountry()))
					return (String) value;
			}
		}
		
		else if (column.equals("Contract Multiplier Unit")) {
			if (value instanceof String) {
				if (value.equals("Shares"))
					return (String) value;
				if (value.equals("Hours"))
					return (String) value;
				if (value.equals("Days"))
					return (String) value;
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return "Shares";
				if (((Double) value).doubleValue() == 1)
					return "Hours";
				if (((Double) value).doubleValue() == 2)
					return "Days";
			}
		}
		
		else if (column.equals("Unit of Measure")||column.equals("Price Unit of Measure")) {
			if (value instanceof String) {
				if (value.equals("Billion cubic feet"))
					return (String) value;
				if (value.equals("Million Barrels"))
					return (String) value;
				if (value.equals("One Million BTU"))
					return (String) value;
				if (value.equals("Megawatt hours"))
					return (String) value;
				if (value.equals("Barrels"))
					return (String) value;
				if (value.equals("Bushels"))
					return (String) value;
				if (value.equals("Pounds"))
					return (String) value;
				if (value.equals("Gallons"))
					return (String) value;
				if (value.equals("Troy Ounces"))
					return (String) value;
				if (value.equals("Metric Tons"))
					return (String) value;
				if (value.equals("Tons (US)"))
					return (String) value;
				if (value.equals("US Dollars"))
					return (String) value;
				if (value.equals("Allowances"))
					return (String) value;

				if (value.equals("Bcf"))
					return "Billion cubic feet";
				if (value.equals("MMbbl"))
					return "Million Barrels";
				if (value.equals("MMBtu"))
					return "One Million BTU";
				if (value.equals("MWh"))
					return "Megawatt hours";
				if (value.equals("Bbl"))
					return "Barrels";
				if (value.equals("Bu"))
					return "Bushels";
				if (value.equals("lbs"))
					return "Pounds";
				if (value.equals("Gal"))
					return "Gallons";
				if (value.equals("oz_tr"))
					return "Troy Ounces";
				if (value.equals("t"))
					return "Metric Tons";
				if (value.equals("tn"))
					return "Tons (US)";
				if (value.equals("USD"))
					return "US Dollars";
				if (value.equals("Alw"))
					return "Allowances";
			}
		}
		
		else if (column.equals("Time Unit")) {
			if (value instanceof String) {
				if (value.equals("Hour"))
					return (String) value;
				if (value.equals("Minute"))
					return (String) value;
				if (value.equals("Second"))
					return (String) value;
				if (value.equals("Day"))
					return (String) value;
				if (value.equals("Week"))
					return (String) value;
				if (value.equals("Month"))
					return (String) value;
				if (value.equals("Year"))
					return (String) value;
				
				if (value.equals("H"))
					return "Hour";
				if (value.equals("Min"))
					return "Minute";
				if (value.equals("S"))
					return "Second";
				if (value.equals("D"))
					return "Day";
				if (value.equals("Wk"))
					return "Week";
				if (value.equals("Mo"))
					return "Month";
				if (value.equals("Yr"))
					return "Year";

			}
		}
		
		else if (column.equals("Strike Price Determination Method")) {
			if (value instanceof String) {
				if (value.equals("Fixed Strike"))
					return (String) value;
				if (value.equals("Strike set at expiration"))
					return (String) value;
				if (value.equals("Strike set to average across life"))
					return (String) value;
				if (value.equals("Strike set to optimal value"))
					return (String) value;
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Fixed Strike";
				if (((Double) value).doubleValue() == 2)
					return "Strike set at expiration";
				if (((Double) value).doubleValue() == 3)
					return "Strike set to average across life";
				if (((Double) value).doubleValue() == 4)
					return "Strike set to optimal value";
			}
		}
		
		else if (column.equals("Strike Price Boundary Method")) {
			if (value instanceof String) {
				if (value.equals("Less than"))
					return (String) value;
				if (value.equals("Less than or equal"))
					return (String) value;
				if (value.equals("Equal"))
					return (String) value;
				if (value.equals("Greater than or equal"))
					return (String) value;
				if (value.equals("Greater than"))
					return (String) value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Less than";
				if (((Double) value).doubleValue() == 2)
					return "Less than or equal";
				if (((Double) value).doubleValue() == 3)
					return "Equal";
				if (((Double) value).doubleValue() == 4)
					return "Greater than or equal";
				if (((Double) value).doubleValue() == 5)
					return "Greater than";
			}
		}
		
		else if (column.equals("Security Exchange")) {
			if (value instanceof String) {
				ExchangeComboBoxEntries exchangeComboBoxEntries = new ExchangeComboBoxEntries();
				for(ComboBoxEntry comboBoxEntry: exchangeComboBoxEntries.getComboBoxEntries())
				{
					if(comboBoxEntry.toString().equals(value))
						return comboBoxEntry.toString();
					if(comboBoxEntry.getEntry().equals(value))
						return comboBoxEntry.toString();
				}
			}
		}
		
		else if (column.equals("Commercial Paper Programm")) {
			if (value instanceof String) {
				
				if (value.equals("3(a)(3)"))
					return (String) value;
				if (value.equals("4(2)"))
					return (String) value;
				if (value.equals("Other"))
					return (String) value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "3(a)(3)";
				if (((Double) value).doubleValue() == 2)
					return "4(2)";
				if (((Double) value).doubleValue() == 99)
					return "Other";
			}
		}
		
		else if (column.equals("Security Status")) {
			if (value instanceof String) {
				
				if (value.equals("Active"))
					return (String)value;
				if (value.equals("Inactive"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Active";
				if (((Double) value).doubleValue() == 2)
					return "Inactive";
			}
		}
		
		else if (column.equals("Settle on Open")) {
			if (value instanceof String) {
				
				if (value.equals("Yes"))
					return (String)value;
				if (value.equals("No"))
					return (String)value;
				if (value.equals("Y"))
					return "Yes";
				if (value.equals("N"))
					return "No";
			}
			
		}

		else if (column.equals("Instrument Assignment Method")) {
			if (value instanceof String) {
				
				if (value.equals("Pro rata"))
					return (String)value;
				if (value.equals("Random"))
					return (String)value;
				if (value.equals("P"))
					return "Pro rata";
				if (value.equals("R"))
					return "Random";
			}
			
		}
		
		else if (column.equals("Underlying Price Determination Method")) {
			if (value instanceof String) {
				
				if (value.equals("Regular"))
					return (String)value;
				if (value.equals("Special reference"))
					return (String)value;
				if (value.equals("Optimal value"))
					return (String)value;
				if (value.equals("Average value"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Regular";
				if (((Double) value).doubleValue() == 2)
					return "Special reference";
				if (((Double) value).doubleValue() == 3)
					return "Optimal value";
				if (((Double) value).doubleValue() == 4)
					return "Average value";
			}
		}
		
		else if (column.equals("Delivery Form")) {
			if (value instanceof String) {
				
				if (value.equals("Book Entry"))
					return (String)value;
				if (value.equals("Bearer"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Book Entry";
				if (((Double) value).doubleValue() == 2)
					return "Bearer";
			}
		}
		
		else if (column.equals("Settlement Method")) {
			if (value instanceof String) {
				
				if (value.equals("Cash settlement required"))
					return (String)value;
				if (value.equals("Physical settlement required"))
					return (String)value;
				if (value.equals("C"))
					return "Cash settlement required";
				if (value.equals("P"))
					return "Physical settlement required";
			}
		}
		
		else if (column.equals("Exercise Style")) {
			if (value instanceof String) {
				
				if (value.equals("European"))
					return (String)value;
				if (value.equals("American"))
					return (String)value;
				if (value.equals("Bermuda"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return "European";
				if (((Double) value).doubleValue() == 1)
					return "American";
				if (((Double) value).doubleValue() == 2)
					return "Bermuda";
			}
		}
		
		else if (column.equals("Option Payout Type")) {
			if (value instanceof String) {
				
				if (value.equals("Vanilla"))
					return (String)value;
				if (value.equals("Capped"))
					return (String)value;
				if (value.equals("Binary"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Vanilla";
				if (((Double) value).doubleValue() == 2)
					return "Capped";
				if (((Double) value).doubleValue() == 3)
					return "Binary";
			}
		}
		
		else if (column.equals("Price Quote Method")) {
			if (value instanceof String) {
				
				if (value.equals("Standard, money per unit of a physical"))
					return (String)value;
				if (value.equals("Index"))
					return (String)value;
				if (value.equals("Interest rate Index"))
					return (String)value;
				if (value.equals("Percent of Par"))
					return (String)value;
				if (value.equals("STD"))
					return "Standard, money per unit of a physical";
				if (value.equals("INX"))
					return "Index";
				if (value.equals("INT"))
					return "Interest rate Index";
				if (value.equals("PCTPAR"))
					return "Percent of Par";
			}
		}
		
		else if (column.equals("List Method")) {
			if (value instanceof String) {
				
				if (value.equals("pre-listed only"))
					return (String)value;
				if (value.equals("user requested"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return "pre-listed only";
				if (((Double) value).doubleValue() == 1)
					return "user requested";
			}
		}
		
		else if (column.equals("Flexible Indicator")||column.equals("Flexible Product Eligibility Indicator")) {
			if (value instanceof String) {
				
				if (value.equals("Flexible"))
					return (String)value;
				if (value.equals("Not flexible"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return  "Not flexible";
				if (((Double) value).doubleValue() == 1)
					return "Flexible";
			}
			
			if (value instanceof Boolean) {
				if((Boolean)value)
					return "Flexible";
				else
					return  "Not flexible";
			}
		}
		
		else if (column.equals("Valuation Method")) {
			if (value instanceof String) {
				
				if (value.equals( "Premium style"))
					return (String)value;
				if (value.equals("Futures style mark-to-market"))
					return (String)value;
				if (value.equals("Futures style with an attached cash adjustment"))
					return (String)value;
				if (value.equals("CDS style collateralization"))
					return (String)value;
				if (value.equals("CDS in delivery - use recovery rate to calculate obligation"))
					return (String)value;
				
				if (value.equals("EQTY"))
					return  "Premium style";
				if (value.equals("FUT"))
					return "Futures style mark-to-market";
				if (value.equals("FUTDA"))
					return "Futures style with an attached cash adjustment";
				if (value.equals("CDS"))
					return "CDS style collateralization";
				if (value.equals("CDSD"))
					return "CDS in delivery - use recovery rate to calculate obligation";
			}
		}
		
		else if (column.equals("Flow Schedule Type")) {
			if (value instanceof String) {
				
				if (value.equals("NERC Eastern Off-Peak"))
					return (String)value;
				if (value.equals("NERC Western Off-Peak"))
					return (String)value;
				if (value.equals("NERC Calendar-All Days in month"))
					return (String)value;
				if (value.equals("NERC Eastern Peak"))
					return (String)value;
				if (value.equals("NERC Western Peak"))
					return (String)value;

			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 0)
					return "NERC Eastern Off-Peak";
				if (((Double) value).doubleValue() == 1)
					return "NERC Western Off-Peak";
				if (((Double) value).doubleValue() == 2)
					return "NERC Calendar-All Days in month";
				if (((Double) value).doubleValue() == 3)
					return "NERC Eastern Peak";
				if (((Double) value).doubleValue() == 4)
					return "NERC Western Peak";

			}
		}
		
		else if (column.equals("Restructuring Type")) {
			if (value instanceof String) {
				
				if (value.equals("Full Restructuring"))
					return (String)value;
				if (value.equals("Modified Restructuring"))
					return (String)value;
				if (value.equals("Modified Mod Restructuring"))
					return (String)value;
				if (value.equals("No Restructuring specified"))
					return (String)value;
				
				if (value.equals("FR"))
					return "Full Restructuring";
				if (value.equals("MR"))
					return "Modified Restructuring";
				if (value.equals("MM"))
					return "Modified Mod Restructuring";
				if (value.equals("XR"))
					return "No Restructuring specified";
				
			}
			
		}
		
		else if (column.equals("Seniority")) {
			if (value instanceof String) {
				
				if (value.equals("Senior Secured"))
					return (String)value;
				if (value.equals("Senior"))
					return (String)value;
				if (value.equals("Subordinated"))
					return (String)value;
				
				if (value.equals("SD"))
					return "Senior Secured";
				if (value.equals("SR"))
					return "Senior";
				if (value.equals("SB"))
					return "Subordinated";

			}
			
		}
		
		else if (column.equals("Event Type")) {
			if (value instanceof String) {
				
				if (value.equals("Put"))
					return (String)value;
				if (value.equals("Call"))
					return (String)value;
				if (value.equals("Tender"))
					return (String)value;
				if (value.equals("Sinking Fund Call"))
					return (String)value;
				if (value.equals("Activation"))
					return (String)value;
				if (value.equals("Inactiviation"))
					return (String)value;
				if (value.equals("Last Eligible Trade Date"))
					return (String)value;
				if (value.equals("Swap Start Date"))
					return (String)value;
				if (value.equals("Swap End Date"))
					return (String)value;
				if (value.equals("Swap Roll Date"))
					return (String)value;
				if (value.equals("Swap Next Start Date"))
					return (String)value;
				if (value.equals("Swap Next Roll Date"))
					return (String)value;
				if (value.equals("First Delivery Date"))
					return (String)value;
				if (value.equals("Last Delivery Date"))
					return (String)value;
				if (value.equals("Initial Inventory Due Date"))
					return (String)value;
				if (value.equals("Final Inventory Due Date"))
					return (String)value;
				if (value.equals("First Intent Date"))
					return (String)value;
				if (value.equals("Last Intent Date"))
					return (String)value;
				if (value.equals("Position Removal Date"))
					return (String)value;
				if (value.equals("Other"))
					return (String)value;

			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Put";
				if (((Double) value).doubleValue() == 2)
					return "Call";
				if (((Double) value).doubleValue() == 3)
					return "Tender";
				if (((Double) value).doubleValue() == 4)
					return "Sinking Fund Call";
				if (((Double) value).doubleValue() == 5)
					return "Activation";
				if (((Double) value).doubleValue() == 6)
					return "Inactiviation";
				if (((Double) value).doubleValue() == 7)
					return "Last Eligible Trade Date";
				if (((Double) value).doubleValue() == 8)
					return "Swap Start Date";
				if (((Double) value).doubleValue() == 9)
					return "Swap End Date";
				if (((Double) value).doubleValue() == 10)
					return "Swap Roll Date";
				if (((Double) value).doubleValue() == 11)
					return "Swap Next Start Date";
				if (((Double) value).doubleValue() == 12)
					return "Swap Next Roll Date";
				if (((Double) value).doubleValue() == 13)
					return "First Delivery Date";
				if (((Double) value).doubleValue() == 14)
					return "Last Delivery Date";
				if (((Double) value).doubleValue() == 15)
					return "Initial Inventory Due Date";
				if (((Double) value).doubleValue() == 16)
					return "Final Inventory Due Date";
				if (((Double) value).doubleValue() == 17)
					return "First Intent Date";
				if (((Double) value).doubleValue() == 18)
					return "Last Intent Date";
				if (((Double) value).doubleValue() == 19)
					return "Position Removal Date";
				if (((Double) value).doubleValue() == 99)
					return "Other";

			}
		}
		
		else if (column.equals("Complex Event Type")) {
			if (value instanceof String) {
				
				if (value.equals("Capped"))
					return (String)value;
				if (value.equals("Trigger"))
					return (String)value;
				if (value.equals("Knock-in up"))
					return (String)value;
				if (value.equals("Kock-in down"))
					return (String)value;
				if (value.equals("Knock-out up"))
					return (String)value;
				if (value.equals("Knock-out down"))
					return (String)value;
				if (value.equals("Underlying"))
					return (String)value;
				if (value.equals("Reset Barrier"))
					return (String)value;
				if (value.equals("Rolling Barrier"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Capped";
				if (((Double) value).doubleValue() == 2)
					return "Trigger";
				if (((Double) value).doubleValue() == 3)
					return "Knock-in up";
				if (((Double) value).doubleValue() == 4)
					return "Kock-in down";
				if (((Double) value).doubleValue() == 5)
					return "Knock-out up";
				if (((Double) value).doubleValue() == 6)
					return "Knock-out down";
				if (((Double) value).doubleValue() == 7)
					return "Underlying";
				if (((Double) value).doubleValue() == 8)
					return "Reset Barrier";
				if (((Double) value).doubleValue() == 9)
					return "Rolling Barrier";
			}
		}
		
		else if (column.equals("Complex Event Price Boundary Method")) {
			if (value instanceof String) {
				
				if (value.equals("Less than complex event price"))
					return (String)value;
				if (value.equals("Less than or equal to complex event price"))
					return (String)value;
				if (value.equals("Equal to complex event price"))
					return (String)value;
				if (value.equals("Greater than or equal to complex event price"))
					return (String)value;
				if (value.equals("Greater than complex event price"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Less than complex event price";
				if (((Double) value).doubleValue() == 2)
					return "Less than or equal to complex event price";
				if (((Double) value).doubleValue() == 3)
					return "Equal to complex event price";
				if (((Double) value).doubleValue() == 4)
					return "Greater than or equal to complex event price";
				if (((Double) value).doubleValue() == 5)
					return "Greater than complex event price";
			}
		}
		
		else if (column.equals("Complex Event Price Time Type")) {
			if (value instanceof String) {
				
				if (value.equals("Expiration"))
					return (String)value;
				if (value.equals("Immediate"))
					return (String)value;
				if (value.equals("Specified Date"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Expiration";
				if (((Double) value).doubleValue() == 2)
					return "Immediate";
				if (((Double) value).doubleValue() == 3)
					return "Specified Date";
			}
		}
		
		else if (column.equals("Complex Event Condition")) {
			if (value instanceof String) {
				
				if (value.equals("And"))
					return (String)value;
				if (value.equals("Or"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "And";
				if (((Double) value).doubleValue() == 2)
					return "Or";
			}
		}
		
		else if (column.equals("Underlying Settlement Type")) {
			if (value instanceof String) {
				
				if (value.equals("T+1"))
					return (String)value;
				if (value.equals("T+3"))
					return (String)value;
				if (value.equals("T+4"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 2)
					return "T+1";
				if (((Double) value).doubleValue() == 4)
					return "T+3";
				if (((Double) value).doubleValue() == 5)
					return "T+4";
			}
		}
		
		else if (column.equals("Underlying Cash Type")) {
			if (value instanceof String) {				
				if (value.equals("FIXED"))
					return (String)value;
				if (value.equals("DIFF"))
					return (String)value;
			}
		}
		
		else if (column.equals("Leg Side")) {
			if (value instanceof String) {
				
				if (value.equals("A"))
					return "Cross short exempt";
				if (value.equals("B"))
					return "As Defined";
				if (value.equals("C"))
					return "Opposite";
				if (value.equals("D"))
					return "Subscribe";
				if (value.equals("E"))
					return "Redeem";
				if (value.equals("F"))
					return "Lend";
				if (value.equals("G"))
					return "Borrow";
				
				if (value.equals("Buy"))
					return (String)value;
				if (value.equals("Sell"))
					return (String)value;
				if (value.equals("Buy minus"))
					return (String)value;
				if (value.equals("Sell plus"))
					return (String)value;
				if (value.equals("Sell short"))
					return (String)value;
				if (value.equals("Sell short exempt"))
					return (String)value;
				if (value.equals("Undisclosed"))
					return (String)value;
				if (value.equals("Cross"))
					return (String)value;
				if (value.equals("Cross short"))
					return (String)value;
				if (value.equals("Cross short exempt"))
					return (String)value;
				if (value.equals("As Defined"))
					return (String)value;
				if (value.equals("Opposite"))
					return (String)value;
				if (value.equals("Subscribe"))
					return (String)value;
				if (value.equals("Redeem"))
					return (String)value;
				if (value.equals("Lend"))
					return (String)value;
				if (value.equals("Borrow"))
					return (String)value;
			}
			
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Buy";
				if (((Double) value).doubleValue() == 2)
					return "Sell";
				if (((Double) value).doubleValue() == 3)
					return "Buy minus";
				if (((Double) value).doubleValue() == 4)
					return "Sell plus";
				if (((Double) value).doubleValue() == 5)
					return "Sell short";
				if (((Double) value).doubleValue() == 6)
					return "Sell short exempt";
				if (((Double) value).doubleValue() == 7)
					return "Undisclosed";
				if (((Double) value).doubleValue() == 8)
					return "Cross";
				if (((Double) value).doubleValue() == 9)
					return "Sell short";
			}
			
		}	
		
		else if (column.equals("Attribute Type")) {
			if (value instanceof String) {
				if (value.equals("Flat"))
					return (String) value;
				if (value.equals("Zero coupon"))
					return (String) value;
				if (value.equals("Interest bearing"))
					return (String) value;
				if (value.equals("No periodic payments"))
					return (String) value;
				if (value.equals("Variable rate"))
					return (String) value;
				if (value.equals("Less fee for put"))
					return (String) value;
				if (value.equals("Stepped coupon"))
					return (String) value;
				if (value.equals("Coupon period"))
					return (String) value;
				if (value.equals("When (and if) issued"))
					return (String) value;
				if (value.equals("Original issue discount"))
					return (String) value;
				if (value.equals("Callable, puttable"))
					return (String) value;
				if (value.equals("Escrowed to Maturity"))
					return (String) value;
				if (value.equals("Escrowed to redemption date"))
					return (String) value;
				if (value.equals("Pre-refunded"))
					return (String) value;
				if (value.equals("In default"))
					return (String) value;
				if (value.equals("Unrated"))
					return (String) value;
				if (value.equals("Taxable"))
					return (String) value;
				if (value.equals("Indexed"))
					return (String) value;
				if (value.equals("Subject To Alternative Minimum Tax"))
					return (String) value;
				if (value.equals("Original issue discount price"))
					return (String) value;
				if (value.equals("Callable below maturity value"))
					return (String) value;
				if (value.equals("Callable without notice"))
					return (String) value;
				if (value.equals("Price tick rules for security"))
					return (String) value;
				if (value.equals("Trade type eligibility details for security"))
					return (String) value;
				if (value.equals("Instrument Denominator"))
					return (String) value;
				if (value.equals("Instrument Numerator"))
					return (String) value;
				if (value.equals("Instrument Price Precision"))
					return (String) value;
				if (value.equals("Instrument Strike Price"))
					return (String) value;
				if (value.equals("Tradeable Indicator"))
					return (String) value;
				if (value.equals("Text"))
					return (String) value;
			}
			if (value instanceof Double) {
				if (((Double) value).doubleValue() == 1)
					return "Flat";
				if (((Double) value).doubleValue() == 2)
					return "Zero coupon";
				if (((Double) value).doubleValue() == 3)
					return "Interest bearing";
				if (((Double) value).doubleValue() == 4)
					return "No periodic payments";
				if (((Double) value).doubleValue() == 5)
					return "Variable rate";
				if (((Double) value).doubleValue() == 6)
					return "Less fee for put";
				if (((Double) value).doubleValue() == 7)
					return "Stepped coupon";
				if (((Double) value).doubleValue() == 8)
					return "Coupon period";
				if (((Double) value).doubleValue() == 9)
					return "When (and if) issued";
				if (((Double) value).doubleValue() == 10)
					return "Original issue discount";
				if (((Double) value).doubleValue() == 11)
					return "Callable, puttable";
				if (((Double) value).doubleValue() == 12)
					return "Escrowed to Maturity";
				if (((Double) value).doubleValue() == 13)
					return "Escrowed to redemption date";
				if (((Double) value).doubleValue() == 14)
					return "Pre-refunded";
				if (((Double) value).doubleValue() == 15)
					return "In default";
				if (((Double) value).doubleValue() == 16)
					return "Unrated";
				if (((Double) value).doubleValue() == 17)
					return "Taxable";
				if (((Double) value).doubleValue() == 18)
					return "Indexed";
				if (((Double) value).doubleValue() == 19)
					return "Subject To Alternative Minimum Tax";
				if (((Double) value).doubleValue() == 20)
					return "Original issue discount price";
				if (((Double) value).doubleValue() == 21)
					return "Callable below maturity value";
				if (((Double) value).doubleValue() == 22)
					return "Callable without notice";
				if (((Double) value).doubleValue() == 23)
					return "Price tick rules for security";
				if (((Double) value).doubleValue() == 24)
					return "Trade type eligibility details for security";
				if (((Double) value).doubleValue() == 25)
					return "Instrument Denominator";
				if (((Double) value).doubleValue() == 26)
					return "Instrument Numerator";
				if (((Double) value).doubleValue() == 27)
					return "Instrument Price Precision";
				if (((Double) value).doubleValue() == 28)
					return "Instrument Strike Price";
				if (((Double) value).doubleValue() == 29)
					return "Tradeable Indicator";
				if (((Double) value).doubleValue() == 99)
					return "Text";
			}
		}

		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Boolean) {
			if ((Boolean) value)
				return "TRUE";
			return "FALSE";
		}
		if (value instanceof Double)
			return decimalFormat.format((Double) value);
		if (value instanceof Date)
			return simpleDateFormat.format((Date) value);

		return value.toString();
	}

	/**
	 * Gets the securities.
	 *
	 * @param table the table
	 * @param sheet the sheet
	 * @return the securities
	 */
	public List<UploadEntry> getSecurities(JTable table, Sheet sheet) {

		Map<Integer, String> columnTypes = excelImportTableModel.getColumnTypeMap();

		List<UploadEntry> securities = new ArrayList<UploadEntry>();

		if (sheet != null) {

			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Object value = excelImportTableModel.getValueAt(i + 1, 0);
					if (value instanceof ExcelImportCell && (((ExcelImportCell) value).getEntryStatus() == EntryStatus.VALID)) {

						SecurityDetails securityDetails = new SecurityDetails();
						securityDetails.getSecurityAltIDGroups();
						securityDetails.getSecurityAttribute();
						securityDetails.getSecurityEvents();
						securityDetails.getSecurityComplexEvents();
						securityDetails.getSecurityEvents();
						FSecurity security = null;
						String symbol = null;
						String securityID = null;
						Date maturityDate = null;
						for (int j = 1; j < table.getColumnCount(); j++) {
							int columnIndex = table.convertColumnIndexToModel(j);
							String columnType = columnTypes.get(columnIndex);
							if (columnType != null) {

								Cell cell = row.getCell(columnIndex - 1);
								Object convertedValue = null;
								if (cell != null) {
									switch (cell.getCellType()) {
										case Cell.CELL_TYPE_STRING:
											convertedValue = convertValue(cell.getStringCellValue(), columnType);
											break;
										case Cell.CELL_TYPE_NUMERIC:
											if (DateUtil.isCellDateFormatted(cell)) {
												convertedValue = convertValue(DateUtil.getJavaDate(cell.getNumericCellValue()), columnType);
											}
											else
												convertedValue = convertValue(cell.getNumericCellValue(), columnType);
											break;
										case Cell.CELL_TYPE_BOOLEAN:
											convertedValue = convertValue(cell.getBooleanCellValue(), columnType);
											break;
									}
								}

								if (columnType.equals("Maturity Date")) {
									maturityDate = (Date) convertedValue;
								}
								else if (columnType.equals("Coupon Payment Date")) {
									securityDetails.setCouponPaymentDate((Date) convertedValue);
								}
								else if (columnType.equals("Issue Date")) {
									securityDetails.setIssueDate((Date) convertedValue);
								}
								else if (columnType.equals("Redemption Date")) {
									securityDetails.setRedemptionDate((Date) convertedValue);
								}
								else if (columnType.equals("Interest Accrual Date")) {
									securityDetails.setInterestAccrualDate((Date) convertedValue);
								}
								else if (columnType.equals("Dated Date")) {
									securityDetails.setDatedDate((Date) convertedValue);
								}
								else if (columnType.equals("Contract Settlement")) {
									securityDetails.setContractSettlMonth((Date) convertedValue);
								}
								else if (columnType.equals("Event Date")) {
									if (securityDetails.getSecurityEvents().size() > 0) {
										securityDetails.getSecurityEvents().get(securityDetails.getSecurityEvents().size() - 1).setEventDate((Date) convertedValue);
									}
								}
								else if (columnType.equals("Complex Event Start Date")) {
									if (securityDetails.getSecurityComplexEvents().size() > 0) {
										SecurityComplexEvent securityComplexEvent = securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size() - 1);
										ComplexEventDate complexEventDate = new ComplexEventDate();
										complexEventDate.setSecurityComplexEvent(securityComplexEvent);
										complexEventDate.setEventStartDate((Date) convertedValue);
										securityComplexEvent.getComplexEventDates().add(complexEventDate);
									}
								}
								else if (columnType.equals("Complex Event End Date")) {
									if (securityDetails.getSecurityComplexEvents().size() > 0) {
										SecurityComplexEvent securityComplexEvent = securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size() - 1);
										if (securityComplexEvent.getComplexEventDates().size() > 0)
											securityComplexEvent.getComplexEventDates().get(securityComplexEvent.getComplexEventDates().size() - 1)
													.setEventEndDate((Date) convertedValue);
									}
								}
								else if (columnType.equals("Complex Event Start Time")) {
									if (securityDetails.getSecurityComplexEvents().size() > 0) {
										SecurityComplexEvent securityComplexEvent = securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size() - 1);
										if (securityComplexEvent.getComplexEventDates().size() > 0)
										{
											ComplexEventDate complexEventDate = securityComplexEvent.getComplexEventDates().get(securityComplexEvent.getComplexEventDates().size() - 1);
											ComplexEventTime complexEventTime = new ComplexEventTime();
											complexEventTime.setComplexEventDate(complexEventDate);
											complexEventTime.setEventStartTime((Date) convertedValue);
										}
									}
								}
								else if (columnType.equals("Complex Event End Time")) {
									if (securityDetails.getSecurityComplexEvents().size() > 0) {
										SecurityComplexEvent securityComplexEvent = securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size() - 1);
										if (securityComplexEvent.getComplexEventDates().size() > 0)
										{
											ComplexEventDate complexEventDate = securityComplexEvent.getComplexEventDates().get(securityComplexEvent.getComplexEventDates().size() - 1);
											if(complexEventDate.getComplexEventTimes().size()>0)
												complexEventDate.getComplexEventTimes().get(complexEventDate.getComplexEventTimes().size()-1).setEventEndTime((Date) convertedValue);
										}
									}
								}
								else if (columnType.equals("Event Time")) {
									if (securityDetails.getSecurityEvents().size() > 0) {
										securityDetails.getSecurityEvents().get(securityDetails.getSecurityEvents().size() - 1).setEventTime((Date) convertedValue);
									}
								}
								else if (columnType.equals("Maturity Time")) {
									securityDetails.setMaturityTime((Date) convertedValue);
								}
								else if (columnType.equals("Symbol")) {
									symbol = (String) convertedValue;
									security = excelImportTableModel.getSymbolMap().get(symbol);
								}
								else if (columnType.equals("Classification of Financial Instrument")) {
									securityDetails.setcFICode((String) convertedValue);
								}
								else if (columnType.equals("Security Sub-type")) {
									securityDetails.setSecuritySubType((String) convertedValue);
								}
								else if (columnType.equals("Security Identifier")) {
									securityID = (String) convertedValue;
								}
								else if (columnType.equals("Security Alternative Identifier")) {
									SecurityAltIDGroup securityAltIDGroup = new SecurityAltIDGroup();
									securityAltIDGroup.setSecurityAltID((String) convertedValue);
									securityAltIDGroup.setSecurity(securityDetails);
									securityDetails.getSecurityAltIDGroups().add(securityAltIDGroup);
								}
								else if (columnType.equals("Issuer")) {
									securityDetails.setIssuer((String) convertedValue);
								}
								else if (columnType.equals("State of Issue")) {
									securityDetails.setStateOfIssue((String) convertedValue);
								}
								else if (columnType.equals("Locale of Issue")) {
									securityDetails.setLocaleOfIssue((String) convertedValue);
								}
								else if (columnType.equals("Credit Rating")) {
									securityDetails.setCreditRating((String) convertedValue);
								}
								else if (columnType.equals("Instrument Registry")) {
									securityDetails.setInstrumentRegistry((String) convertedValue);
								}
								else if (columnType.equals("Description")) {
									securityDetails.setDescription((String) convertedValue);
								}
								else if (columnType.equals("Security Group")) {
									securityDetails.setSecurityGroup((String) convertedValue);
								}
								else if (columnType.equals("Pool")) {
									securityDetails.setFpool((String) convertedValue);
								}
								else if (columnType.equals("Commercial Paper Registration Type")) {
									securityDetails.setCpRegType((String) convertedValue);
								}
								else if (columnType.equals("Product Complex")) {
									securityDetails.setProductComplex((String) convertedValue);
								}
								else if (columnType.equals("Attribute Value")) {
									if(securityDetails.getSecurityAttribute().size()>0)
										securityDetails.getSecurityAttribute().get(securityDetails.getSecurityAttribute().size()-1).setAttributeValue((String) convertedValue);
								}
								else if (columnType.equals("Event Text")) {
									if(securityDetails.getSecurityEvents().size()>0)
										securityDetails.getSecurityEvents().get(securityDetails.getSecurityEvents().size()-1).setEventText((String) convertedValue);
								}
								else if (columnType.equals("Coupon Rate")) {
									securityDetails.setCouponRate((Double) convertedValue);
								}
								else if (columnType.equals("Cap Price")) {
									securityDetails.setCapPrice((Double) convertedValue);
								}
								else if (columnType.equals("Floor Price")) {
									securityDetails.setFloorPrice((Double) convertedValue);
								}
								else if (columnType.equals("Contract Multiplier")) {
									securityDetails.setContractMultiplier((Double) convertedValue);
								}
								else if (columnType.equals("Minimum Price Increment")) {
									securityDetails.setMinPriceIncrement((Double) convertedValue);
								}
								else if (columnType.equals("Minimum Price Increment Amount")) {
									securityDetails.setMinPriceIncrementAmount((Double) convertedValue);
								}
								else if (columnType.equals("Factor")) {
									securityDetails.setFactor((Double) convertedValue);
								}
								else if (columnType.equals("Strike Price")) {
									securityDetails.setStrikePrice((Double) convertedValue);
								}
								else if (columnType.equals("Unit of Measure Quantity")) {
									securityDetails.setUnitOfMeasureQty((Double) convertedValue);
								}
								else if (columnType.equals("Price Unit of Measure Quantity")) {
									securityDetails.setPriceUnitOfMeasureQty((Double) convertedValue);
								}
								else if (columnType.equals("Strike Value")) {
									securityDetails.setStrikeValue((Double) convertedValue);
								}
								else if (columnType.equals("Strike Price Boundary Precision")) {
									securityDetails.setStrikePriceBoundaryPrecision((Double) convertedValue);
								}
								else if (columnType.equals("Strike Multiplier")) {
									securityDetails.setStrikeMultiplier((Double) convertedValue);
								}
								else if (columnType.equals("Percent at Risk")) {
									securityDetails.setPercentAtRisk((Double) convertedValue);
								}
								else if (columnType.equals("Option Payout Amount")) {
									securityDetails.setOptionPayoutAmount((Double) convertedValue);
								}
								else if (columnType.equals("Event Price")) {
									if(securityDetails.getSecurityEvents().size()>0)
										securityDetails.getSecurityEvents().get(securityDetails.getSecurityEvents().size()-1).setEventPrice((Double) convertedValue);
								}
								else if (columnType.equals("Complex Event Option Payout Amount")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setOptionPayoutAmount((Double) convertedValue);
								}
								else if (columnType.equals("Complex Event Price")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setEventPrice(((Double) convertedValue));

								}
								else if (columnType.equals("Complex Event Price Boundary Precision")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setEventPriceBoundaryPrecision(((Double) convertedValue));

								}
								else if (columnType.equals("Underlying End Price")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setEndPrice((Double) convertedValue);
								}
								else if (columnType.equals("Underlying Start Value")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setStartValue((Double) convertedValue);

								}
								else if (columnType.equals("Underlying End Value")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setEndValue((Double) convertedValue);

								}
								else if (columnType.equals("Underlying Allocation Percent")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setAllocationPercent((Double) convertedValue);

								}
								else if (columnType.equals("Underlying Cash Amount")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setCashAmount((Double) convertedValue);

								}
								else if (columnType.equals("Leg Ratio Quantity")) {
									if(securityDetails.getSecurityLegs().size()>0)
										securityDetails.getSecurityLegs().get(securityDetails.getSecurityLegs().size()-1).setRatioQuantity((Double) convertedValue);

								}
								else if (columnType.equals("Leg Option Ratio")) {
									if(securityDetails.getSecurityLegs().size()>0)
										securityDetails.getSecurityLegs().get(securityDetails.getSecurityLegs().size()-1).setOptionRatio((Double) convertedValue);

								}
								else if (columnType.equals("Position Limit")) {
									securityDetails.setPositionLimit((Integer) convertedValue);
								}
								else if (columnType.equals("Near-Term Position Limit")) {
									securityDetails.setNtPositionLimit((Integer) convertedValue);
								}
								else if (columnType.equals("Currency")) {
									securityDetails.setCurrency((String) convertedValue);
								}
								else if (columnType.equals("Strike Currency")) {
									securityDetails.setStrikeCurrency((String) convertedValue);
								}
								else if (columnType.equals("Additional Information")) {
									securityDetails.setSymbolSfx((String) convertedValue);
								}
								else if (columnType.equals("Product")) {
									securityDetails.setProduct((Integer) convertedValue);
								}
								else if (columnType.equals("Security Type")) {
									securityDetails.setSecurityType((String) convertedValue);
								}
								else if (columnType.equals("Security Identifier Source")) {
									securityDetails.setSecurityIDSource((String) convertedValue);
								}
								else if (columnType.equals("Security Alternative Identifier Source")) {
									if(securityDetails.getSecurityAltIDGroups().size()>0)
										securityDetails.getSecurityAltIDGroups().get(securityDetails.getSecurityAltIDGroups().size()-1).setSecurityAltIDSource((String) convertedValue);
								}
								else if (columnType.equals("Put or Call")) {
									securityDetails.setPutOrCall((Integer) convertedValue);
								}
								else if (columnType.equals("Country of Issue")) {
									securityDetails.setCountryOfIssue((String) convertedValue);
								}
								else if (columnType.equals("Contract Multiplier Unit")) {
									securityDetails.setContractMultiplierUnit((Integer) convertedValue);
								}
								else if (columnType.equals("Unit of Measure")) {
									securityDetails.setUnitOfMeasure((String) convertedValue);
								}
								else if (columnType.equals("Price Unit of Measure")) {
									securityDetails.setPriceUnitOfMeasure((String) convertedValue);
								}
								else if (columnType.equals("Strike Price Determination Method")) {
									securityDetails.setStrikePriceDeterminationMethod((Integer) convertedValue);
								}
								else if (columnType.equals("Strike Price Boundary Method")) {
									securityDetails.setStrikePriceBoundaryMethod((Integer) convertedValue);
								}
								else if (columnType.equals("Security Exchange")) {
									securityDetails.setSecurityExchange((String) convertedValue);
								}
								else if (columnType.equals("Commercial Paper Programm")) {
									securityDetails.setCpProgramm((Integer) convertedValue);
								}
								else if (columnType.equals("Security Status")) {
									securityDetails.setSecurityStatus((String) convertedValue);
								}
								else if (columnType.equals("Settle on Open")) {
									securityDetails.setSettlOnOpen((String) convertedValue);
								}
								else if (columnType.equals("Instrument Assignment Method")) {
									securityDetails.setInstrmtAssignmentMethod((String) convertedValue);
								}
								else if (columnType.equals("Underlying Price Determination Method")) {
									securityDetails.setUnderlyingDeterminationMethod((Integer) convertedValue);
								}
								else if (columnType.equals("Delivery Form")) {
									securityDetails.setDeliveryForm((Integer) convertedValue);
								}
								else if (columnType.equals("Settlement Method")) {
									securityDetails.setSettleMethod((String) convertedValue);
								}
								else if (columnType.equals("Exercise Style")) {
									securityDetails.setExerciseStyle((Integer) convertedValue);
								}
								else if (columnType.equals("Option Payout Type")) {
									securityDetails.setOptionPayoutType((Integer) convertedValue);
								}
								else if (columnType.equals("Price Quote Method")) {
									securityDetails.setPriceQuoteMethod((String) convertedValue);
								}
								else if (columnType.equals("List Method")) {
									securityDetails.setListMethod((Integer) convertedValue);
								}
								else if (columnType.equals("Flexible Indicator")) {
									securityDetails.setFlexibleIndicator((Boolean) convertedValue);
								}
								else if (columnType.equals("Flexible Product Eligibility Indicator")) {
									securityDetails.setFlexProductEligibilityIndicator((Boolean) convertedValue);
								}
								else if (columnType.equals("Valuation Method")) {
									securityDetails.setValuationMethod((String) convertedValue);
								}
								else if (columnType.equals("Flow Schedule Type")) {
									securityDetails.setFlowScheduleTyped((Integer) convertedValue);
								}
								else if (columnType.equals("Restructuring Type")) {
									securityDetails.setRestructuringType((String) convertedValue);
								}
								else if (columnType.equals("Seniority")) {
									securityDetails.setSeniority((String) convertedValue);
								}
								else if (columnType.equals("Attribute Type")) {
									SecurityAttribute securityAttribute = new SecurityAttribute();
									securityAttribute.setSecurity(securityDetails);
									securityAttribute.setAttributeType((Integer) convertedValue);
									securityDetails.getSecurityAttribute().add(securityAttribute);
								}
								else if (columnType.equals("Event Type")) {
									SecurityEvent securityEvent = new SecurityEvent();
									securityEvent.setSecurity(securityDetails);
									securityEvent.setEventType((Integer) convertedValue);
									securityDetails.getSecurityEvents().add(securityEvent);
								}
								else if (columnType.equals("Complex Event Type")) {
									SecurityComplexEvent securityComplexEvent = new SecurityComplexEvent();
									securityComplexEvent.setSecurity(securityDetails);
									securityComplexEvent.setEventType((Integer) convertedValue);
									securityDetails.getSecurityComplexEvents().add(securityComplexEvent);

								}
								else if (columnType.equals("Complex Event Price Boundary Method")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setEventPriceBoundaryMethod(((Integer) convertedValue));
								}
								else if (columnType.equals("Complex Event Price Time Type")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setEventPriceTimeType(((Integer) convertedValue));
								}
								else if (columnType.equals("Complex Event Condition")) {
									if(securityDetails.getSecurityComplexEvents().size()>0)
										securityDetails.getSecurityComplexEvents().get(securityDetails.getSecurityComplexEvents().size()-1).setEventCondition(((Integer) convertedValue));
								}
								else if (columnType.equals("Underlying Security")) {
									String legSecurityID = (String) convertedValue;
									SecurityUnderlying securityUnderlying = new SecurityUnderlying();
									securityUnderlying.setSecurity(securityDetails);
									securityUnderlying.setUnderlyingSecurity(excelImportTableModel.getSecurityIDMap().get(legSecurityID));
								}
								else if (columnType.equals("Underlying Settlement Type")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setSettlementType((Integer) convertedValue);
								}
								else if (columnType.equals("Underlying Cash Type")) {
									if(securityDetails.getSecurityUnderlyings().size()>0)
										securityDetails.getSecurityUnderlyings().get(securityDetails.getSecurityUnderlyings().size()-1).setCashType((String) convertedValue);
								}
								else if (columnType.equals("Leg Security")) {
									String legSecurityID = (String) convertedValue;
									SecurityLeg securityLeg = new SecurityLeg();
									securityLeg.setSecurity(securityDetails);
									securityLeg.setLegSecurity(excelImportTableModel.getSecurityIDMap().get(legSecurityID));
								}
								else if (columnType.equals("Leg Side")) {
									if(securityDetails.getSecurityLegs().size()>0)
										securityDetails.getSecurityLegs().get(securityDetails.getSecurityLegs().size()-1).setSide((String) convertedValue);
								}

							}
						}
						if(security==null)
							security = new FSecurity();
						security.setMaturity(maturityDate);
						security.setName(symbol);
						security.setSecurityID(securityID);
						security.setSecurityDetails(securityDetails);
						securities.add(new UploadEntry(i+1, security));
					}
				}
			}
		}

		return securities;

	}

}
