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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.persistence.ComplexEventTime;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

/**
 * The Class SecurityComplexEventTimeDialog.
 */
public class SecurityComplexEventTimeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private ComplexEventTime complexEventTime = null;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	private JLabel eventStartDateWarningLabel;
	private JDateChooser eventStartDateChooser;
	private JDateChooser eventEndDateChooser;
	private boolean dirty;
	private final ImageIcon bugIcon = new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private JLabel eventEndDateWarningLabel;

	/**
	 * Instantiates a new security complex event time dialog.
	 *
	 * @param complexEventTime the complex event time
	 */
	public SecurityComplexEventTimeDialog(ComplexEventTime complexEventTime) {

		this.complexEventTime = complexEventTime;
		setBounds(100, 100, 533, 192);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SecurityComplexEventTimeDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 1.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 146 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel eventStartDateLabel = new JLabel("Start Time");
		eventStartDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventStartDateLabel = new GridBagConstraints();
		gbc_eventStartDateLabel.anchor = GridBagConstraints.WEST;
		gbc_eventStartDateLabel.insets = new Insets(25, 25, 5, 5);
		gbc_eventStartDateLabel.gridx = 0;
		gbc_eventStartDateLabel.gridy = 0;
		contentPanel.add(eventStartDateLabel, gbc_eventStartDateLabel);

		eventStartDateWarningLabel = new JLabel("");
		eventStartDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_eventStartDateWarningLabel = new GridBagConstraints();
		gbc_eventStartDateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventStartDateWarningLabel.insets = new Insets(25, 0, 5, 5);
		gbc_eventStartDateWarningLabel.gridx = 1;
		gbc_eventStartDateWarningLabel.gridy = 0;
		contentPanel.add(eventStartDateWarningLabel, gbc_eventStartDateWarningLabel);

		eventStartDateChooser = new JDateChooser();
		eventStartDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor) eventStartDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		eventStartDateChooser.setOpaque(false);
		eventStartDateChooser.getCalendarButton().setVisible(false);
		eventStartDateChooser.setDateFormatString("HH:mm:ss");
		((JTextFieldDateEditor) eventStartDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
		((JTextFieldDateEditor) eventStartDateChooser.getDateEditor()).setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				new EmptyBorder(0, 5, 0, 0)));
		GridBagConstraints gbc_eventStartDateChooser = new GridBagConstraints();
		gbc_eventStartDateChooser.gridwidth = 2;
		gbc_eventStartDateChooser.insets = new Insets(25, 0, 5, 25);
		gbc_eventStartDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventStartDateChooser.gridx = 2;
		gbc_eventStartDateChooser.gridy = 0;
		contentPanel.add(eventStartDateChooser, gbc_eventStartDateChooser);

		if (complexEventTime != null)
			eventStartDateChooser.setDate(complexEventTime.getEventStartTime());

		eventStartDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) eventStartDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		JLabel eventEndDateLabel = new JLabel("End Time");
		eventEndDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_eventEndDateLabel = new GridBagConstraints();
		gbc_eventEndDateLabel.anchor = GridBagConstraints.WEST;
		gbc_eventEndDateLabel.insets = new Insets(0, 25, 5, 5);
		gbc_eventEndDateLabel.gridx = 0;
		gbc_eventEndDateLabel.gridy = 1;
		contentPanel.add(eventEndDateLabel, gbc_eventEndDateLabel);

		eventEndDateWarningLabel = new JLabel("");
		eventEndDateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_eventEndDateWarningLabel = new GridBagConstraints();
		gbc_eventEndDateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventEndDateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_eventEndDateWarningLabel.gridx = 1;
		gbc_eventEndDateWarningLabel.gridy = 1;
		contentPanel.add(eventEndDateWarningLabel, gbc_eventEndDateWarningLabel);

		eventEndDateChooser = new JDateChooser();
		eventEndDateChooser.setPreferredSize(new Dimension(4, 25));
		((JTextFieldDateEditor) eventEndDateChooser.getDateEditor()).setDisabledTextColor(Color.BLACK);
		eventEndDateChooser.setOpaque(false);
		eventEndDateChooser.getCalendarButton().setVisible(false);
		eventEndDateChooser.setDateFormatString("HH:mm:ss");
		((JTextFieldDateEditor) eventEndDateChooser.getDateEditor()).setBackground(new Color(255, 243, 204));
		((JTextFieldDateEditor) eventEndDateChooser.getDateEditor()).setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				new EmptyBorder(0, 5, 0, 0)));
		eventEndDateChooser.setPreferredSize(new Dimension(4, 25));
		GridBagConstraints gbc_eventEndDateChooser = new GridBagConstraints();
		gbc_eventEndDateChooser.gridwidth = 2;
		gbc_eventEndDateChooser.insets = new Insets(0, 0, 5, 25);
		gbc_eventEndDateChooser.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventEndDateChooser.gridx = 2;
		gbc_eventEndDateChooser.gridy = 1;
		contentPanel.add(eventEndDateChooser, gbc_eventEndDateChooser);

		if (complexEventTime != null)
			eventEndDateChooser.setDate(complexEventTime.getEventEndTime());

		eventEndDateChooser.getDateEditor().addPropertyChangeListener(propertyChangeListener);
		((JTextFieldDateEditor) eventEndDateChooser.getDateEditor()).getDocument().addDocumentListener(documentListener);

		okButton = new JButton();

		if (this.complexEventTime == null) {
			this.complexEventTime = new ComplexEventTime();
			setTitle("Add Security Complex Event Time");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Security Complex Event Time");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
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

				try {
					Date date = simpleDateFormat.parse(((JTextFieldDateEditor) eventStartDateChooser.getDateEditor()).getText());
					SecurityComplexEventTimeDialog.this.complexEventTime.setEventStartTime(date);
				}
				catch (ParseException parseException) {
				}

				try {
					Date date = simpleDateFormat.parse(((JTextFieldDateEditor) eventEndDateChooser.getDateEditor()).getText());
					SecurityComplexEventTimeDialog.this.complexEventTime.setEventEndTime(date);
				}
				catch (ParseException parseException) {
				}

				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
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

	private boolean dirtyTimeFieldCheck(Date value, JDateChooser jDateChooser, JLabel warningLabel) {

		JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) jDateChooser.getDateEditor();
		Date value2 = null;
		if (jTextFieldDateEditor.getText().length() > 0) {
			try {
				value2 = simpleDateFormat.parse(jTextFieldDateEditor.getText());
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

	private void checkConsistency() {

		dirty = false;

		boolean consistent = true;

		consistent = consistent && dirtyTimeFieldCheck(complexEventTime.getEventStartTime(), eventStartDateChooser, eventStartDateWarningLabel);

		consistent = consistent && dirtyTimeFieldCheck(complexEventTime.getEventEndTime(), eventEndDateChooser, eventEndDateWarningLabel);

		if (consistent) {
			JTextFieldDateEditor jTextFieldDateEditor = (JTextFieldDateEditor) eventStartDateChooser.getDateEditor();
			Date value = null;
			if (jTextFieldDateEditor.getText().length() > 0) {
				try {
					value = simpleDateFormat.parse(jTextFieldDateEditor.getText());
				}
				catch (ParseException e) {
				}
			}
			if (value == null) {
				eventStartDateWarningLabel.setToolTipText("Start date is empty.");
				eventStartDateWarningLabel.setIcon(bugIcon);
				consistent = false;
			}
			else {
				JTextFieldDateEditor jTextFieldDateEditor2 = (JTextFieldDateEditor) eventEndDateChooser.getDateEditor();
				Date value2 = null;
				if (jTextFieldDateEditor2.getText().length() > 0) {
					try {
						value2 = simpleDateFormat.parse(jTextFieldDateEditor2.getText());
					}
					catch (ParseException e) {
					}
				}
				if (value2 != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(value);
					Calendar calendar2 = Calendar.getInstance();
					calendar2.setTime(value2);
					calendar.set(Calendar.YEAR, 1);
					calendar2.set(Calendar.YEAR, 1);
					calendar.set(Calendar.MONTH, 1);
					calendar2.set(Calendar.MONTH, 1);
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar2.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.MILLISECOND, 1);
					calendar2.set(Calendar.MILLISECOND, 1);
					if (calendar2.before(calendar)) {
						eventEndDateWarningLabel.setToolTipText("End date is before start date.");
						eventEndDateWarningLabel.setIcon(bugIcon);
						consistent = false;
					}
				}
			}
		}

		okButton.setEnabled(consistent && dirty);

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
	 * Gets the complex event time.
	 *
	 * @return the complex event time
	 */
	public ComplexEventTime getComplexEventTime() {

		return complexEventTime;
	}

}
