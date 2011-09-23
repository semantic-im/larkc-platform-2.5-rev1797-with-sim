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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.core.pluginregistry.PluginRegistryQueryException;
import eu.larkc.plugin.Plugin;

/**
 * Tests if plug-ins can be retrieved from the plug-in registry. Also tests
 * advanced functionality like switching the decider.
 * 
 * @author Christoph Fuchs, Luka Bradesko
 * 
 */
public class PluginRegistryTest extends LarkcTest {

	/**
	 * Tests if _test plug-ins are in the registry
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws PluginRegistryQueryException
	 */
	@Test
	public void testAllPluginsMethods() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, PluginRegistryQueryException {

		Collection<URI> plugins = Larkc.getPluginRegistry().getAllPlugins();

		Assert.assertNotNull("no plug-ins retrieved", plugins);

		Assert.assertTrue("test decider not retrieved",
				plugins.contains(new URIImpl(
						"urn:eu.larkc.plugin.decide.TestDecider")));

		Assert.assertTrue("test identifier not retrieved", plugins
				.contains(new URIImpl(
						"urn:eu.larkc.plugin.identify.TestIdentifier")));

		Assert.assertTrue("test transformer not retrieved", plugins
				.contains(new URIImpl(
						"urn:eu.larkc.plugin.transform.TestTransformer")));

	}

	/**
	 * Tests if plugin instance can be retrieved via the plug-in registry using
	 * the plugins TestIdentifier, TestTransformer and TestDecider which only
	 * have purpose for testing.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void pluginRetrieval() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		URI testIdentifierUri = new URIImpl(
				"urn:eu.larkc.plugin.identify.TestIdentifier");
		Plugin newTestIdentifierInstance = Larkc.getPluginRegistry()
				.getNewPluginInstance(testIdentifierUri);
		Assert.assertNotNull("Plugin retrieved by plugin registry is null.",
				newTestIdentifierInstance);

		URI testTransformerUri = new URIImpl(
				"urn:eu.larkc.plugin.transform.TestTransformer");
		Plugin newTransformerInstance = Larkc.getPluginRegistry()
				.getNewPluginInstance(testTransformerUri);
		Assert.assertNotNull("Plugin retrieved by plugin registry is null.",
				newTransformerInstance);

		URI testDeciderUri = new URIImpl(
				"urn:eu.larkc.plugin.decide.TestDecider");
		Plugin newDeciderInstance = Larkc.getPluginRegistry()
				.getNewPluginInstance(testDeciderUri);
		Assert.assertNotNull("Plugin retrieved by plugin registry is null.",
				newDeciderInstance);
	}

	/**
	 * If a plugin is retrieved which does not exist, the plugin registry should
	 * not return any plugin (<code>null</code>).
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void nonexistentPluginRetrieval() throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		String nonexistentPlugin = "urn:foobar";
		URI testUri = new URIImpl(nonexistentPlugin);
		Plugin newPluginInstance = Larkc.getPluginRegistry()
				.getNewPluginInstance(testUri);

		Assert.assertNull(
				"Plugin "
						+ nonexistentPlugin
						+ " does not exist and thus the plugin registry should not retrieve any plugin.",
				newPluginInstance);
	}

	/**
	 * Test whether plug-in endpoints are returned correctly
	 * 
	 * @throws PluginRegistryQueryException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void testEndpointDataRetrieval() throws PluginRegistryQueryException {
		String epoint = Larkc.getPluginRegistry().getPluginEndpoint(
				new URIImpl("urn:eu.larkc.plugin.identify.TestIdentifier"));

		Assert.assertNotSame("endpoint is not correct",
				epoint.equals("java:eu.larkc.plugin.identify.TestIdentifier"));
	}

}
