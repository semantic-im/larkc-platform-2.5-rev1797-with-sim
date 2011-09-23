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

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import eu.larkc.shared.Resources;
import eu.larkc.shared.SampleN3Workflows;
import eu.larkc.shared.SampleRdfWorkflows;

/**
 * Tests deleting a workflow via the management interface.
 * 
 * @author Norbert Lanzanasto, Christoph Fuchs
 * 
 */
public class WorkflowResourceTest extends MgmtTest {

	/**
	 * Tries to delete a workflow.
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteRdfXmlWorkflowTest() throws Exception {
		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		// a workflow is created
		Form form = new Form();
		form.add("workflow", SampleRdfWorkflows.getWorkflowWithTestEndpoint());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.APPLICATION_RDF_XML);
		Representation response = rdfResource.post(rep);
		Reference url = response.getLocationRef();

		// the workflow is deleted
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();

		// HTTP code should be SUCCESS - 200
		Assert.assertEquals(200, workflowResource.getStatus().getCode());
	}

	/**
	 * Tries to delete a workflow.
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteN3WorkflowTest() throws Exception {
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		// a workflow is created
		Form form = new Form();
		form.add("workflow", SampleN3Workflows.getMinimalN3WorkflowFromD533());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);
		Representation response = n3Resource.post(rep);
		Reference url = response.getLocationRef();

		// the workflow is deleted
		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();

		// HTTP code should be SUCCESS - 200
		Assert.assertEquals(200, workflowResource.getStatus().getCode());
	}

	/**
	 * Tries to delete a workflow which was created via the /n3 url via the /rdf
	 * url.
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteN3WorkflowViaRdfTest() throws Exception {
		ClientResource n3Resource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		// a workflow is created
		Form form = new Form();
		form.add("workflow", SampleN3Workflows.getMinimalN3WorkflowFromD533());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);
		Representation response = n3Resource.post(rep);
		Reference url = response.getLocationRef();

		// Replace the /n3 part of the url with /rdf
		Reference newUrl = new Reference(url.toString().replaceAll("/n3",
				"/rdf"));

		// the workflow is deleted
		ClientResource workflowResource = new ClientResource(newUrl);
		workflowResource.delete();

		// HTTP code should be SUCCESS - 200
		Assert.assertEquals(200, workflowResource.getStatus().getCode());
	}
}
