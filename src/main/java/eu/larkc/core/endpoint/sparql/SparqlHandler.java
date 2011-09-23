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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.endpoint.sparql.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlQueryRefusedException;
import eu.larkc.core.executor.Executor;

/**
 * Simple HTTP Server Handler which is routed to SPARQLProtocol servlet
 * 
 * @author Janez Brank, Luka Bradesko, Blaz Fortuna
 */
public class SparqlHandler extends Endpoint {

	private static Logger logger = LoggerFactory.getLogger(SparqlHandler.class);

	/**
	 * Constructor. The endpoint has to know which {@link Executor} is
	 * responsible for it, hence an Executor has to be passed as an argument.
	 * 
	 * @param ex
	 *            The Executor responsible for this SPARQL endpoint
	 */
	public SparqlHandler(Executor ex) {
		super(ex, "/sparql");
	}

	/**
	 * Parses an URL-encoded SPARQL query request, as described in sec. 2.2 of
	 * the SPARQL Protocol for RDF.
	 * 
	 * @param url
	 *            The URL to be used in error messages.
	 * @param query
	 *            The query part a URL, which is to be parsed.
	 * @return An instance of SparqlQueryRequest containing data extracted from
	 *         the URL.
	 * @exception MalformedSparqlQueryException
	 *                Thrown if the URL is malformed or does not contains
	 *                exactly one 'query' parameter.
	 */
	protected SparqlQueryRequest parseGetUrl(java.net.URI url, String query)
			throws SparqlException {
		String[] parts = query.split("&");
		if (parts == null) {
			throw new MalformedSparqlQueryException(
					"The query part of the URL of the GET request is empty.");
		}
		SparqlQueryRequest qr = new SparqlQueryRequest();
		boolean hasQuery = false;
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL \"" + url.toString() + "\".");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new MalformedSparqlQueryException(e.toString());
			}
			if (name.equals("query")) {
				if (hasQuery) {
					throw new MalformedSparqlQueryException(
							"More than one value of the \""
									+ name
									+ "\" parameter is being provided (first \""
									+ qr.getQuery() + "\", then \"" + value
									+ "\").");
				}
				hasQuery = true;
				qr.setQuery(value);
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else {
				throw new MalformedSparqlQueryException(
						"Unknown parameter name: \""
								+ name
								+ "\"; should be \"query\", \"default-graph-uri\" or \"named-graph-uri\".");
			}
		}
		if (!hasQuery) {
			throw new MalformedSparqlQueryException(
					"The required parameter \"query\" is missing.");
		}
		return qr;
	}

	/**
	 * Sends a response with the given HTTP code and the given contents (encoded
	 * using UTF-8).
	 */
	protected void sendResponse(HttpExchange httpExchange, int httpCode,
			String contentType, String text) throws IOException {
		byte[] bytes;
		try {
			bytes = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			bytes = text.getBytes();
		}
		Headers headers = httpExchange.getResponseHeaders();
		headers.add("Content-Type", contentType);
		httpExchange.sendResponseHeaders(httpCode, bytes.length);
		OutputStream os = httpExchange.getResponseBody();
		os.write(bytes);
		os.close();
	}

	/**
	 * Sends a text/plain response with the given HTTP code and the given
	 * contents (encoded using UTF-8).
	 */
	protected void sendErrorResponse(HttpExchange httpExchange, int httpCode,
			String text) throws IOException {

		sendResponse(httpExchange, httpCode, "text/plain; charset=UTF-8", text);
	}

	/**
	 * Implementation of the method required to implement the HttpHandler
	 * interface.
	 * 
	 * @param httpExchange
	 *            Single-exchange HTTP request/response.
	 */
	public void handle(HttpExchange httpExchange) throws IOException {

		SparqlQueryRequest queryRequest = null;

		try {
			String method = httpExchange.getRequestMethod();
			java.net.URI uri = httpExchange.getRequestURI();
			logger.info(method + " " + uri.toString() + " "
					+ httpExchange.getProtocol());

			if (method.equals("GET")) {
				// FIXME no query is passed with HTTP GET
				// results must be retrieved from the executor and returned

				// Read, and discard, the request body; normally, it should be
				// empty anyway.
				InputStream is = httpExchange.getRequestBody();
				int count = 0;
				byte[] buf = new byte[8192];
				while ((count = is.read(buf)) > 0) {
					logger.error("SPARQLHandler.handle: Unexpected " + count
							+ " bytes read from the body of a GET request.");
				}
				is.close();

				logger.debug("URI: {}", uri);
				logger.debug("query: {}", uri.getRawQuery());
				queryRequest = parseGetUrl(uri, uri.getRawQuery());
			} else if (method.equals("POST")) {
				Headers headers = httpExchange.getRequestHeaders();
				if (!headers.containsKey("Content-Type")) {
					throw new MalformedSparqlQueryException(
							"The Content-Type header is missing.");
				}
				String contentType = headers.getFirst("Content-Type");
				// ToDo: in principle, contentType might also equal
				// "application/x-www-form-urlencoded; encoding=UTF-8" and it
				// would still be valid.
				if (!contentType.contains("application/x-www-form-urlencoded")) {
					throw new MalformedSparqlQueryException(
							"Unsupported Content-Type in the HTTP request: \""
									+ contentType
									+ "\".  Only application/x-www-form-urlencoded is supported.");
				}

				// Read the body of the request.
				InputStream is = httpExchange.getRequestBody();
				StringBuilder reqBody = new StringBuilder();
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				final int capacity = 8192;
				char[] buf = new char[capacity];
				int count;
				while ((count = isr.read(buf, 0, capacity)) > 0) {
					reqBody.append(buf, 0, count);
				}
				isr.close();

				queryRequest = parseGetUrl(uri, reqBody.toString());
			} else {
				sendErrorResponse(httpExchange,
						HttpURLConnection.HTTP_BAD_METHOD,
						"Unsupported request method: \"" + method + "\".");
				return;
			}

			SparqlQueryResult queryResult = handleQueryRequest(queryRequest);

			String xmlResult = SparqlQueryHandler.xmlToString(
					queryRequest.getQuery(), queryResult.getDocument());
			sendResponse(httpExchange, HttpURLConnection.HTTP_OK,
					queryResult.getContentType(), xmlResult);
		} catch (MalformedSparqlQueryException e) {
			// See the example in sec. 2.2.1.9 of the SPARQL Procotol for RDF.
			sendErrorResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST,
					"MalformedQuery: " + e.getMessage());
		} catch (SparqlQueryRefusedException e) {
			// See the example in sec. 2.2.1.10 of the SPARQL Procotol for RDF.
			logger.debug("SparqlQueryRefusedException: " + e.getMessage());
			sendErrorResponse(httpExchange,
					HttpURLConnection.HTTP_INTERNAL_ERROR,
					"QueryRequestRefused: " + e.getMessage());
		} catch (Exception e) {
			logger.debug("EXCEPTION: {}", e.getMessage());
			e.printStackTrace();
			sendErrorResponse(httpExchange,
					HttpURLConnection.HTTP_INTERNAL_ERROR,
					SparqlQueryHandler.getExceptionString(e));
		}
	}

	protected SparqlQueryResult handleQueryRequest(
			SparqlQueryRequest queryRequest) throws SparqlException {
		SparqlQueryHandler handler = new SparqlQueryHandler(getExecutor(), this);
		SparqlQueryResult queryResult = handler.handleQuery(queryRequest);
		return queryResult;
	}
}
