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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 * The Class FIXAcceptor.
 */
@Entity
public class FIXAcceptor extends AbstractAcceptor {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum MarketDataType.
	 */
	public enum MarketDataType {
		
		/** The unsubscribed incremental refresh. */
		UNSUBSCRIBED_INCREMENTAL_REFRESH, 
 /** The unsubscribed full refresh. */
 UNSUBSCRIBED_FULL_REFRESH, 
 /** The maket data request. */
 MAKET_DATA_REQUEST
	}

	@Column(unique = false, nullable = false)
	private String socketAdress = null;
	
	@Column(unique = false, nullable = false)
	private String dataDictionary = null;
	
	@Column(unique = false, nullable = false)
	private Integer socketPort = null;
	

	@Column(unique = false, nullable = false)
	private String senderCompID = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
	@OrderBy(value="id asc")
    @JoinColumn(name="fixacceptor_id", nullable=false)
	private List<FIXAcceptorTarget> acceptorTargets;
		
	@Column(unique = false, nullable = false)
	private Boolean marketDataRequest = null;
	
	@Column(unique = false, nullable = false)
	private MarketDataType marketDataType = null;
	
	@Column(unique = false, nullable = true)
	private String partyID = null;
	
	@Column(unique = false, nullable = true)
	private Integer partyRole = null;
	
	@Column(unique = false, nullable = true)
	private String partyIDSource = null;
		
	@Column(unique = false, nullable = false)
	private Boolean persistMessage=false;
	
	@Transient
	private Set<String> openSessions = new HashSet<String>();		
	
	
	/**
	 * Gets the persist message.
	 *
	 * @return the persist message
	 */
	public Boolean getPersistMessage() {
	
		return persistMessage;
	}


	
	/**
	 * Sets the persist message.
	 *
	 * @param persistMessage the new persist message
	 */
	public void setPersistMessage(Boolean persistMessage) {
	
		this.persistMessage = persistMessage;
	}


	

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.basis.server.control.component.FIXAcceptorComponentHandler";
	}		
	
	

	/**
	 * Gets the party id.
	 *
	 * @return the party id
	 */
	public String getPartyID() {
	
		return partyID;
	}

	
	/**
	 * Sets the party id.
	 *
	 * @param partyID the new party id
	 */
	public void setPartyID(String partyID) {
	
		this.partyID = partyID;
	}


	
	/**
	 * Gets the party role.
	 *
	 * @return the party role
	 */
	public Integer getPartyRole() {
	
		return partyRole;
	}


	
	/**
	 * Sets the party role.
	 *
	 * @param partyRole the new party role
	 */
	public void setPartyRole(Integer partyRole) {
	
		this.partyRole = partyRole;
	}


	
	/**
	 * Gets the party id source.
	 *
	 * @return the party id source
	 */
	public String getPartyIDSource() {
	
		return partyIDSource;
	}


	
	/**
	 * Sets the party id source.
	 *
	 * @param partyIDSource the new party id source
	 */
	public void setPartyIDSource(String partyIDSource) {
	
		this.partyIDSource = partyIDSource;
	}


	/**
	 * Gets the market data type.
	 *
	 * @return the market data type
	 */
	public MarketDataType getMarketDataType() {
	
		return marketDataType;
	}





	
	/**
	 * Sets the market data type.
	 *
	 * @param marketDataType the new market data type
	 */
	public void setMarketDataType(MarketDataType marketDataType) {
	
		this.marketDataType = marketDataType;
	}





	/**
	 * Gets the market data request.
	 *
	 * @return the market data request
	 */
	public Boolean getMarketDataRequest() {
	
		return marketDataRequest;
	}




	
	/**
	 * Sets the market data request.
	 *
	 * @param marketDataRequest the new market data request
	 */
	public void setMarketDataRequest(Boolean marketDataRequest) {
	
		this.marketDataRequest = marketDataRequest;
	}




	/**
	 * Gets the acceptor targets.
	 *
	 * @return the acceptor targets
	 */
	public List<FIXAcceptorTarget> getAcceptorTargets() {
	
		if(acceptorTargets==null)
			acceptorTargets = new ArrayList<FIXAcceptorTarget>();
		return acceptorTargets;
	}





	
	/**
	 * Sets the acceptor targets.
	 *
	 * @param acceptorTargets the new acceptor targets
	 */
	public void setAcceptorTargets(List<FIXAcceptorTarget> acceptorTargets) {
	
		this.acceptorTargets = acceptorTargets;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor#getDataDictionary()
	 */
	public String getDataDictionary() {
	
		return dataDictionary;
	}



	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor#setDataDictionary(java.lang.String)
	 */
	public void setDataDictionary(String dataDictionary) {
	
		this.dataDictionary = dataDictionary;
	}



	/**
	 * Gets the socket adress.
	 *
	 * @return the socket adress
	 */
	public String getSocketAdress() {
	
		return socketAdress;
	}


	
	/**
	 * Sets the socket adress.
	 *
	 * @param socketAdress the new socket adress
	 */
	public void setSocketAdress(String socketAdress) {
	
		this.socketAdress = socketAdress;
	}


	
	/**
	 * Gets the socket port.
	 *
	 * @return the socket port
	 */
	public Integer getSocketPort() {
	
		return socketPort;
	}


	
	/**
	 * Sets the socket port.
	 *
	 * @param socketPort the new socket port
	 */
	public void setSocketPort(Integer socketPort) {
	
		this.socketPort = socketPort;
	}


	
	/**
	 * Gets the sender comp id.
	 *
	 * @return the sender comp id
	 */
	public String getSenderCompID() {
	
		return senderCompID;
	}


	
	/**
	 * Sets the sender comp id.
	 *
	 * @param senderCompID the new sender comp id
	 */
	public void setSenderCompID(String senderCompID) {
	
		this.senderCompID = senderCompID;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "FIX Acceptor";
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();
		for(FIXAcceptorTarget acceptorTarget: acceptorTargets)
			acceptorTarget.makeEager();
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#setTransientValues(net.sourceforge.fixagora.basis.shared.model.persistence.TransientValueSetter)
	 */
	@Override
	public void setTransientValues(TransientValueSetter transientValueSetter) {

		super.setTransientValues(transientValueSetter);
		openSessions  = transientValueSetter.getOpenSessions(this);
	}

	
	/**
	 * Gets the open sessions.
	 *
	 * @return the open sessions
	 */
	public Set<String> getOpenSessions() {
	
		return openSessions;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	@Override
	public String getAdditionalTreeText() {

		if(started==0)
			return "Stopped";
		if(started==1)
			return "Starting";
		if(started==3)
			return "Stopping";
		if(started==2)
			return "Started ("+openSessions.size()+"/"+getAcceptorTargets().size()+" connected)";
		return null;
	}
	
	

}
