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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideQuotePageEditorCompositeTableModel;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideQuotePageEditorDepthTableModel;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideQuotePageEntry;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideCounterpartyMDInputEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.OpenQuoteDepthRequest;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;

import org.apache.log4j.Logger;

import bibliothek.gui.Dockable;

/**
 * The Class BuySideQuotePageEditorMonitor.
 */
public class BuySideQuotePageEditorMonitor extends JSplitPane {

	private static final long serialVersionUID = 1L;

	private BuySideQuotePageEditor buySideQuotePageEditor = null;

	private JTable topTable = null;

	private BuySideQuotePageEditorCompositeTableModel buySideQuotePageEditorMonitorTableModel = null;

	private boolean visible = true;

	private Color backGround1 = Color.GRAY;

	private Color backGround2 = Color.BLACK;

	private Color backGround3 = null;

	private Color foreground = null;

	private QuotePageTableRenderer quotePageMonitorTableRenderer = null;

	private JScrollPane topScrollPane = null;

	private JSplitPane jSplitPane = null;

	private JScrollPane leftScrollPane = null;

	private JScrollPane rightScrollPane = null;

	private BuySideQuotePageEditorDepthTableModel buySideQuotePageEditorBidDepthTableModel = null;

	private JTable bidTable = null;

	private JTable askTable = null;

	private GradientLabel bidLabel = null;

	private GradientLabel askLabel = null;

	private FSecurity security = null;

	private BuySideQuotePageEditorDepthTableModel buySideQuotePageEditorAskDepthTableModel = null;

	private QuotePageBidDepthTableRenderer quotePageBidDepthTableRenderer = null;

	private QuotePageAskDepthTableRenderer quotePageAskDepthTableRenderer = null;

	private Color bidBackGround = new Color(204, 0, 0);

	private Color askBackGround = new Color(0, 153, 0);

	private BuySideBook buySideBook;

	private static Logger log = Logger.getLogger(BuySideQuotePageEditorMonitor.class);

	/**
	 * Instantiates a new buy side quote page editor monitor.
	 *
	 * @param buySideQuotePageEditor the buy side quote page editor
	 */
	public BuySideQuotePageEditorMonitor(BuySideQuotePageEditor buySideQuotePageEditor) {

		super();

		switch (buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle()) {
			case 0:
				backGround1 = new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				backGround3 = Color.BLACK;
				foreground = Color.WHITE;
				break;
			case 1:
				backGround1 = new Color(255, 202, 10);
				backGround2 = (new Color(255, 202, 10)).darker();
				backGround3 = new Color(0, 22, 102);
				foreground = Color.BLACK;

				break;
			default:
				backGround1 = Color.GRAY;
				backGround2 = Color.BLACK;
				backGround3 = new Color(255, 243, 204);
				foreground = Color.WHITE;
				break;
		}

		this.buySideQuotePageEditor = buySideQuotePageEditor;

		setOpaque(true);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setDividerSize(3);

		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() == false) {
			setDividerLocation(1.0);
			setResizeWeight(1.0);
		}
		else {
			setDividerLocation(0.75);
			setResizeWeight(0.75);
		}

		setOrientation(JSplitPane.VERTICAL_SPLIT);

