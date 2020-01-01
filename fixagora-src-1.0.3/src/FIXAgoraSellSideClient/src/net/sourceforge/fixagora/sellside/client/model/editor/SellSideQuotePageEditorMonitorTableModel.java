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
package net.sourceforge.fixagora.sellside.client.model.editor;

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

import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideQuotePageEditor;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage.SortBy;

/**
 * The Class SellSideQuotePageEditorMonitorTableModel.
 */
public class SellSideQuotePageEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities = new ArrayList<AssignedSellSideBookSecurity>();

	private Map<Long, SellSideQuotePageEntry> sellSideMDInputEntryMap = new HashMap<Long, SellSideQuotePageEntry>();

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	private DecimalFormat priceDecimalFormat = null;
	
	private DecimalFormat yieldDecimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0");

	private long timestamp = 0;

	private int mouseOverRow = -1;

	private int firstColumnWidth = 0;

	private boolean showYield = false;

	private boolean absoluteChange = true;

	/**
	 * Instantiates a new sell side quote page editor monitor table model.
	 *
	 * @param sellSideQuotePageEditor the sell side quote page editor
	 */
	public SellSideQuotePageEditorMonitorTableModel(final SellSideQuotePageEditor sellSideQuotePageEditor) {

		super();

		final SellSideQuotePage sellSideQuotePage = sellSideQuotePageEditor.getSellSideQuotePage();

		assignedSellSideBookSecurities.addAll(sellSideQuotePage.getAssignedSellSideBookSecurities());

		SellSideBook sellSideBook = (SellSideBook) sellSideQuotePage.getParent();

		if (sellSideBook.getAbsolutChange() != null && sellSideBook.getAbsolutChange() == false) {
			absoluteChange = false;
		}

		if (sellSideBook.getCalcMethod() != CalcMethod.NONE)
			showYield = true;

		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = sellSideQuotePageEditor.getMainPanel().getFontMetrics(font);

		firstColumnWidth = fontMetrics.stringWidth("Security" + 10);

		Collections.sort(assignedSellSideBookSecurities, new Comparator<AssignedSellSideBookSecurity>() {

			@Override
			public int compare(AssignedSellSideBookSecurity o1, AssignedSellSideBookSecurity o2) {

				FSecurity security = o1.getSecurity();
				FSecurity security2 = o2.getSecurity();

				if (sellSideQuotePage.getSortBy() == SortBy.SYMBOL || (security.getMaturity() == null && security2.getMaturity() == null))
					return security.compareTo(security2);

				if (security.getMaturity() == null && security2.getMaturity() != null)
					return 1;

				if (security.getMaturity() != null && security2.getMaturity() == null)
					return -1;

				return security.getMaturity().compareTo(security2.getMaturity());
			}

		});

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		int priceFractionDigits = 3;
		
		int yieldFractionDigits = 4;

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : assignedSellSideBookSecurities) {

			FSecurity security = sellSideQuotePageEditor.getMainPanel().getSecurityTreeDialog()
					.getSecurityForId(assignedSellSideBookSecurity.getSecurity().getId());

			if (security != null) {
				int width = fontMetrics.stringWidth(security.getName()) + 10;
				if (width > firstColumnWidth)
					firstColumnWidth = width;
			}

			Boolean fractionalDisplay = assignedSellSideBookSecurity.getFractionalDisplay();
			if (fractionalDisplay == null)
				fractionalDisplay = sellSideBook.getFractionalDisplay();

			if (fractionalDisplay == null || fractionalDisplay == false) {

				if (security != null && security.getSecurityDetails().getMinPriceIncrement() != null) {

					int digits = getDecimalPlaces(security.getSecurityDetails().getMinPriceIncrement());
					
					if(security.getSecurityDetails().getPriceQuoteMethod()!=null&&security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
					{
						if (digits > yieldFractionDigits)
							yieldFractionDigits = digits;
					}
					else
					{
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

				dollarTickMap.put(assignedSellSideBookSecurity.getSecurity().getId(), dollarTick);
			}

			if ((!showYield) && assignedSellSideBookSecurity.getCalcMethod() != null && assignedSellSideBookSecurity.getCalcMethod() != CalcMethod.NONE)
				showYield = true;

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
			return 8;
		return 7;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {

		if (column > 1 && !showYield)
			column++;

		switch (column) {

			case 0:
				return "Security";

			case 1:
				return "Price";

			case 2:
				return "Yield";

			case 3:
				return "Size";

			case 4:
				return "Change";

			case 5:
				return "High";

			case 6:
				return "Low";

			case 7:
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

		return assignedSellSideBookSecurities.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AssignedSellSideBookSecurity assignedSellSideBookSecurity = assignedSellSideBookSecurities.get(rowIndex);

		SellSideQuotePageEntry sellSideQuotePageEntry = sellSideMDInputEntryMap.get(assignedSellSideBookSecurity.getSecurity().getId());

		if (columnIndex > 1 && !showYield)
			columnIndex++;

		switch (columnIndex) {

			case 0:
				return assignedSellSideBookSecurity.getSecurity().getName();

			case 7:
				if (sellSideQuotePageEntry == null || sellSideQuotePageEntry.getSellSideMDInputEntry() == null)
					return "-";
				return DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(
						new Date(sellSideQuotePageEntry.getSellSideMDInputEntry().getTime()));

			default:
				return sellSideQuotePageEntry;
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

		if (!showYield)
			table.getColumnModel().getColumn(6).setPreferredWidth(minWidth);
		else
			table.getColumnModel().getColumn(7).setPreferredWidth(minWidth);
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
	 * Update sell side md input entry.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void updateSellSideMDInputEntry(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		timestamp = System.currentTimeMillis();

		for (SellSideMDInputEntry sellSideMDInputEntry : updateSellSideMDInputEntryResponse.getSellSideMDInputEntries()) {
			SellSideQuotePageEntry sellSideQuotePageEntry = sellSideMDInputEntryMap.get(sellSideMDInputEntry.getSecurityValue());
			if (sellSideQuotePageEntry == null) {
				sellSideQuotePageEntry = new SellSideQuotePageEntry();
				sellSideQuotePageEntry.setPriceDecimalFormat(priceDecimalFormat);
				sellSideQuotePageEntry.setYieldDecimalFormat(yieldDecimalFormat);
				sellSideMDInputEntryMap.put(sellSideMDInputEntry.getSecurityValue(), sellSideQuotePageEntry);
			}
			sellSideQuotePageEntry.setSellSideMDInputEntry(sellSideMDInputEntry);
		}

		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);
		int priceWidth = 25;
		int dollarFracOffset = 0;
		int yieldWidth = 25;
		int changeWidth = 25;
		int changeFracOffset = 0;
		int highWidth = 25;
		int highFracOffset = 0;
		int lowWidth = 25;
		int lowFracOffset = 0;
		int sizeWidth = 25;

		for (SellSideQuotePageEntry sellSideQuotePageEntry : sellSideMDInputEntryMap.values()) {
			SellSideMDInputEntry sellSideMDInputEntry = sellSideQuotePageEntry.getSellSideMDInputEntry();
			
			DollarFraction dollarFraction = getDollarFraction(sellSideMDInputEntry.getSecurityValue());

			if (sellSideMDInputEntry.getMdEntryBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdEntryBidPxValue(), true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdEntryBidPxValue(), 1, true)) + dollarFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdEntryBidPxValue()));

				if (width > priceWidth)
					priceWidth = width;
			}

			if (sellSideMDInputEntry.getMdEntryBidYieldValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(yieldDecimalFormat.format(sellSideMDInputEntry.getMdEntryBidYieldValue()));
				
				if (width > yieldWidth)
					yieldWidth = width;
			}

			if (sellSideMDInputEntry.getMdBidPriceDeltaValue() != null) {
				
				int width = 0;

				if (dollarFraction != null && absoluteChange ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdBidPriceDeltaValue(), true));
						
						if (offset > changeFracOffset)
							changeFracOffset = offset;
						
						width  = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdBidPriceDeltaValue(), 0, true)) - offset + changeFracOffset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					if (!absoluteChange)
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdBidPriceDeltaValue()) + "%");
					else
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdBidPriceDeltaValue()));

				if (sellSideMDInputEntry.getMdBidPriceDeltaValue() > 0)
					width = width + fontMetrics.stringWidth("+");
				if (width > changeWidth)
					changeWidth = width;
			}

			if (sellSideMDInputEntry.getHighBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getHighBidPxValue(),  true));
						if (offset > highFracOffset)
							highFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getHighBidPxValue(),1, true))+ highFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getHighBidPxValue()));
				
				if (width > highWidth)
					highWidth = width;
			}

			if (sellSideMDInputEntry.getLowBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getLowBidPxValue(),  true));
						if (offset > lowFracOffset)
							lowFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getLowBidPxValue(),1, true)) + lowFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getLowBidPxValue()));

				if (width > lowWidth)
					lowWidth = width;
			}

			if (sellSideMDInputEntry.getMdEntryBidSizeValue() != null) {
				int width = 10 + fontMetrics.stringWidth(decimalFormat2.format(sellSideMDInputEntry.getMdEntryBidSizeValue()));
				if (width > sizeWidth)
					sizeWidth = width;
			}

			if (sellSideMDInputEntry.getMdEntryAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdEntryAskPxValue(),  true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdEntryAskPxValue(),1, true)) + dollarFracOffset - offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdEntryAskPxValue()));

				if (width > priceWidth)
					priceWidth = width;
			}

			if (sellSideMDInputEntry.getMdEntryAskYieldValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(yieldDecimalFormat.format(sellSideMDInputEntry.getMdEntryAskYieldValue()));
				
				if (width > yieldWidth)
					yieldWidth = width;
			}

			if (sellSideMDInputEntry.getMdAskPriceDeltaValue() != null) {
				
				int width = 0;

				if (dollarFraction != null&&absoluteChange) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdAskPriceDeltaValue(),  true));
												
						if (offset > changeFracOffset)
							changeFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdAskPriceDeltaValue(), 0, true)) + changeFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					if (!absoluteChange)
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdAskPriceDeltaValue()) + "%");
					else
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getMdAskPriceDeltaValue()));

				if (sellSideMDInputEntry.getMdAskPriceDeltaValue() > 0)
					width = width + fontMetrics.stringWidth("+");
				if (width > changeWidth)
					changeWidth = width;
			}

			if (sellSideMDInputEntry.getHighAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getHighAskPxValue(), true));
						if (offset > highFracOffset)
							highFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getHighAskPxValue(), 1, true)) + highFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getHighAskPxValue()));

				if (width > highWidth)
					highWidth = width;
			}

			if (sellSideMDInputEntry.getLowAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getLowAskPxValue(), true));
						if (offset > lowFracOffset)
							lowFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(sellSideMDInputEntry.getLowAskPxValue(), 1, true)) + lowFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(sellSideMDInputEntry.getLowAskPxValue()));
				
				if (width > lowWidth)
					lowWidth = width;
			}

			if (sellSideMDInputEntry.getMdEntryAskSizeValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(decimalFormat2.format(sellSideMDInputEntry.getMdEntryAskSizeValue()));
				
				if (width > sizeWidth)
					sizeWidth = width;
			}
		}

		for (SellSideQuotePageEntry sellSideQuotePageEntry : sellSideMDInputEntryMap.values()) {
			
			sellSideQuotePageEntry.setPriceWidth(priceWidth);
			if (table.getColumnModel().getColumn(1).getPreferredWidth() < priceWidth * 2 + 15)
				table.getColumnModel().getColumn(1).setPreferredWidth(priceWidth * 2 + 15);
			sellSideQuotePageEntry.setDollarFracOffset(dollarFracOffset);

			sellSideQuotePageEntry.setYieldWidth(yieldWidth);
			if (table.getColumnModel().getColumn(2).getPreferredWidth() < yieldWidth * 2 + 15)
				table.getColumnModel().getColumn(2).setPreferredWidth(yieldWidth * 2 + 15);
			
			sellSideQuotePageEntry.setSizeWidth(sizeWidth);
			if (table.getColumnModel().getColumn(3).getPreferredWidth() < sizeWidth * 2 + 15)
				table.getColumnModel().getColumn(3).setPreferredWidth(sizeWidth * 2 + 15);

			
			sellSideQuotePageEntry.setChangeWidth(changeWidth);
			if (table.getColumnModel().getColumn(4).getPreferredWidth() < changeWidth * 2 + 15)
				table.getColumnModel().getColumn(4).setPreferredWidth(changeWidth * 2 + 15);
			sellSideQuotePageEntry.setChangeFracOffset(changeFracOffset);
			
			
			sellSideQuotePageEntry.setHighWidth(highWidth);
			if (table.getColumnModel().getColumn(5).getPreferredWidth() < highWidth * 2 + 15)
				table.getColumnModel().getColumn(5).setPreferredWidth(highWidth * 2 + 15);
			sellSideQuotePageEntry.setHighFracOffset(highFracOffset);
			
			sellSideQuotePageEntry.setLowWidth(lowWidth);
			if (table.getColumnModel().getColumn(6).getPreferredWidth() < lowWidth * 2 + 15)
				table.getColumnModel().getColumn(6).setPreferredWidth(lowWidth * 2 + 15);
			sellSideQuotePageEntry.setLowFracOffset(lowFracOffset);
			
		}

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
	 * Gets the update sell side md input entry response.
	 *
	 * @return the update sell side md input entry response
	 */
	public UpdateSellSideMDInputEntryResponse getUpdateSellSideMDInputEntryResponse() {

		List<SellSideMDInputEntry> sellSideMDInputEntries = new ArrayList<SellSideMDInputEntry>();

		for (SellSideQuotePageEntry sellSideQuotePageEntry : sellSideMDInputEntryMap.values())
			sellSideMDInputEntries.add(sellSideQuotePageEntry.getSellSideMDInputEntry());

		return new UpdateSellSideMDInputEntryResponse(sellSideMDInputEntries);
	}

	/**
	 * Gets the first column width.
	 *
	 * @return the first column width
	 */
	public int getFirstColumnWidth() {

		return firstColumnWidth;
	}

	/**
	 * Checks if is show yield.
	 *
	 * @return true, if is show yield
	 */
	public boolean isShowYield() {

		return showYield;
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
