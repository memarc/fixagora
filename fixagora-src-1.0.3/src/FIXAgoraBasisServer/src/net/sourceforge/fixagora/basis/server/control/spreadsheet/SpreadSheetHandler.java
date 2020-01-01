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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.ReplaceQueue;
import net.sourceforge.fixagora.basis.server.control.component.AbstractComponentHandler;
import net.sourceforge.fixagora.basis.server.control.component.MDInputEntry;
import net.sourceforge.fixagora.basis.server.control.component.MDInputEntryList;
import net.sourceforge.fixagora.basis.server.control.component.MDOutputEntry;
import net.sourceforge.fixagora.basis.server.control.component.SpreadSheetComponentHandler;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.AccruedInterestAtMaturityFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.AccruedInterestFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CellDeltaFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CleanPriceFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponDateNextFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponDatePrecedeFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponDaysBeforeSettlementFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponDaysFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponDaysNextSettlementFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponNumbersFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.CouponRateFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.DiscountFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.DurationFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.EffectFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.IntRateFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.InterceptFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.InterestAccrualDateFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.IssueDateFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.LastValueFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.LinearFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.MaturityFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.ModifiedDurationFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.NominalFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.OddFirstPriceFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.OddFirstYieldFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.OddLastPriceFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.OddLastYieldFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.PriceAtMaturityFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.PriceDiscountFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.SlopeFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.SplineFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.SpreadSheetLocation;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.TBillEqFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.TBillPriceFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.TBillYieldFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.ValutaFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.YieldAtMaturityFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.YieldDiscountFunction;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.calculation.YieldFunction;
import net.sourceforge.fixagora.basis.shared.model.communication.CloseSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CopySpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.OpenSpreadSheetRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SolverRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SolverResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellContent;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetColumnFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetRowFormat;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.jboss.netty.channel.Channel;

/**
 * The Class SpreadSheetHandler.
 */
public class SpreadSheetHandler {

	private Map<Long, SpreadSheetWorker> spreadSheetWorkerMap = Collections.synchronizedMap(new HashMap<Long, SpreadSheetWorker>());
	private Map<String, SpreadSheetWorker> nameSpreadSheetWorkerMap = Collections.synchronizedMap(new HashMap<String, SpreadSheetWorker>());
	private Map<String, Set<SpreadSheetWorker>> workbookSpreadSheetWorkerMap = Collections.synchronizedMap(new HashMap<String, Set<SpreadSheetWorker>>());
	private Map<Channel, HSSFWorkbook> copyWorkbookMap = Collections.synchronizedMap(new HashMap<Channel, HSSFWorkbook>());
	private Map<Channel, SpreadSheetLocation> copySpreadSheetLocationMap = Collections.synchronizedMap(new HashMap<Channel, SpreadSheetLocation>());
	private Map<Channel, Set<SpreadSheetCellFormat>> copySpreadSheetCellFormatMap = Collections
			.synchronizedMap(new HashMap<Channel, Set<SpreadSheetCellFormat>>());
	private Map<Channel, Set<SpreadSheetConditionalFormat>> copySpreadSheetConditionalFormatMap = Collections
			.synchronizedMap(new HashMap<Channel, Set<SpreadSheetConditionalFormat>>());
	private Map<String, HSSFWorkbook> workbookMap = Collections.synchronizedMap(new HashMap<String, HSSFWorkbook>());
	private final ReplaceQueue updateSpreadSheetCellContents = new ReplaceQueue();
	private boolean closed = false;
	private Map<Long, MDInputEntryList> entryQueueMap = Collections.synchronizedMap(new HashMap<Long, MDInputEntryList>());
	private BasisPersistenceHandler basisPersistenceHandler = null;
	private AggregatingUDFFinder udfToolpack = null;
	private BusinessComponentHandler businessComponentHandler = null;
	private Map<String, Object> lockMap = new HashMap<String, Object>();
	private Timer timer;

