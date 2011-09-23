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
package eu.larkc.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;

/**
 * Utilities class for RDF.
 * 
 * @author spyros
 * 
 */
public class RDFUtilities {

	protected static Logger logger = LoggerFactory
			.getLogger(RDFUtilities.class);

	/**
	 * This method creates a canonical representation of the input by sorting
	 * triples and rewriting blank nodes. RDF canonicalization is not always
	 * possible, so this method might return two different representations for
	 * equivalent graphs, although this is unlikely and depends on how blank
	 * nodes are used.
	 * 
	 * @param iter
	 * @return the canonicalized triples
	 */
	public static List<Statement> canonicalize(CloseableIterator<Statement> iter) {
		ArrayList<Statement> l = new ArrayList<Statement>();
		while (iter.hasNext())
			l.add(iter.next());
		iter.close();
		Collections.sort(l, new Comparator<Statement>() {

			@Override
			public int compare(Statement arg0, Statement arg1) {
				int s = compareResources(arg0.getSubject(), arg1.getSubject());
				if (s != 0)
					return s;
				else
					s = compareResources(arg0.getPredicate(),
							arg1.getPredicate());
				if (s != 0)
					return s;
				else
					s = compareResources(arg0.getObject(), arg1.getObject());
				if (s != 0)
					return s;
				else
					s = compareResources(arg0.getContext(), arg1.getContext());
				return s;
			}

			private int compareResources(Value arg0, Value arg1) {
				if (arg0 == null && arg1 == null)
					return 0;
				else if (arg0 == null)
					return -1;
				else if (arg1 == null)
					return 1;
				else if (arg0 instanceof BNode)
					if (arg1 instanceof BNode)
						return 0;
					else
						return -1;
				else if (arg1 instanceof BNode)
					return 1;
				else
					return arg0.toString().compareTo(arg1.toString());
			}
		});

		Map<BNode, BNode> mappings = new HashMap<BNode, BNode>();
		int counter = 0;
		ArrayList<Statement> ret = new ArrayList<Statement>();
		for (Statement s : l) {
			Resource nSubj = null;
			if (s.getSubject() instanceof BNode) {
				BNode n = (BNode) s.getSubject();
				BNode m = mappings.get(n);
				if (m == null)
					mappings.put(n, m = new BNodeImpl("" + counter++));
				nSubj = m;
			} else
				nSubj = s.getSubject();

			Value nObj = null;
			if (s.getObject() instanceof BNode) {
				BNode n = (BNode) s.getObject();
				BNode m = mappings.get(n);
				if (m == null)
					mappings.put(n, m = new BNodeImpl("" + counter++));
				nObj = m;
			} else
				nObj = s.getObject();
			ret.add(new StatementImpl(nSubj, s.getPredicate(), nObj));
		}

		if (logger.isDebugEnabled()) {
			System.out.println("Input canonicalized to: " + ret);
		}

		return ret;
	}

	/**
	 * For testing
	 */
	/*
	 * public static void main(String[] args) { List<Statement> sts = new
	 * ArrayList<Statement>(); sts.add(new StatementImpl(new
	 * BNodeImpl("http://some5"), new URIImpl( "http://boo"), new
	 * BNodeImpl("haha2"))); sts.add(new StatementImpl(new BNodeImpl("haha2"),
	 * new URIImpl( "http://boo"), new LiteralImpl("haha"))); sts.add(new
	 * StatementImpl(new URIImpl("http://some3"), new URIImpl( "http://boo"),
	 * new LiteralImpl("haha"))); sts.add(new StatementImpl(new
	 * URIImpl("http://some4"), new URIImpl( "http://boo"), new
	 * LiteralImpl("haha")));
	 * 
	 * List<Statement> sts2 = new ArrayList<Statement>(); sts2.add(new
	 * StatementImpl(new URIImpl("http://some3"), new URIImpl( "http://boo"),
	 * new LiteralImpl("haha"))); sts2.add(new StatementImpl(new
	 * URIImpl("http://some4"), new URIImpl( "http://boo"), new
	 * LiteralImpl("haha"))); sts2.add(new StatementImpl(new
	 * BNodeImpl("http://some1"), new URIImpl( "http://boo"), new
	 * BNodeImpl("haha"))); sts2.add(new StatementImpl(new
	 * BNodeImpl("http://some2"), new URIImpl( "http://boo"), new
	 * LiteralImpl("haha")));
	 * 
	 * System.out.println(canonicalize(sts));
	 * System.out.println(canonicalize(sts2));
	 * System.out.println(canonicalize(sts).equals(canonicalize(sts2))); }
	 */
}
