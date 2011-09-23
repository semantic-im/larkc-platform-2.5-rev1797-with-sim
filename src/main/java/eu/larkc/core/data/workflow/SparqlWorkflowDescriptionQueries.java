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

/**
 * This class provides methods to retrieve SPARQL queries to extract information
 * out of a workflow description.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class SparqlWorkflowDescriptionQueries {

	/**
	 * Returns the query to get all statements.
	 * 
	 * @return the query
	 */
	public static String getAllStatementsQuery() {
		return "CONSTRUCT { ?subject ?predicate ?object } WHERE { ?subject ?predicate ?object }";
	}

	/**
	 * Returns the query to get the plugin parameter blank nodes.
	 * 
	 * @param pluginID
	 *            the plugin ID
	 * 
	 * @return the query
	 */
	public static String getPluginParameterBlankNodesQuery() {
		return "CONSTRUCT { ?subject <" + WorkflowDescriptionPredicates.HAS_PARAMETERS + "> ?parameters }\n" + 
			   "WHERE {\n" +
			   "	?subject <" + WorkflowDescriptionPredicates.HAS_PARAMETERS + "> ?parameters .\n" +
			   "}";
	}

	/**
	 * Returns the query to get all endpoints.
	 * 
	 * @return the query
	 */
	public static String getEndpointsQuery() {
		return  "CONSTRUCT { ?subject <" + WorkflowDescriptionPredicates.RDF_TYPE + "> ?type .\n" +
				"			 ?subject <" + WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI + "> ?path }\n" +
				"WHERE {\n" +
				"	{ ?subject <" + WorkflowDescriptionPredicates.ENDPOINT_TYPE_URI + "> ?type }\n UNION { ?subject <" + WorkflowDescriptionPredicates.RDF_TYPE + "> ?type } .\n" +
				"	?subject <" + WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI + "> ?path .\n" +
				"}";
	}

	/**
	 * Returns the query to get all inputs.
	 * 
	 * @return the query
	 */
	public static String getInputsQuery() {
		return  "CONSTRUCT { ?plugin <" + WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI + "> ?input }\n" +
				"WHERE {\n" +
				"	?plugin <" + WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI + "> ?input .\n" +
				"}";
	}

	/**
	 * Returns the query to get all outputs.
	 * 
	 * @return the query
	 */
	public static String getOutputs() {
		return  "CONSTRUCT { ?plugin <" + WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI + "> ?output }\n" +
				"WHERE {\n" +
				"	?plugin <" + WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI + "> ?output .\n" +
				"}";
	}

	/**
	 * Returns the query to get all paths.
	 * 
	 * @return the query
	 */
	public static String getPaths() {
		return  "CONSTRUCT { ?plugin1 <" + WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI + "> ?input .\n" +
				"			 ?plugin1 <" + WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI + "> ?output }\n" +
				"WHERE {\n" +
				"	?plugin1 <" + WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI + "> ?input .\n" +
				"	?plugin1 <" + WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI + "> ?output .\n" +
				"}";
	}

	/**
	 * Returns the query to get all plugins.
	 * 
	 * @return the query
	 */
	public static String getPluginsQuery() {
		return  "CONSTRUCT { ?subject <" + WorkflowDescriptionPredicates.RDF_TYPE + "> ?type }\n" +
				"WHERE {\n" +
				"	{ ?subject <" + WorkflowDescriptionPredicates.TYPE_OF_URI + "> ?type }\n UNION { ?subject <" + WorkflowDescriptionPredicates.RDF_TYPE + "> ?type } .\n" +
				"}";
	}

	/**
	 * Returns the query to get all connections between plugins.
	 * 
	 * @return the query
	 */
	public static String getPluginConnectionsQuery() {
		return  "CONSTRUCT { ?subject <" + WorkflowDescriptionPredicates.CONNECTS_TO_URI + "> ?object }\n" +
				"WHERE {\n" +
				"	?subject <" + WorkflowDescriptionPredicates.CONNECTS_TO_URI + "> ?object .\n" +
				"}";
	}
	
	/**
	 * Returns the query to get the deployment options.
	 * 
	 * @return the query
	 */
	public static String getDeploymentOptionQuery() {
		return  "CONSTRUCT { ?subject <" + WorkflowDescriptionPredicates.RUNS_ON_URI + "> ?object }\n" +
				"WHERE {\n" +
				"	?subject <" + WorkflowDescriptionPredicates.RUNS_ON_URI + "> ?object .\n" +
				"}";
	}

}
