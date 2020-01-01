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
package net.sourceforge.fixagora.basis.client.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.view.dialog.AcceptorTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.CounterpartyTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.InitiatorTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.SecurityTreeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.client.view.editor.BankCalendarEditor;
import net.sourceforge.fixagora.basis.client.view.editor.CounterpartyEditor;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultBusinessGroupEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FIXAcceptorEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FIXInitiatorEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FRoleEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FSecurityGroupEditor;
import net.sourceforge.fixagora.basis.client.view.editor.FUserEditor;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditor;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditorSheet.FormatAction;
import net.sourceforge.fixagora.basis.client.view.editor.TraderEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionariesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionariesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RefreshResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RolesResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.BankCalendar;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXAcceptor;
import net.sourceforge.fixagora.basis.shared.model.persistence.FIXInitiator;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;

import org.apache.log4j.Logger;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseColorScheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.ArchGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.LinePainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPanePainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.popup.ActionPopupMenu;
import bibliothek.gui.dock.action.popup.ActionPopupMenuFactory;
import bibliothek.gui.dock.event.VetoableDockFrontendEvent;
import bibliothek.gui.dock.event.VetoableDockFrontendListener;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.PaintableComponent;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

/**
 * The Class MainPanel.
 */
public class MainPanel extends SplitDockStation implements BasisClientConnectorListener, BasisResponses {

	private static final long serialVersionUID = 1L;

	private final Vector<MainPanelListener> mainPanelListeners = new Vector<MainPanelListener>();

	private DockFrontend dockFrontend = null;

	private BasisClientConnector basisClientConnector = null;

	private JFrame jFrame = null;

	private Color blue = new Color(204, 216, 255);

	private ImageIcon backImageIcon = null;

	private Image backImage = null;

	private ScreenDockStation screenDockStation = null;

	private SecurityTreeDialog securityTreeDialog = null;

	private CounterpartyTreeDialog counterpartyTreeDialog = null;

	private InitiatorTreeDialog fixInitiatorTreeDialog = null;

	private List<DataDictionary> dataDictionaries = null;

	private List<DataDictionary> completeDataDictionaries = null;

	private List<AbstractBusinessObject> businessComponents = new ArrayList<AbstractBusinessObject>();

	private KeyStroke stroke4;

	private ActionListener listener4;

	private boolean showingFormula = false;

	private Map<String, AbstractBusinessObject> namedBusinessObjectMap = new HashMap<String, AbstractBusinessObject>();

	private Map<Long, AbstractBusinessObject> businessObjectMap = new HashMap<Long, AbstractBusinessObject>();

	private AcceptorTreeDialog fixAcceptorTreeDialog;

	private List<BankCalendar> bankCalendars = new ArrayList<BankCalendar>();

	private LinkedBlockingQueue<AbstractResponse> linkedBlockingQueue = new LinkedBlockingQueue<AbstractResponse>();

	private boolean shutdown = false;

	/** The alpha. */
	float alpha = 0;

	private Thread thread;

	private Timer timer;

	private static Logger log = Logger.getLogger(MainPanel.class);

	/**
	 * Instantiates a new main panel.
	 *
	 * @param jFrame the j frame
	 * @param basisClientConnector the basis client connector
	 */
	public MainPanel(JFrame jFrame, final BasisClientConnector basisClientConnector) {

		super(false);

		fixInitiatorTreeDialog = new InitiatorTreeDialog(basisClientConnector);
		fixAcceptorTreeDialog = new AcceptorTreeDialog(basisClientConnector);

		this.jFrame = jFrame;
		this.basisClientConnector = basisClientConnector;

		backImageIcon = new ImageIcon(MainPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/logo.png"));
		backImage = backImageIcon.getImage();

		setOpaque(true);
		setBackground(new Color(204, 216, 255));
		setResizingEnabled(true);
		setExpandOnDoubleclick(false);
		setContinousDisplay(true);

		getComponent().addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {

				// TODO Auto-generated method stub

			}
		});

		getBasePane().setOpaque(true);
		getBasePane().setBackground(new Color(204, 216, 255));

		EclipseColorScheme colorScheme = new EclipseColorScheme() {

			@Override
			public void updateUI() {

				super.updateUI();

				setColor("stack.border", blue);
				setColor("stack.tab.border", blue);
				setColor("stack.tab.border.selected", blue);
				setColor("stack.tab.border.selected.focused", blue);
				setColor("stack.tab.top", blue.darker());
				setColor("stack.tab.top.selected", new Color(143, 169, 255));
				setColor("stack.tab.top.selected.focused", new Color(143, 169, 255));
				setColor("stack.tab.text", Color.WHITE);
				setColor("stack.tab.text.selected", Color.WHITE);
				setColor("stack.tab.text.selected.focused", Color.WHITE);
				setColor("stack.tab.bottom", blue);
			}
		};

		EclipseTheme eclipseTheme = new EclipseTheme();
		eclipseTheme.setColorScheme(colorScheme);

		BorderModifier borderModifier = new BorderModifier() {

			@Override
			public Border modify(Border arg0) {

				return BorderFactory.createEmptyBorder();
			}
		};

