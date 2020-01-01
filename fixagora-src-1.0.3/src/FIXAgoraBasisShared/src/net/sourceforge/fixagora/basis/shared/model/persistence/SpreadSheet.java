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
package net.sourceforge.fixagora.basis.shared.model.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * The Class SpreadSheet.
 */
@Entity
public class SpreadSheet extends AbstractBusinessComponent {

	private static final long serialVersionUID = 1L;

	@Column(unique = false, nullable = true)
	private String workbook = null;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name="spreadsheet_id", nullable=false)
	private List<SpreadSheetMDInput> spreadSheetMDInputs;	

	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name="spreadsheet_id", nullable=false)
	private List<SpreadSheetMDOutput> spreadSheetMDOutputs;		
	
	



	
	/**
	 * Gets the spread sheet md inputs.
	 *
	 * @return the spread sheet md inputs
	 */
	public List<SpreadSheetMDInput> getSpreadSheetMDInputs() {
	
		if(spreadSheetMDInputs==null)
			spreadSheetMDInputs=new ArrayList<SpreadSheetMDInput>();
		return spreadSheetMDInputs;
	}




	
	/**
	 * Sets the spread sheet md inputs.
	 *
	 * @param spreadSheetMDInputs the new spread sheet md inputs
	 */
	public void setSpreadSheetMDInputs(List<SpreadSheetMDInput> spreadSheetMDInputs) {
	
		this.spreadSheetMDInputs = spreadSheetMDInputs;
	}




	
	/**
	 * Gets the spread sheet md outputs.
	 *
	 * @return the spread sheet md outputs
	 */
	public List<SpreadSheetMDOutput> getSpreadSheetMDOutputs() {
	
		if(spreadSheetMDOutputs==null)
			spreadSheetMDOutputs=new ArrayList<SpreadSheetMDOutput>();
		return spreadSheetMDOutputs;
	}




	
	/**
	 * Sets the spread sheet md outputs.
	 *
	 * @param spreadSheetMDOutputs the new spread sheet md outputs
	 */
	public void setSpreadSheetMDOutputs(List<SpreadSheetMDOutput> spreadSheetMDOutputs) {
	
		this.spreadSheetMDOutputs = spreadSheetMDOutputs;
	}




	/**
	 * Gets the workbook.
	 *
	 * @return the workbook
	 */
	public String getWorkbook() {
	
		return workbook;
	}



	
	/**
	 * Sets the workbook.
	 *
	 * @param workbook the new workbook
	 */
	public void setWorkbook(String workbook) {
	
		this.workbook = workbook;
	}



	



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	@Override
	public String getIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/16x16/kspread.png";
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	@Override
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/basis/client/view/images/24x24/kspread_ksp.png";
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Spreadsheet";
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {
		
		super.makeEager();
		for(SpreadSheetMDInput spreadSheetMDInput: getSpreadSheetMDInputs())
			spreadSheetMDInput.makeEager();
		for(SpreadSheetMDOutput spreadSheetMDOutput: getSpreadSheetMDOutputs())
			spreadSheetMDOutput.makeEager();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getInputComponents()
	 */
	public List<AbstractBusinessComponent> getInputComponents()
	{
		
		ArrayList<AbstractBusinessComponent> inputComponents = new ArrayList<AbstractBusinessComponent>();
		for(SpreadSheetMDInput spreadSheetMDInput: getSpreadSheetMDInputs())
			inputComponents.add(spreadSheetMDInput.getBusinessComponent());
		return inputComponents;
		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getOutputComponents()
	 */
	public List<AbstractBusinessComponent> getOutputComponents()
	{
		ArrayList<AbstractBusinessComponent> outputComponents = new ArrayList<AbstractBusinessComponent>();
		for(SpreadSheetMDOutput spreadSheetMDOutput: getSpreadSheetMDOutputs())
			outputComponents.add(spreadSheetMDOutput.getBusinessComponent());
		return outputComponents;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.basis.server.control.component.SpreadSheetComponentHandler";
	}
	
	
}