		setUI(new BasicSplitPaneUI() {

			public BasicSplitPaneDivider createDefaultDivider() {

				return new BasicSplitPaneDivider(this) {

					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {

					}

					@Override
					public void paint(Graphics g) {

						g.setColor(backGround3);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});

		topScrollPane = new JScrollPane() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, backGround1, width / 2.F, 26, backGround2);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		topScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		setLeftComponent(topScrollPane);

		jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jSplitPane.setOpaque(true);
		jSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		jSplitPane.setDividerSize(3);
		jSplitPane.setResizeWeight(0.5);
		jSplitPane.setContinuousLayout(true);

		jSplitPane.setUI(new BasicSplitPaneUI() {

			public BasicSplitPaneDivider createDefaultDivider() {

				return new BasicSplitPaneDivider(this) {

					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {

					}

					@Override
					public void paint(Graphics g) {

						g.setColor(backGround3);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});

		setRightComponent(jSplitPane);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		bidLabel = new GradientLabel((new Color(204, 0, 0)).darker(), new Color(204, 0, 0));
		bidLabel.setText("Best Bid Market Depth");
		bidLabel.setMinimumSize(new Dimension(4, 25));
		bidLabel.setPreferredSize(new Dimension(4, 25));
		bidLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bidLabel.setForeground(Color.WHITE);
		leftPanel.add(bidLabel, BorderLayout.NORTH);
		leftScrollPane = new JScrollPane() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, bidBackGround, width / 2.F, 26, (bidBackGround).darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		leftScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		leftPanel.add(leftScrollPane, BorderLayout.CENTER);

		jSplitPane.setLeftComponent(leftPanel);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		askLabel = new GradientLabel((new Color(0, 153, 0)).darker(), new Color(0, 153, 0));
		askLabel.setText("Best Ask Market Depth");
		askLabel.setMinimumSize(new Dimension(4, 25));
		askLabel.setPreferredSize(new Dimension(4, 25));
		askLabel.setHorizontalAlignment(SwingConstants.CENTER);
		askLabel.setForeground(Color.WHITE);
		rightPanel.add(askLabel, BorderLayout.NORTH);
		rightScrollPane = new JScrollPane() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, askBackGround, width / 2.F, 26, (askBackGround).darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		rightScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		rightPanel.add(rightScrollPane, BorderLayout.CENTER);

		jSplitPane.setRightComponent(rightPanel);

		initTable();

		topScrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				int width = buySideQuotePageEditorMonitorTableModel.getFirstColumnWidth();

				for (int i = 1; i < topTable.getColumnCount() - 1; i++) {
					width = width + topTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorMonitorTableModel.setMinWidth((int) topScrollPane.getViewport().getWidth() - width);

				revalidate();
			}

			@Override
			public void componentShown(final ComponentEvent e) {

				topScrollPane.setPreferredSize(new Dimension(getWidth(), getHeight() / 2));
				jSplitPane.setPreferredSize(new Dimension(getWidth(), getHeight() / 2));
				resetToPreferredSizes();
				componentResized(e);
			}
		});

		leftScrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				int width = 0;

				for (int i = 0; i < bidTable.getColumnCount() - 1; i++) {
					width = width + bidTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorBidDepthTableModel.setMinWidth((int) leftScrollPane.getViewport().getWidth() - width);
			}

			@Override
			public void componentShown(final ComponentEvent e) {

				componentResized(e);
			}
		});

		rightScrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				int width = 0;

				for (int i = 0; i < askTable.getColumnCount() - 1; i++) {
					width = width + askTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorAskDepthTableModel.setMinWidth((int) rightScrollPane.getViewport().getWidth() - width);
			}

			@Override
			public void componentShown(final ComponentEvent e) {

				componentResized(e);
			}
		});

		Thread thread = new Thread() {

			@Override
			public void run() {

				while (visible) {
					try {
						sleep(200);

						quotePageMonitorTableRenderer.switchHighlight();
						quotePageBidDepthTableRenderer.switchHighlight();
						quotePageAskDepthTableRenderer.switchHighlight();
						if (buySideQuotePageEditorMonitorTableModel.isRepaintRequired()) {
							topTable.revalidate();
							topTable.repaint();
						}
						if (buySideQuotePageEditorBidDepthTableModel.isRepaintRequired()) {
							bidTable.revalidate();
							bidTable.repaint();
						}
						if (buySideQuotePageEditorAskDepthTableModel.isRepaintRequired()) {
							askTable.revalidate();
							askTable.repaint();
						}

					}
					catch (Exception e) {
						log.error("Bug", e);
					}
				}
			}

		};

		thread.start();

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() == false) {
			setDividerLocation(1.0);
			setResizeWeight(1.0);
		}
		else {
			setDividerLocation(0.75);
			setResizeWeight(0.75);
		}

		buySideBook = (BuySideBook) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(
				buySideQuotePageEditor.getBuySideQuotePage().getParent().getId());

		initTable();

		revalidate();

	}

	private void initTable() {

		security = null;

		if (buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities().size() > 0) {
			security = buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities().get(0).getSecurity();
			security = (FSecurity) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(security.getId());
			askLabel.setText(security.getName() + "  Ask Market Depth");
			bidLabel.setText(security.getName() + "  Bid Market Depth");
		}
		else {
			askLabel.setText("Ask Market Depth");
			bidLabel.setText("Bid Market Depth");
		}

		switch (buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle()) {
			case 0:

				backGround1 = new Color(255, 202, 10);
				backGround2 = (new Color(255, 202, 10)).darker();

				backGround3 = Color.BLACK;
				foreground = Color.BLACK;
				break;
			case 1:
				backGround1 = new Color(250, 196, 0);
				backGround2 = (new Color(250, 196, 0)).darker();
				backGround3 = new Color(0, 22, 102);
				foreground = Color.BLACK;

				break;
			default:
				backGround1 = Color.GRAY;
				backGround2 = Color.BLACK;
				backGround3 = new Color(255, 243, 204);
				foreground = Color.WHITE;
				break;
		}

		topScrollPane.getViewport().setBackground(backGround3);

		topTable = new JTable();
		topTable.setTableHeader(new JTableHeader(topTable.getColumnModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, backGround1, width / 2.F, 25, backGround2);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}
		});

		topTable.getTableHeader().setReorderingAllowed(false);

		topTable.getTableHeader().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {

				int width = buySideQuotePageEditorMonitorTableModel.getFirstColumnWidth();

				for (int i = 1; i < topTable.getColumnCount() - 1; i++) {
					width = width + topTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorMonitorTableModel.setMinWidth((int) topScrollPane.getViewport().getWidth() - width);

			}

		});

		topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		buySideQuotePageEditorMonitorTableModel = new BuySideQuotePageEditorCompositeTableModel(buySideQuotePageEditor.getBuySideQuotePage(),
				buySideQuotePageEditor.getMainPanel());
		topTable.setModel(buySideQuotePageEditorMonitorTableModel);

		quotePageMonitorTableRenderer = new QuotePageTableRenderer(buySideQuotePageEditor.getBuySideQuotePage());

		topTable.setDefaultRenderer(Object.class, quotePageMonitorTableRenderer);
		topTable.setShowHorizontalLines(false);
		topTable.setShowVerticalLines(false);
		topTable.setIntercellSpacing(new Dimension(0, 0));
		topTable.setAutoscrolls(false);
		topTable.setRowHeight(25);
		topTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		topTable.setBackground(backGround3);
		topTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		topTable.setFillsViewportHeight(true);

		buySideBook = (BuySideBook) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(
				buySideQuotePageEditor.getBuySideQuotePage().getParent().getId());

		topTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final MouseEvent e) {

				final FSecurity security1 = buySideQuotePageEditorMonitorTableModel.getSecurityForRow(topTable.rowAtPoint(e.getPoint()));
				final FSecurity security = (FSecurity) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(security1.getId());

				if (security != null && e.getButton() != MouseEvent.BUTTON1
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canExecute(buySideBook)) {

					JPopupMenu tablePopup = new JPopupMenu();

					JMenuItem buyMenuItem = new JMenuItem("Buy " + security.getName());
					buyMenuItem.setEnabled(true);
					buyMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(buyMenuItem);

					buyMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							Side side = Side.ASK;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorMonitorTableModel.getValueAt(
									topTable.rowAtPoint(e.getPoint()), 1);
							if (buySideQuotePageEntry != null) {
								if (security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskPxValue();
								}
							}
							BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
							buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
							buySideNewOrderSingleEntry.setSecurity(security.getId());
							buySideNewOrderSingleEntry.setSide(side);
							buySideNewOrderSingleEntry.setLimit(limit);

							BuySideQuotePageEntry buySideQuotePageEntry2 = null;
							if (buySideQuotePageEditorAskDepthTableModel.getRowCount() > 0)
								buySideQuotePageEntry2 = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel.getValueAt(0, 1);
							if (buySideQuotePageEntry2 != null && buySideQuotePageEntry2.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry()).getInterfaceValue());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());
							}
							openTicket(buySideNewOrderSingleEntry);
						}
					});

					JMenuItem sellMenuItem = new JMenuItem("Sell " + security.getName());
					sellMenuItem.setEnabled(true);
					sellMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(sellMenuItem);

					sellMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							Side side = Side.BID;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorMonitorTableModel.getValueAt(
									topTable.rowAtPoint(e.getPoint()), 1);
							if (buySideQuotePageEntry != null) {
								if (security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidPxValue();
								}
							}
							BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
							buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
							buySideNewOrderSingleEntry.setSecurity(security.getId());
							buySideNewOrderSingleEntry.setSide(side);
							buySideNewOrderSingleEntry.setLimit(limit);

							BuySideQuotePageEntry buySideQuotePageEntry2 = null;
							if (buySideQuotePageEditorBidDepthTableModel.getRowCount() > 0)
								buySideQuotePageEntry2 = (BuySideQuotePageEntry) buySideQuotePageEditorBidDepthTableModel.getValueAt(0, 1);
							if (buySideQuotePageEntry2 != null && buySideQuotePageEntry2.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry()).getInterfaceValue());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());

							}
							openTicket(buySideNewOrderSingleEntry);

						}

					});

					JMenuItem inquiryBuyMenuItem = new JMenuItem("Inquiry buy " + security.getName());
					inquiryBuyMenuItem.setEnabled(true);
					inquiryBuyMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(inquiryBuyMenuItem);

					inquiryBuyMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.Side side = net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.Side.ASK;

							Set<Counterparty> counterparties = new HashSet<Counterparty>();

							for (int i = 0; i < Math.min(buySideQuotePageEditorAskDepthTableModel.getRowCount(), 4); i++) {
								BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel.getValueAt(i, 1);
								if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
									Counterparty counterparty = buySideQuotePageEditor
											.getMainPanel()
											.getCounterpartyTreeDialog()
											.getCounterpartyForId(
													((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
									counterparties.add(counterparty);
								}

							}
							openTicket(side, new ArrayList<Counterparty>(counterparties), security.getId());
						}

					});

					JMenuItem inquirySellMenuItem = new JMenuItem("Inquiry sell " + security.getName());
					inquirySellMenuItem.setEnabled(true);
					inquirySellMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(inquirySellMenuItem);

					inquirySellMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.Side side = net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.Side.BID;

							Set<Counterparty> counterparties = new HashSet<Counterparty>();

							for (int i = 0; i < Math.min(buySideQuotePageEditorAskDepthTableModel.getRowCount(), 4); i++) {
								BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel.getValueAt(i, 1);
								if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
									Counterparty counterparty = buySideQuotePageEditor
											.getMainPanel()
											.getCounterpartyTreeDialog()
											.getCounterpartyForId(
													((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
									counterparties.add(counterparty);
								}

							}
							openTicket(side, new ArrayList<Counterparty>(counterparties), security.getId());
						}

					});

					JMenuItem openMenuItem = new JMenuItem("Open " + security.getName());
					openMenuItem.setEnabled(true);
					openMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(openMenuItem);

					openMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							buySideQuotePageEditor.getMainPanel().openBusinessObject(security.getName());

						}
					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {

				buySideQuotePageEditorMonitorTableModel.setMouseOverRow(-1);

				topTable.repaint();
			}

			@Override
			public void mouseClicked(final MouseEvent e) {

				final int row = buySideQuotePageEditorMonitorTableModel.getMouseOverRow();
				if (row >= 0) {
					security = buySideQuotePageEditorMonitorTableModel.getSecurityForRow(row);
					security = (FSecurity) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(security.getId());
				}

				if (e.getClickCount() == 1) {

					if (security != null) {
						buySideQuotePageEditorAskDepthTableModel.setSecurity(security.getId());
						buySideQuotePageEditorBidDepthTableModel.setSecurity(security.getId());

						askLabel.setText(security.getName() + "  Ask Market Depth");
						bidLabel.setText(security.getName() + "  Bid Market Depth");

						buySideQuotePageEditor.getBasisClientConnector().sendRequest(
								new OpenQuoteDepthRequest(buySideQuotePageEditor.getBuySideQuotePage().getId(), security.getId(), RequestIDManager
										.getInstance().getID()));
					}
				}
				if (e.getClickCount() == 2) {
					final FSecurity security1 = buySideQuotePageEditorMonitorTableModel.getSecurityForRow(topTable.rowAtPoint(e.getPoint()));
					final FSecurity selectedSecurity = (FSecurity) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(security1.getId());

					if (selectedSecurity != null) {
						int col = topTable.columnAtPoint(e.getPoint());
						if (col == 0) {
							buySideQuotePageEditor.getMainPanel().openBusinessObject(selectedSecurity.getName());
							return;
						}
						if (col > 0 && col < 4) {

							int x = e.getPoint().x;
							if (x < 0) {
								return;
							}
							int cc = topTable.getColumnModel().getColumnCount();
							for (int column = 0; column < cc; column++) {
								x = x - topTable.getColumnModel().getColumn(column).getWidth();
								if (x < 0) {
									Side side = Side.ASK;
									Double limit = null;
									BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorMonitorTableModel.getValueAt(
											row, 1);
									if (x * -1d > topTable.getColumnModel().getColumn(column).getWidth() / 2) {
										side = Side.BID;
									}
									if (buySideQuotePageEntry != null) {
										if (selectedSecurity.getSecurityDetails().getPriceQuoteMethod() != null
												&& selectedSecurity.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
											if (side == Side.ASK)
												limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskYieldValue();
											else
												limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidYieldValue();
										}
										else {
											if (side == Side.ASK)
												limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskPxValue();
											else
												limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidPxValue();
										}
									}
									BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
									buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
									buySideNewOrderSingleEntry.setSecurity(selectedSecurity.getId());
									buySideNewOrderSingleEntry.setSide(side);
									buySideNewOrderSingleEntry.setLimit(limit);

									BuySideQuotePageEntry buySideQuotePageEntry2 = null;
									if (side == Side.BID && buySideQuotePageEditorBidDepthTableModel.getRowCount() > 0)
										buySideQuotePageEntry2 = (BuySideQuotePageEntry) buySideQuotePageEditorBidDepthTableModel.getValueAt(0, 1);
									if (side == Side.ASK && buySideQuotePageEditorAskDepthTableModel.getRowCount() > 0)
										buySideQuotePageEntry2 = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel.getValueAt(0, 1);
									if (buySideQuotePageEntry2 != null
											&& buySideQuotePageEntry2.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
										Counterparty counterparty = buySideQuotePageEditor
												.getMainPanel()
												.getCounterpartyTreeDialog()
												.getCounterpartyForId(
														((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry())
																.getCounterpartyValue());
										AbstractInitiator fixInitiator = buySideQuotePageEditor
												.getMainPanel()
												.getInitiatorTreeDialog()
												.getAbstractInitiatorForId(
														((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry2.getBuySideMDInputEntry()).getInterfaceValue());
										if (counterparty != null)
											buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
										if (fixInitiator != null)
											buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());

									}

									openTicket(buySideNewOrderSingleEntry);
									return;
								}

							}

						}
					}
				}
			}

		});

		topTable.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {

			}

			@Override
			public void mouseMoved(final MouseEvent e) {

				int row = topTable.rowAtPoint(e.getPoint());

				buySideQuotePageEditorMonitorTableModel.setMouseOverRow(row);

				topTable.repaint();
			}
		});

		buySideQuotePageEditorMonitorTableModel.setTable(topTable);

		for (int i = 0; i < topTable.getColumnCount(); i++) {

			GradientTableHeaderRenderer gradientTableHeaderRenderer = new GradientTableHeaderRenderer(5, backGround2, backGround1, foreground);
			if (i == 0)
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			else if (i == 7)
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
			else
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.CENTER);

			topTable.getColumnModel().getColumn(i).setHeaderRenderer(gradientTableHeaderRenderer);
			if (i == 0)
				topTable.getColumnModel().getColumn(i).setPreferredWidth(buySideQuotePageEditorMonitorTableModel.getFirstColumnWidth());
			else
				topTable.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
		buySideQuotePageEditorMonitorTableModel.setTable(topTable);
		topScrollPane.setViewportView(topTable);

		leftScrollPane.getViewport().setBackground(backGround3);

		bidTable = new JTable();
		bidBackGround = new Color(204, 0, 0);
		// if (buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle() ==
		// 1)
		// bidBackGround = new Color(128, 0, 28);

		bidLabel.setBright(bidBackGround);
		bidLabel.setDark(bidBackGround.darker());

		bidTable.setTableHeader(new JTableHeader(bidTable.getColumnModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bidBackGround, width / 2.F, 25, (bidBackGround).darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}
		});

		bidTable.getTableHeader().setReorderingAllowed(false);

		bidTable.getTableHeader().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {

				int width = 0;

				for (int i = 0; i < bidTable.getColumnCount() - 1; i++) {
					width = width + bidTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorBidDepthTableModel.setMinWidth((int) leftScrollPane.getViewport().getWidth() - width);

			}

		});

		bidTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		Comparator<BuySideQuotePageEntry> comparator = new Comparator<BuySideQuotePageEntry>() {

			@Override
			public int compare(BuySideQuotePageEntry o1, BuySideQuotePageEntry o2) {

				if (o1.getBuySideMDInputEntry().getMdEntryBidPxValue() == null && o2.getBuySideMDInputEntry().getMdEntryBidPxValue() == null)
					return 0;
				if (o1.getBuySideMDInputEntry().getMdEntryBidPxValue() == null && o2.getBuySideMDInputEntry().getMdEntryBidPxValue() != null)
					return 1;
				if (o1.getBuySideMDInputEntry().getMdEntryBidPxValue() != null && o2.getBuySideMDInputEntry().getMdEntryBidPxValue() == null)
					return -1;
				return o1.getBuySideMDInputEntry().getMdEntryBidPxValue().compareTo(o2.getBuySideMDInputEntry().getMdEntryBidPxValue()) * -1;

			}
		};

		buySideQuotePageEditorBidDepthTableModel = new BuySideQuotePageEditorDepthTableModel(buySideQuotePageEditor.getBuySideQuotePage(), comparator,
				buySideQuotePageEditor.getMainPanel());
		if (security != null)
			buySideQuotePageEditorBidDepthTableModel.setSecurity(security.getId());
		bidTable.setModel(buySideQuotePageEditorBidDepthTableModel);
		quotePageBidDepthTableRenderer = new QuotePageBidDepthTableRenderer(buySideQuotePageEditor.getBuySideQuotePage());
		bidTable.setDefaultRenderer(Object.class, quotePageBidDepthTableRenderer);
		bidTable.setShowHorizontalLines(false);
		bidTable.setShowVerticalLines(false);
		bidTable.setIntercellSpacing(new Dimension(0, 0));
		bidTable.setAutoscrolls(false);
		bidTable.setRowHeight(25);
		bidTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		bidTable.setBackground(backGround3);
		bidTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		bidTable.setFillsViewportHeight(true);

		bidTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {

				buySideQuotePageEditorBidDepthTableModel.setMouseOverRow(-1);

				bidTable.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				if (security != null && e.getButton() != MouseEvent.BUTTON1
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canExecute(buySideBook)) {

					JPopupMenu tablePopup = new JPopupMenu();

					JMenuItem sellMenuItem = new JMenuItem("Sell " + security.getName());
					sellMenuItem.setEnabled(true);
					sellMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(sellMenuItem);

					sellMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							Side side = Side.BID;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorBidDepthTableModel
									.getBuySideQuotePageEntry(bidTable.rowAtPoint(e.getPoint()));
							if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								if (security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidPxValue();
								}
								BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
								buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
								buySideNewOrderSingleEntry.setSecurity(security.getId());
								buySideNewOrderSingleEntry.setSide(side);
								buySideNewOrderSingleEntry.setLimit(limit);

								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getInterfaceValue());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());

								openTicket(buySideNewOrderSingleEntry);

							}
						}

					});

					JMenuItem openMenuItem = new JMenuItem("Open " + security.getName());
					openMenuItem.setEnabled(true);
					openMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(openMenuItem);

					openMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							buySideQuotePageEditor.getMainPanel().openBusinessObject(security.getName());

						}
					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}

			}

			@Override
			public void mouseClicked(final MouseEvent e) {

				if (e.getClickCount() == 2) {
					FSecurity selectedSecurity = security;
					if (selectedSecurity != null) {
						final int row = topTable.rowAtPoint(e.getPoint());
						if (row >= 0) {

							Side side = Side.BID;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorBidDepthTableModel.getValueAt(row, 1);
							if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								if (selectedSecurity.getSecurityDetails().getPriceQuoteMethod() != null
										&& selectedSecurity.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryBidPxValue();
								}
								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getInterfaceValue());

								BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
								buySideNewOrderSingleEntry.setSecurity(selectedSecurity.getId());
								buySideNewOrderSingleEntry.setSide(side);
								buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());
								buySideNewOrderSingleEntry.setLimit(limit);
								openTicket(buySideNewOrderSingleEntry);
							}

						}
					}
				}

			}

		});

		bidTable.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {

			}

			@Override
			public void mouseMoved(final MouseEvent e) {

				int row = bidTable.rowAtPoint(e.getPoint());

				buySideQuotePageEditorBidDepthTableModel.setMouseOverRow(row);

				bidTable.repaint();
			}
		});

		buySideQuotePageEditorBidDepthTableModel.setTable(bidTable);

		for (int i = 0; i < bidTable.getColumnCount(); i++) {
			GradientTableHeaderRenderer gradientTableHeaderRenderer = new GradientTableHeaderRenderer(5, (bidBackGround).darker(), bidBackGround, Color.WHITE);
			if (i == 0)
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			else
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
			bidTable.getColumnModel().getColumn(i).setHeaderRenderer(gradientTableHeaderRenderer);
			if (i > 0)
				bidTable.getColumnModel().getColumn(i).setPreferredWidth(100);
			else
				bidTable.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
		buySideQuotePageEditorBidDepthTableModel.setTable(bidTable);
		leftScrollPane.setViewportView(bidTable);

		rightScrollPane.getViewport().setBackground(backGround3);

		askBackGround = new Color(0, 153, 0);
		if (buySideQuotePageEditor.getBuySideQuotePage().getDisplayStyle() == 1)
			askBackGround = new Color(0, 128, 36);

		askLabel.setBright(askBackGround);
		askLabel.setDark(askBackGround.darker());

		askTable = new JTable();
		askTable.setTableHeader(new JTableHeader(askTable.getColumnModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, askBackGround, width / 2.F, 25, (askBackGround).darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}
		});

		askTable.getTableHeader().setReorderingAllowed(false);

		askTable.getTableHeader().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {

				int width = 0;

				for (int i = 0; i < askTable.getColumnCount() - 1; i++) {
					width = width + askTable.getColumnModel().getColumn(i).getWidth();
				}

				buySideQuotePageEditorAskDepthTableModel.setMinWidth((int) rightScrollPane.getViewport().getWidth() - width);

			}

		});

		askTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		Comparator<BuySideQuotePageEntry> comparator2 = new Comparator<BuySideQuotePageEntry>() {

			@Override
			public int compare(BuySideQuotePageEntry o1, BuySideQuotePageEntry o2) {

				if (o1.getBuySideMDInputEntry().getMdEntryAskPxValue() == null && o2.getBuySideMDInputEntry().getMdEntryAskPxValue() == null)
					return 0;
				if (o1.getBuySideMDInputEntry().getMdEntryAskPxValue() == null && o2.getBuySideMDInputEntry().getMdEntryAskPxValue() != null)
					return 1;
				if (o1.getBuySideMDInputEntry().getMdEntryAskPxValue() != null && o2.getBuySideMDInputEntry().getMdEntryAskPxValue() == null)
					return -1;
				return o1.getBuySideMDInputEntry().getMdEntryAskPxValue().compareTo(o2.getBuySideMDInputEntry().getMdEntryAskPxValue());

			}
		};

		buySideQuotePageEditorAskDepthTableModel = new BuySideQuotePageEditorDepthTableModel(buySideQuotePageEditor.getBuySideQuotePage(), comparator2,
				buySideQuotePageEditor.getMainPanel());
		if (security != null)
			buySideQuotePageEditorAskDepthTableModel.setSecurity(security.getId());
		askTable.setModel(buySideQuotePageEditorAskDepthTableModel);
		quotePageAskDepthTableRenderer = new QuotePageAskDepthTableRenderer(buySideQuotePageEditor.getBuySideQuotePage());
		askTable.setDefaultRenderer(Object.class, quotePageAskDepthTableRenderer);
		askTable.setShowHorizontalLines(false);
		askTable.setShowVerticalLines(false);
		askTable.setIntercellSpacing(new Dimension(0, 0));
		askTable.setAutoscrolls(false);
		askTable.setRowHeight(25);
		askTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		askTable.setBackground(backGround3);
		askTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		askTable.setFillsViewportHeight(true);

		askTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {

				buySideQuotePageEditorAskDepthTableModel.setMouseOverRow(-1);

				askTable.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				if (security != null && e.getButton() != MouseEvent.BUTTON1
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canExecute(buySideBook)) {

					JPopupMenu tablePopup = new JPopupMenu();

					JMenuItem buyMenuItem = new JMenuItem("Buy " + security.getName());
					buyMenuItem.setEnabled(true);
					buyMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(buyMenuItem);

					buyMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e1) {

							Side side = Side.ASK;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel
									.getBuySideQuotePageEntry(askTable.rowAtPoint(e.getPoint()));
							if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								if (security.getSecurityDetails().getPriceQuoteMethod() != null
										&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskPxValue();
								}

								BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
								buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
								buySideNewOrderSingleEntry.setSecurity(security.getId());
								buySideNewOrderSingleEntry.setSide(side);
								buySideNewOrderSingleEntry.setLimit(limit);

								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getInterfaceValue());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());

								openTicket(buySideNewOrderSingleEntry);
							}

						}
					});

					JMenuItem openMenuItem = new JMenuItem("Open " + security.getName());
					openMenuItem.setEnabled(true);
					openMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(openMenuItem);

					openMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							buySideQuotePageEditor.getMainPanel().openBusinessObject(security.getName());

						}
					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseClicked(final MouseEvent e) {

				FSecurity selectedSecurity = security;

				if (e.getClickCount() == 2) {
					if (selectedSecurity != null) {
						final int row = topTable.rowAtPoint(e.getPoint());
						if (row >= 0) {

							Side side = Side.ASK;
							Double limit = null;
							BuySideQuotePageEntry buySideQuotePageEntry = (BuySideQuotePageEntry) buySideQuotePageEditorAskDepthTableModel.getValueAt(row, 1);
							if (buySideQuotePageEntry != null && buySideQuotePageEntry.getBuySideMDInputEntry() instanceof BuySideCounterpartyMDInputEntry) {
								if (selectedSecurity.getSecurityDetails().getPriceQuoteMethod() != null
										&& selectedSecurity.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskYieldValue();
								}
								else {
									limit = buySideQuotePageEntry.getBuySideMDInputEntry().getMdEntryAskPxValue();
								}
								Counterparty counterparty = buySideQuotePageEditor
										.getMainPanel()
										.getCounterpartyTreeDialog()
										.getCounterpartyForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getCounterpartyValue());
								AbstractInitiator fixInitiator = buySideQuotePageEditor
										.getMainPanel()
										.getInitiatorTreeDialog()
										.getAbstractInitiatorForId(
												((BuySideCounterpartyMDInputEntry) buySideQuotePageEntry.getBuySideMDInputEntry()).getInterfaceValue());

								BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
								buySideNewOrderSingleEntry.setSecurity(selectedSecurity.getId());
								buySideNewOrderSingleEntry.setSide(side);
								buySideNewOrderSingleEntry.setBuySideBook(buySideBook.getId());
								if (counterparty != null)
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
								if (fixInitiator != null)
									buySideNewOrderSingleEntry.setMarketInterface(fixInitiator.getId());
								buySideNewOrderSingleEntry.setLimit(limit);
								openTicket(buySideNewOrderSingleEntry);
							}
						}
					}
				}

			}

		});

		askTable.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {

			}

			@Override
			public void mouseMoved(final MouseEvent e) {

				int row = askTable.rowAtPoint(e.getPoint());

				buySideQuotePageEditorAskDepthTableModel.setMouseOverRow(row);

				askTable.repaint();
			}
		});

		buySideQuotePageEditorAskDepthTableModel.setTable(askTable);

		for (int i = 0; i < askTable.getColumnCount(); i++) {
			GradientTableHeaderRenderer gradientTableHeaderRenderer = new GradientTableHeaderRenderer(5, (askBackGround).darker(), askBackGround, Color.WHITE);
			if (i == 0)
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			else
				gradientTableHeaderRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
			askTable.getColumnModel().getColumn(i).setHeaderRenderer(gradientTableHeaderRenderer);
			if (i > 0)
				askTable.getColumnModel().getColumn(i).setPreferredWidth(100);
			else
				askTable.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
		buySideQuotePageEditorAskDepthTableModel.setTable(askTable);
		rightScrollPane.setViewportView(askTable);

	}

	/**
	 * On update buy side md input entry response.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public void onUpdateBuySideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		buySideQuotePageEditorMonitorTableModel.updateBuySideMDInputEntry(updateBuySideMDInputEntryResponse);
		buySideQuotePageEditorBidDepthTableModel.updateBuySideMDInputEntry(updateBuySideMDInputEntryResponse);
		buySideQuotePageEditorAskDepthTableModel.updateBuySideMDInputEntry(updateBuySideMDInputEntryResponse);

	}

	/**
	 * Close abstract business object editor.
	 */
	public void closeAbstractBusinessObjectEditor() {

		visible = false;
	}

	/**
	 * Gets the update buy side md input entry response.
	 *
	 * @return the update buy side md input entry response
	 */
	public UpdateBuySideMDInputEntryResponse getUpdateBuySideMDInputEntryResponse() {

		// TODO Auto-generated method stub
		return buySideQuotePageEditorMonitorTableModel.getUpdateBuySideMDInputEntryResponse();
	}

	private void openTicket(final BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		final BuySideBookEditor buySideBookEditor = openBuySideBook();

		if (buySideBookEditor != null) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					buySideBookEditor.addNewOrderSingle(buySideNewOrderSingleEntry);

				}
			});
		}
	}

	private void openTicket(final net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.Side side,
			final List<Counterparty> counterparties, final long security) {

		final BuySideBookEditor buySideBookEditor = openBuySideBook();

		if (buySideBookEditor != null) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					buySideBookEditor.addQuoteRequest(side, counterparties, security);

				}
			});
		}

	}

	private BuySideBookEditor openBuySideBook() {

		BuySideBookEditor buySideBookEditor = null;

		for (Dockable dock : buySideQuotePageEditor.getMainPanel().getNamedDockables()) {
			if (dock instanceof BuySideBookEditor) {

				BuySideBookEditor abstractBusinessObjectEditor = (BuySideBookEditor) dock;
				if (abstractBusinessObjectEditor.getAbstractBusinessObject().getId() == buySideBook.getId()) {
					buySideBookEditor = abstractBusinessObjectEditor;
					dock.getController().setFocusedDockable(dock, true);
				}
			}
		}

		if (buySideBookEditor == null) {

			AbstractBusinessObjectEditor abstractBusinessObjectEditor = buySideQuotePageEditor.getMainPanel().addBusinessObjectEditor(buySideBook, true);
			if (abstractBusinessObjectEditor instanceof BuySideBookEditor)
				return (BuySideBookEditor) abstractBusinessObjectEditor;
			return null;
		}

		return buySideBookEditor;

	}

	/* (non-Javadoc)
	 * @see javax.swing.JSplitPane#getDividerLocation()
	 */
	@Override
	public int getDividerLocation() {

		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() == false)
			return getHeight();
		return super.getDividerLocation();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JSplitPane#getLastDividerLocation()
	 */
	@Override
	public int getLastDividerLocation() {

		if (buySideQuotePageEditor.getBuySideQuotePage().getShowMarketDepth() == false)
			return getHeight();
		return super.getLastDividerLocation();

	}

}
