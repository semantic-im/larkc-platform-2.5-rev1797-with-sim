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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.IllegalWorkflowGraphException;
import eu.larkc.core.executor.Executor;
import eu.larkc.shared.RDFParsingUtils;

/**
 * Abstract workflow handler base class. Has to be extended by actual workflow
 * handler implementation, e.g. a workflow handler that handles RDF/XML, or a
 * workflow handler that handles workflows in N3 notation.
 * 
 * @author Christoph Fuchs
 * 
 */
public class WorkflowHandler extends ServerResource implements
		WorkflowHandlerResource {

	private static Logger logger = LoggerFactory
			.getLogger(WorkflowHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.management.RDFHandlerResource#downloadWorkflowDescription()
	 */
	@Override
	public Representation downloadWorkflowDescription(Representation entity)
			throws Exception {

		Form form = new Form(entity);

		MediaType mediaType = entity.getMediaType();
		logger.debug("Media type: {}", mediaType);

		String workflow = form.getFirstValue("workflow");

		if (workflow == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new IllegalWorkflowGraphException("Empty workflow!");
		}

		try {
			Collection<Statement> col = null;

			if (mediaType.equals(MediaType.APPLICATION_RDF_XML)) {
				logger.debug("Workflow is in RDF/XML format.");
				col = extractXMLStatementsFromWorkflow(workflow);
			} else if (mediaType.equals(MediaType.TEXT_RDF_N3)) {
				logger.debug("Workflow is in N3 format.");
				col = extractN3StatementsFromWorkflow(workflow);
			} else {

				try {
					logger.debug("Try to parse RDF/XML ...");
					col = extractXMLStatementsFromWorkflow(workflow);
				} catch (Exception e1) {
					logger.debug("Exception: {}", e1.getMessage());
					try {
						logger.debug("Try to parse N3 ...");
						col = extractN3StatementsFromWorkflow(workflow);
					} catch (Exception e2) {
						logger.debug("Exception: {}", e2.getMessage());
						logger.debug(
								"No matching media type found (media type was {})",
								mediaType);
						throw new RuntimeException(
								"No matching media type found!");
					}
				}

			}

			Iterator<Statement> iter = col.iterator();
			List<Statement> stmnts = new ArrayList<Statement>();

			while (iter.hasNext()) {
				stmnts.add(iter.next());
			}

			SetOfStatements workflowDescription = new SetOfStatementsImpl(
					stmnts);
			Executor executor = new Executor(workflowDescription);

			// TODO execution of embedded query is not possible due to the
			// reason that we don't know which endpoint should be used, thus we
			// don't know which path to use
			// if workflow description contains query, execute it
			// if (WorkflowDescription.containsQuery(workflowDescription))
			// executor.execute(WorkflowDescription
			// .getQueryFromWorkflowDescription(workflowDescription));

			// set response
			setStatus(Status.SUCCESS_CREATED);

			UUID workflowId = executor.getId();
			Representation rep = new StringRepresentation(
					workflowId.toString(), MediaType.TEXT_PLAIN);
			// Indicates where the new resource is located
			String identifier = getRequest().getResourceRef().getIdentifier();
			rep.setLocationRef(identifier + "/" + workflowId);
			return rep;
		} catch (RuntimeException e) {
			String errorDescription = "No matching media type found";

			Representation rep = generateErrorMessage(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, workflow, e,
					errorDescription);

			return rep;
		} catch (RDFParseException e) {
			String errorDescription = "Error parsing RDF file";

			Representation rep = generateErrorMessage(Status.SUCCESS_ACCEPTED,
					workflow, e, errorDescription);

			return rep;
		} catch (IllegalWorkflowGraphException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL,
					new IllegalWorkflowGraphException(
							"Executor is not initialized!"));
			e.printStackTrace();
			throw new IllegalWorkflowGraphException(
					"Executor is not initialized!");
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			throw new Exception("Internal server error!");
		}
	}

	/**
	 * TODO Describe the purpose of this method.
	 * 
	 * @param workflow
	 * @param e
	 * @param errorDescription
	 * @return
	 */
	private Representation generateErrorMessage(Status status, String workflow,
			Exception e, String errorDescription) {
		setStatus(status);
		StringBuilder sb = new StringBuilder();
		sb.append("<h1>").append(errorDescription).append("</h1>");
		sb.append("<pre>");
		sb.append(workflow);
		sb.append("</pre>");
		sb.append("<p>Error message: </p>");
		sb.append("<pre>");
		sb.append(e.getMessage());
		sb.append("</pre>");
		Representation rep = new StringRepresentation(sb.toString(),
				MediaType.TEXT_HTML);
		return rep;
	}

	/**
	 * Extract statements from a workflow description by means of parsing the
	 * given workflow using a N3 RDF parser.
	 * 
	 * @param workflow
	 *            - the workflow description as a string
	 * @return a Collection of {@link Statement}s
	 * @throws RDFParseException
	 *             on RDF parsing error (e.g. malformed RDF)
	 * @throws RDFHandlerException
	 *             on RDF handling error
	 * @throws IOException
	 *             on I/O error
	 */
	private Collection<Statement> extractN3StatementsFromWorkflow(
			String workflow) throws RDFParseException, RDFHandlerException,
			IOException {
		return RDFParsingUtils.parseN3(workflow);
	}

	/**
	 * Extract statements from a workflow description by means of parsing the
	 * given workflow using an RDF/XML RDF parser.
	 * 
	 * @param workflow
	 *            - the workflow description as a string
	 * @return a Collection of {@link Statement}s
	 * @throws RDFParseException
	 *             on RDF parsing error (e.g. malformed RDF)
	 * @throws RDFHandlerException
	 *             on RDF handling error
	 * @throws IOException
	 *             on I/O error
	 */
	private Collection<Statement> extractXMLStatementsFromWorkflow(
			String workflow) throws RDFParseException, RDFHandlerException,
			IOException {
		return RDFParsingUtils.parseXML(workflow);
	}
}
