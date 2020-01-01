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
 * The Class CopySpreadSheetCellRequest.
 */
public class CopySpreadSheetCellRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private long sourceSheetId = 0;
		
	private int sourceRow = 0;
	
	private int sourceRowCount = 0;
	
	private int sourceColumn = 0;
	
	private int sourceColumnCount = 0;
	
	/**
	 * Instantiates a new copy spread sheet cell request.
	 *
	 * @param sourceSheetId the source sheet id
	 * @param sourceRow the source row
	 * @param sourceRowCount the source row count
	 * @param sourceColumn the source column
	 * @param sourceColumnCount the source column count
	 * @param requestID the request id
	 */
	public CopySpreadSheetCellRequest(long sourceSheetId, int sourceRow, int sourceRowCount, int sourceColumn,
			int sourceColumnCount, long requestID) {

		super(requestID);
		this.sourceSheetId = sourceSheetId;
		this.sourceRow = sourceRow;
		this.sourceRowCount = sourceRowCount;
		this.sourceColumn = sourceColumn;
		this.sourceColumnCount = sourceColumnCount;
	}



	
	/**
	 * Gets the source sheet id.
	 *
	 * @return the source sheet id
	 */
	public long getSourceSheetId() {
	
		return sourceSheetId;
	}


	
	/**
	 * Gets the source row.
	 *
	 * @return the source row
	 */
	public int getSourceRow() {
	
		return sourceRow;
	}



	
	/**
	 * Gets the source row count.
	 *
	 * @return the source row count
	 */
	public int getSourceRowCount() {
	
		return sourceRowCount;
	}



	
	/**
	 * Gets the source column.
	 *
	 * @return the source column
	 */
	public int getSourceColumn() {
	
		return sourceColumn;
	}



	
	/**
	 * Gets the source column count.
	 *
	 * @return the source column count
	 */
	public int getSourceColumnCount() {
	
		return sourceColumnCount;
	}




	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onCopySpreadSheetCellRequest(this, channel);
		
	}




}
