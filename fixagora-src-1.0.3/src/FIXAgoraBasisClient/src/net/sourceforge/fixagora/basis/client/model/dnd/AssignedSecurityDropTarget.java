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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.fixagora.basis.client.model.editor.AssignedSecurityTableModel;
import net.sourceforge.fixagora.basis.client.view.editor.FIXInitiatorEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FIXInitiatorEditorAssignedSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.AssignedInitiatorSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;

/**
 * The Class AssignedSecurityDropTarget.
 */
public class AssignedSecurityDropTarget implements DropTargetListener {

	private FIXInitiatorEditor fixInitiatorEditor = null;

	private  FIXInitiatorEditorAssignedSecurity fixInitiatorEditorAssignedSecurity = null;

	private AssignedSecurityTableModel assignedSecurityTableModel = null;
	
	/**
	 * Instantiates a new assigned security drop target.
	 *
	 * @param targetJTable the target j table
	 * @param fixInitiatorEditor the fix initiator editor
	 * @param fixInitiatorEditorAssignedSecurity the fix initiator editor assigned security
	 * @param assignedSecurityTableModel the assigned security table model
	 */
	public AssignedSecurityDropTarget(JTable targetJTable, FIXInitiatorEditor fixInitiatorEditor, FIXInitiatorEditorAssignedSecurity fixInitiatorEditorAssignedSecurity, AssignedSecurityTableModel assignedSecurityTableModel) {

		new DropTarget(targetJTable, this);
		this.fixInitiatorEditor = fixInitiatorEditor;
		this.fixInitiatorEditorAssignedSecurity = fixInitiatorEditorAssignedSecurity;
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
		if (fixInitiatorEditor.getBasisClientConnector().getFUser().canWrite(fixInitiatorEditor.getFixInitiator())&&fixInitiatorEditor.getFixInitiator().getStartLevel()==0) {
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					boolean atLeastOneSecurity = false;
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (!(defaultMutableTreeNode.getUserObject() instanceof FSecurity||defaultMutableTreeNode.getUserObject() instanceof Counterparty))
								allMatched = false;
						if (defaultMutableTreeNode.getUserObject() instanceof FSecurity)
							atLeastOneSecurity = true;
					}
					if (allMatched&&atLeastOneSecurity)
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

		if (fixInitiatorEditor.getBasisClientConnector().getFUser().canWrite(fixInitiatorEditor.getFixInitiator())) {
			try {
				@SuppressWarnings("unchecked")
				List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
				List<FSecurity> securities = new ArrayList<FSecurity>();
				List<Counterparty> counterparties = new ArrayList<Counterparty>();
				for (TreePath treePath : treePaths) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					if (defaultMutableTreeNode.getUserObject() instanceof FSecurity)
					{	
						securities.add((FSecurity)defaultMutableTreeNode.getUserObject());
					}
					if (defaultMutableTreeNode.getUserObject() instanceof Counterparty)
					{	
						counterparties.add((Counterparty)defaultMutableTreeNode.getUserObject());
					}
				}
				
				for(FSecurity security: securities)
				{
					if(counterparties.size()==0)
					{			
						AssignedInitiatorSecurity assignedInitiatorSecurity = new AssignedInitiatorSecurity();
						assignedInitiatorSecurity.setFixInitiator(fixInitiatorEditor.getFixInitiator());
						assignedInitiatorSecurity.setSecurity(security);
						assignedSecurityTableModel.addAssignedInitiatorSecurity(assignedInitiatorSecurity);
						fixInitiatorEditorAssignedSecurity.checkConsistency();
					}
					else for(Counterparty counterparty: counterparties)
					{
						AssignedInitiatorSecurity assignedInitiatorSecurity = new AssignedInitiatorSecurity();
						assignedInitiatorSecurity.setFixInitiator(fixInitiatorEditor.getFixInitiator());
						assignedInitiatorSecurity.setSecurity(security);
						assignedInitiatorSecurity.setCounterparty(counterparty);
						assignedSecurityTableModel.addAssignedInitiatorSecurity(assignedInitiatorSecurity);
						fixInitiatorEditorAssignedSecurity.checkConsistency();						
					}
				}
			}
			catch (Exception e) {
			}

		}

		dtde.dropComplete(true);

	}
}
