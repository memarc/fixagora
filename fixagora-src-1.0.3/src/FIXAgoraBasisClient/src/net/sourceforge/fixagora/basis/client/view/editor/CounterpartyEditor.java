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
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class CounterpartyEditor.
 */
public class CounterpartyEditor extends AbstractBusinessObjectEditor {

	private Counterparty counterparty = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private CounterpartyEditorMasterData counterpartyMasterData = null;

	private CounterpartyEditorCommunication counterpartyEditorCommunication = null;

	private CounterPartyEditorPartyID counterPartyEditorPartyID = null;

	private RolePermissionEditor rolePermissionEditor = null;

	/**
	 * Instantiates a new counterparty editor.
	 *
	 * @param counterparty the counterparty
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public CounterpartyEditor(Counterparty counterparty, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(counterparty, mainPanel, basisClientConnector);
		this.counterparty = counterparty;
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

		counterpartyMasterData = new CounterpartyEditorMasterData(this);

		DefaultDockable userEditorMasterDataDockable = new DefaultDockable(counterpartyMasterData);
		userEditorMasterDataDockable.setFactoryID("Master data");
		userEditorMasterDataDockable.setTitleText("Master data");
		userEditorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			frontDockable = userEditorMasterDataDockable;

		stackDockStation.drop(userEditorMasterDataDockable);

		counterpartyEditorCommunication = new CounterpartyEditorCommunication(this);

		DefaultDockable counterpartyEditorCommunicationDockable = new DefaultDockable(counterpartyEditorCommunication);
		counterpartyEditorCommunicationDockable.setFactoryID("Communication");
		counterpartyEditorCommunicationDockable.setTitleText("Communication");
		counterpartyEditorCommunicationDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(counterpartyEditorCommunicationDockable);

		counterPartyEditorPartyID = new CounterPartyEditorPartyID(this);

		DefaultDockable counterPartyEditorPartyIDDockable = new DefaultDockable(counterPartyEditorPartyID);
		counterPartyEditorPartyIDDockable.setFactoryID("Party ID");
		counterPartyEditorPartyIDDockable.setTitleText("Party ID");
		counterPartyEditorPartyIDDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(counterPartyEditorPartyIDDockable);

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

		stackDockStation.setFrontDockable(userEditorMasterDataDockable);

		checkDirty();
	}

	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public Counterparty getCounterparty() {

		return counterparty;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		counterpartyMasterData.save();
		counterpartyEditorCommunication.save();
		counterPartyEditorPartyID.save();
		rolePermissionEditor.save();

		super.save();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (counterpartyMasterData != null && counterpartyMasterData.isDirty())
			dirty = true;

		if (counterpartyEditorCommunication != null && counterpartyEditorCommunication.isDirty())
			dirty = true;

		if (counterPartyEditorPartyID != null && counterPartyEditorPartyID.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (counterpartyMasterData != null && !counterpartyMasterData.isConsistent())
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

		this.counterparty = (Counterparty) abstractBusinessObject;
		counterpartyMasterData.updateContent();
		counterPartyEditorPartyID.updateContent();
		counterpartyEditorCommunication.updateContent();
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

		return counterparty;
	}

}
