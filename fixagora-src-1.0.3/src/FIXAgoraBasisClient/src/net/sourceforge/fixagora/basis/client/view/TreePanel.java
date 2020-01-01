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

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.dnd.TreeDragSource;
import net.sourceforge.fixagora.basis.client.model.dnd.TreeDropTarget;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses;
import net.sourceforge.fixagora.basis.shared.model.communication.ChildrenBusinessObjectRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ChildrenBusinessObjectResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RootBusinessObjectRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RootBusinessObjectResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

import org.apache.log4j.Logger;

/**
 * The Class TreePanel.
 */
public class TreePanel extends JScrollPane implements BasisClientConnectorListener, BasisResponses {

	private static final long serialVersionUID = 1L;

	private BusinessObjectTreeNode topNode = null;

	private TopPanel topPanel = null;

	private JTree tree = null;

	private BasisClientConnector basisClientConnector = null;

	private Map<Long, BusinessObjectTreeNode> defaultMutableTreeNodeMap = Collections.synchronizedMap(new HashMap<Long, BusinessObjectTreeNode>());

	private DefaultTreeModel defaultTreeModel = null;

	private Set<Long> expandSet = null;

	private int width = 0;

	private Color blue = new Color(204, 216, 255);

	private ImageIcon logoIcon = null;

	private Image logoIconImageImage = null;

	private static Logger log = Logger.getLogger(TreePanel.class);

	/**
	 * Instantiates a new tree panel.
	 *
	 * @param waitDialog the wait dialog
	 * @param topPanel the top panel
	 * @param basisClientConnector the basis client connector
	 */
	public TreePanel(WaitDialog waitDialog, final TopPanel topPanel, BasisClientConnector basisClientConnector) {

		super();

		setBorder(new EmptyBorder(new Insets(27, 2, 27, 2)));

		setBackground(new Color(204, 216, 255));

		logoIcon = new ImageIcon(TreePanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/network.png"));
		logoIconImageImage = logoIcon.getImage();

		this.topPanel = topPanel;
		this.expandSet = basisClientConnector.getFUser().getExpandSet();
		this.basisClientConnector = basisClientConnector;

		topPanel.setExpandSet(expandSet);

		topNode = new BusinessObjectTreeNode(null);
		defaultTreeModel = new DefaultTreeModel(topNode);

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
				try {
					super.paintComponent(g);
				}
				catch (Exception e) {
				}
			}
		};

		tree.setRootVisible(false);
		tree.setUI(ui);
		tree.setOpaque(false);
		tree.setFont(new Font("Dialog", Font.PLAIN, 12));
		tree.setBorder(new EmptyBorder(10, 10, 10, 10));
		tree.setToggleClickCount(0);
		tree.setRowHeight(24);

		new TreeDragSource(tree, DnDConstants.ACTION_COPY_OR_MOVE, basisClientConnector);
		new TreeDropTarget(tree, basisClientConnector, topPanel.getMainPanel());

		int row = 0;

		while (row < tree.getRowCount()) {

			tree.expandRow(row);
			row++;
		}

		JViewport jViewport = new JViewport() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				super.paintComponent(g);

				final Graphics2D graphics2D = (Graphics2D) g;

				graphics2D.setColor(new Color(255, 236, 179));
				graphics2D.setPaintMode();

				int y = TreePanel.this.getViewport().getViewPosition().y;

				for (int i = 0; i < tree.getRowCount(); i++)
					if (i % 2 == 0) {
						try {
							Rectangle rectangle = tree.getRowBounds(i);
							if (rectangle != null)
								graphics2D.fillRect(0, rectangle.y - y, tree.getWidth(), rectangle.height);
						}
						catch (Exception e) {
						}
					}

