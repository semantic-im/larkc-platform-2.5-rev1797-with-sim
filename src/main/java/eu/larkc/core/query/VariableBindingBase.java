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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;

import eu.larkc.core.data.CheatingInMemoryObjectStore;
import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.data.VariableBindingValue;
import eu.larkc.core.util.RDFConstants;

/**
 * VariableBindingBase is an abstract class to implement asynchronous streaming
 * of RDF variable bindings. All implementing classes must: 1. Invoke
 * super.notifyAll() after setting bindNames 2. Put next the next binding
 * results with results.put(X).
 * 
 * @author vassil
 * 
 */
public class VariableBindingBase implements VariableBinding {

	protected transient boolean isClosed = false;
	protected transient String[] bindNames;
	protected transient BlockingQueue<BindingSet> results;
	protected transient CloseableIterator<Binding> iterator;
	private final static long serialVersionUID = 1L;

	public VariableBindingBase() {
		this.results = new ArrayBlockingQueue<BindingSet>(1024);
	}

	public synchronized List<String> getVariables() {
		if (bindNames == null) {
			try {
				wait();
			} catch (InterruptedException ie) {
			}
		}
		return Collections.unmodifiableList(Arrays.asList(bindNames));
	}

	public synchronized CloseableIterator<Binding> iterator() {
		if (iterator == null) {
			iterator = new BindingIterator();
		}
		return iterator;
	}

	/**
	 * This class iterates an asynchronous blocking and returns back the results
	 * on demand. It works as a local result cache.
	 * 
	 * @author vassil
	 * 
	 */
	public class BindingIterator implements CloseableIterator<Binding> {

		private BindingSet next;

		public synchronized boolean hasNext() {
			while (next == null) {
				try {
					next = results.take();
				} catch (InterruptedException ie) {
				}
			}
			if (next instanceof FinalBindingSet) {
				return false;
			}
			return true;
		}

		public synchronized Binding next() {
			if (hasNext() == false) {
				throw new NoSuchElementException();
			}
			WrappedBinding result = new WrappedBinding(next);
			next = null;
			return result;
		}

		public void remove() {
		}

		public synchronized void close() {
			isClosed = true;
		}

		public synchronized boolean isClosed() {
			return isClosed;
		}
	}

	/**
	 * Helper class to transform a Sesame to LarKC binding.
	 * 
	 * @author vassil
	 * 
	 */
	public class WrappedBinding implements Binding {

		public List<Value> values = new ArrayList<Value>();

		public WrappedBinding(BindingSet bs) {
			if (bs == null) {
				throw new IllegalArgumentException("null!");
			}
			for (int i = 0; i < bs.size(); i++) {
				values.add(bs.getValue(bindNames[i]));
			}
		}

		public WrappedBinding(List<Value> values) {
			if (values == null) {
				throw new IllegalArgumentException("null!");
			}
			this.values = values;
		}

		public List<Value> getValues() {
			return Collections.unmodifiableList(values);
		}

		public String toString() {
			return values.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Binding) {
				Binding b = (Binding) obj;
				return b.getValues().equals(values);
			}
			return super.equals(obj);
		}
	}

	/**
	 * Helper class to denote the end of the streamed solutions.
	 * 
	 * @author vassil
	 * 
	 */
	public class FinalBindingSet implements BindingSet {

		public Iterator<org.openrdf.query.Binding> iterator() {
			throw new UnsupportedOperationException();
		}

		public Set<String> getBindingNames() {
			throw new UnsupportedOperationException();
		}

		public org.openrdf.query.Binding getBinding(String s) {
			throw new UnsupportedOperationException();
		}

		public boolean hasBinding(String s) {
			throw new UnsupportedOperationException();
		}

		public Value getValue(String s) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			throw new UnsupportedOperationException();
		}
	}

	public static final URI vbbPredicate = new URIImpl(
			RDFConstants.LARKC_NAMESPACE + "vbbPredicate");
	public static final BNode vbbBNode = new BNodeImpl(
			RDFConstants.LARKC_NAMESPACE + "vbbPredicate");

	/**
	 * 
	 * Return an RDF representation of this object TODO:This should be fixed to
	 * really use RDF and not the CheatingInMemoryObjectStore
	 * 
	 * @return
	 */
	public SetOfStatements toRDF() {
		String objectID = CheatingInMemoryObjectStore.getInstance()
				.storeObject(this);
		StatementImpl statement = new StatementImpl(vbbBNode, vbbPredicate,
				new LiteralImpl(objectID));

		ArrayList<Statement> list = new ArrayList<Statement>();
		list.add(statement);
		return new SetOfStatementsImpl(list);
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
		SetOfStatementsImpl s = null;
		if (data instanceof SetOfStatementsImpl == false) {
			s = (SetOfStatementsImpl) data;
		} else {
			s = new SetOfStatementsImpl(data.getStatements());
		}
		ValueFactory vf = new ValueFactoryImpl();
		s.getData().add(
				new StatementImpl(vf.createBNode(), new URIImpl(
						DataFactory.LARKC_NS + "VariableBinding"),
						new VariableBindingValue(this)));
		return s;
	}
}
