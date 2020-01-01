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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.fixagora.basis.client.view.editor.SpreadSheetEditorSheet;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;

import org.apache.poi.ss.usermodel.DateUtil;

/**
 * The Class PlainTextField.
 */
public class PlainTextField extends JPanel implements FieldInterface {

	private static final long serialVersionUID = 1L;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");

	private SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("MM/dd/yyyy");

	private final DecimalFormat doubleFormat = new DecimalFormat("##0.#########");

	private boolean externalUpdate = false;

	private boolean gradientPaint = false;

	private Color backgroundColor;

	/** The date formats. */
	protected List<DateFormat> dateFormats = new ArrayList<DateFormat>();

	private JPanel textFieldPanel;

	private JTextField jTextField;

	private SpreadSheetCellFormat spreadSheetCellFormat = null;
	
	/**
	 * Instantiates a new plain text field.
	 *
	 * @param cellEditor the cell editor
	 * @param backgroundColor the background color
	 * @param spreadSheetEditorSheet the spread sheet editor sheet
	 * @param hyperlink the hyperlink
	 */
	public PlainTextField(final CellEditor cellEditor, Color backgroundColor, final SpreadSheetEditorSheet spreadSheetEditorSheet, final String hyperlink) {

		setLayout(new BorderLayout());
		setRequestFocusEnabled(true);

		this.backgroundColor = backgroundColor;

		dateFormats.add(DateFormat.getTimeInstance(DateFormat.FULL, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(simpleDateFormat);
		dateFormats.add(simpleDateFormat2);
		dateFormats.add(DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH));
		dateFormats.add(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		dateFormats.add(simpleDateFormat3);

		textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new BorderLayout());

		textFieldPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), backgroundColor));

		add(textFieldPanel, BorderLayout.CENTER);

		jTextField = new JTextField() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				final int width = this.getWidth();
				final int height = this.getHeight();
				Color bright = PlainTextField.this.backgroundColor;
				Color dark = bright;
				if (PlainTextField.this.gradientPaint)
					dark = bright.darker();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, height);

				getUI().paint(graphics2D, this);
			}

			public void addNotify() {

				super.addNotify();
				if (cellEditor != null)
					requestFocusInWindow();
			}

		};

		jTextField.setOpaque(false);

		jTextField.setBorder(new EmptyBorder(0, 5, 0, 5));

		jTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				if (spreadSheetEditorSheet != null && !externalUpdate)
					spreadSheetEditorSheet.setCellText(jTextField.getText());
			}
		});
		textFieldPanel.add(jTextField, BorderLayout.CENTER);

		if (hyperlink != null) {
			JLabel jLabel = new JLabel() {

				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(final Graphics graphics) {

					final Graphics2D graphics2D = (Graphics2D) graphics;

					final int width = this.getWidth();
					final int height = this.getHeight();
					Color bright = PlainTextField.this.backgroundColor;
					Color dark = bright;
					if (PlainTextField.this.gradientPaint)
						dark = bright.darker();
					final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 0, bright, width / 2.F, height, dark);

					graphics2D.setPaint(gradientPaint);
					graphics2D.fillRect(0, 0, width, height);

					getUI().paint(graphics2D, this);
				}

				public void addNotify() {

					super.addNotify();
					if (cellEditor != null)
						requestFocusInWindow();
				}

			};
			jLabel.setOpaque(false);
			jLabel.setIcon(new ImageIcon(PlainTextField.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/network.png")));
			textFieldPanel.add(jLabel, BorderLayout.WEST);
			jLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {

					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {

					if(cellEditor!=null)
						cellEditor.cancelCellEditing();
					
					
					try {
						
						if(hyperlink.toUpperCase().startsWith("AGORA://"))
						{
							spreadSheetEditorSheet.getSpreadSheetEditor().getMainPanel().openBusinessObject(hyperlink.substring(8));
						}
						else if (java.awt.Desktop.isDesktopSupported()) {
							Desktop desktop = java.awt.Desktop.getDesktop();
							
							if(hyperlink.toUpperCase().startsWith("MAILTO:"))
							{
								if(desktop.isSupported(java.awt.Desktop.Action.MAIL))
									desktop.mail(new URI(hyperlink.substring(7)));
							}
							else if(hyperlink.toUpperCase().startsWith("FILE://"))
							{
								if(desktop.isSupported(java.awt.Desktop.Action.OPEN))
								{
									final File file = new File(hyperlink.substring(7));
									desktop.open(file);
								}
							}
							else 
							if (desktop.isSupported(java.awt.Desktop.Action.BROWSE))
							{
								if(!hyperlink.toUpperCase().startsWith("HTTP://"))
									desktop.browse(new URI("http://"+hyperlink));
								else
									desktop.browse(new URI(hyperlink));
							}
						}
					}
					catch (Exception e1) {
					}
					

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

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {

		if (value instanceof Double) {
			if (spreadSheetCellFormat != null) {
				if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
						|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE) {

					Date date = DateUtil.getJavaDate((Double) value);
					if (date != null)
						switch (spreadSheetCellFormat.getDateFormat()) {
							case 1:
								jTextField.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
								break;
							case 2:
								jTextField.setText(DateFormat.getDateInstance(DateFormat.LONG).format(date));
								break;
							case 3:
								jTextField.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
								break;
							default:
								jTextField.setText(simpleDateFormat3.format(date));
						}
				}
				else if (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
						|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME) {
					Date date = DateUtil.getJavaDate((Double) value);
					if (date != null)
						switch (spreadSheetCellFormat.getTimeFormat()) {
							case 1:
								jTextField.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
								break;
							case 2:
								jTextField.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date));
								break;
							case 3:
								jTextField.setText(DateFormat.getTimeInstance(DateFormat.LONG).format(date));
								break;
							case 4:
								jTextField.setText(simpleDateFormat.format(date));
								break;
							default:
								jTextField.setText(simpleDateFormat2.format(date));
								break;
						}
				}
				else {
					RoundingMode roundingMode = RoundingMode.HALF_EVEN;
					switch (spreadSheetCellFormat.getPresentationRoundingMode()) {
						case CEILING:
							roundingMode = RoundingMode.CEILING;
							break;
						case DOWN:
							roundingMode = RoundingMode.DOWN;
							break;
						case FLOOR:
							roundingMode = RoundingMode.FLOOR;
							break;
						case HALF_DOWN:
							roundingMode = RoundingMode.HALF_DOWN;
							break;
						case HALF_EVEN:
							roundingMode = RoundingMode.HALF_EVEN;
							break;
						case HALF_UP:
							roundingMode = RoundingMode.HALF_UP;
							break;
						case UP:
							roundingMode = RoundingMode.UP;
							break;
					}
					int decimalPlaces = spreadSheetCellFormat.getDecimalPlaces();

					BigDecimal bigDecimal = new BigDecimal(Double.toString((Double) value)).setScale(decimalPlaces, roundingMode);

					StringBuffer stringBuffer = new StringBuffer();
					if (decimalPlaces > 0) {
						stringBuffer.append(".");
						for (int i = 0; i < decimalPlaces; i++)
							stringBuffer.append("0");
					}
					for (int i = 0; i < spreadSheetCellFormat.getLeadingZeroes(); i++) {
						stringBuffer.insert(0, "0");
						if (spreadSheetCellFormat.getThousandsSeparator() && (i + 1) % 3 == 0)
							stringBuffer.insert(0, ",");
					}
					for (int i = spreadSheetCellFormat.getLeadingZeroes(); i < 7; i++) {
						stringBuffer.insert(0, "#");
						if (spreadSheetCellFormat.getThousandsSeparator() && (i + 1) % 3 == 0)
							stringBuffer.insert(0, ",");
					}
					DecimalFormat decimalFormat = new DecimalFormat(stringBuffer.toString());
					jTextField.setText(decimalFormat.format(bigDecimal));
					if (bigDecimal.doubleValue() < 0 && spreadSheetCellFormat.getNegativeNumbersRed()) {
						jTextField.setForeground(Color.RED);
					}
				}
			}
			else {
				jTextField.setText(doubleFormat.format((Double) value));
			}
			jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		else if (value instanceof Boolean) {
			if (spreadSheetCellFormat != null) {
				if ((Boolean) value) {
					jTextField.setText(spreadSheetCellFormat.getTrueValue());
				}
				else {
					jTextField.setText(spreadSheetCellFormat.getFalseValue());
				}
			}
			jTextField.setText(value.toString().toUpperCase());
		}
		else if (value instanceof String) {
			jTextField.setText(value.toString());
		}
		else
			jTextField.setText("");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#getValue()
	 */
	@Override
	public Object getValue() {

		String text = jTextField.getText().trim();
		try {
			Number number = Double.parseDouble(text.replaceAll(",", ""));
			return number;
		}
		catch (Exception e1) {
			if (text.equals("TRUE"))
				return true;
			if (text.equals("FALSE"))
				return false;
			if (spreadSheetCellFormat != null && text.equals(spreadSheetCellFormat.getTrueValue()))
				return true;
			if (spreadSheetCellFormat != null && text.equals(spreadSheetCellFormat.getFalseValue()))
				return false;
			if (jTextField.getText().trim().length() > 0)
				return jTextField.getText().trim();
			else
				return "";
		}
	}

	/**
	 * Sets the external value.
	 *
	 * @param text the new external value
	 */
	public void setExternalValue(String text) {

		externalUpdate = true;
		jTextField.setText(text);
		externalUpdate = false;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setHighLightColor(java.awt.Color)
	 */
	@Override
	public void setHighLightColor(Color color) {

		textFieldPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), color));

	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color color) {

		backgroundColor = color;

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
	 * @see net.sourceforge.fixagora.basis.client.view.component.FieldInterface#setSpreadSheetCellFormat(net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat)
	 */
	@Override
	public void setSpreadSheetCellFormat(SpreadSheetCellFormat spreadSheetCellFormat) {

		this.spreadSheetCellFormat = spreadSheetCellFormat;
		setBackground(new Color(spreadSheetCellFormat.getBackgroundRed(), spreadSheetCellFormat.getBackgroundGreen(), spreadSheetCellFormat.getBackgroundBlue()));
		gradientPaint = spreadSheetCellFormat.getGradientPaint();
		jTextField.setForeground(new Color(spreadSheetCellFormat.getForegroundRed(), spreadSheetCellFormat.getForegroundGreen(), spreadSheetCellFormat
				.getForegroundBlue()));

	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
	 */
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {

		InputMap map = jTextField.getInputMap(condition);
		ActionMap am = jTextField.getActionMap();

		if (map != null && am != null && isEnabled()) {
			Object binding = map.get(ks);
			Action action = (binding == null) ? null : am.get(binding);
			if (action != null) {
				return SwingUtilities.notifyAction(action, ks, e, jTextField, e.getModifiers());
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
