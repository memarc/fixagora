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
 * The Class PersistResponse.
 */
public class PersistResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum PersistStatus.
	 */
	public static enum PersistStatus{/** The success. */
SUCCESS,/** The failed. */
FAILED};
	
	private PersistStatus persistStatus = null;
	
	private AbstractBusinessObject abstractBusinessObject = null;
	
	/**
	 * Instantiates a new persist response.
	 *
	 * @param persistRequest the persist request
	 * @param abstractBusinessObject the abstract business object
	 * @param persistStatus the persist status
	 */
	public PersistResponse(PersistRequest persistRequest, AbstractBusinessObject abstractBusinessObject, PersistStatus persistStatus) {

		super(persistRequest);
		this.persistStatus = persistStatus;
		this.abstractBusinessObject = abstractBusinessObject;
	}
	
	/**
	 * Instantiates a new persist response.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public PersistResponse(AbstractBusinessObject abstractBusinessObject) {

		super(null);
		this.persistStatus = PersistStatus.SUCCESS;
		this.abstractBusinessObject = abstractBusinessObject;
	}
	
	/**
	 * Gets the persist status.
	 *
	 * @return the persist status
	 */
	public PersistStatus getPersistStatus() {
	
		return persistStatus;
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

		fBasisResponses.onPersistResponse(this);	
	}

}
