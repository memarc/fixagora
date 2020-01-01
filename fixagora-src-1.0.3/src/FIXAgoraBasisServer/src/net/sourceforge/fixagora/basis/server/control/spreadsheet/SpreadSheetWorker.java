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
package net.sourceforge.fixagora.basis.server.control.spreadsheet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fixagora.basis.server.control.component.MDInputEntry;
import net.sourceforge.fixagora.basis.server.control.component.MDOutputEntry;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.SpreadSheetLocation;
import net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.LogEntryResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest.ValueFormat;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellContent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellContent.SpreadSheetCellType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetColumnFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition.ConditionType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDOutput;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetRowFormat;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.jboss.netty.channel.Channel;

/**
 * The Class SpreadSheetWorker.
 */
public class SpreadSheetWorker {

	private HSSFSheet sheet;
	private SpreadSheet spreadSheet;
	private Set<Channel> channels = new HashSet<Channel>();
	private HSSFWorkbook workbook;
	private HSSFFormulaEvaluator evaluator;
	private Map<String, SpreadSheetCellContent> spreadSheetCellMap = new HashMap<String, SpreadSheetCellContent>();
	private Map<String, Object> oldSheetCellMap = new HashMap<String, Object>();
	private Map<String, Double> deltaCellMap = new HashMap<String, Double>();
	private Map<String, SpreadSheetCellFormat> spreadSheetCellFormatMap = new HashMap<String, SpreadSheetCellFormat>();
	private Map<String, SpreadSheetConditionalFormat> spreadSheetConditionalFormatMap = new HashMap<String, SpreadSheetConditionalFormat>();
	private Map<Integer, SpreadSheetColumnFormat> spreadSheetColumnFormatMap = new HashMap<Integer, SpreadSheetColumnFormat>();
	private Map<Integer, SpreadSheetRowFormat> spreadSheetRowFormatMap = new HashMap<Integer, SpreadSheetRowFormat>();
	private Map<Long, Set<String>> sentMDOutputs = new HashMap<Long, Set<String>>();
	private SpreadSheetHandler spreadSheetHandler;
	private List<DateFormat> dateFormats = new ArrayList<DateFormat>();
	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.###########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private boolean closed = false;
	private Timer timer;
	private Map<String, SpreadSheetCell> periodicUpdateMap = new HashMap<String, SpreadSheetCell>();

	private static Logger log = Logger.getLogger(SpreadSheetWorker.class);

	/**
	 * Instantiates a new spread sheet worker.
	 *
	 * @param spreadSheet the spread sheet
	 * @param workbook the workbook
	 * @param spreadSheetHandler the spread sheet handler
	 * @param channels the channels
	 */
	public SpreadSheetWorker(SpreadSheet spreadSheet, Workbook workbook, SpreadSheetHandler spreadSheetHandler, Set<Channel> channels) {

		super();
		dateFormats.add(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a"));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ENGLISH));
		dateFormats.add(new SimpleDateFormat("HH:mm:ss"));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(new SimpleDateFormat("MM/dd/yyyy"));
		this.workbook = (HSSFWorkbook) workbook;
		this.spreadSheetHandler = spreadSheetHandler;
		evaluator = (HSSFFormulaEvaluator) workbook.getCreationHelper().createFormulaEvaluator();
		sheet = (HSSFSheet) workbook.createSheet(spreadSheet.getName());
		this.spreadSheet = spreadSheet;
		this.channels.addAll(channels);

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {

				sendPeriodicUpdates();

			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 100, 100);

	}

	private void sendPeriodicUpdates() {

		if (closed)
			timer.cancel();

		List<SpreadSheetCell> spreadSheetCells = new ArrayList<SpreadSheetCell>();

		synchronized (periodicUpdateMap) {

			if (periodicUpdateMap.size() > 0) {

				spreadSheetCells.addAll(periodicUpdateMap.values());

				periodicUpdateMap.clear();
			}
		}

		if (spreadSheetCells.size() > 0) {
			UpdateSheetCellResponse updateSheetCellResponse = new UpdateSheetCellResponse(spreadSheetCells);

			for (Channel channel2 : channels)
				spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetCellResponse, channel2);
		}

	}

