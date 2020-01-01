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
package net.sourceforge.fixagora.basis.client.view.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

import org.apache.poi.ss.usermodel.DateUtil;

import com.toedter.calendar.JCalendar;

/**
 * The Class DateChooser.
 */
public class DateChooser extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;

	private DateTextField jTextField = null;

	private Color blue = new Color(204, 216, 255);

	private JButton spinner1PlusButton;

	private boolean dateSelected = false;

	/**
	 * Instantiates a new date chooser.
	 *
	 * @param cellEditor the cell editor
	 * @param bright the bright
	 */
	public DateChooser(final CellEditor cellEditor, Color bright) {

		super();

		this.jTextField = new DateTextField(bright);

		this.jTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cellEditor.stopCellEditing();

			}
		});

		GridBagLayout gbl_numberPanel = new GridBagLayout();
		gbl_numberPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_numberPanel.rowWeights = new double[] { 1.0 };
		setLayout(gbl_numberPanel);

		GridBagConstraints gbc_jPanel = new GridBagConstraints();
		gbc_jPanel.insets = new Insets(0, 0, 0, 0);
		gbc_jPanel.fill = GridBagConstraints.BOTH;
		gbc_jPanel.gridx = 0;
		gbc_jPanel.gridy = 0;
		add(jTextField, gbc_jPanel);

		JPanel plusPanel = new JPanel(new BorderLayout());
		plusPanel.setBorder(new MatteBorder(0, 1, 0, 0, blue.darker()));
		GridBagConstraints gbc_spinner1PlusButton = new GridBagConstraints();
		gbc_spinner1PlusButton.insets = new Insets(0, 0, 0, 0);
		gbc_spinner1PlusButton.gridx = 2;
		gbc_spinner1PlusButton.gridy = 0;
		add(plusPanel, gbc_spinner1PlusButton);

		URL iconURL = getClass().getResource("/com/toedter/calendar/images/JDateChooserIcon.gif");
		ImageIcon icon = new ImageIcon(iconURL);

		spinner1PlusButton = new JButton();
		spinner1PlusButton.setBorderPainted(false);
		spinner1PlusButton.setPreferredSize(new Dimension(25, 25));
		spinner1PlusButton.setMinimumSize(new Dimension(25, 25));
		spinner1PlusButton.setMargin(new Insets(2, 2, 2, 2));
		spinner1PlusButton.setIcon(icon);
		plusPanel.add(spinner1PlusButton, BorderLayout.CENTER);
		spinner1PlusButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

				Calendar calendar = Calendar.getInstance();
				if (getValue() instanceof Double)
				{
					Date date = DateUtil.getJavaDate((Double) getValue());
					if(date!=null)
					calendar.setTime(date);
				}
				final JCalendar jCalendar = new JCalendar();
				jCalendar.setCalendar(calendar);

				final JPopupMenu popup = new JPopupMenu() {

					private static final long serialVersionUID = 1L;

					public void setVisible(boolean b) {

						Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");

						if (b || (!b && dateSelected) || ((isCanceled != null) && !b && isCanceled.booleanValue())) {
							super.setVisible(b);
						}
					}
				};
				popup.setLightWeightPopupEnabled(true);
				popup.add(jCalendar);

				jCalendar.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {

						if (evt.getPropertyName().equals("day")) {
							dateSelected = true;
							popup.setVisible(false);
							DateChooser.this.setValue(DateUtil.getExcelDate(jCalendar.getCalendar().getTime()));
							dateSelected = false;
							jTextField.postActionEvent();
						}
					}
				}

				);
				jCalendar.getDayChooser().setAlwaysFireDayProperty(true);
				int x = spinner1PlusButton.getWidth() - (int) popup.getPreferredSize().getWidth();
				int y = spinner1PlusButton.getY() + spinner1PlusButton.getHeight();

				popup.show(spinner1PlusButton, x, y);
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Object getValue() {

		return jTextField.getValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	public void setHighLightColor(Color highlight) {

		jTextField.setHighLightColor(highlight);
	}

	/**
	 * Adds the action listener.
	 *
	 * @param actionListener the action listener
	 */
	public void addActionListener(ActionListener actionListener) {

		jTextField.addActionListener(actionListener);

	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color bg) {

		super.setBackground(bg);
		if (jTextField != null)
			jTextField.setBackground(bg);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		jTextField.setSpreadSheetCellFormat(spreadSheetCellFormat);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		jTextField.setValue(value);

	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
	 */
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed){
        InputMap map = jTextField.getJTextField().getInputMap(condition);
        ActionMap am = jTextField.getJTextField().getActionMap();

        if(map!=null && am!=null && isEnabled()){
            Object binding = map.get(ks);
            Action action = (binding==null) ? null : am.get(binding);
            if(action!=null){
                return SwingUtilities.notifyAction(action, ks, e, jTextField.getJTextField(),
                        e.getModifiers());
            }
        }
        return false;
    }
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setBold(boolean)
	 */
	@Override
	public void setBold(boolean bold) {

		if(bold)
		{
			jTextField.setFont(jTextField.getFont().deriveFont(Font.BOLD));
		}
		else
		{
			jTextField.setFont(jTextField.getFont().deriveFont(Font.PLAIN));
		}
		
	}
	

}
