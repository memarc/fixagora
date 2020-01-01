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
package net.sourceforge.fixagora.basis.server.control.spreadsheet;

import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;

import org.apache.poi.ss.formula.functions.FreeRefFunction;


/**
 * The Class AbstractSpreadSheetFunction.
 */
public abstract class AbstractSpreadSheetFunction implements FreeRefFunction{
	
	/** The business component handler. */
	protected BusinessComponentHandler businessComponentHandler = null;
	
	/**
	 * Sets the business component handler.
	 *
	 * @param businessComponentHandler the new business component handler
	 */
	void setBusinessComponentHandler(BusinessComponentHandler businessComponentHandler) {
	
		this.businessComponentHandler = businessComponentHandler;
	}
	
	/**
	 * Gets the function name.
	 *
	 * @return the function name
	 */
	public abstract String getFunctionName();

}
