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
package net.sourceforge.fixagora.basis.server.control;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;

import org.jboss.netty.channel.Channel;


/**
 * The Class ResponseSet.
 */
public class ResponseSet {
	
	/** The abstract response. */
	AbstractResponse abstractResponse= null;
	
	/** The channel. */
	Channel channel = null;
	
	/**
	 * Instantiates a new response set.
	 *
	 * @param abstractResponse the abstract response
	 * @param channel the channel
	 */
	public ResponseSet(AbstractResponse abstractResponse, Channel channel) {

		super();
		this.abstractResponse = abstractResponse;
		this.channel = channel;
	}

	
	/**
	 * Gets the abstract response.
	 *
	 * @return the abstract response
	 */
	public AbstractResponse getAbstractResponse() {
	
		return abstractResponse;
	}

	
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public Channel getChannel() {
	
		return channel;
	}
	
	

}
