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


/**
 * The Class SolverResponse.
 */
public class SolverResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private boolean solved = false;

	private Double value = null;
		
	/**
	 * Instantiates a new solver response.
	 *
	 * @param solverRequest the solver request
	 * @param solved the solved
	 */
	public SolverResponse(SolverRequest solverRequest, boolean solved) {

		super(solverRequest);
		this.solved = solved;
	}
	
	/**
	 * Instantiates a new solver response.
	 *
	 * @param solverRequest the solver request
	 * @param value the value
	 */
	public SolverResponse(SolverRequest solverRequest, double value) {

		super(solverRequest);
		this.solved = true;
		this.value = value;
	}
	
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Double getValue() {
	
		return value;
	}

	/**
	 * Checks if is solved.
	 *
	 * @return true, if is solved
	 */
	public boolean isSolved() {
	
		return solved;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {
		
	}

}
