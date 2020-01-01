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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry.Side;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;

/**
 * The Class SellSideBookEditorMonitorTableModel.
 */
public class SellSideBookEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 100;

	private JTable table = null;

	private List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities = new ArrayList<AssignedSellSideBookSecurity>();

	private DecimalFormat priceDecimalFormat = null;

	private DecimalFormat yieldDecimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private int mouseOverRow = -1;

	private List<AbstractSellSideEntry> abstractSellSideEntries = new ArrayList<AbstractSellSideEntry>();

	private MainPanel mainPanel = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<AbstractSellSideEntry> updated = new HashSet<AbstractSellSideEntry>();
	
	private int limitOffset = 0;
	
	private int lastOffset = 0;

	private Comparator<AbstractSellSideEntry> comparator = new Comparator<AbstractSellSideEntry>() {

		@Override
		public int compare(AbstractSellSideEntry o1, AbstractSellSideEntry o2) {

			if (o1.getCreated() < o2.getCreated())
				return 1;
			return -1;
		}
	};

	private int priceFractionDigits = 3;

	private int yieldFractionDigits = 4;

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	private DecimalFormatSymbols decimalFormatSymbols = null;

	private boolean showYield = false;

	/**
	 * Instantiates a new sell side book editor monitor table model.
	 *
	 * @param sellSideBook the sell side book
	 * @param mainPanel the main panel
	 */
	public SellSideBookEditorMonitorTableModel(final SellSideBook sellSideBook, MainPanel mainPanel) {

		super();

		this.mainPanel = mainPanel;

		assignedSellSideBookSecurities.addAll(sellSideBook.getAssignedSellSideBookSecurities());

		decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		if (sellSideBook.getCalcMethod() != CalcMethod.NONE)
			showYield = true;

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : assignedSellSideBookSecurities) {
			FSecurity fSecurity = mainPanel.getSecurityTreeDialog().getSecurityForId(assignedSellSideBookSecurity.getSecurity().getId());

			Boolean fractionalDisplay = assignedSellSideBookSecurity.getFractionalDisplay();
			if (fractionalDisplay == null)
				fractionalDisplay = sellSideBook.getFractionalDisplay();

			if (fractionalDisplay == null || fractionalDisplay == false) {

				if (fSecurity != null && fSecurity.getSecurityDetails().getMinPriceIncrement() != null) {
					
					int digits = getDecimalPlaces(fSecurity.getSecurityDetails().getMinPriceIncrement());

					if (fSecurity.getSecurityDetails().getPriceQuoteMethod() != null && fSecurity.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
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

				if (fSecurity != null && fSecurity.getSecurityDetails() != null) {
					dollarTick = fSecurity.getSecurityDetails().getMinPriceIncrement();
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
			return 14;
		return 13;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {

		if (column > 5 && !showYield)
			column++;

		switch (column) {

			case 0:
				return "Status";

			case 1:
				return "Security";

			case 2:
				return "Counterparty (Market)";

			case 3:
				return "Side";

			case 4:
				return "Limit";

			case 5:
				return "Last Price";

			case 6:
				return "Last Yield";

			case 7:
				return "Last Quantity";

			case 8:
				return "Cumulative Quantity";

			case 9:
				return "Order Quantity";

			case 10:
				return "Time in Force";

			case 11:
				return "Owner";

			case 12:
				return "Updated";

			case 13:
				return "Created";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return abstractSellSideEntries.size();
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
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {

		AbstractSellSideEntry abstractSellSideEntry = abstractSellSideEntries.get(rowIndex);

		if (columnIndex > 5 && !showYield)
			columnIndex++;

		switch (columnIndex) {

			case 0:
				if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {
					switch (((SellSideNewOrderSingleEntry) abstractSellSideEntry).getOrderStatus()) {

						case CANCEL:
							if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
								return "Canceled (Partially Filled)";
							}
							else {
								return "Canceled";
							}

						case DONE_FOR_DAY:
							if (abstractSellSideEntry.getLeaveQuantity() > 0) {
								return "Done for Day";
							}
							else {
								if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
									return "Done (Partially Filled)";
								}
								else {
									return "Done (Zero Filled)";
								}
							}
						case NEW:
							if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
								return "Partially Filled";
							}
							else
								return "New Order";

						case FILLED:
							return "Done (Filled)";

						case FILLED_PENDING:
							return "Done (Filled Pending)";

						case PARTIALLY_FILLED:
							return "Partially Filled";

						case PARTIALLY_FILLED_PENDING:
							return "Partially Filled Pending";

						case REJECTED:
							if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
								return "Done (Partially Filled)";
							}
							else {
								return "Rejected";
							}
						case REJECTED_PENDING:
							if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
								return "Done (Partially Filled Pending)";
							}
							else {
								return "Rejected Pending";
							}
						case DONE_FOR_DAY_PENDING:
							if (abstractSellSideEntry.getLeaveQuantity() > 0) {
								return "Done for Day Pending";
							}
							else {
								if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
									return "Done (Partially Filled Pending)";
								}
								else {
									return "Done (Zero Filled Pending)";
								}
							}
						case MODIFIED:
							if (abstractSellSideEntry.getCumulativeQuantity() > 0) {
								return "Modified (Partially Filled)";
							}
							else {
								return "Modified";
							}

					}
					return "Unknown";
				}
				if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry) {
					switch (((SellSideQuoteRequestEntry) abstractSellSideEntry).getOrderStatus()) {
						case COUNTER:
							return "Buy Side Countered "
									+ getTimeLabel(((SellSideQuoteRequestEntry) abstractSellSideEntry).getExpireDate() - System.currentTimeMillis());
						case COVER:
							return "Covered";
						case DONE_AWAY:
							return "Done Away";
						case EXPIRED:
							return "Expired";
						case FILLED:
							if (abstractSellSideEntry.getLeaveQuantity() > 0) {
								return "Done (Partially Filled)";
							}
							else {
								return "Done (Filled)";
							}
						case FILLED_PENDING:
							return "Done (Filled Pending)";
						case HIT_LIFT:
							if (abstractSellSideEntry.getSide() == Side.BID)
								return "Hit " + getTimeLabel(((SellSideQuoteRequestEntry) abstractSellSideEntry).getExpireDate() - System.currentTimeMillis());
							else
								return "Lift " + getTimeLabel(((SellSideQuoteRequestEntry) abstractSellSideEntry).getExpireDate() - System.currentTimeMillis());
						case NEW:
							return "New Inquiry "
									+ getTimeLabel(((SellSideQuoteRequestEntry) abstractSellSideEntry).getExpireDate() - System.currentTimeMillis());
						case PASS:
							return "Pass";
						case QUOTED:
							Long subject = ((SellSideQuoteRequestEntry) abstractSellSideEntry).getSubjectDate();
							if (subject != null && subject > System.currentTimeMillis()) {
								return "Firm Quote " + getTimeLabel(subject - System.currentTimeMillis());
							}
							else {
								return "Subject Quote "
										+ getTimeLabel(((SellSideQuoteRequestEntry) abstractSellSideEntry).getExpireDate() - System.currentTimeMillis());
							}
						case QUOTED_PENDING:
							return "Quote (Pending)";
						case REJECTED:
							return "Rejected";
						case REJECTED_PENDING:
							return "Rejected (Pending)";
					}
					return "Unknown";
				}

			case 1:
				FSecurity security = mainPanel.getSecurityTreeDialog().getSecurityForId(abstractSellSideEntry.getSecurity());
				if (security != null)
					return security.getName();
				else
					return "Unknown";

			case 2:
				StringBuffer stringBuffer = new StringBuffer();
				Counterparty counterparty = mainPanel.getCounterpartyTreeDialog().getCounterpartyForId(abstractSellSideEntry.getCounterparty());
				if (counterparty != null)
					stringBuffer.append(counterparty.getName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(" (");
				AbstractAcceptor fixAcceptor = mainPanel.getAcceptorTreeDialog().getAbstractAcceptorForId(abstractSellSideEntry.getMarketInterface());
				if (fixAcceptor != null)
					stringBuffer.append(fixAcceptor.getMarketName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(")");
				return stringBuffer.toString();

			case 3:
				if (abstractSellSideEntry.getSide() == Side.ASK)
					return "Ask (you sell)";
				return "Bid (you buy)";

			case 4:

				if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry
						&& (abstractSellSideEntry.getLimit() == null || ((SellSideQuoteRequestEntry) abstractSellSideEntry).getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.NEW))
					return "Inquiry";

				if (abstractSellSideEntry.getLimit() != null)
				{
					
					FSecurity security1 = mainPanel.getSecurityTreeDialog().getSecurityForId(abstractSellSideEntry.getSecurity());
					if (security1 != null&&security1.getSecurityDetails()!=null&&security1.getSecurityDetails().getPriceQuoteMethod()!=null&&security1.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
						return yieldDecimalFormat.format(abstractSellSideEntry.getLimit());	
					
					Double dollarTick = dollarTickMap.get(abstractSellSideEntry.getSecurity());
					if(dollarTick!=null)
					{
						try {
							DollarFraction dollarFraction = new DollarFraction(dollarTick);
							return dollarFraction.getDollarPrice(abstractSellSideEntry.getLimit(),1,true);
						}
						catch (Exception e) {
						}
					}
					
					return priceDecimalFormat.format(abstractSellSideEntry.getLimit());
				}

				return "Best";

			case 5:
				if (abstractSellSideEntry.getLastPrice() != null)
				{
					Double dollarTick = dollarTickMap.get(abstractSellSideEntry.getSecurity());
					if(dollarTick!=null)
					{
						try {
							DollarFraction dollarFraction = new DollarFraction(dollarTick);
							return dollarFraction.getDollarPrice(abstractSellSideEntry.getLastPrice(),1,true);
						}
						catch (Exception e) {
						}
					}
					return priceDecimalFormat.format(abstractSellSideEntry.getLastPrice());
				}
				return "";

			case 6:
				if (abstractSellSideEntry.getLastYield() != null)
					return yieldDecimalFormat.format(abstractSellSideEntry.getLastYield());
				return "";

			case 7:
				if (abstractSellSideEntry.getLastQuantity() != null)
					return decimalFormat2.format(abstractSellSideEntry.getLastQuantity());
				return "";
			case 8:
				if (abstractSellSideEntry.getCumulativeQuantity() > 0)
					return decimalFormat2.format(abstractSellSideEntry.getCumulativeQuantity());
				return "";
			case 9:
				return decimalFormat2.format(abstractSellSideEntry.getOrderQuantity());

			case 10:
				if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {
					SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = (SellSideNewOrderSingleEntry) abstractSellSideEntry;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(sellSideNewOrderSingleEntry.getTifDate());
					StringBuffer stringBuffer2 = new StringBuffer();
					switch (sellSideNewOrderSingleEntry.getTimeInForce()) {
						case FILL_OR_KILL:
							stringBuffer2.append("Fill Or Kill");
							if (abstractSellSideEntry.getSettlementDate() != null) {
								stringBuffer2.append(" (Settlement ");
								stringBuffer2.append(simpleDateFormat.format(new Date(abstractSellSideEntry.getSettlementDate())));
								stringBuffer2.append(")");
							}
							return stringBuffer2.toString();
						case GOOD_TILL_CANCEL:
							return "Good Till Cancel";
						case GOOD_TILL_DATE:
							stringBuffer2.append("Good Till ");
							stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
							return stringBuffer2.toString();
						case GOOD_TILL_DAY:
							stringBuffer2.append("Good Till Day");
							if (abstractSellSideEntry.getSettlementDate() != null) {
								stringBuffer2.append(" (Settlement ");
								stringBuffer2.append(simpleDateFormat.format(new Date(abstractSellSideEntry.getSettlementDate())));
								stringBuffer2.append(")");
							}
							return stringBuffer2.toString();
					}
				}
				else if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry) {
					SellSideQuoteRequestEntry sellSideQuoteRequestEntry = (SellSideQuoteRequestEntry) abstractSellSideEntry;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(sellSideQuoteRequestEntry.getSettlementDate());
					StringBuffer stringBuffer2 = new StringBuffer();
					stringBuffer2.append("Settlement ");
					stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
					return stringBuffer2.toString();
				}
				return "Unknown";

			case 11:
				return abstractSellSideEntry.getUser();

			case 12:
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTimeInMillis(abstractSellSideEntry.getUpdated());
				return DateFormat.getTimeInstance().format(calendar2.getTime()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(calendar2.getTime());

			case 13:
				Calendar calendar3 = Calendar.getInstance();
				calendar3.setTimeInMillis(abstractSellSideEntry.getCreated());
				return DateFormat.getTimeInstance().format(calendar3.getTime()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(calendar3.getTime());
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

		if (!showYield)
			table.getColumnModel().getColumn(12).setPreferredWidth(minWidth);
		else
			table.getColumnModel().getColumn(13).setPreferredWidth(minWidth);
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final JTable table) {

		this.table = table;
		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);
		table.getColumnModel().getColumn(0).setPreferredWidth(fontMetrics.stringWidth("Done (Partially Filled Pending)") + 20);
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
	 * @param row the row
	 * @return true, if successful
	 */
	public boolean setMouseOverRow(int row) {

		if (row == mouseOverRow)
			return false;

		mouseOverRow = row;

		return true;

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
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public synchronized void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = newOrderSingleResponse.getSellSideNewOrderSingleEntry();

		if (sellSideNewOrderSingleEntry == null)
			return;

		List<AbstractSellSideEntry> abstractSellSideEntries2 = new ArrayList<AbstractSellSideEntry>();
		abstractSellSideEntries2.addAll(abstractSellSideEntries);
		for (AbstractSellSideEntry abstractSellSideEntry2 : abstractSellSideEntries2) {
			if (abstractSellSideEntry2.getOrderId() != null && abstractSellSideEntry2.getOrderId().equals(sellSideNewOrderSingleEntry.getOrderId())) {

				updated.remove(abstractSellSideEntry2);
				abstractSellSideEntries.remove(abstractSellSideEntry2);

			}
		}

		updated.add(sellSideNewOrderSingleEntry);

		abstractSellSideEntries.add(sellSideNewOrderSingleEntry);

		Collections.sort(abstractSellSideEntries, comparator);

		adjust();

		fireTableDataChanged();
	}

	/**
	 * On open sell side book response.
	 *
	 * @param openSellSideBookResponse the open sell side book response
	 */
	public synchronized void onOpenSellSideBookResponse(OpenSellSideBookResponse openSellSideBookResponse) {

		abstractSellSideEntries.addAll(openSellSideBookResponse.getAbstractSellSideEntries());

		Collections.sort(abstractSellSideEntries, comparator);

		adjust();

		fireTableDataChanged();

	}
	
	/**
	 * Gets the limit offset.
	 *
	 * @param text the text
	 * @return the limit offset
	 */
	public int getLimitOffset(String text)
	{
		if(text==null)
			return 0;
		
		if(text.equals("Inquiry"))
			return 0;
		
		if(text.equals("Best"))
			return 0;
		
		return limitOffset-getFractionOffset(text);
	}
	
	/**
	 * Gets the last offset.
	 *
	 * @param text the text
	 * @return the last offset
	 */
	public int getLastOffset(String text)
	{
		if(text==null)
			return 0;
		
		return lastOffset-getFractionOffset(text);
	}
	
	private int getFractionOffset(String text)
	{
		
		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);
		
		if(text.indexOf('.')!=-1)
			return fontMetrics.stringWidth(text.substring(text.indexOf('.')));
		else if(text.lastIndexOf('-')!=-1)
			return fontMetrics.stringWidth(text.substring(text.indexOf('-')));
		return 0;
	}

	private void adjust() {

		StringBuffer stringBuffer = new StringBuffer("#,##0.");
		for (int k = 0; k < priceFractionDigits; k++)
			stringBuffer.append("0");

		priceDecimalFormat = new DecimalFormat(stringBuffer.toString());
		priceDecimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);

		for (int i = 1; i < getColumnCount(); i++) {
			int width = fontMetrics.stringWidth(getColumnName(i)) + 12;
			for (int j = 0; j < abstractSellSideEntries.size(); j++) {
				Object object = getValueAt(j, i);
				if (object != null) {
					int width2 = fontMetrics.stringWidth(object.toString()) + 12;
					
					if(i==4)
					{
						int offset = getFractionOffset(object.toString());
						if(offset>limitOffset)
							limitOffset = offset;
						
						if(offset>0)
							width2 = width2 + limitOffset - offset;
						
						
					}
					
					if(i==5)
					{
						int offset = getFractionOffset(object.toString());
						if(offset>lastOffset)
							lastOffset = offset;
						
						if(offset>0)
							width2 = width2 + lastOffset - offset;
					}
					
					if (width2 > width)
						width = width2;
				}
			}
			if (i < getColumnCount() - 1)
				table.getColumnModel().getColumn(i).setPreferredWidth(width);
			else {
				table.getColumnModel().getColumn(i).setMinWidth(width);
				if (width > minWidth)
					setMinWidth(width);
			}
		}

	}

	/**
	 * Gets the entry for row.
	 *
	 * @return the entry for row
	 */
	public AbstractSellSideEntry getEntryForRow() {

		if (mouseOverRow != -1 && abstractSellSideEntries.size() > mouseOverRow)
			return abstractSellSideEntries.get(mouseOverRow);
		return null;
	}

	/**
	 * Checks if is updated.
	 *
	 * @param row the row
	 * @return true, if is updated
	 */
	public boolean isUpdated(int row) {

		return updated.contains(abstractSellSideEntries.get(row));
	}

	/**
	 * Reset update.
	 */
	public synchronized void resetUpdate() {

		updated.clear();
		fireTableDataChanged();

	}

	/**
	 * Checks if is canceled.
	 *
	 * @param row the row
	 * @return true, if is canceled
	 */
	public boolean isCanceled(int row) {

		if (row < abstractSellSideEntries.size()) {
			AbstractSellSideEntry abstractSellSideEntry = abstractSellSideEntries.get(row);
			if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {
				SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = (SellSideNewOrderSingleEntry) abstractSellSideEntry;
				if (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL
						|| sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED_PENDING
						|| sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED) {
					return true;
				}
			}
			if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry) {
				SellSideQuoteRequestEntry sellSideQuoteRequestEntry = (SellSideQuoteRequestEntry) abstractSellSideEntry;
				if (sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.COVER
						|| sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.DONE_AWAY
						|| sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.EXPIRED
						|| sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.PASS
						|| sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED
						|| sellSideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry.OrderStatus.REJECTED_PENDING) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if is complete filled.
	 *
	 * @param row the row
	 * @return true, if is complete filled
	 */
	public boolean isCompleteFilled(int row) {

		if (row < abstractSellSideEntries.size()) {
			AbstractSellSideEntry abstractSellSideEntry = abstractSellSideEntries.get(row);
			if (abstractSellSideEntry.getCumulativeQuantity() > 0 && abstractSellSideEntry.getLeaveQuantity() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * On sell side quote request response.
	 *
	 * @param sellSideQuoteRequestResponse the sell side quote request response
	 */
	public void onSellSideQuoteRequestResponse(SellSideQuoteRequestResponse sellSideQuoteRequestResponse) {

		SellSideQuoteRequestEntry sellSideQuoteRequestEntry = sellSideQuoteRequestResponse.getSellSideQuoteRequestEntry();

		if (sellSideQuoteRequestEntry == null)
			return;

		List<AbstractSellSideEntry> abstractSellSideEntries2 = new ArrayList<AbstractSellSideEntry>();
		abstractSellSideEntries2.addAll(abstractSellSideEntries);
		for (AbstractSellSideEntry abstractSellSideEntry2 : abstractSellSideEntries2) {
			if (abstractSellSideEntry2 instanceof SellSideQuoteRequestEntry) {
				SellSideQuoteRequestEntry sellSideQuoteRequestEntry2 = (SellSideQuoteRequestEntry) abstractSellSideEntry2;
				if (sellSideQuoteRequestEntry2.getQuoteReqId().equals(sellSideQuoteRequestEntry.getQuoteReqId())) {

					updated.remove(abstractSellSideEntry2);
					abstractSellSideEntries.remove(abstractSellSideEntry2);

				}
			}
		}

		updated.add(sellSideQuoteRequestEntry);

		abstractSellSideEntries.add(sellSideQuoteRequestEntry);

		Collections.sort(abstractSellSideEntries, comparator);

		adjust();

		fireTableDataChanged();
	}

	/**
	 * Gets the time label.
	 *
	 * @param time the time
	 * @return the time label
	 */
	public static String getTimeLabel(long time) {

		StringBuffer stringBuffer = new StringBuffer();

		if (time >= 0) {

			time = time / 1000;

			while (time > 0) {

				long value = time % 60;
				stringBuffer.insert(0, value);

				if (value < 10)
					stringBuffer.insert(0, "0");

				time = time / 60;

				if (time > 0)
					stringBuffer.insert(0, ":");
			}

			if (stringBuffer.length() == 0)
				stringBuffer.append("00");
			if (stringBuffer.indexOf(":") == -1)
				stringBuffer.insert(0, "00:");
		}

		return stringBuffer.toString();
	}

}
