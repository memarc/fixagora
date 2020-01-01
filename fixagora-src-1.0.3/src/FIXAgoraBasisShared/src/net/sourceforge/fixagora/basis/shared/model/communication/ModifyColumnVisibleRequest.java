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
 * The Class ModifyColumnVisibleRequest.
 */
public class ModifyColumnVisibleRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private long sheetId = 0;
		
	private int column = 0;
	
	private int columnCount = 0;
	
	private boolean visible = false;
	
	/**
	 * Instantiates a new modify column visible request.
	 *
	 * @param requestID the request id
	 * @param sheetId the sheet id
	 * @param column the column
	 * @param columnCount the column count
	 * @param visible the visible
	 */
	public ModifyColumnVisibleRequest(long requestID, long sheetId, int column, int columnCount,
			boolean visible) {

		super(requestID);
		this.sheetId = sheetId;
		this.column = column;
		this.columnCount = columnCount;
		this.visible = visible;
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
	 * Gets the column.
	 *
	 * @return the column
	 */
	public int getColumn() {
	
		return column;
	}



	
	/**
	 * Gets the column count.
	 *
	 * @return the column count
	 */
	public int getColumnCount() {
	
		return columnCount;
	}





	
	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
	
		return visible;
	}




	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onModifyColumnVisibleRequest(this, channel);
		
	}




}
