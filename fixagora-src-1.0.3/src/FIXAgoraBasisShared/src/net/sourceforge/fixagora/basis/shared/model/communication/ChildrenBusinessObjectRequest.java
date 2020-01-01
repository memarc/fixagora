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
 * The Class ChildrenBusinessObjectRequest.
 */
public class ChildrenBusinessObjectRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private long objectId = 0;
	
	private String className = null;
	
	/**
	 * Instantiates a new children business object request.
	 *
	 * @param className the class name
	 * @param objectId the object id
	 * @param requestID the request id
	 */
	public ChildrenBusinessObjectRequest(String className, long objectId , long requestID) {

		super(requestID);
		this.className = className;
		this.objectId = objectId;
	}
	

	
	/**
	 * Gets the object id.
	 *
	 * @return the object id
	 */
	public long getObjectId() {
	
		return objectId;
	}




	
	/**
	 * Sets the object id.
	 *
	 * @param objectId the new object id
	 */
	public void setObjectId(long objectId) {
	
		this.objectId = objectId;
	}






	
	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	public String getClassName() {
	
		return className;
	}






	
	/**
	 * Sets the class name.
	 *
	 * @param className the new class name
	 */
	public void setClassName(String className) {
	
		this.className = className;
	}






	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onChildrenBusinessObjectRequest(this, channel);
	}
	
	

}
