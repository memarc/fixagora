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
package net.sourceforge.fixagora.basis.shared.control;

import java.util.Date;

import net.sourceforge.fixagora.basis.shared.control.analysis.AnalysisToolPak;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAttribute;

/**
 * The Class YieldCalculator.
 */
public class YieldCalculator {

	/**
	 * The Enum CalcMethod.
	 */
	public enum CalcMethod {
		
		/** The default. */
		DEFAULT, 
 /** The disc. */
 DISC, 
 /** The mat. */
 MAT, 
 /** The oddf. */
 ODDF, 
 /** The oddl. */
 ODDL, 
 /** The tbill. */
 TBILL
	}

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
	 * @param calcMethod the calc method
	 * @return the yield
	 */
	public Double getYield(FSecurity fSecurity, double price, Date settlementDate, int basis, CalcMethod calcMethod) {

		try {

			Date maturity = fSecurity.getMaturity();

			Date issue = null;

			Date firstCoupon = null;

			double rate = Double.NaN;

			double redemption = 100;

			int frequency = 2;

			for (SecurityAttribute securityAttribute : fSecurity.getSecurityDetails().getSecurityAttribute())
				if (securityAttribute.getAttributeType() == 8)
					frequency = Integer.parseInt(securityAttribute.getAttributeValue());

			switch (calcMethod) {
				case DEFAULT:
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					return 100d * AnalysisToolPak.getYield(settlementDate, maturity, rate, price, redemption, frequency, basis);
				case DISC:
					return 100d * AnalysisToolPak.getYieldDisc(settlementDate, maturity, price, redemption, basis);
				case MAT:
					issue = fSecurity.getSecurityDetails().getIssueDate();
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					return 100d * AnalysisToolPak.getYieldMat(settlementDate, maturity, issue, rate, price, basis);
				case ODDF:
					issue = fSecurity.getSecurityDetails().getIssueDate();
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					firstCoupon = fSecurity.getSecurityDetails().getInterestAccrualDate();
					
					if(firstCoupon.after(settlementDate))
						return 100d * AnalysisToolPak.getOddFYield(settlementDate, maturity, issue, firstCoupon, rate, price, redemption, frequency, basis);
					else
						return 100d * AnalysisToolPak.getYield(settlementDate, maturity, rate, price, redemption, frequency, basis);
					
				case ODDL:
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					firstCoupon = fSecurity.getSecurityDetails().getInterestAccrualDate();
					return 100d * AnalysisToolPak.getOddLYield(settlementDate, maturity, firstCoupon, rate, price, redemption, frequency, basis);
				case TBILL:
					return 100d * AnalysisToolPak.getTBillYield(settlementDate, maturity, price);
				default:
					return null;
			}

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
	 * @param calcMethod the calc method
	 * @return the price
	 */
	public Double getPrice(FSecurity fSecurity, double yield, Date settlementDate, int basis, CalcMethod calcMethod) {

		try {
			Date maturity = fSecurity.getMaturity();

			double rate = Double.NaN;

			double redemption = 100;

			Date issue = null;

			Date firstCoupon = null;

			int frequency = 2;

			for (SecurityAttribute securityAttribute : fSecurity.getSecurityDetails().getSecurityAttribute())
				if (securityAttribute.getAttributeType() == 8)
					frequency = Integer.parseInt(securityAttribute.getAttributeValue());

			switch (calcMethod) {
				case DEFAULT:
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					return AnalysisToolPak.getPrice(settlementDate, maturity, rate, yield/100d, redemption, frequency, basis);
				case DISC:
					return AnalysisToolPak.getPriceDisc(settlementDate, maturity, rate, redemption, basis);
				case MAT:
					issue = fSecurity.getSecurityDetails().getIssueDate();
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					return AnalysisToolPak.getPriceMat(settlementDate, maturity, issue, rate, yield/100d, basis);
				case ODDF:
					issue = fSecurity.getSecurityDetails().getIssueDate();
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					firstCoupon = fSecurity.getSecurityDetails().getInterestAccrualDate();
					if(firstCoupon.after(settlementDate))
						return AnalysisToolPak.getOddFPrice(settlementDate, maturity, issue, firstCoupon, rate, yield/100d, redemption, frequency, basis);
					else
						return AnalysisToolPak.getPrice(settlementDate, maturity, rate, yield/100d, redemption, frequency, basis);
				case ODDL:
					rate = fSecurity.getSecurityDetails().getCouponRate() / 100d;
					firstCoupon = fSecurity.getSecurityDetails().getInterestAccrualDate();
					return AnalysisToolPak.getOddLPrice(settlementDate, maturity, firstCoupon, rate, yield/100d, redemption, frequency, basis);
				case TBILL:
					return AnalysisToolPak.getTBillPrice(settlementDate, maturity, rate);
				default:
					return null;
			}

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
	public static YieldCalculator getInstance() {

		if (yieldCalculator == null)
			yieldCalculator = new YieldCalculator();
		return yieldCalculator;
	}

}
