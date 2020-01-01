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
package net.sourceforge.fixagora.tradecapture.client.view.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.RolePermissionEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.tradecapture.shared.communication.CloseTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureRequest;
import net.sourceforge.fixagora.tradecapture.shared.communication.OpenTradeCaptureResponse;
import net.sourceforge.fixagora.tradecapture.shared.communication.TradeCaptureEntryResponse;
import net.sourceforge.fixagora.tradecapture.shared.persistence.TradeCapture;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.dockable.DockableStateEvent;
import bibliothek.gui.dock.dockable.DockableStateListener;

/**
 * The Class TradeCaptureEditor.
 */
public class TradeCaptureEditor extends AbstractBusinessObjectEditor {

	private TradeCapture tradeCapture = null;

	private final ImageIcon folderBlue = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_blue.png"));

	private final ImageIcon folderRed = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_red.png"));

	private TradeCaptureEditorMasterData tradeCaptureEditorMasterData = null;
		
	private TradeCaptureEditorSourceTarget tradeCaptureEditorAssignedSources = null;
	
	private TradeCaptureEditorMonitor tradeCaptureEditorMonitor = null;

	private RolePermissionEditor rolePermissionEditor = null;
	
	private Icon originalIcon;
		
	/**
	 * Instantiates a new trade capture editor.
	 *
	 * @param tradeCapture the trade capture
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public TradeCaptureEditor(TradeCapture tradeCapture, MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super(tradeCapture, mainPanel, basisClientConnector);
		this.tradeCapture = tradeCapture;
		addDockableStateListener(new DockableStateListener() {
			
			@Override
			public void changed(DockableStateEvent arg0) {
			
				if(tradeCaptureEditorMonitor!=null&&isDockableVisible())
					tradeCaptureEditorMonitor.clearUpdated();
				
				updateIcon();
				
				
			}
		});
		initContent();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#initContent()
	 */
	@Override
	public void initContent() {

		Dockable frontDockable = stackDockStation.getFrontDockable();

		while(stackDockStation.getDockableCount()>0)
			stackDockStation.remove(0);
		
		tradeCaptureEditorMonitor = new TradeCaptureEditorMonitor(this);

		DefaultDockable tradeCaptureEditorMonitorDockable = new DefaultDockable(tradeCaptureEditorMonitor);
		tradeCaptureEditorMonitorDockable.setFactoryID("Trades");
		tradeCaptureEditorMonitorDockable.setTitleText("Trades");
		tradeCaptureEditorMonitorDockable.setTitleIcon(folderBlue);

		stackDockStation.drop(tradeCaptureEditorMonitorDockable);
		
		tradeCaptureEditorMasterData = new TradeCaptureEditorMasterData(this);
		
		DefaultDockable tradeCaptureEditorMasterDataDockable = new DefaultDockable(tradeCaptureEditorMasterData);
		tradeCaptureEditorMasterDataDockable.setFactoryID("Master Data");
		tradeCaptureEditorMasterDataDockable.setTitleText("Master Data");
		tradeCaptureEditorMasterDataDockable.setTitleIcon(folderBlue);
		
		stackDockStation.drop(tradeCaptureEditorMasterDataDockable);
		
		if (frontDockable == null)
			if (tradeCapture.getId() == 0)
				frontDockable = tradeCaptureEditorMasterDataDockable;
			else
				frontDockable = tradeCaptureEditorMonitorDockable;
		
		tradeCaptureEditorAssignedSources = new TradeCaptureEditorSourceTarget(this);
		
		DefaultDockable tradeCaptureEditorAssignedSourcesDockable = new DefaultDockable(tradeCaptureEditorAssignedSources);
		tradeCaptureEditorAssignedSourcesDockable.setFactoryID("Sources / Targets");
		tradeCaptureEditorAssignedSourcesDockable.setTitleText("Sources / Targets");
		tradeCaptureEditorAssignedSourcesDockable.setTitleIcon(folderBlue);
		
		stackDockStation.drop(tradeCaptureEditorAssignedSourcesDockable);

		RolesResponse rolesResponse = null;
		AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));
		if (abstractResponse instanceof RolesResponse) {
			rolesResponse = (RolesResponse) abstractResponse;
			rolePermissionEditor = new RolePermissionEditor(this, rolesResponse.getRoles());

		}
		else if (abstractResponse instanceof ErrorResponse) {
			rolePermissionEditor = new RolePermissionEditor(this, new ArrayList<FRole>());
			ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
			JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		DefaultDockable newRolePermissionEditorDockable = new DefaultDockable(rolePermissionEditor);
		newRolePermissionEditorDockable.setFactoryID("Roles");
		newRolePermissionEditorDockable.setTitleText("Roles");
		newRolePermissionEditorDockable.setTitleIcon(folderRed);

		stackDockStation.drop(newRolePermissionEditorDockable);

		for (int i = 0; i < stackDockStation.getDockableCount(); i++) {
			
			if (stackDockStation.getDockable(i).getTitleText().equals(frontDockable.getTitleText()))
				stackDockStation.setFrontDockable(stackDockStation.getDockable(i));
		}
		
		checkDirty();		
	}



	
	/**
	 * Gets the trade capture.
	 *
	 * @return the trade capture
	 */
	public TradeCapture getTradeCapture() {
	
		return tradeCapture;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#save()
	 */
	@Override
	public void save() {

		tradeCaptureEditorMasterData.save();
		tradeCaptureEditorAssignedSources.save();
		rolePermissionEditor.save();

		super.save();
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#closeAbstractBusinessObjectEditor()
	 */
	@Override
	public void closeAbstractBusinessObjectEditor() {

		if (tradeCapture.getId() != 0)
			basisClientConnector.sendRequest(new CloseTradeCaptureRequest(tradeCapture.getId(), RequestIDManager.getInstance().getID()));
		super.closeAbstractBusinessObjectEditor();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#postConstruct()
	 */
	@Override
	public void postConstruct() {

		if (tradeCapture.getId() != 0)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);		
			OpenTradeCaptureResponse openTradeCaptureResponse = (OpenTradeCaptureResponse)basisClientConnector.sendRequest(new OpenTradeCaptureRequest(tradeCapture.getId(),calendar.getTimeInMillis(), RequestIDManager.getInstance().getID()));
			tradeCaptureEditorMonitor.onOpenTradeCaptureResponse(openTradeCaptureResponse);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#checkDirty()
	 */
	@Override
	public void checkDirty() {

		boolean dirty = false;
		
		if(tradeCaptureEditorMasterData!=null&&tradeCaptureEditorMasterData.isDirty())
			dirty = true;
				
		if(tradeCaptureEditorAssignedSources!=null&&tradeCaptureEditorAssignedSources.isDirty())
			dirty = true;
		
		if(rolePermissionEditor!=null&&rolePermissionEditor.isDirty())
			dirty = true;
		
		boolean consistent = true;
		
		if(tradeCaptureEditorMasterData!=null&&!tradeCaptureEditorMasterData.isConsistent())
			consistent = false;
		
		setConsistent(consistent);
		
		setDirty(dirty);		
	}
	

	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateContent(net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject)
	 */
	@Override
	public void updateContent(AbstractBusinessObject abstractBusinessObject) {

		super.updateContent(abstractBusinessObject);
		
		this.tradeCapture = (TradeCapture)abstractBusinessObject;
		
		tradeCaptureEditorMonitor.updateContent();
		
		if (tradeCapture.getId() != 0)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);		
			OpenTradeCaptureResponse openTradeCaptureResponse = (OpenTradeCaptureResponse)basisClientConnector.sendRequest(new OpenTradeCaptureRequest(tradeCapture.getId(),calendar.getTimeInMillis(), RequestIDManager.getInstance().getID()));
			tradeCaptureEditorMonitor.onOpenTradeCaptureResponse(openTradeCaptureResponse);
		}
		
		tradeCaptureEditorMasterData.updateContent();
				
		tradeCaptureEditorAssignedSources.updateContent();

		rolePermissionEditor.updateContent(null);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#updateRoles(java.util.List)
	 */
	@Override
	public void updateRoles(List<FRole> roles) {

		rolePermissionEditor.updateContent(roles);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor#getAbstractBusinessObject()
	 */
	@Override
	public AbstractBusinessObject getAbstractBusinessObject() {
		
		return tradeCapture;
	}

	/**
	 * Stop warning.
	 */
	public synchronized void stopWarning() {

		if(originalIcon!=null)
			setTitleIcon(originalIcon);
		
	}
	
	/**
	 * On trade capture entry response.
	 *
	 * @param tradeCaptureEntryResponse the trade capture entry response
	 */
	public synchronized void onTradeCaptureEntryResponse(TradeCaptureEntryResponse tradeCaptureEntryResponse) {

		if(tradeCaptureEntryResponse.getTradeCaptureEntry().getTradeCapture()==tradeCapture.getId())
		{
			tradeCaptureEditorMonitor.onTradeCaptureEntryResponse(tradeCaptureEntryResponse);
		}
	}

	/**
	 * Update icon.
	 */
	public void updateIcon() {

		if(originalIcon==null)
			originalIcon=getTitleIcon();
		
		
		if(isDockableVisible()||tradeCaptureEditorMonitor.getUpdated()==0)
		{
			setTitleIcon(originalIcon);
			return;
		}
		
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = (Graphics2D) image.createGraphics();
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
		GradientPaint gradientPaint = new GradientPaint(0, 0, Color.RED, 16, 16,Color.BLACK);
		
		graphics2d.setPaint(gradientPaint);
		
		graphics2d.fillOval(0, 0, 16, 16);
		
		
		graphics2d.setColor(Color.WHITE);

		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int size = 12;
		
		int yoffset = 0;
		
		int updated = tradeCaptureEditorMonitor.getUpdated();
		
		if(updated>9)
		{
			size=10;
			yoffset++;
		}
		
		Font font = new Font("Dialog", Font.BOLD, size);
		
		FontMetrics metrics = getComponent().getFontMetrics(font);
		String count = Integer.toString(updated);
		int offset = (16-metrics.stringWidth(count))/2;
		if(offset<0)
			offset=0;
		graphics2d.setFont(font);
		graphics2d.drawString(count, offset, size+yoffset);
		
		setTitleIcon(new ImageIcon(image));
		
	}

	
	
}
