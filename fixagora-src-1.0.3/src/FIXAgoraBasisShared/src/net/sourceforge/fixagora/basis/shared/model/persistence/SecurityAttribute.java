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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class SecurityAttribute.
 */
@Entity
public class SecurityAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;	
	
	@ManyToOne
    @JoinColumn(name="securitydetails_id", insertable=false, updatable=false, nullable=false)
    private SecurityDetails security;
	
	@Column(unique = false, nullable = true)
	private Integer attributeType = null;
	
	@Column(unique = false, nullable = true)
	private String attributeValue = null;

	
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
	 * Gets the attribute type.
	 *
	 * @return the attribute type
	 */
	public Integer getAttributeType() {
	
		return attributeType;
	}

	
	/**
	 * Sets the attribute type.
	 *
	 * @param attributeType the new attribute type
	 */
	public void setAttributeType(Integer attributeType) {
	
		this.attributeType = attributeType;
	}

	
	/**
	 * Gets the attribute value.
	 *
	 * @return the attribute value
	 */
	public String getAttributeValue() {
	
		return attributeValue;
	}

	
	/**
	 * Sets the attribute value.
	 *
	 * @param attributeValue the new attribute value
	 */
	public void setAttributeValue(String attributeValue) {
	
		this.attributeValue = attributeValue;
	}
	
	
	
}
