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
package eu.larkc.core.executor.path;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.pluginManager.ControlMessage;
import eu.larkc.core.pluginManager.PluginManager;
import eu.larkc.core.queue.Queue;

/**
 * This class implements an output. An output is used to get the output of a
 * workflow after executing it and providing the output to endpoints that are
 * connected to this output.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class Output {

	/** The output queue of the workflow that is connected to the output. */
	private Queue<SetOfStatements> pathOutputQueue;
	/** The plugin manager of the workflow that is connected to the output. */
	private PluginManager workflowPluginManager;

	/**
	 * Constructor to create a new sink.
	 */
	public Output() {
	}

	/**
	 * Sends a message to the plugin manager.
	 * 
	 * @param next
	 */
	public void accept(ControlMessage next) {
		workflowPluginManager.accept(next);
	}

	/**
	 * Sets the output queue of the path.
	 * 
	 * @param outputQueue
	 *            the ouput queue
	 */
	public void setPathOutputQueue(Queue<SetOfStatements> outputQueue) {
		pathOutputQueue = outputQueue;
	}

	/**
	 * Returns the next results that the workflow produces.
	 * 
	 * @return the next results
	 */
	public SetOfStatements getNextResults() {
		return pathOutputQueue.take();
	}

	/**
	 * Setter. Sets or updates the workflowPluginManager to the passed value.
	 * 
	 * @param pluginManager
	 *            the workflowPluginManager to set
	 */
	public void setPluginManager(PluginManager pluginManager) {
		workflowPluginManager = pluginManager;
	}
}
