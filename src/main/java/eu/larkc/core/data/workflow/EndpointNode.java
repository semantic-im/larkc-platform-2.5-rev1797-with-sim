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
 * This class contains the information about an endpoint.
 * 
 * @author norlan
 * 
 */
public class EndpointNode {

	private URI uri;
	private String type;
	private String path;

	/**
	 * Default constructor.
	 */
	public EndpointNode() {

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
	 * Getter. Retrieves the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter. Sets or updates the type to the passed value.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Setter. Sets or updates the path to the passed value.
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Getter. Retrieves the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
}
