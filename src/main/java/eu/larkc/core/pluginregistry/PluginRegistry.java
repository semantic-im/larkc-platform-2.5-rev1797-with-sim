/***
 *   Copyright (c) 1995-2010 Cycorp R.E.R. d.o.o
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package eu.larkc.core.pluginregistry;

import static com.cyc.tool.subl.jrtl.nativeCode.subLisp.ConsesLow.list;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cyc.tool.subl.jrtl.nativeCode.subLisp.Errors;
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.SubLThread;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLConsPair;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLString;

import eu.larkc.core.Larkc;
import eu.larkc.core.LarkcKBStatus;
import eu.larkc.core.pluginregistry.query.SparqlQuery;
import eu.larkc.core.pluginregistry.query.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.pluginregistry.query.exceptions.SparqlQueryTransformException;
import eu.larkc.plugin.Plugin;
import eu.larkc.shared.Resources;

/**
 * The LarKC platform plug-in registry. This class stores all the plug-in meta
 * data and is able to execute queries agaisnt the plug-ins KB
 * 
 * @author Blaz Fortuna, Luka Bradesko
 * 
 */
public class PluginRegistry {
	private static Logger logger = LoggerFactory
			.getLogger(PluginRegistry.class);

	private String PLUGIN_DIR = "." + File.separatorChar + "plugins";

	// count for registered plug-ins. Used also to generate unique id in the
	// internal kb.
	static int iPluginCount = 0;
	private final HashMap<URI, Class<?>> javaPluginClassH;

	// implementation of SubL Thread used to execute commands
	// against Cyc reasoning engine and internal KB
	abstract class SublThreadLink extends SubLThread {

		private boolean executedOk;

		public SubLObject result;
		public String sresult;

		public SublThreadLink() {
			super(null, "Plug-in registry");
			executedOk = false;
		}

		public SubLObject askQuery(String sQuery) {
			return CycUtil.askQuery(sQuery, false);
		}

		/**
		 * actual execution against Cyc RE and KB happens here
		 */
		public abstract void runImpl();

		@Override
		public void run() {
			try {
				runImpl();
			} catch (Exception e) {
				e.printStackTrace();
				synchronized (this) {
					executedOk = false;
				}
			} finally {
				synchronized (this) {
					executedOk = true;
				}
			}
		}

		/**
		 * Checkes if the query has successfully executed
		 * 
		 * @return true if query successfully executed
		 */
		public synchronized boolean isExecutedOk() {
			return executedOk;
		}
	}

	/**
	 * Initializes the registry and populates its KB (ex Cyc KB).
	 * 
	 * @param pluginsPath
	 *            the directory where plug-ins are stores. If null default will
	 *            be taken
	 */
	public PluginRegistry(String pluginsPath) {
		if (pluginsPath != null && pluginsPath.length() != 0) {

			// check if path exists
			File file = new File(PLUGIN_DIR);
			if (file.isDirectory() && file.getAbsoluteFile().exists())
				PLUGIN_DIR = pluginsPath;
			else
				logger.warn(pluginsPath
						+ " is not a valid directory. Default ./plugins will be used for plug-ins import!");

		}

		if (Larkc.getKBStatus() == LarkcKBStatus.NOT_INITIALIZED)
			initializeLarkcKb();
		javaPluginClassH = new HashMap<URI, Class<?>>();
	}

	/**
	 * Initializes the registry and populates its KB (ex Cyc KB).
	 */
	public PluginRegistry() {
		this(null);
	}

