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
package net.sourceforge.fixagora.basis.client.view;

import javax.swing.ImageIcon;

/**
 * The listener interface for receiving mainPanel events.
 * The class that is interested in processing a mainPanel
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMainPanelListener<code> method. When
 * the mainPanel event occurs, that object's appropriate
 * method is invoked.
 *
 * @see MainPanelEvent
 */
public interface MainPanelListener {

	/**
	 * On main panel changed.
	 */
	public void onMainPanelChanged();

	/**
	 * Sets the status info.
	 *
	 * @param imageIcon the image icon
	 * @param text the text
	 */
	public void setStatusInfo(ImageIcon imageIcon, String text);

}
