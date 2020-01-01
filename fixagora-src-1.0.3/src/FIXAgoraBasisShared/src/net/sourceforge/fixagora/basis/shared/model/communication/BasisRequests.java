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

import org.jboss.netty.channel.Channel;


/**
 * The Interface BasisRequests.
 */
public interface BasisRequests extends AbstractRequests {

	/**
	 * On login request.
	 *
	 * @param loginRequest the login request
	 * @param channel the channel
	 */
	public void onLoginRequest(LoginRequest loginRequest, Channel channel);

	/**
	 * On root business object request.
	 *
	 * @param rootBusinessObjectRequest the root business object request
	 * @param channel the channel
	 */
	public void onRootBusinessObjectRequest(RootBusinessObjectRequest rootBusinessObjectRequest, Channel channel);

	/**
	 * On children business object request.
	 *
	 * @param childrenBusinessObjectRequest the children business object request
	 * @param channel the channel
	 */
	public void onChildrenBusinessObjectRequest(ChildrenBusinessObjectRequest childrenBusinessObjectRequest, Channel channel);

	/**
	 * On persist request.
	 *
	 * @param persistRequest the persist request
	 * @param channel the channel
	 */
	public void onPersistRequest(PersistRequest persistRequest, Channel channel);

	/**
	 * On roles request.
	 *
	 * @param rolesRequest the roles request
	 * @param channel the channel
	 */
	public void onRolesRequest(RolesRequest rolesRequest, Channel channel);

	/**
	 * On update request.
	 *
	 * @param updateRequest the update request
	 * @param channel the channel
	 */
	public void onUpdateRequest(UpdateRequest updateRequest, Channel channel);

	/**
	 * On remove request.
	 *
	 * @param removeRequest the remove request
	 * @param channel the channel
	 */
	public void onRemoveRequest(RemoveRequest removeRequest, Channel channel);

	/**
	 * On logoff request.
	 *
	 * @param logoffRequest the logoff request
	 * @param channel the channel
	 */
	public void onLogoffRequest(LogoffRequest logoffRequest, Channel channel);

	/**
	 * On data dictionaries request.
	 *
	 * @param securitiesRequest the securities request
	 * @param channel the channel
	 */
	public void onDataDictionariesRequest(DataDictionariesRequest securitiesRequest, Channel channel);

	/**
	 * On refresh request.
	 *
	 * @param refreshRequest the refresh request
	 * @param channel the channel
	 */
	public void onRefreshRequest(RefreshRequest refreshRequest, Channel channel);

	/**
	 * On unique security id request.
	 *
	 * @param uniqueSecurityIDRequest the unique security id request
	 * @param channel the channel
	 */
	public void onUniqueSecurityIDRequest(UniqueSecurityIDRequest uniqueSecurityIDRequest, Channel channel);

	/**
	 * On unique name request.
	 *
	 * @param uniqueNameRequest the unique name request
	 * @param channel the channel
	 */
	public void onUniqueNameRequest(UniqueNameRequest uniqueNameRequest, Channel channel);

	/**
	 * On open spread sheet request.
	 *
	 * @param openSpreadSheetRequest the open spread sheet request
	 * @param channel the channel
	 */
	public void onOpenSpreadSheetRequest(OpenSpreadSheetRequest openSpreadSheetRequest, Channel channel);

	/**
	 * On modify sheet cell request.
	 *
	 * @param modifySheetCellRequest the modify sheet cell request
	 * @param channel the channel
	 */
	public void onModifySheetCellRequest(ModifySheetCellRequest modifySheetCellRequest, Channel channel);

	/**
	 * On close spread sheet request.
	 *
	 * @param closeSpreadSheetRequest the close spread sheet request
	 * @param channel the channel
	 */
	public void onCloseSpreadSheetRequest(CloseSpreadSheetRequest closeSpreadSheetRequest, Channel channel);

	/**
	 * On copy spread sheet cell request.
	 *
	 * @param copySpreadSheetCellRequest the copy spread sheet cell request
	 * @param channel the channel
	 */
	public void onCopySpreadSheetCellRequest(CopySpreadSheetCellRequest copySpreadSheetCellRequest, Channel channel);

