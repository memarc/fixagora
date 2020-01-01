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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The Class SpreadSheetCondition.
 */
@Entity
public class SpreadSheetCondition implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The Enum ConditionType.
	 */
	public enum ConditionType {
		
		/** The formula is. */
		FORMULA_IS, 
 /** The cell value is. */
 CELL_VALUE_IS
	};
	
	/**
	 * The Enum IsType.
	 */
	public enum IsType {
		
		/** The equal to. */
		EQUAL_TO, 
 /** The greater than. */
 GREATER_THAN, 
 /** The less than or equal to. */
 LESS_THAN_OR_EQUAL_TO, 
 /** The greater than or equal to. */
 GREATER_THAN_OR_EQUAL_TO, 
 /** The not equal to. */
 NOT_EQUAL_TO, 
 /** The between. */
 BETWEEN, 
 /** The not between. */
 NOT_BETWEEN, 
 /** The less than. */
 LESS_THAN
	};
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	private Boolean fadeOut = false;
	
	@Column(unique = false, nullable = true)
	private String formula = null;
	
	@Column(unique = false, nullable = true)
	private Double value1 = null;
	
	@Column(unique = false, nullable = true)
	private Double value2 = null;
	
	@Column(unique = false, nullable = false)
	private ConditionType conditionType = ConditionType.FORMULA_IS;
	
	@Column(unique = false, nullable = false)
	private IsType isType = IsType.EQUAL_TO;
	
	

	
	
	/**
	 * Gets the condition type.
	 *
	 * @return the condition type
	 */
	public ConditionType getConditionType() {
	
		return conditionType;
	}



	
	/**
	 * Sets the condition type.
	 *
	 * @param conditionType the new condition type
	 */
	public void setConditionType(ConditionType conditionType) {
	
		this.conditionType = conditionType;
	}



	
	/**
	 * Gets the checks if is type.
	 *
	 * @return the checks if is type
	 */
	public IsType getIsType() {
	
		return isType;
	}



	
	/**
	 * Sets the checks if is type.
	 *
	 * @param isType the new checks if is type
	 */
	public void setIsType(IsType isType) {
	
		this.isType = isType;
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
	 * Gets the fade out.
	 *
	 * @return the fade out
	 */
	public Boolean getFadeOut() {
	
		return fadeOut;
	}


	
	/**
	 * Sets the fade out.
	 *
	 * @param fadeOut the new fade out
	 */
	public void setFadeOut(Boolean fadeOut) {
	
		this.fadeOut = fadeOut;
	}


	
	/**
	 * Gets the formula.
	 *
	 * @return the formula
	 */
	public String getFormula() {
	
		return formula;
	}


	
	/**
	 * Sets the formula.
	 *
	 * @param formula the new formula
	 */
	public void setFormula(String formula) {
	
		this.formula = formula;
	}


	
	/**
	 * Gets the value1.
	 *
	 * @return the value1
	 */
	public Double getValue1() {
	
		return value1;
	}


	
	/**
	 * Sets the value1.
	 *
	 * @param value1 the new value1
	 */
	public void setValue1(Double value1) {
	
		this.value1 = value1;
	}


	
	/**
	 * Gets the value2.
	 *
	 * @return the value2
	 */
	public Double getValue2() {
	
		return value2;
	}


	
	/**
	 * Sets the value2.
	 *
	 * @param value2 the new value2
	 */
	public void setValue2(Double value2) {
	
		this.value2 = value2;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SpreadSheetCondition clone()
	{
		SpreadSheetCondition spreadSheetCondition = new SpreadSheetCondition();
		
		spreadSheetCondition.setForegroundBlue(foregroundBlue);
		
		spreadSheetCondition.setForegroundGreen(foregroundGreen);
		
		spreadSheetCondition.setForegroundRed(foregroundRed);
		
		spreadSheetCondition.setBackgroundBlue(backgroundBlue);
		
		spreadSheetCondition.setBackgroundGreen(backgroundGreen);
		
		spreadSheetCondition.setBackgroundRed(backgroundRed);
		
		spreadSheetCondition.setConditionType(conditionType);
		
		spreadSheetCondition.setIsType(isType);
		
		spreadSheetCondition.setFormula(formula);
		
		spreadSheetCondition.setValue1(value1);
		
		spreadSheetCondition.setValue2(value2);
		
		spreadSheetCondition.setFadeOut(fadeOut);
		
		return spreadSheetCondition;
	}


	/**
	 * Update.
	 *
	 * @param spreadSheetCondition the spread sheet condition
	 */
	public void update(SpreadSheetCondition spreadSheetCondition) {

		foregroundBlue = spreadSheetCondition.getForegroundBlue();
		
		foregroundGreen = spreadSheetCondition.getForegroundGreen();
		
		foregroundRed = spreadSheetCondition.getForegroundRed();
		
		backgroundBlue = spreadSheetCondition.getBackgroundBlue();
		
		backgroundGreen = spreadSheetCondition.getBackgroundGreen();
		
		backgroundRed = spreadSheetCondition.getBackgroundRed();
		
		conditionType = spreadSheetCondition.getConditionType();
		
		isType = spreadSheetCondition.getIsType();
		
		formula = spreadSheetCondition.getFormula();
		
		value1 = spreadSheetCondition.getValue1();
		
		value2 = spreadSheetCondition.getValue2();
		
		fadeOut = spreadSheetCondition.getFadeOut();
		
	}
	
	/**
	 * Checks if is same format.
	 *
	 * @param spreadSheetCondition the spread sheet condition
	 * @return true, if is same format
	 */
	public boolean isSameFormat(SpreadSheetCondition spreadSheetCondition) {
		
		if(spreadSheetCondition==null)
			return false;

		if(foregroundBlue.intValue() != spreadSheetCondition.getForegroundBlue().intValue())
			return false;
		
		if(foregroundGreen.intValue() != spreadSheetCondition.getForegroundGreen().intValue())
			return false;
		
		if(foregroundRed.intValue() != spreadSheetCondition.getForegroundRed().intValue())
			return false;
		
		if(backgroundBlue.intValue() != spreadSheetCondition.getBackgroundBlue().intValue())
			return false;
		
		if(backgroundGreen.intValue() != spreadSheetCondition.getBackgroundGreen().intValue())
			return false;
		
		if(backgroundRed.intValue() != spreadSheetCondition.getBackgroundRed().intValue())
			return false;
		
		if(conditionType != spreadSheetCondition.getConditionType())
			return false;
		
		if(isType != spreadSheetCondition.getIsType())
			return false;
		
		if(formula==null&&spreadSheetCondition.getFormula()!=null)
			return false;
		
		if(formula!=null&&spreadSheetCondition.getFormula()==null)
			return false;
		
		if(formula!=null&&spreadSheetCondition.getFormula()!=null&&!formula.equals(spreadSheetCondition.getFormula()))
			return false;
		
		if(value1==null&&spreadSheetCondition.getValue1()!=null)
			return false;
		
		if(value1!=null&&spreadSheetCondition.getValue1()==null)
			return false;
		
		if(value1!=null&&spreadSheetCondition.getValue1()!=null&& value1.doubleValue() != spreadSheetCondition.getValue1().doubleValue())
			return false;
		
		if(value2==null&&spreadSheetCondition.getValue2()!=null)
			return false;
		
		if(value2!=null&&spreadSheetCondition.getValue2()==null)
			return false;
		
		if(value2!=null&&spreadSheetCondition.getValue2()!=null&& value2.doubleValue() != spreadSheetCondition.getValue2().doubleValue())
			return false;
		
		if(fadeOut.booleanValue() != spreadSheetCondition.getFadeOut().booleanValue())
			return false;
		
		return true;
	}
	
}
