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

import java.util.Date;
import java.util.Map;

import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.TimeUnit;

/**
 * The Class ValutaFunction.
 */
public class ValutaFunction implements FreeRefFunction {

	private Map<String, BankCalendar> bankCalendarMap = null;
	
	
	/**
	 * Instantiates a new valuta function.
	 *
	 * @param bankCalendarMap the bank calendar map
	 */
	public ValutaFunction(Map<String, BankCalendar> bankCalendarMap) {

		super();
		this.bankCalendarMap = bankCalendarMap;
	}

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length !=2) {
			return ErrorEval.VALUE_INVALID;
		}

		try {

			ValueEval valueEval1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
			String bankCalendarName = OperandResolver.coerceValueToString(valueEval1);
			if(bankCalendarName==null)
				return ErrorEval.VALUE_INVALID;
			BankCalendar bankCalendar = bankCalendarMap.get(bankCalendarName);
			if(bankCalendar==null)
				return ErrorEval.VALUE_INVALID;
			ValueEval valueEval2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
			int settlementDays = OperandResolver.coerceValueToInt(valueEval2);
			Calendar calendar = bankCalendar.getCalendar();
			org.jquantlib.time.Date settlementDate = calendar.advance(new org.jquantlib.time.Date(new Date()),settlementDays,TimeUnit.Days);
			java.util.Calendar calendar2 = java.util.Calendar.getInstance();
			calendar2.setTime(settlementDate.isoDate());
			calendar2.set(java.util.Calendar.HOUR_OF_DAY, 0);
			calendar2.set(java.util.Calendar.MINUTE, 0);
			calendar2.set(java.util.Calendar.SECOND, 0);
			calendar2.set(java.util.Calendar.MILLISECOND, 0);
			return new NumberEval(DateUtil.getExcelDate(calendar2.getTime()));
		}
		catch (Exception e) {

			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return ErrorEval.VALUE_INVALID;
		}
	}

}
