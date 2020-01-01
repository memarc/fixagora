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
 * The Class AccruedInterestAtMaturityFunction.
 */
public class AccruedInterestAtMaturityFunction implements FreeRefFunction {

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length < 3 || args.length > 5) {
			return ErrorEval.VALUE_INVALID;
		}

		try {

			ValueEval valueEval1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
			ValueEval valueEval2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
			ValueEval valueEval3 = OperandResolver.getSingleValue(args[2], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval4 = null;
			if (args.length > 3)
				valueEval4 = OperandResolver.getSingleValue(args[3], ec.getRowIndex(), ec.getColumnIndex());

			ValueEval valueEval5 = null;
			if (args.length > 4)
				valueEval5 = OperandResolver.getSingleValue(args[4], ec.getRowIndex(), ec.getColumnIndex());

			Date issue = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval1));

			Date settlement = DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(valueEval2));

			double rate = OperandResolver.coerceValueToDouble(valueEval3);

			double par = 1000;

			double valueEval4Double = OperandResolver.coerceValueToDouble(valueEval4);

			int basis = 0;

			if (valueEval4Double != 1d && valueEval4Double != 2d && valueEval4Double != 4d) {
				par = valueEval4Double;
				if (valueEval5 != null) {
					basis = OperandResolver.coerceValueToInt(valueEval5);

				}
				else
					return ErrorEval.VALUE_INVALID;

			}
			else {
				if (valueEval4 != null)
					basis = OperandResolver.coerceValueToInt(valueEval4);
			}

			return new NumberEval(AnalysisToolPak.getAccrIntM(issue, settlement, rate, par, basis));

		}
		catch (Exception e) {

			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return ErrorEval.VALUE_INVALID;
		}
	}

}
