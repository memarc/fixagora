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
package net.sourceforge.fixagora.basis.client.model.dnd;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;

/**
 * The Class TreeDropTarget.
 */
public class TreeDropTarget implements DropTargetListener {

	private JTree targetJTree = null;

	private BasisClientConnector basisClientConnector = null;

	private MainPanel mainPanel = null;
	
	private static Logger log = Logger.getLogger(TreeDropTarget.class);

	/**
	 * Instantiates a new tree drop target.
	 *
	 * @param tree the tree
	 * @param basisClientConnector the basis client connector
	 * @param mainPanel the main panel
	 */
	public TreeDropTarget(JTree tree, BasisClientConnector basisClientConnector, MainPanel mainPanel) {

		targetJTree = tree;
		new DropTarget(targetJTree, this);
		this.basisClientConnector = basisClientConnector;
		this.mainPanel = mainPanel;
	}

	private DefaultMutableTreeNode getNodeForEvent(DropTargetDropEvent dtde) {

		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		return (DefaultMutableTreeNode) path.getLastPathComponent();
	}

	private DefaultMutableTreeNode getNodeForEvent(DropTargetDragEvent dtde) {

		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		return (DefaultMutableTreeNode) path.getLastPathComponent();
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dragEnter(DropTargetDragEvent dtde) {

	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dragOver(DropTargetDragEvent dtde) {

		int action = DnDConstants.ACTION_NONE;
		DefaultMutableTreeNode node = getNodeForEvent(dtde);
		if (node != null && node.getUserObject() instanceof BusinessObjectGroup && !(node.getUserObject() instanceof BusinessSection)) {
			BusinessObjectGroup businessObjectGroup = (BusinessObjectGroup) node.getUserObject();
			if (basisClientConnector.getFUser().canWrite((AbstractBusinessObject) businessObjectGroup)) {
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
							AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();
							if (basisClientConnector.getFUser().canWrite(abstractBusinessObject)) {
								if (businessObjectGroup.getClass() != abstractBusinessObject.getClass()
										&& !businessObjectGroup.getChildClass().isAssignableFrom(abstractBusinessObject.getClass())) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getParent().getId() == ((AbstractBusinessObject) businessObjectGroup).getId()) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getId() == ((AbstractBusinessObject) businessObjectGroup).getId()) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getParent() instanceof BusinessSection) {
									allMatched = false;
								}
								else if (!abstractBusinessObject.isMovable())
									allMatched = false;
							}
							else
								allMatched = false;
						}
						else
							allMatched = false;
					}
					if (allMatched)
						action = DnDConstants.ACTION_MOVE;
				}
				catch (Exception e) {
					
					log.error("Bug", e);

				}
			}
		}
		dtde.acceptDrag(action);
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent dte) {

	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {

		DefaultMutableTreeNode node = getNodeForEvent(dtde);

		if (node != null && node.getUserObject() instanceof BusinessObjectGroup && !(node.getUserObject() instanceof BusinessSection)) {

			BusinessObjectGroup businessObjectGroup = (BusinessObjectGroup) node.getUserObject();

			if (basisClientConnector.getFUser().canWrite((AbstractBusinessObject) businessObjectGroup)) {
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					final List<AbstractBusinessObject> abstractBusinessObjects = new ArrayList<AbstractBusinessObject>();
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
							AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();
							if (basisClientConnector.getFUser().canWrite(abstractBusinessObject)) {
								if (businessObjectGroup.getClass() != abstractBusinessObject.getClass()
										&& !businessObjectGroup.getChildClass().isAssignableFrom(abstractBusinessObject.getClass())) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getParent().getId() == ((AbstractBusinessObject) businessObjectGroup).getId()) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getId() == ((AbstractBusinessObject) businessObjectGroup).getId()) {
									allMatched = false;
								}
								else if (abstractBusinessObject.getParent() instanceof BusinessSection) {
									allMatched = false;
								}
								else {
									abstractBusinessObject.setParent((AbstractBusinessObject) businessObjectGroup);
									abstractBusinessObjects.add(abstractBusinessObject);
								}
							}
							else
								allMatched = false;
						}
						else
							allMatched = false;
					}
					if (allMatched) {
						final WaitDialog waitDialog = new WaitDialog(mainPanel, "Update tree structure ...");

						final SwingWorker<AbstractResponse, Void> swingWorker = new SwingWorker<AbstractResponse, Void>() {

							@Override
							protected AbstractResponse doInBackground() throws Exception {

								UpdateRequest updateRequest = new UpdateRequest(abstractBusinessObjects, RequestIDManager.getInstance().getID());
								AbstractResponse abstractResponse = basisClientConnector.sendRequest(updateRequest);
								return abstractResponse;
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
				}
				catch (Exception e) {
				}
			}
		}
		dtde.dropComplete(true);

	}
}
