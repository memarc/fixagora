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

import java.awt.Color;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * The Class AbstractInitiator.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractInitiator extends AbstractBusinessComponent {

	private static final long serialVersionUID = 1L;
	
	@Column(unique = false, nullable = false)
	private Boolean connectAtStartup = null;
	
	@Column(unique = false, nullable = false)
	private String marketName = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private AbstractInitiator route = null;
	
	@Column(unique = false, nullable = true)
	private String securityIDSource = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private Counterparty counterparty = null;
		
	
	/**
	 * Gets the counterparty.
	 *
	 * @return the counterparty
	 */
	public Counterparty getCounterparty() {
	
		return counterparty;
	}

	
	/**
	 * Sets the counterparty.
	 *
	 * @param counterparty the new counterparty
	 */
	public void setCounterparty(Counterparty counterparty) {
	
		this.counterparty = counterparty;
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
	 * Gets the route.
	 *
	 * @return the route
	 */
	public AbstractInitiator getRoute() {
	
		return route;
	}

	
	/**
	 * Sets the route.
	 *
	 * @param route the new route
	 */
	public void setRoute(AbstractInitiator route) {
	
		this.route = route;
	}


	/**
	 * Gets the market name.
	 *
	 * @return the market name
	 */
	public String getMarketName() {
	
		return marketName;
	}





	
	/**
	 * Sets the market name.
	 *
	 * @param marketName the new market name
	 */
	public void setMarketName(String marketName) {
	
		this.marketName = marketName;
	}






	/**
	 * Gets the connect at startup.
	 *
	 * @return the connect at startup
	 */
	public boolean getConnectAtStartup() {

		if (connectAtStartup == null)
			connectAtStartup = false;
		return connectAtStartup;
	}

	/**
	 * Sets the connect at startup.
	 *
	 * @param connectAtStartup the new connect at startup
	 */
	public void setConnectAtStartup(Boolean connectAtStartup) {

		this.connectAtStartup = connectAtStartup;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getDataDictionary()
	 */
	public String getDataDictionary() {

		return dataDictionary;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#setDataDictionary(java.lang.String)
	 */
	public void setDataDictionary(String dataDictionary) {

		this.dataDictionary = dataDictionary;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "FIX Initiator";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeTextColor()
	 */
	public Color getAdditionalTreeTextColor() {
		
		if (started == 3)
			return super.getAdditionalTreeTextColor();
		if (started == 2)
			return new Color(51,153,0);
		if (started == 1)
			return super.getAdditionalTreeTextColor();
		return new Color(204,0,0);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getAdditionalTreeText()
	 */
	public String getAdditionalTreeText() {
		
		if (started == 3)
			return "Stopping";
		if (started == 2)
			return "Started";
		if (started == 1)
			return "Starting";
		return "Stopped";
		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon() {

		if (started == 3)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_pending.png";
		if (started == 2)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_online.png";
		if (started == 1)
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_pending.png";
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/interface_offline.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/connect_no.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();

		if(counterparty!=null)
			counterparty.getName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#isStartable()
	 */
	@Override
	public boolean isStartable() {

		return true;
	}

}
