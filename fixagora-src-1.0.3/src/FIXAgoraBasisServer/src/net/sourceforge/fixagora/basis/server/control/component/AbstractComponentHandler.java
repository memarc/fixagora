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
package net.sourceforge.fixagora.basis.server.control.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.server.control.spreadsheet.SpreadSheetHandler;
import net.sourceforge.fixagora.basis.shared.model.communication.DataDictionary;
import net.sourceforge.fixagora.basis.shared.model.persistence.AbstractBusinessComponent;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import quickfix.Message;
import quickfix.field.MsgType;

/**
 * The Class AbstractComponentHandler.
 */
public abstract class AbstractComponentHandler {

	/** The basis persistence handler. */
	protected BasisPersistenceHandler basisPersistenceHandler = null;

	/** The data dictionaries. */
	protected List<DataDictionary> dataDictionaries = new ArrayList<DataDictionary>();

	/** The spread sheet handler. */
	protected SpreadSheetHandler spreadSheetHandler = null;

	/** The business component handler. */
	protected BusinessComponentHandler businessComponentHandler = null;

	/** The input components. */
	protected Set<AbstractComponentHandler> inputComponents = new HashSet<AbstractComponentHandler>();

	/** The output components. */
	protected Set<AbstractComponentHandler> outputComponents = new HashSet<AbstractComponentHandler>();

	/** The security dictionary. */
	protected SecurityDictionary securityDictionary = null;

	private final LinkedBlockingQueue<MessageEntry> messageQueue = new LinkedBlockingQueue<MessageEntry>();	
	
	/**
	 * Sets the abstract business component.
	 *
	 * @param abstractBusinessComponent the new abstract business component
	 */
	public abstract void setAbstractBusinessComponent(AbstractBusinessComponent abstractBusinessComponent);

	/**
	 * Gets the abstract business component.
	 *
	 * @return the abstract business component
	 */
	public abstract AbstractBusinessComponent getAbstractBusinessComponent();

	/**
	 * Gets the start level.
	 *
	 * @return the start level
	 */
	public abstract int getStartLevel();

	/**
	 * Process fix message.
	 *
	 * @param messageList the message list
	 */
	protected abstract void processFIXMessage(List<MessageEntry> messageList);
	
	private static Logger log = Logger.getLogger(AbstractComponentHandler.class);
	

	private Thread closingThread = null;

	/**
	 * Start handler.
	 */
	public void startHandler() {

		final Thread thread = new Thread() {

			@Override
			public void run() {
				
				startProcessor();
			}

		};
		thread.start();
		
		final Thread thread2 = new Thread() {

			@Override
			public void run() {

				startChangeOfDayCheck();
			}

		};
		thread2.start();
	}

	
	private void startChangeOfDayCheck() {

		while (closingThread == null) {
			Calendar calendar = Calendar.getInstance();
			int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			try {

				Thread.sleep(60000);
				
				Calendar calendar2 = Calendar.getInstance();
				int newDayOfYear = calendar2.get(Calendar.DAY_OF_YEAR);
				if(dayOfYear!=newDayOfYear)
				{
					dayOfYear = newDayOfYear;
					onChangeOfDay();
				}
			}
			catch (InterruptedException e) {

				log.error("Bug", e);
			}
		}
		
	}
	
	/**
	 * On change of day.
	 */
	protected abstract void onChangeOfDay();
	
	/**
	 * Start processor.
	 */
	protected void startProcessor() {

		log.info("Business Component "+getAbstractBusinessComponent().getName()+" is started");
		
		while (closingThread == null) {
			try {
				MessageEntry message = messageQueue.take();
				List<MessageEntry> messageList = new ArrayList<MessageEntry>();
				synchronized (messageQueue) {
					messageList.add(message);
					messageQueue.drainTo(messageList);
					messageQueue.clear();
				}
				
				try {
					processFIXMessage(messageList);
				}
				catch (Exception e) {
					log.error("Bug", e);
				}

			}
			catch (InterruptedException e) {
				log.error("Bug", e);
			}
		}
		synchronized (closingThread) {
			closingThread.notify();
			closingThread = null;
		}
		
		log.info("Business Component "+getAbstractBusinessComponent().getName()+" is stopped");

	}	

	
	/**
	 * Sets the security dictionary.
	 *
	 * @param securityDictionary the new security dictionary
	 */
	public void setSecurityDictionary(SecurityDictionary securityDictionary) {
	
		this.securityDictionary = securityDictionary;
	}

	/**
	 * Adds the fix messages.
	 *
	 * @param messages the messages
	 */
	public void addFIXMessages(List<MessageEntry> messages) {

		synchronized (messageQueue) {
			messageQueue.addAll(messages);
		}
	}

	/**
	 * Adds the fix message.
	 *
	 * @param message the message
	 */
	public void addFIXMessage(MessageEntry message) {

		synchronized (messageQueue) {
			messageQueue.add(message);
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		closingThread = Thread.currentThread();
		try {
			synchronized (closingThread) {
				Message message = new Message();
				message.getHeader().setString(MsgType.FIELD, "1");
				messageQueue.add(new MessageEntry(getAbstractBusinessComponent(), getAbstractBusinessComponent(), message));
				closingThread.wait();
			}
		}
		catch (InterruptedException e) {
			log.error("Bug", e);
		}
	}

	/**
	 * Adds the input component.
	 *
	 * @param abstractComponentHandler the abstract component handler
	 */
	public synchronized void addInputComponent(AbstractComponentHandler abstractComponentHandler) {
		
		inputComponents.add(abstractComponentHandler);
		
	}

	/**
	 * Adds the output component.
	 *
	 * @param abstractComponentHandler the abstract component handler
	 */
	public synchronized void addOutputComponent(AbstractComponentHandler abstractComponentHandler) {
		
		outputComponents.add(abstractComponentHandler);
		
	}

	/**
	 * Sets the business component handler.
	 *
	 * @param businessComponentHandler the new business component handler
	 */
	public void setBusinessComponentHandler(BusinessComponentHandler businessComponentHandler) {

		this.businessComponentHandler = businessComponentHandler;
	}

	/**
	 * Sets the basis persistence handler.
	 *
	 * @param basisPersistenceHandler the new basis persistence handler
	 */
	public void setBasisPersistenceHandler(BasisPersistenceHandler basisPersistenceHandler) {

		this.basisPersistenceHandler = basisPersistenceHandler;
	}

	/**
	 * Sets the data dictionaries.
	 *
	 * @param dataDictionaries the new data dictionaries
	 */
	public void setDataDictionaries(List<DataDictionary> dataDictionaries) {

		this.dataDictionaries = dataDictionaries;
	}

	/**
	 * Sets the spread sheet handler.
	 *
	 * @param spreadSheetHandler the new spread sheet handler
	 */
	public void setSpreadSheetHandler(SpreadSheetHandler spreadSheetHandler) {

		this.spreadSheetHandler = spreadSheetHandler;
	}

	/**
	 * Start.
	 *
	 * @param channel the channel
	 */
	public abstract void start(Channel channel);

	/**
	 * Stop.
	 *
	 * @param channel the channel
	 */
	public abstract void stop(Channel channel);

	/**
	 * Gets the target comp id if online.
	 *
	 * @param counterparty the counterparty
	 * @return the target comp id if online
	 */
	public String getTargetCompIDIfOnline(Counterparty counterparty) {

		return null;
	}
	
	

}
