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
package sim.instrumentation.aop.aspectj.larkc;

import org.aspectj.lang.JoinPoint;

import sim.data.Context;
import sim.instrumentation.aop.aspectj.AbstractContextCounter;

/**
 * Counts the number of handled MalformedSparqlQueryException exceptions. 
 * The counting is done per Context.
 *  
 * @author mcq
 *
 */
public aspect MalformedSparqlQueryExceptionCounter extends AbstractContextCounter {
	public pointcut pointcutToIncrementCounter(): within(eu.larkc..*) && handler(
			eu.larkc.core.endpoint.sparql.exceptions.MalformedSparqlQueryException
			|| org.openrdf.query.MalformedQueryException);
	
	@Override
	protected String getCounterName(JoinPoint jp, Context c) {
		return c.getTag() + "NumberOfMalformedSparqlQueryExceptions";
	}
}
