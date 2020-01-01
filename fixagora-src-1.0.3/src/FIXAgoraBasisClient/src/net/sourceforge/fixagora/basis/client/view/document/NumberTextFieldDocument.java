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

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import net.sourceforge.fixagora.basis.client.view.dialog.fix.SpreadSheetFIXInputDialog;

import org.apache.log4j.Logger;

/**
 * The Class NumberTextFieldDocument.
 */
public class NumberTextFieldDocument extends AbstractNumberDocument {

	private static final long serialVersionUID = 1L;
	
	private boolean dot = false;

	private Double minValue = null;

	private int decimalPlaces = Integer.MAX_VALUE;

	private boolean thousandsSeparator = false;

	private JTextField jTextField = null;
	
	private int reformat = 0;

	private static Logger log = Logger.getLogger(SpreadSheetFIXInputDialog.class);

	
	/**
	 * Instantiates a new number text field document.
	 *
	 * @param jTextField the j text field
	 */
	public NumberTextFieldDocument(JTextField jTextField) {

		super();
		this.jTextField = jTextField;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setThousandsSeparator(boolean)
	 */
	public void setThousandsSeparator(boolean thousandsSeparator) {

		this.thousandsSeparator = thousandsSeparator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setMinValue(java.lang.Double)
	 */
	public void setMinValue(Double minValue) {

		this.minValue = minValue;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setDecimalPlaces(int)
	 */
	public void setDecimalPlaces(int decimalPlaces) {

		this.decimalPlaces = decimalPlaces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String,
	 * javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(final int offset, final String originalText, final AttributeSet attributeSet) throws BadLocationException {

		reformat++;

		String text = originalText.replaceAll(",", "");
		text = text.replaceAll("m", "000");

		try {

			dot = getText(0, getLength()).indexOf(".") == -1 ? false : true;

			if (text.length() > 1) {

				for (int x = 0; x != text.length(); x++)
					insertString(x + offset, text.substring(x, x + 1), null);
				reformat();
				return;
			}
			if(decimalPlaces==0&&text.equals("."))
			{
				throw new Exception();
			}
			else if (text.equals("-")) {

				if(minValue!=null&&minValue>=0)
					throw new Exception();
				
				int caretPosition = jTextField.getCaretPosition();
				
				String tempString = getText(0, getLength());

				if (tempString.indexOf("-") != -1)
					tempString = tempString.replaceAll("-", "");
				else
				{
					caretPosition++;
					tempString = "-" + tempString;
				}

				replace(0, getLength(), null, attributeSet);

				super.insertString(0, tempString, attributeSet);
				jTextField.setCaretPosition(caretPosition);
				reformat();
				return;

			}
			else if (text.equals("+")) {

				if (getText(0, getLength()).indexOf("-") != -1)
					insertString(0, "-", attributeSet);
				reformat();
				return;
			}
			else if (!text.equals(".") || dot)
				Double.parseDouble(text);

		}
		catch (final Exception ex) {

			Toolkit.getDefaultToolkit().beep();
			reformat();
			return;
		}

		super.insertString(offset, text, attributeSet);

		reformat();
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#remove(int, int)
	 */
	@Override
	public void remove(int offs, int len) throws BadLocationException {

		super.remove(offs, len);
		reformat++;
		reformat();
	}

	private void reformat() throws BadLocationException {

		reformat--;
		if (reformat == 0) {

			try {
				if (thousandsSeparator) {

					String text = jTextField.getText();

					int caretPosition = jTextField.getCaretPosition();
					for (int i = caretPosition-1; i >= 0; i--) {
						if (text.substring(i, i + 1).equals(","))
							caretPosition--;
					}
					text = text.replaceAll(",", "");
					int offset = text.lastIndexOf(".");
					if (offset == -1)
						offset = text.length();
					int stop = 1;
					if (text.startsWith("-"))
						stop = 2;
					int i = 1;
					StringBuffer newText = new StringBuffer(text);
					int caretOffset = 0;
					for (int j = offset - 1; j > stop; j--) {
						i++;
						if (i % 3 == 0) {
							newText.insert(j - 1, ",");
							if (j <= caretPosition)
								caretOffset++;
						}
					}
					super.remove(0, jTextField.getText().length());

					super.insertString(0, newText.toString(), null);

					jTextField.setCaretPosition(caretPosition + caretOffset);
				}
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setTickSize(double)
	 */
	@Override
	public void setTickSize(double tickSize) {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#removeListener()
	 */
	@Override
	public void removeListener() {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setLeadingZeros(int)
	 */
	@Override
	public void setLeadingZeros(int leadingZeros) {

		// TODO Auto-generated method stub
		
	}


}
