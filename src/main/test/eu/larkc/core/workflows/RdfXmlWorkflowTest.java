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
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.shared.Resources;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SampleRdfWorkflows;
import eu.larkc.shared.SerializationHelper;

/**
 * Tests various workflows for functionality. Workflows in this class are
 * represented in RDF/XML and thus send to the /workflow/ path of the management
 * interface (workflows are the same as in N3WorkflowTest.java).
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class RdfXmlWorkflowTest extends LarkcTest {

	private static Logger logger = LoggerFactory
			.getLogger(RdfXmlWorkflowTest.class);

	/**
	 * Test that uses a minimal workflow in RDF/XML notation.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMinimalWorkflowFromD533() throws IOException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleRdfWorkflows.getMinimalRDFWorkflowFromD533());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);

		// Actually send the workflow to the MGMT interface
		Representation response = rdfResource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, rdfResource.getStatus().getCode());

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
		ClientResource testEndpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = testEndpointResource.get();

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
	 * Test that uses a workflow in RDF/XML notation with one path and multiple
	 * inputs.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUsingMultipleInputs() throws IOException {
		/*
		 * Step 1) Send the workflow to the management interface
		 */
		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow",
				SampleRdfWorkflows.getRDFWorkflowWithMultipleInputs());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);

		// Actually send the workflow to the MGMT interface
		Representation response = rdfResource.post(rep);

		// Check if workflow has been created
		Assert.assertEquals(201, rdfResource.getStatus().getCode());

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
}
