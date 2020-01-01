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
package net.sourceforge.fixagora.buyside.client.model.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.fixagora.basis.client.model.dnd.TransferableTreeNode;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.model.editor.AssignedBuySideSecurityTableModel;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditorAssignedSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;

/**
 * The Class AssignedBuySideSecurityDropTarget.
 */
public class AssignedBuySideSecurityDropTarget implements DropTargetListener {

	private BuySideBookEditor buySideBookEditor = null;

	private BuySideBookEditorAssignedSecurity buySideBookEditorAssignedSecurity = null;

	private AssignedBuySideSecurityTableModel assignedSecurityTableModel = null;

	/**
	 * Instantiates a new assigned buy side security drop target.
	 *
	 * @param targetJTable the target j table
	 * @param buySideBookEditor the buy side book editor
	 * @param buySideBookEditorAssignedSecurity the buy side book editor assigned security
	 * @param assignedSecurityTableModel the assigned security table model
	 */
	public AssignedBuySideSecurityDropTarget(JTable targetJTable, BuySideBookEditor buySideBookEditor,BuySideBookEditorAssignedSecurity buySideBookEditorAssignedSecurity,
			AssignedBuySideSecurityTableModel assignedSecurityTableModel) {

		new DropTarget(targetJTable, this);
		this.buySideBookEditor = buySideBookEditor;
		this.buySideBookEditorAssignedSecurity = buySideBookEditorAssignedSecurity;
		this.assignedSecurityTableModel = assignedSecurityTableModel;
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
		if (buySideBookEditor.getBasisClientConnector().getFUser().canWrite(buySideBookEditor.getBuySideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				boolean allMatched = true;
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (!(defaultMutableTreeNode.getUserObject() instanceof FSecurity))
						allMatched = false;
				}
				if (allMatched)
					action = DnDConstants.ACTION_COPY;
			}
			catch (Exception e) {
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

		if (buySideBookEditor.getBasisClientConnector().getFUser().canWrite(buySideBookEditor.getBuySideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				List<FSecurity> securities = new ArrayList<FSecurity>();
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (defaultMutableTreeNode.getUserObject() instanceof FSecurity) {
						securities.add((FSecurity) defaultMutableTreeNode.getUserObject());
					}
				}

				for (FSecurity security : securities) {
					AssignedBuySideBookSecurity assignedBuySideBookSecurity = new AssignedBuySideBookSecurity();
					assignedBuySideBookSecurity.setBuySideBook(buySideBookEditor.getBuySideBook());
					assignedBuySideBookSecurity.setSecurity(security);
					assignedBuySideBookSecurity.setDaycountConvention(1);
					assignedSecurityTableModel.addAssignedBuySideBookSecurity(assignedBuySideBookSecurity);
					buySideBookEditorAssignedSecurity.checkConsistency();
				}
			}
			catch (Exception e) {
			}

		}

		dtde.dropComplete(true);

	}
}
