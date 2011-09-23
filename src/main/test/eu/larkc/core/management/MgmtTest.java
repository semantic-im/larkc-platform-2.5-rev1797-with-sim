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
package eu.larkc.core.management;

import org.junit.After;
import org.junit.Before;

import eu.larkc.core.Larkc;
import eu.larkc.core.LarkcTest;

/**
 * Base class for all management interface tests. Also starts the LarKC
 * platform.
 * 
 * @author Christoph Fuchs, norlan
 * 
 */
public class MgmtTest extends LarkcTest {

	/**
	 * Starts the platform as well as the management interface server
	 * 
	 */
	@Before
	public void startLarkc() {
		Larkc.start();
	}

	/**
	 * Stops the platform as well as the management interface.
	 * 
	 * @throws Exception
	 *             if management interface was not initialized
	 */
	@After
	public void stopLarkc() throws Exception {
		Larkc.stop();
	}

}
