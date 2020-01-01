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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.shared.communication.HistoricalSellSideDataResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideMDInputEntry;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideQuotePage.SortBy;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * The Class SellSideQuotePageEditorChart.
 */
public class SellSideQuotePageEditorChart extends JSplitPane {

	private static final long serialVersionUID = 1L;

	private SellSideQuotePageEditor sellSideQuotePageEditor = null;

	private Color backGround1 = Color.GRAY;

	private Color backGround2 = Color.BLACK;

	private Color foreground = Color.WHITE;

	private DefaultListModel securityListModel;

	private JList securityList;

	private Map<Long, List<SellSideMDInputEntry>> quoteCache = new HashMap<Long, List<SellSideMDInputEntry>>();

	private TimeSeries series;

	private ChartPanel chartPanel;

	private Double minRange = null;

	private Double maxRange = null;

	private double minDateRange = System.currentTimeMillis();
	
	private double maxDateRange = System.currentTimeMillis() + 3600000;

	private XYPlot xyPlot = null;

	private JPanel jPanel = null;

	private DecimalFormat decimalFormat;
	
	private static Logger log = Logger.getLogger(SellSideQuotePageEditorChart.class);

	/**
	 * Instantiates a new sell side quote page editor chart.
	 *
	 * @param sellSideQuotePageEditor the sell side quote page editor
	 */
	public SellSideQuotePageEditorChart(SellSideQuotePageEditor sellSideQuotePageEditor) {

		super();

		this.sellSideQuotePageEditor = sellSideQuotePageEditor;
		
		decimalFormat = new DecimalFormat("#,##0.000");
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		setOpaque(true);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setDividerSize(3);
		setResizeWeight(0.9);
		setContinuousLayout(true);

		setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		setUI(new BasicSplitPaneUI() {

			public BasicSplitPaneDivider createDefaultDivider() {

				return new BasicSplitPaneDivider(this) {

					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {

					}

					@Override
					public void paint(Graphics g) {

						g.setColor(backGround1);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});

		jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());

		chartPanel = new ChartPanel(createChart());
		
		chartPanel.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
			
				updateGradientPaint();
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				updateGradientPaint();
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			
				// TODO Auto-generated method stub
				
			}
		});

		setLeftComponent(chartPanel);

		securityListModel = new DefaultListModel();

		securityList = new JList();
		securityList.setModel(securityListModel);
		securityList.setBorder(new EmptyBorder(5, 5, 5, 5));
		securityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		securityList.setFocusable(false);
		JScrollPane jScrollPane2 = new JScrollPane(securityList);
		jScrollPane2.setPreferredSize(new Dimension(200, 400));
		jScrollPane2.setBorder(null);
		setRightComponent(jScrollPane2);
		updateContent();

