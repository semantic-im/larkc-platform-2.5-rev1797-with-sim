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
package eu.larkc.core.endpoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import eu.larkc.core.Larkc;
import eu.larkc.core.executor.Executor;

/**
 * This class is responsible for creating endpoints of specific types.
 * 
 * @author norlan
 * 
 */
public class EndpointFactory {

	/**
	 * This method creates an endpoint of the specific type.
	 * 
	 * @param type
	 *            type of the endpoint
	 * @param executor
	 *            the executor responsible for that endpoint
	 * @return the newly instantiated endpoint
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static Endpoint constructEndpoint(String type, Executor executor)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {

		String className = Larkc.getClassName(type);

		Class<Endpoint> endpointClass = (Class<Endpoint>) Class
				.forName(className);

		Constructor<Endpoint> constructor = endpointClass
				.getConstructor(Executor.class);
		return constructor.newInstance(executor);
	}

}
