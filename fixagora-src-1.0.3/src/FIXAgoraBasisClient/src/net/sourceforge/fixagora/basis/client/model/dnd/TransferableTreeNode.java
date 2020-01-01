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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSourceContext;
import java.io.IOException;
import java.util.List;

import javax.swing.tree.TreePath;


/**
 * The Class TransferableTreeNode.
 */
public class TransferableTreeNode implements Transferable {

	  /** The tree path flavor. */
  	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
	      "Tree Path");

	  private DataFlavor flavors[] = { TREE_PATH_FLAVOR };

	  private List<TreePath> treePaths = null;
	  
	  private DragSourceContext dragSourceContext = null;

	  /**
  	 * Instantiates a new transferable tree node.
  	 *
  	 * @param treePaths the tree paths
  	 */
  	public TransferableTreeNode(List<TreePath> treePaths) {
	    this.treePaths = treePaths;
	  }

	  /* (non-Javadoc)
  	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
  	 */
  	public synchronized DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	  }

	  /* (non-Javadoc)
  	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
  	 */
  	public boolean isDataFlavorSupported(DataFlavor flavor) {
	    return (flavor.getRepresentationClass() == TreePath.class);
	  }
	  
	  

	  
	/**
	 * Gets the drag source context.
	 *
	 * @return the drag source context
	 */
	public DragSourceContext getDragSourceContext() {
	
		return dragSourceContext;
	}

	
	/**
	 * Sets the drag source context.
	 *
	 * @param dragSourceContext the new drag source context
	 */
	public void setDragSourceContext(DragSourceContext dragSourceContext) {
	
		this.dragSourceContext = dragSourceContext;
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public synchronized Object getTransferData(DataFlavor flavor)
	      throws UnsupportedFlavorException, IOException {
	    if (isDataFlavorSupported(flavor)) {
	      return (Object) treePaths;
	    } else {
	      throw new UnsupportedFlavorException(flavor);
	    }
	  }
	}