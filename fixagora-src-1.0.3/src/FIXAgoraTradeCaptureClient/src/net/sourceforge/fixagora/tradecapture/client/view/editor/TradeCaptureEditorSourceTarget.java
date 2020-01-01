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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorTableRenderer;
import net.sourceforge.fixagora.tradecapture.client.model.editor.AssignedTradeCaptureSourceTableModel;
import net.sourceforge.fixagora.tradecapture.client.model.editor.AssignedTradeCaptureTargetTableModel;
import net.sourceforge.fixagora.tradecapture.client.view.dialog.TradeCaptureEditorSourceDialog;
import net.sourceforge.fixagora.tradecapture.client.view.dialog.TradeCaptureEditorTargetDialog;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureSource;
import net.sourceforge.fixagora.tradecapture.shared.persistence.AssignedTradeCaptureTarget;

/**
 * The Class TradeCaptureEditorSourceTarget.
 */
public class TradeCaptureEditorSourceTarget extends JPanel {

	private JButton addTargetButton;
	private JTable targetTable;
	private TradeCaptureEditor sellSideBookEditor;
	private AssignedTradeCaptureTargetTableModel assignedTradeCaptureTargetTableModel;
	private JButton removeTargetButton;
	private JScrollPane targetScrollPane;
	private JButton addSourceButton;
	private JScrollPane sourceScrollPane;
	private JTable sourceTable;
	private AssignedTradeCaptureSourceTableModel assignedTradeCaptureSourceTableModel;
	private JButton removeSourceButton;

	/**
	 * Instantiates a new trade capture editor source target.
	 *
	 * @param tradeCaptureEditor the trade capture editor
	 */
	public TradeCaptureEditorSourceTarget(final TradeCaptureEditor tradeCaptureEditor) {

		this.sellSideBookEditor = tradeCaptureEditor;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 ,0};
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		addSourceButton = new JButton("Add");
		addSourceButton.setHorizontalAlignment(SwingConstants.LEFT);
		addSourceButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addSourceButton.setPreferredSize(new Dimension(100, 25));
		addSourceButton.setIcon(new ImageIcon(TradeCaptureEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(25, 25, 5, 25);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		add(addSourceButton, gbc_btnNewButton);

		sourceScrollPane = new JScrollPane(){

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
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 25, 5);
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		add(sourceScrollPane, gbc_scrollPane);

		sourceTable = new JTable();
		sourceTable.setTableHeader(new JTableHeader(sourceTable.getColumnModel()) {

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

		sourceTable.getTableHeader().setReorderingAllowed(false);

		sourceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		assignedTradeCaptureSourceTableModel = new AssignedTradeCaptureSourceTableModel(tradeCaptureEditor);
		sourceTable.setModel(assignedTradeCaptureSourceTableModel);

		sourceTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				tradeCaptureEditor.checkDirty();
			}
		});

		sourceTable.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(tradeCaptureEditor));
		sourceTable.setShowHorizontalLines(false);
		sourceTable.setShowVerticalLines(false);
		sourceTable.setIntercellSpacing(new Dimension(0, 0));
		sourceTable.setAutoscrolls(false);
		sourceTable.setRowHeight(20);
		sourceTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		sourceTable.setBackground(new Color(255, 243, 204));
		sourceTable.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		sourceTable.setFillsViewportHeight(true);

