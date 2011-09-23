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

import java.util.UUID;

import org.openrdf.model.URI;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.Larkc;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.executor.Executor;

/**
 * This class implements the methods defined by the EndpointResource interface
 * to allow operations on specific endpoints over the management interface.
 * 
 * @author norlan
 * 
 */
public class EndpointResourceImpl extends ServerResource implements
		EndpointResource {

	private Logger logger = LoggerFactory.getLogger(EndpointResourceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.management.EndpointResource#getEndpoint()
	 */
	@Override
	public Representation getEndpoint() throws InvalidURLException {
		String workflowId = (String) getRequest().getAttributes().get(
				"workflow");

		if (workflowId == null || workflowId == "") {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			throw new InvalidURLException("No workflow ID found in URL!");
		}

		// Get the whole query (the part after the question mark)
		Form queryAsForm = getRequest().getResourceRef().getQueryAsForm();

		// Get the "type" parameter (?type=endpointtype)
		String urn = queryAsForm.getFirstValue("urn", true);

		if (urn == null || urn == "") {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			throw new InvalidURLException("No URN definition found in URL!");
		}

		logger.debug("Workflow: {}", workflowId);
		logger.debug("Endpoint URN: {}", urn);

		Executor executor = Larkc.getExecutor(UUID.fromString(workflowId));
		Endpoint endpoint = executor.getEndpoint(urn);

		// If the endpoint is null now, the executor does not hold any endpoint
		// matching the type
		if (endpoint == null) {
			logger.warn(
					"No endpoint with URN {} found for executor {}. Probably the workflow of this executor does not have an endpoint with URN {}.",
					new Object[] { urn, executor, urn });
			logger.warn("Available endpoints for this executor: {}",
					executor.getAvailableEndpoints());

			return null;
		}

		URI endpointURL = endpoint.getURI();

		// set response
		setStatus(Status.SUCCESS_CREATED);

		Representation rep = new StringRepresentation(
				endpointURL.stringValue(), MediaType.TEXT_PLAIN);
		// Indicates where the new resource is located
		rep.setLocationRef(endpointURL.stringValue());
		return rep;
	}
}
