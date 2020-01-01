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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;

/**
 * The Class BusinessObjectTreeCellRenderer.
 */
public class BusinessObjectTreeCellRenderer extends JPanel implements TreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private JLabel iconLabel = null;
	private JLabel nameLabel = null;
	private JLabel additionalLabel = null;

	/**
	 * Instantiates a new business object tree cell renderer.
	 */
	public BusinessObjectTreeCellRenderer() {

		super();

		setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0 };
		setLayout(gridBagLayout);
		setOpaque(true);

		iconLabel = new JLabel();
		GridBagConstraints gbc_iconLabel = new GridBagConstraints();
		gbc_iconLabel.fill = GridBagConstraints.NONE;
		gbc_iconLabel.gridx = 0;
		gbc_iconLabel.gridy = 0;
		gbc_iconLabel.insets = new Insets(4, 2, 3, 5);
		add(iconLabel, gbc_iconLabel);

		nameLabel = new JLabel();
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.fill = GridBagConstraints.NONE;
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		gbc_iconLabel.insets = new Insets(4, 5, 3, 5);
		add(nameLabel, gbc_nameLabel);

		additionalLabel = new JLabel();
		additionalLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_additionalLabel = new GridBagConstraints();
		gbc_additionalLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_additionalLabel.gridx = 2;
		gbc_additionalLabel.gridy = 0;
		gbc_additionalLabel.insets = new Insets(4, 5, 3, 5);
		add(additionalLabel, gbc_additionalLabel);

	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		if (selected)
			setBackground((new Color(204, 216, 255)));
		else if (row % 2 == 0)
			setBackground(new Color(255, 236, 179));

		else
			setBackground(new Color(255, 243, 204));

		AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) ((DefaultMutableTreeNode) value).getUserObject();

		if (abstractBusinessObject != null) {

			iconLabel.setIcon(new ImageIcon(TreePanel.class.getResource(abstractBusinessObject.getIcon())));
			nameLabel.setText(abstractBusinessObject.getName());
			int width = getFontMetrics(getFont()).stringWidth(abstractBusinessObject.getName());
			
			additionalLabel.setText(abstractBusinessObject.getAdditionalTreeText());
			additionalLabel.setForeground(abstractBusinessObject.getAdditionalTreeTextColor());
			if (abstractBusinessObject.getAdditionalTreeText() != null) {
				width = width + getFontMetrics(getFont()).stringWidth(abstractBusinessObject.getAdditionalTreeText());
			}
			
			setMinimumSize(new Dimension(50 + width, 27));
			setPreferredSize(new Dimension(50 + width, 27));

		}
		

		return this;
	}

}