		dockFrontend = new DockFrontend(jFrame);
		dockFrontend.addRoot("split", this);
		dockFrontend.getController().setTheme(eclipseTheme);
		dockFrontend.getController().getProperties().set(EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true);
		dockFrontend.getController().getProperties().set(EclipseTheme.TAB_PAINTER, new BackgroundTabPainter());
		dockFrontend.getController().getThemeManager().setBorderModifier("dock.border.displayer.eclipse.content", borderModifier);
		dockFrontend.getController().getThemeManager().setBorderModifier("dock.border.stack.eclipse.content", borderModifier);

		dockFrontend.getController().getThemeManager().setBackgroundPaint(ThemeManager.BACKGROUND_PAINT + ".station.split", new BackgroundPaint() {

			@Override
			public void uninstall(BackgroundComponent arg0) {

			}

			@Override
			public void paint(BackgroundComponent arg0, PaintableComponent arg1, Graphics arg2) {

				arg1.paintBackground(null);
				arg2.setColor(new Color(204, 216, 255));
				Component component = arg1.getComponent();
				arg2.fillRect(0, 0, component.getWidth(), component.getHeight());

				final Graphics2D graphics2d = (Graphics2D) arg2;
				final int width = arg1.getComponent().getBounds().width - 100;
				final int height = width / 12;
				final int y = (int) arg1.getComponent().getBounds().getHeight() - height - 50;

				graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				graphics2d.drawImage(backImage, 50, y, width, height, null);
			}

			@Override
			public void install(BackgroundComponent arg0) {

			}
		});

