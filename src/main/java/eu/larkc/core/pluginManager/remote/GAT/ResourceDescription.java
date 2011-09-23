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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gridlab.gat.security.CertificateSecurityContext;
import org.gridlab.gat.security.PasswordSecurityContext;
import org.gridlab.gat.security.SecurityContext;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.Larkc;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.pluginManager.remote.Servlet.Tomcat.JeeResourceDescription;
import eu.larkc.shared.RDFParsingUtils;

/**
 * This class provides functions for extracting information out of a workflow
 * description and creating {@link GatResourceDescription} for the
 * GatPluginManager.
 * 
 * @author Alexey Cheptsov
 * 
 */
public class ResourceDescription {

	private static Logger logger = LoggerFactory
			.getLogger(ResourceDescription.class);

	/**
	 * Returns a statement containing the remote resource type (e.g. GAT, Tomcat
	 * etc.) or null it the type is undefined or unknown
	 * 
	 * @param deploymentHostProperties
	 *            the SetOfStatements containing host's properties description
	 * 
	 * @return returns the statement with the host type
	 */
	public static Statement getRemoteHostType(
			SetOfStatements deploymentHostProperties) {

		CloseableIterator<Statement> statements = deploymentHostProperties
				.getStatements();
		Statement statement = null;

		while (statements.hasNext()) {
			statement = statements.next();
			if (statement.getPredicate().equals(UriList.RESOURCE_TYPE)) {
				break;
			}
			statement = null;
		}

		return statement;
	}

	/**
	 * Returns the set of statements containing remote host properties
	 * 
	 * @param fileName
	 *            the path to the file with the host description
	 * 
	 * @return returns the set of statement containing remote host properties
	 */
	public static SetOfStatements getHostPropertiesFromFile(String fileName) {

		SetOfStatements hostDescription = null;

		if (fileName != null) {
			try {
				StringBuffer hostOptions = new StringBuffer();

				InputStream in = Larkc.class.getClassLoader()
						.getResourceAsStream(fileName.substring(8));
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);

				String readObj;
				while ((readObj = br.readLine()) != null) {
					try {
						hostOptions.append(readObj).append(
								System.getProperty("line.separator"));

					} catch (Exception e) {
						break;
					}
				}

				Collection<Statement> col = RDFParsingUtils.parseN3(hostOptions
						.toString());

				// Retrieve statements
				Iterator<Statement> iter = col.iterator();
				List<Statement> stmnts = new ArrayList<Statement>();
				while (iter.hasNext()) {
					stmnts.add(iter.next());
				}
				hostDescription = new SetOfStatementsImpl(stmnts);

			} catch (Exception e) {
				logger.warn("Can not read the host description");
				logger.debug("e.printStackTrace()");
			}
		}

