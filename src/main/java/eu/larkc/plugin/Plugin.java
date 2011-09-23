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
package eu.larkc.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.util.RDFUtilities;

/**
 * Plugin class. It have implemented the URI and identifier methods. All the
 * plug-ins have to extend this. All the plug-ins should be instantiated using
 * constructor with URI parameter, or using PluginRegistry. Otherwise
 * getIdentifier will be null.
 * 
 * The work of the plug-in is done by the invokeInternal() method.
 * 
 * This class also provides caching functionality. The default behavior is to
 * use caching based on the entire input. If only parts of the input need to be
 * taken into consideration, the getKey() method should be overridden. If
 * caching is to be disabled, the cacheInsert() and cacheLookup() methods are to
 * be overridden.
 * 
 * @author Luka Bradesko
 * 
 */
public abstract class Plugin {

	protected static Logger logger = LoggerFactory.getLogger(Plugin.class);
	// Change this to a cache with expirations
	protected Map<SetOfStatements, SetOfStatements> cache;
	protected int CACHE_MAX_SIZE = 100;

	private URI pluginName = null;

	/** The parameters of this plugin. */
	private SetOfStatements pluginParameters;

	/**
	 * Defines the merge behavior of the plugin.
	 */
	private int inputBehavior;

	/**
	 * Getter. Retrieves the inputBehavior.
	 * 
	 * @return the inputBehavior
	 */
	public int getInputBehavior() {
		return inputBehavior;
	}

	/**
	 * Setter. Sets or updates the inputBehavior to the passed value.
	 * 
	 * @param ib
	 *            the inputBehavior to set
	 */
	public void setInputBehavior(int ib) {
		inputBehavior = ib;
	}

	/**
	 * Constructs and gives the name to the plug-in.
	 * 
	 * @param _pluginName
	 */
	public Plugin(URI _pluginName) {
		pluginName = _pluginName;

		// This is an LRU cache with size CACHE_MAX_SIZE, initial size 1 to save
		// memory
		cache = new LinkedHashMap<SetOfStatements, SetOfStatements>(1, 0.75f,
				true) {
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(
					Map.Entry<SetOfStatements, SetOfStatements> eldest) {
				return size() > CACHE_MAX_SIZE;
			}
		};
	}

	/**
	 * Initialisation of the plug-in parameters.
	 * 
	 * @param parameters
	 *            The parameters for the plugin
	 */
	public final void initialise(SetOfStatements parameters) {

		pluginParameters = parameters;
		logger.debug("{} got plug-in parameters: {}", pluginName,
				pluginParameters);

		inputBehavior = extractInputBehavior(pluginParameters);
		logger.debug("Input behavior set to: {}", this.inputBehavior);

		initialiseInternal(parameters);
	}

	/**
	 * Returns the input behavior of the plugin if it is contained in the
	 * workflow description, otherwise the default value is used (-1). The value
	 * of the input behavior defines the number of inputs the plugin is waiting
	 * for.
	 * 
	 * @param pluginParameters
	 *            The plugin parameters.
	 * @return The number of inputs the plugin is waiting for (-1 means the
	 *         plugin waits for all inputs).
	 */
	private int extractInputBehavior(SetOfStatements pluginParameters) {

		// Return default inputBehavior on NULL input
		if (pluginParameters == null)
			return -1;

		// Extract inputBehavior from plugin parameters
		int ib = -1;
		CloseableIterator<Statement> statements = pluginParameters
				.getStatements();
		Statement stmt;
		String inputBehavior = null;

		while (statements.hasNext()) {
			stmt = statements.next();
			String predicateName = stmt.getPredicate().toString();
			String predicateLocalName = stmt.getPredicate().getLocalName();
			if ((predicateName
					.equals(WorkflowDescriptionPredicates.MERGE_BEHAVIOR) || predicateName
					.equals(WorkflowDescriptionPredicates.HAS_INPUT_BEHAVIOUR
							.toString()))
					|| (((WorkflowDescriptionPredicates.LARKC + predicateLocalName)
							.equals(WorkflowDescriptionPredicates.MERGE_BEHAVIOR) || (WorkflowDescriptionPredicates.LARKC + predicateLocalName)
							.equals(WorkflowDescriptionPredicates.HAS_INPUT_BEHAVIOUR
									.toString())))) {
				logger.debug("Found input behavior: {}", stmt.getObject()
						.stringValue());
				inputBehavior = stmt.getObject().stringValue();
			}
		}

		try {
			ib = Integer.parseInt(inputBehavior);
		} catch (NumberFormatException nfe) {
			ib = -1;
		}

		return ib;
	}

