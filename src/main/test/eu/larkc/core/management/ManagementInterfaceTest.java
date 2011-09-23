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

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import eu.larkc.shared.Resources;

/**
 * Test availability and functions of the management interface.
 * 
 * @author Christoph Fuchs
 * 
 */
public class ManagementInterfaceTest extends MgmtTest {

	/**
	 * Test if the management interface server is started correctly and is
	 * reachable.
	 * 
	 * @throws Exception
	 */
	@Test
	public void availabilityTest() throws Exception {
		ClientResource mgmtResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);
		try {
			mgmtResource.get();
		} catch (ResourceException e) {
			// status code is 405 because no URL is given (method not allowed)
			Assert.assertEquals(405, e.getStatus().getCode());
		}
	}

	/**
	 * Test if the management interface server is started correctly and if the
	 * address where workflow descriptions in N3 can be deposited is reachable.
	 * 
	 * @throws Exception
	 */
	@Test
	public void availabilityTestN3() throws Exception {
		ClientResource mgmtResource = new ClientResource(
				Resources.MGMT_WORKFLOWS_URL);
		try {
			mgmtResource.get();
		} catch (ResourceException e) {
			// status code is 405 because no URL is given (method not allowed)
			Assert.assertEquals(405, e.getStatus().getCode());
		}
	}

}
