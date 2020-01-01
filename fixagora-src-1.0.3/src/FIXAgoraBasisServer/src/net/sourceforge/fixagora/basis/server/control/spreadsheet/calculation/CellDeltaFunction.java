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

import java.util.Map;

import net.sourceforge.fixagora.basis.server.control.spreadsheet.SpreadSheetWorker;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

/**
 * The Class CellDeltaFunction.
 */
public class CellDeltaFunction implements FreeRefFunction {

	private Map<String, SpreadSheetWorker> nameSpreadSheetWorkerMap = null;

	/**
	 * Instantiates a new cell delta function.
	 *
	 * @param nameSpreadSheetWorkerMap the name spread sheet worker map
	 */
	public CellDeltaFunction(Map<String, SpreadSheetWorker> nameSpreadSheetWorkerMap) {

		super();
		this.nameSpreadSheetWorkerMap = nameSpreadSheetWorkerMap;
	}

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length != 1) {
			return ErrorEval.VALUE_INVALID;
		}

		try {

			String name = args[0].toString();
			name = name.substring(0, name.length() - 1);
			name = name.substring(name.indexOf("[") + 1);
			String names[] = name.split("!");
			SpreadSheetWorker spreadSheetWorker = nameSpreadSheetWorkerMap.get(names[0]);
			Object oldValue = spreadSheetWorker.getOldValue(names[1]);

			ValueEval valueEval = OperandResolver.getSingleValue( args[0], 
                    ec.getRowIndex(), 
                    ec.getColumnIndex() );
			
			if (oldValue instanceof Number) {
				
				double newValue = OperandResolver.coerceValueToDouble( valueEval);
				double newDelta = newValue-((Number) oldValue).doubleValue();
				if(newDelta==0)
				{
					Double oldDelta = spreadSheetWorker.getDeltaValue(names[1]);
					if(oldDelta!=null)
						return new NumberEval(oldDelta);
				}
				else
				{
					spreadSheetWorker.putDeltaValue( names[1],newDelta);
					return new NumberEval(newDelta);
				}
			}
			return BlankEval.instance;

		}
		catch (Exception e) {
			
			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return BlankEval.instance;
		}
	}

}
