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
import java.io.IOException;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import eu.larkc.core.query.SPARQLQuery;

/**
 * DataFactory is responsible to abstract the construction of different
 * implementation used by in the API.
 * 
 * @author vassil
 * 
 */
public interface DataFactory {

	/**
	 * LarKC NS
	 */
	public final static String LARKC_NS = "http://larkc.eu/";

	/**
	 * To consider the implementation of DefaultFactory, which to automated the
	 * creation of the different factories.
	 */
	public final static DataFactory INSTANCE = DataFactoryImpl.getInstance();

	/**
	 * Creates a RDF graph that will be passed by value.
	 * 
	 * @param graph
	 *            name (may be null)
	 * @param sts
	 *            collection of the statements
	 * @return the graph
	 */
	public RdfGraph createRdfGraph(Iterable<Statement> sts, URI graph);

	/**
	 * Creates RDF graph from a remote location. If the URI could not be
	 * resolved an exception will be generated during the construction of the
	 * graph.
	 * 
	 * @param uri
	 *            to be resolved
	 * @return the constructed rdf graph
	 */
	public RdfGraph createRemoteRdfGraph(URI uri);

	/**
	 * Creates a connection to local storage.
	 * 
	 * @return a connection to the local store
	 */
	public RdfStoreConnection createRdfStoreConnection();

	/**
	 * Creates a connection to remote SPARQL endpoint. If the URI is not
	 * resolvable or SPARQL enabled service could not be found an exception will
	 * be generated after constructing the object.
	 * 
	 * @param uri
	 * @return sparql endpoint object
	 */
	public SPARQLEndpoint createSPARQLEndpoint(URI uri);

	/**
	 * Create a SPARQL query from a string
	 * 
	 * @param query
	 *            to be parsed
	 * @return executable query by a SPARQL endpoint
	 */
	public SPARQLQuery createSPARQLQuery(String query);

	/**
	 * Creates a SPARQL query from a string to be executed against a specific
	 * labeled group. The labeled group for now could not be specified as part
	 * of the syntax like the dataset in FROM/FROM NAMED clauses.
	 * 
	 * @param query
	 *            to be parsed
	 * @param label
	 *            labeled group to be used (may be null)
	 * @return executable query by a SPARQL endpoint
	 */
	public SPARQLQuery createSPARQLQuery(String query, URI label);

	/**
	 * Creates a SPARQL query from a string to be executed against a specific
	 * labeled group. The labeled group for now could not be specified as part
	 * of the syntax like the dataset in FROM/FROM NAMED clauses.
	 * 
	 * @param query
	 *            to be parsed
	 * @param ns
	 *            default namespace to be used in the expansion of the short
	 *            qname in query
	 * @param label
	 *            labeled group to be used (may be null)
	 * @return executable query by a SPARQL endpoint
	 */
	public SPARQLQuery createSPARQLQuery(String query, String ns, URI label);

	/**
	 * Create RdfGraph from RDF data.
	 * 
	 * @param rdf
	 * @return null or the RdfGraph
	 */
	public RdfGraph createRdfGraph(SetOfStatements rdf);

	/**
	 * Create RdfGraph from RDF data.
	 * 
	 * @param rdf
	 * @param invocationID
	 * @return null or the RdfGraph
	 */
	public RdfGraph createRdfGraph(SetOfStatements rdf, URI invocationID);

	/**
	 * Create SPARQLQuery from RDF data.
	 * 
	 * @param rdf
	 * @return null or the SPARQLQuery
	 */
	public SPARQLQuery createSPARQLQuery(SetOfStatements rdf);

	/**
	 * Create SPARQLQuery from RDF data.
	 * 
	 * @param rdf
	 * @param invocationID
	 * @return null or the SPARQLQuery
	 */
	public SPARQLQuery createSPARQLQuery(SetOfStatements rdf, URI invocationID);

	/**
	 * 
	 * TODO Describe the purpose of this method.
	 * 
	 * @param rdf
	 * @return null or AttributeValueMap
	 */
	public AttributeValueMap createAttributeValueList(SetOfStatements rdf);

	/**
	 * 
	 * TODO Describe the purpose of this method.
	 * 
	 * @param rdf
	 * @param invocationID
	 * @return null or AttributeValueMap
	 */
	public AttributeValueMap createAttributeValueList(SetOfStatements rdf,
			URI invocationID);

	/**
	 * 
	 * TODO Describe the purpose of this method.
	 * 
	 * @param rdf
	 * @return null or variable binding
	 */
	public VariableBinding createVariableBinding(SetOfStatements rdf);

	/**
	 * 
	 * TODO Describe the purpose of this method.
	 * 
	 * @param rdf
	 * @param invocationID
	 * @return null or variable binding
	 */
	public VariableBinding createVariableBinding(SetOfStatements rdf,
			URI invocationID);

	/**
	 * Sets the name of the graph for the given SetOfStatements. Old values are
	 * overwritten
	 * 
	 * @param input
	 * @param newName
	 * @return a graph with the given name and data
	 */
	public RdfGraph setNamedGraph(SetOfStatements input, URI newName);

	/**
	 * TODO Describe the purpose of this method.
	 * 
	 * @param file
	 * @param nameSpace
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws RDFHandlerException
	 * @throws RDFParseException
	 */
	public SetOfStatements parseGraph(File file, String nameSpace)
			throws RDFParseException, RDFHandlerException,
			FileNotFoundException, IOException;

	/**
	 * Extracts all objects from the triples that have the given predicate, may
	 * contain duplicates
	 * 
	 * @param statements
	 * @param predicate
	 * @return
	 */
	public List<String> extractObjectsForPredicate(SetOfStatements statements,
			URI predicate);

	/**
	 * Returns a String printing a SetOfStatements. Calling this method will
	 * consume SetOfStatements that can be read only once (e.g. result sets).
	 * 
	 * @param statements
	 * @return a String printing statements
	 */
	public String printSetOfStatements(SetOfStatements statements);

}
