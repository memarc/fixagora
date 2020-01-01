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
package net.sourceforge.fixagora.tradecapture.client.model.editor;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.tradecapture.client.view.editor.TradeCaptureEditor;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;

import org.apache.log4j.Logger;

/**
 * The Class TradeCaptureEditorMonitorTableModel.
 */
public class TradeCaptureEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 200;

	private JTable table = null;

	private DecimalFormat decimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private int mouseOverRow = -1;

	private List<TradeCaptureEntry> tradeCaptureEntries = new ArrayList<TradeCaptureEntry>();

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<TradeCaptureEntry> updated = new HashSet<TradeCaptureEntry>();

	private int fractionDigits = 3;

	private DecimalFormatSymbols decimalFormatSymbols;

	private TradeCaptureEditor tradeCaptureEditor = null;

	private static Logger log = Logger.getLogger(TradeCaptureEditorMonitorTableModel.class);

	/**
	 * Instantiates a new trade capture editor monitor table model.
	 *
	 * @param tradeCaptureEditor the trade capture editor
	 */
	public TradeCaptureEditorMonitorTableModel(final TradeCaptureEditor tradeCaptureEditor) {

		super();

		this.tradeCaptureEditor = tradeCaptureEditor;

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

		return 16;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Source";

			case 1:
				return "Market";

			case 2:
				return "Side";

			case 3:
				return "Security";

			case 4:
				return "Counterparty";

			case 5:
				return "Trader";

			case 6:
				return "Price";

			case 7:
				return "Yield";

			case 8:
				return "Quantity";

			case 9:
				return "Settlement";

			case 10:
				return "Owner";

			case 11:
				return "Trade ID";

			case 12:
				return "Order ID";

			case 13:
				return "Counterparty Order ID";

			case 14:
				return "Execution ID";

			case 15:
				return "Updated";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return tradeCaptureEntries.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		TradeCaptureEntry tradeCaptureEntry = tradeCaptureEntries.get(rowIndex);

		switch (columnIndex) {

			case 0:

				String sourceName = "Unknown";

				AbstractBusinessObject abstractBusinessComponent = tradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
						tradeCaptureEntry.getSourceComponent());
				if (abstractBusinessComponent != null)
					sourceName = abstractBusinessComponent.getName();
				return sourceName;

			case 1:

				String marketName = "Unknown";

				if (tradeCaptureEntry.getMarket() != null)
					marketName = tradeCaptureEntry.getMarket();

				return marketName;

			case 2:
				if (tradeCaptureEntry.getSide() == net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry.Side.SELL)
					return "Sell";
				return "Buy";

			case 3:
				FSecurity security = tradeCaptureEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(tradeCaptureEntry.getSecurity());
				if (security != null)
					return security.getName();
				else
					return "Unknown";

			case 4:
				Counterparty counterparty = tradeCaptureEditor.getMainPanel().getCounterpartyTreeDialog()
						.getCounterpartyForId(tradeCaptureEntry.getCounterparty());
				if (counterparty != null)
					return counterparty.getName();
				else
					return "Unknown";

			case 5:
				if (tradeCaptureEntry.getTrader() != null) {
					AbstractBusinessObject abstractBusinessObject = tradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
							tradeCaptureEntry.getTrader());
					if (abstractBusinessObject != null)
						return abstractBusinessObject.getName();
				}
				return "";

			case 6:
				if (tradeCaptureEntry.getLastPrice() != null)
					return decimalFormat.format(tradeCaptureEntry.getLastPrice());
				return "";
			case 7:
				if (tradeCaptureEntry.getLastYield() != null)
					return decimalFormat.format(tradeCaptureEntry.getLastYield());
				return "";
			case 8:
				if (tradeCaptureEntry.getLastQuantity() != null)
					return decimalFormat2.format(tradeCaptureEntry.getLastQuantity());
				return "";

			case 9:
				if (tradeCaptureEntry.getSettlementDate() != null)
					return simpleDateFormat.format(new Date(tradeCaptureEntry.getSettlementDate()));
				return "";

			case 10:
				String owner = "";
				if (tradeCaptureEntry.getUser() != null) {
					AbstractBusinessObject abstractBusinessComponent2 = tradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
							tradeCaptureEntry.getUser());
					if (abstractBusinessComponent2 != null)
						owner = abstractBusinessComponent2.getName();
				}
				return owner;

			case 11:
				if (tradeCaptureEntry.getTradeId() != null)
					return tradeCaptureEntry.getTradeId();
				return "";

			case 12:
				if (tradeCaptureEntry.getOrderId() != null)
					return tradeCaptureEntry.getOrderId();
				return "";

			case 13:
				if (tradeCaptureEntry.getCounterpartyOrderId() != null)
					return tradeCaptureEntry.getCounterpartyOrderId();
				return "";

			case 14:
				if (tradeCaptureEntry.getExecId() != null)
					return tradeCaptureEntry.getExecId();
				return "";

			case 15:
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTimeInMillis(tradeCaptureEntry.getUpdated());
				return DateFormat.getTimeInstance().format(calendar2.getTime()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(calendar2.getTime());

			default:
				return "";
		}
	}

	/**
	 * Sets the min width.
	 *
	 * @param minWidth the new min width
	 */
	public void setMinWidth(final int minWidth) {

		if (minWidth > 0)
			this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		table.getColumnModel().getColumn(15).setPreferredWidth(minWidth);
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final JTable table) {

		this.table = table;
		setTableWidth();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	/**
	 * Checks if is repaint required.
	 *
	 * @return true, if is repaint required
	 */
	public boolean isRepaintRequired() {

		return updated.size() > 0;
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
	 * On trade capture entry response.
	 *
	 * @param tradeCaptureEntryResponse the trade capture entry response
	 */
	public synchronized void onTradeCaptureEntryResponse(TradeCaptureEntryResponse tradeCaptureEntryResponse) {

		TradeCaptureEntry tradeCaptureEntry = tradeCaptureEntryResponse.getTradeCaptureEntry();

		if (tradeCaptureEntry.getTradeCapture() != tradeCaptureEditor.getTradeCapture().getId())
			return;

		List<TradeCaptureEntry> tradeCaptureEntries2 = new ArrayList<TradeCaptureEntry>();
		tradeCaptureEntries2.addAll(tradeCaptureEntries);
		for (TradeCaptureEntry tradeCaptureEntry2 : tradeCaptureEntries2) {
			if (tradeCaptureEntry2.getId() == tradeCaptureEntry.getId()) {

				updated.remove(tradeCaptureEntry2);
				tradeCaptureEntries.remove(tradeCaptureEntry2);

			}
		}

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

		tradeCaptureEntries.add(tradeCaptureEntry);
		updated.add(tradeCaptureEntry);

		Collections.sort(tradeCaptureEntries, new Comparator<TradeCaptureEntry>() {

			@Override
			public int compare(TradeCaptureEntry o1, TradeCaptureEntry o2) {

				if (o1.getCreated() < o2.getCreated())
					return 1;
				return -1;
			}
		});

		adjust();

		fireTableDataChanged();

		if (tradeCaptureEditor.isDockableVisible())
			updated.clear();
	}

	private void adjust() {

		try {
			Font font = new Font("Dialog", Font.PLAIN, 12);
			FontMetrics fontMetrics = table.getFontMetrics(font);

			for (int i = 0; i < getColumnCount(); i++) {
				int width = fontMetrics.stringWidth(getColumnName(i)) + 12;
				for (int j = 0; j < tradeCaptureEntries.size(); j++) {
					Object object = getValueAt(j, i);
					if (object != null) {
						int width2 = fontMetrics.stringWidth(object.toString()) + 12;
						if (width2 > width)
							width = width2;
					}
				}
				if (i < 15)
					table.getColumnModel().getColumn(i).setPreferredWidth(width);
				else if (width > minWidth)
					setMinWidth(width);
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
	public TradeCaptureEntry getEntryForRow() {

		if (mouseOverRow != -1 && tradeCaptureEntries.size() > mouseOverRow)
			return tradeCaptureEntries.get(mouseOverRow);
		return null;
	}

	/**
	 * Checks if is updated.
	 *
	 * @param row the row
	 * @return true, if is updated
	 */
	public boolean isUpdated(int row) {

		return updated.contains(tradeCaptureEntries.get(row));
	}

	/**
	 * Reset update.
	 */
	public synchronized void resetUpdate() {

		updated.clear();
		fireTableDataChanged();

	}

	/**
	 * On open trade capture response.
	 *
	 * @param openTradeCaptureResponse the open trade capture response
	 */
	public synchronized void onOpenTradeCaptureResponse(OpenTradeCaptureResponse openTradeCaptureResponse) {

		tradeCaptureEntries.clear();
		updated.clear();

		for (TradeCaptureEntry tradeCaptureEntry : openTradeCaptureResponse.getCaptureEntries()) {
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

			tradeCaptureEntries.add(tradeCaptureEntry);
		}

		Collections.sort(tradeCaptureEntries, new Comparator<TradeCaptureEntry>() {

			@Override
			public int compare(TradeCaptureEntry o1, TradeCaptureEntry o2) {

				if (o1.getCreated() < o2.getCreated())
					return 1;
				return -1;
			}
		});

		adjust();

		fireTableDataChanged();
	}

	/**
	 * Gets the updated.
	 *
	 * @return the updated
	 */
	public int getUpdated() {

		return updated.size();
	}

	/**
	 * Clear updated.
	 */
	public void clearUpdated() {

		updated.clear();

	}

}
