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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement;
import net.sourceforge.fixagora.basis.client.model.message.FIXComponent;
import net.sourceforge.fixagora.basis.client.model.message.FIXField;
import net.sourceforge.fixagora.basis.client.model.message.FIXGroup;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessage;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessageFilter;
import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement.FieldType;
import net.sourceforge.fixagora.basis.client.view.BackgroundViewPort;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXInputMessage;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXOutputMessage;

/**
 * The Class FIXMessagePanel.
 */
public class FIXMessagePanel extends AbstractFIXPanel {

	private static final long serialVersionUID = 1L;

	private JPanel panel = null;

	private JPanel panel_4 = null;

	private JScrollPane scrollPane = null;

	private FIXMainMessagePanel fixMainMessagePanel = null;

	/**
	 * Instantiates a new fIX message panel.
	 *
	 * @param abstractFIXElement the abstract fix element
	 * @param message the message
	 * @param fixMessageFilter the fix message filter
	 * @param waitDialog the wait dialog
	 * @param input the input
	 * @param fixMainMessagePanel the fix main message panel
	 */
	public FIXMessagePanel(final FIXMessage abstractFIXElement, final SpreadSheetFIXFieldMap message, FIXMessageFilter fixMessageFilter,
			final WaitDialog waitDialog, boolean input, FIXMainMessagePanel fixMainMessagePanel) {

		super(null, abstractFIXElement, message, true, abstractFIXElement.getDepth() + 1, fixMessageFilter, input);

		this.fixMainMessagePanel = fixMainMessagePanel;
		setOpaque(false);

		depth = abstractFIXElement.getDepth() + 1;
		enabled = true;

		numberLabel.setText(abstractFIXElement.getMessageType());
		requiredLabel.setText("");

		final JLabel lblNewLabel_5 = new JLabel("Spread Sheet Column");
		lblNewLabel_5.setFont(new Font("Dialog", Font.BOLD, 12));
		final GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.WEST;
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 0;
		lastPanel.add(lblNewLabel_5, gbc_comboBox);

		final JViewport jViewport = new BackgroundViewPort();
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 0, new Color(0, 0, 0)));
		scrollPane.setViewport(jViewport);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		childPanel.setLayout(new BorderLayout());
		childPanel.add(scrollPane, BorderLayout.CENTER);

		panel_4 = new JPanel();
		panel_4.setOpaque(false);
		scrollPane.setViewportView(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel_4.add(panel, BorderLayout.NORTH);
		panel.setBackground(Color.GREEN);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 1.0 };
		panel.setLayout(gbl_panel);

		int i = 0;

		if (!fixMessageFilter.isHideHeader()) {

			final FIXComponent fixComponent = abstractFIXElement.getHeader();

			SpreadSheetFIXFieldMap header = null;

			if (message instanceof SpreadSheetFIXInputMessage) {
				header = ((SpreadSheetFIXInputMessage) message).getHeader();
			}
			else if (message instanceof SpreadSheetFIXOutputMessage) {
				header = ((SpreadSheetFIXOutputMessage) message).getHeader();
			}

			final boolean atLeastOneSet = fixComponent.atLeastOneSet(header);

			if (atLeastOneSet || !fixMessageFilter.isHideEmptyFields()) {

				final FIXComponentPanel panel_3 = new FIXComponentPanel(this, fixComponent, header, true, abstractFIXElement.getDepth(), fixMessageFilter,
						input);
				final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
				gbc_panel_3.anchor = GridBagConstraints.WEST;
				gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel_3.gridx = 0;
				gbc_panel_3.gridy = i;
				children.add(panel_3);
				panel.add(panel_3, gbc_panel_3);
				i++;

			}
		}

		for (final AbstractFIXElement abstractFIXElement2 : abstractFIXElement.getAbstractFIXElements()) {

			waitDialog.doProgress();

			if (!(abstractFIXElement2.getFieldType() == FieldType.OPTIONAL) || !fixMessageFilter.isHideOptionalFields()) {

				if (abstractFIXElement2 instanceof FIXField) {

					final FIXField fixField = (FIXField) abstractFIXElement2;

					if (message.isSetField(fixField.getNumber()) || !fixMessageFilter.isHideEmptyFields()) {

						final FIXFieldPanel panel_3 = new FIXFieldPanel(this, fixField, message, true, abstractFIXElement.getDepth(), fixMessageFilter, input);
						final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
						gbc_panel_3.anchor = GridBagConstraints.WEST;
						gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
						gbc_panel_3.gridx = 0;
						gbc_panel_3.gridy = i;
						children.add(panel_3);
						panel.add(panel_3, gbc_panel_3);

						i++;
					}
				}

				if (abstractFIXElement2 instanceof FIXComponent) {
					final FIXComponent fixComponent = (FIXComponent) abstractFIXElement2;
					final boolean atLeastOneSet = fixComponent.atLeastOneSet(message);
					if (atLeastOneSet || !fixMessageFilter.isHideEmptyFields()) {

						final FIXComponentPanel panel_3 = new FIXComponentPanel(this, fixComponent, message, true, abstractFIXElement.getDepth(),
								fixMessageFilter, input);
						final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
						gbc_panel_3.anchor = GridBagConstraints.WEST;
						gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
						gbc_panel_3.gridx = 0;
						gbc_panel_3.gridy = i;
						children.add(panel_3);
						panel.add(panel_3, gbc_panel_3);

						i++;
					}
				}

				if (abstractFIXElement2 instanceof FIXGroup) {

					final FIXGroup fixGroup = (FIXGroup) abstractFIXElement2;
					if (message.hasGroup(fixGroup.getNumber()) && message.getGroupCount(fixGroup.getNumber()) > 0 || !fixMessageFilter.isHideEmptyFields()) {

						final FIXGroupPanel panel_3 = new FIXGroupPanel(this, fixGroup, message, true, abstractFIXElement.getDepth(), fixMessageFilter, input);
						final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
						gbc_panel_3.anchor = GridBagConstraints.WEST;
						gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
						gbc_panel_3.gridx = 0;
						gbc_panel_3.gridy = i;
						children.add(panel_3);
						panel.add(panel_3, gbc_panel_3);

						i++;
					}
				}
			}

		}

		if (!fixMessageFilter.isHideHeader()) {

			SpreadSheetFIXFieldMap trailer = null;

			if (message instanceof SpreadSheetFIXInputMessage) {
				trailer = ((SpreadSheetFIXInputMessage) message).getTrailer();
			}
			else if (message instanceof SpreadSheetFIXOutputMessage) {
				trailer = ((SpreadSheetFIXOutputMessage) message).getTrailer();
			}

			final FIXComponent fixComponent = abstractFIXElement.getTrailer();
			final boolean atLeastOneSet = fixComponent.atLeastOneSet(trailer);

			if (atLeastOneSet || !fixMessageFilter.isHideEmptyFields()) {

				final FIXComponentPanel panel_3 = new FIXComponentPanel(this, fixComponent, trailer, true, abstractFIXElement.getDepth(), fixMessageFilter,
						input);
				final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
				gbc_panel_3.anchor = GridBagConstraints.WEST;
				gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel_3.gridx = 0;
				gbc_panel_3.gridy = i;
				children.add(panel_3);
				panel.add(panel_3, gbc_panel_3);

				i++;
			}
		}

		if (i > 0) {

			topPanel.setBorder(new EmptyBorder(5, 25 * depth, 5, 10));
			add(leftPanel, BorderLayout.WEST);
			add(childPanel, BorderLayout.CENTER);
		}

		if (!input)
			warningIconLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(final MouseEvent e) {

				}

				@Override
				public void mouseEntered(final MouseEvent e) {

				}

				@Override
				public void mouseExited(final MouseEvent e) {

				}

				@Override
				public void mousePressed(final MouseEvent e) {

					final AbstractFIXPanel abstractFIXPanel = browseFieldCheckResult(fieldCheckResult);
					scrollTo(abstractFIXPanel);
				}

				@Override
				public void mouseReleased(final MouseEvent e) {

				}

			});

		fieldValueChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fixpusher.view.message.AbstractFIXPanel#fieldValueChanged
	 * ()
	 */
	@Override
	public void fieldValueChanged() {

		for (final AbstractFIXElement abstractFIXElement2 : abstractFIXElement.getAbstractFIXElements()) {

			final FieldCheckResult j = checkInputFieldMap(abstractFIXElement2, fieldMap);

			if (j.getBugLevel() == 0) {
				fieldCheckResult = j;
				warningIconLabel.setIcon(null);
				warningIconLabel.setToolTipText(null);
				break;
			}
			if(input)
				fieldCheckResult = new FieldCheckResult(2, "No key is assigned to a column", this);
			else
				fieldCheckResult = new FieldCheckResult(2, "No trigger is assigned to a column", this);
			warningIconLabel.setIcon(fieldCheckResult.getImageIcon());
			warningIconLabel.setToolTipText(fieldCheckResult.getToolTipText());
		}
		if (fieldCheckResult.getBugLevel() != 2 && !input) {
			FieldCheckResult i = new FieldCheckResult(0, "Message " + abstractFIXElement.getName() + " is ok.", this);

			for (final AbstractFIXElement abstractFIXElement2 : abstractFIXElement.getAbstractFIXElements()) {

				final FieldCheckResult j = checkFieldMap(abstractFIXElement2, fieldMap);

				if (j.getBugLevel() > i.getBugLevel())
					i = j;
			}

			fieldCheckResult = i;
			warningIconLabel.setIcon(i.getImageIcon());
			warningIconLabel.setToolTipText(i.getToolTipText());
		}

		validate();
		fixMainMessagePanel.fieldValueChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fixpusher.view.message.AbstractFIXPanel#getPanelBackground
	 * ()
	 */
	@Override
	protected Color getPanelBackground() {

		return new Color(204, 216, 255);
	}

	/**
	 * Scroll to.
	 *
	 * @param abstractFIXPanel the abstract fix panel
	 */
	public void scrollTo(final AbstractFIXPanel abstractFIXPanel) {

		if (abstractFIXPanel != null) {

			final Rectangle rectangle = abstractFIXPanel.getBounds();
			rectangle.height = scrollPane.getViewport().getHeight();

			panel_4.scrollRectToVisible(SwingUtilities.convertRectangle(abstractFIXPanel.getParent(), rectangle, panel_4));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#toString()
	 */
	@Override
	public String toString() {

		return "Message";
	}

	/**
	 * Gets the field check result.
	 *
	 * @return the field check result
	 */
	public FieldCheckResult getFieldCheckResult() {

		return fieldCheckResult;
	}

}
