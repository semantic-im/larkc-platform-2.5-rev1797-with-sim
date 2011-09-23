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
 * Collects data layer inserts metrics.
 * 
 * Current implementation counts all executions of addStatement, from any 
 * RdfStoreConnection implementation, as DataLayerInserts. These values are 
 * stored into current Context.
 *  
 * @author mcq
 *
 */
public aspect DataLayerInsertsCounter extends AbstractContextCounter {
	public pointcut pointcutToIncrementCounter(): within(eu.larkc.core.data.RdfStoreConnection+) && execution(public * addStatement(..));
	
	@Override
	protected String getCounterName(JoinPoint jp, Context c) {
		return c.getTag() + "DataLayerInserts";
	}
}
