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
package net.sourceforge.agora.simulator.model;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.agora.simulator.model.BlotterEntry.LevelIcon;

/**
 * The Class FIXAgoraSimulatorTableModel.
 */
public class FIXAgoraSimulatorTableModel extends AbstractTableModel implements TableModelListener {

	private static final long serialVersionUID = 1L;

	private List<BlotterEntry> blotterEntries = new ArrayList<BlotterEntry>();

	private JTable table = null;

	private boolean hideMessage = true;

	private JScrollBar verticalScrollBar = null;

	/**
	 * Instantiates a new fIX agora simulator table model.
	 *
	 * @param table the table
	 * @param verticalScrollBar the vertical scroll bar
	 */
	public FIXAgoraSimulatorTableModel(JTable table, JScrollBar verticalScrollBar) {

		super();
		this.table = table;
		
		addTableModelListener(this);
		
		this.verticalScrollBar = verticalScrollBar;
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

		return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return blotterEntries.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		return blotterEntries.get(rowIndex);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	/**
	 * Adds the blotter entry.
	 *
	 * @param blotterEntry the blotter entry
	 */
	public synchronized void addBlotterEntry(BlotterEntry blotterEntry) {

		blotterEntries.add(blotterEntry);

		if (blotterEntries.size() > 3000) {
			if (hideMessage)
				for (int i = 0; i < blotterEntries.size(); i++)
					if (blotterEntries.get(i).getLevelIcon() == LevelIcon.MESSAGE) {
						blotterEntries.remove(i);
						i = blotterEntries.size();
					}

			if (blotterEntries.size() > 3000)
				blotterEntries.remove(0);
		}

		int size = 25 + blotterEntry.getOffset();

		FontMetrics fontMetrics = table.getFontMetrics(new Font("Dialog", Font.PLAIN, 12));

		for (BlotterText blotterText : blotterEntry.blotterTexts)
			size = size + fontMetrics.stringWidth(blotterText.getText());

		if (table.getColumnModel().getColumn(1).getPreferredWidth() < size) {
			table.getColumnModel().getColumn(1).setPreferredWidth(size);
		}
		
		fireTableDataChanged();

	}

	/**
	 * Gets the blotter entry.
	 *
	 * @param row the row
	 * @return the blotter entry
	 */
	public BlotterEntry getBlotterEntry(int row) {

		return blotterEntries.get(row);
	}

	/**
	 * Sets the hide message.
	 *
	 * @param selected the new hide message
	 */
	public void setHideMessage(boolean selected) {

		hideMessage = selected;

	}

	/**
	 * Checks if is scroll bar fully extended.
	 *
	 * @param vScrollBar the v scroll bar
	 * @return true, if is scroll bar fully extended
	 */
	public static boolean isScrollBarFullyExtended(JScrollBar vScrollBar) {

		BoundedRangeModel model = vScrollBar.getModel();
		return (model.getExtent() + model.getValue()) == model.getMaximum();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		
		boolean scroll = isScrollBarFullyExtended(verticalScrollBar);
		
		if (scroll)
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					table.scrollRectToVisible(table.getCellRect(blotterEntries.size() - 1, 0, false));


				}
			});
		
	}

}
