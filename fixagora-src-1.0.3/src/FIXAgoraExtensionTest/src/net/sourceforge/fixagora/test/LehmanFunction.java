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
package net.sourceforge.fixagora.test;

import java.util.Date;
import java.util.Map;

import net.sourceforge.fixagora.basis.server.control.spreadsheet.AbstractSpreadSheetFunction;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;
import org.jquantlib.time.Calendar;

/**
 * The Class LehmanFunction.
 */
public class LehmanFunction extends AbstractSpreadSheetFunction {

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.spreadsheet.AbstractSpreadSheetFunction#getFunctionName()
	 */
	@Override
	public String getFunctionName() {

		return "LEHMAN";
	}

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length != 2) {

			return ErrorEval.VALUE_INVALID;
		}

		try {

			Map<String, BankCalendar> map = businessComponentHandler.getBankCalendarMap();

			ValueEval valueEval1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());

			String bankCalendarName = OperandResolver.coerceValueToString(valueEval1);

			if (bankCalendarName == null)
				return ErrorEval.VALUE_INVALID;

			BankCalendar bankCalendar = map.get(bankCalendarName);

			if (bankCalendar == null)
				return ErrorEval.VALUE_INVALID;

			ValueEval valueEval2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());

			int daycount = 0;

			org.jquantlib.time.Date lehmanCrash = new org.jquantlib.time.Date(15, 9, 2008);

			Date javaDate = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval2));

			org.jquantlib.time.Date date = new org.jquantlib.time.Date(javaDate);

			Calendar calendar = bankCalendar.getCalendar();

			while (lehmanCrash.lt(date)) {

				if (calendar.isBusinessDay(lehmanCrash))
					daycount++;

				lehmanCrash.addAssign(1);
			}

			return new NumberEval(daycount);

		}
		catch (Exception e) {

			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return ErrorEval.VALUE_INVALID;
		}
	}
}
