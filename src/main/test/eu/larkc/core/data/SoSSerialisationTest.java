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
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to test functionalities of the RemotePluginManager. If necessary
 * include a longer description and/or an example.
 * 
 * @author hpcassel
 * 
 */
public class SoSSerialisationTest {

	private static Logger logger = LoggerFactory
			.getLogger(SoSSerialisationTest.class);

	/**
	 * Test the serialization of SetofStatements into an output file
	 */
	@Test
	public void testSoSSerialization() {

		List<Statement> allStatements = new ArrayList<Statement>();

		Resource r = new BNodeImpl("blankNode1");
		Resource r1 = new BNodeImpl("blankNode2");
		Resource r2 = new BNodeImpl("blankNode3");
		Resource r3 = new BNodeImpl("blankNode4");

		URI uri = new URIImpl("http://dbpedia.org/resource/Amelia_Heinle");
		URI uri1 = new URIImpl(
				"http://dbpedia.org/resource/Purple_People_Eater");
		URI uri2 = new URIImpl(
				"http://dbpedia.org/resource/Claude_Fauchet_(historian)");
		URI uri3 = new URIImpl(
				"http://dbpedia.org/resource/Naval_Air_Station_Pensacola");

		Value value = new URIImpl("http://0.8306754");
		Value value1 = new URIImpl("http://0.83837366");
		Value value2 = new URIImpl("http://0.8492048");
		Value value3 = new URIImpl("http://0.8409036");

		Statement statement = new StatementImpl(r, uri, value);
		Statement statement1 = new StatementImpl(r1, uri1, value1);
		Statement statement2 = new StatementImpl(r2, uri2, value2);
		Statement statement3 = new StatementImpl(r3, uri3, value3);

		allStatements.add(statement);
		allStatements.add(statement1);
		allStatements.add(statement2);
		allStatements.add(statement3);

		SetOfStatements input = new SetOfStatementsImpl(allStatements);

		// Input serialization
		java.io.FileOutputStream fos;
		java.io.ObjectOutputStream oos;
		try {
			fos = new java.io.FileOutputStream("input");
			oos = new java.io.ObjectOutputStream(fos);

			oos.writeObject(input);

			oos.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Test the deserialization of SetofStatements from the file
	 */
	@Test
	public void testSoSDeserialization() {
		SetOfStatements params = null;
		try {
			// Params read and deserialization
			try {
				java.io.FileInputStream inputStream = new java.io.FileInputStream(
						"input_param");
				java.io.ObjectInputStream oin = new java.io.ObjectInputStream(
						inputStream);
				try {
					Object readObj = (Object) oin.readObject();
					params = (SetOfStatements) readObj;
					logger.debug("Params loaded");
				} catch (java.io.EOFException e) {
					logger.error("Cannot initialize parameters");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// initialize plugin with workflow parameters
			logger.debug("Intializing parameters...");
			CloseableIterator<Statement> tempstate = params.getStatements();
			if (tempstate.hasNext()) {
				while (tempstate.hasNext())
					logger.debug("Got parameter: {}", tempstate.next());
				tempstate.close();

				// plugin.initialise(params, null, null);
			}
		} catch (Exception e) {
		}
	}
}
