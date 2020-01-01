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
package net.sourceforge.fixagora.basis.client.view.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;

/**
 * The Class AbstractSpinner.
 */
public abstract class AbstractSpinner extends JPanel  implements FieldInterface {

	private static final long serialVersionUID = 1L;

	/** The spinner1 minus button. */
	protected JButton spinner1MinusButton = null;

	/** The spinner1 plus button. */
	protected JButton spinner1PlusButton = null;

	/** The j text field. */
	protected AbstractTextField jTextField = null;
	
	private Color blue = new Color(204, 216, 255);

	/** The Constant blueLeftIcon. */
	protected final static ImageIcon blueLeftIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1leftarrow.png"));
	
	/** The Constant blueRightIcon. */
	protected final static ImageIcon blueRightIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1rightarrow.png"));
	
	/** The blue up icon. */
	protected final ImageIcon blueUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrow.png"));
	
	/** The red down icon. */
	protected final ImageIcon redDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowred.png"));
	
	/** The red up icon. */
	protected final ImageIcon redUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowred.png"));
	
	/** The green down icon. */
	protected final ImageIcon greenDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowgreen.png"));
	
	/** The green up icon. */
	protected final ImageIcon greenUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowgreen.png"));
	
	/** The blue down icon. */
	protected final ImageIcon blueDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrow.png"));
	
	/**
	 * Instantiates a new abstract spinner.
	 *
	 * @param cellEditor the cell editor
	 * @param plusIcon the plus icon
	 * @param minusIcon the minus icon
	 * @param jTextField the j text field
	 */
	public AbstractSpinner(final CellEditor cellEditor, ImageIcon plusIcon, ImageIcon minusIcon,  final AbstractTextField jTextField) {

		super();
		
		setRequestFocusEnabled(true);

		this.jTextField = jTextField;
		
		this.jTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cellEditor.stopCellEditing();
				
			}
		});
				

		GridBagLayout gbl_numberPanel = new GridBagLayout();
		gbl_numberPanel.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		gbl_numberPanel.rowWeights = new double[] { 1.0 };
		setLayout(gbl_numberPanel);

		JPanel minusPanel = new JPanel(new BorderLayout());
		minusPanel.setBorder(new MatteBorder(0, 0, 0, 1, blue.darker()));
		GridBagConstraints gbc_spinner1MinusButton = new GridBagConstraints();
		gbc_spinner1MinusButton.insets = new Insets(0, 0, 0, 0);
		gbc_spinner1MinusButton.gridx = 0;
		gbc_spinner1MinusButton.gridy = 0;
		add(minusPanel, gbc_spinner1MinusButton);
		
		spinner1MinusButton = new JButton();
		spinner1MinusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner1MinusButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		spinner1MinusButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		spinner1MinusButton.setPreferredSize(new Dimension(25, 25));
		spinner1MinusButton.setMinimumSize(new Dimension(25, 25));
		if (minusIcon == null)
			spinner1MinusButton.setText("-");
		else
			spinner1MinusButton.setIcon(minusIcon);
		minusPanel.add(spinner1MinusButton, BorderLayout.CENTER);
		
		spinner1MinusButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				decrement();
				
			}
		});
	

		GridBagConstraints gbc_jPanel = new GridBagConstraints();
		gbc_jPanel.insets = new Insets(0, 0, 0, 0);
		gbc_jPanel.fill = GridBagConstraints.BOTH;
		gbc_jPanel.gridx = 1;
		gbc_jPanel.gridy = 0;
		add(jTextField, gbc_jPanel);

		
		JPanel plusPanel = new JPanel(new BorderLayout());
		plusPanel.setBorder(new MatteBorder(0, 1, 0, 0, blue.darker()));
		GridBagConstraints gbc_spinner1PlusButton = new GridBagConstraints();
		gbc_spinner1PlusButton.insets = new Insets(0, 0, 0, 0);
		gbc_spinner1PlusButton.gridx = 2;
		gbc_spinner1PlusButton.gridy = 0;
		add(plusPanel, gbc_spinner1PlusButton);

		spinner1PlusButton = new JButton();
		spinner1PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner1PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner1PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner1PlusButton.setBorder(new EmptyBorder(0, 0, 0, 0));

		if (plusIcon == null)
			spinner1PlusButton.setText("+");
		else
			spinner1PlusButton.setIcon(plusIcon);
		plusPanel.add(spinner1PlusButton, BorderLayout.CENTER);
	
		spinner1PlusButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				increment();
				
			}
		});
	}

	/**
	 * Sets the minus icon.
	 *
	 * @param minusIcon the new minus icon
	 */
	public void setMinusIcon(ImageIcon minusIcon) {

		if (minusIcon == null)
			spinner1MinusButton.setText("-");
		else
			spinner1MinusButton.setText(null);
		spinner1MinusButton.setIcon(minusIcon);

	}

	/**
	 * Sets the plus icon.
	 *
	 * @param plusIcon the new plus icon
	 */
	public void setPlusIcon(ImageIcon plusIcon) {

		if (plusIcon == null)
			spinner1PlusButton.setText("+");
		else
			spinner1PlusButton.setText(null);
		spinner1PlusButton.setIcon(plusIcon);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color bg) {

		super.setBackground(bg);
		if(jTextField!=null)
		{
			jTextField.setBackground(bg);
			jTextField.repaint();
		}

	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	@Override
	public void setForeground(Color bg) {

		if(jTextField!=null)
			jTextField.setForeground(bg);

	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	public void setHighLightColor(Color highlight)
	{
		jTextField.setHighLightColor(highlight);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
	 */
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed){
        InputMap map = jTextField.getJTextField().getInputMap(condition);
        ActionMap am = jTextField.getJTextField().getActionMap();

        if(map!=null && am!=null && isEnabled()){
            Object binding = map.get(ks);
            Action action = (binding==null) ? null : am.get(binding);
            if(action!=null){
                return SwingUtilities.notifyAction(action, ks, e, jTextField.getJTextField(),
                        e.getModifiers());
            }
        }
        return false;
    }
	
	/**
	 * Increment.
	 */
	protected abstract void increment();
	
	/**
	 * Decrement.
	 */
	protected abstract void decrement();
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		spinner1MinusButton.setEnabled(enabled);
		spinner1PlusButton.setEnabled(enabled);
		jTextField.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setBold(boolean)
	 */
	@Override
	public void setBold(boolean bold) {

		jTextField.setBold(bold);
		
	}
	
	/**
	 * Adds the document listener.
	 *
	 * @param documentListener the document listener
	 */
	public void addDocumentListener(DocumentListener documentListener)
	{
		jTextField.getDocument().addDocumentListener(documentListener);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		
		jTextField.addMouseListener(l);
	}

}
