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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MDInputEntryList.
 */
public class MDInputEntryList {

	private List<MDInputEntry> mdInputEntries = new ArrayList<MDInputEntry>();

	private void add(MDInputEntry mdInputEntry) {

		boolean replaced = false;

		for (int i = 0; i < mdInputEntries.size(); i++) {
			MDInputEntry mdInputEntry2 = mdInputEntries.get(i);
			if (mdInputEntry.canReplace(mdInputEntry2)) {
				mdInputEntries.remove(i);
				mdInputEntries.add(i, mdInputEntry);
				replaced = true;
				i = mdInputEntries.size();
			}
		}

		if (!replaced)
			mdInputEntries.add(mdInputEntry);
	}

	/**
	 * Adds the.
	 *
	 * @param mdInputEntries the md input entries
	 */
	public void add(List<MDInputEntry> mdInputEntries) {

		synchronized (this.mdInputEntries) {
			for (MDInputEntry mdInputEntry : mdInputEntries)
				add(mdInputEntry);

			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Take.
	 *
	 * @return the list
	 */
	public List<MDInputEntry> take() {

		synchronized (mdInputEntries) {
			if (mdInputEntries.size() > 0) {
				ArrayList<MDInputEntry> list = new ArrayList<MDInputEntry>(mdInputEntries);
				mdInputEntries.clear();
				return list;
			}
		}

		int size = 0;

		synchronized (mdInputEntries) {
			size = mdInputEntries.size();
		}

		if (size == 0) {
			synchronized (this) {
				try {
					wait();

				}
				catch (InterruptedException e) {
				}
			}
		}

		synchronized (mdInputEntries) {
			ArrayList<MDInputEntry> list = new ArrayList<MDInputEntry>(mdInputEntries);
			mdInputEntries.clear();
			return list;
		}
	}
}
