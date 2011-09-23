package eu.larkc.core.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;

import eu.larkc.core.util.RDFConstants;

/**
 * Convenience method to pass aroung arguments as attribute/value pairs. TODO something better from a KR perspective
 * @author spyros
 *
 */
public class AttributeValueMap extends HashMap<String, String>{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Get an RDF representation of the attribute/value map. To the the attribute/value map from the RDF, use the DataFactory.
	 * @return an RDF representation of this attribute/value map
	 */
	public SetOfStatements toRDF() {
		Set<Statement> statements = new HashSet<Statement>();

		for (Map.Entry<String,String> e: this.entrySet()) {
			statements.add(new StatementImpl(RDFConstants.LARKC_ATTVALUESUBJECT,
					RDFConstants.LARKC_ATTVALUE, new LiteralImpl(e.getKey() + "->" + e.getValue()))); // TODO check again for "->"
		}
		return new SetOfStatementsImpl(statements);
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(String key, String value) {
		if (key.contains("->"))
			throw new IllegalArgumentException("Keys cannot contain the string \"->\"");
		return super.put(key, value);
	}
	
	
}
