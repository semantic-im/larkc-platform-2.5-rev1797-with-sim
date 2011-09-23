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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.Larkc;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.pluginManager.remote.GAT.ResourceDescription;
import eu.larkc.core.pluginregistry.PluginRegistryQueryException;

/**
 * This class provides functions for extracting information out of a workflow
 * description with SPARQL queries.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class SparqlWorkflowDescription {

	private static Logger logger = LoggerFactory
			.getLogger(SparqlWorkflowDescription.class);
	private RepositoryConnection wdConn;

	/**
	 * Constructor which constructs an RDF graph out of the workflow
	 * description.
	 * 
	 * @param workflowDescription
	 * @throws RepositoryException
	 */
	public SparqlWorkflowDescription(SetOfStatements workflowDescription)
			throws RepositoryException {

		Repository wdRep = new SailRepository(new MemoryStore());
		wdRep.initialize();

		wdConn = wdRep.getConnection();

		CloseableIterator<Statement> statements = workflowDescription
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			wdConn.add(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
		}
	}

	/**
	 * This method constructs a JGraphT out of a workflow description.
	 * 
	 * @return a JGraph representing the workflow
	 * @throws IllegalWorkflowGraphException
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	private SimpleDirectedGraph<GraphNode, DefaultEdge> constructJGraphT()
			throws IllegalWorkflowGraphException, RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		Map<String, WorkflowGraphNode> nodes = constructWorkflowGraphNodes();

		SimpleDirectedGraph<GraphNode, DefaultEdge> graph = new SimpleDirectedGraph<GraphNode, DefaultEdge>(
				DefaultEdge.class);
		GraphNode node;
		Map<String, GraphNode> valueGraphNodePairs = new HashMap<String, GraphNode>();
		Map<WorkflowGraphNode, GraphNode> workflowGraphNodeGraphNodePairs = new HashMap<WorkflowGraphNode, GraphNode>();

		// construct nodes in graph
		for (Map.Entry<String, WorkflowGraphNode> entry : nodes.entrySet()) {
			node = new GraphNode(entry.getValue().getUri(), entry.getKey());
			graph.addVertex(node);
			valueGraphNodePairs.put(entry.getKey(), node);
			workflowGraphNodeGraphNodePairs.put(entry.getValue(), node);
		}

		// construct edges
		for (Map.Entry<String, WorkflowGraphNode> entry : nodes.entrySet()) {
			for (WorkflowGraphNode next : entry.getValue().getNext()) {
				if (graph.addEdge(valueGraphNodePairs.get(entry.getKey()),
						workflowGraphNodeGraphNodePairs.get(next)) == null) {
					logger.debug("DUPLICATE EDGE - not added");
				}
			}
		}

		if (checkGraphForCycles(graph)) {
			throw new IllegalWorkflowGraphException(
					"Cylces exist in the graph!");
		}

		return graph;
	}

	/**
	 * This method checks a graph for cycles.
	 * 
	 * @param graph
	 *            The graph to be checked
	 * @return Returns true if there are cycles in the graph, false otherwise
	 */
	private boolean checkGraphForCycles(
			SimpleDirectedGraph<GraphNode, DefaultEdge> graph) {
		boolean hasCycles = false;
		CycleDetector<GraphNode, DefaultEdge> cycleDetector = new CycleDetector<GraphNode, DefaultEdge>(
				graph);

		hasCycles = cycleDetector.detectCycles();
		logger.debug("Cycles in the graph: {}", Boolean.toString(hasCycles));
		return hasCycles;
	}

	/**
	 * This method returns all plugins defined in the workflow description.
	 * 
	 * @return the plugins
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public SetOfStatements getPlugins() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		SetOfStatements statements = executeQuery(SparqlWorkflowDescriptionQueries
				.getPluginsQuery());

		return statements;
	}

	/**
	 * This method returns all connections between plugins.
	 * 
	 * @return the connections
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public SetOfStatements getPluginConnections() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		return executeQuery(SparqlWorkflowDescriptionQueries
				.getPluginConnectionsQuery());
	}

	/**
	 * This method constructs a map where the values of RDF are mapped to
	 * WorkflowGraphNodes.
	 * 
	 * @return the map containing value workflow graph node pairs
	 * @throws IllegalWorkflowGraphException
	 *             if no nodes are found
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	private Map<String, WorkflowGraphNode> constructWorkflowGraphNodes()
			throws IllegalWorkflowGraphException, RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		Map<String, WorkflowGraphNode> nodes = new HashMap<String, WorkflowGraphNode>();

		SetOfStatements pluginTypeStatements = getPlugins();
		CloseableIterator<Statement> statements = pluginTypeStatements
				.getStatements();
		Statement stmt;

		while (statements.hasNext()) {
			stmt = statements.next();
			if (isPlugin(stmt.getObject())) {
				String stringValue = stmt.getSubject().stringValue();
				if (!nodes.containsKey(stringValue)) {
					nodes.put(stringValue, new WorkflowGraphNode());
					nodes.get(stringValue).setUri((URI) stmt.getObject());
				}
			}
		}

		SetOfStatements pluginConnectStatements = getPluginConnections();
		statements = pluginConnectStatements.getStatements();

		while (statements.hasNext()) {
			stmt = statements.next();
			String stringValue = stmt.getSubject().stringValue();
			if (!nodes.containsKey(stringValue)) {
				throw new IllegalWorkflowGraphException(
						"Plugin without type definition found!");
			}

			String objectValue = stmt.getObject().stringValue();
			if (!nodes.containsKey(objectValue)) {
				throw new IllegalWorkflowGraphException(
						"Plugin without type definition found!");
			}

			nodes.get(stringValue).addNext(nodes.get(objectValue));
			logger.debug("Added connection from {} to {}.", stringValue,
					objectValue);
		}

		if (nodes.isEmpty())
			throw new IllegalWorkflowGraphException("No nodes are found!");

		return nodes;
	}

	/**
	 * Checks if the passed Value object is a LarKC plug-in
	 * 
	 * @param object
	 *            the object of (subject, predicate, object) triple
	 * @return true if the object is a LarKC plug-in, false otherwise
	 */
	private boolean isPlugin(Value object) {
		boolean isPlugin = false;
		try {
			if ((object instanceof URI)) {
				isPlugin = Larkc.getPluginRegistry()
						.isLarkCPlugin((URI) object);
			}
		} catch (PluginRegistryQueryException e) {
			isPlugin = false;
			e.printStackTrace();
		}// object.stringValue().contains("larkc.plugin");
		logger.debug("Checking if {} is a plug-in... {}", object.stringValue(),
				isPlugin);
		return isPlugin;
	}

	/**
	 * Method to execute a given query.
	 * 
	 * @param query
	 *            the query
	 * @return the query result as a SetOfStatements
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 */
	private SetOfStatements executeQuery(String query)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		GraphQuery gq = wdConn.prepareGraphQuery(QueryLanguage.SPARQL, query);
		GraphQueryResult graphQueryResult = gq.evaluate();

		ArrayList<Statement> stmtList = new ArrayList<Statement>();
		while (graphQueryResult.hasNext()) {
			stmtList.add(graphQueryResult.next());
		}

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Returns all triples contained in the local repository.
	 * 
	 * @return all triples
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 * @throws QueryEvaluationException
	 */
	private SetOfStatements getAllStatements() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		return executeQuery(SparqlWorkflowDescriptionQueries
				.getAllStatementsQuery());
	}

	/**
	 * Returns the plugin parameters for one plugin
	 * 
	 * @param pluginID
	 *            the plugin ID
	 * @return the plugin parameters
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public SetOfStatements getPluginParameters(String pluginID)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String query = SparqlWorkflowDescriptionQueries
				.getPluginParameterBlankNodesQuery();
		String parameterNode = null;

		SetOfStatements pluginParameterStatements = executeQuery(query);
		CloseableIterator<Statement> statements = pluginParameterStatements
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			if (stmt.getSubject().stringValue().equals(pluginID)) {
				parameterNode = stmt.getObject().toString();
				logger.debug("Found plugin parameter node for plugin {}: {}",
						pluginID, parameterNode);
				break;
			}
		}

		ArrayList<Statement> stmtList = new ArrayList<Statement>();
		if (parameterNode != null && !parameterNode.isEmpty()) {
			CloseableIterator<Statement> allStatements = getAllStatements()
					.getStatements();
			while (allStatements.hasNext()) {
				stmt = allStatements.next();
				if (stmt.getSubject().toString().equals(parameterNode)) {
					stmtList.add(stmt);
					logger.debug("Found plugin parameter {}", stmt);
				}
			}
		}

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Returns the deployment properties for one plugin
	 * 
	 * @param pluginID
	 *            the plugin ID
	 * @return the deployment properties
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public SetOfStatements getDeploymentProperties(String pluginID)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		SetOfStatements hostDescription = null;
		String deploymentOption = null;

		// Step 1. Obtaining the statement that describes the deployment option
		String deploymentOptionQuery = SparqlWorkflowDescriptionQueries
				.getDeploymentOptionQuery();
		SetOfStatements deploymentOptionStatement = executeQuery(deploymentOptionQuery);
		CloseableIterator<Statement> statements = deploymentOptionStatement
				.getStatements();
		Statement stmt;
		String subject;
		while (statements.hasNext()) {
			stmt = statements.next();
			subject = stmt.getSubject().stringValue();
			if (subject.equals(pluginID)) {
				String value = stmt.getObject().toString();
				logger.debug("Found deployment option for plugin {}: {}",
						pluginID, value);
				deploymentOption = value;
				break;
			}
		}

		// Step 2. Checking whether the deployment option is a file or a blank
		// node
		if (deploymentOption != null) {
			if (deploymentOption.contains("file://")) {
				// This is a file
				hostDescription = ResourceDescription
						.getHostPropertiesFromFile(deploymentOption);
			} else {
				// This is a blank node; obtaining the parameters from the
				// workflow description
				ArrayList<Statement> stmtList = new ArrayList<Statement>();
				CloseableIterator<Statement> allStatements = getAllStatements()
						.getStatements();
				while (allStatements.hasNext()) {
					stmt = allStatements.next();
					if (stmt.getSubject().toString().equals(deploymentOption)) {
						stmtList.add(stmt);
						logger.debug("Found deployment parameter {}", stmt);
					}
				}

				hostDescription = new SetOfStatementsImpl(stmtList);
			}
		}

		return hostDescription;
	}

	/**
	 * This method returns all endpoints that are specified in the workflow
	 * description.
	 * 
	 * @return Returns all endpoints that are specified in this workflow
	 *         description
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public Map<String, EndpointNode> getEndpoints() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		Map<String, EndpointNode> endpoints = new HashMap<String, EndpointNode>();

		SetOfStatements endpointStatements = executeQuery(SparqlWorkflowDescriptionQueries
				.getEndpointsQuery());
		CloseableIterator<Statement> statements = endpointStatements
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.RDF_TYPE)) {
				if (!endpoints.containsKey(stmt.getSubject().stringValue())) {
					endpoints.put(stmt.getSubject().stringValue(),
							new EndpointNode());
				}
				endpoints.get(stmt.getSubject().stringValue()).setType(
						stmt.getObject().stringValue());
			} else if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI)) {
				if (!endpoints.containsKey(stmt.getSubject().stringValue())) {
					endpoints.put(stmt.getSubject().stringValue(),
							new EndpointNode());
				}
				endpoints.get(stmt.getSubject().stringValue()).setPath(
						stmt.getObject().stringValue());
			}
		}

		return endpoints;
	}

	/**
	 * Extracts all inputs of the workflow description.
	 * 
	 * @return A map with all inputs specified by the workflow description.
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public Map<String, InputNode> getInputs() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		Map<String, InputNode> inputs = new HashMap<String, InputNode>();

		SetOfStatements inputStatements = executeQuery(SparqlWorkflowDescriptionQueries
				.getInputsQuery());
		CloseableIterator<Statement> statements = inputStatements
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI)) {
				String subjectValue = stmt.getSubject().stringValue();
				if (!inputs.containsKey(subjectValue)) {
					inputs.put(subjectValue, new InputNode());
				}
				inputs.get(subjectValue).addPluginId(
						stmt.getObject().stringValue());
			}
		}

		return inputs;
	}

	/**
	 * Extracts all outputs of the workflow description.
	 * 
	 * @return A map with all outputs specified by the workflow description.
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public Map<String, OutputNode> getOutputs() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		Map<String, OutputNode> outputs = new HashMap<String, OutputNode>();

		SetOfStatements outputStatements = executeQuery(SparqlWorkflowDescriptionQueries
				.getOutputs());
		CloseableIterator<Statement> statements = outputStatements
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI)) {
				String subjectValue = stmt.getSubject().stringValue();
				if (!outputs.containsKey(subjectValue)) {
					outputs.put(subjectValue, new OutputNode());
				}
				outputs.get(subjectValue).setPluginId(
						stmt.getObject().stringValue());
			}
		}

		return outputs;
	}

	/**
	 * Extracts all paths out of a workflow description and returns a map
	 * containing the IDs for the path and the corresponding path node object.
	 * 
	 * @return Map with IDs and path node objects.
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 * @throws IllegalWorkflowGraphException
	 */
	public Map<String, PathNode> getPaths() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException,
			IllegalWorkflowGraphException {
		Map<String, PathNode> paths = new HashMap<String, PathNode>();

		SetOfStatements pathStatements = executeQuery(SparqlWorkflowDescriptionQueries
				.getPaths());
		CloseableIterator<Statement> statements = pathStatements
				.getStatements();
		Statement stmt;
		while (statements.hasNext()) {
			stmt = statements.next();
			if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI)) {
				String subjectValue = stmt.getSubject().stringValue();
				if (!paths.containsKey(subjectValue)) {
					paths.put(subjectValue, new PathNode());
				}
				paths.get(subjectValue).addInputId(
						stmt.getObject().stringValue());
			} else if (stmt.getPredicate().equals(
					WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI)) {
				String subjectValue = stmt.getSubject().stringValue();
				if (!paths.containsKey(subjectValue)) {
					paths.put(subjectValue, new PathNode());
				}
				if (paths.get(subjectValue).getOutputId() == null) {
					paths.get(subjectValue).setOutputId(
							stmt.getObject().stringValue());
				} else {
					throw new IllegalWorkflowGraphException(
							"Multiple outputs are connected to one path!");
				}
			}
		}

		return paths;
	}

	/**
	 * Returns the workflow as a graph.
	 * 
	 * @return the graph
	 * @throws IllegalWorkflowGraphException
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException
	 * @throws RepositoryException
	 */
	public SimpleDirectedGraph<GraphNode, DefaultEdge> getWorkflowGraph()
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException, IllegalWorkflowGraphException {
		return constructJGraphT();
	}

}
