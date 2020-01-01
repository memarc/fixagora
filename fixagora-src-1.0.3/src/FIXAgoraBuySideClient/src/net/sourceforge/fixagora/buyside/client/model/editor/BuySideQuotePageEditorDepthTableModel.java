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
package net.sourceforge.fixagora.buyside.client.model.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.dialog.CounterpartyTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.InitiatorTreeDialog;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCounterpartyMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;

/**
 * The Class BuySideQuotePageEditorDepthTableModel.
 */
public class BuySideQuotePageEditorDepthTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 200;

	private JTable table = null;

	private long security = 0;

	private List<BuySideQuotePageEntry> buySideMDInputEntryList = new ArrayList<BuySideQuotePageEntry>();

	private DecimalFormat priceDecimalFormat = null;

	private DecimalFormat yieldDecimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0");

	private long timestamp = 0;

	private int mouseOverRow = -1;

	private CounterpartyTreeDialog counterpartyTreeDialog = null;

	private Comparator<BuySideQuotePageEntry> comparator = null;

	private InitiatorTreeDialog fixInitiatorTreeDialog = null;

	private boolean showYield = false;
	
	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();
	
	private boolean absoluteChange = true;

	/**
	 * Instantiates a new buy side quote page editor depth table model.
	 *
	 * @param buySideQuotePage the buy side quote page
	 * @param comparator the comparator
	 * @param mainPanel the main panel
	 */
	public BuySideQuotePageEditorDepthTableModel(final BuySideQuotePage buySideQuotePage, Comparator<BuySideQuotePageEntry> comparator, MainPanel mainPanel) {

		super();

		this.counterpartyTreeDialog = mainPanel.getCounterpartyTreeDialog();
		this.fixInitiatorTreeDialog = mainPanel.getInitiatorTreeDialog();
		this.comparator = comparator;

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		BuySideBook buySideBook = (BuySideBook) buySideQuotePage.getParent();

		if (buySideBook.getCalcMethod() != CalcMethod.NONE)
			showYield = true;
		
		if (buySideBook.getAbsolutChange() != null && buySideBook.getAbsolutChange() == false) {
			absoluteChange = false;
		}

		int priceFractionDigits = 3;

		int yieldFractionDigits = 4;

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideQuotePage.getAssignedBuySideBookSecurities()) {
			if ((!showYield) && assignedBuySideBookSecurity.getCalcMethod() != null && assignedBuySideBookSecurity.getCalcMethod() != CalcMethod.NONE)
				showYield = true;
			
			FSecurity security = mainPanel.getSecurityTreeDialog().getSecurityForId(assignedBuySideBookSecurity.getSecurity().getId());
			if (security != null) {

				Boolean fractionalDisplay = assignedBuySideBookSecurity.getFractionalDisplay();
				if (fractionalDisplay == null)
					fractionalDisplay = buySideBook.getFractionalDisplay();

				if (fractionalDisplay == null || fractionalDisplay == false) {

					if (security != null && security.getSecurityDetails().getMinPriceIncrement() != null) {

						int digits = getDecimalPlaces(security.getSecurityDetails().getMinPriceIncrement());

						if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
							if (digits > yieldFractionDigits)
								yieldFractionDigits = digits;
						}
						else {
							if (digits > priceFractionDigits)
								priceFractionDigits = digits;
						}
					}

				}
				else {

					Double dollarTick = null;

					if (security != null && security.getSecurityDetails() != null) {
						dollarTick = security.getSecurityDetails().getMinPriceIncrement();
					}

					if (dollarTick == null)
						dollarTick = 0.0009765625;

					dollarTickMap.put(assignedBuySideBookSecurity.getSecurity().getId(), dollarTick);
				}

			}
		}

		StringBuffer stringBuffer = new StringBuffer("#,##0.");
		for (int i = 0; i < priceFractionDigits; i++)
			stringBuffer.append("0");

		priceDecimalFormat = new DecimalFormat(stringBuffer.toString());
		priceDecimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		stringBuffer = new StringBuffer("#,##0.");
		for (int i = 0; i < yieldFractionDigits; i++)
			stringBuffer.append("0");

		yieldDecimalFormat = new DecimalFormat(stringBuffer.toString());
		yieldDecimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
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

		if (showYield)
			return 6;
		return 5;
	}

	
	/**
	 * Checks if is show yield.
	 *
	 * @return true, if is show yield
	 */
	public boolean isShowYield() {
	
		return showYield;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {

		if(column>1&&!showYield)
			column++;
		
		switch (column) {

			case 0:
				return "Counterparty (Market)";

			case 1:
				return "Price";

			case 2:
				return "Yield";

			case 3:
				return "Size";

			case 4:
				return "Change";

			case 5:
				return "Updated  ";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return buySideMDInputEntryList.size();
	}

	/**
	 * Gets the buy side quote page entry.
	 *
	 * @param row the row
	 * @return the buy side quote page entry
	 */
	public BuySideQuotePageEntry getBuySideQuotePageEntry(int row) {

		return buySideMDInputEntryList.get(row);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {

		BuySideQuotePageEntry buySideQuotePageEntry = buySideMDInputEntryList.get(rowIndex);

		Counterparty counterparty = null;
		AbstractInitiator fixInitiator = null;

		if (buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
			BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry = (BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry();
			counterparty = counterpartyTreeDialog.getCounterpartyForId(buySideCounterpartyMDInputEntry.getCounterpartyValue());
			fixInitiator = fixInitiatorTreeDialog.getAbstractInitiatorForId(buySideCounterpartyMDInputEntry.getInterfaceValue());
		}

		if(columnIndex>1&&!showYield)
			columnIndex++;
		
		switch (columnIndex) {

			case 0:
				StringBuffer stringBuffer = new StringBuffer();
				if (counterparty != null)
					stringBuffer.append(counterparty.getName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(" (");
				if (fixInitiator != null)
					stringBuffer.append(fixInitiator.getMarketName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(")");
				return stringBuffer.toString();

			case 5:
				if (buySideQuotePageEntry == null || buySideQuotePageEntry.getBuySideMDInputEntry() == null)
					return "-";
				return DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date(buySideQuotePageEntry.getBuySideMDInputEntry().getTime()));

			default:
				return buySideQuotePageEntry;
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

		if(showYield)	
			table.getColumnModel().getColumn(5).setPreferredWidth(minWidth);
		else
			table.getColumnModel().getColumn(4).setPreferredWidth(minWidth);
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
	 * Sets the security.
	 *
	 * @param security the new security
	 */
	public void setSecurity(long security) {

		buySideMDInputEntryList.clear();
		this.security = security;
		fireTableDataChanged();
	}

	/**
	 * Update buy side md input entry.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public void updateBuySideMDInputEntry(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		timestamp = System.currentTimeMillis();

		List<BuySideQuotePageEntry> buySideQuotePageEntries = new ArrayList<BuySideQuotePageEntry>();
		buySideQuotePageEntries.addAll(buySideMDInputEntryList);
		
		for (AbstractBuySideMDInputEntry buySideMDInputEntry : updateBuySideMDInputEntryResponse.getBuySideMDInputEntries()) {
			if (buySideMDInputEntry instanceof BuySideCounterpartyMDInputEntry) {
				boolean updated = false;
				BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry = (BuySideCounterpartyMDInputEntry) buySideMDInputEntry;
				for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryList)
					if (buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
						BuySideCounterpartyMDInputEntry buySideCounterpartyMDInputEntry2 = (BuySideCounterpartyMDInputEntry) buySideQuotePageEntry
								.getBuySideMDInputEntry();
						if (buySideCounterpartyMDInputEntry.getSecurityValue() == buySideCounterpartyMDInputEntry2.getSecurityValue()
								&& buySideCounterpartyMDInputEntry.getCounterpartyValue() == buySideCounterpartyMDInputEntry2.getCounterpartyValue()
								&& buySideCounterpartyMDInputEntry.getInterfaceValue() == buySideCounterpartyMDInputEntry2.getInterfaceValue()) {
							buySideQuotePageEntry.setBuySideMDInputEntry(buySideCounterpartyMDInputEntry);
							updated = true;
						}
					}
				if ((!updated) && buySideCounterpartyMDInputEntry.getSecurityValue() == security) {
					BuySideQuotePageEntry buySideQuotePageEntry = new BuySideQuotePageEntry();
					buySideQuotePageEntry.setBuySideMDInputEntry(buySideMDInputEntry);
					buySideQuotePageEntry.setPriceDecimalFormat(priceDecimalFormat);
					buySideQuotePageEntry.setYieldDecimalFormat(yieldDecimalFormat);
					buySideMDInputEntryList.add(buySideQuotePageEntry);
				}
			}
		}
		
		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);

		int dollarFracOffset = 0;
		int changeFracOffset = 0;
		
		for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryList) {
			AbstractBuySideMDInputEntry buySideMDInputEntry = buySideQuotePageEntry.getBuySideMDInputEntry();
			
			DollarFraction dollarFraction = getDollarFraction(buySideMDInputEntry.getSecurityValue());

			if (buySideMDInputEntry.getMdEntryBidPxValue() != null) {

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdEntryBidPxValue(), true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;

					}
					catch (Exception e) {
					}
				}
			}

			if (buySideMDInputEntry.getMdBidPriceDeltaValue() != null) {
				
				if (dollarFraction != null && absoluteChange ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdBidPriceDeltaValue(), true));
						if (offset > changeFracOffset)
							changeFracOffset = offset;

					}
					catch (Exception e) {
					}
				}

			}

			if (buySideMDInputEntry.getMdEntryAskPxValue() != null) {

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdEntryAskPxValue(), true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;

					}
					catch (Exception e) {
					}
				}

			}

			if (buySideMDInputEntry.getMdAskPriceDeltaValue() != null) {
				
				if (dollarFraction != null&&absoluteChange) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdAskPriceDeltaValue(), true));
						if (offset > changeFracOffset)
							changeFracOffset = offset;

					}
					catch (Exception e) {
					}
				}
				
			}

		}

		for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryList) {
			
			buySideQuotePageEntry.setDollarFracOffset(dollarFracOffset);
			buySideQuotePageEntry.setChangeFracOffset(changeFracOffset);
			
		}


		Collections.sort(buySideMDInputEntryList, comparator);

		fireTableDataChanged();
	}

	/**
	 * Checks if is repaint required.
	 *
	 * @return true, if is repaint required
	 */
	public boolean isRepaintRequired() {

		return System.currentTimeMillis() - timestamp < 2000;
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
	 * Gets the dollar fraction.
	 *
	 * @param security the security
	 * @return the dollar fraction
	 */
	public DollarFraction getDollarFraction(long security) {

		Double dollarTick = dollarTickMap.get(security);

		DollarFraction dollarFraction = null;

		try {
			if (dollarTick != null)
				dollarFraction = new DollarFraction(dollarTick);
		}
		catch (Exception e) {
		}

		return dollarFraction;
	}


}
