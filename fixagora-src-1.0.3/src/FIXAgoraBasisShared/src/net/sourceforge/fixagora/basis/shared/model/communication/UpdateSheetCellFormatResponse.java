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
package net.sourceforge.fixagora.basis.shared.model.communication;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;



/**
 * The Class UpdateSheetCellFormatResponse.
 */
public class UpdateSheetCellFormatResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private Collection<SpreadSheetCellFormat> spreadSheetCellFormats = null;
	
	/**
	 * Instantiates a new update sheet cell format response.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public UpdateSheetCellFormatResponse(SpreadSheetCellFormat spreadSheetCellFormat) {

		super(null);
		this.spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		this.spreadSheetCellFormats.add(spreadSheetCellFormat);
	}

	/**
	 * Instantiates a new update sheet cell format response.
	 *
	 * @param spreadSheetCellFormats the spread sheet cell formats
	 */
	public UpdateSheetCellFormatResponse(Collection<SpreadSheetCellFormat> spreadSheetCellFormats) {

		super(null);
		this.spreadSheetCellFormats = spreadSheetCellFormats;
	}	
	


	
	/**
	 * Gets the spread sheet cell formats.
	 *
	 * @return the spread sheet cell formats
	 */
	public Collection<SpreadSheetCellFormat> getSpreadSheetCellFormats() {
	
		return spreadSheetCellFormats;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
		fBasisResponses.onUpdateSheetCellFormatResponse(this);	
		
	}

}
