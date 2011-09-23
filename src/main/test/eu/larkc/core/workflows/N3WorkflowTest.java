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
package eu.larkc.core.workflows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.shared.Resources;
import eu.larkc.shared.SampleN3Workflows;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SerializationHelper;

/**
 * Tests various workflows for functionality. Workflows in this class are
 * represented in N3 and thus send to the /workflow/ path of the management
 * interface.
 * 
 * @author Christoph Fuchs
 * 
 */
public class N3WorkflowTest extends LarkcTest {

	private static Logger logger = LoggerFactory
			.getLogger(N3WorkflowTest.class);

	/**
	 * Test that uses a minimal workflow in N3 notation.
	 * 
	 * @throws IOException
	 */
	// @Ignore
	@Test
	public void testMinimalWorkflowFromD533() throws IOException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleN3Workflows.getMinimalN3WorkflowFromD533());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		// Actually send the workflow to the MGMT interface
		Representation response = n3Resource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, n3Resource.getStatus().getCode());

		/*
		 * Step 2) Retrieve the corresponding endpoint
		 */

		// Work with the workflow UUID
		Reference url = response.getLocationRef();
		logger.info("Workflow URL: {}", url);

		// Try to get a SPARQL endpoint of the workflow
		ClientResource sparqlEndpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.sparql.ep1");
		Representation nonexistentEndpointResponse = sparqlEndpointResource
				.get();

		// BUT there is no SPARQL endpoint in this workflow...
		Assert.assertNull(nonexistentEndpointResponse.getLocationRef());

		// Now get the correct endpoint
		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = endpointResource.get();

		// Retrieve the endpoint
		String endpointUrl = endpointResponse.getLocationRef().toString();
		logger.info("Endpoint URL: {}", endpointUrl);

		/*
		 * Step 3) Ask a query and retrieve results
		 */
		ClientResource clientResource = new ClientResource(endpointUrl);

		Form queryForm = new Form();
		queryForm.add("query", SampleQueries.SELECT_ALL_TRIPLES);
		Representation queryRep = queryForm.getWebRepresentation();

		clientResource.post(queryRep);

		// retrieve results
		ClientResource resultsResource = new ClientResource(endpointUrl);
		Representation queryResponse = resultsResource.get();
		String actualString = queryResponse.getText();

		// Construct the expected result
		ByteArrayOutputStream expectedByteStream = new ByteArrayOutputStream();
		Collection<Statement> expectedStatements = new ArrayList<Statement>();
		Literal object = ValueFactoryImpl.getInstance().createLiteral(
				SampleQueries.SELECT_ALL_TRIPLES);
		Statement stmt = new StatementImpl(new BNodeImpl("query"),
				WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY, object);
		expectedStatements.add(stmt);

		SerializationHelper.printSetOfStatements(expectedStatements,
				expectedByteStream, RDFFormat.RDFXML);
		String expectedSerializedStatements = new String(
				expectedByteStream.toByteArray());

		// check the retrieved results
		Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
				queryResponse.getMediaType());
		Assert.assertTrue(expectedSerializedStatements.equals(actualString));

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

	/**
	 * Test that uses a workflow in N3 notation with one path and multiple
	 * inputs.
	 * 
	 * @throws IOException
	 */
	// @Ignore
	@Test
	public void testUsingMultipleInputs() throws IOException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow",
				SampleN3Workflows.getN3WorkflowWithMultipleInputs());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		// Actually send the workflow to the MGMT interface
		Representation response = n3Resource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, n3Resource.getStatus().getCode());

		/*
		 * Step 2) Retrieve the corresponding endpoint
		 */

		// Work with the workflow UUID
		Reference url = response.getLocationRef();
		logger.info("Workflow URL: {}", url);

		// get the endpoint
		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = endpointResource.get();

		// Retrieve the endpoint
		String endpointUrl = endpointResponse.getLocationRef().toString();
		logger.info("Endpoint URL: {}", endpointUrl);

		/*
		 * Step 3) Ask a query and retrieve results
		 */
		ClientResource clientResource = new ClientResource(endpointUrl);

		Form queryForm = new Form();
		queryForm.add("query", SampleQueries.SELECT_ALL_TRIPLES);
		Representation queryRep = queryForm.getWebRepresentation();

		clientResource.post(queryRep);

		// retrieve results
		ClientResource resultsResource = new ClientResource(endpointUrl);
		Representation queryResponse = resultsResource.get();
		String actualString = queryResponse.getText();

		// Construct the expected result
		ByteArrayOutputStream expectedByteStream = new ByteArrayOutputStream();
		Collection<Statement> expectedStatements = new ArrayList<Statement>();
		Literal object = ValueFactoryImpl.getInstance().createLiteral(
				SampleQueries.SELECT_ALL_TRIPLES);
		Statement stmt = new StatementImpl(new BNodeImpl("query"),
				WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY, object);
		expectedStatements.add(stmt);

		SerializationHelper.printSetOfStatements(expectedStatements,
				expectedByteStream, RDFFormat.RDFXML);
		String expectedSerializedStatements = new String(
				expectedByteStream.toByteArray());

		// check the retrieved results
		Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
				queryResponse.getMediaType());
		Assert.assertTrue(expectedSerializedStatements.equals(actualString));

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

	/**
	 * Tests spanning a new workflow within a plugin.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	// @Ignore
	@Test
	public void testSpanningWorkflow() throws IOException, RDFParseException,
			RDFHandlerException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow",
				SampleN3Workflows.getMinimalWorkflowWithInternalWorkflow());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		// Actually send the workflow to the MGMT interface
		Representation response = n3Resource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, n3Resource.getStatus().getCode());

		/*
		 * Step 2) Retrieve the corresponding endpoint
		 */

		// Work with the workflow UUID
		Reference url = response.getLocationRef();
		logger.info("Workflow URL: {}", url);

		// Get the endpoint
		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = endpointResource.get();

		// Retrieve the endpoint
		String endpointUrl = endpointResponse.getLocationRef().toString();
		logger.info("Endpoint URL: {}", endpointUrl);

		/*
		 * Step 3) Ask a query and retrieve results
		 */
		ClientResource clientResource = new ClientResource(endpointUrl);

		Form queryForm = new Form();
		queryForm.add("query", SampleQueries.SELECT_ALL_TRIPLES);
		Representation queryRep = queryForm.getWebRepresentation();

		clientResource.post(queryRep);

		// retrieve results
		ClientResource resultsResource = new ClientResource(endpointUrl);
		Representation queryResponse = resultsResource.get();
		String actualString = queryResponse.getText();

		RDFXMLParser parser = new RDFXMLParser();
		StringReader reader = new StringReader(actualString);
		StatementCollector handler = new StatementCollector();
		parser.setPreserveBNodeIDs(true);
		parser.setRDFHandler(handler);
		parser.parse(reader, "");
		Collection<Statement> actualStatements = handler.getStatements();

		// Construct the expected result
		SetOfStatements statements = new SPARQLQueryImpl(
				SampleQueries.WHO_KNOWS_FRANK).toRDF();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationHelper.printSetOfStatements(statements, byteStream,
				RDFFormat.RDFXML);
		String expectedSerializedStatements = new String(
				byteStream.toByteArray());
		parser = new RDFXMLParser();
		reader = new StringReader(expectedSerializedStatements);
		handler = new StatementCollector();
		parser.setRDFHandler(handler);
		parser.parse(reader, "");
		Collection<Statement> expectedStatements = handler.getStatements();

		// check the retrieved results
		Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
				queryResponse.getMediaType());
		Assert.assertEquals(expectedStatements.size(), actualStatements.size());

		boolean found = false;
		Statement expectedStmt = null;
		for (Statement actualStmt : actualStatements) {
			for (Statement stmt : expectedStatements) {
				expectedStmt = stmt;
				if (actualStmt.getPredicate().stringValue()
						.equals(expectedStmt.getPredicate().stringValue())) {
					Assert.assertEquals(expectedStmt.getObject().stringValue(),
							actualStmt.getObject().stringValue());
					found = true;
					break;
				}
			}
			expectedStatements.remove(expectedStmt);
			if (!found)
				Assert.fail("Statement not present in expected statements!");
			found = false;
		}

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

	/**
	 * Tests not spanning a new workflow within a plugin.
	 * 
	 * @throws IOException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	// @Ignore
	@Test
	public void testNotSpanningWorkflow() throws IOException,
			RDFParseException, RDFHandlerException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow",
				SampleN3Workflows.getMinimalWorkflowWithoutInternalWorkflow());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		// Actually send the workflow to the MGMT interface
		Representation response = n3Resource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, n3Resource.getStatus().getCode());

		/*
		 * Step 2) Retrieve the corresponding endpoint
		 */

		// Work with the workflow UUID
		Reference url = response.getLocationRef();
		logger.info("Workflow URL: {}", url);

		// Get the endpoint
		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = endpointResource.get();

		// Retrieve the endpoint
		String endpointUrl = endpointResponse.getLocationRef().toString();
		logger.info("Endpoint URL: {}", endpointUrl);

		/*
		 * Step 3) Ask a query and retrieve results
		 */
		ClientResource clientResource = new ClientResource(endpointUrl);

		Form queryForm = new Form();
		queryForm.add("query", SampleQueries.SELECT_ALL_TRIPLES);
		Representation queryRep = queryForm.getWebRepresentation();

		clientResource.post(queryRep);

		// retrieve results
		ClientResource resultsResource = new ClientResource(endpointUrl);
		Representation queryResponse = resultsResource.get();
		String actualString = queryResponse.getText();

		StringReader reader = new StringReader(actualString);
		StatementCollector handler = new StatementCollector();
		RDFXMLParser parser = new RDFXMLParser();
		parser.setRDFHandler(handler);
		parser.setPreserveBNodeIDs(true);
		parser.parse(reader, "");
		Collection<Statement> actualStatements = handler.getStatements();

		// construct expected result
		Statement expectedStatement = new StatementImpl(new BNodeImpl("blank"),
				new URIImpl("urn:result"), new LiteralImpl("null"));
		Collection<Statement> expectedStatements = new ArrayList<Statement>();
		expectedStatements.add(expectedStatement);

		// check the retrieved results
		Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
				queryResponse.getMediaType());
		Assert.assertTrue(actualStatements.containsAll(expectedStatements));
		Assert.assertTrue(expectedStatements.containsAll(actualStatements));

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

	/**
	 * Tests a workflow description in N3 with two test endpoints.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMultipleTestEndpoints() throws IOException {
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow",
				SampleN3Workflows.getWorkflowWithMultipleTestEndpoints());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		Representation response = n3Resource.post(rep);

		Assert.assertEquals(201, n3Resource.getStatus().getCode());

		Reference url = response.getLocationRef();
		logger.info("Workflow URL: {}", url);

		// Use the first endpoint
		// ClientResource endpointResource = new ClientResource(url
		// + "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		// Representation endpointResponse = endpointResource.get();
		// String endpointUrl = endpointResponse.getLocationRef().toString();
		// logger.info("Endpoint 1 URL: {}", endpointUrl);
		//
		// ClientResource clientResource = new ClientResource(endpointUrl);
		//
		// Form queryForm = new Form();
		// queryForm.add("query", SampleQueries.WHO_KNOWS_TIM_BERNERS_LEE);
		// Representation queryRep = queryForm.getWebRepresentation();
		//
		// clientResource.post(queryRep);
		//
		// // retrieve results
		// ClientResource resultsResource = new ClientResource(endpointUrl);
		// Representation queryResponse = resultsResource.get();
		// String actualString = queryResponse.getText();
		//
		// // Construct the expected result
		// ByteArrayOutputStream expectedByteStream = new
		// ByteArrayOutputStream();
		// Collection<Statement> expectedStatements = new
		// ArrayList<Statement>();
		// Literal object = ValueFactoryImpl.getInstance().createLiteral(
		// SampleQueries.WHO_KNOWS_TIM_BERNERS_LEE);
		// Statement stmt = new StatementImpl(new BNodeImpl("query"),
		// WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY, object);
		// expectedStatements.add(stmt);
		//
		// SerializationHelper.printSetOfStatements(expectedStatements,
		// expectedByteStream, RDFFormat.RDFXML);
		// String expectedSerializedStatements = new String(
		// expectedByteStream.toByteArray());
		//
		// // check the retrieved results
		// logger.debug("Expected MediaType: \n{}",
		// MediaType.APPLICATION_RDF_XML);
		// logger.debug("Actual MediaType: \n{}", queryResponse.getMediaType());
		// Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
		// queryResponse.getMediaType());
		//
		// logger.debug("Expected Result: \n{}", expectedSerializedStatements);
		// logger.debug("Actual Result: \n{}", actualString);
		// Assert.assertTrue(expectedSerializedStatements.equals(actualString));

		// Use the second endpoint
		ClientResource endpointResource2 = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep2");
		Representation endpointResponse2 = endpointResource2.get();
		String endpointUrl2 = endpointResponse2.getLocationRef().toString();
		logger.info("Endpoint 2 URL: {}", endpointUrl2);

		ClientResource clientResource2 = new ClientResource(endpointUrl2);

		Form queryForm2 = new Form();
		queryForm2.add("query", SampleQueries.WHO_KNOWS_FRANK);
		Representation queryRep2 = queryForm2.getWebRepresentation();

		clientResource2.post(queryRep2);

		// retrieve results
		ClientResource resultsResource2 = new ClientResource(endpointUrl2);
		Representation queryResponse2 = resultsResource2.get();
		String actualString2 = queryResponse2.getText();

		// Construct the expected result
		ByteArrayOutputStream expectedByteStream2 = new ByteArrayOutputStream();
		ArrayList<Statement> expectedStatements2 = new ArrayList<Statement>();
		Literal object2 = ValueFactoryImpl.getInstance().createLiteral(
				SampleQueries.WHO_KNOWS_FRANK);
		StatementImpl stmt2 = new StatementImpl(new BNodeImpl("query"),
				WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY, object2);
		expectedStatements2.add(stmt2);

		SerializationHelper.printSetOfStatements(expectedStatements2,
				expectedByteStream2, RDFFormat.RDFXML);
		String expectedSerializedStatements2 = new String(
				expectedByteStream2.toByteArray());

		// check the retrieved results
		logger.debug("Expected MediaType: \n{}", MediaType.APPLICATION_RDF_XML);
		logger.debug("Actual MediaType: \n{}", queryResponse2.getMediaType());
		Assert.assertEquals(MediaType.APPLICATION_RDF_XML,
				queryResponse2.getMediaType());

		logger.debug("Expected Result: \n{}", expectedSerializedStatements2);
		logger.debug("Actual Result: \n{}", actualString2);
		Assert.assertTrue(expectedSerializedStatements2.equals(actualString2));

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}
}
