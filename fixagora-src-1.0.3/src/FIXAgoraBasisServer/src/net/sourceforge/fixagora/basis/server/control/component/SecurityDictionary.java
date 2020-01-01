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
package net.sourceforge.fixagora.basis.server.control.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventDate;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventTime;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAttribute;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityComplexEvent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityDetails;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityEvent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityLeg;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityUnderlying;

import org.apache.log4j.Logger;

import quickfix.Group;

/**
 * The Class SecurityDictionary.
 */
public class SecurityDictionary {

	/** The original security map. */
	Map<String, FSecurity> originalSecurityMap = new HashMap<String, FSecurity>();

	/** The id security map. */
	Map<Long, FSecurity> idSecurityMap = new HashMap<Long, FSecurity>();

	/** The alternative security map. */
	Map<String, Map<String, FSecurity>> alternativeSecurityMap = new HashMap<String, Map<String, FSecurity>>();

	private SimpleDateFormat localMarketDateFormat = new SimpleDateFormat("yyyyMMdd");

	private SimpleDateFormat tzTimeFormat = new SimpleDateFormat("HH:mm:ss");

	private static Logger log = Logger.getLogger(SecurityDictionary.class);

	/**
	 * Removes the security.
	 *
	 * @param security the security
	 */
	public synchronized void removeSecurity(FSecurity security) {

		List<String> toRemove = new ArrayList<String>();
		for (Entry<String, FSecurity> entry : originalSecurityMap.entrySet())
			if (entry.getValue().getId() == security.getId())
				toRemove.add(entry.getKey());
		for (String id : toRemove)
			originalSecurityMap.remove(id);

		idSecurityMap.remove(security.getId());

		List<String> sources = new ArrayList<String>();
		sources.addAll(alternativeSecurityMap.keySet());

		for (String idSource : sources) {
			Map<String, FSecurity> securityMap = alternativeSecurityMap.get(idSource);
			List<String> toRemove2 = new ArrayList<String>();
			for (Entry<String, FSecurity> entry : securityMap.entrySet())
				if (entry.getValue().getId() == security.getId())
					toRemove2.add(entry.getKey());
			for (String id : toRemove2)
				securityMap.remove(id);
		}
	}

	/**
	 * Adds the security.
	 *
	 * @param security the security
	 */
	public synchronized void addSecurity(FSecurity security) {

		originalSecurityMap.put(security.getSecurityID(), security);
		idSecurityMap.put(security.getId(), security);
		for (SecurityAltIDGroup securityAltIDGroup : security.getSecurityDetails().getSecurityAltIDGroups()) {
			Map<String, FSecurity> securityMap = alternativeSecurityMap.get(securityAltIDGroup.getSecurityAltIDSource());
			if (securityMap == null) {
				securityMap = new HashMap<String, FSecurity>();
				alternativeSecurityMap.put(securityAltIDGroup.getSecurityAltIDSource(), securityMap);
			}
			securityMap.put(securityAltIDGroup.getSecurityAltID(), security);
		}

	}

	/**
	 * Update security.
	 *
	 * @param security the security
	 */
	public synchronized void updateSecurity(FSecurity security) {

		removeSecurity(security);
		addSecurity(security);

	}

	/**
	 * Gets the security for default security id.
	 *
	 * @param securityID the security id
	 * @param idSource the id source
	 * @return the security for default security id
	 */
	public synchronized FSecurity getSecurityForDefaultSecurityID(String securityID, String idSource) {

		if (securityID == null || idSource == null)
			return null;

		FSecurity defaultSecurity = originalSecurityMap.get(securityID);
		if (defaultSecurity != null && idSource.equals(defaultSecurity.getSecurityDetails().getSecurityIDSource()))
			return defaultSecurity;

		Map<String, FSecurity> securityMap = alternativeSecurityMap.get(idSource);
		if (securityMap == null)
			return null;
		FSecurity security = securityMap.get(securityID);
		return security;
	}

	/**
	 * Gets the security for default security id.
	 *
	 * @param securityID the security id
	 * @return the security for default security id
	 */
	public synchronized FSecurity getSecurityForDefaultSecurityID(String securityID) {

		if (securityID == null)
			return null;

		FSecurity defaultSecurity = originalSecurityMap.get(securityID);
		return defaultSecurity;
	}

	/**
	 * Gets the security for business object id.
	 *
	 * @param securityID the security id
	 * @return the security for business object id
	 */
	public synchronized FSecurity getSecurityForBusinessObjectID(long securityID) {

		return idSecurityMap.get(securityID);
	}

	/**
	 * Gets the alternative security id.
	 *
	 * @param securityID the security id
	 * @param alternativeIDSource the alternative id source
	 * @return the alternative security id
	 */
	public synchronized String getAlternativeSecurityID(String securityID, String alternativeIDSource) {

		if (securityID == null || alternativeIDSource == null)
			return null;

		FSecurity defaultSecurity = originalSecurityMap.get(securityID);
		if (defaultSecurity != null && alternativeIDSource.equals(defaultSecurity.getSecurityDetails().getSecurityIDSource()))
			return defaultSecurity.getSecurityID();

		if (defaultSecurity == null)
			return null;

		for (SecurityAltIDGroup securityAltIDGroup : defaultSecurity.getSecurityDetails().getSecurityAltIDGroups())
			if (alternativeIDSource.equals(securityAltIDGroup.getSecurityAltIDSource()))
				return securityAltIDGroup.getSecurityAltID();

		return null;
	}

