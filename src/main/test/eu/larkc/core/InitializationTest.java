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
import org.junit.Test;


/**
 * Tests if LarKC is initialized properly. 
 * 
 * @author Christoph Fuchs
 *
 */
public class InitializationTest extends LarkcTest {

	/**
	 * Tests if LarKC is already initialized
	 * @throws InterruptedException
	 */
	@Test
	public void initializationTest() throws InterruptedException {
		Assert.assertTrue("LarKC should be initialized but was not", Larkc.isInitialized());
	}
	
}
