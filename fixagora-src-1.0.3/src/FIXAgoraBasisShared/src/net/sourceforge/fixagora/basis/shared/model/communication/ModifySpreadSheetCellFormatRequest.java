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

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

import org.jboss.netty.channel.Channel;


/**
 * The Class ModifySpreadSheetCellFormatRequest.
 */
public class ModifySpreadSheetCellFormatRequest extends AbstractBasisRequest {

	private static final long serialVersionUID = 1L;

	private long sheetId = 0;
		
	private int row = 0;
	
	private int rowCount = 0;
	
	private int column = 0;
	
	private int columnCount = 0;
	
	private SpreadSheetCellFormat spreadSheetCellFormat = null;

	/**
	 * Instantiates a new modify spread sheet cell format request.
	 *
	 * @param requestID the request id
	 * @param sheetId the sheet id
	 * @param row the row
	 * @param rowCount the row count
	 * @param column the column
	 * @param columnCount the column count
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public ModifySpreadSheetCellFormatRequest(long requestID, long sheetId, int row, int rowCount, int column, int columnCount,
			SpreadSheetCellFormat spreadSheetCellFormat) {

		super(requestID);
		this.sheetId = sheetId;
		this.row = row;
		this.rowCount = rowCount;
		this.column = column;
		this.columnCount = columnCount;
		this.spreadSheetCellFormat = spreadSheetCellFormat;
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
	 * Gets the spread sheet cell format.
	 *
	 * @return the spread sheet cell format
	 */
	public SpreadSheetCellFormat getSpreadSheetCellFormat() {
	
		return spreadSheetCellFormat;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onModifySpreadSheetCellFormatRequest(this, channel);
		
	}




}
