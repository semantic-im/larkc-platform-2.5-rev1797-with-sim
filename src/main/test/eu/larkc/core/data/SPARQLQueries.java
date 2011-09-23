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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.VariableBinding.Binding;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.shared.SampleQueries;

public class SPARQLQueries extends ORDITestCase {

	/**
	 * Non-existent test URI
	 */
	private static final String HTTP_TEST_URI_NET = "http://test-uri.net";

	private Logger logger = LoggerFactory.getLogger(SPARQLQueries.class);

	@Test
	public void testSPARQLSelect() {
		df.createRdfStoreConnection().addStatement(new URIImpl("urn:test"),
				new URIImpl("urn:test"), new URIImpl("urn:test"),
				new URIImpl("urn:test"));
		SPARQLQuery query = new SPARQLQueryImpl(
				SampleQueries.SELECT_ALL_TRIPLES);
		SPARQLEndpoint endpoint = df.createRdfStoreConnection();

		VariableBinding result = endpoint.executeSelect(query);
		result.getVariables();
		CloseableIterator<Binding> iter = result.iterator();
		while (iter.hasNext()) {
			iter.next();
		}
	}

	@Test
	public void testSPARQLConstruct() {
		df.createRdfStoreConnection().addStatement(new URIImpl("urn:test"),
				new URIImpl("urn:test"), new URIImpl("urn:test"),
				new URIImpl("urn:test"));
		SPARQLQuery query = new SPARQLQueryImpl(
				SampleQueries.CONSTRUCT_ALL_TRIPLES);
		SPARQLEndpoint endpoint = df.createRdfStoreConnection();

		SetOfStatements result = endpoint.executeConstruct(query);
		CloseableIterator<Statement> iter = result.getStatements();
		Assert.assertTrue(iter.hasNext());
		while (iter.hasNext()) {
			iter.next();
		}
	}

	@Test
	public void testSPARQLAsk() {
		df.createRdfStoreConnection().addStatement(new URIImpl("urn:test"),
				new URIImpl("urn:test"), new URIImpl("urn:test"),
				new URIImpl("urn:test"));
		SPARQLQuery query = new SPARQLQueryImpl(SampleQueries.ASK_ALL_TRIPLES);
		SPARQLEndpoint endpoint = df.createRdfStoreConnection();
		Assert.assertTrue(endpoint.executeAsk(query));

		query = new SPARQLQueryImpl(
				"ASK WHERE {?s ?p \"NOT%EXISTING%STRING%\"}");
		endpoint = df.createRdfStoreConnection();
		Assert.assertFalse(endpoint.executeAsk(query));
	}

	@Test
	public void testSPARQLLabelledGroup() {
		RdfStoreConnection con = df.createRdfStoreConnection();
		LabelledGroupOfStatements label = con.createLabelledGroupOfStatements();
		URI test = new URIImpl(HTTP_TEST_URI_NET);
		con.addStatement(test, test, test, test, label.getLabel());

		SPARQLQuery query = df
				.createSPARQLQuery(SampleQueries.SELECT_ALL_TRIPLES);
		query.setLabelledGroup(label.getLabel());

		VariableBinding result = con.executeSelect(query);
		CloseableIterator<Binding> iter = result.iterator();
		Assert.assertTrue(iter.hasNext());
		while (iter.hasNext()) {
			logger.debug("Variable binding: {}", iter.next());
		}
	}
}
