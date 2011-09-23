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
package eu.larkc.core.endpoint.pull;

import java.net.URI;

import eu.larkc.core.endpoint.sparql.SparqlHandler;
import eu.larkc.core.endpoint.sparql.SparqlQueryRequest;
import eu.larkc.core.endpoint.sparql.SparqlQueryResult;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.core.executor.Executor;

/**
 * TODO Describe this type briefly. If necessary include a longer description
 * and/or an example.
 * 
 * @author spyros
 * 
 */
public class PullEndpoint extends SparqlHandler {

	protected String fixedQuery;

	/**
	 * Construct new PullEndpoint
	 * 
	 * @param ex
	 *            the executor to be called by this endpoint
	 * @param query
	 *            the query, which is fixed in the workflow
	 */
	public PullEndpoint(Executor ex, String query) {
		super(ex);
		this.fixedQuery = query;
	}

	/**
	 * Parses the request, but puts the fixed query instead of any possible
	 * query in the request
	 */
	@Override
	protected SparqlQueryRequest parseGetUrl(URI url, String query)
			throws SparqlException {
		SparqlQueryRequest qr = new SparqlQueryRequest();
		qr.setQuery(fixedQuery); // Put the query (which is fixed in the
									// workflow)
		return qr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.endpoint.sparql.SparqlHandler#handleQueryRequest(eu.larkc
	 * .core.endpoint.sparql.SparqlQueryRequest)
	 */
	@Override
	protected SparqlQueryResult handleQueryRequest(
			SparqlQueryRequest queryRequest) throws SparqlException {
		return super.handleQueryRequest(queryRequest);
	}

}
