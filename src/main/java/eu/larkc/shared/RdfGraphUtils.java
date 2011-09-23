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

import org.junit.Assert;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.data.VariableBinding.Binding;
import eu.larkc.core.data.util.SPARQLQueryExecutor;
import eu.larkc.core.query.SPARQLQuery;

/**
 * Various utility methods used for testing the LarKC data layer.
 * 
 * @author Christoph Fuchs
 * 
 */
public class RdfGraphUtils {

	private final static Logger logger = LoggerFactory
			.getLogger(RdfGraphUtils.class);

	/**
	 * Retrieves the number of statements contained in the RDF graph
	 * <code>graph</code> as returned by <code>SELECT * WHERE {?s ?p ?o}</code>
	 * query.
	 * 
	 * @param df
	 *            the {@link DataFactory} which holds the RDF graph
	 * 
	 * @param graph
	 *            an RDF graph
	 * @return the number of statements contained in the RDF graph
	 */
	public static int getNumberOfStatementsViaSelectAll(DataFactory df,
			SetOfStatements graph) {
		SPARQLQuery query = df
				.createSPARQLQuery(SampleQueries.SELECT_ALL_TRIPLES);
		return getNumberOfStatements(query, graph);
	}

	/**
	 * Retrieves the number of statements contained in the RDF graph
	 * <code>graph</code> as returned by the given query.
	 * 
	 * @param query
	 *            the SPARQL query to execute
	 * @param graph
	 *            an RDF graph
	 * @return the number of statements retrieved by the given query
	 */
	private static int getNumberOfStatements(SPARQLQuery query,
			SetOfStatements graph) {
		SPARQLQueryExecutor sqe = new SPARQLQueryExecutor();
		VariableBinding result = sqe.executeSelect(query, graph);
		CloseableIterator<Binding> iter = result.iterator();

		int n = 0;
		logger.debug("==========================================");
		logger.debug("Statements in graph {} for query {}", graph, query);
		logger.debug("------------------------------------------");
		while (iter.hasNext()) {
			n++;
			Binding next = iter.next();
			logger.debug("Statement in graph {}: {}", graph, next);
		}
		return n;
	}

	/**
	 * Checks if containedGraph is contained in largeGraph.
	 * 
	 * @param df
	 *            the {@link DataFactory} which holds the RDF graphs
	 * 
	 * @param containedGraph
	 *            an RDF graph
	 * @param largeGraph
	 *            another RDF graph
	 * @return true if every statement in containedGraph can be found in
	 *         largeGraph, false otherwise.
	 */
	public static boolean isGraphContained(DataFactory df,
			SetOfStatements containedGraph, SetOfStatements largeGraph) {
		SPARQLQuery query = df
				.createSPARQLQuery(SampleQueries.SELECT_ALL_TRIPLES);
		SPARQLQueryExecutor sqe = new SPARQLQueryExecutor();
		VariableBinding result1 = sqe.executeSelect(query, containedGraph);
		VariableBinding result2 = sqe.executeSelect(query, largeGraph);
		CloseableIterator<Binding> iter1 = result1.iterator();
		CloseableIterator<Binding> iter2 = result2.iterator();

		// Loop through the (larger) graph which should contain the smaller
		// graph
		outer: while (iter1.hasNext()) {
			// Check if the statement is in the smaller graph
			Binding binding1 = iter1.next();
			while (iter2.hasNext()) {
				Binding binding2 = iter2.next();
				if (binding2.equals(binding1)) {
					// Statement is contained
					continue outer;
				}
			}
			logger.debug("Statement {} was not found in graph {}.", binding1,
					largeGraph);
			return false;
		}

		return true;
	}

	/**
	 * The implementor of the method does not know whether it's a value or
	 * reference graph.
	 * 
	 * @param graph
	 *            the {@link SetOfStatements} describing the RDF graph
	 * @param expectedCount
	 *            the expected number of triples in the RDF graph
	 */
	public static void assertStatementsInGraph(SetOfStatements graph,
			int expectedCount) {
		CloseableIterator<Statement> iter = graph.getStatements();

		int count = 0;
		while (iter.hasNext()) {
			iter.next();
			count++;
		}
		Assert.assertTrue(count >= expectedCount);
	}

}
