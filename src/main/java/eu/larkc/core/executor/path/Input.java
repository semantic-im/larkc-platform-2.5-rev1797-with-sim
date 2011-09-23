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

import java.util.ArrayList;
import java.util.List;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.queue.Queue;

/**
 * This class implements an input. An input is used to give an input to a
 * workflow.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class Input {

	/** The input queues of the workflow that the input is connected to. */
	private List<Queue<SetOfStatements>> pathInputQueues;

	/**
	 * Constructor to create a new input.
	 */
	public Input() {
		pathInputQueues = new ArrayList<Queue<SetOfStatements>>();
	}

	/**
	 * This method starts the execution of the workflow.
	 * 
	 * @param query
	 *            The query which is passed to the workflow.
	 * 
	 */
	public void putQuery(SetOfStatements query) {
		for (Queue<SetOfStatements> pathInputQueue : pathInputQueues) {
			pathInputQueue.put(query);
		}
	}

	/**
	 * Sets the input queue which is used to give the query to the workflow.
	 * 
	 * @param inputQueue
	 *            the input queue
	 */
	public void addPathInputQueue(Queue<SetOfStatements> inputQueue) {
		pathInputQueues.add(inputQueue);
	}
}
