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
package net.sourceforge.fixagora.buyside.client.view.editor;

import java.util.ArrayList;
import java.util.List;

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
import net.sourceforge.fixagora.buyside.shared.communication.CloseQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.HistoricalBuySideDataResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuotePageRequest;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;

import org.apache.log4j.Logger;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class BuySideQuotePageEditor.
 */
public class BuySideQuotePageEditor extends AbstractBusinessObjectEditor {

	private BuySideQuotePage buySideQuotePage = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private BuySideQuotePageEditorMasterData buySideQuotePageEditorMasterData = null;

	private BuySideQuotePageEditorAssignedSecurity buySideQuotePageEditorAssignedSecurity = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private BuySideQuotePageEditorMonitor buySideQuotePageEditorMonitor;
	
	private BuySideQuotePageEditorChart buySideQuotePageEditorChart;
	
	private static Logger log = Logger.getLogger(BuySideQuotePageEditor.class);

	/**
	 * Instantiates a new buy side quote page editor.
	 *
	 * @param buySideQuotePage the buy side quote page
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public BuySideQuotePageEditor(BuySideQuotePage buySideQuotePage, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(buySideQuotePage, mainPanel, basisClientConnector);
		this.buySideQuotePage = buySideQuotePage;
		
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

		buySideQuotePageEditorMonitor = new BuySideQuotePageEditorMonitor(this);

		DefaultDockable buySideQuotePageEditorMonitorDockable = new DefaultDockable(buySideQuotePageEditorMonitor);
		buySideQuotePageEditorMonitorDockable.setFactoryID("Quotes");
		buySideQuotePageEditorMonitorDockable.setTitleText("Quotes");
		buySideQuotePageEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideQuotePageEditorMonitorDockable);
		
		buySideQuotePageEditorChart = new BuySideQuotePageEditorChart(this);

		DefaultDockable buySideQuotePageEditorChartDockable = new DefaultDockable(buySideQuotePageEditorChart);
		buySideQuotePageEditorChartDockable.setFactoryID("Chart");
		buySideQuotePageEditorChartDockable.setTitleText("Chart");
		buySideQuotePageEditorChartDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideQuotePageEditorChartDockable);

		buySideQuotePageEditorMasterData = new BuySideQuotePageEditorMasterData(this);

		DefaultDockable buySideBookEditorMasterDataDockable = new DefaultDockable(buySideQuotePageEditorMasterData);
		buySideBookEditorMasterDataDockable.setFactoryID("Master data");
		buySideBookEditorMasterDataDockable.setTitleText("Master data");
		buySideBookEditorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			if (buySideQuotePage.getId() == 0)
				frontDockable = buySideBookEditorMasterDataDockable;
			else
				frontDockable = buySideQuotePageEditorMonitorDockable;

		stackDockStation.drop(buySideBookEditorMasterDataDockable);

		buySideQuotePageEditorAssignedSecurity = new BuySideQuotePageEditorAssignedSecurity(this);

		DefaultDockable buySideQuotePageEditorAssignedSecurityDockable = new DefaultDockable(buySideQuotePageEditorAssignedSecurity);
		buySideQuotePageEditorAssignedSecurityDockable.setFactoryID("Securities");
		buySideQuotePageEditorAssignedSecurityDockable.setTitleText("Securities");
		buySideQuotePageEditorAssignedSecurityDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideQuotePageEditorAssignedSecurityDockable);

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
	 * Gets the buy side quote page.
	 *
	 * @return the buy side quote page
	 */
	public BuySideQuotePage getBuySideQuotePage() {

		return buySideQuotePage;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		try {
			
			buySideQuotePageEditorMasterData.save();
			buySideQuotePageEditorAssignedSecurity.save();
			rolePermissionEditor.save();

			super.save();			
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (buySideQuotePageEditorMasterData != null && buySideQuotePageEditorMasterData.isDirty())
			dirty = true;

		if (buySideQuotePageEditorAssignedSecurity != null && buySideQuotePageEditorAssignedSecurity.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (buySideQuotePageEditorMasterData != null && !buySideQuotePageEditorMasterData.isConsistent())
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

		this.buySideQuotePage = (BuySideQuotePage) abstractBusinessObject;

		buySideQuotePageEditorMasterData.updateContent();

		buySideQuotePageEditorAssignedSecurity.updateContent();

		buySideQuotePageEditorMonitor.updateContent();
		
		buySideQuotePageEditorChart.updateContent();

		rolePermissionEditor.updateContent(null);
		
		basisClientConnector.sendRequest(new OpenQuotePageRequest(buySideQuotePage.getId(), RequestIDManager.getInstance().getID()));
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

		return buySideQuotePage;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		buySideQuotePageEditorMonitor.closeAbstractBusinessObjectEditor();
		if (buySideQuotePage.getId() != 0)
			basisClientConnector.sendRequest(new CloseQuotePageRequest(buySideQuotePage.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();
		
		for (Dockable dockable : getMainPanel().getNamedDockables())
			if (dockable instanceof BuySideBookEditor) {
				BuySideBookEditor buySideBookEditor = (BuySideBookEditor)dockable;
				if(buySideBookEditor.getBuySideBook().getId()==buySideQuotePage.getParent().getId())
				{
					buySideBookEditor.clearCrawler(buySideQuotePage);
				}
			}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (buySideQuotePage.getId() != 0)
			basisClientConnector.sendRequest(new OpenQuotePageRequest(buySideQuotePage.getId(), RequestIDManager.getInstance().getID()));
	}

	/**
	 * On update buy side md input entry response.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public void onUpdateBuySideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		try {
			buySideQuotePageEditorMonitor.onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);
			
			buySideQuotePageEditorChart.onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);

			for (Dockable dockable : getMainPanel().getNamedDockables())
				if (dockable instanceof BuySideBookEditor) {
					BuySideBookEditor buySideBookEditor = (BuySideBookEditor)dockable;
					if(buySideBookEditor.getBuySideBook().getId()==buySideQuotePage.getParent().getId())
					{
						buySideBookEditor.onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);
					}
				}
		}
		catch (Exception e) {
			
			log.error("Bug", e);
		}
		
	}

	/**
	 * Gets the update buy side md input entry response.
	 *
	 * @return the update buy side md input entry response
	 */
	public UpdateBuySideMDInputEntryResponse getUpdateBuySideMDInputEntryResponse() {

		// TODO Auto-generated method stub
		return buySideQuotePageEditorMonitor.getUpdateBuySideMDInputEntryResponse();
	}

	/**
	 * On historical data response.
	 *
	 * @param historicalDataResponse the historical data response
	 */
	public void onHistoricalDataResponse(HistoricalBuySideDataResponse historicalDataResponse) {

		if(buySideQuotePage.getId()==historicalDataResponse.getBuysideQuotePage())
			buySideQuotePageEditorChart.onHistoricalDataResponse(historicalDataResponse);
	}
}
