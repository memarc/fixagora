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
 * The Class ErrorResponse.
 */
public class ErrorResponse extends AbstractBasisResponse {

	private static final long serialVersionUID = 1L;
	
	private String text = "An error occured during transaction.";
	
	/**
	 * Instantiates a new error response.
	 *
	 * @param abstractRequest the abstract request
	 */
	public ErrorResponse(AbstractRequest abstractRequest) {

		super(abstractRequest);
	}
	
	/**
	 * Instantiates a new error response.
	 *
	 * @param abstractRequest the abstract request
	 * @param text the text
	 */
	public ErrorResponse(AbstractRequest abstractRequest, String text) {

		super(abstractRequest);
		this.text = text;
	}	

	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
	
		return text;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.AbstractBasisResponse#handleAbstractFBasisResponse(net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses)
	 */
	@Override
	public void handleAbstractFBasisResponse(BasisResponses fBasisResponses) {

	}

}
