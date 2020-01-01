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


/**
 * The Class UpdateResponse.
 */
public class UpdateResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum UpdateStatus.
	 */
	public static enum UpdateStatus{/** The success. */
SUCCESS,/** The failed. */
FAILED, /** The partially failed. */
 PARTIALLY_FAILED};
	
	private UpdateStatus persistStatus = null;
	
	private List<AbstractBusinessObject> abstractBusinessObjects = null;
	
	/**
	 * Instantiates a new update response.
	 *
	 * @param persistRequest the persist request
	 * @param abstractBusinessObject the abstract business object
	 * @param persistStatus the persist status
	 */
	public UpdateResponse(UpdateRequest persistRequest, AbstractBusinessObject abstractBusinessObject, UpdateStatus persistStatus) {

		super(persistRequest);
		this.persistStatus = persistStatus;
		this.abstractBusinessObjects = new ArrayList<AbstractBusinessObject>();
		this.abstractBusinessObjects.add(abstractBusinessObject);
	}
	
	/**
	 * Instantiates a new update response.
	 *
	 * @param persistRequest the persist request
	 * @param abstractBusinessObjects the abstract business objects
	 * @param persistStatus the persist status
	 */
	public UpdateResponse(UpdateRequest persistRequest, List<AbstractBusinessObject> abstractBusinessObjects, UpdateStatus persistStatus) {

		super(persistRequest);
		this.persistStatus = persistStatus;
		this.abstractBusinessObjects = abstractBusinessObjects;
	}
	
	/**
	 * Instantiates a new update response.
	 *
	 * @param persistRequest the persist request
	 * @param abstractBusinessObjects the abstract business objects
	 */
	public UpdateResponse(UpdateRequest persistRequest, List<AbstractBusinessObject> abstractBusinessObjects) {

		super(persistRequest);
		this.persistStatus = UpdateStatus.SUCCESS;
		this.abstractBusinessObjects = abstractBusinessObjects;
	}
	
	/**
	 * Instantiates a new update response.
	 *
	 * @param abstractBusinessObjects the abstract business objects
	 */
	public UpdateResponse(List<AbstractBusinessObject> abstractBusinessObjects) {

		super(null);
		this.persistStatus = UpdateStatus.SUCCESS;
		this.abstractBusinessObjects = abstractBusinessObjects;
	}
	
	/**
	 * Instantiates a new update response.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public UpdateResponse(AbstractBusinessObject abstractBusinessObject) {

		super(null);
		this.persistStatus = UpdateStatus.SUCCESS;
		this.abstractBusinessObjects = new ArrayList<AbstractBusinessObject>();
		this.abstractBusinessObjects.add(abstractBusinessObject);
	}
	
	/**
	 * Gets the update status.
	 *
	 * @return the update status
	 */
	public UpdateStatus getUpdateStatus() {
	
		return persistStatus;
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

		fBasisResponses.onUpdateResponse(this);	
	}

}
