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
package eu.larkc.core.resourceregistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.pluginregistry.CycUtil;
import eu.larkc.core.pluginregistry.PluginRegistry;

/**
 * The LarKC platform resource registry. This class stores all the resource meta
 * data and is able to execute queries against the resources KB
 * 
 * @author Alexey Cheptsov
 * 
 */
public class ResourceRegistry extends PluginRegistry {
	private static Logger logger = LoggerFactory
			.getLogger(PluginRegistry.class);

	// count for registered resources. Used also to generate unique id in the
	// internal kb.
	static int iResourceCount = 0;
	private final HashMap<URI, Class<?>> javaResourceClassH;

	/**
	 * Initializes the registry and populates its KB (ex Cyc KB).
	 */
	public ResourceRegistry() {
		super();
		javaResourceClassH = new HashMap<URI, Class<?>>();
	}

	/**
	 * Registers the resources
	 * 
	 */
	public void loadResources() {

		// checks files and directories in PLATFORM/resources
		File resourcesDir = new File("." + File.separatorChar + "resources");
		File[] resourcesFiles = resourcesDir.listFiles();
		if (resourcesFiles != null) {
			for (File file : resourcesFiles) {
				if (file.getAbsolutePath().endsWith(".rdf")) {
					// storing the resource's rdf description in KB
					try {
						InputStream rdfFileStream = new FileInputStream(file);
						CycUtil.loadRdfTurtle(rdfFileStream);

					} catch (FileNotFoundException e) {
						logger.warn("Cannot read the rdf resource description from "
								+ file.getAbsolutePath());
					} catch (RDFParseException e) {
						logger.warn("Error in resource's rdf description for "
								+ file.getAbsolutePath());
					} catch (RDFHandlerException e) {
						logger.warn("Error in resource's rdf description for "
								+ file.getAbsolutePath());
					} catch (IOException e) {
						logger.warn("Cannot read the rdf resource description from for "
								+ file.getAbsolutePath());
					}
				}
			}
		} else
			logger.warn("No resource description found, only local execution is possible");

	}

}
