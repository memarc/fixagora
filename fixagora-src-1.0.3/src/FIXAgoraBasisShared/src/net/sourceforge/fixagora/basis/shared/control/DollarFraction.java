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
package net.sourceforge.fixagora.basis.shared.control;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The Class DollarFraction.
 */
public class DollarFraction {

	private static DecimalFormat decimalFormat = new DecimalFormat("###0.########################", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private static DecimalFormat decimalFormat2 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private static DecimalFormat decimalFormat3 = new DecimalFormat("##00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private static List<Character> numerators = Arrays.asList('\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078',
			'\u2079');

	private static List<Character> denominators = Arrays.asList('\u2080', '\u2081', '\u2082', '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088',
			'\u2089');

	private static char fractionSlash = '\u2044';

	private BigDecimal tickSize = null;

	/**
	 * Instantiates a new dollar fraction.
	 *
	 * @param tickSize the tick size
	 * @throws Exception the exception
	 */
	public DollarFraction(double tickSize) throws Exception {

		super();
		if (!isValidTickSize(tickSize))
			throw new Exception("Invalid tick size.");
		this.tickSize = new BigDecimal(decimalFormat.format(tickSize));
	}

	/**
	 * Gets the dollar price.
	 *
	 * @param price the price
	 * @param leadingZeros the leading zeros
	 * @return the dollar price
	 * @throws Exception the exception
	 */
	public String getDollarPrice(double price, int leadingZeros) throws Exception {

		return getDollarPrice(price, leadingZeros, false);
	}

	/**
	 * Gets the dollar price fraction.
	 *
	 * @param price the price
	 * @return the dollar price fraction
	 * @throws Exception the exception
	 */
	public String getDollarPriceFraction(double price) throws Exception {

		return getDollarPriceFraction(price, false);
	}

	/**
	 * Gets the dollar price fraction.
	 *
	 * @param price the price
	 * @param shorten the shorten
	 * @return the dollar price fraction
	 * @throws Exception the exception
	 */
	public String getDollarPriceFraction(double price, boolean shorten) throws Exception {

		if (!isValidPrice(price))
			throw new Exception("Invalid tick price.");

		StringBuffer stringBuffer = new StringBuffer();

		BigDecimal bigPrice = new BigDecimal(decimalFormat.format(price));

		bigPrice = bigPrice.abs();

		bigPrice = bigPrice.subtract(bigPrice.setScale(0, RoundingMode.DOWN));

		bigPrice = bigPrice.multiply(new BigDecimal("32"));

		stringBuffer.append(decimalFormat3.format(bigPrice.longValue()));

		bigPrice = bigPrice.subtract(bigPrice.setScale(0, RoundingMode.DOWN));

		BigDecimal secondDenominator = new BigDecimal(decimalFormat3.format(getSecondDenominator(tickSize)));

		bigPrice = bigPrice.multiply(secondDenominator);

		if (shorten && bigPrice.intValue() > 0) {

			BigDecimal two = new BigDecimal("2");

			while (bigPrice.intValue() % 2 == 0) {
				bigPrice = bigPrice.divide(two);
				secondDenominator = secondDenominator.divide(two);
			}

			if (bigPrice.intValue() == 1 && secondDenominator.intValue() == 2)
				stringBuffer.append("+");

		}

		if ((!shorten) || bigPrice.intValue() > 1 || (bigPrice.intValue() == 1 && secondDenominator.intValue() != 2)) {
			String denominatorString = getDenomiatorString(secondDenominator.intValue());

			String numeratorString = getNumeratorString(bigPrice.intValue());

			stringBuffer.append(' ');

			if (!shorten)
				for (int i = 0; i < denominatorString.length() - numeratorString.length(); i++)
					stringBuffer.append(getNumeratorDigit('0'));

			stringBuffer.append(numeratorString);

			stringBuffer.append(fractionSlash);

			stringBuffer.append(denominatorString);

		}

		return stringBuffer.toString();

	}

	/**
	 * Gets the dollar price.
	 *
	 * @param price the price
	 * @param leadingZeros the leading zeros
	 * @param shorten the shorten
	 * @return the dollar price
	 * @throws Exception the exception
	 */
	public String getDollarPrice(double price, int leadingZeros, boolean shorten) throws Exception {

		StringBuffer stringBuffer = new StringBuffer();

		if (price < 0)
			stringBuffer.append("-");

		String text = decimalFormat2.format(Math.abs((long) price));

		for (int i = 0; i < leadingZeros - text.replaceAll(",", "").length(); i++)
			stringBuffer.append("0");

		if (!(leadingZeros == 0 && text.equals("0"))) {
			stringBuffer.append(text);
			stringBuffer.append("-");
		}

		stringBuffer.append(getDollarPriceFraction(price, shorten));
		
		return stringBuffer.toString();
	}

	/**
	 * Checks if is valid price.
	 *
	 * @param price the price
	 * @return true, if is valid price
	 */
	public boolean isValidPrice(double price) {
		
		if(price==0)
			return true;

		BigDecimal bigPrice = new BigDecimal(decimalFormat.format(price));

		bigPrice = bigPrice.divide(tickSize);

		if (bigPrice.longValue() != bigPrice.doubleValue())
			return false;

		return true;

	}

	/**
	 * Checks if is valid tick size.
	 *
	 * @param tickSize the tick size
	 * @return true, if is valid tick size
	 */
	public static boolean isValidTickSize(double tickSize) {

		if (tickSize == 0)
			return false;

		BigDecimal multiply = new BigDecimal("2");

		BigDecimal bigTickSize = new BigDecimal(decimalFormat.format(tickSize));

		while (bigTickSize.doubleValue() < 1)
			bigTickSize = bigTickSize.multiply(multiply);

		if (bigTickSize.longValue() != bigTickSize.doubleValue())
			return false;

		return true;

	}

	/**
	 * Gets the second denominator.
	 *
	 * @param tickSize the tick size
	 * @return the second denominator
	 */
	public static int getSecondDenominator(double tickSize) {

		BigDecimal bigTickSize = new BigDecimal(decimalFormat.format(tickSize));

		return getSecondDenominator(bigTickSize);
	}

	private static int getSecondDenominator(BigDecimal bigTickSize) {

		BigDecimal multiply1 = new BigDecimal("32");

		BigDecimal multiply2 = new BigDecimal("2");

		bigTickSize = bigTickSize.multiply(multiply1);

		int secondDenominator = 1;

		while (bigTickSize.doubleValue() < 1) {
			secondDenominator = secondDenominator * 2;
			bigTickSize = bigTickSize.multiply(multiply2);
		}

		if (secondDenominator == 1)
			return 0;

		return secondDenominator;
	}

	/**
	 * Gets the denomiator digit.
	 *
	 * @param denomiator the denomiator
	 * @return the denomiator digit
	 */
	public static char getDenomiatorDigit(char denomiator) {

		int position = ((int) denomiator) - 48;

		if (position < 0 || position > 9)
			return '?';

		return denominators.get(position);
	}

	/**
	 * Gets the numerator digit.
	 *
	 * @param numerator the numerator
	 * @return the numerator digit
	 */
	public static char getNumeratorDigit(char numerator) {

		int position = ((int) numerator) - 48;

		if (position < 0 || position > 9)
			return '?';

		return numerators.get(position);
	}

	/**
	 * Gets the denomiator int char.
	 *
	 * @param denominator the denominator
	 * @return the denomiator int char
	 */
	public static char getDenomiatorIntChar(char denominator) {

		int position = denominators.indexOf(denominator);
		if (position < 0)
			return '?';
		return (char) (position + 48);

	}

	/**
	 * Gets the numerator int char.
	 *
	 * @param numerator the numerator
	 * @return the numerator int char
	 */
	public static char getNumeratorIntChar(char numerator) {

		int position = numerators.indexOf(numerator);
		if (position < 0)
			return '?';
		return (char) (position + 48);
	}

	/**
	 * Gets the denomiator string.
	 *
	 * @param denomiator the denomiator
	 * @return the denomiator string
	 */
	public static String getDenomiatorString(int denomiator) {

		StringBuffer stringBuffer = new StringBuffer();
		while (denomiator > 0) {
			stringBuffer.insert(0, denominators.get(denomiator % 10));
			denomiator = denomiator / 10;
		}

		stringBuffer.insert(0, ' ');

		return stringBuffer.toString();
	}

	/**
	 * Gets the numerator string.
	 *
	 * @param numerator the numerator
	 * @return the numerator string
	 */
	public static String getNumeratorString(int numerator) {

		StringBuffer stringBuffer = new StringBuffer();
		while (numerator > 0) {
			stringBuffer.insert(0, numerators.get(numerator % 10));
			numerator = numerator / 10;
		}

		stringBuffer.append(' ');

		return stringBuffer.toString();
	}

	/**
	 * Gets the double value.
	 *
	 * @param text the text
	 * @return the double value
	 */
	public static Double getDoubleValue(String text) {
		
		if (text == null)
			return null;

		boolean negative = false;

		try {
			text = text.replaceAll(",", "");

			int i = 0;

			if (text.startsWith("-")) {

				negative = true;

				i = 1;
			}

			BigDecimal bigDecimal = new BigDecimal("0");

			if (text.indexOf('-') != -1 && (text.lastIndexOf('-') != text.indexOf('-') || !negative)) {

				while (text.length() > i && Character.isDigit(text.charAt(i)))
					i++;

				if (i == text.length() || text.charAt(i) != '-')
					return null;

				bigDecimal = new BigDecimal(text.substring(0, i));

				i++;

			}

			int j = i;

			while (text.length() > i && Character.isDigit(text.charAt(i)))
				i++;
			
			BigDecimal bigDecimal2 = new BigDecimal(text.substring(j, i));

			BigDecimal bigDecimal3 = new BigDecimal("0");

			BigDecimal bigDecimal4 = new BigDecimal("0");

			BigDecimal ten = new BigDecimal("10");

			BigDecimal thirtyTwo = new BigDecimal("32");

			while (text.length() > i + 1) {
				i++;

				if (text.charAt(i) == '+') {
					bigDecimal3 = new BigDecimal("1.0");
					bigDecimal4 = new BigDecimal("2.0");
					i = text.length();
				}
				else {
					int number = numerators.indexOf(text.charAt(i));

					if (number >= 0) {
						bigDecimal3 = bigDecimal3.multiply(ten).add(new BigDecimal(number));
					}

					number = denominators.indexOf(text.charAt(i));

					if (number >= 0) {
						bigDecimal4 = bigDecimal4.multiply(ten).add(new BigDecimal(number));
					}
				}
			}

			if(bigDecimal3.doubleValue()!=0)
				bigDecimal2 = bigDecimal2.add(bigDecimal3.divide(bigDecimal4));

			if (negative)
				bigDecimal = bigDecimal.subtract(bigDecimal2.divide(thirtyTwo));
			else
				bigDecimal = bigDecimal.add(bigDecimal2.divide(thirtyTwo));
			
			return bigDecimal.doubleValue();
		}
		catch (Exception e) {
			return null;
		}

	}

}
