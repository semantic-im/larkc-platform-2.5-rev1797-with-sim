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

/**
 * A collection of example workflows in Turtle format for testing purposes.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class SampleTurtleWorkflows {

	/**
	 * Minimal workflow in Turtle format as presented in D.5.3.3.
	 * 
	 * @return the workflow as a string in turtle format
	 */
	public static String getMinimalTurtleWorkflowFromD533() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestIdentifier> ; \n"
				+ "          larkc:connectsTo _:plugin2 . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path larkc:hasInput _:plugin1 ; \n"
				+ "       larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> ;"
				+ "                                 larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow in Turtle format as presented in D.5.3.3 without
	 * prefixes.
	 * 
	 * @return the workflow as a string in turtle format
	 */
	public static String getMinimalTurtleWorkflowFromD533WithoutPrefixes() {
		return "# Define two plug-ins  \n"
				+ "_:plugin1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.plugin.identify.TestIdentifier> ; \n"
				+ "          <http://larkc.eu/schema#connectsTo> _:plugin2 . \n"
				+ "_:plugin2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path <http://larkc.eu/schema#hasInput> _:plugin1 ; \n"
				+ "       <http://larkc.eu/schema#hasOutput> _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.endpoint.test> ;"
				+ "                                 <http://larkc.eu/schema#links> _:path . \n";
	}

}
