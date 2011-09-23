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
 * Collects exceptions metrics.
 * 
 * Current implementation counts the number of executions of any handled Throwable. 
 * The counting is done per Context.
 *  
 * @author mcq
 *
 */
public aspect ExceptionsCounter extends AbstractContextCounter {
	public pointcut pointcutToIncrementCounter(): within(eu.larkc..*) && handler(java.lang.Throwable+);
	
	@Override
	protected String getCounterName(JoinPoint jp, Context c) {
		return c.getTag() + "NumberOfExceptions";
	}
}
