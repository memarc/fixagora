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
package net.sourceforge.fixagora.basis.shared.model.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Interface AbstractRequests.
 */
public abstract interface AbstractRequests {

	/**
	 * Handle session closed.
	 *
	 * @param channel the channel
	 */
	public void handleSessionClosed(Channel channel);
	
	/**
	 * Inits the.
	 *
	 * @param object the object
	 * @param object2 the object2
	 * @throws Exception the exception
	 */
	public void init(Object object, Object object2) throws Exception;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the license.
	 *
	 * @return the license
	 */
	public String getLicense();
	
}
