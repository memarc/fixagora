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
package net.sourceforge.fixagora.basis.client.control;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;


/**
 * The listener interface for receiving basisClientConnector events.
 * The class that is interested in processing a basisClientConnector
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBasisClientConnectorListener<code> method. When
 * the basisClientConnector event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BasisClientConnectorEvent
 */
public interface BasisClientConnectorListener {

	/**
	 * On connected.
	 */
	public void onConnected();
	
	/**
	 * On disconnected.
	 */
	public void onDisconnected();
	
	/**
	 * On abstract response.
	 *
	 * @param abstractResponse the abstract response
	 */
	public void onAbstractResponse(AbstractResponse abstractResponse);

	/**
	 * Sets the highlight key.
	 *
	 * @param key the new highlight key
	 */
	public void setHighlightKey(String key);
	
}
