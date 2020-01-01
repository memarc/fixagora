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
package net.sourceforge.fixagora.buyside.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideQuotePageEditorDepthTableModel;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideQuotePageEntry;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideQuotePage;

/**
 * The Class QuotePageBidDepthTableRenderer.
 */
public class QuotePageBidDepthTableRenderer implements TableCellRenderer {

	private Color backGround1 = null;

	private Color backGround2 = null;

	private Color foreGroundDefault = null;

	private Color foreGroundGreen = null;

	private Color foreGroundRed = null;

	private boolean highlight = false;
	
	private String percentage = "";

	/**
	 * Instantiates a new quote page bid depth table renderer.
	 *
	 * @param buySideQuotePage the buy side quote page
	 */
	public QuotePageBidDepthTableRenderer(BuySideQuotePage buySideQuotePage) {

		super();
		
		if(buySideQuotePage.getParent() instanceof BuySideBook)
		{
			BuySideBook buySideBook = (BuySideBook)buySideQuotePage.getParent();
			if(!buySideBook.getAbsolutChange())
				percentage = "%";
		}

		switch (buySideQuotePage.getDisplayStyle()) {
			case 0:
				backGround1 = new Color(0, 0, 0);
				backGround2 = new Color(31, 31, 31);

				foreGroundDefault = new Color(255, 153, 0);
				foreGroundGreen = new Color(0, 204, 0);
				foreGroundRed = new Color(255, 51, 51);
				break;
			case 1:

				backGround1 = new Color(0, 22, 102);
				backGround2 = new Color(0, 28, 128);
				
				foreGroundDefault = new Color(250, 196, 0);
				foreGroundGreen = new Color(0, 224, 64);
				foreGroundRed = new Color(250, 117, 47);

				break;
			default:
				backGround1 = new Color(255, 243, 204);
				backGround2 = new Color(255, 236, 179);

				foreGroundDefault = Color.BLACK;
				foreGroundGreen = new Color(51, 153, 0);
				foreGroundRed = new Color(204, 0, 0);
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
		
		if(column>1&&!((BuySideQuotePageEditorDepthTableModel) table.getModel()).isShowYield())
			column++;

		if (((BuySideQuotePageEditorDepthTableModel) table.getModel()).getMouseOverRow() == row)
			borderColor = foreGroundDefault;
		
			JLabel component = new JLabel("-");

			component.setFont(new Font("Dialog", Font.PLAIN, 12));
			component.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0, borderColor), new EmptyBorder(4, 5, 4, 5)));

			if (column != 0)
				component.setHorizontalAlignment(SwingConstants.RIGHT);

			if (row % 2 == 0)
				component.setBackground(backGround1);

			else
				component.setBackground(backGround2);

			component.setForeground(foreGroundDefault);
			component.setOpaque(true);
			
			BuySideQuotePageEntry buySideQuotePageEntry = null;
			AbstractBuySideMDInputEntry buySideMDInputEntry = null;
			
			if(value instanceof BuySideQuotePageEntry)
			{
				buySideQuotePageEntry = (BuySideQuotePageEntry)value;
				buySideMDInputEntry = buySideQuotePageEntry.getBuySideMDInputEntry();
			}
			
			DollarFraction dollarFraction = null;
			
			if(buySideMDInputEntry!=null)
			{
				dollarFraction  = ((BuySideQuotePageEditorDepthTableModel)table.getModel()).getDollarFraction(buySideMDInputEntry.getSecurityValue());
			}

			Font font = new Font("Dialog", Font.PLAIN, 12);
			FontMetrics fontMetrics = table.getFontMetrics(font);
			
			switch (column) {
				case 0:
					if(value!=null)
						component.setText(value.toString());
					break;
				case 1:
					if (buySideMDInputEntry == null || buySideMDInputEntry.getMdEntryBidPxValue() == null)
						component.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							component.setText(dollarFraction.getDollarPrice(buySideMDInputEntry.getMdEntryBidPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(buySideMDInputEntry.getMdEntryBidPxValue(),true));
							component.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0, borderColor),new EmptyBorder(4, 5, 4, 5+buySideQuotePageEntry.getDollarFracOffset()-fracWidth)));
						}
						catch (Exception e) {
							component.setText(buySideQuotePageEntry.getPriceDecimalFormat().format(buySideMDInputEntry.getMdEntryBidPxValue()));
						}
					}
					else
						component.setText(buySideQuotePageEntry.getPriceDecimalFormat().format(buySideMDInputEntry.getMdEntryBidPxValue()));

					if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBidPriceUp() != null) {

						if (buySideQuotePageEntry.getBidPriceUp())
							component.setForeground(getHighlightColor(foreGroundGreen, buySideQuotePageEntry.getBidPriceUpdate()));

						else
							component.setForeground(getHighlightColor(foreGroundRed, buySideQuotePageEntry.getBidPriceUpdate()));
					}

					break;

				case 2:
					if (buySideMDInputEntry == null || buySideMDInputEntry.getMdEntryBidYieldValue() == null)
						component.setText("-");
					else
						component.setText(buySideQuotePageEntry.getYieldDecimalFormat().format(buySideMDInputEntry.getMdEntryBidYieldValue()));

					break;

				case 3:
					if (buySideMDInputEntry == null || buySideMDInputEntry.getMdEntryBidSizeValue() == null)
						component.setText("-");
					else
						component.setText(buySideQuotePageEntry.getSizeDecimalFormat().format(buySideMDInputEntry.getMdEntryBidSizeValue()));

					break;

				case 4:
					if (buySideMDInputEntry == null || buySideMDInputEntry.getMdBidPriceDeltaValue() == null)
						component.setText("-");
					else if(dollarFraction!=null && percentage.length()==0)
					{
						
						double delta = buySideMDInputEntry.getMdBidPriceDeltaValue();
						
						try {
							
							if (delta > 0) {
								component.setForeground(foreGroundGreen);
								component.setText("+" + dollarFraction.getDollarPrice(delta,0,true));
							}
							else {
								if (delta < 0)
									component.setForeground(foreGroundRed);
								component.setText(dollarFraction.getDollarPrice(delta,0,true));
							}
							
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(delta,true));
							component.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0, borderColor),new EmptyBorder(4, 5, 4, 5+buySideQuotePageEntry.getChangeFracOffset()-fracWidth)));
						}
						catch (Exception e) {
							
							if (delta > 0) {
								component.setForeground(foreGroundGreen);
								component.setText("+" + buySideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
							else {
								if (delta < 0)
									component.setForeground(foreGroundRed);
								component.setText(buySideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
						}
					}
					else {
						double delta = buySideMDInputEntry.getMdBidPriceDeltaValue();
						if (delta > 0) {
							component.setForeground(foreGroundGreen);
							component.setText("+" + buySideQuotePageEntry.getPriceDecimalFormat().format(delta)+percentage);
						}
						else {
							if (delta < 0)
								component.setForeground(foreGroundRed);
							component.setText(buySideQuotePageEntry.getPriceDecimalFormat().format(delta)+percentage);
						}
					}

					break;
				case 5:
					if(value!=null)
						component.setText(value.toString());
					break;
			}
			
			
			return component;

	}

	/**
	 * Switch highlight.
	 */
	public void switchHighlight() {

		highlight = !highlight;
	}

	private Color getHighlightColor(Color color, long updateTime) {

		long diff = System.currentTimeMillis() - updateTime;

		if (diff > 1000 || highlight)
			return color;
		return foreGroundDefault;

	}

}
