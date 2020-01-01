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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityUnderlying;

/**
 * The Class SecurityUnderlyingTableModel.
 */
public class SecurityUnderlyingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<SecurityUnderlying> securityUnderlyings = null;
		
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");

	private boolean modified = false;

	
	/**
	 * Instantiates a new security underlying table model.
	 *
	 * @param securityEditor the security editor
	 */
	public SecurityUnderlyingTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;
		
		updateSecurityUnderlyings(securityEditor.getSecurity().getSecurityDetails().getSecurityUnderlyings());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 8;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {
			
			case 0:
				return "Underlying";

			case 1:
				return "End Price";
				
			case 2:
				return "Start Value";
				
			case 3:
				return "End Value";
				
			case 4:
				return "Allocation Percent";
			
			case 5:
				return "Settlement";
				
			case 6:
				return "Cash Amount";
				
			case 7:
				return "Cash Type";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityUnderlyings.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityUnderlying securityUnderlying = securityUnderlyings.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return securityUnderlying.getUnderlyingSecurity().getName();
								
			case 1:
				if(securityUnderlying.getEndPrice()!=null)
					return doubleFormat.format(securityUnderlying.getEndPrice());
				return "";
				
			case 2:
				if(securityUnderlying.getStartValue()!=null)
					return doubleFormat.format(securityUnderlying.getStartValue());
				return "";
				
			case 3:
				if(securityUnderlying.getEndValue()!=null)
					return doubleFormat.format(securityUnderlying.getEndValue());
				return "";
				
			case 4:
				if(securityUnderlying.getAllocationPercent()!=null)
					return doubleFormat.format(securityUnderlying.getAllocationPercent());
				return "";
				
			case 5:
				if(securityUnderlying.getSettlementType()!=null)
					switch(securityUnderlying.getSettlementType())
					{
						case 2:
							return "T+1";
						case 4:
							return "T+3";
						case 5:
							return "T+4";
					}
				return "";
				
			
			case 6:
				if(securityUnderlying.getCashAmount()!=null)
					return doubleFormat.format(securityUnderlying.getCashAmount());
				return "";

			case 7:
				if(securityUnderlying.getCashType()!=null)
					return securityUnderlying.getCashType();
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

		this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

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
	 * Gets the abstract business object editor.
	 *
	 * @return the abstract business object editor
	 */
	public AbstractBusinessObjectEditor getAbstractBusinessObjectEditor() {

		return securityEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {
		
		boolean clean = !modified  && securityUnderlyings.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityUnderlyings())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityUnderlyings().containsAll(securityUnderlyings);

		return !clean;
	}


	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityUnderlyings(securityUnderlyings);
	}

	/**
	 * Update security underlyings.
	 *
	 * @param list the list
	 */
	public void updateSecurityUnderlyings(List<SecurityUnderlying> list) {

		this.securityUnderlyings = new ArrayList<SecurityUnderlying>();
		
		if(list!=null)
			this.securityUnderlyings.addAll(list);
		
		modified = false;
		
		fireTableDataChanged();
	}

	/**
	 * Adds the security underlying.
	 *
	 * @param securityUnderlying the security underlying
	 */
	public void addSecurityUnderlying(SecurityUnderlying securityUnderlying) {

		securityUnderlyings.add(securityUnderlying);
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param securityUnderlying the security underlying
	 */
	public void remove(SecurityUnderlying securityUnderlying) {

		securityUnderlyings.remove(securityUnderlying);
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the security underlying
	 */
	public SecurityUnderlying get(int index) {

		return securityUnderlyings.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		modified = true;
	}

}
