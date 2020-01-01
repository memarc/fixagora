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
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCompositeMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage.SortBy;

/**
 * The Class BuySideQuotePageEditorCompositeTableModel.
 */
public class BuySideQuotePageEditorCompositeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities = new ArrayList<AssignedBuySideBookSecurity>();

	private Map<Long, BuySideQuotePageEntry> buySideMDInputEntryMap = new HashMap<Long, BuySideQuotePageEntry>();

	private DecimalFormat priceDecimalFormat = null;

	private DecimalFormat yieldDecimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0");

	private long timestamp = 0;

	private int mouseOverRow = -1;

	private int firstColumnWidth = 0;

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	private boolean showYield = false;
	
	private boolean absoluteChange = true;

	/**
	 * Instantiates a new buy side quote page editor composite table model.
	 *
	 * @param buySideQuotePage the buy side quote page
	 * @param mainPanel the main panel
	 */
	public BuySideQuotePageEditorCompositeTableModel(final BuySideQuotePage buySideQuotePage, MainPanel mainPanel) {

		super();

		assignedBuySideBookSecurities.addAll(buySideQuotePage.getAssignedBuySideBookSecurities());

		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = mainPanel.getFontMetrics(font);

		firstColumnWidth = fontMetrics.stringWidth("Security" + 10);

		Collections.sort(assignedBuySideBookSecurities, new Comparator<AssignedBuySideBookSecurity>() {

			@Override
			public int compare(AssignedBuySideBookSecurity o1, AssignedBuySideBookSecurity o2) {

				FSecurity security = o1.getSecurity();
				FSecurity security2 = o2.getSecurity();

				if (buySideQuotePage.getSortBy() == SortBy.SYMBOL || (security.getMaturity() == null && security2.getMaturity() == null))
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

		BuySideBook buySideBook = (BuySideBook) buySideQuotePage.getParent();
		
		if (buySideBook.getAbsolutChange() != null && buySideBook.getAbsolutChange() == false) {
			absoluteChange = false;
		}

		if (buySideBook.getCalcMethod() != CalcMethod.NONE)
			showYield = true;

		int priceFractionDigits = 3;

		int yieldFractionDigits = 4;

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : assignedBuySideBookSecurities) {
			
			if ((!showYield) && assignedBuySideBookSecurity.getCalcMethod() != null && assignedBuySideBookSecurity.getCalcMethod() != CalcMethod.NONE)
				showYield = true;
			
			FSecurity security = mainPanel.getSecurityTreeDialog().getSecurityForId(assignedBuySideBookSecurity.getSecurity().getId());
			if (security != null) {
				int width = fontMetrics.stringWidth(security.getName()) + 10;
				if (width > firstColumnWidth)
					firstColumnWidth = width;

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

		return assignedBuySideBookSecurities.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {

		AssignedBuySideBookSecurity assignedBuySideBookSecurity = assignedBuySideBookSecurities.get(rowIndex);

		BuySideQuotePageEntry buySideQuotePageEntry = buySideMDInputEntryMap.get(assignedBuySideBookSecurity.getSecurity().getId());

		if (columnIndex > 1 && !showYield)
			columnIndex++;

		switch (columnIndex) {

			case 0:
				return assignedBuySideBookSecurity.getSecurity().getName();

			case 7:
				if (buySideQuotePageEntry == null || buySideQuotePageEntry.getBuySideMDInputEntry() == null)
					return "-";
				return DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date(buySideQuotePageEntry.getBuySideMDInputEntry().getTime()));

			default:
				return buySideQuotePageEntry;
		}
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

		if (showYield)
			table.getColumnModel().getColumn(7).setPreferredWidth(minWidth);
		else
			table.getColumnModel().getColumn(6).setPreferredWidth(minWidth);
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
	 * Update buy side md input entry.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public synchronized void updateBuySideMDInputEntry(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		timestamp = System.currentTimeMillis();

		for (AbstractBuySideMDInputEntry buySideMDInputEntry : updateBuySideMDInputEntryResponse.getBuySideMDInputEntries()) {
			if (buySideMDInputEntry instanceof BuySideCompositeMDInputEntry) {
				BuySideQuotePageEntry buySideQuotePageEntry = buySideMDInputEntryMap.get(buySideMDInputEntry.getSecurityValue());
				if (buySideQuotePageEntry == null) {
					buySideQuotePageEntry = new BuySideQuotePageEntry();
					buySideQuotePageEntry.setPriceDecimalFormat(priceDecimalFormat);
					buySideQuotePageEntry.setYieldDecimalFormat(yieldDecimalFormat);
					buySideMDInputEntryMap.put(buySideMDInputEntry.getSecurityValue(), buySideQuotePageEntry);
				}
				buySideQuotePageEntry.setBuySideMDInputEntry(buySideMDInputEntry);
			}
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

		for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryMap.values()) {
			BuySideCompositeMDInputEntry buySideMDInputEntry = (BuySideCompositeMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry();
			
			DollarFraction dollarFraction = getDollarFraction(buySideMDInputEntry.getSecurityValue());

			if (buySideMDInputEntry.getMdEntryBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdEntryBidPxValue(), true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getMdEntryBidPxValue(), 1, true)) + dollarFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdEntryBidPxValue()));

				if (width > priceWidth)
					priceWidth = width;
			}

			if (buySideMDInputEntry.getMdEntryBidYieldValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(yieldDecimalFormat.format(buySideMDInputEntry.getMdEntryBidYieldValue()));
				
				if (width > yieldWidth)
					yieldWidth = width;
			}

			if (buySideMDInputEntry.getMdBidPriceDeltaValue() != null) {
				
				int width = 0;

				if (dollarFraction != null && absoluteChange ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdBidPriceDeltaValue(), true));
						
						if (offset > changeFracOffset)
							changeFracOffset = offset;
						
						width  = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getMdBidPriceDeltaValue(), 0, true)) - offset + changeFracOffset;

					}
					catch (Exception e) {
					}
				}

				if (width==0)
					if (!absoluteChange)
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdBidPriceDeltaValue()) + "%");
					else
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdBidPriceDeltaValue()));

				if (buySideMDInputEntry.getMdBidPriceDeltaValue() > 0)
					width = width + fontMetrics.stringWidth("+");
				if (width > changeWidth)
					changeWidth = width;
			}

			if (buySideMDInputEntry.getHighBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getHighBidPxValue(),  true));
						if (offset > highFracOffset)
							highFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getHighBidPxValue(),1, true))+ highFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getHighBidPxValue()));
				
				if (width > highWidth)
					highWidth = width;
			}

			if (buySideMDInputEntry.getLowBidPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getLowBidPxValue(),  true));
						if (offset > lowFracOffset)
							lowFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getLowBidPxValue(),1, true)) + lowFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getLowBidPxValue()));

				if (width > lowWidth)
					lowWidth = width;
			}

			if (buySideMDInputEntry.getMdEntryBidSizeValue() != null) {
				int width = 10 + fontMetrics.stringWidth(decimalFormat2.format(buySideMDInputEntry.getMdEntryBidSizeValue()));
				if (width > sizeWidth)
					sizeWidth = width;
			}

			if (buySideMDInputEntry.getMdEntryAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdEntryAskPxValue(),  true));
						if (offset > dollarFracOffset)
							dollarFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getMdEntryAskPxValue(),1, true)) + dollarFracOffset - offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdEntryAskPxValue()));

				if (width > priceWidth)
					priceWidth = width;
			}

			if (buySideMDInputEntry.getMdEntryAskYieldValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(yieldDecimalFormat.format(buySideMDInputEntry.getMdEntryAskYieldValue()));
				
				if (width > yieldWidth)
					yieldWidth = width;
			}

			if (buySideMDInputEntry.getMdAskPriceDeltaValue() != null) {
				
				int width = 0;

				if (dollarFraction != null&&absoluteChange) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdAskPriceDeltaValue(),  true));
												
						if (offset > changeFracOffset)
							changeFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getMdAskPriceDeltaValue(), 0, true)) + changeFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					if (!absoluteChange)
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdAskPriceDeltaValue()) + "%");
					else
						width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getMdAskPriceDeltaValue()));

				if (buySideMDInputEntry.getMdAskPriceDeltaValue() > 0)
					width = width + fontMetrics.stringWidth("+");
				if (width > changeWidth)
					changeWidth = width;
			}

			if (buySideMDInputEntry.getHighAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getHighAskPxValue(), true));
						if (offset > highFracOffset)
							highFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getHighAskPxValue(), 1, true)) + highFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getHighAskPxValue()));

				if (width > highWidth)
					highWidth = width;
			}

			if (buySideMDInputEntry.getLowAskPxValue() != null) {
				
				int width = 0;

				if (dollarFraction != null ) {
					try {

						int offset = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getLowAskPxValue(), true));
						if (offset > lowFracOffset)
							lowFracOffset = offset;
						
						width = 10 + fontMetrics.stringWidth(dollarFraction.getDollarPrice(buySideMDInputEntry.getLowAskPxValue(), 1, true)) + lowFracOffset -offset;

					}
					catch (Exception e) {
					}
				}

				if (width == 0)
					width = 10 + fontMetrics.stringWidth(priceDecimalFormat.format(buySideMDInputEntry.getLowAskPxValue()));
				
				if (width > lowWidth)
					lowWidth = width;
			}

			if (buySideMDInputEntry.getMdEntryAskSizeValue() != null) {
				
				int width = 10 + fontMetrics.stringWidth(decimalFormat2.format(buySideMDInputEntry.getMdEntryAskSizeValue()));
				
				if (width > sizeWidth)
					sizeWidth = width;
			}
		}

		for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryMap.values()) {
			
			buySideQuotePageEntry.setPriceWidth(priceWidth);
			if (table.getColumnModel().getColumn(1).getPreferredWidth() < priceWidth * 2 + 15)
				table.getColumnModel().getColumn(1).setPreferredWidth(priceWidth * 2 + 15);
			buySideQuotePageEntry.setDollarFracOffset(dollarFracOffset);

			buySideQuotePageEntry.setYieldWidth(yieldWidth);
			if (table.getColumnModel().getColumn(2).getPreferredWidth() < yieldWidth * 2 + 15)
				table.getColumnModel().getColumn(2).setPreferredWidth(yieldWidth * 2 + 15);
			
			buySideQuotePageEntry.setSizeWidth(sizeWidth);
			if (table.getColumnModel().getColumn(3).getPreferredWidth() < sizeWidth * 2 + 15)
				table.getColumnModel().getColumn(3).setPreferredWidth(sizeWidth * 2 + 15);

			
			buySideQuotePageEntry.setChangeWidth(changeWidth);
			if (table.getColumnModel().getColumn(4).getPreferredWidth() < changeWidth * 2 + 15)
				table.getColumnModel().getColumn(4).setPreferredWidth(changeWidth * 2 + 15);
			
			buySideQuotePageEntry.setChangeFracOffset(changeFracOffset);
			
			
			buySideQuotePageEntry.setHighWidth(highWidth);
			if (table.getColumnModel().getColumn(5).getPreferredWidth() < highWidth * 2 + 15)
				table.getColumnModel().getColumn(5).setPreferredWidth(highWidth * 2 + 15);
			buySideQuotePageEntry.setHighFracOffset(highFracOffset);
			
			buySideQuotePageEntry.setLowWidth(lowWidth);
			if (table.getColumnModel().getColumn(6).getPreferredWidth() < lowWidth * 2 + 15)
				table.getColumnModel().getColumn(6).setPreferredWidth(lowWidth * 2 + 15);
			buySideQuotePageEntry.setLowFracOffset(lowFracOffset);
			
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
	 * Gets the security for row.
	 *
	 * @param row the row
	 * @return the security for row
	 */
	public FSecurity getSecurityForRow(int row) {

		if (assignedBuySideBookSecurities.size() > row)
			return assignedBuySideBookSecurities.get(row).getSecurity();
		return null;
	}

	/**
	 * Gets the update buy side md input entry response.
	 *
	 * @return the update buy side md input entry response
	 */
	public synchronized UpdateBuySideMDInputEntryResponse getUpdateBuySideMDInputEntryResponse() {

		List<AbstractBuySideMDInputEntry> buySideMDInputEntries = new ArrayList<AbstractBuySideMDInputEntry>();

		for (BuySideQuotePageEntry buySideQuotePageEntry : buySideMDInputEntryMap.values())
			buySideMDInputEntries.add(buySideQuotePageEntry.getBuySideMDInputEntry());

		return new UpdateBuySideMDInputEntryResponse(buySideMDInputEntries);
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
