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
 * The Interface TransientValueSetter.
 */
public interface TransientValueSetter {

	/**
	 * Checks if is logged in.
	 *
	 * @param fUser the f user
	 */
	public void isLoggedIn(FUser fUser);
	
	/**
	 * Gets the start level.
	 *
	 * @param abstractBusinessComponent the abstract business component
	 * @return the start level
	 */
	public int getStartLevel(AbstractBusinessComponent abstractBusinessComponent);
	
	/**
	 * Gets the child count.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @return the child count
	 */
	public int getChildCount(FSecurityGroup abstractBusinessObject);

	/**
	 * Gets the open sessions.
	 *
	 * @param fixAcceptor the fix acceptor
	 * @return the open sessions
	 */
	public Set<String> getOpenSessions(AbstractAcceptor fixAcceptor);

}
