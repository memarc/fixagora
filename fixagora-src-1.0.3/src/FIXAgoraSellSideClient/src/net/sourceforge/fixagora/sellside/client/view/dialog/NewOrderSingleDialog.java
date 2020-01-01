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
package net.sourceforge.fixagora.sellside.client.view.dialog;

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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.control.YieldCalculator;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.client.view.editor.SellSideBookEditor;
import net.sourceforge.fixagora.sellside.shared.communication.AbstractSellSideEntry.Side;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleRequest;
import net.sourceforge.fixagora.sellside.shared.communication.NewOrderSingleResponse;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry;
import net.sourceforge.fixagora.sellside.shared.communication.SellSideNewOrderSingleEntry.OrderStatus;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;

import org.apache.log4j.Logger;

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

	private JTextField marketTextField = null;

	private JTextField counterpartyTextField;

	private JTextField timeInForceComboBox;

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

	private SellSideBookEditor sellSideBookEditor = null;

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

	private SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry = null;

	private JButton cancelButton = null;

	private GradientLabel sideLabel = null;

	private JCheckBox partialsAcceptedCheckBox = null;

	private FSecurity security = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private DecimalFormat decimalFormat = new DecimalFormat("0.0##############");

	private DecimalFormat decimalFormat2 = new DecimalFormat("0.0000");

	private JButton discardButton;

	private JLabel limitCalcLabel;

	private Integer basis;

	private CalcMethod calcMethod;

	private static Logger log = Logger.getLogger(NewOrderSingleDialog.class);

	/**
	 * Instantiates a new new order single dialog.
	 *
	 * @param sellSideNewOrderSingleEntry the sell side new order single entry
	 * @param sellSideBookEditor the sell side book editor
	 */
	public NewOrderSingleDialog(final SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry, final SellSideBookEditor sellSideBookEditor) {

		super(sellSideBookEditor.getMainPanel().getJFrame());

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat2.setDecimalFormatSymbols(decimalFormatSymbols);

		this.sellSideBookEditor = sellSideBookEditor;
		this.sellSideNewOrderSingleEntry = sellSideNewOrderSingleEntry;

		setBounds(100, 100, 487, 440);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(NewOrderSingleDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(false);
		setAlwaysOnTop(true);

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
		sideLabel.setText("BID (YOU BUY)");

		if (sellSideNewOrderSingleEntry.getSide() != SellSideNewOrderSingleEntry.Side.BID) {
			sideLabel.setBright(new Color(204, 0, 0));
			sideLabel.setDark((new Color(204, 0, 0)).darker());
			sideLabel.setText("ASK (YOU SELL)");
		}

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
		GridBagConstraints gbc_securityTextField = new GridBagConstraints();
		gbc_securityTextField.gridwidth = 4;
		gbc_securityTextField.anchor = GridBagConstraints.NORTH;
		gbc_securityTextField.insets = new Insets(0, 0, 5, 25);
		gbc_securityTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityTextField.gridx = 2;
		gbc_securityTextField.gridy = 1;
		contentPanel.add(securityTextField, gbc_securityTextField);
		securityTextField.setEditable(false);
		securityTextField.setBackground(new Color(204, 216, 255));

		Boolean fractional = false;

		if (sellSideBookEditor.getSellSideBook().getFractionalDisplay() != null)
			fractional = sellSideBookEditor.getSellSideBook().getFractionalDisplay();

		calcMethod = sellSideBookEditor.getSellSideBook().getCalcMethod();

		AssignedSellSideBookSecurity assignedSellSideBookSecurity = null;
		for (AssignedSellSideBookSecurity assignedSellSideBookSecurity2 : sellSideBookEditor.getSellSideBook().getAssignedSellSideBookSecurities())
			if (assignedSellSideBookSecurity2.getSecurity().getId() == sellSideNewOrderSingleEntry.getSecurity())
				assignedSellSideBookSecurity = assignedSellSideBookSecurity2;

		if (assignedSellSideBookSecurity != null && assignedSellSideBookSecurity.getSecurity() != null) {
			security = sellSideBookEditor.getMainPanel().getSecurityTreeDialog().getSecurityForId(assignedSellSideBookSecurity.getSecurity().getId());
			if (security != null)
				securityTextField.setText(security.getName());

			if (assignedSellSideBookSecurity.getCalcMethod() != null)
				calcMethod = assignedSellSideBookSecurity.getCalcMethod();

			if (assignedSellSideBookSecurity.getFractionalDisplay() != null)
				fractional = assignedSellSideBookSecurity.getFractionalDisplay();
		}

		securityTextField.setEditable(false);
		securityTextField.setBackground(new Color(204, 216, 255));

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

		marketTextField = new JTextField("Unknown");
		marketTextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		marketTextField.setMinimumSize(new Dimension(32, 25));
		marketTextField.setPreferredSize(new Dimension(32, 25));
		marketTextField.setBackground(new Color(204, 216, 255));
		marketTextField.setEditable(false);

		AbstractAcceptor fixAcceptor = sellSideBookEditor.getMainPanel().getAcceptorTreeDialog()
				.getAbstractAcceptorForId(sellSideNewOrderSingleEntry.getMarketInterface());
		if (fixAcceptor != null)
			marketTextField.setText(fixAcceptor.getMarketName());

		GridBagConstraints gbc_marketTextField = new GridBagConstraints();
		gbc_marketTextField.insets = new Insets(0, 0, 5, 25);
		gbc_marketTextField.gridwidth = 4;
		gbc_marketTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_marketTextField.gridx = 2;
		gbc_marketTextField.gridy = 2;
		contentPanel.add(marketTextField, gbc_marketTextField);

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
		counterpartyTextField.setEditable(false);
		counterpartyTextField.setBackground(new Color(204, 216, 255));

		Counterparty counterparty = sellSideBookEditor.getMainPanel().getCounterpartyTreeDialog()
				.getCounterpartyForId(sellSideNewOrderSingleEntry.getCounterparty());
		if (counterparty != null)
			counterpartyTextField.setText(counterparty.getName());

		GridBagConstraints gbc_counterpartyTextField = new GridBagConstraints();
		gbc_counterpartyTextField.gridwidth = 4;
		gbc_counterpartyTextField.insets = new Insets(0, 0, 5, 25);
		gbc_counterpartyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterpartyTextField.gridx = 2;
		gbc_counterpartyTextField.gridy = 3;
		contentPanel.add(counterpartyTextField, gbc_counterpartyTextField);

		limitLabel = new JLabel("Fill (Price)");
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
		gbc_priceSpinner.insets = new Insets(0, 0, 5, 25);
		gbc_priceSpinner.gridwidth = 4;
		gbc_priceSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceSpinner.gridx = 2;
		gbc_priceSpinner.gridy = 4;
		contentPanel.add(priceSpinner, gbc_priceSpinner);

		double tickSize = 0.001;

		if (security == null) {
			dirty = false;
			securityWarningLabel.setIcon(warning);
			securityWarningLabel.setToolTipText("No security selected");

			securityWarningLabel.setText(null);

		}
		else {
			securityWarningLabel.setIcon(null);
			securityWarningLabel.setToolTipText(null);
		}

		if (security != null && security.getSecurityDetails() != null) {
			if (security.getSecurityDetails().getPriceQuoteMethod() != null && security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
				tickSize = 0.0001;
			}

			if (security.getSecurityDetails().getMinPriceIncrement() != null)
				tickSize = security.getSecurityDetails().getMinPriceIncrement();
		}

		priceSpinner.setLeadingZeroes(1);
		priceSpinner.setDecimalPlaces(getDecimalPlaces(tickSize));
		priceSpinner.setIncrement(tickSize);
		priceSpinner.setTickSize(tickSize);
		priceSpinner.setFractional(fractional);

		if (sellSideNewOrderSingleEntry.getLimit() != null)
			priceSpinner.setValue(sellSideNewOrderSingleEntry.getLimit());

		if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
				&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
			limitLabel.setText("Fill (Yield)");
			if (sellSideNewOrderSingleEntry.getSide() == Side.BID) {
				priceSpinner.setMaxValue(sellSideNewOrderSingleEntry.getLimit());
			}
			else {
				priceSpinner.setMinValue(sellSideNewOrderSingleEntry.getLimit());
			}
		}
		else {
			if (sellSideNewOrderSingleEntry.getLimit() != null) {
				if (sellSideNewOrderSingleEntry.getSide() == Side.BID) {
					priceSpinner.setMinValue(sellSideNewOrderSingleEntry.getLimit());
				}
				else {
					priceSpinner.setMaxValue(sellSideNewOrderSingleEntry.getLimit());
					priceSpinner.setMinValue(0d);
				}
			}
			else {
				priceSpinner.setMinValue(0d);
			}
		}

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

		partialsAcceptedCheckBox.setEnabled(false);
		partialsAcceptedCheckBox.setSelected(sellSideNewOrderSingleEntry.getMinQuantity() != sellSideNewOrderSingleEntry.getOrderQuantity());

		limitCalcLabel = new JLabel("");
		limitCalcLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_limitCalcLabel = new GridBagConstraints();
		gbc_limitCalcLabel.anchor = GridBagConstraints.EAST;
		gbc_limitCalcLabel.gridwidth = 2;
		gbc_limitCalcLabel.insets = new Insets(0, 0, 5, 58);
		gbc_limitCalcLabel.gridx = 3;
		gbc_limitCalcLabel.gridy = 5;
		contentPanel.add(limitCalcLabel, gbc_limitCalcLabel);

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

		double sizeIncrement = 1;
		if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null) {
			sizeIncrement = security.getSecurityDetails().getContractMultiplier();
		}

		sizeSpinner.setMaxValue(sellSideNewOrderSingleEntry.getLeaveQuantity());
		sizeSpinner.setMinValue(sizeIncrement);
		sizeSpinner.setIncrement(sizeIncrement);
		sizeSpinner.setTickSize(sizeIncrement);
		sizeSpinner.setValue(sellSideNewOrderSingleEntry.getLeaveQuantity());

		sizeSpinner.addDocumentListener(documentListener);

		GridBagConstraints gbc_sizeSpinner = new GridBagConstraints();
		gbc_sizeSpinner.insets = new Insets(0, 0, 5, 25);
		gbc_sizeSpinner.gridwidth = 4;
		gbc_sizeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeSpinner.gridx = 2;
		gbc_sizeSpinner.gridy = 6;
		contentPanel.add(sizeSpinner, gbc_sizeSpinner);

		JLabel timeInForceLabel = new JLabel("Time in Force");
		timeInForceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_timeInForceLabel = new GridBagConstraints();
		gbc_timeInForceLabel.anchor = GridBagConstraints.WEST;
		gbc_timeInForceLabel.insets = new Insets(0, 25, 5, 5);
		gbc_timeInForceLabel.gridx = 0;
		gbc_timeInForceLabel.gridy = 7;
		contentPanel.add(timeInForceLabel, gbc_timeInForceLabel);

		timeInForceComboBox = new JTextField();
		timeInForceComboBox.setMinimumSize(new Dimension(32, 25));
		timeInForceComboBox.setPreferredSize(new Dimension(32, 25));
		timeInForceComboBox.setBackground(new Color(204, 216, 255));
		timeInForceComboBox.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		timeInForceComboBox.setEditable(false);

		GridBagConstraints gbc_timeInForceComboBox = new GridBagConstraints();
		gbc_timeInForceComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_timeInForceComboBox.gridwidth = 4;
		gbc_timeInForceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeInForceComboBox.gridx = 2;
		gbc_timeInForceComboBox.gridy = 7;
		contentPanel.add(timeInForceComboBox, gbc_timeInForceComboBox);

		switch (sellSideNewOrderSingleEntry.getTimeInForce()) {
			case FILL_OR_KILL:
				timeInForceComboBox.setText("Fill Or Kill");
				break;
			case GOOD_TILL_CANCEL:
				timeInForceComboBox.setText("Good Till Cancel");
				break;
			case GOOD_TILL_DATE:
				timeInForceComboBox.setText("Good Till " + simpleDateFormat.format(new Date(sellSideNewOrderSingleEntry.getTifDate())));
				break;
			case GOOD_TILL_DAY:
				timeInForceComboBox.setText("Good Till Day");
				break;

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

		if (sellSideNewOrderSingleEntry.getSettlementDate() != null) {
			dateSpinner.setEnabled(false);
			dateSpinner.setValue(new Date(sellSideNewOrderSingleEntry.getSettlementDate()));
		}

		GridBagConstraints gbc_dateSpinner = new GridBagConstraints();
		gbc_dateSpinner.insets = new Insets(0, 0, 5, 25);
		gbc_dateSpinner.gridwidth = 4;
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
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(20, 0, 0, 25);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 9;
		contentPanel.add(panel, gbc_panel);

		cancelButton = new JButton("Kill");
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setIcon(cancel);
		cancelButton.setActionCommand("Cancel");

		if (sellSideNewOrderSingleEntry.getCumulativeQuantity() == 0)
			cancelButton.setText("Reject");

		cancelButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {

				super.mouseEntered(e);
				sizeSpinner.setBackground(new Color(204, 0, 0));
				sizeSpinner.setForeground(new Color(204, 0, 0));
				sizeSpinner.revalidate();

				priceSpinner.setBackground(new Color(204, 0, 0));
				priceSpinner.setForeground(new Color(204, 0, 0));
				priceSpinner.revalidate();
			}

			@Override
			public void mouseExited(MouseEvent e) {

				super.mouseExited(e);
				sizeSpinner.setBackground(new Color(255, 243, 204));
				sizeSpinner.setForeground(Color.BLACK);
				sizeSpinner.revalidate();

				priceSpinner.setBackground(new Color(255, 243, 204));
				priceSpinner.setForeground(Color.BLACK);
				priceSpinner.revalidate();
			}

		});

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

						sellSideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

						if (sellSideNewOrderSingleEntry.getCumulativeQuantity() > 0)
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.DONE_FOR_DAY_PENDING);
						else
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.REJECTED_PENDING);

						sellSideNewOrderSingleEntry.setLeaveQuantity(0);

						sellSideBookEditor.getBasisClientConnector().sendRequest(
								new NewOrderSingleRequest(sellSideNewOrderSingleEntry, RequestIDManager.getInstance().getID()));
					}
				};

				thread.start();

			}
		});
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_cancelButton.insets = new Insets(20, 25, 0, 5);
		gbc_cancelButton.gridx = 0;
		gbc_cancelButton.gridy = 9;
		contentPanel.add(cancelButton, gbc_cancelButton);

		okButton = new JButton();
		panel.add(okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);

		setTitle("Fill Order");
		okButton.setText("Fill");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png")));

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = true;
				setVisible(false);
				okButton.setEnabled(false);
				cancelButton.setEnabled(false);
				discardButton.setEnabled(false);

				Thread thread = new Thread() {

					public void run() {

						sellSideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

						Calendar calendar = dateSpinner.getCalendarValue();

						if (calendar != null)
							sellSideNewOrderSingleEntry.setSettlementDate(calendar.getTimeInMillis());

						Double value = priceSpinner.getValue();

						if (security != null && value != null && calendar != null && basis != null) {
							Date settlementDate = calendar.getTime();

							if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
									&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {

								Double calculated = null;
								switch (calcMethod) {
									case DEFAULT:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DEFAULT);
										break;
									case DISC:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DISC);
										break;
									case MAT:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.MAT);
										break;
									case NONE:
										calculated = null;
										break;
									case ODDF:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDF);
										break;
									case ODDL:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDL);
										break;
									case TBILL:
										calculated = YieldCalculator.getInstance().getPrice(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.TBILL);
										break;

								}
								if (calculated != null) {
									BigDecimal bigDecimal = new BigDecimal(calculated);
									double price = bigDecimal.setScale(3, RoundingMode.HALF_EVEN).doubleValue();
									sellSideNewOrderSingleEntry.setLastPrice(price);
								}
								else
									sellSideNewOrderSingleEntry.setLastPrice(null);
								sellSideNewOrderSingleEntry.setLastYield(value);
							}
							else {
								Double calculated = null;
								switch (calcMethod) {
									case DEFAULT:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DEFAULT);
										break;
									case DISC:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.DISC);
										break;
									case MAT:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.MAT);
										break;
									case NONE:
										calculated = null;
										break;
									case ODDF:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDF);
										break;
									case ODDL:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.ODDL);
										break;
									case TBILL:
										calculated = YieldCalculator.getInstance().getYield(security, value, settlementDate, basis,
												net.sourceforge.fixagora.basis.shared.control.YieldCalculator.CalcMethod.TBILL);
										break;

								}
								if (calculated != null) {
									BigDecimal bigDecimal = new BigDecimal(calculated);
									double yield = bigDecimal.setScale(4, RoundingMode.HALF_EVEN).doubleValue();
									sellSideNewOrderSingleEntry.setLastYield(yield);
								}
								else
									sellSideNewOrderSingleEntry.setLastYield(null);
								sellSideNewOrderSingleEntry.setLastPrice(value);
							}
						}
						else
							return;

						BigDecimal cumulativeQuantity = new BigDecimal(decimalFormat.format(sellSideNewOrderSingleEntry.getCumulativeQuantity()));

						BigDecimal lastQuantity = new BigDecimal(decimalFormat.format(sizeSpinner.getValue()));

						BigDecimal leaveQuantity = new BigDecimal(sellSideNewOrderSingleEntry.getLeaveQuantity());

						cumulativeQuantity = cumulativeQuantity.add(lastQuantity);

						leaveQuantity = leaveQuantity.subtract(lastQuantity);

						sellSideNewOrderSingleEntry.setLastQuantity(lastQuantity.doubleValue());

						sellSideNewOrderSingleEntry.setLeaveQuantity(leaveQuantity.doubleValue());

						sellSideNewOrderSingleEntry.setCumulativeQuantity(cumulativeQuantity.doubleValue());

						if (leaveQuantity.doubleValue() == 0)
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.FILLED_PENDING);
						else
							sellSideNewOrderSingleEntry.setOrderStatus(OrderStatus.PARTIALLY_FILLED_PENDING);

						sellSideBookEditor.getBasisClientConnector().sendRequest(
								new NewOrderSingleRequest(sellSideNewOrderSingleEntry, RequestIDManager.getInstance().getID()));
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
		discardButton.setOpaque(true);

		discardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				sellSideBookEditor.removeNewOrderSingleDialog(NewOrderSingleDialog.this);

				cancelled = true;
				setVisible(false);
			}
		});

		sellSideBookEditor.addNewOrderSingleDialog(this);

		checkConsistency();
	}

	private synchronized void checkConsistency() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				Double value = null;

				Date settlementDate = null;

				try {
					dirty = true;

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

					if (!sizeSpinner.isValidNumber()) {
						dirty = false;
						sizeWarningLabel.setIcon(warning);
						sizeWarningLabel.setToolTipText("Invalid size");
					}
					else {
						sizeWarningLabel.setIcon(null);
						sizeWarningLabel.setToolTipText(null);
					}

					if (!priceSpinner.isValidNumber()) {
						dirty = false;
						limitWarningLabel.setIcon(warning);
						limitWarningLabel.setToolTipText("Invalid price");
					}
					else {
						limitWarningLabel.setIcon(null);
						limitWarningLabel.setToolTipText(null);
					}

					if (dateReset) {
						BankCalendar bankCalendar = sellSideBookEditor.getSellSideBook().getBankCalendar();
						int valuta = sellSideBookEditor.getSellSideBook().getValuta();
						basis = sellSideBookEditor.getSellSideBook().getDaycountConvention();
						if (security != null)
							for (AssignedSellSideBookSecurity assignedSellSideBookSecurity : sellSideBookEditor.getSellSideBook()
									.getAssignedSellSideBookSecurities())
								if (assignedSellSideBookSecurity.getSecurity().getId() == security.getId()) {
									if (assignedSellSideBookSecurity.getBankCalendar() != null)
										bankCalendar = assignedSellSideBookSecurity.getBankCalendar();
									if (assignedSellSideBookSecurity.getValuta() != null)
										valuta = assignedSellSideBookSecurity.getValuta();
									if (assignedSellSideBookSecurity.getDaycountConvention() != null)
										basis = assignedSellSideBookSecurity.getDaycountConvention();
								}
						dateSpinner.setBankCalendar(bankCalendar);

						dateLabel.setEnabled(true);

						final Calendar calendar = Calendar.getInstance();

						dateSpinner.setMinValue(Calendar.getInstance());

						dateLabel.setText("Date (Settlement)");
						for (int i = 0; i < valuta; i++) {
							calendar.add(Calendar.DAY_OF_YEAR, +1);
							while (!bankCalendar.getCalendar().isBusinessDay(new org.jquantlib.time.Date(calendar.getTime())))
								calendar.add(Calendar.DAY_OF_YEAR, +1);
						}

						if (sellSideNewOrderSingleEntry.getSettlementDate() == null)
							dateSpinner.setValue(calendar.getTime());
					}

					if (sellSideNewOrderSingleEntry.getSettlementDate() == null)
						dateSpinner.setEnabled(true);
					else
						dateSpinner.setEnabled(false);

					if (!dateSpinner.isValidDate()) {
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

					dateReset = false;

					if (security != null && value != null && settlementDate != null && basis != null) {
						if (security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
								&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
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
								limitCalcLabel.setText("Price: " + decimalFormat2.format(price));
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
								limitCalcLabel.setText("Yield: " + decimalFormat2.format(yield));
							else
								limitCalcLabel.setText(null);
						}
					}
					else
						limitCalcLabel.setText(null);

					okButton.setEnabled(dirty);
				}
				catch (Exception e) {
					log.error("Bug", e);
				}

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
	 * Gets the sell side new order single entry.
	 *
	 * @return the sell side new order single entry
	 */
	public SellSideNewOrderSingleEntry getSellSideNewOrderSingleEntry() {

		return sellSideNewOrderSingleEntry;
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

		if (sellSideNewOrderSingleEntry.getOrderId() == null)
			return;

		if (newOrderSingleResponse.getSellSideNewOrderSingleEntry() == null)
			return;

		SellSideNewOrderSingleEntry sellSideNewOrderSingleEntry2 = newOrderSingleResponse.getSellSideNewOrderSingleEntry();
		if (sellSideNewOrderSingleEntry2.getOrderId().equals(sellSideNewOrderSingleEntry.getOrderId())
				|| sellSideNewOrderSingleEntry.getOrderId().equals(sellSideNewOrderSingleEntry2.getOriginalOrderId())) {
			sellSideNewOrderSingleEntry = sellSideNewOrderSingleEntry2;
			checkConsistency();
		}
	}

	private int getDecimalPlaces(double value) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0#########");
		BigDecimal bigDecimal = new BigDecimal(decimalFormat.format(value));
		return bigDecimal.scale();
	}

}
