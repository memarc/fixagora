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
package net.sourceforge.fixagora.sap.client.model.editor;

import java.awt.Color;
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
import net.sourceforge.fixagora.sap.client.view.editor.SAPTradeCaptureEditor;
import net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse;

import org.apache.log4j.Logger;

/**
 * The Class SAPTradeCaptureEditorMonitorTableModel.
 */
public class SAPTradeCaptureEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private JTable table = null;

	private DecimalFormat decimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private int mouseOverRow = -1;

	private List<SAPTradeCaptureEntry> sapTradeCaptureEntries = new ArrayList<SAPTradeCaptureEntry>();

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private int fractionDigits = 3;

	private DecimalFormatSymbols decimalFormatSymbols;

	private SAPTradeCaptureEditor sapTradeCaptureEditor = null;
	
	private Color blue = new Color(204, 216, 255);

	private Color red = new Color(255, 198, 179);
	
	private Color green = new Color(216, 255, 204);
	
	private static Logger log = Logger.getLogger(SAPTradeCaptureEditorMonitorTableModel.class);

	/**
	 * Instantiates a new sAP trade capture editor monitor table model.
	 *
	 * @param tradeCaptureEditor the trade capture editor
	 */
	public SAPTradeCaptureEditorMonitorTableModel(final SAPTradeCaptureEditor tradeCaptureEditor) {

		super();

		this.sapTradeCaptureEditor = tradeCaptureEditor;

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

		int columnCount = 18;

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange())
			columnCount--;

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader())
			columnCount--;

		return columnCount;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		int col = column;

		if (col == 0)
			return "Status";

		if (col == 1)
			return "Financial Transaction";

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange())
			col++;

		if (col == 2)
			return "Exchange";

		if (col == 3)
			return "Transaction Type";

		if (col == 4)
			return "Security ID Number";

		if (col == 5)
			return "Partner";

		if (col == 6)
			return "Contact Person";

		if (col == 7)
			return "Price";

		if (col == 8)
			return "Number of Units";

		if (col == 9)
			return "Nominal Amount";

		if (col == 10)
			return "Position Value Date";

		if (col == 11)
			return "Calculation Date";

		if (col == 12)
			return "Payment Date";

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader())
			col++;

		if (col == 13)
			return "Trader";

		if (col == 14)
			return "Internal Reference";

		if (col == 15)
			return "External Reference";

		if (col == 16)
			return "Contract Time";

		if (col == 17)
			return "Contract Date";

		return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		if (sapTradeCaptureEntries.size() < 50)
			return 50;
		return sapTradeCaptureEntries.size();

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		if (rowIndex >= sapTradeCaptureEntries.size()) {
			if (columnIndex == 0) {
				return new SAPTradeCaptureCellValue(null, null, new Color(204, 216, 255));
			}
			return new SAPTradeCaptureCellValue(null, null);
		}

		SAPTradeCaptureEntry sapTradeCaptureEntry = sapTradeCaptureEntries.get(rowIndex);

		FSecurity security = sapTradeCaptureEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(sapTradeCaptureEntry.getSecurity());

		Counterparty counterparty = sapTradeCaptureEditor.getMainPanel().getCounterpartyTreeDialog()
				.getCounterpartyForId(sapTradeCaptureEntry.getCounterparty());

		Trader trader = null;

		if (sapTradeCaptureEntry.getTrader() != null) {
			AbstractBusinessObject abstractBusinessObject = sapTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
					sapTradeCaptureEntry.getTrader());
			if (abstractBusinessObject instanceof Trader)
				trader = (Trader) abstractBusinessObject;
		}

		FUser user = null;

		if (sapTradeCaptureEntry.getUser() != null) {
			AbstractBusinessObject abstractBusinessObject = sapTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(sapTradeCaptureEntry.getUser());
			if (abstractBusinessObject instanceof FUser)
				user = (FUser) abstractBusinessObject;
		}

		int colIndex = columnIndex;

		if (colIndex == 0) {
			switch(sapTradeCaptureEntry.getExportStatus())
			{
				case DONE:
					return new SAPTradeCaptureCellValue("Export done", null, green);
				case FAILED:
					return new SAPTradeCaptureCellValue("Export failed", null, red);
				case TEST_DONE:
					return new SAPTradeCaptureCellValue("Test passed", null, green);					
				case TEST_FAILED:
					return new SAPTradeCaptureCellValue("Test failed", null, red);
				default:
					return new SAPTradeCaptureCellValue("New", null, blue);

			}
		}

		if (colIndex == 1)
		{
			if(sapTradeCaptureEntry.getSapFinancialTransaction()==null)
				return new SAPTradeCaptureCellValue(null, "uncommitted");
			else
				return new SAPTradeCaptureCellValue(sapTradeCaptureEntry.getSapFinancialTransaction(), null);
		}

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange())
			colIndex++;

		if (colIndex == 2)
			return new SAPTradeCaptureCellValue(sapTradeCaptureEntry.getMarket(), null);

		if (colIndex == 3) {
			if (sapTradeCaptureEntry.getSide() == SAPTradeCaptureEntry.Side.SELL)
				return new SAPTradeCaptureCellValue(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeSell(), "Sell");

			return new SAPTradeCaptureCellValue(sapTradeCaptureEditor.getSAPTradeCapture().getSapTransactionTypeBuy(), "Buy");
		}

		if (colIndex == 4) {
			if (security != null) {
				String securityIDSource = sapTradeCaptureEditor.getSAPTradeCapture().getSecurityIDSource();
				String securityID = security.getSecurityID();
				if (securityIDSource != null)
					for (SecurityAltIDGroup securityAltIDGroup : security.getSecurityDetails().getSecurityAltIDGroups())
						if (securityIDSource.equals(securityAltIDGroup.getSecurityAltIDSource()))
							return new SAPTradeCaptureCellValue(securityAltIDGroup.getSecurityAltID(), security.getName());
				return new SAPTradeCaptureCellValue(securityID, security.getName(), Color.RED);
			}
			else
				return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 5) {
			if (counterparty != null) {
				
				String partyID = null;
				
				for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
					if(counterPartyPartyID.getAbstractBusinessComponent()==null)
					{
						if(partyID==null)
							partyID = counterPartyPartyID.getPartyID();
					}
					else if (counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole() == 17
							&& counterPartyPartyID.getAbstractBusinessComponent().getId() == sapTradeCaptureEditor.getSAPTradeCapture().getId())
						partyID = counterPartyPartyID.getPartyID();
				}
				
				return new SAPTradeCaptureCellValue(partyID, counterparty.getName(), Color.RED);
			}
			else
				return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 6) {
			if (trader != null) {
				
				String partyID = null;
				
				for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
					if (traderPartyID.getAbstractBusinessComponent()==null)
					{
						if(partyID==null)
							partyID = traderPartyID.getPartyID();
					}
					else if (traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole() == 37
							&& traderPartyID.getAbstractBusinessComponent().getId() == sapTradeCaptureEditor.getSAPTradeCapture().getId())
						partyID = traderPartyID.getPartyID();
				}
				
				if(partyID==null)
					return new SAPTradeCaptureCellValue(trader.getName(), trader.getName());
				else
					return new SAPTradeCaptureCellValue(partyID, trader.getName());
			}
			else
				return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 7) {
			if (sapTradeCaptureEntry.getLastPrice() != null)
				return new SAPTradeCaptureCellValue(decimalFormat.format(sapTradeCaptureEntry.getLastPrice()), null);

			return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 8) {
			if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() == null
					&& sapTradeCaptureEntry.getLastQuantity() != null)
				return new SAPTradeCaptureCellValue(decimalFormat2.format(sapTradeCaptureEntry.getLastQuantity()), null);
			return new SAPTradeCaptureCellValue(null, "not used");
		}

		if (colIndex == 9) {
			if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null
					&& sapTradeCaptureEntry.getLastQuantity() != null)
				return new SAPTradeCaptureCellValue(decimalFormat2.format(sapTradeCaptureEntry.getLastQuantity()), null);
			return new SAPTradeCaptureCellValue(null, "not used");
		}

		if (colIndex == 10) {
			if (sapTradeCaptureEntry.getSettlementDate() != null)
				return new SAPTradeCaptureCellValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), null);
			return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 11) {
			if (sapTradeCaptureEntry.getSettlementDate() != null)
				return new SAPTradeCaptureCellValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), null);
			return new SAPTradeCaptureCellValue(null, null);
		}

		if (colIndex == 12) {
			if (sapTradeCaptureEntry.getSettlementDate() != null)
				return new SAPTradeCaptureCellValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), null);
			return new SAPTradeCaptureCellValue(null, null);
		}

		if (!sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader())
			colIndex++;

		if (colIndex == 13) {
			if (user != null) {
				
				String userID = null;
				
				for (FUserPartyID userPartyID : user.getfUserPartyIDs()) {
					if (userPartyID.getAbstractBusinessComponent()==null)
					{
						if(userID==null)
							userID = userPartyID.getPartyID();
					}
					else if (userPartyID.getPartyRole() != null && userPartyID.getPartyRole() == 12
							&& userPartyID.getAbstractBusinessComponent().getId() == sapTradeCaptureEditor.getSAPTradeCapture().getId())
						userID = userPartyID.getPartyID();
				}
				
				if(userID==null)
					return new SAPTradeCaptureCellValue("???", user.getName());
				else
					return new SAPTradeCaptureCellValue(userID, user.getName());

			}

			return new SAPTradeCaptureCellValue(sapTradeCaptureEntry.getUserID(), null);
		}

		if (colIndex == 14)
			return new SAPTradeCaptureCellValue(sapTradeCaptureEntry.getTradeId(), null);

		if (colIndex == 15)
			return new SAPTradeCaptureCellValue(sapTradeCaptureEntry.getCounterpartyOrderId(), null);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(sapTradeCaptureEntry.getUpdated());

		if (colIndex == 16)
			return new SAPTradeCaptureCellValue(DateFormat.getTimeInstance().format(calendar2.getTime()), null);

		if (colIndex == 17)
			return new SAPTradeCaptureCellValue(simpleDateFormat.format(calendar2.getTime()), null);

		return new SAPTradeCaptureCellValue(null, null);
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
	 * On sap trade capture entry response.
	 *
	 * @param sapTradeCaptureEntryResponse the sap trade capture entry response
	 */
	public synchronized void onSAPTradeCaptureEntryResponse(SAPTradeCaptureEntryResponse sapTradeCaptureEntryResponse) {

		SAPTradeCaptureEntry tradeCaptureEntry = sapTradeCaptureEntryResponse.getSAPTradeCaptureEntry();

		if (tradeCaptureEntry.getSAPTradeCapture() != sapTradeCaptureEditor.getSAPTradeCapture().getId())
			return;

		List<SAPTradeCaptureEntry> sapTradeCaptureEntries2 = new ArrayList<SAPTradeCaptureEntry>();
		sapTradeCaptureEntries2.addAll(sapTradeCaptureEntries);
		for (SAPTradeCaptureEntry tradeCaptureEntry2 : sapTradeCaptureEntries2) {
			if (tradeCaptureEntry2.getId() == tradeCaptureEntry.getId()) {

				sapTradeCaptureEntries.remove(tradeCaptureEntry2);

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

			sapTradeCaptureEntries.add(tradeCaptureEntry);

			Collections.sort(sapTradeCaptureEntries, new Comparator<SAPTradeCaptureEntry>() {

				@Override
				public int compare(SAPTradeCaptureEntry o1, SAPTradeCaptureEntry o2) {

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

			table.getColumnModel().getColumn(0).setPreferredWidth(fontMetrics.stringWidth("Export failed") + 17);
			
			for (int i = 1; i < getColumnCount(); i++) {

				int width = fontMetrics.stringWidth(getColumnName(i)) + 12;
				for (int j = 0; j < sapTradeCaptureEntries.size(); j++) {
					Object object = getValueAt(j, i);
					if (object instanceof SAPTradeCaptureCellValue) {
						SAPTradeCaptureCellValue sapTradeCaptureCellValue = (SAPTradeCaptureCellValue) object;
						StringBuffer stringBuffer = new StringBuffer();
						if (sapTradeCaptureCellValue.getValue() != null)
							stringBuffer.append(sapTradeCaptureCellValue.getValue());
						if (sapTradeCaptureCellValue.getAdditionalInfo() != null) {
							stringBuffer.append(" ");
							stringBuffer.append(sapTradeCaptureCellValue.getAdditionalInfo());
						}
						int width2 = fontMetrics.stringWidth(stringBuffer.toString()) + 17;
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
	public SAPTradeCaptureEntry getEntryForRow() {

		if (mouseOverRow != -1 && sapTradeCaptureEntries.size() > mouseOverRow)
			return sapTradeCaptureEntries.get(mouseOverRow);
		return null;
	}

	/**
	 * On open sap trade capture response.
	 *
	 * @param openSAPTradeCaptureResponse the open sap trade capture response
	 */
	public synchronized void onOpenSAPTradeCaptureResponse(OpenSAPTradeCaptureResponse openSAPTradeCaptureResponse) {

		sapTradeCaptureEntries.clear();

		for (SAPTradeCaptureEntry sapTradeCaptureEntry : openSAPTradeCaptureResponse.getCaptureEntries()) {
			if (sapTradeCaptureEntry.getLastPrice() != null) {
				int decimalPlaces = getDecimalPlaces(sapTradeCaptureEntry.getLastPrice());
				if (decimalPlaces > fractionDigits) {
					StringBuffer stringBuffer = new StringBuffer("#,##0.");
					for (int i = 0; i < decimalPlaces; i++) {
						stringBuffer.append("0");
					}
					decimalFormat = new DecimalFormat(stringBuffer.toString());
					decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
				}
			}

			sapTradeCaptureEntries.add(sapTradeCaptureEntry);
		}

		Collections.sort(sapTradeCaptureEntries, new Comparator<SAPTradeCaptureEntry>() {

			@Override
			public int compare(SAPTradeCaptureEntry o1, SAPTradeCaptureEntry o2) {

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

		return sapTradeCaptureEntries.size();
	}

	/**
	 * Gets the sAP trade capture entries.
	 *
	 * @return the sAP trade capture entries
	 */
	public List<SAPTradeCaptureEntry> getSAPTradeCaptureEntries() {

		return sapTradeCaptureEntries;
	}

	/**
	 * Gets the decimal format pattern.
	 *
	 * @return the decimal format pattern
	 */
	public String getDecimalFormatPattern() {

		return decimalFormat.toPattern();
	}

	/**
	 * Checks if is exportable.
	 *
	 * @return true, if is exportable
	 */
	public boolean isExportable() {

		for(SAPTradeCaptureEntry sapTradeCaptureEntry: sapTradeCaptureEntries)
			if(sapTradeCaptureEntry.getExportStatus()!=ExportStatus.DONE)
				return true;
		return false;
	}

}
