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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * The Class AbstractBusinessComponent.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractBusinessComponent extends AbstractBusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The data dictionary. */
	@Column(unique = false, nullable = true)
	public String dataDictionary;

	/** The started. */
	@Transient
	protected int started = 0;

	/**
	 * Gets the input components.
	 *
	 * @return the input components
	 */
	public List<AbstractBusinessComponent> getInputComponents() {

		return new ArrayList<AbstractBusinessComponent>();
	}

	/**
	 * Gets the output components.
	 *
	 * @return the output components
	 */
	public List<AbstractBusinessComponent> getOutputComponents() {

		return new ArrayList<AbstractBusinessComponent>();
	}

	/**
	 * Gets the data dictionary.
	 *
	 * @return the data dictionary
	 */
	public String getDataDictionary() {

		return dataDictionary;
	}

	/**
	 * Sets the data dictionary.
	 *
	 * @param dataDictionary the new data dictionary
	 */
	public void setDataDictionary(String dataDictionary) {

		this.dataDictionary = dataDictionary;
	}

	/**
	 * Checks if is startable.
	 *
	 * @return true, if is startable
	 */
	public boolean isStartable() {

		return false;
	}

	/**
	 * Gets the start level.
	 *
	 * @return the start level
	 */
	public int getStartLevel() {

		return started;
	}
	
	/**
	 * Gets the component class.
	 *
	 * @return the component class
	 */
	public abstract String getComponentClass();

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	@Override
	public void setTransientValues(TransientValueSetter transientValueSetter) {

		started = transientValueSetter.getStartLevel(this);
	}

}
