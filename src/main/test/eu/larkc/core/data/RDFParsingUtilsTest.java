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
package eu.larkc.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.shared.RDFParsingUtils;
import eu.larkc.shared.SampleN3Workflows;
import eu.larkc.shared.SampleRdfWorkflows;
import eu.larkc.shared.SampleTurtleWorkflows;

/**
 * Tests the functionality of the class RDFParsingUtils.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class RDFParsingUtilsTest {

	private Collection<Statement> expectedXMLStatements;
	private Collection<Statement> expectedN3TurtleStatements;

	/**
	 * Constructor to define the expected statements.
	 */
	public RDFParsingUtilsTest() {
		URI identifierURI = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");
		URI transformerURI = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");

		BNode identifierInstance = new BNodeImpl("plugin1");
		BNode transformerInstance = new BNodeImpl("plugin2");

		Statement pluginType1 = new StatementImpl(identifierInstance,
				WorkflowDescriptionPredicates.TYPE_OF_URI, identifierURI);
		Statement pluginType2 = new StatementImpl(transformerInstance,
				WorkflowDescriptionPredicates.TYPE_OF_URI, transformerURI);

		Statement rdfType1 = new StatementImpl(identifierInstance,
				WorkflowDescriptionPredicates.RDF_TYPE, identifierURI);
		Statement rdfType2 = new StatementImpl(transformerInstance,
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
				WorkflowDescriptionPredicates.ENDPOINT_TYPE_URI,
				testEndpointType);
		Statement endpointRdfTypeStmt1 = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.RDF_TYPE, testEndpointType);
		Statement endpointLinksPathStmt = new StatementImpl(testEndpointUrn,
				WorkflowDescriptionPredicates.ENDPOINT_LINKS_PATH_URI,
				pathInstance);

		expectedXMLStatements = new ArrayList<Statement>();
		expectedXMLStatements.add(pluginType1);
		expectedXMLStatements.add(pluginType2);
		expectedXMLStatements.add(connectsToStmt1);
		expectedXMLStatements.add(endpointTypeOfStmt1);
		expectedXMLStatements.add(endpointLinksPathStmt);
		expectedXMLStatements.add(pathHasInputStmt);
		expectedXMLStatements.add(pathHasOutputStmt);

		expectedN3TurtleStatements = new ArrayList<Statement>();
		expectedN3TurtleStatements.add(rdfType1);
		expectedN3TurtleStatements.add(rdfType2);
		expectedN3TurtleStatements.add(connectsToStmt1);
		expectedN3TurtleStatements.add(endpointRdfTypeStmt1);
		expectedN3TurtleStatements.add(endpointLinksPathStmt);
		expectedN3TurtleStatements.add(pathHasInputStmt);
		expectedN3TurtleStatements.add(pathHasOutputStmt);
	}

	/**
	 * Tests the functionality of parsing a workflow description in RDF/XML
	 * format with prefixes.
	 * 
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	@Test
	public void testParseXMLWithPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedXML = RDFParsingUtils
				.parseXML(SampleRdfWorkflows.getMinimalRDFWorkflowFromD533());

		Assert.assertTrue(expectedXMLStatements.containsAll(parsedXML));
		Assert.assertTrue(parsedXML.containsAll(expectedXMLStatements));
	}

	/**
	 * Tests the functionality of parsing a workflow description in RDF/XML
	 * without prefixes.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	@Test
	public void testParsedXMLWithoutPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedXML = RDFParsingUtils
				.parseXML(SampleRdfWorkflows
						.getMinimalRDFWorkflowFromD533WithoutPrefixes());

		Assert.assertTrue(expectedXMLStatements.containsAll(parsedXML));
		Assert.assertTrue(parsedXML.containsAll(expectedXMLStatements));
	}

	/**
	 * Tests the functionality of parsing a workflow description in N3 format
	 * with prefixes.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	@Test
	public void testParsedN3WithPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedN3 = RDFParsingUtils
				.parseN3(SampleN3Workflows.getMinimalN3WorkflowFromD533());

		Assert.assertTrue(expectedN3TurtleStatements.containsAll(parsedN3));
		Assert.assertTrue(parsedN3.containsAll(expectedN3TurtleStatements));
	}

	/**
	 * Tests the functionality of parsing a workflow description in N3 format
	 * without prefixes.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	@Test
	public void testParsedN3WithoutPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedN3 = RDFParsingUtils
				.parseN3(SampleN3Workflows
						.getMinimalN3WorkflowFromD533WithoutPrefixes());

		Assert.assertTrue(expectedN3TurtleStatements.containsAll(parsedN3));
		Assert.assertTrue(parsedN3.containsAll(expectedN3TurtleStatements));
	}

	/**
	 * Tests the functionality of parsing a workflow description in Turtle
	 * format with prefixes.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	@Test
	public void testParsedTurtleWithPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedTurtle = RDFParsingUtils
				.parseTurtle(SampleTurtleWorkflows
						.getMinimalTurtleWorkflowFromD533());

		Assert.assertTrue(expectedN3TurtleStatements.containsAll(parsedTurtle));
		Assert.assertTrue(parsedTurtle.containsAll(expectedN3TurtleStatements));
	}

	/**
	 * Tests the functionality of parsing a workflow description in Turtle
	 * format without prefixes.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	@Test
	public void testParsedTurtleWithoutPrefix() throws RDFParseException,
			RDFHandlerException, IOException {
		Collection<Statement> parsedTurtle = RDFParsingUtils
				.parseTurtle(SampleTurtleWorkflows
						.getMinimalTurtleWorkflowFromD533WithoutPrefixes());

		Assert.assertTrue(expectedN3TurtleStatements.containsAll(parsedTurtle));
		Assert.assertTrue(parsedTurtle.containsAll(expectedN3TurtleStatements));
	}

}
