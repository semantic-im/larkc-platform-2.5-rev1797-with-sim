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
package eu.larkc.core.parallelization;

import java.util.HashMap;
import java.util.List;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.plugin.Plugin;

/**
 * This simple class can be used to create thread pools which allow an execution
 * of multiple plug-in types or instances of the same plug-in type.
 * 
 * @author Matthias Assel
 * 
 */
public interface MultiThreading {

	/**
	 * Automates the creation of different multi-threading instances.
	 */
	public final static MultiThreading INSTANCE = MultiThreadingImpl
			.getInstance();

	/**
	 * Invokes a thread pool for a particular list of plug-ins. The method
	 * firstly creates a new thread pool based on the number of available cores
	 * or size of the list of plug-ins. Afterwards, the instantiated thread pool
	 * is executed.
	 * 
	 * @param pluginList
	 * @throws MultiThreadingException
	 */
	public void invokeThreadPool(
			HashMap<List<Plugin>, SetOfStatements> pluginList)
			throws MultiThreadingException;

	/**
	 * Allows for retrieving the synchronized results from all instances of the
	 * thread pool.
	 * 
	 * @return SetOfStatements
	 * @throws MultiThreadingException
	 */
	public SetOfStatements getSynchronizedResults()
			throws MultiThreadingException;
}
