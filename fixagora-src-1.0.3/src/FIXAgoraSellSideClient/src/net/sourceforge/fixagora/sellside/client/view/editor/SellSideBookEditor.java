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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.sellside.client.view.dialog.NewOrderSingleDialog;
import net.sourceforge.fixagora.sellside.client.view.dialog.SellSideQuoteRequestDialog;
import net.sourceforge.fixagora.sellside.shared.communication.CloseSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookRequest;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class SellSideBookEditor.
 */
public class SellSideBookEditor extends AbstractBusinessObjectEditor {

	private SellSideBook sellSideBook = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private SellSideBookEditorMasterData sellSideBookEditorMasterData = null;

	private SellSideBookEditorAssignedSecurity sellSideBookEditorAssignedSecurity = null;

	private SellSideBookEditorAssignedFIXAcceptor sellSideBookEditorAssignedFIXAcceptor = null;

	private SellSideBookEditorMonitor sellSideBookEditorMonitor = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private Icon originalIcon;

	private Set<NewOrderSingleDialog> newOrderSingleDialogs = new HashSet<NewOrderSingleDialog>();
	
	private Set<SellSideQuoteRequestDialog> sellSideQuoteRequestDialogs = new HashSet<SellSideQuoteRequestDialog>();

	private boolean warning = false;

