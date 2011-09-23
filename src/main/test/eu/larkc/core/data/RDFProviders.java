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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.shared.RdfGraphUtils;

/**
 * Tests various retrieval methods of RDF graphs. Tests currently include:
 * <ul>
 * <li>passing RDF graph by dataset reference</li>
 * <li>passing RDF graph by HTTP reference</li>
 * <li>passing RDF graph by labeled group</li>
 * <li>passing RDF graph by value</li>
 * </ul>
 * 
 * @author ?, Christoph Fuchs
 * 
 */
public class RDFProviders extends ORDITestCase {

	/*
	 * FIXME Topic count changes regularly. Write a better test which does not
	 * rely on exact topic count.
	 */

	/**
	 * Expected topic count of the Ontotext entry in dbpedia.
	 */
	public static final int TOPIC_COUNT_ONTOTEXT = 35;

	/**
	 * URL of the Ontotext entry in dbpedia
	 */
	public static final String TOPIC_URL_ONTOTEXT = "http://dbpedia.org/resource/Ontotext";

	/**
	 * URL of the Knowledge Engineering entry in dbpedia
	 */
	public static final String TOPIC_URL_KNOWLEDGE_ENGINEERING = "http://dbpedia.org/resource/Knowledge_Engineering";

	/**
	 * This example demonstrate how to pass RdfGraph by Value
	 */
	@Test
	public void testPassGraphByValue() {
		URI uri = new URIImpl(TOPIC_URL_ONTOTEXT);

		// Access RDF graph exposed according linked data principles
		RdfGraph remote = df.createRemoteRdfGraph(uri);

		// Transfer all remote data
		CloseableIterator<Statement> iter = remote.getStatements();
		Set<Statement> statements = new HashSet<Statement>();
		while (iter.hasNext()) {
			Statement s = iter.next();
			statements.add(s);
		}

		// Create a RdfGraph passed by value
		URI graphName = new URIImpl(TOPIC_URL_ONTOTEXT);
		RdfGraph graph = df.createRdfGraph(statements, graphName);

		// Send to another component transparently
		RdfGraphUtils.assertStatementsInGraph(graph, TOPIC_COUNT_ONTOTEXT);
	}

	/**
	 * This example demonstrate how to pass RdfGraph by HTTP reference.
	 */
	@Test
	public void testPassGraphByHTTPReference() {
		URI uri = new URIImpl(TOPIC_URL_ONTOTEXT);

		// Access RDF graph exposed according linked data principles
		RdfGraph remote = df.createRemoteRdfGraph(uri);

		// Send to another component transparently
		RdfGraphUtils.assertStatementsInGraph(remote, TOPIC_COUNT_ONTOTEXT);
	}

	/**
	 * This example demonstrates how to pass an RdfGraph by a SPARQL dataset
	 * reference.
	 */
	@Test
	public void testPassGraphByDatasetReference() {

		// Create a new DataSet
		URI uri = new URIImpl(TOPIC_URL_ONTOTEXT);
		RdfGraph remote = df.createRemoteRdfGraph(uri);

		// Create a graph for this DataSet (it lives only in repository)
		RdfStoreConnection con = df.createRdfStoreConnection();
		CloseableIterator<Statement> i = remote.getStatements();
		while (i.hasNext()) {
			Statement s = i.next();
			con.addStatement(s.getSubject(), s.getPredicate(), s.getObject(),
					(URI) s.getContext());
		}

		Set<URI> dataSetURI = new HashSet<URI>();
		dataSetURI.add(remote.getName());
		DataSet ds = con.createDataSet(dataSetURI, dataSetURI);

		// Pass the DataSet by reference
		RdfGraphUtils.assertStatementsInGraph(ds, TOPIC_COUNT_ONTOTEXT);
	}

	/**
	 * This example demonstrates how to pass an RdfGraph by a labeled group
	 * reference.
	 */
	@Test
	public void testPassGraphByLabeledGroup() {
		RdfStoreConnection con = df.createRdfStoreConnection();
		df.createRdfStoreConnection().addStatement(new URIImpl("urn:test"),
				new URIImpl("urn:test"), new URIImpl("urn:test"),
				new URIImpl("urn:test"));

		LabelledGroupOfStatements group = con.createLabelledGroupOfStatements();
		boolean result = group.includeStatement(null, null, null, null);
		assertEquals(true, result);

		// Pass the labeled group by reference
		RdfGraphUtils.assertStatementsInGraph(group, 1);
	}
}
