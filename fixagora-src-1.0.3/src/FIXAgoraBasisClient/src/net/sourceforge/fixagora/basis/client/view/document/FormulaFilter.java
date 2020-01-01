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
package net.sourceforge.fixagora.basis.client.view.document;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;


/**
 * The Class FormulaFilter.
 */
public class FormulaFilter extends NavigationFilter
{
    private Action deletePrevious;

    /**
     * Instantiates a new formula filter.
     *
     * @param component the component
     */
    public FormulaFilter(JTextComponent component)
    {
        deletePrevious = component.getActionMap().get("delete-previous");
        component.getActionMap().put("delete-previous", new BackspaceAction());
        component.setCaretPosition(1);
    }

    /* (non-Javadoc)
     * @see javax.swing.text.NavigationFilter#setDot(javax.swing.text.NavigationFilter.FilterBypass, int, javax.swing.text.Position.Bias)
     */
    public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
    {
        fb.setDot(Math.max(dot, 1), bias);
    }

    /* (non-Javadoc)
     * @see javax.swing.text.NavigationFilter#moveDot(javax.swing.text.NavigationFilter.FilterBypass, int, javax.swing.text.Position.Bias)
     */
    public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
    {
        fb.moveDot(Math.max(dot, 1), bias);
    }

    /**
     * The Class BackspaceAction.
     */
    class BackspaceAction extends AbstractAction
    {
 
		private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
        {
            JTextComponent component = (JTextComponent)e.getSource();

            if (component.getCaretPosition() > 1)
            {
                deletePrevious.actionPerformed( null );
            }
        }
    }
}
