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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.buyside.client.view.editor.BuySideBookEditor;
import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideEntry.Side;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestEntry;
import net.sourceforge.fixagora.buyside.shared.communication.BuySideQuoteRequestRequest;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideBookSecurity;
import net.sourceforge.fixagora.buyside.shared.persistence.AssignedBuySideInitiator;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class QuoteRequestDialog.
 */
public class QuoteRequestDialog extends JDialog implements CellEditor {

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

	private JTextField counterparty1TextField;

	private JButton counterparty1Button;

	private NumberSpinner expireTimeSpinner;

	private final ImageIcon blueUpIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1uparrow.png"));

	private final ImageIcon blueDownIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/1downarrow.png"));

	private final ImageIcon warning = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	private NumberSpinner sizeSpinner;

	private DaySpinner dateSpinner;

	private BuySideBookEditor buySideBookEditor = null;

	private SecurityTreeDialog securityTreeDialog = null;

	private CounterpartyTreeDialog counterpartyTreeDialog = null;

	/** The date reset. */
	protected boolean dateReset = true;

	private JLabel dateLabel = null;

	private JLabel securityWarningLabel = null;

	private JLabel marketWarningLabel = null;

	private JLabel counterparty1WarningLabel = null;

	private JLabel sizeWarningLabel = null;

	private JLabel dateWarningLabel = null;

	private JButton cancelButton;

	private GradientLabel sideLabel;

	private JCheckBox partialsAcceptedCheckBox;

	private JButton discardButton;

	private JLabel limitCalcLabel;

	private DecimalFormat decimalFormat = new DecimalFormat("0.0000");

	private JLabel counterparty2WarningLabel;

	private JTextField counterparty2TextField;

	private JButton counterparty2Button;

	private JLabel counterparty3WarningLabel;

	private JTextField counterparty3TextField;

	private JButton counterparty3Button;

	private JLabel counterparty4WarningLabel;

	private JTextField counterparty4TextField;

	private JButton counterparty4Button;

	private JLabel expireTimeWarningLabel;

	private static double expireTime = 180;

	private static Logger log = Logger.getLogger(QuoteRequestDialog.class);

	/**
	 * Instantiates a new quote request dialog.
	 *
	 * @param counterparties the counterparties
	 * @param security the security
	 * @param side the side
	 * @param buySideBookEditor the buy side book editor
	 */
	public QuoteRequestDialog(List<Counterparty> counterparties, long security, BuySideQuoteRequestEntry.Side side, final BuySideBookEditor buySideBookEditor) {

		super(buySideBookEditor.getMainPanel().getJFrame());

		if (counterparties == null)
			counterparties = new ArrayList<Counterparty>();

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		this.buySideBookEditor = buySideBookEditor;

		Set<FSecurity> securities = new HashSet<FSecurity>();
		for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook().getAssignedBuySideBookSecurities()) {
			securities.add(assignedBuySideBookSecurity.getSecurity());
		}

		this.securityTreeDialog = buySideBookEditor.getMainPanel().getSecurityTreeDialog().getFilteredSecurityTreeDialog(securities, this);

