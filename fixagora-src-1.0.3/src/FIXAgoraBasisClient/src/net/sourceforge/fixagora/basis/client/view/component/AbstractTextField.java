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
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.Document;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

/**
 * The Class AbstractTextField.
 */
public abstract class AbstractTextField extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;

	/** The j text field. */
	protected JTextField jTextField = new JTextField();

	private boolean gradientPaint = false;

	/** The background color. */
	protected Color backgroundColor = null;

	/** The highlight color. */
	protected Color highlightColor = null;

	/** The spread sheet cell format. */
	protected SpreadSheetCellFormat spreadSheetCellFormat = null;
	
	/** The date formats. */
	protected List<DateFormat> dateFormats = new ArrayList<DateFormat>();
	
	/**
	 * Instantiates a new abstract text field.
	 *
	 * @param backgroundColor the background color
	 */
	public AbstractTextField(Color backgroundColor) {

		super();

		this.backgroundColor = backgroundColor;
		this.highlightColor = backgroundColor;

		jTextField = new JTextField() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {
					final Graphics2D graphics2D = (Graphics2D) graphics;

					super.paintComponent(graphics2D);

					final int width = this.getWidth();
					final int height = this.getHeight();
					Color bright = AbstractTextField.this.backgroundColor;
					Color dark = bright;
					if (AbstractTextField.this.gradientPaint)
						dark = bright.darker();
					final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

					graphics2D.setPaint(gradientPaint);
					graphics2D.fillRect(0, 0, width, height);

					getUI().paint(graphics2D, this);
			}

		};
		
		jTextField.setOpaque(false);
		
		jTextField.setBorder(new EmptyBorder(0, 5, 0, 5));
		setLayout(new BorderLayout());
		add(jTextField,BorderLayout.CENTER);
		setBorder(new MatteBorder(new Insets(1, 1, 1, 1), backgroundColor));

	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public Document getDocument()
	{
		return jTextField.getDocument();
	}
	
	/**
	 * Adds the action listener.
	 *
	 * @param actionListener the action listener
	 */
	public void addActionListener(ActionListener actionListener) {

		jTextField.addActionListener(actionListener);

	}

	/**
	 * Post action event.
	 */
	public void postActionEvent() {

		jTextField.postActionEvent();

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
		setBorder(new MatteBorder(new Insets(1, 1, 1, 1), backgroundColor));

	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	@Override
	public void setForeground(Color color) {

		if (jTextField != null)
			jTextField.setForeground(color);

	}

	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	public void setHighLightColor(Color highlight)
	{
		this.highlightColor  = highlight;
		setBorder(new MatteBorder(new Insets(1, 1, 1, 1), highlight));
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	@Override
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		this.spreadSheetCellFormat  = spreadSheetCellFormat;
		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(),spreadSheetCellFormat.getBackgroundGreen(),spreadSheetCellFormat.getBackgroundBlue()));
		setGradientPaint(spreadSheetCellFormat.getGradientPaint());	
		jTextField.setForeground(new Color(spreadSheetCellFormat.getForegroundRed(),spreadSheetCellFormat.getForegroundGreen(),spreadSheetCellFormat.getForegroundBlue()));
	}

	
	/**
	 * Gets the j text field.
	 *
	 * @return the j text field
	 */
	public JTextField getJTextField() {
	
		return jTextField;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		jTextField.setDisabledTextColor(jTextField.getForeground());
		jTextField.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	@Override
	public Object getValue() {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		
		jTextField.addMouseListener(l);
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
