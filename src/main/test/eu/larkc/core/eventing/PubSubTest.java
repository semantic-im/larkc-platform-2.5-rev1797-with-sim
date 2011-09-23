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
package eu.larkc.core.eventing;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.InformationSet;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.data.workflow.MultiplePluginParametersException;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SampleWorkflows;

/**
 * This class tests the eventing with ActiveMQ.
 * 
 * @author hpcjiang
 * 
 */
public class PubSubTest extends LarkcTest {

	private static Logger logger = LoggerFactory.getLogger(PubSubTest.class);

	/**
	 * PubSub test using a workflow description and the test plugins
	 * 
	 * @throws IllegalWorkflowGraphException
	 * @throws MultiplePluginParametersException
	 * @throws RepositoryException
	 * 
	 */
	@Test
	public void executorTestUsingWorkflowDescription()
			throws IllegalWorkflowGraphException,
			MultiplePluginParametersException, RepositoryException {

		/*
		 * FIXME This test just seems to be a copy of
		 * ExecutorTest#executorTestUsingWorkflowDescription() - please write a
		 * separate test which really tests the publish/subscribe and messaging
		 * features.
		 */

		SetOfStatements workflowDescription = SampleWorkflows
				.getSimpleWorkflowDescription();

		Executor executor;

		executor = new Executor(workflowDescription);
		executor.execute(new SPARQLQueryImpl(SampleQueries.WHO_KNOWS_FRANK)
				.toRDF());
		InformationSet nextResults = executor.getNextResults();

		// TODO The following assert does not really test the PubSub mechanisms,
		// does it?
		Assert.assertNotNull(nextResults);
	}

	// FIXME Clean up stale code.
	/*
	 * public static void main(String args[]){ if (Larkc.isInitialized()) {
	 * logger.debug("LarKC is already initialized."); return; }
	 * 
	 * logger.debug("Starting LarKC");
	 * 
	 * try { Larkc.start(); } catch (OutOfMemoryError e) {
	 * logger.error("Error starting LarKC platform."); logger.error(
	 * "An out of memory error occured. Try adding -Xmx512M (or higher) to your VM parameters."
	 * ); e.printStackTrace(); return; } catch (RuntimeException e) {
	 * logger.error("Runtime error while starting the LarKC platform.");
	 * logger.error("Exception: {}, Message: {}", e.getClass(), e
	 * .getLocalizedMessage()); e.printStackTrace(); return; } catch (Exception
	 * e) { logger.error("Fatal error while starting the LarKC platform.");
	 * logger.error("Exception: {}, Message: {}", e.getClass(), e
	 * .getLocalizedMessage()); e.printStackTrace(); return; }
	 * 
	 * while (!Larkc.isInitialized()) { logger.debug("Initializing LarKC"); try
	 * { Thread.sleep(2000); } catch (InterruptedException e) { // Print any
	 * error that comes up e.printStackTrace(); } }
	 * logger.debug("Done initializing LarKC.");
	 * 
	 * Collection<URI> plugins; try { plugins =
	 * Larkc.getPluginRegistry().getAllPlugins(); for (URI uri : plugins) {
	 * System.out.println(uri);
	 * System.out.println("ENDPOINT: "+Larkc.getPluginRegistry
	 * ().getPluginEndpoint(uri)); } SetOfStatements workflowDescription =
	 * SampleWorkflows .getSimpleWorkflowDescription();
	 * 
	 * Executor executor = new Executor(workflowDescription);
	 * executor.execute(new
	 * SPARQLQueryImpl(SampleQueries.WHO_KNOWS_FRANK).toRDF()); } catch
	 * (PluginRegistryQueryException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IllegalWorkflowGraphException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * }
	 */

}
