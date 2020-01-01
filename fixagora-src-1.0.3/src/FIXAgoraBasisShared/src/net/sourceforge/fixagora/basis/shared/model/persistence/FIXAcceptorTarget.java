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
package net.sourceforge.fixagora.basis.shared.model.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class FIXAcceptorTarget.
 */
@Entity
public class FIXAcceptorTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="fixacceptor_id", insertable=false, updatable=false, nullable=false)
    private AbstractAcceptor fixAcceptor;
	
	@Column(unique = false, nullable = false)
	private String targetCompID = null;

	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private Counterparty counterparty = null;


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}



	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
	}




	
	/**
	 * Gets the fix acceptor.
	 *
	 * @return the fix acceptor
	 */
	public AbstractAcceptor getFixAcceptor() {
	
		return fixAcceptor;
	}




	
	/**
	 * Sets the fix acceptor.
	 *
	 * @param fixAcceptor the new fix acceptor
	 */
	public void setFixAcceptor(AbstractAcceptor fixAcceptor) {
	
		this.fixAcceptor = fixAcceptor;
	}




	
	/**
	 * Gets the target comp id.
	 *
	 * @return the target comp id
	 */
	public String getTargetCompID() {
	
		return targetCompID;
	}




	
	/**
	 * Sets the target comp id.
	 *
	 * @param targetCompID the new target comp id
	 */
	public void setTargetCompID(String targetCompID) {
	
		this.targetCompID = targetCompID;
	}




	
	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public Counterparty getCounterparty() {
	
		return counterparty;
	}




	
	/**
	 * Sets the counterparty.
	 *
	 * @param counterparty the new counterparty
	 */
	public void setCounterparty(Counterparty counterparty) {
	
		this.counterparty = counterparty;
	}




	/**
	 * Make eager.
	 */
	public void makeEager() {

		getCounterparty().getName();
		
	}


	
	



	
	
}
