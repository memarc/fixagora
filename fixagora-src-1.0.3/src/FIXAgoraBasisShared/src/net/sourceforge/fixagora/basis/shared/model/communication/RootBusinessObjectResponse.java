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

import java.util.List;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;


/**
 * The Class RootBusinessObjectResponse.
 */
public class RootBusinessObjectResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = -2163161729367033425L;
	
	private List<AbstractBusinessObject> abstractBusinessObjects = null;	

	/**
	 * Instantiates a new root business object response.
	 *
	 * @param abstractRequest the abstract request
	 * @param abstractBusinessObjects the abstract business objects
	 */
	public RootBusinessObjectResponse(RootBusinessObjectRequest abstractRequest, List<AbstractBusinessObject> abstractBusinessObjects) {

		super(abstractRequest);
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
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
	}

	
	
}
