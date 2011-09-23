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
package eu.larkc.core.management;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import eu.larkc.shared.Resources;
import eu.larkc.shared.SampleRdfWorkflows;

/**
 * This class tests the functionality of the endpoint resource.
 * 
 * @author norlan, Christoph Fuchs
 * 
 */
public class EndpointResourceTest extends MgmtTest {

	/**
	 * Tries to download and parse a RDF workflow description from
	 * LOCAL_WORKFLOW_DESCRIPTION_WITH_ENDPOINT. The workflow which is described
	 * in this document is initialized after parsing. Then an endpoint is asked
	 * from the server.
	 * 
	 * @throws Exception
	 */
	@Test
	public void exampleWorkflowCreateAndEndpointRetrievalTest()
			throws Exception {

		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleRdfWorkflows.getWorkflowWithTestEndpoint());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);
		Representation rdfResponse = rdfResource.post(rep);
		Reference url = rdfResponse.getLocationRef();

		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.test.ep1");
		Representation endpointResponse = endpointResource.get();

		String responseUrl = endpointResponse.getLocationRef().toString();

		// The \\d{4} part means exactly 4 digits since we don't know the exact
		// port number (usually 8182) if we run multiple tests.
		String regexIP = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		String regexPort = "\\d{4}";

		String fullRegex = "http://" + regexIP + ":" + regexPort
				+ "/testendpoint";

		// As long as the (http://IP:port/testendpoint) regular expression
		// matches, we are good
		Assert.assertTrue(Pattern.matches(fullRegex, responseUrl));

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

	/**
	 * Tries to download and parse an RDF workflow description from
	 * LOCAL_WORKFLOW_DESCRIPTION_WITH_ENDPOINT. The workflow which is described
	 * in this document is initialized after parsing. Then an endpoint is asked
	 * from the server by a faulty URL.
	 */
	@Test
	public void exampleWorkflowCreateAndEndpointRetrievalWithoutTypeTest() {

		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleRdfWorkflows.getWorkflowWithTestEndpoint());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);
		Representation rdfResponse = rdfResource.post(rep);
		Reference url = rdfResponse.getLocationRef();

		ClientResource endpointResource = new ClientResource(url + "/endpoint");

		try {
			endpointResource.get();
		} catch (ResourceException e) {
			Assert.assertEquals(500, e.getStatus().getCode());
		} finally {
			// delete workflow
			ClientResource workflowResource = new ClientResource(url);
			workflowResource.delete();
		}
	}

}
