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
package net.sourceforge.fixagora.sellside.client.view.editor;

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
import net.sourceforge.fixagora.sellside.shared.communication.CloseQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.HistoricalSellSideDataResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenQuoteMonitorRequest;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;

import org.apache.log4j.Logger;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class SellSideQuotePageEditor.
 */
public class SellSideQuotePageEditor extends AbstractBusinessObjectEditor {

	private SellSideQuotePage sellSideQuotePage = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private SellSideQuotePageEditorMasterData sellSideQuotePageEditorMasterData = null;

	private SellSideQuotePageEditorAssignedSecurity sellSideQuotePageEditorAssignedSecurity = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private SellSideQuotePageEditorMonitor sellSideQuotePageEditorMonitor = null;
	
	private SellSideQuotePageEditorChart sellSideQuotePageEditorChart = null;
	
	private static Logger log = Logger.getLogger(SellSideQuotePageEditor.class);

	/**
	 * Instantiates a new sell side quote page editor.
	 *
	 * @param sellSideQuotePage the sell side quote page
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public SellSideQuotePageEditor(SellSideQuotePage sellSideQuotePage, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(sellSideQuotePage, mainPanel, basisClientConnector);
		this.sellSideQuotePage = sellSideQuotePage;

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

		sellSideQuotePageEditorMonitor = new SellSideQuotePageEditorMonitor(this);

		DefaultDockable sellSideQuotePageEditorMonitorDockable = new DefaultDockable(sellSideQuotePageEditorMonitor);
		sellSideQuotePageEditorMonitorDockable.setFactoryID("Monitor");
		sellSideQuotePageEditorMonitorDockable.setTitleText("Monitor");
		sellSideQuotePageEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideQuotePageEditorMonitorDockable);
		
		sellSideQuotePageEditorChart = new SellSideQuotePageEditorChart(this);

		DefaultDockable sellSideQuotePageEditorChartDockable = new DefaultDockable(sellSideQuotePageEditorChart);
		sellSideQuotePageEditorChartDockable.setFactoryID("Chart");
		sellSideQuotePageEditorChartDockable.setTitleText("Chart");
		sellSideQuotePageEditorChartDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideQuotePageEditorChartDockable);

		sellSideQuotePageEditorMasterData = new SellSideQuotePageEditorMasterData(this);

		DefaultDockable sellSideBookEditorMasterDataDockable = new DefaultDockable(sellSideQuotePageEditorMasterData);
		sellSideBookEditorMasterDataDockable.setFactoryID("Master data");
		sellSideBookEditorMasterDataDockable.setTitleText("Master data");
		sellSideBookEditorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			if (sellSideQuotePage.getId() == 0)
				frontDockable = sellSideBookEditorMasterDataDockable;
			else
				frontDockable = sellSideQuotePageEditorMonitorDockable;

		stackDockStation.drop(sellSideBookEditorMasterDataDockable);

		sellSideQuotePageEditorAssignedSecurity = new SellSideQuotePageEditorAssignedSecurity(this);

		DefaultDockable sellSideQuotePageEditorAssignedSecurityDockable = new DefaultDockable(sellSideQuotePageEditorAssignedSecurity);
		sellSideQuotePageEditorAssignedSecurityDockable.setFactoryID("Securities");
		sellSideQuotePageEditorAssignedSecurityDockable.setTitleText("Securities");
		sellSideQuotePageEditorAssignedSecurityDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideQuotePageEditorAssignedSecurityDockable);

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
	 * Gets the sell side quote page.
	 *
	 * @return the sell side quote page
	 */
	public SellSideQuotePage getSellSideQuotePage() {

		return sellSideQuotePage;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		try {
			sellSideQuotePageEditorMasterData.save();
			sellSideQuotePageEditorAssignedSecurity.save();
			rolePermissionEditor.save();

			super.save();

			
			basisClientConnector.sendRequest(new OpenQuoteMonitorRequest(sellSideQuotePage.getId(), RequestIDManager.getInstance().getID()));
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

		if (sellSideQuotePageEditorMasterData != null && sellSideQuotePageEditorMasterData.isDirty())
			dirty = true;

		if (sellSideQuotePageEditorAssignedSecurity != null && sellSideQuotePageEditorAssignedSecurity.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (sellSideQuotePageEditorMasterData != null && !sellSideQuotePageEditorMasterData.isConsistent())
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

		this.sellSideQuotePage = (SellSideQuotePage) abstractBusinessObject;

		sellSideQuotePageEditorMasterData.updateContent();

		sellSideQuotePageEditorAssignedSecurity.updateContent();

		sellSideQuotePageEditorMonitor.updateContent();
		
		sellSideQuotePageEditorChart.updateContent();

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

		return sellSideQuotePage;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		sellSideQuotePageEditorMonitor.closeAbstractBusinessObjectEditor();
		if (sellSideQuotePage.getId() != 0)
			basisClientConnector.sendRequest(new CloseQuoteMonitorRequest(sellSideQuotePage.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();
		
		for (Dockable dockable : getMainPanel().getNamedDockables())
			if (dockable instanceof SellSideBookEditor) {
				SellSideBookEditor sellSideBookEditor = (SellSideBookEditor)dockable;
				if(sellSideBookEditor.getSellSideBook().getId()==sellSideQuotePage.getParent().getId())
				{
					sellSideBookEditor.clearCrawler(sellSideQuotePage);
				}
			}


	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (sellSideQuotePage.getId() != 0)
			basisClientConnector.sendRequest(new OpenQuoteMonitorRequest(sellSideQuotePage.getId(), RequestIDManager.getInstance().getID()));
	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		sellSideQuotePageEditorMonitor.onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);
		sellSideQuotePageEditorChart.onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);
		
		try {
			for (Dockable dockable : getMainPanel().getNamedDockables())
				if (dockable instanceof SellSideBookEditor) {
					SellSideBookEditor sellSideBookEditor = (SellSideBookEditor)dockable;
					if(sellSideBookEditor.getSellSideBook().getId()==sellSideQuotePage.getParent().getId())
					{
						sellSideBookEditor.onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);
					}
				}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}
	
	/**
	 * Gets the update sell side md input entry response.
	 *
	 * @return the update sell side md input entry response
	 */
	public UpdateSellSideMDInputEntryResponse getUpdateSellSideMDInputEntryResponse() {

		return sellSideQuotePageEditorMonitor.getUpdateSellSideMDInputEntryResponse();
	}

	/**
	 * On historical sell side data response.
	 *
	 * @param historicalSellSideDataResponse the historical sell side data response
	 */
	public void onHistoricalSellSideDataResponse(HistoricalSellSideDataResponse historicalSellSideDataResponse) {

		if(sellSideQuotePage.getId()==historicalSellSideDataResponse.getSellsideQuotePage())
			sellSideQuotePageEditorChart.onHistoricalDataResponse(historicalSellSideDataResponse);
		
	}
	
}
