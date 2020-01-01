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
import java.util.List;
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
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.buyside.client.model.dnd.BuySideBookDropTarget;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideBookEditorMonitorRowFilter;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideBookEditorMonitorTableModel;
import net.sourceforge.fixagora.buyside.client.view.dialog.NewOrderSingleDialog;
import net.sourceforge.fixagora.buyside.client.view.dialog.QuoteRequestDialog;
import net.sourceforge.fixagora.buyside.client.view.dialog.QuoteResponseDialog;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.communication.OpenBuySideBookResponse;
import net.sourceforge.fixagora.buyside.shared.communication.UpdateBuySideMDInputEntryResponse;

import org.apache.log4j.Logger;

/**
 * The Class BuySideBookEditorMonitor.
 */
public class BuySideBookEditorMonitor extends JPanel {

	private static final long serialVersionUID = 1L;

	private BuySideBookEditor buySideBookEditor = null;

	private JTable table;

	private BuySideBookEditorMonitorTableModel buySideQuotePageEditorMonitorTableModel;

	private boolean visible = true;

	private Color backGround1 = new Color(0, 153, 0);

	private Color backGround2 = (new Color(0, 153, 0)).darker();

	private Color backGround3 = null;

	private Color foreground = Color.WHITE;

	private BuySideBookTableRenderer quotePageMonitorTableRenderer;

	private JScrollPane scrollPane = null;

	private JCheckBox hideCancelCheckBox = null;

	private JCheckBox hideCompleteFilledCheckBox = null;

	private BuySideBookEditorMonitorRowFilter buySideBookEditorMonitorRowFilter = new BuySideBookEditorMonitorRowFilter();

	private QuoteCrawler crawlerPanel;

	private int counter = 0;

	private int secondCounter = 0;

	private Timer timer;

	private static Logger log = Logger.getLogger(BuySideBookEditorMonitor.class);