	/**
	 * On cut spread sheet cell request.
	 *
	 * @param cutSpreadSheetCellRequest the cut spread sheet cell request
	 * @param channel the channel
	 */
	public void onCutSpreadSheetCellRequest(CutSpreadSheetCellRequest cutSpreadSheetCellRequest, Channel channel);

	/**
	 * On paste spread sheet cell request.
	 *
	 * @param pasteSpreadSheetCellRequest the paste spread sheet cell request
	 * @param channel the channel
	 */
	public void onPasteSpreadSheetCellRequest(PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest, Channel channel);

	/**
	 * On modify spread sheet cell format request.
	 *
	 * @param modifySpreadSheetCellFormatRequest the modify spread sheet cell format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetCellFormatRequest(ModifySpreadSheetCellFormatRequest modifySpreadSheetCellFormatRequest, Channel channel);

	/**
	 * On modify column width request.
	 *
	 * @param modifyColumnWidthRequest the modify column width request
	 * @param channel the channel
	 */
	public void onModifyColumnWidthRequest(ModifyColumnWidthRequest modifyColumnWidthRequest, Channel channel);

	/**
	 * On modify column visible request.
	 *
	 * @param modifyColumnVisibleRequest the modify column visible request
	 * @param channel the channel
	 */
	public void onModifyColumnVisibleRequest(ModifyColumnVisibleRequest modifyColumnVisibleRequest, Channel channel);

	/**
	 * On modify row visible request.
	 *
	 * @param modifyRowVisibleRequest the modify row visible request
	 * @param channel the channel
	 */
	public void onModifyRowVisibleRequest(ModifyRowVisibleRequest modifyRowVisibleRequest, Channel channel);

	/**
	 * On modify spread sheet conditional format request.
	 *
	 * @param modifySpreadSheetConditionalFormatRequest the modify spread sheet conditional format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetConditionalFormatRequest(ModifySpreadSheetConditionalFormatRequest modifySpreadSheetConditionalFormatRequest,
			Channel channel);

	/**
	 * On insert column request.
	 *
	 * @param insertColumnRequest the insert column request
	 * @param channel the channel
	 */
	public void onInsertColumnRequest(InsertColumnRequest insertColumnRequest, Channel channel);

	/**
	 * On insert row request.
	 *
	 * @param insertRowRequest the insert row request
	 * @param channel the channel
	 */
	public void onInsertRowRequest(InsertRowRequest insertRowRequest, Channel channel);

	/**
	 * On delete column request.
	 *
	 * @param deleteColumnRequest the delete column request
	 * @param channel the channel
	 */
	public void onDeleteColumnRequest(DeleteColumnRequest deleteColumnRequest, Channel channel);

	/**
	 * On delete row request.
	 *
	 * @param deleteRowRequest the delete row request
	 * @param channel the channel
	 */
	public void onDeleteRowRequest(DeleteRowRequest deleteRowRequest, Channel channel);

	/**
	 * On start business component request.
	 *
	 * @param startBusinessComponentRequest the start business component request
	 * @param channel the channel
	 */
	public void onStartBusinessComponentRequest(StartBusinessComponentRequest startBusinessComponentRequest, Channel channel);

	/**
	 * On stop business component request.
	 *
	 * @param stopBusinessComponentRequest the stop business component request
	 * @param channel the channel
	 */
	public void onStopBusinessComponentRequest(StopBusinessComponentRequest stopBusinessComponentRequest, Channel channel);

	/**
	 * On fill spread sheet cell request.
	 *
	 * @param fillSpreadSheetCellRequest the fill spread sheet cell request
	 * @param channel the channel
	 */
	public void onFillSpreadSheetCellRequest(FillSpreadSheetCellRequest fillSpreadSheetCellRequest, Channel channel);

	/**
	 * On solver request.
	 *
	 * @param solverRequest the solver request
	 * @param channel the channel
	 */
	public void onSolverRequest(SolverRequest solverRequest, Channel channel);

	
}
