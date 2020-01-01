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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.CellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;


/**
 * The Class CheckboxField.
 */
public class CheckboxField extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;
	private boolean gradientPaint =false;
	private Color backgroundColor = null;
	private String trueValue = "True";
	private JCheckBox jCheckBox = null;
	private JPanel checkboxPanel =null;

	/**
	 * Instantiates a new checkbox field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public CheckboxField(final CellEditor cellEditor, Color backgroundColor) {

		super();

		this.backgroundColor = backgroundColor;
		
		setLayout(new BorderLayout());
		
		checkboxPanel  = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {
					final Graphics2D graphics2D = (Graphics2D) graphics;

					super.paintComponent(graphics2D);

					final int width = this.getWidth();
					final int height = this.getHeight();
					Color bright = CheckboxField.this.backgroundColor;
					Color dark = bright;
					if (CheckboxField.this.gradientPaint)
						dark = bright.darker();
					final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

					graphics2D.setPaint(gradientPaint);
					graphics2D.fillRect(0, 0, width, height);

					getUI().paint(graphics2D, this);
			}
		};
		
		checkboxPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), backgroundColor));
		
		add(checkboxPanel,BorderLayout.CENTER);
		
		GridBagLayout gbl_checkboxPanel = new GridBagLayout();
		gbl_checkboxPanel.columnWeights = new double[] { 1.0 };
		gbl_checkboxPanel.rowWeights = new double[] { 1.0 };
		checkboxPanel.setLayout(gbl_checkboxPanel);

		GridBagConstraints gbc_calendarTextField = new GridBagConstraints();
		gbc_calendarTextField.insets = new Insets(0, 0, 0, 0);
		gbc_calendarTextField.fill = GridBagConstraints.BOTH;
		gbc_calendarTextField.gridx = 0;
		gbc_calendarTextField.gridy = 0;

		jCheckBox = new JCheckBox(trueValue);
		jCheckBox.setOpaque(false);
		checkboxPanel.add(jCheckBox, gbc_calendarTextField);
		jCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cellEditor.stopCellEditing();

			}
		});
		
		jCheckBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				jCheckBox.dispatchEvent(new ActionEvent(jCheckBox, 0, ""));

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});
		
	}
	
	/**
	 * Sets the gradient paint.
	 *
	 * @param gradientPaint the new gradient paint
	 */
	public void setGradientPaint(boolean gradientPaint) {

		this.gradientPaint = gradientPaint;
	}
	
	/**
	 * Checks if is gradient paint.
	 *
	 * @return true, if is gradient paint
	 */
	public boolean isGradientPaint() {

		return gradientPaint;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color bg) {

		super.setBackground(bg);
		backgroundColor = bg;

	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	public void setHighLightColor(Color highlight)
	{
		checkboxPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), highlight));
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Object getValue()
	{
		return jCheckBox.isSelected();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(),spreadSheetCellFormat.getBackgroundGreen(),spreadSheetCellFormat.getBackgroundBlue()));
		gradientPaint = spreadSheetCellFormat.getGradientPaint();	
		jCheckBox.setForeground(new Color(spreadSheetCellFormat.getForegroundRed(),spreadSheetCellFormat.getForegroundGreen(),spreadSheetCellFormat.getForegroundBlue()));
		trueValue = spreadSheetCellFormat.getTrueValue();
		jCheckBox.setText(trueValue);
		
	}

	/**
	 * Adds the action listener.
	 *
	 * @param actionListener the action listener
	 */
	public void addActionListener(ActionListener actionListener) {

		jCheckBox.addActionListener(actionListener);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		if(!(value instanceof Boolean))
			jCheckBox.setSelected(false);
		else
			jCheckBox.setSelected((Boolean)value);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setBold(boolean)
	 */
	@Override
	public void setBold(boolean bold) {

		if(bold)
		{
			jCheckBox.setFont(jCheckBox.getFont().deriveFont(Font.BOLD));
		}
		else
		{
			jCheckBox.setFont(jCheckBox.getFont().deriveFont(Font.PLAIN));
		}
		
	}
	
}
