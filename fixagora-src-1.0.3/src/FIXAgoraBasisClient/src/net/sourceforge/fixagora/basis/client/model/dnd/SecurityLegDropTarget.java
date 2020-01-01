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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.fixagora.basis.client.model.editor.SecurityLegTableModel;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditorLeg;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityLeg;

/**
 * The Class SecurityLegDropTarget.
 */
public class SecurityLegDropTarget implements DropTargetListener {

	/** The drop target. */
	DropTarget dropTarget = null;

	/** The target j table. */
	JTable targetJTable = null;

	private FSecurityEditor securityEditor;

	private  FSecurityEditorLeg securityEditorLeg;

	private SecurityLegTableModel securityLegTableModel;

	/**
	 * Instantiates a new security leg drop target.
	 *
	 * @param targetJTable the target j table
	 * @param securityEditor the security editor
	 * @param securityEditorLeg the security editor leg
	 * @param securityLegTableModel the security leg table model
	 */
	public SecurityLegDropTarget(JTable targetJTable, FSecurityEditor securityEditor, FSecurityEditorLeg securityEditorLeg, SecurityLegTableModel securityLegTableModel) {

		this.targetJTable = targetJTable;
		dropTarget = new DropTarget(targetJTable, this);
		this.securityEditor = securityEditor;
		this.securityEditorLeg = securityEditorLeg;
		this.securityLegTableModel = securityLegTableModel;
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
		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getSecurity())) {
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (!(defaultMutableTreeNode.getUserObject() instanceof FSecurity))
								allMatched = false;
						else if (((AbstractBusinessObject)defaultMutableTreeNode.getUserObject()).getId()==securityEditor.getSecurity().getId())
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

		if (securityEditor.getBasisClientConnector().getFUser().canWrite(securityEditor.getSecurity())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (defaultMutableTreeNode.getUserObject() instanceof FSecurity)
					{	
						SecurityLeg securityLeg = new SecurityLeg();
						securityLeg.setSecurity(securityEditor.getSecurity().getSecurityDetails());
						securityLeg.setLegSecurity((FSecurity)defaultMutableTreeNode.getUserObject());
						securityLegTableModel.addSecurityLeg(securityLeg);
						securityEditorLeg.checkConsistency();
						
					}
				}
			}
			catch (Exception e) {
			}

		}

		dtde.dropComplete(true);

	}
}
