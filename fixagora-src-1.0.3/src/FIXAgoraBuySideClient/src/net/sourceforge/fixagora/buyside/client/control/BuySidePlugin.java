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
package net.sourceforge.fixagora.buyside.client.control;

import javax.swing.JMenuItem;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideQuotePageEditor;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideResponse;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses;
import net.sourceforge.fixagora.buyside.shared.communication.HistoricalBuySideDataResponse;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;

import org.apache.log4j.Logger;

import bibliothek.gui.Dockable;

/**
 * The Class BuySidePlugin.
 */
public class BuySidePlugin implements FIXAgoraPlugin, BuySideResponses {

	private BasisClientConnector basisClientConnector = null;

	private MainPanel mainPanel = null;
	
	private static Logger log = Logger.getLogger(BuySidePlugin.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getExtraJMenuItem(net.sourceforge.fixagora.basis.client.view.MainPanel)
	 */
	@Override
	public JMenuItem getExtraJMenuItem(MainPanel mainPanel) {

		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getOrderNumber()
	 */
	@Override
	public double getOrderNumber() {

		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#setBasisClientConnector(net.sourceforge.fixagora.basis.client.control.BasisClientConnector)
	 */
	@Override
	public void setBasisClientConnector(BasisClientConnector basisClientConnector) {

		this.basisClientConnector = basisClientConnector;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getAbstractBusinessEditor(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject, net.sourceforge.fixagora.basis.client.view.MainPanel)
	 */
	@Override
	public AbstractBusinessObjectEditor getAbstractBusinessEditor(AbstractBusinessObject abstractBusinessObject, MainPanel mainPanel) {

		if (this.mainPanel == null)
			this.mainPanel = mainPanel;

		try {
			if (abstractBusinessObject instanceof BuySideBook)
				return new BuySideBookEditor((BuySideBook) abstractBusinessObject, mainPanel, basisClientConnector);
			if (abstractBusinessObject instanceof BuySideQuotePage)
				return new BuySideQuotePageEditor((BuySideQuotePage) abstractBusinessObject, mainPanel, basisClientConnector);
		}
		catch (Exception e) {
			
			log.error("Bug", e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

		if (abstractResponse instanceof AbstractBuySideResponse) {
			((AbstractBuySideResponse) abstractResponse).handleBuySideResponse(this);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses#onUpdateSellSideMDInputEntryResponse(net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse)
	 */
	@Override
	public void onUpdateSellSideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof BuySideQuotePageEditor)
					((BuySideQuotePageEditor) dockable).onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses#onNewOrderSingleResponse(net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse)
	 */
	@Override
	public void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof BuySideBookEditor)
					((BuySideBookEditor) dockable).onNewOrderSingleResponse(newOrderSingleResponse);
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses#onBuySideQuoteRequestResponse(net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse)
	 */
	@Override
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse) {
		
		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof BuySideBookEditor)
					((BuySideBookEditor) dockable).onBuySideQuoteRequestResponse(quoteRequestResponse);
		}		
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.buyside.shared.communication.BuySideResponses#onHistoricalBuySideDataResponse(net.sourceforge.fixagora.buyside.shared.communication.HistoricalBuySideDataResponse)
	 */
	@Override
	public void onHistoricalBuySideDataResponse(HistoricalBuySideDataResponse historicalDataResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof BuySideQuotePageEditor)
					((BuySideQuotePageEditor) dockable).onHistoricalDataResponse(historicalDataResponse);
		}		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getPluginInfo()
	 */
	@Override
	public PluginInfo getPluginInfo() {

		return new PluginInfo("FIX Agora Buy Side", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com");
	}

}
