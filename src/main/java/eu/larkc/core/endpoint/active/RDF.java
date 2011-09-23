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
package eu.larkc.core.endpoint.active;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class RDF {
	public static String CYC_PREFIX = "http://example.com/cyc#";
	public static String GC_PREFIX = "http://example.com/GCPlugin/cyc#";

	public static final URI URI_EVENT = new URIImpl(CYC_PREFIX + "event");
	public static final URI URI_EVENT_HASNAME = new URIImpl(CYC_PREFIX
			+ "eventHasName");
	public static final URI URI_PREFERRED_NAME_STRING = new URIImpl(CYC_PREFIX
			+ "preferredNameString");
	public static final URI URI_EVENT_HASLOCATION = new URIImpl(CYC_PREFIX
			+ "eventHasLocation");
	public static final URI URI_EVENT_HASOWNER = new URIImpl(CYC_PREFIX
			+ "eventHasOwner");
	public static final URI URI_SUBEVENTS = new URIImpl(CYC_PREFIX
			+ "subEvents");

	public static final URI URI_PROVIDING_LODGING = new URIImpl(CYC_PREFIX
			+ "ProvidingLodging");
}
