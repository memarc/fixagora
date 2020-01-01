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
 * The Class FillSpreadSheetCellRequest.
 */
public class FillSpreadSheetCellRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum Direction.
	 */
	public enum Direction{/** The down. */
DOWN,/** The right. */
RIGHT,/** The up. */
UP,/** The left. */
LEFT};

	private long sourceSheetId = 0;
		
	private int sourceRow = 0;
	
	private int sourceRowCount = 0;
	
	private int sourceColumn = 0;
	
	private int sourceColumnCount = 0;
	
	private Direction direction = null;
	
	/**
	 * Instantiates a new fill spread sheet cell request.
	 *
	 * @param sourceSheetId the source sheet id
	 * @param sourceRow the source row
	 * @param sourceRowCount the source row count
	 * @param sourceColumn the source column
	 * @param sourceColumnCount the source column count
	 * @param direction the direction
	 * @param requestID the request id
	 */
	public FillSpreadSheetCellRequest(long sourceSheetId, int sourceRow, int sourceRowCount, int sourceColumn,
			int sourceColumnCount,Direction direction , long requestID) {

		super(requestID);
		this.sourceSheetId = sourceSheetId;
		this.direction = direction;
		this.sourceRow = sourceRow;
		this.sourceRowCount = sourceRowCount;
		this.sourceColumn = sourceColumn;
		this.sourceColumnCount = sourceColumnCount;
	}

	
	
	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public Direction getDirection() {
	
		return direction;
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

		basisRequests.onFillSpreadSheetCellRequest(this, channel);
		
	}




}
