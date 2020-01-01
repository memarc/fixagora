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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.model.editor.CellUpdateHandler;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.basis.client.view.component.ButtonField;
import net.sourceforge.fixagora.basis.client.view.component.CheckboxField;
import net.sourceforge.fixagora.basis.client.view.component.ComboBoxField;
import net.sourceforge.fixagora.basis.client.view.component.DateChooser;
import net.sourceforge.fixagora.basis.client.view.component.DaySpinner;
import net.sourceforge.fixagora.basis.client.view.component.FieldInterface;
import net.sourceforge.fixagora.basis.client.view.component.HourSpinner;
import net.sourceforge.fixagora.basis.client.view.component.MinuteSpinner;
import net.sourceforge.fixagora.basis.client.view.component.MonthSpinner;
import net.sourceforge.fixagora.basis.client.view.component.NumberSpinner;
import net.sourceforge.fixagora.basis.client.view.component.NumberTextField;
import net.sourceforge.fixagora.basis.client.view.component.PlainTextField;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCondition;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;

/**
 * The Class SpreadSheetTableRenderer.
 */
public class SpreadSheetTableRenderer implements TableCellRenderer {

	private Color blue = new Color(204, 216, 255);
	
	private Color yellowDark = new Color(255, 236, 179);

	private CellUpdateHandler cellUpdateHandler = null;

