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

import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityComplexEvent;

/**
 * The Class ComplexEventTableModel.
 */
public class ComplexEventTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private List<SecurityComplexEvent> securityComplexEvents = null;
		
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");

	private boolean modified = false;

	
	/**
	 * Instantiates a new complex event table model.
	 *
	 * @param securityEditor the security editor
	 */
	public ComplexEventTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;
		
		updateSecurityComplexEvents(securityEditor.getSecurity().getSecurityDetails().getSecurityComplexEvents());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 7;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {
			
			case 0:
				return "Type";

			case 1:
				return "Option Payout Amount";
				
			case 2:
				return "Price";
				
			case 3:
				return "Price Boundary Method";
				
			case 4:
				return "Price Boundary Precision";
				
			case 5:
				return "Time Type";
				
			case 6:
				return "Condition";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityComplexEvents.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityComplexEvent securityComplexEvent = securityComplexEvents.get(rowIndex);

		switch (columnIndex) {

			case 0:
				switch(securityComplexEvent.getEventType())
				{
					case 1:
						return "Capped";
					case 2:
						return "Trigger";
					case 3:
						return "Knock-in up";
					case 4:
						return "Kock-in down";
					case 5:
						return "Knock-out up";
					case 6:
						return "Knock-out down";
					case 7:
						return "Underlying";
					case 8:
						return "Reset Barrier";
					case 9:
						return "Rolling Barrier";
				}
				
			
			case 1:
				if(securityComplexEvent.getOptionPayoutAmount()!=null)
					return doubleFormat.format(securityComplexEvent.getOptionPayoutAmount());
				return "";

			case 2:
				if(securityComplexEvent.getEventPrice()!=null)
					return doubleFormat.format(securityComplexEvent.getEventPrice());
				return "";			

			case 3:
				if(securityComplexEvent.getEventPriceBoundaryMethod()!=null)
				switch(securityComplexEvent.getEventPriceBoundaryMethod())
				{
					case 1:
						return "Less than complex event price";
					case 2:
						return "Less than or equal to complex event price";
					case 3:
						return "Equal to complex event price";
					case 4:
						return "Greater than or equal to complex event price";
					case 5:
						return "Greater than complex event price";
				}
				return "";
			case 4:
				if(securityComplexEvent.getEventPriceBoundaryPrecision()!=null)
					return doubleFormat.format(securityComplexEvent.getEventPriceBoundaryPrecision());
				return "";
				
			case 5:
				if(securityComplexEvent.getEventPriceTimeType()!=null)
				switch(securityComplexEvent.getEventPriceTimeType())
				{
					case 1:
						return "Expiration";
					case 2:
						return "Immediate";
					case 3:
						return "Specified Date";
				}
				return "";
			case 6:
				if(securityComplexEvent.getEventCondition()!=null)
				switch(securityComplexEvent.getEventCondition())
				{
					case 1:
						return "And";
					case 2:
						return "Or";
				}
				return "";
			default:
				return "";
		}
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
		
		boolean clean = !modified  && securityComplexEvents.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityComplexEvents())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityComplexEvents().containsAll(securityComplexEvents);

		return !clean;
	}


	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityComplexEvents(securityComplexEvents);
	}

	/**
	 * Update security complex events.
	 *
	 * @param list the list
	 */
	public void updateSecurityComplexEvents(List<SecurityComplexEvent> list) {

		this.securityComplexEvents = new ArrayList<SecurityComplexEvent>();
		
		if(list!=null)
			this.securityComplexEvents.addAll(list);
		
		modified = false;
		
		fireTableDataChanged();
	}

	/**
	 * Adds the security complex event.
	 *
	 * @param securityComplexEvent the security complex event
	 */
	public void addSecurityComplexEvent(SecurityComplexEvent securityComplexEvent) {

		securityComplexEvents.add(securityComplexEvent);
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param securityComplexEvent the security complex event
	 */
	public void remove(SecurityComplexEvent securityComplexEvent) {

		securityComplexEvents.remove(securityComplexEvent);
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the security complex event
	 */
	public SecurityComplexEvent get(int index) {

		return securityComplexEvents.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		modified = true;
	}

}