	/**
	 * Instantiates a new spread sheet handler.
	 *
	 * @param basisPersistenceHandler the basis persistence handler
	 * @param businessComponentHandler the business component handler
	 */
	public SpreadSheetHandler(BasisPersistenceHandler basisPersistenceHandler, BusinessComponentHandler businessComponentHandler) {

		this.basisPersistenceHandler = basisPersistenceHandler;
		this.businessComponentHandler = businessComponentHandler;

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {

				updateSpreadSheetCellContents();
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 10000, 10000);

		List<AbstractSpreadSheetFunction> abstractSpreadSheetFunctions = new ArrayList<AbstractSpreadSheetFunction>();

		ServiceLoader<SpreadsheetFunctions> serviceLoader = ServiceLoader.load(SpreadsheetFunctions.class);

		for (SpreadsheetFunctions spreadsheetFunctions : serviceLoader) {

			for (AbstractSpreadSheetFunction abstractSpreadSheetFunction : spreadsheetFunctions.getSpreadsheetFunctions()) {
				abstractSpreadSheetFunction.setBusinessComponentHandler(businessComponentHandler);
				abstractSpreadSheetFunctions.add(abstractSpreadSheetFunction);
			}
		}

		String[] functionNames = new String[9 + abstractSpreadSheetFunctions.size()];
		FreeRefFunction[] functionImpls = new FreeRefFunction[9 + abstractSpreadSheetFunctions.size()];

		functionNames[0] = "VALUTA";
		functionImpls[0] = new ValutaFunction(businessComponentHandler.getBankCalendarMap());

		functionNames[1] = "SPLINE";
		functionImpls[1] = new SplineFunction();

		functionNames[2] = "LINEAR";
		functionImpls[2] = new LinearFunction();

		functionNames[3] = "COUPONRATE";
		functionImpls[3] = new CouponRateFunction(businessComponentHandler.getSecurityDictionary());

		functionNames[4] = "MATURITY";
		functionImpls[4] = new MaturityFunction(businessComponentHandler.getSecurityDictionary());

		functionNames[5] = "LASTVALUE";
		functionImpls[5] = new LastValueFunction(nameSpreadSheetWorkerMap);

		functionNames[6] = "CELLDELTA";
		functionImpls[6] = new CellDeltaFunction(nameSpreadSheetWorkerMap);
		
		functionNames[7] = "INTERESTACCRUALDATE";
		functionImpls[7] = new InterestAccrualDateFunction(businessComponentHandler.getSecurityDictionary());
		
		functionNames[8] = "ISSUEDATE";
		functionImpls[8] = new IssueDateFunction(businessComponentHandler.getSecurityDictionary());

		int i = 9;

		for (AbstractSpreadSheetFunction abstractSpreadSheetFunction : abstractSpreadSheetFunctions) {
			functionNames[i] = abstractSpreadSheetFunction.getFunctionName();
			functionImpls[i] = abstractSpreadSheetFunction;

			i++;
		}

		UDFFinder udfs = new DefaultUDFFinder(functionNames, functionImpls);

		udfToolpack = new AggregatingUDFFinder(udfs);

		AnalysisToolPak.registerFunction("YIELD", new YieldFunction());
		WorkbookEvaluator.registerFunction("PRICE", new CleanPriceFunction());
		WorkbookEvaluator.registerFunction("ACCRINT", new AccruedInterestFunction());
		WorkbookEvaluator.registerFunction("ACCRINTM", new AccruedInterestAtMaturityFunction());
		WorkbookEvaluator.registerFunction("TBILLYIELD", new TBillYieldFunction());
		WorkbookEvaluator.registerFunction("TBILLPRICE", new TBillPriceFunction());
		WorkbookEvaluator.registerFunction("TBILLEQ", new TBillEqFunction());
		WorkbookEvaluator.registerFunction("PRICEMAT", new PriceAtMaturityFunction());
		WorkbookEvaluator.registerFunction("YIELDMAT", new YieldAtMaturityFunction());
		WorkbookEvaluator.registerFunction("ODDFYIELD", new OddFirstYieldFunction());
		WorkbookEvaluator.registerFunction("ODDLYIELD", new OddLastYieldFunction());
		WorkbookEvaluator.registerFunction("ODDLPRICE", new OddLastPriceFunction());
		WorkbookEvaluator.registerFunction("DURATION", new DurationFunction());
		WorkbookEvaluator.registerFunction("MDURATION", new ModifiedDurationFunction());
		WorkbookEvaluator.registerFunction("ODDFPRICE", new OddFirstPriceFunction());
		WorkbookEvaluator.registerFunction("COUPDAYS", new CouponDaysFunction());
		WorkbookEvaluator.registerFunction("COUPDAYBS", new CouponDaysBeforeSettlementFunction());
		WorkbookEvaluator.registerFunction("COUPDAYSNC", new CouponDaysNextSettlementFunction());
		WorkbookEvaluator.registerFunction("COUPNUM", new CouponNumbersFunction());
		WorkbookEvaluator.registerFunction("COUPPCD", new CouponDatePrecedeFunction());
		WorkbookEvaluator.registerFunction("COUPNCD", new CouponDateNextFunction());
		WorkbookEvaluator.registerFunction("DISC", new DiscountFunction());
		WorkbookEvaluator.registerFunction("PRICEDISC", new PriceDiscountFunction());
		WorkbookEvaluator.registerFunction("YIELDDISC", new YieldDiscountFunction());
		WorkbookEvaluator.registerFunction("INTRATE", new IntRateFunction());
		WorkbookEvaluator.registerFunction("EFFECT", new EffectFunction());
		WorkbookEvaluator.registerFunction("NOMINAL", new NominalFunction());
		FunctionEval.registerFunction("SLOPE", new SlopeFunction());
		FunctionEval.registerFunction("INTERCEPT", new InterceptFunction());

		List<SpreadSheet> spreadSheets = basisPersistenceHandler
				.executeQuery(SpreadSheet.class, "select a from SpreadSheet a ", businessComponentHandler, true);

		for (SpreadSheet spreadSheet : spreadSheets)
			addSpreadSheet(spreadSheet, false);
		for (SpreadSheetWorker spreadSheetWorker : spreadSheetWorkerMap.values())
			spreadSheetWorker.init();

	}

	/**
	 * Adds the spread sheet.
	 *
	 * @param spreadSheet the spread sheet
	 * @param init the init
	 */
	public void addSpreadSheet(SpreadSheet spreadSheet, boolean init) {

		addSpreadSheet(spreadSheet, new HashSet<Channel>(), init);
	}

	/**
	 * Adds the spread sheet.
	 *
	 * @param spreadSheet the spread sheet
	 * @param channels the channels
	 * @param init the init
	 */
	public void addSpreadSheet(SpreadSheet spreadSheet, Set<Channel> channels, boolean init) {

		HSSFWorkbook workbook = null;
		boolean multiworkbook = false;
		if (spreadSheet.getWorkbook() == null || spreadSheet.getWorkbook().trim().length() == 0) {
			workbook = new HSSFWorkbook();
			workbook.addToolPack(udfToolpack);
			workbookMap.put("emptyworkbook" + Long.toString(spreadSheet.getId()), workbook);
		}
		else {
			workbook = workbookMap.get(spreadSheet.getWorkbook());
			if (workbook == null) {
				workbook = new HSSFWorkbook();
				workbook.addToolPack(udfToolpack);
				workbookMap.put(spreadSheet.getWorkbook(), workbook);
			}
			multiworkbook = true;
		}

		SpreadSheetWorker spreadSheetWorker = new SpreadSheetWorker(spreadSheet, workbook, this, channels);
		if (init)
			spreadSheetWorker.init();
		spreadSheetWorkerMap.put(spreadSheet.getId(), spreadSheetWorker);
		nameSpreadSheetWorkerMap.put(spreadSheet.getName(), spreadSheetWorker);
		if (multiworkbook) {

			Set<SpreadSheetWorker> spreadSheetWorkers = workbookSpreadSheetWorkerMap.get(spreadSheet.getWorkbook());
			if (spreadSheetWorkers == null) {
				spreadSheetWorkers = new HashSet<SpreadSheetWorker>();
				workbookSpreadSheetWorkerMap.put(spreadSheet.getWorkbook(), spreadSheetWorkers);
			}
			spreadSheetWorkers.add(spreadSheetWorker);
		}
	}

