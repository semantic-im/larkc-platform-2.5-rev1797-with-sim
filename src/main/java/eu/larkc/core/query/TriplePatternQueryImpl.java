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
package eu.larkc.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.util.RDFConstants;

/**
 * 
 * TODO Describe this type briefly. If necessary include a longer description
 * and/or an example.
 * 
 * @author ?
 * 
 */
public class TriplePatternQueryImpl implements TriplePatternQuery {

	private Collection<TriplePattern> patterns = new ArrayList<TriplePattern>();
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory
			.getLogger(TriplePatternQueryImpl.class);

	/**
	 * Creates a new triple pattern query using the given patterns.
	 * 
	 * @param thePatterns
	 *            patterns to use for this triple pattern query
	 */
	public TriplePatternQueryImpl(Collection<TriplePattern> thePatterns) {
		this.patterns.addAll(thePatterns);
	}

	public void add(TriplePattern theTriplePattern) {
		this.patterns.add(theTriplePattern);
	}

	public void remove(TriplePattern theTriplePattern) {
		this.patterns.remove(theTriplePattern);
	}

	public void clear() {
		this.patterns.clear();
	}

	public boolean isEmpty() {
		return this.patterns.isEmpty();
	}

	public Collection<TriplePattern> getTriplePatterns() {
		return patterns;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (TriplePattern tp : patterns) {
			sb.append(tp.toString() + "\n");
		}
		sb.append("]\n");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.query.Query#toRDF(eu.larkc.core.data.SetOfStatements)
	 */
	@Override
	public SetOfStatements toRDF(SetOfStatements data) {
		throw new RuntimeException("Not Impemented!");
	}

	/**
	 * Returns the RDF representation of this TriplePatternQuery as a
	 * SetOfStatements.
	 * 
	 * @return the RDF representation of this TriplePatternQuery as a
	 *         SetOfStatements.
	 */
	@Override
	public SetOfStatements toRDF() {
		/*
		 * TODO Discuss with other about generalization of this method
		 */
		Set<Statement> statements = new HashSet<Statement>();

		for (TriplePattern p : patterns) {

			if (p != null) {
				logger.debug(
						"S: {}, P: {}, O: {}",
						new Object[] { p.getSubject(), p.getPredicate(),
								p.getObject() });

				Resource res = null;
				URI uri = null;
				Value val = null;

				if (p.getSubject() == null)
					res = ValueFactoryImpl.getInstance().createBNode();
				else
					res = p.getSubject();

				if (p.getPredicate() == null)
					uri = new URIImpl(DataFactory.LARKC_NS + "SetOfStatements");
				else
					uri = p.getPredicate();

				if (p.getObject() == null)
					val = RDFConstants.LARKC_ATTVALUE;
				else
					val = p.getObject();

				statements.add(new StatementImpl(res, uri, val));
			}
		}

		return new SetOfStatementsImpl(statements);
	}
}
