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
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDInput;

import org.apache.poi.hssf.util.CellReference;

/**
 * The Class SpreadSheetMDInputTableModel.
 */
public class SpreadSheetMDInputTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private SpreadSheetEditor spreadSheetEditor = null;

	private int minWidth = 100;

	private JTable table = null;

	private List<SpreadSheetMDInput> spreadSheetMDInputs = null;

	private boolean modified = false;

	/**
	 * Instantiates a new spread sheet md input table model.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetMDInputTableModel(final SpreadSheetEditor spreadSheetEditor) {

		super();

		this.spreadSheetEditor = spreadSheetEditor;

		updateSpreadSheetMDInputs(this.spreadSheetEditor.getSpreadSheet().getSpreadSheetMDInputs());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 12;
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
				return "Security";

			case 2:
				return "Counterparty";
				
			case 3:
				return "MD Entry Type";
				
			case 4:
				return "Px";
				
			case 5:
				return "Size";
				
			case 6:
				return "Date";
				
			case 7:
				return "Time";
				
			case 8:
				return "Delta";
				
			case 9:
				return "Trade Volume";
				
			case 10:
				return "High Px";
				
			case 11:
				return "Low Px";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return spreadSheetMDInputs.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SpreadSheetMDInput spreadSheetMDInput = spreadSheetMDInputs.get(rowIndex);

		switch (columnIndex) {
			
			case 0:
				return spreadSheetMDInput.getBusinessComponent().getName();

			case 1:
				return CellReference.convertNumToColString(spreadSheetMDInput.getSecurityColumn());

			case 2:
				if(spreadSheetMDInput.getCounterpartyColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getCounterpartyColumn());
				return "";
				
			case 3:
				if(spreadSheetMDInput.getMdEntryType().equals("0"))
					return "Bid";
				if(spreadSheetMDInput.getMdEntryType().equals("1"))
					return "Offer";
				if(spreadSheetMDInput.getMdEntryType().equals("2"))
					return "Trade";
				if(spreadSheetMDInput.getMdEntryType().equals("3"))
					return "Index Value";
				if(spreadSheetMDInput.getMdEntryType().equals("4"))
					return "Opening Price";
				if(spreadSheetMDInput.getMdEntryType().equals("5"))
					return "Closing Price";
				if(spreadSheetMDInput.getMdEntryType().equals("6"))
					return "Settlement Price";
				if(spreadSheetMDInput.getMdEntryType().equals("7"))
					return "Trading Session High Price";
				if(spreadSheetMDInput.getMdEntryType().equals("8"))
					return "Trading Session Low Price";
				if(spreadSheetMDInput.getMdEntryType().equals("9"))
					return "Trading Session VWAP Price";
				if(spreadSheetMDInput.getMdEntryType().equals("A"))
					return "Imbalance";
				if(spreadSheetMDInput.getMdEntryType().equals("B"))
					return "Trade Volume";
				if(spreadSheetMDInput.getMdEntryType().equals("C"))
					return "Open Interest";
				if(spreadSheetMDInput.getMdEntryType().equals("D"))
					return "Composite Underlying Price";
				if(spreadSheetMDInput.getMdEntryType().equals("E"))
					return "Simulated Sell Price";
				if(spreadSheetMDInput.getMdEntryType().equals("F"))
					return "Simulated Buy Price";
				if(spreadSheetMDInput.getMdEntryType().equals("G"))
					return "Margin Rate";
				if(spreadSheetMDInput.getMdEntryType().equals("H"))
					return "Mid Price";
				if(spreadSheetMDInput.getMdEntryType().equals("J"))
					return "Empty Book";
				if(spreadSheetMDInput.getMdEntryType().equals("K"))
					return "Settle High Price";
				if(spreadSheetMDInput.getMdEntryType().equals("L"))
					return "Settle Low Price";
				if(spreadSheetMDInput.getMdEntryType().equals("M"))
					return "Prior Settle Price";
				if(spreadSheetMDInput.getMdEntryType().equals("N"))
					return "Session High Bid";
				if(spreadSheetMDInput.getMdEntryType().equals("O"))
					return "Session Low Offer";
				if(spreadSheetMDInput.getMdEntryType().equals("P"))
					return "Early Prices";
				if(spreadSheetMDInput.getMdEntryType().equals("Q"))
					return "Auction Clearing Price";
				if(spreadSheetMDInput.getMdEntryType().equals("S"))
					return "Swap Value Factor";
				if(spreadSheetMDInput.getMdEntryType().equals("R"))
					return "Daily value adjustment for long positions";
				if(spreadSheetMDInput.getMdEntryType().equals("T"))
					return "Cumulative Value Adjustment for long positions";
				if(spreadSheetMDInput.getMdEntryType().equals("U"))
					return "Daily Value Adjustment for Short Positions";
				if(spreadSheetMDInput.getMdEntryType().equals("V"))
					return "Cumulative Value Adjustment for Short Positions";
				if(spreadSheetMDInput.getMdEntryType().equals("Y"))
					return "Recovery Rate";
				if(spreadSheetMDInput.getMdEntryType().equals("Z"))
					return "Recovery Rate for Long";
				if(spreadSheetMDInput.getMdEntryType().equals("a"))
					return "Recovery Rate for Short";
				if(spreadSheetMDInput.getMdEntryType().equals("W"))
					return "Fixing Price";
				if(spreadSheetMDInput.getMdEntryType().equals("X"))
					return "Cash Rate";
				
			case 4:
				if(spreadSheetMDInput.getMdEntryPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdEntryPxColumn());
				return "";
				
			case 5:
				if(spreadSheetMDInput.getMdEntrySizeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdEntrySizeColumn());
				return "";
				
			case 6:
				if(spreadSheetMDInput.getMdEntryDateColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdEntryDateColumn());
				return "";
				
			case 7:
				if(spreadSheetMDInput.getMdEntryTimeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdEntryTimeColumn());
				return "";
				
			case 8:
				if(spreadSheetMDInput.getMdPriceDeltaColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdPriceDeltaColumn());
				return "";
				
			case 9:
				if(spreadSheetMDInput.getMdTradeVolumeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getMdTradeVolumeColumn());
				return "";
				
			case 10:
				if(spreadSheetMDInput.getHighPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getHighPxColumn());
				return "";
				
			case 11:
				if(spreadSheetMDInput.getLowPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDInput.getLowPxColumn());
				return "";

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

		if(minWidth>0)
			this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		table.getColumnModel().getColumn(11).setPreferredWidth(minWidth);
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

		return spreadSheetEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && spreadSheetMDInputs.containsAll(spreadSheetEditor.getSpreadSheet().getSpreadSheetMDInputs())
				&& spreadSheetEditor.getSpreadSheet().getSpreadSheetMDInputs().containsAll(spreadSheetMDInputs);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		spreadSheetEditor.getSpreadSheet().setSpreadSheetMDInputs(spreadSheetMDInputs);
	}

	/**
	 * Update spread sheet md inputs.
	 *
	 * @param list the list
	 */
	public void updateSpreadSheetMDInputs(List<SpreadSheetMDInput> list) {

		this.spreadSheetMDInputs = new ArrayList<SpreadSheetMDInput>();

		if (list != null)
			this.spreadSheetMDInputs.addAll(list);

		modified = false;

		
		fireTableDataChanged();
	}

	/**
	 * Adds the spread sheet md input.
	 *
	 * @param spreadSheetMDInput the spread sheet md input
	 */
	public void addSpreadSheetMDInput(SpreadSheetMDInput spreadSheetMDInput) {

		spreadSheetMDInputs.add(spreadSheetMDInput);
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param spreadSheetMDInput the spread sheet md input
	 */
	public void remove(SpreadSheetMDInput spreadSheetMDInput) {

		spreadSheetMDInputs.remove(spreadSheetMDInput);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the spread sheet md input
	 */
	public SpreadSheetMDInput get(int index) {

		return spreadSheetMDInputs.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}


}
