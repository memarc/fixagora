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
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.CellEditor;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.document.AbstractNumberDocument;
import net.sourceforge.fixagora.basis.client.view.document.FractionalTextFieldDocument;
import net.sourceforge.fixagora.basis.client.view.document.NumberTextFieldDocument;
import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

/**
 * The Class NumberTextField.
 */
public class NumberTextField extends AbstractTextField {

	private static final long serialVersionUID = 1L;

	private boolean negativeNumbersRed = false;

	private int leadingZeros = 0;

	private int decimalPlaces = 0;

	private Double minValue = null;

	private Double maxValue = null;

	private Double value = null;

	private double tickSize = 0;

	private boolean thousandsSeparator = false;

	private RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	private AbstractNumberDocument document = null;

	private Color foreground = Color.BLACK;

	private Double defaultValue = null;

	private DecimalFormat decimalFormat = new DecimalFormat("###.###");

	private boolean fractional = false;
	
	private Color blue = new Color(204, 216, 255);
	
	private CellEditor cellEditor = null;
	
	private int fractionOffset = 0;
	
	private int offset = 0;

	private DocumentListener documentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {

			changedUpdate(e);

		}

		@Override
		public void insertUpdate(DocumentEvent e) {

			changedUpdate(e);

		}

		@Override
		public void changedUpdate(DocumentEvent e) {

			fieldCheck();
		}
	};

	private boolean standalone = false;
	
	/**
	 * Instantiates a new number text field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 * @param standalone the standalone
	 */
	public NumberTextField(final CellEditor cellEditor, Color backgroundColor, boolean standalone) {
		this(cellEditor,backgroundColor,standalone,null);
	}
	
	/**
	 * Instantiates a new number text field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 * @param standalone the standalone
	 * @param fractionalOffset the fractional offset
	 */
	public NumberTextField(final CellEditor cellEditor, Color backgroundColor, boolean standalone, Integer fractionalOffset) {
		super(backgroundColor);
		this.standalone  = standalone;
		this.cellEditor = cellEditor;
		if(fractionalOffset!=null)
			this.fractionOffset = fractionalOffset;
		else
			this.fractionOffset = 0;
		
		document = new NumberTextFieldDocument(jTextField);
		jTextField.setDocument(document);
		jTextField.setHorizontalAlignment(JTextField.RIGHT);

		document.addDocumentListener(documentListener);
	}

	/**
	 * Instantiates a new number text field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public NumberTextField(final CellEditor cellEditor, Color backgroundColor) {

		this(cellEditor, backgroundColor,false, null);

	}
	
	/**
	 * Instantiates a new number text field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 * @param fractionalOffset the fractional offset
	 */
	public NumberTextField(final CellEditor cellEditor, Color backgroundColor, Integer fractionalOffset) {

		this(cellEditor, backgroundColor,false, fractionalOffset);

	}

	private void fieldCheck() {
		
		Insets insets = new Insets(0, 0, 0, 0);
		
		if(standalone)
			insets = new Insets(0,0,1,1);
		
		Insets fractionInsets = new Insets(0,0,0,0);
		
		if(fractionOffset!=0)
			fractionInsets = new Insets(0,0,0,fractionOffset-offset);
		
		if (!isValidNumber())
			setBorder(new CompoundBorder(new MatteBorder(insets, Color.RED), new MatteBorder( new Insets(1,1,1,1), Color.RED)));
		else
			setBorder(new CompoundBorder(new MatteBorder(insets, blue.darker()),new CompoundBorder(new MatteBorder(new Insets(1,1,1,1), NumberTextField.this.highlightColor),new EmptyBorder(fractionInsets))));

		Double value = getValue();
		
		if (isValidNumber()&& value != null && value.doubleValue()<0 && negativeNumbersRed)
			jTextField.setForeground(Color.RED);
		else
			jTextField.setForeground(foreground);
	}

	/**
	 * Checks if is valid number.
	 *
	 * @return true, if is valid number
	 */
	public boolean isValidNumber() {

		if (fractional) {
			
			try {
				
				Double doubleValue = DollarFraction.getDoubleValue(jTextField.getText().trim());
				
				if(doubleValue==null)
					return false;
				else if (minValue != null && doubleValue < minValue)
					return false;
				else if (maxValue != null && doubleValue > maxValue)
					return false;
				else
					return true;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		else {
			if (jTextField.getText().trim().length() == 0) {
				if (defaultValue != null)
					return false;
			}
			else {
				try {
					BigDecimal bigDecimal = new BigDecimal(jTextField.getText().replace(",", ""));
					if (tickSize > 0) {
						BigDecimal tickSizeBigDecimal = new BigDecimal(Double.toString(tickSize));
						bigDecimal = bigDecimal.divide(tickSizeBigDecimal).setScale(0, roundingMode).multiply(tickSizeBigDecimal);
					}
					else
						bigDecimal = bigDecimal.setScale(decimalPlaces, roundingMode);
					Number value = decimalFormat.parse(jTextField.getText().replace(",", ""));
					if (value.doubleValue() != bigDecimal.doubleValue())
						return false;
					else if (minValue != null && value.doubleValue() < minValue)
						return false;
					else if (maxValue != null && value.doubleValue() > maxValue)
						return false;
					else
						return true;
				}
				catch (Exception e1) {
				}
			}
		}
		return false;
	}

	private void updateDecimalFormat() {

		StringBuffer stringBuffer = new StringBuffer();
		if (decimalPlaces > 0) {
			stringBuffer.append(".");
			for (int i = 0; i < decimalPlaces; i++)
				stringBuffer.append("0");
		}
		for (int i = 0; i < leadingZeros; i++) {
			stringBuffer.insert(0, "0");
			if (thousandsSeparator && (i + 1) % 3 == 0)
				stringBuffer.insert(0, ",");
		}
		for (int i = leadingZeros; i < 7; i++) {
			stringBuffer.insert(0, "#");
			if (thousandsSeparator && (i + 1) % 3 == 0)
				stringBuffer.insert(0, ",");
		}
		decimalFormat = new DecimalFormat(stringBuffer.toString());
	}

	/**
	 * Checks if is negative numbers red.
	 *
	 * @return true, if is negative numbers red
	 */
	public boolean isNegativeNumbersRed() {

		return negativeNumbersRed;
	}

	/**
	 * Sets the negative numbers red.
	 *
	 * @param negativeNumbersRed the new negative numbers red
	 */
	public void setNegativeNumbersRed(boolean negativeNumbersRed) {

		this.negativeNumbersRed = negativeNumbersRed;
	}

	/**
	 * Gets the leading zeroes.
	 *
	 * @return the leading zeroes
	 */
	public int getLeadingZeroes() {

		return leadingZeros;
	}

	/**
	 * Sets the leading zeroes.
	 *
	 * @param leadingZeros the new leading zeroes
	 */
	public void setLeadingZeroes(int leadingZeros) {

		if(cellEditor!=null&&leadingZeros==0)
			this.leadingZeros = 1;
		else
			this.leadingZeros = leadingZeros;
		document.setLeadingZeros(leadingZeros);
		updateDecimalFormat();
	}

	/**
	 * Gets the decimal places.
	 *
	 * @return the decimal places
	 */
	public int getDecimalPlaces() {

		return decimalPlaces;
	}

	/**
	 * Sets the decimal places.
	 *
	 * @param decimalPlaces the new decimal places
	 */
	public void setDecimalPlaces(int decimalPlaces) {

		this.decimalPlaces = decimalPlaces;
		document.setDecimalPlaces(decimalPlaces);
		updateDecimalFormat();
	}

	/**
	 * Gets the min value.
	 *
	 * @return the min value
	 */
	public Double getMinValue() {

		return minValue;
	}

	/**
	 * Sets the min value.
	 *
	 * @param minValue the new min value
	 */
	public void setMinValue(Double minValue) {

		this.minValue = minValue;
		if (minValue != null && defaultValue != null) {
			if (minValue > defaultValue)
				defaultValue = minValue;
		}
		document.setMinValue(minValue);
		fieldCheck();
	}

	/**
	 * Gets the max value.
	 *
	 * @return the max value
	 */
	public Double getMaxValue() {

		return maxValue;
	}

	/**
	 * Sets the max value.
	 *
	 * @param maxValue the new max value
	 */
	public void setMaxValue(Double maxValue) {

		this.maxValue = maxValue;
		if (maxValue != null && defaultValue != null) {
			if (maxValue < defaultValue)
				defaultValue = maxValue;
		}
		fieldCheck();
	}
	
	

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	@Override
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		super.setSpreadSheetCellFormat(spreadSheetCellFormat);
		setLeadingZeroes(spreadSheetCellFormat.getLeadingZeroes());
		setDecimalPlaces(spreadSheetCellFormat.getDecimalPlaces());
		setMinValue(spreadSheetCellFormat.getMinimumValue());
		setMaxValue(spreadSheetCellFormat.getMaximumValue());
		setTickSize(spreadSheetCellFormat.getTickSize());
		setNegativeNumbersRed(spreadSheetCellFormat.getNegativeNumbersRed());
		setThousandsSeparator(spreadSheetCellFormat.getThousandsSeparator());
		
		if(spreadSheetCellFormat.getFractionalDisplay()!=null&&spreadSheetCellFormat.getFractionalDisplay()==true)
			setFractional(true);
		else
			setFractional(false);
		
		
		
	}

	/**
	 * Gets the tick size.
	 *
	 * @return the tick size
	 */
	public double getTickSize() {

		return tickSize;
	}

	/**
	 * Sets the tick size.
	 *
	 * @param tickSize the new tick size
	 */
	public void setTickSize(double tickSize) {

		this.tickSize = tickSize;
		document.setTickSize(tickSize);
		fieldCheck();
	}

	/**
	 * Gets the rounding mode.
	 *
	 * @return the rounding mode
	 */
	public RoundingMode getRoundingMode() {

		return roundingMode;
	}

	/**
	 * Sets the rounding mode.
	 *
	 * @param roundingMode the new rounding mode
	 */
	public void setRoundingMode(RoundingMode roundingMode) {

		this.roundingMode = roundingMode;
	}

	/**
	 * Sets the foreground color.
	 *
	 * @param fg the new foreground color
	 */
	public void setForegroundColor(Color fg) {

		this.foreground = fg;
		jTextField.setForeground(fg);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#setForeground(java.awt.Color)
	 */
	@Override
	public void setForeground(Color color) {

		this.foreground = color;
		if (jTextField != null) {
			String text = jTextField.getText();

			if (text.startsWith("-") && negativeNumbersRed)
				jTextField.setForeground(Color.RED);
			else
				jTextField.setForeground(foreground);
		}
	}

	/**
	 * Checks if is thousands separator.
	 *
	 * @return true, if is thousands separator
	 */
	public boolean isThousandsSeparator() {

		return thousandsSeparator;
	}

	/**
	 * Sets the thousands separator.
	 *
	 * @param thousandsSeparator the new thousands separator
	 */
	public void setThousandsSeparator(boolean thousandsSeparator) {

		this.thousandsSeparator = thousandsSeparator;
		document.setThousandsSeparator(thousandsSeparator);
		updateDecimalFormat();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		
		try {
			if (value instanceof Double) {
				
				String stringValue = null;
				if(fractional)
				{
					try {
						DollarFraction dollarFraction = ((FractionalTextFieldDocument)document).getDollarFraction();
						
						if(cellEditor==null)
						{
							stringValue = dollarFraction.getDollarPrice((Double)value, leadingZeros, true);
							offset = jTextField.getFontMetrics(jTextField.getFont()).stringWidth(dollarFraction.getDollarPriceFraction((Double)value, true));
									
						}
						else
						{
							stringValue = dollarFraction.getDollarPrice((Double)value, leadingZeros);
							offset = jTextField.getFontMetrics(jTextField.getFont()).stringWidth(dollarFraction.getDollarPriceFraction((Double)value));
						}
						
						
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(stringValue==null)
				{					
					offset = fractionOffset;
					
					if(fractional)
						setFractional(false);

					stringValue = decimalFormat.format(value);
				}
				if (!jTextField.getText().trim().equals(stringValue))
					jTextField.setText(stringValue);
			}
			else if (defaultValue != null)
			{
				if(!fractional)
				{
					offset = fractionOffset;
					jTextField.setText(decimalFormat.format(defaultValue));
				}
				else
				{
					DollarFraction dollarFraction = ((FractionalTextFieldDocument)document).getDollarFraction();			
					try {
						offset = jTextField.getFontMetrics(jTextField.getFont()).stringWidth(dollarFraction.getDollarPriceFraction((Double)value));
						jTextField.setText(dollarFraction.getDollarPrice((Double)defaultValue, leadingZeros));
					}
					catch (Exception e) {
						offset = fractionOffset;
						setFractional(false);
					}
				}
			}
			else
				jTextField.setText("");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.AbstractTextField#getValue()
	 */
	public Double getValue() {

		if (!isValidNumber())
			return value;
		else {
			try {
				if(fractional)
				{
					Double doubleValue = DollarFraction.getDoubleValue(jTextField.getText().trim());
					
					if(doubleValue==null)
						return value;
					this.value = doubleValue;
					
					return this.value;
				}
				
				this.value = decimalFormat.parse(jTextField.getText().replace(",", "")).doubleValue();
				return this.value;
			}
			catch (ParseException e) {
				return value;
			}
		}
	}

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public Double getDefaultValue() {

		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the new default value
	 */
	public void setDefaultValue(Double defaultValue) {

		if (minValue != null && defaultValue != null) {
			if (minValue > defaultValue)
				defaultValue = minValue;
		}
		if (maxValue != null && defaultValue != null) {
			if (maxValue < defaultValue)
				defaultValue = maxValue;
		}
		this.defaultValue = defaultValue;
		if (this.value == null)
			value = defaultValue;
	}

	/**
	 * Sets the fractional.
	 *
	 * @param fractional the new fractional
	 */
	public void setFractional(boolean fractional) {

		if (this.fractional != fractional) {

			Double value = getValue();

			this.fractional = fractional;

			if(document!=null)
				document.removeListener();
			
			if (fractional) {
				
				document = new FractionalTextFieldDocument(jTextField);

				document.setTickSize(tickSize);
				
				document.setThousandsSeparator(thousandsSeparator);
				
				document.setLeadingZeros(leadingZeros);
				
				jTextField.setDocument(document);

				document.addDocumentListener(documentListener);
				
				setValue(value);
				
			}
			else {
				document = new NumberTextFieldDocument(jTextField);
				document.setThousandsSeparator(thousandsSeparator);
				jTextField.setDocument(document);
				document.addDocumentListener(documentListener);
				setValue(value);
			}

		}

	}


}
