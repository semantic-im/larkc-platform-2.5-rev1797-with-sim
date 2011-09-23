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
package eu.larkc.core.endpoint.query;

import java.io.IOException;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;

import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.endpoint.EndpointException;
import eu.larkc.core.endpoint.EndpointInitalizationException;
import eu.larkc.core.endpoint.EndpointShutdownException;
import eu.larkc.core.executor.Executor;
import eu.larkc.shared.SampleWorkflows;

/**
 * This class represents a simple endpoint which takes one parameter, namely
 * <code>query</code>.
 * 
 * @author Norbert Lanzanasto, Christoph Fuchs
 * 
 */
public class QueryEndpointMain extends Endpoint {

	private static Logger logger = LoggerFactory
			.getLogger(QueryEndpointMain.class);

	private static Component component;

	private Server restletServer;

	// private static String endpointURI;

	/**
	 * Custom constructor
	 * 
	 * @param ex
	 *            the executor
	 */
	public QueryEndpointMain(Executor ex) {
		super(ex, "/");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.endpoint.Endpoint#start()
	 */
	@Override
	public void start(int port) throws EndpointException {
		// Create a new Component.
		component = new Component();

		// Create a new server
		restletServer = new Server(Protocol.HTTP, port);

		// Add the new HTTP server
		component.getServers().add(restletServer);

		component.getDefaultHost().attach("/", new QueryEndpoint(this));

		try {
			component.start();
		} catch (Exception e) {
			throw new EndpointInitalizationException(e);
		}
		setPort(port);
		initialized = true;
		logger.info("QueryEndpoint started on port " + port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.endpoint.Endpoint#stop()
	 */
	@Override
	public void stop() throws EndpointShutdownException {
		try {
			component.stop();
		} catch (Exception e) {
			throw new EndpointShutdownException(e);
		}
		initialized = false;
		logger.info("QueryEndpoint on port " + getPort() + " stopped");
	}

	/**
	 * Main method for manual testing.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Just for testing
		new QueryEndpointMain(new Executor(
				SampleWorkflows.getWorkflowDescriptionWithMultipleEndpoints()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange
	 * )
	 */
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		// TODO Auto-generated method stub

	}

}