	/**
	 * Instantiates a new spread sheet table renderer.
	 *
	 * @param cellUpdateHandler the cell update handler
	 */
	public SpreadSheetTableRenderer(CellUpdateHandler cellUpdateHandler) {

		super();
		this.cellUpdateHandler = cellUpdateHandler;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {
		
		SpreadSheetTableModel spreadSheetTableModel = (SpreadSheetTableModel) table.getModel();

		SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(table.convertRowIndexToModel(row),
				table.convertColumnIndexToModel(column));

		Color bright = new Color(255, 243, 204);

		Color dark = new Color(255, 243, 204);
		
		Color foreground = Color.BLACK;

		Color fadeOutBackgroundColor = null;
		
		SpreadSheetCell spreadSheetCell = (SpreadSheetCell) value;

		if (spreadSheetCell == null)
			spreadSheetCell = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(), null, null, table.convertRowIndexToModel(row),
					table.convertColumnIndexToModel(column));

		if (isSelected) {
			dark = new Color(179, 196, 255);

			bright = new Color(179, 196, 255);
		}
		else {
			if (spreadSheetCellFormat != null) {

				bright = new Color(spreadSheetCellFormat.getBackgroundRed(), spreadSheetCellFormat.getBackgroundGreen(),
						spreadSheetCellFormat.getBackgroundBlue());
				foreground = new Color(spreadSheetCellFormat.getForegroundRed(), spreadSheetCellFormat.getForegroundGreen(),
						spreadSheetCellFormat.getForegroundBlue());
				if (spreadSheetCellFormat.getGradientPaint())
					dark = bright.darker();
				else
					dark = bright;
			}
			if (spreadSheetCell.getCondition() > 0) {
				SpreadSheetConditionalFormat spreadSheetConditionalFormat = ((SpreadSheetTableModel) table.getModel()).getSpreadSheetConditionalFormatAt(
						table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
				if (spreadSheetConditionalFormat != null) {
					SpreadSheetCondition spreadSheetCondition = null;
					switch (spreadSheetCell.getCondition()) {
						case 1:
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition1();
							break;
						case 2:
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition2();
							break;
						case 3:
							spreadSheetCondition = spreadSheetConditionalFormat.getSpreadSheetCondition3();
							break;
					}
					if (spreadSheetCondition != null) {
						if (spreadSheetCondition.getFadeOut())
						{
							fadeOutBackgroundColor = new Color(spreadSheetCondition.getBackgroundRed(), spreadSheetCondition.getBackgroundGreen(),
									spreadSheetCondition.getBackgroundBlue());
						}
						else {
							bright = new Color(spreadSheetCondition.getBackgroundRed(), spreadSheetCondition.getBackgroundGreen(),
									spreadSheetCondition.getBackgroundBlue());
							dark = bright;
						}
						foreground = new Color(spreadSheetCondition.getForegroundRed(), spreadSheetCondition.getForegroundGreen(),
						spreadSheetCondition.getForegroundBlue());

					}
				}
			}
		}

		JLabel component = new GradientLabel(dark, bright);

		FieldInterface fieldInterface = null;

		if (spreadSheetCellFormat != null) {

			component.setForeground(foreground);
			
			Integer fractionalOffset = null;
			
			if(spreadSheetCellFormat.getFractionalDisplay()!=null&&spreadSheetCellFormat.getFractionalDisplay()==true)
				fractionalOffset = spreadSheetTableModel.getFractionalOffset(table.convertColumnIndexToModel(column));

			if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.NUMBER_SPINNER||(spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.SOLVER&&!spreadSheetTableModel.isShowingFormula())) {
				fieldInterface = new NumberSpinner(null, null, null, bright, fractionalOffset);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DATE_CHOOSER) {
				fieldInterface = new DateChooser(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DAY_SPINNER) {
				fieldInterface = new DaySpinner(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MONTH_SPINNER) {
				fieldInterface = new MonthSpinner(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.HOUR_SPINNER) {
				fieldInterface = new HourSpinner(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MINUTE_SPINNER) {
				fieldInterface = new MinuteSpinner(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.CHECKBOX) {
				fieldInterface = new CheckboxField(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.BUTTON) {
				fieldInterface = new ButtonField(null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PREDEFINED) {
				fieldInterface = new ComboBoxField(null, bright);
			}
			else if (spreadSheetCell.getValue() instanceof Double && spreadSheetCellFormat.getFractionalDisplay()!=null &&spreadSheetCellFormat.getFractionalDisplay()==true&&!spreadSheetTableModel.isShowingFormula()) {
				fieldInterface = new NumberTextField(null, bright, true, fractionalOffset);
			}
			else {
				fieldInterface = new PlainTextField(null, bright, null, getHyperlink(spreadSheetCell));
			}
			fieldInterface.setSpreadSheetCellFormat(spreadSheetCellFormat);
			
			if(spreadSheetCellFormat.getBoldFont()!=null)
				fieldInterface.setBold(spreadSheetCellFormat.getBoldFont());
			
		}
		else {
			fieldInterface = new PlainTextField(null, bright, null, getHyperlink(spreadSheetCell));
		}

		if(spreadSheetTableModel.isDropShadow(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column)))
		{
			fieldInterface.setBackground(yellowDark);
		}
		else if (fadeOutBackgroundColor!=null)
		{
			fieldInterface.setBackground(cellUpdateHandler.getFadeoutColor(bright,fadeOutBackgroundColor, spreadSheetCell));			
		}
		else
			fieldInterface.setBackground(bright);
		
		fieldInterface.setForeground(foreground);
		fieldInterface.setBorder(new MatteBorder(new Insets(0, 0, 1, 1), blue.darker()));
		fieldInterface.setHighLightColor(cellUpdateHandler.getHighlightColor(dark, spreadSheetCell));
		if(spreadSheetCell.getFormula()!=null&&spreadSheetCell.getFormula().trim().length()>0&&spreadSheetTableModel.isShowingFormula())
			fieldInterface.setValue("="+spreadSheetCell.getFormula().trim());
		else
			fieldInterface.setValue(spreadSheetCell.getValue());

		return (Component) fieldInterface;

	}
	
	private String getHyperlink(SpreadSheetCell spreadSheetCell)
	{

		if(spreadSheetCell==null)
			return null;
				
		if(spreadSheetCell.getFormula()==null)
			return null;
		
		String formula = spreadSheetCell.getFormula();
		
		if(formula.replaceAll("\\s+","").toUpperCase().contains("HYPERLINK("))
		{
			
			int index = formula.toUpperCase().indexOf("HYPERLINK");
			int startIndex = -1;
			int stopIndex = -1;
			
			while(index<formula.length()&&stopIndex==-1)
			{
				if(formula.charAt(index)=='\"')
				{
					if(startIndex==-1)
						startIndex=index;
					else
						stopIndex=index;
				}
				index++;
			}
			if(stopIndex==-1)
				return null;
			else
				return formula.substring(startIndex+1, stopIndex);
		}
		return null;
	}

}