	/**
	 * Gets the related sym.
	 *
	 * @param fSecurity the f security
	 * @param major the major
	 * @param minor the minor
	 * @param servicepack the servicepack
	 * @return the related sym
	 */
	public synchronized Group getRelatedSym(FSecurity fSecurity, int major, int minor, int servicepack) {

		final Group group = new Group(146, 55, new int[] { 55, 65, 48, 22, 454, 460, 461, 167, 762, 200, 541, 224, 225, 239, 226, 227, 228, 255, 543, 470, 471,
				472, 240, 202, 947, 206, 231, 223, 207, 106, 348, 349, 107, 350, 351, 691, 667, 875, 876, 864, 873, 874, 965, 966, 1049, 967, 968, 969, 970,
				971, 996, 997, 1079, 1151, 1146, 1147, 1227, 1191, 1192, 1193, 1194, 1195, 1196, 1198, 1199, 1200, 201, 1244, 1242, 1197, 1435, 1439, 1449,
				1450, 1478, 1479, 1480, 1481, 1482, 1483, 668, 869, 870, 913, 914, 915, 918, 788, 916, 917, 919, 898, 711, 15, 232, 233, 234, 555, 218, 220,
				221, 222, 662, 663, 699, 761, 235, 236, 701, 696, 697, 698 });
		group.setString(55, fSecurity.getName());
		if (fSecurity.getSecurityDetails().getSymbolSfx() != null && fSecurity.getSecurityDetails().getSymbolSfx().trim().length() > 0)
			group.setString(65, fSecurity.getSecurityDetails().getSymbolSfx());
		if (fSecurity.getSecurityID() != null && fSecurity.getSecurityID().trim().length() > 0)
			group.setString(48, fSecurity.getSecurityID());
		if (fSecurity.getSecurityDetails().getSecurityIDSource() != null && fSecurity.getSecurityDetails().getSecurityIDSource().trim().length() > 0)
			group.setString(22, fSecurity.getSecurityDetails().getSecurityIDSource());
		for (SecurityAltIDGroup securityAltIDGroup : fSecurity.getSecurityDetails().getSecurityAltIDGroups()) {
			final Group securityAltID = new Group(454, 455, new int[] { 455, 456 });
			if (securityAltIDGroup.getSecurityAltID() != null && securityAltIDGroup.getSecurityAltID().length() > 0)
				securityAltID.setString(455, securityAltIDGroup.getSecurityAltID());
			if (securityAltIDGroup.getSecurityAltIDSource() != null && securityAltIDGroup.getSecurityAltIDSource().trim().length() > 0)
				securityAltID.setString(456, securityAltIDGroup.getSecurityAltIDSource());
			group.addGroup(securityAltID);
		}

		if (fSecurity.getSecurityDetails().getProduct() != null)
			group.setInt(460, fSecurity.getSecurityDetails().getProduct());

		if (fSecurity.getSecurityDetails().getcFICode() != null && fSecurity.getSecurityDetails().getcFICode().trim().length() > 0)
			group.setString(461, fSecurity.getSecurityDetails().getcFICode());

		if (fSecurity.getSecurityDetails().getFpool() != null && fSecurity.getSecurityDetails().getFpool().trim().length() > 0)
			group.setString(691, fSecurity.getSecurityDetails().getFpool());

		if (fSecurity.getSecurityDetails().getSecurityType() != null && fSecurity.getSecurityDetails().getSecurityType().trim().length() > 0)
			group.setString(167, fSecurity.getSecurityDetails().getSecurityType());

		if (fSecurity.getSecurityDetails().getSecuritySubType() != null && fSecurity.getSecurityDetails().getSecuritySubType().trim().length() > 0)
			group.setString(762, fSecurity.getSecurityDetails().getSecuritySubType());

		if (fSecurity.getMaturity() != null)
			group.setString(541, localMarketDateFormat.format(fSecurity.getMaturity()));

		if (fSecurity.getSecurityDetails().getCouponPaymentDate() != null)
			group.setString(224, localMarketDateFormat.format(fSecurity.getSecurityDetails().getCouponPaymentDate()));
		if (fSecurity.getSecurityDetails().getIssueDate() != null)
			group.setString(225, localMarketDateFormat.format(fSecurity.getSecurityDetails().getIssueDate()));
		if (fSecurity.getSecurityDetails().getFactor() != null)
			group.setDouble(228, fSecurity.getSecurityDetails().getFactor());
		if (fSecurity.getSecurityDetails().getCreditRating() != null && fSecurity.getSecurityDetails().getCreditRating().trim().length() > 0)
			group.setString(255, fSecurity.getSecurityDetails().getCreditRating());
		if (fSecurity.getSecurityDetails().getInstrumentRegistry() != null && fSecurity.getSecurityDetails().getInstrumentRegistry().trim().length() > 0)
			group.setString(543, fSecurity.getSecurityDetails().getInstrumentRegistry());
		if (fSecurity.getSecurityDetails().getCountryOfIssue() != null && fSecurity.getSecurityDetails().getCountryOfIssue().trim().length() > 0)
			group.setString(470, fSecurity.getSecurityDetails().getCountryOfIssue());
		if (fSecurity.getSecurityDetails().getStateOfIssue() != null && fSecurity.getSecurityDetails().getStateOfIssue().trim().length() > 0)
			group.setString(471, fSecurity.getSecurityDetails().getStateOfIssue());
		if (fSecurity.getSecurityDetails().getLocaleOfIssue() != null && fSecurity.getSecurityDetails().getLocaleOfIssue().trim().length() > 0)
			group.setString(472, fSecurity.getSecurityDetails().getLocaleOfIssue());
		if (fSecurity.getSecurityDetails().getRedemptionDate() != null)
			group.setString(240, localMarketDateFormat.format(fSecurity.getSecurityDetails().getRedemptionDate()));
		if (fSecurity.getSecurityDetails().getStrikePrice() != null)
			group.setDouble(202, fSecurity.getSecurityDetails().getStrikePrice());
		if (fSecurity.getSecurityDetails().getStrikeCurrency() != null && fSecurity.getSecurityDetails().getStrikeCurrency().trim().length() > 0)
			group.setString(947, fSecurity.getSecurityDetails().getStrikeCurrency());
		if (fSecurity.getSecurityDetails().getContractMultiplier() != null)
			group.setDouble(231, fSecurity.getSecurityDetails().getContractMultiplier());
		if (fSecurity.getSecurityDetails().getCouponRate() != null)
			group.setDouble(223, fSecurity.getSecurityDetails().getCouponRate());
		if (fSecurity.getSecurityDetails().getSecurityExchange() != null && fSecurity.getSecurityDetails().getSecurityExchange().trim().length() > 0)
			group.setString(207, fSecurity.getSecurityDetails().getSecurityExchange());
		if (fSecurity.getSecurityDetails().getIssuer() != null && fSecurity.getSecurityDetails().getIssuer().trim().length() > 0)
			group.setString(106, fSecurity.getSecurityDetails().getIssuer());
		if (fSecurity.getSecurityDetails().getDescription() != null && fSecurity.getSecurityDetails().getDescription().length() > 0)
			group.setString(107, fSecurity.getSecurityDetails().getDescription());
		if (fSecurity.getSecurityDetails().getContractSettlMonth() != null)
			group.setString(667, localMarketDateFormat.format(fSecurity.getSecurityDetails().getContractSettlMonth()));
		if (fSecurity.getSecurityDetails().getCpProgramm() != null)
			group.setInt(875, fSecurity.getSecurityDetails().getCpProgramm());
		if (fSecurity.getSecurityDetails().getCpRegType() != null && fSecurity.getSecurityDetails().getCpRegType().trim().length() > 0)
			group.setString(876, fSecurity.getSecurityDetails().getCpRegType());

		for (SecurityEvent securityEvent : fSecurity.getSecurityDetails().getSecurityEvents()) {
			if (securityEvent.getEventType() != null) {

				boolean valid = false;

				if (major <= 4 && securityEvent.getEventType() <= 4)
					valid = true;

				if (major > 4) {
					if (securityEvent.getEventType() == 99)
						valid = true;
					else if ((major > 5 || minor == 0) && securityEvent.getEventType() <= 6)
						valid = true;
					else if ((major > 5 || minor > 0 || servicepack > 0) && securityEvent.getEventType() <= 19)
						valid = true;
				}

				if (valid) {

					final Group securityEventGroup = new Group(864, 865, new int[] { 865, 866, 867, 868, 1145 });
					securityEventGroup.setInt(865, securityEvent.getEventType());
					if (securityEvent.getEventDate() != null)
						securityEventGroup.setString(866, localMarketDateFormat.format(securityEvent.getEventDate()));
					if (major > 4 && (major > 5 || minor > 0 || servicepack > 0) && securityEvent.getEventTime() != null)
						securityEventGroup.setUtcTimeStamp(1145, securityEvent.getEventTime());
					if (securityEvent.getEventPrice() != null)
						securityEventGroup.setDouble(867, securityEvent.getEventPrice());
					if (securityEvent.getEventText() != null && securityEvent.getEventText().trim().length() > 0)
						securityEventGroup.setString(868, securityEvent.getEventText());
					group.addGroup(securityEventGroup);
				}
			}
		}

		if (fSecurity.getSecurityDetails().getDatedDate() != null)
			group.setString(873, localMarketDateFormat.format(fSecurity.getSecurityDetails().getDatedDate()));
		if (fSecurity.getSecurityDetails().getInterestAccrualDate() != null)
			group.setString(874, localMarketDateFormat.format(fSecurity.getSecurityDetails().getInterestAccrualDate()));

		// check appid

		if (major > 4) {

			if (fSecurity.getSecurityDetails().getSecurityStatus() != null && fSecurity.getSecurityDetails().getSecurityStatus().trim().length() > 0)
				group.setString(965, fSecurity.getSecurityDetails().getSecurityStatus());

			if (fSecurity.getSecurityDetails().getSettlOnOpen() != null && fSecurity.getSecurityDetails().getSettlOnOpen().trim().length() > 0)
				group.setString(966, fSecurity.getSecurityDetails().getSettlOnOpen());

			if (fSecurity.getSecurityDetails().getInstrmtAssignmentMethod() != null
					&& fSecurity.getSecurityDetails().getInstrmtAssignmentMethod().trim().length() > 0)
				group.setString(1049, fSecurity.getSecurityDetails().getInstrmtAssignmentMethod());

			if (fSecurity.getSecurityDetails().getStrikeMultiplier() != null)
				group.setDouble(967, fSecurity.getSecurityDetails().getStrikeMultiplier());

			if (fSecurity.getSecurityDetails().getStrikeValue() != null)
				group.setDouble(968, fSecurity.getSecurityDetails().getStrikeValue());

			if (fSecurity.getSecurityDetails().getMinPriceIncrement() != null)
				group.setDouble(969, fSecurity.getSecurityDetails().getMinPriceIncrement());

			if (fSecurity.getSecurityDetails().getPositionLimit() != null)
				group.setInt(970, fSecurity.getSecurityDetails().getPositionLimit());

			if (fSecurity.getSecurityDetails().getNtPositionLimit() != null)
				group.setInt(971, fSecurity.getSecurityDetails().getNtPositionLimit());

			if (fSecurity.getSecurityDetails().getUnitOfMeasure() != null && fSecurity.getSecurityDetails().getUnitOfMeasure().trim().length() > 0)
				group.setString(996, fSecurity.getSecurityDetails().getUnitOfMeasure());

			if (fSecurity.getSecurityDetails().getTimeUnit() != null && fSecurity.getSecurityDetails().getTimeUnit().trim().length() > 0)
				group.setString(997, fSecurity.getSecurityDetails().getTimeUnit());

			if (major > 5 || minor > 0 || servicepack > 0) {

				if (fSecurity.getSecurityDetails().getMaturityTime() != null)
					group.setString(1079, tzTimeFormat.format(fSecurity.getSecurityDetails().getMaturityTime()));

				if (fSecurity.getSecurityDetails().getSecurityGroup() != null && fSecurity.getSecurityDetails().getSecurityGroup().trim().length() > 0)
					group.setString(1151, fSecurity.getSecurityDetails().getSecurityGroup());

				if (fSecurity.getSecurityDetails().getMinPriceIncrementAmount() != null)
					group.setDouble(1146, fSecurity.getSecurityDetails().getMinPriceIncrementAmount());

				if (fSecurity.getSecurityDetails().getUnitOfMeasureQty() != null)
					group.setDouble(1147, fSecurity.getSecurityDetails().getUnitOfMeasureQty());

				if (fSecurity.getSecurityDetails().getProductComplex() != null && fSecurity.getSecurityDetails().getProductComplex().trim().length() > 0)
					group.setString(1227, fSecurity.getSecurityDetails().getProductComplex());

				if (fSecurity.getSecurityDetails().getPriceUnitOfMeasure() != null
						&& fSecurity.getSecurityDetails().getPriceUnitOfMeasure().trim().length() > 0)
					group.setString(1191, fSecurity.getSecurityDetails().getPriceUnitOfMeasure());

				if (fSecurity.getSecurityDetails().getPriceUnitOfMeasureQty() != null)
					group.setDouble(1192, fSecurity.getSecurityDetails().getPriceUnitOfMeasureQty());

				if (fSecurity.getSecurityDetails().getSettleMethod() != null && fSecurity.getSecurityDetails().getSettleMethod().trim().length() > 0)
					group.setString(1193, fSecurity.getSecurityDetails().getSettleMethod());

				if (fSecurity.getSecurityDetails().getExerciseStyle() != null)
					group.setInt(1194, fSecurity.getSecurityDetails().getExerciseStyle());

				if (fSecurity.getSecurityDetails().getOptionPayoutAmount() != null)
					group.setDouble(1195, fSecurity.getSecurityDetails().getOptionPayoutAmount());

				if (fSecurity.getSecurityDetails().getPriceQuoteMethod() != null && fSecurity.getSecurityDetails().getPriceQuoteMethod().trim().length() > 0) {
					if (major > 5 || minor > 0 || servicepack > 1)
						group.setString(1196, fSecurity.getSecurityDetails().getPriceQuoteMethod());
					else if ((major > 5 || minor > 0 || servicepack > 0) && !fSecurity.getSecurityDetails().getPriceQuoteMethod().equals("PCTPAR"))
						group.setString(1196, fSecurity.getSecurityDetails().getPriceQuoteMethod());

				}

				if (fSecurity.getSecurityDetails().getCapPrice() != null)
					group.setDouble(1199, fSecurity.getSecurityDetails().getCapPrice());

				if (fSecurity.getSecurityDetails().getFloorPrice() != null)
					group.setDouble(1200, fSecurity.getSecurityDetails().getFloorPrice());

				if (fSecurity.getSecurityDetails().getPutOrCall() != null)
					group.setInt(201, fSecurity.getSecurityDetails().getPutOrCall());

				if (fSecurity.getSecurityDetails().getFlexibleIndicator() != null)
					group.setBoolean(1244, fSecurity.getSecurityDetails().getFlexibleIndicator());

				if (fSecurity.getSecurityDetails().getFlexProductEligibilityIndicator() != null)
					group.setBoolean(1242, fSecurity.getSecurityDetails().getFlexProductEligibilityIndicator());

				if (fSecurity.getSecurityDetails().getValuationMethod() != null && fSecurity.getSecurityDetails().getValuationMethod().trim().length() > 0)
					group.setString(1197, fSecurity.getSecurityDetails().getValuationMethod());

			}
			if (major > 5 || minor > 0 || servicepack > 1) {
				if (fSecurity.getSecurityDetails().getContractMultiplierUnit() != null)
					group.setInt(1435, fSecurity.getSecurityDetails().getContractMultiplierUnit());

				if (fSecurity.getSecurityDetails().getFlowScheduleTyped() != null)
					group.setInt(1439, fSecurity.getSecurityDetails().getFlowScheduleTyped());

				if (fSecurity.getSecurityDetails().getRestructuringType() != null && fSecurity.getSecurityDetails().getRestructuringType().trim().length() > 0)
					group.setString(1449, fSecurity.getSecurityDetails().getRestructuringType());

				if (fSecurity.getSecurityDetails().getSeniority() != null && fSecurity.getSecurityDetails().getSeniority().trim().length() > 0)
					group.setString(1450, fSecurity.getSecurityDetails().getSeniority());

				if (fSecurity.getSecurityDetails().getStrikePriceDeterminationMethod() != null)
					group.setInt(1478, fSecurity.getSecurityDetails().getStrikePriceDeterminationMethod());

				if (fSecurity.getSecurityDetails().getStrikePriceBoundaryMethod() != null)
					group.setInt(1479, fSecurity.getSecurityDetails().getStrikePriceBoundaryMethod());

				if (fSecurity.getSecurityDetails().getStrikePriceBoundaryPrecision() != null)
					group.setDouble(1480, fSecurity.getSecurityDetails().getStrikePriceBoundaryPrecision());

				if (fSecurity.getSecurityDetails().getUnderlyingDeterminationMethod() != null)
					group.setInt(1481, fSecurity.getSecurityDetails().getUnderlyingDeterminationMethod());

				if (fSecurity.getSecurityDetails().getOptionPayoutType() != null)
					group.setInt(1482, fSecurity.getSecurityDetails().getOptionPayoutType());
			}

			if (minor > 0 || servicepack > 1)
				for (SecurityComplexEvent securityComplexEvent : fSecurity.getSecurityDetails().getSecurityComplexEvents()) {

					final Group securityComplexEventGroup = new Group(1483, 1484, new int[] { 1484, 1485, 1486, 1487, 1488, 1489, 1490, 1491 });

					if (securityComplexEvent.getEventType() != null)
						securityComplexEventGroup.setInt(1484, securityComplexEvent.getEventType());

					if (securityComplexEvent.getOptionPayoutAmount() != null)
						securityComplexEventGroup.setDouble(1485, securityComplexEvent.getOptionPayoutAmount());

					if (securityComplexEvent.getEventPrice() != null)
						securityComplexEventGroup.setDouble(1486, securityComplexEvent.getEventPrice());

					if (securityComplexEvent.getEventPriceBoundaryMethod() != null)
						securityComplexEventGroup.setInt(1487, securityComplexEvent.getEventPriceBoundaryMethod());

					if (securityComplexEvent.getEventPriceBoundaryPrecision() != null)
						securityComplexEventGroup.setDouble(1488, securityComplexEvent.getEventPriceBoundaryPrecision());

					if (securityComplexEvent.getEventPriceTimeType() != null)
						securityComplexEventGroup.setInt(1489, securityComplexEvent.getEventPriceTimeType());

					if (securityComplexEvent.getEventCondition() != null)
						securityComplexEventGroup.setInt(1490, securityComplexEvent.getEventCondition());

					for (ComplexEventDate complexEventDate : securityComplexEvent.getComplexEventDates()) {
						final Group complexEventDateGroup = new Group(1491, 1492, new int[] { 1492, 1493, 1494 });

						if (complexEventDate.getEventStartDate() != null)
							complexEventDateGroup.setUtcTimeStamp(1492, complexEventDate.getEventStartDate());
						if (complexEventDate.getEventEndDate() != null)
							complexEventDateGroup.setUtcTimeStamp(1493, complexEventDate.getEventEndDate());

						for (ComplexEventTime complexEventTime : complexEventDate.getComplexEventTimes()) {
							final Group complexEventTimeGroup = new Group(1494, 1495, new int[] { 1495, 1496 });

							if (complexEventTime.getEventStartTime() != null)
								complexEventTimeGroup.setUtcTimeOnly(1495, complexEventTime.getEventStartTime());
							if (complexEventTime.getEventEndTime() != null)
								complexEventTimeGroup.setUtcTimeOnly(1496, complexEventTime.getEventEndTime());

							complexEventDateGroup.addGroup(complexEventTimeGroup);
						}

						securityComplexEventGroup.addGroup(complexEventDateGroup);
					}

					group.addGroup(securityComplexEventGroup);

				}

		}

		// endcheck

		if (fSecurity.getSecurityDetails().getDeliveryForm() != null)
			group.setInt(668, fSecurity.getSecurityDetails().getDeliveryForm());

		if (fSecurity.getSecurityDetails().getPercentAtRisk() != null)
			group.setDouble(869, fSecurity.getSecurityDetails().getPercentAtRisk());

		for (SecurityAttribute securityAttribute : fSecurity.getSecurityDetails().getSecurityAttribute()) {

			boolean valid = false;

			if (securityAttribute.getAttributeType() == 99)
				valid = true;

			if (major <= 4 && securityAttribute.getAttributeType() <= 22)
				valid = true;

			if (major > 4) {
				if ((major > 5 || minor == 0) && securityAttribute.getAttributeType() <= 22)
					valid = true;
				else if ((major > 5 || minor > 0 || servicepack > 0) && securityAttribute.getAttributeType() <= 29)
					valid = true;
			}

			if (valid) {

				final Group securityAttributeGroup = new Group(870, 871, new int[] { 871, 872 });
				if (securityAttribute.getAttributeType() != null)
					securityAttributeGroup.setInt(871, securityAttribute.getAttributeType());
				if (securityAttribute.getAttributeValue() != null && securityAttribute.getAttributeValue().trim().length() > 0)
					securityAttributeGroup.setString(872, securityAttribute.getAttributeValue());
				group.addGroup(securityAttributeGroup);

			}
		}

		for (SecurityUnderlying securityUnderlying : fSecurity.getSecurityDetails().getSecurityUnderlyings()) {
			final Group securityUnderlyingGroup = new Group(711, 311, new int[] { 311, 309, 305, 883, 884, 886, 972, 975, 973, 974 });

			FSecurity underlying = securityUnderlying.getUnderlyingSecurity();

			securityUnderlyingGroup.setString(311, underlying.getName());
			if (underlying.getSecurityID() != null && underlying.getSecurityID().trim().length() > 0)
				securityUnderlyingGroup.setString(309, underlying.getSecurityID());
			if (underlying.getSecurityDetails().getSecurityIDSource() != null && underlying.getSecurityDetails().getSecurityIDSource().trim().length() > 0)
				securityUnderlyingGroup.setString(305, underlying.getSecurityDetails().getSecurityIDSource());

			if (securityUnderlying.getEndPrice() != null)
				securityUnderlyingGroup.setDouble(883, securityUnderlying.getEndPrice());

			if (securityUnderlying.getStartValue() != null)
				securityUnderlyingGroup.setDouble(884, securityUnderlying.getStartValue());

			if (securityUnderlying.getEndValue() != null)
				securityUnderlyingGroup.setDouble(886, securityUnderlying.getEndValue());

			// check applverid

			if (major > 4) {
				if (securityUnderlying.getAllocationPercent() != null)
					securityUnderlyingGroup.setDouble(972, securityUnderlying.getAllocationPercent());

				if (securityUnderlying.getSettlementType() != null)
					securityUnderlyingGroup.setInt(975, securityUnderlying.getSettlementType());

				if (securityUnderlying.getCashAmount() != null)
					securityUnderlyingGroup.setDouble(973, securityUnderlying.getCashAmount());

				if (securityUnderlying.getCashType() != null && securityUnderlying.getCashType().trim().length() > 0)
					securityUnderlyingGroup.setString(974, securityUnderlying.getCashType());
			}
			// end check

			group.addGroup(securityUnderlyingGroup);
		}

		if (fSecurity.getSecurityDetails().getCurrency() != null && fSecurity.getSecurityDetails().getCurrency().trim().length() > 0)
			group.setString(15, fSecurity.getSecurityDetails().getCurrency());

		for (SecurityLeg securityLeg : fSecurity.getSecurityDetails().getSecurityLegs()) {
			final Group securityLegGroup = new Group(555, 600, new int[] { 600, 602, 603, 623, 624, 1017 });

			FSecurity underlying = securityLeg.getLegSecurity();

			securityLegGroup.setString(600, "N/A");

			if (underlying.getSecurityID() != null && underlying.getSecurityID().trim().length() > 0)
				securityLegGroup.setString(602, underlying.getSecurityID());
			if (underlying.getSecurityDetails().getSecurityIDSource() != null && underlying.getSecurityDetails().getSecurityIDSource().trim().length() > 0)
				securityLegGroup.setString(603, underlying.getSecurityDetails().getSecurityIDSource());

			if (securityLeg.getRatioQuantity() != null)
				securityLegGroup.setDouble(623, securityLeg.getRatioQuantity());

			if (securityLeg.getSide() != null && securityLeg.getSide().trim().length() > 0)
				securityLegGroup.setString(624, securityLeg.getSide());

			// check applverid

			if (major > 4 && (major > 5 || minor > 0 || servicepack > 0) && securityLeg.getOptionRatio() != null)
				securityLegGroup.setDouble(1017, securityLeg.getOptionRatio());

			// end check

			group.addGroup(securityLegGroup);
		}

		return group;
	}

