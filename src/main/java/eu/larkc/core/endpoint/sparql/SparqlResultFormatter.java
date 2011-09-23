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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.core.endpoint.sparql.exceptions.SparqlQueryRefusedException;

/**
 * This class provides functionality to present the results of a SPARQL query in
 * XML format, as described in http://www.w3.org/2001/sw/DataAccess/rf1/
 * andhttp://www.w3.org/TR/2008/REC-rdf-sparql-XMLres-20080115/
 * 
 * @author janez
 * 
 */
class SparqlResultFormatter {
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Document doc;
	Element root;
	// see http://www.w3.org/2001/sw/DataAccess/rf1/#defn-srd , sec. 2.1
	// public final String sparqlNsPref = "vbr"; // same as used in sec. 1 of
	// the
	public final String sparqlNsPref = "";
	// SPARQL protocol spec
	public final String sparqlNsUri = "http://www.w3.org/2005/sparql-results#";

	public Document getDocument() {
		return doc;
	}

	public SparqlResultFormatter() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		doc = builder.newDocument();
	}

	/**
	 * Creates a root element <sparql> and adds it to the document 'doc'. This
	 * root element is needed in the responses to SELECT and ASK queries (but
	 * not CONSTRUCT or DESCRIBE queries, which use <rdf:RDF>).
	 */
	public void buildSparqlRoot() {
		root = doc.createElementNS(sparqlNsUri, sparqlNsPref + "sparql");
		setXmlnsAttribute(root, sparqlNsPref, sparqlNsUri);
		doc.appendChild(root);
	}

	public org.w3c.dom.Element buildHeadElement(List<String> variableNames) {
		Element head = doc.createElementNS(sparqlNsUri, sparqlNsPref + "head");
		if (variableNames != null)
			for (String variableName : variableNames) {
				Element elt = doc.createElementNS(sparqlNsUri, sparqlNsPref
						+ "variable");
				elt.setAttributeNS(sparqlNsUri, sparqlNsPref + "name",
						variableName);
				head.appendChild(elt);
			}
		return head;
	}

	/**
	 * Fills a <binding> element with content, depending on the RDF term given
	 * by 'value'. This implements sec. 2.3.1 of
	 * http://www.w3.org/2001/sw/DataAccess/rf1/#defn-srd .
	 * 
	 * @param bindingElt
	 *            The XML element to be augmented.
	 * @param value
	 *            The RDF term that we want to represent in XML.
	 * @throws SparqlException
	 *             If 'value' is neither a blank node, nor an IRI, nor a
	 *             literal.
	 */
	void fillBindingElement(Element bindingElt, Value value)
			throws SparqlException {
		if (value instanceof BNode) {
			Element bnode = doc.createElementNS(sparqlNsUri, sparqlNsPref
					+ "bnode");
			BNode i = (BNode) value;
			bnode.appendChild(doc.createTextNode(i.getID()));
			bindingElt.appendChild(bnode);
		} else if (value instanceof URI) {
			Element uri = doc
					.createElementNS(sparqlNsUri, sparqlNsPref + "uri");
			URI i = (URI) value;
			uri.appendChild(doc.createTextNode(i.stringValue()));
			bindingElt.appendChild(uri);
		} else if (value instanceof Literal) {
			Element lit = doc.createElementNS(sparqlNsUri, sparqlNsPref
					+ "literal");

			Literal i = (Literal) value;

			if (i.getDatatype() != null)
				lit.setAttributeNS(sparqlNsUri, sparqlNsPref + "datatype", i
						.getDatatype().stringValue());
			else {
				String lang = i.getLanguage();
				if (lang != null && lang.length() > 0)
					lit.setAttribute("xml:lang", lang);
			}
			lit.appendChild(doc.createTextNode(i.getLabel()));
			bindingElt.appendChild(lit);
		} else
			throw new SparqlQueryRefusedException(
					"Invalid RDF term appears in the result set: claims to be neither an IRI, nor a literal, nor a blank node.");
	}

	/**
	 * Creates a <result> element containing a <binding name="...">...</binding>
	 * child for each value.
	 * 
	 * @param variableNames
	 *            A list of variable names, for use in the "name" attributes.
	 * @param values
	 *            A list of corresponding values. 'values' and 'variableNames'
	 *            should have the same number of elements and in the same order.
	 * @return The new <result> element.
	 * @throws SparqlException
	 *             If thrown by fillBindingElement when a RDF term doesn't say
	 *             what kind of term it is.
	 */
	org.w3c.dom.Element buildResultElement(ArrayList<String> variableNames,
			List<Value> values) throws SparqlException {
		// A binding is a pair (variable, RDF term).
		// http://www.w3.org/TR/rdf-sparql-query/ sec 1.2.3
		// An RDF term can be [ibid. sec. 12.1.1]:
		// - an IRI
		// - a blank node of an RDF graph
		// - an RDF literal, which can be:
		// [http://www.w3.org/TR/2004/REC-rdf-concepts-20040210/#dfn-literal]
		// - a plain literal (= lexical form + optionally a language tag)
		// - a typed literal (= lexical form + a datatype URI)
		// A lexical form, language tag, and datatype URI are unicode strings.
		Element result = doc.createElementNS(sparqlNsUri, sparqlNsPref
				+ "result");
		if (values != null) {
			int i = 0;
			for (Value value : values) {
				// "If, for a particular solution, a variable is unbound, no binding element for that variable is included in the result element."
				// http://www.w3.org/2001/sw/DataAccess/rf1/#defn-srd sec. 2.3.1
				if (value != null) {
					Element binding = doc.createElementNS(sparqlNsUri,
							sparqlNsPref + "binding");
					binding.setAttributeNS(sparqlNsUri, sparqlNsPref + "name",
							variableNames.get(i));
					fillBindingElement(binding, value);
					result.appendChild(binding);
				}
				i++;
			}
		}
		return result;
	}

	/**
	 * Creates an XML <results> element for the results of a SELECT query. The
	 * new element is appended as a child of 'this.root'.
	 * 
	 * @param variableBinding
	 *            The list of results to be converted to XML. If there are
	 *            several VariableBinding instances, they should all be using
	 *            the same set of variables, because we can produce only one
	 *            <head> with the list of variable names in our output.
	 * @throws SparqlException
	 *             may be thrown by fillBindingElement if some RDF term doesn't
	 *             say what kind of term it is
	 */
	public void buildSelectResults(VariableBinding variableBinding)
			throws SparqlException {

		// do nothing when nothing to do
		if (variableBinding == null) {
			return;
		}
		// load variable names
		Element resultsElt = doc.createElementNS(sparqlNsUri, sparqlNsPref
				+ "results");
		ArrayList<String> variableNames = new ArrayList<String>();
		for (String variableName : variableBinding.getVariables())
			variableNames.add(variableName);
		// prepare head of result XML
		Element head = buildHeadElement(variableBinding.getVariables());
		root.appendChild(head);
		// iterate over all the result rows...
		Iterator<VariableBinding.Binding> it = variableBinding.iterator();
		while (it.hasNext()) {
			VariableBinding.Binding binding = it.next();
			// ... and add them to the result XML
			Element result = buildResultElement(variableNames, binding
					.getValues());
			resultsElt.appendChild(result);
		}
		// remember result XML
		root.appendChild(resultsElt);
	}

	/**
	 * Creates an XML <boolean> element containing the value of 'results', and
	 * appends this new element as a child of 'this.root'.
	 * 
	 * @param results
	 *            The value to be stored inside the <boolean> element.
	 */
	public void buildAskResults(boolean results) {
		Element resultsElt = doc.createElementNS(sparqlNsUri, sparqlNsPref
				+ "boolean");
		resultsElt.appendChild(doc.createTextNode(results ? "true" : "false"));
		root.appendChild(resultsElt);
	}

	final String rdfNamespaceUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	final String rdfNamespacePref = "rdf";
	final String xmlns = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;

	// [4] NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] |
	// [#xD8-#xF6] | [#xF8-#x2FF]
	// | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F]
	// | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD]
	// | [#x10000-#xEFFFF]
	public static boolean isNameStartChar(char c) {
		if (c == ':' || ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')
				|| c == '_')
			return true;
		int i = (int) c;
		if (0xc0 <= i && i <= 0x2ff)
			return (c != 0xd7) && (c != 0xf7);
		if (0x370 <= i && i <= 0x1fff)
			return (c != 0x37e);
		if (0x200c <= i && i <= 0x200d)
			return true;
		if (0x2070 <= i && i <= 0x218f)
			return true;
		if (0x2c00 <= i && i <= 0x2fef)
			return true;
		if (0x3001 <= i && i <= 0xd7ff)
			return true;
		if (0xf900 <= i && i <= 0xfdcf)
			return true;
		if (0xfdf0 <= i && i <= 0xfffd)
			return true;
		if (0x10000 <= i && i <= 0xeffff)
			return true;
		return false;
	}

	// [4a] NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 |
	// [#x0300-#x036F] | [#x203F-#x2040]
	public static boolean isNameChar(char c) {
		if (c == '_' || c == '.' || ('0' <= c && c <= '9'))
			return true;
		if (isNameStartChar(c))
			return true;
		int i = (int) c;
		if (i == 0xb7 || (0x300 <= i && i <= 0x36f)
				|| (0x203f <= i && i <= 0x2040))
			return true;
		return false;
	}

	/**
	 * The 'predicate' part of an RDF triple is identified by an IRI or URI; on
	 * the other hand, representing RDF in XML requires us to use the predicate
	 * as the name of an XML element or attribute. Nor every IRI is a valid XML
	 * element/attribute name. This class provides a method that, given a
	 * predicate IRI, looks for the longest suffix that could be an XML name;
	 * the previous part is then treated as a namespace URI, a suitable
	 * namespace prefix is assigned to it, and an 'xmlns:...' attribute is added
	 * to the root element of the XML document.
	 * 
	 * @author janez
	 */
	static class NamespaceManager {
		HashMap<String, String> uriToPrefix;
		Element rootElt; // the element to which we add 'xmlns:...' attributes
		// for the namespaces that we came up with
		int counter; // our namespace prefixes are named 'ns1', 'ns2', etc.

		public NamespaceManager(Element rootElt_) {
			uriToPrefix = new HashMap<String, String>();
			rootElt = rootElt_;
			counter = 0;
		}

		static class QName {
			public String nsUri;
			public String nsPref;
			public String localName;

			public QName(String nsUri_, String nsPref_, String localName_) {
				nsUri = nsUri_;
				nsPref = nsPref_;
				localName = localName_;
			}

			public boolean hasPref() {
				return nsPref != null && nsPref.length() > 0;
			}

			public String getQName() {
				return nsPref + ":" + localName;
			}
		}

		public QName processName(String name) {
			// For the local name, we'll take the longest suffix of 'name'
			// that can be used as the name of an XML attribute or element.
			// The part of the name before that is the namespace URI.
			int n = name.length();
			int i, localFrom = n;
			for (i = n - 1; i >= 0; i--) {
				char c = name.charAt(i);
				if (c == ':')
					break;
				if (isNameStartChar(c)) {
					localFrom = i;
					continue;
				}
				if (!isNameChar(c))
					break;
			}
			String localName = name.substring(localFrom);
			String nsUri = name.substring(0, localFrom);
			// If we don't have a prefix for this URI yet, we'll create one now.
			String nsPref = uriToPrefix.get(nsUri);
			if (nsPref == null) {
				++counter;
				nsPref = "ns" + Integer.toString(counter);
				uriToPrefix.put(nsUri, nsPref);
				// Add an xmlns:... attribute to the root element. This ensures
				// that when we latter use this namespace deeper inside the tree
				// (using setAttributeNS / createElementNS), there won't be any
				// duplicate
				// xmlns:... attributes scattered around the tree.
				setXmlnsAttribute(rootElt, nsPref, nsUri);
			}
			return new QName(nsUri, nsPref, localName);
		}
	}

	public static void setXmlnsAttribute(Element element, String nsPref,
			String nsUri) {
		element.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				javax.xml.XMLConstants.XMLNS_ATTRIBUTE + nsPref, nsUri);
	}

	/**
	 * Creates an <rdf:RDF> element and suitable subelements to store statements
	 * of the given RDF graph. The new <rdf:RDF> element becomes the root of
	 * 'this.doc'.
	 * 
	 * If the terms in the RDF graph report us full URI references, e.g.
	 * "http://purl.org/dc/elements/1.1/#title", this means that we cannot
	 * directly use them as names of XML elements in the output XML document; in
	 * this case we use the NamespaceManager class to generate namespace
	 * prefixes and convert a URI reference like this into a qname such as
	 * "ns1:title", and add 'xmlns:ns1="http://purl.org/dc/elements/1.1/"' to
	 * the root <rdf:RDF> element.
	 * 
	 * ToDo: if, on the other hand, it turns out that the terms in the RDF graph
	 * return, as URI references, strings of the form "dc:title", then we need a
	 * mechanism to find out what base URI each prefix refers to, so that we can
	 * add corresponding 'xmlns:...' attributes to the root <rdf:RDF> element.
	 * 
	 * @param stats
	 * @throws SparqlException
	 */
	public void buildRdf(SetOfStatements stats) throws SparqlException {
		Element rdfElt = doc.createElementNS(rdfNamespaceUri, rdfNamespacePref
				+ ":RDF");
		setXmlnsAttribute(rdfElt, rdfNamespacePref, rdfNamespaceUri);
		root = rdfElt;
		doc.appendChild(rdfElt);
		// rdfElt.setAttribute(xmlns + ":" + rdfNamespacePref, rdfNamespaceUri);
		NamespaceManager nsMgr = new NamespaceManager(rdfElt);

		HashMap<String, Element> iriSubjElts = new HashMap<String, Element>();
		HashMap<String, Element> blankSubjElts = new HashMap<String, Element>();
		HashMap<Pair<String, String>, Attr> iriPropAttrs = new HashMap<Pair<String, String>, Attr>();
		HashMap<Pair<String, String>, Attr> blankPropAttrs = new HashMap<Pair<String, String>, Attr>();

		CloseableIterator<Statement> iter = stats.getStatements();
		while (iter.hasNext()) {
			Statement stat = iter.next();
			Resource subject = stat.getSubject();
			// Create a <rdf:about> element for this subject.
			// If such an element already exists, reuse it.
			Element subjElt;
			String subjStr;
			HashMap<Pair<String, String>, Attr> propAttrs;
			if (subject instanceof URI) {
				subjStr = subject.stringValue();
				subjElt = iriSubjElts.get(subjStr);
				propAttrs = iriPropAttrs;
				if (subjElt == null) {
					subjElt = doc.createElementNS(rdfNamespaceUri,
							rdfNamespacePref + ":Description");
					subjElt.setAttributeNS(rdfNamespaceUri, rdfNamespacePref
							+ ":about", subjStr);
					rdfElt.appendChild(subjElt);
					iriSubjElts.put(subjStr, subjElt);
				}
			} else if (subject instanceof BNode) {
				subjStr = ((BNode) subject).getID();
				subjElt = blankSubjElts.get(subjStr);
				propAttrs = blankPropAttrs;
				if (subjElt == null) {
					subjElt = doc.createElementNS(rdfNamespaceUri,
							rdfNamespacePref + ":Description");
					subjElt.setAttributeNS(rdfNamespaceUri, rdfNamespacePref
							+ ":nodeID", subjStr);
					rdfElt.appendChild(subjElt);
					blankSubjElts.put(subjStr, subjElt);
				}
			} else
				throw new SparqlQueryRefusedException(
						"The query resulted in an invalid RDF graph.  A subject claims to be neither an IRI node nor a blank node.");
			// Determine the name of the predicate.
			// - ToDo: if this predicate appears just once for this subject,
			// and if the corresponding object is a plain literal with the same
			// language as the parent element, then it could be represented by
			// an attribute rather than a subelement.
			URI predicate = stat.getPredicate();
			String predStr;
			predStr = predicate.stringValue();
			// if (predicate.isIRI())
			// predStr = ((RdfIriNode) predicate).getIRI();
			// else
			// throw new
			// SparqlQueryRefusedException("The query resulted in an invalid RDF graph.  A predicate claims to be something else than an IRI.");
			NamespaceManager.QName predQName = nsMgr.processName(predStr);
			// Do we need a subelement for this predicate? If the object is
			// a plain literal without a language tag, we can make in an
			// attribute instead of
			// a subelement. However, only one attribute with this name can
			// exist
			// for a given subject element.
			Value object = stat.getObject();
			Literal objLiteral = (object instanceof Literal) ? ((Literal) object)
					: null;
			Pair<String, String> key = new Pair<String, String>(subjStr,
					predStr);
			boolean canBeAttr = false;
			if ((!propAttrs.containsKey(key)) && objLiteral != null
					&& !(objLiteral.getDatatype() != null)) {
				String langTag = objLiteral.getLanguage();
				if (langTag == null || langTag.length() <= 0)
					canBeAttr = true;
			}
			// If the new statement can be represented by an attribute, add it
			// now.
			if (canBeAttr) {
				Attr attrNode;
				if (predQName.hasPref())
					attrNode = doc.createAttributeNS(predQName.nsUri, predQName
							.getQName());
				else
					attrNode = doc.createAttribute(predQName.localName);
				attrNode.setValue(((Literal) object).getLanguage());
				// subjElt.appendChild(attrNode);
				if (predQName.hasPref())
					subjElt.setAttributeNodeNS(attrNode);
				else
					subjElt.setAttributeNode(attrNode);
				propAttrs.put(key, attrNode);
			}
			// Otherwise, we'll create a new subelement.
			else {
				Element predElt;
				if (predQName.hasPref())
					predElt = doc.createElementNS(predQName.nsUri, predQName
							.getQName());
				else
					predElt = doc.createElement(predQName.localName);
				subjElt.appendChild(predElt);
				// Now store the object information in the new subelement.
				if (object instanceof URI) {
					String objIri = ((URI) object).stringValue();
					predElt.setAttributeNS(rdfNamespaceUri, rdfNamespacePref
							+ ":resource", objIri);
				} else if (object instanceof BNode) {
					String objId = ((BNode) object).getID();
					predElt.setAttributeNS(rdfNamespaceUri, rdfNamespacePref
							+ ":nodeID", objId);
				} else if (object instanceof Literal) {
					Literal objLit = (Literal) object;
					if (objLit.getDatatype() != null)
						predElt.setAttributeNS(rdfNamespaceUri,
								rdfNamespacePref + ":datatype", objLit
										.getDatatype().stringValue());
					else {
						String langTag = objLit.getLanguage();
						boolean hasLang = (langTag != null && langTag.length() > 0);
						boolean knownBefore = propAttrs.containsKey(key);
						if (!(hasLang || knownBefore))

							predElt.setAttribute("xml:lang", langTag);
					}
					predElt.appendChild(doc.createTextNode(objLit.getLabel()));
				} else
					throw new SparqlQueryRefusedException(
							"The query resulted in an invalid RDF graph.  An object claims to be something else than an IRI node, blank node, or literal.");
			}
		}
		iter.close();
	}
}

class Pair<Left, Right> {

	private final Left left;
	private final Right right;

	public Right getRight() {
		return right;
	}

	public Left getLeft() {
		return left;
	}

	public Pair(final Left left_, final Right right_) {
		left = left_;
		right = right_;
	}

	public static <A, B> Pair<A, B> create(A left, B right) {
		return new Pair<A, B>(left, right);
	}

	public final boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		final Pair<?, ?> other = (Pair<?, ?>) o;
		return equal(getLeft(), other.getLeft())
				&& equal(getRight(), other.getRight());
	}

	public static final boolean equal(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		else
			return o1.equals(o2);
	}

	public int hashCode() {
		int a = getLeft() == null ? 0 : getLeft().hashCode();
		int b = getRight() == null ? 0 : getRight().hashCode();

		long s = ((long) a) + b;
		s = ((s * (s + 1)) >> 1) + a;
		s %= 0x7fffffffL;
		if (s < 0)
			s = -s;
		if (s < 0)
			s = 0;
		return (int) s;
	}
}
