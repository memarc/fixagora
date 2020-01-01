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
package net.sourceforge.fixagora.basis.client.view;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;


/**
 * The Class BusinessObjectTreeNode.
 */
public class BusinessObjectTreeNode extends DefaultMutableTreeNode {
	

	private static final long serialVersionUID = 1L;
	
	

	/**
	 * Instantiates a new business object tree node.
	 */
	public BusinessObjectTreeNode() {

		super();

	}

	/**
	 * Instantiates a new business object tree node.
	 *
	 * @param userObject the user object
	 * @param allowsChildren the allows children
	 */
	public BusinessObjectTreeNode(Object userObject, boolean allowsChildren) {

		super(userObject, allowsChildren);

	}

	/**
	 * Instantiates a new business object tree node.
	 *
	 * @param userObject the user object
	 */
	public BusinessObjectTreeNode(Object userObject) {

		super(userObject);
		
	}

	/**
	 * Insert.
	 *
	 * @param newChild the new child
	 * @param childIndex the child index
	 */
	public void insert(DefaultMutableTreeNode newChild, int childIndex) {
		super.insert(newChild, childIndex);
		updateSortOrder();
	}

	/** The node comparator. */
	@SuppressWarnings("rawtypes")
	protected static Comparator nodeComparator = new Comparator () {
		public int compare(Object o1, Object o2) {
			 if(o1 instanceof BusinessObjectTreeNode && o2 instanceof BusinessObjectTreeNode)
			 {
				 Object object1 = ((BusinessObjectTreeNode)o1).getUserObject();
				 Object object2 = ((BusinessObjectTreeNode)o2).getUserObject();
				 
				 if(object1 instanceof AbstractBusinessObject && object2 instanceof AbstractBusinessObject)
				 {
					 AbstractBusinessObject abstractBusinessObject1 = (AbstractBusinessObject) object1;
					 AbstractBusinessObject abstractBusinessObject2 = (AbstractBusinessObject) object2;
					 return abstractBusinessObject1.compareTo(abstractBusinessObject2);
				 }
			 }
		     return o1.toString().compareToIgnoreCase(o2.toString());
		}
		
		public boolean equals(Object obj) {
			return false;
		}
	};

	/**
	 * Update sort order.
	 */
	@SuppressWarnings("unchecked")
	public void updateSortOrder()
	{
		if(this.children!=null)
			Collections.sort(this.children, nodeComparator);		
	}
	
}
