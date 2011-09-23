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
import eu.larkc.core.pluginManager.Message;

/**
 * This class represents a path. One workflow can contain multiple paths. Each
 * path has to have exactly one input and one output. Multiple endpoints can be
 * associated with a path.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class Path implements PathInterface {

	/** ID of the path. */
	private String id;
	/** The input of this path. */
	private Input input;
	/** The output of this path. */
	private Output output;

	/**
	 * Custom constructor that takes an input and an output as parameter.
	 * 
	 * @param pathId
	 *            the id.
	 * @param in
	 *            the input.
	 * @param out
	 *            the output.
	 */
	public Path(String pathId, Input in, Output out) {
		id = pathId;
		input = in;
		output = out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.executor.PathInterface#execute(eu.larkc.core.data.
	 * SetOfStatements)
	 */
	@Override
	public void execute(SetOfStatements query) {
		input.putQuery(query);

		// send NEXT instruction to the manager of the last plugin
		output.accept(new ControlMessage(Message.NEXT, id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.executor.PathInterface#getNextResult()
	 */
	@Override
	public SetOfStatements getNextResults() {
		return output.getNextResults();
	}

}
