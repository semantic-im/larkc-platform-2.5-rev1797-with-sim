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
package eu.larkc.core.data.workflow;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.core.util.RDFConstants;

/**
 * Collection of predicates used in the LarKC workflow description.
 * 
 * @author Christoph Fuchs
 * 
 */
public class WorkflowDescriptionPredicates {

	/*
	 * ==== Namespace definitions ====
	 */

	/** Namespace for LarKC. */
	public static final String LARKC = RDFConstants.LARKC_NAMESPACE;

	/** Namespace for RDF. */
	public static final String RDF = RDFConstants.RDF_NAMESPACE;

	/*
	 * ==== Basic constructs ====
	 */

	/**
	 * This URI represents the URI for the predicate connectsTo, which connects
	 * two plug-in instances.
	 */

	public static final URI CONNECTS_TO_URI = new URIImpl(LARKC + "connectsTo");

	/** This URI represents rdf:type which is used to describe the plug-in type. */
	public static final URI RDF_TYPE = new URIImpl(RDF + "type");

	/*
	 * ==== Plugin parameters ====
	 */
	/** This URI represents that a plugin has specific parameters */
	public static final URI HAS_PARAMETERS = new URIImpl(LARKC + "hasParameter");

	/**
	 * This URI represents the plugin parameter which indicates if multi
	 * threading can be used with this plugin.
	 */
	public static final URI IS_INPUT_SPLITTABLE_URI = new URIImpl(LARKC
			+ "isInputSplittable");

	/**
	 * This URI represents that a plugin has a predefined SPARQL query as a
	 * parameter.
	 */
	public static final URI PLUGIN_PARAMETER_SPARQLQUERY = new URIImpl(LARKC
			+ "sparqlQuery");

	/**
	 * URI to represent that a plug-in has a query (any query, no syntax or type
	 * checking what so ever) as a parameter.
	 */
	public static final URI PLUGIN_PARAMETER_QUERY = new URIImpl(LARKC
			+ "query");

	/*
	 * ==== Remote execution ====
	 */

	/** This URI represents the URI for the predicate pluginRunsOn. */
	public static final URI RUNS_ON_URI = new URIImpl(LARKC + "runsOn");

	/*
	 * ==== Workflow parallelism (split & merge) ====
	 */
	/**
	 * Predicate to determine the input behavior of a plug-in.
	 */
	public static final URI HAS_INPUT_BEHAVIOUR = new URIImpl(LARKC
			+ "hasInputBehaviour");

	/**
	 * Predicate to determine the output behavior of a plug-in. <b>Currently
	 * unused.</b>
	 */
	public static final URI HAS_OUTPUT_BEHAVIOUR = new URIImpl(LARKC
			+ "hasOutputBehaviour");

	/*
	 * ==== Paths (Pipes) ====
	 */
	/**
	 * Sets a plug-in instance as the input of a path.
	 */
	public static final URI PATH_HAS_INPUT_URI = new URIImpl(LARKC + "hasInput");

	/**
	 * Sets a plug-in instance as the output of a path.
	 */
	public static final URI PATH_HAS_OUTPUT_URI = new URIImpl(LARKC
			+ "hasOutput");

	/** An endpoint is linked to a path */
	public static final URI ENDPOINT_LINKS_PATH_URI = new URIImpl(LARKC
			+ "links");

	/*
	 * ==== Deprecated constructs, for backward compatibility ====
	 */
	/**
	 * This URI represents the URI for the predicate pluginType. Deprecated
	 * since in the new workflow description ontology rdf:type is used instead.
	 */
	@Deprecated
	public static final URI TYPE_OF_URI = new URIImpl(LARKC + "pluginType");
	/**
	 * This URI represents the URI for the predicate endpointType. Deprecated
	 * since in the new workflow description ontology rdf:type is used instead.
	 */
	@Deprecated
	public static final URI ENDPOINT_TYPE_URI = new URIImpl(LARKC
			+ "endpointType");
	/**
	 * Predicate to determine the merge behavior (input behavior) of a plug-in.
	 * This predicate is @deprecated but still exists for backward compatibility
	 * reasons. Use {@link WorkflowDescriptionPredicates#HAS_INPUT_BEHAVIOUR}
	 * instead.
	 */
	@Deprecated
	public static final URI MERGE_BEHAVIOR = new URIImpl(LARKC
			+ "mergeBehavior");

	/**
	 * This URI represents that a plugin has specific parameters. @Deprecated.
	 * Use {@link WorkflowDescriptionPredicates#HAS_PARAMETERS} instead.
	 */
	@Deprecated
	public static final URI PLUGIN_PARAMETERS = new URIImpl(LARKC
			+ "pluginParameters");

}
