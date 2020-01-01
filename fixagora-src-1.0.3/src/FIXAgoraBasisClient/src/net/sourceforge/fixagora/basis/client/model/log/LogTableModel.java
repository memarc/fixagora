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
package net.sourceforge.fixagora.basis.client.model.log;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.fixagora.basis.client.control.BasisClientConnector;
import net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LogEntryResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LoginResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;

import org.apache.log4j.Logger;

/**
 * The Class LogTableModel.
 */
public class LogTableModel extends AbstractTableModel implements BasisClientConnectorListener {

	private static final long serialVersionUID = 1L;

	private final List<LogEntry> filteredMessages = new ArrayList<LogEntry>();

	private final Font font = new Font("Dialog", Font.PLAIN, 12);

	private String longestMessage = new String("Detail");

	private List<LogEntry> messages = null;

	private int minWidth = 0;

	private JTable table = null;

	private Set<Integer> highlighted = new HashSet<Integer>();

	private HashSet<String> keys = new HashSet<String>();
	
	private static Logger log = Logger.getLogger(LogTableModel.class);

	/**
	 * Instantiates a new log table model.
	 *
	 * @param basisClientConnector the basis client connector
	 * @param loginResponse the login response
	 */
	public LogTableModel(final BasisClientConnector basisClientConnector, final LoginResponse loginResponse) {

		super();

		Thread thread = new Thread() {

			public void run() {

				byte[] childBytes = loginResponse.getLogs();

				if (childBytes.length > 0) {
					try {

						ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(childBytes);
						GZIPInputStream gzip = new GZIPInputStream(byteArrayInputStream);
						ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
						@SuppressWarnings("unchecked")
						List<LogEntry> logEntries = (List<LogEntry>) objectInputStream.readObject();

						messages = logEntries;
					}
					catch (Exception e) {
						log.error("Bug", e);
						messages = new ArrayList<LogEntry>();
					}
				}
				else
					messages = new ArrayList<LogEntry>();

				resetFilter();

				basisClientConnector.addBasisClientConnectorListener(LogTableModel.this);
			}

		};

		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();

	}

	private synchronized void addFilteredMessage(final LogEntry message) {

		filteredMessages.add(0, message);

		if (message.getMessageText().length() > longestMessage.length()) {

			longestMessage = message.toString();

			if (table != null)
				setTableWidth();
		}

		updateHighlighted();

	}
	
	private void updateHighlighted()
	{
		if(keys.size()>0)
		{
			StringBuffer stringBuffer = new StringBuffer();
			for(String string: keys)
			{
				stringBuffer.append(string);
				stringBuffer.append(";;;");
			}
			resetHighlight(stringBuffer.toString());
		}
	}

	/**
	 * Adds the message.
	 *
	 * @param message the message
	 */
	public synchronized void addMessage(final LogEntry message) {

		if (messages.size() > 30000)
			messages.remove(messages.size() - 1);

		messages.add(0, message);

		addFilteredMessage(message);
		
		updateHighlighted();
		
		fireTableDataChanged();
	}

	/**
	 * Clear.
	 */
	public void clear() {

		filteredMessages.clear();
		messages.clear();

		fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int column) {

		switch (column) {

			case 0:
				return "Time";

			case 1:
				return "Level";

			case 2:
				return "Component";

			default:
				return "Message";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		return filteredMessages.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {

		final LogEntry message = filteredMessages.get(rowIndex);

		switch (columnIndex) {

			case 0:
				return DateFormat.getTimeInstance().format(message.getLogDate()) + " "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(message.getLogDate());

			case 1:
				switch (message.getLogLevel()) {
					case FATAL:
						return "Fatal";

					case WARNING:
						return "Warning";

					case DEBUG:
						return "Debug";

					case INFO:
						return "Info";

					default:
						return "Unknown";
				}

			case 2:
				return message.getMessageComponent();

			case 3:
				return message.getMessageText();

			default:
				return "";
		}
	}

	/**
	 * Reset filter.
	 */
	public void resetFilter() {

		filteredMessages.clear();

		for (final LogEntry message : messages)
			addFilteredMessage(message);

		Collections.reverse(filteredMessages);

		fireTableDataChanged();
	}

	/**
	 * Sets the min width.
	 *
	 * @param minWidth the new min width
	 */
	public void setMinWidth(final int minWidth) {

		this.minWidth = minWidth;
		setTableWidth();
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final JTable table) {

		this.table = table;
		setTableWidth();
	}

	/**
	 * Sets the table width.
	 */
	public void setTableWidth() {

		final FontMetrics fontMetrics = table.getFontMetrics(font);
		final Rectangle2D r = fontMetrics.getStringBounds(longestMessage, table.getGraphics());

		int width = (int) r.getWidth() + 10;

		if (width < minWidth)
			width = minWidth;

		table.getColumnModel().getColumn(3).setPreferredWidth(width);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onConnected()
	 */
	@Override
	public void onConnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onDisconnected()
	 */
	@Override
	public void onDisconnected() {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#onAbstractResponse(net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse)
	 */
	@Override
	public void onAbstractResponse(AbstractResponse abstractResponse) {

		if (abstractResponse instanceof LogEntryResponse) {

			LogEntryResponse logEntryResponse = (LogEntryResponse) abstractResponse;

			for (LogEntry logEntry : logEntryResponse.getLogEntries())
				addMessage(logEntry);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixagora.basis.client.control.BasisClientConnectorListener#setHighlightKey(java.lang.String)
	 */
	@Override
	public synchronized void setHighlightKey(String key) {
		resetHighlight(key);
	}
	
	private void resetHighlight(String key)
	{

		highlighted.clear();

		if (key != null) {
			keys  = new HashSet<String>();
			String[] keySplit = key.split(";;;");
			for(String key2: keySplit)
				keys.add(key2);

			boolean added = false;
			
			do {
				added = false;
				for (LogEntry logEntry : messages) {
					Set<String> copy = new HashSet<String>(keys);
					for(String keyString: copy)
					if (logEntry.getHighlightKey() != null && logEntry.getHighlightKey().contains(keyString)) {
						String[] keySplit2 = logEntry.getHighlightKey().split(";;;");
						for (String key2 : keySplit2)
							if(keys.add(key2))
								added=true;
						
						highlighted.add(messages.indexOf(logEntry));
					}
				}

			}
			while (added);

		}
		
		fireTableDataChanged();

	}

	/**
	 * Checks if is highlighted.
	 *
	 * @param row the row
	 * @return true, if is highlighted
	 */
	public boolean isHighlighted(int row) {

		return highlighted.contains(row);
	}

	/**
	 * Gets the entry for row.
	 *
	 * @param row the row
	 * @return the entry for row
	 */
	public LogEntry getEntryForRow(int row) {

		return messages.get(row);
	}

}
