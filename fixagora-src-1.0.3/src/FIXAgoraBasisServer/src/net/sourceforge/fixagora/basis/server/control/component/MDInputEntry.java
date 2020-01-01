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
package net.sourceforge.fixagora.basis.server.control.component;



/**
 * The Class MDInputEntry.
 */
public class MDInputEntry {

	
	private int securityColumn = -1;			
	
	private int counterpartyColumn = -1;
	
	private int mdEntryPxColumn = -1;
		
	private int mdEntrySizeColumn =  -1;
	
	private int mdEntryDateColumn =  -1;	

	private int mdEntryTimeColumn =  -1;
	
	private int mdPriceDeltaColumn = -1;
	
	private int mdTradeVolumeColumn =  -1;
	
	private int highPxColumn =  -1;
	
	private int lowPxColumn =  -1;
	
	private String securityValue = null;			
	
	private String counterpartyValue = null;
	
	private Double mdEntryPxValue = null;
		
	private Double mdEntrySizeValue =  null;
	
	private Double mdEntryDateValue = null;

	private Double mdEntryTimeValue =  null;
	
	private Double mdPriceDeltaValue = null;
	
	private Double mdTradeVolumeValue =  null;
	
	private Double highPxValue =  null;
	
	private Double lowPxValue =  null;
		
	/**
	 * Gets the security column.
	 *
	 * @return the security column
	 */
	public int getSecurityColumn() {
	
		return securityColumn;
	}

	
	/**
	 * Sets the security column.
	 *
	 * @param securityColumn the new security column
	 */
	public void setSecurityColumn(int securityColumn) {
	
		this.securityColumn = securityColumn;
	}

	
	/**
	 * Gets the counterparty column.
	 *
	 * @return the counterparty column
	 */
	public int getCounterpartyColumn() {
	
		return counterpartyColumn;
	}

	
	/**
	 * Sets the counterparty column.
	 *
	 * @param counterpartyColumn the new counterparty column
	 */
	public void setCounterpartyColumn(int counterpartyColumn) {
	
		this.counterpartyColumn = counterpartyColumn;
	}

	
	/**
	 * Gets the md entry px column.
	 *
	 * @return the md entry px column
	 */
	public int getMdEntryPxColumn() {
	
		return mdEntryPxColumn;
	}

	
	/**
	 * Sets the md entry px column.
	 *
	 * @param mdEntryPxColumn the new md entry px column
	 */
	public void setMdEntryPxColumn(int mdEntryPxColumn) {
	
		this.mdEntryPxColumn = mdEntryPxColumn;
	}

	
	/**
	 * Gets the md entry size column.
	 *
	 * @return the md entry size column
	 */
	public int getMdEntrySizeColumn() {
	
		return mdEntrySizeColumn;
	}

	
	/**
	 * Sets the md entry size column.
	 *
	 * @param mdEntrySizeColumn the new md entry size column
	 */
	public void setMdEntrySizeColumn(int mdEntrySizeColumn) {
	
		this.mdEntrySizeColumn = mdEntrySizeColumn;
	}

	
	/**
	 * Gets the md entry date column.
	 *
	 * @return the md entry date column
	 */
	public int getMdEntryDateColumn() {
	
		return mdEntryDateColumn;
	}

	
	/**
	 * Sets the md entry date column.
	 *
	 * @param mdEntryDateColumn the new md entry date column
	 */
	public void setMdEntryDateColumn(int mdEntryDateColumn) {
	
		this.mdEntryDateColumn = mdEntryDateColumn;
	}

	
	/**
	 * Gets the md entry time column.
	 *
	 * @return the md entry time column
	 */
	public int getMdEntryTimeColumn() {
	
		return mdEntryTimeColumn;
	}

	
	/**
	 * Sets the md entry time column.
	 *
	 * @param mdEntryTimeColumn the new md entry time column
	 */
	public void setMdEntryTimeColumn(int mdEntryTimeColumn) {
	
		this.mdEntryTimeColumn = mdEntryTimeColumn;
	}

	
	/**
	 * Gets the md price delta column.
	 *
	 * @return the md price delta column
	 */
	public int getMdPriceDeltaColumn() {
	
		return mdPriceDeltaColumn;
	}

	
	/**
	 * Sets the md price delta column.
	 *
	 * @param mdPriceDeltaColumn the new md price delta column
	 */
	public void setMdPriceDeltaColumn(int mdPriceDeltaColumn) {
	
		this.mdPriceDeltaColumn = mdPriceDeltaColumn;
	}

	
	/**
	 * Gets the md trade volume column.
	 *
	 * @return the md trade volume column
	 */
	public int getMdTradeVolumeColumn() {
	
		return mdTradeVolumeColumn;
	}

	
	/**
	 * Sets the md trade volume column.
	 *
	 * @param mdTradeVolumeColumn the new md trade volume column
	 */
	public void setMdTradeVolumeColumn(int mdTradeVolumeColumn) {
	
		this.mdTradeVolumeColumn = mdTradeVolumeColumn;
	}

	
	/**
	 * Gets the high px column.
	 *
	 * @return the high px column
	 */
	public int getHighPxColumn() {
	
		return highPxColumn;
	}

	
	/**
	 * Sets the high px column.
	 *
	 * @param highPxColumn the new high px column
	 */
	public void setHighPxColumn(int highPxColumn) {
	
		this.highPxColumn = highPxColumn;
	}

	
	/**
	 * Gets the low px column.
	 *
	 * @return the low px column
	 */
	public int getLowPxColumn() {
	
		return lowPxColumn;
	}

	
	/**
	 * Sets the low px column.
	 *
	 * @param lowPxColumn the new low px column
	 */
	public void setLowPxColumn(int lowPxColumn) {
	
		this.lowPxColumn = lowPxColumn;
	}

	
	/**
	 * Gets the security value.
	 *
	 * @return the security value
	 */
	public String getSecurityValue() {
	
		return securityValue;
	}

	
	/**
	 * Sets the security value.
	 *
	 * @param securityValue the new security value
	 */
	public void setSecurityValue(String securityValue) {
	
		this.securityValue = securityValue;
	}

	
	/**
	 * Gets the counterparty value.
	 *
	 * @return the counterparty value
	 */
	public String getCounterpartyValue() {
	
		return counterpartyValue;
	}

	
	/**
	 * Sets the counterparty value.
	 *
	 * @param counterpartyValue the new counterparty value
	 */
	public void setCounterpartyValue(String counterpartyValue) {
	
		this.counterpartyValue = counterpartyValue;
	}
	
	
	
