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
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class DefaultBusinessGroupEditor.
 */
public class DefaultBusinessGroupEditor extends AbstractBusinessObjectEditor {

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private DefaultBusinessGroupMasterData userEditorMasterData = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private AbstractBusinessObject abstractBusinessObject = null;

	/**
	 * Instantiates a new default business group editor.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public DefaultBusinessGroupEditor(AbstractBusinessObject abstractBusinessObject, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(abstractBusinessObject, mainPanel, basisClientConnector);
		this.abstractBusinessObject = abstractBusinessObject;
		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		userEditorMasterData = new DefaultBusinessGroupMasterData(this);

		DefaultDockable newUserEditorMasterDataDockable = new DefaultDockable(userEditorMasterData);
		newUserEditorMasterDataDockable.setFactoryID(Long.toString(System.currentTimeMillis()));
		newUserEditorMasterDataDockable.setTitleText("Master data");
		newUserEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(newUserEditorMasterDataDockable);

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
		newRolePermissionEditorDockable.setFactoryID(Long.toString(System.currentTimeMillis()));
		newRolePermissionEditorDockable.setTitleText("Roles");
		newRolePermissionEditorDockable.setTitleIcon(folderRed);

		stackDockStation.drop(newRolePermissionEditorDockable);

		stackDockStation.setFrontDockable(newUserEditorMasterDataDockable);

		checkDirty();
	}

	/**
	 * Gets the business object group.
	 *
	 * @return the business object group
	 */
	public BusinessObjectGroup getBusinessObjectGroup() {

		return (BusinessObjectGroup) getAbstractBusinessObject();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		userEditorMasterData.save();
		rolePermissionEditor.save();

		super.save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (userEditorMasterData != null && userEditorMasterData.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (userEditorMasterData != null && !userEditorMasterData.isConsistent())
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

		this.abstractBusinessObject = abstractBusinessObject;
		userEditorMasterData.updateContent();

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

		return abstractBusinessObject;
	}

}
