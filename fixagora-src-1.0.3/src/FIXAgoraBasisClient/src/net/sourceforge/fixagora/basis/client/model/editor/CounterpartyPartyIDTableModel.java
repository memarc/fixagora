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
package net.sourceforge.fixagora.basis.client.model.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.CounterpartyEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;

/**
 * The Class CounterpartyPartyIDTableModel.
 */
public class CounterpartyPartyIDTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private CounterpartyEditor counterPartyEditor = null;

	private int minWidth = 200;

	private JTable table = null;

	private List<CounterPartyPartyID> counterPartyPartyIDs = null;

	private boolean modified = false;

	/**
	 * Instantiates a new counterparty party id table model.
	 *
	 * @param counterPartyEditor the counter party editor
	 */
	public CounterpartyPartyIDTableModel(final CounterpartyEditor counterPartyEditor) {

		super();

		this.counterPartyEditor = counterPartyEditor;

		updateCounterPartyPartyIDs(this.counterPartyEditor.getCounterparty().getCounterPartyPartyIDs());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Business Component";

			case 1:
				return "Party ID";

			case 2:
				return "Party ID Source";

			case 3:
				return "Party Role";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return counterPartyPartyIDs.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		CounterPartyPartyID counterPartyPartyID = counterPartyPartyIDs.get(rowIndex);
		switch (columnIndex) {
			case 1:
				return counterPartyPartyID.getPartyID();

			case 2:
				if (counterPartyPartyID.getPartyIDSource() == null)
					return "not used";
				else if (counterPartyPartyID.getPartyIDSource().equals("B"))
					return "BIC (Bank Identification Code)";
				else if (counterPartyPartyID.getPartyIDSource().equals("C"))
					return "Generally accepted market participant identifier";
				else if (counterPartyPartyID.getPartyIDSource().equals("D"))
					return "Proprietary / Custom code";
				else if (counterPartyPartyID.getPartyIDSource().equals("E"))
					return "ISO Country Code";
				else if (counterPartyPartyID.getPartyIDSource().equals("F"))
					return "Settlement Entity Location";
				else if (counterPartyPartyID.getPartyIDSource().equals("G"))
					return "MIC (ISO 10383 - Market Identificer Code)";
				else if (counterPartyPartyID.getPartyIDSource().equals("H"))
					return "CSD participant/member code";
				else if (counterPartyPartyID.getPartyIDSource().equals("6"))
					return "UK National Insurance or Pension Number";
				else if (counterPartyPartyID.getPartyIDSource().equals("7"))
					return "US Social Security Number";
				else if (counterPartyPartyID.getPartyIDSource().equals("8"))
					return "US Employer or Tax ID Number";
				else if (counterPartyPartyID.getPartyIDSource().equals("9"))
					return "Australian Business Number";
				else if (counterPartyPartyID.getPartyIDSource().equals("A"))
					return "Australian Tax File Number";
				else if (counterPartyPartyID.getPartyIDSource().equals("I"))
					return "Directed broker three character acronym as defined in ISITC";
				else if (counterPartyPartyID.getPartyIDSource().equals("1"))
					return "Korean Investor ID";
				else if (counterPartyPartyID.getPartyIDSource().equals("2"))
					return "Taiwanese Qualified Foreign Investor ID QFII/FID";
				else if (counterPartyPartyID.getPartyIDSource().equals("3"))
					return "Taiwanese Trading Acct";
				else if (counterPartyPartyID.getPartyIDSource().equals("4"))
					return "Malaysian Central Depository (MCD) number";
				else if (counterPartyPartyID.getPartyIDSource().equals("5"))
					return "Chinese Investor ID";
				else
					return "unknown";

			case 3:
				if (counterPartyPartyID.getPartyRole() == null)
					return "all";
				else
					switch (counterPartyPartyID.getPartyRole()) {
						case 1:
							return "Executing Firm ";
						case 2:
							return "Broker of Credit";
						case 3:
							return "Client ID";
						case 4:
							return "Clearing Firm";
						case 5:
							return "Investor ID";
						case 6:
							return "Introducing Firm";
						case 7:
							return "Entering Firm";
						case 8:
							return "Locate / Lending Firm";
						case 9:
							return "Fund Manager Client ID";
						case 10:
							return "Settlement Location";
						case 11:
							return "Order Origination Trader";
						case 12:
							return "Executing Trader";
						case 13:
							return "Order Origination Firm";
						case 14:
							return "Giveup Clearing Firm";
						case 15:
							return "Correspondant Clearing Firm";
						case 16:
							return "Executing System";
						case 17:
							return "Contra Firm";
						case 18:
							return "Contra Clearing Firm";
						case 19:
							return "Sponsoring Firm";
						case 20:
							return "Underlying Contra Firm";
						case 21:
							return "Clearing Organization";
						case 22:
							return "Exchange";
						case 24:
							return "Customer Account";
						case 25:
							return "Correspondent Clearing Organization";
						case 26:
							return "Correspondent Broker";
						case 27:
							return "Buyer/Seller (Receiver/Deliverer)";
						case 28:
							return "Custodian";
						case 29:
							return "Intermediary";
						case 30:
							return "Agent";
						case 31:
							return "Sub-custodian";
						case 32:
							return "Beneficiary";
						case 33:
							return "Interested party";
						case 34:
							return "Regulatory body";
						case 35:
							return "Liquidity provider";
						case 36:
							return "Entering trader";
						case 37:
							return "Contra trader";
						case 38:
							return "Position account";
						case 39:
							return "Contra Investor ID";
						case 40:
							return "Transfer to Firm";
						case 41:
							return "Contra Position Account";
						case 42:
							return "Contra Exchange";
						case 43:
							return "Internal Carry Account";
						case 44:
							return "Order Entry Operator ID";
						case 45:
							return "Secondary Account Number";
						case 46:
							return "Foreign Firm";
						case 47:
							return "Third Party Allocation Firm";
						case 48:
							return "Claiming Account";
						case 49:
							return "Asset Manager";
						case 50:
							return "Pledgor Account";
						case 51:
							return "Pledgee Account";
						case 52:
							return "Large Trader Reportable Account";
						case 53:
							return "Trader mnemonic";
						case 54:
							return "Sender Location";
						case 55:
							return "Session ID";
						case 56:
							return "Acceptable Counterparty";
						case 57:
							return "Unacceptable Counterparty";
						case 58:
							return "Entering Unit";
						case 59:
							return "Executing Unit";
						case 60:
							return "Introducing Broker";
						case 61:
							return "Quote originator";
						case 62:
							return "Report originator";
						case 63:
							return "Systematic internaliser";
						case 64:
							return "Multilateral Trading Facility";
						case 65:
							return "Regulated Market";
						case 66:
							return "Market Maker";
						case 67:
							return "Investment Firm";
						case 68:
							return "Host Competent Authority";
						case 69:
							return "Home Competent Authority";
						case 70:
							return "Competent Authority of the most relevant market in terms of liquidity";
						case 71:
							return "Competent Authority of the Transaction (Execution) Venue";
						case 72:
							return "Reporting intermediary";
						case 73:
							return "Execution Venue";
						case 74:
							return "Market data entry originator";
						case 75:
							return "Location ID";
						case 76:
							return "Desk ID";
						case 77:
							return "Market data market";
						case 78:
							return "Allocation Entity";
						case 79:
							return "Prime Broker providing General Trade Services";
						case 80:
							return "Step-Out Firm (Prime Broker)";
						case 81:
							return "BrokerClearingID";
						case 82:
							return "Central Registration Depository";
						case 83:
							return "Clearing Account";
						case 84:
							return "Acceptable Settling Counterparty";
						case 85:
							return "Unacceptable Settling Counterparty";
						default:
							return "unknown";
					}

			default:
				if(counterPartyPartyID.getAbstractBusinessComponent()==null)
					return "Default";
				return counterPartyPartyID.getAbstractBusinessComponent().getName();
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

		table.getColumnModel().getColumn(3).setPreferredWidth(minWidth);
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

		return counterPartyEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && counterPartyPartyIDs.containsAll(counterPartyEditor.getCounterparty().getCounterPartyPartyIDs())
				&& counterPartyEditor.getCounterparty().getCounterPartyPartyIDs().containsAll(counterPartyPartyIDs);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		counterPartyEditor.getCounterparty().setCounterPartyPartyIDs(counterPartyPartyIDs);
	}

	/**
	 * Update counter party party i ds.
	 *
	 * @param list the list
	 */
	public void updateCounterPartyPartyIDs(List<CounterPartyPartyID> list) {

		this.counterPartyPartyIDs = new ArrayList<CounterPartyPartyID>();

		if (list != null)
			this.counterPartyPartyIDs.addAll(list);

		modified = false;

		checkDoubleEntries();

		fireTableDataChanged();
	}

	/**
	 * Adds the counter party party id.
	 *
	 * @param counterPartyPartyID the counter party party id
	 */
	public void addCounterPartyPartyID(CounterPartyPartyID counterPartyPartyID) {

		counterPartyPartyIDs.add(counterPartyPartyID);
		checkDoubleEntries();
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param counterPartyPartyID the counter party party id
	 */
	public void remove(CounterPartyPartyID counterPartyPartyID) {

		counterPartyPartyIDs.remove(counterPartyPartyID);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the counter party party id
	 */
	public CounterPartyPartyID get(int index) {

		return counterPartyPartyIDs.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		checkDoubleEntries();
		modified = true;
	}
	
	private boolean optionalValueCheck(Object value, Object value2) {

		if (value != null && value2 == null)
			return false;

		else if (value == null && value2 != null)
			return false;

		else if (value == null && value2 == null)
			return true;

		return value.equals(value2);
	}

	private void checkDoubleEntries() {

		List<CounterPartyPartyID> counterPartyPartyIDs2 = new ArrayList<CounterPartyPartyID>();
		counterPartyPartyIDs2.addAll(this.counterPartyPartyIDs);
		for (CounterPartyPartyID counterPartyPartyID : counterPartyPartyIDs2) {
			List<CounterPartyPartyID> counterPartyPartyIDs3 = new ArrayList<CounterPartyPartyID>();
			counterPartyPartyIDs3.addAll(this.counterPartyPartyIDs);
			for (CounterPartyPartyID counterPartyPartyID2 : counterPartyPartyIDs3)
				if (counterPartyPartyID != counterPartyPartyID2
				&& counterPartyPartyID.getAbstractBusinessComponent()==null && counterPartyPartyID2.getAbstractBusinessComponent()==null)
					counterPartyPartyIDs.remove(counterPartyPartyID);
				else if (counterPartyPartyID != counterPartyPartyID2
						&& counterPartyPartyID.getAbstractBusinessComponent()!=null && counterPartyPartyID2.getAbstractBusinessComponent()!=null
						&& counterPartyPartyID.getAbstractBusinessComponent().getId() == counterPartyPartyID2.getAbstractBusinessComponent().getId()
						&& counterPartyPartyID.getCounterparty().getId() == counterPartyPartyID2.getCounterparty().getId()
						&& optionalValueCheck(counterPartyPartyID.getPartyRole(),counterPartyPartyID2.getPartyRole())
						&& optionalValueCheck(counterPartyPartyID.getPartyIDSource(),counterPartyPartyID2.getPartyIDSource()))
					counterPartyPartyIDs.remove(counterPartyPartyID);
		}

		Collections.sort(counterPartyPartyIDs);

	}

}
