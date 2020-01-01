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
package net.sourceforge.fixagora.sap.client.model.editor;

import java.awt.Color;


/**
 * The Class SAPTradeCaptureCellValue.
 */
public class SAPTradeCaptureCellValue {
	
	/** The color. */
	Color color = Color.BLACK;
	
	private String value = null;
	
	private String additionalInfo = null;

	
	
	/**
	 * Instantiates a new sAP trade capture cell value.
	 *
	 * @param value the value
	 * @param additionalInfo the additional info
	 */
	public SAPTradeCaptureCellValue(String value, String additionalInfo) {

		super();
		this.value = value;
		this.additionalInfo = additionalInfo;
	}

	

	/**
	 * Instantiates a new sAP trade capture cell value.
	 *
	 * @param value the value
	 * @param additionalInfo the additional info
	 * @param color the color
	 */
	public SAPTradeCaptureCellValue(String value, String additionalInfo, Color color) {

		super();
		this.value = value;
		this.additionalInfo = additionalInfo;
		this.color = color;
	}



	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
	
		return color;
	}

	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
	
		return value;
	}

	
	/**
	 * Gets the additional info.
	 *
	 * @return the additional info
	 */
	public String getAdditionalInfo() {
	
		return additionalInfo;
	}
	
	

}
