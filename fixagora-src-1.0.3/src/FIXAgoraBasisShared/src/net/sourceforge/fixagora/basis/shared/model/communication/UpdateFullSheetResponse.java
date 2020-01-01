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





/**
 * The Class UpdateFullSheetResponse.
 */
public class UpdateFullSheetResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private UpdateColumnFormatResponse updateColumnFormatResponse = null;
	
	private UpdateRowFormatResponse updateRowFormatResponse = null;
	
	private UpdateSheetCellFormatResponse updateSheetCellFormatResponse = null;
	
	private UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse = null;
	
	private UpdateSheetCellResponse updateSheetCellResponse = null;
	
	private long sheet = 0;
	
	/**
	 * Instantiates a new update full sheet response.
	 *
	 * @param sheet the sheet
	 */
	public UpdateFullSheetResponse(long sheet) {

		super(null);
		this.sheet = sheet;
		
	}
	
	
	/**
	 * Gets the sheet.
	 *
	 * @return the sheet
	 */
	public long getSheet() {
	
		return sheet;
	}




	/**
	 * Gets the update column format response.
	 *
	 * @return the update column format response
	 */
	public UpdateColumnFormatResponse getUpdateColumnFormatResponse() {
	
		return updateColumnFormatResponse;
	}



	
	/**
	 * Sets the update column format response.
	 *
	 * @param updateColumnFormatResponse the new update column format response
	 */
	public void setUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse) {
	
		this.updateColumnFormatResponse = updateColumnFormatResponse;
	}



	
	/**
	 * Gets the update row format response.
	 *
	 * @return the update row format response
	 */
	public UpdateRowFormatResponse getUpdateRowFormatResponse() {
	
		return updateRowFormatResponse;
	}



	
	/**
	 * Sets the update row format response.
	 *
	 * @param updateRowFormatResponse the new update row format response
	 */
	public void setUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse) {
	
		this.updateRowFormatResponse = updateRowFormatResponse;
	}



	
	/**
	 * Gets the update sheet cell format response.
	 *
	 * @return the update sheet cell format response
	 */
	public UpdateSheetCellFormatResponse getUpdateSheetCellFormatResponse() {
	
		return updateSheetCellFormatResponse;
	}



	
	/**
	 * Sets the update sheet cell format response.
	 *
	 * @param updateSheetCellFormatResponse the new update sheet cell format response
	 */
	public void setUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {
	
		this.updateSheetCellFormatResponse = updateSheetCellFormatResponse;
	}



	
	/**
	 * Gets the update sheet conditional format response.
	 *
	 * @return the update sheet conditional format response
	 */
	public UpdateSheetConditionalFormatResponse getUpdateSheetConditionalFormatResponse() {
	
		return updateSheetConditionalFormatResponse;
	}



	
	/**
	 * Sets the update sheet conditional format response.
	 *
	 * @param updateSheetConditionalFormatResponse the new update sheet conditional format response
	 */
	public void setUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {
	
		this.updateSheetConditionalFormatResponse = updateSheetConditionalFormatResponse;
	}



	
	/**
	 * Gets the update sheet cell response.
	 *
	 * @return the update sheet cell response
	 */
	public UpdateSheetCellResponse getUpdateSheetCellResponse() {
	
		return updateSheetCellResponse;
	}



	
	/**
	 * Sets the update sheet cell response.
	 *
	 * @param updateSheetCellResponse the new update sheet cell response
	 */
	public void setUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {
	
		this.updateSheetCellResponse = updateSheetCellResponse;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
		fBasisResponses.onUpdateFullSheetResponse(this);	
		
	}

}
