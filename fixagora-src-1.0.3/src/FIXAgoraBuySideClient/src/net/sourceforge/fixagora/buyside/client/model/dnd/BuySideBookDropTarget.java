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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.fixagora.basis.client.model.dnd.TransferableTreeNode;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;

/**
 * The Class BuySideBookDropTarget.
 */
public class BuySideBookDropTarget implements DropTargetListener {

	/** The drop target. */
	DropTarget dropTarget = null;

	/** The target j table. */
	JTable targetJTable = null;

	private BuySideBookEditor buySideBookEditor;

	/**
	 * Instantiates a new buy side book drop target.
	 *
	 * @param targetJTable the target j table
	 * @param buySideBookEditor the buy side book editor
	 */
	public BuySideBookDropTarget(JTable targetJTable, BuySideBookEditor buySideBookEditor) {

		this.targetJTable = targetJTable;
		dropTarget = new DropTarget(targetJTable, this);
		this.buySideBookEditor = buySideBookEditor;
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
		if (buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				boolean allMatched = true;
				Set<FSecurity> securities = new HashSet<FSecurity>();
				Set<Counterparty> counterparties = new HashSet<Counterparty>();
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

					if (defaultMutableTreeNode.getUserObject() instanceof FSecurity) {
						securities.add((FSecurity) defaultMutableTreeNode.getUserObject());
					}
					else if (defaultMutableTreeNode.getUserObject() instanceof Counterparty) {
						counterparties.add((Counterparty) defaultMutableTreeNode.getUserObject());
					}
					else
						allMatched = false;
				}
				if (allMatched) {

					if (securities.size() == 1 || counterparties.size() == 1)
						action = DnDConstants.ACTION_COPY;
					if (securities.size() > 1 && counterparties.size() < 2)
						action = DnDConstants.ACTION_COPY;
					if (securities.size() < 2 && counterparties.size() > 1)
						action = DnDConstants.ACTION_COPY;
				}
				targetJTable.repaint();
			}
			catch (Exception e) {
			}

		}
		dtde.acceptDrag(action);
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

		if (buySideBookEditor.getBasisClientConnector().getFUser().canExecute(buySideBookEditor.getBuySideBook())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				boolean allMatched = true;
				Set<FSecurity> securities = new HashSet<FSecurity>();
				Set<Counterparty> counterparties = new HashSet<Counterparty>();
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

					if (defaultMutableTreeNode.getUserObject() instanceof FSecurity) {
						securities.add((FSecurity) defaultMutableTreeNode.getUserObject());
					}
					else if (defaultMutableTreeNode.getUserObject() instanceof Counterparty) {
						counterparties.add((Counterparty) defaultMutableTreeNode.getUserObject());
					}
					else
						allMatched = false;
				}
				if (allMatched) {

					if (securities.size() == 1 || counterparties.size() == 1 || (securities.size() > 1 && counterparties.size() < 2)
							|| (securities.size() < 2 && counterparties.size() > 1)) {

						if (securities.size() > 0) {
							for (FSecurity security : securities) {
								if (counterparties.size() > 0) {
									for (Counterparty counterparty : counterparties) {
										BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
										buySideNewOrderSingleEntry.setSecurity(security.getId());
										buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
										buySideNewOrderSingleEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
										buySideBookEditor.addNewOrderSingle(buySideNewOrderSingleEntry);
									}
								}
								else {
									BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
									buySideNewOrderSingleEntry.setSecurity(security.getId());
									buySideNewOrderSingleEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
									buySideBookEditor.addNewOrderSingle(buySideNewOrderSingleEntry);
								}
							}
						}
						else if (counterparties.size() > 0) {

							for (Counterparty counterparty : counterparties) {
								if (securities.size() > 0) {
									for (FSecurity security : securities)

									{
										BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
										buySideNewOrderSingleEntry.setSecurity(security.getId());
										buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
										buySideNewOrderSingleEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
										buySideBookEditor.addNewOrderSingle(buySideNewOrderSingleEntry);
									}
								}
								else {
									BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = new BuySideNewOrderSingleEntry();
									buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());
									buySideNewOrderSingleEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());
									buySideBookEditor.addNewOrderSingle(buySideNewOrderSingleEntry);
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
			}

		}
		dtde.dropComplete(true);

	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragExit(DropTargetEvent dte) {

	}
}
