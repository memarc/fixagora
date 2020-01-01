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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.model.editor.ComboBoxEntry;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityTreeDialog;
import net.sourceforge.fixagora.basis.client.view.document.PositiveIntegerDocument;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.AssignedSellSideBookSecurity;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook;
import net.sourceforge.fixagora.sellside.shared.persistence.SellSideBook.CalcMethod;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The Class AssignedSellSideSecurityDialog.
 */
public class AssignedSellSideSecurityDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
		
	private boolean cancelled = true;
	
	private JButton okButton = null;
	
	private AssignedSellSideBookSecurity assignedSellSideBookSecurity = null;
	
	private JTextField securityTextField = null;
		
	private boolean dirty = false;
		
	private JButton btnNewButton = null;
	
	private SecurityTreeDialog securityTreeDialog = null;

	private JComboBox daycountComboBox = null;

	private JTextField valutaTextField = null;

	private JComboBox bankCalendarComboBox = null;
	
	private final DecimalFormat integerFormat = new DecimalFormat("##0");

	private JComboBox calculationComboBox;

	private JComboBox displayPriceComboBox;


	/**
	 * Instantiates a new assigned sell side security dialog.
	 *
	 * @param assignedSellSideBookSecurity the assigned sell side book security
	 * @param securityTreeDialog the security tree dialog
	 * @param bankCalendars the bank calendars
	 * @param edit the edit
	 */
	public AssignedSellSideSecurityDialog(AssignedSellSideBookSecurity assignedSellSideBookSecurity, final SecurityTreeDialog securityTreeDialog, List<BankCalendar> bankCalendars, boolean edit) {

		this.assignedSellSideBookSecurity = assignedSellSideBookSecurity;
		this.securityTreeDialog = securityTreeDialog;

		setBounds(100, 100, 487, 301);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(AssignedSellSideSecurityDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
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

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel legSecurityLabel = new JLabel("Security");
		legSecurityLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 0;
		contentPanel.add(legSecurityLabel, gbc_legSecurityLabel);

		securityTextField = new JTextField();
		securityTextField.setPreferredSize(new Dimension(200, 25));
		securityTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		securityTextField.setBackground(new Color(255, 243, 204));
		securityTextField.setColumns(10);
		
		AutoCompleteDecorator.decorate(securityTextField,securityTreeDialog.getSecurityList(),true);
		GridBagConstraints gbc_localeOfissueTextField = new GridBagConstraints();
		gbc_localeOfissueTextField.gridwidth = 2;
		gbc_localeOfissueTextField.anchor = GridBagConstraints.NORTH;
		gbc_localeOfissueTextField.insets = new Insets(25, 0, 5, 5);
		gbc_localeOfissueTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_localeOfissueTextField.gridx = 2;
		gbc_localeOfissueTextField.gridy = 0;
		contentPanel.add(securityTextField, gbc_localeOfissueTextField);
		
		btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(25, 0, 5, 25);
		gbc_btnNewButton.gridx = 4;
		gbc_btnNewButton.gridy = 0;
		contentPanel.add(btnNewButton, gbc_btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				securityTreeDialog.setVisible(true);
				if(securityTreeDialog.getSecurity()!=null&&!securityTreeDialog.isCancelled())
					securityTextField.setText(securityTreeDialog.getSecurity().getName());
			}
		});
		
		if(assignedSellSideBookSecurity.getId()!=0)
		{
			btnNewButton.setEnabled(false);
			securityTextField.setBackground(new Color(204, 216, 255));
			securityTextField.setEditable(false);
		}
		
		if(assignedSellSideBookSecurity!=null&&assignedSellSideBookSecurity.getSecurity()!=null)
			securityTextField.setText(assignedSellSideBookSecurity.getSecurity().getName());
		
		securityTextField.getDocument().addDocumentListener(documentListener);
		
		JLabel valutaLabel = new JLabel("Valuta T+");
		valutaLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_positionLimitLabel = new GridBagConstraints();
		gbc_positionLimitLabel.anchor = GridBagConstraints.WEST;
		gbc_positionLimitLabel.insets = new Insets(0, 25, 5, 5);
		gbc_positionLimitLabel.gridx = 0;
		gbc_positionLimitLabel.gridy = 1;
		contentPanel.add(valutaLabel, gbc_positionLimitLabel);

		valutaTextField = new JTextField();
		valutaTextField.setDocument(new PositiveIntegerDocument());
		valutaTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		valutaTextField.setBackground(new Color(255, 243, 204));
		valutaTextField.setPreferredSize(new Dimension(250, 25));
		valutaTextField.setColumns(10);
		GridBagConstraints gbc_positionLimitTextField = new GridBagConstraints();
		gbc_positionLimitTextField.insets = new Insets(0, 0, 5, 5);
		gbc_positionLimitTextField.gridwidth = 2;
		gbc_positionLimitTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_positionLimitTextField.gridx = 2;
		gbc_positionLimitTextField.gridy = 1;
		contentPanel.add(valutaTextField, gbc_positionLimitTextField);
		
		if (assignedSellSideBookSecurity.getValuta() != null)
			valutaTextField.setText(integerFormat.format(assignedSellSideBookSecurity.getValuta()));
		else
			valutaTextField.setText(integerFormat.format(assignedSellSideBookSecurity.getSellSideBook().getValuta()));
		
		valutaTextField.getDocument().addDocumentListener(documentListener);

		JLabel bankCalendarLabel = new JLabel("Bank Calendar");
		bankCalendarLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_bankCalendarLabel = new GridBagConstraints();
		gbc_bankCalendarLabel.anchor = GridBagConstraints.WEST;
		gbc_bankCalendarLabel.insets = new Insets(0, 25, 5, 5);
		gbc_bankCalendarLabel.gridx = 0;
		gbc_bankCalendarLabel.gridy = 2;
		contentPanel.add(bankCalendarLabel, gbc_bankCalendarLabel);

		bankCalendarComboBox = new JComboBox(bankCalendars.toArray());
		bankCalendarComboBox.setMinimumSize(new Dimension(32, 25));
		bankCalendarComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		bankCalendarComboBox.setPreferredSize(new Dimension(32, 25));
		bankCalendarComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_bankCalendarComboBox = new GridBagConstraints();
		gbc_bankCalendarComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_bankCalendarComboBox.gridwidth = 2;
		gbc_bankCalendarComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_bankCalendarComboBox.gridx = 2;
		gbc_bankCalendarComboBox.gridy = 2;
		contentPanel.add(bankCalendarComboBox, gbc_bankCalendarComboBox);

		if(assignedSellSideBookSecurity.getBankCalendar()!=null)
			bankCalendarComboBox.setSelectedItem(assignedSellSideBookSecurity.getBankCalendar());
		else
			bankCalendarComboBox.setSelectedItem(assignedSellSideBookSecurity.getSellSideBook().getBankCalendar());
		
		bankCalendarComboBox.addActionListener(actionListener);

		JLabel daycountLabel = new JLabel("Day Count Basis");
		daycountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_daycountLabel = new GridBagConstraints();
		gbc_daycountLabel.anchor = GridBagConstraints.WEST;
		gbc_daycountLabel.insets = new Insets(0, 25, 5, 5);
		gbc_daycountLabel.gridx = 0;
		gbc_daycountLabel.gridy = 3;
		contentPanel.add(daycountLabel, gbc_daycountLabel);

		List<ComboBoxEntry> daycountEntries = new ArrayList<ComboBoxEntry>();
		daycountEntries.add(new ComboBoxEntry(0, "US (NASD) 30/360"));
		daycountEntries.add(new ComboBoxEntry(1, "Actual/actual"));
		daycountEntries.add(new ComboBoxEntry(2, "Actual/360"));
		daycountEntries.add(new ComboBoxEntry(3, "Actual/365"));
		daycountEntries.add(new ComboBoxEntry(4, "European 30/360"));

		daycountComboBox = new JComboBox(daycountEntries.toArray());
		daycountComboBox.setMinimumSize(new Dimension(32, 25));
		daycountComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		daycountComboBox.setPreferredSize(new Dimension(32, 25));
		daycountComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_priceBoundaryMethodComboBox = new GridBagConstraints();
		gbc_priceBoundaryMethodComboBox.gridwidth = 2;
		gbc_priceBoundaryMethodComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_priceBoundaryMethodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceBoundaryMethodComboBox.gridx = 2;
		gbc_priceBoundaryMethodComboBox.gridy = 3;
		contentPanel.add(daycountComboBox, gbc_priceBoundaryMethodComboBox);
		
		if(assignedSellSideBookSecurity.getDaycountConvention()!=null)
			daycountComboBox.setSelectedItem(new ComboBoxEntry(assignedSellSideBookSecurity.getDaycountConvention(), null));
		else
			daycountComboBox.setSelectedItem(new ComboBoxEntry(assignedSellSideBookSecurity.getSellSideBook().getDaycountConvention(), null));
		
		daycountComboBox.addActionListener(actionListener);
		
		
		JLabel calculationLabel = new JLabel("Price/Yield Calculation");
		calculationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_calculationLabel = new GridBagConstraints();
		gbc_calculationLabel.anchor = GridBagConstraints.WEST;
		gbc_calculationLabel.insets = new Insets(0, 25, 5, 5);
		gbc_calculationLabel.gridx = 0;
		gbc_calculationLabel.gridy = 4;
		contentPanel.add(calculationLabel, gbc_calculationLabel);

		List<ComboBoxEntry> calculationEntries = new ArrayList<ComboBoxEntry>();
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.NONE.ordinal(), "None"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.DEFAULT.ordinal(), "PRICE / YIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.DISC.ordinal(), "PRICEDISC / YIELDDISC"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.MAT.ordinal(), "PRICEMAT / YIELDMAT"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.ODDF.ordinal(), "ODDFPRICE / ODDFYIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.ODDL.ordinal(), "ODDLPRICE / ODDLYIELD"));
		calculationEntries.add(new ComboBoxEntry(SellSideBook.CalcMethod.TBILL.ordinal(), "TBILLPRICE / TBILLYIELD"));

		calculationComboBox = new JComboBox(calculationEntries.toArray());
		calculationComboBox.setMinimumSize(new Dimension(32, 25));
		calculationComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		calculationComboBox.setPreferredSize(new Dimension(32, 25));
		calculationComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_calculationComboBox = new GridBagConstraints();
		gbc_calculationComboBox.gridwidth = 2;
		gbc_calculationComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_calculationComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_calculationComboBox.gridx = 2;
		gbc_calculationComboBox.gridy = 4;
		contentPanel.add(calculationComboBox, gbc_calculationComboBox);

		CalcMethod calcMethod = CalcMethod.DEFAULT;

		if (assignedSellSideBookSecurity.getSellSideBook().getCalcMethod() != null)
			calcMethod = assignedSellSideBookSecurity.getSellSideBook().getCalcMethod();

		if (assignedSellSideBookSecurity.getCalcMethod() != null)
			calcMethod = assignedSellSideBookSecurity.getCalcMethod();

		switch (calcMethod) {
			case DEFAULT:
				calculationComboBox.setSelectedIndex(1);
				break;
			case DISC:
				calculationComboBox.setSelectedIndex(2);
				break;
			case MAT:
				calculationComboBox.setSelectedIndex(3);
				break;
			case NONE:
				calculationComboBox.setSelectedIndex(0);
				break;
			case ODDF:
				calculationComboBox.setSelectedIndex(4);
				break;
			case ODDL:
				calculationComboBox.setSelectedIndex(5);
				break;
			case TBILL:
				calculationComboBox.setSelectedIndex(6);
				break;
			default:
				calculationComboBox.setSelectedIndex(1);
				break;

		}
		
		calculationComboBox.addActionListener(actionListener);

		JLabel displayPriceLabel = new JLabel("Display Price");
		displayPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_displayPriceLabel = new GridBagConstraints();
		gbc_displayPriceLabel.anchor = GridBagConstraints.WEST;
		gbc_displayPriceLabel.insets = new Insets(0, 25, 5, 5);
		gbc_displayPriceLabel.gridx = 0;
		gbc_displayPriceLabel.gridy = 5;
		contentPanel.add(displayPriceLabel, gbc_displayPriceLabel);

		List<ComboBoxEntry> displayPriceEntries = new ArrayList<ComboBoxEntry>();
		displayPriceEntries.add(new ComboBoxEntry(false, "Decimal"));
		displayPriceEntries.add(new ComboBoxEntry(true, "Fraction"));

		displayPriceComboBox = new JComboBox(displayPriceEntries.toArray());
		displayPriceComboBox.setMinimumSize(new Dimension(32, 25));
		displayPriceComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		displayPriceComboBox.setPreferredSize(new Dimension(32, 25));
		displayPriceComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_displayPriceComboBox = new GridBagConstraints();
		gbc_displayPriceComboBox.gridwidth = 2;
		gbc_displayPriceComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_displayPriceComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_displayPriceComboBox.gridx = 2;
		gbc_displayPriceComboBox.gridy = 5;
		contentPanel.add(displayPriceComboBox, gbc_displayPriceComboBox);
		
		displayPriceComboBox.setSelectedIndex(0);

		if (assignedSellSideBookSecurity.getSellSideBook().getFractionalDisplay() != null && assignedSellSideBookSecurity.getSellSideBook().getFractionalDisplay()==true)
			displayPriceComboBox.setSelectedIndex(1);

		if (assignedSellSideBookSecurity.getFractionalDisplay() != null)
		{
			if(assignedSellSideBookSecurity.getFractionalDisplay()==true)
				displayPriceComboBox.setSelectedIndex(1);
			else
				displayPriceComboBox.setSelectedIndex(0);
		}
		
		displayPriceComboBox.addActionListener(actionListener);
		
		
		okButton = new JButton();

		if (!edit) {
			setTitle("Subscribe Security");
			okButton.setText("Add");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		}
		else {
			setTitle("Edit Subscribed Security");
			okButton.setText("Edit");
			okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit.png")));
		}

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 6;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				cancelled = false;
								
				if (securityTextField.getText().trim().length() > 0)
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setSecurity(securityTreeDialog.getSecurityForName(securityTextField.getText()));
				
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setSecurity(null);
				
				if(!AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.getSellSideBook().getDaycountConvention().equals(((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry()))
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setDaycountConvention((Integer) ((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry());
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setDaycountConvention(null);
				
				if (AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.getSellSideBook().getCalcMethod().ordinal() != ((Integer) ((ComboBoxEntry) calculationComboBox
						.getSelectedItem()).getEntry()))
					switch (calculationComboBox.getSelectedIndex()) {
						case 1:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.DEFAULT);
							break;
						case 2:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.DISC);
							break;
						case 3:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.MAT);
							break;
						case 4:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.ODDF);
							break;
						case 5:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.ODDL);
							break;
						case 6:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.TBILL);
							break;
						default:
							AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(CalcMethod.NONE);
							break;
					}
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setCalcMethod(null);
							
				if(!AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.getSellSideBook().getBankCalendar().equals(bankCalendarComboBox.getSelectedItem()))
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setBankCalendar((BankCalendar)bankCalendarComboBox.getSelectedItem());
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setBankCalendar(null);	
				
				Integer value = null;
				try {
					value = integerFormat.parse(valutaTextField.getText()).intValue();
				}
				catch (ParseException e1) {
					value = 0;
				}
				
				if(!AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.getSellSideBook().getFractionalDisplay().equals(((ComboBoxEntry) displayPriceComboBox.getSelectedItem()).getEntry()))
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setFractionalDisplay((Boolean) ((ComboBoxEntry) displayPriceComboBox.getSelectedItem()).getEntry());
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setFractionalDisplay(null);
				
				if(!AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.getSellSideBook().getValuta().equals(value))
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setValuta(value);
				else
					AssignedSellSideSecurityDialog.this.assignedSellSideBookSecurity.setValuta(null);	
				
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridwidth = 2;
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 6;
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
		
		FSecurity security = securityTreeDialog.getSecurityForName(securityTextField.getText());
		
		if (assignedSellSideBookSecurity.getSecurity() != null && !assignedSellSideBookSecurity.getSecurity().equals(security))
			dirty = true;
			
		if (assignedSellSideBookSecurity.getDaycountConvention() != null && !assignedSellSideBookSecurity.getDaycountConvention().equals(((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry()))
			dirty = true;
				
		if (assignedSellSideBookSecurity.getDaycountConvention() == null && !assignedSellSideBookSecurity.getSellSideBook().getDaycountConvention().equals(((ComboBoxEntry) daycountComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (assignedSellSideBookSecurity.getFractionalDisplay() != null && !assignedSellSideBookSecurity.getFractionalDisplay().equals(((ComboBoxEntry) displayPriceComboBox.getSelectedItem()).getEntry()))
			dirty = true;
				
		if (assignedSellSideBookSecurity.getFractionalDisplay() == null && !assignedSellSideBookSecurity.getSellSideBook().getFractionalDisplay().equals(((ComboBoxEntry) displayPriceComboBox.getSelectedItem()).getEntry()))
			dirty = true;
		
		if (assignedSellSideBookSecurity.getCalcMethod() != null
				&& assignedSellSideBookSecurity.getCalcMethod().ordinal() != ((Integer) ((ComboBoxEntry) calculationComboBox.getSelectedItem()).getEntry()))
			dirty = true;

		if (assignedSellSideBookSecurity.getCalcMethod() == null
				&& assignedSellSideBookSecurity.getSellSideBook().getCalcMethod().ordinal() != ((Integer) ((ComboBoxEntry) calculationComboBox.getSelectedItem())
						.getEntry()))
			dirty = true;
		
		if (assignedSellSideBookSecurity.getBankCalendar() != null && !assignedSellSideBookSecurity.getBankCalendar().equals(bankCalendarComboBox.getSelectedItem()));
			dirty = true;
		
		if (assignedSellSideBookSecurity.getBankCalendar() == null && !assignedSellSideBookSecurity.getSellSideBook().getBankCalendar().equals(bankCalendarComboBox.getSelectedItem()))
			dirty = true;
		
		Integer value = null;
		try {
			value = integerFormat.parse(valutaTextField.getText()).intValue();
		}
		catch (ParseException e) {
			value = 0;
		}
		
		if (assignedSellSideBookSecurity.getValuta() != null && !assignedSellSideBookSecurity.getValuta().equals(value));
			dirty = true;
	
		if (assignedSellSideBookSecurity.getValuta() == null && !assignedSellSideBookSecurity.getSellSideBook().getValuta().equals(value))
			dirty = true;
		
		if(security==null)
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
	 * Gets the assigned sell side book security.
	 *
	 * @return the assigned sell side book security
	 */
	public AssignedSellSideBookSecurity getAssignedSellSideBookSecurity() {
		
		return assignedSellSideBookSecurity;
	}
}
