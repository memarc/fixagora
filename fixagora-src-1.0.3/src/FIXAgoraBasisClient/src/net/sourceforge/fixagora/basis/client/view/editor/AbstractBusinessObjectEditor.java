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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.MainPanel;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse.PersistStatus;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UniqueNameResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse.UpdateStatus;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.FRole;
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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DockableStateEvent;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.event.DockableSelectionEvent;
import bibliothek.gui.dock.event.DockableSelectionListener;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.border.BorderModifier;

/**
 * The Class AbstractBusinessObjectEditor.
 */
public abstract class AbstractBusinessObjectEditor extends DefaultDockable {

	private boolean dirty = false;

	private boolean consistent = false;

	private MainPanel mainPanel = null;

	/** The basis client connector. */
	protected BasisClientConnector basisClientConnector = null;

	/** The scroll pane. */
	protected JScrollPane scrollPane = null;

	/** The warning icon. */
	protected final ImageIcon warningIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));

	/** The bug icon. */
	protected final ImageIcon bugIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));

	/** The stack dock station. */
	protected StackDockStation stackDockStation = null;

	private final LinkedBlockingQueue<String> checkNameQueue = new LinkedBlockingQueue<String>();

	/** The visible. */
	protected boolean visible = true;

	private JLabel nameWarningLabel = new JLabel("");

	private JLabel businessObjectLabel = null;

	private Color blue = new Color(204, 216, 255);

	private DockController dockController;

	/**
	 * Instantiates a new abstract business object editor.
	 *
	 * @param abstractBusinessObject the abstract business object
	 * @param mainPanel the main panel
	 * @param basisClientConnector the basis client connector
	 */
	public AbstractBusinessObjectEditor(AbstractBusinessObject abstractBusinessObject, final MainPanel mainPanel, BasisClientConnector basisClientConnector) {

		super();
		
		setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource(abstractBusinessObject.getIcon())));
		if (abstractBusinessObject.getName() == null || abstractBusinessObject.getName().trim() == null)
			setTitleText("New " + abstractBusinessObject.getBusinessObjectName());
		else
			setTitleText(abstractBusinessObject.getName());

		this.mainPanel = mainPanel;
		this.basisClientConnector = basisClientConnector;

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout(0, 0));

		final JPanel headPanel = new GradientPanel();
		jPanel.add(headPanel, BorderLayout.NORTH);
		final GridBagLayout gbl_headPanel = new GridBagLayout();
		gbl_headPanel.rowHeights = new int[] { 15 };
		gbl_headPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_headPanel.rowWeights = new double[] { 0.0 };
		headPanel.setLayout(gbl_headPanel);

		jPanel.add(headPanel, BorderLayout.NORTH);

		businessObjectLabel = new JLabel(abstractBusinessObject.getName());
		businessObjectLabel.setForeground(Color.WHITE);
		businessObjectLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 20));
		final GridBagConstraints gbc_businessObjectLabel = new GridBagConstraints();
		gbc_businessObjectLabel.anchor = GridBagConstraints.WEST;
		gbc_businessObjectLabel.insets = new Insets(5, 25, 5, 5);
		gbc_businessObjectLabel.gridx = 3;
		gbc_businessObjectLabel.gridy = 0;
		headPanel.add(businessObjectLabel, gbc_businessObjectLabel);

		final JLabel businessObjectNameLabel = new JLabel();
		businessObjectNameLabel.setText(abstractBusinessObject.getBusinessObjectName());
		businessObjectNameLabel.setIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource(abstractBusinessObject.getLargeIcon())));
		businessObjectNameLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 20));
		businessObjectNameLabel.setForeground(Color.WHITE);
		final GridBagConstraints gbc_businessObjectNameLabel = new GridBagConstraints();
		gbc_businessObjectNameLabel.anchor = GridBagConstraints.EAST;
		gbc_businessObjectNameLabel.insets = new Insets(5, 0, 5, 25);
		gbc_businessObjectNameLabel.gridx = 4;
		gbc_businessObjectNameLabel.gridy = 0;
		headPanel.add(businessObjectNameLabel, gbc_businessObjectNameLabel);

		EclipseTheme eclipseTheme = new EclipseTheme();
		EclipseColorScheme colorScheme = new EclipseColorScheme() {

			@Override
			public void updateUI() {

				super.updateUI();

				setColor("stack.border", Color.GRAY);
				setColor("stack.tab.top", blue);
				setColor("stack.tab.top.selected",new Color(143, 169, 255));
				setColor("stack.tab.top.selected.focused", new Color(143, 169, 255));
				setColor("stack.tab.text", Color.WHITE);
				setColor("stack.tab.text.selected", Color.WHITE);
				setColor("stack.tab.text.selected.focused", Color.WHITE);
				setColor("stack.tab.bottom", blue.darker());
			}
		};

		eclipseTheme.setColorScheme(colorScheme);

		BorderModifier borderModifier = new BorderModifier() {

			@Override
			public Border modify(Border arg0) {

				return BorderFactory.createEmptyBorder();
			}
		};

		dockController = new DockController();
		dockController.setRootWindow(mainPanel.getJFrame());
		dockController.setTheme(eclipseTheme);

		addDockableStateListener(new DockableStateListener() {

			@Override
			public void changed(DockableStateEvent arg0) {

				if (getController() != null && getController().getFocusedDockable() != null
						&& getController().getFocusedDockable() == AbstractBusinessObjectEditor.this) {
					handleFocus();
				}
				
				mainPanel.onMainPanelChanged();

			}
		});

		dockController.addDockableSelectionListener(new DockableSelectionListener() {

			@Override
			public void dockableSelected(DockableSelectionEvent arg0) {

				mainPanel.onMainPanelChanged();

			}
		});

		final Thread thread = new Thread() {

			@Override
			public void run() {

				nameCheck();
			}
		};

		stackDockStation = new StackDockStation(eclipseTheme);
		dockController.add(stackDockStation);
		stackDockStation.getController().getProperties().set(StackDockStation.TAB_PLACEMENT, TabPlacement.BOTTOM_OF_DOCKABLE);
		stackDockStation.getController().getProperties().set(EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true);
		stackDockStation.getController().getProperties().set(EclipseTheme.TAB_PAINTER, new BackgroundTabPainter());
		stackDockStation.getController().getThemeManager().setBorderModifier("dock.border.displayer.eclipse.content", borderModifier);
		stackDockStation.getController().getThemeManager().setBorderModifier("dock.border.stack.eclipse.content", borderModifier);

		jPanel.add(stackDockStation.getComponent());
		add(jPanel);
		thread.start();

	}

	/**
	 * Handle focus.
	 */
	public void handleFocus() {

	}

	/**
	 * Gets the name warning label.
	 *
	 * @return the name warning label
	 */
	public JLabel getNameWarningLabel() {

		return nameWarningLabel;
	}

	/**
	 * Checks if is dirty.
	 *
	 * @return true, if is dirty
	 */
	public boolean isDirty() {

		return dirty;
	}

	/**
	 * Checks if is consistent.
	 *
	 * @return true, if is consistent
	 */
	public boolean isConsistent() {

		return consistent;
	}

	/**
	 * Sets the consistent.
	 *
	 * @param consistent the new consistent
	 */
	protected void setConsistent(boolean consistent) {

		this.consistent = consistent;
		mainPanel.onMainPanelChanged();
	}

	/**
	 * Sets the dirty.
	 *
	 * @param dirty the new dirty
	 */
	protected void setDirty(boolean dirty) {

		this.dirty = dirty;

		mainPanel.onMainPanelChanged();

		if (dirty) {
			if (consistent)
				setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/unsaved.png")));
			else

				setTitleIcon(new ImageIcon(
						AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png")));
		}

		else
			setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource(getAbstractBusinessObject().getIcon())));
	}

	/**
	 * Inits the content.
	 */
	public abstract void initContent();

	/**
	 * Save.
	 */
	public void save() {

		if (getAbstractBusinessObject().getId() == 0) {

			AbstractResponse abstractResponse = basisClientConnector.sendRequest(new PersistRequest(getAbstractBusinessObject(), RequestIDManager.getInstance()
					.getID()));
			if (abstractResponse instanceof PersistResponse) {
				PersistResponse persistResponse = (PersistResponse) abstractResponse;

				if (persistResponse.getPersistStatus() == PersistStatus.SUCCESS) {

					AbstractBusinessObject abstractBusinessObject = persistResponse.getAbstractBusinessObject();

					setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource(abstractBusinessObject.getIcon())));
					setTitleText(abstractBusinessObject.getName());

					businessObjectLabel.setText(abstractBusinessObject.getName());

					updateContent(abstractBusinessObject);
				}
				else {

					JOptionPane.showMessageDialog(AbstractBusinessObjectEditor.this.getComponent(), "Saving business object "+getAbstractBusinessObject().getName()+" failed.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (abstractResponse instanceof ErrorResponse) {
				ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
				JOptionPane.showMessageDialog(AbstractBusinessObjectEditor.this.getComponent(), errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			AbstractResponse abstractResponse = basisClientConnector.sendRequest(new UpdateRequest(getAbstractBusinessObject(), RequestIDManager.getInstance()
					.getID()));
			if (abstractResponse instanceof UpdateResponse) {
				UpdateResponse persistResponse = (UpdateResponse) abstractResponse;

				if (persistResponse.getUpdateStatus() == UpdateStatus.SUCCESS) {

					AbstractBusinessObject abstractBusinessObject = persistResponse.getAbstractBusinessObjects().get(0);

					setTitleIcon(new ImageIcon(AbstractBusinessObjectEditor.class.getResource(abstractBusinessObject.getIcon())));
					setTitleText(abstractBusinessObject.getName());

					businessObjectLabel.setText(abstractBusinessObject.getName());

					updateContent(abstractBusinessObject);
				}
				else {

					JOptionPane.showMessageDialog(AbstractBusinessObjectEditor.this.getComponent(), "Saving business object "+getAbstractBusinessObject().getName()+" failed.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (abstractResponse instanceof ErrorResponse) {
				ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
				JOptionPane.showMessageDialog(AbstractBusinessObjectEditor.this.getComponent(), errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Gets the warning icon.
	 *
	 * @return the warning icon
	 */
	public ImageIcon getWarningIcon() {

		return warningIcon;
	}

	/**
	 * Gets the bug icon.
	 *
	 * @return the bug icon
	 */
	public ImageIcon getBugIcon() {

		return bugIcon;
	}

	/**
	 * Check name.
	 *
	 * @param name the name
	 */
	public void checkName(String name) {

		checkNameQueue.add(name);
	}

	private void nameCheck() {

		while (visible)
			try {

				final String name = checkNameQueue.take();

				if (visible) {
					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new UniqueNameRequest(name, getAbstractBusinessObject().getId(),
							RequestIDManager.getInstance().getID()));
					if (abstractResponse instanceof UniqueNameResponse) {
						UniqueNameResponse uniqueNameResponse = (UniqueNameResponse) abstractResponse;

						if (!uniqueNameResponse.isUnique()) {

							nameWarningLabel.setToolTipText("A business object with this name already exist.");
							nameWarningLabel.setIcon(getBugIcon());

							consistent = false;
							mainPanel.onMainPanelChanged();
						}
					}
					else if (abstractResponse instanceof ErrorResponse) {
						ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
						JOptionPane.showMessageDialog(null, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			catch (final InterruptedException e1) {
			}
	}

	/**
	 * Close abstract business object editor.
	 */
	public void closeAbstractBusinessObjectEditor() {

		visible = false;
		checkNameQueue.add("");
	}

	/**
	 * Gets the basis client connector.
	 *
	 * @return the basis client connector
	 */
	public BasisClientConnector getBasisClientConnector() {

		return basisClientConnector;
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

			final GradientPaint gradientPaint = new GradientPaint(parent.getWidth() / 2.F, parent.getHeight() - 30, blue, parent.getWidth() / 2.F,
					parent.getHeight(), blue.darker());

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
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	public boolean isEditable() {

		return basisClientConnector.getFUser().canWrite(getAbstractBusinessObject());
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public MainPanel getMainPanel() {

		return mainPanel;
	}

	/**
	 * Gets the selected tab.
	 *
	 * @return the selected tab
	 */
	public String getSelectedTab() {

		if (dockController.getFocusedDockable() != null)
			return dockController.getFocusedDockable().getTitleText();
		return null;

	}

	/**
	 * Post construct.
	 */
	public void postConstruct() {

	};

	/**
	 * Update roles.
	 *
	 * @param roles the roles
	 */
	public abstract void updateRoles(List<FRole> roles);

	/**
	 * Update content.
	 *
	 * @param abstractBusinessObject the abstract business object
	 */
	public void updateContent(AbstractBusinessObject abstractBusinessObject) {

		setTitleText(abstractBusinessObject.getName());
		businessObjectLabel.setText(abstractBusinessObject.getName());
	}

	/**
	 * Check dirty.
	 */
	public abstract void checkDirty();
	
	/**
	 * Gets the abstract business object.
	 *
	 * @return the abstract business object
	 */
	public abstract AbstractBusinessObject getAbstractBusinessObject();

	
	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
	
		return visible;
	}
	
	

}
