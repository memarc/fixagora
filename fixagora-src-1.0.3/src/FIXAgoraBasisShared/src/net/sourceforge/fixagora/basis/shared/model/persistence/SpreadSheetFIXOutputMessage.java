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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * The Class SpreadSheetFIXOutputMessage.
 */
@Entity
public class SpreadSheetFIXOutputMessage extends SpreadSheetFIXFieldMap implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(unique = false, nullable = false)
	private String messageName = null;
	
	@Column(unique = false, nullable = false)
	private String messageId = null;
	
	@Column(unique = false, nullable = false)
	private String dataDictionary = null;
	
	@Column(unique = false, nullable = true)
	private String sendIf = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private AbstractBusinessComponent businessComponent = null;
	
	@ManyToOne
	@JoinColumn(name = "spreadsheet_id", insertable = false, updatable = false, nullable = false)
	private SpreadSheet spreadSheet = null;
		
	
	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
	private SpreadSheetFIXFieldMap header = new SpreadSheetFIXFieldMap();

	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
	private SpreadSheetFIXFieldMap trailer = new SpreadSheetFIXFieldMap();
	
	/**
	 * Gets the message id.
	 *
	 * @return the message id
	 */
	public String getMessageId() {
	
		return messageId;
	}



	/**
	 * Gets the data dictionary.
	 *
	 * @return the data dictionary
	 */
	public String getDataDictionary() {
	
		return dataDictionary;
	}

	
	/**
	 * Sets the data dictionary.
	 *
	 * @param dataDictionary the new data dictionary
	 */
	public void setDataDictionary(String dataDictionary) {
			
		this.dataDictionary = dataDictionary;
	}





	/**
	 * Sets the message id.
	 *
	 * @param messageId the new message id
	 */
	public void setMessageId(String messageId) {
	
		this.messageId = messageId;
	}





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
	 * Gets the message name.
	 *
	 * @return the message name
	 */
	public String getMessageName() {
	
		return messageName;
	}


	
	/**
	 * Sets the message name.
	 *
	 * @param messageName the new message name
	 */
	public void setMessageName(String messageName) {
	
		this.messageName = messageName;
	}






	/**
	 * Gets the trailer.
	 *
	 * @return the trailer
	 */
	public SpreadSheetFIXFieldMap getTrailer() {
		return trailer;
	}






	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public SpreadSheetFIXFieldMap getHeader() {

		// TODO Auto-generated method stub
		return header;
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
	 * Sets the header.
	 *
	 * @param header the new header
	 */
	public void setHeader(SpreadSheetFIXFieldMap header) {
	
		this.header = header;
	}






	
	/**
	 * Sets the trailer.
	 *
	 * @param trailer the new trailer
	 */
	public void setTrailer(SpreadSheetFIXFieldMap trailer) {
	
		this.trailer = trailer;
	}






	
	/**
	 * Gets the send if.
	 *
	 * @return the send if
	 */
	public String getSendIf() {
	
		return sendIf;
	}



	
	/**
	 * Sets the send if.
	 *
	 * @param sendIf the new send if
	 */
	public void setSendIf(String sendIf) {
	
		this.sendIf = sendIf;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap#makeEager()
	 */
	@Override
	public void makeEager() {
		super.makeEager();
		if(businessComponent!=null)
			businessComponent.makeEager();
		header.makeEager();
		trailer.makeEager();
	}


	
}
