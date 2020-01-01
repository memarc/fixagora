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
package net.sourceforge.fixagora.sap.client.control;

import javax.swing.JMenuItem;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.sap.client.view.editor.SAPTradeCaptureEditor;
import net.sourceforge.fixagora.sap.shared.communication.AbstractSAPResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPResponses;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCapture;
import bibliothek.gui.Dockable;

/**
 * The Class SAPPlugin.
 */
public class SAPPlugin implements FIXAgoraPlugin, SAPResponses {

	private BasisClientConnector basisClientConnector = null;
	
	private MainPanel mainPanel = null;



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
		
			if(abstractBusinessObject instanceof SAPTradeCapture)
				return new SAPTradeCaptureEditor((SAPTradeCapture)abstractBusinessObject, mainPanel, basisClientConnector);
		
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

		if(abstractResponse instanceof AbstractSAPResponse)
		{
			((AbstractSAPResponse)abstractResponse).handleAbstractSAPResponse(this);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPResponses#onSAPTradeCaptureEntryResponse(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse)
	 */
	@Override
	public void onSAPTradeCaptureEntryResponse(SAPTradeCaptureEntryResponse sapTradeCaptureEntryResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof SAPTradeCaptureEditor)
					((SAPTradeCaptureEditor) dockable).onSAPTradeCaptureEntryResponse(sapTradeCaptureEntryResponse);
		}				
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getExtraJMenuItem(net.sourceforge.fixagora.basis.client.view.MainPanel)
	 */
	@Override
	public JMenuItem getExtraJMenuItem(MainPanel mainPanel) {

		return null;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sap.shared.communication.SAPResponses#onSAPTradeCaptureActionResponse(net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse)
	 */
	@Override
	public void onSAPTradeCaptureActionResponse(SAPTradeCaptureActionResponse sapTradeCaptureActionResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof SAPTradeCaptureEditor)
					((SAPTradeCaptureEditor) dockable).onSAPTradeCaptureActionResponse(sapTradeCaptureActionResponse);
		}			
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getPluginInfo()
	 */
	@Override
	public PluginInfo getPluginInfo() {

		return new PluginInfo("FIX Agora SAP Integration", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com");
	}
}
