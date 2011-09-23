/*
 * This file is part of the LarKC platform http://www.larkc.eu/
 *
 *  Copyright 2010 LarKC project consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sim.instrumentation.aop.aspectj.larkc;

import org.aspectj.lang.JoinPoint;

import eu.larkc.core.data.RdfGraphInMemory;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.plugin.Plugin;

import sim.data.Context;
import sim.instrumentation.aop.aspectj.AbstractContextWriter;
import sim.instrumentation.data.ContextManager;

/**
 * @author mcq
 *
 */
privileged public aspect PluginExecutionContextWriter1 extends AbstractContextWriter {

	public pointcut placeToTriggerTheContextWrite(): within(eu.larkc.plugin.Plugin) && execution(public eu.larkc.core.data.SetOfStatements invoke(eu.larkc.core.data.SetOfStatements));

	@Override
	protected void beforeInvoke(JoinPoint jp) {
		Context c = ContextManager.getCurrentContext();
		if (c == null)
			return;
		// get plugin namme
		Object thiz = jp.getThis();
		Plugin p;
		if (!(thiz instanceof Plugin))
			return;
		p = (Plugin)thiz;
		c.put("PluginName", p.pluginName.toString());
		// get PluginInputSizeInTriples
		Object[] args = jp.getArgs();
		if (args.length != 1)
			return;
		c.put("PluginInputSizeInTriples", getNumberOfTriples(args[0]));
	}

	@Override
	protected void afterReturningInvoke(JoinPoint jp, Object result) {
		// get PluginOutputSizeInTriples
		Context c = ContextManager.getCurrentContext();
		if (c == null)
			return;
		c.put("PluginOutputSizeInTriples", getNumberOfTriples(result));
	}

	@Override
	protected void afterInvoke(JoinPoint jp) {}
	
	private int getNumberOfTriples(Object o) {
		int result = -1;
		if (o instanceof SetOfStatementsImpl) {
			SetOfStatementsImpl input = (SetOfStatementsImpl)o;
			result = input.data.size();
		} else if (o instanceof RdfGraphInMemory) {
			RdfGraphInMemory input = (RdfGraphInMemory)o;
			if (input.data instanceof SetOfStatementsImpl) {
				SetOfStatementsImpl data = (SetOfStatementsImpl)input.data;
				result = data.data.size();
			}
		}
		return result;
	}
	
}
