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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideQuotePageEditorMonitorTableModel;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideQuotePageEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage;

/**
 * The Class QuotePageMonitorTableRenderer.
 */
public class QuotePageMonitorTableRenderer implements TableCellRenderer {

	private Color backGround1 = null;

	private Color backGround2 = null;

	private Color foreGroundDefault = null;

	private Color foreGroundGreen = null;

	private Color foreGroundRed = null;

	private boolean highlight = false;
	
	private String percentage = "";
	
	private DecimalFormat percentageDecimalFormat = new DecimalFormat("###0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	/**
	 * Instantiates a new quote page monitor table renderer.
	 *
	 * @param sellSideQuotePage the sell side quote page
	 */
	public QuotePageMonitorTableRenderer(SellSideQuotePage sellSideQuotePage) {

		super();
		
		if(sellSideQuotePage.getParent() instanceof SellSideBook)
		{
			SellSideBook sellSideBook = (SellSideBook)sellSideQuotePage.getParent();
			if(!sellSideBook.getAbsolutChange())
				percentage = "%";
		}

		switch (sellSideQuotePage.getDisplayStyle()) {
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

		if (((SellSideQuotePageEditorMonitorTableModel) table.getModel()).getMouseOverRow() == row)
			borderColor = foreGroundDefault;
		
		if(column>1&&!((SellSideQuotePageEditorMonitorTableModel) table.getModel()).isShowYield())
			column++;

		if (column == 0 || column == 7) {
			JLabel component = new JLabel(value.toString());

			component.setFont(new Font("Dialog", Font.PLAIN, 12));
			component.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0, borderColor), new EmptyBorder(4, 5, 4, 5)));

			if (column == 7)
				component.setHorizontalAlignment(SwingConstants.RIGHT);

			if (row % 2 == 0)
				component.setBackground(backGround1);

			else
				component.setBackground(backGround2);

			component.setForeground(foreGroundDefault);
			component.setOpaque(true);
			return component;
		}
		else {
			JPanel jPanel = new JPanel();
			jPanel.setBorder(new MatteBorder(1, 0, 1, 0, borderColor));
			GridBagLayout gridBagLayout = new GridBagLayout();
			jPanel.setLayout(gridBagLayout);

			if (row % 2 == 0)
				jPanel.setBackground(backGround1);

			else
				jPanel.setBackground(backGround2);

			jPanel.setOpaque(true);

			JLabel leftLabel = new JLabel();
			leftLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			leftLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			leftLabel.setBorder(new EmptyBorder(4, 5, 4, 5));

			GridBagConstraints gbc_leftLabel = new GridBagConstraints();
			gbc_leftLabel.gridx = 1;
			gbc_leftLabel.gridy = 0;
			gbc_leftLabel.fill = GridBagConstraints.BOTH;
			jPanel.add(leftLabel, gbc_leftLabel);

			JLabel dividerLabel = new JLabel("/");
			dividerLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			dividerLabel.setHorizontalAlignment(SwingConstants.CENTER);

			dividerLabel.setBorder(new EmptyBorder(4, 5, 4, 5));

			GridBagConstraints gbc_dividerLabel = new GridBagConstraints();
			gbc_dividerLabel.gridx = 2;
			gbc_dividerLabel.gridy = 0;
			gbc_dividerLabel.weightx = 0.0;

			jPanel.add(dividerLabel, gbc_dividerLabel);

			JLabel rightLabel = new JLabel();
			rightLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			rightLabel.setBorder(new EmptyBorder(4, 5, 4, 5));

			GridBagConstraints gbc_rightLabel = new GridBagConstraints();
			gbc_rightLabel.gridx = 3;
			gbc_rightLabel.gridy = 0;

			jPanel.add(rightLabel, gbc_rightLabel);

			SellSideMDInputEntry sellSideMDInputEntry = null;
			SellSideQuotePageEntry sellSideQuotePageEntry = null;
			DollarFraction dollarFraction = null;

			leftLabel.setForeground(foreGroundDefault);
			rightLabel.setForeground(foreGroundDefault);
			dividerLabel.setForeground(foreGroundDefault);

			if (value instanceof SellSideQuotePageEntry) {
				sellSideQuotePageEntry = (SellSideQuotePageEntry) value;
				sellSideMDInputEntry = sellSideQuotePageEntry.getSellSideMDInputEntry();
			}

			if(sellSideMDInputEntry!=null)
			{
				dollarFraction = ((SellSideQuotePageEditorMonitorTableModel)table.getModel()).getDollarFraction(sellSideMDInputEntry.getSecurityValue());
			}
			
			Font font = new Font("Dialog", Font.PLAIN, 12);
			FontMetrics fontMetrics = table.getFontMetrics(font);
			
			switch (column) {
				case 1:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryBidPxValue() == null)
						leftLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							leftLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdEntryBidPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdEntryBidPxValue(),true));
							leftLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getDollarFracOffset()-fracWidth));
						}
						catch (Exception e) {
							leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getMdEntryBidPxValue()));
						}
					}
					else
						leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getMdEntryBidPxValue()));

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryAskPxValue() == null)
						rightLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							rightLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getMdEntryAskPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getMdEntryAskPxValue(),true));
							rightLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getDollarFracOffset()-fracWidth));
						}
						catch (Exception e) {
							rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getMdEntryAskPxValue()));
						}
					}
					else
						rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getMdEntryAskPxValue()));

					if (sellSideQuotePageEntry != null && sellSideQuotePageEntry.getBidPriceUp() != null) {

						if (sellSideQuotePageEntry.getBidPriceUp())
							leftLabel.setForeground(getHighlightColor(foreGroundGreen, sellSideQuotePageEntry.getBidPriceUpdate()));

						else
							leftLabel.setForeground(getHighlightColor(foreGroundRed, sellSideQuotePageEntry.getBidPriceUpdate()));
					}

					if (sellSideQuotePageEntry != null && sellSideQuotePageEntry.getAskPriceUp() != null) {
						if (sellSideQuotePageEntry.getAskPriceUp())
							rightLabel.setForeground(getHighlightColor(foreGroundGreen, sellSideQuotePageEntry.getAskPriceUpdate()));

						else
							rightLabel.setForeground(getHighlightColor(foreGroundRed, sellSideQuotePageEntry.getAskPriceUpdate()));
					}

					if (sellSideQuotePageEntry != null) {
						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getPriceWidth(), 25));
					}

					break;

				case 2:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryBidYieldValue() == null || sellSideMDInputEntry.getMdEntryBidYieldValue().isInfinite()|| sellSideMDInputEntry.getMdEntryBidYieldValue().isNaN())
						leftLabel.setText("-");
					else
						leftLabel.setText(sellSideQuotePageEntry.getYieldDecimalFormat().format(sellSideMDInputEntry.getMdEntryBidYieldValue()));

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryAskYieldValue() == null|| sellSideMDInputEntry.getMdEntryAskYieldValue().isInfinite()|| sellSideMDInputEntry.getMdEntryAskYieldValue().isNaN())
						rightLabel.setText("-");
					else
						rightLabel.setText(sellSideQuotePageEntry.getYieldDecimalFormat().format(sellSideMDInputEntry.getMdEntryAskYieldValue()));

					if (sellSideQuotePageEntry != null) {

						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getYieldWidth(), 25));

					}

					break;

				case 3:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryBidSizeValue() == null)
						leftLabel.setText("-");
					else
						leftLabel.setText(sellSideQuotePageEntry.getSizeDecimalFormat().format(sellSideMDInputEntry.getMdEntryBidSizeValue()));

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdEntryAskSizeValue() == null)
						rightLabel.setText("-");
					else
						rightLabel.setText(sellSideQuotePageEntry.getSizeDecimalFormat().format(sellSideMDInputEntry.getMdEntryAskSizeValue()));

					if (sellSideQuotePageEntry != null) {

						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getSizeWidth(), 25));

					}

					break;

				case 4:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdBidPriceDeltaValue() == null)
						leftLabel.setText("-");
					else if(dollarFraction!=null && percentage.length()==0)
					{
						
						double delta = sellSideMDInputEntry.getMdBidPriceDeltaValue();
						
						try {
							
							if (delta > 0) {
								leftLabel.setForeground(foreGroundGreen);
								leftLabel.setText("+" + dollarFraction.getDollarPrice(delta,0,true));
															}
							else {
								if (delta < 0)
									leftLabel.setForeground(foreGroundRed);
								leftLabel.setText(dollarFraction.getDollarPrice(delta,0,true));
							}
							
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(delta,true));
							leftLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getDollarFracOffset()-fracWidth));
						}
						catch (Exception e) {
							
							if (delta > 0) {
								leftLabel.setForeground(foreGroundGreen);
								leftLabel.setText("+" + sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
							else {
								if (delta < 0)
									leftLabel.setForeground(foreGroundRed);
								leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
						}
					}
					else {
						double delta = sellSideMDInputEntry.getMdBidPriceDeltaValue();
						if (delta > 0) {
							leftLabel.setForeground(foreGroundGreen);
							if(percentage.length()>0)
								leftLabel.setText("+" + percentageDecimalFormat.format(delta)+percentage);
							else
								leftLabel.setText("+" + sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
						}
						else {
							if (delta < 0)
								leftLabel.setForeground(foreGroundRed);
							if(percentage.length()>0)
								leftLabel.setText(percentageDecimalFormat.format(delta)+percentage);
							else
								leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
						}
					}

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getMdAskPriceDeltaValue() == null)
						rightLabel.setText("-");
					else if(dollarFraction!=null && percentage.length()==0)
					{
						
						double delta = sellSideMDInputEntry.getMdAskPriceDeltaValue();
						
						try {
							
							if (delta > 0) {
								rightLabel.setForeground(foreGroundGreen);
								rightLabel.setText("+" + dollarFraction.getDollarPrice(delta,0,true));
								
							}
							else {
								if (delta < 0)
									rightLabel.setForeground(foreGroundRed);
								rightLabel.setText(dollarFraction.getDollarPrice(delta,0,true));
							}
							
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(delta,true));
							rightLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getDollarFracOffset()-fracWidth));
						}
						catch (Exception e) {
							
							if (delta > 0) {
								rightLabel.setForeground(foreGroundGreen);
								rightLabel.setText("+" + sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
							else {
								if (delta < 0)
									rightLabel.setForeground(foreGroundRed);
								rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
							}
						}
					}
					else {
						double delta = sellSideMDInputEntry.getMdAskPriceDeltaValue();
						if (delta > 0) {
							rightLabel.setForeground(foreGroundGreen);
							if(percentage.length()>0)
								rightLabel.setText("+" + percentageDecimalFormat.format(delta)+percentage);
							else
								rightLabel.setText("+" + sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
						}
						else {
							if (delta < 0)
								rightLabel.setForeground(foreGroundRed);
							if(percentage.length()>0)
								rightLabel.setText(percentageDecimalFormat.format(delta)+percentage);
							else
								rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(delta));
						}
					}

					if (sellSideQuotePageEntry != null) {

						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getChangeWidth(), 25));

					}

					break;

				case 5:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getHighBidPxValue() == null|| sellSideMDInputEntry.getHighBidPxValue().isNaN() || sellSideMDInputEntry.getHighBidPxValue().isInfinite())
						leftLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							leftLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getHighBidPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getHighBidPxValue(),true));
							leftLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getHighFracOffset()-fracWidth));
						}
						catch (Exception e) {
							leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getHighBidPxValue()));
						}
					}
					else
						leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getHighBidPxValue()));

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getHighAskPxValue() == null)
						rightLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							rightLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getHighAskPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getHighAskPxValue(),true));
							rightLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getHighFracOffset()-fracWidth));
						}
						catch (Exception e) {
							rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getHighAskPxValue()));
						}
					}
					else
						rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getHighAskPxValue()));

					if (sellSideQuotePageEntry != null) {

						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getHighWidth(), 25));

					}

					break;

				case 6:
					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getLowBidPxValue() == null)
						leftLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							leftLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getLowBidPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getLowBidPxValue(),true));
							leftLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getLowFracOffset()-fracWidth));
						}
						catch (Exception e) {
							leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getLowBidPxValue()));
						}
					}
					else
						leftLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getLowBidPxValue()));

					if (sellSideMDInputEntry == null || sellSideMDInputEntry.getLowAskPxValue() == null)
						rightLabel.setText("-");
					else if(dollarFraction!=null)
					{
						try {
							rightLabel.setText(dollarFraction.getDollarPrice(sellSideMDInputEntry.getLowAskPxValue(),1,true));
							int fracWidth = fontMetrics.stringWidth(dollarFraction.getDollarPriceFraction(sellSideMDInputEntry.getLowAskPxValue(),true));
							rightLabel.setBorder(new EmptyBorder(4, 5, 4, 5+sellSideQuotePageEntry.getLowFracOffset()-fracWidth));
						}
						catch (Exception e) {
							rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getLowAskPxValue()));
						}
					}
					else
						rightLabel.setText(sellSideQuotePageEntry.getPriceDecimalFormat().format(sellSideMDInputEntry.getLowAskPxValue()));

					if (sellSideQuotePageEntry != null) {

						leftLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));
						leftLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));
						leftLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));

						rightLabel.setMaximumSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));
						rightLabel.setMinimumSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));
						rightLabel.setPreferredSize(new Dimension(sellSideQuotePageEntry.getLowWidth(), 25));

					}

					break;
			}

			return jPanel;
		}

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
