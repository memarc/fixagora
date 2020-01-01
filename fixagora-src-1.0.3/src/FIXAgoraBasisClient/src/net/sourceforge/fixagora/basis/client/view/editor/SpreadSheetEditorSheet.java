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
package net.sourceforge.fixagora.basis.client.view.editor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.model.dnd.SpreadSheetDropTarget;
import net.sourceforge.fixagora.basis.client.model.editor.CellUpdateHandler;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetFirstColumnTableModel;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetTableModel;
import net.sourceforge.fixagora.basis.client.model.editor.SpreadSheetTableRowFilter;
import net.sourceforge.fixagora.basis.client.view.GradientPanel;
import net.sourceforge.fixagora.basis.client.view.TopPanel;
import net.sourceforge.fixagora.basis.client.view.dialog.ColumnWidthDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.ConditionalFormattingDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.FormatCellsDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.FunctionWizardDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.CopySpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.CutSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.DeleteRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.FillSpreadSheetCellRequest.Direction;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertColumnRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.InsertRowRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyColumnWidthRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifyRowVisibleRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetCellFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.ModifySpreadSheetConditionalFormatRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PasteSpreadSheetCellRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateColumnFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateFullSheetResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRowFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetCellResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateSheetConditionalFormatResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetCellFormat.SpreadSheetFormatType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetColumnFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetConditionalFormat;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetRowFormat;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.util.CellReference;

/**
 * The Class SpreadSheetEditorSheet.
 */
public class SpreadSheetEditorSheet extends JPanel {

	/**
	 * The Enum FormatAction.
	 */
	public enum FormatAction {
		
		/** The clear. */
		CLEAR, 
 /** The format. */
 FORMAT, 
 /** The conditional format. */
 CONDITIONAL_FORMAT, 
 /** The insert row. */
 INSERT_ROW, 
 /** The delete row. */
 DELETE_ROW, 
 /** The hide row. */
 HIDE_ROW, 
 /** The show row. */
 SHOW_ROW, 
 /** The column width. */
 COLUMN_WIDTH, 
 /** The insert column. */
 INSERT_COLUMN, 
 /** The delete column. */
 DELETE_COLUMN, 
 /** The hide column. */
 HIDE_COLUMN, 
 /** The show column. */
 SHOW_COLUMN, 
 /** The fill left. */
 FILL_LEFT, 
 /** The fill right. */
 FILL_RIGHT, 
 /** The fill up. */
 FILL_UP, 
 /** The fill down. */
 FILL_DOWN
	};

	private static final long serialVersionUID = 1L;
	private JTable table = null;
	private SpreadSheetTableModel spreadSheetTableModel;
	private JTextField textField;
	private JTextField textField_1;
	private Color blue = new Color(204, 216, 255);
	
	/** The funtion wizard icon. */
	protected final ImageIcon funtionWizardIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/lc20556.png"));
	
	/** The sum icon. */
	protected final ImageIcon sumIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/lc26048.png"));
	
	/** The function icon. */
	protected final ImageIcon functionIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/lc26049.png"));
	
	/** The ok icon. */
	protected final ImageIcon okIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/agt_action_success.png"));
	
