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



/**
 * The Interface BusinessObjectGroup.
 */
public interface BusinessObjectGroup {

	/**
	 * Gets the child class.
	 *
	 * @return the child class
	 */
	public Class<? extends AbstractBusinessObject> getChildClass();
	
	/**
	 * Checks if is subfolder allowed.
	 *
	 * @return true, if is subfolder allowed
	 */
	public boolean isSubfolderAllowed();

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Sets the description.
	 *
	 * @param text the new description
	 */
	public void setDescription(String text);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Sets the name.
	 *
	 * @param trim the new name
	 */
	public void setName(String trim);
	
}
