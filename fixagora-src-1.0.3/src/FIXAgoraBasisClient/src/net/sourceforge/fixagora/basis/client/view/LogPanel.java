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
package net.sourceforge.fixagora.basis.client.view;

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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.log.LogTableModel;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;

/**
 * The Class LogPanel.
 */
public class LogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table = null;
	private LogTableModel logTableModel = null;

	/**
	 * Instantiates a new log panel.
	 *
	 * @param basisClientConnector the basis client connector
	 * @param loginResponse the login response
	 */
	public LogPanel(final BasisClientConnector basisClientConnector, LoginResponse loginResponse) {

		super();

		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(new Insets(0, 1, 0, 1)));

		final JPanel filterPanel = new TabGradientPanel();
		filterPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.rowHeights = new int[] { 15, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		filterPanel.setLayout(gbl_panel);
		add(filterPanel, BorderLayout.NORTH);

		final JLabel filterLabel = new JLabel("Blotter");
		filterLabel.setForeground(Color.WHITE);
		filterLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		filterLabel.setIcon(new ImageIcon(LogPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/network.png")));
		final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		filterPanel.add(filterLabel, gbc_lblNewLabel);

		final JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, Color.GRAY, width / 2.F, 26, Color.BLACK);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);
				
				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height-26);

				getUI().paint(graphics2D, this);
			}
		};
		
		scrollPane.setOpaque(true);
		scrollPane.getViewport().setBackground(new Color(255, 243, 204));
		scrollPane.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), Color.GRAY));
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setTableHeader(new JTableHeader(table.getColumnModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, Color.GRAY, width / 2.F, 25, Color.BLACK);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}
		});
		
		logTableModel = new LogTableModel(basisClientConnector, loginResponse);
		
		table.setModel(logTableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(170);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		table.getColumnModel().getColumn(2).setPreferredWidth(250);
		table.setDefaultRenderer(Object.class, new LogTableRenderer());
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		logTableModel.setTable(table);
		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {

				
				basisClientConnector.setHighlightKey(null);

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
				
				LogEntry logEntry  = logTableModel.getEntryForRow(row);

				if(logEntry!=null)
					basisClientConnector.setHighlightKey(logEntry.getHighlightKey());
				else
					basisClientConnector.setHighlightKey(null);
				
				table.repaint();
			}
		});
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(25));

		scrollPane.setViewportView(table);
		scrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				int width = 0;
				width = width + table.getColumnModel().getColumn(0).getWidth();
				width = width + table.getColumnModel().getColumn(1).getWidth();
				width = width + table.getColumnModel().getColumn(2).getWidth();

				logTableModel.setMinWidth((int) scrollPane.getSize().getWidth() - width - 3);
			}

			@Override
			public void componentShown(final ComponentEvent e) {

			}
		});

	}

}
