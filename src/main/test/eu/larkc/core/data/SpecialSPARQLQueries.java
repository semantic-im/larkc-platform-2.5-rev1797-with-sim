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
package eu.larkc.core.data;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.util.SetOfStatementsMerger;
import eu.larkc.shared.RdfGraphUtils;

/**
 * Tests special SPARQL query against a ORDI data factory.
 * 
 * @author ?, Christoph Fuchs
 * 
 */
public class SpecialSPARQLQueries extends ORDITestCase {

	private final static Logger logger = LoggerFactory
			.getLogger(SpecialSPARQLQueries.class);

	/**
	 * Tests an in-memory select statement.
	 */
	@Test
	public void testInMemorySelect() {
		RdfGraph graph = df.createRemoteRdfGraph(new URIImpl(
				RDFProviders.TOPIC_URL_ONTOTEXT));

		int count = RdfGraphUtils.getNumberOfStatementsViaSelectAll(df, graph);

		// Check if there are any statements in the graph
		Assert.assertTrue(
				"There should be at least one statement in the RDF graph.",
				count > 0);

		// Not sure if it is wise to assert a specific amount of statements,
		// since dbpedia changes over time. I will remove it for now. ~Gigi
		// assertEquals(RDFProviders.TOPIC_COUNT_ONTOTEXT, count);
	}

	/**
	 * Tests an in-memory select statement.
	 */
	@Test
	public void testInMemorySelect2() {
		RdfGraph graph = df.createRemoteRdfGraph(new URIImpl(
				RDFProviders.TOPIC_URL_KNOWLEDGE_ENGINEERING));

		int count = RdfGraphUtils.getNumberOfStatementsViaSelectAll(df, graph);

		// Check if there are any statements in the graph
		Assert.assertTrue(
				"There should be at least one statement in the RDF graph.",
				count > 0);

		// If two selects are run successively the statements of the first
		// graph are kept in the ORDI base. This makes sense since the
		// DataFactory instance is static, but is this the desired behavior?

		// Currently this is fixed by resetting the DataFactory in @Before
		// ORDITestCase#retrieveDataFactory()
	}

	/**
	 * Tests the merging of two RDF graphs
	 */
	@Test
	public void testRdfDataMerge() {
		RdfGraph graph1 = df.createRemoteRdfGraph(new URIImpl(
				RDFProviders.TOPIC_URL_ONTOTEXT));
		RdfGraph graph2 = df.createRemoteRdfGraph(new URIImpl(
				RDFProviders.TOPIC_URL_KNOWLEDGE_ENGINEERING));

		// Retrieve statements of graph1 and graph2
		int numberOfStatementsGraph1 = RdfGraphUtils
				.getNumberOfStatementsViaSelectAll(df, graph1);
		int numberOfStatementsGraph2 = RdfGraphUtils
				.getNumberOfStatementsViaSelectAll(df, graph2);

		logger.debug("Statements in {}: {}", RDFProviders.TOPIC_URL_ONTOTEXT,
				numberOfStatementsGraph1);
		logger.debug("Statements in {}: {}",
				RDFProviders.TOPIC_URL_KNOWLEDGE_ENGINEERING,
				numberOfStatementsGraph2);

		SetOfStatementsMerger merger = new SetOfStatementsMerger();
		merger.add(graph1);
		merger.add(graph2);

		int numberOfStatementsTotal = RdfGraphUtils
				.getNumberOfStatementsViaSelectAll(df, merger);

		// Check if there are any statements in the graph(s)
		Assert.assertTrue(
				"There should be at least one statement in the RDF graph.",
				numberOfStatementsGraph1 > 0);
		Assert.assertTrue(
				"There should be at least one statement in the RDF graph.",
				numberOfStatementsGraph2 > 0);
		Assert.assertTrue(
				"There should be at least one statement in the RDF graph.",
				numberOfStatementsTotal > 0);

		// Check if every statement of graph1 (and graph2) is in the merged
		// graph
		Assert.assertTrue("Graph1 is not fully contained in the merged graph.",
				RdfGraphUtils.isGraphContained(df, graph1, merger));
		Assert.assertTrue("Graph2 is not fully contained in the merged graph.",
				RdfGraphUtils.isGraphContained(df, graph2, merger));

		// Check if there are more statements in the merged graph than in the
		// separate graphs
		Assert
				.assertTrue(
						"Sum of statements of the separate graphs do not add up to the merged total.",
						numberOfStatementsGraph1 + numberOfStatementsGraph2 >= numberOfStatementsTotal);
	}
}
