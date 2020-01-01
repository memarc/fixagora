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

import javax.persistence.Entity;

import org.jquantlib.time.Calendar;
import org.jquantlib.time.calendars.Argentina;
import org.jquantlib.time.calendars.Argentina.Market;
import org.jquantlib.time.calendars.Australia;
import org.jquantlib.time.calendars.Brazil;
import org.jquantlib.time.calendars.Canada;
import org.jquantlib.time.calendars.China;
import org.jquantlib.time.calendars.CzechRepublic;
import org.jquantlib.time.calendars.Denmark;
import org.jquantlib.time.calendars.Finland;
import org.jquantlib.time.calendars.Germany;
import org.jquantlib.time.calendars.HongKong;
import org.jquantlib.time.calendars.Hungary;
import org.jquantlib.time.calendars.Iceland;
import org.jquantlib.time.calendars.India;
import org.jquantlib.time.calendars.Indonesia;
import org.jquantlib.time.calendars.Italy;
import org.jquantlib.time.calendars.Japan;
import org.jquantlib.time.calendars.Mexico;
import org.jquantlib.time.calendars.NewZealand;
import org.jquantlib.time.calendars.Norway;
import org.jquantlib.time.calendars.NullCalendar;
import org.jquantlib.time.calendars.Poland;
import org.jquantlib.time.calendars.SaudiArabia;
import org.jquantlib.time.calendars.Singapore;
import org.jquantlib.time.calendars.Slovakia;
import org.jquantlib.time.calendars.SouthAfrica;
import org.jquantlib.time.calendars.SouthKorea;
import org.jquantlib.time.calendars.Sweden;
import org.jquantlib.time.calendars.Switzerland;
import org.jquantlib.time.calendars.Taiwan;
import org.jquantlib.time.calendars.Target;
import org.jquantlib.time.calendars.Turkey;
import org.jquantlib.time.calendars.Ukraine;
import org.jquantlib.time.calendars.UnitedKingdom;
import org.jquantlib.time.calendars.UnitedStates;

/**
 * The Class BankCalendar.
 */
