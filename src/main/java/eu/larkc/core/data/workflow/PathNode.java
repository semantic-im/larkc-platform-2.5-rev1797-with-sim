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
package eu.larkc.core.data.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all information that is needed and can be extracted out
 * of a workflow description to create a path.
 * 
 * @author norbert
 * 
 */
public class PathNode {

	private List<String> inputIds;
	private String ouputId;

	/**
	 * Constructor.
	 */
	public PathNode() {
		inputIds = new ArrayList<String>();
		ouputId = null;
	}

	/**
	 * Setter. Sets or updates the inputId to the passed value.
	 * 
	 * @param inputId
	 *            the inputId to set
	 */
	public void addInputId(String inputId) {
		inputIds.add(inputId);
	}

	/**
	 * Getter. Retrieves the inputId.
	 * 
	 * @return the inputId
	 */
	public List<String> getInputId() {
		return inputIds;
	}

	/**
	 * Setter. Sets or updates the outputId to the passed value.
	 * 
	 * @param outputId
	 *            the outputId to set
	 */
	public void setOutputId(String outputId) {
		this.ouputId = outputId;
	}

	/**
	 * Getter. Retrieves the outputId.
	 * 
	 * @return the outputId
	 */
	public String getOutputId() {
		return ouputId;
	}

}
