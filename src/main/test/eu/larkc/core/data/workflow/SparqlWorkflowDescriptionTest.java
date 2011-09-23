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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.shared.SampleWorkflows;

/**
 * This class tests the functionality provided by the class
 * SparqlWorkflowDescription.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class SparqlWorkflowDescriptionTest extends LarkcTest {

	/**
	 * Retrieves all plugins.
	 */
	@Test
	public void testGetPlugins() {
		try {
			SetOfStatements minimalWorkflowDescription = SampleWorkflows
					.getMinimalWorkflowDescription();

			SparqlWorkflowDescription sparqlWorkflowDescription;
			sparqlWorkflowDescription = new SparqlWorkflowDescription(
					minimalWorkflowDescription);

			SetOfStatements plugins = sparqlWorkflowDescription.getPlugins();

			URI transformerURI = new URIImpl(
					"urn:eu.larkc.plugin.transform.TestTransformer");
			URI identifierURI = new URIImpl(
					"urn:eu.larkc.plugin.identify.TestIdentifier");

			BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
			BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

			Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
					WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
			Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
					WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

			ArrayList<Statement> stmtList = new ArrayList<Statement>();
			stmtList.add(typeOfStmt2);
			stmtList.add(typeOfStmt3);

			Assert.assertEquals(new SetOfStatementsImpl(stmtList), plugins);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all plugin connections.
	 */
	@Test
	public void testGetConnections() {
		try {
			SetOfStatements minimalWorkflowDescription = SampleWorkflows
					.getMinimalWorkflowDescription();

			SparqlWorkflowDescription sparqlWorkflowDescription;
			sparqlWorkflowDescription = new SparqlWorkflowDescription(
					minimalWorkflowDescription);

			SetOfStatements connections = sparqlWorkflowDescription
					.getPluginConnections();

			BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
			BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

			Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
					WorkflowDescriptionPredicates.CONNECTS_TO_URI,
					bTestTransformer1);

			ArrayList<Statement> stmtList = new ArrayList<Statement>();
			stmtList.add(connectsToStmt1);

			Assert.assertEquals(new SetOfStatementsImpl(stmtList), connections);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Tests the construction of the workflow graph.
	 */
	@Test
	public void testGetWorkflowGraph() {
		try {
			SetOfStatements minimalWorkflowDescription = SampleWorkflows
					.getMinimalWorkflowDescription();

			SparqlWorkflowDescription sparqlWorkflowDescription;
			sparqlWorkflowDescription = new SparqlWorkflowDescription(
					minimalWorkflowDescription);

			SimpleDirectedGraph<GraphNode, DefaultEdge> workflowGraph = sparqlWorkflowDescription
					.getWorkflowGraph();

			SimpleDirectedGraph<GraphNode, DefaultEdge> expectedGraph = new SimpleDirectedGraph<GraphNode, DefaultEdge>(
					DefaultEdge.class);
			GraphNode transformerNode = new GraphNode(new URIImpl(
					"urn:eu.larkc.plugin.transform.TestTransformer"),
					"TestTransformer1");
			GraphNode identifierNode = new GraphNode(new URIImpl(
					"urn:eu.larkc.plugin.identify.TestIdentifier"),
					"TestIdentifier1");
			expectedGraph.addVertex(transformerNode);
			expectedGraph.addVertex(identifierNode);
			expectedGraph.addEdge(identifierNode, transformerNode);

			Assert.assertEquals(expectedGraph.vertexSet().toString(),
					workflowGraph.vertexSet().toString());
			Assert.assertEquals(expectedGraph.edgeSet().toString(),
					workflowGraph.edgeSet().toString());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalWorkflowGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all plugin parameters for one specific plugin.
	 */
	@Test
	public void testGetPluginParameters() {
		try {
			SetOfStatements simpleWorkflowDescriptionWithParameters = SampleWorkflows
					.getSimpleWorkflowDescriptionWithParameters();

			SparqlWorkflowDescription sparqlWorkflowDescription;
			sparqlWorkflowDescription = new SparqlWorkflowDescription(
					simpleWorkflowDescriptionWithParameters);

			BNode bDecider = new BNodeImpl("TestDecider1");

			SetOfStatements pluginParameters = sparqlWorkflowDescription
					.getPluginParameters(bDecider.stringValue());

			BNode bTestDecider1Parameters = new BNodeImpl(
					"TestDecider1Parameters");
			Statement parameterStmt1 = new StatementImpl(
					bTestDecider1Parameters, new URIImpl(
							"http://larkc.eu/schema#parameter1"), new URIImpl(
							"http://larkc.eu/schema#value1"));
			Statement parameterStmt2 = new StatementImpl(
					bTestDecider1Parameters, new URIImpl(
							"http://larkc.eu/schema#parameter2"), new URIImpl(
							"http://larkc.eu/schema#value2"));
			Statement parameterStmt3 = new StatementImpl(
					bTestDecider1Parameters, new URIImpl(
							"http://larkc.eu/schema#parameter3"), new URIImpl(
							"http://larkc.eu/schema#value3"));
			ArrayList<Statement> stmtList = new ArrayList<Statement>();
			stmtList.add(parameterStmt1);
			stmtList.add(parameterStmt2);
			stmtList.add(parameterStmt3);
			SetOfStatements expectedResult = new SetOfStatementsImpl(stmtList);

			Assert.assertEquals(expectedResult, pluginParameters);

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all endpoints.
	 */
	@Test
	public void testGetEndpoints() {
		try {
			SetOfStatements workflowDescriptionWithMultipleEndpoints = SampleWorkflows
					.getWorkflowDescriptionWithMultipleEndpoints();

			SparqlWorkflowDescription sparqlWorkflowDescription = new SparqlWorkflowDescription(
					workflowDescriptionWithMultipleEndpoints);

			Map<String, EndpointNode> endpoints = sparqlWorkflowDescription
					.getEndpoints();

			for (Entry<String, EndpointNode> entry : endpoints.entrySet()) {
				if (entry.getKey().equals("urn:eu.larkc.endpoint.test.ep1")) {
					Assert.assertEquals("urn:eu.larkc.endpoint.test", entry
							.getValue().getType());
				} else if (entry.getKey().equals(
						"urn:eu.larkc.endpoint.sparql.ep1")) {
					Assert.assertEquals("urn:eu.larkc.endpoint.sparql", entry
							.getValue().getType());
				}
				Assert.assertEquals("TestPath", entry.getValue().getPath());
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all inputs.
	 */
	@Test
	public void testGetInputs() {
		try {
			SetOfStatements workflowDescriptionWithTwoInputs = SampleWorkflows
					.getWorkflowDescriptionWithTwoInputs();

			SparqlWorkflowDescription sparqlWorkflowDescription = new SparqlWorkflowDescription(
					workflowDescriptionWithTwoInputs);

			Map<String, InputNode> inputs = sparqlWorkflowDescription
					.getInputs();

			Assert.assertEquals(1, inputs.entrySet().size());

			for (Entry<String, InputNode> entry : inputs.entrySet()) {
				if (entry.getKey().equals("TestPath")) {
					List<String> pluginIds = entry.getValue().getPluginIds();
					Assert.assertTrue(pluginIds.contains("TestIdentifier1"));
					Assert.assertTrue(pluginIds.contains("TestTransformer1"));
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all outputs.
	 */
	@Test
	public void testGetOutputs() {
		try {
			SetOfStatements workflowDescriptionWithMultiplePaths = SampleWorkflows
					.getSimpleWorkflowDescriptionWithMultiplePaths();

			SparqlWorkflowDescription sparqlWorkflowDescription = new SparqlWorkflowDescription(
					workflowDescriptionWithMultiplePaths);

			Map<String, OutputNode> outputs = sparqlWorkflowDescription
					.getOutputs();

			Assert.assertEquals(2, outputs.entrySet().size());

			for (Entry<String, OutputNode> entry : outputs.entrySet()) {
				if (entry.getKey().equals("Path1")) {
					Assert.assertTrue(entry.getValue().getPluginId()
							.equals("Plugin6"));
				} else if (entry.getKey().equals("Path2")) {
					Assert.assertTrue(entry.getValue().getPluginId()
							.equals("Plugin7"));
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all paths.
	 */
	@Test
	public void testGetPaths() {
		try {
			SetOfStatements workflowDescriptionWithMultiplePaths = SampleWorkflows
					.getSimpleWorkflowDescriptionWithMultiplePaths();

			SparqlWorkflowDescription sparqlWorkflowDescription = new SparqlWorkflowDescription(
					workflowDescriptionWithMultiplePaths);

			Map<String, PathNode> outputs = sparqlWorkflowDescription
					.getPaths();

			Assert.assertEquals(2, outputs.entrySet().size());

			for (Entry<String, PathNode> entry : outputs.entrySet()) {
				if (entry.getKey().equals("_:Path1")) {
					Assert.assertTrue(entry.getValue().getInputId()
							.equals("_:Plugin1"));
					Assert.assertTrue(entry.getValue().getOutputId()
							.equals("_:Plugin6"));
				} else if (entry.getKey().equals("_:Path2")) {
					Assert.assertTrue(entry.getValue().getInputId()
							.equals("_:Plugin2"));
					Assert.assertTrue(entry.getValue().getOutputId()
							.equals("_:Plugin7"));
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalWorkflowGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the deployment properties for a plugin.
	 */
	@Test
	public void testGetDeploymentProperties() {
		try {
			SetOfStatements workflowDescriptionWithDeploymentDescription = SampleWorkflows
					.getSimpleWorkflowDescriptionWithDeploymentDescription();

			SparqlWorkflowDescription sparqlWorkflowDescription = new SparqlWorkflowDescription(
					workflowDescriptionWithDeploymentDescription);

			BNode bNode = new BNodeImpl("TestRemoteIdentifier1");

			SetOfStatements deploymentProperties = sparqlWorkflowDescription
					.getDeploymentProperties(bNode.stringValue());

			CloseableIterator<Statement> statements = deploymentProperties
					.getStatements();
			Statement stmt;
			Assert.assertEquals(statements.hasNext(), true);
			while (statements.hasNext()) {
				stmt = statements.next();
				Assert.assertEquals(
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", stmt
								.getPredicate().toString());
				Assert.assertEquals("_:TestRemoteIdentifier1", stmt.getObject()
						.toString());
			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
