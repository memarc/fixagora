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
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import net.sourceforge.fixagora.basis.client.view.ColoredCheckBoxIcon;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideBookEditorMonitorRowFilter;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideBookEditorMonitorTableModel;
import net.sourceforge.fixagora.sellside.client.view.dialog.NewOrderSingleDialog;
import net.sourceforge.fixagora.sellside.client.view.dialog.SellSideQuoteRequestDialog;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.OpenSellSideBookResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideQuoteRequestResponse;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;

import org.apache.log4j.Logger;

/**
 * The Class SellSideBookEditorMonitor.
 */
public class SellSideBookEditorMonitor extends JPanel {

	private static final long serialVersionUID = 1L;

	private SellSideBookEditor sellSideBookEditor = null;

	private JTable table;

	private SellSideBookEditorMonitorTableModel sellSideQuotePageEditorMonitorTableModel;

	private boolean visible = true;

	private Color backGround1 = new Color(0, 153, 0);

	private Color backGround2 = (new Color(0, 153, 0)).darker();

	private Color backGround3 = null;

	private Color foreground = Color.WHITE;

	private SellSideBookTableRenderer quotePageMonitorTableRenderer;

	private JScrollPane scrollPane = null;

	private JCheckBox hideCancelCheckBox = null;

	private JCheckBox hideCompleteFilledCheckBox = null;

	private SellSideBookEditorMonitorRowFilter sellSideBookEditorMonitorRowFilter = new SellSideBookEditorMonitorRowFilter();

	private QuoteCrawler crawlerPanel = null;

	private int counter = 0;

	private Timer timer = null;

	/** The second counter. */
	protected int secondCounter = 0;

	private static Logger log = Logger.getLogger(SellSideBookEditorMonitor.class);

