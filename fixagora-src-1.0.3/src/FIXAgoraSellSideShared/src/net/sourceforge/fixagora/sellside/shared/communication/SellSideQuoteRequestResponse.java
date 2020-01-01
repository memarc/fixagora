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
package net.sourceforge.fixagora.sellside.shared.communication;


/**
 * The Class SellSideQuoteRequestResponse.
 */
public class SellSideQuoteRequestResponse extends AbstractSellSideResponse {

	private static final long serialVersionUID = 1L;

	private SellSideQuoteRequestEntry sellSideQuoteRequestEntry = null;

	/**
	 * Instantiates a new sell side quote request response.
	 *
	 * @param sellSideQuoteRequestEntry the sell side quote request entry
	 */
	public SellSideQuoteRequestResponse(SellSideQuoteRequestEntry sellSideQuoteRequestEntry) {

		super(null);
		this.sellSideQuoteRequestEntry = sellSideQuoteRequestEntry;
	}

	/**
	 * Instantiates a new sell side quote request response.
	 *
	 * @param sellSideQuoteRequestEntry the sell side quote request entry
	 * @param quoteRequestRequest the quote request request
	 */
	public SellSideQuoteRequestResponse(SellSideQuoteRequestEntry sellSideQuoteRequestEntry, SellSideQuoteRequestRequest quoteRequestRequest) {

		super(quoteRequestRequest);
		this.sellSideQuoteRequestEntry = sellSideQuoteRequestEntry;
	}

	/**
	 * Gets the sell side quote request entry.
	 *
	 * @return the sell side quote request entry
	 */
	public SellSideQuoteRequestEntry getSellSideQuoteRequestEntry() {

		return sellSideQuoteRequestEntry;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideResponse#handleSellSideResponse(net.sourceforge.fixagora.sellside.shared.communication.SellSideResponses)
	 */
	@Override
	public void handleSellSideResponse(SellSideResponses sellSideResponses) {

		sellSideResponses.onSellSideQuoteRequestResponse(this);

	}

}
