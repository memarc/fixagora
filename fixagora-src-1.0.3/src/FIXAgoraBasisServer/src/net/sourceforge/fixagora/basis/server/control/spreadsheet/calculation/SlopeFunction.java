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

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.poi.ss.formula.eval.AreaEvalBase;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;

/**
 * The Class SlopeFunction.
 */
public class SlopeFunction implements Function {

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.Function#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], int, int)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, int arg1, int arg2) {
		
		if (args.length != 2) {
			return ErrorEval.VALUE_INVALID;
		}

		try {

			AreaEvalBase yvalues = (AreaEvalBase) args[0];

			AreaEvalBase xvalues = (AreaEvalBase) args[1];

			if (yvalues.getWidth() != 1)
				return ErrorEval.VALUE_INVALID;
			if (xvalues.getWidth() != 1)
				return ErrorEval.VALUE_INVALID;

			if (xvalues.getHeight() != yvalues.getHeight())
				return ErrorEval.VALUE_INVALID;

			double[] xarray = new double[yvalues.getHeight()];
			double[] yarray = new double[yvalues.getHeight()];

			int j = 0;

			for (int i = 0; i < yvalues.getHeight(); i++) {
				try {
					yvalues.getRelativeValue(i, 0);
					if (yvalues.getRelativeValue(i, 0) instanceof NumberEval && xvalues.getRelativeValue(i, 0) instanceof NumberEval) {
						double yEntry = OperandResolver.coerceValueToDouble(yvalues.getRelativeValue(i, 0));
						double xEntry = OperandResolver.coerceValueToDouble(xvalues.getRelativeValue(i, 0));
						yarray[j] = yEntry;
						xarray[j] = xEntry;
						j++;
					}
				}
				catch (Exception e) {
				}
			}
			
			boolean sorted = true;
			
			do
			{
				sorted = true;
				for(int i=1;i<j;i++)
				{
					if(xarray[i]<xarray[i-1])
					{
						sorted = false;
						double tmp = xarray[i];
						xarray[i] = xarray[i-1];
						xarray[i-1] = tmp;
						tmp = yarray[i];
						yarray[i] = yarray[i-1];
						yarray[i-1] = tmp;
					}
				}
			}
			while(!sorted);

			SimpleRegression simpleRegression = new SimpleRegression();
			
			for (int i = 0; i < yvalues.getHeight(); i++) {
				simpleRegression.addData(xarray[i], yarray[i]);
			}
			
			return new NumberEval(simpleRegression.getSlope());

		}
		catch (Exception e) {
						
			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return ErrorEval.VALUE_INVALID;
		}
		
	}

}
