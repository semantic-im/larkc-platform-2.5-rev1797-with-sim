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
package eu.larkc.core;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all LarKC tests. All tests should extend this class since it
 * initializes the LarKC platform.
 * 
 * @author Christoph Fuchs
 * 
 */
public class LarkcTest {

	private static Logger logger = LoggerFactory.getLogger(LarkcTest.class);

	/**
	 * Start the platform before running any tests.
	 */
	@BeforeClass
	public static void startLarKC() {
		logger.debug("Starting LarKC");

		try {
			Larkc.start();
		} catch (OutOfMemoryError e) {
			logger.error("Error starting LarKC platform.");
			logger
					.error("An out of memory error occured. Try adding -Xmx512M (or higher) to your VM parameters.");
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			logger.error("Runtime error while starting the LarKC platform.");
			logger.error("Exception: {}, Message: {}", e.getClass(), e
					.getLocalizedMessage());
			e.printStackTrace();
			return;
		} catch (Exception e) {
			logger.error("Fatal error while starting the LarKC platform.");
			logger.error("Exception: {}, Message: {}", e.getClass(), e
					.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Deletes the ordi-trree directory
	 */
	@AfterClass
	public static void deleteOrdiTreeDirectory() {
		File f = new File("ordi-trree");
		if (f.exists() && f.isDirectory()) {
			for (File f2 : f.listFiles()) {
				f2.delete();
			}
		}
	}

}
