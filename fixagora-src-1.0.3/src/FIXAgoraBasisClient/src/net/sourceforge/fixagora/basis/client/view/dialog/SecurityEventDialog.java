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
package net.sourceforge.fixagora.basis.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.document.DoubleDocument;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityEvent;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

/**
 * The Class SecurityEventDialog.
 */
public class SecurityEventDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField priceTextField;
	private JTextField eventTextField;
	private boolean cancelled = true;
	private JButton okButton;
	private SecurityEvent securityEvent = null;
	private JComboBox typeComboBox;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
	private final DecimalFormat doubleFormat = new DecimalFormat("##0.0#########");
	private JLabel eventDateWarningLabel;
	private JDateChooser eventDateChooser;
	private JDateChooser eventTimeDateChooser;
	private boolean dirty;
	private final ImageIcon bugIcon = new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private JLabel eventTimeWarningLabel;	

	/**
	 * Instantiates a new security event dialog.
	 *
	 * @param securityEvent the security event
	 */
	public SecurityEventDialog(SecurityEvent securityEvent) {

		this.securityEvent = securityEvent;
		setBounds(100, 100, 533, 276);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityEventDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				checkConsistency();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				checkConsistency();
			}
		};

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};

		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("date")) {

					checkConsistency();

				}

			}
		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 146 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel typeLabel = new JLabel("Type");
		typeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_typeLabel = new GridBagConstraints();
		gbc_typeLabel.anchor = GridBagConstraints.WEST;
		gbc_typeLabel.insets = new Insets(25, 25, 5, 5);
		gbc_typeLabel.gridx = 0;
		gbc_typeLabel.gridy = 0;
		contentPanel.add(typeLabel, gbc_typeLabel);

		List<ComboBoxEntry> typeComboEntries = new ArrayList<ComboBoxEntry>();
		typeComboEntries.add(new ComboBoxEntry(1, "Put"));
		typeComboEntries.add(new ComboBoxEntry(2, "Call"));
		typeComboEntries.add(new ComboBoxEntry(3, "Tender"));
		typeComboEntries.add(new ComboBoxEntry(4, "Sinking Fund Call"));
		typeComboEntries.add(new ComboBoxEntry(5, "Activation"));
		typeComboEntries.add(new ComboBoxEntry(6, "Inactiviation"));
		typeComboEntries.add(new ComboBoxEntry(7, "Last Eligible Trade Date"));
		typeComboEntries.add(new ComboBoxEntry(8, "Swap Start Date"));
		typeComboEntries.add(new ComboBoxEntry(9, "Swap End Date"));
		typeComboEntries.add(new ComboBoxEntry(10, "Swap Roll Date"));
		typeComboEntries.add(new ComboBoxEntry(11, "Swap Next Start Date"));
		typeComboEntries.add(new ComboBoxEntry(12, "Swap Next Roll Date"));
		typeComboEntries.add(new ComboBoxEntry(13, "First Delivery Date"));
		typeComboEntries.add(new ComboBoxEntry(14, "Last Delivery Date"));
		typeComboEntries.add(new ComboBoxEntry(15, "Initial Inventory Due Date"));
		typeComboEntries.add(new ComboBoxEntry(16, "Final Inventory Due Date"));
		typeComboEntries.add(new ComboBoxEntry(17, "First Intent Date"));
		typeComboEntries.add(new ComboBoxEntry(18, "Last Intent Date"));
		typeComboEntries.add(new ComboBoxEntry(19, "Position Removal Date"));
		typeComboEntries.add(new ComboBoxEntry(99, "Other"));

		typeComboBox = new JComboBox(typeComboEntries.toArray());
		typeComboBox.setMinimumSize(new Dimension(32, 25));
		typeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		typeComboBox.setPreferredSize(new Dimension(32, 25));
		typeComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.gridwidth = 3;
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(25, 0, 5, 25);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 0;
		contentPanel.add(typeComboBox, gbc_localeOfissueTextField);
		
		if(securityEvent!=null)
			typeComboBox.setSelectedItem(new ComboBoxEntry(securityEvent.getEventType(), null));
		else
			typeComboBox.setSelectedIndex(0);
		
		typeComboBox.addActionListener(actionListener);

		JLabel eventDateLabel = new JLabel("Date");
		eventDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventDateLabel = new GridBagConstraints();
		gbc_eventDateLabel.anchor = GridBagConstraints.WEST;
		gbc_eventDateLabel.insets = new Insets(0, 25, 5, 5);
		gbc_eventDateLabel.gridx = 0;
		gbc_eventDateLabel.gridy = 1;
		contentPanel.add(eventDateLabel, gbc_eventDateLabel);
		
		eventDateWarningLabel = new JLabel("");
		eventDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_maturityDateWarningLabel = new GridBagConstraints();
		gbc_maturityDateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_maturityDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maturityDateWarningLabel.gridx = 1;
		gbc_maturityDateWarningLabel.gridy = 1;
		contentPanel.add(eventDateWarningLabel, gbc_maturityDateWarningLabel);
		
		eventDateChooser = new JDateChooser();
		eventDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor) eventDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		eventDateChooser.setOpaque(false);
		eventDateChooser.setDateFormatString("MM/dd/yyyy");
		((JTextFieldDateEditor) eventDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
		((JTextFieldDateEditor) eventDateChooser.getDateEditor()).setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 5, new Color(204,
				216, 255)), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)), new EmptyBorder(0, 5, 0, 0)));
		GridBagConstraints gbc_eventDateChooser = new GridBagConstraints();
		gbc_eventDateChooser.gridwidth = 2;
		gbc_eventDateChooser.insets = new Insets(0, 0, 5, 25);
		gbc_eventDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventDateChooser.gridx = 2;
		gbc_eventDateChooser.gridy = 1;
		contentPanel.add(eventDateChooser, gbc_eventDateChooser);
		
		if(securityEvent!=null)
			eventDateChooser.setDate(securityEvent.getEventDate());

		eventDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) eventDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel eventTimeLabel = new JLabel("Time");
		eventTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventTimeLabel = new GridBagConstraints();
		gbc_eventTimeLabel.anchor = GridBagConstraints.WEST;
		gbc_eventTimeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_eventTimeLabel.gridx = 0;
		gbc_eventTimeLabel.gridy = 2;
		contentPanel.add(eventTimeLabel, gbc_eventTimeLabel);
		
		eventTimeWarningLabel = new JLabel("");
		eventTimeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_eventTimeWarningLabel = new GridBagConstraints();
		gbc_eventTimeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventTimeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_eventTimeWarningLabel.gridx = 1;
		gbc_eventTimeWarningLabel.gridy = 2;
		contentPanel.add(eventTimeWarningLabel, gbc_eventTimeWarningLabel);
		
		eventTimeDateChooser = new JDateChooser();
		eventTimeDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor) eventTimeDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		eventTimeDateChooser.setOpaque(false);
		eventTimeDateChooser.getCalendarButton().setVisible(false);
		eventTimeDateChooser.setDateFormatString("HH:mm:ss");
		((JTextFieldDateEditor) eventTimeDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
		((JTextFieldDateEditor) eventTimeDateChooser.getDateEditor()).setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null,
				null), new EmptyBorder(0, 5, 0, 0)));
		eventTimeDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_eventTimeDateChooser = new GridBagConstraints();
		gbc_eventTimeDateChooser.gridwidth = 2;
		gbc_eventTimeDateChooser.insets = new Insets(0, 0, 5, 25);
		gbc_eventTimeDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventTimeDateChooser.gridx = 2;
		gbc_eventTimeDateChooser.gridy = 2;
		contentPanel.add(eventTimeDateChooser, gbc_eventTimeDateChooser);

		if(securityEvent!=null)
			eventTimeDateChooser.setDate(securityEvent.getEventTime());
		
		
		eventTimeDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) eventTimeDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);
		
		JLabel lblNewLabel = new JLabel("Price");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 25, 5, 15);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 3;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);

		priceTextField = new JTextField();
		priceTextField.setPreferredSize(new Dimension(200, 25));
		priceTextField.setDocument(new DoubleDocument());
		priceTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		priceTextField.setBackground(new Color(255, 243, 204));
		priceTextField.setColumns(10);
		GridBagConstraints gbc_priceTextField = new GridBagConstraints();
		gbc_priceTextField.gridwidth = 2;
		gbc_priceTextField.insets = new Insets(0, 0, 5, 25);
		gbc_priceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceTextField.gridx = 2;
		gbc_priceTextField.gridy = 3;
		contentPanel.add(priceTextField, gbc_priceTextField);
		
		if(securityEvent!=null&&securityEvent.getEventPrice()!=null)
			priceTextField.setText(doubleFormat.format(securityEvent.getEventPrice()));
		
		priceTextField.getDocument().addDocumentListener(documentListener);

		JLabel eventTextLabel = new JLabel("Text");
		eventTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventTextLabel = new GridBagConstraints();
		gbc_eventTextLabel.anchor = GridBagConstraints.WEST;
		gbc_eventTextLabel.insets = new Insets(0, 25, 5, 15);
		gbc_eventTextLabel.gridx = 0;
		gbc_eventTextLabel.gridy = 4;
		contentPanel.add(eventTextLabel, gbc_eventTextLabel);

		eventTextField = new JTextField();
		eventTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		eventTextField.setBackground(new Color(255, 243, 204));
		eventTextField.setPreferredSize(new Dimension(200, 25));
		GridBagConstraints gbc_eventTextField = new GridBagConstraints();
		gbc_eventTextField.gridwidth = 2;
		gbc_eventTextField.insets = new Insets(0, 0, 5, 25);
		gbc_eventTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventTextField.gridx = 2;
		gbc_eventTextField.gridy = 4;
		contentPanel.add(eventTextField, gbc_eventTextField);
		eventTextField.setColumns(10);
		
		if(securityEvent!=null)
			eventTextField.setText(securityEvent.getEventText());
		
		eventTextField.getDocument().addDocumentListener(documentListener);
		
		okButton = new JButton();

		if (this.securityEvent == null) {
			this.securityEvent = new SecurityEvent();
			setTitle("Add Security Event");
			typeComboBox.setSelectedIndex(19);
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Event");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 5;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
				
				SecurityEventDialog.this.securityEvent.setEventType((Integer) ((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry());
				
				SecurityEventDialog.this.securityEvent.setEventDate(eventDateChooser.getDate());
				
				try {
					Date date = simpleTimeFormat.parse(((JTextFieldDateEditor)eventTimeDateChooser.getDateEditor()).getText());
					SecurityEventDialog.this.securityEvent.setEventTime(date);
				}
				catch (ParseException parseException) {
				}
				
				if (priceTextField.getText().trim().length() > 0)
					SecurityEventDialog.this.securityEvent.setEventPrice(Double.parseDouble(priceTextField.getText()));
				
				else
					SecurityEventDialog.this.securityEvent.setEventPrice(null);
				
				SecurityEventDialog.this.securityEvent.setEventText(eventTextField.getText());
				
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 5;
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
	
	private boolean dirtyFieldCheck(String value, JTextField jTextField) {

		if (value == null && jTextField.getText().trim().length() > 0)
			return true;

		if (value == null && jTextField.getText().trim().length() == 0)
			return false;

		return (!value.equals(jTextField.getText().trim()));
	}

	private boolean dirtyTimeFieldCheck(Date value, JDateChooser jDateChooser, JLabel warningLabel) {
		JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) jDateChooser.getDateEditor();
		Date value2 = null;
		if (jTextFieldDateEditor.getText().length()>0) {
			try {
				value2 = simpleTimeFormat.parse(jTextFieldDateEditor.getText());
			}
			catch (ParseException e) {
				
				warningLabel.setToolTipText("Time is invalid.");
				warningLabel.setIcon(bugIcon);
				
				dirty = true;
				return false;
			}
		}
		
		warningLabel.setToolTipText(null);
		warningLabel.setIcon(null);
		
		if (value != null && value2 == null)
			dirty = true;

		else if (value == null && value2 != null)
			dirty = true;
			
		else if (value == null && value2 == null)
			return true;

		else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(value2);
			
			if (calendar.get(Calendar.HOUR_OF_DAY) != calendar2.get(Calendar.HOUR_OF_DAY) || calendar.get(Calendar.MINUTE) != calendar2.get(Calendar.MINUTE)
					|| calendar.get(Calendar.SECOND) != calendar2.get(Calendar.SECOND))
				dirty = true;
		}
		return true;
	}

	private boolean dirtyFieldCheck(Double value, JTextField jTextField) {

		Number value2 = null;
		
		try {
		
				value2 = doubleFormat.parse(jTextField.getText()).doubleValue();
		}
		catch (ParseException e) {
		}
		
		if (value != null && value2 == null)
			return true;

		else if (value == null && value2 != null)
			return true;
			
		else if (value == null && value2 == null)
			return false;
		
		return (!value.equals(value2));
	}

	private boolean dirtyFieldCheck(Date value, JDateChooser jDateChooser, JLabel warningLabel) {
		JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) jDateChooser.getDateEditor();
		Date value2 = null;
		if (jTextFieldDateEditor.getText().length()>0) {
			try {
				value2 = simpleDateFormat.parse(jTextFieldDateEditor.getText());
			}
			catch (ParseException e) {
				
				warningLabel.setToolTipText("Date is invalid.");
				warningLabel.setIcon(bugIcon);
				
				dirty = true;
				return false;
			}
		}
		
		warningLabel.setToolTipText(null);
		warningLabel.setIcon(null);
		
		if (value != null && value2 == null)
			dirty = true;

		else if (value == null && value2 != null)
			dirty = true;
			
		else if (value == null && value2 == null)
			return true;

		else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(value2);
			
			if (calendar.get(Calendar.DAY_OF_MONTH) != calendar2.get(Calendar.DAY_OF_MONTH) || calendar.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)
					|| calendar.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR))
				dirty = true;
		}
		return true;
	}
	
	private void checkConsistency() {

		dirty = false;
		
		boolean consistent = true;
		
		if (securityEvent.getEventType() != null && !securityEvent.getEventType().equals(((ComboBoxEntry) typeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((securityEvent.getEventType() == null )
				&& typeComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		consistent = consistent && dirtyFieldCheck(securityEvent.getEventDate(),eventDateChooser, eventDateWarningLabel);
		
		consistent = consistent && dirtyTimeFieldCheck(securityEvent.getEventTime(),eventTimeDateChooser, eventTimeWarningLabel);
		
		if(((JTextFieldDateEditor) eventTimeDateChooser.getDateEditor()).getText().trim().length()>0&&((JTextFieldDateEditor) eventDateChooser.getDateEditor()).getText().trim().length()==0)
		{
			eventDateWarningLabel.setIcon(bugIcon);
			eventDateWarningLabel.setToolTipText("Event date must be set if event time is used.");
			consistent = false;
		}
		
		if(dirtyFieldCheck(securityEvent.getEventText(), eventTextField))
			dirty = true;
		
		if(dirtyFieldCheck(securityEvent.getEventPrice(), priceTextField))
			dirty = true;		
		
		okButton.setEnabled(consistent&&dirty);

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
	 * Gets the security event.
	 *
	 * @return the security event
	 */
	public SecurityEvent getSecurityEvent() {
		return securityEvent;
	}

}