	/**
	 * Inits the.
	 */
	public void init() {

		Set<HSSFCell> updatedCells = new HashSet<HSSFCell>();

		for (SpreadSheetCellContent spreadSheetCell : spreadSheetHandler.getBasisPersistenceHandler().getSpreadSheetCellContents(spreadSheet.getId())) {
			chacheOldContent(spreadSheetCell);
			CellReference cellReference = new CellReference(spreadSheetCell.getCellRow(), spreadSheetCell.getCellColumn());
			spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCell);
			HSSFRow row = sheet.getRow(cellReference.getRow());
			if (row == null)
				row = sheet.createRow(cellReference.getRow());
			HSSFCell cell = row.getCell((int) cellReference.getCol());
			if (cell == null) {
				cell = row.createCell((int) cellReference.getCol());
			}
			updatedCells.add(cell);
			switch (spreadSheetCell.getSpreadSheetCellType()) {
				case TYPE_BOOLEAN:
					if (spreadSheetCell.getBooleanValue() != null)
						cell.setCellValue(spreadSheetCell.getBooleanValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_FORMULA:
					if (spreadSheetCell.getFormulaValue() != null)
						try {
							cell.setCellFormula(spreadSheetCell.getFormulaValue());
						}
						catch (FormulaParseException e) {
							cell.setCellType(Cell.CELL_TYPE_ERROR);
						}
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_NUMERIC:
					if (spreadSheetCell.getNumericValue() != null)
						cell.setCellValue(spreadSheetCell.getNumericValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_STRING:
					if (spreadSheetCell.getStringValue() != null)
						cell.setCellValue(spreadSheetCell.getStringValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case CLEAR:
					break;
			}

		}

		for (SpreadSheetCellFormat spreadSheetCellFormat : spreadSheetHandler.getBasisPersistenceHandler().getSpreadSheetCellFormats(spreadSheet.getId())) {
			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
		}

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : spreadSheetHandler.getBasisPersistenceHandler().getSpreadSheetConditionalFormats(
				spreadSheet.getId())) {
			CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
			spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
		}

		for (SpreadSheetColumnFormat spreadSheetColumnFormat : spreadSheetHandler.getBasisPersistenceHandler().getSpreadSheetColumnFormats(spreadSheet.getId())) {
			spreadSheetColumnFormatMap.put(spreadSheetColumnFormat.getColumnNumber(), spreadSheetColumnFormat);
		}

		for (SpreadSheetRowFormat spreadSheetRowFormat : spreadSheetHandler.getBasisPersistenceHandler().getSpreadSheetRowFormats(spreadSheet.getId())) {
			spreadSheetRowFormatMap.put(spreadSheetRowFormat.getRowNumber(), spreadSheetRowFormat);
		}

		recalculate();
		getUpdatedCells();
		checkTrigger(updatedCells);
		open(null);
	}

	/**
	 * Check trigger.
	 *
	 * @param updatedCells the updated cells
	 */
	public void checkTrigger(Set<HSSFCell> updatedCells) {

		try {
			Set<MDOutputEntry> mdOutputEntries = new HashSet<MDOutputEntry>();

			for (SpreadSheetMDOutput spreadSheetMDOutput : spreadSheet.getSpreadSheetMDOutputs()) {
				for (HSSFCell hssfCell : updatedCells) {
					if ((spreadSheetMDOutput.getHighPxColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getHighPxColumn())
							|| (spreadSheetMDOutput.getLowPxColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getLowPxColumn())
							|| (spreadSheetMDOutput.getSecurityColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getSecurityColumn())
							|| (spreadSheetMDOutput.getMdEntryPxColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getMdEntryPxColumn())
							|| (spreadSheetMDOutput.getMdEntrySizeColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getMdEntrySizeColumn())
							|| (spreadSheetMDOutput.getMdPriceDeltaColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getMdPriceDeltaColumn())
							|| (spreadSheetMDOutput.getSendIfColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getSendIfColumn())
							|| (spreadSheetMDOutput.getDeleteIfNot() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput.getDeleteIfNot())
							|| (spreadSheetMDOutput.getMdTradeVolumeColumn() != null && hssfCell.getColumnIndex() == spreadSheetMDOutput
									.getMdTradeVolumeColumn())) {
						HSSFRow row = sheet.getRow(hssfCell.getRowIndex());
						if (row!=null&&spreadSheetMDOutput.getSecurityColumn() >= 0) {
							HSSFCell securityCell = row.getCell(spreadSheetMDOutput.getSecurityColumn());
							if (securityCell != null) {
								if (securityCell.getCellType() == Cell.CELL_TYPE_STRING
										|| (securityCell.getCellType() == Cell.CELL_TYPE_FORMULA && securityCell.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING)) {

									MDOutputEntry mdOutputEntry = new MDOutputEntry();

									mdOutputEntry.setAbstractBusinessComponent(spreadSheetMDOutput.getBusinessComponent());

									mdOutputEntry.setSecurityValue(securityCell.getStringCellValue());

									mdOutputEntry.setMdEntryType(spreadSheetMDOutput.getMdEntryType());

									boolean delete = false;

									if (spreadSheetMDOutput.getDeleteIfNot() != null) {

										delete = true;

										HSSFCell deleteIfNotCell = row.getCell(spreadSheetMDOutput.getDeleteIfNot());

										if (deleteIfNotCell != null) {
											if (deleteIfNotCell.getCellType() == Cell.CELL_TYPE_NUMERIC
													|| (deleteIfNotCell.getCellType() == Cell.CELL_TYPE_FORMULA && deleteIfNotCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

												delete = deleteIfNotCell.getNumericCellValue() == 0;

											}

											if (deleteIfNotCell.getCellType() == Cell.CELL_TYPE_BOOLEAN
													|| (deleteIfNotCell.getCellType() == Cell.CELL_TYPE_FORMULA && deleteIfNotCell.getCachedFormulaResultType() == Cell.CELL_TYPE_BOOLEAN)) {

												delete = !deleteIfNotCell.getBooleanCellValue();

											}

										}
									}

									boolean send = true;

									if (delete) {
										Set<String> securitySet = sentMDOutputs.get(spreadSheetMDOutput.getId());
										if (securitySet == null) {
											send = false;
										}
										else if (!securitySet.remove(mdOutputEntry.getSecurityValue()))
											send = false;
									}
									else {
										if (spreadSheetMDOutput.getSendIfColumn() != null) {

											send = false;

											HSSFCell sendIfCell = row.getCell(spreadSheetMDOutput.getSendIfColumn());

											if (sendIfCell != null) {
												if (sendIfCell.getCellType() == Cell.CELL_TYPE_NUMERIC
														|| (sendIfCell.getCellType() == Cell.CELL_TYPE_FORMULA && sendIfCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

													send = sendIfCell.getNumericCellValue() != 0;

												}

												if (sendIfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN
														|| (sendIfCell.getCellType() == Cell.CELL_TYPE_FORMULA && sendIfCell.getCachedFormulaResultType() == Cell.CELL_TYPE_BOOLEAN)) {

													send = sendIfCell.getBooleanCellValue();

												}

											}
										}

										if (send) {
											Set<String> securitySet = sentMDOutputs.get(spreadSheetMDOutput.getId());
											if (securitySet == null) {
												securitySet = new HashSet<String>();
												sentMDOutputs.put(spreadSheetMDOutput.getId(), securitySet);
											}
											securitySet.add(mdOutputEntry.getSecurityValue());

											if (spreadSheetMDOutput.getHighPxColumn() != null) {
												HSSFCell highPxCell = row.getCell(spreadSheetMDOutput.getHighPxColumn());

												if (highPxCell != null) {
													if (highPxCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (highPxCell.getCellType() == Cell.CELL_TYPE_FORMULA && highPxCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setHighPxValue(highPxCell.getNumericCellValue());

													}
												}
											}

											if (spreadSheetMDOutput.getLowPxColumn() != null) {

												HSSFCell lowPxCell = row.getCell(spreadSheetMDOutput.getLowPxColumn());

												if (lowPxCell != null) {
													if (lowPxCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (lowPxCell.getCellType() == Cell.CELL_TYPE_FORMULA && lowPxCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setLowPxValue(lowPxCell.getNumericCellValue());

													}
												}
											}
											if (spreadSheetMDOutput.getMdEntryDateColumn() != null) {
												HSSFCell mdEntryDateCell = row.getCell(spreadSheetMDOutput.getMdEntryDateColumn());

												if (mdEntryDateCell != null) {
													if (mdEntryDateCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdEntryDateCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdEntryDateCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														try {
															mdOutputEntry.setMdEntryDateValue(DateUtil.getJavaDate(mdEntryDateCell.getNumericCellValue()));
														}
														catch (Exception e) {
															log.error("Bug", e);
														}

													}
												}
											}

											if (spreadSheetMDOutput.getMdEntryPxColumn() != null) {

												HSSFCell mdEntryPxCell = row.getCell(spreadSheetMDOutput.getMdEntryPxColumn());

												if (mdEntryPxCell != null) {
													if (mdEntryPxCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdEntryPxCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdEntryPxCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setMdEntryPxValue(mdEntryPxCell.getNumericCellValue());
													}
												}
											}

											if (spreadSheetMDOutput.getMdEntrySizeColumn() != null) {
												HSSFCell mdEntrySizeCell = row.getCell(spreadSheetMDOutput.getMdEntrySizeColumn());
												if (mdEntrySizeCell != null) {
													if (mdEntrySizeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdEntrySizeCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdEntrySizeCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setMdEntrySizeValue(mdEntrySizeCell.getNumericCellValue());

													}
												}
											}

											if (spreadSheetMDOutput.getMdEntryTimeColumn() != null) {
												HSSFCell mdEntryTimeCell = row.getCell(spreadSheetMDOutput.getMdEntryTimeColumn());

												if (mdEntryTimeCell != null) {
													if (mdEntryTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdEntryTimeCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdEntryTimeCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														try {
															mdOutputEntry.setMdEntryTimeValue(DateUtil.getJavaDate(mdEntryTimeCell.getNumericCellValue()));
														}
														catch (Exception e) {
														}

													}
												}
											}

											if (spreadSheetMDOutput.getMdPriceDeltaColumn() != null) {
												HSSFCell mdPriceDeltaCell = row.getCell(spreadSheetMDOutput.getMdPriceDeltaColumn());

												if (mdPriceDeltaCell != null) {
													if (mdPriceDeltaCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdPriceDeltaCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdPriceDeltaCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setMdPriceDeltaValue(mdPriceDeltaCell.getNumericCellValue());

													}
												}
											}

											if (spreadSheetMDOutput.getMdTradeVolumeColumn() != null) {
												HSSFCell mdTradeVolumeCell = row.getCell(spreadSheetMDOutput.getMdTradeVolumeColumn());

												if (mdTradeVolumeCell != null) {
													if (mdTradeVolumeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
															|| (mdTradeVolumeCell.getCellType() == Cell.CELL_TYPE_FORMULA && mdTradeVolumeCell
																	.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {

														mdOutputEntry.setMdTradeVolumeValue(mdTradeVolumeCell.getNumericCellValue());

													}
												}
											}
										}
									}

									if (send)
										mdOutputEntries.add(mdOutputEntry);
								}
							}
						}
					}
				}
			}

			if (mdOutputEntries.size() > 0)
				spreadSheetHandler.handleMDOutputEntries(mdOutputEntries, spreadSheet);
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	/**
	 * Gets the sheet.
	 *
	 * @return the sheet
	 */
	public HSSFSheet getSheet() {

		return (HSSFSheet) sheet;
	}

	/**
	 * Gets the spread sheet.
	 *
	 * @return the spread sheet
	 */
	public SpreadSheet getSpreadSheet() {

		return spreadSheet;
	}

	/**
	 * Close.
	 *
	 * @param channel the channel
	 */
	public void close(Channel channel) {

		channels.remove(channel);

	}

	/**
	 * Open.
	 *
	 * @param channel the channel
	 */
	public void open(Channel channel) {

		if (channel != null)
			channels.add(channel);

		Set<HSSFCell> updatedCell = new HashSet<HSSFCell>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			
			HSSFRow row2 = sheet.getRow(i);
			if (row2 != null)
				for (int j = 0; j < row2.getLastCellNum(); j++) {
					HSSFCell cell2 = row2.getCell(j);
					if (cell2 != null)
						updatedCell.add(cell2);
				}
		}

		List<SpreadSheetColumnFormat> spreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();
		spreadSheetColumnFormats.addAll(spreadSheetColumnFormatMap.values());

		List<SpreadSheetRowFormat> spreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();
		spreadSheetRowFormats.addAll(spreadSheetRowFormatMap.values());

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		spreadSheetCellFormats.addAll(spreadSheetCellFormatMap.values());

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		spreadSheetConditionalFormats.addAll(spreadSheetConditionalFormatMap.values());

		UpdateFullSheetResponse updateFullSheetResponse = new UpdateFullSheetResponse(spreadSheet.getId());

		List<SpreadSheetCell> spreadSheetCells = getSpreadSheetCells(updatedCell);
		
		if (spreadSheetCells.size() > 0)
			updateFullSheetResponse.setUpdateSheetCellResponse(new UpdateSheetCellResponse(spreadSheetCells));

		if (spreadSheetColumnFormats.size() > 0)
			updateFullSheetResponse.setUpdateColumnFormatResponse(new UpdateColumnFormatResponse(spreadSheetColumnFormats));

		if (spreadSheetRowFormats.size() > 0)
			updateFullSheetResponse.setUpdateRowFormatResponse(new UpdateRowFormatResponse(spreadSheetRowFormats));

		if (spreadSheetCellFormats.size() > 0)
			updateFullSheetResponse.setUpdateSheetCellFormatResponse(new UpdateSheetCellFormatResponse(spreadSheetCellFormats));

		if (spreadSheetConditionalFormats.size() > 0)
			updateFullSheetResponse.setUpdateSheetConditionalFormatResponse(new UpdateSheetConditionalFormatResponse(spreadSheetConditionalFormats));

		if (channel != null)
			spreadSheetHandler.getBasisPersistenceHandler().send(updateFullSheetResponse, channel);
		else
			for (Channel channel2 : channels)
				spreadSheetHandler.getBasisPersistenceHandler().send(updateFullSheetResponse, channel2);

	}

	private void recalculate() {

		evaluator.clearAllCachedResultValues();

		for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {

			HSSFSheet sheet = workbook.getSheetAt(sheetNum);
			for (Row r : sheet) {

				for (Cell c : r) {
					if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
						try {
							evaluator.evaluateFormulaCell(c);
						}
						catch (Exception e) {
						}
					}
				}

			}

		}

		spreadSheetHandler.updateAffected(this);

	}

	/**
	 * Gets the updated cells.
	 *
	 * @return the updated cells
	 */
	public Set<HSSFCell> getUpdatedCells() {

		Set<HSSFCell> updatedCells = new HashSet<HSSFCell>();



		List<Row> conditionRows = new ArrayList<Row>();
		for (int i = 0; i < 20; i++)
			conditionRows.add(sheet.createRow(sheet.getLastRowNum() + 1));

		Map<SpreadSheetCondition, Cell> conditionCellMap = new HashMap<SpreadSheetCondition, Cell>();

		int conditionRowNumber = 0;

		for (Row row2 : sheet) {
			//alex keine ahnung ob das if hier richtig ist
			if (row2 != null && !conditionRows.contains(row2))
			for (Cell cell3 : row2) {
				if (cell3 != null) {
					CellReference cellReference = new CellReference(cell3.getRowIndex(), cell3.getColumnIndex());
					SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetConditionalFormatMap.get(cellReference.formatAsString());
					if (spreadSheetConditionalFormat != null) {
						for (int k = 0; k < 3; k++) {
							SpreadSheetCondition spreadSheetCondition = null;
							switch (k) {
								case 0:
									spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
									break;
								case 1:
									spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
									break;
								case 2:
									spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
									break;
							}
							if (spreadSheetCondition != null) {
								if (spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
									Row conditionRow = conditionRows.get(conditionRowNumber);
									int lastCellNum = conditionRow.getLastCellNum();
									if (lastCellNum > 253) {
										conditionRowNumber++;
										if (conditionRows.size() > conditionRowNumber) {
											conditionRow = conditionRows.get(conditionRowNumber);
											lastCellNum = conditionRow.getLastCellNum();
										}
										else
											lastCellNum = lastCellNum - 1;
									}
									Cell hssfCell = conditionRow.createCell(lastCellNum + 1);
									conditionCellMap.put(spreadSheetCondition, hssfCell);
									try {
										hssfCell.setCellFormula(spreadSheetCondition.getFormula());
									}
									catch (Exception e) {
										log.error("Bug", e);
									}
								}
							}
						}
					}
				}
			}
		}

		HSSFFormulaEvaluator evaluator2 = workbook.getCreationHelper().createFormulaEvaluator();

		for (Row row : conditionRows) {
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					try {
						evaluator2.evaluateFormulaCell(cell);
					}
					catch (Exception e1) {
					}
				}
			}
		}

		for (Row row2 : sheet) {
			if (row2 != null && !conditionRows.contains(row2))
				for (Cell cell3 : row2) {
					if (cell3 != null) {
						int condition = 0;
						CellReference cellReference = new CellReference(cell3.getRowIndex(), cell3.getColumnIndex());
						SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetConditionalFormatMap.get(cellReference.formatAsString());
						if (spreadSheetConditionalFormat != null) {
							for (int k = 0; k < 3; k++) {
								SpreadSheetCondition spreadSheetCondition = null;
								switch (k) {
									case 0:
										spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
										break;
									case 1:
										spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
										break;
									case 2:
										spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
										break;
								}
								if (spreadSheetCondition != null) {
									if (spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
										Cell hssfCell = conditionCellMap.get(spreadSheetCondition);
										try {

											switch (hssfCell.getCachedFormulaResultType()) {
												case Cell.CELL_TYPE_BOOLEAN:
													if (hssfCell.getBooleanCellValue()) {
														condition = k + 1;
														k = 3;
													}
													break;
												case Cell.CELL_TYPE_NUMERIC:
													if (hssfCell.getNumericCellValue() != 0) {
														condition = k + 1;
														k = 3;
													}
													break;
											}
										}
										catch (Exception e) {
											log.error("Bug", e);
										}

									}
									else {
										double value = Double.NaN;
										if (cell3.getCellType() == Cell.CELL_TYPE_NUMERIC)
											value = cell3.getNumericCellValue();
										else if (cell3.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
											if (cell3.getBooleanCellValue())
												value = 1d;
											else
												value = 0d;

										}
										else if (cell3.getCellType() == Cell.CELL_TYPE_FORMULA) {
											if (cell3.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)
												value = cell3.getNumericCellValue();
											else if (cell3.getCachedFormulaResultType() == Cell.CELL_TYPE_BOOLEAN) {
												if (cell3.getBooleanCellValue())
													value = 1d;
												else
													value = 0d;
											}
										}
										Double value1 = spreadSheetCondition.getValue1();
										Double value2 = spreadSheetCondition.getValue2();
										if (value1 != null && value2 != null) {
											if (value2 < value1) {
												Double tmp = value2;
												value2 = value1;
												value1 = tmp;
											}
										}
										if (value1 != null && !Double.isNaN(value)) {
											switch (spreadSheetCondition.getIsType()) {
												case BETWEEN:
													if (value2 != null && value >= value1 && value <= value2) {
														condition = k + 1;
														k = 3;
													}
													break;
												case EQUAL_TO:
													if (value == value1) {
														condition = k + 1;
														k = 3;
													}
													break;
												case GREATER_THAN:
													if (value > value1) {
														condition = k + 1;
														k = 3;
													}
													break;
												case GREATER_THAN_OR_EQUAL_TO:
													if (value >= value1) {
														condition = k + 1;
														k = 3;
													}
													break;
												case LESS_THAN_OR_EQUAL_TO:
													if (value <= value1) {
														condition = k + 1;
														k = 3;
													}
													break;
												case NOT_BETWEEN:
													if (value2 != null && !(value >= value1 && value <= value2)) {
														condition = k + 1;
														k = 3;
													}
													break;
												case NOT_EQUAL_TO:
													if (value != value1) {
														condition = k + 1;
														k = 3;
													}
													break;
												case LESS_THAN:
													if (value < value1) {
														condition = k + 1;
														k = 3;
													}
													break;
											}

										}
									}
								}
							}
							if (condition != spreadSheetConditionalFormat.getCondition()) {
								spreadSheetConditionalFormat.setCondition(condition);
								updatedCells.add((HSSFCell) cell3);
							}
						}

					}

				}
		}

		for (Row conditionRow : conditionRows)
			sheet.removeRow(conditionRow);
		
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row2 = sheet.getRow(i);
			if (row2 != null)
				for (int j = 0; j < row2.getLastCellNum(); j++) {
					HSSFCell cell2 = row2.getCell(j);
					if (cell2 != null && cell2.getCellType() == Cell.CELL_TYPE_FORMULA) {
						CellReference cellReference = new CellReference(cell2);
						SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.get(cellReference.formatAsString());
						
						if (spreadSheetCellContent == null) {
							spreadSheetCellContent = new SpreadSheetCellContent();
							spreadSheetCellContent.setSpreadSheet(spreadSheet.getId());
							spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_FORMULA);
							spreadSheetCellContent.setCellRow(cell2.getRowIndex());
							spreadSheetCellContent.setCellColumn(cell2.getColumnIndex());
							spreadSheetCellContent.setFormulaValue(cell2.getCellFormula());
							spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent);
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCellContent);
						}
						
						
						switch (cell2.getCachedFormulaResultType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								if (spreadSheetCellContent.getCalculatedValue() == null
										|| !spreadSheetCellContent.getCalculatedValue().equals(cell2.getBooleanCellValue())) {
									spreadSheetCellContent.setCalculatedValue(cell2.getBooleanCellValue());
									chacheOldContent(spreadSheetCellContent);
									updatedCells.add(cell2);
								}
								break;
							case Cell.CELL_TYPE_NUMERIC:
								if (spreadSheetCellContent.getCalculatedValue() == null
										|| !spreadSheetCellContent.getCalculatedValue().equals(cell2.getNumericCellValue())) {
									spreadSheetCellContent.setCalculatedValue(cell2.getNumericCellValue());
									chacheOldContent(spreadSheetCellContent);
									updatedCells.add(cell2);
								}
								break;
							case Cell.CELL_TYPE_STRING:
								if (spreadSheetCellContent.getCalculatedValue() == null
										|| !spreadSheetCellContent.getCalculatedValue().equals(cell2.getStringCellValue())) {
									spreadSheetCellContent.setCalculatedValue(cell2.getStringCellValue());
									chacheOldContent(spreadSheetCellContent);
									updatedCells.add(cell2);
								}
								break;
							case Cell.CELL_TYPE_BLANK:
								if (spreadSheetCellContent.getCalculatedValue() != null) {
									spreadSheetCellContent.setCalculatedValue(null);
									chacheOldContent(spreadSheetCellContent);
									updatedCells.add(cell2);
								}
								break;
							case Cell.CELL_TYPE_ERROR:
								if (spreadSheetCellContent.getCalculatedValue() != null) {
									spreadSheetCellContent.setCalculatedValue(null);
									chacheOldContent(spreadSheetCellContent);
									updatedCells.add(cell2);
								}
								break;

							case Cell.CELL_TYPE_FORMULA:
								break;
						}

					}
				}
		}

		return updatedCells;

	}

	/**
	 * On modify sheet cell request.
	 *
	 * @param modifySheetCellRequests the modify sheet cell requests
	 * @param channel the channel
	 */
	public void onModifySheetCellRequest(Collection<ModifySheetCellRequest> modifySheetCellRequests, Channel channel) {

		StringBuffer logMessage = new StringBuffer();
		FUser user = spreadSheetHandler.getUser(channel);
		if (user != null) {
			logMessage.append("User ");
			logMessage.append(user.getName());
		}
		else
			logMessage.append("Unknown user");
		logMessage.append(" modified spreadsheet ");
		logMessage.append(spreadSheet.getName());

		Set<HSSFCell> updatedCells = new HashSet<HSSFCell>();

		Set<SpreadSheetCellFormat> updatedSpreadSheetCellFormats = new HashSet<SpreadSheetCellFormat>();

		for (ModifySheetCellRequest modifySheetCellRequest : modifySheetCellRequests) {
			SpreadSheetCell spreadSheetCell = modifySheetCellRequest.getSpreadSheetCell();

			HSSFRow row = (HSSFRow) sheet.getRow(spreadSheetCell.getRow());
			if (row == null)
				row = (HSSFRow) sheet.createRow(spreadSheetCell.getRow());
			HSSFCell cell = row.getCell(spreadSheetCell.getColumn());
			if (cell != null)
				row.removeCell(cell);
			cell = row.createCell(spreadSheetCell.getColumn());
			CellReference cellReference = new CellReference(cell);

			logMessage.append(" cell ");
			logMessage.append(cellReference.formatAsString());

			SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.get(cellReference.formatAsString());
			SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetCellFormatMap.get(cellReference.formatAsString());

			if (spreadSheetCellFormat == null && modifySheetCellRequest.getValueFormat() != ValueFormat.UNKNOWN) {
				spreadSheetCellFormat = new SpreadSheetCellFormat();
				spreadSheetCellFormat.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormat.setCellRow(spreadSheetCell.getRow());
				spreadSheetCellFormat.setCellColumn(spreadSheetCell.getColumn());
				if (modifySheetCellRequest.getValueFormat() == ValueFormat.TIME)
					spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
				else
					spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
				spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
				spreadSheetCellFormat.setTimeFormat(2);
				updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
			}

			if (spreadSheetCellContent == null) {
				spreadSheetCellContent = new SpreadSheetCellContent();
				spreadSheetCellContent.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
				spreadSheetCellContent.setCellRow(cell.getRowIndex());
				spreadSheetCellContent.setCellColumn(cell.getColumnIndex());
				spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent);
			}
			if (spreadSheetCell.getFormula() != null && spreadSheetCell.getFormula().trim().length() > 0) {
				try {
					
					cell.setCellFormula(spreadSheetCell.getFormula());
					logMessage.append(" to formula =");
					logMessage.append(spreadSheetCell.getFormula());
					logMessage.append(".");
					cell.setCellType(Cell.CELL_TYPE_FORMULA);
					spreadSheetCellContent.setBooleanValue(null);
					spreadSheetCellContent.setStringValue(null);
					spreadSheetCellContent.setNumericValue(null);
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_FORMULA);
					spreadSheetCellContent.setFormulaValue(cell.getCellFormula());
					if (spreadSheetCell.getFormula().toLowerCase().trim().startsWith("now")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("time")) {
						if (spreadSheetCellFormat == null) {
							spreadSheetCellFormat = new SpreadSheetCellFormat();
							spreadSheetCellFormat.setSpreadSheet(spreadSheet.getId());
							spreadSheetCellFormat.setCellRow(spreadSheetCell.getRow());
							spreadSheetCellFormat.setCellColumn(spreadSheetCell.getColumn());
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
							spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
							spreadSheetCellFormat.setTimeFormat(2);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}
						else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
								|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE)) {

							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_TIME);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}
						else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE) {
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
							spreadSheetCellFormat.setTimeFormat(2);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}

					}
					else if (spreadSheetCell.getFormula().toLowerCase().trim().startsWith("today")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("maturity")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("interestaccrualdate")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("issuedate")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("valuta")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("date")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("couppcd")
							|| spreadSheetCell.getFormula().toLowerCase().trim().startsWith("coupncd")) {
						if (spreadSheetCellFormat == null) {
							spreadSheetCellFormat = new SpreadSheetCellFormat();
							spreadSheetCellFormat.setSpreadSheet(spreadSheet.getId());
							spreadSheetCellFormat.setCellRow(spreadSheetCell.getRow());
							spreadSheetCellFormat.setCellColumn(spreadSheetCell.getColumn());
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
							spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}
						else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
								|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE)
								|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME)) {

							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_DATE);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}
						else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME) {
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
							spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
						}
					}
					else {
						if (updateSpreadSheetCellFormat(spreadSheetCellFormat, modifySheetCellRequest.getValueFormat()))
							updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
					}
				}
				catch (Exception e) {
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_FORMULA);
					spreadSheetCellContent.setBooleanValue(null);
					spreadSheetCellContent.setNumericValue(null);
					spreadSheetCellContent.setStringValue(null);
					spreadSheetCellContent.setFormulaValue(spreadSheetCell.getFormula());
					cell.setCellType(Cell.CELL_TYPE_ERROR);
					if (updateSpreadSheetCellFormat(spreadSheetCellFormat, modifySheetCellRequest.getValueFormat()))
						updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
					if (e instanceof FormulaParseException) {
						LogEntry logEntry = new LogEntry();
						logEntry.setLogDate(new Date());
						logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
						logEntry.setMessageText(e.getMessage());
						logEntry.setMessageComponent("Basis");
						List<LogEntry> logEntries = new ArrayList<LogEntry>();
						logEntries.add(logEntry);
						spreadSheetHandler.getBasisPersistenceHandler().send(new LogEntryResponse(logEntries), channel);
					}
				}
			}
			else if (spreadSheetCell.getValue() instanceof Boolean) {
				cell.setCellValue((Boolean) spreadSheetCell.getValue());
				chacheOldContent(spreadSheetCellContent);
				spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_BOOLEAN);
				spreadSheetCellContent.setBooleanValue(cell.getBooleanCellValue());
				spreadSheetCellContent.setFormulaValue(null);
				if (updateSpreadSheetCellFormat(spreadSheetCellFormat, modifySheetCellRequest.getValueFormat()))
					updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
				logMessage.append(" to value ");
				if (spreadSheetCellFormat == null)
					logMessage.append(spreadSheetCell.getValue().toString().toUpperCase());
				else if ((Boolean) spreadSheetCell.getValue())
					logMessage.append(spreadSheetCellFormat.getTrueValue());
				else
					logMessage.append(spreadSheetCellFormat.getFalseValue());
				logMessage.append(".");
			}
			else if (spreadSheetCell.getValue() instanceof Number) {
				cell.setCellValue(((Number) spreadSheetCell.getValue()).doubleValue());
				chacheOldContent(spreadSheetCellContent);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_NUMERIC);
					spreadSheetCellContent.setNumericValue(cell.getNumericCellValue());
					spreadSheetCellContent.setFormulaValue(null);
					if (updateSpreadSheetCellFormat(spreadSheetCellFormat, modifySheetCellRequest.getValueFormat()))
						updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
					logMessage.append(" to value ");
					if (spreadSheetCellFormat != null) {
						if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DATE_CHOOSER
								|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DAY_SPINNER
								|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MONTH_SPINNER) {
							logMessage.append(dateFormats.get(5).format(DateUtil.getJavaDate(cell.getNumericCellValue())));
						}
						else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MINUTE_SPINNER
								|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.HOUR_SPINNER) {
							logMessage.append(dateFormats.get(1).format(DateUtil.getJavaDate(cell.getNumericCellValue())));
						}
						else
							logMessage.append(decimalFormat.format(cell.getNumericCellValue()));
					}
					else
						logMessage.append(decimalFormat.format(cell.getNumericCellValue()));
					logMessage.append(".");
				}
				else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
					spreadSheetCellContent.setStringValue("#VALUE!");
					spreadSheetCellContent.setFormulaValue(null);
				}
			}
			else if (spreadSheetCell.getValue() instanceof Date) {
				cell.setCellValue(DateUtil.getExcelDate((Date) spreadSheetCell.getValue()));
				chacheOldContent(spreadSheetCellContent);
				spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_NUMERIC);
				spreadSheetCellContent.setNumericValue(cell.getNumericCellValue());
				spreadSheetCellContent.setFormulaValue(null);
			}
			else if (spreadSheetCell.getValue() instanceof String) {
				logMessage.append(" to value ");
				logMessage.append(spreadSheetCell.getValue());
				logMessage.append(".");

				Double numeric = parseDate(spreadSheetCellFormat, spreadSheetCell);
				if (!numeric.isNaN()) {
					cell.setCellValue(numeric);
					chacheOldContent(spreadSheetCellContent);
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_NUMERIC);
					spreadSheetCellContent.setNumericValue(cell.getNumericCellValue());
					spreadSheetCellContent.setFormulaValue(null);
				}
				else {
					cell.setCellValue((String) spreadSheetCell.getValue());
					chacheOldContent(spreadSheetCellContent);
					spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
					spreadSheetCellContent.setStringValue((String) spreadSheetCell.getValue());
					spreadSheetCellContent.setFormulaValue(null);
					if (updateSpreadSheetCellFormat(spreadSheetCellFormat, modifySheetCellRequest.getValueFormat()))
						updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
				}
			}
			else if (spreadSheetCell.getValue() == null) {
				logMessage.append(" is now blank");
				logMessage.append(".");

				cell.setCellType(Cell.CELL_TYPE_BLANK);
				spreadSheetCellMap.remove(cellReference.formatAsString());
				spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.CLEAR);
				chacheOldContent(spreadSheetCellContent);
				spreadSheetCellContent.setFormulaValue(null);
				if (spreadSheetCellFormat != null) {
					if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME) {
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN);
						updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
						spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
					}
					else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME) {
						spreadSheetCellFormatMap.remove(cellReference.formatAsString());
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
						updatedSpreadSheetCellFormats.add(spreadSheetCellFormat);
						spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
					}
				}

			}

			spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCellContent);

			updatedCells.add(cell);

		}

		recalculate();

		updatedCells.addAll(getUpdatedCells());

		sendUpdates(updatedCells);

		sendCellFormats(updatedSpreadSheetCellFormats, null);

		checkTrigger(updatedCells);

		if (channel != null) {
			LogEntry logEntry = new LogEntry();
			logEntry.setLogDate(new Date());
			logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.INFO);
			logEntry.setMessageText(logMessage.toString());
			logEntry.setMessageComponent("Basis");
			spreadSheetHandler.writeLogEntry(logEntry);
		}

	}

	private void chacheOldContent(SpreadSheetCellContent spreadSheetCellContent) {

		CellReference cellReference = new CellReference(spreadSheetCellContent.getCellRow(), spreadSheetCellContent.getCellColumn());
		if (spreadSheetCellContent.getSpreadSheetCellType() == SpreadSheetCellType.TYPE_FORMULA) {
			if (spreadSheetCellContent.getCalculatedValue() == null)
				oldSheetCellMap.remove(cellReference.formatAsString());
			else
				oldSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent.getCalculatedValue());
		}
		else if (spreadSheetCellContent.getSpreadSheetCellType() == SpreadSheetCellType.CLEAR) {
			oldSheetCellMap.remove(cellReference.formatAsString());
		}
		else if (spreadSheetCellContent.getSpreadSheetCellType() == SpreadSheetCellType.TYPE_BOOLEAN) {
			if (spreadSheetCellContent.getBooleanValue() == null)
				oldSheetCellMap.remove(cellReference.formatAsString());
			else
				oldSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent.getBooleanValue());
		}
		else if (spreadSheetCellContent.getSpreadSheetCellType() == SpreadSheetCellType.TYPE_NUMERIC) {
			if (spreadSheetCellContent.getNumericValue() == null)
				oldSheetCellMap.remove(cellReference.formatAsString());
			else
				oldSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent.getNumericValue());
		}
		else if (spreadSheetCellContent.getSpreadSheetCellType() == SpreadSheetCellType.TYPE_STRING) {
			if (spreadSheetCellContent.getStringValue() == null)
				oldSheetCellMap.remove(cellReference.formatAsString());
			else
				oldSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent.getStringValue());
		}
	}

	/**
	 * Parses the date.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 * @param spreadSheetCell the spread sheet cell
	 * @return the double
	 */
	public double parseDate(SpreadSheetCellFormat spreadSheetCellFormat, SpreadSheetCell spreadSheetCell) {

		for (int i = 0; i < 9; i++) {
			DateFormat dateFormat = dateFormats.get(i);
			try {
				Double numeric = DateUtil.getExcelDate(dateFormat.parse(spreadSheetCell.getValue().toString()));
				if (spreadSheetCellFormat == null) {
					spreadSheetCellFormat = new SpreadSheetCellFormat();
					spreadSheetCellFormat.setSpreadSheet(spreadSheet.getId());
					spreadSheetCellFormat.setCellRow(spreadSheetCell.getRow());
					spreadSheetCellFormat.setCellColumn(spreadSheetCell.getColumn());
					if (i < 5)
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
					else
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
					CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
					spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
					spreadSheetCellFormat.setTimeFormat(1);
					List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
					spreadSheetCellFormats.add(spreadSheetCellFormat);
					sendCellFormats(spreadSheetCellFormats, null);
					spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
				}
				else {
					if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
							|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE && i < 5)
							|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME && i > 4)) {
						if (i < 5)
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_TIME);
						else
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_DATE);
						List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
						spreadSheetCellFormats.add(spreadSheetCellFormat);
						sendCellFormats(spreadSheetCellFormats, null);
						spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
					}
					else if ((spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE && i < 5)
							|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME && i > 4)) {
						if (i < 5)
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
						else
							spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
						spreadSheetCellFormat.setTimeFormat(1);
						List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
						spreadSheetCellFormats.add(spreadSheetCellFormat);
						sendCellFormats(spreadSheetCellFormats, null);
						spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
					}
				}
				return numeric;
			}
			catch (Exception e) {
			}
		}
		return Double.NaN;
	}

	private boolean updateSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat, ValueFormat valueFormat) {

		if (spreadSheetCellFormat != null) {
			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			if ((spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN && valueFormat != ValueFormat.UNKNOWN)
					|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE && valueFormat != ValueFormat.DATE)
					|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME && valueFormat != ValueFormat.TIME)) {
				switch (valueFormat) {
					case DATE:
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_DATE);
						break;
					case TIME:
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN_TIME);
						break;
					default:
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.PLAIN);
						break;
				}
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
				return true;
			}
			else if ((spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE && valueFormat != ValueFormat.DATE)
					|| (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME && valueFormat != ValueFormat.TIME)) {

				switch (valueFormat) {
					case DATE:
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_DATE);
						break;
					case TIME:
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.TMP_TIME);
						break;
					default:
						spreadSheetCellFormatMap.remove(cellReference.formatAsString());
						spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
						break;
				}
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
				return true;
			}
		}
		return false;
	}

	/**
	 * Send updates.
	 *
	 * @param updatedCell the updated cell
	 */
	public void sendUpdates(Set<HSSFCell> updatedCell) {

		List<SpreadSheetCell> spreadSheetCells = getSpreadSheetCells(updatedCell);

		synchronized (periodicUpdateMap) {
			
			for (SpreadSheetCell spreadSheetCell : spreadSheetCells)
			{				
				periodicUpdateMap.put(CellReference.convertNumToColString(spreadSheetCell.getColumn()) + Integer.toString(spreadSheetCell.getRow()),
						spreadSheetCell);
			}

		}

	}

	private List<SpreadSheetCell> getSpreadSheetCells(Set<HSSFCell> updatedCell) {

		List<SpreadSheetCell> spreadSheetCells = new ArrayList<SpreadSheetCell>();

		for (HSSFCell cell : updatedCell) {
			int condition = 0;
			CellReference cellReference = new CellReference(cell);
			SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetConditionalFormatMap.get(cellReference.formatAsString());
			if (spreadSheetConditionalFormat != null)
				condition = spreadSheetConditionalFormat.getCondition();
			Object value = null;
			SpreadSheetCell spreadSheetCell = null;
			if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				switch (cell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						value = cell.getNumericCellValue();
						break;
					case Cell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;
					case Cell.CELL_TYPE_BLANK:
						break;
					case Cell.CELL_TYPE_ERROR:
						if (cell.getErrorCellValue() == ErrorEval.NUM_ERROR.getErrorCode())
							value = "#NUM!";
						else
							value = "#VALUE!";
						break;
					case Cell.CELL_TYPE_FORMULA:
						break;

				}
				spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), value, cell.getCellFormula(), cell.getRowIndex(), cell.getColumnIndex(), condition);
			}
			else {
				String formula = null;
				switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						value = cell.getNumericCellValue();
						break;
					case Cell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;
					case Cell.CELL_TYPE_BLANK:
						break;
					case Cell.CELL_TYPE_ERROR:
						value = "#VALUE!";
						SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.get(cellReference.formatAsString());
						if (spreadSheetCellContent != null) {
							formula = spreadSheetCellContent.getFormulaValue();
						}
						break;

					case Cell.CELL_TYPE_FORMULA:
						break;

				}
				spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), value, formula, cell.getRowIndex(), cell.getColumnIndex(), condition);
			}
			if (spreadSheetCell != null)
				spreadSheetCells.add(spreadSheetCell);
		}
		return spreadSheetCells;
	}

	private void sendCellFormats(Collection<SpreadSheetCellFormat> spreadSheetCellFormats, Channel channel) {

		UpdateSheetCellFormatResponse updateSheetCellResponse = new UpdateSheetCellFormatResponse(spreadSheetCellFormats);
		if (channel != null)
			spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetCellResponse, channel);
		else
			for (Channel channel2 : channels) {
				spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetCellResponse, channel2);
			}
	}

	private void sendColumnFormats(List<SpreadSheetColumnFormat> spreadSheetColumnFormats, Channel channel) {

		UpdateColumnFormatResponse updateColumnFormatResponse = new UpdateColumnFormatResponse(spreadSheetColumnFormats);

		if (channel != null)
			spreadSheetHandler.getBasisPersistenceHandler().send(updateColumnFormatResponse, channel);

		else
			for (Channel channel2 : channels)
				spreadSheetHandler.getBasisPersistenceHandler().send(updateColumnFormatResponse, channel2);

	}

	private void sendRowFormats(List<SpreadSheetRowFormat> spreadSheetRowFormats, Channel channel) {

		UpdateRowFormatResponse updateRowFormatResponse = new UpdateRowFormatResponse(spreadSheetRowFormats);
		if (channel != null)
			spreadSheetHandler.getBasisPersistenceHandler().send(updateRowFormatResponse, channel);
		else
			for (Channel channel2 : channels) {
				spreadSheetHandler.getBasisPersistenceHandler().send(updateRowFormatResponse, channel2);
			}

	}

	/**
	 * On paste spread sheet cell request.
	 *
	 * @param pasteSpreadSheetCellRequest the paste spread sheet cell request
	 * @param pasteWorkbook the paste workbook
	 * @param spreadSheetCellFormatSet the spread sheet cell format set
	 * @param spreadSheetConditionalFormatSet the spread sheet conditional format set
	 * @param spreadSheetLocation the spread sheet location
	 * @param channel the channel
	 */
	public void onPasteSpreadSheetCellRequest(PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest, HSSFWorkbook pasteWorkbook,
			Set<SpreadSheetCellFormat> spreadSheetCellFormatSet, Set<SpreadSheetConditionalFormat> spreadSheetConditionalFormatSet,
			SpreadSheetLocation spreadSheetLocation, Channel channel) {

		FormulaShifter formulaShifter = new FormulaShifter(workbook);

		if (spreadSheetLocation == null)
			return;

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		for (SpreadSheetCellFormat spreadSheetCellFormat : spreadSheetCellFormatSet) {
			CellReference cellReference = new CellReference(pasteSpreadSheetCellRequest.getTargetRow() + spreadSheetCellFormat.getCellRow()
					- spreadSheetLocation.getRow(), pasteSpreadSheetCellRequest.getTargetColumn() + spreadSheetCellFormat.getCellColumn()
					- spreadSheetLocation.getColumn());
			SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetCellFormatMap.get(cellReference.formatAsString());

			if (spreadSheetCellFormat2 != null) {
				
				spreadSheetCellFormat2.update(spreadSheetCellFormat);
			}
			else {
				spreadSheetCellFormat2 = spreadSheetCellFormat.copy();
				spreadSheetCellFormat2.setCellColumn((int) cellReference.getCol());
				spreadSheetCellFormat2.setCellRow(cellReference.getRow());
				spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat2);
			}
			
			spreadSheetCellFormat2.setSolverCell(FormulaShifter.updateCellReference(spreadSheetCellFormat.getSolverCell(), pasteSpreadSheetCellRequest.getTargetColumn() - spreadSheetLocation.getColumn(),
					pasteSpreadSheetCellRequest.getTargetRow() - spreadSheetLocation.getRow()));
			spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat2);
			spreadSheetCellFormats.add(spreadSheetCellFormat2);
		}
		if (spreadSheetCellFormats.size() > 0)
			sendCellFormats(spreadSheetCellFormats, null);

		HSSFSheet conditionShiftSheet = pasteWorkbook.createSheet("conditonshift");
		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : spreadSheetConditionalFormatSet) {

			CellReference cellReference = new CellReference(pasteSpreadSheetCellRequest.getTargetRow() + spreadSheetConditionalFormat.getCellRow()
					- spreadSheetLocation.getRow(), pasteSpreadSheetCellRequest.getTargetColumn() + spreadSheetConditionalFormat.getCellColumn()
					- spreadSheetLocation.getColumn());

			SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = spreadSheetConditionalFormatMap.get(cellReference.formatAsString());

			if (spreadSheetConditionalFormat2 != null) {
				spreadSheetConditionalFormat2.update(spreadSheetConditionalFormat);
			}
			else {
				spreadSheetConditionalFormat2 = spreadSheetConditionalFormat.copy();
				spreadSheetConditionalFormat2.setCellColumn(pasteSpreadSheetCellRequest.getTargetColumn() + spreadSheetConditionalFormat.getCellColumn()
						- spreadSheetLocation.getColumn());
				spreadSheetConditionalFormat2.setCellRow(pasteSpreadSheetCellRequest.getTargetRow() + spreadSheetConditionalFormat.getCellRow()
						- spreadSheetLocation.getRow());
				spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat2);
			}
			for (int i = 0; i < 3; i++) {
				SpreadSheetCondition spreadSheetCondition = spreadSheetConditionalFormat2.getSpreadSheetCondition1();
				if (i == 1)
					spreadSheetCondition = spreadSheetConditionalFormat2.getSpreadSheetCondition2();
				if (i == 2)
					spreadSheetCondition = spreadSheetConditionalFormat2.getSpreadSheetCondition3();
				if (spreadSheetCondition != null && spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
					try {
						HSSFRow row = conditionShiftSheet.createRow(spreadSheetConditionalFormat2.getCellRow());
						HSSFCell cell = row.createCell(spreadSheetConditionalFormat2.getCellColumn());
						cell.setCellFormula(spreadSheetCondition.getFormula());
						formulaShifter.updateCellFormula(cell, pasteSpreadSheetCellRequest.getTargetColumn() - spreadSheetLocation.getColumn(),
								pasteSpreadSheetCellRequest.getTargetRow() - spreadSheetLocation.getRow());
						spreadSheetCondition.setFormula(cell.getCellFormula());
						conditionShiftSheet.removeRow(row);
					}
					catch (Exception e) {
						log.error("Bug", e);
					}
				}
			}
			spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat2);
			spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
		}
		pasteWorkbook.removeSheetAt(pasteWorkbook.getSheetIndex("conditonshift"));

		if (spreadSheetConditionalFormats.size() > 0)
			sendConditionalFormats(spreadSheetConditionalFormats, null);

		Set<HSSFCell> updatedCell = new HashSet<HSSFCell>();

		HSSFSheet copySheet = pasteWorkbook.getSheet("c1o2p3y4");

		if (copySheet != null) {
			for (int i = copySheet.getFirstRowNum(); i <= copySheet.getLastRowNum(); i++) {
				HSSFRow copyRow = copySheet.getRow(i);
				if (copyRow != null) {
					HSSFRow pasteRow = sheet.getRow(pasteSpreadSheetCellRequest.getTargetRow() + i - copySheet.getFirstRowNum());
					if (pasteRow == null)
						pasteRow = sheet.createRow(pasteSpreadSheetCellRequest.getTargetRow() + i - copySheet.getFirstRowNum());
					for (int j = copyRow.getFirstCellNum(); j < copyRow.getLastCellNum(); j++) { // alex
						HSSFCell copyCell = copyRow.getCell(j);
						if (copyCell != null) {
							HSSFCell pasteCell = pasteRow.getCell(pasteSpreadSheetCellRequest.getTargetColumn() + j - copyRow.getFirstCellNum());
							if (pasteCell != null)
								pasteRow.removeCell(pasteCell);
							pasteCell = pasteRow.createCell(pasteSpreadSheetCellRequest.getTargetColumn() + j - copyRow.getFirstCellNum());
							CellReference cellReference = new CellReference(pasteCell);
							SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.get(cellReference.formatAsString());
							if (spreadSheetCellContent == null) {
								spreadSheetCellContent = new SpreadSheetCellContent();
								spreadSheetCellContent.setSpreadSheet(spreadSheet.getId());
								spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
								spreadSheetCellContent.setCellRow(pasteCell.getRowIndex());
								spreadSheetCellContent.setCellColumn(pasteCell.getColumnIndex());
								spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCellContent);
							}
							switch (copyCell.getCellType()) {
								case Cell.CELL_TYPE_BOOLEAN:
									pasteCell.setCellValue(copyCell.getBooleanCellValue());
									spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_BOOLEAN);
									spreadSheetCellContent.setBooleanValue(pasteCell.getBooleanCellValue());
									break;
								case Cell.CELL_TYPE_NUMERIC:
									pasteCell.setCellValue(copyCell.getNumericCellValue());
									spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_NUMERIC);
									spreadSheetCellContent.setNumericValue(pasteCell.getNumericCellValue());
									break;
								case Cell.CELL_TYPE_STRING:
									pasteCell.setCellValue(copyCell.getStringCellValue());
									spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
									spreadSheetCellContent.setStringValue(pasteCell.getStringCellValue());
									break;
								case Cell.CELL_TYPE_FORMULA:
									pasteCell.setCellFormula(copyCell.getCellFormula());
									formulaShifter.updateCellFormula(pasteCell, pasteSpreadSheetCellRequest.getTargetColumn() - copyRow.getFirstCellNum(),
											pasteSpreadSheetCellRequest.getTargetRow() - copySheet.getFirstRowNum());
									spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.TYPE_FORMULA);
									spreadSheetCellContent.setFormulaValue(pasteCell.getCellFormula());
									spreadSheetCellContent.setBooleanValue(null);
									spreadSheetCellContent.setStringValue(null);
									spreadSheetCellContent.setNumericValue(null);
									break;
								case Cell.CELL_TYPE_BLANK:
									pasteCell.setCellType(Cell.CELL_TYPE_BLANK);
									spreadSheetCellContent.setStringValue(null);
									spreadSheetCellContent.setBooleanValue(null);
									spreadSheetCellContent.setNumericValue(null);
									break;
								case Cell.CELL_TYPE_ERROR:
									pasteCell.setCellType(Cell.CELL_TYPE_BLANK);
									spreadSheetCellContent.setStringValue(null);
									spreadSheetCellContent.setBooleanValue(null);
									spreadSheetCellContent.setNumericValue(null);
									break;
							}
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCellContent);
							updatedCell.add(pasteCell);
						}
						else {
							HSSFCell pasteCell = pasteRow.getCell(pasteSpreadSheetCellRequest.getTargetColumn() + j - copyRow.getFirstCellNum());

							if (pasteCell != null)
								pasteRow.removeCell(pasteCell);
							pasteCell = pasteRow.createCell(pasteSpreadSheetCellRequest.getTargetColumn() + j - copyRow.getFirstCellNum());
							pasteCell.setCellType(Cell.CELL_TYPE_BLANK);
							updatedCell.add(pasteCell);
							CellReference cellReference = new CellReference(pasteSpreadSheetCellRequest.getTargetRow() + i - copySheet.getFirstRowNum(),
									pasteSpreadSheetCellRequest.getTargetColumn() + j - copyRow.getFirstCellNum());
							SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.get(cellReference.formatAsString());

							if (spreadSheetCellContent != null) {
								spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.CLEAR);
								spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCellContent);
							}
						}
					}
				}
			}

			recalculate();

			updatedCell.addAll(getUpdatedCells());

			checkTrigger(updatedCell);

			sendUpdates(updatedCell);

		}
	}

	/**
	 * On modify spread sheet cell format request.
	 *
	 * @param modifySpreadSheetCellFormatRequest the modify spread sheet cell format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetCellFormatRequest(ModifySpreadSheetCellFormatRequest modifySpreadSheetCellFormatRequest, Channel channel) {

		SpreadSheetCellFormat spreadSheetCellFormat = modifySpreadSheetCellFormatRequest.getSpreadSheetCellFormat();
		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();

		for (int i = modifySpreadSheetCellFormatRequest.getRow(); i < modifySpreadSheetCellFormatRequest.getRow()
				+ modifySpreadSheetCellFormatRequest.getRowCount(); i++)
			for (int j = modifySpreadSheetCellFormatRequest.getColumn(); j < modifySpreadSheetCellFormatRequest.getColumn()
					+ modifySpreadSheetCellFormatRequest.getColumnCount(); j++) {

				CellReference cellReference = new CellReference(i, j);

				SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetCellFormatMap.get(cellReference.formatAsString());

				if (spreadSheetCellFormat2 != null)
					spreadSheetCellFormat2.update(spreadSheetCellFormat);
				else {
					spreadSheetCellFormat2 = spreadSheetCellFormat.copy();
					spreadSheetCellFormat2.setCellColumn(j);
					spreadSheetCellFormat2.setCellRow(i);
					spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
					spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat2);
				}
				spreadSheetCellFormats.add(spreadSheetCellFormat2);
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat2);
			}

		sendCellFormats(spreadSheetCellFormats, null);

	}

	/**
	 * On cut spread sheet cell request.
	 *
	 * @param cutSpreadSheetCellRequest the cut spread sheet cell request
	 * @return the hSSF workbook
	 */
	public HSSFWorkbook onCutSpreadSheetCellRequest(CutSpreadSheetCellRequest cutSpreadSheetCellRequest) {

		HSSFWorkbook copyWorkbook = new HSSFWorkbook();
		for (int i = 0; i < sheet.getWorkbook().getNumberOfSheets(); i++)
			copyWorkbook.createSheet(sheet.getWorkbook().getSheetAt(i).getSheetName());
		HSSFSheet copySheet = copyWorkbook.createSheet("c1o2p3y4");
		for (int i = cutSpreadSheetCellRequest.getSourceRow(); i < cutSpreadSheetCellRequest.getSourceRow() + cutSpreadSheetCellRequest.getSourceRowCount(); i++) {
			Row sourceRow = sheet.getRow(i);
			Row copyRow = copySheet.createRow(i);
			if (sourceRow != null) {
				for (int j = cutSpreadSheetCellRequest.getSourceColumn(); j < cutSpreadSheetCellRequest.getSourceColumn()
						+ cutSpreadSheetCellRequest.getSourceColumnCount(); j++) {
					Cell sourceCell = sourceRow.getCell(j);
					Cell copyCell = copyRow.createCell(j);
					if (sourceCell != null) {

						sourceCell.getRow().removeCell(sourceCell);
						CellReference cellReference = new CellReference(sourceCell);
						SpreadSheetCellContent spreadSheetCellContent = spreadSheetCellMap.remove(cellReference.formatAsString());
						if (spreadSheetCellContent != null) {
							spreadSheetCellContent.setSpreadSheetCellType(SpreadSheetCellType.CLEAR);
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCellContent);
						}

						SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), null, null, sourceCell.getRowIndex(),
								sourceCell.getColumnIndex());
						UpdateSheetCellResponse updateSheetCellResponse = new UpdateSheetCellResponse(spreadSheetCell);
						for (Channel channel : channels)
							spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetCellResponse, channel);

						switch (sourceCell.getCellType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								copyCell.setCellValue(sourceCell.getBooleanCellValue());
								break;
							case Cell.CELL_TYPE_NUMERIC:
								copyCell.setCellValue(sourceCell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								copyCell.setCellValue(sourceCell.getStringCellValue());
								break;
							case Cell.CELL_TYPE_FORMULA:
								copyCell.setCellFormula(sourceCell.getCellFormula());
								break;
							case Cell.CELL_TYPE_BLANK:
								break;
							case Cell.CELL_TYPE_ERROR:
								break;

						}
					}

				}

			}
		}
		return copyWorkbook;
	}

	/**
	 * Gets the spread sheet cell format.
	 *
	 * @param formatAsString the format as string
	 * @return the spread sheet cell format
	 */
	public SpreadSheetCellFormat getSpreadSheetCellFormat(String formatAsString) {

		return spreadSheetCellFormatMap.get(formatAsString);
	}

	/**
	 * On clear spread sheet cell format.
	 *
	 * @param sourceRow the source row
	 * @param sourceRowCount the source row count
	 * @param sourceColumn the source column
	 * @param sourceColumnCount the source column count
	 * @param copySpreadSheetCellFormatSet the copy spread sheet cell format set
	 * @param copySpreadSheetConditionalFormatSet the copy spread sheet conditional format set
	 */
	public void onClearSpreadSheetCellFormat(int sourceRow, int sourceRowCount, int sourceColumn, int sourceColumnCount,
			Set<SpreadSheetCellFormat> copySpreadSheetCellFormatSet, Set<SpreadSheetConditionalFormat> copySpreadSheetConditionalFormatSet) {

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();

		for (int i = sourceRow; i < sourceRow + sourceRowCount; i++) {
			for (int j = sourceColumn; j < sourceColumn + sourceColumnCount; j++) {
				CellReference cellReference = new CellReference(i, j);
				SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetCellFormatMap.remove(cellReference.formatAsString());

				if (spreadSheetCellFormat != null) {
					SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetCellFormat.copy();
					spreadSheetCellFormat2.setCellColumn(j);
					spreadSheetCellFormat2.setCellRow(i);
					spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
					copySpreadSheetCellFormatSet.add(spreadSheetCellFormat2);
					spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
					spreadSheetCellFormats.add(spreadSheetCellFormat);
					spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
				}

				SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetConditionalFormatMap.remove(cellReference.formatAsString());

				if (spreadSheetConditionalFormat != null) {
					SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = spreadSheetConditionalFormat.copy();
					spreadSheetConditionalFormat2.setCellColumn(j);
					spreadSheetConditionalFormat2.setCellRow(i);
					spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
					copySpreadSheetConditionalFormatSet.add(spreadSheetConditionalFormat2);
					spreadSheetConditionalFormat.setSpreadSheetCondition1(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition2(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition3(null);
					spreadSheetConditionalFormats.add(spreadSheetConditionalFormat);
					spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat);
				}

			}
		}
		sendCellFormats(spreadSheetCellFormats, null);
		sendConditionalFormats(spreadSheetConditionalFormats, null);
	}

	/**
	 * On modify column width request.
	 *
	 * @param modifyColumnWidthRequest the modify column width request
	 * @param channel the channel
	 */
	public void onModifyColumnWidthRequest(ModifyColumnWidthRequest modifyColumnWidthRequest, Channel channel) {

		List<SpreadSheetColumnFormat> spreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();

		for (int j = modifyColumnWidthRequest.getColumn(); j < modifyColumnWidthRequest.getColumn() + modifyColumnWidthRequest.getColumnCount(); j++) {

			SpreadSheetColumnFormat spreadSheetColumnFormat = spreadSheetColumnFormatMap.get(j);

			if (spreadSheetColumnFormat != null) {
				spreadSheetColumnFormat.setColumnWidth(modifyColumnWidthRequest.getWidth());
			}
			else {
				spreadSheetColumnFormat = new SpreadSheetColumnFormat();
				spreadSheetColumnFormat.setColumnNumber(j);
				spreadSheetColumnFormat.setColumnWidth(modifyColumnWidthRequest.getWidth());
				spreadSheetColumnFormat.setSpreadSheet(spreadSheet.getId());
				spreadSheetColumnFormat.setHidden(false);
				spreadSheetColumnFormatMap.put(j, spreadSheetColumnFormat);
			}
			spreadSheetColumnFormats.add(spreadSheetColumnFormat);
			spreadSheetHandler.updateSpreadSheetColumnFormat(spreadSheetColumnFormat);
		}

		sendColumnFormats(spreadSheetColumnFormats, null);

	}

	/**
	 * On modify column visible request.
	 *
	 * @param modifyColumnVisibleRequest the modify column visible request
	 * @param channel the channel
	 */
	public void onModifyColumnVisibleRequest(ModifyColumnVisibleRequest modifyColumnVisibleRequest, Channel channel) {

		List<SpreadSheetColumnFormat> spreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();

		for (int j = modifyColumnVisibleRequest.getColumn(); j < modifyColumnVisibleRequest.getColumn() + modifyColumnVisibleRequest.getColumnCount(); j++) {

			SpreadSheetColumnFormat spreadSheetColumnFormat = spreadSheetColumnFormatMap.get(j);

			if (spreadSheetColumnFormat != null) {
				spreadSheetColumnFormat.setHidden(!modifyColumnVisibleRequest.isVisible());
			}
			else {
				spreadSheetColumnFormat = new SpreadSheetColumnFormat();
				spreadSheetColumnFormat.setColumnNumber(j);
				spreadSheetColumnFormat.setColumnWidth(100);
				spreadSheetColumnFormat.setSpreadSheet(spreadSheet.getId());
				spreadSheetColumnFormat.setHidden(!modifyColumnVisibleRequest.isVisible());
				spreadSheetColumnFormatMap.put(j, spreadSheetColumnFormat);
			}
			spreadSheetColumnFormats.add(spreadSheetColumnFormat);
			spreadSheetHandler.updateSpreadSheetColumnFormat(spreadSheetColumnFormat);
		}

		sendColumnFormats(spreadSheetColumnFormats, null);
	}

	/**
	 * On modify row visible response.
	 *
	 * @param modifyRowVisibleRequest the modify row visible request
	 * @param channel the channel
	 */
	public void onModifyRowVisibleResponse(ModifyRowVisibleRequest modifyRowVisibleRequest, Channel channel) {

		List<SpreadSheetRowFormat> spreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();

		for (int j = modifyRowVisibleRequest.getRow(); j < modifyRowVisibleRequest.getRow() + modifyRowVisibleRequest.getRowCount(); j++) {

			SpreadSheetRowFormat spreadSheetRowFormat = spreadSheetRowFormatMap.get(j);

			if (spreadSheetRowFormat != null) {
				spreadSheetRowFormat.setHidden(!modifyRowVisibleRequest.isVisible());
			}
			else {
				spreadSheetRowFormat = new SpreadSheetRowFormat();
				spreadSheetRowFormat.setRowNumber(j);
				spreadSheetRowFormat.setSpreadSheet(spreadSheet.getId());
				spreadSheetRowFormat.setHidden(!modifyRowVisibleRequest.isVisible());
				spreadSheetRowFormatMap.put(j, spreadSheetRowFormat);
			}
			spreadSheetRowFormats.add(spreadSheetRowFormat);
			spreadSheetHandler.updateSpreadSheetRowFormat(spreadSheetRowFormat);
		}

		sendRowFormats(spreadSheetRowFormats, null);
	}

	/**
	 * Gets the old value.
	 *
	 * @param string the string
	 * @return the old value
	 */
	public Object getOldValue(String string) {

		return oldSheetCellMap.get(string);
	}
	
	/**
	 * Gets the delta value.
	 *
	 * @param string the string
	 * @return the delta value
	 */
	public Double getDeltaValue(String string) {

		return deltaCellMap.get(string);
	}
	
	/**
	 * Put delta value.
	 *
	 * @param string the string
	 * @param value the value
	 */
	public void putDeltaValue(String string, double value) {

		deltaCellMap.put(string,value);
	}

	/**
	 * On modify spread sheet conditional format request.
	 *
	 * @param modifySpreadSheetConditionalFormatRequest the modify spread sheet conditional format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetConditionalFormatRequest(ModifySpreadSheetConditionalFormatRequest modifySpreadSheetConditionalFormatRequest, Channel channel) {

		SpreadSheetConditionalFormat spreadSheetConditionalFormat = modifySpreadSheetConditionalFormatRequest.getSpreadSheetConditionalFormat();
		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();

		for (int i = modifySpreadSheetConditionalFormatRequest.getRow(); i < modifySpreadSheetConditionalFormatRequest.getRow()
				+ modifySpreadSheetConditionalFormatRequest.getRowCount(); i++)
			for (int j = modifySpreadSheetConditionalFormatRequest.getColumn(); j < modifySpreadSheetConditionalFormatRequest.getColumn()
					+ modifySpreadSheetConditionalFormatRequest.getColumnCount(); j++) {

				CellReference cellReference = new CellReference(i, j);

				SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = spreadSheetConditionalFormatMap.get(cellReference.formatAsString());

				if (spreadSheetConditionalFormat2 != null) {
					spreadSheetConditionalFormat2.update(spreadSheetConditionalFormat);
				}
				else {
					spreadSheetConditionalFormat2 = spreadSheetConditionalFormat.copy();
					spreadSheetConditionalFormat2.setCellColumn(j);
					spreadSheetConditionalFormat2.setCellRow(i);
					spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
					spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat2);
				}
				spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
				spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat2);
			}

		sendConditionalFormats(spreadSheetConditionalFormats, null);
		recalculate();
		sendUpdates(getUpdatedCells());

	}

	private void sendConditionalFormats(List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats, Channel channel) {

		UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse = new UpdateSheetConditionalFormatResponse(spreadSheetConditionalFormats);
		if (channel != null)
			spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetConditionalFormatResponse, channel);
		else
			for (Channel channel2 : channels) {
				spreadSheetHandler.getBasisPersistenceHandler().send(updateSheetConditionalFormatResponse, channel2);
			}

	}

	/**
	 * Gets the spread sheet conditional format.
	 *
	 * @param formatAsString the format as string
	 * @return the spread sheet conditional format
	 */
	public SpreadSheetConditionalFormat getSpreadSheetConditionalFormat(String formatAsString) {

		return spreadSheetConditionalFormatMap.get(formatAsString);
	}

	/**
	 * On delete row request.
	 *
	 * @param deleteRowRequest the delete row request
	 * @param channel the channel
	 */
	public void onDeleteRowRequest(DeleteRowRequest deleteRowRequest, Channel channel) {

		FormulaShifter formulaShifter = new FormulaShifter(workbook);
		
		oldSheetCellMap.clear();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
		
		List<SpreadSheetCell> spreadSheetCells = new ArrayList<SpreadSheetCell>();
		List<SpreadSheetCellContent> originalSpreadSheetCells = new ArrayList<SpreadSheetCellContent>();
		originalSpreadSheetCells.addAll(spreadSheetCellMap.values());

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		List<SpreadSheetCellFormat> originalSpreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		originalSpreadSheetCellFormats.addAll(spreadSheetCellFormatMap.values());

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		List<SpreadSheetConditionalFormat> originalSpreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		originalSpreadSheetConditionalFormats.addAll(spreadSheetConditionalFormatMap.values());

		List<SpreadSheetRowFormat> spreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();
		List<SpreadSheetRowFormat> originalSpreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();
		originalSpreadSheetRowFormats.addAll(spreadSheetRowFormatMap.values());

		spreadSheetCellMap.clear();
		spreadSheetCellFormatMap.clear();
		spreadSheetConditionalFormatMap.clear();
		spreadSheetRowFormatMap.clear();

		for (SpreadSheetCellContent spreadSheetCell : originalSpreadSheetCells) {
			if (spreadSheetCell.getCellRow() >= deleteRowRequest.getRow()) {
				SpreadSheetCell spreadSheetCell2 = new SpreadSheetCell(spreadSheet.getId(), null, null, spreadSheetCell.getCellRow(),
						spreadSheetCell.getCellColumn());
				spreadSheetCells.add(spreadSheetCell2);
				if (spreadSheetCell.getCellRow() < deleteRowRequest.getRow() + deleteRowRequest.getRowCount())
					spreadSheetCell.setSpreadSheetCellType(SpreadSheetCellType.CLEAR);
				else
					spreadSheetCell.setCellRow(spreadSheetCell.getCellRow() - deleteRowRequest.getRowCount());
				spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
			}
						
			chacheOldContent(spreadSheetCell);
			CellReference cellReference = new CellReference(spreadSheetCell.getCellRow(), spreadSheetCell.getCellColumn());
			
			if (spreadSheetCell.getSpreadSheetCellType() != SpreadSheetCellType.CLEAR)
				spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCell);
			Row row = sheet.getRow(cellReference.getRow());
			if (row == null)
				row = sheet.createRow(cellReference.getRow());
			Cell cell = row.getCell(cellReference.getCol());
			if (cell == null) {
				cell = row.createCell(cellReference.getCol());
			}
			switch (spreadSheetCell.getSpreadSheetCellType()) {
				case TYPE_BOOLEAN:
					if (spreadSheetCell.getBooleanValue() != null)
						cell.setCellValue(spreadSheetCell.getBooleanValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_FORMULA:
					if (spreadSheetCell.getFormulaValue() != null) {
						
						cell.setCellFormula(spreadSheetCell.getFormulaValue());
						if (spreadSheetCell.getCellRow() >= deleteRowRequest.getRow()) {
							formulaShifter.updateCellFormula((HSSFCell) cell, 0, -deleteRowRequest.getRowCount());
							try {
								spreadSheetCell.setFormulaValue(cell.getCellFormula());
							}
							catch (Exception e) {
								spreadSheetCell.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
								spreadSheetCell.setStringValue(spreadSheetCell.getFormulaValue());
								spreadSheetCell.setFormulaValue(null);
								LogEntry logEntry = new LogEntry();
								logEntry.setLogDate(new Date());
								logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
								logEntry.setMessageText("Formula shifting of cell " + CellReference.convertNumToColString(spreadSheetCell.getCellColumn())
										+ (spreadSheetCell.getCellRow() + 1) + " failed.");
								logEntry.setMessageComponent("Basis");
								List<LogEntry> logEntries = new ArrayList<LogEntry>();
								logEntries.add(logEntry);
								spreadSheetHandler.getBasisPersistenceHandler().send(new LogEntryResponse(logEntries), channel);
							}
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
						}
					}
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_NUMERIC:
					if (spreadSheetCell.getNumericValue() != null)
						cell.setCellValue(spreadSheetCell.getNumericValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_STRING:
					if (spreadSheetCell.getStringValue() != null)
						cell.setCellValue(spreadSheetCell.getStringValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case CLEAR:
					break;
			}

		}
		



		for (SpreadSheetCellFormat spreadSheetCellFormat : originalSpreadSheetCellFormats) {
			if (spreadSheetCellFormat.getCellRow() >= deleteRowRequest.getRow()) {
				SpreadSheetCellFormat spreadSheetCellFormat2 = new SpreadSheetCellFormat();
				spreadSheetCellFormat2.setCellRow(spreadSheetCellFormat.getCellRow());
				spreadSheetCellFormat2.setCellColumn(spreadSheetCellFormat.getCellColumn());
				spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormat2.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				spreadSheetCellFormats.add(spreadSheetCellFormat2);
				if (spreadSheetCellFormat.getCellRow() < deleteRowRequest.getRow() + deleteRowRequest.getRowCount()) {
					spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				}
				else
				{
					spreadSheetCellFormat.setCellRow(spreadSheetCellFormat.getCellRow() - deleteRowRequest.getRowCount());
					spreadSheetCellFormat.setSolverCell(FormulaShifter.updateCellReference(spreadSheetCellFormat.getSolverCell(), 0,  -deleteRowRequest.getRowCount()));

				}
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			if (spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.CLEAR)
				spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
		}

		HSSFSheet conditionShiftSheet = workbook.createSheet("conditonshift");

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : originalSpreadSheetConditionalFormats) {
			if (spreadSheetConditionalFormat.getCellRow() >= deleteRowRequest.getRow()) {
				SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = new SpreadSheetConditionalFormat();
				spreadSheetConditionalFormat2.setCellRow(spreadSheetConditionalFormat.getCellRow());
				spreadSheetConditionalFormat2.setCellColumn(spreadSheetConditionalFormat.getCellColumn());
				spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetConditionalFormat2.setSpreadSheetCondition1(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition2(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition3(null);
				spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
				if (spreadSheetConditionalFormat.getCellRow() < deleteRowRequest.getRow() + deleteRowRequest.getRowCount()) {
					spreadSheetConditionalFormat.setSpreadSheetCondition1(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition2(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition3(null);
				}
				else {
					spreadSheetConditionalFormat.setCellRow(spreadSheetConditionalFormat.getCellRow() - deleteRowRequest.getRowCount());

					for (int i = 0; i < 3; i++) {
						SpreadSheetCondition spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
						if (i == 1)
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
						if (i == 2)
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
						if (spreadSheetCondition != null && spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
							HSSFRow row = conditionShiftSheet.createRow(spreadSheetConditionalFormat.getCellRow());
							HSSFCell cell = row.createCell(spreadSheetConditionalFormat.getCellColumn());
							try {
								
								cell.setCellFormula(spreadSheetCondition.getFormula());
								formulaShifter.updateCellFormula(cell, 0, -deleteRowRequest.getRowCount());
								spreadSheetCondition.setFormula(cell.getCellFormula());
							}
							catch (Exception e) {
								LogEntry logEntry = new LogEntry();
								logEntry.setLogDate(new Date());
								logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
								logEntry.setMessageText("Formula shifting of cell "
										+ CellReference.convertNumToColString(spreadSheetConditionalFormat.getCellColumn())
										+ (spreadSheetConditionalFormat.getCellRow() + 1) + " failed.");
								logEntry.setMessageComponent("Basis");
								List<LogEntry> logEntries = new ArrayList<LogEntry>();
								logEntries.add(logEntry);
								spreadSheetHandler.getBasisPersistenceHandler().send(new LogEntryResponse(logEntries), channel);
							}
							conditionShiftSheet.removeRow(row);
						}
					}
				}

				spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat);

			}

			CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
			if (spreadSheetConditionalFormat.getSpreadSheetCondition1() != null || spreadSheetConditionalFormat.getSpreadSheetCondition2() != null
					|| spreadSheetConditionalFormat.getSpreadSheetCondition3() != null)
				spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
		}
		workbook.removeSheetAt(workbook.getSheetIndex("conditonshift"));

		for (SpreadSheetRowFormat spreadSheetRowFormat : originalSpreadSheetRowFormats) {
			if (spreadSheetRowFormat.getRowNumber() >= deleteRowRequest.getRow()) {
				SpreadSheetRowFormat spreadSheetRowFormat2 = new SpreadSheetRowFormat();
				spreadSheetRowFormat2.setRowNumber(spreadSheetRowFormat.getRowNumber());
				spreadSheetRowFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetRowFormats.add(spreadSheetRowFormat2);
				spreadSheetRowFormatMap.put(spreadSheetRowFormat2.getRowNumber(), spreadSheetRowFormat2);

				if (spreadSheetRowFormat.getRowNumber() < deleteRowRequest.getRow() + deleteRowRequest.getRowCount()) {
					spreadSheetRowFormat.setRowNumber(-1);
				}
				else
					spreadSheetRowFormat.setRowNumber(spreadSheetRowFormat.getRowNumber() - deleteRowRequest.getRowCount());
				spreadSheetHandler.updateSpreadSheetRowFormat(spreadSheetRowFormat);
			}
			if (spreadSheetRowFormat.getRowNumber() != -1)
				spreadSheetRowFormatMap.put(spreadSheetRowFormat.getRowNumber(), spreadSheetRowFormat);
		}

		recalculate();

		open(null);
	}

	/**
	 * On delete column request.
	 *
	 * @param deleteColumnRequest the delete column request
	 * @param channel the channel
	 */
	public void onDeleteColumnRequest(DeleteColumnRequest deleteColumnRequest, Channel channel) {

		FormulaShifter formulaShifter = new FormulaShifter(workbook);

		List<SpreadSheetCellContent> originalSpreadSheetCells = new ArrayList<SpreadSheetCellContent>();
		originalSpreadSheetCells.addAll(spreadSheetCellMap.values());

		List<SpreadSheetCellFormat> originalSpreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		originalSpreadSheetCellFormats.addAll(spreadSheetCellFormatMap.values());

		List<SpreadSheetConditionalFormat> originalSpreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		originalSpreadSheetConditionalFormats.addAll(spreadSheetConditionalFormatMap.values());

		List<SpreadSheetColumnFormat> originalSpreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();
		originalSpreadSheetColumnFormats.addAll(spreadSheetColumnFormatMap.values());

		spreadSheetCellMap.clear();
		spreadSheetCellFormatMap.clear();
		spreadSheetConditionalFormatMap.clear();
		spreadSheetColumnFormatMap.clear();

		oldSheetCellMap.clear();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
						
		List<SpreadSheetCell> spreadSheetCells = new ArrayList<SpreadSheetCell>();

		for (SpreadSheetCellContent spreadSheetCell : originalSpreadSheetCells) {

			if (spreadSheetCell.getCellColumn() >= deleteColumnRequest.getColumn()) {

				SpreadSheetCell spreadSheetCell2 = new SpreadSheetCell(spreadSheet.getId(), null, null, spreadSheetCell.getCellRow(),
						spreadSheetCell.getCellColumn());
				spreadSheetCells.add(spreadSheetCell2);
				if (spreadSheetCell.getCellColumn() < deleteColumnRequest.getColumn() + deleteColumnRequest.getColumnCount()) {
					spreadSheetCell.setSpreadSheetCellType(SpreadSheetCellType.CLEAR);
				}
				else
					spreadSheetCell.setCellColumn(spreadSheetCell.getCellColumn() - deleteColumnRequest.getColumnCount());
				spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
			}
			chacheOldContent(spreadSheetCell);
			CellReference cellReference = new CellReference(spreadSheetCell.getCellRow(), spreadSheetCell.getCellColumn());
			if (spreadSheetCell.getSpreadSheetCellType() != SpreadSheetCellType.CLEAR)
				spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCell);
			Row row = sheet.getRow(cellReference.getRow());
			if (row == null)
				row = sheet.createRow(cellReference.getRow());
			Cell cell = row.getCell(cellReference.getCol());
			if (cell == null) {
				cell = row.createCell(cellReference.getCol());
			}
			switch (spreadSheetCell.getSpreadSheetCellType()) {
				case TYPE_BOOLEAN:
					if (spreadSheetCell.getBooleanValue() != null)
						cell.setCellValue(spreadSheetCell.getBooleanValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_FORMULA:
					if (spreadSheetCell.getFormulaValue() != null) {
						cell.setCellFormula(spreadSheetCell.getFormulaValue());
						if (spreadSheetCell.getCellColumn() >= deleteColumnRequest.getColumn()) {
							formulaShifter.updateCellFormula((HSSFCell) cell, -deleteColumnRequest.getColumnCount(), 0);
							try {
								spreadSheetCell.setFormulaValue(cell.getCellFormula());
								spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
							}
							catch (Exception e) {
								spreadSheetCell.setSpreadSheetCellType(SpreadSheetCellType.TYPE_STRING);
								spreadSheetCell.setStringValue(spreadSheetCell.getFormulaValue());
								spreadSheetCell.setFormulaValue(null);
								LogEntry logEntry = new LogEntry();
								logEntry.setLogDate(new Date());
								logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
								logEntry.setMessageText("Formula shifting of cell " + CellReference.convertNumToColString(spreadSheetCell.getCellColumn())
										+ (spreadSheetCell.getCellRow() + 1) + " failed.");
								logEntry.setMessageComponent("Basis");
								List<LogEntry> logEntries = new ArrayList<LogEntry>();
								logEntries.add(logEntry);
								spreadSheetHandler.getBasisPersistenceHandler().send(new LogEntryResponse(logEntries), channel);
							}
						}
					}
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_NUMERIC:
					if (spreadSheetCell.getNumericValue() != null)
						cell.setCellValue(spreadSheetCell.getNumericValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_STRING:
					if (spreadSheetCell.getStringValue() != null)
						cell.setCellValue(spreadSheetCell.getStringValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case CLEAR:
					break;
			}

		}

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();

		for (SpreadSheetCellFormat spreadSheetCellFormat : originalSpreadSheetCellFormats) {
			if (spreadSheetCellFormat.getCellColumn() >= deleteColumnRequest.getColumn()) {
				SpreadSheetCellFormat spreadSheetCellFormat2 = new SpreadSheetCellFormat();
				spreadSheetCellFormat2.setCellColumn(spreadSheetCellFormat.getCellColumn());
				spreadSheetCellFormat2.setCellRow(spreadSheetCellFormat.getCellRow());
				spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormat2.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				spreadSheetCellFormats.add(spreadSheetCellFormat2);
				if (spreadSheetCellFormat.getCellColumn() < deleteColumnRequest.getColumn() + deleteColumnRequest.getColumnCount()) {
					spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				}
				else
				{
					spreadSheetCellFormat.setCellColumn(spreadSheetCellFormat.getCellColumn() - deleteColumnRequest.getColumnCount());
					spreadSheetCellFormat.setSolverCell(FormulaShifter.updateCellReference(spreadSheetCellFormat.getSolverCell(),  - deleteColumnRequest.getColumnCount(), 0));
				}
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			if (spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.CLEAR)
				spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
		}

		HSSFSheet conditionShiftSheet = workbook.createSheet("conditonshift");

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : originalSpreadSheetConditionalFormats) {
			if (spreadSheetConditionalFormat.getCellColumn() >= deleteColumnRequest.getColumn()) {
				SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = new SpreadSheetConditionalFormat();
				spreadSheetConditionalFormat2.setCellColumn(spreadSheetConditionalFormat.getCellColumn());
				spreadSheetConditionalFormat2.setCellRow(spreadSheetConditionalFormat.getCellRow());
				spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetConditionalFormat2.setSpreadSheetCondition1(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition2(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition3(null);
				spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
				if (spreadSheetConditionalFormat.getCellColumn() < deleteColumnRequest.getColumn() + deleteColumnRequest.getColumnCount()) {
					spreadSheetConditionalFormat.setSpreadSheetCondition1(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition2(null);
					spreadSheetConditionalFormat.setSpreadSheetCondition3(null);
				}
				else {
					spreadSheetConditionalFormat.setCellColumn(spreadSheetConditionalFormat.getCellColumn() - deleteColumnRequest.getColumnCount());

					for (int i = 0; i < 3; i++) {
						SpreadSheetCondition spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
						if (i == 1)
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
						if (i == 2)
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
						if (spreadSheetCondition != null && spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
							HSSFRow row = conditionShiftSheet.createRow(spreadSheetConditionalFormat.getCellRow());
							HSSFCell cell = row.createCell(spreadSheetConditionalFormat.getCellColumn());
							try {
								cell.setCellFormula(spreadSheetCondition.getFormula());
								formulaShifter.updateCellFormula(cell, -deleteColumnRequest.getColumnCount(), 0);
								spreadSheetCondition.setFormula(cell.getCellFormula());
							}
							catch (Exception e) {
								LogEntry logEntry = new LogEntry();
								logEntry.setLogDate(new Date());
								logEntry.setLogLevel(net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level.WARNING);
								logEntry.setMessageText("Formula shifting of cell "
										+ CellReference.convertNumToColString(spreadSheetConditionalFormat.getCellColumn())
										+ (spreadSheetConditionalFormat.getCellRow() + 1) + " failed.");
								logEntry.setMessageComponent("Basis");
								List<LogEntry> logEntries = new ArrayList<LogEntry>();
								logEntries.add(logEntry);
								spreadSheetHandler.getBasisPersistenceHandler().send(new LogEntryResponse(logEntries), channel);
							}
							conditionShiftSheet.removeRow(row);
						}
					}
				}

				spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
			if (spreadSheetConditionalFormat.getSpreadSheetCondition1() != null || spreadSheetConditionalFormat.getSpreadSheetCondition2() != null
					|| spreadSheetConditionalFormat.getSpreadSheetCondition3() != null)
				spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
		}
		workbook.removeSheetAt(workbook.getSheetIndex("conditonshift"));

		List<SpreadSheetColumnFormat> spreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();

		for (SpreadSheetColumnFormat spreadSheetColumnFormat : originalSpreadSheetColumnFormats) {
			if (spreadSheetColumnFormat.getColumnNumber() >= deleteColumnRequest.getColumn()) {
				SpreadSheetColumnFormat spreadSheetColumnFormat2 = new SpreadSheetColumnFormat();
				spreadSheetColumnFormat2.setColumnNumber(spreadSheetColumnFormat.getColumnNumber());
				spreadSheetColumnFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetColumnFormat2.setColumnWidth(100);
				spreadSheetColumnFormats.add(spreadSheetColumnFormat2);
				spreadSheetColumnFormatMap.put(spreadSheetColumnFormat2.getColumnNumber(), spreadSheetColumnFormat2);
				if (spreadSheetColumnFormat.getColumnNumber() < deleteColumnRequest.getColumn() + deleteColumnRequest.getColumnCount()) {
					spreadSheetColumnFormat.setColumnNumber(-1);
				}
				else
					spreadSheetColumnFormat.setColumnNumber(spreadSheetColumnFormat.getColumnNumber() - deleteColumnRequest.getColumnCount());
				spreadSheetHandler.updateSpreadSheetColumnFormat(spreadSheetColumnFormat);
			}
			if (spreadSheetColumnFormat.getColumnNumber() != -1)
				spreadSheetColumnFormatMap.put(spreadSheetColumnFormat.getColumnNumber(), spreadSheetColumnFormat);
		}

		recalculate();

		open(null);
	}

	/**
	 * On insert row request.
	 *
	 * @param insertRowRequest the insert row request
	 * @param channel the channel
	 */
	public void onInsertRowRequest(InsertRowRequest insertRowRequest, Channel channel) {

		FormulaShifter formulaShifter = new FormulaShifter(workbook);

		List<SpreadSheetCellContent> originalSpreadSheetCells = new ArrayList<SpreadSheetCellContent>();
		originalSpreadSheetCells.addAll(spreadSheetCellMap.values());

		List<SpreadSheetCellFormat> originalSpreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		originalSpreadSheetCellFormats.addAll(spreadSheetCellFormatMap.values());

		List<SpreadSheetConditionalFormat> originalSpreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		originalSpreadSheetConditionalFormats.addAll(spreadSheetConditionalFormatMap.values());

		List<SpreadSheetRowFormat> originalSpreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();
		originalSpreadSheetRowFormats.addAll(spreadSheetRowFormatMap.values());

		spreadSheetCellMap.clear();
		spreadSheetCellFormatMap.clear();
		spreadSheetConditionalFormatMap.clear();
		spreadSheetRowFormatMap.clear();

		oldSheetCellMap.clear();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
		
		Collection<SpreadSheetCellContent> spreadSheetCellContents = new ArrayList<SpreadSheetCellContent>();
		spreadSheetCellContents.addAll(spreadSheetCellMap.values());
		for (SpreadSheetCellContent spreadSheetCell : originalSpreadSheetCells) {
			if (spreadSheetCell.getCellRow() >= insertRowRequest.getRow()) {
				spreadSheetCell.setCellRow(spreadSheetCell.getCellRow() + insertRowRequest.getRowCount());
				spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
			}
			chacheOldContent(spreadSheetCell);
			CellReference cellReference = new CellReference(spreadSheetCell.getCellRow(), spreadSheetCell.getCellColumn());
			spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCell);
			Row row = sheet.getRow(cellReference.getRow());
			if (row == null)
				row = sheet.createRow(cellReference.getRow());
			Cell cell = row.getCell(cellReference.getCol());
			if (cell == null) {
				cell = row.createCell(cellReference.getCol());
			}
			switch (spreadSheetCell.getSpreadSheetCellType()) {
				case TYPE_BOOLEAN:
					if (spreadSheetCell.getBooleanValue() != null)
						cell.setCellValue(spreadSheetCell.getBooleanValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_FORMULA:
					if (spreadSheetCell.getFormulaValue() != null) {
						cell.setCellFormula(spreadSheetCell.getFormulaValue());
						if (spreadSheetCell.getCellRow() >= insertRowRequest.getRow()) {
							formulaShifter.updateCellFormula((HSSFCell) cell, 0, insertRowRequest.getRowCount());
							spreadSheetCell.setFormulaValue(cell.getCellFormula());
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
						}
					}
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_NUMERIC:
					if (spreadSheetCell.getNumericValue() != null)
						cell.setCellValue(spreadSheetCell.getNumericValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_STRING:
					if (spreadSheetCell.getStringValue() != null)
						cell.setCellValue(spreadSheetCell.getStringValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case CLEAR:
					break;
			}

		}

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();

		for (SpreadSheetCellFormat spreadSheetCellFormat : originalSpreadSheetCellFormats) {
			if (spreadSheetCellFormat.getCellRow() >= insertRowRequest.getRow()) {
				SpreadSheetCellFormat spreadSheetCellFormat2 = new SpreadSheetCellFormat();
				spreadSheetCellFormat2.setCellRow(spreadSheetCellFormat.getCellRow());
				spreadSheetCellFormat2.setCellColumn(spreadSheetCellFormat.getCellColumn());
				spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormat2.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				spreadSheetCellFormats.add(spreadSheetCellFormat2);

				spreadSheetCellFormat.setCellRow(spreadSheetCellFormat.getCellRow() + insertRowRequest.getRowCount());
				spreadSheetCellFormat.setSolverCell(FormulaShifter.updateCellReference(spreadSheetCellFormat.getSolverCell(), 0,  insertRowRequest.getRowCount()));

				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
		}

		HSSFSheet conditionShiftSheet = workbook.createSheet("conditonshift");

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : originalSpreadSheetConditionalFormats) {
			if (spreadSheetConditionalFormat.getCellRow() >= insertRowRequest.getRow()) {
				SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = new SpreadSheetConditionalFormat();
				spreadSheetConditionalFormat2.setCellRow(spreadSheetConditionalFormat.getCellRow());
				spreadSheetConditionalFormat2.setCellColumn(spreadSheetConditionalFormat.getCellColumn());
				spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetConditionalFormat2.setSpreadSheetCondition1(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition2(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition3(null);
				spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
				spreadSheetConditionalFormat.setCellRow(spreadSheetConditionalFormat.getCellRow() + insertRowRequest.getRowCount());

				for (int i = 0; i < 3; i++) {
					SpreadSheetCondition spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
					if (i == 1)
						spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
					if (i == 2)
						spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
					if (spreadSheetCondition != null && spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
						HSSFRow row = conditionShiftSheet.createRow(spreadSheetConditionalFormat.getCellRow());
						HSSFCell cell = row.createCell(spreadSheetConditionalFormat.getCellColumn());
						try {
							cell.setCellFormula(spreadSheetCondition.getFormula());
							formulaShifter.updateCellFormula(cell, 0, insertRowRequest.getRowCount());
							spreadSheetCondition.setFormula(cell.getCellFormula());
						}
						catch (Exception e) {
							log.error("Bug", e);
						}
						conditionShiftSheet.removeRow(row);
					}
				}

				spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
			spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
		}
		workbook.removeSheetAt(workbook.getSheetIndex("conditonshift"));

		List<SpreadSheetRowFormat> spreadSheetRowFormats = new ArrayList<SpreadSheetRowFormat>();
		for (SpreadSheetRowFormat spreadSheetRowFormat : originalSpreadSheetRowFormats) {
			if (spreadSheetRowFormat.getRowNumber() >= insertRowRequest.getRow()) {
				SpreadSheetRowFormat spreadSheetRowFormat2 = new SpreadSheetRowFormat();
				spreadSheetRowFormat2.setRowNumber(spreadSheetRowFormat.getRowNumber());
				spreadSheetRowFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetRowFormats.add(spreadSheetRowFormat2);
				spreadSheetRowFormatMap.put(spreadSheetRowFormat2.getRowNumber(), spreadSheetRowFormat2);

				spreadSheetRowFormat.setRowNumber(spreadSheetRowFormat.getRowNumber() + insertRowRequest.getRowCount());
				spreadSheetHandler.updateSpreadSheetRowFormat(spreadSheetRowFormat);
			}

			spreadSheetRowFormatMap.put(spreadSheetRowFormat.getRowNumber(), spreadSheetRowFormat);
		}

		recalculate();

		open(null);
	}

	/**
	 * On insert column request.
	 *
	 * @param insertColumnRequest the insert column request
	 * @param channel the channel
	 */
	public void onInsertColumnRequest(InsertColumnRequest insertColumnRequest, Channel channel) {

		FormulaShifter formulaShifter = new FormulaShifter(workbook);

		List<SpreadSheetCellContent> originalSpreadSheetCells = new ArrayList<SpreadSheetCellContent>();
		originalSpreadSheetCells.addAll(spreadSheetCellMap.values());

		List<SpreadSheetCellFormat> originalSpreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();
		originalSpreadSheetCellFormats.addAll(spreadSheetCellFormatMap.values());

		List<SpreadSheetConditionalFormat> originalSpreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();
		originalSpreadSheetConditionalFormats.addAll(spreadSheetConditionalFormatMap.values());

		List<SpreadSheetColumnFormat> originalSpreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();
		originalSpreadSheetColumnFormats.addAll(spreadSheetColumnFormatMap.values());

		spreadSheetCellMap.clear();
		spreadSheetCellFormatMap.clear();
		spreadSheetConditionalFormatMap.clear();
		spreadSheetColumnFormatMap.clear();

		oldSheetCellMap.clear();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				sheet.removeRow(row);
			}
		}
		
		List<SpreadSheetCell> spreadSheetCells = new ArrayList<SpreadSheetCell>();

		for (SpreadSheetCellContent spreadSheetCell : originalSpreadSheetCells) {

			if (spreadSheetCell.getCellColumn() >= insertColumnRequest.getColumn()) {

				SpreadSheetCell spreadSheetCell2 = new SpreadSheetCell(spreadSheet.getId(), null, null, spreadSheetCell.getCellRow(),
						spreadSheetCell.getCellColumn());
				spreadSheetCells.add(spreadSheetCell2);
				spreadSheetCell.setCellColumn(spreadSheetCell.getCellColumn() + insertColumnRequest.getColumnCount());
				spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
			}
			chacheOldContent(spreadSheetCell);
			CellReference cellReference = new CellReference(spreadSheetCell.getCellRow(), spreadSheetCell.getCellColumn());
			spreadSheetCellMap.put(cellReference.formatAsString(), spreadSheetCell);
			Row row = sheet.getRow(cellReference.getRow());
			if (row == null)
				row = sheet.createRow(cellReference.getRow());
			Cell cell = row.getCell(cellReference.getCol());
			if (cell == null) {
				cell = row.createCell(cellReference.getCol());
			}
			switch (spreadSheetCell.getSpreadSheetCellType()) {
				case TYPE_BOOLEAN:
					if (spreadSheetCell.getBooleanValue() != null)
						cell.setCellValue(spreadSheetCell.getBooleanValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_FORMULA:
					if (spreadSheetCell.getFormulaValue() != null) {
						cell.setCellFormula(spreadSheetCell.getFormulaValue());
						if (spreadSheetCell.getCellColumn() >= insertColumnRequest.getColumn()) {
							formulaShifter.updateCellFormula((HSSFCell) cell, insertColumnRequest.getColumnCount(), 0);
							spreadSheetCell.setFormulaValue(cell.getCellFormula());
							spreadSheetHandler.addSpreadSheetCellContentUpdate(spreadSheetCell);
						}
					}
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_NUMERIC:
					if (spreadSheetCell.getNumericValue() != null)
						cell.setCellValue(spreadSheetCell.getNumericValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case TYPE_STRING:
					if (spreadSheetCell.getStringValue() != null)
						cell.setCellValue(spreadSheetCell.getStringValue());
					else
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					break;
				case CLEAR:
					break;
			}

		}

		List<SpreadSheetCellFormat> spreadSheetCellFormats = new ArrayList<SpreadSheetCellFormat>();

		for (SpreadSheetCellFormat spreadSheetCellFormat : originalSpreadSheetCellFormats) {
			if (spreadSheetCellFormat.getCellColumn() >= insertColumnRequest.getColumn()) {
				SpreadSheetCellFormat spreadSheetCellFormat2 = new SpreadSheetCellFormat();
				spreadSheetCellFormat2.setCellColumn(spreadSheetCellFormat.getCellColumn());
				spreadSheetCellFormat2.setCellRow(spreadSheetCellFormat.getCellRow());
				spreadSheetCellFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetCellFormat2.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				spreadSheetCellFormats.add(spreadSheetCellFormat2);

				spreadSheetCellFormat.setCellColumn(spreadSheetCellFormat.getCellColumn() + insertColumnRequest.getColumnCount());
				spreadSheetCellFormat.setSolverCell(FormulaShifter.updateCellReference(spreadSheetCellFormat.getSolverCell(),  insertColumnRequest.getColumnCount(), 0));
				
				spreadSheetHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetCellFormat.getCellRow(), spreadSheetCellFormat.getCellColumn());
			spreadSheetCellFormatMap.put(cellReference.formatAsString(), spreadSheetCellFormat);
		}

		HSSFSheet conditionShiftSheet = workbook.createSheet("conditonshift");

		List<SpreadSheetConditionalFormat> spreadSheetConditionalFormats = new ArrayList<SpreadSheetConditionalFormat>();

		for (SpreadSheetConditionalFormat spreadSheetConditionalFormat : originalSpreadSheetConditionalFormats) {
			if (spreadSheetConditionalFormat.getCellColumn() >= insertColumnRequest.getColumn()) {
				SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = new SpreadSheetConditionalFormat();
				spreadSheetConditionalFormat2.setCellColumn(spreadSheetConditionalFormat.getCellColumn());
				spreadSheetConditionalFormat2.setCellRow(spreadSheetConditionalFormat.getCellRow());
				spreadSheetConditionalFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetConditionalFormat2.setSpreadSheetCondition1(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition2(null);
				spreadSheetConditionalFormat2.setSpreadSheetCondition3(null);
				spreadSheetConditionalFormats.add(spreadSheetConditionalFormat2);
				spreadSheetConditionalFormat.setCellColumn(spreadSheetConditionalFormat.getCellColumn() + insertColumnRequest.getColumnCount());

				for (int i = 0; i < 3; i++) {
					SpreadSheetCondition spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
					if (i == 1)
						spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
					if (i == 2)
						spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
					if (spreadSheetCondition != null && spreadSheetCondition.getConditionType() == ConditionType.FORMULA_IS) {
						HSSFRow row = conditionShiftSheet.createRow(spreadSheetConditionalFormat.getCellRow());
						HSSFCell cell = row.createCell(spreadSheetConditionalFormat.getCellColumn());
						try {
							cell.setCellFormula(spreadSheetCondition.getFormula());
							formulaShifter.updateCellFormula(cell, insertColumnRequest.getColumnCount(), 0);
							spreadSheetCondition.setFormula(cell.getCellFormula());
						}
						catch (Exception e) {
							log.error("Bug", e);
						}
						conditionShiftSheet.removeRow(row);
					}
				}

				spreadSheetHandler.addSpreadSheetConditionalFormatUpdate(spreadSheetConditionalFormat);
			}

			CellReference cellReference = new CellReference(spreadSheetConditionalFormat.getCellRow(), spreadSheetConditionalFormat.getCellColumn());
			spreadSheetConditionalFormatMap.put(cellReference.formatAsString(), spreadSheetConditionalFormat);
		}
		workbook.removeSheetAt(workbook.getSheetIndex("conditonshift"));

		List<SpreadSheetColumnFormat> spreadSheetColumnFormats = new ArrayList<SpreadSheetColumnFormat>();
		for (SpreadSheetColumnFormat spreadSheetColumnFormat : originalSpreadSheetColumnFormats) {
			if (spreadSheetColumnFormat.getColumnNumber() >= insertColumnRequest.getColumn()) {
				SpreadSheetColumnFormat spreadSheetColumnFormat2 = new SpreadSheetColumnFormat();
				spreadSheetColumnFormat2.setColumnNumber(spreadSheetColumnFormat.getColumnNumber());
				spreadSheetColumnFormat2.setSpreadSheet(spreadSheet.getId());
				spreadSheetColumnFormat2.setColumnWidth(100);
				spreadSheetColumnFormats.add(spreadSheetColumnFormat2);
				spreadSheetColumnFormatMap.put(spreadSheetColumnFormat2.getColumnNumber(), spreadSheetColumnFormat2);

				spreadSheetColumnFormat.setColumnNumber(spreadSheetColumnFormat.getColumnNumber() + insertColumnRequest.getColumnCount());
				spreadSheetHandler.updateSpreadSheetColumnFormat(spreadSheetColumnFormat);
			}

			spreadSheetColumnFormatMap.put(spreadSheetColumnFormat.getColumnNumber(), spreadSheetColumnFormat);
		}

		recalculate();

		open(null);
	}

	/**
	 * Gets the channels.
	 *
	 * @return the channels
	 */
	public Set<Channel> getChannels() {

		return channels;
	}

	/**
	 * Handle fix message.
	 *
	 * @param mdInputEntries the md input entries
	 */
	public void handleFIXMessage(List<MDInputEntry> mdInputEntries) {

		List<ModifySheetCellRequest> modifySheetCellRequests = new ArrayList<ModifySheetCellRequest>();
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				for (MDInputEntry mdInputEntry : mdInputEntries) {

					int securityColumn = mdInputEntry.getSecurityColumn();
					int counterpartyColumn = mdInputEntry.getCounterpartyColumn();
					boolean securityMatched = false;
					boolean counterpartyMatched = false;
					if (securityColumn >= 0) {
						HSSFCell cell = row.getCell(securityColumn);
						if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && mdInputEntry.getSecurityValue() != null
								&& cell.getStringCellValue() != null && mdInputEntry.getSecurityValue().trim().equals(cell.getStringCellValue().trim()))
							securityMatched = true;
					}
					if (counterpartyColumn < 0)
						counterpartyMatched = true;
					else {
						HSSFCell cell = row.getCell(counterpartyColumn);
						if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && mdInputEntry.getCounterpartyValue() != null
								&& cell.getStringCellValue() != null && mdInputEntry.getCounterpartyValue().trim().equals(cell.getStringCellValue().trim()))
							counterpartyMatched = true;
					}
					if (securityMatched && counterpartyMatched) {
						if (mdInputEntry.getMdEntryPxColumn() >= 0) {
							HSSFCell cell = row.getCell(mdInputEntry.getMdEntryPxColumn());
							if (updateCheck(cell, mdInputEntry.getMdEntryPxValue())) {
								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdEntryPxValue(), null, i,
										mdInputEntry.getMdEntryPxColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}

						if (mdInputEntry.getMdEntrySizeColumn() >= 0) {
							HSSFCell cell = row.getCell(mdInputEntry.getMdEntrySizeColumn());
							if (updateCheck(cell, mdInputEntry.getMdEntrySizeValue())) {
								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdEntrySizeValue(), null, i,
										mdInputEntry.getMdEntrySizeColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getMdEntryDateColumn() >= 0) {
							HSSFCell cell = row.getCell(mdInputEntry.getMdEntryDateColumn());
							if (updateCheck(cell, mdInputEntry.getMdEntryDateValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdEntryDateValue(), null, i,
										mdInputEntry.getMdEntryDateColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, ValueFormat.DATE, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getMdEntryTimeColumn() >= 0) {

							HSSFCell cell = row.getCell(mdInputEntry.getMdEntryTimeColumn());
							if (updateCheck(cell, mdInputEntry.getMdEntryTimeValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdEntryTimeValue(), null, i,
										mdInputEntry.getMdEntryTimeColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, ValueFormat.TIME, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getMdPriceDeltaColumn() >= 0) {

							HSSFCell cell = row.getCell(mdInputEntry.getMdPriceDeltaColumn());
							if (updateCheck(cell, mdInputEntry.getMdPriceDeltaValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdPriceDeltaValue(), null, i,
										mdInputEntry.getMdPriceDeltaColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getMdTradeVolumeColumn() >= 0) {

							HSSFCell cell = row.getCell(mdInputEntry.getMdTradeVolumeColumn());
							if (updateCheck(cell, mdInputEntry.getMdTradeVolumeValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getMdTradeVolumeValue(), null, i,
										mdInputEntry.getMdTradeVolumeColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getHighPxColumn() >= 0) {

							HSSFCell cell = row.getCell(mdInputEntry.getHighPxColumn());
							if (updateCheck(cell, mdInputEntry.getHighPxValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getHighPxValue(), null, i,
										mdInputEntry.getHighPxColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
						if (mdInputEntry.getLowPxColumn() >= 0) {

							HSSFCell cell = row.getCell(mdInputEntry.getLowPxColumn());
							if (updateCheck(cell, mdInputEntry.getLowPxValue())) {

								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheet.getId(), mdInputEntry.getLowPxValue(), null, i,
										mdInputEntry.getLowPxColumn());
								ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell, 0);
								modifySheetCellRequests.add(modifySheetCellRequest);
							}
						}
					}

				}
			}
		}

		if (modifySheetCellRequests.size() > 0)
			onModifySheetCellRequest(modifySheetCellRequests, null);

	}

	private boolean updateCheck(HSSFCell cell, Double value) {

		if (cell == null && value != null)
			return true;
		if (value == null) {
			if (cell == null)
				return false;
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return false;
			return true;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue() != value;
		}
		return true;
	}

	/**
	 * Close.
	 */
	public void close() {

		closed = true;

	}

	/**
	 * Copy workbook.
	 *
	 * @return the hSSF workbook
	 */
	public HSSFWorkbook copyWorkbook() {

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			workbook.write(byteArrayOutputStream);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(byteArrayInputStream);
			byteArrayInputStream.close();
			byteArrayOutputStream.close();
			return hssfWorkbook;
		}
		catch (IOException e) {
			log.error("Bug", e);
		}
		
		return null;
	}
	

}
