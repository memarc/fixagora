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

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * The Class IntegerDocument.
 */
public class IntegerDocument extends PlainDocument {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(final int offset, final String text, final AttributeSet attributeSet) throws BadLocationException {

		try {
			if (text.length() > 1) {
				
				for (int x = 0; x != text.length(); x++)
					insertString(x+offset, text.substring(x, x + 1), null);
				
				return;
			}
			else if (text.equals("-")) {
				
				String tempString = getText(0, getLength());
				
				if (tempString.indexOf("-") != -1)
					tempString = tempString.replaceAll("-", "");
				else
					tempString = "-" + tempString;
				
				replace(0, getLength(), null, attributeSet);
				
				super.insertString(0, tempString, attributeSet);
				
				return;
			}
			else if (text.equals("+")) {
				
				if (getText(0, getLength()).indexOf("-") != -1)
					insertString(0, "-", attributeSet);
				
				return;
			}
			else
				Integer.parseInt(text);

		}
		catch (final Exception ex) {
			
			Toolkit.getDefaultToolkit().beep();
			
			return;
		}
		
		super.insertString(offset, text, attributeSet);
	}
}