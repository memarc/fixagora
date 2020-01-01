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
package net.sourceforge.fixagora.sellside.client.control;

import javax.swing.JMenuItem;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditor;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideQuotePageEditor;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideResponse;
import net.sourceforge.fixagora.sellside.shared.communication.HistoricalSellSideDataResponse;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;
import bibliothek.gui.Dockable;

/**
 * The Class SellSidePlugin.
 */
public class SellSidePlugin implements FIXAgoraPlugin, SellSideResponses {

	private BasisClientConnector basisClientConnector;
	
	private MainPanel mainPanel = null;

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

		if(this.mainPanel==null)
			this.mainPanel = mainPanel;
		
		if(abstractBusinessObject instanceof SellSideBook)
			return new SellSideBookEditor((SellSideBook)abstractBusinessObject, mainPanel, basisClientConnector);
		if(abstractBusinessObject instanceof SellSideQuotePage)
			return new SellSideQuotePageEditor((SellSideQuotePage)abstractBusinessObject, mainPanel, basisClientConnector);
		
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

		if(abstractResponse instanceof AbstractSellSideResponse)
		{
			((AbstractSellSideResponse)abstractResponse).handleSellSideResponse(this);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses#onUpdateSellSideMDInputEntryResponse(net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse)
	 */
	@Override
	public void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		if(mainPanel!=null)
		{
			for(Dockable dockable:  mainPanel.getNamedDockables())
				if(dockable instanceof SellSideQuotePageEditor)
					((SellSideQuotePageEditor)dockable).onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses#onNewOrderSingleResponse(net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse)
	 */
	@Override
	public void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof SellSideBookEditor)
					((SellSideBookEditor) dockable).onNewOrderSingleResponse(newOrderSingleResponse);
		}				
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses#onHistoricalSellSideDataResponse(net.sourceforge.fixagora.sellside.shared.communication.HistoricalSellSideDataResponse)
	 */
	@Override
	public void onHistoricalSellSideDataResponse(HistoricalSellSideDataResponse historicalSellSideDataResponse) {

		if(mainPanel!=null)
		{
			for(Dockable dockable:  mainPanel.getNamedDockables())
				if(dockable instanceof SellSideQuotePageEditor)
					((SellSideQuotePageEditor)dockable).onHistoricalSellSideDataResponse(historicalSellSideDataResponse);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getPluginInfo()
	 */
	@Override
	public PluginInfo getPluginInfo() {

		return new PluginInfo("FIX Agora Sell Side", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com");
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses#onSellSideQuoteRequestResponse(net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse)
	 */
	@Override
	public void onSellSideQuoteRequestResponse(SellSideQuoteRequestResponse sellSideQuoteRequestResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof SellSideBookEditor)
					((SellSideBookEditor) dockable).onSellSideQuoteRequestResponse(sellSideQuoteRequestResponse);
		}				
	}
	
}
