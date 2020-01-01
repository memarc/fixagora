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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.fixagora.basis.client.view.ToolbarButton;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook;

/**
 * The Class BuySideQuotePageEditorAssignedSecurity.
 */
public class BuySideQuotePageEditorAssignedSecurity extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private BuySideQuotePageEditor buySideQuotePageEditor = null;

	private JList assignedList = null;

	private boolean dirty;

	private JList notAssignedList = null;

	private JButton addButton = null;

	private JButton removeButton = null;

	private JButton addAllButton = null;

	private JButton removeAllButton = null;

	private DefaultListModel defaultListModel2;

	private DefaultListModel defaultListModel;

	/**
	 * Instantiates a new buy side quote page editor assigned security.
	 *
	 * @param buySideQuotePageEditor the buy side quote page editor
	 */
	public BuySideQuotePageEditorAssignedSecurity(final BuySideQuotePageEditor buySideQuotePageEditor) {

		super();

		this.buySideQuotePageEditor = buySideQuotePageEditor;

		setBorder(new EmptyBorder(0, 0, 0, 0));
		getViewport().setBackground(new Color(204, 216, 255));

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 0;
		panel.add(leftFillPanel, gbc_leftFillPanel);
		
		DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

				Component component =  super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				StringBuffer stringBuffer = new StringBuffer(value.toString());
				
				if(value instanceof AssignedBuySideBookSecurity)
				{
					
					FSecurity security = (FSecurity)buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(((AssignedBuySideBookSecurity)value).getSecurity().getId());
					if(security!=null&&security.getParent()!=null)
					{
						AbstractBusinessObject abstractBusinessObject = buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(security.getParent().getId());
						if(abstractBusinessObject!=null)
						{
							stringBuffer.append(" (");
							stringBuffer.append(abstractBusinessObject.getName());
							stringBuffer.append(")");
						}
					}
				}
				
				setText(stringBuffer.toString());
				return component;
			}
			
		};

		defaultListModel2 = new DefaultListModel();
		
		notAssignedList = new JList();
		notAssignedList.setCellRenderer(defaultListCellRenderer);
		notAssignedList.setModel(defaultListModel2);
		notAssignedList.setBorder(new EmptyBorder(5, 5, 5, 5));
		notAssignedList.setBackground(new Color(255, 243, 204));
		
		JScrollPane jScrollPane2 = new JScrollPane(notAssignedList);
		jScrollPane2.setPreferredSize(new Dimension(300, 400));
		
		GridBagConstraints gbc_jScrollPane2 = new GridBagConstraints();
		gbc_jScrollPane2.gridheight = 4;
		gbc_jScrollPane2.insets = new Insets(50, 50, 50, 55);
		gbc_jScrollPane2.fill = GridBagConstraints.BOTH;
		gbc_jScrollPane2.gridx = 1;
		gbc_jScrollPane2.gridy = 0;
		panel.add(jScrollPane2, gbc_jScrollPane2);

		addButton = new JButton("Add");
		addButton.setEnabled(false);
		addButton.setHorizontalAlignment(SwingConstants.LEFT);
		addButton.setIcon(new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1rightarrow.png")));
		addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addButton.anchor = GridBagConstraints.SOUTH;
		gbc_addButton.insets = new Insets(0, 0, 5, 5);
		gbc_addButton.gridx = 2;
		gbc_addButton.gridy = 0;
		panel.add(addButton, gbc_addButton);

		notAssignedList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (notAssignedList.getSelectedValues().length > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()))
					addButton.setEnabled(true);

				else
					addButton.setEnabled(false);
			}
		});

		defaultListModel = new DefaultListModel();

		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities())
			defaultListModel.addElement(assignedBuySideBookSecurity);
		


		assignedList = new JList();
		assignedList.setCellRenderer(defaultListCellRenderer);
		assignedList.setModel(defaultListModel);
		assignedList.setBackground(new Color(255, 243, 204));
		assignedList.setBorder(new EmptyBorder(5, 5, 5, 5));		
		
		JScrollPane jScrollPane = new JScrollPane(assignedList);
		jScrollPane.setPreferredSize(new Dimension(300, 400));
		
		GridBagConstraints gbc_jScrollPane = new GridBagConstraints();
		gbc_jScrollPane.insets = new Insets(50, 50, 50, 50);
		gbc_jScrollPane.gridheight = 4;
		gbc_jScrollPane.fill = GridBagConstraints.BOTH;
		gbc_jScrollPane.gridx = 3;
		gbc_jScrollPane.gridy = 0;
		panel.add(jScrollPane, gbc_jScrollPane);

		removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		removeButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeButton.setIcon(new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1leftarrow.png")));
		removeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_removeButton.insets = new Insets(0, 0, 15, 5);
		gbc_removeButton.gridx = 2;
		gbc_removeButton.gridy = 1;
		panel.add(removeButton, gbc_removeButton);

		assignedList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (assignedList.getSelectedValues().length > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()))
					removeButton.setEnabled(true);
				else
					removeButton.setEnabled(false);

			}
		});

		addAllButton = new JButton("Add all");
		addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0);
		addAllButton.setHorizontalAlignment(SwingConstants.LEFT);
		addAllButton.setIcon(new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/2rightarrow.png")));
		addAllButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_addAllButton = new GridBagConstraints();
		gbc_addAllButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addAllButton.insets = new Insets(15, 0, 5, 5);
		gbc_addAllButton.gridx = 2;
		gbc_addAllButton.gridy = 2;
		panel.add(addAllButton, gbc_addAllButton);

		removeAllButton = new JButton("Remove all");
		removeAllButton.setEnabled(assignedList.getModel().getSize() > 0);
		removeAllButton.setHorizontalAlignment(SwingConstants.LEFT);
		removeAllButton.setIcon(new ImageIcon(ToolbarButton.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/2leftarrow.png")));
		removeAllButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_removeAllButton = new GridBagConstraints();
		gbc_removeAllButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_removeAllButton.anchor = GridBagConstraints.NORTH;
		gbc_removeAllButton.insets = new Insets(0, 0, 0, 5);
		gbc_removeAllButton.gridx = 2;
		gbc_removeAllButton.gridy = 3;
		panel.add(removeAllButton, gbc_removeAllButton);

		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Object[] selectedValues = assignedList.getSelectedValues();

				for (Object object : selectedValues) {

					defaultListModel.removeElement(object);

					boolean added = false;

					for (int i = 0; i < defaultListModel2.getSize(); i++) {

						if (defaultListModel2.get(i).toString().compareTo(object.toString()) > 0) {

							defaultListModel2.add(i, object);
							i = defaultListModel2.getSize();
							added = true;
						}
					}

					if (!added)
						defaultListModel2.add(defaultListModel2.getSize(), object);
				}

				removeAllButton.setEnabled(assignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));
				addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));

				checkDirty();
			}
		});

		removeAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				List<Object> allValues = new ArrayList<Object>();

				for (int i = 0; i < assignedList.getModel().getSize(); i++)
					allValues.add(assignedList.getModel().getElementAt(i));

				for (Object object : allValues) {

					defaultListModel.removeElement(object);
					boolean added = false;

					for (int i = 0; i < defaultListModel2.getSize(); i++) {

						if (defaultListModel2.get(i).toString().compareTo(object.toString()) > 0) {

							defaultListModel2.add(i, object);
							i = defaultListModel2.getSize();
							added = true;
						}
					}

					if (!added)
						defaultListModel2.add(defaultListModel2.getSize(), object);

				}

				removeAllButton.setEnabled(assignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));
				addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));

				checkDirty();
			}
		});

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Object[] selectedValues = notAssignedList.getSelectedValues();

				for (Object object : selectedValues) {

					defaultListModel2.removeElement(object);
					boolean added = false;

					for (int i = 0; i < defaultListModel.getSize(); i++) {

						if (defaultListModel.get(i).toString().compareTo(object.toString()) > 0) {

							defaultListModel.add(i, object);
							i = defaultListModel.getSize();
							added = true;
						}
					}

					if (!added)
						defaultListModel.add(defaultListModel.getSize(), object);
				}

				removeAllButton.setEnabled(assignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));
				addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));

				checkDirty();
			}
		});

		addAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				List<Object> allValues = new ArrayList<Object>();

				for (int i = 0; i < notAssignedList.getModel().getSize(); i++)
					allValues.add(notAssignedList.getModel().getElementAt(i));

				for (Object object : allValues) {

					defaultListModel2.removeElement(object);
					boolean added = false;

					for (int i = 0; i < defaultListModel.getSize(); i++) {

						if (defaultListModel.get(i).toString().compareTo(object.toString()) > 0) {

							defaultListModel.add(i, object);
							i = defaultListModel.getSize();
							added = true;
						}
					}

					if (!added)
						defaultListModel.add(defaultListModel.getSize(), object);
				}

				removeAllButton.setEnabled(assignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));
				addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0
						&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));

				checkDirty();
			}
		});

		JPanel rightFillPanel = new JPanel();
		rightFillPanel.setOpaque(false);
		GridBagConstraints gbc_rightFillPanel = new GridBagConstraints();
		gbc_rightFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_rightFillPanel.fill = GridBagConstraints.BOTH;
		gbc_rightFillPanel.gridx = 4;
		gbc_rightFillPanel.gridy = 0;
		panel.add(rightFillPanel, gbc_rightFillPanel);

		updateContent();
	}

	private void checkDirty() {

		List<Object> objects = new ArrayList<Object>();

		for (int i = 0; i < assignedList.getModel().getSize(); i++)
			objects.add(assignedList.getModel().getElementAt(i));

		if (buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities().containsAll(objects) && objects.containsAll(buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities()))
			dirty = false;

		else
			dirty = true;

		buySideQuotePageEditor.checkDirty();
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return dirty;
	}

	/**
	 * Save.
	 */
	public void save() {

		List<AssignedBuySideBookSecurity> objects = new ArrayList<AssignedBuySideBookSecurity>();

		for (int i = 0; i < assignedList.getModel().getSize(); i++)
			objects.add((AssignedBuySideBookSecurity) assignedList.getModel().getElementAt(i));

		buySideQuotePageEditor.getBuySideQuotePage().setAssignedBuySideBookSecurities(objects);
		for(AssignedBuySideBookSecurity assignedBuySideBookSecurity: buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities())
			assignedBuySideBookSecurity.getBuySideQuotePages().add(buySideQuotePageEditor.getBuySideQuotePage());
	}

	/**
	 * Update content.
	 */
	public void updateContent() {
		
		defaultListModel2.removeAllElements();
		
		BuySideBook buySideBook = (BuySideBook) buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(buySideQuotePageEditor.getBuySideQuotePage().getParent().getId());
		
		List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities = new ArrayList<AssignedBuySideBookSecurity>(buySideBook.getAssignedBuySideBookSecurities());
		assignedBuySideBookSecurities.removeAll(buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities());
		Collections.sort(assignedBuySideBookSecurities);
		for(AssignedBuySideBookSecurity assignedBuySideBookSecurity: assignedBuySideBookSecurities)
		{
			
			FSecurity fSecurity = (FSecurity)buySideQuotePageEditor.getMainPanel().getAbstractBusinessObjectForId(assignedBuySideBookSecurity.getSecurity().getId());

			if(!defaultListModel.contains(assignedBuySideBookSecurity)&&buySideQuotePageEditor.getBasisClientConnector().getFUser().canRead(fSecurity))
				defaultListModel2.addElement(assignedBuySideBookSecurity);
		}
		
		defaultListModel.removeAllElements();
		
		List<AssignedBuySideBookSecurity> assignedBuySideBookSecurities2 = new ArrayList<AssignedBuySideBookSecurity>(buySideQuotePageEditor.getBuySideQuotePage().getAssignedBuySideBookSecurities());
		Collections.sort(assignedBuySideBookSecurities2);
		
		for(AssignedBuySideBookSecurity assignedBuySideBookSecurity: assignedBuySideBookSecurities2)
			defaultListModel.addElement(assignedBuySideBookSecurity);
		

		if (buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject())) {
			
			notAssignedList.setBackground(new Color(255, 243, 204));
			assignedList.setBackground(new Color(255, 243, 204));
		}
		else {
			
			notAssignedList.setBackground(new Color(204, 216, 255));
			assignedList.setBackground(new Color(204, 216, 255));
		}

		if (assignedList.getSelectedValues().length > 0 && buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()))
			removeButton.setEnabled(true);
		
		else
			removeButton.setEnabled(false);
		
		if (notAssignedList.getSelectedValues().length > 0 && buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()))
			addButton.setEnabled(true);
		
		else
			addButton.setEnabled(false);

		removeAllButton.setEnabled(assignedList.getModel().getSize() > 0
				&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));
		
		addAllButton.setEnabled(notAssignedList.getModel().getSize() > 0
				&& buySideQuotePageEditor.getBasisClientConnector().getFUser().canWrite(buySideQuotePageEditor.getAbstractBusinessObject()));

		checkDirty();
	}
	
	/**
	 * Update roles.
	 *
	 * @param roles the roles
	 */
	public void updateRoles(List<FRole> roles)
	{

	}

}
