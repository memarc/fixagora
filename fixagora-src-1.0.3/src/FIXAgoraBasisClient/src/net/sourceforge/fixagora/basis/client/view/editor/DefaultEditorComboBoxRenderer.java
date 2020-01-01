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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;


/**
 * The Class DefaultEditorComboBoxRenderer.
 */
public class DefaultEditorComboBoxRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new default editor combo box renderer.
		 */
		public DefaultEditorComboBoxRenderer() {

			setOpaque(false);
			setHorizontalAlignment(LEFT);
			setBorder(new EmptyBorder(0, 5, 0, 0));
			setVerticalAlignment(CENTER);
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			if(value!=null)
				setText(value.toString());
			return this;
		}

}
