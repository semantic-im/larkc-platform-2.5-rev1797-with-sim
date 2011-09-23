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

import sim.data.Context;

import sim.instrumentation.data.ContextManager;


/**
 * Propagates Context between Workflow and Plugins.
 *  
 * @author mcq
 *
 */
public aspect WorkflowExecutionContextPropagator {
	private Context eu.larkc.core.pluginManager.ControlMessage.context;
	
	pointcut sendContext(eu.larkc.core.pluginManager.ControlMessage message): within(eu.larkc.core.pluginManager.local.LocalPluginManager || eu.larkc.core.pluginManager.remote.Servlet.Tomcat.JeePluginManager) && execution(public void accept(eu.larkc.core.pluginManager.ControlMessage)) && args(message);
	pointcut receiveContext(): within(eu.larkc.core.pluginManager.local.LocalPluginManager || eu.larkc.core.pluginManager.remote.Servlet.Tomcat.JeePluginManager) && execution(public eu.larkc.core.pluginManager.ControlMessage getNextControlMessage());
	
	before(eu.larkc.core.pluginManager.ControlMessage message): sendContext(message) {
		message.context = ContextManager.getCurrentContext();
	}
	
	after() returning(eu.larkc.core.pluginManager.ControlMessage message): receiveContext() {
		ContextManager.setCurrentContext(message.context);
		message.context = null;
	}
	
	private Context eu.larkc.core.data.SetOfStatements.context;
	
	pointcut sendContextRemote(eu.larkc.core.data.SetOfStatements sos): within(eu.larkc.core.pluginManager.remote.Servlet.Tomcat.JeePluginManager) && execution(protected eu.larkc.core.data.SetOfStatements callPluginInstance(eu.larkc.core.data.SetOfStatements, java.lang.String)) && args(sos, String);
	pointcut receiveContextRemote(eu.larkc.core.data.SetOfStatements sos): within(eu.larkc.core.pluginManager.remote.Servlet.Tomcat.servlet.PluginServlet) && call(public final eu.larkc.core.data.SetOfStatements eu.larkc.plugin.Plugin.invoke(eu.larkc.core.data.SetOfStatements)) && args(sos);
	
	before(eu.larkc.core.data.SetOfStatements sos): sendContextRemote(sos) {
		sos.context = ContextManager.getCurrentContext();
	}
	
	before(eu.larkc.core.data.SetOfStatements sos): receiveContextRemote(sos) {
		ContextManager.setCurrentContext(sos.context);
		sos.context = null;
	}
	
}
