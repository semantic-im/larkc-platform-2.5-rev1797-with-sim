/***
 *   Copyright (c) 1995-2010 Cycorp R.E.R. d.o.o
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package eu.larkc.core.pluginregistry.query;

import java.util.HashSet;

import eu.larkc.core.pluginregistry.query.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.pluginregistry.query.exceptions.SparqlQueryTransformException;

/**
 * @author Blaz Fortuna
 */
class GraphPattern {

  private Value[] triplet;

  public GraphPattern(Lexer lx, Prefix prefix) throws MalformedSparqlQueryException {
    // parse in the atoms
    triplet = new Value[3];
    triplet[0] = new Value(lx, prefix);
    triplet[1] = new Value(lx, prefix);
    triplet[2] = new Value(lx, prefix);
  }

  // gets a list of all bounded variables, and adds all the ones
  // from this patter not in the list to the list of unbounded ones
  public void getUnbounded(HashSet<String> boundedH, HashSet<Value> unboundedH) {
    for (Value atom : triplet) {
      if (atom.isVariable() && !boundedH.contains(atom.getVal())) {
        if (!unboundedH.contains(atom)) {
          unboundedH.add(atom);
        }
      }
    }
  }

  public String toCycQuery() throws SparqlQueryTransformException {
    String query = new String();
    query += "(" + triplet[1].toCycQuery() + " ";
    query += triplet[0].toCycQuery() + " ";
    query += triplet[2].toCycQuery() + ")";
    return query;
  }
}
