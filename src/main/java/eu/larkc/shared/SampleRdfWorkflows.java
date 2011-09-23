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
 * A collection of example workflows in RDF/XML format for testing purposes.
 * 
 * @author norlan
 * 
 */
public class SampleRdfWorkflows {

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins.
	 * 
	 * @return the workflow as a string
	 */
	public static String getSimpleWorkflow() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"TestDecider1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.decide.TestDecider\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestPath\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestIdentifier1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "</rdf:Description>" +

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins and a test endpoint.
	 * 
	 * @return the workflow as a string
	 */
	public static String getWorkflowWithTestEndpoint() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"TestPath\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestDecider1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.decide.TestDecider\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestIdentifier1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.test.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.test\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"TestPath\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins and a push endpoint.
	 * 
	 * @return the workflow as a string
	 */
	public static String getWorkflowWithPushEndpoint() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"TestPath\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestDecider1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.decide.TestDecider\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestIdentifier1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.push.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.push\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"TestPath\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins and a SPARQL endpoint.
	 * 
	 * @return the workflow as a string
	 */
	public static String getWorkflowWithSparqlEndpoint() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"TestPath\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestDecider1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.decide.TestDecider\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestIdentifier1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.sparql.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.sparql\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"TestPath\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Simple, linear workflow description. Describes a workflow consisting of
	 * three plug-ins and a SPARQL endpoint where two plugins are input plugins.
	 * 
	 * @return the workflow as a string
	 */
	public static String getWorkflowWithMultipleInputPlugins() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"TestPath\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestIdentifier1\"/>"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestDecider1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.decide.TestDecider\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestIdentifier1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestTransformer1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"TestTransformer1\">"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"TestDecider1\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.sparql.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.sparql\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"TestPath\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Minimal workflow in RDF/XML as presented in D.5.3.3.
	 * 
	 * @return the workflow as a string in RDF/XML format
	 */
	public static String getMinimalRDFWorkflowFromD533() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"path\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"plugin1\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin2\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.test.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.test\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"path\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Minimal workflow in RDF/XML as presented in D.5.3.3 without prefixes.
	 * 
	 * @return the workflow as a string in RDF/XML format
	 */
	public static String getMinimalRDFWorkflowFromD533WithoutPrefixes() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
				+

				"<rdf:Description rdf:nodeID=\"path\">"
				+ "<hasInput xmlns=\"http://larkc.eu/schema#\" rdf:nodeID=\"plugin1\"/>"
				+ "<hasOutput xmlns=\"http://larkc.eu/schema#\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin1\">"
				+ "<pluginType xmlns=\"http://larkc.eu/schema#\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"http://larkc.eu/schema#\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin2\">"
				+ "<pluginType xmlns=\"http://larkc.eu/schema#\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.test.ep1\">"
				+ "<endpointType xmlns=\"http://larkc.eu/schema#\" rdf:resource=\"urn:eu.larkc.endpoint.test\"/>"
				+ "<links xmlns=\"http://larkc.eu/schema#\" rdf:nodeID=\"path\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

	/**
	 * Workflow in RDF/XML with one path and two inputs.
	 * 
	 * @return the workflow as a string in RDF/XML format
	 */
	public static String getRDFWorkflowWithMultipleInputs() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:larkc=\"http://larkc.eu/schema#\">"
				+

				"<rdf:Description rdf:nodeID=\"path\">"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"plugin1\"/>"
				+ "<hasInput xmlns=\"larkc:\" rdf:nodeID=\"plugin2\"/>"
				+ "<hasOutput xmlns=\"larkc:\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin2\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.transform.TestTransformer\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:nodeID=\"plugin1\">"
				+ "<pluginType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.plugin.identify.TestIdentifier\"/>"
				+ "<connectsTo xmlns=\"larkc:\" rdf:nodeID=\"plugin2\"/>"
				+ "</rdf:Description>"
				+

				"<rdf:Description rdf:about=\"urn:eu.larkc.endpoint.test.ep1\">"
				+ "<endpointType xmlns=\"larkc:\" rdf:resource=\"urn:eu.larkc.endpoint.test\"/>"
				+ "<links xmlns=\"larkc:\" rdf:nodeID=\"path\"/>"
				+ "</rdf:Description>" +

				"</rdf:RDF>";
	}

}
