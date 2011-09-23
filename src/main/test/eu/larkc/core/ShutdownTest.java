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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.larkc.core.endpoint.EndpointShutdownException;

/**
 * Test the regular and error-caused shutdown of the LarKC platform. Tests in
 * this class will also test platform initialization.
 * 
 * @author Christoph Fuchs
 * 
 */
public class ShutdownTest extends LarkcTest {

	/**
	 * Start the platform if it was not yet started or shut down again.
	 */
	@Before
	public void startLarkcBeforeEveryTest() {
		// Start LarKC (since it may have been shut down due to a previous
		// test)
		Larkc.start();
	}

	/**
	 * Tests if LarKC will shut down correctly after initialization. The LarKC
	 * platform will be initialized since this test inherits LarkcTest.
	 * 
	 * @throws EndpointShutdownException
	 *             on endpoint shutdown error
	 */
	@Test
	@Ignore
	public void regularShutdownTest() throws EndpointShutdownException {
		Assert.assertTrue("LarKC should be initialized but was not",
				Larkc.isInitialized());

		// Stop the platform
		Larkc.stop();

		Assert.assertTrue("LarKC should be stopped but was not",
				Larkc.getPlatformStatus() == PlatformStatus.STOPPED);
	}

	/**
	 * Tests if the LarKC platform will shut down correctly if it encounters an
	 * error during initialization.
	 * 
	 * @throws EndpointShutdownException
	 *             on endpoint shutdown error
	 */
	@Test
	@Ignore
	public void shutdownDueToErrorTest() throws EndpointShutdownException {
		// FIXME if the platform is initialized twice a 'SubLException: A
		// package named "KEYWORD" already exists' pops up

		Assert.assertTrue("LarKC should be initialized but was not",
				Larkc.isInitialized());

		// Stop the platform
		Larkc.stop();

		Assert.assertTrue("LarKC should be stopped but was not",
				Larkc.getPlatformStatus() == PlatformStatus.STOPPED);

		// Start the platform again, specifying a lisp configuration file that
		// does not exist.
		Larkc.main(new String[] { "-i", "this_file_does_not_exist.lisp", "-b" });

		Assert.assertTrue(
				"LarKC should have encountered an initialization error and should have shut down but has not",
				Larkc.getPlatformStatus() == PlatformStatus.STOPPED);
	}

}