	/**
	 * Override this method to initialise a plugin based on parameters from the
	 * workflow. Note that the initialiseInternal is the only method that may
	 * change the state of a plugin.
	 * 
	 * @param workflowDescription
	 *            the workflow parameters
	 */
	protected abstract void initialiseInternal(
			SetOfStatements workflowDescription);

	/**
	 * This is the method where the work of this plug-in should be done. This
	 * method is not allowed to change the state of the plugin
	 * 
	 * @param input
	 *            the input of the plugin
	 * @return the output of the plugin
	 */
	protected abstract SetOfStatements invokeInternal(SetOfStatements input);

	/**
	 * Called by the platform to allow the plug-in to do any final resource
	 * clean up. The plug-in should not be used after this has been invoked.
	 */
	protected abstract void shutdownInternal();

	/**
	 * Called by the plug-in manager. Will take care of shut down the plugin and
	 * calls shutdownInternal.
	 */
	public void shutdown() {
		logger.debug("Calling shutdownInternal for plugin {}", this.getClass());
		shutdownInternal();
	}

	/**
	 * Returns the URI of the plug-in.
	 * 
	 * @return plug-in name URI
	 */
	public URI getIdentifier() {
		return pluginName;
	}

	/**
	 * Called by the plug-in manager. Will take care of caching and delegate to
	 * invokeInternal
	 * 
	 * @param input
	 * @return Returns the output
	 */
	public final SetOfStatements invoke(SetOfStatements input) {

		SetOfStatements output = cacheLookup(input);
		if (output == null) {
			logger.debug("Cache miss");
			output = invokeInternal(input);
			cacheInsert(input, output);
		} else
			logger.debug("Cache hit");

		if (logger.isDebugEnabled()) // to avoid expensive String operations
			// below when not needed
			logger.debug(String.format(
					"Plugin %s called. \n input: %s \n output: %s", this
							.getClass().getCanonicalName(),
					DataFactory.INSTANCE.printSetOfStatements(input),
					DataFactory.INSTANCE.printSetOfStatements(output)));

		return output;
	}

	/**
	 * Insert calculated result into cache. If you desire no caching
	 * functionality, override this method and make it empty
	 * 
	 * @param res
	 * @param context
	 */
	protected void cacheInsert(SetOfStatements input, SetOfStatements output) {
		cache.put(getInvocationKey(input), output);
	}

	/**
	 * Lookup results in cache. If you desire no caching functionality, override
	 * this method and make it return null
	 * 
	 * @param res
	 * @param context
	 */
	protected SetOfStatements cacheLookup(SetOfStatements input) {
		return cache.get(getInvocationKey(input));
	}

	/**
	 * For caching purposes, each plugin should be able to extract the part of
	 * its input that determines its output. For correctness, (a) it should
	 * always hold that if
	 * getInvocationKey(input1).equals(getInvocationKey(input2)) then
	 * invokeInternal(input1).equals(invokeInternal(input2)). For performance,
	 * (b) it is desirable that it holds that if
	 * invokeInternal(input1).equals(invokeInternal(input2)) then
	 * getInvocationKey(input1).equals(getInvocationKey(input2)). The default
	 * implementation does (a) but not (b). Override this method for better
	 * caching performance.
	 * 
	 * The default implementation makes a best-effort to canonicalize RDF.
	 * 
	 * @param input
	 *            the input from which the key will be extracted
	 * @return the key for the given input
	 */
	protected SetOfStatements getInvocationKey(SetOfStatements input) {
		return new SetOfStatementsImpl(RDFUtilities.canonicalize(input
				.getStatements()));
	}

	/**
	 * Getter. Retrieves the pluginParameters.
	 * 
	 * @return the pluginParameters
	 */
	public SetOfStatements getPluginParameters() {
		return pluginParameters;
	}

	protected URI getNamedGraphFromParameters(SetOfStatements parameters,
			URI name) {

		// TODO This is semantically very sloppy
		CloseableIterator<Statement> it = parameters.getStatements();
		while (it.hasNext()) {
			Statement s = it.next();
			if (s.getPredicate().equals(name)) {
				it.close();
				return (URI) s.getObject();
			}
		}
		return new URIImpl(this.getIdentifier().toString() + UUID.randomUUID());

		// TODO this is very inefficient, but correct
		/*
		 * VariableBinding b = new SPARQLQueryExecutor().executeSelect( new
		 * SPARQLQueryImpl("SELECT ?X WHERE ?Y " + RDFConstants.ARGUMENTNAME +
		 * " " + name + ", ?Y. " + RDFConstants.BINDSTO + " ?X."), parameters);
		 * 
		 * 
		 * 
		 * CloseableIterator<Binding> it = b.iterator(); while (it.hasNext())
		 * for (Value v : it.next().getValues()) return new
		 * URIImpl(v.toString()); it.close();
		 * 
		 * return null;
		 */

	}
}
