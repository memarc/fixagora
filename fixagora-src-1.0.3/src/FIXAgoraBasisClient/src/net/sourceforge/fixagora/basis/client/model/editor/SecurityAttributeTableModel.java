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
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAttribute;

/**
 * The Class SecurityAttributeTableModel.
 */
public class SecurityAttributeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<SecurityAttribute> securityAttributes = null;
	
	private boolean modified = false;

	
	/**
	 * Instantiates a new security attribute table model.
	 *
	 * @param securityEditor the security editor
	 */
	public SecurityAttributeTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;
		
		updateSecurityAttributes(securityEditor.getSecurity().getSecurityDetails().getSecurityAttribute());

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
				return "Type";

			case 1:
				return "Value";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityAttributes.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityAttribute securityAttribute = securityAttributes.get(rowIndex);

		switch (columnIndex) {

			case 0:
				switch(securityAttribute.getAttributeType())
				{
					case 1:
						return "Flat";
					case 2:
						return "Zero coupon";
					case 3:
						return "Interest bearing";
					case 4:
						return "No periodic payments";
					case 5:
						return "Variable rate";
					case 6:
						return "Less fee for put";
					case 7:
						return "Stepped coupon";
					case 8:
						return "Coupon period";
					case 9:
						return "When (and if) issued";
					case 10:
						return "Original issue discount";
					case 11:
						return "Callable, puttable";
					case 12:
						return "Escrowed to Maturity";
					case 13:
						return "Escrowed to redemption date";
					case 14:
						return "Pre-refunded";
					case 15:
						return "In default";
					case 16:
						return "Unrated";
					case 17:
						return "Taxable";
					case 18:
						return "Indexed";
					case 19:
						return "Subject To Alternative Minimum Tax";
					case 20:
						return "Original issue discount price";
					case 21:
						return "Callable below maturity value";
					case 22:
						return "Callable without notice";
					case 23:
						return "Price tick rules for security";
					case 24:
						return "Trade type eligibility details for security";
					case 25:
						return "Instrument Denominator";
					case 26:
						return "Instrument Numerator";
					case 27:
						return "Instrument Price Precision";
					case 28:
						return "Instrument Strike Price";
					case 29:
						return "Tradeable Indicator";
					default:
						return "Text";		
				}				
			
			case 1:
				return securityAttribute.getAttributeValue();

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
		
		boolean clean = !modified  && securityAttributes.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityAttribute())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityAttribute().containsAll(securityAttributes);

		return !clean;
	}


	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityAttribute(securityAttributes);
	}

	/**
	 * Update security attributes.
	 *
	 * @param list the list
	 */
	public void updateSecurityAttributes(List<SecurityAttribute> list) {

		this.securityAttributes = new ArrayList<SecurityAttribute>();
		
		if(list!=null)
			this.securityAttributes.addAll(list);
		
		modified = false;
		
		fireTableDataChanged();
	}

	/**
	 * Adds the security attribute.
	 *
	 * @param securityAttribute the security attribute
	 */
	public void addSecurityAttribute(SecurityAttribute securityAttribute) {

		securityAttributes.add(securityAttribute);
		fireTableDataChanged();		
	}

	/**
	 * Removes the.
	 *
	 * @param securityAttribute the security attribute
	 */
	public void remove(SecurityAttribute securityAttribute) {

		securityAttributes.remove(securityAttribute);
		fireTableDataChanged();		
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the security attribute
	 */
	public SecurityAttribute get(int index) {

		return securityAttributes.get(index);
	}
	
	/**
	 * Modified.
	 */
	public void modified()
	{
		modified = true;
	}

}
