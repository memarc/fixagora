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

import java.util.Set;


/**
 * The Interface PersistenceInterface.
 */
public interface PersistenceInterface extends Comparable<Object>{

	/**
	 * Make eager.
	 */
	public void makeEager();

	/**
	 * Gets the write roles.
	 *
	 * @return the write roles
	 */
	public Set<FRole> getWriteRoles();
	
	/**
	 * Gets the read roles.
	 *
	 * @return the read roles
	 */
	public Set<FRole> getReadRoles();
	
	/**
	 * Gets the execute roles.
	 *
	 * @return the execute roles
	 */
	public Set<FRole> getExecuteRoles();

	/**
	 * Sets the transient values.
	 *
	 * @param transientValueSetter the new transient values
	 */
	public void setTransientValues(TransientValueSetter transientValueSetter);
	
}
