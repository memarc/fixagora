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

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * The listener interface for receiving transferAction events.
 * The class that is interested in processing a transferAction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addTransferActionListener<code> method. When
 * the transferAction event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TransferActionEvent
 */
public class TransferActionListener implements ActionListener, PropertyChangeListener {

	private JComponent focusOwner = null;

	/**
	 * Instantiates a new transfer action listener.
	 */
	public TransferActionListener() {

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addPropertyChangeListener("permanentFocusOwner", this);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {

		Object o = e.getNewValue();
		if (o instanceof JComponent) {
			focusOwner = (JComponent) o;
		}
		else {
			focusOwner = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (focusOwner == null)
			return;
		String action = (String) e.getActionCommand();
		Action a = focusOwner.getActionMap().get(action);
		if (a != null) {
			a.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
		}
	}

}
