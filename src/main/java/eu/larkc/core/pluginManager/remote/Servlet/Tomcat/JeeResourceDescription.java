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
package eu.larkc.core.pluginManager.remote.Servlet.Tomcat;

import eu.larkc.core.Resource;

/**
 * The class represents remote resource's properties for the remote execution
 * with JEE
 * 
 * @author Alexey Cheptsov
 * 
 */

public class JeeResourceDescription implements Resource {
	public String URI;

	@Deprecated
	public int NumMPIProcesses;

	/** Empty constructor */
	public JeeResourceDescription() {

	}

	/**
	 * Constructor of the class for a standard application.
	 * 
	 * @param URI
	 *            A string with resource's URI in the Jee notation, e.g.
	 *            "http://localhost:8080/my_identifier/identifier"
	 */
	public JeeResourceDescription(String URI) {
		this.URI = URI;
	}

}