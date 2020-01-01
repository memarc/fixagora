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
package net.sourceforge.fixagora.basis.client.view.component;

import java.awt.Color;

import javax.swing.border.Border;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;


/**
 * The Interface FieldInterface.
 */
public interface FieldInterface {

	/**
	 * Sets the border.
	 *
	 * @param border the new border
	 */
	public void setBorder(Border border);
	
	/**
	 * Sets the bold.
	 *
	 * @param bold the new bold
	 */
	public void setBold(boolean bold);
	
	/**
	 * Sets the high light color.
	 *
	 * @param color the new high light color
	 */
	public void setHighLightColor(Color color);
	
	/**
	 * Sets the spread sheet cell format.
	 *
	 * @param spreadSheetCellFormat the new spread sheet cell format
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat);
	
	/**
	 * Sets the background.
	 *
	 * @param color the new background
	 */
	public void setBackground(Color color);
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value);
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue();
	
	/**
	 * Sets the foreground.
	 *
	 * @param fadeoutColor the new foreground
	 */
	public void setForeground(Color fadeoutColor);

}
