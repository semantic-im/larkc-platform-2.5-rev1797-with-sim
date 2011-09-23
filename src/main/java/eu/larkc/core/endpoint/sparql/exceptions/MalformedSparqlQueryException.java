package eu.larkc.core.endpoint.sparql.exceptions;

public class MalformedSparqlQueryException extends SparqlException {

  private static final long serialVersionUID = 1L;

  public MalformedSparqlQueryException(String message) {
    super(message);
  }
}
