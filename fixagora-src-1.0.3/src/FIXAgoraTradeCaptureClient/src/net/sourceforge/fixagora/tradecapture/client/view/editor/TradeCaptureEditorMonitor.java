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
package net.sourceforge.fixagora.tradecapture.client.view.editor;

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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.component.DaySpinner;
import net.sourceforge.fixagora.tradecapture.client.model.editor.TradeCaptureEditorMonitorTableModel;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntry;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;

/**
 * The Class TradeCaptureEditorMonitor.
 */
public class TradeCaptureEditorMonitor extends JPanel implements CellEditor {

	private static final long serialVersionUID = 1L;

	private TradeCaptureEditor tradeCaptureEditor = null;

	private JTable table;

	private TradeCaptureEditorMonitorTableModel tradeCaptureEditorMonitorTableModel;

	private Color backGround1 = Color.GRAY;

	private Color backGround2 = Color.BLACK;

	private Color backGround3 = new Color(255, 243, 204);

	private Color foreground = Color.WHITE;

	private TradeCaptureTableRenderer tradeCaptureTableRenderer;

	private JScrollPane scrollPane = null;

	private JLabel tradeDateLabel;

	private DaySpinner dateSpinner;

	/**
	 * Instantiates a new trade capture editor monitor.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public TradeCaptureEditorMonitor(final TradeCaptureEditor sellSideBookEditor) {

		super();

		this.tradeCaptureEditor = sellSideBookEditor;

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
		gbl_gradientPanel.columnWeights = new double[] { 0.0, 1.0 };
		gradientPanel.setLayout(gbl_gradientPanel);

		tradeDateLabel = new JLabel("Trade Date");
		tradeDateLabel.setForeground(Color.WHITE);
		tradeDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradeDateLabel.setOpaque(false);
		GridBagConstraints gbc_tradeDateLabel = new GridBagConstraints();
		gbc_tradeDateLabel.anchor = GridBagConstraints.WEST;
		gbc_tradeDateLabel.insets = new Insets(0, 25, 0, 10);
		gbc_tradeDateLabel.gridx = 0;
		gbc_tradeDateLabel.gridy = 0;
		gradientPanel.add(tradeDateLabel, gbc_tradeDateLabel);

		dateSpinner = new DaySpinner(this, new Color(255, 243, 204));
		dateSpinner.setValue(new Date());
		dateSpinner.setPreferredSize(new Dimension(150, 25));
		dateSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		GridBagConstraints gbc_dateSpinner = new GridBagConstraints();
		gbc_dateSpinner.anchor = GridBagConstraints.WEST;
		gbc_dateSpinner.insets = new Insets(5, 0, 5, 5);
		gbc_dateSpinner.gridx = 1;
		gbc_dateSpinner.gridy = 0;
		gradientPanel.add(dateSpinner, gbc_dateSpinner);

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

				//adjust();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				adjust();
			}
		});

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

		tradeCaptureEditorMonitorTableModel.setMinWidth((int) scrollPane.getViewport().getSize().getWidth() - width - 3);
	}

	private synchronized void initTable() {

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

		tradeCaptureEditorMonitorTableModel = new TradeCaptureEditorMonitorTableModel(tradeCaptureEditor);
		table.setModel(tradeCaptureEditorMonitorTableModel);

		tradeCaptureTableRenderer = new TradeCaptureTableRenderer(tradeCaptureEditor.getTradeCapture());

		table.setDefaultRenderer(Object.class, tradeCaptureTableRenderer);
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

				tradeCaptureEditorMonitorTableModel.setMouseOverRow(-1);
				
				tradeCaptureEditor.getBasisClientConnector().setHighlightKey(null);

				table.repaint();
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

				tradeCaptureEditorMonitorTableModel.setMouseOverRow(row);
				
				TradeCaptureEntry tradeCaptureEntry = tradeCaptureEditorMonitorTableModel.getEntryForRow();
				
				if(tradeCaptureEntry!=null)
				{
					tradeCaptureEditor.getBasisClientConnector().setHighlightKey(tradeCaptureEntry.getOrderId()+";;;"+tradeCaptureEntry.getTradeId());
				}
				else
					tradeCaptureEditor.getBasisClientConnector().setHighlightKey(null);

				table.repaint();
			}
		});

		tradeCaptureEditorMonitorTableModel.setTable(table);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5, backGround2, backGround1, foreground));
			table.getColumnModel().getColumn(i).setPreferredWidth(150);
		}
		tradeCaptureEditorMonitorTableModel.setTable(table);
		scrollPane.setViewportView(table);

		adjust();

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
	 * On trade capture entry response.
	 *
	 * @param tradeCaptureEntryResponse the trade capture entry response
	 */
	public void onTradeCaptureEntryResponse(final TradeCaptureEntryResponse tradeCaptureEntryResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				adjust();
				tradeCaptureEditorMonitorTableModel.onTradeCaptureEntryResponse(tradeCaptureEntryResponse);
				tradeCaptureEditor.updateIcon();
			}
		});

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable(EventObject anEvent) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@Override
	public boolean shouldSelectCell(EventObject anEvent) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {

		Calendar calendar = dateSpinner.getCalendarValue();
		if (calendar != null) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			OpenTradeCaptureResponse openTradeCaptureResponse = (OpenTradeCaptureResponse) tradeCaptureEditor.getBasisClientConnector().sendRequest(
					new OpenTradeCaptureRequest(tradeCaptureEditor.getTradeCapture().getId(), calendar.getTimeInMillis(), RequestIDManager.getInstance()
							.getID()));
			onOpenTradeCaptureResponse(openTradeCaptureResponse);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	@Override
	public void cancelCellEditing() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void addCellEditorListener(CellEditorListener l) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void removeCellEditorListener(CellEditorListener l) {

		// TODO Auto-generated method stub

	}

	/**
	 * On open trade capture response.
	 *
	 * @param openTradeCaptureResponse the open trade capture response
	 */
	public void onOpenTradeCaptureResponse(final OpenTradeCaptureResponse openTradeCaptureResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				tradeCaptureEditorMonitorTableModel.onOpenTradeCaptureResponse(openTradeCaptureResponse);
				adjust();

			}
		});

	}

	/**
	 * Clear updated.
	 */
	public void clearUpdated() {

		tradeCaptureEditorMonitorTableModel.clearUpdated();
		
	}
	
	/**
	 * Gets the updated.
	 *
	 * @return the updated
	 */
	public int getUpdated() {

		return tradeCaptureEditorMonitorTableModel.getUpdated();
		
	}

}
