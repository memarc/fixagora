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
package net.sourceforge.agora.simulator.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.agora.simulator.model.BlotterEntry;
import net.sourceforge.agora.simulator.model.BlotterText;

/**
 * The Class FIXAgoraSimulatorTableRenderer.
 */
public class FIXAgoraSimulatorTableRenderer implements TableCellRenderer {

	private Color backGround1 = new Color(255, 243, 204);

	private Color backGround2 = new Color(255, 236, 179);

	private ImageIcon successIcon = new ImageIcon(
			FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/agt_action_success.png"));

	private ImageIcon infoIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/info.png"));

	private ImageIcon warningIcon = new ImageIcon(
			FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/messagebox_warning.png"));

	private ImageIcon errorIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/stop.png"));

	private ImageIcon mailIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/mail_generic.png"));
	
	private ImageIcon runIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/runprog.png"));
	
	/**
	 * Instantiates a new fIX agora simulator table renderer.
	 */
	public FIXAgoraSimulatorTableRenderer() {

		super();

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column) {

		Font font = new Font("Dialog", Font.PLAIN, 12);

		Color borderColor = backGround1;

		if (row % 2 != 0)
			borderColor = backGround2;
		
		JPanel jPanel = new JPanel();
		jPanel.setBorder(new EmptyBorder(4, 5, 4, 5));
		jPanel.setBackground(borderColor);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.rowHeights = new int[] { 0 };
		gbl_panel.rowWeights = new double[] { 0.0 };
		jPanel.setLayout(gbl_panel);

		if (column == 0 && value instanceof BlotterEntry) {
			BlotterEntry blotterEntry = (BlotterEntry) value;
			JLabel jLabel = new JLabel();
			jLabel.setOpaque(false);
			jLabel.setBorder(null);
			GridBagConstraints gbc_jTextField1 = new GridBagConstraints();
			gbc_jTextField1.insets = new Insets(0, 0, 0, 0);
			gbc_jTextField1.fill = GridBagConstraints.BOTH;
			gbc_jTextField1.gridx = 0;
			gbc_jTextField1.gridy = 0;
			jPanel.add(jLabel, gbc_jTextField1);

			switch (blotterEntry.getLevelIcon()) {
				case ERROR:
					jLabel.setIcon(errorIcon);
					break;
				case INFO:
					jLabel.setIcon(infoIcon);
					break;
				case WARNING:
					jLabel.setIcon(warningIcon);
					break;
				case SUCESS:
					jLabel.setIcon(successIcon);
					break;
				case MESSAGE:
					jLabel.setIcon(mailIcon);
					break;
				case RUN:
					jLabel.setIcon(runIcon);
					break;
				case BLANK:
					break;
			}
		}

		if (column > 0 && value instanceof BlotterEntry) {
			BlotterEntry blotterEntry = (BlotterEntry) value;
			gbl_panel.columnWeights = new double[blotterEntry.getBlotterTexts().size() + 1];
			for (int i = 0; i < blotterEntry.getBlotterTexts().size(); i++) {
				BlotterText blotterText = blotterEntry.getBlotterTexts().get(i);
				JLabel jLabel = new JLabel();
				jLabel.setFont(font);
				jLabel.setOpaque(false);
				jLabel.setText(blotterText.getText());
				jLabel.setForeground(blotterText.getColor());
				FontMetrics fontMetrics = table.getFontMetrics(new Font("Dialog", Font.PLAIN, 12));
				jLabel.setPreferredSize(new Dimension(fontMetrics.stringWidth(blotterText.getText()),20));
				jLabel.setMinimumSize(new Dimension(fontMetrics.stringWidth(blotterText.getText()),20));
				jLabel.setBorder(null);
				GridBagConstraints gbc_jTextField1 = new GridBagConstraints();
				if(i==0)
					gbc_jTextField1.insets = new Insets(0, blotterEntry.getOffset(), 0, 0);
				else
					gbc_jTextField1.insets = new Insets(0, 0, 0, 0);
				gbc_jTextField1.fill = GridBagConstraints.BOTH;
				gbc_jTextField1.gridx = i;
				gbc_jTextField1.gridy = 0;
				jPanel.add(jLabel, gbc_jTextField1);
				gbl_panel.columnWeights[i] = 0.0;
			}
			JLabel jLabel = new JLabel();
			jLabel.setOpaque(false);
			jLabel.setBorder(null);
			GridBagConstraints gbc_jTextField1 = new GridBagConstraints();
			gbc_jTextField1.insets = new Insets(0, 0, 0, 0);
			gbc_jTextField1.fill = GridBagConstraints.BOTH;
			gbc_jTextField1.gridx = blotterEntry.getBlotterTexts().size();
			gbc_jTextField1.gridy = 0;
			jPanel.add(jLabel, gbc_jTextField1);
			gbl_panel.columnWeights[blotterEntry.getBlotterTexts().size()] = 1.0;

		}
		
		jPanel.setOpaque(true);
		return jPanel;

	}

}
