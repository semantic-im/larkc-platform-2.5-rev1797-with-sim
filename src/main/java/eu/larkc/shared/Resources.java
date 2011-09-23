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
package eu.larkc.shared;

import java.io.File;

/**
 * Holds file names of shared resources, such as configuration or property
 * files. Usually the files described in this class are placed in the resources
 * directory and can thus be loaded without knowing the respective paths, using
 * the class loader.
 * 
 * @author Christoph Fuchs
 * 
 */
public class Resources {

	/**
	 * Filename of the LarKC initialization file used by the plug-in registry.
	 */
	public static final String LARKC_INIT = "larkc-init.lisp";

	/**
	 * Filename of the plug-in INI file (used by the plug-in registry).
	 */
	public static final String PLUGINS_INI = "plugins.ini";

	/**
	 * Filename of the LarKC ontology, described as RDF
	 */
	public static final String LARKC_RDF = "larkc.rdf";

	/**
	 * Filename of the one-to-one endpoint-class mapping which defines which
	 * endpoint type will be mapped to which corresponding Java class.
	 */
	public static final String ENDPOINT_CLASS_MAPPINGS_XML = "EndpointClassMappings.xml";

	/**
	 * Directory with the deployment resource description files.
	 */
	public static final String RESOURCE_DIR = "remote_hosts";

	/**
	 * Directory with the remote host templates.
	 */
	public static final String HOST_TEMPLATES_DIR = "remote_hosts"
			+ File.separatorChar + "templates";

	/**
	 * File with the list of remote host templates.
	 */
	public static final String HOST_TEMPLATES_FILE = "remote_hosts"
			+ File.separatorChar + "templates" + File.separatorChar
			+ "list.txt";

	/**
	 * Directory with the deployment resource description files.
	 */
	public static final String GAT_ADAPTORS_DIR = "target" + File.separatorChar
			+ "classes" + File.separatorChar + "gatadaptors";

	/**
	 * The root directory of the management interface HTML files
	 */
	public static final String MGMT_HTML_ROOT = "mgmt" + File.separatorChar;

	/**
	 * Base URL to get workflows from the mgmt interface
	 */
	public static final String MGMT_WORKFLOWS_URL = "http://localhost:8182/workflow";

}
