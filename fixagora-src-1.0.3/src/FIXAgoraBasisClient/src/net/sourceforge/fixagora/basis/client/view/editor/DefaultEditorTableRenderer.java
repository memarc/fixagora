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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * The Class DefaultEditorTableRenderer.
 */
public class DefaultEditorTableRenderer implements TableCellRenderer {

	private AbstractBusinessObjectEditor abstractBusinessObjectEditor = null;
	
	private boolean enabled = true;

	/**
	 * Instantiates a new default editor table renderer.
	 *
	 * @param abstractBusinessObjectEditor the abstract business object editor
	 */
	public DefaultEditorTableRenderer(AbstractBusinessObjectEditor abstractBusinessObjectEditor) {

		super();
		this.abstractBusinessObjectEditor = abstractBusinessObjectEditor;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		JLabel component = new JLabel(value.toString());

		component.setFont(new Font("Dialog", Font.PLAIN, 12));
		component.setBorder(new EmptyBorder(5, 5, 5, 5));

		if (abstractBusinessObjectEditor.getBasisClientConnector().getFUser().canWrite(abstractBusinessObjectEditor.getAbstractBusinessObject())&&enabled) {

			if(isSelected)
				component.setBackground(new Color(179, 196, 255));
			
			else if (row % 2 == 0)
				component.setBackground(new Color(255, 243, 204));

			else
				component.setBackground(new Color(255, 236, 179));
		}
		else {

			if (row % 2 == 0)
				component.setBackground(new Color(204, 216, 255));

			else
				component.setBackground(new Color(179, 196, 255));
		}

		component.setOpaque(true);

		return component;
	}

	
	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
	
		return enabled;
	}

	
	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled) {
	
		this.enabled = enabled;
	}
	
	

}
