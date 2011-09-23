/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.core.endpoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.routing.VirtualHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyc.tool.subl.jrtl.nativeCode.subLisp.SubLThread;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Interface that all endpoints have to implement.
 * 
 * @author Blaz Fortuna, norlan, Luka Bradesko, Christoph Fuchs
 * 
 */
public abstract class Endpoint implements HttpHandler {

	private static Logger logger = LoggerFactory.getLogger(Endpoint.class);
	private int endpointPort;
	private String urlContext;
	private String pathId;

	private eu.larkc.core.executor.Executor executor;

	protected HttpServer server;

	/** Initialized flag */
	protected static boolean initialized = false;

	/***
	 * Specific executor, which creates SublThread instead of normal Thread.
	 * 
	 * @author Luka Bradesko
	 */
	private static class ThreadPerTaskExecutor implements Executor {
		public void execute(Runnable r) {
			SubLThread t = new SubLThread(r, "SPARQLServer");
			t.start();
		}
	}

	/**
	 * Constructs and gives the name to the plug-in.
	 * 
	 * @param ex
	 *            the executor
	 * 
	 * @param _urlContext
	 *            the url context
	 */
	public Endpoint(eu.larkc.core.executor.Executor ex, String _urlContext) {
		urlContext = _urlContext;
		executor = ex;
	}

	/**
	 * Stops the endpoint.
	 * 
	 * @throws EndpointShutdownException
	 *             on endpoint shutdown error (if the server could not be
	 *             stopped, etc.)
	 */
	public void stop() throws EndpointShutdownException {
		server.stop(0);
		initialized = false;
		logger.info(this.getClass().toString() + " endpoint on port "
				+ getPort() + " stopped");
	}

	/**
	 * Starts the endpoint.
	 * 
	 * @param _port
	 * @throws EndpointException
	 *             on endpoint initialization error
	 * 
	 */
	public void start(int _port) throws EndpointException {
		try {
			server = HttpServer.create(new InetSocketAddress(_port), 0);
		} catch (IOException e) {
			throw new EndpointException(e);
		}
		server.createContext(urlContext, this);

		/*
		 * have to use this executor, otherwise SUBL connection doesn't work If
		 * we want to use internal KB, reasoner and plug-in registry, this is
		 * needed.
		 */
		server.setExecutor(new ThreadPerTaskExecutor());
		server.start();

		setPort(_port);
		initialized = true;

		logger.info(this.getClass().toString() + " endpoint started on port "
				+ _port);
	}

	/**
	 * Returns the URI of this endpoint.
	 * 
	 * @return the URI
	 */
	public URI getURI() {
		return new URIImpl("http://" + VirtualHost.getLocalHostAddress() + ":"
				+ getPort() + urlContext);
	}

	/**
	 * Determines if the management interface is initialized.
	 * 
	 * @return true if initialized, false if not
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Getter. Retrieves the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return endpointPort;
	}

	/**
	 * Setter. Sets or updates the port to the passed value.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.endpointPort = port;
	}

	/**
	 * Getter. Retrieves the executor.
	 * 
	 * @return the executor
	 */
	public eu.larkc.core.executor.Executor getExecutor() {
		return executor;
	}

	/**
	 * Setter. Sets or updates the pathId to the passed value.
	 * 
	 * @param pathId
	 *            the pathId to set
	 */
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	/**
	 * Getter. Retrieves the pathId.
	 * 
	 * @return the pathId
	 */
	public String getPathId() {
		return pathId;
	}
}
