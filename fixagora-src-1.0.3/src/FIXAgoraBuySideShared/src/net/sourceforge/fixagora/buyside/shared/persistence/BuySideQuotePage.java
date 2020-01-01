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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;

/**
 * The Class BuySideQuotePage.
 */
@Entity
public class BuySideQuotePage extends AbstractBusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * The Enum SortBy.
	 */
	public enum SortBy {
		
		/** The symbol. */
		SYMBOL, 
 /** The maturity. */
 MATURITY
	}

	@Column(nullable = true, unique = false, length = 2000)
	private String description = null;

	@Column(nullable = false, unique = false)
	private SortBy sortBy = SortBy.SYMBOL;

	@Column(nullable = false, unique = false)
	private Integer displayStyle = 0;

	@Column(nullable = false, unique = false)
	private Boolean showMarketDepth = true;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
	@JoinTable(name = "bsqp_absbs", joinColumns = { @JoinColumn(name = "buysidequotepage_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "absbs_id", nullable = false, updatable = false) })
	private List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities;

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getIcon()
	 */
	public String getIcon() {

		return "/net/sourceforge/fixagora/buyside/client/view/images/16x16/kdb_table.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getLargeIcon()
	 */
	public String getLargeIcon() {

		return "/net/sourceforge/fixagora/buyside/client/view/images/24x24/kdb_table.png";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#getBusinessObjectName()
	 */
	@Override
	public String getBusinessObjectName() {

		return "Buy Side Quote Page";
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
	 * Gets the show market depth.
	 *
	 * @return the show market depth
	 */
	public Boolean getShowMarketDepth() {

		return showMarketDepth;
	}

	/**
	 * Sets the show market depth.
	 *
	 * @param showMarketDepth the new show market depth
	 */
	public void setShowMarketDepth(Boolean showMarketDepth) {

		this.showMarketDepth = showMarketDepth;
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

	/**
	 * Gets the sort by.
	 *
	 * @return the sort by
	 */
	public SortBy getSortBy() {

		return sortBy;
	}

	/**
	 * Sets the sort by.
	 *
	 * @param sortBy the new sort by
	 */
	public void setSortBy(SortBy sortBy) {

		this.sortBy = sortBy;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#makeEager()
	 */
	@Override
	public void makeEager() {

		super.makeEager();

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : getAssignedBuySideBookSecurities())
			assignedBuySideBookSecurity.makeEager();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#isMovable()
	 */
	@Override
	public boolean isMovable() {

		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject#isAffectedBy(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject)
	 */
	@Override
	public boolean isAffectedBy(AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject.getId() == getParent().getId())
			return true;
		return super.isAffectedBy(abstractBusinessObject);
	}

}
