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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityEvent;

/**
 * The Class SecurityEventTableModel.
 */
public class SecurityEventTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<SecurityEvent> securityEvents = null;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
	
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");

	private boolean modified = false;

	
	/**
	 * Instantiates a new security event table model.
	 *
	 * @param securityEditor the security editor
	 */
	public SecurityEventTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;
		
		updateSecurityEvents(securityEditor.getSecurity().getSecurityDetails().getSecurityEvents());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 5;
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
				return "Date";
				
			case 2:
				return "Time";
				
			case 3:
				return "Price";
				
			case 4:
				return "Text";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityEvents.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityEvent securityEvent = securityEvents.get(rowIndex);

		switch (columnIndex) {

			case 0:
				switch(securityEvent.getEventType())
				{
					case 1:
						return "Put";
					case 2:
						return "Call";
					case 3:
						return "Tender";
					case 4:
						return "Sinking Fund Call";
					case 5:
						return "Activation";
					case 6:
						return "Inactiviation";
					case 7:
						return "Last Eligible Trade Date";
					case 8:
						return "Swap Start Date";
					case 9:
						return "Swap End Date";
					case 10:
						return "Swap Roll Date";
					case 11:
						return "Swap Next Start Date";
					case 12:
						return "Swap Next Roll Date";
					case 13:
						return "First Delivery Date";
					case 14:
						return "Last Delivery Date";
					case 15:
						return "Initial Inventory Due Date";
					case 16:
						return "Final Inventory Due Date";
					case 17:
						return "First Intent Date";
					case 18:
						return "Last Intent Date";
					case 19:
						return "Position Removal Date";
					default:
						return "Other";
						
				}
				
			
			case 1:
				if(securityEvent.getEventDate()!=null)
					return simpleDateFormat.format(securityEvent.getEventDate());
				return "";

			case 2:
				if(securityEvent.getEventTime()!=null)
					return simpleTimeFormat.format(securityEvent.getEventTime());
				return "";			

			case 3:
				if(securityEvent.getEventPrice()!=null)
					return doubleFormat.format(securityEvent.getEventPrice());
				return "";
			case 4:
				return securityEvent.getEventText();

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

		table.getColumnModel().getColumn(4).setPreferredWidth(minWidth);
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
		
		boolean clean = !modified  && securityEvents.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityEvents())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityEvents().containsAll(securityEvents);

		return !clean;
	}


	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityEvents(securityEvents);
	}

	/**
	 * Update security events.
	 *
	 * @param list the list
	 */
	public void updateSecurityEvents(List<SecurityEvent> list) {

		this.securityEvents = new ArrayList<SecurityEvent>();
		
		if(list!=null)
			this.securityEvents.addAll(list);
		
		modified = false;
		
		fireTableDataChanged();
	}

	/**
	 * Adds the security event.
	 *
	 * @param securityEvent the security event
	 */
	public void addSecurityEvent(SecurityEvent securityEvent) {

		securityEvents.add(securityEvent);
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param securityEvent the security event
	 */
	public void remove(SecurityEvent securityEvent) {

		securityEvents.remove(securityEvent);
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the security event
	 */
	public SecurityEvent get(int index) {

		return securityEvents.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		modified = true;
	}

}
