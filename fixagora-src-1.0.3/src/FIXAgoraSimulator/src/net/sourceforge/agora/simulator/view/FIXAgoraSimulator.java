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
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableRowSorter;

import net.sourceforge.agora.simulator.control.AgoraAcceptor;
import net.sourceforge.agora.simulator.control.AgoraInitiator;
import net.sourceforge.agora.simulator.control.BlotterListener;
import net.sourceforge.agora.simulator.control.YahooAcceptor;
import net.sourceforge.agora.simulator.model.BlotterEntry;
import net.sourceforge.agora.simulator.model.BlotterEntry.LevelIcon;
import net.sourceforge.agora.simulator.model.BlotterText;
import net.sourceforge.agora.simulator.model.FIXAgoraSimulatorRowFilter;
import net.sourceforge.agora.simulator.model.FIXAgoraSimulatorTableModel;

import org.jquantlib.time.Date;
import org.jquantlib.time.calendars.Germany;
import org.jquantlib.time.calendars.UnitedStates;
import org.jquantlib.time.calendars.UnitedStates.Market;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * The Class FIXAgoraSimulator.
 */
public class FIXAgoraSimulator extends JFrame implements BlotterListener {

	private static final long serialVersionUID = 1L;

	/** The script. */
	static String script = null;

	private static int screenWidth = 0;

	private List<String> clientStartCommands = new ArrayList<String>();

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private List<AgoraInitiator> agoraInitiators = new ArrayList<AgoraInitiator>();

	private List<AgoraAcceptor> agoraAcceptors = new ArrayList<AgoraAcceptor>();

	private FIXAgoraSimulatorTableModel fixAgoraSimulatorMonitorTableModel;

	private Color blue = new Color(82, 122, 255);

	private Image backImage = null;

	private Image logoImage = null;

	private boolean clientsStarted = false;

	private int clientCommandLine = 0;

	private final String[] licenseText = new String[] {
			"FIX Agora is free software; you can redistribute it and/or modify it under the terms of the GNU Library General Public License",
			"as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.",

			"FIX Agora is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of",
			"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more details.",

			"You should have received a copy of the GNU Library General Public License along with this library; if not, write to the",
			"Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA." };

	/**
	 * Instantiates a new fIX agora simulator.
	 */
	public FIXAgoraSimulator() {

		ImageIcon backImageIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/background.png"));
		backImage = backImageIcon.getImage();

		ImageIcon simulatorImageIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/simulator.png"));

		ImageIcon demoImageIcon = new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/demo.png"));

		if (script != null)
			logoImage = demoImageIcon.getImage();
		else
			logoImage = simulatorImageIcon.getImage();

		setMinimumSize(new Dimension(200, 200));
		setTitle("FIX Agora Simulator");
		setIconImage(new ImageIcon(FIXAgoraSimulator.class.getResource("/net/sourceforge/agora/simulator/view/images/16x16/a-logo.png")).getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(200, 200));
		setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 };

