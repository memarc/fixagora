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

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;

/**
 * The Class TreeDragSource.
 */
public class TreeDragSource implements DragSourceListener, DragGestureListener {

	/** The source. */
	DragSource source = null;

	/** The recognizer. */
	DragGestureRecognizer recognizer = null;

	/** The transferable. */
	TransferableTreeNode transferable = null;

	/** The basis client connector. */
	BasisClientConnector basisClientConnector = null;
	
	/** The source tree. */
	JTree sourceTree = null;

	/**
	 * Instantiates a new tree drag source.
	 *
	 * @param tree the tree
	 * @param actions the actions
	 * @param basisClientConnector the basis client connector
	 */
	public TreeDragSource(JTree tree, int actions, BasisClientConnector basisClientConnector) {
		
		sourceTree = tree;
		this.basisClientConnector = basisClientConnector;
		source = new DragSource();
		
		recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {

		TreePath[] paths = sourceTree.getSelectionPaths();
		if ((paths == null)) {
			return;
		}
		boolean sorted = true;
		do
		{
			sorted = true;
			for(int i=1;i<paths.length; i++)
			{
				if(sourceTree.getRowForPath(paths[i])<sourceTree.getRowForPath(paths[i-1]))
				{
					TreePath tmp = paths[i-1];
					paths[i-1] = paths[i];
					paths[i] = tmp;
					sorted = false;
				}
			}
		}
		while(!sorted);
		transferable = new TransferableTreeNode(new ArrayList<TreePath>(Arrays.asList(paths)));
		source.startDrag(dge, null, transferable, this);
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
	 */
	public void dragExit(DragSourceEvent dse) {

	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragOver(DragSourceDragEvent dsde) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dropActionChanged(DragSourceDragEvent dsde) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
	 */
	public void dragDropEnd(DragSourceDropEvent dsde) {

	}
}