		securityList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				initChart();
			}
		});

	}
	
	private void updateGradientPaint()
	{
		if(sellSideQuotePageEditor.getSellSideQuotePage().getDisplayStyle()!=2)
			chartPanel.getChart().setBackgroundPaint(new GradientPaint(0, chartPanel.getHeight(), backGround1, chartPanel.getWidth(), 0, backGround2.brighter()));
		else
			chartPanel.getChart().setBackgroundPaint(new GradientPaint(0, chartPanel.getHeight(), backGround1, chartPanel.getWidth(), 0, backGround2));
	}
	
	private void initChart()
	{
		series.clear();

		minRange = null;
		maxRange = null;
		
		minDateRange = System.currentTimeMillis();
		maxDateRange = System.currentTimeMillis() + 3600000;

		if (securityList.getSelectedValue() instanceof AssignedSellSideBookSecurity) {
			AssignedSellSideBookSecurity assignedSellSideBookSecurity = (AssignedSellSideBookSecurity) securityList.getSelectedValue();
			List<SellSideMDInputEntry> sellSideMDInpuEntries = quoteCache.get(assignedSellSideBookSecurity.getSecurity().getId());
			if (sellSideMDInpuEntries != null)
				addEntriesToChart(sellSideMDInpuEntries);

		}
	}
	
	private void addEntriesToChart(List<SellSideMDInputEntry> sellSideMDInpuEntries)
	{
		boolean minRangeUpdated = false;
		
		boolean maxRangeUpdated = false;
		
		for (SellSideMDInputEntry sellSideMDInpuEntry : sellSideMDInpuEntries)
			try {
				double value = 0;
				int count = 0;
				if (sellSideMDInpuEntry.getMdEntryBidPxValue() != null) {
					value = value + sellSideMDInpuEntry.getMdEntryBidPxValue();
					count++;
				}
				if (sellSideMDInpuEntry.getMdEntryAskPxValue() != null) {
					value = value + sellSideMDInpuEntry.getMdEntryAskPxValue();
					count++;
				}

				if (count > 0) {
					
					if(minDateRange>sellSideMDInpuEntry.getTime())
						minDateRange = sellSideMDInpuEntry.getTime();
					
					if(maxDateRange<sellSideMDInpuEntry.getTime())
						maxDateRange = sellSideMDInpuEntry.getTime();
						
					value=value/(double)count;
					
					if (minRange == null || value < minRange)
					{
						minRange = value;
						minRangeUpdated = true;
					}
					if (maxRange == null || value > maxRange)
					{
						maxRange = value;
						maxRangeUpdated = true;
					}
					if (series.getDataItem(new Millisecond(new Date(sellSideMDInpuEntry.getTime()))) != null)
						series.update(new Millisecond(new Date(sellSideMDInpuEntry.getTime())), value);
					else
						series.add(new TimeSeriesDataItem(new Millisecond(new Date(sellSideMDInpuEntry.getTime())), value));
				}
			}
			catch (Exception e1) {
				log.error("Bug", e1);
			}
		if (minRange != null && maxRange != null)
		{
			if(minRangeUpdated)
				minRange = minRange-((maxRange-minRange)*0.1);
			if(maxRangeUpdated)
				maxRange = maxRange+((maxRange-minRange)*0.1);
			
			xyPlot.getRangeAxis(0).setRange(minRange, maxRange);
		}

		xyPlot.getDomainAxis().setRange(minDateRange, maxDateRange);
		
		if(minRange!=maxRange)
			((NumberAxis)xyPlot.getRangeAxis()).setTickUnit(new NumberTickUnit((maxRange-minRange)/10,decimalFormat));
		
		chartPanel.getChart().fireChartChanged();
		
		
	}

	/**
	 * Update content.
	 */
	public void updateContent() {
		
		switch (sellSideQuotePageEditor.getSellSideQuotePage().getDisplayStyle()) {
			case 0:
				backGround1 = new Color(0, 0, 0);
				backGround2 = new Color(31, 31, 31);
				foreground = new Color(255, 153, 0);
				break;
			case 1:
				backGround1 = new Color(0, 22, 102);
				backGround2 = new Color(0, 28, 128);
				foreground = new Color(250, 196, 0);

				break;
			default:
				backGround1 = new Color(255, 243, 204);
				backGround2 = new Color(255, 236, 179);
				foreground = Color.BLACK;
				break;
		}
		
		securityList.setBackground(backGround1);
		securityList.setForeground(foreground);
		securityList.setSelectionBackground(foreground);
		securityList.setSelectionForeground(backGround1);
		jPanel.setBackground(backGround1);
		chartPanel.setBorder(new MatteBorder(25, 25, 25, 25, backGround1));
		chartPanel.setChart(createChart());
		
		updateGradientPaint();

		repaint();
		
		securityListModel.clear();
		
		List<AssignedSellSideBookSecurity> assignedSellSideBookSecurities = new ArrayList<AssignedSellSideBookSecurity>();
		
		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideQuotePageEditor.getSellSideQuotePage().getAssignedSellSideBookSecurities()) {
			if (sellSideQuotePageEditor.getBasisClientConnector().getFUser().canRead(sellSideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(assignedSellSideBookSecurity.getSecurity().getId())))
				assignedSellSideBookSecurities.add(assignedSellSideBookSecurity);
		}
		
		Collections.sort(assignedSellSideBookSecurities,  new Comparator<AssignedSellSideBookSecurity>() {

			@Override
			public int compare(AssignedSellSideBookSecurity o1, AssignedSellSideBookSecurity o2) {

				FSecurity security = o1.getSecurity();
				FSecurity security2 = o2.getSecurity();

				if (sellSideQuotePageEditor.getSellSideQuotePage().getSortBy() == SortBy.SYMBOL || (security.getMaturity() == null && security2.getMaturity() == null))
					return security.compareTo(security2);

				if (security.getMaturity() == null && security2.getMaturity() != null)
					return 1;

				if (security.getMaturity() != null && security2.getMaturity() == null)
					return -1;

				return security.getMaturity().compareTo(security2.getMaturity());
			}

		});
		
		for(AssignedSellSideBookSecurity assignedSellSideBookSecurity: assignedSellSideBookSecurities)
			securityListModel.addElement(assignedSellSideBookSecurity);
		
		if(securityListModel.getSize()>0&&securityList.getSelectedIndex()==-1)
			securityList.setSelectedIndex(0);
		
		initChart();

	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		addSellSideMDInputEntries(updateSellSideMDInputEntryResponse.getSellSideMDInputEntries());

	}

	private synchronized void addSellSideMDInputEntries(Collection<SellSideMDInputEntry> sellSideMDInpuEntryCollection)
	{
		List<SellSideMDInputEntry> sellSideMDInpuEntries = new ArrayList<SellSideMDInputEntry>();
		
		for (SellSideMDInputEntry sellSideMDInpuEntry : sellSideMDInpuEntryCollection) {
			
				List<SellSideMDInputEntry> quoteList = quoteCache.get(sellSideMDInpuEntry.getSecurityValue());
				if (quoteList == null) {
					quoteList = new ArrayList<SellSideMDInputEntry>();
					quoteCache.put(sellSideMDInpuEntry.getSecurityValue(), quoteList);
				}
				quoteList.add(sellSideMDInpuEntry);
				if (securityList.getSelectedValue() instanceof AssignedSellSideBookSecurity) {
					AssignedSellSideBookSecurity assignedSellSideBookSecurity = (AssignedSellSideBookSecurity) securityList.getSelectedValue();
					if (assignedSellSideBookSecurity.getSecurity().getId() == sellSideMDInpuEntry.getSecurityValue()) {
						sellSideMDInpuEntries.add(sellSideMDInpuEntry);
					}
				}

			
		}

		if(sellSideMDInpuEntries.size()>0)
			addEntriesToChart(sellSideMDInpuEntries);
	}
	
	private JFreeChart createChart() {

		series = new TimeSeries("");
		final TimeSeriesCollection data = new TimeSeriesCollection(series);
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesPaint(0, foreground);
		DateAxis dateAxis = new DateAxis("");
		dateAxis.setTickLabelPaint(foreground);
		dateAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
		dateAxis.setAxisLinePaint(foreground);
		NumberAxis numberAxis = new NumberAxis("Mid Price");
		numberAxis.setLabelPaint(foreground);
		numberAxis.setLabelFont(new Font("Dialog", Font.PLAIN, 12));
		numberAxis.setAxisLinePaint(foreground);
		numberAxis.setTickLabelPaint(foreground);
		numberAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
		numberAxis.setNumberFormatOverride(decimalFormat);
		numberAxis.setTickUnit(new NumberTickUnit(0.001));
		numberAxis.setAutoRange(false);
		numberAxis.setRange(0d, 100d);
		
		xyPlot = new XYPlot(data, dateAxis, numberAxis, renderer);
		xyPlot.setDataset(0, data);
		xyPlot.setBackgroundPaint(null);
		xyPlot.setDomainGridlinePaint(backGround2.brighter());
		xyPlot.setRangeGridlinePaint(backGround2.brighter());
		xyPlot.setOutlineVisible(false);
		
		
		final JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyPlot, true);
		chart.setBorderVisible(false);
		chart.setBackgroundPaint(backGround1);
		chart.getLegend().setVisible(false);
		chart.setAntiAlias(true);
		chart.setTextAntiAlias(true);
		return chart;
	}

	/**
	 * On historical data response.
	 *
	 * @param historicalDataResponse the historical data response
	 */
	public void onHistoricalDataResponse(HistoricalSellSideDataResponse historicalDataResponse) {

		byte[] historicalBytes = historicalDataResponse.getHistoricalData();

		if (historicalBytes.length > 0) {
			try {

				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(historicalBytes);
				GZIPInputStream gzip = new GZIPInputStream(byteArrayInputStream);
				ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
				@SuppressWarnings("unchecked")
				List<SellSideMDInputEntry> sellSideCompositeMDInputEntries = (List<SellSideMDInputEntry>) objectInputStream.readObject();

				addSellSideMDInputEntries(sellSideCompositeMDInputEntries);
			}
			catch (Exception e) {
				log.error("Bug", e);

			}
		}
		
	}

}