	/**
	 * Close.
	 */
	public void close() {

		closed = true;
		timer.cancel();
		updateSpreadSheetCellContents();
	}

	/**
	 * On close spread sheet request.
	 *
	 * @param closeSpreadSheetRequest the close spread sheet request
	 * @param channel the channel
	 */
	public void onCloseSpreadSheetRequest(CloseSpreadSheetRequest closeSpreadSheetRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(closeSpreadSheetRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.close(channel);
			}
		}

	}

	/**
	 * On open spread sheet request.
	 *
	 * @param openSpreadSheetRequest the open spread sheet request
	 * @param channel the channel
	 */
	public void onOpenSpreadSheetRequest(OpenSpreadSheetRequest openSpreadSheetRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(openSpreadSheetRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {

				spreadSheetWorker.open(channel);
			}
		}
	}

	/**
	 * On modify sheet cell request.
	 *
	 * @param modifySheetCellRequest the modify sheet cell request
	 * @param channel the channel
	 */
	public void onModifySheetCellRequest(ModifySheetCellRequest modifySheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifySheetCellRequest.getSpreadSheetCell().getSheet());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				List<ModifySheetCellRequest> modifySheetCellRequests = new ArrayList<ModifySheetCellRequest>();
				modifySheetCellRequests.add(modifySheetCellRequest);
				spreadSheetWorker.onModifySheetCellRequest(modifySheetCellRequests, channel);
			}
		}
	}

	/**
	 * On copy spread sheet cell request.
	 *
	 * @param copySpreadSheetCellRequest the copy spread sheet cell request
	 * @param channel the channel
	 */
	public void onCopySpreadSheetCellRequest(CopySpreadSheetCellRequest copySpreadSheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(copySpreadSheetCellRequest.getSourceSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				doCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
			}
		}
	}

	private void doCopySpreadSheetCellRequest(CopySpreadSheetCellRequest copySpreadSheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(copySpreadSheetCellRequest.getSourceSheetId());
		if (spreadSheetWorker != null) {
			Sheet sourceSheet = spreadSheetWorker.getSheet();
			Set<SpreadSheetCellFormat> spreadSheetCellFormatMap = copySpreadSheetCellFormatMap.get(channel);
			if (spreadSheetCellFormatMap == null) {
				spreadSheetCellFormatMap = new HashSet<SpreadSheetCellFormat>();
				copySpreadSheetCellFormatMap.put(channel, spreadSheetCellFormatMap);
			}
			else
				spreadSheetCellFormatMap.clear();

			copySpreadSheetLocationMap.put(channel,
					new SpreadSheetLocation(copySpreadSheetCellRequest.getSourceRow(), copySpreadSheetCellRequest.getSourceColumn()));

			Set<SpreadSheetConditionalFormat> spreadSheetConditionalFormatMap = copySpreadSheetConditionalFormatMap.get(channel);
			if (spreadSheetConditionalFormatMap == null) {
				spreadSheetConditionalFormatMap = new HashSet<SpreadSheetConditionalFormat>();
				copySpreadSheetConditionalFormatMap.put(channel, spreadSheetConditionalFormatMap);
			}
			else
				spreadSheetConditionalFormatMap.clear();

			HSSFWorkbook copyWorkbook = new HSSFWorkbook();
			for (int i = 0; i < sourceSheet.getWorkbook().getNumberOfSheets(); i++)
				copyWorkbook.createSheet(sourceSheet.getWorkbook().getSheetAt(i).getSheetName());
			Sheet copySheet = copyWorkbook.createSheet("c1o2p3y4");
			for (int i = copySpreadSheetCellRequest.getSourceRow(); i < copySpreadSheetCellRequest.getSourceRow()
					+ copySpreadSheetCellRequest.getSourceRowCount(); i++) {
				Row sourceRow = sourceSheet.getRow(i);

				Row copyRow = copySheet.createRow(i);
				for (int j = copySpreadSheetCellRequest.getSourceColumn(); j < copySpreadSheetCellRequest.getSourceColumn()
						+ copySpreadSheetCellRequest.getSourceColumnCount(); j++) {
					CellReference cellReference = new CellReference(i, j);
					SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetWorker.getSpreadSheetCellFormat(cellReference.formatAsString());
					if (spreadSheetCellFormat != null) {
						SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetCellFormat.copy();
						spreadSheetCellFormat2.setCellColumn(j);
						spreadSheetCellFormat2.setCellRow(i);
						spreadSheetCellFormatMap.add(spreadSheetCellFormat2);
					}

					SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetWorker.getSpreadSheetConditionalFormat(cellReference
							.formatAsString());
					if (spreadSheetConditionalFormat != null) {
						SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = spreadSheetConditionalFormat.copy();
						spreadSheetConditionalFormat2.setCellColumn(j);
						spreadSheetConditionalFormat2.setCellRow(i);
						spreadSheetConditionalFormatMap.add(spreadSheetConditionalFormat2);
					}

				}
				if (sourceRow != null) {
					for (int j = copySpreadSheetCellRequest.getSourceColumn(); j < copySpreadSheetCellRequest.getSourceColumn()
							+ copySpreadSheetCellRequest.getSourceColumnCount(); j++) {
						Cell sourceCell = sourceRow.getCell(j);
						Cell copyCell = copyRow.createCell(j);
						if (sourceCell != null) {

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
			copyWorkbookMap.put(channel, copyWorkbook);
		}

	}

	/**
	 * On cut spread sheet cell request.
	 *
	 * @param cutSpreadSheetCellRequest the cut spread sheet cell request
	 * @param channel the channel
	 */
	public void onCutSpreadSheetCellRequest(CutSpreadSheetCellRequest cutSpreadSheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(cutSpreadSheetCellRequest.getSourceSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				HSSFWorkbook copyWorkbook = spreadSheetWorker.onCutSpreadSheetCellRequest(cutSpreadSheetCellRequest);
				copyWorkbookMap.put(channel, copyWorkbook);

				Set<SpreadSheetCellFormat> spreadSheetCellFormatSet = copySpreadSheetCellFormatMap.get(channel);
				if (spreadSheetCellFormatSet == null) {
					spreadSheetCellFormatSet = new HashSet<SpreadSheetCellFormat>();
					copySpreadSheetCellFormatMap.put(channel, spreadSheetCellFormatSet);
				}
				else
					spreadSheetCellFormatSet.clear();

				copySpreadSheetLocationMap.put(channel,
						new SpreadSheetLocation(cutSpreadSheetCellRequest.getSourceRow(), cutSpreadSheetCellRequest.getSourceColumn()));

				Set<SpreadSheetConditionalFormat> spreadSheetConditionalFormatSet = copySpreadSheetConditionalFormatMap.get(channel);
				if (spreadSheetConditionalFormatSet == null) {
					spreadSheetConditionalFormatSet = new HashSet<SpreadSheetConditionalFormat>();
					copySpreadSheetConditionalFormatMap.put(channel, spreadSheetConditionalFormatSet);
				}
				else
					spreadSheetConditionalFormatSet.clear();

				spreadSheetWorker.onClearSpreadSheetCellFormat(cutSpreadSheetCellRequest.getSourceRow(), cutSpreadSheetCellRequest.getSourceRowCount(),
						cutSpreadSheetCellRequest.getSourceColumn(), cutSpreadSheetCellRequest.getSourceColumnCount(), spreadSheetCellFormatSet,
						spreadSheetConditionalFormatSet);
			}
		}
	}

	/**
	 * On paste spread sheet cell request.
	 *
	 * @param pasteSpreadSheetCellRequest the paste spread sheet cell request
	 * @param channel the channel
	 */
	public void onPasteSpreadSheetCellRequest(PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(pasteSpreadSheetCellRequest.getTargetSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				doPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
			}
		}
	}

	private void doPasteSpreadSheetCellRequest(PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest, Channel channel) {

		HSSFWorkbook workbook = copyWorkbookMap.get(channel);
		Set<SpreadSheetCellFormat> spreadSheetCellFormatMap = copySpreadSheetCellFormatMap.get(channel);
		Set<SpreadSheetConditionalFormat> spreadSheetConditionalFormatMap = copySpreadSheetConditionalFormatMap.get(channel);
		SpreadSheetLocation spreadSheetLocation = copySpreadSheetLocationMap.get(channel);
		if (workbook != null) {
			SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(pasteSpreadSheetCellRequest.getTargetSheetId());
			if (spreadSheetWorker != null)
				spreadSheetWorker.onPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, workbook, spreadSheetCellFormatMap,
						spreadSheetConditionalFormatMap, spreadSheetLocation, channel);
		}
	}

	private void updateSpreadSheetCellContents() {

		List<SpreadSheetCellContent> spreadSheetCellContents = updateSpreadSheetCellContents.getList();
		if (spreadSheetCellContents.size() > 0)
			basisPersistenceHandler.updateSpreadSheetCellContent(spreadSheetCellContents);
	}

	/**
	 * Adds the spread sheet cell content update.
	 *
	 * @param spreadSheetCellContent the spread sheet cell content
	 */
	public void addSpreadSheetCellContentUpdate(SpreadSheetCellContent spreadSheetCellContent) {

		if (spreadSheetCellContent.getId() == 0) {
			List<SpreadSheetCellContent> spreadSheetCellContents = new ArrayList<SpreadSheetCellContent>();
			spreadSheetCellContents.add(spreadSheetCellContent);
			basisPersistenceHandler.updateSpreadSheetCellContent(spreadSheetCellContents);
		}
		else
			updateSpreadSheetCellContents.add(spreadSheetCellContent.clone());

	}

	/**
	 * Update spread sheet cell format.
	 *
	 * @param spreadSheetCellFormat the spread sheet cell format
	 */
	public void updateSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		basisPersistenceHandler.updateSpreadSheetCellFormat(spreadSheetCellFormat);

	}

	/**
	 * Update spread sheet row format.
	 *
	 * @param spreadSheetRowFormat the spread sheet row format
	 */
	public void updateSpreadSheetRowFormat(SpreadSheetRowFormat spreadSheetRowFormat) {

		basisPersistenceHandler.updateSpreadSheetRowFormat(spreadSheetRowFormat);

	}

	/**
	 * Update spread sheet column format.
	 *
	 * @param spreadSheetColumnFormat the spread sheet column format
	 */
	public void updateSpreadSheetColumnFormat(SpreadSheetColumnFormat spreadSheetColumnFormat) {

		basisPersistenceHandler.updateSpreadSheetColumnFormat(spreadSheetColumnFormat);

	}

	/**
	 * Removes the spread sheet.
	 *
	 * @param spreadSheet the spread sheet
	 * @return the sets the
	 */
	public Set<Channel> removeSpreadSheet(SpreadSheet spreadSheet) {

		Set<Channel> channels = new HashSet<Channel>();

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.remove(spreadSheet.getId());
		if (spreadSheetWorker != null) {

			spreadSheetWorker.close();

			channels.addAll(spreadSheetWorker.getChannels());

			workbookMap.remove("emptyworkbook" + Long.toString(spreadSheet.getId()));

			for (HSSFWorkbook workbook : workbookMap.values()) {
				int i = workbook.getSheetIndex(spreadSheetWorker.getSheet());
				if (i >= 0)
					workbook.removeSheetAt(i);
			}

			for (Set<SpreadSheetWorker> spreadSheetWorkers : workbookSpreadSheetWorkerMap.values())
				spreadSheetWorkers.remove(spreadSheetWorker);

		}

		return channels;

	}

	/**
	 * On modify spread sheet cell format request.
	 *
	 * @param modifySpreadSheetCellFormatRequest the modify spread sheet cell format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetCellFormatRequest(ModifySpreadSheetCellFormatRequest modifySpreadSheetCellFormatRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifySpreadSheetCellFormatRequest.getSheetId());
		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onModifySpreadSheetCellFormatRequest(modifySpreadSheetCellFormatRequest, channel);
			}
		}

	}

	/**
	 * On modify column width request.
	 *
	 * @param modifyColumnWidthRequest the modify column width request
	 * @param channel the channel
	 */
	public void onModifyColumnWidthRequest(ModifyColumnWidthRequest modifyColumnWidthRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifyColumnWidthRequest.getSheetId());
		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onModifyColumnWidthRequest(modifyColumnWidthRequest, channel);
			}
		}

	}

	/**
	 * On modify column visible request.
	 *
	 * @param modifyColumnVisibleRequest the modify column visible request
	 * @param channel the channel
	 */
	public void onModifyColumnVisibleRequest(ModifyColumnVisibleRequest modifyColumnVisibleRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifyColumnVisibleRequest.getSheetId());
		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onModifyColumnVisibleRequest(modifyColumnVisibleRequest, channel);
			}
		}
	}

	/**
	 * On modify row visible response.
	 *
	 * @param modifyRowVisibleRequest the modify row visible request
	 * @param channel the channel
	 */
	public void onModifyRowVisibleResponse(ModifyRowVisibleRequest modifyRowVisibleRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifyRowVisibleRequest.getSheetId());
		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onModifyRowVisibleResponse(modifyRowVisibleRequest, channel);
			}
		}
	}

	/**
	 * On modify spread sheet conditional format response.
	 *
	 * @param modifySpreadSheetConditionalFormatRequest the modify spread sheet conditional format request
	 * @param channel the channel
	 */
	public void onModifySpreadSheetConditionalFormatResponse(ModifySpreadSheetConditionalFormatRequest modifySpreadSheetConditionalFormatRequest,
			Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(modifySpreadSheetConditionalFormatRequest.getSheetId());
		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onModifySpreadSheetConditionalFormatRequest(modifySpreadSheetConditionalFormatRequest, channel);
			}
		}

	}

	/**
	 * Adds the spread sheet conditional format update.
	 *
	 * @param spreadSheetConditionalFormat the spread sheet conditional format
	 */
	public void addSpreadSheetConditionalFormatUpdate(SpreadSheetConditionalFormat spreadSheetConditionalFormat) {

		basisPersistenceHandler.updateSpreadSheetConditionalFormat(spreadSheetConditionalFormat);

	}

	/**
	 * On insert column request.
	 *
	 * @param insertColumnRequest the insert column request
	 * @param channel the channel
	 */
	public void onInsertColumnRequest(InsertColumnRequest insertColumnRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(insertColumnRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onInsertColumnRequest(insertColumnRequest, channel);
			}
		}
	}

	/**
	 * On insert row request.
	 *
	 * @param insertRowRequest the insert row request
	 * @param channel the channel
	 */
	public void onInsertRowRequest(InsertRowRequest insertRowRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(insertRowRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onInsertRowRequest(insertRowRequest, channel);
			}
		}
	}

	/**
	 * On delete column request.
	 *
	 * @param deleteColumnRequest the delete column request
	 * @param channel the channel
	 */
	public void onDeleteColumnRequest(DeleteColumnRequest deleteColumnRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(deleteColumnRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onDeleteColumnRequest(deleteColumnRequest, channel);
			}
		}
	}

	/**
	 * On delete row request.
	 *
	 * @param deleteRowRequest the delete row request
	 * @param channel the channel
	 */
	public void onDeleteRowRequest(DeleteRowRequest deleteRowRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(deleteRowRequest.getSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {
				spreadSheetWorker.onDeleteRowRequest(deleteRowRequest, channel);
			}
		}
	}

	/**
	 * Update spread sheet.
	 *
	 * @param spreadSheet the spread sheet
	 */
	public void updateSpreadSheet(SpreadSheet spreadSheet) {

		if (spreadSheet != null) {
			synchronized (getLock(spreadSheet)) {
				Set<Channel> channels = removeSpreadSheet(spreadSheet);
				addSpreadSheet(spreadSheet, channels, true);
			}
		}

	}

	/**
	 * Update affected.
	 *
	 * @param spreadSheetWorker the spread sheet worker
	 */
	public void updateAffected(SpreadSheetWorker spreadSheetWorker) {

		String workbookName = spreadSheetWorker.getSpreadSheet().getWorkbook();
		if (workbookName != null && workbookName.trim().length() > 0) {
			Set<SpreadSheetWorker> spreadSheetWorkers = workbookSpreadSheetWorkerMap.get(workbookName);
			if (spreadSheetWorkers != null)
				for (SpreadSheetWorker spreadSheetWorker2 : spreadSheetWorkers)
					if (spreadSheetWorker2 != spreadSheetWorker) {

						Set<HSSFCell> hssfCells = spreadSheetWorker2.getUpdatedCells();

						spreadSheetWorker2.checkTrigger(hssfCells);

						spreadSheetWorker2.sendUpdates(hssfCells);

					}
		}

	}

	/**
	 * Gets the user.
	 *
	 * @param channel the channel
	 * @return the user
	 */
	public FUser getUser(Channel channel) {

		if (channel == null)
			return null;
		return basisPersistenceHandler.getUser(channel);
	}

	/**
	 * Write log entry.
	 *
	 * @param logEntry the log entry
	 */
	public void writeLogEntry(LogEntry logEntry) {

		basisPersistenceHandler.writeLogEntry(logEntry);

	}

	/**
	 * Handle fix message.
	 *
	 * @param spreadSheet the spread sheet
	 * @param mdInputEntries the md input entries
	 */
	public void handleFIXMessage(final SpreadSheet spreadSheet, List<MDInputEntry> mdInputEntries) {

		MDInputEntryList mdInputEntryList = entryQueueMap.get(spreadSheet.getId());
		if (mdInputEntryList == null) {
			mdInputEntryList = new MDInputEntryList();
			entryQueueMap.put(spreadSheet.getId(), mdInputEntryList);
			Thread thread = new Thread() {

				public void run() {

					processFIXMessage(spreadSheet);
				}
			};
			thread.start();
		}
		mdInputEntryList.add(mdInputEntries);
	}

	/**
	 * Process fix message.
	 *
	 * @param spreadSheet the spread sheet
	 */
	public void processFIXMessage(SpreadSheet spreadSheet) {

		MDInputEntryList mdInputEntryList = entryQueueMap.get(spreadSheet.getId());

		while (!closed) {

			List<MDInputEntry> mdInputEntries = mdInputEntryList.take();

			synchronized (getLock(spreadSheet)) {

				SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(spreadSheet.getId());

				if (spreadSheetWorker != null)
					spreadSheetWorker.handleFIXMessage(mdInputEntries);

			}

		}

	}

	/**
	 * Gets the basis persistence handler.
	 *
	 * @return the basis persistence handler
	 */
	public BasisPersistenceHandler getBasisPersistenceHandler() {

		return basisPersistenceHandler;
	}

	/**
	 * On fill spread sheet cell request.
	 *
	 * @param fillSpreadSheetCellRequest the fill spread sheet cell request
	 * @param channel the channel
	 */
	public void onFillSpreadSheetCellRequest(FillSpreadSheetCellRequest fillSpreadSheetCellRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(fillSpreadSheetCellRequest.getSourceSheetId());

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {

				switch (fillSpreadSheetCellRequest.getDirection()) {
					case DOWN: {
						CopySpreadSheetCellRequest copySpreadSheetCellRequest = new CopySpreadSheetCellRequest(fillSpreadSheetCellRequest.getSourceSheetId(),
								fillSpreadSheetCellRequest.getSourceRow(), 1, fillSpreadSheetCellRequest.getSourceColumn(),
								fillSpreadSheetCellRequest.getSourceColumnCount(), fillSpreadSheetCellRequest.getRequestID());
						doCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
						for (int i = fillSpreadSheetCellRequest.getSourceRow() + 1; i < fillSpreadSheetCellRequest.getSourceRow()
								+ fillSpreadSheetCellRequest.getSourceRowCount(); i++) {
							PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest = new PasteSpreadSheetCellRequest(
									fillSpreadSheetCellRequest.getSourceSheetId(), i, fillSpreadSheetCellRequest.getSourceColumn(),
									fillSpreadSheetCellRequest.getRequestID());
							doPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
						}
					}
						break;
					case UP: {
						CopySpreadSheetCellRequest copySpreadSheetCellRequest = new CopySpreadSheetCellRequest(fillSpreadSheetCellRequest.getSourceSheetId(),
								fillSpreadSheetCellRequest.getSourceRow() + fillSpreadSheetCellRequest.getSourceRowCount() - 1, 1,
								fillSpreadSheetCellRequest.getSourceColumn(), fillSpreadSheetCellRequest.getSourceColumnCount(),
								fillSpreadSheetCellRequest.getRequestID());
						doCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
						for (int i = fillSpreadSheetCellRequest.getSourceRow() + fillSpreadSheetCellRequest.getSourceRowCount() - 2; i >= fillSpreadSheetCellRequest
								.getSourceRow(); i--) {
							PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest = new PasteSpreadSheetCellRequest(
									fillSpreadSheetCellRequest.getSourceSheetId(), i, fillSpreadSheetCellRequest.getSourceColumn(),
									fillSpreadSheetCellRequest.getRequestID());
							doPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
						}
					}
						break;
					case RIGHT: {
						CopySpreadSheetCellRequest copySpreadSheetCellRequest = new CopySpreadSheetCellRequest(fillSpreadSheetCellRequest.getSourceSheetId(),
								fillSpreadSheetCellRequest.getSourceRow(), fillSpreadSheetCellRequest.getSourceRowCount(),
								fillSpreadSheetCellRequest.getSourceColumn(), 1, fillSpreadSheetCellRequest.getRequestID());
						doCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
						for (int i = fillSpreadSheetCellRequest.getSourceColumn() + 1; i < fillSpreadSheetCellRequest.getSourceColumn()
								+ fillSpreadSheetCellRequest.getSourceColumnCount(); i++) {
							PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest = new PasteSpreadSheetCellRequest(
									fillSpreadSheetCellRequest.getSourceSheetId(), fillSpreadSheetCellRequest.getSourceRow(), i,
									fillSpreadSheetCellRequest.getRequestID());
							doPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
						}
					}
						break;
					case LEFT: {
						CopySpreadSheetCellRequest copySpreadSheetCellRequest = new CopySpreadSheetCellRequest(fillSpreadSheetCellRequest.getSourceSheetId(),
								fillSpreadSheetCellRequest.getSourceRow(), fillSpreadSheetCellRequest.getSourceRowCount(),
								fillSpreadSheetCellRequest.getSourceColumn() + fillSpreadSheetCellRequest.getSourceColumnCount() - 1, 1,
								fillSpreadSheetCellRequest.getRequestID());
						doCopySpreadSheetCellRequest(copySpreadSheetCellRequest, channel);
						for (int i = fillSpreadSheetCellRequest.getSourceColumn() + fillSpreadSheetCellRequest.getSourceColumnCount() - 2; i >= fillSpreadSheetCellRequest
								.getSourceColumn(); i--) {
							PasteSpreadSheetCellRequest pasteSpreadSheetCellRequest = new PasteSpreadSheetCellRequest(
									fillSpreadSheetCellRequest.getSourceSheetId(), fillSpreadSheetCellRequest.getSourceRow(), i,
									fillSpreadSheetCellRequest.getRequestID());
							doPasteSpreadSheetCellRequest(pasteSpreadSheetCellRequest, channel);
						}
					}
						break;
					default:
						break;
				}
			}
		}

	}

	/**
	 * Handle md output entries.
	 *
	 * @param mdOutputEntries the md output entries
	 * @param spreadSheet the spread sheet
	 */
	public void handleMDOutputEntries(Set<MDOutputEntry> mdOutputEntries, SpreadSheet spreadSheet) {

		AbstractComponentHandler abstractComponentHandler = businessComponentHandler.getBusinessComponentHandler(spreadSheet.getId());
		if (abstractComponentHandler instanceof SpreadSheetComponentHandler) {
			((SpreadSheetComponentHandler) abstractComponentHandler).handleMDOutputEntries(mdOutputEntries);
		}

	}

	private synchronized Object getLock(SpreadSheet spreadsheet) {

		if (spreadsheet.getWorkbook() == null || spreadsheet.getWorkbook().trim().length() == 0) {
			Object object = lockMap.get(Long.toString(spreadsheet.getId()));
			if (object == null) {
				object = new Object();
				lockMap.put(Long.toString(spreadsheet.getId()), object);
			}
			return object;
		}

		Object object = lockMap.get(spreadsheet.getWorkbook());
		if (object == null) {
			object = new Object();
			lockMap.put(spreadsheet.getWorkbook(), object);
		}
		return object;

	}

	/**
	 * On solver request.
	 *
	 * @param solverRequest the solver request
	 * @param channel the channel
	 */
	public void onSolverRequest(SolverRequest solverRequest, Channel channel) {

		SpreadSheetWorker spreadSheetWorker = spreadSheetWorkerMap.get(solverRequest.getSpreadSheetCell().getSheet());

		HSSFWorkbook copyWorkbook = null;

		HSSFSheet adjustSheet = null;

		SpreadSheetCell spreadSheetCell = solverRequest.getSpreadSheetCell();
		
		SpreadSheetCellFormat solveCellFormat = null;

		SpreadSheetCellFormat adjustCellFormat = null;

		CellReference adjustCellReference = null;

		CellReference solveCellReference = null;

		if (spreadSheetWorker != null) {
			synchronized (getLock(spreadSheetWorker.getSpreadSheet())) {

				copyWorkbook = spreadSheetWorker.copyWorkbook();

				solveCellReference = new CellReference(spreadSheetCell.getRow(), spreadSheetCell.getColumn());
				solveCellFormat = spreadSheetWorker.getSpreadSheetCellFormat(solveCellReference.formatAsString());
				if (solveCellFormat != null) {
					String adjustReference = solveCellFormat.getSolverCell();
					if (adjustReference != null) {
						adjustReference = adjustReference.replaceAll("$", "");
						adjustCellReference = new CellReference(adjustReference);
						adjustCellFormat = spreadSheetWorker.getSpreadSheetCellFormat(adjustCellReference.formatAsString());
						adjustSheet = copyWorkbook.getSheet(spreadSheetWorker.getSheet().getSheetName());
					}

				}

			}
		}

		if (copyWorkbook == null || solveCellFormat == null || adjustSheet == null)
			basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
		else {
			HSSFRow adjustRow = adjustSheet.getRow(adjustCellReference.getRow());
			if (adjustRow == null)
				adjustRow = adjustSheet.createRow(adjustCellReference.getRow());

			HSSFCell adjustCell = adjustRow.getCell((int) adjustCellReference.getCol());

			if (adjustCell == null)
				adjustCell = adjustRow.createCell((int) adjustCellReference.getCol());

			if (adjustCell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
				return;
			}

			HSSFRow solveRow = adjustSheet.getRow(solveCellReference.getRow());
			if (solveRow == null)
				solveRow = adjustSheet.createRow(solveCellReference.getRow());

			HSSFCell solveCell = solveRow.getCell((int) solveCellReference.getCol());

			if (solveCell == null) {
				basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
				return;
			}

			if (adjustCell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
				return;
			}

			if (adjustCellFormat != null
					&& (adjustCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.PLAIN && adjustCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.NUMBER_SPINNER)) {

				basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
				return;
			}

			try {

				BrentSolver bisectionSolver = new BrentSolver(solveCellFormat.getSolverTolerance());

				HSSFFormulaEvaluator evaluator = (HSSFFormulaEvaluator) copyWorkbook.getCreationHelper().createFormulaEvaluator();

				Double startValue = null;

				if (adjustCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
					startValue = adjustCell.getNumericCellValue();

				Double minBracket = null;

				if (adjustCellFormat != null && adjustCellFormat.getMinimumValue() != null)
					minBracket = adjustCellFormat.getMinimumValue();
				else
				{
					if(startValue!=null)
						minBracket = startValue;
					else
						minBracket = 0d;
					
					for (int i = 0; i < 20; i++) {

						adjustCell.setCellValue(minBracket-Math.pow(10,i));

						evaluator.clearAllCachedResultValues();

						evaluator.evaluateAll();

						if (solveCell.getCachedFormulaResultType() != Cell.CELL_TYPE_NUMERIC) {
							minBracket = minBracket-Math.pow(10,i-1);
							i = 20;
						}
						else if(i==19)
						{
							minBracket = minBracket-Math.pow(10,i);
						}
					}
				}

				Double maxBracket = null;

				if (adjustCellFormat != null && adjustCellFormat.getMaximumValue() != null)
					maxBracket = adjustCellFormat.getMaximumValue();
				else
				{
					if(startValue!=null)
						maxBracket = startValue;
					else
						maxBracket = 0d;
					
					for (int i = 0; i < 20; i++) {

						adjustCell.setCellValue(maxBracket+Math.pow(10,i));

						evaluator.clearAllCachedResultValues();

						evaluator.evaluateAll();

						if (solveCell.getCachedFormulaResultType() != Cell.CELL_TYPE_NUMERIC) {
							maxBracket = maxBracket+Math.pow(10,i-1);
							i = 20;
						}
						else if(i==19)
						{
							maxBracket = maxBracket+Math.pow(10,i);
						}
					}
				}


				if (adjustCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					
					Double resolved = null;
					
					CellFunction cellFunction = new CellFunction(copyWorkbook, solveCell, adjustCell, adjustCellFormat, solverRequest.getSolve());
					
					if (startValue != null)
					{
						resolved = bisectionSolver.solve(10000, cellFunction ,
								minBracket, maxBracket, startValue);
					}
					else
					{
						resolved = bisectionSolver.solve(10000, cellFunction,
								minBracket, maxBracket);
					}
					
					resolved = cellFunction.round(resolved);
					
					adjustCell.setCellValue(resolved);

					evaluator.clearAllCachedResultValues();

					evaluator.evaluateAll();
					
					if (solveCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
						
						double result = solveCell.getNumericCellValue();
						if(solveCellFormat.getSolverTolerance()==null||Math.abs(result-solverRequest.getSolve())<=solveCellFormat.getSolverTolerance())
						{
							
							SpreadSheetCell spreadSheetCell2 = new SpreadSheetCell(spreadSheetCell.getSheet(), resolved, null, adjustCellReference.getRow(), adjustCellReference.getCol());
							ModifySheetCellRequest modifySheetCellRequest = new ModifySheetCellRequest(spreadSheetCell2, 0);
							onModifySheetCellRequest(modifySheetCellRequest, channel);
							
							basisPersistenceHandler.send(new SolverResponse(solverRequest, result), channel);
							return;
						}
					}

				}
			}
			catch (Exception e) {
				basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
				e.printStackTrace();
			}

			basisPersistenceHandler.send(new SolverResponse(solverRequest, false), channel);
		}

	}

	private class CellFunction implements UnivariateFunction {

		private HSSFCell adjustCell = null;
		private HSSFFormulaEvaluator evaluator = null;
		private HSSFCell solveCell = null;
		private double solve = Double.NaN;
		private SpreadSheetCellFormat adjustCellFormat = null;
		private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		private BigDecimal tickSizeBigDecimal = null;
		private int decimalPlaces = 3;

		public CellFunction(HSSFWorkbook copyWorkbook, HSSFCell solveCell, HSSFCell adjustCell, SpreadSheetCellFormat adjustCellFormat, double solve) {

			super();
			evaluator = (HSSFFormulaEvaluator) copyWorkbook.getCreationHelper().createFormulaEvaluator();
			this.adjustCell = adjustCell;
			this.solveCell = solveCell;
			this.solve = solve;
			this.adjustCellFormat = adjustCellFormat;
			
			if(adjustCellFormat!=null)
			{
				if(adjustCellFormat.getPresentationRoundingMode()!=null)
				switch (adjustCellFormat.getPresentationRoundingMode()) {
					case CEILING:
						roundingMode = RoundingMode.CEILING;
						break;
					case DOWN:
						roundingMode = RoundingMode.DOWN;
						break;
					case FLOOR:
						roundingMode = RoundingMode.FLOOR;
						break;
					case HALF_DOWN:
						roundingMode = RoundingMode.HALF_DOWN;
						break;
					case HALF_EVEN:
						roundingMode = RoundingMode.HALF_EVEN;
						break;
					case HALF_UP:
						roundingMode = RoundingMode.HALF_UP;
						break;
					case UP:
						roundingMode = RoundingMode.UP;
						break;
				}
				
				if (adjustCellFormat.getTickSize()!=null&&adjustCellFormat.getTickSize() > 0) {
					tickSizeBigDecimal = new BigDecimal(Double.toString(adjustCellFormat.getTickSize()));
				}
				
				if (adjustCellFormat.getDecimalPlaces()!=null) {
					decimalPlaces = adjustCellFormat.getDecimalPlaces();
				}
			}

		}

		@Override
		public double value(double arg0) {				

			adjustCell.setCellValue(round(arg0));

			evaluator.clearAllCachedResultValues();

			evaluator.evaluateAll();

			if (solveCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
				return solveCell.getNumericCellValue() - solve;
			}

			return Double.NaN;
		}
		
		public double round(double arg0) {
			
			BigDecimal bigDecimal = new BigDecimal(Double.toString(arg0));
			
			
			if(adjustCellFormat!=null)
			{
				if (adjustCellFormat.getTickSize() > 0) 
					bigDecimal = bigDecimal.divide(tickSizeBigDecimal).setScale(0, roundingMode).multiply(tickSizeBigDecimal);
				else
					bigDecimal = bigDecimal.setScale(decimalPlaces, roundingMode);
			}
			
			return bigDecimal.doubleValue();
			
		}

	}

}
