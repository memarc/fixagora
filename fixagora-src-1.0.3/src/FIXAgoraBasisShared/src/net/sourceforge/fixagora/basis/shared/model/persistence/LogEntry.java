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
package net.sourceforge.fixagora.basis.shared.model.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The Class LogEntry.
 */
@Entity
public class LogEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Enum Level.
	 */
	public enum Level {/** The fatal. */
FATAL,/** The warning. */
WARNING,/** The info. */
INFO,/** The debug. */
DEBUG};

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id = 0;
	
	@Column(nullable = false, unique = false)
	private Date logDate = null;
	
	@Column(nullable = false, unique = false, length=2000)
	private String messageText = null;
	
	@Column(nullable = false, unique = false)
	private String messageComponent = null;
	
	@Column(nullable = true, unique = false)
	private String highlightKey = null;
	
	@Column(nullable = false, unique = false)
	private Level logLevel = Level.DEBUG;
	
	
	/**
	 * Gets the highlight key.
	 *
	 * @return the highlight key
	 */
	public String getHighlightKey() {
	
		return highlightKey;
	}


	
	/**
	 * Sets the highlight key.
	 *
	 * @param highlightKey the new highlight key
	 */
	public void setHighlightKey(String highlightKey) {
	
		this.highlightKey = highlightKey;
	}


	/**
	 * Gets the log date.
	 *
	 * @return the log date
	 */
	public Date getLogDate() {
	
		return logDate;
	}

	
	/**
	 * Sets the log date.
	 *
	 * @param logDate the new log date
	 */
	public void setLogDate(Date logDate) {
	
		this.logDate = logDate;
	}

	
	/**
	 * Gets the message text.
	 *
	 * @return the message text
	 */
	public String getMessageText() {
	
		return messageText;
	}

	
	/**
	 * Sets the message text.
	 *
	 * @param messageText the new message text
	 */
	public void setMessageText(String messageText) {
	
		this.messageText = messageText;
	}

	
	/**
	 * Gets the message component.
	 *
	 * @return the message component
	 */
	public String getMessageComponent() {
	
		return messageComponent;
	}

	
	/**
	 * Sets the message component.
	 *
	 * @param messageComponent the new message component
	 */
	public void setMessageComponent(String messageComponent) {
	
		this.messageComponent = messageComponent;
	}


	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}


	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
	}


	
	/**
	 * Gets the log level.
	 *
	 * @return the log level
	 */
	public Level getLogLevel() {
	
		return logLevel;
	}


	
	/**
	 * Sets the log level.
	 *
	 * @param logLevel the new log level
	 */
	public void setLogLevel(Level logLevel) {
	
		this.logLevel = logLevel;
	}




}
