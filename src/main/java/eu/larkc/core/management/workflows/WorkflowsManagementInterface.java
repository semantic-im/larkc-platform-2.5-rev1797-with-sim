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
package eu.larkc.core.management.workflows;

import java.util.Collection;
import java.util.Map.Entry;

import org.openrdf.model.URI;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.Larkc;
import eu.larkc.core.data.WorkflowObject;

/**
 * The management interface application for N3 workflow descriptions.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class WorkflowsManagementInterface extends ServerResource {

	private static Logger logger = LoggerFactory
			.getLogger(WorkflowsManagementInterface.class);

	/**
	 * Returns a page with all deployed workflow ids.
	 * 
	 * @return webpage
	 */
	@Get
	public Representation getDeployedWorkflows() {

		Collection<WorkflowObject> workflows = Larkc.getWorkflows();

		StringBuilder sb = new StringBuilder();

		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"");
		sb.append("\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append("<style type=\"text/css\" title=\"currentStyle\" media=\"screen\">");
		sb.append("@import \"style.css\";</style></head>");
		sb.append("<body>");
		sb.append("<div id=\"main\"><div id=\"header\"></div>");
		sb.append("<h1>LarKC management interface</h1>");
		sb.append("<h2>Currently deployed workflows</h2>");
		sb.append("<table border=\"1\" cellpadding=\"10\"><tr><th>Workflow ID</th><th>Endpoints</th></tr>");
		// sb.append("<table border=\"1\" cellpadding=\"10\"><tr><th>Workflow ID</th><th>Endpoints</th><th></th></tr>");

		for (WorkflowObject wo : workflows) {
			sb.append("<tr><td>");
			sb.append(wo.getWorkflowId());
			sb.append("</td><td>");
			for (Entry<String, URI> endpoint : wo.getEndpoints().entrySet()) {
				sb.append(endpoint.getKey());
				sb.append(" (");
				sb.append(endpoint.getValue().stringValue());
				sb.append(")");
				sb.append("<br>");
			}

			sb.append("</td>");
			// sb.append("<td>");
			// sb.append("<form id=\"delete-workflow\" action=\"http://localhost:8182/workflow/");
			// sb.append(wo.getWorkflowId());
			// sb.append("\" method=\"delete\">");
			// sb.append("<input type=\"submit\" value=\"Delete\" />");
			// sb.append("</form>");
			// sb.append("</td>");
			sb.append("</tr>");
		}

		sb.append("</table></div></body></html>");

		// set response
		setStatus(Status.SUCCESS_OK);

		String page = sb.toString();
		logger.debug("HTML:\n{}", page);

		Representation rep = new StringRepresentation(sb.toString(),
				MediaType.TEXT_HTML);
		return rep;
	}

}
