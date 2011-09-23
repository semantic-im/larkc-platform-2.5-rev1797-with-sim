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

import eu.larkc.core.Resource;

/**
 * The class represents remote resource's properties for the remote execution
 * with GAT
 * 
 * @author Alexey Cheptsov
 * 
 */

public class GatResourceDescription implements Resource {
	public String URI;
	public String FileAdaptor;
	public String Broker;
	public String LarKCDir;
	public String WorkDir;
	public String JavaDir;
	public String JavaArgs;
	@Deprecated
	public int NumMPIProcesses;

	/** Empty constructor */
	public GatResourceDescription() {

	}

	/**
	 * Constructor of the class for a standard application.
	 * 
	 * @param URI
	 *            A string with resource's URI in the GAT notation, e.g.
	 *            "any://localhost"
	 * @param FileAdaptor
	 *            A string with FileAdaptor's name, e.g. "local"
	 * @param Broker
	 *            A string with Broker's name, e.g. "local"
	 * @param LarKCDir
	 *            A string with the path to the directory containing a LarKC
	 *            platform's installation, e.g. "/home/user/LarKC"
	 * @param WorkDir
	 *            A string with the path to the working directory, i.e. where
	 *            the output will be created etc., e.g. "/home/user/tmp"
	 * @param JavaDir
	 *            A string with the path to the Java installation, e.g.
	 *            "/usr/bin"
	 * @param JavaArgs
	 *            A string containing JVM arguments, e.g. "-Xmx1024M"
	 */
	public GatResourceDescription(String URI, String FileAdaptor,
			String Broker, String LarKCDir, String WorkDir, String JavaDir,
			String JavaArgs) {
		this.URI = URI;
		this.FileAdaptor = FileAdaptor;
		this.Broker = Broker;
		this.LarKCDir = LarKCDir;
		this.WorkDir = WorkDir;
		this.JavaDir = JavaDir;
		this.JavaArgs = JavaArgs;
	}

	/**
	 * Constructor of the class for an MPI parallel application.
	 * 
	 * @param URI
	 *            A string with resource's URI in the GAT notation, e.g.
	 *            "any://localhost"
	 * @param FileAdaptor
	 *            A string with FileAdaptor's name, e.g. "local"
	 * @param Broker
	 *            A string with Broker's name, e.g. "local"
	 * @param LarKCDir
	 *            A string with the path to the directory containing a LarKC
	 *            platform's installation, e.g. "/home/user/LarKC"
	 * @param WorkDir
	 *            A string with the path to the working directory, i.e. where
	 *            the output will be created etc., e.g. "/home/user/tmp"
	 * @param JavaDir
	 *            A string with the path to the Java installation, e.g.
	 *            "/usr/bin"
	 * @param JavaArgs
	 *            A string containing JVM arguments, e.g. "-Xmx1024M"
	 * @param NumMPIProcesses
	 *            Number of requested CPU nodes
	 */

	public GatResourceDescription(String URI, String FileAdaptor,
			String Broker, String LarKCDir, String WorkDir, String JavaDir,
			String JavaArgs, int NumMPIProcesses) {
		this.URI = URI;
		this.FileAdaptor = FileAdaptor;
		this.Broker = Broker;
		this.LarKCDir = LarKCDir;
		this.WorkDir = WorkDir;
		this.JavaDir = JavaDir;
		this.JavaArgs = JavaArgs;
		this.NumMPIProcesses = NumMPIProcesses;
	}
}