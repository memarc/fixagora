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

import java.util.HashMap;
import java.util.Map;

import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetRowFormat;


/**
 * The Class SpreadSheetTableRowFilter.
 */
public class SpreadSheetTableRowFilter extends RowFilter<AbstractTableModel, Integer>{

	/** The spread sheet row formats. */
	Map<Integer, SpreadSheetRowFormat> spreadSheetRowFormats = new HashMap<Integer, SpreadSheetRowFormat>();
	
	/**
	 * Put.
	 *
	 * @param spreadSheetRowFormat the spread sheet row format
	 */
	public void put(SpreadSheetRowFormat spreadSheetRowFormat)
	{
		spreadSheetRowFormats.put(spreadSheetRowFormat.getRowNumber(), spreadSheetRowFormat);
	}

	/* (non-Javadoc)
	 * @see javax.swing.RowFilter#include(javax.swing.RowFilter.Entry)
	 */
	@Override
    public boolean include(Entry< ? extends AbstractTableModel, ? extends Integer > entry) {
		if(entry.getIdentifier() instanceof Integer)
		{
			SpreadSheetRowFormat spreadSheetRowFormat = spreadSheetRowFormats.get(entry.getIdentifier());
			if(spreadSheetRowFormat!=null&&spreadSheetRowFormat.getHidden())
				return false;
			return true;
		}
		return true;
    }

	
}
