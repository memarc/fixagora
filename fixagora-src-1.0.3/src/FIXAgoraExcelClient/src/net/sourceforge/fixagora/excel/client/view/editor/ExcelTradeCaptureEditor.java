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
package net.sourceforge.fixagora.excel.client.view.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.RolePermissionEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.excel.shared.communication.CloseExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureRequest;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureResponse;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCapture;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class ExcelTradeCaptureEditor.
 */
public class ExcelTradeCaptureEditor extends AbstractBusinessObjectEditor {

	private ExcelTradeCapture excelTradeCapture = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private ExcelTradeCaptureEditorMasterData tradeCaptureEditorMasterData = null;

	private ExcelTradeCaptureEditorMonitor tradeCaptureEditorMonitor = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private Icon originalIcon;

	/**
	 * Instantiates a new excel trade capture editor.
	 *
	 * @param excelTradeCapture the excel trade capture
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public ExcelTradeCaptureEditor(ExcelTradeCapture excelTradeCapture, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(excelTradeCapture, mainPanel, basisClientConnector);
		this.excelTradeCapture = excelTradeCapture;
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

		tradeCaptureEditorMonitor = new ExcelTradeCaptureEditorMonitor(this);

		DefaultDockable tradeCaptureEditorMonitorDockable = new DefaultDockable(tradeCaptureEditorMonitor);
		tradeCaptureEditorMonitorDockable.setFactoryID("Trades");
		tradeCaptureEditorMonitorDockable.setTitleText("Trades");
		tradeCaptureEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(tradeCaptureEditorMonitorDockable);

		tradeCaptureEditorMasterData = new ExcelTradeCaptureEditorMasterData(this);

		DefaultDockable tradeCaptureEditorMasterDataDockable = new DefaultDockable(tradeCaptureEditorMasterData);
		tradeCaptureEditorMasterDataDockable.setFactoryID("Master Data");
		tradeCaptureEditorMasterDataDockable.setTitleText("Master Data");
		tradeCaptureEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(tradeCaptureEditorMasterDataDockable);

		if (frontDockable == null)
			if (excelTradeCapture.getId() == 0)
				frontDockable = tradeCaptureEditorMasterDataDockable;
			else
				frontDockable = tradeCaptureEditorMonitorDockable;

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
	 * Gets the excel trade capture.
	 *
	 * @return the excel trade capture
	 */
	public ExcelTradeCapture getExcelTradeCapture() {

		return excelTradeCapture;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		tradeCaptureEditorMasterData.save();
		rolePermissionEditor.save();

		super.save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		if (excelTradeCapture.getId() != 0)
			basisClientConnector.sendRequest(new CloseExcelTradeCaptureRequest(excelTradeCapture.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (excelTradeCapture.getId() != 0) {
			OpenExcelTradeCaptureResponse openExcelTradeCaptureResponse = (OpenExcelTradeCaptureResponse) basisClientConnector
					.sendRequest(new OpenExcelTradeCaptureRequest(excelTradeCapture.getId(), RequestIDManager.getInstance().getID()));
			tradeCaptureEditorMonitor.onOpenExcelTradeCaptureResponse(openExcelTradeCaptureResponse);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (tradeCaptureEditorMasterData != null && tradeCaptureEditorMasterData.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (tradeCaptureEditorMasterData != null && !tradeCaptureEditorMasterData.isConsistent())
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

		this.excelTradeCapture = (ExcelTradeCapture) abstractBusinessObject;

		tradeCaptureEditorMonitor.updateContent();

		if (excelTradeCapture.getId() != 0) {
			OpenExcelTradeCaptureResponse openExcelTradeCaptureResponse = (OpenExcelTradeCaptureResponse) basisClientConnector
					.sendRequest(new OpenExcelTradeCaptureRequest(excelTradeCapture.getId(), RequestIDManager.getInstance().getID()));
			tradeCaptureEditorMonitor.onOpenExcelTradeCaptureResponse(openExcelTradeCaptureResponse);
		}

		tradeCaptureEditorMasterData.updateContent();

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
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#getAbstractBusinessObject()
	 */
	@Override
	public AbstractBusinessObject getAbstractBusinessObject() {

		return excelTradeCapture;
	}

	/**
	 * Stop warning.
	 */
	public synchronized void stopWarning() {

		if (originalIcon != null)
			setTitleIcon(originalIcon);

	}

	/**
	 * On excel trade capture entry response.
	 *
	 * @param excelTradeCaptureEntryResponse the excel trade capture entry response
	 */
	public synchronized void onExcelTradeCaptureEntryResponse(ExcelTradeCaptureEntryResponse excelTradeCaptureEntryResponse) {
		
		if (excelTradeCaptureEntryResponse.getExcelTradeCaptureEntry().getExcelTradeCapture() == excelTradeCapture.getId()) {
						
			tradeCaptureEditorMonitor.onExcelTradeCaptureEntryResponse(excelTradeCaptureEntryResponse);
		}
	}

}
