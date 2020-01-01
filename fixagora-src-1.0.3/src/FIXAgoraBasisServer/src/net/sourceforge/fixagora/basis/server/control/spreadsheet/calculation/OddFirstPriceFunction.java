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

import net.sourceforge.fixagora.basis.shared.control.analysis.AnalysisToolPak;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * The Class OddFirstPriceFunction.
 */
public class OddFirstPriceFunction implements FreeRefFunction {

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length != 8 && args.length != 9) 
			return ErrorEval.VALUE_INVALID;
		

		try {

			ValueEval valueEval1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
			
			ValueEval valueEval3 = OperandResolver.getSingleValue(args[2], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval4 = OperandResolver.getSingleValue(args[3], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval5 = OperandResolver.getSingleValue(args[4], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval6 = OperandResolver.getSingleValue(args[5], ec.getRowIndex(), ec.getColumnIndex());
			
			ValueEval valueEval7 = OperandResolver.getSingleValue(args[6], ec.getRowIndex(), ec.getColumnIndex());
			
			ValueEval valueEval8 = OperandResolver.getSingleValue(args[7], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval9 = null;
			
			if (args.length == 9)
				valueEval9 = OperandResolver.getSingleValue(args[8], ec.getRowIndex(), ec.getColumnIndex());

			Date settlement = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval1));

			Date maturity = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval2));
			
			Date issue = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval3));

			Date firstCoupon = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval4));

			double rate = OperandResolver.coerceValueToDouble(valueEval5);

			double yield = OperandResolver.coerceValueToDouble(valueEval6);

			double redemption = OperandResolver.coerceValueToDouble(valueEval7);

			int frequency = OperandResolver.coerceValueToInt(valueEval8);

			int basis = 0;

			if (valueEval9 != null)
				basis = OperandResolver.coerceValueToInt(valueEval9);

			return new NumberEval(AnalysisToolPak.getOddFPrice(settlement, maturity, issue, firstCoupon, rate, yield, redemption, frequency, basis));

		}
		catch (Exception e) {
			
			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			
			else
				return ErrorEval.VALUE_INVALID;
		}
	}

}