	/**
	 * Instantiates a new sell side book editor.
	 *
	 * @param sellSideBook the sell side book
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public SellSideBookEditor(SellSideBook sellSideBook, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(sellSideBook, mainPanel, basisClientConnector);
		this.sellSideBook = sellSideBook;
		if (sellSideBook.getId() == 0)
			this.sellSideBook.setBankCalendar(getMainPanel().getBankCalendars().get(getMainPanel().getBankCalendars().size() - 1));
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

		sellSideBookEditorMonitor = new SellSideBookEditorMonitor(this);

		DefaultDockable sellSideBookEditorMonitorDockable = new DefaultDockable(sellSideBookEditorMonitor);
		sellSideBookEditorMonitorDockable.setFactoryID("Book");
		sellSideBookEditorMonitorDockable.setTitleText("Book");
		sellSideBookEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideBookEditorMonitorDockable);

		sellSideBookEditorMasterData = new SellSideBookEditorMasterData(this);

		DefaultDockable sellSideBookEditorMasterDataDockable = new DefaultDockable(sellSideBookEditorMasterData);
		sellSideBookEditorMasterDataDockable.setFactoryID("Master data");
		sellSideBookEditorMasterDataDockable.setTitleText("Master data");
		sellSideBookEditorMasterDataDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideBookEditorMasterDataDockable);

		if (frontDockable == null)
			if (sellSideBook.getId() == 0)
				frontDockable = sellSideBookEditorMasterDataDockable;
			else
				frontDockable = sellSideBookEditorMonitorDockable;

		sellSideBookEditorAssignedSecurity = new SellSideBookEditorAssignedSecurity(this);

		DefaultDockable sellSideBookEditorAssignedSecurityDockable = new DefaultDockable(sellSideBookEditorAssignedSecurity);
		sellSideBookEditorAssignedSecurityDockable.setFactoryID("Subscribed Securities");
		sellSideBookEditorAssignedSecurityDockable.setTitleText("Subscribed Securities");
		sellSideBookEditorAssignedSecurityDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideBookEditorAssignedSecurityDockable);

		sellSideBookEditorAssignedFIXAcceptor = new SellSideBookEditorAssignedFIXAcceptor(this);

		DefaultDockable sellSideBookEditorAssignedFIXAcceptorDockable = new DefaultDockable(sellSideBookEditorAssignedFIXAcceptor);
		sellSideBookEditorAssignedFIXAcceptorDockable.setFactoryID("Acceptors");
		sellSideBookEditorAssignedFIXAcceptorDockable.setTitleText("Acceptors");
		sellSideBookEditorAssignedFIXAcceptorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(sellSideBookEditorAssignedFIXAcceptorDockable);

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
	 * Gets the sell side book.
	 *
	 * @return the sell side book
	 */
	public SellSideBook getSellSideBook() {

		return sellSideBook;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		sellSideBookEditorMasterData.save();
		sellSideBookEditorAssignedSecurity.save();
		sellSideBookEditorAssignedFIXAcceptor.save();
		rolePermissionEditor.save();

		super.save();
		
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(getAbstractBusinessObject().getClass().getName(),getAbstractBusinessObject().getId(), RequestIDManager
				.getInstance().getID()));
		if (abstractResponse instanceof RefreshResponse) {
			RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
			updateContent(refreshResponse.getAbstractBusinessObject());
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		sellSideBookEditorMonitor.closeAbstractBusinessObjectEditor();
		if (sellSideBook.getId() != 0)
			basisClientConnector.sendRequest(new CloseSellSideBookRequest(sellSideBook.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (sellSideBook.getId() != 0) {
			OpenSellSideBookResponse openSellSideBookResponse = (OpenSellSideBookResponse) basisClientConnector.sendRequest(new OpenSellSideBookRequest(
					sellSideBook.getId(), RequestIDManager.getInstance().getID()));
			sellSideBookEditorMonitor.onOpenSellSideBookResponse(openSellSideBookResponse);
		}

		for (Dockable dockable : getMainPanel().getNamedDockables())
			if (dockable instanceof SellSideQuotePageEditor) {
				SellSideQuotePageEditor sellSideQuotePageEditor = (SellSideQuotePageEditor) dockable;
				if (getSellSideBook().getId() == sellSideQuotePageEditor.getSellSideQuotePage().getParent().getId()) {
					onUpdateSellSideMDInputEntryResponse(sellSideQuotePageEditor.getUpdateSellSideMDInputEntryResponse());
				}
			}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (sellSideBookEditorMasterData != null && sellSideBookEditorMasterData.isDirty())
			dirty = true;

		if (sellSideBookEditorAssignedSecurity != null && sellSideBookEditorAssignedSecurity.isDirty())
			dirty = true;

		if (sellSideBookEditorAssignedFIXAcceptor != null && sellSideBookEditorAssignedFIXAcceptor.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (sellSideBookEditorMasterData != null && !sellSideBookEditorMasterData.isConsistent())
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

		this.sellSideBook = (SellSideBook) abstractBusinessObject;

		sellSideBookEditorMonitor.updateContent();

		if (sellSideBook.getId() != 0) {
			OpenSellSideBookResponse openSellSideBookResponse = (OpenSellSideBookResponse) basisClientConnector.sendRequest(new OpenSellSideBookRequest(
					sellSideBook.getId(), RequestIDManager.getInstance().getID()));
			sellSideBookEditorMonitor.onOpenSellSideBookResponse(openSellSideBookResponse);
		}

		sellSideBookEditorMasterData.updateContent();

		sellSideBookEditorAssignedSecurity.updateContent();

		sellSideBookEditorAssignedFIXAcceptor.updateContent();

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

		return sellSideBook;
	}

	/**
	 * Stop warning.
	 */
	public synchronized void stopWarning() {

		if (originalIcon != null)
			setTitleIcon(originalIcon);

	}

	/**
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public synchronized void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		if (newOrderSingleResponse.getSellSideNewOrderSingleEntry().getSellSideBook() == sellSideBook.getId()) {
			sellSideBookEditorMonitor.onNewOrderSingleResponse(newOrderSingleResponse);
			for (NewOrderSingleDialog newOrderSingleDialog : newOrderSingleDialogs)
				newOrderSingleDialog.onNewOrderSingleResponse(newOrderSingleResponse);
		}

	}

	/**
	 * Toggle warning.
	 */
	public synchronized void toggleWarning() {

		if (originalIcon == null)
			originalIcon = getTitleIcon();
		warning = !warning;
		if (warning)
			setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/info.png")));
		else if (originalIcon != null)
			setTitleIcon(originalIcon);
	}

	/**
	 * Adds the new order single dialog.
	 *
	 * @param newOrderSingleDialog the new order single dialog
	 */
	public synchronized void addNewOrderSingleDialog(NewOrderSingleDialog newOrderSingleDialog) {

		newOrderSingleDialogs.add(newOrderSingleDialog);
	}

	/**
	 * Removes the new order single dialog.
	 *
	 * @param newOrderSingleDialog the new order single dialog
	 */
	public synchronized void removeNewOrderSingleDialog(NewOrderSingleDialog newOrderSingleDialog) {

		newOrderSingleDialogs.remove(newOrderSingleDialog);
	}

	/**
	 * Gets the new order single dialog.
	 *
	 * @param sellSideNewOrderSingleEntry the sell side new order single entry
	 * @return the new order single dialog
	 */
	public synchronized NewOrderSingleDialog getNewOrderSingleDialog(SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry) {

		for (NewOrderSingleDialog newOrderSingleDialog : newOrderSingleDialogs)
			if (newOrderSingleDialog.getSellSideNewOrderSingleEntry() == sellSideNewOrderSingleEntry)
				return newOrderSingleDialog;
		return null;
	}
	
	/**
	 * Gets the sell side quote request dialog.
	 *
	 * @param sellSideQuoteRequestEntry the sell side quote request entry
	 * @return the sell side quote request dialog
	 */
	public synchronized SellSideQuoteRequestDialog getSellSideQuoteRequestDialog(SellSideQuoteRequestEntry sellSideQuoteRequestEntry) {

		for (SellSideQuoteRequestDialog sellSideQuoteRequestDialog : sellSideQuoteRequestDialogs)
			if (sellSideQuoteRequestDialog.getSellSideQuoteRequestEntry() == sellSideQuoteRequestEntry)
				return sellSideQuoteRequestDialog;
		return null;
	}

	/**
	 * Clear crawler.
	 *
	 * @param sellSideQuotePage the sell side quote page
	 */
	public void clearCrawler(SellSideQuotePage sellSideQuotePage) {

		Set<Long> securities = new HashSet<Long>();

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideQuotePage.getAssignedSellSideBookSecurities())
			securities.add(assignedSellSideBookSecurity.getSecurity().getId());

		sellSideBookEditorMonitor.clearCrawler(securities);

	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		sellSideBookEditorMonitor.onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);

	}

	/**
	 * On sell side quote request response.
	 *
	 * @param sellSideQuoteRequestResponse the sell side quote request response
	 */
	public void onSellSideQuoteRequestResponse(SellSideQuoteRequestResponse sellSideQuoteRequestResponse) {

		if (sellSideQuoteRequestResponse.getSellSideQuoteRequestEntry().getSellSideBook() == sellSideBook.getId()) {
			sellSideBookEditorMonitor.onSellSideQuoteRequestResponse(sellSideQuoteRequestResponse);
			for (SellSideQuoteRequestDialog sellSideQuoteRequestDialog : sellSideQuoteRequestDialogs)
				sellSideQuoteRequestDialog.onSellSideQuoteRequestResponse(sellSideQuoteRequestResponse);
		}
		
	}

	/**
	 * Removes the sell side quote request dialog.
	 *
	 * @param sellSideQuoteRequestDialog the sell side quote request dialog
	 */
	public void removeSellSideQuoteRequestDialog(SellSideQuoteRequestDialog sellSideQuoteRequestDialog) {

		sellSideQuoteRequestDialogs.remove(sellSideQuoteRequestDialog);
		
	}

	/**
	 * Adds the sell side quote request dialog.
	 *
	 * @param sellSideQuoteRequestDialog the sell side quote request dialog
	 */
	public void addSellSideQuoteRequestDialog(SellSideQuoteRequestDialog sellSideQuoteRequestDialog) {

		sellSideQuoteRequestDialogs.add(sellSideQuoteRequestDialog);
		
	}
}
