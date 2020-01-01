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
package net.sourceforge.fixagora.buyside.shared.persistence;

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
 * The Class BuySideBook.
 */
@Entity
public class BuySideBook extends AbstractBusinessComponent implements BusinessObjectGroup {

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
	@JoinColumn(name = "buysidebook_id", nullable = false)
	private List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "buysidebook_id", nullable = false)
	private List<AssignedBuySideInitiator> assignedBuySideFIXInitiators;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "id asc")
	@JoinColumn(name = "buysidebook_id", nullable = false)
	private List<AssignedBuySideTradeCaptureTarget> assignedTradeCaptureTargets;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private BankCalendar bankCalendar = null;

	@Column(nullable = false, unique = false)
	private Integer valuta = 1;

	@Column(nullable = false, unique = false)
	private CalcMethod calcMethod = CalcMethod.DEFAULT;

	@Column(nullable = false, unique = false)
	private Boolean fractionalDisplay = false;

	@Column(nullable = true, unique = false)
	private Boolean absolutChange = true;

	@Column(unique = false, nullable = false)
	private Integer daycountConvention = 1;

	@Column(nullable = false, unique = false)
	private Integer displayStyle = 0;

	@Column(nullable = true, unique = false)
	private Integer killFill = null;

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

		return "/net/sourceforge/fixagora/buyside/client/view/images/16x16/kaddressbook.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/buyside/client/view/images/24x24/kaddressbook.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Buy Side Book";
	}

	/**
	 * Gets the assigned buy side book securities.
	 *
	 * @return the assigned buy side book securities
	 */
	public List<AssignedBuySideBookSecurity> getAssignedBuySideBookSecurities() {

		if (assignedBuySideBookSecurities == null)
			assignedBuySideBookSecurities = new ArrayList<AssignedBuySideBookSecurity>();
		return assignedBuySideBookSecurities;
	}

	/**
	 * Sets the assigned buy side book securities.
	 *
	 * @param assignedBuySideBookSecurities the new assigned buy side book securities
	 */
	public void setAssignedBuySideBookSecurities(List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities) {

		this.assignedBuySideBookSecurities = assignedBuySideBookSecurities;
	}

	/**
	 * Gets the assigned buy side fix initiators.
	 *
	 * @return the assigned buy side fix initiators
	 */
	public List<AssignedBuySideInitiator> getAssignedBuySideFIXInitiators() {

		if (assignedBuySideFIXInitiators == null)
			assignedBuySideFIXInitiators = new ArrayList<AssignedBuySideInitiator>();
		return assignedBuySideFIXInitiators;
	}

	/**
	 * Sets the assigned buy side fix initiators.
	 *
	 * @param assignedBuySideFIXInitiators the new assigned buy side fix initiators
	 */
	public void setAssignedBuySideFIXInitiators(List<AssignedBuySideInitiator> assignedBuySideFIXInitiators) {

		this.assignedBuySideFIXInitiators = assignedBuySideFIXInitiators;
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
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : getAssignedBuySideBookSecurities())
			assignedBuySideBookSecurity.makeEager();

		for (AssignedBuySideInitiator assignedBuySideFIXInitiator : getAssignedBuySideFIXInitiators())
			assignedBuySideFIXInitiator.makeEager();

		for (AssignedBuySideTradeCaptureTarget assignedTradeCaptureTarget : getAssignedTradeCaptureTargets())
			assignedTradeCaptureTarget.makeEager();

		bankCalendar.getName();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup#getChildClass()
	 */
	@Override
	public Class<? extends AbstractBusinessObject> getChildClass() {

		return BuySideQuotePage.class;
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

		return "net.sourceforge.fixagora.buyside.server.control.component.BuySideBookComponentHandler";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getOutputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getOutputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for (AssignedBuySideTradeCaptureTarget assignedTradeCaptureTarget : getAssignedTradeCaptureTargets())
			abstractBusinessComponents.add(assignedTradeCaptureTarget.getAbstractBusinessComponent());
		return abstractBusinessComponents;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent#getInputComponents()
	 */
	@Override
	public List<AbstractBusinessComponent> getInputComponents() {

		List<AbstractBusinessComponent> abstractBusinessComponents = new ArrayList<AbstractBusinessComponent>();
		for (AssignedBuySideInitiator assignedSellSideFIXAcceptor : getAssignedBuySideFIXInitiators())
			abstractBusinessComponents.add(assignedSellSideFIXAcceptor.getInitiator());
		return abstractBusinessComponents;

	}

	/**
	 * Gets the assigned trade capture targets.
	 *
	 * @return the assigned trade capture targets
	 */
	public List<AssignedBuySideTradeCaptureTarget> getAssignedTradeCaptureTargets() {

		if (assignedTradeCaptureTargets == null)
			assignedTradeCaptureTargets = new ArrayList<AssignedBuySideTradeCaptureTarget>();

		return assignedTradeCaptureTargets;
	}

	/**
	 * Sets the assigned trade capture targets.
	 *
	 * @param assignedTradeCaptureTargets the new assigned trade capture targets
	 */
	public void setAssignedTradeCaptureTargets(List<AssignedBuySideTradeCaptureTarget> assignedTradeCaptureTargets) {

		this.assignedTradeCaptureTargets = assignedTradeCaptureTargets;
	}

}
