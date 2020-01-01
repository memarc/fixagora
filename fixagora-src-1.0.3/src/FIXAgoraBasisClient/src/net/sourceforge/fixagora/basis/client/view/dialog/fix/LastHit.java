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
package net.sourceforge.fixagora.basis.client.view.dialog.fix;

/**
 * The Class LastHit.
 */
public class LastHit {

	private AbstractFIXPanel abstractFIXPanel = null;

	/**
	 * Instantiates a new last hit.
	 *
	 * @param abstractFIXPanel the abstract fix panel
	 */
	public LastHit(final AbstractFIXPanel abstractFIXPanel) {

		super();

		this.abstractFIXPanel = abstractFIXPanel;
	}

	/**
	 * Gets the abstract fix panel.
	 *
	 * @return the abstract fix panel
	 */
	public AbstractFIXPanel getAbstractFIXPanel() {

		return abstractFIXPanel;
	}

	/**
	 * Sets the abstract fix panel.
	 *
	 * @param abstractFIXPanel the new abstract fix panel
	 */
	public void setAbstractFIXPanel(final AbstractFIXPanel abstractFIXPanel) {

		this.abstractFIXPanel = abstractFIXPanel;
	}

}