	/**
	 * Instantiates the plug-in
	 * 
	 * @param pluginUri
	 * @return plug-in instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public Plugin getNewPluginInstance(URI pluginUri)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Class<?> plugin = javaPluginClassH.get(pluginUri);
		if (plugin == null)
			return null;

		Constructor<?>[] constructors = plugin.getDeclaredConstructors();

		for (Constructor<?> constructor : constructors) {
			if (constructor.getGenericParameterTypes().length == 1) {
				return (Plugin) constructor
						.newInstance(new Object[] { pluginUri });
			}
		}

		throw new InstantiationException("Cannot instantiate the "
				+ pluginUri.getLocalName()
				+ " plug-in. It doesn't have (URI _plugInName) constructor!");
	}

	/**
	 * Initialize the core concepts of the Larkc upper-level ontology. All the
	 * concepts and assertions are read from the rdf file, wrtten in Turtle
	 * format.
	 */
	private void initializeLarkcKb() {
		// read larkc ontology
		// InputStream fstream = ClassLoader.getSystemClassLoader()
		// .getResourceAsStream(LARKC_RDF);

		Larkc.setKBStatus(LarkcKBStatus.INITIALIZING);

		InputStream fstream = this.getClass().getClassLoader()
				.getResourceAsStream(Resources.LARKC_RDF);

		try {
			if (fstream == null) {
				logger.error("Cannot find the " + Resources.LARKC_RDF
						+ " file. PLUGIN REGISTRY MIGHT NOT WORK!!");
				Larkc.setKBStatus(LarkcKBStatus.NOT_INITIALIZED);
				return;
			}
			CycUtil.loadRdfTurtle(fstream);
		} catch (Exception e) {
			logger.error("Error parsing the " + Resources.LARKC_RDF
					+ ". PLUGIN REGISTRY MIGHT NOT WORK!! " + e.getMessage());
			Larkc.setKBStatus(LarkcKBStatus.NOT_INITIALIZED);
		}

		// add a rule used for inferring connections between plug-ins
		String mtStr = "BaseKB";
		String forwardRuleStr = "(#$implies " + "  (#$and "
				+ "    (#$genls ?X #$larkc-Plugin) "
				+ "    (#$genls ?Y #$larkc-Plugin) "
				+ "    (#$larkc-hasOutputType ?X ?TYPE) "
				+ "    (#$larkc-hasInputType ?Y ?TYPE1) "
				+ "    (#$genls ?TYPE ?TYPE1))"
				// + "    (#$isa ?Z ?X)"
				// + "    (#$isa ?V ?Y))"
				+ "  (#$larkc-pluginByDataConnectsTo ?X ?Y))";
		CycUtil.addForwardRule(forwardRuleStr, mtStr);
		Larkc.setKBStatus(LarkcKBStatus.INITIALIZED);
	}

