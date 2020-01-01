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
package net.sourceforge.fixagora.basis.client.model;

import javax.swing.JMenuItem;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;


/**
 * The Interface FIXAgoraPlugin.
 */
public interface FIXAgoraPlugin extends BasisClientConnectorListener {
	
	/**
	 * Gets the extra j menu item.
	 *
	 * @param mainPanel the main panel
	 * @return the extra j menu item
	 */
	public JMenuItem getExtraJMenuItem(MainPanel mainPanel);
	
	/**
	 * Gets the order number.
	 *
	 * @return the order number
	 */
	public double getOrderNumber();
	
	/**
	 * Sets the basis client connector.
	 *
	 * @param basisClientConnector the new basis client connector
	 */
	public void setBasisClientConnector(BasisClientConnector basisClientConnector);

	/**
	 * Gets the abstract business editor.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param mainPanel the main panel
	 * @return the abstract business editor
	 */
	public AbstractBusinessObjectEditor getAbstractBusinessEditor(AbstractBusinessObject abstractBusinessObject, MainPanel mainPanel);

	/**
	 * Gets the plugin info.
	 *
	 * @return the plugin info
	 */
	public PluginInfo getPluginInfo();
	
}
