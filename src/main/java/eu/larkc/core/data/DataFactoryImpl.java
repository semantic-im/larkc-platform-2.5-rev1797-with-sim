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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.ntriples.NTriplesParser;
import org.openrdf.rio.trig.TriGParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.query.VariableBindingBase;
import eu.larkc.core.util.RDFConstants;

/**
 * This is a dummy factory to abstract ORDI creation specifics.
 * 
 * @author vassil
 * 
 */
public class DataFactoryImpl implements DataFactory {

	private static Logger logger = LoggerFactory.getLogger(DataFactory.class);
	public static String REFERENCE_TYPES_CACHE_TIME_PROP = "eu.larkc.core.data.reference-type-cache-time";
	public static long REFERENCE_TYPES_CACHE_TIME = 1000 * 60;

	private DataFactoryImpl() {
		initReferenceTypeCache();
	}

	public synchronized static DataFactoryImpl getInstance() {
		return new DataFactoryImpl();
	}

	/**
	 * TODO Gets VariableBindings for the SetOfStatements FIXME: This is using
	 * the cheating object store
	 * 
	 * @param sts
	 * @return
	 */
	public VariableBindingBase createVariableBindingBase(SetOfStatements sts) {
		CloseableIterator<Statement> it = sts.getStatements();
		while (it.hasNext()) {
			Statement s = it.next();
			if (s.getSubject().equals(VariableBindingBase.vbbBNode)
					&& s.getPredicate()
							.equals(VariableBindingBase.vbbPredicate)) {
				return (VariableBindingBase) CheatingInMemoryObjectStore
						.getInstance().get(s.getObject().stringValue());
			}
		}
		return null;
	}

	/**
	 * Creates a RDF graph that will be passed by value.
	 * 
	 * @param graph
	 *            name (may be null)
	 * @param sts
	 *            collection of the statements
	 * @return the graph
	 */
	public RdfGraph createRdfGraph(Iterable<Statement> sts, URI graph) {
		return new RdfGraphInMemory(graph, sts);
	}

	/**
	 * Creates RDF graph from a remote location. If the URI could not be
	 * resolved an exception will be generated during the construction of the
	 * graph.
	 * 
	 * @param uri
	 *            to be resolved
	 * @return the constructed rdf graph
	 */
	public RdfGraph createRemoteRdfGraph(URI uri) {
		return new HTTPRemoteGraph(uri);
	}

	/**
	 * Creates a connection to local storage.
	 * 
	 * @return a connection to the local store
	 */
	public RdfStoreConnection createRdfStoreConnection() {
		if (DataLayerService.getORDI() == null) {
			throw new UnsupportedOperationException(
					"Cannot access remote data layer instance!");
		}

		return new RdfStoreConnectionImpl(DataLayerService.getORDI()
				.getConnection());
	}

	/**
	 * Create a connection to remote SPARQL endpoint. If the URI is not
	 * resolvable or SPARQL enabled service could not be found an exception will
	 * be generated after constructing the object.
	 * 
	 * @param uri
	 * @return
	 */
	public SPARQLEndpoint createSPARQLEndpoint(URI uri) {
		throw new UnsupportedOperationException(
				"The execution of remote SPARQL endpoints "
						+ "is still not supported! Please use "
						+ "createRdfStoreConnection() method instead!");
	}

	public SPARQLQuery createSPARQLQuery(String query) {
		return new SPARQLQueryImpl(query);
	}

	public SPARQLQuery createSPARQLQuery(String query, URI label) {
		SPARQLQuery sparql = createSPARQLQuery(query);
		sparql.setLabelledGroup(label);
		return sparql;
	}

	public SPARQLQuery createSPARQLQuery(String query, String ns, URI label) {
		SPARQLQuery sparql = new SPARQLQueryImpl(query, ns);
		sparql.setLabelledGroup(label);
		return sparql;
	}

