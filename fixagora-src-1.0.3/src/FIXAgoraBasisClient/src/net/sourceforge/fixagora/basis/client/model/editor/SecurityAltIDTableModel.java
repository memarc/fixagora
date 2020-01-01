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
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;

/**
 * The Class SecurityAltIDTableModel.
 */
public class SecurityAltIDTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<SecurityAltIDGroup> securityAltIDGroups = null;
	
	/**
	 * Instantiates a new security alt id table model.
	 *
	 * @param securityEditor the security editor
	 */
	public SecurityAltIDTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;
		
		updateSecurityAltIDGroups(securityEditor.getSecurity().getSecurityDetails().getSecurityAltIDGroups());

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Identifier";

			default:
				return "Source";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityAltIDGroups.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityAltIDGroup securityAltIDGroup = securityAltIDGroups.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return securityAltIDGroup.getSecurityAltID();

			case 1:		
				if(securityAltIDGroup.getSecurityAltIDSource().equals("1"))
						return "CUSIP";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("2"))
					return "SEDOL";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("3"))
					return "QUIK";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("4"))
					return "ISIN number";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("5"))
					return "RIC code";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("6"))
					return "ISO Currency Code";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("7"))
					return "ISO Country Code";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("8"))
					return "Exchange Symbol";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("9"))
					return "Consolidated Tape Association";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("A"))
					return "Bloomberg Symbol";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("B"))
					return "Wertpapier";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("C"))
					return "Dutch";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("D"))
					return "Valoren";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("E"))
					return "Sicovam";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("F"))
					return "Belgian";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("G"))
					return "Common";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("H"))
					return "Clearing House";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("I"))
					return "ISDA/FpML Product Specification";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("J"))
					return "Option Price Reporting Authority";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("K"))
					return "ISDA/FpML Product URL";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("L"))
					return "Letter of Credit";
				if(securityAltIDGroup.getSecurityAltIDSource().equals("M"))
					return "Marketplace-assigned Identifier";	

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

		table.getColumnModel().getColumn(1).setPreferredWidth(minWidth);
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
		
		boolean clean = securityAltIDGroups.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityAltIDGroups())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityAltIDGroups().containsAll(securityAltIDGroups);

		return !clean;
	}


	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityAltIDGroups(securityAltIDGroups);
	}

	/**
	 * Update security alt id groups.
	 *
	 * @param securityAltIDGroups the security alt id groups
	 */
	public void updateSecurityAltIDGroups(List<SecurityAltIDGroup> securityAltIDGroups) {

		this.securityAltIDGroups = new ArrayList<SecurityAltIDGroup>();
		
		if(securityAltIDGroups!=null)
			this.securityAltIDGroups.addAll(securityAltIDGroups);
		
		fireTableDataChanged();
	}

	/**
	 * Adds the security alt id group.
	 *
	 * @param securityAltIDGroup the security alt id group
	 */
	public void addSecurityAltIDGroup(SecurityAltIDGroup securityAltIDGroup) {

		securityAltIDGroups.add(securityAltIDGroup);
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param index the index
	 */
	public void remove(int index) {

		securityAltIDGroups.remove(index);
		fireTableDataChanged();		
	}

}
