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

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.control.TransferActionListener;
import net.sourceforge.fixagora.basis.client.model.BasisProperties;
import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.client.view.dialog.AboutDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.PermissionChangeDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditorSheet.FormatAction;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LogoffRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.RemoveResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.StartBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.StopBusinessComponentRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessObjectGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.BusinessSection;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

import org.apache.log4j.Logger;

/**
 * The Class TopPanel.
 */
public class TopPanel extends JPanel implements BasisClientConnectorListener, MainPanelListener {

	private static final long serialVersionUID = 1L;

	private Desktop desktop = null;

	private BasisClientConnector basisClientConnector = null;

	private MainPanel mainPanel = null;

	private StatusPanel statusPanel = null;

	private ToolbarButton newBusinessObjectButton = null;

	private ToolbarButton openProjectButton = null;

	private ToolbarButton saveButton = null;

	private List<AbstractBusinessObject> treeSelection = null;

	private ToolbarButton newBusinessObjectGroupButton = null;

	private JMenuItem newBusinessObjectGroupPopupMenuItem = null;

	private JMenuItem newBusinessObjectPopupMenuItem = null;

	private JMenuItem openBusinessObjectPopupMenuItem = null;

	private JMenuItem removeBusinessObjectPopupMenuItem = null;

	private JMenuItem saveAllMenuItem = null;

	private JMenuItem saveMenuItem = null;

	private ToolbarButton saveAllButton = null;

	private JPopupMenu treePopup = null;

	private JMenuItem openBusinessObjectMenuItem = null;

	private JMenuItem removeBusinessObjectMenuItem = null;

	private JMenuItem newBusinessObjectMenuItem = null;

	private JMenuItem newBusinessObjectGroupMenuItem = null;

	private JMenuItem massPermissionChangeMenuItem = null;

	private int treeWidth = 0;

	private int mainPanelHeight = 0;

	private Set<Long> expandSet = null;

	private JMenuItem cutMenuItem;

	private JMenuItem copyMenuItem;

	private JMenuItem pasteMenuItem;

	private JMenuItem clearFormatMenuItem;

	private JMenuItem conditionalFormatCellsMenuItem;

	private JMenuItem formatCellsMenuItem;

	private JMenuItem insertRowMenuItem;

	private JMenuItem deleteRowMenuItem;

	private JMenuItem hideRowMenuItem;

	private JMenuItem showRowMenuItem;

	private JMenuItem columnWidthMenuItem;

	private JMenuItem insertColumnMenuItem;

	private JMenuItem deleteColumnMenuItem;

	private JMenuItem hideColumnMenuItem;

	private JMenuItem showColumnMenuItem;

	private JMenuItem startBusinessComponentMenuItem;

	private JMenuItem stopBusinessComponentMenuItem;

	private ToolbarButton startBusinessObjectButton;

	private ToolbarButton stopBusinessObjectButton;

	private List<AbstractBusinessObject> treeSelectionWithChildren;

	private JMenuItem downMenuItem;

	private JMenuItem rightMenuItem;

	private JMenuItem upMenuItem;

	private JMenuItem leftMenuItem;

	private JMenuItem startBusinessComponentPopupMenuItem;

	private JMenuItem stopBusinessComponentPopupMenuItem;

	private static Logger log = Logger.getLogger(TopPanel.class);

