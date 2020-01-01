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
package net.sourceforge.agora.simulator.model;

import java.awt.Color;


/**
 * The Class BlotterText.
 */
public class BlotterText {

	private String text = null;
	private Color color = Color.BLACK;
	
	/**
	 * Instantiates a new blotter text.
	 *
	 * @param text the text
	 * @param color the color
	 */
	public BlotterText(String text, Color color) {

		super();
		this.text = text;
		this.color = color;
	}
	
	/**
	 * Instantiates a new blotter text.
	 *
	 * @param text the text
	 */
	public BlotterText(String text) {

		super();
		this.text = text;
	}
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
	
		return text;
	}
	
	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public Color getColor() {
	
		return color;
	}
	
	
	
}
