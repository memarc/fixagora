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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Class SpreadSheetMDInput.
 */
@Entity
public class SpreadSheetMDInput implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "spreadsheet_id", insertable = false, updatable = false, nullable = false)
	private SpreadSheet spreadSheet = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private AbstractBusinessComponent businessComponent = null;
			
	@Column(unique = false, nullable = false)
	private String mdEntryType = null;
	
	@Column(unique = false, nullable = false)
	private Integer securityColumn = null;	
	
	@Column(unique = false, nullable = false)
	private Boolean cleanUpOnLogout = false;	
	
	@Column(unique = false, nullable = true)
	private Integer counterpartyColumn = null;
	
	@Column(unique = false, nullable = true)
	private Integer mdEntryPxColumn = null;
		
	@Column(unique = false, nullable = true)
	private Integer mdEntrySizeColumn = null;
	
	@Column(unique = false, nullable = true)
	private Integer mdEntryDateColumn = null;		

	@Column(unique = false, nullable = true)
	private Integer mdEntryTimeColumn = null;
	
	@Column(unique = false, nullable = true)
	private Integer mdPriceDeltaColumn = null;
	
	@Column(unique = false, nullable = true)
	private Integer mdTradeVolumeColumn = null;
	
	@Column(unique = false, nullable = true)
	private Integer highPxColumn = null;	
	
	@Column(unique = false, nullable = true)
	private Integer lowPxColumn = null;	

	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public SpreadSheet getSpreadSheet() {
	
		return spreadSheet;
	}




	
	/**
	 * Sets the spread sheet.
	 *
	 * @param spreadSheet the new spread sheet
	 */
	public void setSpreadSheet(SpreadSheet spreadSheet) {
	
		this.spreadSheet = spreadSheet;
	}

	
	
	
	/**
	 * Gets the high px column.
	 *
	 * @return the high px column
	 */
	public Integer getHighPxColumn() {
	
		return highPxColumn;
	}





	
	/**
	 * Sets the high px column.
	 *
	 * @param highPxColumn the new high px column
	 */
	public void setHighPxColumn(Integer highPxColumn) {
	
		this.highPxColumn = highPxColumn;
	}





	
	/**
	 * Gets the low px column.
	 *
	 * @return the low px column
	 */
	public Integer getLowPxColumn() {
	
		return lowPxColumn;
	}





	
	/**
	 * Sets the low px column.
	 *
	 * @param lowPxColumn the new low px column
	 */
	public void setLowPxColumn(Integer lowPxColumn) {
	
		this.lowPxColumn = lowPxColumn;
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
	 * Gets the security column.
	 *
	 * @return the security column
	 */
	public Integer getSecurityColumn() {
	
		return securityColumn;
	}





	
	/**
	 * Sets the security column.
	 *
	 * @param securityColumn the new security column
	 */
	public void setSecurityColumn(Integer securityColumn) {
	
		this.securityColumn = securityColumn;
	}





	
	/**
	 * Gets the counterparty column.
	 *
	 * @return the counterparty column
	 */
	public Integer getCounterpartyColumn() {
	
		return counterpartyColumn;
	}





	
	/**
	 * Sets the counterparty column.
	 *
	 * @param counterpartyColumn the new counterparty column
	 */
	public void setCounterpartyColumn(Integer counterpartyColumn) {
	
		this.counterpartyColumn = counterpartyColumn;
	}





	
	/**
	 * Gets the md entry px column.
	 *
	 * @return the md entry px column
	 */
	public Integer getMdEntryPxColumn() {
	
		return mdEntryPxColumn;
	}





	
	/**
	 * Sets the md entry px column.
	 *
	 * @param mdEntryPxColumn the new md entry px column
	 */
	public void setMdEntryPxColumn(Integer mdEntryPxColumn) {
	
		this.mdEntryPxColumn = mdEntryPxColumn;
	}





	
	/**
	 * Gets the md entry size column.
	 *
	 * @return the md entry size column
	 */
	public Integer getMdEntrySizeColumn() {
	
		return mdEntrySizeColumn;
	}





	
	/**
	 * Sets the md entry size column.
	 *
	 * @param mdEntrySizeColumn the new md entry size column
	 */
	public void setMdEntrySizeColumn(Integer mdEntrySizeColumn) {
	
		this.mdEntrySizeColumn = mdEntrySizeColumn;
	}





	
	/**
	 * Gets the md entry date column.
	 *
	 * @return the md entry date column
	 */
	public Integer getMdEntryDateColumn() {
	
		return mdEntryDateColumn;
	}





	
	/**
	 * Sets the md entry date column.
	 *
	 * @param mdEntryDateColumn the new md entry date column
	 */
	public void setMdEntryDateColumn(Integer mdEntryDateColumn) {
	
		this.mdEntryDateColumn = mdEntryDateColumn;
	}





	
	/**
	 * Gets the md entry time column.
	 *
	 * @return the md entry time column
	 */
	public Integer getMdEntryTimeColumn() {
	
		return mdEntryTimeColumn;
	}





	
	/**
	 * Sets the md entry time column.
	 *
	 * @param mdEntryTimeColumn the new md entry time column
	 */
	public void setMdEntryTimeColumn(Integer mdEntryTimeColumn) {
	
		this.mdEntryTimeColumn = mdEntryTimeColumn;
	}





	
	/**
	 * Gets the md price delta column.
	 *
	 * @return the md price delta column
	 */
	public Integer getMdPriceDeltaColumn() {
	
		return mdPriceDeltaColumn;
	}





	
	/**
	 * Sets the md price delta column.
	 *
	 * @param mdPriceDeltaColumn the new md price delta column
	 */
	public void setMdPriceDeltaColumn(Integer mdPriceDeltaColumn) {
	
		this.mdPriceDeltaColumn = mdPriceDeltaColumn;
	}





	
	/**
	 * Gets the md trade volume column.
	 *
	 * @return the md trade volume column
	 */
	public Integer getMdTradeVolumeColumn() {
	
		return mdTradeVolumeColumn;
	}





	
	/**
	 * Sets the md trade volume column.
	 *
	 * @param mdTradeVolumeColumn the new md trade volume column
	 */
	public void setMdTradeVolumeColumn(Integer mdTradeVolumeColumn) {
	
		this.mdTradeVolumeColumn = mdTradeVolumeColumn;
	}





	/**
	 * Gets the business component.
	 *
	 * @return the business component
	 */
	public AbstractBusinessComponent getBusinessComponent() {
	
		return businessComponent;
	}






	
	/**
	 * Sets the business component.
	 *
	 * @param businessComponent the new business component
	 */
	public void setBusinessComponent(AbstractBusinessComponent businessComponent) {
	
		this.businessComponent = businessComponent;
	}





	/**
	 * Make eager.
	 */
	public void makeEager() {
		
		if(businessComponent!=null)
			businessComponent.getName();
		
	}





	/**
	 * Gets the clean up on logout.
	 *
	 * @return the clean up on logout
	 */
	public Boolean getCleanUpOnLogout() {

		return cleanUpOnLogout;
	}





	/**
	 * Sets the clean up on logout.
	 *
	 * @param cleanUpOnLogout the new clean up on logout
	 */
	public void setCleanUpOnLogout(Boolean cleanUpOnLogout) {

		this.cleanUpOnLogout = cleanUpOnLogout;
	}


	
}
