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
package net.sourceforge.agora.simulator.control;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import net.sourceforge.agora.simulator.control.analysis.AnalysisToolPak;
import net.sourceforge.agora.simulator.model.Security;
import net.sourceforge.agora.simulator.model.SecurityAttribute;


/**
 * The Class YieldCalculator.
 */
public class YieldCalculator {
	
	private static YieldCalculator yieldCalculator = null;
	
	private YieldCalculator() {

		super();

	}

	/**
	 * Gets the yield.
	 *
	 * @param fSecurity the f security
	 * @param price the price
	 * @param settlementDate the settlement date
	 * @param basis the basis
	 * @return the yield
	 */
	public Double getYield(Security fSecurity, double price, Date settlementDate, int basis) {

			try {
				Date maturity = fSecurity.getMaturity();

				double rate = fSecurity.getSecurityDetails().getCouponRate()/100d;

				double redemption = 100;

				int frequency = 2;

				for (SecurityAttribute securityAttribute : fSecurity.getSecurityDetails().getSecurityAttribute())
					if (securityAttribute.getAttributeType() == 8)
						frequency = Integer.parseInt(securityAttribute.getAttributeValue());

				return 100d*AnalysisToolPak.getYield(settlementDate, maturity, rate, price, redemption, frequency, basis);
			}
			catch (Exception e) {
				return null;
			}
	}
	
	/**
	 * Gets the price.
	 *
	 * @param fSecurity the f security
	 * @param yield the yield
	 * @param settlementDate the settlement date
	 * @param basis the basis
	 * @return the price
	 */
	public Double getPrice(Security fSecurity, double yield, Date settlementDate, int basis) {

			try {
				Date maturity = fSecurity.getMaturity();

				double rate = fSecurity.getSecurityDetails().getCouponRate()/100d;

				double redemption = 100;

				int frequency = 2;

				for (SecurityAttribute securityAttribute : fSecurity.getSecurityDetails().getSecurityAttribute())
					if (securityAttribute.getAttributeType() == 8)
						frequency = Integer.parseInt(securityAttribute.getAttributeValue());

				BigDecimal bigDecimal = new BigDecimal(AnalysisToolPak.getPrice(settlementDate, maturity, rate, yield/100d, redemption, frequency, basis));
				return bigDecimal.setScale(3,  RoundingMode.HALF_EVEN).doubleValue();

			}
			catch (Exception e) {
				return null;
			}
	}


	/**
	 * Gets the single instance of YieldCalculator.
	 *
	 * @return single instance of YieldCalculator
	 */
	public static YieldCalculator getInstance()
	{
		if(yieldCalculator==null)
			yieldCalculator = new YieldCalculator();
		return yieldCalculator;
	}
	
}
