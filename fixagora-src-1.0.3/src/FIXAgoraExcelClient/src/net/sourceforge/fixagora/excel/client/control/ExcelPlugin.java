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
package net.sourceforge.fixagora.excel.client.control;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.model.PluginInfo;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.excel.client.view.editor.ExcelTradeCaptureEditor;
import net.sourceforge.fixagora.excel.client.view.editor.ExcelUploadDockable;
import net.sourceforge.fixagora.excel.shared.communication.AbstractExcelResponse;
import net.sourceforge.fixagora.excel.shared.communication.ExcelResponses;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListRequest;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListResponse;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCapture;

import org.apache.log4j.Logger;

import bibliothek.gui.Dockable;

/**
 * The Class ExcelPlugin.
 */
public class ExcelPlugin implements FIXAgoraPlugin, ExcelResponses {

	private BasisClientConnector basisClientConnector = null;

	private MainPanel mainPanel = null;

	private static Logger log = Logger.getLogger(ExcelPlugin.class);

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getExtraJMenuItem(net.sourceforge.fixagora.basis.client.view.MainPanel)
	 */
	@Override
	public JMenuItem getExtraJMenuItem(MainPanel mainPanel) {

		if (this.mainPanel == null)
			this.mainPanel = mainPanel;

		final JMenu excelMenu = new JMenu("Excel \u00a9");

		JMenuItem downMenuItem = new JMenuItem("Security Upload");
		downMenuItem.setEnabled(true);
		downMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		excelMenu.add(downMenuItem);

		downMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (basisClientConnector != null) {
					final WaitDialog waitDialog = new WaitDialog(null, "Open Excel Upload");

					final SwingWorker<AbstractResponse, Void> swingWorker = new SwingWorker<AbstractResponse, Void>() {

						@Override
						protected AbstractResponse doInBackground() throws Exception {

							return basisClientConnector.sendRequest(new SecurityListRequest(RequestIDManager.getInstance().getID()));

						}

						@Override
						protected void done() {

							super.done();
							waitDialog.setVisible(false);

						}

					};

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							waitDialog.setVisible(true);
							swingWorker.execute();

							try {
								AbstractResponse abstractResponse = swingWorker.get();
								if (abstractResponse instanceof SecurityListResponse) {
									SecurityListResponse securityListResponse = (SecurityListResponse) abstractResponse;
									if (securityListResponse != null && basisClientConnector.getFUser().canWrite(securityListResponse.getSecurityGroup())) {

										try {

											Dockable dockable = ExcelPlugin.this.mainPanel.getDockFrontend().getDockable("excelupload");
											if (dockable == null) {
												dockable = new ExcelUploadDockable(ExcelPlugin.this.mainPanel, basisClientConnector, securityListResponse);
												ExcelPlugin.this.mainPanel.addPluginDockable("excelupload", dockable);
											}

											ExcelPlugin.this.mainPanel.getDockFrontend().getController().setFocusedDockable(dockable, true);

											//
										}
										catch (Exception e1) {
											log.error("Bug", e1);
										}
									}
								}
							}
							catch (Exception e1) {
								log.error("Bug", e1);
							}

						}
					});

				}

			}
		});

		return excelMenu;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getOrderNumber()
	 */
	@Override
	public double getOrderNumber() {

		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#setBasisClientConnector(net.sourceforge.fixagora.basis.client.control.BasisClientConnector)
	 */
	@Override
	public void setBasisClientConnector(BasisClientConnector basisClientConnector) {

		this.basisClientConnector = basisClientConnector;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getAbstractBusinessEditor(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject, net.sourceforge.fixagora.basis.client.view.MainPanel)
	 */
	@Override
	public AbstractBusinessObjectEditor getAbstractBusinessEditor(AbstractBusinessObject abstractBusinessObject, MainPanel mainPanel) {

		if (this.mainPanel == null)
			this.mainPanel = mainPanel;

		if (abstractBusinessObject instanceof ExcelTradeCapture)
			return new ExcelTradeCaptureEditor((ExcelTradeCapture) abstractBusinessObject, mainPanel, basisClientConnector);

		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

		if (abstractResponse instanceof AbstractExcelResponse) {
			((AbstractExcelResponse) abstractResponse).handleAbstractExcelResponse(this);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.excel.shared.communication.ExcelResponses#onExcelTradeCaptureEntryResponse(net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryResponse)
	 */
	@Override
	public void onExcelTradeCaptureEntryResponse(ExcelTradeCaptureEntryResponse excelTradeCaptureEntryResponse) {

		if (mainPanel != null) {
			for (Dockable dockable : mainPanel.getNamedDockables())
				if (dockable instanceof ExcelTradeCaptureEditor)
					((ExcelTradeCaptureEditor) dockable).onExcelTradeCaptureEntryResponse(excelTradeCaptureEntryResponse);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin#getPluginInfo()
	 */
	@Override
	public PluginInfo getPluginInfo() {

		return new PluginInfo("FIX Agora Excel Integration", "Version 1.0.3  -  Copyright (C) 2012-2015  Alexander Pinnow", "alexander.pinnow@googlemail.com");
	}

}