	/**
	 * Instantiates a new top panel.
	 *
	 * @param basisProperties the basis properties
	 * @param basisClientConnector the basis client connector
	 * @param mainPanel the main panel
	 */
	public TopPanel(final BasisProperties basisProperties, final BasisClientConnector basisClientConnector, final MainPanel mainPanel) {

		super();

		this.mainPanel = mainPanel;
		this.basisClientConnector = basisClientConnector;
		this.statusPanel = new StatusPanel(basisClientConnector);

		initDesktop();

		treeWidth = basisClientConnector.getFUser().getTreePanelWidth();
		mainPanelHeight = basisClientConnector.getFUser().getMainPanelPanelHeight();

		basisClientConnector.addBasisClientConnectorListener(this);

		final GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[] { 0, 0 };
		gbl_topPanel.rowHeights = new int[] { 0, 0 };
		gbl_topPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_topPanel.rowWeights = new double[] { 0.0, 0.0 };
		setLayout(gbl_topPanel);

		treePopup = new JPopupMenu();

		final JMenuBar menuBar = new JMenuBar();
		final GridBagConstraints gbc_menuBar = new GridBagConstraints();
		gbc_menuBar.gridwidth = 2;
		gbc_menuBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_menuBar.gridx = 0;
		gbc_menuBar.gridy = 0;
		add(menuBar, gbc_menuBar);

		final JMenu fileMenu = new JMenu("File");
		fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
		menuBar.add(fileMenu);

		final JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
		mntmNewMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/cancel.png")));
		mntmNewMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		mntmNewMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				exit();
			}
		});

		newBusinessObjectMenuItem = new JMenuItem("New Business Object", 'N');
		newBusinessObjectMenuItem.setEnabled(false);
		newBusinessObjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newBusinessObjectMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/window_new.png")));
		newBusinessObjectMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(newBusinessObjectMenuItem);

		newBusinessObjectMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObject();
			}
		});

		newBusinessObjectGroupMenuItem = new JMenuItem("New Business Object Group", 'G');
		newBusinessObjectGroupMenuItem.setEnabled(false);
		newBusinessObjectGroupMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		newBusinessObjectGroupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_new.png")));
		newBusinessObjectGroupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(newBusinessObjectGroupMenuItem);

		newBusinessObjectGroupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObjectGroup();
			}
		});

		openBusinessObjectMenuItem = new JMenuItem("Open Business Object", 'O');
		openBusinessObjectMenuItem.setEnabled(false);
		openBusinessObjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openBusinessObjectMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/file-manager.png")));
		openBusinessObjectMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(openBusinessObjectMenuItem);

		openBusinessObjectMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				openBusinessObject();
			}
		});

		removeBusinessObjectMenuItem = new JMenuItem("Remove Business Object");
		removeBusinessObjectMenuItem.setEnabled(false);
		removeBusinessObjectMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		removeBusinessObjectMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(removeBusinessObjectMenuItem);

		removeBusinessObjectMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				removeBusinessObject();
			}
		});

		saveMenuItem = new JMenuItem("Save", 'S');
		saveMenuItem.setEnabled(false);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.setEnabled(false);
		saveMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/3floppy_unmount.png")));
		saveMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(saveMenuItem);

		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				TopPanel.this.mainPanel.save();
			}
		});

		saveAllMenuItem = new JMenuItem("Save All", 'A');
		saveAllMenuItem.setEnabled(false);
		saveAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		saveAllMenuItem.setEnabled(false);
		saveAllMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/save_all.png")));
		saveAllMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileMenu.add(saveAllMenuItem);

		saveAllMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				TopPanel.this.mainPanel.saveAll();
			}
		});

		TransferActionListener actionListener = new TransferActionListener();

		final JMenu editMenu = new JMenu("Edit");
		fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
		menuBar.add(editMenu);

		cutMenuItem = new JMenuItem("Cut");
		cutMenuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
		cutMenuItem.addActionListener(actionListener);
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		cutMenuItem.setMnemonic(KeyEvent.VK_T);
		cutMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-cut-6.png")));
		editMenu.add(cutMenuItem);

		copyMenuItem = new JMenuItem("Copy");
		copyMenuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
		copyMenuItem.addActionListener(actionListener);
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenuItem.setMnemonic(KeyEvent.VK_C);
		copyMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-copy-6.png")));
		editMenu.add(copyMenuItem);

		pasteMenuItem = new JMenuItem("Paste");
		pasteMenuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
		pasteMenuItem.addActionListener(actionListener);
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		pasteMenuItem.setMnemonic(KeyEvent.VK_P);
		pasteMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-paste-6.png")));
		editMenu.add(pasteMenuItem);

		editMenu.add(new JSeparator());

		final JMenu fillMenu = new JMenu("Fill");
		editMenu.add(fillMenu);

		downMenuItem = new JMenuItem("Down", 'D');
		downMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		downMenuItem.setEnabled(false);
		downMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fillMenu.add(downMenuItem);

		downMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.FILL_DOWN);

			}
		});

		rightMenuItem = new JMenuItem("Right");
		rightMenuItem.setEnabled(false);
		rightMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fillMenu.add(rightMenuItem);

		rightMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.FILL_RIGHT);

			}
		});

		upMenuItem = new JMenuItem("Up");
		upMenuItem.setEnabled(false);
		upMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fillMenu.add(upMenuItem);

		upMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.FILL_UP);

			}
		});

		leftMenuItem = new JMenuItem("Left");
		leftMenuItem.setEnabled(false);
		leftMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		fillMenu.add(leftMenuItem);

		leftMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.FILL_UP);

			}
		});

		final JSeparator separator = new JSeparator();
		fileMenu.add(separator);
		fileMenu.add(mntmNewMenuItem);

		final JMenu formatMenu = new JMenu("Format");
		formatMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
		menuBar.add(formatMenu);

		clearFormatMenuItem = new JMenuItem("Clear Direct Formatting");
		clearFormatMenuItem.setEnabled(false);
		clearFormatMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		formatMenu.add(clearFormatMenuItem);

		clearFormatMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.CLEAR);

			}
		});

		formatCellsMenuItem = new JMenuItem("Format Cells...");
		formatCellsMenuItem.setEnabled(false);
		formatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		formatMenu.add(formatCellsMenuItem);

		formatCellsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.FORMAT);

			}
		});

		conditionalFormatCellsMenuItem = new JMenuItem("Conditional Formatting...");
		conditionalFormatCellsMenuItem.setEnabled(false);
		conditionalFormatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		formatMenu.add(conditionalFormatCellsMenuItem);

		conditionalFormatCellsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.CONDITIONAL_FORMAT);

			}
		});

		formatMenu.add(new JSeparator());

		final JMenu rowMenu = new JMenu("Row");
		formatMenu.add(rowMenu);

		insertRowMenuItem = new JMenuItem("Insert");
		insertRowMenuItem.setEnabled(false);
		insertRowMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		insertRowMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/insert-table-row.png")));
		rowMenu.add(insertRowMenuItem);

		insertRowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.INSERT_ROW);

			}
		});

		deleteRowMenuItem = new JMenuItem("Delete");
		deleteRowMenuItem.setEnabled(false);
		deleteRowMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		deleteRowMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-delete.png")));
		rowMenu.add(deleteRowMenuItem);

		deleteRowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.DELETE_ROW);

			}
		});

		rowMenu.add(new JSeparator());

		hideRowMenuItem = new JMenuItem("Hide");
		hideRowMenuItem.setEnabled(false);
		hideRowMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		hideRowMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-hide.png")));
		rowMenu.add(hideRowMenuItem);

		hideRowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.HIDE_ROW);

			}
		});

		showRowMenuItem = new JMenuItem("Show");
		showRowMenuItem.setEnabled(false);
		showRowMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		showRowMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-show.png")));
		rowMenu.add(showRowMenuItem);

		showRowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.SHOW_ROW);

			}
		});

		final JMenu columnMenu = new JMenu("Column");
		formatMenu.add(columnMenu);

		columnWidthMenuItem = new JMenuItem("Column Width ...");
		columnWidthMenuItem.setEnabled(false);
		columnWidthMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		columnWidthMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/format-column-width.png")));
		columnMenu.add(columnWidthMenuItem);

		columnWidthMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.COLUMN_WIDTH);

			}
		});

		columnMenu.add(new JSeparator());

		insertColumnMenuItem = new JMenuItem("Insert");
		insertColumnMenuItem.setEnabled(false);
		insertColumnMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		insertColumnMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/insert-table-column.png")));
		columnMenu.add(insertColumnMenuItem);

		insertColumnMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.INSERT_COLUMN);

			}
		});

		deleteColumnMenuItem = new JMenuItem("Delete");
		deleteColumnMenuItem.setEnabled(false);
		deleteColumnMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		deleteColumnMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-delete.png")));
		columnMenu.add(deleteColumnMenuItem);

		deleteColumnMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.DELETE_COLUMN);

			}
		});

		columnMenu.add(new JSeparator());

		hideColumnMenuItem = new JMenuItem("Hide");
		hideColumnMenuItem.setEnabled(false);
		hideColumnMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		hideColumnMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-hide.png")));
		columnMenu.add(hideColumnMenuItem);

		hideColumnMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.HIDE_COLUMN);

			}
		});

		showColumnMenuItem = new JMenuItem("Show");
		showColumnMenuItem.setEnabled(false);
		showColumnMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		showColumnMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-show.png")));
		columnMenu.add(showColumnMenuItem);

		showColumnMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mainPanel.format(FormatAction.SHOW_COLUMN);

			}
		});

		final JMenu extrasMenu = new JMenu("Extras");
		extrasMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
		menuBar.add(extrasMenu);

		startBusinessComponentMenuItem = new JMenuItem("Start Business Component");
		startBusinessComponentMenuItem.setEnabled(false);
		startBusinessComponentMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		startBusinessComponentMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_login.png")));
		extrasMenu.add(startBusinessComponentMenuItem);

		startBusinessComponentMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				startBusinessComponent();
			}
		});

		stopBusinessComponentMenuItem = new JMenuItem("Stop Business Component");
		stopBusinessComponentMenuItem.setEnabled(false);
		stopBusinessComponentMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		stopBusinessComponentMenuItem
				.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/shutdown.png")));
		extrasMenu.add(stopBusinessComponentMenuItem);

		stopBusinessComponentMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				stopBusinessComponent();
			}
		});

		extrasMenu.add(new JSeparator());

		massPermissionChangeMenuItem = new JMenuItem("Mass Permission Change...", 'M');
		massPermissionChangeMenuItem.setEnabled(false);
		massPermissionChangeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		massPermissionChangeMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/advancedsettings.png")));
		massPermissionChangeMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		extrasMenu.add(massPermissionChangeMenuItem);

		massPermissionChangeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				PermissionChangeDialog permissionChangeDialog = new PermissionChangeDialog(basisClientConnector, treeSelection);
				permissionChangeDialog.setLocationRelativeTo(mainPanel);
				permissionChangeDialog.setVisible(true);
			}
		});

		final JMenu helpMenu = new JMenu("Help");
		helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
		menuBar.add(helpMenu);

		if (desktop != null) {

			final JMenuItem helpMenuItem = new JMenuItem("FIX Agora Help", 'F');
			helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			helpMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/sc_helperdialog.png")));
			helpMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
			helpMenu.add(helpMenuItem);

			helpMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {

					try {

						final File file = new File("doc/html/help.html");
						desktop.open(file);
					}
					catch (final Exception e1) {

						JOptionPane.showMessageDialog(TopPanel.this.mainPanel, e1.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}

		final JMenuItem mntmNewMenuItem_8 = new JMenuItem("About FIX Agora");
		mntmNewMenuItem_8.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/info.png")));
		mntmNewMenuItem_8.setFont(new Font("Dialog", Font.PLAIN, 12));
		helpMenu.add(mntmNewMenuItem_8);
		mntmNewMenuItem_8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final AboutDialog aboutDialog = new AboutDialog(basisClientConnector.getFixAgoraPlugins());
				aboutDialog.setLocationRelativeTo(TopPanel.this.mainPanel);
				aboutDialog.setVisible(true);
			}
		});

		for (FIXAgoraPlugin fixAgoraPlugin : basisClientConnector.getFixAgoraPlugins()) {
			JMenuItem jMenuItem = fixAgoraPlugin.getExtraJMenuItem(mainPanel);
			if (jMenuItem != null)
				extrasMenu.add(jMenuItem);
		}

		final JToolBar toolBar = new JToolBar() {

			private static final long serialVersionUID = 1L;

			ImageIcon backImage = new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/background.png"));

			Image image = backImage.getImage();

			@Override
			public void paintComponent(final Graphics g) {

				for (int i = 0; i < getWidth(); i = i + backImage.getIconWidth())
					g.drawImage(image, i, 0, null);
			}
		};

		toolBar.setFloatable(false);
		toolBar.setOpaque(false);
		toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		toolBar.setBorderPainted(false);
		toolBar.setPreferredSize(new Dimension(18, 32));
		final GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 1;
		add(toolBar, gbc_toolBar);

		newBusinessObjectButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/window_new.png")), "Create Business Object");
		toolBar.add(newBusinessObjectButton);

		newBusinessObjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObject();
			}
		});

		newBusinessObjectGroupButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/folder_new.png")), "Create Business Object Group");
		toolBar.add(newBusinessObjectGroupButton);

		newBusinessObjectGroupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObjectGroup();
			}
		});

		openProjectButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/file-manager.png")), "Open Project");
		toolBar.add(openProjectButton);

		openProjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				openBusinessObject();

			}

		});

		saveButton = new ToolbarButton(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/filesave.png")),
				"Save Settings");
		toolBar.add(saveButton);

		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				TopPanel.this.mainPanel.save();
			}
		});

		saveAllButton = new ToolbarButton(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/save_all.png")),
				"Save Settings");
		toolBar.add(saveAllButton);

		saveAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				TopPanel.this.mainPanel.saveAll();
			}
		});

		final JLabel separatorLabel = new JLabel();
		separatorLabel.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/separator.png")));
		separatorLabel.setVerticalAlignment(SwingConstants.TOP);
		toolBar.add(separatorLabel);

		ToolbarButton cutButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/edit-cut-6.png")), "Cut");
		cutButton.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
		cutButton.addActionListener(actionListener);
		cutButton.setEnabled(true);
		toolBar.add(cutButton);

		ToolbarButton copyButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/edit-copy-6.png")), "Copy");
		copyButton.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
		copyButton.addActionListener(actionListener);
		copyButton.setEnabled(true);
		toolBar.add(copyButton);

		ToolbarButton pasteButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/edit-paste-6.png")), "Paste");
		pasteButton.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
		pasteButton.addActionListener(actionListener);
		pasteButton.setEnabled(true);
		toolBar.add(pasteButton);

		final JLabel separatorLabel2 = new JLabel();
		separatorLabel2.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/separator.png")));
		separatorLabel2.setVerticalAlignment(SwingConstants.TOP);
		toolBar.add(separatorLabel2);

		startBusinessObjectButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/agt_login.png")), "Start Business Component");
		toolBar.add(startBusinessObjectButton);

		startBusinessObjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				startBusinessComponent();
			}
		});

		stopBusinessObjectButton = new ToolbarButton(new ImageIcon(
				TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/shutdown.png")), "Stop Business Component");
		toolBar.add(stopBusinessObjectButton);

		stopBusinessObjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				stopBusinessComponent();
			}
		});

		if (desktop != null) {

			final JLabel separatorLabel4 = new JLabel();
			separatorLabel4.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/separator.png")));
			toolBar.add(separatorLabel4);

			final ToolbarButton helpButton = new ToolbarButton(new ImageIcon(
					TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/lc_helperdialog.png")), "FIX Agora Help");
			helpButton.setEnabled(true);
			toolBar.add(helpButton);

			helpButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {

					try {
						final File file = new File("doc/html/help.html");
						desktop.open(file);
					}
					catch (final Exception e1) {

					}
				}
			});

		}

		final JLabel logoLabel = new JLabel();
		logoLabel.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/toolbar-logo.png")));
		final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		add(logoLabel, gbc_lblNewLabel);

		for (int i = 0; i < 10; i++) {
			final JLabel backgroundLabel = new JLabel();
			backgroundLabel.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/background.png")));
			toolBar.add(backgroundLabel);
		}

		startBusinessComponentPopupMenuItem = new JMenuItem("Start Business Component");
		startBusinessComponentPopupMenuItem.setVisible(false);
		startBusinessComponentPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		startBusinessComponentPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_login.png")));
		treePopup.add(startBusinessComponentPopupMenuItem);

		startBusinessComponentPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				startBusinessComponent();
			}
		});

		stopBusinessComponentPopupMenuItem = new JMenuItem("Stop Business Component");
		stopBusinessComponentPopupMenuItem.setVisible(false);
		stopBusinessComponentPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		stopBusinessComponentPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/shutdown.png")));
		treePopup.add(stopBusinessComponentPopupMenuItem);

		stopBusinessComponentPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				stopBusinessComponent();
			}
		});

		newBusinessObjectPopupMenuItem = new JMenuItem("New Business Object");
		newBusinessObjectPopupMenuItem.setEnabled(false);
		newBusinessObjectPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/window_new.png")));
		newBusinessObjectPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		treePopup.add(newBusinessObjectPopupMenuItem);

		newBusinessObjectPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObject();
			}
		});

		newBusinessObjectGroupPopupMenuItem = new JMenuItem("New Business Object Group");
		newBusinessObjectGroupPopupMenuItem.setEnabled(false);
		newBusinessObjectGroupPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_new.png")));
		newBusinessObjectGroupPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		treePopup.add(newBusinessObjectGroupPopupMenuItem);

		newBusinessObjectGroupPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createBusinessObjectGroup();
			}
		});

		openBusinessObjectPopupMenuItem = new JMenuItem("Open Business Object");
		openBusinessObjectPopupMenuItem.setEnabled(false);
		openBusinessObjectPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/file-manager.png")));
		openBusinessObjectPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		treePopup.add(openBusinessObjectPopupMenuItem);

		openBusinessObjectPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				openBusinessObject();
			}
		});

		removeBusinessObjectPopupMenuItem = new JMenuItem("Remove Business Object");
		removeBusinessObjectPopupMenuItem.setEnabled(false);
		removeBusinessObjectPopupMenuItem.setIcon(new ImageIcon(TopPanel.class
				.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		removeBusinessObjectPopupMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
		treePopup.add(removeBusinessObjectPopupMenuItem);

		removeBusinessObjectPopupMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				removeBusinessObject();
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {

				log.info("Stop FIX Agora Client");

				FUser fUser = basisClientConnector.getFUser();

				fUser.setTreePanelWidth(treeWidth);
				fUser.setMainPanelPanelHeight(mainPanelHeight);
				fUser.setOpenBusinessObjects(mainPanel.getOpenBusinessObjects());
				fUser.setExpandSet(expandSet);
				fUser.setXMLDockSettings(mainPanel.getXMLProperties());

				basisClientConnector.sendRequest(new LogoffRequest(fUser, RequestIDManager.getInstance().getID()));
				basisClientConnector.disconnect();
				basisProperties.store();

			}
		});

	}

	/**
	 * Stop business component.
	 */
	protected void stopBusinessComponent() {

		final List<Long> abstractBusinessComponents = new ArrayList<Long>();

		for (AbstractBusinessObject abstractBusinessObject : treeSelectionWithChildren) {
			if (abstractBusinessObject instanceof AbstractBusinessComponent) {
				AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObject;
				if (abstractBusinessComponent.isStartable() && abstractBusinessComponent.getStartLevel() > 0 && abstractBusinessComponent.getStartLevel() < 3) {
					abstractBusinessComponents.add(abstractBusinessComponent.getId());
				}
			}
		}

		basisClientConnector.sendRequest(new StopBusinessComponentRequest(RequestIDManager.getInstance().getID(), abstractBusinessComponents));

	}

	/**
	 * Start business component.
	 */
	protected void startBusinessComponent() {

		final List<Long> abstractBusinessComponents = new ArrayList<Long>();

		for (AbstractBusinessObject abstractBusinessObject : treeSelectionWithChildren) {
			if (abstractBusinessObject instanceof AbstractBusinessComponent) {
				AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObject;
				if (abstractBusinessComponent.isStartable() && abstractBusinessComponent.getStartLevel() == 0) {
					abstractBusinessComponents.add(abstractBusinessComponent.getId());
				}
			}
		}

		basisClientConnector.sendRequest(new StartBusinessComponentRequest(RequestIDManager.getInstance().getID(), abstractBusinessComponents));

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.MainPanelListener#onMainPanelChanged()
	 */
	@Override
	public void onMainPanelChanged() {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				if (mainPanel.isSaveAllowed()) {

					saveButton.setEnabled(true);
					saveMenuItem.setEnabled(true);
				}
				else {

					saveButton.setEnabled(false);
					saveMenuItem.setEnabled(false);
				}

				if (mainPanel.isSaveAllAllowed()) {

					saveAllButton.setEnabled(true);
					saveAllMenuItem.setEnabled(true);
				}
				else {

					saveAllButton.setEnabled(false);
					saveAllMenuItem.setEnabled(false);
				}
				int formatAllowed = mainPanel.getSpreadSheetStatus();

				if (formatAllowed > 0) {

					if (formatAllowed == 2) {
						showColumnMenuItem.setEnabled(true);
						hideColumnMenuItem.setEnabled(true);
						deleteColumnMenuItem.setEnabled(true);
						insertColumnMenuItem.setEnabled(true);
						columnWidthMenuItem.setEnabled(true);
					}
					else {
						showColumnMenuItem.setEnabled(false);
						hideColumnMenuItem.setEnabled(false);
						deleteColumnMenuItem.setEnabled(false);
						insertColumnMenuItem.setEnabled(false);
						columnWidthMenuItem.setEnabled(false);
					}

					clearFormatMenuItem.setEnabled(true);
					formatCellsMenuItem.setEnabled(true);
					conditionalFormatCellsMenuItem.setEnabled(true);

					if (formatAllowed == 3) {
						insertRowMenuItem.setEnabled(true);
						deleteRowMenuItem.setEnabled(true);
						hideRowMenuItem.setEnabled(true);
						showRowMenuItem.setEnabled(true);
					}
					else {
						insertRowMenuItem.setEnabled(false);
						deleteRowMenuItem.setEnabled(false);
						hideRowMenuItem.setEnabled(false);
						showRowMenuItem.setEnabled(false);

					}

					if (formatAllowed < 2) {
						upMenuItem.setEnabled(true);
						rightMenuItem.setEnabled(true);
						downMenuItem.setEnabled(true);
						leftMenuItem.setEnabled(true);
					}
					else {
						upMenuItem.setEnabled(false);
						rightMenuItem.setEnabled(false);
						downMenuItem.setEnabled(false);
						leftMenuItem.setEnabled(false);
					}
				}
				else {

					showColumnMenuItem.setEnabled(false);
					hideColumnMenuItem.setEnabled(false);
					deleteColumnMenuItem.setEnabled(false);
					insertColumnMenuItem.setEnabled(false);
					columnWidthMenuItem.setEnabled(false);
					clearFormatMenuItem.setEnabled(false);
					formatCellsMenuItem.setEnabled(false);
					conditionalFormatCellsMenuItem.setEnabled(false);
					insertRowMenuItem.setEnabled(false);
					deleteRowMenuItem.setEnabled(false);
					hideRowMenuItem.setEnabled(false);
					showRowMenuItem.setEnabled(false);
					upMenuItem.setEnabled(false);
					rightMenuItem.setEnabled(false);
					downMenuItem.setEnabled(false);
					leftMenuItem.setEnabled(false);
				}
			}
		});

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.MainPanelListener#setStatusInfo(javax.swing.ImageIcon, java.lang.String)
	 */
	@Override
	public void setStatusInfo(ImageIcon imageIcon, String text) {

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

		statusPanel.onDisconnected();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

	}

	/**
	 * Exit.
	 */
	public void exit() {

		final WaitDialog waitDialog = new WaitDialog(mainPanel, "Closing ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				System.exit(0);
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
	 * Gets the status panel.
	 *
	 * @return the status panel
	 */
	public StatusPanel getStatusPanel() {

		return statusPanel;
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public MainPanel getMainPanel() {

		return mainPanel;
	}

	private void createBusinessObject() {

		final WaitDialog waitDialog = new WaitDialog(mainPanel, "Create business object...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {
					AbstractBusinessObject abstractBusinessObject = treeSelection.get(0);
					AbstractBusinessObject businessObjectGroup = null;

					if (abstractBusinessObject instanceof BusinessObjectGroup)
						businessObjectGroup = abstractBusinessObject;

					else if (abstractBusinessObject.getParent() instanceof BusinessObjectGroup)
						businessObjectGroup = abstractBusinessObject.getParent();

					if (businessObjectGroup != null && !(businessObjectGroup instanceof BusinessSection)) {

						try {

							AbstractBusinessObject abstractBusinessObject2 = ((BusinessObjectGroup) businessObjectGroup).getChildClass().newInstance();

							abstractBusinessObject2.setParent(businessObjectGroup);

							abstractBusinessObject2.getReadRoles().addAll(businessObjectGroup.getReadRoles());

							abstractBusinessObject2.getWriteRoles().addAll(businessObjectGroup.getWriteRoles());

							abstractBusinessObject2.getExecuteRoles().addAll(businessObjectGroup.getExecuteRoles());

							mainPanel.addBusinessObjectEditor(abstractBusinessObject2, false);

						}
						catch (Exception e) {

							log.error("Bug", e);
						}
					}
				}
				catch (Exception e) {
					log.error("Bug", e);
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

	private void createBusinessObjectGroup() {

		AbstractBusinessObject abstractBusinessObject = treeSelection.get(0);
		AbstractBusinessObject businessObjectGroup = null;

		if (abstractBusinessObject instanceof BusinessObjectGroup)
			businessObjectGroup = abstractBusinessObject;

		else if (abstractBusinessObject.getParent() instanceof BusinessObjectGroup)
			businessObjectGroup = abstractBusinessObject.getParent();

		if (businessObjectGroup != null) {

			try {

				AbstractBusinessObject abstractBusinessObject2 = (AbstractBusinessObject) businessObjectGroup.getClass().newInstance();
				abstractBusinessObject2.setParent(businessObjectGroup);
				abstractBusinessObject2.getReadRoles().addAll(businessObjectGroup.getReadRoles());
				abstractBusinessObject2.getWriteRoles().addAll(businessObjectGroup.getWriteRoles());
				abstractBusinessObject2.getExecuteRoles().addAll(businessObjectGroup.getExecuteRoles());
				abstractBusinessObject2.setName("New " + abstractBusinessObject2.getBusinessObjectName());

				mainPanel.addBusinessObjectEditor(abstractBusinessObject2, false);
			}
			catch (Exception e) {

				log.error("Bug", e);
			}
		}
	}

	private void openBusinessObject() {

		final WaitDialog waitDialog = new WaitDialog(mainPanel, "Open business object...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				for (AbstractBusinessObject abstractBusinessObject : treeSelection)
					mainPanel.addBusinessObjectEditor(abstractBusinessObject, false);
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

	private void removeBusinessObject() {

		final Object[] options = { "Yes", "No" };
		final int n = JOptionPane.showOptionDialog(mainPanel, "Are you sure you want to remove the selected business objects?", "Confirm Delete",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/messagebox_warning.png")), options,
				options[1]);

		if (n == 0) {

			final HashSet<AbstractBusinessObject> abstractBusinessObjects = new HashSet<AbstractBusinessObject>();
			abstractBusinessObjects.addAll(treeSelection);
			String text = "Remove business object ...";
			if (abstractBusinessObjects.size() > 1)
				text = "Remove business objects ...";
			final WaitDialog waitDialog = new WaitDialog(mainPanel, text);

			final SwingWorker<AbstractResponse, Void> swingWorker = new SwingWorker<AbstractResponse, Void>() {

				@Override
				protected AbstractResponse doInBackground() throws Exception {
					
					return basisClientConnector.sendRequest(new RemoveRequest(abstractBusinessObjects, RequestIDManager.getInstance().getID()));
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

						if (abstractResponse instanceof RemoveResponse) {
							RemoveResponse removeResponse = (RemoveResponse) abstractResponse;

							abstractBusinessObjects.removeAll(removeResponse.getAbstractBusinessObjects());
							if (abstractBusinessObjects.size() == 1) {
								AbstractBusinessObject abstractBusinessObject = abstractBusinessObjects.iterator().next();
								JOptionPane.showMessageDialog(TopPanel.this.mainPanel,
										"The following business object could not be removed:\n" + abstractBusinessObject.getName(), "Remove Business Object",
										JOptionPane.ERROR_MESSAGE);
							}
							else if (abstractBusinessObjects.size() > 1) {
								StringBuffer stringBuffer = new StringBuffer("he following business objects could not be removed:");
								Iterator<AbstractBusinessObject> iterator = abstractBusinessObjects.iterator();
								while (iterator.hasNext()) {
									stringBuffer.append("\n");
									stringBuffer.append(iterator.next().getName());
								}
							}
						}
						else if (abstractResponse instanceof ErrorResponse) {
							ErrorResponse errorResponse = (ErrorResponse) abstractResponse;
							JOptionPane.showMessageDialog(TopPanel.this.mainPanel, errorResponse.getText(), "Error", JOptionPane.ERROR_MESSAGE);
						}

					}
					catch (Exception e) {
						
						e.printStackTrace();
					}

				}
			});

		}

	}

	/**
	 * Tree selection changed.
	 *
	 * @param abstractBusinessObjects the abstract business objects
	 * @param abstractBusinessObjectsAndChilds the abstract business objects and childs
	 */
	public void treeSelectionChanged(List<AbstractBusinessObject> abstractBusinessObjects, List<AbstractBusinessObject> abstractBusinessObjectsAndChilds) {

		treeSelection = abstractBusinessObjects;
		treeSelectionWithChildren = abstractBusinessObjectsAndChilds;

		boolean startEnabled = false;
		boolean stopEnabled = false;

		for (AbstractBusinessObject abstractBusinessObject : treeSelectionWithChildren) {
			if (abstractBusinessObject instanceof AbstractBusinessComponent) {
				AbstractBusinessComponent abstractBusinessComponent = (AbstractBusinessComponent) abstractBusinessObject;
				if (abstractBusinessComponent.isStartable() && basisClientConnector.getFUser().canExecute(abstractBusinessObject)) {
					if (abstractBusinessComponent.getStartLevel() != 0 && abstractBusinessComponent.getStartLevel() != 3)
						stopEnabled = true;
					else
						startEnabled = true;
				}
			}
		}

		if (startEnabled) {
			startBusinessComponentMenuItem.setEnabled(true);
			startBusinessObjectButton.setEnabled(true);
			startBusinessComponentPopupMenuItem.setVisible(true);
		}
		else {
			startBusinessComponentMenuItem.setEnabled(false);
			startBusinessObjectButton.setEnabled(false);
			startBusinessComponentPopupMenuItem.setVisible(false);
		}

		if (stopEnabled) {
			stopBusinessComponentMenuItem.setEnabled(true);
			stopBusinessObjectButton.setEnabled(true);
			stopBusinessComponentPopupMenuItem.setVisible(true);
		}
		else {
			stopBusinessComponentMenuItem.setEnabled(false);
			stopBusinessObjectButton.setEnabled(false);
			stopBusinessComponentPopupMenuItem.setVisible(false);
		}

		boolean createEnabled = false;
		boolean createGroupEnabled = false;

		if (treeSelection.size() == 1) {

			AbstractBusinessObject abstractBusinessObject = treeSelection.get(0);

			if (abstractBusinessObject instanceof BusinessObjectGroup && !(abstractBusinessObject instanceof BusinessSection)) {

				if (basisClientConnector.getFUser().canWrite(abstractBusinessObject))
					createEnabled = true;

				else if (abstractBusinessObject.getParent() != null) {
					AbstractBusinessObject parent = mainPanel.getAbstractBusinessObjectForId(abstractBusinessObject.getParent().getId());
					if (basisClientConnector.getFUser().canWrite(parent))
						createEnabled = true;
				}
			}
			if (createEnabled && ((BusinessObjectGroup) abstractBusinessObject).isSubfolderAllowed())
				createGroupEnabled = true;
		}

		boolean removeEnabled = abstractBusinessObjects.size() > 0;

		for (AbstractBusinessObject abstractBusinessObject : treeSelection)

			if (abstractBusinessObject.getParent() == null || abstractBusinessObject.getParent() instanceof BusinessSection
					|| abstractBusinessObject instanceof BusinessSection || !basisClientConnector.getFUser().canWrite(abstractBusinessObject))
				removeEnabled = false;

		removeBusinessObjectPopupMenuItem.setEnabled(removeEnabled);
		removeBusinessObjectMenuItem.setEnabled(removeEnabled);
		massPermissionChangeMenuItem.setEnabled(removeEnabled);

		boolean openEnabled = abstractBusinessObjects.size() > 0;

		openProjectButton.setEnabled(openEnabled);
		openBusinessObjectPopupMenuItem.setEnabled(openEnabled);
		openBusinessObjectMenuItem.setEnabled(openEnabled);

		newBusinessObjectButton.setEnabled(createEnabled);
		newBusinessObjectGroupButton.setEnabled(createGroupEnabled);
		newBusinessObjectPopupMenuItem.setEnabled(createEnabled);
		newBusinessObjectGroupPopupMenuItem.setEnabled(createGroupEnabled);
		newBusinessObjectMenuItem.setEnabled(createEnabled);
		newBusinessObjectGroupMenuItem.setEnabled(createGroupEnabled);
	}

	/**
	 * Gets the tree popup.
	 *
	 * @return the tree popup
	 */
	public JPopupMenu getTreePopup() {

		return treePopup;
	}

	/**
	 * Sets the tree width.
	 *
	 * @param dividerLocation the new tree width
	 */
	public void setTreeWidth(int dividerLocation) {

		this.treeWidth = dividerLocation;
	}

	/**
	 * Sets the table height.
	 *
	 * @param dividerLocation the new table height
	 */
	public void setTableHeight(int dividerLocation) {

		this.mainPanelHeight = dividerLocation;
	}

	/**
	 * Sets the expand set.
	 *
	 * @param expandSet the new expand set
	 */
	public void setExpandSet(Set<Long> expandSet) {

		this.expandSet = expandSet;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public void setHighlightKey(String key) {

		// TODO Auto-generated method stub

	}

	private void initDesktop() {

		if (java.awt.Desktop.isDesktopSupported()) {
			desktop = java.awt.Desktop.getDesktop();

			if (!desktop.isSupported(java.awt.Desktop.Action.OPEN))
				desktop = null;
		}
	}

}
