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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.CellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;


/**
 * The Class ButtonField.
 */
public class ButtonField extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;
	private boolean gradientPaint =false;
	private Color backgroundColor = null;
	private String trueValue = "True";
	private JLabel jLabel = null;
	private JPanel textFieldPanel =null;
	private Boolean value = null;
	private String falseValue = "False";

	/**
	 * Instantiates a new button field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public ButtonField(final CellEditor cellEditor, Color backgroundColor) {

		super();

		this.backgroundColor = backgroundColor;
		
		setLayout(new BorderLayout());
		
		textFieldPanel  = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {
					final Graphics2D graphics2D = (Graphics2D) graphics;

					super.paintComponent(graphics2D);

					final int width = this.getWidth();
					final int height = this.getHeight();
					Color bright = ButtonField.this.backgroundColor;
					Color dark = bright;
					if (ButtonField.this.gradientPaint)
						dark = bright.darker();
					final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

					graphics2D.setPaint(gradientPaint);
					graphics2D.fillRect(0, 0, width, height);

					getUI().paint(graphics2D, this);
			}
		};
			
		textFieldPanel.setBackground(backgroundColor);
		textFieldPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		add(textFieldPanel,BorderLayout.CENTER);
		
		GridBagLayout gbl_checkboxPanel = new GridBagLayout();
		gbl_checkboxPanel.columnWeights = new double[] { 1.0 };
		gbl_checkboxPanel.rowWeights = new double[] { 1.0 };
		textFieldPanel.setLayout(gbl_checkboxPanel);

		GridBagConstraints gbc_calendarTextField = new GridBagConstraints();
		gbc_calendarTextField.insets = new Insets(0, 0, 0, 0);
		gbc_calendarTextField.fill = GridBagConstraints.BOTH;
		gbc_calendarTextField.gridx = 0;
		gbc_calendarTextField.gridy = 0;

		jLabel = new JLabel();
		jLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), backgroundColor));
		jLabel.setOpaque(false);
		textFieldPanel.add(jLabel, gbc_calendarTextField);
		jLabel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			
				if(value==null)
					value=true;
				else
					value = new Boolean(!value.booleanValue());
				
				cellEditor.stopCellEditing();
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
		if(textFieldPanel!=null)
		{
			textFieldPanel.setBackground(bg);
			textFieldPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		}
		backgroundColor = bg;

	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	@Override
	public void setForeground(Color color) {

		if (jLabel != null)
			jLabel.setForeground(color);

	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	public void setHighLightColor(Color highlight)
	{
		jLabel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), highlight));
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Object getValue()
	{
		return value;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		trueValue = spreadSheetCellFormat.getTrueValue();
		falseValue = spreadSheetCellFormat.getFalseValue();
		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(),spreadSheetCellFormat.getBackgroundGreen(),spreadSheetCellFormat.getBackgroundBlue()));
		gradientPaint = spreadSheetCellFormat.getGradientPaint();	
		jLabel.setForeground(new Color(spreadSheetCellFormat.getForegroundRed(),spreadSheetCellFormat.getForegroundGreen(),spreadSheetCellFormat.getForegroundBlue()));
		setValue(getValue());
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		if(value instanceof Boolean)
			this.value = (Boolean)value;
		else
			this.value=null;
		if(this.value==null||this.value.booleanValue()==false)
			jLabel.setText(falseValue);
		else
			jLabel.setText(trueValue);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setBold(boolean)
	 */
	@Override
	public void setBold(boolean bold) {

		if(bold)
		{
			jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD));
		}
		else
		{
			jLabel.setFont(jLabel.getFont().deriveFont(Font.PLAIN));
		}
		
	}

}
