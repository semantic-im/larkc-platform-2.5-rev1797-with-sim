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

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;

/**
 * The management interface application for workflow descriptions in different
 * formats.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class WorkflowManagementInterface extends Application {

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());

		// allows creation of new workflows
		TemplateRoute route = router.attachDefault(WorkflowHandler.class);
		route.setMatchingMode(Template.MODE_EQUALS);

		// allows operations on a specific workflow
		TemplateRoute workflowRoute = router.attach("/{workflow}",
				WorkflowResourceImpl.class);
		workflowRoute.setMatchingMode(Template.MODE_EQUALS);

		// allow operations on endpoints
		router.attach("/{workflow}/endpoint", EndpointResourceImpl.class);

		return router;
	}

}
