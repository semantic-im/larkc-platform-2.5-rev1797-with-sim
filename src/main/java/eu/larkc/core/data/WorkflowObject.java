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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openrdf.model.URI;

/**
 * This class represents the information available for a workflow.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class WorkflowObject {

	private UUID workflowID;
	private Map<String, URI> endpointMap;

	/**
	 * Constructor.
	 */
	public WorkflowObject() {
		endpointMap = new HashMap<String, URI>();
	}

	/**
	 * Sets the workflow ID for this workflow object.
	 * 
	 * @param key
	 */
	public void setWorkflowId(UUID key) {
		workflowID = key;
	}

	/**
	 * Adds an endpoint to the map.
	 * 
	 * @param endpointName
	 *            name of the endpoint
	 * @param endpointURI
	 *            uri of the endpoint
	 */
	public void addEndpoint(String endpointName, URI endpointURI) {
		endpointMap.put(endpointName, endpointURI);
	}

	/**
	 * Returns the workflow id.
	 * 
	 * @return the workflow id
	 */
	public UUID getWorkflowId() {
		return workflowID;
	}

	/**
	 * Returns a map with all endpoints.
	 * 
	 * @return endpoint map
	 */
	public Map<String, URI> getEndpoints() {
		return endpointMap;
	}

}
