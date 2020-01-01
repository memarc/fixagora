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
package net.sourceforge.fixagora.basis.shared.control.analysis;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The Class AnalysisDate.
 */
public class AnalysisDate {

	private int originalDay = 1;

	private int day = 1;

	private int month = 1;

	private int year = 1900;

	private boolean lastDay = false;

	private boolean thirtyDays = false;

	private boolean usMode = false;

	/**
	 * Instantiates a new analysis date.
	 *
	 * @param date the date
	 * @param basis the basis
	 */
	public AnalysisDate(Date date, int basis) {

		super();

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		originalDay = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);

		lastDay = originalDay >= getLastDayOfMonth(month, year);
		if (basis == 0 || basis == 4)
			thirtyDays = true;
		usMode = (basis == 0);
		setDay();
	}

	/**
	 * Instantiates a new analysis date.
	 *
	 * @param copy the copy
	 */
	public AnalysisDate(AnalysisDate copy) {

		super();

		originalDay = copy.originalDay;
		day = copy.day;
		month = copy.month;
		year = copy.year;

		lastDay = copy.lastDay;
		thirtyDays = copy.thirtyDays;
		usMode = copy.usMode;
	}

	private void setDay() {

		int daysOfMonth = getLastDayOfMonth(month, year);

		if (thirtyDays) {
			day = Math.min(originalDay, 30);

			if (lastDay || (day >= daysOfMonth)) {

				day = 30;
			}
		}
		else {

			day = lastDay ? daysOfMonth : Math.min(originalDay, daysOfMonth);
		}
	}

	/**
	 * Gets the last day of month.
	 *
	 * @param month the month
	 * @param year the year
	 * @return the last day of month
	 */
	public static int getLastDayOfMonth(int month, int year) {

		switch (month) {
			case 1: // '\001'
			case 3: // '\003'
			case 5: // '\005'
			case 7: // '\007'
			case 8: // '\b'
			case 10: // '\n'
			case 12: // '\f'
				return 31;

			case 4: // '\004'
			case 6: // '\006'
			case 9: // '\t'
			case 11: // '\013'
				return 30;
		}
		return !isLeapYear(year) ? 28 : 29;
	}

	/**
	 * Checks if is leap year.
	 *
	 * @param i the i
	 * @return true, if is leap year
	 */
	public static boolean isLeapYear(int i) {

		if (i % 4 != 0)
			return false;
		if (i % 400 == 0)
			return true;
		return i % 100 != 0;
	}

	/**
	 * Lt.
	 *
	 * @param compareDate the compare date
	 * @return true, if successful
	 */
	public boolean lt(AnalysisDate compareDate) {

		if (year != compareDate.year)
			return year < compareDate.year;
		if (month != compareDate.month)
			return month < compareDate.month;
		if (day != compareDate.day)
			return day < compareDate.day;
		if (lastDay || compareDate.lastDay)
			return !lastDay && compareDate.lastDay;
		return originalDay < compareDate.originalDay;
	}

	/**
	 * Le.
	 *
	 * @param compareDate the compare date
	 * @return true, if successful
	 */
	public boolean le(AnalysisDate compareDate) {

		if (eq(compareDate))
			return true;
		return lt(compareDate);
	}

	/**
	 * Eq.
	 *
	 * @param compareDate the compare date
	 * @return true, if successful
	 */
	public boolean eq(AnalysisDate compareDate) {

		if (year == compareDate.year && month == compareDate.month && day == compareDate.day && lastDay == compareDate.lastDay)
			return true;
		return false;
	}

	/**
	 * Gt.
	 *
	 * @param compareDate the compare date
	 * @return true, if successful
	 */
	public boolean gt(AnalysisDate compareDate) {

		if (year != compareDate.year)
			return year > compareDate.year;
		if (month != compareDate.month)
			return month > compareDate.month;
		if (day != compareDate.day)
			return day > compareDate.day;
		if (lastDay || compareDate.lastDay)
			return !compareDate.lastDay && lastDay;
		return originalDay > compareDate.originalDay;
	}

	/**
	 * Adds the months.
	 *
	 * @param monthCount the month count
	 */
	public void addMonths(int monthCount) {

		int newMonth = monthCount + month;
		if (newMonth > 12) {
			--newMonth;
			year = year + newMonth / 12;
			month = (newMonth % 12) + 1;
		}
		else if (newMonth < 1) {
			year = year + (newMonth / 12 - 1);
			month = (newMonth % 12) + 12;
		}
		else
			month = newMonth;
		setDay();
	}

	/**
	 * Gets the diff.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return the diff
	 */
	public static int getDiff(AnalysisDate startDate, AnalysisDate endDate) {

		return getDiff(startDate, endDate, false);
	}

	/**
	 * Gets the diff.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param forceThirty the force thirty
	 * @return the diff
	 */
	public static int getDiff(AnalysisDate startDate, AnalysisDate endDate, boolean forceThirty) {

		if (startDate.gt(endDate))
			return getDiff(endDate, startDate, forceThirty);

		int diff = 0;
		AnalysisDate fromDate = new AnalysisDate(startDate);
		AnalysisDate toDate = new AnalysisDate(endDate);

		if (endDate.thirtyDays || forceThirty) {

			if (endDate.usMode) {
				if (((startDate.month == 2) || (startDate.day < 30)) && (toDate.originalDay == 31))
					toDate.day = 31;
				else if ((toDate.month == 2) && toDate.lastDay)
					toDate.day = getLastDayOfMonth(2, toDate.year);
			}
			else {
				if ((fromDate.month == 2) && (fromDate.day == 30))
					fromDate.day = getLastDayOfMonth(2, fromDate.year);
				if ((toDate.month == 2) && (toDate.day == 30))
					toDate.day = getLastDayOfMonth(2, toDate.year);
			}
		}

		if ((fromDate.year < toDate.year) || ((fromDate.year == toDate.year) && (fromDate.month < toDate.month))) {

			diff = fromDate.getDaysInMonth(fromDate.month) - fromDate.day + 1;
			fromDate.originalDay = fromDate.day = 1;
			fromDate.lastDay = false;
			fromDate.addMonths(1);

			if (fromDate.year < toDate.year) {

				diff += fromDate.getDaysInMonthRange(fromDate.month, 12);
				fromDate.addMonths(13 - fromDate.month);

				diff += fromDate.getDaysInYearRange(fromDate.year, toDate.year - 1);
				fromDate.year = toDate.year;
			}

			diff += fromDate.getDaysInMonthRange(fromDate.month, toDate.month - 1);
			fromDate.addMonths(toDate.month - fromDate.month);
		}

		diff += toDate.day - fromDate.day;
		return diff > 0 ? diff : 0;
	}

	private int getDaysInMonthRange(int fromMonth, int toMonth) {

		if (fromMonth > toMonth)
			return 0;

		int days = 0;
		
		if (thirtyDays)
			days = (toMonth - fromMonth + 1) * 30;
		else {
			for (int nMonthIx = fromMonth; nMonthIx <= toMonth; ++nMonthIx)
				days += getDaysInMonth(nMonthIx);
		}
		return days;
	}

	private int getDaysInYearRange(int fromDay, int toDay) {

		if (fromDay > toDay)
			return 0;

		return thirtyDays ? ((toDay - fromDay + 1) * 360) : getDaysInYears(fromDay, toDay);
	}

	/**
	 * Gets the days in years.
	 *
	 * @param fromYear the from year
	 * @param toYear the to year
	 * @return the days in years
	 */
	public static int getDaysInYears(int fromYear, int toYear) {

		int leaps = 0;
		for (int n = fromYear; n <= toYear; n++) {
			if (isLeapYear(n))
				leaps++;
		}

		int sum = 1;
		sum += toYear;
		sum -= fromYear;
		sum *= 365;
		sum += leaps;

		return sum;
	}

	private int getDaysInMonth(int month) {

		int lastDay = getLastDayOfMonth(month, year);

		if (thirtyDays) {
			if (lastDay < 30)
				lastDay = 30;
			return Math.min(lastDay, 30);
		}
		return lastDay;
	}

	/**
	 * Gets the original day.
	 *
	 * @return the original day
	 */
	public int getOriginalDay() {

		return originalDay;
	}

	/**
	 * Sets the originalday.
	 *
	 * @param originalday the new originalday
	 */
	public void setOriginalday(int originalday) {

		this.originalDay = originalday;
	}

	/**
	 * Gets the day.
	 *
	 * @return the day
	 */
	public int getDay() {

		return day;
	}

	/**
	 * Sets the day.
	 *
	 * @param day the new day
	 */
	public void setDay(int day) {

		this.day = day;
	}
	
	/**
	 * Sets the last day.
	 *
	 * @param lastDay the new last day
	 */
	public void setLastDay(boolean lastDay) {

		this.lastDay = lastDay;
	}

	/**
	 * Gets the month.
	 *
	 * @return the month
	 */
	public int getMonth() {

		return month;
	}

	/**
	 * Sets the month.
	 *
	 * @param month the new month
	 */
	public void setMonth(int month) {

		this.month = month;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public int getYear() {

		return year;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(int year) {

		this.year = year;
	}

	/**
	 * Checks if is thirty days.
	 *
	 * @return true, if is thirty days
	 */
	public boolean isThirtyDays() {

		return thirtyDays;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public AnalysisDate getDate() {

		AnalysisDate scaDate = new AnalysisDate(this);
		int lastDay = getLastDayOfMonth(month, year);
		int realday = this.lastDay ? lastDay : Math.min(lastDay, originalDay);
		scaDate.originalDay = realday;
		scaDate.lastDay = originalDay >= lastDay;
		scaDate.setDay();
		return scaDate;

	}

	/**
	 * Gets the days in year.
	 *
	 * @param date the date
	 * @param basis the basis
	 * @return the days in year
	 * @throws Exception the exception
	 */
	public static int getDaysInYear(AnalysisDate date, int basis) throws Exception {

		switch (basis) {
			case 0: 
			case 2: 
			case 4: 
				return 360;
			case 1: 
				return isLeapYear(date.year) ? 366 : 365;
			case 3: 
				return 365;
			default:
				throw new Exception();
		}
	}

	/**
	 * Checks if is last day.
	 *
	 * @return true, if is last day
	 */
	public boolean isLastDay() {

		return lastDay;
	}

	/**
	 * Should count feb29.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return true, if successful
	 */
	public static boolean shouldCountFeb29(AnalysisDate startDate, AnalysisDate endDate) {

		boolean startIsLeapYear = isLeapYear(startDate.year);

		if (startIsLeapYear && startDate.year == endDate.year) {
			return true;
		}

		boolean endIsLeapYear = isLeapYear(endDate.year);

		if (!startIsLeapYear && !endIsLeapYear) {
			return false;
		}

		if (startIsLeapYear) {
			switch (startDate.month) {
				case 1:
				case 2:
					return true;
			}
			return false;
		}
		if (endIsLeapYear) {
			switch (endDate.month) {
				case 1:
					return false;
				case 2:
					break;
				default:
					return true;
			}
			return endDate.day == 29;
		}
		return false;
	}



	/**
	 * Gets the yearfrac.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param basis the basis
	 * @return the yearfrac
	 */
	public static double getYearfrac(AnalysisDate startDate, AnalysisDate endDate, int basis) {
		if (basis ==1)
		{	
		double yearLength = 365D;
		if (isGreaterThanOneYear(startDate, endDate))
			yearLength = getAverageYearLength(startDate.year, endDate.year);
		else if (shouldCountFeb29(startDate, endDate))
			yearLength = 366D;
		return getDiff(startDate, endDate) / yearLength;
		}
		
		if (basis == 0&&startDate.month==2&&endDate.month==2&&startDate.originalDay==28&&endDate.originalDay==28)
		{
			return endDate.year-startDate.year;	        
		}
		
		return getDiff(startDate, endDate, true) / 360D;

	}

	/**
	 * Gets the average year length.
	 *
	 * @param startYear the start year
	 * @param endYear the end year
	 * @return the average year length
	 */
	public static double getAverageYearLength(int startYear, int endYear) {

		int dayCount = 0;
		for (int i = startYear; i <= endYear; i++) {
			dayCount += 365;
			if (isLeapYear(i))
				dayCount++;
		}

		double numberOfYears = (endYear - startYear) + 1;
		return (double) dayCount / numberOfYears;
	}

	private static boolean isGreaterThanOneYear(AnalysisDate startDate, AnalysisDate endDate) {

		if (startDate.year == endDate.year)
			return false;
		if (startDate.year + 1 != endDate.year)
			return true;
		if (startDate.month > endDate.month)
			return false;
		if (startDate.month < endDate.month)
			return true;
		else
			return startDate.day < endDate.day;
	}
	
	/**
	 * Gets the modified diff.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param modifyBothDates the modify both dates
	 * @param basis the basis
	 * @return the modified diff
	 */
	public static int getModifiedDiff(AnalysisDate startDate, AnalysisDate endDate, boolean modifyBothDates, int basis) {

		if (basis != 0)
			return getDiff(startDate, endDate);

		AnalysisDate fromDate = new AnalysisDate(startDate);
		AnalysisDate toDate = new AnalysisDate(endDate);

		if (getLastDayOfMonth(2, toDate.year) == toDate.originalDay && toDate.month == 2 && (modifyBothDates || (getLastDayOfMonth(2, fromDate.year) == fromDate.originalDay && fromDate.month == 2)))
			toDate.originalDay = 30;
		if (toDate.originalDay == 31 && (startDate.getOriginalDay()>=30 || modifyBothDates))
			toDate.originalDay = 30;
		if (fromDate.originalDay == 31)
			fromDate.originalDay = 30;
		if (getLastDayOfMonth(2, fromDate.year) == fromDate.originalDay && fromDate.month == 2)
			fromDate.originalDay = 30;
		return (toDate.year - fromDate.year) * 360 + (toDate.month - fromDate.month) * 30 + (toDate.originalDay - fromDate.originalDay);

	}
	
	/**
	 * Adds the months unadjusted.
	 *
	 * @param monthCount the month count
	 */
	public void addMonthsUnadjusted(int monthCount) {

		int newMonth = monthCount + month;
		if (newMonth > 12) {
			--newMonth;
			year = year + newMonth / 12;
			month = (newMonth % 12) + 1;
		}
		else if (newMonth < 1) {
			year = year + (newMonth / 12 - 1);
			month = (newMonth % 12) + 12;
		}
		else
			month = newMonth;

		int daysOfMonth = getLastDayOfMonth(month, year);

		day = Math.min(originalDay, daysOfMonth);

		originalDay = day;

	}
	
	/**
	 * Gets the odd f diff.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param basis the basis
	 * @return the odd f diff
	 */
	public static double getOddFDiff(AnalysisDate startDate, AnalysisDate endDate, int basis) {

		if (basis != 4)
			return getModifiedDiff(startDate, endDate, false, basis);

		AnalysisDate fromDate = new AnalysisDate(startDate);
		AnalysisDate toDate = new AnalysisDate(endDate);

		if (toDate.originalDay == 31)
			toDate.originalDay = 30;
		if (fromDate.originalDay == 31)
			fromDate.originalDay = 30;
		
		return (toDate.year - fromDate.year) * 360 + (toDate.month - fromDate.month) * 30 + (toDate.originalDay - fromDate.originalDay);
	}

}
