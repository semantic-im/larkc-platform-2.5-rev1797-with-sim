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
 * A collection of example workflows in N3 format for testing purposes.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class SampleN3Workflows {

	/**
	 * Minimal workflow in N3 as presented in D.5.3.3.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getMinimalN3WorkflowFromD533() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> ."
				+ "<urn:eu.larkc.endpoint.test.ep1> larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow in N3 as presented in D.5.3.3 without prefixes.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getMinimalN3WorkflowFromD533WithoutPrefixes() {
		return "# Define two plug-ins  \n"
				+ "_:plugin1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 <http://larkc.eu/schema#connectsTo> _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path <http://larkc.eu/schema#hasInput> _:plugin1 . \n"
				+ "_:path <http://larkc.eu/schema#hasOutput> _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:eu.larkc.endpoint.test> ."
				+ "<urn:eu.larkc.endpoint.test.ep1> <http://larkc.eu/schema#links> _:path . \n";
	}

	/**
	 * Workflow in N3 with one path and two inputs.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getN3WorkflowWithMultipleInputs() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasInput _:plugin2 . \n"
				+ "_:path larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> ."
				+ "<urn:eu.larkc.endpoint.test.ep1> larkc:links _:path . \n";
	}

	/**
	 * More complex split&merge workflow in N3 as presented in D.5.3.3.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getSplitMergeWorkflowFromD533() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> . \n"

				+ "# Define four plug-ins \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"
				+ "_:plugin3 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"
				+ "_:plugin4 a <urn:eu.larkc.plugin.decide.TestDecider> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"
				+ "_:plugin1 larkc:connectsTo _:plugin3 . \n"
				+ "_:plugin2 larkc:connectsTo _:plugin4 . \n"
				+ "_:plugin3 larkc:connectsTo _:plugin4 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path . \n"
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin4 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.sparql.ep1> a <urn:eu.larkc.endpoint.sparql> . \n"
				+ "<urn:eu.larkc.endpoint.sparql.ep1> larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow in N3 for testing GAT based distribution model
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getDistributedWorkflowForGAT() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestRemoteIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				// + "# Define the remote host \n"
				// + "_:host1 a <file:////local_host.rdf> . \n"

				+ "# Define the plug-in which runs remotely \n"
				+ "_:plugin1 larkc:runsOn <file:///remote_hosts/templates/local_host.rdf> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.sparql.ep1> a <urn:eu.larkc.endpoint.sparql> ."
				+ "<urn:eu.larkc.endpoint.sparql.ep1> larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow in N3 as presented for testing JEE based distribution
	 * model
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getDistributedWorkflowForJEE() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestRemoteIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				// + "# Define the remote host \n"
				// + "_:host1 a <file:////local_host.rdf> . \n"

				+ "# Define the plug-in which runs remotely \n"
				+ "_:plugin1 larkc:runsOn <file:///remote_hosts/templates/tomcat.rdf> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.sparql.ep1> a <urn:eu.larkc.endpoint.sparql> ."
				+ "<urn:eu.larkc.endpoint.sparql.ep1> larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow in N3 as presented in D.5.3.3, whereby one of the
	 * plug-ins runs remotely. The remote host parameters are contained in the
	 * workflow description.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getMinimalDistributedWorkflowFromD533WithHostDescription() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define two plug-ins  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestRemoteIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"

				+ "# Define the remote host \n"
				+ "_:host1 a <urn:eu.larkc.host.localhost1> . \n"

				+ "# Define the plug-in which runs remotely \n"
				+ "_:plugin1 larkc:runsOn _:host1 . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin2 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin2 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.sparql.ep1> a <urn:eu.larkc.endpoint.sparql> ."
				+ "<urn:eu.larkc.endpoint.sparql.ep1> larkc:links _:path . \n"

				+ "# Specify the remote host options \n"
				+ "_:host1 larkc:hostType larkc:GAT . \n"
				+ "_:host1 larkc:gatBroker larkc:GAT-broker-local . \n"
				+ "_:host1 larkc:gatFileAdaptor larkc:GAT-fileadaptor-local . \n"
				+ "_:host1 larkc:gatWorkDir <file:////tmp> . \n"
				+ "_:host1 larkc:gatJavaDir <file:////usr/bin> . \n"
				+ "_:host1 larkc:gatJavaOptions \"-Xmx512M\" . \n";

	}

	/**
	 * Minimal workflow that defines only one plugin with parameters to create a
	 * new workflow within the plugin.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getMinimalWorkflowWithInternalWorkflow() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define one plug-in  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.spanningworkflowplugin.SpanningWorkflowPlugin> . \n"
				+ "_:plugin1 larkc:hasParameter _:pp1 . \n"
				+ "_:pp1 larkc:constructWorkflow \"true\" . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin1 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> ."
				+ "<urn:eu.larkc.endpoint.test.ep1> larkc:links _:path . \n";
	}

	/**
	 * Minimal workflow that defines only one plugin with parameters to not
	 * create a new workflow within the plugin.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getMinimalWorkflowWithoutInternalWorkflow() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> .  \n"

				+ "# Define one plug-in  \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.spanningworkflowplugin.SpanningWorkflowPlugin> . \n"
				+ "_:plugin1 larkc:hasParameter _:pp1 . \n"
				+ "_:pp1 larkc:constructWorkflow \"false\" . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path a larkc:Path ."
				+ "_:path larkc:hasInput _:plugin1 . \n"
				+ "_:path larkc:hasOutput _:plugin1 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> ."
				+ "<urn:eu.larkc.endpoint.test.ep1> larkc:links _:path . \n";
	}

	/**
	 * Workflow in N3 with two test endpoints.
	 * 
	 * @return the workflow as a string in N3 format
	 */
	public static String getWorkflowWithMultipleTestEndpoints() {
		return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix larkc: <http://larkc.eu/schema#> . \n"

				+ "# Define four plug-ins \n"
				+ "_:plugin1 a <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin2 a <urn:eu.larkc.plugin.identify.TestIdentifier> . \n"
				+ "_:plugin3 a <urn:eu.larkc.plugin.transform.TestTransformer> . \n"
				+ "_:plugin4 a <urn:eu.larkc.plugin.decide.TestDecider> . \n"
				+ "_:plugin5 a <urn:eu.larkc.plugin.decide.TestDecider> . \n"

				+ "# Connect the plug-ins \n"
				+ "_:plugin1 larkc:connectsTo _:plugin3 . \n"
				+ "_:plugin2 larkc:connectsTo _:plugin3 . \n"
				+ "_:plugin3 larkc:connectsTo _:plugin4 . \n"
				+ "_:plugin3 larkc:connectsTo _:plugin5 . \n"

				+ "# Define the plug-in parameter \n"
				+ "_:plugin3 larkc:hasParameter _:pp1 . \n"
				+ "_:pp1 larkc:hasInputBehaviour \"1\" . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path1 a larkc:Path . \n"
				+ "_:path1 larkc:hasInput _:plugin1 . \n"
				+ "_:path1 larkc:hasOutput _:plugin4 . \n"

				+ "# Define a path to set the input and output of the workflow \n"
				+ "_:path2 a larkc:Path . \n"
				+ "_:path2 larkc:hasInput _:plugin2 . \n"
				+ "_:path2 larkc:hasOutput _:plugin5 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> a <urn:eu.larkc.endpoint.test> . \n"
				+ "<urn:eu.larkc.endpoint.test.ep1> larkc:links _:path1 . \n"

				+ "# Connect an endpoint to the path \n"
				+ "<urn:eu.larkc.endpoint.test.ep2> a <urn:eu.larkc.endpoint.test> . \n"
				+ "<urn:eu.larkc.endpoint.test.ep2> larkc:links _:path2 . \n";
	}

}
