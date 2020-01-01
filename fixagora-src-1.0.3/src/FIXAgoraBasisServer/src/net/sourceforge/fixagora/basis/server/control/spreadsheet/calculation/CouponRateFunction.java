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

import net.sourceforge.fixagora.basis.server.control.component.SecurityDictionary;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

/**
 * The Class CouponRateFunction.
 */
public class CouponRateFunction implements FreeRefFunction {

	private SecurityDictionary securityDictionary = null;
	
	
	/**
	 * Instantiates a new coupon rate function.
	 *
	 * @param securityDictionary the security dictionary
	 */
	public CouponRateFunction(SecurityDictionary securityDictionary) {

		super();
		this.securityDictionary = securityDictionary;
	}

	/* (non-Javadoc)
	 * @see org.apache.poi.ss.formula.functions.FreeRefFunction#evaluate(org.apache.poi.ss.formula.eval.ValueEval[], org.apache.poi.ss.formula.OperationEvaluationContext)
	 */
	@Override
	public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

		if (args.length !=1) {
			return ErrorEval.VALUE_INVALID;
		}

		try {

			ValueEval valueEval1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
			String security = OperandResolver.coerceValueToString(valueEval1);
			if(security==null)
				return ErrorEval.VALUE_INVALID;
			FSecurity fSecurity = securityDictionary.getSecurityForDefaultSecurityID(security);	
			if(fSecurity==null)
				return ErrorEval.VALUE_INVALID;
			Double couponRate = fSecurity.getSecurityDetails().getCouponRate();
			if(couponRate==null)
				return ErrorEval.VALUE_INVALID;
			return new NumberEval(couponRate);

		}
		catch (Exception e) {

			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();
			else
				return ErrorEval.VALUE_INVALID;
		}
	}

}
