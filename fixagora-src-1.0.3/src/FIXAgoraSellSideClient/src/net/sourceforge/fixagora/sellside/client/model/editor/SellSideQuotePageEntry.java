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
package net.sourceforge.fixagora.sellside.client.model.editor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;


/**
 * The Class SellSideQuotePageEntry.
 */
public class SellSideQuotePageEntry {
	
	private SellSideMDInputEntry sellSideMDInputEntry = null;
	
	private Boolean bidPriceUp = null;
	
	private long bidPriceUpdate = 0;
	
	private Boolean askPriceUp = null;
	
	private long askPriceUpdate = 0;
	
	private int priceWidth = 0;
	
	private int dollarFracOffset = 0;
	
	private Boolean bidYieldUp = null;
	
	private long bidYieldUpdate = 0;
	
	private Boolean askYieldUp = null;
	
	private long askYieldUpdate = 0;
	
	private int yieldWidth = 0;
	
	private long bidSizeUpdate = 0;
	
	private long askSizeUpdate = 0;
	
	private int sizeWidth = 0;
	
	private int changeWidth = 0;
	
	private int changeFracOffset = 0;
	
	private int highWidth = 0;
	
	private int highFracOffset = 0;
	
	private int lowWidth = 0;
	
	private int lowFracOffset = 0;

	private DecimalFormat priceDecimalFormat = null;
	
	private DecimalFormat yieldDecimalFormat = null;
	
	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0");

