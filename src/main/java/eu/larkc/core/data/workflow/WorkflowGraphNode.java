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
package eu.larkc.core.data.workflow;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;

/**
 * This class represents a node of a workflow graph which contains the uri of
 * the node as well as the next and previous nodes.
 * 
 * @author norlan
 * 
 */
public class WorkflowGraphNode {

	private URI uri;
	private List<WorkflowGraphNode> next;

	/**
	 * Default constructor.
	 */
	public WorkflowGraphNode() {
		this.uri = null;
		this.next = new ArrayList<WorkflowGraphNode>();
	}

	/**
	 * Getter. Retrieves the uri.
	 * 
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Setter. Sets or updates the uri to the passed value.
	 * 
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * Getter. Retrieves the next nodes.
	 * 
	 * @return the next
	 */
	public List<WorkflowGraphNode> getNext() {
		return next;
	}

	/**
	 * Setter. Sets or updates the next to the passed value.
	 * 
	 * @param next
	 *            the next to set
	 */
	public void setNext(List<WorkflowGraphNode> next) {
		this.next = next;
	}

	/**
	 * Adds the passed value to the next.
	 * 
	 * @param next
	 *            the next to add
	 */
	public void addNext(WorkflowGraphNode next) {
		this.next.add(next);
	}

}
