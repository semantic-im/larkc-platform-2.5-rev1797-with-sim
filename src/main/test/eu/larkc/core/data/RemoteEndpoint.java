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

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.core.data.VariableBinding.Binding;
import eu.larkc.core.query.RemoteSPARQLEndpoint;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;

/**
 * Tests the functionality of the data layer with sparql queries.
 * 
 * @author
 * 
 */
public class RemoteEndpoint {

	/**
	 * Tests a sparql select on the data layer.
	 */
	@Test
	public void testSPARQLSelect() {
		RemoteSPARQLEndpoint rse = new RemoteSPARQLEndpoint(new URIImpl(
				"http://linkedlifedata.com/sparql"));
		SPARQLQuery q = new SPARQLQueryImpl(
				"PREFIX biopax2: <http://www.biopax.org/release/biopax-level2.owl#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?interaction ?protein ?protein_name WHERE { ?interaction rdf:type biopax2:interaction . ?interaction biopax2:PARTICIPANTS ?p . ?p biopax2:PHYSICAL-ENTITY ?protein . ?protein biopax2:NAME ?protein_name filter(regex(str(?protein_name), \"IL-2\")) } LIMIT 100");
		VariableBinding vb = rse.executeSelect(q);
		System.out.println(vb.getVariables());
		CloseableIterator<Binding> iter = vb.iterator();
		while (iter.hasNext()) {
			Binding b = iter.next();
			System.out.println(b);
		}
	}

	/**
	 * Tests a sparql construct on the data layer.
	 */
	@Test
	public void testSPARQLConstruct() {
		RemoteSPARQLEndpoint rse = new RemoteSPARQLEndpoint(new URIImpl(
				"http://linkedlifedata.com/sparql"));
		SPARQLQuery q = new SPARQLQueryImpl(
				"CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o} LIMIT 100");
		SetOfStatements result = rse.executeConstruct(q);
		CloseableIterator<Statement> iter = result.getStatements();
		while (iter.hasNext()) {
			Statement s = iter.next();
			System.out.println(s);
		}
	}

	/**
	 * Tests a sparql ask on the data layer.
	 */
	@Test
	public void testSPARQLAsk() {
		RemoteSPARQLEndpoint rse = new RemoteSPARQLEndpoint(new URIImpl(
				"http://linkedlifedata.com/sparql"));
		SPARQLQuery q = new SPARQLQueryImpl("ASK WHERE { ?s ?p ?o}");
		System.out.println(rse.executeAsk(q));
	}
}
