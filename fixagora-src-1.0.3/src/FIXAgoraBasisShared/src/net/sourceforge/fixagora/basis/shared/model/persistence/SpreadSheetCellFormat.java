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
package net.sourceforge.fixagora.basis.shared.model.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;

/**
 * The Class SpreadSheetCellFormat.
 */
@Entity
public class SpreadSheetCellFormat implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum SpreadSheetFormatType.
	 */
	public enum SpreadSheetFormatType {
		
		/** The clear. */
		CLEAR, 
 /** The plain. */
 PLAIN, 
 /** The plain time. */
 PLAIN_TIME, 
 /** The plain date. */
 PLAIN_DATE, 
 /** The number spinner. */
 NUMBER_SPINNER, 
 /** The date chooser. */
 DATE_CHOOSER, 
 /** The day spinner. */
 DAY_SPINNER, 
 /** The month spinner. */
 MONTH_SPINNER, 
 /** The hour spinner. */
 HOUR_SPINNER, 
 /** The minute spinner. */
 MINUTE_SPINNER, 
 /** The checkbox. */
 CHECKBOX, 
 /** The button. */
 BUTTON, 
 /** The predefined. */
 PREDEFINED, 
 /** The tmp date. */
 TMP_DATE, 
 /** The tmp time. */
 TMP_TIME, 
 /** The solver. */
 SOLVER
	};

	/**
	 * The Enum PresentationRoundingMode.
	 */
	public enum PresentationRoundingMode {
		
		/** The up. */
		UP, 
 /** The down. */
 DOWN, 
 /** The ceiling. */
 CEILING, 
 /** The floor. */
 FLOOR, 
 /** The half up. */
 HALF_UP, 
 /** The half down. */
 HALF_DOWN, 
 /** The half even. */
 HALF_EVEN
	};

	@Id
	@GeneratedValue
	private long id;

	@Column(unique = false, nullable = false)
	private Integer foregroundRed = 0;

	@Column(unique = false, nullable = false)
	private Integer foregroundGreen = 0;

	@Column(unique = false, nullable = false)
	private Integer foregroundBlue = 0;

	@Column(unique = false, nullable = false)
	private Integer backgroundRed = 255;

	@Column(unique = false, nullable = false)
	private Integer backgroundGreen = 243;

	@Column(unique = false, nullable = false)
	private Integer backgroundBlue = 204;

	@Column(unique = false, nullable = false)
	private Boolean gradientPaint = false;

	@Column(unique = false, nullable = false)
	private Boolean locked = false;

	@Column(unique = false, nullable = false)
	private Integer decimalPlaces = 0;

	@Column(unique = false, nullable = false)
	private Integer leadingZeroes = 0;

	@Column(unique = false, nullable = false)
	private Boolean negativeNumbersRed = false;

	@Column(unique = false, nullable = false)
	private Boolean thousandsSeparator = false;

	@Column(unique = false, nullable = true)
	private Boolean boldFont = false;
	
	@Column(unique = false, nullable = true)
	private Boolean fractionalDisplay = false;

	@Column(unique = false, nullable = false)
	private SpreadSheetFormatType spreadSheetFormatType = SpreadSheetFormatType.PLAIN;

	@Column(unique = false, nullable = false)
	private Double tickSize = 0.001;

	@Column(unique = false, nullable = false, name = "vincrement")
	private Double increment = 0.001;

	@Column(unique = false, nullable = true)
	private Double minimumValue = null;

	@Column(unique = false, nullable = true)
	private Double solverTolerance = null;

	@Column(unique = false, nullable = true)
	private String solverCell = null;

	@Column(unique = false, nullable = true)
	private Double maximumValue = null;

	@Column(unique = false, nullable = false)
	private Integer spinnerStyle = 0;

	@Column(unique = false, nullable = false)
	private Integer dateFormat = 0;

	@Column(unique = false, nullable = false)
	private Integer timeFormat = 0;

	@Column(unique = false, nullable = false)
	private String trueValue = "True";

	@Column(unique = false, nullable = false)
	private String falseValue = "False";

	/** The predefined strings. */
	@ElementCollection
	@JoinTable(name = "SSCF_PDS")
	protected List<String> predefinedStrings = new ArrayList<String>();

	@Column(unique = false, nullable = false)
	private Long spreadSheet = 0l;

	@Column(unique = false, nullable = false)
	private Integer cellRow = null;

	@Column(unique = false, nullable = false)
	private Integer cellColumn = null;

	@Column(unique = false, nullable = false)
	private PresentationRoundingMode presentationRoundingMode = PresentationRoundingMode.HALF_EVEN;
	
	
	/**
	 * Gets the fractional display.
	 *
	 * @return the fractional display
	 */
	public Boolean getFractionalDisplay() {
	
		return fractionalDisplay;
	}

	
	/**
	 * Sets the fractional display.
	 *
	 * @param fractionalDisplay the new fractional display
	 */
	public void setFractionalDisplay(Boolean fractionalDisplay) {
	
		this.fractionalDisplay = fractionalDisplay;
	}

	/**
	 * Gets the bold font.
	 *
	 * @return the bold font
	 */
	public Boolean getBoldFont() {

		return boldFont;
	}

	/**
	 * Sets the bold font.
	 *
	 * @param boldFont the new bold font
	 */
	public void setBoldFont(Boolean boldFont) {

		this.boldFont = boldFont;
	}

	/**
	 * Gets the solver tolerance.
	 *
	 * @return the solver tolerance
	 */
	public Double getSolverTolerance() {

		return solverTolerance;
	}

	/**
	 * Sets the solver tolerance.
	 *
	 * @param solverTolerance the new solver tolerance
	 */
	public void setSolverTolerance(Double solverTolerance) {

		this.solverTolerance = solverTolerance;
	}

	/**
	 * Gets the solver cell.
	 *
	 * @return the solver cell
	 */
	public String getSolverCell() {

		return solverCell;
	}

	/**
	 * Sets the solver cell.
	 *
	 * @param solverCell the new solver cell
	 */
	public void setSolverCell(String solverCell) {

		this.solverCell = solverCell;
	}

	/**
	 * Gets the presentation rounding mode.
	 *
	 * @return the presentation rounding mode
	 */
	public PresentationRoundingMode getPresentationRoundingMode() {

		return presentationRoundingMode;
	}

	/**
	 * Sets the presentation rounding mode.
	 *
	 * @param presentationRoundingMode the new presentation rounding mode
	 */
	public void setPresentationRoundingMode(PresentationRoundingMode presentationRoundingMode) {

		this.presentationRoundingMode = presentationRoundingMode;
	}

	/**
	 * Gets the cell row.
	 *
	 * @return the cell row
	 */
	public Integer getCellRow() {

		return cellRow;
	}

	/**
	 * Sets the cell row.
	 *
	 * @param cellRow the new cell row
	 */
	public void setCellRow(Integer cellRow) {

		this.cellRow = cellRow;
	}

	/**
	 * Gets the cell column.
	 *
	 * @return the cell column
	 */
	public Integer getCellColumn() {

		return cellColumn;
	}

	/**
	 * Sets the cell column.
	 *
	 * @param cellColumn the new cell column
	 */
	public void setCellColumn(Integer cellColumn) {

		this.cellColumn = cellColumn;
	}

	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public long getSpreadSheet() {

		return spreadSheet;
	}

	/**
	 * Sets the spread sheet.
	 *
	 * @param spreadSheet the new spread sheet
	 */
	public void setSpreadSheet(long spreadSheet) {

		this.spreadSheet = spreadSheet;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {

		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {

		this.id = id;
	}

	/**
	 * Gets the foreground red.
	 *
	 * @return the foreground red
	 */
	public Integer getForegroundRed() {

		return foregroundRed;
	}

	/**
	 * Sets the foreground red.
	 *
	 * @param foregroundRed the new foreground red
	 */
	public void setForegroundRed(Integer foregroundRed) {

		this.foregroundRed = foregroundRed;
	}

	/**
	 * Gets the foreground green.
	 *
	 * @return the foreground green
	 */
	public Integer getForegroundGreen() {

		return foregroundGreen;
	}

	/**
	 * Sets the foreground green.
	 *
	 * @param foregroundGreen the new foreground green
	 */
	public void setForegroundGreen(Integer foregroundGreen) {

		this.foregroundGreen = foregroundGreen;
	}

	/**
	 * Gets the foreground blue.
	 *
	 * @return the foreground blue
	 */
	public Integer getForegroundBlue() {

		return foregroundBlue;
	}

	/**
	 * Sets the foreground blue.
	 *
	 * @param foregroundBlue the new foreground blue
	 */
	public void setForegroundBlue(Integer foregroundBlue) {

		this.foregroundBlue = foregroundBlue;
	}

	/**
	 * Gets the background red.
	 *
	 * @return the background red
	 */
	public Integer getBackgroundRed() {

		return backgroundRed;
	}

	/**
	 * Sets the background red.
	 *
	 * @param backgroundRed the new background red
	 */
	public void setBackgroundRed(Integer backgroundRed) {

		this.backgroundRed = backgroundRed;
	}

	/**
	 * Gets the background green.
	 *
	 * @return the background green
	 */
	public Integer getBackgroundGreen() {

		return backgroundGreen;
	}

	/**
	 * Sets the background green.
	 *
	 * @param backgroundGreen the new background green
	 */
	public void setBackgroundGreen(Integer backgroundGreen) {

		this.backgroundGreen = backgroundGreen;
	}

	/**
	 * Gets the background blue.
	 *
	 * @return the background blue
	 */
	public Integer getBackgroundBlue() {

		return backgroundBlue;
	}

	/**
	 * Sets the background blue.
	 *
	 * @param backgroundBlue the new background blue
	 */
	public void setBackgroundBlue(Integer backgroundBlue) {

		this.backgroundBlue = backgroundBlue;
	}

	/**
	 * Gets the gradient paint.
	 *
	 * @return the gradient paint
	 */
	public Boolean getGradientPaint() {

		return gradientPaint;
	}

	/**
	 * Sets the gradient paint.
	 *
	 * @param gradientPaint the new gradient paint
	 */
	public void setGradientPaint(Boolean gradientPaint) {

		this.gradientPaint = gradientPaint;
	}

	/**
	 * Gets the locked.
	 *
	 * @return the locked
	 */
	public Boolean getLocked() {

		return locked;
	}

	/**
	 * Sets the locked.
	 *
	 * @param locked the new locked
	 */
	public void setLocked(Boolean locked) {

		this.locked = locked;
	}

	/**
	 * Gets the decimal places.
	 *
	 * @return the decimal places
	 */
	public Integer getDecimalPlaces() {

		return decimalPlaces;
	}

	/**
	 * Sets the decimal places.
	 *
	 * @param decimalPlaces the new decimal places
	 */
	public void setDecimalPlaces(Integer decimalPlaces) {

		this.decimalPlaces = decimalPlaces;
	}

	/**
	 * Gets the leading zeroes.
	 *
	 * @return the leading zeroes
	 */
	public Integer getLeadingZeroes() {

		return leadingZeroes;
	}

	/**
	 * Sets the leading zeroes.
	 *
	 * @param leadingZeroes the new leading zeroes
	 */
	public void setLeadingZeroes(Integer leadingZeroes) {

		this.leadingZeroes = leadingZeroes;
	}

	/**
	 * Gets the negative numbers red.
	 *
	 * @return the negative numbers red
	 */
	public Boolean getNegativeNumbersRed() {

		return negativeNumbersRed;
	}

	/**
	 * Sets the negative numbers red.
	 *
	 * @param negativeNumbersRed the new negative numbers red
	 */
	public void setNegativeNumbersRed(Boolean negativeNumbersRed) {

		this.negativeNumbersRed = negativeNumbersRed;
	}

	/**
	 * Gets the thousands separator.
	 *
	 * @return the thousands separator
	 */
	public Boolean getThousandsSeparator() {

		return thousandsSeparator;
	}

	/**
	 * Sets the thousands separator.
	 *
	 * @param thousandsSeparator the new thousands separator
	 */
	public void setThousandsSeparator(Boolean thousandsSeparator) {

		this.thousandsSeparator = thousandsSeparator;
	}

	/**
	 * Gets the spread sheet format type.
	 *
	 * @return the spread sheet format type
	 */
	public SpreadSheetFormatType getSpreadSheetFormatType() {

		return spreadSheetFormatType;
	}

	/**
	 * Sets the spread sheet format type.
	 *
	 * @param spreadSheetFormatType the new spread sheet format type
	 */
	public void setSpreadSheetFormatType(SpreadSheetFormatType spreadSheetFormatType) {

		this.spreadSheetFormatType = spreadSheetFormatType;
	}

	/**
	 * Gets the tick size.
	 *
	 * @return the tick size
	 */
	public Double getTickSize() {

		return tickSize;
	}

	/**
	 * Sets the tick size.
	 *
	 * @param tickSize the new tick size
	 */
	public void setTickSize(Double tickSize) {

		this.tickSize = tickSize;
	}

	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public Double getIncrement() {

		return increment;
	}

	/**
	 * Sets the increment.
	 *
	 * @param increment the new increment
	 */
	public void setIncrement(Double increment) {

		this.increment = increment;
	}

	/**
	 * Gets the minimum value.
	 *
	 * @return the minimum value
	 */
	public Double getMinimumValue() {

		return minimumValue;
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param minimumValue the new minimum value
	 */
	public void setMinimumValue(Double minimumValue) {

		this.minimumValue = minimumValue;
	}

	/**
	 * Gets the maximum value.
	 *
	 * @return the maximum value
	 */
	public Double getMaximumValue() {

		return maximumValue;
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param maximumValue the new maximum value
	 */
	public void setMaximumValue(Double maximumValue) {

		this.maximumValue = maximumValue;
	}

	/**
	 * Gets the spinner style.
	 *
	 * @return the spinner style
	 */
	public Integer getSpinnerStyle() {

		return spinnerStyle;
	}

	/**
	 * Sets the spinner style.
	 *
	 * @param spinnerStyle the new spinner style
	 */
	public void setSpinnerStyle(Integer spinnerStyle) {

		this.spinnerStyle = spinnerStyle;
	}

	/**
	 * Gets the date format.
	 *
	 * @return the date format
	 */
	public Integer getDateFormat() {

		return dateFormat;
	}

	/**
	 * Sets the date format.
	 *
	 * @param dateFormat the new date format
	 */
	public void setDateFormat(Integer dateFormat) {

		this.dateFormat = dateFormat;
	}

	/**
	 * Gets the time format.
	 *
	 * @return the time format
	 */
	public Integer getTimeFormat() {

		return timeFormat;
	}

	/**
	 * Sets the time format.
	 *
	 * @param timeFormat the new time format
	 */
	public void setTimeFormat(Integer timeFormat) {

		this.timeFormat = timeFormat;
	}

	/**
	 * Gets the true value.
	 *
	 * @return the true value
	 */
	public String getTrueValue() {

		return trueValue;
	}

	/**
	 * Sets the true value.
	 *
	 * @param trueValue the new true value
	 */
	public void setTrueValue(String trueValue) {

		this.trueValue = trueValue;
	}

	/**
	 * Gets the false value.
	 *
	 * @return the false value
	 */
	public String getFalseValue() {

		return falseValue;
	}

	/**
	 * Sets the false value.
	 *
	 * @param falseValue the new false value
	 */
	public void setFalseValue(String falseValue) {

		this.falseValue = falseValue;
	}

	/**
	 * Gets the predefined strings.
	 *
	 * @return the predefined strings
	 */
	public List<String> getPredefinedStrings() {

		return predefinedStrings;
	}

	/**
	 * Sets the predefined strings.
	 *
	 * @param predefinedStrings the new predefined strings
	 */
	public void setPredefinedStrings(List<String> predefinedStrings) {

		this.predefinedStrings = predefinedStrings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof SpreadSheetCellFormat) {
			SpreadSheetCellFormat spreadSheetCellFormat = (SpreadSheetCellFormat) obj;
			return spreadSheet == spreadSheetCellFormat.getSpreadSheet() && spreadSheetCellFormat.getCellRow() == getCellRow()
					&& spreadSheetCellFormat.getCellColumn() == getCellColumn();

		}
		return false;
	}

	/**
	 * Copy.
	 *
	 * @return the spread sheet cell format
	 */
	public SpreadSheetCellFormat copy() {

		SpreadSheetCellFormat spreadSheetCellFormat = new SpreadSheetCellFormat();

		spreadSheetCellFormat.setForegroundBlue(foregroundBlue);

		spreadSheetCellFormat.setForegroundGreen(foregroundGreen);

		spreadSheetCellFormat.setForegroundRed(foregroundRed);

		spreadSheetCellFormat.setBackgroundBlue(backgroundBlue);

		spreadSheetCellFormat.setBackgroundGreen(backgroundGreen);

		spreadSheetCellFormat.setBackgroundRed(backgroundRed);

		spreadSheetCellFormat.setGradientPaint(gradientPaint);

		spreadSheetCellFormat.setLocked(locked);

		spreadSheetCellFormat.setDecimalPlaces(decimalPlaces);

		spreadSheetCellFormat.setSolverCell(solverCell);
		
		spreadSheetCellFormat.setBoldFont(boldFont);
		
		spreadSheetCellFormat.setFractionalDisplay(fractionalDisplay);

		spreadSheetCellFormat.setSolverTolerance(solverTolerance);

		spreadSheetCellFormat.setLeadingZeroes(leadingZeroes);

		spreadSheetCellFormat.setNegativeNumbersRed(negativeNumbersRed);

		spreadSheetCellFormat.setThousandsSeparator(thousandsSeparator);

		spreadSheetCellFormat.setSpreadSheetFormatType(spreadSheetFormatType);

		spreadSheetCellFormat.setTickSize(tickSize);

		spreadSheetCellFormat.setIncrement(increment);

		spreadSheetCellFormat.setMinimumValue(minimumValue);

		spreadSheetCellFormat.setMaximumValue(maximumValue);

		spreadSheetCellFormat.setSpinnerStyle(spinnerStyle);

		spreadSheetCellFormat.setDateFormat(dateFormat);

		spreadSheetCellFormat.setTimeFormat(timeFormat);

		spreadSheetCellFormat.setTrueValue(trueValue);

		spreadSheetCellFormat.setFalseValue(falseValue);

		spreadSheetCellFormat.getPredefinedStrings().addAll(getPredefinedStrings());

		spreadSheetCellFormat.setPresentationRoundingMode(presentationRoundingMode);

		return spreadSheetCellFormat;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SpreadSheetCellFormat clone() {

		SpreadSheetCellFormat spreadSheetCellFormat = new SpreadSheetCellFormat();

		spreadSheetCellFormat.setForegroundBlue(foregroundBlue);

		spreadSheetCellFormat.setForegroundGreen(foregroundGreen);

		spreadSheetCellFormat.setForegroundRed(foregroundRed);

		spreadSheetCellFormat.setBackgroundBlue(backgroundBlue);

		spreadSheetCellFormat.setBackgroundGreen(backgroundGreen);

		spreadSheetCellFormat.setSolverCell(solverCell);

		spreadSheetCellFormat.setBoldFont(boldFont);
		
		spreadSheetCellFormat.setFractionalDisplay(fractionalDisplay);
		
		spreadSheetCellFormat.setSolverTolerance(solverTolerance);

		spreadSheetCellFormat.setBackgroundRed(backgroundRed);

		spreadSheetCellFormat.setGradientPaint(gradientPaint);

		spreadSheetCellFormat.setLocked(locked);

		spreadSheetCellFormat.setDecimalPlaces(decimalPlaces);

		spreadSheetCellFormat.setLeadingZeroes(leadingZeroes);

		spreadSheetCellFormat.setNegativeNumbersRed(negativeNumbersRed);

		spreadSheetCellFormat.setThousandsSeparator(thousandsSeparator);

		spreadSheetCellFormat.setSpreadSheetFormatType(spreadSheetFormatType);

		spreadSheetCellFormat.setTickSize(tickSize);

		spreadSheetCellFormat.setIncrement(increment);

		spreadSheetCellFormat.setMinimumValue(minimumValue);

		spreadSheetCellFormat.setMaximumValue(maximumValue);

		spreadSheetCellFormat.setSpinnerStyle(spinnerStyle);

		spreadSheetCellFormat.setDateFormat(dateFormat);

		spreadSheetCellFormat.setTimeFormat(timeFormat);

		spreadSheetCellFormat.setTrueValue(trueValue);

		spreadSheetCellFormat.setFalseValue(falseValue);

		spreadSheetCellFormat.getPredefinedStrings().addAll(getPredefinedStrings());

		spreadSheetCellFormat.setPresentationRoundingMode(presentationRoundingMode);

		spreadSheetCellFormat.setCellRow(getCellRow());

		spreadSheetCellFormat.setCellColumn(getCellColumn());

		spreadSheetCellFormat.setId(getId());

		spreadSheetCellFormat.setSpreadSheet(getSpreadSheet());

		return spreadSheetCellFormat;
	}

	/**
	 * Update.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public void update(SpreadSheetCellFormat spreadSheetCellFormat) {

		foregroundBlue = spreadSheetCellFormat.getForegroundBlue();

		foregroundGreen = spreadSheetCellFormat.getForegroundGreen();

		foregroundRed = spreadSheetCellFormat.getForegroundRed();

		backgroundBlue = spreadSheetCellFormat.getBackgroundBlue();

		backgroundGreen = spreadSheetCellFormat.getBackgroundGreen();

		backgroundRed = spreadSheetCellFormat.getBackgroundRed();

		gradientPaint = spreadSheetCellFormat.getGradientPaint();

		locked = spreadSheetCellFormat.getLocked();

		decimalPlaces = spreadSheetCellFormat.getDecimalPlaces();

		leadingZeroes = spreadSheetCellFormat.getLeadingZeroes();

		negativeNumbersRed = spreadSheetCellFormat.getNegativeNumbersRed();

		thousandsSeparator = spreadSheetCellFormat.getThousandsSeparator();
		
		boldFont = spreadSheetCellFormat.getBoldFont();
		
		fractionalDisplay = spreadSheetCellFormat.getFractionalDisplay();

		if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.CLEAR) {
			if (spreadSheetFormatType == SpreadSheetFormatType.PLAIN_DATE)
				spreadSheetFormatType = SpreadSheetFormatType.TMP_DATE;
			else if (spreadSheetFormatType == SpreadSheetFormatType.PLAIN_TIME)
				spreadSheetFormatType = SpreadSheetFormatType.TMP_TIME;
			else if (spreadSheetFormatType != SpreadSheetFormatType.TMP_TIME && spreadSheetFormatType != SpreadSheetFormatType.TMP_DATE)
				spreadSheetFormatType = SpreadSheetFormatType.CLEAR;
		}
		else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN) {
			if (spreadSheetFormatType == SpreadSheetFormatType.TMP_DATE)
				spreadSheetFormatType = SpreadSheetFormatType.PLAIN_DATE;
			else if (spreadSheetFormatType == SpreadSheetFormatType.TMP_TIME)
				spreadSheetFormatType = SpreadSheetFormatType.PLAIN_TIME;
			else if (spreadSheetFormatType != SpreadSheetFormatType.PLAIN_TIME && spreadSheetFormatType != SpreadSheetFormatType.PLAIN_DATE)
				spreadSheetFormatType = SpreadSheetFormatType.PLAIN;
		}
		else
			spreadSheetFormatType = spreadSheetCellFormat.getSpreadSheetFormatType();

		tickSize = spreadSheetCellFormat.getTickSize();

		increment = spreadSheetCellFormat.getIncrement();

		minimumValue = spreadSheetCellFormat.getMinimumValue();

		maximumValue = spreadSheetCellFormat.getMaximumValue();

		spinnerStyle = spreadSheetCellFormat.getSpinnerStyle();

		dateFormat = spreadSheetCellFormat.getDateFormat();

		timeFormat = spreadSheetCellFormat.getTimeFormat();

		trueValue = spreadSheetCellFormat.getTrueValue();

		falseValue = spreadSheetCellFormat.getFalseValue();
		
		solverCell = spreadSheetCellFormat.getSolverCell();
		
		solverTolerance = spreadSheetCellFormat.getSolverTolerance();

		predefinedStrings = new ArrayList<String>(spreadSheetCellFormat.getPredefinedStrings());

		presentationRoundingMode = spreadSheetCellFormat.getPresentationRoundingMode();

	}

	/**
	 * Checks if is same format.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 * @return true, if is same format
	 */
	public boolean isSameFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		if (foregroundBlue.intValue() != spreadSheetCellFormat.getForegroundBlue().intValue())
			return false;

		if (foregroundGreen.intValue() != spreadSheetCellFormat.getForegroundGreen().intValue())
			return false;

		if (foregroundRed.intValue() != spreadSheetCellFormat.getForegroundRed().intValue())
			return false;

		if (backgroundBlue.intValue() != spreadSheetCellFormat.getBackgroundBlue().intValue())
			return false;

		if (backgroundGreen.intValue() != spreadSheetCellFormat.getBackgroundGreen().intValue())
			return false;

		if (backgroundRed.intValue() != spreadSheetCellFormat.getBackgroundRed().intValue())
			return false;

		if (gradientPaint.booleanValue() != spreadSheetCellFormat.getGradientPaint().booleanValue())
			return false;

		if (locked.booleanValue() != spreadSheetCellFormat.getLocked().booleanValue())
			return false;

		if (decimalPlaces.intValue() != spreadSheetCellFormat.getDecimalPlaces().intValue())
			return false;

		if (leadingZeroes.intValue() != spreadSheetCellFormat.getLeadingZeroes().intValue())
			return false;

		if (negativeNumbersRed.booleanValue() != spreadSheetCellFormat.getNegativeNumbersRed().booleanValue())
			return false;

		if (thousandsSeparator.booleanValue() != spreadSheetCellFormat.getThousandsSeparator().booleanValue())
			return false;

		if (tickSize.doubleValue() != spreadSheetCellFormat.getTickSize().doubleValue())
			return false;

		if (increment.doubleValue() != spreadSheetCellFormat.getIncrement().doubleValue())
			return false;

		if (minimumValue != null && spreadSheetCellFormat.getMinimumValue() == null)
			return false;

		if (minimumValue == null && spreadSheetCellFormat.getMinimumValue() != null)
			return false;

		if (minimumValue != null && spreadSheetCellFormat.getMinimumValue() != null
				&& minimumValue.doubleValue() != spreadSheetCellFormat.getMinimumValue().doubleValue())
			return false;

		if (maximumValue != null && spreadSheetCellFormat.getMaximumValue() == null)
			return false;

		if (maximumValue == null && spreadSheetCellFormat.getMaximumValue() != null)
			return false;

		if (maximumValue != null && spreadSheetCellFormat.getMaximumValue() != null
				&& maximumValue.doubleValue() != spreadSheetCellFormat.getMaximumValue().doubleValue())
			return false;		
		
		if (solverCell != null && spreadSheetCellFormat.getSolverCell() == null)
			return false;

		if (solverCell == null && spreadSheetCellFormat.getSolverCell() != null)
			return false;

		if (solverCell != null && spreadSheetCellFormat.getSolverCell() != null
				&& !solverCell.equals(spreadSheetCellFormat.getSolverCell()))
			return false;
		
		if (solverTolerance != null && spreadSheetCellFormat.getSolverTolerance() == null)
			return false;

		if (solverTolerance == null && spreadSheetCellFormat.getSolverTolerance() != null)
			return false;

		if (solverTolerance != null && spreadSheetCellFormat.getSolverTolerance() != null
				&& solverTolerance.doubleValue() != spreadSheetCellFormat.getSolverTolerance().doubleValue())
			return false;	

		if (boldFont == null && spreadSheetCellFormat.getBoldFont() != null && spreadSheetCellFormat.getBoldFont() == true)
			return false;

		if (boldFont != null && spreadSheetCellFormat.getBoldFont() != null
				&& boldFont.booleanValue() != spreadSheetCellFormat.getBoldFont().booleanValue())
			return false;
		
		if (fractionalDisplay == null && spreadSheetCellFormat.getFractionalDisplay() != null && spreadSheetCellFormat.getFractionalDisplay() == true)
			return false;

		if (fractionalDisplay != null && spreadSheetCellFormat.getFractionalDisplay() != null
				&& fractionalDisplay.booleanValue() != spreadSheetCellFormat.getFractionalDisplay().booleanValue())
			return false;		

		if (spinnerStyle != spreadSheetCellFormat.getSpinnerStyle())
			return false;

		if (dateFormat != spreadSheetCellFormat.getDateFormat())
			return false;

		if (timeFormat != spreadSheetCellFormat.getTimeFormat())
			return false;

		if (!trueValue.equals(spreadSheetCellFormat.getTrueValue()))
			return false;

		if (!falseValue.equals(spreadSheetCellFormat.getFalseValue()))
			return false;

		if (!(getPredefinedStrings().containsAll(spreadSheetCellFormat.getPredefinedStrings()) && spreadSheetCellFormat.getPredefinedStrings().containsAll(
				getPredefinedStrings())))
			return false;

		if (presentationRoundingMode != spreadSheetCellFormat.getPresentationRoundingMode())
			return false;

		if ((spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
				|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE || spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME)
				&& (spreadSheetFormatType == SpreadSheetFormatType.PLAIN || spreadSheetFormatType == SpreadSheetFormatType.PLAIN_DATE || spreadSheetFormatType == SpreadSheetFormatType.PLAIN_TIME))
			return true;

		if (spreadSheetFormatType != spreadSheetCellFormat.getSpreadSheetFormatType())
			return false;

		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(spreadSheet);
		buffer.append("+");
		buffer.append(getCellRow());
		buffer.append("+");
		buffer.append(getCellColumn());
		return buffer.toString().hashCode();
	}

}
