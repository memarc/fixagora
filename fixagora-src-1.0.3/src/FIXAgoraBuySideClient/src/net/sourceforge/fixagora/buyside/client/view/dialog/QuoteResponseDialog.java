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
package net.sourceforge.fixagora.buyside.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.basis.client.view.component.DaySpinner;
import net.sourceforge.fixagora.basis.client.view.component.NumberSpinner;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.model.editor.BuySideBookEditorMonitorTableModel;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry.OrderStatus;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestRequest;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideInitiator;
import net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod;

/**
 * The Class QuoteResponseDialog.
 */
public class QuoteResponseDialog extends JDialog implements CellEditor {

	/** The partials. */
	public static boolean partials = true;

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private boolean cancelled = false;

	private JButton okButton = null;

	private JTextField securityTextField = null;

	private JButton btnNewButton = null;

	private JComboBox marketComboBox = null;

	private JTextField counterpartyTextField;

	private JButton btnNewButton2;

	private final ImageIcon redDownIcon = new ImageIcon(
			QuoteResponseDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowred.png"));

	private final ImageIcon greenUpIcon = new ImageIcon(
			QuoteResponseDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowgreen.png"));

	private final ImageIcon blueUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrow.png"));

	private final ImageIcon blueDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrow.png"));

	private final ImageIcon warning = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	private final ImageIcon cancel = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/cancel.png"));

	private NumberSpinner priceSpinner;

	private NumberSpinner quantitySpinner;

	private DaySpinner settlementSpinner;

	private JLabel limitLabel = null;

	/** The date reset. */
	protected boolean dateReset = true;

	private JLabel dateLabel = null;

	private JLabel securityWarningLabel = null;

	private JLabel marketWarningLabel = null;

	private JLabel counterpartyWarningLabel = null;

	private JLabel limitWarningLabel = null;

	private JLabel sizeWarningLabel = null;

	private JLabel dateWarningLabel = null;

	private BuySideQuoteRequestEntry buySideQuoteRequestEntry = null;

	private JButton cancelButton;

	private GradientLabel sideLabel;

	private JCheckBox partialsAcceptedCheckBox;

	private JButton discardButton;

	private JLabel limitCalcLabel;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0000");

	private Integer basis;

	private JButton cancelAllButton;

	private JPanel panel_1;

	private FSecurity security;

	private Timer timer;

	private CalcMethod calcMethod;

	private boolean fractional = false;

	/**
	 * Instantiates a new quote response dialog.
	 *
	 * @param buySideQuoteRequestEntry the buy side quote request entry
	 * @param buySideBookEditor the buy side book editor
	 */
	public QuoteResponseDialog(final BuySideQuoteRequestEntry buySideQuoteRequestEntry, final BuySideBookEditor buySideBookEditor) {

		super(buySideBookEditor.getMainPanel().getJFrame());

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		this.buySideQuoteRequestEntry = buySideQuoteRequestEntry;

		setBounds(100, 100, 531, 413);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(QuoteResponseDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(false);
		setAlwaysOnTop(true);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				dateReset = true;
				checkConsistency();
			}
		};

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

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		sideLabel = new GradientLabel((new Color(0, 153, 0)).darker(), new Color(0, 153, 0));
		sideLabel.setForeground(Color.WHITE);
		sideLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		sideLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sideLabel.setPreferredSize(new Dimension(0, 30));
		sideLabel.setText("INQUIRY  ASK (YOU BUY)");

		if (buySideQuoteRequestEntry.getSide() != Side.ASK) {
			sideLabel.setBright(new Color(204, 0, 0));
			sideLabel.setDark((new Color(204, 0, 0)).darker());
			sideLabel.setText("INQUIRY  BID (YOU SELL)");
		}

		if (buySideQuoteRequestEntry.getOrderStatus() == null)
			sideLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (buySideQuoteRequestEntry.getOrderStatus() == null)
						if (buySideQuoteRequestEntry.getSide() == Side.ASK) {
							sideLabel.setBright(new Color(204, 0, 0));
							sideLabel.setDark((new Color(204, 0, 0)).darker());
							sideLabel.setText("INQUIRY BID (YOU SELL)");
							buySideQuoteRequestEntry.setSide(Side.BID);
						}
						else {
							sideLabel.setBright(new Color(0, 153, 0));
							sideLabel.setDark((new Color(0, 153, 0)).darker());
							sideLabel.setText("INQUIRY ASK (YOU BUY)");
							buySideQuoteRequestEntry.setSide(Side.ASK);

						}
				}

			});

		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.SOUTH;
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.gridwidth = 6;
		gbc_lblNewLabel.insets = new Insets(10, 25, 25, 25);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(sideLabel, gbc_lblNewLabel);

		JLabel legSecurityLabel = new JLabel("Security");
		legSecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(0, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 1;
		contentPanel.add(legSecurityLabel, gbc_legSecurityLabel);

		securityWarningLabel = new JLabel("");
		securityWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_timeWarningLabel = new GridBagConstraints();
		gbc_timeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_timeWarningLabel.gridx = 1;
		gbc_timeWarningLabel.gridy = 1;
		contentPanel.add(securityWarningLabel, gbc_timeWarningLabel);

		securityTextField = new JTextField();
		securityTextField.setPreferredSize(new Dimension(200, 25));
		securityTextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		securityTextField.setBackground(new Color(255, 243, 204));
		securityTextField.setColumns(10);
		
		if(buySideBookEditor.getBuySideBook().getFractionalDisplay()!=null)
			fractional = buySideBookEditor.getBuySideBook().getFractionalDisplay();


		AssignedBuySideBookSecurity assignedBuySideBookSecurity = null;
		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity2 : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities())
			if (assignedBuySideBookSecurity2.getSecurity().getId() == buySideQuoteRequestEntry.getSecurity())
				assignedBuySideBookSecurity = assignedBuySideBookSecurity2;

		calcMethod = buySideBookEditor.getBuySideBook().getCalcMethod();

		if (assignedBuySideBookSecurity != null && assignedBuySideBookSecurity.getSecurity() != null) {
			security = buySideBookEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(assignedBuySideBookSecurity.getSecurity().getId());

			if (assignedBuySideBookSecurity.getCalcMethod() != null)
				calcMethod = assignedBuySideBookSecurity.getCalcMethod();
			
			if (assignedBuySideBookSecurity.getFractionalDisplay() != null)
				fractional = assignedBuySideBookSecurity.getFractionalDisplay();

			securityTextField.setText(assignedBuySideBookSecurity.getSecurity().getName());
		}

		GridBagConstraints gbc_securityTextField = new GridBagConstraints();
		gbc_securityTextField.gridwidth = 3;
		gbc_securityTextField.anchor = GridBagConstraints.NORTH;
		gbc_securityTextField.insets = new Insets(0, 0, 5, 5);
		gbc_securityTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityTextField.gridx = 2;
		gbc_securityTextField.gridy = 1;
		contentPanel.add(securityTextField, gbc_securityTextField);

		if (buySideQuoteRequestEntry.getOrderStatus() != null) {
			securityTextField.setEditable(false);
			securityTextField.setBackground(new Color(204, 216, 255));
		}

		btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnNewButton.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 25);
		gbc_btnNewButton.gridx = 5;
		gbc_btnNewButton.gridy = 1;
		contentPanel.add(btnNewButton, gbc_btnNewButton);

		securityTextField.setEditable(false);
		btnNewButton.setEnabled(false);

		securityTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				dateReset = true;
				checkConsistency();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				dateReset = true;
				checkConsistency();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				dateReset = true;
				checkConsistency();
			}
		});

		JLabel bankCalendarLabel = new JLabel("Market");
		bankCalendarLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_bankCalendarLabel = new GridBagConstraints();
		gbc_bankCalendarLabel.anchor = GridBagConstraints.WEST;
		gbc_bankCalendarLabel.insets = new Insets(0, 25, 5, 5);
		gbc_bankCalendarLabel.gridx = 0;
		gbc_bankCalendarLabel.gridy = 2;
		contentPanel.add(bankCalendarLabel, gbc_bankCalendarLabel);

		marketWarningLabel = new JLabel("");
		marketWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_marketWarningLabel = new GridBagConstraints();
		gbc_marketWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_marketWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_marketWarningLabel.gridx = 1;
		gbc_marketWarningLabel.gridy = 2;
		contentPanel.add(marketWarningLabel, gbc_marketWarningLabel);

		List<String> markets = new ArrayList<String>();
		for (AssignedBuySideInitiator assignedBuySideFIXInitiator : buySideBookEditor.getBuySideBook().getAssignedBuySideFIXInitiators()) {
			AbstractInitiator fixInitiator = assignedBuySideFIXInitiator.getInitiator();
			if (fixInitiator.getRoute() != null) {
				if (!markets.contains(fixInitiator.getRoute().getMarketName()))
					markets.add(fixInitiator.getRoute().getMarketName());
			}
			else {
				if (!markets.contains(fixInitiator.getMarketName()))
					markets.add(fixInitiator.getMarketName());
			}
		}

		Collections.sort(markets);
		marketComboBox = new JComboBox(markets.toArray());
		marketComboBox.setMinimumSize(new Dimension(32, 25));
		marketComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		marketComboBox.setPreferredSize(new Dimension(32, 25));
		marketComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_bankCalendarComboBox = new GridBagConstraints();
		gbc_bankCalendarComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_bankCalendarComboBox.gridwidth = 3;
		gbc_bankCalendarComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_bankCalendarComboBox.gridx = 2;
		gbc_bankCalendarComboBox.gridy = 2;
		contentPanel.add(marketComboBox, gbc_bankCalendarComboBox);

		marketComboBox.addActionListener(actionListener);

		if (buySideQuoteRequestEntry.getOrderStatus() != null) {
			marketComboBox.setBackground(new Color(204, 216, 255));
			marketComboBox.setEnabled(false);
		}

		JLabel counterpartyLabel = new JLabel("Counterparty");
		counterpartyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterpartyLabel = new GridBagConstraints();
		gbc_counterpartyLabel.anchor = GridBagConstraints.WEST;
		gbc_counterpartyLabel.insets = new Insets(0, 25, 5, 5);
		gbc_counterpartyLabel.gridx = 0;
		gbc_counterpartyLabel.gridy = 3;
		contentPanel.add(counterpartyLabel, gbc_counterpartyLabel);

		counterpartyWarningLabel = new JLabel("");
		counterpartyWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_counterpartyWarningLabel = new GridBagConstraints();
		gbc_counterpartyWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterpartyWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_counterpartyWarningLabel.gridx = 1;
		gbc_counterpartyWarningLabel.gridy = 3;
		contentPanel.add(counterpartyWarningLabel, gbc_counterpartyWarningLabel);

		counterpartyTextField = new JTextField();
		counterpartyTextField.setPreferredSize(new Dimension(200, 25));
		counterpartyTextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		counterpartyTextField.setBackground(new Color(255, 243, 204));
		counterpartyTextField.setColumns(10);

		Counterparty counterparty = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog()
				.getCounterpartyForId(buySideQuoteRequestEntry.getCounterparty());
		if (counterparty != null)
			counterpartyTextField.setText(counterparty.getName());

		counterpartyTextField.getDocument().addDocumentListener(documentListener);

		GridBagConstraints gbc_counterpartyTextField = new GridBagConstraints();
		gbc_counterpartyTextField.gridwidth = 3;
		gbc_counterpartyTextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterpartyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterpartyTextField.gridx = 2;
		gbc_counterpartyTextField.gridy = 3;
		contentPanel.add(counterpartyTextField, gbc_counterpartyTextField);

		btnNewButton2 = new JButton("...");
		btnNewButton2.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnNewButton2.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_btnNewButton2 = new GridBagConstraints();
		gbc_btnNewButton2.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton2.insets = new Insets(0, 0, 5, 25);
		gbc_btnNewButton2.gridx = 5;
		gbc_btnNewButton2.gridy = 3;
		contentPanel.add(btnNewButton2, gbc_btnNewButton2);

		counterpartyTextField.setEditable(false);
		counterpartyTextField.setBackground(new Color(204, 216, 255));
		btnNewButton2.setEnabled(false);

		limitLabel = new JLabel("Limit (Price)");
		limitLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_limitLabel = new GridBagConstraints();
		gbc_limitLabel.anchor = GridBagConstraints.WEST;
		gbc_limitLabel.insets = new Insets(0, 25, 5, 5);
		gbc_limitLabel.gridx = 0;
		gbc_limitLabel.gridy = 4;
		contentPanel.add(limitLabel, gbc_limitLabel);

		limitWarningLabel = new JLabel("");
		limitWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_limitWarningLabel = new GridBagConstraints();
		gbc_limitWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_limitWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_limitWarningLabel.gridx = 1;
		gbc_limitWarningLabel.gridy = 4;
		contentPanel.add(limitWarningLabel, gbc_limitWarningLabel);

		priceSpinner = new NumberSpinner(this, greenUpIcon, redDownIcon, new Color(255, 243, 204));
		priceSpinner.setMinimumSize(new Dimension(32, 25));
		priceSpinner.setPreferredSize(new Dimension(32, 25));
		priceSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));

		GridBagConstraints gbc_priceSpinner = new GridBagConstraints();
		gbc_priceSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_priceSpinner.gridwidth = 3;
		gbc_priceSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceSpinner.gridx = 2;
		gbc_priceSpinner.gridy = 4;
		contentPanel.add(priceSpinner, gbc_priceSpinner);

		if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null) {
			priceSpinner.setIncrement(security.getSecurityDetails().getMinPriceIncrement());
			priceSpinner.setTickSize(security.getSecurityDetails().getMinPriceIncrement());
			priceSpinner.setDecimalPlaces(getDecimalPlaces(security.getSecurityDetails().getMinPriceIncrement()));

		}
		else {
			priceSpinner.setIncrement(0.001);
			priceSpinner.setTickSize(0.001);
			priceSpinner.setDecimalPlaces(3);
		}
		
		priceSpinner.setFractional(fractional);

		if (buySideQuoteRequestEntry.getLimit() == null) {
			priceSpinner.setValue(0);
			priceSpinner.setEnabled(false);
		}
		else
			priceSpinner.setValue(buySideQuoteRequestEntry.getLimit());

		priceSpinner.addDocumentListener(documentListener);

		JLabel partialsAcceptedLabel = new JLabel("Partials Accepted");
		partialsAcceptedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partialsAcceptedLabel = new GridBagConstraints();
		gbc_partialsAcceptedLabel.anchor = GridBagConstraints.WEST;
		gbc_partialsAcceptedLabel.insets = new Insets(0, 25, 5, 5);
		gbc_partialsAcceptedLabel.gridx = 0;
		gbc_partialsAcceptedLabel.gridy = 5;
		contentPanel.add(partialsAcceptedLabel, gbc_partialsAcceptedLabel);

		partialsAcceptedCheckBox = new JCheckBox();
		partialsAcceptedCheckBox.setOpaque(false);
		partialsAcceptedCheckBox.setMinimumSize(new Dimension(32, 25));
		partialsAcceptedCheckBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_partialsAcceptedCheckBox = new GridBagConstraints();
		gbc_partialsAcceptedCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_partialsAcceptedCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_partialsAcceptedCheckBox.gridx = 2;
		gbc_partialsAcceptedCheckBox.gridy = 5;
		contentPanel.add(partialsAcceptedCheckBox, gbc_partialsAcceptedCheckBox);

		limitCalcLabel = new JLabel("");
		limitCalcLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_calcLabel = new GridBagConstraints();
		gbc_calcLabel.gridwidth = 2;
		gbc_calcLabel.anchor = GridBagConstraints.EAST;
		gbc_calcLabel.insets = new Insets(0, 0, 5, 39);
		gbc_calcLabel.gridx = 3;
		gbc_calcLabel.gridy = 5;
		contentPanel.add(limitCalcLabel, gbc_calcLabel);

		JLabel sizeLabel = new JLabel("Size");
		sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sizeLabel = new GridBagConstraints();
		gbc_sizeLabel.anchor = GridBagConstraints.WEST;
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

		quantitySpinner = new NumberSpinner(this, blueUpIcon, blueDownIcon, new Color(204, 216, 255));
		quantitySpinner.setMinimumSize(new Dimension(32, 25));
		quantitySpinner.setPreferredSize(new Dimension(32, 25));
		quantitySpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		quantitySpinner.setThousandsSeparator(true);
		quantitySpinner.setValue(buySideQuoteRequestEntry.getOrderQuantity());
		quantitySpinner.setEnabled(false);

		GridBagConstraints gbc_sizeSpinner = new GridBagConstraints();
		gbc_sizeSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_sizeSpinner.gridwidth = 3;
		gbc_sizeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeSpinner.gridx = 2;
		gbc_sizeSpinner.gridy = 6;
		contentPanel.add(quantitySpinner, gbc_sizeSpinner);

		if (buySideQuoteRequestEntry.getOrderStatus() != null) {
			partialsAcceptedCheckBox.setEnabled(false);
			partialsAcceptedCheckBox.setSelected(buySideQuoteRequestEntry.getMinQuantity() != buySideQuoteRequestEntry.getOrderQuantity());
		}
		else {
			partialsAcceptedCheckBox.setSelected(partials);
			partialsAcceptedCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					partials = partialsAcceptedCheckBox.isSelected();

				}
			});
		}

		dateLabel = new JLabel("Date (Settlement)");
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		int width = getFontMetrics(new Font("Dialog", Font.PLAIN, 12)).stringWidth("Date (Settlement)");
		dateLabel.setMinimumSize(new Dimension(width, 25));
		dateLabel.setPreferredSize(new Dimension(width, 25));
		GridBagConstraints gbc_dateLabel = new GridBagConstraints();
		gbc_dateLabel.anchor = GridBagConstraints.WEST;
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

		settlementSpinner = new DaySpinner(this, new Color(204, 216, 255));
		settlementSpinner.setMinimumSize(new Dimension(32, 25));
		settlementSpinner.setPreferredSize(new Dimension(32, 25));
		settlementSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		settlementSpinner.setValue(new Date(buySideQuoteRequestEntry.getSettlementDate()));
		settlementSpinner.setEnabled(false);

		GridBagConstraints gbc_dateSpinner = new GridBagConstraints();
		gbc_dateSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_dateSpinner.gridwidth = 3;
		gbc_dateSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateSpinner.gridx = 2;
		gbc_dateSpinner.gridy = 7;
		contentPanel.add(settlementSpinner, gbc_dateSpinner);

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 6;
		gbc_panel.insets = new Insets(15, 25, 0, 25);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 8;
		contentPanel.add(panel, gbc_panel);

		cancelButton = new JButton("Pass");
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setIcon(cancel);
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				okButton.setEnabled(false);
				cancelButton.setEnabled(false);
				discardButton.setEnabled(false);
				setVisible(false);

				Thread thread = new Thread() {

					public void run() {

						buySideBookEditor.removeQuoteResponseDialog(QuoteResponseDialog.this);

						buySideQuoteRequestEntry.setOrderStatus(OrderStatus.PASS_PENDING);

						List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();

						buySideQuoteRequestEntries.add(buySideQuoteRequestEntry);

						buySideBookEditor.getBasisClientConnector().sendRequest(
								new BuySideQuoteRequestRequest(buySideQuoteRequestEntries, RequestIDManager.getInstance().getID()));
					}
				};

				thread.start();

			}
		});

		panel.add(cancelButton);

		cancelAllButton = new JButton("Pass all");
		cancelAllButton.setPreferredSize(new Dimension(100, 25));
		cancelAllButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelAllButton.setIcon(cancel);

		cancelAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				okButton.setEnabled(false);
				cancelButton.setEnabled(false);
				discardButton.setEnabled(false);
				setVisible(false);

				Thread thread = new Thread() {

					public void run() {

						buySideBookEditor.removeQuoteResponseDialog(QuoteResponseDialog.this);

						buySideQuoteRequestEntry.setOrderStatus(OrderStatus.PASS_ALL_PENDING);

						List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();

						buySideQuoteRequestEntries.add(buySideQuoteRequestEntry);

						buySideBookEditor.getBasisClientConnector().sendRequest(
								new BuySideQuoteRequestRequest(buySideQuoteRequestEntries, RequestIDManager.getInstance().getID()));
					}
				};

				thread.start();

			}
		});

		panel.add(cancelAllButton);

		panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(30, 25));
		panel_1.setMinimumSize(new Dimension(30, 25));
		panel_1.setOpaque(false);
		panel.add(panel_1);
		// }

		okButton = new JButton();
		panel.add(okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);

		setTitle("Inquiry");
		if (buySideQuoteRequestEntry.getSide() == Side.BID)
			okButton.setText("Hit");
		else
			okButton.setText("Lift");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				okButton.setEnabled(false);
				discardButton.setEnabled(false);
				if (cancelButton != null)
					cancelButton.setEnabled(false);
				setVisible(false);

				Thread thread = new Thread() {

					public void run() {

						buySideBookEditor.removeQuoteResponseDialog(QuoteResponseDialog.this);

						Double value = priceSpinner.getValue();

						if (QuoteResponseDialog.this.buySideQuoteRequestEntry.getLimit() != null
								&& value.doubleValue() != QuoteResponseDialog.this.buySideQuoteRequestEntry.getLimit().doubleValue()) {
							QuoteResponseDialog.this.buySideQuoteRequestEntry.setLimit(value);
							QuoteResponseDialog.this.buySideQuoteRequestEntry.setOrderStatus(OrderStatus.COUNTER_PENDING);
						}
						else
							QuoteResponseDialog.this.buySideQuoteRequestEntry.setOrderStatus(OrderStatus.HIT_LIFT_PENDING);

						List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();

						buySideQuoteRequestEntries.add(QuoteResponseDialog.this.buySideQuoteRequestEntry);

						buySideBookEditor.getBasisClientConnector().sendRequest(
								new BuySideQuoteRequestRequest(buySideQuoteRequestEntries, RequestIDManager.getInstance().getID()));

					}
				};

				thread.start();
			}
		});

		getRootPane().setDefaultButton(okButton);

		discardButton = new JButton("Discard");
		panel.add(discardButton);
		discardButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		discardButton.setPreferredSize(new Dimension(100, 25));
		discardButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		discardButton.setActionCommand("Cancel");
		discardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				buySideBookEditor.removeQuoteResponseDialog(QuoteResponseDialog.this);

				cancelled = true;
				setVisible(false);
			}
		});

		double tickSize = 0.001;

		if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null)
			tickSize = security.getSecurityDetails().getMinPriceIncrement();
		priceSpinner.setIncrement(tickSize);
		priceSpinner.setTickSize(tickSize);
		if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
				&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
			priceSpinner.setMinValue(null);
			limitLabel.setText("Limit (Yield)");
		}
		else {
			priceSpinner.setMinValue(0d);
			limitLabel.setText("Limit (Price)");
		}

		basis = buySideBookEditor.getBuySideBook().getDaycountConvention();
		if (security != null)
			for (AssignedBuySideBookSecurity assignedBuySideBookSecurity2 : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities())
				if (assignedBuySideBookSecurity2.getSecurity().getId() == security.getId()) {
					if (assignedBuySideBookSecurity2.getDaycountConvention() != null)
						basis = assignedBuySideBookSecurity2.getDaycountConvention();
				}

		buySideBookEditor.addQuoteResponseDialog(this);

		timer = new Timer(200, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();

				if (QuoteResponseDialog.this.buySideQuoteRequestEntry.isDone())
					timer.stop();

			}
		});

		timer.start();

		checkConsistency();
	}

	private synchronized void checkConsistency() {

		Double value = null;

		Date settlementDate = null;

		String text = "ASK (YOU BUY) - ";

		if (buySideQuoteRequestEntry.getSide() != Side.ASK) {
			text = "BID (YOU SELL) - ";
		}

		switch (buySideQuoteRequestEntry.getOrderStatus()) {
			case COUNTER:
				sideLabel.setText(text + "Counter");
				break;
			case COUNTER_PENDING:
				sideLabel.setText(text + "Counter (Pending)");
				break;
			case FILLED:
				if (buySideQuoteRequestEntry.getLeaveQuantity() > 0) {
					sideLabel.setText(text + "Done (Partially Filled)");
				}
				else {
					sideLabel.setText(text + "Done (Filled)");
				}
				break;
			case HIT_LIFT:
				if (buySideQuoteRequestEntry.getSide() == Side.BID)
					sideLabel.setText(text + "Hit");
				else
					sideLabel.setText(text + "Lift");
				break;
			case HIT_LIFT_PENDING:
				if (buySideQuoteRequestEntry.getSide() == Side.BID)
					sideLabel.setText(text + "Hit (Pending)");
				else
					sideLabel.setText(text + "Lift (Pending)");
				break;
			case NEW:
				sideLabel.setText(text + "New Inquiry "
						+ BuySideBookEditorMonitorTableModel.getTimeLabel(buySideQuoteRequestEntry.getExpireDate() - System.currentTimeMillis()));
				break;
			case NEW_PENDING:
				sideLabel.setText(text + "New Inquiry (Pending)");
				break;
			case REJECTED:
				sideLabel.setText(text + "Rejected");
				break;
			case PASS:
				sideLabel.setText(text + "Pass");
				break;
			case PASS_ALL_PENDING:
			case PASS_PENDING:
				sideLabel.setText(text + "Pass (Pending)");
				break;
			case QUOTED:
				Long subject = buySideQuoteRequestEntry.getSubjectDate();
				if (subject != null && subject > System.currentTimeMillis()) {
					sideLabel.setText(text + "Firm Quote " + BuySideBookEditorMonitorTableModel.getTimeLabel(subject - System.currentTimeMillis()));
				}
				else {
					sideLabel.setText(text + "Subject Quote "
							+ BuySideBookEditorMonitorTableModel.getTimeLabel(buySideQuoteRequestEntry.getExpireDate() - System.currentTimeMillis()));
				}
				break;
			case COVER:
				sideLabel.setText(text + "Cover");
				break;
			case COVER_PENDING:
				sideLabel.setText(text + "Cover (Pending)");
				break;
			case DONE_AWAY:
				sideLabel.setText(text + "Done Away");
				break;
			case DONE_AWAY_PENDING:
				sideLabel.setText(text + "Done Away (Pending)");
				break;
			case EXPIRED:
				sideLabel.setText(text + "Expired");
				break;
			case EXPIRED_PENDING:
				sideLabel.setText(text + "Expired (Pending)");
				break;
		}

		if (buySideQuoteRequestEntry.getLimit() == null) {
			priceSpinner.setEnabled(false);
		}
		else {
			priceSpinner.setEnabled(true);
		}

		if (!priceSpinner.isValidNumber()) {
			limitWarningLabel.setIcon(warning);
			limitWarningLabel.setToolTipText("Invalid price");
			value = null;
		}
		else {
			limitWarningLabel.setIcon(null);
			limitWarningLabel.setToolTipText(null);
			value = priceSpinner.getValue();
		}

		if (buySideQuoteRequestEntry.getSubjectDate() != null && buySideQuoteRequestEntry.getSubjectDate() > System.currentTimeMillis()) {

			if (value != null && buySideQuoteRequestEntry.getLimit() != null && value.doubleValue() != buySideQuoteRequestEntry.getLimit().doubleValue())
				okButton.setText("Counter");
			else if (buySideQuoteRequestEntry.getSide() == Side.BID)
				okButton.setText("Hit");
			else
				okButton.setText("Lift");
		}
		else {
			if (value != null && buySideQuoteRequestEntry.getLimit() != null && value.doubleValue() != buySideQuoteRequestEntry.getLimit().doubleValue())
				okButton.setText("Counter");
			else
				okButton.setText("Order");
		}

		if (buySideQuoteRequestEntry.getQuoteId() == null || buySideQuoteRequestEntry.isDone()) {
			okButton.setEnabled(false);
			priceSpinner.setBackground(new Color(204, 216, 255));
			priceSpinner.setEnabled(false);

		}
		else {
			okButton.setEnabled(true);
			priceSpinner.setBackground(new Color(255, 243, 204));
			priceSpinner.setEnabled(true);
		}
		
		settlementDate = new Date(buySideQuoteRequestEntry.getSettlementDate());

		if (security != null && value != null && settlementDate != null && basis != null) {
			if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
				Double price = null;
				switch (calcMethod) {
					case DEFAULT:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DEFAULT);
						break;
					case DISC:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DISC);
						break;
					case MAT:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.MAT);
						break;
					case NONE:
						price = null;
						break;
					case ODDF:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDF);
						break;
					case ODDL:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDL);
						break;
					case TBILL:
						price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.TBILL);
						break;

				}
				if (price != null)
					limitCalcLabel.setText("Price: " + decimalFormat.format(price));
				else
					limitCalcLabel.setText(null);
			}
			else {
				Double yield = null;
				switch (calcMethod) {
					case DEFAULT:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DEFAULT);
						break;
					case DISC:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DISC);
						break;
					case MAT:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.MAT);
						break;
					case NONE:
						yield = null;
						break;
					case ODDF:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDF);
						break;
					case ODDL:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDL);
						break;
					case TBILL:
						yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
								net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.TBILL);
						break;

				}
				if (yield != null)
					limitCalcLabel.setText("Yield: " + decimalFormat.format(yield));
				else
					limitCalcLabel.setText(null);
			}
		}
		else
			limitCalcLabel.setText(null);

		boolean cancel = !buySideQuoteRequestEntry.isDone();

		cancelButton.setEnabled(cancel);

		if (!cancel) {
			sideLabel.setBright(Color.LIGHT_GRAY);
			sideLabel.setDark(Color.LIGHT_GRAY.darker());
			sideLabel.validate();
			sideLabel.repaint();
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
	 * Gets the buy side quote request entry.
	 *
	 * @return the buy side quote request entry
	 */
	public BuySideQuoteRequestEntry getBuySideQuoteRequestEntry() {

		return buySideQuoteRequestEntry;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {

		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable(EventObject anEvent) {

		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@Override
	public boolean shouldSelectCell(EventObject anEvent) {

		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {

		checkConsistency();
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	@Override
	public void cancelCellEditing() {

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void addCellEditorListener(CellEditorListener l) {

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void removeCellEditorListener(CellEditorListener l) {

	}

	/**
	 * On buy side quote request response.
	 *
	 * @param quoteRequestResponse the quote request response
	 */
	public void onBuySideQuoteRequestResponse(BuySideQuoteRequestResponse quoteRequestResponse) {

		if (cancelled)
			return;

		if (buySideQuoteRequestEntry.getQuoteReqId() == null)
			return;

		for (final BuySideQuoteRequestEntry buySideQuoteRequestEntry2 : quoteRequestResponse.getBuySideQuoteRequestEntries()) {
			if (buySideQuoteRequestEntry2.getQuoteReqId().equals(buySideQuoteRequestEntry.getQuoteReqId())) {

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						buySideQuoteRequestEntry = buySideQuoteRequestEntry2;

						if (buySideQuoteRequestEntry.getLimit() != null)
							priceSpinner.setValue(buySideQuoteRequestEntry.getLimit());
						
						priceSpinner.setFractional(fractional);

						quantitySpinner.setValue(buySideQuoteRequestEntry.getOrderQuantity());

						checkConsistency();

					}
				});

			}
		}

	}

	private int getDecimalPlaces(double value) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0###################");
		BigDecimal bigDecimal = new BigDecimal(decimalFormat.format(value));
		return bigDecimal.scale();
	}
}
