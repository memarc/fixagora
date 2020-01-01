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
package net.sourceforge.fixagora.sap.client.view.editor;

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

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.sap.client.model.editor.SAPTradeCaptureEditorMonitorTableModel;
import net.sourceforge.fixagora.sap.shared.communication.OpenSAPTradeCaptureResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionRequest.SAPTradeCaptureAction;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureActionResponse.ActionResponse;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntryResponse;

/**
 * The Class SAPTradeCaptureEditorMonitor.
 */
public class SAPTradeCaptureEditorMonitor extends JPanel {

	private static final long serialVersionUID = 1L;

	private SAPTradeCaptureEditor tradeCaptureEditor = null;

	private JTable table;

	private SAPTradeCaptureEditorMonitorTableModel tradeCaptureEditorMonitorTableModel;

	private Color backGround1 = Color.GRAY;

	private Color backGround2 = Color.BLACK;

	private Color backGround3 = new Color(255, 243, 204);

	private Color foreground = Color.WHITE;

	private SAPTradeCaptureTableRenderer tradeCaptureTableRenderer;

	private JScrollPane scrollPane = null;
	private JButton testButton;

	private JButton removeButton;

	private JButton exportButton;
	
	private boolean updateInProgress = false;
	
	private boolean sapConnectorExist = false;

	/**
	 * Instantiates a new sAP trade capture editor monitor.
	 *
	 * @param sellSideBookEditor the sell side book editor
	 */
	public SAPTradeCaptureEditorMonitor(final SAPTradeCaptureEditor sellSideBookEditor) {

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
		gbl_gradientPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0};
		gradientPanel.setLayout(gbl_gradientPanel);
		
		testButton = new JButton("Test");
		testButton.setMargin(new Insets(2, 5, 2, 5));
		testButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		testButton.setPreferredSize(new Dimension(100, 25));
		testButton.setActionCommand("OK");
		testButton.setIcon(new ImageIcon(SAPTradeCaptureEditorMonitor.class
				.getResource("/net/sourceforge/fixagora/sap/client/view/images/16x16/sapimport.png")));
		testButton.setEnabled(false);
		GridBagConstraints gbc_testButton = new GridBagConstraints();
		gbc_testButton.insets = new Insets(5, 5, 5, 5);
		gbc_testButton.anchor = GridBagConstraints.EAST;
		gbc_testButton.gridx = 1;
		gbc_testButton.gridy = 0;
		gradientPanel.add(testButton, gbc_testButton);
		
		testButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				tradeCaptureEditor.getBasisClientConnector().sendRequest(new SAPTradeCaptureActionRequest(tradeCaptureEditor.getSAPTradeCapture().getId(), SAPTradeCaptureAction.TEST, RequestIDManager.getInstance().getID()));
				updateInProgress = true;
				buttonCheck();

			}
		});

		exportButton = new JButton("Export");
		exportButton.setMargin(new Insets(2, 5, 2, 5));
		exportButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		exportButton.setPreferredSize(new Dimension(100, 25));
		exportButton.setActionCommand("OK");
		exportButton.setIcon(new ImageIcon(SAPTradeCaptureEditorMonitor.class
				.getResource("/net/sourceforge/fixagora/sap/client/view/images/16x16/sapimport.png")));
		exportButton.setEnabled(false);
		GridBagConstraints gbc_exportButton = new GridBagConstraints();
		gbc_exportButton.insets = new Insets(5, 5, 5, 5);
		gbc_exportButton.anchor = GridBagConstraints.EAST;
		gbc_exportButton.gridx = 2;
		gbc_exportButton.gridy = 0;
		gradientPanel.add(exportButton, gbc_exportButton);
		
		exportButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				tradeCaptureEditor.getBasisClientConnector().sendRequest(new SAPTradeCaptureActionRequest(tradeCaptureEditor.getSAPTradeCapture().getId(), SAPTradeCaptureAction.EXPORT, RequestIDManager.getInstance().getID()));
				updateInProgress = true;
				buttonCheck();

			}
		});
		
		removeButton = new JButton("Remove");
		removeButton.setMargin(new Insets(2, 5, 2, 5));
		removeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeButton.setPreferredSize(new Dimension(100, 25));
		removeButton.setActionCommand("REMOVE");
		removeButton.setIcon(new ImageIcon(SAPTradeCaptureEditorMonitor.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		removeButton.setEnabled(false);
		GridBagConstraints gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.insets = new Insets(5, 5, 5, 25);
		gbc_removeButton.anchor = GridBagConstraints.EAST;
		gbc_removeButton.gridx = 3;
		gbc_removeButton.gridy = 0;
		gradientPanel.add(removeButton, gbc_removeButton);
		
		removeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				tradeCaptureEditor.getBasisClientConnector().sendRequest(new SAPTradeCaptureActionRequest(tradeCaptureEditor.getSAPTradeCapture().getId(), SAPTradeCaptureAction.REMOVE, RequestIDManager.getInstance().getID()));
				updateInProgress = true;
				buttonCheck();
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

		tradeCaptureEditorMonitorTableModel = new SAPTradeCaptureEditorMonitorTableModel(tradeCaptureEditor);
		table.setModel(tradeCaptureEditorMonitorTableModel);

		tradeCaptureTableRenderer = new SAPTradeCaptureTableRenderer(tradeCaptureEditor);

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

				SAPTradeCaptureEntry sapTradeCaptureEntry = tradeCaptureEditorMonitorTableModel.getEntryForRow();

				if (sapTradeCaptureEntry != null) {
					tradeCaptureEditor.getBasisClientConnector().setHighlightKey(
							sapTradeCaptureEntry.getOrderId() + ";;;" + sapTradeCaptureEntry.getTradeId());
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
	 * On sap trade capture entry response.
	 *
	 * @param sapTradeCaptureEntryResponse the sap trade capture entry response
	 */
	public void onSAPTradeCaptureEntryResponse(final SAPTradeCaptureEntryResponse sapTradeCaptureEntryResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				tradeCaptureEditorMonitorTableModel.onSAPTradeCaptureEntryResponse(sapTradeCaptureEntryResponse);
				
				buttonCheck();
			}
		});

	}

	/**
	 * On open sap trade capture response.
	 *
	 * @param openSAPTradeCaptureResponse the open sap trade capture response
	 */
	public void onOpenSAPTradeCaptureResponse(final OpenSAPTradeCaptureResponse openSAPTradeCaptureResponse) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				tradeCaptureEditorMonitorTableModel.onOpenSAPTradeCaptureResponse(openSAPTradeCaptureResponse);
				
				updateInProgress = openSAPTradeCaptureResponse.isExportInProgress();
				
				sapConnectorExist = openSAPTradeCaptureResponse.isSapConnectorExist();

				buttonCheck();
				
			}
		});

	}

	/**
	 * On sap trade capture action response.
	 *
	 * @param sapTradeCaptureActionResponse the sap trade capture action response
	 */
	public void onSAPTradeCaptureActionResponse(SAPTradeCaptureActionResponse sapTradeCaptureActionResponse) {

		if(sapTradeCaptureActionResponse.getActionResponse()==ActionResponse.STOPPED)
			updateInProgress = false;
		
		buttonCheck();
		
	}

	private void buttonCheck()
	{
		if (tradeCaptureEditor.getBasisClientConnector().getFUser().canExecute(tradeCaptureEditor.getAbstractBusinessObject())
				&& tradeCaptureEditorMonitorTableModel.getTradeCount() > 0  && !updateInProgress) {
			testButton.setEnabled(sapConnectorExist&&tradeCaptureEditorMonitorTableModel.isExportable());
			removeButton.setEnabled(true);
			exportButton.setEnabled(sapConnectorExist&&tradeCaptureEditorMonitorTableModel.isExportable());
		}
		else
		{
			testButton.setEnabled(false);
			removeButton.setEnabled(false);
			exportButton.setEnabled(false);
		}
	}
	
}
