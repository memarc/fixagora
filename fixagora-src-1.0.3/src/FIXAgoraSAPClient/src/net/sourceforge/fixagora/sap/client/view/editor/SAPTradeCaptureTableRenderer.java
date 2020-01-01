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
package net.sourceforge.fixagora.sap.client.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.fixagora.basis.client.view.GradientLabel;
import net.sourceforge.fixagora.sap.client.model.editor.SAPTradeCaptureCellValue;

/**
 * The Class SAPTradeCaptureTableRenderer.
 */
public class SAPTradeCaptureTableRenderer implements TableCellRenderer {

	private Color backGround1 = new Color(255, 243, 204);

	private Color backGround2 = new Color(255, 236, 179);

	private SAPTradeCaptureEditor sapTradeCaptureEditor;

	/**
	 * Instantiates a new sAP trade capture table renderer.
	 *
	 * @param sapTradeCaptureEditor the sap trade capture editor
	 */
	public SAPTradeCaptureTableRenderer(SAPTradeCaptureEditor sapTradeCaptureEditor) {

		super();
		this.sapTradeCaptureEditor = sapTradeCaptureEditor;

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		if (value instanceof SAPTradeCaptureCellValue) {

			SAPTradeCaptureCellValue sapTradeCaptureCellValue = (SAPTradeCaptureCellValue) value;

			if (column == 0) {
				GradientLabel statusLabel = new GradientLabel(sapTradeCaptureCellValue.getColor().darker(), sapTradeCaptureCellValue.getColor());
				statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
				statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
				statusLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
				statusLabel.setText(sapTradeCaptureCellValue.getValue());
				return statusLabel;
			}
			else {
				
				int colIndex = column;
				
				if(colIndex>1&&!sapTradeCaptureEditor.getSAPTradeCapture().isSetExchange())
					colIndex++;
				
				if(colIndex>12&&!sapTradeCaptureEditor.getSAPTradeCapture().isSetTrader())
					colIndex++;
				
				Color borderColor = backGround1;

				if (row % 2 != 0)
					borderColor = backGround2;
				
				JPanel jPanel = new JPanel();
				jPanel.setBorder(new EmptyBorder(4, 5, 4, 5));
				jPanel.setBackground(borderColor);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.rowHeights = new int[] { 0};
				if(colIndex>6&&colIndex<10)
					gbl_panel.columnWeights = new double[] { 1.0, 0.0 };
				else
					gbl_panel.columnWeights = new double[] { 0.0, 1.0 };
				gbl_panel.rowWeights = new double[] { 0.0};
				jPanel.setLayout(gbl_panel);
				
				JLabel jLabel1 = new JLabel();
				jLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
				jLabel1.setOpaque(false);
				jLabel1.setText(sapTradeCaptureCellValue.getValue());
				if(colIndex>6&&colIndex<10)
					jLabel1.setHorizontalAlignment(JTextField.RIGHT);
				GridBagConstraints gbc_jLabel1 = new GridBagConstraints();
				gbc_jLabel1.insets = new Insets(0, 0, 0, 0);
				gbc_jLabel1.fill = GridBagConstraints.BOTH;
				gbc_jLabel1.gridx = 0;
				gbc_jLabel1.gridy = 0;
				jPanel.add(jLabel1, gbc_jLabel1);
				
				JLabel jLabel2 = new JLabel();
				jLabel2.setFont(new Font("Dialog", Font.PLAIN, 12));
				jLabel2.setOpaque(false);
				jLabel2.setForeground(Color.LIGHT_GRAY);
				jLabel2.setText(sapTradeCaptureCellValue.getAdditionalInfo());
				GridBagConstraints gbc_jTextField2 = new GridBagConstraints();
				gbc_jTextField2.insets = new Insets(0, 5, 0, 0);
				gbc_jTextField2.fill = GridBagConstraints.BOTH;
				gbc_jTextField2.gridx = 1;
				gbc_jTextField2.gridy = 0;
				jPanel.add(jLabel2, gbc_jTextField2);
				
				return jPanel;
			}

		}

		return new JLabel();

	}

}
