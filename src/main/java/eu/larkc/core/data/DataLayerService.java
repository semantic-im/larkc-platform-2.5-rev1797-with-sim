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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontotext.ordi.Factory;
import com.ontotext.ordi.tripleset.TSource;

import eu.larkc.core.data.util.LoggingOutputStream;

/**
 * Data Layer service management type.
 * 
 * @author vassil
 * 
 */
public class DataLayerService {

	private static TSource ordi;
	private static Logger logger = LoggerFactory
			.getLogger(DataLayerService.class);
	static String NO_PERSIST = "eu.larkc.core.data.persistence";

	/**
	 * <p>
	 * Gets the ORDI triple source from the ORDI factory.
	 * </p>
	 * 
	 * <p>
	 * <i>Note: Since ORDI prints information to stdout, and we do not
	 * necessarily want that, stdout is redirected two times using the
	 * {@link LoggingOutputStream} class. One redirect takes place in the
	 * getORDI() method, the other one in the shutdown thread.</i>
	 * </p>
	 * 
	 * @return
	 */
	static synchronized TSource getORDI() {
		if (ordi == null) {
			String queryOptimize = System.getProperty("trree.optimization",
					"true");
			System.setProperty("trree.optimization", queryOptimize);
			// Create the defaultTSource which prints to stdout
			ordi = Factory.createDefaultTSource();
		}
		return ordi;
	}

	static synchronized void setORDI(TSource source) {
		ordi = source;
	}

	/**
	 * Shutdown the data layer service.
	 */
	public static synchronized void shutdown() {
		logger.info("Shutdown request is invoked!");
		String persist = System.getProperty(NO_PERSIST, "true");
		if (persist.equals("false")) {
			logger.info("No data persistence set!");
		} else {
			// Do the shutdown which prints to stdout
			ordi.shutdown();
			logger.info("Shutdown completed!");
		}
	}
}
