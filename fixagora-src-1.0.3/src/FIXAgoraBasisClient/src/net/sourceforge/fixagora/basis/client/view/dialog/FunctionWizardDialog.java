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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.sourceforge.fixagora.basis.client.model.editor.FunctionDescription;
import net.sourceforge.fixagora.basis.client.view.document.FormulaFilter;

import org.apache.poi.ss.formula.WorkbookEvaluator;

/**
 * The Class FunctionWizardDialog.
 */
public class FunctionWizardDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private JButton okButton;
	private int columnWidth = 0;
	private JList list;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTextArea textArea;
	private Color blue = new Color(204, 216, 255);
	private Color darkBlue = new Color(143, 169, 255);
	private JScrollPane scrollPane_2;
	private JPanel panel;

	/**
	 * Instantiates a new function wizard dialog.
	 *
	 * @param formula the formula
	 */
	public FunctionWizardDialog(String formula) {

		
		setBounds(100, 100, 658, 501);
		setBackground(blue);
		setIconImage(new ImageIcon(FunctionWizardDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(true);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(blue);
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 25, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(150, 3));
		scrollPane.setMinimumSize(new Dimension(150, 3));
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.insets = new Insets(25, 25, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPanel.add(scrollPane, gbc_scrollPane);
		
		List<String> functions = new ArrayList<String>(WorkbookEvaluator.getSupportedFunctionNames());
		functions.add("YIELD");
		functions.add("PRICE");
		functions.add("ACCRINT");
		functions.add("ACCRINTM");
		functions.add("TBILLEQ");
		functions.add("TBILLYIELD");
		functions.add("TBILLPRICE");
		functions.add("PRICEMAT");
		functions.add("YIELDMAT");
		functions.add("ODDFYIELD");
		functions.add("ODDFPRICE");
		functions.add("ODDLYIELD");
		functions.add("ODDLPRICE");
		functions.add("DURATION");
		functions.add("MDURATION");
		functions.add("COUPDAYS");
		functions.add("COUPDAYBS");
		functions.add("COUPDAYSNC");
		functions.add("COUPNUM");
		functions.add("COUPPCD");
		functions.add("COUPNCD");
		functions.add("DISC");
		functions.add("PRICEDISC");
		functions.add("YIELDDISC");
		functions.add("VALUTA");
		functions.add("MATURITY");
		functions.add("COUPONRATE");
		functions.add("SPLINE");
		functions.add("LINEAR");
		functions.add("SLOPE");
		functions.add("INTERCEPT");
		functions.add("INTRATE");
		functions.add("EFFECT");
		functions.add("NOMINAL");
		functions.add("INTERESTACCRUALDATE");
		functions.add("ISSUEDATE");
		
		for(String function: WorkbookEvaluator.getSupportedFunctionNames())
		{
			if(FunctionDescription.getHtml(function)==null)
				functions.remove(function);
		}
		Collections.sort(functions);
		
		list = new JList(functions.toArray());
		list.setFont(new Font("Dialog", Font.PLAIN, 12));
		list.setBackground(new Color(255, 243, 204));
		list.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		scrollPane.setViewportView(list);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				textPane.setText(FunctionDescription.getHtml((String)list.getSelectedValue()));
				
				 Font font = UIManager.getFont("Label.font");
			        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
			                "font-size: " + font.getSize() + "pt; }";
			        ((HTMLDocument)textPane.getDocument()).getStyleSheet().addRule(bodyRule);
			        
			     textPane.setCaretPosition(0);
			}
			
		});

		okButton = new JButton();
		okButton.setMaximumSize(new Dimension(100, 25));
		okButton.setMinimumSize(new Dimension(100, 25));

		setTitle("Formula Wizard");
		
		panel = new JPanel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);
				
				String text = "Source: OpenOffice.org Wiki - Apache License, Version 2.0";
				
				FontMetrics fontMetrics = getFontMetrics(new Font("Dialog", Font.PLAIN, 10));
				final int height = fontMetrics.stringWidth(text);
				
				graphics2D.setFont(new Font("Dialog", Font.PLAIN, 10));
				graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				graphics2D.setPaintMode();
				graphics2D.setColor(darkBlue);
				graphics2D.rotate(-Math.PI/2.0);
				graphics2D.drawString(text,-height,20);
				
				getUI().paint(graphics2D, this);
			}
		};
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(25, 10));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(25, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		contentPanel.add(panel, gbc_panel);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 3;
		gbc_scrollPane_1.insets = new Insets(25, 0, 5, 25);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 0;
		contentPanel.add(scrollPane_1, gbc_scrollPane_1);
		
		textPane = new JTextPane();
		scrollPane_1.setViewportView(textPane);
		scrollPane_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textPane.setBorder(new EmptyBorder(0, 5, 5, 5));
		textPane.setContentType("text/plain");
		textPane.setEditorKit(new HTMLEditorKit());
		textPane.setBackground(new Color(204, 216, 255));
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane_2.setPreferredSize(new Dimension(0, 75));
		scrollPane_2.setMinimumSize(new Dimension(0, 75));
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridwidth = 3;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 25);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 2;
		gbc_scrollPane_2.gridy = 2;
		contentPanel.add(scrollPane_2, gbc_scrollPane_2);
		
		textArea = new JTextArea();
		textArea.setText("=");
		textArea.setNavigationFilter(new FormulaFilter(textArea));
		if(formula!=null)
			textArea.replaceSelection(formula);
		scrollPane_2.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(new Color(255, 243, 204));
		textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2)
				{
					Object object = list.getSelectedValue();
					if (object != null) {
						textArea.replaceSelection(object.toString() + "()");
						textArea.setCaretPosition(textArea.getCaretPosition()-1);
					}
				}
			}
			
			
		});
		
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2)
				{
					String text = textArea.getSelectedText();
					if(text!=null)
						list.setSelectedValue(text, true);
				}
			}			
		});
		
		list.setSelectedIndex(0);
		
		okButton.setText("Ok");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png")));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.fill = GridBagConstraints.VERTICAL;
		gbc_okButton.anchor = GridBagConstraints.EAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 2;
		gbc_okButton.gridy = 3;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cancelled = false;


				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(100, 25));
		cancelButton.setMaximumSize(new Dimension(100, 25));
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.fill = GridBagConstraints.VERTICAL;
		gbc_cancelButton.gridwidth = 2;
		gbc_cancelButton.anchor = GridBagConstraints.EAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 3;
		gbc_cancelButton.gridy = 3;
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
	 * Gets the column width.
	 *
	 * @return the column width
	 */
	public int getColumnWidth() {

		return columnWidth;
	}
	
	/**
	 * Gets the formula.
	 *
	 * @return the formula
	 */
	public String getFormula()
	{
		return textArea.getText().substring(1).replaceAll("\n", "");
	}
}
