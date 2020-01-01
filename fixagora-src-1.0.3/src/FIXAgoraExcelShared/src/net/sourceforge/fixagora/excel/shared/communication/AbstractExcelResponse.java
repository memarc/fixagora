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
package net.sourceforge.fixagora.excel.shared.communication;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponses;



/**
 * The Class AbstractExcelResponse.
 */
public abstract class AbstractExcelResponse extends AbstractResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new abstract excel response.
	 *
	 * @param abstractRequest the abstract request
	 */
	public AbstractExcelResponse(AbstractRequest abstractRequest) {

		super(abstractRequest);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse#handleAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponses)
	 */
	@Override
	public void handleAbstractResponse(AbstractResponses abstractResponses) {

		if(abstractResponses instanceof ExcelResponses)
		{
			ExcelResponses excelResponses = (ExcelResponses)abstractResponses;
			handleAbstractExcelResponse(excelResponses);
		}
		
	}
	
	/**
	 * Handle abstract excel response.
	 *
	 * @param excelResponses the excel responses
	 */
	public abstract void handleAbstractExcelResponse(ExcelResponses excelResponses);	
	
}