		dockFrontend.addVetoableListener(new VetoableDockFrontendListener() {

			@Override
			public void shown(VetoableDockFrontendEvent arg0) {

			}

			@Override
			public void showing(VetoableDockFrontendEvent arg0) {

			}

			@Override
			public void hiding(VetoableDockFrontendEvent arg0) {

				for (Dockable dockable : arg0.getDockables()) {

					if (dockable instanceof AbstractBusinessObjectEditor) {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;

						if (abstractBusinessObjectEditor.isVisible() && abstractBusinessObjectEditor.isDirty()) {

							final Object[] options = { "Yes", "No" };
							final int n = JOptionPane.showOptionDialog(
									MainPanel.this,
									"Discard modified values?",
									"Discard",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									new ImageIcon(MainPanel.class
											.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/messagebox_warning.png")), options,
									options[1]);

							if (n == 1) {
								arg0.cancel();
							}
						}
					}
				}
			}

			@Override
			public void hidden(VetoableDockFrontendEvent arg0) {

				try {
					for (Dockable dockable : arg0.getDockables()) {

						if (dockable instanceof AbstractBusinessObjectEditor)
							((AbstractBusinessObjectEditor) dockable).closeAbstractBusinessObjectEditor();

						synchronized (dockFrontend) {
							dockFrontend.remove(dockable);
							removeDockable(dockable);
						}
					}
				}
				catch (Exception e) {
					log.error("Bug", e);
				}
			}
		});

		screenDockStation = new ScreenDockStation(dockFrontend.getController().getRootWindowProvider());
		screenDockStation.setShowing(true);

		dockFrontend.addRoot("screen", screenDockStation);
		dockFrontend.getController().add(screenDockStation);

		stroke4 = KeyStroke.getKeyStroke(KeyEvent.VK_NUMBER_SIGN, ActionEvent.CTRL_MASK, false);

		listener4 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				switchShowingFormula();

			}
		};

		registerKeyboardAction(listener4, "Formula", stroke4, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		dockFrontend.getController().setPopupMenuFactory(new ActionPopupMenuFactory() {

			@Override
			public ActionPopupMenu createMenu(Component arg0, Dockable arg1, DockActionSource arg2, Object arg3) {

				return new BusinessObjectActionPopupMenu(arg1, arg2, MainPanel.this);
			}
		});

	}

	private void handleResponses() {

		while (!shutdown) {
			try {
				AbstractResponse abstractResponse = linkedBlockingQueue.take();
				if (abstractResponse instanceof UpdateResponse) {
					doUpdateResponse((UpdateResponse) abstractResponse);
				}
				if (abstractResponse instanceof PersistResponse)
					doPersistResponse((PersistResponse) abstractResponse);
				if (abstractResponse instanceof RemoveResponse)
					doRemoveResponse((RemoveResponse) abstractResponse);
			}
			catch (Exception e) {
				log.error("Bug", e);
			}
		}

	}

	/**
	 * Adds the business object editor.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param refresh the refresh
	 * @return the abstract business object editor
	 */
	public AbstractBusinessObjectEditor addBusinessObjectEditor(AbstractBusinessObject abstractBusinessObject, boolean refresh) {

		try {
			if (abstractBusinessObject.getId() != 0) {

				AbstractBusinessObjectEditor dockable = null;

				for (Dockable dock : getNamedDockables()) {
					if (dock instanceof AbstractBusinessObjectEditor) {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dock;
						if (abstractBusinessObjectEditor.getAbstractBusinessObject().getId() == abstractBusinessObject.getId()) {
							dockable = (AbstractBusinessObjectEditor) dock;
						}
					}
				}

				if (dockable == null) {

					if (refresh) {
						AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(abstractBusinessObject.getClass().getName(),
								abstractBusinessObject.getId(), RequestIDManager.getInstance().getID()));
						if (abstractResponse instanceof RefreshResponse) {
							RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
							abstractBusinessObject = refreshResponse.getAbstractBusinessObject();
						}
						else if (abstractResponse instanceof ErrorResponse) {
							ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
							JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
							return null;
						}
					}

					dockable = getAbstractBusinessEditor(abstractBusinessObject);
					synchronized (dockFrontend) {
						dockFrontend.addDockable(Long.toString(abstractBusinessObject.getId()), dockable);
						dockFrontend.setHideable(dockable, true);
					}

					SplitDockProperty splitDockProperty = new SplitDockProperty(0, 0, 1, 1);
					Dockable dockable3 = dockFrontend.getController().getFocusedDockable();

					if (dockable3 != null && dockable3.getDockParent() instanceof ScreenDockStation)
						dockable3 = null;

					if (dockable3 == null)
						for (Dockable dockable2 : dockFrontend.getNamedDockables().values())
							if (dockable2.getDockParent() != null && !(dockable2.getDockParent() instanceof ScreenDockStation))
								dockable3 = dockable2;

					if (dockable3 != null && dockable3.getDockParent() != null) {

						if (dockable3.getDockParent() instanceof SplitDockStation) {

							SplitDockStation splitDock = (SplitDockStation) dockable3.getDockParent();
							dockable3.getDockParent().drop(dockable, splitDock.getDockableLocationProperty(dockable3));
						}
						else
							dockable3.getDockParent().drop(dockable);
					}
					else
						drop(dockable, splitDockProperty);
					((AbstractBusinessObjectEditor) dockable).postConstruct();
				}

				synchronized (dockFrontend) {
					dockFrontend.getController().setFocusedDockable(dockable, true);
				}

				return dockable;
			}
			else {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = getAbstractBusinessEditor(abstractBusinessObject);

				synchronized (dockFrontend) {

					dockFrontend.addDockable(Long.toString(System.currentTimeMillis()), abstractBusinessObjectEditor);

					dockFrontend.setHideable(abstractBusinessObjectEditor, true);
				}

				drop(abstractBusinessObjectEditor, new SplitDockProperty(0, 0, 1, 1));

				return abstractBusinessObjectEditor;
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
		return null;
	}

	/**
	 * Adds the plugin dockable.
	 *
	 * @param name the name
	 * @param dockable the dockable
	 */
	public void addPluginDockable(String name, Dockable dockable) {

		synchronized (dockFrontend) {

			dockFrontend.addDockable(name, dockable);

			dockFrontend.setHideable(dockable, true);
		}

		drop(dockable, new SplitDockProperty(0, 0, 1, 1));
	}

	private AbstractBusinessObjectEditor getAbstractBusinessEditor(AbstractBusinessObject abstractBusinessObject) {

		for (FIXAgoraPlugin fixAgoraPlugin : basisClientConnector.getFixAgoraPlugins()) {
			AbstractBusinessObjectEditor abstractBusinessObjectEditor = fixAgoraPlugin.getAbstractBusinessEditor(abstractBusinessObject, this);
			if (abstractBusinessObjectEditor != null)
				return abstractBusinessObjectEditor;
		}

		if (abstractBusinessObject instanceof FUser)
			return new FUserEditor((FUser) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof FRole)
			return new FRoleEditor((FRole) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof Trader)
			return new TraderEditor((Trader) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof Counterparty)
			return new CounterpartyEditor((Counterparty) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof FSecurity)
			return new FSecurityEditor((FSecurity) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof BankCalendar)
			return new BankCalendarEditor((BankCalendar) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof FIXInitiator)
			return new FIXInitiatorEditor((FIXInitiator) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof FIXAcceptor)
			return new FIXAcceptorEditor((FIXAcceptor) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof SpreadSheet)
			return new SpreadSheetEditor((SpreadSheet) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof FSecurityGroup)
			return new FSecurityGroupEditor((FSecurityGroup) abstractBusinessObject, this, basisClientConnector);

		else if (abstractBusinessObject instanceof BusinessObjectGroup || abstractBusinessObject instanceof BusinessSection)
			return new DefaultBusinessGroupEditor(abstractBusinessObject, this, basisClientConnector);

		return null;

	}

	/**
	 * Adds the main panel listener.
	 *
	 * @param mainPanelListener the main panel listener
	 */
	public void addMainPanelListener(final MainPanelListener mainPanelListener) {

		mainPanelListeners.add(mainPanelListener);
	}

	/**
	 * Removes the main panel listener.
	 *
	 * @param mainPanelListener the main panel listener
	 */
	public void removeMainPanelListener(final MainPanelListener mainPanelListener) {

		mainPanelListeners.remove(mainPanelListener);
	}

	/**
	 * On main panel changed.
	 */
	public void onMainPanelChanged() {

		for (MainPanelListener mainPanelListener : mainPanelListeners)
			mainPanelListener.onMainPanelChanged();

	}

	/**
	 * Gets the j frame.
	 *
	 * @return the j frame
	 */
	public JFrame getJFrame() {

		return jFrame;
	}

	/**
	 * Save.
	 */
	public void save() {

		final WaitDialog waitDialog = new WaitDialog(this, "Save business object ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				synchronized (dockFrontend) {

					Dockable dockable = dockFrontend.getController().getFocusedDockable();

					if (dockable instanceof AbstractBusinessObjectEditor) {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
						abstractBusinessObjectEditor.save();
					}

				}

				return null;
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
				
				
			}
		});
	}

	/**
	 * Checks if is save allowed.
	 *
	 * @return true, if is save allowed
	 */
	public boolean isSaveAllowed() {

		Dockable dockable = dockFrontend.getController().getFocusedDockable();

		if (dockable instanceof AbstractBusinessObjectEditor) {

			AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
			return abstractBusinessObjectEditor.isDirty() && abstractBusinessObjectEditor.isConsistent();
		}

		return false;
	}

	/**
	 * Gets the spread sheet status.
	 *
	 * @return the spread sheet status
	 */
	public int getSpreadSheetStatus() {

		Dockable dockable = dockFrontend.getController().getFocusedDockable();

		if (dockable instanceof SpreadSheetEditor) {

			SpreadSheetEditor abstractBusinessObjectEditor = (SpreadSheetEditor) dockable;

			if (!basisClientConnector.getFUser().canWrite(abstractBusinessObjectEditor.getSpreadSheet()))
				return 0;

			if (abstractBusinessObjectEditor.getSelectedTab() != null && abstractBusinessObjectEditor.getSelectedTab().equals("Sheet")) {
				return abstractBusinessObjectEditor.getSelectionState();
			}
		}

		return 0;
	}

	/**
	 * Format.
	 *
	 * @param formatAction the format action
	 */
	public void format(FormatAction formatAction) {

		Dockable dockable = dockFrontend.getController().getFocusedDockable();

		if (dockable instanceof SpreadSheetEditor) {

			SpreadSheetEditor abstractBusinessObjectEditor = (SpreadSheetEditor) dockable;
			abstractBusinessObjectEditor.format(formatAction);
		}

	}

	/**
	 * Gets the dock frontend.
	 *
	 * @return the dock frontend
	 */
	public DockFrontend getDockFrontend() {

		return dockFrontend;
	}

	/**
	 * Save all.
	 */
	public void saveAll() {

		final WaitDialog waitDialog = new WaitDialog(this, "Save all business objects ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				for (Dockable dockable : getNamedDockables())
					if (dockable instanceof AbstractBusinessObjectEditor) {

						synchronized (dockFrontend) {

							AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;

							if (abstractBusinessObjectEditor.isDirty() && abstractBusinessObjectEditor.isConsistent())
								abstractBusinessObjectEditor.save();

						}
					}

				return null;
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
				
				
			}
		});
	}

	/**
	 * Gets the open business objects.
	 *
	 * @return the open business objects
	 */
	public Set<Long> getOpenBusinessObjects() {

		Set<Long> openBusinessObjects = new HashSet<Long>();

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof AbstractBusinessObjectEditor) {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;

				if (abstractBusinessObjectEditor.getAbstractBusinessObject().getId() != 0)
					openBusinessObjects.add(abstractBusinessObjectEditor.getAbstractBusinessObject().getId());
			}

		return openBusinessObjects;
	}

	/**
	 * Gets the xML properties.
	 *
	 * @return the xML properties
	 */
	public String getXMLProperties() {

		shutdown = true;
		linkedBlockingQueue.add(new ErrorResponse(null));

		synchronized (dockFrontend) {
			XElement root = new XElement("layout");
			dockFrontend.writeXML(root);
			return root.toString();
		}
	}

	/**
	 * Sets the xml properties.
	 */
	public void setXMLProperties() {

		String backup = null;

		synchronized (dockFrontend) {
			XElement root = new XElement("layout");
			dockFrontend.writeXML(root);
			backup = root.toString();
		}

		String settings = basisClientConnector.getFUser().getXMLDockSettings();

		try {

			initXMLProperties(settings);
		}
		catch (Exception e) {
			try {
				initXMLProperties(backup);
			}
			catch (Exception e1) {
			}
		}

		List<Dockable> dockables = getNamedDockables();

		synchronized (dockFrontend) {
			if (dockFrontend.getController().getFocusedDockable() == null && dockables.size() > 0)
				dockFrontend.getController().setFocusedDockable(dockables.get(0), true);
		}

		log.info("client is started");
	}

	private void initXMLProperties(String settings) throws Exception {

		synchronized (dockFrontend) {
			Set<String> layouts = dockFrontend.getSettings();
			String[] keys = layouts.toArray(new String[layouts.size()]);
			for (String key : keys)
				dockFrontend.delete(key);
			XElement root = XIO.read(settings);
			dockFrontend.readXML(root);
			getDockFrontend().readXML(root);
		}
	}

	/**
	 * Checks if is save all allowed.
	 *
	 * @return true, if is save all allowed
	 */
	public boolean isSaveAllAllowed() {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof AbstractBusinessObjectEditor) {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;

				if (abstractBusinessObjectEditor.isDirty() && abstractBusinessObjectEditor.isConsistent())
					return true;
			}

		return false;
	}

	/**
	 * Gets the named dockables.
	 *
	 * @return the named dockables
	 */
	public List<Dockable> getNamedDockables() {

		List<Dockable> dockables = new ArrayList<Dockable>();
		synchronized (dockFrontend) {
			dockables.addAll(dockFrontend.getNamedDockables().values());
		}
		return dockables;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

		abstractResponse.handleAbstractResponse(this);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onLoginResponse(net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse)
	 */
	@Override
	public void onLoginResponse(LoginResponse loginResponse) {

	}

	/**
	 * Do persist response.
	 *
	 * @param persistResponse the persist response
	 */
	public void doPersistResponse(PersistResponse persistResponse) {

		securityTreeDialog.onPersistResponse(persistResponse);
		counterpartyTreeDialog.onPersistResponse(persistResponse);
		fixInitiatorTreeDialog.onPersistResponse(persistResponse);
		fixAcceptorTreeDialog.onPersistResponse(persistResponse);

		AbstractBusinessObject abstractBusinessObject = persistResponse.getAbstractBusinessObject();

		namedBusinessObjectMap.put(abstractBusinessObject.getName(), abstractBusinessObject);
		businessObjectMap.put(abstractBusinessObject.getId(), abstractBusinessObject);

		if (abstractBusinessObject instanceof AbstractBusinessComponent)
			businessComponents.add(abstractBusinessObject);
		Collections.sort(businessComponents);

		if (abstractBusinessObject instanceof FRole) {
			AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));

			if (abstractResponse instanceof RolesResponse) {

				RolesResponse rolesResponse = (RolesResponse) abstractResponse;
				for (Dockable dockable : getNamedDockables())
					if (dockable instanceof AbstractBusinessObjectEditor) {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
						abstractBusinessObjectEditor.updateRoles(rolesResponse.getRoles());
					}
			}
			else if (abstractResponse instanceof ErrorResponse) {

				ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
				JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Do update response.
	 *
	 * @param updateResponse the update response
	 */
	public void doUpdateResponse(UpdateResponse updateResponse) {

		try {

			if (securityTreeDialog != null)
				securityTreeDialog.onUpdateResponse(updateResponse);
			if (counterpartyTreeDialog != null)
				counterpartyTreeDialog.onUpdateResponse(updateResponse);
			if (fixInitiatorTreeDialog != null)
				fixInitiatorTreeDialog.onUpdateResponse(updateResponse);
			if (fixAcceptorTreeDialog != null)
				fixAcceptorTreeDialog.onUpdateResponse(updateResponse);

			for (AbstractBusinessObject abstractBusinessObject : updateResponse.getAbstractBusinessObjects()) {

				namedBusinessObjectMap.put(abstractBusinessObject.getName(), abstractBusinessObject);
				businessObjectMap.put(abstractBusinessObject.getId(), abstractBusinessObject);

				if (abstractBusinessObject instanceof AbstractBusinessComponent) {
					AbstractBusinessObject abstractBusinessObject3 = null;
					for (AbstractBusinessObject abstractBusinessObject2 : businessComponents)
						if (abstractBusinessObject2.getId() == abstractBusinessObject.getId())
							abstractBusinessObject3 = abstractBusinessObject2;
					if (abstractBusinessObject3 != null)
						businessComponents.remove(abstractBusinessObject3);
					businessComponents.add(abstractBusinessObject);
					Collections.sort(businessComponents);
				}

				for (Dockable dockable : getNamedDockables())
					if (dockable instanceof AbstractBusinessObjectEditor) {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
						if (abstractBusinessObjectEditor.getAbstractBusinessObject().getId() == abstractBusinessObject.getId()) {

							synchronized (dockFrontend) {

								if (basisClientConnector.getFUser().canRead(abstractBusinessObject))
									abstractBusinessObjectEditor.updateContent(abstractBusinessObject);

								else {
									abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();
									dockFrontend.hide(dockable);
								}

							}
						}

						if (abstractBusinessObjectEditor.getAbstractBusinessObject().isAffectedBy(abstractBusinessObject)) {

							synchronized (dockFrontend) {
								AbstractBusinessObject abstractBusinessObject2 = abstractBusinessObjectEditor.getAbstractBusinessObject();
								AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(abstractBusinessObject2.getClass()
										.getName(), abstractBusinessObject2.getId(), RequestIDManager.getInstance().getID()));
								if (abstractResponse instanceof RefreshResponse) {
									RefreshResponse refreshResponse = (RefreshResponse) abstractResponse;
									abstractBusinessObjectEditor.updateContent(refreshResponse.getAbstractBusinessObject());
								}
								else if (abstractResponse instanceof ErrorResponse) {
									ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
									JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}

				if (abstractBusinessObject instanceof FRole) {
					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));

					if (abstractResponse instanceof RolesResponse) {

						RolesResponse rolesResponse = (RolesResponse) abstractResponse;
						for (Dockable dockable : getNamedDockables())
							if (dockable instanceof AbstractBusinessObjectEditor) {

								AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
								abstractBusinessObjectEditor.updateRoles(rolesResponse.getRoles());
							}
					}
					else if (abstractResponse instanceof ErrorResponse) {

						ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
						JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/**
	 * Do remove response.
	 *
	 * @param removeResponse the remove response
	 */
	public void doRemoveResponse(RemoveResponse removeResponse) {

		try {
			securityTreeDialog.onRemoveResponse(removeResponse);
			counterpartyTreeDialog.onRemoveResponse(removeResponse);
			fixInitiatorTreeDialog.onRemoveResponse(removeResponse);
			fixAcceptorTreeDialog.onRemoveResponse(removeResponse);

			boolean updateRoles = false;

			for (AbstractBusinessObject abstractBusinessObject : removeResponse.getAbstractBusinessObjects()) {

				namedBusinessObjectMap.remove(abstractBusinessObject.getName());
				businessObjectMap.remove(abstractBusinessObject.getId());

				if (abstractBusinessObject instanceof FRole)
					updateRoles = true;
				if (abstractBusinessObject instanceof AbstractBusinessComponent)
					businessComponents.remove(abstractBusinessObject);
			}

			RolesResponse rolesResponse = null;

			if (updateRoles) {
				AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RolesRequest(RequestIDManager.getInstance().getID()));

				if (abstractResponse instanceof RolesResponse) {

					rolesResponse = (RolesResponse) abstractResponse;
				}
				else if (abstractResponse instanceof ErrorResponse) {

					ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
					JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}

			for (final Dockable dockable : getNamedDockables())
				if (dockable instanceof AbstractBusinessObjectEditor) {

					AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
					if (removeResponse.getAbstractBusinessObjects().contains(abstractBusinessObjectEditor.getAbstractBusinessObject())) {
						abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {

								synchronized (dockFrontend) {
									dockFrontend.hide(dockable);
								}
							}
						});

					}
					else if (updateRoles)
						abstractBusinessObjectEditor.updateRoles(rolesResponse.getRoles());
				}
		}
		catch (Exception e) {
			log.error("Bug", e);
		}
	}

	/**
	 * Check all.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void checkAll(AbstractBusinessObject abstractBusinessObject) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof AbstractBusinessObjectEditor) {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;

				if (basisClientConnector.getFUser().canRead(abstractBusinessObjectEditor.getAbstractBusinessObject())) {

					if (abstractBusinessObject.getId() == abstractBusinessObjectEditor.getAbstractBusinessObject().getId())
						abstractBusinessObjectEditor.updateContent(abstractBusinessObject);

					else
						abstractBusinessObjectEditor.updateContent(abstractBusinessObjectEditor.getAbstractBusinessObject());
				}
				else {

					synchronized (dockFrontend) {
						abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();
						dockFrontend.hide(dockable);
					}
				}
			}
	}

	private class BackgroundPainter extends LinePainter {

		private EclipseTabPane pane;

		public BackgroundPainter(EclipseTabPane pane) {

			super(pane);
			this.pane = pane;
		}

		@Override
		public void paintBackground(Graphics g) {

			JComponent parent = pane.getComponent();

			final Graphics2D graphics2D = (Graphics2D) g;

			final GradientPaint gradientPaint = new GradientPaint(parent.getWidth() / 2.F, 0, blue.darker(), parent.getWidth() / 2.F, 30, blue);

			graphics2D.setPaint(gradientPaint);

			graphics2D.fillRect(0, 0, parent.getWidth(), parent.getHeight());
		}
	}

	private class BackgroundTabPainter implements TabPainter {

		private TabPainter delegate = ArchGradientPainter.FACTORY;

		public TabPanePainter createDecorationPainter(EclipseTabPane pane) {

			return new BackgroundPainter(pane);
		}

		public InvisibleTab createInvisibleTab(InvisibleTabPane pane, Dockable dockable) {

			return delegate.createInvisibleTab(pane, dockable);
		}

		public TabComponent createTabComponent(EclipseTabPane pane, Dockable dockable) {

			return delegate.createTabComponent(pane, dockable);
		}

		public Border getFullBorder(BorderedComponent owner, DockController controller, Dockable dockable) {

			return delegate.getFullBorder(owner, controller, dockable);
		}
	}

	/**
	 * Gets the security tree dialog.
	 *
	 * @return the security tree dialog
	 */
	public SecurityTreeDialog getSecurityTreeDialog() {
		
		if(securityTreeDialog==null)
			return new SecurityTreeDialog(new BusinessObjectTreeNode(), basisClientConnector);

		return securityTreeDialog;
	}

	/**
	 * Gets the counterparty tree dialog.
	 *
	 * @return the counterparty tree dialog
	 */
	public CounterpartyTreeDialog getCounterpartyTreeDialog() {
		
		if(securityTreeDialog==null)
			return new CounterpartyTreeDialog(new BusinessObjectTreeNode(), basisClientConnector);

		return counterpartyTreeDialog;
	}

	/**
	 * Gets the data dictionaries.
	 *
	 * @return the data dictionaries
	 */
	public List<DataDictionary> getDataDictionaries() {

		return dataDictionaries;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetCellResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse)
	 */
	@Override
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {

		try {
			for (Dockable dockable : getNamedDockables())
				if (dockable instanceof SpreadSheetEditor)
					((SpreadSheetEditor) dockable).onUpdateSheetCellResponse(updateSheetCellResponse);
		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetCellFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse)
	 */
	@Override
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).onUpdateSheetCellFormatResponse(updateSheetCellFormatResponse);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateColumnFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse)
	 */
	@Override
	public void onUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).onUpdateColumnFormatResponse(updateColumnFormatResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateRowFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse)
	 */
	@Override
	public void onUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).onUpdateRowFormatResponse(updateRowFormatResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateSheetConditionalFormatResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse)
	 */
	@Override
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).onUpdateSheetConditionalFormatResponse(updateSheetConditionalFormatResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateFullSheetResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse)
	 */
	@Override
	public void onUpdateFullSheetResponse(UpdateFullSheetResponse updateFullSheetResponse) {

		for (Dockable dockable : getNamedDockables())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).onUpdateFullSheetResponse(updateFullSheetResponse);

	}

	/**
	 * Switch showing formula.
	 */
	public void switchShowingFormula() {

		showingFormula = !showingFormula;

		for (Dockable dockable : dockFrontend.getNamedDockables().values())
			if (dockable instanceof SpreadSheetEditor)
				((SpreadSheetEditor) dockable).switchShowingFormula(showingFormula);

	}

	/**
	 * Gets the complete data dictionaries.
	 *
	 * @return the complete data dictionaries
	 */
	public List<DataDictionary> getCompleteDataDictionaries() {

		if (completeDataDictionaries != null)
			return completeDataDictionaries;

		final WaitDialog waitDialog = new WaitDialog(this, "Loading FIX Data Dictionaries ...");

		final SwingWorker<AbstractResponse, Void> swingWorker = new SwingWorker<AbstractResponse, Void>() {

			@Override
			protected AbstractResponse doInBackground() throws Exception {

				AbstractResponse abstractResponse = basisClientConnector
						.sendRequest(new DataDictionariesRequest(RequestIDManager.getInstance().getID(), false));

				return abstractResponse;
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

			}
		});

		AbstractResponse abstractResponse;
		try {
			abstractResponse = swingWorker.get();
			if (abstractResponse instanceof DataDictionariesResponse) {

				DataDictionariesResponse dataDictionariesResponse = (DataDictionariesResponse) abstractResponse;
				completeDataDictionaries = dataDictionariesResponse.getDataDictionaries();
				return completeDataDictionaries;
			}
			else if (abstractResponse instanceof ErrorResponse) {
				ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
				JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
				return new ArrayList<DataDictionary>();
			}
		}
		catch (Exception exception) {
			JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return new ArrayList<DataDictionary>();

	}

	/**
	 * Sets the dialog node.
	 *
	 * @param dialogNode the new dialog node
	 */
	public void setDialogNode(DefaultMutableTreeNode dialogNode) {

		businessComponents = new ArrayList<AbstractBusinessObject>();
		namedBusinessObjectMap = new HashMap<String, AbstractBusinessObject>();
		businessObjectMap = new HashMap<Long, AbstractBusinessObject>();

		parseTreeNode(dialogNode);
		Collections.sort(businessComponents);

	}

	private void parseTreeNode(DefaultMutableTreeNode dialogNode) {

		for (int i = 0; i < dialogNode.getChildCount(); i++) {
			BusinessObjectTreeNode defaultMutableTreeNode = (BusinessObjectTreeNode) dialogNode.getChildAt(i);

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessObject) {
				AbstractBusinessObject abstractBusinessObject = (AbstractBusinessObject) defaultMutableTreeNode.getUserObject();

				namedBusinessObjectMap.put(abstractBusinessObject.getName(), abstractBusinessObject);
				businessObjectMap.put(abstractBusinessObject.getId(), abstractBusinessObject);

				if (abstractBusinessObject instanceof BankCalendar)
					bankCalendars.add((BankCalendar) abstractBusinessObject);

				if (abstractBusinessObject.getName().equals("Securities")) {

					securityTreeDialog = new SecurityTreeDialog(defaultMutableTreeNode, basisClientConnector);
				}

				if (abstractBusinessObject.getName().equals("Counterparties")) {

					counterpartyTreeDialog = new CounterpartyTreeDialog(defaultMutableTreeNode, basisClientConnector);
				}

				if (abstractBusinessObject instanceof BusinessObjectGroup) {

					BusinessObjectGroup businessObjectGroup = (BusinessObjectGroup) abstractBusinessObject;

					if (AbstractInitiator.class.isAssignableFrom(businessObjectGroup.getChildClass())) {
						BusinessObjectTreeNode businessObjectTreeNode = new BusinessObjectTreeNode();
						businessObjectTreeNode.setUserObject(defaultMutableTreeNode.getUserObject());
						copySubTree(businessObjectTreeNode, defaultMutableTreeNode);
						fixInitiatorTreeDialog.addTopNode(businessObjectTreeNode);
					}

					if (AbstractAcceptor.class.isAssignableFrom(businessObjectGroup.getChildClass())) {
						BusinessObjectTreeNode businessObjectTreeNode = new BusinessObjectTreeNode();
						businessObjectTreeNode.setUserObject(defaultMutableTreeNode.getUserObject());
						copySubTree(businessObjectTreeNode, defaultMutableTreeNode);
						fixAcceptorTreeDialog.addTopNode(businessObjectTreeNode);

					}

				}

			}

			if (defaultMutableTreeNode.getUserObject() instanceof AbstractBusinessComponent)
				businessComponents.add((AbstractBusinessObject) ((DefaultMutableTreeNode) dialogNode.getChildAt(i)).getUserObject());
			parseTreeNode(defaultMutableTreeNode);
		}
	}

	/**
	 * Gets the business components.
	 *
	 * @return the business components
	 */
	public List<AbstractBusinessObject> getBusinessComponents() {

		return businessComponents;
	}

	/**
	 * Gets the initiator tree dialog.
	 *
	 * @return the initiator tree dialog
	 */
	public InitiatorTreeDialog getInitiatorTreeDialog() {

		return fixInitiatorTreeDialog;
	}

	/**
	 * Gets the acceptor tree dialog.
	 *
	 * @return the acceptor tree dialog
	 */
	public AcceptorTreeDialog getAcceptorTreeDialog() {

		return fixAcceptorTreeDialog;
	}

	/**
	 * Close.
	 *
	 * @param dockable2 the dockable2
	 */
	public void close(Dockable dockable2) {

		List<Dockable> dockables = new ArrayList<Dockable>();
		dockables.addAll(getNamedDockables());

		for (final Dockable dockable : dockables)
			if (dockable == dockable2) {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
				abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						synchronized (dockFrontend) {
							dockFrontend.hide(dockable);
						}
					}
				});

			}
	}

	/**
	 * Close all.
	 */
	public void closeAll() {

		List<Dockable> dockables = new ArrayList<Dockable>();
		dockables.addAll(getNamedDockables());

		for (final Dockable dockable : dockables)
			if (dockable instanceof AbstractBusinessObjectEditor) {

				AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
				abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						synchronized (dockFrontend) {
							dockFrontend.hide(dockable);
						}
					}
				});

			}
	}

	/**
	 * Open business object.
	 *
	 * @param substring the substring
	 */
	public void openBusinessObject(String substring) {

		final AbstractBusinessObject abstractBusinessObject = namedBusinessObjectMap.get(substring);

		if (abstractBusinessObject != null) {

			final WaitDialog waitDialog = new WaitDialog(MainPanel.this, "Open business object...");

			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					MainPanel.this.addBusinessObjectEditor(abstractBusinessObject, false);
					return null;
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

				}
			});
		}

	}

	/**
	 * Copy sub tree.
	 *
	 * @param subRoot the sub root
	 * @param sourceTree the source tree
	 * @return the object
	 */
	public Object copySubTree(BusinessObjectTreeNode subRoot, BusinessObjectTreeNode sourceTree) {

		if (sourceTree == null) {
			return subRoot;
		}
		for (int i = 0; i < sourceTree.getChildCount(); i++) {
			BusinessObjectTreeNode child = (BusinessObjectTreeNode) sourceTree.getChildAt(i);
			BusinessObjectTreeNode clone = new BusinessObjectTreeNode(child.getUserObject());
			subRoot.add(clone);
			copySubTree(clone, child);
		}
		return subRoot;
	}

	/**
	 * Gets the abstract business object for id.
	 *
	 * @param id the id
	 * @return the abstract business object for id
	 */
	public AbstractBusinessObject getAbstractBusinessObjectForId(long id) {

		AbstractBusinessObject abstractBusinessObject = businessObjectMap.get(id);

		if (abstractBusinessObject == null) {
			AbstractResponse abstractResponse = basisClientConnector.sendRequest(new RefreshRequest(AbstractBusinessObject.class.getName(), id,
					RequestIDManager.getInstance().getID()));
			if (abstractResponse instanceof RefreshResponse) {
				abstractBusinessObject = ((RefreshResponse) abstractResponse).getAbstractBusinessObject();
				if (abstractBusinessObject != null)
					businessObjectMap.put(abstractBusinessObject.getId(), abstractBusinessObject);
			}
		}

		return abstractBusinessObject;
	}

	/**
	 * Gets the bank calendars.
	 *
	 * @return the bank calendars
	 */
	public List<BankCalendar> getBankCalendars() {

		return bankCalendars;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onPersistResponse(net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse)
	 */
	@Override
	public void onPersistResponse(PersistResponse persistResponse) {

		linkedBlockingQueue.add(persistResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onUpdateResponse(net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse)
	 */
	@Override
	public void onUpdateResponse(UpdateResponse updateResponse) {

		linkedBlockingQueue.add(updateResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.shared.model.communication.BasisResponses#onRemoveResponse(net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse)
	 */
	@Override
	public void onRemoveResponse(RemoveResponse removeResponse) {

		linkedBlockingQueue.add(removeResponse);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

		// TODO Auto-generated method stub

	}

	/**
	 * Inits the.
	 */
	public void init() {

		try {

			timer = new Timer(50, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					alpha = alpha + 0.05f;
					if (alpha > 1f) {
						alpha = 1f;
						timer.stop();

						synchronized (MainPanel.this) {
							MainPanel.this.notify();
						}
					}
					((JComponent) getComponent()).paintImmediately(0, 0, getWidth(), getHeight());

				}

			});

			timer.setCoalesce(false);

			timer.start();

			synchronized (this) {
				wait();
			}

			thread = new Thread() {

				public void run() {

					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new DataDictionariesRequest(RequestIDManager.getInstance().getID(),
							true));
					if (abstractResponse instanceof DataDictionariesResponse) {

						DataDictionariesResponse dataDictionariesResponse = (DataDictionariesResponse) abstractResponse;
						dataDictionaries = dataDictionariesResponse.getDataDictionaries();
					}
					else if (abstractResponse instanceof ErrorResponse) {
						ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
						JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
					}

					basisClientConnector.addBasisClientConnectorListener(MainPanel.this);
					handleResponses();

				}
			};

			thread.start();

		}
		catch (Exception e) {
			log.error("Bug", e);
		}

	}

	/**
	 * Close others.
	 *
	 * @param otherDockable the other dockable
	 */
	public void closeOthers(Dockable otherDockable) {

		List<Dockable> dockables = new ArrayList<Dockable>();
		dockables.addAll(getNamedDockables());

		for (final Dockable dockable : dockables)
			if (dockable instanceof AbstractBusinessObjectEditor && dockable != otherDockable) {

				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
						abstractBusinessObjectEditor.closeAbstractBusinessObjectEditor();

						synchronized (dockFrontend) {
							dockFrontend.hide(dockable);
						}
					}
				});

			}
	}

}