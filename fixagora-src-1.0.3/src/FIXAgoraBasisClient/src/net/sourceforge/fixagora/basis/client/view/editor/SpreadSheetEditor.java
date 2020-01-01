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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditorSheet.FormatAction;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.CloseSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.OpenSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class SpreadSheetEditor.
 */
public class SpreadSheetEditor extends AbstractBusinessObjectEditor {

	private SpreadSheet spreadSheet = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private SpreadSheetEditorMasterData spreadSheetEditorMasterDataterData = null;

	private SpreadSheetEditorSheet spreadSheetEditorSheet = null;
	
	private SpreadSheetEditorMDInput spreadSheetEditorFIXInput = null;
	
	private SpreadSheetEditorMDOutput spreadSheetEditorFIXOutput = null;

	private RolePermissionEditor rolePermissionEditor = null;

	/**
	 * Instantiates a new spread sheet editor.
	 *
	 * @param spreadSheet the spread sheet
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public SpreadSheetEditor(SpreadSheet spreadSheet, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(spreadSheet, mainPanel, basisClientConnector);
		this.spreadSheet = spreadSheet;
		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		Dockable frontDockable = stackDockStation.getFrontDockable();

		while (stackDockStation.getDockableCount() > 0)
			stackDockStation.remove(0);

		spreadSheetEditorSheet = new SpreadSheetEditorSheet(this);

		DefaultDockable spreadSheetEditorSheetDockable = new DefaultDockable(spreadSheetEditorSheet);
		spreadSheetEditorSheetDockable.setFactoryID("Sheet");
		spreadSheetEditorSheetDockable.setTitleText("Sheet");
		spreadSheetEditorSheetDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(spreadSheetEditorSheetDockable);

		spreadSheetEditorMasterDataterData = new SpreadSheetEditorMasterData(this);

		DefaultDockable userEditorMasterDataDockable = new DefaultDockable(spreadSheetEditorMasterDataterData);
		userEditorMasterDataDockable.setFactoryID("Master data");
		userEditorMasterDataDockable.setTitleText("Master data");
		userEditorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			if (spreadSheet.getId() == 0)
				frontDockable = userEditorMasterDataDockable;
			else
				frontDockable = spreadSheetEditorSheetDockable;

		stackDockStation.drop(userEditorMasterDataDockable);
		
		
		spreadSheetEditorFIXInput = new SpreadSheetEditorMDInput(this);

		DefaultDockable spreadSheetEditorFIXInputDockable = new DefaultDockable(spreadSheetEditorFIXInput);
		spreadSheetEditorFIXInputDockable.setFactoryID("Market Data Input");
		spreadSheetEditorFIXInputDockable.setTitleText("Market Data Input");
		spreadSheetEditorFIXInputDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(spreadSheetEditorFIXInputDockable);
		
		spreadSheetEditorFIXOutput = new SpreadSheetEditorMDOutput(this);

		DefaultDockable spreadSheetEditorFIXOutputDockable = new DefaultDockable(spreadSheetEditorFIXOutput);
		spreadSheetEditorFIXOutputDockable.setFactoryID("Market Data Output");
		spreadSheetEditorFIXOutputDockable.setTitleText("Market Data Output");
		spreadSheetEditorFIXOutputDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(spreadSheetEditorFIXOutputDockable);
		

		RolesResponse rolesResponse = null;
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof RolesResponse) {
			rolesResponse = (RolesResponse) abstractResponse;
			rolePermissionEditor = new RolePermissionEditor(this, rolesResponse.getRoles());

		}
		else if (abstractResponse instanceof ErrorResponse) {
			rolePermissionEditor = new RolePermissionEditor(this, new ArrayList<FRole>());
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		DefaultDockable newRolePermissionEditorDockable = new DefaultDockable(rolePermissionEditor);
		newRolePermissionEditorDockable.setFactoryID("Roles");
		newRolePermissionEditorDockable.setTitleText("Roles");
		newRolePermissionEditorDockable.setTitleIcon(folderRed);

		stackDockStation.drop(newRolePermissionEditorDockable);

		for (int i = 0; i < stackDockStation.getDockableCount(); i++) {

			if (stackDockStation.getDockable(i).getTitleText().equals(frontDockable.getTitleText()))
				stackDockStation.setFrontDockable(stackDockStation.getDockable(i));
		}

		checkDirty();
	}

	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public SpreadSheet getSpreadSheet() {

		return spreadSheet;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {
		
		spreadSheetEditorMasterDataterData.save();
		spreadSheetEditorFIXInput.save();
		spreadSheetEditorFIXOutput.save();
		rolePermissionEditor.save();

		boolean newSheet = false;

		if (spreadSheet.getId() == 0)
			newSheet = true;

		super.save();

		if (newSheet)
			basisClientConnector.sendRequest(new OpenSpreadSheetRequest(spreadSheet.getId(), RequestIDManager.getInstance().getID()));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (spreadSheetEditorMasterDataterData != null && spreadSheetEditorMasterDataterData.isDirty())
			dirty = true;

		if (spreadSheetEditorFIXInput != null && spreadSheetEditorFIXInput.isDirty())
			dirty = true;
		
		if (spreadSheetEditorFIXOutput != null && spreadSheetEditorFIXOutput.isDirty())
			dirty = true;

		
		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (spreadSheetEditorMasterDataterData != null && !spreadSheetEditorMasterDataterData.isConsistent())
			consistent = false;

		setConsistent(consistent);

		setDirty(dirty);
	}


	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateContent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject)
	 */
	@Override
	public void updateContent(AbstractBusinessObject abstractBusinessObject) {

		super.updateContent(abstractBusinessObject);
		
		spreadSheet = (SpreadSheet) abstractBusinessObject;

		spreadSheetEditorMasterDataterData.updateContent();
		
		spreadSheetEditorFIXInput.updateContent();
		
		spreadSheetEditorFIXOutput.updateContent();

		spreadSheetEditorSheet.updateContent();

		rolePermissionEditor.updateContent(null);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateRoles(java.util.List)
	 */
	@Override
	public void updateRoles(List<FRole> roles) {

		rolePermissionEditor.updateContent(roles);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		if (spreadSheet.getId() != 0)
			basisClientConnector.sendRequest(new CloseSpreadSheetRequest(spreadSheet.getId(), RequestIDManager.getInstance().getID()));
		spreadSheetEditorSheet.closeAbstractBusinessObjectEditor();
		super.closeAbstractBusinessObjectEditor();
		
	}

	/**
	 * On update sheet cell response.
	 *
	 * @param updateSheetCellResponse the update sheet cell response
	 */
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {

		spreadSheetEditorSheet.onUpdateSheetCellResponse(updateSheetCellResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (spreadSheet.getId() != 0)
			basisClientConnector.sendRequest(new OpenSpreadSheetRequest(spreadSheet.getId(), RequestIDManager.getInstance().getID()));
	}

	/**
	 * On update sheet cell format response.
	 *
	 * @param updateSheetCellFormatResponse the update sheet cell format response
	 */
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {

		spreadSheetEditorSheet.onUpdateSheetCellFormatResponse(updateSheetCellFormatResponse);

	}

	/**
	 * On update column format response.
	 *
	 * @param updateColumnFormatResponse the update column format response
	 */
	public void onUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse) {

		spreadSheetEditorSheet.onUpdateColumnFormatResponse(updateColumnFormatResponse);

	}

	/**
	 * On update row format response.
	 *
	 * @param updateRowFormatResponse the update row format response
	 */
	public void onUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse) {

		spreadSheetEditorSheet.onUpdateRowFormatResponse(updateRowFormatResponse);

	}