@Entity
public class BankCalendar extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Bank Calendar";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon()
	{
		if(getName().startsWith("Argentina"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-ar.png";
		if(getName().startsWith("Australia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-au.png";
		if(getName().startsWith("Brazil"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-br.png";
		if(getName().startsWith("Canada"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-ca.png";
		if(getName().startsWith("China"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-cn.png";
		if(getName().startsWith("Czech Republic"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-cz.png";
		if(getName().startsWith("Denmark"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-dk.png";
		if(getName().startsWith("Finland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-fi.png";
		if(getName().startsWith("Germany"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-de.png";
		if(getName().startsWith("Hong Kong"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-hk.png";
		if(getName().startsWith("Hungary"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-hu.png";
		if(getName().startsWith("Iceland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-is.png";
		if(getName().startsWith("India"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-in.png";
		if(getName().startsWith("Indonesia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-id.png";
		if(getName().startsWith("Italy"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-it.png";
		if(getName().startsWith("Japan"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-jp.png";
		if(getName().startsWith("Mexico"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-mx.png";
		if(getName().startsWith("New Zealand"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-nz.png";
		if(getName().startsWith("Norway"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-no.png";
		if(getName().startsWith("Poland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-pl.png";
		if(getName().startsWith("Saudi Arabia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-sa.png";
		if(getName().startsWith("Singapore"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-sg.png";
		if(getName().startsWith("Slovakia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-sk.png";
		if(getName().startsWith("South Africa"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-za.png";
		if(getName().startsWith("South Korea"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-kr.png";
		if(getName().startsWith("Sweden"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-se.png";
		if(getName().startsWith("Switzerland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-ch.png";
		if(getName().startsWith("Taiwan"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-tw.png";
		if(getName().startsWith("Target"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-eu.png";
		if(getName().startsWith("Turkey"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-tr.png";
		if(getName().startsWith("Ukraine"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-ua.png";
		if(getName().startsWith("United Kingdom"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-gb.png";
		if(getName().startsWith("United States"))
			return "/net/sourceforge/fixagora/basis/client/view/images/16x16/flag-us.png";
		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/home.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		if(getName().startsWith("Argentina"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-ar.png";
		if(getName().startsWith("Australia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-au.png";
		if(getName().startsWith("Brazil"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-br.png";
		if(getName().startsWith("Canada"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-ca.png";
		if(getName().startsWith("China"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-cn.png";
		if(getName().startsWith("Czech Republic"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-cz.png";
		if(getName().startsWith("Denmark"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-dk.png";
		if(getName().startsWith("Finland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-fi.png";
		if(getName().startsWith("Germany"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-de.png";
		if(getName().startsWith("Hong Kong"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-hk.png";
		if(getName().startsWith("Hungary"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-hu.png";
		if(getName().startsWith("Iceland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-is.png";
		if(getName().startsWith("India"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-in.png";
		if(getName().startsWith("Indonesia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-id.png";
		if(getName().startsWith("Italy"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-it.png";
		if(getName().startsWith("Japan"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-jp.png";
		if(getName().startsWith("Mexico"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-mx.png";
		if(getName().startsWith("New Zealand"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-nz.png";
		if(getName().startsWith("Norway"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-no.png";
		if(getName().startsWith("Poland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-pl.png";
		if(getName().startsWith("Saudi Arabia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-sa.png";
		if(getName().startsWith("Singapore"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-sg.png";
		if(getName().startsWith("Slovakia"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-sk.png";
		if(getName().startsWith("South Africa"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-za.png";
		if(getName().startsWith("South Korea"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-kr.png";
		if(getName().startsWith("Sweden"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-se.png";
		if(getName().startsWith("Switzerland"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-ch.png";
		if(getName().startsWith("Taiwan"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-tw.png";
		if(getName().startsWith("Target"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-eu.png";
		if(getName().startsWith("Turkey"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-tr.png";
		if(getName().startsWith("Ukraine"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-ua.png";
		if(getName().startsWith("United Kingdom"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-gb.png";
		if(getName().startsWith("United States"))
			return "/net/sourceforge/fixagora/basis/client/view/images/24x24/flag-us.png";
		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/home.png";
	}
	
	/**
	 * Gets the calendar.
	 *
	 * @return the calendar
	 */
	public Calendar getCalendar()
	{
		if(getName().equals("Argentina (Buenos Aires Stock Exchange)"))
			return new Argentina(Market.MERVAL);
		if(getName().equals("Australia"))
			return new Australia();
		if(getName().equals("Brazil (Generic Settlement)"))
			return new Brazil(org.jquantlib.time.calendars.Brazil.Market.SETTLEMENT);
		if(getName().equals("Brazil (Bolsa de Valores de Sao Paulo)"))
			return new Brazil(org.jquantlib.time.calendars.Brazil.Market.BOVESPA);
		if(getName().equals("Canada (Generic Settlement)"))
			return new Canada(org.jquantlib.time.calendars.Canada.Market.SETTLEMENT);
		if(getName().equals("Canada (Toronto Stock Exchange)"))
			return new Canada(org.jquantlib.time.calendars.Canada.Market.TSX);
		if(getName().equals("China (Shanghai Stock Exchange)"))
			return new China(org.jquantlib.time.calendars.China.Market.SSE);
		if(getName().equals("Czech Republic (Prague Stock Exchange)"))
			return new CzechRepublic(org.jquantlib.time.calendars.CzechRepublic.Market.PSE);
		if(getName().equals("Denmark"))
			return new Denmark();
		if(getName().equals("Finland"))
			return new Finland();
		if(getName().equals("Germany (Generic Settlement)"))
			return new Germany(org.jquantlib.time.calendars.Germany.Market.Settlement);
		if(getName().equals("Germany (Eurex)"))
			return new Germany(org.jquantlib.time.calendars.Germany.Market.Eurex);
		if(getName().equals("Germany (Frankfurt Stock Exchange)"))
			return new Germany(org.jquantlib.time.calendars.Germany.Market.FrankfurtStockExchange);
		if(getName().equals("Germany (Xetra)"))
			return new Germany(org.jquantlib.time.calendars.Germany.Market.Xetra);
		if(getName().equals("Hong Kong (Hong Kong Stock Exchange)"))
			return new HongKong(org.jquantlib.time.calendars.HongKong.Market.HKEx);
		if(getName().equals("Hungary"))
			return new Hungary();
		if(getName().equals("Iceland (Iceland Stock Exchange)"))
			return new Iceland(org.jquantlib.time.calendars.Iceland.Market.ICEX);
		if(getName().equals("India (National Stock Exchange)"))
			return new India(org.jquantlib.time.calendars.India.Market.NSE);
		if(getName().equals("Indonesia (Jakarta Stock Exchange)"))
			return new Indonesia(org.jquantlib.time.calendars.Indonesia.Market.JSX);
		if(getName().equals("Italy (Milan Stock Exchange)"))
			return new Italy(org.jquantlib.time.calendars.Italy.Market.Exchange);
		if(getName().equals("Italy (Generic Settlement)"))
			return new Italy(org.jquantlib.time.calendars.Italy.Market.Settlement);
		if(getName().equals("Japan"))
			return new Japan();
		if(getName().equals("Mexico"))
			return new Mexico();
		if(getName().equals("New Zealand"))
			return new NewZealand();
		if(getName().equals("Norway"))
			return new Norway();
		if(getName().equals("Poland"))
			return new Poland();
		if(getName().equals("Saudi Arabia (Tadawul Financial Market)"))
			return new SaudiArabia();
		if(getName().equals("Singapore"))
			return new Singapore();
		if(getName().equals("Slovakia (Bratislava Stock Exchange)"))
			return new Slovakia();
		if(getName().equals("South Africa"))
			return new SouthAfrica();
		if(getName().equals("South Korea (Korea Exchange)"))
			return new SouthKorea(org.jquantlib.time.calendars.SouthKorea.Market.KRX);
		if(getName().equals("South Korea (Generic Settlement)"))
			return new SouthKorea(org.jquantlib.time.calendars.SouthKorea.Market.Settlement);
		if(getName().equals("Sweden"))
			return new Sweden();
		if(getName().equals("Switzerland"))
			return new Switzerland();
		if(getName().equals("Taiwan"))
			return new Taiwan();
		if(getName().equals("Target"))
			return new Target();
		if(getName().equals("Turkey"))
			return new Turkey();
		if(getName().equals("Ukraine"))
			return new Ukraine();
		if(getName().equals("United Kingdom (Generic Settlement)"))
			return new UnitedKingdom(org.jquantlib.time.calendars.UnitedKingdom.Market.Settlement);
		if(getName().equals("United Kingdom (London Stock Exchange)"))
			return new UnitedKingdom(org.jquantlib.time.calendars.UnitedKingdom.Market.Exchange);
		if(getName().equals("United Kingdom (London Metals Exchange)"))
			return new UnitedKingdom(org.jquantlib.time.calendars.UnitedKingdom.Market.Metals);
		if(getName().equals("United States (Generic Settlement)"))
			return new UnitedStates(org.jquantlib.time.calendars.UnitedStates.Market.SETTLEMENT);
		if(getName().equals("United States (Government Bond Market)"))
			return new UnitedStates(org.jquantlib.time.calendars.UnitedStates.Market.GOVERNMENTBOND);
		if(getName().equals("United States (North American Energy Reliability Council)"))
			return new UnitedStates(org.jquantlib.time.calendars.UnitedStates.Market.NERC);
		if(getName().equals("United States (New York Stock Exchange)"))
			return new UnitedStates(org.jquantlib.time.calendars.UnitedStates.Market.NYSE);
		return new NullCalendar();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#isEditable()
	 */
	public boolean isEditable()
	{
		
		return true;
	}
	
}
