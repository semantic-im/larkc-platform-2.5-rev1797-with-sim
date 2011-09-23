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

import org.openrdf.model.URI;

/**
 * This class represents a node in a workflow graph.
 * 
 * @author norlan
 * 
 */
public class GraphNode {

	private URI uri;
	private String pluginID;

	/**
	 * Custom constructor which takes an URI and the plugin id as parameters.
	 * 
	 * @param uri
	 *            uri of the node
	 * @param pluginID
	 *            id of the plugin which refers to this node
	 */
	public GraphNode(URI uri, String pluginID) {
		this.uri = uri;
		this.pluginID = pluginID;
	}

	/**
	 * Getter. Retrieves the pluginID.
	 * 
	 * @return the pluginID
	 */
	public String getPluginID() {
		return pluginID;
	}

	/**
	 * Setter. Sets or updates the pluginID to the passed value.
	 * 
	 * @param pluginID
	 *            the pluginID to set
	 */
	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return uri + "[" + pluginID + "]";
	}

}
