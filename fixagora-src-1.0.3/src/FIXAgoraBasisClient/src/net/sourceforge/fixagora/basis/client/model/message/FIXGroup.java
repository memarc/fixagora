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

import java.util.List;

/**
 * The Class FIXGroup.
 */
public class FIXGroup extends AbstractFIXElement {

	/**
	 * Instantiates a new fIX group.
	 *
	 * @param name the name
	 * @param number the number
	 * @param fieldType the field type
	 * @param fixFields the fix fields
	 */
	public FIXGroup(final String name, final int number, final FieldType fieldType, final List<AbstractFIXElement> fixFields) {

		super(name, number, fieldType, fixFields);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixpusher.model.message.AbstractFIXElement#getDepth()
	 */
	@Override
	public int getDepth() {

		int max = 0;
		
		for (final AbstractFIXElement abstractFIXElement : fixFields)
			if (abstractFIXElement.getDepth() > max)
				max = abstractFIXElement.getDepth();
		
		return max + 2;
	}
	
	/**
	 * Gets the field order.
	 *
	 * @return the field order
	 */
	public int[] getFieldOrder()
	{
		
		List<Integer> fieldOrderList = getFieldOrderList();
		
		int[] fieldOrder = new int[fieldOrderList.size()];
		
		for(int i=0;i<fieldOrderList.size();i++)
		{
			
			fieldOrder[i] = fieldOrderList.get(i);
		}
		
		return fieldOrder;
	}

}
