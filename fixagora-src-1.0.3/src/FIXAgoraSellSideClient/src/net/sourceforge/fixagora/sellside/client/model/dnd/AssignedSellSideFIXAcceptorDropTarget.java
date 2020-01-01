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
package net.sourceforge.fixagora.sellside.client.model.dnd;

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
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.sellside.client.model.editor.AssignedSellSideFIXAcceptorTableModel;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditor;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditorAssignedFIXAcceptor;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideAcceptor;

/**
 * The Class AssignedSellSideFIXAcceptorDropTarget.
 */
public class AssignedSellSideFIXAcceptorDropTarget implements DropTargetListener {

	private SellSideBookEditor sellSideBookEditor = null;

	private SellSideBookEditorAssignedFIXAcceptor sellSideBookEditorAssignedFIXAcceptor = null;

	private AssignedSellSideFIXAcceptorTableModel assignedSellSideFIXIniatorTableModel = null;

	/**
	 * Instantiates a new assigned sell side fix acceptor drop target.
	 *
	 * @param targetJTable the target j table
	 * @param sellSideBookEditor the sell side book editor
	 * @param sellSideBookEditorAssignedFIXAcceptor the sell side book editor assigned fix acceptor
	 * @param assignedSellSideFIXIniatorTableModel the assigned sell side fix iniator table model
	 */
	public AssignedSellSideFIXAcceptorDropTarget(JTable targetJTable, SellSideBookEditor sellSideBookEditor,SellSideBookEditorAssignedFIXAcceptor sellSideBookEditorAssignedFIXAcceptor,
			AssignedSellSideFIXAcceptorTableModel assignedSellSideFIXIniatorTableModel) {

		new DropTarget(targetJTable, this);
		this.sellSideBookEditor = sellSideBookEditor;
		this.sellSideBookEditorAssignedFIXAcceptor = sellSideBookEditorAssignedFIXAcceptor;
		this.assignedSellSideFIXIniatorTableModel = assignedSellSideFIXIniatorTableModel;
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
		if (sellSideBookEditor.getBasisClientConnector().getFUser().canWrite(sellSideBookEditor.getSellSideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				boolean allMatched = true;
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (!(defaultMutableTreeNode.getUserObject() instanceof AbstractAcceptor))
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

		if (sellSideBookEditor.getBasisClientConnector().getFUser().canWrite(sellSideBookEditor.getSellSideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				List<AbstractAcceptor> fixAcceptors = new ArrayList<AbstractAcceptor>();
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (defaultMutableTreeNode.getUserObject() instanceof AbstractAcceptor) {
						fixAcceptors.add((AbstractAcceptor) defaultMutableTreeNode.getUserObject());
					}
				}

				for (AbstractAcceptor fixAcceptor : fixAcceptors) {
					AssignedSellSideAcceptor assignedSellSideFIXAcceptor = new AssignedSellSideAcceptor();
					assignedSellSideFIXAcceptor.setSellSideBook(sellSideBookEditor.getSellSideBook());
					assignedSellSideFIXAcceptor.setAcceptor(fixAcceptor);
					assignedSellSideFIXIniatorTableModel.addAssignedSellSideBookSecurity(assignedSellSideFIXAcceptor);
					sellSideBookEditorAssignedFIXAcceptor.checkConsistency();
				}
			}
			catch (Exception e) {
			}

		}

		dtde.dropComplete(true);

	}
}
