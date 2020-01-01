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
package net.sourceforge.agora.simulator.control;

import net.sourceforge.agora.simulator.model.BlotterEntry;


/**
 * The listener interface for receiving blotter events.
 * The class that is interested in processing a blotter
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBlotterListener<code> method. When
 * the blotter event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BlotterEvent
 */
public interface BlotterListener {
	
	/**
	 * Write blotter entry.
	 *
	 * @param blotterEntry the blotter entry
	 */
	public void writeBlotterEntry(BlotterEntry blotterEntry);

	/**
	 * On logon.
	 *
	 * @param fromServer the from server
	 */
	public void onLogon(boolean fromServer);


}
