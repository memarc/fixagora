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
package net.sourceforge.fixagora.buyside.client.model.editor;

import javax.swing.RowFilter;

/**
 * The Class BuySideBookEditorMonitorRowFilter.
 */
public class BuySideBookEditorMonitorRowFilter extends RowFilter<BuySideBookEditorMonitorTableModel, Integer> {

	private boolean hideCancel = false;

	private boolean hideCompleteFilled = false;

	/* (non-Javadoc)
	 * @see javax.swing.RowFilter#include(javax.swing.RowFilter.Entry)
	 */
	@Override
	public boolean include(Entry<? extends BuySideBookEditorMonitorTableModel, ? extends Integer> entry) {

		if (entry.getIdentifier() instanceof Integer) {
			boolean include = true;

			int row = entry.getIdentifier();
			if (!entry.getModel().isUpdated(row)) {
				if (hideCancel)
					include = !entry.getModel().isCanceled(row);
				if (hideCompleteFilled)
					include = include && !entry.getModel().isCompleteFilled(row);
			}
			return include;
		}
		return true;
	}

	/**
	 * Sets the hide cancel.
	 *
	 * @param selected the new hide cancel
	 */
	public void setHideCancel(boolean selected) {

		hideCancel = selected;

	}

	/**
	 * Sets the complete filled.
	 *
	 * @param selected the new complete filled
	 */
	public void setCompleteFilled(boolean selected) {

		hideCompleteFilled = selected;

	}

}