	/**
	 * Gets the security for related sym.
	 *
	 * @param group the group
	 * @return the security for related sym
	 */
	public synchronized FSecurity getSecurityForRelatedSym(Group group) {

		try {
			FSecurity fSecurity = new FSecurity();
			SecurityDetails securityDetails = new SecurityDetails();
			fSecurity.setSecurityDetails(securityDetails);

			if (group.isSetField(55))
				fSecurity.setName(group.getString(55));

			if (group.isSetField(65))
				securityDetails.setSymbolSfx(group.getString(65));
			if (group.isSetField(48)) {
				fSecurity.setSecurityID(group.getString(48));
				if (fSecurity.getName().equals("N/A"))
					fSecurity.setName(group.getString(48));
			}
			if (group.isSetField(22))
				securityDetails.setSecurityIDSource(group.getString(22));

			List<SecurityAltIDGroup> securityAltIDGroups = new ArrayList<SecurityAltIDGroup>();

			for (int i = 1; i <= group.getGroupCount(454); i++) {

				final Group securityAltIDGroup = group.getGroup(i, 454);
				SecurityAltIDGroup securityAltID = new SecurityAltIDGroup();
				securityAltID.setSecurity(securityDetails);
				if (securityAltIDGroup.isSetField(455))
					securityAltID.setSecurityAltID(securityAltIDGroup.getString(455));
				if (securityAltIDGroup.isSetField(456))
					securityAltID.setSecurityAltIDSource(securityAltIDGroup.getString(456));
				securityAltIDGroups.add(securityAltID);
			}

			securityDetails.setSecurityAltIDGroups(securityAltIDGroups);

			if (group.isSetField(460))
				securityDetails.setProduct(group.getInt(460));
			if (group.isSetField(691))
				securityDetails.setFpool(group.getString(691));
			if (group.isSetField(461))
				securityDetails.setcFICode(group.getString(461));
			if (group.isSetField(167))
				securityDetails.setSecurityType(group.getString(167));
			if (group.isSetField(762))
				securityDetails.setSecuritySubType(group.getString(762));
			if (group.isSetField(541))
				fSecurity.setMaturity(localMarketDateFormat.parse(group.getString(541)));
			if (group.isSetField(224))
				securityDetails.setCouponPaymentDate(localMarketDateFormat.parse(group.getString(224)));
			if (group.isSetField(225))
				securityDetails.setIssueDate(localMarketDateFormat.parse(group.getString(225)));
			if (group.isSetField(228))
				securityDetails.setFactor(group.getDouble(228));
			if (group.isSetField(255))
				securityDetails.setCreditRating(group.getString(255));
			if (group.isSetField(543))
				securityDetails.setInstrumentRegistry(group.getString(543));
			if (group.isSetField(470))
				securityDetails.setCountryOfIssue(group.getString(470));
			if (group.isSetField(471))
				securityDetails.setStateOfIssue(group.getString(471));
			if (group.isSetField(472))
				securityDetails.setLocaleOfIssue(group.getString(472));
			if (group.isSetField(240))
				securityDetails.setRedemptionDate(localMarketDateFormat.parse(group.getString(240)));
			if (group.isSetField(202))
				securityDetails.setStrikePrice(group.getDouble(202));
			if (group.isSetField(947))
				securityDetails.setStrikeCurrency(group.getString(947));
			if (group.isSetField(231))
				securityDetails.setContractMultiplier(group.getDouble(231));
			if (group.isSetField(223))
				securityDetails.setCouponRate(group.getDouble(223));
			if (group.isSetField(207))
				securityDetails.setSecurityExchange(group.getString(207));
			if (group.isSetField(106))
				securityDetails.setIssuer(group.getString(106));
			if (group.isSetField(107))
				securityDetails.setDescription(group.getString(107));
			if (group.isSetField(667))
				securityDetails.setContractSettlMonth(localMarketDateFormat.parse(group.getString(667)));
			if (group.isSetField(875))
				securityDetails.setCpProgramm(group.getInt(875));
			if (group.isSetField(876))
				securityDetails.setCpRegType(group.getString(876));

			List<SecurityEvent> securityEvents = new ArrayList<SecurityEvent>();

			for (int i = 1; i <= group.getGroupCount(864); i++) {

				final Group securityEventGroup = group.getGroup(i, 864);

				SecurityEvent securityEvent = new SecurityEvent();
				securityEvent.setSecurity(securityDetails);

				if (securityEventGroup.isSetField(865))
					securityEvent.setEventType(securityEventGroup.getInt(865));
				if (securityEventGroup.isSetField(866))
					securityEvent.setEventDate(localMarketDateFormat.parse(securityEventGroup.getString(866)));
				if (securityEventGroup.isSetField(867))
					securityEvent.setEventPrice(securityEventGroup.getDouble(867));
				if (securityEventGroup.isSetField(868))
					securityEvent.setEventText(securityEventGroup.getString(868));
				if (securityEventGroup.isSetField(1145))
					securityEvent.setEventTime(securityEventGroup.getUtcTimeStamp(1145));

				securityEvents.add(securityEvent);
			}

			securityDetails.setSecurityEvents(securityEvents);

			if (group.isSetField(873))
				securityDetails.setDatedDate(localMarketDateFormat.parse(group.getString(873)));
			if (group.isSetField(874))
				securityDetails.setInterestAccrualDate(localMarketDateFormat.parse(group.getString(874)));

			// check appid

			if (group.isSetField(965))
				securityDetails.setSecurityStatus(group.getString(965));

			if (group.isSetField(966))
				securityDetails.setSettlOnOpen(group.getString(966));

			if (group.isSetField(1049))
				securityDetails.setInstrmtAssignmentMethod(group.getString(1049));

			if (group.isSetField(967))
				securityDetails.setStrikeMultiplier(group.getDouble(967));

			if (group.isSetField(968))
				securityDetails.setStrikeValue(group.getDouble(968));

			if (group.isSetField(969))
				securityDetails.setMinPriceIncrement(group.getDouble(969));

			if (group.isSetField(970))
				securityDetails.setPositionLimit(group.getInt(970));

			if (group.isSetField(971))
				securityDetails.setNtPositionLimit(group.getInt(971));

			if (group.isSetField(996))
				securityDetails.setUnitOfMeasure(group.getString(996));

			if (group.isSetField(997))
				securityDetails.setTimeUnit(group.getString(997));

			if (group.isSetField(1079))
				securityDetails.setMaturityTime(tzTimeFormat.parse(group.getString(1079)));

			if (group.isSetField(1151))
				securityDetails.setSecurityGroup(group.getString(1151));

			if (group.isSetField(1146))
				securityDetails.setMinPriceIncrementAmount(group.getDouble(1146));

			if (group.isSetField(1147))
				securityDetails.setUnitOfMeasureQty(group.getDouble(1147));

			if (group.isSetField(1227))
				securityDetails.setProductComplex(group.getString(1227));

			if (group.isSetField(1191))
				securityDetails.setPriceUnitOfMeasure(group.getString(1191));

			if (group.isSetField(1192))
				securityDetails.setPriceUnitOfMeasureQty(group.getDouble(1192));

			if (group.isSetField(1193))
				securityDetails.setSettleMethod(group.getString(1193));

			if (group.isSetField(1194))
				securityDetails.setExerciseStyle(group.getInt(1194));

			if (group.isSetField(1195))
				securityDetails.setOptionPayoutAmount(group.getDouble(1195));

			if (group.isSetField(1196))
				securityDetails.setPriceQuoteMethod(group.getString(1196));

			if (group.isSetField(1198))
				securityDetails.setListMethod(group.getInt(1198));

			if (group.isSetField(1199))
				securityDetails.setCapPrice(group.getDouble(1199));

			if (group.isSetField(1200))
				securityDetails.setFloorPrice(group.getDouble(1200));

			if (group.isSetField(201))
				securityDetails.setPutOrCall(group.getInt(201));

			if (group.isSetField(1244))
				securityDetails.setFlexibleIndicator(group.getBoolean(1244));

			if (group.isSetField(1242))
				securityDetails.setFlexProductEligibilityIndicator(group.getBoolean(1242));

			if (group.isSetField(1197))
				securityDetails.setValuationMethod(group.getString(1197));

			if (group.isSetField(1435))
				securityDetails.setContractMultiplierUnit(group.getInt(1435));

			if (group.isSetField(1439))
				securityDetails.setFlowScheduleTyped(group.getInt(1439));

			if (group.isSetField(1449))
				securityDetails.setRestructuringType(group.getString(1449));

			if (group.isSetField(1450))
				securityDetails.setSeniority(group.getString(1450));

			if (group.isSetField(1478))
				securityDetails.setStrikePriceDeterminationMethod(group.getInt(1478));

			if (group.isSetField(1479))
				securityDetails.setStrikePriceBoundaryMethod(group.getInt(1479));

			if (group.isSetField(1480))
				securityDetails.setStrikePriceBoundaryPrecision(group.getDouble(1480));

			if (group.isSetField(1481))
				securityDetails.setUnderlyingDeterminationMethod(group.getInt(1481));

			if (group.isSetField(1482))
				securityDetails.setOptionPayoutType(group.getInt(1482));

			List<SecurityComplexEvent> securityComplexEvents = new ArrayList<SecurityComplexEvent>();

			for (int i = 1; i <= group.getGroupCount(1483); i++) {

				final Group securityComplexEventGroup = group.getGroup(i, 1483);

				SecurityComplexEvent securityComplexEvent = new SecurityComplexEvent();
				securityComplexEvent.setSecurity(securityDetails);

				if (securityComplexEventGroup.isSetField(1484))
					securityComplexEvent.setEventType(securityComplexEventGroup.getInt(1484));

				if (securityComplexEventGroup.isSetField(1485))
					securityComplexEvent.setOptionPayoutAmount(securityComplexEventGroup.getDouble(1485));

				if (securityComplexEventGroup.isSetField(1486))
					securityComplexEvent.setEventPrice(securityComplexEventGroup.getDouble(1486));

				if (securityComplexEventGroup.isSetField(1487))
					securityComplexEvent.setEventPriceBoundaryMethod(securityComplexEventGroup.getInt(1487));

				if (securityComplexEventGroup.isSetField(1488))
					securityComplexEvent.setEventPriceBoundaryPrecision(securityComplexEventGroup.getDouble(1488));

				if (securityComplexEventGroup.isSetField(1489))
					securityComplexEvent.setEventPriceTimeType(securityComplexEventGroup.getInt(1489));

				if (securityComplexEventGroup.isSetField(1490))
					securityComplexEvent.setEventCondition(securityComplexEventGroup.getInt(1490));

				List<ComplexEventDate> complexEventDates = new ArrayList<ComplexEventDate>();

				for (int j = 1; j <= securityComplexEventGroup.getGroupCount(1491); j++) {

					final Group complexEventDateGroup = securityComplexEventGroup.getGroup(j, 1491);

					ComplexEventDate complexEventDate = new ComplexEventDate();
					complexEventDate.setSecurityComplexEvent(securityComplexEvent);

					if (complexEventDateGroup.isSetField(1492))
						complexEventDate.setEventStartDate(complexEventDateGroup.getUtcTimeStamp(1492));
					if (complexEventDateGroup.isSetField(1493))
						complexEventDate.setEventEndDate(complexEventDateGroup.getUtcTimeStamp(1493));

					List<ComplexEventTime> complexEventTimes = new ArrayList<ComplexEventTime>();

					for (int k = 1; k <= complexEventDateGroup.getGroupCount(1494); k++) {

						final Group complexEventTimeGroup = complexEventDateGroup.getGroup(k, 1494);

						ComplexEventTime complexEventTime = new ComplexEventTime();
						complexEventTime.setComplexEventDate(complexEventDate);

						if (complexEventTimeGroup.isSetField(1495))
							complexEventTime.setEventStartTime(complexEventTimeGroup.getUtcTimeOnly(1495));
						if (complexEventTimeGroup.isSetField(1496))
							complexEventTime.setEventEndTime(complexEventTimeGroup.getUtcTimeOnly(1496));

						complexEventTimes.add(complexEventTime);
					}

					complexEventDate.setComplexEventTimes(complexEventTimes);
					complexEventDates.add(complexEventDate);
				}

				securityComplexEvent.setComplexEventDates(complexEventDates);
				securityComplexEvents.add(securityComplexEvent);
			}

			securityDetails.setSecurityComplexEvents(securityComplexEvents);

			// endcheck

			if (group.isSetField(668))
				securityDetails.setDeliveryForm(group.getInt(668));

			if (group.isSetField(869))
				securityDetails.setPercentAtRisk(group.getDouble(869));

			List<SecurityAttribute> securityAttributes = new ArrayList<SecurityAttribute>();

			for (int i = 1; i <= group.getGroupCount(870); i++) {

				final Group securityAttributeGroup = group.getGroup(i, 870);

				SecurityAttribute securityAttribute = new SecurityAttribute();
				securityAttribute.setSecurity(securityDetails);

				if (securityAttributeGroup.isSetField(871))
					securityAttribute.setAttributeType(securityAttributeGroup.getInt(871));
				if (securityAttributeGroup.isSetField(872))
					securityAttribute.setAttributeValue(securityAttributeGroup.getString(872));
				securityAttributes.add(securityAttribute);
			}

			securityDetails.setSecurityAttribute(securityAttributes);

			List<SecurityUnderlying> securityUnderlyings = new ArrayList<SecurityUnderlying>();

			for (int i = 1; i <= group.getGroupCount(711); i++) {

				final Group securityUnderlyingGroup = group.getGroup(i, 711);

				String securityID = null;
				String securityIDSource = null;

				if (securityUnderlyingGroup.isSetField(309))
					securityID = securityUnderlyingGroup.getString(309);
				if (securityUnderlyingGroup.isSetField(305))
					securityIDSource = securityUnderlyingGroup.getString(305);

				FSecurity underlying = getSecurityForDefaultSecurityID(securityID, securityIDSource);

				if (underlying != null) {

					SecurityUnderlying securityUnderlying = new SecurityUnderlying();
					securityUnderlying.setUnderlyingSecurity(underlying);
					securityUnderlying.setSecurity(securityDetails);

					if (securityUnderlyingGroup.isSetField(883))
						securityUnderlying.setEndPrice(securityUnderlyingGroup.getDouble(883));

					if (securityUnderlyingGroup.isSetField(884))
						securityUnderlying.setStartValue(securityUnderlyingGroup.getDouble(884));

					if (securityUnderlyingGroup.isSetField(886))
						securityUnderlying.setEndValue(securityUnderlyingGroup.getDouble(886));

					// check applverid

					if (securityUnderlyingGroup.isSetField(972))
						securityUnderlying.setAllocationPercent(securityUnderlyingGroup.getDouble(972));

					if (securityUnderlyingGroup.isSetField(975))
						securityUnderlying.setSettlementType(securityUnderlyingGroup.getInt(975));

					if (securityUnderlyingGroup.isSetField(973))
						securityUnderlying.setCashAmount(securityUnderlyingGroup.getDouble(973));

					if (securityUnderlyingGroup.isSetField(974))
						securityUnderlying.setCashType(securityUnderlyingGroup.getString(974));

					// end check

					securityUnderlyings.add(securityUnderlying);

				}
			}

			securityDetails.setSecurityUnderlyings(securityUnderlyings);

			if (group.isSetField(15))
				securityDetails.setCurrency(group.getString(15));

			List<SecurityLeg> securityLegs = new ArrayList<SecurityLeg>();

			for (int i = 1; i <= group.getGroupCount(555); i++) {

				final Group securityLegGroup = group.getGroup(i, 555);

				String securityID = null;
				String securityIDSource = null;

				if (securityLegGroup.isSetField(602))
					securityID = securityLegGroup.getString(602);
				if (securityLegGroup.isSetField(603))
					securityIDSource = securityLegGroup.getString(603);

				FSecurity leg = getSecurityForDefaultSecurityID(securityID, securityIDSource);

				if (leg != null) {

					SecurityLeg securityLeg = new SecurityLeg();
					securityLeg.setLegSecurity(leg);
					securityLeg.setSecurity(securityDetails);

					if (securityLegGroup.isSetField(623))
						securityLeg.setRatioQuantity(securityLegGroup.getDouble(623));

					if (securityLegGroup.isSetField(624))
						securityLeg.setSide(securityLegGroup.getString(624));

					// check applverid

					if (securityLegGroup.isSetField(1017))
						securityLeg.setOptionRatio(securityLegGroup.getDouble(1017));

					// end check

					securityLegs.add(securityLeg);
				}
			}

			securityDetails.setSecurityLegs(securityLegs);

			return fSecurity;
		}
		catch (Exception e) {
			log.error("Bug", e);
			return null;
		}
	}

}