		setBounds(100, 100, 487, 460);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(QuoteRequestDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		sideLabel = new GradientLabel((new Color(0, 153, 0)).darker(), new Color(0, 153, 0));
		sideLabel.setForeground(Color.WHITE);
		sideLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		sideLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sideLabel.setPreferredSize(new Dimension(0, 30));
		sideLabel.setText("INQUIRY ASK (YOU BUY)");

		if (side != BuySideQuoteRequestEntry.Side.ASK) {
			sideLabel.setBright(new Color(204, 0, 0));
			sideLabel.setDark((new Color(204, 0, 0)).darker());
			sideLabel.setText("INQUIRY BID (YOU SELL)");
		}

		sideLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (sideLabel.getText().equals("INQUIRY ASK (YOU BUY)")) {
					sideLabel.setBright(new Color(204, 0, 0));
					sideLabel.setDark((new Color(204, 0, 0)).darker());
					sideLabel.setText("INQUIRY BID (YOU SELL)");
				}
				else {
					sideLabel.setBright(new Color(0, 153, 0));
					sideLabel.setDark((new Color(0, 153, 0)).darker());
					sideLabel.setText("INQUIRY ASK (YOU BUY)");
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
			if (assignedBuySideBookSecurity2.getSecurity().getId() == security)
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

		btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnNewButton.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 25);
		gbc_btnNewButton.gridx = 5;
		gbc_btnNewButton.gridy = 1;
		contentPanel.add(btnNewButton, gbc_btnNewButton);

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

		JLabel counterparty1Label = new JLabel("Counterparty 1");
		counterparty1Label.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterparty1Label = new GridBagConstraints();
		gbc_counterparty1Label.anchor = GridBagConstraints.WEST;
		gbc_counterparty1Label.insets = new Insets(0, 25, 5, 5);
		gbc_counterparty1Label.gridx = 0;
		gbc_counterparty1Label.gridy = 3;
		contentPanel.add(counterparty1Label, gbc_counterparty1Label);

		counterparty1WarningLabel = new JLabel("");
		counterparty1WarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_counterparty1WarningLabel = new GridBagConstraints();
		gbc_counterparty1WarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty1WarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty1WarningLabel.gridx = 1;
		gbc_counterparty1WarningLabel.gridy = 3;
		contentPanel.add(counterparty1WarningLabel, gbc_counterparty1WarningLabel);

		counterparty1TextField = new JTextField();
		counterparty1TextField.setPreferredSize(new Dimension(200, 25));
		counterparty1TextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		counterparty1TextField.setBackground(new Color(255, 243, 204));
		counterparty1TextField.setColumns(10);

		Counterparty counterparty1 = null;
		if (counterparties.size() > 0)
			counterparty1 = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyForId(counterparties.get(0).getId());
		if (counterparty1 != null)
			counterparty1TextField.setText(counterparty1.getName());

		counterparty1TextField.getDocument().addDocumentListener(documentListener);

		counterparty1TextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE
						|| (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE && counterparty1TextField.getText().equals(
								counterparty1TextField.getSelectedText())))
					counterparty1TextField.setText(null);

			}
		});

		AutoCompleteDecorator.decorate(counterparty1TextField, buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyList(), true);
		GridBagConstraints gbc_counterparty1TextField = new GridBagConstraints();
		gbc_counterparty1TextField.gridwidth = 3;
		gbc_counterparty1TextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty1TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty1TextField.gridx = 2;
		gbc_counterparty1TextField.gridy = 3;
		contentPanel.add(counterparty1TextField, gbc_counterparty1TextField);

		counterparty1Button = new JButton("...");
		counterparty1Button.setFont(new Font("Dialog", Font.PLAIN, 12));
		counterparty1Button.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_counterparty1Button = new GridBagConstraints();
		gbc_counterparty1Button.anchor = GridBagConstraints.WEST;
		gbc_counterparty1Button.insets = new Insets(0, 0, 5, 25);
		gbc_counterparty1Button.gridx = 5;
		gbc_counterparty1Button.gridy = 3;
		contentPanel.add(counterparty1Button, gbc_counterparty1Button);

		counterparty1Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (counterpartyTreeDialog != null) {

					counterpartyTreeDialog.setAlwaysOnTop(true);
					counterpartyTreeDialog.setVisible(true);

					if (!counterpartyTreeDialog.isCancelled())
						counterparty1TextField.setText(counterpartyTreeDialog.getCounterparty().getName());
				}
			}
		});

		JLabel counterparty2Label = new JLabel("Counterparty 2");
		counterparty2Label.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterparty2Label = new GridBagConstraints();
		gbc_counterparty2Label.anchor = GridBagConstraints.WEST;
		gbc_counterparty2Label.insets = new Insets(0, 25, 5, 5);
		gbc_counterparty2Label.gridx = 0;
		gbc_counterparty2Label.gridy = 4;
		contentPanel.add(counterparty2Label, gbc_counterparty2Label);

		counterparty2WarningLabel = new JLabel("");
		counterparty2WarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_counterparty2WarningLabel = new GridBagConstraints();
		gbc_counterparty2WarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty2WarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty2WarningLabel.gridx = 1;
		gbc_counterparty2WarningLabel.gridy = 4;
		contentPanel.add(counterparty2WarningLabel, gbc_counterparty2WarningLabel);

		counterparty2TextField = new JTextField();
		counterparty2TextField.setPreferredSize(new Dimension(200, 25));
		counterparty2TextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		counterparty2TextField.setBackground(new Color(255, 243, 204));
		counterparty2TextField.setColumns(10);

		counterparty2TextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE
						|| (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE && counterparty2TextField.getText().equals(
								counterparty2TextField.getSelectedText())))
					counterparty2TextField.setText(null);

			}
		});

		Counterparty counterparty2 = null;
		if (counterparties.size() > 1)
			counterparty2 = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyForId(counterparties.get(1).getId());

		if (counterparty2 != null)
			counterparty2TextField.setText(counterparty2.getName());

		counterparty2TextField.getDocument().addDocumentListener(documentListener);

		AutoCompleteDecorator.decorate(counterparty2TextField, buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyList(), true);
		GridBagConstraints gbc_counterparty2TextField = new GridBagConstraints();
		gbc_counterparty2TextField.gridwidth = 3;
		gbc_counterparty2TextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty2TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty2TextField.gridx = 2;
		gbc_counterparty2TextField.gridy = 4;
		contentPanel.add(counterparty2TextField, gbc_counterparty2TextField);

		counterparty2Button = new JButton("...");
		counterparty2Button.setFont(new Font("Dialog", Font.PLAIN, 12));
		counterparty2Button.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_counterparty2Button = new GridBagConstraints();
		gbc_counterparty2Button.anchor = GridBagConstraints.WEST;
		gbc_counterparty2Button.insets = new Insets(0, 0, 5, 25);
		gbc_counterparty2Button.gridx = 5;
		gbc_counterparty2Button.gridy = 4;
		contentPanel.add(counterparty2Button, gbc_counterparty2Button);

		counterparty2Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (counterpartyTreeDialog != null) {

					counterpartyTreeDialog.setAlwaysOnTop(true);
					counterpartyTreeDialog.setVisible(true);

					if (!counterpartyTreeDialog.isCancelled())
						counterparty2TextField.setText(counterpartyTreeDialog.getCounterparty().getName());
				}
			}
		});

		JLabel counterparty3Label = new JLabel("Counterparty 3");
		counterparty3Label.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterparty3Label = new GridBagConstraints();
		gbc_counterparty3Label.anchor = GridBagConstraints.WEST;
		gbc_counterparty3Label.insets = new Insets(0, 25, 5, 5);
		gbc_counterparty3Label.gridx = 0;
		gbc_counterparty3Label.gridy = 5;
		contentPanel.add(counterparty3Label, gbc_counterparty3Label);

		counterparty3WarningLabel = new JLabel("");
		counterparty3WarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_counterparty3WarningLabel = new GridBagConstraints();
		gbc_counterparty3WarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty3WarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty3WarningLabel.gridx = 1;
		gbc_counterparty3WarningLabel.gridy = 5;
		contentPanel.add(counterparty3WarningLabel, gbc_counterparty3WarningLabel);

		counterparty3TextField = new JTextField();
		counterparty3TextField.setPreferredSize(new Dimension(200, 25));
		counterparty3TextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		counterparty3TextField.setBackground(new Color(255, 243, 204));
		counterparty3TextField.setColumns(10);

		counterparty3TextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE
						|| (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE && counterparty3TextField.getText().equals(
								counterparty3TextField.getSelectedText())))
					counterparty3TextField.setText(null);

			}
		});

		Counterparty counterparty3 = null;
		if (counterparties.size() > 2)
			counterparty3 = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyForId(counterparties.get(2).getId());

		if (counterparty3 != null)
			counterparty3TextField.setText(counterparty3.getName());

		counterparty3TextField.getDocument().addDocumentListener(documentListener);

		AutoCompleteDecorator.decorate(counterparty3TextField, buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyList(), true);
		GridBagConstraints gbc_counterparty3TextField = new GridBagConstraints();
		gbc_counterparty3TextField.gridwidth = 3;
		gbc_counterparty3TextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty3TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty3TextField.gridx = 2;
		gbc_counterparty3TextField.gridy = 5;
		contentPanel.add(counterparty3TextField, gbc_counterparty3TextField);

		counterparty3Button = new JButton("...");
		counterparty3Button.setFont(new Font("Dialog", Font.PLAIN, 12));
		counterparty3Button.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_counterparty3Button = new GridBagConstraints();
		gbc_counterparty3Button.anchor = GridBagConstraints.WEST;
		gbc_counterparty3Button.insets = new Insets(0, 0, 5, 25);
		gbc_counterparty3Button.gridx = 5;
		gbc_counterparty3Button.gridy = 5;
		contentPanel.add(counterparty3Button, gbc_counterparty3Button);

		counterparty3Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (counterpartyTreeDialog != null) {

					counterpartyTreeDialog.setAlwaysOnTop(true);
					counterpartyTreeDialog.setVisible(true);

					if (!counterpartyTreeDialog.isCancelled())
						counterparty3TextField.setText(counterpartyTreeDialog.getCounterparty().getName());
				}
			}
		});

		JLabel counterparty4Label = new JLabel("Counterparty 4");
		counterparty4Label.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_counterparty4Label = new GridBagConstraints();
		gbc_counterparty4Label.anchor = GridBagConstraints.WEST;
		gbc_counterparty4Label.insets = new Insets(0, 25, 5, 5);
		gbc_counterparty4Label.gridx = 0;
		gbc_counterparty4Label.gridy = 6;
		contentPanel.add(counterparty4Label, gbc_counterparty4Label);

		counterparty4WarningLabel = new JLabel("");
		counterparty4WarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_counterparty4WarningLabel = new GridBagConstraints();
		gbc_counterparty4WarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty4WarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty4WarningLabel.gridx = 1;
		gbc_counterparty4WarningLabel.gridy = 6;
		contentPanel.add(counterparty4WarningLabel, gbc_counterparty4WarningLabel);

		counterparty4TextField = new JTextField();
		counterparty4TextField.setPreferredSize(new Dimension(200, 25));
		counterparty4TextField.setBorder(new CompoundBorder(new LineBorder((new Color(204, 216, 255)).darker()), new EmptyBorder(0, 5, 0, 0)));
		counterparty4TextField.setBackground(new Color(255, 243, 204));
		counterparty4TextField.setColumns(10);

		counterparty4TextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE
						|| (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE && counterparty4TextField.getText().equals(
								counterparty4TextField.getSelectedText())))
					counterparty4TextField.setText(null);

			}
		});

		Counterparty counterparty4 = null;
		if (counterparties.size() > 3)
			counterparty4 = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyForId(counterparties.get(3).getId());

		if (counterparty4 != null)
			counterparty4TextField.setText(counterparty4.getName());

		counterparty4TextField.getDocument().addDocumentListener(documentListener);

		AutoCompleteDecorator.decorate(counterparty4TextField, buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterpartyList(), true);
		GridBagConstraints gbc_counterparty4TextField = new GridBagConstraints();
		gbc_counterparty4TextField.gridwidth = 3;
		gbc_counterparty4TextField.insets = new Insets(0, 0, 5, 5);
		gbc_counterparty4TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_counterparty4TextField.gridx = 2;
		gbc_counterparty4TextField.gridy = 6;
		contentPanel.add(counterparty4TextField, gbc_counterparty4TextField);

		counterparty4Button = new JButton("...");
		counterparty4Button.setFont(new Font("Dialog", Font.PLAIN, 12));
		counterparty4Button.setPreferredSize(new Dimension(50, 25));
		GridBagConstraints gbc_counterparty4Button = new GridBagConstraints();
		gbc_counterparty4Button.anchor = GridBagConstraints.WEST;
		gbc_counterparty4Button.insets = new Insets(0, 0, 5, 25);
		gbc_counterparty4Button.gridx = 5;
		gbc_counterparty4Button.gridy = 6;
		contentPanel.add(counterparty4Button, gbc_counterparty4Button);

		counterparty4Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (counterpartyTreeDialog != null) {

					counterpartyTreeDialog.setAlwaysOnTop(true);
					counterpartyTreeDialog.setVisible(true);

					if (!counterpartyTreeDialog.isCancelled())
						counterparty4TextField.setText(counterpartyTreeDialog.getCounterparty().getName());
				}
			}
		});

		JLabel partialsAcceptedLabel = new JLabel("Partials Accepted");
		partialsAcceptedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_partialsAcceptedLabel = new GridBagConstraints();
		gbc_partialsAcceptedLabel.anchor = GridBagConstraints.WEST;
		gbc_partialsAcceptedLabel.insets = new Insets(0, 25, 5, 5);
		gbc_partialsAcceptedLabel.gridx = 0;
		gbc_partialsAcceptedLabel.gridy = 7;
		contentPanel.add(partialsAcceptedLabel, gbc_partialsAcceptedLabel);

		partialsAcceptedCheckBox = new JCheckBox();
		partialsAcceptedCheckBox.setOpaque(false);
		partialsAcceptedCheckBox.setMinimumSize(new Dimension(32, 25));
		partialsAcceptedCheckBox.setPreferredSize(new Dimension(32, 25));
		GridBagConstraints gbc_partialsAcceptedCheckBox = new GridBagConstraints();
		gbc_partialsAcceptedCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_partialsAcceptedCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_partialsAcceptedCheckBox.gridx = 2;
		gbc_partialsAcceptedCheckBox.gridy = 7;
		contentPanel.add(partialsAcceptedCheckBox, gbc_partialsAcceptedCheckBox);

		limitCalcLabel = new JLabel("");
		limitCalcLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_calcLabel = new GridBagConstraints();
		gbc_calcLabel.gridwidth = 2;
		gbc_calcLabel.anchor = GridBagConstraints.EAST;
		gbc_calcLabel.insets = new Insets(0, 0, 5, 39);
		gbc_calcLabel.gridx = 3;
		gbc_calcLabel.gridy = 7;
		contentPanel.add(limitCalcLabel, gbc_calcLabel);

		JLabel sizeLabel = new JLabel("Size");
		sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sizeLabel = new GridBagConstraints();
		gbc_sizeLabel.anchor = GridBagConstraints.WEST;
		gbc_sizeLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sizeLabel.gridx = 0;
		gbc_sizeLabel.gridy = 8;
		contentPanel.add(sizeLabel, gbc_sizeLabel);

		sizeWarningLabel = new JLabel("");
		sizeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sizeWarningLabel = new GridBagConstraints();
		gbc_sizeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sizeWarningLabel.gridx = 1;
		gbc_sizeWarningLabel.gridy = 8;
		contentPanel.add(sizeWarningLabel, gbc_sizeWarningLabel);

		sizeSpinner = new NumberSpinner(this, blueUpIcon, blueDownIcon, new Color(255, 243, 204));
		sizeSpinner.setMinimumSize(new Dimension(32, 25));
		sizeSpinner.setPreferredSize(new Dimension(32, 25));
		sizeSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		sizeSpinner.setThousandsSeparator(true);

		sizeSpinner.setIncrement(1d);

		sizeSpinner.addDocumentListener(documentListener);

		GridBagConstraints gbc_sizeSpinner = new GridBagConstraints();
		gbc_sizeSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_sizeSpinner.gridwidth = 3;
		gbc_sizeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_sizeSpinner.gridx = 2;
		gbc_sizeSpinner.gridy = 8;
		contentPanel.add(sizeSpinner, gbc_sizeSpinner);

		partialsAcceptedCheckBox.setSelected(partials);
		partialsAcceptedCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				partials = partialsAcceptedCheckBox.isSelected();

			}
		});

		JLabel timeInForceLabel = new JLabel("Expire Time (Seconds)");
		timeInForceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_timeInForceLabel = new GridBagConstraints();
		gbc_timeInForceLabel.anchor = GridBagConstraints.WEST;
		gbc_timeInForceLabel.insets = new Insets(0, 25, 5, 5);
		gbc_timeInForceLabel.gridx = 0;
		gbc_timeInForceLabel.gridy = 9;
		contentPanel.add(timeInForceLabel, gbc_timeInForceLabel);

		expireTimeSpinner = new NumberSpinner(this, blueUpIcon, blueDownIcon, new Color(255, 243, 204));
		expireTimeSpinner.setMinimumSize(new Dimension(32, 25));
		expireTimeSpinner.setPreferredSize(new Dimension(32, 25));
		expireTimeSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		expireTimeSpinner.setThousandsSeparator(true);

		expireTimeSpinner.setIncrement(1d);
		expireTimeSpinner.setMinValue(30d);
		expireTimeSpinner.setValue(expireTime);

		expireTimeSpinner.addDocumentListener(documentListener);

		expireTimeWarningLabel = new JLabel("");
		expireTimeWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_expireTimeWarningLabel = new GridBagConstraints();
		gbc_expireTimeWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_expireTimeWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_expireTimeWarningLabel.gridx = 1;
		gbc_expireTimeWarningLabel.gridy = 9;
		contentPanel.add(expireTimeWarningLabel, gbc_expireTimeWarningLabel);

		GridBagConstraints gbc_timeInForceComboBox = new GridBagConstraints();
		gbc_timeInForceComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_timeInForceComboBox.gridwidth = 3;
		gbc_timeInForceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeInForceComboBox.gridx = 2;
		gbc_timeInForceComboBox.gridy = 9;
		contentPanel.add(expireTimeSpinner, gbc_timeInForceComboBox);

		dateLabel = new JLabel("Date (Settlement)");
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		int width = getFontMetrics(new Font("Dialog", Font.PLAIN, 12)).stringWidth("Date (Settlement)");
		dateLabel.setMinimumSize(new Dimension(width, 25));
		dateLabel.setPreferredSize(new Dimension(width, 25));
		GridBagConstraints gbc_dateLabel = new GridBagConstraints();
		gbc_dateLabel.anchor = GridBagConstraints.WEST;
		gbc_dateLabel.insets = new Insets(0, 25, 5, 5);
		gbc_dateLabel.gridx = 0;
		gbc_dateLabel.gridy = 10;
		contentPanel.add(dateLabel, gbc_dateLabel);

		dateWarningLabel = new JLabel("");
		dateWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_dateWarningLabel = new GridBagConstraints();
		gbc_dateWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dateWarningLabel.gridx = 1;
		gbc_dateWarningLabel.gridy = 10;
		contentPanel.add(dateWarningLabel, gbc_dateWarningLabel);

		dateSpinner = new DaySpinner(this, new Color(255, 243, 204));
		dateSpinner.setMinimumSize(new Dimension(32, 25));
		dateSpinner.setPreferredSize(new Dimension(32, 25));
		dateSpinner.setBorder(new LineBorder((new Color(204, 216, 255)).darker()));
		dateSpinner.addDocumentListener(documentListener);

		GridBagConstraints gbc_dateSpinner = new GridBagConstraints();
		gbc_dateSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_dateSpinner.gridwidth = 3;
		gbc_dateSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateSpinner.gridx = 2;
		gbc_dateSpinner.gridy = 10;
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
		gbc_panel.gridy = 11;
		contentPanel.add(panel, gbc_panel);

		okButton = new JButton();
		panel.add(okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);

		setTitle("Submit New Inquiry");
		okButton.setText("Submit");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));

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

						List<BuySideQuoteRequestEntry> buySideQuoteRequestEntries = new ArrayList<BuySideQuoteRequestEntry>();

						for (int i = 0; i < 4; i++) {

							String counterpartyText = null;

							if (i == 0)
								counterpartyText = counterparty1TextField.getText();

							else if (i == 1)
								counterpartyText = counterparty2TextField.getText();
							
							else if (i == 2)
								counterpartyText = counterparty3TextField.getText();
							
							else if (i == 3)
								counterpartyText = counterparty4TextField.getText();
							
							Counterparty counterparty = counterpartyTreeDialog.getCounterpartyForName(counterpartyText);
							if (counterparty != null) {

								BuySideQuoteRequestEntry buySideQuoteRequestEntry = new BuySideQuoteRequestEntry();

								buySideQuoteRequestEntry.setCounterparty(counterparty.getId());

								buySideQuoteRequestEntry.setBuySideBook(buySideBookEditor.getBuySideBook().getId());

								buySideQuoteRequestEntries.add(buySideQuoteRequestEntry);
								
								AbstractInitiator marketInterface = null;

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
										AbstractInitiator fixInitiator = (AbstractInitiator)buySideBookEditor.getMainPanel().getAbstractBusinessObjectForId(assignedBuySideFIXInitiator.getInitiator().getId());
										if (fixInitiator!=null&&fixInitiator.getRoute() == null && fixInitiator.getCounterparty() != null
												&& fixInitiator.getMarketName().equals(marketComboBox.getSelectedItem())
												&& fixInitiator.getCounterparty().getId() == counterparty.getId())
											marketInterface = fixInitiator;
									}
								
								if(marketInterface!=null)
									buySideQuoteRequestEntry.setMarketInterface(marketInterface.getId());						
								
							}

						}

						if (sizeSpinner.getValue() != null)
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setOrderQuantity(sizeSpinner.getValue());
						if (!partialsAcceptedCheckBox.isSelected())
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setMinQuantity(sizeSpinner.getValue());
						
						if(sideLabel.getText().equals("INQUIRY ASK (YOU BUY)"))
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setSide(Side.ASK);
						else
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setSide(Side.BID);

						FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());

						if (security != null)
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setSecurity(security.getId());

						long expireTime = 0;

						if (expireTimeSpinner.getValue() != null)
							expireTime = expireTimeSpinner.getValue().longValue() * 1000 + System.currentTimeMillis();

						for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
							buySideQuoteRequestEntry.setExpireDate(expireTime);

						Calendar calendar = dateSpinner.getCalendarValue();
						if (calendar != null)
							for (BuySideQuoteRequestEntry buySideQuoteRequestEntry : buySideQuoteRequestEntries)
								buySideQuoteRequestEntry.setSettlementDate(calendar.getTimeInMillis());

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

				// buySideBookEditor.removeNewOrderSingleDialog(QuoteRequestDialog.this);

				cancelled = true;
				setVisible(false);
			}
		});

		// buySideBookEditor.addNewOrderSingleDialog(this);

		checkConsistency();
	}

	private synchronized void checkConsistency() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				dirty = true;

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

						Set<Counterparty> counterparties = new HashSet<Counterparty>();
						for (Counterparty counterparty : buySideBookEditor.getMainPanel().getCounterpartyTreeDialog().getCounterparties()) {
							for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
								if (counterPartyPartyID.getAbstractBusinessComponent() instanceof AbstractInitiator) {
									AbstractInitiator fixInitiator = (AbstractInitiator) counterPartyPartyID.getAbstractBusinessComponent();
									if (fixInitiator.getRoute() == null && fixInitiator.getMarketName().equals(market)
											&& counterPartyPartyID.getPartyRole() == 1)
										counterparties.add(counterparty);
								}
								else if(counterPartyPartyID.getAbstractBusinessComponent()==null)
								{
									counterparties.add(counterparty);
								}
							}
						}
						
						counterpartyTreeDialog = buySideBookEditor.getMainPanel().getCounterpartyTreeDialog()
								.getFilteredCounterpartyTreeTreeDialog(counterparties, QuoteRequestDialog.this);

						String counterpartyText = counterparty1TextField.getText();

						Set<Counterparty> selectedCounterparties = new HashSet<Counterparty>();

						if (counterpartyTreeDialog.getCounterpartyForName(counterpartyText) == null) {

							counterparty1TextField.setText(null);
							counterparty1WarningLabel.setIcon(null);
							counterparty1WarningLabel.setToolTipText(null);

						}
						else
							selectedCounterparties.add(counterpartyTreeDialog.getCounterpartyForName(counterpartyText));

						counterpartyText = counterparty2TextField.getText();

						if (counterpartyTreeDialog.getCounterpartyForName(counterpartyText) == null) {

							counterparty2TextField.setText(null);
							counterparty2WarningLabel.setIcon(null);
							counterparty2WarningLabel.setToolTipText(null);

						}
						else if (!selectedCounterparties.add(counterpartyTreeDialog.getCounterpartyForName(counterpartyText))) {
							counterparty2WarningLabel.setIcon(warning);
							counterparty2WarningLabel.setToolTipText("Duplicate entry");
							dirty = false;
						}
						else {
							counterparty2WarningLabel.setIcon(null);
							counterparty2WarningLabel.setToolTipText(null);
						}

						counterpartyText = counterparty3TextField.getText();

						if (counterpartyTreeDialog.getCounterpartyForName(counterpartyText) == null) {

							counterparty3TextField.setText(null);
							counterparty3WarningLabel.setIcon(null);
							counterparty3WarningLabel.setToolTipText(null);

						}
						else if (!selectedCounterparties.add(counterpartyTreeDialog.getCounterpartyForName(counterpartyText))) {
							counterparty3WarningLabel.setIcon(warning);
							counterparty3WarningLabel.setToolTipText("Duplicate entry");
							dirty = false;
						}
						else {
							counterparty3WarningLabel.setIcon(null);
							counterparty3WarningLabel.setToolTipText(null);
						}

						counterpartyText = counterparty4TextField.getText();

						if (counterpartyTreeDialog.getCounterpartyForName(counterpartyText) == null) {

							counterparty4TextField.setText(null);
							counterparty4WarningLabel.setIcon(null);
							counterparty4WarningLabel.setToolTipText(null);

						}
						else if (!selectedCounterparties.add(counterpartyTreeDialog.getCounterpartyForName(counterpartyText))) {
							counterparty4WarningLabel.setIcon(warning);
							counterparty4WarningLabel.setToolTipText("Duplicate entry");
							dirty = false;
						}
						else {
							counterparty4WarningLabel.setIcon(null);
							counterparty4WarningLabel.setToolTipText(null);
						}

						if (selectedCounterparties.size() == 0) {
							counterparty1WarningLabel.setIcon(warning);
							counterparty1WarningLabel.setToolTipText("No counterparty selected");
							dirty = false;
						}
						else {
							counterparty1WarningLabel.setIcon(null);
							counterparty1WarningLabel.setToolTipText(null);
						}

					}

					FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());

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

					if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getPriceQuoteMethod() != null
							&& security.getSecurityDetails().getPriceQuoteMethod().equals("INT")) {
					}
					else {
					}

					double sizeIncrement = 1;
					if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null) {
						sizeIncrement = security.getSecurityDetails().getContractMultiplier();
					}

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

					if (!expireTimeSpinner.isValidNumber()) {
						dirty = false;
						expireTimeWarningLabel.setIcon(warning);
						expireTimeWarningLabel.setToolTipText("Invalid expire time");
					}
					else {
						expireTimeWarningLabel.setIcon(null);
						expireTimeWarningLabel.setToolTipText(null);
						expireTime = expireTimeSpinner.getValue();
					}

					if (dateReset) {
						BankCalendar bankCalendar = buySideBookEditor.getBuySideBook().getBankCalendar();
						int valuta = buySideBookEditor.getBuySideBook().getValuta();

						if (security != null)
							for (AssignedBuySideBookSecurity assignedBuySideBookSecurity : buySideBookEditor.getBuySideBook()
									.getAssignedBuySideBookSecurities())
								if (assignedBuySideBookSecurity.getSecurity().getId() == security.getId()) {
									if (assignedBuySideBookSecurity.getBankCalendar() != null)
										bankCalendar = assignedBuySideBookSecurity.getBankCalendar();
									if (assignedBuySideBookSecurity.getValuta() != null)
										valuta = assignedBuySideBookSecurity.getValuta();

								}
						dateSpinner.setBankCalendar(bankCalendar);

						dateLabel.setEnabled(true);

						final Calendar calendar = Calendar.getInstance();

						dateSpinner.setMinValue(Calendar.getInstance());

						for (int i = 0; i < valuta; i++) {
							calendar.add(Calendar.DAY_OF_YEAR, +1);
							while (!bankCalendar.getCalendar().isBusinessDay(new org.jquantlib.time.Date(calendar.getTime())))
								calendar.add(Calendar.DAY_OF_YEAR, +1);
						}

						dateSpinner.setValue(calendar.getTime());

					}

					dateReset = false;

					if (!dateSpinner.isValidDate()) {
						dirty = false;
						dateWarningLabel.setIcon(warning);
						dateWarningLabel.setToolTipText("Invalid date");

					}
					else {
						dateWarningLabel.setIcon(null);
						dateWarningLabel.setToolTipText(null);

					}

				}
				catch (Exception e) {
					log.error("Bug", e);
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

}
