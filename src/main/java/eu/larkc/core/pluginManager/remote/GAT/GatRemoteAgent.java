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
package eu.larkc.core.pluginManager.remote.GAT;

// TODO Provide the documentation and tests.

import java.io.IOException;
import java.util.ArrayList;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.plugin.Plugin;

public class GatRemoteAgent {

	private static Plugin plugin;
	private static SetOfStatements input;
	private static SetOfStatements params;

	private static Logger logger = LoggerFactory
			.getLogger(GatRemoteAgent.class);

	/**
	 * Contains the code executed on the remote machine
	 * 
	 * @param stub
	 *            stub for adapting to a concrete plug-in type
	 */
	protected static void runRemoteJob() {

		logger.debug("Hello from remote plug-in");

		// getting the remote machine's hostname
		try {
			java.net.InetAddress localMachine = java.net.InetAddress
					.getLocalHost();
			System.out.println("The plug-in is executed on: "
					+ localMachine.getHostName());
		} catch (java.net.UnknownHostException e) {
			e.printStackTrace();
		}

		logger.debug("The LarKC location is: "
				+ System.getProperty("larkc.location"));

		logger.debug("The input is: " + System.getProperty("larkc.job.input"));

		logger.debug("The output is: " + System.getProperty("larkc.job.output"));

		logger.debug("The executed plug-in is: "
				+ System.getProperty("larkc.plugin"));

		// Instantiating plug-in's class
		try {
			URI plugin_uri = new URIImpl(
					"urn:eu.larkc.plugin.identify.RemotePlugin");

			plugin = (Plugin) Class.forName(System.getProperty("larkc.plugin"))
					.getConstructor(URI.class).newInstance(plugin_uri);

			logger.debug("Remote plug-in " + plugin.getIdentifier()
					+ " instantiated");

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		// Input read and deserialization
		try {
			java.io.FileInputStream inputStream = new java.io.FileInputStream(
					System.getProperty("larkc.job.input"));
			java.io.ObjectInputStream oin = new java.io.ObjectInputStream(
					inputStream);
			try {
				Object readObj = (Object) oin.readObject();
				input = (SetOfStatements) readObj;
				logger.debug("Input loaded");
			} catch (java.io.EOFException e) {
				logger.error("Cannot load parameters");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Params read and deserialization
		try {
			java.io.FileInputStream inputStream = new java.io.FileInputStream(
					System.getProperty("larkc.job.input") + "_param");
			java.io.ObjectInputStream oin = new java.io.ObjectInputStream(
					inputStream);
			try {
				Object readObj = (Object) oin.readObject();
				params = (SetOfStatements) readObj;
				logger.debug("Params loaded");
			} catch (java.io.EOFException e) {
				logger.error("Cannot initialize parameters");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// initialize plugin with workflow parameters
		logger.debug("Intializing parameters...");
		CloseableIterator<Statement> tempstate = params.getStatements();
		if (tempstate.hasNext()) {
			while (tempstate.hasNext())
				logger.debug("Got parameter: {}", tempstate.next());
			tempstate.close();

			plugin.initialise(params);
		} else
			logger.debug("Plug-in initialized without parameters");

		// Plugin invocation
		SetOfStatements output = plugin.invoke(input);

		// Output serialization
		try {
			java.io.FileOutputStream fos = new java.io.FileOutputStream(
					System.getProperty("larkc.job.output"));
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fos);

			oos.writeObject(output);
			logger.debug("Output stored");

			oos.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	protected interface RemoteContainerStub {
		public ArrayList<Object> invoke(ArrayList<Object> params)
				throws Exception;
	}

	public static void main(String[] args) {

		runRemoteJob();

	}
}