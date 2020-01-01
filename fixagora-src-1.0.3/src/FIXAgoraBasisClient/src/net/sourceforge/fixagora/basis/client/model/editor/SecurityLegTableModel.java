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
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityLeg;

/**
 * The Class SecurityLegTableModel.
 */
public class SecurityLegTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FSecurityEditor securityEditor = null;

	private int minWidth = 0;

	private JTable table = null;

	private List<SecurityLeg> securityLegs = null;

	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");

	private boolean modified = false;

	/**
	 * Instantiates a new security leg table model.
	 *
	 * @param securityEditor the security editor
	 */
	public SecurityLegTableModel(final FSecurityEditor securityEditor) {

		super();

		this.securityEditor = securityEditor;

		updateSecurityLegs(securityEditor.getSecurity().getSecurityDetails().getSecurityLegs());

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
				return "Leg Security";

			case 1:
				return "Ratio Quantity";

			case 2:
				return "Option Ratio";

			case 3:
				return "Side";

			default:
				return "";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return securityLegs.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		SecurityLeg securityLeg = securityLegs.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return securityLeg.getLegSecurity().getName();

			case 1:
				if (securityLeg.getRatioQuantity() != null)
					return doubleFormat.format(securityLeg.getRatioQuantity());
				return "";

			case 2:
				if (securityLeg.getOptionRatio() != null)
					return doubleFormat.format(securityLeg.getOptionRatio());
				return "";

			case 3:
				if (securityLeg.getSide() != null) {
					if (securityLeg.getSide().equals("1"))
						return "Buy";
					if (securityLeg.getSide().equals("2"))
						return "Sell";
					if (securityLeg.getSide().equals("3"))
						return "Buy minus";
					if (securityLeg.getSide().equals("4"))
						return "Sell plus";
					if (securityLeg.getSide().equals("5"))
						return "Sell short";
					if (securityLeg.getSide().equals("6"))
						return "Sell short exempt";
					if (securityLeg.getSide().equals("7"))
						return "Undisclosed";
					if (securityLeg.getSide().equals("8"))
						return "Cross";
					if (securityLeg.getSide().equals("9"))
						return "Cross short";
					if (securityLeg.getSide().equals("A"))
						return "Cross short exempt";
					if (securityLeg.getSide().equals("B"))
						return "As Defined";
					if (securityLeg.getSide().equals("C"))
						return "Opposite";
					if (securityLeg.getSide().equals("D"))
						return "Subscribe";
					if (securityLeg.getSide().equals("E"))
						return "Redeem";
					if (securityLeg.getSide().equals("F"))
						return "Lend";
					if (securityLeg.getSide().equals("G"))
						return "Borrow";
				}
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

		return securityEditor;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		boolean clean = !modified && securityLegs.containsAll(securityEditor.getSecurity().getSecurityDetails().getSecurityLegs())
				&& securityEditor.getSecurity().getSecurityDetails().getSecurityLegs().containsAll(securityLegs);

		return !clean;
	}

	/**
	 * Save.
	 */
	public void save() {

		securityEditor.getSecurity().getSecurityDetails().setSecurityLegs(securityLegs);
	}

	/**
	 * Update security legs.
	 *
	 * @param list the list
	 */
	public void updateSecurityLegs(List<SecurityLeg> list) {

		this.securityLegs = new ArrayList<SecurityLeg>();

		if (list != null)
			this.securityLegs.addAll(list);

		modified = false;

		fireTableDataChanged();
	}

	/**
	 * Adds the security leg.
	 *
	 * @param securityLeg the security leg
	 */
	public void addSecurityLeg(SecurityLeg securityLeg) {

		securityLegs.add(securityLeg);
		fireTableDataChanged();
	}

	/**
	 * Removes the.
	 *
	 * @param securityLeg the security leg
	 */
	public void remove(SecurityLeg securityLeg) {

		securityLegs.remove(securityLeg);
		fireTableDataChanged();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the security leg
	 */
	public SecurityLeg get(int index) {

		return securityLegs.get(index);
	}

	/**
	 * Modified.
	 */
	public void modified() {

		modified = true;
	}

}
