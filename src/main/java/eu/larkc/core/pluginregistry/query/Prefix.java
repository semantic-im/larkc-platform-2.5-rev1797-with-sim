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

import java.util.HashMap;

import eu.larkc.core.pluginregistry.query.exceptions.MalformedSparqlQueryException;

/**
 * @author Blaz Fortuna
 */
public class Prefix {
  private HashMap<String, String> prefixH;
  
  // empty prefix class
  public Prefix() {
  }
  
  // parsed prefix class
  public Prefix(Lexer lx, Prefix prefix) throws MalformedSparqlQueryException {
    prefixH = new HashMap<String, String>();
    // iterate over all the prefixes
    lx.next();
    while (true) {
      // parse prefix
      String prefixStr = "";
      if (lx.getTokenType() == Lexer.TokenType.STRING) {
        prefixStr = lx.getStrVal();        
      } else {
        lx.unexpectedToken(lx.getTokenType());        
      }
      lx.next();
      // parse URI
      Value value = new Value(lx, prefix);
      if (!value.isUri()) { 
        lx.unexpectedToken(lx.getTokenType()); 
      }
      // add to the map
      prefixH.put(prefixStr, value.getVal());
      // check what is next
      if (lx.getTokenType() != Lexer.TokenType.STRING) { break; }
      if (!lx.getStrVal().equalsIgnoreCase("PREFIX")) { break; }
    }
  }

  public String resolve(String strVal) {
    if (prefixH == null) { return null; }
    for (String prefixStr : prefixH.keySet()) {
      if (strVal.startsWith(prefixStr)) {
        String uriStr = prefixH.get(prefixStr);
        return strVal.replaceFirst(prefixStr, uriStr);
      }
    }
    return null;
  }  
}
