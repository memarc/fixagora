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
 * The Class SolverRequest.
 */
public class SolverRequest extends AbstractBasisRequest {
	
	private static final long serialVersionUID = 1L;

	private SpreadSheetCell spreadSheetCell=null;
	
	private double solve = Double.NaN;
	
	/**
	 * Instantiates a new solver request.
	 *
	 * @param spreadSheetCell the spread sheet cell
	 * @param solve the solve
	 * @param requestID the request id
	 */
	public SolverRequest(SpreadSheetCell spreadSheetCell, double solve, long requestID) {

		super(requestID);
		this.spreadSheetCell = spreadSheetCell;
		this.solve = solve;
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
	 * Gets the solve.
	 *
	 * @return the solve
	 */
	public double getSolve() {
	
		return solve;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisRequest#handleAbstractFBasisRequest(net.sourceforge.fixagora.basis.shared.model.communication.BasisRequests, org.jboss.netty.channel.Channel)
	 */
	@Override
	public void handleAbstractFBasisRequest(BasisRequests basisRequests, Channel channel) {

		basisRequests.onSolverRequest(this, channel);	
	}

}
