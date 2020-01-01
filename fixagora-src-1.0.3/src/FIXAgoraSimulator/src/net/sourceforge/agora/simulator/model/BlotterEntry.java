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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class BlotterEntry.
 */
public class BlotterEntry {

	/** The blotter texts. */
	List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

	/**
	 * The Enum LevelIcon.
	 */
	public enum LevelIcon {
		
		/** The blank. */
		BLANK, 
 /** The info. */
 INFO, 
 /** The sucess. */
 SUCESS, 
 /** The warning. */
 WARNING, 
 /** The error. */
 ERROR, 
 /** The message. */
 MESSAGE, 
 /** The run. */
 RUN
	};

	/** The level icon. */
	LevelIcon levelIcon = LevelIcon.BLANK;

	private int offset = 0;
	
	private String counterparty = null;

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param blotterTexts the blotter texts
	 * @param levelIcon the level icon
	 */
	public BlotterEntry(String counterparty, List<BlotterText> blotterTexts, LevelIcon levelIcon) {

		super();
		this.blotterTexts = blotterTexts;
		this.levelIcon = levelIcon;
		this.counterparty = counterparty;
	}

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param blotterText the blotter text
	 * @param levelIcon the level icon
	 */
	public BlotterEntry(String counterparty, BlotterText blotterText, LevelIcon levelIcon) {

		super();
		this.blotterTexts = new ArrayList<BlotterText>();
		blotterTexts.add(blotterText);
		this.levelIcon = levelIcon;
		this.counterparty = counterparty;
	}

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param text the text
	 * @param levelIcon the level icon
	 */
	public BlotterEntry(String counterparty, String text, LevelIcon levelIcon) {

		super();
		this.blotterTexts = new ArrayList<BlotterText>();
		blotterTexts.add(new BlotterText(text));
		this.levelIcon = levelIcon;
		this.counterparty = counterparty;
	}

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param blotterText the blotter text
	 */
	public BlotterEntry(String counterparty, BlotterText blotterText) {

		super();
		this.blotterTexts = new ArrayList<BlotterText>();
		blotterTexts.add(blotterText);
		this.counterparty = counterparty;
	}

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param text the text
	 */
	public BlotterEntry(String counterparty, String text) {

		super();
		this.blotterTexts = new ArrayList<BlotterText>();
		blotterTexts.add(new BlotterText(text));
		this.counterparty = counterparty;
	}

	/**
	 * Instantiates a new blotter entry.
	 *
	 * @param counterparty the counterparty
	 * @param blotterTexts the blotter texts
	 */
	public BlotterEntry(String counterparty, List<BlotterText> blotterTexts) {

		super();
		this.blotterTexts = blotterTexts;
		this.counterparty = counterparty;
	}

	
	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public String getCounterparty() {
	
		return counterparty;
	}

	/**
	 * Gets the blotter texts.
	 *
	 * @return the blotter texts
	 */
	public List<BlotterText> getBlotterTexts() {

		return blotterTexts;
	}

	/**
	 * Gets the level icon.
	 *
	 * @return the level icon
	 */
	public LevelIcon getLevelIcon() {

		return levelIcon;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {

		return offset;
	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public void setOffset(int offset) {

		if (offset < 0)
			this.offset = 0;
		else
			this.offset = offset;
	}

}