	private DecimalFormatSymbols decimalFormatSymbols = null;

	
	/**
	 * Instantiates a new sell side quote page entry.
	 */
	public SellSideQuotePageEntry() {

		super();
		decimalFormatSymbols  = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);

	}

	
	/**
	 * Gets the yield decimal format.
	 *
	 * @return the yield decimal format
	 */
	public DecimalFormat getYieldDecimalFormat() {
	
		return yieldDecimalFormat;
	}



	
	/**
	 * Sets the yield decimal format.
	 *
	 * @param yieldDecimalFormat the new yield decimal format
	 */
	public void setYieldDecimalFormat(DecimalFormat yieldDecimalFormat) {
	
		this.yieldDecimalFormat = yieldDecimalFormat;
	}



	/**
	 * Gets the sell side md input entry.
	 *
	 * @return the sell side md input entry
	 */
	public SellSideMDInputEntry getSellSideMDInputEntry() {
	
		return sellSideMDInputEntry;
	}

	
	/**
	 * Sets the sell side md input entry.
	 *
	 * @param sellSideMDInputEntry the new sell side md input entry
	 */
	public void setSellSideMDInputEntry(SellSideMDInputEntry sellSideMDInputEntry) {
	
		if(this.sellSideMDInputEntry!=null)
		{
			if(this.sellSideMDInputEntry.getMdEntryBidPxValue()==null||sellSideMDInputEntry.getMdEntryBidPxValue()==null)
				bidPriceUp = null;
			else if(this.sellSideMDInputEntry.getMdEntryBidPxValue()>sellSideMDInputEntry.getMdEntryBidPxValue())
			{
				bidPriceUp = false;
				bidPriceUpdate = System.currentTimeMillis();
			}
			else if(this.sellSideMDInputEntry.getMdEntryBidPxValue()<sellSideMDInputEntry.getMdEntryBidPxValue())
			{
				bidPriceUp = true;
				bidPriceUpdate = System.currentTimeMillis();
			}
			
			if(this.sellSideMDInputEntry.getMdEntryAskPxValue()==null||sellSideMDInputEntry.getMdEntryAskPxValue()==null)
				askPriceUp = null;
			else if(this.sellSideMDInputEntry.getMdEntryAskPxValue()>sellSideMDInputEntry.getMdEntryAskPxValue())
			{
				askPriceUp = false;
				askPriceUpdate = System.currentTimeMillis();
			}
			else if(this.sellSideMDInputEntry.getMdEntryAskPxValue()<sellSideMDInputEntry.getMdEntryAskPxValue())
			{
				askPriceUp = true;
				askPriceUpdate = System.currentTimeMillis();
			}
			
		}
		this.sellSideMDInputEntry = sellSideMDInputEntry;
	}
	
	
	/**
	 * Gets the change frac offset.
	 *
	 * @return the change frac offset
	 */
	public int getChangeFracOffset() {
	
		return changeFracOffset;
	}


	
	/**
	 * Sets the change frac offset.
	 *
	 * @param changeFracOffset the new change frac offset
	 */
	public void setChangeFracOffset(int changeFracOffset) {
	
		this.changeFracOffset = changeFracOffset;
	}


	
	/**
	 * Gets the high frac offset.
	 *
	 * @return the high frac offset
	 */
	public int getHighFracOffset() {
	
		return highFracOffset;
	}


	
	/**
	 * Sets the high frac offset.
	 *
	 * @param highFracOffset the new high frac offset
	 */
	public void setHighFracOffset(int highFracOffset) {
	
		this.highFracOffset = highFracOffset;
	}


	
	/**
	 * Gets the low frac offset.
	 *
	 * @return the low frac offset
	 */
	public int getLowFracOffset() {
	
		return lowFracOffset;
	}


	
	/**
	 * Sets the low frac offset.
	 *
	 * @param lowFracOffset the new low frac offset
	 */
	public void setLowFracOffset(int lowFracOffset) {
	
		this.lowFracOffset = lowFracOffset;
	}


	/**
	 * Gets the dollar frac offset.
	 *
	 * @return the dollar frac offset
	 */
	public int getDollarFracOffset() {
	
		return dollarFracOffset;
	}


	
	/**
	 * Sets the dollar frac offset.
	 *
	 * @param dollarFracOffset the new dollar frac offset
	 */
	public void setDollarFracOffset(int dollarFracOffset) {
	
		this.dollarFracOffset = dollarFracOffset;
	}


	/**
	 * Gets the bid price up.
	 *
	 * @return the bid price up
	 */
	public Boolean getBidPriceUp() {
	
		return bidPriceUp;
	}

	
	/**
	 * Sets the bid price up.
	 *
	 * @param bidPriceUp the new bid price up
	 */
	public void setBidPriceUp(Boolean bidPriceUp) {
	
		this.bidPriceUp = bidPriceUp;
	}

	
	/**
	 * Gets the ask price up.
	 *
	 * @return the ask price up
	 */
	public Boolean getAskPriceUp() {
	
		return askPriceUp;
	}

	
	/**
	 * Sets the ask price up.
	 *
	 * @param askPriceUp the new ask price up
	 */
	public void setAskPriceUp(Boolean askPriceUp) {
	
		this.askPriceUp = askPriceUp;
	}

	
	/**
	 * Gets the price width.
	 *
	 * @return the price width
	 */
	public int getPriceWidth() {
	
		return priceWidth;
	}

	
	/**
	 * Sets the price width.
	 *
	 * @param priceWidth the new price width
	 */
	public void setPriceWidth(int priceWidth) {
	
		this.priceWidth = priceWidth;
	}

	
	/**
	 * Gets the bid yield up.
	 *
	 * @return the bid yield up
	 */
	public Boolean getBidYieldUp() {
	
		return bidYieldUp;
	}

	
	/**
	 * Sets the bid yield up.
	 *
	 * @param bidYieldUp the new bid yield up
	 */
	public void setBidYieldUp(Boolean bidYieldUp) {
	
		this.bidYieldUp = bidYieldUp;
	}

	
	/**
	 * Gets the ask yield up.
	 *
	 * @return the ask yield up
	 */
	public Boolean getAskYieldUp() {
	
		return askYieldUp;
	}

	
	/**
	 * Sets the ask yield up.
	 *
	 * @param askYieldUp the new ask yield up
	 */
	public void setAskYieldUp(Boolean askYieldUp) {
	
		this.askYieldUp = askYieldUp;
	}

	
	/**
	 * Gets the yield width.
	 *
	 * @return the yield width
	 */
	public int getYieldWidth() {
	
		return yieldWidth;
	}

	
	/**
	 * Sets the yield width.
	 *
	 * @param yieldWidth the new yield width
	 */
	public void setYieldWidth(int yieldWidth) {
	
		this.yieldWidth = yieldWidth;
	}

	
	/**
	 * Gets the size width.
	 *
	 * @return the size width
	 */
	public int getSizeWidth() {
	
		return sizeWidth;
	}

	
	/**
	 * Sets the size width.
	 *
	 * @param sizeWidth the new size width
	 */
	public void setSizeWidth(int sizeWidth) {
	
		this.sizeWidth = sizeWidth;
	}

	
	/**
	 * Gets the change width.
	 *
	 * @return the change width
	 */
	public int getChangeWidth() {
	
		return changeWidth;
	}

	
	/**
	 * Sets the change width.
	 *
	 * @param changeWidth the new change width
	 */
	public void setChangeWidth(int changeWidth) {
	
		this.changeWidth = changeWidth;
	}

	
	/**
	 * Gets the high width.
	 *
	 * @return the high width
	 */
	public int getHighWidth() {
	
		return highWidth;
	}

	
	/**
	 * Sets the high width.
	 *
	 * @param highWidth the new high width
	 */
	public void setHighWidth(int highWidth) {
	
		this.highWidth = highWidth;
	}

	
	/**
	 * Gets the low width.
	 *
	 * @return the low width
	 */
	public int getLowWidth() {
	
		return lowWidth;
	}

	
	/**
	 * Sets the low width.
	 *
	 * @param lowWidth the new low width
	 */
	public void setLowWidth(int lowWidth) {
	
		this.lowWidth = lowWidth;
	}


	
	/**
	 * Gets the bid price update.
	 *
	 * @return the bid price update
	 */
	public long getBidPriceUpdate() {
	
		return bidPriceUpdate;
	}


	
	/**
	 * Gets the ask price update.
	 *
	 * @return the ask price update
	 */
	public long getAskPriceUpdate() {
	
		return askPriceUpdate;
	}


	
	/**
	 * Gets the bid yield update.
	 *
	 * @return the bid yield update
	 */
	public long getBidYieldUpdate() {
	
		return bidYieldUpdate;
	}


	
	/**
	 * Gets the ask yield update.
	 *
	 * @return the ask yield update
	 */
	public long getAskYieldUpdate() {
	
		return askYieldUpdate;
	}


	
	/**
	 * Gets the bid size update.
	 *
	 * @return the bid size update
	 */
	public long getBidSizeUpdate() {
	
		return bidSizeUpdate;
	}


	
	/**
	 * Gets the ask size update.
	 *
	 * @return the ask size update
	 */
	public long getAskSizeUpdate() {
	
		return askSizeUpdate;
	}
	
	
	/**
	 * Sets the price decimal format.
	 *
	 * @param decimalFormat the new price decimal format
	 */
	public void setPriceDecimalFormat(DecimalFormat decimalFormat) {
	
		this.priceDecimalFormat = decimalFormat;
	}


	/**
	 * Gets the price decimal format.
	 *
	 * @return the price decimal format
	 */
	public DecimalFormat getPriceDecimalFormat() {
	
		return priceDecimalFormat;
	}


	
	/**
	 * Gets the size decimal format.
	 *
	 * @return the size decimal format
	 */
	public DecimalFormat getSizeDecimalFormat() {
	
		return decimalFormat2;
	}
	


}
