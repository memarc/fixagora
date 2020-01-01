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
package net.sourceforge.fixagora.basis.shared.model.communication;

import org.jboss.netty.channel.Channel;


/**
 * The Class ModifySheetCellRequest.
 */
public class ModifySheetCellRequest extends AbstractBasisRequest {
	
	/**
	 * The Enum ValueFormat.
	 */
	public enum ValueFormat {/** The date. */
DATE, /** The time. */
 TIME, /** The unknown. */
 UNKNOWN};
	
	private ValueFormat valueFormat = ValueFormat.UNKNOWN;

	private static final long serialVersionUID = 1L;

	private SpreadSheetCell spreadSheetCell=null;
		
	/**
	 * Instantiates a new modify sheet cell request.
	 *
	 * @param spreadSheetCell the spread sheet cell
	 * @param requestID the request id
	 */
	public ModifySheetCellRequest(SpreadSheetCell spreadSheetCell, long requestID) {

		super(requestID);
		this.spreadSheetCell = spreadSheetCell;
	}
	
	/**
	 * Instantiates a new modify sheet cell request.
	 *
	 * @param spreadSheetCell the spread sheet cell
	 * @param valueFormat the value format
	 * @param requestID the request id
	 */
	public ModifySheetCellRequest(SpreadSheetCell spreadSheetCell, ValueFormat valueFormat, long requestID) {

		super(requestID);
		this.spreadSheetCell = spreadSheetCell;
		this.valueFormat = valueFormat;
	}
	
	/**
	 * Gets the spread sheet cell.
	 *
	 * @return the spread sheet cell
	 */
	public SpreadSheetCell getSpreadSheetCell() {
	
		return spreadSheetCell;
	}

	
	/**
	 * Gets the value format.
	 *
	 * @return the value format
	 */
	public ValueFormat getValueFormat() {
	
		return valueFormat;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onModifySheetCellRequest(this, channel);	
	}

}
