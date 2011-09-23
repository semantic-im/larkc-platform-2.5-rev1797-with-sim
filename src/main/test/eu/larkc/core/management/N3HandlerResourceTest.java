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

/**
 * Tests the retrieval of remote workflow descriptions via the management
 * interface.
 * 
 * @author Christoph Fuchs
 * 
 */
public class N3HandlerResourceTest extends MgmtTest {

	/**
	 * Tries parse a N3 workflow description.
	 * 
	 * @throws Exception
	 */
	@Test
	public void exampleWorkflowWithEndpointTest() throws Exception {
		ClientResource rdfResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);

		Form form = new Form();
		form.add("workflow", SampleN3Workflows.getMinimalN3WorkflowFromD533());
		Representation rep = form.getWebRepresentation();
		rep.setMediaType(MediaType.TEXT_RDF_N3);

		Representation response = rdfResource.post(rep);
		Reference url = response.getLocationRef();

		Assert.assertEquals(201, rdfResource.getStatus().getCode());

		ClientResource workflowResource = new ClientResource(url);
		workflowResource.delete();
	}

}
