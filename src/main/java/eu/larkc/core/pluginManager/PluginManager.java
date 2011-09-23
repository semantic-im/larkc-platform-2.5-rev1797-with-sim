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
package eu.larkc.core.pluginManager;

import java.util.List;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.queue.Queue;

/**
 * A Plugin Manager is a container within which a particular instantiation of a
 * Plugin lives during the execution of the pipeline.
 * 
 * The Plugin manager makes the life of the plugin developer easier by providing
 * all those features related to communication with the rest of the pipeline.
 * 
 * @author Mick Kerrigan, Barry Bishop
 */
public interface PluginManager {

	/**
	 * The accept method allows a particular party to send a control message to
	 * the Plugin Manger.
	 * 
	 * @see Message
	 * 
	 * @param message
	 *            the control message being sent from another PluginManager
	 */
	public void accept(ControlMessage message);

	/**
	 * The setPrevious method is used to inform the PluginManager of the
	 * previous PluginManager in the pipeline, i.e. the PluginManger from which
	 * it should request data
	 * 
	 * @param thePluginManager
	 *            the PluginManager responsible for managing the previous plugin
	 *            in the pipeline
	 */
	public void addPrevious(PluginManager thePluginManager);

	/**
	 * This method returns the input queues of the manager.
	 * 
	 * @param pathId
	 *            path id
	 * 
	 * @return the input queues
	 */
	public List<Queue<SetOfStatements>> getInputQueues(String pathId);

	/**
	 * This method sets the input queues of the manager.
	 * 
	 * @param theInputQueues
	 *            the input queues
	 * @param pathId
	 *            path id
	 */
	public void setInputQueues(List<Queue<SetOfStatements>> theInputQueues,
			String pathId);

	/**
	 * This method adds an input queue to the list of input queues.
	 * 
	 * @param inputQueue
	 *            the new input queue
	 * @param pathId
	 *            path id
	 */
	public void addInputQueue(Queue<SetOfStatements> inputQueue, String pathId);

	/**
	 * This method returns the output queues of the manager.
	 * 
	 * @param pathId
	 *            path id
	 * 
	 * @return the output queues
	 */
	public List<Queue<SetOfStatements>> getOutputQueues(String pathId);

	/**
	 * This method sets the output queues of the manager.
	 * 
	 * @param theOutputQueues
	 *            the output queues
	 * @param pathId
	 *            path id
	 */
	public void setOutputQueues(List<Queue<SetOfStatements>> theOutputQueues,
			String pathId);

	/**
	 * This method adds an output queue to the list of output queues.
	 * 
	 * @param outputQueue
	 *            the new output queue
	 * @param pathId
	 *            path id
	 */
	public void addOutputQueue(Queue<SetOfStatements> outputQueue, String pathId);

	/**
	 * The start method should be called to instruct the Plugin Manager that it
	 * should begin the process of finding data for the plugin it manages and
	 * producing data for the next plugin in the pipeline.
	 */
	public void start();

	/**
	 * The stopWaiting method should be called to instruct the Plugin Manager
	 * that it should stop the process of finding data for the plugin it
	 * manages.
	 */
	public void stopWaiting();
}
