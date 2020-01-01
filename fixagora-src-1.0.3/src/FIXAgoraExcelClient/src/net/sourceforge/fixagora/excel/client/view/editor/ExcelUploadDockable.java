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
package net.sourceforge.fixagora.excel.client.view.editor;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListResponse;
import bibliothek.gui.dock.DefaultDockable;

/**
 * The Class ExcelUploadDockable.
 */
public class ExcelUploadDockable extends DefaultDockable {

	private MainPanel mainPanel = null;

	/** The basis client connector. */
	protected BasisClientConnector basisClientConnector = null;

	/** The scroll pane. */
	protected JScrollPane scrollPane = null;

	/** The visible. */
	protected boolean visible = true;

	private Color blue = new Color(204, 216, 255);

	/**
	 * Instantiates a new excel upload dockable.
	 *
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 * @param securityListResponse the security list response
	 */
	public ExcelUploadDockable(final MainPanel mainPanel, BasisClientConnector basisClientConnector, SecurityListResponse securityListResponse) {

		super();
		
		setTitleIcon(new ImageIcon(ExcelUploadDockable.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_sent_mail.png")));
		setTitleText("Excel \u00a9 Security Upload");

		this.mainPanel = mainPanel;
		this.basisClientConnector = basisClientConnector;

		JPanel jPanel = new JPanel(){

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();


				final GradientPaint gradientPaint2 = new GradientPaint(width / 2.F, height - 27, blue, width / 2.F, height, blue.darker());

				graphics2D.setPaint(gradientPaint2);
				graphics2D.fillRoundRect(0, height - 31, width - 1 , 30, 14, 14);

				graphics2D.setColor(Color.GRAY);
				graphics2D.drawRoundRect(0, 0 , width-1  , height-1, 14, 14);

				getUI().paint(graphics2D, this);
			}

		};
		
		add(jPanel);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1d};
		gridBagLayout.rowWeights = new double[]{1d};
		jPanel.setLayout(gridBagLayout);
		ExcelImportPanel excelImportPanel = new ExcelImportPanel(basisClientConnector, securityListResponse);
		excelImportPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
		GridBagConstraints gbc_excelImportPanel = new GridBagConstraints();
		gbc_excelImportPanel.anchor = GridBagConstraints.WEST;
		gbc_excelImportPanel.fill = GridBagConstraints.BOTH;
		gbc_excelImportPanel.insets = new Insets(0, 0, 24, 0);
		gbc_excelImportPanel.gridx = 0;
		gbc_excelImportPanel.gridy = 0;
		jPanel.add(excelImportPanel, gbc_excelImportPanel);
		
	}

	/**
	 * Handle focus.
	 */
	public void handleFocus() {

	}




	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public MainPanel getMainPanel() {

		return mainPanel;
	}

	

}
