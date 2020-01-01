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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.sellside.client.model.editor.SellSideQuotePageEditorMonitorTableModel;
import net.sourceforge.fixagora.sellside.shared.communication.UpdateSellSideMDInputEntryResponse;

import org.apache.log4j.Logger;

/**
 * The Class SellSideQuotePageEditorMonitor.
 */
public class SellSideQuotePageEditorMonitor extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private SellSideQuotePageEditor sellSideQuotePageEditor = null;

	private JTable table;

	private SellSideQuotePageEditorMonitorTableModel sellSideQuotePageEditorMonitorTableModel;

	private boolean visible = true;

	private Color backGround1 = Color.GRAY;

	private Color backGround2 = Color.BLACK;

	private Color backGround3;

	private Color foreground;

	private QuotePageMonitorTableRenderer quotePageMonitorTableRenderer;
	
	private static Logger log = Logger.getLogger(SellSideQuotePageEditorMonitor.class);

	/**
	 * Instantiates a new sell side quote page editor monitor.
	 *
	 * @param sellSideQuotePageEditor the sell side quote page editor
	 */
	public SellSideQuotePageEditorMonitor(SellSideQuotePageEditor sellSideQuotePageEditor) {

		super();

		this.sellSideQuotePageEditor = sellSideQuotePageEditor;

		setBorder(new EmptyBorder(0, 0, 0, 0));

		initTable();
		
		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				int width = sellSideQuotePageEditorMonitorTableModel.getFirstColumnWidth();

				for (int i = 1; i < table.getColumnCount() - 1; i++) {
					width = width + table.getColumnModel().getColumn(i).getWidth();
				}

				sellSideQuotePageEditorMonitorTableModel.setMinWidth((int) getViewport().getSize().getWidth() - width - 3);
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
						if (sellSideQuotePageEditorMonitorTableModel.isRepaintRequired())
							table.repaint();
					}
					catch (InterruptedException e) {
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

		initTable();

	}

	private void initTable() {

		switch (sellSideQuotePageEditor.getSellSideQuotePage().getDisplayStyle()) {
			case 0:
				backGround1 =  new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				backGround3 = Color.BLACK;
				foreground = Color.WHITE;
				break;
			case 1:
				backGround1 =  new Color(204, 0, 0);
				backGround2 = (new Color(204, 0, 0)).darker();
				backGround3 = new Color(0, 22, 102);
				foreground = Color.WHITE;

				break;
			default:
				backGround1 = Color.GRAY;
				backGround2 = Color.BLACK;
				backGround3 = new Color(255, 243, 204);
				foreground = Color.WHITE;
				break;
		}

		getViewport().setBackground(backGround3);

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

		sellSideQuotePageEditorMonitorTableModel = new SellSideQuotePageEditorMonitorTableModel(sellSideQuotePageEditor);
		table.setModel(sellSideQuotePageEditorMonitorTableModel);

		quotePageMonitorTableRenderer = new QuotePageMonitorTableRenderer(sellSideQuotePageEditor.getSellSideQuotePage());
		
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

				sellSideQuotePageEditorMonitorTableModel.setMouseOverRow(row);

				table.repaint();
			}
		});

		sellSideQuotePageEditorMonitorTableModel.setTable(table);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5, backGround2, backGround1, foreground));
			if(i==0)
				table.getColumnModel().getColumn(i).setPreferredWidth(sellSideQuotePageEditorMonitorTableModel.getFirstColumnWidth());
			else
				table.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
		sellSideQuotePageEditorMonitorTableModel.setTable(table);
		setViewportView(table);

	}

	/**
	 * On update sell side md input entry response.
	 *
	 * @param updateSellSideMDInputEntryResponse the update sell side md input entry response
	 */
	public void onUpdateSellSideMDInputEntryResponse(UpdateSellSideMDInputEntryResponse updateSellSideMDInputEntryResponse) {

			sellSideQuotePageEditorMonitorTableModel.updateSellSideMDInputEntry(updateSellSideMDInputEntryResponse);

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
	 * Gets the update sell side md input entry response.
	 *
	 * @return the update sell side md input entry response
	 */
	public UpdateSellSideMDInputEntryResponse getUpdateSellSideMDInputEntryResponse() {

		return sellSideQuotePageEditorMonitorTableModel.getUpdateSellSideMDInputEntryResponse();
	}

}
