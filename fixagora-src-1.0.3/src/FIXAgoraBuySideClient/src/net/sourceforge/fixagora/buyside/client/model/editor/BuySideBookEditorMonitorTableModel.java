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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;

/**
 * The Class BuySideBookEditorMonitorTableModel.
 */
public class BuySideBookEditorMonitorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private int minWidth = 100;

	private JTable table = null;

	private List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities = new ArrayList<AssignedBuySideBookSecurity>();

	private DecimalFormat priceDecimalFormat = null;

	private DecimalFormat yieldDecimalFormat = null;

	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");

	private int mouseOverRow = -1;

	private List<AbstractBuySideEntry> abstractBuySideEntries = new ArrayList<AbstractBuySideEntry>();

	private MainPanel mainPanel = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private Set<AbstractBuySideEntry> updated = new HashSet<AbstractBuySideEntry>();

	private boolean showYield = false;

	private int priceFractionDigits = 3;

	private int yieldFractionDigits = 4;
	
	private int limitOffset = 0;
	
	private int lastOffset = 0;

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	private Comparator<AbstractBuySideEntry> comparator = new Comparator<AbstractBuySideEntry>() {

		@Override
		public int compare(AbstractBuySideEntry o1, AbstractBuySideEntry o2) {

			if (o1.getCreated() < o2.getCreated())
				return 1;
			if (o1.getCreated() == o2.getCreated()) {
				AbstractBusinessObject counterparty1 = mainPanel.getAbstractBusinessObjectForId(o1.getCounterparty());
				AbstractBusinessObject counterparty2 = mainPanel.getAbstractBusinessObjectForId(o2.getCounterparty());
				if (counterparty1 != null && counterparty2 != null)
					return counterparty1.getName().compareTo(counterparty2.getName());
				return 1;
			}
			return -1;
		}
	};

	/**
	 * Instantiates a new buy side book editor monitor table model.
	 *
	 * @param buySideBook the buy side book
	 * @param mainPanel the main panel
	 */
	public BuySideBookEditorMonitorTableModel(final BuySideBook buySideBook, MainPanel mainPanel) {

		super();

		this.mainPanel = mainPanel;

		assignedBuySideBookSecurities.addAll(buySideBook.getAssignedBuySideBookSecurities());

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');

		if (buySideBook.getCalcMethod() != CalcMethod.NONE)
			showYield = true;

		for (AssignedBuySideBookSecurity assignedSellSideBookSecurity : assignedBuySideBookSecurities) {
			FSecurity fSecurity = mainPanel.getSecurityTreeDialog().getSecurityForId(assignedSellSideBookSecurity.getSecurity().getId());

			Boolean fractionalDisplay = assignedSellSideBookSecurity.getFractionalDisplay();
			if (fractionalDisplay == null)
				fractionalDisplay = buySideBook.getFractionalDisplay();

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

	/**
	 * Checks if is show yield.
	 *
	 * @return true, if is show yield
	 */
	public boolean isShowYield() {

		return showYield;
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

		return abstractBuySideEntries.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {

		AbstractBuySideEntry abstractBuySideEntry = abstractBuySideEntries.get(rowIndex);

		if (columnIndex > 5 && !showYield)
			columnIndex++;

		switch (columnIndex) {

			case 0:
				if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry) {
					switch (((BuySideNewOrderSingleEntry) abstractBuySideEntry).getOrderStatus()) {
						case CANCEL:
							if (abstractBuySideEntry.getCumulativeQuantity() > 0) {
								return "Canceled (Partially Filled)";
							}
							else {
								return "Canceled";
							}
						case CANCEL_PENDING:
							return "Cancel (Pending)";
						case DONE_FOR_DAY:
							if (abstractBuySideEntry.getLeaveQuantity() > 0) {
								return "Done for Day";
							}
							else {
								if (abstractBuySideEntry.getCumulativeQuantity() > 0) {
									return "Done (Partially Filled)";
								}
								else {
									return "Done (Zero Filled)";
								}
							}
						case NEW:
							if (abstractBuySideEntry.getCumulativeQuantity() > 0) {
								return "Partially Filled";
							}
							else
								return "New Order";
						case NEW_PENDING:
							return "New Order (Pending)";
						case FILLED:
							return "Done (Filled)";
						case PARTIALLY_FILLED:
							return "Partially Filled";
						case REJECTED:
							if (abstractBuySideEntry.getCumulativeQuantity() > 0) {
								return "Done (Partially Filled)";
							}
							else {
								return "Rejected";
							}
						case REPLACE_PENDING:
							return "Replace (Pending)";
					}

				}

				if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {

					switch (((BuySideQuoteRequestEntry) abstractBuySideEntry).getOrderStatus()) {
						case COUNTER:
							return "Counter";
						case COUNTER_PENDING:
							return "Counter (Pending)";
						case FILLED:
							if (abstractBuySideEntry.getLeaveQuantity() > 0) {
								return "Done (Partially Filled)";
							}
							else {
								return "Done (Filled)";
							}
						case HIT_LIFT:
							if (abstractBuySideEntry.getSide() == Side.BID)
								return "Hit";
							else
								return "Lift";
						case HIT_LIFT_PENDING:
							if (abstractBuySideEntry.getSide() == Side.BID)
								return "Hit (Pending)";
							else
								return "Lift (Pending)";
						case NEW:
							return "New Inquiry "
									+ getTimeLabel(((BuySideQuoteRequestEntry) abstractBuySideEntry).getExpireDate() - System.currentTimeMillis());
						case NEW_PENDING:
							return "New Inquiry (Pending)";
						case REJECTED:
							return "Rejected";
						case PASS:
							return "Pass";
						case PASS_ALL_PENDING:
						case PASS_PENDING:
							return "Pass (Pending)";
						case QUOTED:
							Long subject = ((BuySideQuoteRequestEntry) abstractBuySideEntry).getSubjectDate();
							if (subject != null && subject > System.currentTimeMillis()) {
								return "Firm Quote " + getTimeLabel(subject - System.currentTimeMillis());
							}
							else {
								return "Subject Quote "
										+ getTimeLabel(((BuySideQuoteRequestEntry) abstractBuySideEntry).getExpireDate() - System.currentTimeMillis());
							}
						case COVER:
							return "Cover";
						case COVER_PENDING:
							return "Cover (Pending)";
						case DONE_AWAY:
							return "Done Away";
						case DONE_AWAY_PENDING:
							return "Done Away (Pending)";
						case EXPIRED:
							return "Expired";
						case EXPIRED_PENDING:
							return "Expired (Pending)";
					}
				}
				return "Unknown";

			case 1:
				FSecurity security = mainPanel.getSecurityTreeDialog().getSecurityForId(abstractBuySideEntry.getSecurity());
				if (security != null)
					return security.getName();
				else
					return "Unknown";

			case 2:
				StringBuffer stringBuffer = new StringBuffer();
				Counterparty counterparty = mainPanel.getCounterpartyTreeDialog().getCounterpartyForId(abstractBuySideEntry.getCounterparty());
				if (counterparty != null)
					stringBuffer.append(counterparty.getName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(" (");
				AbstractInitiator fixInitiator = mainPanel.getInitiatorTreeDialog().getAbstractInitiatorForId(abstractBuySideEntry.getMarketInterface());
				if (fixInitiator != null)
					stringBuffer.append(fixInitiator.getMarketName());
				else
					stringBuffer.append("Unknown");
				stringBuffer.append(")");
				return stringBuffer.toString();

			case 3:
				if (abstractBuySideEntry.getSide() == Side.ASK)
					return "Ask (you buy)";
				return "Bid (you sell)";

			case 4:
				if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry
						&& ((BuySideNewOrderSingleEntry) abstractBuySideEntry).getOrderStatus() == OrderStatus.REPLACE_PENDING) {
					if (((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewLimit() != null) {
						
						FSecurity security1 = mainPanel.getSecurityTreeDialog().getSecurityForId(abstractBuySideEntry.getSecurity());
						if (security1 != null&&security1.getSecurityDetails()!=null&&security1.getSecurityDetails().getPriceQuoteMethod()!=null&&security1.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							return yieldDecimalFormat.format(((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewLimit());	
						
						Double dollarTick = dollarTickMap.get(abstractBuySideEntry.getSecurity());
						if (dollarTick != null) {
							try {
								DollarFraction dollarFraction = new DollarFraction(dollarTick);
								return dollarFraction.getDollarPrice(((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewLimit(), 1 , true);
							}
							catch (Exception e) {
							}
						}

						return priceDecimalFormat.format(((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewLimit());
					}

					return "Best";
				}
				else {
					if (abstractBuySideEntry.getLimit() != null)
					{
						
						FSecurity security1 = mainPanel.getSecurityTreeDialog().getSecurityForId(abstractBuySideEntry.getSecurity());
						if (security1 != null&&security1.getSecurityDetails()!=null&&security1.getSecurityDetails().getPriceQuoteMethod()!=null&&security1.getSecurityDetails().getPriceQuoteMethod().equals("INT"))
							return yieldDecimalFormat.format(abstractBuySideEntry.getLimit());	
						
						Double dollarTick = dollarTickMap.get(abstractBuySideEntry.getSecurity());
						if (dollarTick != null) {
							try {
								DollarFraction dollarFraction = new DollarFraction(dollarTick);
								return dollarFraction.getDollarPrice(abstractBuySideEntry.getLimit(), 1 , true);
							}
							catch (Exception e) {
							}
						}

						return priceDecimalFormat.format(abstractBuySideEntry.getLimit());
						
					}

					return "Inquiry";
				}

			case 5:
				if (abstractBuySideEntry.getLastPrice() != null)
				{
					Double dollarTick = dollarTickMap.get(abstractBuySideEntry.getSecurity());
					if (dollarTick != null) {
						try {
							DollarFraction dollarFraction = new DollarFraction(dollarTick);
							return dollarFraction.getDollarPrice(abstractBuySideEntry.getLastPrice() , 1, true);
						}
						catch (Exception e) {
						}
					}
					
					return priceDecimalFormat.format(abstractBuySideEntry.getLastPrice());
				}
				return "";

			case 6:
				if (abstractBuySideEntry.getLastYield() != null)
					return yieldDecimalFormat.format(abstractBuySideEntry.getLastYield());
				return "";

			case 7:
				if (abstractBuySideEntry.getLastQuantity() != null)
					return decimalFormat2.format(abstractBuySideEntry.getLastQuantity());
				return "";
			case 8:
				if (abstractBuySideEntry.getCumulativeQuantity() > 0)
					return decimalFormat2.format(abstractBuySideEntry.getCumulativeQuantity());
				return "";
			case 9:
				if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry
						&& ((BuySideNewOrderSingleEntry) abstractBuySideEntry).getOrderStatus() == OrderStatus.REPLACE_PENDING) {
					if (((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewOrderQuantity() != null)
						return decimalFormat2.format(((BuySideNewOrderSingleEntry) abstractBuySideEntry).getNewOrderQuantity());
					return "Unknown";
				}
				else
					return decimalFormat2.format(abstractBuySideEntry.getOrderQuantity());

			case 10:
				if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry) {
					BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = (BuySideNewOrderSingleEntry) abstractBuySideEntry;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(buySideNewOrderSingleEntry.getTifDate());
					StringBuffer stringBuffer2 = new StringBuffer();
					switch (buySideNewOrderSingleEntry.getTimeInForce()) {
						case FILL_OR_KILL:
							stringBuffer2.append("Fill Or Kill (Settlement ");
							stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
							stringBuffer2.append(")");
							return stringBuffer2.toString();
						case GOOD_TILL_CANCEL:
							return "Good Till Cancel";
						case GOOD_TILL_DATE:
							stringBuffer2.append("Good Till ");
							stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
							return stringBuffer2.toString();
						case GOOD_TILL_DAY:
							stringBuffer2.append("Good Till Day (Settlement ");
							stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
							stringBuffer2.append(")");
							return stringBuffer2.toString();
					}
				}
				else if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {
					BuySideQuoteRequestEntry buySideQuoteRequestEntry = (BuySideQuoteRequestEntry) abstractBuySideEntry;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(buySideQuoteRequestEntry.getSettlementDate());
					StringBuffer stringBuffer2 = new StringBuffer();
					stringBuffer2.append("Settlement ");
					stringBuffer2.append(simpleDateFormat.format(calendar.getTime()));
					return stringBuffer2.toString();
				}
				return "Unknown";

			case 11:
				return abstractBuySideEntry.getUser();

			case 12:
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTimeInMillis(abstractBuySideEntry.getUpdated());
				return DateFormat.getTimeInstance().format(calendar2.getTime()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(calendar2.getTime());

			case 13:
				Calendar calendar3 = Calendar.getInstance();
				calendar3.setTimeInMillis(abstractBuySideEntry.getCreated());
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

		if (showYield)
			table.getColumnModel().getColumn(13).setPreferredWidth(minWidth);
		else
			table.getColumnModel().getColumn(12).setPreferredWidth(minWidth);
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
		table.getColumnModel().getColumn(0).setPreferredWidth(fontMetrics.stringWidth("Canceled (Partially Filled)") + 20);
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

		BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = newOrderSingleResponse.getBuySideNewOrderSingleEntry();
		List<AbstractBuySideEntry> abstractBuySideEntries2 = new ArrayList<AbstractBuySideEntry>();
		abstractBuySideEntries2.addAll(abstractBuySideEntries);
		for (AbstractBuySideEntry abstractBuySideEntry : abstractBuySideEntries2) {
			if (abstractBuySideEntry.getOrderId() != null
					&& (abstractBuySideEntry.getOrderId().equals(buySideNewOrderSingleEntry.getOrderId()) || abstractBuySideEntry.getOrderId().equals(
							buySideNewOrderSingleEntry.getOriginalOrderId()))) {

				updated.remove(abstractBuySideEntry);
				abstractBuySideEntries.remove(abstractBuySideEntry);

			}
		}

		updated.add(buySideNewOrderSingleEntry);
		abstractBuySideEntries.add(buySideNewOrderSingleEntry);
		Collections.sort(abstractBuySideEntries, comparator);

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

		Font font = new Font("Dialog", Font.PLAIN, 12);
		FontMetrics fontMetrics = table.getFontMetrics(font);

		for (int i = 1; i < getColumnCount(); i++) {
			int width = fontMetrics.stringWidth(getColumnName(i)) + 12;
			for (int j = 0; j < abstractBuySideEntries.size(); j++) {
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
	 * On open buy side book response.
	 *
	 * @param openBuyBookResponse the open buy book response
	 */
	public void onOpenBuySideBookResponse(OpenBuySideBookResponse openBuyBookResponse) {

		abstractBuySideEntries.addAll(openBuyBookResponse.getAbstractBuySideEntries());

		Collections.sort(abstractBuySideEntries, comparator);

		adjust();

		fireTableDataChanged();

	}

	/**
	 * Gets the entry for row.
	 *
	 * @return the entry for row
	 */
	public AbstractBuySideEntry getEntryForRow() {

		if (mouseOverRow != -1 && abstractBuySideEntries.size() > mouseOverRow)
			return abstractBuySideEntries.get(mouseOverRow);
		return null;
	}

	/**
	 * Checks if is updated.
	 *
	 * @param row the row
	 * @return true, if is updated
	 */
	public boolean isUpdated(int row) {

		return updated.contains(abstractBuySideEntries.get(row));
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

		if (row < abstractBuySideEntries.size()) {

			AbstractBuySideEntry abstractBuySideEntry = abstractBuySideEntries.get(row);

			if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry) {

				BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = (BuySideNewOrderSingleEntry) abstractBuySideEntry;
				if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL_PENDING
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED) {
					return true;
				}
			}

			if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {

				BuySideQuoteRequestEntry buySideQuoteRequestEntry = (BuySideQuoteRequestEntry) abstractBuySideEntry;
				if (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.PASS_PENDING
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COVER_PENDING
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.DONE_AWAY_PENDING
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.EXPIRED_PENDING
						|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.REJECTED) {
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

		if (row < abstractBuySideEntries.size()) {
			AbstractBuySideEntry abstractBuySideEntry = abstractBuySideEntries.get(row);
			if (abstractBuySideEntry.getCumulativeQuantity() > 0 && abstractBuySideEntry.getLeaveQuantity() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * On buy side quote request response.
	 *
	 * @param quoteRequestResponse the quote request response
	 */
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse) {

		for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : quoteRequestResponse.getBuySideQuoteRequestEntries()) {

			List<AbstractBuySideEntry> abstractBuySideEntries2 = new ArrayList<AbstractBuySideEntry>();
			abstractBuySideEntries2.addAll(abstractBuySideEntries);
			for (AbstractBuySideEntry abstractBuySideEntry : abstractBuySideEntries2) {
				if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {
					BuySideQuoteRequestEntry buySideQuoteRequestEntry2 = (BuySideQuoteRequestEntry) abstractBuySideEntry;
					if (buySideQuoteRequestEntry.getQuoteReqId().equals(buySideQuoteRequestEntry2.getQuoteReqId())) {

						updated.remove(abstractBuySideEntry);
						abstractBuySideEntries.remove(abstractBuySideEntry);

					}
				}
			}
			updated.add(buySideQuoteRequestEntry);
			abstractBuySideEntries.add(buySideQuoteRequestEntry);

		}

		Collections.sort(abstractBuySideEntries, comparator);

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
