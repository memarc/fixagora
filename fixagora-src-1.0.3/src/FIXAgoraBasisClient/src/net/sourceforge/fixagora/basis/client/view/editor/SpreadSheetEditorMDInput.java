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

import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetMDInputTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.dialog.SpreadSheetMDInputDialog;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDInput;

/**
 * The Class SpreadSheetEditorMDInput.
 */
public class SpreadSheetEditorMDInput extends JPanel {

	private JButton addButton;
	private JTable table;
	private SpreadSheetEditor spreadSheetEditor;
	private SpreadSheetMDInputTableModel fixInputTableModel;
	private JButton removeButton;
	private JScrollPane scrollPane;
	private JButton editButton;

	/**
	 * Instantiates a new spread sheet editor md input.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetEditorMDInput(final SpreadSheetEditor spreadSheetEditor) {

		this.spreadSheetEditor = spreadSheetEditor;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		addButton = new JButton("Add");
		addButton.setHorizontalAlignment(SwingConstants.LEFT);
		addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addButton.setPreferredSize(new Dimension(100, 25));
		addButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(25, 25, 5, 25);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		add(addButton, gbc_btnNewButton);

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

		fixInputTableModel = new SpreadSheetMDInputTableModel(spreadSheetEditor);
		table.setModel(fixInputTableModel);

		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				spreadSheetEditor.checkDirty();
			}
		});

		table.setDefaultRenderer(Object.class, new DefaultEditorTableRenderer(spreadSheetEditor));
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		for(int i=1;i<12;i++)
			table.getColumnModel().getColumn(i).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		table.setBackground(new Color(255, 243, 204));
		table.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setFillsViewportHeight(true);

		fixInputTableModel.setTable(table);
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new GradientTableHeaderRenderer(5));
		fixInputTableModel.setTable(table);

		scrollPane.setViewportView(table);

		editButton = new JButton("Edit");
		editButton.setHorizontalAlignment(SwingConstants.LEFT);
		editButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		editButton.setPreferredSize(new Dimension(100, 25));
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
		removeButton.setIcon(new ImageIcon(FSecurityEditorMasterData.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_2.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton_2.insets = new Insets(0, 25, 0, 25);
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 2;
		add(removeButton, gbc_btnNewButton_2);

		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {
					
					List<SpreadSheetMDInput> spreadSheetMDInputs = new ArrayList<SpreadSheetMDInput>();
					for(int index: table.getSelectedRows())
						spreadSheetMDInputs.add(fixInputTableModel.get(index));
					for(SpreadSheetMDInput spreadSheetMDInput: spreadSheetMDInputs)
						fixInputTableModel.remove(spreadSheetMDInput);
					checkConsistency();
				}
			}
		});

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (spreadSheetEditor.getMainPanel().getBusinessComponents().size() > 0) {
					SpreadSheetMDInputDialog spreadSheetMDInputDialog = new SpreadSheetMDInputDialog(null, spreadSheetEditor.getMainPanel().getBusinessComponents());
					spreadSheetMDInputDialog.setVisible(true);
					if (!spreadSheetMDInputDialog.isCancelled()) {
						SpreadSheetMDInput spreadSheetMDInput = spreadSheetMDInputDialog.getSpreadSheetMDInput();
						spreadSheetMDInput.setSpreadSheet(spreadSheetEditor.getSpreadSheet());
						fixInputTableModel.addSpreadSheetMDInput(spreadSheetMDInput);
						checkConsistency();

					}
				}
			}
		});

		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {
					
					SpreadSheetMDInputDialog spreadSheetMDInputDialog = new SpreadSheetMDInputDialog(fixInputTableModel.get(table.getSelectionModel()
							.getMinSelectionIndex()), spreadSheetEditor.getMainPanel().getBusinessComponents());
					spreadSheetMDInputDialog.setVisible(true);
					if (!spreadSheetMDInputDialog.isCancelled()) {
						fixInputTableModel.modified();
						fixInputTableModel.fireTableDataChanged();
					}
				}
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (table.getSelectionModel().getMinSelectionIndex() != -1
						&& spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {
					removeButton.setEnabled(true);
					if(table.getSelectedRowCount()==1)
						editButton.setEnabled(true);
					else
						editButton.setEnabled(false);				}
				else {
					removeButton.setEnabled(false);
					editButton.setEnabled(false);
				}
			}
		});

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
				for(int i=0;i<11;i++)
					width = width + table.getColumnModel().getColumn(i).getWidth();

				fixInputTableModel.setMinWidth((int) scrollPane.getSize().getWidth() - width - 3);
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

		fixInputTableModel.save();

	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return fixInputTableModel.isDirty();
	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		fixInputTableModel.updateSpreadSheetMDInputs(spreadSheetEditor.getSpreadSheet().getSpreadSheetMDInputs());

		if (spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {

			scrollPane.getViewport().setBackground(new Color(255, 243, 204));
			table.setBackground(new Color(255, 243, 204));
			addButton.setEnabled(true);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
		else {

			scrollPane.getViewport().setBackground(new Color(204, 216, 255));
			table.setBackground(new Color(204, 216, 255));
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}

		checkConsistency();

	}

	/**
	 * Check consistency.
	 */
	public void checkConsistency() {

		spreadSheetEditor.checkDirty();
	}

}
