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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;

/**
 * The Class FIXInitiatorEditorMarketData.
 */
public class FIXInitiatorEditorMarketData extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private FIXInitiatorEditor fixInitiatorEditor = null;

	private boolean dirty = false;

	private JCheckBox bidCkeckBox;

	private JCheckBox offerCheckBox;

	private JCheckBox recoveryRateForShortCheckBox;

	private JCheckBox recoveryRateForLongCheckBox;

	private JCheckBox recoveryRateCheckBox;

	private JCheckBox cashRateCheckBox;

	private JCheckBox fixingPriceCheckBox;

	private JCheckBox cumulativeValueAdjustmentForShortCheckBox;

	private JCheckBox dailyValueAdjustmentForShortCheckBox;

	private JCheckBox cumulativeValueAdjustmentForLongCheckBox;

	private JCheckBox dailyValueAdjustmentForLongCheckBox;

	private JCheckBox swapValueFactorCheckBox;

	private JCheckBox auctionClearingPriceCheckBox;

	private JCheckBox earlyPricesCheckBox;

	private JCheckBox sessionLowOfferCheckBox;

	private JCheckBox sessionHighBidCheckBox;

	private JCheckBox priorSettlePriceCheckBox;

	private JCheckBox settleLowPriceCheckBox;

	private JCheckBox settleHighPriceCheckBox;

	private JCheckBox midPriceCheckBox;

	private JCheckBox marginRateCheckBox;

	private JCheckBox simulatedBuyPriceCheckBox;

	private JCheckBox simulatedSellPriceCheckBox;

	private JCheckBox compositeUnderlyingCheckBox;

	private JCheckBox openInterestCheckBox;

	private JCheckBox tradeVolumeCheckBox;

	private JCheckBox tradingSessionVWAPPriceCheckBox;

	private JCheckBox tradingSessionLowPriceCheckBox;

	private JCheckBox tradingSessionHighPriceCheckBox;

	private JCheckBox settlementPriceCheckBox;

	private JCheckBox closingPriceCheckBox;

	private JCheckBox openingPriceCheckBox;

	private JCheckBox indexValueCheckBox;

	private JCheckBox tradeCheckBox;
	private JPanel panel_1;

	private JCheckBox shutdownCheckBox;

	private JLabel requestTypeWarningLabel;

	private JComboBox requestTypeComboBox;

	private boolean consistent;

	/**
	 * Instantiates a new fIX initiator editor market data.
	 *
	 * @param fixInitiatorEditor the fix initiator editor
	 */
	public FIXInitiatorEditorMarketData(FIXInitiatorEditor fixInitiatorEditor) {

		super();

		setBorder(new EmptyBorder(0, 0, 0, 0));
		getViewport().setBackground(new Color(204, 216, 255));

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		setViewportView(panel);

		this.fixInitiatorEditor = fixInitiatorEditor;
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(50, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 1;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0 };
		gbl_panel_1.rowHeights = new int[] { 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0 };
		gbl_panel_1.rowWeights = new double[] { Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel requestTypeLabel = new JLabel("Request Type");
		requestTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_requestTypeLabel = new GridBagConstraints();
		gbc_requestTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_requestTypeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_requestTypeLabel.gridx = 0;
		gbc_requestTypeLabel.gridy = 0;
		panel_1.add(requestTypeLabel, gbc_requestTypeLabel);

		requestTypeWarningLabel = new JLabel("");
		requestTypeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_requestTypeWarningLabel = new GridBagConstraints();
		gbc_requestTypeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_requestTypeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_requestTypeWarningLabel.gridx = 1;
		gbc_requestTypeWarningLabel.gridy = 0;
		panel_1.add(requestTypeWarningLabel, gbc_requestTypeWarningLabel);

		List<ComboBoxEntry> comboBoxEntries = new ArrayList<ComboBoxEntry>();
		comboBoxEntries.add(new ComboBoxEntry(0, "Disabled"));
		comboBoxEntries.add(new ComboBoxEntry(1, "Snapshot only"));
		comboBoxEntries.add(new ComboBoxEntry(2, "Snapshot + Full Refresh"));
		comboBoxEntries.add(new ComboBoxEntry(3, "Snapshot + Incremental Refresh"));

		requestTypeComboBox = new JComboBox(comboBoxEntries.toArray());
		requestTypeComboBox.setMinimumSize(new Dimension(250, 25));
		requestTypeComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		requestTypeComboBox.setPreferredSize(new Dimension(250, 25));
		requestTypeComboBox.setBackground(new Color(255, 243, 204));

		requestTypeComboBox.addActionListener(actionListener);

		GridBagConstraints gbc_requestTypeComboBox = new GridBagConstraints();
		gbc_requestTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_requestTypeComboBox.gridwidth = 2;
		gbc_requestTypeComboBox.anchor = GridBagConstraints.NORTH;
		gbc_requestTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_requestTypeComboBox.gridx = 2;
		gbc_requestTypeComboBox.gridy = 0;
		panel_1.add(requestTypeComboBox, gbc_requestTypeComboBox);

		JPanel leftFillPanel = new JPanel();
		leftFillPanel.setOpaque(false);
		GridBagConstraints gbc_leftFillPanel = new GridBagConstraints();
		gbc_leftFillPanel.weightx = 1.0;
		gbc_leftFillPanel.gridheight = 7;
		gbc_leftFillPanel.insets = new Insets(0, 0, 5, 5);
		gbc_leftFillPanel.fill = GridBagConstraints.BOTH;
		gbc_leftFillPanel.gridx = 0;
		gbc_leftFillPanel.gridy = 2;
		panel.add(leftFillPanel, gbc_leftFillPanel);

		shutdownCheckBox = new JCheckBox("Shutdown after Snapshot");
		shutdownCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		shutdownCheckBox.addActionListener(actionListener);
		shutdownCheckBox.setOpaque(false);

		GridBagConstraints gbc_shutdownCkeckBox = new GridBagConstraints();
		gbc_shutdownCkeckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_shutdownCkeckBox.insets = new Insets(50, 50, 5, 5);
		gbc_shutdownCkeckBox.gridx = 3;
		gbc_shutdownCkeckBox.gridy = 1;
		panel.add(shutdownCheckBox, gbc_shutdownCkeckBox);

		bidCkeckBox = new JCheckBox("Bid");
		bidCkeckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		bidCkeckBox.addActionListener(actionListener);
		bidCkeckBox.setOpaque(false);

		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameLabel.insets = new Insets(0, 50, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 2;
		panel.add(bidCkeckBox, gbc_nameLabel);

		offerCheckBox = new JCheckBox("Offer");
		offerCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		offerCheckBox.addActionListener(actionListener);
		offerCheckBox.setOpaque(false);

		GridBagConstraints gbc_offerCheckBox = new GridBagConstraints();
		gbc_offerCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_offerCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_offerCheckBox.gridx = 1;
		gbc_offerCheckBox.gridy = 3;
		panel.add(offerCheckBox, gbc_offerCheckBox);

		tradeCheckBox = new JCheckBox("Trade");
		tradeCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradeCheckBox.addActionListener(actionListener);
		tradeCheckBox.setOpaque(false);

		GridBagConstraints gbc_tradeCheckBox = new GridBagConstraints();
		gbc_tradeCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradeCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_tradeCheckBox.gridx = 1;
		gbc_tradeCheckBox.gridy = 4;
		panel.add(tradeCheckBox, gbc_tradeCheckBox);

		indexValueCheckBox = new JCheckBox("Index Value");
		indexValueCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		indexValueCheckBox.addActionListener(actionListener);
		indexValueCheckBox.setOpaque(false);

		GridBagConstraints gbc_indexValueCheckBox = new GridBagConstraints();
		gbc_indexValueCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_indexValueCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_indexValueCheckBox.gridx = 1;
		gbc_indexValueCheckBox.gridy = 5;
		panel.add(indexValueCheckBox, gbc_indexValueCheckBox);

		openingPriceCheckBox = new JCheckBox("Opening Price");
		openingPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		openingPriceCheckBox.addActionListener(actionListener);
		openingPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_openingPriceCheckBox = new GridBagConstraints();
		gbc_openingPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_openingPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_openingPriceCheckBox.gridx = 1;
		gbc_openingPriceCheckBox.gridy = 6;
		panel.add(openingPriceCheckBox, gbc_openingPriceCheckBox);

		closingPriceCheckBox = new JCheckBox("Closing Price");
		closingPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		closingPriceCheckBox.addActionListener(actionListener);
		closingPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_closingPriceCheckBox = new GridBagConstraints();
		gbc_closingPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_closingPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_closingPriceCheckBox.gridx = 1;
		gbc_closingPriceCheckBox.gridy = 7;
		panel.add(closingPriceCheckBox, gbc_closingPriceCheckBox);

		settlementPriceCheckBox = new JCheckBox("Settlement Price");
		settlementPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		settlementPriceCheckBox.addActionListener(actionListener);
		settlementPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_settlementPriceCheckBox = new GridBagConstraints();
		gbc_settlementPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settlementPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_settlementPriceCheckBox.gridx = 1;
		gbc_settlementPriceCheckBox.gridy = 8;
		panel.add(settlementPriceCheckBox, gbc_settlementPriceCheckBox);

		tradingSessionHighPriceCheckBox = new JCheckBox("Trading Session High Price");
		tradingSessionHighPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradingSessionHighPriceCheckBox.addActionListener(actionListener);
		tradingSessionHighPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_tradingSessionHighPriceCheckBox = new GridBagConstraints();
		gbc_tradingSessionHighPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradingSessionHighPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_tradingSessionHighPriceCheckBox.gridx = 1;
		gbc_tradingSessionHighPriceCheckBox.gridy = 9;
		panel.add(tradingSessionHighPriceCheckBox, gbc_tradingSessionHighPriceCheckBox);

		tradingSessionLowPriceCheckBox = new JCheckBox("Trading Session Low Price");
		tradingSessionLowPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradingSessionLowPriceCheckBox.addActionListener(actionListener);
		tradingSessionLowPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_tradingSessionLowPriceCheckBox = new GridBagConstraints();
		gbc_tradingSessionLowPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradingSessionLowPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_tradingSessionLowPriceCheckBox.gridx = 1;
		gbc_tradingSessionLowPriceCheckBox.gridy = 10;
		panel.add(tradingSessionLowPriceCheckBox, gbc_tradingSessionLowPriceCheckBox);

		tradingSessionVWAPPriceCheckBox = new JCheckBox("Trading Session VWAP Price");
		tradingSessionVWAPPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradingSessionVWAPPriceCheckBox.addActionListener(actionListener);
		tradingSessionVWAPPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_tradingSessionVWAPPriceCheckBox = new GridBagConstraints();
		gbc_tradingSessionVWAPPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradingSessionVWAPPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_tradingSessionVWAPPriceCheckBox.gridx = 1;
		gbc_tradingSessionVWAPPriceCheckBox.gridy = 11;
		panel.add(tradingSessionVWAPPriceCheckBox, gbc_tradingSessionVWAPPriceCheckBox);


		tradeVolumeCheckBox = new JCheckBox("Trade Volume");
		tradeVolumeCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		tradeVolumeCheckBox.addActionListener(actionListener);
		tradeVolumeCheckBox.setOpaque(false);

		GridBagConstraints gbc_tradeVolumeCheckBox = new GridBagConstraints();
		gbc_tradeVolumeCheckBox.anchor = GridBagConstraints.NORTH;
		gbc_tradeVolumeCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_tradeVolumeCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_tradeVolumeCheckBox.gridx = 1;
		gbc_tradeVolumeCheckBox.gridy = 12;
		panel.add(tradeVolumeCheckBox, gbc_tradeVolumeCheckBox);

		openInterestCheckBox = new JCheckBox("Open Interest");
		openInterestCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		openInterestCheckBox.addActionListener(actionListener);
		openInterestCheckBox.setOpaque(false);

		GridBagConstraints gbc_openInterestCheckBox = new GridBagConstraints();
		gbc_openInterestCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_openInterestCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_openInterestCheckBox.gridx = 1;
		gbc_openInterestCheckBox.gridy = 13;
		panel.add(openInterestCheckBox, gbc_openInterestCheckBox);

		compositeUnderlyingCheckBox = new JCheckBox("Composite Underlying Price");
		compositeUnderlyingCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		compositeUnderlyingCheckBox.addActionListener(actionListener);
		compositeUnderlyingCheckBox.setOpaque(false);

		GridBagConstraints gbc_compositeUnderlyingCheckBox = new GridBagConstraints();
		gbc_compositeUnderlyingCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_compositeUnderlyingCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_compositeUnderlyingCheckBox.gridx = 2;
		gbc_compositeUnderlyingCheckBox.gridy = 2;
		panel.add(compositeUnderlyingCheckBox, gbc_compositeUnderlyingCheckBox);

		simulatedSellPriceCheckBox = new JCheckBox("Simulated Sell Price");
		simulatedSellPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		simulatedSellPriceCheckBox.addActionListener(actionListener);
		simulatedSellPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_simulatedSellPriceCheckBox = new GridBagConstraints();
		gbc_simulatedSellPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_simulatedSellPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_simulatedSellPriceCheckBox.gridx = 2;
		gbc_simulatedSellPriceCheckBox.gridy = 3;
		panel.add(simulatedSellPriceCheckBox, gbc_simulatedSellPriceCheckBox);

		simulatedBuyPriceCheckBox = new JCheckBox("Simulated Buy Price");
		simulatedBuyPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		simulatedBuyPriceCheckBox.addActionListener(actionListener);
		simulatedBuyPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_simulatedBuyPriceCheckBox = new GridBagConstraints();
		gbc_simulatedBuyPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_simulatedBuyPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_simulatedBuyPriceCheckBox.gridx = 2;
		gbc_simulatedBuyPriceCheckBox.gridy = 4;
		panel.add(simulatedBuyPriceCheckBox, gbc_simulatedBuyPriceCheckBox);

		marginRateCheckBox = new JCheckBox("Margin Rate");
		marginRateCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		marginRateCheckBox.addActionListener(actionListener);
		marginRateCheckBox.setOpaque(false);

		GridBagConstraints gbc_marginRateCheckBox = new GridBagConstraints();
		gbc_marginRateCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_marginRateCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_marginRateCheckBox.gridx = 2;
		gbc_marginRateCheckBox.gridy = 5;
		panel.add(marginRateCheckBox, gbc_marginRateCheckBox);

		midPriceCheckBox = new JCheckBox("Mid Price");
		midPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		midPriceCheckBox.addActionListener(actionListener);
		midPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_midPriceCheckBox = new GridBagConstraints();
		gbc_midPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_midPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_midPriceCheckBox.gridx = 2;
		gbc_midPriceCheckBox.gridy = 6;
		panel.add(midPriceCheckBox, gbc_midPriceCheckBox);

		settleHighPriceCheckBox = new JCheckBox("Settle High Price");
		settleHighPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		settleHighPriceCheckBox.addActionListener(actionListener);
		settleHighPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_settleHighPriceCheckBox = new GridBagConstraints();
		gbc_settleHighPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settleHighPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_settleHighPriceCheckBox.gridx = 2;
		gbc_settleHighPriceCheckBox.gridy = 7;
		panel.add(settleHighPriceCheckBox, gbc_settleHighPriceCheckBox);

		settleLowPriceCheckBox = new JCheckBox("Settle Low Price");
		settleLowPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		settleLowPriceCheckBox.addActionListener(actionListener);
		settleLowPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_settleLowPriceCheckBox = new GridBagConstraints();
		gbc_settleLowPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_settleLowPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_settleLowPriceCheckBox.gridx = 2;
		gbc_settleLowPriceCheckBox.gridy = 8;
		panel.add(settleLowPriceCheckBox, gbc_settleLowPriceCheckBox);

		priorSettlePriceCheckBox = new JCheckBox("Prior Settle Price");
		priorSettlePriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		priorSettlePriceCheckBox.addActionListener(actionListener);
		priorSettlePriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_priorSettlePriceCheckBox = new GridBagConstraints();
		gbc_priorSettlePriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priorSettlePriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_priorSettlePriceCheckBox.gridx = 2;
		gbc_priorSettlePriceCheckBox.gridy = 9;
		panel.add(priorSettlePriceCheckBox, gbc_priorSettlePriceCheckBox);

		sessionHighBidCheckBox = new JCheckBox("Session High Bid");
		sessionHighBidCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		sessionHighBidCheckBox.addActionListener(actionListener);
		sessionHighBidCheckBox.setOpaque(false);

		GridBagConstraints gbc_sessionHighBidCheckBox = new GridBagConstraints();
		gbc_sessionHighBidCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sessionHighBidCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_sessionHighBidCheckBox.gridx = 2;
		gbc_sessionHighBidCheckBox.gridy = 10;
		panel.add(sessionHighBidCheckBox, gbc_sessionHighBidCheckBox);

		sessionLowOfferCheckBox = new JCheckBox("Session Low Offer");
		sessionLowOfferCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		sessionLowOfferCheckBox.addActionListener(actionListener);
		sessionLowOfferCheckBox.setOpaque(false);

		GridBagConstraints gbc_sessionLowOfferCheckBox = new GridBagConstraints();
		gbc_sessionLowOfferCheckBox.anchor = GridBagConstraints.NORTH;
		gbc_sessionLowOfferCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sessionLowOfferCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_sessionLowOfferCheckBox.gridx = 2;
		gbc_sessionLowOfferCheckBox.gridy = 11;
		panel.add(sessionLowOfferCheckBox, gbc_sessionLowOfferCheckBox);

		earlyPricesCheckBox = new JCheckBox("Early Prices");
		earlyPricesCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		earlyPricesCheckBox.addActionListener(actionListener);
		earlyPricesCheckBox.setOpaque(false);

		GridBagConstraints gbc_earlyPricesCheckBox = new GridBagConstraints();
		gbc_earlyPricesCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_earlyPricesCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_earlyPricesCheckBox.gridx = 2;
		gbc_earlyPricesCheckBox.gridy = 12;
		panel.add(earlyPricesCheckBox, gbc_earlyPricesCheckBox);

		auctionClearingPriceCheckBox = new JCheckBox("Auction Clearing Price");
		auctionClearingPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		auctionClearingPriceCheckBox.addActionListener(actionListener);
		auctionClearingPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_auctionClearingPriceCheckBox = new GridBagConstraints();
		gbc_auctionClearingPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_auctionClearingPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_auctionClearingPriceCheckBox.gridx = 2;
		gbc_auctionClearingPriceCheckBox.gridy = 13;
		panel.add(auctionClearingPriceCheckBox, gbc_auctionClearingPriceCheckBox);

		swapValueFactorCheckBox = new JCheckBox("Swap Value Factor");
		swapValueFactorCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		swapValueFactorCheckBox.addActionListener(actionListener);
		swapValueFactorCheckBox.setOpaque(false);

		GridBagConstraints gbc_swapValueFactorCheckBox = new GridBagConstraints();
		gbc_swapValueFactorCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_swapValueFactorCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_swapValueFactorCheckBox.gridx = 3;
		gbc_swapValueFactorCheckBox.gridy = 2;
		panel.add(swapValueFactorCheckBox, gbc_swapValueFactorCheckBox);

		dailyValueAdjustmentForLongCheckBox = new JCheckBox("Daily value adjustment for long positions");
		dailyValueAdjustmentForLongCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		dailyValueAdjustmentForLongCheckBox.addActionListener(actionListener);
		dailyValueAdjustmentForLongCheckBox.setOpaque(false);

		GridBagConstraints gbc_dailyValueAdjustmentForLongCheckBox = new GridBagConstraints();
		gbc_dailyValueAdjustmentForLongCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dailyValueAdjustmentForLongCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_dailyValueAdjustmentForLongCheckBox.gridx = 3;
		gbc_dailyValueAdjustmentForLongCheckBox.gridy = 3;
		panel.add(dailyValueAdjustmentForLongCheckBox, gbc_dailyValueAdjustmentForLongCheckBox);

		cumulativeValueAdjustmentForLongCheckBox = new JCheckBox("Cumulative value adjustment for long positions");
		cumulativeValueAdjustmentForLongCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		cumulativeValueAdjustmentForLongCheckBox.addActionListener(actionListener);
		cumulativeValueAdjustmentForLongCheckBox.setOpaque(false);

		GridBagConstraints gbc_cumulativeValueAdjustmentForLongCheckBox = new GridBagConstraints();
		gbc_cumulativeValueAdjustmentForLongCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_cumulativeValueAdjustmentForLongCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_cumulativeValueAdjustmentForLongCheckBox.gridx = 3;
		gbc_cumulativeValueAdjustmentForLongCheckBox.gridy = 4;
		panel.add(cumulativeValueAdjustmentForLongCheckBox, gbc_cumulativeValueAdjustmentForLongCheckBox);

		dailyValueAdjustmentForShortCheckBox = new JCheckBox("Daily value adjustment for short positions");
		dailyValueAdjustmentForShortCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		dailyValueAdjustmentForShortCheckBox.addActionListener(actionListener);
		dailyValueAdjustmentForShortCheckBox.setOpaque(false);

		GridBagConstraints gbc_dailyValueAdjustmentForShortCheckBox = new GridBagConstraints();
		gbc_dailyValueAdjustmentForShortCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dailyValueAdjustmentForShortCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_dailyValueAdjustmentForShortCheckBox.gridx = 3;
		gbc_dailyValueAdjustmentForShortCheckBox.gridy = 5;
		panel.add(dailyValueAdjustmentForShortCheckBox, gbc_dailyValueAdjustmentForShortCheckBox);

		cumulativeValueAdjustmentForShortCheckBox = new JCheckBox("Cumulative value adjustment for short positions");
		cumulativeValueAdjustmentForShortCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		cumulativeValueAdjustmentForShortCheckBox.addActionListener(actionListener);
		cumulativeValueAdjustmentForShortCheckBox.setOpaque(false);

		GridBagConstraints gbc_cumulativeValueAdjustmentForShortCheckBox = new GridBagConstraints();
		gbc_cumulativeValueAdjustmentForShortCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_cumulativeValueAdjustmentForShortCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_cumulativeValueAdjustmentForShortCheckBox.gridx = 3;
		gbc_cumulativeValueAdjustmentForShortCheckBox.gridy = 6;
		panel.add(cumulativeValueAdjustmentForShortCheckBox, gbc_cumulativeValueAdjustmentForShortCheckBox);

		fixingPriceCheckBox = new JCheckBox("Fixing Price");
		fixingPriceCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		fixingPriceCheckBox.addActionListener(actionListener);
		fixingPriceCheckBox.setOpaque(false);

		GridBagConstraints gbc_fixingPriceCheckBox = new GridBagConstraints();
		gbc_fixingPriceCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_fixingPriceCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_fixingPriceCheckBox.gridx = 3;
		gbc_fixingPriceCheckBox.gridy = 7;
		panel.add(fixingPriceCheckBox, gbc_fixingPriceCheckBox);

		cashRateCheckBox = new JCheckBox("Cash Rate");
		cashRateCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		cashRateCheckBox.addActionListener(actionListener);
		cashRateCheckBox.setOpaque(false);

		GridBagConstraints gbc_cashRateCheckBox = new GridBagConstraints();
		gbc_cashRateCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_cashRateCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_cashRateCheckBox.gridx = 3;
		gbc_cashRateCheckBox.gridy = 8;
		panel.add(cashRateCheckBox, gbc_cashRateCheckBox);

		recoveryRateCheckBox = new JCheckBox("Recovery Rate");
		recoveryRateCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		recoveryRateCheckBox.addActionListener(actionListener);
		recoveryRateCheckBox.setOpaque(false);

		GridBagConstraints gbc_recoveryRateCheckBox = new GridBagConstraints();
		gbc_recoveryRateCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_recoveryRateCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_recoveryRateCheckBox.gridx = 3;
		gbc_recoveryRateCheckBox.gridy = 9;
		panel.add(recoveryRateCheckBox, gbc_recoveryRateCheckBox);

		recoveryRateForLongCheckBox = new JCheckBox("Recovery Rate for Long");
		recoveryRateForLongCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		recoveryRateForLongCheckBox.addActionListener(actionListener);
		recoveryRateForLongCheckBox.setOpaque(false);

		GridBagConstraints gbc_recoveryRateForLongCheckBox = new GridBagConstraints();
		gbc_recoveryRateForLongCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_recoveryRateForLongCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_recoveryRateForLongCheckBox.gridx = 3;
		gbc_recoveryRateForLongCheckBox.gridy = 10;
		panel.add(recoveryRateForLongCheckBox, gbc_recoveryRateForLongCheckBox);

		recoveryRateForShortCheckBox = new JCheckBox("Recovery Rate for Short");
		recoveryRateForShortCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		recoveryRateForShortCheckBox.addActionListener(actionListener);
		recoveryRateForShortCheckBox.setOpaque(false);

		GridBagConstraints gbc_recoveryRateForShortCheckBox = new GridBagConstraints();
		gbc_recoveryRateForShortCheckBox.anchor = GridBagConstraints.NORTH;
		gbc_recoveryRateForShortCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_recoveryRateForShortCheckBox.insets = new Insets(0, 50, 5, 5);
		gbc_recoveryRateForShortCheckBox.gridx = 3;
		gbc_recoveryRateForShortCheckBox.gridy = 11;
		panel.add(recoveryRateForShortCheckBox, gbc_recoveryRateForShortCheckBox);

		updateContent();
	}

	private boolean dirtyFieldCheck(JCheckBox jCheckBox, Boolean value) {

		if (value == null) {
			if (jCheckBox.isSelected())
				return true;
			return false;
		}

		return ((boolean) value) != jCheckBox.isSelected();

	}

	private void checkConsistency() {

		dirty = false;

		if (dirtyFieldCheck(bidCkeckBox, fixInitiatorEditor.getFixInitiator().getBid()))
			dirty = true;

		if (dirtyFieldCheck(offerCheckBox, fixInitiatorEditor.getFixInitiator().getOffer()))
			dirty = true;

		if (dirtyFieldCheck(recoveryRateForShortCheckBox, fixInitiatorEditor.getFixInitiator().getRecoveryRateForShort()))
			dirty = true;

		if (dirtyFieldCheck(recoveryRateForLongCheckBox, fixInitiatorEditor.getFixInitiator().getRecoveryRateForLong()))
			dirty = true;

		if (dirtyFieldCheck(recoveryRateCheckBox, fixInitiatorEditor.getFixInitiator().getRecoveryRate()))
			dirty = true;

		if (dirtyFieldCheck(cashRateCheckBox, fixInitiatorEditor.getFixInitiator().getCashRate()))
			dirty = true;

		if (dirtyFieldCheck(fixingPriceCheckBox, fixInitiatorEditor.getFixInitiator().getFixingPrice()))
			dirty = true;

		if (dirtyFieldCheck(cumulativeValueAdjustmentForShortCheckBox, fixInitiatorEditor.getFixInitiator().getCumulativeValueAdjustmentForShort()))
			dirty = true;

		if (dirtyFieldCheck(dailyValueAdjustmentForShortCheckBox, fixInitiatorEditor.getFixInitiator().getDailyValueAdjustmentForShort()))
			dirty = true;

		if (dirtyFieldCheck(cumulativeValueAdjustmentForLongCheckBox, fixInitiatorEditor.getFixInitiator().getCumulativeValueAdjustmentForLong()))
			dirty = true;

		if (dirtyFieldCheck(dailyValueAdjustmentForLongCheckBox, fixInitiatorEditor.getFixInitiator().getDailyValueAdjustmentForLong()))
			dirty = true;

		if (dirtyFieldCheck(swapValueFactorCheckBox, fixInitiatorEditor.getFixInitiator().getSwapValueFactor()))
			dirty = true;

		if (dirtyFieldCheck(auctionClearingPriceCheckBox, fixInitiatorEditor.getFixInitiator().getAuctionClearingPrice()))
			dirty = true;

		if (dirtyFieldCheck(earlyPricesCheckBox, fixInitiatorEditor.getFixInitiator().getEarlyPrices()))
			dirty = true;

		if (dirtyFieldCheck(sessionLowOfferCheckBox, fixInitiatorEditor.getFixInitiator().getSessionLowOffer()))
			dirty = true;

		if (dirtyFieldCheck(sessionHighBidCheckBox, fixInitiatorEditor.getFixInitiator().getSessionHighBid()))
			dirty = true;

		if (dirtyFieldCheck(priorSettlePriceCheckBox, fixInitiatorEditor.getFixInitiator().getPriorSettlePrice()))
			dirty = true;

		if (dirtyFieldCheck(settleLowPriceCheckBox, fixInitiatorEditor.getFixInitiator().getSettleLowPrice()))
			dirty = true;

		if (dirtyFieldCheck(settleHighPriceCheckBox, fixInitiatorEditor.getFixInitiator().getSettleHighPrice()))
			dirty = true;

		if (dirtyFieldCheck(midPriceCheckBox, fixInitiatorEditor.getFixInitiator().getMidPrice()))
			dirty = true;

		if (dirtyFieldCheck(marginRateCheckBox, fixInitiatorEditor.getFixInitiator().getMarginRate()))
			dirty = true;

		if (dirtyFieldCheck(simulatedBuyPriceCheckBox, fixInitiatorEditor.getFixInitiator().getSimulatedBuyPrice()))
			dirty = true;

		if (dirtyFieldCheck(simulatedSellPriceCheckBox, fixInitiatorEditor.getFixInitiator().getSimulatedSellPrice()))
			dirty = true;

		if (dirtyFieldCheck(compositeUnderlyingCheckBox, fixInitiatorEditor.getFixInitiator().getCompositeUnderlying()))
			dirty = true;

		if (dirtyFieldCheck(openInterestCheckBox, fixInitiatorEditor.getFixInitiator().getOpenInterest()))
			dirty = true;

		if (dirtyFieldCheck(tradeVolumeCheckBox, fixInitiatorEditor.getFixInitiator().getTradeVolume()))
			dirty = true;

		if (dirtyFieldCheck(tradingSessionVWAPPriceCheckBox, fixInitiatorEditor.getFixInitiator().getTradingSessionVWAPPrice()))
			dirty = true;

		if (dirtyFieldCheck(tradingSessionLowPriceCheckBox, fixInitiatorEditor.getFixInitiator().getTradingSessionLowPrice()))
			dirty = true;

		if (dirtyFieldCheck(tradingSessionHighPriceCheckBox, fixInitiatorEditor.getFixInitiator().getTradingSessionHighPrice()))
			dirty = true;

		if (dirtyFieldCheck(settlementPriceCheckBox, fixInitiatorEditor.getFixInitiator().getSettlementPrice()))
			dirty = true;

		if (dirtyFieldCheck(closingPriceCheckBox, fixInitiatorEditor.getFixInitiator().getClosingPrice()))
			dirty = true;

		if (dirtyFieldCheck(openingPriceCheckBox, fixInitiatorEditor.getFixInitiator().getOpeningPrice()))
			dirty = true;

		if (dirtyFieldCheck(indexValueCheckBox, fixInitiatorEditor.getFixInitiator().getIndexValue()))
			dirty = true;

		if (dirtyFieldCheck(tradeCheckBox, fixInitiatorEditor.getFixInitiator().getTrade()))
			dirty = true;

		if (dirtyFieldCheck(tradeCheckBox, fixInitiatorEditor.getFixInitiator().getTrade()))
			dirty = true;

		if (requestTypeComboBox.getSelectedIndex() != fixInitiatorEditor.getFixInitiator().getRequestType())
			dirty = true;

		boolean enabled = fixInitiatorEditor.getBasisClientConnector().getFUser().canWrite(fixInitiatorEditor.getAbstractBusinessObject())
				&& fixInitiatorEditor.getFixInitiator().getStartLevel() == 0;

		if (enabled) {
			requestTypeComboBox.setBackground(new Color(255, 243, 204));
			requestTypeComboBox.setEnabled(true);
		}
		else {
			requestTypeComboBox.setBackground(new Color(204, 216, 255));
			requestTypeComboBox.setEnabled(false);
		}

		shutdownCheckBox.setEnabled(requestTypeComboBox.getSelectedIndex() == 1);

		enabled = enabled && requestTypeComboBox.getSelectedIndex() != 0;

		bidCkeckBox.setEnabled(enabled);

		offerCheckBox.setEnabled(enabled);

		recoveryRateForShortCheckBox.setEnabled(enabled);

		recoveryRateForLongCheckBox.setEnabled(enabled);

		recoveryRateCheckBox.setEnabled(enabled);

		cashRateCheckBox.setEnabled(enabled);

		fixingPriceCheckBox.setEnabled(enabled);

		cumulativeValueAdjustmentForShortCheckBox.setEnabled(enabled);

		dailyValueAdjustmentForShortCheckBox.setEnabled(enabled);

		cumulativeValueAdjustmentForLongCheckBox.setEnabled(enabled);

		dailyValueAdjustmentForLongCheckBox.setEnabled(enabled);

		swapValueFactorCheckBox.setEnabled(enabled);

		auctionClearingPriceCheckBox.setEnabled(enabled);

		earlyPricesCheckBox.setEnabled(enabled);

		sessionLowOfferCheckBox.setEnabled(enabled);

		sessionHighBidCheckBox.setEnabled(enabled);

		priorSettlePriceCheckBox.setEnabled(enabled);

		settleLowPriceCheckBox.setEnabled(enabled);

		settleHighPriceCheckBox.setEnabled(enabled);

		midPriceCheckBox.setEnabled(enabled);

		marginRateCheckBox.setEnabled(enabled);

		simulatedBuyPriceCheckBox.setEnabled(enabled);

		simulatedSellPriceCheckBox.setEnabled(enabled);

		compositeUnderlyingCheckBox.setEnabled(enabled);

		openInterestCheckBox.setEnabled(enabled);

		tradeVolumeCheckBox.setEnabled(enabled);

		tradingSessionVWAPPriceCheckBox.setEnabled(enabled);

		tradingSessionLowPriceCheckBox.setEnabled(enabled);

		tradingSessionHighPriceCheckBox.setEnabled(enabled);

		settlementPriceCheckBox.setEnabled(enabled);

		closingPriceCheckBox.setEnabled(enabled);

		openingPriceCheckBox.setEnabled(enabled);

		indexValueCheckBox.setEnabled(enabled);

		tradeCheckBox.setEnabled(enabled);

		consistent = false;

		if (requestTypeComboBox.getSelectedIndex() == 0)
			consistent = true;
		else {
			if (bidCkeckBox.isSelected())
				consistent = true;

			if (offerCheckBox.isSelected())
				consistent = true;

			if (recoveryRateForShortCheckBox.isSelected())
				consistent = true;

			if (recoveryRateForLongCheckBox.isSelected())
				consistent = true;

			if (recoveryRateCheckBox.isSelected())
				consistent = true;

			if (cashRateCheckBox.isSelected())
				consistent = true;

			if (fixingPriceCheckBox.isSelected())
				consistent = true;

			if (cumulativeValueAdjustmentForShortCheckBox.isSelected())
				consistent = true;

			if (dailyValueAdjustmentForShortCheckBox.isSelected())
				consistent = true;

			if (cumulativeValueAdjustmentForLongCheckBox.isSelected())
				consistent = true;

			if (dailyValueAdjustmentForLongCheckBox.isSelected())
				consistent = true;

			if (swapValueFactorCheckBox.isSelected())
				consistent = true;

			if (auctionClearingPriceCheckBox.isSelected())
				consistent = true;

			if (earlyPricesCheckBox.isSelected())
				consistent = true;

			if (sessionLowOfferCheckBox.isSelected())
				consistent = true;

			if (sessionHighBidCheckBox.isSelected())
				consistent = true;

			if (priorSettlePriceCheckBox.isSelected())
				consistent = true;

			if (settleLowPriceCheckBox.isSelected())
				consistent = true;

			if (settleHighPriceCheckBox.isSelected())
				consistent = true;

			if (midPriceCheckBox.isSelected())
				consistent = true;

			if (marginRateCheckBox.isSelected())
				consistent = true;

			if (simulatedBuyPriceCheckBox.isSelected())
				consistent = true;

			if (simulatedSellPriceCheckBox.isSelected())
				consistent = true;

			if (compositeUnderlyingCheckBox.isSelected())
				consistent = true;

			if (openInterestCheckBox.isSelected())
				consistent = true;

			if (tradeVolumeCheckBox.isSelected())
				consistent = true;

			if (tradingSessionVWAPPriceCheckBox.isSelected())
				consistent = true;

			if (tradingSessionLowPriceCheckBox.isSelected())
				consistent = true;

			if (tradingSessionHighPriceCheckBox.isSelected())
				consistent = true;

			if (settlementPriceCheckBox.isSelected())
				consistent = true;

			if (closingPriceCheckBox.isSelected())
				consistent = true;

			if (openingPriceCheckBox.isSelected())
				consistent = true;

			if (indexValueCheckBox.isSelected())
				consistent = true;

			if (tradeCheckBox.isSelected())
				consistent = true;

		}

		if (!consistent) {
			requestTypeWarningLabel.setIcon(fixInitiatorEditor.getBugIcon());
			requestTypeWarningLabel.setToolTipText("No entry type selected.");
		}
		else {
			requestTypeWarningLabel.setIcon(null);
			requestTypeWarningLabel.setToolTipText(null);
		}

		fixInitiatorEditor.checkDirty();
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
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return dirty;
	}

	/**
	 * Save.
	 */
	public void save() {

		fixInitiatorEditor.getFixInitiator().setBid(bidCkeckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setOffer(offerCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setRecoveryRateForShort(recoveryRateForShortCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setRecoveryRateForLong(recoveryRateForLongCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setRecoveryRate(recoveryRateCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setCashRate(cashRateCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setFixingPrice(fixingPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setCumulativeValueAdjustmentForShort(cumulativeValueAdjustmentForShortCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setDailyValueAdjustmentForShort(dailyValueAdjustmentForShortCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setCumulativeValueAdjustmentForLong(cumulativeValueAdjustmentForLongCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setDailyValueAdjustmentForLong(dailyValueAdjustmentForLongCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSwapValueFactor(swapValueFactorCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setAuctionClearingPrice(auctionClearingPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setEarlyPrices(earlyPricesCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSessionLowOffer(sessionLowOfferCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSessionHighBid(sessionHighBidCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setPriorSettlePrice(priorSettlePriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSettleLowPrice(settleLowPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSettleHighPrice(settleHighPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setMidPrice(midPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setMarginRate(marginRateCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSimulatedBuyPrice(simulatedBuyPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSimulatedSellPrice(simulatedSellPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setCompositeUnderlying(compositeUnderlyingCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setOpenInterest(openInterestCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setTradeVolume(tradeVolumeCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setTradingSessionVWAPPrice(tradingSessionVWAPPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setTradingSessionLowPrice(tradingSessionLowPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setTradingSessionHighPrice(tradingSessionHighPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setSettlementPrice(settlementPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setClosingPrice(closingPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setOpeningPrice(openingPriceCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setIndexValue(indexValueCheckBox.isSelected());

		fixInitiatorEditor.getFixInitiator().setTrade(tradeCheckBox.isSelected());
		
		fixInitiatorEditor.getFixInitiator().setShutdownAfterSnapshot(shutdownCheckBox.isSelected());
		
		fixInitiatorEditor.getFixInitiator().setRequestType(requestTypeComboBox.getSelectedIndex());

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		bidCkeckBox.setSelected(fixInitiatorEditor.getFixInitiator().getBid());

		offerCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getOffer());

		recoveryRateForShortCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getRecoveryRateForShort());

		recoveryRateForLongCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getRecoveryRateForLong());

		recoveryRateCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getRecoveryRate());

		cashRateCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getCashRate());

		fixingPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getFixingPrice());

		cumulativeValueAdjustmentForShortCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getCumulativeValueAdjustmentForShort());

		dailyValueAdjustmentForShortCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getDailyValueAdjustmentForShort());

		cumulativeValueAdjustmentForLongCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getCumulativeValueAdjustmentForLong());

		dailyValueAdjustmentForLongCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getDailyValueAdjustmentForLong());

		swapValueFactorCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSwapValueFactor());

		auctionClearingPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getAuctionClearingPrice());

		earlyPricesCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getEarlyPrices());

		sessionLowOfferCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSessionLowOffer());

		sessionHighBidCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSessionHighBid());

		priorSettlePriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getPriorSettlePrice());

		settleLowPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSettleLowPrice());

		settleHighPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSettleHighPrice());

		midPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getMidPrice());

		marginRateCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getMarginRate());

		simulatedBuyPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSimulatedBuyPrice());

		simulatedSellPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSimulatedSellPrice());

		compositeUnderlyingCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getCompositeUnderlying());

		openInterestCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getOpenInterest());

		tradeVolumeCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getTradeVolume());

		tradingSessionVWAPPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getTradingSessionVWAPPrice());

		tradingSessionLowPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getTradingSessionLowPrice());

		tradingSessionHighPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getTradingSessionHighPrice());

		settlementPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getSettlementPrice());

		closingPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getClosingPrice());

		openingPriceCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getOpeningPrice());

		indexValueCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getIndexValue());

		tradeCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getTrade());

		shutdownCheckBox.setSelected(fixInitiatorEditor.getFixInitiator().getShutdownAfterSnapshot());

		requestTypeComboBox.setSelectedIndex(fixInitiatorEditor.getFixInitiator().getRequestType());

		checkConsistency();
	}
}
