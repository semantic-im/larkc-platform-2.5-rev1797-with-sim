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

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all ORDI tests
 * 
 * @author ?, Christoph Fuchs
 * 
 */
public abstract class ORDITestCase {

	private static final Logger logger = LoggerFactory
			.getLogger(ORDITestCase.class);

	protected static DataFactory df = DataFactory.INSTANCE;

	/**
	 * Retrieves a new {@link DataFactory} instance
	 */
	@Before
	public void retrieveNewDataFactory() {
		logger.debug("Retrieving new DataFactory instance...");
		df = DataFactoryImpl.getInstance();
	}

	/**
	 * Deletes the ordi-trree directory before and after each test
	 */
	@Before
	@After
	public void deleteOrdiTreeDirectory() {
		removeStatements();
		File f = new File("ordi-trree");
		if (f.exists() && f.isDirectory()) {
			for (File f2 : f.listFiles()) {
				f2.delete();
			}
		}
	}

	private void removeStatements() {
		// Since it is unclear to me what con.removeStatement(null, null, null,
		// null) does exactly, this method will not be called for the time
		// beeing.
		// Please someone explain to me what it does! ~Gigi
		RdfStoreConnection con = df.createRdfStoreConnection();
		con.removeStatement(null, null, null, null);
	}
}
