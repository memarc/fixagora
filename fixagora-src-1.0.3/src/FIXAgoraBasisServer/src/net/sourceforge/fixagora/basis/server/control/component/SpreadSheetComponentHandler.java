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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDInput;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.jboss.netty.channel.Channel;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class SpreadSheetComponentHandler.
 */
public class SpreadSheetComponentHandler extends AbstractComponentHandler {

	private SpreadSheet spreadSheet = null;

	private Map<Long, Map<String, Group>> refIDMap = new HashMap<Long, Map<String, Group>>();

	private Map<Long, Set<MDInputEntry>> deleteEntries = new HashMap<Long, Set<MDInputEntry>>();

	private Set<Long> onlineInputs = new HashSet<Long>();

	private Map<String, Map<Character, String>> refIdMap = new HashMap<String, Map<Character, String>>();

	private DecimalFormat idFormat = new DecimalFormat("######");
	
	private static Logger log = Logger.getLogger(SpreadSheetComponentHandler.class);

	/**
	 * Instantiates a new spread sheet component handler.
	 */
	public SpreadSheetComponentHandler() {

		super();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#setAbstractBusinessComponent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent)
	 */
	@Override
	public void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent) {

		if (abstractBusinessComponent instanceof SpreadSheet) {
			spreadSheet = (SpreadSheet) abstractBusinessComponent;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getStartLevel()
	 */
	@Override
	public int getStartLevel() {

		return 2;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#start(org.jboss.netty.channel.Channel)
	 */
	@Override
	public void start(Channel channel) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#stop(org.jboss.netty.channel.Channel)
	 */
	@Override
	public synchronized void stop(Channel channel) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#getAbstractBusinessComponent()
	 */
	@Override
	public AbstractBusinessComponent getAbstractBusinessComponent() {

		return spreadSheet;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#processFIXMessage(java.util.List)
	 */
	@Override
	protected synchronized void processFIXMessage(List<MessageEntry> messages) {
		
		List<MDInputEntry> mdInputEntries = new ArrayList<MDInputEntry>();
		for (MessageEntry messageEntry : messages) {
			try {
				
				Message message = messageEntry.getMessage();
								
				String messageType = message.getHeader().getString(MsgType.FIELD);
				
				if (messageType.equals("A") || onlineInputs.contains(messageEntry.getInputComponent().getId())) {
					if (messageType.equals("A")) {
						// http://www.quickfixj.org/jira/browse/QFJ-610
						// prevents process of old message
						for(AbstractBusinessComponent abstractBusinessComponent: spreadSheet.getInputComponents())
						{
							if(messageEntry.getInputComponent().getId()==abstractBusinessComponent.getId())
								onlineInputs.add(messageEntry.getInputComponent().getId());
						}
					}
					else if (messageType.equals("W")) {
						if (message.isSetField(48)) {
							String securityID = message.getString(48);
							for (int i = 1; i <= message.getGroupCount(268); i++) {
								Group mdEntries = message.getGroup(i, 268);
								for (SpreadSheetMDInput spreadSheetMDInput : spreadSheet.getSpreadSheetMDInputs()) {
									if (mdEntries.isSetField(269)) {
										String mdEntryType = mdEntries.getString(269);
										if (spreadSheetMDInput.getMdEntryType().equals(mdEntryType)) {
											MDInputEntry mdInputEntry = new MDInputEntry();
											mdInputEntry.setSecurityColumn(spreadSheetMDInput.getSecurityColumn());
											mdInputEntry.setSecurityValue(securityID);
											if (mdEntries.isSetField(270) && spreadSheetMDInput.getMdEntryPxColumn() != null) {
												mdInputEntry.setMdEntryPxColumn(spreadSheetMDInput.getMdEntryPxColumn());
												mdInputEntry.setMdEntryPxValue(mdEntries.getDouble(270));
											}
											if (mdEntries.isSetField(271) && spreadSheetMDInput.getMdEntrySizeColumn() != null) {
												mdInputEntry.setMdEntrySizeColumn(spreadSheetMDInput.getMdEntrySizeColumn());
												mdInputEntry.setMdEntrySizeValue(mdEntries.getDouble(271));
											}
											if (mdEntries.isSetField(272) && spreadSheetMDInput.getMdEntryDateColumn() != null) {
												try {
													mdInputEntry.setMdEntryDateValue(DateUtil.getExcelDate(mdEntries.getUtcDateOnly(272)));
													mdInputEntry.setMdEntryDateColumn(spreadSheetMDInput.getMdEntryDateColumn());
												}
												catch (Exception e) {
													log.error("Bug", e);
												}

											}
											if (mdEntries.isSetField(273) && spreadSheetMDInput.getMdEntryTimeColumn() != null) {
												try {
													Calendar calendar = Calendar.getInstance();
													calendar.setTime(mdEntries.getUtcTimeOnly(273));
													calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

													// 1970 in germany was no
													// daylight saving time!!!

													Calendar calendar2 = Calendar.getInstance();
													calendar2.setTimeZone(TimeZone.getTimeZone("UTC"));
													calendar2.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
													calendar2.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
													calendar2.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
													calendar2.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));

													mdInputEntry.setMdEntryTimeValue(DateUtil.getExcelDate(calendar2.getTime()));
													mdInputEntry.setMdEntryTimeColumn(spreadSheetMDInput.getMdEntryTimeColumn());
												}
												catch (Exception e) {
													log.error("Bug", e);
												}

											}
											if (mdEntries.isSetField(811) && spreadSheetMDInput.getMdPriceDeltaColumn() != null) {
												mdInputEntry.setMdPriceDeltaColumn(spreadSheetMDInput.getMdPriceDeltaColumn());
												mdInputEntry.setMdPriceDeltaValue(mdEntries.getDouble(811));
											}
											if (mdEntries.isSetField(332) && spreadSheetMDInput.getHighPxColumn() != null) {
												mdInputEntry.setHighPxColumn(spreadSheetMDInput.getHighPxColumn());
												mdInputEntry.setHighPxValue(mdEntries.getDouble(332));
											}
											if (mdEntries.isSetField(333) && spreadSheetMDInput.getLowPxColumn() != null) {
												mdInputEntry.setLowPxColumn(spreadSheetMDInput.getLowPxColumn());
												mdInputEntry.setLowPxValue(mdEntries.getDouble(333));
											}
											if (mdEntries.isSetField(1020) && spreadSheetMDInput.getMdTradeVolumeColumn() != null) {
												mdInputEntry.setMdTradeVolumeColumn(spreadSheetMDInput.getMdTradeVolumeColumn());
												mdInputEntry.setMdTradeVolumeValue(mdEntries.getDouble(1020));
											}
											if (mdEntries.getGroupCount(453) > 0) {
												MDInputEntry mdInputEntry2 = mdInputEntry.clone();
												for (int j = 1; j <= message.getGroupCount(453); j++) {
													Group partyIDs = message.getGroup(j, 453);
													if (partyIDs.isSetField(448) && spreadSheetMDInput.getMdTradeVolumeColumn() != null) {
														mdInputEntry2.setCounterpartyColumn(spreadSheetMDInput.getCounterpartyColumn());
														mdInputEntry2.setCounterpartyValue(partyIDs.getString(448));
														mdInputEntries.add(mdInputEntry2);
														if (spreadSheetMDInput.getCleanUpOnLogout())
															addDeleteEntry(mdInputEntry2, messageEntry.getInputComponent());
													}
												}
											}
											else {
												mdInputEntries.add(mdInputEntry);
												if (spreadSheetMDInput.getCleanUpOnLogout())
													addDeleteEntry(mdInputEntry, messageEntry.getInputComponent());
											}
										}
									}
								}
							}
						}
					}
					else if (messageType.equals("X")) {
						Map<String, Group> refMap = refIDMap.get(messageEntry.getInitialComponent().getId());
						if (refMap == null) {
							refMap = new HashMap<String, Group>();
							refIDMap.put(messageEntry.getInitialComponent().getId(), refMap);
						}
						for (int i = 1; i <= message.getGroupCount(268); i++) {
							Group mdEntries = message.getGroup(i, 268);
							Group refMdEntry = mdEntries;
							if (mdEntries.isSetField(279)) {
								String updateAction = mdEntries.getString(279);
								if (updateAction.equals("0")) {
									if (mdEntries.isSetField(278))
										refMap.put(mdEntries.getString(278), mdEntries);
								}
								else {
									Group refMdEntry2 = null;
									if (mdEntries.isSetField(278))
										refMdEntry2 = refMap.get(mdEntries.getString(278));
									if (refMdEntry2 != null) {
										refMdEntry = refMdEntry2;
										if (updateAction.equals("2") || updateAction.equals("3") || updateAction.equals("4")) {
											refMap.remove(mdEntries.getString(278));
										}
									}
									else if (mdEntries.isSetField(280)) {
										Group refMdEntry3 = refMap.get(mdEntries.getString(280));
										if (refMdEntry3 != null) {
											refMdEntry = refMdEntry3;
											if (updateAction.equals("2") || updateAction.equals("3") || updateAction.equals("4")) {
												refMap.remove(mdEntries.getString(280));
											}
										}
									}
								}

							}
							if (refMdEntry.isSetField(48)) {
								String securityID = refMdEntry.getString(48);
								if (refMdEntry.isSetField(269)) {
									String mdEntryType = refMdEntry.getString(269);
									for (SpreadSheetMDInput spreadSheetMDInput : spreadSheet.getSpreadSheetMDInputs()) {
										if (spreadSheetMDInput.getMdEntryType().equals(mdEntryType)) {
											MDInputEntry mdInputEntry = new MDInputEntry();
											mdInputEntry.setSecurityColumn(spreadSheetMDInput.getSecurityColumn());

											mdInputEntry.setSecurityValue(securityID);
											if (mdEntries.isSetField(270) && spreadSheetMDInput.getMdEntryPxColumn() != null) {
												mdInputEntry.setMdEntryPxColumn(spreadSheetMDInput.getMdEntryPxColumn());
												mdInputEntry.setMdEntryPxValue(mdEntries.getDouble(270));
											}
											if (mdEntries.isSetField(271) && spreadSheetMDInput.getMdEntrySizeColumn() != null) {
												mdInputEntry.setMdEntrySizeColumn(spreadSheetMDInput.getMdEntrySizeColumn());
												mdInputEntry.setMdEntrySizeValue(mdEntries.getDouble(271));
											}
											if (mdEntries.isSetField(272) && spreadSheetMDInput.getMdEntryDateColumn() != null) {
												try {
													mdInputEntry.setMdEntryDateValue(DateUtil.getExcelDate(mdEntries.getUtcDateOnly(272)));
													mdInputEntry.setMdEntryDateColumn(spreadSheetMDInput.getMdEntryDateColumn());
												}
												catch (Exception e) {
													log.error("Bug", e);
												}

											}
											if (mdEntries.isSetField(273) && spreadSheetMDInput.getMdEntryTimeColumn() != null) {
												try {
													Calendar calendar = Calendar.getInstance();
													calendar.setTime(mdEntries.getUtcTimeOnly(273));
													calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

													// 1970 in germany was no
													// daylight saving time!!!

													Calendar calendar2 = Calendar.getInstance();
													calendar2.setTimeZone(TimeZone.getTimeZone("UTC"));
													calendar2.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
													calendar2.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
													calendar2.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
													calendar2.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));

													mdInputEntry.setMdEntryTimeValue(DateUtil.getExcelDate(calendar2.getTime()));
													mdInputEntry.setMdEntryTimeColumn(spreadSheetMDInput.getMdEntryTimeColumn());
												}
												catch (Exception e) {
													log.error("Bug", e);
												}

											}
											if (mdEntries.isSetField(811) && spreadSheetMDInput.getMdPriceDeltaColumn() != null) {
												mdInputEntry.setMdPriceDeltaColumn(spreadSheetMDInput.getMdPriceDeltaColumn());
												mdInputEntry.setMdPriceDeltaValue(mdEntries.getDouble(811));
											}
											if (mdEntries.isSetField(332) && spreadSheetMDInput.getHighPxColumn() != null) {
												mdInputEntry.setHighPxColumn(spreadSheetMDInput.getHighPxColumn());
												mdInputEntry.setHighPxValue(mdEntries.getDouble(332));
											}
											if (mdEntries.isSetField(333) && spreadSheetMDInput.getLowPxColumn() != null) {
												mdInputEntry.setLowPxColumn(spreadSheetMDInput.getLowPxColumn());
												mdInputEntry.setLowPxValue(mdEntries.getDouble(333));
											}
											if (mdEntries.isSetField(1020) && spreadSheetMDInput.getMdTradeVolumeColumn() != null) {
												mdInputEntry.setMdTradeVolumeColumn(spreadSheetMDInput.getMdTradeVolumeColumn());
												mdInputEntry.setMdTradeVolumeValue(mdEntries.getDouble(1020));
											}
											if (refMdEntry.getGroupCount(453) > 0) {
												MDInputEntry mdInputEntry2 = mdInputEntry.clone();
												for (int j = 1; j <= refMdEntry.getGroupCount(453); j++) {
													Group partyIDs = refMdEntry.getGroup(j, 453);
													if (partyIDs.isSetField(448)) {
														mdInputEntry2.setCounterpartyColumn(spreadSheetMDInput.getCounterpartyColumn());
														mdInputEntry2.setCounterpartyValue(partyIDs.getString(448));
														mdInputEntries.add(mdInputEntry2);
														if (spreadSheetMDInput.getCleanUpOnLogout())
															addDeleteEntry(mdInputEntry2, messageEntry.getInputComponent());
													}
												}
											}
											else {
												mdInputEntries.add(mdInputEntry);
												if (spreadSheetMDInput.getCleanUpOnLogout())
													addDeleteEntry(mdInputEntry, messageEntry.getInputComponent());
											}
										}
									}
								}
							}
						}
					}
					else if (messageType.equals("5")) {
						onlineInputs.remove(messageEntry.getInputComponent().getId());
						List<MDInputEntry> deleteInputEntries = new ArrayList<MDInputEntry>();
						Set<MDInputEntry> mdInputEntries2 = deleteEntries.remove(messageEntry.getInputComponent().getId());
						if (mdInputEntries2 != null)
							deleteInputEntries.addAll(mdInputEntries2);
						spreadSheetHandler.handleFIXMessage(spreadSheet, deleteInputEntries);
					}
					else if (messageType.equals("x")) {
						handleSecurityListRequest(message, messageEntry.getInitialComponent());
					}
				}
			}
			catch (FieldNotFound e) {
				log.error("Bug", e);
			}
		}
		spreadSheetHandler.handleFIXMessage(spreadSheet, mdInputEntries);

	}

