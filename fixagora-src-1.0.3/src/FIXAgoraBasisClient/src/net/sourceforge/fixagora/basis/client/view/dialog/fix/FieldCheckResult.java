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
package net.sourceforge.fixagora.basis.client.view.dialog.fix;

import java.awt.Component;

import javax.swing.ImageIcon;

/**
 * The Class FieldCheckResult.
 */
public class FieldCheckResult {

	private int bugLevel = 0;

	private final ImageIcon error = new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	private String tooltipText = null;

	private final ImageIcon warning = new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));

	/**
	 * Instantiates a new field check result.
	 *
	 * @param bugLevel the bug level
	 * @param tooltipText the tooltip text
	 * @param component the component
	 */
	public FieldCheckResult(final int bugLevel, final String tooltipText, final Component component) {

		super();

		this.bugLevel = bugLevel;
		this.tooltipText = tooltipText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object object) {

		if (object == null)
			return false;

		if (object instanceof FieldCheckResult) {

			final FieldCheckResult fieldCheckResult = (FieldCheckResult) object;

			if (fieldCheckResult.getToolTipText().equals(getToolTipText()) && fieldCheckResult.getBugLevel() == getBugLevel())
				return true;
		}

		return false;
	}

	/**
	 * Gets the bug level.
	 *
	 * @return the bug level
	 */
	public int getBugLevel() {

		return bugLevel;
	}

	/**
	 * Gets the image icon.
	 *
	 * @return the image icon
	 */
	public ImageIcon getImageIcon() {

		switch (bugLevel) {

			case 2:
				return error;

			case 1:
				return warning;

			default:
				return null;
		}
	}

	/**
	 * Gets the tool tip text.
	 *
	 * @return the tool tip text
	 */
	public String getToolTipText() {

		return tooltipText;
	}

}
