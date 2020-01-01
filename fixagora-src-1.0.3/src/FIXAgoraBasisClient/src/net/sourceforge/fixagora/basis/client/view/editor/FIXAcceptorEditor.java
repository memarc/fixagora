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
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class FIXAcceptorEditor.
 */
public class FIXAcceptorEditor extends AbstractBusinessObjectEditor {

	private FIXAcceptor fixAcceptor = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private FIXAcceptorEditorMasterData fixAcceptorMasterData = null;

	private FIXAcceptorEditorFIXAcceptorTarget fixAcceptorEditorFIXAcceptorTarget = null;

	private RolePermissionEditor rolePermissionEditor = null;

	/**
	 * Instantiates a new fIX acceptor editor.
	 *
	 * @param fixAcceptor the fix acceptor
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public FIXAcceptorEditor(FIXAcceptor fixAcceptor, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(fixAcceptor, mainPanel, basisClientConnector);
		this.fixAcceptor = fixAcceptor;
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

		fixAcceptorMasterData = new FIXAcceptorEditorMasterData(this);

		DefaultDockable fixInitiatorMasterDataDockable = new DefaultDockable(fixAcceptorMasterData);
		fixInitiatorMasterDataDockable.setFactoryID("Master data");
		fixInitiatorMasterDataDockable.setTitleText("Master data");
		fixInitiatorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			frontDockable = fixInitiatorMasterDataDockable;

		stackDockStation.drop(fixInitiatorMasterDataDockable);

		fixAcceptorEditorFIXAcceptorTarget = new FIXAcceptorEditorFIXAcceptorTarget(this);

		DefaultDockable fixAcceptorEditorFIXAcceptorTargetDockable = new DefaultDockable(fixAcceptorEditorFIXAcceptorTarget);
		fixAcceptorEditorFIXAcceptorTargetDockable.setFactoryID("FIX Targets");
		fixAcceptorEditorFIXAcceptorTargetDockable.setTitleText("FIX Targets");
		fixAcceptorEditorFIXAcceptorTargetDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(fixAcceptorEditorFIXAcceptorTargetDockable);

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
	 * Gets the fix acceptor.
	 *
	 * @return the fix acceptor
	 */
	public FIXAcceptor getFixAcceptor() {

		return fixAcceptor;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		fixAcceptorMasterData.save();
		fixAcceptorEditorFIXAcceptorTarget.save();
		rolePermissionEditor.save();
		super.save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (fixAcceptorMasterData != null && fixAcceptorMasterData.isDirty())
			dirty = true;

		if (fixAcceptorEditorFIXAcceptorTarget != null && fixAcceptorEditorFIXAcceptorTarget.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (fixAcceptorMasterData != null && !fixAcceptorMasterData.isConsistent())
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

		this.fixAcceptor = (FIXAcceptor) abstractBusinessObject;
		fixAcceptorMasterData.updateContent();

		fixAcceptorEditorFIXAcceptorTarget.updateContent();

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

		return fixAcceptor;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#isEditable()
	 */
	public boolean isEditable() {

		return basisClientConnector.getFUser().canWrite(getAbstractBusinessObject()) && fixAcceptor.getStartLevel()==0;
	}
}
