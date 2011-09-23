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
package eu.larkc.shared;

import java.util.ArrayList;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.pluginManager.remote.GAT.UriList;

/**
 * A collection of generated example workflows for testing purposes.
 * 
 * @author Christoph Fuchs, Norbert Lanzanasto
 * 
 */
public class SampleWorkflows {

	/**
	 * Minimal workflow description.
	 * 
	 * @return a mini-workflow which is as minimal as possible.
	 */
	public static SetOfStatements getMinimalWorkflowDescription() {
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

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);

		// define a path
		BNode bPath = new BNodeImpl("ExamplePath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestTransformer1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);

		return workflowDescription;
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescription() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		// stmtList.add(namespaceStmt1);
		// stmtList.add(namespaceStmt2);
		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);

		return workflowDescription;
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins where two of them have parameters.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithParameters() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		BNode bTestDecider1Parameters = new BNodeImpl("TestDecider1Parameters");
		BNode bTestIdentifier1Parameters = new BNodeImpl(
				"TestIdentifier1Parameters");

		Statement pluginParametersStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestDecider1Parameters);
		Statement pluginParametersStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestIdentifier1Parameters);

		Statement parameterStmt1 = new StatementImpl(bTestDecider1Parameters,
				new URIImpl("http://larkc.eu/schema#parameter1"), new URIImpl(
						"http://larkc.eu/schema#value1"));
		Statement parameterStmt2 = new StatementImpl(bTestDecider1Parameters,
				new URIImpl("http://larkc.eu/schema#parameter2"), new URIImpl(
						"http://larkc.eu/schema#value2"));
		Statement parameterStmt3 = new StatementImpl(bTestDecider1Parameters,
				new URIImpl("http://larkc.eu/schema#parameter3"), new URIImpl(
						"http://larkc.eu/schema#value3"));
		Statement parameterStmt4 = new StatementImpl(
				bTestIdentifier1Parameters, new URIImpl(
						"http://larkc.eu/schema#parameter4"), new URIImpl(
						"http://larkc.eu/schema#value4"));
		Statement parameterStmt5 = new StatementImpl(
				bTestIdentifier1Parameters, new URIImpl(
						"http://larkc.eu/schema#parameter5"), new URIImpl(
						"http://larkc.eu/schema#value5"));

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(pluginParametersStmt1);
		stmtList.add(pluginParametersStmt3);
		stmtList.add(parameterStmt1);
		stmtList.add(parameterStmt2);
		stmtList.add(parameterStmt3);
		stmtList.add(parameterStmt4);
		stmtList.add(parameterStmt5);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);

		return workflowDescription;
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins where two of them have parameters two blank nodes for
	 * plugin parameters each.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithMultipleParametersPerPlugin() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		BNode bTestDecider1Parameters1 = new BNodeImpl(
				"TestDecider1Parameters1");
		BNode bTestIdentifier1Parameters1 = new BNodeImpl(
				"TestIdentifier1Parameters1");

		BNode bTestDecider1Parameters2 = new BNodeImpl(
				"TestDecider1Parameters2");
		BNode bTestIdentifier1Parameters2 = new BNodeImpl(
				"TestIdentifier1Parameters2");

		Statement pluginParametersStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestDecider1Parameters1);
		Statement pluginParametersStmt2 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestIdentifier1Parameters1);

		Statement pluginParametersStmt3 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestDecider1Parameters2);
		Statement pluginParametersStmt4 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestIdentifier1Parameters2);

		Statement parameterStmt1 = new StatementImpl(bTestDecider1Parameters1,
				new URIImpl("http://larkc.eu/schema#parameter1"), new URIImpl(
						"http://larkc.eu/schema#value1"));
		Statement parameterStmt2 = new StatementImpl(bTestDecider1Parameters1,
				new URIImpl("http://larkc.eu/schema#parameter2"), new URIImpl(
						"http://larkc.eu/schema#value2"));
		Statement parameterStmt3 = new StatementImpl(bTestDecider1Parameters2,
				new URIImpl("http://larkc.eu/schema#parameter3"), new URIImpl(
						"http://larkc.eu/schema#value3"));
		Statement parameterStmt4 = new StatementImpl(
				bTestIdentifier1Parameters1, new URIImpl(
						"http://larkc.eu/schema#parameter4"), new URIImpl(
						"http://larkc.eu/schema#value4"));
		Statement parameterStmt5 = new StatementImpl(
				bTestIdentifier1Parameters2, new URIImpl(
						"http://larkc.eu/schema#parameter5"), new URIImpl(
						"http://larkc.eu/schema#value5"));

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(pluginParametersStmt1);
		stmtList.add(pluginParametersStmt2);
		stmtList.add(pluginParametersStmt3);
		stmtList.add(pluginParametersStmt4);
		stmtList.add(parameterStmt1);
		stmtList.add(parameterStmt2);
		stmtList.add(parameterStmt3);
		stmtList.add(parameterStmt4);
		stmtList.add(parameterStmt5);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);

		return workflowDescription;
	}

	/**
	 * Constructs a workflow containing cycles.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getWorkflowDescriptionWithCycles() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);
		Statement connectsToStmt3 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestIdentifier1);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(connectsToStmt3);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);
		return workflowDescription;
	}

	/**
	 * Constructs a workflow with splits and merges in it.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getWorkflowDescriptionWithSplitsAndMerges() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestTransformer2 = new BNodeImpl("TestTransformer2");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestTransformer2,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt4 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer2);
		Statement connectsToStmt3 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);
		Statement connectsToStmt4 = new StatementImpl(bTestTransformer2,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(typeOfStmt4);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(connectsToStmt3);
		stmtList.add(connectsToStmt4);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);
		return workflowDescription;
	}

	/**
	 * Constructs a workflow with a single SPARQL endpoint.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getWorkflowDescriptionWithSPARQLEndpoint() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		URI endpointUrn = new URIImpl("urn:eu.larkc.endpoint.sparql.ep1");
		URI endpointType = new URIImpl("urn:eu.larkc.endpoint.sparql");

		BNode bTestDecider1 = new BNodeImpl("p1");
		BNode bTestTransformer1 = new BNodeImpl("p2");
		BNode bTestIdentifier1 = new BNodeImpl("p3");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		Statement endpointTypeOfStmt4 = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, endpointType);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		Statement endpointLinksPathStmt = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(endpointTypeOfStmt4);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);
		stmtList.add(endpointLinksPathStmt);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);
		return workflowDescription;
	}

	/**
	 * Constructs a workflow with multiple endpoints.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getWorkflowDescriptionWithMultipleEndpoints() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		URI sparqlEndpointUrn = new URIImpl("urn:eu.larkc.endpoint.sparql.ep1");
		URI sparqlEndpointType = new URIImpl("urn:eu.larkc.endpoint.sparql");
		URI testEndpointUrn = new URIImpl("urn:eu.larkc.endpoint.test.ep1");
		URI testEndpointType = new URIImpl("urn:eu.larkc.endpoint.test");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		Statement endpointTypeOfStmt4 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, sparqlEndpointType);
		Statement endpointTypeOfStmt5 = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, testEndpointType);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		Statement endpointLinksPathStmt1 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);
		Statement endpointLinksPathStmt2 = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(endpointTypeOfStmt4);
		stmtList.add(endpointTypeOfStmt5);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);
		stmtList.add(endpointLinksPathStmt1);
		stmtList.add(endpointLinksPathStmt2);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * A very simple example workflow by Spyros with the query embedded. See
	 * larkc-wp5 conversation from July 30.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements spyrosSimpleWorkflowWithEmbeddedQuery() {
		URI fileReaderURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestFileReader");
		URI reasonerURI = new URIImpl("urn:eu.larkc.plugin.reason.TestReasoner");

		BNode bPlugin1 = new BNodeImpl("p1");
		BNode bPlugin2 = new BNodeImpl("p2");

		Statement typeOfStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.RDF_TYPE, fileReaderURI);
		Statement typeOfStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.RDF_TYPE, reasonerURI);

		Statement connectsToStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin2);

		BNode bParameters = new BNodeImpl("param");

		Statement paramStmnt = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.HAS_PARAMETERS, bParameters);

		Statement queryStmnt = new StatementImpl(bParameters,
				WorkflowDescriptionPredicates.PLUGIN_PARAMETER_SPARQLQUERY,
				new LiteralImpl(SampleQueries.WHO_KNOWS_FRANK));

		Statement otherParamStmnt = new StatementImpl(bParameters, new URIImpl(
				"urn:someOtherParameter"), new LiteralImpl("foo"));

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin2);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(connectsToStmt1);
		stmtList.add(paramStmnt);
		stmtList.add(queryStmnt);
		stmtList.add(otherParamStmnt);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * A very simple example workflow by Spyros without the embedded query but
	 * with a SPARQL endpoint. See larkc-wp5 conversation from July 30 and the
	 * one from 26 August 2010.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements spyrosSimpleWorkflowWithEndpoint() {
		URI fileReaderURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestFileReader");
		URI reasonerURI = new URIImpl("urn:eu.larkc.plugin.reason.TestReasoner");

		BNode bPlugin1 = new BNodeImpl("p1");
		BNode bPlugin2 = new BNodeImpl("p2");

		Statement typeOfStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.RDF_TYPE, fileReaderURI);
		Statement typeOfStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.RDF_TYPE, reasonerURI);

		Statement connectsToStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin2);

		// Endpoint
		URI endpointType = new URIImpl("urn:eu.larkc.endpoint.sparql");
		URI endpointUrn = new URIImpl("urn:eu.larkc.endpoint.sparql.ep1");
		Statement endpointTypeOfStmt = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, endpointType);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		Statement endpointLinksPathStmt1 = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin2);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(connectsToStmt1);
		stmtList.add(endpointTypeOfStmt);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);
		stmtList.add(endpointLinksPathStmt1);

		SetOfStatements workflowDescription = new SetOfStatementsImpl(stmtList);
		return workflowDescription;
	}

	/**
	 * Constructs a workflow with one plugin which can be splitted.
	 * 
	 * @return The workflow.
	 */
	public static SetOfStatements getWorkflowDescriptionWithMultiThreading() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		BNode bTestTransformer1Parameters = new BNodeImpl(
				"TestTransformer1Parameters");

		Statement pluginParametersStmt1 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bTestTransformer1Parameters);

		Statement parameterStmt1 = new StatementImpl(
				bTestTransformer1Parameters, new URIImpl(
						"http://larkc.eu/schema#isInputSplittable"),
				new LiteralImpl("true"));

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(pluginParametersStmt1);
		stmtList.add(parameterStmt1);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins with one path.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithPath() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		URI endpointUrn = new URIImpl("urn:eu.larkc.endpoint.test.ep1");
		URI testEndpointType = new URIImpl("urn:eu.larkc.endpoint.test");

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		// define an endpoint
		Statement endpointTypeOfStmt1 = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, testEndpointType);
		Statement endpointLinksPathStmt = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins with one path with two input plugins.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getWorkflowDescriptionWithTwoInputs() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		URI endpointUrn = new URIImpl("urn:eu.larkc.endpoint.test.ep1");
		URI testEndpointType = new URIImpl("urn:eu.larkc.endpoint.test");

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define the inputs
		Statement pathHasInputStmt1 = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);
		Statement pathHasInputStmt2 = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestTransformer1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		// define an endpoint
		Statement endpointTypeOfStmt1 = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, testEndpointType);
		Statement endpointLinksPathStmt = new StatementImpl(endpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt);
		stmtList.add(pathHasInputStmt1);
		stmtList.add(pathHasInputStmt2);
		stmtList.add(pathHasOutputStmt);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins. In addition, one plug-ins is further specified on which
	 * resource to be executed
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithDeploymentDescription() {
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestRemoteIdentifier");

		BNode bTestDecider1 = new BNodeImpl("TestDecider1");
		BNode bTestTransformer1 = new BNodeImpl("TestTransformer1");
		BNode bTestIdentifier1 = new BNodeImpl("TestRemoteIdentifier1");

		Statement typeOfStmt1 = new StatementImpl(bTestDecider1,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);

		Statement connectsToStmt1 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				bTestTransformer1);
		Statement connectsToStmt2 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bTestDecider1);

		BNodeImpl bLocalHost1 = new BNodeImpl("localHost1");
		Statement runsOnStmt1 = new StatementImpl(bTestTransformer1,
				WorkflowDescriptionPredicates.RUNS_ON_URI, bLocalHost1);
		BNodeImpl bLocalHost2 = new BNodeImpl("localHost2");
		Statement runsOnStmt2 = new StatementImpl(bTestIdentifier1,
				WorkflowDescriptionPredicates.RUNS_ON_URI, bLocalHost2);
		Statement deploymentParameterStmt = new StatementImpl(bLocalHost2,
				WorkflowDescriptionPredicates.RDF_TYPE, bTestIdentifier1);

		// define a path
		BNode bPath = new BNodeImpl("TestPath");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				bTestIdentifier1);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(bPath,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				bTestDecider1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(connectsToStmt1);
		stmtList.add(runsOnStmt1);
		stmtList.add(runsOnStmt2);
		stmtList.add(connectsToStmt2);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);
		stmtList.add(deploymentParameterStmt);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Simple GAT resource description. Describes resource properties
	 * 
	 * @return the resource parameters as a set of RDF statements
	 */
	public static SetOfStatements getSimpleGatResourecDescription() {
		URI resource1_URI = new URIImpl("urn:MyLocalHost");
		URI resource1_Type = new URIImpl("urn:GAT");

		BNode bTestResource1 = new BNodeImpl("MyLocalHost");
		BNode bTestResource1Properties = new BNodeImpl(
				"TestResource1Parameters");

		Statement typeOfStmt1 = new StatementImpl(bTestResource1,
				UriList.RESOURCE_ID, resource1_URI);
		Statement typeOfStmt2 = new StatementImpl(bTestResource1,
				UriList.RESOURCE_TYPE, resource1_Type);

		Statement Resource1ParametersStmt1 = new StatementImpl(bTestResource1,
				UriList.RESOURCE_PROPERTIES, bTestResource1Properties);

		Statement parameterStmt1 = new StatementImpl(bTestResource1Properties,
				UriList.RESOURCE_GAT_Broker, new URIImpl("gat:local"));

		Statement parameterStmt2 = new StatementImpl(bTestResource1Properties,
				UriList.RESOURCE_GAT_FileAdaptor, new URIImpl("gat:local"));

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(Resource1ParametersStmt1);
		stmtList.add(parameterStmt1);
		stmtList.add(parameterStmt2);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Workflow description that describes a workflow consisting of five
	 * plug-ins with two paths.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithMultiplePaths() {
		URI sparqlEndpointUrn1 = new URIImpl("urn:eu.larkc.endpoint.test.ep1");
		URI sparqlEndpointUrn2 = new URIImpl("urn:eu.larkc.endpoint.test.ep2");
		URI sparqlEndpointType = new URIImpl("urn:eu.larkc.endpoint.test");
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		// define plugins
		BNode bPlugin1 = new BNodeImpl("Plugin1");
		BNode bPlugin2 = new BNodeImpl("Plugin2");
		BNode bPlugin3 = new BNodeImpl("Plugin3");
		BNode bPlugin4 = new BNodeImpl("Plugin4");
		BNode bPlugin5 = new BNodeImpl("Plugin5");
		BNode bPlugin6 = new BNodeImpl("Plugin6");
		BNode bPlugin7 = new BNodeImpl("Plugin7");

		// define the type of the plugins
		Statement typeOfStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement typeOfStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement typeOfStmt4 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);
		Statement typeOfStmt5 = new StatementImpl(bPlugin5,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt6 = new StatementImpl(bPlugin6,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement typeOfStmt7 = new StatementImpl(bPlugin7,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);

		// define connections between plugins
		Statement connectsToStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin3);
		Statement connectsToStmt2 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin4);
		Statement connectsToStmt3 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin4);
		Statement connectsToStmt4 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin5);
		Statement connectsToStmt5 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin6);
		Statement connectsToStmt6 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin6);
		Statement connectsToStmt7 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin7);
		Statement connectsToStmt8 = new StatementImpl(bPlugin5,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin7);

		// define path 1
		BNode bPath1 = new BNodeImpl("Path1");

		Statement pathHasInputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin1);

		Statement pathHasOutputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin6);

		// define path 2
		BNode bPath2 = new BNodeImpl("Path2");

		Statement pathHasInputStmt2 = new StatementImpl(bPath2,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin2);

		Statement pathHasOutputStmt2 = new StatementImpl(bPath2,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin7);

		// define a sparql endpoint
		Statement endpointTypeOfStmt1 = new StatementImpl(sparqlEndpointUrn1,
				WorkflowDescriptionPredicates.RDF_TYPE, sparqlEndpointType);
		Statement endpointLinksPathStmt1 = new StatementImpl(
				sparqlEndpointUrn1,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath1);

		// define a second sparql endpoint
		Statement endpointTypeOfStmt2 = new StatementImpl(sparqlEndpointUrn2,
				WorkflowDescriptionPredicates.RDF_TYPE, sparqlEndpointType);
		Statement endpointLinksPathStmt2 = new StatementImpl(
				sparqlEndpointUrn2,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath2);

		// define plugin parameters
		BNode bPlugin3Parameters1 = new BNodeImpl("Plugin3Parameters1");
		BNode bPlugin3Parameters2 = new BNodeImpl("Plugin3Parameters2");

		BNode bPlugin4Parameters1 = new BNodeImpl("Plugin4Parameters1");

		Statement pluginParametersStmt1 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bPlugin3Parameters1);
		Statement pluginParametersStmt2 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bPlugin3Parameters2);
		Statement pluginParametersStmt3 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bPlugin4Parameters1);

		Statement parameterStmt1 = new StatementImpl(bPlugin3Parameters1,
				new URIImpl("http://larkc.eu/schema#parameter1"), new URIImpl(
						"http://larkc.eu/schema#value1"));
		Statement parameterStmt2 = new StatementImpl(bPlugin3Parameters1,
				new URIImpl("http://larkc.eu/schema#parameter2"), new URIImpl(
						"http://larkc.eu/schema#value2"));
		Statement parameterStmt3 = new StatementImpl(bPlugin3Parameters2,
				new URIImpl("http://larkc.eu/schema#parameter3"), new URIImpl(
						"http://larkc.eu/schema#value3"));
		Statement parameterStmt4 = new StatementImpl(bPlugin3Parameters2,
				new URIImpl("http://larkc.eu/schema#parameter4"), new URIImpl(
						"http://larkc.eu/schema#value4"));
		Statement parameterStmt5 = new StatementImpl(bPlugin4Parameters1,
				WorkflowDescriptionPredicates.HAS_INPUT_BEHAVIOUR,
				ValueFactoryImpl.getInstance().createLiteral("1"));

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(typeOfStmt4);
		stmtList.add(typeOfStmt5);
		stmtList.add(typeOfStmt6);
		stmtList.add(typeOfStmt7);

		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(connectsToStmt3);
		stmtList.add(connectsToStmt4);
		stmtList.add(connectsToStmt5);
		stmtList.add(connectsToStmt6);
		stmtList.add(connectsToStmt7);
		stmtList.add(connectsToStmt8);

		stmtList.add(pathHasInputStmt1);
		stmtList.add(pathHasOutputStmt1);

		stmtList.add(pathHasInputStmt2);
		stmtList.add(pathHasOutputStmt2);

		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt1);
		stmtList.add(endpointTypeOfStmt2);
		stmtList.add(endpointLinksPathStmt2);

		stmtList.add(pluginParametersStmt1);
		stmtList.add(pluginParametersStmt2);
		stmtList.add(pluginParametersStmt3);
		stmtList.add(parameterStmt1);
		stmtList.add(parameterStmt2);
		stmtList.add(parameterStmt3);
		stmtList.add(parameterStmt4);
		stmtList.add(parameterStmt5);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Workflow description that describes a workflow consisting of three
	 * plug-ins with one plugin that does not have any input.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithPluginWithoutInput() {
		URI sparqlEndpointType = new URIImpl("urn:eu.larkc.endpoint.sparql");
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		// define plugins
		BNode bPlugin1 = new BNodeImpl("Plugin1");
		BNode bPlugin2 = new BNodeImpl("Plugin2");
		BNode bPlugin3 = new BNodeImpl("Plugin3");

		// define the type of the plugins
		Statement typeOfStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement typeOfStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);

		// define connections between plugins
		Statement connectsToStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin2);
		Statement connectsToStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin3);

		// define path 1
		BNode bPath1 = new BNodeImpl("Path1");

		Statement pathHasInputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin2);

		Statement pathHasOutputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin3);

		// define a sparql endpoint
		URI sparqlEndpointUrn = new URIImpl("urn:eu.larkc.endpoint.sparql.ep1");
		Statement endpointTypeOfStmt1 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, sparqlEndpointType);
		Statement endpointLinksPathStmt1 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath1);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);

		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);

		stmtList.add(pathHasInputStmt1);
		stmtList.add(pathHasOutputStmt1);

		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt1);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Workflow description that describes a workflow consisting of five
	 * plug-ins and the last plug-in waits for two of the three inputs.
	 * 
	 * @return the workflow as a set of RDF statements
	 */
	public static SetOfStatements getSimpleWorkflowDescriptionWithWaitForTwoMergeBehavior() {
		URI sparqlEndpointUrn = new URIImpl("urn:eu.larkc.endpoint.sparql.ep1");
		URI sparqlEndpointType = new URIImpl("urn:eu.larkc.endpoint.sparql");
		URI deciderURI = new URIImpl("urn:eu.larkc.plugin.decide.TestDecider");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");

		// define plugins
		BNode bPlugin1 = new BNodeImpl("Plugin1");
		BNode bPlugin2 = new BNodeImpl("Plugin2");
		BNode bPlugin3 = new BNodeImpl("Plugin3");
		BNode bPlugin4 = new BNodeImpl("Plugin4");
		BNode bPlugin5 = new BNodeImpl("Plugin5");

		// define the type of the plugins
		Statement typeOfStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement typeOfStmt2 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt3 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt4 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);
		Statement typeOfStmt5 = new StatementImpl(bPlugin5,
				WorkflowDescriptionPredicates.RDF_TYPE, deciderURI);

		// define connections between plugins
		Statement connectsToStmt1 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin2);
		Statement connectsToStmt2 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin3);
		Statement connectsToStmt3 = new StatementImpl(bPlugin1,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin4);
		Statement connectsToStmt4 = new StatementImpl(bPlugin2,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin5);
		Statement connectsToStmt5 = new StatementImpl(bPlugin3,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin5);
		Statement connectsToStmt6 = new StatementImpl(bPlugin4,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI, bPlugin5);

		// define path 1
		BNode bPath1 = new BNodeImpl("Path1");

		Statement pathHasInputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI, bPlugin1);

		Statement pathHasOutputStmt1 = new StatementImpl(bPath1,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI, bPlugin5);

		// define a sparql endpoint
		Statement endpointTypeOfStmt1 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, sparqlEndpointType);
		Statement endpointLinksPathStmt1 = new StatementImpl(sparqlEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI, bPath1);

		// define plugin parameters
		BNode bPlugin5Parameters = new BNodeImpl("Plugin5Parameters");

		Statement pluginParametersStmt1 = new StatementImpl(bPlugin5,
				WorkflowDescriptionPredicates.HAS_PARAMETERS,
				bPlugin5Parameters);

		Statement parameterStmt1 = new StatementImpl(bPlugin5Parameters,
				WorkflowDescriptionPredicates.HAS_INPUT_BEHAVIOUR,
				ValueFactoryImpl.getInstance().createLiteral("2"));

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(typeOfStmt1);
		stmtList.add(typeOfStmt2);
		stmtList.add(typeOfStmt3);
		stmtList.add(typeOfStmt4);
		stmtList.add(typeOfStmt5);

		stmtList.add(connectsToStmt1);
		stmtList.add(connectsToStmt2);
		stmtList.add(connectsToStmt3);
		stmtList.add(connectsToStmt4);
		stmtList.add(connectsToStmt5);
		stmtList.add(connectsToStmt6);

		stmtList.add(pathHasInputStmt1);
		stmtList.add(pathHasOutputStmt1);

		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt1);

		stmtList.add(pluginParametersStmt1);
		stmtList.add(parameterStmt1);

		return new SetOfStatementsImpl(stmtList);
	}

	/**
	 * Workflow description as presented in in deliverable 5.3.3.
	 * 
	 * @return the minimal workflow as described in D533
	 */
	public static SetOfStatements getMinimalWorkflowFromD533() {
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");

		BNode identifierInstance = new BNodeImpl("plugin1");
		BNode transformerInstance = new BNodeImpl("plugin2");

		Statement pluginType1 = new StatementImpl(identifierInstance,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement pluginType2 = new StatementImpl(transformerInstance,
				WorkflowDescriptionPredicates.RDF_TYPE, transformerURI);

		Statement connectsToStmt1 = new StatementImpl(identifierInstance,
				WorkflowDescriptionPredicates.CONNECTS_TO_URI,
				transformerInstance);

		URI testEndpointUrn = new URIImpl("urn:eu.larkc.endpoint.test.ep1");
		URI testEndpointType = new URIImpl("urn:eu.larkc.endpoint.test");

		// define a path
		BNode pathInstance = new BNodeImpl("path");

		// define an input
		Statement pathHasInputStmt = new StatementImpl(pathInstance,
				WorkflowDescriptionPredicates.PATH_HAS_INPUT_URI,
				identifierInstance);

		// define an output
		Statement pathHasOutputStmt = new StatementImpl(pathInstance,
				WorkflowDescriptionPredicates.PATH_HAS_OUTPUT_URI,
				transformerInstance);

		// define an endpoint
		Statement endpointTypeOfStmt1 = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, testEndpointType);
		Statement endpointLinksPathStmt = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI,
				pathInstance);

		ArrayList<Statement> stmtList = new ArrayList<Statement>();

		stmtList.add(pluginType2);
		stmtList.add(pluginType1);
		stmtList.add(connectsToStmt1);
		stmtList.add(endpointTypeOfStmt1);
		stmtList.add(endpointLinksPathStmt);
		stmtList.add(pathHasInputStmt);
		stmtList.add(pathHasOutputStmt);

		return new SetOfStatementsImpl(stmtList);
	}
}
