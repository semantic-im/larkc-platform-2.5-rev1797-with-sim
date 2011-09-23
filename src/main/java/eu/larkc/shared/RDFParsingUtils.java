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
package eu.larkc.shared;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.n3.N3ParserFactory;
import org.openrdf.rio.rdfxml.RDFXMLParserFactory;
import org.openrdf.rio.turtle.TurtleParserFactory;

/**
 * Utility class which uses org.openrdf.rio methods to parse various RDF
 * formats.
 * 
 * @author Christoph Fuchs, Norbert Lanzanasto
 * 
 */
public class RDFParsingUtils {

	/**
	 * This method parses a string in RDF/XML format and constructs a
	 * collections of statements.
	 * 
	 * @param workflow
	 *            the string representing the workflow in RDF/XML
	 * @return collection of statements
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static Collection<Statement> parseXML(String workflow)
			throws RDFParseException, RDFHandlerException, IOException {

		RDFXMLParserFactory rdfxmlParserFactory = new RDFXMLParserFactory();
		RDFParser parser = rdfxmlParserFactory.getParser();
		return extractStatements(workflow, parser);
	}

	/**
	 * This method parses a string in N3 notation and constructs a collections
	 * of statements.
	 * 
	 * @param workflow
	 *            the string representing the workflow in N3
	 * @return collection of statements
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static Collection<Statement> parseN3(String workflow)
			throws RDFParseException, RDFHandlerException, IOException {

		N3ParserFactory n3ParserFactory = new N3ParserFactory();
		RDFParser parser = n3ParserFactory.getParser();
		return extractStatements(workflow, parser);
	}

	/**
	 * This method parses a string in Turtle format and constructs a collections
	 * of statements.
	 * 
	 * @param workflow
	 *            the string representing the workflow in Turtle
	 * @return collection of statements
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static Collection<Statement> parseTurtle(String workflow)
			throws RDFParseException, RDFHandlerException, IOException {

		TurtleParserFactory turtleParserFactory = new TurtleParserFactory();
		RDFParser parser = turtleParserFactory.getParser();
		return extractStatements(workflow, parser);
	}

	/**
	 * Extract statements from a given workflow using the provided RDFParser
	 * instance.
	 * 
	 * @param workflow
	 *            the given workflow as a String
	 * @param parser
	 *            any RDFParser instance
	 * @return a set of RDF Statements
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 */
	private static Collection<Statement> extractStatements(String workflow,
			RDFParser parser) throws UnsupportedEncodingException, IOException,
			RDFParseException, RDFHandlerException {
		InputStream is = new ByteArrayInputStream(workflow.getBytes("UTF-8"));

		InputStreamReader inputstreamreader = new InputStreamReader(is);

		StatementCollector handler = new StatementCollector();
		parser.setRDFHandler(handler);
		ParseErrorListener parseErrorListener = new LarkcParseErrorListener();
		parser.setParseErrorListener(parseErrorListener);
		parser.setPreserveBNodeIDs(true);
		parser.parse(inputstreamreader, "");

		Collection<Statement> result = new ArrayList<Statement>();

		Collection<Statement> statements = handler.getStatements();
		Map<String, String> namespaces = handler.getNamespaces();

		// TODO Should we throw an exception when the prefix is not found in
		// namespaces?
		String namespace;
		for (Statement stmt : statements) {
			namespace = stmt.getPredicate().getNamespace();
			namespace = namespace.substring(0, namespace.length() - 1);
			if (namespaces.containsKey(namespace)) {
				result.add(new StatementImpl(stmt.getSubject(), new URIImpl(
						namespaces.get(namespace)
								+ stmt.getPredicate().getLocalName()), stmt
						.getObject()));
			} else {
				result.add(stmt);
			}
		}

		return result;
	}
}
