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
package net.sourceforge.fixagora.excel.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.fixagora.basis.client.control.RequestIDManager;
import net.sourceforge.fixagora.basis.client.view.dialog.LoginDialog;
import net.sourceforge.fixagora.basis.client.view.dialog.WaitDialog;
import net.sourceforge.fixagora.basis.shared.model.communication.UpdateRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessObject;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.excel.client.model.editor.ExcelTradeCaptureEditorMonitorTableModel;
import net.sourceforge.fixagora.excel.client.view.editor.ExcelTradeCaptureEditor;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntry;
import net.sourceforge.fixagora.excel.shared.communication.ExcelTradeCaptureEntryRequest;
import net.sourceforge.fixagora.excel.shared.persistence.AssignedUserSettings;
import net.sourceforge.fixagora.excel.shared.persistence.ExcelTradeCapture;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The Class ExcelExportDialog.
 */
public class ExcelExportDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField fileTextField;
	private JButton okButton;
	private JTextField sheetTextField;
	private JButton browseButton;
	
	/** The workbook. */
	protected Workbook workbook = null;
	private JCheckBox replaceOldSheetCombobox;
	private final ImageIcon bug = new ImageIcon(ExcelExportDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/stop.png"));
	
	/** The uploading. */
	public boolean uploading = false;
	private JLabel fileWarningLabel;
	private JLabel sheetWarningLabel;
	private JCheckBox removeAfterExport;
	private AssignedUserSettings assignedUserSettings = null;

	/**
	 * Instantiates a new excel export dialog.
	 *
	 * @param excelTradeCaptureEditorMonitorTableModel the excel trade capture editor monitor table model
	 * @param excelTradeCaptureEditor the excel trade capture editor
	 */
	public ExcelExportDialog(final ExcelTradeCaptureEditorMonitorTableModel excelTradeCaptureEditorMonitorTableModel,
			final ExcelTradeCaptureEditor excelTradeCaptureEditor) {

		setTitle("Excel © Trade Capture Export");
		setBounds(100, 100, 598, 183);
		setBackground(new Color(204, 216, 255));
		setIconImage(new ImageIcon(ExcelExportDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/a-logo.png")).getImage());
		setModal(false);

		final FUser fUser = excelTradeCaptureEditor.getBasisClientConnector().getFUser();

		for (AssignedUserSettings userSettings2 : excelTradeCaptureEditor.getExcelTradeCapture().getAssignedUserSettings())
			if (userSettings2.getfUser().getId() == fUser.getId())
				assignedUserSettings = userSettings2;
		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {

				checkConsistency();

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

				checkConsistency();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				checkConsistency();

			}

		};

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setBackground(new Color(204, 216, 255));
		getContentPane().add(contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0, 52, 0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel fileLabel = new JLabel("File");
		fileLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_legSecurityLabel = new GridBagConstraints();
		gbc_legSecurityLabel.anchor = GridBagConstraints.WEST;
		gbc_legSecurityLabel.insets = new Insets(25, 25, 5, 5);
		gbc_legSecurityLabel.gridx = 0;
		gbc_legSecurityLabel.gridy = 0;
		contentPanel.add(fileLabel, gbc_legSecurityLabel);

		fileWarningLabel = new JLabel("");
		fileWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_fileWarningLabel = new GridBagConstraints();
		gbc_fileWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileWarningLabel.insets = new Insets(25, 0, 5, 5);
		gbc_fileWarningLabel.gridx = 1;
		gbc_fileWarningLabel.gridy = 0;
		contentPanel.add(fileWarningLabel, gbc_fileWarningLabel);

		fileTextField = new JTextField();
		fileTextField.setBackground(new Color(255, 243, 204));
		fileTextField.setMinimumSize(new Dimension(200, 25));
		fileTextField.setPreferredSize(new Dimension(200, 25));
		fileTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));

		if (assignedUserSettings != null)
			fileTextField.setText(assignedUserSettings.getPath());

		fileTextField.getDocument().addDocumentListener(documentListener);

		GridBagConstraints gbc_fileTextField = new GridBagConstraints();
		gbc_fileTextField.gridwidth = 3;
		gbc_fileTextField.insets = new Insets(25, 0, 5, 5);
		gbc_fileTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileTextField.gridx = 2;
		gbc_fileTextField.gridy = 0;
		contentPanel.add(fileTextField, gbc_fileTextField);

		browseButton = new JButton("Browse");
		browseButton.setMinimumSize(new Dimension(100, 25));
		browseButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		browseButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/folder.png")));

		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(25, 0, 5, 25);
		gbc_btnNewButton.gridx = 5;
		gbc_btnNewButton.gridy = 0;
		contentPanel.add(browseButton, gbc_btnNewButton);

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

				if (chooser.showOpenDialog(ExcelExportDialog.this) == JFileChooser.APPROVE_OPTION) {

					String filename = chooser.getSelectedFile().toString();
					if (!filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx"))
						filename = filename + ".xls";
					fileTextField.setText(filename);

				}

			}
		});

		JLabel sideLabel = new JLabel("Sheet");
		sideLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_sideLabel = new GridBagConstraints();
		gbc_sideLabel.anchor = GridBagConstraints.WEST;
		gbc_sideLabel.insets = new Insets(0, 25, 5, 5);
		gbc_sideLabel.gridx = 0;
		gbc_sideLabel.gridy = 1;
		contentPanel.add(sideLabel, gbc_sideLabel);

		sheetWarningLabel = new JLabel("");
		sheetWarningLabel.setPreferredSize(new Dimension(21, 25));
		GridBagConstraints gbc_sheetWarningLabel = new GridBagConstraints();
		gbc_sheetWarningLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheetWarningLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sheetWarningLabel.gridx = 1;
		gbc_sheetWarningLabel.gridy = 1;
		contentPanel.add(sheetWarningLabel, gbc_sheetWarningLabel);

		sheetTextField = new JTextField();
		sheetTextField.setBackground(new Color(255, 243, 204));
		sheetTextField.setMinimumSize(new Dimension(100, 25));
		sheetTextField.setPreferredSize(new Dimension(100, 25));
		sheetTextField.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(0, 5, 0, 0)));

		if (assignedUserSettings != null)
			sheetTextField.setText(assignedUserSettings.getSheet());

		sheetTextField.getDocument().addDocumentListener(documentListener);

		GridBagConstraints gbc_sheetComboBox = new GridBagConstraints();
		gbc_sheetComboBox.gridwidth = 3;
		gbc_sheetComboBox.anchor = GridBagConstraints.NORTH;
		gbc_sheetComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_sheetComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheetComboBox.gridx = 2;
		gbc_sheetComboBox.gridy = 1;
		contentPanel.add(sheetTextField, gbc_sheetComboBox);

		removeAfterExport = new JCheckBox("Remove after export");
		removeAfterExport.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeAfterExport.setOpaque(false);
		GridBagConstraints gbc_removeAfterExport = new GridBagConstraints();
		gbc_removeAfterExport.anchor = GridBagConstraints.WEST;
		gbc_removeAfterExport.insets = new Insets(0, 0, 5, 5);
		gbc_removeAfterExport.gridx = 2;
		gbc_removeAfterExport.gridy = 2;
		contentPanel.add(removeAfterExport, gbc_removeAfterExport);

		if (assignedUserSettings != null && assignedUserSettings.getDeleteAfterExport() != null)
			removeAfterExport.setSelected(assignedUserSettings.getDeleteAfterExport());

		replaceOldSheetCombobox = new JCheckBox("Replace old sheet");
		replaceOldSheetCombobox.setOpaque(false);
		replaceOldSheetCombobox.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_chckbxReplaceOldInstrument = new GridBagConstraints();
		gbc_chckbxReplaceOldInstrument.gridwidth = 2;
		gbc_chckbxReplaceOldInstrument.anchor = GridBagConstraints.WEST;
		gbc_chckbxReplaceOldInstrument.insets = new Insets(0, 0, 5, 25);
		gbc_chckbxReplaceOldInstrument.gridx = 3;
		gbc_chckbxReplaceOldInstrument.gridy = 2;
		contentPanel.add(replaceOldSheetCombobox, gbc_chckbxReplaceOldInstrument);

		if (assignedUserSettings != null && assignedUserSettings.getReplaceOldSheet() != null)
			replaceOldSheetCombobox.setSelected(assignedUserSettings.getReplaceOldSheet());

		okButton = new JButton("Export");
		okButton.setMinimumSize(new Dimension(100, 25));
		okButton.setMargin(new Insets(2, 5, 2, 5));

		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_okButton.insets = new Insets(15, 0, 15, 5);
		gbc_okButton.gridx = 4;
		gbc_okButton.gridy = 3;
		contentPanel.add(okButton, gbc_okButton);
		okButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.setActionCommand("OK");
		okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/excel/client/view/images/16x16/fileimport.png")));
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final WaitDialog waitDialog = new WaitDialog(excelTradeCaptureEditor.getMainPanel(), "Export...");

				final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						try {
							final File file = new File(fileTextField.getText());

							if (file.exists()) {

								try {
									final FileInputStream fileInputStream = new FileInputStream(file);
									if (file.getName().toLowerCase().endsWith(".xlsx"))
										workbook = new XSSFWorkbook(fileInputStream);
									else
										workbook = new HSSFWorkbook(fileInputStream);
								}
								catch (Exception e1) {
									if (file.getName().toLowerCase().endsWith(".xlsx"))
										workbook = new XSSFWorkbook();
									else
										workbook = new HSSFWorkbook();
								}
							}
							else {
								if (file.getName().toLowerCase().endsWith(".xlsx"))
									workbook = new XSSFWorkbook();
								else
									workbook = new HSSFWorkbook();
							}

							Sheet sheet = workbook.getSheet(sheetTextField.getText().trim());
							if (sheet != null && replaceOldSheetCombobox.isSelected()) {
								workbook.removeSheetAt(workbook.getSheetIndex(sheetTextField.getText().trim()));
								sheet = null;
							}
							if (sheet == null) {
								sheet = workbook.createSheet(sheetTextField.getText().trim());
							}

							CellStyle cellStyle = null;

							List<ExcelTradeCaptureEntry> excelTradeCaptureEntries = new ArrayList<ExcelTradeCaptureEntry>();
							excelTradeCaptureEntries.addAll(excelTradeCaptureEditorMonitorTableModel.getExcelTradeCaptureEntries());

							if (sheet.getLastRowNum() == 0) {
								Row row = sheet.createRow(0);

								cellStyle = workbook.createCellStyle();
								org.apache.poi.ss.usermodel.Font font = workbook.createFont();
								font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
								cellStyle.setFont(font);

								for (int i = 1; i < 21; i++) {
									Cell cell = row.createCell(i - 1);
									cell.setCellValue((String) excelTradeCaptureEditorMonitorTableModel.getValueAt(0, i));
									cell.setCellStyle(cellStyle);
								}
							}

							for (ExcelTradeCaptureEntry excelTradeCaptureEntry : excelTradeCaptureEntries) {

								if (removeAfterExport.isSelected())
									excelTradeCaptureEntry.setRemoved(true);

								Row row = sheet.createRow(sheet.getLastRowNum() + 1);

								String sourceName = "Unknown";

								for (AbstractBusinessObject abstractBusinessComponent : excelTradeCaptureEditor.getMainPanel().getBusinessComponents())
									if (abstractBusinessComponent.getId() == excelTradeCaptureEntry.getSourceComponent())
										sourceName = abstractBusinessComponent.getName();

								Cell cell = row.createCell(0);
								cell.setCellValue(sourceName);

								String marketName = "Unknown";

								if (excelTradeCaptureEntry.getMarket() != null)
									marketName = excelTradeCaptureEntry.getMarket();

								cell = row.createCell(1);
								cell.setCellValue(marketName);

								cell = row.createCell(2);

								if (excelTradeCaptureEntry.getSide() == ExcelTradeCaptureEntry.Side.SELL) {
									cellStyle = workbook.createCellStyle();
									org.apache.poi.ss.usermodel.Font font = workbook.createFont();
									font.setColor(IndexedColors.RED.getIndex());
									cellStyle.setFont(font);
									cell.setCellValue("Sell");
									cell.setCellStyle(cellStyle);
								}
								else {
									cellStyle = workbook.createCellStyle();
									org.apache.poi.ss.usermodel.Font font = workbook.createFont();
									font.setColor(IndexedColors.GREEN.getIndex());
									cellStyle.setFont(font);
									cell.setCellValue("Buy");
									cell.setCellStyle(cellStyle);
								}

								cell = row.createCell(3);

								FSecurity security = excelTradeCaptureEditor.getMainPanel().getSecurityTreeDialog()
										.getSecurityForId(excelTradeCaptureEntry.getSecurity());
								if (security != null)
									cell.setCellValue(security.getName());
								else
									cell.setCellValue("Unknown");

								cell = row.createCell(4);

								if (security != null) {
									String securityIDSource = excelTradeCaptureEditor.getExcelTradeCapture().getSecurityIDSource();
									String securityID = security.getSecurityID();
									if (securityIDSource != null)
										for (SecurityAltIDGroup securityAltIDGroup : security.getSecurityDetails().getSecurityAltIDGroups())
											if (securityIDSource.equals(securityAltIDGroup.getSecurityAltIDSource()))
												securityID = securityAltIDGroup.getSecurityAltID();
									cell.setCellValue(securityID);
								}

								cell = row.createCell(5);

								Counterparty counterparty = excelTradeCaptureEditor.getMainPanel().getCounterpartyTreeDialog()
										.getCounterpartyForId(excelTradeCaptureEntry.getCounterparty());
								if (counterparty != null)
									cell.setCellValue(counterparty.getName());
								else
									cell.setCellValue("Unknown");

								cell = row.createCell(6);

								String counterPartyID = null;
								for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
									if (counterPartyPartyID.getAbstractBusinessComponent() == null) {
										if (counterPartyID == null)
											counterPartyID = counterPartyPartyID.getPartyID();
									}
									else if (counterPartyPartyID.getPartyRole() != null
											&& counterPartyPartyID.getPartyRole() == 17
											&& counterPartyPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture()
													.getId())
										counterPartyID = counterPartyPartyID.getPartyID();
								}

								if (counterPartyID == null)
									cell.setCellValue("");
								else
									cell.setCellValue(counterPartyID);

								cell = row.createCell(7);

								Trader trader = null;

								if (excelTradeCaptureEntry.getTrader() != null) {
									AbstractBusinessObject abstractBusinessObject = excelTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
											excelTradeCaptureEntry.getTrader());
									if (abstractBusinessObject instanceof Trader) {
										trader = (Trader) abstractBusinessObject;
										cell.setCellValue(abstractBusinessObject.getName());
									}
								}

								cell = row.createCell(8);

								String traderID = null;

								if (trader != null) {
									for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
										if (traderPartyID.getAbstractBusinessComponent() == null) {
											if (traderID == null)
												traderID = traderPartyID.getPartyID();
										}
										else if (traderPartyID.getPartyRole() != null
												&& traderPartyID.getPartyRole() == 37
												&& traderPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture()
														.getId())
											traderID = traderPartyID.getPartyID();
									}
								}

								if (traderID == null)
									cell.setCellValue("");
								else
									cell.setCellValue(traderID);

								cell = row.createCell(9);

								if (excelTradeCaptureEntry.getLastPrice() != null) {
									cell.setCellValue(excelTradeCaptureEntry.getLastPrice());
									cellStyle = workbook.createCellStyle();
									cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
											.getFormat(excelTradeCaptureEditorMonitorTableModel.getDecimalFormatPattern()));
									cell.setCellStyle(cellStyle);
								}

								cell = row.createCell(10);

								if (excelTradeCaptureEntry.getLastYield() != null) {
									cell.setCellValue(excelTradeCaptureEntry.getLastYield());
									cellStyle = workbook.createCellStyle();
									cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
											.getFormat(excelTradeCaptureEditorMonitorTableModel.getDecimalFormatPattern()));
									cell.setCellStyle(cellStyle);
								}

								cell = row.createCell(11);

								if (excelTradeCaptureEntry.getLastQuantity() != null) {
									cell.setCellValue(excelTradeCaptureEntry.getLastQuantity());
									cellStyle = workbook.createCellStyle();
									cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.########"));
									cell.setCellStyle(cellStyle);
								}

								cell = row.createCell(12);

								if (excelTradeCaptureEntry.getSettlementDate() != null)
									cell.setCellValue(new Date(excelTradeCaptureEntry.getSettlementDate()));

								cellStyle = workbook.createCellStyle();
								cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
								cell.setCellStyle(cellStyle);

								cell = row.createCell(13);

								FUser user = null;

								if (excelTradeCaptureEntry.getUser() != null) {
									AbstractBusinessObject abstractBusinessObject = excelTradeCaptureEditor.getMainPanel().getAbstractBusinessObjectForId(
											excelTradeCaptureEntry.getUser());
									if (abstractBusinessObject instanceof FUser) {
										user = (FUser) abstractBusinessObject;
										cell.setCellValue(user.getName());
									}
								}

								cell = row.createCell(14);

								if (user != null) {
									
									String userID = null;
									
									for (FUserPartyID userPartyID : user.getfUserPartyIDs()) {
										if (userPartyID.getAbstractBusinessComponent() == null) {
											if (userID == null)
												userID = userPartyID.getPartyID();
										}
										else if (userPartyID.getPartyRole() != null && userPartyID.getPartyRole() == 12
												&& userPartyID.getAbstractBusinessComponent().getId() == excelTradeCaptureEditor.getExcelTradeCapture().getId())
											userID = userPartyID.getPartyID();
									}

									if (userID == null)
										cell.setCellValue("");
									else
										cell.setCellValue(userID);

								}
								else if (excelTradeCaptureEntry.getUserID() != null)
									cell.setCellValue(excelTradeCaptureEntry.getUserID());

								cell = row.createCell(15);
								cell.setCellValue(excelTradeCaptureEntry.getTradeId());

								cell = row.createCell(16);

								if (excelTradeCaptureEntry.getOrderId() != null)
									cell.setCellValue(excelTradeCaptureEntry.getOrderId());

								cell = row.createCell(17);
								if (excelTradeCaptureEntry.getCounterpartyOrderId() != null)
									cell.setCellValue(excelTradeCaptureEntry.getCounterpartyOrderId());

								cell = row.createCell(18);
								if (excelTradeCaptureEntry.getExecId() != null)
									cell.setCellValue(excelTradeCaptureEntry.getExecId());

								cell = row.createCell(19);
								Calendar calendar2 = Calendar.getInstance();
								calendar2.setTimeInMillis(excelTradeCaptureEntry.getUpdated());
								cell.setCellValue(calendar2);

								cellStyle = workbook.createCellStyle();
								cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy hh:mm:ss"));
								cell.setCellStyle(cellStyle);
							}

							for (int i = 0; i < 20; i++) {
								sheet.autoSizeColumn(i);
								int width = sheet.getColumnWidth(i);
								sheet.setColumnWidth(i, (int) (width * 1.1));
							}

							FileOutputStream fileOut = new FileOutputStream(file);
							workbook.write(fileOut);
							fileOut.close();

							ExcelTradeCapture excelTradeCapture = excelTradeCaptureEditor.getExcelTradeCapture();

							if (assignedUserSettings == null) {
								assignedUserSettings = new AssignedUserSettings();
								assignedUserSettings.setfUser(fUser);
								assignedUserSettings.setExcelTradeCapture(excelTradeCapture);
								excelTradeCapture.getAssignedUserSettings().add(assignedUserSettings);
							}

							assignedUserSettings.setPath(fileTextField.getText());
							assignedUserSettings.setSheet(sheetTextField.getText());
							assignedUserSettings.setReplaceOldSheet(replaceOldSheetCombobox.isSelected());
							assignedUserSettings.setDeleteAfterExport(removeAfterExport.isSelected());

							excelTradeCaptureEditor.getBasisClientConnector().sendRequest(
									new UpdateRequest(excelTradeCapture, RequestIDManager.getInstance().getID()));

							if (removeAfterExport.isSelected())
								excelTradeCaptureEditor.getBasisClientConnector().sendRequest(
										new ExcelTradeCaptureEntryRequest(excelTradeCaptureEntries, RequestIDManager.getInstance().getID()));

							return null;
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							return null;
						}
					}

					@Override
					protected void done() {

						super.done();
						waitDialog.setVisible(false);
					}

				};

				setVisible(false);
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					
						waitDialog.setVisible(true);
						swingWorker.execute();
						
						
					}
				});
			}
		});

		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Close");
		cancelButton.setMinimumSize(new Dimension(100, 25));
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_cancelButton.insets = new Insets(15, 0, 15, 25);
		gbc_cancelButton.gridx = 5;
		gbc_cancelButton.gridy = 3;
		contentPanel.add(cancelButton, gbc_cancelButton);
		cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/fileclose.png")));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setVisible(false);
			}
		});

		checkConsistency();

	}

	private void checkConsistency() {

		boolean valid = true;

		if (fileTextField.getText().trim().length() == 0) {
			fileWarningLabel.setIcon(bug);
			fileWarningLabel.setToolTipText("No excel file selected.");
			valid = false;
		}
		else {
			fileWarningLabel.setIcon(null);
			fileWarningLabel.setToolTipText(null);
		}

		try {
			File file = new File(fileTextField.getText().trim());

			if (file.isDirectory())
				throw new FileNotFoundException();

			if (!file.exists()) {
				file.createNewFile();
				file.delete();
			}
			else if (!file.canWrite())
				throw new FileNotFoundException();
		}
		catch (Exception e) {
			valid = false;
			fileWarningLabel.setIcon(bug);
			fileWarningLabel.setToolTipText("Invalid file name.");
		}

		if (sheetTextField.getText().trim().length() == 0) {
			sheetWarningLabel.setIcon(bug);
			sheetWarningLabel.setToolTipText("No sheet defined.");
			valid = false;
		}
		else if (!WorkbookUtil.createSafeSheetName(sheetTextField.getText().trim()).equals(sheetTextField.getText().trim())) {
			sheetWarningLabel.setIcon(bug);
			sheetWarningLabel.setToolTipText("Unsafe sheet name.");
			valid = false;
		}
		else {
			sheetWarningLabel.setIcon(null);
			sheetWarningLabel.setToolTipText(null);
		}

		okButton.setEnabled(valid);
	}

}
