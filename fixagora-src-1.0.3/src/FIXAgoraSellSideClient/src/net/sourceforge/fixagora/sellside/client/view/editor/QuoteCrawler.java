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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import net.sourceforge.fixagora.basis.shared.control.DollarFraction;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;

import org.apache.log4j.Logger;

/**
 * The Class QuoteCrawler.
 */
public class QuoteCrawler extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Long, FSecurity> securityMap = new HashMap<Long, FSecurity>();

	private DecimalFormat decimalFormat;

	private int crawlershift = 0;

	private Color orange = new Color(255, 153, 0);

	private Color green = new Color(0, 204, 0);

	private Color red = new Color(255, 51, 51);

	private BufferedImage originalImage = null;

	private SellSideBookEditor sellSideBookEditor = null;

	private Map<Long, SellSideMDInputEntry> sellSideMDInputEntryMap = new HashMap<Long, SellSideMDInputEntry>();

	private int quoteWidth = 0;

	private int changeWidth = 0;

	private boolean crawl = false;

	private static Logger log = Logger.getLogger(QuoteCrawler.class);

	private Set<Long> securitySet = new HashSet<Long>();

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	/**
	 * Instantiates a new quote crawler.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public QuoteCrawler(SellSideBookEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		int fractionDigits = 3;

		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBookEditor.getSellSideBook().getAssignedSellSideBookSecurities()) {

			FSecurity security = (FSecurity) sellSideBookEditor.getMainPanel().getAbstractBusinessObjectForId(
					assignedSellSideBookSecurity.getSecurity().getId());

			if (security != null) {

				securitySet.add(security.getId());

				Boolean fractionalDisplay = assignedSellSideBookSecurity.getFractionalDisplay();
				if (fractionalDisplay == null)
					fractionalDisplay = sellSideBookEditor.getSellSideBook().getFractionalDisplay();

				if (fractionalDisplay == null || fractionalDisplay == false) {

					if (security.getSecurityDetails().getMinPriceIncrement() != null && !(security.getSecurityDetails().getPriceQuoteMethod()!=null&&security.getSecurityDetails().getPriceQuoteMethod().equals("INT"))) {
						int digits = getDecimalPlaces(security.getSecurityDetails().getMinPriceIncrement());
						if (digits > fractionDigits)
							fractionDigits = digits;
					}
				}
				else {

					Double dollarTick = null;

					if (security.getSecurityDetails() != null) {
						dollarTick = security.getSecurityDetails().getMinPriceIncrement();
					}

					if (dollarTick == null)
						dollarTick = 0.0009765625;

					dollarTickMap.put(assignedSellSideBookSecurity.getSecurity().getId(), dollarTick);
				}
			}
		}

		StringBuffer stringBuffer = new StringBuffer("#,##0.");
		for (int i = 0; i < fractionDigits; i++)
			stringBuffer.append("0");

		decimalFormat = new DecimalFormat(stringBuffer.toString());
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {

		if (!crawl)
			return;

		draw(g);
	}

	private void draw(Graphics g) {

		if (g == null || !isShowing())
			return;

		crawlershift = crawlershift + 1;

		if (originalImage == null || crawlershift > originalImage.getWidth())
			crawlershift = 0;

		if (originalImage != null) {
			int width = originalImage.getWidth() - crawlershift;
			if (width > getWidth())
				width = getWidth();
			g.drawImage(originalImage, 0, 0, width, getHeight(), crawlershift, 0, crawlershift + width, getHeight(), this);
			int offset = width;
			while (offset < getWidth()) {
				g.drawImage(originalImage, offset, 0, offset + originalImage.getWidth(), getHeight(), 0, 0, originalImage.getWidth(), getHeight(), this);
				offset = offset + originalImage.getWidth();
			}
		}
	}

	private int getDecimalPlaces(double value) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0#########");
		BigDecimal bigDecimal = new BigDecimal(decimalFormat.format(value));
		return bigDecimal.scale();
	}

	/**
	 * Crawl.
	 */
	public void crawl() {

		if (originalImage == null)
			initOriginalImage();

		if (originalImage != null) {

			crawl = true;
			paintImmediately(0, 0, getWidth(), getHeight());
			crawl = false;

		}
	}

	private void initOriginalImage() {

		try {
			if (getHeight() > 0) {

				int width = 0;

				FontMetrics metrics = getFontMetrics(new Font("Dialog", Font.PLAIN, 12));

				List<SellSideMDInputEntry> sellSideCompositeMDInputEntries = new ArrayList<SellSideMDInputEntry>();
				sellSideCompositeMDInputEntries.addAll(sellSideMDInputEntryMap.values());

				int securityCount = 0;

				for (SellSideMDInputEntry sellSideQuotePageEntry : sellSideCompositeMDInputEntries) {

					FSecurity security = securityMap.get(sellSideQuotePageEntry.getSecurityValue());
					if (security == null) {
						security = sellSideBookEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(sellSideQuotePageEntry.getSecurityValue());
						securityMap.put(sellSideQuotePageEntry.getSecurityValue(), security);
					}
					if (security != null) {

						securityCount++;

						width = width + metrics.stringWidth(security.getName()) + 10;

						String bidValue = "-";

						if (sellSideQuotePageEntry.getMdEntryBidPxValue() != null) {

							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									bidValue = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdEntryBidPxValue(),1, true);
								}
								catch (Exception e) {
									bidValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryBidPxValue());
								}
							}
							else
								bidValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryBidPxValue());
						}

						int bidWidth = metrics.stringWidth(bidValue);

						if (quoteWidth < bidWidth)
							quoteWidth = bidWidth;

						String askValue = "-";

						if (sellSideQuotePageEntry.getMdEntryAskPxValue() != null) {

							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									askValue = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdEntryAskPxValue(),1, true);
								}
								catch (Exception e) {
									askValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryAskPxValue());
								}
							}
							else
								askValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryAskPxValue());
						}

						int askWidth = metrics.stringWidth(askValue);

						if (quoteWidth < askWidth)
							quoteWidth = askWidth;

						String bidChange = "-";

						if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() != null) {
							if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() <= 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
							}
							if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() > 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = "+" + dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
							}

							if (!sellSideBookEditor.getSellSideBook().getAbsolutChange())
								bidChange = bidChange + "%";

						}

						int bidChangeWidth = metrics.stringWidth(bidChange);

						if (changeWidth < bidChangeWidth)
							changeWidth = bidChangeWidth;

						String askChange = "-";

						if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() != null) {
							if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() <= 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
							}
							if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() > 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = "+" + dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
							}

							if (!sellSideBookEditor.getSellSideBook().getAbsolutChange())
								askChange = askChange + "%";
						}

						int askChangeWidth = metrics.stringWidth(askChange);

						if (changeWidth < askChangeWidth)
							changeWidth = askChangeWidth;

					}
				}

				width = width + securityCount * (2 * (quoteWidth + changeWidth + metrics.stringWidth(" / ")) + 30);

				if (width == 0) {
					if (getWidth() > 0) {
						BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics2D graphics2d = (Graphics2D) image.createGraphics();

						final GradientPaint gradientPaint = new GradientPaint(getWidth() / 2.F, 0, Color.GRAY, getWidth() / 2.F, getHeight(), Color.BLACK);

						graphics2d.setPaint(gradientPaint);
						graphics2d.fillRect(0, 0, getWidth(), getHeight());
						originalImage = image;
					}
					else
						originalImage = null;
					return;
				}

				BufferedImage image = new BufferedImage(width, getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2d = (Graphics2D) image.createGraphics();

				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, Color.GRAY, width / 2.F, getHeight(), Color.BLACK);

				graphics2d.setPaint(gradientPaint);
				graphics2d.fillRect(0, 0, width, getHeight());

				graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				graphics2d.setFont(new Font("Dialog", Font.PLAIN, 12));

				Collections.sort(sellSideCompositeMDInputEntries, new Comparator<SellSideMDInputEntry>() {

					@Override
					public int compare(SellSideMDInputEntry o1, SellSideMDInputEntry o2) {

						FSecurity security = securityMap.get(o1.getSecurityValue());
						FSecurity security2 = securityMap.get(o2.getSecurityValue());

						if (security == null && security2 == null)
							return 0;

						if (security.getMaturity() == null && security2.getMaturity() == null)
							return security.compareTo(security2);

						if (security.getMaturity() == null && security2.getMaturity() != null)
							return 1;

						if (security.getMaturity() != null && security2.getMaturity() == null)
							return -1;

						return security.getMaturity().compareTo(security2.getMaturity());
					}

				});

				int shift = 0;

				for (SellSideMDInputEntry sellSideQuotePageEntry : sellSideCompositeMDInputEntries) {

					graphics2d.setColor(orange);

					FSecurity security = securityMap.get(sellSideQuotePageEntry.getSecurityValue());

					if (security != null) {
						graphics2d.drawString(security.getName(), shift, 20);
						shift = shift + metrics.stringWidth(security.getName()) + 10;

						String bidValue = "-";

						if (sellSideQuotePageEntry.getMdEntryBidPxValue() != null) {

							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									bidValue = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdEntryBidPxValue(),1, true);
								}
								catch (Exception e) {
									bidValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryBidPxValue());
								}
							}
							else
								bidValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryBidPxValue());
							if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() != null) {
								if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() < 0)
									graphics2d.setColor(red);
								if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() > 0)
									graphics2d.setColor(green);
							}
						}

						graphics2d.drawString(bidValue, shift + quoteWidth - metrics.stringWidth(bidValue), 20);
						shift = shift + quoteWidth;

						graphics2d.setColor(orange);

						graphics2d.drawString(" / ", shift, 20);
						shift = shift + metrics.stringWidth(" / ");

						String askValue = "-";

						if (sellSideQuotePageEntry.getMdEntryAskPxValue() != null) {

							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									askValue = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdEntryAskPxValue(),1, true);
								}
								catch (Exception e) {
									askValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryAskPxValue());
								}
							}
							else
								askValue = decimalFormat.format(sellSideQuotePageEntry.getMdEntryAskPxValue());
							if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() != null) {
								if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() < 0)
									graphics2d.setColor(red);
								if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() > 0)
									graphics2d.setColor(green);
							}
						}

						graphics2d.drawString(askValue, shift, 20);
						shift = shift + quoteWidth + 10;

						String bidChange = "-";

						if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() != null) {

							if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() <= 0) {

								if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() < 0) {

									graphics2d.setColor(red);
								}

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
							}

							if (sellSideQuotePageEntry.getMdBidPriceDeltaValue() > 0) {

								graphics2d.setColor(green);

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = "+" + dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdBidPriceDeltaValue());
							}

							if (!sellSideBookEditor.getSellSideBook().getAbsolutChange())
								bidChange = bidChange + "%";

						}

						graphics2d.drawString(bidChange, shift + changeWidth - metrics.stringWidth(bidChange), 20);
						shift = shift + changeWidth;

						graphics2d.setColor(orange);

						graphics2d.drawString(" / ", shift, 20);
						shift = shift + metrics.stringWidth(" / ");

						String askChange = "-";

						if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() != null) {

							if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() <= 0) {

								if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() < 0) {

									graphics2d.setColor(red);
								}

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
							}
							if (sellSideQuotePageEntry.getMdAskPriceDeltaValue() > 0) {

								graphics2d.setColor(green);

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = "+" + dollarFraction.getDollarPrice(sellSideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = "+" + decimalFormat.format(sellSideQuotePageEntry.getMdAskPriceDeltaValue());
							}

							if (!sellSideBookEditor.getSellSideBook().getAbsolutChange())
								askChange = askChange + "%";

						}

						graphics2d.drawString(askChange, shift, 20);
						shift = shift + changeWidth + 20;
					}
				}
				originalImage = image;

			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/**
	 * Clear crawler.
	 *
	 * @param securities the securities
	 */
	public synchronized void clearCrawler(Set<Long> securities) {

		for (Long security : securities)
			sellSideMDInputEntryMap.remove(security);

		initOriginalImage();

	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public synchronized void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		for (SellSideMDInputEntry sellSideMDInputEntry : updateSellSideMDInputEntryResponse.getSellSideMDInputEntries()) {
			if (securitySet.contains(sellSideMDInputEntry.getSecurityValue()))
				sellSideMDInputEntryMap.put(sellSideMDInputEntry.getSecurityValue(), sellSideMDInputEntry);

		}

		initOriginalImage();
	}
}
