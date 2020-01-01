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
 * The Class SecurityLeg.
 */
@Entity
public class SecurityLeg implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="securitydetails_id", insertable=false, updatable=false, nullable=false)
    private SecurityDetails security;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private FSecurity legSecurity = null;
	
	@Column(unique = false, nullable = true)
	private Double ratioQuantity = null;
	
	@Column(unique = false, nullable = true)
	private Double optionRatio = null;
	
	@Column(unique = false, nullable = true)
	private String side = null;
	
	
	
	/**
	 * Gets the option ratio.
	 *
	 * @return the option ratio
	 */
	public Double getOptionRatio() {
	
		return optionRatio;
	}




	
	/**
	 * Sets the option ratio.
	 *
	 * @param optionRatio the new option ratio
	 */
	public void setOptionRatio(Double optionRatio) {
	
		this.optionRatio = optionRatio;
	}




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
	 * Gets the security.
	 *
	 * @return the security
	 */
	public SecurityDetails getSecurity() {
	
		return security;
	}



	
	/**
	 * Sets the security.
	 *
	 * @param security the new security
	 */
	public void setSecurity(SecurityDetails security) {
	
		this.security = security;
	}





	/**
	 * Gets the leg security.
	 *
	 * @return the leg security
	 */
	public FSecurity getLegSecurity() {
	
		return legSecurity;
	}



	
	/**
	 * Sets the leg security.
	 *
	 * @param legSecurity the new leg security
	 */
	public void setLegSecurity(FSecurity legSecurity) {
	
		this.legSecurity = legSecurity;
	}



	
	/**
	 * Gets the ratio quantity.
	 *
	 * @return the ratio quantity
	 */
	public Double getRatioQuantity() {
	
		return ratioQuantity;
	}



	
	/**
	 * Sets the ratio quantity.
	 *
	 * @param ratioQuantity the new ratio quantity
	 */
	public void setRatioQuantity(Double ratioQuantity) {
	
		this.ratioQuantity = ratioQuantity;
	}



	
	/**
	 * Gets the side.
	 *
	 * @return the side
	 */
	public String getSide() {
	
		return side;
	}



	
	/**
	 * Sets the side.
	 *
	 * @param side the new side
	 */
	public void setSide(String side) {
	
		this.side = side;
	}



	/**
	 * Make eager.
	 */
	public void makeEager() {

		legSecurity.getName();
		
	}
	


	
	
}
