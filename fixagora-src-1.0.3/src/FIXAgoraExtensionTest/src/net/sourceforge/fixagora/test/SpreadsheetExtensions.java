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
package net.sourceforge.fixagora.test;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fixagora.basis.server.control.spreadsheet.AbstractSpreadSheetFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.SpreadsheetFunctions;


/**
 * The Class SpreadsheetExtensions.
 */
public class SpreadsheetExtensions implements SpreadsheetFunctions {

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.spreadsheet.SpreadsheetFunctions#getSpreadsheetFunctions()
	 */
	@Override
	public List<AbstractSpreadSheetFunction> getSpreadsheetFunctions() {

		List<AbstractSpreadSheetFunction> functions = new ArrayList<AbstractSpreadSheetFunction>();
		functions.add(new LehmanFunction());
		return functions;
		
	}

}
