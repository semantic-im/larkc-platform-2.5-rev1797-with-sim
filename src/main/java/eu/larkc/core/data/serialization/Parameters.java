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
package eu.larkc.core.data.serialization;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.InformationSet;
import eu.larkc.core.data.SetOfStatements;

/**
 * Parameters is a helper type used to automate the
 * serialization/deserialization of InformationSet to and from RDF data.
 * 
 * @author vassil
 * 
 */
public interface Parameters extends SetOfStatements {

	/**
	 * Writes parameter to RDF data using a explicit parameterName. The
	 * parameterName should be always used to identify different parameters from
	 * the same type.
	 * 
	 * @param parameter
	 *            to be serialized
	 * @param parameterName
	 *            parameter overloading
	 */
	public void write(InformationSet parameter, URI parameterName);

	/**
	 * Writes parameter to RDF data using a auto-generated parameterName. This
	 * method should be used if each parameterType is used only one time.
	 * 
	 * @param parameter
	 *            to be serialized
	 */
	public void write(InformationSet parameter);

	/**
	 * Reads a parameters from the RDF statements. This method returns the first
	 * occurrence of the type.
	 * 
	 * @param parameterType
	 *            type to be constructed
	 * @return constructed type or null
	 */
	public InformationSet read(Class<? extends InformationSet> parameterType);

	/**
	 * Reads a parameters from the RDF statements. This method returns the first
	 * occurrence of the type with the specified parameterName.
	 * 
	 * @param parameterType
	 *            type to be constructed
	 * @param parameterName
	 *            is used to distinguish parameter overloading
	 * @return constructed type or null
	 */
	public InformationSet read(Class<? extends InformationSet> parameterType,
			URI parameterName);

	/**
	 * Returns all objects from a specified type that could be constructed from
	 * this RDF data.
	 * 
	 * @param parameterType
	 *            type to be constructed
	 * @return list of types
	 */
	public InformationSet[] readAll(
			Class<? extends InformationSet> parameterType);

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.data.SetOfStatements#getStatements()
	 */
	public CloseableIterator<Statement> getStatements();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.InformationSet#toRDF(eu.larkc.core.data.SetOfStatements
	 * )
	 */
	public SetOfStatements toRDF(SetOfStatements data);
}
