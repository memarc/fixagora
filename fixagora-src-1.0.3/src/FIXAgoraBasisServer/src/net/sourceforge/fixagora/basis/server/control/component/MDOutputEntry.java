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

import java.util.Date;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;


/**
 * The Class MDOutputEntry.
 */
public class MDOutputEntry {
	
	private String mdEntryType = null;
	
	private String securityValue = null;
		
	private Double mdEntryPxValue = null;
	
	private Double mdEntrySizeValue =  null;
	
	private Date mdEntryDateValue = null;

	private Date mdEntryTimeValue =  null;
	
	private Double mdPriceDeltaValue = null;
	
	private Double mdTradeVolumeValue =  null;
	
	private Double highPxValue =  null;
	
	private Double lowPxValue =  null;

	private AbstractBusinessComponent abstractBusinessComponent = null;
	
	
	
	
	/**
	 * Gets the abstract business component.
	 *
	 * @return the abstract business component
	 */
	public AbstractBusinessComponent getAbstractBusinessComponent() {
	
		return abstractBusinessComponent;
	}


	
	/**
	 * Sets the abstract business component.
	 *
	 * @param abstractBusinessComponent the new abstract business component
	 */
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {
	
		this.abstractBusinessComponent = abstractBusinessComponent;
	}


	/**
	 * Gets the md entry type.
	 *
	 * @return the md entry type
	 */
	public String getMdEntryType() {
	
		return mdEntryType;
	}

	
	/**
	 * Sets the md entry type.
	 *
	 * @param mdEntryType the new md entry type
	 */
	public void setMdEntryType(String mdEntryType) {
	
		this.mdEntryType = mdEntryType;
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
	
	
	/**
	 * Gets the md entry date value.
	 *
	 * @return the md entry date value
	 */
	public Date getMdEntryDateValue() {
	
		return mdEntryDateValue;
	}


	
	/**
	 * Sets the md entry date value.
	 *
	 * @param mdEntryDateValue the new md entry date value
	 */
	public void setMdEntryDateValue(Date mdEntryDateValue) {
	
		this.mdEntryDateValue = mdEntryDateValue;
	}


	
	/**
	 * Gets the md entry time value.
	 *
	 * @return the md entry time value
	 */
	public Date getMdEntryTimeValue() {
	
		return mdEntryTimeValue;
	}


	
	/**
	 * Sets the md entry time value.
	 *
	 * @param mdEntryTimeValue the new md entry time value
	 */
	public void setMdEntryTimeValue(Date mdEntryTimeValue) {
	
		this.mdEntryTimeValue = mdEntryTimeValue;
	}

	/**
	 * Checks if is delete.
	 *
	 * @return true, if is delete
	 */
	public boolean isDelete()
	{
		if(mdEntryPxValue != null)
			return false;
		
		if(mdEntrySizeValue != null)
			return false;
		
		if(mdEntryDateValue != null)
			return false;
		
		if(mdEntryTimeValue  != null)
			return false;
		
		if( mdPriceDeltaValue != null)
			return false;
		
		if(mdTradeVolumeValue != null)
			return false;
		
		if(highPxValue != null)
			return false;
		
		if(lowPxValue != null)
			return false;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof MDOutputEntry)
		{
			MDOutputEntry mdOutputEntry = (MDOutputEntry)obj;
						
			if(securityValue==null&&mdOutputEntry.getSecurityValue()!=null)
				return false;
			if(securityValue!=null&&mdOutputEntry.getSecurityValue()==null)
				return false;
			if(securityValue!=null&&mdOutputEntry.getSecurityValue()!=null&&!securityValue.equals(mdOutputEntry.getSecurityValue()))
				return false;
			
			if(mdEntryType==null&&mdOutputEntry.getMdEntryType()!=null)
				return false;
			if(mdEntryType!=null&&mdOutputEntry.getMdEntryType()==null)
				return false;
			if(mdEntryType!=null&&mdOutputEntry.getMdEntryType()!=null&&!mdEntryType.equals(mdOutputEntry.getMdEntryType()))
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
	    if(securityValue!=null)
	    	buffer.append(securityValue);
	    else
	    	buffer.append("null");
	    buffer.append("+");
	    if(mdEntryType!=null)
	    	buffer.append(mdEntryType);
	    else
	    	buffer.append("null");

	    return buffer.toString().hashCode();
	}

}
