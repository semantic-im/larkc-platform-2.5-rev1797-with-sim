package eu.larkc.core.util;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Set of RDF constants to use in the code
 * 
 * @author spyros
 * 
 */
public class RDFConstants {

	/** Namespace of RDF. */
	public static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	/** Namespace of RDFS. */
	public static final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
	/** Namespace of OWL. */
	public static final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
	/** Namespace of LARKC. */
	public static final String LARKC_NAMESPACE = "http://larkc.eu/schema#";

	/** URI which represents the predicate for type. */
	public static final URI RDF_TYPE = new URIImpl(RDF_NAMESPACE + "type");

	/** URI which represents the predicate for a query. */
	public static final URI LARKC_QUERY = new URIImpl(LARKC_NAMESPACE + "Query");
	/** URI which represents the predicate for a sparql query. */
	public static final URI LARKC_SPARQLQUERY = new URIImpl(LARKC_NAMESPACE
			+ "SPARQLquery");
	/** URI which represents the predicate hasSerializedForm. */
	public static final URI LARKC_HASSERIALIZEDFORM = new URIImpl(
			LARKC_NAMESPACE + "hasSerializedForm");

	/** URI which represents the predicate for attribute value. */
	public static final URI LARKC_ATTVALUE = new URIImpl(LARKC_NAMESPACE
			+ "attvalue");
	/** URI which represents the predicate for attribute value subject. */
	public static final URI LARKC_ATTVALUESUBJECT = new URIImpl(LARKC_NAMESPACE
			+ "serializedSubject");
	/**
	 * URI for the property to specify the name of an argument
	 */
	public static final URI ARGUMENTNAME = new URIImpl(LARKC_NAMESPACE
			+ "hasargumentname");
	/**
	 * URI for the property to specify the binding of an argument
	 */
	public static final URI BINDSTO = new URIImpl(LARKC_NAMESPACE + "bindsto");
	/**
	 * The default name for an argument (utility for plug-ins with a single
	 * argument)
	 */
	public static final URI DEFAULTOUTPUTNAME = new URIImpl(LARKC_NAMESPACE
			+ "defaultoutputname");
}
