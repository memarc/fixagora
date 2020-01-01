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
package net.sourceforge.fixagora.basis.client.view.component;

import java.awt.Color;
import java.math.RoundingMode;

import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

/**
 * The Class NumberSpinner.
 */
public class NumberSpinner extends AbstractSpinner {

	private static final long serialVersionUID = 1L;

	private double increment = 0.001;
	
	/**
	 * Instantiates a new number spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param background the background
	 */
	public NumberSpinner(final CellEditor cellEditor, Color background) {

		this(cellEditor, null, null, background, null);
	}
	
	/**
	 * Instantiates a new number spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param plusIcon the plus icon
	 * @param minusIcon the minus icon
	 * @param backgroundColor the background color
	 */
	public NumberSpinner(final CellEditor cellEditor, ImageIcon plusIcon, ImageIcon minusIcon, Color backgroundColor) {
		this(cellEditor, plusIcon, minusIcon, backgroundColor,null);
	}

	/**
	 * Instantiates a new number spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param plusIcon the plus icon
	 * @param minusIcon the minus icon
	 * @param backgroundColor the background color
	 * @param fractionalOffset the fractional offset
	 */
	public NumberSpinner(final CellEditor cellEditor, ImageIcon plusIcon, ImageIcon minusIcon, Color backgroundColor, Integer fractionalOffset) {

		super(cellEditor, plusIcon, minusIcon, new NumberTextField(cellEditor, backgroundColor, fractionalOffset));
		((NumberTextField) jTextField).setDefaultValue(0d);
		jTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				buttonCheck();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				buttonCheck();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				buttonCheck();

			}
		});
	}

	/**
	 * Sets the negative numbers red.
	 *
	 * @param negativeNumbersRed the new negative numbers red
	 */
	public void setNegativeNumbersRed(boolean negativeNumbersRed) {

		((NumberTextField) jTextField).setNegativeNumbersRed(negativeNumbersRed);
	}
	
	/**
	 * Sets the fractional.
	 *
	 * @param fractional the new fractional
	 */
	public void setFractional(boolean fractional) {
		
		if(fractional&&!DollarFraction.isValidTickSize(increment))
			setIncrement(0.0009765625);
		
		((NumberTextField) jTextField).setFractional(fractional);
		
	}

	/**
	 * Sets the leading zeroes.
	 *
	 * @param leadingZeroes the new leading zeroes
	 */
	public void setLeadingZeroes(int leadingZeroes) {

		((NumberTextField) jTextField).setLeadingZeroes(leadingZeroes);
	}

	/**
	 * Sets the decimal places.
	 *
	 * @param decimalPlaces the new decimal places
	 */
	public void setDecimalPlaces(int decimalPlaces) {

		((NumberTextField) jTextField).setDecimalPlaces(decimalPlaces);
	}

	/**
	 * Sets the min value.
	 *
	 * @param minValue the new min value
	 */
	public void setMinValue(Double minValue) {

		((NumberTextField) jTextField).setMinValue(minValue);
		buttonCheck();
	}

	/**
	 * Checks if is valid number.
	 *
	 * @return true, if is valid number
	 */
	public boolean isValidNumber() {

		return ((NumberTextField) jTextField).isValidNumber();
	}

	/**
	 * Sets the max value.
	 *
	 * @param maxValue the new max value
	 */
	public void setMaxValue(Double maxValue) {

		((NumberTextField) jTextField).setMaxValue(maxValue);
		buttonCheck();
	}

	/**
	 * Sets the tick size.
	 *
	 * @param tickSize the new tick size
	 */
	public void setTickSize(double tickSize) {

		((NumberTextField) jTextField).setTickSize(tickSize);
		buttonCheck();
	}

	/**
	 * Sets the increment.
	 *
	 * @param increment the new increment
	 */
	public void setIncrement(double increment) {

		this.increment = increment;
		buttonCheck();
	}

	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public double getIncrement() {

		return increment;
	}

	/**
	 * Sets the rounding mode.
	 *
	 * @param roundingMode the new rounding mode
	 */
	public void setRoundingMode(RoundingMode roundingMode) {

		((NumberTextField) jTextField).setRoundingMode(roundingMode);
	}

	/**
	 * Sets the thousands separator.
	 *
	 * @param thousandsSeparator the new thousands separator
	 */
	public void setThousandsSeparator(boolean thousandsSeparator) {

		((NumberTextField) jTextField).setThousandsSeparator(thousandsSeparator);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		ImageIcon plusIcon = null;
		ImageIcon minusIcon = null;

		switch (spreadSheetCellFormat.getSpinnerStyle()) {
			case 1:
				minusIcon = blueLeftIcon;
				plusIcon = blueRightIcon;
				break;
			case 2:
				minusIcon = blueDownIcon;
				plusIcon = blueUpIcon;
				break;
			case 3:
				minusIcon = redDownIcon;
				plusIcon = greenUpIcon;
				break;
			case 4:
				minusIcon = greenDownIcon;
				plusIcon = redUpIcon;
				break;
		}
				
		setPlusIcon(plusIcon);
		setMinusIcon(minusIcon);
		setNegativeNumbersRed(spreadSheetCellFormat.getNegativeNumbersRed());
		setLeadingZeroes(spreadSheetCellFormat.getLeadingZeroes());
		setDecimalPlaces(spreadSheetCellFormat.getDecimalPlaces());
		setMinValue(spreadSheetCellFormat.getMinimumValue());
		setMaxValue(spreadSheetCellFormat.getMaximumValue());
		setTickSize(spreadSheetCellFormat.getTickSize());
		
		if(spreadSheetCellFormat.getFractionalDisplay()!=null&&spreadSheetCellFormat.getFractionalDisplay()==true)
			setFractional(true);
		else
			setFractional(false);
		
		setThousandsSeparator(spreadSheetCellFormat.getThousandsSeparator());
		setIncrement(spreadSheetCellFormat.getIncrement());
		RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		switch (spreadSheetCellFormat.getPresentationRoundingMode()) {
			case CEILING:
				roundingMode = RoundingMode.CEILING;
				break;
			case DOWN:
				roundingMode = RoundingMode.DOWN;
				break;
			case FLOOR:
				roundingMode = RoundingMode.FLOOR;
				break;
			case HALF_DOWN:
				roundingMode = RoundingMode.HALF_DOWN;
				break;
			case HALF_EVEN:
				roundingMode = RoundingMode.HALF_EVEN;
				break;
			case HALF_UP:
				roundingMode = RoundingMode.HALF_UP;
				break;
			case UP:
				roundingMode = RoundingMode.UP;
				break;
		}
		setRoundingMode(roundingMode);
		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(), spreadSheetCellFormat.getBackgroundGreen(), spreadSheetCellFormat.getBackgroundBlue()));
		jTextField.setSpreadSheetCellFormat(spreadSheetCellFormat);
	}

	private void buttonCheck() {

		Double value = ((NumberTextField) jTextField).getValue();
		if (value != null && ((NumberTextField) jTextField).isValidNumber()) {
			spinner1MinusButton.setEnabled(true);
			spinner1PlusButton.setEnabled(true);
			Double minValue = ((NumberTextField) jTextField).getMinValue();
			
			if (minValue != null && value - increment < minValue)
				spinner1MinusButton.setEnabled(false);
			Double maxValue = ((NumberTextField) jTextField).getMaxValue();
			if (maxValue != null && value + increment > maxValue)
				spinner1PlusButton.setEnabled(false);
		}
		else {
			spinner1MinusButton.setEnabled(false);
			spinner1PlusButton.setEnabled(false);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Double getValue() {

		return ((NumberTextField) jTextField).getValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	public void setValue(final Object value) {

		if (value instanceof Double)
			((NumberTextField) jTextField).setValue((Double) value);
		else
			((NumberTextField) jTextField).setValue(0d);
		
		buttonCheck();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#increment()
	 */
	@Override
	protected void increment() {

		setValue(getValue() + increment);
		((NumberTextField) jTextField).postActionEvent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractSpinner#decrement()
	 */
	@Override
	protected void decrement() {
		
		setValue(getValue() - increment);
		((NumberTextField) jTextField).postActionEvent();
	}

}
