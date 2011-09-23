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

import eu.larkc.core.executor.Executor;

import sim.data.Context;
import sim.instrumentation.aop.aspectj.AbstractContextWriter;
import sim.instrumentation.data.ContextManager;

/**
 * @author mcq
 *
 */
privileged public aspect WorkflowExecutionContextWriter extends AbstractContextWriter {

	public pointcut placeToTriggerTheContextWrite(): within(eu.larkc.core.executor.Executor) && (execution(* execute(*, *)) || execution(* getNextResults(*)));

	@Override
	protected void beforeInvoke(JoinPoint jp) {
		Context c = ContextManager.getCurrentContext();
		if (c == null)
			return;
		Object thiz = jp.getThis();
		Executor e;
		if (!(thiz instanceof Executor))
			return;
		e = (Executor)thiz;
		// get workflowid
		c.put("WorkflowId", e.uuid.toString());
		// get number of plugins
		c.put("WorkflowNumberOfPlugins", e.pluginInstances.size());
	}

	@Override
	protected void afterReturningInvoke(JoinPoint jp, Object result) {}

	@Override
	protected void afterInvoke(JoinPoint jp) {}

}
