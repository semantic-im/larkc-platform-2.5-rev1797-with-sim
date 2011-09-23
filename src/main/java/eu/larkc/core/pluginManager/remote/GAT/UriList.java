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
package eu.larkc.core.pluginManager.remote.GAT;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.core.util.RDFConstants;

/**
 * Contains static definition of URIs used in the rdf resource description
 * 
 * @author hpcochep
 * 
 */
public class UriList {

	/** Namespace for LarKC. */
	public static final String LARKC = RDFConstants.LARKC_NAMESPACE;

	/** This URI represents the URI identifying GAT resource properties. */
	public static final URI GAT_RESOURCE = new URIImpl(LARKC + "GAT");

	/**
	 * This URI represents the URI identifying Servlet.Tomcat resource
	 * properties.
	 */
	public static final URI JEE_RESOURCE = new URIImpl(LARKC + "JEE");

	/** This URI represents the URI for the predicate resourceID. */
	public static final URI RESOURCE_JEE_URI = new URIImpl(LARKC + "jeeUri");

	/** This URI represents the URI for the predicate resourceID. */
	/* TODO to be deprecated */
	public static final URI RESOURCE_ID = new URIImpl(LARKC + "resourceID");

	/** This URI represents the host type (GAT or JEE) */
	public static final URI RESOURCE_TYPE = new URIImpl(LARKC + "hostType");

	/** This URI represents the URI for the predicate resourceGatProperties. */
	/* TODO to be deprecated */
	public static final URI RESOURCE_PROPERTIES = new URIImpl(LARKC
			+ "resourceProperties");

	/** This URI represents the URI for the predicate resourceGatBroker. */
	public static final URI RESOURCE_GAT_Broker = new URIImpl(LARKC
			+ "gatBroker");

	/** This URI represents the URI for the predicate resourceGatFileAdaptor. */
	public static final URI RESOURCE_GAT_FileAdaptor = new URIImpl(LARKC
			+ "gatFileAdaptor");

	/** This URI represents the URI for the predicate resourceGatUri. */
	public static final URI RESOURCE_GAT_URI = new URIImpl(LARKC + "gatUri");

	/** This URI represents the URI for the predicate resourceGatLarkcDir. */
	public static final URI RESOURCE_GAT_LARKC_DIR = new URIImpl(LARKC
			+ "gatLarkcDir");

	/** This URI represents the URI for the predicate resourceGatWorkDir. */
	public static final URI RESOURCE_GAT_WORK_DIR = new URIImpl(LARKC
			+ "gatWorkDir");

	/** This URI represents the URI for the predicate resourceGatJavaDir. */
	public static final URI RESOURCE_GAT_JAVA_DIR = new URIImpl(LARKC
			+ "gatJavaDir");

	/** This URI represents the URI for the predicate resourceGatJavaArgs. */
	/* TODO to be deprecated */
	public static final URI RESOURCE_GAT_JAVA_ARGS = new URIImpl(LARKC
			+ "resourceGatJavaArgs");

	/** This URI represents the URI for the predicate resourceGatJavaArgs. */
	/* TODO to be deprecated */
	public static final URI RESOURCE_GAT_JAVA_OPTIONS = new URIImpl(LARKC
			+ "gatJavaOptions");

	/** This URI represents the URI for the predicate userName. */
	public static final URI RESOURCE_GAT_USERNAME = new URIImpl(LARKC
			+ "gatUserName");

	/** This URI represents the URI for the predicate password. */
	public static final URI RESOURCE_GAT_PASSWORD = new URIImpl(LARKC
			+ "gatPassword");

	/** This URI represents the URI for the predicate certKey. */
	public static final URI RESOURCE_GAT_CERTKEY = new URIImpl(LARKC
			+ "gatCertKey");
}
