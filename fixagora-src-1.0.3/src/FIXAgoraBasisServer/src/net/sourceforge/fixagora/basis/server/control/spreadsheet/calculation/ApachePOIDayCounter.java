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
package net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.time.Date;

/**
 * The Class ApachePOIDayCounter.
 */
public class ApachePOIDayCounter extends DayCounter {

	/**
	 * Instantiates a new apache poi day counter.
	 *
	 * @param basis the basis
	 */
	public ApachePOIDayCounter(int basis) {

		super();
		switch(basis)
		{
			case 1:
				super.impl = new Basis1DayCounter();
				break;
			case 2:
				super.impl = new Basis2DayCounter();
				break;
			case 3:
				super.impl = new Basis3DayCounter();
				break;
			case 4:
				super.impl = new Basis4DayCounter();
				break;
			default:
				super.impl = new Basis0DayCounter();
				break;
		}
	}

	final private class Basis1DayCounter extends DayCounter.Impl {

		@Override
		public final String name() {

			return "Actual/Actual";
		}

		@Override
		public final double yearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart, final Date refPeriodEnd) 
		{

            if (dateStart.equals(dateEnd))
                return 0.0;
            if (dateStart.gt(dateEnd))
                return yearFraction(dateEnd, dateStart, new Date(), new Date());
			
		       	SimpleDate startDate = new SimpleDate(dateStart);
		        SimpleDate endDate = new SimpleDate(dateEnd);
		        double yearLength;
		        if(isGreaterThanOneYear(startDate, endDate))
		            yearLength = averageYearLength(startDate.year, endDate.year);
		        else
		        if(shouldCountFeb29(startDate, endDate))
		            yearLength = 366D;
		        else
		            yearLength = 365D;
		        
		        return (double)dateDiff(startDate.tsMilliseconds, endDate.tsMilliseconds) / yearLength;
		}

	}
	
	
	final private class Basis2DayCounter extends DayCounter.Impl {

		@Override
		public final String name() {

			return "Actual/360";
		}

		@Override
		public final double yearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart, final Date refPeriodEnd) 
		{
			return (double)(dateEnd.sub(dateStart)) / 360D;
		}

	}
	
	final private class Basis3DayCounter extends DayCounter.Impl {

		@Override
		public final String name() {

			return "Actual/365";
		}

		@Override
		public final double yearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart, final Date refPeriodEnd) 
		{
			return (double)(dateEnd.sub(dateStart)) / 365D;
		}

	}
	
	final private class Basis4DayCounter extends DayCounter.Impl {

		@Override
		public final String name() {

			return "European 30/360";
		}

		@Override
		public final double yearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart, final Date refPeriodEnd) 
		{
	        SimpleDate startDate = new SimpleDate(dateStart);
	        SimpleDate endDate = new SimpleDate(dateEnd);
	        int date1day = startDate.day;
	        int date2day = endDate.day;
	        if(date1day == 31)
	            date1day = 30;
	        if(date2day == 31)
	            date2day = 30;
	        return calculateAdjusted(startDate, endDate, date1day, date2day);
		}

	}
	
	final private class Basis0DayCounter extends DayCounter.Impl {

		@Override
		public final String name() {

			return "US (NASD) system. 30 days/month, 360 days/year (30/360)";
		}

		@Override
		public final double yearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart, final Date refPeriodEnd) 
		{

	        SimpleDate startDate = new SimpleDate(dateStart);
	        SimpleDate endDate = new SimpleDate(dateEnd);
	        int date1day = startDate.day;
	        int date2day = endDate.day;
	        if(date1day == 31 && date2day == 31)
	        {
	            date1day = 30;
	            date2day = 30;
	        } else
	        if(date1day == 31)
	            date1day = 30;
	        else
	        if(date1day == 30 && date2day == 31)
	            date2day = 30;
	        else
	        if(startDate.month == 2 && isLastDayOfMonth(startDate))
	        {
	            date1day = 30;
	            if(endDate.month == 2 && isLastDayOfMonth(endDate))
	                date2day = 30;
	        }
	        return calculateAdjusted(startDate, endDate, date1day, date2day);
	        
		}

	}
	
    private static double calculateAdjusted(SimpleDate startDate, SimpleDate endDate, int date1day, int date2day)
    {
        double dayCount = (endDate.year - startDate.year) * 360 + (endDate.month - startDate.month) * 30 + (date2day - date1day) * 1;
        return dayCount / 360D;
    }
	
    private static boolean isLastDayOfMonth(SimpleDate date)
    {
        if(date.day < 28)
            return false;
        else
            return date.day == getLastDayOfMonth(date);
    }
    
    private static int getLastDayOfMonth(SimpleDate date)
    {
        switch(date.month)
        {
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
        return !isLeapYear(date.year) ? 28 : 29;
    }
	
    private static int dateDiff(long startDateMS, long endDateMS)
    {
        long msDiff = endDateMS - startDateMS;
        int remainderHours = (int)((msDiff % 86400000L) / 3600000L);
        switch(remainderHours)
        {
        case 1: // '\001'
        case 23: // '\027'
        default:
            throw new RuntimeException((new StringBuilder()).append("Unexpected date diff between ").append(startDateMS).append(" and ").append(endDateMS).toString());

        case 0: // '\0'
            return (int)(0.5D + (double)msDiff / 86400000D);
        }
    }

	
    private static boolean isLeapYear(int i)
    {
        if(i % 4 != 0)
            return false;
        if(i % 400 == 0)
            return true;
        return i % 100 != 0;
    }
    
    private static boolean shouldCountFeb29(SimpleDate start, SimpleDate end)
    {
        boolean startIsLeapYear = isLeapYear(start.year);
        if(startIsLeapYear && start.year == end.year)
            return true;
        boolean endIsLeapYear = isLeapYear(end.year);
        if(!startIsLeapYear && !endIsLeapYear)
            return false;
        if(startIsLeapYear)
        {
            switch(start.month)
            {
            case 1: // '\001'
            case 2: // '\002'
                return true;
            }
            return false;
        }
        if(endIsLeapYear)
            switch(end.month)
            {
            case 1: // '\001'
                return false;

            default:
                return true;

            case 2: // '\002'
                return end.day == 29;
            }
        else
            return false;
    }
	
    private static double averageYearLength(int startYear, int endYear)
    {
        int dayCount = 0;
        for(int i = startYear; i <= endYear; i++)
        {
            dayCount += 365;
            if(isLeapYear(i))
                dayCount++;
        }

        double numberOfYears = (endYear - startYear) + 1;
        return (double)dayCount / numberOfYears;
    }
	
	   private static boolean isGreaterThanOneYear(SimpleDate start, SimpleDate end)
	    {
	        if(start.year == end.year)
	            return false;
	        if(start.year + 1 != end.year)
	            return true;
	        if(start.month > end.month)
	            return false;
	        if(start.month < end.month)
	            return true;
	        else
	            return start.day < end.day;
	    }

	
    private static final class SimpleDate
    {

       public final int year;
        public final int month;
        public final int day;
        public long tsMilliseconds;

        public SimpleDate(Date date)
        {
            year = date.year();
            month = date.month().value();
            day = date.dayOfMonth();
            Calendar calendar = new GregorianCalendar(UTC_TIME_ZONE);
            
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);            
            tsMilliseconds = calendar.getTimeInMillis();
        }
    }
    
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

}
