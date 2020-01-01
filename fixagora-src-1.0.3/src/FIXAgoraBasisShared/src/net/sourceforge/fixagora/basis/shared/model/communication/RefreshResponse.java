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

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;


/**
 * The Class RefreshResponse.
 */
public class RefreshResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private AbstractBusinessObject abstractBusinessObject = null;
	
	/**
	 * Instantiates a new refresh response.
	 *
	 * @param refreshRequest the refresh request
	 * @param abstractBusinessObject the abstract business object
	 */
	public RefreshResponse(RefreshRequest refreshRequest, AbstractBusinessObject abstractBusinessObject) {

		super(refreshRequest);
		this.abstractBusinessObject = abstractBusinessObject;
	}
	
	/**
	 * Gets the abstract business object.
	 *
	 * @return the abstract business object
	 */
	public AbstractBusinessObject getAbstractBusinessObject() {
	
		return abstractBusinessObject;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
	}

}
