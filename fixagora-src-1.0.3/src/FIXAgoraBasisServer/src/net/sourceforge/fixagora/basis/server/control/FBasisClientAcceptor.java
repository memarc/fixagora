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
package net.sourceforge.fixagora.basis.server.control;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequests;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;

import org.apache.log4j.Logger;
import org.apache.mina.common.IoHandlerAdapter;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

/**
 * The Class FBasisClientAcceptor.
 */
public class FBasisClientAcceptor extends IoHandlerAdapter {

	private final static Logger log = Logger.getLogger(FBasisClientAcceptor.class.getName());

	private List<AbstractRequests> abstractRequestList = null;

	private Set<Channel> channels = new HashSet<Channel>();

	private BasisPersistenceHandler basisPersistenceHandler;

	private ServerBootstrap bootstrap = null;

	/**
	 * Instantiates a new f basis client acceptor.
	 *
	 * @param abstractRequestList the abstract request list
	 * @param host the host
	 * @param port the port
	 * @throws Exception the exception
	 */
	public FBasisClientAcceptor(List<AbstractRequests> abstractRequestList, String host, int port) throws Exception {

		super();

		this.abstractRequestList = abstractRequestList;		
		
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		
		bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new ObjectEncoder(),
		                new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)), new BasisChannelHandler(FBasisClientAcceptor.this));
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(host, port));
		
	}

	/**
	 * Message received.
	 *
	 * @param channel the channel
	 * @param object the object
	 * @throws Exception the exception
	 */
	public void messageReceived(Channel channel, Object object) throws Exception {

		if (log.isTraceEnabled())
			log.trace("message received " + object.toString());
		
		if (object instanceof AbstractRequest) {

			AbstractRequest abstractRequest = (AbstractRequest) object;
						
			try {

				for (AbstractRequests abstractRequests : abstractRequestList)
					abstractRequest.handleAbstractRequest(abstractRequests, channel);
				
			}
			catch (Exception e) {
				
				log.error("Bug", e);

				channel.write(new ErrorResponse(abstractRequest));
				log.fatal("error " + object.toString());
				if (basisPersistenceHandler != null) {
					LogEntry logEntry = new LogEntry();
					logEntry.setLogDate(new Date());
					logEntry.setLogLevel(Level.FATAL);
					logEntry.setMessageComponent("Basis");
					logEntry.setMessageText(e.getMessage());
					basisPersistenceHandler.writeLogEntry(logEntry);
				}
			}
			
		}
	}

	/**
	 * Channel connected.
	 *
	 * @param channel the channel
	 * @throws Exception the exception
	 */
	public void channelConnected(Channel channel) throws Exception {

		log.info("client connected");

		synchronized (channels) {
			channels.add(channel);
		}
	}

	/**
	 * Channel closed.
	 *
	 * @param channel the channel
	 * @throws Exception the exception
	 */
	public void channelClosed(Channel channel) throws Exception {

		log.info("client disconnected");

		synchronized (channels) {

			channels.remove(channel);

		}

		for (AbstractRequests abstractRequests : abstractRequestList)
			abstractRequests.handleSessionClosed(channel);

	}

	/**
	 * Send.
	 *
	 * @param abstractResponse the abstract response
	 * @param channel the channel
	 */
	public void send(AbstractResponse abstractResponse, Channel channel) {

		if (channel.isOpen())
			channel.write(abstractResponse);
	}

	/**
	 * Send.
	 *
	 * @param abstractResponse the abstract response
	 */
	public void send(AbstractResponse abstractResponse) {

		synchronized (channels) {
			for (Channel channel : channels) {
				if (channel.isConnected())
					channel.write(abstractResponse);
			}
		}

	}

	/**
	 * Sets the basis persistence handler.
	 *
	 * @param basisPersistenceHandler the new basis persistence handler
	 */
	public void setBasisPersistenceHandler(BasisPersistenceHandler basisPersistenceHandler) {

		this.basisPersistenceHandler = basisPersistenceHandler;

	}

}
