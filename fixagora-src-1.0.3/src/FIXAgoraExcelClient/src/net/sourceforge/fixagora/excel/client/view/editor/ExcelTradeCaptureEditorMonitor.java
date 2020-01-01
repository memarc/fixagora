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
package net.sourceforge.fixagora.excel.client.view.editor;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.excel.client.model.editor.ExcelTradeCaptureEditorMonitorTableModel;
import net.sourceforge.fixagora.excel.client.view.dialog.ExcelExportDialog;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.OpenExcelTradeCaptureResponse;

/**
 * The Class ExcelTradeCaptureEditorMonitor.
 */
public class ExcelTradeCaptureEditorMonitor extends JPanel {

	private static final long serialVersionUID = 1L;

	private ExcelTradeCaptureEditor tradeCaptureEditor = null;

	private JTable table;

	private ExcelTradeCaptureEditorMonitorTableModel tradeCaptureEditorMonitorTableModel;

	private Color backGround1 = new Color(204, 216, 255);

	private Color backGround2 = (new Color(204, 216, 255)).darker();

	private Color backGround3 = new Color(255, 243, 204);

	private Color foreground = Color.BLACK;

	private ExcelTradeCaptureTableRenderer tradeCaptureTableRenderer;

	private JScrollPane scrollPane = null;
	private JButton exportButton;

	/**
	 * Instantiates a new excel trade capture editor monitor.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public ExcelTradeCaptureEditorMonitor(final ExcelTradeCaptureEditor sellSideBookEditor) {

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
		gbl_gradientPanel.columnWeights = new double[] { 1.0, 0.0 };
		gradientPanel.setLayout(gbl_gradientPanel);

		exportButton = new JButton("Export ...");
		exportButton.setMargin(new Insets(2, 5, 2, 5));
		exportButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		exportButton.setPreferredSize(new Dimension(100, 25));
		exportButton.setActionCommand("OK");
		exportButton.setIcon(new ImageIcon(ExcelTradeCaptureEditorMonitor.class
				.getResource("/net/sourceforge/fixagora/excel/client/view/images/16x16/fileimport.png")));
		exportButton.setEnabled(false);
		GridBagConstraints gbc_exportButton = new GridBagConstraints();
		gbc_exportButton.insets = new Insets(5, 5, 5, 25);
		gbc_exportButton.anchor = GridBagConstraints.EAST;
		gbc_exportButton.gridx = 1;
		gbc_exportButton.gridy = 0;
		gradientPanel.add(exportButton, gbc_exportButton);
		
		exportButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				ExcelExportDialog excelExportDialog = new ExcelExportDialog(tradeCaptureEditorMonitorTableModel, tradeCaptureEditor);
				excelExportDialog.setVisible(true);
				
			}
		});

		initTable();

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		initTable();

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

		tradeCaptureEditorMonitorTableModel = new ExcelTradeCaptureEditorMonitorTableModel(tradeCaptureEditor);
		table.setModel(tradeCaptureEditorMonitorTableModel);

		tradeCaptureTableRenderer = new ExcelTradeCaptureTableRenderer();

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

				ExcelTradeCaptureEntry excelTradeCaptureEntry = tradeCaptureEditorMonitorTableModel.getEntryForRow();

				if (excelTradeCaptureEntry != null) {
					tradeCaptureEditor.getBasisClientConnector().setHighlightKey(
							excelTradeCaptureEntry.getOrderId() + ";;;" + excelTradeCaptureEntry.getTradeId());
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
	 * On excel trade capture entry response.
	 *
	 * @param excelTradeCaptureEntryResponse the excel trade capture entry response
	 */
	public void onExcelTradeCaptureEntryResponse(final ExcelTradeCaptureEntryResponse excelTradeCaptureEntryResponse) {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				tradeCaptureEditorMonitorTableModel.onExcelTradeCaptureEntryResponse(excelTradeCaptureEntryResponse);
				if (tradeCaptureEditor.getBasisClientConnector().getFUser().canExecute(tradeCaptureEditor.getAbstractBusinessObject())
						&& tradeCaptureEditorMonitorTableModel.getTradeCount() > 0) {
					exportButton.setEnabled(true);
				}
				else
				{
					exportButton.setEnabled(false);
				}
			}
		});

	}

	/**
	 * On open excel trade capture response.
	 *
	 * @param openExcelTradeCaptureResponse the open excel trade capture response
	 */
	public void onOpenExcelTradeCaptureResponse(final OpenExcelTradeCaptureResponse openExcelTradeCaptureResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				tradeCaptureEditorMonitorTableModel.onOpenExcelTradeCaptureResponse(openExcelTradeCaptureResponse);
				if (tradeCaptureEditor.getBasisClientConnector().getFUser().canExecute(tradeCaptureEditor.getAbstractBusinessObject())
						&& tradeCaptureEditorMonitorTableModel.getTradeCount() > 0) {
					exportButton.setEnabled(true);
				}
				else
				{
					exportButton.setEnabled(false);
				}

			}
		});

	}

}
