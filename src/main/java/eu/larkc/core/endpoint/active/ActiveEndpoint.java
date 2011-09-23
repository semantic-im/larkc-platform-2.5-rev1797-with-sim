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
package eu.larkc.core.endpoint.active;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.data.VariableBinding.Binding;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.endpoint.push.PushEndpoint;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.query.SPARQLQueryImpl;

/**
 * Custom Endpoint for ACTIVE project (will be moved out when we support
 * pluggable endpoints
 * 
 * @author Luka Bradesko
 * 
 */
public class ActiveEndpoint extends Endpoint {

	private static Logger logger = LoggerFactory.getLogger(PushEndpoint.class);
	private static String HTML_PART1 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> "
			+ "<title>Collected Appointments</title>"
			+ "<style type=\"text/css\">"
			+ ".err {"
			+ "	color: #F00;"
			+ "}"
			+ ".ok { "
			+ "	color: #0A0; "
			+ "}"
			+ "</style>"
			+ "</head>"
			+ "<body>" + "<h1> List of collected appointments</h1>";

	/**
	 * Constructor. The endpoint has to know in advance which {@link Executor}
	 * is responsible for incoming queries.
	 * 
	 * @param ex
	 *            the {@link Executor} responsible for this endpoint
	 */
	public ActiveEndpoint(Executor ex) {
		super(ex, "/active");
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		logger.info("Active endpoint Call Start");

		// execute query
		logger.debug("Getting executor...");
		Executor ex = this.getExecutor();
		logger.debug("Using Executor " + ex.toString());

		// ex.execute(new SetOfStatementsImpl(statements), this.getPathId());
		ex.execute(new SetOfStatementsImpl(), this.getPathId());

		SetOfStatements st = ex.getNextResults(this.getPathId());

		StringBuffer sResponse = new StringBuffer();
		sResponse.append(HTML_PART1 + generateEvents(st) + "</body></html>");

		Headers responseHeaders = httpExchange.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/html");
		httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
				sResponse.length());
		// send back OK response
		httpExchange.getResponseBody().write(sResponse.toString().getBytes());
		httpExchange.getResponseBody().close();
		logger.info("PushEndpoint Call End");
	}

	/**
	 * Reads the statements and generates the html events
	 * 
	 * @param st
	 * @return
	 */
	private String generateEvents(SetOfStatements st) {
		// Hashtable<String, String> events = new Hashtable<String, String>();
		String sResponse = "";
		RdfStoreConnection con = DataFactory.INSTANCE
				.createRdfStoreConnection();
		VariableBinding vb = con
				.executeSelect(new SPARQLQueryImpl(
						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
								+ "PREFIX cyc:<http://example.com/cyc#>"
								+ "PREFIX tripit:<http://example.com/TripitPlugin/tripit#>"
								+ "PREFIX time:<http://www.w3.org/2006/time#>"
								+ "SELECT DISTINCT ?NAME " + "WHERE"
								+ "{ ?P rdf:type cyc:event."
								+ "  ?P cyc:eventHasLocation ?LOC."
								+ "  ?P cyc:preferredNameString ?NAME."
								+ "  ?P cyc:starts ?START."
								+ "  ?P cyc:ends ?END." + "}"));
		CloseableIterator<Binding> iter = vb.iterator();
		int iPersonNum = 0;
		while (iter.hasNext()) {
			Binding b = iter.next();
			List<Value> values = b.getValues();
			// String location = values.get(0).toString().replaceAll("\"", "");
			String eventName = values.get(0).toString().replaceAll("\"", "");
			// String starts = values.get(2).toString().replaceAll("\"", "");
			// String ends = values.get(3).toString().replaceAll("\"", "");

			/*
			 * sResponse += eventName + " in " + location + " (" + starts +
			 * " - " + ends + ") </br>";
			 */

			sResponse += "<Strong>" + eventName + "</Strong>";
			CloseableIterator<Statement> checkEventNode = con.search(null,
					RDF.URI_PREFERRED_NAME_STRING, (Literal) values.get(0),
					null, null);
			if (checkEventNode.hasNext()) {
				BNode trip = (BNode) checkEventNode.next().getSubject();
				CloseableIterator<Statement> checkLoc = con.search(trip,
						RDF.URI_EVENT_HASLOCATION, null, null, null);
				if (checkLoc.hasNext())
					sResponse += " in "
							+ checkLoc.next().getObject().toString()
									.replaceAll("\"", "");
			}

			sResponse += " </br>";

			VariableBinding vbAttendees = con
					.executeSelect(new SPARQLQueryImpl(
							"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
									+ "PREFIX cyc:<http://example.com/cyc#>"
									+ "SELECT ?OWNER ?P WHERE" + "{ "
									+ " ?P rdf:type cyc:event."
									+ " ?P cyc:preferredNameString \""
									+ eventName + "\"."
									+ " ?P cyc:eventHasOwner ?OWNER.}"));
			CloseableIterator<Binding> iterAtendees = vbAttendees.iterator();
			// all events with the same name

			while (iterAtendees.hasNext()) {
				boolean bThisOneHaveErr = false;

				Binding bOwner = iterAtendees.next();
				BNode btrip = (BNode) bOwner.getValues().get(1);
				sResponse += " - "
						+ bOwner.getValues().get(0).toString()
								.replaceAll("\"", "") + " <img src=\"niceimage"
						+ iPersonNum + ".png\" width=\"14\" height=\"14\" /> ";

				// get the lodging info
				VariableBinding vbLodging = con
						.executeSelect(new SPARQLQueryImpl(
								"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
										+ "PREFIX cyc:<http://example.com/cyc#>"
										+ "SELECT ?LODGING ?LNAME WHERE"
										+ "{"
										+ " ?P rdf:type cyc:event."
										+ " ?P cyc:preferredNameString \""
										+ eventName
										+ "\"."
										+ " ?P cyc:eventHasOwner "
										+ bOwner.getValues().get(0).toString()
										+ "."
										+ " ?LODGING cyc:preferredNameString ?LNAME."
										+ " ?P cyc:ProvidingLodging ?LODGING.}"));

				CloseableIterator<Binding> iterLodging = vbLodging.iterator();
				if (iterLodging.hasNext()) {
					while (iterLodging.hasNext()) {
						sResponse += okResponse(iterLodging.next().getValues()
								.get(1).toString().replaceAll("\"", ""));
					}
				} else {
					bThisOneHaveErr = true;
					sResponse += errResponse(" Lodging missing");
				}

				/*
				 * CloseableIterator<Statement> checklodging = con.search(btrip,
				 * RDF.URI_PROVIDING_LODGING, null, null, null); if
				 * (checklodging.hasNext()) { Statement stt =
				 * checklodging.next();
				 * System.out.println("SSSSSSSSSSSSSSSSSSSSS: " +
				 * stt.getSubject() + "  " + stt.getPredicate() + "  " +
				 * stt.getObject()); sResponse +=
				 * okResponse(stt.getObject().stringValue()); } else {
				 * bThisOneHaveErr = true; sResponse +=
				 * errResponse(" Lodging missing"); }
				 */

				CloseableIterator<Statement> checkflight = con.search(btrip,
						RDF.URI_SUBEVENTS, null, null, null);
				if (checkflight.hasNext()) {
					sResponse += okResponse(", Flight status OK");
				} else {
					bThisOneHaveErr = true;
					sResponse += errResponse(", Flight information missing");
				}

				sResponse += "</br>";

				if (bThisOneHaveErr) {
					String sReplace = "niceimage" + iPersonNum++;
					sResponse = sResponse
							.replaceAll(sReplace,
									"http://www.clker.com/cliparts/a/x/q/L/G/9/delete-icon-th");
				} else {
					String sReplace = "niceimage" + iPersonNum++;
					sResponse = sResponse
							.replaceAll(sReplace,
									"http://www.clker.com/cliparts/U/b/3/E/T/z/ok-icon-th");
				}
			}

			sResponse += "</br>";
		}
		/*
		 * String sResponse = ""; CloseableIterator<Statement> it =
		 * st.getStatements(); while (it.hasNext()) { Statement statement =
		 * it.next(); if (statement.getPredicate().equals(RDFConstants.RDF_TYPE)
		 * && statement.getObject().equals(RDF.URI_EVENT)) { sResponse +=
		 * it.next().getObject().toString() + " in "; }
		 * 
		 * 
		 * if(statement.getPredicate().equals(new
		 * URIImpl("http://credentials#username")))
		 * username=st.getObject().stringValue(); else
		 * if(st.getPredicate().equals(new
		 * URIImpl("http://credentials#password")))
		 * password=st.getObject().stringValue();
		 * 
		 * // sResponse += statement.toString() + "\n"; }
		 */

		return sResponse;
	}

	private String errResponse(String sString) {
		return "<span class=\"err\">" + sString + "</span>";
	}

	private String okResponse(String sString) {
		return "<span class=\"ok\">" + sString + "</span>";
	}
}
