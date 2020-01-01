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
package net.sourceforge.fixagora.basis.client.model.message;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap;

/**
 * The Class AbstractFIXElement.
 */
public abstract class AbstractFIXElement {

	/**
	 * The Enum FieldType.
	 */
	public enum FieldType {

		/** The conditionally. */
		CONDITIONALLY, 
 /** The optional. */
		OPTIONAL, 
 /** The required. */
		REQUIRED
	};

	private FieldType fieldType = null;

	/** The fix fields. */
	protected List<AbstractFIXElement> fixFields = null;

	private FontProperties fontProperties = null;

	private String name = null;

	private int number = 0;

	/**
	 * Instantiates a new abstract fix element.
	 *
	 * @param name the name
	 * @param number the number
	 * @param fieldType the field type
	 * @param fixFields the fix fields
	 */
	public AbstractFIXElement(final String name, final int number, final FieldType fieldType, final List<AbstractFIXElement> fixFields) {

		super();

		this.name = name;
		this.number = number;
		this.fieldType = fieldType;
		this.fixFields = fixFields;
	}

	/**
	 * At least one set.
	 *
	 * @param fieldMap the field map
	 * @return true, if successful
	 */
	public boolean atLeastOneSet(final SpreadSheetFIXFieldMap fieldMap) {

		boolean atLeastOneSet = false;

		for (final AbstractFIXElement abstractFIXElement2 : getAbstractFIXElements()) {

			if (abstractFIXElement2 instanceof FIXGroup)
				if (fieldMap.hasGroup(abstractFIXElement2.getNumber()) && fieldMap.getGroupCount(abstractFIXElement2.getNumber()) > 0)
					atLeastOneSet = true;

			if (abstractFIXElement2 instanceof FIXField)
				if (fieldMap.isSetField(abstractFIXElement2.getNumber()))
					atLeastOneSet = true;

			if (abstractFIXElement2 instanceof FIXComponent)
				if (abstractFIXElement2.atLeastOneSet(fieldMap))
					atLeastOneSet = true;
		}

		return atLeastOneSet;
	}

	/**
	 * Gets the abstract fix elements.
	 *
	 * @return the abstract fix elements
	 */
	public List<AbstractFIXElement> getAbstractFIXElements() {

		return fixFields;
	}

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	public abstract int getDepth();

	/**
	 * Gets the field type.
	 *
	 * @return the field type
	 */
	public FieldType getFieldType() {

		return fieldType;
	}

	/**
	 * Gets the font properties.
	 *
	 * @return the font properties
	 */
	public FontProperties getFontProperties() {

		return fontProperties;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public int getNumber() {

		return number;
	}

	/**
	 * Sets the font properties.
	 *
	 * @param fontProperties the new font properties
	 */
	public void setFontProperties(final FontProperties fontProperties) {

		this.fontProperties = fontProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(name);
		stringBuffer.append(" (");
		stringBuffer.append(number);
		stringBuffer.append(")");

		return stringBuffer.toString();
	}

	/**
	 * Gets the field order list.
	 *
	 * @return the field order list
	 */
	List<Integer> getFieldOrderList() {

		ArrayList<Integer> fieldOrder = new ArrayList<Integer>();
		
		for (AbstractFIXElement abstractFIXElement : getAbstractFIXElements()) {
			
			if (abstractFIXElement instanceof FIXField)
				fieldOrder.add(abstractFIXElement.getNumber());
			
			if (abstractFIXElement instanceof FIXGroup)
				fieldOrder.add(abstractFIXElement.getNumber());
	
			
			if (abstractFIXElement instanceof FIXComponent)
				fieldOrder.addAll(abstractFIXElement.getFieldOrderList());
		}
		
		return fieldOrder;
	}

}
