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

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.Larkc;
import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.endpoint.EndpointShutdownException;

/**
 * This class implements the methods defined by the WorkflowResource interface
 * to allow operations on specific workflows over the management interface.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class WorkflowResourceImpl extends ServerResource implements
		WorkflowResource {

	private Logger logger = LoggerFactory.getLogger(WorkflowResourceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.management.WorkflowResource#deleteWorkflow()
	 */
	@Override
	public Representation deleteWorkflow() throws InvalidURLException,
			EndpointShutdownException, RepositoryException,
			MalformedQueryException, QueryEvaluationException,
			IllegalWorkflowGraphException {
		String workflowId = (String) getRequest().getAttributes().get(
				"workflow");

		UUID workflowUUID = UUID.fromString(workflowId);
		Representation rep;

		if (workflowId != null) {
			logger.debug("Delete workflow " + workflowId + " ...");

			// terminate executor
			Larkc.terminateExecutor(workflowUUID);

			rep = new StringRepresentation(workflowId.toString() + " deleted.",
					MediaType.TEXT_PLAIN);
			return rep;

		} else {
			rep = new StringRepresentation("No workflow ID found in URL!",
					MediaType.TEXT_PLAIN);
			return rep;
		}
	}

}
