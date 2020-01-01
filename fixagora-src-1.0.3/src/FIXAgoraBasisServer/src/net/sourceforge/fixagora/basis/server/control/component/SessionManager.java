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
package net.sourceforge.fixagora.basis.server.control.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * The Class SessionManager.
 */
public class SessionManager {
	
	/** The incremental refresh. */
	public boolean incrementalRefresh = false;
	
	private Map<String, Set<Character>> securities = new HashMap<String, Set<Character>>();
	
	private Set<Character> entryTypes = new HashSet<Character>();
	
	private Map<String, Map<Character, String>> refIdMap = new HashMap<String, Map<Character,String>>();

	/**
	 * Instantiates a new session manager.
	 *
	 * @param incrementalRefresh the incremental refresh
	 */
	public SessionManager(boolean incrementalRefresh) {

		super();
		this.incrementalRefresh = incrementalRefresh;
	}

	/**
	 * Adds the security.
	 *
	 * @param security the security
	 */
	public void addSecurity(String security)
	{
		securities.put(security, new HashSet<Character>());
	}
	
	/**
	 * Removes the security.
	 *
	 * @param security the security
	 */
	public void removeSecurity(String security)
	{
		securities.remove(security);
	}
	
	/**
	 * Adds the entry type.
	 *
	 * @param entry the entry
	 */
	public void addEntryType(char entry)
	{
		entryTypes.add(entry);
	}
	
	
	
	
	/**
	 * Sets the incremental refresh.
	 *
	 * @param incrementalRefresh the new incremental refresh
	 */
	public void setIncrementalRefresh(boolean incrementalRefresh) {
	
		this.incrementalRefresh = incrementalRefresh;
		
	}


	
	/**
	 * Checks if is incremental refresh.
	 *
	 * @return true, if is incremental refresh
	 */
	public boolean isIncrementalRefresh() {
	
		return incrementalRefresh;
	}

	
	
	/**
	 * Gets the securities.
	 *
	 * @return the securities
	 */
	public Collection<String> getSecurities() {
	
		return securities.keySet();
	}
	

	
	/**
	 * Gets the entry types.
	 *
	 * @return the entry types
	 */
	public Set<Character> getEntryTypes() {
	
		return entryTypes;
	}

	/**
	 * Gets the ref id.
	 *
	 * @param securityID the security id
	 * @param entry the entry
	 * @return the ref id
	 */
	public String getRefId(String securityID, char entry) {

		Map<Character, String> entryRefMap = refIdMap.get(securityID);
		if(entryRefMap==null)
			return null;
		return entryRefMap.get(entry);
	}

	/**
	 * Sets the ref id.
	 *
	 * @param securityID the security id
	 * @param entry the entry
	 * @param entryID the entry id
	 */
	public void setRefId(String securityID, char entry, String entryID) {

		Map<Character, String> entryRefMap = refIdMap.get(securityID);
		if(entryRefMap==null)
		{
			entryRefMap = new HashMap<Character, String>();
			refIdMap.put(securityID, entryRefMap);
		}
		entryRefMap.put(entry, entryID);
		
	}

	/**
	 * Removes the ref id.
	 *
	 * @param securityID the security id
	 * @param entry the entry
	 */
	public void removeRefId(String securityID, char entry) {

		Map<Character, String> entryRefMap = refIdMap.get(securityID);
		if(entryRefMap!=null)
		{
			entryRefMap.remove(entry);
		}
		
		
	}
	

}
