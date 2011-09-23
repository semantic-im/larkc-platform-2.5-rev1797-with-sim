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

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Various tests. TODO Move/rename tests so that they better represent a clear
 * structure.
 * 
 * @author vassil, Christoph Fuchs
 * 
 */
public class RdfDataEqualitiers extends ORDITestCase {

	/**
	 * Tests if the DataFactory can be configured properly using system
	 * properties.
	 */
	@Test
	public void testParamConfig() {
		DataFactoryImpl.getInstance();
		assertEquals(1000 * 60, DataFactoryImpl.REFERENCE_TYPES_CACHE_TIME);

		// Set REFERENCE_TYPES_CACHE_TIME_PROP property to 10
		System.setProperty(DataFactoryImpl.REFERENCE_TYPES_CACHE_TIME_PROP,
				"10");
		DataFactoryImpl.getInstance();
		assertEquals(10, DataFactoryImpl.REFERENCE_TYPES_CACHE_TIME);

		// Set REFERENCE_TYPES_CACHE_TIME_PROP to an invalid value
		System.setProperty(DataFactoryImpl.REFERENCE_TYPES_CACHE_TIME_PROP,
				"invalid");
		DataFactoryImpl.getInstance();
		// Should be still 10
		assertEquals(10, DataFactoryImpl.REFERENCE_TYPES_CACHE_TIME);
	}

	/**
	 * Tests if two data sets are equal. Includes testing of some caching
	 * mechanism.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testDataSet() throws InterruptedException {
		RdfStoreConnection con = df.createRdfStoreConnection();

		// Create the two data sets
		DataSet ds = new DataSetImpl(con, null, null);
		DataSet ds2 = new DataSetImpl(con, null, null);
		assertEquals(ds, ds2);

		// TODO vassil: Explain why this sleep is needed (and please add it to
		// the test description while you are at it)
		Thread.sleep(100);
		ds2 = new DataSetImpl(con, null, null);

		// cache has expired
		assertEquals(ds.equals(ds2), false);

		ArrayList<URI> graphs = new ArrayList<URI>();
		graphs.add(new URIImpl("urn:test"));
		ds2 = new DataSetImpl(con, graphs, null);

		assertEquals(ds.equals(ds2), false);
	}

	/**
	 * Tests if two remote graphs (sharing the same URL) are equal
	 */
	@Test
	public void testHTTPRemoteGraph() {
		HTTPRemoteGraph rdf1 = new HTTPRemoteGraph(new URIImpl(
				"http:////linkedlifedata.com//resource//umls/id//C0024117"));
		HTTPRemoteGraph rdf2 = new HTTPRemoteGraph(new URIImpl(
				"http:////linkedlifedata.com//resource//umls/id//C0024117"));

		assertEquals(rdf1, rdf2);
	}

	/**
	 * Assures that two labeled groups of statements are not equal as long as
	 * they have a different label.
	 */
	@Test(expected = RuntimeException.class)
	public void testLabelledGroupOfStatementsImpl() {
		RdfStoreConnection con = df.createRdfStoreConnection();
		LabelledGroupOfStatements l1 = null;

		try {
			l1 = con.createLabelledGroupOfStatements(new URIImpl("urn:test"));
			Assert.fail("Already created!");

		} catch (RuntimeException e) {

		}
		LabelledGroupOfStatements l2 = con
				.createLabelledGroupOfStatements(new URIImpl("urn:test10"));

		assertEquals(l1, l2);
		assertEquals(l1.equals(l2), false);

	}
}
