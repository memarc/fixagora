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
package net.sourceforge.fixagora.sellside.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideBookEditorMonitorTableModel;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;

/**
 * The Class SellSideBookTableRenderer.
 */
public class SellSideBookTableRenderer implements TableCellRenderer {

	private Color backGround1 = null;

	private Color backGround2 = null;

	private Color foreGroundDefault = null;

	private Color green = null;

	private Color red = null;

	private SellSideBook sellSideBook = null;

	/**
	 * Instantiates a new sell side book table renderer.
	 *
	 * @param sellSideBook the sell side book
	 */
	public SellSideBookTableRenderer(SellSideBook sellSideBook) {

		super();
		
		this.sellSideBook  = sellSideBook;

		switch (sellSideBook.getDisplayStyle()) {
			case 0:
				backGround1 = new Color(0, 0, 0);
				backGround2 = new Color(31, 31, 31);

				foreGroundDefault = new Color(255, 153, 0);
				green = new Color(0, 204, 0);
				red = new Color(255, 51, 51);
				break;
			case 1:

				backGround1 = new Color(0, 22, 102);
				backGround2 = new Color(0, 28, 128);

				foreGroundDefault = new Color(250, 196, 0);	 
				green = new Color(0, 224, 64);  
				red = new Color(250, 117, 47);
				

				break;
			default:
				backGround1 = new Color(255, 243, 204);
				backGround2 = new Color(255, 236, 179);

				foreGroundDefault = Color.BLACK;
				green = new Color(51, 153, 0);
				red = new Color(204, 0, 0);
				break;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			int column) {

		Color borderColor = backGround1;

		if (row % 2 != 0)
			borderColor = backGround2;
		
		SellSideBookEditorMonitorTableModel sellSideBookEditorMonitorTableModel = (SellSideBookEditorMonitorTableModel) table.getModel();	

		if(column>5&&!sellSideBookEditorMonitorTableModel.isShowYield())
			column++;

		if (sellSideBookEditorMonitorTableModel.getMouseOverRow()!=-1&&sellSideBookEditorMonitorTableModel.getMouseOverRow() == table.convertRowIndexToModel(row)&&column!=0)
			borderColor = foreGroundDefault;

		GradientLabel component = new GradientLabel();

		component.setFont(new Font("Dialog", Font.PLAIN, 12));
		
		int border = 1;
		int border2 = 0;
		
		if(column==0)
		{
			border = 0;
			border2 = 1;
		}
		
		component.setBorder(new CompoundBorder(new MatteBorder(border, 0, border, border2, borderColor), new EmptyBorder(4, 5, 4, 5)));
		
		if (value != null)
		{
			component.setText(value.toString());
			if(column==4)
			{
				component.setBorder(new CompoundBorder(new MatteBorder(border, 0, border, border2, borderColor), new EmptyBorder(4, 5, 4, 5+sellSideBookEditorMonitorTableModel.getLimitOffset(value.toString()))));
			}
			if(column==5)
			{
				component.setBorder(new CompoundBorder(new MatteBorder(border, 0, border, border2, borderColor), new EmptyBorder(4, 5, 4, 5+sellSideBookEditorMonitorTableModel.getLastOffset(value.toString()))));
			}
		}


		if ((column > 3 && column < 10) || column > 11)
			component.setHorizontalAlignment(SwingConstants.RIGHT);

		if (row % 2 == 0)
		{
			component.setBright(backGround1);
			component.setDark(backGround1);
		}
		else
		{
			component.setBright(backGround2);
			component.setDark(backGround2);
		}
		
		component.setForeground(foreGroundDefault);

		if (column == 0 && value instanceof String) {
			
			Color backGround = new Color(255, 202, 10);
			
			if(sellSideBook.getDisplayStyle()==1)
				backGround = new Color(250, 196, 0);
			
			component.setForeground(Color.WHITE);
			
			if(sellSideBookEditorMonitorTableModel.isCompleteFilled(table.convertRowIndexToModel(row)))
			{
				backGround = new Color(0, 153, 0);
				
				if(sellSideBook.getDisplayStyle()==1)
					backGround = new Color(0, 128, 36);
			}
			else if(sellSideBookEditorMonitorTableModel.isCanceled(table.convertRowIndexToModel(row)))
			{
				backGround = new Color(204, 0, 0);
				
			}
			else
			{
				component.setForeground(Color.BLACK);
			}
			
			if(sellSideBookEditorMonitorTableModel.isUpdated(table.convertRowIndexToModel(row)))
				backGround = getHighlightColor(backGround);
			
			component.setBright(backGround);
			component.setDark(backGround.darker());
			
		}
		
		if (column == 3 && value instanceof String) {
			if(((String)value).startsWith("Ask"))
				component.setForeground(red);
			else
				component.setForeground(green);
				
		}


		component.setOpaque(true);
		return component;

	}

	private Color getHighlightColor(Color color) {		
		
		long time = System.currentTimeMillis()/100;
		
		int shift = (int)((Math.sin(time)+1)*15);

		int red = color.getRed()-shift;
		
		int green = color.getGreen()-shift;
		
		int blue = color.getBlue()-shift;
		
		if(red<0)
			red=0;
		
		if(green<0)
			green=0;
		
		if(blue<0)
			blue=0;
				
		return new Color(red,green,blue);

	}

}
