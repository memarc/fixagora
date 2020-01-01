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
package net.sourceforge.fixagora.tradecapture.client.control;

import javax.swing.JMenuItem;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.tradecapture.client.view.editor.TradeCaptureEditor;
import net.sourceforge.fixagora.tradecapture.shared.communication.AbstractTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureResponses;
import net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCapture;
import bibliothek.gui.Dockable;

/**
 * The Class TradeCapturePlugin.
 */
public class TradeCapturePlugin implements FIXAgoraPlugin, TradeCaptureResponses {

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
		
		if(abstractBusinessObject instanceof TradeCapture)
			return new TradeCaptureEditor((TradeCapture)abstractBusinessObject, mainPanel, basisClientConnector);
		
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

		if(abstractResponse instanceof AbstractTradeCaptureResponse)
		{
			((AbstractTradeCaptureResponse)abstractResponse).handleTradeCaptureResponse(this);
		}
		
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureResponses#onTradeCaptureEntryResponse(net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse)
	 */
	@Override
	public void onTradeCaptureEntryResponse(TradeCaptureEntryResponse tradeCaptureEntryResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof TradeCaptureEditor)
					((TradeCaptureEditor) dockable).onTradeCaptureEntryResponse(tradeCaptureEntryResponse);
		}		
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {
		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getPluginInfo()
	 */
	@Override
	public PluginInfo getPluginInfo() {

		return new PluginInfo("FIX Agora Trade Capture", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com");
	}

}
