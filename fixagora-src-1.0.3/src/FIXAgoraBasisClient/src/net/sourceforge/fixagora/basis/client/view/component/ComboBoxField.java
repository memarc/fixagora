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
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * The Class ComboBoxField.
 */
public class ComboBoxField extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;
	private boolean gradientPaint = false;
	private Color backgroundColor = null;
	private JComboBox jComboBox;
	private String value = null;
	private Color highlight = null;

	/**
	 * Instantiates a new combo box field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 */
	public ComboBoxField(final CellEditor cellEditor, Color backgroundColor) {

		super();
		this.backgroundColor = backgroundColor;
		this.highlight = backgroundColor;

		setLayout(new BorderLayout());

		jComboBox = new JComboBox();
		jComboBox.setRenderer(new HighlightComboRenderer());
		jComboBox.setUI(new BasicComboBoxUI());

		add(jComboBox, BorderLayout.CENTER);

		jComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(cellEditor!=null)
					cellEditor.stopCellEditing();

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
	public void setHighLightColor(Color highlight) {

		this.highlight = highlight;
		jComboBox.repaint();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	public Object getValue() {

		if (jComboBox.getSelectedItem() != null)
			return (String) jComboBox.getSelectedItem();
		else
			return value;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(),spreadSheetCellFormat.getBackgroundGreen(),spreadSheetCellFormat.getBackgroundBlue()));
		setGradientPaint(spreadSheetCellFormat.getGradientPaint());	
		jComboBox.setForeground(new Color(spreadSheetCellFormat.getForegroundRed(),spreadSheetCellFormat.getForegroundGreen(),spreadSheetCellFormat.getForegroundBlue()));
		
		List<String> predefinedStrings = spreadSheetCellFormat.getPredefinedStrings();
		ListComboBoxModel<String> comboBoxModel = new ListComboBoxModel<String>(predefinedStrings);
		jComboBox.setModel(comboBoxModel);
		setValue(getValue());

	}

	private class HighlightComboRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		public HighlightComboRenderer() {

			super();
			setOpaque(false);
			setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 1, highlight), new EmptyBorder(2, 5, 2, 5)));
		}

		public Component getListCellRendererComponent(JList list, Object obj, int row, boolean sel, boolean hasFocus) {

			setText((String) obj);
			return this;
		}

		@Override
		public void paintComponent(Graphics graphics) {

			final Graphics2D graphics2D = (Graphics2D) graphics;

			setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 1, highlight), new EmptyBorder(5, 5, 5, 5)));

			super.paintComponent(graphics2D);

			final int width = this.getWidth();
			final int height = this.getHeight();
			Color bright = ComboBoxField.this.backgroundColor;
			Color dark = bright;
			if (ComboBoxField.this.gradientPaint)
				dark = bright.darker();
			final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

			graphics2D.setPaint(gradientPaint);
			graphics2D.fillRect(0, 0, width, height);

			getUI().paint(graphics2D, this);
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		if (value instanceof String) {
			this.value = (String) value;
			jComboBox.setSelectedItem(value);
		}
		if (jComboBox.getSelectedIndex() == -1 && jComboBox.getModel().getSize() > 0)
			jComboBox.setSelectedIndex(0);

	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setBold(boolean)
	 */
	@Override
	public void setBold(boolean bold) {

		if(bold)
		{
			jComboBox.setFont(jComboBox.getFont().deriveFont(Font.BOLD));
		}
		else
		{
			jComboBox.setFont(jComboBox.getFont().deriveFont(Font.PLAIN));
		}
		
	}

}
