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
package eu.larkc.core.endpoint.sparql;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the data of a SPARQL query request, as described in sec. 2.1.2
 * of the SPARQL Protocol for RDF.
 * 
 * @author Janez Brank, Luka Bradesko, Blaz Fortuna
 */
public class SparqlQueryRequest {

	protected String query;
	/**
	 * Note: the default graph for the query is just one, but several default
	 * graph URIs may be provided. In this case the default graph is "composed
	 * by the RDF merge of the RDC graphs identified by" the graph URIs.
	 */
	protected ArrayList<String> defaultGraphUris; // 0 or more
	protected ArrayList<String> namedGraphUris; // 0 or more

	public SparqlQueryRequest() {
		query = null;
		defaultGraphUris = new ArrayList<String>();
		namedGraphUris = new ArrayList<String>();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query_) {
		query = query_;
	}

	public void addNamedGraphUri(String s) {
		namedGraphUris.add(s);
	}

	public void addDefaultGraphUri(String s) {
		defaultGraphUris.add(s);
	}

	public List<String> getDefaultGraphUris() {
		return defaultGraphUris;
	}

	public List<String> getNamedGraphUris() {
		return namedGraphUris;
	}
}