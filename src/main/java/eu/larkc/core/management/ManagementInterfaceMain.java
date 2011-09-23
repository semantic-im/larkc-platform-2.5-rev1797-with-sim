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
package eu.larkc.core.management;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.LocalReference;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;

import eu.larkc.core.management.hosts.HostManagementInterface;
import eu.larkc.core.management.registry.RegistryManagementInterface;
import eu.larkc.core.management.workflows.WorkflowsManagementInterface;
import eu.larkc.shared.Resources;

/**
 * <p>
 * Main class to start the management interface server for manual testing.
 * </p>
 * <p>
 * Example: To test RDF retrieval start this class and point your browser to <a
 * href="http://localhost:8182/rdf/retrieve?q=http://www.w3.org/2009/08/skos-
 * reference/skos.rdf"> http://localhost:8182/rdf/retrieve?q=
 * http://www.w3.org/2009/08/skos-reference/skos.rdf</a>
 * </p>
 * 
 * @author Christoph Fuchs
 * 
 */
public class ManagementInterfaceMain {

	private static Logger logger = LoggerFactory
			.getLogger(ManagementInterfaceMain.class);

	/**
	 * The port of the management interface server
	 */
	private static final int SERVER_PORT = 8182;

	/**
	 * Initialized flag
	 */
	private static boolean initialized = false;

	/**
	 * The component for the management interface.
	 */
	private static Component component;

	/**
	 * The server for the management interface
	 */
	private static Server server;

	/**
	 * Main method to start the management interface server.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 *             on server initialization error
	 */
	public static void main(String[] args) throws Exception {
		// Create a new Component.
		component = new Component();

		// Create a new server
		server = new Server(Protocol.HTTP, SERVER_PORT);

		// Add the new HTTP server listening on port 8182.
		component.getServers().add(server);

		server.getContext().getParameters().add("maxTotalConnections", "512");
		server.getContext().getParameters().add("maxThreads", "512");

		// The following application will handle the representation of HTML
		// files
		component.getClients().add(Protocol.FILE);

		// The application will also handle files which are possibly packed
		// inside a JAR file
		component.getClients().add(Protocol.JAR);

		Application application = new Application() {
			@Override
			public Restlet createInboundRoot() {
				try {
					URL systemResource = ClassLoader
							.getSystemResource(Resources.MGMT_HTML_ROOT);
					URI localUri = systemResource.toURI();
					LocalReference lr = new LocalReference(localUri.toString());
					return new Directory(getContext(), lr);
				} catch (IllegalArgumentException iae) {
					// File can't be accessed if packed inside a jar (URI is not
					// hierarchical)
					logger.error("Cannot read management interface properties");
					iae.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};

		// Attach the applications to the component and start it
		component.getDefaultHost().attach(application);

		component.getDefaultHost().attach("/workflow",
				new WorkflowManagementInterface());
		component.getDefaultHost().attach("/registry",
				RegistryManagementInterface.class);
		component.getDefaultHost().attach("/hosts",
				HostManagementInterface.class);
		component.getDefaultHost().attach("/workflows",
				WorkflowsManagementInterface.class);

		// to be backward compatible
		component.getDefaultHost().attach("/rdf/workflows",
				new WorkflowManagementInterface());
		component.getDefaultHost().attach("/n3/workflows",
				new WorkflowManagementInterface());

		component.start();

		logger.info("Management server started on " + SERVER_PORT);
		initialized = true;
	}

	/**
	 * Method to stop the execution of the management interface.
	 * 
	 * @throws Exception
	 *             if the management interface is not initialized
	 */
	public static void stop() throws Exception {
		if (isInitialized()) {
			component.stop();
			initialized = false;
			logger.debug("Management server on port " + SERVER_PORT
					+ " stopped");
		}
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
	 * This method is the entry point for the SubL classes which will invoke
	 * this start method. No SubLObject will be returned in any case.
	 * 
	 * @return returns <code>null</code> in any case
	 */
	public static SubLObject start() {
		try {
			ManagementInterfaceMain.main(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
