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

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.shared.RdfGraphUtils;

/**
 * This test class demonstrate the accept request made by the default
 * RemoteRdfGraph.
 * 
 * @author vassil, Christoph Fuchs
 * 
 */
public class TestRemoteRdfHeader extends ORDITestCase {

	private static final String RICHARD_CYGANIAK_DE = "http://richard.cyganiak.de/2008/03/rdfbugs/accept.php";

	/**
	 * Accept header test for RDF agents
	 */
	@Test
	public void testRemoteHeader() {
		URI uri = new URIImpl(RICHARD_CYGANIAK_DE);
		RdfGraph graph = df.createRemoteRdfGraph(uri);
		int n = RdfGraphUtils.getNumberOfStatementsViaSelectAll(df, graph);
		Assert.assertTrue("There must be at least one statement", n > 0);
	}
}
