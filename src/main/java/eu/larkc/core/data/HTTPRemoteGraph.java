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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;

import eu.larkc.core.data.iterator.RDFHandlerIterator;

/**
 * HTTPRemoteGraph is RDF named graph passed by reference. The class could be
 * used to access RDF data published in the web and access via HTTP protocol.
 * The implementation supports a 303 URL redirection.
 * 
 * @author vassil
 * 
 */
public class HTTPRemoteGraph extends RdfGraphBase {

	private final URL url;
	private static final long serialVersionUID = 1L;
	private static ValueFactory vf = new ValueFactoryImpl();

	/**
	 * Constructor
	 * 
	 * @param uri
	 *            is resolveable address that contains the data.
	 */
	public HTTPRemoteGraph(URI uri) {
		super(uri);
		try {
			java.net.URI address = new java.net.URI(uri.stringValue());
			url = address.toURL();
		} catch (URISyntaxException use) {
			throw new IllegalArgumentException("Not a valid URL!", use);
		} catch (MalformedURLException mue) {
			throw new IllegalArgumentException("Malforemd URL!", mue);
		}
	}

	public CloseableIterator<Statement> getStatements() {
		RDFHandlerIterator iter = new RDFHandlerIterator(getName());
		processData(iter);
		return iter;
	}

	public boolean equals(Object o) {
		if (o instanceof HTTPRemoteGraph == false) {
			return false;
		}
		return url.equals(((HTTPRemoteGraph) o).url);
	}

	public int hashCode() {
		return url.hashCode();
	}

	private void processData(RDFHandler handler) {
		RDFParser parser = new RDFXMLParser();
		parser.setValueFactory(vf);
		parser.setRDFHandler(handler);

		try {
			java.net.URLConnection con = url.openConnection();
			con
					.setRequestProperty(
							"Accept",
							"application/rdf+xml, application/xhtml+xml;q=0.3, text/xml;q=0.2, "
									+ "application/xml;q=0.2, text/html;q=0.3, text/plain;q=0.1");
			parser.parse(con.getInputStream(), url.toString());
		} catch (IOException ioe) {
			throw new RuntimeException("Could not load the remote URL file:"
					+ url);
		} catch (RDFParseException re) {
			throw new RuntimeException(
					"Could not extract RDF from the remote URL file!");
		} catch (RDFHandlerException rhe) {
			// Iterator is closed!
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.InformationSet#toRDF(eu.larkc.core.data.SetOfStatements
	 * )
	 */
	@Override
	public SetOfStatements toRDF(SetOfStatements data) {
		try {
			ArrayList<Statement> list = new ArrayList<Statement>();
			list.add(new StatementImpl(ValueFactoryImpl.getInstance()
					.createBNode(), new URIImpl(DataFactory.LARKC_NS
					+ "RDFGraphRemote"), new URIImpl(url.toString())));

			CloseableIterator<Statement> iter = data.getStatements();
			while (iter.hasNext()) {
				Statement s = iter.next();
				URI uri = (URI) s.getContext();
				if (s.getContext() == null) {
					uri = new URIImpl("http://larkc.eu/invocation/last1");
				}
				list.add(new ContextStatementImpl(s.getSubject(), s
						.getPredicate(), s.getObject(), uri));
			}
			return new SetOfStatementsImpl(list);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
