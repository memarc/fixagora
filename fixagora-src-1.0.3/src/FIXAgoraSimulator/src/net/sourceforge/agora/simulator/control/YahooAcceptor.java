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
package net.sourceforge.agora.simulator.control;

import java.io.FileInputStream;
import java.io.InputStream;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DefaultSessionFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

/**
 * The Class YahooAcceptor.
 */
public class YahooAcceptor implements Application {

	private SocketAcceptor acceptor = null;

	private SessionID sessionID = null;

	private YahooFinanceReader yahooFinanceReader;

	/**
	 * Instantiates a new yahoo acceptor.
	 */
	public YahooAcceptor() {

		super();
		
	}

	/**
	 * Connect.
	 *
	 * @throws Exception the exception
	 */
	public void connect() throws Exception {

		final InputStream inputStream = new FileInputStream("./conf/yahoo.conf");
		final SessionSettings settings = new SessionSettings(inputStream);
		final MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		final LogFactory logFactory = new EmptyLogFactory();
		final MessageFactory messageFactory = new DefaultMessageFactory();


		acceptor = new SocketAcceptor(new DefaultSessionFactory(this, storeFactory, logFactory, messageFactory), settings);

		try {

			acceptor.start();
		}
		catch (final Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {


		if (acceptor != null) {

			acceptor.stop();
			acceptor = null;
			
		}
		
		if(yahooFinanceReader!=null)
			yahooFinanceReader.stop();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromAdmin(final Message msg, final SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void fromApp(final Message message, final SessionID session) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		
		try {
			if(message.getHeader().getString(MsgType.FIELD).equals("V"))
				yahooFinanceReader = new YahooFinanceReader(message, session);
		}
		catch (FieldNotFound e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#onCreate(quickfix.SessionID)
	 */
	@Override
	public void onCreate(final SessionID arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#onLogon(quickfix.SessionID)
	 */
	@Override
	public void onLogon(final SessionID arg0) {

		sessionID = arg0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#onLogout(quickfix.SessionID)
	 */
	@Override
	public void onLogout(final SessionID arg0) {

		sessionID = null;
		yahooFinanceReader.stop();

	}


	/**
	 * Send.
	 *
	 * @param message the message
	 */
	public void send(final Message message) {

		message.getHeader().setString(1128, "9");
		Session.lookupSession(sessionID).send(message);
	}

	/**
	 * Send message.
	 *
	 * @param message the message
	 */
	public void sendMessage(final Message message) {

		if (message != null)
			send(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toAdmin(final Message msg, final SessionID sessionID) {
		
		try {
			
			msg.getHeader().setString(1128, "9");
			
			String messageType = msg.getHeader().getString(MsgType.FIELD);
			if(messageType.equals("4")&&yahooFinanceReader!=null)
				yahooFinanceReader.reset();
		}
		catch (FieldNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
	 */
	@Override
	public void toApp(final Message message, final SessionID session) throws DoNotSend {

	}

}
