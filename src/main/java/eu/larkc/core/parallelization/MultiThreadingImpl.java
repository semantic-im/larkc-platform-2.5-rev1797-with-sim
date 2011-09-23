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
package eu.larkc.core.parallelization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.plugin.Plugin;

/**
 * This class implements simple multi-threading functionalities.
 * 
 * @author Matthias Assel
 * 
 */
public class MultiThreadingImpl implements MultiThreading {

	private static Logger logger = LoggerFactory
			.getLogger(MultiThreadingImpl.class);

	private Collection<Callable<SetOfStatements>> collection;
	private Collection<Future<SetOfStatements>> futures;

	/**
	 * Instantiates a new multi-threading implementation.
	 * 
	 * @return MultiThreadingImpl
	 */
	public synchronized static MultiThreadingImpl getInstance() {
		return new MultiThreadingImpl();
	}

	/**
	 * Invokes the parallel execution for a given set of plug-ins.
	 * 
	 * @throws MultiThreadingException
	 */
	public synchronized void invokeThreadPool(
			HashMap<List<Plugin>, SetOfStatements> pluginList)
			throws MultiThreadingException {

		int nrOfThreads = 1;
		int nrOfProcessors = Runtime.getRuntime().availableProcessors();

		if (pluginList.isEmpty()) {
			throw new MultiThreadingException("Empty plug-in list.");
		}

		// reset local variable;
		if (collection != null)
			collection = null;

		if (futures != null)
			futures = null;

		if (pluginList.size() > 1)
			nrOfThreads = pluginList.size();
		else
			nrOfThreads = nrOfProcessors;

		ExecutorService threadPoolExecutor = Executors
				.newFixedThreadPool(nrOfThreads);

		logger.debug("Created new fixed thread pool with " + nrOfThreads
				+ " threads.");

		collection = new ArrayList<Callable<SetOfStatements>>();
		Callable<SetOfStatements> callable = null;

		if (pluginList.size() == 1) {

			logger
					.debug("One plug-in instance found. Trying to parallelise the execution of this particular plug-in instance.");

			Plugin plugin = null;
			SetOfStatements sos = null;

			for (Map.Entry<List<Plugin>, SetOfStatements> entries : pluginList
					.entrySet()) {

				List<Plugin> plugins = (List<Plugin>) entries.getKey();

				if (plugins.isEmpty()) {
					throw new MultiThreadingException("Empty plug-in list.");
				} else if (plugins.size() > 1) {
					throw new MultiThreadingException(
							"no multiple plug-in branches supported.");
				} else {

					plugin = plugins.get(0);
					sos = entries.getValue();
				}
			}

			if (plugin == null || sos == null) {
				throw new NullPointerException(
						"Plug-ins and SetOfStatements must not be null.");
			}

			for (int i = 1; i <= nrOfThreads; i++) {

				callable = new PluginInstance(plugin, sos);
				collection.add(callable);
			}
		} else {

			logger
					.debug(""
							+ nrOfThreads
							+ " plug-ins found. Trying to parallelise the execution of these plug-in instances.");

			Plugin plugin = null;
			SetOfStatements sos = null;

			for (Map.Entry<List<Plugin>, SetOfStatements> entries : pluginList
					.entrySet()) {

				List<Plugin> plugins = (List<Plugin>) entries.getKey();

				if (plugins.isEmpty()) {
					throw new MultiThreadingException("Empty plug-in list.");
				} else if (plugins.size() > 1) {
					throw new MultiThreadingException(
							"No multiple plug-in branches supported.");
				} else
					plugin = plugins.get(0);

				sos = entries.getValue();

				if (plugin == null || sos == null) {
					throw new NullPointerException(
							"Plug-ins and SetOfStatements must not be null.");
				}

				callable = new PluginInstance(plugin, sos);
				collection.add(callable);
			}
		}

		try {

			logger.debug("Starting parallelisation of " + collection.size()
					+ " plug-ins...");

			futures = threadPoolExecutor.invokeAll(collection);
		} catch (InterruptedException e) {
			throw new MultiThreadingException(e.getMessage());
		}

		logger.debug("Parallelisation finished");

		threadPoolExecutor.shutdown();
	}

	/**
	 * Merges the individual outputs into a synchronized result set.
	 * 
	 * @return SetOfStatements
	 * @throws MultiThreadingException
	 */
	public synchronized SetOfStatements getSynchronizedResults()
			throws MultiThreadingException {

		List<Statement> allStatements = new ArrayList<Statement>();

		if (futures != null) {

			if (futures.isEmpty())
				return null;
			else {

				logger.debug("Merging the different results from "
						+ futures.size() + " plug-ins...");

				for (Future<SetOfStatements> instances : futures) {

					try {

						if (instances != null) {

							CloseableIterator<Statement> itInput = instances
									.get().getStatements();

							while (itInput.hasNext()) {

								Statement statement = itInput.next();

								if (statement != null) {
									allStatements.add(new StatementImpl(
											statement.getSubject(), statement
													.getPredicate(), statement
													.getObject()));
								}
							}
						}
					} catch (InterruptedException e) {
						throw new MultiThreadingException(e.getMessage());
					} catch (ExecutionException e) {
						e.printStackTrace();
						throw new MultiThreadingException(e.getMessage());
					}
				}

				SetOfStatementsImpl output = new SetOfStatementsImpl(
						allStatements);

				return output;
			}
		} else
			return null;
	}

	/**
	 * Internal class to invoke the different plug-ins.
	 */
	class PluginInstance implements Callable<SetOfStatements> {

		private Plugin plugin;
		private SetOfStatements statements;

		/**
		 * Instantiate a new PluginInstance.
		 * 
		 * @param plugin
		 * @param theSetOfStatements
		 */
		public PluginInstance(Plugin plugin, SetOfStatements theSetOfStatements) {
			this.plugin = plugin;
			this.statements = theSetOfStatements;
		}

		/**
		 * Starts the invoke method of the particular plug-in.
		 */
		public SetOfStatements call() {

			if (plugin instanceof Plugin) {

				logger.debug("Invoking Plug-in...");

				return plugin.invoke(statements);
			} else {
				// TODO: Throw unsupported plug-in type exception

				// Simply return input
				return statements;
			}
		}
	}
}
