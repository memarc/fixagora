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
package net.sourceforge.fixagora.basis.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.JTableHeader;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.editor.RolePermissionTableModel;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.GradientTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.CheckboxTableHeaderRenderer;
import net.sourceforge.fixagora.basis.client.view.editor.RolePermissionCelleditor;
import net.sourceforge.fixagora.basis.client.view.editor.RolePermissionTableRenderer;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;

/**
 * The Class PermissionChangeDialog.
 */
public class PermissionChangeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private JTable table = null;

	private RolePermissionTableModel rolePermissionTableModel = null;

	/**
	 * Instantiates a new permission change dialog.
	 *
	 * @param basisClientConnector the basis client connector
	 * @param abstractBusinessObjects the abstract business objects
	 */
	public PermissionChangeDialog(final BasisClientConnector basisClientConnector, final List<AbstractBusinessObject> abstractBusinessObjects) {

		RolesResponse rolesResponse = null;
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof RolesResponse) {
			rolesResponse = (RolesResponse) abstractResponse;
		}
		else if (abstractResponse instanceof ErrorResponse) {
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		setIconImage(new ImageIcon(PermissionChangeDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setTitle("Mass Permission Change");
		setBounds(100, 100, 681, 487);
		setBackground(new Color(204, 216, 255));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(204, 216, 255));

		contentPanel.setBorder(new CompoundBorder(new EmptyBorder(20, 20, 0, 20), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BorderLayout(0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane);

		final JPanel headPanel = new GradientPanel();
		contentPanel.add(headPanel, BorderLayout.NORTH);
		final GridBagLayout gbl_headPanel = new GridBagLayout();
		gbl_headPanel.rowHeights = new int[] { 15 };
		gbl_headPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_headPanel.rowWeights = new double[] { 0.0 };
		headPanel.setLayout(gbl_headPanel);
		contentPanel.add(headPanel, BorderLayout.NORTH);

		JLabel permissionChangeLabel = new JLabel("Mass Permission Change");
		permissionChangeLabel.setForeground(Color.WHITE);
		permissionChangeLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 20));
		final GridBagConstraints gbc_permissionChangeLabel = new GridBagConstraints();
		gbc_permissionChangeLabel.anchor = GridBagConstraints.WEST;
		gbc_permissionChangeLabel.insets = new Insets(5, 25, 0, 5);
		gbc_permissionChangeLabel.gridx = 0;
		gbc_permissionChangeLabel.gridy = 0;
		headPanel.add(permissionChangeLabel, gbc_permissionChangeLabel);

		final JLabel settingsLabel = new JLabel();
		settingsLabel.setIcon(new ImageIcon(AbstractBusinessObjectEditor.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/24x24/advancedsettings.png")));
		settingsLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 20));
		settingsLabel.setForeground(Color.WHITE);
		final GridBagConstraints gbc_settingsLabel = new GridBagConstraints();
		gbc_settingsLabel.anchor = GridBagConstraints.EAST;
		gbc_settingsLabel.insets = new Insets(5, 0, 0, 25);
		gbc_settingsLabel.gridx = 1;
		gbc_settingsLabel.gridy = 0;
		headPanel.add(settingsLabel, gbc_settingsLabel);

		JPanel buttonPane = new JPanel();
		buttonPane.setBorder(new EmptyBorder(10, 15, 15, 15));
		buttonPane.setOpaque(false);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("Change");
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setActionCommand("OK");
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/advancedsettings.png")));
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
				
				final WaitDialog waitDialog = new WaitDialog(getParent(), "Perform mass permission change ...");

				final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						for (AbstractBusinessObject abstractBusinessObject : abstractBusinessObjects) {
							abstractBusinessObject.setReadRoles(rolePermissionTableModel.getReadRoles());
							abstractBusinessObject.setWriteRoles(rolePermissionTableModel.getWriteRoles());
							abstractBusinessObject.setExecuteRoles(rolePermissionTableModel.getExecuteRoles());
							basisClientConnector.sendRequest(new UpdateRequest(abstractBusinessObject, RequestIDManager.getInstance().getID()));
						}
						return null;
					}

					@Override
					protected void done() {

						super.done();
						waitDialog.setVisible(false);
					}

				};

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					
						waitDialog.setVisible(true);
						swingWorker.execute();
						
						
					}
				});

			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setActionCommand("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		buttonPane.add(cancelButton);

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setVisible(false);

			}
		});

		scrollPane.setOpaque(true);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.getViewport().setBackground(new Color(255, 243, 204));

		table = new JTable();
		table.setTableHeader(new JTableHeader(table.getColumnModel()) {

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
		if(rolesResponse!=null)
			rolePermissionTableModel = new RolePermissionTableModel(null, rolesResponse.getRoles());
		else
			rolePermissionTableModel = new RolePermissionTableModel(null, new ArrayList<FRole>());
		table.setModel(rolePermissionTableModel);
		table.setDefaultRenderer(Object.class, new RolePermissionTableRenderer(rolePermissionTableModel));
		table.setDefaultEditor(Object.class, new RolePermissionCelleditor(rolePermissionTableModel));
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		rolePermissionTableModel.setTable(table);
		table.getColumnModel().getColumn(0).setHeaderRenderer(new GradientTableHeaderRenderer(25));

		for (int i = 1; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setHeaderRenderer(new CheckboxTableHeaderRenderer());

		scrollPane.setViewportView(table);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

				rolePermissionTableModel.setMinWidth((int) getSize().getWidth() - 450);

			}

			@Override
			public void componentShown(final ComponentEvent e) {

			}
		});

	}

}
