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

import java.awt.dnd.DragSourceContext;
import java.io.Serializable;
import java.util.List;

import javax.swing.tree.TreePath;

/**
 * The Class TransferTreePath.
 */
public class TransferTreePath implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<TreePath> treePaths = null;

	private DragSourceContext dragSourceContext = null;

	/**
	 * Instantiates a new transfer tree path.
	 *
	 * @param treePaths the tree paths
	 */
	public TransferTreePath(List<TreePath> treePaths) {

		super();
		this.treePaths = treePaths;
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

	/**
	 * Gets the tree paths.
	 *
	 * @return the tree paths
	 */
	public List<TreePath> getTreePaths() {

		return treePaths;
	}

}
