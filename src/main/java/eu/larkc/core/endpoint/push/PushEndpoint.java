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
package eu.larkc.core.endpoint.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;

import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.executor.Executor;

/**
 * This class implements an endpoint for testing purpose.
 * 
 * @author Luka Bradesko
 * 
 */
public class PushEndpoint extends Endpoint {

	private static Logger logger = LoggerFactory.getLogger(PushEndpoint.class);

	/**
	 * Constructor. The endpoint has to know in advance which {@link Executor}
	 * is responsible for incoming queries.
	 * 
	 * @param ex
	 *            the {@link Executor} responsible for this endpoint
	 */
	public PushEndpoint(Executor ex) {
		super(ex, "/pushendpoint");
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		logger.info("PushEndpoint Call Start");

		// parse input
		BufferedReader rdfList = new BufferedReader(new InputStreamReader(
				httpExchange.getRequestBody()));
		Set<Statement> statements = new HashSet<Statement>();
		BNode bnode = new BNodeImpl("123");
		try {
			String lineUri;
			while ((lineUri = rdfList.readLine()) != null) {
				statements.add(new StatementImpl(bnode,
						new URIImpl("urn:link"), new LiteralImpl(lineUri)));
			}
		} finally {
			rdfList.close();
		}
		// execute query
		logger.debug("Getting executor...");
		Executor ex = this.getExecutor();
		logger.debug("Using Executor " + ex.toString());
		ex.execute(new SetOfStatementsImpl(statements), this.getPathId());
		// send back OK response
		httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		httpExchange.getResponseBody().close();
		logger.info("PushEndpoint Call End");
	}
}
