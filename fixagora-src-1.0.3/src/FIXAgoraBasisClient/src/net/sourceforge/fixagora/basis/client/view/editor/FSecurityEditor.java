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
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueSecurityIDResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class FSecurityEditor.
 */
public class FSecurityEditor extends AbstractBusinessObjectEditor {

	private FSecurity security = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private FSecurityEditorMasterData securityEditorMasterData = null;

	private FSecurityEditorDetails1 securityEditorDetails1 = null;

	private FSecurityEditorDetails2 securityEditorDetails2 = null;

	private FSecurityEditorAttribute securityEditorAttribute = null;

	private FSecurityEditorEvent securityEditorEvent = null;

	private FSecurityEditorComplexEvent securityEditorComplexEvent = null;

	private FSecurityEditorUnderlying securityEditorUnderlying = null;

	private FSecurityEditorLeg securityEditorLeg = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private final LinkedBlockingQueue<String> checkSecurityIdQueue = new LinkedBlockingQueue<String>();

	private JLabel securityIdWarningLabel = new JLabel("");

	/**
	 * Instantiates a new f security editor.
	 *
	 * @param security the security
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public FSecurityEditor(FSecurity security, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(security, mainPanel, basisClientConnector);

		this.security = security;

		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		securityEditorMasterData = new FSecurityEditorMasterData(this);

		DefaultDockable securityEditorMasterDataDockable = new DefaultDockable(securityEditorMasterData);
		securityEditorMasterDataDockable.setFactoryID("Master Data");
		securityEditorMasterDataDockable.setTitleText("Master Data");
		securityEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorMasterDataDockable);

		securityEditorDetails1 = new FSecurityEditorDetails1(this);

		DefaultDockable securityEditorDetails1Dockable = new DefaultDockable(securityEditorDetails1);
		securityEditorDetails1Dockable.setFactoryID("Details 1");
		securityEditorDetails1Dockable.setTitleText("Details 1");
		securityEditorDetails1Dockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorDetails1Dockable);

		securityEditorDetails2 = new FSecurityEditorDetails2(this);

		DefaultDockable securityEditorDetails2Dockable = new DefaultDockable(securityEditorDetails2);
		securityEditorDetails2Dockable.setFactoryID("Details 2");
		securityEditorDetails2Dockable.setTitleText("Details 2");
		securityEditorDetails2Dockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorDetails2Dockable);

		securityEditorAttribute = new FSecurityEditorAttribute(this);

		DefaultDockable securityEditorAttributeDockable = new DefaultDockable(securityEditorAttribute);
		securityEditorAttributeDockable.setFactoryID("Attributes");
		securityEditorAttributeDockable.setTitleText("Attributes");
		securityEditorAttributeDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorAttributeDockable);

		securityEditorEvent = new FSecurityEditorEvent(this);

		DefaultDockable securityEditorEventDockable = new DefaultDockable(securityEditorEvent);
		securityEditorEventDockable.setFactoryID("Events");
		securityEditorEventDockable.setTitleText("Events");
		securityEditorEventDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorEventDockable);

		securityEditorComplexEvent = new FSecurityEditorComplexEvent(this);

		DefaultDockable securityEditorComplexEventDockable = new DefaultDockable(securityEditorComplexEvent);
		securityEditorComplexEventDockable.setFactoryID("Complex Events");
		securityEditorComplexEventDockable.setTitleText("Complex Events");
		securityEditorComplexEventDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorComplexEventDockable);

		securityEditorUnderlying = new FSecurityEditorUnderlying(this);

		DefaultDockable securityEditorUnderlyingDockable = new DefaultDockable(securityEditorUnderlying);
		securityEditorUnderlyingDockable.setFactoryID("Underlyings");
		securityEditorUnderlyingDockable.setTitleText("Underlyings");
		securityEditorUnderlyingDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorUnderlyingDockable);

		securityEditorLeg = new FSecurityEditorLeg(this);

		DefaultDockable securityEditorLegsDockable = new DefaultDockable(securityEditorLeg);
		securityEditorLegsDockable.setFactoryID("Legs");
		securityEditorLegsDockable.setTitleText("Legs");
		securityEditorLegsDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(securityEditorLegsDockable);

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

		stackDockStation.setFrontDockable(securityEditorMasterDataDockable);

		checkDirty();

		final Thread thread = new Thread() {

			@Override
			public void run() {

				securityIdCheck();
			}
		};

		thread.start();
	}

	/**
	 * Gets the security.
	 *
	 * @return the security
	 */
	public FSecurity getSecurity() {

		return security;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

			securityEditorMasterData.save();
			securityEditorDetails1.save();
			securityEditorDetails2.save();
			securityEditorAttribute.save();
			securityEditorEvent.save();
			securityEditorComplexEvent.save();
			securityEditorUnderlying.save();
			securityEditorLeg.save();
			rolePermissionEditor.save();

			super.save();
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	public void checkDirty() {

		boolean dirty = false;

		if (securityEditorMasterData != null && securityEditorMasterData.isDirty())
			dirty = true;

		if (securityEditorDetails1 != null && securityEditorDetails1.isDirty())
			dirty = true;

		if (securityEditorDetails2 != null && securityEditorDetails2.isDirty())
			dirty = true;

		if (securityEditorAttribute != null && securityEditorAttribute.isDirty())
			dirty = true;

		if (securityEditorEvent != null && securityEditorEvent.isDirty())
			dirty = true;

		if (securityEditorComplexEvent != null && securityEditorComplexEvent.isDirty())
			dirty = true;

		if (securityEditorUnderlying != null && securityEditorUnderlying.isDirty())
			dirty = true;

		if (securityEditorLeg != null && securityEditorLeg.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (securityEditorMasterData != null && !securityEditorMasterData.isConsistent())
			consistent = false;

		if (securityEditorDetails1 != null && !securityEditorDetails1.isConsistent())
			consistent = false;

		if (securityEditorDetails2 != null && !securityEditorDetails2.isConsistent())
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

		security = (FSecurity) abstractBusinessObject;

		securityEditorMasterData.updateContent();

		securityEditorDetails1.updateContent();

		securityEditorDetails2.updateContent();

		securityEditorAttribute.updateContent();

		securityEditorEvent.updateContent();

		securityEditorComplexEvent.updateContent();

		securityEditorUnderlying.updateContent();

		securityEditorLeg.updateContent();

		rolePermissionEditor.updateContent(null);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateRoles(java.util.List)
	 */
	@Override
	public void updateRoles(List<FRole> roles) {

		rolePermissionEditor.updateContent(roles);

	}

	/**
	 * Check security id.
	 *
	 * @param id the id
	 */
	public void checkSecurityID(String id) {

		checkSecurityIdQueue.add(id);
	}

	private void securityIdCheck() {

		while (visible)
			try {

				final String id = checkSecurityIdQueue.take();

				if (visible) {
					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new UniqueSecurityIDRequest(id, security.getId(), RequestIDManager
							.getInstance().getID()));
					if (abstractResponse instanceof UniqueSecurityIDResponse) {
						UniqueSecurityIDResponse uniqueNameResponse = (UniqueSecurityIDResponse) abstractResponse;

						if (!uniqueNameResponse.isUnique()) {

							securityIdWarningLabel.setToolTipText("A security with this identifier already exist.");
							securityIdWarningLabel.setIcon(getBugIcon());

							setConsistent(false);
							getMainPanel().onMainPanelChanged();
						}
					}
					else if (abstractResponse instanceof ErrorResponse) {

						ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
						JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			catch (final InterruptedException e1) {
			}
	}

	/**
	 * Gets the security id warning label.
	 *
	 * @return the security id warning label
	 */
	public JLabel getSecurityIdWarningLabel() {

		return securityIdWarningLabel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#getAbstractBusinessObject()
	 */
	@Override
	public AbstractBusinessObject getAbstractBusinessObject() {

		return security;
	}

}