	/**
	 * Instantiates a new buy side book editor monitor.
	 *
	 * @param buySideBookEditor the buy side book editor
	 */
	public BuySideBookEditorMonitor(final BuySideBookEditor buySideBookEditor) {

		super();

		this.buySideBookEditor = buySideBookEditor;

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

				buySideBookEditorMonitorRowFilter.setHideCancel(hideCancelCheckBox.isSelected());
				buySideQuotePageEditorMonitorTableModel.fireTableDataChanged();

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

		crawlerPanel = new QuoteCrawler(buySideBookEditor);
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

				buySideBookEditorMonitorRowFilter.setCompleteFilled(hideCompleteFilledCheckBox.isSelected());
				buySideQuotePageEditorMonitorTableModel.fireTableDataChanged();
			}
		});

		initTable();

		scrollPane.addComponentListener(new ComponentListener() {

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

				if (buySideQuotePageEditorMonitorTableModel.isRepaintRequired()) {

					secondCounter = 0;
					counter++;

					if (counter > 10) {
						buySideBookEditor.toggleWarning();
						counter = 0;
					}

					if (counter % 2 == 0)
						table.repaint(0, 0, table.getColumnModel().getColumn(0).getWidth(), table.getVisibleRect().height);

				}
				else if (secondCounter == 20) {
					secondCounter = 0;
					table.repaint(0, 0, table.getColumnModel().getColumn(0).getWidth(), table.getVisibleRect().height);
				}

				if (!visible)
					timer.stop();

			}

		});

		timer.start();

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		initTable();

	}

	private void adjust() {

		int width = 0;

		for (int i = 0; i < table.getColumnCount() - 1; i++) {
			width = width + table.getColumnModel().getColumn(i).getWidth();
		}

		buySideQuotePageEditorMonitorTableModel.setMinWidth((int) scrollPane.getViewport().getSize().getWidth() - width - 3);
	}

	private void initTable() {

		switch (buySideBookEditor.getBuySideBook().getDisplayStyle()) {
			case 0:
				backGround3 = Color.BLACK;
				backGround1 = new Color(0, 153, 0);
				backGround2 = (new Color(0, 153, 0)).darker();
				break;
			case 1:
				backGround1 = new Color(0, 128, 36);
				backGround2 = (new Color(0, 128, 36)).darker();
				backGround3 = new Color(0, 22, 102);

				break;
			default:
				backGround3 = new Color(255, 243, 204);
				backGround1 = new Color(0, 153, 0);
				backGround2 = (new Color(0, 153, 0)).darker();
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

		buySideQuotePageEditorMonitorTableModel = new BuySideBookEditorMonitorTableModel(buySideBookEditor.getBuySideBook(), buySideBookEditor.getMainPanel());
		table.setModel(buySideQuotePageEditorMonitorTableModel);

		quotePageMonitorTableRenderer = new BuySideBookTableRenderer(buySideBookEditor.getBuySideBook());

		TableRowSorter<BuySideBookEditorMonitorTableModel> tableRowSorter = new TableRowSorter<BuySideBookEditorMonitorTableModel>(
				buySideQuotePageEditorMonitorTableModel);
		tableRowSorter.setRowFilter(buySideBookEditorMonitorRowFilter);
		table.setRowSorter(tableRowSorter);
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

		new BuySideBookDropTarget(table, buySideBookEditor);

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {

				buySideQuotePageEditorMonitorTableModel.setMouseOverRow(-1);

				buySideBookEditor.getBasisClientConnector().setHighlightKey(null);

				table.repaint();

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2 && buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {

					AbstractBuySideEntry abstractBuySideEntry = buySideQuotePageEditorMonitorTableModel.getEntryForRow();

					if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry) {
						final BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = (BuySideNewOrderSingleEntry) abstractBuySideEntry;
						if (buySideNewOrderSingleEntry != null
								&& (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW_PENDING
										|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REPLACE_PENDING
										|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW || buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED)) {

							final NewOrderSingleDialog newOrderSingleDialog = buySideBookEditor.getNewOrderSingleDialog(buySideNewOrderSingleEntry);
							if (newOrderSingleDialog == null) {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										try {
											NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(buySideNewOrderSingleEntry, buySideBookEditor);
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

					else if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {
						final BuySideQuoteRequestEntry buySideQuoteRequestEntry = (BuySideQuoteRequestEntry) abstractBuySideEntry;
						if (buySideQuoteRequestEntry != null
								&& (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW_PENDING
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.QUOTED
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER || buySideQuoteRequestEntry
										.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING)) {

							final QuoteResponseDialog quoteResponseDialog = buySideBookEditor.getQuoteResponseDialog(buySideQuoteRequestEntry);
							if (quoteResponseDialog == null) {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										try {
											QuoteResponseDialog quoteResponseDialog2 = new QuoteResponseDialog(buySideQuoteRequestEntry, buySideBookEditor);
											quoteResponseDialog2.validate();
											quoteResponseDialog2.setVisible(true);
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

										quoteResponseDialog.validate();
										quoteResponseDialog.toFront();
										quoteResponseDialog.repaint();
									}
								});

							}
						}
					}
				}

				buySideQuotePageEditorMonitorTableModel.resetUpdate();
				buySideBookEditor.stopWarning();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1
						&& buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {

					JPopupMenu ticketPopup = new JPopupMenu();

					JMenuItem newOrderMenuItem = new JMenuItem("New order");
					newOrderMenuItem.setEnabled(true);
					newOrderMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					ticketPopup.add(newOrderMenuItem);

					newOrderMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {

									try {
										BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
										buySideNewOrderSingleEntry.setSide(BuySideNewOrderSingleEntry.Side.ASK);
										buySideNewOrderSingleEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
										NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(buySideNewOrderSingleEntry, buySideBookEditor);
										newOrderSingleDialog2.validate();
										newOrderSingleDialog2.setVisible(true);
									}
									catch (Exception e) {
										log.error("Bug", e);
									}

								}
							});

						}
					});

					JMenuItem newInquiryMenuItem = new JMenuItem("New inquiry");
					newInquiryMenuItem.setEnabled(true);
					newInquiryMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					ticketPopup.add(newInquiryMenuItem);

					newInquiryMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {

									try {

										QuoteRequestDialog quoteRequestDialog = new QuoteRequestDialog(null, 0L, BuySideQuoteRequestEntry.Side.ASK,
												buySideBookEditor);
										quoteRequestDialog.validate();
										quoteRequestDialog.setVisible(true);
									}
									catch (Exception e) {
										log.error("Bug", e);
									}

								}
							});

						}
					});

					AbstractBuySideEntry abstractBuySideEntry = buySideQuotePageEditorMonitorTableModel.getEntryForRow();

					if (abstractBuySideEntry instanceof BuySideNewOrderSingleEntry) {
						final BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = (BuySideNewOrderSingleEntry) abstractBuySideEntry;

						if (buySideNewOrderSingleEntry != null
								&& (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.DONE_FOR_DAY
										|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.FILLED || buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED)) {

							JMenuItem resubmitOrderMenuItem = new JMenuItem("Resubmit order");
							resubmitOrderMenuItem.setEnabled(true);
							resubmitOrderMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
							ticketPopup.add(resubmitOrderMenuItem);

							resubmitOrderMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {

											try {
												BuySideNewOrderSingleEntry buySideNewOrderSingleEntry2 = new BuySideNewOrderSingleEntry();
												buySideNewOrderSingleEntry2.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
												buySideNewOrderSingleEntry2.setSecurity(buySideNewOrderSingleEntry.getSecurity());
												buySideNewOrderSingleEntry2.setSide(buySideNewOrderSingleEntry.getSide());
												buySideNewOrderSingleEntry2.setMarketInterface(buySideNewOrderSingleEntry.getMarketInterface());
												buySideNewOrderSingleEntry2.setCounterparty(buySideNewOrderSingleEntry.getCounterparty());
												buySideNewOrderSingleEntry2.setLimit(buySideNewOrderSingleEntry.getLimit());
												buySideNewOrderSingleEntry2.setMinQuantity(buySideNewOrderSingleEntry.getMinQuantity());
												buySideNewOrderSingleEntry2.setOrderQuantity(buySideNewOrderSingleEntry.getOrderQuantity());
												buySideNewOrderSingleEntry2.setTifDate(buySideNewOrderSingleEntry.getTifDate());
												NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(buySideNewOrderSingleEntry2,
														buySideBookEditor);
												newOrderSingleDialog2.validate();
												newOrderSingleDialog2.setVisible(true);
											}
											catch (Exception e) {
												log.error("Bug", e);
											}

										}
									});
								}
							});

						}

						if (buySideNewOrderSingleEntry != null
								&& (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW_PENDING
										|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REPLACE_PENDING
										|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.NEW || buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.PARTIALLY_FILLED)) {

							JMenuItem ticketMenuItem = new JMenuItem("Open ticket");
							ticketMenuItem.setEnabled(true);
							ticketMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
							ticketPopup.add(ticketMenuItem);

							ticketMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									final NewOrderSingleDialog newOrderSingleDialog = buySideBookEditor.getNewOrderSingleDialog(buySideNewOrderSingleEntry);
									if (newOrderSingleDialog == null) {
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {

												try {
													NewOrderSingleDialog newOrderSingleDialog2 = new NewOrderSingleDialog(buySideNewOrderSingleEntry,
															buySideBookEditor);
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

						}

					}
					if (abstractBuySideEntry instanceof BuySideQuoteRequestEntry) {

						final BuySideQuoteRequestEntry buySideQuoteRequestEntry = (BuySideQuoteRequestEntry) abstractBuySideEntry;
						if (buySideQuoteRequestEntry != null
								&& (buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW_PENDING
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.NEW
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.QUOTED
										|| buySideQuoteRequestEntry.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER || buySideQuoteRequestEntry
										.getOrderStatus() == net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus.COUNTER_PENDING)) {

							JMenuItem ticketMenuItem = new JMenuItem("Open ticket");
							ticketMenuItem.setEnabled(true);
							ticketMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
							ticketPopup.add(ticketMenuItem);

							ticketMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									final QuoteResponseDialog quoteResponseDialog = buySideBookEditor.getQuoteResponseDialog(buySideQuoteRequestEntry);
									if (quoteResponseDialog == null) {
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {

												try {
													QuoteResponseDialog quoteResponseDialog2 = new QuoteResponseDialog(buySideQuoteRequestEntry,
															buySideBookEditor);
													quoteResponseDialog2.validate();
													quoteResponseDialog2.setVisible(true);
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

												quoteResponseDialog.validate();
												quoteResponseDialog.toFront();
												quoteResponseDialog.repaint();
											}
										});

									}
								}

							});
						}
					}

					ticketPopup.show((JComponent) e.getSource(), e.getX(), e.getY());

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

				boolean repaint = buySideQuotePageEditorMonitorTableModel.setMouseOverRow(row);

				if (repaint) {
					AbstractBuySideEntry buySideNewOrderSingleEntry = buySideQuotePageEditorMonitorTableModel.getEntryForRow();

					if (buySideNewOrderSingleEntry instanceof BuySideQuoteRequestEntry)
						buySideBookEditor.getBasisClientConnector().setHighlightKey(((BuySideQuoteRequestEntry) buySideNewOrderSingleEntry).getQuoteReqId());
					else if (buySideNewOrderSingleEntry != null)
						buySideBookEditor.getBasisClientConnector().setHighlightKey(buySideNewOrderSingleEntry.getOrderId());
					else
						buySideBookEditor.getBasisClientConnector().setHighlightKey(null);

					table.repaint();
				}
			}
		});

		buySideQuotePageEditorMonitorTableModel.setTable(table);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5, backGround2, backGround1, foreground));
			table.getColumnModel().getColumn(i).setPreferredWidth(150);
		}
		buySideQuotePageEditorMonitorTableModel.setTable(table);
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
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 */
	public void addNewOrderSingle(final BuySideNewOrderSingleEntry buySideNewOrderSingleEntry) {

		if (buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					try {
						NewOrderSingleDialog newOrderSingleDialog = new NewOrderSingleDialog(buySideNewOrderSingleEntry, buySideBookEditor);
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
	public void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		buySideQuotePageEditorMonitorTableModel.onNewOrderSingleResponse(newOrderSingleResponse);
		adjust();
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
	 * On update buy side md input entry response.
	 *
	 * @param updateBuySideMDInputEntryResponse the update buy side md input entry response
	 */
	public synchronized void onUpdateBuySideMDInputEntryResponse(UpdateBuySideMDInputEntryResponse updateBuySideMDInputEntryResponse) {

		crawlerPanel.onUpdateBuySideMDInputEntryResponse(updateBuySideMDInputEntryResponse);

	}

	/**
	 * On open buy side book response.
	 *
	 * @param openBuyBookResponse the open buy book response
	 */
	public void onOpenBuySideBookResponse(OpenBuySideBookResponse openBuyBookResponse) {

		buySideQuotePageEditorMonitorTableModel.onOpenBuySideBookResponse(openBuyBookResponse);

	}

	/**
	 * Adds the new order single.
	 *
	 * @param side the side
	 * @param counterparties the counterparties
	 * @param security the security
	 */
	public void addNewOrderSingle(final Side side, final List<Counterparty> counterparties, final long security) {

		if (buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					try {
						QuoteRequestDialog quoteRequestDialog = new QuoteRequestDialog(counterparties, security, side, buySideBookEditor);
						quoteRequestDialog.validate();
						quoteRequestDialog.setVisible(true);
					}
					catch (Exception e) {
						log.error("Bug", e);
					}

				}
			});
		}
	}

	/**
	 * On buy side quote request response.
	 *
	 * @param quoteRequestResponse the quote request response
	 */
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse) {

		buySideQuotePageEditorMonitorTableModel.onBuySideQuoteRequestResponse(quoteRequestResponse);
		adjust();

	}

}
