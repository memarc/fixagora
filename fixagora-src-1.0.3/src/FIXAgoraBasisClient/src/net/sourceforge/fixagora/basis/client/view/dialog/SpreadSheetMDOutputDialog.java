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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.dialog.fix.AbstractFIXPanel;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetMDOutput;

import org.apache.poi.hssf.util.CellReference;

/**
 * The Class SpreadSheetMDOutputDialog.
 */
public class SpreadSheetMDOutputDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private SpreadSheetMDOutput spreadSheetMDOutput = null;
	private JComboBox mdEntryTypeComboBox;
	private boolean dirty;
	private final ImageIcon bugIcon = new ImageIcon(SpreadSheetMDOutputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private final ImageIcon warning = new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));
	private JComboBox outputComponentComboBox;
	private JComboBox securityComboBox;
	private JComboBox sendIfComboBox;
	private JComboBox pxComboBox;
	private JComboBox sizeComboBox;
	private JComboBox dateComboBox;
	private JComboBox timeComboBox;
	private JComboBox deltaComboBox;
	private JComboBox tradeVolumeComboBox;
	private JComboBox highPxComboBox;
	private JComboBox lowPxComboBox;
	private JLabel lowPxWarningLabel;
	private JLabel highPxWarningLabel;
	private JLabel tradeVolumeWarningLabel;
	private JLabel deltaWarningLabel;
	private JLabel timeWarningLabel;
	private JLabel dateWarningLabel;
	private JLabel sizeWarningLabel;
	private JLabel pxWarningLabel;
	private JLabel sendIfWarningLabel;
	private JLabel securityWarningLabel;
	private JLabel deleteIfNotWarningLabel;
	private JComboBox deleteIfNotComboBox;

	/**
	 * Instantiates a new spread sheet md output dialog.
	 *
	 * @param spreadSheetMDOutput the spread sheet md output
	 * @param businessComponents the business components
	 */
	public SpreadSheetMDOutputDialog(SpreadSheetMDOutput spreadSheetMDOutput, List<AbstractBusinessObject> businessComponents) {

		this.spreadSheetMDOutput = spreadSheetMDOutput;
		setBounds(100, 100, 517, 520);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(SpreadSheetMDOutputDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};
		
		List<ComboBoxEntry> columnComboEntries = new ArrayList<ComboBoxEntry>();
		for(int i=0;i<255;i++)
			columnComboEntries.add(new ComboBoxEntry(i,CellReference.convertNumToColString(i)));
		
		List<ComboBoxEntry> optionalColumnComboEntries = new ArrayList<ComboBoxEntry>();
		optionalColumnComboEntries.add(new ComboBoxEntry("Not used"));
		for(int i=0;i<255;i++)
			optionalColumnComboEntries.add(new ComboBoxEntry(i,CellReference.convertNumToColString(i)));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0, 1.0, 0.0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		
		
		JLabel outputComponentLabel = new JLabel("Output Component");
		outputComponentLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_outputComponentLabel = new GridBagConstraints();
		gbc_outputComponentLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputComponentLabel.insets = new Insets(25, 25, 5, 5);
		gbc_outputComponentLabel.gridx = 0;
		gbc_outputComponentLabel.gridy = 0;
		contentPanel.add(outputComponentLabel, gbc_outputComponentLabel);

		ArrayList<ComboBoxEntry> businessComponents2 = new ArrayList<ComboBoxEntry>();
		for (AbstractBusinessObject abstractBusinessObject : businessComponents)
			if (!(abstractBusinessObject instanceof SpreadSheet))
				businessComponents2.add(new ComboBoxEntry(abstractBusinessObject,abstractBusinessObject.getName()));

		outputComponentComboBox = new JComboBox(businessComponents2.toArray());
		outputComponentComboBox.setMinimumSize(new Dimension(250, 25));
		outputComponentComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		outputComponentComboBox.setPreferredSize(new Dimension(250, 25));
		outputComponentComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			outputComponentComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getBusinessComponent(), null));
		else
			outputComponentComboBox.setSelectedIndex(0);
		outputComponentComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_outputComponentComboBox = new GridBagConstraints();
		gbc_outputComponentComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputComponentComboBox.gridwidth = 2;
		gbc_outputComponentComboBox.anchor = GridBagConstraints.NORTH;
		gbc_outputComponentComboBox.insets = new Insets(25, 0, 5, 25);
		gbc_outputComponentComboBox.gridx = 2;
		gbc_outputComponentComboBox.gridy = 0;
		contentPanel.add(outputComponentComboBox, gbc_outputComponentComboBox);
		
		JLabel securityLabel = new JLabel("Security");
		securityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_securityLabel = new GridBagConstraints();
		gbc_securityLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityLabel.insets = new Insets(0, 25, 5, 5);
		gbc_securityLabel.gridx = 0;
		gbc_securityLabel.gridy = 1;
		contentPanel.add(securityLabel, gbc_securityLabel);
		
		securityWarningLabel = new JLabel("");
		securityWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_securityWarningLabel = new GridBagConstraints();
		gbc_securityWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_securityWarningLabel.gridx = 1;
		gbc_securityWarningLabel.gridy = 1;
		contentPanel.add(securityWarningLabel, gbc_securityWarningLabel);

		securityComboBox = new JComboBox(columnComboEntries.toArray());
		securityComboBox.setMinimumSize(new Dimension(250, 25));
		securityComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		securityComboBox.setPreferredSize(new Dimension(250, 25));
		securityComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			securityComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getSecurityColumn(), null));
		else
			securityComboBox.setSelectedIndex(0);
		securityComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_securityComboBox = new GridBagConstraints();
		gbc_securityComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityComboBox.gridwidth = 2;
		gbc_securityComboBox.anchor = GridBagConstraints.NORTH;
		gbc_securityComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_securityComboBox.gridx = 2;
		gbc_securityComboBox.gridy = 1;
		contentPanel.add(securityComboBox, gbc_securityComboBox);
		
		JLabel sendIfLabel = new JLabel("Send If (Checked only)");
		sendIfLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sendIfLabel = new GridBagConstraints();
		gbc_sendIfLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendIfLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sendIfLabel.gridx = 0;
		gbc_sendIfLabel.gridy = 2;
		contentPanel.add(sendIfLabel, gbc_sendIfLabel);
		
		sendIfWarningLabel = new JLabel("");
		sendIfWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sendIfWarningLabel = new GridBagConstraints();
		gbc_sendIfWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendIfWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sendIfWarningLabel.gridx = 1;
		gbc_sendIfWarningLabel.gridy = 2;
		contentPanel.add(sendIfWarningLabel, gbc_sendIfWarningLabel);
		
		sendIfComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		sendIfComboBox.setMinimumSize(new Dimension(250, 25));
		sendIfComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sendIfComboBox.setPreferredSize(new Dimension(250, 25));
		sendIfComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			sendIfComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getSendIfColumn(), null));
		else
			sendIfComboBox.setSelectedIndex(0);
		sendIfComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_sendIfComboBox = new GridBagConstraints();
		gbc_sendIfComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendIfComboBox.gridwidth = 2;
		gbc_sendIfComboBox.anchor = GridBagConstraints.NORTH;
		gbc_sendIfComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_sendIfComboBox.gridx = 2;
		gbc_sendIfComboBox.gridy = 2;
		contentPanel.add(sendIfComboBox, gbc_sendIfComboBox);
		
		
		
		JLabel deleteIfNotLabel = new JLabel("Delete if NOT (Checked only)");
		deleteIfNotLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_deleteIfNotLabel = new GridBagConstraints();
		gbc_deleteIfNotLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteIfNotLabel.insets = new Insets(0, 25, 5, 5);
		gbc_deleteIfNotLabel.gridx = 0;
		gbc_deleteIfNotLabel.gridy = 3;
		contentPanel.add(deleteIfNotLabel, gbc_deleteIfNotLabel);
		
		deleteIfNotWarningLabel = new JLabel("");
		deleteIfNotWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_deleteIfNotWarningLabel = new GridBagConstraints();
		gbc_deleteIfNotWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteIfNotWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_deleteIfNotWarningLabel.gridx = 1;
		gbc_deleteIfNotWarningLabel.gridy = 3;
		contentPanel.add(deleteIfNotWarningLabel, gbc_deleteIfNotWarningLabel);
		
		deleteIfNotComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		deleteIfNotComboBox.setMinimumSize(new Dimension(250, 25));
		deleteIfNotComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		deleteIfNotComboBox.setPreferredSize(new Dimension(250, 25));
		deleteIfNotComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			deleteIfNotComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getDeleteIfNot(), null));
		else
			deleteIfNotComboBox.setSelectedIndex(0);
		deleteIfNotComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_deleteIfNotComboBox = new GridBagConstraints();
		gbc_deleteIfNotComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteIfNotComboBox.gridwidth = 2;
		gbc_deleteIfNotComboBox.anchor = GridBagConstraints.NORTH;
		gbc_deleteIfNotComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_deleteIfNotComboBox.gridx = 2;
		gbc_deleteIfNotComboBox.gridy = 3;
		contentPanel.add(deleteIfNotComboBox, gbc_deleteIfNotComboBox);
		
		
		JLabel mdEntryTypeLabel = new JLabel("MD Entry Type");
		mdEntryTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_mdEntryTypeLabel = new GridBagConstraints();
		gbc_mdEntryTypeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_mdEntryTypeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_mdEntryTypeLabel.gridx = 0;
		gbc_mdEntryTypeLabel.gridy = 4;
		contentPanel.add(mdEntryTypeLabel, gbc_mdEntryTypeLabel);

		List<ComboBoxEntry> typeComboEntries = new ArrayList<ComboBoxEntry>();
		typeComboEntries.add(new ComboBoxEntry("0", "Bid"));
		typeComboEntries.add(new ComboBoxEntry("1", "Offer"));
		typeComboEntries.add(new ComboBoxEntry("2", "Trade"));
		typeComboEntries.add(new ComboBoxEntry("3", "Index Value"));
		typeComboEntries.add(new ComboBoxEntry("4", "Opening Price"));
		typeComboEntries.add(new ComboBoxEntry("5", "Closing Price"));
		typeComboEntries.add(new ComboBoxEntry("6", "Settlement Price"));
		typeComboEntries.add(new ComboBoxEntry("7", "Trading Session High Price"));
		typeComboEntries.add(new ComboBoxEntry("8", "Trading Session Low Price"));
		typeComboEntries.add(new ComboBoxEntry("9", "Trading Session VWAP Price"));
		typeComboEntries.add(new ComboBoxEntry("A", "Imbalance"));
		typeComboEntries.add(new ComboBoxEntry("B", "Trade Volume"));
		typeComboEntries.add(new ComboBoxEntry("C", "Open Interest"));
		typeComboEntries.add(new ComboBoxEntry("D", "Composite Underlying Price"));
		typeComboEntries.add(new ComboBoxEntry("E", "Simulated Sell Price"));
		typeComboEntries.add(new ComboBoxEntry("F", "Simulated Buy Price"));
		typeComboEntries.add(new ComboBoxEntry("G", "Margin Rate"));
		typeComboEntries.add(new ComboBoxEntry("H", "Mid Price"));
		typeComboEntries.add(new ComboBoxEntry("J", "Empty Book"));
		typeComboEntries.add(new ComboBoxEntry("K", "Settle High Price"));
		typeComboEntries.add(new ComboBoxEntry("L", "Settle Low Price"));
		typeComboEntries.add(new ComboBoxEntry("M", "Prior Settle Price"));
		typeComboEntries.add(new ComboBoxEntry("N", "Session High Bid"));
		typeComboEntries.add(new ComboBoxEntry("O", "Session Low Offer"));
		typeComboEntries.add(new ComboBoxEntry("P", "Early Prices"));
		typeComboEntries.add(new ComboBoxEntry("Q", "Auction Clearing Price"));
		typeComboEntries.add(new ComboBoxEntry("S", "Swap Value Factor"));
		typeComboEntries.add(new ComboBoxEntry("R", "Daily value adjustment for long positions"));
		typeComboEntries.add(new ComboBoxEntry("T", "Cumulative Value Adjustment for long positions"));
		typeComboEntries.add(new ComboBoxEntry("U", "Daily Value Adjustment for Short Positions"));
		typeComboEntries.add(new ComboBoxEntry("V", "Cumulative Value Adjustment for Short Positions"));
		typeComboEntries.add(new ComboBoxEntry("Y", "Recovery Rate"));
		typeComboEntries.add(new ComboBoxEntry("Z", "Recovery Rate for Long"));
		typeComboEntries.add(new ComboBoxEntry("a", "Recovery Rate for Short"));
		typeComboEntries.add(new ComboBoxEntry("W", "Fixing Price"));
		typeComboEntries.add(new ComboBoxEntry("X", "Cash Rate"));

		mdEntryTypeComboBox = new JComboBox(typeComboEntries.toArray());
		mdEntryTypeComboBox.setMinimumSize(new Dimension(32, 25));
		mdEntryTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		mdEntryTypeComboBox.setPreferredSize(new Dimension(32, 25));
		mdEntryTypeComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			mdEntryTypeComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdEntryType(), null));
		else
			mdEntryTypeComboBox.setSelectedIndex(0);
		
		mdEntryTypeComboBox.addActionListener(actionListener);
		
		GridBagConstraints gbc_mdEntryTypeComboBox = new GridBagConstraints();
		gbc_mdEntryTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mdEntryTypeComboBox.gridwidth = 2;
		gbc_mdEntryTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_mdEntryTypeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_mdEntryTypeComboBox.gridx = 2;
		gbc_mdEntryTypeComboBox.gridy = 4;
		contentPanel.add(mdEntryTypeComboBox, gbc_mdEntryTypeComboBox);
		
		JLabel pxLabel = new JLabel("Px");
		pxLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_pxLabel = new GridBagConstraints();
		gbc_pxLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_pxLabel.insets = new Insets(0, 25, 5, 5);
		gbc_pxLabel.gridx = 0;
		gbc_pxLabel.gridy = 5;
		contentPanel.add(pxLabel, gbc_pxLabel);
		
		pxWarningLabel = new JLabel("");
		pxWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_pxWarningLabel = new GridBagConstraints();
		gbc_pxWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_pxWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_pxWarningLabel.gridx = 1;
		gbc_pxWarningLabel.gridy = 5;
		contentPanel.add(pxWarningLabel, gbc_pxWarningLabel);
		
		pxComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		pxComboBox.setMinimumSize(new Dimension(250, 25));
		pxComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		pxComboBox.setPreferredSize(new Dimension(250, 25));
		pxComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			pxComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdEntryPxColumn(), null));
		else
			pxComboBox.setSelectedIndex(0);
		pxComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_pxComboBox = new GridBagConstraints();
		gbc_pxComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_pxComboBox.gridwidth = 2;
		gbc_pxComboBox.anchor = GridBagConstraints.NORTH;
		gbc_pxComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_pxComboBox.gridx = 2;
		gbc_pxComboBox.gridy = 5;
		contentPanel.add(pxComboBox, gbc_pxComboBox);
		
		JLabel sizeLabel = new JLabel("Size");
		sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sizeLabel = new GridBagConstraints();
		gbc_sizeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sizeLabel.gridx = 0;
		gbc_sizeLabel.gridy = 6;
		contentPanel.add(sizeLabel, gbc_sizeLabel);
		
		sizeWarningLabel = new JLabel("");
		sizeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sizeWarningLabel = new GridBagConstraints();
		gbc_sizeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sizeWarningLabel.gridx = 1;
		gbc_sizeWarningLabel.gridy = 6;
		contentPanel.add(sizeWarningLabel, gbc_sizeWarningLabel);
		
		sizeComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		sizeComboBox.setMinimumSize(new Dimension(250, 25));
		sizeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sizeComboBox.setPreferredSize(new Dimension(250, 25));
		sizeComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			sizeComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdEntrySizeColumn(), null));
		else
			sizeComboBox.setSelectedIndex(0);
		sizeComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_sizeComboBox = new GridBagConstraints();
		gbc_sizeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeComboBox.gridwidth = 2;
		gbc_sizeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_sizeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_sizeComboBox.gridx = 2;
		gbc_sizeComboBox.gridy = 6;
		contentPanel.add(sizeComboBox, gbc_sizeComboBox);
		
		JLabel dateLabel = new JLabel("Date (no trigger)");
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_dateLabel = new GridBagConstraints();
		gbc_dateLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateLabel.insets = new Insets(0, 25, 5, 5);
		gbc_dateLabel.gridx = 0;
		gbc_dateLabel.gridy = 7;
		contentPanel.add(dateLabel, gbc_dateLabel);
		
		dateWarningLabel = new JLabel("");
		dateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_dateWarningLabel = new GridBagConstraints();
		gbc_dateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dateWarningLabel.gridx = 1;
		gbc_dateWarningLabel.gridy = 7;
		contentPanel.add(dateWarningLabel, gbc_dateWarningLabel);
		
		dateComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		dateComboBox.setMinimumSize(new Dimension(250, 25));
		dateComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		dateComboBox.setPreferredSize(new Dimension(250, 25));
		dateComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			dateComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdEntryDateColumn(), null));
		else
			dateComboBox.setSelectedIndex(0);
		dateComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_dateComboBox = new GridBagConstraints();
		gbc_dateComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateComboBox.gridwidth = 2;
		gbc_dateComboBox.anchor = GridBagConstraints.NORTH;
		gbc_dateComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_dateComboBox.gridx = 2;
		gbc_dateComboBox.gridy = 7;
		contentPanel.add(dateComboBox, gbc_dateComboBox);
		
		JLabel timeLabel = new JLabel("Time (no trigger)");
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_timeLabel = new GridBagConstraints();
		gbc_timeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_timeLabel.gridx = 0;
		gbc_timeLabel.gridy = 8;
		contentPanel.add(timeLabel, gbc_timeLabel);
		
		timeWarningLabel = new JLabel("");
		timeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_timeWarningLabel = new GridBagConstraints();
		gbc_timeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_timeWarningLabel.gridx = 1;
		gbc_timeWarningLabel.gridy = 8;
		contentPanel.add(timeWarningLabel, gbc_timeWarningLabel);
		
		timeComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		timeComboBox.setMinimumSize(new Dimension(250, 25));
		timeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		timeComboBox.setPreferredSize(new Dimension(250, 25));
		timeComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			timeComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdEntryTimeColumn(), null));
		else
			timeComboBox.setSelectedIndex(0);
		timeComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_timeComboBox = new GridBagConstraints();
		gbc_timeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeComboBox.gridwidth = 2;
		gbc_timeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_timeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_timeComboBox.gridx = 2;
		gbc_timeComboBox.gridy = 8;
		contentPanel.add(timeComboBox, gbc_timeComboBox);
		
		JLabel deltaLabel = new JLabel("Px Delta");
		deltaLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_deltaLabel = new GridBagConstraints();
		gbc_deltaLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_deltaLabel.insets = new Insets(0, 25, 5, 5);
		gbc_deltaLabel.gridx = 0;
		gbc_deltaLabel.gridy = 9;
		contentPanel.add(deltaLabel, gbc_deltaLabel);
		
		deltaWarningLabel = new JLabel("");
		deltaWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_deltaWarningLabel = new GridBagConstraints();
		gbc_deltaWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_deltaWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_deltaWarningLabel.gridx = 1;
		gbc_deltaWarningLabel.gridy = 9;
		contentPanel.add(deltaWarningLabel, gbc_deltaWarningLabel);
		
		deltaComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		deltaComboBox.setMinimumSize(new Dimension(250, 25));
		deltaComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		deltaComboBox.setPreferredSize(new Dimension(250, 25));
		deltaComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			deltaComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdPriceDeltaColumn(), null));
		else
			deltaComboBox.setSelectedIndex(0);
		deltaComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_deltaComboBox = new GridBagConstraints();
		gbc_deltaComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_deltaComboBox.gridwidth = 2;
		gbc_deltaComboBox.anchor = GridBagConstraints.NORTH;
		gbc_deltaComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_deltaComboBox.gridx = 2;
		gbc_deltaComboBox.gridy = 9;
		contentPanel.add(deltaComboBox, gbc_deltaComboBox);
		
		JLabel tradeVolumeLabel = new JLabel("Trade Volume");
		tradeVolumeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_tradeVolumeLabel = new GridBagConstraints();
		gbc_tradeVolumeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradeVolumeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_tradeVolumeLabel.gridx = 0;
		gbc_tradeVolumeLabel.gridy = 10;
		contentPanel.add(tradeVolumeLabel, gbc_tradeVolumeLabel);
		
		tradeVolumeWarningLabel = new JLabel("");
		tradeVolumeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_tradeVolumeWarningLabel = new GridBagConstraints();
		gbc_tradeVolumeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradeVolumeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_tradeVolumeWarningLabel.gridx = 1;
		gbc_tradeVolumeWarningLabel.gridy = 10;
		contentPanel.add(tradeVolumeWarningLabel, gbc_tradeVolumeWarningLabel);
		
		tradeVolumeComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		tradeVolumeComboBox.setMinimumSize(new Dimension(250, 25));
		tradeVolumeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		tradeVolumeComboBox.setPreferredSize(new Dimension(250, 25));
		tradeVolumeComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			tradeVolumeComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getMdTradeVolumeColumn(), null));
		else
			tradeVolumeComboBox.setSelectedIndex(0);
		tradeVolumeComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_tradeVolumeComboBox = new GridBagConstraints();
		gbc_tradeVolumeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradeVolumeComboBox.gridwidth = 2;
		gbc_tradeVolumeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_tradeVolumeComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_tradeVolumeComboBox.gridx = 2;
		gbc_tradeVolumeComboBox.gridy = 10;
		contentPanel.add(tradeVolumeComboBox, gbc_tradeVolumeComboBox);
		
		JLabel highPxLabel = new JLabel("High Px");
		highPxLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_highPxLabel = new GridBagConstraints();
		gbc_highPxLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_highPxLabel.insets = new Insets(0, 25, 5, 5);
		gbc_highPxLabel.gridx = 0;
		gbc_highPxLabel.gridy = 11;
		contentPanel.add(highPxLabel, gbc_highPxLabel);
		
		highPxWarningLabel = new JLabel("");
		highPxWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_highPxWarningLabel = new GridBagConstraints();
		gbc_highPxWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_highPxWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_highPxWarningLabel.gridx = 1;
		gbc_highPxWarningLabel.gridy = 11;
		contentPanel.add(highPxWarningLabel, gbc_highPxWarningLabel);

		
		highPxComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		highPxComboBox.setMinimumSize(new Dimension(250, 25));
		highPxComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		highPxComboBox.setPreferredSize(new Dimension(250, 25));
		highPxComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			highPxComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getHighPxColumn(), null));
		else
			highPxComboBox.setSelectedIndex(0);
		highPxComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_highPxComboBox = new GridBagConstraints();
		gbc_highPxComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_highPxComboBox.gridwidth = 2;
		gbc_highPxComboBox.anchor = GridBagConstraints.NORTH;
		gbc_highPxComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_highPxComboBox.gridx = 2;
		gbc_highPxComboBox.gridy = 11;
		contentPanel.add(highPxComboBox, gbc_highPxComboBox);
		
		JLabel lowPxLabel = new JLabel("Low Px");
		lowPxLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_lowPxLabel = new GridBagConstraints();
		gbc_lowPxLabel.anchor = GridBagConstraints.NORTH;
		gbc_lowPxLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lowPxLabel.insets = new Insets(3, 25, 5, 5);
		gbc_lowPxLabel.gridx = 0;
		gbc_lowPxLabel.gridy = 12;
		contentPanel.add(lowPxLabel, gbc_lowPxLabel);
		
		lowPxWarningLabel = new JLabel("");
		lowPxWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_lowPxWarningLabel = new GridBagConstraints();
		gbc_lowPxWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lowPxWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lowPxWarningLabel.gridx = 1;
		gbc_lowPxWarningLabel.gridy = 12;
		contentPanel.add(lowPxWarningLabel, gbc_lowPxWarningLabel);
		
		lowPxComboBox = new JComboBox(optionalColumnComboEntries.toArray());
		lowPxComboBox.setMinimumSize(new Dimension(250, 25));
		lowPxComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		lowPxComboBox.setPreferredSize(new Dimension(250, 25));
		lowPxComboBox.setBackground(new Color(255, 243, 204));
		
		if(spreadSheetMDOutput!=null)
			lowPxComboBox.setSelectedItem(new ComboBoxEntry(spreadSheetMDOutput.getLowPxColumn(), null));
		else
			lowPxComboBox.setSelectedIndex(0);
		lowPxComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_lowPxComboBox = new GridBagConstraints();
		gbc_lowPxComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_lowPxComboBox.gridwidth = 2;
		gbc_lowPxComboBox.anchor = GridBagConstraints.NORTH;
		gbc_lowPxComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_lowPxComboBox.gridx = 2;
		gbc_lowPxComboBox.gridy = 12;
		contentPanel.add(lowPxComboBox, gbc_lowPxComboBox);
		
		
		okButton = new JButton();
		okButton.setMinimumSize(new Dimension(100, 25));

		if (this.spreadSheetMDOutput == null) {
			this.spreadSheetMDOutput = new SpreadSheetMDOutput();
			setTitle("Add Market Data Input");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Market Data Input");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 13;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;				
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setBusinessComponent((AbstractBusinessComponent) ((ComboBoxEntry) outputComponentComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setSecurityColumn((Integer) ((ComboBoxEntry) securityComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdEntryType((String) ((ComboBoxEntry) mdEntryTypeComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setSendIfColumn((Integer) ((ComboBoxEntry) sendIfComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdEntryPxColumn((Integer) ((ComboBoxEntry) pxComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdEntrySizeColumn((Integer) ((ComboBoxEntry) sizeComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdEntryDateColumn((Integer) ((ComboBoxEntry) dateComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdEntryTimeColumn((Integer) ((ComboBoxEntry) timeComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdPriceDeltaColumn((Integer) ((ComboBoxEntry) deltaComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setMdTradeVolumeColumn((Integer) ((ComboBoxEntry) tradeVolumeComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setHighPxColumn((Integer) ((ComboBoxEntry) highPxComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setLowPxColumn((Integer) ((ComboBoxEntry) lowPxComboBox.getSelectedItem()).getEntry());
				SpreadSheetMDOutputDialog.this.spreadSheetMDOutput.setDeleteIfNot((Integer) ((ComboBoxEntry) deleteIfNotComboBox.getSelectedItem()).getEntry());
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTH;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 13;
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

		dirty = false;
				
		if (spreadSheetMDOutput.getBusinessComponent() != null && !spreadSheetMDOutput.getBusinessComponent().equals(((ComboBoxEntry) outputComponentComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getBusinessComponent() == null )
				&& outputComponentComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getSecurityColumn() != null && !spreadSheetMDOutput.getSecurityColumn().equals(((ComboBoxEntry) securityComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getSecurityColumn() == null )
				&& securityComboBox.getSelectedIndex() >= 0)
			dirty = true;
				
		
		if (spreadSheetMDOutput.getSendIfColumn() != null && !spreadSheetMDOutput.getSendIfColumn().equals(((ComboBoxEntry) sendIfComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getSendIfColumn() == null )
				&& sendIfComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (spreadSheetMDOutput.getDeleteIfNot() != null && !spreadSheetMDOutput.getDeleteIfNot().equals(((ComboBoxEntry) deleteIfNotComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getDeleteIfNot() == null )
				&& deleteIfNotComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdEntryPxColumn() != null && !spreadSheetMDOutput.getMdEntryPxColumn().equals(((ComboBoxEntry) pxComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdEntryPxColumn() == null )
				&& pxComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdEntryType() != null && !spreadSheetMDOutput.getMdEntryType().equals(((ComboBoxEntry) mdEntryTypeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdEntryType() == null )
				&& mdEntryTypeComboBox.getSelectedIndex() >= 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdEntrySizeColumn() != null && !spreadSheetMDOutput.getMdEntrySizeColumn().equals(((ComboBoxEntry) sizeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdEntrySizeColumn() == null )
				&& sizeComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdEntryDateColumn() != null && !spreadSheetMDOutput.getMdEntryDateColumn().equals(((ComboBoxEntry) dateComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdEntryDateColumn() == null )
				&& dateComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdEntryTimeColumn() != null && !spreadSheetMDOutput.getMdEntryTimeColumn().equals(((ComboBoxEntry) timeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdEntryTimeColumn() == null )
				&& timeComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getMdPriceDeltaColumn() != null && !spreadSheetMDOutput.getMdPriceDeltaColumn().equals(((ComboBoxEntry) deltaComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdPriceDeltaColumn() == null )
				&& deltaComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (spreadSheetMDOutput.getMdTradeVolumeColumn() != null && !spreadSheetMDOutput.getMdTradeVolumeColumn().equals(((ComboBoxEntry) tradeVolumeComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getMdTradeVolumeColumn() == null )
				&& tradeVolumeComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		
		if (spreadSheetMDOutput.getHighPxColumn() != null && !spreadSheetMDOutput.getHighPxColumn().equals(((ComboBoxEntry) highPxComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getHighPxColumn() == null )
				&& highPxComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		if (spreadSheetMDOutput.getLowPxColumn() != null && !spreadSheetMDOutput.getLowPxColumn().equals(((ComboBoxEntry) lowPxComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if ((spreadSheetMDOutput.getLowPxColumn() == null )
				&& lowPxComboBox.getSelectedIndex() > 0)
			dirty = true;
		
		Set<Integer> usedFields = new HashSet<Integer>();
		usedFields.add((Integer)((ComboBoxEntry)securityComboBox.getSelectedItem()).getEntry());
		
		checkComboBox(usedFields, pxComboBox, pxWarningLabel);
		checkComboBox(usedFields, sizeComboBox, sizeWarningLabel);
		checkComboBox(usedFields, dateComboBox, dateWarningLabel);
		checkComboBox(usedFields, timeComboBox, timeWarningLabel);
		checkComboBox(usedFields, deltaComboBox, deltaWarningLabel);
		checkComboBox(usedFields, tradeVolumeComboBox, tradeVolumeWarningLabel);
		checkComboBox(usedFields, highPxComboBox, highPxWarningLabel);
		checkComboBox(usedFields, lowPxComboBox, lowPxWarningLabel);
		
		String entryType = (String)((ComboBoxEntry)mdEntryTypeComboBox.getSelectedItem()).getEntry();
		if(!(entryType.equals("A")||entryType.equals("B")||entryType.equals("C")))
		{
			if(pxComboBox.getSelectedIndex()==0)
			{
				pxWarningLabel.setIcon(bugIcon);
				pxWarningLabel.setToolTipText("Conditionally required if MD Entry Type is not Imbalance, Trade Volume or Open Interest");
				dirty = false;
			}
		}
		
		if(entryType.equals("0")||entryType.equals("1")||entryType.equals("2")||entryType.equals("B")||entryType.equals("C"))
		{
			if(sizeComboBox.getSelectedIndex()==0)
			{
				sizeWarningLabel.setIcon(bugIcon);
				sizeWarningLabel.setToolTipText("Conditionally required if MD Entry Type is Bid, Offer, Trade, Trade Volume or Open Interest");
				dirty = false;
			}
		}
		
		if(!(entryType.equals("0")||entryType.equals("1")||entryType.equals("2")))
		{
			if(highPxComboBox.getSelectedIndex()!=0)
			{
				highPxWarningLabel.setIcon(warning);
				highPxWarningLabel.setToolTipText("Used only if MD Entry Type is Bid, Offer or Trade.");
			}
			if(lowPxComboBox.getSelectedIndex()!=0)
			{
				lowPxWarningLabel.setIcon(warning);
				lowPxWarningLabel.setToolTipText("Used only if MD Entry Type is Bid, Offer or Trade.");
			}
			if(highPxComboBox.getSelectedIndex()!=0)
			{
				highPxWarningLabel.setIcon(warning);
				highPxWarningLabel.setToolTipText("Used only if MD Entry Type is Bid, Offer or Trade.");
			}
		}
		
		okButton.setEnabled(dirty);

	}
	
	private void checkComboBox(Set<Integer> usedFields, JComboBox comboBox, JLabel warningLabel)
	{

		if(comboBox.getSelectedIndex()>0&&usedFields.contains(((ComboBoxEntry)comboBox.getSelectedItem()).getEntry()))
		{
			warningLabel.setIcon(warning);
			warningLabel.setToolTipText("Column is already in use. Please check if this is correct.");
		}
		else
		{
			if(comboBox.getSelectedIndex()>0)
			{
				usedFields.add((Integer)((ComboBoxEntry)comboBox.getSelectedItem()).getEntry());
			}
			warningLabel.setIcon(null);
			warningLabel.setToolTipText(null);
		}
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
	 * Gets the spread sheet md output.
	 *
	 * @return the spread sheet md output
	 */
	public SpreadSheetMDOutput getSpreadSheetMDOutput() {
		return spreadSheetMDOutput;
	}

}
