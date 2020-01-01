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
package net.sourceforge.fixagora.excel.client.model.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.excel.client.view.editor.ExcelTradeCaptureEditor;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellReference;

/**
 * The Class ExcelTradeCaptureEditorMonitorTableModel.
 */
public class ExcelTradeCaptureEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private JTable table = null;

	private DecimalFormat decimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private int mouseOverRow = -1;

	private List<ExcelTradeCaptureEntry> excelTradeCaptureEntries = new ArrayList<ExcelTradeCaptureEntry>();

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private int fractionDigits = 3;

	private DecimalFormatSymbols decimalFormatSymbols;

	private ExcelTradeCaptureEditor excelTradeCaptureEditor = null;
	
	private static Logger log = Logger.getLogger(ExcelTradeCaptureEditorMonitorTableModel.class);


	/**
	 * Instantiates a new excel trade capture editor monitor table model.
	 *
	 * @param tradeCaptureEditor the trade capture editor
	 */
	public ExcelTradeCaptureEditorMonitorTableModel(final ExcelTradeCaptureEditor tradeCaptureEditor) {

		super();

		this.excelTradeCaptureEditor = tradeCaptureEditor;

		decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		decimalFormat = new DecimalFormat("#,##0.000");
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);
	}

	private int getDecimalPlaces(double value) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0#########");
		BigDecimal bigDecimal = new BigDecimal(decimalFormat.format(value));
		return bigDecimal.scale();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 50;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		if (column > 0)
			return CellReference.convertNumToColString(column - 1);
		return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		if (excelTradeCaptureEntries.size() > 100)
			return excelTradeCaptureEntries.size() + 1;
		return 100;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		if (rowIndex == 0)
			switch (columnIndex) {

				case 0:
					return "1";

				case 1:
					return "Source";

				case 2:
					return "Market";

				case 3:
					return "Side";

				case 4:
					return "Security";

				case 5:
					return "Security ID";

				case 6:
					return "Counterparty";

				case 7:
					return "Counterparty ID";

				case 8:
					return "Contra Trader";

				case 9:
					return "Contra Trader ID";

				case 10:
					return "Price";
					
				case 11:
					return "Yield";

				case 12:
					return "Quantity";

				case 13:
					return "Settlement";

				case 14:
					return "Executing Trader";

				case 15:
					return "Executing Trader ID";

				case 16:
					return "Trade ID";

				case 17:
					return "Order ID";

				case 18:
					return "Counterparty Order ID";

				case 19:
					return "Execution ID";

				case 20:
					return "Updated";

				default:
					return "";
			}

		if (rowIndex > excelTradeCaptureEntries.size()) {
			if (columnIndex == 0)
				return Integer.toString(rowIndex);
			return "";
		}

		ExcelTradeCaptureEntry excelTradeCaptureEntry = excelTradeCaptureEntries.get(rowIndex - 1);

		FSecurity security = excelTradeCaptureEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(excelTradeCaptureEntry.getSecurity());

		Counterparty counterparty = excelTradeCaptureEditor.getMainPanel().getCounterpartyTreeDialog()
				.getCounterpartyForId(excelTradeCaptureEntry.getCounterparty());

		Trader trader = null;

		if (excelTradeCaptureEntry.getTrader() != null) {
			AbstractBusinessObject abstractBusinessObject = excelTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
					excelTradeCaptureEntry.getTrader());
			if (abstractBusinessObject instanceof Trader)
				trader = (Trader) abstractBusinessObject;
		}

		FUser user = null;

		if (excelTradeCaptureEntry.getUser() != null) {
			AbstractBusinessObject abstractBusinessObject = excelTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
					excelTradeCaptureEntry.getUser());
			if (abstractBusinessObject instanceof FUser)
				user = (FUser) abstractBusinessObject;
		}

		switch (columnIndex) {

			case 0:

				return Integer.toString(rowIndex + 1);

			case 1:

				String sourceName = "Unknown";

				for (AbstractBusinessObject abstractBusinessComponent : excelTradeCaptureEditor.getMainPanel().getBusinessComponents())
					if (abstractBusinessComponent.getId() == excelTradeCaptureEntry.getSourceComponent())
						sourceName = abstractBusinessComponent.getName();
				return sourceName;

			case 2:

				String marketName = "Unknown";

				if (excelTradeCaptureEntry.getMarket() != null)
					marketName = excelTradeCaptureEntry.getMarket();

				return marketName;

			case 3:
				if (excelTradeCaptureEntry.getSide() == ExcelTradeCaptureEntry.Side.SELL)
					return "Sell";
				return "Buy";

			case 4:
				if (security != null) {
					return security.getName();
				}
				else
					return "Unknown";

			case 5:
				if (security != null) {
					String securityIDSource = excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource();
					String securityID = security.getSecurityID();
					if (securityIDSource != null)
						for (SecurityAltIDGroup securityAltIDGroup : security.getSecurityDetails().getSecurityAltIDGroups())
							if (securityIDSource.equals(securityAltIDGroup.getSecurityAltIDSource()))
								return securityAltIDGroup.getSecurityAltID();
					return securityID;
				}
				else
					return "";

			case 6:
				if (counterparty != null)
					return counterparty.getName();
				else
					return "Unknown";

			case 7:
				if (counterparty != null)
				{
					String counterPartyID = null;
					for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
						if(counterPartyPartyID.getAbstractBusinessComponent()==null)
						{
							if(counterPartyID==null)
								counterPartyID = counterPartyPartyID.getPartyID();
						}
						else if (counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole() == 17
								&& counterPartyPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture().getId())
							counterPartyID = counterPartyPartyID.getPartyID();
					}
					
					if(counterPartyID==null)
						return "";
					else
						return counterPartyID;
				}
				else
					return "";

			case 8:
				if (trader != null)
					return trader.getName();

				return "";

			case 9:
				if (trader != null)
				{
					String traderID = null;
					for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
						if(traderPartyID.getAbstractBusinessComponent()==null)
						{
							if(traderID==null)
								traderID = traderPartyID.getPartyID();
						}
						else if (traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole() == 37
								&& traderPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture().getId())
							traderID = traderPartyID.getPartyID();
					}
					
					if(traderID==null)
						return "";
					else
						return traderID;
					
				}
				else
					return "";

			case 10:
				if (excelTradeCaptureEntry.getLastPrice() != null)
					return decimalFormat.format(excelTradeCaptureEntry.getLastPrice());
				return "";
				
			case 11:
				if (excelTradeCaptureEntry.getLastYield() != null)
					return decimalFormat.format(excelTradeCaptureEntry.getLastYield());
				return "";	
				
			case 12:
				if (excelTradeCaptureEntry.getLastQuantity() != null)
					return decimalFormat2.format(excelTradeCaptureEntry.getLastQuantity());
				return "";

			case 13:
				if (excelTradeCaptureEntry.getSettlementDate() != null)
					return simpleDateFormat.format(new Date(excelTradeCaptureEntry.getSettlementDate()));
				return "";

			case 14:
				if(user!=null)
					return user.getName();

			case 15:
				if(user!=null)
				{
					String userID = null;
					for (FUserPartyID userPartyID : user.getfUserPartyIDs()) {
						if(userPartyID.getAbstractBusinessComponent()==null)
						{
							if(userID==null)
								userID = userPartyID.getPartyID();
						}
						else if (userPartyID.getPartyRole() != null && userPartyID.getPartyRole() == 12
								&& userPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture().getId())
							userID = userPartyID.getPartyID();
					}
					
					if(userID==null)
						return "";
					else
						return userID;
					
				}
				return excelTradeCaptureEntry.getUserID();

			case 16:
				if (excelTradeCaptureEntry.getTradeId() != null)
					return excelTradeCaptureEntry.getTradeId();
				return "";

			case 17:
				if (excelTradeCaptureEntry.getOrderId() != null)
					return excelTradeCaptureEntry.getOrderId();
				return "";

			case 18:
				if (excelTradeCaptureEntry.getCounterpartyOrderId() != null)
					return excelTradeCaptureEntry.getCounterpartyOrderId();
				return "";

			case 19:
				if (excelTradeCaptureEntry.getExecId() != null)
					return excelTradeCaptureEntry.getExecId();
				return "";

			case 20:
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTimeInMillis(excelTradeCaptureEntry.getUpdated());
				return DateFormat.getTimeInstance().format(calendar2.getTime()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(calendar2.getTime());

			default:
				return "";
		}
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final JTable table) {

		this.table = table;
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		for (int i = 1; i < getColumnCount(); i++)

			table.getColumnModel().getColumn(i).setPreferredWidth(100);

		adjust();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	/**
	 * Sets the mouse over row.
	 *
	 * @param row the new mouse over row
	 */
	public void setMouseOverRow(int row) {

		mouseOverRow = row;

	}

	/**
	 * Gets the mouse over row.
	 *
	 * @return the mouse over row
	 */
	public int getMouseOverRow() {

		return mouseOverRow;
	}

	/**
	 * On excel trade capture entry response.
	 *
	 * @param excelTradeCaptureEntryResponse the excel trade capture entry response
	 */
	public synchronized void onExcelTradeCaptureEntryResponse(ExcelTradeCaptureEntryResponse excelTradeCaptureEntryResponse) {
		
		ExcelTradeCaptureEntry tradeCaptureEntry = excelTradeCaptureEntryResponse.getExcelTradeCaptureEntry();

		if (tradeCaptureEntry.getExcelTradeCapture() != excelTradeCaptureEditor.getExcelTradeCapture().getId())
			return;

		List<ExcelTradeCaptureEntry> excelTradeCaptureEntries2 = new ArrayList<ExcelTradeCaptureEntry>();
		excelTradeCaptureEntries2.addAll(excelTradeCaptureEntries);
		for (ExcelTradeCaptureEntry tradeCaptureEntry2 : excelTradeCaptureEntries2) {
			if (tradeCaptureEntry2.getId() == tradeCaptureEntry.getId()) {

				excelTradeCaptureEntries.remove(tradeCaptureEntry2);

			}
		}

		if (!tradeCaptureEntry.isRemoved()) {

			if (tradeCaptureEntry.getLastPrice() != null) {
				int decimalPlaces = getDecimalPlaces(tradeCaptureEntry.getLastPrice());
				if (decimalPlaces > fractionDigits) {
					StringBuffer stringBuffer = new StringBuffer("#,##0.");
					for (int i = 0; i < decimalPlaces; i++) {
						stringBuffer.append("0");
					}
					decimalFormat = new DecimalFormat(stringBuffer.toString());
					decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
				}
			}
			if (tradeCaptureEntry.getLastYield() != null) {
				int decimalPlaces = getDecimalPlaces(tradeCaptureEntry.getLastYield());
				if (decimalPlaces > fractionDigits) {
					StringBuffer stringBuffer = new StringBuffer("#,##0.");
					for (int i = 0; i < decimalPlaces; i++) {
						stringBuffer.append("0");
					}
					decimalFormat = new DecimalFormat(stringBuffer.toString());
					decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
				}
			}

			excelTradeCaptureEntries.add(tradeCaptureEntry);

			Collections.sort(excelTradeCaptureEntries, new Comparator<ExcelTradeCaptureEntry>() {

				@Override
				public int compare(ExcelTradeCaptureEntry o1, ExcelTradeCaptureEntry o2) {

					if (o1.getCreated() < o2.getCreated())
						return 1;
					return -1;
				}
			});

		}

		adjust();

		fireTableDataChanged();

	}

	private void adjust() {

		try {
			Font font = new Font("Dialog", Font.PLAIN, 12);
			FontMetrics fontMetrics = table.getFontMetrics(font);

			Font font2 = new Font("Dialog", Font.BOLD, 12);
			FontMetrics fontMetrics2 = table.getFontMetrics(font2);

			for (int i = 1; i < 21; i++) {

				int width = fontMetrics2.stringWidth(getValueAt(0, i).toString()) + 12;
				for (int j = 1; j < excelTradeCaptureEntries.size() + 1; j++) {
					Object object = getValueAt(j, i);
					if (object != null) {
						int width2 = fontMetrics.stringWidth(object.toString()) + 12;
						if (width2 > width)
							width = width2;
					}
				}
				table.getColumnModel().getColumn(i).setPreferredWidth(width);

			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/**
	 * Gets the entry for row.
	 *
	 * @return the entry for row
	 */
	public ExcelTradeCaptureEntry getEntryForRow() {

		if (mouseOverRow > 0 && excelTradeCaptureEntries.size() >= mouseOverRow)
			return excelTradeCaptureEntries.get(mouseOverRow-1);
		return null;
	}

	/**
	 * On open excel trade capture response.
	 *
	 * @param openExcelTradeCaptureResponse the open excel trade capture response
	 */
	public synchronized void onOpenExcelTradeCaptureResponse(OpenExcelTradeCaptureResponse openExcelTradeCaptureResponse) {

		excelTradeCaptureEntries.clear();

		for (ExcelTradeCaptureEntry excelTradeCaptureEntry : openExcelTradeCaptureResponse.getCaptureEntries()) {
			if (excelTradeCaptureEntry.getLastPrice() != null) {
				int decimalPlaces = getDecimalPlaces(excelTradeCaptureEntry.getLastPrice());
				if (decimalPlaces > fractionDigits) {
					StringBuffer stringBuffer = new StringBuffer("#,##0.");
					for (int i = 0; i < decimalPlaces; i++) {
						stringBuffer.append("0");
					}
					decimalFormat = new DecimalFormat(stringBuffer.toString());
					decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
				}
			}
			if (excelTradeCaptureEntry.getLastYield() != null) {
				int decimalPlaces = getDecimalPlaces(excelTradeCaptureEntry.getLastYield());
				if (decimalPlaces > fractionDigits) {
					StringBuffer stringBuffer = new StringBuffer("#,##0.");
					for (int i = 0; i < decimalPlaces; i++) {
						stringBuffer.append("0");
					}
					decimalFormat = new DecimalFormat(stringBuffer.toString());
					decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
				}
			}

			excelTradeCaptureEntries.add(excelTradeCaptureEntry);
		}

		Collections.sort(excelTradeCaptureEntries, new Comparator<ExcelTradeCaptureEntry>() {

			@Override
			public int compare(ExcelTradeCaptureEntry o1, ExcelTradeCaptureEntry o2) {

				if (o1.getCreated() < o2.getCreated())
					return 1;
				return -1;
			}
		});

		adjust();

		fireTableDataChanged();
	}

	/**
	 * Gets the trade count.
	 *
	 * @return the trade count
	 */
	public int getTradeCount() {

		return excelTradeCaptureEntries.size();
	}

	/**
	 * Gets the excel trade capture entries.
	 *
	 * @return the excel trade capture entries
	 */
	public List<ExcelTradeCaptureEntry> getExcelTradeCaptureEntries() {

		return excelTradeCaptureEntries;
	}
	
	/**
	 * Gets the decimal format pattern.
	 *
	 * @return the decimal format pattern
	 */
	public String getDecimalFormatPattern()
	{
		return decimalFormat.toPattern();
	}

}