	/**
	 * Load plug-ins from the ini file and from PLATFORM/plugins or specified
	 * dir (over runtime params).
	 */
	public void loadPlugins() {

		// checks files and directories in PLATFORM/plugins
		File pluginsDir = new File(PLUGIN_DIR);
		File[] pluginFiles = pluginsDir.listFiles();
		if (pluginFiles != null && pluginFiles.length != 0) {
			for (File file : pluginFiles) {
				findPlugins(file);
			}
		} else
			logger.warn("No plug-ins in the plugins directory in the "
					+ PLUGIN_DIR + ". Using only plugins.ini");

		try {
			// Open the plugins.ini where the additional plug-in list is written
			InputStream fstream = this.getClass().getClassLoader()
					.getResourceAsStream(Resources.PLUGINS_INI);

			if (fstream == null) {
				throw new RuntimeException(Resources.PLUGINS_INI
						+ " not found. Plug-in registry will not work.");
			}

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith("//") || strLine.trim().isEmpty())
					continue;
				if (strLine.endsWith(".wsdl") || strLine.endsWith(".larkc")) {
					strLine.replace('/', File.separatorChar);
					strLine.replace('\\', File.separatorChar);
					findPlugins(new File(strLine));
				} else
					logger.warn("Invalid line in the " + Resources.PLUGINS_INI
							+ " file:" + strLine);
			}// read plugins.ini
			in.close();

		} catch (IOException e) {
			logger.error("Error reading the " + Resources.PLUGINS_INI
					+ " file:" + e.getMessage());
			Errors.handleError(e);
		}
	}

	/**
	 * Returns the URIs of all the plug-ins available to the platform
	 * 
	 * @return Collection of Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllPlugins() throws PluginRegistryQueryException {
		return getPluginTypes("larkc-Plugin");
	}

	/**
	 * Returns the URIs of all the Decider plug-ins available to the platform
	 * 
	 * @return Collection of Decider Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllDeciders() throws PluginRegistryQueryException {
		return getPluginTypes("larkc-Decider");
	}

	/**
	 * Returns the URIs of all the Identifier plug-ins available to the platform
	 * 
	 * @return Collection of Identifier Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllIdentifiers()
			throws PluginRegistryQueryException {
		return getPluginTypes("larkc-Identifier");
	}

	/**
	 * Returns the URIs of all the Transformer plug-ins available to the
	 * platform
	 * 
	 * @return Collection of Transformer Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllTransformers()
			throws PluginRegistryQueryException {
		return getPluginTypes("larkc-InformationSetTransformer");
	}

	/**
	 * Returns the URIs of all the Selecter plug-ins available to the platform
	 * 
	 * @return Collection of Selecter Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllSelecters()
			throws PluginRegistryQueryException {
		return getPluginTypes("larkc-Selecter");
	}

	/**
	 * Returns the URIs of all the Reasoner plug-ins available to the platform
	 * 
	 * @return Collection of Reasoner Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getAllReasoners()
			throws PluginRegistryQueryException {
		return getPluginTypes("larkc-Reasoner");
	}

	/**
	 * Returns true if _plugin is a LarKC plugin
	 * 
	 * @param _plugin
	 * 
	 * @return Collection of Plugin URIs
	 * @throws PluginRegistryQueryException
	 */
	public boolean isLarkCPlugin(URI _plugin)
			throws PluginRegistryQueryException {

		String cycQuery = "(#$thereExists ?X " + "  (#$and "
				+ "    (#$larkc-hasUri ?X \"" + _plugin + "\")"
				+ "    (#$larkc-hasUri ?X ?Y)))";

		/*
		 * String cycQuery = "(#$thereExists ?X " + "   (#$and " +
		 * "		(#$genls #$larkc-Plugin ?X)" + "		(#$larkc-hasUri ?X \"" + _plugin
		 * + "\")))";
		 */
		return !getPluginCyc(cycQuery).isEmpty();
	}

	/**
	 * Returns the Endpoint (how to access it) of the given plug-ins
	 * 
	 * @param _Plugin
	 *            URI of the plug-in
	 * @return Endpoint location
	 * @throws PluginRegistryQueryException
	 */
	public String getPluginEndpoint(final URI _Plugin)
			throws PluginRegistryQueryException {

		class QueryThread extends SublThreadLink {
			String endpoint = null;

			@Override
			public void runImpl() {
				String name = _Plugin.stringValue();
				String query = "(#$thereExists ?X " + "   (#$and "
						+ "		(#$larkc-hasUri ?X \"" + name + "\")"
						+ "		(#$larkc-hasEndpoint ?X ?Y)))";

				SubLObject result = super.askQuery(query);
				SubLConsPair SubLendpoint = (SubLConsPair) result.first()
						.first();
				endpoint = SubLendpoint.getDottedElement().toString();
			}

			public String getEndpoint() {
				return endpoint;
			}
		}
		QueryThread qt = new QueryThread();

		// start the query thread
		qt.start();
		// wait for the query to finish
		try {
			qt.join();
		} catch (InterruptedException e) {
			throw new PluginRegistryQueryException();
		}
		// check all ok executed
		if (!qt.isExecutedOk()) {
			throw new PluginRegistryQueryException();
		}
		// all fine, return the plug-in endpoint
		return qt.getEndpoint();
	}

	/**
	 * Returns a collection of all the plug-ins of the type sPluginTypeConcept
	 * 
	 * @param sPluginTypeConcept
	 * @return collection of plug-in URIs
	 * @throws PluginRegistryQueryException
	 */
	private Collection<URI> getPluginTypes(final String sPluginTypeConcept)
			throws PluginRegistryQueryException {
		String cycQuery = "(#$thereExists ?X " + "   (#$and "
				+ "		(#$genls ?X #$" + sPluginTypeConcept + ")"
				+ "		(#$larkc-hasUri ?X ?URN)))";
		return getPluginCyc(cycQuery);
	}

	/**
	 * Returns a collection of all the plug-ins corresponding to cycQuery
	 * 
	 * @param sPluginTypeConcept
	 * @return collection of plug-in URIs
	 * @throws PluginRegistryQueryException
	 */
	private Collection<URI> getPluginCyc(final String cycQuery)
			throws PluginRegistryQueryException {
		// prepare cyc query thread link
		class QueryThread extends SublThreadLink {
			private Collection<URI> plugins = new ArrayList<URI>();

			@Override
			public void runImpl() {
				SubLObject result = super.askQuery(cycQuery);
				for (int i = 0; i < result.size(); i++) {
					if (result.get(i).isNil())
						continue;
					SubLConsPair urn = (SubLConsPair) result.get(i).first();
					String stringy = urn.getDottedElement().toString()
							.replaceAll("\"", "");
					plugins.add(new URIImpl(stringy));
				}
			}

			public Collection<URI> getAllPlugins() {
				return plugins;
			}
		}
		QueryThread qt = new QueryThread();

		// start the query thread
		qt.start();
		// wait for the query to finish
		try {
			qt.join();
		} catch (InterruptedException e) {
			throw new PluginRegistryQueryException();
		}
		// check all ok executed
		if (!qt.isExecutedOk()) {
			throw new PluginRegistryQueryException();
		}
		// all fine, return the list of plug-ins
		return qt.getAllPlugins();
	}

	/**
	 * Returns a collection of all the plug-ins that correspond to the specified
	 * input SPARQL query
	 * 
	 * @param sparqlQuery
	 * @return collection of plug-in URIs
	 * @throws MalformedSparqlQueryException
	 * @throws SparqlQueryTransformException
	 * @throws PluginRegistryQueryException
	 */
	public Collection<URI> getPluginSparql(final String sparqlQuery)
			throws MalformedSparqlQueryException,
			SparqlQueryTransformException, PluginRegistryQueryException {

		// translate sparql query to cyc query
		SparqlQuery query = new SparqlQuery(sparqlQuery);
		final String cycQuery = query.toCycQuery();
		return getPluginCyc(cycQuery);
	}

	/**
	 * Registers the plug-in or more plug-ins in the directory, but only one
	 * level deep
	 * 
	 * @param fileOrDir
	 *            directory or a file location of the plug-in
	 */
	private void findPlugins(File fileOrDir) {
		InputStream wsdlFile = null;
		File wsdlFileParent = null;

		if (fileOrDir.isDirectory()) {
			for (File file : fileOrDir.listFiles()) {
				if (!file.isDirectory())// only check directories one level deep
					findPlugins(file);
			}// list files in the sub-directory
			return;// after scanned all the files it already registered whatever
			// it had. So the method should end.
		} else {
			String fileOrDirName = fileOrDir.getName();
			if (fileOrDirName.endsWith(".wsdl")) {
				try {
					wsdlFile = new FileInputStream(fileOrDir);
				} catch (FileNotFoundException e) {
					logger.warn("File doesn't exists: "
							+ fileOrDir.getAbsolutePath());
					return;
				}
				wsdlFileParent = fileOrDir.getParentFile();
			} else if (fileOrDirName.endsWith(".larkc")
					|| fileOrDirName.endsWith(".jar")) {
				try {
					String unzipDirName = fileOrDirName.substring(0,
							fileOrDirName.lastIndexOf('.'));
					File unzipWhere = new File(fileOrDir.getParentFile()
							.getAbsolutePath() + File.separator + unzipDirName);

					// if the directory exists already, unzip anyway since the
					// plug-in might have been updated
					if (unzipWhere.exists()) {
						logger.debug(
								"Plug-in {} was already registered. Redeploying plug-in...",
								unzipDirName);
					}
					unzip(fileOrDir, unzipDirName);
					findPlugins(unzipWhere);
					return;
				} catch (ZipException e) {
					logger.warn(
							"Cannot extract " + fileOrDir.getAbsolutePath(), e);
				} catch (IOException e) {
					logger.warn(
							"Cannot extract " + fileOrDir.getAbsolutePath(), e);
				}
			} else
				// ignore other files
				return;
		}

		Document document;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			document = documentBuilder.parse(wsdlFile);
		} catch (Exception e) {
			logger.warn("Error parsing the wsdl file " + wsdlFile + " in:"
					+ fileOrDir.getAbsolutePath(), e);
			return;
		}

		NodeList plugins = document.getElementsByTagName("wsdl:service");
		for (int iPluginNum = 0; iPluginNum < plugins.getLength(); iPluginNum++) {
			Element plugin = (Element) plugins.item(iPluginNum);
			String sPluginName = plugin.getAttribute("name");
			String sRdfReferece = plugin.getAttribute("sawsdl:modelReference");
			String sRdfFile = sRdfReferece.split("#")[0];

			InputStream rdfFileStream = findFileInJarOrDir(wsdlFileParent,
					".rdf");
			if (rdfFileStream == null) {
				logger.warn("Cannot find the rdf file in "
						+ wsdlFileParent.getAbsolutePath()
						+ ". Checking if old (obsolete) plug-in model is used... ");
				File oldRdfLocation = new File(wsdlFileParent.getAbsolutePath()
						+ File.separator + sRdfFile);
				try {
					rdfFileStream = new FileInputStream(oldRdfLocation);
				} catch (FileNotFoundException e) {
					logger.warn("Cannot find the rdf file in "
							+ oldRdfLocation.getAbsolutePath()
							+ ". Skipping this plug-in registration ");
					return;
				}
			}

			NodeList endpoints = plugin.getElementsByTagName("wsdl:endpoint");
			String sLocation = ((Element) endpoints.item(0))
					.getAttribute("location");

			if (sLocation.startsWith("java:")) {
				loadPluginClass(new URIImpl(sPluginName),
						sLocation.substring(5), wsdlFileParent);
				logger.info("Registered the " + sLocation.substring(5));
			} else {
				logger.warn("Other endpoints than java: are currently not supported ("
						+ sLocation + ")");
			}

			try {
				CycUtil.loadRdfTurtle(rdfFileStream);

				SubLObject sblPlugin = CycUtil.addRdfTerm(sRdfReferece);
				SubLObject hasUri = CycUtil
						.addRdfTerm("http://larkc.eu/plugin#hasUri");
				SubLString uri = SubLObjectFactory.makeString(sPluginName);
				SubLObject cycAssertion = list(hasUri, sblPlugin, uri);
				CycUtil.addAssertion(cycAssertion, CycUtil.mtStr);

				SubLObject hasEndpoint = CycUtil
						.addRdfTerm("http://larkc.eu/plugin#hasEndpoint");
				SubLString endpoint = SubLObjectFactory.makeString(sLocation);
				cycAssertion = list(hasEndpoint, sblPlugin, endpoint);
				CycUtil.addAssertion(cycAssertion, CycUtil.mtStr);
			} catch (Exception e) {
				logger.warn("Error parsing the " + sRdfFile + "("
						+ e.getMessage() + ")");
			}

		}// for all services in wsdl
	}

	/**
	 * Lists the files from given jar file or directory and returns the first
	 * occurrence of .wsdl file
	 * 
	 * @param _theJar
	 * @return tha InputStream of the wsdl file
	 */
	private InputStream findFileInJarOrDir(File _theJar, String _suffix) {
		try {
			if (_theJar.isDirectory()) {
				for (File file : _theJar.listFiles()) {
					if (!file.isDirectory() && file.getName().endsWith(_suffix)) {
						return new FileInputStream(file);
					}
				}// list files in the subdirectory
			} else {
				JarFile jarFile;
				jarFile = new JarFile(_theJar);

				Enumeration<JarEntry> enumr = jarFile.entries();
				while (enumr.hasMoreElements()) {
					JarEntry entry = (JarEntry) enumr.nextElement();
					if (entry.getName().endsWith(_suffix)) {
						URLClassLoader cl = URLClassLoader
								.newInstance(new URL[] { _theJar.toURI()
										.toURL() });
						InputStream is = cl
								.getResourceAsStream(entry.getName());
						return is;
					}
				}
			}

		} catch (IOException e) {
			logger.warn("Error reading from the " + _theJar.getAbsolutePath());
		}
		return null;
	}

	/**
	 * Finds the class file and loads it with ClassLoader
	 * 
	 * @param _class
	 *            name with package included
	 * @param pluginPath
	 *            path to the root of where the class is
	 */
	@SuppressWarnings("unchecked")
	private void loadPluginClass(URI _pluginName, String _class, File file) {
		try {
			// find external plug-in libraries
			ArrayList<URL> vUrl = new ArrayList<URL>();
			File libDir = new File(file.getCanonicalPath() + File.separator
					+ "lib");
			if (libDir.exists()) {
				for (File jarlib : libDir.listFiles()) {
					if (jarlib.getName().endsWith(".jar")) {
						vUrl.add(jarlib.toURI().toURL());
					}
				}// list files in the subdirectory
			} else {
				logger.warn("Can not find the " + libDir.getCanonicalPath()
						+ ". Assuming this plug-in doesn't have any libraries");
			}

			URL url = file.toURI().toURL();
			vUrl.add(url);
			URL[] urls = new URL[vUrl.size()];
			for (int i = 0; i < vUrl.size(); i++) {
				urls[i] = vUrl.get(i);
			}
			// Create a new class loader with the directory
			ClassLoader classLoader = new URLClassLoader(urls);
			// load the class
			Class<Plugin> pluginClass = (Class<Plugin>) classLoader
					.loadClass(_class);

			javaPluginClassH.put(_pluginName, pluginClass);
		} catch (MalformedURLException e) {
			Errors.handleError(e);
		} catch (ClassNotFoundException e) {
			logger.warn("Classloader cannot find class: " + e.getMessage()
					+ "! Plug-in not loaded!");
		} catch (ClassCastException e) {
			Errors.handleError("Plugin \"" + _class + "\" must implement"
					+ Plugin.class.getName() + " interface", e);
		} catch (IOException e) {
			logger.error("Error loading " + _class + "! " + e.getMessage());
		} catch (IncompatibleClassChangeError e) {
			logger.warn("Classloader cannot load plug-in class: "
					+ _class
					+ "! The plug-in API had changed. Pleas recompile the plug-in. Plug-in not loaded!");
		}
	}

	/*
	 * private void setDecider(Class<Plugin> theDecider) { this.defaultDecider =
	 * theDecider; }
	 */

	/**
	 * Unzips the .larkc or other file into unzipSubDir directory
	 */
	private void unzip(File file, String unzipSubDir) throws ZipException,
			IOException {
		int BUFFER = 2048;
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ZipEntry entry;
		ZipFile zipfile = new ZipFile(file);

		Enumeration<? extends ZipEntry> e = zipfile.entries();
		while (e.hasMoreElements()) {
			entry = (ZipEntry) e.nextElement();
			is = new BufferedInputStream(zipfile.getInputStream(entry));
			int count;
			byte data[] = new byte[BUFFER];

			File where = new File(file.getParentFile().getAbsolutePath()
					+ File.separator + unzipSubDir + File.separator
					+ entry.getName());
			if (entry.isDirectory()) {
				where.mkdirs();
				continue;
			} else {
				where.getParentFile().mkdirs();
				where.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(
					where.getCanonicalFile());
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
			is.close();
		}
	}
}
