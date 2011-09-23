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
 * Helper class which holds some example queries used in the tests.
 * 
 * @author Christoph Fuchs
 * 
 */
public class SampleQueries {

	/**
	 * TODO: Describe this query
	 */
	public static final String PASS_ALL_CONSTRUCT = "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> CONSTRUCT {?x ?y ?z} WHERE { ?x ?y ?z }";

	/**
	 * Simple query from LUBM
	 */
	public static String LUBM = "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> select * where { ?z a ub:Department . ?z ub:subOrganizationOf ?y . ?x ub:undergraduateDegreeFrom ?y }";

	/**
	 * Simple construct query from LUBM
	 */
	public static String LUBM_CONSTRUCT = "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> CONSTRUCT {?x ?y ?z} WHERE { ?z a ub:Department. ?z ub:subOrganizationOf ?y. ?x ub:undergraduateDegreeFrom ?y. }";

	/**
	 * Simple construct query from LUBM with a single join
	 */
	public static String LUBM_ONEJOIN_CONSTRUCT = "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> CONSTRUCT {?z ?y ?z} WHERE { ?z a ub:Department. ?z ub:subOrganizationOf ?y. }";

	/**
	 * Very simple construct query from LUBM
	 */
	public static String SIMPLE_LUBM_CONSTRUCT = "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> CONSTRUCT {?z ?z ?z} WHERE { ?z a ub:Department.}";

	/**
	 * Simple query which looks for a person who knows Frank van Harmelen
	 */
	public static String WHO_KNOWS_FRANK = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "CONSTRUCT {?person2 foaf:name ?name} where {\n"
			+ "?person rdf:type foaf:Person .\n"
			+ "?person foaf:name \"Frank van Harmelen\" .\n"
			+ "?person2 rdf:type foaf:Person .\n"
			+ "?person2 foaf:knows ?person .\n"
			+ "?person2 foaf:name ?name .\n" + "}";

	/**
	 * Simple query that can be answered with info from the web
	 */
	public static String SIMPLE_WEB = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "SELECT DISTINCT ?name ?y ?z where {\n"
			+ "?person ?y ?z .\n"
			+ "?person foaf:name \"Frank van Harmelen\" .\n" + "}";

	/**
	 * Simple query that can be answered with info from the web
	 */
	public static String SIMPLE_CONSTRUCT_WEB = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "CONSTRUCT {?person ?y ?z} where {\n"
			+ "?person ?y ?z .\n"
			+ "?person foaf:name \"Frank van Harmelen\" .\n" + "}";

	/**
	 * Simple query which looks for a person who knows Sir Tim Berners Lee
	 */
	public static final String WHO_KNOWS_TIM_BERNERS_LEE = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "SELECT DISTINCT ?name where {\n"
			+ "?person rdf:type foaf:Person.\n"
			+ "?person foaf:name \"Tim Berners-Lee\".\n"
			+ "?person2 rdf:type foaf:Person.\n"
			+ "?person2 foaf:knows ?person.\n"
			+ "?person2 foaf:name ?name.\n"
			+ "}";

	/**
	 * Selects all triples (<code>SELECT * WHERE {?s ?p ?o}</code>)
	 */
	public static final String SELECT_ALL_TRIPLES = "SELECT * WHERE {?s ?p ?o}";

	/**
	 * Constructs all triples (
	 * <code>CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}</code>)
	 */
	public static final String CONSTRUCT_ALL_TRIPLES = "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}";

	/**
	 * Ask all triples ("<code>ASK WHERE {?s ?p ?o}"</code>)
	 */
	public static final String ASK_ALL_TRIPLES = "ASK WHERE {?s ?p ?o}";

}