	/**
	 * Gets the md entry px value.
	 *
	 * @return the md entry px value
	 */
	public Double getMdEntryPxValue() {
	
		return mdEntryPxValue;
	}


	
	/**
	 * Sets the md entry px value.
	 *
	 * @param mdEntryPxValue the new md entry px value
	 */
	public void setMdEntryPxValue(Double mdEntryPxValue) {
	
		this.mdEntryPxValue = mdEntryPxValue;
	}


	
	/**
	 * Gets the md entry size value.
	 *
	 * @return the md entry size value
	 */
	public Double getMdEntrySizeValue() {
	
		return mdEntrySizeValue;
	}


	
	/**
	 * Sets the md entry size value.
	 *
	 * @param mdEntrySizeValue the new md entry size value
	 */
	public void setMdEntrySizeValue(Double mdEntrySizeValue) {
	
		this.mdEntrySizeValue = mdEntrySizeValue;
	}


	
	/**
	 * Gets the md entry date value.
	 *
	 * @return the md entry date value
	 */
	public Double getMdEntryDateValue() {
	
		return mdEntryDateValue;
	}


	
	/**
	 * Sets the md entry date value.
	 *
	 * @param mdEntryDateValue the new md entry date value
	 */
	public void setMdEntryDateValue(Double mdEntryDateValue) {
	
		this.mdEntryDateValue = mdEntryDateValue;
	}


	
	/**
	 * Gets the md entry time value.
	 *
	 * @return the md entry time value
	 */
	public Double getMdEntryTimeValue() {
	
		return mdEntryTimeValue;
	}


	
	/**
	 * Sets the md entry time value.
	 *
	 * @param mdEntryTimeValue the new md entry time value
	 */
	public void setMdEntryTimeValue(Double mdEntryTimeValue) {
	
		this.mdEntryTimeValue = mdEntryTimeValue;
	}


	
	/**
	 * Gets the md price delta value.
	 *
	 * @return the md price delta value
	 */
	public Double getMdPriceDeltaValue() {
	
		return mdPriceDeltaValue;
	}


	
	/**
	 * Sets the md price delta value.
	 *
	 * @param mdPriceDeltaValue the new md price delta value
	 */
	public void setMdPriceDeltaValue(Double mdPriceDeltaValue) {
	
		this.mdPriceDeltaValue = mdPriceDeltaValue;
	}


	
	/**
	 * Gets the md trade volume value.
	 *
	 * @return the md trade volume value
	 */
	public Double getMdTradeVolumeValue() {
	
		return mdTradeVolumeValue;
	}


	
	/**
	 * Sets the md trade volume value.
	 *
	 * @param mdTradeVolumeValue the new md trade volume value
	 */
	public void setMdTradeVolumeValue(Double mdTradeVolumeValue) {
	
		this.mdTradeVolumeValue = mdTradeVolumeValue;
	}


	
	/**
	 * Gets the high px value.
	 *
	 * @return the high px value
	 */
	public Double getHighPxValue() {
	
		return highPxValue;
	}


	
	/**
	 * Sets the high px value.
	 *
	 * @param highPxValue the new high px value
	 */
	public void setHighPxValue(Double highPxValue) {
	
		this.highPxValue = highPxValue;
	}


	
	/**
	 * Gets the low px value.
	 *
	 * @return the low px value
	 */
	public Double getLowPxValue() {
	
		return lowPxValue;
	}


	
	/**
	 * Sets the low px value.
	 *
	 * @param lowPxValue the new low px value
	 */
	public void setLowPxValue(Double lowPxValue) {
	
		this.lowPxValue = lowPxValue;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public MDInputEntry clone() {

		MDInputEntry mdInputEntry = new MDInputEntry();
		mdInputEntry.setCounterpartyColumn(counterpartyColumn);
		mdInputEntry.setCounterpartyValue(counterpartyValue);
		mdInputEntry.setHighPxColumn(highPxColumn);
		mdInputEntry.setHighPxValue(highPxValue);
		mdInputEntry.setLowPxColumn(lowPxColumn);
		mdInputEntry.setLowPxValue(lowPxValue);
		mdInputEntry.setMdEntryDateColumn(mdEntryDateColumn);
		mdInputEntry.setMdEntryDateValue(mdEntryDateValue);
		mdInputEntry.setMdEntryPxColumn(mdEntryPxColumn);
		mdInputEntry.setMdEntryPxValue(mdEntryPxValue);
		mdInputEntry.setMdEntrySizeColumn(mdEntrySizeColumn);
		mdInputEntry.setMdEntrySizeValue(mdEntrySizeValue);
		mdInputEntry.setMdEntryTimeColumn(mdEntryTimeColumn);
		mdInputEntry.setMdEntrySizeValue(mdEntrySizeValue);
		mdInputEntry.setMdPriceDeltaColumn(mdPriceDeltaColumn);
		mdInputEntry.setMdPriceDeltaValue(mdPriceDeltaValue);
		mdInputEntry.setMdTradeVolumeColumn(mdTradeVolumeColumn);
		mdInputEntry.setMdTradeVolumeValue(mdTradeVolumeValue);
		mdInputEntry.setSecurityColumn(securityColumn);
		mdInputEntry.setSecurityValue(securityValue);
		return mdInputEntry;
	}
	
	/**
	 * Gets the delete md input entry.
	 *
	 * @return the delete md input entry
	 */
	public MDInputEntry getDeleteMDInputEntry() {

		MDInputEntry mdInputEntry = new MDInputEntry();
		mdInputEntry.setCounterpartyColumn(counterpartyColumn);
		mdInputEntry.setCounterpartyValue(counterpartyValue);
		mdInputEntry.setSecurityColumn(securityColumn);
		mdInputEntry.setSecurityValue(securityValue);
		
		mdInputEntry.setHighPxColumn(highPxColumn);
		mdInputEntry.setLowPxColumn(lowPxColumn);
		mdInputEntry.setMdEntryDateColumn(mdEntryDateColumn);
		mdInputEntry.setMdEntryPxColumn(mdEntryPxColumn);
		mdInputEntry.setMdEntrySizeColumn(mdEntrySizeColumn);
		mdInputEntry.setMdEntryTimeColumn(mdEntryTimeColumn);
		mdInputEntry.setMdPriceDeltaColumn(mdPriceDeltaColumn);
		mdInputEntry.setMdTradeVolumeColumn(mdTradeVolumeColumn);
		
		return mdInputEntry;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof MDInputEntry)
		{
			MDInputEntry mdInputEntry = (MDInputEntry)obj;
			if(counterpartyColumn!=mdInputEntry.getCounterpartyColumn())
				return false;
			if(securityColumn!=mdInputEntry.getSecurityColumn())
				return false;
			
			if(highPxColumn!=mdInputEntry.getHighPxColumn())
				return false;
			if(lowPxColumn!=mdInputEntry.getLowPxColumn())
				return false;
			if(mdEntryDateColumn!=mdInputEntry.getMdEntryDateColumn())
				return false;
			if(mdEntryPxColumn!=mdInputEntry.getMdEntryPxColumn())
				return false;
			if(mdEntrySizeColumn!=mdInputEntry.getMdEntrySizeColumn())
				return false;
			if(mdEntryTimeColumn!=mdInputEntry.getMdEntryTimeColumn())
				return false;
			if(mdPriceDeltaColumn!=mdInputEntry.getMdPriceDeltaColumn())
				return false;
			if(mdTradeVolumeColumn!=mdInputEntry.getMdTradeVolumeColumn())
				return false;
			
			if(counterpartyValue==null&&mdInputEntry.getCounterpartyValue()!=null)
				return false;
			if(counterpartyValue!=null&&mdInputEntry.getCounterpartyValue()==null)
				return false;
			if(counterpartyValue!=null&&mdInputEntry.getCounterpartyValue()!=null&&!counterpartyValue.equals(mdInputEntry.getCounterpartyValue()))
				return false;
			
			if(securityValue==null&&mdInputEntry.getSecurityValue()!=null)
				return false;
			if(securityValue!=null&&mdInputEntry.getSecurityValue()==null)
				return false;
			if(securityValue!=null&&mdInputEntry.getSecurityValue()!=null&&!securityValue.equals(mdInputEntry.getSecurityValue()))
				return false;
			
			return true;
			
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(securityColumn);
	    if(securityValue!=null)
	    	buffer.append(securityValue);
	    else
	    	buffer.append("null");
	    buffer.append(counterpartyColumn);
	    if(counterpartyValue!=null)
	    	buffer.append(counterpartyValue);
	    else
	    	buffer.append("null");
	    buffer.append(highPxColumn);
	    buffer.append(lowPxColumn);
	    buffer.append(mdEntryDateColumn);
	    buffer.append(mdEntryPxColumn);
	    buffer.append(mdEntrySizeColumn);
	    buffer.append(mdEntryTimeColumn);
	    buffer.append(mdPriceDeltaColumn);
	    buffer.append(mdTradeVolumeColumn);
	    
	    return buffer.toString().hashCode();
	}


	/**
	 * Can replace.
	 *
	 * @param mdInputEntry2 the md input entry2
	 * @return true, if successful
	 */
	public boolean canReplace(MDInputEntry mdInputEntry2) {

		if(mdInputEntry2==null)
			return false;
		
		if(securityValue!=null&&!securityValue.equals(mdInputEntry2.getSecurityValue()))
				return false;
		
		if(counterpartyValue!=null&&!counterpartyValue.equals(mdInputEntry2.getCounterpartyValue()))
			return false;
	
		
		if(securityColumn != mdInputEntry2.getSecurityColumn())
			return false;
		
		
		if(counterpartyColumn != mdInputEntry2.getCounterpartyColumn())
			return false;
		
		if(mdEntryPxColumn != mdInputEntry2.getMdEntryPxColumn())
			return false;
		
		if(mdEntrySizeColumn!=mdInputEntry2.getMdEntrySizeColumn())
			return false;
		
		if(mdEntryDateColumn!=mdInputEntry2.getMdEntryDateColumn())
			return false;
		
		if(mdEntryTimeColumn!=mdInputEntry2.getMdEntryTimeColumn())
			return false;
		
		if(mdPriceDeltaColumn!=mdInputEntry2.getMdPriceDeltaColumn())
			return false;
		
		if(mdTradeVolumeColumn!=mdInputEntry2.getMdTradeVolumeColumn())
			return false;
		
		if(highPxColumn!=mdInputEntry2.getHighPxColumn())
			return false;
		
		if(lowPxColumn!=mdInputEntry2.getLowPxColumn())
			return false;
		
		return true;
		

	}
	
}
