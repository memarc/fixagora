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
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class FIXInitiatorEditor.
 */
public class FIXInitiatorEditor extends AbstractBusinessObjectEditor {

	private FIXInitiator fixInitiator = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private FIXInitiatorEditorMasterData fixInitiatorMasterData = null;
	
	private FIXInitiatorEditorMarketData fixInitiatorEditorEntryTypes = null;
	
	private FIXInitiatorEditorAssignedSecurity fixInitiatorEditorAssignedSecurity = null;
	
	private RolePermissionEditor rolePermissionEditor = null;

	/**
	 * Instantiates a new fIX initiator editor.
	 *
	 * @param fixInitiator the fix initiator
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public FIXInitiatorEditor(FIXInitiator fixInitiator, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(fixInitiator, mainPanel, basisClientConnector);
		this.fixInitiator = fixInitiator;
		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		Dockable frontDockable = stackDockStation.getFrontDockable();

		while(stackDockStation.getDockableCount()>0)
			stackDockStation.remove(0);
		
		fixInitiatorMasterData = new FIXInitiatorEditorMasterData(this);
		
		DefaultDockable fixInitiatorMasterDataDockable = new DefaultDockable(fixInitiatorMasterData);
		fixInitiatorMasterDataDockable.setFactoryID("Master Data");
		fixInitiatorMasterDataDockable.setTitleText("Master Data");
		fixInitiatorMasterDataDockable.setTitleIcon(folderBlue);
		
		if(frontDockable==null)
			frontDockable=fixInitiatorMasterDataDockable;
		
		stackDockStation.drop(fixInitiatorMasterDataDockable);
		
		fixInitiatorEditorEntryTypes = new FIXInitiatorEditorMarketData(this);
		
		DefaultDockable fixInitiatorEditorEntryTypesDockable = new DefaultDockable(fixInitiatorEditorEntryTypes);
		fixInitiatorEditorEntryTypesDockable.setFactoryID("Market Data");
		fixInitiatorEditorEntryTypesDockable.setTitleText("Market Data");
		fixInitiatorEditorEntryTypesDockable.setTitleIcon(folderBlue);
		
		stackDockStation.drop(fixInitiatorEditorEntryTypesDockable);
		
		fixInitiatorEditorAssignedSecurity = new FIXInitiatorEditorAssignedSecurity(this);
		
		DefaultDockable fixInitiatorEditorAssignedSecurityDockable = new DefaultDockable(fixInitiatorEditorAssignedSecurity);
		fixInitiatorEditorAssignedSecurityDockable.setFactoryID("Subscribed Securities");
		fixInitiatorEditorAssignedSecurityDockable.setTitleText("Subscribed Securities");
		fixInitiatorEditorAssignedSecurityDockable.setTitleIcon(folderBlue);
		
		stackDockStation.drop(fixInitiatorEditorAssignedSecurityDockable);
		

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

		stackDockStation.setFrontDockable(fixInitiatorMasterDataDockable);
		
		checkDirty();		
	}
	
	/**
	 * Gets the fix initiator.
	 *
	 * @return the fix initiator
	 */
	public FIXInitiator getFixInitiator() {
	
		return fixInitiator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

			fixInitiatorMasterData.save();
			fixInitiatorEditorEntryTypes.save();
			fixInitiatorEditorAssignedSecurity.save();
			rolePermissionEditor.save();

			super.save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;
		
		if(fixInitiatorMasterData!=null&&fixInitiatorMasterData.isDirty())
			dirty = true;
		
		if(fixInitiatorEditorAssignedSecurity!=null&&fixInitiatorEditorAssignedSecurity.isDirty())
			dirty = true;
		
		if(fixInitiatorEditorEntryTypes!=null&&fixInitiatorEditorEntryTypes.isDirty())
			dirty = true;
		
		if(rolePermissionEditor!=null&&rolePermissionEditor.isDirty())
			dirty = true;
		
		boolean consistent = true;
		
		if(fixInitiatorMasterData!=null&&!fixInitiatorMasterData.isConsistent())
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
		
		this.fixInitiator = (FIXInitiator)abstractBusinessObject;
		fixInitiatorMasterData.updateContent();
		fixInitiatorEditorEntryTypes.updateContent();
		fixInitiatorEditorAssignedSecurity.updateContent();
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

		return fixInitiator;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#isEditable()
	 */
	public boolean isEditable() {

		return basisClientConnector.getFUser().canWrite(getAbstractBusinessObject()) && fixInitiator.getStartLevel()==0;
	}
}
