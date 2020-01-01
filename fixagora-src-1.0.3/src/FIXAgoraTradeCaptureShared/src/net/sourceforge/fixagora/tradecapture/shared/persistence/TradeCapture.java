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
package net.sourceforge.fixagora.tradecapture.shared.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;

/**
 * The Class TradeCapture.
 */
@Entity
public class TradeCapture extends AbstractBusinessComponent  {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = true, unique = false, length=2000)
	private String description = null;
		
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "tradecapture_id", nullable = false)
	private List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name="tradecapture_id", nullable=false)
	private List<AssignedTradeCaptureSource> assignedTradeCaptureSources;

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
	
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
	
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon()
	{
		return "/net/sourceforge/fixagora/sellside/client/view/images/16x16/kaddressbook.png";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon()
	{
		return "/net/sourceforge/fixagora/sellside/client/view/images/24x24/kaddressbook.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Trade Capture";
	}

	
	
	/**
	 * Gets the assigned trade capture targets.
	 *
	 * @return the assigned trade capture targets
	 */
	public List<AssignedTradeCaptureTarget> getAssignedTradeCaptureTargets(){
	
		if(assignedTradeCaptureTargets==null)
			assignedTradeCaptureTargets=new ArrayList<AssignedTradeCaptureTarget>();		
		return assignedTradeCaptureTargets;
	}

	
	
	/**
	 * Sets the assigned trade capture targets.
	 *
	 * @param assignedTradeCaptureTargets the new assigned trade capture targets
	 */
	public void setAssignedTradeCaptureTargets(List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets) {
	
		this.assignedTradeCaptureTargets = assignedTradeCaptureTargets;
	}

	
	/**
	 * Sets the assigned trade capture sources.
	 *
	 * @param assignedTradeCaptureSources the new assigned trade capture sources
	 */
	public void setAssignedTradeCaptureSources(List<AssignedTradeCaptureSource> assignedTradeCaptureSources) {
	
		this.assignedTradeCaptureSources = assignedTradeCaptureSources;
	}

	/**
	 * Gets the assigned trade capture sources.
	 *
	 * @return the assigned trade capture sources
	 */
	public List<AssignedTradeCaptureSource> getAssignedTradeCaptureSources() {
	
		if(assignedTradeCaptureSources==null)
			assignedTradeCaptureSources = new ArrayList<AssignedTradeCaptureSource>();
		return assignedTradeCaptureSources;
	}

	

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();
				
		for (AssignedTradeCaptureTarget assignedTradeCaptureTarget : getAssignedTradeCaptureTargets())
			assignedTradeCaptureTarget.makeEager();
		
		for (AssignedTradeCaptureSource assignedTradeCaptureSource : getAssignedTradeCaptureSources())
			assignedTradeCaptureSource.makeEager();
		
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.tradecapture.server.control.component.TradeCaptureComponentHandler";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getOutputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getOutputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for(AssignedTradeCaptureTarget assignedTradeCaptureTarget: getAssignedTradeCaptureTargets())
			abstractBusinessComponents.add(assignedTradeCaptureTarget.getAbstractBusinessComponent());
		return abstractBusinessComponents;
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getInputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getInputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for(AssignedTradeCaptureSource assignedTradeCaptureSource: getAssignedTradeCaptureSources())
			abstractBusinessComponents.add(assignedTradeCaptureSource.getBusinessComponent());
		return abstractBusinessComponents;
		
	}	

	
}
