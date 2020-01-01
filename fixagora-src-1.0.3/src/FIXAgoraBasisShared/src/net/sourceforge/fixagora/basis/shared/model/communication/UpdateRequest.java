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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;

import org.jboss.netty.channel.Channel;


/**
 * The Class UpdateRequest.
 */
public class UpdateRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private List<AbstractBusinessObject> abstractBusinessObjects;
		
	/**
	 * Instantiates a new update request.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param requestID the request id
	 */
	public UpdateRequest(AbstractBusinessObject abstractBusinessObject, long requestID) {

		super(requestID);
		abstractBusinessObjects = new ArrayList<AbstractBusinessObject>();
		abstractBusinessObjects.add(abstractBusinessObject);
	}
	
	/**
	 * Instantiates a new update request.
	 *
	 * @param abstractBusinessObjects the abstract business objects
	 * @param requestID the request id
	 */
	public UpdateRequest(List<AbstractBusinessObject> abstractBusinessObjects, long requestID) {

		super(requestID);
		this.abstractBusinessObjects = abstractBusinessObjects;
	}
	
	
	/**
	 * Gets the abstract business objects.
	 *
	 * @return the abstract business objects
	 */
	public List<AbstractBusinessObject> getAbstractBusinessObjects() {
	
		return abstractBusinessObjects;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onUpdateRequest(this, channel);
		
	}




}
