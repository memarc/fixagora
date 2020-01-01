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
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCompositeMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;

import org.apache.log4j.Logger;

/**
 * The Class QuoteCrawler.
 */
public class QuoteCrawler extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<Long, FSecurity> securityMap = new HashMap<Long, FSecurity>();

	private DecimalFormat decimalFormat;

	private double crawlershift = 0;

	private Color orange = new Color(255, 153, 0);

	private Color green = new Color(0, 204, 0);

	private Color red = new Color(255, 51, 51);

	private BufferedImage originalImage = null;

	private BuySideBookEditor buySideBookEditor = null;

	private Map<Long, BuySideCompositeMDInputEntry> buySideMDInputEntryMap = new HashMap<Long, BuySideCompositeMDInputEntry>();

	private int quoteWidth = 0;

	private int changeWidth = 0;

	private boolean crawl = false;

	private static Logger log = Logger.getLogger(BuySideBookEditor.class);

	private Set<Long> securitySet = new HashSet<Long>();

	private Map<Long, Double> dollarTickMap = new HashMap<Long, Double>();

	/**
	 * Instantiates a new quote crawler.
	 *
	 * @param buySideBookEditor the buy side book editor
	 */
	public QuoteCrawler(BuySideBookEditor buySideBookEditor) {

		super();

		this.buySideBookEditor = buySideBookEditor;

		int fractionDigits = 3;

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities()) {
			FSecurity security = (FSecurity) buySideBookEditor.getMainPanel().getAbstractBusinessObjectForId(assignedBuySideBookSecurity.getSecurity().getId());

			if (security != null) {

				securitySet.add(security.getId());

				Boolean fractionalDisplay = assignedBuySideBookSecurity.getFractionalDisplay();
				if (fractionalDisplay == null)
					fractionalDisplay = buySideBookEditor.getBuySideBook().getFractionalDisplay();

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

					dollarTickMap.put(assignedBuySideBookSecurity.getSecurity().getId(), dollarTick);
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

	private void draw(Graphics graphics) {

		if (graphics == null || !isShowing())
			return;

		try {

			crawlershift = crawlershift + 1;

			if (originalImage == null || crawlershift > originalImage.getWidth())
				crawlershift = 0;

			if (originalImage != null) {
				int width = originalImage.getWidth() - (int) crawlershift;
				if (width > getWidth())
					width = getWidth();
				graphics.drawImage(originalImage, 0, 0, width, getHeight(), (int) crawlershift, 0, (int) crawlershift + width, getHeight(), this);
				int offset = width;
				while (offset < getWidth()) {
					graphics.drawImage(originalImage, offset, 0, offset + originalImage.getWidth(), getHeight(), 0, 0, originalImage.getWidth(), getHeight(),
							this);
					offset = offset + originalImage.getWidth();
				}
			}
		}
		catch (Exception e) {
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

				List<BuySideCompositeMDInputEntry> buySideCompositeMDInputEntries = new ArrayList<BuySideCompositeMDInputEntry>();
				buySideCompositeMDInputEntries.addAll(buySideMDInputEntryMap.values());

				int securityCount = 0;

				for (BuySideCompositeMDInputEntry buySideQuotePageEntry : buySideCompositeMDInputEntries) {

					FSecurity security = securityMap.get(buySideQuotePageEntry.getSecurityValue());
					if (security == null) {
						security = buySideBookEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(buySideQuotePageEntry.getSecurityValue());
						securityMap.put(buySideQuotePageEntry.getSecurityValue(), security);
					}
					if (security != null) {

						securityCount++;

						width = width + metrics.stringWidth(security.getName()) + 10;

						String bidValue = "-";

						if (buySideQuotePageEntry.getMdEntryBidPxValue() != null) {

							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									bidValue = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdEntryBidPxValue(), 1, true);
								}
								catch (Exception e) {
									bidValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryBidPxValue());
								}
							}
							else
								bidValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryBidPxValue());
						}

						int bidWidth = metrics.stringWidth(bidValue);

						if (quoteWidth < bidWidth)
							quoteWidth = bidWidth;

						String askValue = "-";

						if (buySideQuotePageEntry.getMdEntryAskPxValue() != null) {
							
							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									askValue = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdEntryAskPxValue(), 1, true);
								}
								catch (Exception e) {
									askValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryAskPxValue());
								}
							}
							else
							askValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryAskPxValue());
						}

						int askWidth = metrics.stringWidth(askValue);

						if (quoteWidth < askWidth)
							quoteWidth = askWidth;

						String bidChange = "-";

						if (buySideQuotePageEntry.getMdBidPriceDeltaValue() != null) {

							if (buySideQuotePageEntry.getMdBidPriceDeltaValue() <= 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdBidPriceDeltaValue(), 0, true);
									}
									catch (Exception e) {
										bidChange = decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
							}
							if (buySideQuotePageEntry.getMdBidPriceDeltaValue() > 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = "+" + dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdBidPriceDeltaValue(), 0, true);
									}
									catch (Exception e) {
										bidChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
							}

							if (!buySideBookEditor.getBuySideBook().getAbsolutChange())
								bidChange = bidChange + "%";

						}

						int bidChangeWidth = metrics.stringWidth(bidChange);

						if (changeWidth < bidChangeWidth)
							changeWidth = bidChangeWidth;

						String askChange = "-";

						if (buySideQuotePageEntry.getMdAskPriceDeltaValue() != null) {

							if (buySideQuotePageEntry.getMdAskPriceDeltaValue() <= 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
							}
							if (buySideQuotePageEntry.getMdAskPriceDeltaValue() > 0) {

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = "+" + dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
							}

							if (!buySideBookEditor.getBuySideBook().getAbsolutChange())
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

				Collections.sort(buySideCompositeMDInputEntries, new Comparator<BuySideCompositeMDInputEntry>() {

					@Override
					public int compare(BuySideCompositeMDInputEntry o1, BuySideCompositeMDInputEntry o2) {

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

				for (BuySideCompositeMDInputEntry buySideQuotePageEntry : buySideCompositeMDInputEntries) {

					graphics2d.setColor(orange);

					FSecurity security = securityMap.get(buySideQuotePageEntry.getSecurityValue());

					if (security != null) {
						graphics2d.drawString(security.getName(), shift, 20);
						shift = shift + metrics.stringWidth(security.getName()) + 10;

						String bidValue = "-";

						if (buySideQuotePageEntry.getMdEntryBidPxValue() != null) {
							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									bidValue = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdEntryBidPxValue(),1, true);
								}
								catch (Exception e) {
									bidValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryBidPxValue());
								}
							}
							else
							bidValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryBidPxValue());
							if (buySideQuotePageEntry.getMdBidPriceDeltaValue() != null) {
								if (buySideQuotePageEntry.getMdBidPriceDeltaValue() < 0)
									graphics2d.setColor(red);
								if (buySideQuotePageEntry.getMdBidPriceDeltaValue() > 0)
									graphics2d.setColor(green);
							}
						}

						graphics2d.drawString(bidValue, shift + quoteWidth - metrics.stringWidth(bidValue), 20);
						shift = shift + quoteWidth;

						graphics2d.setColor(orange);

						graphics2d.drawString(" / ", shift, 20);
						shift = shift + metrics.stringWidth(" / ");

						String askValue = "-";

						if (buySideQuotePageEntry.getMdEntryAskPxValue() != null) {
							
							Double dollarTick = dollarTickMap.get(security.getId());
							if (dollarTick != null) {
								try {
									DollarFraction dollarFraction = new DollarFraction(dollarTick);
									askValue = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdEntryAskPxValue(),1, true);
								}
								catch (Exception e) {
									askValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryAskPxValue());
								}
							}
							else
							askValue = decimalFormat.format(buySideQuotePageEntry.getMdEntryAskPxValue());
							if (buySideQuotePageEntry.getMdAskPriceDeltaValue() != null) {
								if (buySideQuotePageEntry.getMdAskPriceDeltaValue() < 0)
									graphics2d.setColor(red);
								if (buySideQuotePageEntry.getMdAskPriceDeltaValue() > 0)
									graphics2d.setColor(green);
							}
						}

						graphics2d.drawString(askValue, shift, 20);
						shift = shift + quoteWidth + 10;

						String bidChange = "-";

						if (buySideQuotePageEntry.getMdBidPriceDeltaValue() != null) {

							if (buySideQuotePageEntry.getMdBidPriceDeltaValue() <= 0) {

								if (buySideQuotePageEntry.getMdBidPriceDeltaValue() < 0) {

									graphics2d.setColor(red);
								}

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
							}

							if (buySideQuotePageEntry.getMdBidPriceDeltaValue() > 0) {

								graphics2d.setColor(green);

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										bidChange = "+" + dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdBidPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										bidChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
									}
								}
								else
									bidChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdBidPriceDeltaValue());
							}


							if (!buySideBookEditor.getBuySideBook().getAbsolutChange())
								bidChange = bidChange + "%";
						}

						graphics2d.drawString(bidChange, shift + changeWidth - metrics.stringWidth(bidChange), 20);
						shift = shift + changeWidth;

						graphics2d.setColor(orange);

						graphics2d.drawString(" / ", shift, 20);
						shift = shift + metrics.stringWidth(" / ");

						String askChange = "-";

						if (buySideQuotePageEntry.getMdAskPriceDeltaValue() != null) {

							if (buySideQuotePageEntry.getMdAskPriceDeltaValue() <= 0) {

								if (buySideQuotePageEntry.getMdAskPriceDeltaValue() < 0) {

									graphics2d.setColor(red);
								}

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
							}
							if (buySideQuotePageEntry.getMdAskPriceDeltaValue() > 0) {

								graphics2d.setColor(green);

								Double dollarTick = dollarTickMap.get(security.getId());
								if (dollarTick != null) {
									try {
										DollarFraction dollarFraction = new DollarFraction(dollarTick);
										askChange = "+" + dollarFraction.getDollarPrice(buySideQuotePageEntry.getMdAskPriceDeltaValue(),0, true);
									}
									catch (Exception e) {
										askChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
									}
								}
								else
									askChange = "+" + decimalFormat.format(buySideQuotePageEntry.getMdAskPriceDeltaValue());
							}


							if (!buySideBookEditor.getBuySideBook().getAbsolutChange())
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
			buySideMDInputEntryMap.remove(security);

		initOriginalImage();

	}

	/**
	 * On update buy side md input entry response.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public synchronized void onUpdateBuySideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		for (AbstractBuySideMDInputEntry buySideMDInputEntry : updateBuySideMDInputEntryResponse.getBuySideMDInputEntries()) {
			if (buySideMDInputEntry instanceof BuySideCompositeMDInputEntry && securitySet.contains(buySideMDInputEntry.getSecurityValue())) {

				buySideMDInputEntryMap.put(buySideMDInputEntry.getSecurityValue(), (BuySideCompositeMDInputEntry) buySideMDInputEntry);

			}
		}

		initOriginalImage();
	}
}
