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
package eu.larkc.core.management.hosts;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import eu.larkc.core.pluginManager.remote.shared.HostTemplates;

/**
 * <p>
 * Constructs the list of remote host templates to be retrieved through the
 * management interface with the suffix "/host?getHostTemplates". In particular,
 * used by the Workflow Designer GUI. E.g:
 * </p>
 * 
 * <code>GET http://localhost:8182/hosts?getHostTemplates</code>
 * 
 * <p>
 * Currently no other operation except for listing plug-ins is implemented.
 * </p>
 * 
 * @author hpcochep
 * 
 */
public class HostManagementInterface extends ServerResource {

	@Get
	public String toString() {

		String sFunction = getReference().getRemainingPart();

		if (sFunction == null || sFunction.length() < 1) {
			super.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
					"Missing function name in the request URL.");
			return null;
		}

		sFunction = sFunction.substring(1);
		int iParamIndex = sFunction.indexOf('?');
		if (iParamIndex != -1) {
			sFunction = sFunction.substring(0, iParamIndex);
			// For now this is ignored since we don't use any parameters. In
			// future call to parameter parsing function.
		}
		String sReturnHosts = "";

		try {

			sReturnHosts = HostTemplates.ReturnTemplatesAsString();

		} catch (SecurityException e) {
			super.setStatus(Status.SERVER_ERROR_INTERNAL, e);
			return null;
		} catch (IllegalArgumentException e) {
			super.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e,
					"Wrong arguments for this function.");
			return null;
		}

		return sReturnHosts;
	}
}