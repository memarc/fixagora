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
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.buyside.client.view.dialog.NewOrderSingleDialog;
import net.sourceforge.fixagora.buyside.client.view.dialog.QuoteResponseDialog;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.communication.CloseBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookRequest;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookResponse;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class BuySideBookEditor.
 */
public class BuySideBookEditor extends AbstractBusinessObjectEditor {

	private BuySideBook buySideBook = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private BuySideBookEditorMasterData buySideBookEditorMasterData = null;

	private BuySideBookEditorAssignedSecurity buySideBookEditorAssignedSecurity = null;

	private BuySideBookEditorAssignedFIXInitiator buySideBookEditorAssignedFIXAcceptor = null;

	private BuySideBookEditorMonitor buySideBookEditorMonitor = null;

	private RolePermissionEditor rolePermissionEditor = null;

	private DefaultDockable buySideBookEditorMonitorDockable = null;

	private Icon originalIcon;

	private boolean warning = false;

	private Set<NewOrderSingleDialog> newOrderSingleDialogs = new HashSet<NewOrderSingleDialog>();
	
	private Set<QuoteResponseDialog> quoteResponseDialogs = new HashSet<QuoteResponseDialog>();

	/**
	 * Instantiates a new buy side book editor.
	 *
	 * @param buySideBook the buy side book
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public BuySideBookEditor(BuySideBook buySideBook, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(buySideBook, mainPanel, basisClientConnector);
		this.buySideBook = buySideBook;
		if (buySideBook.getId() == 0)
			this.buySideBook.setBankCalendar(getMainPanel().getBankCalendars().get(getMainPanel().getBankCalendars().size() - 1));
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

		buySideBookEditorMonitor = new BuySideBookEditorMonitor(this);

		buySideBookEditorMonitorDockable = new DefaultDockable(buySideBookEditorMonitor);
		buySideBookEditorMonitorDockable.setFactoryID("Book");
		buySideBookEditorMonitorDockable.setTitleText("Book");
		buySideBookEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideBookEditorMonitorDockable);

		buySideBookEditorMasterData = new BuySideBookEditorMasterData(this);

		DefaultDockable buySideBookEditorMasterDataDockable = new DefaultDockable(buySideBookEditorMasterData);
		buySideBookEditorMasterDataDockable.setFactoryID("Master data");
		buySideBookEditorMasterDataDockable.setTitleText("Master data");
		buySideBookEditorMasterDataDockable.setTitleIcon(folderBlue);

		if (frontDockable == null)
			if (buySideBook.getId() == 0)
				frontDockable = buySideBookEditorMasterDataDockable;
			else
				frontDockable = buySideBookEditorMonitorDockable;

		stackDockStation.drop(buySideBookEditorMasterDataDockable);

		buySideBookEditorAssignedSecurity = new BuySideBookEditorAssignedSecurity(this);

		DefaultDockable buySideBookEditorAssignedSecurityDockable = new DefaultDockable(buySideBookEditorAssignedSecurity);
		buySideBookEditorAssignedSecurityDockable.setFactoryID("Subscribed Securities");
		buySideBookEditorAssignedSecurityDockable.setTitleText("Subscribed Securities");
		buySideBookEditorAssignedSecurityDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideBookEditorAssignedSecurityDockable);

		buySideBookEditorAssignedFIXAcceptor = new BuySideBookEditorAssignedFIXInitiator(this);

		DefaultDockable buySideBookEditorAssignedFIXAcceptorDockable = new DefaultDockable(buySideBookEditorAssignedFIXAcceptor);
		buySideBookEditorAssignedFIXAcceptorDockable.setFactoryID("Initiators");
		buySideBookEditorAssignedFIXAcceptorDockable.setTitleText("Initiators");
		buySideBookEditorAssignedFIXAcceptorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(buySideBookEditorAssignedFIXAcceptorDockable);

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
	 * Gets the buy side book.
	 *
	 * @return the buy side book
	 */
	public BuySideBook getBuySideBook() {

		return buySideBook;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		buySideBookEditorMasterData.save();
		buySideBookEditorAssignedSecurity.save();
		buySideBookEditorAssignedFIXAcceptor.save();
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
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;

		if (buySideBookEditorMasterData != null && buySideBookEditorMasterData.isDirty())
			dirty = true;

		if (buySideBookEditorAssignedSecurity != null && buySideBookEditorAssignedSecurity.isDirty())
			dirty = true;

		if (buySideBookEditorAssignedFIXAcceptor != null && buySideBookEditorAssignedFIXAcceptor.isDirty())
			dirty = true;

		if (rolePermissionEditor != null && rolePermissionEditor.isDirty())
			dirty = true;

		boolean consistent = true;

		if (buySideBookEditorMasterData != null && !buySideBookEditorMasterData.isConsistent())
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

		this.buySideBook = (BuySideBook) abstractBusinessObject;

		buySideBookEditorMonitor.updateContent();

		if (buySideBook.getId() != 0) {
			OpenBuySideBookResponse openBuyBookResponse = (OpenBuySideBookResponse) basisClientConnector.sendRequest(new OpenBuySideBookRequest(buySideBook
					.getId(), RequestIDManager.getInstance().getID()));
			buySideBookEditorMonitor.onOpenBuySideBookResponse(openBuyBookResponse);
		}

		buySideBookEditorMasterData.updateContent();

		buySideBookEditorAssignedSecurity.updateContent();

		buySideBookEditorAssignedFIXAcceptor.updateContent();

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

		return buySideBook;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		buySideBookEditorMonitor.closeAbstractBusinessObjectEditor();
		if (buySideBook.getId() != 0)
			basisClientConnector.sendRequest(new CloseBuySideBookRequest(buySideBook.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (buySideBook.getId() != 0) {
			OpenBuySideBookResponse openBuyBookResponse = (OpenBuySideBookResponse) basisClientConnector.sendRequest(new OpenBuySideBookRequest(buySideBook
					.getId(), RequestIDManager.getInstance().getID()));
			buySideBookEditorMonitor.onOpenBuySideBookResponse(openBuyBookResponse);
		}

		for (Dockable dockable : getMainPanel().getNamedDockables())
			if (dockable instanceof BuySideQuotePageEditor) {
				BuySideQuotePageEditor buySideQuotePageEditor = (BuySideQuotePageEditor) dockable;
				if (getBuySideBook().getId() == buySideQuotePageEditor.getBuySideQuotePage().getParent().getId()) {
					onUpdateBuySideMDInputEntryResponse(buySideQuotePageEditor.getUpdateBuySideMDInputEntryResponse());
				}
			}
	}

	/**
	 * Adds the new order single.
	 *
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 */
	public void addNewOrderSingle(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		stackDockStation.setFrontDockable(buySideBookEditorMonitorDockable);
		buySideBookEditorMonitor.addNewOrderSingle(buySideNewOrderSingleEntry);
	}

	/**
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public synchronized void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		if (newOrderSingleResponse.getBuySideNewOrderSingleEntry().getBuySideBook() == buySideBook.getId()) {
			buySideBookEditorMonitor.onNewOrderSingleResponse(newOrderSingleResponse);
			for (NewOrderSingleDialog newOrderSingleDialog : newOrderSingleDialogs)
				newOrderSingleDialog.onNewOrderSingleResponse(newOrderSingleResponse);
		}

	}
	
	/**
	 * On buy side quote request response.
	 *
	 * @param quoteRequestResponse the quote request response
	 */
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse) {

		if (quoteRequestResponse.getBuySideQuoteRequestEntries().get(0).getBuySideBook() == buySideBook.getId()) {
			buySideBookEditorMonitor.onBuySideQuoteRequestResponse(quoteRequestResponse);
			for (QuoteResponseDialog quoteResponseDialog : quoteResponseDialogs)
				quoteResponseDialog.onBuySideQuoteRequestResponse(quoteRequestResponse);
		}

		
	}

	/**
	 * Stop warning.
	 */
	public synchronized void stopWarning() {

		if (originalIcon != null)
			setTitleIcon(originalIcon);

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
	 * Removes the quote response dialog.
	 *
	 * @param quoteResponseDialog the quote response dialog
	 */
	public void removeQuoteResponseDialog(QuoteResponseDialog quoteResponseDialog) {

		quoteResponseDialogs.remove(quoteResponseDialog);
		
	}

	/**
	 * Adds the quote response dialog.
	 *
	 * @param quoteResponseDialog the quote response dialog
	 */
	public void addQuoteResponseDialog(QuoteResponseDialog quoteResponseDialog) {

		quoteResponseDialogs.add(quoteResponseDialog);
		
	}

	/**
	 * Gets the new order single dialog.
	 *
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 * @return the new order single dialog
	 */
	public synchronized NewOrderSingleDialog getNewOrderSingleDialog(BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		for (NewOrderSingleDialog newOrderSingleDialog : newOrderSingleDialogs)
			if (newOrderSingleDialog.getBuySideNewOrderSingleEntry() == buySideNewOrderSingleEntry)
				return newOrderSingleDialog;
		return null;
	}
	
	
	/**
	 * Gets the quote response dialog.
	 *
	 * @param buySideQuoteRequestEntry the buy side quote request entry
	 * @return the quote response dialog
	 */
	public QuoteResponseDialog getQuoteResponseDialog(BuySideQuoteRequestEntry buySideQuoteRequestEntry) {

		for (QuoteResponseDialog quoteResponseDialog : quoteResponseDialogs)
			if (quoteResponseDialog.getBuySideQuoteRequestEntry() == buySideQuoteRequestEntry)
				return quoteResponseDialog;
		return null;
	}
	
	

	/**
	 * Clear crawler.
	 *
	 * @param buySideQuotePage the buy side quote page
	 */
	public void clearCrawler(BuySideQuotePage buySideQuotePage) {

		Set<Long> securities = new HashSet<Long>();

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideQuotePage.getAssignedBuySideBookSecurities())
			securities.add(assignedBuySideBookSecurity.getSecurity().getId());

		buySideBookEditorMonitor.clearCrawler(securities);

	}

	/**
	 * On update buy side md input entry response.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public void onUpdateBuySideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		buySideBookEditorMonitor.onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);

	}

	/**
	 * Adds the quote request.
	 *
	 * @param side the side
	 * @param counterparties the counterparties
	 * @param security the security
	 */
	public void addQuoteRequest(Side side, List<Counterparty> counterparties,
			long security) {

		stackDockStation.setFrontDockable(buySideBookEditorMonitorDockable);
		buySideBookEditorMonitor.addNewOrderSingle(side,counterparties,security);
		
	}



}
