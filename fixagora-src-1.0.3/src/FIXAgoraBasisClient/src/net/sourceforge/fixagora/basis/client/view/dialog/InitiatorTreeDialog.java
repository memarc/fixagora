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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.view.BusinessObjectTreeNode;
import net.sourceforge.fixagora.basis.client.view.TreePanel;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;

/**
 * The Class InitiatorTreeDialog.
 */
public class InitiatorTreeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private AbstractInitiator abstractBusinessComponent = null;
	private JScrollPane scrollPane;
	private DefaultMutableTreeNode topNode;
	private DefaultTreeModel defaultTreeModel;
	private JTree tree;
	private int width = 0;
	private Map<Long, BusinessObjectTreeNode> defaultMutableTreeNodeMap = new HashMap<Long, BusinessObjectTreeNode>();
	private BasisClientConnector basisClientConnector = null;

	/**
	 * Instantiates a new initiator tree dialog.
	 *
	 * @param basisClientConnector the basis client connector
	 */
	public InitiatorTreeDialog(BasisClientConnector basisClientConnector) {

		this.basisClientConnector = basisClientConnector;

		setBounds(100, 100, 487, 494);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(InitiatorTreeDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		setTitle("Select Market Interface");

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 52 };
		contentPanel.setLayout(gbl_contentPanel);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(25, 25, 5, 25);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPanel.add(scrollPane, gbc_scrollPane);

		this.topNode = new BusinessObjectTreeNode();
		defaultTreeModel = new DefaultTreeModel(this.topNode);

		final BasicTreeUI ui = new BasicTreeUI() {

			@Override
			protected void paintRow(final Graphics g, final Rectangle clipBounds, final Insets insets, final Rectangle bounds, final TreePath path,
					final int row, final boolean isExpanded, final boolean hasBeenExpanded, final boolean isLeaf) {

				bounds.width = width - bounds.x;
				super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
			}
		};

		tree = new JTree(defaultTreeModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				width = this.getWidth();
				super.paintComponent(g);
			}
		};

		tree.setRootVisible(false);
		tree.setUI(ui);
		tree.setBackground(Color.GREEN);
		tree.setOpaque(false);
		tree.setFont(new Font("Dialog", Font.PLAIN, 12));
		tree.setBorder(new EmptyBorder(10, 10, 10, 10));

		JViewport jViewport = new JViewport() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				super.paintComponent(g);

				final Graphics2D graphics2D = (Graphics2D) g;

				graphics2D.setColor(new Color(255, 236, 179));
				graphics2D.setPaintMode();

				int y = scrollPane.getViewport().getViewPosition().y;

				for (int i = 0; i < tree.getRowCount(); i++)
					if (i % 2 == 0)
						graphics2D.fillRect(0, tree.getRowBounds(i).y - y, tree.getWidth(), tree.getRowBounds(i).height);

				getUI().paint(graphics2D, this);
			}
		};

		jViewport.setOpaque(true);
		jViewport.setBackground(new Color(255, 243, 204));

		scrollPane.setViewport(jViewport);
		scrollPane.setViewportView(tree);

		final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf,
					final int row, final boolean hasFocus) {

				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

				if (row % 2 == 0)
					this.setBackground(new Color(255, 236, 179));

				else
					this.setBackground(new Color(255, 243, 204));

				AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) ((BusinessObjectTreeNode) value).getUserObject();

				if (abstractBusinessObject != null) {

					this.setLeafIcon(new ImageIcon(TreePanel.class.getResource(abstractBusinessObject.getIcon())));
					this.setOpenIcon(new ImageIcon(TreePanel.class.getResource(abstractBusinessObject.getIcon())));
					this.setClosedIcon(new ImageIcon(TreePanel.class.getResource(abstractBusinessObject.getIcon())));
					this.setIcon(new ImageIcon(TreePanel.class.getResource(abstractBusinessObject.getIcon())));
				}

				return this;
			}

		};

		renderer.setOpaque(false);
		renderer.setBackgroundNonSelectionColor(null);
		renderer.setBackgroundSelectionColor((new Color(204, 216, 255)));
		renderer.setBorderSelectionColor((new Color(204, 216, 255)));
		renderer.setBorder(new EmptyBorder(2, 2, 2, 0));
		renderer.setFont(new Font("Dialog", Font.PLAIN, 12));

		tree.setCellRenderer(renderer);

		tree.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {

				if (e.getClickCount() == 2) {

					JTree tree = (JTree) (e.getSource());
					int x = e.getX();
					int y = e.getY();
					TreePath path = tree.getPathForLocation(x, y);

					if (path != null) {

						final BusinessObjectTreeNode node = (BusinessObjectTreeNode) path.getLastPathComponent();
						if (node.getUserObject() instanceof AbstractInitiator) {

							abstractBusinessComponent = (AbstractInitiator) node.getUserObject();
							setVisible(false);
						}
					}
				}
			}

			@Override
			public void mouseEntered(final MouseEvent e) {

			}

			@Override
			public void mouseExited(final MouseEvent e) {

			}

			@Override
			public void mousePressed(final MouseEvent e) {

			}

			@Override
			public void mouseReleased(final MouseEvent e) {

			}
		});

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {

				if (tree.getSelectionPaths() != null)
					for (TreePath treePath : tree.getSelectionPaths()) {

						BusinessObjectTreeNode node = (BusinessObjectTreeNode) treePath.getLastPathComponent();
						Object object = node.getUserObject();
						if (object instanceof AbstractInitiator) {
							abstractBusinessComponent = (AbstractInitiator) object;
						}
						else {
							abstractBusinessComponent = null;
						}
						checkConsistency();
					}
			}
		});

		okButton = new JButton();
		okButton.setText("Select");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png")));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 0;
		gbc_okButton.gridy = 1;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 1;
		gbc_cancelButton.gridy = 1;
		contentPanel.add(cancelButton, gbc_cancelButton);
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
			}
		});

		checkConsistency();
	}

	/**
	 * Adds the top node.
	 *
	 * @param businessObjectTreeNode the business object tree node
	 */
	public void addTopNode(BusinessObjectTreeNode businessObjectTreeNode) {

		topNode.add(businessObjectTreeNode);
		if (businessObjectTreeNode.getUserObject() instanceof AbstractBusinessObject) {
			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) businessObjectTreeNode.getUserObject();

			defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), businessObjectTreeNode);
		}

		parseTreeNode(businessObjectTreeNode);

		TreePath treePath = new TreePath(topNode);
		defaultTreeModel.reload();
		tree.expandPath(treePath);

		checkConsistency();
	}

	private void parseTreeNode(BusinessObjectTreeNode dialogNode) {

		for (int i = 0; i < dialogNode.getChildCount(); i++) {

			BusinessObjectTreeNode defaultMutableTreeNode = (BusinessObjectTreeNode) dialogNode.getChildAt(i);
			if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
				AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

				defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode);

			}
			parseTreeNode(defaultMutableTreeNode);
		}
	}

	private void checkConsistency() {

		okButton.setEnabled(abstractBusinessComponent != null);
	}

	/* (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {

		if (visible) {

			cancelled = false;
			checkConsistency();
		}

		super.setVisible(visible);

	}

	/**
	 * Gets the abstract initiator list.
	 *
	 * @return the abstract initiator list
	 */
	public List<String> getAbstractInitiatorList() {

		List<String> abstractBusinessComponents = new ArrayList<String>();

		for (BusinessObjectTreeNode defaultMutableTreeNode : defaultMutableTreeNodeMap.values())

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractInitiator)
				abstractBusinessComponents.add(defaultMutableTreeNode.getUserObject().toString());

		return abstractBusinessComponents;
	}

	/**
	 * Gets the abstract initiator for name.
	 *
	 * @param name the name
	 * @return the abstract initiator for name
	 */
	public AbstractInitiator getAbstractInitiatorForName(String name) {

		if (name == null)
			return null;

		for (BusinessObjectTreeNode defaultMutableTreeNode : defaultMutableTreeNodeMap.values())

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractInitiator) {

				AbstractInitiator abstractBusinessComponent = (AbstractInitiator) defaultMutableTreeNode.getUserObject();

				if (abstractBusinessComponent.getName().trim().equals(name.trim()))
					return abstractBusinessComponent;
			}

		return null;
	}

	/**
	 * Sets the business object tree node map.
	 *
	 * @param defaultMutableTreeNodeMap the default mutable tree node map
	 */
	public void setBusinessObjectTreeNodeMap(Map<Long, BusinessObjectTreeNode> defaultMutableTreeNodeMap) {

		this.defaultMutableTreeNodeMap = defaultMutableTreeNodeMap;
	}

	/**
	 * Checks if is cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {

		return cancelled;
	}

	/**
	 * Gets the abstract initiator.
	 *
	 * @return the abstract initiator
	 */
	public AbstractInitiator getAbstractInitiator() {

		return abstractBusinessComponent;
	}

	/**
	 * On persist response.
	 *
	 * @param persistResponse the persist response
	 */
	public void onPersistResponse(PersistResponse persistResponse) {

		AbstractBusinessObject abstractBusinessObject = persistResponse.getAbstractBusinessObject();

		if (abstractBusinessObject instanceof AbstractInitiator) {

			AbstractBusinessObject parent = abstractBusinessObject.getParent();

			BusinessObjectTreeNode defaultMutableTreeNode = defaultMutableTreeNodeMap.get(parent.getId());
			if (defaultMutableTreeNode != null) {

				BusinessObjectTreeNode defaultMutableTreeNode2 = null;

				for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {

					BusinessObjectTreeNode defaultMutableTreeNode3 = (BusinessObjectTreeNode) defaultMutableTreeNode.getChildAt(i);
					AbstractBusinessObject abstractBusinessObject2 = (AbstractBusinessObject) defaultMutableTreeNode3.getUserObject();

					if (abstractBusinessObject2.compareTo(abstractBusinessObject) > 0) {

						defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);
						defaultTreeModel.insertNodeInto(defaultMutableTreeNode2, defaultMutableTreeNode, i);
						i = defaultMutableTreeNode.getChildCount();
						tree.repaint();
					}
				}

				if (defaultMutableTreeNode2 == null) {

					defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);
					defaultTreeModel.insertNodeInto(defaultMutableTreeNode2, defaultMutableTreeNode, defaultMutableTreeNode.getChildCount());
				}

				defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);
			}
		}
	}

	/**
	 * On update response.
	 *
	 * @param updateResponse the update response
	 */
	public void onUpdateResponse(UpdateResponse updateResponse) {

		for (AbstractBusinessObject abstractBusinessObject : updateResponse.getAbstractBusinessObjects()) {

			AbstractBusinessObject oldParent = null;

			BusinessObjectTreeNode oldBusinessObjectTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());
			if (oldBusinessObjectTreeNode != null) {

				if (oldBusinessObjectTreeNode.getUserObject() instanceof AbstractBusinessObject) {
					AbstractBusinessObject oldAbstractBusinessObject = (AbstractBusinessObject) oldBusinessObjectTreeNode.getUserObject();
					oldParent = oldAbstractBusinessObject.getParent();

				}
			}

			AbstractBusinessObject parent = abstractBusinessObject.getParent();

			if (parent != null) {

				BusinessObjectTreeNode defaultMutableTreeNode = defaultMutableTreeNodeMap.get(parent.getId());

				if (defaultMutableTreeNode != null) {

					parent = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

					if (basisClientConnector.getFUser().canRead(parent) && basisClientConnector.getFUser().canRead(abstractBusinessObject)) {

						if (oldParent != null && oldParent.getId() != parent.getId()) {

							defaultTreeModel.removeNodeFromParent(oldBusinessObjectTreeNode);
							defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());

							BusinessObjectTreeNode defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);
							defaultMutableTreeNode.add(defaultMutableTreeNode2);
							defaultMutableTreeNode.updateSortOrder();

							defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);

							defaultTreeModel.nodesWereInserted(defaultMutableTreeNode, new int[] { defaultMutableTreeNode.getIndex(defaultMutableTreeNode2) });

						}
						else {
							BusinessObjectTreeNode treeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());
							if (treeNode != null) {
								treeNode.setUserObject(abstractBusinessObject);
								int[] childIndices = new int[treeNode.getChildCount()];
								for (int i = 0; i < treeNode.getChildCount(); i++) {
									childIndices[i] = i;
									BusinessObjectTreeNode businessObjectTreeNode = (BusinessObjectTreeNode) treeNode.getChildAt(i);
									if (businessObjectTreeNode.getUserObject() instanceof AbstractBusinessObject) {
										AbstractBusinessObject abstractBusinessObject2 = (AbstractBusinessObject) businessObjectTreeNode.getUserObject();
										if (abstractBusinessObject2.getParent() != null
												&& abstractBusinessObject2.getParent().getId() == abstractBusinessObject.getId())
											abstractBusinessObject2.setParent(abstractBusinessObject);
									}
								}
								treeNode.updateSortOrder();
								defaultTreeModel.nodeChanged(treeNode);
								defaultTreeModel.nodesChanged(treeNode, childIndices);

								defaultMutableTreeNode.updateSortOrder();
								defaultTreeModel.nodeChanged(defaultMutableTreeNode);
							}
							else {
								BusinessObjectTreeNode defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);
								defaultMutableTreeNode.add(defaultMutableTreeNode2);
								defaultMutableTreeNode.updateSortOrder();

								defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);

								defaultTreeModel.nodesWereInserted(defaultMutableTreeNode,
										new int[] { defaultMutableTreeNode.getIndex(defaultMutableTreeNode2) });
							}
						}
					}
				}

			}
			else if (oldBusinessObjectTreeNode != null) {
				defaultTreeModel.removeNodeFromParent(oldBusinessObjectTreeNode);
				defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());
			}

		}

	}

	/**
	 * On remove response.
	 *
	 * @param removeResponse the remove response
	 */
	public void onRemoveResponse(RemoveResponse removeResponse) {

		for (AbstractBusinessObject abstractBusinessObject : removeResponse.getAbstractBusinessObjects()) {

			BusinessObjectTreeNode oldBusinessObjectTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());

			if (oldBusinessObjectTreeNode != null) {

				defaultTreeModel.removeNodeFromParent(oldBusinessObjectTreeNode);
				defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());
			}
		}

		tree.repaint();
	}

	/**
	 * Gets the abstract initiator for id.
	 *
	 * @param interfaceValue the interface value
	 * @return the abstract initiator for id
	 */
	public AbstractInitiator getAbstractInitiatorForId(long interfaceValue) {

		for (BusinessObjectTreeNode defaultMutableTreeNode : defaultMutableTreeNodeMap.values())

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractInitiator) {

				AbstractInitiator abstractBusinessComponent = (AbstractInitiator) defaultMutableTreeNode.getUserObject();

				if (abstractBusinessComponent.getId() == interfaceValue)
					return abstractBusinessComponent;
			}

		return null;
	}

}
