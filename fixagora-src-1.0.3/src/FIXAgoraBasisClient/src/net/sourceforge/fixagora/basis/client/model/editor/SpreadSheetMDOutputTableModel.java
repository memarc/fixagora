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
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDOutput;

import org.apache.poi.hssf.util.CellReference;

/**
 * The Class SpreadSheetMDOutputTableModel.
 */
public class SpreadSheetMDOutputTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private SpreadSheetEditor spreadSheetEditor = null;

	private int minWidth = 100;

	private JTable table = null;

	private List<SpreadSheetMDOutput> spreadSheetMDOutputs = null;

	private boolean modified = false;

	/**
	 * Instantiates a new spread sheet md output table model.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetMDOutputTableModel(final SpreadSheetEditor spreadSheetEditor) {

		super();

		this.spreadSheetEditor = spreadSheetEditor;

		updateSpreadSheetMDOutputs(this.spreadSheetEditor.getSpreadSheet().getSpreadSheetMDOutputs());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 13;
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
				return "Send If";
				
			case 3:
				return "Delete If NOT";
				
			case 4:
				return "MD Entry Type";
				
			case 5:
				return "Px";
				
			case 6:
				return "Size";
				
			case 7:
				return "Date";
				
			case 8:
				return "Time";
				
			case 9:
				return "Delta";
				
			case 10:
				return "Trade Volume";
				
			case 11:
				return "High Px";
				
			case 12:
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

		return spreadSheetMDOutputs.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SpreadSheetMDOutput spreadSheetMDOutput = spreadSheetMDOutputs.get(rowIndex);

		switch (columnIndex) {
			
			case 0:
				return spreadSheetMDOutput.getBusinessComponent().getName();

			case 1:
				return CellReference.convertNumToColString(spreadSheetMDOutput.getSecurityColumn());
				
			case 2:
				if(spreadSheetMDOutput.getSendIfColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getSendIfColumn());
				return "";
				
			case 3:
				if(spreadSheetMDOutput.getDeleteIfNot()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getDeleteIfNot());
				return "";
				
			case 4:
				if(spreadSheetMDOutput.getMdEntryType().equals("0"))
					return "Bid";
				if(spreadSheetMDOutput.getMdEntryType().equals("1"))
					return "Offer";
				if(spreadSheetMDOutput.getMdEntryType().equals("2"))
					return "Trade";
				if(spreadSheetMDOutput.getMdEntryType().equals("3"))
					return "Index Value";
				if(spreadSheetMDOutput.getMdEntryType().equals("4"))
					return "Opening Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("5"))
					return "Closing Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("6"))
					return "Settlement Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("7"))
					return "Trading Session High Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("8"))
					return "Trading Session Low Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("9"))
					return "Trading Session VWAP Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("A"))
					return "Imbalance";
				if(spreadSheetMDOutput.getMdEntryType().equals("B"))
					return "Trade Volume";
				if(spreadSheetMDOutput.getMdEntryType().equals("C"))
					return "Open Interest";
				if(spreadSheetMDOutput.getMdEntryType().equals("D"))
					return "Composite Underlying Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("E"))
					return "Simulated Sell Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("F"))
					return "Simulated Buy Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("G"))
					return "Margin Rate";
				if(spreadSheetMDOutput.getMdEntryType().equals("H"))
					return "Mid Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("J"))
					return "Empty Book";
				if(spreadSheetMDOutput.getMdEntryType().equals("K"))
					return "Settle High Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("L"))
					return "Settle Low Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("M"))
					return "Prior Settle Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("N"))
					return "Session High Bid";
				if(spreadSheetMDOutput.getMdEntryType().equals("O"))
					return "Session Low Offer";
				if(spreadSheetMDOutput.getMdEntryType().equals("P"))
					return "Early Prices";
				if(spreadSheetMDOutput.getMdEntryType().equals("Q"))
					return "Auction Clearing Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("S"))
					return "Swap Value Factor";
				if(spreadSheetMDOutput.getMdEntryType().equals("R"))
					return "Daily value adjustment for long positions";
				if(spreadSheetMDOutput.getMdEntryType().equals("T"))
					return "Cumulative Value Adjustment for long positions";
				if(spreadSheetMDOutput.getMdEntryType().equals("U"))
					return "Daily Value Adjustment for Short Positions";
				if(spreadSheetMDOutput.getMdEntryType().equals("V"))
					return "Cumulative Value Adjustment for Short Positions";
				if(spreadSheetMDOutput.getMdEntryType().equals("Y"))
					return "Recovery Rate";
				if(spreadSheetMDOutput.getMdEntryType().equals("Z"))
					return "Recovery Rate for Long";
				if(spreadSheetMDOutput.getMdEntryType().equals("a"))
					return "Recovery Rate for Short";
				if(spreadSheetMDOutput.getMdEntryType().equals("W"))
					return "Fixing Price";
				if(spreadSheetMDOutput.getMdEntryType().equals("X"))
					return "Cash Rate";
				
			case 5:
				if(spreadSheetMDOutput.getMdEntryPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdEntryPxColumn());
				return "";
				
			case 6:
				if(spreadSheetMDOutput.getMdEntrySizeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdEntrySizeColumn());
				return "";
				
			case 7:
				if(spreadSheetMDOutput.getMdEntryDateColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdEntryDateColumn());
				return "";
				
			case 8:
				if(spreadSheetMDOutput.getMdEntryTimeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdEntryTimeColumn());
				return "";
				
			case 9:
				if(spreadSheetMDOutput.getMdPriceDeltaColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdPriceDeltaColumn());
				return "";
				
			case 10:
				if(spreadSheetMDOutput.getMdTradeVolumeColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getMdTradeVolumeColumn());
				return "";
				
			case 11:
				if(spreadSheetMDOutput.getHighPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getHighPxColumn());
				return "";
				
			case 12:
				if(spreadSheetMDOutput.getLowPxColumn()!=null)
					return CellReference.convertNumToColString(spreadSheetMDOutput.getLowPxColumn());
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

		table.getColumnModel().getColumn(12).setPreferredWidth(minWidth);
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

		boolean clean = !modified && spreadSheetMDOutputs.containsAll(spreadSheetEditor.getSpreadSheet().getSpreadSheetMDOutputs())
				&& spreadSheetEditor.getSpreadSheet().getSpreadSheetMDOutputs().containsAll(spreadSheetMDOutputs);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		spreadSheetEditor.getSpreadSheet().setSpreadSheetMDOutputs(spreadSheetMDOutputs);
	}

	/**
	 * Update spread sheet md outputs.
	 *
	 * @param list the list
	 */
	public void updateSpreadSheetMDOutputs(List<SpreadSheetMDOutput> list) {

		this.spreadSheetMDOutputs = new ArrayList<SpreadSheetMDOutput>();

		if (list != null)
			this.spreadSheetMDOutputs.addAll(list);

		modified = false;

		
		fireTableDataChanged();
	}

	/**
	 * Adds the spread sheet md output.
	 *
	 * @param spreadSheetMDOutput the spread sheet md output
	 */
	public void addSpreadSheetMDOutput(SpreadSheetMDOutput spreadSheetMDOutput) {

		spreadSheetMDOutputs.add(spreadSheetMDOutput);
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param spreadSheetMDOutput the spread sheet md output
	 */
	public void remove(SpreadSheetMDOutput spreadSheetMDOutput) {

		spreadSheetMDOutputs.remove(spreadSheetMDOutput);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the spread sheet md output
	 */
	public SpreadSheetMDOutput get(int index) {

		return spreadSheetMDOutputs.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}


}
