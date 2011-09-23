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

import eu.larkc.core.pluginregistry.query.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.pluginregistry.query.exceptions.SparqlQueryTransformException;

/**
 * @author Blaz Fortuna
 */
public class SparqlQuery {

  public enum Type {
    SELECT
  }
  private Type queryType;
  private SelectQuery select;
  
  // gets a SPARQL query as string and parses it into an object-structure-thingy
  public SparqlQuery(String queryStr) throws MalformedSparqlQueryException {
    Lexer lx = new Lexer(queryStr);
    Prefix prefix = new Prefix();
    while (true) {
      if (lx.getTokenType() == Lexer.TokenType.STRING) {
        // we have beginning of SELECT statement
        if (lx.getStrVal().equalsIgnoreCase("SELECT")) {
          queryType = Type.SELECT;
          select = new SelectQuery(lx, prefix);
          // nothing to do anymore
          break;
        } else if (lx.getStrVal().equalsIgnoreCase("PREFIX")) {
          // list of prefixes
          prefix = new Prefix(lx, prefix);
        } else {
          lx.unexpectedString(lx.getStrVal());
        }
      } else {
        lx.unexpectedToken(lx.getTokenType());
      }
    }
  }

  public boolean isSelect() {
    return (queryType == Type.SELECT);
  }

  public boolean isAsk() {
    return false;
  }

  public String toCycQuery() throws SparqlQueryTransformException {
    if (queryType == Type.SELECT) {
      return select.toCycQuery();
    }
    return null;
  }
  
  public String toCycMicroTheory() throws SparqlQueryTransformException {
    String microTheory = null;
    if (queryType == Type.SELECT) {
      microTheory = select.toCycMicroTheory();
    }
    if (microTheory != null) {
      return microTheory;
    } else { 
      return "#$InferencePSC";
    }
  }  
}
