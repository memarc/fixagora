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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class FUserEditor.
 */
public class FUserEditor extends AbstractBusinessObjectEditor {

	private FUser user = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private FUserEditorMasterData userEditorMasterData = null;

	private FUserEditorAssignedRoles userEditorAssignedRoles = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private FUserEditorAddress userEditorAdress = null;
	
	private FUserEditorPartyID userEditorPartyID = null;
	
	private List<FRole> roles = new ArrayList<FRole>();

	/**
	 * Instantiates a new f user editor.
	 *
	 * @param user the user
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public FUserEditor(FUser user, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(user, mainPanel, basisClientConnector);
		
		this.user = user;

		if (user.getId() == 0) {

			user.setMainPanelPanelHeight(basisClientConnector.getFUser().getMainPanelPanelHeight());
			user.setTreePanelWidth(basisClientConnector.getFUser().getTreePanelWidth());
		}

		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		

		userEditorMasterData = new FUserEditorMasterData(this);

		DefaultDockable newUserEditorMasterDataDockable = new DefaultDockable(userEditorMasterData);
		newUserEditorMasterDataDockable.setFactoryID("Master Data");
		newUserEditorMasterDataDockable.setTitleText("Master Data");
		newUserEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(newUserEditorMasterDataDockable);

		userEditorAdress = new FUserEditorAddress(this);

		DefaultDockable fUserEditorAdressDockable = new DefaultDockable(userEditorAdress);
		fUserEditorAdressDockable.setFactoryID("Address");
		fUserEditorAdressDockable.setTitleText("Address");
		fUserEditorAdressDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(fUserEditorAdressDockable);


		RolesResponse rolesResponse = null;
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof RolesResponse) {
			
			rolesResponse = (RolesResponse) abstractResponse;
			
			List<FRole> fRoles = new ArrayList<FRole>(rolesResponse.getRoles());

			userEditorAssignedRoles = new FUserEditorAssignedRoles(this, fRoles);
			
			rolePermissionEditor = new RolePermissionEditor(this, rolesResponse.getRoles());

		}
		else if (abstractResponse instanceof ErrorResponse) {
			userEditorAssignedRoles = new FUserEditorAssignedRoles(this, new ArrayList<FRole>());
			rolePermissionEditor = new RolePermissionEditor(this, new ArrayList<FRole>());
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
		DefaultDockable userEditorAssignedRolesDockable = new DefaultDockable(userEditorAssignedRoles);
		userEditorAssignedRolesDockable.setFactoryID("Assigned Roles");
		userEditorAssignedRolesDockable.setTitleText("Assigned Roles");
		userEditorAssignedRolesDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(userEditorAssignedRolesDockable);
		
		
		userEditorPartyID = new FUserEditorPartyID(this);

		DefaultDockable userEditorPartyIDDockable = new DefaultDockable(userEditorPartyID);
		userEditorPartyIDDockable.setFactoryID("Party ID");
		userEditorPartyIDDockable.setTitleText("Party ID");
		userEditorPartyIDDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(userEditorPartyIDDockable);

		
		DefaultDockable newRolePermissionEditorDockable = new DefaultDockable(rolePermissionEditor);
		newRolePermissionEditorDockable.setFactoryID("Roles");
		newRolePermissionEditorDockable.setTitleText("Roles");
		newRolePermissionEditorDockable.setTitleIcon(folderRed);

		stackDockStation.drop(newRolePermissionEditorDockable);

		stackDockStation.setFrontDockable(newUserEditorMasterDataDockable);
		
		if(rolesResponse!=null)
			updateRoles(rolesResponse.getRoles());

		checkDirty();
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public FUser getUser() {

		return user;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		if (!userEditorMasterData.checkPassword()) {

			JOptionPane jOptionPane = new JOptionPane("Incorrect password.", JOptionPane.ERROR_MESSAGE);
			JDialog dialog = jOptionPane.createDialog(getComponent(), "Password Validation");

			dialog.setModal(false);
			dialog.setVisible(true);
		}
		else {
			
			userEditorMasterData.save();
			userEditorAdress.save();
			userEditorAssignedRoles.save();
			userEditorPartyID.save();
			rolePermissionEditor.save();
			
			super.save();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	public void checkDirty() {

		boolean dirty = false;
		
		if (userEditorMasterData != null && userEditorMasterData.isDirty())
			dirty = true;
		
		if (userEditorAdress != null && userEditorAdress.isDirty())
			dirty = true;
		
		if (userEditorAssignedRoles != null && userEditorAssignedRoles.isDirty())
			dirty = true;
		
		if (userEditorPartyID != null && userEditorPartyID.isDirty())
			dirty = true;
		
		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;
		
		boolean consistent = true;
		
		if(userEditorMasterData!=null&&!userEditorMasterData.isConsistent())
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
		
		user = (FUser) abstractBusinessObject;
		
		userEditorMasterData.updateContent();
		
		userEditorAdress.updateContent();
		
		userEditorPartyID.updateContent();
		
		rolePermissionEditor.updateContent(null);
		
		userEditorAssignedRoles.updateContent();
	}

	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateRoles(java.util.List)
	 */
	@Override
	public void updateRoles(List<FRole> roles) {

		rolePermissionEditor.updateContent(roles);
		this.roles=roles;
		userEditorAssignedRoles.updateContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#getAbstractBusinessObject()
	 */
	@Override
	public AbstractBusinessObject getAbstractBusinessObject() {

		return user;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<FRole> getRoles() {
		
		return roles;
	}
}
