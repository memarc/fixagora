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
package net.sourceforge.agora.simulator.model;

import java.util.HashSet;
import java.util.Set;

import javax.swing.RowFilter;

import net.sourceforge.agora.simulator.model.BlotterEntry.LevelIcon;

/**
 * The Class FIXAgoraSimulatorRowFilter.
 */
public class FIXAgoraSimulatorRowFilter extends RowFilter<FIXAgoraSimulatorTableModel, Integer> {

	private boolean hideMessage = true;

	private Set<String> showCounterparty = new HashSet<String>();
	
	/**
	 * Sets the hide message.
	 *
	 * @param hideMessage the new hide message
	 */
	public void setHideMessage(boolean hideMessage) {
	
		this.hideMessage = hideMessage;
	}



	/* (non-Javadoc)
	 * @see javax.swing.RowFilter#include(javax.swing.RowFilter.Entry)
	 */
	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends FIXAgoraSimulatorTableModel, ? extends Integer> entry) {

		if (entry.getIdentifier() instanceof Integer) {
			int row = entry.getIdentifier();
			BlotterEntry blotterEntry = entry.getModel().getBlotterEntry(row);
			
			if(hideMessage&&blotterEntry.getLevelIcon()==LevelIcon.MESSAGE)
				return false;
			
			if(blotterEntry.getCounterparty()!=null&&!showCounterparty.contains(blotterEntry.getCounterparty()))
				return false;
			
			return true;
		}
		return true;
		
	}
	
	
	/**
	 * Adds the counterparty.
	 *
	 * @param counterparty the counterparty
	 */
	public void addCounterparty(String counterparty)
	{
		showCounterparty.add(counterparty);
	}
	
	/**
	 * Removes the counterparty.
	 *
	 * @param counterparty the counterparty
	 */
	public void removeCounterparty(String counterparty)
	{
		showCounterparty.remove(counterparty);
	}

}
