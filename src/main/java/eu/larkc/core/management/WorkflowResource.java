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

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;

import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.endpoint.EndpointShutdownException;

/**
 * This interface defines the methods which are possible via the management
 * interface for a workflow.
 * 
 * @author Luka, Norbert Lanzanasto
 * 
 */
public interface WorkflowResource {
	/**
	 * This method deletes a workflow via the management interface.
	 * 
	 * @return a representation indicating of the deletion was successful.
	 * 
	 * @throws InvalidURLException
	 *             if the URL is invalid
	 * @throws EndpointShutdownException
	 *             if one or more endpoints are unable to shutdown
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 * @throws IllegalWorkflowGraphException
	 */
	@Delete
	public Representation deleteWorkflow() throws InvalidURLException,
			EndpointShutdownException, RepositoryException,
			MalformedQueryException, QueryEvaluationException,
			IllegalWorkflowGraphException;
}