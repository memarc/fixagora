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
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXOutputMessage;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;

/**
 * The Class SpreadSheetFIXOutputDialog.
 */
public class SpreadSheetFIXOutputDialog extends AbstractSpreadSheetFIXDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private boolean cancelled = true;

	private JButton okButton = null;

	private boolean dirty = false;

	private JComboBox outputComponentComboBox;

	private JComboBox dataDictionaryComboBox;

	private FIXMainMessagePanel panel;

	private SpreadSheetFIXOutputMessage fixOutputMessage;

	private MessageTreePanel messageTreePanel;

	private boolean modified = false;

	private DictionaryParser dictionaryParser;

	private final ImageIcon warningIcon = new ImageIcon(
			SpreadSheetFIXOutputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));
	
	private static Logger log = Logger.getLogger(SpreadSheetFIXInputDialog.class);

	private JComboBox sendIfComboBox;

	/**
	 * Instantiates a new spread sheet fix output dialog.
	 *
	 * @param fixOutputMessage the fix output message
	 * @param dataDictionaries the data dictionaries
	 * @param businessComponents the business components
	 */
	public SpreadSheetFIXOutputDialog(final SpreadSheetFIXOutputMessage fixOutputMessage, final List<DataDictionary> dataDictionaries,
			List<AbstractBusinessObject> businessComponents) {

		this.fixOutputMessage = fixOutputMessage;

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(0, 0, dim.width, dim.height);

		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SpreadSheetFIXOutputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 50, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 0.0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel legSecurityLabel = new JLabel("Output Component");
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

		outputComponentComboBox = new JComboBox(businessComponents2.toArray());
		outputComponentComboBox.setMinimumSize(new Dimension(250, 25));
		outputComponentComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		outputComponentComboBox.setPreferredSize(new Dimension(250, 25));
		outputComponentComboBox.setBackground(new Color(255, 243, 204));

		GridBagConstraints gbc_outputComponentComboBox = new GridBagConstraints();
		gbc_outputComponentComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_outputComponentComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_outputComponentComboBox.gridx = 3;
		gbc_outputComponentComboBox.gridy = 0;
		contentPanel.add(outputComponentComboBox, gbc_outputComponentComboBox);

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

		
		
		
		JLabel sendIfLabel = new JLabel("Send If");
		sendIfLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sendIfLabel = new GridBagConstraints();
		gbc_sendIfLabel.anchor = GridBagConstraints.WEST;
		gbc_sendIfLabel.insets = new Insets(25, 25, 5, 5);
		gbc_sendIfLabel.gridx = 7;
		gbc_sendIfLabel.gridy = 0;
		contentPanel.add(sendIfLabel, gbc_sendIfLabel);
		
		List<String> column = new ArrayList<String>();
		column.add("No condition");

		for (int i = 0; i < 255; i++)
			column.add(CellReference.convertNumToColString(i));

		sendIfComboBox = new JComboBox(column.toArray());
		sendIfComboBox.setMinimumSize(new Dimension(100, 25));
		sendIfComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sendIfComboBox.setPreferredSize(new Dimension(250, 25));
		sendIfComboBox.setBackground(new Color(255, 243, 204));
		
		sendIfComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				checkConsistency();
				
			}
		});

		GridBagConstraints gbc_sendIfComboBox = new GridBagConstraints();
		gbc_sendIfComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_sendIfComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_sendIfComboBox.gridx = 8;
		gbc_sendIfComboBox.gridy = 0;
		contentPanel.add(sendIfComboBox, gbc_sendIfComboBox);		
		
		
		panel = new FIXMainMessagePanel(this);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 8;
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

					AbstractBusinessComponent businessComponentInterface = (AbstractBusinessComponent) outputComponentComboBox.getSelectedItem();
					if (businessComponentInterface.getDataDictionary() != null
							&& !businessComponentInterface.getDataDictionary().equals(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName())) {
						dataDictionaryWarningLabel.setToolTipText("The business component is using another data dictionary.");
						dataDictionaryWarningLabel.setIcon(warningIcon);
					}
					else {
						dataDictionaryWarningLabel.setToolTipText(null);
						dataDictionaryWarningLabel.setIcon(null);
					}
					if (fixOutputMessage != null && fixOutputMessage.getMessageId() != null
							&& dictionaryParser.getFIXMessage(fixOutputMessage.getMessageId()) != null) {
						panel.addFIXMessagePanel(dictionaryParser.getFIXMessage(fixOutputMessage.getMessageId()), fixOutputMessage);
					}
					else {
						panel.addFIXMessagePanel(dictionaryParser.getMessageList().get(0), null);
					}
				}
				catch (Exception e1) {
					log.error("Bug", e1);
				}
			}
		});

		outputComponentComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (fixOutputMessage == null || fixOutputMessage.getDataDictionary() == null) {
					AbstractBusinessComponent businessComponentInterface = (AbstractBusinessComponent) outputComponentComboBox.getSelectedItem();
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

		if (this.fixOutputMessage == null) {
			outputComponentComboBox.setSelectedIndex(0);
			sendIfComboBox.setSelectedIndex(0);
			outputComponentComboBox.dispatchEvent(new ActionEvent(this, 0, ""));
			this.fixOutputMessage = new SpreadSheetFIXOutputMessage();
			FIXMessage fixMessage = dictionaryParser.getMessageList().get(0);
			if (fixMessage != null) {
				panel.addFIXMessagePanel(fixMessage, null);
			}
			setTitle("Add FIX Output");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			outputComponentComboBox.setSelectedItem(fixOutputMessage.getBusinessComponent());
			if(fixOutputMessage.getSendIf()==null)
				sendIfComboBox.setSelectedIndex(0);
			else
				sendIfComboBox.setSelectedItem(fixOutputMessage.getSendIf());
			for (DataDictionary dataDictionary : dataDictionaries)
				if (dataDictionary.getName().equals(fixOutputMessage.getDataDictionary())) {
					dataDictionaryComboBox.setSelectedItem(dataDictionary);
				}
			dataDictionaryComboBox.dispatchEvent(new ActionEvent(this, 0, ""));
			FIXMessage fixMessage = dictionaryParser.getFIXMessage(this.fixOutputMessage.getMessageId());
			if (fixMessage != null) {
				panel.addFIXMessagePanel(fixMessage, fixOutputMessage);
			}
			setTitle("Edit FIX Output");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 8;
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
		gbc_cancelButton.gridx = 9;
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

			if (fixOutputMessage.getDataDictionary() != null
					&& !fixOutputMessage.getDataDictionary().equals(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName()))
				dirty = true;

			if ((fixOutputMessage.getDataDictionary() == null) && dataDictionaryComboBox.getSelectedIndex() >= 0)
				dirty = true;

			if (fixOutputMessage.getBusinessComponent() != null
					&& !fixOutputMessage.getBusinessComponent().equals(((AbstractBusinessObject) outputComponentComboBox.getSelectedItem())))
				dirty = true;

			if ((fixOutputMessage.getBusinessComponent() == null) && outputComponentComboBox.getSelectedIndex() >= 0)
				dirty = true;
			
			
			if (fixOutputMessage.getSendIf() != null
					&& !fixOutputMessage.getSendIf().equals(((String) sendIfComboBox.getSelectedItem())))
				dirty = true;

			if ((fixOutputMessage.getSendIf() == null) && sendIfComboBox.getSelectedIndex() > 0)
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
	 * Gets the fIX output message.
	 *
	 * @return the fIX output message
	 */
	public SpreadSheetFIXOutputMessage getFIXOutputMessage() {

		SpreadSheetFIXOutputMessage fixOutputMessage = (SpreadSheetFIXOutputMessage)panel.getMessage();
		fixOutputMessage.setBusinessComponent((AbstractBusinessComponent) outputComponentComboBox.getSelectedItem());
		fixOutputMessage.setDataDictionary(((DataDictionary) dataDictionaryComboBox.getSelectedItem()).getName());
		return fixOutputMessage;
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
