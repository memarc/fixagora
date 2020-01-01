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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditor;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;

/**
 * The Class AssignedSellSideSecurityTableModel.
 */
public class AssignedSellSideSecurityTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private SellSideBookEditor sellSideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities = null;

	private boolean modified = false;

	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	/**
	 * Instantiates a new assigned sell side security table model.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public AssignedSellSideSecurityTableModel(final SellSideBookEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		updateAssignedInitiatorSecurities(this.sellSideBookEditor.getSellSideBook().getAssignedSellSideBookSecurities());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 6;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Security";

			case 1:
				return "Valuta";

			case 2:
				return "Bank Calendar";

			case 3:
				return "Daycount Basis";
				
			case 4:
				return "Price/Yield Calculation";
				
			case 5:
				return "Display Price";

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
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedSellSideBookSecurity assignedSellSideBookSecurity = assignedSellSideBookSecurities.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return assignedSellSideBookSecurity.getSecurity().getName();

			case 1:
				if (assignedSellSideBookSecurity.getValuta() != null)
					return "T+" + integerFormat.format(assignedSellSideBookSecurity.getValuta());
				else
					return "T+" + integerFormat.format(assignedSellSideBookSecurity.getSellSideBook().getValuta());

			case 2:

				if (assignedSellSideBookSecurity.getBankCalendar() != null)
					return assignedSellSideBookSecurity.getBankCalendar().getName();
				else
					return assignedSellSideBookSecurity.getSellSideBook().getBankCalendar().getName();

			case 3:
				Integer daycount = assignedSellSideBookSecurity.getDaycountConvention();
				if (daycount == null)
					daycount = assignedSellSideBookSecurity.getSellSideBook().getDaycountConvention();

				switch (daycount) {
					case 1:
						return "Actual/actual";
					case 2:
						return "Actual/360";
					case 3:
						return "Actual/365";
					case 4:
						return "European 30/360";
					default:
						return "US (NASD) 30/360";
				}
				
			case 4:
				CalcMethod calcMethod = assignedSellSideBookSecurity.getCalcMethod();
				if (calcMethod == null)
					calcMethod = assignedSellSideBookSecurity.getSellSideBook().getCalcMethod();

				switch (calcMethod) {
					case DEFAULT:
						return "PRICE / YIELD";
					case DISC:
						return "PRICEDISC / YIELDDISC";
					case MAT:
						return "PRICEMAT / YIELDMAT";
					case NONE:
						return "None";
					case ODDF:
						return "ODDFPRICE / ODDFYIELD";
					case ODDL:
						return "ODDLPRICE / ODDLYIELD";
					case TBILL:
						return "TBILLPRICE / TBILLYIELD";
					default:
						return "???";
				}
				
			case 5:
				Boolean displayFraction = assignedSellSideBookSecurity.getFractionalDisplay();
				if (displayFraction == null)
					displayFraction = assignedSellSideBookSecurity.getSellSideBook().getFractionalDisplay();

				
				if(displayFraction==null||displayFraction==false)
					return "Decimal";
				else
					return "Fraction";
				
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

		table.getColumnModel().getColumn(5).setPreferredWidth(minWidth);
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
	 * Gets the abstract business object editor.
	 *
	 * @return the abstract business object editor
	 */
	public AbstractBusinessObjectEditor getAbstractBusinessObjectEditor() {

		return sellSideBookEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && assignedSellSideBookSecurities.containsAll(sellSideBookEditor.getSellSideBook().getAssignedSellSideBookSecurities())
				&& sellSideBookEditor.getSellSideBook().getAssignedSellSideBookSecurities().containsAll(assignedSellSideBookSecurities);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		sellSideBookEditor.getSellSideBook().setAssignedSellSideBookSecurities(assignedSellSideBookSecurities);
	}

	/**
	 * Update assigned initiator securities.
	 *
	 * @param list the list
	 */
	public void updateAssignedInitiatorSecurities(List<AssignedSellSideBookSecurity> list) {

		this.assignedSellSideBookSecurities = new ArrayList<AssignedSellSideBookSecurity>();

		if (list != null)
			this.assignedSellSideBookSecurities.addAll(list);

		modified = false;
		Collections.sort(assignedSellSideBookSecurities);
		fireTableDataChanged();
	}

	/**
	 * Adds the assigned sell side book security.
	 *
	 * @param assignedSellSideBookSecurity the assigned sell side book security
	 */
	public void addAssignedSellSideBookSecurity(AssignedSellSideBookSecurity assignedSellSideBookSecurity) {

		if (!isDuplicate(assignedSellSideBookSecurity)) {
			assignedSellSideBookSecurities.add(assignedSellSideBookSecurity);
			Collections.sort(assignedSellSideBookSecurities);
			fireTableDataChanged();
		}
	}

	/**
	 * Removes the.
	 *
	 * @param assignedSellSideBookSecurity the assigned sell side book security
	 */
	public void remove(AssignedSellSideBookSecurity assignedSellSideBookSecurity) {

		assignedSellSideBookSecurities.remove(assignedSellSideBookSecurity);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned sell side book security
	 */
	public AssignedSellSideBookSecurity get(int index) {

		return assignedSellSideBookSecurities.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}

	private boolean isDuplicate(AssignedSellSideBookSecurity assignedSellSideBookSecurity) {

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity2 : assignedSellSideBookSecurities)
			if (assignedSellSideBookSecurity != assignedSellSideBookSecurity2
					&& assignedSellSideBookSecurity.getSecurity().getId() == assignedSellSideBookSecurity2.getSecurity().getId())
				return true;
		
		return false;

	}

}
