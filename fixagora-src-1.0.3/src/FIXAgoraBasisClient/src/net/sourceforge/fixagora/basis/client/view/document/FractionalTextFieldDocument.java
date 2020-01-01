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

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import net.sourceforge.fixagora.basis.client.view.dialog.fix.SpreadSheetFIXInputDialog;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;

import org.apache.log4j.Logger;

/**
 * The Class FractionalTextFieldDocument.
 */
public class FractionalTextFieldDocument extends AbstractNumberDocument {

	private static final long serialVersionUID = 1L;

	private Double minValue = null;

	private boolean thousandsSeparator = false;
	
	private int leadingZeros = 1;

	private JTextField jTextField = null;

	private int reformat = 0;

	private static Logger log = Logger.getLogger(SpreadSheetFIXInputDialog.class);

	private int secondDenominator = 32;

	private DollarFraction dollarFraction = null;

	private int secondDenominatorLength = 2;

	private int lastCaretPosition = 0;

	private int replace = 0;

	private boolean selectionChange = false;

	private CaretListener caretListener = new CaretListener() {

		@Override
		public void caretUpdate(CaretEvent e) {

			FractionalTextFieldDocument.this.caretUpdate();

		}
	};

	/**
	 * Instantiates a new fractional text field document.
	 *
	 * @param jTextField the j text field
	 */
	public FractionalTextFieldDocument(final JTextField jTextField) {

		super();
		try {
			this.dollarFraction = new DollarFraction(0.0009765625);
		}
		catch (Exception e) {
		}
		this.jTextField = jTextField;
		jTextField.addCaretListener(caretListener);
		try {
			super.insertString(0, dollarFraction.getDollarPrice(0, leadingZeros),  null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#setLeadingZeros(int)
	 */
	public void setLeadingZeros(int leadingZeros) {
	
		this.leadingZeros = leadingZeros;
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

	}

	/* (non-Javadoc)
	 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(final int offset, final String originalText, final AttributeSet attributeSet) throws BadLocationException {

		if (replace == 0 && originalText.equals("-")) {
			changeSign();
			return;
		}

		if (replace == 0 && originalText.equals("+")) {
			setHalfTick();
			return;
		}

		if (replace == 0 && originalText.length() > 1)
			return;
		super.insertString(offset, originalText, attributeSet);

		reformat++;
		reformat();
	}

	private synchronized void caretUpdate() {

		if (selectionChange)
			return;

		try {

			if (jTextField.getCaretPosition() == 0 && jTextField.getText().startsWith("-")&&jTextField.getText().lastIndexOf('-')!=jTextField.getText().indexOf('-')) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						jTextField.setCaretPosition(1);
					}

				});
			}
			else if (jTextField.getCaretPosition() == getLength() - 6 - 2 * secondDenominatorLength
					|| jTextField.getCaretPosition() == getLength() - 3 - 2 * secondDenominatorLength) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						try {
							if (lastCaretPosition < jTextField.getCaretPosition()) {
								lastCaretPosition = jTextField.getCaretPosition();
								jTextField.setCaretPosition(jTextField.getCaretPosition() + 1);
							}
							else {
								lastCaretPosition = jTextField.getCaretPosition();
								jTextField.setCaretPosition(jTextField.getCaretPosition() - 1);
							}
						}
						catch (Exception e) {
						}

					}
				});
			}
			else if (jTextField.getCaretPosition() > getLength() - 3 - secondDenominatorLength) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						jTextField.setCaretPosition(lastCaretPosition);

					}
				});
			}
			else if (jTextField.getCaretPosition() == getLength() - 5 - 2 * secondDenominatorLength
					|| jTextField.getCaretPosition() == getLength() - 4 - 2 * secondDenominatorLength
					|| jTextField.getCaretPosition() > getLength() - 3 - 2 * secondDenominatorLength) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						lastCaretPosition = jTextField.getCaretPosition();
						selectionChange = true;
						
						try {
							jTextField.setSelectionStart(jTextField.getCaretPosition() - 1);
							jTextField.setSelectionEnd(jTextField.getCaretPosition());
						}
						catch (Exception e) {
						}
						
						selectionChange = false;
					}
				});
			}
			else
				lastCaretPosition = jTextField.getCaretPosition();

			if (jTextField.getSelectionStart() - jTextField.getSelectionEnd() > 1) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						lastCaretPosition = jTextField.getCaretPosition();
						selectionChange = true;
						
						try {
							jTextField.setSelectionEnd(jTextField.getSelectionStart() - 1);
						}
						catch (Exception e) {
						}
						
						selectionChange = false;
					}
				});
			}

		}
		catch (Exception e1) {
		}
	}

	private synchronized void setHalfTick() {

		String tick = Integer.toString(secondDenominator / 2);
		while (tick.length() < secondDenominatorLength)
			tick = "0" + tick;

		selectionChange = true;
		int caretPosition = jTextField.getCaretPosition();

		StringBuffer stringBuffer2 = new StringBuffer();
		for (int i = 0; i < tick.length(); i++)
			stringBuffer2.append(Character.toString(DollarFraction.getNumeratorDigit(tick.charAt(i))));
		replace++;
		try {
			super.replace(getLength() - 3 - 2 * secondDenominatorLength, stringBuffer2.length(), stringBuffer2.toString(), null);
		}
		catch (BadLocationException e) {
		}
		replace--;

		jTextField.setCaretPosition(caretPosition);
		selectionChange = false;
		caretUpdate();

	}

	private synchronized void changeSign() throws BadLocationException {

		if (minValue == null || minValue.doubleValue() > 0) {

			selectionChange = true;
			int caretPosition = jTextField.getCaretPosition();
			if (jTextField.getText().indexOf('-') != 0 && jTextField.getText().lastIndexOf('-') == jTextField.getText().indexOf('-')) {
				jTextField.setText("-" + jTextField.getText());
				reformat++;
				reformat();
				jTextField.setCaretPosition(caretPosition + 1);
			}
			else if (jTextField.getText().indexOf('-') == 0 && jTextField.getText().lastIndexOf('-') != jTextField.getText().indexOf('-')) {
				jTextField.setText(jTextField.getText().substring(1));
				reformat++;
				reformat();
				jTextField.setCaretPosition(caretPosition - 1);
			}
			selectionChange = false;
			caretUpdate();

		}

	}

	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#replace(int, int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

		if (replace == 0 && text.equals("-")) {
			changeSign();
			return;
		}

		if (replace == 0 && text.equals("+")) {
			setHalfTick();
			return;
		}

		replace++;

		if (offset == 0 && length != 1 && (getLength() == 0 || length == getLength())) {
			Double doubleValue = DollarFraction.getDoubleValue(text);

			if (doubleValue != null) {
				super.replace(offset, length, text, attrs);
			}
		}
		else if (text.length() == 1) {

			if (Character.isDigit(text.charAt(0))) {

				if (jTextField.getCaretPosition() < getLength() - 6 - 2 * secondDenominatorLength) {
					super.replace(offset, length, text, attrs);
				}
				else if (jTextField.getCaretPosition() == getLength() - 5 - 2 * secondDenominatorLength) {
					StringBuffer stringBuffer = new StringBuffer(text);
					stringBuffer.append(jTextField.getText(getLength() - 5 - 2 * secondDenominatorLength, 1));
					try {
						Integer integer = Integer.parseInt(stringBuffer.toString());
						if (integer > 39)
							throw new NumberFormatException();
						if (integer > 31) {
							text = "30";
							length = 2;
						}
						super.replace(offset, length, text, attrs);
						jTextField.setCaretPosition(jTextField.getCaretPosition() + 2 - length);
					}
					catch (NumberFormatException e) {
						replace--;
						throw new BadLocationException("invalid tick", offset);
					}

				}
				else if (jTextField.getCaretPosition() == getLength() - 4 - 2 * secondDenominatorLength) {
					StringBuffer stringBuffer = new StringBuffer(jTextField.getText(getLength() - 6 - 2 * secondDenominatorLength, 1));
					stringBuffer.append(text);
					try {
						Integer integer = Integer.parseInt(stringBuffer.toString());
						if (integer > 31)
							throw new NumberFormatException();
						super.replace(offset, length, text, attrs);
						jTextField.setCaretPosition(jTextField.getCaretPosition() + 2);
					}
					catch (NumberFormatException e) {
						replace--;
						throw new BadLocationException("invalid tick", offset);
					}

				}
				else if (jTextField.getCaretPosition() > getLength() - 3 - 2 * secondDenominatorLength
						&& jTextField.getCaretPosition() < getLength() - 2 - secondDenominatorLength) {
					StringBuffer stringBuffer = new StringBuffer();
					for (int i = 0; i < secondDenominatorLength; i++) {

						if (offset == getLength() + i - 3 - 2 * secondDenominatorLength)
							stringBuffer.append(text);
						else
							stringBuffer.append(DollarFraction.getNumeratorIntChar(jTextField.getText().charAt(
									getLength() + i - 3 - 2 * secondDenominatorLength)));
					}
					try {
						Integer integer = Integer.parseInt(stringBuffer.toString());
						if (integer >= secondDenominator) {
							stringBuffer.replace(offset - getLength() + 4 + 2 * secondDenominatorLength,
									offset - getLength() + 5 + 2 * secondDenominatorLength, "0");
							integer = Integer.parseInt(stringBuffer.toString());
						}
						if (integer >= secondDenominator)
							throw new NumberFormatException();
						StringBuffer stringBuffer2 = new StringBuffer();
						for (int i = offset - getLength() + 3 + 2 * secondDenominatorLength; i < stringBuffer.length(); i++)
							stringBuffer2.append(Character.toString(DollarFraction.getNumeratorDigit(stringBuffer.charAt(i))));
						super.replace(offset, stringBuffer2.length(), stringBuffer2.toString(), attrs);
						if (stringBuffer2.length() > 1) {

							jTextField.setCaretPosition(jTextField.getCaretPosition() + 2 - stringBuffer2.length());
						}

					}
					catch (Exception e) {

						replace--;
						throw new BadLocationException("invalid tick", offset);
					}
				}
			}
			else {
				replace--;
				throw new BadLocationException("no digit", offset);
			}

		}

		reformat++;
		reformat();

		replace--;

	}

	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#remove(int, int)
	 */
	@Override
	public void remove(int offs, int len) throws BadLocationException {

		if (offs + len >= getLength() - 6 - 2 * secondDenominatorLength && replace == 0)
			throw new BadLocationException("invalid", offs);

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
					for (int i = caretPosition - 1; i >= 0; i--) {
						if (text.substring(i, i + 1).equals(","))
							caretPosition--;
					}
					text = text.replaceAll(",", "");
					int offset = text.lastIndexOf("-");

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

		Double value = null;

		try {
			value = DollarFraction.getDoubleValue(getText(0, getLength()));
		}
		catch (BadLocationException e1) {
			e1.printStackTrace();
		}

		secondDenominator = DollarFraction.getSecondDenominator(tickSize);
		if (secondDenominator != 0)
			secondDenominatorLength = Integer.toString(secondDenominator).length();
		else
			secondDenominatorLength = 0;
		try {
			dollarFraction = new DollarFraction(tickSize);
			super.remove(0, getLength());
			if (value != null)
				super.insertString(0, dollarFraction.getDollarPrice(value, leadingZeros), null);
			else {

				super.insertString(0, dollarFraction.getDollarPrice(0, leadingZeros), null);
			}
		}
		catch (Exception e) {
		}

	}

	/**
	 * Gets the dollar fraction.
	 *
	 * @return the dollar fraction
	 */
	public DollarFraction getDollarFraction() {

		return dollarFraction;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument#removeListener()
	 */
	@Override
	public void removeListener() {

		jTextField.removeCaretListener(caretListener);

	}

}