		assignedTradeCaptureSourceTableModel.setTable(sourceTable);
		for (int i = 0; i < sourceTable.getColumnCount(); i++)
			sourceTable.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));
		assignedTradeCaptureSourceTableModel.setTable(sourceTable);
		
		sourceScrollPane.setViewportView(sourceTable);

		removeSourceButton = new JButton("Remove");
		removeSourceButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeSourceButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeSourceButton.setPreferredSize(new Dimension(100, 25));
		removeSourceButton.setIcon(new ImageIcon(TradeCaptureEditorSourceTarget.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_2.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton_2.insets = new Insets(0, 25, 0, 25);
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 1;
		add(removeSourceButton, gbc_btnNewButton_2);

		removeSourceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sourceTable.getSelectionModel().getMinSelectionIndex() != -1
						&& tradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(tradeCaptureEditor.getAbstractBusinessObject())) {
					
					List<AssignedTradeCaptureSource> assignedTradeCaptureSources = new ArrayList<AssignedTradeCaptureSource>();
					for(int index: sourceTable.getSelectedRows())
						assignedTradeCaptureSources.add(assignedTradeCaptureSourceTableModel.get(index));
					for(AssignedTradeCaptureSource assignedTradeCaptureSource: assignedTradeCaptureSources)
						assignedTradeCaptureSourceTableModel.remove(assignedTradeCaptureSource);
					checkConsistency();
				}
			}
		});

		addSourceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				TradeCaptureEditorSourceDialog tradeCaptureEditorSourceDialog = new TradeCaptureEditorSourceDialog(tradeCaptureEditor.getMainPanel().getBusinessComponents());
				tradeCaptureEditorSourceDialog.setVisible(true);
				if (!tradeCaptureEditorSourceDialog.isCancelled()) {
					AssignedTradeCaptureSource assignedTradeCaptureSource = tradeCaptureEditorSourceDialog.getAssignedTradeCaptureSource();
					assignedTradeCaptureSource.setTradeCapture(tradeCaptureEditor.getTradeCapture());
					assignedTradeCaptureSourceTableModel.addAssignedTradeCaptureSource(assignedTradeCaptureSource);
					checkConsistency();
				}
			}
		});

		sourceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (sourceTable.getSelectionModel().getMinSelectionIndex() != -1
						&& tradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(tradeCaptureEditor.getAbstractBusinessObject())) {
					removeSourceButton.setEnabled(true);
				}
				else {
					removeSourceButton.setEnabled(false);
				}
			}
		});
		
		sourceScrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				assignedTradeCaptureSourceTableModel.setMinWidth((int) sourceScrollPane.getSize().getWidth() - 3);
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				componentResized(e);
			}
		});
		
		addTargetButton = new JButton("Add");
		addTargetButton.setHorizontalAlignment(SwingConstants.LEFT);
		addTargetButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addTargetButton.setPreferredSize(new Dimension(100, 25));
		addTargetButton.setIcon(new ImageIcon(TradeCaptureEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_addTargetButton = new GridBagConstraints();
		gbc_addTargetButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addTargetButton.insets = new Insets(25, 25, 5, 25);
		gbc_addTargetButton.gridx = 2;
		gbc_addTargetButton.gridy = 0;
		add(addTargetButton, gbc_addTargetButton);

		targetScrollPane = new JScrollPane(){

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
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};
		GridBagConstraints gbc_targetScrollPane = new GridBagConstraints();
		gbc_targetScrollPane.insets = new Insets(0, 0, 25, 25);
		gbc_targetScrollPane.gridheight = 2;
		gbc_targetScrollPane.fill = GridBagConstraints.BOTH;
		gbc_targetScrollPane.gridx = 3;
		gbc_targetScrollPane.gridy = 0;
		add(targetScrollPane, gbc_targetScrollPane);

		targetTable = new JTable();
		targetTable.setTableHeader(new JTableHeader(targetTable.getColumnModel()) {

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

		targetTable.getTableHeader().setReorderingAllowed(false);

		targetTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		assignedTradeCaptureTargetTableModel = new AssignedTradeCaptureTargetTableModel(tradeCaptureEditor);
		targetTable.setModel(assignedTradeCaptureTargetTableModel);

		targetTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				tradeCaptureEditor.checkDirty();
			}
		});

		targetTable.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(tradeCaptureEditor));
		targetTable.setShowHorizontalLines(false);
		targetTable.setShowVerticalLines(false);
		targetTable.setIntercellSpacing(new Dimension(0, 0));
		targetTable.setAutoscrolls(false);
		targetTable.setRowHeight(20);
		targetTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		targetTable.setBackground(new Color(255, 243, 204));
		targetTable.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		targetTable.setFillsViewportHeight(true);

		assignedTradeCaptureTargetTableModel.setTable(targetTable);
		for (int i = 0; i < targetTable.getColumnCount(); i++)
			targetTable.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));
		assignedTradeCaptureTargetTableModel.setTable(targetTable);
		
		targetScrollPane.setViewportView(targetTable);

		removeTargetButton = new JButton("Remove");
		removeTargetButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeTargetButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeTargetButton.setPreferredSize(new Dimension(100, 25));
		removeTargetButton.setIcon(new ImageIcon(TradeCaptureEditorSourceTarget.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_removeTargetButton = new GridBagConstraints();
		gbc_removeTargetButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_removeTargetButton.anchor = GridBagConstraints.NORTH;
		gbc_removeTargetButton.insets = new Insets(0, 25, 0, 25);
		gbc_removeTargetButton.gridx = 2;
		gbc_removeTargetButton.gridy = 1;
		add(removeTargetButton, gbc_removeTargetButton);

		removeTargetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (targetTable.getSelectionModel().getMinSelectionIndex() != -1
						&& tradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(tradeCaptureEditor.getAbstractBusinessObject())) {
					
					List<AssignedTradeCaptureTarget> assignedTradeCaptureTargets = new ArrayList<AssignedTradeCaptureTarget>();
					for(int index: targetTable.getSelectedRows())
						assignedTradeCaptureTargets.add(assignedTradeCaptureTargetTableModel.get(index));
					for(AssignedTradeCaptureTarget assignedTradeCaptureTarget: assignedTradeCaptureTargets)
						assignedTradeCaptureTargetTableModel.remove(assignedTradeCaptureTarget);
					checkConsistency();
				}
			}
		});

		addTargetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				TradeCaptureEditorTargetDialog tradeCaptureEditorTargetDialog = new TradeCaptureEditorTargetDialog(tradeCaptureEditor.getMainPanel().getBusinessComponents());
				tradeCaptureEditorTargetDialog.setVisible(true);
				if (!tradeCaptureEditorTargetDialog.isCancelled()) {
					AssignedTradeCaptureTarget assignedTradeCaptureTarget = tradeCaptureEditorTargetDialog.getAssignedTradeCaptureTarget();
					assignedTradeCaptureTarget.setTradeCapture(tradeCaptureEditor.getTradeCapture());
					assignedTradeCaptureTargetTableModel.addAssignedTradeCaptureTarget(assignedTradeCaptureTarget);
					checkConsistency();
				}
			}
		});

		targetTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (targetTable.getSelectionModel().getMinSelectionIndex() != -1
						&& tradeCaptureEditor.getBasisClientConnector().getFUser().canWrite(tradeCaptureEditor.getAbstractBusinessObject())) {
					removeTargetButton.setEnabled(true);
				}
				else {
					removeTargetButton.setEnabled(false);
				}
			}
		});
		
		targetScrollPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				assignedTradeCaptureTargetTableModel.setMinWidth((int) targetScrollPane.getSize().getWidth() - 3);
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				componentResized(e);
			}
		});


		updateContent();
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Save.
	 */
	public void save() {

		assignedTradeCaptureTargetTableModel.save();
		assignedTradeCaptureSourceTableModel.save();

	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return assignedTradeCaptureTargetTableModel.isDirty()||assignedTradeCaptureSourceTableModel.isDirty();
	}

	/**
	 * Update content.
	 */
	public void updateContent() {
		
		assignedTradeCaptureTargetTableModel.updateAssignedTradeCaptureTargets(sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureTargets());
		assignedTradeCaptureSourceTableModel.updateAssignedTradeCaptureSources(sellSideBookEditor.getTradeCapture().getAssignedTradeCaptureSources());

		if (sellSideBookEditor.getBasisClientConnector().getFUser().canWrite(sellSideBookEditor.getAbstractBusinessObject())) {

			targetScrollPane.getViewport().setBackground(new Color(255, 243, 204));
			addTargetButton.setEnabled(sellSideBookEditor.getMainPanel().getBusinessComponents().size()>0);
			removeTargetButton.setEnabled(false);
			addSourceButton.setEnabled(sellSideBookEditor.getMainPanel().getBusinessComponents().size()>0);
			removeSourceButton.setEnabled(false);
			sourceTable.setBackground(new Color(255, 243, 204));
			targetTable.setBackground(new Color(255, 243, 204));

		}
		else {

			targetScrollPane.getViewport().setBackground(new Color(204, 216, 255));
			addTargetButton.setEnabled(false);
			removeTargetButton.setEnabled(false);
			addSourceButton.setEnabled(false);
			removeSourceButton.setEnabled(false);
			sourceTable.setBackground(new Color(204, 216, 255));
			targetTable.setBackground(new Color(204, 216, 255));
		}

		checkConsistency();

	}

	/**
	 * Check consistency.
	 */
	public void checkConsistency() {

		sellSideBookEditor.checkDirty();
	}

}
