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
 * The Interface BasisResponses.
 */
public interface BasisResponses extends AbstractResponses {
	
	/**
	 * On login response.
	 *
	 * @param loginResponse the login response
	 */
	public void onLoginResponse(LoginResponse loginResponse);

	/**
	 * On persist response.
	 *
	 * @param persistResponse the persist response
	 */
	public void onPersistResponse(PersistResponse persistResponse);

	/**
	 * On update response.
	 *
	 * @param updateResponse the update response
	 */
	public void onUpdateResponse(UpdateResponse updateResponse);

	/**
	 * On remove response.
	 *
	 * @param removeResponse the remove response
	 */
	public void onRemoveResponse(RemoveResponse removeResponse);

	/**
	 * On update sheet cell response.
	 *
	 * @param updateSheetCellResponse the update sheet cell response
	 */
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse);

	/**
	 * On update sheet cell format response.
	 *
	 * @param updateSheetCellFormatResponse the update sheet cell format response
	 */
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse);

	/**
	 * On update column format response.
	 *
	 * @param updateColumnFormatResponse the update column format response
	 */
	public void onUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse);

	/**
	 * On update row format response.
	 *
	 * @param updateRowFormatResponse the update row format response
	 */
	public void onUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse);

	/**
	 * On update sheet conditional format response.
	 *
	 * @param updateSheetConditionalFormatResponse the update sheet conditional format response
	 */
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse);

	/**
	 * On update full sheet response.
	 *
	 * @param updateFullSheetResponse the update full sheet response
	 */
	public void onUpdateFullSheetResponse(UpdateFullSheetResponse updateFullSheetResponse);
	
}
