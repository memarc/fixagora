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
package net.sourceforge.fixagora.basis.client.view.editor;

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

import net.sourceforge.fixagora.basis.client.model.editor.ComplexEventDateTableModel;
import net.sourceforge.fixagora.basis.client.model.editor.ComplexEventTableModel;
import net.sourceforge.fixagora.basis.client.model.editor.ComplexEventTimeTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityComplexEventDateDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityComplexEventDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityComplexEventTimeDialog;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventDate;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventTime;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityComplexEvent;

/**
 * The Class FSecurityEditorComplexEvent.
 */
public class FSecurityEditorComplexEvent extends JPanel {

	private JButton addButton;
	private JTable table;
	private FSecurityEditor securityEditor;
	private ComplexEventTableModel complexEventTableModel;
	private JButton removeButton;
	private JScrollPane scrollPane;
	private JButton editButton;
	private JButton addDateButton;
	private JButton editDateButton;
	private JButton removeDateButton;
	private JScrollPane dateScrollPane;
	private JButton editTimeButton;
	private JButton removeTimeButton;
	private JScrollPane timeScrollPane;
	private JTable dateTable;
	private JTable timeTable;
	private JButton addTimeButton;
	private ComplexEventDateTableModel eventDateTableModel;
	private ComplexEventTimeTableModel eventTimeTableModel;

