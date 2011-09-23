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
import java.util.Vector;

import eu.larkc.core.pluginregistry.query.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.pluginregistry.query.exceptions.SparqlQueryTransformException;

/**
 * @author Blaz Fortuna
 */
class SelectQuery {

  private Vector<Value> variableV;
  private Vector<GraphPattern> graphPatternV;
  private HashSet<String> variableIndexH;
  private Value namedGraph;

  public SelectQuery(Lexer lx, Prefix prefix) throws MalformedSparqlQueryException {
    variableV = new Vector<Value>();
    graphPatternV = new Vector<GraphPattern>();
    variableIndexH = new HashSet<String>();
    namedGraph = null;
    // get list of variables
    lx.next();
    while (lx.getTokenType() == Lexer.TokenType.QUESTION_MARK) {
      // read the variable
      variableV.add(new Value(lx, prefix));
      variableIndexH.add(variableV.lastElement().getVal());
    }
    // FROM and WHERE clause
    while (true) {
      if (lx.getTokenType() != Lexer.TokenType.STRING) {
        lx.unexpectedToken(lx.getTokenType());
      }
      // we have a microtheory specification
      if (lx.getStrVal().equalsIgnoreCase("FROM")) {
        lx.next();
        // parse URI
        namedGraph = new Value(lx, prefix);
        if (!namedGraph.isUri()) { 
          lx.unexpectedToken(lx.getTokenType()); 
        }
      } else if (lx.getStrVal().equalsIgnoreCase("WHERE")) {
        lx.next();
        break;
      } else {
        lx.unexpectedString(lx.getStrVal());
      }
    }
    // beginning of the list of constraints
    if (lx.getTokenType() != Lexer.TokenType.LEFT_CURLY_BRACKET) {
      lx.unexpectedToken(lx.getTokenType());
    }
    lx.next();
    // iterate over the list
    while (true) {
      if (lx.getTokenType() == Lexer.TokenType.RIGHT_CURLY_BRACKET) {
        lx.next();
        break; // end of constraints
      } else if (lx.getTokenType() == Lexer.TokenType.DOT) {
        lx.next();
        continue; // we go to the next constraint
      } else {
        graphPatternV.add(new GraphPattern(lx, prefix));
      }
    }
  }

  public String toCycQuery() throws SparqlQueryTransformException {
    // get list of all atomic elements in graph patterns that are not variable
    HashSet<Value> unboundedH = new HashSet<Value>();
    for (GraphPattern graphPattern : graphPatternV) {
      graphPattern.getUnbounded(variableIndexH, unboundedH);
    }
    // construct the query from the graph patterns
    String query = null;
    for (GraphPattern graphPattern : graphPatternV) {
      if (query == null) {
        // first element in the list
        query = graphPattern.toCycQuery();
      } else {
        String newElt = graphPattern.toCycQuery();
        query = "(#$and " + query + " " + newElt + ")";
      }
    }
    // add unbounded elements
    for (Value atom : unboundedH) {
      query = "(#$thereExists " + atom.toCycQuery() + " " + query + ")";
    }
    // finish
    return query;
  }
  
  public String toCycMicroTheory() throws SparqlQueryTransformException {
    if (namedGraph != null) {
      return namedGraph.toCycQuery();
    }
    return null;
  }
}