	/** The cancel icon. */
	protected final ImageIcon cancelIcon = new ImageIcon(
			AbstractBusinessObjectEditor.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/22x22/cancel.png"));
	private SpreadSheetEditor spreadSheetEditor;
	private SpreadSheetCellEditor spreadSheetCellEditor;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private boolean externalTextMofication = false;
	private CellUpdateHandler cellUpdateHandler;
	private boolean visible = true;
	private CardLayout cardLayout;
	private JComboBox functionComboBox;
	private JPanel cardPanel;
	
	/** The adjusting. */
	protected boolean adjusting;
	private Map<Integer, SpreadSheetColumnFormat> spreadSheetColumnFormatMap = new HashMap<Integer, SpreadSheetColumnFormat>();
	private KeyStroke stroke;
	private KeyStroke stroke2;
	private KeyStroke stroke3;
	private KeyStroke stroke4;
	private Action listener3;
	private Action listener2;
	private Action listener;
	private ActionListener listener4;
	private JScrollPane scrollPane;
	private ListSelectionListener listSelectionListener;
	private SpreadSheetTableRowFilter spreadSheetTableRowFilter = null;
	private JTable fixedTable;
	private SpreadSheetFirstColumnTableModel spreadSheetFirstColumnTableModel;
	private CardLayout updateCardlayout;
	private JPanel tableCardPanel;
	private BufferedImage bufferedImage;
	private Timer timer;
	private List<UpdateSheetCellResponse> updateSheetCellResponses = new ArrayList<UpdateSheetCellResponse>();
	
	/** The mouse pressed. */
	protected boolean mousePressed;
	private static Logger log = Logger.getLogger(SpreadSheetEditorSheet.class);

	/**
	 * Instantiates a new spread sheet editor sheet.
	 *
	 * @param spreadSheetEditor the spread sheet editor
	 */
	public SpreadSheetEditorSheet(final SpreadSheetEditor spreadSheetEditor) {

		this.spreadSheetEditor = spreadSheetEditor;
		this.cellUpdateHandler = new CellUpdateHandler();
		spreadSheetTableRowFilter = new SpreadSheetTableRowFilter();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		setFocusable(true);

		JPanel panel = new GradientPanel(blue.darker(), blue);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		textField = new JTextField();
		textField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		textField.setMinimumSize(new Dimension(4, 25));
		textField.setPreferredSize(new Dimension(100, 25));

		functionComboBox = new JComboBox(WorkbookEvaluator.getSupportedFunctionNames().toArray());
		functionComboBox.setBackground(new Color(255, 243, 204));

		cardLayout = new CardLayout(0, 0);

		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);

		cardPanel.add(textField, "navigation");
		cardPanel.add(functionComboBox, "function");
		cardLayout.show(cardPanel, "navigation");

		GridBagConstraints gbc_cardPanel = new GridBagConstraints();
		gbc_cardPanel.insets = new Insets(5, 5, 5, 5);
		gbc_cardPanel.anchor = GridBagConstraints.WEST;
		gbc_cardPanel.gridx = 0;
		gbc_cardPanel.gridy = 0;
		panel.add(cardPanel, gbc_cardPanel);

		textField.setColumns(10);

		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(funtionWizardIcon);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		lblNewLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				if (row >= 0 && col >= 0) {
					CellEditor cellEditor = table.getCellEditor();
					if (cellEditor != null)
						cellEditor.cancelCellEditing();
					row = table.convertRowIndexToModel(row);
					col = table.convertColumnIndexToModel(col);
					SpreadSheetCell spreadSheetCell = (SpreadSheetCell) spreadSheetTableModel.getValueAt(row, col);
					if (spreadSheetCell == null) {
						spreadSheetCell = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(), null, null, row, col);
					}
					FunctionWizardDialog functionWizardDialog = new FunctionWizardDialog(spreadSheetCell.getFormula());
					functionWizardDialog.setVisible(true);
					if (!functionWizardDialog.isCancelled()) {
						spreadSheetCell.setFormula(functionWizardDialog.getFormula());
						spreadSheetCell.setValue(null);
						spreadSheetEditor.getBasisClientConnector().sendRequest(
								new ModifySheetCellRequest(spreadSheetCell, RequestIDManager.getInstance().getID()));
					}
				}
			}

		});

		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(sumIcon);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewLabel_1.gridx = 2;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);

		lblNewLabel_1.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {

				if (lblNewLabel_1.getIcon() == cancelIcon && spreadSheetCellEditor != null) {
					spreadSheetCellEditor.cancelCellEditing();
					textField_1.setText("");
				}
				else if (lblNewLabel_1.getIcon() == sumIcon) {
					int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
					int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());
					if (minRow >= 0 && minCol >= 0) {
						int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
						if (maxRow == table.getRowCount())
							return;
						int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());
						for (int i = minCol; i <= maxCol; i++) {
							CellReference cellReference = new CellReference(minRow, i);
							CellReference cellReference2 = new CellReference(maxRow, i);
							StringBuffer stringBuffer = new StringBuffer("SUM(");
							stringBuffer.append(cellReference.formatAsString());
							stringBuffer.append(":");
							stringBuffer.append(cellReference2.formatAsString());
							stringBuffer.append(")");
							if (table.isCellEditable(maxRow + 1, i)) {
								SpreadSheetCell spreadSheetCell = new SpreadSheetCell(spreadSheetTableModel.getSpreadSheet().getId(), null, stringBuffer
										.toString(), maxRow + 1, i);
								spreadSheetEditor.getBasisClientConnector().sendRequest(
										new ModifySheetCellRequest(spreadSheetCell, RequestIDManager.getInstance().getID()));
							}
						}
					}
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				// TODO Auto-generated method stub

			}
		});

		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(functionIcon);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.gridx = 3;
		gbc_lblNewLabel_2.gridy = 0;
		panel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		lblNewLabel_2.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {

				if (lblNewLabel_2.getIcon() == okIcon && spreadSheetCellEditor != null) {
					spreadSheetCellEditor.stopCellEditing();
					textField_1.setText("");
				}
				else if (lblNewLabel_2.getIcon() == functionIcon) {
					int row = table.getSelectedRow();
					int col = table.getSelectedColumn();
					if (row >= 0 && col >= 0) {
						table.editCellAt(row, col);
						textField_1.setText("=");
					}
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {

				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				// TODO Auto-generated method stub

			}
		});

		textField_1 = new JTextField();
		textField_1.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		textField_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		textField_1.setMinimumSize(new Dimension(4, 25));
		textField_1.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(5, 5, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 4;
		gbc_textField_1.gridy = 0;
		panel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		textField_1.getDocument().addDocumentListener(new DocumentListener() {

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

				if (textField_1.getText().trim().startsWith("=")) {
					cardLayout.show(cardPanel, "function");
				}
				else {
					cardLayout.show(cardPanel, "navigation");
				}

				if (spreadSheetCellEditor != null && !externalTextMofication) {
					spreadSheetCellEditor.setText(textField_1.getText());
				}

			}
		});

		textField_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (spreadSheetCellEditor != null) {
					spreadSheetCellEditor.stopCellEditing();
					setCellEditor(null);
				}

			}
		});

		functionComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Object object = functionComboBox.getSelectedItem();
				if (object != null) {
					textField_1.replaceSelection(object.toString() + "()");
				}

			}
		});

		scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				final int width = this.getWidth();
				final int height = this.getHeight();
				final GradientPaint gradientPaint = new GradientPaint(width / 2.F, 1, blue, width / 2.F, 26, blue.darker());

				graphics2D.setPaint(gradientPaint);
				graphics2D.fillRect(0, 0, width, 26);

				graphics2D.setColor(new Color(204, 216, 255));
				graphics2D.setPaintMode();
				graphics2D.fillRect(0, 27, width, height - 26);

				getUI().paint(graphics2D, this);
			}
		};

		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(blue);

		GridBagConstraints gbc_updatePanel = new GridBagConstraints();
		gbc_updatePanel.fill = GridBagConstraints.BOTH;
		gbc_updatePanel.gridx = 0;
		gbc_updatePanel.gridy = 1;

		updateCardlayout = new CardLayout(0, 0);
		tableCardPanel = new JPanel();
		tableCardPanel.setLayout(updateCardlayout);

		add(tableCardPanel, gbc_updatePanel);

		tableCardPanel.add(scrollPane, "table");

		JPanel updatePanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
			 */
			@Override
			protected void paintComponent(final Graphics graphics) {

				final Graphics2D graphics2D = (Graphics2D) graphics;

				super.paintComponent(graphics2D);

				if (bufferedImage != null)
					graphics2D.drawImage(bufferedImage, 0, 0, this);

				getUI().paint(graphics2D, this);
			}
		};

		tableCardPanel.add(updatePanel, "update");

		updateCardlayout.show(tableCardPanel, "update");

		spreadSheetTableModel = new SpreadSheetTableModel(spreadSheetEditor);

		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String[] text = textField.getText().split(":");
				if (text.length == 1) {
					try {
						CellReference cellReference = new CellReference(text[0].trim());
						table.setRowSelectionInterval(table.convertRowIndexToView(cellReference.getRow()), table.convertRowIndexToView(cellReference.getRow()));
						table.setColumnSelectionInterval(table.convertColumnIndexToView(cellReference.getCol()),
								table.convertColumnIndexToView(cellReference.getCol()));
					}
					catch (Exception e1) {
					}
				}
				else if (text.length == 2) {
					try {
						CellReference cellReference = new CellReference(text[0].trim());
						CellReference cellReference2 = new CellReference(text[1].trim());
						table.setRowSelectionInterval(table.convertRowIndexToView(cellReference.getRow()), table.convertRowIndexToView(cellReference2.getRow()));
						table.setColumnSelectionInterval(table.convertColumnIndexToView(cellReference.getCol()),
								table.convertColumnIndexToView(cellReference2.getCol()));
					}
					catch (Exception e1) {
					}
				}

			}
		});

		listener = new Action() {

			@Override
			public Object getValue(String key) {

				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putValue(String key, Object value) {

				// TODO Auto-generated method stub

			}

			@Override
			public void setEnabled(boolean b) {

				// TODO Auto-generated method stub

			}

			@Override
			public boolean isEnabled() {

				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			public void actionPerformed(ActionEvent event) {

				if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
					return;

				int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
				int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

				int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
				int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new CopySpreadSheetCellRequest(spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow + 1, minCol, maxCol - minCol + 1,
								RequestIDManager.getInstance().getID()));

			}
		};

		listener2 = new Action() {

			@Override
			public Object getValue(String key) {

				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putValue(String key, Object value) {

				// TODO Auto-generated method stub

			}

			@Override
			public void setEnabled(boolean b) {

				// TODO Auto-generated method stub

			}

			@Override
			public boolean isEnabled() {

				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			public void actionPerformed(ActionEvent event) {

				int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
				int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

				if (minCol == -1 || minRow == -1)
					return;

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new PasteSpreadSheetCellRequest(spreadSheetEditor.getSpreadSheet().getId(), minRow, minCol, RequestIDManager.getInstance().getID()));
			}
		};

		listener3 = new Action() {

			@Override
			public Object getValue(String key) {

				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putValue(String key, Object value) {

				// TODO Auto-generated method stub

			}

			@Override
			public void setEnabled(boolean b) {

				// TODO Auto-generated method stub

			}

			@Override
			public boolean isEnabled() {

				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {

				// TODO Auto-generated method stub

			}

			public void actionPerformed(ActionEvent event) {

				if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
					return;

				int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
				int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

				int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
				int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new CutSpreadSheetCellRequest(spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow + 1, minCol, maxCol - minCol + 1,
								RequestIDManager.getInstance().getID()));

			}
		};

		listener4 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				spreadSheetEditor.getMainPanel().switchShowingFormula();
				table.repaint();

			}
		};

		stroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

		stroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);

		stroke3 = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);

		stroke4 = KeyStroke.getKeyStroke(KeyEvent.VK_NUMBER_SIGN, ActionEvent.CTRL_MASK, false);

		registerKeyboardAction(listener4, "Formula", stroke4, JComponent.WHEN_IN_FOCUSED_WINDOW);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {

				if (e.getValue() > 0
						&& e.getValue() == scrollPane.getVerticalScrollBar().getModel().getMaximum() - scrollPane.getVerticalScrollBar().getModel().getExtent()) {
					if (adjusting)
						return;
					adjusting = true;
					Point point = scrollPane.getViewport().getViewPosition();
					spreadSheetTableModel.increaseRowCount();
					spreadSheetFirstColumnTableModel.increaseRowCount();

					JViewport viewport = new JViewport();
					viewport.setView(fixedTable);
					viewport.setPreferredSize(fixedTable.getPreferredSize());
					scrollPane.setRowHeaderView(viewport);
					scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());

					scrollPane.getViewport().setViewPosition(point);

				}
				else
					adjusting = false;
			}

		});

		scrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {

				if (e.getValue() > 0
						&& e.getValue() == scrollPane.getHorizontalScrollBar().getModel().getMaximum()
								- scrollPane.getHorizontalScrollBar().getModel().getExtent()) {
					if (adjusting)
						return;
					adjusting = true;
					final Point point = scrollPane.getViewport().getViewPosition();
					spreadSheetTableModel.increaseColumnCount();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							initTable();
							scrollPane.getViewport().setViewPosition(point);
						}
					});

				}
				else
					adjusting = false;
			}
		});

		listSelectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				spreadSheetEditor.getMainPanel().onMainPanelChanged();

				int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();
				int minRow = table.getSelectionModel().getMinSelectionIndex();

				lblNewLabel.setEnabled(false);
				lblNewLabel_1.setEnabled(false);
				lblNewLabel_2.setEnabled(false);

				if (minCol == -1 || minRow == -1) {
					textField.setText("");
					return;
				}

				minCol = table.convertColumnIndexToModel(minCol);
				minRow = table.convertRowIndexToModel(minRow);

				int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

				int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());

				if (spreadSheetEditor.getBasisClientConnector().getFUser().canExecute(spreadSheetEditor.getSpreadSheet())
						|| spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {

					SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(minRow, minCol);
					if (spreadSheetCellFormat == null
							|| (spreadSheetCellFormat.getLocked() == false && (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
									|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat
									.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME))) {
						lblNewLabel.setEnabled(true);
						lblNewLabel_2.setEnabled(true);
					}
					lblNewLabel_1.setEnabled(true);
					for (int i = minCol; i <= maxCol; i++) {
						SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetTableModel.getSpreadSheetCellFormatAt(maxRow + 1, i);
						if (!(spreadSheetCellFormat2 == null || (spreadSheetCellFormat2.getLocked() == false && (spreadSheetCellFormat2
								.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN
								|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_DATE
								|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.PLAIN_TIME
								|| spreadSheetCellFormat2.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_DATE || spreadSheetCellFormat2
								.getSpreadSheetFormatType() == SpreadSheetFormatType.TMP_TIME)))) {
							lblNewLabel_1.setEnabled(false);
						}
					}
				}

				StringBuffer stringBuffer = new StringBuffer(CellReference.convertNumToColString(minCol));
				stringBuffer.append(minRow + 1);

				if (table.getSelectedColumnCount() > 1 || table.getSelectedRowCount() > 1) {
					stringBuffer.append(":");
					stringBuffer.append(CellReference.convertNumToColString(maxCol));
					stringBuffer.append(maxRow + 1);
				}
				textField.setText(stringBuffer.toString());

				table.getTableHeader().resizeAndRepaint();

				fixedTable.repaint();
			}
		};

		spreadSheetFirstColumnTableModel = new SpreadSheetFirstColumnTableModel();
		fixedTable = new JTable(spreadSheetFirstColumnTableModel);
		TableRowSorter<SpreadSheetFirstColumnTableModel> tableRowSorter = new TableRowSorter<SpreadSheetFirstColumnTableModel>(spreadSheetFirstColumnTableModel);
		tableRowSorter.setRowFilter(spreadSheetTableRowFilter);
		fixedTable.setRowSorter(tableRowSorter);
		fixedTable.setModel(spreadSheetFirstColumnTableModel);
		fixedTable.setShowHorizontalLines(true);
		fixedTable.setShowVerticalLines(true);
		fixedTable.setGridColor(blue.darker());
		fixedTable.setIntercellSpacing(new Dimension(0, 0));
		fixedTable.setAutoscrolls(false);
		fixedTable.setRowHeight(25);
		fixedTable.setRowSelectionAllowed(true);
		fixedTable.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
		fixedTable.setColumnSelectionAllowed(false);
		fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		fixedTable.getColumnModel().getColumn(0).setPreferredWidth(45);
		fixedTable.getColumnModel().getColumn(0).setHeaderRenderer(new SpreadSheetHeaderRenderer());
		tableRowSorter.setSortable(0, false);

		fixedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (fixedTable.getSelectionModel().getMinSelectionIndex() == -1 || fixedTable.getSelectionModel().getMaxSelectionIndex() == -1)
					return;

				table.setRowSelectionInterval(fixedTable.getSelectionModel().getMinSelectionIndex(), fixedTable.getSelectionModel().getMaxSelectionIndex());
				table.setColumnSelectionInterval(0, table.convertColumnIndexToView(table.getModel().getColumnCount() - 1));
			}
		});

		fixedTable.getTableHeader().setReorderingAllowed(false);

		fixedTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1 && spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {

					JPopupMenu tablePopup = new JPopupMenu();
					JMenuItem clearFormatMenuItem = new JMenuItem("Clear Direct Formatting");
					clearFormatMenuItem.setEnabled(true);
					clearFormatMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(clearFormatMenuItem);
					JMenuItem formatCellsMenuItem = new JMenuItem("Format Cells...");
					formatCellsMenuItem.setEnabled(true);
					formatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(formatCellsMenuItem);

					JMenuItem conditionalFormatCellsMenuItem = new JMenuItem("Conditional Formatting...");
					conditionalFormatCellsMenuItem.setEnabled(true);
					conditionalFormatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(conditionalFormatCellsMenuItem);

					int row = fixedTable.rowAtPoint(e.getPoint());
					if (!(fixedTable.getSelectionModel().isSelectedIndex(row) && table.getColumnModel().getSelectionModel().getMaxSelectionIndex() == table
							.convertColumnIndexToView(table.getModel().getColumnCount() - 1))) {
						fixedTable.clearSelection();
						fixedTable.setRowSelectionInterval(row, row);
					}

					tablePopup.add(new JSeparator());

					JMenuItem insertMenuItem = new JMenuItem("Insert Rows");
					insertMenuItem.setEnabled(true);
					insertMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					insertMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/insert-table-row.png")));
					tablePopup.add(insertMenuItem);

					JMenuItem deleteMenuItem = new JMenuItem("Delete Rows");
					deleteMenuItem.setEnabled(true);
					deleteMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					deleteMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-delete.png")));
					tablePopup.add(deleteMenuItem);

					tablePopup.add(new JSeparator());

					JMenuItem hideMenuItem = new JMenuItem("Hide");
					hideMenuItem.setEnabled(true);
					hideMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					hideMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-hide.png")));
					tablePopup.add(hideMenuItem);

					JMenuItem showMenuItem = new JMenuItem("Show");
					showMenuItem.setEnabled(true);
					showMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					showMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-row-show.png")));
					tablePopup.add(showMenuItem);

					insertMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							insertRow();

						}

					});

					deleteMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							deleteRow();

						}

					});

					hideMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							hideRow();

						}

					});

					showMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showRow();

						}

					});

					tablePopup.add(new JSeparator());

					JMenuItem cutMenuItem = new JMenuItem("Cut");
					cutMenuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
					cutMenuItem.addActionListener(listener3);
					cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
					cutMenuItem.setMnemonic(KeyEvent.VK_X);
					cutMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-cut-6.png")));
					tablePopup.add(cutMenuItem);

					JMenuItem copyMenuItem = new JMenuItem("Copy");
					copyMenuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
					copyMenuItem.addActionListener(listener);
					copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
					copyMenuItem.setMnemonic(KeyEvent.VK_C);
					copyMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-copy-6.png")));
					tablePopup.add(copyMenuItem);

					JMenuItem pasteMenuItem = new JMenuItem("Paste");
					pasteMenuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
					pasteMenuItem.addActionListener(listener2);
					pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
					pasteMenuItem.setMnemonic(KeyEvent.VK_V);
					pasteMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-paste-6.png")));
					tablePopup.add(pasteMenuItem);

					clearFormatMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							clearFormat();

						}

					});

					formatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showFormatDialog();

						}

					});

					conditionalFormatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showConditionalFormattingDialog();

						}

					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}

			}

		});

		fixedTable.registerKeyboardAction(listener2, "Paste", stroke2, JComponent.WHEN_FOCUSED);

		fixedTable.registerKeyboardAction(listener, "Copy", stroke, JComponent.WHEN_FOCUSED);

		fixedTable.registerKeyboardAction(listener3, "Cut", stroke3, JComponent.WHEN_FOCUSED);

		ActionMap map = fixedTable.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME), listener3);
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME), listener);
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), listener2);

		initTable();

		updateContent();

		timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean paint = false;

				if (!mousePressed) {
					synchronized (updateSheetCellResponses) {
						for (UpdateSheetCellResponse updateSheetCellResponse : updateSheetCellResponses) {
							paint = true;
							cellUpdateHandler.addSpreadSheetCells(updateSheetCellResponse.getSpreadSheetCells());
							spreadSheetTableModel.onUpdateSheetCellResponse(updateSheetCellResponse);
						}
						updateSheetCellResponses.clear();
					}

					if (paint) {

						if (spreadSheetFirstColumnTableModel.getRowCount() != spreadSheetTableModel.getRowCount()) {
							spreadSheetFirstColumnTableModel.setRowCount(spreadSheetTableModel.getRowCount());
							spreadSheetFirstColumnTableModel.fireTableDataChanged();

							fixedTable.setDefaultRenderer(Object.class, new SpreadSheetFirstColumnTableRenderer(table));

							JViewport viewport = new JViewport();
							viewport.setView(fixedTable);
							viewport.setPreferredSize(fixedTable.getPreferredSize());
							scrollPane.setRowHeaderView(viewport);
							scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());
							spreadSheetFirstColumnTableModel.fireTableDataChanged();
							fixedTable.repaint();
						}

						table.repaint();
					}

					if (visible) {
						if (!paint && table.isShowing() && cellUpdateHandler.isRepaintRequired()) {
							table.paintImmediately(table.getVisibleRect());
						}
					}
					else
						timer.stop();

				}

			}
		});

		// timer.setCoalesce(false);
		timer.start();

	}

	/**
	 * Update content.
	 */
	public void updateContent() {

		if (spreadSheetEditor.getSpreadSheet().getId() > 0
				&& spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {

			textField.setBackground(new Color(255, 243, 204));
			textField.setEditable(true);

			textField_1.setBackground(new Color(255, 243, 204));
			textField_1.setEditable(true);

			table.setEnabled(true);
		}
		else {
			textField.setBackground(new Color(204, 216, 255));
			textField.setEditable(false);

			textField_1.setBackground(new Color(204, 216, 255));
			textField_1.setEditable(false);

			table.setEnabled(false);
		}
	}

	/**
	 * On update sheet cell response.
	 *
	 * @param updateSheetCellResponse the update sheet cell response
	 */
	public void onUpdateSheetCellResponse(UpdateSheetCellResponse updateSheetCellResponse) {

		synchronized (updateSheetCellResponses) {
			updateSheetCellResponses.add(updateSheetCellResponse);
		}

	}

	/**
	 * Sets the cell editor.
	 *
	 * @param spreadSheetCellEditor the new cell editor
	 */
	public void setCellEditor(SpreadSheetCellEditor spreadSheetCellEditor) {

		this.spreadSheetCellEditor = spreadSheetCellEditor;
		if (spreadSheetCellEditor != null) {
			SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetCellEditor.getSpreadSheetCellFormat();
			if (spreadSheetCellFormat != null
					&& (spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.NUMBER_SPINNER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DATE_CHOOSER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.DAY_SPINNER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MONTH_SPINNER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.HOUR_SPINNER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.MINUTE_SPINNER
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.CHECKBOX
							|| spreadSheetCellFormat.getSpreadSheetFormatType() == SpreadSheetFormatType.BUTTON || spreadSheetCellFormat
							.getSpreadSheetFormatType() == SpreadSheetFormatType.PREDEFINED)) {
				textField_1.setBackground(new Color(204, 216, 255));
				textField_1.setEditable(false);
			}
			else if (spreadSheetEditor.getSpreadSheet().getId() > 0
					&& spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getAbstractBusinessObject())) {

				textField_1.setBackground(new Color(255, 243, 204));
				textField_1.setEditable(true);
			}

			lblNewLabel_1.setIcon(cancelIcon);
			lblNewLabel_2.setIcon(okIcon);
		}
		else {
			lblNewLabel_1.setIcon(sumIcon);
			lblNewLabel_2.setIcon(functionIcon);
			try {
				textField_1.getDocument().remove(0, textField_1.getDocument().getLength());
			}
			catch (BadLocationException e) {
				log.error("Bug", e);

			}
			textField_1.setBackground(new Color(204, 216, 255));
			textField_1.setEditable(false);
		}

	}

	/**
	 * Sets the cell text.
	 *
	 * @param text the new cell text
	 */
	public void setCellText(String text) {

		externalTextMofication = true;
		textField_1.setText(text);
		externalTextMofication = false;

	}

	/**
	 * Close abstract business object editor.
	 */
	public void closeAbstractBusinessObjectEditor() {

		visible = false;
	}

	private void showColumnWidthDialog() {

		final ColumnWidthDialog columnWidthDialog = new ColumnWidthDialog(table.getColumnModel().getColumn(table.getSelectedColumn()).getWidth());
		columnWidthDialog.setVisible(true);
		if (!columnWidthDialog.isCancelled()) {

			final WaitDialog waitDialog = new WaitDialog(this, "Modify column width ...");

			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					int columnWidth = columnWidthDialog.getColumnWidth();

					int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();

					int maxCol = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

					if (minCol == -1 || maxCol == -1)
						return null;

					minCol = table.convertColumnIndexToModel(minCol);

					maxCol = table.convertColumnIndexToModel(maxCol);

					spreadSheetEditor.getBasisClientConnector().sendRequest(
							new ModifyColumnWidthRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minCol, maxCol
									- minCol + 1, columnWidth));

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

	}

	private void hideColumn() {

		final WaitDialog waitDialog = new WaitDialog(this, "Hide columns ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();

				int maxCol = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

				if (minCol == -1 || maxCol == -1)
					return null;

				minCol = table.convertColumnIndexToModel(minCol);

				maxCol = table.convertColumnIndexToModel(maxCol);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new ModifyColumnVisibleRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minCol, maxCol
								- minCol + 1, false));
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

	private void showColumn() {

		final WaitDialog waitDialog = new WaitDialog(this, "Show columns ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();

				int maxCol = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

				if (minCol == -1 || maxCol == -1)
					return null;

				if (minCol > 0)
					minCol = table.convertColumnIndexToModel(minCol);

				maxCol = table.convertColumnIndexToModel(maxCol);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new ModifyColumnVisibleRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minCol, maxCol
								- minCol + 1, true));

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

	private void insertColumn() {

		final WaitDialog waitDialog = new WaitDialog(this, "Insert columns ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();

				int maxCol = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

				if (minCol == -1 || maxCol == -1)
					return null;

				minCol = table.convertColumnIndexToModel(minCol);

				maxCol = table.convertColumnIndexToModel(maxCol);

				spreadSheetEditor.getBasisClientConnector()
						.sendRequest(
								new InsertColumnRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minCol, maxCol
										- minCol + 1));

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

	private void deleteColumn() {

		final WaitDialog waitDialog = new WaitDialog(this, "Delete columns ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minCol = table.getColumnModel().getSelectionModel().getMinSelectionIndex();

				int maxCol = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

				if (minCol == -1 || maxCol == -1)
					return null;

				minCol = table.convertColumnIndexToModel(minCol);

				maxCol = table.convertColumnIndexToModel(maxCol);

				spreadSheetEditor.getBasisClientConnector()
						.sendRequest(
								new DeleteColumnRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minCol, maxCol
										- minCol + 1));

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

	private void insertRow() {

		final WaitDialog waitDialog = new WaitDialog(this, "Insert rows ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minRow = table.getSelectionModel().getMinSelectionIndex();

				int maxRow = table.getSelectionModel().getMaxSelectionIndex();

				if (minRow == -1 || maxRow == -1)
					return null;

				if (minRow > 0)
					minRow = table.convertRowIndexToModel(minRow);

				maxRow = table.convertRowIndexToModel(maxRow);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new InsertRowRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow + 1));

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

	private void deleteRow() {

		final WaitDialog waitDialog = new WaitDialog(this, "Delete rows ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minRow = table.getSelectionModel().getMinSelectionIndex();

				int maxRow = table.getSelectionModel().getMaxSelectionIndex();

				if (minRow == -1 || maxRow == -1)
					return null;

				if (minRow > 0)
					minRow = table.convertRowIndexToModel(minRow);

				maxRow = table.convertRowIndexToModel(maxRow);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new DeleteRowRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow + 1));

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

	private void hideRow() {

		final WaitDialog waitDialog = new WaitDialog(this, "Hide rows ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minRow = table.getSelectionModel().getMinSelectionIndex();

				int maxRow = table.getSelectionModel().getMaxSelectionIndex();

				if (minRow == -1 || maxRow == -1)
					return null;

				minRow = table.convertRowIndexToModel(minRow);

				maxRow = table.convertRowIndexToModel(maxRow);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new ModifyRowVisibleRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow
								+ 1, false));
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

	private void showRow() {

		final WaitDialog waitDialog = new WaitDialog(this, "Show rows ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				int minRow = table.getSelectionModel().getMinSelectionIndex();

				int maxRow = table.getSelectionModel().getMaxSelectionIndex();

				if (minRow == -1 || maxRow == -1)
					return null;

				if (minRow > 0)
					minRow = table.convertRowIndexToModel(minRow);

				maxRow = table.convertRowIndexToModel(maxRow);

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new ModifyRowVisibleRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow
								+ 1, true));

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
	 * Show format dialog.
	 */
	public void showFormatDialog() {

		if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
			return;

		final int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
		final int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

		final int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
		final int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

		SpreadSheetCellFormat spreadSheetCellFormat = spreadSheetTableModel.getSpreadSheetCellFormatAt(minRow, minCol);
		if (spreadSheetCellFormat == null)
			spreadSheetCellFormat = new SpreadSheetCellFormat();
		else
			for (int i = minRow; i <= maxRow; i++)
				for (int j = minCol; j <= maxCol; j++) {
					SpreadSheetCellFormat spreadSheetCellFormat2 = spreadSheetTableModel.getSpreadSheetCellFormatAt(i, j);
					if (spreadSheetCellFormat2 == null || !spreadSheetCellFormat.isSameFormat(spreadSheetCellFormat2)) {
						spreadSheetCellFormat = new SpreadSheetCellFormat();
						i = maxRow + 1;
						j = maxCol + 1;
					}
				}

		final FormatCellsDialog conditionalFormattingDialog = new FormatCellsDialog(spreadSheetCellFormat);
		conditionalFormattingDialog.setVisible(true);
		if (!conditionalFormattingDialog.isCancelled()) {

			final WaitDialog waitDialog = new WaitDialog(this, "Update cell format ...");

			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					SpreadSheetCellFormat spreadSheetCellFormat3 = conditionalFormattingDialog.getSpreadSheetCellFormat();

					spreadSheetCellFormat3.setSpreadSheet(spreadSheetEditor.getSpreadSheet().getId());

					spreadSheetEditor.getBasisClientConnector().sendRequest(
							new ModifySpreadSheetCellFormatRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow,
									maxRow - minRow + 1, minCol, maxCol - minCol + 1, spreadSheetCellFormat3));

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
	}

	/**
	 * Show conditional formatting dialog.
	 */
	public void showConditionalFormattingDialog() {

		if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
			return;

		final int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
		final int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

		final int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
		final int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

		SpreadSheetConditionalFormat spreadSheetConditionalFormat = spreadSheetTableModel.getSpreadSheetConditionalFormatAt(minRow, minCol);
		if (spreadSheetConditionalFormat == null)
			spreadSheetConditionalFormat = new SpreadSheetConditionalFormat();
		else
			for (int i = minRow; i <= maxRow; i++)
				for (int j = minCol; j <= maxCol; j++) {
					SpreadSheetConditionalFormat spreadSheetConditionalFormat2 = spreadSheetTableModel.getSpreadSheetConditionalFormatAt(i, j);
					if (spreadSheetConditionalFormat2 == null || !spreadSheetConditionalFormat.isSameFormat(spreadSheetConditionalFormat2)) {
						spreadSheetConditionalFormat = new SpreadSheetConditionalFormat();
						i = maxRow + 1;
						j = maxCol + 1;
					}
				}

		final ConditionalFormattingDialog conditionalFormattingDialog = new ConditionalFormattingDialog(spreadSheetConditionalFormat);
		conditionalFormattingDialog.setVisible(true);
		if (!conditionalFormattingDialog.isCancelled()) {

			final WaitDialog waitDialog = new WaitDialog(this, "Update conditional format ...");

			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					SpreadSheetConditionalFormat spreadSheetConditionalFormat3 = conditionalFormattingDialog.getSpreadSheetConditionalFormat();

					spreadSheetConditionalFormat3.setSpreadSheet(spreadSheetEditor.getSpreadSheet().getId());

					spreadSheetEditor.getBasisClientConnector().sendRequest(
							new ModifySpreadSheetConditionalFormatRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(),
									minRow, maxRow - minRow + 1, minCol, maxCol - minCol + 1, spreadSheetConditionalFormat3));

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
	}

	/**
	 * Clear format.
	 */
	public void clearFormat() {

		final WaitDialog waitDialog = new WaitDialog(this, "Clear direct formatting ...");

		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				SpreadSheetCellFormat spreadSheetCellFormat = new SpreadSheetCellFormat();

				spreadSheetCellFormat.setSpreadSheetFormatType(SpreadSheetFormatType.CLEAR);
				spreadSheetCellFormat.setSpreadSheet(spreadSheetEditor.getSpreadSheet().getId());

				if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
					return null;

				final int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
				final int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

				final int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
				final int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

				spreadSheetEditor.getBasisClientConnector().sendRequest(
						new ModifySpreadSheetCellFormatRequest(RequestIDManager.getInstance().getID(), spreadSheetEditor.getSpreadSheet().getId(), minRow,
								maxRow - minRow + 1, minCol, maxCol - minCol + 1, spreadSheetCellFormat));

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
	 * On update sheet cell format response.
	 *
	 * @param updateSheetCellFormatResponse the update sheet cell format response
	 */
	public void onUpdateSheetCellFormatResponse(UpdateSheetCellFormatResponse updateSheetCellFormatResponse) {

		spreadSheetTableModel.onUpdateSheetCellFormatResponse(updateSheetCellFormatResponse);
		table.repaint();
	}

	/**
	 * On update column format response.
	 *
	 * @param updateColumnFormatResponse the update column format response
	 */
	public void onUpdateColumnFormatResponse(UpdateColumnFormatResponse updateColumnFormatResponse) {

		for (SpreadSheetColumnFormat spreadSheetColumnFormat : updateColumnFormatResponse.getSpreadSheetColumnFormats()) {
			if (spreadSheetColumnFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId()) {
				spreadSheetColumnFormatMap.put(spreadSheetColumnFormat.getColumnNumber(), spreadSheetColumnFormat);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				initTable();

			}
		});

	}

	/**
	 * On update row format response.
	 *
	 * @param updateRowFormatResponse the update row format response
	 */
	public void onUpdateRowFormatResponse(UpdateRowFormatResponse updateRowFormatResponse) {

		for (SpreadSheetRowFormat spreadSheetRowFormat : updateRowFormatResponse.getSpreadSheetRowFormats()) {
			if (spreadSheetRowFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId())
				spreadSheetTableRowFilter.put(spreadSheetRowFormat);
		}
		spreadSheetTableModel.fireTableDataChanged();
		spreadSheetFirstColumnTableModel.fireTableDataChanged();
	}

	private synchronized void initTable() {

		mousePressed = false;

		Dimension size = scrollPane.getSize();
		if (size.getWidth() > 0) {
			bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			Graphics bufferedGraphics = bufferedImage.createGraphics();
			scrollPane.paint(bufferedGraphics);
		}

		updateCardlayout.show(tableCardPanel, "update");

		if (table != null)
			scrollPane.getViewport().remove(table);

		table = new JTable();

		table.setTableHeader(new JTableHeader(table.getColumnModel()));

		TableRowSorter<SpreadSheetTableModel> tableRowSorter = new TableRowSorter<SpreadSheetTableModel>(spreadSheetTableModel);
		tableRowSorter.setRowFilter(spreadSheetTableRowFilter);
		table.setRowSorter(tableRowSorter);
		table.setRequestFocusEnabled(true);
		table.setFocusable(true);
		table.setModel(spreadSheetTableModel);
		table.setDefaultRenderer(Object.class, new SpreadSheetTableRenderer(cellUpdateHandler));
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setGridColor(blue.darker());
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(25);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		new SpreadSheetDropTarget(table, spreadSheetEditor);
		table.requestFocus();

		List<TableColumn> hiddenColumns = new ArrayList<TableColumn>();
		for (int i = 0; i < spreadSheetTableModel.getColumnCount(); i++) {
			tableRowSorter.setSortable(i, false);
			SpreadSheetColumnFormat spreadSheetColumnFormat = spreadSheetColumnFormatMap.get(i);
			if (spreadSheetColumnFormat != null) {
				TableColumn tableColumn2 = table.getColumnModel().getColumn(i);
				tableColumn2.setPreferredWidth(spreadSheetColumnFormat.getColumnWidth());
				tableColumn2.setHeaderRenderer(new SpreadSheetHeaderRenderer());
				tableColumn2.setCellEditor(new SpreadSheetCellEditor(spreadSheetTableModel, this, spreadSheetEditor.getBasisClientConnector()));
				if (spreadSheetColumnFormat.getHidden())
					hiddenColumns.add(tableColumn2);
			}
			else {
				TableColumn tableColumn2 = table.getColumnModel().getColumn(i);
				tableColumn2.setPreferredWidth(100);
				tableColumn2.setHeaderRenderer(new SpreadSheetHeaderRenderer());
				tableColumn2.setCellEditor(new SpreadSheetCellEditor(spreadSheetTableModel, this, spreadSheetEditor.getBasisClientConnector()));
			}
		}
		for (TableColumn tableColumn2 : hiddenColumns)
			table.removeColumn(tableColumn2);

		table.registerKeyboardAction(listener2, "Paste", stroke2, JComponent.WHEN_FOCUSED);

		table.registerKeyboardAction(listener, "Copy", stroke, JComponent.WHEN_FOCUSED);

		table.registerKeyboardAction(listener3, "Cut", stroke3, JComponent.WHEN_FOCUSED);

		ActionMap map = table.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME), listener3);
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME), listener);
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), listener2);

		table.getSelectionModel().addListSelectionListener(listSelectionListener);
		table.getColumnModel().getSelectionModel().addListSelectionListener(listSelectionListener);
		lblNewLabel.setEnabled(false);
		lblNewLabel_1.setEnabled(false);
		lblNewLabel_2.setEnabled(false);

		table.getTableHeader().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				if (table.getTableHeader().getCursor().getType() == Cursor.E_RESIZE_CURSOR)
					e.consume();
				else {
					int col = table.getTableHeader().columnAtPoint(e.getPoint());

					if (e.isShiftDown()) {
						int[] selectedColumns = table.getSelectedColumns();
						int min = col;
						int max = col;
						for (int i : selectedColumns) {
							if (i < min)
								min = i;
							if (i > max)
								max = i;
						}
						table.setColumnSelectionInterval(min, max);
					}
					else
						table.setColumnSelectionInterval(col, col);
					table.setRowSelectionInterval(0, table.convertRowIndexToView(table.getModel().getRowCount() - 1));
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1 && spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {

					int col = table.getTableHeader().columnAtPoint(e.getPoint());

					if (!(table.getColumnModel().getSelectionModel().isSelectedIndex(col) && table.convertRowIndexToModel(table.getSelectionModel()
							.getMaxSelectionIndex()) == spreadSheetTableModel.getRowCount() - 1))
						mouseClicked(e);

					JPopupMenu tablePopup = new JPopupMenu();

					JMenuItem clearFormatMenuItem = new JMenuItem("Clear Direct Formatting");
					clearFormatMenuItem.setEnabled(true);
					clearFormatMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(clearFormatMenuItem);

					JMenuItem formatCellsMenuItem = new JMenuItem("Format Cells...");
					formatCellsMenuItem.setEnabled(true);
					formatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(formatCellsMenuItem);

					JMenuItem conditionalFormatCellsMenuItem = new JMenuItem("Conditional Formatting...");
					conditionalFormatCellsMenuItem.setEnabled(true);
					conditionalFormatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(conditionalFormatCellsMenuItem);

					JMenuItem columnWidthMenuItem = new JMenuItem("Column Width ...");
					columnWidthMenuItem.setEnabled(true);
					columnWidthMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					columnWidthMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/format-column-width.png")));
					tablePopup.add(columnWidthMenuItem);

					tablePopup.add(new JSeparator());

					JMenuItem insertMenuItem = new JMenuItem("Insert Columns");
					insertMenuItem.setEnabled(true);
					insertMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					insertMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/insert-table-column.png")));
					tablePopup.add(insertMenuItem);

					JMenuItem deleteMenuItem = new JMenuItem("Delete Columns");
					deleteMenuItem.setEnabled(true);
					deleteMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					deleteMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-delete.png")));
					tablePopup.add(deleteMenuItem);

					tablePopup.add(new JSeparator());

					JMenuItem hideMenuItem = new JMenuItem("Hide");
					hideMenuItem.setEnabled(true);
					hideMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					hideMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-hide.png")));
					tablePopup.add(hideMenuItem);

					JMenuItem showMenuItem = new JMenuItem("Show");
					showMenuItem.setEnabled(true);
					showMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					showMenuItem.setIcon(new ImageIcon(TopPanel.class
							.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/table-column-show.png")));
					tablePopup.add(showMenuItem);

					tablePopup.add(new JSeparator());

					JMenuItem cutMenuItem = new JMenuItem("Cut");
					cutMenuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
					cutMenuItem.addActionListener(listener3);
					cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
					cutMenuItem.setMnemonic(KeyEvent.VK_T);
					cutMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-cut-6.png")));
					tablePopup.add(cutMenuItem);

					JMenuItem copyMenuItem = new JMenuItem("Copy");
					copyMenuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
					copyMenuItem.addActionListener(listener);
					copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
					copyMenuItem.setMnemonic(KeyEvent.VK_C);
					copyMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-copy-6.png")));
					tablePopup.add(copyMenuItem);

					JMenuItem pasteMenuItem = new JMenuItem("Paste");
					pasteMenuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
					pasteMenuItem.addActionListener(listener2);
					pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
					pasteMenuItem.setMnemonic(KeyEvent.VK_P);
					pasteMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-paste-6.png")));
					tablePopup.add(pasteMenuItem);

					deleteMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							deleteColumn();

						}

					});

					insertMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							insertColumn();

						}

					});

					hideMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							hideColumn();

						}

					});

					showMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showColumn();

						}

					});

					clearFormatMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							clearFormat();

						}

					});

					formatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showFormatDialog();

						}

					});

					conditionalFormatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showConditionalFormattingDialog();

						}

					});

					columnWidthMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showColumnWidthDialog();

						}

					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}

			}

		});

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				mousePressed = true;

				int row = table.rowAtPoint(e.getPoint());
				int column = table.columnAtPoint(e.getPoint());
				if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && column >= 0) {
					row = table.convertRowIndexToModel(row);
					column = table.convertColumnIndexToModel(column);
					if (!spreadSheetTableModel.isCellEditable(row, column)) {
						SpreadSheetCell spreadSheetCell = (SpreadSheetCell) spreadSheetTableModel.getValueAt(row, column);
						if (spreadSheetCell != null) {
							String hyperlink = getHyperlink(spreadSheetCell);
							if (hyperlink != null) {

								mousePressed = false;

								if (hyperlink.toUpperCase().startsWith("AGORA://")) {
									spreadSheetEditor.getMainPanel().openBusinessObject(hyperlink.substring(8));
								}
								else if (java.awt.Desktop.isDesktopSupported()) {
									Desktop desktop = java.awt.Desktop.getDesktop();

									try {
										if (hyperlink.toUpperCase().startsWith("MAILTO:")) {
											if (desktop.isSupported(java.awt.Desktop.Action.MAIL))
												desktop.mail(new URI(hyperlink.substring(7)));
										}
										else if (hyperlink.toUpperCase().startsWith("FILE://")) {
											if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
												final File file = new File(hyperlink.substring(7));
												desktop.open(file);
											}
										}
										else if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
											if (!hyperlink.toUpperCase().startsWith("HTTP://"))
												desktop.browse(new URI("http://" + hyperlink));
											else
												desktop.browse(new URI(hyperlink));
										}
									}
									catch (Exception e1) {
									}

								}

							}
						}
					}
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {

				mousePressed = false;

				if (e.getButton() != MouseEvent.BUTTON1 && spreadSheetEditor.getBasisClientConnector().getFUser().canWrite(spreadSheetEditor.getSpreadSheet())) {

					int row = table.rowAtPoint(e.getPoint());
					int column = table.columnAtPoint(e.getPoint());
					if (!(table.getSelectionModel().isSelectedIndex(row) && table.getColumnModel().getSelectionModel().isSelectedIndex(column))) {
						table.getSelectionModel().setSelectionInterval(row, row);
						table.getColumnModel().getSelectionModel().setSelectionInterval(column, column);
					}

					JPopupMenu tablePopup = new JPopupMenu();
					JMenuItem clearFormatMenuItem = new JMenuItem("Clear Direct Formatting");
					clearFormatMenuItem.setEnabled(true);
					clearFormatMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(clearFormatMenuItem);
					JMenuItem formatCellsMenuItem = new JMenuItem("Format Cells...");
					formatCellsMenuItem.setEnabled(true);
					formatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(formatCellsMenuItem);

					JMenuItem conditionalFormatCellsMenuItem = new JMenuItem("Conditional Formatting...");
					conditionalFormatCellsMenuItem.setEnabled(true);
					conditionalFormatCellsMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
					tablePopup.add(conditionalFormatCellsMenuItem);

					tablePopup.add(new JSeparator());

					JMenuItem cutMenuItem = new JMenuItem("Cut");
					cutMenuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
					cutMenuItem.addActionListener(listener3);
					cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
					cutMenuItem.setMnemonic(KeyEvent.VK_T);
					cutMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-cut-6.png")));
					tablePopup.add(cutMenuItem);

					JMenuItem copyMenuItem = new JMenuItem("Copy");
					copyMenuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
					copyMenuItem.addActionListener(listener);
					copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
					copyMenuItem.setMnemonic(KeyEvent.VK_C);
					copyMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-copy-6.png")));
					tablePopup.add(copyMenuItem);

					JMenuItem pasteMenuItem = new JMenuItem("Paste");
					pasteMenuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
					pasteMenuItem.addActionListener(listener2);
					pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
					pasteMenuItem.setMnemonic(KeyEvent.VK_P);
					pasteMenuItem.setIcon(new ImageIcon(TopPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-paste-6.png")));
					tablePopup.add(pasteMenuItem);

					tablePopup.add(new JSeparator());

					JMenu fillMenu = new JMenu("Fill");
					tablePopup.add(fillMenu);

					JMenuItem downMenuItem = new JMenuItem("Down");
					downMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
					downMenuItem.setMnemonic(KeyEvent.VK_D);
					fillMenu.add(downMenuItem);
					if (table.getSelectionModel().getMaxSelectionIndex() == table.convertRowIndexToView(table.getModel().getRowCount() - 1)) {
						downMenuItem.setEnabled(false);
					}

					JMenuItem rightMenuItem = new JMenuItem("Right");
					fillMenu.add(rightMenuItem);
					if (table.getColumnModel().getSelectionModel().getMaxSelectionIndex() == table
							.convertColumnIndexToView(table.getModel().getColumnCount() - 1)) {
						rightMenuItem.setEnabled(false);
					}

					JMenuItem upMenuItem = new JMenuItem("Up");
					fillMenu.add(upMenuItem);
					if (table.getSelectionModel().getMaxSelectionIndex() == table.convertRowIndexToView(table.getModel().getRowCount() - 1)) {
						upMenuItem.setEnabled(false);
					}

					JMenuItem leftMenuItem = new JMenuItem("Left");
					fillMenu.add(leftMenuItem);
					if (table.getColumnModel().getSelectionModel().getMaxSelectionIndex() == table
							.convertColumnIndexToView(table.getModel().getColumnCount() - 1)) {
						leftMenuItem.setEnabled(false);
					}

					downMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							fill(Direction.DOWN);

						}

					});

					rightMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							fill(Direction.RIGHT);

						}

					});

					upMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							fill(Direction.UP);

						}

					});

					leftMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							fill(Direction.LEFT);

						}

					});

					if (table.getColumnModel().getSelectionModel().getMaxSelectionIndex() != table
							.convertColumnIndexToView(table.getModel().getColumnCount() - 1)) {

					}

					clearFormatMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							clearFormat();

						}

					});

					formatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showFormatDialog();

						}

					});

					conditionalFormatCellsMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							showConditionalFormattingDialog();

						}

					});

					tablePopup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}

			}

		});

		scrollPane.setViewportView(table);

		fixedTable.setDefaultRenderer(Object.class, new SpreadSheetFirstColumnTableRenderer(table));

		spreadSheetFirstColumnTableModel.setRowCount(spreadSheetTableModel.getRowCount());

		JViewport viewport = new JViewport();
		viewport.setView(fixedTable);
		viewport.setPreferredSize(fixedTable.getPreferredSize());
		scrollPane.setRowHeaderView(viewport);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());
		spreadSheetFirstColumnTableModel.fireTableDataChanged();
		fixedTable.repaint();
		table.repaint();

		updateCardlayout.show(tableCardPanel, "table");

		table.repaint();

	}

	/**
	 * On update sheet conditional format response.
	 *
	 * @param updateSheetConditionalFormatResponse the update sheet conditional format response
	 */
	public void onUpdateSheetConditionalFormatResponse(UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse) {

		spreadSheetTableModel.onUpdateSheetConditionalFormatResponse(updateSheetConditionalFormatResponse);
		table.repaint();

	}

	/**
	 * Format.
	 *
	 * @param formatAction the format action
	 */
	public void format(FormatAction formatAction) {

		switch (formatAction) {
			case CLEAR:
				clearFormat();
				break;
			case COLUMN_WIDTH:
				showColumnWidthDialog();
				break;
			case CONDITIONAL_FORMAT:
				showConditionalFormattingDialog();
				break;
			case DELETE_COLUMN:
				deleteColumn();
				break;
			case DELETE_ROW:
				deleteRow();
				break;
			case FORMAT:
				showFormatDialog();
				break;
			case HIDE_COLUMN:
				hideColumn();
				break;
			case HIDE_ROW:
				hideRow();
				break;
			case INSERT_COLUMN:
				insertColumn();
				break;
			case INSERT_ROW:
				insertRow();
				break;
			case SHOW_COLUMN:
				showColumn();
				break;
			case SHOW_ROW:
				showRow();
				break;
			case FILL_LEFT:
				fill(Direction.LEFT);
				break;
			case FILL_RIGHT:
				fill(Direction.RIGHT);
				break;
			case FILL_UP:
				fill(Direction.UP);
				break;
			case FILL_DOWN:
				fill(Direction.DOWN);
				break;
		}
	}

	/**
	 * Handle focus.
	 */
	public void handleFocus() {

		table.requestFocus();

	}

	/**
	 * On update full sheet response.
	 *
	 * @param updateFullSheetResponse the update full sheet response
	 */
	public void onUpdateFullSheetResponse(UpdateFullSheetResponse updateFullSheetResponse) {

		if (updateFullSheetResponse.getSheet() == spreadSheetEditor.getAbstractBusinessObject().getId()) {

			spreadSheetTableModel = new SpreadSheetTableModel(spreadSheetEditor);

			UpdateSheetCellFormatResponse updateSheetCellFormatResponse = updateFullSheetResponse.getUpdateSheetCellFormatResponse();

			if (updateSheetCellFormatResponse != null)
				spreadSheetTableModel.onUpdateSheetCellFormatResponse(updateSheetCellFormatResponse);

			UpdateColumnFormatResponse updateColumnFormatResponse = updateFullSheetResponse.getUpdateColumnFormatResponse();

			if (updateColumnFormatResponse != null)
				for (SpreadSheetColumnFormat spreadSheetColumnFormat : updateColumnFormatResponse.getSpreadSheetColumnFormats()) {

					if (spreadSheetColumnFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId())
						spreadSheetColumnFormatMap.put(spreadSheetColumnFormat.getColumnNumber(), spreadSheetColumnFormat);
				}

			UpdateRowFormatResponse updateRowFormatResponse = updateFullSheetResponse.getUpdateRowFormatResponse();

			if (updateRowFormatResponse != null)
				for (SpreadSheetRowFormat spreadSheetRowFormat : updateRowFormatResponse.getSpreadSheetRowFormats())
					if (spreadSheetRowFormat.getSpreadSheet() == spreadSheetEditor.getSpreadSheet().getId())
						spreadSheetTableRowFilter.put(spreadSheetRowFormat);

			UpdateSheetConditionalFormatResponse updateSheetConditionalFormatResponse = updateFullSheetResponse.getUpdateSheetConditionalFormatResponse();

			if (updateSheetConditionalFormatResponse != null)
				spreadSheetTableModel.onUpdateSheetConditionalFormatResponse(updateSheetConditionalFormatResponse);

			UpdateSheetCellResponse updateSheetCellResponse = updateFullSheetResponse.getUpdateSheetCellResponse();
			if (updateSheetCellResponse != null) {
				spreadSheetTableModel.onUpdateSheetCellResponse(updateSheetCellResponse);

				if (spreadSheetFirstColumnTableModel.getRowCount() != spreadSheetTableModel.getRowCount()) {
					spreadSheetFirstColumnTableModel.setRowCount(spreadSheetTableModel.getRowCount());
					spreadSheetFirstColumnTableModel.fireTableDataChanged();

					fixedTable.setDefaultRenderer(Object.class, new SpreadSheetFirstColumnTableRenderer(table));

					JViewport viewport = new JViewport();
					viewport.setView(fixedTable);
					viewport.setPreferredSize(fixedTable.getPreferredSize());
					scrollPane.setRowHeaderView(viewport);
					scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());
					spreadSheetFirstColumnTableModel.fireTableDataChanged();
					fixedTable.repaint();
					table.repaint();
				}
			}

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					initTable();

				}
			});
		}
	}

	/**
	 * Switch showing formula.
	 *
	 * @param showingFormula the showing formula
	 */
	public void switchShowingFormula(boolean showingFormula) {

		spreadSheetTableModel.switchShowingFormula(showingFormula);
		table.repaint();

	}

	/**
	 * Fill.
	 *
	 * @param direction the direction
	 */
	public void fill(Direction direction) {

		if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
			return;

		int minRow = table.convertRowIndexToModel(table.getSelectionModel().getMinSelectionIndex());
		int minCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMinSelectionIndex());

		int maxRow = table.convertRowIndexToModel(table.getSelectionModel().getMaxSelectionIndex());
		int maxCol = table.convertColumnIndexToModel(table.getColumnModel().getSelectionModel().getMaxSelectionIndex());

		spreadSheetEditor.getBasisClientConnector().sendRequest(
				new FillSpreadSheetCellRequest(spreadSheetEditor.getSpreadSheet().getId(), minRow, maxRow - minRow + 1, minCol, maxCol - minCol + 1, direction,
						RequestIDManager.getInstance().getID()));
	}

	/**
	 * Gets the selection state.
	 *
	 * @return the selection state
	 */
	public int getSelectionState() {

		if (table.getSelectedRows().length == 0 || table.getSelectedColumns().length == 0)
			return 0;

		if (table.getSelectionModel().getMaxSelectionIndex() == table.convertRowIndexToView(table.getModel().getRowCount() - 1)) {
			return 2;
		}

		if (table.getColumnModel().getSelectionModel().getMaxSelectionIndex() == table.convertColumnIndexToView(table.getModel().getColumnCount() - 1)) {
			return 3;
		}

		return 1;
	}

	/**
	 * Gets the spread sheet editor.
	 *
	 * @return the spread sheet editor
	 */
	public SpreadSheetEditor getSpreadSheetEditor() {

		return spreadSheetEditor;
	}

	private String getHyperlink(SpreadSheetCell spreadSheetCell) {

		if (spreadSheetCell == null)
			return null;

		if (spreadSheetCell.getFormula() == null)
			return null;

		String formula = spreadSheetCell.getFormula();

		if (formula.replaceAll("\\s+", "").toUpperCase().contains("HYPERLINK(")) {

			int index = formula.toUpperCase().indexOf("HYPERLINK");
			int startIndex = -1;
			int stopIndex = -1;

			while (index < formula.length() && stopIndex == -1) {
				if (formula.charAt(index) == '\"') {
					if (startIndex == -1)
						startIndex = index;
					else
						stopIndex = index;
				}
				index++;
			}
			if (stopIndex == -1)
				return null;
			else
				return formula.substring(startIndex + 1, stopIndex);
		}
		return null;
	}

	/**
	 * Gets the cell update handler.
	 *
	 * @return the cell update handler
	 */
	public CellUpdateHandler getCellUpdateHandler() {

		return cellUpdateHandler;
	}

}