		return hostDescription;
	}

	/**
	 * Returns the set of statements containing remote host properties from the
	 * workflow description
	 * 
	 * @param hostBlankNodeName
	 *            the name of the blank node containing the host parameters
	 * @param workflowDescription
	 *            the SetOfStatements containing the workflow description
	 * 
	 * @return returns the set of statement containing remote host properties
	 */
	public static SetOfStatements getHostPropertiesFromWorkflowDescription(
			String hostBlankNodeName, SetOfStatements workflowDescription) {

		List<Statement> hostDescription = new ArrayList<Statement>();

		CloseableIterator<Statement> statements = workflowDescription
				.getStatements();

		while (statements.hasNext()) {
			Statement stmt = statements.next();
			if (stmt.getSubject().stringValue().equals(hostBlankNodeName)) {
				hostDescription.add(stmt);
			}
		}

		return new SetOfStatementsImpl(hostDescription);
	}

	/**
	 * Creates a {@link GatResourceDescription} out of a SetOfStaments
	 * describing the resource
	 * 
	 * @param resourceProperties
	 *            the SetOfStatements containing resource properties
	 * @return the constructed GatResourceDescription
	 */
	public static GatResourceDescription getGatResourceDescriptionFromProperties(
			SetOfStatements resourceProperties) {

		GatResourceDescription resource = new GatResourceDescription();

		// Default values
		resource.LarKCDir = new File("").getAbsolutePath();
		resource.WorkDir = "/tmp";
		resource.URI = "any://localhost";

		// Values from resource description
		CloseableIterator<Statement> statements = resourceProperties
				.getStatements();
		while (statements.hasNext()) {
			Statement stmt = statements.next();

			if (stmt.getPredicate().equals(UriList.RESOURCE_GAT_Broker)) {
				resource.Broker = stmt.getObject().stringValue()
						.substring(23 + 11);
				// throughs an exception if not specified in GatOptions.Brokers
				// enum
				GatOptions.Brokers.valueOf(resource.Broker);
			} else if (stmt.getPredicate().equals(
					UriList.RESOURCE_GAT_FileAdaptor)) {
				resource.FileAdaptor = stmt.getObject().stringValue()
						.substring(23 + 16);
				// throughs an exception if not specified in GatOptions.Brokers
				// enum
				GatOptions.FileAdaptors.valueOf(resource.FileAdaptor);
			} else if (stmt.getPredicate().equals(UriList.RESOURCE_GAT_URI)) {
				resource.URI = stmt.getObject().stringValue();
			} else if (stmt.getPredicate().equals(
					UriList.RESOURCE_GAT_LARKC_DIR)) {
				resource.LarKCDir = stmt.getObject().stringValue().substring(8);
			} else if (stmt.getPredicate()
					.equals(UriList.RESOURCE_GAT_WORK_DIR)) {
				resource.WorkDir = stmt.getObject().stringValue().substring(8);
			} else if (stmt.getPredicate()
					.equals(UriList.RESOURCE_GAT_JAVA_DIR)) {
				resource.JavaDir = stmt.getObject().stringValue().substring(8);
			} else if (stmt.getPredicate().equals(
					UriList.RESOURCE_GAT_JAVA_OPTIONS)) {
				resource.JavaArgs = stmt.getObject().stringValue();
			}
			logger.debug("Deployment resource property: {}", stmt.toString());
		}

		return resource;
	}

	/**
	 * Creates a {@link GatResourceDescription} out of a SetOfStaments
	 * describing the resource
	 * 
	 * @param resourceProperties
	 *            the SetOfStatements containing resource properties
	 * @return the constructed GatResourceDescription
	 */
	public static JeeResourceDescription getJeeResourceDescriptionFromProperties(
			SetOfStatements resourceProperties) {

		JeeResourceDescription resource = new JeeResourceDescription();

		// Values from resource description
		CloseableIterator<Statement> statements = resourceProperties
				.getStatements();
		while (statements.hasNext()) {
			Statement stmt = statements.next();

			if (stmt.getPredicate().equals(UriList.RESOURCE_JEE_URI)) {
				resource.URI = stmt.getObject().stringValue();
			}

			logger.debug("Deployment resource property: {}", stmt.toString());
		}

		return resource;
	}

	/**
	 * Creates a GAT SecurityContext out of a SetOfStaments describing the
	 * resource
	 * 
	 * @param resourceProperties
	 *            the SetOfStatements containing resource properties
	 * @return the constructed GatResourceDescription
	 * @throws URISyntaxException
	 */
	public static SecurityContext getGatSecurityContextFromProperties(
			SetOfStatements resourceProperties) throws URISyntaxException {

		SecurityContext secContext = null;
		String userName = null;
		String password = null;
		String certkey = null;

		// Values from resource description
		CloseableIterator<Statement> statements = resourceProperties
				.getStatements();
		while (statements.hasNext()) {
			Statement stmt = statements.next();

			if (stmt.getPredicate().equals(UriList.RESOURCE_GAT_USERNAME)) {
				userName = stmt.getObject().stringValue();
			} else if (stmt.getPredicate()
					.equals(UriList.RESOURCE_GAT_PASSWORD)) {
				password = stmt.getObject().stringValue();
			} else if (stmt.getPredicate().equals(UriList.RESOURCE_GAT_CERTKEY)) {
				certkey = stmt.getObject().stringValue();
			}
		}

		if (certkey == null) {
			secContext = new PasswordSecurityContext(userName, password);
		} else {
			secContext = new CertificateSecurityContext(
					new org.gridlab.gat.URI(System.getProperty("user.home")
							+ "/.globus/userkey.pem"), new org.gridlab.gat.URI(
							System.getProperty("user.home")
									+ "/.globus/usercert.pem"), certkey);
		}

		return secContext;
	}

	/**
	 * This method parses a file and constructs a collections of statements.
	 * 
	 * @param filestream
	 *            the file representing the workflow
	 * @return collection of statements
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static SetOfStatements parseXML(InputStream filestream)
			throws RDFParseException, RDFHandlerException, IOException {

		RDFXMLParser parser = new RDFXMLParser();
		// InputStream is = new FileInputStream(file);
		InputStreamReader inputstreamreader = new InputStreamReader(filestream);
		StatementCollector handler = new StatementCollector();
		parser.setRDFHandler(handler);
		parser.parse(inputstreamreader, "");
		SetOfStatements statements = new SetOfStatementsImpl(
				handler.getStatements());

		return statements;
	}

}
