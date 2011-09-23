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

/**
 * @author Blaz Fortuna
 */
class Lexer {
  // constants defining the states of lexer

  public enum TokenType {

    STRING, // string such as 'SELECT' or 'foaf:'
    LEFT_CURLY_BRACKET, // {
    RIGHT_CURLY_BRACKET, // }
    QUESTION_MARK, // ?
    LESS_THAN, // <
    GREATER_THAN, // >
    DOT, // .
    QUOTE, // "
    EOF, // eof
    UNDEF   // basicaly an error...
  }
  private String queryStr; // query string we are parsings
  private int queryPos; // next character in the query stream

  public int getQueryPos() {
    return queryPos;
  }
  // place-holder for the value in the case when the result is a string
  private String stringVal;
  private TokenType tokenType;

  public TokenType getTokenType() {
    return tokenType;
  }

  public String getStrVal() {
    return stringVal;
  }

  // constructor, resets stuff
  public Lexer(String _queryStr) throws MalformedSparqlQueryException {
    queryStr = _queryStr + " ";
    queryPos = 0;
    next();
  }

  // did we reach the end of query?
  private boolean isEof() {
    return queryPos == queryStr.length();
  }

  // is the current character white-space?
  private boolean isWs() {
    char ch = queryStr.charAt(queryPos);
    switch (ch) {
      case ' ':
      case '\n':
      case '\t':
        return true;
    }
    return false;
  }

  private boolean isNewToken() {
    char ch = queryStr.charAt(queryPos);
    switch (ch) {
      case '}':
      case '{':
      case '?':
      case '<':
      case '>':
      case '"':
        return true;
    }
    return false;
  }

  private char getCh() {
    char ch = queryStr.charAt(queryPos);
    queryPos++;
    return ch;
  }

  private boolean isAlpha() {
    char ch = queryStr.charAt(queryPos);
    return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
  }

  private String readTillStrEnd() {
    StringBuffer chA = new StringBuffer();
    while (!isEof() && !isWs() && !isNewToken()) {
      chA.append(getCh());
    }
    return chA.toString();
  }

  // returns the next token
  public void next() throws MalformedSparqlQueryException {
    tokenType = TokenType.UNDEF;
    // skip white space
    while (!isEof() && isWs()) {
      queryPos++;
    }
    // if we finished, return eof
    if (isEof()) {
      tokenType = TokenType.EOF;
      return;
    }
    // read the string, if it is string
    if (isAlpha()) {
      stringVal = readTillStrEnd();
      tokenType = TokenType.STRING;
      return;
    }
    // it's a single character token
    char ch = getCh();
    if (ch == '.') {
      tokenType = TokenType.DOT;
      return;
    } else if (ch == '{') {
      tokenType = TokenType.LEFT_CURLY_BRACKET;
      return;
    } else if (ch == '}') {
      tokenType = TokenType.RIGHT_CURLY_BRACKET;
      return;
    } else if (ch == '?') {
      tokenType = TokenType.QUESTION_MARK;
      return;
    } else if (ch == '<') {
      tokenType = TokenType.LESS_THAN;
      return;
    } else if (ch == '>') {
      tokenType = TokenType.GREATER_THAN;
      return;
    } else if (ch == '"') {
      tokenType = TokenType.QUOTE;
      return;
    }
    // didn't find anything useful
    throw new MalformedSparqlQueryException("Wrong character '" + ch + "' at position " + queryPos);
  }

  public void unexpectedToken(TokenType tokenType) throws MalformedSparqlQueryException {
    throw new MalformedSparqlQueryException("Unexpected token '" + tokenType.toString() + "' ending at position " + getQueryPos());
  }

  public void unexpectedString(String string) throws MalformedSparqlQueryException {
    throw new MalformedSparqlQueryException("Unexpected string '" + string + "' ending at position " + getQueryPos());
  }
}
