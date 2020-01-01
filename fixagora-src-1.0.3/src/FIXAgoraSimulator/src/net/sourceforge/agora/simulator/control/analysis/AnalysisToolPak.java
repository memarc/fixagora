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
package net.sourceforge.agora.simulator.control.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.DateUtil;

/**
 * The Class AnalysisToolPak.
 */
public class AnalysisToolPak {

	/**
	 * Gets the coup pcd.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coup pcd
	 * @throws Exception the exception
	 */
	public static double getCoupPcd(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate result = getCoupPcd(settlementDate, maturityDate, frequency).getDate();

		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, result.getYear());
		calendar.set(Calendar.MONTH, result.getMonth() - 1);
		calendar.set(Calendar.DAY_OF_MONTH, result.getOriginalDay());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return DateUtil.getExcelDate(calendar.getTime());
	}

	/**
	 * Gets the coup ncd.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coup ncd
	 * @throws Exception the exception
	 */
	public static double getCoupNcd(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate result = getCoupNcd(settlementDate, maturityDate, frequency).getDate();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, result.getYear());
		calendar.set(Calendar.MONTH, result.getMonth() - 1);
		calendar.set(Calendar.DAY_OF_MONTH, result.getOriginalDay());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return DateUtil.getExcelDate(calendar.getTime());
	}

	/**
	 * Gets the coup num.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coup num
	 * @throws Exception the exception
	 */
	public static double getCoupNum(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate coupPcd = getCoupPcd(settlementDate, maturityDate, frequency).getDate();
		int month = (maturityDate.getYear() - coupPcd.getYear()) * 12 + maturityDate.getMonth() - coupPcd.getMonth();
		return month * frequency / 12;

	}

	/**
	 * Gets the coupon day bs.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coupon day bs
	 * @throws Exception the exception
	 */
	public static double getCouponDayBS(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate coupPcd = getCoupPcd(settlementDate, maturityDate, frequency).getDate();
		return AnalysisDate.getDiff(coupPcd, settlementDate, true);

	}

	/**
	 * Gets the coup days.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coup days
	 * @throws Exception the exception
	 */
	public static double getCoupDays(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		// For calendar "basis" 1, this frequently produces incorrect numbers in
		// Excel for semi-annual and quarterly end-of-month dates. The following
		// is a typical example. In Excel, the result is:
		//
		// COUPDAYS("12/30/1994", "9/30/1997", 4, 1) = 90
		//
		// The correct answer is 92, which can be seen from the following series
		// of formulas
		//
		// COUPDAYBS("12/30/1994", "9/30/1997", 4, 1) = 91
		//
		// COUPDAYSNC("12/30/1994", "9/30/1997", 4, 1) = 1
		//
		// COUPPCD("12/30/1994", "9/30/1997", 4, 1) = 34607 (or 9/30/94)
		//
		// COUPNCD("12/30/1994", "9/30/1997", 4, 1) = 34699 (or 12/31/94)
		//
		// see: http://www.mit.edu/~mbarker/formula1/function_readme.html

		if (basis == 1) {

			AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
			AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
			AnalysisDate coupPcd = getCoupPcd(settlementDate, maturityDate, frequency);
			AnalysisDate nextDate = new AnalysisDate(coupPcd);
			nextDate.addMonths(12 / frequency);
			return AnalysisDate.getDiff(nextDate, coupPcd, false);
		}

		return AnalysisDate.getDaysInYear(null, basis) / (double) frequency;
	}

	/**
	 * Gets the coup days nc.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the coup days nc
	 * @throws Exception the exception
	 */
	public static double getCoupDaysNC(Date settlement, Date maturity, int frequency, int basis) throws Exception {

		if ((basis != 0) && (basis != 4)) {
			AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
			AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
			AnalysisDate coupNcd = getCoupNcd(settlementDate, maturityDate, frequency);
			return AnalysisDate.getDiff(coupNcd, settlementDate);
		}

		return getCoupDays(settlement, maturity, frequency, basis) - getCouponDayBS(settlement, maturity, frequency, basis);

	}

	/**
	 * Gets the disc.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param price the price
	 * @param redemption the redemption
	 * @param basis the basis
	 * @return the disc
	 */
	public static double getDisc(Date settlement, Date maturity, double price, double redemption, int basis) {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		double disc = (redemption - price) / (redemption * AnalysisDate.getYearfrac(settlementDate, maturityDate, basis));
		return disc;
	}

	/**
	 * Gets the accr int m.
	 *
	 * @param issue the issue
	 * @param settlement the settlement
	 * @param rate the rate
	 * @param redemption the redemption
	 * @param basis the basis
	 * @return the accr int m
	 */
	public static double getAccrIntM(Date issue, Date settlement, double rate, double redemption, int basis) {

		if (basis != 3)
			return getYearfrac(issue, settlement, basis) * rate * redemption;

		double fRet = getYearfrac(issue, settlement, basis) * rate * redemption * 360d / 365d;

		return fRet;

	}

	/**
	 * Gets the price disc.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param discount the discount
	 * @param redemption the redemption
	 * @param basis the basis
	 * @return the price disc
	 * @throws Exception the exception
	 */
	public static double getPriceDisc(Date settlement, Date maturity, double discount, double redemption, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		if (maturityDate.le(settlementDate))
			throw new Exception();
		return redemption - discount * redemption * AnalysisDate.getYearfrac(settlementDate, maturityDate, basis);
	}

	/**
	 * Gets the yield disc.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param price the price
	 * @param redemption the redemption
	 * @param basis the basis
	 * @return the yield disc
	 * @throws Exception the exception
	 */
	public static double getYieldDisc(Date settlement, Date maturity, double price, double redemption, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);

		if (maturityDate.le(settlementDate))
			throw new Exception();

		return (redemption / price - 1d) / AnalysisDate.getYearfrac(settlementDate, maturityDate, basis);
	}

	/**
	 * Gets the price.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param rate the rate
	 * @param yield the yield
	 * @param redemption the redemption
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the price
	 * @throws Exception the exception
	 */
	public static double getPrice(Date settlement, Date maturity, double rate, double yield, double redemption, int frequency, int basis) throws Exception {

		double coupDays = getCoupDays(settlement, maturity, frequency, basis);

		double couponDayNC = 0;

		if (basis == 2) {
			double coupPcd = getCoupPcd(settlement, maturity, frequency, basis);
			Date coupPcdDate = DateUtil.getJavaDate(coupPcd);

			if (frequency == 4) {

				couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis)
						+ (90 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1));

			}
			else
				couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis) - getCoupDaysNC(coupPcdDate, maturity, frequency, 1) % 30;
		}
		else if (basis == 3) {

			double frac = 0;
			if (frequency == 4)
				frac = 1d / frequency + 91 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1);
			else if (frequency == 2)
				frac = 1d / frequency + 182 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1);
			else
				frac = 1d / frequency - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1) % 182;
			couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis) + frac;

		}
		else
			couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis);

		double coupNum = getCoupNum(settlement, maturity, frequency, basis);
		double coupDayBS = getCouponDayBS(settlement, maturity, frequency, basis);

		if (coupNum > 1) {

			double dsc = couponDayNC / coupDays;

			double price = redemption / (Math.pow(1d + yield / frequency, coupNum - 1d + dsc));

			price -= 100d * rate / frequency * coupDayBS / coupDays;

			double rateTerm = 100d * rate / frequency;
			double yieldTerm = 1d + yield / frequency;

			for (double fK = 0.0; fK < coupNum; fK++) {
				price += rateTerm / Math.pow(yieldTerm, fK + dsc);
			}
			return price;

		}
		else {

			return 100d * ((redemption / 100d + rate / frequency) / (yield / ((frequency * coupDays) / couponDayNC) + 1) - ((coupDayBS / coupDays) * (rate / frequency)));
		}

	}

	/**
	 * Gets the yield.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param rate the rate
	 * @param price the price
	 * @param redemption the redemption
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the yield
	 * @throws Exception the exception
	 */
	public static double getYield(Date settlement, Date maturity, double rate, double price, double redemption, int frequency, int basis) throws Exception {

		// System.err.println(settlement);
		// System.err.println(maturity);
		// System.err.println(rate);
		// System.err.println(price);
		// System.err.println(redemption);
		// System.err.println(frequency);
		// System.err.println(basis);

		double coupNum = getCoupNum(settlement, maturity, frequency, basis);

		if (coupNum > 1) {
			double priceNew = 0;
			double yield1 = -1;
			double yield2 = 1;
			double price1 = getPrice(settlement, maturity, rate, yield1, redemption, frequency, basis);
			double price2 = getPrice(settlement, maturity, rate, yield2, redemption, frequency, basis);
			double yieldNew = (yield2 - yield1) / 2d;

			if (price1 == Double.POSITIVE_INFINITY)
				;
			do {
				yield1 = yield1 / 2d;
				price1 = getPrice(settlement, maturity, rate, yield1, redemption, frequency, basis);
			}
			while (price1 == Double.POSITIVE_INFINITY || yield1 < -0.1);

			for (int i = 0; i < 100; i++) {
				priceNew = getPrice(settlement, maturity, rate, yieldNew, redemption, frequency, basis);

				if (price == price1)
					return yield1;

				if (price == price2)
					return yield2;

				if (price == priceNew)
					return yieldNew;

				if (price < price2) {
					yield2 = yield2 * 2d;
					price2 = getPrice(settlement, maturity, rate, yield2, redemption, frequency, basis);

					yieldNew = (yield2 - yield1) * 0.5;
				}
				else {
					if (price < priceNew) {
						yield1 = yieldNew;
						price1 = priceNew;
					}
					else {
						yield2 = yieldNew;
						price2 = priceNew;
					}

					yieldNew = yield2 - (yield2 - yield1) * ((price - price2) / (price1 - price2));
				}
			}

			if (Math.abs(price - priceNew) > price / 100d)
				throw new Exception();

			return yieldNew;
		}

		if (basis == 2 || basis == 3)
			basis = 1;

		double coupDays = getCoupDays(settlement, maturity, frequency, basis);

		double couponDayBS = getCouponDayBS(settlement, maturity, frequency, basis);

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate aDate = getCoupNcd(settlementDate, maturityDate, frequency);
		double couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis) + AnalysisDate.getDiff(aDate, settlementDate)
				- getCoupDaysNC(settlement, maturity, frequency, basis);

		return ((redemption / 100d + rate / frequency) - (price / 100d + (couponDayBS / coupDays * rate / frequency)))
				/ (price / 100 + (couponDayBS / coupDays * rate / frequency)) * frequency * coupDays / couponDayNC;

	}

	private static AnalysisDate getCoupPcd(AnalysisDate settlementDate, AnalysisDate maturityDate, int frequency) throws Exception {

		if (maturityDate.le(settlementDate))
			throw new Exception();

		AnalysisDate coupPcd = new AnalysisDate(maturityDate);

		coupPcd.setYear(settlementDate.getYear());
		if (coupPcd.lt(settlementDate))
			coupPcd.setYear(coupPcd.getYear() + 1);
		while (coupPcd.gt(settlementDate))
			coupPcd.addMonths(-12 / frequency);

		return coupPcd;
	}

	/**
	 * Gets the t bill price.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param rate the rate
	 * @return the t bill price
	 * @throws Exception the exception
	 */
	public static double getTBillPrice(Date settlement, Date maturity, double rate) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, 1);
		AnalysisDate maturityDate = new AnalysisDate(maturity, 1);

		double yearfrac = AnalysisDate.getYearfrac(settlementDate, maturityDate, 1);

		if (yearfrac > 1 || maturityDate.le(settlementDate))
			throw new Exception();

		return 100 * (1 - rate * AnalysisDate.getDiff(settlementDate, maturityDate) / 360d);

	}

	/**
	 * Gets the t bill yield.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param price the price
	 * @return the t bill yield
	 * @throws Exception the exception
	 */
	public static double getTBillYield(Date settlement, Date maturity, double price) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, 1);
		AnalysisDate maturityDate = new AnalysisDate(maturity, 1);

		double yearfrac = AnalysisDate.getYearfrac(settlementDate, maturityDate, 1);

		if (yearfrac > 1 || maturityDate.le(settlementDate))
			throw new Exception();

		return ((100d - price) / price) * (360d / AnalysisDate.getDiff(settlementDate, maturityDate));

	}

	/**
	 * Gets the t bill eq.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param discount the discount
	 * @return the t bill eq
	 * @throws Exception the exception
	 */
	public static double getTBillEq(Date settlement, Date maturity, double discount) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, 1);
		AnalysisDate maturityDate = new AnalysisDate(maturity, 1);

		double yearfrac = AnalysisDate.getYearfrac(settlementDate, maturityDate, 1);

		if (yearfrac > 1 || maturityDate.le(settlementDate))
			throw new Exception();

		double diff = AnalysisDate.getDiff(settlementDate, maturityDate);

		if (diff <= 182)
			return (365d * discount) / (360d - (discount * AnalysisDate.getDiff(settlementDate, maturityDate)));

		double price = getTBillPrice(settlement, maturity, discount);

		return (-2d * diff / 365d + 2 * Math.sqrt(Math.pow(diff / 365d, 2d) - (2d * diff / 365d - 1d) * (1d - 100d / price))) / (2d * diff / 365d - 1d);

	}

	/**
	 * Gets the price mat.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param issue the issue
	 * @param rate the rate
	 * @param yield the yield
	 * @param basis the basis
	 * @return the price mat
	 * @throws Exception the exception
	 */
	public static double getPriceMat(Date settlement, Date maturity, Date issue, double rate, double yield, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate issueDate = new AnalysisDate(issue, basis);

		if (maturityDate.le(settlementDate) || settlementDate.le(issueDate))
			throw new Exception();

		double dimDiff = AnalysisDate.getDiff(issueDate, maturityDate, false);
		double aDiff = AnalysisDate.getDiff(issueDate, settlementDate, false);

		double dsmDiff = dimDiff - aDiff;
		double daysInYear = 360;

		if (basis == 3)
			daysInYear = 365;

		if (basis == 1) {
			daysInYear = AnalysisDate.getAverageYearLength(issueDate.getYear(), settlementDate.getYear());
			if (daysInYear == 365.5 && aDiff <= 366) {
				if (AnalysisDate.shouldCountFeb29(issueDate, settlementDate))
					daysInYear = 366;
				else
					daysInYear = 365;
			}

		}

		double dimYearfrac = dimDiff / daysInYear;
		double dsmYearfrac = dsmDiff / daysInYear;
		double aYearfrac = aDiff / daysInYear;

		double pricemat = (100d + (dimYearfrac * rate * 100d)) / (1d + (dsmYearfrac * yield)) - (aYearfrac * rate * 100d);

		return pricemat;

	}
	
	/**
	 * Gets the yield mat.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param issue the issue
	 * @param rate the rate
	 * @param price the price
	 * @param basis the basis
	 * @return the yield mat
	 * @throws Exception the exception
	 */
	public static double getYieldMat(Date settlement, Date maturity, Date issue, double rate, double price, int basis) throws Exception {

		AnalysisDate settlementDate = new AnalysisDate(settlement, basis);
		AnalysisDate maturityDate = new AnalysisDate(maturity, basis);
		AnalysisDate issueDate = new AnalysisDate(issue, basis);

		if (maturityDate.le(settlementDate) || settlementDate.le(issueDate))
			throw new Exception();

		double dimDiff = AnalysisDate.getDiff(issueDate, maturityDate, false);
		double aDiff = AnalysisDate.getDiff(issueDate, settlementDate, false);

		double dsmDiff = dimDiff - aDiff;
		double daysInYear = 360;

		if (basis == 3)
			daysInYear = 365;

		if (basis == 1) {
			daysInYear = AnalysisDate.getAverageYearLength(issueDate.getYear(), settlementDate.getYear());
			if (daysInYear == 365.5 && aDiff <= 366) {
				if (AnalysisDate.shouldCountFeb29(issueDate, settlementDate))
					daysInYear = 366;
				else
					daysInYear = 365;
			}

		}
		
		if(basis==0&&issueDate.getMonth()==2&&settlementDate.getMonth()==2&&issueDate.getOriginalDay()==29&&settlementDate.getOriginalDay()==29)
		{
			//not exact
		}

		double dimYearfrac = dimDiff / daysInYear;
		double dsmYearfrac = dsmDiff / daysInYear;
		double aYearfrac = aDiff / daysInYear;

		double yield = ((100d + (dimYearfrac * rate * 100d)) /  (price + (aYearfrac * rate * 100d))-1d)/dsmYearfrac;

		return yield;

	}
	
	//not like excel
	/**
	 * Gets the duration.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param rate the rate
	 * @param yield the yield
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the duration
	 * @throws Exception the exception
	 */
	public static double getDuration(Date settlement, Date maturity, double rate, double yield, int frequency, int basis) throws Exception {

		double redemption = 100d;

		double coupDays = getCoupDays(settlement, maturity, frequency, basis);

		double couponDayNC = 0;

		if (basis == 2) {
			double coupPcd = getCoupPcd(settlement, maturity, frequency, basis);
			Date coupPcdDate = DateUtil.getJavaDate(coupPcd);

			if (frequency == 4) {

				couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis)
						+ (90 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1));

			}
			else
				couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis) - getCoupDaysNC(coupPcdDate, maturity, frequency, 1) % 30;
		}
		else if (basis == 3) {

			double frac = 0;
			if (frequency == 4)
				frac = 1d / frequency + 91 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1);
			else if (frequency == 2)
				frac = 1d / frequency + 182 - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1);
			else
				frac = 1d / frequency - getCoupDaysNC(DateUtil.getJavaDate(getCoupPcd(settlement, maturity, frequency, basis)), maturity, frequency, 1) % 182;
			couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis) + frac;

		}
		else
			couponDayNC = getCoupDaysNC(settlement, maturity, frequency, basis);

		double coupNum = getCoupNum(settlement, maturity, frequency, basis);
		double coupDayBS = getCouponDayBS(settlement, maturity, frequency, basis);

		double dsc = couponDayNC / coupDays;
		
		double price = redemption / (Math.pow(1d + yield / frequency, coupNum - 1d + dsc));

		price -= 100d * rate / frequency * coupDayBS / coupDays;

		double duration = price * (coupNum - 1d + dsc);

		double rateTerm = 100d * rate / frequency;

		double yieldTerm = 1d + yield / frequency;

		for (double fK = 0.0; fK < coupNum; fK++) {
			
			price += rateTerm /  Math.pow(yieldTerm, fK + dsc);

			duration += (fK + dsc) * rateTerm / Math.pow(yieldTerm, fK + dsc);

		}
		
		return duration / price / frequency;

	}
	
	//not like excel
	/**
	 * Gets the m duration.
	 *
	 * @param settlement the settlement
	 * @param maturity the maturity
	 * @param rate the rate
	 * @param yield the yield
	 * @param frequency the frequency
	 * @param basis the basis
	 * @return the m duration
	 * @throws Exception the exception
	 */
	public static double getMDuration(Date settlement, Date maturity, double rate, double yield, int frequency, int basis) throws Exception {

		return getDuration(settlement, maturity, rate, yield, frequency, basis) / (1d + yield / frequency);

	}

	private static AnalysisDate getCoupNcd(AnalysisDate settlementDate, AnalysisDate maturityDate, int frequency) throws Exception {

		if (maturityDate.le(settlementDate))
			throw new Exception();

		AnalysisDate coupNcd = new AnalysisDate(maturityDate);

		coupNcd.setYear(settlementDate.getYear());
		if (coupNcd.gt(settlementDate))
			coupNcd.setYear(coupNcd.getYear() - 1);

		while (coupNcd.le(settlementDate))
			coupNcd.addMonths(12 / frequency);

		return coupNcd;
	}

	private static double getYearfrac(Date startDate, Date endDate, int basis) {

		AnalysisDate start = new AnalysisDate(startDate, basis);
		AnalysisDate end = new AnalysisDate(endDate, basis);
		return AnalysisDate.getYearfrac(start, end, basis);

	}

}
