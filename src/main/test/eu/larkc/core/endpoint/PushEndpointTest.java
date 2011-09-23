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
package eu.larkc.core.endpoint;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.management.MgmtTest;
import eu.larkc.shared.Resources;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SampleRdfWorkflows;

/**
 * This class tests the PushEndpoint.
 * 
 * @author Norbert Lanzanasto, Christoph Fuchs
 * 
 */
public class PushEndpointTest extends MgmtTest {

	private static Logger logger = LoggerFactory
			.getLogger(PushEndpointTest.class);

	/**
	 * Tries to send a query to the PushEndpoint running on
	 * http://localhost:8183/pushendpoint.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sendQueryToPushEndpoint() throws Exception {

		// construct workflow
		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleRdfWorkflows.getWorkflowWithPushEndpoint());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);
		Representation rdfResponse = rdfResource.post(rep);
		Reference url = rdfResponse.getLocationRef();

		// retrieve endpoint
		ClientResource endpointResource = new ClientResource(url
				+ "/endpoint?urn=urn:eu.larkc.endpoint.push.ep1");
		Representation endpointResponse = endpointResource.get();

		Reference pushEndpointUrl = endpointResponse.getLocationRef();
		logger.debug("PushEndpoint URL: {}", pushEndpointUrl);

		// execute query
		ClientResource pushEndpointResource = new ClientResource(
				pushEndpointUrl);

		Form queryForm = new Form();
		queryForm.add("query", SampleQueries.WHO_KNOWS_FRANK);
		Representation queryRep = queryForm.getWebRepresentation();

		pushEndpointResource.post(queryRep);

		Assert.assertEquals(200, pushEndpointResource.getResponse().getStatus()
				.getCode());

		// delete workflow
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}
}
