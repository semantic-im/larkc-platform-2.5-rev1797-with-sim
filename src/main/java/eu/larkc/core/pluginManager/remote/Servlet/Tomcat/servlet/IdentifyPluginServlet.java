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
package eu.larkc.core.pluginManager.remote.Servlet.Tomcat.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import eu.larkc.shared.SampleQueries;

/**
 * Servlet for an identifier plugin.
 * 
 * @author Barry Bishop
 * 
 */
public class IdentifyPluginServlet extends PluginServlet {

	protected void invokePlugin(Plugin plugin, UrlParameters parameters,
			ObjectInputStream input, ObjectOutputStream output)
			throws IOException {

		SetOfStatements invocationParameters;

		try {
			invocationParameters = (SetOfStatements) input.readObject();
		} catch (ClassNotFoundException e) {
			String message = "Unable to read input stream: " + e.getMessage();
			log(message);
			throw new IOException(message, e);
		}

		Plugin identifier = (Plugin) plugin;

		SetOfStatements result = identifier.invoke(invocationParameters);

		// output.writeObject(result);
		output.writeObject(new SPARQLQueryImpl(
				SampleQueries.WHO_KNOWS_TIM_BERNERS_LEE).toRDF());

	}

	private static final long serialVersionUID = 1L;
}
