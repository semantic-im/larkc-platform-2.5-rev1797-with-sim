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

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlQueryRefusedException;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;

/**
 * Feeds SPARQL query to the Executor and parses back the results.
 * 
 * @author Janez Brank, Luka Bradesko, Blaz Fortuna
 */
public class SparqlQueryHandler {

	private Executor ex;
	private Endpoint ep;

	public SparqlQueryHandler(Executor ex, Endpoint ep) {
		this.ep = ep;
		this.ex = ex;
	}

	public static String getExceptionString(Exception e) {
		if (e == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		buf.append(e.toString());
		StackTraceElement[] ste = e.getStackTrace();
		if (ste != null) {
			for (StackTraceElement st : ste) {
				buf.append("\r\n - " + st.toString());
			}
		}
		return buf.toString();
	}

	public SparqlQueryResult handleQuery(SparqlQueryRequest queryRequest)
			throws SparqlException {
		// Parse the query.
		String queryString = queryRequest.getQuery();
		SPARQLQuery query = new SPARQLQueryImpl(queryString);

		return handleParsedQuery(query);
	}

	protected SparqlQueryResult handleParsedQuery(SPARQLQuery query)
			throws SparqlQueryRefusedException, SparqlException {
		// prepare results formatter
		SparqlResultFormatter formatter;
		SparqlQueryResult queryResult = new SparqlQueryResult();
		try {
			formatter = new SparqlResultFormatter();
		} catch (Exception e) {
			throw new SparqlQueryRefusedException(
					"An error occurred while creating the XML formatter for the results of the query (\""
							+ query + "\"): " + e.toString());
		}

		// execute the query
		this.ex.execute(query.toRDF(), ep.getPathId());

		// retrieve results from executor
		SetOfStatements resultsSetOfStatements = ex.getNextResults(ep
				.getPathId());

		// parse the results
		if (query.isSelect()) {
			VariableBinding results = DataFactory.INSTANCE
					.createVariableBinding(resultsSetOfStatements);
			// prepare XML response
			try {
				formatter.buildSparqlRoot();
				formatter.buildSelectResults(results);
			} catch (SparqlException e) {
				throw e;
			} catch (Exception e) {
				throw new SparqlQueryRefusedException(
						"An error occurred while formatting the results of the query (\""
								+ query + "\"): " + getExceptionString(e));
			}
			queryResult.setContentType("application/sparql-results+xml");
		} else if (query.isConstruct() || query.isDescribe()) {
			// prepare XML response
			try {
				formatter.buildRdf(resultsSetOfStatements);
			} catch (SparqlException e) {
				throw e;
			} catch (Exception e) {
				throw new SparqlQueryRefusedException(
						"An error occurred while formatting the results of the query (\""
								+ query + "\"): " + getExceptionString(e));
			}
			queryResult.setContentType("application/rdf+xml");
		} else if (query.isAsk()) {
			// TODO translate SetOfStatements to boolean
			boolean resultBool = false;
			// prepare XML response
			try {
				formatter.buildSparqlRoot();
				formatter.buildAskResults(resultBool);
			} catch (Exception e) {
				throw new SparqlQueryRefusedException(
						"An error occurred while formatting the results of the query (\""
								+ query + "\"): " + getExceptionString(e));
			}
			queryResult.setContentType("application/sparql-results+xml");
		} else {
			throw new SparqlQueryRefusedException(
					"Query type not supported (\"" + query + "\").");
		}

		queryResult.setDocument(formatter.getDocument());
		return queryResult;
	}

	public static String xmlToString(String query, Document doc)
			throws SparqlException {
		try {
			TransformerFactory transFac = TransformerFactory.newInstance();
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
			transFac.setAttribute("indent-number", new Integer(2));
			Transformer trans = transFac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			trans.transform(domSource, streamResult);
			String xmlResult = stringWriter.toString();
			return xmlResult;
		} catch (TransformerException e) {
			throw new SparqlQueryRefusedException(
					"An error occurred while formatting the results of the query (\""
							+ query + "\"): "
							+ SparqlQueryHandler.getExceptionString(e));
		} catch (Exception e) {
			throw new SparqlQueryRefusedException(
					"An error occurred while formatting the results of the query (\""
							+ query + "\"): "
							+ SparqlQueryHandler.getExceptionString(e));
		}
	}
}
