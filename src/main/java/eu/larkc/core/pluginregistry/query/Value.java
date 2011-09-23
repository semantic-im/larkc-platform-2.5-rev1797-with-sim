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
public class Value {

  public enum Type {
    VARIABLE, URI, LITERAL, BNODE
  }
  
  private Type atomType;
  private String stringVal;
  private String dataTypeVal;
  private String langVal;

  public boolean isVariable() {
    return atomType == Type.VARIABLE;
  }

  public boolean isUri() {
    return atomType == Type.URI;
  }

  public boolean isLiteral() {
    return atomType == Type.LITERAL;
  }

  public boolean isBNode() {
    return atomType == Type.BNODE;
  }

  public String getVal() {
    return stringVal;
  }

  public String getDataType() {
    return dataTypeVal;
  }

  public String getLanguage() {
    return langVal;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Value) {
      Value atom = (Value) obj;
      return (atom.atomType == this.atomType)
          && (atom.stringVal.equals(this.stringVal));
    }
    return false;
  }

  private Value(Type type, String val) {
    atomType = type;
    stringVal = val;
  }

  private Value(String val, String dataType, String lang) {
    atomType = Type.LITERAL;
    stringVal = val;
    dataTypeVal = dataType;
    langVal = lang;
  }

  public static Value getUri(String uri) {
    return new Value(Type.URI, uri);
  }

  public static Value getLiteral(String val) {
    return new Value(val, null, null);
  }

  public static Value getLiteral(String val, String dataType) {
    return new Value(val, dataType, null);
  }

  public static Value getLiteral(String val, String dataType, String lang) {
    return new Value(val, dataType, lang);
  }

  public static Value getVariable(String variableName) {
    return new Value(Type.VARIABLE, variableName);
  }

  public static Value getBNode(String bnodeId) {
    return new Value(Type.BNODE, bnodeId);
  }

  public Value(Lexer lx, Prefix prefix) throws MalformedSparqlQueryException {
    if (lx.getTokenType() == Lexer.TokenType.QUESTION_MARK) {
      // variable
      atomType = Type.VARIABLE;
      lx.next();
      if (lx.getTokenType() == Lexer.TokenType.STRING) {
        stringVal = lx.getStrVal();
      } else {
        lx.unexpectedToken(lx.getTokenType());
      }
    } else if (lx.getTokenType() == Lexer.TokenType.LESS_THAN) {
      // fully specified URI
      atomType = Type.URI;
      lx.next();
      if (lx.getTokenType() == Lexer.TokenType.STRING) {
        stringVal = lx.getStrVal();
      } else {
        lx.unexpectedToken(lx.getTokenType());
      }
      lx.next();
      if (lx.getTokenType() != Lexer.TokenType.GREATER_THAN) {
        lx.unexpectedToken(lx.getTokenType());
      }
    } else if (lx.getTokenType() == Lexer.TokenType.QUOTE){
      throw new MalformedSparqlQueryException("Literal strings not yet supported!");
    } else if (lx.getTokenType() == Lexer.TokenType.STRING) {
      // URI with prefix
      atomType = Type.URI;      
      stringVal = prefix.resolve(lx.getStrVal());
      if (stringVal == null) {
        lx.unexpectedString(lx.getStrVal());
      }
    } else {
      lx.unexpectedToken(lx.getTokenType());
    }      
    lx.next();
  }  

  public String toCycQuery() throws SparqlQueryTransformException {
    if (isVariable()) {
      return "?" + stringVal;
    } else if (isUri()) {
      //HACK extract cyc concept out of URI
      int startIndex = stringVal.lastIndexOf('/');
      if (startIndex < 0 || startIndex >= stringVal.length()) {
        throw new SparqlQueryTransformException("Bad URI to a Cyc concept: " + stringVal);
      }
      return "#$" + stringVal.substring(startIndex + 1);
    }
    throw new SparqlQueryTransformException("Unknown atom type '" + atomType.toString() + "' of atom '" + stringVal + "' !");
  }
}