	private void initReferenceTypeCache() {
		String ct = System.getProperty(REFERENCE_TYPES_CACHE_TIME_PROP);
		try {
			REFERENCE_TYPES_CACHE_TIME = Long.parseLong(ct);
			logger.info(String
					.format("New value (%d) for %s has been set!",
							REFERENCE_TYPES_CACHE_TIME,
							REFERENCE_TYPES_CACHE_TIME_PROP));
		} catch (NumberFormatException nfe) {
			if (ct != null) {
				logger.warn(String.format(
						"Invalid set value for %s. Keep using the value %d!",
						REFERENCE_TYPES_CACHE_TIME_PROP,
						REFERENCE_TYPES_CACHE_TIME));
			}
		}
	}

	/**
	 * NEW METHODS
	 */
	public RdfGraph createRdfGraph(SetOfStatements rdf) {
		return createRdfGraph(rdf, null);
	}

	public RdfGraph createRdfGraph(SetOfStatements rdf, URI invocationID) {
		CloseableIterator<Statement> i = rdf.getStatements();
		final ArrayList<Statement> list = new ArrayList<Statement>();
		RdfGraph result = null;

		while (i.hasNext()) {
			Statement s = i.next();

			if ((s.getContext() == null && invocationID == null)
					|| (s.getContext() != null && invocationID != null && s
							.getContext().equals(invocationID))) {
				// success
			} else {
				continue;
			}

			if (s.getPredicate().stringValue()
					.equals(LARKC_NS + "RDFGraphInMemory")) {
				StringReader sr = new StringReader(s.getObject().stringValue());
				TriGParser parser = new TriGParser();
				parser.setRDFHandler(new RDFHandlerBase() {
					public void handleStatement(Statement arg0)
							throws RDFHandlerException {
						list.add(arg0);
					}

				});
				try {
					parser.parse(sr, LARKC_NS);
					result = createRdfGraph(list, ((URI) list.get(0)
							.getContext()));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else if (s.getContext() == null
					&& s.getPredicate().stringValue()
							.equals(LARKC_NS + "RDFGraphRemote")
					&& s.getObject() instanceof URI) {
				result = createRemoteRdfGraph((URI) s.getObject());
			}
		}

		return result;
	}

	public SPARQLQuery createSPARQLQuery(SetOfStatements rdf) {
		return createSPARQLQuery(rdf, null);

	}

	public SPARQLQuery createSPARQLQuery(SetOfStatements rdf, URI invocationID) {
		CloseableIterator<Statement> i = rdf.getStatements();
		while (i.hasNext()) {
			Statement s = i.next();

			if ((s.getContext() == null && invocationID == null)
					|| (s.getContext() != null && invocationID != null && s
							.getContext().equals(invocationID))) {
				// success
			} else {
				continue;
			}

			if (s.getPredicate().equals(RDFConstants.LARKC_HASSERIALIZEDFORM)) {
				return createSPARQLQuery(s.getObject().stringValue()); // FIXME
				// this
				// is
				// not
				// compatible
				// with
				// the
				// toRDF
				// method
				// of
				// SPARQLQuery
			}
		}

		return null;
	}

	/**
	 * ------ Methods to handle arguments as Attribute/value lists. TODO: They
	 * are slightly evil from a KR perspective.
	 */

	public AttributeValueMap createAttributeValueList(SetOfStatements rdf,
			URI invocationID) {
		AttributeValueMap r = new AttributeValueMap();

		CloseableIterator<Statement> i = rdf.getStatements();
		while (i.hasNext()) {
			Statement s = i.next();

			if ((s.getContext() == null && invocationID == null)
					|| (s.getContext() != null && invocationID != null && s
							.getContext().equals(invocationID))) {
				// success
			} else {
				continue;
			}

			if (s.getPredicate().equals(RDFConstants.LARKC_ATTVALUE)) {
				String[] f = s.getObject().stringValue().split("->", 2);
				r.put(f[0], f[1]);
			}
		}

		return r;
	}

	@Override
	public AttributeValueMap createAttributeValueList(SetOfStatements rdf) {
		return createAttributeValueList(rdf, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.DataFactory#createVariableBinding(eu.larkc.core.data
	 * .SetOfStatements)
	 */
	@Override
	public VariableBinding createVariableBinding(SetOfStatements rdf) {
		CloseableIterator<Statement> iter = rdf.getStatements();
		while (iter.hasNext()) {
			Statement s = iter.next();
			if (s.getPredicate().equals(
					new URIImpl(DataFactory.LARKC_NS + "VariableBinding"))) {
				if (s.getObject() instanceof VariableBindingValue) {
					return ((VariableBindingValue) s.getObject()).v;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.DataFactory#createVariableBinding(eu.larkc.core.data
	 * .SetOfStatements, org.openrdf.model.URI)
	 */
	@Override
	public VariableBinding createVariableBinding(SetOfStatements rdf,
			URI invocationID) {
		CloseableIterator<Statement> iter = rdf.getStatements();
		while (iter.hasNext()) {
			Statement s = iter.next();
			if (s.getPredicate()
					.stringValue()
					.equals(new URIImpl(DataFactory.LARKC_NS
							+ "VariableBindingImpl"))
					&& (invocationID == null || invocationID.equals(s
							.getContext()))) {
				if (s.getObject() instanceof VariableBindingValue) {
					return ((VariableBindingValue) s.getObject()).v;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.data.DataFactory#changeNamedGraph(eu.larkc.core.data.
	 * SetOfStatements)
	 */
	@Override
	public RdfGraph setNamedGraph(SetOfStatements input, URI newName) {
		ArrayList<Statement> sts = new ArrayList<Statement>();
		CloseableIterator<Statement> it = input.getStatements();
		while (it.hasNext()) {
			Statement s = it.next();
			sts.add(new StatementImpl(s.getSubject(), s.getPredicate(), s
					.getObject()));
		}
		return new RdfGraphInMemory(newName, sts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.data.DataFactory#parseGraph(java.io.File,
	 * java.lang.String)
	 */
	@Override
	public SetOfStatements parseGraph(File file, String nameSpace)
			throws RDFParseException, RDFHandlerException,
			FileNotFoundException, IOException {

		final ArrayList<Statement> sts = new ArrayList<Statement>();

		RDFParser parser = new NTriplesParser();
		parser.setRDFHandler(new RDFHandlerBase() {
			public void handleStatement(Statement s) {
				sts.add(new StatementImpl(s.getSubject(), s.getPredicate(), s
						.getObject()));
			}
		});

		parser.parse(new FileReader(file), "defaultNS");

		return new SetOfStatementsImpl(sts);
	}

	/**
	 * Extracts all objects from the triples that have the given predicate, may
	 * contain duplicates
	 * 
	 * @param statements
	 * @param predicate
	 * @return
	 */
	@Override
	public List<String> extractObjectsForPredicate(SetOfStatements statements,
			URI predicate) {
		CloseableIterator<Statement> s = statements.getStatements();

		List<String> objects = new ArrayList<String>();
		while (s.hasNext()) {
			Statement stmt = s.next();
			if (stmt.getPredicate().equals(predicate)) {
				objects.add(stmt.getObject().stringValue());
			}
		}
		s.close();
		return objects;
	}

	/**
	 * Returns a String printing a SetOfStatements. Calling this method will
	 * consume SetOfStatements that can be read only once (e.g. result sets).
	 * 
	 * @param statements
	 * @return a String printing statements
	 */
	public String printSetOfStatements(SetOfStatements statements) {
		CloseableIterator<Statement> it = statements.getStatements();
		StringBuffer buf = new StringBuffer();
		while (it.hasNext())
			buf.append(it.next() + "\n");
		it.close();
		return buf.toString();
	}

}
