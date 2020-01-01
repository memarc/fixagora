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
package net.sourceforge.fixagora.sellside.shared.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;

/**
 * The Class SellSideBook.
 */
@Entity
public class SellSideBook extends AbstractBusinessComponent implements BusinessObjectGroup {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum CalcMethod.
	 */
	public enum CalcMethod {
		
		/** The none. */
		NONE, 
 /** The default. */
 DEFAULT, 
 /** The disc. */
 DISC, 
 /** The mat. */
 MAT, 
 /** The oddf. */
 ODDF, 
 /** The oddl. */
 ODDL, 
 /** The tbill. */
 TBILL
	}

	@Column(nullable = true, unique = false, length = 2000)
	private String description = null;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "sellsidebook_id", nullable = false)
	private List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "sellsidebook_id", nullable = false)
	private List<AssignedSellSideAcceptor> assignedSellSideFIXAcceptors;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "sellsidebook_id", nullable = false)
	private List<AssignedSellSideTradeCaptureTarget> assignedSellSideTradeCaptureTargets;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sellsidebook_id", nullable = false)
	private List<SellSideBookMDInput> sellSideBookMDInputs;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private BankCalendar bankCalendar = null;

	@Column(nullable = false, unique = false)
	private Integer valuta = 0;

	@Column(unique = false, nullable = false)
	private Integer daycountConvention = 1;

	@Column(nullable = false, unique = false)
	private Integer displayStyle = 0;

	@Column(nullable = true, unique = false)
	private Integer killFill = null;

	@Column(nullable = false, unique = false)
	private CalcMethod calcMethod = CalcMethod.DEFAULT;

	@Column(nullable = false, unique = false)
	private Boolean fractionalDisplay = false;

	@Column(nullable = false, unique = false)
	private Boolean absolutChange = true;

	/**
	 * Gets the calc method.
	 *
	 * @return the calc method
	 */
	public CalcMethod getCalcMethod() {

		return calcMethod;
	}

	/**
	 * Sets the calc method.
	 *
	 * @param calcMethod the new calc method
	 */
	public void setCalcMethod(CalcMethod calcMethod) {

		this.calcMethod = calcMethod;
	}

	/**
	 * Gets the fractional display.
	 *
	 * @return the fractional display
	 */
	public Boolean getFractionalDisplay() {

		return fractionalDisplay;
	}

	/**
	 * Sets the fractional display.
	 *
	 * @param fractionalDisplay the new fractional display
	 */
	public void setFractionalDisplay(Boolean fractionalDisplay) {

		this.fractionalDisplay = fractionalDisplay;
	}

	/**
	 * Gets the absolut change.
	 *
	 * @return the absolut change
	 */
	public Boolean getAbsolutChange() {

		return absolutChange;
	}

	/**
	 * Sets the absolut change.
	 *
	 * @param absolutChange the new absolut change
	 */
	public void setAbsolutChange(Boolean absolutChange) {

		this.absolutChange = absolutChange;
	}

	/**
	 * Gets the display style.
	 *
	 * @return the display style
	 */
	public Integer getDisplayStyle() {

		return displayStyle;
	}

	/**
	 * Sets the display style.
	 *
	 * @param displayStyle the new display style
	 */
	public void setDisplayStyle(Integer displayStyle) {

		this.displayStyle = displayStyle;
	}

	/**
	 * Gets the kill fill.
	 *
	 * @return the kill fill
	 */
	public Integer getKillFill() {

		return killFill;
	}

	/**
	 * Sets the kill fill.
	 *
	 * @param killFill the new kill fill
	 */
	public void setKillFill(Integer killFill) {

		this.killFill = killFill;
	}

	/**
	 * Gets the bank calendar.
	 *
	 * @return the bank calendar
	 */
	public BankCalendar getBankCalendar() {

		return bankCalendar;
	}

	/**
	 * Sets the bank calendar.
	 *
	 * @param bankCalendar the new bank calendar
	 */
	public void setBankCalendar(BankCalendar bankCalendar) {

		this.bankCalendar = bankCalendar;
	}

	/**
	 * Gets the valuta.
	 *
	 * @return the valuta
	 */
	public Integer getValuta() {

		return valuta;
	}

	/**
	 * Sets the valuta.
	 *
	 * @param valuta the new valuta
	 */
	public void setValuta(Integer valuta) {

		this.valuta = valuta;
	}

	/**
	 * Gets the daycount convention.
	 *
	 * @return the daycount convention
	 */
	public Integer getDaycountConvention() {

		return daycountConvention;
	}

	/**
	 * Sets the daycount convention.
	 *
	 * @param daycountConvention the new daycount convention
	 */
	public void setDaycountConvention(Integer daycountConvention) {

		this.daycountConvention = daycountConvention;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getDescription()
	 */
	public String getDescription() {

		return description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {

		this.description = description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon() {

		return "/net/sourceforge/fixagora/sellside/client/view/images/16x16/kaddressbook.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/sellside/client/view/images/24x24/kaddressbook.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Sell Side Book";
	}

	/**
	 * Gets the assigned sell side book securities.
	 *
	 * @return the assigned sell side book securities
	 */
	public List<AssignedSellSideBookSecurity> getAssignedSellSideBookSecurities() {

		if (assignedSellSideBookSecurities == null)
			assignedSellSideBookSecurities = new ArrayList<AssignedSellSideBookSecurity>();
		return assignedSellSideBookSecurities;
	}

	/**
	 * Sets the assigned sell side book securities.
	 *
	 * @param assignedSellSideBookSecurities the new assigned sell side book securities
	 */
	public void setAssignedSellSideBookSecurities(List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities) {

		this.assignedSellSideBookSecurities = assignedSellSideBookSecurities;
	}

	/**
	 * Gets the assigned sell side trade capture targets.
	 *
	 * @return the assigned sell side trade capture targets
	 */
	public List<AssignedSellSideTradeCaptureTarget> getAssignedSellSideTradeCaptureTargets() {

		if (assignedSellSideTradeCaptureTargets == null)
			assignedSellSideTradeCaptureTargets = new ArrayList<AssignedSellSideTradeCaptureTarget>();
		return assignedSellSideTradeCaptureTargets;

	}

	/**
	 * Sets the assigned sell side trade capture targets.
	 *
	 * @param assignedSellSideTradeCaptureTargets the new assigned sell side trade capture targets
	 */
	public void setAssignedSellSideTradeCaptureTargets(List<AssignedSellSideTradeCaptureTarget> assignedSellSideTradeCaptureTargets) {

		this.assignedSellSideTradeCaptureTargets = assignedSellSideTradeCaptureTargets;
	}

	/**
	 * Gets the assigned sell side fix acceptors.
	 *
	 * @return the assigned sell side fix acceptors
	 */
	public List<AssignedSellSideAcceptor> getAssignedSellSideFIXAcceptors() {

		if (assignedSellSideFIXAcceptors == null)
			assignedSellSideFIXAcceptors = new ArrayList<AssignedSellSideAcceptor>();
		return assignedSellSideFIXAcceptors;
	}

	/**
	 * Sets the assigned sell side fix acceptors.
	 *
	 * @param assignedSellSideFIXAcceptors the new assigned sell side fix acceptors
	 */
	public void setAssignedSellSideFIXAcceptors(List<AssignedSellSideAcceptor> assignedSellSideFIXAcceptors) {

		this.assignedSellSideFIXAcceptors = assignedSellSideFIXAcceptors;
	}

	/**
	 * Gets the sell side book md inputs.
	 *
	 * @return the sell side book md inputs
	 */
	public List<SellSideBookMDInput> getSellSideBookMDInputs() {

		if (sellSideBookMDInputs == null)
			sellSideBookMDInputs = new ArrayList<SellSideBookMDInput>();
		return sellSideBookMDInputs;
	}

	/**
	 * Sets the sell side book md inputs.
	 *
	 * @param sellSideBookMDInputs the new sell side book md inputs
	 */
	public void setSellSideBookMDInputs(List<SellSideBookMDInput> sellSideBookMDInputs) {

		this.sellSideBookMDInputs = sellSideBookMDInputs;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : getAssignedSellSideBookSecurities())
			assignedSellSideBookSecurity.makeEager();

		for (AssignedSellSideAcceptor assignedSellSideFIXAcceptor : getAssignedSellSideFIXAcceptors())
			assignedSellSideFIXAcceptor.makeEager();

		for (AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget : getAssignedSellSideTradeCaptureTargets())
			assignedSellSideTradeCaptureTarget.makeEager();

		for (SellSideBookMDInput sellSideBookMDInput : getSellSideBookMDInputs())
			sellSideBookMDInput.makeEager();

		bankCalendar.getName();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getChildClass()
	 */
	@Override
	public Class<? extends AbstractBusinessObject> getChildClass() {

		return SellSideQuotePage.class;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#isSubfolderAllowed()
	 */
	@Override
	public boolean isSubfolderAllowed() {

		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getComponentClass()
	 */
	@Override
	public String getComponentClass() {

		return "net.sourceforge.fixagora.sellside.server.control.component.SellSideBookComponentHandler";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getOutputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getOutputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for (AssignedSellSideAcceptor assignedSellSideFIXAcceptor : getAssignedSellSideFIXAcceptors())
			abstractBusinessComponents.add(assignedSellSideFIXAcceptor.getAcceptor());
		for (AssignedSellSideTradeCaptureTarget assignedSellSideTradeCaptureTarget : getAssignedSellSideTradeCaptureTargets())
			abstractBusinessComponents.add(assignedSellSideTradeCaptureTarget.getAbstractBusinessComponent());
		return abstractBusinessComponents;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getInputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getInputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for (SellSideBookMDInput sellSideBookMDInput : getSellSideBookMDInputs())
			abstractBusinessComponents.add(sellSideBookMDInput.getBusinessComponent());
		return abstractBusinessComponents;

	}

}
