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
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class TraderEditor.
 */
public class TraderEditor extends AbstractBusinessObjectEditor {

	private Trader trader = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private TraderEditorMasterData traderEditorMasterData = null;
	
	private TraderEditorPartyID traderEditorPartyID = null;

	private RolePermissionEditor rolePermissionEditor = null;
	
	/**
	 * Instantiates a new trader editor.
	 *
	 * @param trader the trader
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public TraderEditor(Trader trader, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(trader, mainPanel, basisClientConnector);
		
		this.trader = trader;

		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		

		traderEditorMasterData = new TraderEditorMasterData(this);

		DefaultDockable tradeEditorMasterDataDockable = new DefaultDockable(traderEditorMasterData);
		tradeEditorMasterDataDockable.setFactoryID("Master Data");
		tradeEditorMasterDataDockable.setTitleText("Master Data");
		tradeEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(tradeEditorMasterDataDockable);
		
		traderEditorPartyID = new TraderEditorPartyID(this);

		DefaultDockable traderEditorPartyIDDockable = new DefaultDockable(traderEditorPartyID);
		traderEditorPartyIDDockable.setFactoryID("Party ID");
		traderEditorPartyIDDockable.setTitleText("Party ID");
		traderEditorPartyIDDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(traderEditorPartyIDDockable);

		
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

		stackDockStation.setFrontDockable(tradeEditorMasterDataDockable);
		
		checkDirty();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {


			traderEditorMasterData.save();
			traderEditorPartyID.save();
			rolePermissionEditor.save();
			
			super.save();
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	public void checkDirty() {

		boolean dirty = false;
		
		if (traderEditorMasterData != null && traderEditorMasterData.isDirty())
			dirty = true;
		
		if (traderEditorPartyID != null && traderEditorPartyID.isDirty())
			dirty = true;
		
		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;
		
		boolean consistent = true;
		
		if(traderEditorMasterData!=null&&!traderEditorMasterData.isConsistent())
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
		
		trader = (Trader) abstractBusinessObject;
		
		traderEditorMasterData.updateContent();
		
		traderEditorPartyID.updateContent();
				
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

		return trader;
	}

	
	/**
	 * Gets the trader.
	 *
	 * @return the trader
	 */
	public Trader getTrader() {
	
		return trader;
	}
	

}