				getUI().paint(graphics2D, this);
			}
		};

		jViewport.setOpaque(true);
		jViewport.setBackground(new Color(255, 243, 204));

		setViewport(jViewport);
		setViewportView(tree);

		tree.setCellRenderer(new BusinessObjectTreeCellRenderer());

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
						final AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) node.getUserObject();

						if (abstractBusinessObject != null && abstractBusinessObject.getParent() != null
								&& !(abstractBusinessObject instanceof BusinessSection)) {

							e.consume();

							final WaitDialog waitDialog = new WaitDialog(topPanel.getMainPanel(), "Open business object...");

							final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

								@Override
								protected Void doInBackground() throws Exception {

									topPanel.getMainPanel().addBusinessObjectEditor(abstractBusinessObject, false);
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
						else if (abstractBusinessObject != null) {
							if (expandSet.contains(abstractBusinessObject.getId())) {
								expandSet.remove(abstractBusinessObject.getId());
								tree.collapsePath(path);
							}
							else
								tree.expandPath(path);
							tree.repaint();
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

				if (e.getButton() != MouseEvent.BUTTON1) {

					JTree tree = (JTree) (e.getSource());
					int x = e.getX();
					int y = e.getY();
					TreePath path = tree.getPathForLocation(x, y);

					if (path != null) {

						tree.addSelectionPath(path);

						JPopupMenu popup = topPanel.getTreePopup();

						popup.show((JComponent) e.getSource(), e.getX(), e.getY());
					}
				}
			}
		});

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {


			SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					
						selectionChanged(treeSelectionEvent);
						
					}
				});
			}


		});

		tree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(final TreeExpansionEvent event) {

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					
						expanded(event);
						
					}
				});
			}

			@Override
			public void treeCollapsed(final TreeExpansionEvent event) {

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						collapsed(event);
					}
				});
			}
		});

		initTree(waitDialog);

		final WaitDialog waitDialog2 = new WaitDialog(null, "Restore last session ...");
		if (basisClientConnector.getFUser().getOpenBusinessObjects().size() > 1)
			waitDialog2.setIncrement(100d / (double) (basisClientConnector.getFUser().getOpenBusinessObjects().size()));

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {

					while (!topPanel.getMainPanel().isShowing())
						synchronized (this) {
							wait(100);
						}

					topPanel.getMainPanel().init();

					openLastBusinessObjects(topNode, waitDialog2);
				}
				catch (Exception e) {
					log.error("Bug", e);
				}
				return null;
			}

			@Override
			protected void done() {

				super.done();
				waitDialog2.setVisible(false);
				topPanel.getMainPanel().setXMLProperties();
			}

		};

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				swingWorker.execute();
				waitDialog2.setVisible(true);
			}
		});

		basisClientConnector.addBasisClientConnectorListener(this);
	}

	private void openLastBusinessObjects(BusinessObjectTreeNode defaultMutableTreeNode, WaitDialog waitDialog2) {

		for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {

			BusinessObjectTreeNode defaultMutableTreeNode2 = (BusinessObjectTreeNode) defaultMutableTreeNode.getChildAt(i);
			openLastBusinessObjects(defaultMutableTreeNode2, waitDialog2);
		}
		if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {

			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

			if (basisClientConnector.getFUser().getOpenBusinessObjects().contains(abstractBusinessObject.getId())) {
				waitDialog2.doProgress();
				topPanel.getMainPanel().addBusinessObjectEditor(abstractBusinessObject, false);
			}
		}
	}

	private synchronized void initTree(WaitDialog waitDialog) {

		TreePath treePath = new TreePath(topNode);
		topNode.removeAllChildren();
		defaultTreeModel.reload();
		tree.expandPath(treePath);
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RootBusinessObjectRequest(RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof RootBusinessObjectResponse) {
			RootBusinessObjectResponse rootBusinessObjectResponse = (RootBusinessObjectResponse) abstractResponse;
			waitDialog.doProgress();
			int index = 0;
			if (rootBusinessObjectResponse != null)
				for (AbstractBusinessObject abstractBusinessObject : rootBusinessObjectResponse.getAbstractBusinessObjects()) {
					waitDialog.setIncrement(100d / (double) rootBusinessObjectResponse.getAbstractBusinessObjects().size());
					if (basisClientConnector.getFUser().canRead(abstractBusinessObject)) {

						BusinessObjectTreeNode defaultMutableTreeNode = new BusinessObjectTreeNode(abstractBusinessObject);
						defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode);
						defaultTreeModel.insertNodeInto(defaultMutableTreeNode, topNode, index);
						addChildren(waitDialog, abstractBusinessObject, defaultMutableTreeNode);
						index++;
					}
					else
						waitDialog.doProgress();
				}
		}
		else if (abstractResponse instanceof ErrorResponse) {
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(topPanel.getMainPanel(), errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		defaultTreeModel.reload();

		for (int i = 0; i < tree.getRowCount(); i++) {

			BusinessObjectTreeNode defaultMutableTreeNode = (BusinessObjectTreeNode) tree.getPathForRow(i).getLastPathComponent();

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {

				AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

				if (expandSet.contains(abstractBusinessObject.getId()))
					tree.expandRow(i);
			}
		}

		tree.validate();
		tree.repaint();

		BusinessObjectTreeNode dialogNode = new BusinessObjectTreeNode();
		copySubTree(dialogNode, topNode);
		topPanel.getMainPanel().setDialogNode(dialogNode);
	}

	/**
	 * Copy sub tree.
	 *
	 * @param subRoot the sub root
	 * @param sourceTree the source tree
	 * @return the object
	 */
	public Object copySubTree(BusinessObjectTreeNode subRoot, BusinessObjectTreeNode sourceTree) {

		if (sourceTree == null) {
			return subRoot;
		}
		for (int i = 0; i < sourceTree.getChildCount(); i++) {
			BusinessObjectTreeNode child = (BusinessObjectTreeNode) sourceTree.getChildAt(i);
			BusinessObjectTreeNode clone = new BusinessObjectTreeNode(child.getUserObject());
			subRoot.add(clone);
			copySubTree(clone, child);
		}
		return subRoot;
	}

	private void addChildren(WaitDialog waitDialog, AbstractBusinessObject abstractBusinessObject, MutableTreeNode mutableTreeNode) {

		int index = 0;

		List<AbstractBusinessObject> children = new ArrayList<AbstractBusinessObject>();

		if (abstractBusinessObject instanceof BusinessSection)
			children.addAll(((BusinessSection) abstractBusinessObject).getChildren());
		else if (abstractBusinessObject instanceof BusinessObjectGroup) {

			AbstractResponse abstractResponse = basisClientConnector.sendRequest(new ChildrenBusinessObjectRequest(abstractBusinessObject.getClass().getName(),
					abstractBusinessObject.getId(), RequestIDManager.getInstance().getID()));

			if (abstractResponse instanceof ChildrenBusinessObjectResponse) {
				ChildrenBusinessObjectResponse childrenBusinessObjectResponse = (ChildrenBusinessObjectResponse) abstractResponse;

				byte[] childBytes = childrenBusinessObjectResponse.getChildren();

				try {

					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(childBytes);
					GZIPInputStream gzip = new GZIPInputStream(byteArrayInputStream);
					ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
					@SuppressWarnings("unchecked")
					List<AbstractBusinessObject> abstractBusinessObjects = (ArrayList<AbstractBusinessObject>) objectInputStream.readObject();

					children.addAll(abstractBusinessObjects);
				}
				catch (IOException e) {
					log.error("Bug", e);
				}
				catch (ClassNotFoundException e) {
					log.error("Bug", e);
				}
			}
			else if (abstractResponse instanceof ErrorResponse) {
				ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
				JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		Collections.sort(children);
		if (waitDialog != null && children.size() == 0)
			waitDialog.doProgress();

		double lastIncrement = 1;

		if (waitDialog != null) {
			lastIncrement = waitDialog.getIncrement();
		}

		for (AbstractBusinessObject abstractBusinessObject2 : children) {
			if (waitDialog != null && waitDialog.getIncrement() > 0)
				waitDialog.setIncrement(lastIncrement / (double) children.size());
			if (basisClientConnector.getFUser().canRead(abstractBusinessObject2)) {

				BusinessObjectTreeNode defaultMutableTreeNode = new BusinessObjectTreeNode(abstractBusinessObject2);
				defaultMutableTreeNodeMap.put(abstractBusinessObject2.getId(), defaultMutableTreeNode);
				defaultTreeModel.insertNodeInto(defaultMutableTreeNode, mutableTreeNode, index);
				index++;
				addChildren(waitDialog, abstractBusinessObject2, defaultMutableTreeNode);
			}
			else if (waitDialog != null)
				waitDialog.doProgress();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onLoginResponse(net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse)
	 */
	@Override
	public void onLoginResponse(LoginResponse loginResponse) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onPersistResponse(net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse)
	 */
	@Override
	public synchronized void onPersistResponse(PersistResponse persistResponse) {

		AbstractBusinessObject abstractBusinessObject = persistResponse.getAbstractBusinessObject();
		AbstractBusinessObject parent = abstractBusinessObject.getParent();

		if (parent != null && basisClientConnector.getFUser().canRead(parent) && basisClientConnector.getFUser().canRead(abstractBusinessObject)) {

			BusinessObjectTreeNode defaultMutableTreeNode = defaultMutableTreeNodeMap.get(parent.getId());
			if (defaultMutableTreeNode != null) {

				AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(parent.getClass().getName(), parent.getId(),
						RequestIDManager.getInstance().getID()));
				if (abstractResponse instanceof RefreshResponse) {
					RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
					if (refreshResponse.getAbstractBusinessObject() != null) {
						defaultMutableTreeNode.setUserObject(refreshResponse.getAbstractBusinessObject());
						defaultTreeModel.nodeChanged(defaultMutableTreeNode);
					}
				}

				BusinessObjectTreeNode defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);

				defaultMutableTreeNode.add(defaultMutableTreeNode2);

				defaultMutableTreeNode.updateSortOrder();

				defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);

				defaultTreeModel.nodesWereInserted(defaultMutableTreeNode, new int[] { defaultMutableTreeNode.getIndex(defaultMutableTreeNode2) });
			}
		}

		viewport.validate();
		tree.validate();
		tree.repaint();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse)
	 */
	@Override
	public synchronized void onUpdateResponse(UpdateResponse updateResponse) {

		final Set<Long> updateParents = new HashSet<Long>();

		for (AbstractBusinessObject abstractBusinessObject : updateResponse.getAbstractBusinessObjects()) {

			AbstractBusinessObject oldParent = null;

			BusinessObjectTreeNode oldDefaultMutableTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());
			if (oldDefaultMutableTreeNode != null) {

				if (oldDefaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
					AbstractBusinessObject oldAbstractBusinessObject = (AbstractBusinessObject) oldDefaultMutableTreeNode.getUserObject();
					oldParent = oldAbstractBusinessObject.getParent();

				}
			}

			if (oldParent != null)
				updateParents.add(oldParent.getId());

			AbstractBusinessObject parent = abstractBusinessObject.getParent();

			if (parent != null)
				updateParents.add(parent.getId());

		}

		for (Long parentId : updateParents) {

			BusinessObjectTreeNode oldParentNode = defaultMutableTreeNodeMap.get(parentId);
			if (oldParentNode != null) {
				AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) oldParentNode.getUserObject();
				AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(abstractBusinessObject.getClass().getName(),
						abstractBusinessObject.getId(), RequestIDManager.getInstance().getID()));
				if (abstractResponse instanceof RefreshResponse) {
					RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
					if (refreshResponse.getAbstractBusinessObject() != null) {
						oldParentNode.setUserObject(refreshResponse.getAbstractBusinessObject());
						defaultTreeModel.nodeChanged(oldParentNode);
					}
				}
			}
		}

		for (AbstractBusinessObject abstractBusinessObject : updateResponse.getAbstractBusinessObjects())
			updateAbstractBusinessObject(abstractBusinessObject);

		viewport.validate();
		tree.revalidate();
		tree.repaint();

	}

	private void updateAbstractBusinessObject(final AbstractBusinessObject abstractBusinessObject) {

		if (abstractBusinessObject.getId() == basisClientConnector.getFUser().getId()) {

			final WaitDialog waitDialog = new WaitDialog(topPanel.getMainPanel(), "Update user profile ...");

			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					basisClientConnector.setFUser((FUser) abstractBusinessObject);
					initTree(waitDialog);
					topPanel.getMainPanel().checkAll(abstractBusinessObject);
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
		else {

			AbstractBusinessObject oldParent = null;

			BusinessObjectTreeNode oldDefaultMutableTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());
			if (oldDefaultMutableTreeNode != null) {

				if (oldDefaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
					AbstractBusinessObject oldAbstractBusinessObject = (AbstractBusinessObject) oldDefaultMutableTreeNode.getUserObject();
					oldParent = oldAbstractBusinessObject.getParent();

				}
			}

			AbstractBusinessObject parent = abstractBusinessObject.getParent();

			BusinessObjectTreeNode defaultMutableTreeNode = topNode;

			if (parent != null)
				defaultMutableTreeNode = defaultMutableTreeNodeMap.get(parent.getId());

			if (defaultMutableTreeNode != null)
				parent = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

			if ((parent == null || basisClientConnector.getFUser().canRead(parent)) && basisClientConnector.getFUser().canRead(abstractBusinessObject)) {

				if (oldParent != null && parent != null && oldParent.getId() != parent.getId()) {

					defaultTreeModel.removeNodeFromParent(oldDefaultMutableTreeNode);
					defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());

					BusinessObjectTreeNode defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);
					defaultMutableTreeNode.add(defaultMutableTreeNode2);
					defaultMutableTreeNode.updateSortOrder();

					defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);

					defaultTreeModel.nodesWereInserted(defaultMutableTreeNode, new int[] { defaultMutableTreeNode.getIndex(defaultMutableTreeNode2) });

				}
				else if (defaultMutableTreeNode != null) {
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
					}
					else {
						BusinessObjectTreeNode defaultMutableTreeNode2 = new BusinessObjectTreeNode(abstractBusinessObject);

						addChildren(null, abstractBusinessObject, defaultMutableTreeNode2);

						defaultMutableTreeNode.add(defaultMutableTreeNode2);
						defaultMutableTreeNode.updateSortOrder();

						defaultMutableTreeNodeMap.put(abstractBusinessObject.getId(), defaultMutableTreeNode2);

						defaultTreeModel.nodesWereInserted(defaultMutableTreeNode, new int[] { defaultMutableTreeNode.getIndex(defaultMutableTreeNode2) });
					}

					int[] childIndices = new int[defaultMutableTreeNode.getChildCount()];
					for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++)
						childIndices[i] = i;
					defaultMutableTreeNode.updateSortOrder();
					defaultTreeModel.nodeChanged(defaultMutableTreeNode);
					defaultTreeModel.nodesChanged(defaultMutableTreeNode, childIndices);
				}

			}
			else if (oldDefaultMutableTreeNode != null) {
				defaultTreeModel.removeNodeFromParent(oldDefaultMutableTreeNode);
				defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());
			}

		}

		TreePath[] treePaths = tree.getSelectionPaths();
		tree.clearSelection();
		tree.getSelectionModel().setSelectionPaths(treePaths);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

		abstractResponse.handleAbstractResponse(this);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onRemoveResponse(net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse)
	 */
	@Override
	public synchronized void onRemoveResponse(RemoveResponse removeResponse) {

		Set<AbstractBusinessObject> updateParents = new HashSet<AbstractBusinessObject>();

		for (AbstractBusinessObject abstractBusinessObject : removeResponse.getAbstractBusinessObjects()) {
			AbstractBusinessObject oldParent = null;

			BusinessObjectTreeNode oldDefaultMutableTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());
			if (oldDefaultMutableTreeNode != null) {

				if (oldDefaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
					AbstractBusinessObject oldAbstractBusinessObject = (AbstractBusinessObject) oldDefaultMutableTreeNode.getUserObject();
					oldParent = oldAbstractBusinessObject.getParent();

				}
			}

			if (oldParent != null)
				updateParents.add(oldParent);

		}

		for (AbstractBusinessObject abstractBusinessObject : removeResponse.getAbstractBusinessObjects()) {

			if (abstractBusinessObject.getId() == basisClientConnector.getFUser().getId())
				basisClientConnector.disconnect();

			BusinessObjectTreeNode oldDefaultMutableTreeNode = defaultMutableTreeNodeMap.get(abstractBusinessObject.getId());

			if (oldDefaultMutableTreeNode != null) {

				defaultTreeModel.removeNodeFromParent(oldDefaultMutableTreeNode);
				defaultMutableTreeNodeMap.remove(abstractBusinessObject.getId());
			}

		}

		for (AbstractBusinessObject parent : updateParents) {

			BusinessObjectTreeNode oldParentNode = defaultMutableTreeNodeMap.get(parent.getId());
			if (oldParentNode != null) {
				AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(parent.getClass().getName(), parent.getId(),
						RequestIDManager.getInstance().getID()));
				if (abstractResponse instanceof RefreshResponse) {
					RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
					if (refreshResponse.getAbstractBusinessObject() != null) {
						oldParentNode.setUserObject(refreshResponse.getAbstractBusinessObject());
						defaultTreeModel.nodeChanged(oldParentNode);
					}
				}
			}
		}

		viewport.validate();
		tree.validate();
		tree.repaint();

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

		final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, blue.darker(), width / 2.F, 25, blue);

		graphics2D.setPaint(gradientPaint);
		graphics2D.fillRoundRect(1, 1, width - 2, height / 2, 14, 14);

		final GradientPaint gradientPaint2 = new GradientPaint(width / 2.F, height - 27, blue, width / 2.F, height, blue.darker());

		graphics2D.setPaint(gradientPaint2);
		graphics2D.fillRoundRect(1, height - 32, width - 2, 30, 14, 14);

		graphics2D.setColor(Color.GRAY);
		graphics2D.drawRoundRect(1, height - 32, width - 3, 30, 14, 14);
		graphics2D.drawRect(1, 26, width - 3, height - 53);

		graphics2D.setPaint(blue);
		graphics2D.fillRect(2, height - 50, width - 4, 22);

		graphics2D.setFont(new Font("Dialog", Font.PLAIN, 12));

		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setPaintMode();

		graphics2D.drawImage(logoIconImageImage, 12, 6, this);

		graphics2D.setColor(Color.WHITE);

		graphics2D.drawString("Business Object Browser", 32, 18);

		getUI().paint(graphics2D, this);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetCellResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse)
	 */
	@Override
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetCellFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse)
	 */
	@Override
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateColumnFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse)
	 */
	@Override
	public void onUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateRowFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse)
	 */
	@Override
	public void onUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetConditionalFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse)
	 */
	@Override
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateFullSheetResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse)
	 */
	@Override
	public void onUpdateFullSheetResponse(UpdateFullSheetResponse updateFullSheetResponse) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

		// TODO Auto-generated method stub

	}
	
	private synchronized void expanded(TreeExpansionEvent event) {

		TreePath treePath = event.getPath();
		BusinessObjectTreeNode node = (BusinessObjectTreeNode) treePath.getLastPathComponent();

		if (node.getUserObject() instanceof AbstractBusinessObject) {

			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) node.getUserObject();
			expandSet.add(abstractBusinessObject.getId());
		}
	}
	
	private synchronized void collapsed(TreeExpansionEvent event) {

		TreePath treePath = event.getPath();
		BusinessObjectTreeNode node = (BusinessObjectTreeNode) treePath.getLastPathComponent();
		if (node.getUserObject() instanceof AbstractBusinessObject) {

			AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) node.getUserObject();
			expandSet.remove(abstractBusinessObject.getId());
		}
	}
	
	private synchronized void selectionChanged(final TreeSelectionEvent treeSelectionEvent) {

		List<AbstractBusinessObject> abstractBusinessObjects = new ArrayList<AbstractBusinessObject>();

		List<AbstractBusinessObject> abstractBusinessObjectsWithChildren = new ArrayList<AbstractBusinessObject>();

		if (tree.getSelectionPaths() != null)
			for (TreePath treePath : tree.getSelectionPaths()) {

				BusinessObjectTreeNode node = (BusinessObjectTreeNode) treePath.getLastPathComponent();
				Object object = node.getUserObject();
				if (object instanceof AbstractBusinessObject) {
					abstractBusinessObjects.add((AbstractBusinessObject) object);
					abstractBusinessObjectsWithChildren.add((AbstractBusinessObject) object);
					addChildren(node, abstractBusinessObjectsWithChildren);
				}
			}

		topPanel.treeSelectionChanged(abstractBusinessObjects, abstractBusinessObjectsWithChildren);
	}

	private void addChildren(BusinessObjectTreeNode node, List<AbstractBusinessObject> abstractBusinessObjectsWithChildren) {

		for (int i = 0; i < node.getChildCount(); i++) {
			BusinessObjectTreeNode defaultMutableTreeNode = (BusinessObjectTreeNode) node.getChildAt(i);
			Object object = defaultMutableTreeNode.getUserObject();
			if (object instanceof AbstractBusinessObject) {
				abstractBusinessObjectsWithChildren.add((AbstractBusinessObject) object);
				addChildren(defaultMutableTreeNode, abstractBusinessObjectsWithChildren);
			}
		}

	}
	
}
