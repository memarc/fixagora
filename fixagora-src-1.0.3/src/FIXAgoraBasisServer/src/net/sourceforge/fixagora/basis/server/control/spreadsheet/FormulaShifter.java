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

import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;

/**
 * The Class FormulaShifter.
 */
public class FormulaShifter {

	private HSSFWorkbook hssfWorkbook = null;

	/**
	 * Instantiates a new formula shifter.
	 *
	 * @param hssfWorkbook the hssf workbook
	 */
	public FormulaShifter(HSSFWorkbook hssfWorkbook) {

		super();
		this.hssfWorkbook = hssfWorkbook;
	}

	/**
	 * Update cell formula.
	 *
	 * @param cell the cell
	 * @param colShift the col shift
	 * @param rowShift the row shift
	 */
	public void updateCellFormula(HSSFCell cell, int colShift, int rowShift) {

		try {
			if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				Ptg[] ptgs = HSSFFormulaParser.parse(cell.getCellFormula(), hssfWorkbook);
				for (int i = 0; i < ptgs.length; i++) {
					Ptg newPtg = adjustFormula(ptgs[i], colShift, rowShift);
					if (newPtg != null)
						ptgs[i] = newPtg;

				}
								
				cell.setCellFormula(HSSFFormulaParser.toFormulaString(hssfWorkbook, ptgs));
			}
		}
		catch (Exception e) {
			cell.setCellType(Cell.CELL_TYPE_ERROR);
		}

	}

	private Ptg adjustFormula(Ptg ptg, int colShift, int rowShift) {

		if (ptg instanceof RefPtg) {
			RefPtg rptg = (RefPtg) ptg;
			return moveRefPtg(rptg, colShift, rowShift);
		}
		if (ptg instanceof Ref3DPtg) {
			Ref3DPtg rptg = (Ref3DPtg) ptg;
			return moveRefPtg(rptg, colShift, rowShift);
		}
		if (ptg instanceof Area2DPtgBase) {
			return moveAreaPtg((Area2DPtgBase) ptg, colShift, rowShift);
		}
		if (ptg instanceof Area3DPtg) {
			Area3DPtg aptg = (Area3DPtg) ptg;
			return moveAreaPtg(aptg, colShift, rowShift);
		}
		return null;

	}

	private Ptg moveRefPtg(RefPtgBase rptg, int colShift, int rowShift) {

		if (rptg.isColRelative()) {
			
			int refCol = rptg.getColumn();
			rptg.setColumn(refCol + colShift);
		}
		if (rptg.isRowRelative()) {
			
			int refRow = rptg.getRow();
			rptg.setRow(refRow + rowShift);
		}
		return rptg;
	}

	private Ptg moveAreaPtg(AreaPtgBase aptg, int colShift, int rowShift) {

		int firstRow = aptg.getFirstRow();
		int lastRow = aptg.getLastRow();
		
		int firstCol = aptg.getFirstColumn();
		int lastCol = aptg.getLastColumn();

		if (aptg.isFirstColRelative())
			aptg.setFirstColumn(firstCol+colShift);
		if (aptg.isLastColRelative())
			aptg.setLastColumn(lastCol+colShift);
		
		if (aptg.isFirstRowRelative())
			aptg.setFirstRow(firstRow+rowShift);
		if (aptg.isLastRowRelative())
			aptg.setLastRow(lastRow+rowShift);
		
		return aptg;

	}
	
	/**
	 * Update cell reference.
	 *
	 * @param reference the reference
	 * @param colShift the col shift
	 * @param rowShift the row shift
	 * @return the string
	 */
	public static String updateCellReference(String reference, int colShift, int rowShift) {

		if(reference==null)
			return null;
		
		boolean fixedColumn = false;
		
		if(reference.trim().startsWith("$"))
			fixedColumn = true;
		
		boolean fixedRow = false;
		
		if(reference.trim().lastIndexOf('$')>0)
			fixedRow = true;
		
		try {
			CellReference cellReference = new CellReference(reference.trim().replaceAll("$", ""));
			
			StringBuffer buffer = new StringBuffer();
			
			if(!fixedColumn)
			{
				buffer.append(CellReference.convertNumToColString(cellReference.getCol()+colShift));
			}
			else
			{
				buffer.append("$");
				buffer.append(CellReference.convertNumToColString(cellReference.getCol()));
			}
			
			if(!fixedRow)
			{
				buffer.append(Integer.toString(cellReference.getRow()+1+rowShift));
			}
			else
			{
				buffer.append("$");
				buffer.append(Integer.toString(cellReference.getRow()+1));
			}
			
			return buffer.toString();
		}
		catch (Exception e) {
			return null;
		}
		
	}

}