	/**
	 * On update sheet conditional format response.
	 *
	 * @param updateSheetConditionalFormatResponse the update sheet conditional format response
	 */
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {

		spreadSheetEditorSheet.onUpdateSheetConditionalFormatResponse(updateSheetConditionalFormatResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#handleFocus()
	 */
	public void handleFocus() {

		spreadSheetEditorSheet.handleFocus();
	}

	/**
	 * Format.
	 *
	 * @param formatAction the format action
	 */
	public void format(FormatAction formatAction) {

		spreadSheetEditorSheet.format(formatAction);

	}

	/**
	 * On update full sheet response.
	 *
	 * @param updateFullSheetResponse the update full sheet response
	 */
	public void onUpdateFullSheetResponse(UpdateFullSheetResponse updateFullSheetResponse) {

		spreadSheetEditorSheet.onUpdateFullSheetResponse(updateFullSheetResponse);

	}

	/**
	 * Switch showing formula.
	 *
	 * @param showingFormula the showing formula
	 */
	public void switchShowingFormula(boolean showingFormula) {

		spreadSheetEditorSheet.switchShowingFormula(showingFormula);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#getAbstractBusinessObject()
	 */
	@Override
	public AbstractBusinessObject getAbstractBusinessObject() {

		return spreadSheet;
	}

	/**
	 * Gets the selection state.
	 *
	 * @return the selection state
	 */
	public int getSelectionState() {

		return spreadSheetEditorSheet.getSelectionState();
	}

}
