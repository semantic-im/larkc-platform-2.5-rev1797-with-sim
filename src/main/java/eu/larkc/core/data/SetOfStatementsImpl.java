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
package eu.larkc.core.data;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.trig.TriGWriter;

import eu.larkc.core.data.iterator.SimpleCloseableIterator;

/**
 * This class is a in-memory set of RDF statements. It is not thread safe!
 * 
 * @author vassil
 * 
 */
public class SetOfStatementsImpl implements SetOfStatements {

	private static final long serialVersionUID = 1L;
	private final ArrayList<Statement> data;

	/**
	 * Default constructor that initializes an empty list.
	 */
	public SetOfStatementsImpl() {
		this(new ArrayList<Statement>());
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            passed by iterator
	 */
	public SetOfStatementsImpl(Iterable<Statement> data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		this.data = new ArrayList<Statement>();
		for (Statement st : data) {
			this.data.add(new StatementImpl(st.getSubject(), st.getPredicate(),
					st.getObject()));
		}
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            passed as a collection
	 */
	public SetOfStatementsImpl(Collection<Statement> data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		this.data = new ArrayList<Statement>(data);
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            passed as a closeableiterator
	 */
	public SetOfStatementsImpl(CloseableIterator<Statement> data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		this.data = new ArrayList<Statement>();
		while (data.hasNext()) {
			Statement s = data.next();
			this.data.add(s);
		}
	}

	public CloseableIterator<Statement> getStatements() {
		return new SimpleCloseableIterator<Statement>(data.iterator());
	}

	public boolean equals(Object o) {
		if (o instanceof SetOfStatementsImpl == false) {
			return false;
		}
		return data.equals(((SetOfStatementsImpl) o).data);
	}

	public int hashCode() {
		return data.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.InformationSet#toRDF(eu.larkc.core.data.SetOfStatements
	 * )
	 */
	@Override
	public SetOfStatements toRDF(SetOfStatements data) {
		try {
			StringWriter sw = new StringWriter();
			final TriGWriter writer = new TriGWriter(sw);
			writer.startRDF();
			CloseableIterator<Statement> iter = getStatements();
			while (iter.hasNext()) {
				Statement s = iter.next();
				writer.handleStatement(s);
			}

			writer.endRDF();

			ArrayList<Statement> list = new ArrayList<Statement>();
			list.add(new StatementImpl(ValueFactoryImpl.getInstance()
					.createBNode(), new URIImpl(DataFactory.LARKC_NS
					+ "SetOfStatements"), new LiteralImpl(sw.toString())));

			iter = data.getStatements();
			while (iter.hasNext()) {
				Statement s = iter.next();
				URI uri = (URI) s.getContext();
				if (s.getContext() == null) {
					uri = new URIImpl("http://larkc.eu/invocation/last_1");
				} else {
					uri = new URIImpl(s.getContext().stringValue() + "1");
				}
				list.add(new ContextStatementImpl(s.getSubject(), s
						.getPredicate(), s.getObject(), uri));
			}
			return new SetOfStatementsImpl(list);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the internal statement representation.
	 * 
	 * @return statements
	 */
	public Collection<Statement> getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int c = 0;
		StringBuffer sb = new StringBuffer(1000);
		while (c < 50 && c < data.size()) {
			sb.append(data.get(c));
			sb.append("\n");
			c++;
		}
		return sb.toString();
	}
}
