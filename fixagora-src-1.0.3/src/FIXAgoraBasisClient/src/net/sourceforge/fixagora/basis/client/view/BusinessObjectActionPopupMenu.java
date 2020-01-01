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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.editor.AbstractBusinessObjectEditor;
import net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.popup.ActionPopupMenu;
import bibliothek.gui.dock.action.popup.ActionPopupMenuListener;

/**
 * The Class BusinessObjectActionPopupMenu.
 */
public class BusinessObjectActionPopupMenu implements ActionPopupMenu {

	private Dockable dockable;

	/** the actual menu that is shown to the user */
	private JPopupMenu menu;

	/** all the listeners that were added to this menu */
	private List<ActionPopupMenuListener> listeners = new ArrayList<ActionPopupMenuListener>();

	/** whether the {@link #menu} is currently open */
	private boolean showing = false;

	private JMenuItem startBusinessComponentPopupMenuItem;

	private JMenuItem stopBusinessComponentPopupMenuItem;

	private JMenuItem saveMenuItem;

	private JMenuItem saveAllMenuItem;

	private JMenuItem closeMenuItem;

	private JMenuItem closeAllMenuItem;

	private JMenuItem closeOthersMenuItem;

	/**
	 * Instantiates a new business object action popup menu.
	 *
	 * @param dockable the dockable
	 * @param actions the actions
	 * @param mainPanel the main panel
	 */
	public BusinessObjectActionPopupMenu(final Dockable dockable, DockActionSource actions, final MainPanel mainPanel) {

		menu = new JPopupMenu();
		menu.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

				EventQueue.invokeLater(new Runnable() {

					public void run() {

						showing = false;

						fireClosed();
					}
				});
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				if (mainPanel.isSaveAllowed()) {

					saveMenuItem.setEnabled(true);
				}
				else {

					saveMenuItem.setEnabled(false);
				}

				if (mainPanel.isSaveAllAllowed()) {

					saveAllMenuItem.setEnabled(true);
				}

				boolean startEnabled = false;
				boolean stopEnabled = false;

				if (dockable instanceof AbstractBusinessObjectEditor) {
					AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
					if (abstractBusinessObjectEditor.getAbstractBusinessObject() instanceof AbstractBusinessComponent) {

						AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObjectEditor
								.getAbstractBusinessObject();

						if (abstractBusinessComponent.isStartable()
								&& abstractBusinessObjectEditor.getBasisClientConnector().getFUser().canExecute(abstractBusinessComponent)) {
							if (abstractBusinessComponent.getStartLevel() != 0 && abstractBusinessComponent.getStartLevel() != 3)
								stopEnabled = true;
							else
								startEnabled = true;
						}
					}
				}

				if (startEnabled) {
					startBusinessComponentPopupMenuItem.setVisible(true);
				}
				else {
					startBusinessComponentPopupMenuItem.setVisible(false);
				}

				if (stopEnabled) {
					stopBusinessComponentPopupMenuItem.setVisible(true);
				}
				else {
					stopBusinessComponentPopupMenuItem.setVisible(false);
				}
			}

			public void popupMenuCanceled(PopupMenuEvent e) {

			}
		});

		startBusinessComponentPopupMenuItem = new JMenuItem("Start Business Component");
		startBusinessComponentPopupMenuItem.setVisible(false);
		startBusinessComponentPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		startBusinessComponentPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_login.png")));
		menu.add(startBusinessComponentPopupMenuItem);

		startBusinessComponentPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (dockable instanceof AbstractBusinessObjectEditor) {
					final AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
					if (abstractBusinessObjectEditor.getAbstractBusinessObject() instanceof AbstractBusinessComponent) {

						final AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObjectEditor
								.getAbstractBusinessObject();

						final List<Long> abstractBusinessComponents = new ArrayList<Long>();

						abstractBusinessComponents.add(abstractBusinessComponent.getId());

						abstractBusinessObjectEditor.getBasisClientConnector().sendRequest(
								new StartBusinessComponentRequest(RequestIDManager.getInstance().getID(), abstractBusinessComponents));

					}
				}
			}
		});

		stopBusinessComponentPopupMenuItem = new JMenuItem("Stop Business Component");
		stopBusinessComponentPopupMenuItem.setVisible(false);
		stopBusinessComponentPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		stopBusinessComponentPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/shutdown.png")));
		menu.add(stopBusinessComponentPopupMenuItem);

		stopBusinessComponentPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (dockable instanceof AbstractBusinessObjectEditor) {
					final AbstractBusinessObjectEditor abstractBusinessObjectEditor = (AbstractBusinessObjectEditor) dockable;
					if (abstractBusinessObjectEditor.getAbstractBusinessObject() instanceof AbstractBusinessComponent) {

						final AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObjectEditor
								.getAbstractBusinessObject();


								final List<Long> abstractBusinessComponents = new ArrayList<Long>();

								abstractBusinessComponents.add(abstractBusinessComponent.getId());

								abstractBusinessObjectEditor.getBasisClientConnector().sendRequest(
										new StopBusinessComponentRequest(RequestIDManager.getInstance().getID(), abstractBusinessComponents));
					}
				}
			}
		});

		saveMenuItem = new JMenuItem("Save", 'S');
		saveMenuItem.setEnabled(false);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.setEnabled(false);
		saveMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/3floppy_unmount.png")));
		saveMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		menu.add(saveMenuItem);

		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				mainPanel.save();
			}
		});

		saveAllMenuItem = new JMenuItem("Save All", 'A');
		saveAllMenuItem.setEnabled(false);
		saveAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		saveAllMenuItem.setEnabled(false);
		saveAllMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/save_all.png")));
		saveAllMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		menu.add(saveAllMenuItem);

		saveAllMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				mainPanel.saveAll();
			}
		});

		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		closeMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		menu.add(closeMenuItem);

		closeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				mainPanel.close(dockable);
			}
		});
		
		closeOthersMenuItem = new JMenuItem("Close Others");
		closeOthersMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		closeOthersMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		menu.add(closeOthersMenuItem);

		closeOthersMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				mainPanel.closeOthers(dockable);
			}
		});

		closeAllMenuItem = new JMenuItem("Close All");
		closeAllMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/cancel.png")));
		closeAllMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		menu.add(closeAllMenuItem);

		closeAllMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				mainPanel.closeAll();
			}
		});

	}

	private void fireClosed() {

		for (ActionPopupMenuListener listener : listeners.toArray(new ActionPopupMenuListener[listeners.size()])) {
			listener.closed(this);
		}
	}

	/* (non-Javadoc)
	 * @see bibliothek.gui.dock.action.popup.ActionPopupMenu#addListener(bibliothek.gui.dock.action.popup.ActionPopupMenuListener)
	 */
	public void addListener(ActionPopupMenuListener listener) {

		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see bibliothek.gui.dock.action.popup.ActionPopupMenu#getDockable()
	 */
	public Dockable getDockable() {

		return dockable;
	}

	/* (non-Javadoc)
	 * @see bibliothek.gui.dock.action.popup.ActionPopupMenu#removeListener(bibliothek.gui.dock.action.popup.ActionPopupMenuListener)
	 */
	public void removeListener(ActionPopupMenuListener listener) {

		listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see bibliothek.gui.dock.action.popup.ActionPopupMenu#show(java.awt.Component, int, int)
	 */
	public void show(Component owner, int x, int y) {

		if (!isShowing()) {
			menu.show(owner, x, y);
			showing = true;
		}
	}

	/**
	 * Checks if is showing.
	 *
	 * @return true, if is showing
	 */
	public boolean isShowing() {

		return showing;
	}
}
