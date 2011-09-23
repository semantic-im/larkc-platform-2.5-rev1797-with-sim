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

import org.junit.Test;
import org.openrdf.rio.RDFFormat;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.shared.SampleWorkflows;
import eu.larkc.shared.SerializationHelper;

/**
 * Tests the serialization helper for various workflows and formats. Note that
 * those tests don't have any assertions. As long as they don't throw any
 * exceptions it's fine.
 * 
 * @author Christoph Fuchs
 * 
 */
public class SerializationTest {

	/**
	 * Prints out a workflow where the used plugins have multiple parameters
	 */
	@Test
	public void testSerializationOfWorkflowWithParameters() {
		SetOfStatements wf = SampleWorkflows
				.getSimpleWorkflowDescriptionWithParameters();
		SerializationHelper.printSetOfStatements(wf, System.out,
				RDFFormat.NTRIPLES);
	}

	/**
	 * Prints out two sample workflows. One with an embedded query and one with
	 * a SPARQL endpoint. This is to show the two possibilities of query
	 * entry-points of a workflow.
	 */
	@Test
	public void testSerializationOfSpyrosExamples() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.spyrosSimpleWorkflowWithEmbeddedQuery();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.NTRIPLES);

		SetOfStatements descriptionWithSPARQLEndpoint = SampleWorkflows
				.spyrosSimpleWorkflowWithEndpoint();
		SerializationHelper.printSetOfStatements(descriptionWithSPARQLEndpoint,
				System.out, RDFFormat.NTRIPLES);
	}

	/**
	 * Tests the serialization of a simple workflow to RDF/XML
	 */
	@Test
	public void testSerializationToRdfXml() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.RDFXML);
	}

	/**
	 * Tests the serialization of a simple workflow to N3
	 */
	@Test
	public void testSerializationToN3() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.N3);
	}

	/**
	 * Tests the serialization of a simple workflow to NTRIPLES format
	 */
	@Test
	public void testSerializationToNTriples() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.NTRIPLES);
	}

	/**
	 * Tests the serialization of a simple workflow to turtle format
	 */
	@Test
	public void testSerializationToTurtle() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.TURTLE);
	}

	/**
	 * Tests the serialization of a simple workflow to Trig format
	 */
	@Test
	public void testSerializationToTrig() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.TRIG);
	}

	/**
	 * Tests the serialization of a simple workflow to Trix format
	 */
	@Test
	public void testSerializationToTrix() {
		SetOfStatements descriptionWithQuery = SampleWorkflows
				.getSimpleWorkflowDescription();
		SerializationHelper.printSetOfStatements(descriptionWithQuery,
				System.out, RDFFormat.TRIX);
	}

	/**
	 * Tests the serialization of a workflow with splits
	 */
	@Test
	public void testSerializationOfSplitAndMerge() {
		SetOfStatements splitMergeWf = SampleWorkflows
				.getWorkflowDescriptionWithSplitsAndMerges();
		SerializationHelper.printSetOfStatements(splitMergeWf, System.out,
				RDFFormat.RDFXML);
	}

	/**
	 * Serializes an example workflow which has three plug-ins, an input and
	 * output, which forms a path.
	 */
	@Test
	public void testSerializationOfWorkflowWithPath() {
		SetOfStatements splitMergeWf = SampleWorkflows
				.getSimpleWorkflowDescriptionWithPath();
		SerializationHelper.printSetOfStatements(splitMergeWf, System.out,
				RDFFormat.RDFXML);
	}

	/**
	 * Serializes the minimal workflow.
	 */
	@Test
	public void testSerializationOfMinimalWorkflow() {
		SetOfStatements minWf = SampleWorkflows.getMinimalWorkflowDescription();
		SerializationHelper.printSetOfStatements(minWf, System.out,
				RDFFormat.RDFXML);
	}

	/**
	 * Serializes the minimal workflow to N3.
	 */
	@Test
	public void testSerializationOfMinimalWorkflowN3() {
		SetOfStatements minWfN3 = SampleWorkflows
				.getMinimalWorkflowDescription();
		SerializationHelper.printSetOfStatements(minWfN3, System.out,
				RDFFormat.NTRIPLES);
	}

	/**
	 * Serializes the simple workflow to from D533 to N3 format.
	 */
	@Test
	public void testSerializationOfD533Workflow() {
		SetOfStatements simpleCorrect = SampleWorkflows
				.getMinimalWorkflowFromD533();
		SerializationHelper.printSetOfStatements(simpleCorrect, System.out,
				RDFFormat.NTRIPLES);
	}

}