		JPanel jPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {

				int imageHeight = backImage.getHeight(this);
				int imageWidth = backImage.getWidth(this);

				if (imageHeight > 0 && imageWidth > 0)
					for (int i = 0; i < getWidth(); i = i + imageWidth)
						for (int j = 0; j < getHeight(); j = j + imageHeight)
							g.drawImage(backImage, i, j, this);

				int width = logoImage.getWidth(this);
				if (width > 0)
					g.drawImage(logoImage, getWidth() / 2 - width / 2, 0, this);
			}
		};

		setContentPane(jPanel);
		getContentPane().setLayout(gridBagLayout);

		final JTable table = new JTable();
		table.setTableHeader(null);

		final JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(70, 25, 5, 25);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);
		scrollPane.setColumnHeaderView(null);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		fixAgoraSimulatorMonitorTableModel = new FIXAgoraSimulatorTableModel(table, scrollPane.getVerticalScrollBar());
		table.setModel(fixAgoraSimulatorMonitorTableModel);

		FIXAgoraSimulatorTableRenderer fixAgoraSimulatorTableRenderer = new FIXAgoraSimulatorTableRenderer();

		table.setDefaultRenderer(Object.class, fixAgoraSimulatorTableRenderer);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.setBackground(new Color(255, 243, 204));
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		
		TableRowSorter<FIXAgoraSimulatorTableModel> tableRowSorter = new TableRowSorter<FIXAgoraSimulatorTableModel>(fixAgoraSimulatorMonitorTableModel);
		final FIXAgoraSimulatorRowFilter fixAgoraSimulatorRowFilter = new FIXAgoraSimulatorRowFilter();
		tableRowSorter.setRowFilter(fixAgoraSimulatorRowFilter);
		table.setRowSorter(tableRowSorter);

		scrollPane.setViewportView(table);
		
		final JCheckBox hideMessagesCheckBox = new JCheckBox("Hide Messages");
		hideMessagesCheckBox.setForeground(Color.WHITE);
		hideMessagesCheckBox.setIcon(new ColoredCheckBoxIcon(new Color(255, 243, 204)));
		hideMessagesCheckBox.setOpaque(false);
		hideMessagesCheckBox.setSelected(true);
		hideMessagesCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 25, 25, 5);
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 1;
		jPanel.add(hideMessagesCheckBox, gbc_chckbxNewCheckBox);

		hideMessagesCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fixAgoraSimulatorRowFilter.setHideMessage(hideMessagesCheckBox.isSelected());
				fixAgoraSimulatorMonitorTableModel.setHideMessage(hideMessagesCheckBox.isSelected());
				fixAgoraSimulatorMonitorTableModel.fireTableDataChanged();

			}
		});

		final JCheckBox showBankOfAphrodite = new JCheckBox("Show Bank of Aphrodite");
		fixAgoraSimulatorRowFilter.addCounterparty("Bank of Aphrodite");
		showBankOfAphrodite.setForeground(Color.WHITE);
		showBankOfAphrodite.setIcon(new ColoredCheckBoxIcon(new Color(255, 243, 204)));
		showBankOfAphrodite.setOpaque(false);
		showBankOfAphrodite.setSelected(true);
		showBankOfAphrodite.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_showBankOfAphrodite = new GridBagConstraints();
		gbc_showBankOfAphrodite.insets = new Insets(0, 25, 25, 5);
		gbc_showBankOfAphrodite.anchor = GridBagConstraints.WEST;
		gbc_showBankOfAphrodite.gridx = 1;
		gbc_showBankOfAphrodite.gridy = 1;
		jPanel.add(showBankOfAphrodite, gbc_showBankOfAphrodite);

		showBankOfAphrodite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (showBankOfAphrodite.isSelected())
					fixAgoraSimulatorRowFilter.addCounterparty("Bank of Aphrodite");
				else
					fixAgoraSimulatorRowFilter.removeCounterparty("Bank of Aphrodite");
				fixAgoraSimulatorMonitorTableModel.fireTableDataChanged();

			}
		});

		final JCheckBox showBankOfHades = new JCheckBox("Show Bank of Hades");
		fixAgoraSimulatorRowFilter.addCounterparty("Bank of Hades");
		showBankOfHades.setForeground(Color.WHITE);
		showBankOfHades.setIcon(new ColoredCheckBoxIcon(new Color(255, 243, 204)));
		showBankOfHades.setOpaque(false);
		showBankOfHades.setSelected(true);
		showBankOfHades.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_showBankOfHades = new GridBagConstraints();
		gbc_showBankOfHades.insets = new Insets(0, 25, 25, 5);
		gbc_showBankOfHades.anchor = GridBagConstraints.WEST;
		gbc_showBankOfHades.gridx = 2;
		gbc_showBankOfHades.gridy = 1;
		jPanel.add(showBankOfHades, gbc_showBankOfHades);

		showBankOfHades.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (showBankOfHades.isSelected())
					fixAgoraSimulatorRowFilter.addCounterparty("Bank of Hades");
				else
					fixAgoraSimulatorRowFilter.removeCounterparty("Bank of Hades");
				fixAgoraSimulatorMonitorTableModel.fireTableDataChanged();

			}
		});

		final JCheckBox showBankOfPoseidon = new JCheckBox("Show Bank of Poseidon");
		fixAgoraSimulatorRowFilter.addCounterparty("Bank of Poseidon");
		showBankOfPoseidon.setForeground(Color.WHITE);
		showBankOfPoseidon.setIcon(new ColoredCheckBoxIcon(new Color(255, 243, 204)));
		showBankOfPoseidon.setOpaque(false);
		showBankOfPoseidon.setSelected(true);
		showBankOfPoseidon.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_showBankOfPoseidon = new GridBagConstraints();
		gbc_showBankOfPoseidon.insets = new Insets(0, 25, 25, 5);
		gbc_showBankOfPoseidon.anchor = GridBagConstraints.WEST;
		gbc_showBankOfPoseidon.gridx = 3;
		gbc_showBankOfPoseidon.gridy = 1;
		jPanel.add(showBankOfPoseidon, gbc_showBankOfPoseidon);

		showBankOfPoseidon.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (showBankOfPoseidon.isSelected())
					fixAgoraSimulatorRowFilter.addCounterparty("Bank of Poseidon");
				else
					fixAgoraSimulatorRowFilter.removeCounterparty("Bank of Poseidon");
				fixAgoraSimulatorMonitorTableModel.fireTableDataChanged();

			}
		});

		final JCheckBox showBankOfZeus = new JCheckBox("Show Bank of Zeus");
		fixAgoraSimulatorRowFilter.addCounterparty("Bank of Zeus");
		showBankOfZeus.setForeground(Color.WHITE);
		showBankOfZeus.setIcon(new ColoredCheckBoxIcon(new Color(255, 243, 204)));
		showBankOfZeus.setOpaque(false);
		showBankOfZeus.setSelected(true);
		showBankOfZeus.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_showBankOfZeus = new GridBagConstraints();
		gbc_showBankOfZeus.insets = new Insets(0, 25, 25, 5);
		gbc_showBankOfZeus.anchor = GridBagConstraints.WEST;
		gbc_showBankOfZeus.gridx = 4;
		gbc_showBankOfZeus.gridy = 1;
		jPanel.add(showBankOfZeus, gbc_showBankOfZeus);

		showBankOfZeus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (showBankOfZeus.isSelected())
					fixAgoraSimulatorRowFilter.addCounterparty("Bank of Zeus");
				else
					fixAgoraSimulatorRowFilter.removeCounterparty("Bank of Zeus");
				fixAgoraSimulatorMonitorTableModel.fireTableDataChanged();

			}
		});

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

				setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);

				executorService.execute(new Runnable() {

					@Override
					public void run() {

						FontMetrics fontMetrics = getFontMetrics(new Font("Dialog", Font.PLAIN, 12));
						int licenseWidth = fontMetrics.stringWidth(licenseText[0]);
						int licenseOffset = screenWidth / 2 - licenseWidth / 2;
						BlotterText blotterText = new BlotterText("FIX Agora Demo", blue);
						if (script == null)
							blotterText = new BlotterText("FIX Agora Simulator", blue);
						BlotterEntry blotterEntry1 = new BlotterEntry(null, blotterText);
						blotterEntry1.setOffset(licenseOffset + licenseWidth / 2 - fontMetrics.stringWidth("FIX Agora Demo Suite") / 2);
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(blotterEntry1);
						BlotterEntry blotterEntry2 = new BlotterEntry(null, new BlotterText("Version 1.0.3 - Copyright (C) 2012-2015 Alexander Pinnow", blue));
						blotterEntry2.setOffset(licenseOffset + licenseWidth / 2
								- fontMetrics.stringWidth("Version 1.0.3 - Copyright (C) 2012-2015 Alexander Pinnow") / 2);
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(blotterEntry2);
						for (String text : licenseText) {
							BlotterEntry licenseEntry = new BlotterEntry(null, new BlotterText(text, Color.GRAY));
							licenseEntry.setOffset(licenseOffset);
							fixAgoraSimulatorMonitorTableModel.addBlotterEntry(licenseEntry);

						}
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, ""));
						
						if (script != null)
							fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Welcome to the FIX Agora Demo", LevelIcon.INFO));
						else
							fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Welcome to the FIX Agora Simulator", LevelIcon.INFO));
						init();

					}
				});

			}

			@Override
			public void componentResized(ComponentEvent e) {

				if (table.getColumnModel().getColumn(1).getPreferredWidth() < scrollPane.getViewport().getWidth()) {
					table.getColumnModel().getColumn(1).setPreferredWidth(scrollPane.getViewport().getWidth());
				}

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
	}

	private void init() {

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Checking available ports ...", LevelIcon.INFO));

		if (script != null) {

			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4711.", Color.GRAY), LevelIcon.RUN));

			if (isPortFree(4711)) {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4711 is available.", LevelIcon.SUCESS));
			}
			else {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4711 is not available.", LevelIcon.ERROR));
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
						"Check that no other application is using the port or an instance of FIX Agora Server is already started.", LevelIcon.INFO));
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
				return;
			}
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4713.", Color.GRAY), LevelIcon.RUN));

		if (isPortFree(4713)) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4713 is available.", LevelIcon.SUCESS));
		}
		else {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4713 is not available.", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Check that no other application is using the port or another instance of this simulator is already started.", LevelIcon.INFO));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
			return;
		}

		if (script != null) {

			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4714.", Color.GRAY), LevelIcon.RUN));

			if (isPortFree(4711)) {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4714 is available.", LevelIcon.SUCESS));
			}
			else {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4714 is not available.", LevelIcon.ERROR));
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
						"Check that no other application is using the port or an instance of FIX Agora Server is already started.", LevelIcon.INFO));
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
				return;
			}
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4715.", Color.GRAY), LevelIcon.RUN));

		if (isPortFree(4715)) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4715 is available.", LevelIcon.SUCESS));
		}
		else {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4715 is not available.", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Check that no other application is using the port or another instance of this simulator is already started.", LevelIcon.INFO));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
			return;
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4716.", Color.GRAY), LevelIcon.RUN));

		if (isPortFree(4716)) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4716 is available.", LevelIcon.SUCESS));
		}
		else {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4716 is not available.", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Check that no other application is using the port or another instance of this simulator is already started.", LevelIcon.INFO));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
			return;
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4717.", Color.GRAY), LevelIcon.RUN));

		if (isPortFree(4717)) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4717 is available.", LevelIcon.SUCESS));
		}
		else {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4717 is not available.", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Check that no other application is using the port or another instance of this simulator is already started.", LevelIcon.INFO));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
			return;
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Checking port 4718.", Color.GRAY), LevelIcon.RUN));

		if (isPortFree(4718)) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4718 is available.", LevelIcon.SUCESS));
		}
		else {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Port 4718 is not available.", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Check that no other application is using the port or another instance of this simulator is already started.", LevelIcon.INFO));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator is stopped.", LevelIcon.INFO));
			return;
		}

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
				"FIX Agora Simulator is emulating a FIX protocol based realtime feed using Yahoo! Finance.", LevelIcon.INFO));

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Checking access to Yahoo! Finance ...", LevelIcon.INFO));

		try {
			StringBuffer url = new StringBuffer("http://finance.yahoo.com");
			URL yahoo = new URL(url.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));
			in.close();

			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Simulator can connect to Yahoo! Finance.", LevelIcon.SUCESS));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Provided realtime data are for demo purposes only!", LevelIcon.WARNING));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
					"Please read the Yahoo! Finance Web Services Terms of Use under http://finance.yahoo.com/badges/tos", LevelIcon.INFO));

			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Checking market data availability ...", LevelIcon.INFO));

			boolean isUsTicking = true;

			UnitedStates unitedStates = new UnitedStates(Market.NYSE);
			if (unitedStates.isBusinessDay(new Date(new java.util.Date()))) {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Today is a business day on wall street.", LevelIcon.INFO));
				java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
				if (calendar.get(java.util.Calendar.HOUR_OF_DAY) < 9
						|| (calendar.get(java.util.Calendar.HOUR_OF_DAY) < 10 && calendar.get(java.util.Calendar.MINUTE) < 30)) {
					fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "New York Stock Exchange is still closed.", LevelIcon.WARNING));
					isUsTicking = false;
				}
				if (calendar.get(java.util.Calendar.HOUR_OF_DAY) > 15) {
					fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "New York Stock Exchange is already closed.", LevelIcon.WARNING));
					isUsTicking = false;
				}
			}
			else {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Today is no business day on wall street.", LevelIcon.WARNING));
				isUsTicking = false;
			}
			if (isUsTicking)
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "There will be realtime data for U.S. Treasuries and equities.",
						LevelIcon.SUCESS));
			else
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "There will be no realtime data for U.S. Treasuries and equities.",
						LevelIcon.WARNING));

			boolean isGermanyTicking = true;

			Germany germany = new Germany(org.jquantlib.time.calendars.Germany.Market.FrankfurtStockExchange);
			if (germany.isBusinessDay(new Date(new java.util.Date()))) {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Today is a business day in Germany.", LevelIcon.INFO));
				java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
				if (calendar.get(java.util.Calendar.HOUR_OF_DAY) < 9
						|| (calendar.get(java.util.Calendar.HOUR_OF_DAY) < 10 && calendar.get(java.util.Calendar.MINUTE) < 30)) {
					fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Frankfurt Stock Exchange is still closed.", LevelIcon.WARNING));
					isGermanyTicking = false;
				}
				if (calendar.get(java.util.Calendar.HOUR_OF_DAY) > 17) {
					fixAgoraSimulatorMonitorTableModel
							.addBlotterEntry(new BlotterEntry(null, "Frankfurt Stock Exchange is already closed.", LevelIcon.WARNING));
					isGermanyTicking = false;
				}
			}
			else {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Today is no business day in Germany.", LevelIcon.WARNING));
				isGermanyTicking = false;
			}
			if (isGermanyTicking)
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "There will be realtime data for german futures and equities.",
						LevelIcon.SUCESS));
			else
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "There will be no realtime data for german futures and equities.",
						LevelIcon.WARNING));

		}
		catch (Exception e1) {
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "No connection to  Yahoo! Finance", LevelIcon.ERROR));
			fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "The simulator will not provide realtime data. Will coninue anyway ...",
					LevelIcon.INFO));
		}

		YahooAcceptor fixAcceptor = new YahooAcceptor();
		try {
			fixAcceptor.connect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		fixAgoraSimulatorMonitorTableModel
				.addBlotterEntry(new BlotterEntry(
						null,
						"FIX Agora Simulator is emulating four FIX Agora Server instances for the counterparties Bank of Aphrodite, Bank of Hades, Bank of Poseidon and Bank of Zeus.",
						LevelIcon.INFO));
		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "All institutions are acting as buy side and sell side.", LevelIcon.INFO));
		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Starting the simulation ...", LevelIcon.INFO));

		List<String> traderBOA = new ArrayList<String>();
		traderBOA.add("adams");
		traderBOA.add("abraham");
		traderBOA.add("archer");

		AgoraInitiator agoraInitiatorBOA = new AgoraInitiator("BOA", 1, 1.25, traderBOA);
		agoraInitiatorBOA.addBlotterListener(this);
		agoraInitiators.add(agoraInitiatorBOA);

		AgoraAcceptor agoraAcceptorBOA = new AgoraAcceptor("BOA", traderBOA);
		agoraAcceptorBOA.addBlotterListener(this);
		agoraAcceptors.add(agoraAcceptorBOA);
		agoraInitiatorBOA.addAgoraAcceptor(agoraAcceptorBOA);
		agoraAcceptorBOA.addAgoraInitiator(agoraInitiatorBOA);
		agoraInitiatorBOA.connect();
		agoraAcceptorBOA.connect();

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Simulation of FIX Agora Server for Bank of Aphrodite started.",
				LevelIcon.SUCESS));

		List<String> traderBOH = new ArrayList<String>();
		traderBOH.add("harrow");
		traderBOH.add("hooverman");
		traderBOH.add("heppner");

		AgoraInitiator agoraInitiatorBOH = new AgoraInitiator("BOH", -1, 0.75, traderBOH);
		agoraInitiatorBOH.addBlotterListener(this);
		agoraInitiators.add(agoraInitiatorBOH);

		AgoraAcceptor agoraAcceptorBOH = new AgoraAcceptor("BOH", traderBOH);
		agoraAcceptorBOH.addBlotterListener(this);
		agoraAcceptors.add(agoraAcceptorBOH);
		agoraInitiatorBOH.addAgoraAcceptor(agoraAcceptorBOH);
		agoraAcceptorBOH.addAgoraInitiator(agoraInitiatorBOH);
		agoraInitiatorBOH.connect();
		agoraAcceptorBOH.connect();

		fixAgoraSimulatorMonitorTableModel
				.addBlotterEntry(new BlotterEntry(null, "Simulation of FIX Agora Server for Bank of Hades started.", LevelIcon.SUCESS));

		List<String> traderBOP = new ArrayList<String>();
		traderBOP.add("pullmann");
		traderBOP.add("peterson");
		traderBOP.add("palin");

		AgoraInitiator agoraInitiatorBOP = new AgoraInitiator("BOP", -2, 0.5, traderBOP);
		agoraInitiatorBOP.addBlotterListener(this);
		agoraInitiators.add(agoraInitiatorBOP);

		AgoraAcceptor agoraAcceptorBOP = new AgoraAcceptor("BOP", traderBOP);
		agoraAcceptorBOP.addBlotterListener(this);
		agoraAcceptors.add(agoraAcceptorBOP);
		agoraInitiatorBOP.addAgoraAcceptor(agoraAcceptorBOP);
		agoraAcceptorBOP.addAgoraInitiator(agoraInitiatorBOP);
		agoraInitiatorBOP.connect();
		agoraAcceptorBOP.connect();

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Simulation of FIX Agora Server for Bank of Poseidon started.",
				LevelIcon.SUCESS));

		List<String> traderBOZ = new ArrayList<String>();
		traderBOZ.add("zoidberg");
		traderBOZ.add("zacharias");
		traderBOZ.add("zuckerman");

		AgoraInitiator agoraInitiatorBOZ = new AgoraInitiator("BOZ", 2, 1.5, traderBOZ);
		agoraInitiatorBOZ.addBlotterListener(this);
		agoraInitiators.add(agoraInitiatorBOZ);

		AgoraAcceptor agoraAcceptorBOZ = new AgoraAcceptor("BOZ", traderBOZ);
		agoraAcceptorBOZ.addBlotterListener(this);
		agoraAcceptors.add(agoraAcceptorBOZ);
		agoraInitiatorBOZ.addAgoraAcceptor(agoraAcceptorBOZ);
		agoraAcceptorBOZ.addAgoraInitiator(agoraInitiatorBOZ);
		agoraInitiatorBOZ.connect();
		agoraAcceptorBOZ.connect();

		fixAgoraSimulatorMonitorTableModel
				.addBlotterEntry(new BlotterEntry(null, "Simulation of FIX Agora Server for Bank of Zeus started.", LevelIcon.SUCESS));

		if (script != null) {
			try {
				FileInputStream fstream = new FileInputStream("./conf/" + script);

				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;

				boolean first = true;

				while ((strLine = br.readLine()) != null) {
					if (first) {
						first = false;
						fixAgoraSimulatorMonitorTableModel
								.addBlotterEntry(new BlotterEntry(null,
										"FIX Agora Simulator is now starting a physical FIX Agora Server representing the Bank of Midas. This is you.",
										LevelIcon.INFO));
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
								"Starting the server may take some time. A new console will pop up.", LevelIcon.INFO));
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Starting the server ...", LevelIcon.INFO));
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Launch " + strLine, Color.GRAY),
								LevelIcon.RUN));
						Runtime.getRuntime().exec(strLine);

					}
					else
						clientStartCommands.add(strLine);
				}
				br.close();
			}
			catch (Exception e) {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, e.getMessage(), LevelIcon.ERROR));
			}
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Get size of each screen
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			screenWidth = dm.getWidth();
		}

		if (args.length == 1)
			script = args[0];

		try {
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		}
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		FIXAgoraSimulator simulatorFrame = new FIXAgoraSimulator();
		simulatorFrame.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.agora.simulator.control.BlotterListener#writeBlotterEntry(net.sourceforge.agora.simulator.model.BlotterEntry)
	 */
	@Override
	public void writeBlotterEntry(BlotterEntry blotterEntry) {

		fixAgoraSimulatorMonitorTableModel.addBlotterEntry(blotterEntry);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.agora.simulator.control.BlotterListener#onLogon(boolean)
	 */
	@Override
	public synchronized void onLogon(boolean fromServer) {

		if (clientStartCommands.size() > clientCommandLine && !(clientsStarted && fromServer)) {
			
			clientsStarted = true;

			if (clientCommandLine == 0) {

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "FIX Agora Server of Bank of Midas is started.", LevelIcon.SUCESS));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
						"FIX Agora Demo will start now three different clients for Bank of Midas. The login procedure will be skipped.", LevelIcon.INFO));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "The clients are customized to fit the following business roles:",
						LevelIcon.INFO));

				List<BlotterText> blotterTexts = new ArrayList<BlotterText>();

				blotterTexts.add(new BlotterText("Buy Side "));

				blotterTexts.add(new BlotterText("trader: Bill Buyside User: ", Color.GRAY));

				blotterTexts.add(new BlotterText("bill"));

				blotterTexts.add(new BlotterText(" Password: ", Color.GRAY));

				blotterTexts.add(new BlotterText("bill123"));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, blotterTexts, LevelIcon.INFO));

				List<BlotterText> blotterTexts2 = new ArrayList<BlotterText>();

				blotterTexts2.add(new BlotterText("Sell side "));

				blotterTexts2.add(new BlotterText("trader: Sally Sellside User: ", Color.GRAY));

				blotterTexts2.add(new BlotterText("sally"));

				blotterTexts2.add(new BlotterText(" Password:  ", Color.GRAY));

				blotterTexts2.add(new BlotterText("sally123"));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, blotterTexts2, LevelIcon.INFO));

				List<BlotterText> blotterTexts3 = new ArrayList<BlotterText>();

				blotterTexts3.add(new BlotterText("System Administrator"));

				blotterTexts3.add(new BlotterText(": User: ", Color.GRAY));

				blotterTexts3.add(new BlotterText("Admin"));

				blotterTexts3.add(new BlotterText(" Password:  ", Color.GRAY));

				blotterTexts3.add(new BlotterText("admin"));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, blotterTexts3, LevelIcon.INFO));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
						"You can identify the user at the bottom right of the client application window.", LevelIcon.INFO));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "You should close two of the clients if you work on a slow machine.",
						LevelIcon.INFO));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Be careful when you work as admin!", LevelIcon.WARNING));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null,
						"The demo ships with 300 securities. Client initialization may take some time.", LevelIcon.INFO));

				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, "Starting the clients ...", LevelIcon.INFO));

			}

			String command = clientStartCommands.get(clientCommandLine);
			try {
				fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, new BlotterText("Launch " + command, Color.GRAY), LevelIcon.RUN));
				Process process = Runtime.getRuntime().exec(command);
				LogStreamReader lsr = new LogStreamReader(process.getInputStream());
				Thread thread = new Thread(lsr);
				thread.start();

			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private boolean isPortFree(int portNumber) {

		ServerSocket socket = null;
		try {
			socket = new ServerSocket(portNumber, 0, InetAddress.getByName("127.0.0.1"));
			return true;
		}
		catch (IOException e) {
			return false;
		}
		finally {

			try {
				if (socket != null)
					socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class LogStreamReader implements Runnable {

		private BufferedReader reader;

		public LogStreamReader(InputStream is) {

			this.reader = new BufferedReader(new InputStreamReader(is));
		}

		public void run() {

			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("client is started")) {
						
						StringBuffer message = new StringBuffer("FIX Agora Client");
						String[] commandSplit = clientStartCommands.get(clientCommandLine).split("\\s+");
						if (commandSplit.length > 2) {
							message.append(" for user ");
							message.append(commandSplit[commandSplit.length - 2]);
						}
						message.append(" is started.");
						fixAgoraSimulatorMonitorTableModel.addBlotterEntry(new BlotterEntry(null, message.toString(), LevelIcon.SUCESS));
						
						clientCommandLine++;
						onLogon(false);
					}
				}
				reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
