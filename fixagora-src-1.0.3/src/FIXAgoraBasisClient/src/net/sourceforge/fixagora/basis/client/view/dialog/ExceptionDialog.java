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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

/**
 * The Class ExceptionDialog.
 */
public class ExceptionDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();

	private JTextPane textPane = null;
	
	private static ExceptionDialog exceptionDialog = null;

	/**
	 * Instantiates a new exception dialog.
	 */
	public ExceptionDialog() {

		setBounds(100, 100, 450, 300);
		setResizable(false);
		setModal(false);
		setIconImage(new ImageIcon(ExceptionDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setTitle("Error");
		setAlwaysOnTop(true);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		JLabel titelLabel = new JLabel("An unexpected error has occurred.");
		titelLabel.setIcon(new ImageIcon(ExceptionDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/messagebox_warning.png")));
		titelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_titelLabel = new GridBagConstraints();
		gbc_titelLabel.anchor = GridBagConstraints.WEST;
		gbc_titelLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titelLabel.gridx = 0;
		gbc_titelLabel.gridy = 0;
		contentPanel.add(titelLabel, gbc_titelLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPanel.add(scrollPane, gbc_scrollPane);

		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		JPanel buttonPane = new JPanel();
		
		buttonPane.setBorder(new EmptyBorder(0, 0, 15, 5));
		buttonPane.setBackground(new Color(204, 216, 255));
		final FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
		fl_buttonPane.setHgap(10);
		fl_buttonPane.setVgap(0);
		buttonPane.setLayout(fl_buttonPane);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		final JButton cancelButton = new JButton("Close");
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setActionCommand("Cancel");
		cancelButton.setIcon(new ImageIcon(ExceptionDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				textPane.setText(null);
				ExceptionDialog.this.setVisible(false);

			}
		});
		buttonPane.add(cancelButton);

	}
	
	/**
	 * Show exception.
	 *
	 * @param exception the exception
	 */
	public static void showException(Exception exception)
	{
		if(exceptionDialog==null)
			exceptionDialog = new ExceptionDialog();
		StringBuffer stringBuffer = new StringBuffer(exceptionDialog.textPane.getText());
		stringBuffer.append(exception.getMessage());
		for(StackTraceElement stackTraceElement: exception.getStackTrace())
		{
			stringBuffer.append(stackTraceElement);
			stringBuffer.append("\n");
		}
		exceptionDialog.textPane.setText(stringBuffer.toString());
		exceptionDialog.setVisible(true);
	}

}
