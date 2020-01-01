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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellEditor;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetTableModel;
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
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SolverRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SolverResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;

/**
 * The Class SpreadSheetCellEditor.
 */
public class SpreadSheetCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;

	private FieldInterface fieldInterface = null;

	private SpreadSheetTableModel spreadSheetTableModel = null;

	private BasisClientConnector basisClientConnector = null;

	private SpreadSheetCell spreadSheetCell = null;

	private Color blue = new Color(204, 216, 255);

	private SpreadSheetEditorSheet spreadSheetEditorSheet = null;

	private SpreadSheetCellFormat spreadSheetCellFormat = null;
	
	private Object originalValue = null;
	
	private Object originalFormula = null;

	
	/**
	 * Instantiates a new spread sheet cell editor.
	 *
	 * @param spreadSheetTableModel the spread sheet table model
	 * @param spreadSheetEditorSheet the spread sheet editor sheet
	 * @param basisClientConnector the basis client connector
	 */
	public SpreadSheetCellEditor(SpreadSheetTableModel spreadSheetTableModel, final SpreadSheetEditorSheet spreadSheetEditorSheet,
			BasisClientConnector basisClientConnector) {

		super();
		this.spreadSheetTableModel = spreadSheetTableModel;
		this.spreadSheetEditorSheet = spreadSheetEditorSheet;
		this.basisClientConnector = basisClientConnector;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int rowIndex, final int vColIndex) {
		
		spreadSheetCell = (SpreadSheetCell)value;
		spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(table.convertRowIndexToModel(rowIndex), table.convertColumnIndexToModel(vColIndex));
		spreadSheetEditorSheet.setCellEditor(this);

		if (spreadSheetCell == null) {
			spreadSheetCell = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(), null, null, table.convertRowIndexToModel(rowIndex), table.convertColumnIndexToModel(vColIndex) );
		}
		originalValue = spreadSheetCell.getValue();
		originalFormula = spreadSheetCell.getFormula();
		Color bright = new Color(255, 243, 204);
		Color foreground = Color.BLACK;

		if (spreadSheetCellFormat != null) {
			
			foreground = new Color(spreadSheetCellFormat.getForegroundRed(), spreadSheetCellFormat.getForegroundGreen(),
					spreadSheetCellFormat.getForegroundBlue());
			
			bright = new Color(spreadSheetCellFormat.getBackgroundRed(), spreadSheetCellFormat.getBackgroundGreen(), spreadSheetCellFormat.getBackgroundBlue());

			if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.NUMBER_SPINNER||(spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.SOLVER&&!spreadSheetTableModel.isShowingFormula())) {
				fieldInterface = new NumberSpinner(this, null, null, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DATE_CHOOSER) {
				fieldInterface = new DateChooser(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DAY_SPINNER) {
				fieldInterface = new DaySpinner(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MONTH_SPINNER) {
				fieldInterface = new MonthSpinner(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.HOUR_SPINNER) {
				fieldInterface = new HourSpinner(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MINUTE_SPINNER) {
				fieldInterface = new MinuteSpinner(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.CHECKBOX) {
				fieldInterface = new CheckboxField(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.BUTTON) {
				fieldInterface = new ButtonField(this, bright);
			}
			else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PREDEFINED) {
				fieldInterface = new ComboBoxField(this, bright);
			}
			else if (spreadSheetCell.getFormula()==null& spreadSheetCell.getValue() instanceof Double && spreadSheetCellFormat.getFractionalDisplay()!=null &&spreadSheetCellFormat.getFractionalDisplay()==true&&!spreadSheetTableModel.isShowingFormula()) {
				fieldInterface = new NumberTextField(this, bright, true);
			}
			else {
				
				fieldInterface = new PlainTextField(this, bright, spreadSheetEditorSheet, getHyperlink(spreadSheetCell));
			}
			fieldInterface.setSpreadSheetCellFormat(spreadSheetCellFormat);
			
			if(spreadSheetCellFormat.getBoldFont()!=null)
				fieldInterface.setBold(spreadSheetCellFormat.getBoldFont());
		}
		else {
			fieldInterface = new PlainTextField(this, bright, spreadSheetEditorSheet, getHyperlink(spreadSheetCell));
		}
		
		fieldInterface.setForeground(foreground);
		fieldInterface.setBorder(new MatteBorder(new Insets(0, 0, 1, 1), blue.darker()));
		fieldInterface.setHighLightColor(blue.darker());
		if(spreadSheetCell.getFormula()!=null)
		{
			if (spreadSheetCellFormat != null&&spreadSheetCellFormat.getSpreadSheetFormatType()==SpreadSheetFormatType.SOLVER&&!spreadSheetTableModel.isShowingFormula())
				fieldInterface.setValue(spreadSheetCell.getValue());
			else
				fieldInterface.setValue("="+spreadSheetCell.getFormula());
		}
		else
			fieldInterface.setValue(spreadSheetCell.getValue());
		((Component)fieldInterface).addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			
				
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			
				if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
				{
					stopCellEditing();
					table.getSelectionModel().setAnchorSelectionIndex(rowIndex);
					table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(vColIndex);
					table.changeSelection(rowIndex, vColIndex, false, false);
				}
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			
				// TODO Auto-generated method stub
				
			}
		});
		return (Component) fieldInterface;
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractCellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable( EventObject eventObject )
	{
		
		if(eventObject instanceof KeyEvent)
		{
			KeyEvent e = (KeyEvent)eventObject;
		if(e.getKeyCode()==KeyEvent.VK_C&&e.getModifiers()==ActionEvent.CTRL_MASK)
			return false;
		if(e.getKeyCode()==KeyEvent.VK_X&&e.getModifiers()==ActionEvent.CTRL_MASK)
			return false;
		if(e.getKeyCode()==KeyEvent.VK_V&&e.getModifiers()==ActionEvent.CTRL_MASK)
			return false;
		if(e.getKeyCode()==KeyEvent.VK_D&&e.getModifiers()==ActionEvent.CTRL_MASK)
			return false;
		if(e.getKeyCode()==KeyEvent.VK_NUMBER_SIGN&&e.getModifiers()==ActionEvent.CTRL_MASK)
			return false;
		}

		
		return super.isCellEditable(eventObject);
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		
		if(fieldInterface==null)
			return null;
		
		if(spreadSheetCellFormat != null && spreadSheetCellFormat.getSpreadSheetFormatType()==SpreadSheetFormatType.SOLVER)
		{		

			boolean valueChanged = true;
			
			if(!(fieldInterface.getValue() instanceof Number))
				valueChanged = false;
			
			else if(originalValue!=null&&originalValue.equals(fieldInterface.getValue()))
				valueChanged = false;
			
			if(valueChanged)
			{
				SolverResponse solverResponse = (SolverResponse)basisClientConnector.sendRequest(new SolverRequest(spreadSheetCell, ((Number)fieldInterface.getValue()
						).doubleValue(), RequestIDManager.getInstance().getID()));
				
				if(!solverResponse.isSolved())
					spreadSheetEditorSheet.getCellUpdateHandler().addFailedSheetCell(spreadSheetCell);
				else
					spreadSheetCell.setValue(solverResponse.getValue());
			}
			
			return spreadSheetCell;
		}

		if (spreadSheetCellFormat != null && spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.PLAIN
				&& spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.PLAIN_DATE
				&& spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.PLAIN_TIME
				&& spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.TMP_DATE
				&& spreadSheetCellFormat.getSpreadSheetFormatType() != SpreadSheetFormatType.TMP_TIME) {
			spreadSheetCell.setValue(fieldInterface.getValue());
			spreadSheetCell.setFormula(null);
		}
		else {
			if (fieldInterface.getValue() instanceof String) {
				String text = (String) fieldInterface.getValue();
				if (text.trim().startsWith("=")) {
					spreadSheetCell.setFormula(text.trim().substring(1));
					spreadSheetCell.setValue(null);
				}
				else {
					spreadSheetCell.setFormula(null);
					if(text.length()==0)
						spreadSheetCell.setValue(null);
					else
						spreadSheetCell.setValue(text.trim());
				}
			}
			else {
				spreadSheetCell.setFormula(null);
				spreadSheetCell.setValue(fieldInterface.getValue());
			}
		}
		
		boolean valueChanged = true;
		if(originalValue==null&&spreadSheetCell.getValue()==null)
			valueChanged = false;
		else if(originalValue!=null&&originalValue.equals(spreadSheetCell.getValue()))
			valueChanged = false;
		
		boolean formulaChanged = true;
		if(originalFormula==null&&spreadSheetCell.getFormula()==null)
			formulaChanged = false;
		else if(originalFormula!=null&&originalFormula.equals(spreadSheetCell.getFormula()))
			formulaChanged = false;
		if(formulaChanged||valueChanged)
			basisClientConnector.sendRequest(new ModifySheetCellRequest(spreadSheetCell, RequestIDManager.getInstance().getID()));
		return spreadSheetCell;
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractCellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {
		spreadSheetEditorSheet.setCellEditor(null);
		getCellEditorValue();
		cancelCellEditing();
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractCellEditor#cancelCellEditing()
	 */
	@Override
	public void cancelCellEditing() {
		spreadSheetEditorSheet.setCellEditor(null);
		super.cancelCellEditing();
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {

		if (fieldInterface instanceof PlainTextField)
			((PlainTextField) fieldInterface).setExternalValue(text);
	}

	
	/**
	 * Gets the spread sheet cell format.
	 *
	 * @return the spread sheet cell format
	 */
	public SpreadSheetCellFormat getSpreadSheetCellFormat() {
	
		return spreadSheetCellFormat;
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