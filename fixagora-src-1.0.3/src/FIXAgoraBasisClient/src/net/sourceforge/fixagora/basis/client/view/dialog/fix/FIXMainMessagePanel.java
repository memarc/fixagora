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
package net.sourceforge.fixagora.basis.client.view.dialog.fix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.message.FIXMessage;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessageFilter;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXInputMessage;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXOutputMessage;

/**
 * The Class FIXMainMessagePanel.
 */
public class FIXMainMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image backImage = null;

	private ImageIcon backImageIcon = null;

	private JCheckBox hideOptionalFieldsCheckBox = null;

	private JCheckBox hideEmptyFieldsCheckBox = null;

	private JCheckBox hideHeaderCheckBox = null;

	private boolean consistent = false;

	private FIXMessagePanel fixMessagePanel = null;

	private final LastHit lastHit = new LastHit(null);

	private FIXMessage fixMessage = null;
	
	private FIXMessageFilter fixMessageFilter = null;

	private SpreadSheetFIXFieldMap message = null;

	private JTextField searchTextField = null;

	private JLabel messageLogoLabel = null;

	private boolean input = false;

	private AbstractSpreadSheetFIXDialog abstractSpreadSheetFIXDialog = null;
	
	private Map<String, SpreadSheetFIXFieldMap> cachedMessages = new HashMap<String, SpreadSheetFIXFieldMap>();

	/**
	 * Instantiates a new fIX main message panel.
	 *
	 * @param abstractSpreadSheetFIXDialog the abstract spread sheet fix dialog
	 */
	public FIXMainMessagePanel(AbstractSpreadSheetFIXDialog abstractSpreadSheetFIXDialog) {

		if(abstractSpreadSheetFIXDialog instanceof SpreadSheetFIXInputDialog)
			this.input  = true;
		this.abstractSpreadSheetFIXDialog  = abstractSpreadSheetFIXDialog;
		
		backImageIcon = new ImageIcon(FIXMainMessagePanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/logo.png"));
		backImage = backImageIcon.getImage();

		setBackground(new Color(204, 216, 255));
		setLayout(new BorderLayout(0, 0));
		
		fixMessageFilter = new FIXMessageFilter();

		final JPanel filterPanel = new GradientPanel();
		filterPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0, 0, 0)));
		final GridBagLayout gbl_filterPanel = new GridBagLayout();
		gbl_filterPanel.rowHeights = new int[] { 15 };
		gbl_filterPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_filterPanel.rowWeights = new double[] { 0.0 };
		filterPanel.setLayout(gbl_filterPanel);
		add(filterPanel, BorderLayout.NORTH);

		final JLabel filterLabel = new JLabel("Filter");
		filterLabel.setForeground(Color.WHITE);
		filterLabel.setIcon(new ImageIcon(FIXMainMessagePanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/filter.png")));
		filterLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_filterLabel = new GridBagConstraints();
		gbc_filterLabel.anchor = GridBagConstraints.WEST;
		gbc_filterLabel.insets = new Insets(5, 25, 5, 5);
		gbc_filterLabel.gridx = 0;
		gbc_filterLabel.gridy = 0;
		filterPanel.add(filterLabel, gbc_filterLabel);

		hideOptionalFieldsCheckBox = new JCheckBox("Hide optional fields");
		hideOptionalFieldsCheckBox.setEnabled(false);
		hideOptionalFieldsCheckBox.setForeground(Color.WHITE);
		hideOptionalFieldsCheckBox.setFocusPainted(false);
		hideOptionalFieldsCheckBox.setOpaque(false);
		hideOptionalFieldsCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_hideOptionalFieldsCheckBox = new GridBagConstraints();
		gbc_hideOptionalFieldsCheckBox.insets = new Insets(5, 20, 5, 5);
		gbc_hideOptionalFieldsCheckBox.gridx = 1;
		gbc_hideOptionalFieldsCheckBox.gridy = 0;
		filterPanel.add(hideOptionalFieldsCheckBox, gbc_hideOptionalFieldsCheckBox);
		
		hideOptionalFieldsCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				fixMessageFilter.setHideOptionalFields(hideOptionalFieldsCheckBox.isSelected());
				addFIXMessagePanel(fixMessage,message);
			}
		});


		hideEmptyFieldsCheckBox = new JCheckBox("Hide unused fields");
		hideEmptyFieldsCheckBox.setOpaque(false);
		hideEmptyFieldsCheckBox.setEnabled(false);
		hideEmptyFieldsCheckBox.setForeground(Color.WHITE);
		hideEmptyFieldsCheckBox.setFocusPainted(false);
		hideEmptyFieldsCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_hideEmptyFieldsCheckBox = new GridBagConstraints();
		gbc_hideEmptyFieldsCheckBox.insets = new Insets(5, 20, 5, 5);
		gbc_hideEmptyFieldsCheckBox.gridx = 2;
		gbc_hideEmptyFieldsCheckBox.gridy = 0;
		filterPanel.add(hideEmptyFieldsCheckBox, gbc_hideEmptyFieldsCheckBox);
		
		hideEmptyFieldsCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				fixMessageFilter.setHideEmptyFields(hideEmptyFieldsCheckBox.isSelected());
				addFIXMessagePanel(fixMessage,message);
			}
		});

		
		hideHeaderCheckBox = new JCheckBox("Hide header/trailer");
		hideHeaderCheckBox.setForeground(Color.WHITE);
		hideHeaderCheckBox.setOpaque(false);
		hideEmptyFieldsCheckBox.setEnabled(false);
		hideHeaderCheckBox.setFocusPainted(false);
		hideHeaderCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_hideHeaderCheckBox = new GridBagConstraints();
		gbc_hideHeaderCheckBox.anchor = GridBagConstraints.WEST;
		gbc_hideHeaderCheckBox.insets = new Insets(5, 20, 5, 5);
		gbc_hideHeaderCheckBox.gridx = 3;
		gbc_hideHeaderCheckBox.gridy = 0;
		filterPanel.add(hideHeaderCheckBox, gbc_hideHeaderCheckBox);

		hideHeaderCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				fixMessageFilter.setHideHeader(hideHeaderCheckBox.isSelected());
				addFIXMessagePanel(fixMessage,message);
			}
		});		
		
		searchTextField = new JTextField();
		searchTextField.setPreferredSize(new Dimension(4, 25));
		searchTextField.setMinimumSize(new Dimension(4, 25));
		searchTextField.setColumns(10);
		final GridBagConstraints gbc_searchTextField = new GridBagConstraints();
		gbc_searchTextField.anchor = GridBagConstraints.WEST;
		gbc_searchTextField.insets = new Insets(5, 20, 5, 5);
		gbc_searchTextField.gridx = 4;
		gbc_searchTextField.gridy = 0;
		filterPanel.add(searchTextField, gbc_searchTextField);
		
		searchTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {

				searchTextField.setBackground(Color.WHITE);
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {

				searchTextField.setBackground(Color.WHITE);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {

				searchTextField.setBackground(Color.WHITE);
			}
		});
		
		searchTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent e) {

				final int key = e.getKeyCode();

				if (key == KeyEvent.VK_ENTER)
					find();
			}

			@Override
			public void keyReleased(final KeyEvent e) {

			}

			@Override
			public void keyTyped(final KeyEvent e) {

			}
		});

		final JButton searchButton = new JButton("Search");
		searchButton.setOpaque(false);
		searchButton.setIcon(new ImageIcon(FIXMainMessagePanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/search.png")));
		searchButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		final GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(5, 5, 5, 5);
		gbc_btnNewButton.gridx = 5;
		gbc_btnNewButton.gridy = 0;
		filterPanel.add(searchButton, gbc_btnNewButton);

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				find();
			}
		});		
		
		messageLogoLabel = new JLabel();
		messageLogoLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 20));
		messageLogoLabel.setForeground(Color.WHITE);
		final GridBagConstraints gbc_messageLogoLabel = new GridBagConstraints();
		gbc_messageLogoLabel.anchor = GridBagConstraints.EAST;
		gbc_messageLogoLabel.insets = new Insets(5, 0, 5, 25);
		gbc_messageLogoLabel.gridx = 6;
		gbc_messageLogoLabel.gridy = 0;
		filterPanel.add(messageLogoLabel, gbc_messageLogoLabel);
		
	}

	/**
	 * Adds the fix message panel.
	 *
	 * @param fixMessage the fix message
	 * @param message the message
	 */
	public void addFIXMessagePanel(final FIXMessage fixMessage, SpreadSheetFIXFieldMap message) {
		
		if(message==null)
		{
			message = cachedMessages.get(fixMessage.getMessageType());
			if(message==null)
			{
				if(input)
				{
					message = new SpreadSheetFIXInputMessage();
					((SpreadSheetFIXInputMessage)message).setMessageId(fixMessage.getMessageType());
					((SpreadSheetFIXInputMessage)message).setMessageName(fixMessage.getName());
				}
				else
				{
					message = new SpreadSheetFIXOutputMessage();
					((SpreadSheetFIXOutputMessage)message).setMessageId(fixMessage.getMessageType());
					((SpreadSheetFIXOutputMessage)message).setMessageName(fixMessage.getName());					
				}
			}
		}
		cachedMessages.put(fixMessage.getMessageType(), message);
		this.fixMessage = fixMessage;
		messageLogoLabel.setText(fixMessage.getName());
		this.message = message;
		
		hideEmptyFieldsCheckBox.setEnabled(false);
		hideOptionalFieldsCheckBox.setEnabled(false);
		hideHeaderCheckBox.setEnabled(false);
		
		if (fixMessagePanel != null)
		{
			remove(fixMessagePanel);
			fixMessagePanel.dispose();
		}
		
		final WaitDialog waitDialog = new WaitDialog(this, "Initialize " + fixMessage.getName()+" ...");
		waitDialog.setIncrement(100d/fixMessage.getAbstractFIXElements()
				.size());

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				repaint();
				
				fixMessage.initFontProperties(FIXMainMessagePanel.this);
				fixMessagePanel = new FIXMessagePanel(fixMessage, FIXMainMessagePanel.this.message, fixMessageFilter, waitDialog, input, FIXMainMessagePanel.this);
				fixMessagePanel.validate();
				add(fixMessagePanel, BorderLayout.CENTER);
				getParent().validate();
				getParent().repaint();
				hideEmptyFieldsCheckBox.setEnabled(true);
				hideOptionalFieldsCheckBox.setEnabled(true);
				hideHeaderCheckBox.setEnabled(true);
				//System.gc();
				
				
				return null;
			}

			@Override
			protected void done() {

				super.done();
				waitDialog.setVisible(false);
			}

		};
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
			
				waitDialog.setVisible(true);
				swingWorker.execute();
				
				
			}
		});
	}

	private void find() {

		if (searchTextField.getText().trim().length() == 0)
			return;
		
		final AbstractFIXPanel abstractFIXPanel = fixMessagePanel.find(searchTextField.getText(), lastHit);
		
		if (abstractFIXPanel == null)
			searchTextField.setBackground(new Color(255, 198, 179));
		
		else
			searchTextField.setBackground(Color.WHITE);
		
		lastHit.setAbstractFIXPanel(abstractFIXPanel);
		fixMessagePanel.scrollTo(abstractFIXPanel);
	}

	/**
	 * Gets the fIX message.
	 *
	 * @return the fIX message
	 */
	public FIXMessage getFIXMessage() {

		return fixMessage;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public SpreadSheetFIXFieldMap getMessage() {

		return message;
	}


	/**
	 * Checks if is consistent.
	 *
	 * @return true, if is consistent
	 */
	public boolean isConsistent() {

		return consistent;
	}

	/**
	 * Update fields.
	 */
	public void updateFields() {

		addFIXMessagePanel(fixMessage,message);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {

		super.paintComponent(g);

		final Graphics2D graphics2d = (Graphics2D) g;
		final int width = getBounds().width - 100;
		final int height = width / 12;
		final int y = (int) getBounds().getHeight() - height - 50;
		graphics2d.drawImage(backImage, 50, y, width, height, null);

	}
	
	

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#removeNotify()
	 */
	@Override
	public void removeNotify() {
		
		if (fixMessagePanel != null)
		{
			remove(fixMessagePanel);
			fixMessagePanel.dispose();
		}

		KeyListener[] keyListeners = searchTextField.getKeyListeners();
		
		for(KeyListener keyListener: keyListeners)
			searchTextField.removeKeyListener(keyListener);

	}

	/**
	 * Field value changed.
	 */
	public void fieldValueChanged() {
		
		if(fixMessagePanel!=null&&fixMessagePanel.getFieldCheckResult()!=null&&fixMessagePanel.getFieldCheckResult().getBugLevel()!=2)
			consistent = true;
		else
			consistent = false;

		abstractSpreadSheetFIXDialog.fieldValueChanged();
		
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {

		cachedMessages.clear();
		
	}

}