	/**
	 * Instantiates a new sell side book editor monitor.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public SellSideBookEditorMonitor(final SellSideBookEditor sellSideBookEditor) {

		super();

		this.sellSideBookEditor = sellSideBookEditor;

		setLayout(new BorderLayout());

		scrollPane = new JScrollPane() {

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

		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		add(scrollPane, BorderLayout.CENTER);

		GradientPanel gradientPanel = new GradientPanel();

		add(gradientPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_gradientPanel = new GridBagLayout();
		gbl_gradientPanel.columnWidths = new int[] { 166, 86, 0, 0 };
		gbl_gradientPanel.rowHeights = new int[] { 23, 0 };
		gbl_gradientPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_gradientPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		gradientPanel.setLayout(gbl_gradientPanel);

		hideCancelCheckBox = new JCheckBox("Hide Canceled/Rejected");
		hideCancelCheckBox.setIcon(new ColoredCheckBoxIcon(Color.WHITE));
		hideCancelCheckBox.setForeground(Color.WHITE);
		hideCancelCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		hideCancelCheckBox.setOpaque(false);
		GridBagConstraints gbc_hideCancelCheckBox = new GridBagConstraints();
		gbc_hideCancelCheckBox.anchor = GridBagConstraints.WEST;
		gbc_hideCancelCheckBox.insets = new Insets(0, 10, 0, 5);
		gbc_hideCancelCheckBox.gridx = 0;
		gbc_hideCancelCheckBox.gridy = 0;
		gradientPanel.add(hideCancelCheckBox, gbc_hideCancelCheckBox);

		hideCancelCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				sellSideBookEditorMonitorRowFilter.setHideCancel(hideCancelCheckBox.isSelected());
				sellSideQuotePageEditorMonitorTableModel.fireTableDataChanged();

			}
		});

		hideCompleteFilledCheckBox = new JCheckBox("Hide Done");
		hideCompleteFilledCheckBox.setIcon(new ColoredCheckBoxIcon(Color.WHITE));
		hideCompleteFilledCheckBox.setOpaque(false);
		hideCompleteFilledCheckBox.setForeground(Color.WHITE);
		hideCompleteFilledCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		hideCompleteFilledCheckBox.setVerifyInputWhenFocusTarget(false);
		GridBagConstraints gbc_hideCompleteFilledCheckBox = new GridBagConstraints();
		gbc_hideCompleteFilledCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_hideCompleteFilledCheckBox.anchor = GridBagConstraints.WEST;
		gbc_hideCompleteFilledCheckBox.gridx = 1;
		gbc_hideCompleteFilledCheckBox.gridy = 0;
		gradientPanel.add(hideCompleteFilledCheckBox, gbc_hideCompleteFilledCheckBox);

		crawlerPanel = new QuoteCrawler(sellSideBookEditor);
		crawlerPanel.setPreferredSize(new Dimension(10, 30));
		crawlerPanel.setOpaque(false);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		gradientPanel.add(crawlerPanel, gbc_panel);

		hideCompleteFilledCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				sellSideBookEditorMonitorRowFilter.setCompleteFilled(hideCompleteFilledCheckBox.isSelected());
				sellSideQuotePageEditorMonitorTableModel.fireTableDataChanged();
			}
		});

		initTable();

		scrollPane.getViewport().addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				adjust();
			}

			@Override
			public void componentShown(final ComponentEvent e) {

				adjust();
			}
		});

		timer = new Timer(50, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				crawlerPanel.crawl();
				
				secondCounter++;

				if (sellSideQuotePageEditorMonitorTableModel.isRepaintRequired()) {
					
					counter++;
					secondCounter=0;
					
					if (counter > 10) {
						sellSideBookEditor.toggleWarning();
						counter = 0;
					}

					if (counter % 2 == 0)
						table.repaint(0, 0, table.getColumnModel().getColumn(0).getWidth(), table.getVisibleRect().height);

				}
				else if(secondCounter==20)
				{
					secondCounter=0;
					table.repaint(0, 0, table.getColumnModel().getColumn(0).getWidth(), table.getVisibleRect().height);
				}

				if (!visible)
					timer.stop();

			}

		});

		// timer.setCoalesce(false);

		timer.start();

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		initTable();

	}

	private void adjust() {

		try {
			int width = 0;

			for (int i = 0; i < table.getColumnCount() - 1; i++) {
				width = width + table.getColumnModel().getColumn(i).getWidth();
			}

			sellSideQuotePageEditorMonitorTableModel.setMinWidth((int) scrollPane.getViewport().getSize().getWidth() - width - 3);
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	private synchronized void initTable() {

		switch (sellSideBookEditor.getSellSideBook().getDisplayStyle()) {
			case 0:
				backGround3 = Color.BLACK;
				backGround1 = new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				break;
			case 1:
				// backGround1 = new Color(128, 0, 28);
				// backGround2 = (new Color(128, 0, 28)).darker();

				backGround1 = new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				backGround3 = new Color(0, 22, 102);

				break;
			default:
				backGround3 = new Color(255, 243, 204);
				backGround1 = new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				break;
		}

		scrollPane.getViewport().setBackground(backGround3);

		table = new JTable();
		table.setTableHeader(new JTableHeader(table.getColumnModel()) {

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

		table.getTableHeader().setReorderingAllowed(false);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		sellSideQuotePageEditorMonitorTableModel = new SellSideBookEditorMonitorTableModel(sellSideBookEditor.getSellSideBook(),
				sellSideBookEditor.getMainPanel());
		table.setModel(sellSideQuotePageEditorMonitorTableModel);

		TableRowSorter<SellSideBookEditorMonitorTableModel> tableRowSorter = new TableRowSorter<SellSideBookEditorMonitorTableModel>(
				sellSideQuotePageEditorMonitorTableModel);
		tableRowSorter.setRowFilter(sellSideBookEditorMonitorRowFilter);
		table.setRowSorter(tableRowSorter);

		quotePageMonitorTableRenderer = new SellSideBookTableRenderer(sellSideBookEditor.getSellSideBook());

		table.setDefaultRenderer(Object.class, quotePageMonitorTableRenderer);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.setBackground(backGround3);
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {

				sellSideQuotePageEditorMonitorTableModel.setMouseOverRow(-1);

				sellSideBookEditor.getBasisClientConnector().setHighlightKey(null);

				table.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2 && sellSideBookEditor.getBasisClientConnector().getFUser().canExecute(sellSideBookEditor.getSellSideBook())) {
					AbstractSellSideEntry abstractSellSideEntry = sellSideQuotePageEditorMonitorTableModel.getEntryForRow();
					if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {

						final SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = (SellSideNewOrderSingleEntry) abstractSellSideEntry;

						if (sellSideNewOrderSingleEntry != null
								&& (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED_PENDING || sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.MODIFIED
										|| sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW || sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED)) {

							final NewOrderSingleDialog newOrderSingleDialog = sellSideBookEditor.getNewOrderSingleDialog(sellSideNewOrderSingleEntry);
							if (newOrderSingleDialog == null) {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										try {
											NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(sellSideNewOrderSingleEntry,
													sellSideBookEditor);
											newOrderSingleDialog2.validate();
											newOrderSingleDialog2.setVisible(true);
										}
										catch (Exception e) {
											log.error("Bug", e);
										}
									}
								});
							}
							else {
								java.awt.EventQueue.invokeLater(new Runnable() {

									@Override
									public void run() {

										newOrderSingleDialog.validate();
										newOrderSingleDialog.toFront();
										newOrderSingleDialog.repaint();
									}
								});

							}
						}
						
					}
					if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry) {

						final SellSideQuoteRequestEntry sellSideQuoteRequestEntry = (SellSideQuoteRequestEntry) abstractSellSideEntry;

						if (sellSideQuoteRequestEntry != null
								&& !sellSideQuoteRequestEntry.isDone()) {

							final SellSideQuoteRequestDialog sellSideQuoteRequestDialog = sellSideBookEditor.getSellSideQuoteRequestDialog(sellSideQuoteRequestEntry);
							if (sellSideQuoteRequestDialog == null) {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										try {
											SellSideQuoteRequestDialog sellSideQuoteRequestDialog2 = new SellSideQuoteRequestDialog(sellSideQuoteRequestEntry,
													sellSideBookEditor);
											sellSideQuoteRequestDialog2.validate();
											sellSideQuoteRequestDialog2.setVisible(true);
										}
										catch (Exception e) {
											log.error("Bug", e);
										}
									}
								});
							}
							else {
								java.awt.EventQueue.invokeLater(new Runnable() {

									@Override
									public void run() {

										sellSideQuoteRequestDialog.validate();
										sellSideQuoteRequestDialog.toFront();
										sellSideQuoteRequestDialog.repaint();
									}
								});

							}
						}
					}
					
				}
				sellSideQuotePageEditorMonitorTableModel.resetUpdate();
				sellSideBookEditor.stopWarning();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1
						&& sellSideBookEditor.getBasisClientConnector().getFUser().canExecute(sellSideBookEditor.getSellSideBook())) {
					AbstractSellSideEntry abstractSellSideEntry = sellSideQuotePageEditorMonitorTableModel.getEntryForRow();
					if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry) {

						final SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = (SellSideNewOrderSingleEntry) abstractSellSideEntry;

						if (sellSideNewOrderSingleEntry != null
								&& (sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED_PENDING || sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.MODIFIED
										|| sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW || sellSideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED)) {

							JPopupMenu ticketPopup = new JPopupMenu();

							JMenuItem ticketMenuItem = new JMenuItem("Open ticket");
							ticketMenuItem.setEnabled(true);
							ticketMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
							ticketPopup.add(ticketMenuItem);

							ticketMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									final NewOrderSingleDialog newOrderSingleDialog = sellSideBookEditor.getNewOrderSingleDialog(sellSideNewOrderSingleEntry);
									if (newOrderSingleDialog == null) {
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {

												try {
													NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(sellSideNewOrderSingleEntry,
															sellSideBookEditor);
													newOrderSingleDialog2.validate();
													newOrderSingleDialog2.setVisible(true);
												}
												catch (Exception e) {
													log.error("Bug", e);
												}
											}
										});
									}
									else {
										java.awt.EventQueue.invokeLater(new Runnable() {

											@Override
											public void run() {

												newOrderSingleDialog.validate();
												newOrderSingleDialog.toFront();
												newOrderSingleDialog.repaint();
											}
										});

									}

								}
							});

							ticketPopup.show((JComponent) e.getSource(), e.getX(), e.getY());
						}

					}
					if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry) {

						final SellSideQuoteRequestEntry sellSideQuoteRequestEntry = (SellSideQuoteRequestEntry) abstractSellSideEntry;

						if (sellSideQuoteRequestEntry != null
								&& !sellSideQuoteRequestEntry.isDone()) {

							JPopupMenu ticketPopup = new JPopupMenu();

							JMenuItem ticketMenuItem = new JMenuItem("Open ticket");
							ticketMenuItem.setEnabled(true);
							ticketMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
							ticketPopup.add(ticketMenuItem);

							ticketMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									final SellSideQuoteRequestDialog sellSideQuoteRequestDialog = sellSideBookEditor.getSellSideQuoteRequestDialog(sellSideQuoteRequestEntry);
									if (sellSideQuoteRequestDialog == null) {
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {

												try {
													SellSideQuoteRequestDialog sellSideQuoteRequestDialog2 = new SellSideQuoteRequestDialog(sellSideQuoteRequestEntry,
															sellSideBookEditor);
													sellSideQuoteRequestDialog2.validate();
													sellSideQuoteRequestDialog2.setVisible(true);
												}
												catch (Exception e) {
													log.error("Bug", e);
												}
											}
										});
									}
									else {
										java.awt.EventQueue.invokeLater(new Runnable() {

											@Override
											public void run() {

												sellSideQuoteRequestDialog.validate();
												sellSideQuoteRequestDialog.toFront();
												sellSideQuoteRequestDialog.repaint();
											}
										});

									}

								}
							});

							ticketPopup.show((JComponent) e.getSource(), e.getX(), e.getY());
						}

					}
				}
			}

		});

		table.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {

			}

			@Override
			public void mouseMoved(final MouseEvent e) {

				int row = table.rowAtPoint(e.getPoint());
				if (row != -1)
					row = table.convertRowIndexToModel(row);

				if (sellSideQuotePageEditorMonitorTableModel.setMouseOverRow(row)) {
					
					

					AbstractSellSideEntry abstractSellSideEntry = sellSideQuotePageEditorMonitorTableModel.getEntryForRow();

					if (abstractSellSideEntry instanceof SellSideNewOrderSingleEntry)
						sellSideBookEditor.getBasisClientConnector().setHighlightKey(abstractSellSideEntry.getOrderId());
					else if (abstractSellSideEntry instanceof SellSideQuoteRequestEntry)
						sellSideBookEditor.getBasisClientConnector().setHighlightKey(((SellSideQuoteRequestEntry)abstractSellSideEntry).getQuoteReqId());
					else
						sellSideBookEditor.getBasisClientConnector().setHighlightKey(null);

					table.repaint();

				}
			}
		});

		sellSideQuotePageEditorMonitorTableModel.setTable(table);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5, backGround2, backGround1, foreground));
			table.getColumnModel().getColumn(i).setPreferredWidth(150);
		}
		sellSideQuotePageEditorMonitorTableModel.setTable(table);
		scrollPane.setViewportView(table);

	}

	/**
	 * Close abstract business object editor.
	 */
	public void closeAbstractBusinessObjectEditor() {

		visible = false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
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

	/**
	 * Adds the new order single.
	 *
	 * @param sellSideNewOrderSingleEntry the sell side new order single entry
	 */
	public void addNewOrderSingle(final SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry) {

		if (sellSideBookEditor.getBasisClientConnector().getFUser().canExecute(sellSideBookEditor.getSellSideBook())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					try {
						NewOrderSingleDialog newOrderSingleDialog = new NewOrderSingleDialog(sellSideNewOrderSingleEntry, sellSideBookEditor);
						newOrderSingleDialog.validate();
						newOrderSingleDialog.setVisible(true);
					}
					catch (Exception e) {
						log.error("Bug", e);
					}
				}
			});
		}
	}

	/**
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public void onNewOrderSingleResponse(final NewOrderSingleResponse newOrderSingleResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				sellSideQuotePageEditorMonitorTableModel.onNewOrderSingleResponse(newOrderSingleResponse);
				adjust();

			}
		});

	}

	/**
	 * Clear crawler.
	 *
	 * @param securities the securities
	 */
	public synchronized void clearCrawler(Set<Long> securities) {

		crawlerPanel.clearCrawler(securities);

	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public synchronized void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

		crawlerPanel.onUpdateSellSideMDInputEntryResponse(updateSellSideMDInputEntryResponse);

	}

	/**
	 * On open sell side book response.
	 *
	 * @param openSellSideBookResponse the open sell side book response
	 */
	public void onOpenSellSideBookResponse(final OpenSellSideBookResponse openSellSideBookResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				sellSideQuotePageEditorMonitorTableModel.onOpenSellSideBookResponse(openSellSideBookResponse);

			}
		});
	}

	/**
	 * On sell side quote request response.
	 *
	 * @param sellSideQuoteRequestResponse the sell side quote request response
	 */
	public void onSellSideQuoteRequestResponse(final SellSideQuoteRequestResponse sellSideQuoteRequestResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				sellSideQuotePageEditorMonitorTableModel.onSellSideQuoteRequestResponse(sellSideQuoteRequestResponse);
				adjust();

			}
		});

	}

}
