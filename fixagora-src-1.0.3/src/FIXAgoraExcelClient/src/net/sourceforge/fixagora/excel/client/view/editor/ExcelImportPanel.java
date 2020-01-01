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
package net.sourceforge.fixagora.excel.client.view.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.editor.DefaultEditorComboBoxRenderer;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.PersistResponse.PersistStatus;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateResponse.UpdateStatus;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurityGroup;
import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel;
import net.sourceforge.fixagora.excel.client.model.ExcelImportTableModel.EntryStatus;
import net.sourceforge.fixagora.excel.client.model.UploadEntry;
import net.sourceforge.fixagora.excel.shared.communication.SecurityListResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The Class ExcelImportPanel.
 */
public class ExcelImportPanel extends JPanel implements TableModelListener, TableColumnModelListener {

	private static final long serialVersionUID = 1L;
	private JTextField fileTextField;
	private JButton okButton;
	private Color blue = new Color(204, 216, 255);
	private JComboBox sheetComboBox;
	private JTextField statusTextField;
	private JButton browseButton;
	private List<FSecurity> securities = null;
	private JScrollPane scrollPane;
	private JTable table = null;
	
	/** The workbook. */
	protected Workbook workbook = null;
	private JCheckBox replaceOldInstrumentCombobox;
	private ExcelImportTableModel excelImportTableModel;
	private FSecurityGroup parentGroup;
	private BasisClientConnector basisClientConnector;
	private final ImageIcon warning = new ImageIcon(ExcelImportPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/status_unknown.png"));
	private final ImageIcon bug = new ImageIcon(ExcelImportPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	private final ImageIcon success = new ImageIcon(ExcelImportPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/agt_action_success.png"));
	
	/** The uploading. */
	public boolean uploading = false;
	private JLabel fileWarningLabel;
	private JLabel sheetWarningLabel;
	private JLabel statusWarningLabel;

	/**
	 * Instantiates a new excel import panel.
	 *
	 * @param basisClientConnector the basis client connector
	 * @param securityListResponse the security list response
	 */
	public ExcelImportPanel(final BasisClientConnector basisClientConnector, SecurityListResponse securityListResponse) {

		this.basisClientConnector = basisClientConnector;
		this.securities = securityListResponse.getSecurities();
		this.parentGroup = securityListResponse.getSecurityGroup();

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkConsistency();
			}
		};

		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 52, 0 };
		setLayout(gbl_contentPanel);

		JLabel fileLabel = new JLabel("File");
		fileLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 0;
		add(fileLabel, gbc_legSecurityLabel);
		
		fileWarningLabel = new JLabel("");
		fileWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_fileWarningLabel = new GridBagConstraints();
		gbc_fileWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileWarningLabel.insets = new Insets(25, 0, 5, 5);
		gbc_fileWarningLabel.gridx = 1;
		gbc_fileWarningLabel.gridy = 0;
		add(fileWarningLabel, gbc_fileWarningLabel);

		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileTextField.setMinimumSize(new Dimension(200, 25));
		fileTextField.setPreferredSize(new Dimension(200, 25));
		fileTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		fileTextField.setBackground(new Color(204, 216, 255));
		fileTextField.setColumns(10);
		
		GridBagConstraints gbc_fileTextField = new GridBagConstraints();
		gbc_fileTextField.insets = new Insets(25, 0, 5, 25);
		gbc_fileTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileTextField.gridx = 2;
		gbc_fileTextField.gridy = 0;
		add(fileTextField, gbc_fileTextField);

		browseButton = new JButton("Open");
		browseButton.setPreferredSize(new Dimension(100, 25));
		browseButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		browseButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder.png")));

		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(25, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 0;
		add(browseButton, gbc_btnNewButton);

		JLabel sideLabel = new JLabel("Sheet");
		sideLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sideLabel = new GridBagConstraints();
		gbc_sideLabel.anchor = GridBagConstraints.WEST;
		gbc_sideLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sideLabel.gridx = 0;
		gbc_sideLabel.gridy = 1;
		add(sideLabel, gbc_sideLabel);

		sheetWarningLabel = new JLabel("");
		sheetWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sheetWarningLabel = new GridBagConstraints();
		gbc_sheetWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheetWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sheetWarningLabel.gridx = 1;
		gbc_sheetWarningLabel.gridy = 1;
		add(sheetWarningLabel, gbc_sheetWarningLabel);
		
		sheetComboBox = new JComboBox();
		sheetComboBox.setMinimumSize(new Dimension(32, 25));
		sheetComboBox.setRenderer(new DefaultEditorComboBoxRenderer());
		sheetComboBox.setPreferredSize(new Dimension(32, 25));
		sheetComboBox.setBackground(new Color(255, 243, 204));
		GridBagConstraints gbc_sheetComboBox = new GridBagConstraints();
		gbc_sheetComboBox.anchor = GridBagConstraints.NORTH;
		gbc_sheetComboBox.insets = new Insets(0, 0, 5, 25);
		gbc_sheetComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheetComboBox.gridx = 2;
		gbc_sheetComboBox.gridy = 1;
		add(sheetComboBox, gbc_sheetComboBox);

		browseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Excel Export");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				final FileFilter fileFilter = new FileFilter() {

					@Override
					public boolean accept(final File file) {

						if (file.isDirectory())
							return true;

						else {

							final String filename = file.getName();
							return filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx");
						}
					}

					@Override
					public String getDescription() {

						return "*.xls";
					}
				};

				chooser.setFileFilter(fileFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setApproveButtonText("Open");

				if (chooser.showOpenDialog(ExcelImportPanel.this) == JFileChooser.APPROVE_OPTION) {

					final File file = new File(chooser.getSelectedFile().toString());

					fileTextField.setText(chooser.getSelectedFile().toString());
					
					if (file.exists()) {

						try {
							final FileInputStream fileInputStream = new FileInputStream(file);
							if (file.getName().toLowerCase().endsWith(".xlsx"))
								workbook  = new XSSFWorkbook(fileInputStream);
							else
								workbook = new HSSFWorkbook(fileInputStream);
							sheetComboBox.removeAllItems();
							for (int i = 0; i < workbook.getNumberOfSheets(); i++)
								sheetComboBox.addItem(workbook.getSheetName(i));
							sheetComboBox.setSelectedIndex(0);
						}
						catch (Exception e1) {
							workbook=null;
							sheetComboBox.removeAllItems();
							checkConsistency();
						}
					}

				}

			}
		});

		sheetComboBox.addActionListener(actionListener);

		replaceOldInstrumentCombobox = new JCheckBox("Replace old instrument values");
		replaceOldInstrumentCombobox.setOpaque(false);
		replaceOldInstrumentCombobox.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_chckbxReplaceOldInstrument = new GridBagConstraints();
		gbc_chckbxReplaceOldInstrument.gridwidth = 2;
		gbc_chckbxReplaceOldInstrument.insets = new Insets(0, 0, 5, 25);
		gbc_chckbxReplaceOldInstrument.gridx = 3;
		gbc_chckbxReplaceOldInstrument.gridy = 1;
		add(replaceOldInstrumentCombobox, gbc_chckbxReplaceOldInstrument);

		replaceOldInstrumentCombobox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				excelImportTableModel.setUpdateExisting(replaceOldInstrumentCombobox.isSelected());
				excelImportTableModel.fireTableDataChanged();
				
			}
		});
		
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.anchor = GridBagConstraints.WEST;
		gbc_statusLabel.insets = new Insets(0, 25, 5, 15);
		gbc_statusLabel.gridx = 0;
		gbc_statusLabel.gridy = 2;
		add(statusLabel, gbc_statusLabel);
		
		statusWarningLabel = new JLabel("");
		statusWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_statusWarningLabel = new GridBagConstraints();
		gbc_statusWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusWarningLabel.gridx = 1;
		gbc_statusWarningLabel.gridy = 2;
		add(statusWarningLabel, gbc_statusWarningLabel);

		statusTextField = new JTextField();
		statusTextField.setMinimumSize(new Dimension(200, 25));
		statusTextField.setPreferredSize(new Dimension(200, 25));
		statusTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));
		statusTextField.setBackground(new Color(204, 216, 255));
		statusTextField.setColumns(10);
		GridBagConstraints gbc_dirtyPriceTextField = new GridBagConstraints();
		gbc_dirtyPriceTextField.gridwidth = 3;
		gbc_dirtyPriceTextField.insets = new Insets(0, 0, 5, 25);
		gbc_dirtyPriceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_dirtyPriceTextField.gridx = 2;
		gbc_dirtyPriceTextField.gridy = 2;
		add(statusTextField, gbc_dirtyPriceTextField);

		scrollPane = new JScrollPane(){

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
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(25, 25, 5, 25);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		add(scrollPane, gbc_scrollPane);

		okButton = new JButton("Upload");

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 25);
		gbc_okButton.gridx = 4;
		gbc_okButton.gridy = 5;
		add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder_sent_mail.png")));
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UploadThread uploadThread = new UploadThread();
				Thread thread = new Thread(uploadThread);
				thread.start();
			}
		});

		initTable(null, false);

		checkConsistency();
		
	}


	private void checkConsistency() {

		Sheet sheet = null;
		
		if(fileTextField.getText().trim().length()==0)
		{
			fileWarningLabel.setIcon(bug);
			fileWarningLabel.setToolTipText("No excel file selected.");
		}
		else
		{
			fileWarningLabel.setIcon(null);
			fileWarningLabel.setToolTipText(null);			
		}
		
		if(sheetComboBox.getSelectedIndex()==-1)
		{
			sheetWarningLabel.setIcon(bug);
			sheetWarningLabel.setToolTipText("No sheet selected.");
		}
		else
		{
			sheetWarningLabel.setIcon(null);
			sheetWarningLabel.setToolTipText(null);			
		}		
		
		
		if(workbook!=null&&sheetComboBox.getSelectedIndex()>-1)
		{
			sheet = workbook.getSheetAt(sheetComboBox.getSelectedIndex());
		}
		initTable(sheet, replaceOldInstrumentCombobox.isSelected());

	}

	private synchronized void initTable(Sheet sheet, boolean updateExisting) {

		if (table != null)
			scrollPane.getViewport().remove(table);

		table = new JTable();

		table.setTableHeader(new JTableHeader(table.getColumnModel()));

		table.setRequestFocusEnabled(true);
		table.setFocusable(true);
		excelImportTableModel = new ExcelImportTableModel(sheet, updateExisting, table, securities);
		table.setModel(excelImportTableModel);
		table.setDefaultRenderer(Object.class, new ExcelImportTableRenderer());
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setGridColor(blue.darker());
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setAutoscrolls(false);
		table.setRowHeight(25);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(true);
		table.requestFocus();
		for (int i = 0; i < table.getModel().getColumnCount(); i++) {
			TableColumn tableColumn2 = table.getColumnModel().getColumn(i);
			tableColumn2.setPreferredWidth(100);
			tableColumn2.setCellEditor(new ExcelImportCellEditor(excelImportTableModel));
			tableColumn2.setHeaderRenderer(new ExcelImportHeaderRenderer());
		}
		excelImportTableModel.addTableModelListener(ExcelImportPanel.this);
		table.getColumnModel().addColumnModelListener(this);
		statusTextField.setText(excelImportTableModel.getStatusText());
		scrollPane.setViewportView(table);
		
		uploading = false;
		
		excelImportTableModel.fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		
		String text = excelImportTableModel.getStatusText();
		
		if(text!=null)
		{
			statusWarningLabel.setIcon(bug);
			statusWarningLabel.setToolTipText("The sheet is not consistent.");			
		}
		else
		{
			statusWarningLabel.setIcon(null);
			statusWarningLabel.setToolTipText(null);						
		}
		
		if(text==null&&!uploading)
		{
			statusWarningLabel.setIcon(success);
			statusWarningLabel.setToolTipText("The sheet is configured. The upload could start now.");
			text = "The sheet is configured. The upload could start now.";
			okButton.setEnabled(true);
		}
		else 
			okButton.setEnabled(false);
		
		if(!uploading)
			statusTextField.setText(text);
		else
		{
			statusWarningLabel.setIcon(warning);
			statusWarningLabel.setToolTipText("Upload in progress.");
		}

		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
	 */
	@Override
	public void columnAdded(TableColumnModelEvent e) {


		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
	 */
	@Override
	public void columnRemoved(TableColumnModelEvent e) {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableColumnModelListener#columnMoved(javax.swing.event.TableColumnModelEvent)
	 */
	@Override
	public void columnMoved(TableColumnModelEvent e) {

		excelImportTableModel.fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void columnMarginChanged(ChangeEvent e) {

		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {

		// TODO Auto-generated method stub
		
	}
	
	private class UploadThread implements Runnable
	{

		@Override
		public void run() {
			
			uploading = true;

			browseButton.setEnabled(false);
			okButton.setEnabled(false);
			sheetComboBox.setEnabled(false);
			sheetComboBox.setBackground(blue);
			replaceOldInstrumentCombobox.setEnabled(false);
			
			int successCount = 0;  
			List<UploadEntry> uploadEntries = excelImportTableModel.getSecurities();
			for(UploadEntry uploadEntry: uploadEntries)
			{
				FSecurity security = uploadEntry.getSecurity();
				statusTextField.setText("Uploading "+security.getName()+" ...");
				excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOADING);
				excelImportTableModel.fireTableDataChanged();
				if(security.getId()==0)
				{
					security.setParent(parentGroup);
					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new PersistRequest(security, RequestIDManager.getInstance().getID()));
					if(abstractResponse instanceof ErrorResponse)
						excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOAD_FAILED);
					else if(abstractResponse instanceof PersistResponse)
					{
						PersistResponse persistResponse = (PersistResponse)abstractResponse;
						if(persistResponse.getPersistStatus()==PersistStatus.SUCCESS)
							excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOADED);
						else
							excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOAD_FAILED);
						successCount++;
					}
				}
				else
				{	
					AbstractResponse abstractResponse = basisClientConnector.sendRequest(new UpdateRequest(security, RequestIDManager.getInstance().getID()));
					if(abstractResponse instanceof ErrorResponse)
						excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOAD_FAILED);
					else if(abstractResponse instanceof UpdateResponse)
					{
						UpdateResponse persistResponse = (UpdateResponse)abstractResponse;
						if(persistResponse.getUpdateStatus()==UpdateStatus.SUCCESS)
							excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOADED);
						else
							excelImportTableModel.setUploadStatus(uploadEntry.getRow(), EntryStatus.UPLOAD_FAILED);
						successCount++;
					}
				}
				excelImportTableModel.fireTableDataChanged();
			}
			statusTextField.setText(successCount +" of "+uploadEntries.size()+" successfully imported.");
			statusWarningLabel.setIcon(success);
			statusWarningLabel.setToolTipText(successCount +" of "+uploadEntries.size()+" successfully imported.");
			browseButton.setEnabled(true);
			sheetComboBox.setEnabled(true);
			sheetComboBox.setBackground(new Color(255, 243, 204));
			replaceOldInstrumentCombobox.setEnabled(true);			
			
		}
		
	}

}
