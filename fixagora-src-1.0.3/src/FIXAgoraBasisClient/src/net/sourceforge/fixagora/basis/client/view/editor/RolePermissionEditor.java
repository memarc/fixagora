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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.model.editor.RolePermissionTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;

/**
 * The Class RolePermissionEditor.
 */
public class RolePermissionEditor extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	private JTable table = null;
	
	private RolePermissionTableModel logTableModel = null;
	
	private AbstractBusinessObjectEditor abstractBusinessObjectEditor = null;

	/**
	 * Instantiates a new role permission editor.
	 *
	 * @param abstractBusinessObjectEditor the abstract business object editor
	 * @param roles the roles
	 */
	public RolePermissionEditor(final AbstractBusinessObjectEditor abstractBusinessObjectEditor, List<FRole> roles) {

		super();
		
		this.abstractBusinessObjectEditor = abstractBusinessObjectEditor;
		
		setOpaque(true);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		getViewport().setBackground(new Color(255, 243, 204));
		
		table = new JTable();
		table.setTableHeader(new JTableHeader(table.getColumnModel()){

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, Color.GRAY, width / 2.F, 21, Color.BLACK);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				getUI().paint(graphics2D, this);
			}		
		});
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		logTableModel = new RolePermissionTableModel(abstractBusinessObjectEditor,roles);
		table.setModel(logTableModel);
		
		table.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
			
				abstractBusinessObjectEditor.checkDirty();				
			}
		});
		
		table.setDefaultRenderer(Object.class, new RolePermissionTableRenderer(logTableModel));
		table.setDefaultEditor(Object.class, new RolePermissionCelleditor(logTableModel));
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		logTableModel.setTable(table);
		table.getColumnModel().getColumn(0).setHeaderRenderer(new GradientTableHeaderRenderer(25));
		
		for (int i = 1; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new CheckboxTableHeaderRenderer());
		
		setViewportView(table);
		
		if (abstractBusinessObjectEditor != null && !abstractBusinessObjectEditor.isEditable()) {
			getViewport().setBackground(new Color(204, 216, 255));
			table.setBackground(new Color(204, 216, 255));
		}
		else
		{
			getViewport().setBackground(new Color(255, 243, 204));
			table.setBackground(new Color(255, 243, 204));
		}
		table.setFillsViewportHeight(true);

		
		getViewport().addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				logTableModel.setMinWidth((int) getSize().getWidth()-450);
				repaint();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				componentResized(e);
			}
		});
	}
	
	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {
		
		return logTableModel.isDirty();
	}

	/**
	 * Save.
	 */
	public void save() {

		logTableModel.save();		
	}

	/**
	 * Update content.
	 *
	 * @param fRoles the f roles
	 */
	public void updateContent(List<FRole> fRoles) {

		logTableModel.updateRoles(fRoles);
		abstractBusinessObjectEditor.checkDirty();		
	}

}
