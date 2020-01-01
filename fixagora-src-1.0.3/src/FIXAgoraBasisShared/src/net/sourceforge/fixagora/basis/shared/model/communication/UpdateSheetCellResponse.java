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
import java.util.List;

import net.sourceforge.fixagora.basis.shared.control.SpreadSheetCellConverter;



/**
 * The Class UpdateSheetCellResponse.
 */
public class UpdateSheetCellResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private byte[] spreadSheetCells=null;
	
	/**
	 * Instantiates a new update sheet cell response.
	 *
	 * @param spreadSheetCell the spread sheet cell
	 */
	public UpdateSheetCellResponse(SpreadSheetCell spreadSheetCell) {

		super(null);
		List<SpreadSheetCell> cells = new ArrayList<SpreadSheetCell>();
		cells.add(spreadSheetCell);
		spreadSheetCells = SpreadSheetCellConverter.getBytes(cells);
	}

	/**
	 * Instantiates a new update sheet cell response.
	 *
	 * @param modifySheetCellRequest the modify sheet cell request
	 * @param spreadSheetCells the spread sheet cells
	 */
	public UpdateSheetCellResponse(ModifySheetCellRequest modifySheetCellRequest, List<SpreadSheetCell> spreadSheetCells) {

		super(modifySheetCellRequest);
		this.spreadSheetCells = SpreadSheetCellConverter.getBytes(spreadSheetCells);
	}
	
	/**
	 * Instantiates a new update sheet cell response.
	 *
	 * @param spreadSheetCells the spread sheet cells
	 */
	public UpdateSheetCellResponse(List<SpreadSheetCell> spreadSheetCells) {

		super(null);
		this.spreadSheetCells = SpreadSheetCellConverter.getBytes(spreadSheetCells);
	}	
	
	
	/**
	 * Gets the spread sheet cells.
	 *
	 * @return the spread sheet cells
	 */
	public List<SpreadSheetCell> getSpreadSheetCells() {
	
		return SpreadSheetCellConverter.getSpreadSheetCells(spreadSheetCells);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
		fBasisResponses.onUpdateSheetCellResponse(this);	
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "UpdateSheetCellResponse length="+spreadSheetCells.length;
	}

}
