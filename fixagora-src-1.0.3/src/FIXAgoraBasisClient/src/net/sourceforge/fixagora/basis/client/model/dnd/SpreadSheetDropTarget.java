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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.poi.ss.util.CellReference;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetTableModel;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;

/**
 * The Class SpreadSheetDropTarget.
 */
public class SpreadSheetDropTarget implements DropTargetListener {

	/** The drop target. */
	DropTarget dropTarget = null;

	/** The target j table. */
	JTable targetJTable = null;

	private SpreadSheetEditor spreadSheetEditor;

	/**
	 * Instantiates a new spread sheet drop target.
	 *
	 * @param targetJTable the target j table
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetDropTarget(JTable targetJTable, SpreadSheetEditor spreadSheetEditor) {

		this.targetJTable = targetJTable;
		dropTarget = new DropTarget(targetJTable, this);
		this.spreadSheetEditor = spreadSheetEditor;
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
		if (spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {
			SpreadSheetTableModel spreadSheetTableModel = (SpreadSheetTableModel) targetJTable.getModel();
			Set<String> cellRefs = new HashSet<String>();
			int row = targetJTable.rowAtPoint(dtde.getLocation());
			int column = targetJTable.columnAtPoint(dtde.getLocation());
			if (row != -1 && column != -1) {
				row = targetJTable.convertRowIndexToModel(row);
				column = targetJTable.convertColumnIndexToModel(column);
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject
								&& !(defaultMutableTreeNode.getUserObject() instanceof BusinessObjectGroup)) {
							SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(row, column);
							cellRefs.add(CellReference.convertNumToColString(column)+row);
							if (!(spreadSheetCellFormat == null || spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat
										.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME))
								allMatched = false;
							else if (!spreadSheetTableModel.isCellEditable(row, column))
								allMatched = false;
							else if(defaultMutableTreeNode.getUserObject() instanceof FSecurity)
							{
								cellRefs.add(CellReference.convertNumToColString(column+1)+row);
								SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetTableModel.getSpreadSheetCellFormatAt(row, column+1);
								if (!(spreadSheetCellFormat2 == null || spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat2
											.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME))
									allMatched = false;								
							}
							row++;	
						}
					}
					if (allMatched)
					{
						spreadSheetTableModel.addDropShadow(cellRefs);
						action = DnDConstants.ACTION_COPY;
					}
					else
						spreadSheetTableModel.addDropShadow(new HashSet<String>());
					targetJTable.repaint();
				}
				catch (Exception e) {
				}

			}

		}
		dtde.acceptDrag(action);
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent dte) {

		SpreadSheetTableModel spreadSheetTableModel = (SpreadSheetTableModel) targetJTable.getModel();
		spreadSheetTableModel.addDropShadow(new HashSet<String>());
		targetJTable.repaint();
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

		if (spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {
			SpreadSheetTableModel spreadSheetTableModel = (SpreadSheetTableModel) targetJTable.getModel();
			int startRow = targetJTable.rowAtPoint(dtde.getLocation());
			int column = targetJTable.columnAtPoint(dtde.getLocation());
			if (startRow != -1 && column != -1) {
				startRow = targetJTable.convertRowIndexToModel(startRow);
				column = targetJTable.convertColumnIndexToModel(column);
				try {
					@SuppressWarnings("unchecked")
					List<TreePath> treePaths = (List<TreePath>) dtde.getTransferable().getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
					boolean allMatched = true;
					int row =startRow;
					for (TreePath treePath : treePaths) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject
								&& !(defaultMutableTreeNode.getUserObject() instanceof BusinessObjectGroup)) {
							SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(row, column);
							if (!(spreadSheetCellFormat == null || spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat
										.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME))
								allMatched = false;
							else if (!spreadSheetTableModel.isCellEditable(row, column))
								allMatched = false;
							else if(defaultMutableTreeNode.getUserObject() instanceof FSecurity)
							{
								SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetTableModel.getSpreadSheetCellFormatAt(row, column+1);
								if (!(spreadSheetCellFormat2 == null || spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
										|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat2
											.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME))
									allMatched = false;								
							}
							row++;
						}
					}
					row = startRow;
					if (allMatched)
						for (TreePath treePath : treePaths) {
							DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
							if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject
									&& !(defaultMutableTreeNode.getUserObject() instanceof BusinessObjectGroup)) {
								AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject)defaultMutableTreeNode
										.getUserObject();
								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(),null, "HYPERLINK(\"agora://"+ abstractBusinessObject.getName()+"\",\""+abstractBusinessObject.getName()+"\")", row, column);
								spreadSheetEditor.getBasisClientConnector().sendRequest(
										new ModifySheetCellRequest(spreadSheetCell, RequestIDManager.getInstance().getID()));
								
								if(abstractBusinessObject instanceof FSecurity)
								{
									FSecurity fSecurity = (FSecurity)abstractBusinessObject;
									SpreadSheetCell spreadSheetCell2 = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(), fSecurity.getSecurityID(), null, row, column+1);
									spreadSheetEditor.getBasisClientConnector().sendRequest(
											new ModifySheetCellRequest(spreadSheetCell2, RequestIDManager.getInstance().getID()));
								}
								
								row++;
							}
						}
				}
				catch (Exception e) {
				}

			}
			spreadSheetTableModel.addDropShadow(new HashSet<String>());
			targetJTable.repaint();
		}
		dtde.dropComplete(true);

	}
}
