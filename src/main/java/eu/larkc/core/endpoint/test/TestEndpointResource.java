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
package eu.larkc.core.endpoint.test;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.restlet.Application;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.executor.Executor;
import eu.larkc.shared.SerializationHelper;

/**
 * The current TestEndpoint implementations offers two basic methods:
 * <ul>
 * <li>
 * <code>HTTP POST http://host:port/testendpoint?query=QUERY_AS_STRING</code> <br/>
 * passes any query to the corresponding path and starts execution,</li>
 * <li><code>HTTP GET http://host:port/testendpoint</code><br/>
 * retrieves the results as RDF/XML.</li>
 * </ul>
 * 
 * @author Norbert Lanzanasto, Christoph Fuchs
 * 
 */
public class TestEndpointResource extends ServerResource {

	private static Logger logger = LoggerFactory
			.getLogger(TestEndpointResource.class);

	/**
	 * This method executes a query.
	 * 
	 * @param entity
	 *            the entity
	 * @throws Exception
	 *             if the query is empty
	 */
	@Post
	public void executeQuery(Representation entity) throws Exception {
		logger.debug("executeQuery called ...");
		Form form = new Form(entity);
		final String q = form.getFirstValue("query");

		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new Exception("Empty query!");
		}

		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof TestEndpoint) {
			ep = ((TestEndpoint) application).getEndpoint();
			ex = ep.getExecutor();
		}

		assert (ep != null);
		assert (ex != null);

		logger.debug("Found executor " + ex.toString());

		// Prepare a triple which holds the query
		Set<Statement> statementSet = new HashSet<Statement>();
		Resource subject = new BNodeImpl("query");
		URI predicate = WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY;

		Literal object = ValueFactoryImpl.getInstance().createLiteral(q);

		statementSet.add(new StatementImpl(subject, predicate, object));

		// Pass a set of statements containing the query to the executor
		SetOfStatements setOfStatementsImpl = new SetOfStatementsImpl(
				statementSet);

		ex.execute(setOfStatementsImpl, ep.getPathId());
	}

	/**
	 * Returns the results of the workflow.
	 * 
	 * @return the results
	 */
	@Get
	public Representation getResults() {
		logger.debug("getNextResults called ...");

		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof TestEndpoint) {
			ep = ((TestEndpoint) application).getEndpoint();
			ex = ep.getExecutor();
		}

		assert (ep != null);
		assert (ex != null);

		logger.debug("Found executor " + ex.toString());

		// Get the next results
		SetOfStatements nextResults = ex.getNextResults(ep.getPathId());

		// If the result is empty, return an empty rdf/xml document
		if (nextResults == null) {
			return new StringRepresentation("", MediaType.APPLICATION_RDF_XML);
		}

		// Transform the SetOfStatements in an RDF/XML string
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationHelper.printSetOfStatements(nextResults, byteStream,
				RDFFormat.RDFXML);
		String serializedStatements = new String(byteStream.toByteArray());

		// Set mime-type to APP RDF/XML and return result
		Representation rep = new StringRepresentation(serializedStatements,
				MediaType.APPLICATION_RDF_XML);

		return rep;
	}
}
