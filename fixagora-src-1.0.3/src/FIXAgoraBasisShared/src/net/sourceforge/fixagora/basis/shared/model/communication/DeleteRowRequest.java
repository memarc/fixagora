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
 * The Class DeleteRowRequest.
 */
public class DeleteRowRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private long sheetId = 0;
		
	private int row = 0;
	
	private int rowCount = 0;
		
	/**
	 * Instantiates a new delete row request.
	 *
	 * @param requestID the request id
	 * @param sheetId the sheet id
	 * @param row the row
	 * @param rowCount the row count
	 */
	public DeleteRowRequest(long requestID, long sheetId, int row, int rowCount) {

		super(requestID);
		this.sheetId = sheetId;
		this.row = row;
		this.rowCount = rowCount;
	}
	


	
	/**
	 * Gets the sheet id.
	 *
	 * @return the sheet id
	 */
	public long getSheetId() {
	
		return sheetId;
	}




	
	
	/**
	 * Gets the row.
	 *
	 * @return the row
	 */
	public int getRow() {
	
		return row;
	}




	
	/**
	 * Gets the row count.
	 *
	 * @return the row count
	 */
	public int getRowCount() {
	
		return rowCount;
	}





	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onDeleteRowRequest(this, channel);
		
	}




}
