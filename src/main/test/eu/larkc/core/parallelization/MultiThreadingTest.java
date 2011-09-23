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
package eu.larkc.core.parallelization;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.InformationSet;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.data.workflow.MultiplePluginParametersException;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SampleWorkflows;

/**
 * Class to test the multi-threading functionalities. If necessary include a
 * longer description and/or an example.
 * 
 * @author Matthias Assel
 * 
 */

public class MultiThreadingTest extends LarkcTest {

	private static Logger logger = LoggerFactory
			.getLogger(MultiThreadingTest.class);

	/**
	 * Tests the functionality of the local plug-in manager to to
	 * multi-threading for one plug-in which has a parameter that it can be
	 * split.
	 * 
	 * @throws IllegalWorkflowGraphException
	 * @throws MultiplePluginParametersException
	 * @throws RepositoryException
	 */
	@Test
	public void multiThreadingTestWithStringWorkflowDescrption()
			throws IllegalWorkflowGraphException,
			MultiplePluginParametersException, RepositoryException {
		SetOfStatements workflowDescription = SampleWorkflows
				.getWorkflowDescriptionWithMultiThreading();

		Executor executor = new Executor(workflowDescription);
		executor.execute(new SPARQLQueryImpl(SampleQueries.WHO_KNOWS_FRANK)
				.toRDF());
		InformationSet nextResults = executor.getNextResults();

		ArrayList<Statement> list = new ArrayList<Statement>();
		SetOfStatements emptySOS = new SetOfStatementsImpl(list);
		SetOfStatements sos = nextResults.toRDF(emptySOS);
		CloseableIterator<Statement> statements = sos.getStatements();
		Statement stmt;

		while (statements.hasNext()) {
			stmt = statements.next();
			logger.info(stmt.getSubject().stringValue() + " "
					+ stmt.getPredicate().stringValue() + " "
					+ stmt.getObject().stringValue());
		}

		Assert.assertNotNull(nextResults);
	}
}