	private void addDeleteEntry(MDInputEntry mdInputEntry2, AbstractBusinessComponent inputComponent) {

		Set<MDInputEntry> mdInputEntries = deleteEntries.get(inputComponent.getId());
		if (mdInputEntries == null) {
			mdInputEntries = new HashSet<MDInputEntry>();
			deleteEntries.put(inputComponent.getId(), mdInputEntries);
		}
		mdInputEntries.add(mdInputEntry2.getDeleteMDInputEntry());
	}

	/**
	 * Handle md output entries.
	 *
	 * @param mdOutputEntries the md output entries
	 */
	public void handleMDOutputEntries(Set<MDOutputEntry> mdOutputEntries) {

		Map<AbstractBusinessComponent, Message> messages = new HashMap<AbstractBusinessComponent, Message>();

		for (MDOutputEntry mdOutputEntry : mdOutputEntries) {
			
			Message message = messages.get(mdOutputEntry.getAbstractBusinessComponent());
			if (message == null) {
				message = new Message();
				message.getHeader().setString(MsgType.FIELD, "X");
				messages.put(mdOutputEntry.getAbstractBusinessComponent(), message);
			}

			char entryType = mdOutputEntry.getMdEntryType().charAt(0);

			final Group group = new Group(268, 279, new int[] { 279, 269, 278, 280, 55, 48, 22,1018, 270, 271, 272, 273, 811, 332, 333, 1020, 453 });
			group.setChar(269, entryType);

			Map<Character, String> entryMap = refIdMap.get(mdOutputEntry.getAbstractBusinessComponent().getId() + "-" + mdOutputEntry.getSecurityValue());
			if (entryMap == null) {
				entryMap = new HashMap<Character, String>();
				refIdMap.put(mdOutputEntry.getAbstractBusinessComponent().getId() + "-" + mdOutputEntry.getSecurityValue(), entryMap);
			}

			String refId = entryMap.get(entryType);
			String entryId = idFormat.format(businessComponentHandler.getCentralID());

			if (mdOutputEntry.isDelete()) {
				if (refId != null) {
					group.setString(279, "2");
					group.setString(280, refId);
					message.addGroup(group);
					entryMap.remove(entryType);
				}
			}
			else {
				if (refId == null) {

					group.setString(55, "N/A");
					group.setString(48, mdOutputEntry.getSecurityValue());
					group.setString(279, "0");
					entryMap.put(entryType, entryId);

				}
				else {
					group.setString(279, "1");
					group.setString(280, refId);
				}

				group.setString(278, entryId);

				if (mdOutputEntry.getHighPxValue() != null) {
					group.setDouble(332, mdOutputEntry.getHighPxValue());
				}

				if (mdOutputEntry.getLowPxValue() != null) {
					group.setDouble(333, mdOutputEntry.getLowPxValue());
				}

				if (mdOutputEntry.getMdEntryDateValue() != null) {
					group.setUtcDateOnly(272, mdOutputEntry.getMdEntryDateValue());
				}

				if (mdOutputEntry.getMdEntryTimeValue() != null) {
					group.setUtcTimeOnly(273, mdOutputEntry.getMdEntryTimeValue());
				}

				if (mdOutputEntry.getMdEntryPxValue() != null) {
					group.setDouble(270, mdOutputEntry.getMdEntryPxValue());
				}
				else if(entryType!='B')
				{
					group.setString(279, "2");
					entryMap.remove(entryType);
				}

				if (mdOutputEntry.getMdEntrySizeValue() != null) {
					group.setDouble(271, mdOutputEntry.getMdEntrySizeValue());
				}
				else if(entryType=='0'||entryType=='1'||entryType=='2'||entryType=='B'||entryType=='C')
				{
					group.setString(279, "2");
					entryMap.remove(entryType);
				}

				if (mdOutputEntry.getMdPriceDeltaValue() != null) {
					group.setDouble(811, mdOutputEntry.getMdPriceDeltaValue());
				}

				if (mdOutputEntry.getMdTradeVolumeValue() != null) {
					group.setDouble(1020, mdOutputEntry.getMdTradeVolumeValue());
				}

				message.addGroup(group);
			}

		}

		for (AbstractComponentHandler abstractComponentHandler : outputComponents) {

			Message message = messages.get(abstractComponentHandler.getAbstractBusinessComponent());

			if (message != null) {

				MessageEntry messageEntry = new MessageEntry(spreadSheet, spreadSheet, message);
				abstractComponentHandler.addFIXMessage(messageEntry);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler#onChangeOfDay()
	 */
	@Override
	protected void onChangeOfDay() {

		// TODO Auto-generated method stub
		
	}

	
	private void handleSecurityListRequest(Message message, AbstractBusinessComponent initialComponent) {

		try {
			Message securityList = new Message();
			securityList.getHeader().setString(MsgType.FIELD, "y");
			securityList.setString(320,message.getString(320));
			securityList.setString(322,message.getString(320));
			securityList.setInt(560,0);
			AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(initialComponent.getId());
			MessageEntry messageEntry = new MessageEntry(getAbstractBusinessComponent(), getAbstractBusinessComponent(), securityList);
			abstractComponentHandler.addFIXMessage(messageEntry);
		}
		catch (FieldNotFound e) {
			log.error("Bug", e);
		}
		
	}

}
