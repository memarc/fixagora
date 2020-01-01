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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.control.DictionaryParser;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessage;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXInputMessage;

import org.apache.log4j.Logger;

/**
 * The Class SpreadSheetFIXInputDialog.
 */
public class SpreadSheetFIXInputDialog extends AbstractSpreadSheetFIXDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private boolean cancelled = true;

	private JButton okButton = null;

	private boolean dirty = false;

	private JComboBox inputComponentComboBox;

	private JComboBox dataDictionaryComboBox;

	private FIXMainMessagePanel panel;

	private SpreadSheetFIXInputMessage fixInputMessage;

	private MessageTreePanel messageTreePanel;

	private boolean modified = false;

	private DictionaryParser dictionaryParser;
	
	private static Logger log = Logger.getLogger(SpreadSheetFIXInputDialog.class);

	private final ImageIcon warningIcon = new ImageIcon(
			SpreadSheetFIXInputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));

	/**
	 * Instantiates a new spread sheet fix input dialog.
	 *
	 * @param fixInputMessage the fix input message
	 * @param dataDictionaries the data dictionaries
	 * @param businessComponents the business components
	 */
	public SpreadSheetFIXInputDialog(final SpreadSheetFIXInputMessage fixInputMessage, final List<DataDictionary> dataDictionaries,
			List<AbstractBusinessObject> businessComponents) {

		this.fixInputMessage = fixInputMessage;

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(0, 0, dim.width, dim.height);

		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SpreadSheetFIXInputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 50, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 0.0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel legSecurityLabel = new JLabel("Input Component");
		legSecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 25);
		gbc_legSecurityLabel.gridx = 2;
		gbc_legSecurityLabel.gridy = 0;
		contentPanel.add(legSecurityLabel, gbc_legSecurityLabel);

		ArrayList<AbstractBusinessObject> businessComponents2 = new ArrayList<AbstractBusinessObject>();
		for (AbstractBusinessObject abstractBusinessObject : businessComponents)
			if (!(abstractBusinessObject instanceof SpreadSheet))
				businessComponents2.add(abstractBusinessObject);

		inputComponentComboBox = new JComboBox(businessComponents2.toArray());
		inputComponentComboBox.setMinimumSize(new Dimension(250, 25));
		inputComponentComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		inputComponentComboBox.setPreferredSize(new Dimension(250, 25));
		inputComponentComboBox.setBackground(new Color(255, 243, 204));

		GridBagConstraints gbc_inputComponentComboBox = new GridBagConstraints();
		gbc_inputComponentComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_inputComponentComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_inputComponentComboBox.gridx = 3;
		gbc_inputComponentComboBox.gridy = 0;
		contentPanel.add(inputComponentComboBox, gbc_inputComponentComboBox);

		JLabel counterpartySecurityLabel = new JLabel("Data Dictionary");
		counterpartySecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterpartySecurityLabel = new GridBagConstraints();
		gbc_counterpartySecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_counterpartySecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_counterpartySecurityLabel.gridx = 4;
		gbc_counterpartySecurityLabel.gridy = 0;
		contentPanel.add(counterpartySecurityLabel, gbc_counterpartySecurityLabel);

		final JLabel dataDictionaryWarningLabel = new JLabel("");
		dataDictionaryWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_dataDictionaryWarningLabel = new GridBagConstraints();
		gbc_dataDictionaryWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataDictionaryWarningLabel.insets = new Insets(25, 0, 5, 5);
		gbc_dataDictionaryWarningLabel.gridx = 5;
		gbc_dataDictionaryWarningLabel.gridy = 0;
		contentPanel.add(dataDictionaryWarningLabel, gbc_dataDictionaryWarningLabel);

		dataDictionaryComboBox = new JComboBox(dataDictionaries.toArray());
		dataDictionaryComboBox.setMinimumSize(new Dimension(250, 25));
		dataDictionaryComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		dataDictionaryComboBox.setPreferredSize(new Dimension(250, 25));
		dataDictionaryComboBox.setBackground(new Color(255, 243, 204));

		GridBagConstraints gbc_dataDictionaryComboBox = new GridBagConstraints();
		gbc_dataDictionaryComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_dataDictionaryComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_dataDictionaryComboBox.gridx = 6;
		gbc_dataDictionaryComboBox.gridy = 0;
		contentPanel.add(dataDictionaryComboBox, gbc_dataDictionaryComboBox);

		panel = new FIXMainMessagePanel(this);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 7;
		gbc_panel.insets = new Insets(25, 25, 5, 25);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 1;
		contentPanel.add(panel, gbc_panel);

		dataDictionaryComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				DataDictionary dataDictionary = (DataDictionary) dataDictionaryComboBox.getSelectedItem();
				StringBuffer stringBuffer = new StringBuffer();
				try {
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataDictionary.getCompressed());
					GZIPInputStream gzip = new GZIPInputStream(byteArrayInputStream);
					InputStreamReader reader = new InputStreamReader(gzip);
					BufferedReader in = new BufferedReader(reader);

					String readed;
					while ((readed = in.readLine()) != null)
						stringBuffer.append(readed);

					dictionaryParser = new DictionaryParser(stringBuffer);
					messageTreePanel.addMessages(dictionaryParser.getMessageList());

					AbstractBusinessComponent businessComponentInterface = (AbstractBusinessComponent) inputComponentComboBox.getSelectedItem();
					if (businessComponentInterface.getDataDictionary() != null
							&& !businessComponentInterface.getDataDictionary().equals(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName())) {
						dataDictionaryWarningLabel.setToolTipText("The business component is using another data dictionary.");
						dataDictionaryWarningLabel.setIcon(warningIcon);
					}
					else {
						dataDictionaryWarningLabel.setToolTipText(null);
						dataDictionaryWarningLabel.setIcon(null);
					}
					if (fixInputMessage != null && fixInputMessage.getMessageId() != null
							&& dictionaryParser.getFIXMessage(fixInputMessage.getMessageId()) != null) {
						if(fixInputMessage.getDataDictionary().equals(dataDictionary.getName()))
							panel.addFIXMessagePanel(dictionaryParser.getFIXMessage(fixInputMessage.getMessageId()), fixInputMessage);
						else
						{
							panel.clearCache();
							panel.addFIXMessagePanel(dictionaryParser.getFIXMessage(fixInputMessage.getMessageId()), null);
						}
					}
					else {
						panel.clearCache();
						panel.addFIXMessagePanel(dictionaryParser.getMessageList().get(0), null);
					}
				}
				catch (Exception e1) {
					log.error("Bug", e1);
				}
			}
		});

		inputComponentComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (fixInputMessage == null || fixInputMessage.getDataDictionary() == null) {
					AbstractBusinessComponent businessComponentInterface = (AbstractBusinessComponent) inputComponentComboBox.getSelectedItem();
					if (businessComponentInterface.getDataDictionary() != null)
						for (DataDictionary dataDictionary : dataDictionaries)
							if (dataDictionary.getName().equals(businessComponentInterface.getDataDictionary()))
								dataDictionaryComboBox.setSelectedItem(dataDictionary);
				}
			}
		});

		messageTreePanel = new MessageTreePanel(panel);
		messageTreePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		messageTreePanel.setPreferredSize(new Dimension(250, 10));
		messageTreePanel.setMinimumSize(new Dimension(250, 10));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridheight = 2;
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(25, 25, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		contentPanel.add(messageTreePanel, gbc_panel_1);

		okButton = new JButton();
		okButton.setMinimumSize(new Dimension(100, 25));

		if (this.fixInputMessage == null) {
			inputComponentComboBox.setSelectedIndex(0);
			inputComponentComboBox.dispatchEvent(new ActionEvent(this, 0, ""));
			this.fixInputMessage = new SpreadSheetFIXInputMessage();
			FIXMessage fixMessage = dictionaryParser.getMessageList().get(0);
			if (fixMessage != null) {
				panel.addFIXMessagePanel(fixMessage, null);
			}
			setTitle("Add FIX Input");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			inputComponentComboBox.setSelectedItem(fixInputMessage.getBusinessComponent());
			for (DataDictionary dataDictionary : dataDictionaries)
				if (dataDictionary.getName().equals(fixInputMessage.getDataDictionary())) {
					dataDictionaryComboBox.setSelectedItem(dataDictionary);
				}
			dataDictionaryComboBox.dispatchEvent(new ActionEvent(this, 0, ""));
			FIXMessage fixMessage = dictionaryParser.getFIXMessage(this.fixInputMessage.getMessageId());
			if (fixMessage != null) {
				panel.addFIXMessagePanel(fixMessage, fixInputMessage);
			}
			setTitle("Edit FIX Input");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 6;
		gbc_okButton.gridy = 2;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 7;
		gbc_cancelButton.gridy = 2;
		contentPanel.add(cancelButton, gbc_cancelButton);
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
			}
		});

		checkConsistency();
	}

	private void checkConsistency() {

		dirty = modified;

		if (panel.isConsistent()) {

			if (fixInputMessage.getDataDictionary() != null
					&& !fixInputMessage.getDataDictionary().equals(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName()))
				dirty = true;

			if ((fixInputMessage.getDataDictionary() == null) && dataDictionaryComboBox.getSelectedIndex() >= 0)
				dirty = true;

			if (fixInputMessage.getBusinessComponent() != null
					&& !fixInputMessage.getBusinessComponent().equals(((AbstractBusinessObject) inputComponentComboBox.getSelectedItem())))
				dirty = true;

			if ((fixInputMessage.getBusinessComponent() == null) && inputComponentComboBox.getSelectedIndex() >= 0)
				dirty = true;
		}
		else
			dirty = false;

		okButton.setEnabled(dirty);

	}

	/**
	 * Checks if is cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {

		return cancelled;
	}

	/**
	 * Gets the fIX input message.
	 *
	 * @return the fIX input message
	 */
	public SpreadSheetFIXInputMessage getFIXInputMessage() {

		SpreadSheetFIXInputMessage fixInputMessage = (SpreadSheetFIXInputMessage)panel.getMessage();
		fixInputMessage.setBusinessComponent((AbstractBusinessComponent) inputComponentComboBox.getSelectedItem());
		fixInputMessage.setDataDictionary(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName());
		return fixInputMessage;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.dialog.fix.AbstractSpreadSheetFIXDialog#fieldValueChanged()
	 */
	@Override
	public void fieldValueChanged() {

		modified = true;
		checkConsistency();
	}

}
