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
package net.sourceforge.fixagora.sap.shared.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;

/**
 * The Class SAPTradeCapture.
 */
@Entity
public class SAPTradeCapture extends AbstractBusinessComponent  {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
		
	@Column(unique = false, nullable = false)
	private String securityIDSource = null;
	
	@Column(unique = false, nullable = false)
	private String sapClient = null;
	
	@Column(unique = false, nullable = false)
	private String sapUserName = null;
	
	@Column(unique = false, nullable = false)
	private String sapPassword = null;
	
	@Column(unique = false, nullable = false)
	private String sapServerName = null;

	@Column(unique = false, nullable = false)
	private String sapSystemNumber = null;
		
	@Column(unique = false, nullable = false)
	private String sapCompanyCode = null;
	
	@Column(unique = false, nullable = false)
	private String sapTransactionTypeBuy = null;
	
	@Column(unique = false, nullable = false)
	private String sapTransactionTypeSell = null;
	
	@Column(unique = false, nullable = false)
	private String sapSecuritiesAccount = null;
		
	@Column(unique = false, nullable = false)
	private String sapGeneralValuationClass = null;	
	
	@Column(unique = false, nullable = false)
	private boolean setTrader = false;
	
	@Column(unique = false, nullable = false)
	private boolean setExchange = false;
	
	
	
	
	/**
	 * Gets the sap transaction type buy.
	 *
	 * @return the sap transaction type buy
	 */
	public String getSapTransactionTypeBuy() {
	
		return sapTransactionTypeBuy;
	}



	
	/**
	 * Sets the sap transaction type buy.
	 *
	 * @param sapTransactionTypeBuy the new sap transaction type buy
	 */
	public void setSapTransactionTypeBuy(String sapTransactionTypeBuy) {
	
		this.sapTransactionTypeBuy = sapTransactionTypeBuy;
	}



	
	/**
	 * Gets the sap transaction type sell.
	 *
	 * @return the sap transaction type sell
	 */
	public String getSapTransactionTypeSell() {
	
		return sapTransactionTypeSell;
	}



	
	/**
	 * Sets the sap transaction type sell.
	 *
	 * @param sapTransactionTypeSell the new sap transaction type sell
	 */
	public void setSapTransactionTypeSell(String sapTransactionTypeSell) {
	
		this.sapTransactionTypeSell = sapTransactionTypeSell;
	}



	
	/**
	 * Gets the sap securities account.
	 *
	 * @return the sap securities account
	 */
	public String getSapSecuritiesAccount() {
	
		return sapSecuritiesAccount;
	}



	
	/**
	 * Sets the sap securities account.
	 *
	 * @param sapSecuritiesAccount the new sap securities account
	 */
	public void setSapSecuritiesAccount(String sapSecuritiesAccount) {
	
		this.sapSecuritiesAccount = sapSecuritiesAccount;
	}



	
	/**
	 * Gets the sap general valuation class.
	 *
	 * @return the sap general valuation class
	 */
	public String getSapGeneralValuationClass() {
	
		return sapGeneralValuationClass;
	}



	
	/**
	 * Sets the sap general valuation class.
	 *
	 * @param sapGeneralValuationClass the new sap general valuation class
	 */
	public void setSapGeneralValuationClass(String sapGeneralValuationClass) {
	
		this.sapGeneralValuationClass = sapGeneralValuationClass;
	}



	
	/**
	 * Checks if is sets the trader.
	 *
	 * @return true, if is sets the trader
	 */
	public boolean isSetTrader() {
	
		return setTrader;
	}



	
	/**
	 * Sets the sets the trader.
	 *
	 * @param setTrader the new sets the trader
	 */
	public void setSetTrader(boolean setTrader) {
	
		this.setTrader = setTrader;
	}



	
	/**
	 * Checks if is sets the exchange.
	 *
	 * @return true, if is sets the exchange
	 */
	public boolean isSetExchange() {
	
		return setExchange;
	}



	
	/**
	 * Sets the sets the exchange.
	 *
	 * @param setExchange the new sets the exchange
	 */
	public void setSetExchange(boolean setExchange) {
	
		this.setExchange = setExchange;
	}



	/**
	 * Gets the sap user name.
	 *
	 * @return the sap user name
	 */
	public String getSapUserName() {
	
		return sapUserName;
	}


	
	/**
	 * Sets the sap user name.
	 *
	 * @param sapUserName the new sap user name
	 */
	public void setSapUserName(String sapUserName) {
	
		this.sapUserName = sapUserName;
	}


	
	/**
	 * Gets the sap password.
	 *
	 * @return the sap password
	 */
	public String getSapPassword() {
	
		return sapPassword;
	}


	
	/**
	 * Sets the sap password.
	 *
	 * @param sapPassword the new sap password
	 */
	public void setSapPassword(String sapPassword) {
	
		this.sapPassword = sapPassword;
	}


	
	/**
	 * Gets the sap server name.
	 *
	 * @return the sap server name
	 */
	public String getSapServerName() {
	
		return sapServerName;
	}


	
	/**
	 * Sets the sap server name.
	 *
	 * @param sapServerName the new sap server name
	 */
	public void setSapServerName(String sapServerName) {
	
		this.sapServerName = sapServerName;
	}


	

	
	/**
	 * Gets the sap client.
	 *
	 * @return the sap client
	 */
	public String getSapClient() {
	
		return sapClient;
	}



	
	/**
	 * Sets the sap client.
	 *
	 * @param sapClient the new sap client
	 */
	public void setSapClient(String sapClient) {
	
		this.sapClient = sapClient;
	}



	
	/**
	 * Gets the sap system number.
	 *
	 * @return the sap system number
	 */
	public String getSapSystemNumber() {
	
		return sapSystemNumber;
	}



	
	/**
	 * Sets the sap system number.
	 *
	 * @param sapSystemNumber the new sap system number
	 */
	public void setSapSystemNumber(String sapSystemNumber) {
	
		this.sapSystemNumber = sapSystemNumber;
	}



	
	/**
	 * Gets the sap company code.
	 *
	 * @return the sap company code
	 */
	public String getSapCompanyCode() {
	
		return sapCompanyCode;
	}



	
	/**
	 * Sets the sap company code.
	 *
	 * @param sapCompanyCode the new sap company code
	 */
	public void setSapCompanyCode(String sapCompanyCode) {
	
		this.sapCompanyCode = sapCompanyCode;
	}



	/**
	 * Gets the security id source.
	 *
	 * @return the security id source
	 */
	public String getSecurityIDSource() {
	
		return securityIDSource;
	}

	
	/**
	 * Sets the security id source.
	 *
	 * @param securityIDSource the new security id source
	 */
	public void setSecurityIDSource(String securityIDSource) {
	
		this.securityIDSource = securityIDSource;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
	
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
	
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon()
	{
		return "/net/sourceforge/fixagora/sap/client/view/images/16x16/sapimport.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		return "/net/sourceforge/fixagora/sap/client/view/images/24x24/sapimport.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "SAP\u00ae Trade Export";
	}
	


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.sap.server.control.component.SAPTradeCaptureComponentHandler";
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();
								
	}
	
}
