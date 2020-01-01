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

import java.util.concurrent.Callable;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;

import org.jboss.netty.channel.Channel;


/**
 * The Class RequestCallable.
 */
public class RequestCallable implements Callable<AbstractResponse>{

	private Channel channel = null;
	
	private AbstractRequest abstractRequest = null;
	
	private AbstractResponse abstractResponse = null;
	

	/**
	 * Instantiates a new request callable.
	 *
	 * @param channel the channel
	 * @param abstractRequest the abstract request
	 */
	public RequestCallable(Channel channel, AbstractRequest abstractRequest) {

		super();
		
		this.channel = channel;
		this.abstractRequest = abstractRequest;
	}



	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public AbstractResponse call() throws Exception {
		
		synchronized (this) {

			channel.write(abstractRequest);
			this.wait();
		}
		
		return abstractResponse;
	}



	
	/**
	 * Sets the abstract response.
	 *
	 * @param abstractResponse the new abstract response
	 */
	public void setAbstractResponse(AbstractResponse abstractResponse) {
			
		this.abstractResponse = abstractResponse;
		
		synchronized (this) {
			
			this.notify();
		}
	}



	/**
	 * Cancel.
	 */
	public void cancel() {

		synchronized (this) {
			
			this.notify();
		}		
	}
	

	


}
