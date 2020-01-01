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
package net.sourceforge.fixagora.basis.client.control;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sourceforge.fixagora.basis.client.model.FIXAgoraPlugin;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractRequest;
import net.sourceforge.fixagora.basis.shared.model.communication.AbstractResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.ErrorResponse;
import net.sourceforge.fixagora.basis.shared.model.communication.LogoffRequest;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

/**
 * The Class BasisClientConnector.
 */
public class BasisClientConnector {

	private ExecutorService pool = Executors.newCachedThreadPool();

	private Map<Long, RequestCallable> openRequests = Collections.synchronizedMap(new HashMap<Long, RequestCallable>());

	private Channel channel = null;

	private String host = null;

	private int port = 0;

	private Vector<BasisClientConnectorListener> basisClientConnectorListeners = new Vector<BasisClientConnectorListener>();

	private LinkedBlockingQueue<AbstractResponse> receivedQueue = new LinkedBlockingQueue<AbstractResponse>();

	private FUser user = null;

	private ClientBootstrap bootstrap;

	private static ArrayList<FIXAgoraPlugin> fixAgoraPlugins = null;

	private static Logger log = Logger.getLogger(BasisClientConnector.class);

	/**
	 * Instantiates a new basis client connector.
	 *
	 * @param host the host
	 * @param port the port
	 */
	public BasisClientConnector(String host, int port) {

		if (fixAgoraPlugins == null) {

			fixAgoraPlugins = new ArrayList<FIXAgoraPlugin>();

			ServiceLoader<FIXAgoraPlugin> serviceLoader = ServiceLoader.load(FIXAgoraPlugin.class);

			for (FIXAgoraPlugin fixAgoraPlugin : serviceLoader) {
				fixAgoraPlugins.add(fixAgoraPlugin);

				log.info("FIX Agora Plugin \"" + fixAgoraPlugin.getPluginInfo().getPluginName() + "\" loaded.");
				log.info(fixAgoraPlugin.getPluginInfo().getPluginName() + " - Copyright: " + fixAgoraPlugin.getPluginInfo().getCopyRight());

			}

			Comparator<FIXAgoraPlugin> comparator = new Comparator<FIXAgoraPlugin>() {

				@Override
				public int compare(FIXAgoraPlugin o1, FIXAgoraPlugin o2) {

					return Double.compare(o1.getOrderNumber(), o2.getOrderNumber());
				}

			};

			Collections.sort(fixAgoraPlugins, comparator);

		}

		for (FIXAgoraPlugin fixAgoraPlugin : fixAgoraPlugins) {
			fixAgoraPlugin.setBasisClientConnector(this);
			addBasisClientConnectorListener(fixAgoraPlugin);
		}


		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

		bootstrap = new ClientBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			public ChannelPipeline getPipeline() {

				return Channels.pipeline(new ObjectEncoder(),
		                new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)), new BasisChannelHandler(BasisClientConnector.this));
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		this.host = host;
		this.port = port;

	}

	/**
	 * Adds the basis client connector listener.
	 *
	 * @param basisClientConnectorListener the basis client connector listener
	 */
	public synchronized void addBasisClientConnectorListener(BasisClientConnectorListener basisClientConnectorListener) {

		basisClientConnectorListeners.add(basisClientConnectorListener);
	}

	/**
	 * Gets the f user.
	 *
	 * @return the f user
	 */
	public FUser getFUser() {

		return user;
	}

	/**
	 * Sets the f user.
	 *
	 * @param user the new f user
	 */
	public void setFUser(FUser user) {

		this.user = user;
	}

	/**
	 * Removes the basis client connector listener.
	 *
	 * @param basisClientConnectorListener the basis client connector listener
	 */
	public synchronized void removeBasisClientConnectorListener(BasisClientConnectorListener basisClientConnectorListener) {

		basisClientConnectorListeners.remove(basisClientConnectorListener);
	}

	/**
	 * Connect.
	 */
	public void connect() {
		
		bootstrap.connect(new InetSocketAddress(host, port));

		Thread thread = new Thread() {

			public void run() {

				try {

					sleep(5000);
				}
				catch (InterruptedException interruptedException) {

					log.error("Bug", interruptedException);
				}

				if (channel == null)
					for (BasisClientConnectorListener basisClientConnectorListener : basisClientConnectorListeners)
						basisClientConnectorListener.onDisconnected();
			}
		};

		thread.start();

	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {

		if (channel != null) {

			
			
			ChannelFuture closeFuture = channel.getCloseFuture();
			channel.disconnect();
			closeFuture.awaitUninterruptibly();
			channel = null;
		}
	}

	/**
	 * Message received.
	 *
	 * @param object the object
	 * @throws Exception the exception
	 */
	public void messageReceived(Object object) throws Exception {

		if (object instanceof AbstractResponse) {

			AbstractResponse abstractResponse = (AbstractResponse) object;
			
			RequestCallable requestCallable = openRequests.remove(abstractResponse.getRequestID());

			if (requestCallable != null)
				requestCallable.setAbstractResponse(abstractResponse);
			else
				receivedQueue.put(abstractResponse);
		}
	}

	/**
	 * Channel closed.
	 *
	 * @throws Exception the exception
	 */
	public void channelClosed() throws Exception {

		if (this.channel != null) {

			this.channel = null;

			for (BasisClientConnectorListener basisClientConnectorListener : basisClientConnectorListeners)
				basisClientConnectorListener.onDisconnected();
		}
	}

	/**
	 * Channel connected.
	 *
	 * @param channel the channel
	 * @throws Exception the exception
	 */
	public void channelConnected(Channel channel) throws Exception {

		this.channel = channel;

		Thread thread = new Thread() {

			public void run() {

				List<BasisClientConnectorListener> basisClientConnectorListeners2 = new ArrayList<BasisClientConnectorListener>(basisClientConnectorListeners);

				for (BasisClientConnectorListener basisClientConnectorListener : basisClientConnectorListeners2)
					basisClientConnectorListener.onConnected();
			}
		};

		thread.start();

		Thread thread2 = new Thread() {

			public void run() {

				while (BasisClientConnector.this.channel != null) {

					try {

						AbstractResponse abstractResponse = receivedQueue.take();
						fireAbstractResponse(abstractResponse);
					}
					catch (InterruptedException interruptedException) {

						log.error("Bug", interruptedException);
					}
				}
			}
		};

		thread2.start();

	}

	private synchronized void fireAbstractResponse(AbstractResponse abstractResponse) {

		for (BasisClientConnectorListener basisClientConnectorListener : basisClientConnectorListeners) {
			basisClientConnectorListener.onAbstractResponse(abstractResponse);
		}
	}

	/**
	 * Sets the highlight key.
	 *
	 * @param key the new highlight key
	 */
	public synchronized void setHighlightKey(String key) {

		for (BasisClientConnectorListener basisClientConnectorListener : basisClientConnectorListeners) {
			basisClientConnectorListener.setHighlightKey(key);

		}
	}

	/**
	 * Send request.
	 *
	 * @param abstractRequest the abstract request
	 * @return the abstract response
	 */
	public AbstractResponse sendRequest(AbstractRequest abstractRequest) {

		if (abstractRequest instanceof LogoffRequest) {
			basisClientConnectorListeners.clear();
		}

		abstractRequest.setTimestamp(System.currentTimeMillis());
		
		RequestCallable requestCallable = new RequestCallable(channel, abstractRequest);
		openRequests.put(abstractRequest.getRequestID(), requestCallable);
		Future<AbstractResponse> future = pool.submit(requestCallable);
		AbstractResponse abstractResponse = null;

		try {

			abstractResponse = future.get(60, TimeUnit.SECONDS);
		}
		catch (Exception exception) {

			requestCallable.cancel();
			log.error("Bug", exception);
			abstractResponse = new ErrorResponse(abstractRequest);
		}

		openRequests.remove(abstractRequest.getRequestID());

		return abstractResponse;
	}

	/**
	 * Gets the fix agora plugins.
	 *
	 * @return the fix agora plugins
	 */
	public List<FIXAgoraPlugin> getFixAgoraPlugins() {

		return fixAgoraPlugins;
	}


}
