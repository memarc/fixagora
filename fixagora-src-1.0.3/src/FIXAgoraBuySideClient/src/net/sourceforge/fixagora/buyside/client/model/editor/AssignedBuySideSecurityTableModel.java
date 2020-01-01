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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;

/**
 * The Class AssignedBuySideSecurityTableModel.
 */
public class AssignedBuySideSecurityTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private BuySideBookEditor buySideBookEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities = null;

	private boolean modified = false;

	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	/**
	 * Instantiates a new assigned buy side security table model.
	 *
	 * @param buySideBookEditor the buy side book editor
	 */
	public AssignedBuySideSecurityTableModel(final BuySideBookEditor buySideBookEditor) {

		super();

		this.buySideBookEditor = buySideBookEditor;

		updateAssignedInitiatorSecurities(this.buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities());

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

		return assignedBuySideBookSecurities.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		AssignedBuySideBookSecurity assignedBuySideBookSecurity = assignedBuySideBookSecurities.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return assignedBuySideBookSecurity.getSecurity().getName();

			case 1:
				if (assignedBuySideBookSecurity.getValuta() != null)
					return "T+" + integerFormat.format(assignedBuySideBookSecurity.getValuta());
				else
					return "T+" + integerFormat.format(assignedBuySideBookSecurity.getBuySideBook().getValuta());

			case 2:

				if (assignedBuySideBookSecurity.getBankCalendar() != null)
					return assignedBuySideBookSecurity.getBankCalendar().getName();
				else
					return assignedBuySideBookSecurity.getBuySideBook().getBankCalendar().getName();

			case 3:
				Integer daycount = assignedBuySideBookSecurity.getDaycountConvention();
				if (daycount == null)
					daycount = assignedBuySideBookSecurity.getBuySideBook().getDaycountConvention();

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
				CalcMethod calcMethod = assignedBuySideBookSecurity.getCalcMethod();
				if (calcMethod == null)
					calcMethod = assignedBuySideBookSecurity.getBuySideBook().getCalcMethod();

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
				Boolean displayFraction = assignedBuySideBookSecurity.getFractionalDisplay();
				if (displayFraction == null)
					displayFraction = assignedBuySideBookSecurity.getBuySideBook().getFractionalDisplay();
				
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

		return buySideBookEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && assignedBuySideBookSecurities.containsAll(buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities())
				&& buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities().containsAll(assignedBuySideBookSecurities);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		buySideBookEditor.getBuySideBook().setAssignedBuySideBookSecurities(assignedBuySideBookSecurities);
	}

	/**
	 * Update assigned initiator securities.
	 *
	 * @param list the list
	 */
	public void updateAssignedInitiatorSecurities(List<AssignedBuySideBookSecurity> list) {

		this.assignedBuySideBookSecurities = new ArrayList<AssignedBuySideBookSecurity>();

		if (list != null)
			this.assignedBuySideBookSecurities.addAll(list);

		Collections.sort(assignedBuySideBookSecurities);

		modified = false;

		fireTableDataChanged();
	}

	/**
	 * Adds the assigned buy side book security.
	 *
	 * @param assignedBuySideBookSecurity the assigned buy side book security
	 */
	public void addAssignedBuySideBookSecurity(AssignedBuySideBookSecurity assignedBuySideBookSecurity) {

		if (!isDuplicate(assignedBuySideBookSecurity)) {
			assignedBuySideBookSecurities.add(assignedBuySideBookSecurity);
			fireTableDataChanged();
		}
	}

	/**
	 * Removes the.
	 *
	 * @param assignedBuySideBookSecurity the assigned buy side book security
	 */
	public void remove(AssignedBuySideBookSecurity assignedBuySideBookSecurity) {

		assignedBuySideBookSecurities.remove(assignedBuySideBookSecurity);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the assigned buy side book security
	 */
	public AssignedBuySideBookSecurity get(int index) {

		return assignedBuySideBookSecurities.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}

	private boolean isDuplicate(AssignedBuySideBookSecurity assignedBuySideBookSecurity) {

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity2 : assignedBuySideBookSecurities)
			if (assignedBuySideBookSecurity != assignedBuySideBookSecurity2
					&& assignedBuySideBookSecurity.getSecurity().getId() == assignedBuySideBookSecurity2.getSecurity().getId())
				return true;

		return false;

	}

}
