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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;

/**
 * The Class FIXAcceptorTargetEditorTableRenderer.
 */
public class FIXAcceptorTargetEditorTableRenderer implements TableCellRenderer {

	private AbstractBusinessObjectEditor abstractBusinessObjectEditor = null;
	
	private final ImageIcon greenLED = new ImageIcon(FIXAcceptorTargetEditorTableRenderer.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/greenled.png"));
	
	private final ImageIcon redLED = new ImageIcon(FIXAcceptorTargetEditorTableRenderer.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/redled.png"));

	/**
	 * Instantiates a new fIX acceptor target editor table renderer.
	 *
	 * @param abstractBusinessObjectEditor the abstract business object editor
	 */
	public FIXAcceptorTargetEditorTableRenderer(AbstractBusinessObjectEditor abstractBusinessObjectEditor) {

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

		if (abstractBusinessObjectEditor.getBasisClientConnector().getFUser().canWrite(abstractBusinessObjectEditor.getAbstractBusinessObject())&&((FIXAcceptor)abstractBusinessObjectEditor.getAbstractBusinessObject()).getStartLevel()==0) {

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
		
		if(column==0)
		{
			if(value!=null)
			{
				if(value.equals("Online"))
				{
					component.setForeground(new Color(51,153,0));
					component.setIcon(greenLED);
				}
				if(value.equals("Offline"))
				{
					component.setForeground(new Color(204,0,0));
					component.setIcon(redLED);
				}
			}
		}

		component.setOpaque(true);

		return component;
	}

}
