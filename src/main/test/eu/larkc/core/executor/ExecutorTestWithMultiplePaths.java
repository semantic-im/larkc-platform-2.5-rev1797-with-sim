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
package eu.larkc.core.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.LarkcTest;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.data.workflow.MultiplePluginParametersException;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.shared.SampleQueries;
import eu.larkc.shared.SampleWorkflows;

/**
 * Test the functionality of the executor with multiple paths.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class ExecutorTestWithMultiplePaths extends LarkcTest {

	private static Logger logger = LoggerFactory
			.getLogger(ExecutorTestWithMultiplePaths.class);

	/**
	 * Executor test using a workflow description with multiple paths.
	 * 
	 * @throws IllegalWorkflowGraphException
	 * @throws MultiplePluginParametersException
	 * @throws RepositoryException
	 */
	@Test
	public void executorTestUsingWorkflowDescriptionWithMultiplePaths()
			throws IllegalWorkflowGraphException,
			MultiplePluginParametersException, RepositoryException {
		SetOfStatements workflowDescription = SampleWorkflows
				.getSimpleWorkflowDescriptionWithMultiplePaths();

		Executor executor = new Executor(workflowDescription);

		Set<String> ids = executor.getPathIds();

		Assert.assertEquals(ids.size(), 2);

		Iterator<String> iterator = ids.iterator();
		String pathId = iterator.next();

		SetOfStatements query;
		query = new SPARQLQueryImpl(SampleQueries.WHO_KNOWS_TIM_BERNERS_LEE)
				.toRDF();

		executor.execute(query, pathId);
		SetOfStatements nextResults = executor.getNextResults(pathId);

		Collection<Statement> actualStatements = new ArrayList<Statement>();
		CloseableIterator<Statement> it = nextResults.getStatements();
		while (it.hasNext()) {
			actualStatements.add(it.next());
		}
		it.close();

		// Construct the expected result
		Collection<Statement> expectedStatements = new ArrayList<Statement>();
		BNode bnode = new BNodeImpl("blank");

		Statement stmt1 = new StatementImpl(bnode, RDFConstants.RDF_TYPE,
				RDFConstants.LARKC_SPARQLQUERY);
		expectedStatements.add(stmt1);
		expectedStatements.add(stmt1);
		StatementImpl stmt2;
		stmt2 = new StatementImpl(bnode, RDFConstants.LARKC_HASSERIALIZEDFORM,
				new LiteralImpl(
						SampleQueries.WHO_KNOWS_TIM_BERNERS_LEE.toString()));
		expectedStatements.add(stmt2);
		expectedStatements.add(stmt2);

		for (Statement stmt : actualStatements) {
			logger.debug("Actual stmt: {}", stmt);
		}

		for (Statement stmt : expectedStatements) {
			logger.debug("Expected stmt: {}", stmt);
		}

		Assert.assertEquals(expectedStatements.size(), actualStatements.size());

		boolean found = false;
		Statement expectedStmt = null;
		for (Statement actualStmt : actualStatements) {
			for (Statement stmt : expectedStatements) {
				expectedStmt = stmt;
				if (actualStmt.getPredicate().stringValue()
						.equals(expectedStmt.getPredicate().stringValue())) {
					Assert.assertEquals(expectedStmt.getObject().stringValue(),
							actualStmt.getObject().stringValue());
					found = true;
					break;
				}
			}
			expectedStatements.remove(expectedStmt);
			if (!found)
				Assert.fail("Statement not present in expected statements!");
			found = false;
		}

		String pathId2 = iterator.next();

		SetOfStatements query2;
		query2 = new SPARQLQueryImpl(SampleQueries.WHO_KNOWS_FRANK).toRDF();

		executor.execute(query2, pathId2);
		SetOfStatements nextResults2 = executor.getNextResults(pathId2);

		Collection<Statement> actualStatements2 = new ArrayList<Statement>();
		CloseableIterator<Statement> it2 = nextResults2.getStatements();
		while (it2.hasNext()) {
			actualStatements2.add(it2.next());
		}
		it2.close();

		// Construct the expected result
		Collection<Statement> expectedStatements2 = new ArrayList<Statement>();
		BNode bnode2 = new BNodeImpl("blank");

		Statement stmt12 = new StatementImpl(bnode2, RDFConstants.RDF_TYPE,
				RDFConstants.LARKC_SPARQLQUERY);
		expectedStatements2.add(stmt12);
		expectedStatements2.add(stmt12);
		StatementImpl stmt22;
		stmt22 = new StatementImpl(bnode2,
				RDFConstants.LARKC_HASSERIALIZEDFORM, new LiteralImpl(
						SampleQueries.WHO_KNOWS_FRANK.toString()));
		expectedStatements2.add(stmt22);
		expectedStatements2.add(stmt22);

		for (Statement stmt : actualStatements2) {
			logger.debug("Actual stmt: {}", stmt);
		}

		for (Statement stmt : expectedStatements2) {
			logger.debug("Expected stmt: {}", stmt);
		}

		Assert.assertEquals(expectedStatements2.size(),
				actualStatements2.size());

		boolean found2 = false;
		Statement expectedStmt2 = null;
		for (Statement actualStmt2 : actualStatements2) {
			for (Statement stmt : expectedStatements2) {
				expectedStmt2 = stmt;
				if (actualStmt2.getPredicate().stringValue()
						.equals(expectedStmt2.getPredicate().stringValue())) {
					Assert.assertEquals(
							expectedStmt2.getObject().stringValue(),
							actualStmt2.getObject().stringValue());
					found2 = true;
					break;
				}
			}
			expectedStatements2.remove(expectedStmt2);
			if (!found2)
				Assert.fail("Statement not present in expected statements!");
			found2 = false;
		}
	}

}
