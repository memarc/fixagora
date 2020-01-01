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
import java.util.concurrent.TimeUnit;

import net.sourceforge.fixagora.basis.shared.control.analysis.AnalysisToolPak;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * The Class OddFirstYieldFunction.
 */
public class OddFirstYieldFunction implements FreeRefFunction {

	private LoadingCache<YieldKey, NumberEval> yieldCache = null;

	/**
	 * Instantiates a new odd first yield function.
	 */
	public OddFirstYieldFunction() {

		super();

		yieldCache = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build(new CacheLoader<YieldKey, NumberEval>() {

			public NumberEval load(YieldKey key) throws Exception {

				return new NumberEval(AnalysisToolPak.getOddFYield(key.getSettlement(), key.getMaturity(), key.getIssue(), key.getFirstCoupon(), key.getRate(),
						key.getPrice(), key.getRedemption(), key.getFrequency(), key.getBasis()));
			}
		});

	}

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

			double price = OperandResolver.coerceValueToDouble(valueEval6);

			double redemption = OperandResolver.coerceValueToDouble(valueEval7);

			int frequency = OperandResolver.coerceValueToInt(valueEval8);

			int basis = 0;

			if (valueEval9 != null)
				basis = OperandResolver.coerceValueToInt(valueEval9);

			YieldKey yieldKey = new YieldKey(settlement, maturity, issue, firstCoupon, rate, price, redemption, frequency, basis);

			return yieldCache.get(yieldKey);

		}
		catch (Exception e) {

			if (e instanceof EvaluationException)
				return ((EvaluationException) e).getErrorEval();

			else
				return ErrorEval.VALUE_INVALID;
		}
	}

	private class YieldKey {

		Date settlement = null;

		Date maturity = null;

		Date issue = null;

		Date firstCoupon = null;

		double rate = 0d;

		double price = 0d;

		double redemption = 0d;

		int frequency = 0;

		int basis = 0;

		public YieldKey(Date settlement, Date maturity, Date issue, Date firstCoupon, double rate, double price, double redemption, int frequency, int basis) {

			super();
			this.settlement = settlement;
			this.maturity = maturity;
			this.issue = issue;
			this.firstCoupon = firstCoupon;
			this.rate = rate;
			this.price = price;
			this.redemption = redemption;
			this.frequency = frequency;
			this.basis = basis;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + basis;
			result = prime * result + ((firstCoupon == null) ? 0 : firstCoupon.hashCode());
			result = prime * result + frequency;
			result = prime * result + ((issue == null) ? 0 : issue.hashCode());
			result = prime * result + ((maturity == null) ? 0 : maturity.hashCode());
			long temp;
			temp = Double.doubleToLongBits(price);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(rate);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(redemption);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((settlement == null) ? 0 : settlement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			YieldKey other = (YieldKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (basis != other.basis)
				return false;
			if (firstCoupon == null) {
				if (other.firstCoupon != null)
					return false;
			}
			else if (!firstCoupon.equals(other.firstCoupon))
				return false;
			if (frequency != other.frequency)
				return false;
			if (issue == null) {
				if (other.issue != null)
					return false;
			}
			else if (!issue.equals(other.issue))
				return false;
			if (maturity == null) {
				if (other.maturity != null)
					return false;
			}
			else if (!maturity.equals(other.maturity))
				return false;
			if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
				return false;
			if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
				return false;
			if (Double.doubleToLongBits(redemption) != Double.doubleToLongBits(other.redemption))
				return false;
			if (settlement == null) {
				if (other.settlement != null)
					return false;
			}
			else if (!settlement.equals(other.settlement))
				return false;
			return true;
		}

		private OddFirstYieldFunction getOuterType() {

			return OddFirstYieldFunction.this;
		}

		public Date getSettlement() {

			return settlement;
		}

		public Date getMaturity() {

			return maturity;
		}

		public Date getIssue() {

			return issue;
		}

		public Date getFirstCoupon() {

			return firstCoupon;
		}

		public double getRate() {

			return rate;
		}

		public double getPrice() {

			return price;
		}

		public double getRedemption() {

			return redemption;
		}

		public int getFrequency() {

			return frequency;
		}

		public int getBasis() {

			return basis;
		}

	}

}