	/**
	 * Instantiates a new f security editor complex event.
	 *
	 * @param fSecurityEditor the f security editor
	 */
	public FSecurityEditorComplexEvent(FSecurityEditor fSecurityEditor) {

		this.securityEditor = fSecurityEditor;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 208, 0, 208, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		scrollPane = new JScrollPane(){

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
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		table = new JTable();
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

		table.getTableHeader().setReorderingAllowed(false);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		complexEventTableModel = new ComplexEventTableModel(securityEditor);
		table.setModel(complexEventTableModel);

		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				securityEditor.checkDirty();
			}
		});

		table.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(securityEditor));
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(170);
		table.getColumnModel().getColumn(5).setPreferredWidth(100);
		table.getColumnModel().getColumn(6).setPreferredWidth(100);
		table.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));

		scrollPane.setViewportView(table);
		
		
		addButton = new JButton("Add");
		addButton.setHorizontalAlignment(SwingConstants.LEFT);
		addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addButton.setPreferredSize(new Dimension(100, 25));
		addButton.setMinimumSize(new Dimension(100, 25));
		addButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(25, 25, 5, 25);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		add(addButton, gbc_btnNewButton);

		editButton = new JButton("Edit");
		editButton.setHorizontalAlignment(SwingConstants.LEFT);
		editButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		editButton.setPreferredSize(new Dimension(100, 25));
		editButton.setMinimumSize(new Dimension(100, 25));
		editButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_1.insets = new Insets(0, 25, 5, 25);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 1;
		add(editButton, gbc_btnNewButton_1);

		removeButton = new JButton("Remove");
		removeButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeButton.setPreferredSize(new Dimension(100, 25));
		removeButton.setMinimumSize(new Dimension(100, 25));
		removeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton_2.insets = new Insets(0, 25, 0, 25);
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 2;
		add(removeButton, gbc_btnNewButton_2);

		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					
					List<SecurityComplexEvent> securityComplexEvents = new ArrayList<SecurityComplexEvent>();
					for(int index: table.getSelectedRows())
						securityComplexEvents.add(complexEventTableModel.get(index));
					for(SecurityComplexEvent securityComplexEvent: securityComplexEvents)
						complexEventTableModel.remove(securityComplexEvent);
					checkConsistency();
				}
			}
		});

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SecurityComplexEventDialog securityComplexEventDialog = new SecurityComplexEventDialog(null);
				securityComplexEventDialog.setVisible(true);
				if (!securityComplexEventDialog.isCancelled()) {
					SecurityComplexEvent securityComplexEvent = securityComplexEventDialog.getSecurityComplexEvent();
					securityComplexEvent.setSecurity(securityEditor.getSecurity().getSecurityDetails());
					complexEventTableModel.addSecurityComplexEvent(securityComplexEvent);
					checkConsistency();
				}
			}
		});

		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					SecurityComplexEventDialog securityComplexEventDialog = new SecurityComplexEventDialog(complexEventTableModel.get(table.getSelectionModel().getMinSelectionIndex()));
					securityComplexEventDialog.setVisible(true);
					if (!securityComplexEventDialog.isCancelled()) {
						complexEventTableModel.modified();
						complexEventTableModel.fireTableDataChanged();
						checkConsistency();
					}
				}
			}
		});


		
		
		addDateButton = new JButton("Add");
		addDateButton.setHorizontalAlignment(SwingConstants.LEFT);
		addDateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addDateButton.setPreferredSize(new Dimension(100, 25));
		addDateButton.setMinimumSize(new Dimension(100, 25));
		addDateButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_addDateButton = new GridBagConstraints();
		gbc_addDateButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addDateButton.insets = new Insets(25, 25, 5, 25);
		gbc_addDateButton.gridx = 2;
		gbc_addDateButton.gridy = 0;
		add(addDateButton, gbc_addDateButton);

		editDateButton = new JButton("Edit");
		editDateButton.setHorizontalAlignment(SwingConstants.LEFT);
		editDateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		editDateButton.setPreferredSize(new Dimension(100, 25));
		editDateButton.setMinimumSize(new Dimension(100, 25));
		editDateButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		GridBagConstraints gbc_editDateButton = new GridBagConstraints();
		gbc_editDateButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_editDateButton.insets = new Insets(0, 25, 5, 25);
		gbc_editDateButton.gridx = 2;
		gbc_editDateButton.gridy = 1;
		add(editDateButton, gbc_editDateButton);

		removeDateButton = new JButton("Remove");
		removeDateButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeDateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeDateButton.setPreferredSize(new Dimension(100, 25));
		removeDateButton.setMinimumSize(new Dimension(100, 25));
		removeDateButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_removeDateButton = new GridBagConstraints();
		gbc_removeDateButton.anchor = GridBagConstraints.NORTH;
		gbc_removeDateButton.insets = new Insets(0, 25, 0, 25);
		gbc_removeDateButton.gridx = 2;
		gbc_removeDateButton.gridy = 2;
		add(removeDateButton, gbc_removeDateButton);
		
		dateScrollPane = new JScrollPane();
		GridBagConstraints gbc_dateScrollPane = new GridBagConstraints();
		gbc_dateScrollPane.insets = new Insets(0, 0, 25, 5);
		gbc_dateScrollPane.gridheight = 3;
		gbc_dateScrollPane.fill = GridBagConstraints.BOTH;
		gbc_dateScrollPane.gridx = 3;
		gbc_dateScrollPane.gridy = 0;
		add(dateScrollPane, gbc_dateScrollPane);
		
		dateTable = new JTable();
		dateTable.setTableHeader(new JTableHeader(dateTable.getColumnModel()) {

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

		dateTable.getTableHeader().setReorderingAllowed(false);

		dateTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		eventDateTableModel = new ComplexEventDateTableModel(complexEventTableModel);
		dateTable.setModel(eventDateTableModel);

		dateTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				securityEditor.checkDirty();
			}
		});

		dateTable.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(securityEditor));
		dateTable.setShowHorizontalLines(false);
		dateTable.setShowVerticalLines(false);
		dateTable.setIntercellSpacing(new Dimension(0, 0));
		dateTable.setAutoscrolls(false);
		dateTable.setRowHeight(20);
		dateTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		dateTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		dateTable.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		for (int i = 0; i < dateTable.getColumnCount(); i++)
			dateTable.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));

		dateScrollPane.setViewportView(dateTable);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					removeButton.setEnabled(true);

					if(table.getSelectedRowCount()==1)
					{
						addDateButton.setEnabled(true);
						editButton.setEnabled(true);
					}
					else
					{
						addDateButton.setEnabled(false);
						editButton.setEnabled(false);
					}
					
				}
				else {
					addDateButton.setEnabled(false);
					removeButton.setEnabled(false);
					editButton.setEnabled(false);
				}
				
				if (table.getSelectionModel().getMinSelectionIndex() != -1)
					eventDateTableModel.updateComplexEventDates(complexEventTableModel.get(table.getSelectionModel().getMinSelectionIndex()).getComplexEventDates());

				else
					eventDateTableModel.updateComplexEventDates(new ArrayList<ComplexEventDate>());

			}
		});
		
		removeDateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (dateTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					
					List<ComplexEventDate> complexEventDates = new ArrayList<ComplexEventDate>();
					for(int index: dateTable.getSelectedRows())
						complexEventDates.add(eventDateTableModel.get(index));
					for(ComplexEventDate complexEventDate: complexEventDates)
						eventDateTableModel.remove(complexEventDate);
					checkConsistency();
				}
			}
		});

		
		addDateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SecurityComplexEventDateDialog securityComplexEventDateDialog = new SecurityComplexEventDateDialog(null);
				securityComplexEventDateDialog.setVisible(true);
				if (!securityComplexEventDateDialog.isCancelled()) {
					ComplexEventDate complexEventDate = securityComplexEventDateDialog.getComplexEventDate();
					complexEventDate.setSecurityComplexEvent(complexEventTableModel.get(table.getSelectionModel().getMinSelectionIndex()));
					eventDateTableModel.addSecurityEvent(complexEventDate);
					checkConsistency();
				}
			}
		});

		editDateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (dateTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					SecurityComplexEventDateDialog securityComplexEventDateDialog = new SecurityComplexEventDateDialog(eventDateTableModel.get(dateTable.getSelectionModel().getMinSelectionIndex()));
					securityComplexEventDateDialog.setVisible(true);
					if (!securityComplexEventDateDialog.isCancelled()) {
						eventDateTableModel.modified();
						eventDateTableModel.fireTableDataChanged();
						checkConsistency();
					}
				}
			}
		});
		
		addTimeButton = new JButton("Add");
		addTimeButton.setHorizontalAlignment(SwingConstants.LEFT);
		addTimeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addTimeButton.setPreferredSize(new Dimension(100, 25));
		addTimeButton.setMinimumSize(new Dimension(100, 25));
		addTimeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_addTimeButton = new GridBagConstraints();
		gbc_addTimeButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addTimeButton.insets = new Insets(25, 25, 5, 25);
		gbc_addTimeButton.gridx = 4;
		gbc_addTimeButton.gridy = 0;
		add(addTimeButton, gbc_addTimeButton);

		editTimeButton = new JButton("Edit");
		editTimeButton.setHorizontalAlignment(SwingConstants.LEFT);
		editTimeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		editTimeButton.setPreferredSize(new Dimension(100, 25));
		editTimeButton.setMinimumSize(new Dimension(100, 25));
		editTimeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		GridBagConstraints gbc_editTimeButton = new GridBagConstraints();
		gbc_editTimeButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_editTimeButton.insets = new Insets(0, 25, 5, 25);
		gbc_editTimeButton.gridx = 4;
		gbc_editTimeButton.gridy = 1;
		add(editTimeButton, gbc_editTimeButton);

		removeTimeButton = new JButton("Remove");
		removeTimeButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeTimeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeTimeButton.setPreferredSize(new Dimension(100, 25));
		removeTimeButton.setMinimumSize(new Dimension(100, 25));
		removeTimeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_removeTimeButton = new GridBagConstraints();
		gbc_removeTimeButton.anchor = GridBagConstraints.NORTH;
		gbc_removeTimeButton.insets = new Insets(0, 25, 0, 25);
		gbc_removeTimeButton.gridx = 4;
		gbc_removeTimeButton.gridy = 2;
		add(removeTimeButton, gbc_removeTimeButton);
		
		timeScrollPane = new JScrollPane();
		GridBagConstraints gbc_timeScrollPane = new GridBagConstraints();
		gbc_timeScrollPane.insets = new Insets(0, 0, 25, 5);
		gbc_timeScrollPane.gridheight = 3;
		gbc_timeScrollPane.fill = GridBagConstraints.BOTH;
		gbc_timeScrollPane.gridx = 5;
		gbc_timeScrollPane.gridy = 0;
		add(timeScrollPane, gbc_timeScrollPane);
		
		timeTable = new JTable();
		timeTable.setTableHeader(new JTableHeader(timeTable.getColumnModel()) {

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

		timeTable.getTableHeader().setReorderingAllowed(false);

		timeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		eventTimeTableModel = new ComplexEventTimeTableModel(complexEventTableModel);
		timeTable.setModel(eventTimeTableModel);

		timeTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				securityEditor.checkDirty();
			}
		});

		timeTable.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(securityEditor));
		timeTable.setShowHorizontalLines(false);
		timeTable.setShowVerticalLines(false);
		timeTable.setIntercellSpacing(new Dimension(0, 0));
		timeTable.setAutoscrolls(false);
		timeTable.setRowHeight(20);
		timeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		timeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		timeTable.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		for (int i = 0; i < timeTable.getColumnCount(); i++)
			timeTable.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));

		timeScrollPane.setViewportView(timeTable);

		dateTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (dateTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					removeDateButton.setEnabled(true);
					
					if(dateTable.getSelectedRowCount()==1)
					{
						addTimeButton.setEnabled(true);
						editDateButton.setEnabled(true);
					}
					else
					{
						addTimeButton.setEnabled(false);
						editDateButton.setEnabled(false);
					}					
				}
				else {
					addTimeButton.setEnabled(false);
					removeDateButton.setEnabled(false);
					editDateButton.setEnabled(false);
				}
				
				if (dateTable.getSelectionModel().getMinSelectionIndex() != -1)
					eventTimeTableModel.updateComplexEventTimes(eventDateTableModel.get(dateTable.getSelectionModel().getMinSelectionIndex()).getComplexEventTimes());

				else
					eventTimeTableModel.updateComplexEventTimes(new ArrayList<ComplexEventTime>());

			}
		});
		
		timeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (timeTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					
					removeTimeButton.setEnabled(true);
					
					if(timeTable.getSelectedRowCount()==1)						
						editTimeButton.setEnabled(true);
					
					else
						editTimeButton.setEnabled(false);					
				}
				else {
					
					removeTimeButton.setEnabled(false);
					editTimeButton.setEnabled(false);
				}

			}
		});
		
		removeTimeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (timeTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					
					List<ComplexEventTime> complexEventTimes = new ArrayList<ComplexEventTime>();
					for(int index: timeTable.getSelectedRows())
						complexEventTimes.add(eventTimeTableModel.get(index));
					for(ComplexEventTime complexEventTime: complexEventTimes)
						eventTimeTableModel.remove(complexEventTime);
					checkConsistency();
				}
			}
		});

		
		addTimeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SecurityComplexEventTimeDialog securityComplexEventTimeDialog = new SecurityComplexEventTimeDialog(null);
				securityComplexEventTimeDialog.setVisible(true);
				if (!securityComplexEventTimeDialog.isCancelled()) {
					ComplexEventTime complexEventTime = securityComplexEventTimeDialog.getComplexEventTime();
					complexEventTime.setComplexEventDate(eventDateTableModel.get(dateTable.getSelectionModel().getMinSelectionIndex()));
					eventTimeTableModel.addSecurityEvent(complexEventTime);
					checkConsistency();
				}
			}
		});

		editTimeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (timeTable.getSelectionModel().getMinSelectionIndex() != -1
						&& securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {
					SecurityComplexEventTimeDialog securityComplexEventTimeDialog = new SecurityComplexEventTimeDialog(eventTimeTableModel.get(timeTable.getSelectionModel().getMinSelectionIndex()));
					securityComplexEventTimeDialog.setVisible(true);
					if (!securityComplexEventTimeDialog.isCancelled()) {
						eventTimeTableModel.modified();
						eventTimeTableModel.fireTableDataChanged();
						checkConsistency();
					}
				}
			}
		});
		
		updateContent();
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Save.
	 */
	public void save() {

		complexEventTableModel.save();

	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return complexEventTableModel.isDirty();
	}

	/**
	 * Update content.
	 */
	public void updateContent() {
		
		complexEventTableModel.updateSecurityComplexEvents(securityEditor.getSecurity().getSecurityDetails().getSecurityComplexEvents());

		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getAbstractBusinessObject())) {

			scrollPane.getViewport().setBackground(new Color(255, 243, 204));
			addButton.setEnabled(true);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
			
			dateScrollPane.getViewport().setBackground(new Color(255, 243, 204));
			addDateButton.setEnabled(false);
			editDateButton.setEnabled(false);
			removeDateButton.setEnabled(false);
			
			timeScrollPane.getViewport().setBackground(new Color(255, 243, 204));
			addTimeButton.setEnabled(false);
			editTimeButton.setEnabled(false);
			removeTimeButton.setEnabled(false);
		}
		else {

			scrollPane.getViewport().setBackground(new Color(204, 216, 255));
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
			
			dateScrollPane.getViewport().setBackground(new Color(204, 216, 255));
			addDateButton.setEnabled(false);
			editDateButton.setEnabled(false);
			removeDateButton.setEnabled(false);
			
			timeScrollPane.getViewport().setBackground(new Color(204, 216, 255));
			addTimeButton.setEnabled(false);
			editTimeButton.setEnabled(false);
			removeTimeButton.setEnabled(false);
		}

		checkConsistency();

	}

	private void checkConsistency() {

		securityEditor.checkDirty();
	}

}
