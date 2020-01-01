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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.sourceforge.fixagora.basis.client.view.dialog.CounterpartyTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityTreeDialog;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideNewOrderSingleEntry.TimeInForce;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.buyside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideInitiator;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class NewOrderSingleDialog.
 */
public class NewOrderSingleDialog extends JDialog implements CellEditor {

	/** The partials. */
	public static boolean partials = true;

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private boolean cancelled = false;

	private JButton okButton = null;

	private JTextField securityTextField = null;

	private boolean dirty = false;

	private JButton btnNewButton = null;

	private JComboBox marketComboBox = null;

	private JTextField counterpartyTextField;

	private JButton btnNewButton2;

	private JComboBox timeInForceComboBox;

	private final ImageIcon redDownIcon = new ImageIcon(
			NewOrderSingleDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrowred.png"));

	private final ImageIcon greenUpIcon = new ImageIcon(
			NewOrderSingleDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrowgreen.png"));

	private final ImageIcon blueUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrow.png"));

	private final ImageIcon blueDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrow.png"));

	private final ImageIcon warning = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	private final ImageIcon cancel = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/cancel.png"));

	private NumberSpinner priceSpinner;

	private NumberSpinner sizeSpinner;

	private DaySpinner dateSpinner;

	private BuySideBookEditor buySideBookEditor = null;

	private SecurityTreeDialog securityTreeDialog = null;

	private CounterpartyTreeDialog counterpartyTreeDialog = null;

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

	private BuySideNewOrderSingleEntry buySideNewOrderSingleEntry = null;

	private JButton cancelButton;

	private GradientLabel sideLabel;

	private JCheckBox partialsAcceptedCheckBox;

	private JButton discardButton;

	private JLabel limitCalcLabel;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0000");

	private Integer basis;

	private static Logger log = Logger.getLogger(NewOrderSingleDialog.class);

	/**
	 * Instantiates a new new order single dialog.
	 *
	 * @param buySideNewOrderSingleEntry the buy side new order single entry
	 * @param buySideBookEditor the buy side book editor
	 */
	public NewOrderSingleDialog(final BuySideNewOrderSingleEntry buySideNewOrderSingleEntry, final BuySideBookEditor buySideBookEditor) {

		super(buySideBookEditor.getMainPanel().getJFrame());

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		this.buySideBookEditor = buySideBookEditor;
		this.buySideNewOrderSingleEntry = buySideNewOrderSingleEntry;

		Set<FSecurity> securities = new HashSet<FSecurity>();
		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities()) {
			securities.add(assignedBuySideBookSecurity.getSecurity());
		}

		this.securityTreeDialog = buySideBookEditor.getMainPanel().getSecurityTreeDialog().getFilteredSecurityTreeDialog(securities, this);

		setBounds(100, 100, 487, 413);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(NewOrderSingleDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		sideLabel = new GradientLabel((new Color(0, 153, 0)).darker(), new Color(0, 153, 0));
		sideLabel.setForeground(Color.WHITE);
		sideLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		sideLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sideLabel.setPreferredSize(new Dimension(0, 30));
		sideLabel.setText("ASK (YOU BUY)");

		if (buySideNewOrderSingleEntry.getSide() != BuySideNewOrderSingleEntry.Side.ASK) {
			sideLabel.setBright(new Color(204, 0, 0));
			sideLabel.setDark((new Color(204, 0, 0)).darker());
			sideLabel.setText("BID (YOU SELL)");
		}

		if (buySideNewOrderSingleEntry.getOrderStatus() == null)
			sideLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (buySideNewOrderSingleEntry.getOrderStatus() == null)
						if (buySideNewOrderSingleEntry.getSide() == Side.ASK) {
							sideLabel.setBright(new Color(204, 0, 0));
							sideLabel.setDark((new Color(204, 0, 0)).darker());
							sideLabel.setText("BID (YOU SELL)");
							buySideNewOrderSingleEntry.setSide(Side.BID);
						}
						else {
							sideLabel.setBright(new Color(0, 153, 0));
							sideLabel.setDark((new Color(0, 153, 0)).darker());
							sideLabel.setText("ASK (YOU BUY)");
							buySideNewOrderSingleEntry.setSide(Side.ASK);

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

		AssignedBuySideBookSecurity assignedBuySideBookSecurity = null;
		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity2 : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities())
			if (assignedBuySideBookSecurity2.getSecurity().getId() == buySideNewOrderSingleEntry.getSecurity())
				assignedBuySideBookSecurity = assignedBuySideBookSecurity2;

		if (assignedBuySideBookSecurity != null && assignedBuySideBookSecurity.getSecurity() != null)
			securityTextField.setText(assignedBuySideBookSecurity.getSecurity().getName());

		AutoCompleteDecorator.decorate(securityTextField, securityTreeDialog.getSecurityList(), true);
		GridBagConstraints gbc_securityTextField = new GridBagConstraints();
		gbc_securityTextField.gridwidth = 3;
		gbc_securityTextField.anchor = GridBagConstraints.NORTH;
		gbc_securityTextField.insets = new Insets(0, 0, 5, 5);
		gbc_securityTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityTextField.gridx = 2;
		gbc_securityTextField.gridy = 1;
		contentPanel.add(securityTextField, gbc_securityTextField);

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
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

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			securityTextField.setEditable(false);
			btnNewButton.setEnabled(false);
		}

		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				securityTreeDialog.setAlwaysOnTop(true);
				securityTreeDialog.setVisible(true);

				if (!securityTreeDialog.isCancelled())
					securityTextField.setText(securityTreeDialog.getSecurity().getName());
			}
		});

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

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
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
				.getCounterpartyForId(buySideNewOrderSingleEntry.getCounterparty());
		if (counterparty != null)
			counterpartyTextField.setText(counterparty.getName());

		counterpartyTextField.getDocument().addDocumentListener(documentListener);

		AutoCompleteDecorator.decorate(counterpartyTextField, buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyList(), true);
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

		btnNewButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (counterpartyTreeDialog != null) {

					counterpartyTreeDialog.setAlwaysOnTop(true);
					counterpartyTreeDialog.setVisible(true);

					if (!counterpartyTreeDialog.isCancelled())
						counterpartyTextField.setText(counterpartyTreeDialog.getCounterparty().getName());
				}
			}
		});

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			counterpartyTextField.setEditable(false);
			counterpartyTextField.setBackground(new Color(204, 216, 255));
			btnNewButton2.setEnabled(false);
		}

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

		FSecurity security = securityTreeDialog.getSecurityForId(buySideNewOrderSingleEntry.getSecurity());
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
				
		priceSpinner.setValue(buySideNewOrderSingleEntry.getLimit());

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

		sizeSpinner = new NumberSpinner(this, blueUpIcon, blueDownIcon, new Color(255, 243, 204));
		sizeSpinner.setMinimumSize(new Dimension(32, 25));
		sizeSpinner.setPreferredSize(new Dimension(32, 25));
		sizeSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		sizeSpinner.setThousandsSeparator(true);

		sizeSpinner.setIncrement(1d);
		sizeSpinner.setValue(buySideNewOrderSingleEntry.getOrderQuantity());

		sizeSpinner.addDocumentListener(documentListener);

		GridBagConstraints gbc_sizeSpinner = new GridBagConstraints();
		gbc_sizeSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_sizeSpinner.gridwidth = 3;
		gbc_sizeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeSpinner.gridx = 2;
		gbc_sizeSpinner.gridy = 6;
		contentPanel.add(sizeSpinner, gbc_sizeSpinner);

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			partialsAcceptedCheckBox.setEnabled(false);
			partialsAcceptedCheckBox.setSelected(buySideNewOrderSingleEntry.getMinQuantity() != buySideNewOrderSingleEntry.getOrderQuantity());
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

		JLabel timeInForceLabel = new JLabel("Time in Force");
		timeInForceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_timeInForceLabel = new GridBagConstraints();
		gbc_timeInForceLabel.anchor = GridBagConstraints.WEST;
		gbc_timeInForceLabel.insets = new Insets(0, 25, 5, 5);
		gbc_timeInForceLabel.gridx = 0;
		gbc_timeInForceLabel.gridy = 7;
		contentPanel.add(timeInForceLabel, gbc_timeInForceLabel);

		timeInForceComboBox = new JComboBox(new String[] { "Fill Or Kill", "Good Till Day", "Good Till Cancel", "Good Till Date" });
		timeInForceComboBox.setMinimumSize(new Dimension(32, 25));
		timeInForceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		timeInForceComboBox.setPreferredSize(new Dimension(32, 25));
		timeInForceComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_timeInForceComboBox = new GridBagConstraints();
		gbc_timeInForceComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_timeInForceComboBox.gridwidth = 3;
		gbc_timeInForceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeInForceComboBox.gridx = 2;
		gbc_timeInForceComboBox.gridy = 7;
		contentPanel.add(timeInForceComboBox, gbc_timeInForceComboBox);

		timeInForceComboBox.setSelectedIndex(0);

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			timeInForceComboBox.setBackground(new Color(204, 216, 255));
			timeInForceComboBox.setEnabled(false);
		}

		timeInForceComboBox.addActionListener(actionListener);

		dateLabel = new JLabel("Date (Settlement)");
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		int width = getFontMetrics(new Font("Dialog", Font.PLAIN, 12)).stringWidth("Date (Settlement)");
		dateLabel.setMinimumSize(new Dimension(width, 25));
		dateLabel.setPreferredSize(new Dimension(width, 25));
		GridBagConstraints gbc_dateLabel = new GridBagConstraints();
		gbc_dateLabel.anchor = GridBagConstraints.WEST;
		gbc_dateLabel.insets = new Insets(0, 25, 5, 5);
		gbc_dateLabel.gridx = 0;
		gbc_dateLabel.gridy = 8;
		contentPanel.add(dateLabel, gbc_dateLabel);

		dateWarningLabel = new JLabel("");
		dateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_dateWarningLabel = new GridBagConstraints();
		gbc_dateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dateWarningLabel.gridx = 1;
		gbc_dateWarningLabel.gridy = 8;
		contentPanel.add(dateWarningLabel, gbc_dateWarningLabel);

		dateSpinner = new DaySpinner(this, new Color(255, 243, 204));
		dateSpinner.setMinimumSize(new Dimension(32, 25));
		dateSpinner.setPreferredSize(new Dimension(32, 25));
		dateSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		dateSpinner.addDocumentListener(documentListener);

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			dateSpinner.setEnabled(false);
		}

		GridBagConstraints gbc_dateSpinner = new GridBagConstraints();
		gbc_dateSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_dateSpinner.gridwidth = 3;
		gbc_dateSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateSpinner.gridx = 2;
		gbc_dateSpinner.gridy = 8;
		contentPanel.add(dateSpinner, gbc_dateSpinner);

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 5;
		gbc_panel.insets = new Insets(15, 0, 0, 25);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 9;
		contentPanel.add(panel, gbc_panel);

		if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
			cancelButton = new JButton("Cancel");
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

							buySideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

							buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.CANCEL_PENDING);

							buySideBookEditor.getBasisClientConnector().sendRequest(
									new NewOrderSingleRequest(buySideNewOrderSingleEntry, RequestIDManager.getInstance().getID()));
						}
					};

					thread.start();

				}
			});
			GridBagConstraints gbc_cancelButton = new GridBagConstraints();
			gbc_cancelButton.anchor = GridBagConstraints.WEST;
			gbc_cancelButton.insets = new Insets(20, 25, 5, 0);
			gbc_cancelButton.gridx = 0;
			gbc_cancelButton.gridy = 9;
			contentPanel.add(cancelButton, gbc_cancelButton);
		}

		okButton = new JButton();
		panel.add(okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);

		if (buySideNewOrderSingleEntry.getOrderStatus() == null) {
			setTitle("Submit New Order");
			okButton.setText("Submit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Replace Order");
			okButton.setText("Replace");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

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

						buySideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

						Counterparty counterparty = counterpartyTreeDialog.getCounterpartyForName(counterpartyTextField.getText());
						if (counterparty != null) {

							AbstractInitiator marketInterface = null;

							buySideNewOrderSingleEntry.setCounterparty(counterparty.getId());

							for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
								if (counterPartyPartyID.getAbstractBusinessComponent() instanceof AbstractInitiator) {
									AbstractInitiator fixInitiator = (AbstractInitiator) counterPartyPartyID.getAbstractBusinessComponent();
									if (fixInitiator.getRoute() == null && fixInitiator.getMarketName().equals(marketComboBox.getSelectedItem())
											&& counterPartyPartyID.getPartyRole() == 1) {
										marketInterface = fixInitiator;
									}
								}
							}

							if (marketInterface == null)
								for (AssignedBuySideInitiator assignedBuySideFIXInitiator : buySideBookEditor.getBuySideBook()
										.getAssignedBuySideFIXInitiators()) {
									AbstractInitiator fixInitiator = (AbstractInitiator) buySideBookEditor.getMainPanel().getAbstractBusinessObjectForId(
											assignedBuySideFIXInitiator.getInitiator().getId());
									if (fixInitiator != null && fixInitiator.getRoute() == null && fixInitiator.getCounterparty() != null
											&& fixInitiator.getMarketName().equals(marketComboBox.getSelectedItem())
											&& fixInitiator.getCounterparty().getId() == counterparty.getId())
										marketInterface = fixInitiator;
								}

							if (marketInterface != null)
								buySideNewOrderSingleEntry.setMarketInterface(marketInterface.getId());

						}

						if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
							buySideNewOrderSingleEntry.setOrderStatus(OrderStatus.REPLACE_PENDING);
							buySideNewOrderSingleEntry.setNewLimit(priceSpinner.getValue());
							if (sizeSpinner.getValue() != null)
								buySideNewOrderSingleEntry.setNewOrderQuantity(sizeSpinner.getValue());
						}
						else {
							buySideNewOrderSingleEntry.setLimit(priceSpinner.getValue());
							if (sizeSpinner.getValue() != null)
								buySideNewOrderSingleEntry.setOrderQuantity(sizeSpinner.getValue());
							if (!partialsAcceptedCheckBox.isSelected())
								buySideNewOrderSingleEntry.setMinQuantity(sizeSpinner.getValue());
						}
						FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());

						if (security != null)
							buySideNewOrderSingleEntry.setSecurity(security.getId());

						switch (timeInForceComboBox.getSelectedIndex()) {
							case 1:
								buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DAY);
								break;
							case 2:
								buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_CANCEL);
								break;
							case 3:
								buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
								break;
							default:
								buySideNewOrderSingleEntry.setTimeInForce(TimeInForce.FILL_OR_KILL);
								break;
						}

						Calendar calendar = dateSpinner.getCalendarValue();
						if (calendar != null)
							buySideNewOrderSingleEntry.setTifDate(calendar.getTimeInMillis());
						else
							buySideNewOrderSingleEntry.setTifDate(0);

						buySideBookEditor.getBasisClientConnector().sendRequest(
								new NewOrderSingleRequest(buySideNewOrderSingleEntry, RequestIDManager.getInstance().getID()));

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

				buySideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

				cancelled = true;
				setVisible(false);
			}
		});

		buySideBookEditor.addNewOrderSingleDialog(this);

		checkConsistency();
	}

	private synchronized void checkConsistency() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				dirty = true;

				Double value = null;

				Date settlementDate = null;

				try {
					if (marketComboBox.getSelectedIndex() == -1) {
						if (marketComboBox.getModel().getSize() == 0) {
							dirty = false;
							marketWarningLabel.setIcon(warning);
							marketWarningLabel.setToolTipText("No market defined.");
						}
						else {
							marketWarningLabel.setIcon(null);
							marketWarningLabel.setToolTipText(null);
							marketComboBox.setSelectedIndex(0);
						}
					}
					if (marketComboBox.getSelectedIndex() != -1) {
						String market = marketComboBox.getSelectedItem().toString();
						String counterpartyText = counterpartyTextField.getText();
						Set<Counterparty> counterparties = new HashSet<Counterparty>();
						for (Counterparty counterparty : buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterparties()) {
							for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
								if (counterPartyPartyID.getAbstractBusinessComponent() instanceof AbstractInitiator) {
									AbstractInitiator fixInitiator = (AbstractInitiator) counterPartyPartyID.getAbstractBusinessComponent();
									if (fixInitiator.getRoute() == null && fixInitiator.getMarketName().equals(market)
											&& counterPartyPartyID.getPartyRole() == 1)
										counterparties.add(counterparty);
								}
								else if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
									counterparties.add(counterparty);
								}
							}
						}
						counterpartyTreeDialog = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog()
								.getFilteredCounterpartyTreeTreeDialog(counterparties, NewOrderSingleDialog.this);
						if (counterpartyTreeDialog.getCounterpartyForName(counterpartyText) == null) {
							dirty = false;
							counterpartyWarningLabel.setIcon(warning);
							counterpartyWarningLabel.setToolTipText("No counterparty selected");

							counterpartyTextField.setText(null);

						}
						else {
							counterpartyWarningLabel.setIcon(null);
							counterpartyWarningLabel.setToolTipText(null);
						}

					}
					double tickSize = 0.001;

					FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());

					net.sourceforge.fixagora.buyside.shared.persistence.BuySideBook.CalcMethod calcMethod = buySideBookEditor.getBuySideBook().getCalcMethod();

					Boolean fractional = false;
					
					if(buySideBookEditor.getBuySideBook().getFractionalDisplay()!=null)
						fractional = buySideBookEditor.getBuySideBook().getFractionalDisplay();

					
					if (security == null) {

						dirty = false;
						securityWarningLabel.setIcon(warning);
						securityWarningLabel.setToolTipText("No security selected");

						securityWarningLabel.setText(null);

					}
					else {

						for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities()) {
							if (assignedBuySideBookSecurity.getSecurity().getId() == security.getId() && assignedBuySideBookSecurity.getCalcMethod() != null)
								calcMethod = assignedBuySideBookSecurity.getCalcMethod();
							if (assignedBuySideBookSecurity.getSecurity().getId() == security.getId() && assignedBuySideBookSecurity.getFractionalDisplay() != null)
								fractional = assignedBuySideBookSecurity.getFractionalDisplay();
						}

						securityWarningLabel.setIcon(null);
						securityWarningLabel.setToolTipText(null);
					}
					

					if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getMinPriceIncrement() != null)
						tickSize = security.getSecurityDetails().getMinPriceIncrement();
					
					priceSpinner.setDecimalPlaces(getDecimalPlaces(tickSize));
					priceSpinner.setIncrement(tickSize);
					priceSpinner.setTickSize(tickSize);
					priceSpinner.setFractional(fractional);
					if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
							&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
						priceSpinner.setMinValue(null);
						limitLabel.setText("Limit (Yield)");
					}
					else {
						priceSpinner.setMinValue(0d);
						limitLabel.setText("Limit (Price)");
					}

					if (!priceSpinner.isValidNumber()) {
						dirty = false;
						limitWarningLabel.setIcon(warning);
						limitWarningLabel.setToolTipText("Invalid price");
						value = null;
					}
					else {
						limitWarningLabel.setIcon(null);
						limitWarningLabel.setToolTipText(null);
						value = priceSpinner.getValue();
					}

					double sizeIncrement = 1;
					if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null) {
						sizeIncrement = security.getSecurityDetails().getContractMultiplier();
					}
					if (buySideNewOrderSingleEntry.getOrderStatus() != null) {
						sizeSpinner.setMinValue(buySideNewOrderSingleEntry.getCumulativeQuantity());
					}
					else
						sizeSpinner.setMinValue(sizeIncrement);
					sizeSpinner.setIncrement(sizeIncrement);
					sizeSpinner.setTickSize(sizeIncrement);
					if (sizeSpinner.getValue() == null || sizeSpinner.getValue().doubleValue() == 0) {
						sizeSpinner.setValue(sizeIncrement);
					}

					if (!sizeSpinner.isValidNumber()) {
						dirty = false;
						sizeWarningLabel.setIcon(warning);
						sizeWarningLabel.setToolTipText("Invalid size");
					}
					else {
						sizeWarningLabel.setIcon(null);
						sizeWarningLabel.setToolTipText(null);
					}

					if (dateReset) {
						BankCalendar bankCalendar = buySideBookEditor.getBuySideBook().getBankCalendar();
						int valuta = buySideBookEditor.getBuySideBook().getValuta();
						basis = buySideBookEditor.getBuySideBook().getDaycountConvention();
						if (security != null)
							for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook()
									.getAssignedBuySideBookSecurities())
								if (assignedBuySideBookSecurity.getSecurity().getId() == security.getId()) {
									if (assignedBuySideBookSecurity.getBankCalendar() != null)
										bankCalendar = assignedBuySideBookSecurity.getBankCalendar();
									if (assignedBuySideBookSecurity.getValuta() != null)
										valuta = assignedBuySideBookSecurity.getValuta();
									if (assignedBuySideBookSecurity.getDaycountConvention() != null)
										basis = assignedBuySideBookSecurity.getDaycountConvention();
								}
						dateSpinner.setBankCalendar(bankCalendar);

						dateLabel.setEnabled(true);

						final Calendar calendar = Calendar.getInstance();

						dateSpinner.setMinValue(Calendar.getInstance());

						switch (timeInForceComboBox.getSelectedIndex()) {
							case 0:
							case 1:
								dateLabel.setText("Date (Settlement)");
								for (int i = 0; i < valuta; i++) {
									calendar.add(Calendar.DAY_OF_YEAR, +1);
									while (!bankCalendar.getCalendar().isBusinessDay(new org.jquantlib.time.Date(calendar.getTime())))
										calendar.add(Calendar.DAY_OF_YEAR, +1);
								}

								dateSpinner.setValue(calendar.getTime());
								break;

							case 2:

								dateLabel.setEnabled(false);
								dateLabel.setText("Date (not used)");
								dateSpinner.setEnabled(false);
								break;

							case 3:
								dateLabel.setText("Date (Good Till)");
								while (!bankCalendar.getCalendar().isBusinessDay(new org.jquantlib.time.Date(calendar.getTime())))
									calendar.add(Calendar.DAY_OF_YEAR, +1);

								dateSpinner.setValue(calendar.getTime());
								break;
							default:
								break;
						}
					}

					if (buySideNewOrderSingleEntry.getOrderStatus() == null)
						dateSpinner.setEnabled(true);
					else
						dateSpinner.setEnabled(false);

					dateReset = false;

					if (timeInForceComboBox.getSelectedIndex() != 2 && !dateSpinner.isValidDate()) {
						dirty = false;
						dateWarningLabel.setIcon(warning);
						dateWarningLabel.setToolTipText("Invalid date");
						settlementDate = null;
					}
					else {
						dateWarningLabel.setIcon(null);
						dateWarningLabel.setToolTipText(null);
						settlementDate = dateSpinner.getCalendarValue().getTime();
					}

					if (security != null && value != null && settlementDate != null && basis != null) {
						if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
							Double price = null;
							switch (calcMethod) {
								case DEFAULT:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.DEFAULT);
									break;
								case DISC:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.DISC);
									break;
								case MAT:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.MAT);
									break;
								case NONE:
									price = null;
									break;
								case ODDF:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.ODDF);
									break;
								case ODDL:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.ODDL);
									break;
								case TBILL:
									price = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis, CalcMethod.TBILL);
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
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.DEFAULT);
									break;
								case DISC:
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.DISC);
									break;
								case MAT:
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.MAT);
									break;
								case NONE:
									yield = null;
									break;
								case ODDF:
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.ODDF);
									break;
								case ODDL:
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.ODDL);
									break;
								case TBILL:
									yield = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis, CalcMethod.TBILL);
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

				}
				catch (Exception e) {
					log.error("Bug", e);
				}

				if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL_PENDING
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED) {
					dirty = false;
				}

				boolean cancel = true;

				if (buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.CANCEL_PENDING
						|| buySideNewOrderSingleEntry.getOrderStatus() == OrderStatus.REJECTED) {
					dirty = false;
					cancel = false;
				}

				if (buySideNewOrderSingleEntry.getCumulativeQuantity() > 0 && buySideNewOrderSingleEntry.getLeaveQuantity() == 0) {
					dirty = false;
					cancel = false;
				}

				if (cancelButton != null)
					cancelButton.setEnabled(cancel);

				if (!cancel) {
					sizeSpinner.setEnabled(false);
					priceSpinner.setEnabled(false);
					sideLabel.setBright(Color.LIGHT_GRAY);
					sideLabel.setDark(Color.LIGHT_GRAY.darker());
					sideLabel.validate();
					sideLabel.repaint();
				}

				okButton.setEnabled(dirty);

			}
		});

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
	 * Gets the buy side new order single entry.
	 *
	 * @return the buy side new order single entry
	 */
	public BuySideNewOrderSingleEntry getBuySideNewOrderSingleEntry() {

		return buySideNewOrderSingleEntry;
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
	 * On new order single response.
	 *
	 * @param newOrderSingleResponse the new order single response
	 */
	public void onNewOrderSingleResponse(NewOrderSingleResponse newOrderSingleResponse) {

		if (cancelled)
			return;

		if (buySideNewOrderSingleEntry.getOrderId() == null)
			return;

		BuySideNewOrderSingleEntry buySideNewOrderSingleEntry2 = newOrderSingleResponse.getBuySideNewOrderSingleEntry();
		if (buySideNewOrderSingleEntry2.getOrderId().equals(buySideNewOrderSingleEntry.getOrderId())
				|| buySideNewOrderSingleEntry.getOrderId().equals(buySideNewOrderSingleEntry2.getOriginalOrderId())) {
			buySideNewOrderSingleEntry = buySideNewOrderSingleEntry2;
			checkConsistency();
		}
	}
	
	private int getDecimalPlaces(double value) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0###################");
		BigDecimal bigDecimal = new BigDecimal(decimalFormat.format(value));
		return bigDecimal.scale();
	}
}
