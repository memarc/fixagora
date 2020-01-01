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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.model.BasisProperties;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;


/**
 * The Class BasisClientFrame.
 */
public class BasisClientFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private JSplitPane horizontalSplitPane = null;

	private JSplitPane verticalSplitPane = null;

	private MainPanel mainPanel;

	private TopPanel topPanel;
	
	/**
	 * Instantiates a new basis client frame.
	 *
	 * @param waitDialog the wait dialog
	 * @param basisProperties the basis properties
	 * @param basisClientConnector the basis client connector
	 * @param loginResponse the login response
	 * @throws HeadlessException the headless exception
	 */
	public BasisClientFrame(WaitDialog waitDialog, final BasisProperties basisProperties, final BasisClientConnector basisClientConnector, LoginResponse loginResponse) throws HeadlessException {

		super();

		setBounds(100, 100, 640, 480);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(BasisClientFrame.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setTitle("FIX Agora");
		setAlwaysOnTop(false);
		
		verticalSplitPane = new JSplitPane();
		verticalSplitPane.setBorder(new EmptyBorder(2, 2, 0, 2));
		verticalSplitPane.setOpaque(true);
		verticalSplitPane.setBackground(new Color(204, 216, 255));
		verticalSplitPane.setDividerSize(5);
		verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPane.setResizeWeight(0.8);
		verticalSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				
				return new BasicSplitPaneDivider(this) {
					
					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {
						
					}
 
					@Override
					public void paint(Graphics g) {
						
						g.setColor(new Color(204, 216, 255));
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});
		getContentPane().add(verticalSplitPane, BorderLayout.CENTER);

		horizontalSplitPane = new JSplitPane();
		horizontalSplitPane.setOpaque(true);
		horizontalSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		horizontalSplitPane.setDividerSize(5);
		
		horizontalSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				
				return new BasicSplitPaneDivider(this) {
					
					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {
						
					}
 
					@Override
					public void paint(Graphics g) {
						
						g.setColor(new Color(204, 216, 255));
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});
		
		verticalSplitPane.setLeftComponent(horizontalSplitPane);

		
		final Dimension minimumSize = new Dimension(0, 0);
		setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		mainPanel = new MainPanel(this, basisClientConnector);
		mainPanel.setMinimumSize(minimumSize);
		
		topPanel = new TopPanel(basisProperties, basisClientConnector, mainPanel);
		mainPanel.addMainPanelListener(topPanel);
		
		final LogPanel logPanel = new LogPanel(basisClientConnector, loginResponse);
		
		final TreePanel treePanel = new TreePanel(waitDialog, topPanel, basisClientConnector);

		horizontalSplitPane.setLeftComponent(treePanel);

		horizontalSplitPane.setRightComponent(mainPanel);

		getContentPane().add(topPanel, BorderLayout.NORTH);

		getContentPane().add(topPanel.getStatusPanel(), BorderLayout.SOUTH);
		
		verticalSplitPane.setRightComponent(logPanel);

		verticalSplitPane.validate();
		validate();
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
			
				topPanel.exit();				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
			}
		});

		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {

			}

			@Override
			public void componentMoved(final ComponentEvent e) {

			}

			@Override
			public void componentResized(final ComponentEvent e) {

			}

			@Override
			public void componentShown(final ComponentEvent e) {

				

				verticalSplitPane.setDividerLocation(basisClientConnector.getFUser().getMainPanelPanelHeight());
				horizontalSplitPane.setDividerLocation(basisClientConnector.getFUser().getTreePanelWidth());
				
			}
		});

		addWindowStateListener(new WindowStateListener() {

			private boolean notRegistered = true;

			@Override
			public void windowStateChanged(final WindowEvent e) {

				final int oldState = e.getOldState();
				final int newState = e.getNewState();

				if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0) {

					verticalSplitPane.validate();
					validate();

					verticalSplitPane.setDividerLocation(basisClientConnector.getFUser().getMainPanelPanelHeight());
					horizontalSplitPane.setDividerLocation(basisClientConnector.getFUser().getTreePanelWidth());
					verticalSplitPane.resetToPreferredSizes();
					horizontalSplitPane.resetToPreferredSizes();

					if (notRegistered) {
												
						notRegistered = false;
						
						horizontalSplitPane.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(final PropertyChangeEvent evt) {

								if (evt.getPropertyName().equals("dividerLocation")) {
									
									topPanel.setTreeWidth(horizontalSplitPane.getDividerLocation());
									
									verticalSplitPane.validate();
									validate();
								}
							}
						});

						verticalSplitPane.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(final PropertyChangeEvent evt) {

								if (evt.getPropertyName().equals("dividerLocation") && verticalSplitPane.getLastDividerLocation() != -1) {
									
									topPanel.setTableHeight(verticalSplitPane.getDividerLocation());
									
									verticalSplitPane.validate();
									validate();
								}
							}
						});
					}
				}
			}
		});
	}

	/**
	 * On disconnected.
	 */
	public void onDisconnected() {

		topPanel.onDisconnected();
		
	}	
}
