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

/**
 * @author vassil
 */
public class RdfGraphInMemory extends RdfGraphBase {

	private SetOfStatements data;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param graph
	 *            is the named of the graph
	 * @param data
	 *            are the statements to associated
	 */
	public RdfGraphInMemory(URI graph, Iterable<Statement> data) {
		super(graph);
		this.data = new SetOfStatementsImpl(data);
	}

	/**
	 * Constructor
	 * 
	 * @param graph
	 *            is the named of the graph
	 * @param data
	 *            are the statements to associated
	 */
	public RdfGraphInMemory(URI graph, Collection<Statement> data) {
		super(graph);
		this.data = new SetOfStatementsImpl(data);
	}

	public CloseableIterator<Statement> getStatements() {
		return data.getStatements();
	}

	public boolean equals(Object o) {
		if (o instanceof RdfGraphInMemory == false) {
			return false;
		}
		return data.equals(((RdfGraphInMemory) o).data);
	}

	public int hashCode() {
		return getName().toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.InformationSet#toRDF(eu.larkc.core.data.SetOfStatements
	 * )
	 */
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
					+ "RDFGraphInMemory"), new LiteralImpl(sw.toString())));
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
}
