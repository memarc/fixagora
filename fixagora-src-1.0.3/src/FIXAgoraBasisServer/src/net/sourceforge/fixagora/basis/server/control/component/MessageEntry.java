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
package net.sourceforge.fixagora.basis.server.control.component;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import quickfix.Message;


/**
 * The Class MessageEntry.
 */
public class MessageEntry {

	private AbstractBusinessComponent inputComponent = null;
	
	private AbstractBusinessComponent initialComponent = null;
	
	private Message message = null;

	
	
	/**
	 * Instantiates a new message entry.
	 *
	 * @param inputComponent the input component
	 * @param initialComponent the initial component
	 * @param message the message
	 */
	public MessageEntry(AbstractBusinessComponent inputComponent, AbstractBusinessComponent initialComponent, Message message) {

		super();
		this.inputComponent = inputComponent;
		this.initialComponent = initialComponent;
		this.message = message;
	}




	
	
	/**
	 * Gets the input component.
	 *
	 * @return the input component
	 */
	public AbstractBusinessComponent getInputComponent() {
	
		return inputComponent;
	}





	
	/**
	 * Gets the initial component.
	 *
	 * @return the initial component
	 */
	public AbstractBusinessComponent getInitialComponent() {
	
		return initialComponent;
	}





	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public Message getMessage() {
	
		return message;
	}
	
}
